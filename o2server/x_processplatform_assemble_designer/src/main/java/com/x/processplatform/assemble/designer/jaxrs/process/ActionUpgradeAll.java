package com.x.processplatform.assemble.designer.jaxrs.process;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Process;

class ActionUpgradeAll extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if (effectivePerson.isManager()) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				emc.beginTransaction(Process.class);
				for (Process process : business.entityManagerContainer().listAll(Process.class)) {
					if(StringUtils.isEmpty(process.getEdition())){
						process.setEdition(process.getId());
						process.setEditionEnable(true);
						process.setEditionNumber(1.0);
						process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
						emc.check(process, CheckPersistType.all);
					}
				}
				emc.commit();
				cacheNotify();
			}
			wo.setValue(true);
		}else{
			wo.setValue(false);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 5740098922538311966L;

	}
}