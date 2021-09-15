/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.x.jpush.assemble.control.huawei.model;

import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import com.google.gson.annotations.SerializedName;
import com.x.jpush.assemble.control.huawei.CollectionUtils;
import com.x.jpush.assemble.control.huawei.ValidatorUtils;
import com.x.jpush.assemble.control.huawei.android.AndroidConfig;
import com.x.jpush.assemble.control.huawei.apns.ApnsConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Message {
    @SerializedName( "data")
    private String data;

    @SerializedName( "notification")
    private Notification notification;

    @SerializedName( "android")
    private AndroidConfig androidConfig;
    @SerializedName( "apns")
    private ApnsConfig apns;

    @SerializedName( "token")
    private List<String> token = new ArrayList<>();

    @SerializedName( "topic")
    private String topic;

    @SerializedName( "condition")
    private String condition;

    private Message(Builder builder) {
        this.data = builder.data;
        this.notification = builder.notification;
        this.androidConfig = builder.androidConfig;
        this.apns = builder.apns;

        if (!CollectionUtils.isEmpty(builder.token)) {
            this.token.addAll(builder.token);
        } else {
            this.token = null;
        }

        this.topic = builder.topic;
        this.condition = builder.condition;

        /** check after message is created */
        check();
    }

    /**
     * check message's parameters
     */
    public void check() {

        int count = Booleans.countTrue(
                !CollectionUtils.isEmpty(this.token),
                !Strings.isNullOrEmpty(this.topic),
                !Strings.isNullOrEmpty(this.condition)
        );

        ValidatorUtils.checkArgument(count == 1, "Exactly one of token, topic or condition must be specified");

        boolean isEmptyData = StringUtils.isEmpty(data);

        if (this.notification != null) {
            this.notification.check();
        }
        if (this.apns != null) {
            this.apns.check();
        }
        if (null != this.androidConfig) {
            this.androidConfig.check(this.notification);
        }

    }

    /**
     * getter
     */
    public String getData() {
        return data;
    }

    public Notification getNotification() {
        return notification;
    }

    public AndroidConfig getAndroidConfig() {
        return androidConfig;
    }

    public ApnsConfig getApns() {
        return apns;
    }

    public List<String> getToken() {
        return token;
    }

    public String getTopic() {
        return topic;
    }

    public String getCondition() {
        return condition;
    }

    /**
     * builder
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * push message builder
     */
    public static class Builder {
        private String data;
        private Notification notification;
        private AndroidConfig androidConfig;
        private ApnsConfig apns;
        private List<String> token = new ArrayList<>();
        private String topic;
        private String condition;

        private Builder() {
        }

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setNotification(Notification notification) {
            this.notification = notification;
            return this;
        }

        public Builder setAndroidConfig(AndroidConfig androidConfig) {
            this.androidConfig = androidConfig;
            return this;
        }

        public Builder setApns(ApnsConfig apns) {
            this.apns = apns;
            return this;
        }

        public Builder addToken(String token) {
            this.token.add(token);
            return this;
        }

        public Builder addAllToken(List<String> tokens) {
            this.token.addAll(tokens);
            return this;
        }

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
