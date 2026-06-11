package io.quarkiverse.playwright.deployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.microsoft.playwright.Playwright;

/**
 * A Testcontainers implementation for running a Playwright server in a Docker container.
 *
 * This container extends GenericContainer to provide a containerized Playwright server
 * that listens on port 3000 and can be used for browser automation testing. The npx-installed
 * playwright CLI version is pinned to the version of the com.microsoft.playwright:playwright
 * client library on the classpath (read from its jar manifest), since the server and client
 * must run matching Playwright versions regardless of which Docker image is configured.
 *
 * The container supports configuration options including:
 * - Custom Docker image selection
 * - Verbose mode for debugging with Playwright API logs
 * - Shared network configuration for container networking
 *
 * When verbose mode is enabled, the container will output both stdout and stderr logs
 * to the configured logger, with stdout logged at INFO level and stderr at ERROR level.
 * Debug output from Playwright's internal API (pw:api) is also enabled in verbose mode.
 *
 * The container exposes the Playwright server on port 3000 and uses a listening port
 * wait strategy to ensure the server is ready before proceeding with tests.
 */
class PlaywrightServerContainer extends GenericContainer<PlaywrightServerContainer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaywrightServerContainer.class);
    public static final int PLAYWRIGHT_SERVER_PORT = 3000;

    record PlaywrightDevServiceConfiguration(String imageName, boolean verbose, boolean sharedNetwork) {
    }

    private final PlaywrightDevServiceConfiguration config;

    PlaywrightServerContainer(PlaywrightDevServiceConfiguration config) {
        super(DockerImageName.parse(config.imageName));
        this.config = config;
        if (config.verbose) {
            withEnv("DEBUG", "pw:api");
        }
        if (config.sharedNetwork) {
            withNetwork(Network.SHARED);
        }
        withExposedPorts(PLAYWRIGHT_SERVER_PORT);
        withCommand("/bin/sh", "-c", playwrightServerCommand());
        waitingFor(Wait.forListeningPort());
    }

    /**
     * Pins the npx-installed playwright CLI/server to the exact version of the
     * com.microsoft.playwright:playwright client library on the classpath, read from its jar
     * manifest's Implementation-Version. The Playwright client and server must run matching
     * versions to talk to each other. The version baked into a given Docker image tag isn't
     * a reliable proxy for that (the tag may be missing, e.g. for a user-supplied custom image).
     * Without an explicit version, "npx -y playwright" would resolve whatever is newest on the
     * npm registry at container start, silently drifting away from the client and causing the
     * "Playwright version mismatch" server/client error.
     */
    private static String playwrightServerCommand() {
        final String clientVersion = Playwright.class.getPackage().getImplementationVersion();
        final String npxPackage;
        if (clientVersion != null) {
            npxPackage = "playwright@" + clientVersion;
        } else {
            LOGGER.warn(
                    "Could not determine the com.microsoft.playwright:playwright client library version; falling back to "
                            + "the latest npx playwright package, which may not match the client version and cause a server/client "
                            + "version mismatch");
            npxPackage = "playwright";
        }
        return "npx -y " + npxPackage + " run-server --host 0.0.0.0 --port " + PLAYWRIGHT_SERVER_PORT;
    }

    @Override
    public void start() {
        super.start();
        if (config.verbose) {
            followOutput(this::writeToStdOut, OutputFrame.OutputType.STDOUT);
            followOutput(this::writeToStdErr, OutputFrame.OutputType.STDERR);
        }
    }

    private void writeToStdOut(OutputFrame frame) {
        writeOutputFrame(frame, Level.INFO);
    }

    private void writeToStdErr(OutputFrame frame) {
        writeOutputFrame(frame, Level.ERROR);
    }

    private void writeOutputFrame(OutputFrame frame, Level level) {
        LOGGER.atLevel(level).log(frame.getUtf8StringWithoutLineEnding());
    }

}
