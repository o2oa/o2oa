package com.x.build.redeploy;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.*;
import org.junit.Test;

public class Redeploy_127_0_0_1 {

	private static Logger logger = LoggerFactory.getLogger(Redeploy_127_0_0_1.class);

	public static final String HOST = "127.0.0.1";

	public static Integer PORT = 20010;

	@Test
	public void x_hotpic_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_hotpic_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_file_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_file_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_cms_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_cms_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_cms_core_entity() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_cms_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_component_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_component_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_attendance_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_attendance_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_base_core_project() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_base_core_project.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_message_assemble_communicate() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_message_assemble_communicate.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_mind_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_mind_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_bbs_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_bbs_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_bbs_core_entity() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_bbs_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_calendar_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_calendar_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_calendar_core_entity() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_calendar_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_okr_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_okr_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_okr_core_entity() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_okr_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_mind_core_entity() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_mind_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_query_assemble_surface() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_query_assemble_surface.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_query_assemble_designer() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_query_assemble_designer.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_query_service_processing() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_query_service_processing.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_assemble_surface() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_processplatform_assemble_surface.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_assemble_designer() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_processplatform_assemble_designer.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_service_processing() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, x_processplatform_service_processing.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_core_entity() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, x_processplatform_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_meeting_assemble_control() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, x_meeting_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_organization_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_organization_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_organization_assemble_authentication() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_organization_assemble_authentication.class,
				Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_organization_assemble_personal() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_organization_assemble_personal.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_portal_assemble_surface() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_portal_assemble_surface.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_program_center() {
		logger.print("result:{}", Redeploy.redeploy(HOST, PORT, x_program_center.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_meeting_core_entity() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_meeting_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

 

	@Test
	public void x_wcrm_assemble_control() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, "x_wcrm_assemble_control", Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_teamwork_assemble_control() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, x_teamwork_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_teamwork_core_entity() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, "x_teamwork_core_entity", Redeploy.DEFAULT_PUBLIC_KEY));
	}
	
	@Test
	public void zoneland_unicom_bj_assemble_control() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, "zoneland_unicom_bj_assemble_control", Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_file_core_entity() {
		logger.print("result:{}.",
				Redeploy.redeploy(HOST, PORT, "x_file_core_entity",Redeploy.DEFAULT_PUBLIC_KEY));
	}
	

}
