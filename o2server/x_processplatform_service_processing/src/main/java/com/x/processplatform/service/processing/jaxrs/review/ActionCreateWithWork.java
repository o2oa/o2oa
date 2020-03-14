package com.x.processplatform.service.processing.jaxrs.review;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.ActionLogger;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;

class ActionCreateWithWork extends BaseAction {

	@ActionLogger
	private static Logger logger = LoggerFactory.getLogger(ActionCreateWithWork.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(wi.getWork(), Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(wi.getWork(), Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

					Business business = new Business(emc);
					Work work = emc.find(wi.getWork(), Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(wi.getWork(), Work.class);
					}

					List<String> people = business.organization().person().list(wi.getPersonList());
					if (ListTools.isEmpty(people)) {
						throw new ExceptionPersonEmpty();
					}
					if (ListTools.isNotEmpty(people)) {
						emc.beginTransaction(Review.class);
						for (String person : people) {
							Review review = new Review(work, person);
							emc.persist(review, CheckPersistType.all);
							Wo wo = new Wo();
							wo.setId(review.getId());
							wos.add(wo);
						}
						emc.commit();
					}
				}
				return "";
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("工作标识")
		private String work;

		@FieldDescribe("人员列表")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

	}

	public static class Wo extends WoId {
	}

}
