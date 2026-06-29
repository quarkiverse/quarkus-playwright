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
}
