package com.x.organization.assemble.control.jaxrs.unit;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import net.sf.ehcache.Element;

class ActionListLike extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), wi.getKey(), wi.getType(),
					StringUtils.join(wi.getUnitList(), ","));
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(effectivePerson, business, wi);
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索关键字")
		private String key;
		@FieldDescribe("搜索组织范围,为空则不限定")
		private List<String> unitList = new ArrayList<>();
		@FieldDescribe("组织类型")
		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("递归上级组织对象")
		private List<Wo> woSupNestedUnitList;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
		private Long subDirectIdentityCount = 0L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public List<Wo> getWoSupNestedUnitList() {
			return woSupNestedUnitList;
		}

		public void setWoSupNestedUnitList(List<Wo> woSupNestedUnitList) {
			this.woSupNestedUnitList = woSupNestedUnitList;
		}

		public Long getSubDirectUnitCount() {
			return subDirectUnitCount;
		}

		public void setSubDirectUnitCount(Long subDirectUnitCount) {
			this.subDirectUnitCount = subDirectUnitCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

	}

	private List<Wo> list(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isEmpty(wi.getKey())) {
			return wos;
		}
		List<String> unitIds = business.expendUnitToUnit(ListTools.trim(wi.getUnitList(), true, true));
		/** 去掉指定范围本身,仅包含下级 */
		unitIds.removeAll(ListTools.extractProperty(business.unit().pick(wi.getUnitList()), JpaObject.id_FIELDNAME,
				String.class, true, true));
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.like(cb.lower(root.get(Unit_.name)), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(cb.lower(root.get(Unit_.unique)), "%" + str + "%", '\\'));
		p = cb.or(p, cb.like(cb.lower(root.get(Unit_.pinyin)), str + "%", '\\'));
		p = cb.or(p, cb.like(cb.lower(root.get(Unit_.pinyinInitial)), str + "%", '\\'));
		p = cb.or(p, cb.like(cb.lower(root.get(Unit_.distinguishedName)), str + "%", '\\'));

		if (ListTools.isNotEmpty(unitIds)) {
			p = cb.and(p, root.get(Unit_.id).in(unitIds));
		}
		if (StringUtils.isNotEmpty(wi.getType())) {
			p = cb.and(p, cb.isMember(wi.getType(), root.get(Unit_.typeList)));
		}
		cq.select(root).where(p);
		List<Unit> os = em.createQuery(cq).getResultList();
		wos = Wo.copier.copy(os);
		for (Wo wo : wos) {
			wo.setWoSupNestedUnitList(Wo.copier.copy(business.unit().listSupNestedObject(wo)));
			wo.setSubDirectUnitCount(
					business.entityManagerContainer().countEqual(Unit.class, Unit.superior_FIELDNAME, wo.getId()));
			wo.setSubDirectIdentityCount(
					business.entityManagerContainer().countEqual(Identity.class, Identity.unit_FIELDNAME, wo.getId()));
		}
		wos = business.unit().sort(wos);
		return wos;
	}

}