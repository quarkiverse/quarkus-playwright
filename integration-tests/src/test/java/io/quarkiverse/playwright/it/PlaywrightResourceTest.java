package io.quarkiverse.playwright.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PlaywrightResourceTest {

    @Test
    public void testGoogleEndpoint() {
        given()
                .when().get("/playwright")
                .then()
                .statusCode(200)
                .body(is("Google"));
    }

    @Test
    public void testAriaAiModeEndpoint() {
        given()
                .when().get("/playwright/aria")
                .then()
                .statusCode(200)
                // If Aria did snapshot in AI Mode then the iframe contents will be included
                .body(containsString("OpenStreetMap contributors"));
    }

    @Test
    public void testMultiBrowserScreenshotEndpoint() throws Exception {
        given()
                .when().get("/playwright/screenshot")
                .then()
                .statusCode(200)
                .body(is("Screenshots taken"));

        String[] browsers = { "chromium", "webkit", "firefox" };
        for (String browser : browsers) {
            Path screenshotPath = Paths.get("target/screenshot-" + browser + ".png");
            assertTrue(Files.exists(screenshotPath), "Screenshot file should exist for " + browser);
            assertTrue(Files.size(screenshotPath) > 0, "Screenshot file should not be empty for " + browser);
        }
    }
}