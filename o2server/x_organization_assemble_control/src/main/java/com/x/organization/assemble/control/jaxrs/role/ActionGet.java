package com.x.organization.assemble.control.jaxrs.role;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

import net.sf.ehcache.Element;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
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
		Role o = business.role().pick(flag);
		if (null == o) {
			throw new ExceptionRoleNotExist(flag);
		}
		Wo wo = Wo.copier.copy(o);
		this.referenceGroup(business, wo);
		this.referencePerson(business, wo);
		return wo;
	}

	private void referenceGroup(Business business, Wo wo) throws Exception {
		List<Group> os = business.group().pick(wo.getGroupList());
		List<WoGroup> wos = WoGroup.copier.copy(os);
		wos = wos.stream()
				.sorted(Comparator.comparing(WoGroup::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(Comparator.comparing(WoGroup::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		wo.setWoGroupList(wos);
	}

	private void referencePerson(Business business, Wo wo) throws Exception {
		List<Person> os = business.person().pick(wo.getPersonList());
		List<WoPerson> wos = WoPerson.copier.copy(os);
		wos = wos.stream()
				.sorted(Comparator.comparing(WoPerson::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(WoPerson::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		wo.setWoPersonList(wos);
	}

	public static class Wo extends WoRoleAbstract {

		private static final long serialVersionUID = -127291000673692614L;

		private List<WoGroup> woGroupList;

		private List<WoPerson> woPersonList;

		static WrapCopier<Role, Wo> copier = WrapCopierFactory.wo(Role.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public List<WoGroup> getWoGroupList() {
			return woGroupList;
		}

		public void setWoGroupList(List<WoGroup> woGroupList) {
			this.woGroupList = woGroupList;
		}

		public List<WoPerson> getWoPersonList() {
			return woPersonList;
		}

		public void setWoPersonList(List<WoPerson> woPersonList) {
			this.woPersonList = woPersonList;
		}

	}

	public static class WoGroup extends Group {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Group, WoGroup> copier = WrapCopierFactory.wo(Group.class, WoGroup.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password", "icon"));

	}

	public static class WoPerson extends Person {

		private static final long serialVersionUID = -127291000673692614L;

		static WrapCopier<Person, WoPerson> copier = WrapCopierFactory.wo(Person.class, WoPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, "password"));

	}

}