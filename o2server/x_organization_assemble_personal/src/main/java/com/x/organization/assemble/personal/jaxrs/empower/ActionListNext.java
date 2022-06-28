package com.x.organization.assemble.personal.jaxrs.empower;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.accredit.Empower;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListNext extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNext.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {

		LOGGER.debug("execute:{}, id:{}, count:{}.", effectivePerson::getDistinguishedName, () -> id, () -> count);

		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null, null, null,
				null, null, null, true, DESC);
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.empower.ActionListNext$Wo")
	public static class Wo extends Empower {

		private static final long serialVersionUID = -7495725325510376323L;

		static WrapCopier<Empower, Wo> copier = WrapCopierFactory.wo(Empower.class, Wo.class,
				JpaObject.singularAttributeField(Empower.class, true, true), null);

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
