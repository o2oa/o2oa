MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.workcenter = MWF.xApplication.process.workcenter || {};
MWF.xApplication.process.workcenter.LP={
	"title": "Work Center",
	"task": "Task",
	"read": "Read",
	"taskCompleted": "TaskCompleted",
	"readCompleted": "ReadCompleted",
	"draft": "Draft",
	"createProcess": "Create Process",
	"all": "All",
	"byType": "Category",
	"expire1": "Task deadline: {time}",
	"expire2": "The task is about to time out. Deadline:{time}",
	"expire3": "The task has timed out. Deadline:{time}",
	"firstPage": "First Page",
	"lastPage": "Last Page",
	"process": "Flow",
	"processStarted": "Process Started",
	"taskProcessed": "The file has been submitted",
	"taskProcessedMessage": "You processed a to-do task:",
	"nextActivity": "Next activity:",
	"nextUser": "Processor:",
	"processStartedMessage": "You started a new job:",
	"deal": "Deal",
	"processing": "Processing",
	"workCompleted": "File transfer completed",
	"completed": "Transfer completed",
	"workProcess": "Continue to flow",
	"next_etc": "Wait for {count} people",
	"processTaskCompleted": "Todo has been processed",
	"arrivedActivity": "The work has arrived at the activity:",
	"rapidEditor": "Quick To Do",
	"setReadCompleted": "Set as read",
	"setReaded": "Set as read",
	"setReadedConfirmContent": "Are you sure you want to mark “{title}” as read?",
	"setReadedConfirmTitle": "Mark read confirmation",
	"readOpinion": "Read Opinion",
	"processInfo": "Flow Details",
	"opinion": "opinion",
	"time": "time",
	"starttime": "Arrival Time",
	"workFlowTo": "File flow to:",
	"taskPerson": "Processor:",
	"open": "Open",
	"select": "Select",
	"processActivity": "Done",
	"processActivityInfo": "Activity status when processing this file",
	"readActivity": "Read",
	"readActivityInfo": "Activity status when receiving pending reading",
	"filter": "Filter",
	"search": "Search",
	"filterPlaceholder": "Please enter a keyword",
	"commonUseProcess": "Common Process",
	"searchProcessResault": "Search results for “{key}”",
	"filterStartPlaceholder": "Search for a startable process",
	"filterCategoryList": [
		{
			"key": "applicationList",
			"name": "Application"
		},
		{
			"key": "processList",
			"name": "Process"
		},
		{
			"key": "activityNameList",
			"name": "Activity"
		},
		{
			"key": "creatorUnitList",
			"name": "Creator Unit"
		},
		{
			"key": "startTimeMonthList",
			"name": "Received Month"
		},
		{
			"key": "completedTimeMonthList",
			"name": "Processed Month"
		},
		{
			"key": "completedList",
			"name": "Is Complete"
		},
		{
			"key": "key",
			"name": "Key"
		}
	],
	"filterCategoryShortList": [
		{
			"key": "applicationList",
			"name": "Application"
		},
		{
			"key": "processList",
			"name": "Process"
		},
		{
			"key": "activityNameList",
			"name": "Activity"
		},
		{
			"key": "creatorUnitList",
			"name": "Unit"
		},
		{
			"key": "startTimeMonthList",
			"name": "Month"
		},
		{
			"key": "completedTimeMonthList",
			"name": "Month"
		},
		{
			"key": "completedList",
			"name": "Complete"
		},
		{
			"key": "key",
			"name": "Key"
		}
	],
	"noTask": "No to-dos to process",
	"noTaskCompleted": "No files processed",
	"noRead": "No files to read",
	"noReadCompleted": "No files have been read",
	"noDraft": "No draft files",
	"createWork": "New Process File",
	"batch": "Batch processing",
	"readConfirm" : "Are you sure you want to mark the selected files as read?",

	"selectBatch": "Select multiple to-dos of the same link for batch processing",
	"cannotSelectBatch": "To-dos in different links cannot be batch processed",
	"unnamed": "unnamed",
	"review": "Refer to",
	"myCreated": "My drafting",
	"noReview": "No reference",
	"noMyCreated": "No files I created",
	"filterCategoryShortListReview": [
		{
			"key": "applicationList",
			"name": "application"
		},
		{
			"key": "processList",
			"name": "flow"
		},
		{
			"key": "activityNameList",
			"name": "activity"
		},
		{
			"key": "creatorUnitList",
			"name": "department"
		},
		{
			"key": "startTimeMonthList",
			"name": "Create month"
		},
		{
			"key": "completedList",
			"name": "complete"
		},
		{
			"key": "key",
			"name": "keyword"
		}
	],
	"filterCategoryListReview": [
		{
			"key": "applicationList",
			"name": "apply name"
		},
		{
			"key": "processList",
			"name": "Process Name"
		},
		{
			"key": "activityNameList",
			"name": "Handling activities"
		},
		{
			"key": "creatorUnitList",
			"name": "Create department"
		},
		{
			"key": "startTimeMonthList",
			"name": "Create month"
		},
		{
			"key": "completedList",
			"name": "Is the circulation completed"
		},
		{
			"key": "key",
			"name": "keyword"
		}
	]
}
MWF.xApplication.process.workcenter["lp."+o2.language] = MWF.xApplication.process.workcenter.LP
