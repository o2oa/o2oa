package com.x.query.core.express.assemble.surface.jaxrs.search;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.Filter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.search.ActionPostWi")
public class ActionPostWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

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

    @FieldDescribe("过滤条件.")
    @Schema(description = "过滤条件.")
    List<Filter> filterList = new ArrayList<>();

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

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

}