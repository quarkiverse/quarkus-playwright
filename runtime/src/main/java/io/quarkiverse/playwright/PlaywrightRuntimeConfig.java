package io.quarkiverse.playwright;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.playwright")
public interface PlaywrightRuntimeConfig {

    /**
     * Optional Playwright websocket endpoint. If set, Playwright connects to this endpoint instead of launching a local
     * browser.
     */
    Optional<String> endpoint();

    /**
     * Browser used by {@code @WithPlaywright} tests. When set, it overrides the {@code browser} attribute of the
     * annotation, which makes it easy to run the whole test suite against another browser, e.g.
     * {@code -Dquarkus.playwright.browser=firefox}.
     */
    Optional<WithPlaywright.Browser> browser();

    /**
     * Whether {@code @WithPlaywright} tests run the browser in headless mode. When set, it overrides the
     * {@code headless} attribute of the annotation, e.g. {@code -Dquarkus.playwright.headless=false} to watch tests
     * locally.
     */
    Optional<Boolean> headless();

    /**
     * Browser distribution channel used by {@code @WithPlaywright} tests, such as {@code chrome} or {@code msedge}.
     * When set, it overrides the {@code channel} attribute of the annotation.
     */
    Optional<String> channel();

    /**
     * Slows down Playwright operations in {@code @WithPlaywright} tests by the given number of milliseconds. When set,
     * it overrides the {@code slowMo} attribute of the annotation.
     */
    Optional<Double> slowMo();
}
