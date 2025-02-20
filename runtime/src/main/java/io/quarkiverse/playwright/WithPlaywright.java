package io.quarkiverse.playwright;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkus.test.common.QuarkusTestResource;

/**
 * Annotation to configure and enable Playwright for Quarkus tests.
 * <p>
 * This annotation sets up a Playwright testing environment with options for
 * browser selection, debugging, and configuration of other Playwright features.
 * It is applied at the class level and managed by {@link QuarkusPlaywrightManager}.
 * </p>
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * @WithPlaywright(browser = Browser.FIREFOX, headless = false, verbose = true)
 * public class PlaywrightTest {
 *     // Test code here
 * }
 * }
 * </pre>
 * </p>
 *
 * @see io.quarkus.test.common.QuarkusTestResource
 * @see QuarkusPlaywrightManager
 * @since 1.0
 */
@QuarkusTestResource(QuarkusPlaywrightManager.class)
@Retention(RetentionPolicy.RUNTIME) // Annotation is retained at runtime for test setup.
@Target(ElementType.TYPE) // Applied only at the class level.
public @interface WithPlaywright {

    /**
     * Specifies the browser to use for Playwright tests.
     * <p>
     * Defaults to {@link Browser#CHROMIUM}.
     * </p>
     */
    Browser browser() default Browser.CHROMIUM;

    /**
     * Enables Playwright verbose logging.
     * <p>
     * Set to {@code true} to enable detailed logging, useful for debugging.
     * </p>
     */
    boolean verbose() default false;

    /**
     * Enables the Playwright Debug Inspector for debugging tests.
     * <p>
     * Use {@code true} to launch the inspector, which pauses tests for interactive debugging.
     * </p>
     */
    boolean debug() default false;

    /**
     * Specifies the distribution channel of the browser to use, such as "chrome" or "msedge".
     * <p>
     * Supported values include "chrome", "chrome-beta", "chrome-dev", "chrome-canary", "msedge",
     * "msedge-beta", "msedge-dev", and "msedge-canary".
     * </p>
     * <p>
     * Refer to the <a href="https://playwright.dev/java/docs/browsers#google-chrome--microsoft-edge">
     * Playwright documentation</a> for additional details on using these channels.
     * </p>
     */
    String channel() default "";

    /**
     * Defines custom attribute name to be used in Page.getByTestId(). "data-testid" is used by default.
     * <p>
     * Defaults to "data-testid".
     * </p>
     */
    String testId() default "data-testid";

    /**
     * Enables sandboxing for Chromium-based browsers.
     * <p>
     * Defaults to {@code false} for compatibility. Set to {@code true} to enable sandboxing if supported.
     * </p>
     */
    boolean chromiumSandbox() default false;

    /**
     * Runs the browser in headless mode, which is suitable for CI environments.
     * <p>
     * Defaults to {@code true} unless the {@code devtools} option is enabled.
     * </p>
     * <p>
     * See more about headless mode in <a href="https://developers.google.com/web/updates/2017/04/headless-chrome">
     * Chromium</a> and <a href="https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode">Firefox</a>.
     * </p>
     */
    boolean headless() default true;

    /**
     * Slows down Playwright operations by the specified number of milliseconds.
     * <p>
     * This is useful for observing browser interactions more clearly during tests.
     * </p>
     */
    double slowMo() default 0;

    /**
     * Specifies the directory to store video recordings of all pages.
     * <p>
     * If not set, video recording is disabled.
     * </p>
     */
    String recordVideoDir() default "";

    /**
     * Specifies command-line arguments to use when launching the browser.
     * <p>
     * Defaults to disabling GPU with {@code "--disable-gpu"}.
     * </p>
     */
    String[] args() default { "--disable-gpu" };

    /**
     * Specifies Playwright selectors to be used for locating elements in tests.
     * <p>
     * Multiple selectors can be defined and optionally named for reference.
     * These selectors will be available for use in test methods annotated with this annotation.
     * </p>
     *
     * @return Array of {@link PlaywrightSelector} annotations defining the selectors
     */
    PlaywrightSelector[] selectors() default {};

    /**
     * Enum representing the supported browsers for Playwright testing.
     */
    enum Browser {
        CHROMIUM, // Google Chrome and other Chromium-based browsers.
        FIREFOX, // Mozilla Firefox browser.
        WEBKIT // WebKit browser, primarily for Safari compatibility.
    }

    /**
     * Configuration for creation of the {@link com.microsoft.playwright.BrowserContext BrowserContext}
     */
    BrowserContextConfig browserContext() default @BrowserContextConfig;
}