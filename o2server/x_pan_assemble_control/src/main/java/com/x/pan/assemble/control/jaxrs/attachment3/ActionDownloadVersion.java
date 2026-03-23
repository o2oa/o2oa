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
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.AttachmentVersion;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.StreamingOutput;

class ActionDownloadVersion extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionDownloadVersion.class );

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer version) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			String originFileId, fileName;
			Integer fileVersion;
			Long updateTime;
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				Attachment2 attachment2 = emc.find(id, Attachment2.class);
				if(attachment2 == null) {
					throw new ExceptionAttachmentNotExist(id);
				}else{
					if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
					originFileId = attachment2.getOriginFile();
					fileVersion = attachment2.getFileVersion();
					fileName = attachment2.getName();
					updateTime = attachment2.getUpdateTime().getTime();
				}
			}else{
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
				if(!business.zoneReadable(effectivePerson, zoneId)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
				originFileId = attachment.getOriginFile();
				fileVersion = attachment.getFileVersion();
				fileName = attachment.getName();
				updateTime = attachment.getUpdateTime().getTime();
			}

			if(!version.equals(fileVersion)) {
				AttachmentVersion attachmentVersion = business.attachment3().getVersion(id, version);
				if (attachmentVersion != null) {
					originFileId = attachmentVersion.getOriginFile();
				}
			}

			OriginFile originFile = emc.find(originFileId, OriginFile.class);
			if (null == originFile) {
				throw new ExceptionAttachmentNotExist(id, originFileId);
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
					logger.warn("{}附件下载异常：{}", fileName, e.getMessage());
				}
			};
			String fastETag = id+version+updateTime;
			wo = new Wo(streamingOutput, this.contentType(false, fileName),
					this.contentDisposition(false, fileName),
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
