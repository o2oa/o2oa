package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapOutOkrWorkReportBaseInfo;
import com.x.okr.entity.OkrTask;

@Wrap( OkrTask.class)
public class WrapOutOkrTaskReportEntity extends OkrTask{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private String name = null;
	
	private List<WrapOutOkrWorkReportBaseInfo> reports = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WrapOutOkrWorkReportBaseInfo> getReports() {
		return reports;
	}

	public void setReports(List<WrapOutOkrWorkReportBaseInfo> reports) {
		this.reports = reports;
	}

	public void addReports( WrapOutOkrWorkReportBaseInfo wrapOutOkrWorkReportBaseInfo) {
		if( wrapOutOkrWorkReportBaseInfo == null ){
			return ;
		}
		
		if( reports == null ){
			reports = new ArrayList<WrapOutOkrWorkReportBaseInfo>();
		}
		
		if( !reports.contains( wrapOutOkrWorkReportBaseInfo )){
			reports.add( wrapOutOkrWorkReportBaseInfo );
		}
	}
}
