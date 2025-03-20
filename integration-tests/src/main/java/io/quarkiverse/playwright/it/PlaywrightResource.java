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

import org.jboss.logging.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

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
     * <p>This endpoint demonstrates basic Playwright functionality by:
     * <ul>
     *   <li>Launching a headless Chromium browser</li>
     *   <li>Creating a new page and navigating to google.com</li>
     *   <li>Retrieving and returning the page title</li>
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
}
