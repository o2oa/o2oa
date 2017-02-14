package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSRoleInfo;

@Wrap( BBSRoleInfo.class)
public class WrapInRoleInfo extends BBSRoleInfo{

	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	private List<String> permissionCodes = null;

	public List<String> getPermissionCodes() {
		return permissionCodes;
	}

	public void setPermissionCodes(List<String> permissionCodes) {
		this.permissionCodes = permissionCodes;
	}	
}