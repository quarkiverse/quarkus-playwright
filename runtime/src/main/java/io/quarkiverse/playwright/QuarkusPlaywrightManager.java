package io.quarkiverse.playwright;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;

import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;

/**
 * Manages the lifecycle of a Playwright instance in Quarkus tests.
 * <p>
 * This class implements {@link QuarkusTestResourceConfigurableLifecycleManager} and is responsible
 * for initializing and configuring Playwright resources, such as the {@link Browser},
 * {@link BrowserContext}, and {@link Playwright} instance, according to the settings specified
 * in the {@link WithPlaywright} annotation.
 * </p>
 *
 * <p>
 * This manager supports injecting Playwright resources into test classes marked with the
 * {@code @InjectPlaywright} annotation, allowing easy access to {@link BrowserContext},
 * {@link Browser}, or {@link Playwright} instances.
 * </p>
 *
 * @see WithPlaywright
 * @see InjectPlaywright
 * @see QuarkusTestResourceConfigurableLifecycleManager
 * @since 1.0
 */
public class QuarkusPlaywrightManager implements QuarkusTestResourceConfigurableLifecycleManager<WithPlaywright> {

    /** Holds the configuration options from the {@link WithPlaywright} annotation. */
    private WithPlaywright options;

    /** The global Playwright instance for managing browser creation and operations. */
    private Playwright playwright;

    /** The context in which the browser operates, encapsulating tabs, storage, etc. */
    private BrowserContext playwrightContext;

    /** The specific browser instance (Chromium, Firefox, WebKit) launched by Playwright. */
    private Browser playwrightBrowser;

    /**
     * Initializes the Playwright manager with configuration from {@link WithPlaywright}.
     *
     * @param withPlaywright the Playwright configuration options
     */
    @Override
    public void init(WithPlaywright withPlaywright) {
        this.options = withPlaywright;
    }

    /**
     * Not used in this implementation. Throws an exception to ensure only {@code @WithPlaywright}
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
     * Starts the Playwright environment and configures the browser and context based on
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

        // Create Playwright instance with the specified environment variables
        this.playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env));

        // Configure launch options based on @WithPlaywright attributes
        final LaunchOptions launchOptions = new LaunchOptions()
                .setChannel(this.options.channel())
                .setChromiumSandbox(this.options.chromiumSandbox())
                .setHeadless(this.options.headless())
                .setSlowMo(this.options.slowMo())
                .setEnv(env)
                .setArgs(Arrays.asList(this.options.args()));

        // Launch the browser based on the specified type (CHROMIUM, FIREFOX, or WEBKIT)
        this.playwrightBrowser = browser(playwright, this.options.browser()).launch(launchOptions);

        // Configure the context, setting the video directory if specified
        final Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (StringUtils.isNotBlank(this.options.recordVideoDir())) {
            contextOptions.setRecordVideoDir(Paths.get(this.options.recordVideoDir()));
        }

        applyBrowserContextConfig(contextOptions, this.options.browserContext());

        this.playwrightContext = playwrightBrowser.newContext(contextOptions);

        applyConfig();

        return Collections.emptyMap();
    }

    private static void applyBrowserContextConfig(NewContextOptions contextOptions, BrowserContextConfig config) {
        contextOptions.setOffline(config.offline());

        if (StringUtils.isNotBlank(config.locale())) {
            contextOptions.setLocale(config.locale());
        }

        if (StringUtils.isNotBlank(config.userAgent())) {
            contextOptions.setUserAgent(config.userAgent());
        }
    }

    private void applyConfig() {
        var browserContextConfig = this.options.browserContext();

        if (StringUtils.isNotBlank(browserContextConfig.defaultNavigationTimeout())) {
            this.playwrightContext
                    .setDefaultNavigationTimeout(Duration.parse(browserContextConfig.defaultNavigationTimeout()).toMillis());
        }

        if (StringUtils.isNotBlank(browserContextConfig.defaultTimeout())) {
            this.playwrightContext.setDefaultTimeout(Duration.parse(browserContextConfig.defaultTimeout()).toMillis());
        }
    }

    /**
     * Helper method to retrieve the correct {@link BrowserType} based on the specified browser.
     *
     * @param playwright the Playwright instance
     * @param browser the browser type from the {@link WithPlaywright.Browser} enum
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
     * Injects Playwright resources into fields annotated with {@code @InjectPlaywright} in test classes.
     * <p>
     * This method supports injection of {@link BrowserContext}, {@link Browser}, and {@link Playwright}
     * into appropriately annotated fields.
     * </p>
     *
     * @param testInjector the test injector responsible for dependency injection
     */
    @Override
    public void inject(TestInjector testInjector) {
        // Injects BrowserContextConfig if @InjectPlaywright is present on a matching field
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
