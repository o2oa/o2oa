package com.x.hotpic.assemble.control;

import javax.servlet.annotation.WebListener;

import com.x.base.core.project.AbstractApplicationServletContextListener;
import com.x.base.core.project.ThisApplicationClass;
import com.x.base.core.project.x_hotpic_assemble_control;

@WebListener
@ThisApplicationClass( ThisApplication.class )
public class ApplicationServletContextListener extends AbstractApplicationServletContextListener {

	@Override
	public Class<?> getThis() {
		return x_hotpic_assemble_control.class;
	}
}