package com.x.program.center.jaxrs.invoke;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoSeeOther;
import com.x.base.core.project.jaxrs.WoTemporaryRedirect;
import com.x.base.core.project.jaxrs.WoValue;
import com.x.organization.core.express.Organization;
import com.x.program.center.Context;
import com.x.program.center.ThisApplication;
import com.x.program.center.WebservicesClient;
import com.x.program.center.core.entity.Invoke;

class ActionExecute extends BaseAction {

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag,
			JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
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
			resources.setApplications(ThisApplication.context().applications());
			engineScope.put(Resources.RESOURCES_BINDING_NAME, resources);
			engineScope.put("requestText", gson.toJson(jsonElement));
			engineScope.put("request", request);
			engineScope.put("effectivePerson", effectivePerson);
			CustomResponse customResponse = new CustomResponse();
			engineScope.put("customResponse", customResponse);
			Wo wo = new Wo();
			try {
				engine.eval(Config.initialServiceScriptText(), newContext);
				StringBuffer sb = new StringBuffer();

				// sb.append(Config.initialServiceScriptText()).append(System.lineSeparator());
				sb.append(invoke.getText()).append(System.lineSeparator());
				// sb.append("return (function(){").append(System.lineSeparator());
//				if (StringUtils.isNotEmpty(scriptName)) {
//					List<Script> list = business.element().listScriptNestedWithApplicationWithUniqueName(application,
//							scriptName);
//					for (Script o : list) {
//						sb.append(o.getText()).append(SystemUtils.LINE_SEPARATOR);
//					}
//				}
//				if (StringUtils.isNotEmpty(invoke.getText())) {
//					sb.append(invoke.getText()).append(System.lineSeparator());
//				}
				// sb.append("}).apply(bind);");
				Object o = engine.eval(sb.toString(), newContext);

				if (StringUtils.equals("seeOther", customResponse.type)) {
					WoSeeOther woSeeOther = new WoSeeOther(Objects.toString(customResponse.value, ""));
					result.setData(woSeeOther);
				} else if (StringUtils.equals("temporaryRedirect", customResponse.type)) {
					WoTemporaryRedirect woTemporaryRedirect = new WoTemporaryRedirect(
							Objects.toString(customResponse.value, ""));
					result.setData(woTemporaryRedirect);
				} else {
					if (null != customResponse.value) {
						wo.setValue(customResponse.value);
					} else {
						wo.setValue(o);
					}
					result.setData(wo);
				}
			} catch (Exception e) {
				throw new ExceptionExecuteError(invoke.getName(), e);
			}
			emc.beginTransaction(Invoke.class);
			invoke.setLastEndTime(new Date());
			emc.check(invoke, CheckPersistType.all);
			emc.commit();
			return result;
		}
	}

	public static class CustomResponse {
		protected String type = null;
		protected Object value;

		public void seeOther(String url) {
			this.type = "seeOther";
			this.value = url;
		}

		public void temporaryRedirect(String url) {
			this.type = "temporaryRedirect";
			this.value = url;
		}

		public void setBody(Object obj) {
			this.value = obj;
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
		private Applications applications;

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

		public Applications getApplications() {
			return applications;
		}

		public void setApplications(Applications applications) {
			this.applications = applications;
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