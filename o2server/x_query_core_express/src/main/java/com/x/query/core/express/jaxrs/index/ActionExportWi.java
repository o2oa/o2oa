package com.x.query.core.express.jaxrs.index;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.Filter;
import com.x.query.core.express.index.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionExportWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

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
    private String query;

    @FieldDescribe("分页.")
    @Schema(description = "分页.")
    private Integer page;

    @FieldDescribe("数量.")
    @Schema(description = "数量.")
    private Integer size;

    @FieldDescribe("人员.")
    @Schema(description = "人员.")
    private String person;

    @FieldDescribe("显示的固定字段.")
    @Schema(description = "显示的固定字段.")
    List<String> fixedFieldList = new ArrayList<>();

    @FieldDescribe("显示的动态字段.")
    @Schema(description = "显示的动态字段.")
    List<String> dynamicFieldList = new ArrayList<>();

    @FieldDescribe("过滤条件.")
    @Schema(description = "过滤条件.")
    List<Filter> filterList = new ArrayList<>();

    @FieldDescribe("排序字段.")
    @Schema(description = "排序字段.")
    Sort sort;

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public List<String> getFixedFieldList() {
        return null == fixedFieldList ? new ArrayList<>() : this.fixedFieldList;
    }

    public void setFixedFieldList(List<String> fixedFieldList) {
        this.fixedFieldList = fixedFieldList;
    }

    public List<String> getDynamicFieldList() {
        return null == dynamicFieldList ? new ArrayList<>() : this.dynamicFieldList;
    }

    public void setDynamicFieldList(List<String> dynamicFieldList) {
        this.dynamicFieldList = dynamicFieldList;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<Filter> getFilterList() {
        return null == filterList ? new ArrayList<>() : this.filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

}
