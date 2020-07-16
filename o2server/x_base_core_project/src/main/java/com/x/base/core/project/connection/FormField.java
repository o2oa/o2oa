package com.x.base.core.project.connection;

import java.util.Objects;

import com.x.base.core.project.gson.GsonPropertyObject;

public class FormField extends GsonPropertyObject {

    public FormField(String name, Object value) {
        this.name = name;
        this.value = Objects.toString(value, "");
    }

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}