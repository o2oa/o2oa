package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.documentpermission.element.PermissionInfo;
import com.x.cms.core.entity.Document;

@Wrap(Document.class)
public class WrapInDocument extends Document {
	
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);

	private String identity = null;
	
	private List<PermissionInfo> permissionList = null;
	
	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public List<PermissionInfo> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<PermissionInfo> permissionList) {
		this.permissionList = permissionList;
	}
	
}