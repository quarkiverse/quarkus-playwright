package io.quarkiverse.playwright;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a Playwright selector for test elements.
 * Can be used multiple times on a method or class.
 */
@Retention(RetentionPolicy.RUNTIME) // Annotation is retained at runtime for test setup.
@Target(ElementType.TYPE) // Applied only at the class level.
@Inherited
public @interface PlaywrightSelector {

    /**
     * Name to reference this selector script.
     */
    String name();

    /**
     * Script that evaluates to a selector engine instance. The script is evaluated in the page context.
     */
    String script();

}
