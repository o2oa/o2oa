MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.AgentDesigner = MWF.xApplication.service.AgentDesigner || {};
MWF.xApplication.service.AgentDesigner.LP={
	"title": "AgentEditor",
	"newAgent": "Create Agent",
	"agentLibrary": "Agent Library",
	"property": "Property",
	"include": "Include",
	"id": "ID",
	"name": "Name",
	"alias": "Alias",
	"description": "Description",
	"validated": "Is the code format correct",
	"isEnable": "Whether to enable",
	"cron": "Timed task cron expression",
	"lastStartTime": "Last Start Time",
	"lastEndTime": "Last EndTime",
	"appointmentTime": "Estimated next execution time",
	"true": "Yes",
	"false": "No",
	"enable": "Click to enable",
	"disable": "Click to disable",
	"openLogViewer": "Open LogViewer",
	"debugger": "Debug",
	"run": "Run",
	"runSuccess": "Run successfully",
	"notice": {
		"save_success": "Agent saved successfully!",
		"deleteDataTitle": "Delete Data Confirmation",
		"deleteData": "Are you sure to delete the current data and its sub-data?",
		"changeTypeTitle": "Change Data Type Confirmation",
		"changeTypeDeleteChildren": "Changing the data type will delete all child data. Are you sure you want to execute it?",
		"changeType": "Changing the data type will change the value of the data. Are you sure you want to execute it?",
		"inputTypeError": "The data type you entered is wrong, please re-enter",
		"sameKey": "The item name you entered already exists in the object, please re-enter",
		"emptyKey": "Project name cannot be empty, please re-enter",
		"numberKey": "The item name cannot be a number, please re-enter",
		"inputName": "Please enter the agent name",
		"inputCron": "Timed task corn expression cannot be empty"
	},
	"comment": {
		"entityManager": "Entity Manager",
		"applications": "Access to services in the system",
		"organization": "Organization Visit",
		"org": "Organize quick access method",
		"service": "webSerivces client"
	},
	"formToolbar": {
		"save": "Save agent",
		"autoSave": "AutoSave",
		"fontSize": "Font size",
		"style": "style",
		"scriptEditor": "Script Editor",
		"viewAllVersions": "View all script versions"
	}
}
MWF.xApplication.service.AgentDesigner["lp."+o2.language] = MWF.xApplication.service.AgentDesigner.LP