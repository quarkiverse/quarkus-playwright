package org.acme;

import static org.assertj.core.api.Assertions.*;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import io.quarkiverse.playwright.BrowserContextConfig;
import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithPlaywright(browserContext = @BrowserContextConfig(userAgent = "playwright-browser", defaultNavigationTimeout = "PT10s"))
class PlaywrightConfigTest {
    @InjectPlaywright
    BrowserContext context;

    @TestHTTPResource("/")
    URL index;

    @Test
    void timeoutWorks() {
        var page = context.newPage();
        page.route(
                index.toString(),
                r -> {
                    try {
                        // Introduce intentional timeout
                        TimeUnit.SECONDS.sleep(12);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    r.resume();
                });

        assertThatExceptionOfType(TimeoutError.class)
                .isThrownBy(() -> page.navigate(index.toString()));
    }

    @Test
    void testConfig() {
        var page = context.newPage();

        page.onRequest(r -> assertThat(r.headerValue(HttpHeaders.USER_AGENT)).isEqualTo("playwright-browser"));

        var response = page.navigate(index.toString());

        assertThat(response)
                .extracting(Response::status)
                .isEqualTo(200);

        page.waitForLoadState();

        PlaywrightAssertions.assertThat(page)
                .hasTitle("My Awesome App");

        // Make sure the web app is loaded and hits the backend
        var quinoaEl = page.waitForSelector(".toast-body.received");
        var greeting = quinoaEl.innerText();

        assertThat(greeting)
                .isEqualTo("Hello from RESTEasy Reactive");
    }
}
