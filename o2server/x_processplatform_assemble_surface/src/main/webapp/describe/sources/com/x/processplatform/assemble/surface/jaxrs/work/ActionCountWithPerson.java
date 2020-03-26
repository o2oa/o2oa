package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;

class ActionCountWithPerson extends ActionComplex {

	private static Logger logger = LoggerFactory.getLogger(ActionCountWithPerson.class);

	ActionResult<Wo> execute(String credential) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			String person = business.organization().person().get(credential);
			if (StringUtils.isNotEmpty(person)) {
				CompletableFuture<Void> future_task = CompletableFuture.runAsync(() -> {
					try {
						wo.setTask(emc.countEqual(Task.class, Task.person_FIELDNAME, person));
					} catch (Exception e) {
						logger.error(e);
					}
				});
				/* 已办仅取latest */
				CompletableFuture<Void> future_taskCompleted = CompletableFuture.runAsync(() -> {
					try {
						wo.setTaskCompleted(emc.countEqualAndNotEqual(TaskCompleted.class,
								TaskCompleted.person_FIELDNAME, person, TaskCompleted.latest_FIELDNAME, false));
					} catch (Exception e) {
						logger.error(e);
					}
				});
				CompletableFuture<Void> future_read = CompletableFuture.runAsync(() -> {
					try {
						wo.setRead(emc.countEqual(Read.class, Read.person_FIELDNAME, person));
					} catch (Exception e) {
						logger.error(e);
					}
				});
				CompletableFuture<Void> future_readCompleted = CompletableFuture.runAsync(() -> {
					try {
						wo.setReadCompleted(
								emc.countEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME, person));
					} catch (Exception e) {
						logger.error(e);
					}
				});
				CompletableFuture<Void> future_review = CompletableFuture.runAsync(() -> {
					try {
						wo.setReview(emc.countEqual(Review.class, Review.person_FIELDNAME, person));
					} catch (Exception e) {
						logger.error(e);
					}
				});
				future_task.get(300, TimeUnit.SECONDS);
				future_taskCompleted.get(300, TimeUnit.SECONDS);
				future_read.get(300, TimeUnit.SECONDS);
				future_readCompleted.get(300, TimeUnit.SECONDS);
				future_review.get(300, TimeUnit.SECONDS);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("待办数量")
		private Long task = 0L;
		@FieldDescribe("已办数量")
		private Long taskCompleted = 0L;
		@FieldDescribe("待阅数量")
		private Long read = 0L;
		@FieldDescribe("已阅数量")
		private Long readCompleted = 0L;
		@FieldDescribe("待阅数量")
		private Long review = 0L;

		public Long getTask() {
			return task;
		}

		public void setTask(Long task) {
			this.task = task;
		}

		public Long getTaskCompleted() {
			return taskCompleted;
		}

		public void setTaskCompleted(Long taskCompleted) {
			this.taskCompleted = taskCompleted;
		}

		public Long getRead() {
			return read;
		}

		public void setRead(Long read) {
			this.read = read;
		}

		public Long getReadCompleted() {
			return readCompleted;
		}

		public void setReadCompleted(Long readCompleted) {
			this.readCompleted = readCompleted;
		}

		public Long getReview() {
			return review;
		}

		public void setReview(Long review) {
			this.review = review;
		}

	}

}