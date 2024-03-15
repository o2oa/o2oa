package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Handover;
import com.x.processplatform.core.entity.content.HandoverSchemeEnum;
import com.x.processplatform.core.entity.content.HandoverStatusEnum;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Handover handover = emc.find(id, Handover.class);
			if (null == handover) {
				throw new ExceptionEntityNotExist(id);
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(handover);
			wo.setStatus(HandoverStatusEnum.getNameByValue(wo.getStatus()));
			wo.setScheme(HandoverSchemeEnum.getNameByValue(wo.getScheme()));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Handover {

		private static final long serialVersionUID = -8191959290823285559L;
		static WrapCopier<Handover, Wo> copier = WrapCopierFactory.wo(Handover.class, Wo.class, null,
				JpaObject.FieldsInvisibleIncludeProperites);

	}
}
