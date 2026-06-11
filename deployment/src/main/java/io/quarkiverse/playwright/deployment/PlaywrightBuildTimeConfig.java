package io.quarkiverse.playwright.deployment;

import io.quarkus.runtime.annotations.ConfigDocDefault;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "quarkus.playwright")
public interface PlaywrightBuildTimeConfig {

    @ConfigDocSection
    PlaywrightDevServicesConfig devservices();

    @ConfigGroup
    interface PlaywrightDevServicesConfig {
        String DEFAULT_IMAGE = "mcr.microsoft.com/playwright:v1.60.0-noble";

        /**
         * Starts a Playwright server container in dev and test modes.
         */
        @WithDefault("false")
        boolean enabled();

        /**
         * Playwright container image.
         */
        @WithDefault(DEFAULT_IMAGE)
        @ConfigDocDefault(DEFAULT_IMAGE)
        String imageName();

        /**
         * Starts the playwright container with verbose logging
         */
        @WithDefault("false")
        boolean verbose();

        /**
         * Whether to use a shared Docker network for the Playwright container, allowing it
         * to access the host machine via {@code host.testcontainers.internal} (e.g. to reach
         * the application under test running on the host during integration tests).
         */
        @WithDefault("false")
        boolean sharedNetwork();
    }
}
