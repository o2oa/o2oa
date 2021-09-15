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

import com.google.gson.annotations.SerializedName;
import com.x.jpush.assemble.control.huawei.ValidatorUtils;

import java.util.Locale;

public class Notification {
    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("image")
    private String image;

    public Notification() {
    }

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Notification(String title, String body, String image) {
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public Notification(Builder builder) {
        this.title = builder.title;
        this.body = builder.body;
        this.image = builder.image;
    }

    public void check() {
        if (this.image != null) {
            ValidatorUtils.checkArgument(this.image.toLowerCase(Locale.getDefault()).trim().startsWith("https"), "image's url should start with HTTPS");
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        private String title;

        private String body;

        private String image;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }

    }
}
