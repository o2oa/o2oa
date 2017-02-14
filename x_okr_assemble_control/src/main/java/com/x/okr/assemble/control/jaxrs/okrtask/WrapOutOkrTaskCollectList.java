package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrTask;

@Wrap( OkrTask.class)
public class WrapOutOkrTaskCollectList extends OkrTask{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private List<String> organizationNames = null;
	
	private List<WrapOutOkrTaskReportEntity> reportInfos = null;	
	
	public List<String> getOrganizationNames() {
		return organizationNames;
	}
	
	public void setOrganizationNames(List<String> organizationNames) {
		this.organizationNames = organizationNames;
	}

	public List<WrapOutOkrTaskReportEntity> getReportInfos() {
		return reportInfos;
	}

	public void setReportInfos(List<WrapOutOkrTaskReportEntity> reportInfos) {
		this.reportInfos = reportInfos;
	}



	public void addOrganizationName( String organizationName ) {
		if( organizationName == null ){
			return ;
		}
		
		if( organizationNames == null ){
			organizationNames = new ArrayList<String>();
		}
		
		if( !organizationNames.contains( organizationName )){
			organizationNames.add( organizationName );
		}
	}
}
