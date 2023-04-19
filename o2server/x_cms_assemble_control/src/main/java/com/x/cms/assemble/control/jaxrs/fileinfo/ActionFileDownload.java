package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Cms;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 下载附件
 * @author sword
 */
public class ActionFileDownload extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, String fileName) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			FileInfo fileInfo = emc.find(id, FileInfo.class);
			if (null == fileInfo) {
				throw new ExceptionFileInfoNotExists(id);
			}
			if (StringUtils.isBlank(fileName)) {
				fileName = fileInfo.getName();
			} else {
				String extension = FilenameUtils.getExtension(fileName);
				if (StringUtils.isEmpty(extension)) {
					fileName = fileName + "." + fileInfo.getExtension();
				}
			}
			Document document = emc.find(fileInfo.getDocumentId(), Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(fileInfo.getDocumentId());
			}
			Business business = new Business(emc);
			if (!business.isDocumentReader(effectivePerson, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class, fileInfo.getStorage());
			byte[] bytes;
			Optional<Cms.DocExtensionEvent> event = Config.cms().getExtensionEvents()
					.getDocAttachmentDownloadEvents().bind(document.getAppId(), document.getCategoryId());
			if (event.isPresent()) {
				bytes = this.extensionService(effectivePerson, fileInfo, event.get());
			} else {
				bytes = fileInfo.readContent(mapping);
			}
			Wo wo = new Wo(bytes,
					this.contentType(false, fileName),
					this.contentDisposition(false, fileName));
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}
