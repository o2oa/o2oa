package com.x.organization.core.entity.accredit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.list.TreeList;

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
	public Map<String, String> parameter = new HashMap<>();

	@FieldDescribe("数量")
	public Integer count = 0;

}
