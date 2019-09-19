package com.x.processplatform.assemble.surface.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;

class ActionUpdateWithWorkPath7 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String path0, String path1, String path2,
			String path3, String path4, String path5, String path6, String path7, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			if (!business.editable(effectivePerson, work)) {
				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
						work.getId());
			}
			/** 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
			this.updateTitleSerial(business, work, jsonElement);
			this.updateData(business, work, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			/** 在方法内进行了commit不需要再次进行commit */
			// emc.commit();
			Wo wo = new Wo();
			wo.setId(work.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WoControl extends WorkControl {
	}

}
