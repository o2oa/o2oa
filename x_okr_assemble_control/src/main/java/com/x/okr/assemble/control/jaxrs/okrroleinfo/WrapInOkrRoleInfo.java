package com.x.okr.assemble.control.jaxrs.okrroleinfo;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrPermissionInfo;

@Wrap( OkrPermissionInfo.class)
public class WrapInOkrRoleInfo extends OkrConfigSystem {

	private static final long serialVersionUID = -5076990764713538973L;

	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	/**
	 * 角色所包含的权限编码列表
	 */
	private List<String> permissions = null;

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	
	

}
