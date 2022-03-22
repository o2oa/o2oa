package com.x.server.console.action;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentBbs extends EraseContent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentBbs.class);

	@Override
	public boolean execute() {
		try {
			ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
			Thread.currentThread().setContextClassLoader(classLoader);
			this.init("bbs", ItemCategory.bbs, classLoader);
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSOperationRecord"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSReplyInfo"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSSubjectAttachment"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSSubjectContent"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSSubjectInfo"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSSubjectVoteResult"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSVoteOption"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSVoteOptionGroup"));
			addClass(classLoader.loadClass("com.x.bbs.entity.BBSVoteRecord"));
			this.run();
			return true;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}
}