package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionDeleteWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

/**
 * 
 * @author Rui
 *
 */
class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Param param = this.init(id);

		CallableImpl callable = new CallableImpl(param);

		return ProcessPlatformKeyClassifyExecutorFactory.get(param.job).submit(callable).get(300, TimeUnit.SECONDS);

	}

	private Param init(String id) throws Exception {
		Param param = new Param();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			param.job = work.getJob();
			param.id = work.getId();
		}
		return param;
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private Param param;

		private CallableImpl(Param param) {
			this.param = param;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(param.id, Work.class);
				cascadeDeleteWorkBeginButNotCommit(business, work);
				emc.commit();
				// 创建删除事件
				emc.beginTransaction(WorkEvent.class);
				emc.persist(WorkEvent.deleteEventInstance(work), CheckPersistType.all);
				emc.commit();
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				wo.setId(work.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	private class Param {

		private String job;

		private String id;

	}

	public static class Wo extends ActionDeleteWo {

		private static final long serialVersionUID = 7042621469119019501L;

	}

}