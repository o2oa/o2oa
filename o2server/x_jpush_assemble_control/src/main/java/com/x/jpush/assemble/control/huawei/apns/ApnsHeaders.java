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

public class ApnsHeaders {
    private static final String AUTHORIZATION_PATTERN = "^bearer*";

    private static final String APN_ID_PATTERN = "[0-9a-z]{8}(-[0-9a-z]{4}){3}-[0-9a-z]{12}";

    private static final int SEND_IMMEDIATELY = 10;

    private static final int SEND_BY_GROUP = 5;

    @SerializedName( "authorization")
    private String authorization;

    @SerializedName( "apns-id")
    private String apnsId;

    @SerializedName( "apns-expiration")
    private Long apnsExpiration;

    @SerializedName( "apns-priority")
    private String apnsPriority;

    @SerializedName( "apns-topic")
    private String apnsTopic;

    @SerializedName( "apns-collapse-id")
    private String apnsCollapseId;

    public String getAuthorization() {
        return authorization;
    }

    public String getApnsId() {
        return apnsId;
    }

    public Long getApnsExpiration() {
        return apnsExpiration;
    }

    public String getApnsPriority() {
        return apnsPriority;
    }

    public String getApnsTopic() {
        return apnsTopic;
    }

    public String getApnsCollapseId() {
        return apnsCollapseId;
    }

    public void check() {
        if (this.authorization != null) {
            ValidatorUtils.checkArgument(this.authorization.matches(AUTHORIZATION_PATTERN), "authorization must start with bearer");
        }
        if (this.apnsId != null) {
            ValidatorUtils.checkArgument(this.apnsId.matches(APN_ID_PATTERN), "apns-id format error");
        }
        if (this.apnsPriority != null) {
            ValidatorUtils.checkArgument(Integer.parseInt(this.apnsPriority) == SEND_BY_GROUP ||
                    Integer.parseInt(this.apnsPriority) == SEND_IMMEDIATELY, "apns-priority should be SEND_BY_GROUP:5  or SEND_IMMEDIATELY:10");
        }
        if (this.apnsCollapseId != null) {
            ValidatorUtils.checkArgument(this.apnsCollapseId.getBytes().length < 64, "Number of apnsCollapseId bytes should be less than 64");
        }
    }

    private ApnsHeaders(Builder builder) {
        this.authorization = builder.authorization;
        this.apnsId = builder.apnsId;
        this.apnsExpiration = builder.apnsExpiration;
        this.apnsPriority = builder.apnsPriority;
        this.apnsTopic = builder.apnsTopic;
        this.apnsCollapseId = builder.apnsCollapseId;
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String authorization;
        private String apnsId;
        private Long apnsExpiration;
        private String apnsPriority;
        private String apnsTopic;
        private String apnsCollapseId;

        public Builder setAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder setApnsId(String apnsId) {
            this.apnsId = apnsId;
            return this;
        }

        public Builder setApnsExpiration(Long apnsExpiration) {
            this.apnsExpiration = apnsExpiration;
            return this;
        }

        public Builder setApnsPriority(String apnsPriority) {
            this.apnsPriority = apnsPriority;
            return this;
        }

        public Builder setApnsTopic(String apnsTopic) {
            this.apnsTopic = apnsTopic;
            return this;
        }

        public Builder setApnsCollapseId(String apnsCollapseId) {
            this.apnsCollapseId = apnsCollapseId;
            return this;
        }

        public ApnsHeaders build() {
            return new ApnsHeaders(this);
        }
    }
}
