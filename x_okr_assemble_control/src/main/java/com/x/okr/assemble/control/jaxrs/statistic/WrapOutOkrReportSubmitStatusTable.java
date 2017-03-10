package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrReportSubmitStatusTable.class)
public class WrapOutOkrReportSubmitStatusTable{

	private List<WrapOutOkrReportSubmitStatusStatisticHeader> header = null;
	
	private List<WrapOutOkrReportSubmitStatusStatisticEntity> content = null;	

	public List<WrapOutOkrReportSubmitStatusStatisticHeader> getHeader() {
		return header;
	}

	public void setHeader(List<WrapOutOkrReportSubmitStatusStatisticHeader> header) {
		this.header = header;
	}

	public List<WrapOutOkrReportSubmitStatusStatisticEntity> getContent() {
		return content;
	}

	public void setContent(List<WrapOutOkrReportSubmitStatusStatisticEntity> content) {
		this.content = content;
	}
}
