package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;

/**
 * 在应用管理界面下获取一个WorkCompleted的内容,.同时需要添加一个permission. 权限:需要至少process的管理权限.
 */
class ManageGet extends ActionBase {

	ActionResult<WrapOutWorkCompleted> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutWorkCompleted> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new WorkCompletedNotExistedException(id);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new ProcessAccessDeniedException(effectivePerson.getName(), workCompleted.getProcess());
			}
			WrapOutWorkCompleted wrap = workCompletedOutCopier.copy(workCompleted);
			/* 添加权限 */
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			wrap.setControl(control);
			result.setData(wrap);
			return result;
		}
	}
}
