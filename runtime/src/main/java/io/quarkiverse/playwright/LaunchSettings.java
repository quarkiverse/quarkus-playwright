package io.quarkiverse.playwright;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;

/**
 * Browser launch settings for a {@link WithPlaywright} test: the annotation values, overridden by
 * {@code quarkus.playwright.*} config properties when they are set.
 * <p>
 * The properties are declared on {@link PlaywrightRuntimeConfig} for documentation, but are read here by key: config
 * mappings are not available yet when test resources start (see #112). {@link #KEYS} is checked against the mapping
 * by a unit test so the two cannot drift apart.
 * </p>
 */
record LaunchSettings(WithPlaywright.Browser browser, boolean headless, String channel, double slowMo) {

    static final String BROWSER = "quarkus.playwright.browser";
    static final String HEADLESS = "quarkus.playwright.headless";
    static final String CHANNEL = "quarkus.playwright.channel";
    static final String SLOW_MO = "quarkus.playwright.slow-mo";

    static final List<String> KEYS = List.of(BROWSER, HEADLESS, CHANNEL, SLOW_MO);

    static LaunchSettings resolve(WithPlaywright options, Config config) {
        return new LaunchSettings(
                value(config, BROWSER, LaunchSettings::parseBrowser).orElse(options.browser()),
                value(config, HEADLESS, LaunchSettings::parseBoolean).orElse(options.headless()),
                value(config, CHANNEL, String::trim).orElse(options.channel()),
                value(config, SLOW_MO, Double::parseDouble).orElse(options.slowMo()));
    }

    /**
     * Reads a property as a string so that blank values count as unset and bad values get a clear message.
     */
    private static <T> Optional<T> value(Config config, String key, Function<String, T> parser) {
        return config.getOptionalValue(key, String.class)
                .filter(StringUtils::isNotBlank)
                .map(raw -> {
                    try {
                        return parser.apply(raw.trim());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(
                                "Invalid value for " + key + ": '" + raw + "'. " + expected(key), e);
                    }
                });
    }

    private static String expected(String key) {
        return switch (key) {
            case BROWSER -> "Expected one of " + Arrays.toString(WithPlaywright.Browser.values());
            case HEADLESS -> "Expected true or false";
            case SLOW_MO -> "Expected a number of milliseconds";
            default -> "";
        };
    }

    private static WithPlaywright.Browser parseBrowser(String value) {
        return WithPlaywright.Browser.valueOf(value.toUpperCase(Locale.ROOT));
    }

    private static boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException("not a boolean");
    }
}
