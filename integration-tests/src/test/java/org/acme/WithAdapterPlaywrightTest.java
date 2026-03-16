package org.acme;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.*;

import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithPlaywright(playwrightAdapter = WithAdapterPlaywrightTest.CustomAdapter.class)
public class WithAdapterPlaywrightTest {

    public static class CustomAdapter implements io.quarkiverse.playwright.PlaywrightAdapter {

        @Override
        public Browser.NewContextOptions adaptNewContextOptions(Browser.NewContextOptions newContextOptions) {
            newContextOptions.setViewportSize(100, 200);
            return newContextOptions;
        }
    }

    @InjectPlaywright
    BrowserContext context;

    @Test
    public void testIndex() {
        final Page page = context.newPage();

        Assertions.assertEquals(100, page.viewportSize().width);
        Assertions.assertEquals(200, page.viewportSize().height);
    }
}
