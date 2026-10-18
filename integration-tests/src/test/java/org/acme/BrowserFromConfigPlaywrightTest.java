package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;

import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

/**
 * The annotation asks for the default browser (Chromium), but {@code quarkus.playwright.browser} from a profiled
 * entry in {@code application.properties} wins. This checks the property is visible when the test resource starts.
 */
@QuarkusTest
@WithPlaywright
@TestProfile(BrowserFromConfigPlaywrightTest.FirefoxProfile.class)
public class BrowserFromConfigPlaywrightTest {

    public static class FirefoxProfile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "browser-from-config";
        }
    }

    @InjectPlaywright
    Browser browser;

    @Test
    public void browserComesFromConfig() {
        assertEquals("firefox", browser.browserType().name());
    }
}
