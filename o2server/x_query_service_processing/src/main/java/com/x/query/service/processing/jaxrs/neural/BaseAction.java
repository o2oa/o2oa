package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.neural.Model;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cache = new CacheCategory(Model.class);

	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	static final Integer MAX_ATTACHMENT_BYTE_LENGTH = 10 * 1024 * 1024;

	static final String PROPERTY_WORKCOMPLETED = "workCompleted";
	static final String PROPERTY_DATA = "data";
	static final String PROPERTY_INVALUES = "inValues";
	static final String PROPERTY_ATTACHMENTS = "attachments";

}