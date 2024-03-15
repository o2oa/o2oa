package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Date;
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
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ExtractTextTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageBatchUpload extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageBatchUpload.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workIds, String site, String fileName,
			byte[] bytes, FormDataContentDisposition disposition, String extraParam, String person, Integer order,
			Boolean isSoftUpload, String mainWork) throws Exception {

		LOGGER.debug("execute:{}, workIds:{}, site:{}.", effectivePerson::getDistinguishedName, () -> workIds);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			// 需要对这个应用的管理权限
			if (BooleanUtils.isFalse(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
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
			if (StringUtils.isEmpty(person)) {
				person = effectivePerson.getDistinguishedName();
			}
			if (StringUtils.isNotEmpty(workIds) && bytes != null && bytes.length > 0) {
				Attachment mainAtt = null;
				if (BooleanUtils.isTrue(isSoftUpload) && StringUtils.isNotEmpty(mainWork)) {
					LOGGER.print("file {} soft upload from mainWork:{}", fileName, mainWork);
					mainWork = mainWork.trim();
					Work work = emc.find(mainWork, Work.class);
					if (work != null) {
						mainAtt = this.concreteAttachment(work, person, site, order);
					} else {
						WorkCompleted workCompleted = emc.find(mainWork, WorkCompleted.class);
						if (null != workCompleted) {
							Process process = business.process().pick(workCompleted.getProcess());
							if (null == process) {
								throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
							}
							List<End> ends = business.end().listWithProcess(process);
							if (ends.isEmpty()) {
								throw new ExceptionEndNotExist(process.getId());
							}
							mainAtt = this.concreteAttachment(workCompleted, person, site, order, ends.get(0));
						}
					}
					if (mainAtt != null) {
						StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
						mainAtt.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
						mainAtt.setType((new Tika()).detect(bytes, fileName));
						if (BooleanUtils.isTrue(
								Config.query().getExtractImage() && ExtractTextTools.supportImage(mainAtt.getName()))
								&& ExtractTextTools.available(bytes)) {
							mainAtt.setText(ExtractTextTools.image(bytes));
						}
						emc.beginTransaction(Attachment.class);
						emc.persist(mainAtt, CheckPersistType.all);
						emc.commit();
					}
				}
				String[] idArray = workIds.split(",");
				for (String workId : idArray) {
					Attachment attachment = null;
					workId = workId.trim();
					if (mainAtt != null && workId.equals(mainWork)) {
						continue;
					}
					Work work = emc.find(workId, Work.class);
					if (work != null) {
						attachment = this.concreteAttachment(work, person, site, order);
					} else {
						WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
						if (null != workCompleted) {
							Process process = business.process().pick(workCompleted.getProcess());
							if (null == process) {
								throw new ExceptionEntityNotExist(workCompleted.getProcess(), Process.class);
							}
							List<End> ends = business.end().listWithProcess(process);
							if (ends.isEmpty()) {
								throw new ExceptionEndNotExist(process.getId());
							}
							attachment = this.concreteAttachment(workCompleted, person, site, order, ends.get(0));
						}
					}
					if (attachment != null) {
						StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
						if (mainAtt != null) {
							attachment.setName(mainAtt.getName());
							attachment.setDeepPath(mapping.getDeepPath());
							attachment.setExtension(
									StringUtils.lowerCase(StringUtils.substringAfterLast(mainAtt.getName(), ".")));
							attachment.setLength(mainAtt.getLength());
							attachment.setStorage(mapping.getName());
							attachment.setType(mainAtt.getType());
							attachment.setText(mainAtt.getText());
							attachment.setLastUpdateTime(new Date());
							attachment.setFromJob(mainAtt.getJob());
							attachment.setFromId(mainAtt.getId());
							attachment.setFromPath(mainAtt.path());
						} else {
							attachment.saveContent(mapping, bytes, fileName,
									Config.general().getStorageEncrypt());
							attachment.setType((new Tika()).detect(bytes, fileName));
							if (BooleanUtils.isTrue(Config.query().getExtractImage())
									&& ExtractTextTools.supportImage(attachment.getName())
									&& ExtractTextTools.available(bytes)) {
								attachment.setText(ExtractTextTools.image(bytes));
							}
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
		// 用于判断目录的值
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		if (order != null) {
			attachment.setOrderNumber(order);
		}
		return attachment;
	}

	private Attachment concreteAttachment(WorkCompleted workCompleted, String person, String site, Integer order,
			End end) throws Exception {
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
		if (order != null) {
			attachment.setOrderNumber(order);
		}
		return attachment;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionManageBatchUpload$Wo")
	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 5608898238425800133L;

	}

}
