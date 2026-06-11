/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.quarkiverse.playwright.it;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.logging.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaSnapshotMode;

/**
 * Resource class demonstrating Playwright browser automation capabilities.
 * This class provides endpoints for interacting with web pages using Playwright.
 */
@Path("/playwright")
@ApplicationScoped
public class PlaywrightResource {

    private static final Logger log = Logger.getLogger(PlaywrightResource.class);

    /**
     * Navigates to Google homepage and retrieves the page title using Playwright.
     *
     * <p>
     * This endpoint demonstrates basic Playwright functionality by:
     * <ul>
     * <li>Launching a headless Chromium browser</li>
     * <li>Creating a new page and navigating to google.com</li>
     * <li>Retrieving and returning the page title</li>
     * </ul>
     * </p>
     *
     * @return The title of the Google homepage
     */
    @GET
    public String google() {
        String pageTitle;
        final BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setChromiumSandbox(false)
                .setChannel("")
                .setArgs(List.of("--disable-gpu"));
        final Map<String, String> env = new HashMap<>(System.getenv());
        env.put("DEBUG", "pw:api");
        try (Playwright playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env))) {
            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                Page page = browser.newPage();
                page.navigate("https://www.google.com/");
                pageTitle = page.title();
                log.infof("Page title: %s", pageTitle);
            }
        }
        return pageTitle;
    }

    /**
     * Endpoint to load an Aria Snapshot in AI mode
     */
    @GET
    @Path("/aria")
    public String ariaAiModeSnapshot() {
        String ariaSnapshot;
        final BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setChromiumSandbox(false)
                .setChannel("")
                .setArgs(List.of("--disable-gpu"));
        final Map<String, String> env = new HashMap<>(System.getenv());
        env.put("DEBUG", "pw:api");
        try (Playwright playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env))) {
            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                Page page = browser.newPage();
                page.navigate("https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe");
                page.waitForSelector("interactive-example iframe");
                Page.AriaSnapshotOptions ariaSnapshotOptions = new Page.AriaSnapshotOptions();
                ariaSnapshotOptions.setMode(AriaSnapshotMode.AI);
                ariaSnapshot = page.ariaSnapshot(ariaSnapshotOptions);
                log.infof("Page title: %s", ariaSnapshot);
            }
        }
        return ariaSnapshot;
    }

    /**
     * Endpoint to take a screenshot using multiple browser types
     */
    @GET
    @Path("/screenshot")
    public String multiBrowserScreenshot() {
        final Map<String, String> env = new HashMap<>(System.getenv());
        env.put("DEBUG", "pw:api");
        final BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setChromiumSandbox(false)
                .setChannel("");

        try (Playwright playwright = Playwright.create(new Playwright.CreateOptions().setEnv(env))) {
            List<BrowserType> browserTypes = List.of(
                    playwright.chromium(),
                    playwright.webkit(),
                    playwright.firefox());

            for (BrowserType browserType : browserTypes) {
                try (Browser browser = browserType.launch(launchOptions)) {
                    final com.microsoft.playwright.BrowserContext context = browser.newContext();
                    final Page page = context.newPage();
                    page.navigate("https://playwright.dev/");
                    java.nio.file.Path screenshotPath = java.nio.file.Paths
                            .get("target/screenshot-" + browserType.name() + ".png");
                    page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath));
                }
            }
        }
        return "Screenshots taken";
    }
}
