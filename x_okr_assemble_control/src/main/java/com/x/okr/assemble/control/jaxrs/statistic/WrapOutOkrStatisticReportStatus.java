package com.x.okr.assemble.control.jaxrs.statistic;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.timertask.entity.WorkBaseReportSubmitEntity;
import com.x.okr.entity.OkrStatisticReportStatus;

@Wrap( WrapOutOkrStatisticReportStatus.class)
public class WrapOutOkrStatisticReportStatus extends OkrStatisticReportStatus{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();

	private List<WorkBaseReportSubmitEntity> statistic = null;
	
	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public List<WorkBaseReportSubmitEntity> getStatistic() {
		return statistic;
	}

	public void setStatistic(List<WorkBaseReportSubmitEntity> statistic) {
		this.statistic = statistic;
	}

	
}
