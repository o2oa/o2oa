package com.x.meeting.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.meeting.assemble.control.jaxrs.attachment.AttachmentAction;
import com.x.meeting.assemble.control.jaxrs.building.BuildingAction;
import com.x.meeting.assemble.control.jaxrs.config.ConfigAction;
import com.x.meeting.assemble.control.jaxrs.meeting.MeetingAction;
import com.x.meeting.assemble.control.jaxrs.openmeeting.OpenMeetingAction;
import com.x.meeting.assemble.control.jaxrs.room.RoomAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {
		classes.add(BuildingAction.class);
		classes.add(RoomAction.class);
		classes.add(MeetingAction.class);
		classes.add(AttachmentAction.class);
		classes.add(OpenMeetingAction.class);
		classes.add(ConfigAction.class);
		return classes;
	}

}
