package com.x.processplatform.service.processing;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.collaboration.core.message.Collaboration;
import com.x.processplatform.service.processing.schedule.Delay;
import com.x.processplatform.service.processing.schedule.Expire;
import com.x.processplatform.service.processing.schedule.Reorganize;
import com.x.processplatform.service.processing.schedule.Urge;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_processplatform_service_processing());
			ScriptHelperFactory.initialScriptText = Config.initialScriptText();
			Collaboration.start(context());
			MessageConnector.start(context());
			if (BooleanUtils.isTrue(Config.processPlatform().getUrge().getEnable())) {
				context.schedule(Urge.class, Config.processPlatform().getUrge().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getExpire().getEnable())) {
				context.schedule(Expire.class, Config.processPlatform().getExpire().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getDelay().getEnable())) {
				context.schedule(Delay.class, Config.processPlatform().getDelay().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getReorganize().getEnable())) {
				context.schedule(Reorganize.class, Config.processPlatform().getReorganize().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			Collaboration.stop();
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
