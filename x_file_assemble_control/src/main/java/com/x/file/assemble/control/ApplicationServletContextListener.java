package com.x.file.assemble.control;

import javax.servlet.annotation.WebListener;

import com.x.base.core.project.AbstractApplicationServletContextListener;
import com.x.base.core.project.ThisApplicationClass;
import com.x.base.core.project.x_file_assemble_control;

@WebListener
@ThisApplicationClass(ThisApplication.class)
public class ApplicationServletContextListener extends AbstractApplicationServletContextListener {
	public Class<?> getThis() {
		return x_file_assemble_control.class;
	}

}
