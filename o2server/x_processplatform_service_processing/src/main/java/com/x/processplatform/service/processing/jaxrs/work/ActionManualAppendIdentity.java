package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.ListUtils;

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
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.service.processing.Business;

class ActionManualAppendIdentity extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);

			ActionResult<Wo> result = new ActionResult<>();

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Work work = emc.find(id, Work.class);

			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}

			if (!Objects.equals(ActivityType.manual, work.getActivityType())) {
				throw new ExceptionNotManual(work.getActivity());
			}

			List<String> taskIdentities = business.organization().identity().list(wi.getTaskIdentityList());

			taskIdentities = ListUtils.subtract(taskIdentities, work.getManualTaskIdentityList());

			work.setManualTaskIdentityList(ListUtils.sum(work.getManualTaskIdentityList(), wi.getTaskIdentityList()));

			emc.beginTransaction(Work.class);

			emc.check(work, CheckPersistType.all);

			emc.commit();

			Wo wo = new Wo();

			wo.setTaskIdentityList(taskIdentities);

			result.setData(wo);

			return result;
		}
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

}