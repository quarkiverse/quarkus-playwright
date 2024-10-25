package io.quarkiverse.playwright.deployment.devui;

import com.microsoft.playwright.Playwright;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.ExternalPageBuilder;
import io.quarkus.devui.spi.page.Page;

/**
 * Dev UI card for displaying important details such as the Playwright library version.
 */
public class PlaywrightDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createVersion(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();

        final ExternalPageBuilder versionPage = Page.externalPageBuilder("Playwright Version")
                .icon("font-awesome-solid:tag")
                .url("https://playwright.dev")
                .doNotEmbed()
                .staticLabel(Playwright.class.getPackage().getImplementationVersion());

        card.addPage(versionPage);

        card.setCustomCard("qwc-playwright-card.js");

        cardPageBuildItemBuildProducer.produce(card);
    }
}