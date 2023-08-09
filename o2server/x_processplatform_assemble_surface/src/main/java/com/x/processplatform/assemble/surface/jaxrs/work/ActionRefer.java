package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;

class ActionRefer extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRefer.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId) throws Exception {

		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		CompletableFuture<Void> futureTask = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Task> os = emc.listEqualAndEqual(Task.class, Task.work_FIELDNAME, workId, Task.person_FIELDNAME,
						effectivePerson.getDistinguishedName());
				wo.getTaskList().addAll(WoTask.copier.copy(os));
				wo.setHasTask(!os.isEmpty());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureTaskCompleted = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<TaskCompleted> os = emc.listEqualAndEqual(TaskCompleted.class, TaskCompleted.work_FIELDNAME,
						workId, TaskCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
				wo.getTaskCompletedList().addAll(WoTaskCompleted.copier.copy(os));
				wo.setHasTaskCompleted(!os.isEmpty());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureRead = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Read> os = emc.listEqualAndEqual(Read.class, Read.work_FIELDNAME, workId, Read.person_FIELDNAME,
						effectivePerson.getDistinguishedName());
				wo.getReadList().addAll(WoRead.copier.copy(os));
				wo.setHasRead(!os.isEmpty());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureReadCompleted = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<ReadCompleted> os = emc.listEqualAndEqual(ReadCompleted.class, ReadCompleted.work_FIELDNAME,
						workId, ReadCompleted.person_FIELDNAME, effectivePerson.getDistinguishedName());
				wo.getReadCompletedList().addAll(WoReadCompleted.copier.copy(os));
				wo.setHasReadCompleted(!os.isEmpty());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureReview = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Review> os = emc.listEqualAndEqual(Review.class, Review.work_FIELDNAME, workId,
						Review.person_FIELDNAME, effectivePerson.getDistinguishedName());
				wo.getReviewList().addAll(WoReview.copier.copy(os));
				wo.setHasReview(!os.isEmpty());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		});
		futureTask.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureTaskCompleted.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureRead.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureReadCompleted.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureReview.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		result.setData(wo);

		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6231785082740620916L;

		@FieldDescribe("待办列表")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("已办列表")
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		@FieldDescribe("待阅列表")
		private List<Read> readList = new ArrayList<>();

		@FieldDescribe("已阅列表")
		private List<ReadCompleted> readCompletedList = new ArrayList<>();

		@FieldDescribe("待阅列表")
		private List<Review> reviewList = new ArrayList<>();

		@FieldDescribe("是否有待办")
		private Boolean hasTask = false;

		@FieldDescribe("是否有已办")
		private Boolean hasTaskCompleted = false;

		@FieldDescribe("是否有待阅")
		private Boolean hasRead = false;

		@FieldDescribe("是否有已阅")
		private Boolean hasReadCompleted = false;

		@FieldDescribe("是否有待阅")
		private Boolean hasReview = false;

		public Boolean getHasTask() {
			return hasTask;
		}

		public void setHasTask(Boolean hasTask) {
			this.hasTask = hasTask;
		}

		public Boolean getHasTaskCompleted() {
			return hasTaskCompleted;
		}

		public void setHasTaskCompleted(Boolean hasTaskCompleted) {
			this.hasTaskCompleted = hasTaskCompleted;
		}

		public Boolean getHasRead() {
			return hasRead;
		}

		public void setHasRead(Boolean hasRead) {
			this.hasRead = hasRead;
		}

		public Boolean getHasReadCompleted() {
			return hasReadCompleted;
		}

		public void setHasReadCompleted(Boolean hasReadCompleted) {
			this.hasReadCompleted = hasReadCompleted;
		}

		public Boolean getHasReview() {
			return hasReview;
		}

		public void setHasReview(Boolean hasReview) {
			this.hasReview = hasReview;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<Read> getReadList() {
			return readList;
		}

		public void setReadList(List<Read> readList) {
			this.readList = readList;
		}

		public List<ReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<ReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

		public List<Review> getReviewList() {
			return reviewList;
		}

		public void setReviewList(List<Review> reviewList) {
			this.reviewList = reviewList;
		}
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = -2782720476210565770L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -2960668290774153792L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -187062961012243676L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 6368046584961145277L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = -4006873185105270169L;

		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class, null,
				JpaObject.FieldsInvisible);

	}

}