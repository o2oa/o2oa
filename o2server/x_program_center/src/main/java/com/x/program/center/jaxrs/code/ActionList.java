package com.x.program.center.jaxrs.code;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.program.center.core.entity.Code;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = Wo.copier.copy(this.list(emc));
			wos = wos.stream().sorted(Comparator.comparing(Wo::getCreateTime)).collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<Code> list(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Code.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Code> cq = cb.createQuery(Code.class);
		Root<Code> root = cq.from(Code.class);
		return em.createQuery(cq.select(root)).getResultList();
	}

	public static class Wo extends Code {

		private static final long serialVersionUID = 4814641801050838505L;

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
