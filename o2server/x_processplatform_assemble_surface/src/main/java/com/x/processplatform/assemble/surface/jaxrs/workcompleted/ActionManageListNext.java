package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;

class ActionManageListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			/** 避免后面输出空值 */
			result.setData(new ArrayList<Wo>());
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			if (effectivePerson.isManager()
					|| business.organization().person().hasRole(effectivePerson,
							OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager)
					|| effectivePerson.isPerson(application.getControllerList())) {
				EqualsTerms equalsTerms = new EqualsTerms();
				equalsTerms.put("application", application.getId());
				result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equalsTerms, null,
						null, null, null, null, null, null, true, DESC);
			} else {
				List<String> ids = business.process().listControllableProcess(effectivePerson, application);
				if (ListTools.isNotEmpty(ids)) {
					InTerms inTerms = new InTerms();
					inTerms.put("process", ids);
					result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null,
							inTerms, null, null, null, null, true, DESC);
				}
			}
			/* 添加权限 */
			if (null != result.getData()) {
				for (Wo wo : result.getData()) {
					WorkCompleted o = emc.find(wo.getId(), WorkCompleted.class);
					wo.setControl(new WorkCompletedControlBuilder(effectivePerson, business, o).enableAll().build());
				}
			}
			return result;
		}
	}

	public static class Wo extends WorkCompleted {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<WorkCompleted, Wo> copier = WrapCopierFactory.wo(WorkCompleted.class, Wo.class, null,
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
