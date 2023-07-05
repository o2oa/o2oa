package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;

class ActionListNextWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNextWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag)
			throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> id, () -> count, () -> applicationFlag);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			EqualsTerms equals = new EqualsTerms();
			equals.put("creatorPerson", effectivePerson.getDistinguishedName());
			equals.put("application", application.getId());
			ActionResult<List<Wo>> result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME,
					equals, null, null, null, null, null, null, null, true, DESC);
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

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null,
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
