package com.x.processplatform.assemble.surface.jaxrs.data;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;

class ActionDeleteWithWorkPath4 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1, String path2,
			String path3, String path4) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
		}
		Wo wo = ThisApplication.context().applications()
				.deleteQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("data", "work", work.getId(), path0, path1, path2, path3, path4),
						work.getJob())
				.getData(Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}
}
