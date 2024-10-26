package io.quarkiverse.playwright.deployment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.jar.DriverJar;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.Timing;
import com.microsoft.playwright.options.ViewportSize;

import io.quarkiverse.playwright.runtime.PlaywrightRecorder;
import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.NativeImageEnableAllCharsetsBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourcePatternsBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.logging.Log;

class PlaywrightProcessor {

    private static final String FEATURE = "playwright";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void indexTransitiveDependencies(BuildProducer<IndexDependencyBuildItem> index) {
        index.produce(new IndexDependencyBuildItem("com.microsoft.playwright", "driver"));
        index.produce(new IndexDependencyBuildItem("com.microsoft.playwright", "driver-bundle"));
        index.produce(new IndexDependencyBuildItem("com.microsoft.playwright", "playwright"));
    }

    @BuildStep
    NativeImageEnableAllCharsetsBuildItem enableAllCharsetsBuildItem() {
        return new NativeImageEnableAllCharsetsBuildItem();
    }

    @BuildStep
    void registerForReflection(CombinedIndexBuildItem combinedIndex, BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        //@formatter:off
        final List<String> classNames = new ArrayList<>();

        classNames.add("com.microsoft.playwright.impl.Message");
        classNames.add("com.microsoft.playwright.impl.SerializedArgument");
        classNames.add("com.microsoft.playwright.impl.SerializedValue");
        classNames.add("com.microsoft.playwright.impl.SerializedValue$O");
        classNames.add(Browser.CloseOptions.class.getName());
        classNames.add(Browser.NewContextOptions.class.getName());
        classNames.add(Browser.NewPageOptions.class.getName());
        classNames.add(Browser.StartTracingOptions.class.getName());
        classNames.add(DriverJar.class.getName());
        classNames.add(ElementHandle.CheckOptions.class.getName());
        classNames.add(ElementHandle.ClickOptions.class.getName());
        classNames.add(ElementHandle.DblclickOptions.class.getName());
        classNames.add(ElementHandle.FillOptions.class.getName());
        classNames.add(ElementHandle.HoverOptions.class.getName());
        classNames.add(ElementHandle.InputValueOptions.class.getName());
        classNames.add(ElementHandle.PressOptions.class.getName());
        classNames.add(ElementHandle.ScreenshotOptions.class.getName());
        classNames.add(ElementHandle.ScrollIntoViewIfNeededOptions.class.getName());
        classNames.add(ElementHandle.SelectTextOptions.class.getName());
        classNames.add(ElementHandle.SetInputFilesOptions.class.getName());
        classNames.add(ElementHandle.TapOptions.class.getName());
        classNames.add(ElementHandle.TypeOptions.class.getName());
        classNames.add(ElementHandle.UncheckOptions.class.getName());
        classNames.add(ElementHandle.WaitForElementStateOptions.class.getName());
        classNames.add(ElementHandle.WaitForSelectorOptions.class.getName());
        classNames.add(HttpHeader.class.getName());
        classNames.add(Timing.class.getName());
        classNames.add(ViewportSize.class.getName());
        classNames.addAll(collectImplementors(combinedIndex, Playwright.class.getName()));

        //@formatter:on
        final TreeSet<String> uniqueClasses = new TreeSet<>(classNames);
        Log.debugf("Playwright Reflection: %s", uniqueClasses);

        reflectiveClass.produce(
                ReflectiveClassBuildItem.builder(uniqueClasses.toArray(new String[0])).constructors().methods().fields()
                        .serialization().unsafeAllocated().build());
    }

    @BuildStep(onlyIf = IsNormal.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    void registerRuntimeDrivers(PlaywrightRecorder recorder) {
        recorder.initialize();
    }

    @BuildStep(onlyIf = IsNormal.class)
    void registerNativeDrivers(BuildProducer<NativeImageResourcePatternsBuildItem> nativeImageResourcePatterns) {
        final NativeImageResourcePatternsBuildItem.Builder builder = NativeImageResourcePatternsBuildItem.builder();
        builder.includeGlob("driver/**");
        nativeImageResourcePatterns.produce(builder.build());
    }

    private List<String> collectSubclasses(CombinedIndexBuildItem combinedIndex, String className) {
        List<String> classes = combinedIndex.getIndex()
                .getAllKnownSubclasses(DotName.createSimple(className))
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toList());
        classes.add(className);
        Log.debugf("Subclasses: %s", classes);
        return classes;
    }

    private List<String> collectImplementors(CombinedIndexBuildItem combinedIndex, String className) {
        Set<String> classes = combinedIndex.getIndex()
                .getAllKnownImplementors(DotName.createSimple(className))
                .stream()
                .map(ClassInfo::toString)
                .collect(Collectors.toCollection(HashSet::new));
        classes.add(className);
        Set<String> subclasses = new HashSet<>();
        for (String implementationClass : classes) {
            subclasses.addAll(collectSubclasses(combinedIndex, implementationClass));
        }
        classes.addAll(subclasses);
        Log.debugf("Implementors: %s", classes);
        return new ArrayList<>(classes);
    }
}