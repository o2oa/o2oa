package com.x.organization.assemble.control.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;
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
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.UnitDuty;

public class ActionListPinyinInitial extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getKey(), StringUtils.join(wi.getUnitList(), ","),
					StringUtils.join(wi.getUnitDutyList(), ","), StringUtils.join(wi.getGroupList(), ","));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
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
		@FieldDescribe("搜索职务范围,为空则不限定")
		private List<String> unitDutyList = new ArrayList<>();
		@FieldDescribe("搜索群组范围(仅限群组的身份成员),为空则不限定")
		private List<String> groupList = new ArrayList<>();

		public List<String> getUnitDutyList() {
			return unitDutyList;
		}

		public void setUnitDutyList(List<String> unitDutyList) {
			this.unitDutyList = unitDutyList;
		}

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

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

	}

	public static class Wo extends Identity {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (StringUtils.isEmpty(wi.getKey())) {
			return wos;
		}
		String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.pinyinInitial), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);

		ListOrderedSet<String> set = new ListOrderedSet<>();
		if (ListTools.isNotEmpty(wi.getUnitDutyList())) {
			List<UnitDuty> unitDuties = business.unitDuty().pick(wi.getUnitDutyList());
			List<String> unitDutyIdentities = new ArrayList<>();
			for (UnitDuty o : unitDuties) {
				unitDutyIdentities.addAll(o.getIdentityList());
			}
			unitDutyIdentities = ListTools.trim(unitDutyIdentities, true, true);
			set.addAll(unitDutyIdentities);
		}
		if (ListTools.isNotEmpty(wi.getUnitList())) {
			List<String> identityIds = business.expendUnitToIdentity(wi.getUnitList());
			set.addAll(identityIds);
		}
		if (ListTools.isNotEmpty(wi.getGroupList())) {
			List<String> identityIds = business.expendGroupToIdentity(wi.getGroupList());
			set.addAll(identityIds);
		}
		if (!set.isEmpty()) {
			p = cb.and(p, root.get(Identity_.id).in(set.asList()));
		}

		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		wos = Wo.copier.copy(os);
		wos = business.identity().sort(wos);
		return wos;
	}

}