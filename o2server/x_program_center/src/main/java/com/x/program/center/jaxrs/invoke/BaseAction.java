package com.x.program.center.jaxrs.invoke;

import java.util.Objects;
import java.util.Optional;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoContentType;
import com.x.base.core.project.jaxrs.WoSeeOther;
import com.x.base.core.project.jaxrs.WoTemporaryRedirect;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.jaxrs.WoValue;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Invoke;

abstract class BaseAction extends StandardJaxrsAction {

	protected ActionResult<Object> executeInvoke(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement, CacheCategory cacheCategory, Invoke invoke)
			throws Exception, ExceptionExecuteError {
		ActionResult<Object> result = new ActionResult<>();
		CompiledScript compiledScript = this.getCompiledScript(cacheCategory, invoke);
		ScriptContext scriptContext = new SimpleScriptContext();
		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		Resources resources = new Resources();
		resources.setContext(ThisApplication.context());
		resources.setOrganization(new Organization(ThisApplication.context()));
		resources.setWebservicesClient(new WebservicesClient());
		resources.setApplications(ThisApplication.context().applications());
		bindings.put(ScriptFactory.BINDING_NAME_RESOURCES, resources);
		bindings.put("requestText", gson.toJson(jsonElement));
		bindings.put("request", request);
		bindings.put("effectivePerson", effectivePerson);
		bindings.put(ScriptFactory.BINDING_NAME_APPLICATIONS, ThisApplication.context().applications());
		CustomResponse customResponse = new CustomResponse();
		bindings.put("customResponse", customResponse);
		Wo wo = new Wo();
		try {
			ScriptFactory.initialServiceScriptText().eval(scriptContext);
			Object o = compiledScript.eval(scriptContext);
			if (StringUtils.equals("seeOther", customResponse.type)) {
				WoSeeOther woSeeOther = new WoSeeOther(Objects.toString(customResponse.value, ""));
				result.setData(woSeeOther);
			} else if (StringUtils.equals("temporaryRedirect", customResponse.type)) {
				WoTemporaryRedirect woTemporaryRedirect = new WoTemporaryRedirect(
						Objects.toString(customResponse.value, ""));
				result.setData(woTemporaryRedirect);
			} else {
				if (null != customResponse.value) {
					if (StringUtils.isNotEmpty(customResponse.contentType)) {
						result.setData(new WoContentType(customResponse.value, customResponse.contentType));
					} else if (customResponse.value instanceof WoText) {
						result.setData(customResponse.value);
					} else {
						wo.setValue(customResponse.value);
						result.setData(wo);
					}
				} else {
					wo.setValue(o);
					result.setData(wo);
				}
			}
		} catch (Exception e) {
			throw new ExceptionExecuteError(invoke.getName(), e);
		}
		return result;
	}

	protected CompiledScript getCompiledScript(CacheCategory cacheCategory, Invoke invoke) throws Exception {
		CacheKey cacheKey = new CacheKey(ActionExecuteToken.class, "CompiledScript", invoke.getId());
		CompiledScript compiledScript = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			compiledScript = (CompiledScript) optional.get();
		} else {
			compiledScript = ScriptFactory.compile(invoke.getText());
			CacheManager.put(cacheCategory, cacheKey, compiledScript);
		}
		return compiledScript;
	}

	protected Invoke get(CacheCategory cacheCategory, String flag) throws Exception {
		CacheKey cacheKey = new CacheKey(ActionExecuteToken.class, flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return (Invoke) optional.get();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Invoke invoke = emc.flag(flag, Invoke.class);
				if (null != invoke) {
					CacheManager.put(cacheCategory, cacheKey, invoke);
				}
				return invoke;
			}
		}
	}

	public static class CustomResponse {
		protected String type = null;
		protected Object value;
		protected String contentType;

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

		public void setBody(Object obj, String contentType) {
			this.value = obj;
			this.contentType = contentType;
		}

	}

	public static class Wo extends WoValue {

		private static final long serialVersionUID = -2253926744723217590L;

	}

	public static class Resources extends AbstractResources {
		private Organization organization;

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

	}

}
