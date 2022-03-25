package com.x.program.center.jaxrs.code;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.Code;

class ActionListPaging extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(Code.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(Code.class, Wo.copier, p, page, size, Code.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Code.class, p));
			return result;
		}
	}

	public static class Wo extends Code {

		private static final long serialVersionUID = 9141971868817571577L;

		static WrapCopier<Code, Wo> copier = WrapCopierFactory.wo(Code.class, Wo.class, null,
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
