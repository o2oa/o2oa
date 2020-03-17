package com.x.processplatform.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;

class ActionManualAppendIdentity extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

		Wo wo = new Wo();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

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

					ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", work.getId(), "processing"), null, work.getJob());

					result.setData(wo);

					if (!Objects.equals(ActivityType.manual, work.getActivityType())) {
						throw new ExceptionNotManual(work.getActivity());
					}

					List<String> taskIdentities = business.organization().identity().list(wi.getTaskIdentityList());

					taskIdentities = ListUtils.subtract(taskIdentities, work.getManualTaskIdentityList());

					work.setManualTaskIdentityList(
							ListUtils.sum(work.getManualTaskIdentityList(), wi.getTaskIdentityList()));

					List<Review> addReviews = new ArrayList<>();
					for (String identity : taskIdentities) {
						String person = business.organization().person().getWithIdentity(identity);
						if (StringUtils.isNotEmpty(person)) {
							if (count(business, work, person) < 1) {
								Review review = new Review(work, person);
								addReviews.add(review);
							}
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

				return "";
			}
		};
		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

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

}