package com.x.a.redeploy;

import org.junit.Test;

import com.x.base.core.project.x_calendar_assemble_control;
import com.x.base.core.project.x_calendar_core_entity;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_cms_core_entity;
import com.x.base.core.project.x_organization_assemble_authentication;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.x_processplatform_core_entity;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.x_query_assemble_surface;
import com.x.base.core.project.x_query_core_entity;
import com.x.base.core.project.x_report_assemble_control;
import com.x.base.core.project.x_report_core_entity;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class Redeploy_127_0_0_1 {

	private static Logger logger = LoggerFactory.getLogger(Redeploy_127_0_0_1.class);

	public static final String HOST = "localhost";

	public static Integer PORT = 20010;

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
	public void x_report_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_report_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_report_core_entity() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_report_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
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
	public void x_query_assemble_surface() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_query_assemble_surface.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_query_assemble_designer() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_query_assemble_designer.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_query_core_entity() {
		logger.print("result: {}.",
				Redeploy.redeploy(HOST, PORT, x_query_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_assemble_surface() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_processplatform_assemble_surface.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}
	

	@Test
	public void x_processplatform_service_processing() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_processplatform_service_processing.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

	@Test
	public void x_processplatform_core_entity() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_processplatform_core_entity.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}
	
	@Test
	public void x_organization_assemble_control() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_organization_assemble_control.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}
	
	@Test
	public void x_organization_assemble_authentication() {
		logger.print("result:{}",
				Redeploy.redeploy(HOST, PORT, x_organization_assemble_authentication.class, Redeploy.DEFAULT_PUBLIC_KEY));
	}

}
