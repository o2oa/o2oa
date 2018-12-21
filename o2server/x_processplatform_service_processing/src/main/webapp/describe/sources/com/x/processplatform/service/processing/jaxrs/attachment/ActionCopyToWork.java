package com.x.processplatform.service.processing.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.ThisApplication;

class ActionCopyToWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCopyToWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Work work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			if (ListTools.isNotEmpty(wi.getAttachmentList())) {
				List<Attachment> adds = new ArrayList<>();
				for (WiAttachment w : wi.getAttachmentList()) {
					Attachment o = emc.find(w.getId(), Attachment.class);
					if (null == o) {
						throw new ExceptionAttachmentNotExist(w.getId());
					}
					StorageMapping fromStorageMapping = ThisApplication.context().storageMappings()
							.get(Attachment.class, o.getStorage());
					StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
					byte[] bs = o.readContent(fromStorageMapping);
					Attachment attachment = new Attachment(work, effectivePerson.getDistinguishedName(), w.getSite());
					attachment.saveContent(mapping, bs, w.getName());
					logger.debug(effectivePerson, "create attachment:{}.", attachment);
					adds.add(attachment);
				}
				if (!adds.isEmpty()) {
					emc.beginTransaction(Attachment.class);
					// emc.beginTransaction(Work.class);
					for (Attachment o : adds) {
						// work.getAttachmentList().add(o.getId());
						emc.persist(o, CheckPersistType.all);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					emc.commit();
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("附件对象")
		private List<WiAttachment> attachmentList = new ArrayList<>();

		public List<WiAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WiAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

	}

	public static class WiAttachment extends Attachment {

		private static final long serialVersionUID = 5623475924507252797L;

	}

	public static class Wo extends WoId {

	}

}