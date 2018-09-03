package com.x.base.core.project.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;

public class OrganizationDefinition {

	public final static String Manager = "Manager";

	public final static String AttendanceManager = "AttendanceManager";

	public final static String OrganizationManager = "OrganizationManager";

	public final static String PersonManager = "PersonManager";

	public final static String GroupManager = "GroupManager";

	public final static String UnitManager = "UnitManager";

	public final static String RoleManager = "RoleManager";

	public final static String ProcessPlatformManager = "ProcessPlatformManager";

	public final static String ProcessPlatformCreator = "ProcessPlatformCreator";

	public final static String MeetingManager = "MeetingManager";

	public final static String MeetingViewer = "MeetingViewer";

	public final static String PortalManager = "PortalManager";

	public final static String BBSManager = "BSSManager";

	public final static String CMSManager = "CMSManager";

	public final static String OKRManager = "OKRManager";

	public final static String CRMManager = "CRMManager";

	public final static String QueryManager = "QueryManager";

	public final static String RoleDefinitionSuffix = "SystemRole";

	public final static Pattern person_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");

	public final static Pattern personAttribute_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@PA$");

	public final static Pattern group_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@G$");

	public final static Pattern role_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@R$");

	public final static Pattern identity_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@I$");

	public final static Pattern unit_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@U$");

	public final static Pattern unitAttribute_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UA$");

	public final static Pattern unitDuty_distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UD$");

	public static String isName(String distinguishedName) {

		if (StringUtils.contains(distinguishedName, "@")) {
			return StringUtils.substringBefore(distinguishedName, "@");
		}
		return distinguishedName;

	}

	public static String name(String distinguishedName) {

		if (StringUtils.contains(distinguishedName, "@")) {
			return StringUtils.substringBefore(distinguishedName, "@");
		}
		return distinguishedName;

	}

	public static boolean isIdentityDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (identity_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isPersonDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (person_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isPersonAttributeDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (personAttribute_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unit_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitAttributeDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unitAttribute_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitDutyDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unitDuty_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isGroupDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (group_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isRoleDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (role_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static DistinguishedNameCategory distinguishedNameCategory(List<String> list) {
		DistinguishedNameCategory category = new DistinguishedNameCategory();
		if (ListTools.isNotEmpty(list)) {
			list.stream().forEach(o -> {
				if (isIdentityDistinguishedName(o)) {
					category.getIdentityList().add(o);
				} else if (isPersonDistinguishedName(o)) {
					category.getPersonList().add(o);
				} else if (isPersonAttributeDistinguishedName(o)) {
					category.getPersonAttributeList().add(o);
				} else if (isUnitDistinguishedName(o)) {
					category.getUnitList().add(o);
				} else if (isUnitAttributeDistinguishedName(o)) {
					category.getUnitAttributeList().add(o);
				} else if (isUnitDutyDistinguishedName(o)) {
					category.getUnitDutyList().add(o);
				} else if (isGroupDistinguishedName(o)) {
					category.getGroupList().add(o);
				} else if (isRoleDistinguishedName(o)) {
					category.getRoleList().add(o);
				} else {
					category.getUnknownList().add(o);
				}
			});
		}
		return category;

	}

	public static class DistinguishedNameCategory {
		private List<String> personList = new ArrayList<>();
		private List<String> personAttributeList = new ArrayList<>();
		private List<String> unitList = new ArrayList<>();
		private List<String> unitAttributeList = new ArrayList<>();
		private List<String> unitDutyList = new ArrayList<>();
		private List<String> identityList = new ArrayList<>();
		private List<String> groupList = new ArrayList<>();
		private List<String> roleList = new ArrayList<>();
		private List<String> unknownList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public List<String> getPersonAttributeList() {
			return personAttributeList;
		}

		public void setPersonAttributeList(List<String> personAttributeList) {
			this.personAttributeList = personAttributeList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public List<String> getUnitAttributeList() {
			return unitAttributeList;
		}

		public void setUnitAttributeList(List<String> unitAttributeList) {
			this.unitAttributeList = unitAttributeList;
		}

		public List<String> getUnitDutyList() {
			return unitDutyList;
		}

		public void setUnitDutyList(List<String> unitDutyList) {
			this.unitDutyList = unitDutyList;
		}

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
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

		public List<String> getUnknownList() {
			return unknownList;
		}

		public void setUnknownList(List<String> unknownList) {
			this.unknownList = unknownList;
		}
	}

}
