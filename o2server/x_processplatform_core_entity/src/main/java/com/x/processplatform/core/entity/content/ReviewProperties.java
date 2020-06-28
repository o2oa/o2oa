package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class ReviewProperties extends JsonProperties {

    @FieldDescribe("标题")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
