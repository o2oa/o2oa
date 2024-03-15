package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Handover;
import com.x.processplatform.core.entity.content.HandoverStatusEnum;

class ActionCancel extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Handover handover = emc.find(id, Handover.class);
			if (null == handover) {
				throw new ExceptionEntityNotExist(id);
			}
			if(!HandoverStatusEnum.WAIT.getValue().equals(handover.getStatus())){
				throw new ExceptionHasProcess("不能取消");
			}
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Handover.class);
			handover.setStatus(HandoverStatusEnum.CANCEL.getValue());
			emc.commit();
			Wo wo = new Wo(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 4480236405915728857L;

		public Wo(Boolean value){
			super(value);
		}
	}
}
