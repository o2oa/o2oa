package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.entity.OkrCenterWorkInfo;

@Wrap( WrapOutOkrCenterWorkInfo.class )
public class WrapOutOkrCenterWorkInfo extends OkrCenterWorkInfo  {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	@EntityFieldDescribe( "所有的工作列表" )
	private List<WrapOutOkrWorkBaseInfo> works = null;
	
	@EntityFieldDescribe( "所有的工作类别列表" )
	private List<WrapOutOkrWorkType> workTypes = null;
	
	@EntityFieldDescribe( "用户可以对工作进行的操作(多值):CREATEWORK|IMPORTWORK|DEPLOY|ARCHIVE|CLOSE|DELETE" )
	private List<String> operation = null;
	
	@EntityFieldDescribe( "是否为新创建的草稿信息" )
	private Boolean isNew = true;

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

	public List<WrapOutOkrWorkType> getWorkTypes() {
		return workTypes;
	}

	public void setWorkTypes(List<WrapOutOkrWorkType> workTypes) {
		this.workTypes = workTypes;
	}

	public List<String> getOperation() {
		return operation;
	}

	public void setOperation(List<String> operation) {
		this.operation = operation;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}
	
}