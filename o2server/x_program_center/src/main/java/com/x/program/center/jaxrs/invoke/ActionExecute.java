package com.x.program.center.jaxrs.invoke;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoContentType;
import com.x.base.core.project.jaxrs.WoSeeOther;
import com.x.base.core.project.jaxrs.WoTemporaryRedirect;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.jaxrs.WoValue;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.Invoke;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	public static Ehcache CACHE = ApplicationCache.instance().getCache(Invoke.class);

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
			emc.beginTransaction(Invoke.class);
			invoke.setLastStartTime(new Date());
			emc.commit();

			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), invoke.getId());

			CompiledScript compiledScript = null;

			Element element = CACHE.get(cacheKey);

			if ((null != element) && (null != element.getObjectValue())) {
				compiledScript = (CompiledScript) element.getObjectValue();
			} else {
				compiledScript = ScriptFactory.compile(invoke.getText());
				CACHE.put(new Element(cacheKey, compiledScript));
			}
			ScriptContext scriptContext = new SimpleScriptContext();
			Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
			Resources resources = new Resources();
			resources.setEntityManagerContainer(emc);
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