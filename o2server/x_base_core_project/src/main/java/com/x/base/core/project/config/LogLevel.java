package com.x.base.core.project.config;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.logger.Logger;

public class LogLevel extends ConfigObject {

	@FieldDescribe("是否启用调试")
	private String x_program_center = "";

	@FieldDescribe("是否启用调试")
	private String x_processplatform_service_processing = "";

	@FieldDescribe("是否启用调试")
	private String x_processplatform_assemble_surface = "";

	@FieldDescribe("是否启用调试")
	private String x_processplatform_assemble_designer = "";

	@FieldDescribe("是否启用调试")
	private String x_query_assemble_designer = "";

	@FieldDescribe("是否启用调试")
	private String x_query_assemble_surface = "";

	@FieldDescribe("是否启用调试")
	private String x_query_service_processing = "";

	@FieldDescribe("是否启用调试")
	private String x_meeting_assemble_control = "";

	@FieldDescribe("是否启用调试")
	private String x_collaboration_service_message = "";

	@FieldDescribe("是否启用调试")
	private String x_collaboration_assemble_websocket = "";

	@FieldDescribe("是否启用调试")
	private String x_organization_assemble_authentication = "";

	@FieldDescribe("是否启用调试")
	private String x_general_assemble_control = "";

	public static LogLevel defaultInstance() {
		return new LogLevel();
	}

	public String x_program_center() {
		return this.get(this.x_program_center);
	}

	public String x_processplatform_service_processing() {
		return this.get(this.x_processplatform_service_processing);
	}

	public String x_processplatform_assemble_surface() {
		return this.get(this.x_processplatform_assemble_surface);
	}

	public String x_processplatform_assemble_designer() {
		return this.get(this.x_processplatform_assemble_designer);
	}

	public String x_query_assemble_surface() {
		return this.get(this.x_query_assemble_surface);
	}

	public String x_query_assemble_designer() {
		return this.get(this.x_query_assemble_designer);
	}

	public String x_query_service_processing() {
		return this.get(this.x_query_service_processing);
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

	public String x_organization_assemble_authentication() {
		return this.get(this.x_organization_assemble_authentication);
	}

	public String x_general_assemble_control() {
		return this.get(this.x_general_assemble_control);
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