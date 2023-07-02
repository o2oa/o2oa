package com.x.organization.assemble.control.jaxrs.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, flag);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	private Wo get(Business business, String flag) throws Exception {
		Group group = business.group().pick(flag);
		if (null == group) {
			throw new ExceptionGroupNotExist(flag);
		}
		Wo wo = Wo.copier.copy(group);
		this.referenceGroup(business, wo);
		this.referencePerson(business, wo);
		this.referenceUnit(business, wo);
		this.referenceIdentity(business, wo);
		return wo;
	}

	private void referenceGroup(Business business, Wo wo) throws Exception {
		List<Wo> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(wo.getGroupList())) {
			List<Group> os = business.group().pick(wo.getGroupList());
			wos = Wo.copier.copy(os);
		}
		/*wos = wos.stream()
				.sorted(Comparator.comparing(Wo::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(Comparator.comparing(Wo::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());*/
		wo.setWoGroupList(wos);
	}

	private void referencePerson(Business business, Wo wo) throws Exception {
		List<WoPerson> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(wo.getPersonList())) {
			List<Person> os = business.person().pick(wo.getPersonList());
			wos = WoPerson.copier.copy(os);
		}
		wo.setWoPersonList(wos);
	}

	private void referenceUnit(Business business, Wo wo) throws Exception {
		List<WoUnit> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(wo.getUnitList())) {
			List<Unit> os = business.unit().pick(wo.getUnitList());
			wos = WoUnit.copier.copy(os);
		}
		wo.setWoUnitList(wos);
	}

	private void referenceIdentity(Business business, Wo wo) throws Exception {
		List<WoIdentity> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(wo.getIdentityList())) {
			List<Identity> os = business.identity().pick(wo.getIdentityList());
			wos = WoIdentity.copier.copy(os);
		}
		wo.setWoIdentityList(wos);
	}

	public static class Wo extends WoGroupAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("成员群组对象")
		private List<Wo> woGroupList = new ArrayList<>();

		@FieldDescribe("成员个人对象")
		private List<WoPerson> woPersonList = new ArrayList<>();

		@FieldDescribe("成员组织对象")
		private List<WoUnit> woUnitList = new ArrayList<>();

		@FieldDescribe("成员身份对象")
		private List<WoIdentity> woIdentityList = new ArrayList<>();

		static WrapCopier<Group, Wo> copier = WrapCopierFactory.wo(Group.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

		public List<Wo> getWoGroupList() {
			return woGroupList;
		}

		public void setWoGroupList(List<Wo> woGroupList) {
			this.woGroupList = woGroupList;
		}

		public List<WoPerson> getWoPersonList() {
			return woPersonList;
		}

		public void setWoPersonList(List<WoPerson> woPersonList) {
			this.woPersonList = woPersonList;
		}

		public List<WoUnit> getWoUnitList() {
			return woUnitList;
		}

		public void setWoUnitList(List<WoUnit> woUnitList) {
			this.woUnitList = woUnitList;
		}

		public List<WoIdentity> getWoIdentityList() {
			return woIdentityList;
		}

		public void setWoIdentityList(List<WoIdentity> woIdentityList) {
			this.woIdentityList = woIdentityList;
		}
	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password", "icon"));

	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -8142218653161885824L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class WoIdentity extends Identity {

		private static final long serialVersionUID = 1217674471934438171L;

		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
				JpaObject.FieldsInvisible);

	}

}
