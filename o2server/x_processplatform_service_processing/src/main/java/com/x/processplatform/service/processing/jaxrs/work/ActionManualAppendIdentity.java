package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionManualAppendIdentity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManualAppendIdentity.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}
		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableImpl(id, wi)).get(300,
				TimeUnit.SECONDS);

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6857844608423951314L;

		@FieldDescribe("添加的待办身份.")
		private List<String> taskIdentityList;

		@FieldDescribe("添加的待阅身份.")
		private List<String> readIdentityList;

		@FieldDescribe("添加的参阅身份.")
		private List<String> reviewIdentityList;

		public List<String> getTaskIdentityList() {
			return taskIdentityList;
		}

		public void setTaskIdentityList(List<String> taskIdentityList) {
			this.taskIdentityList = taskIdentityList;
		}

		public List<String> getReadIdentityList() {
			return readIdentityList;
		}

		public void setReadIdentityList(List<String> readIdentityList) {
			this.readIdentityList = readIdentityList;
		}

		public List<String> getReviewIdentityList() {
			return reviewIdentityList;
		}

		public void setReviewIdentityList(List<String> reviewIdentityList) {
			this.reviewIdentityList = reviewIdentityList;
		}

	}

	public static class Wo extends WrapStringList {

		private static final long serialVersionUID = 3490090127579620225L;

		@FieldDescribe("添加的待办身份.")
		private List<String> taskIdentityList;

		@FieldDescribe("添加的待阅身份.")
		private List<String> readIdentityList;

		@FieldDescribe("添加的参阅身份.")
		private List<String> reviewIdentityList;

		public List<String> getTaskIdentityList() {
			return taskIdentityList;
		}

		public void setTaskIdentityList(List<String> taskIdentityList) {
			this.taskIdentityList = taskIdentityList;
		}

		public List<String> getReadIdentityList() {
			return readIdentityList;
		}

		public void setReadIdentityList(List<String> readIdentityList) {
			this.readIdentityList = readIdentityList;
		}

		public List<String> getReviewIdentityList() {
			return reviewIdentityList;
		}

		public void setReviewIdentityList(List<String> reviewIdentityList) {
			this.reviewIdentityList = reviewIdentityList;
		}

	}

	public Long count(Business business, Work work, String person) throws Exception {
		return business.entityManagerContainer().countEqualAndEqual(Review.class, Review.job_FIELDNAME, work.getJob(),
				Review.person_FIELDNAME, person);
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;
		private Wi wi;

		CallableImpl(String id, Wi wi) {
			this.id = id;
			this.wi = wi;
		}

		public ActionResult<Wo> call() throws Exception {

			ActionResult<Wo> result = new ActionResult<>();

			Wo wo = new Wo();

			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				Business business = new Business(emc);

				Work work = emc.find(id, Work.class);

				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}

				result.setData(wo);

				if (!Objects.equals(ActivityType.manual, work.getActivityType())) {
					throw new ExceptionNotManual(work.getActivity());
				}

				Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);

				if (null == manual) {
					throw new ExceptionEntityNotExist(work.getActivity(), Manual.class);
				}

				List<String> taskIdentities = business.organization().identity().list(wi.getTaskIdentityList());

				taskIdentities = ListUtils.subtract(taskIdentities, work.getManualTaskIdentityMatrix().flat());

				work.setManualTaskIdentityMatrix(manual.identitiesToManualTaskIdentityMatrix(taskIdentities));

//				work.setManualTaskIdentityList(
//						ListUtils.sum(work.getManualTaskIdentityList(), wi.getTaskIdentityList()));

				List<Review> addReviews = new ArrayList<>();
				for (String identity : taskIdentities) {
					String person = business.organization().person().getWithIdentity(identity);
					if (StringUtils.isNotEmpty(person) && (count(business, work, person) < 1)) {
						Review review = new Review(work, person);
						addReviews.add(review);
					}
				}

				emc.beginTransaction(Work.class);

				emc.check(work, CheckPersistType.all);

				if (!addReviews.isEmpty()) {
					emc.beginTransaction(Review.class);
					for (Review review : addReviews) {
						emc.persist(review, CheckPersistType.all);
					}
				}

				emc.commit();

				wo.setTaskIdentityList(taskIdentities);
			}

			return result;
		}
	}

}