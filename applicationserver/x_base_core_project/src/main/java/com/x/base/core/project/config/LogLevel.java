package com.x.base.core.project.config;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;

public class LogLevel extends GsonPropertyObject {

	private String x_processplatform_service_processing = "";

	private String x_query_assemble_designer = "";

	private String x_meeting_assemble_control = "";

	private String x_collaboration_service_message = "";

	private String x_collaboration_assemble_websocket = "";

	public static LogLevel defaultInstance() {
		return new LogLevel();
	}

	public String x_processplatform_service_processing() {
		return this.get(this.x_processplatform_service_processing);
	}

	public String x_query_assemble_designer() {
		return this.get(this.x_query_assemble_designer);
	}

	public String x_meeting_assemble_control() {
		return this.get(this.x_meeting_assemble_control);
	}

	public String x_collaboration_service_message() {
		return this.get(this.x_collaboration_service_message);
	}

	public String x_collaboration_assemble_websocket() {
		return this.get(this.x_collaboration_assemble_websocket);
	}

	private String get(String str) {
		if (StringUtils.equalsIgnoreCase(str, Logger.ERROR)) {
			return Logger.ERROR;
		}
		if (StringUtils.equalsIgnoreCase(str, Logger.WARN)) {
			return Logger.WARN;
		}
		if (StringUtils.equalsIgnoreCase(str, Logger.INFO)) {
			return Logger.INFO;
		}
		if (StringUtils.equalsIgnoreCase(str, Logger.DEBUG)) {
			return Logger.DEBUG;
		}
		if (StringUtils.equalsIgnoreCase(str, Logger.TRACE)) {
			return Logger.TRACE;
		}
		return Logger.INFO;
	}
}