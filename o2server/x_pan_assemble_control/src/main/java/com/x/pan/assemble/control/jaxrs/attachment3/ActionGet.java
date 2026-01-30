package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment.getFolder() : attachment.getZoneId();
			if(!business.zoneViewable(effectivePerson, zoneId)){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Wo wo = Wo.copier.copy(attachment);
			setExtendInfo(business, wo, effectivePerson);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapAttachment3 {

		private static final long serialVersionUID = -6085236942155821637L;

		static WrapCopier<Attachment3, Wo> copier = WrapCopierFactory.wo(Attachment3.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
