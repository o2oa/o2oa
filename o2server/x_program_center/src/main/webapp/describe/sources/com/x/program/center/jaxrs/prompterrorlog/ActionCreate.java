package com.x.program.center.jaxrs.prompterrorlog;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.PromptErrorLog;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = gson.fromJson(jsonElement, Wi.class);
		PromptErrorLog o = Wi.copier.copy(wi);
		/** 默认使用传递过来的id,如果不存在那么重新赋值 */
		if (StringUtils.isEmpty(o.getId())) {
			o.setId(StringTools.uniqueToken());
		}
		NameValuePair pair = new NameValuePair();
		pair.setName(PromptErrorLog.class.getName());
		pair.setValue(o);
		ThisApplication.logQueue.send(pair);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}

	static class Wi extends PromptErrorLog {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<Wi, PromptErrorLog> copier = WrapCopierFactory.wi(Wi.class, PromptErrorLog.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

	}
}
