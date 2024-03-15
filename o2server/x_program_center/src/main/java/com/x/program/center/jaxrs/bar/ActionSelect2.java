package com.x.program.center.jaxrs.bar;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.validation.Bar;

class ActionSelect2 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSelect2.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer count) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = Wo.copier.copy(select(count));
		result.setData(wos);
		return result;
	}

	private List<Bar> select(Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Bar.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Bar> cq = cb.createQuery(Bar.class);
			Root<Bar> root = cq.from(Bar.class);
			// cq.select(root).orderBy(cb.asc(root.get(Bar_.name)));
			cq.select(root);
			return em.createQuery(cq).setMaxResults(count).getResultList();
		}
	}

	public static class Wo extends Bar {

		static WrapCopier<Bar, Wo> copier = WrapCopierFactory.wo(Bar.class, Wo.class, null,
				Arrays.asList(Bar.SL1_FIELDNAME, Bar.SL2_FIELDNAME, Bar.SL3_FIELDNAME, Bar.SL4_FIELDNAME));

		private static final long serialVersionUID = 1636014466988591350L;

	}

}
