package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.itextpdf.io.codec.Base64;
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
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.V2UploadWorkOrWorkCompletedBase64Wi;

import io.swagger.v3.oas.annotations.media.Schema;

class V2UploadWorkOrWorkCompletedBase64 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2UploadWorkOrWorkCompletedBase64.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		byte[] bytes = Base64.decode(wi.getContent());

		CompletableFuture<Boolean> checkControlFuture = this.checkControlFuture(effectivePerson, workOrWorkCompleted);

		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Work work = emc.find(workOrWorkCompleted, Work.class);
			WorkCompleted workCompleted = null;
			if (null == work) {
				workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
			}
			if (null != work) {
				wo.setId(uploadWork(business, effectivePerson, work, wi.getSite(), wi.getFileName(), bytes));
			} else if (null != workCompleted) {
				wo.setId(uploadWorkCompleted(business, effectivePerson, workCompleted, wi.getSite(), wi.getFileName(),
						bytes));
			}
		}
		result.setData(wo);
		return result;
	}

	private String uploadWork(Business business, EffectivePerson effectivePerson, Work work, String site,
			String fileName, byte[] bytes) throws Exception {
		Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowSave().build();
		if (BooleanUtils.isNotTrue(control.getAllowSave())) {
			throw new ExceptionAccessDenied(effectivePerson, work);
		}
		this.verifyConstraint(bytes.length, fileName, null);
		Attachment attachment = business.entityManagerContainer().firstEqualAndEqualAndEqual(Attachment.class,
				Attachment.job_FIELDNAME, work.getJob(), Attachment.name_FIELDNAME, fileName, Attachment.site_FIELDNAME,
				site);
		if (null != attachment) {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			attachment.updateContent(mapping, bytes, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes));
			this.updateText(attachment, bytes);
			business.entityManagerContainer().beginTransaction(Attachment.class);
			business.entityManagerContainer().check(attachment, CheckPersistType.all);
			business.entityManagerContainer().commit();
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			attachment = this.concreteAttachmentOfWork(work, effectivePerson, site);
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			this.updateText(attachment, bytes);
			business.entityManagerContainer().beginTransaction(Attachment.class);
			business.entityManagerContainer().persist(attachment, CheckPersistType.all);
			business.entityManagerContainer().commit();
		}
		return attachment.getId();
	}

	private String uploadWorkCompleted(Business business, EffectivePerson effectivePerson, WorkCompleted workCompleted,
			String site, String fileName, byte[] bytes) throws Exception {
		if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson,
				workCompleted.getApplication(), workCompleted.getProcess()))) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		this.verifyConstraint(bytes.length, fileName, null);
		Attachment attachment = business.entityManagerContainer().firstEqualAndEqualAndEqual(Attachment.class,
				Attachment.job_FIELDNAME, workCompleted.getJob(), Attachment.name_FIELDNAME, fileName,
				Attachment.site_FIELDNAME, site);
		if (null != attachment) {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			attachment.updateContent(mapping, bytes, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes));
			this.updateText(attachment, bytes);
			business.entityManagerContainer().beginTransaction(Attachment.class);
			business.entityManagerContainer().check(attachment, CheckPersistType.all);
			business.entityManagerContainer().commit();
		} else {
			// 用于标识状态的结束节点
			Process process = business.process().pick(workCompleted.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
			}
			List<End> ends = business.end().listWithProcess(process);
			if (ends.isEmpty()) {
				throw new ExceptionEndNotExist(process.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			attachment = this.concreteAttachmentOfWorkCompleted(workCompleted, effectivePerson, site, ends.get(0));
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			business.entityManagerContainer().beginTransaction(Attachment.class);
			business.entityManagerContainer().persist(attachment, CheckPersistType.all);
			business.entityManagerContainer().commit();
			return attachment.getId();
		}
		return attachment.getId();
	}

	private void updateText(Attachment attachment, byte[] bytes) throws Exception {
		if (BooleanUtils.isTrue(Config.query().getExtractImage())
				&& BooleanUtils.isTrue(ExtractTextTools.supportImage(attachment.getName()))
				&& ExtractTextTools.available(bytes)) {
			attachment.setText(ExtractTextTools.image(bytes));
		}
	}

	private Attachment concreteAttachmentOfWork(Work work, EffectivePerson effectivePerson, String site) {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
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

	private Attachment concreteAttachmentOfWorkCompleted(WorkCompleted workCompleted, EffectivePerson effectivePerson,
			String site, End end) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(true);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.V2UploadWorkOrWorkCompletedBase64$Wi")
	public static class Wi extends V2UploadWorkOrWorkCompletedBase64Wi {

		private static final long serialVersionUID = -5389058540986817794L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.V2UploadWorkOrWorkCompletedBase64$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 5006428868911776455L;

	}

}