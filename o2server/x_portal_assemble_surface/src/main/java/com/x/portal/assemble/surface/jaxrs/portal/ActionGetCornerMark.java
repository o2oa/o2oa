package com.x.portal.assemble.surface.jaxrs.portal;

import java.util.List;
import java.util.Optional;

import javax.script.CompiledScript;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapCount;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionGetCornerMark extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetCornerMark.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Portal o = business.portal().pick(flag);
			if (null == o) {
				throw new ExceptionPortalNotExist(flag);
			}
			if (!business.portal().visible(effectivePerson, o)) {
				throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), o.getName(), o.getId());
			}
			Wo wo = new Wo();
			wo.setCount(this.getCornerMark(o, business, effectivePerson));
			result.setData(wo);
			return result;
		}
	}

	private Long getCornerMark(Portal portal, Business business, EffectivePerson effectivePerson) {
		Long count = 0L;
		if (StringUtils.isNotBlank(portal.getCornerMarkScript())
				|| StringUtils.isNotBlank(portal.getCornerMarkScriptText())) {
			Source source = this.getCompiledScript(portal, business);
			if (source != null) {
				try {
					JsonElement element = GraalvmScriptingFactory.eval(source, business.binding(effectivePerson));
					if (element != null) {
						count = Long.valueOf(element.toString());
					}
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		}
		return count;
	}

	protected Source getCompiledScript(Portal portal, Business business) {
		Cache.CacheKey cacheKey = new Cache.CacheKey(ActionGetCornerMark.class, CompiledScript.class.getSimpleName(),
				portal.getId());
		Source source = null;
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			source = (Source) optional.get();
		} else {
			try {
				StringBuilder sb = new StringBuilder();
				if (StringUtils.isNotEmpty(portal.getCornerMarkScript())) {
					List<Script> list = business.script().listScriptNestedWithPortalWithFlag(portal,
							portal.getCornerMarkScript());
					for (Script script : list) {
						sb.append(script.getText()).append(System.lineSeparator());
					}
				}
				if (StringUtils.isNotEmpty(portal.getCornerMarkScriptText())) {
					sb.append(portal.getCornerMarkScriptText()).append(System.lineSeparator());
				}
				source = GraalvmScriptingFactory.functionalization(sb.toString());
				CacheManager.put(cacheCategory, cacheKey, source);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return source;
	}

	public static class Wo extends WrapCount {

		private static final long serialVersionUID = 4689550855437177187L;

	}
}
