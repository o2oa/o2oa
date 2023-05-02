package com.x.base.core.project.organization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class PersonDetail extends GsonPropertyObject {

	private static final long serialVersionUID = -1577868963290077559L;

	@FieldDescribe("用户")
	private String distinguishedName = "";

	@FieldDescribe("身份")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("组织")
	private List<String> unitList = new ArrayList<>();

	@FieldDescribe("组织职务")
	private List<String> unitDutyList = new ArrayList<>();

	@FieldDescribe("群组")
	private List<String> groupList = new ArrayList<>();

	@FieldDescribe("角色")
	private List<String> roleList = new ArrayList<>();

	@FieldDescribe("人员属性")
	private List<String> personAttributeList = new ArrayList<>();

	public boolean isPerson(String person) {
		return StringUtils.equalsIgnoreCase(this.distinguishedName, person);
	}

	public boolean containsIdentity(String identity) {
		return identityList.contains(identity);
	}

	public boolean containsAnyIdentity(String... identities) {
		for (String identity : identities) {
			if (identityList.contains(identity)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsUnit(String unit) {
		return unitList.contains(unit);
	}

	public boolean containsAnyUnit(String... units) {
		for (String unit : units) {
			if (unitList.contains(unit)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsUnitDuty(String unitDuty) {
		return unitDutyList.contains(unitDuty);
	}

	public boolean containsAnyUnitDuty(String... unitDuties) {
		for (String unitDuty : unitDuties) {
			if (unitDutyList.contains(unitDuty)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsGroup(String group) {
		return groupList.contains(group);
	}

	public boolean containsAnyGroup(String... groups) {
		for (String group : groups) {
			if (groupList.contains(group)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsRole(String role) {
		if (roleList.contains(role)) {
			return true;
		}
		return roleList.contains(OrganizationDefinition.toDistinguishedName(role));
	}

	public boolean containsAnyRole(String... roles) {
		for (String role : roles) {
			if (roleList.contains(role)) {
				return true;
			}
			if (roleList.contains(OrganizationDefinition.toDistinguishedName(role))) {
				return true;
			}
		}
		return false;
	}

	public List<String> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<String> unitList) {
		this.unitList = unitList;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public List<String> getUnitDutyList() {
		return unitDutyList;
	}

	public void setUnitDutyList(List<String> unitDutyList) {
		this.unitDutyList = unitDutyList;
	}

	public List<String> getPersonAttributeList() {
		return personAttributeList;
	}

	public void setPersonAttributeList(List<String> personAttributeList) {
		this.personAttributeList = personAttributeList;
	}

}
