package com.x.processplatform.assemble.surface.jaxrs.data;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;

class ActionGetWithWorkPath4 extends BaseAction {

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String id, String path0, String path1,
			String path2, String path3, String path4) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<JsonElement> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(), work.getId());
			}
			result.setData(this.getData(business, work.getJob(), path0, path1, path2, path3, path4));
			return result;
		}
	}
	
	public static class WoControl extends WorkControl {
	}

}
