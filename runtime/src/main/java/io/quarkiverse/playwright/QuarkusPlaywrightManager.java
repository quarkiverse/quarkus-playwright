package io.quarkiverse.playwright;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.ListeningAddress;
import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;
import io.quarkus.value.registry.ValueRegistry;

/**
 * Manages the lifecycle of a Playwright instance in Quarkus tests.
 * <p>
 * This class implements {@link QuarkusTestResourceConfigurableLifecycleManager}
 * and is responsible
 * for initializing and configuring Playwright resources, such as the
 * {@link Browser},
 * {@link BrowserContext}, and {@link Playwright} instance, according to the
 * settings specified
 * in the {@link WithPlaywright} annotation.
 * </p>
 *
 * <p>
 * This manager supports injecting Playwright resources into test classes marked
 * with the
 * {@code @InjectPlaywright} annotation, allowing easy access to
 * {@link BrowserContext},
 * {@link Browser}, or {@link Playwright} instances.
 * </p>
 *
 * @see WithPlaywright
 * @see InjectPlaywright
 * @see QuarkusTestResourceConfigurableLifecycleManager
 * @since 1.0
 */
public class QuarkusPlaywrightManager
        implements QuarkusTestResourceConfigurableLifecycleManager<WithPlaywright>, DevServicesContext.ContextAware {

    public static class NoOpAdapter implements PlaywrightAdapter {
        // No-op implementation, can be used as a default adapter
    }

    /**
     * Hostname that resolves to the host machine from inside a Testcontainers container attached to the shared
     * network (see {@code quarkus.playwright.devservices.shared-network}).
     */
    static final String TESTCONTAINERS_HOST = "host.testcontainers.internal";

    private static final String SHARED_NETWORK_CONFIG = "quarkus.playwright.devservices.shared-network";

    /**
     * Holds the configuration options from the {@link WithPlaywright} annotation.
     */
    private WithPlaywright options;

    /**
     * Provides access to properties produced by Dev Services, such as the
     * Playwright server endpoint started by the Playwright Dev Service.
     */
    private DevServicesContext devServicesContext;

    /**
     * The global Playwright instance for managing browser creation and operations.
     */
    private Playwright playwright;

    /**
     * The context in which the browser operates, encapsulating tabs, storage, etc.
     */
    private BrowserContext playwrightContext;

    /**
     * The specific browser instance (Chromium, Firefox, WebKit) launched by
     * Playwright.
     */
    private Browser playwrightBrowser;

    /**
     * Launch settings resolved once from {@link #options} and {@code quarkus.playwright.*} config.
     */
    private LaunchSettings launch;

    /**
     * The adapter used to customise Playwright options, kept so the browser context can be created lazily.
     */
    private PlaywrightAdapter adapter;

    /**
     * Whether the browser runs in a remote Playwright server (Dev Services or {@code quarkus.playwright.endpoint}).
     */
    private boolean remoteBrowser;

    /**
     * Initializes the Playwright manager with configuration from
     * {@link WithPlaywright}.
     *
     * @param withPlaywright the Playwright configuration options
     */
    @Override
    public void init(WithPlaywright withPlaywright) {
        this.options = withPlaywright;
        this.launch = LaunchSettings.resolve(withPlaywright, ConfigProvider.getConfig());
    }

    /**
     * Not used in this implementation. Throws an exception to ensure only
     * {@code @WithPlaywright}
     * annotation is used for initialization.
     *
     * @param initArgs ignored initialization arguments
     * @throws IllegalStateException always, as this method is unsupported
     */
    @Override
    public void init(Map<String, String> initArgs) {
        throw new IllegalStateException("Use @WithPlaywright() annotation instead");
    }

    /**
     * Starts the Playwright environment and configures the browser and context
     * based on
     * {@link WithPlaywright} options.
     *
     * @return an empty map as no additional environment variables are required
     */
    @Override
    public Map<String, String> start() {
        final Map<String, String> env = new HashMap<>(System.getenv());

        // Enable Playwright verbose logging if requested
        if (!env.containsKey("DEBUG") && this.options.verbose()) {
            env.put("DEBUG", "pw:api");
        }
        // Enable Playwright debug mode if specified
        if (!env.containsKey("PWDEBUG") && this.options.debug()) {
            env.put("PWDEBUG", "1");
        }

        try {
            this.adapter = this.options.playwrightAdapter().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Adapter class cannot be created", e);
        }

        // Create Playwright instance with the specified environment variables
        this.playwright = createPlaywright(
                adapter.adaptCreateOptions(
                        new Playwright.CreateOptions().setEnv(env)));

        // register testId attribute default to "data-testid"
        this.playwright.selectors().setTestIdAttribute(this.options.testId());

        // register any selectors
        for (PlaywrightSelector selector : this.options.selectors()) {
            this.playwright.selectors().register(selector.name(), selector.script());
        }

        this.playwrightBrowser = startBrowser(adapter, env);

        // The browser context is created lazily in inject(), once the application under test is running and its
        // HTTP URL (which may use a random port) is known, so it can be used as the context's base URL.
        return Collections.emptyMap();
    }

    /**
     * Creates the browser context, using the URL of the application under test as its base URL so tests can
     * navigate with relative paths such as {@code page.navigate("/")}.
     */
    private BrowserContext createBrowserContext(ValueRegistry valueRegistry) {
        final Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

        final String baseUrl = resolveBaseUrl(valueRegistry);
        if (StringUtils.isNotBlank(baseUrl)) {
            contextOptions.setBaseURL(baseUrl);
        }

        // Configure the context, setting the video directory if specified
        if (StringUtils.isNotBlank(this.options.recordVideoDir())) {
            contextOptions.setRecordVideoDir(Paths.get(this.options.recordVideoDir()));
        }

        applyBrowserContextConfig(contextOptions, this.options.browserContext());

        final BrowserContext context = playwrightBrowser.newContext(adapter.adaptNewContextOptions(contextOptions));
        applyConfig(context);
        return context;
    }

    /**
     * Resolves the base URL of the application under test, or {@code null} if it is not available.
     */
    protected String resolveBaseUrl(ValueRegistry valueRegistry) {
        // registered by the HTTP server of the application under test, or by the launcher of integration tests
        final URI localBaseUri = valueRegistry.getOrDefault(ListeningAddress.LOCAL_BASE_URI, null);
        if (localBaseUri == null) {
            // The application under test has no HTTP URL, so there is no base URL to apply
            return null;
        }
        final String testUrl = localBaseUri.toString();
        final boolean sharedNetwork = this.remoteBrowser && ConfigProvider.getConfig()
                .getOptionalValue(SHARED_NETWORK_CONFIG, Boolean.class).orElse(false);
        return baseUrl(testUrl, sharedNetwork);
    }

    /**
     * Computes the browser context base URL from the application's test URL. When the browser runs in a container
     * on the Testcontainers shared network, the host machine is only reachable as {@value #TESTCONTAINERS_HOST}.
     */
    static String baseUrl(String testUrl, boolean sharedNetwork) {
        if (StringUtils.isBlank(testUrl) || !sharedNetwork) {
            return testUrl;
        }
        try {
            final URI uri = new URI(testUrl);
            return new URI(uri.getScheme(), uri.getUserInfo(), TESTCONTAINERS_HOST, uri.getPort(), uri.getPath(),
                    uri.getQuery(), uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            return testUrl;
        }
    }

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        this.devServicesContext = context;
    }

    protected String resolveEndpoint() {
        if (this.devServicesContext != null) {
            return this.devServicesContext.devServicesProperties().getOrDefault("quarkus.playwright.endpoint", "");
        }

        return "";
    }

    protected Playwright createPlaywright(Playwright.CreateOptions createOptions) {
        return Playwright.create(createOptions);
    }

    private Browser startBrowser(PlaywrightAdapter adapter, Map<String, String> env) {
        final BrowserType browserType = browser(playwright, this.launch.browser());
        final String endpoint = resolveEndpoint();

        if (StringUtils.isNotBlank(endpoint)) {
            this.remoteBrowser = true;
            final BrowserType.ConnectOptions connectOptions = adapter.adaptConnectOptions(
                    new BrowserType.ConnectOptions()
                            .setSlowMo(this.launch.slowMo())
                            .setHeaders(remoteLaunchOptionsHeaders()));
            return browserType.connect(endpoint, connectOptions);
        }

        // No endpoint configured, so launch a local browser process.
        final LaunchOptions launchOptions = adapter.adaptLaunchOptions(
                new LaunchOptions()
                        .setChannel(this.launch.channel())
                        .setChromiumSandbox(this.options.chromiumSandbox())
                        .setHeadless(this.launch.headless())
                        .setSlowMo(this.launch.slowMo())
                        .setEnv(env)
                        .setArgs(Arrays.asList(this.options.args())));
        return browserType.launch(launchOptions);
    }

    /**
     * Maps the subset of {@link WithPlaywright} launch options that a remote Playwright
     * server (Dev Service, {@code run-server --unsafe}) actually accepts into the
     * {@code x-playwright-launch-options} connect header, mirroring what the local-launch
     * branch above sets directly on {@link LaunchOptions}. {@code env} has no remote
     * equivalent — the server process is already running with its own environment before a
     * client ever connects — so it's only applied to a locally-launched browser.
     */
    private Map<String, String> remoteLaunchOptionsHeaders() {
        JsonArrayBuilder argsBuilder = Json.createArrayBuilder();
        Arrays.stream(this.options.args()).forEach(argsBuilder::add);

        JsonObjectBuilder launchOptionsBuilder = Json.createObjectBuilder()
                .add("args", argsBuilder.build())
                .add("headless", this.launch.headless())
                .add("chromiumSandbox", this.options.chromiumSandbox());
        if (StringUtils.isNotBlank(this.launch.channel())) {
            launchOptionsBuilder.add("channel", this.launch.channel());
        }
        JsonObject launchOptions = launchOptionsBuilder.build();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-playwright-launch-options", launchOptions.toString());
        return headers;
    }

    private static void applyBrowserContextConfig(NewContextOptions contextOptions, BrowserContextConfig config) {
        contextOptions.setOffline(config.offline());

        if (StringUtils.isNotBlank(config.locale())) {
            contextOptions.setLocale(config.locale());
        }

        if (StringUtils.isNotBlank(config.timeZoneId())) {
            contextOptions.setTimezoneId(config.timeZoneId());
        }

        contextOptions.setViewportSize(config.viewportSize().width(), config.viewportSize().height());

        if (StringUtils.isNotBlank(config.userAgent())) {
            contextOptions.setUserAgent(config.userAgent());
        }
    }

    private void applyConfig(BrowserContext context) {
        var browserContextConfig = this.options.browserContext();

        if (StringUtils.isNotBlank(browserContextConfig.defaultNavigationTimeout())) {
            context.setDefaultNavigationTimeout(
                    Duration.parse(browserContextConfig.defaultNavigationTimeout()).toMillis());
        }

        if (StringUtils.isNotBlank(browserContextConfig.defaultTimeout())) {
            context.setDefaultTimeout(Duration.parse(browserContextConfig.defaultTimeout()).toMillis());
        }
    }

    /**
     * Helper method to retrieve the correct {@link BrowserType} based on the
     * specified browser.
     *
     * @param playwright the Playwright instance
     * @param browser the browser type from the {@link WithPlaywright.Browser}
     *        enum
     * @return the corresponding {@link BrowserType} instance
     */
    private static BrowserType browser(Playwright playwright, WithPlaywright.Browser browser) {
        return switch (browser) {
            case FIREFOX -> playwright.firefox();
            case WEBKIT -> playwright.webkit();
            default -> playwright.chromium();
        };
    }

    /**
     * Closes and cleans up the Playwright resources, ensuring proper shutdown.
     */
    @Override
    public void stop() {
        if (this.playwrightContext != null) {
            this.playwrightContext.close(); // Closes the browser context
            this.playwrightContext = null;
        }
        if (playwright != null) {
            playwright.close(); // Closes the Playwright instance
            playwright = null;
        }
    }

    /**
     * Injects Playwright resources into fields annotated with
     * {@code @InjectPlaywright} in test classes.
     * <p>
     * This method supports injection of {@link BrowserContext}, {@link Browser},
     * and {@link Playwright}
     * into appropriately annotated fields.
     * </p>
     *
     * @param testInjector the test injector responsible for dependency injection
     */
    @Override
    public void inject(TestInjector testInjector) {
        if (this.playwrightContext == null && this.playwrightBrowser != null) {
            this.playwrightContext = createBrowserContext(testInjector.valueRegistry());
        }

        // Injects BrowserContext if @InjectPlaywright is present on a matching
        // field
        testInjector.injectIntoFields(playwrightContext,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, BrowserContext.class));

        // Injects Playwright if @InjectPlaywright is present on a matching field
        testInjector.injectIntoFields(playwright,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, Playwright.class));

        // Injects Browser if @InjectPlaywright is present on a matching field
        testInjector.injectIntoFields(playwrightBrowser,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, Browser.class));
    }
}
