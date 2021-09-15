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
package com.x.jpush.assemble.control.huawei.apns;

import com.google.gson.annotations.SerializedName;
import com.x.jpush.assemble.control.huawei.ValidatorUtils;

public class Aps {
    @SerializedName( "alert")
    private Object alert;

    @SerializedName( "badge")
    private Integer badge;

    @SerializedName( "sound")
    private String sound;

    @SerializedName( "content-available")
    private Integer contentAvailable;

    @SerializedName( "category")
    private String category;

    @SerializedName( "thread-id")
    private String threadId;

    public Object getAlert() {
        return alert;
    }

    public Integer getBadge() {
        return badge;
    }

    public String getSound() {
        return sound;
    }

    public Integer getContentAvailable() {
        return contentAvailable;
    }

    public String getCategory() {
        return category;
    }

    public String getThreadId() {
        return threadId;
    }

    public void check() {
        if (this.alert != null) {
            if(this.alert instanceof Alert){
                ((Alert) this.alert).check();
            }else{
                ValidatorUtils.checkArgument((this.alert instanceof String), "Alter should be Dictionary or String");
            }
        }
    }

    private Aps(Builder builder) {
        this.alert = builder.alert;
        this.badge = builder.badge;
        this.sound = builder.sound;
        this.contentAvailable = builder.contentAvailable;
        this.category = builder.category;
        this.threadId = builder.threadId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Object alert;
        private Integer badge;
        private String sound;
        private Integer contentAvailable;
        private String category;
        private String threadId;

        public Builder setAlert(Object alert) {
            this.alert = alert;
            return this;
        }

        public Builder setBadge(Integer badge) {
            this.badge = badge;
            return this;
        }

        public Builder setSound(String sound) {
            this.sound = sound;
            return this;
        }

        public Builder setContentAvailable(Integer contentAvailable) {
            this.contentAvailable = contentAvailable;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setThreadId(String threadId) {
            this.threadId = threadId;
            return this;
        }

        public Aps build() {
            return new Aps(this);
        }
    }
}
