package com.x.program.center.jaxrs.module;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.WrapModule;

import net.sf.ehcache.Element;

public class ActionOutputFile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOutputFile.class);

	private static String extension = ".xapp";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Element element = cache.get(flag);
		if (null == element || null == element.getObjectValue()) {
			throw new ExceptionFlagNotExist(flag);
		}
		CacheObject cacheObject = (CacheObject) element.getObjectValue();
		WrapModule module = cacheObject.getModule();
		Wo wo = new Wo(gson.toJson(module).getBytes(DefaultCharset.name),
				this.contentType(true, module.getName() + extension),
				this.contentDisposition(true, module.getName() + extension));
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}