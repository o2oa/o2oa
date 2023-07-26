package com.x.processplatform.core.express.service.processing.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

/**
 * @author sword
 * @date 2023/07/03 13:41
 **/
public class DataWi extends GsonPropertyObject {

    @FieldDescribe("操作数据保存的用户")
    private String operator;

    @FieldDescribe("变更业务数据")
    private JsonElement jsonElement;

    @FieldDescribe("所有业务数据")
    private JsonElement data;

    @FieldDescribe("应用")
    private String application;

    @FieldDescribe("流程")
    private String process;

    @FieldDescribe("工作")
    private String job;

    @FieldDescribe("节点")
    private String activity;

    @FieldDescribe("节点名称")
    private String activityName;

    @FieldDescribe("是否删除操作")
    private Boolean deleted = false;


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

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public JsonElement getData() {
        return data;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public void init(Work work){
        this.activity = work.getActivity();
        this.activityName = work.getActivityName();
        this.job = work.getJob();
        this.application = work.getApplication();
        this.process = work.getProcess();
    }

    public void init(Work work, JsonElement data){
        this.init(work);
        this.data = data;
    }

    public void init(WorkCompleted work){
        this.activity = work.getActivity();
        this.activityName = work.getActivityName();
        this.job = work.getJob();
        this.application = work.getApplication();
        this.process = work.getProcess();
    }

    public void init(WorkCompleted work, JsonElement data){
        this.init(work);
        this.data = data;
    }
}
