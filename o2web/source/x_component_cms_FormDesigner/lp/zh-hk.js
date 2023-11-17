MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
	"selectApplication": "選擇應用程式",
	"formType": {
		"empty": "空白表單",
		"publishEdit": "發佈類編輯表單",
		"publishRead": "發佈類閱讀表單",
		"publishEditGreen": "發佈類編輯表單 (綠色)",
		"publishReadGreen": "發佈類閱讀表單 (綠色)",
		"dataInput": "資料輸入類表單"
	},
	"validation": {
		"publish": "發佈時"
	},
	"modules": {
		"reader": "讀者",
		"commend": "讚好",
		"author": "作者",
		"log": "閱讀記錄",
		"comment": "評論",
		"logCommend": "讚好記錄",
		"group_cms": "內容管理組件"
	},
    "formStyle":{
        "noneStyle": "空樣式",
		"defaultStyle": "傳統樣式",
		"redSimple": "紅色簡潔",
		"blueSimple": "藍色簡潔",
		"greenFlat": "綠色扁平",
		"defaultMobileStyle": "手機樣式",
		"banner": "橫幅",
		"title": "標題",
		"sectionTitle": "區段標題",
		"section": "區段"
    },
    "propertyTemplate": {
        "setPopular": "設置熱門操作",
		"commentPerPage": "每頁評論數",
		"tiao": "條",
		"allowModifyComment": "發表後允許修改",
		"allowComment": "允許發表評論",
		"editor": "編輯器",
		"editorTitle": "CKEditor配置腳本",
		"editorConfigNote": "返回CKEditor的配置對象，用於編輯器初始化",
		"editorConfigLinkNote": "更多屬性請查看",
		"table": "表格",
		"text": "文本",
		"format": "格式",
		"validationSave": "保存驗證",
		"validationPublish": "發佈驗證",
		"notice": "消息",
		"noticeInfo": "注：消息發送的總開關在分類配置中設置。",
		"noticeRange": "範圍",
		"noticeByReader": "根據閱讀範圍",
		"noticeByCustom": "自定義",
		"notifyCreatePerson": "通知創建人",
		"blankToAllNotify": "讀者（發佈範圍）為空時通知閱讀範圍",
		"blankNotToAllNotify": "讀者（發佈範圍）為空時不通知",
		"specificValue": "指定",
		"formField": "表單字段"
    },
    "actionBar": {
        "close": "關閉",
		"closeTitle": "關閉文檔",
		"edit": "編輯",
		"editTitle": "編輯文檔",
		"save": "保存",
		"saveTitle": "保存文檔",
		"publish": "發佈",
		"publishTitle": "發佈文檔",
		"publishDelayed": "定時發佈",
		"publishDelayedTitle": "定時發佈文檔",
		"saveDraft": "保存草稿",
		"saveDraftTitle": "保存草稿",
		"popular": "設置熱門",
		"popularTitle": "設置熱門",
		"delete": "刪除",
		"deleteTitle": "刪除文檔",
		"print": "打印",
		"printTitle": "打印文檔",
		"setTop": "置頂",
		"setTopTitle": "對文檔置頂",
		"cancelTop": "取消置頂",
		"cancelTopTitle": "取消對文檔的置頂",
		"downloadAll": "一鍵下載",
		"downloadAllTitle": "一鍵下載"
    }
});
