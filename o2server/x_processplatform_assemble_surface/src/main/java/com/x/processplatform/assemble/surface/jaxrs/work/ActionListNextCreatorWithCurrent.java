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
import com.x.processplatform.core.entity.content.Work;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListNextCreatorWithCurrent extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNextCreatorWithCurrent.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EqualsTerms equals = new EqualsTerms();
			equals.put(Work.creatorPerson_FIELDNAME, effectivePerson.getDistinguishedName());
			return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, null, null,
					null, null, null, null, true, DESC);
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionListNextCreatorWithCurrent$Wo")
	public static class Wo extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
