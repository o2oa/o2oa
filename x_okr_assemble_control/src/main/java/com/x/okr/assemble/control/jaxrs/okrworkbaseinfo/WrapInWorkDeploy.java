package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInWorkDeploy.class)
public class WrapInWorkDeploy {

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	@EntityFieldDescribe( "需要部署的工作ID列表" )
	private List<String> workIds = null;

	public List<String> getWorkIds() {
		return workIds;
	}

	public void setWorkIds(List<String> workIds) {
		this.workIds = workIds;
	}	
}