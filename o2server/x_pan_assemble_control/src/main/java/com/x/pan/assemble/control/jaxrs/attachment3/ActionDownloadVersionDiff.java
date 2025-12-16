package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.AttachmentVersion;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.StreamingOutput;

class ActionDownloadVersionDiff extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger( ActionDownloadVersionDiff.class );

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer version) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = null;
			/** 确定是否要用application/octet-stream输出 */
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				Attachment2 attachment2 = emc.find(id, Attachment2.class);
				if(attachment2 == null) {
					throw new ExceptionAttachmentNotExist(id);
				}else{
					if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
						throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
					}
				}
			}else{
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
				if(!business.zoneReadable(effectivePerson, zoneId)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}

			AttachmentVersion attachmentVersion = business.attachment3().getVersion(id, version);
			if (attachmentVersion == null) {
				throw new ExceptionAttachmentVersionNotExist(id, version);
			}
			if(StringUtils.isNotBlank(attachmentVersion.getFileDiff())){
				final byte[] bytes = Base64.decodeBase64(attachmentVersion.getFileDiff());
				StreamingOutput streamingOutput = output -> {
					try {
						output.write(bytes);
						output.flush();
					} catch (Exception e) {
						logger.warn("{}附件下载异常：{}", id, e.getMessage());
					}
				};
				String fastETag = id + version + "diff" +attachmentVersion.getUpdateTime().getTime();
				String fileName = "diff.zip";
				wo = new Wo(streamingOutput, this.contentType(false, fileName),
						this.contentDisposition(false, fileName),
						Long.valueOf(bytes.length), fastETag);
				result.setData(wo);
			}

			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(StreamingOutput streamingOutput, String contentType, String contentDisposition, Long contentLength, String fastETag) {
			super(streamingOutput, contentType, contentDisposition, contentLength, fastETag);
		}

	}
}
