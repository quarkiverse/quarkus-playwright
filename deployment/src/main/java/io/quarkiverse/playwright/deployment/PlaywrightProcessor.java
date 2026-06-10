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
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.impl.driver.jar.DriverJar;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.Timing;
import com.microsoft.playwright.options.ViewportSize;

import io.quarkiverse.playwright.PlaywrightRecorder;
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
        classNames.add(ElementHandle.SetCheckedOptions.class.getName());
        classNames.add(ElementHandle.SetInputFilesOptions.class.getName());
        classNames.add(ElementHandle.TapOptions.class.getName());
        classNames.add(ElementHandle.TypeOptions.class.getName());
        classNames.add(ElementHandle.UncheckOptions.class.getName());
        classNames.add(ElementHandle.WaitForElementStateOptions.class.getName());
        classNames.add(ElementHandle.WaitForSelectorOptions.class.getName());
        classNames.add(HttpHeader.class.getName());
        classNames.add(Timing.class.getName());
        classNames.add(ViewportSize.class.getName());
        classNames.add(Frame.AddScriptTagOptions.class.getName());
        classNames.add(Frame.AddStyleTagOptions.class.getName());
        classNames.add(Frame.CheckOptions.class.getName());
        classNames.add(Frame.ClickOptions.class.getName());
        classNames.add(Frame.DblclickOptions.class.getName());
        classNames.add(Frame.DispatchEventOptions.class.getName());
        classNames.add(Frame.FillOptions.class.getName());
        classNames.add(Frame.FocusOptions.class.getName());
        classNames.add(Frame.GetAttributeOptions.class.getName());
        classNames.add(Frame.GetByRoleOptions.class.getName());
        classNames.add(Frame.GetByTextOptions.class.getName());
        classNames.add(Frame.HoverOptions.class.getName());
        classNames.add(Frame.InnerHTMLOptions.class.getName());
        classNames.add(Frame.InnerTextOptions.class.getName());
        classNames.add(Frame.InputValueOptions.class.getName());
        classNames.add(Frame.IsVisibleOptions.class.getName());
        classNames.add(Frame.LocatorOptions.class.getName());
        classNames.add(Frame.PressOptions.class.getName());
        classNames.add(Frame.SelectOptionOptions.class.getName());
        classNames.add(Frame.SetContentOptions.class.getName());
        classNames.add(Frame.SetInputFilesOptions.class.getName());
        classNames.add(Frame.TapOptions.class.getName());
        classNames.add(Frame.TextContentOptions.class.getName());
        classNames.add(Frame.TypeOptions.class.getName());
        classNames.add(Frame.UncheckOptions.class.getName());
        classNames.add(Frame.UncheckOptions.class.getName());
        classNames.add(Frame.WaitForFunctionOptions.class.getName());
        classNames.add(Frame.WaitForLoadStateOptions.class.getName());
        classNames.add(Frame.WaitForSelectorOptions.class.getName());
        classNames.add(Frame.WaitForURLOptions.class.getName());
        classNames.add(Page.AddScriptTagOptions.class.getName());
        classNames.add(Page.AddStyleTagOptions.class.getName());
        classNames.add(Page.CheckOptions.class.getName());
        classNames.add(Page.ClickOptions.class.getName());
        classNames.add(Page.CloseOptions.class.getName());
        classNames.add(Page.DblclickOptions.class.getName());
        classNames.add(Page.DispatchEventOptions.class.getName());
        classNames.add(Page.DragAndDropOptions.class.getName());
        classNames.add(Page.EmulateMediaOptions.class.getName());
        classNames.add(Page.EvalOnSelectorOptions.class.getName());
        classNames.add(Page.FillOptions.class.getName());
        classNames.add(Page.FocusOptions.class.getName());
        classNames.add(Page.GetAttributeOptions.class.getName());
        classNames.add(Page.GetByAltTextOptions.class.getName());
        classNames.add(Page.GetByLabelOptions.class.getName());
        classNames.add(Page.GetByPlaceholderOptions.class.getName());
        classNames.add(Page.GetByRoleOptions.class.getName());
        classNames.add(Page.GetByTextOptions.class.getName());
        classNames.add(Page.GetByTitleOptions.class.getName());
        classNames.add(Page.GoBackOptions.class.getName());
        classNames.add(Page.GoForwardOptions.class.getName());
        classNames.add(Page.NavigateOptions.class.getName());
        classNames.add(Page.HoverOptions.class.getName());
        classNames.add(Page.InnerHTMLOptions.class.getName());
        classNames.add(Page.InnerTextOptions.class.getName());
        classNames.add(Page.InputValueOptions.class.getName());
        classNames.add(Page.IsCheckedOptions.class.getName());
        classNames.add(Page.IsDisabledOptions.class.getName());
        classNames.add(Page.IsEditableOptions.class.getName());
        classNames.add(Page.IsEnabledOptions.class.getName());
        classNames.add(Page.IsHiddenOptions.class.getName());
        classNames.add(Page.IsVisibleOptions.class.getName());
        classNames.add(Page.ConsoleMessagesOptions.class.getName());
        classNames.add(Page.LocatorOptions.class.getName());
        classNames.add(Page.PdfOptions.class.getName());
        classNames.add(Page.PressOptions.class.getName());
        classNames.add(Page.QuerySelectorOptions.class.getName());
        classNames.add(Page.AddLocatorHandlerOptions.class.getName());
        classNames.add(Page.ReloadOptions.class.getName());
        classNames.add(Page.RouteOptions.class.getName());
        classNames.add(Page.RouteFromHAROptions.class.getName());
        classNames.add(Page.ScreenshotOptions.class.getName());
        classNames.add(Page.SelectOptionOptions.class.getName());
        classNames.add(Page.SetCheckedOptions.class.getName());
        classNames.add(Page.SetContentOptions.class.getName());
        classNames.add(Page.SetInputFilesOptions.class.getName());
        classNames.add(Page.AriaSnapshotOptions.class.getName());
        classNames.add(Page.TapOptions.class.getName());
        classNames.add(Page.TextContentOptions.class.getName());
        classNames.add(Page.TypeOptions.class.getName());
        classNames.add(Page.UncheckOptions.class.getName());
        classNames.add(Page.WaitForCloseOptions.class.getName());
        classNames.add(Page.WaitForConsoleMessageOptions.class.getName());
        classNames.add(Page.WaitForDownloadOptions.class.getName());
        classNames.add(Page.WaitForFileChooserOptions.class.getName());
        classNames.add(Page.WaitForFunctionOptions.class.getName());
        classNames.add(Page.WaitForLoadStateOptions.class.getName());
        classNames.add(Page.WaitForNavigationOptions.class.getName());
        classNames.add(Page.WaitForPopupOptions.class.getName());
        classNames.add(Page.WaitForRequestOptions.class.getName());
        classNames.add(Page.WaitForRequestFinishedOptions.class.getName());
        classNames.add(Page.WaitForResponseOptions.class.getName());
        classNames.add(Page.WaitForSelectorOptions.class.getName());
        classNames.add(Page.WaitForConditionOptions.class.getName());
        classNames.add(Page.WaitForURLOptions.class.getName());
        classNames.add(Page.WaitForWebSocketOptions.class.getName());
        classNames.add(Page.WaitForWorkerOptions.class.getName());
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
                .getAllKnownImplementations(DotName.createSimple(className))
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