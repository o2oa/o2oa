package com.x.query.core.express.service.processing.jaxrs.index;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionCountWo extends GsonPropertyObject {

    @FieldDescribe("分类.")
    @Schema(description = "分类.")
    private String category;

    @FieldDescribe("类型.")
    @Schema(description = "类型.")
    private String type;

    @FieldDescribe("标识.")
    @Schema(description = "标识.")
    private String key;

    @FieldDescribe("搜索内容.")
    @Schema(description = "搜索内容.")
    private Long count;

    @FieldDescribe("是否存在.")
    @Schema(description = "是否存在.")
    private Boolean exists;

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}