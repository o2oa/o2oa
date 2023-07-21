package com.x.query.core.express.assemble.surface.jaxrs.morelikethis;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionPostWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

    @FieldDescribe("标识.")
    @Schema(description = "标识.")
    private String flag;

    @FieldDescribe("分类.")
    @Schema(description = "分类.")
    private String category;

    @FieldDescribe("指定用户.")
    @Schema(description = "指定用户.")
    private String person;

    @FieldDescribe("过滤分类.")
    @Schema(description = "过滤分类.")
    private String filterCategory;

    @FieldDescribe("过滤类型.")
    @Schema(description = "过滤类型.")
    private String filterType;

    @FieldDescribe("过滤目录类型.")
    @Schema(description = "过滤目录类型.")
    private String filterKey;

    @FieldDescribe("返回数量.")
    @Schema(description = "返回数量.")
    private Integer count;

    public String getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterKey() {
        return filterKey;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
