package com.x.program.init.jaxrs.h2;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public static class CheckResult extends GsonPropertyObject {

		private static final long serialVersionUID = -4544008653960661989L;

		private Boolean configured;

		private String version;

		public Boolean getConfigured() {
			return configured;
		}

		public void setConfigured(Boolean configured) {
			this.configured = configured;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

	}

}
