package org.acme;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import io.quarkiverse.playwright.InjectPlaywright;
import io.quarkiverse.playwright.PlaywrightSelector;
import io.quarkiverse.playwright.WithPlaywright;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@WithPlaywright(selectors = {
        @PlaywrightSelector(name = "tag", script = "{\n" +
                "  // Returns the first element matching given selector in the root's subtree.\n" +
                "  query(root, selector) {\n" +
                "    return root.querySelector(selector);\n" +
                "  },\n" +
                "  // Returns all elements matching given selector in the root's subtree.\n" +
                "  queryAll(root, selector) {\n" +
                "    return Array.from(root.querySelectorAll(selector));\n" +
                "  }\n" +
                "}")
})
public class WithSelectorsPlaywrightTest {

    @InjectPlaywright
    BrowserContext context;

    @TestHTTPResource("/")
    URL index;

    @Test
    public void testIndex() {
        final Page page = context.newPage();
        page.setContent("<div><button>Click me</button></div>");
        // Use the selector prefixed with its name.
        Locator button = page.locator("tag=button");
        // Combine it with built-in locators.
        page.locator("tag=div").getByText("Click me").click();
        // Can use it in any methods supporting selectors.
        int buttonCount = (int) page.locator("tag=button").count();
        Assertions.assertEquals(1, buttonCount);
    }

}