o2.LP = window.LP || {
    "name": "名称",
    "description": "描述",
    "searchKey": "请输入搜索关键字",
    "desktop_style": "桌面风格",
    "flat_style": "扁平风格"
};

o2.LP.process = {
    "unnamed": "未命名",
    "unknow": "未知",
    "processConfig": "流程配置",
    "formConfig": "表单配置",

    "createCategory": "创建分类",
    "searchCategory": "搜索分类",
    "noCategoryNotice": "现在还没有流程分类，您可以点击此处创建一个流程分类",
    "noProcessNoticeNode": "此分类现在还没有流程，您可以点击此处创建一个流程",

    "activity": "活动",
    "route": "路由",
    "property": "属性",
    "showJson": "查看JSON",
    "unrealized": "此功能未实现",
    "tools": "工具",
    "repetitions": "重复的属性名称",
    "repetitionsValue": "重复的内容项",
    "repetitionsEvent": "重复的事件名称",
    "repetitionsId": "重复的元素标识符",
    "notNullId": "元素标识符不能为空",
    "editCategory": "编辑分类",
    "createProcess": "新建流程",
    "deleteCategory": "删除分类",
    "deleteProcess": "删除流程",
    "editProcess": "编辑流程",
    "createForm": "新建表单",
    "deleteForm": "删除表单",
    "editForm": "编辑表单",

    "menu": {
        "newRoute": "新建路由",
        "newActivity": "新建活动",
        "newActivityType": {
            "manual": "人工活动",
            "condition": "条件活动",
            "auto": "自动活动",
            "split": "拆分活动",
            "merge": "合并活动",
            "embed": "子流程活动",
            "invoke": "调用活动",
            "begin": "开始活动",
            "end": "结束活动"
        },

        "copyActivity": "复制活动",

        "deleteActivity": "删除活动",
        "deleteRoute": "删除路由",

        "saveProcess": "保存流程",
        "saveProcessNew": "保存为新版本",
        "checkProcess": "检查流程",
        "exportProcess": "导出流程",
        "printProcess": "打印流程",

        "showGrid": "显示网格",
        "hideGrid": "隐藏网格"
    },
    "notice": {
        "save_success": "流程保存成功！",
        "deleteForm_success": "表单已删除！",
        "deleteProcess_success": "流程已删除！",
        "one_begin": "每个流程只能有一个开始活动！",
        "deleteRoute": "您确定要删除选中的路由吗？",
        "deleteRouteTitle": "删除路由确认",
        "deleteActivityTitle": "删除活动确认",
        "deleteActivity": "删除活动将同时删除关联此活动的所有路由，您确定要删除选中的活动吗？",
        "deleteDecisionTitle": "删除决策确认",
        "deleteDecision": "您确定要删除选中的决策吗？",
        "deleteScriptTitle": "删除脚本确认",
        "deleteScript": "您确定要删除当前脚本吗？",
        "deleteElementTitle": "删除表单元素确认",
        "deleteElement": "是否确定删除当前元素，及其子元素吗？",
        "deleteEventTitle": "删除事件确认",
        "deleteEvent": "是否确定删除当前事件吗？",

        "deleteActionTitle": "删除操作确认",
        "deleteAction": "是否确定删除当前操作吗？",

        "deleteRowTitle": "删除表格行确认",
        "deleteRow": "删除当前行将删除该行所有单元格中的内容，是否确定删除当前选中的行？",
        "deleteColTitle": "删除表格列确认",
        "deleteCol": "删除当前行将删除该列所有单元格中的内容，是否确定删除当前选中的列？",

        "deleteProcessTitle": "删除流程确认",
        "deleteProcess": "是否确定要删除当前流程？",

        "deleteFormTitle": "删除表单确认",
        "deleteForm": "是否确定要删除当前表单？",

        "deleteTreeNodeTitle": "删除节点确认",
        "deleteTreeNode": "是否确定要删除当前节点？",

        "inputScriptName": "请输入脚本名称!",
        "inputCategoryName": "请输入分类名称!"
    },
    "button": {
        "ok": "确定",
        "cancel": "取消"
    },
    "formAction": {
        "insertRow": "插入行",
        "insertCol": "插入列",
        "deleteRow": "删除行",
        "deleteCol": "删除列",
        "mergerCell": "合并单元格",
        "splitCell": "拆分单元格",
        "move": "移动",
        "copy": "复制",
        "delete": "删除",
        "add": "添加",
        "script": "脚本"
    }
};
o2.LP.desktop = {
    "loadding": "正在为您加载系统资源，请稍候......",
    "lowBrowser": "您的浏览器版本过低啦！~系统已经不支持IE8及以下版本了!",
    "upgradeBrowser": "请升级您的浏览器：",

    "menuAction": "菜单",
    "configAction": "配置您的工作台",
    "userMenu": "用户选项",
    "userChat": "用户设置",
    "styleAction": "切换主题",
    "showDesktop": "显示桌面",
    "showMessage": "消息",
    "logout": "注销",
    "userConfig": "个人设置",
    "application": "组件",
    "widget": "小工具",
    "process": "应用",
    "nosign": "未编辑个性签名",
    "searchUser": "搜索：用户名",
    "say": "说",
    "clearMessage": "清除消息",

    "lnkAppTitle": "常用应用",
    "deleteLnk": "删除常用应用快捷方式",
    "addLnk": "添加常用应用快捷方式",

    "changeViewTitle": "切换视图样式确认",
    "changeView": "您确定要切换视图样式吗？<br><br>如果您选择“确定”，页面将直接刷新，未保存的数据可能丢失。",

    "messsage": {
        "appliction": "应用",
        "application": "应用",
        "process": "流程",
        "infor": "信息",
        "query": "数据",
        "taskMessage": "待办提醒",
        "receiveTask": "您收到一条待办，标题为：",
        "activity": "活动",

        "readMessage": "待阅提醒",
        "receiveRead": "您收到一条待阅，标题为：",

        "reviewMessage": "阅读提醒",
        "receiveReview": "您收到一条阅读提醒，标题为：",

        "fileEditorMessage": "收到文件",
        "receiveFileEditor": "发送给您一个文件：",

        "fileShareMessage": "共享文件",
        "receiveFileShare": "共享给您一个文件：",

        "meetingInviteMessage": "会议邀请",
        "meetingInvite": "<font style='color: #ea621f'>{person}</font> 邀请您 于<font style='color: #ea621f'>{date}</font>参加会议：“{subject}”，地点：<font style='color: #ea621f'>{addr}</font>",
        "meetingCancelMessage": "会议取消",
        "meetingCancel": "<font style='color: #ea621f'>{person}</font> 取消了原定于<font style='color: #ea621f'>{date}</font>在<font style='color: #ea621f'>{addr}</font>举行的会议: “{subject}”",
        "meetingAcceptMessage": "会议邀请已被接受",
        "meetingAccept": "<font style='color: #ea621f'>{person}</font> 已接受您的会议邀请，将于<font style='color: #ea621f'>{date}</font>到<font style='color: #ea621f'>{addr}</font>参加会议: “{subject}”",
        "meetingRejectMessage": "会议邀请已被拒绝",
        "meetingReject": "<font style='color: #ea621f'>{person}</font> 已拒绝您的会议邀请。会议时间：<font style='color: #ea621f'>{date}</font>；会议标题: “{subject}”",

        "attendanceAppealInviteMessage": "有考勤申述需要您审批",
        "attendanceAppealInvite": "{subject}",
        "attendanceAppealAcceptMessage": "考勤申述通过",
        "attendanceAppealAccept": "{subject}",
        "attendanceAppealRejectMessage": "考勤申述未通过",
        "attendanceAppealReject": "{subject}",

        "customMessageTitle": "消息提醒：",
        "customMessage": "您收到一条消息："
    },
    "styleMenu": {
        "default": "默认",
        "color": "色彩",
        "black": "酷黑",
        "lotus": "荷花",
        "crane": "仙鹤",
        "peony": "牡丹",
        "car": "老爷车",
        "dock": "码头",
        "panda": "熊猫",
        "star": "星空"
    },
    "styleFlatMenu": {
        "blue": "蓝色",
        "red": "红色",
        "orange": "水果橙",
        "green": "青草绿",
        "cyan": "碧水青",
        "purple": "魅力紫",
        "gray": "沉稳灰",
        "darkgreen": "墨绿",
        "tan": "棕色",
        "navy": "藏青"
    },
    "notice": {
        "unload": "如果关闭或刷新当前页面，未保存的内容会丢失，请确定您的操作",
        "changePassword": "您的密码已过期，请及时修改密码",
        "errorConnectCenter1": "无法连接到应用中心服务器，请确保下列地址其中之一可以访问：",
        "errorConnectCenter2": "如果以上地址都无法访问，请检查您的网络，或联系管理员！"
    },
    "login": {
        "title": "用户登录",
        "loginButton": "登　录",

        "mobileDownload": "手机扫描二维码安装",

        "inputUsernamePassword": "请输入用户名和密码...",
        "loginWait": "登录中, 请稍候...",
        "loginError": "用户名或密码输入有误, 请重新输入...",

        "camera_logining": "正在为您登录，请正对摄像头 ...",
        "camera_logining_1": "请保持微笑 ...",
        "camera_logining_2": "请抬头 ...",
        "camera_logining_3": "验证成功 ...",

        "camera_logining2": "请移动不同角度，或变换表情 ...",
        "camera_loginSuccess": "{name}您好，正在为您登录 ...",
        "camera_loginError": "无法验证您的身份，请通过其他方式登录 ...",
        "camera_loginError2": "登录失败，请通过其他方式登录 ...",
        "camera_loginError_camera": "无法打开摄像头，可能已经在使用中 ..."
    },

    "action": {
        "uploadTitle": "正在上传",
        "uploadComplete": "上传完成",
        "sendReady": "正在编码数据，准备传输 . . .",
        "sendStart": "开始传输",
        "sendError": "文件传输出现错误",
        "sendAbort": "文件传输已被取消",
        "speed": "速度",
        "time": "耗时",
        "hour": "时",
        "minute": "分",
        "second": "秒",

        "cancelUploadTitle": "取消上传确认",
        "cancelUpload": "您确定要取消上传文件“{name}”吗？"
    },
    "person": {
        "personEmployee": "人员工号",
        "personMobile": "手机",
        "personMail": "邮件",
        "personDuty": "职务",
        "personQQ": "QQ号码",
        "personWeixin": "微信",
        "duty": "职务"
    },
    "collect": {
        "collectNotConnected": "连接O2云失败",
        "collectNotConnectedText": "无法连接到O2云，请检查服务器网络！"
    }
};
o2.LP.desktop.message = o2.LP.desktop.messsage;
o2.LP.widget = {
    "upload": "上传",
    "uploadTitle": "上传文件",
    "uploadInfor": "请选择要上传的文件",
    "delete": "删除",
    "replace": "替换",
    "select": "选择",

    "download": "下载",
    "share": "分享",
    "send": "发送",
    "downloadAll": "全部下载",
    "createFolder": "创建文件夹",
    "rename": "重命名",
    "property": "属性",
    "refuseUpload": "禁止文件上传",

    "list": "列表",
    "sequence": "序列",
    "icon": "图标",
    "preview": "预览",


    "min": "简洁模式",
    "max": "完整模式",

    "size": "大小",
    "uploader": "上传人",
    "uploadTime": "时间",
    "modifyTime": "修改",
    "uploadActivity": "活动",
    "attCount": "文件",
    "folderCount": "文件夹",

    "pictureSize": "图片显示宽{width}像素,高{height}像素",
    "pictureRatio": "图片宽高比为{ratio}",

    "ok": "确定",
    "cancel": "取消",
    "refresh": "刷新",
    "close": "关闭",
    "open": "打开",
    "choiceWork": "请选择一个文档打开",
    "workcompleted": "流转完成",

    "months": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
    "days_abbr": ["日", "一", "二", "三", "四", "五", "六"],

    "unknow": "未知",
    "uploadImg": "上传图片",
    "clearImg": "清除图片",
    "clearImg_confirmTitle": "清除图片确认",
    "clearImg_confirm": "您确定要清除图片吗？",
    "office": "在文档控件中打开",
    "closeOffice": "关闭附件",
    "configAttachment": "设置附件权限",
    "configAttachmentText": "设置",
    "checkOcrText": "纠正自动识别的图片中的文字",
    "order": "附件排序",

    "record": "录音",
    "stop": "停止",
    "play": "播放",
    "save": "保存"
};

