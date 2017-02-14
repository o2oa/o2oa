package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;

/**
 * 
 * 在管理界面下获取相关联的所有信息,需要control权限,同时需要添加一个permission
 */
class ManageGetAssignment extends ActionBase {

	ActionResult<WrapOutMap> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutMap> result = new ActionResult<>();
			WrapOutMap wrap = new WrapOutMap();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class, ExceptionWhen.not_found);
			Process process = business.process().pick(workCompleted.getProcess());
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has insufficient permissions.");
			}
			wrap.put("taskCompletedList", this.listTaskCompleted(business, workCompleted));
			wrap.put("readList", this.listRead(business, workCompleted));
			wrap.put("readCompletedList", this.listReadCompleted(business, workCompleted));
			wrap.put("reviewList", this.listReview(business, workCompleted));
			Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
			wrap.put("control", control);
			result.setData(wrap);
			return result;
		}
	}

}