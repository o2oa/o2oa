package com.x.strategydeploy.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.strategydeploy.assemble.control.AbstractFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyConfigSys;
import com.x.strategydeploy.core.entity.StrategyConfigSys_;

public class ConfigFactory extends AbstractFactory {

	public ConfigFactory(Business business) throws Exception {
		super(business);
		// TODO Auto-generated constructor stub
	}

	public List<StrategyConfigSys> listByAlias(String _alias) throws Exception {
		List<StrategyConfigSys> configlist = new ArrayList<StrategyConfigSys>();
		EntityManager em = this.entityManagerContainer().get(StrategyConfigSys.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StrategyConfigSys> cq = cb.createQuery(StrategyConfigSys.class);
		Root<StrategyConfigSys> root = cq.from(StrategyConfigSys.class);
		Predicate p = cb.isNotNull(root.get(StrategyConfigSys_.title));
		p = cb.and(cb.equal(root.get(StrategyConfigSys_.alias), _alias));
		configlist = em.createQuery(cq.select(root).where(p)).getResultList();
		return configlist;
	}

}
