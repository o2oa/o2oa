package com.x.query.core.express.jaxrs.mlt;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionPostWi extends GsonPropertyObject {

    private static final long serialVersionUID = 1360655000630283661L;

    @FieldDescribe("数量.")
    @Schema(description = "数量.")
    private String flag;
    private String category;
    private String person;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
