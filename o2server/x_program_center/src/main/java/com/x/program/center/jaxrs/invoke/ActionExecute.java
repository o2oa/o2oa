package com.x.program.center.jaxrs.invoke;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoValue;
import com.x.organization.core.express.Organization;
import com.x.program.center.Context;
import com.x.program.center.ThisApplication;
import com.x.program.center.WebservicesClient;
import com.x.program.center.core.entity.Invoke;

class ActionExecute extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Invoke invoke = emc.flag(flag, Invoke.class);
			if (null == invoke) {
				throw new ExceptionInvokeNotExist(flag);
			}
			if (!BooleanUtils.isTrue(invoke.getEnable())) {
				throw new ExceptionNotEnable(invoke.getName());
			}
			if (StringUtils.isNotEmpty(invoke.getRemoteAddrRegex())) {
				Matcher matcher = Pattern.compile(invoke.getRemoteAddrRegex()).matcher(request.getRemoteAddr());
				if (!matcher.find()) {
					throw new ExceptionInvalidRemoteAddr(request.getRemoteAddr(), invoke.getName());
				}
			}
			invoke.setLastStartTime(new Date());
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			ScriptContext newContext = new SimpleScriptContext();
			Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
			Resources resources = new Resources();
			resources.setEntityManagerContainer(emc);
			resources.setContext(ThisApplication.context());
			resources.setOrganization(new Organization(ThisApplication.context()));
			resources.setWebservicesClient(new WebservicesClient());
			engineScope.put(Resources.RESOURCES_BINDING_NAME, resources);
			engineScope.put("requestText", gson.toJson(jsonElement));
			engineScope.put("request", request);
			engineScope.put("response", response);
			engineScope.put("effectivePerson", effectivePerson);
			Wo wo = new Wo();
			try {
				engine.eval(Config.mooToolsScriptText());
				Object o = engine.eval(invoke.getText(), newContext);
				wo.setValue(o);
			} catch (Exception e) {
				throw new ExceptionExecuteError(invoke.getName(), e);
			}
			result.setData(wo);
			invoke.setLastEndTime(new Date());
			return result;
		}
	}

	public static class Wo extends WoValue {

	}

	public static class Resources {
		private EntityManagerContainer entityManagerContainer;
		private Context context;
		private Organization organization;
		private WebservicesClient webservicesClient;
		private String input;

		public static String RESOURCES_BINDING_NAME = "resources";

		public EntityManagerContainer getEntityManagerContainer() {
			return entityManagerContainer;
		}

		public void setEntityManagerContainer(EntityManagerContainer entityManagerContainer) {
			this.entityManagerContainer = entityManagerContainer;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

		public String getInput() {
			return input;
		}

		public void setInput(String input) {
			this.input = input;
		}

		public WebservicesClient getWebservicesClient() {
			return webservicesClient;
		}

		public void setWebservicesClient(WebservicesClient webservicesClient) {
			this.webservicesClient = webservicesClient;
		}
	}

}