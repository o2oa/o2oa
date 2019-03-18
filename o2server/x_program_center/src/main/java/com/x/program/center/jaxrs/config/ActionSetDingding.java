package com.x.program.center.jaxrs.config;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.jaxrs.config.ActionGetCollect.Wo;
import com.x.program.center.jaxrs.config.ActionSetCollect.Wi;

public class ActionSetDingding extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		wi.save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wi extends Dingding {

		static WrapCopier<Wi, Dingding> copier = WrapCopierFactory.wi(Wi.class, Dingding.class, null, null);

	}

	public static class Wo extends WrapBoolean {

	}
}