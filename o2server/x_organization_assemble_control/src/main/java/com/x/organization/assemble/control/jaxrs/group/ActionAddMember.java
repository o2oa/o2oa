package com.x.organization.assemble.control.jaxrs.group;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;

class ActionAddMember extends BaseAction {

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
			emc.beginTransaction(Group.class);
			group = emc.find(group.getId(), Group.class);
			if (ListTools.isNotEmpty(wi.getPersonList())) {
				List<String> person_add_ids = ListTools.extractProperty(
						business.person().pick(ListTools.trim(wi.getPersonList(), true, true)), JpaObject.id_FIELDNAME,
						String.class, true, true);
				if (ListTools.isNotEmpty(person_add_ids)) {
					group.setPersonList(
							ListTools.trim(ListUtils.sum(group.getPersonList(), person_add_ids), true, true));
				}
			}
			if (ListTools.isNotEmpty(wi.getGroupList())) {
				List<String> group_add_ids = ListTools.extractProperty(
						business.group().pick(ListTools.trim(wi.getGroupList(), true, true)), JpaObject.id_FIELDNAME,
						String.class, true, true);
				if (ListTools.isNotEmpty(group_add_ids)) {
					group.setGroupList(ListTools.trim(ListUtils.sum(group.getGroupList(), group_add_ids), true, true));
				}
			}
			if (ListTools.isNotEmpty(wi.getIdentityList())) {
				List<String> identity_add_ids = ListTools.extractProperty(
						business.identity().pick(ListTools.trim(wi.getIdentityList(), true, true)), JpaObject.id_FIELDNAME,
						String.class, true, true);
				if (ListTools.isNotEmpty(identity_add_ids)) {
					group.setIdentityList(ListTools.trim(ListUtils.sum(group.getIdentityList(), identity_add_ids), true, true));
				}
			}
			emc.check(group, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Group.class);
			Wo wo = new Wo();
			wo.setId(group.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Group {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Group> copier = WrapCopierFactory.wi(Wi.class, Group.class,
				ListTools.toList(Group.personList_FIELDNAME, Group.groupList_FIELDNAME, Group.identityList_FIELDNAME),
				null);
	}

	public static class Wo extends WoId {

	}

}
