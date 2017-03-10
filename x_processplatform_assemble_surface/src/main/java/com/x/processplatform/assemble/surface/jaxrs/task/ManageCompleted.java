package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;

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
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInTask;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.element.Application;

class ManageCompleted extends ActionBase {

	ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapInTask wrapIn = this.convertToWrapIn(jsonElement, WrapInTask.class);
			Task task = null;
			Business business = new Business(emc);
			task = emc.find(id, Task.class);
			if (null == task) {
				throw new TaskNotExistedException(id);
			}
			Application application = business.application().pick(task.getApplication());
			if (null == application) {
				throw new ApplicationNotExistedException(task.getApplication());
			}
			// 需要对这个应用的管理权限
			emc.beginTransaction(Task.class);
			if (!business.application().allowControl(effectivePerson, application)) {
				throw new TaskAccessDeniedException(effectivePerson.getName(), task.getId());
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
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"task/" + URLEncoder.encode(task.getId(), DefaultCharset.name) + "/completed", null);
			WrapOutId wrap = new WrapOutId(task.getId());
			result.setData(wrap);
			return result;
		}
	}

}
