package com.x.processplatform.core.express.service.processing.jaxrs.process;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValueCountPair;

/**
 *
 * @author chengjian
 * @date 2026/07/20 13:12
 **/
public class ProcessNameValueCountPair extends NameValueCountPair {

    @FieldDescribe("流程所属应用.")
    private String application;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}

