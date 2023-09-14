package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionUploadWithUrlWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionUploadWithUrl extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadWithUrl.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.debug("ActionUploadWithUrl receive:{}.", jsonElement.toString());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getWorkId())) {
				throw new ExceptionEntityFieldEmpty(Work.class, wi.getWorkId());
			}
			if (StringUtils.isEmpty(wi.getFileName())) {
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileName());
			}
			if (StringUtils.isEmpty(wi.getFileUrl())) {
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileUrl());
			}
			if (StringUtils.isEmpty(wi.getSite())) {
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getSite());
			}
			String person = effectivePerson.getDistinguishedName();
			if (StringUtils.isNotEmpty(wi.getPerson())
					&& business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				Person p = business.organization().person().getObject(wi.getPerson());
				if (p != null) {
					person = p.getDistinguishedName();
				}
			}
			Attachment attachment = null;
			/* 后面要重新保存 */
			Work work = emc.find(wi.getWorkId(), Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				WorkCompleted workCompleted = emc.find(wi.getWorkId(), WorkCompleted.class);
				if (workCompleted != null) {
					Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
							.enableAllowManage().build();
					if (BooleanUtils.isNotTrue(control.getAllowManage())) {
						throw new ExceptionAccessDenied(effectivePerson, workCompleted.getId());
					}
					Process process = business.process().pick(workCompleted.getProcess());
					if (null == process) {
						throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
					}
					List<End> ends = business.end().listWithProcess(process);
					if (ends.isEmpty()) {
						throw new ExceptionEndNotExist(process.getId());
					}
					attachment = this.concreteAttachment(workCompleted, person, wi.getSite(), ends.get(0));
				}
			} else {
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
				if (BooleanUtils.isNotTrue(control.getAllowSave())) {
					throw new ExceptionAccessDenied(effectivePerson, wi.getWorkId());
				}
				attachment = this.concreteAttachment(work, person, wi.getSite());
			}
			if (attachment == null) {
				throw new ExceptionEntityNotExist(wi.getWorkId());
			}
			byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
			if (bytes == null || bytes.length == 0) {
				throw new IllegalStateException("can not down file from url.");
			}
			String fileName = wi.getFileName();
			if (StringUtils.isEmpty(fileName)) {
				throw new IllegalStateException("fileName can not empty.");
			}
			fileName = this.adjustFileName(business, attachment.getJob(), fileName);
			this.verifyConstraint(bytes.length, fileName, null);

			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			if (BooleanUtils.isTrue(Config.query().getExtractImage())
					&& ExtractTextTools.supportImage(attachment.getName()) && ExtractTextTools.available(bytes)) {
				attachment.setText(ExtractTextTools.image(bytes));
			}
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	private Attachment concreteAttachment(Work work, String person, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(person);
		attachment.setLastUpdatePerson(person);
		attachment.setSite(site);
		// 用于判断目录的值
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		return attachment;
	}

	private Attachment concreteAttachment(WorkCompleted workCompleted, String person, String site, End end)
			throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(true);
		attachment.setPerson(person);
		attachment.setLastUpdatePerson(person);
		attachment.setSite(site);
		// 用于判断目录的值
		attachment.setWorkCreateTime(workCompleted.getStartTime());
		attachment.setApplication(workCompleted.getApplication());
		attachment.setProcess(workCompleted.getProcess());
		attachment.setJob(workCompleted.getJob());
		attachment.setActivity(end.getId());
		attachment.setActivityName(end.getName());
		attachment.setActivityToken(end.getId());
		attachment.setActivityType(end.getActivityType());
		return attachment;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionUploadWithUrl$Wi")
	public static class Wi extends ActionUploadWithUrlWi {

		private static final long serialVersionUID = -861366251609881667L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionUploadWithUrl$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5371958038142731930L;

	}

}
