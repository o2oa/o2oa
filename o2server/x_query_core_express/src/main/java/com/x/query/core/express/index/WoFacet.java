package com.x.query.core.express.index;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.ValueCountPair;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.WoFacet")
public class WoFacet extends GsonPropertyObject {

    private static final long serialVersionUID = 6427227486701877864L;

    @FieldDescribe("字段标识.")
    @Schema(description = "字段标识.")
    private String field;

    @FieldDescribe("字段名称.")
    @Schema(description = "字段名称.")
    private String name;

    @FieldDescribe("分类值列表.")
    @Schema(description = "分类值列表.")
    private List<ValueCountPair> valueCountPairList = new ArrayList<>();

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValueCountPair> getValueCountPairList() {
        return valueCountPairList;
    }

    public void setValueCountPairList(List<ValueCountPair> valueCountPairList) {
        this.valueCountPairList = valueCountPairList;
    }

}