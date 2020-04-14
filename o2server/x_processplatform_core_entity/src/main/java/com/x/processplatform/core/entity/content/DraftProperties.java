package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;

public class DraftProperties extends JsonProperties {

    private Data data = new Data();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
