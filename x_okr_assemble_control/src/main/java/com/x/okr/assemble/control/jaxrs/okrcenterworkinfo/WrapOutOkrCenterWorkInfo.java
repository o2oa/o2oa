package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

@Wrap( OkrCenterWorkInfo.class )
public class WrapOutOkrCenterWorkInfo extends OkrCenterWorkInfo  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	private List<WrapOutOkrWorkBaseInfo> works = null;

	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public List<WrapOutOkrWorkBaseInfo> getWorks() {
		return works;
	}

	public void setWorks(List<WrapOutOkrWorkBaseInfo> works) {
		this.works = works;
	}
	
}