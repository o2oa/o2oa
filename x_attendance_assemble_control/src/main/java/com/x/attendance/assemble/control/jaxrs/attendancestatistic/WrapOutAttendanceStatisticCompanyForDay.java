package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.entity.StatisticCompanyForDay;
import com.x.base.core.http.annotation.Wrap;

@Wrap( StatisticCompanyForDay.class )
public class WrapOutAttendanceStatisticCompanyForDay extends StatisticCompanyForDay  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
}