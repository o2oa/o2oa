package com.x.processplatform.assemble.surface.jaxrs.queryview;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.element.QueryView;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionExcelResult extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionExcelResult.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Ehcache cache = ApplicationCache.instance().getCache(QueryView.class);
		String cacheKey = ApplicationCache.concreteCacheKey(flag);
		Element element = cache.get(cacheKey);
		if (null != element && null != element.getObjectValue()) {
			ExcelResultObject obj = (ExcelResultObject) element.getObjectValue();
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), obj.getPerson())) {
				throw new ExceptionPersonNotMatch(effectivePerson.getDistinguishedName());
			}
			Wo wo = new Wo(obj.getBytes(), this.contentType(true, obj.getName()),
					this.contentDisposition(true, obj.getName()));
			result.setData(wo);
		} else {
			throw new ExceptionExcelResultObject(flag);
		}
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}