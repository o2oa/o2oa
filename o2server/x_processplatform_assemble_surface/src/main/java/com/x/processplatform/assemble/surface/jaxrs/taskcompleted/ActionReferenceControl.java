package com.x.processplatform.assemble.surface.jaxrs.taskcompleted;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionReferenceControl extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			if (null == taskCompleted) {
				throw new ExceptionEntityNotExist(id, TaskCompleted.class);
			}
			Control control = new JobControlBuilder(effectivePerson, business, taskCompleted.getJob())
					.enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, taskCompleted);
			}
			Wo wo = Wo.copier.copy(taskCompleted);
			for (Work o : business.work().listWithJobObject(taskCompleted.getJob())) {
				WoWork w = WoWork.copier.copy(o);
				w.setControl(new WorkControlBuilder(effectivePerson, business, o).enableAll().build());
				wo.getWorkList().add(w);
			}
			for (WorkCompleted o : business.workCompleted().listWithJobObject(taskCompleted.getJob())) {
				WoWorkCompleted w = WoWorkCompleted.copier.copy(o);
				w.setControl(new WorkCompletedControlBuilder(effectivePerson, business, o).enableAll().build());
				wo.getWorkCompletedList().add(w);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends TaskCompleted {

		private static final long serialVersionUID = -3565951901313513613L;
		static WrapCopier<TaskCompleted, Wo> copier = WrapCopierFactory.wo(TaskCompleted.class, Wo.class,
				JpaObject.singularAttributeField(TaskCompleted.class, true, true), null);

		@FieldDescribe("工作对象")
		private List<WoWork> workList = new ArrayList<>();

		@FieldDescribe("已完成工作对象")
		private List<WoWorkCompleted> workCompletedList = new ArrayList<>();

		public List<WoWork> getWorkList() {
			return workList;
		}

		public void setWorkList(List<WoWork> workList) {
			this.workList = workList;
		}

		public List<WoWorkCompleted> getWorkCompletedList() {
			return workCompletedList;
		}

		public void setWorkCompletedList(List<WoWorkCompleted> workCompletedList) {
			this.workCompletedList = workCompletedList;
		}

	}

	public static class WoWork extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		@FieldDescribe("权限对象")
		private Control control;

		static WrapCopier<Work, WoWork> copier = WrapCopierFactory.wo(Work.class, WoWork.class,
				JpaObject.singularAttributeField(Work.class, true, true), null);

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	public static class WoWorkCompleted extends WorkCompleted {

		private static final long serialVersionUID = 2395048971976018595L;

		static WrapCopier<WorkCompleted, WoWorkCompleted> copier = WrapCopierFactory.wo(WorkCompleted.class,
				WoWorkCompleted.class, JpaObject.singularAttributeField(WorkCompleted.class, true, true), null);

		@FieldDescribe("权限对象")
		private Control control;

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

}