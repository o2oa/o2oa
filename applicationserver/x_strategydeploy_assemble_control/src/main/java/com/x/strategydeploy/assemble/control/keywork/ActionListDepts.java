package com.x.strategydeploy.assemble.control.keywork;

import java.util.ArrayList;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.unit.UnitFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.MeasuresInfo_;

public class ActionListDepts extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListDepts.class);

	public static class Wo extends Unit {

	}

	protected List<Unit> execute(HttpServletRequest request, EffectivePerson effectivePersonyear) throws Exception {
		List<Unit> units = new ArrayList<Unit>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EntityManager em = business.entityManagerContainer().get(MeasuresInfo.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MeasuresInfo> cq = cb.createQuery(MeasuresInfo.class);
			Root<MeasuresInfo> root = cq.from(MeasuresInfo.class);
			Predicate p = cb.isNotEmpty(root.get(MeasuresInfo_.deptlist));
			List<MeasuresInfo> os = em.createQuery(cq.select(root).where(p)).getResultList();
			List<String> list = new ArrayList<>();
			
			List<String> units_and_supunits = getUnitsAndSupUnitsByPerson(effectivePersonyear, business);
			
			for (MeasuresInfo measuresInfo : os) {
				if (ListTools.isNotEmpty(measuresInfo.getDeptlist())) {
					List<String> _tmplist = measuresInfo.getDeptlist();
					//取组织交集
					_tmplist.retainAll(units_and_supunits);
					//list.addAll(measuresInfo.getDeptlist());
					list.addAll(_tmplist);
				}
			}

			list = list.stream().filter(o -> !StringUtils.isEmpty(o)).distinct().sorted().collect(Collectors.toList());
			Unit unit = new Unit();
			units = business.organization().unit().listObject(list);
			return units;
		} catch (Exception e) {
			throw e;
		}

	}

	public List<String> getUnitsAndSupUnitsByPerson(EffectivePerson effectivePerson, Business business) throws Exception {
		UnitFactory unitfactory;
		unitfactory = business.organization().unit();
		List<String> units = unitfactory.listWithPerson(effectivePerson);
		List<String> sup_units = unitfactory.listWithPersonSupNested(effectivePerson);
		units.addAll(sup_units);
		return units;
	}
}
