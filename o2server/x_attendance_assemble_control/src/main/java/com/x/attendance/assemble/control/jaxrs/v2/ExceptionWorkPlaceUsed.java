package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.project.exception.PromptException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionWorkPlaceUsed extends PromptException {


	private static final long serialVersionUID = 2153113315776048283L;

	public ExceptionWorkPlaceUsed(@NotNull List<AttendanceV2Group> groupList) {
		super("当前工作场所正在被考勤组【"+ groupList.stream().map(AttendanceV2Group::getGroupName).collect(Collectors.joining(",")) +"】使用！");
	}
}
