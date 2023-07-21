package com.x.query.core.express.index;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.Sort")
public class Sort extends GsonPropertyObject {

    private static final long serialVersionUID = 8404002412785606829L;

    public static final String ORDER_DESC = "desc";
    public static final String ORDER_ASC = "asc";

    @FieldDescribe("字段.")
    @Schema(description = "字段.")
    String field;

    @FieldDescribe("排序顺序,asc顺序,desc倒序.")
    @Schema(description = "排序顺序,asc顺序,desc倒序.")
    String order;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

}