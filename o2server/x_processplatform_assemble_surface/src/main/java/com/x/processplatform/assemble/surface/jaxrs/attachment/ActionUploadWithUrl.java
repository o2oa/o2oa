package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import java.util.List;

class ActionUploadWithUrl extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadWithUrl.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug("ActionUploadWithUrl receive:{}.", jsonElement.toString());
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isEmpty(wi.getWorkId())){
				throw new ExceptionEntityFieldEmpty(Work.class, wi.getWorkId());
			}
			if(StringUtils.isEmpty(wi.getFileName())){
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileName());
			}
			if(StringUtils.isEmpty(wi.getFileUrl())){
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getFileUrl());
			}
			if(StringUtils.isEmpty(wi.getSite())){
				throw new ExceptionEntityFieldEmpty(Attachment.class, wi.getSite());
			}
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, wi.getWorkId(),
					new ExceptionEntityNotExist(wi.getWorkId()))) {
				throw new ExceptionAccessDenied(effectivePerson, wi.getWorkId());
			}
			String person = effectivePerson.getDistinguishedName();
			if(StringUtils.isNotEmpty(wi.getPerson()) && business.canManageApplication(effectivePerson, null)){
				Person p = business.organization().person().getObject(wi.getPerson());
				if(p!=null){
					person = p.getDistinguishedName();
				}
			}
			Attachment attachment = null;
			/* 后面要重新保存 */
			Work work = emc.find(wi.getWorkId(), Work.class);
			/** 判断work是否存在 */
			if (null == work) {
				WorkCompleted workCompleted = emc.find(wi.getWorkId(), WorkCompleted.class);
				if(workCompleted!=null){
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
			}else{
				attachment = this.concreteAttachment(work, person, wi.getSite());
			}
			if(attachment==null){
				new ExceptionEntityNotExist(wi.getWorkId());
			}
			byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
			if(bytes==null || bytes.length==0){
				throw new Exception("can not down file from url!");
			}
			String fileName = wi.getFileName();
			if (StringUtils.isEmpty(fileName)) {
				throw new Exception("fileName can not empty!");
			}
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
		/** 用于判断目录的值 */
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

	private Attachment concreteAttachment(WorkCompleted workCompleted, String person, String site, End end) throws Exception {
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
		attachment.setActivity(end.getId());
		attachment.setActivityName(end.getName());
		attachment.setActivityToken(end.getId());
		attachment.setActivityType(end.getActivityType());
		return attachment;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6675218812025672288L;

		@FieldDescribe("*Work或WorkCompleted的id.")
		private String workId;

		@FieldDescribe("*文件名称,带扩展名的文件名.")
		private String fileName;

		@FieldDescribe("*附件来源url地址.")
		private String fileUrl;

		@FieldDescribe("*附件分类.")
		private String site;

		@FieldDescribe("上传人员（仅对管理员生效）.")
		private String person;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}

}
