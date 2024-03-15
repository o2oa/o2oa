package com.x.program.center.jaxrs.invoke;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

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
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Invoke;

abstract class BaseAction extends StandardJaxrsAction {

	protected static final String SPLIT = "#";
	private static final String SEEOTHER = "seeOther";
	private static final String TEMPORARYREDIRECT = "temporaryRedirect";

	@Deprecated(since = "8.3", forRemoval = true)
//	protected ActionResult<Object> executeInvoke(HttpServletRequest request, EffectivePerson effectivePerson,
//			JsonElement jsonElement, CacheCategory cacheCategory, Invoke invoke) throws Exception {
//		ActionResult<Object> result = new ActionResult<>();
//		CompiledScript compiledScript = this.getCompiledScript(cacheCategory, invoke);
//		ScriptContext scriptContext = ScriptingFactory.scriptContextEvalInitialServiceScript();
//		CustomResponse customResponse = new CustomResponse();
//		binding(request, effectivePerson, jsonElement, scriptContext, customResponse);
//		Wo wo = new Wo();
//		try {
//			JsonElement element = JsonScriptingExecutor.jsonElement(compiledScript, scriptContext);
//			if (StringUtils.equals(SEEOTHER, customResponse.type)) {
//				seeOther(result, customResponse);
//			} else if (StringUtils.equals(TEMPORARYREDIRECT, customResponse.type)) {
//				temporayRedirect(result, customResponse);
//			} else if (null != customResponse.value) {
//				if (StringUtils.isNotEmpty(customResponse.contentType)) {
//					result.setData(new WoContentType(customResponse.value, customResponse.contentType));
//				} else if (customResponse.value instanceof WoText) {
//					result.setData(customResponse.value);
//				} else {
//					wo.setValue(customResponse.value);
//					result.setData(wo);
//				}
//			} else {
//				wo.setValue(element);
//				result.setData(wo);
//			}
//		} catch (Exception e) {
//			throw new ExceptionInvokeExecute(e, invoke.getId(), invoke.getName());
//		}
//		return result;
//	}

	private void temporayRedirect(ActionResult<Object> result, CustomResponse customResponse) {
		WoTemporaryRedirect woTemporaryRedirect = new WoTemporaryRedirect(Objects.toString(customResponse.value, ""));
		result.setData(woTemporaryRedirect);
	}

	private void seeOther(ActionResult<Object> result, CustomResponse customResponse) {
		WoSeeOther woSeeOther = new WoSeeOther(Objects.toString(customResponse.value, ""));
		result.setData(woSeeOther);
	}

//	@Deprecated(since = "8.3", forRemoval = true)
//	private void binding(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement,
//			ScriptContext scriptContext, CustomResponse customResponse) throws Exception {
//		Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
//		Resources resources = new Resources();
//		resources.setContext(ThisApplication.context());
//		resources.setOrganization(new Organization(ThisApplication.context()));
//		resources.setWebservicesClient(new WebservicesClient());
//		resources.setApplications(ThisApplication.context().applications());
//		bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources);
//		bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_REQUESTTEXT, gson.toJson(jsonElement));
//		bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_REQUEST, request);
//		bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_EFFECTIVEPERSON, effectivePerson);
//		bindings.put(ScriptingFactory.BINDING_NAME_SERVICE_CUSTOMRESPONSE, customResponse);
//	}

//	@Deprecated(since = "8.3", forRemoval = true)
//	protected CompiledScript getCompiledScript(CacheCategory cacheCategory, Invoke invoke) throws ScriptException {
//		CacheKey cacheKey = new CacheKey(ActionExecuteToken.class, CompiledScript.class.getSimpleName(),
//				invoke.getId());
//		CompiledScript compiledScript = null;
//		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
//		if (optional.isPresent()) {
//			compiledScript = (CompiledScript) optional.get();
//		} else {
//			compiledScript = ScriptingFactory.functionalizationCompile(invoke.getText());
//			CacheManager.put(cacheCategory, cacheKey, compiledScript);
//		}
//		return compiledScript;
//	}

