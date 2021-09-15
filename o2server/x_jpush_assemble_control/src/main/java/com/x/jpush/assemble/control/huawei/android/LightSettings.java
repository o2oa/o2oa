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

public class LightSettings {
    private static final String LIGTH_DURATION_PATTERN = "\\d+|\\d+[sS]|\\d+.\\d{1,9}|\\d+.\\d{1,9}[sS]";

    private Color color;

    private String light_on_duration;

    private String light_off_duration;

    public LightSettings(Builder builder) {
        this.color = builder.color;
        this.light_on_duration = builder.lightOnDuration;
        this.light_off_duration = builder.lightOffDuration;
    }

    public Color getColor() {
        return color;
    }

    public String getLight_on_duration() {
        return light_on_duration;
    }

    public String getLight_off_duration() {
        return light_off_duration;
    }

    /**
     * 参数校验
     */
    public void check() {
        ValidatorUtils.checkArgument(this.color != null, "color must be selected when light_settings is set");

        if (this.color != null) {
            this.color.check();
        }

        ValidatorUtils.checkArgument(this.light_on_duration != null, "light_on_duration must be selected when light_settings is set");

        ValidatorUtils.checkArgument(this.light_off_duration != null, "light_off_duration must be selected when light_settings is set");

        ValidatorUtils.checkArgument(this.light_on_duration.matches(LIGTH_DURATION_PATTERN), "light_on_duration format is wrong");

        ValidatorUtils.checkArgument(this.light_off_duration.matches(LIGTH_DURATION_PATTERN), "light_off_duration format is wrong");
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Color color;
        private String lightOnDuration;
        private String lightOffDuration;

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder setLightOnDuration(String lightOnDuration) {
            this.lightOnDuration = lightOnDuration;
            return this;
        }

        public Builder setLightOffDuration(String lightOffDuration) {
            this.lightOffDuration = lightOffDuration;
            return this;
        }

        public LightSettings build() {
            return new LightSettings(this);
        }
    }
}
