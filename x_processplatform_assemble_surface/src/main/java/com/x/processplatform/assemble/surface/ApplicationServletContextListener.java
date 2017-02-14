package com.x.processplatform.assemble.surface;

import javax.servlet.annotation.WebListener;

import com.x.base.core.project.AbstractApplicationServletContextListener;
import com.x.base.core.project.ThisApplicationClass;
import com.x.base.core.project.x_processplatform_assemble_surface;

@WebListener
@ThisApplicationClass(ThisApplication.class)
public class ApplicationServletContextListener extends AbstractApplicationServletContextListener {

	public Class<?> getThis() {
		return x_processplatform_assemble_surface.class;
	}

}
