package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionDeleteDraft extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeleteDraft.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		CallableImpl impl = new CallableImpl(id);

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(impl).get(300, TimeUnit.SECONDS);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		private CallableImpl(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Work work = null;
			String workId = null;
			String workTitle = null;
			String workSequence = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				work = emc.find(id, Work.class);
				if ((null != work) && checkCanDelete(business, work)) {
					cascadeDeleteWorkBeginButNotCommit(business, work);
					emc.commit();
					LOGGER.info("删除长期处于草稿状态的工作, id:{}, title:{}, sequence:{}", work::getId, work::getTitle,
							work::getSequence);
					wo.setId(work.getId());
				}
			} catch (Exception e) {
				throw new ExceptionDeleteDraft(e, workId, workTitle, workSequence);
			}
			result.setData(wo);
			return result;
		}

		private boolean checkCanDelete(Business business, Work work) throws Exception {
			EntityManagerContainer emc = business.entityManagerContainer();
			return (BooleanUtils.isFalse(work.getWorkThroughManual()))
					&& (StringUtils.equals(work.getWorkCreateType(), Work.WORKCREATETYPE_SURFACE))
					&& (emc.countEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob()) == 0)
					&& (emc.countEqual(Read.class, Read.job_FIELDNAME, work.getJob()) == 0)
					&& (emc.countEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, work.getJob()) == 0);
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -3251315008033833155L;

	}
}