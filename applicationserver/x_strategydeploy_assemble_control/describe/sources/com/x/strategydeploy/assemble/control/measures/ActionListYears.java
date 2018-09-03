package com.x.strategydeploy.assemble.control.measures;

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
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo_;

public class ActionListYears extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(ActionListYears.class);

	public static class Wo extends WrapStringList {

	}

	protected Wo execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(MeasuresInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
			Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
			Predicate p = cb.isNotNull(root.get(MeasuresInfo_.measuresinfoyear));
			cq.select(root).where(p);
			List<MeasuresInfo> objs = em.createQuery(cq).getResultList();
			List<String> list = new ArrayList<>();
			for (MeasuresInfo measuresinfo : objs) {
				if (StringUtils.isNotBlank(measuresinfo.getMeasuresinfoyear())) {
					list.add(measuresinfo.getMeasuresinfoyear());
				}
			}
			//自然序逆序元素，使用Comparator 提供的reverseOrder() 方法
			list = list.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			Wo wo = new Wo();
			wo.setValueList(list);
			return wo;
		} catch (Exception e) {
			throw e;
		}

	}
}
