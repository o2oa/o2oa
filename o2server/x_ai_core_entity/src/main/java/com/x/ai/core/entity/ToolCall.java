package com.x.ai.core.entity;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * @author chengjian
 * @date 2025/12/12 16:18
 **/
public class ToolCall extends GsonPropertyObject {
    private String name;
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
