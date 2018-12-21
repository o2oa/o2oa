package com.x.report.assemble.control.jaxrs.report.element;

import com.x.base.core.project.annotation.FieldDescribe;

public class PermissionInfo {

	@FieldDescribe( "权限类别：阅读|管理" )
	private String permission =  PermissionName.READER ;
	
	@FieldDescribe( "使用者类别：所有人|组织|人员|群组|角色" )
	private String permissionObjectType = "所有人";
	
	@FieldDescribe( "使用者编码：所有人|组织编码|人员UID|群组编码|角色编码" )
	private String permissionObjectCode = "所有人";
	
	@FieldDescribe( "使用者名称：所有人|组织名称|人员名称|群组名称|角色名称" )
	private String permissionObjectName = "所有人";

	public PermissionInfo() {}
	
	public PermissionInfo( String permission, String permissionObjectType, String permissionObjectCode, String permissionObjectName) {
		super();
		this.permission = permission;
		this.permissionObjectType = permissionObjectType;
		this.permissionObjectCode = permissionObjectCode;
		this.permissionObjectName = permissionObjectName;
	}

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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("'permission' : ").append("'").append(this.permission).append("',");
		sb.append("'permissionObjectType' : ").append("'").append(this.permissionObjectType).append("',");
		sb.append("'permissionObjectCode' : ").append("'").append(this.permissionObjectCode).append("',");
		sb.append("'permissionObjectName' : ").append("'").append(this.permissionObjectName).append("'");
		sb.append("}");
		return sb.toString();
	}
}
