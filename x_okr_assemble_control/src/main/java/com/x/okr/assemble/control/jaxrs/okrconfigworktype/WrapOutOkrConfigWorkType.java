package com.x.okr.assemble.control.jaxrs.okrconfigworktype;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigWorkType;
@Wrap( OkrConfigWorkType.class)
public class WrapOutOkrConfigWorkType extends OkrConfigWorkType{

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>();
	
	private Integer centerCount = 0;

	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public Integer getCenterCount() {
		return centerCount;
	}

	public void setCenterCount( Integer centerCount) {
		this.centerCount = centerCount;
	}
}
