package com.x.program.center.jaxrs.market;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.WrapModule;

abstract class BaseAction extends StandardJaxrsAction {

	public boolean hasAuth(EffectivePerson effectivePerson, String person){
		if(effectivePerson.isManager()){
			return true;
		}
		if(effectivePerson.getDistinguishedName().equals(person)){
			return true;
		}
		return false;
	}

	public static class InstallData extends GsonPropertyObject {
		private WrapModule WrapModule;

		private String staticResource;

		private String customApp;

		public com.x.program.center.WrapModule getWrapModule() {
			return WrapModule;
		}

		public void setWrapModule(com.x.program.center.WrapModule wrapModule) {
			WrapModule = wrapModule;
		}

		public String getStaticResource() {
			return staticResource;
		}

		public void setStaticResource(String staticResource) {
			this.staticResource = staticResource;
		}

		public String getCustomApp() {
			return customApp;
		}

		public void setCustomApp(String customApp) {
			this.customApp = customApp;
		}
	}

}
