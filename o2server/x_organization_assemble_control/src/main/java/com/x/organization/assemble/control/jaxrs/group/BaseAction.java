package com.x.organization.assemble.control.jaxrs.group;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;

public class BaseAction extends StandardJaxrsAction {

	// 如果唯一标识不为空,要检查唯一标识是否唯一
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, Group group) throws Exception {
		if (StringUtils.isNotEmpty(group.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(group.getId(), Group.class, group.getUnique())) {
				return true;
			}
		}
		return false;
	}

	public static class WoGroupAbstract extends Group {

		private static final long serialVersionUID = 5454920726133364605L;
		@FieldDescribe("对群组的操作权限")
		private Control control = new Control();

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}
	}

	public static class Control extends GsonPropertyObject {

		private static final long serialVersionUID = -711017560013703753L;

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

	protected <T extends WoGroupAbstract> void updateControl(EffectivePerson effectivePerson, Business business,
			List<T> list) throws Exception {
		if (effectivePerson.isSecurityManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.GroupManager)) {
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

	protected <T extends WoGroupAbstract> void updateControl(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		if (effectivePerson.isSecurityManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.GroupManager)) {
			t.getControl().setAllowDelete(true);
			t.getControl().setAllowEdit(true);
		} else {
			t.getControl().setAllowDelete(false);
			t.getControl().setAllowEdit(false);
		}
	}
}
