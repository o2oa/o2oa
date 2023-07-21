package com.x.processplatform.assemble.designer.jaxrs.id;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoId;

class ActionGet extends BaseAction {

	ActionResult<List<Wo>> execute(Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		if (count > 0 && count < 200) {
			for (int i = 0; i < count; i++) {
				Wo wo = new Wo();
				wo.setId(JpaObject.createId());
				wos.add(wo);
			}
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends WoId {

	}

}