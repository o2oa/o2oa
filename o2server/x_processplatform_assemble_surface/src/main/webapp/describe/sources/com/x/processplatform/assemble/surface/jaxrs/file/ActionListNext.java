package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.processplatform.core.entity.content.Draft;

class ActionListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		EqualsTerms equals = new EqualsTerms();
		equals.put(Draft.person_FIELDNAME, effectivePerson.getDistinguishedName());
		return this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, equals, null, null, null, null,
				null, null, null, true, DESC);
	}

	public static class Wo extends Draft {

		private static final long serialVersionUID = 2279846765261247910L;
		
		static WrapCopier<Draft, Wo> copier = WrapCopierFactory.wo(Draft.class, Wo.class,
				JpaObject.singularAttributeField(Draft.class, true, true), null);

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
