package com.x.organization.assemble.control.jaxrs.group;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

import net.sf.ehcache.Element;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag);
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.get(business, flag);
				business.cache().put(new Element(cacheKey, wo));
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
		/*wos = wos.stream()
				.sorted(Comparator.comparing(WoPerson::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(WoPerson::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());*/
		wo.setWoPersonList(wos);
	}

	private void referenceUnit(Business business, Wo wo) throws Exception {
		List<WoUnit> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(wo.getUnitList())) {
			List<Unit> os = business.unit().pick(wo.getUnitList());
			wos = WoUnit.copier.copy(os);
		}
		/*wos = wos.stream()
				.sorted(Comparator.comparing(WoUnit::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(Comparator.comparing(WoUnit::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());*/
		wo.setWoUnitList(wos);
	}

	public static class Wo extends WoGroupAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("成员群组对象")
		private List<Wo> woGroupList = new ArrayList<>();

		@FieldDescribe("成员个人对象")
		private List<WoPerson> woPersonList = new ArrayList<>();

		@FieldDescribe("成员组织对象")
		private List<WoUnit> woUnitList = new ArrayList<>();

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

	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password", "icon"));

	}

	public static class WoUnit extends Unit {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
				JpaObject.FieldsInvisible);

	}

}