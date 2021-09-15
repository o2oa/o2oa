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
package com.x.jpush.assemble.control.huawei.android;

import com.x.jpush.assemble.control.huawei.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;

public class ClickAction {
    private static final String PATTERN = "^https.*";

    private Integer type;

    private String intent;

    private String url;

    private String rich_resource;

    private String action;

    private ClickAction(Builder builder) {
        this.type = builder.type;
        switch (this.type) {
            case 1:
                this.intent = builder.intent;
                this.action = builder.action;
                break;
            case 2:
                this.url = builder.url;
                break;
            case 4:
                this.rich_resource = builder.richResource;
                break;
        }
    }

    /**
     * check clickAction's parameters
     */
    public void check() {
        boolean isTrue = this.type == 1 ||
                this.type == 2 ||
                this.type == 3 ||
                this.type == 4;
        ValidatorUtils.checkArgument(isTrue, "click type should be one of 1: customize action, 2: open url, 3: open app, 4: open rich media");

        switch (this.type) {
            case 1:
                ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.intent) || StringUtils.isNotEmpty(this.action), "intent or action is required when click type=1");
                break;
            case 2:
                ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.url), "url is required when click type=2");
                ValidatorUtils.checkArgument(this.url.matches(PATTERN), "url must start with https");
                break;
            case 4:
                ValidatorUtils.checkArgument(StringUtils.isNotEmpty(this.rich_resource), "richResource is required when click type=4");
                ValidatorUtils.checkArgument(this.rich_resource.matches(PATTERN), "richResource must start with https");
                break;
        }
    }

    /**
     * getter
     */
    public int getType() {
        return type;
    }

    public String getIntent() {
        return intent;
    }

    public String getUrl() {
        return url;
    }

    public String getRich_resource() {
        return rich_resource;
    }

    public String getAction() {
        return action;
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer type;
        private String intent;
        private String url;
        private String richResource;
        private String action;

        private Builder() {
        }

        public Builder setType(Integer type) {
            this.type = type;
            return this;
        }

        public Builder setIntent(String intent) {
            this.intent = intent;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setRichResource(String richResource) {
            this.richResource = richResource;
            return this;
        }

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public ClickAction build() {
            return new ClickAction(this);
        }
    }
}
