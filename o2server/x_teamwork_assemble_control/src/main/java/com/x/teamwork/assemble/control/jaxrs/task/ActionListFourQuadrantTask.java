package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;

import java.util.List;
import java.util.Optional;

/**
 * 任务四象限展现
 * @author sword
 */
public class ActionListFourQuadrantTask extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListFourQuadrantTask.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String projectId, String important, String urgency, Integer count, Boolean justExecutor) throws Exception {
		logger.debug("ActionListFourQuadrantTask:{}#{}#{}#{}#{}#{}", effectivePerson::getDistinguishedName,
				() -> projectId, () -> important, () -> urgency, () -> count, () -> justExecutor);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos;
		Cache.CacheKey cacheKey = new Cache.CacheKey(effectivePerson.getDistinguishedName(), projectId, important, urgency, count, justExecutor);
		Optional<?> optional = CacheManager.get(taskCache, cacheKey);
		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
			result.setData(wos);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> statusList = ListTools.toList(ProjectStatusEnum.PROCESSING.getValue(), ProjectStatusEnum.DELAY.getValue());
				List<Task> taskList = business.taskFactory().listFourQuadrant(effectivePerson, statusList,
						projectId, important, urgency, count, justExecutor);
				wos = Wo.copier.copy(taskList);
				result.setData(wos);
				CacheManager.put(taskCache, cacheKey, wos);
			}
		}

		return result;
	}

	public static class Wo extends Task {

		private static final long serialVersionUID = -4942117965200544174L;
		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}

}
