package com.x.organization.assemble.personal.jaxrs.person;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionEdit extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
				throw new ExceptionEditInitialManagerDeny();
			}
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null == person) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			/** 从内存中pick出来的无法作为实体保存 */
			person = emc.find(person.getId(), Person.class);
			Wi.copier.copy(wi, person);
			this.checkName(business, person.getName(), person.getId());
			this.checkMobile(business, person.getMobile(), person.getId());
			this.checkEmployee(business, person.getEmployee(), person.getId());
			this.checkMail(business, person.getMail(), person.getId());
			/** 不能更新person的superior 和 controllerList */
			emc.beginTransaction(Person.class);
			emc.check(person, CheckPersistType.all);
			emc.commit();
			/** 刷新缓存 */
			ApplicationCache.notify(Person.class);
			/** 通知x_collect_service_transmit同步数据到collect */
			business.instrument().collect().person();
			Wo wo = new Wo();
			wo.setId(person.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Person {

		private static final long serialVersionUID = 1571810726944802231L;

		static WrapCopier<Wi, Person> copier = WrapCopierFactory.wi(Wi.class, Person.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, "icon", "iconMdpi", "iconLdpi", "pinyin", "pinyinInitial",
						"password", "passwordExpiredTime", "lastLoginTime", "lastLoginAddress", "lastLoginClient",
						"superior", "controllerList"));
	}

	public static class Wo extends WoId {

	}

}
