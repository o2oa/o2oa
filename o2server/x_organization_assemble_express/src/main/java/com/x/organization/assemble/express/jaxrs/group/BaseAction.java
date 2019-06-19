package com.x.organization.assemble.express.jaxrs.group;

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
import com.x.organization.core.entity.Unit;

import net.sf.ehcache.Ehcache;

class BaseAction extends StandardJaxrsAction {

	Ehcache cache = CacheFactory.getOrganizationCache();

	static class WoGroupAbstract extends GsonPropertyObject {

		@FieldDescribe("群组识别名")
		private List<String> groupList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

	}

	protected <T extends com.x.base.core.project.organization.Group> T convert(Business business, Group group,
			Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(group.getName());
		t.setDescription(group.getDescription());
		t.setUnique(group.getUnique());
		t.setDistinguishedName(group.getDistinguishedName());
		t.setOrderNumber(group.getOrderNumber());
		if (ListTools.isNotEmpty(group.getPersonList())) {
			for (String str : group.getPersonList()) {
				Person o = business.person().pick(str);
				t.getPersonList().add(o.getDistinguishedName());
			}
		}
		if (ListTools.isNotEmpty(group.getGroupList())) {
			for (String str : group.getGroupList()) {
				Group o = business.group().pick(str);
				t.getGroupList().add(o.getDistinguishedName());
			}
		}
		if (ListTools.isNotEmpty(group.getUnitList())) {
			for (String str : group.getUnitList()) {
				Unit o = business.unit().pick(str);
				t.getUnitList().add(o.getDistinguishedName());
			}
		}
		return t;
	}

}