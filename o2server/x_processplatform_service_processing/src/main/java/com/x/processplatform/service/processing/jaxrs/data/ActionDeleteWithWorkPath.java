package com.x.processplatform.service.processing.jaxrs.data;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionDeleteWithWorkPath extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteWithWorkPath.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}, path:{}.", effectivePerson::getDistinguishedName, () -> id, () -> path);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<String> callable = () -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				wi.init(work);
				String[] paths = path.split(PATH_SPLIT);
				if(paths.length == 1){
					wi.setDeleted(true);
					wi.setJsonElement(getDataWithPath(business, work.getJob(), paths[0]));
				}

				wo.setId(work.getId());
				deleteData(business, work, paths);

				if(paths.length > 1){
					wi.setJsonElement(getDataWithPath(business, work.getJob(), paths[0]));
				}
				createDataRecord(business, wi);
			}
			return "";
		};
		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;

	}

	public static class Wi extends DataWi {

		private static final long serialVersionUID = -2217144001809174544L;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1270932083788386471L;

	}

}
