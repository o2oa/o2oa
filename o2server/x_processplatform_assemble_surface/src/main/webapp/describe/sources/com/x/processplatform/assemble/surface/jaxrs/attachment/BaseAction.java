package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public static class WiExtraParam {
		private String site;

		private String fileName;

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}
}
