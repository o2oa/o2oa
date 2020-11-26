package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.service.processing.ThisApplication;

class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Snap snap = emc.fetch(id, Snap.class, ListTools.toList(Snap.job_FIELDNAME));
			if (null == snap) {
				throw new ExceptionEntityNotExist(id, Snap.class);
			}
			job = snap.getJob();
		}
		return ProcessPlatformExecutorFactory.get(job).submit(new CallableImpl(id)).get(300, TimeUnit.SECONDS);
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		public CallableImpl(String id) {
			this.id = id;
		}

		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Snap snap = emc.find(id, Snap.class);
				if (null == snap) {
					throw new ExceptionEntityNotExist(id, Snap.class);
				}
				// 已经没有work,workCompleted以及snap所以附件可以删除了
				if ((emc.countEqual(Work.class, Work.job_FIELDNAME, snap.getJob()) == 0)
						&& (emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, snap.getJob()) == 0)
						&& (emc.countEqualAndNotEqual(Snap.class, Snap.job_FIELDNAME, snap.getJob(),
								JpaObject.id_FIELDNAME, snap.getId()) == 0)) {
					emc.beginTransaction(Attachment.class);
					emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, snap.getJob()).forEach(o -> {
						try {
							StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
									o.getStorage());
							if (null != mapping) {
								o.deleteContent(mapping);
							}
							emc.remove(o, CheckRemoveType.all);
						} catch (Exception e) {
							logger.error(e);
						}
					});
					emc.commit();
				}
				emc.beginTransaction(Snap.class);
				emc.remove(snap, CheckRemoveType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(snap.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
