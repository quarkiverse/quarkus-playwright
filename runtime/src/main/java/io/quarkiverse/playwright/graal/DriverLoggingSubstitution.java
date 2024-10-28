package io.quarkiverse.playwright.graal;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.quarkus.logging.Log;

/**
 * Replace System.err.println with Jboss Logging.
 */
@TargetClass(className = "com.microsoft.playwright.impl.driver.DriverLogging")
final class DriverLoggingSubstitution {

    @Alias
    private static boolean isEnabled;

    @Alias
    private static DateTimeFormatter timestampFormat;

    @Substitute
    static void logWithTimestamp(String message) {
        if (!isEnabled) {
            return;
        }
        String timestamp = ZonedDateTime.now().format(timestampFormat);
        Log.infof("%s %s", timestamp, message);
    }
}
