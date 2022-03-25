package com.x.organization.assemble.control.jaxrs.unit;

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
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

class ActionListWithUnitWithType extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getType(),
					StringUtils.join(wi.getUnitList(), ","));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(effectivePerson, business, wi);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索组织范围,为空则不限定")
		private List<String> unitList = new ArrayList<>();
		@FieldDescribe("组织的type属性值,匹配多值中的某一个,不能为空")
		private String type;

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

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 7096544058621159846L;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("直接下级组织组织对象")
		private List<Wo> woSubDirectUnitList = new ArrayList<>();

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public List<Wo> getWoSubDirectUnitList() {
			return woSubDirectUnitList;
		}

		public void setWoSubDirectUnitList(List<Wo> woSubDirectUnitList) {
			this.woSubDirectUnitList = woSubDirectUnitList;
		}

	}

	private List<Wo> list(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isNotEmpty(wi.getType())) {
			if (ListTools.isEmpty(wi.getUnitList())) {
				EntityManager em = business.entityManagerContainer().get(Unit.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Unit> root = cq.from(Unit.class);
				Predicate p = cb.isMember(wi.getType(), root.get(Unit_.typeList));
				List<String> os = em.createQuery(cq.select(root.get(Unit_.id)).where(p)).getResultList().stream().distinct().collect(Collectors.toList());

				List<String> referenceUnitIds = new ArrayList<>(os);
				for (String str : os) {
					referenceUnitIds.addAll(business.unit().listSupNested(str));
				}
				referenceUnitIds = ListTools.trim(referenceUnitIds, true, true);
				List<Wo> list = Wo.copier.copy(business.entityManagerContainer().list(Unit.class, referenceUnitIds));
				list = business.unit().sort(list);
				this.format(list);
				for (Wo wo : list) {
					if (wo.getLevel() == 1) {
						wos.add(wo);
					}
				}
			} else {
				return this.listWithUnitWithType(effectivePerson, business, wi);
			}
		}
		return wos;
	}

	private List<Wo> listWithUnitWithType(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		List<String> unitIds = new ArrayList<>();
		for (Unit o : business.unit().pick(wi.getUnitList())) {
			if (null != o) {
				unitIds.add(o.getId());
			}
		}
		if (ListTools.isEmpty(unitIds)) {
			return wos;
		}
		List<String> expendUnitIds = business.expendUnitToUnit(unitIds);
		/** 搜索范围不包含自己 */
		expendUnitIds.removeAll(unitIds);
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isMember(wi.getType(), root.get(Unit_.typeList));
		p = cb.and(p, root.get(Unit_.id).in(expendUnitIds));
		List<String> os = em.createQuery(cq.select(root.get(Unit_.id)).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		List<String> referenceUnitIds = new ArrayList<String>(os);
		for (String str : os) {
			referenceUnitIds.addAll(business.unit().listSupNested(str));
		}
		referenceUnitIds = ListTools.trim(referenceUnitIds, true, true);
		List<Wo> list = Wo.copier.copy(business.entityManagerContainer().list(Unit.class, referenceUnitIds));
		list = business.unit().sort(list);
		this.format(list);
		for (Wo wo : list) {
			if (unitIds.contains(wo.getId())) {
				wos.add(wo);
			}
		}
		return wos;
	}

	private void format(List<Wo> list) {
		for (Wo wo : list) {
			if (wo.getLevel() != 1) {
				Wo o = this.find(list, wo.getSuperior());
				if (null != o) {
					o.getWoSubDirectUnitList().add(wo);
				}
			}
		}
	}

	private Wo find(List<Wo> list, String id) {
		for (Wo o : list) {
			if (StringUtils.equalsIgnoreCase(id, o.getId())) {
				return o;
			}
		}
		return null;
	}

}