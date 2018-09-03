package com.x.program.center.schedule;

import org.quartz.Job;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;

abstract class BaseAction implements Job {

	static Gson gson = XGsonBuilder.instance();

	static final String ADDRESS_COLLECT_TRANSMIT_RECEIVE = "/o2_collect_assemble/jaxrs/collect/transmit/receive";

	static final String ADDRESS_COLLECT_PROMPTERRORLOG = "/o2_collect_assemble/jaxrs/collect/prompterrorlog/receive";

	static final String ADDRESS_COLLECT_UNEXPECTEDERRORLOG = "/o2_collect_assemble/jaxrs/collect/unexpectederrorlog/receive";

	static final String ADDRESS_COLLECT_WARNLOG = "/o2_collect_assemble/jaxrs/collect/warnlog/receive";

	static final String ADDRESS_COLLECT_REMOTE_IP = "/o2_collect_assemble/jaxrs/remote/ip";

}
