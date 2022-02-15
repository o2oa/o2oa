package com.x.base.core.project.jaxrs.cache;

import javax.servlet.ServletContext;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionDetail extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDetail.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext) throws Exception {
		LOGGER.debug("{} access by {}.", ActionDetail.class::getName, effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(new Wo(CacheManager.detail()));
		return result;
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 6523578259551600220L;

		public Wo(String str) {
			super(str);
		}

	}

}