package io.quarkiverse.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Interface for adapting Playwright browser launch and context options. This allows for a more extensive configuration
 * of Playwright's behavior in Quarkus tests, enabling users to customize how browsers are launched and how contexts are
 * created.
 */
public interface PlaywrightAdapter {

    /**
     * Adapts the options for creating the Playwright instance. This method can be overridden to modify the default creation
     * options
     * used when initializing a Playwright instance. For example, you can set the path to the Playwright executable,
     * configure environment variables, or adjust other settings related to Playwright's initialization.
     *
     * @param createOptions The create options to be adapted before creating a Playwright browser instance.
     */
    default Playwright.CreateOptions adaptCreateOptions(Playwright.CreateOptions createOptions) {
        return createOptions;
    };

    /**
     * Adapts the launch options for the Playwright browser. This method can be overridden to modify the default launch options
     * used when starting a browser instance. For example, you can set headless mode, specify a user agent, or configure other
     * launch parameters.
     *
     * @param launchOptions The launch options to be adapted before launching the browser.
     */
    default BrowserType.LaunchOptions adaptLaunchOptions(BrowserType.LaunchOptions launchOptions) {
        return launchOptions;
    };

    /**
     * Adapts the options for creating a new browser context. This method can be overridden to modify the default context
     * options
     * used when creating a new browser context. For example, you can set viewport size, user agent, or configure other context
     * parameters.
     *
     * @param newContextOptions The new context options to be adapted before creating a browser context.
     */
    default Browser.NewContextOptions adaptNewContextOptions(Browser.NewContextOptions newContextOptions) {
        return newContextOptions;
    };

}
