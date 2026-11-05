package io.quarkiverse.playwright;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class QuarkusPlaywrightManagerTest {

    @Test
    void baseUrlIsTestUrlForLocalBrowser() {
        assertEquals("http://localhost:8081", QuarkusPlaywrightManager.baseUrl("http://localhost:8081", false));
    }

    @Test
    void baseUrlUsesTestcontainersHostOnSharedNetwork() {
        assertEquals("http://host.testcontainers.internal:8081/app",
                QuarkusPlaywrightManager.baseUrl("http://localhost:8081/app", true));
    }

    @Test
    void baseUrlIsNullWithoutTestUrl() {
        assertNull(QuarkusPlaywrightManager.baseUrl(null, true));
    }
}
