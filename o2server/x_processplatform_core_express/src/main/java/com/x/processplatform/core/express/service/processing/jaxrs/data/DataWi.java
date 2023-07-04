package com.x.processplatform.core.express.service.processing.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * @author sword
 * @date 2023/07/03 13:41
 **/
public class DataWi extends GsonPropertyObject {

    @FieldDescribe("操作数据保存的用户")
    private String operator;
    @FieldDescribe("业务数据")
    private JsonElement jsonElement;

    @FieldDescribe("应用")
    private String application;
    @FieldDescribe("流程")
    private String process;
    @FieldDescribe("工作")
    private String job;
    private String activity;
    private String activityName;


    public DataWi(){}

    public DataWi(String operator, JsonElement jsonElement){
        this.operator = operator;
        this.jsonElement = jsonElement;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }

    public void setJsonElement(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
}
