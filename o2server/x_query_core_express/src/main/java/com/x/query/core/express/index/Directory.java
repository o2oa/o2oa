package com.x.query.core.express.index;

import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index$Directory")
public class Directory {

    @FieldDescribe("分类.")
    @Schema(description = "分类.")
    private String category;

    @FieldDescribe("标识.")
    @Schema(description = "标识.")
    private String key;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}