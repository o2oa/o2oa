package com.x.query.core.express.jaxrs.mlt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.query.core.express.index.WoFacet;
import com.x.query.core.express.index.WoField;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "com.x.query.core.express.jaxrs.index.ActionPostWo")
public class ActionPostWo extends GsonPropertyObject {

    private String category;
    private String type;
    private String key;
    private String flag;
    private String title;
    private Float score;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

}