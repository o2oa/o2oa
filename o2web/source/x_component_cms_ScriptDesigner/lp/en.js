MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.ScriptDesigner = MWF.xApplication.cms.ScriptDesigner || {};
MWF.xApplication.cms.ScriptDesigner.LP={
	"title": "Script Editor",
	"newScript": "New Script",
	"scriptLibrary": "Script Library",
	"property": "Property",
	"include": "include",
	"id": "Identification",
	"name": "Name",
	"alias": "Alias",
	"description": "Description",
	"notice": {
		"save_success": "The script was saved successfully!",
		"deleteDataTitle": "Delete Data Confirmation",
		"deleteData": "Are you sure to delete the current data and its sub-data?",
		"changeTypeTitle": "Change Data Type Confirmation",
		"changeTypeDeleteChildren": "Changing the data type will delete all child data. Are you sure you want to do it?",
		"changeType": "Changing the data type will change the value of the data. Are you sure you want to do it?",
		"inputTypeError": "The data type you entered is wrong, please re-enter",
		"sameKey": "The item name you entered already exists in the object, please re-enter",
		"emptyKey": "Project name cannot be empty, please re-enter",
		"numberKey": "The item name cannot be a number, please re-enter",
		"inputName": "Please enter the script name"
	},
	"version": {
		"title": "View form version history",
		"close": "Close",
		"no": "SerialNumber",
		"updateTime": "UpdateTime",
		"op": "action",
		"resume": "Resume",
		"resumeConfirm": "Resume Confirmation",
		"resumeInfo": "Are you sure you need to perform a form recovery operation? After confirming the restoration, the current form will be updated, and the current form needs to be manually saved to take effect.",
		"resumeSuccess": "Resume Successfully"
	},
	"formToolbar": {
		"save": "Save script",
		"autoSave": "AutoSave",
		"fontSize": "Font size",
		"style": "style",
		"scriptEditor": "Script Editor",
		"viewAllVersions": "View all script versions"
	}
}
MWF.xApplication.cms.ScriptDesigner["lp."+o2.language] = MWF.xApplication.cms.ScriptDesigner.LP