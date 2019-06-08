package io.quarkus.omnifaces.view;

import java.io.Serializable;

public class SampleBean implements Serializable {

    private Integer id;
    private String name;

    public SampleBean() {
    }

    public SampleBean(int i) {
        id = i;
        name = "bean " + i;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
