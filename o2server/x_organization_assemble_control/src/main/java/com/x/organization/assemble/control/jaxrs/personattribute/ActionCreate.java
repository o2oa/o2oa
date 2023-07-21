package com.x.organization.assemble.control.jaxrs.personattribute;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Person person = business.person().pick(wi.getPerson());
			if (null == person) {
				throw new ExceptionPersonNotExist(wi.getPerson());
			}
			if (!business.editable(effectivePerson, person)) {
				throw new ExceptionDenyEditPerson(effectivePerson, person.getName());
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			PersonAttribute o = Wi.copier.copy(wi);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.uniqueDuplicateWhenNotEmpty(business, o)) {
				throw new ExceptionDuplicateUnique(o.getName(), o.getUnique());
			}
			o.setPerson(person.getId());
			if (this.duplicateOnPerson(business, person, wi.getName(), o)) {
				throw new ExceptionNameExistWithPerson(person, wi.getName());
			}
			emc.beginTransaction(PersonAttribute.class);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(PersonAttribute.class);
			Wo wo = new Wo();
			wo.setId(o.getId());
			result.setData(wo);
			return result;
		}

	}

	public static class Wo extends WoId {

	}

	public static class Wi extends PersonAttribute {

		private static final long serialVersionUID = 370024636157241213L;

		static WrapCopier<Wi, PersonAttribute> copier = WrapCopierFactory.wi(Wi.class, PersonAttribute.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial"));

	}

}
