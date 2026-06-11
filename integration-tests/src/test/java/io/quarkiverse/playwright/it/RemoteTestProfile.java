package io.quarkiverse.playwright.it;

import io.quarkus.test.junit.QuarkusTestProfile;

public class RemoteTestProfile implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "remote";
    }
}
