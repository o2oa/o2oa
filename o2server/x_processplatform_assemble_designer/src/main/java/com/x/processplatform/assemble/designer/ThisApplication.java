package com.x.processplatform.assemble.designer;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;

	public static final ProjectionExecuteQueue projectionExecuteQueue = new ProjectionExecuteQueue();
	public static final MappingExecuteQueue mappingExecuteQueue = new MappingExecuteQueue();
	public static final FormVersionQueue formVersionQueue = new FormVersionQueue();
	public static final ProcessVersionQueue processVersionQueue = new ProcessVersionQueue();
	public static final ScriptVersionQueue scriptVersionQueue = new ScriptVersionQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			LoggerFactory.setLevel(Config.logLevel().x_processplatform_assemble_designer());
			MessageConnector.start(context());
			context().startQueue(projectionExecuteQueue);
			context().startQueue(mappingExecuteQueue);
			context().startQueue(formVersionQueue);
			context().startQueue(processVersionQueue);
			context().startQueue(scriptVersionQueue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}