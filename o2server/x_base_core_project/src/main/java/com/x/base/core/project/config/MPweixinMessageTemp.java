package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;

/**
 * Created by fancyLou on 3/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class MPweixinMessageTemp extends ConfigObject  {


    @FieldDescribe("流程字段名")
    private String name;
    @FieldDescribe("模版字段名")
    private String tempName = "";

    public static MPweixinMessageTemp defaultInstance() { return new MPweixinMessageTemp(); }

    public MPweixinMessageTemp() {
        this.name = "";
        this.tempName = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }
}
