package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.WrapOutPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;

@Wrap( BBSRoleInfo.class)
public class WrapOutRoleInfo extends BBSRoleInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();	
	
	private List<WrapOutPermissionInfo> permissions = null;

	public List<WrapOutPermissionInfo> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<WrapOutPermissionInfo> permissions) {
		this.permissions = permissions;
	}
	
}
