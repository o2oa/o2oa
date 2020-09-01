package com.x.organization.assemble.control.jaxrs.permissionsetting;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PermissionSetting;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			PermissionSetting permssion = emc.find(flag, PermissionSetting.class);
			if (null == permssion) {
				throw new ExceptionPersonCardNotExist(flag); 
			}
			Wo wo = Wo.copier.copy(permssion);
			result.setData(wo);
			
			return result;
		}
	}
	public static class Wo extends WoPermissionSettingAbstract {
		private static final long serialVersionUID = 5661133561098715100L;
		public static WrapCopier<PermissionSetting, Wo> copier = WrapCopierFactory.wo(PermissionSetting.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}