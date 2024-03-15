package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;

class ActionUploadWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadWithWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workCompletedId, String site, String fileName,
			byte[] bytes, FormDataContentDisposition disposition, String extraParam) throws Exception {

		LOGGER.debug("execute:{}, workCompletedId:{}, site:{}, filename:{}.", effectivePerson.getDistinguishedName(),
				workCompletedId, site, fileName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			/** 判断work是否存在 */
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(workCompletedId, WorkCompleted.class);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
			}

			/* 用于标识状态的结束节点 */
			List<End> ends = business.end().listWithProcess(process);
			if (ends.isEmpty()) {
				throw new ExceptionEndNotExist(process.getId());
			}

			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowManage().build();

			if (BooleanUtils.isFalse(control.getAllowManage())) {
				throw new ExceptionAccessDenied(effectivePerson, workCompleted);
			}

			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			/* 调整可能的附件名称 */
			fileName = this.adjustFileName(business, workCompleted.getJob(), fileName);

			this.verifyConstraint(bytes.length, fileName, null);

//			/* 天印扩展 */
//			if (StringUtils.isNotEmpty(extraParam)) {
//				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
//				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
//					fileName = wiExtraParam.getFileName();
//				}
//				if (StringUtils.isNotEmpty(wiExtraParam.getSite())) {
//					site = wiExtraParam.getSite();
//				}
//			}
			/** 禁止不带扩展名的文件上传 */
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(workCompleted, effectivePerson, site, ends.get(0));
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	private Attachment concreteAttachment(WorkCompleted workCompleted, EffectivePerson effectivePerson, String site,
			End end) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(true);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
		/** 用于判断目录的值 */
		attachment.setWorkCreateTime(workCompleted.getCreateTime());
		attachment.setApplication(workCompleted.getApplication());
		attachment.setProcess(workCompleted.getProcess());
		attachment.setJob(workCompleted.getJob());
		attachment.setActivity(end.getId());
		attachment.setActivityName(end.getName());
		attachment.setActivityToken(end.getId());
		attachment.setActivityType(end.getActivityType());
		return attachment;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 2980101927460641628L;

	}

}
