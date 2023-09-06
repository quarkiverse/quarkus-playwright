package io.quarkiverse.playwright;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(QuarkusPlaywrightManager.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithPlaywright {

    /**
     * Browser to use
     */
    Browser browser() default Browser.CHROMIUM;

    /**
     * Enable playwright logs
     */
    boolean verbose() default false;

    /**
     * Enable Playwright Debug Inspector
     */
    boolean debug() default false;

    /**
     * Browser distribution channel. Supported values are "chrome", "chrome-beta", "chrome-dev", "chrome-canary", "msedge",
     * "msedge-beta", "msedge-dev", "msedge-canary". Read more about using <a
     * href="https://playwright.dev/java/docs/browsers#google-chrome--microsoft-edge">Google Chrome and Microsoft Edge</a>.
     */
    String channel() default "";

    /**
     * Enable Chromium sandboxing. Defaults to {@code false}.
     */
    boolean chromiumSandbox() default false;

    /**
     * Whether to run browser in headless mode. More details for <a
     * href="https://developers.google.com/web/updates/2017/04/headless-chrome">Chromium</a> and <a
     * href="https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode">Firefox</a>. Defaults to {@code true}
     * unless the {@code devtools} option is {@code true}.
     */
    boolean headless() default true;

    /**
     * Slows down Playwright operations by the specified amount of milliseconds. Useful so that you can see what is going on.
     */
    double slowMo() default 0;

    /**
     * Args for use to launch the browser
     */
    String[] args() default { "--disable-gpu" };

    enum Browser {
        CHROMIUM,
        FIREFOX,
        WEBKIT
    }

}
