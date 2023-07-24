package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.element.Application;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListPrevWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPrevWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String applicationFlag)
			throws Exception {

		LOGGER.debug("execute:{}, id:{}, count:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName,
				() -> id, () -> count, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			EqualsTerms equals = new EqualsTerms();
			equals.put("person", effectivePerson.getDistinguishedName());
			equals.put("application", application.getId());
			return this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, null, null,
					null, null, null, null, true, DESC);
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionListPrevWithApplication$Wo")
	public static class Wo extends ReadCompleted {

		private static final long serialVersionUID = 2279846765261247910L;

		static WrapCopier<ReadCompleted, Wo> copier = WrapCopierFactory.wo(ReadCompleted.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号.")
		@Schema(description = "排序号.")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