o2.LP.widget.SimpleEditor = {
    "insertEmotion": "插入表情",
    "insertImage": "插入图像",
    "Emotions": "regular_smile|微笑，teeth_smile|大笑,angry_smile|生气,confused_smile|迷惑,cry_smile|大哭,embaressed_smile|尴尬,omg_smile|吃惊,sad_smile|难过,shades_smile|装酷,tounge_smile|吐舌,wink_smile|眨眼,angel_smile|天使,devil_smile|魔鬼,heart|红心,broken_heart|心碎,thumbs_up|顶,thumbs_down|踩,cake|蛋糕,lightbulb|灯泡,envelope|信封"
};
o2.LP.authentication = {
    "LoginFormTitle": "欢迎登录",
    "SignUpFormTitle": "欢迎注册",
    "ResetPasswordFormTitle": "找回密码",
    "userName": "用户名",
    "password": "密码",
    "verificationCode": "验证码",
    "loginAction": "登录",
    "autoLogin": "下次自动登陆",
    "signUp": "注册",
    "forgetPassword": "忘记密码？",
    "inputYourUserName": "请输入用户名",
    "inputYourPassword": "请输入密码",
    "inputYourMail": "请输入您的邮箱地址",
    "inputYourMobile": "请输入您的手机号码",
    "inputPicVerificationCode": "请输入右侧的验证码",
    "inputComfirmPassword": "请确认您的密码",
    "inputVerificationCode": "请输入手机验证码",
    "confirmPassword": "确认密码",
    "sendVerification": "发送验证码",
    "resendVerification": "重新发送",
    "passwordIsSimple": "请使用数字字母混合且至少7位",
    "mobileIsRegisted": "手机号码已经被注册",
    "hasAccount": "已有账号？",
    "gotoLogin": "去登录",
    "weak": "弱",
    "middle": "中",
    "high": "强",
    "userExist": "用户已经存在",
    "userNotExist": "用户不存在",
    "passwordNotEqual": "密码与上面不一致，请重新输入",
    "changeVerification": "换一张",
    "genderType": "性别",
    "genderTypeText": ",男,女",
    "genderTypeValue": ",m,f",
    "selectGenderType": "请选择性别",
    "registeSuccess": "注册成功",
    "codeLogin": "短信验证登录",
    "passwordLogin": "密码登录",
    "bindLogin": "二维码登录",
    "bingLoginTitle": "手机扫码，安全登录",
    "o2downloadLink": "https://sample.o2oa.net/app/download.html",
    "loginSuccess": "登录成功！",
    "userCheck": "用户认证",
    "shotMessageCheck": "短信验证",
    "setMewPassword": "设置新密码",
    "completed": "完成",
    "nextStep": "下一步",
    "mobile": "手机",
    "setNewPassword": "设置新密码",
    "confirmNewPassword": "确认新密码",
    "passwordIsWeak": "密码太弱，请使用数字和字母，且长度大于7",
    "resetPasswordSuccess": "重置密码成功！",
    "resetPasswordFail": "重置密码失败！",
    "resetPasswordSuccessWord": "请牢记您新设置的密码。",
    "resetPasswordFailWord": "请核对您的用户名和短信验证码。",
    "backtoModify": "返回修改",
    "pageNotFound": "404错误，未找到服务或服务器已断开"
};
o2.LP.script = {
    "error": "脚本运行错误，请查看以下详细信息"
};
