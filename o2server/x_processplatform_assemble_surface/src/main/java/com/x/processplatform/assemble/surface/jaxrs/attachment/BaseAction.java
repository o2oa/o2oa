package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import net.sf.ehcache.Ehcache;

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

	public static Ehcache cachePreviewPdf = ApplicationCache.instance().getCache(PreviewPdfResultObject.class);

	public static class PreviewPdfResultObject extends GsonPropertyObject {

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	public static Ehcache cachePreviewImage = ApplicationCache.instance().getCache(PreviewImageResultObject.class);

	public static class PreviewImageResultObject extends GsonPropertyObject {

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}
}
