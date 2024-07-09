package com.x.organization.assemble.express.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionListObject extends BaseAction {

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getIdentityList(), wi.getReferenceFlag(), wi.getReferenceGroupFlag(), wi.getRecursiveFlag());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		for (String str : wi.getIdentityList()) {
			Identity o = business.identity().pick(str);
			if(o!=null){
				Wo wo = Wo.copier.copy(o);
				wo.setMatchKey(str);
				Person p = business.person().pick(o.getPerson());
				if (null != p) {
					wo.setPerson(p.getDistinguishedName());
				}
				if (BooleanUtils.isTrue(wi.getReferenceFlag())){
					referenceUnit(business, wo, wi);
					referenceUnitDuty(business, wo);
				}else {
					Unit u = business.unit().pick(o.getUnit());
					if (null != u) {
						wo.setUnit(u.getDistinguishedName());
					}
				}
				if(BooleanUtils.isTrue(wi.getReferenceGroupFlag())){
					referenceGroup(business, wo, wi);
				}
				wos.add(wo);
			}
		}
		return wos;
	}

	private void referenceUnit(Business business, Wo woIdentity, Wi wi) throws Exception {
		if (StringUtils.isNotEmpty(woIdentity.getUnit())) {
			Unit unit = business.unit().pick(woIdentity.getUnit());
			if (null != unit) {
				WoUnit wo = WoUnit.copier.copy(unit);
				List<String> unitIdList = new ArrayList<>();
				unitIdList.add(wo.getId());
				if(BooleanUtils.isTrue(wi.getRecursiveFlag())){
					referenceUnit(business, wo, unitIdList);
				}
				wo.setUnitIdList(unitIdList);
				woIdentity.setWoUnit(wo);
				woIdentity.setUnit(wo.getDistinguishedName());

			}
		}
	}

	private void referenceUnit(Business business, WoUnit woUnit, List<String> unitIdList) throws Exception {
		if (StringUtils.isNotEmpty(woUnit.getSuperior())) {
			Unit unit = business.unit().pick(woUnit.getSuperior());
			if (null != unit) {
				WoUnit wo = WoUnit.copier.copy(unit);
				unitIdList.add(wo.getId());
				if (StringUtils.isNotEmpty(wo.getSuperior()) && wo.getLevel()>1) {
					referenceUnit(business, wo, unitIdList);
				}
				woUnit.setWoSupDirectUnit(wo);
			}
		}
	}


	private void referenceUnitDuty(Business business, Wo woIdentity) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(woIdentity.getId(), root.get(UnitDuty_.identityList));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		List<WoUnitDuty> wos = WoUnitDuty.copier.copy(os);
		wos = business.unitDuty().sort(wos);
		for (WoUnitDuty woUnitDuty : wos) {
			this.referenceUnit(business, woUnitDuty);
			woUnitDuty.setIdentityList(null);
		}
		woIdentity.setWoUnitDutyList(wos);
	}

	private void referenceUnit(Business business, WoUnitDuty woUnitDuty) throws Exception {
		if (StringUtils.isNotEmpty(woUnitDuty.getUnit())) {
			Unit unit = business.unit().pick(woUnitDuty.getUnit());
			if (null != unit) {
				WoUnit wo = WoUnit.copier.copy(unit);
				woUnitDuty.setWoUnit(wo);
			}
		}
	}

	private void referenceGroup(Business business, Wo wo, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(wo.getId(), root.get(Group_.identityList));
		if(wo.getWoUnit()!=null){
			p = cb.or(p, root.get(Group_.unitList).in(wo.getWoUnit().getUnitIdList()));
		}
		List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
		final List<WoGroup> wos = new ArrayList<>();
		os.stream().forEach(o -> {
			try {
				WoGroup woGroup = WoGroup.copier.copy(o);
				if(BooleanUtils.isTrue(wi.getRecursiveFlag())) {
					woGroup.setWoSupGroupList(WoGroup.copier.copy(business.group().listSupNestedObject(o)));
				}
				wos.add(woGroup);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		wo.setWoGroupList(wos);
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("是否关联查询身份所属组织、角色信息")
		private Boolean referenceFlag;

		@FieldDescribe("是否关联查询身份所属群组信息")
		private Boolean referenceGroupFlag;

		@FieldDescribe("是否递归查询上级组织或群组，默认false")
		private Boolean recursiveFlag;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public Boolean getReferenceFlag() {
			return referenceFlag;
		}

		public void setReferenceFlag(Boolean referenceFlag) {
			this.referenceFlag = referenceFlag;
		}

		public Boolean getRecursiveFlag() {
			return recursiveFlag;
		}

		public void setRecursiveFlag(Boolean recursiveFlag) {
			this.recursiveFlag = recursiveFlag;
		}

		public Boolean getReferenceGroupFlag() {
			return referenceGroupFlag;
		}

		public void setReferenceGroupFlag(Boolean referenceGroupFlag) {
			this.referenceGroupFlag = referenceGroupFlag;
		}
	}

	public static class Wo extends Identity {

		private static final long serialVersionUID = -7628608775316429534L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		@FieldDescribe("匹配字段")
		private String matchKey;

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		@FieldDescribe("组织职务对象")
		private List<WoUnitDuty> woUnitDutyList;

		@FieldDescribe("群组对象")
		private List<WoGroup> woGroupList;

		public String getMatchKey() {
			return matchKey;
		}

		public void setMatchKey(String matchKey) {
			this.matchKey = matchKey;
		}

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}

		public List<WoUnitDuty> getWoUnitDutyList() {
			return woUnitDutyList;
		}

		public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
			this.woUnitDutyList = woUnitDutyList;
		}

		public List<WoGroup> getWoGroupList() {
			return woGroupList;
		}

		public void setWoGroupList(List<WoGroup> woGroupList) {
			this.woGroupList = woGroupList;
		}
	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -7760842451561513441L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Unit.controllerList_FIELDNAME));

		@FieldDescribe("上级组织对象.")
		private WoUnit woSupDirectUnit;

		private List<String> unitIdList;

		public WoUnit getWoSupDirectUnit() {
			return woSupDirectUnit;
		}

		public void setWoSupDirectUnit(WoUnit woSupDirectUnit) {
			this.woSupDirectUnit = woSupDirectUnit;
		}

		public List<String> getUnitIdList() {
			return unitIdList;
		}

		public void setUnitIdList(List<String> unitIdList) {
			this.unitIdList = unitIdList;
		}
	}

	public static class WoUnitDuty extends UnitDuty {

		private static final long serialVersionUID = -2375022310349169180L;

		@FieldDescribe("组织对象")
		private WoUnit woUnit;

		static WrapCopier<UnitDuty, WoUnitDuty> copier = WrapCopierFactory.wo(UnitDuty.class, WoUnitDuty.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		public WoUnit getWoUnit() {
			return woUnit;
		}

		public void setWoUnit(WoUnit woUnit) {
			this.woUnit = woUnit;
		}
	}

	public static class WoGroup extends Group {

		private static final long serialVersionUID = 1430979713075568834L;

		@FieldDescribe("上级群组对象列表.")
		private List<WoGroup> woSupGroupList;

		static WrapCopier<Group, WoGroup> copier = WrapCopierFactory.wo(Group.class, WoGroup.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		public List<WoGroup> getWoSupGroupList() {
			return woSupGroupList;
		}

		public void setWoSupGroupList(List<WoGroup> woSupGroupList) {
			this.woSupGroupList = woSupGroupList;
		}
	}

}
