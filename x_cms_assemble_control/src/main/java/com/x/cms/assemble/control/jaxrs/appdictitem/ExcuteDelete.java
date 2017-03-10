package com.x.cms.assemble.control.jaxrs.appdictitem;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;

public class ExcuteDelete extends ExcuteBase {
	
	protected ActionResult<WrapOutId> execute( EffectivePerson effectivePerson, String appId, String appDictId, JsonElement jsonElement, String... paths ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			deleteWithAppDictItem( appId, appDictId, paths );
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}
	
}