<div align="center">
<img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/playwright.svg" height="70" >

# Quarkus Playwright
</div>
<br>

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.playwright/quarkus-playwright?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.playwright/quarkus-playwright)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/quarkiverse/quarkus-playwright/actions/workflows/build.yml/badge.svg)](https://github.com/quarkiverse/quarkus-playwright/actions/workflows/build.yml)

[Playwright](https://playwright.dev/) is an open-source automation library designed for browser testing and web scraping. This extension supports two primary use cases:

1. **Testing:** Perform end-to-end tests for your Quarkus web application.
2. **Runtime:** Leverage Playwright for screen scraping or other browser tasks in your runtime application, including support for GraalVM native compilation.

All the information you need to use Quarkus Playwright is in the [user documentation](https://docs.quarkiverse.io/quarkus-playwright/dev/).

## Test Usage

The primary use case for Playwright is integration with `@QuarkusTest` for end-to-end testing of your application. You can easily create effective cross-browser end-to-end tests for your Quarkus web application using Playwright with frameworks such as Qute, Quinoa, Renarde, Web-Bundler, and MyFaces. Playwright Test was specifically designed to meet the requirements of end-to-end testing. It supports all modern rendering engines, including Chromium, WebKit, and Firefox. You can run tests on Windows, Linux, and macOS—either locally or in CI—both in headless and headed modes, with native mobile emulation for Google Chrome on Android and Mobile Safari.


Just add the dependency as `<scope>test</scope>` to pom.xml:
```xml
<dependency>
    <groupId>io.quarkiverse.playwright</groupId>
    <artifactId>quarkus-playwright</artifactId>
    <version>${playwright.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>
```
Write your tests:
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

Use the annotation `@WithPlaywright()` to change the browser (Chromium, Firefox, Webkit), headless, enable debug, logs and other options.

Debug your tests with the Playwright inspector `@WithPlaywright(debug=true)`:

![Debug](https://github.com/quarkiverse/quarkus-playwright/blob/main/docs/modules/ROOT/assets/images/playwright-debug.gif)

## Runtime Usage

Leverage Playwright for screen scraping or other browser tasks in your runtime application, including support for GraalVM native compilation.

Just add the `runtime` dependency to pom.xml:
```xml
<dependency>
    <groupId>io.quarkiverse.playwright</groupId>
    <artifactId>quarkus-playwright</artifactId>
    <version>${playwright.version}</version>
</dependency>
```

## Native

If you plan on running in a Docker image we highly recommend you use a pre-built image from Microsoft `mcr.microsoft.com/playwright:v1.48.1` which is based on Ubuntu and already has all libraries and tools necessary for PlayWright.

```yaml
FROM mcr.microsoft.com/playwright:v1.48.1-noble
WORKDIR /work/
RUN chown 1001:root /work \
    && chmod g+rwX /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*.properties target/*.so /work/
COPY --chown=1001:root target/*-runner /work/application
# Make application executable for all users
RUN chmod ugo+x /work/application
EXPOSE 8080
USER 1001
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

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
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kucharzyk"><img src="https://avatars.githubusercontent.com/u/5682894?v=4?s=100" width="100px;" alt="Tomasz Kucharzyk"/><br /><sub><b>Tomasz Kucharzyk</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-playwright/commits?author=kucharzyk" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://gjstewart.net"><img src="https://avatars.githubusercontent.com/u/7083701?v=4?s=100" width="100px;" alt="Greg Stewart"/><br /><sub><b>Greg Stewart</b></sub></a><br /><a href="#ideas-GregJohnStewart" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/thomaswiradikusuma"><img src="https://avatars.githubusercontent.com/u/169544234?v=4?s=100" width="100px;" alt="thomaswiradikusuma"/><br /><sub><b>thomaswiradikusuma</b></sub></a><br /><a href="#ideas-thomaswiradikusuma" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://developers.redhat.com/author/eric-deandrea"><img src="https://avatars.githubusercontent.com/u/363447?v=4?s=100" width="100px;" alt="Eric Deandrea"/><br /><sub><b>Eric Deandrea</b></sub></a><br /><a href="#ideas-edeandrea" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://maxsonaraujo.miwteam.com.br/pt-BR"><img src="https://avatars.githubusercontent.com/u/91495720?v=4?s=100" width="100px;" alt="Maxson Araújo"/><br /><sub><b>Maxson Araújo</b></sub></a><br /><a href="#ideas-maxsonaraujo" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/rmanibus"><img src="https://avatars.githubusercontent.com/u/10419172?v=4?s=100" width="100px;" alt="Loïc Hermann"/><br /><sub><b>Loïc Hermann</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-playwright/commits?author=rmanibus" title="Tests">⚠️</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!