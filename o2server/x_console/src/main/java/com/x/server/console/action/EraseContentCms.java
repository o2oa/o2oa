package com.x.server.console.action;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentCms extends EraseContent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentCms.class);

	@Override
	public boolean execute() {
		try {
			ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
			Thread.currentThread().setContextClassLoader(classLoader);
			this.init("cms", ItemCategory.cms, classLoader);
			addClass(classLoader.loadClass("com.x.cms.core.entity.CmsBatchOperation"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.Document"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.DocumentCommend"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.DocumentCommentCommend"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.DocumentCommentContent"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.DocumentCommentInfo"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.DocumentViewRecord"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.FileInfo"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.Log"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.ReadRemind"));
			addClass(classLoader.loadClass("com.x.cms.core.entity.Review"));
			addClass(classLoader.loadClass("com.x.query.core.entity.Item"));
			this.run();
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}
}