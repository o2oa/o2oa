package com.x.organization.assemble.control.jaxrs.permissionsetting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.message.OrgMessageFactory;
import com.x.organization.core.entity.PermissionSetting;


class ActionDelete extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			PermissionSetting permission = business.permissionSetting().pick(flag);
			if (null == permission) {
				throw new ExceptionPersonCardNotExist(flag); 
			}

			emc.beginTransaction(PermissionSetting.class);
			permission = emc.find(permission.getId(), PermissionSetting.class);
			emc.remove(permission, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(PermissionSetting.class);
			
			/**创建 组织变更org消息通信 */
			OrgMessageFactory  orgMessageFactory = new OrgMessageFactory();
			orgMessageFactory.createMessageCommunicate("delete", "permissionSetting", permission, effectivePerson);
			
			Wo wo = new Wo();
			wo.setId(permission.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}


}