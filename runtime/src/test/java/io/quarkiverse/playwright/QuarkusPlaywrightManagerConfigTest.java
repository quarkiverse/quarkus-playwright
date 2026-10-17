package io.quarkiverse.playwright;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class QuarkusPlaywrightManagerConfigTest {

    private static final List<String> PROPERTIES = List.of(
            "quarkus.playwright.browser",
            "quarkus.playwright.headless",
            "quarkus.playwright.channel",
            "quarkus.playwright.slow-mo");

    @WithPlaywright(browser = WithPlaywright.Browser.WEBKIT, headless = true, channel = "chrome", slowMo = 5)
    static class Annotated {
    }

    @AfterEach
    void clearProperties() {
        PROPERTIES.forEach(System::clearProperty);
    }

    private static QuarkusPlaywrightManager resolve() {
        QuarkusPlaywrightManager manager = new QuarkusPlaywrightManager();
        manager.init(Annotated.class.getAnnotation(WithPlaywright.class));
        manager.resolveOptions();
        return manager;
    }

    @Test
    void annotationValuesAreUsedWithoutConfig() {
        QuarkusPlaywrightManager manager = resolve();
        assertEquals(WithPlaywright.Browser.WEBKIT, manager.browser());
        assertTrue(manager.headless());
        assertEquals("chrome", manager.channel());
        assertEquals(5, manager.slowMo());
    }

    @Test
    void configOverridesAnnotation() {
        System.setProperty("quarkus.playwright.browser", "firefox");
        System.setProperty("quarkus.playwright.headless", "false");
        System.setProperty("quarkus.playwright.channel", "msedge");
        System.setProperty("quarkus.playwright.slow-mo", "250");

        QuarkusPlaywrightManager manager = resolve();
        assertEquals(WithPlaywright.Browser.FIREFOX, manager.browser());
        assertFalse(manager.headless());
        assertEquals("msedge", manager.channel());
        assertEquals(250, manager.slowMo());
    }

    @Test
    void invalidBrowserFailsWithHelpfulMessage() {
        System.setProperty("quarkus.playwright.browser", "netscape");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                QuarkusPlaywrightManagerConfigTest::resolve);
        assertTrue(e.getMessage().contains("netscape"), e.getMessage());
    }
}
