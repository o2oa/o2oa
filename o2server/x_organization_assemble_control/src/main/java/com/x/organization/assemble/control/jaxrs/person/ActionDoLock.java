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
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

class ActionDoLock extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDoLock.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(DateTools.beforeNowMinutesNullIsTrue(wi.getLockExpiredTime(), -1)){
				throw new ExceptionInvalidLockTime();
			}
			Business business = new Business(emc);
			Person person = business.person().pick(flag);
			if (null == person) {
				throw new ExceptionPersonNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !this.editable(business, effectivePerson, person)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			LOGGER.info("{} operate lock user:{} expire:{}", effectivePerson.getDistinguishedName(),
					person.getDistinguishedName(),wi.getLockExpiredTime());
			emc.beginTransaction(Person.class);
			Person entityPerson = emc.find(person.getId(), Person.class);
			entityPerson.setStatus(PersonStatusEnum.LOCK.getValue());
			entityPerson.setStatusDes(wi.getDesc());
			entityPerson.setLockExpireTime(wi.getLockExpiredTime());
			emc.check(entityPerson, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Person.class);

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("到期日期.")
		private Date lockExpiredTime;

		@FieldDescribe("锁定原因.")
		private String desc;

		public Date getLockExpiredTime() {
			return lockExpiredTime == null ? new Date() : lockExpiredTime;
		}

		public void setLockExpiredTime(Date lockExpiredTime) {
			this.lockExpiredTime = lockExpiredTime;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
	}

}
