<div align="center">
<img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/playwright.svg" height="70" >

# Quarkus Playwright
</div>
<br>

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.playwright/quarkus-playwright?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.playwright/quarkus-playwright)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/quarkiverse/quarkus-playwright/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-playwright/actions/workflows/build.yml)


Easily create effective cross-browsers e2e tests for your Quarkus web-app using Playwright (Qute, Quinoa, Renarde, Web-Bundler, ...):

All the information you need to use Quarkus Playwright is in the [user documentation](https://docs.quarkiverse.io/quarkus-playwright/dev/).

## Usage

````java
@QuarkusTest
@WithPlaywright
public class WithDefaultPlaywrightTest {

    @InjectPlaywright
    BrowserContext context;

    @TestHTTPResource("/")
    URL index;

    @Test
    public void testIndex() {
        final Page page = context.newPage();
        Response response = page.navigate(index.toString());
        Assertions.assertEquals("OK", response.statusText());

        page.waitForLoadState();

        String title = page.title();
        Assertions.assertEquals("My Awesome App", title);

        // Make sure the web app is loaded and hits the backend
        final ElementHandle quinoaEl = page.waitForSelector(".toast-body.received");
        String greeting = quinoaEl.innerText();
        Assertions.assertEquals("Hello from RESTEasy Reactive", greeting);
    }
}
````

Debug your tests with the Playwright inspector `@WithPlaywright(debug=true)`:

![Debug](https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/playwright-debug.gif)
## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ia3andy"><img src="https://avatars.githubusercontent.com/u/2223984?v=4?s=100" width="100px;" alt="Andy Damevin"/><br /><sub><b>Andy Damevin</b></sub></a><br /><a href="#maintenance-ia3andy" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-playwright/commits?author=ia3andy" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://melloware.com"><img src="https://avatars.githubusercontent.com/u/4399574?v=4?s=100" width="100px;" alt="Melloware"/><br /><sub><b>Melloware</b></sub></a><br /><a href="#maintenance-melloware" title="Maintenance">🚧</a> <a href="https://github.com/quarkiverse/quarkus-playwright/commits?author=melloware" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
