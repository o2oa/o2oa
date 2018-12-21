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

public class ActionDownloadWithWork extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//Work work = emc.find(workId, Work.class);

			Attachment o = emc.find(id, Attachment.class);
			if (null == o) {
				//throw new ExceptionAttachmentNotExist(id);
				throw new Exception("根据 id：" + id + " 找不到 附件");
			}

			/** 生成当前用户针对work的权限控制,并判断是否可以访问 */
			/*			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
						if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
							throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
									work.getId());
						}
						if (!work.getAttachmentList().contains(id)) {
							throw new ExceptionWorkNotContainsAttachment(work.getTitle(), work.getId(), o.getName(), o.getId());
						}*/

			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			Wo wo = new Wo(o.readContent(mapping), this.contentType(false, o.getName()), this.contentDisposition(false, o.getName()));
			
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
