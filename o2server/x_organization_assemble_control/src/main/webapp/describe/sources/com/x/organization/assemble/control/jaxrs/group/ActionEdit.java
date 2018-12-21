package com.x.organization.assemble.control.jaxrs.group;

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
import com.x.organization.core.entity.Group;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Group group = business.group().pick(flag);
			if (null == group) {
				throw new ExceptionGroupNotExist(flag);
			}
			if (!business.editable(effectivePerson, group)) {
				throw new ExceptionDenyEditGroup(effectivePerson, flag);
			}
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionNameEmpty();
			}
			emc.beginTransaction(Group.class);
			group = emc.find(group.getId(), Group.class);
			Wi.copier.copy(wi, group);
			/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
			if (this.uniqueDuplicateWhenNotEmpty(business, group)) {
				throw new ExceptionDuplicateUnique(group.getName(), group.getUnique());
			}
			group.setPersonList(
					ListTools.extractProperty(business.person().pick(ListTools.trim(group.getPersonList(), true, true)),
							JpaObject.id_FIELDNAME, String.class, true, true));
			group.setGroupList(
					ListTools.extractProperty(business.group().pick(ListTools.trim(group.getGroupList(), true, true)),
							JpaObject.id_FIELDNAME, String.class, true, true));
			emc.check(group, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Group.class);
			Wo wo = new Wo();
			wo.setId(group.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Group {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Group> copier = WrapCopierFactory.wi(Wi.class, Group.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial"));
	}

	public static class Wo extends WoId {

	}

}
