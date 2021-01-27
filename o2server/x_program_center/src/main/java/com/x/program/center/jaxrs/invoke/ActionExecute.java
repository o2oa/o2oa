package com.x.program.center.jaxrs.invoke;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Invoke;

class ActionExecute extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag,
			JsonElement jsonElement) throws Exception {

		CacheCategory cacheCategory = new CacheCategory(Invoke.class);

		Invoke invoke = this.get(cacheCategory, flag);

		if (null == invoke) {
			throw new ExceptionEntityNotExist(flag, Invoke.class);
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
		
		if (BooleanUtils.isTrue(invoke.getEnableToken())) {
			throw new ExceptionEnableToken(invoke.getName());
		}

		return executeInvoke(request, effectivePerson, jsonElement, cacheCategory, invoke);
	}
}