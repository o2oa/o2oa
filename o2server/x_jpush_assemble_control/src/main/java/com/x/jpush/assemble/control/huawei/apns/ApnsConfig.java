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

import java.util.HashMap;
import java.util.Map;

public class ApnsConfig {
    @SerializedName( "hms_options")
    private ApnsHmsOptions hmsOptions;

    @SerializedName( "headers")
    private ApnsHeaders apnsHeaders;

    @SerializedName( "payload")
    private Map<String, Object> payload = new HashMap<>();

    public void check() {
        if (this.hmsOptions != null) {
            this.hmsOptions.check();
        }
        if (this.apnsHeaders != null) {
            this.apnsHeaders.check();
        }
        if (this.payload != null) {
            if (this.payload.get("aps") != null) {
                Aps aps = (Aps) this.payload.get("aps");
                aps.check();
            }
        }
    }

    public ApnsConfig(Builder builder) {
        this.hmsOptions = builder.hmsOptions;
        this.apnsHeaders = builder.apnsHeaders;
        if (!CollectionUtils.isEmpty(builder.payload) || builder.aps != null) {
            if (!CollectionUtils.isEmpty(builder.payload)) {
                this.payload.putAll(builder.payload);
            }
            if (builder.aps != null) {
                this.payload.put("aps", builder.aps);
            }
        } else {
            this.payload = null;
        }
    }

    public ApnsHmsOptions getHmsOptions() {
        return hmsOptions;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public ApnsHeaders getApnsHeaders() {
        return apnsHeaders;
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ApnsHmsOptions hmsOptions;
        private Map<String, Object> payload = new HashMap<>();
        private ApnsHeaders apnsHeaders;
        private Aps aps;

        public Builder setHmsOptions(ApnsHmsOptions hmsOptions) {
            this.hmsOptions = hmsOptions;
            return this;
        }

        public Builder addPayload(String key, Object value) {
            this.payload.put(key, value);
            return this;
        }

        public Builder addAllPayload(Map<String, Object> map) {
            this.payload.putAll(map);
            return this;
        }

        public Builder setApnsHeaders(ApnsHeaders apnsHeaders) {
            this.apnsHeaders = apnsHeaders;
            return this;
        }

        public Builder addPayloadAps(Aps aps) {
            this.aps = aps;
            return this;
        }

        public Builder addPayload(Aps aps) {
            this.aps = aps;
            return this;
        }

        public ApnsConfig build() {
            return new ApnsConfig(this);
        }
    }

}
