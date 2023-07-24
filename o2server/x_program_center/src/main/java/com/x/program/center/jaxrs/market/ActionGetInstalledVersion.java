package com.x.program.center.jaxrs.market;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.program.center.core.entity.InstallLog;

class ActionGetInstalledVersion extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			InstallLog installLog = emc.find(id, InstallLog.class);
			Wo wo = new Wo();
			if(installLog!=null && CommonStatus.VALID.getValue().equals(installLog.getStatus())){
				wo.setValue(installLog.getVersion());
			}else{
				wo.setValue("");
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapString {

	}
}