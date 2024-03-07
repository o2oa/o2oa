package com.x.organization.assemble.control.jaxrs.person;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonExtend;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

class ActionDelete extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (Config.token().isInitialManager(flag)) {
				throw new ExceptionDenyDeleteInitialManager();
			} else {
				Business business = new Business(emc);
				Person person = business.person().pick(flag);
				if (null == person) {
					throw new ExceptionEntityNotExist(flag, Person.class);
				}
				LOGGER.info("{} operate delete user:{}", effectivePerson.getDistinguishedName(), person.getDistinguishedName());
				this.doDelete(business, flag, effectivePerson);
				CacheManager.notify(Person.class);
				CacheManager.notify(Identity.class);
				CacheManager.notify(Unit.class);
				CacheManager.notify(UnitDuty.class);
				CacheManager.notify(PersonAttribute.class);
				CacheManager.notify(Custom.class);
				CacheManager.notify(Group.class);
				CacheManager.notify(Role.class);
				// 通知x_collect_service_transmit同步数据到collect
				business.instrument().collect().person();
				Wo wo = new Wo();
				wo.setId(person.getId());
				result.setData(wo);
			}
			return result;
		}
	}

	private void doDelete(Business business, String id, EffectivePerson effectivePerson) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();

		// 从内存中取到人
		Person person = emc.find(id, Person.class);
		if (!this.editable(business, effectivePerson, person)) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		List<Identity> identities = this.listIdentity(business, person);
		// 删除身份组织职务成员,提交后才可以删除身份
		emc.beginTransaction(UnitDuty.class);
		this.removeMemberOfUnitDuty(business, identities);
		emc.commit();
		// 删除身份
		emc.beginTransaction(Identity.class);
		for (Identity o : identities) {
			emc.remove(o, CheckRemoveType.all);
		}
		emc.commit();
		// 删除个人属性
		emc.beginTransaction(PersonAttribute.class);
		this.removePersonAttribute(business, person);
		// 删除个人扩展
		emc.beginTransaction(PersonExtend.class);
		this.removePersonExtend(business, person);
		// 删除个人自定义信息
		emc.beginTransaction(Custom.class);
		this.removePersonCustom(business, person);
		// 删除群组成员
		emc.beginTransaction(Group.class);
		this.removeMemberOfGroup(business, person);
		// 删除角色成员
		emc.beginTransaction(Role.class);
		this.removeMemberOfRole(business, person);
		// 删除组织的管理个人以及继承的管理个人
		emc.beginTransaction(Unit.class);
		this.removeMemberOfUnitController(business, person);
		// 删除个人管理者成员
		this.removeMemberOfPersonController(business, person);
		// 删除汇报人员为将要删除的人
		this.removeMemberOfPersonSuperior(business, person);
		emc.beginTransaction(Person.class);
		// 先进行一次提交,通过check
		emc.commit();
		emc.beginTransaction(Custom.class);
		Custom custom = new Custom();
		custom.setPerson(person.getDistinguishedName());
		custom.setName(PERSON_DELETE_CUSTOM_NAME);
		CustomPersonInfo customPersonInfo = new CustomPersonInfo();
		customPersonInfo.setOperator(effectivePerson.getDistinguishedName());
		customPersonInfo.setOperateTime(new Date());
		customPersonInfo.setPerson(WrapPerson.copier.copy(person));
		custom.setData(gson.toJson(customPersonInfo));
		emc.persist(custom);
		emc.beginTransaction(Person.class);
		emc.remove(person, CheckRemoveType.all);
		emc.commit();
	}

	public static class Wo extends WoId {

	}

}
