package com.x.onlyofficefile.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.organization.core.express.Organization;

public class Business {

	public static final String TOKEN_NAME = "xtoken";
	public static final String PROCESS_PLATFORM_APP = x_processplatform_assemble_surface.class.getSimpleName();
	public static final String CMS_APP = x_cms_assemble_control.class.getSimpleName();
	public static final String TEMPLATE_APP = "template";
	public static final String OFFICE_ONLINE_APP = "OfficeOnline";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}


}
