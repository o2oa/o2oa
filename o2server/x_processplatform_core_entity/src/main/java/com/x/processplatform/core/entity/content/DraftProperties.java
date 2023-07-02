package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;

public class DraftProperties extends JsonProperties {

    private Data data = new Data();

    private String title;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
