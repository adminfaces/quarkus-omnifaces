package io.quarkus.omnifaces.view;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ContextParam;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

@Named
@ViewScoped
public class ViewScopeBean implements Serializable {

    String viewAction;
    String preRenderView;
    Integer id;
    SampleBean sampleBean;

    @Inject
    @ContextParam(name = "javax.faces.PROJECT_STAGE")
    String projectStage;

    @Inject
    @Named("beans")
    Map<Integer, SampleBean> beans;

    public void preRenderView() {
        if (id != null && beans.containsKey(id)) {
            sampleBean = beans.get(id);
        } else {
            sampleBean = new SampleBean();
        }
        preRenderView = "preRenderView was called " + System.currentTimeMillis();
    }

    public void viewAction() {
        viewAction = "viewAction was called " + System.currentTimeMillis();
    }

    public void setViewAction(String viewAction) {
        this.viewAction = viewAction;
    }

    public String getPreRenderView() {
        return preRenderView;
    }

    public void setPreRenderView(String preRenderView) {
        this.preRenderView = preRenderView;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SampleBean getSampleBean() {
        return sampleBean;
    }

    public void setSampleBean(SampleBean sampleBean) {
        this.sampleBean = sampleBean;
    }

    public String getProjectStage() {
        return projectStage;
    }

    public void save() {
        Messages.addInfo(null, "Form saved!");
    }

    public String getViewAction() {
        return viewAction;
    }
}
