package com.x.organization.assemble.custom;

import javax.servlet.annotation.WebListener;

import com.x.base.core.project.AbstractApplicationServletContextListener;
import com.x.base.core.project.ThisApplicationClass;
import com.x.base.core.project.x_organization_assemble_custom;

@WebListener
@ThisApplicationClass(ThisApplication.class)
public class ApplicationServletContextListener extends AbstractApplicationServletContextListener {

	public Class<?> getThis() {
		return x_organization_assemble_custom.class;
	}

}
