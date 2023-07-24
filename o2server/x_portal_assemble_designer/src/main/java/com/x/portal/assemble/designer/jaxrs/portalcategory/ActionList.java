package com.x.portal.assemble.designer.jaxrs.portalcategory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			List<Wo> allWos = this.countPortCategory(business);
			List<Wo> defaultWos = allWos.stream().filter(o -> (StringUtils.isBlank(o.getPortalCategory()) || Portal.CATEGORY_DEFAULT.equals(o.portalCategory))).collect(Collectors.toList());
			if(defaultWos.size() > 0) {
				Wo wo = new Wo();
				wo.setPortalCategory(Portal.CATEGORY_DEFAULT);
				wo.setCount(defaultWos.stream().collect(Collectors.summingLong(Wo::getCount)));
				wos.add(wo);
			}
			wos.addAll(allWos);
			wos.removeAll(defaultWos);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> countPortCategory(Business business) throws Exception{
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Wo> cq = cb.createQuery(Wo.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		Path<String> path = root.get(Portal_.portalCategory);
		cq.multiselect(path, cb.count(root)).where(p).groupBy(path).orderBy(cb.asc(path));
		return em.createQuery(cq).getResultList();
	}

	private List<String> listPortalCategory(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		return em.createQuery(cq.select(root.get(Portal_.portalCategory))).getResultList();
	}

	public static class Wo extends GsonPropertyObject {
		public Wo(){}

		public Wo(String date, Long count){
			this.portalCategory = date;
			this.count = count;
		}

		@FieldDescribe("门户分类")
		private String portalCategory;

		@FieldDescribe("数量")
		private Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public String getPortalCategory() {
			return portalCategory;
		}

		public void setPortalCategory(String portalCategory) {
			this.portalCategory = portalCategory;
		}

	}

}
