MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "工作",
    "selectRoute": "选择决策",
    "inputOpinion": "填写意见",
    "selectPerson" : "选择人员",
    "cancel": "取 消",
    "ok": "提 交",
    "close": "关　闭",
    "saveWrite": "保　存",
    "inputText": "请在此处填写流程意见",

    "mustSelectRoute": "请先选择决策",
    "mustSelectRouteGroup" : "请先选择决策组",
    "opinionRequired" : "请填写意见",

    "searchKey": "请输入关键字",

    "task": "待办中心",
    "done": "已办中心",
    "draft": "草稿箱",
    "myfile": "我的文件",
    "reset": "重置处理人",
    "reroute": "调度",
    "addSplit": "增加分支",
    "rollback": "流程回溯",
    "goBack": "退回",

    "phone": "手机",
    "mail": "邮箱",
    "save": "保存",
    "process": "继续流转",
    "flowWork": "继续流转",
    "handwriting": "手写",
    "audioRecord": "录音",

    "noAppendTaskIdentityConfig" : "没有配置转交人，请联系管理员",
    "selectAppendTaskIdentityNotice" : "请选择转交人",
    "routeValidFailure" : "路由校验失败",
    "loadedOrgCountUnexpected" : "人员选择界面未加载完成，请稍候...",

    "taskCompletedPerson": "办理人",
    "readPerson": "阅读人",
    "systemFlow": "系统自动处理",

    "openWorkError": "您没有权限查看该文档或该文档已删除。",

    "rollbackConfirmTitle": "流程回溯确认",
    "rollbackConfirmContent": "您确定要将流程回溯到“{log}”状态吗？（流程回溯会清除此状态之后的所有信息）",
    "rollbackSuccess" : "回溯成功",

    "recoverFileConfirmTitle": "恢复正文确认",
    "recoverFileConfirmContent": "您确定要将正文恢复到 “{att}”版本吗？（恢复后，已保存的临时文件将被删除，您无法再次恢复）",

    "notRecoverFileConfirmTitle": "取消正文恢复确认",
    "notRecoverFileConfirmContent": "您确定要取消正文恢复吗？（取消后，已保存的临时文件将被删除，您无法再次恢复）",

    "closePageCountDownText" : "将在“{second}”秒后关闭页面！",
    "closePage" : "关闭页面",

    "selectRouteGroup" : "选择决策组",
    "defaultDecisionOpinionName" : "其它",
    "routeGroupOrderList" : ["同意","不同意","其它","其他"],

    "selectWork": "您要打开的文件已形成多个分支，请选择其中一个查看：",
    "currentActivity": "当前活动: ",
    "currentUsers": "当前处理人: ",
    "completedWork": "文件已流转完成",

    "managerProcessNotice" : "注：快速处理功能适用于以下情况，否则可能出错：<br\>1、表单中已填好必填项。<br\>2、不需要在提交时选择人员。<br\>3、没有基于用户身份计算的内容。<br\>您是管理员，可以模拟待办人登录后在表单上提交，点击下面的链接执行。",
    "managerLogin" : "模拟登录并打开文件",
    "managerLoginConfirmTitle" : "模拟登陆",
    "managerLoginConfirmContent" : "确定要以{user}身份登录并打开文件？点击确定后，需注销重新登录才能回到当前用户。",
    "managerLoginSuccess" : "已成功切换为{user}",

    "selectIdentity": "选择办理此待办的身份",
    "selectIdentityInfo": "检测到当前工作您有多个不同身份的待办，请选择一个身份处理此工作",

    "org": "组织",
    "duty": "职务",

    "flowActions": {
        "addTask": "加签",
        "reset": "重置",
        "process": "提交",
        "goBack": "退回"
    },
    "modeType": "处理方式",
    "single": "单人",
    "queue": "串行",
    "parallel": "并行",
    "addTaskType": "加签方式",
    "addTaskBefore": "前加签",
    "addTaskAfter": "后加签",
    "opinion": "意见",
    "addTaskPerson": "加签人",
    "inputOpinionNote": "请在此处填写意见",
    "inputAddTaskPeople": "请选择加签人",
    "inputResetPeople": "请选择重置人",
    "inputAddTaskType": "请选择加签方式",
    "inputModeType": "请选择处理方式",
    "resetTo": "重置给",
    "keepTask": "保留我的待办",
    "quickSelect": "快速选择",
    "empowerTo": "授权给",
    "selectAll": "全选",
    "ok1": "确定",
    "not": "不",
    "selectPerson1": "请选择人员",
    "noQuickSelectDataNote": "系统还未记录您在当前节点选择的数据。",
    "submitQuickText": "选择[{route}]，意见：{opinion}{org}。",
    "addTaskQuickText": "选择[{route}{mode}]，意见：{opinion}，加签人：{org}。",
    "resetQuickText": "意见：{opinion}，重置给：{org}。",

    "users": "处理人",
    "goBackActivity": "退回到活动",
    "goBackActivityWay": "退回后处理：",
    "goBackActivityWayStep": "按流程正常流转",
    "goBackActivityWayJump": "直接回到退回人",
    "goBackTo": "退回到：",
    "selectGoBackActivity": "请选择要退回的活动"
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
