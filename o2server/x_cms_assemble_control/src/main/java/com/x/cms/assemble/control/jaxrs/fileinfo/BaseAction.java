package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.Cms;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.*;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class BaseAction extends StandardJaxrsAction {

	protected static final String SITE_SEPARATOR = "~";
	protected static final String FILE_SEPARATOR = ",";

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileInfo.class, Document.class);
	protected LogService logService = new LogService();
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();

	protected byte[] extensionService(EffectivePerson effectivePerson, FileInfo fileInfo, Cms.DocExtensionEvent event)
			throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setAttachment(fileInfo.getId());
		req.setFileName(fileInfo.getName());
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class,
					fileInfo.getStorage());
			req.setFileBase64(Base64.encodeBase64String(fileInfo.readContent(mapping)));
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	public static class Req {

		private String person;
		private String attachment;
		private String fileName;
		private String fileBase64;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getAttachment() {
			return attachment;
		}

		public void setAttachment(String attachment) {
			this.attachment = attachment;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileBase64() {
			return fileBase64;
		}

		public void setFileBase64(String fileBase64) {
			this.fileBase64 = fileBase64;
		}

	}

}
