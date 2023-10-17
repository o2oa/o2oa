package com.x.organization.assemble.control.jaxrs.unitduty;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ActionListLike extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getKey(), StringUtils.join(wi.getUnitList(), ","));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.listLike(business, wi);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索关键字")
		private String key;
		@FieldDescribe("搜索组织范围,为空则不限定")
		private List<String> unitList = new ArrayList<>();

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}
	}

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = 2747652629603650333L;

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

	}

	private List<Wo> listLike(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isBlank(wi.getKey())) {
			return wos;
		}
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.conjunction();
		p = cb.and(p, cb.or(cb.like(cb.lower(root.get(UnitDuty_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR),
				cb.like(cb.lower(root.get(UnitDuty_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR),
				cb.like(cb.lower(root.get(UnitDuty_.pinyin)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR),
				cb.like(cb.lower(root.get(UnitDuty_.pinyinInitial)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR)));
		if (ListTools.isNotEmpty(wi.getUnitList())) {
			List<String> units = business.expendUnitToUnit(wi.getUnitList());
			if (!units.isEmpty()) {
				p = cb.and(p, root.get(UnitDuty_.unit).in(units));
			}
		}
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
		wos = Wo.copier.copy(os);
		wos = business.unitDuty().sort(wos);
		return wos;
	}

}
