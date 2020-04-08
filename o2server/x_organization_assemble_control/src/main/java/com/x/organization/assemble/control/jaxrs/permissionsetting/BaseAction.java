package com.x.organization.assemble.control.jaxrs.permissionsetting;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PermissionSetting;
import com.x.organization.core.entity.PersonCard;
import com.x.organization.core.entity.Role;

abstract class BaseAction extends StandardJaxrsAction {

	
	public static class WoPermissionSettingAbstract extends PermissionSetting {

		private static final long serialVersionUID = 8148720363115902733L;
	}
	
	// 初始化信息
		public PersonCard initDefaultValue(EffectivePerson effectivePerson, PersonCard personCard,PersonCard newCard) {
		    //System.out.println("personCard_groupType="+personCard.getStatus());
		    //System.out.println("newCard_groupType="+newCard);
			return personCard;
		}
	
	
	/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, Role role) throws Exception {
		if (StringUtils.isNotEmpty(role.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(role.getId(), Role.class, role.getUnique())) {
				return true;
			}
		}
		return false;
	}

	public static class WoRoleAbstract extends Role {

		private static final long serialVersionUID = 8148720363115902733L;
		@FieldDescribe("对角色的操作权限")
		private Control control = new Control();

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}
	}

	public static class Control extends GsonPropertyObject {

		private Boolean allowEdit = false;
		private Boolean allowDelete = false;

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowDelete() {
			return allowDelete;
		}

		public void setAllowDelete(Boolean allowDelete) {
			this.allowDelete = allowDelete;
		}

	}

	protected <T extends WoRoleAbstract> void updateControl(EffectivePerson effectivePerson, Business business,
			List<T> list) throws Exception {
		if (effectivePerson.isManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.RoleManager)) {
			for (T t : list) {
				t.getControl().setAllowDelete(true);
				t.getControl().setAllowEdit(true);
			}
		} else {
			for (T t : list) {
				t.getControl().setAllowDelete(false);
				t.getControl().setAllowEdit(false);
			}
		}
	}

	protected <T extends WoRoleAbstract> void updateControl(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		if (effectivePerson.isManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.RoleManager)) {
			t.getControl().setAllowDelete(true);
			t.getControl().setAllowEdit(true);
		} else {
			t.getControl().setAllowDelete(false);
			t.getControl().setAllowEdit(false);
		}
	}
}
