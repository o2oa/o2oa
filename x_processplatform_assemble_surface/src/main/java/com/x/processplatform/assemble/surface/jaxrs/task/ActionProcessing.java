package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInTask;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkLog;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.WorkLog;

class ActionProcessing extends ActionBase {

	ActionResult<List<WrapOutWorkLog>> execute(EffectivePerson effectivePerson, String id, WrapInTask wrapIn)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutWorkLog>> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(Task.class);
			Task task = emc.find(id, Task.class, ExceptionWhen.not_found);
			Map<String, Object> requestAttributes = new HashMap<String, Object>();
			if (!StringUtils.equalsIgnoreCase(task.getPerson(), effectivePerson.getName())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} access task{id:" + task.getId()
						+ "} was denied.");
			}
			/* 如果有输入新的路由决策覆盖原有决策 */
			if (StringUtils.isNotEmpty(wrapIn.getRouteName())) {
				task.setRouteName(wrapIn.getRouteName());
			}
			/* 如果有新的流程意见那么覆盖原有流程意见 */
			if (StringUtils.isNotEmpty(wrapIn.getOpinion())) {
				task.setOpinion(wrapIn.getOpinion());
			}
			emc.commit();
			/* processing task */
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"task/" + URLEncoder.encode(task.getId(), "UTF-8") + "/processing", requestAttributes);
			/* 流程处理完毕,开始组装返回信息 */
			List<WrapOutWorkLog> wraps = WorkLogBuilder.complex(business, emc.list(WorkLog.class,
					business.workLog().listWithFromActivityTokenForwardNotConnected(task.getActivityToken())));
			result.setData(wraps);
			return result;
		}
	}

}
