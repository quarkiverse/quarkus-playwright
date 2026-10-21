package io.quarkiverse.playwright;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;

class LaunchSettingsTest {

    @WithPlaywright(browser = WithPlaywright.Browser.WEBKIT, headless = true, channel = "chrome", slowMo = 5)
    static class Annotated {
    }

    private static final WithPlaywright OPTIONS = Annotated.class.getAnnotation(WithPlaywright.class);

    private static Config config(Map<String, String> properties) {
        return new SmallRyeConfigBuilder()
                .withSources(new PropertiesConfigSource(properties, "test", 100))
                .build();
    }

    @Test
    void annotationValuesAreUsedWithoutConfig() {
        assertEquals(new LaunchSettings(WithPlaywright.Browser.WEBKIT, true, "chrome", 5),
                LaunchSettings.resolve(OPTIONS, config(Map.of())));
    }

    @Test
    void configOverridesAnnotation() {
        LaunchSettings settings = LaunchSettings.resolve(OPTIONS, config(Map.of(
                LaunchSettings.BROWSER, "Firefox",
                LaunchSettings.HEADLESS, "false",
                LaunchSettings.CHANNEL, "msedge",
                LaunchSettings.SLOW_MO, "250")));

        assertEquals(new LaunchSettings(WithPlaywright.Browser.FIREFOX, false, "msedge", 250), settings);
    }

    @Test
    void blankValuesAreTreatedAsUnset() {
        LaunchSettings settings = LaunchSettings.resolve(OPTIONS, config(Map.of(
                LaunchSettings.BROWSER, " ",
                LaunchSettings.HEADLESS, " ",
                LaunchSettings.CHANNEL, " ",
                LaunchSettings.SLOW_MO, " ")));

        assertEquals(new LaunchSettings(WithPlaywright.Browser.WEBKIT, true, "chrome", 5), settings);
    }

    @ParameterizedTest
    @CsvSource({
            "quarkus.playwright.browser, netscape",
            "quarkus.playwright.headless, maybe",
            "quarkus.playwright.slow-mo, fast"
    })
    void invalidValuesFailWithHelpfulMessage(String key, String value) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> LaunchSettings.resolve(OPTIONS, config(Map.of(key, value))));

        assertTrue(e.getMessage().startsWith("Invalid value for " + key + ": '" + value + "'. Expected"),
                e.getMessage());
    }

    @Test
    void keysMatchConfigMapping() {
        Set<String> mapped = Arrays.stream(PlaywrightRuntimeConfig.class.getDeclaredMethods())
                .map(Method::getName)
                .filter(name -> !name.equals("endpoint"))
                .map(name -> "quarkus.playwright." + name.replaceAll("([A-Z])", "-$1").toLowerCase())
                .collect(Collectors.toSet());

        assertEquals(mapped, Set.copyOf(LaunchSettings.KEYS));
    }
}
