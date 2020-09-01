package com.x.program.center.jaxrs.module;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;

abstract class BaseAction extends StandardJaxrsAction {

	public static class CacheObject {

		private WrapModule module;

		public WrapModule getModule() {
			return module;
		}

		public void setModule(WrapModule module) {
			this.module = module;
		}

	}

}
