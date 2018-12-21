package com.x.organization.assemble.express.jaxrs.role;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.CacheFactory;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

import net.sf.ehcache.Ehcache;

class BaseAction extends StandardJaxrsAction {

	Ehcache cache = CacheFactory.getOrganizationCache();

	static class WoRoleAbstract extends GsonPropertyObject {

		@FieldDescribe("角色识别名")
		private List<String> roleList = new ArrayList<>();

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

	}

	protected <T extends com.x.base.core.project.organization.Role> T convert(Business business, Role role,
			Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(role.getName());
		t.setDescription(role.getDescription());
		t.setUnique(role.getUnique());
		t.setDistinguishedName(role.getDistinguishedName());
		t.setOrderNumber(role.getOrderNumber());
		if (ListTools.isNotEmpty(role.getPersonList())) {
			for (String str : role.getPersonList()) {
				Person o = business.person().pick(str);
				t.getPersonList().add(o.getDistinguishedName());
			}
		}
		if (ListTools.isNotEmpty(role.getGroupList())) {
			for (String str : role.getGroupList()) {
				Group o = business.group().pick(str);
				t.getGroupList().add(o.getDistinguishedName());
			}
		}
		return t;
	}

}