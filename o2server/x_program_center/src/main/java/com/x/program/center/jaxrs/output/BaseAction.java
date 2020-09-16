package com.x.program.center.jaxrs.output;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

abstract class BaseAction extends StandardJaxrsAction {

    public static class CacheObject extends GsonPropertyObject {

        private static final long serialVersionUID = 594807992442100468L;

        private WrapServiceModule module;

        private String name;

        public WrapServiceModule getModule() {
            return module;
        }

        public void setModule(WrapServiceModule module) {
            this.module = module;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
