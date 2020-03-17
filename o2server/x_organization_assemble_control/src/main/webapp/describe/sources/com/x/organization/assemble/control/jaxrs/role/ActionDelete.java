package com.x.organization.assemble.control.jaxrs.role;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.assemble.control.message.OrgBodyMessage;
import com.x.organization.assemble.control.message.OrgMessage;
import com.x.organization.assemble.control.message.OrgMessageFactory;
import com.x.organization.core.entity.Role;

class ActionDelete extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Role role = business.role().pick(flag);
			if (null == role) {
				throw new ExceptionRoleNotExist(flag);
			}
			if (!business.editable(effectivePerson, role)) {
				throw new ExceptionDenyDeleteRole(effectivePerson, flag);
			}
			if (OrganizationDefinition.DEFAULTROLES.contains(role.getName())) {
				throw new ExceptionDenyDeleteDefaultRole(role.getName());

			}

			emc.beginTransaction(Role.class);
			role = emc.find(role.getId(), Role.class);
			emc.remove(role, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Role.class);
			
			/**创建 组织变更org消息通信 */
			OrgMessageFactory  orgMessageFactory = new OrgMessageFactory();
			orgMessageFactory.createMessageCommunicate("delete", "role", role, effectivePerson);
			
			Wo wo = new Wo();
			wo.setId(role.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}


}