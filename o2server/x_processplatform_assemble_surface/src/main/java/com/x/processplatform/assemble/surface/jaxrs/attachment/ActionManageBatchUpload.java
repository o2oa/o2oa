package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.ActivityType;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionManageBatchUpload extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageBatchUpload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workIds, String site, String fileName, byte[] bytes,
			FormDataContentDisposition disposition, String extraParam, String person, Integer order) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			// 需要对这个应用的管理权限
			if (!business.canManageApplication(effectivePerson, null)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}

			/* 天谷印章扩展 */
			if (StringUtils.isNotEmpty(extraParam)) {
				WiExtraParam wiExtraParam = gson.fromJson(extraParam, WiExtraParam.class);
				if (StringUtils.isNotEmpty(wiExtraParam.getFileName())) {
					fileName = wiExtraParam.getFileName();
				}
				if (StringUtils.isNotEmpty(wiExtraParam.getSite())) {
					site = wiExtraParam.getSite();
				}
			}

			person = business.organization().person().get(person);
			if(StringUtils.isEmpty(person)){
				person = effectivePerson.getDistinguishedName();
			}
			if(StringUtils.isNotEmpty(workIds) && bytes!=null && bytes.length>0) {
				String[] idArray = workIds.split(",");
				for (String workId : idArray) {
                    Attachment attachment = null;
					Work work = emc.find(workId.trim(), Work.class);
                    if(work!=null) {
						attachment = this.concreteAttachment(work, person, site, order);
                    }else{
                        WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
                        if (null != workCompleted) {
                            attachment = this.concreteAttachment(workCompleted, person, site, order);
                        }
                    }
                    if(attachment!=null){
                        StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
                        attachment.saveContent(mapping, bytes, fileName);
                        attachment.setType((new Tika()).detect(bytes, fileName));
                        if (Config.query().getExtractImage() && ExtractTextTools.supportImage(attachment.getName())
                                && ExtractTextTools.available(bytes)) {
                            attachment.setText(ExtractTextTools.image(bytes));
                        }
                        emc.beginTransaction(Attachment.class);
                        emc.persist(attachment, CheckPersistType.all);
                        emc.commit();
                    }
				}
			}

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private Attachment concreteAttachment(Work work, String person, String site, Integer order) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(person);
		attachment.setLastUpdatePerson(person);
		attachment.setSite(site);
		/** 用于判断目录的值 */
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		if(order!=null){
			attachment.setOrderNumber(order);
		}
		return attachment;
	}

    private Attachment concreteAttachment(WorkCompleted workCompleted, String person, String site, Integer order) throws Exception {
        Attachment attachment = new Attachment();
        attachment.setCompleted(true);
        attachment.setPerson(person);
        attachment.setLastUpdatePerson(person);
        attachment.setSite(site);
        /** 用于判断目录的值 */
        attachment.setWorkCreateTime(workCompleted.getStartTime());
        attachment.setApplication(workCompleted.getApplication());
        attachment.setProcess(workCompleted.getProcess());
        attachment.setJob(workCompleted.getJob());
        attachment.setActivity(workCompleted.getActivity());
        attachment.setActivityName(workCompleted.getActivityName());
        attachment.setActivityToken(workCompleted.getActivity());
        attachment.setActivityType(ActivityType.end);
		if(order!=null){
			attachment.setOrderNumber(order);
		}
        return attachment;
    }

	public static class Wo extends WrapBoolean {

	}

	public static class WoControl extends WorkControl {
	}
}
