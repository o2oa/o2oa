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
import com.x.jpush.assemble.control.huawei.CollectionUtils;
import com.x.jpush.assemble.control.huawei.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Alert {
    @SerializedName( "title")
    private String title;

    @SerializedName( "body")
    private String body;

    @SerializedName( "title-loc-key")
    private String titleLocKey;

    @SerializedName( "title-loc-args")
    private List<String> titleLocArgs = new ArrayList<String>();

    @SerializedName( "action-loc-key")
    private String actionLocKey;

    @SerializedName( "loc-key")
    private String locKey;

    @SerializedName( "loc-args")
    private List<String> locArgs = new ArrayList<String>();

    @SerializedName( "launch-image")
    private String launchImage;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTitleLocKey() {
        return titleLocKey;
    }

    public List<String> getTitleLocArgs() {
        return titleLocArgs;
    }

    public String getActionLocKey() {
        return actionLocKey;
    }

    public String getLocKey() {
        return locKey;
    }

    public List<String> getLocArgs() {
        return locArgs;
    }

    public String getLaunchImage() {
        return launchImage;
    }

    private Alert(Builder builder) {
        this.title = builder.title;
        this.body = builder.body;
        this.titleLocKey = builder.titleLocKey;
        if (!CollectionUtils.isEmpty(builder.titleLocArgs)) {
            this.titleLocArgs.addAll(builder.titleLocArgs);
        } else {
            this.titleLocArgs = null;
        }
        this.actionLocKey = builder.actionLocKey;
        this.locKey = builder.locKey;
        if (!CollectionUtils.isEmpty(builder.locArgs)) {
            this.locArgs.addAll(builder.locArgs);
        } else {
            this.locArgs = null;
        }
        this.launchImage = builder.launchImage;
    }

    public void check() {
        if (!CollectionUtils.isEmpty(this.locArgs)) {
            ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.locKey), "locKey is required when specifying locArgs");
        }
        if (!CollectionUtils.isEmpty(this.titleLocArgs)) {
            ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.titleLocKey), "titleLocKey is required when specifying titleLocArgs");
        }
    }

    /**
     * builder
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String body;
        private String titleLocKey;
        private List<String> titleLocArgs = new ArrayList<String>();
        private String actionLocKey;
        private String locKey;
        private List<String> locArgs = new ArrayList<String>();
        private String launchImage;

        public Builder setTitle(String title) {
            this.title = title;
            return this;

        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setTitleLocKey(String titleLocKey) {
            this.titleLocKey = titleLocKey;
            return this;
        }

        public Builder setAddAllTitleLocArgs(List<String> titleLocArgs) {
            this.titleLocArgs.addAll(titleLocArgs);
            return this;
        }

        public Builder setAddTitleLocArg(String titleLocArg) {
            this.titleLocArgs.add(titleLocArg);
            return this;
        }

        public Builder setActionLocKey(String actionLocKey) {
            this.actionLocKey = actionLocKey;
            return this;
        }

        public Builder setLocKey(String locKey) {
            this.locKey = locKey;
            return this;
        }

        public Builder AddAllLocArgs(List<String> locArgs) {
            this.locArgs.addAll(locArgs);
            return this;
        }

        public Builder AddLocArg(String locArg) {
            this.locArgs.add(locArg);
            return this;
        }

        public Builder setLaunchImage(String launchImage) {
            this.launchImage = launchImage;
            return this;
        }

        public Alert build() {
            return new Alert(this);
        }
    }
}
