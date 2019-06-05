package io.quarkus.omnifaces.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;

import org.apache.myfaces.cdi.JsfApplicationArtifactHolder;

@ApplicationScoped
public class ServletContextProducer {

    private ServletContext servletContext;

    @Produces
    @Dependent
    public ServletContext produce() {
        if (servletContext == null) {
            servletContext = CDI.current().select(JsfApplicationArtifactHolder.class).get().getServletContext();
        }
        return servletContext;
    }

}
