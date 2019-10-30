package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Identity identity = business.identity().pick(flag);
			if (null == identity) {
				throw new ExceptionEntityNotExist(flag, Identity.class);
			}
			Person person = emc.find(identity.getPerson(), Person.class);
			if (null == person) {
				throw new ExceptionEntityNotExist(identity.getPerson(), Person.class);
			}
			Unit unit = business.unit().pick(identity.getUnit());
			if (null == unit) {
				throw new ExceptionEntityNotExist(identity.getUnit(), Unit.class);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionAccessDenied(effectivePerson, unit);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			emc.beginTransaction(Identity.class);
			emc.beginTransaction(Person.class);
			identity = emc.find(identity.getId(), Identity.class);
			Wi.copier.copy(wi, identity);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.uniqueDuplicateWhenNotEmpty(business, identity)) {
				throw new ExceptionDuplicateUnique(identity.getName(), identity.getUnique());
			}
			identity.setUnit(unit.getId());
			identity.setUnitLevel(unit.getLevel());
			identity.setUnitLevelName(unit.getLevelName());
			identity.setUnitName(unit.getName());
			/* 设置主身份 */
			if (BooleanUtils.isTrue(identity.getMajor())) {
				for (Identity o : emc.listEqual(Identity.class, Identity.person_FIELDNAME, identity.getPerson())) {
					if (!StringUtils.equals(identity.getId(), o.getId())) {
						o.setMajor(false);
					}
				}
			}
			emc.check(identity, CheckPersistType.all);
			List<Unit> topUnits = business.unit()
					.pick(ListTools.trim(person.getTopUnitList(), true, true, this.topUnit(business, unit).getId()));
			person.setTopUnitList(ListTools.extractField(topUnits, Unit.id_FIELDNAME, String.class, true, true));
			emc.check(person, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Identity.class);
			ApplicationCache.notify(Person.class);
			Wo wo = new Wo();
			wo.setId(identity.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Identity {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Identity> copier = WrapCopierFactory.wi(Wi.class, Identity.class, null, ListTools
				.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial", "unitName", "unitLevel", "unitLevelName"));
	}

}
