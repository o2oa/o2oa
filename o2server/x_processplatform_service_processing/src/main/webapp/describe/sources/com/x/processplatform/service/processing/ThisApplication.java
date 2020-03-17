package com.x.processplatform.service.processing;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.processplatform.service.processing.processor.embed.SyncEmbedQueue;
import com.x.processplatform.service.processing.processor.invoke.SyncJaxrsInvokeQueue;
import com.x.processplatform.service.processing.processor.invoke.SyncJaxwsInvokeQueue;
import com.x.processplatform.service.processing.schedule.DataMerge;
import com.x.processplatform.service.processing.schedule.DeleteDraft;
import com.x.processplatform.service.processing.schedule.Expire;
import com.x.processplatform.service.processing.schedule.LogLongDetained;
import com.x.processplatform.service.processing.schedule.PassExpired;
import com.x.processplatform.service.processing.schedule.TouchDelay;
import com.x.processplatform.service.processing.schedule.TouchDetained;
import com.x.processplatform.service.processing.schedule.Urge;

public class ThisApplication {

	protected static Context context;

	public static SyncJaxrsInvokeQueue syncJaxrsInvokeQueue = new SyncJaxrsInvokeQueue();

	public static SyncJaxwsInvokeQueue syncJaxwsInvokeQueue = new SyncJaxwsInvokeQueue();

	public static SyncEmbedQueue syncEmbedQueue = new SyncEmbedQueue();

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_processplatform_service_processing());
			MessageConnector.start(context());
			context().startQueue(syncJaxrsInvokeQueue);
			context().startQueue(syncJaxwsInvokeQueue);
			context().startQueue(syncEmbedQueue);
			if (BooleanUtils.isTrue(Config.processPlatform().getDataMerge().getEnable())) {
				context.schedule(DataMerge.class, Config.processPlatform().getDataMerge().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getDeleteDraft().getEnable())) {
				context.schedule(DeleteDraft.class, Config.processPlatform().getDeleteDraft().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getExpire().getEnable())) {
				context.schedule(Expire.class, Config.processPlatform().getExpire().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getLogLongDetained().getEnable())) {
				context.schedule(LogLongDetained.class, Config.processPlatform().getLogLongDetained().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getPassExpired().getEnable())) {
				context.schedule(PassExpired.class, Config.processPlatform().getPassExpired().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getTouchDelay().getEnable())) {
				context.schedule(TouchDelay.class, Config.processPlatform().getTouchDelay().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getTouchDetained().getEnable())) {
				context.schedule(TouchDetained.class, Config.processPlatform().getTouchDetained().getCron());
			}
			if (BooleanUtils.isTrue(Config.processPlatform().getUrge().getEnable())) {
				context.schedule(Urge.class, Config.processPlatform().getUrge().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			MessageConnector.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
