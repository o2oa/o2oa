package com.x.program.center.jaxrs.agent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Agent_;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/* 判断当前用户是否有权限访问 */
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			EntityManager em = emc.get(Agent.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Agent> cq = cb.createQuery(Agent.class);
			Root<Agent> root = cq.from(Agent.class);
			List<Agent> os = em.createQuery(cq.select(root).orderBy(cb.asc(root.get(Agent_.name)))).getResultList();
			wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Agent {

		private static final long serialVersionUID = -8906753148776277888L;

		static WrapCopier<Agent, Wo> copier = WrapCopierFactory.wo(Agent.class, Wo.class,
				ListTools.toList(JpaObject.singularAttributeField(Agent.class, true, true)), null);
	}

}
