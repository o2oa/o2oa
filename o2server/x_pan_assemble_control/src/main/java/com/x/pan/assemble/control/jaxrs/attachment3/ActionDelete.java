package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Recycle3;

import java.util.Date;


class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if(!business.zoneEditable(effectivePerson, attachment.getFolder(), attachment.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}


			if(FileStatusEnum.VALID.getName().equals(attachment.getStatus())){
				String name = attachment.getName();
				if (business.attachment3().exist(name, attachment.getFolder(), FileStatusEnum.INVALID.getName())) {
					name = business.adjustDate(name);
				}
				Recycle3 recycle = new Recycle3(effectivePerson.getDistinguishedName(), name, attachment.getId(), Share.FILE_TYPE_ATTACHMENT, attachment.getZoneId());
				recycle.setExtension(attachment.getExtension());
				recycle.setLength(attachment.getLength());
				emc.beginTransaction(Recycle3.class);
				emc.persist(recycle, CheckPersistType.all);

				emc.beginTransaction(Attachment3.class);
				attachment.setName(name);
				attachment.setStatus(FileStatusEnum.INVALID.getName());
				attachment.setLastUpdateTime(new Date());
				attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
