o2.LP = window.LP || {
	"name": "Name",
	"description": "Description",
	"searchKey": "Search",
	"desktop_style": "dDesktop",
	"flat_style": "Flat",
	"cmsName" : "CMS",
	"processName" : "Process",
	"portalName" : "Portal"
};

o2.LP.process = {
	"unnamed": "Unnamed",
	"unknow": "Unknow",
	"processConfig": "Process Config",
	"formConfig": "Form Config",

	"createCategory": "Create Category",
	"searchCategory": "Search Category",
	"noCategoryNotice": "There is no process category, you can click here to create a process category.",
	"noProcessNoticeNode": "There is no process, you can click here to create a process",

	"activity": "Activity",
	"route": "Route",
	"property": "Property",
	"showJson": "View JSON",
	"unrealized": "Unrealized",
	"tools": "Tools",
	"repetitions": "Duplicate property name",
	"repetitionsValue": "Duplicate content item",
	"repetitionsEvent": "Duplicate event name",
	"repetitionsId": "Duplicate element identifiers",
	"repetitionsOrUnvalid": "Duplicate or invalid property name",
	"notNullId": "Element identifier cannot be null",
	"editCategory": "Edit Category",
	"createProcess": "Create Process",
	"deleteCategory": "Delete Category",
	"deleteProcess": "Delete Process",
	"editProcess": "Edit Process",
	"createForm": "Create Form",
	"deleteForm": "Delete Form",
	"editForm": "Edit Form",

	"menu": {
		"newRoute": "Create Route",
		"newActivity": "Create Activity",
		"newActivityType": {
			"manual": "Manual Activity",
			"condition": "Condition Activity",
			"auto": "Auto Activity",
			"split": "Split Activity",
			"merge": "Marge Activity",
			"embed": "Embed Activity",
			"invoke": "Invoke Activity",
			"begin": "Begin Activity",
			"end": "End Activity"
		},

		"copyActivity": "Copy Activity",

		"deleteActivity": "Delete Activity",
		"deleteRoute": "Delete Route",

		"saveProcess": "Save Process",
		"saveProcessNew": "Save as new process",
		"checkProcess": "Check Process",
		"exportProcess": "Export Process",
		"printProcess": "Print Process",

		"showGrid": "Show Grid",
		"hideGrid": "Hide Grid"
	},
	"notice": {
		"save_success": "Process saved successfully!",
		"deleteForm_success": "Form deleted!",
		"deleteProcess_success": "Process deleted!",
		"one_begin": "There can only be one start activity per process!",
		"deleteRoute": "Are you sure you want to delete the selected route?",
		"deleteRouteTitle": "Delete route confirmation",
		"deleteActivityTitle": "Delete activity confirmation",
		"deleteActivity": "Deleting the activity will also delete all routes associated with this activity, Are you sure you want to delete the selected activity？",
		"deleteDecisionTitle": "Delete decision confirmation",
		"deleteDecision": "Are you sure you want to delete the selected decision?",
		"deleteScriptTitle": "Delete script confirmation",
		"deleteScript": "Are you sure you want to delete the selected script?",
		"deleteElementTitle": "Delete form element confirmation",
		"deleteElement": "Are you sure to delete the current element and its child elements?",
		"deleteEventTitle": "Delete event confirmation",
		"deleteEvent": "Are you sure you want to delete the selected event?",

		"deleteActionTitle": "Delete operation confirmation",
		"deleteAction": "Are you sure you want to delete the selected operation?",

		"deleteRowTitle": "Delete table row confirmation",
		"deleteRow": "Deletes the current row is the row is deleted all the content in the cells, Are you sure to delete the currently selected row?",
		"deleteColTitle": "Delete Table Columns confirmation",
		"deleteCol": "Deletes the current column is the column is deleted all the content in the cells, Are you sure to delete the currently selected column?",

		"deleteProcessTitle": "Delete process confirmation",
		"deleteProcess": "Are you sure you want to delete the selected process?",

		"deleteFormTitle": "Delete form confirmation",
		"deleteForm": "You sure you want to delete the selected form?",

		"deleteTreeNodeTitle": "Delete node confirmation",
		"deleteTreeNode": "Are You sure you want to delete the selected node?",

		"inputScriptName": "Please enter the name of the script!",
		"inputCategoryName": "Please enter the category name of the script!"
	},
	"button": {
		"ok": "Ok",
		"cancel": "Cancel"
	},
	"formAction": {
		"insertRow": "Insert Row",
		"insertCol": "Insert Column",
		"deleteRow": "Delete Row",
		"deleteCol": "Delete Column",
		"mergerCell": "Merge Cells",
		"splitCell": "Split Cells",
		"move": "Move",
		"copy": "Copy",
		"delete": "Delete",
		"add": "Add",
		"script": "Script"
	}
};
o2.LP.desktop = {
	"homepage": "Homepage",
	"taskCenter": "TaskCenter",
	"info": "Information",
	"calendar": "Calendar",
	"profile": "Profile",
	"loadding": "System is loadding, please wait......",
	"lowBrowser": " Your browser version is too low! ~ IE8 and the following versions are not supported!",
	"upgradeBrowser": "Please upgrade your browser：",

	"menuAction": "Menu",
	"configAction": "Configure your workstations",
	"userMenu": "User Options",
	"userChat": "Online Communication",
	"styleAction": "Switch Themes",
	"showDesktop": "Show Desktop",
	"showMessage": "Message",
	"logout": "Logout",
	"userConfig": "Settings",
	"application": "Component",
	"widget": "Widget",
	"process": "Process",
	"nosign": "Edit You Signature",
	"searchUser": "Search：Username",
	"say": "Say",
	"clearMessage": "Clear Message",

	"refresh": "Refresh",
	"close": "Close",
	"closeAll": "Close All",
	"closeOther": "Close Others",

	"lnkAppTitle": "Often Used",
	"deleteLnk": "Delete Link",
	"addLnk": "Add Link",

	"changeViewTitle": "Toggle style confirmation",
	"changeView": "Are you sure you want to switch the style？<br><br>If you select \"OK\", the page will refresh directly, and unsaved data may be lost.",
	"refreshMenu": "Restore the default menu order",
	"defaultMenuTitle": "Confirmation",
	"defaultMenuInfor": "Are you sure you want to restore the ordering and grouping of the menu to the default state？",

	"deleteLink" : "Delete Link",

	"messsage": {
		"appliction": "Application",
		"application": "App",
		"process": "Process",
		"infor": "Info",
		"query": "Data",
		"taskMessage": "Task Reminders",
		"receiveTask": "You get a Task, The title is: ",
		"activity": "Activity",

		"readMessage": "Read Reminders",
		"receiveRead": "You get a Read, The title is: ",

		"reviewMessage": "Review Reminders",
		"receiveReview": "You get a Review, The title is: ",

		"fileEditorMessage": "Received File",
		"receiveFileEditor": "A file sent to you: ",

		"fileShareMessage": "Shared File",
		"receiveFileShare": "shared to you a file: ",

		"meetingInviteMessage": "Meeting Invitation",
		"meetingInvite": "<font style='color: #ea621f'>{person}</font> invite you to attend the meeting in <font style='color: #ea621f'>{date}</font>: Topics is: \"{subject}\"，location: <font style='color: #ea621f'>{addr}</font>",
		"meetingCancelMessage": "Meeting.Canceled",
		"meetingCancel": "<font style='color: #ea621f'>{person}</font> cancelled the meeting on <font style='color: #ea621f'>{date}</font> in <font style='color: #ea621f'>{addr}</font>: \"{subject}\"",
		"meetingAcceptMessage": "Meeting invitation has been accepted",
		"meetingAccept": "<font style='color: #ea621f'>{person}</font> has accepted your meeting invitation, will attend the meeting in <font style='color: #ea621f'>{addr}</font> on <font style='color: #ea621f'>{date}</font>: \"{subject}\"",
		"meetingRejectMessage": "Meeting invitation has been rejected",
		"meetingReject": "<font style='color: #ea621f'>{person}</font> has declined your meeting invitation。Time: <font style='color: #ea621f'>{date}</font>; Subject: \"{subject}\"",

		"attendanceAppealInviteMessage": "There is an attendance claim that needs your approval",
		"attendanceAppealInvite": "{subject}",
		"attendanceAppealAcceptMessage": "Attendance application passed",
		"attendanceAppealAccept": "{subject}",
		"attendanceAppealRejectMessage": "Attendance appeal failed",
		"attendanceAppealReject": "{subject}",

		"canlendarAlarmMessage" : "Schedule",
		"canlendarAlarm" : "{title}",

		"teamwork":{
			"executor":"Assignee",
			"creatorPerson":"Creator"
		},

		"publishDocument" : "Publish document: ",

		"customMessageTitle": "Message: ",
		"customMessage": "You received a message: ",
		"emoji": "Emoji"
	},
	"styleMenu": {
		"default": "default",
		"color": "color",
		"black": "black",
		"lotus": "lotus",
		"crane": "crane",
		"peony": "peony",
		"car": "car",
		"dock": "dock",
		"panda": "panda",
		"star": "star"
	},
	"styleFlatMenu": {
		"blue": "blue",
		"red": "red",
		"orange": "orange",
		"green": "green",
		"cyan": "cyan",
		"purple": "purple",
		"gray": "gray",
		"darkgreen": "darkgreen",
		"tan": "tan",
		"navy": "navy"
	},
	"notice": {
		"unload": "If you close or refresh the current page, the unsaved content will be lost, please confirm your operation",
		"changePassword": "Your password has expired, please change it in time",
		"errorConnectCenter1": "Unable to connect to the Center Server, please make sure one of the following addresses is accessible: ",
		"errorConnectCenter2": "If none of the above addresses can be accessed, please check your network or contact the administrator!"
	},
	"login": {
		"title": "User Login",
		"loginButton": "Login",

		"mobileDownload": "Phone scanning the QR code to install",

		"inputUsernamePassword": "Enter you user name and password...",
		"loginWait": "Login, please wait...",
		"loginError": "User name or password entered is incorrect, please re-enter...",

		"camera_logining": "Logging in, please face the camera ...",
		"camera_logining_1": "Please keep smile ...",
		"camera_logining_2": "Please Look Up ...",
		"camera_logining_3": "Verification Successful ...",

		"camera_logining2": "Please move a different perspective, or transform expression ...",
		"camera_loginSuccess": "{name} Hello, is to sign you in ...",
		"camera_loginError": "Unable to verify your identity, please log in through other means ...",
		"camera_loginError2": "Login failed, please log in through other means ...",
		"camera_loginError_camera": "Unable to open the camera, probably already in use ..."
	},

	"action": {
		"uploadTitle": "Uploading",
		"uploadComplete": "Upload Complete",
		"sendReady": "Encoding the data, prepared for transmission ...",
		"sendStart": "Begin Transfer",
		"sendError": "File Transfer Error",
		"sendAbort": "File transfer was canceled",
		"speed": "Speed",
		"time": "Elapsed Time",
		"hour": "Hour",
		"minute": "Minute",
		"second": "Second",

		"cancelUploadTitle": "Cancel upload confirmation",
		"cancelUpload": "Are you sure you want to cancel uploading file \"{name}\" ?"
	},
	"person": {
		"personEmployee": "Empno",
		"personMobile": "Mobile Number",
		"personMail": "Mail",
		"personDuty": "Duty",
		"personQQ": "QQ",
		"personWeixin": "Weixin",
		"duty": "Duty"
	},
	"collect": {
		"collectNotConnected": "Failed to connect O2 cloud",
		"collectNotConnectedText": "Failed to connect O2 cloud, Please check the server network!"
	}
};
o2.LP.desktop.message = o2.LP.desktop.messsage;
o2.LP.widget = {
	"upload": "Upload",
	"uploadTitle": "Upload File",
	"uploadInfor": "Please select a file to upload",
	"delete": "Delete",
	"replace": "Replace",
	"select": "Select",

	"download": "Download",
	"share": "Share",
	"send": "Ssend",
	"downloadAll": "Download All",
	"createFolder": "Create Folder",
	"rename": "Rename",
	"property": "Property",
	"refuseUpload": "File upload is prohibited",
	"refuseUploadHTML": "<div>The type of attachment named <font style='color:#0000ff'>\"{filename}\"</font> is not allowed to be uploaded and has been excluded</div>",
	"refuseUploadNotice" : "The type of attachment named \"{filename}\" is not allowed to be uploaded",
	"refuseUploadHTML_size" : "<div>The size of the attachment named <font style='color:#0000ff'>\"{filename}\"</font> is too large and has been excluded </font> (Only allow uploading of attachments smaller than {size}M) </div>",
	"refuseUploadNotice_size": "The size of the attachment named \"{filename}\" is too large (Only allow uploading of attachments smaller than {size}M) ",


	"list": "List",
	"sequence": "Sequence",
	"icon": "Icon",
	"preview": "Preview",


	"min": "Simple Mode",
	"max": "Full Mode",

	"size": "Size",
	"uploader": "Uploader",
	"uploadTime": "Time",
	"modifyTime": "Modify",
	"uploadActivity": "Activity",
	"attCount": "File",
	"folderCount": "Folder",

	"pictureSize": "Width {width}px, Height {height}px",
	"pictureRatio": "Image aspect ratio{ratio}",

	"ok": "Ok",
	"cancel": "Cancel",
	"refresh": "Refresh",
	"close": "close",
	"open": "open",
	"choiceWork": "choice document open",
	"workcompleted": "Process completed",

	"months": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
	"days_abbr": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
	"clear": "clear",
	"year": ", ",
	"month": " ",
	"date": " ",
	"week": "week",
	"dateGreaterThanCurrentNotice" : "The selected date must be greater than the current date",

	"unknow": "unknow",
	"uploadImg": "uploadImg",
	"clearImg": "Clear Image",
	"clearImg_confirmTitle": "Clear Image confirmation",
	"clearImg_confirm": "Are you sure you want to clear the image？",
	"office": "Open in document Office Control",
	"closeOffice": "Close Attachment",
	"configAttachment": "Set attachment permissions",
	"configAttachmentText": "Config",
	"checkOcrText": "Correct the text in the automatically recognized picture",
	"order": "Attachment Sorting",

	"record": "Record",
	"stop": "Stop",
	"play": "Play",
	"save": "Save",
	"userRefuse" : "The user refuses to provide information.",
	"explorerNotSupportDevice" : "The browser does not support hardware devices.",
	"canNotFindDevice": "Unable to discover the specified hardware device.",
	"canNotOpenMicrophone": "Unable to turn on the microphone. Exception information: ",
	"explorerNotSupportRecordVoice" : "The browser does not support the recording function.",

	"clickToEditCss" : "Click here to write CSS",
	"uploadSuccess": "Uploaded successfully!",
	"uploadFail": "Upload failed, please upload again!",
	"selectLocalImage" : "Choose local picture",
	"selectCloudImage": "Choose server picture",
	"reset": "Reset",
	"uploadOriginalImage": "Upload Original Image",

	"startRecord" : "Start Recording",
	"completeRecord": "Stop Recording",
	"requireHttps" : "Video recording function must use https protocol",
	"canNotToRecordVideo" : "The video recording function cannot be used. Your browser does not support the following features:",

	"pageJumperTitle": "Enter the page number",

	"scriptAreaEditNotice" : "Click here to write script code",
	"empty" : "Clear",
	"undo" : "Undo",
	"redo" : "Redo",
	"thickness" : "Thickness",
	"color" : "Color",
	"insertImage" : "Insert Image",
	"imageClipper" : "Crop Image",

	"explorerNotSupportFeatures" : "Your browser does not support the following features:"
};

