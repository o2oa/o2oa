package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdateWithWorkCompletedPath extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateWithWorkCompletedPath.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, path:{}.", effectivePerson::getDistinguishedName, () -> id, () -> path);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		Callable<String> callable = () -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
				if (null == workCompleted) {
					throw new ExceptionEntityNotExist(id, WorkCompleted.class);
				}
				if (BooleanUtils.isTrue(workCompleted.getMerged())) {
					throw new ExceptionModifyMerged(workCompleted.getId());
				}
				wo.setId(workCompleted.getId());
				String[] paths = path.split(PATH_SPLIT);
				updateData(business, workCompleted, wi.getJsonElement(), paths);

				wi.init(workCompleted);
				wi.setJsonElement(getDataWithPath(business, wi.getJob(), paths[0]));
				createDataRecord(business, wi);
			}
			return "";
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wi extends DataWi {

		private static final long serialVersionUID = -5719431331566055697L;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7437967246393300913L;

	}

}
