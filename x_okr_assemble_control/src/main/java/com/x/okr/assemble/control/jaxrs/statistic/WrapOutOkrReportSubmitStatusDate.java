package com.x.okr.assemble.control.jaxrs.statistic;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrReportSubmitStatusDate.class)
public class WrapOutOkrReportSubmitStatusDate{

	private String datetime = null;
	
	private String reportCycle = null;

	public WrapOutOkrReportSubmitStatusDate( String datetime, String reportCycle ){
		this.datetime = datetime;
		this.reportCycle = reportCycle;
	}
	
	public String getDatetime() {
		return datetime;
	}

	public String getReportCycle() {
		return reportCycle;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}
	
	
	
}
