package io.quarkiverse.playwright.graal;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.quarkus.logging.Log;

/**
 * Replace System.out.println with Jboss Logging.
 */
@TargetClass(className = "com.microsoft.playwright.impl.LoggingSupport")
final class LoggingSupportSubstitution {

    @Alias
    private static boolean isEnabled;

    @Alias
    private static DateTimeFormatter timestampFormat;

    @Substitute
    static void logWithTimestamp(String message) {
        String timestamp = ZonedDateTime.now().format(timestampFormat);
        Log.infof("%s %s", timestamp, message);
    }

    @Substitute
    static void logApiIfEnabled(String message) {
        if (isEnabled) {
            logApi(message);
        }
    }

    @Substitute
    static void logApi(String message) {
        logWithTimestamp("pw:api " + message);
    }
}
