package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

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
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

/**
 * 在应用管理界面下获取一个WorkCompleted的内容,.同时需要添加一个permission. 权限:需要至少process的管理权限.
 */
class ActionManageGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			Process process = business.process().pick(workCompleted.getProcess());
			Application application = business.application().pick(workCompleted.getApplication());
			// 需要对这个应用的管理权限
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(workCompleted);
			/* 添加权限 */
			wo.setControl(new WorkCompletedControlBuilder(effectivePerson, business, workCompleted).enableAll().build());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WorkCompleted {

		private static final long serialVersionUID = 2395048971976018595L;

		static WrapCopier<WorkCompleted, Wo> copier = WrapCopierFactory.wo(WorkCompleted.class, Wo.class, null,
				JpaObject.FieldsInvisible);

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
