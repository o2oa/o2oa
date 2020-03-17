package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.ThisApplication;

/**
 * 
 * @author Rui
 *
 */
class ActionDeleteDraft extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionDeleteDraft.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				Wo wo = new Wo();
				Work work = null;
				boolean delete = false;
				String workId = null;
				String workTitle = null;
				String workSequence = null;
				try {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						work = emc.find(id, Work.class);
						if (null != work) {
							workId = work.getId();
							workTitle = work.getTitle();
							workSequence = work.getSequence();
							if (work.getWorkThroughManual() == false) {
								if (StringUtils.equals(work.getWorkCreateType(), Work.WORKCREATETYPE_SURFACE)) {
									if (emc.countEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
											work.getJob()) == 0) {
										if (emc.countEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME,
												work.getJob()) == 0) {
											delete = true;
										}
									}
								}
							}
						}
					} catch (Exception e) {
						throw new ExceptionDeleteDraft(e, workId, workTitle, workSequence);
					}
				} catch (Exception e) {
					logger.error(e);
				}
				if (delete && (null != work)) {
					ThisApplication.context().applications().deleteQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId()), work.getJob()).getData(WoId.class);
					logger.print("删除长期处于草稿状态的工作, id:{}, title:{}, sequence:{}", workId, workTitle, workSequence);
				}
				wo.setId(work.getId());
				result.setData(wo);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();
	}

	public static class Wo extends WoId {

	}

}