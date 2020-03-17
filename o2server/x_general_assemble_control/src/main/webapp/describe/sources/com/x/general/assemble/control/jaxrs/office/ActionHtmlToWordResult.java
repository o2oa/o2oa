package com.x.general.assemble.control.jaxrs.office;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.Element;

class ActionHtmlToWordResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionHtmlToWordResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.info("{}", flag);
		ActionResult<Wo> result = new ActionResult<>();
		String cacheKey = ApplicationCache.concreteCacheKey(flag);
		Element element = cache.get(cacheKey);
		if (null != element && null != element.getObjectValue()) {
			HtmlToWordResultObject obj = (HtmlToWordResultObject) element.getObjectValue();
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), obj.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
					this.contentDisposition(true, obj.getName()));
			result.setData(wo);
		} else {
			throw new ExceptionHtmlToWordResultObject(flag);
		}
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}