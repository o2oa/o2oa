package com.x.query.core.express.statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Runtime extends GsonPropertyObject {

	@FieldDescribe("当前用户")
	public String person = "";

	@FieldDescribe("组织")
	public List<String> unitList = new TreeList<>();

	@FieldDescribe("群组")
	public List<String> groupList = new TreeList<>();

	@FieldDescribe("角色")
	public List<String> roleList = new TreeList<>();

	@FieldDescribe("所有群组")
	public List<String> unitAllList = new TreeList<>();

	@FieldDescribe("身份")
	public List<String> identityList = new TreeList<>();

	@FieldDescribe("参数")
	public Map<String, Object> parameter = new HashMap<>();

	@FieldDescribe("页码")
	public Integer page = 0;

	@FieldDescribe("每页大小")
	public Integer size = 20;

	public boolean hasParameter(String name) {
		if (StringUtils.isEmpty(name)) {
			return false;
		}
		if (StringUtils.equals(name, "person") || StringUtils.equals(name, "unitList")
				|| StringUtils.equals(name, "groupList") || StringUtils.equals(name, "roleList")
				|| StringUtils.equals(name, "unitAllList") || StringUtils.equals(name, "identityList")) {
			return true;
		}
		if ((null != this.parameter) && this.parameter.containsKey(name)) {
			return true;
		}
		return false;
	}

	public Object getParameter(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		if (StringUtils.equals(name, "person")) {
			return this.person;
		} else if (StringUtils.equals(name, "unitList")) {
			return this.unitList;
		} else if (StringUtils.equals(name, "groupList")) {
			return this.groupList;
		} else if (StringUtils.equals(name, "roleList")) {
			return roleList;
		} else if (StringUtils.equals(name, "unitAllList")) {
			return unitAllList;
		} else if (StringUtils.equals(name, "identityList")) {
			return identityList;
		} else if ((null != this.parameter) && this.parameter.containsKey(name)) {
			return this.parameter.get(name);
		}
		return null;
	}
}
