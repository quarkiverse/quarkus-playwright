package io.quarkiverse.playwright;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        this.playwrightContext = playwrightBrowser.newContext();
        return Collections.emptyMap();
    }

    private static BrowserType browser(Playwright playwright, WithPlaywright.Browser browser) {
        switch (browser) {
            case CHROMIUM:
                return playwright.chromium();
            case FIREFOX:
                return playwright.firefox();
            case WEBKIT:
                return playwright.webkit();
        }
        return playwright.chromium();
    }

    @Override
    public void stop() {
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
