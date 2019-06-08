package io.quarkus.omnifaces.view;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class BeanMap {

    private Map<Integer, SampleBean> beans;

    void onStart(@Observes StartupEvent ev) {
        beans = new HashMap<>();
        IntStream.rangeClosed(1, 50)
                .forEach(i -> beans.put(i, new SampleBean(i)));

    }

    @Produces
    @Dependent
    @Named("beans")
    public Map<Integer, SampleBean> produceBeans() {
        return beans;
    }

}
