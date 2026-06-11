package io.quarkiverse.playwright.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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
}