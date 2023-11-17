o2.xApplication.systemconfig.LP = {
    "title": "系統配置",
    "searchKey": "搜尋設定項目",
    "default": "默認",
    "permissionDenied": "當前用戶權限不足，您必須使用管理員帳號訪問系統配置",

    "yes": "是",
    "no": "否",
    "uploadInfo": "將文件拖到此處，或點擊上傳",

    "baseConfig": "基礎配置",
    "systemInfo": "系統信息",
    "uiConfig": "界面配置",
    "componentDeploy": "組件部署",
    "resourceDeploy": "資源部署",
    "serviceDeploy": "服務部署",

    "securityConfig": "安全配置",
    "passwordConfig": "密碼配置",
    "loginConfig": "登錄配置",
    "ssoConfig": "單點登錄",
    "ternaryManagement": "三元管理",

    "serverConfig": "服務器配置",
    "serversConfig": "伺服器配置",
    "centerServer": "中心伺服器",
    "appServer": "應用伺服器",
    "webServer": "Web伺服器",
    "databaseServer": "資料庫配置",
    "storageServer": "儲存配置",
    "cacheConfig": "快取配置",
    "clusterConfig": "集群配置",
    "orgConfig": "組織配置",
    "processConfig": "流程配置",
    "cloudConfig": "雲服務配置",
    "dumpConfig": "備份配置",
    "worktimeConfig": "工作時間",

    "messageConfig": "消息配置",
    "msgTypeConfig": "類型配置",
    "pushConfig": "消息推送配置",
    "mailConfig": "郵件配置",
    "smsConfig": "短信配置",
    "mqConfig": "消息佇列配置",

    "queryIndexConfig": "索引配置",

    "mobileConfig": "移動端配置",
    "connectConfig": "連接配置",
    "appConfig": "APP配置",
    "moduleConfig": "模塊配置",
    "iconConfig": "圖示配置",
    "ddConfig": "釘釘集成",
    "wechatConfig": "微信集成",
    "welinkConfig": "welink集成",
    "appTools": "APP工具",
    "integrationConfig": "應用集成",

    "select": "選擇",

    "_systemInfo": {
        "title": "配置系統的基本信息",
		"systemName": "系統名稱",
		"systemNameInfo": "您的系統名稱，它將會顯示在您的登錄頁面和瀏覽器標題欄",
		"systemSubTitle": "系統副標題",
		"systemSubTitleInfo": "您的系統副標題，它將會顯示在您的登錄頁面下方",
		"systemVersion": "當前系統版本",
		"systemVersionInfo": "當前系統版本",
		"baseInfo": "基本信息",
		"systemStatus": "系統狀態",
		"moduleStatus": "模塊運行狀態",
		"language": "語言環境",
		"languageInfo": "設置伺服器語言環境",
		"languageValues": {
			"zh-CN": "簡體中文",
			"en": "英文",
			"es": "西班牙文"
		},
		"running": "運行中",
		"stop": "已停用",
		"enable": "已啟用",
		"server": "伺服器",
		"node": "節點",
		"serverInfo": "伺服器信息",
		"webServer": "WEB伺服器",
		"appServer": "應用伺服器",
		"centerServer": "中心伺服器",
		"dataServer": "數據庫服務",
		"storageServer": "文件存儲服務",
		"dataNode": "數據庫",
		"databaseUrl": "數據庫連接",
		"byModule": "按應用模塊",
		"byServer": "按服務節點",
		"storageNode": "文件存儲",

        "serverData": {
			// "exposeJest": "接口文檔(exposeJest)",
			"httpProtocol": "http協議(httpProtocol)",
			"host": "主機(host)",
			"port": "端口(port)",
			"proxyHost": "代理主機(proxyHost)",
			"proxyPort": "代理端口(proxyPort)",
			"requestLogEnable": "啟用http日誌",
			"requestLogBodyEnable": "記錄Body內容",
			"requestLogRetainDays": "日誌保留天數",
			"sslEnable": "啟用SSL(sslEnable)",
			// "statEnable": "啟用Druid",

			"cacheSize": "緩存大小(cacheSize)",
			"includes": "包含類(includes)",
			"excludes": "排除類(excludes)",
			"jmxEnable": "啟用JMX(jmxEnable)",
			"lockTimeout": "表鎖超時(lockTimeout)",
			"logLevel": "日誌級別(logLevel)",
			"maxIdle": "最大空閑連接數(maxIdle)",
			"maxTotal": "最大連接數(maxTotal)",
			"slowSqlMillis": "慢SQL閾值(slowSqlMillis)",
			"statFilter": "啟用Druid語句合併(statFilter)",
			"tcpPort": "TCP端口(tcpPort)",
			"webPort": "WEB端口(webPort)"
		},
        "storageData": {
			"port": "ftp端口(port)",
			"sslEnable": "啟用SSL(sslEnable)",
			"name": "名稱(name)",
			"passivePorts": "被動模式端口(passivePorts)",
			"prefix": "路徑前綴(prefix)",
			"deepPath": "使用深路徑(deepPath)"
		},
		"storageAccounts": {
			"protocol": "協議",
			"username": "模塊",
			"weight": "權重",
			"name": "名稱",
			"prefix": "路徑前綴",
			"deepPath": "使用深路徑",
			"host": "主機",
			"port": "端口"
		},
		"moduleData": {
			"node": "服務節點",
			"contextPath": "上下文",
			"port": "服務端口",
			"sslEnable": "啟用SSL",
			"proxyHost": "代理主機",
			"proxyPort": "代理端口",
			"reportDate": "上次報告時間",
			"moduleName": "模塊名稱",
			"className": "類別"
		}
    },
    "operation": {
        "edit": "編輯",
        "ok": "確定",
        "cancel": "取消",
        "enable": "啟用",
        "disable": "禁用"
    },
    "_component": {
        "open": "打開",
        "edit": "編輯",
        "uninstall": "卸載",

        "deploy": "部署組件",

        "removeComponentTitle": "卸載組件確認",
        "removeComponent": "您確定要卸載組件：{name} 嗎？",
        "removeComponentOk": "組件已卸載",

        "deploySuccess": "組件部署成功",

        "selectIcon": "選擇圖標",
        "clearIcon": "清除圖標",

        "name": "組件名稱",
        "title": "組件標題",
        "path": "組件路徑",
        "urlPathInfo": "您可以通過“@url:”將路徑添加為一個網頁URL，例如“@url:http://www.bing.com”",
        "visible": "是否可見",
        "allowList": "可訪問列表",
        "denyList": "拒絕訪問列表",
        "icon": "組件圖標",

        "upload": "上傳資源",
        "uploadWarn": "上傳組件zip包，原有組件將被覆蓋，請謹慎操作！",

        "componentDataError": "組件名稱、組件路徑和組件標題不能為空"
    },
    "_resource": {
        "webResource": "部署Web資源",
        "webResourceInfo": "您可以在此處部署Web資源，上傳靜態資源文件或zip文件，它將被部署到系統的Web伺服器，可以通過Http協議訪問到。",
        "serviceResource": "部署自定義服務",
        "serviceResourceInfo": "您可以在此處部署您開發的自定義工程，上傳編譯後的jar包或者war包。部署後需要重啟伺服器。",

        "componentResource": "組件部署",
        "componentResourceInfo": "您自定義開發的O2OA組件，或從官方獲取組件，都可以在此處部署。O2OA組件是名為“x_component_{組件名稱}”的文件夾或zip文件。更多詳細信息請查閱：<a href='https://www.o2oa.net/develop.html' target='_blank'>O2OA官方社區。</a>",

        "upload": "上傳資源",
        "webUploadWarn": "上傳要部署的靜態資源文件，zip文件會自動解壓",
        "serviceUploadWarn": "上傳要部署的jar包或者war包",

        "overwrite": "部署方式",
        "overwriteFalse": "刪除後上傳：刪除同名文件和文件夾後上傳。",
        "overwriteTrue": "覆蓋：直接覆蓋同名文件和文件夾。",

        "deployPath": "部署路徑",
        "deployPathInfo": "如果部署zip文件，路徑可以為空；單個文件部署必須指定部署路徑。例如：/myWebResource/subPath",

        "noDeployFile": "請先選擇要部署的資源文件",
        "deploySuccess": "部署資源成功",

        "notWebResource": "<span style='color: red'>當前伺服器不允許前端部署Web資源，您可以到伺服器配置-伺服器任務中啟用此功能</span>",
        "notServiceResource": "<span style='color: red'>當前伺服器不允許前端部署自定義服務，您可以到伺服器配置-伺服器任務中啟用此功能</span>"
    },
    "_uiConfig": {
        "baseConfig": "基本配置",
        "menuConfig": "主菜單配置",
        "lnkConfig": "側邊欄配置",
        "userConfig": "用戶界面配置",

        "openStatus": "進入系統",
        "openStatusInfo": "每次進入O2OA系統時，默認會打開上一次退出系統時打開的應用，您可以在此處改變這一行為。",
        "openStatusCurrent": "將打開的應用和當前應用都定位到上一次退出時的狀態（默認）",
        "openStatusApp": "打開上一次退出系統時的應用，並將首頁作為當前應用",
        "openStatusIndex": "只打開首頁應用",

        "skin": "系統皮膚",
        "skinConfig": "允許修改系統皮膚",
        "skinConfigInfo": "是否允許用戶個性化修改修改系統皮膚",
        "skinDefault": "系統默認皮膚",
        "skinDefaultInfo": "設置系統默認皮膚色系",
        "scaleConfig": "是否允許縮放",
        "scaleConfigInfo": "是否允許用戶個性化設置系統顯示的縮放比例",

        "defaultMenuInfo": "保存為默認菜單設置後，未進行個性化菜單設置的用戶，會按此設置展現菜單。",
        "forceMenuInfo": "保存為強制菜單設置後，所有的用戶都會按此設置展現菜單，個性化設置將會失效。",
        "userMenuInfo": "所有用戶的個性化菜單設置將被清除，以默認方式展現菜單。",

        "clearDefaultMenuDataTitle": "清除默認菜單設置",
        "clearDefaultMenuData": "您是否確認清除默認菜單設置？",
        "clearDefaultMenuDataSuccess": "默認菜單設置已清除",
        "clearForceMenuDataTitle": "清除強制菜單設置",
        "clearForceMenuData": "您是否確認清除強制菜單設置？",
        "clearForceMenuDataSuccess": "強制菜單設置已清除",

        "clearUserMenuData": "清除用戶個性化菜單設置",
        "clearUserMenuDataSuccess": "用戶個性化菜單設置已清除",
        "clearUserMenuDataConfirm": "您確定要清除所有用戶的個性化菜單設置",

        "saveDefaultMenuDataSuccess": "默認菜單設置保存成功",
        "saveForceMenuDataSuccess": "強制菜單設置保存成功",

        "defaultMenu": "默認菜單配置",
        "forceMenu": "強制菜單配置",
        "userMenu": "用戶個性化菜單配置",

        "saveMenu": "保存配置",
        "clearMenu": "清除配置",
        "loadMenu": "載入配置",
        "clearUserMenu": "清除配置",

        "menu": {
            "application": "應用",
            "process": "流程",
            "cms": "信息",
            "query": "數據",

            "defaultMenu": "恢復默認菜單狀態"
        },
        "deleteLink": "刪除常用應用快捷方式"
    },
    "_passwordConfig": {
        "personPassword": "用戶密碼設置",
		"adminPassword": "管理員密碼",
		"saveSuccess": "配置保存成功",
		"passwordScript": "密碼腳本",

		"newPersonPassword": "新建用戶的初始密碼",
		"newPersonPasswordInfo": "創建新建用戶時，會按以下設定生成用戶初始密碼，用戶可登錄系統後自行修改",
		"initialPassword": "用戶初始密碼",
		"initialPasswordText": "輸入初始密碼",
		"initialPasswordTypeOptions": {
			"mobile": "手機號碼後六位",
			"unique": "唯一編碼後六位",
			"employee": "人員工號",
			"pinyin": "人員名稱全拼",
			"text": "固定口令",
			"script": "通過腳本自定義初始密碼"
		},
		"initialPasswordType": {
			"mobileScript": "return person.getMobile().slice(-6)",
			"uniqueScript": "return person.getUnique().slice(-6)",
			"employeeScript": "return person.getEmployee()",
			"pinyinScript": "return person.getPinyin()",
			"textInfo": "在下面的輸入框中輸入的密碼，將作為新建用戶的初始密碼。",
			"scriptInfo": "在下面的編輯器中輸入腳本，返回一個字符串值，作為新建用戶的初始密碼。您可以使用person對象獲取人員相關信息。例如，將人員姓名全拼作為初始密碼，可以使用腳本：return person.getPinyin()"
		},

        "passwordPeriod": "密碼過期天數",
		"passwordPeriodInfo": "超過此設定天數未修改密碼的使用者，登錄後將強制要求修改密碼，否則無法進入系統。設置為 0 表示密碼永不過期",

		"passwordRegex": "密碼複雜度",
		"passwordRegexInfo": "設置使用者密碼複雜度要求",

		"passwordRegexMin": "最小長度",
		"passwordRegexMax": "最大長度",
		"passwordRegexLength": "密碼長度",
		"passwordRule": "密碼規則",
		"passwordRuleValue": {
			"useLowercase": "必須包含小寫字母",
			"useNumber": "必須包含數字",
			"useUppercase": "必須包含大寫字母",
			"useSpecial": "必須包含特殊字符 (#?!@$%^&*-)"
		},
		"passwordRuleRegex": {
			"useLowercase": "(?=.*[a-z])",
			"useNumber": "(?=.*\\d)",
			"useUppercase": "(?=.*[A-Z])",
			"useSpecial": "(?=.*?[#?!@$%^&*-])"
		},
		"savePasswordRule": "保存密碼規則設置",
		"passwordLengthText": "{n}位，{text}",

		"passwordRsa": "密碼加密傳輸",
		"passwordRsaInfo": "系統默認使用明文傳輸，您可以啟用此選項以啟用密碼的加密傳輸。（修改後需要重新啟動服務器）",


        "adminPasswordInfo": "您可以在此處修改超級管理員 xadmin 的密碼。(修改後需要重啟伺服器)",
		"modifyAdminPassword": "修改管理員密碼",

		"oldPassword": "原密碼",
		"newPassword": "新密碼",
		"confirmPassword": "確認密碼",

		"ternaryPassword": "三員管理員密碼",
		"ternaryPasswordInfo": "如果您啟用了三員管理，系統管理員可以在此處修改系統管理員（systemManager）、安全管理員（securityManager）和安全審計員（auditManager）的密碼。",
		"modifySystemManagerPassword": "修改系統管理員密碼",
		"modifySecurityManagerPassword": "修改安全管理員密碼",
		"modifyAuditManagerPassword": "修改安全審計員密碼",

		"passwordDisaccord": "您輸入的新密碼與確認密碼不一致",
		"passwordEmpty": "請輸入原密碼、新密碼和確認密碼",

		"tokenEncryptType": "密碼加密方式",
		"tokenEncryptTypeInfo": "O2OA 支持以下幾種密碼和令牌加密方式，可以根據需要選擇。更多信息請查看：<a href='https://www.o2oa.net/search.html?q=%E5%9B%BD%E5%AF%86' target='_blank'>國密</a>",
		"tokenEncryptTypeLabel": "加密方式",
		"encryptTypeOptions": {
			"default": "默認",
			"sm4": "國家商用密碼算法"
		},
		"tokenEncryptTypeInfo3": "<div style='color: red'>注意：點擊“確定修改密碼加密方式”後，此設置立即生效。<ul style='line-height: 30px'><li>這會導致：1. 所有用戶的登錄狀態過期。2. 由於加密方式改變，所有現有用戶將無法登錄系統。</li>" +
			"<li>您必須執行以下步驟，才能正常使用系統：<br> 使用 xadmin 帳戶重新登錄系統，並通過任何方式重置所有用戶密碼。</li></ul></div>",
		"tokenEncryptTypeButton": "確定修改密碼加密方式",
		"changeTokenEncryptTypeInfo": "您確定要修改密碼加密方式嗎？"
    },
    "_loginConfig": {
        "baseConfig": "基本配置",
		"moreConfig": "更多配置",
		"ldapConfig": "LDAP認證配置",
		"captchaLogin": "啟用圖片驗證碼登錄",
		"codeLogin": "啟用短信驗證碼登錄",
		"bindLogin": "啟用掃描二維碼登錄",
		"faceLogin": "啟用人臉識別登錄",
		"captchaLoginInfo": "啟用後登錄時必須正確輸入圖片驗證碼",
		"codeLoginInfo": "啟用後允許使用短信驗證碼登錄",
		"bindLoginInfo": "啟用後允許掃描二維碼登錄",
		"faceLoginInfo": "啟用後允許人臉識別登錄，用戶可到個人設置中設置人臉特徵。啟用後您必須創建一個SSO配置，名稱為face，密鑰為xplatform（這是一個試驗性功能，您必須啟用https）",

		"loginError": "登錄錯誤處理",
		"loginErrorInfo": "用戶登錄時，如果連續多次輸入錯誤密碼，帳號將被鎖定。您可以在此處設置連續登錄錯誤次數上限，以及帳號鎖定的時長。",

		"loginErrorCount": "登錄錯誤次數上限",
		"lockTime": "鎖定時長（分鐘）",

		"tokenExpired": "登錄有效時長",
		"tokenExpiredInfo": "用戶登錄系統後，如果長時間不與伺服器互動，系統將註銷該次登錄。您可以在此處設置登錄有效時長，單位為分鐘。",

		"tokenName": "token名稱",
		"tokenNameInfo": "系統默認的token名稱為x-token，您可以在此處修改token名稱，以防止在相同Domain下的Cookie衝突，這在相同Domain下部署多套O2OA時尤其有用。(需要重啟伺服器)",

		"tokenCookieHttpOnly": "啟用Cookie HttpOnly",
		"tokenCookieHttpOnlyInfo": "保存token的cookie是否啟用httponly",

		"tokenCookieSecure": "啟用Cookie Secure",
		"tokenCookieSecureInfo": "保存token的cookie是否啟用secure，表示僅在https協議才會傳輸此cookie",

		"enableSafeLogout": "啟用安全註銷",
		"enableSafeLogoutInfo": "啟用安全註銷後，您在任意終端執行註銷操作，將會同時註銷所有終端的登錄狀態。",

		"register": "啟用自助註冊",
		"registerInfo": "此處配置是否允許自助註冊成為系統用戶，以及自助註冊方式",
		"registerValues": {
			"disable": "不允許",
			"captcha": "通過驗證碼註冊",
			"code": "通過短信註冊"
		},

        "loginPage": "使用門戶頁面登錄",
		"loginPageInfo": "系統支援將自定義的門戶頁面用作登錄頁面，您可以在應用市場上免費獲取登錄頁應用模板。",
		"loginPagePortal": "登錄門戶",

		"selectPortal": "請選擇門戶",

		"indexPage": "使用門戶頁面作為系統首頁",
		"indexPageInfo": "可使用自定義的門戶頁面作為系統首頁，登錄後將打開此頁面。",
		"indexPagePortal": "首頁門戶",

		"ldapAuthEnable": "啟用LDAP認證",
		"ldapAuthEnableInfo": "啟用後，使用LDAP認證進行用戶登錄，不再使用本系統的密碼登錄。請正確配置下面的LDAP參數。",
		"ldapAuthUrl": "LDAP地址",
		"ldapAuthUrlInfo": "LDAP服務地址，ldap://域名或IP:端口",
		"baseDn": "LDAP查詢根(BaseDN)",
		"baseDnInfo": "LDAP查詢的根名稱，例如：dc=zone, DC=COM",
		"userDn": "認證用戶綁定屬性",
		"userDnInfo": "認證用戶綁定屬性：uid、手機號、員工編號或郵箱（必須確保在baseDn下查找到的數據是唯一的並且在o2中能查到關聯人員，例如：uid或mail等",

		"superPermission": "啟用超級管理員口令",
		"superPermissionInfo": "啟用此選項允許使用超級管理員（xadmin）的口令登錄其他用戶帳戶，以方便管理員以普通用戶的身份進行數據維護和故障排除。",

		"bindDnUser": "綁定管理用戶",
		"bindDnUserInfo": "綁定一個具有管理權限的用戶，用於查詢認證，例如：cn=root",
		"bindDnPwd": "管理用戶密碼",
		"bindDnPwdInfo": "綁定管理員的密碼",
		"ldapEnabledError": "請完整配置所有LDAP參數後，再啟用LDAP認證。"

    },
    "_ssoConfig": {
        "ssoConfig": "認證密鑰配置",
		"ssoConfigInfo": "您可以為多個系統創建認證，用於SSO登錄和服務調用。",
		"ssoConfigInfo2": "每個認證需要提供認證名稱和密鑰，此密鑰即是用於生成訪問票據的加解密公鑰。",
		"addSSOConfig": "添加認證配置",
		"editSSOConfig": "編輯認證配置",
		"isEnable": "是否啟用",
		"ssoConfigName": "認證名稱",
		"ssoConfigKey": "密鑰",

		"ssoConfigKeyInfo": "密鑰長度應為8的倍數。",
		"ssoKeyLengthError": "請保持密鑰長度為8的倍數。",

		"removeSSOConfigTitle": "刪除認證配置確認",
		"removeSSOConfig": "您確定要刪除認證配置：“{name}” 嗎？",

		"ssoDataError": "認證名稱和認證密鑰不能為空。",
		"ssoSameNameError": "認證名稱 “{name}” 已存在，請使用其他名稱。",

        "useSSOConfig": "如何使用認證密鑰",
		"useSSOConfigInfo": "在兩種情況下需要使用認證密鑰：",
		"useSSOConfigInfo1": "1. 外部系統需要與O2OA實現單點登錄;",
		"useSSOConfigInfo2": "2. 外部系統需要調用O2OA平台的接口服務;",
		"useSSOConfigInfo3": "需要將認證的名稱和密鑰告知外部系統，外部系統使用3DES算法使用密鑰對<span style='color: blue'>\"person#timestamp\"</span>文本進行加密，以獲取訪問O2OA的臨時票據（token）。<br/>" +
			"<span style='color: blue'>person</span>: 表示指定用戶的用戶名、唯一編碼或員工號。（具體使用哪個要根據外部系統與O2OA的用戶關聯的字段）<br/>" +
			"<span style='color: blue'>timestamp</span>: 表示為1970年1月1日0時0分0秒到當前時間的毫秒數。（為了確保token的時效性,有效時間為1分鐘）<br/><br>" +
			"生成token後，外部系統可以直接通過訪問以下地址，實現與O2OA的單點認證：<br/>" +
			"http://servername/x_desktop/sso.html?client={<span style='color: blue'>client</span>}&xtoken={<span style='color: blue'>token</span>}&redirect={<span style='color: blue'>redirect</span>}<br/>" +
			"<span style='color: blue'>client</span>表示使用的認證名稱；<br/>" +
			"<span style='color: blue'>token</span>表示產生的臨時票據token；<br/>" +
			"<span style='color: blue'>redirect</span>表示認證成功後要跳轉到的地址；<br/>",
		"useSSOConfigInfo4": "有關認證配置的詳細信息，<a target='_blank' href='https://www.o2oa.net/search.html?q=%E9%89%B4%E6%9D%83'>請點擊此處查看</a>。",

        "ssoTokenTools": "相關工具",
		"ssoTokenCode": "查看加密範例代碼",
		"ssoTokenCheck": "驗證token有效性",

		"oauthConfig": "OAuth配置",
		"oauthClientConfig": "OAuth客戶端配置",
		"oauthServerConfig": "OAuth服務端配置",

		"oauthClientConfigInfo": "如果將O2OA平台作為OAuth2認證伺服器，您可以在此可以配置多個OAuth客戶端，為其他系統實現登錄授權",
		"oauthServerConfigInfo": "如果您已有OAuth2認證服務端，您可以在此配置多個OAuth服務端，為本系統實現登錄授權",

		"addOauthClientConfig": "添加OAuth客戶端配置",
		"addOauthServerConfig": "添加OAuth服務端配置",
		"editOauthClientConfig": "編輯OAuth客戶端",
		"editOauthServerConfig": "編輯OAuth服務端",

		"removeOauthConfigTitle": "刪除OAuth配置確認",
		"removeOauthConfig": "您確定要刪除OAuth配置：“{name}” 嗎？",

		"oauthClientDataError": "客戶端ID(ClientId)和客戶端密鑰(ClientSecret)不能為空。",
		"oauthClientSameNameError": "客戶端ID(ClientId) “{name}”已存在。請使用其他客戶端ID。",

        "oauth_clientId": "客戶號",
		"oauth_clientSecret": "客戶密鑰",
		"oauth_mapping": "返回映射",
		"oauth_name": "名稱",
		"oauth_displayName": "顯示名稱",
		"oauth_icon": "圖標URL",
		"oauth_authAddress": "請求金鑰地址",
		"oauth_authParameter": "請求金鑰參數",
		"oauth_authMethod": "請求金鑰方法",

		"oauth_tokenAddress": "請求令牌地址",
		"oauth_tokenParameter": "請求令牌參數",
		"oauth_tokenMethod": "請求令牌方法",
		"oauth_tokenType": "令牌格式",

		"oauth_infoAddress": "請求信息地址",
		"oauth_infoParameter": "請求信息參數",
		"oauth_infoMethod": "請求信息方法",
		"oauth_infoType": "信息格式",

		"oauth_infoCredentialField": "個人信息字段",
		"oauth_bindingField": "綁定用戶字段",

		"oauth_infoScriptText": "信息處理腳本",

		"infoScriptTextInfo": "當信息格式不是JSON，也不是FORM時，您可以使用腳本，將信息格式化為JSON對象，以便系統可以正確處理。在下面的腳本編輯器中編寫腳本，返回一個JSON對象，您可以使用 <span style='color: blue'>this.text</span> 獲取響應信息的原始文本。"

    },
    "_ternaryManagement": {
        "enable": "啟用三員管理",
		"enableInfo": "系統支援以系統管理員、安全管理員和安全審計員三員分責分權的方式進行系統安全管理，啟動三員管理後會解除xadmin用戶及權限同時啟用系統的審計日誌記錄（需重啟伺服器）<br>" +
			"三員各自角色分工分別是： " +
			"<ul><li>系統管理員（系統內建用戶：systemManager）：負責為系統用戶、組織管理和系統運行維護工作； </li>" +
			"<li>安全管理員（系統內建用戶：securityManager）：負責權限設定，負責系統審計日誌、用戶和系統管理員操作行為的審查分析； </li>" +
			"<li>安全審計員（系統內建用戶：auditManager）：負責對系統管理員、安全管理員的操作行為進行審計、跟蹤。 </li></ul>" +
			"應用定時每天1點分析前一天的操作日誌供三個管理員審計查詢。<br>" +
			"要完整使用三員管理功能，您還需要從應用市場安裝“三員管理”應用。" +
			"更多關於三員管理的內容可查看以下文檔和視頻：<a href='https://www.o2oa.net/search.html?q=%E4%B8%89%E5%91%98%E7%AE%A1%E7%90%86' target='_blank'>三員管理</a>",
		"logRetainDays": "日誌保留天數",
		"logRetainDaysInfo": "設置日誌最多保留的天數。",

		"logBodyEnable": "記錄Body內容",
		"logBodyEnableInfo": "記錄Body內容會得到更詳細的日誌信息，但也會大大增加磁盤空間佔用和伺服器開銷。"
    },
    "_databaseServer": {
        "databaseSource": "數據源配置",
		"entity": "實體類配置",
		"tools": "備份工具",
		"infoInner": "您正在使用O2OA內置數據庫，O2OA自帶的數據庫是一個內嵌式的內存數據庫，適合用於開發環境、功能演示環境，並不適合用作正式環境。 " +
			"如果作為正式環境使用，建議您使用擁有更高性能並且更加穩定的商用級數據庫。",
		"infoExternal": "您已經使用了擴展數據庫，O2OA內置數據庫已停用。",

		"info": "<span style='color: red'>修改數據庫配置在大部分情況下都會影響到系統現有數據，請慎重修改此處配置！</span>",
		"info2": "在修改數據庫配置之前，建議您先使用O2OA的備份功能（ctl -dd）將系統數據進行備份，在修改完數據庫配置後重啟服務器，然後將備份的數據恢復到數據庫（ctl -rd）。所有數據庫相關配置的修改，都需要重啟服務器",

		"innerDataSources": "內置數據庫",
		"externalDataSources": "擴展數據庫",
		"innerDataSourcesInfo": "O2OA自帶的數據庫是一個內嵌式的內存數據庫，適合用於開發環境、功能演示環境。",
		"externalDataSourcesInfo": "O2OA支持外部數據庫擴展，建議生產環境使用商用級別數據庫以保證數據安全和性能。",

		"addDatabaseConfig": "添加數據庫配置",

		"databaseUrl": "數據庫連接",
		"enable": "是否啟用",
		"username": "用戶名",
		"password": "密碼",

        "tcpPort": "連接埠",
		"tcpPortInfo": "數據庫 JDBC 連接埠，登錄的用戶名為 sa，密碼是 xadmin 的密碼。數據庫創建在 /o2server/local/repository/data/X.mv.db 中，一旦數據庫文件被創建，則該數據庫的密碼也被創建。",
		"webPort": "WEB 埠",
		"webPortInfo": "H2提供了一個 Web 客戶端，此埠用於 Web 客戶端的訪問，用戶名為 sa，密碼是 xadmin 數據庫的初始密碼。",
		"jmxEnable": "啟用 JMX",
		"jmxEnableInfo": "如果啟用，可以通過本地的 JMX 客戶端進行訪問，不支持遠程 JMX 客戶端。",
		"cacheSize": "緩存大小",
		"cacheSizeInfo": "H2 數據庫的緩存大小，以 M 為單位設置 H2 用於緩存的內存大小，默認為 512M。",
		"logLevel": "日誌級別",
		"maxTotal": "最大使用連接數",
		"maxIdle": "最大空閑連接數",
		"statEnable": "啟用統計",
		"statFilter": "統計篩選",
		"slowSqlMillis": "慢 SQL 毫秒數",
		"slowSqlMillisInfo": "執行緩慢的 SQL 將被單獨記錄的時間（默認值：2000 毫秒）",
		"lockTimeout": "鎖超時時間（毫秒）",

        "inputDatabaseUrl": "請填寫數據庫連接信息",

		"entityConfig": "實體類存儲分配",
		"entityConfigInfo": "如果您啟用了多數據庫，您可以在此分配系統中實體類存儲的數據庫，以提高性能。<span style='color: red'>您必須確保為所有實體類都分配了對應的存儲數據庫。</span>",

		"oneDatabase": "要為系統中實體類分配存儲數據庫，您必須要啟用兩個或以上的數據庫，您現在只有一個數據庫已啟用。",
		"oneDatabaseInfo": "要為系統中實體類分配存儲數據庫，您必須要啟用兩個或以上的數據庫。",

		"includeEntity": "允許的實體類",
		"includeEntityInfo": "此數據庫允許存儲的實體類，為空表示全部，多個用逗號或換行分割",
		"excludeEntity": "排除的實體類",
		"excludeEntityInfo": "此數據庫禁止存儲的類別，為空表示不禁止任何類別，多個用逗號或換行分割",

		"editDatabase": "編輯數據庫配置",


        "saveDatabaseConfig": "保存所有數據庫配置",
		"saveDatabaseConfigInfo": "本頁中的配置在修改後不會立即保存，您必須點擊此按鈕後，您修改的配置才會被保存",
		"saveDatabaseConfirm": "您即將保存數據庫配置<br><span style='color:red'>這有可能會影響到系統現有數據（包括業務數據和設計數據）</span><br><br>您是否確定要保存數據庫配置？",

		"reloadDatabaseConfig": "恢復所有數據庫配置",
		"reloadDatabaseConfigInfo": "如果您想廢棄本頁中未保存的修改，可以點擊此按鈕，以重新載入配置",
		"reloadDatabaseConfirm": "此操作將重新載入數據庫配置，未保存的修改將會丟失，您是否確定恢復數據庫配置？",

		"saveEntityConfig": "保存實體類配置",
		"saveEntityConfirm": "您即將保存實體類配置<br><span style='color:red'>這有可能會影響到系統現有數據（包括業務數據和設計數據）</span><br><br>您是否確定要保存實體類配置？",
		"reloadEntityConfig": "恢復實體類配置",
		"reloadEntityConfirm": "此操作將重新載入實體類配置，未保存的修改將會丟失，您是否確定恢復實體類配置？",

        "entityList": "可選列表",
		"selectedEntityList": "已選列表",
		"findClass": "查找類別",

		"removeDatabaseConfigTitle": "刪除數據庫配置確認",
		"removeDatabaseConfig": "<span style='color: red'>注意：您即將刪除數據庫配置：“{name}”，請務必在刪除數據庫之前，備份系統數據。</span><br><br>您確定要執行此操作嗎？",

		"saveDatabaseConfigSuccess": "數據庫配置保存成功，請重新啟動伺服器",
		"saveEntityConfigSuccess": "實體類配置保存成功，請重新啟動伺服器",

		"dumpRestoreTools": "數據庫備份還原工具",
		"toolsInfo": "O2OA提供了數據備份和還原工具，<span style='color: red'>修改數據庫配置在大部分情況下都會影響到系統現有數據</span>，" +
			"所以在修改數據庫配置之前，建議您先使用O2OA的備份功能將系統數據進行備份，在修改完數據庫配置後重新啟動伺服器，然後將備份的數據還原到數據庫。<br>" +
			"<span class='mainColor_color'>在您進行備份或還原數據時，請勿離開本頁面。您可以在另一個瀏覽器窗口中進行其他操作。</span>",

        "dumpTools": "備份數據",
		"dumpToolsInfo": "點擊此按鈕進行數據備份，<span style='color: red'>請勿在系統頻繁讀寫數據期間進行備份</span>",
		"dumpWaitLog": "數據備份未進行",
		"dumpErrorLog": "數據備份發生錯誤",

		"dumpBegin": "開始備份確認",
		"dumpBeginInfo": "數據備份可能會影響伺服器性能，您確定要開始數據備份嗎？",

		"dumpCheckButton": "檢查備份狀態",
		"dumpCheck": "檢查備份狀態中...",
		"dumpStop": "數據備份未進行",
		"dumpRunning": "數據備份進行中...",
		"dumpEnd": "數據備份已完成",

        "restoreTools": "數據恢復",
		"restoreToolsInfo": "點擊此按鈕進行數據恢復，<span style='color: red'>請勿在系統頻繁讀寫數據期間進行恢復</span>",
		"restoreToolsInfo2": "如果您的系統中包含數據表，數據恢復完成後，請進入數據中心編譯所有數據表，然後再執行一次數據恢復，然後重啟伺服器",
		"restoreWaitLog": "數據恢復未進行",
		"restoreErrorLog": "數據恢復發生錯誤",

		"restoreBegin": "恢復開始確認",
		"restoreBeginInfo": "數據恢復可能會影響伺服器性能，您確定要開始數據恢復嗎？",

		"restoreCheckButton": "檢查恢復狀態",
		"restoreCheck": "檢查恢復狀態中...",
		"restoreStop": "數據恢復未進行",
		"restoreRunning": "數據恢復進行中...",
		"restoreEnd": "數據恢復已完成"


    },
    "_cloudConfig": {
        "info": "O2云服務提供了應用市場、移動辦公定位、短信服務、文件轉換等眾多增值服務，您只需登錄到O2云服務器，即可使用。",
		"recheck": "重新檢查連線",

		"notValidatedInfo": "登錄到O2云，您即可訪問應用市場，連接移動辦公APP，以及短信服務、文件轉換等眾多功能！",
		"disconnectInfo": "您的伺服器無法連接到O2云，請檢查您的伺服器網絡環境。",
		"validatedInfo": "<span style='color: #ff0000'>您好：</span>{name}，您已經登錄到O2云，可使用包括移動辦公在內的所有O2平台功能！",

		"connected": "您已經可以連接到O2云了！",
		"disconnect": "您的伺服器無法連接到O2云！",
		"notValidated": "您還未登錄到O2云！",
		"validated": "您已經登錄到O2云了！",

		"loginInfo": "如果您已有O2云帳號，請點擊此處登錄：",
		"loginButtonText": "登錄到O2云",
		"registerInfo": "如果您沒有O2云帳號，請點擊此處註冊：",
		"registerButtonText": "註冊O2云帳號",
		"forgotPasswordInfo": "如果您忘記了O2云帳號的密碼，請點擊此處重置：",
		"forgotPasswordButtonText": "重置O2云密碼",

        "collectUsername": "O2雲帳號",
		"collectPassword": "O2雲密碼",
		"collectMobile": "手機號碼",
		"collectMail": "郵箱地址",
		"collectCode": "驗證碼",
		"collectConfirm": "確認密碼",
		"getCode": "獲取驗證碼",
		"regetCode": "重新獲取",

		"inputCollectUsername": "請輸入O2雲帳號",
		"inputCollectPassword": "請輸入O2雲帳號密碼",
		"inputCollectMobile": "請輸入手機號碼",
		"inputCollectMail": "請輸入郵箱地址",
		"inputCollectCode": "請輸入簡訊驗證碼",
		"inputCollectConfirm": "請輸入確認密碼",
		"collectUsernameExist": "O2雲帳戶名稱已存在",
		"collectUsernameNotExist": "O2雲帳戶名稱不存在",
		"passwordDisagree": "密碼確認不一致",
		"mobileError": "手機號碼輸入不正確",
		"mailError": "郵箱地址輸入不正確",

        "registerCollect": "註冊O2雲帳號",
		"forgotPassword": "忘記密碼",
		"loginError": "登錄O2雲失敗，請檢查帳戶名稱和密碼",
		"registerError": "註冊O2雲帳號出錯，請聯繫技術支持",
		"deleteError": "刪除O2雲帳號出錯，請聯繫技術支持",
		"resetPasswordError": "修改O2雲帳號密碼出錯，請聯繫技術支持",

		"deleteCollectUnit": "刪除O2雲帳號",
		"deleteCollectUnitInfo": "即將刪除O2雲帳號：{name}，請輸入手機號碼，然後獲取驗證碼以確認",

		"resetPasswordCollect": "修改O2雲帳號密碼",

		"modifyCollect": "修改帳號",
		"logoutCollect": "斷開連接",
		"modifyCollectPassword": "修改密碼",
		"deleteCollect": "刪除帳號",
		"reloginCollect": "重新登錄"
    },
    "_serversConfig": {
        "serverInfo": "伺服器資訊",
		"baseConfig": "基本設定",
		"environmentConfig": "環境變數設定",
		"sameConfig": "使用相同的伺服器配置",
		"sameConfigInfo": "O2OA有三個邏輯伺服器：中心服務、應用服務和WEB服務，默认情况下它们使用同一个端口和同一套配置，您也可以为三个服务分开配置不同的端口、主机等信息。",

		"serverConfig": "伺服器配置",
		"serverConfigInfo": "在此配置伺服器相關參數（需要重新啟動伺服器）",

		"serverPort": "服務埠",
		"serverPortInfo": "伺服器監聽埠",

		"serverProxyHost": "訪問主機名",
		"serverProxyPort": "訪問主機埠",
		"sslEnable": "是否啟用SSL",
		"httpProtocol": "WEB訪問協議",
		"sslKeyStorePassword": "SSL密碼",
		"sslKeyManagerPassword": "SSL管理密碼",
		"sslInfo": "<span>啟用SSL，您需要將已申請的證書文件複製到O2OA伺服器端的config目錄下，並改名成`keystore`，集群環境需要在每台伺服器存放證書文件。（需要重新啟動伺服器）</span>",

		"saveServerConfig": "儲存伺服器配置",
		"saveServerConfigSuccess": "伺服器配置儲存成功",
		"saveServerConfigPortError": "中心服務、應用服務和WEB服務的埠必須全部相同或全部不同",

		"saveServerSSLConfig": "儲存SSL配置",
		"saveServerSSLConfigSuccess": "SSL配置儲存成功",

        "sslConfig": "是否啟用SSL",

		"serverTaskConfig": "伺服器任務",

		"proxyCenterEnable": "代理中心服務",
		"proxyApplicationEnable": "代理應用服務",
		"proxyTimeOut": "代理超時（秒）",

		"includes": "啟用的應用模塊",
		"includesInfo": "您可以在此處選擇伺服器允許運行的應用模塊，只有在此處配置的應用模塊才會啟動，這可以使得集群環境中更加靈活地分配伺服器性能。但請謹慎修改此配置，如果配置不當，可能會導致服務異常（需要重新啟動伺服器）",
		"includesInfo2": "<b style='color: #666666'>選擇要啟用的內置應用：</b> 如果您沒有選擇任何模塊，則表示所有模塊都會啟用",
		"includesInfo3": "<b style='color: #666666'>要啟用的自定義應用：</b> 在下面的輸入框中輸入自定義應用名稱，用半角逗號分隔",

		"saveIncludes": "保存啟用應用模塊配置",
		"saveExcludes": "保存禁用應用模塊配置",

        "excludes": "禁用的應用模塊",
		"excludesInfo": "您可以在此處選擇伺服器禁止運行的應用模塊，此處配置的應用模塊將不會啟動，這可以使得集群環境中更加靈活地分配伺服器性能。但請謹慎修改此配置，如果配置不當，可能會導致服務異常（需要重新啟動伺服器）",
		"excludesInfo2": "<b style='color: #666666'>禁用的內置應用：</b>如果您沒有選擇任何模塊，則表示不禁用任何模塊",
		"excludesInfo3": "<b style='color: #666666'>要禁用的自定義應用：</b>在下面的輸入框中輸入自定義應用名稱，用半角逗號分隔",

		"includesAll": "啟用所有模塊",
		"includesSelect": "選擇要啟用的模塊",
		"includesModules": "已啟用模塊",
		"selectModules": "可選擇模塊",

		"excludesNone": "不禁用任何模塊",
		"excludesSelect": "選擇要禁用的模塊",

		"saveServerIncludesSuccess": "保存啟用應用模塊成功",
		"saveServerExcludesSuccess": "保存禁用應用模塊成功",

        "requestLogEnable": "啟用HTTP日誌",
		"requestLogBodyEnable": "記錄Body內容",
		"requestLogRetainDays": "日誌保留天數",
		"requestLogInfo": "在此處配置伺服器HTTP日誌相關內容（需要重啟伺服器）：" +
			"<ul><li>啟用HTTP日誌後，日誌文件將保存在伺服器logs目錄下。（啟用三員管理的情況下，HTTP日誌始終會啟用）</li>" +
			"<li>記錄Body內容會得到更詳細的日誌信息，但也會大大增加磁盤空間占用和伺服器開銷。</li>" +
			"<li>設置日誌最多保留的天數，超過此天數的日誌文件將被刪除。</li></ul>",
		
		"webSocketEnable": "是否啟用WebSocket",
		"webSocketEnableInfo": "WebSocket用於伺服器給WEB用戶的消息提醒和聊天等功能，如果啟用了WebSocket，請正確配置nginx、WAF等網路系統，以確保允許WebSocket協議通訊。（需要重啟伺服器）",

		"deployWarEnable": "是否允許前端部署自定義應用",
		"deployWarEnableInfo": "此配置控制自定義應用（war）是否允許在WEB端上傳部署（需要重啟伺服器）",

		"deployResourceEnable": "是否允許前端部署Web資源",
		"deployResourceEnableInfo": "此配置控制前端元件和靜態資源，是否允許在WEB端上傳部署（需要重啟伺服器）",

        "statEnable": "啟用Druid統計",
		"statExclusions": "統計忽略路徑",
		"statEnableInfo": "是否啟用Druid統計數據庫連接、SQL執行、HTTP請求等相關信息，您可以通過以下URL訪問統計結果頁面：<a href='{url}' target='_blank'>Druid監控</a>。",
		
		"exposeJest": "是否輸出Restful API文檔頁面",
		"exposeJestInfo": "輸出Restful API文檔頁面嗎？ API文檔可以通過以下URL訪問：<a href='{url}' target='_blank'>Restful API</a>。",
		
		"scriptingBlockedClasses": "服務器腳本禁用的Java類",
		"scriptingBlockedClassesInfo": "在此設置不允許在服務器腳本中使用的Java類，用逗號分隔。",
		
		"httpWhiteList": "外部http接口服務地址白名單",
		"httpWhiteListInfo": "外部http接口服務地址白名單，*代表不限制，用半角逗號分隔。",
		
		"refererHeadCheckRegular": "請求Referer驗證",
		"refererHeadCheckRegularInfo": "在此處可配置伺服器對於請求的Referer頭的校驗規則，配置一個正則表達式，通過正則表達式校驗Referer值的請求才被允許。合理配置此項可有效防止CSRF攻擊。如配置 (.+?)o2oa.net(.+?) 僅允許referer包含“o2oa.net”的請求。",

        "contentSecurityPolicy": "Content-Security-Policy 响應頭",
		"contentSecurityPolicyInfo": "HTTP 响應頭 Content-Security-Policy 允許站點管理員控制用戶代理能夠為指定的頁面加載哪些資源。除了少數例外情況，設定的政策主要涉及指定伺服器的來源和腳本結束點。這將有助於防止跨站腳本攻擊（Cross-Site Script）。",
		"contentSecurityPolicyInfo2": "有關 Content-Security-Policy 响應頭的更多信息，請參閱：<a target='_blank' href='https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy'>https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy</a>",
		
		"accessControlAllowOrigin": "跨域來源許可",
		"accessControlAllowOriginInfo": "跨源資源共享許可，設置 HTTP 返回的 Access-Control-Allow-Origin 標識，可用於防止 CORS 攻擊，例如：https://www.o2oa.net",
		
		"personUnitOrderByAsc": "人員組織升序",
		"personUnitOrderByAscInfo": "在檢索人員組織數據時，是否使用升序排序，默认為 true，否則為降序排序。",
		
		"attachmentConfig": "附件上傳配置",
		"attachmentConfigInfo": "在此處，您可以配置允許上傳附件的系統中的大小和類型。",

        "fileSize": "附件大小限制",
		"fileSizeInfo": "以M為單位，最大2048M",
		"fileTypeIncludes": "允許上傳的附件類型",
		"fileTypeIncludesInfo": "設置允許上傳的附件類型，設置擴展名，用半角逗號分隔",
		"fileTypeExcludes": "禁止上傳的附件類型",
		"fileTypeExcludesInfo": "設置禁止上傳的附件類型，設置擴展名，用半角逗號分隔",
		"dumpData": "自動備份數據",
		"dumpDataInfo": "O2OA支持定時自動備份數據，請在此處配置",
		"dumpEnable": "是否啟用",
		"dumpCron": "定時表達式",
		"dumpSize": "最大備份數量",
		"dumpPath": "備份路徑",
		"saveDump": "保存自動備份配置",
		"saveDumpSuccess": "保存自動備份配置成功",
		"restoreData": "自動恢復數據",
		"restoreDataInfo": "O2OA支持定時自動恢復數據，請在此處配置",
		"restoreEnable": "是否啟用",
		"restoreCron": "定時表達式",
		"restorePath": "恢復路徑",
		"saveRestore": "保存自動恢復配置",
		"saveRestoreSuccess": "保存自動恢復配置成功",
		"reloadServerConfig": "重新載入伺服器配置"
    },
    "_worktimeConfig": {
		"amWorktime": "上午工作時間",
		"pmWorktime": "下午工作時間",
		"holidays": "節假日",
		"workdays": "工作日",
		"weekends": "周末",

		"amWorktimeInfo": "在此處設定工作日的上午工作時間範圍",
		"pmWorktimeInfo": "在此處設定工作日的下午工作時間範圍",
		"holidaysInfo": "設定節假日，將原本為工作日的日期添加到此處，作為節假日",
		"workdaysInfo": "設定工作日，將原本為非工作日的日期添加到此處，作為工作日",
		"weekendsInfo": "設定周末，選擇下面的周幾作為周末非工作日",

		"timeRangeTo": "至",
		"startTime": "開始時間",
		"endTime": "結束時間",

		"weekData": {
			"週一": 2,
			"週二": 3,
			"週三": 4,
			"週四": 5,
			"週五": 6,
			"週六": 7,
			"週日": 1
		}
	},
    "_cacheConfig": {
		"type": "快取類型",
		"typeInfo": "O2OA 系統支援 guava 和 redis 兩種快取，默認使用 guava。",

		"guava_maximumSize": "快取最大容量",
		"guava_maximumSizeInfo": "快取最大容量，對象數量，默認值: 3000",
		"guava_expireMinutes": "過期時間",
		"guava_expireMinutesInfo": "過期時間，單位分鐘，默認值: 30",

		"redis": "redis 服務配置",
		"redisInfo": "在此配置 redis 服務",
		"redis_host": "伺服器地址",
		"redis_port": "伺服器端口",
		"redis_user": "認證使用者",
		"redis_password": "認證口令",
		"redis_connectionTimeout": "連接等待超時",
		"redis_socketTimeout": "返回等待超時",
		"redis_sslEnable": "啟用 SSL",
		"redis_index": "資料庫編號",

		"saveRedis": "保存 redis 配置",
		"saveRedisSuccess": "保存 redis 配置成功",
	},
    "_processConfig": {
        "baseConfig": "基本配置",
		"timerConfig": "定時器配置",

		"maintenanceIdentity": "流程維護人身份",
		"selectMaintenanceIdentity": "選擇流程維護人身份",
		"maintenanceIdentityInfo": "當流程工作發生意外錯誤，無法找到對應的處理人情況下，系統先嘗試將工作分配給創建者身份，如果創建身份也不可獲取，那麼就分配給此處設定的身份",

		"formVersionCount": "表單歷史版本保留數量",
		"formVersionCountInfo": "表單每次保存時，系統可以保留一個副本作為歷史版本，以便在一些特殊情況下找回以前的設計。此處配置表單歷史版本最多保留的數量，超過此數量的話最早的歷史版本會被刪除",

		"processVersionCount": "流程歷史版本保留數量",
		"processVersionCountInfo": "流程每次保存時，系統可以保留一個副本作為歷史版本，以便在一些特殊情況下找回以前的設計。此處配置流程歷史版本最多保留的數量，超過此數量的話最早的歷史版本會被刪除",

		"scriptVersionCount": "腳本歷史版本保留數量",
		"scriptVersionCountInfo": "腳本每次保存時，系統可以保留一個副本作為歷史版本，以便在一些特殊情況下找回以前的設計。此處配置腳本歷史版本最多保留的數量，超過此數量的話最早的歷史版本會被刪除",

		"docToWordType": "公文編輯器組件轉換WORD方式",
		"docToWordTypeInfo": "公文編輯器組件在配置了轉換WORD方式為“Service”時，由後端服務進行WORD轉換。" +
			"O2OA系統支持本地服務轉換或者使用雲服務轉換，使用雲服務轉換能夠更好地兼容WORD格式，但您必須先連接到O2雲。請在“雲服務配置”中連接O2雲。",
		"docWordTypeSelect": {
			"local": "本地服務",
			"cloud": "雲服務"
		},

        "press": "工作提醒設定",
		"pressInfo": "流程設定中的人工活動節點可以設定允許發起提醒，使得處理過某個工作的人可以對此工作當前的待辦人發起辦理提醒。您可以在此處設定對於此行為在一個時間段內的次數限制。",
		"pressInfo1": "在",
		"pressInfo2": "分鐘內最多發起",
		"pressInfo3": "次提醒",

		"executorCount": "流轉執行器數量",
		"executorCountInfo": "處理流程流轉的執行器數量。默認32，一般不建議修改",

		"executorQueueBusyThreshold": "執行器隊列繁忙閾值",
		"executorQueueBusyThresholdInfo": "處理流程流轉的執行器隊列的繁忙閾值。默認5，一般不建議修改",

		"timerInfo": "O2OA流程平台需要一些定時器來處理流程任務，您可在此處對這些定時器進行配置。（所有對定時器的修改，都需要重啟伺服器才能生效）",

		"enable": "是否啟用",
		"cron": "定時表達式",
		"urge": "催辦定時器",
		"urgeInfo": "如果活動設定了超時時間，此定時器會檢查即將到達規定時間的待辦，並給其處理人發送催辦信息。",

        "expire": "超時定時器",
		"expireInfo": "如果活動設定了超時時間，此定時器會檢查待辦是否已經超過了規定時間，並將這些待辦標記為超時。",

		"touchDelay": "定時活動觸發定時器",
		"touchDelayInfo": "此定時器用於觸發流程中的定時活動。",

		"deleteDraft": "清除草稿定時器",
		"deleteDraftInfo": "流程中可以使用草稿模式創建流程實例，這種模式在保存前並沒有正式啟動流程，此定時器可以清除長期沒有進行流轉的草稿文件",

		"thresholdMinutes": "時間閾值（分）",
		"thresholdMinutesInfo": "設定閾值，單位分鐘，如果超過這個時間認為是可以刪除的草稿，默认為10天",

		"passExpired": "自動流轉定時器",
		"passExpiredInfo": "如果流程活動啟用了超時處理，此定時器會去流轉那些已經超時的待辦",

		"touchDetained": "滯留待辦檢查定時器",
		"touchDetainedInfo": "此定時器會查找長時間滯留的工作，並嘗試驅動此工作進行流轉，這可以自動處理由於人員變動等原因引起的工作滯留。",
		"thresholdMinutesInfo_touchDetained": "定時器會處理滯留時間超過這個閾值的工作，默认1440分鐘（1天）",

		"updateTable": "同步到數據表定時器",
		"updateTableInfo": "如果流程中設置了將流程數據映射到數據表，此定時器用於處理映射數據隊列",

		"archiveHadoop": "歸檔到Hadoop",
		"archiveHadoopInfo": "O2OA支持將已完成的工作數據歸檔到Hadoop，您可以在此處設置Hadoop相關配置",
		"fsDefaultFS": "Hadoop地址",
		"username": "Hadoop用戶名",
		"path": "路徑前綴",
		"saveHadoop": "保存Hadoop配置",
		"saveHadooping": "正在保存 ... ",
		"saveHadoopSuccess": "保存成功",

		"merge": "歸檔定時器",
		"mergeInfo": ""
    },
    "_queryConfig": {
        "queryIndexConfig": "索引配置",
		"workConfig": "在流轉文檔",
		"workCompletedConfig": "已完成文檔",
		"documentConfig": "內容管理文檔",
		"indexTools": "索引工具",

		"work": "在流轉",
		"workCompleted": "已完成",
		"document": "內容管理",

		"touchWorkIndex": "執行在流轉文檔全量索引",
		"touchWorkIndexInfo": "如果您首次啟用索引，或從舊版本升級，可以在系統閒置時立即觸發在流轉文檔的全量索引。",
		"touchWorkIndexAction": "立即執行在流轉文檔的全量索引",

        "touchWorkCompletedIndex": "執行已完成文檔全量索引",
		"touchWorkCompletedIndexInfo": "如果您首次啟用索引，或從舊版本升級，可以在系統閒置時立即觸發已完成文檔的全量索引。",
		"touchWorkCompletedIndexAction": "立即執行已完成文檔的全量索引",

		"touchDocumentIndex": "執行內容管理文檔全量索引",
		"touchDocumentIndexInfo": "如果您首次啟用索引，或從舊版本升級，可以在系統閒置時立即觸發內容管理文檔的全量索引。",
		"touchDocumentIndexAction": "立即執行內容管理文檔的全量索引",

		"optimizeIndex": "執行索引優化",
		"optimizeIndexInfo": "優化索引可壓縮索引存儲空間，優化索引結構，以提升檢索性能。執行索引優化需要較長時間，可在系統閒置時立即觸發索引優化。",
		"optimizeIndexAction": "立即執行索引優化",

        "indexActionConfirmTitle": "執行{type}全量索引確認",
		"indexActionConfirm": "全量索引會占用較多伺服器資源，可能會導致伺服器回應變慢，建議在系統閒置時執行。<br><br>您確認要執行{type}文檔的全量索引嗎？",
		"indexActionSuccess": "{type}全量索引任務已加入佇列，系統會立即執行！",

		"optimizeIndexConfirmTitle": "執行索引優化確認",
		"optimizeIndexConfirm": "執行索引優化會占用較多伺服器資源，可能會導致伺服器回應變慢，建議在系統閒置時執行。<br><br>您確認要執行索引優化嗎？",
		"optimizeIndexSuccess": "索引優化任務已加入佇列，系統會立即執行！",

		"restartServerInfo": "<span style='color: red'>關於索引配置的修改，將在重啟伺服器後生效！</span>",

		"enable": "是否啟用索引服務",

        "modeConfig": "索引存儲位置",
		"modeConfigInfo": "選擇索引存儲位置。默認為'本地文件系統'。",
		"indexMode": "索引存儲位置",
		"modeOptions": {
			"localDirectory": "本地文件系統",
			"hdfsDirectory": "Hadoop文件系統",
			"sharedDirectory": "共享文件系統"
		},
		"hdfsDirectoryDefaultFS": "Hadoop文件系統地址",
		"hdfsDirectoryPath": "Hadoop文件系統目錄",
		"sharedDirectoryPath": "共享文件系統目錄",

		"optimizeIndexEnable": "索引優化",
		"optimizeIndexEnableInfo": "啟用索引優化可壓縮索引存儲空間，優化索引結構，以提高檢索性能。",
		"optimizeIndexCron": "索引優化定時配置",
		"isEnable": "是否啟用",
		"cron": "定時表達式",

        "dataStringThreshold": "業務數據最大文本長度閾值",
		"dataStringThresholdInfo": "業務數據最大文本長度閾值。超過此閾值的數據將被忽略並不進行索引。",

		"summaryLength": "摘要長度",

		"attachmentMaxSize": "附件索引閾值",
		"attachmentMaxSizeInfo": "附件索引閾值（兆字節）。超過此值大小的附件將不進行索引。",

		"cleanupThresholdDays": "檢索內容清理閾值",
		"cleanupThresholdDaysInfo": "檢索內容清理閾值（天）。超過此天數未更新的索引將被清除。",

		"searchMaxPageSize": "搜索每頁最大數量",
		"searchMaxPageSizeInfo": "搜索返回結果每頁的最大數量",

		"moreLikeThisMaxSize": "相關推薦最大返回數量",
		"moreLikeThisMaxSizeInfo": "相關推薦檢索的最大返回數量",

		"workIndexAttachment": "是否對流轉中文件的附件進行索引",
		"workIndexAttachmentInfo": "是否對流轉中文件的附件進行索引。（對附件進行索引，根據不同的業務量，可能需要較強的伺服器性能和更大的記憶體）",

        "lowFreqWorkEnable": "是否啟用全量索引",
		"lowFreqWorkEnableInfo": "全量索引會更新在流轉文檔中的所有索引，以確保權限和數據的準確性。",
		"lowFreqWorkCron": "全量索引定時表達式",
		"lowFreqWorkCronInfo": "全量索引會占用較多的伺服器資源，如果啟用全量索引，建議設置只在系統空閒時段執行。需要注意的是，對於流轉中數據，已完成數據和內容管理數據的全量索引，請儘量分開在不同的時間段運行。",
		"lowFreqWorkMaxCount": "一次執行的全量索引最大文檔數量",
		"lowFreqWorkMaxCountInfo": "設置執行一次索引處理的最大文檔數量，達到此數量索引停止運行，下次運行索引時，會繼上次處理到文檔之後繼續執行。最大數量與處理時長兩個配置任意一個滿足就停止索引。",
		"lowFreqWorkMaxMinutes": "一次執行的全量索引處理時長(分鐘)",
		"lowFreqWorkMaxMinutesInfo": "設置執行一次索引處理的最大時長，達到此時長後索引停止運行，下次運行索引時，會繼上次處理到文檔之後繼續執行。最大數量與處理時長兩個配置任意一個滿足就停止索引。",
		
		"highFreqWorkEnable": "是否啟用增量索引",
		"highFreqWorkEnableInfo": "如果啟用增量索引，在文檔數據或狀態發生改變時，會發出信號，增量索引定時器會在指定的時間運行，獲取增量信號並更新文檔索引。",
		"highFreqWorkCron": "增量索引定時器",
		"highFreqWorkCronInfo": "增量索引定時執行表達式",
		"highFreqWorkMaxCount": "一次執行的增量索引最大處理量",
		"highFreqWorkMaxMinutes": "一次執行的增量索引處理最大時長(分鐘)",


        "workCompletedIndexAttachment": "是否對已完成文檔的附件進行索引",
		"workCompletedIndexAttachmentInfo": "是否對已完成文檔的附件進行索引。（對附件進行索引，根據不同的業務量，可能需要較強的伺服器性能和更大的記憶體）",
		
		"lowFreqWorkCompletedEnable": "是否啟用全量索引",
		"lowFreqWorkCompletedEnableInfo": "全量索引會更新已完成流轉文檔的所有索引，以確保權限和數據的準確性。",
		"lowFreqWorkCompletedCron": "全量索引定時表達式",
		"lowFreqWorkCompletedCronInfo": "全量索引會占用較多的伺服器資源，如果啟用全量索引，建議設置只在系統空閒時段執行。需要注意的是，對於流轉中數據，已完成數據和內容管理數據的全量索引，請儘量分開在不同的時間段運行。",
		"lowFreqWorkCompletedMaxCount": "一次執行的全量索引最大文檔數量",
		"lowFreqWorkCompletedMaxCountInfo": "設置執行一次索引處理的最大文檔數量，達到此數量索引停止運行，下次運行索引時，會繼上次處理到文檔之後繼續執行。最大數量與處理時長兩個配置任意一個满足就停止索引。",
		"lowFreqWorkCompletedMaxMinutes": "一次執行的全量索引處理時長(分鐘)",
		"lowFreqWorkCompletedMaxMinutesInfo": "設置執行一次索引處理的最大時長，達到此時長後索引停止運行，下次運行索引時，會繼上次處理到文檔之後繼續執行。最大數量與處理時長兩個配置任意一個满足就停止索引。",


        "highFreqWorkCompletedEnable": "是否啟用增量索引",
		"highFreqWorkCompletedEnableInfo": "如果啟用增量索引，在文檔數據或狀態發生改變時，會發出信號，增量索引定時器會在指定的時間運行，獲取增量信號並更新文檔索引。",
		"highFreqWorkCompletedCron": "增量索引定時器",
		"highFreqWorkCompletedCronInfo": "增量索引定期執行表達式。",
		"highFreqWorkCompletedMaxCount": "增量索引單次處理最大數量",
		"highFreqWorkCompletedMaxMinutes": "增量索引單次處理最大時長(分)",

		"documentIndexAttachment": "是否對內容管理文檔的附件進行索引",
		"documentIndexAttachmentInfo": "是否對內容管理文檔的附件進行索引。（對附件進行索引，根據不同的業務量，可能需要較強的伺服器性能和更大的記憶體。）",

        "lowFreqDocumentEnable": "是否啟用全量索引",
		"lowFreqDocumentEnableInfo": "全量索引會更新所有類型為“信息”的內容管理文檔的索引，以確保權限和數據的準確性。",
		"lowFreqDocumentCron": "全量索引定時器",
		"lowFreqDocumentCronInfo": "全量索引會佔用較多伺服器資源，如果啟用全量索引，建議設定只在系統閒置時執行。需要注意的是，根據文檔類型，全量索引，已完成數據和內容管理數據的全量索引，請儘量在不同的時間段執行。",
		"lowFreqDocumentMaxCount": "全量索引執行的最大數量",
		"lowFreqDocumentMaxCountInfo": "設定每次索引處理的最大文檔數量，達到此數量索引將停止運行，下次運行索引時，將從上次處理的文檔後繼續執行。最大數量和處理時間中的任何一個滿足就停止索引。",
		"lowFreqDocumentMaxMinutes": "全量索引執行處理時間（分鐘）",
		"lowFreqDocumentMaxMinutesInfo": "設定每次索引處理的最大時間，達到此時間索引將停止運行，下次運行索引時，將從上次處理的文檔後繼續執行。最大數量和處理時間中的任何一個滿足就停止索引。",

		"highFreqDocumentEnable": "是否啟用增量索引",
		"highFreqDocumentEnableInfo": "如果啟用增量索引，在文檔數據或狀態發生改變時，會發出信號，增量索引定時器會在指定的時間運行，獲取增量信號並更新文檔索引。",
		"highFreqDocumentCron": "增量索引定時器",
		"highFreqDocumentCronInfo": "增量索引定期執行的表達式。",
		"highFreqDocumentMaxCount": "增量索引單次處理最大數量",
		"highFreqDocumentMaxMinutes": "增量索引單次處理最大時間（分鐘）"

    },
    "_appConfig": {
        "connectConfig": "連接配置",
		"moduleConfig": "模塊配置",
		"iconConfig": "圖標配置",

		"cloudConnect": "雲服務連接檢查",
		"connectedInfo": "<span style='color:#5fbf78'>[已連接到O2雲服務]</span>",
		"notConnectedInfo": "<span style='color:red'>[尚未連接到O2雲服務]</span>，請轉至雲服務配置頁面進行註冊和登錄",

		"httpProtocol": "Web訪問協議",
		"httpProtocolInfo": "請選擇移動設備訪問中心服務時使用的HTTP協議還是HTTPS協議",

		"centerServer": "中心伺服器",
		"centerServerInfo": "中心伺服器對外服務的IP地址或域名和端口。",

		"webServer": "Web伺服器",
		"webServerInfo": "Web伺服器對外服務的IP地址或域名和端口，如果域名或IP地址為空或“127.0.0.1”，則使用中心伺服器地址。",

		"applicationServer": "應用伺服器",
		"applicationServerInfo": "應用伺服器對外服務的IP地址或域名和端口，如果域名或IP地址為空或“127.0.0.1”，則使用中心伺服器地址。",

		"editServer": "編輯伺服器地址",
		"host": "域名或IP地址",
		"port": "埠口",

        "connectTest": "手機連接測試",
		"connectTestInfo": "使用手機掃描二維碼，檢查外部網絡是否可以連接到服務器",
		"getQrcode": "生成連接測試二維碼",

		"mobileIndex": "移動端首頁配置",
		"mobileIndexInfo": "您可以配置移動端的首頁為默認應用程式風格，或指定一個門戶頁面",

		"simpleMode": "移動端簡易模式",
		"simpleModeInfo": "啟用移動端簡易模式後，僅顯示首頁和設置頁面",

		"appIndexPage": "移動端頁面配置",
		"appIndexPageInfo": "配置移動端的主要幾個頁面是否顯示",
		"appIndexPageHome": "首頁",
		"appIndexPageIM": "消息",
		"appIndexPageContact": "通訊錄",
		"appIndexPageApp": "應用程式",
		"appIndexPageSettings": "設置",

		"appIndexCenteredTitle": "移動App首頁是否居中",
		"appIndexCenteredInfo": "啟用移動App首頁居中，頁面數量將不可配置",

        "appIndexCmsFilterTitle": "首頁資訊中心",
		"appIndexCmsFilterCategoryInfo": "資訊中心列表分類查詢條件，為空即全部查詢",
		"appIndexTaskFilterTitle": "首頁辦公中心",
		"appIndexTaskFilterProcessInfo": "辦公中心列表流程查詢條件，為空即全部查詢",
		"appIndexTaskFilterProcessSelectorTitle": "流程選擇",
		"appIndexCmsFilterCategroySelectorTitle": "分類選擇",

		"systemMessageSwitch": "顯示系統通知",
		"systemMessageSwitchInfo": "移動應用程式消息列表中是否顯示系統通知",
		"systemMessageCanClickInfo": "移動應用程式系統通知是否可點擊打開",

		"contactPermissionView": "移動應用程式通訊錄權限視圖",
		"contactPermissionViewInfo": "需要在應用市場安裝“通訊錄”應用程序，該應用程序包含通訊錄權限配置視圖",

		"appExitAlert": "應用程式退出提示",
		"appExitAlertInfo": "應用程式退出時彈出窗口的提示語，為空即不彈窗",

		"nativeAppList": "應用程序列表",
		"nativeAppListInfo": "您可以在此設置移動應用程序中啟用哪些應用程序，禁用哪些應用程序",

        "imageNames": {
			"application_top": {"text": "應用程式頂部頁面圖片", "action": "ApplicationTop"},
			"index_bottom_menu_logo_blur": {"text": "主頁底部菜單圖標（未選中）", "action": "MenuLogoBlur"},
			"index_bottom_menu_logo_focus": {"text": "主頁底部菜單圖標（選中）", "action": "MenuLogoFocus"},
			"launch_logo": {"text": "啟動標誌圖片", "action": "LaunchLogo"},
			"login_avatar": {"text": "登錄界面默認頭像圖片", "action": "LoginAvatar"},
			"process_default": {"text": "流程默認圖標", "action": "ProcessDefault"},
			"setup_about_logo": {"text": "關於頁面圖標", "action": "SetupAboutLogo"}
		},
		"imageSzie": "尺寸",
		"changeImage": "更換圖片",
		"defaultImage": "默認圖片",
		"defaultImageTitle": "默認圖片確認",
		"defaultImageInfo": "您確定要將{name}替換為默認圖像嗎？",
    },
    "_integrationConfig": {
        "title": "移動端應用集成",
		"dingding": "釘釘集成",
		"mPweixin": "微信公眾號集成",
		"qiyeweixin": "企業微信集成",
		"weLink": "華為WeLink集成",
		"zhengwuDingding": "浙政釘釘集成",


        "enable": "是否啟用釘釘集成",
		"corpId": "釘釘CorpId",
		"agentId": "釘釘AgentId",
		"appKey": "應用唯一標識",
		"appSecret": "應用的密鑰",
		"syncCron": "同步檢查回調信號定時",
		"forceSyncCron": "強制同步定時",
		"oapiAddress": "釘釘API伺服器地址",
		"token": "回調Token",
		"encodingAesKey": "回調encodingAesKey",
		"workUrl": "釘釘消息打開工作的URL",
		"messageRedirectPortal": "處理完成後跳轉到門戶",
		"messageEnable": "是否啟用消息推送",
		"scanLoginEnable": "是否開啟釘釘掃碼登錄",
		"scanLoginAppId": "釘釘掃碼登錄的AppId",
		"scanLoginAppSecret": "釘釘掃碼登錄的appSecret",
		"attendanceSyncEnable": "是否啟用考勤信息",

        "enableInfo": "O2OA平台擁有配套的原生開發的安卓和IOS移動APP，可以以微應用的方式集成到阿里釘釘，同步釘釘的企業通訊錄作為本地組織人員架構，並且可以將待辦等通知直接推送到釘釘進行消息提醒。(需要重啟服務器)",
		"enableInfo2": "<span class='mainColor_color'>如果O2OA成功接入釘釘，O2OA將會自動從釘釘拉取所有的人員和組織進行同步，O2OA的所有人員和組織以企業釘釘中創建的組織架構為準（ 本地已經創建的人員和組織將保留不會被刪除，可能會造成人員和組織重復 ）</span>",
		"enableInfo3": "更多O2OA與釘釘集成的內容，請查看：<a href='https://www.o2oa.net/search.html?q=%E9%92%89%E9%92%89' target='_blank'>釘釘</a>",

		"syncCronInfo": "回調信號觸發同步檢查,默認每10分鐘運行一次,如果期間內有釘釘回調信號接收到,那麼觸發同步任務進行人員同步.(需要在釘釘設置回調配置)",
		"forceSyncCronInfo": "強制同步定時設置，默认在每天的8點和12點強制進行同步人員和組織",
		"oapiAddressInfo": "釘釘API服務器地址，一般不需要修改",
		"workUrlInfo": "釘釘消息打開工作的url地址，如：https://sample.o2oa.net/x_desktop/",
		"messageRedirectPortalInfo": "當釘釘消息處理完成後，可指定跳轉到特定的門戶頁面",

		"saveDingding": "保存釘釘配置",
		"saveDingdingSuccess": "釘釘配置保存成功",

        "mpweixinText": {
			"enable": "是否啟用",
			"enablePublish": "啟用菜單發佈",
			"appid": "微信Appid",
			"appSecret": "微信AppSecret",
			"token": "微信Token",
			"encodingAesKey": "微信encodingAesKey",
			"portalId": "處理完成後指定跳轉到特定的門戶",
			"workUrl": "微信公眾號消息打開工作的URL",
			"scriptId": "執行服務腳本",
			"messageEnable": "啟用模板消息",
			"tempMessageId": "公眾號模板消息ID",
			"fieldList": "模板字段配置",
			"tempName": "模板字段",
			"name": "業務字段",

			"workUrlInfo": "微信公眾號消息打開工作的URL地址，如：https://sample.o2oa.net/x_desktop/",
			"enableInfo": "O2OA支持微信公眾號的集成，用戶可以通過關注微信公眾號進行工作處理。並且支持待辦工作的消息提醒。(需要重啟服務器)",
			"enableInfo2": "更多O2OA與微信公眾號的內容，請查看：<a href='https://www.o2oa.net/search.html?q=%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7' target='_blank'>微信公眾號</a>",
			"enablePublishInfo": "啟用菜單發佈後，可已將在O2OA中配置好的菜單功能，發布到微信公眾號。可在APP工具-公眾號菜單配置中配置微信公眾號菜單",
			"portalIdInfo": "當消息處理完成後，可指定跳轉到特定的門戶頁面",
			"scriptIdInfo": "當從公眾號接收到文本消息時，可執行平台服務管理中的接口，在此處指定要執行的接口",
			"fieldListInfo": "這個是模板的內容中業務字段的對應關係，目前O2OA提供了這幾個業務字段 【creatorPerson:創建人, activityName:當前節點, processName:流程名稱, startTime:開始時間, title:標題】",

			"saveMpweixin": "保存微信公眾號配置",
			"saveMpweixinSuccess": "微信公眾號配置保存成功"
		},
        "qywenxinText": {
            "enable": "是否啟用",
			"corpId": "企業WeChat CorpId",
			"agentId": "企業WeChat AgentId",
			"corpSecret": "企業WeChat CorpSecret",
			"syncCron": "同步檢查回調信號定時",
			"forceSyncCron": "強制同步定時",
			"apiAddress": "API服務地址",
			"qrConnectAddress": "掃碼登錄服務地址",
			"oauth2Address": "oAuth2服務地址",
			"syncSecret": "通訊錄同步Secret",
			"token": "回調Token",
			"encodingAesKey": "回調EncodingAesKey",
			"workUrl": "消息打開工作的URL",
			"messageRedirectPortal": "處理完成後跳轉到門戶",
			"messageEnable": "是否啟用消息推送",
			"scanLoginEnable": "是否啟用掃碼登錄",
			"attendanceSyncEnable": "是否啟用考勤信息",
			"attendanceSyncAgentId": "考勤打卡應用ID",
			"attendanceSyncSecret": "考勤打卡應用Secret",
			"bindEnable": "是否啟用用戶綁定",
			"bindEnableInfo": "默認不要啟用，這是私有化綁定用戶用的，跟同步用戶組織是互斥的！",

            "getUserPrivateInfoMessageTitle": "企業WeChat獲取個人隱私信息的消息發送",
			"getUserPrivateInfoMessageDesc": "企業WeChat新版本同步API限制了用戶隱私信息（比如：手機號碼、郵箱等）的獲取，目前同步程序只能獲取到用戶姓名和userId。下面的消息發送功能是給用戶發送一個授權獲取隱私信息的消息，用戶點擊這個消息後，本程序就能讀取到需要的用戶信息！",
			"getUserPrivateInfoMessageConsumerList": "消息接收者",
			"getUserPrivateInfoMessageFormTitle": "消息標題",
			"getUserPrivateInfoMessageFormContent": "消息內容",
			"getUserPrivateInfoMessageFormTitleDefault": "【授權獲取個人信息】",
			"getUserPrivateInfoMessageFormContentDefault": "應用需要獲取您的個人信息，點擊授權！",
			"getUserPrivateInfoMessageConsumerEmpty": "請先選擇消息接收者！",
			"getUserPrivateInfoMessageFormTitleEmpty": "消息標題不能為空！",
			"getUserPrivateInfoMessageFormContentEmpty": "消息內容不能為空！",
			"getUserPrivateInfoMessageConfirmTitle": "提示",
			"getUserPrivateInfoMessageConfirmText": "確定要給所有選擇的用戶和組織下人員發送一條獲取隱私信息的企業WeChat消息？",
			"getUserPrivateInfoMessageSendBtn": "發送消息",
			"getUserPrivateInfoMessageSendSuccess": "發送消息成功，請稍後在企業WeChat中查收！",


            "syncCronInfo": "回調信號觸發同步檢查，默認每10分鐘運行一次，如果期間內有企業WeChat回調信號接收到，那麼觸發同步任務進行人員同步。（需要在企業WeChat設置回調配置）",
			"forceSyncCronInfo": "強制同步定時設置，默認在每天的8點和12點強制進行同步人員和組織",
			"apiAddressInfo": "企業WeChat API服務器地址，一般不需要修改",
			"workUrlInfo": "企業WeChat消息打開工作的url地址，如：https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "當企業WeChat消息處理完成後，可指定跳轉到特定的門戶頁面",

			"enableInfo": "O2OA支持以自建應用的方式集成到企業WeChat，同步企業WeChat的企業通訊錄作為本地組織人員架構，並且可以將待辦等通知直接推送到企業WeChat進行消息提醒。",
			"enableInfo2": "更多O2OA與企業WeChat的內容，請查看：<a href='https://www.o2oa.net/search.html?q=%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1' target='_blank'>企業WeChat</a>",

			"saveText": "保存企業WeChat配置",
			"saveSuccess": "企業WeChat配置保存成功"
        },
        "welinkText": {

            "enable": "是否啟用###",
			"clientId": "應用的ClientId",
			"clientSecret": "應用的ClientSecret###",
			"syncCron": "同步檢查回調信號定時",
			"forceSyncCron": "強制同步定時",
			"oapiAddress": "API服務地址",
			"messageEnable": "是否啟用消息推送",
			"workUrl": "消息打開工作的URL",
			"messageRedirectPortal": "處理完成後跳轉到門戶",

			"enableInfo": "O2OA支持以華為WeLink企業內部輕應用的方式集成，同步WeLink的通訊錄作為本地組織人員架構，並且可以將待辦等通知直接推送到WeLink進行消息提醒。(需要重啟伺服器)",
			"enableInfo2": "更多O2OA與WeLink的內容，請查看：<a href='https://www.o2oa.net/search.html?q=welink' target='_blank'>WeLink</a>",

			"syncCronInfo": "回調信號觸發同步檢查，默認每10分鐘運行一次，如果期間內有WeLink回調信號接收到，那麼觸發同步任務進行人員同步。（需要在WeLink設置回調配置）",
			"forceSyncCronInfo": "強制同步定時設置，默認在每天的8點和12點強制進行同步人員和組織",

			"workUrlInfo": "WeLink消息打開工作的URL地址，如：https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "當WeLink消息處理完成後，可指定跳轉到特定的門戶頁面",

			"saveText": "保存WeLink配置",
			"saveSuccess": "WeLink配置保存成功"
        }
    },
    "_storageServer": {
        "innerStorage": "內建儲存服務",
		"externalStorage": "擴展儲存服務",

		"info": "<span style='color: red'>修改儲存配置在大部分情況下都會影響到系統現有的文件儲存，請謹慎修改此處配置！</span>",
		"info2": "在修改儲存配置之前，建議您先使用O2OA的備份功能（ctl -dd）將系統數據進行備份，在修改完儲存配置後重啟伺服器，然後將備份的數據恢復（ctl -rd）。所有數據庫相關配置的修改都需要重啟伺服器",

		"saveStorageConfig": "保存所有儲存配置",
		"saveStorageConfigInfo": "本頁中的配置在修改後不會立即保存，您必須點擊此按鈕後，您修改的配置才會被保存",
		"saveStorageConfirm": "您即將保存儲存配置<br><span style='color:red'>這有可能會影響到系統現有文件儲存。</span><br><br>您是否確定要保存儲存配置？",

		"reloadStorageConfig": "恢復所有儲存配置",
		"reloadStorageConfigInfo": "如果您想廢棄本頁中未保存的修改，可以點擊此按鈕，以重新載入配置",
		"reloadStorageConfirm": "此操作將重新載入儲存配置，未保存的修改將會丟失，您是否確定恢復儲存配置？",

		"storageType": "儲存服務類型",
		"storageTypeInfo": "O2OA系統內建提供了文件儲存服務，您也可以根據需要採用外部擴展儲存節點。",
		"storageTypeData": [
			{"value": 'inner', "label": "內建", "text": "內建儲存服務"},
			{"value": 'external', "label": "外部", "text": "擴展儲存服務"}
		],

		"innerInnerInfo": "<span class='mainColor_color'>您正在使用內建文件儲存服務</span>，<span style='color:red'>請務必為每個儲存節點配置不同的名稱</span>",
		"innerExternalInfo": "<span class='mainColor_color'>您已啟用擴展文件儲存服務</span>，但您仍然可以修改內建文件儲存服務的配置，<span style='color:red'>請務必為每個儲存節點配置不同的名稱</span>",

		"innerStorageConfig": "內建儲存服務配置",

        "enable": "是否啟用",
		"port": "端口",
		"name": "名稱",
		"prefix": "前綴路徑",
		"deepPath": "使用深層路徑",
		"saveStorage": "保存存儲配置",
		"saveStorageSuccess": "存儲配置保存成功",

		"externalInnerInfo": "<span class='mainColor_color'>您正在使用內置文件存儲服務</span>，但您任然可以修改擴展文件存儲服務的配置",
		"externalExternalInfo": "<span class='mainColor_color'>您已啟用擴展文件存儲服務</span>",

		"enableExternal": "啟用擴展文件存儲",
		"disableExternal": "禁用擴展文件存儲",
		"enableExternalInfo": "如果要啟用擴展文件存儲，請確保擴展文件存儲配置已經完成，否則可能造成服務器運行異常。啟用或禁用擴展存儲服務都會影響到系統現有的文件存儲，強烈建議先備份系統數據。",

		"enableExternalTitle": "啟用擴展文件存儲確認",
		"enableExternalConfirm": "您即將啟用擴展文件存儲，同時會禁用內置文件存儲服務。<br><span style='color:red'>這會影響到系統現有已存儲的文件</span><br><br>您是否確定要啟用擴展文件存儲？",
		"disableExternalTitle": "禁用擴展文件存儲確認",
		"disableExternalConfirm": "您即將禁用擴展文件存儲，同時會啟用內置文件存儲服務。<br><span style='color:red'>這會影響到系統現有已存儲的文件</span><br><br>您是否確定要啟用擴展文件存儲？",

		"externalStorageNode": "擴展存儲節點配置",
		"addStorageNode": "添加存儲節點",
		"editStorageNode": "編輯存儲節點",
		"inputStorageNodeKey": "請輸入存儲節點標識",
		"inputStorageNodeName": "請輸入存儲節點名稱",

        "external": {
			"protocol": "協議",
			"username": "用戶名",
			"password": "密碼",
			"host": "主機",
			"port": "端口",
			"name": "名稱",
			"key": "節點識別符",
			"protocolData": {
				"webdav": "WebDAV",
				"sftp": "SFTP",
				"ftps": "FTPS",
				"ftp": "FTP",
				"file": "文件",
				"hdfs": "HDFS",
				"cifs": "CIFS",
				"ali": "阿里雲存儲",
				"s3": "亞馬遜雲存儲",
				"min": "MinIO存儲"
			},
			"protocolDataInfo": {
				"ali": "如果您尚未在應用市場安裝阿里雲OSS集成插件，請先安裝。",
				"min": "如果您尚未在應用市場安裝MinIO雲存儲集成插件，請先安裝。"
			}
		},
        "removeNodeConfigTitle": "刪除存儲節點確認",
		"removeNodeConfig": "即將刪除存儲節點“{name}”，此操作可能會影響到系統中已存儲的文件。<br>您確定要刪除存儲節點“{name}”嗎？",

		"assignNode": "存儲節點分配",
		"assignNodeInfo": "O2OA中存在以下多種類型的文件，您可以為這些文件分配存儲節點，一種類型的文件可分配多個節點。",
		"files": {
			"file": "網盤文件（file）",
			"processPlatform": "流程平台文件（processPlatform）",
			"mind": "腦圖文件（mind）",
			"meeting": "會議管理文件（meeting）",
			"calendar": "日程安排文件（calendar）",
			"cms": "內容管理文件（cms）",
			"bbs": "論壇文件（bbs）",
			"teamwork": "工作管理文件（strategyDeploy）",
			"structure": "應用管理（structure）",
			"im": "聊聊文件（im）",
			"general": "其他通用文件（general）",
			"custom": "自定義應用文件（custom）"
		},

		"store": "存儲節點",

		"noStoreNode": "未分配存儲節點",
		"addStore": "添加存儲節點",
		"saveStore": "保存"

    },
    "_appTools": {
        "onlineBuild": "APP在線打包",
		"mpweixinMenu": "公眾號菜單配置",

		"onlineBuildInfo": " <ul style='padding: 0'><li>當前移動App在線打包功能僅支持Android端。</li>" +
			"<li>需要在線打包，必須先到[雲服務配置]中進行註冊、登錄。</li>" +
			"<li>提交信息後，會顯示當前打包狀態，打包過程耗時較長，你可以先離開當前頁面，等待打包完成後來本頁面下載APK文件。</li></ul>",

		"onlineBuildInfo1": "<span class='mainColor_color'>我們在應用市場提供了更優秀的“App在線打包”應用，您可以到應用市場查看獲取</span>",

        "appPack": {
            "formSubmitBtnTitle": "提交並開始打包",
			"formReinputBtnTitle": "重新填寫表單並打包",
			"formRePackBtnTitle": "使用原有資料直接打包",
			"formDownloadApkBtnTitle": "下載APK文件",
			"formDownloadPublishBtnTitle": "下載發佈到本地",
			"refreshStatusBtnTitle": "刷新狀態",
			"formUploadLogoBtnTitle": "上傳圖片",

            "messageO2cloudNotEnable": "O2雲未啟用或無法連接！",
			"messageO2cloudNotLogin": "請先登錄O2雲！",
			"messageO2cloudLoginFail": "App打包服務器登錄失敗！",
			"statusOrderInline": "排隊中...",
			"statusPacking": "打包中...",
			"statusPackEnd": "打包完成",
			"statusPackError": "打包出錯",
			"publishStatusNone": "未發佈",
			"publishStatusDoing": "發佈中...",
			"publishStatusCompleted": "發佈完成，掃描登錄界面的二維碼即可安裝APP！",
			"publishStatusFail": "發佈失敗，請重試或聯繫管理員！",
			"messageSubmitNotAtStatus": "當前正在打包中，請稍後再試！",
			"messageAppnameNotEmpty": "App名稱不能為空！",
			"messageAppnameLenMax6": "App名稱不能超過6個字！",
			"messageAppLogoNotEmpty": "請重新上傳Logo圖片！",
			"messageAppLogoNeedPng": "Logo圖片必須為PNG格式！",
			"messagePortocolNotEmpty": "HTTP協議不能為空！",
			"messageHostNotEmpty": "中心伺服器域名不能為空！",
			"messageHostFormatError": "請填寫中心伺服器域名或IP，如www.o2oa.net，不要帶有'http'等前綴！",
			"messagePortNotEmpty": "中心伺服器端口號不能為空！",
			"messageContext_not_empty": "中心伺服器上下文不能為空！",
			"messagePortocolMustBeHttpHttps": "HTTP協議只能是 http 或 https ！",
			"messageAlertTitle": "確認提交",
			"messageAlertSubmit": "確定要提交嗎？當前表單信息將被打包成移動端App ？",

            "statusLabel": "當前狀態",
			"publishStatusLabel": "發佈狀態",
			"formAppName": "App名稱",
			"formAppNameTip": "App桌面顯示名稱，字數不超過6個",
			"formLogo": "Logo圖片",
			"formLogoTip": "App桌面顯示的Logo圖片，必須是png格式",
			"formProtocol": "HTTP協議",
			"formProtocolTip": "http / https",
			"formHost": "域名",
			"formHostTip": "中心伺服器域名或IP，如www.o2oa.net",
			"formPort": "端口號",
			"formPortTip": "中心伺服器端口號，如20030",
			"formContext": "上下文",
			"formContextTip": "中心伺服器上下文，如/x_program_center",
			"formUrlMapping": "代理urlMapping",
			"formUrlMappingTip": "伺服器外網使用代理地址的時候使用，如{ \"demo.o2oa.net:20020\": \"demo.o2oa.net/dev/app\" }",
			"formAppVersionName": "App版本名稱",
			"formAppVersionNameTip": "App的版本名稱，如v1.0.0。這個欄位預設不需要填寫！",
			"formAppBuildNo": "App版本編號",
			"formAppBuildNoTip": "App的版本編號，必須是正整數，如100。這個欄位預設不需要填寫！",
			"formEnableOuterPackage": "是否啟用外部包名",
			"formEnableOuterPackageTip": "啟用外部包名可以防止與官方發布的APP衝突覆蓋"
        },

        "mpMenu": {
            "mpweixinInfo": "⚠️ 微信公眾號菜單功能需要先啟用相關的配置文件[mpweixin.json]，並到微信的公眾號管理後台，開發模塊中啟用伺服器配置！",
			"mpweixin": "公眾號",
			"publishMpweixin": "發佈到微信公眾號",
			"publishToWxmp": "注意！當前操作會將所有保存的菜單數據覆蓋到微信公眾號，確定要繼續嗎？",
			"publishSuccess": "發佈成功，將在24小時後在手機端同步顯示！",
			"subscribeMpweixin": "關注回覆",
			"subscribeMpweixin_desc": "公眾號有新用戶關注時，自動發送的消息內容",
			"subscribeContentErrorEmpty": "回覆消息內容不能為空！",
			"subscribeMpweixin_save": "保存",
			"deleteMenuBtnTitle": "刪除菜單",
			"defaultNewName": "新增菜單",
			"formNameLabel": "菜單名稱",
			"formOrderLabel": "菜單排序號",
			"formRadioLabel": "菜單內容",
			"formRadioTypeMsg": "發送消息",
			"formRadioTypeUrl": "跳轉網頁",
			"formRadioTypeMiniprogram": "跳轉小程序",

            "formTypeMsgTips": "點擊該菜單將向用戶發送以下文字消息。未認證的訂閱號不支援文字消息。",
			"formTypeMsgLabel": "文字消息",
			"formTypeMsgErrorEmpty": "文字消息內容不能為空！",
			"formSubscribeContentErrorEmpty": "回覆消息內容不能為空！",
			"formTypeUrlTips": "點擊該菜單將跳轉到以下鏈接。",
			"formTypeUrlLabel": "頁面地址",
			"formTypeUrlErrorEmpty": "頁面地址不能為空！",
			"formTypeMiniprogramTips": "點擊該菜單將跳轉到以下小程序。",
			"formTypeMiniprogramAppidLabel": "小程序ID",
			"formTypeMiniprogramAppidPlaceholder": "小程序ID，請查看WeChat小程序管理後台。",
			"formTypeMiniprogramAppidErrorEmpty": "小程序ID不能為空！",
			"formTypeMiniprogramPathLabel": "小程序路徑",
			"formTypeMiniprogramPathPlaceholder": "小程序路徑，請查看WeChat小程序管理後台。",
			"formTypeMiniprogramPathErrorEmpty": "小程序路徑不能為空！",
			"formTypeMiniprogramUrlLabel": "備用網頁",
			"formTypeMiniprogramUrlPlaceholder": "備用網頁，舊版WeChat將打開此備用網頁。",
			"formTypeMiniprogramUrlErrorEmpty": "備用網頁不能為空！",
			"formNameTips4": "僅支援中文、英文和數字，字數不超過4個。",
			"formNameTips6": "僅支援中文、英文和數字，字數不超過6個。",
			"formOrderTips": "僅支援數字，字數不超過6個，排序按字符串排序。",
			"msgFirstMaxLen": "一級菜單最多只能創建3個！",
			"menuMsgSubMaxLen": "二級菜單最多只能創建5個！",
			"menuMsgParentNotSave": "上級菜單數據未保存，請先保存數據！",
			"menuDeleteAlertMsg": "確定要刪除這條數據嗎？將同時刪除其子菜單。",
			"menuDeleteSuccess": "刪除數據成功！",
			"menuSaveSuccess": "保存數據成功！",
			"formNameErrorEmpty": "菜單名稱不能為空！",
			"formNameErrorMaxLen4": "菜單名稱字數不能超過4個！",
			"formNameErrorMaxLen6": "菜單名稱字數不能超過6個！",
			"formNameError": "字數超過上限",
			"formOrderErrorEmpty": "菜單排序號不能為空！",
			"formOrderErrorNotNumber": "菜單排序號只能輸入數字！",
			"formOrderErrorMaxLen": "菜單排序號字數不能超過6個！"
        }
    },
    "_pushConfig": {
        "pushType": "消息推送服務",
		"pushTypeInfo": "O2OA支援JPush和Huawei Push服務，您可以根據需要選擇推送服務。",
		"pushTypeData": [
			{"value": "jpush", "label": "jpush", "text": "JPush推送服務"},
			{"value": "none", "label": "none", "text": "禁用消息推送"}
		],

		"appKey": "JPush應用鍵",
		"masterSecret": "JPush主秘密",
		"appKeyInfo": "JPush應用的應用鍵",
		"masterSecretInfo": "JPush應用的主秘密",

		"appId": "Huawei推送應用ID",
		"appSecret": "Huawei推送應用秘密",
		"appIdInfo": "Huawei推送應用的應用ID",
		"appSecretInfo": "Huawei推送應用的應用秘密"
    },
    "_messageConfig": {
        "messageConsumers": "通道配置",
		"messageType": "類型配置",
		"messageLoader": "載入器",
		"messageFilter": "篩選器",

		"consumerTypes": {
			"ws": "WebSocket",
			"pmsinner": "推送消息",
			"calendar": "日程",
			"dingding": "釘釘",
			"welink": "WeLink",
			"qiyeweixin": "企業微信",
			"mpweixin": "微信公眾號",
			"kafka": "Kafka",
			"activemq": "ActiveMQ",
			"restful": "Restful",
			"mail": "郵件",
			"jdbc": "JDBC",
			"table": "數據表",
			"hadoop": "Hadoop",
			"andfx": "移動辦公消息"
		},
        "consumerInfoTitle": "消息通道配置",
		"consumerInfo": "O2OA系統提供多種消息通道，您可以在此處設置各類消息需要通過什麼方式發送。",
		"consumerInfo2": "更多關於消息配置的內容，請查看：<a href='https://www.o2oa.net/search.html?q=%E6%B6%88%E6%81%AF%E9%85%8D%E7%BD%AE' target='_blank'>消息</a>",

		"addConsumer": "添加消息通道",
		"consumerLabel": {
			"key": "通道名稱",
			"type": "類型",
			"filter": "過濾器",
			"loader": "載入器",
			"startTlsEnable": "升級傳輸加密"
		},
		"none": "無",
		"editConsumer": "編輯消息通道",

		"inputKey": "請輸入消息通道名稱",
		"hasKey": "消息通道名稱已存在，請使用其他名稱",

        "consumerData": {
            "kafka": ['bootstrapServers', 'topic', 'securityProtocol', 'saslMechanism', 'saslMechanism', 'username', 'password'],
            "activemq": ['url', 'queueName', 'username', 'password'],
            "restful": ['url', 'method', 'internal'],
            "mail": ['host', 'port', 'sslEnable', 'auth', 'startTlsEnable', 'from', 'password'],
            "jdbc": ['driverClass', 'url', 'catalog', 'schema', 'table', 'username', 'password'],
            "table": ['table'],
            "hadoop": ['fsDefaultFS', 'path', 'username']
        },

        "messageTypeTitle": "消息類型設置",
		"messageTypeInfo": "O2OA系統內置的各種事件可以發送消息，您可以在此處設置這些事件需要通過那些通道來發送消息。您也可以增加自定義的消息類型",

		"noConsumer": "此類型消息未選擇發送通道",
		"selectConsumer": "選擇通道",
		"addTmpConsumer": "添加通道",

		"addMessageType": "添加消息類型",
		"newMessageData": {
			"key": "消息標識",
			"description": "描述"
		},
		"inputMessageKey": "請輸入消息標識",
		"hasMessageKey": "消息標識已存在，請使用其他標識",

		"deleteTypeTitle": "刪除消息類型確認",
		"deleteTypeInfo": "您確定要刪除消息類型“{name}”嗎？",

        "filterConfigTitle": "消息過濾器配置",
		"filterConfigInfo": "消息通道中可以使用過濾器，過濾器是一個服務端腳本，在消息發送前被調用，過濾器返回true表示允許消息發送，返回false則此消息不會發送",
		"addFilter": "添加消息過濾器",
		"filterKey": "過濾器名稱",
		"inputFilterKey": "請輸入過濾器名稱",
		"hasFilterKey": "過濾器名稱已存在，請使用其他名稱",
		"deleteFilterTitle": "刪除過濾器確認",
		"deleteFilterInfo": "您確定要刪除過濾器“{name}”嗎？",

		"loaderConfigTitle": "消息加載器配置",
		"loaderConfigInfo": "消息通道中可以使用加載器，過濾器是一個服務端腳本，它用於在發送消息前，對消息內容進行修改，在消息發送前被調用，您必須返回一個JSON格式的數據，作為要發送的消息內容",
		"addLoader": "添加消息加載器",
		"loaderKey": "加載器名稱",

        "inputLoaderKey": "請輸入載入器名稱",
		"hasLoaderKey": "載入器名稱已存在，請使用其他名稱。",

		"deleteLoaderTitle": "載入器刪除確認",
		"deleteLoaderInfo": "您確定要刪除載入器“{name}”嗎？",

		"deleteConsumerTitle": "消息通道刪除確認",
		"deleteConsumerInfo": "您確定要刪除消息通道“{name}”嗎？",

		"loaderComment": "/*\nmessage 對象是消息體，有腳本執行上下文環境環境自動注入，其中有四個字段\nmessage.title: 標題\nmessage.person: 發送對象\nmessage.type: 消息類型，如：task_create\nmessage.body: 消息體，如：類型是task_create的消息中消息體是json格式存儲的task(待辦)數據\nreturn 返回的message對象\n*/\nreturn message;",
		"filterComment": "/*\nmessage 對象是消息體，有腳本執行上下文環境環境自動注入，其中有四個字段\nmessage.title: 標題\nmessage.person: 發送對象\nmessage.type: 消息類型，如：task_create\nmessage.body: 消息體，如：類型是task_create的消息中消息體是json格式存儲的task(待辦)數據\nreturn 返回的boolean，true表示需要發送；false表示不發送\n*/\nreturn true;"
    }
}
