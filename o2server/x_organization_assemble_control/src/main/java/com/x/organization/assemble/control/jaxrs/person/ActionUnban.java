package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.*;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class ActionUnban extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUnban.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionPersonNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !this.editable(business, effectivePerson, person)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			emc.beginTransaction(Person.class);
			Person entityPerson = emc.find(person.getId(), Person.class);
			if(PersonStatusEnum.BAN.getValue().equals(entityPerson.getStatus())) {
				entityPerson.setStatus(PersonStatusEnum.NORMAL.getValue());
				entityPerson.setStatusDes("");
				emc.check(entityPerson, CheckPersistType.all);
				this.unban(entityPerson, business);
				emc.commit();
				CacheManager.notify(UnitDuty.class);
				CacheManager.notify(Identity.class);
				CacheManager.notify(Person.class);
				CacheManager.notify(Role.class);
				CacheManager.notify(Group.class);
			}

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void unban(Person person, Business business) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		String name = person.getId()+"#ban";
		Custom custom = getCustomWithName(emc, name);
		if(custom != null && StringUtils.isNotBlank(custom.getData())){
			CustomPersonInfo personBanInfo = gson.fromJson(custom.getData(), CustomPersonInfo.class);
			emc.beginTransaction(Identity.class);
			emc.beginTransaction(UnitDuty.class);
			emc.beginTransaction(Group.class);
			emc.beginTransaction(Role.class);
			for(String groupId : personBanInfo.getPerson().getGroupList()){
				Group group = emc.find(groupId, Group.class);
				if(group != null){
					group.setPersonList(ListTools.add(group.getPersonList(), true, true, person.getId()));
				}
			}
			for(String roleId : personBanInfo.getPerson().getRoleList()){
				Role role = emc.find(roleId, Role.class);
				if(role != null){
					role.setPersonList(ListTools.add(role.getPersonList(), true, true, person.getId()));
				}
			}
			for(WrapIdentity wrapIdentity : personBanInfo.getIdentityList()){
				Identity identity = WrapIdentity.copierIn.copy(wrapIdentity);
				emc.persist(identity, CheckPersistType.all);
				for(String groupId : wrapIdentity.getGroupList()){
					Group group = emc.find(groupId, Group.class);
					if(group != null){
						group.setIdentityList(ListTools.add(group.getIdentityList(), true, true, identity.getId()));
					}
				}
				for(String dutyId : wrapIdentity.getDutyList()){
					UnitDuty unitDuty = emc.find(dutyId, UnitDuty.class);
					if(unitDuty != null){
						unitDuty.setIdentityList(ListTools.add(unitDuty.getIdentityList(), true, true, identity.getId()));
					}
				}
			}
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

}