	protected Invoke get(CacheCategory cacheCategory, String flag) throws Exception {
		CacheKey cacheKey = new CacheKey(ActionExecuteToken.class, flag);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			return (Invoke) optional.get();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Invoke invoke = emc.flag(flag, Invoke.class);
				if (null != invoke) {
					emc.get(Invoke.class).detach(invoke);
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
			this.type = SEEOTHER;
			this.value = url;
		}

		public void temporaryRedirect(String url) {
			this.type = TEMPORARYREDIRECT;
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

	protected void checkEnable(Invoke invoke) throws ExceptionNotEnable {
		if (!BooleanUtils.isTrue(invoke.getEnable())) {
			throw new ExceptionNotEnable(invoke.getName());
		}
	}

	protected void checkRemoteAddrRegex(HttpServletRequest request, Invoke invoke) throws ExceptionInvalidRemoteAddr {
		if (StringUtils.isNotEmpty(invoke.getRemoteAddrRegex())) {
			Matcher matcher = Pattern.compile(invoke.getRemoteAddrRegex()).matcher(request.getRemoteAddr());
			if (!matcher.find()) {
				throw new ExceptionInvalidRemoteAddr(request.getRemoteAddr(), invoke.getName());
			}
		}
	}

	protected void checkToken(String token) throws ExceptionTokenEmpty {
		if (StringUtils.isEmpty(token)) {
			throw new ExceptionTokenEmpty();
		}
	}

	protected void checkClient(String client) throws ExceptionClientEmpty {
		if (StringUtils.isEmpty(client)) {
			throw new ExceptionClientEmpty();
		}
	}

	protected Source getSource(CacheCategory cacheCategory, Invoke invoke) {
		CacheKey cacheKey = new CacheKey(ActionExecuteToken.class, Source.class.getSimpleName(), invoke.getId());
		Source source = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			source = GraalvmScriptingFactory.functionalization(invoke.getText());
			CacheManager.put(cacheCategory, cacheKey, source);
		}
		return source;
	}

	protected ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement, CacheCategory cacheCategory, Invoke invoke) throws Exception {
		ActionResult<Object> result = new ActionResult<>();
		CustomResponse customResponse = new CustomResponse();
		Resources resources = new Resources();
		resources.setContext(ThisApplication.context());
		resources.setOrganization(new Organization(ThisApplication.context()));
		resources.setWebservicesClient(new WebservicesClient());
		resources.setApplications(ThisApplication.context().applications());
		GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
				.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources)
				.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_REQUESTTEXT, gson.toJson(jsonElement))
				.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_REQUEST, request)
				.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_EFFECTIVEPERSON, gson.toJson(effectivePerson))
				.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_CUSTOMRESPONSE, customResponse);
		Wo wo = new Wo();
		try {
			Source source = this.getSource(cacheCategory, invoke);
			JsonElement element = GraalvmScriptingFactory.eval(source, bindings);
			if (StringUtils.equals(SEEOTHER, customResponse.type)) {
				seeOther(result, customResponse);
			} else if (StringUtils.equals(TEMPORARYREDIRECT, customResponse.type)) {
				temporayRedirect(result, customResponse);
			} else if (null != customResponse.value) {
				if (StringUtils.isNotEmpty(customResponse.contentType)) {
					result.setData(new WoContentType(customResponse.value, customResponse.contentType));
				} else if (customResponse.value instanceof WoText) {
					result.setData(customResponse.value);
				} else {
					wo.setValue(customResponse.value);
					result.setData(wo);
				}
			} else {
				wo.setValue(element);
				result.setData(wo);
			}
		} catch (Exception e) {
			throw new ExceptionInvokeExecute(e, invoke.getId(), invoke.getName());
		}
		return result;
	}
}