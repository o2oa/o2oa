package com.x.file.assemble.control.jaxrs.attachment2;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import org.apache.commons.lang3.StringUtils;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Attachment2 attachment = emc.find(id, Attachment2.class);
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment2.class);
			}
			/* 判断文件的所有者是否是当前用户 */
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), attachment.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Attachment2.class);
			Wi.copier.copy(wi, attachment);
			emc.check(attachment, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(attachment.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Attachment2 {

		private static final long serialVersionUID = -5317431633607552753L;

		static WrapCopier<Wi, Attachment2> copier = WrapCopierFactory.wi(Wi.class, Attachment2.class,
				ListTools.toList("folder", "name"), null);

	}

	public static class Wo extends WoId {

	}
}