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
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

@Deprecated
class ActionCreateWithWorkPath7 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkPath7.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1, String path2,
			String path3, String path4, String path5, String path6, String path7, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug(
				"execute:{}, id:{}, path0:{}, path1:{}, path2:{}, path3{}, path4:{}, path5:{}, path6:{}, path7:{}.",
				effectivePerson::getDistinguishedName, () -> id, () -> path0, () -> path1, () -> path2, () -> path3,
				() -> path4, () -> path5, () -> path6, () -> path7);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					createData(business, work, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
					wo.setId(work.getId());
				}
				return "";
			}
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 4113498723483328597L;

	}

}
