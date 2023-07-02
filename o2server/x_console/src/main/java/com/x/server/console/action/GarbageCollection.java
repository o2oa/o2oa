package com.x.server.console.action;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class GarbageCollection {

	private static final Logger LOGGER = LoggerFactory.getLogger(GarbageCollection.class);

	public boolean execute() {
		Runtime.getRuntime().gc();
		LOGGER.print("runtime gc called.");
		return true;
	}

}