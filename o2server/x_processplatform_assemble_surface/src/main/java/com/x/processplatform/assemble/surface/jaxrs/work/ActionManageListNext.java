package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;

class ActionManageListNext extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListNext.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag)
			throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> id, () -> count, () -> applicationFlag);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			result.setData(new ArrayList<Wo>());
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			if (BooleanUtils.isTrue(effectivePerson.isManager())
					|| BooleanUtils.isTrue(business.organization().person().hasRole(effectivePerson,
							OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager))
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
			/** 添加权限 */
			if (null != result.getData()) {
				for (Wo wo : result.getData()) {
					Work o = emc.find(wo.getId(), Work.class);
					wo.setControl(new WorkControlBuilder(effectivePerson, business, o).enableAll().build());
				}
			}
			return result;
		}
	}

	public static class Wo extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null, Excludes);

		@FieldDescribe("排序号")
		private Long rank;

		@FieldDescribe("权限")
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
