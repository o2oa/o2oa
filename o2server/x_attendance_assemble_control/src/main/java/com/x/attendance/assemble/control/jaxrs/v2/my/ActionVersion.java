package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

/**
 * Created by fancyLou on 2023/4/7.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionVersion extends BaseAction {

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        result.setData(new Wo());
        return result;
    }

    public static class Wo extends GsonPropertyObject {
        @FieldDescribe("版本")
        private String version = "2";

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
