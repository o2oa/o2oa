package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkCompletedControl;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

/**
 * 
 * 在管理界面下获取相关联的所有信息,需要control权限,同时需要添加一个permission
 */
class ActionManageGetAssignment extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManageGetAssignment.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			Process process = business.process().pick(workCompleted.getProcess());
			Application application = business.application().pick(workCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (!business.canManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			WoControl control = business.getControl(effectivePerson, workCompleted, WoControl.class);
			wo.setControl(control);

			CompletableFuture<Void> future_taskCompleted = CompletableFuture.runAsync(() -> {
				try {
					emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(TaskCompleted::getStartTime,
									Comparator.nullsLast(Date::compareTo)))
							.forEach(o -> {
								try {
									WoTaskCompleted w = WoTaskCompleted.copier.copy(o);
									w.setControl(control);
									wo.getTaskCompletedList().add(w);
								} catch (Exception e) {
									logger.error(e);
								}
							});
				} catch (Exception e) {
					logger.error(e);
				}
			});
			CompletableFuture<Void> future_read = CompletableFuture.runAsync(() -> {
				try {
					emc.listEqual(Read.class, Read.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Read::getStartTime, Comparator.nullsLast(Date::compareTo)))
							.forEach(o -> {
								try {
									WoRead w = WoRead.copier.copy(o);
									w.setControl(control);
									wo.getReadList().add(w);
								} catch (Exception e) {
									logger.error(e);
								}
							});
				} catch (Exception e) {
					logger.error(e);
				}
			});
			CompletableFuture<Void> future_readCompleted = CompletableFuture.runAsync(() -> {
				try {
					emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(ReadCompleted::getStartTime,
									Comparator.nullsLast(Date::compareTo)))
							.forEach(o -> {
								try {
									WoReadCompleted w = WoReadCompleted.copier.copy(o);
									w.setControl(control);
									wo.getReadCompletedList().add(w);
								} catch (Exception e) {
									logger.error(e);
								}
							});
				} catch (Exception e) {
					logger.error(e);
				}
			});
			CompletableFuture<Void> future_review = CompletableFuture.runAsync(() -> {
				try {
					emc.listEqual(Review.class, Review.job_FIELDNAME, workCompleted.getJob()).stream()
							.sorted(Comparator.comparing(Review::getStartTime, Comparator.nullsLast(Date::compareTo)))
							.forEach(o -> {
								try {
									WoReview w = WoReview.copier.copy(o);
									w.setControl(control);
									wo.getReviewList().add(w);
								} catch (Exception e) {
									logger.error(e);
								}
							});
				} catch (Exception e) {
					logger.error(e);
				}
			});
			future_taskCompleted.get(300, TimeUnit.SECONDS);
			future_read.get(300, TimeUnit.SECONDS);
			future_readCompleted.get(300, TimeUnit.SECONDS);
			future_review.get(300, TimeUnit.SECONDS);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoTaskCompleted> taskCompletedList = new ArrayList<>();
		private List<WoRead> readList = new ArrayList<>();
		private List<WoReadCompleted> readCompletedList = new ArrayList<>();
		private List<WoReview> reviewList = new ArrayList<>();
		private WoControl control;

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

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -5707845125641375013L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);

		private WoControl control;

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
	}

	public static class WoRead extends Read {

		private static final long serialVersionUID = -5258254759835923175L;

		static WrapCopier<Read, WoRead> copier = WrapCopierFactory.wo(Read.class, WoRead.class, null,
				JpaObject.FieldsInvisible);
		private WoControl control;

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
	}

	public static class WoReadCompleted extends ReadCompleted {

		private static final long serialVersionUID = 8123512298634072415L;

		static WrapCopier<ReadCompleted, WoReadCompleted> copier = WrapCopierFactory.wo(ReadCompleted.class,
				WoReadCompleted.class, null, JpaObject.FieldsInvisible);
		private WoControl control;

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
	}

	public static class WoReview extends Review {

		private static final long serialVersionUID = -452279135320456110L;

		static WrapCopier<Review, WoReview> copier = WrapCopierFactory.wo(Review.class, WoReview.class, null,
				JpaObject.FieldsInvisible);

		private WoControl control;

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
	}

	public static class WoControl extends WorkCompletedControl {
	}

}