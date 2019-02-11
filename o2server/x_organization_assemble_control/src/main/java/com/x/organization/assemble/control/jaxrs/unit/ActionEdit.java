package com.x.organization.assemble.control.jaxrs.unit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Unit unit = business.unit().pick(flag);
			if (null == unit) {
				throw new ExceptionUnitNotExist(flag);
			}
			if (!business.editable(effectivePerson, unit)) {
				throw new ExceptionDenyEditUnit(effectivePerson, flag);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}

			/** pick出来的对象需要重新取出 */
			emc.beginTransaction(Unit.class);
			unit = emc.find(unit.getId(), Unit.class);
			unit.setControllerList(ListTools.extractProperty(
					business.person().pick(ListTools.trim(unit.getControllerList(), true, true)),
					JpaObject.id_FIELDNAME, String.class, true, true));
			Wi.copier.copy(wi, unit);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.duplicateUniqueWhenNotEmpty(business, unit)) {
				throw new ExceptionDuplicateUnique(unit.getName(), unit.getUnique());
			}
			/** 判断同一级别下name不重复 */
			if (this.duplicateName(business, unit)) {
				throw new ExceptionDuplicateName(unit.getName());
			}
			business.unit().adjustInherit(unit);
			emc.check(unit, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Unit.class);
			Wo wo = new Wo();
			wo.setId(unit.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends Unit {

		private static final long serialVersionUID = -7527954993386512109L;

//		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
//				ListTools.toList(JpaObject.FieldsUnmodify, Unit.superior_FIELDNAME, Unit.pinyin_FIELDNAME,
//						Unit.pinyinInitial_FIELDNAME, Unit.level_FIELDNAME, Unit.levelName_FIELDNAME,
//						Unit.inheritedControllerList_FIELDNAME));
		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME,
						Unit.level_FIELDNAME, Unit.levelName_FIELDNAME, Unit.inheritedControllerList_FIELDNAME));
	}

}
