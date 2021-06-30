MWF.xApplication.Org.LP = {
	"title": "Organization",
	"manage": "Management",
	"identity" : "Identity",
	"person" : "Person",
    "role" : "Role",
    "group" : "Group",
	"org": "Unit",

    "unitList": "Unit",
    "groupList": "Group",
    "roleList": "Role",
    "personList": "Person",
    "personImport": "Person Import",
    "privateNamesQueryPower":"privateNamesQueryPower Config",

	"application" : "Application",
	"CMSApplication" : "Application",
	"CMSCategory" : "Category",
	"CMSFormField" : "Field",
	"formField" : "Field",
	"process" : "Process",
    "yes": "Yes",
    "no": "No",
    "sortByPinYin" : "Sort by pinyin",
    "sortByPinYinConfirmContent" : "This operation will sort the identities in the organization according to pinyin, confirm the operation?",

	"search": "Search",
	"searchText": "Input search keyword",
	"edit": "Edit",
	"save": "Save",
	"cancel": "Cancel",
	"add": "Add",
	"delete": "Delete",
	"phone": "Phone",
	"mail": "Mail",
	"department": "Department",
	"company": "Company",
	"duty": "Duty",
    "manager": "Manager",
    "ok": "OK",
    "back": "Back",
    "name": "Name",
    "description": "Description",
    "ipAddress" : "IP Address",
    "ipAddressPlaceHolder" : "Only the matching ip address can login to the account. Separate multiple values with \",\"",
    "ipAddressIncorrectNotice" : "IP address format is incorrect:",

    "createSubCompany": "Create sub company",
    "createSubDepartment": "Create sub department",
    "configCompany": "Setup company manager",
    "configCompanyOk": "The company manager is successfully set, and the permissions will take effect after the next login",
    "configCompanyNull": "<font style='color: red'>Company managers will be cleared. After clearing, only super managers and upper-level company managers have the authority to edit this company.</font><br/><br/>Are you sure you want to perform this operation?",
    "configCompanyNullTitle": "Clear company manager confirmation",

    "deleteOrganization": "Delete selected Unit",
    "deleteOrganizationTitle": "Delete Unit confirmation",
    "deleteOrganizationConfirm": "Are you sure you want to delete the selected Unit?",
    "deleteOrganizationAllConfirm": "There are multiple titles, attributes and members in the Unit you selected. Deleting this Unit will delete them at the same time. Are you sure you want to continue the current operation?<br/><br/>" +
    "<div style=\"color:red\">(Note: This operation may cause errors in other applications that depend on these Unit!)</div>",
    "deleteOrganizationSubConfirm": "You choose to delete one or more Unit. Deleting these Unit will perform the following actions:<br/>" +
    "1. Delete all positions and attributes of the selected Unit and subordinate Unit;<br/>" +
    "2. Delete all memberships of the selected Unit and subordinate Unit;<br/>" +
    "3. Delete the selected Unit and all subordinate Unit;<br/>" +
    "Are you sure you want to delete the current Unit?<br/><br/>" +
    "<div style=\"color:red\">(Note: This operation may cause errors in other applications that depend on these Unit!)</div>",


    "organizationSave": "Please save the Unit first",
    "inputOrganizationInfor": "Please enter an Unit name",

    "unitBaseText": "Base",
    "unitPersonMembers": "Members",
    "unitDutys": "Dutys",
    "unitAttribute": "Attributes",
    "unitName": "Name",
	"unitUnique": "Unique",
    "unitTypeList": "Type",
    "unitShortName": "Shortname",
    "unitLevel": "Level",
    "unitLevelName": "Levelname",
    "unitControllerList": "Manager",
    "unitSuperUnit": "Super Unit",
    "unitDescription": "Description",
    "editUnit": "Edit Unit information",
    "saveUnit": "Save Unit information",
    "inputUnitInfor": "Enter Unit name",
    "orderNumber": "Order number",


    "unitReadDn": "Full name: {dn}",
    "unitReadCreate": "Unit creation time: {date}，Last Modified：{date2}",
	"unitReadLevel": "Unit level {level}, Level name: {levelName}",

	"dutyName": "Duty name",
	"dutyMembers": "Members",

	
	"deleteDutyTitle": "Delete Duty confirmation",
    "deleteDuty": "Are you sure you want to delete the selected Duty?",

	"deleteDutyIdentityTitle": "Delete Duty identity confirmation",
	"deleteDutyIdentity": "Are you sure you want to delete \"{identity}\" from \"{duty}\"?",

	"deleteAttributeTitle": "Delete Attribute confirmation",
	"deleteAttribute": "Are you sure you want to delete the selected Attribute?",
	"deleteIdentityInDepartmentTitle": "Removal of personnel identity confirmation",
	"deleteIdentityInDepartment": "Are you sure you want to delete \"{identity}\" from the department \"{depart}\"?\n Deleting an identity will delete all positions of this identity at the same time, are you sure to delete it?",
	
	"deleteGroupsTitle": "Delete Group confirmation",
	"deleteGroupsConfirm": "Are you sure you want to delete the selected Group?",
    "deleteGroupError_InRole": "Error deleting Group, Group has role",
    "deleteGroupError_InGroup": "Error deleting Group, Group is a member of another Group",


    "deleteRolesTitle": "Delete Role confirmation",
    "deleteRolesConfirm": "Are you sure you want to delete the selected Role?",

    "deletePersonsTitle": "Delete Person confirmation",
    "deletePersonsConfirm": "Are you sure you want to delete the selected Person?",
	
	"deleteGroups": "Delete selected Group",
    "deleteRoles": "Delete selected Role",
    "deletePersons": "Delete selected Person",
    "deleteUnits": "Delete selected Unit",
    "deleteElementsCancel": "Cancel",

    "deletePersonMemeberTitle": "Delete Person member confirmation",
    "deletePersonMemeber": "Are you sure you want to delete the selected Person member?",
    "deleteGroupMemeberTitle": "Delete Group member confirmation",
    "deleteUnitMemeberTitle": "Delete Unit member confirmation",
    "deleteGroupMemeber": "Are you sure you want to delete the selected Group member?",
    "deleteUnitMemeber": "Are you sure you want to delete the selected Unit member?",
    "deleteIdentityMemeberTitle": "Delete Identity member confirmation",
    "deleteIdentityMemeber": "Are you sure you want to delete the selected Identity member?",


	"groupLoaded": "Group loading complete",
	"groupSave": "Please save the Group first",
	"inputGroupName": "Enter Group name",
    "editGroup": "Edit Group",
	"saveGroup": "Save Group",
	
	"groupBaseText": "Base",
	"groupMemberPersonText": "Person",
    "groupMemberIdentityText": "Identities",
	"groupMemberGroupText": "Groups",
    "unitMemberGroupText": "Units",
	"groupName": "Name",
    "groupDn": "Fullname",
    "groupUnique": "Unique",


    "inputGroupInfor": "Enter Group name",

    "groupReadDn": "The full name of the group：{dn}",
    "groupReadCreate": "Group creation time: {date}, Last Modified：{date2}",
	
	// "personEmployee": "工号",
	// "personDisplay": "名称",
	// "personMail": "邮件",
	// "personMail": "邮件",
	"personPhone": "Phone",
	"groupDescription": "Description",

    "deletePersonMemberTitle": "Delete Person member confirmation",
	"deleteGroupMemberTitle": "Delete Group member confirmation",
	"deletePersonMember": "Are you sure you want to delete the selected Person member?",
	"deleteGroupMember": "Are you sure you want to delete the selected Group members?",
	
	"roleBaseText": "Base",
	"roleName": "Name",
    "roleUnique": "Unique",
    "roleDescription": "Description",
	"roleLoaded": "Role loading complete",
    "roleSave": "Please save the Role first",
    "roleReadDn": "Full name of the Role: {dn}",
    "roleReadCreate": "Role creation time: {date}, Last Modified: {date2}",
    "rolePersonMembers": "Person",
    "roleGroupMembers": "Groups",
    "editRole": "Edit Role",
    "saveRole": "Save Role",
    "inputRoleInfor": "Enter the Role name",


    "roleMemberPersonText": "Person",
    "roleMemberGroupText": "Groups",

    "inputRoleName": "Enter the Role name",
	
	"personLoaded": "Person loading complete",
	
	"personBaseText": "Base",

    "personSave": "Please save Person first",
    "personImage": "Avatar",
    "uploadImage": "Upload avatar",
	"personName": "Name",
	"personEmployee": "Employee",
    "personGender": "Gender",
	"personDisplay": "Display name",
	"personMobile": "Cellphone",
	"personMail": "e-mail",
	"personQQ": "QQ number",
	"personWeixin": "Wechat number",
	"personWeibo": "Weibo",
    "personUnique": "Unique",
	"personOfficePhone": "Office Phone",
	"personBoardDate": "Entry Time",
	"personBirthday": "Birthday",
	"personSuperior": "Superior",
	"editPerson": "Edit Person",
    "resetPassword": "Reset Password",
    "savePerson": "Save Person",
    "personReadDn": "Full name of the Person: {dn}",
    "personReadLogin": "Last login time: {date}, IP Address: {ip}, Client: {client}",
    "personReadCreate": "Person creation time: {date}, Last Modified：{date2}",
    "personReadPassword": "Password expiration time: {date}, Password last modified time: {date2}",
    "roleFullName": "Role Fullname",

    "unlockPerson" : "Unlock Person",
    "unlockPersonTitle": "Unlock Person confirmation",
    "unlockPersonText": "\"{name}\" has entered the wrong password more than the specified number of times, is it allowed to log in again?",
    "unlockPersonSuccess" : "User \"{name}\" has been allowed to login!",

    "attributeName": "Attribute",
	"attributeValue": "Value",

	//"inputPersonInfor": "请输入完整人员信息（人员名称、人员工号、手机号码、性别必填）",
    "inputPersonInfor": "Please enter complete Person information (Person name, cellphone number, gender are required)",
	"personAttributeText": "Attribute",
	"personIdentityText": "Identity",
    "controllerListText": "Manager",
    "personRoleText" : "Role",

    "IdentityName": "Identity name",
    "IdentityInUnit": "Unit",
    "IdentityDn": "Fullname",
    "IdentityDuty": "Duty",
    "modifyIdentity": "Modify Identity",
    "IdentityMain": "Master Identity",
    "setIdentityMain": "Set Master Identity",

    "noSignature": "Edit personal signature",

    "man": "Man",
    "female": "Female",
    "other": "Other",

	"changePersonIcon": "Change avatar",
    "resetPasswordTitle": "Reset password confirmation",
    "resetPasswordText": "Are you sure you want to reset the password for Person \"{name}\"?",
    "resetPasswordSuccess": "Password for Person \"{name}\" has been reset",

    "importPersonClean": "Clear All Organization Data",
    "importPersonTitle": "Unit import",
    "importPersonInfor": "You can import organizations in batches via Excel, <a target='_blank' href='{url}' > Click here </a>Get Unit import template",
    "importPersonAction": "Upload Excel file",
    "importPersonResult": "The Unit has been imported, <a target='_blank' href='{url}' > Click here </a> view import results",
    "exportPersonText": "Export Organizations",

    "queryPrivateConfigTitle":"Personal address book permission configuration",
    "queryPrivateConfigExcludUnit":"Query not allowed",
    "queryPrivateConfigExcludPerson":"Query not allowed",
    "queryPrivateConfigLimitOuter":"Restrict viewing of external Unit",
    "queryPrivateConfigLimitAll":"Restrict view to everyone",
    "queryPrivateConfigDescribe":"Description",
    "queryPrivateConfigBtnEdit":"Edit",
    "queryPrivateConfigBtnSave":"Save",
    "queryPrivateConfigBtnCancel":"Cancel"
};
