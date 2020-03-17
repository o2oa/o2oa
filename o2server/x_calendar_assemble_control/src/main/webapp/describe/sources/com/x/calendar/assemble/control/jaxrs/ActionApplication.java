package com.x.calendar.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.calendar.assemble.control.jaxrs.calendar.CalendarAction;
import com.x.calendar.assemble.control.jaxrs.event.Calendar_EventAction;
import com.x.calendar.assemble.control.jaxrs.event.Calendar_EventMessageAction;
import com.x.calendar.assemble.control.jaxrs.setting.CalendarSettingAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {

		this.classes.add(CalendarSettingAction.class);
		this.classes.add(Calendar_EventAction.class);
		this.classes.add(CalendarAction.class);
		this.classes.add(Calendar_EventMessageAction.class);
		return this.classes;
	}

}