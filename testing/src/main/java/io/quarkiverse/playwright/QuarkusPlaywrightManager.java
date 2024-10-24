package io.quarkiverse.playwright;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;

import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;

public class QuarkusPlaywrightManager implements QuarkusTestResourceConfigurableLifecycleManager<WithPlaywright> {
    private WithPlaywright options;

    private Playwright playwright;

    private BrowserContext playwrightContext;

    private Browser playwrightBrowser;

    @Override
    public void init(WithPlaywright withPlaywright) {
        this.options = withPlaywright;
    }

    @Override
    public void init(Map<String, String> initArgs) {
        throw new IllegalStateException("Use @WithPlaywright() annotation instead");
    }

    @Override
    public Map<String, String> start() {
        final Map<String, String> env = new HashMap<>(System.getenv());
        if (!env.containsKey("DEBUG") && this.options.verbose()) {
            env.put("DEBUG", "pw:api");
        }
        if (!env.containsKey("PWDEBUG") && this.options.debug()) {
            env.put("PWDEBUG", "1");
        }
        this.playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env));
        final LaunchOptions launchOptions = new LaunchOptions()
                .setChannel(this.options.channel())
                .setChromiumSandbox(this.options.chromiumSandbox())
                .setHeadless(this.options.headless())
                .setSlowMo(this.options.slowMo())
                .setEnv(env)
                .setArgs(Arrays.asList(this.options.args()));
        this.playwrightBrowser = browser(playwright, this.options.browser()).launch(
                launchOptions);

        final Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();
        if (StringUtils.isNotBlank(this.options.recordVideoDir())) {
            contextOptions.setRecordVideoDir(Paths.get(this.options.recordVideoDir()));
        }
        this.playwrightContext = playwrightBrowser.newContext(contextOptions);
        return Collections.emptyMap();
    }

    private static BrowserType browser(Playwright playwright, WithPlaywright.Browser browser) {
        return switch (browser) {
            case FIREFOX -> playwright.firefox();
            case WEBKIT -> playwright.webkit();
            default -> playwright.chromium();
        };
    }

    @Override
    public void stop() {
        if (this.playwrightContext != null) {
            this.playwrightContext.close();
            this.playwrightContext = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(playwrightContext,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, BrowserContext.class));
        testInjector.injectIntoFields(playwright,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, Playwright.class));
        testInjector.injectIntoFields(playwrightBrowser,
                new TestInjector.AnnotatedAndMatchesType(InjectPlaywright.class, Browser.class));
    }

}