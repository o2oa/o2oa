package com.x.query.core.express.index;

import java.util.Objects;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.index.WoField")
public class WoField extends GsonPropertyObject {

    private static final long serialVersionUID = 1L;

    public WoField() {
        // nothing
    }

    public WoField(String field, String name, String fieldType) {
        this.field = field;
        this.name = name;
        this.fieldType = fieldType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, fieldType, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WoField other = (WoField) obj;
        return Objects.equals(field, other.field) && Objects.equals(fieldType, other.fieldType)
                && Objects.equals(name, other.name);
    }

    @FieldDescribe("字段标识.")
    @Schema(description = "字段标识.")
    private String field;

    @FieldDescribe("字段名称.")
    @Schema(description = "字段名称.")
    private String name;

    @FieldDescribe("字段类型.")
    @Schema(description = "字段类型.")
    private String fieldType;

    @FieldDescribe("下限值.")
    @Schema(description = "下限值.")
    private Object min;

    @FieldDescribe("上限值.")
    @Schema(description = "上限值.")
    private Object max;

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

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }
}
