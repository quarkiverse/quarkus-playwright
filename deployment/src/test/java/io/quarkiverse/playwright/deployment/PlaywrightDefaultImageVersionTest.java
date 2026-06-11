package io.quarkiverse.playwright.deployment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Playwright;

/**
 * Guards against the default Playwright Dev Service image drifting out of sync with the
 * com.microsoft.playwright:playwright client library version.
 *
 * The dev service container pins the npx-installed Playwright server to the client library
 * version (see {@link PlaywrightServerContainer}), so a mismatch here isn't a functional bug.
 * It does mean the documented default image - and the browsers baked into it - no longer match
 * the version the extension actually exercises by default, which this test is meant to catch.
 */
class PlaywrightDefaultImageVersionTest {

    private static final Pattern IMAGE_TAG_VERSION_PATTERN = Pattern.compile("^v?(\\d+\\.\\d+\\.\\d+)");

    @Test
    void defaultImageVersionMatchesClientLibraryVersion() {
        final String clientVersion = Playwright.class.getPackage().getImplementationVersion();
        assertNotNull(clientVersion,
                "Could not read the com.microsoft.playwright:playwright client library version from its jar manifest");

        final String imageName = PlaywrightBuildTimeConfig.PlaywrightDevServicesConfig.DEFAULT_IMAGE;
        final String tag = imageName.substring(imageName.lastIndexOf(':') + 1);
        final Matcher matcher = IMAGE_TAG_VERSION_PATTERN.matcher(tag);
        assertNotNull(matcher.find() ? Boolean.TRUE : null,
                "Could not determine a Playwright version from the default image tag '" + tag + "'");

        assertEquals(clientVersion, matcher.group(1),
                "The default Playwright Dev Service image (" + imageName + ") is pinned to a version that no longer "
                        + "matches the com.microsoft.playwright:playwright client library version (" + clientVersion + "). "
                        + "When upgrading the playright.version property in pom.xml, also update "
                        + "PlaywrightBuildTimeConfig.PlaywrightDevServicesConfig.DEFAULT_IMAGE to the matching "
                        + "mcr.microsoft.com/playwright image tag.");
    }
}
