package io.quarkiverse.playwright;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * These settings all correspond to settings on {@link com.microsoft.playwright.Browser.NewContextOptions NewContextOptions}
 * and {@link com.microsoft.playwright.BrowserContext BrowserContext}.
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BrowserContextConfig {
    /**
     * Default maximum navigation time in milliseconds
     * <p>
     * Parsed using {@link java.time.Duration#parse(CharSequence)}
     * </p>
     *
     * @see com.microsoft.playwright.BrowserContext#setDefaultNavigationTimeout(double)
     */
    String defaultNavigationTimeout() default "";

    /**
     * Default maximum time for all methods accepting a timeout option, in milliseconds
     * <p>
     * Parsed using {@link java.time.Duration#parse(CharSequence)}
     * </p>
     *
     * @see com.microsoft.playwright.BrowserContext#setDefaultTimeout(double)
     */
    String defaultTimeout() default "";

    /**
     * Specific user agent to use in all contexts
     *
     * @see com.microsoft.playwright.Browser.NewContextOptions#setUserAgent(String)
     */
    String userAgent() default "";

    /**
     * Specify user local, for example {@code en-GB}, {@code de-DE}, etc.
     * Locale will affect {@code navigator.language}, {@code Accept-Language} request header value,
     * as well as number and date formatting rules.
     *
     * @see com.microsoft.playwright.Browser.NewContextOptions#setLocale(String)
     */
    String locale() default "";

    /**
     * Whether to emulate the network being offline for the browser context
     *
     * @see com.microsoft.playwright.Browser.NewContextOptions#setOffline(boolean)
     */
    boolean offline() default false;
}
