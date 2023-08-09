package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageGetAssignment extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageGetAssignment.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			// Process 也可能为空
			Process process = business.process().pick(work.getProcess());
			Application application = business.application().pick(work.getApplication());
			// 需要对这个应用的管理权限
			if (BooleanUtils
					.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			wo.setControl(new WorkControlBuilder(effectivePerson, business, work).enableAll().build());
		}

		final String job = work.getJob();
		CompletableFuture<Void> futureTask = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.listEqual(Task.class, Task.work_FIELDNAME, id).stream()
						.sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.map(WoTask.copier::copy).forEach(wo.getTaskList()::add);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureTaskCompleted = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.listEqual(TaskCompleted.class, TaskCompleted.work_FIELDNAME, id).stream().sorted(
						Comparator.comparing(TaskCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								WoTaskCompleted w = WoTaskCompleted.copier.copy(o);
								wo.getTaskCompletedList().add(w);
							} catch (Exception e) {
								LOGGER.error(e);
							}
						});
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureRead = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.listEqual(Read.class, Read.work_FIELDNAME, id).stream()
						.sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								WoRead w = WoRead.copier.copy(o);
								wo.getReadList().add(w);
							} catch (Exception e) {
								LOGGER.error(e);
							}
						});
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureReadCompleted = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.listEqual(ReadCompleted.class, ReadCompleted.work_FIELDNAME, id).stream().sorted(
						Comparator.comparing(ReadCompleted::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								WoReadCompleted w = WoReadCompleted.copier.copy(o);
								wo.getReadCompletedList().add(w);
							} catch (Exception e) {
								LOGGER.error(e);
							}
						});
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		CompletableFuture<Void> futureReview = CompletableFuture.runAsync(() -> {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				emc.listEqual(Review.class, Review.job_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(Review::getStartTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								WoReview w = WoReview.copier.copy(o);
								wo.getReviewList().add(w);
							} catch (Exception e) {
								LOGGER.error(e);
							}
						});
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}, ThisApplication.forkJoinPool());
		futureTask.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureReview.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureTaskCompleted.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureRead.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		futureReadCompleted.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -6847443228032059363L;

		@FieldDescribe("待办对象")
		private List<WoTask> taskList = new ArrayList<>();

		@FieldDescribe("已办对象")
		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();

		@FieldDescribe("待阅对象")
		private List<WoRead> readList = new ArrayList<>();

		@FieldDescribe("已阅对象")
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();

		@FieldDescribe("参阅对象")
		private List<WoReview> reviewList = new ArrayList<>();

		@FieldDescribe("权限")
		private Control control;

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

		public List<WoRead> getReadList() {
			return readList;
		}

		public void setReadList(List<WoRead> readList) {
			this.readList = readList;
		}

		public List<WoReadCompleted> getReadCompletedList() {
			return readCompletedList;
		}

		public void setReadCompletedList(List<WoReadCompleted> readCompletedList) {
			this.readCompletedList = readCompletedList;
		}

		public List<WoReview> getReviewList() {
			return reviewList;
		}

		public void setReviewList(List<WoReview> reviewList) {
			this.reviewList = reviewList;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -8067704098385000667L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = -1305610937955675829L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = 2697843292828496041L;

		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

}
