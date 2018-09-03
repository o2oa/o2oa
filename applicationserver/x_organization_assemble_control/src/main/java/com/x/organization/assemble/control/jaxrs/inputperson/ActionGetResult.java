package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import net.sf.ehcache.Element;

public class ActionGetResult extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetResult.class);

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		logger.debug(effectivePerson, "flag:{}.", flag);
		ActionResult<Wo> result = new ActionResult<>();
		Element element = cache.get(flag);
		if (null == element || (null == element.getObjectValue())) {
			throw new ExceptionResultNotFound(flag);
		}
		CacheInputResult o = (CacheInputResult) element.getObjectValue();
		Wo wo = new Wo(o.getBytes(), this.contentType(true, o.getName()), this.contentDisposition(true, o.getName()));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}