package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
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
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.ThisApplication;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug(effectivePerson.getDistinguishedName());
		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Snap snap = emc.fetch(id, Snap.class, ListTools.toList(Snap.job_FIELDNAME));
			if (null == snap) {
				throw new ExceptionEntityNotExist(id, Snap.class);
			}
			job = snap.getJob();
		}
		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(new CallableImpl(id)).get(300, TimeUnit.SECONDS);
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		public CallableImpl(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Snap snap = emc.find(id, Snap.class);
				if (null == snap) {
					throw new ExceptionEntityNotExist(id, Snap.class);
				}
				emc.beginTransaction(Snap.class);
				// work和workCompleted都没有这个job,说明snap的时候已经删除所有关联,如果有Attachment,是因为无法收到json中遗留在attachment表中的内容,所以需要删除.
				if ((0 == emc.countEqual(Work.class, Work.job_FIELDNAME, snap.getJob()))
						&& (0 == emc.countEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, snap.getJob()))) {
					emc.beginTransaction(Attachment.class);
					deleteAttachment(business, snap.getJob());
				}
				emc.remove(snap, CheckRemoveType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(snap.getId());
				result.setData(wo);
				return result;
			}
		}

		private void deleteAttachment(Business business, String job) throws Exception {
			List<String> ids = business.entityManagerContainer().idsEqual(Attachment.class, Attachment.job_FIELDNAME,
					job);
			if (ListTools.isNotEmpty(ids)) {
				business.entityManagerContainer().beginTransaction(Attachment.class);
				Attachment obj;
				for (String id : ids) {
					obj = business.entityManagerContainer().find(id, Attachment.class);
					if (null != obj) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								obj.getStorage());
						if (null != mapping) {
							obj.deleteContent(mapping);
						}
						business.entityManagerContainer().remove(obj, CheckRemoveType.all);
					}
				}
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
