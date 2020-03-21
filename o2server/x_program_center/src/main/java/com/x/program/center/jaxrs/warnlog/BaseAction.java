package com.x.program.center.jaxrs.warnlog;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

    public static Ehcache cacheLog = ApplicationCache.instance().getCache(CacheLogObject.class);

    public static class CacheLogObject extends GsonPropertyObject {

        private String userToken;

        private String node;

        private long lastPoint;

        public long getLastPoint() {
            return lastPoint;
        }

        public void setLastPoint(long lastPoint) {
            this.lastPoint = lastPoint;
        }

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }
    }

}
