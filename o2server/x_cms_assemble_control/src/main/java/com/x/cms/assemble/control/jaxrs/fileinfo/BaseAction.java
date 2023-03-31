package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.Cms;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import org.apache.commons.io.FilenameUtils;
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

	protected byte[] extensionService(EffectivePerson effectivePerson, String id, Cms.DocExtensionEvent event)
			throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setAttachment(id);
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	/**
	 * 判断附件是否符合大小、文件类型的约束
	 *
	 * @param size
	 * @param fileName
	 * @param callback
	 * @throws Exception
	 */
	protected void verifyConstraint(long size, String fileName, String callback) throws Exception {
		if (Config.general().getAttachmentConfig().getFileSize() != null && Config.general().getAttachmentConfig().getFileSize() > 0) {
			size = size / (1024 * 1024);
			if (size > Config.general().getAttachmentConfig().getFileSize()) {
				if (StringUtils.isNotEmpty(callback)) {
					throw new ExceptionAttachmentInvalidCallback(callback, fileName, Config.general().getAttachmentConfig().getFileSize());
				} else {
					throw new ExceptionAttachmentInvalid(fileName, Config.general().getAttachmentConfig().getFileSize());
				}
			}
		}
		String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
		if ((Config.general().getAttachmentConfig().getFileTypeIncludes() != null && !Config.general().getAttachmentConfig().getFileTypeIncludes().isEmpty())
				&& (!ListTools.contains(Config.general().getAttachmentConfig().getFileTypeIncludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
		if ((Config.general().getAttachmentConfig().getFileTypeExcludes() != null && !Config.general().getAttachmentConfig().getFileTypeExcludes().isEmpty())
				&& (ListTools.contains(Config.general().getAttachmentConfig().getFileTypeExcludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
	}

	public static class Req {

		private String person;
		private String attachment;

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

	}

}
