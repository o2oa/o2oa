package com.x.cms.assemble.control.jaxrs.documentpermission.element;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( PermissionInfo.class )
public class PermissionInfo {

	@EntityFieldDescribe( "权限类别：阅读|管理" )
	private String permission = "阅读";
	
	@EntityFieldDescribe( "使用者类别：所有人|组织|人员|群组|角色" )
	private String permissionObjectType = "所有人";
	
	@EntityFieldDescribe( "使用者编码：所有人|组织编码|人员UID|群组编码|角色编码" )
	private String permissionObjectCode = "所有人";
	
	@EntityFieldDescribe( "使用者名称：所有人|组织名称|人员名称|群组名称|角色名称" )
	private String permissionObjectName = "所有人";

	public String getPermission() {
		return permission;
	}

	public String getPermissionObjectType() {
		return permissionObjectType;
	}

	public String getPermissionObjectCode() {
		return permissionObjectCode;
	}

	public String getPermissionObjectName() {
		return permissionObjectName;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setPermissionObjectType(String permissionObjectType) {
		this.permissionObjectType = permissionObjectType;
	}

	public void setPermissionObjectCode(String permissionObjectCode) {
		this.permissionObjectCode = permissionObjectCode;
	}

	public void setPermissionObjectName(String permissionObjectName) {
		this.permissionObjectName = permissionObjectName;
	}	
}
