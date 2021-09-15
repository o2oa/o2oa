/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2029. All rights reserved.
 */

package com.x.jpush.assemble.control.huawei.android;

import com.x.jpush.assemble.control.huawei.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 功能描述
 *
 * @author l00282963
 * @since 2020-01-19
 */
public class Button {
    private String name;

    private Integer action_type;

    private Integer intent_type;

    private String intent;

    private String data;

    public String getName() {
        return name;
    }

    public Integer getAction_type() {
        return action_type;
    }

    public Integer getIntent_type() {
        return intent_type;
    }

    public String getIntent() {
        return intent;
    }

    public String getData() {
        return data;
    }


    public Button(Builder builder) {
        this.name = builder.name;
        this.action_type = builder.actionType;
        this.intent_type = builder.intentType;
        this.intent = builder.intent;
        this.data = builder.data;
    }

    public void check() {
        if (this.action_type != null && this.action_type == 4) {
            ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.data), "data is needed when actionType is 4");
        }
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        private Integer actionType;

        private Integer intentType;

        private String intent;

        private String data;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setActionType(Integer actionType) {
            this.actionType = actionType;
            return this;
        }

        public Builder setIntentType(Integer intentType) {
            this.intentType = intentType;
            return this;
        }

        public Builder setIntent(String intent) {
            this.intent = intent;
            return this;
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Button build() {
            return new Button(this);
        }
    }
}
