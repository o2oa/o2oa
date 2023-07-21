package com.x.server.console.action;

import java.util.Collection;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class EraseContentEntity extends EraseContent {

	private static final Logger LOGGER = LoggerFactory.getLogger(EraseContentEntity.class);

	public boolean execute(Collection<String> names) {
		try {
			ClassLoader classLoader = EntityClassLoaderTools.concreteClassLoader();
			Thread.currentThread().setContextClassLoader(classLoader);
			this.init("entity", null, classLoader);
			for (String name : names) {
				this.addClass(classLoader.loadClass(name));
			}
			return execute();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	@Override
	public boolean execute() {
		this.run();
		return true;
	}

}