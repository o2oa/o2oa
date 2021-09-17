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

public class Color {
    private final float zero = -0.000001f;

    private final float one = 1.000001f;

    private Float alpha = new Float(1.0);

    private Float red = new Float(0.0);

    private Float green = new Float(0.0);

    private Float blue = new Float(0.0);

    public Color(Builder builder) {
        this.alpha = builder.alpha;
        this.red = builder.red;
        this.green = builder.green;
        this.blue = builder.blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public Float getRed() {
        return red;
    }

    public Float getGreen() {
        return green;
    }

    public Float getBlue() {
        return blue;
    }

    public void check() {
        ValidatorUtils.checkArgument(this.alpha > zero && this.alpha < one, "Alpha shoube locate between [0,1]");
        ValidatorUtils.checkArgument(this.red > zero && this.red < one, "Red shoube locate between [0,1]");
        ValidatorUtils.checkArgument(this.green > zero && this.green < one, "Green shoube locate between [0,1]");
        ValidatorUtils.checkArgument(this.blue > zero && this.blue < one, "Blue shoube locate between [0,1]");
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Float alpha = new Float(1.0);
        private Float red = new Float(0.0);
        private Float green = new Float(0.0);
        private Float blue = new Float(0.0);

        public Builder setAlpha(Float alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder setRed(Float red) {
            this.red = red;
            return this;
        }

        public Builder setGreen(Float green) {
            this.green = green;
            return this;
        }

        public Builder setBlue(Float blue) {
            this.blue = blue;
            return this;
        }

        public Color build() {
            return new Color(this);
        }
    }
}
