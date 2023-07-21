package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNoTodayRecordList extends PromptException {

 

	public ExceptionNoTodayRecordList() {
		super(  "没有找到打卡记录列表信息.");
	}
}
