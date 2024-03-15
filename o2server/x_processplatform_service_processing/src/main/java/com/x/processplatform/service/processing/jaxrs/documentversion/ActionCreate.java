package com.x.processplatform.service.processing.jaxrs.documentversion;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Work work = emc.find(workId, Work.class);
					emc.beginTransaction(DocumentVersion.class);
					DocumentVersion documentVersion = new DocumentVersion();
					documentVersion.setActivity(work.getActivity());
					documentVersion.setActivityAlias(work.getActivityAlias());
					documentVersion.setActivityDescription(work.getActivityDescription());
					documentVersion.setActivityName(work.getActivityName());
					documentVersion.setActivityToken(work.getActivityToken());
					documentVersion.setActivityType(work.getActivityType());
					documentVersion.setCategory(wi.getCategory());
					documentVersion.setData(wi.getData());
					documentVersion.setPerson(wi.getPerson());
					documentVersion.setCompleted(false);
					documentVersion.setJob(work.getJob());
					documentVersion.setApplication(work.getApplication());
					documentVersion.setProcess(work.getProcess());
					emc.persist(documentVersion, CheckPersistType.all);
					emc.commit();
					Wo wo = new Wo();
					wo.setId(documentVersion.getId());
					ActionResult<Wo> result = new ActionResult<>();
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);
	}

	public static class Wi extends DocumentVersion {

		private static final long serialVersionUID = 415341949561538852L;
		static WrapCopier<Wi, DocumentVersion> copier = WrapCopierFactory.wi(Wi.class, DocumentVersion.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 7501733209869516518L;

	}

}