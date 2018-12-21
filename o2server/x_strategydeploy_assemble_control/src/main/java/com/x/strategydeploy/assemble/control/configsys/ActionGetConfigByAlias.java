package com.x.strategydeploy.assemble.control.configsys;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyConfigSys;
import com.x.strategydeploy.core.entity.StrategyConfigSys_;

public class ActionGetConfigByAlias extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionGetConfigByAlias.class);

	protected List<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String _alias) throws Exception {
		List<Wo> result = new ArrayList<Wo>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(StrategyConfigSys.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StrategyConfigSys> cq = cb.createQuery(StrategyConfigSys.class);
			Root<StrategyConfigSys> root = cq.from(StrategyConfigSys.class);
			Predicate p = cb.isNotNull(root.get(StrategyConfigSys_.title));
			p = cb.and(cb.equal(root.get(StrategyConfigSys_.alias), _alias));
			List<StrategyConfigSys> origs = em.createQuery(cq.select(root).where(p)).getResultList();
			result = Wo.copyToWo.copy(origs);
			return result;
		} catch (Exception e) {
			throw e;
		}

	}
}
