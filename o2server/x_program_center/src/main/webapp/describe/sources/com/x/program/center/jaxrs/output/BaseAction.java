package com.x.program.center.jaxrs.output;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

    protected Ehcache cache = ApplicationCache.instance().getCache(CacheObject.class);

    public static class CacheObject {

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
