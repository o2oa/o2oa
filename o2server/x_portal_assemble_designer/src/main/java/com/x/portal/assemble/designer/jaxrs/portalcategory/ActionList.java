package com.x.portal.assemble.designer.jaxrs.portalcategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			List<String> list = new ArrayList<>(this.listPortalCategory(business, effectivePerson));
			Map<String, Long> counted = list.stream()
					.collect(Collectors.groupingBy(o -> Objects.toString(o), Collectors.counting()));
			LinkedHashMap<String, Long> sorted = counted.entrySet().stream()
					.sorted(Map.Entry.<String, Long>comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
							Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			for (Entry<String, Long> en : sorted.entrySet()) {
				Wo wo = new Wo();
				wo.setProtalCategory(en.getKey());
				wo.setCount(en.getValue());
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	private List<String> listPortalCategory(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		return em.createQuery(cq.select(root.get(Portal_.portalCategory))).getResultList();
	}

	public class Wo extends GsonPropertyObject {

		@FieldDescribe("门户分类")
		private String protalCategory;
		@FieldDescribe("数量")
		private Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public String getProtalCategory() {
			return protalCategory;
		}

		public void setProtalCategory(String protalCategory) {
			this.protalCategory = protalCategory;
		}
	}

}