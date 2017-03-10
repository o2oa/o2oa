package com.x.cms.assemble.control.jaxrs.documentpermission;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.DocumentPermission;

@Wrap( DocumentPermission.class )
public class WrapInDocumentPermission {
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	@EntityFieldDescribe( "文档ID." )
	private String docId = null;
	
	@EntityFieldDescribe( "文档权限列表：List<PermissionInfo>" )
	private List<PermissionInfo> permissionList = null;

	public String getDocId() {
		return docId;
	}

	public List<PermissionInfo> getPermissionList() {
		return permissionList;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public void setPermissionList(List<PermissionInfo> permissionList) {
		this.permissionList = permissionList;
	}
	
	
}