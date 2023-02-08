package com.x.query.core.express.index;

import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index$Directory")
public class Directory {

    @FieldDescribe("分类.")
    @Schema(description = "分类.")
    private String category;

    @FieldDescribe("名称.")
    @Schema(description = "名称.")
    private String name;

    @FieldDescribe("标识.")
    @Schema(description = "标识.")
    private String key;

    public Directory() {
    }

    public Directory(String category, String name, String key) {
        this.category = category;
        this.name = name;
        this.key = key;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}