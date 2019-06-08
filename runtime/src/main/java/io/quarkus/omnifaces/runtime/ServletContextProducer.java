package io.quarkus.omnifaces.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;

@ApplicationScoped
public class ServletContextProducer {

    private ServletContext servletContext;

    @Produces
    @Dependent
    public ServletContext produce() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
