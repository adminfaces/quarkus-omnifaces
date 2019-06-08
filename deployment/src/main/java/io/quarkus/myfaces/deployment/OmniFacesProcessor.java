package io.quarkus.myfaces.deployment;

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
import io.quarkus.arc.processor.ContextRegistrar;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.omnifaces.runtime.ServletContextProducer;
import io.quarkus.omnifaces.runtime.scopes.OmniFacesQuarkusViewScope;
import io.quarkus.omnifaces.runtime.startup.OmniFacesServletContextListener;
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

        listener.produce(new ListenerBuildItem(OmniFacesServletContextListener.class.getName()));
        AdditionalBeanBuildItem unremovableProducer = AdditionalBeanBuildItem.unremovableOf(ServletContextProducer.class);
        additionalBean.produce(unremovableProducer);

        for (Class<?> clazz : BEAN_CLASSES) {
            additionalBean.produce(AdditionalBeanBuildItem.unremovableOf(clazz));
        }
    }

    @BuildStep
    void buildCdiScopes(BuildProducer<ContextRegistrarBuildItem> contextRegistrar) throws IOException {

        contextRegistrar.produce(new ContextRegistrarBuildItem(new ContextRegistrar() {
            @Override
            public void register(ContextRegistrar.RegistrationContext registrationContext) {
                registrationContext.configure(ViewScoped.class).normal().contextClass(OmniFacesQuarkusViewScope.class).done();
            }
        }));
    }

    @BuildStep
    void buildRecommendedInitParams(BuildProducer<ServletInitParamBuildItem> initParam) throws IOException {

        //disables combined resource handler in dev mode
        if (LaunchMode.DEVELOPMENT.getDefaultProfile().equals(ProfileManager.getActiveProfile())) {
            initParam.produce(new ServletInitParamBuildItem(CombinedResourceHandler.PARAM_NAME_DISABLED, "true"));
        }
    }

}
