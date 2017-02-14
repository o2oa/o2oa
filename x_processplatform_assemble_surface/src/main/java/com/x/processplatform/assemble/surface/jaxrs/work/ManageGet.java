package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;

class ManageGet extends ActionBase {

	ActionResult<WrapOutWork> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutWork> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class, ExceptionWhen.not_found);
			/* Process 也可能为空 */
			Process process = business.process().pick(work.getProcess());
			// 需要对这个应用的管理权限
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has insufficient permissions.");
			}
			WrapOutWork wrap = workOutCopier.copy(work);
			/* 添加权限 */
			Control control = business.getControlOfWorkComplex(effectivePerson, work);
			wrap.setControl(control);
			result.setData(wrap);
			return result;
		}
	}

}