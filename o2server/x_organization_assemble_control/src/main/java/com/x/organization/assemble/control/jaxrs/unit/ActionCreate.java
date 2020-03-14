package com.x.organization.assemble.control.jaxrs.unit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.message.OrgBodyMessage;
import com.x.organization.assemble.control.message.OrgMessage;
import com.x.organization.assemble.control.message.OrgMessageFactory;
import com.x.organization.core.entity.Unit;

class ActionCreate extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);
	
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Unit unit = Wi.copier.copy(wi);
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			if (StringUtils.isNotEmpty(wi.getSuperior())) {
				/** 创建次级组织 */
				Unit superior = business.unit().pick(wi.getSuperior());
				if (null == superior) {
					throw new ExceptionSuperiorNotExist(wi.getName(), wi.getSuperior());
				}
				if (!business.editable(effectivePerson, superior)) {
					throw new ExceptionDenyCreateUnit(effectivePerson, superior.getName());
				}
				unit.setSuperior(superior.getId());
			} else {
				/** 创建顶层组织 */
				if (!business.editable(effectivePerson, unit)) {
					throw new ExceptionDenyCreateTopUnit(effectivePerson, unit.getName());
				}
			}
			unit.setControllerList(ListTools.extractProperty(
					business.person().pick(ListTools.trim(unit.getControllerList(), true, true)),
					JpaObject.id_FIELDNAME, String.class, true, true));
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.duplicateUniqueWhenNotEmpty(business, unit)) {
				throw new ExceptionDuplicateUnique(unit.getName(), unit.getUnique());
			}
			/** 判断同一级别下name不重复 */
			if (this.duplicateName(business, unit)) {
				throw new ExceptionDuplicateName(unit.getName());
			}
			emc.beginTransaction(Unit.class);
			business.unit().adjustInherit(unit);
			emc.persist(unit);
			emc.commit();
			ApplicationCache.notify(Unit.class);
			
			/**创建 组织变更org消息通信 */
			OrgMessageFactory  orgMessageFactory = new OrgMessageFactory();
			orgMessageFactory.createMessageCommunicate("add", "unit", unit, effectivePerson);
			
			Wo wo = new Wo();
			wo.setId(unit.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends Unit {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Unit.superior_FIELDNAME, Unit.pinyin_FIELDNAME,
						Unit.pinyinInitial_FIELDNAME, Unit.level_FIELDNAME, Unit.levelName_FIELDNAME,
						Unit.inheritedControllerList_FIELDNAME));
	}
	

}
