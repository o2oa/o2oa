package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform;
import com.x.base.core.project.config.ProcessPlatform.WorkCompletedExtensionEvent;
import com.x.base.core.project.config.ProcessPlatform.WorkExtensionEvent;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DocumentTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionDocToWordWorkOrWorkCompletedWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDocToWordWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDocToWordWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted, JsonElement jsonElement)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isNotBlank(wi.getContent())) {
			try {
				String decodedContent = URLDecoder.decode(wi.getContent(), StandardCharsets.UTF_8.name());
				wi.setContent(decodedContent);
			} catch (Exception e) {
				LOGGER.warn("docContent URLDecoder error:" + e.getMessage());
			}
		}
		Work work = null;
		WorkCompleted workCompleted = null;
		Wo wo = new Wo();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(workOrWorkCompleted, Work.class);
			if (null == work) {
				workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
			}
			if ((null == work) && (null == workCompleted)) {
				throw new ExceptionEntityNotExist(workOrWorkCompleted, Work.class);
			}
			if (null != work) {
				Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
			}
			if (null != workCompleted) {
				Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
						.enableAllowVisit().build();
				if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
					throw new ExceptionAccessDenied(effectivePerson, workCompleted);
				}
			}
		}
		if (null != work) {
			wo = this.work(effectivePerson, wi, work);
		} else {
			wo = this.workCompleted(effectivePerson, wi, workCompleted);
		}

		result.setData(wo);
		return result;

	}

	private byte[] workConvert(EffectivePerson effectivePerson, Wi wi, String application, String process,
			String activity, String job) throws Exception {
		byte[] bytes = null;
		Optional<WorkExtensionEvent> event;
		if (wi.getFileName().toLowerCase().endsWith(OFD_ATT_KEY)) {
			event = Config.processPlatform().getExtensionEvents().getWorkDocToOfdEvents().bind(application, process,
					activity);
		} else {
			event = Config.processPlatform().getExtensionEvents().getWorkDocToWordEvents().bind(application, process,
					activity);
		}
		if (event.isPresent()) {
			bytes = this.workExtensionService(effectivePerson, wi.getContent(), event.get(), job);
		} else {
			if (StringUtils.equals(ProcessPlatform.DOCTOWORDTYPE_CLOUD, Config.processPlatform().getDocToWordType())) {
				bytes = DocumentTools.docToWord(wi.getFileName(), wi.getContent());
			} else {
				bytes = this.local(wi);
			}
		}
		return bytes;
	}

	private byte[] workExtensionService(EffectivePerson effectivePerson, String content, WorkExtensionEvent event,
			String job) throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setContent(content);
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setJob(job);
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	private byte[] workCompletedConvert(EffectivePerson effectivePerson, Wi wi, String application, String process,
			String job) throws Exception {
		byte[] bytes = null;
		Optional<WorkCompletedExtensionEvent> event;
		if (wi.getFileName().toLowerCase().endsWith(OFD_ATT_KEY)) {
			event = Config.processPlatform().getExtensionEvents().getWorkCompletedDocToOfdEvents().bind(application,
					process);
		} else {
			event = Config.processPlatform().getExtensionEvents().getWorkCompletedDocToWordEvents().bind(application,
					process);
		}
		if (event.isPresent()) {
			bytes = this.workCompletedExtensionService(effectivePerson, wi.getContent(), event.get(), job);
		} else {
			if (StringUtils.equals(ProcessPlatform.DOCTOWORDTYPE_CLOUD, Config.processPlatform().getDocToWordType())) {
				bytes = DocumentTools.docToWord(wi.getFileName(), wi.getContent());
			} else {
				bytes = this.local(wi);
			}
		}
		return bytes;
	}

	private byte[] workCompletedExtensionService(EffectivePerson effectivePerson, String content,
			WorkCompletedExtensionEvent event, String job) throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setContent(content);
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setJob(job);
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	private Wo work(EffectivePerson effectivePerson, Wi wi, Work work) throws Exception {
		String person = effectivePerson.isCipher() ? work.getCreatorPerson() : effectivePerson.getDistinguishedName();
		byte[] bytes = this.workConvert(effectivePerson, wi, work.getApplication(), work.getProcess(),
				work.getActivity(), work.getJob());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Attachment> attachments = emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, work.getJob());
			Attachment attachment = null;
			for (Attachment o : attachments) {
				if (StringUtils.equalsIgnoreCase(wi.getSite(), o.getSite())
						&& StringUtils.equalsIgnoreCase(o.getName(), wi.getFileName())) {
					attachment = o;
					break;
				}
			}
			if (null != attachment) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				emc.beginTransaction(Attachment.class);
				attachment.updateContent(mapping, bytes, wi.getFileName(), Config.general().getStorageEncrypt());
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
				// 用于判断目录的值
				attachment.setWorkCreateTime(work.getCreateTime());
				attachment.setApplication(work.getApplication());
				attachment.setProcess(work.getProcess());
				attachment.setJob(work.getJob());
				attachment.setActivity(work.getActivity());
				attachment.setActivityName(work.getActivityName());
				attachment.setActivityToken(work.getActivityToken());
				attachment.setActivityType(work.getActivityType());
				attachment.saveContent(mapping, bytes, wi.getFileName(), Config.general().getStorageEncrypt());
				attachment.setType((new Tika()).detect(bytes, wi.getFileName()));
				emc.persist(attachment, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			return wo;
		}
	}

	private Wo workCompleted(EffectivePerson effectivePerson, Wi wi, WorkCompleted workCompleted) throws Exception {
		String person = effectivePerson.isCipher() ? workCompleted.getCreatorPerson()
				: effectivePerson.getDistinguishedName();
		byte[] bytes = this.workCompletedConvert(effectivePerson, wi, workCompleted.getApplication(),
				workCompleted.getProcess(), workCompleted.getJob());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Attachment> attachments = emc.listEqual(Attachment.class, Attachment.job_FIELDNAME,
					workCompleted.getJob());
			Attachment attachment = null;
			for (Attachment o : attachments) {
				if (StringUtils.equalsIgnoreCase(wi.getSite(), o.getSite())
						&& StringUtils.equalsIgnoreCase(o.getName(), wi.getFileName())) {
					attachment = o;
					break;
				}
			}
			if (null != attachment) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						attachment.getStorage());
				emc.beginTransaction(Attachment.class);
				attachment.updateContent(mapping, bytes, wi.getFileName(), Config.general().getStorageEncrypt());
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
				// 用于判断目录的值
				attachment.setWorkCreateTime(workCompleted.getCreateTime());
				attachment.setApplication(workCompleted.getApplication());
				attachment.setProcess(workCompleted.getProcess());
				attachment.setJob(workCompleted.getJob());
				attachment.setActivity(workCompleted.getActivity());
				attachment.setActivityName(workCompleted.getActivityName());
				attachment.saveContent(mapping, bytes, wi.getFileName(), Config.general().getStorageEncrypt());
				attachment.setType((new Tika()).detect(bytes, wi.getFileName()));
				emc.persist(attachment, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			return wo;
		}
	}

	public static class Req {

		private String person;

		private String content;

		private String job;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}
	}

	private byte[] local(Wi wi) throws IOException {
		try (POIFSFileSystem fs = new POIFSFileSystem();
				InputStream is = new ByteArrayInputStream(wi.getContent().getBytes(StandardCharsets.UTF_8));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fs.createDocument(is, "WordDocument");
			fs.writeFilesystem(out);
			return out.toByteArray();
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDocToWordWorkOrWorkCompleted$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -2186410922141645057L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDocToWordWorkOrWorkCompleted$Wi")
	public static class Wi extends ActionDocToWordWorkOrWorkCompletedWi {

		private static final long serialVersionUID = -205187635687327642L;

	}

}
