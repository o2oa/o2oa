package com.x.query.core.express.assemble.surface.jaxrs.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.WoFacet;
import com.x.query.core.express.index.WoField;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionPostWo extends GsonPropertyObject {

    private static final long serialVersionUID = 6427227486701877864L;

    @FieldDescribe("查询耗时.")
    @Schema(description = "查询耗时.")
    private Long queryElapsed;

    @FieldDescribe("开始位置.")
    @Schema(description = "开始位置.")
    private Integer start;

    @FieldDescribe("数量.")
    @Schema(description = "数量.")
    private Integer rows;

    @FieldDescribe("总数.")
    @Schema(description = "总数.")
    private Long count;

    @FieldDescribe("固定字段.")
    @Schema(description = "固定字段.")
    private List<WoField> fixedFieldList = new ArrayList<>();

    @FieldDescribe("动态字段.")
    @Schema(description = "动态字段.")
    private List<WoField> dynamicFieldList = new ArrayList<>();

    @FieldDescribe("维度.")
    @Schema(description = "维度.")
    private List<WoFacet> facetList = new ArrayList<>();

    @FieldDescribe("搜索结果.")
    @Schema(description = "搜索结果.")
    private List<Map<String, Object>> documentList = new ArrayList<>();

    public List<WoFacet> getFacetList() {
        return facetList;
    }

    public void setFacetList(List<WoFacet> facetList) {
        this.facetList = facetList;
    }

    public List<WoField> getFixedFieldList() {
        return fixedFieldList;
    }

    public void setFixedFieldList(List<WoField> fixedFieldList) {
        this.fixedFieldList = fixedFieldList;
    }

    public List<WoField> getDynamicFieldList() {
        return dynamicFieldList;
    }

    public void setDynamicFieldList(List<WoField> dynamicFieldList) {
        this.dynamicFieldList = dynamicFieldList;
    }

    public Long getQueryElapsed() {
        return queryElapsed;
    }

    public void setQueryElapsed(Long queryElapsed) {
        this.queryElapsed = queryElapsed;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<Map<String, Object>> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Map<String, Object>> documentList) {
        this.documentList = documentList;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}