package com.x.query.service.processing;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.service.processing.schedule.CrawlCms;
import com.x.query.service.processing.schedule.CrawlWork;
import com.x.query.service.processing.schedule.CrawlWorkCompleted;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_query_service_processing());
			if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
				context.schedule(CrawlWork.class, Config.query().getCrawlWork().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlWork().getEnable())) {
				context.schedule(CrawlWorkCompleted.class, Config.query().getCrawlWorkCompleted().getCron());
			}
			if (BooleanUtils.isTrue(Config.query().getCrawlCms().getEnable())) {
				context.schedule(CrawlCms.class, Config.query().getCrawlCms().getCron());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
