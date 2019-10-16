package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DocumentTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

class ActionDocToWord extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, work.getId(),
					new ExceptionEntityNotExist(work.getId()))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			String person = effectivePerson.isCipher() ? work.getCreatorPerson()
					: effectivePerson.getDistinguishedName();

			byte[] bytes = null;

			if (StringUtils.equals(ProcessPlatform.DOCTOWORDTYPE_CLOUD, Config.processPlatform().getDocToWordType())) {
				bytes = DocumentTools.docToWord(wi.getFileName(), wi.getContent());
			} else {
				bytes = this.local(wi);
			}

			List<Attachment> attachments = emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob());

			Attachment attachment = null;
			for (Attachment o : attachments) {
				if (StringUtils.equalsIgnoreCase(wi.getSite(), o.getSite())) {
					attachment = o;
					break;
				}
			}

			if (null != attachment) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				emc.beginTransaction(Attachment.class);
				attachment.updateContent(mapping, bytes, wi.getFileName());
				attachment.setType((new Tika()).detect(bytes, wi.getFileName()));
				attachment.setLastUpdatePerson(person);
				attachment.setLastUpdateTime(new Date());
				emc.check(attachment, CheckPersistType.all);
				emc.commit();
			} else {
				StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
				emc.beginTransaction(Attachment.class);
				attachment = new Attachment();
				attachment.setCompleted(false);
				attachment.setPerson(person);
				attachment.setLastUpdatePerson(person);
				attachment.setSite(wi.getSite());
				/** 用于判断目录的值 */
				attachment.setWorkCreateTime(work.getCreateTime());
				attachment.setApplication(work.getApplication());
				attachment.setProcess(work.getProcess());
				attachment.setJob(work.getJob());
				attachment.setActivity(work.getActivity());
				attachment.setActivityName(work.getActivityName());
				attachment.setActivityToken(work.getActivityToken());
				attachment.setActivityType(work.getActivityType());
				attachment.saveContent(mapping, bytes, wi.getFileName());
				attachment.setType((new Tika()).detect(bytes, wi.getFileName()));
				emc.persist(attachment, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	private byte[] local(Wi wi) throws Exception {
		String content = "<html><head></head><body>" + wi.getContent() + "</body></html>";
		try (POIFSFileSystem fs = new POIFSFileSystem();
				InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fs.createDocument(is, "WordDocument");
			fs.writeFilesystem(out);
			return out.toByteArray();
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("转换文件名.")
		private String fileName;
		@FieldDescribe("附件site.")
		private String site;
		@FieldDescribe("内容.")
		private String content;

		public String getFileName() throws Exception {
			return StringUtils.isEmpty(fileName) ? Config.processPlatform().getDocToWordDefaultFileName() : fileName;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getSite() throws Exception {
			return StringUtils.isEmpty(site) ? Config.processPlatform().getDocToWordDefaultSite() : site;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

}