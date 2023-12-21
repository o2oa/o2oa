package com.x.program.center.jaxrs.invoke;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Invoke;

class ActionExecute extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("{} invoke :{}.", effectivePerson.getDistinguishedName(), flag);

		CacheCategory cacheCategory = new CacheCategory(Invoke.class);

		Invoke invoke = this.get(cacheCategory, flag);

		if (null == invoke) {
			throw new ExceptionEntityNotExist(flag, Invoke.class);
		}

		checkEnable(invoke);

		checkRemoteAddrRegex(request, invoke);

		if (BooleanUtils.isTrue(invoke.getEnableToken())) {
			throw new ExceptionEnableToken(invoke.getName());
		}

		return execute(request, effectivePerson, jsonElement, cacheCategory, invoke);

	}

}