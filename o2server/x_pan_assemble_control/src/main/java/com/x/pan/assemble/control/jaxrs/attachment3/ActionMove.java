package com.x.pan.assemble.control.jaxrs.attachment3;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionNameExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

class ActionMove extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isBlank(wi.getName())){
				throw new ExceptionFieldEmpty(Attachment3.name_FIELDNAME);
			}
			Attachment3 attachment = emc.find(id, Attachment3.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment3.class);
			}
			if(!business.zoneEditable(effectivePerson, attachment.getFolder(), attachment.getPerson())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			if(!wi.getFolder().equals(attachment.getFolder())){
				Folder3 folder = emc.find(wi.getFolder(), Folder3.class);
				if(!folder.getZoneId().equals(attachment.getZoneId())){
					throw new ExceptionMoveDenied();
				}
				if (!business.zoneEditable(effectivePerson, folder.getId(), null)) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
				if (business.attachment3().exist(wi.getName(), wi.getFolder(), FileStatusEnum.VALID.getName())) {
					throw new ExceptionNameExist(wi.getName());
				}
				emc.beginTransaction(Attachment3.class);
				attachment.setFolder(wi.getFolder());
				attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				attachment.setLastUpdateTime(new Date());
				emc.check(attachment, CheckPersistType.all);
				emc.commit();
			}

			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Attachment3 {

		private static final long serialVersionUID = 3027026431137239038L;

		static WrapCopier<Wi, Attachment3> copier = WrapCopierFactory.wi(Wi.class, Attachment3.class,
				ListTools.toList(Attachment3.folder_FIELDNAME), null);

	}

	public static class Wo extends WoId {

	}
}
