package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInTask;
import com.x.processplatform.core.entity.content.Task;

public class ActionReset extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInTask wrapIn = this.convertToWrapIn(jsonElement, WrapInTask.class);
			ActionResult<WrapOutId> result = new ActionResult<>();
			Business business = new Business(emc);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new TaskNotExistedException(id);
			}
			// Work work = emc.find(task.getWork(), Work.class,
			// ExceptionWhen.not_found);
			Control control = business.getControlOfTask(effectivePerson, task);
			if (BooleanUtils.isNotTrue(control.getAllowReset())) {
				throw new TaskAccessDeniedException(effectivePerson.getName(), task.getId());
			}
			/* 检查reset人员 */
			List<String> identites = business.organization().identity().check(wrapIn.getIdentityList());
			if (!identites.isEmpty()) {
				emc.beginTransaction(Task.class);
				/* 如果有选择新的路由那么覆盖之前的选择 */
				if (StringUtils.isNotEmpty(wrapIn.getRouteName())) {
					task.setRouteName(wrapIn.getRouteName());
				}
				/* 如果有新的流程意见那么覆盖流程意见 */
				if (StringUtils.isNotEmpty(wrapIn.getOpinion())) {
					task.setOpinion(wrapIn.getOpinion());
				}
				emc.commit();
				wrapIn.setIdentityList(identites);
				ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
						"task/" + URLEncoder.encode(task.getId(), DefaultCharset.name) + "/reset", wrapIn);
			}
			WrapOutId wrap = new WrapOutId(task.getWork());
			result.setData(wrap);
			return result;
		}
	}

}
