package com.x.organization.assemble.control.jaxrs.person;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.*;
import com.x.organization.core.entity.enums.PersonStatusEnum;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ActionDoBan extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoBan.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionPersonNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !this.editable(business, effectivePerson, person)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			LOGGER.info("{} operate ban user:{}", effectivePerson.getDistinguishedName(), person.getDistinguishedName());
			emc.beginTransaction(Person.class);
			Person entityPerson = emc.find(person.getId(), Person.class);
			entityPerson.setStatus(PersonStatusEnum.BAN.getValue());
			entityPerson.setStatusDes(wi.getDesc());
			emc.check(entityPerson, CheckPersistType.all);
			this.doBan(entityPerson, business, effectivePerson);
			emc.commit();
			CacheManager.notify(UnitDuty.class);
			CacheManager.notify(Identity.class);
			CacheManager.notify(Person.class);
			CacheManager.notify(Role.class);
			CacheManager.notify(Group.class);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void doBan(Person person, Business business, EffectivePerson effectivePerson) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Custom.class);
		emc.beginTransaction(Group.class);
		emc.beginTransaction(Role.class);
		emc.beginTransaction(UnitDuty.class);
		emc.beginTransaction(Identity.class);
		CustomPersonInfo personBanInfo = new CustomPersonInfo();
		personBanInfo.setOperator(effectivePerson.getDistinguishedName());
		WrapPerson wrapPerson = WrapPerson.copier.copy(person);
		List<Group> groupList = business.group().listSupDirectWithPersonObject(person);
		for (Group group : groupList){
			group.getPersonList().remove(person.getId());
			group.setPersonList(group.getPersonList());
			wrapPerson.getGroupList().add(group.getId());
		}
		List<Role> roleList = business.role().listObjByPerson(person.getId());
		for (Role role : roleList){
			role.getPersonList().remove(person.getId());
			role.setPersonList(role.getPersonList());
			wrapPerson.getRoleList().add(role.getId());
		}
		List<Identity> identityList = emc.listEqual(Identity.class, Identity.person_FIELDNAME, person.getId());
		List<WrapIdentity> wrapIdentityList = new ArrayList<>();
		for (Identity identity : identityList){
			WrapIdentity wrapIdentity = WrapIdentity.copier.copy(identity);
			List<UnitDuty> unitDutyList = business.unitDuty().listObjByIdentity(identity.getId());
			for (UnitDuty unitDuty : unitDutyList){
				unitDuty.getIdentityList().remove(identity.getId());
				unitDuty.setIdentityList(unitDuty.getIdentityList());
				wrapIdentity.getDutyList().add(unitDuty.getId());
			}
			groupList = business.group().listSupDirectWithIdentityObject(identity.getId());
			for (Group group : groupList){
				group.getIdentityList().remove(identity.getId());
				group.setIdentityList(group.getIdentityList());
				wrapIdentity.getGroupList().add(group.getId());
			}
			wrapIdentityList.add(wrapIdentity);
			emc.remove(identity);
		}
		personBanInfo.setPerson(wrapPerson);
		personBanInfo.setIdentityList(wrapIdentityList);
		String name = person.getId()+"#ban";
		Custom custom = getCustomWithName(emc, name);
		if(custom == null) {
			custom = new Custom();
			custom.setPerson(person.getDistinguishedName());
			custom.setName(name);
			custom.setData(gson.toJson(personBanInfo));
			emc.persist(custom);
		}else {
			custom.setData(gson.toJson(personBanInfo));
		}
	}

	private Custom getCustomWithName(EntityManagerContainer emc, String name) throws Exception {
		EntityManager em = emc.get(Custom.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Custom> cq = cb.createQuery(Custom.class);
		Root<Custom> root = cq.from(Custom.class);
		Predicate p = cb.equal(root.get(Custom_.name), name);
		List<Custom> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("禁用原因.")
		private String desc;

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}

}
