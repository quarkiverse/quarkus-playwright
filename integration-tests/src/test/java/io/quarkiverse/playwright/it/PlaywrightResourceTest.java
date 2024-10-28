package io.quarkiverse.playwright.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

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
}