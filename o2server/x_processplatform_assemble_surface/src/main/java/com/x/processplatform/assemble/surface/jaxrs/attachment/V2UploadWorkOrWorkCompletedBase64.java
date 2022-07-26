package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;

class V2UploadWorkOrWorkCompletedBase64 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2UploadWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();

		byte[] bytes = Base64.decodeBase64(wi.getContent());

		CompletableFuture<Boolean> checkControlFuture = this.readableWithWorkOrWorkCompletedFuture(effectivePerson,
				workOrWorkCompleted);

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
		WoControl control = business.getControl(effectivePerson, work, WoControl.class);
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
			attachment.updateContent(mapping, bytes);
			attachment.setType((new Tika()).detect(bytes));
			this.updateText(attachment, bytes);
			business.entityManagerContainer().beginTransaction(Attachment.class);
			business.entityManagerContainer().check(attachment, CheckPersistType.all);
			business.entityManagerContainer().commit();
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			attachment = this.concreteAttachmentOfWork(work, effectivePerson, site);
			attachment.saveContent(mapping, bytes, fileName);
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
		if (BooleanUtils.isNotTrue(business.canManageApplicationOrProcess(effectivePerson,
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
			attachment.updateContent(mapping, bytes);
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
			attachment.saveContent(mapping, bytes, fileName);
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

	protected CompletableFuture<Boolean> readableWithWorkOrWorkCompletedFuture(EffectivePerson effectivePerson,
			String flag) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				value = business.readableWithWorkOrWorkCompleted(effectivePerson, flag,
						new ExceptionEntityNotExist(flag));
			} catch (Exception e) {
				logger.error(e);
			}
			return value;
		}, ThisApplication.threadPool());
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 4127343976931507902L;

		@FieldDescribe("位置.")
		private String site;
		@FieldDescribe("文件名.")
		private String fileName;
		@FieldDescribe("附件内容,base64编码.")
		private String content;

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -1370726528985836188L;

	}

	public static class WoControl extends WorkControl {

		private static final long serialVersionUID = 547408977744326609L;
	}

}