o2.LP.widget.SimpleEditor = {
	"insertEmotion": "Insert Expressions",
	"insertImage": "Insert Image",
	"Emotions": "regular_smile|smile，teeth_smile|laugh,angry_smile|angry,confused_smile|confuse,cry_smile|cry,embaressed_smile|awkward,omg_smile|Surprised,sad_smile|sad,shades_smile|cool,tounge_smile|Tongue out,wink_smile|wink,angel_smile|angel,devil_smile|devil,heart|heart,broken_heart|Broken heart,thumbs_up|up,thumbs_down|down,cake|cake,lightbulb|light,envelope|envelope"
};
o2.LP.authentication = {
	"LoginFormTitle": "Welcome",
	"SignUpFormTitle": "Welcome",
	"ResetPasswordFormTitle": "Retrieve Password",
	"ChangePasswordFormTitle" : "Password has expired",
	"userName": "Username",
	"password": "Password",
	"verificationCode": "CAPTCHA",
	"loginAction": "Login",
	"autoLogin": "The next automatic landing",
	"signUp": "Registration",
	"forgetPassword": "Forgot Password？",
	"inputYourUserName": "Enter your username",
	"inputYourPassword": " Enter your password",
	"inputYourMail": "Please input your e-mail adresse",
	"inputYourMobile": "Please Input your Mobile Number",
	"inputPicVerificationCode": "Please enter the verification code on the right",
	"inputComfirmPassword": "Please confirm your password",
	"inputVerificationCode": "Verification Code",
	"confirmPassword": "Confirm Password",
	"sendVerification": "Send",
	"resendVerification": "ReSend",
	"passwordIsSimple": "Please use alphanumeric mix and at least 7 bits",
	"mobileIsRegisted": "Mobile phone number has been registered",
	"mailFormatError": "Please enter a properly formatted email",
	"hasAccount": "Already have an account？",
	"gotoLogin": "Goto Login",
	"weak": "weak",
	"middle": "middle",
	"high": "high",
	"userExist": "User already exists",
	"userNotExist": "The user does not exist.",
	"passwordNotEqual": "The password is inconsistent with the above, please try again",
	"changeVerification": "Change",
	"mail": "mail",
	"genderType": "Gender",
	"genderTypeText": ",Male, Female",
	"genderTypeValue": ",m,f",
	"selectGenderType": "Please select Gender",
	"registeSuccess": "Registration Successful",
	"codeLogin": "SMS Login",
	"passwordLogin": "Password Login",
	"bindLogin": "QRCode Login",
	"bingLoginTitle": "Scanning the QR code",
	"o2downloadLink": "https://sample.o2oa.net/app/download.html",
	"loginSuccess": "Login Success!",
	"userCheck": "Authentication",
	"shotMessageCheck": "SMS Verification",
	"setMewPassword": "Set New Password",
	"completed": "Completed",
	"nextStep": "Next",
	"mobile": "Cellphone",
	"setNewPassword": "Set New Password",
	"confirmNewPassword": "Confirm New Password",
	"passwordIsWeak": "Password length must be greater than 7",
	"resetPasswordSuccess": "Password Reset successful!",
	"resetPasswordFail": "Failed to reset your password!",
	"resetPasswordSuccessWord": "Please remember your new password.",
	"resetPasswordFailWord": "Please check your username and message authentication codes.",
	"backtoModify": "Return for correction",
	"pageNotFound": "404 error, the service was not found or the server has been disconnected",
	"submitAction" : "OK",
	"oldPassword" : "Old Password",
	"inputYourOldPassword" : "Enter the original password",
	"newPassword" : "New Password",
	"inputYourNewPassword" : "Enter a new password",
	"changePasswordSuccess" : "Successfully change password",
	"userAppCameraHtml" : "<div>open <div styles='bindTipLinkArea'>APP </div> and scan</div>",
	"loginToPage": "Login to the page"

};
o2.LP.script = {
	"error": "Script operation error, please check the following details"
};
