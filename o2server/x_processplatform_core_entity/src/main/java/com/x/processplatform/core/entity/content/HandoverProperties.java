package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

/**
 * @author sword
 */
public class HandoverProperties extends JsonProperties {

    @FieldDescribe("应用列表.")
    private List<String> applicationList;

    @FieldDescribe("流程列表.")
    private List<String> processList;

    @FieldDescribe("工作列表.")
    private List<String> jobList;

    public List<String> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<String> applicationList) {
        this.applicationList = applicationList;
    }

    public List<String> getProcessList() {
        return processList;
    }

    public void setProcessList(List<String> processList) {
        this.processList = processList;
    }

    public List<String> getJobList() {
        return jobList;
    }

    public void setJobList(List<String> jobList) {
        this.jobList = jobList;
    }
}
