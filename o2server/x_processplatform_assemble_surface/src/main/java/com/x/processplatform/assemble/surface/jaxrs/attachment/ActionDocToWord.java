package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

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
import com.x.base.core.project.config.StorageMapping;
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
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.ActionDocToWordWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDocToWord extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDocToWord.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isNotBlank(wi.getContent())) {
			try {
				String decodedContent = URLDecoder.decode(wi.getContent(), StandardCharsets.UTF_8.name());
				wi.setContent(decodedContent);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		Work work = null;
		Wo wo = new Wo();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
		}

		String person = effectivePerson.isCipher() ? work.getCreatorPerson() : effectivePerson.getDistinguishedName();
		byte[] bytes = null;

		if (StringUtils.equals(ProcessPlatform.DOCTOWORDTYPE_CLOUD, Config.processPlatform().getDocToWordType())) {
			bytes = DocumentTools.docToWord(wi.getFileName(), wi.getContent());
		} else {
			bytes = this.local(wi);
		}
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
				/** 用于判断目录的值 */
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
			wo.setId(attachment.getId());
		}
		result.setData(wo);
		return result;

	}

	private byte[] local(Wi wi) throws IOException {
		String content = "<html><head></head><body>" + wi.getContent() + "</body></html>";
		try (POIFSFileSystem fs = new POIFSFileSystem();
				InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			fs.createDocument(is, "WordDocument");
			fs.writeFilesystem(out);
			return out.toByteArray();
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDocToWord$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = 8076380614700207161L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.attachment.ActionDocToWord$Wi")
	public static class Wi extends ActionDocToWordWi {

		private static final long serialVersionUID = -1098540826705109493L;

	}
}
