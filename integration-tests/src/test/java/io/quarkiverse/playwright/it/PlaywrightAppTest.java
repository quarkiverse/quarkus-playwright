package io.quarkiverse.playwright.it;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.BrowserContext;

import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@WithPlaywright
@QuarkusTest
@TestProfile(RemoteTestProfile.class)
public class PlaywrightAppTest {

    @InjectPlaywright
    BrowserContext context;

    @ConfigProperty(name = "quarkus.http.test-port")
    int httpPort;

    @Test
    public void test() {
        var page = context.newPage();
        page.navigate("http://host.testcontainers.internal:" + httpPort);

        page.waitForLoadState();

        assertThat(page.getByTestId("greeting")).hasText("/hello");
    }
}
