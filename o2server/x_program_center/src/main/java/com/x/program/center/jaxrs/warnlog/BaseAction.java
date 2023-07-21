package com.x.program.center.jaxrs.warnlog;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

    public static class CacheLogObject extends GsonPropertyObject {

        private String userToken;

        private String node;

        private long lastPoint;

        private long startTime;

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

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }

}
