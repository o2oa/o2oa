package com.x.strategydeploy.assemble.control.attachment;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.ThisApplication;
import com.x.strategydeploy.core.entity.Attachment;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionUpload {
	private static Logger logger = LoggerFactory.getLogger(ActionUpload.class);

	public static class Wo extends WoId {

	}

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean isexist = true;
			boolean ispass = true;
			isexist = business.keyworkInfoFactory().IsExistById(workId);
			if (!isexist) {
				Exception exception = new Exception("id: " + workId + " is not Exist.");
				result.error(exception);
				ispass = false;
			}
			if (ispass) {
				KeyworkInfo keywork = business.keyworkInfoFactory().getById(workId);
				String fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
				/** 禁止不带扩展名的文件上传 */
				if (StringUtils.isEmpty(fileName)) {
					throw new ExceptionEmptyExtension(fileName);
				} 
				StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
				Attachment attachment = this.concreteAttachment(keywork, effectivePerson, site);
				attachment.saveContent(mapping, bytes, fileName);
				emc.beginTransaction(KeyworkInfo.class);
				emc.beginTransaction(Attachment.class);
				emc.persist(attachment, CheckPersistType.all);
				keywork.getAttachmentList().add(attachment.getId());
				emc.commit();
				Wo wo = new Wo();
				wo.setId(attachment.getId());
				result.setData(wo);
			}
		} catch (Exception e) {
			result.error(e);
		}
		logger.info(result.getMessage());
		return result;
	}

	private Attachment concreteAttachment(KeyworkInfo work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setWorkyear(work.getKeyworkyear());
		attachment.setSite(site);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		//		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		//		attachment.setSite(site);
		//		/** 用于判断目录的值 */
		//		attachment.setWorkCreateTime(work.getCreateTime());
		//		attachment.setApplication(work.getApplication());
		//		attachment.setProcess(work.getProcess());
		//		attachment.setJob(work.getJob());
		//		attachment.setActivity(work.getActivity());
		//		attachment.setActivityName(work.getActivityName());
		//		attachment.setActivityToken(work.getActivityToken());
		//		attachment.setActivityType(work.getActivityType());
		return attachment;
	}

}
