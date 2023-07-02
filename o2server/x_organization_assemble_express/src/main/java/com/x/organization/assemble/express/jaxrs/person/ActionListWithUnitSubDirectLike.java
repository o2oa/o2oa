package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Unit;

class ActionListWithUnitSubDirectLike extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getUnitList(), wi.getKey());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.list(business, wi, this.people(business, wi));
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("关键字")
		private String key;

		@FieldDescribe("组织")
		private List<String> unitList = new ArrayList<>();

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	public static class Wo extends WoPersonListAbstract {

	}

	private Wo list(Business business, Wi wi, List<String> ids) throws Exception {
		Wo wo = new Wo();
		if(StringUtils.isBlank(wi.getKey()) &&
				(ListTools.isEmpty(wi.getUnitList()) || ListTools.isEmpty(ids))){
			return wo;
		}
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.like(cb.lower(root.get(Person_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
		p = cb.or(p, cb.like(cb.lower(root.get(Person_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
		p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyin)), str + "%", StringTools.SQL_ESCAPE_CHAR));
		p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyinInitial)), str + "%", StringTools.SQL_ESCAPE_CHAR));
		p = cb.or(p, cb.like(cb.lower(root.get(Person_.mobile)), str + "%", StringTools.SQL_ESCAPE_CHAR));
		p = cb.or(p, cb.like(cb.lower(root.get(Person_.distinguishedName)), str + "%", StringTools.SQL_ESCAPE_CHAR));
		p = cb.and(p, cb.isMember(root.get(Person_.id), cb.literal(ids)));
		List<String> list = em.createQuery(cq.select(root.get(Person_.id)).where(p))
				.getResultList().stream().distinct().collect(Collectors.toList());
		List<String> values = business.person().listPersonDistinguishedNameSorted(list);
		wo.getPersonList().addAll(values);
		return wo;
	}

	private List<String> people(Business business, Wi wi) throws Exception {
		List<String> list = new ArrayList<>();
		if(ListTools.isNotEmpty(wi.getUnitList())) {
			List<Unit> os = business.unit().pick(wi.getUnitList());
			List<String> unitIds = ListTools.extractField(os, Unit.id_FIELDNAME, String.class, true, true);
			EntityManager em = business.entityManagerContainer().get(Identity.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Identity> root = cq.from(Identity.class);
			Predicate p = root.get(Identity_.unit).in(unitIds);
			list = em.createQuery(cq.select(root.get(Identity_.person)).where(p))
					.getResultList().stream().distinct().collect(Collectors.toList());
		}
		return list;
	}

}
