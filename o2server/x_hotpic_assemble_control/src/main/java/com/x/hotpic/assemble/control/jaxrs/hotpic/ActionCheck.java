package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;

public class ActionCheck extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		ActionResult<Wo> result = new ActionResult<>();
		try {
			hotPictureInfoService.documentExistsCheck();
		} catch (Exception e) {
			throw e;
		}
		Wo wo = new Wo();
		result.setData(wo);
		return result;
	}


	public static class Wo extends GsonPropertyObject {

	}

}
