package com.x.program.center.jaxrs.datastructure;

import com.x.base.core.project.*;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

	/**
	 * 所有需要加载的应用（按启动顺序）
	 */
	static final List<String> OFFICIAL_MODULE_SORTED_TEMPLATE = ListTools.toList(
			x_general_assemble_control.class.getName(), x_organization_assemble_authentication.class.getName(),
			x_organization_assemble_express.class.getName(), x_organization_assemble_control.class.getName(),
			x_organization_assemble_personal.class.getName(), x_component_assemble_control.class.getName(),
			x_message_assemble_communicate.class.getName(), x_calendar_assemble_control.class.getName(),
			x_processplatform_service_processing.class.getName(), x_processplatform_assemble_designer.class.getName(),
			x_processplatform_assemble_surface.class.getName(), x_processplatform_assemble_bam.class.getName(),
			x_cms_assemble_control.class.getName(), x_portal_assemble_designer.class.getName(),
			x_portal_assemble_surface.class.getName(), x_attendance_assemble_control.class.getName(),
			x_bbs_assemble_control.class.getName(), x_file_assemble_control.class.getName(),
			x_meeting_assemble_control.class.getName(), x_mind_assemble_control.class.getName(),
			x_okr_assemble_control.class.getName(), x_hotpic_assemble_control.class.getName(),
			x_query_service_processing.class.getName(), x_query_assemble_designer.class.getName(),
			x_query_assemble_surface.class.getName(), x_jpush_assemble_control.class.getName() );

}
