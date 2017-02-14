package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( AttendanceWorkDayConfig.class)
public class WrapInFilter extends GsonPropertyObject {

	private String q_Name = null;
	
	private String q_Year = null;
	
	private String q_Month = null;

	public String getQ_Name() {
		return q_Name;
	}

	public void setQ_Name(String q_Name) {
		this.q_Name = q_Name;
	}

	public String getQ_Year() {
		return q_Year;
	}

	public void setQ_Year(String q_Year) {
		this.q_Year = q_Year;
	}

	public String getQ_Month() {
		return q_Month;
	}

	public void setQ_Month(String q_Month) {
		this.q_Month = q_Month;
	}	

}
