package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Process;

/**
 * 在一个应用的管理状态下删除一个workCompleted, 权限:需要至少process的管理权限.
 */
class ManageDelete extends ActionBase {

	ActionResult<List<WrapOutId>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutId>> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class, ExceptionWhen.not_found);
			Process process = business.process().pick(workCompleted.getProcess());
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has insufficient permissions.");
			}
			List<WrapOutId> wraps = ThisApplication.applications.deleteQuery(x_processplatform_service_processing.class,
					"job/" + URLEncoder.encode(workCompleted.getJob(), "UTF-8"), WrapOutId.collectionType);
			result.setData(wraps);
			return result;
		}
	}
}
