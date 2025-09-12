package com.x.query.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author chengjian
 * @date 2025/09/01 16:13
 **/
public class ItemAccessActivity extends JsonProperties {

    private static final long serialVersionUID = -2826601221045816580L;

    @FieldDescribe("活动ID.")
    private String id;

    @FieldDescribe("活动唯一编码.")
    private String unique;

    @FieldDescribe("活动名称.")
    private String name;

    @FieldDescribe("活动别名.")
    private String alias;

    @FieldDescribe("流程标识.")
    private String process;

    @FieldDescribe("活动类型.")
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
