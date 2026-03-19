package com.x.pan.assemble.control.jaxrs.attachment2;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Share;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Recycle3;
import org.apache.commons.lang3.StringUtils;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Attachment2 attachment = emc.find(id, Attachment2.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson()) && !business.controlAble(effectivePerson)) {
				throw new ExceptionAttachmentAccessDenied(effectivePerson, attachment);
			}
			boolean flag = false;
			Share share = business.share().getShareByFileId(attachment.getId(),effectivePerson.getDistinguishedName());
			if(share!=null){
				emc.beginTransaction(Share.class);
				emc.remove(share);
				flag = true;
			}
			if(FileStatusEnum.VALID.getName().equals(attachment.getStatus())){
				Recycle3 recycle = new Recycle3(attachment.getPerson(), attachment.getName(), attachment.getId(), Share.FILE_TYPE_ATTACHMENT, "");
				recycle.setExtension(attachment.getExtension());
				recycle.setLength(attachment.getLength());
				emc.beginTransaction(Recycle3.class);
				emc.persist(recycle, CheckPersistType.all);

				emc.beginTransaction(Attachment2.class);
				attachment.setStatus(FileStatusEnum.INVALID.getName());

				flag = true;
			}
			if(flag) {
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
