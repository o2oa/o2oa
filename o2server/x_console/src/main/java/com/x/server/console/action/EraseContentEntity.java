package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentEntity extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentEntity.class);

	@Override
	public boolean execute() throws Exception {
		this.init("entity", null);
		this.run();
		return true;
	}

}