package com.x.strategydeploy.assemble.control.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.StrategyDeploy;
import com.x.strategydeploy.core.entity.StrategyDeploy_;

public class ActionListYears extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListYears.class);

	public static class Wo extends WrapStringList {

	}

	protected Wo execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(StrategyDeploy.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StrategyDeploy> cq = cb.createQuery(StrategyDeploy.class);
			Root<StrategyDeploy> root = cq.from(StrategyDeploy.class);
			Predicate p = cb.isNotNull(root.get(StrategyDeploy_.strategydeployyear));
			// cq.select(root).where(p).groupBy(root.get(StrategyDeploy_.strategydeployyear));
			cq.select(root).where(p);
			List<StrategyDeploy> objs = em.createQuery(cq).getResultList();
			List<String> list = new ArrayList<>();
			for (StrategyDeploy strategydeploy : objs) {
				if (StringUtils.isNotBlank(strategydeploy.getStrategydeployyear())) {
					list.add(strategydeploy.getStrategydeployyear());
				}
			}
			// 自然序逆序元素，使用Comparator 提供的reverseOrder() 方法
			list = list.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted(Comparator.reverseOrder())
					.collect(Collectors.toList());
			Wo wo = new Wo();
			wo.setValueList(list);
			return wo;
		} catch (Exception e) {
			throw e;
		}

	}
}
