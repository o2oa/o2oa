MWF.LP = window.LP || {
	"name": "Name",
	"description": "Description"
};

MWF.LP.process = {
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
		"save_success": "Save the process successful!",
		"deleteForm_success": "Form deleted!",
		"deleteProcess_success": "Process deleted!",
		"one_begin": "Each process can have only one begin activity!",
		"deleteRoute": "You sure you want to delete the selected route?",
		"deleteRouteTitle": "Delete route confirmation",
		"deleteActivityTitle": "Delete activity confirmation",
		"deleteActivity": "Deleting the activity will also delete all routes associated with this activity, you sure you want to delete the selected activity？",
		"deleteDecisionTitle": "Delete decision confirmation",
		"deleteDecision": "You sure you want to delete the selected decision?",
		"deleteScriptTitle": "Delete script confirmation",
		"deleteScript": "You sure you want to delete the selected script?",
		"deleteElementTitle": "Delete form element confirmation",
		"deleteElement": "Are you sure to delete the current element and its child elements?",
		"deleteEventTitle": "Delete event confirmation",
		"deleteEvent": "You sure you want to delete the selected event?",

		"deleteActionTitle": "Delete operation confirmation",
		"deleteAction": "You sure you want to delete the selected operation?",

		"deleteRowTitle": "Delete table row confirmation",
		"deleteRow": "Deletes the current row is the row is deleted all the content in the cells, are you sure to delete the currently selected row?",
		"deleteColTitle": "Delete Table Columns confirmation",
		"deleteCol": "Deletes the current column is the column is deleted all the content in the cells, are you sure to delete the currently selected column?",

		"deleteProcessTitle": "Delete process confirmation",
		"deleteProcess": "You sure you want to delete the selected process?",

		"deleteFormTitle": "Delete form confirmation",
		"deleteForm": "You sure you want to delete the selected form?",

		"deleteTreeNodeTitle": "Delete node confirmation",
		"deleteTreeNode": "You sure you want to delete the selected node?",

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
MWF.LP.desktop = {
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

	"messsage": {
		"appliction": "Application",
		"process": "Process",
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
		"meetingCancel": "<font style='color: #ea621f'>{person}</font> 取消了原定于<font style='color: #ea621f'>{date}</font>在<font style='color: #ea621f'>{addr}</font>举行的会议: “{subject}”",
		"meetingAcceptMessage": "Meeting invitation has been accepted",
		"meetingAccept": "<font style='color: #ea621f'>{person}</font> 已接受您的会议邀请，将于<font style='color: #ea621f'>{date}</font>到<font style='color: #ea621f'>{addr}</font>参加会议: “{subject}”",
		"meetingRejectMessage": "Meeting invitation has been rejected",
		"meetingReject": "<font style='color: #ea621f'>{person}</font> 已拒绝您的会议邀请。会议时间：<font style='color: #ea621f'>{date}</font>；会议标题: “{subject}”",

		"attendanceAppealInviteMessage": "有考勤申述需要您审批",
		"attendanceAppealInvite": "{subject}",
		"attendanceAppealAcceptMessage": "考勤申述通过",
		"attendanceAppealAccept": "{subject}",
		"attendanceAppealRejectMessage": "考勤申述未通过",
		"attendanceAppealReject": "{subject}"

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
	"notice": {
		"unload": "If you close or refresh the current page, the content is not saved is lost, please make sure your operation",
		"changePassword": "Your password has expired, please do not hesitate to change the password"
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
MWF.LP.widget = {
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

	"list": "List",
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
	"pictureRatio": "图片宽高比为{ratio}",

	"checkOcrText": "纠正自动识别的图片中的文字",
	"order": "order",

	"ok": "Ok",
	"cancel": "Cancel",
	"refresh": "Refresh",
	"close": "close",
	"open": "open",
	"choiceWork": "choice document open",

	"months": ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
	"days_abbr": ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],

	"unknow": "unknow"
};

MWF.LP.widget.SimpleEditor = {
	"insertEmotion": "Insert Expressions",
	"insertImage": "Insert Image",
	"Emotions": "regular_smile|微笑，teeth_smile|大笑,angry_smile|生气,confused_smile|迷惑,cry_smile|大哭,embaressed_smile|尴尬,omg_smile|吃惊,sad_smile|难过,shades_smile|装酷,tounge_smile|吐舌,wink_smile|眨眼,angel_smile|天使,devil_smile|魔鬼,heart|红心,broken_heart|心碎,thumbs_up|顶,thumbs_down|踩,cake|蛋糕,lightbulb|灯泡,envelope|信封"
};
MWF.LP.authentication = {
	"LoginFormTitle": "Welcome",
	"SignUpFormTitle": "Welcome",
	"ResetPasswordFormTitle": "Retrieve Password",
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
	"inputVerificationCode": "Please enter the mobile verification code",
	"confirmPassword": "Confirm Password",
	"sendVerification": "Send Code",
	"resendVerification": "ReSend",
	"passwordIsSimple": "Please use alphanumeric mix and at least 7 bits",
	"mobileIsRegisted": "Mobile phone number has been registered",
	"hasAccount": "Already have an account？",
	"gotoLogin": "Goto Login",
	"weak": "weak",
	"middle": "middle",
	"high": "high",
	"userExist": "User already exists",
	"userNotExist": "The user does not exist.",
	"passwordNotEqual": "The password is inconsistent with the above, please try again",
	"changeVerification": "Change",
	"genderType": "Gender",
	"genderTypeText": ",Men, Female",
	"genderTypeValue": ",m,f",
	"selectGenderType": "Please select Gender",
	"registeSuccess": "Registration Successful",
	"codeLogin": "QRCode Login",
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
	"pageNotFound": "404 error, the service was not found or the server has been disconnected"
};