package com.x.processplatform.service.processing.jaxrs.work;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;

/**
 * 创建处于start状态的work
 * 
 * @author Rui
 *
 */
public class ActionProcessing {

	protected WrapOutId execute(Business business, String id, ProcessingAttributes attributes) throws Exception {
		/** 校验work是否存在 */
		business.entityManagerContainer().find(id, Work.class, ExceptionWhen.not_found);
		if (null == attributes) {
			attributes = new ProcessingAttributes();
		}
		Processing processing = new Processing(attributes);
		processing.processing(id);
		WrapOutId wrap = new WrapOutId(id);
		return wrap;
	}

}
