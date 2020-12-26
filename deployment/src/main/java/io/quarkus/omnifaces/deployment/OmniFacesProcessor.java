package io.quarkus.omnifaces.deployment;

import java.io.IOException;

import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.cdi.converter.ConverterManager;
import org.omnifaces.cdi.eager.EagerBeansRepository;
import org.omnifaces.cdi.validator.ValidatorManager;
import org.omnifaces.cdi.viewscope.ViewScopeManager;
import org.omnifaces.resourcehandler.CombinedResourceHandler;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.deployment.ContextRegistrarBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.omnifaces.runtime.scopes.OmniFacesQuarkusViewScope;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.configuration.ProfileManager;
import io.quarkus.undertow.deployment.ListenerBuildItem;
import io.quarkus.undertow.deployment.ServletBuildItem;
import io.quarkus.undertow.deployment.ServletInitParamBuildItem;

public class OmniFacesProcessor {

    private static final Class[] BEAN_CLASSES = {
            EagerBeansRepository.class,
            ValidatorManager.class,
            ViewScopeManager.class,
            ConverterManager.class
    };

    @BuildStep
    void buildFeature(BuildProducer<FeatureBuildItem> feature) throws IOException {
        feature.produce(new FeatureBuildItem("omnifaces"));
    }

    @BuildStep
    void buildCdiBeans(BuildProducer<FeatureBuildItem> feature,
            BuildProducer<ServletBuildItem> servlet,
            BuildProducer<ListenerBuildItem> listener,
            BuildProducer<AdditionalBeanBuildItem> additionalBean,
            BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotation,
            BuildProducer<ContextRegistrarBuildItem> contextRegistrar) throws IOException {

        for (Class<?> clazz : BEAN_CLASSES) {
            additionalBean.produce(AdditionalBeanBuildItem.unremovableOf(clazz));
        }
    }

    @BuildStep
    void buildCdiScopes(BuildProducer<ContextRegistrarBuildItem> contextRegistrar) throws IOException {

        contextRegistrar.produce(new ContextRegistrarBuildItem(registrationContext -> registrationContext
                .configure(ViewScoped.class).normal().contextClass(OmniFacesQuarkusViewScope.class).done(), ViewScoped.class));
    }

    @BuildStep
    void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            CombinedIndexBuildItem combinedIndex) {

        //most of the classes registered for reflection below are used in OmniFaces functions (omnifaces-functions.taglib.xml)
        //myfaces (org.apache.myfaces.view.facelets.compiler.TagLibraryConfig.create) uses reflection to register facelets functions
        reflectiveClass.produce(new ReflectiveClassBuildItem(false, false, "java.util.Set",
                "java.util.List", "java.lang.Iterable", "java.util.Collection", "java.lang.Throwable", "java.util.Date",
                "java.util.Calendar", "java.time.LocalDate", "java.time.LocalDateTime", "java.lang.Integer",
                "java.lang.Long", "java.lang.Double", "java.lang.String", "java.lang.Number"));

        reflectiveClass.produce(new ReflectiveClassBuildItem(true, false,
                "org.omnifaces.el.functions.Strings", "org.omnifaces.el.functions.Arrays",
                "org.omnifaces.el.functions.Components", "org.omnifaces.el.functions.Dates",
                "org.omnifaces.el.functions.Numbers", "org.omnifaces.el.functions.Objects",
                "org.omnifaces.el.functions.Converters", "org.omnifaces.util.Faces", "org.primefaces.util.ComponentUtils",
                "org.apache.myfaces.renderkit.html.HtmlResponseStateManager", "org.primefaces.extensions.util.ComponentUtils"));
    }

    @BuildStep
    void buildRecommendedInitParams(BuildProducer<ServletInitParamBuildItem> initParam) throws IOException {

        //disables combined resource handler in dev mode
        if (LaunchMode.DEVELOPMENT.getDefaultProfile().equals(ProfileManager.getActiveProfile())) {
            initParam.produce(new ServletInitParamBuildItem(CombinedResourceHandler.PARAM_NAME_DISABLED, "true"));
        }
    }

}
