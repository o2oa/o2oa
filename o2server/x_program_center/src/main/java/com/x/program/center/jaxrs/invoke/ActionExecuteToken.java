package com.x.program.center.jaxrs.invoke;

import java.net.URLDecoder;
import java.util.Date;
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
import com.x.base.core.project.tools.Crypto;
import com.x.program.center.core.entity.Invoke;

class ActionExecuteToken extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecuteToken.class);

	private static final String SPLIT = "#";

	ActionResult<Object> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag, String token,
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

		String content = Crypto.decrypt(token, invoke.getKey());

		String name = URLDecoder.decode(StringUtils.substringBefore(content, SPLIT), "UTF-8");
		String timeString = StringUtils.substringAfter(content, SPLIT);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionTokenNameEmpty();
		}

		if (!StringUtils.equalsIgnoreCase(name, invoke.getName())) {
			throw new ExceptionTokenNameNotMatch(name);
		}

		Date date = new Date(Long.parseLong(timeString));
		Date now = new Date();
		if (Math.abs((now.getTime() - date.getTime())) >= (60000 * 15)) {
			throw new ExceptionTokenExpired();
		}

		return executeInvoke(request, effectivePerson, jsonElement, cacheCategory, invoke);
	}

}