package com.x.organization.assemble.authentication.jaxrs.bind;

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
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Bind_;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<Wo> wos = Wo.copier.copy(emc.list(Bind.class, this.list(emc)));
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<String> list(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Bind.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Bind> root = cq.from(Bind.class);
		cq.select(root.get(Bind_.id));
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends Bind {

		private static final long serialVersionUID = -3574645735233129236L;

		static WrapCopier<Bind, Wo> copier = WrapCopierFactory.wo(Bind.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private Long rank;

		private String image;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

	}
}
