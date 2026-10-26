package com.x.attendance.assemble.control.jaxrs;


import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.attendance.assemble.control.jaxrs.v2.appeal.AppealInfoAction;
import com.x.attendance.assemble.control.jaxrs.v2.config.ConfigAction;
import com.x.attendance.assemble.control.jaxrs.v2.detail.DetailAction;
import com.x.attendance.assemble.control.jaxrs.v2.group.GroupAction;
import com.x.attendance.assemble.control.jaxrs.v2.group.schedule.GroupScheduleAction;
import com.x.attendance.assemble.control.jaxrs.v2.leave.LeaveAction;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.LeaveManagerAction;
import com.x.attendance.assemble.control.jaxrs.v2.file.FileAction;
import com.x.attendance.assemble.control.jaxrs.v2.mobile.MobileAction;
import com.x.attendance.assemble.control.jaxrs.v2.my.MyAction;
import com.x.attendance.assemble.control.jaxrs.v2.record.RecordAction;
import com.x.attendance.assemble.control.jaxrs.v2.shift.ShiftAction;
import com.x.attendance.assemble.control.jaxrs.v2.workplace.WorkPlaceV2Action;
import com.x.base.core.project.jaxrs.AbstractActionApplication;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		// v2
		this.classes.add(ShiftAction.class);
		this.classes.add(GroupAction.class);
		this.classes.add(WorkPlaceV2Action.class);
		this.classes.add(MobileAction.class);
		this.classes.add(DetailAction.class);
		this.classes.add(ConfigAction.class);
		this.classes.add(AppealInfoAction.class);
		this.classes.add(MyAction.class);
		this.classes.add(LeaveAction.class);
		this.classes.add(RecordAction.class);
		this.classes.add(GroupScheduleAction.class);
		this.classes.add(LeaveManagerAction.class);
		this.classes.add(FileAction.class);
		return this.classes;
	}

}
