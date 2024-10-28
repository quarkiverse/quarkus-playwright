package io.quarkiverse.playwright;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field for injection of a Playwright instance.
 * <p>
 * This annotation is used in Quarkus applications to automatically inject
 * Playwright resources into fields where it is applied. It is intended
 * for use with fields of classes that depend on Playwright for browser automation
 * and testing.
 * </p>
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * @InjectPlaywright
 * private Playwright playwright;
 * }
 * </pre>
 * </p>
 *
 * <p>
 * The annotation should be retained at runtime, as it is processed by the
 * Quarkus framework to inject the necessary Playwright instances.
 * </p>
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME) // The annotation is available at runtime for Quarkus to process.
@Target(ElementType.FIELD) // This annotation can only be applied to fields.
public @interface InjectPlaywright {
}