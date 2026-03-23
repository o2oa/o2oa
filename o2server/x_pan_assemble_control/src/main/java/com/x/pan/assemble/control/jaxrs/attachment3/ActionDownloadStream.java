package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.core.entity.open.OriginFile;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.StreamingOutput;

class ActionDownloadStream extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionDownloadStream.class );

	ActionResult<Wo> execute(HttpServletResponse response, EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */

			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
			if(!business.zoneReadable(effectivePerson, zoneId)){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			OriginFile originFile = emc.find(attachment.getOriginFile(), OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id,attachment.getOriginFile());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
					originFile.getStorage());
			if (null == mapping) {
				throw new ExceptionStorageNotExist(originFile.getStorage());
			}
			StreamingOutput streamingOutput = output -> {
				try {
					originFile.readContent(mapping, output);
					output.flush();
				} catch (Exception e) {
					logger.warn("{}附件下载异常：{}", attachment.getName(), e.getMessage());
				}
			};
			String fastETag = attachment.getId()+attachment.getUpdateTime().getTime();
			wo = new Wo(streamingOutput, this.contentType(true, attachment.getName()),
					this.contentDisposition(true, attachment.getName()),
					originFile.getLength(), fastETag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}
}
