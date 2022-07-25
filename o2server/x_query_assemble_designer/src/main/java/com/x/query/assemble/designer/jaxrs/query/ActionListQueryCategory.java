package com.x.query.assemble.designer.jaxrs.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Query_;

class ActionListQueryCategory extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListQueryCategory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.QueryManager, OrganizationDefinition.QueryCreator))) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			List<String> os = this.list(business);

			Map<String, Long> map = os.stream().collect(Collectors.groupingBy(o -> {
				return StringUtils.trimToEmpty(o);
			}, Collectors.counting()));
			for (Entry<String, Long> en : map.entrySet()) {
				Wo wo = new Wo();
				wo.setName(en.getKey());
				wo.setCount(en.getValue());
				wos.add(wo);
			}
			wos = wos.stream().sorted(Comparator.comparing(Wo::getName, StringTools.emptyLastComparator()))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<String> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Query.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Query> root = cq.from(Query.class);
		List<String> os = em.createQuery(cq.select(root.get(Query_.queryCategory))).getResultList();
		return os;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4755476029209732172L;

		private String name;
		private Long count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}

}