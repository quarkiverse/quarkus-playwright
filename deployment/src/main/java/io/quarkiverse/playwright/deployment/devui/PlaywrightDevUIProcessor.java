package io.quarkiverse.playwright.deployment.devui;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.page.CardPageBuildItem;

/**
 * Dev UI card for displaying important details such as the Playwright library version.
 */
public class PlaywrightDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    void createVersion(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {
        final CardPageBuildItem card = new CardPageBuildItem();
        card.addLibraryVersion("com.microsoft.playwright", "playwright", "Playwright",
                "https://playwright.dev");
        cardPageBuildItemBuildProducer.produce(card);
    }
}