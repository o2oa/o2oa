package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;

class ActionUploadWithWorkCompleted extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workCompletedId, String site, String fileName,
			byte[] bytes, FormDataContentDisposition disposition, String extraParam) throws Exception {
		System.out.println(">>>>>>>>ActionUploadWithWorkCompleted"  );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 后面要重新保存 */
			WorkCompleted workCompleted = emc.find(workCompletedId, WorkCompleted.class);
			/** 判断work是否存在 */
			if (null == workCompleted) {
				throw new ExceptionWorkCompletedNotExist(workCompletedId);
			}
			Application application = business.application().pick(workCompleted.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(workCompleted.getApplication(), Application.class);
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
			if ((effectivePerson.isNotManager())
					&& (!business.organization().person().hasRole(effectivePerson,
							OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager))
					&& effectivePerson.isNotPerson(application.getControllerList())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
				System.out.println(">>>>>>>>fileName with disposition=" + fileName  );
			}
			/* 天印扩展 */
			if (StringUtils.isNotEmpty(extraParam)) {
				System.out.println(">>>>>>>>extraParam=" + extraParam );
				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
					fileName = wiExtraParam.getFileName();
				}
				if (StringUtils.isNotEmpty(wiExtraParam.getSite())) {
					site = wiExtraParam.getSite();
				}
			}
			/** 禁止不带扩展名的文件上传 */
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(workCompleted, effectivePerson, site, ends.get(0));
			attachment.saveContent(mapping, bytes, fileName);
			attachment.setType((new Tika()).detect(bytes, fileName));
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			// work.getAttachmentList().add(attachment.getId());
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

	}

	public static class WoControl extends WorkControl {
	}
}
