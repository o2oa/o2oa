package com.x.program.center.jaxrs.market;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Attachment;

class ActionGetCoverPic extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (effectivePerson.isAnonymous()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Application app = emc.find(id, Application.class);
			if (null == app) {
				throw new ExceptionEntityNotExist(id, Application.class);
			}
			Wo wo = new Wo();
			List<Attachment> attList = emc.listEqualAndEqual(Attachment.class, Attachment.application_FIELDNAME, id, Attachment.type_FIELDNAME, "coverPic");
			if(attList!=null && !attList.isEmpty()){
				wo.setValue(attList.get(0).getIcon());
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}
}