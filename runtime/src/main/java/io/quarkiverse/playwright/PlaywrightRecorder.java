package io.quarkiverse.playwright;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;

import org.jboss.logging.Logger;

import com.microsoft.playwright.impl.driver.jar.DriverJar;

import io.quarkus.runtime.annotations.Recorder;

/**
 * A recorder for managing Playwright driver initialization.
 * This class handles the setup of the Playwright driver by
 * loading the driver resources and creating a file system
 * for those resources.
 */
@Recorder
public class PlaywrightRecorder {

    private static final Logger log = Logger.getLogger(PlaywrightRecorder.class);

    /**
     * Initializes the Playwright driver by obtaining its resource URI,
     * setting up a file system, and logging relevant information.
     *
     * <p>
     * This method attempts to retrieve the URI of the driver resources
     * and creates a new file system for accessing those resources. If the
     * file system cannot be created, an error is logged.
     * </p>
     *
     * In Native mode this is `resource:/` and must be registered before anything else.
     *
     * @throws RuntimeException if there is an error in URI syntax or
     *         during file system creation.
     */
    public void initialize() {
        try {
            // Retrieve the URI of the Playwright driver resources
            URI uri = DriverJar.getDriverResourceURI();
            log.infof("Playwright Driver: %s", uri);

            // Create a new file system for the driver resources typically resource:/
            FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            if (fs == null) {
                log.errorf("FileSystem Error NULL: %s", uri);
            }

            // Log the directory where the driver is located
            DriverJar jar = new DriverJar();
            log.debugf("Playwright Driver Directory: %s", jar.driverDir());
        } catch (URISyntaxException | IOException e) {
            // Wrap and throw any exceptions that occur during initialization
            throw new RuntimeException(e);
        }
    }
}