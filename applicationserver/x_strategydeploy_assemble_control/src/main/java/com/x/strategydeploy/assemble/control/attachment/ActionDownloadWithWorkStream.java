package com.x.strategydeploy.assemble.control.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.ThisApplication;
import com.x.strategydeploy.core.entity.Attachment;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionDownloadWithWorkStream extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId, Boolean stream) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/** 判断work是否存在 */
			boolean isexist = business.keyworkInfoFactory().IsExistById(workId);
			KeyworkInfo work = emc.find(workId, KeyworkInfo.class);
			if (!isexist) {
				throw new ExceptionWorkNotExist(workId);
			}
			/** 判断attachment是否存在 */
			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!work.getAttachmentList().contains(id)) {
				throw new ExceptionWorkNotContainsAttachment(work.getKeyworktitle(), work.getId(), o.getName(), o.getId());
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			Wo wo = new Wo(o.readContent(mapping), this.contentType(stream, o.getName()), this.contentDisposition(stream, o.getName()));
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
}
