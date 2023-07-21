package com.x.message.assemble.communicate.jaxrs.ws;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import io.swagger.v3.oas.annotations.media.Schema;

abstract class BaseAction extends StandardJaxrsAction {

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.BaseAction$WoPerson")
    protected class WoPerson extends GsonPropertyObject {

        protected WoPerson() {
            //nothing
        }

        protected WoPerson(String person) {
            this.person = person;
        }

        private String person;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

    }

}