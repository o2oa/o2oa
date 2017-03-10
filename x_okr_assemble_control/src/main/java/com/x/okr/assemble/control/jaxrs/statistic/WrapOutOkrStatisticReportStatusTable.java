package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutOkrStatisticReportStatusTable.class)
public class WrapOutOkrStatisticReportStatusTable{

	private List<WrapOutOkrStatisticReportStatusHeader> header = null;
	
	private List<WrapOutOkrStatisticReportStatusEntity> content = null;	

	public List<WrapOutOkrStatisticReportStatusHeader> getHeader() {
		return header;
	}

	public void setHeader(List<WrapOutOkrStatisticReportStatusHeader> header) {
		this.header = header;
	}

	public List<WrapOutOkrStatisticReportStatusEntity> getContent() {
		return content;
	}

	public void setContent(List<WrapOutOkrStatisticReportStatusEntity> content) {
		this.content = content;
	}
}
