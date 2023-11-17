MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "工作",
    "selectRoute": "選擇決策",
    "inputOpinion": "填寫意見",
    "selectPerson" : "選擇人員",
    "cancel": "取消",
    "ok": "確認",
    "close": "關閉",
    "saveWrite": "保存",
    "inputText": "請在此處填寫流程意見",

    "mustSelectRoute": "請先選擇決策",
    "mustSelectRouteGroup" : "請先選擇決策組",
    "opinionRequired" : "請填寫意見",

    "searchKey": "請輸入關鍵字",

    "task": "待辦中心",
    "done": "已辦中心",
    "draft": "草稿箱",
    "myfile": "我的文件",
    "reset": "重置處理人",
    "reroute": "調度",
    "addSplit": "增加分支",
    "rollback": "流程回溯",
    "goBack": "退回",

    "phone": "手機",
    "mail": "郵箱",
    "save": "保存",
    "process": "繼續流轉",
    "handwriting": "手寫",
    "audioRecord": "錄音",

    "noAppendTaskIdentityConfig" : "沒有配置轉交人，請聯絡管理員",
    "selectAppendTaskIdentityNotice" : "請選擇轉交人",
    "routeValidFailure" : "路由校驗失敗",
    "loadedOrgCountUnexpected" : "人員選擇界面未加載完成，請稍候...",

    "taskCompletedPerson": "辦理人",
    "readPerson": "閱讀人",
    "systemFlow": "系統自動處理",

    "openWorkError": "您沒有權限查看該文件或該文件已刪除。",

    "rollbackConfirmTitle": "流程回溯確認",
    "rollbackConfirmContent": "您確定要將流程回溯到“{log}”狀態嗎？（流程回溯會清除此狀態之後的所有信息）",

    "recoverFileConfirmTitle": "恢復正文確認",
    "recoverFileConfirmContent": "您確定要將正文恢復到 “{att}”版本嗎？（恢復後，已保存的臨時文件將被刪除，您無法再次恢復）",

    "notRecoverFileConfirmTitle": "取消正文恢復確認",
    "notRecoverFileConfirmContent": "您確定要取消正文恢復嗎？（取消後，已保存的臨時文件將被刪除，您無法再次恢復）",

    "closePageCountDownText" : "將在“{second}”秒後關閉頁面！",
    "closePage" : "關閉頁面",

    "selectRouteGroup" : "選擇決策組",
    "defaultDecisionOpinionName" : "其他",
    "routeGroupOrderList" : ["同意","不同意","其他"],

    "selectWork": "您要打開的文件已形成多個分支，請選擇其中一個查看：",
    "currentActivity": "當前活動: ",
    "currentUsers": "當前處理人: ",
    "completedWork": "文件已流轉完成",

    "managerProcessNotice" : "註：快速處理功能適用於以下情況，否則可能出錯：<br\>1、表單中已填寫必填項。<br\>2、不需要在提交時選擇人員。<br\>3、沒有基於使用者身份計算的內容。<br\>您是管理員，可以模擬待辦人登錄後在表單上提交，點擊下面的鏈接執行。",
    "managerLogin" : "模擬登錄並打開文件",
    "managerLoginConfirmTitle" : "模擬登錄",
    "managerLoginConfirmContent" : "確定要以{user}身份登錄並打開文件？點擊確定後，需註銷重新登錄才能回到當前使用者。",
    "managerLoginSuccess" : "已成功切換為{user}",

    "selectIdentity": "選擇辦理此待辦的身份",
    "selectIdentityInfo": "檢測到當前工作您有多個不同身份的待辦，請選擇一個身份處理此工作",

    "org": "組織",
    "duty": "職務"
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
