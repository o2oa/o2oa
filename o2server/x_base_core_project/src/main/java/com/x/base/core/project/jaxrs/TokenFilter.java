package com.x.base.core.project.jaxrs;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.net.HttpHeaders;
import com.x.base.core.project.config.Config;

public abstract class TokenFilter implements Filter {

	private static Optional<Pattern> refererPattern;

	public static final String HTTP_OPTIONS = "options";

	protected void options(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(204);
	}

	private static Optional<Pattern> getRefererPattern() {
		try {
			if (Objects.isNull(refererPattern)) {
				synchronized (TokenFilter.class) {
					if (StringUtils.isNotBlank(Config.general().getRefererHeadCheckRegular())) {
						refererPattern = Optional.of(Pattern.compile(Config.general().getRefererHeadCheckRegular(),Pattern.CASE_INSENSITIVE));
					} else {
						refererPattern = Optional.empty();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refererPattern;
	}

	protected void httpRequestCheck(HttpServletRequest request) throws IllegalAccessException {
		if (getRefererPattern().isPresent()) {
			String referer = request.getHeader(HttpHeaders.REFERER);
			if (StringUtils.isNotBlank(referer) && (!getRefererPattern().get().matcher(referer).find())) {
				throw new IllegalAccessException("illegal http request.");
			}
		}
	}
}
