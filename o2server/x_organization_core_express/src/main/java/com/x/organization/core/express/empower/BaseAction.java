package com.x.organization.core.express.empower;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

abstract class BaseAction {

	protected static Class<?> applicationClass = x_organization_assemble_express.class;

	static class WoAbstract extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("应用")
		private String application;

		@FieldDescribe("流程版本")
		private String edition;

		@FieldDescribe("流程")
		private String process;

		@FieldDescribe("工作")
		private String work;

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

		public String getEdition() {
			return edition;
		}

		public void setEdition(String edition) {
			this.edition = edition;
		}

	}

}
