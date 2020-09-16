package com.x.program.center.jaxrs.module;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;

abstract class BaseAction extends StandardJaxrsAction {

	public static class CacheObject extends GsonPropertyObject {

		private static final long serialVersionUID = -670193466778959483L;

		private WrapModule module;

		public WrapModule getModule() {
			return module;
		}

		public void setModule(WrapModule module) {
			this.module = module;
		}

	}

}
