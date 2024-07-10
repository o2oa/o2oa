o2.xApplication.systemconfig.LP = {
    "title": "系统配置",
    "searchKey": "搜索设置项",
    "default": "默认",
    "permissionDenied": "当前用户权限不足，您必须使用管理员帐号访问系统配置",

    "yes": "是",
    "no": "否",
    "uploadInfo": "将文件拖到此处，或点击上传",

    "baseConfig": "基础配置",
    "systemInfo": "系统信息",
    "uiConfig": "界面配置",
    "componentDeploy": "组件部署",
    "resourceDeploy": "资源部署",
    "serviceDeploy": "服务部署",

    "securityConfig": "安全配置",
    "passwordConfig": "密码配置",
    "loginConfig": "登录配置",
    "ssoConfig": "单点登录",
    "ternaryManagement": "三员管理",

    "serverConfig": "服务配置",
    "serversConfig": "服务器配置",
    "centerServer": "中心服务",
    "appServer": "应用服务",
    "webServer": "Web服务",
    "databaseServer": "数据库配置",
    "storageServer": "存储配置",
    "cacheConfig": "缓存配置",
    "clusterConfig": "集群配置",
    "orgConfig": "组织配置",
    "processConfig": "流程配置",
    "cloudConfig": "云服务配置",
    "dumpConfig": "备份配置",
    "worktimeConfig": "工作时间",

    "messageConfig": "消息配置",
    "msgTypeConfig": "类型配置",
    "pushConfig": "消息推送",
    "mailConfig": "邮件配置",
    "smsConfig": "短信配置",
    "mqConfig": "消息队列",

    "queryIndexConfig": "索引配置",


    "mobileConfig": "移动端配置",
    "connectConfig": "连接配置",
    "appConfig": "APP配置",
    "moduleConfig": "模块配置",
    "iconConfig": "图标配置",
    "ddConfig": "钉钉集成",
    "wechatConfig": "微信集成",
    "welinkConfig": "welink集成",
    "appTools": "APP工具",
    "integrationConfig": "应用集成",

    "select": "选择",

    "_systemInfo": {
        "title": "配置系统的基础信息",
        "systemName": "系统名称",
        "systemNameInfo": "您的系统名称，它将会显示在您的登录页面和浏览器标题栏",
        "systemSubTitle": "系统副标题",
        "systemSubTitleInfo": "您的系统副标题，它将会显示在您的登录页面下方",
        "systemVersion": "当前系统版本",
        "systemVersionInfo": "当前系统版本",
        "baseInfo": "基本信息",
        "systemStatus": "系统状态",
        "moduleStatus": "模块运行状态",
        "language": "语言环境",
        "languageInfo": "设置服务端语言环境",
        "supportedLanguages": "语言环境",
        "supportedLanguagesInfo": "系统支持的语言环境",
        "supportedLanguagesInfo2": "添加更多语言环境支持，请从应用市场安装语言包。",
        "supportedLanguagesSetup": "打开应用市场",

        "running": "运行中",
        "stop": "已停用",
        "enable": "已启用",

        "server": "服务器",
        "node": "节点",
        "serverInfo": "服务器信息",
        "webServer": "WEB服务器",
        "appServer": "应用服务器",
        "centerServer": "中心服务器",
        "dataServer": "数据库服务",
        "storageServer": "文件存储服务",
        "dataNode": "数据库",
        "databaseUrl": "数据库连接",

        "byModule": "按应用模块",
        "byServer": "按服务节点",

        "storageNode": "文件存储",

        "serverData": {
            // "exposeJest": "接口文档(exposeJest)",
            "httpProtocol": "http协议(httpProtocol)",
            "host": "主机(host)",
            "port": "端口(port)",
            "proxyHost": "proxy主机(proxyHost)",
            "proxyPort": "proxy端口(proxyPort)",
            "requestLogEnable": "启用http日志",
            "requestLogBodyEnable": "日志记录Body内容",
            "requestLogRetainDays": "日志保留天数",
            "sslEnable": "启用SSL(sslEnable)",
            // "statEnable": "启用Druid",

            "cacheSize": "缓存大小(cacheSize)",
            "includes": "包含类(includes)",
            "excludes": "排除类(excludes)",
            "jmxEnable": "启用JMX(jmxEnable)",
            "lockTimeout": "表锁超时(lockTimeout)",
            "logLevel": "日志级别(logLevel)",
            "maxIdle": "最大空闲连接数(maxIdle)",
            "maxTotal": "最大连接数(maxTotal)",
            "slowSqlMillis": "慢SQL阈值(slowSqlMillis)",
            "statFilter": "启用Druid语句合并(statFilter)",
            "tcpPort": "TCP端口(tcpPort)",
            "webPort": "WEB端口(webPort)"
        },
        "storageData": {
            "port": "ftp端口(port)",
            "sslEnable": "启用SSL(sslEnable)",
            "name": "名称(name)",
            "passivePorts": "被动模式端口(passivePorts)",
            "prefix": "路径前缀(prefix)",
            "deepPath": "使用深路径(deepPath)"
        },
        "storageAccounts": {
            "protocol": "协议",
            "username": "模块",
            "weight": "权重",
            "name": "名称",
            "prefix": "路径前缀",
            "deepPath": "使用深路径",
            "host": "主机",
            "port": "端口",

        },
        "moduleData": {
            "node": "服务节点",
            "contextPath": "上下文",
            "port": "服务端口",
            "sslEnable": "启用SSL",
            "proxyHost": "proxy主机",
            "proxyPort": "proxy端口",
            "reportDate": "上次报告时间",
            "moduleName": "模块名称",
            "className": "类"

        }
    },
    "operation": {
        "edit": "编辑",
        "ok": "确定",
        "cancel": "取消",
        "enable": "启用",
        "disable": "禁用",
        "add": "添加"
    },
    "_component": {
        "open": "打开",
        "edit": "编辑",
        "uninstall": "卸载",

        "deploy": "部署组件",

        "removeComponentTitle": "卸载组件确认",
        "removeComponent": "您确定要卸载组件：{name} 吗？",
        "removeComponentOk": "组件已卸载",

        "deploySuccess": "组件部署成功",

        "selectIcon": "选择图标",
        "clearIcon": "清除图标",

        "name": "组件名称",
        "title": "组件标题",
        "path": "组件路径",
        "urlPathInfo": "您可以通过“@url:”将路径添加为一个网页URL，如“@url:http://www.bing.com”",
        "visible": "是否可见",
        "allowList": "可访问列表",
        "denyList": "拒绝访问列表",
        "icon": "组件图标",

        "upload": "上传资源",
        "uploadWarn": "上传组件zip包，原有组件将被覆盖，请谨慎操作！",

        "componentDataError": "组件名称、组件路径和组件标题不能为空"
    },
    "_resource": {
        "webResource": "部署Web资源",
        "webResourceInfo": "您可以在此处部署Web资源，上传静态资源文件或zip文件，它将被部署到系统的Web服务器，可以通过Http协议访问到。",
        "serviceResource": "部署自定义服务",
        "serviceResourceInfo": "您可以在此处部署您开发的自定义工程，上传编译后的jar包或者war包。部署后需要重启服务器。",

        "componentResource": "组件部署",
        "componentResourceInfo": "您自定义开发的O2OA组件，或从官方获取组件，都可以在此处部署。O2OA组件是名为“x_component_{组件名称}”的文件夹或zip文件。更多详细信息请查阅：<a href='https://www.o2oa.net/develop.html' target='_blank'>O2OA官方社区。</a>",

        "upload": "上传资源",
        "webUploadWarn": "上传要部署的静态资源文件，zip文件会自动解压",
        "serviceUploadWarn": "上传要部署的jar包或者war包",

        "overwrite": "部署方式",
        "overwriteFalse": "删除后上传：删除同名文件和文件夹后上传。",
        "overwriteTrue": "覆盖：直接覆盖同名文件和文件夹。",

        "deployPath": "部署路径",
        "deployPathInfo": "如果部署zip文件，路径可以为空；单个文件部署必须指定部署路径。如：/myWebResource/subPath",

        "noDeployFile": "请先选择要部署的资源文件",
        "deploySuccess": "部署资源成功",

        "notWebResource": "<span style='color: red'>当前服务器不允许前端部署Web资源，您可以到服务器配置-服务器任务中开启此功能</span>",
        "notServiceResource": "<span style='color: red'>当前服务器不允许前端部署部署自定义服务，您可以到服务器配置-服务器任务中开启此功能</span>"
    },
    "_uiConfig": {
        "baseConfig": "基本配置",
        "menuConfig": "主菜单配置",
        "lnkConfig": "侧边栏配置",
        "userConfig": "用户界面配置",

        "openStatus": "进入系统",
        "openStatusInfo": "每次进入O2OA系统，默认会打开上一次退出系统时打开的应用，您可以在此处改变这一行为。",
        "openStatusCurrent": "将打开的应用和当前应用都定位到上一次退出时的状态（默认）",
        "openStatusApp": "打开上一次退出系统时的应用，并将首页作为当前应用",
        "openStatusIndex": "只打开首页应用",

        "skin": "系统皮肤",
        "skinConfig": "允许修改系统皮肤",
        "skinConfigInfo": "是否允许用户个性化修改修改系统皮肤",
        "skinDefault": "系统默认皮肤",
        "skinDefaultInfo": "设置系统默认皮肤色系",
        "scaleConfig": "是否允许缩放",
        "scaleConfigInfo": "是否允许用户个性化设置系统显示的缩放比例",

        "defaultMenuInfo": "保存为默认菜单设置后，未进行个性化菜单设置的用户，会按此设置展现菜单。",
        "forceMenuInfo": "保存为强制菜单设置后，所有的用户都会按此设置展现菜单，个性化设置将会失效。",
        "userMenuInfo": "所有用户的个性化菜单设置将被清除，以默认方式展现菜单。",

        "clearDefaultMenuDataTitle": "清除默认菜单设置",
        "clearDefaultMenuData": "您是否确认清除默认菜单设置？",
        "clearDefaultMenuDataSuccess": "默认菜单设置已清除",
        "clearForceMenuDataTitle": "清除强制菜单设置",
        "clearForceMenuData": "您是否确认清除强制菜单设置？",
        "clearForceMenuDataSuccess": "强制菜单设置已清除",

        "clearUserMenuData": "清除用户个性化菜单设置",
        "clearUserMenuDataSuccess": "用户个性化菜单设置已清除",
        "clearUserMenuDataConfirm": "您确定要清除所有用户的个性化菜单设置",

        "saveDefaultMenuDataSuccess": "默认菜单设置保存成功",
        "saveForceMenuDataSuccess": "强制菜单设置保存成功",

        "defaultMenu": "默认菜单配置",
        "forceMenu": "强制菜单配置",
        "userMenu": "用户个性化菜单配置",

        "saveMenu": "保存配置",
        "clearMenu": "清除配置",
        "loadMenu": "载入配置",
        "clearUserMenu": "清除配置",

        "menu": {
            "application": "应用",
            "process": "流程",
            "cms": "信息",
            "query": "数据",

            "defaultMenu": "恢复默认菜单状态"
        },
        "deleteLink": "删除常用应用快捷方式"
    },
    "_passwordConfig": {
        "personPassword": "用户密码设置",
        "adminPassword": "管理员密码",
        "saveSuccess": "配置保存成功",
        "passwordScript": "密码脚本",

        "newPersonPassword": "新建用户的初始密码",
        "newPersonPasswordInfo": "创建新建用户时，会按以下设定生成用户初始密码，用户可登录系统后自行修改",
        "initialPassword": "用户初始密码",
        "initialPasswordText": "输入初始密码",
        "initialPasswordTypeOptions": {
            "mobile": "手机号码后六位",
            "unique": "唯一编码后六位",
            "employee": "人员工号",
            "pinyin": "人员名称全拼",
            "text": "固定口令",
            'script': "通过脚本自定义初始密码"
        },
        "initialPasswordType": {
            "mobileScript": "return person.getMobile().slice(-6)",
            "uniqueScript": "return person.getUnique().slice(-6)",
            "employeeScript": "return person.getEmployee()",
            "pinyinScript": "return person.getPinyin()",
            "textInfo": "在下面的输入框中输入的密码，将作为新创建用户的初始密码。",
            'scriptInfo': "在下面的编辑器中输入脚本，返回一个字符串值，作为新创建用户的初始密码。您可以使用person对象获取人员相关信息。如将人员姓名全拼作为初始密码，可使用脚本：return person.getPinyin()"
        },

        "passwordPeriod": "密码过期天数",
        "passwordPeriodInfo": "超过此设定天数未修改密码的用户，登录后会强制要求修改密码，否则无法进入系统。设置为 0 表示密码永不过期",

        "passwordRegex": "密码复杂度",
        "passwordRegexInfo": "设置用户密码复杂度要求",

        "passwordRegexMin": "最小长度",
        "passwordRegexMax": "最大长度",
        "passwordRegexLength": "密码长度",
        "passwordRule": "密码规则",
        "passwordRuleValue": {
            "useLowercase": "必须包含小写字母",
            "useNumber": "必须包含数字",
            "useUppercase": "必须包含大写字母",
            "useSpecial": "必须包含特殊字符(#?!@$%^&*-)"
        },
        "passwordRuleRegex": {
            "useLowercase": "(?=.*[a-z])",
            "useNumber": "(?=.*\\d)",
            "useUppercase": "(?=.*[A-Z])",
            "useSpecial": "(?=.*?[#?!@$%^&*-])"
        },
        "savePasswordRule": "保存密码规则设置",
        "passwordLengthText": "{n}位，{text}",

        "passwordRsa": "密码加密传输",
        "passwordRsaInfo": "系统默认使用明文传输，您可以启用此选项，以启用密码的加密传输。(修改后需要重启服务器)",

        "passwordCheck": "密码强制修改",
        "passwordCheckInfo": "如果需要用户首次登录的时候强制修改密码，可以开启此选项。",


        "adminPasswordInfo": "您可以在此处修改超级管理员xadmin的密码。(修改后需要重启服务器)",
        "modifyAdminPassword": "修改管理员密码",

        "oldPassword": "原密码",
        "newPassword": "新密码",
        "confirmPassword": "确认密码",

        "ternaryPassword": "三员管理员密码",
        "ternaryPasswordInfo": "如果您启用了三员管理，系统管理员可以在此处修改系统管理员（systemManager）、安全管理员（securityManager）和安全审计员（auditManager）的密码。",
        "modifySystemManagerPassword": "修改系统管理员密码",
        "modifySecurityManagerPassword": "修改安全管理员密码",
        "modifyAuditManagerPassword": "修改安全审计员密码",

        "passwordDisaccord": "您输入的新密码与确认密码不一致",
        "passwordEmpty": "请输入原密码、新密码和确认密码",

        "tokenEncryptType": "密码加密方式",
        "tokenEncryptTypeInfo": "O2OA支持以下几种密码和Token加密方式，可以根据需要选择。更多信息请查看：<a href='https://www.o2oa.net/search.html?q=%E5%9B%BD%E5%AF%86' target='_blank'>国密</a>",
        "tokenEncryptTypeLabel": "加密方式",
        "encryptTypeOptions": {
            "default": "默认",
            "sm4": "国家商用密码算法"
        },
        "tokenEncryptTypeInfo3": "<div style='color: red'>注意：点击“确定修改密码加密方式”后，此设置立即生效。<ul style='line-height: 30px'><li>这会导致：1、所有用户的登录状态失效；2、由于加密方式改变，所有已有用户将无法登录系统</li>" +
            "<li>您必须执行以下步骤，才能正常使用系统：<br>使用xadmin账号重新登录系统，并通过任何方式重置所有用户密码</li></ul></div>",
        "tokenEncryptTypeButton": "确定修改密码加密方式",
        "changeTokenEncryptTypeInfo": "您确定要修改密码加密方式码？"
    },
    "_loginConfig": {
        "baseConfig": "基本配置",
        "moreConfig": "更多配置",
        "ldapConfig": "Ldap认证配置",
        "captchaLogin": "启用图片验证码登录",
        "codeLogin": "启用短信验证码登录",
        "bindLogin": "启用扫描二维码登录",
        "faceLogin": "启用人脸识别登录",
        "twoFactorLogin": "启用双因素认证登录",
        "captchaLoginInfo": "启用后登陆时必须正确输入图片验证码。",
        "codeLoginInfo": "启用后允许通过短信验证码登录。",
        "bindLoginInfo": "启用后允许扫描二维码登录。",
        "faceLoginInfo": "启用后允许人脸识别登录，用户可到个人设置中设置人脸特征。启用后您必须创建一个SSO配置，名称为face，密钥为xplatform（这是一个试验性功能，您必须启用https）。",
        "twoFactorLoginInfo": "启用后，用户输入账号密码后系统会发送短信验证码，用户再输入短信验证码登录。xadmin和三员管理员不发送短信，在短信验证码界面再次输入密码。双因素认证和短信验证码两种登录方式互斥。",

        "loginError": "登录错误处理",
        "loginErrorInfo": "用户登录时，如果连续多次输入错误密码，账号将被锁定。您可以在此处设置连续登录错误次数上限，及账号锁定的时长。",

        "loginErrorCount": "登录错误次数上限",
        "lockTime": "锁定时长（分钟）",

        "tokenExpired": "pc端登录有效时长",
        "tokenExpiredInfo": "用户登录系统后，如果长时间不和服务器发生交互，系统就会注销次此登录。您可以在此处设置登录有效时长，单位为分钟。用于PC端。",

        "appTokenExpired": "app端登录有效时长",
        "appTokenExpiredInfo": "用户登录系统后，如果长时间不和服务器发生交互，系统就会注销次此登录。您可以在此处设置登录有效时长，单位为分钟。用于移动端。",

        "tokenName": "token名称",
        "tokenNameInfo": "系统默认的token名称为x-token，您可以在此处修改token名称，以防止在相同Domain下的Cookie冲突，这在相同Domain下部署多套O2OA时尤其有用。(需要重启服务器)",

        "tokenCookieHttpOnly": "启用Cookie HttpOnly",
        "tokenCookieHttpOnlyInfo": "保存token的cookie是否启用httponly",

        "tokenCookieSecure": "启用Cookie Secure",
        "tokenCookieSecureInfo": "保存token的cookie是否启用secure，表示仅在https协议才会传输此cookie",

        "enableSafeLogout": "启用安全注销",
        "enableSafeLogoutInfo": "启用安全注销后，您在任意终端执行注销操作，将会同时注销所有终端的登录状态。",

        "register": "启用自助注册",
        "registerInfo": "此处配置是否允许自助注册成为系统用户，以及自助注册方式",
        "registerValues": {
            "disable": "不允许",
            "captcha": "通过验证码注册",
            "code": "通过短信注册"
        },

        "loginPage": "使用门户页面登录",
        "loginPageInfo": "系统支持使用定制的门户页面作为登录页，我们在应用市场上提供了登录页应用模板，您可以免费获取。",
        "loginPagePortal": "登录门户",

        "selectPortal": "请选择门户",

        "indexPage": "使用门户页面作为系统首页",
        "indexPageInfo": "可使用定制的门户页面作为系统首页，登录后打开此页面。",
        "indexPagePortal": "首页门户",

        "ldapAuthEnable": "启用Ldap认证",
        "ldapAuthEnableInfo": "启用后，用户登录认证使用Ldap认证，不再使用本系统的密码登录。请正确配置下面的Ldap参数。",
        "ldapAuthUrl": "Ldap地址",
        "ldapAuthUrlInfo": "Ldap服务地址，ldap://域名或IP:端口",
        "baseDn": "LDAP查询根(BaseDN)",
        "baseDnInfo": "LDAP查询的根名称,如：dc=zone,DC=COM",
        "userDn": "认证用户绑定属性",
        "userDnInfo": "认证用户绑定属性：uid、手机号、员工编码或邮箱(需确保在baseDn下查找到的数据是唯一的并且在o2能查到关联人员, 如： uid 或 mail等",

        "superPermission": "启用超级管理员口令",
        "superPermissionInfo": "开启此项允许用超级管理员（xadmin）的口令登录其他用户账户，以方便管理员用普通用户的身份进行数据维护和故障排除。",

        "bindDnUser": "绑定管理用户",
        "bindDnUserInfo": "绑定一个管理员(需有管理权限的用户)，用于查询认证，如：cn=root",
        "bindDnPwd": "管理用户密码",
        "bindDnPwdInfo": "绑定管理员的密码",
        "ldapEnabledError": "请完整配置所有LDAP参数后，再启用LDAP认证"

    },
    "_ssoConfig": {
        "ssoConfig": "鉴权密钥配置",
        "ssoConfigInfo": "您可以为多个系统创建鉴权，用于SSO登录和服务调用。",
        "ssoConfigInfo2": "每个鉴权需要提供鉴权名称和密钥，此密钥即是用于生成访问票据的加解密公钥。",
        "addSSOConfig": "添加鉴权配置",
        "editSSOConfig": "编辑鉴权配置",
        "isEnable": "是否启用",
        "ssoConfigName": "鉴权名称",
        "ssoConfigKey": "密钥",

        "ssoConfigKeyInfo": "密钥长度为8的倍数",
        "ssoKeyLengthError": "请保持密钥长度为8的倍数",

        "removeSSOConfigTitle": "删除鉴权配置确认",
        "removeSSOConfig": "您确定要删除鉴权配置：“{name}” 吗？",

        "ssoDataError": "鉴权名称和鉴权密钥不能为空",
        "ssoSameNameError": "鉴权名称 “{name}” 已存在，请使用其他名称",

        "useSSOConfig": "如何使用鉴权密钥",
        "useSSOConfigInfo": "在两种场景下需要使用鉴权密钥：",
        "useSSOConfigInfo1": "1、外部系统需要与O2OA实现单点登录;",
        "useSSOConfigInfo2": "2、外部系统需要调用O2OA平台的接口服务;",
        "useSSOConfigInfo3": "需要将鉴权的名称，密钥告知外部系统，外部系统采取3DES算法使用密钥对<span style='color: blue'>\"person#timestamp\"</span>文本进行加密，获取到访问O2OA的临时票据（token）。<br/>" +
            "<span style='color: blue'>person</span>：表示指定用户的用户名、唯一编码或员工号。（具体使用哪个要根据外部系统与O2OA的用户关联的字段）<br/>" +
            "<span style='color: blue'>timestamp</span>：表示为1970年1月1日0时0秒到当前时间的毫秒数。（为了确保token的时效性,有效时间为1分钟）<br/><br>" +
            "生成token后，外部系统可以直接通过访问以下地址，实现与O2OA的单点认证：<br/>" +
            "http://servername/x_desktop/sso.html?client={<span style='color: blue'>client</span>}&xtoken={<span style='color: blue'>token</span>}&redirect={<span style='color: blue'>redirect</span>}<br/>" +
            "<span style='color: blue'>client</span>表示使用的鉴权名称；<br/>" +
            "<span style='color: blue'>token</span>表示产生的临时票据token；<br/>" +
            "<span style='color: blue'>redirect</span>表示认证成功后要跳转到的地址；<br/>",

        "useSSOConfigInfo4": "更多有关鉴权配置的说明，<a target='_blank' href='https://www.o2oa.net/search.html?q=%E9%89%B4%E6%9D%83'>请点击此处查看</a>。",

        "ssoTokenTools": "相关工具",
        "ssoTokenCode": "查看加密样例代码",
        "ssoTokenCheck": "验证token有效性",

        "oauthConfig": "OAuth配置",
        "oauthClientConfig": "OAuth客户端配置",
        "oauthServerConfig": "OAuth服务端配置",

        "oauthClientConfigInfo": "如果将O2OA平台作为OAuth2认证服务器，您可以在此可以配置多个OAuth客户端，为其他系统实现登录授权",
        "oauthServerConfigInfo": "如果您已有OAuth2认证服务端，您可以在此配置多个OAuth服务端，为本系统实现登录授权",

        "addOauthClientConfig": "添加OAuth客户端配置",
        "addOauthServerConfig": "添加OAuth服务端配置",
        "editOauthClientConfig": "编辑OAuth客户端",
        "editOauthServerConfig": "编辑OAuth服务端",

        "removeOauthConfigTitle": "删除OAuth配置确认",
        "removeOauthConfig": "您确定要删除OAuth配置：“{name}” 吗？",

        "oauthClientDataError": "客户号(ClientId)和客户密钥(ClientSecret)不能为空",
        "oauthClientSameNameError": "客户号(ClientId) “{name}” 已存在，请使用其他客户号",

        "oauth_clientId": "客户号",
        "oauth_clientSecret": "客户密钥",
        "oauth_mapping": "返回映射",
        "oauth_name": "名称",
        "oauth_displayName": "显示名称",
        "oauth_icon": "图标URL",
        "oauth_authAddress": "请求密钥地址",
        "oauth_authParameter": "请求密钥参数",
        "oauth_authMethod": "请求密钥方法",

        "oauth_tokenAddress": "请求令牌地址",
        "oauth_tokenParameter": "请求令牌参数",
        "oauth_tokenMethod": "请求令牌方法",
        "oauth_tokenType": "令牌格式",

        "oauth_infoAddress": "请求信息地址",
        "oauth_infoParameter": "请求信息参数",
        "oauth_infoMethod": "请求信息方法",
        "oauth_infoType": "信息格式",

        "oauth_infoCredentialField": "个人信息字段",
        "oauth_bindingField": "绑定用户字段",

        "oauth_infoScriptText": "信息处理脚本",

        "infoScriptTextInfo": "当信息格式不是JSON，也不是FORM时，您可以使用脚本，将信息格式化为JSON对象，以便系统可以正确处理。在下面的脚本编辑器中编写脚本，返回一个JSON对象，您可以使用 <span style='color: blue'>this.text</span> 获取到响应信息的原始文本。"

    },
    "_ternaryManagement": {
        "ternary": "三员管理配置",
        "label": "系统密级标识",

        "enable": "启用三员管理",
        "enableInfo": "系统支持以系统管理员，安全管理员，安全审计员三员分责分权的方式进行系统安全管理，启动三员管理后会解除xadmin用户及权限同时启用系统的审计日志记录（需重启服务器）<br>" +
            "三员各自角色分工分别是： " +
            "<ul><li>系统管理员(系统内置用户：systemManager)：负责为系统用户、组织管理和系统运行维护工作； </li>" +
            "<li>安全管理员(系统内置用户：securityManager)：负责权限设定，负责系统审计日志、用户和系统管理员操作行为的审查分析； </li>" +
            "<li>安全审计员(系统内置用户：auditManager)：负责对系统管理员、安全管理员的操作行为进行审计、跟踪。</li></ul>" +
            "应用定时每天1点分析前一天的操作日志供三个管理员审计查询。<br>" +
            "要完整使用三员管理功能，您还需要从应用市场安装“三员管理”应用。" +
            "更多关于三员管理的内容可查看以下文档和视频：<a href='https://www.o2oa.net/search.html?q=%E4%B8%89%E5%91%98%E7%AE%A1%E7%90%86' target='_blank'>三员管理</a>",
        "logRetainDays": "日志保留天数",
        "logRetainDaysInfo": "设置日志最多保留的天数",

        "logBodyEnable": "记录Body内容",
        "logBodyEnableInfo": "记录Body内容会得到更详细的日志信息，但也会大大增加磁盘空间占用和服务器开销",

        "securityClearanceEnable": "启用系统密级标识",
        "securityClearanceEnableInfo": "如果您的系统涉及相关要求，可启用密级标识<br>" +
            "可设定主体密级标识和客体密级标识，来控制访问权限。 <br>",
            // "<b>主体密级：</b>您可在系统配置-三元管理中设置系统的主体密级。<br>" +
            // "<b>客体密级：</b>可在设计相关表单时增加“密级标识”设计元素，用于设置文档密级标识。",

        "subjectSecurityClearance": "主体密级标识",
        "subjectSecurityClearanceInfo": "配置主题密级标识，标识值为数字，值越大，密级越高。对应密级的主体，可以访问密级值小于或等于主体密级值的客体。如主体密级值为300，则此主体可访问的客体的密级值必须小于或等于300。",

        "objectSecurityClearance": "客体密级标识",
        "objectSecurityClearanceInfo": "配置客体密级标识，标识值为数字，值越大，密级越高",

        "labelName": "标识名称",
        "labelValue": "标识值",

        "defaultSubjectSecurityClearance": "默认主体标识",
        "defaultSubjectSecurityClearanceInfo": "如果主体未设置密级标识，则应用此标识。",

        "systemSecurityClearance": "系统密级标识",
        "systemSecurityClearanceInfo": "设置系统的密级标识，所有其他主体或客体的密级标识，都不会高于系统密级。",

        "labelValueSame": "不能设置相同的标识值",
        "labelNameSame": "不能设置相同的标识名称",
        "labelValueEmpty": "必须输入标识值",
        "labelNameEmpty": "必须输入标识名称"

    },
    "_databaseServer": {
        "databaseSource": "数据源配置",
        "entity": "实体类配置",
        "tools": "备份工具",
        "infoInner": "您正在使用O2OA内置数据库，O2OA自带的数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境，并不适合用作正式环境。 " +
            "如果作为正式环境使用，建议您使用拥有更高性能并且更加稳定的商用级别数据库。",
        "infoExternal": "您已经使用了扩展数据库，O2OA内置数据库已停用。",

        "info": "<span style='color: red'>修改数据库配置在大部分情况下都会影响到系统现有数据，请慎重修改此处配置！</span>",
        "info2": "在修改数据库配置之前，建议您先使用O2OA的备份功能（ctl -dd）将系统数据进行备份，在修改完数据库配置后重启服务器，然后将备份的数据恢复到数据库（ctl -rd）。所有数据库相关配置的修改，都需要重启服务器",

        "innerDataSources": "内置数据库",
        "externalDataSources": "扩展数据库",
        "innerDataSourcesInfo": "O2OA自带的数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境。",
        "externalDataSourcesInfo": "O2OA支持外部数据库扩展，建议生产环境使用商用级别数据库以保证数据安全和性能。",

        "addDatabaseConfig": "添加数据库配置",

        "databaseUrl": "数据库连接",
        "enable": "是否启用",
        "username": "用户名",
        "password": "密码",

        "tcpPort": "连接端口",
        "tcpPortInfo": "数据库jdbc连接端口，登录的用户名:sa，密码为xadmin的密码.数据库创建在/o2server/local/repository/data/X.mv.db，一旦数据库文件被创建,那么该数据库的密码被创建",
        "webPort": "WEB端口",
        "webPortInfo": "H2提供一个web端的client，此端口为web端client的访问端口，用户名sa，密码为xadmin数据库初始创建的密码",
        "jmxEnable": "启动jmx",
        "jmxEnableInfo": "如果启用，可以通过本地的jmx客户端进行访问，不支持远程jmx客户端",
        "cacheSize": "缓存大小",
        "cacheSizeInfo": "H2数据库缓存大小，设置H2用于作为缓存的内存大小，以M作为单位，默认为512M",
        "logLevel": "日志级别",
        "maxTotal": "最大使用连接数",
        "maxIdle": "最大空闲连接数",
        "statEnable": "启用统计",
        "statFilter": "统计方式",
        "slowSqlMillis": "慢sql毫秒数",
        "slowSqlMillisInfo": "执行缓慢sql毫秒数，默认2000毫秒，执行缓慢的sql将被单独记录",
        "lockTimeout": "锁超时时间(毫秒)",

        "inputDatabaseUrl": "请填写数据库连接",

        "entityConfig": "实体类存储分配",
        "entityConfigInfo": "如果您启用了多数据库，您可以在此分配系统中实体类存储的数据库，以提高性能。<span style='color: red'>您必须确保为所有实体类都分配了对应的存储数据库</span>",

        "oneDatabase": "要为系统中实体类分配存储数据库，您必须要启用两个或以上的数据库，您现在只有一个数据库已启用。",
        "oneDatabaseInfo": "要为系统中实体类分配存储数据库，您必须要启用两个或以上的数据库。",


        "includeEntity": "允许的实体类",
        "includeEntityInfo": "此数据库允许存储的实体类，为空表示全部，多个用逗号或换行分割",
        "excludeEntity": "排除的实体类",
        "excludeEntityInfo": "此数据库禁止存储的类，为空表示不禁止任何类，多个用逗号或换行分割",


        "editDatabase": "编辑数据库配置",


        "saveDatabaseConfig": "保存所有数据库配置",
        "saveDatabaseConfigInfo": "本页中的配置在修改后不会立即保存，您必须点击此按钮后，您修改的配置才会被保存",
        "saveDatabaseConfirm": "您即将保存数据库配置<br><span style='color:red'>这有可能会影响到系统现有数据（包括业务数据和设计数据）</span><br><br>您是否确定要保存数据库配置？",

        "reloadDatabaseConfig": "恢复所有数据库配置",
        "reloadDatabaseConfigInfo": "如果您想废弃本页中未保存的修改，可以点击此按钮，以重新载入配置",
        "reloadDatabaseConfirm": "此操作将重新载入数据库配置，未保存的修改将会丢失，您是否确定恢复数据库配置？",

        "saveEntityConfig": "保存实体类配置",
        "saveEntityConfirm": "您即将保存实体类配置<br><span style='color:red'>这有可能会影响到系统现有数据（包括业务数据和设计数据）</span><br><br>您是否确定要保存实体类配置？",
        "reloadEntityConfig": "恢复实体类配置",
        "reloadEntityConfirm": "此操作将重新载入实体类配置，未保存的修改将会丢失，您是否确定恢复实体类配置？",

        "entityList": "可选列表",
        "selectedEntityList": "已选列表",
        "findClass": "查找类名",

        "removeDatabaseConfigTitle": "删除数据库配置确认",
        "removeDatabaseConfig": "<span style='color: red'>注意：您即将删除数据库配置：“{name}”，请务必在删除数据库之前，备份系统数据。</span><br><br>您确定要进行此操作吗？",

        "saveDatabaseConfigSuccess": "数据库配置保存成功，请重启服务器",
        "saveEntityConfigSuccess": "实体类配置保存成功，请重启服务器",

        "dumpRestoreTools": "数据库备份恢复工具",
        "toolsInfo": "O2OA提供了数据备份和恢复工具，<span style='color: red'>修改数据库配置在大部分情况下都会影响到系统现有数据</span>，" +
            "所以在修改数据库配置之前，建议您先使用O2OA的备份功能将系统数据进行备份，在修改完数据库配置后重启服务器，然后将备份的数据恢复到数据库。<br>" +
            "<span class='mainColor_color'>在您进行备份或恢复数据时，请勿离开本页面。您可以在另一个浏览器窗口中进行其它操作</span>",

        "dumpTools": "备份数据",
        "dumpToolsInfo": "点击此按钮进行数据备份，<span style='color: red'>请勿在系统频繁读写数据期间进行</span>",
        "dumpWaitLog": "数据备份未进行",
        "dumpErrorLog": "数据备份发生错误",

        "dumpBegin": "开始备份确认",
        "dumpBeginInfo": "数据备份可能会影响服务器性能，您确定要开始数据备份吗？",

        "dumpCheckButton": "检查备份状态",
        "dumpCheck": "正在检查备份状态 ...",
        "dumpStop": "数据备份未进行",
        "dumpRunning": "数据备份正在进行中 ...",
        "dumpEnd": "数据备份已完成",

        "restoreTools": "数据恢复",
        "restoreToolsInfo": "点击此按钮进行数据恢复，<span style='color: red'>请勿在系统频繁读写数据期间进行</span>",
        "restoreToolsInfo2": "如果您的系统中包含数据表，数据恢复完成后，请进入数据中心编译所有数据表，再执行一次数据恢复，然后重启服务器",
        "restoreWaitLog": "数据恢复未进行",
        "restoreErrorLog": "数据恢复发生错误",

        "restoreBegin": "开始恢复确认",
        "restoreBeginInfo": "数据恢复可能会影响服务器性能，您确定要开始数据恢复吗？",

        "restoreCheckButton": "检查恢复状态",
        "restoreCheck": "正在检查恢复状态 ...",
        "restoreStop": "数据恢复未进行",
        "restoreRunning": "数据恢复正在进行中 ...",
        "restoreEnd": "数据恢复已完成"


    },
    "_cloudConfig": {
        "info": "O2云服务提供了应用市场、移动办公定位、短信服务、文档转换等众多增值服务器，您只需登录到O2云服务器，即可使用。",
        "recheck": "重新检查连接",

        "notValidatedInfo": "登录到O2云，您即可访问应用市场，连接移动办公APP，以及短信服务、文档转换等众多功能！",
        "disconnectInfo": "您的服务器无法连接到O2云，请检查您的服务器网络环境。",
        "validatedInfo": "<span style='color: #ff0000'>您好：</span>{name}，您已经登录到O2云，可使用包括移动办公在内的所有O2平台功能！",

        "connected": "您已经可以连接到O2云了！",
        "disconnect": "您的服务器无法连接到O2云！",
        "notValidated": "您还未登录到O2云！",
        "validated": "您已经登陆到O2云了！",

        "loginInfo": "如果您已有O2云账号，请点击此处登录：",
        "loginButtonText": "登录到O2云",
        "registerInfo": "如果您没有O2云账号，请点击此处注册：",
        "registerButtonText": "注册O2云账号",
        "forgotPasswordInfo": "如果您忘记了O2云账号的密码，请点击此处重置：",
        "forgotPasswordButtonText": "重置O2云密码",

        "collectUsername": "O2云账号",
        "collectPassword": "O2云密码",
        "collectMobile": "手机号码",
        "collectMail": "邮箱地址",
        "collectCode": "验证码",
        "collectConfirm": "确认密码",
        "getCode": "获取验证码",
        "regetCode": "重新获取",

        "inputCollectUsername": "请输入O2云账号",
        "inputCollectPassword": "请输入O2云账号密码",
        "inputCollectMobile": "请输入手机号码",
        "inputCollectMail": "请输入邮箱地址",
        "inputCollectCode": "请输入短信验证码",
        "inputCollectConfirm": "请输入确认密码",
        "collectUsernameExist": "O2云账户名称已存在",
        "collectUsernameNotExist": "O2云账户名称不存在",
        "passwordDisagree": "密码确认不一致",
        "mobileError": "手机号码输入不正确",
        "mailError": "邮箱地址输入不正确",

        "registerCollect": "注册O2云账号",
        "forgotPassword": "忘记密码",
        "loginError": "登录O2云失败，请检查账户名称和密码",
        "registerError": "注册O2云账号出错，请联系技术支持",
        "deleteError": "删除O2云账号出错，请联系技术支持",
        "resetPasswordError": "修改O2云账号密码出错，请联系技术支持",

        "deleteCollectUnit": "删除O2云账号",
        "deleteCollectUnitInfo": "即将删除O2云账号：{name}，请输入手机号码，并获取验证码以确认",

        "resetPasswordCollect": "修改O2云账号密码",

        "modifyCollect": "修改账号",
        "logoutCollect": "断开连接",
        "modifyCollectPassword": "修改密码",
        "deleteCollect": "删除账号",
        "reloginCollect": "重新登录"
    },
    "_serversConfig": {
        "serverInfo": "服务器信息",
        "baseConfig": "基本配置",
        "environmentConfig": "环境变量配置",
        "sameConfig": "使用相同的服务器配置",
        "sameConfigInfo": "O2OA有三个逻辑服务器：中心服务、应用服务和WEB服务，默认情况下它们使用同一个端口和同一套配置，您也可以为三个服务分开配置不同的端口、主机等信息。",

        "serverConfig": "服务器配置",
        "serverConfigInfo": "在此配置服务器相关参数（需要重启服务器）",

        "serverPort": "服务端口",
        "serverPortInfo": "服务器监听端口",

        "serverProxyHost": "访问主机名",
        "serverProxyPort": "访问主机端口",
        "sslEnable": "是否启用SSL",
        "httpProtocol": "WEB访问协议",
        "sslKeyStorePassword": "SSL密码",
        "sslKeyManagerPassword": "SSL管理密码",
        "sslInfo": "<span>启用SSL，您需要将已申请的证书文件复制到O2OA服务端的config目录下，并改名成`keystore`，集群环境需要在每台服务器存放证书文件。（需要重启服务器）</span>",

        "saveServerConfig": "保存服务器配置",
        "saveServerConfigSuccess": "服务器配置保存成功",
        "saveServerConfigPortError": "中心服务器、应用服务器和WEB服务器的端口必须全部相同或全部不同",

        "saveServerSSLConfig": "保存SSL配置",
        "saveServerSSLConfigSuccess": "SSL配置保存成功",

        "sslConfig": "是否启用SSL",

        "serverTaskConfig": "服务器任务",

        "proxyCenterEnable": "代理中心服务",
        "proxyApplicationEnable": "代理应用服务",
        "proxyTimeOut": "代理超时（秒）",

        "includes": "启用的应用模块",
        "includesInfo": "您可以在此选择服务器允许运行的应用模块，只有在此处配置的应用模块才会启动，这可以使得集群环境中更加灵活的分配服务器性能。" +
            "但请慎重修改此配置，如果配置不当，可能会导致服务异常。(需要重启服务器)",
        "includesInfo2": "<b style='color: #666666'>选择要启用的内置应用：</b>如果您没有选择任何模块，则表示所有模块都会启用",
        "includesInfo3": "<b style='color: #666666'>要启用的自定义应用：</b>在下面的输入框中输入自定义应用名称，用半角逗号分隔",

        "saveIncludes": "保存启用应用模块配置",
        "saveExcludes": "保存禁用应用模块配置",

        "excludes": "禁用的应用模块",
        "excludesInfo": "您可以在此选择服务器禁止运行的应用模块，在此配置的应用模块不会启动，这可以使得集群环境中更加灵活的分配服务器性能。" +
            "但请慎重修改此配置，如果配置不当，可能会导致服务异常。(需要重启服务器)",
        "excludesInfo2": "<b style='color: #666666'>选择要禁用的内置应用：</b>如果您没有选择任何模块，则表示所有不禁用任何模块",
        "excludesInfo3": "<b style='color: #666666'>要禁用的自定义应用：</b>在下面的输入框中输入自定义应用名称，用半角逗号分隔",

        "includesAll": "启用全部模块",
        "includesSelect": "选择要启用的模块",
        "includesModules": "已启用模块",
        "selectModules": "可选择模块",

        "excludesNone": "不禁用任何模块",
        "excludesSelect": "选择要禁用的模块",

        "saveServerIncludesSuccess": "保存启用应用模块成功",
        "saveServerExcludesSuccess": "保存禁用应用模块成功",

        "requestLogEnable": "启用HTTP日志",
        "requestLogBodyEnable": "记录Body内容",
        "requestLogRetainDays": "日志保留天数",
        "requestLogInfo": "在此处配置服务器HTTP日志相关内容（需要重启服务器）：" +
            "<ul><li>启用HTTP日志后，日志文件保存在服务器logs目录下。(启用三员管理的情况下，HTTP日志始终会启用)</li>" +
            "<li>记录Body内容会得到更详细的日志信息，但也会大大增加磁盘空间占用和服务器开销。</li>" +
            "<li>设置日志最多保留的天数，超过此天数的日志文件会被删除</li></ul>",

        "webSocketEnable": "是否启用WebSocket",
        "webSocketEnableInfo": "WebSocket用于服务器给WEB用户的消息提醒和聊天等功能，如果启用了WebSocket，请正确配置nginx、WAF等网络系统，以确保允许WebSocket协议通讯。（需要重启服务器）",

        "deployWarEnable": "是否允许前端部署自定义应用",
        "deployWarEnableInfo": "此配置控制自定义应用（war）是否允许在WEB端上传部署（需要重启服务器）",

        "deployResourceEnable": "是否允许前端部署Web资源",
        "deployResourceEnableInfo": "此配置控制前端组件和静态资源，是否允许在WEB端上传部署（需要重启服务器）",

        "statEnable": "启用Druid统计",
        "statExclusions": "统计忽略路径",
        "statEnableInfo": "是否启用Druid统计数据库连接、SQL执行，http请求等相关信息，您可以通过URL：<a href='{url}' target='_blank'>Druid Monitor</a> 访问统计结果页面。",

        "exposeJest": "是否输出Restful API文档页面",
        "exposeJestInfo": "输出Restful API文档可以通过URL：<a href='{url}' target='_blank'>Restful API</a> 访问。",

        "storageEncrypt": "是否对附件进行加密",
        "storageEncryptInfo": "如果开启，系统将对流程平台、内容管理及企业网盘中上传的附件进行加密存储。",

        "scriptingBlockedClasses": "服务端脚本禁用的Java类",
        "scriptingBlockedClassesInfo": "在此设置不允许在服务端脚本中使用的Java类，用逗号分隔。",

        "httpWhiteList": "外部http接口服务地址白名单",
        "httpWhiteListInfo": "外部http接口服务地址白名单，*代表不限制，用半角逗号分隔。",

        "refererHeadCheckRegular": "请求Referer校验",
        "refererHeadCheckRegularInfo": "在此处可配置服务器对于请求的Referer头的校验规则，配置一个正则表达式，通过正则表达式校验Referer值的请求才被允许。" +
            "合理配置此项可有效防止CSRF攻击。如配置 (.+?)o2oa.net(.+?) 仅允许referer包含“o2oa.net”的请求",

        "contentSecurityPolicy": "Content-Security-Policy响应头",
        "contentSecurityPolicyInfo": "HTTP 响应头 Content-Security-Policy 允许站点管理者控制用户代理能够为指定的页面加载哪些资源。除了少数例外情况，设置的政策主要涉及指定服务器的源和脚本结束点。这将帮助防止跨站脚本攻击（Cross-Site Script）。",
        "contentSecurityPolicyInfo2": "更多关于Content-Security-Policy响应头的信息请查看：<a target='_blank' href='https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy'>https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy</a>",

        "accessControlAllowOrigin": "跨域来源许可",
        "accessControlAllowOriginInfo": "跨源资源共享许可，设置http返回的Access-Control-Allow-Origin标识，可以用于CORS攻击防护，如:https://www.o2oa.net",

        "personUnitOrderByAsc": "人员组织升序",
        "personUnitOrderByAscInfo": "在展现获取人员组织数据时，是否使用升序排序，默认true，否则为降序排序",

        "attachmentConfig": "附件上传配置",
        "attachmentConfigInfo": "在此处可以配置系统中允许上传附件的大小和类型",

        "fileSize": "附件大小限制",
        "fileSizeInfo": "以M为单位，最大2048M",
        "fileTypeIncludes": "允许上传的附件类型",
        "fileTypeIncludesInfo": "设置允许上传的附件类型，设置扩展名，用半角逗号分隔",
        "fileTypeExcludes": "禁止上传的附件类型",
        "fileTypeExcludesInfo": "设置禁止上传的附件类型，设置扩展名，用半角逗号分隔",

        "dumpData": "自动备份数据",
        "dumpDataInfo": "O2OA支持定时自动备份数据，请在此处配置",
        "dumpEnable": "是否启用",
        "dumpCron": "定时表达式",
        "dumpSize": "最大备份数",
        "dumpPath": "备份路径",
        "saveDump": "保存自动备份配置",
        "saveDumpSuccess": "保存自动备份配置成功",


        "restoreData": "自动恢复数据",
        "restoreDataInfo": "O2OA支持定时自动恢复数据，请在此处配置",
        "restoreEnable": "是否启用",
        "restoreCron": "定时表达式",
        "restorePath": "恢复路径",
        "saveRestore": "保存自动恢复配置",
        "saveRestoreSuccess": "保存自动恢复配置成功",

        "reloadServerConfig": "重新载入服务器配置"
    },
    "_worktimeConfig": {
        "amWorktime": "上午工作时间",
        "pmWorktime": "下午工作时间",
        "holidays": "节假日",
        "workdays": "工作日",
        "weekends": "周末",

        "amWorktimeInfo": "在此处设置工作日的上午工作时间范围",
        "pmWorktimeInfo": "在此处设置工作日的下午工作时间范围",
        "holidaysInfo": "设置节假日，将原本为工作日的日期添加到此处，作为节假日",
        "workdaysInfo": "设置工作日，将原本为非工作日的日期添加到此处，作为工作日",
        "weekendsInfo": "设置周末，选择下面的周几作为周末非工作日",

        "timeRangeTo": "到",
        "startTime": "开始时间",
        "endTime": "结束时间",

        "weekData": {
            "周一": 2,
            "周二": 3,
            "周三": 4,
            "周四": 5,
            "周五": 6,
            "周六": 7,
            "周日": 1
        }
    },
    "_cacheConfig": {
        "type": "缓存类型",
        "typeInfo": "O2OA系统支持guava和redis两种缓存，默认使用guava。",

        "guava_maximumSize": "缓存最大容量",
        "guava_maximumSizeInfo": "缓存最大容量，对象数量，默认值:3000",
        "guava_expireMinutes": "过期时间",
        "guava_expireMinutesInfo": "过期时间，单位分钟，默认值:30",

        "redis": "redis服务配置",
        "redisInfo": "在此配置redis服务",
        "redis_host": "服务器地址",
        "redis_port": "服务器端口",
        "redis_user": "认证用户",
        "redis_password": "认证口令",
        "redis_connectionTimeout": "连接等待超时",
        "redis_socketTimeout": "返回等待超时",
        "redis_sslEnable": "启用SSL",
        "redis_index": "数据库编号",

        "saveRedis": "保存redis配置",
        "saveRedisSuccess": "保存redis配置成功",
    },
    "_processConfig": {
        "baseConfig": "基本配置",
        "timerConfig": "定时器配置",

        "maintenanceIdentity": "流程维护人身份",
        "selectMaintenanceIdentity": "选择流程维护人身份",
        "maintenanceIdentityInfo": "当流程工作发生意外错误，无法找到对应的处理人情况下，系统先尝试将工作分配给创建者身份，如果创建身份也不可获取，那么就分配给此处设定的身份",

        "formVersionCount": "表单历史版本保留数量",
        "formVersionCountInfo": "表单每次保存时，系统可以保留一个副本作为历史版本，以便在一些特殊情况下找回以前的设计。此处配置表单历史版本最多保留的数量，超过此数量的话最早的历史版本会被删除",

        "processVersionCount": "流程历史版本保留数量",
        "processVersionCountInfo": "流程每次保存时，系统可以保留一个副本作为历史版本，以便在一些特殊情况下找回以前的设计。此处配置流程历史版本最多保留的数量，超过此数量的话最早的历史版本会被删除",

        "scriptVersionCount": "脚本历史版本保留数量",
        "scriptVersionCountInfo": "脚本每次保存时，系统可以保留一个副本作为历史版本，以便在一些特殊情况下找回以前的设计。此处配置脚本历史版本最多保留的数量，超过此数量的话最早的历史版本会被删除",

        "docToWordType": "公文编辑器组件转换WORD方式",
        "docToWordTypeInfo": "公文编辑器组件在配置了转换WORD方式为“Service”时，由后端服务进行WORD转换。" +
            "O2OA系统支持本地服务转换或者使用云服务转换，使用云服务转换能够更好的兼容WORD格式，但您必须先连接到O2云。请在“云服务配置”中连接O2云。",
        "docWordTypeSelect": {
            "local": "本地服务",
            "cloud": "云服务"
        },

        "press": "工作提醒配置",
        "pressInfo": "流程配置中的人工活动节点可以设置允许发起提醒，使得处理过某个工作的人可以对此工作当前的待办人发起办理提醒。您可以在此处设置对于此行为在一个时间段内的次数限制。",
        "pressInfo1": "在",
        "pressInfo2": "分钟内最多发起",
        "pressInfo3": "次提醒",

        "executorCount": "流转执行器数量",
        "executorCountInfo": "处理流程流转的执行器数量。默认32，一般不建议修改",

        "executorQueueBusyThreshold": "执行器队列繁忙阈值",
        "executorQueueBusyThresholdInfo": "处理流程流转的执行器队列的繁忙阈值。默认5，一般不建议修改",

        "timerInfo": "O2OA流程平台需要一些定时器来处理流程任务，您可在此处对这些定时器进行配置。（所有对定时器的修改，都需要重启服务器才能生效）",

        "enable": "是否启用",
        "cron": "定时表达式",
        "urge": "催办定时器",
        "urgeInfo": "如果活动设置了超时时间，此定时器会检查即将到达规定时间的待办，并给其处理人发送催办信息。",

        "expire": "超时定时器",
        "expireInfo": "如果活动设置了超时时间，此定时器会检查待办是否已经超过了规定时间，并将这些待办标记为超时。",

        "touchDelay": "定时活动触发定时器",
        "touchDelayInfo": "此定时器用于触发流程中的定时活动。",

        "deleteDraft": "清除草稿定时器",
        "deleteDraftInfo": "流程中可以使用草稿模式创建流程实例，这种模式在保存前并没有正式启动流程，此定时器可以清除长期没有进行流转的草稿文件",

        "thresholdMinutes": "时间阈值（分）",
        "thresholdMinutesInfo": "设定阈值，单位分钟，如果超过这个时间认为是可以删除的草稿，默认为10天",

        "passExpired": "自动流转定时器",
        "passExpiredInfo": "如果流程活动启用了超时处理，此定时器会去流转那些已经超时的待办",

        "touchDetained": "滞留待办检查定时器",
        "touchDetainedInfo": "此定时器会查找长时间滞留的工作，并尝试驱动此工作进行流转，这可以自动处理由于人员变动等原因引起的工作滞留。",
        "thresholdMinutesInfo_touchDetained": "定时器会处理滞留时间超过这个阈值的工作，默认1440分钟（1天）",

        "updateTable": "同步到数据表定时器",
        "updateTableInfo": "如果流程中设置了将流程数据映射到数据表，此定时器用于处理映射数据队列",

        "archiveHadoop": "归档到Hadoop",
        "archiveHadoopInfo": "O2OA支持将已完成的工作数据归档到Hadoop，您可以在此处设置Hadoop相关配置",
        "fsDefaultFS": "Hadoop地址",
        "username": "Hadoop用户名",
        "path": "路径前缀",
        "saveHadoop": "保存Hadoop配置",
        "saveHadooping": "正在保存 ... ",
        "saveHadoopSuccess": "保存成功",

        "merge": "归档定时器",
        "mergeInfo": ""
    },
    "_queryConfig": {
        "queryIndexConfig": "索引配置",
        "workConfig": "在流转文档",
        "workCompletedConfig": "已完成文档",
        "documentConfig": "内容管理文档",
        "indexTools": "索引工具",

        "work": "在流转",
        "workCompleted": "已完成",
        "document": "内容管理",

        "touchWorkIndex": "执行在流转文档全量索引",
        "touchWorkIndexInfo": "如果您首次启用索引，或从旧版本升级，可以在系统空闲时立即触发在流转文档的全量索引。",
        "touchWorkIndexAction": "立即执行在流转文档的全量索引",

        "touchWorkCompletedIndex": "执行已完成文档全量索引",
        "touchWorkCompletedIndexInfo": "如果您首次启用索引，或从旧版本升级，可以在系统空闲时立即触发已完成文档的全量索引。",
        "touchWorkCompletedIndexAction": "立即执行已完成文档的全量索引",

        "touchDocumentIndex": "执行内容管理文档全量索引",
        "touchDocumentIndexInfo": "如果您首次启用索引，或从旧版本升级，可以在系统空闲时立即触发内容管理文档的全量索引。",
        "touchDocumentIndexAction": "立即执行内容管理文档的全量索引",

        "optimizeIndex": "执行索引优化",
        "optimizeIndexInfo": "优化索引可压缩索引存储空间，优化索引结构，以提升检索性能。执行索引优化需要较长时间，可在系统空闲时立即触发索引优化。",
        "optimizeIndexAction": "立即执行索引优化",

        "indexActionConfirmTitle": "执行{type}全量索引确认",
        "indexActionConfirm": "全量索引会占用较多服务器资源，可能会引起服务器响应变慢，建议在系统空闲时运行。<br><br>您确认要执行{type}文档的全量索引吗？",
        "indexActionSuccess": "{type}全量索引任务已加入队列，系统会立即运行！",

        "optimizeIndexConfirmTitle": "执行索引优化确认",
        "optimizeIndexConfirm": "执行索引优化会占用较多服务器资源，可能会引起服务器响应变慢，建议在系统空闲时运行。<br><br>您确认要执行索引优化吗？",
        "optimizeIndexSuccess": "索引优化任务已加入队列，系统会立即运行！",

        "restartServerInfo": "<span style='color: red'>关于索引配置的修改，将在重启服务器后生效！</span>",

        "enable": "是否启用索引服务",

        "modeConfig": "索引存储位置",
        "modeConfigInfo": "选择索引存储位置，默认为“本地文件系统”",
        "indexMode": "索引存储位置",
        "modeOptions": {
            "localDirectory": "本地文件系统",
            "hdfsDirectory": "hadoop文件系统",
            "sharedDirectory": "共享文件系统"
        },
        "hdfsDirectoryDefaultFS": "hadoop文件系统地址",
        "hdfsDirectoryPath": "hadoop文件系统目录",
        "sharedDirectoryPath": "共享文件系统目录",

        "optimizeIndexEnable": "索引优化",
        "optimizeIndexEnableInfo": "优化索引可压缩索引存储空间，优化索引结构，以提升检索性能。",
        "optimizeIndexCron": "优化索引定时配置",
        "isEnable": "是否启用",
        "cron": "定时表达式",

        "dataStringThreshold": "业务数据最大文本长度阈值",
        "dataStringThresholdInfo": "业务数据最大文本长度阈值，超过此阈值将忽略写入到索引",

        "summaryLength": "摘要长度",

        "attachmentMaxSize":"附件索引阈值",
        "attachmentMaxSizeInfo":"附件索引阈值(兆)，超过此值大小的附件不进行索引",

        "cleanupThresholdDays": "检索内容清理阈值",
        "cleanupThresholdDaysInfo": "检索内容清理阈值(天)，超过此天数未更新的索引会被清除。",

        "searchMaxPageSize": "搜索每页最大数量",
        "searchMaxPageSizeInfo": "搜索返回结果每页的最大数量",

        "moreLikeThisMaxSize": "相关推荐最大返回数量",
        "moreLikeThisMaxSizeInfo": "相关推荐检索的最大返回数量",

        "workIndexAttachment": "是否对流转中文档的附件进行索引",
        "workIndexAttachmentInfo": "是否对流转中文档的附件进行索引。（对附件进行索引，根据不同的业务量，可能需要较强的服务器性能和更大的内存）",

        "lowFreqWorkEnable": "是否启用全量索引",
        "lowFreqWorkEnableInfo": "全量索引会更新所有在流转文档的索引，以保证权限和数据的准确性。",
        "lowFreqWorkCron": "全量索引定时表达式",
        "lowFreqWorkCronInfo": "全量索引会占用较多的服务器资源，如果启用全量索引，建议设置只在系统空闲时段执行。需要注意的是，对于流转中数据，已完成数据和内容管理数据的全量索引，请尽量分开在不同的时间段运行。",
        "lowFreqWorkMaxCount": "全量索引执行的最大数量",
        "lowFreqWorkMaxCountInfo": "设置执行一次索引处理的最大文档数量，达到此数量索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",
        "lowFreqWorkMaxMinutes": "全量索引执行处理时长(分钟)",
        "lowFreqWorkMaxMinutesInfo": "设置执行一次索引处理的最大时长，达到此时长后索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",

        "highFreqWorkEnable": "是否启用增量索引",
        "highFreqWorkEnableInfo": "如果启用增量索引，在文档数据或状态发生改变时，会发出信号，增量索引定时器会在指定的时间运行，获取增量信号并更新文档索引。",
        "highFreqWorkCron": "增量索引定时器",
        "highFreqWorkCronInfo": "增量索引定时执行表达式",
        "highFreqWorkMaxCount": "增量索引单次处理最大数量",
        "highFreqWorkMaxMinutes": "增量索引单次处理最大时长(分钟)",


        "workCompletedIndexAttachment": "是否对已完成文档的附件进行索引",
        "workCompletedIndexAttachmentInfo": "是否对已完成文档的附件进行索引。（对附件进行索引，根据不同的业务量，可能需要较强的服务器性能和更大的内存）",

        "lowFreqWorkCompletedEnable": "是否启用全量索引",
        "lowFreqWorkCompletedEnableInfo": "全量索引会更新所有已流转完成的文档的索引，以保证权限和数据的准确性。",
        "lowFreqWorkCompletedCron": "全量索引定时表达式",
        "lowFreqWorkCompletedCronInfo": "全量索引会占用较多的服务器资源，如果启用全量索引，建议设置只在系统空闲时段执行。需要注意的是，对于流转中数据，已完成数据和内容管理数据的全量索引，请尽量分开在不同的时间段运行。",
        "lowFreqWorkCompletedMaxCount": "全量索引执行的最大数量",
        "lowFreqWorkCompletedMaxCountInfo": "设置执行一次索引处理的最大文档数量，达到此数量索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",
        "lowFreqWorkCompletedMaxMinutes": "全量索引执行处理时长(分钟)",
        "lowFreqWorkCompletedMaxMinutesInfo": "设置执行一次索引处理的最大时长，达到此时长后索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",


        "highFreqWorkCompletedEnable": "否启用增量索引",
        "highFreqWorkCompletedEnableInfo": "如果启用增量索引，在文档数据或状态发生改变时，会发出信号，增量索引定时器会在指定的时间运行，获取增量信号并更新文档索引",
        "highFreqWorkCompletedCron": "增量索引定时器",
        "highFreqWorkCompletedCronInfo": "增量索引定时执行表达式",
        "highFreqWorkCompletedMaxCount": "增量索引单次处理最大数量",
        "highFreqWorkCompletedMaxMinutes": "增量索引单次处理最大时长(分钟)",


        "documentIndexAttachment": "是否对内容管理文档的附件进行索引",
        "documentIndexAttachmentInfo": "是否对已完成文档的附件进行索引。（对附件进行索引，根据不同的业务量，可能需要较强的服务器性能和更大的内存）",

        "lowFreqDocumentEnable": "是否启用内容管理全量索引",
        "lowFreqDocumentEnableInfo": "全量索引会更新所有类型“信息”的内容管理文档的索引，以保证权限和数据的准确性。",
        "lowFreqDocumentCron": "全量索引定时表达式",
        "lowFreqDocumentCronInfo": "全量索引会占用较多的服务器资源，如果启用全量索引，建议设置只在系统空闲时段执行。需要注意的是，对于流转中数据，已完成数据和内容管理数据的全量索引，请尽量分开在不同的时间段运行。",
        "lowFreqDocumentMaxCount": "全量索引执行的最大数量",
        "lowFreqDocumentMaxCountInfo": "设置执行一次索引处理的最大文档数量，达到此数量索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",
        "lowFreqDocumentMaxMinutes": "全量索引执行处理时长(分钟)",
        "lowFreqDocumentMaxMinutesInfo": "设置执行一次索引处理的最大时长，达到此时长后索引停止运行，下次运行索引时，会继上次处理到文档之后继续执行。最大数量与处理时长两个配置任意一个满足就停止索引。",

        "highFreqDocumentEnable": "否启用增量索引",
        "highFreqDocumentEnableInfo": "如果启用增量索引，在文档数据或状态发生改变时，会发出信号，增量索引定时器会在指定的时间运行，获取增量信号并更新文档索引。",
        "highFreqDocumentCron": "增量索引定时器",
        "highFreqDocumentCronInfo": "增量索引定时执行表达式",
        "highFreqDocumentMaxCount": "增量索引单次处理最大数量",
        "highFreqDocumentMaxMinutes": "增量索引单次处理最大时长(分钟)"

    },
    "_appConfig": {
        "connectConfig": "连接配置",
        "moduleConfig": "模块配置",
        "iconConfig": "图标配置",

        "cloudConnect": "云服务连接检查",
        "connectedInfo": "<span style='color:#5fbf78'>[已连接到O2云服务]</span>",
        "notConnectedInfo": "<span style='color:red'>[未连接到O2云服务]</span>，请到 云服务配置 页面进行注册登录",

        "httpProtocol": "WEB访问协议",
        "httpProtocolInfo": "请选择移动端访问中心服务使用HTTP协议还是HTTPS协议",

        "centerServer": "中心服务器",
        "centerServerInfo": "中心服务器对外服务的IP地址或域名和端口。",

        "webServer": "WEB服务器",
        "webServerInfo": "WEB服务器对外服务的IP地址或域名和端口，如果域名或IP地址为空或”127.0.0.1“则使用Center服务器地址。",

        "applicationServer": "应用服务器",
        "applicationServerInfo": "应用服务器对外服务的IP地址或域名和端口，如果域名或IP地址为空或”127.0.0.1“则使用Center服务器地址。",

        "editServer": "编辑服务器地址",
        "host": "域名或IP地址",
        "port": "端口",

        "connectTest": "手机连接测试",
        "connectTestInfo": "使用手机扫描二维码，查看外网是否可以连接服务器",
        "getQrcode": "生成连接测试二维码",


        "mobileIndex": "移动端首页配置",
        "mobileIndexInfo": "您可以配置移动端的首页为默认APP样式，或指定一个门户页面",

        "simpleMode": "移动端简易模式",
        "simpleModeInfo": "移动端开启简易模式后只显示首页和设置页面",

        "appIndexPage": "移动端页面配置",
        "appIndexPageInfo": "移动端几个主页面配置是否显示",
        "appIndexPageHome": "首页",
        "appIndexPageIM": "消息",
        "appIndexPageContact": "通讯录",
        "appIndexPageApp": "应用",
        "appIndexPageSettings": "设置",

        "appIndexCenteredTitle": "移动App首页是否居中",
        "appIndexCenteredInfo": "移动App首页居中，页面个数将不可配置",

        "appIndexCmsFilterTitle": "首页信息中心",
        "appIndexCmsFilterCategoryInfo": "信息中心列表分类查询条件，为空就是都查询",
        "appIndexTaskFilterTitle": "首页办公中心",
        "appIndexTaskFilterProcessInfo": "办公中心列表流程查询条件，为空就是都查询",
        "appIndexTaskFilterProcessSelectorTitle": "流程选择",
        "appIndexCmsFilterCategroySelectorTitle": "分类选择",
        


        "systemMessageSwitch": "显示系统通知",
        "systemMessageSwitchInfo": "移动App消息列表中是否显示系统通知",
        "systemMessageCanClickInfo": "移动App系统通知是否可点击打开",


        "contactPermissionView": "移动App通讯录权限视图",
        "contactPermissionViewInfo": "需要安装应用市场【通讯录】应用，应用内包含通讯录的权限配置视图",

        "appExitAlert": "app退出提示",
        "appExitAlertInfo": "app退出的时候弹出窗口的提示语，为空就不弹窗",

        "nativeAppList": "应用列表",
        "nativeAppListInfo": "您可以在此设置移动端APP中，启用哪些应用，禁用哪些应用，并可设置在APP中的显示名称",

        "imageNames": {
            "application_top": {"text": "应用页面顶部图片", "action": "ApplicationTop"},
            "index_bottom_menu_logo_blur": {"text": "主页导航图标（未选中）", "action": "MenuLogoBlur"},
            "index_bottom_menu_logo_focus": {"text": "主页导航图标（选中）", "action": "MenuLogoFocus"},
            "launch_logo": {"text": "启动Logo图片", "action": "LaunchLogo"},
            "login_avatar": {"text": "登录界面默认头像图片", "action": "LoginAvatar"},
            "process_default": {"text": "流程默认图标", "action": "ProcessDefault"},
            "setup_about_logo": {"text": "关于页面图标", "action": "SetupAboutLogo"}
        },
        "imageSzie": "尺寸",
        "changeImage": "更换图片",
        "defaultImage": "默认图片",
        "defaultImageTitle": "默认图片确认",
        "defaultImageInfo": "您确定要将{name}，替换成默认图像吗？",
    },
    "_integrationConfig": {
        "title": "移动端应用集成",

        "dingding": "钉钉集成",
        "mPweixin": "微信公众号集成",
        "qiyeweixin": "企业微信集成",
        "weLink": "华为WeLink集成",
        "zhengwuDingding": "浙政钉集成",


        "enable": "是否启用钉钉集成",
        "corpId": "钉钉CorpId",
        "agentId": "钉钉AgentId",
        "appKey": "应用唯一标识",
        "appSecret": "应用的密钥",
        "syncCron": "同步检查回调信号定时",
        "forceSyncCron": "强制同步定时",
        "oapiAddress": "钉钉API服务器地址",
        "token": "回调Token",
        "encodingAesKey": "回调encodingAesKey",
        "workUrl": "钉钉消息打开工作的URL",
        "messageRedirectPortal": "处理完成后跳转到门户",
        "messageEnable": "是否启用消息推送",
        "scanLoginEnable": "是否开启钉钉扫码登录",
        "scanLoginAppId": "钉钉扫码登录的AppId",
        "scanLoginAppSecret": "钉钉扫码登录的appSecret",
        "attendanceSyncEnable": "是否启用考勤信息",

        "enableInfo": "O2OA平台拥有配套的原生开发的安卓和IOS移动APP，可以以微应用的方式集成到阿里钉钉，同步钉钉的企业通讯录作为本地组织人员架构，并且可以将待办等通知直接推送到钉钉进行消息提醒。(需要重启服务器)",
        "enableInfo2": "<span class='mainColor_color'>如果O2OA成功接入钉钉，O2OA将会自动从钉钉拉取所有的人员和组织进行同步，O2OA的所有人员和组织以企业钉钉中创建的组织架构为准（ 本地已经创建的人员和组织将保留不会被删除，可能会造成人员和组织重复 ）</span>",
        "enableInfo3": "更多O2OA与钉钉集成的内容，请查看：<a href='https://www.o2oa.net/search.html?q=%E9%92%89%E9%92%89' target='_blank'>钉钉</a>",

        "syncCronInfo": "回调信号触发同步检查,默认每10分钟运行一次,如果期间内有钉钉回调信号接收到,那么触发同步任务进行人员同步.(需要在钉钉设置回调配置)",
        "forceSyncCronInfo": "强制同步定时设置，默认在每天的8点和12点强制进行同步人员和组织",
        "oapiAddressInfo": "钉钉API服务器地址，一般不需要修改",
        "workUrlInfo": "钉钉消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/",
        "messageRedirectPortalInfo": "当钉钉消息处理完成后，可指定跳转到特定的门户页面",

        "saveDingding": "保存钉钉配置",
        "saveDingdingSuccess": "钉钉配置保存成功",

        "mpweixinText": {
            "enable": "是否启用",
            "enablePublish": "启用菜单发布",
            "appid": "微信Appid",
            "appSecret": "微信AppSecret",
            "token": "微信Token",
            "encodingAesKey": "微信encodingAesKey",
            "portalId": "处理完成后跳转到门户",
            "workUrl": "微信公众号消息打开工作的URL",
            "scriptId": "执行服务脚本",
            "messageEnable": "启用模版消息",
            "tempMessageId": "公众号模版消息id",
            "fieldList": "模版字段配置",
            "tempName": "模版字段",
            "name": "业务字段",

            "workUrlInfo": "微信公众号消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/",
            "enableInfo": "O2OA支持微信公众号的集成，用户可以通过关注微信公众号进行工作处理。并且支持待办工作的消息提醒。(需要重启服务器)",
            "enableInfo2": "更多O2OA与微信公众号的内容，请查看：<a href='https://www.o2oa.net/search.html?q=%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7' target='_blank'>微信公众号</a>",
            "enablePublishInfo": "启用菜单发布后，可已将在O2OA中配置好的菜单功能，发布到微信公众号。可在 APP工具-公众号菜单配置 中配置微信公众号菜单",
            "portalIdInfo": "当消息处理完成后，可指定跳转到特定的门户页面",
            "scriptIdInfo": "当从公众号接收到文本消息时，可执行平台服务管理中的接口，在此处指定要执行的接口",
            "fieldListInfo": "这个是模版的内容中业务字段的对应关系，目前O2OA提供了这几个业务字段 【creatorPerson:创建人,  activityName: 当前节点,  processName: 流程名称, startTime: 开始时间, title 标题】",

            "saveMpweixin": "保存微信公众号配置",
            "saveMpweixinSuccess": "微信公众号配置保存成功"
        },
        "qywenxinText": {
            "enable": "是否启用",
            "corpId": "企业微信CorpId",
            "agentId": "企业微信AgentId",
            "corpSecret": "企业微信CorpSecret",
            "syncCron": "同步检查回调信号定时",
            "forceSyncCron": "强制同步定时",
            "apiAddress": "API服务地址",
            "qrConnectAddress": "扫码登录服务地址",
            "oauth2Address": "oAuth2服务地址",
            "syncSecret": "通讯录同步Secret",
            "token": "回调Token",
            "encodingAesKey": "回调EncodingAesKey",
            "workUrl": "消息打开工作的URL",
            "messageRedirectPortal": "处理完成后跳转到门户",
            "messageEnable": "是否启用消息推送",
            "scanLoginEnable": "是否启用扫码登录",
            "attendanceSyncEnable": "是否启用考勤信息",
            "attendanceSyncAgentId": "考勤打卡应用ID",
            "attendanceSyncSecret": "考勤打卡应用Secret",
            "bindEnable": "是否启用用户绑定",
            "bindEnableInfo": "默认不要启用，这个是私有化绑定用户用的，跟同步用户组织是互斥的！",

            "getUserPrivateInfoMessageTitle": "企业微信获取个人隐私信息的消息发送",
            "getUserPrivateInfoMessageDesc": "企业微信新版本同步API限制了用户隐私信息（比如：手机号码、邮箱等）的获取，目前同步程序只能获取到用户姓名和userId。下面的消息发送功能是给用户发送一个授权获取隐私信息的消息，用户点击这个消息后，本程序就能读取到需要的用户信息！",
            "getUserPrivateInfoMessageConsumerList": "消息接收者",
            "getUserPrivateInfoMessageFormTitle": "消息标题",
            "getUserPrivateInfoMessageFormContent": "消息内容",
            "getUserPrivateInfoMessageFormTitleDefault": "【授权获取个人信息】",
            "getUserPrivateInfoMessageFormContentDefault": "应用需要获取您的个人信息，点击授权！",
            "getUserPrivateInfoMessageConsumerEmpty": "请先选择消息接收者！",
            "getUserPrivateInfoMessageFormTitleEmpty": "消息标题不能为空！",
            "getUserPrivateInfoMessageFormContentEmpty": "消息内容不能为空！",
            "getUserPrivateInfoMessageConfirmTitle": "提示",
            "getUserPrivateInfoMessageConfirmText": "确定要给所有选择的用户和组织下人员发送一条获取隐私信息的企业微信消息？",
            "getUserPrivateInfoMessageSendBtn": "发送消息",
            "getUserPrivateInfoMessageSendSuccess": "发送消息成功，请稍后在企业微信中查收！",


            "syncCronInfo": "回调信号触发同步检查,默认每10分钟运行一次,如果期间内有企业微信回调信号接收到,那么触发同步任务进行人员同步.(需要在企业微信设置回调配置)",
            "forceSyncCronInfo": "强制同步定时设置，默认在每天的8点和12点强制进行同步人员和组织",
            "apiAddressInfo": "企业微信API服务器地址，一般不需要修改",
            "workUrlInfo": "企业微信消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/",
            "messageRedirectPortalInfo": "当企业微信消息处理完成后，可指定跳转到特定的门户页面",

            "enableInfo": "O2OA支持以自建应用的方式集成到企业微信，同步企业微信的企业通讯录作为本地组织人员架构，并且可以将待办等通知直接推送到企业微信进行消息提醒。(需要重启服务器)",
            "enableInfo2": "更多O2OA与企业微信的内容，请查看：<a href='https://www.o2oa.net/search.html?q=%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1' target='_blank'>企业微信</a>",

            "saveText": "保存企业微信配置",
            "saveSuccess": "企业微信配置保存成功"
        },
        "welinkText": {

            "enable": "是否启用###",
            "clientId": "应用的ClientId",
            "clientSecret": "应用的ClientSecret###",
            "syncCron": "同步检查回调信号定时",
            "forceSyncCron": "强制同步定时",
            "oapiAddress": "API服务地址",
            "messageEnable": "是否启用消息推送",
            "workUrl": "消息打开工作的URL",
            "messageRedirectPortal": "处理完成后跳转到门户",

            "enableInfo": "O2OA支持以华为WeLink企业内部轻应用的方式集成，同步WeLink的通讯录作为本地组织人员架构，并且可以将待办等通知直接推送到WeLink进行消息提醒。(需要重启服务器)",
            "enableInfo2": "更多O2OA与WeLink的内容，请查看：<a href='https://www.o2oa.net/search.html?q=welink' target='_blank'>WeLink</a>",

            "syncCronInfo": "回调信号触发同步检查,默认每10分钟运行一次,如果期间内有WeLink回调信号接收到,那么触发同步任务进行人员同步.(需要在WeLink设置回调配置)",
            "forceSyncCronInfo": "强制同步定时设置，默认在每天的8点和12点强制进行同步人员和组织",

            "workUrlInfo": "WeLink消息打开工作的url地址，如：https://sample.o2oa.net/x_desktop/",
            "messageRedirectPortalInfo": "当WeLink消息处理完成后，可指定跳转到特定的门户页面",

            "saveText": "保存WeLink配置",
            "saveSuccess": "WeLink配置保存成功"
        }
    },
    "_storageServer": {
        "innerStorage": "内置存储服务",
        "externalStorage": "扩展存储服务",

        "info": "<span style='color: red'>修改存储配置在大部分情况下都会影响到系统现有的文件存储，请慎重修改此处配置！</span>",
        "info2": "在修改存储配置之前，建议您先使用O2OA的备份功能（ctl -dd）将系统数据进行备份，在修改完存储配置后重启服务器，然后将备份的数据恢复（ctl -rd）。所有数据库相关配置的修改，都需要重启服务器",

        "saveStorageConfig": "保存所有存储配置",
        "saveStorageConfigInfo": "本页中的配置在修改后不会立即保存，您必须点击此按钮后，您修改的配置才会被保存",
        "saveStorageConfirm": "您即将保存存储配置<br><span style='color:red'>这有可能会影响到系统现有文件存储。</span><br><br>您是否确定要保存存储配置？",

        "reloadStorageConfig": "恢复所有存储配置",
        "reloadStorageConfigInfo": "如果您想废弃本页中未保存的修改，可以点击此按钮，以重新载入配置",
        "reloadStorageConfirm": "此操作将重新载入存储配置，未保存的修改将会丢失，您是否确定恢复存储配置？",

        "storageType": "存储服务类型",
        "storageTypeInfo": "O2OA系统内置提供了文件存储服务，您也可以更具需要采用外部扩展存储节点。",
        "storageTypeData": [
            {"value": 'inner', "label": "inner", "text": "内置存储服务"},
            {"value": 'external', "label": "external", "text": "扩展存储服务"}
        ],

        "innerInnerInfo": "<span class='mainColor_color'>您正在使用内置文件存储服务</span>，<span style='color:red'>请务必为每个存储节点配置不同的名称</span>",
        "innerExternalInfo": "<span class='mainColor_color'>您已启用扩展文件存储服务</span>，但您任然可以修改内置文件存储服务的配置，<span style='color:red'>请务必为每个存储节点配置不同的名称</span>",

        "innerStorageConfig": "内置存储服务配置",

        "enable": "是否启用",
        "port": "端口",
        "name": "名称",
        "prefix": "前缀路径",
        "deepPath": "使用深路径",
        "saveStorage": "保存存储配置",
        "saveStorageSuccess": "存储配置保存成功",

        "externalInnerInfo": "<span class='mainColor_color'>您正在使用内置文件存储服务</span>，但您任然可以修改扩展文件存储服务的配置",
        "externalExternalInfo": "<span class='mainColor_color'>您已启用扩展文件存储服</span>",

        "enableExternal": "启用扩展文件存储",
        "disableExternal": "禁用扩展文件存储",
        "enableExternalInfo": "如果要启用扩展文件存储，请确保扩展文件存储配置已经完成，否则可能造成服务器运行异常。启用或禁用扩展存储服务都会影响到系统现有的文件存储，强烈建议先备份系统数据。<span style='color:red'>启用后，请为下方的每种类型的文件分别分配存储节点。</span>",

        "enableExternalTitle": "启用扩展文件存储确认",
        "enableExternalConfirm": "您即将启用扩展文件存储，同时会禁用内置文件存储服务。<br><span style='color:red'>这会影响到系统现有已存储的文件</span><br><br>您是否确定要启用扩展文件存？",
        "disableExternalTitle": "禁用扩展文件存储确认",
        "disableExternalConfirm": "您即将禁用扩展文件存储，同时会启用内置文件存储服务。<br><span style='color:red'>这会影响到系统现有已存储的文件</span><br><br>您是否确定要启用扩展文件存？",

        "externalStorageNode": "扩展存储节点配置",
        "addStorageNode": "添加存储节点",
        "editStorageNode": "编辑存储节点",
        "inputStorageNodeKey": "请输入存储节点标识",
        "inputStorageNodeName": "请输入存储节点名称",

        "external": {
            "protocol": "协议",
            "username": "用户名",
            "password": "密码",
            "host": "主机",
            "port": "端口",
            "name": "名称",
            "key": "节点标识",
            "protocolData": {
                "webdav": "webdav",
                "sftp": "sftp",
                "ftps": "ftps",
                "ftp": "ftp",
                "file": "file",
                "hdfs": "hdfs",
                "cifs": "cifs",
                "ali": "阿里云存储",
                "s3":"亚马逊云存储",
                "min":"MinIO存储",
                "cos": "腾讯云存储"
            },
            "protocolDataInfo": {
                "ali": "如果您没有在应用市场安装阿里云OSS集成插件，请先安装。",
                "min":"如果您没有在应用市场安装MinIO云存储集成插件，请先安装。"
            }
        },
        "removeNodeConfigTitle": "删除存储节点确认",
        "removeNodeConfig": "即将删除存储节点“{name}”，此操作可能会影响到系统中已存储的文件，<br>您确定要删除存储节点“{name}”吗？",

        "assignNode": "存储节点分配",
        "assignNodeInfo": "O2OA中存在以下多种类型的文件，您可以给这些文件分配存储节点，一种类型的文件可分配多个节点。",
        "files": {
            "file": "网盘文件（file）",
            "processPlatform": "流程平台文件（processPlatform）",
            "mind": "脑图文件（mind）",
            "meeting": "会议管理文件（meeting）",
            "calendar": "日程安排文件（calendar）",
            "cms": "内容管理文件（cms）",
            "bbs": "论坛文件（bbs）",
            "teamwork": "工作管理文件（strategyDeploy）",
            "structure": "应用管理（structure）",
            "im": "聊聊文件（im）",
            "general": "其他通用文件（general）",
            "custom": "自定义应用文件（custom）",
        },

        "store": "存储节点",

        "noStoreNode": "未分配存储节点",
        "addStore": "添加存储节点",
        "saveStore": "保存"

    },
    "_appTools": {
        "onlineBuild": "APP在线打包",
        "mpweixinMenu": "公众号菜单配置",

        "onlineBuildInfo": " <ul style='padding: 0'><li>当前移动App在线打包功能只支持Android端。</li>" +
            "<li>需要在线打包，必须先到[云服务配置]中进行注册、登录。</li>" +
            "<li>提交信息后，会显示当前打包状态，打包过程耗时较长，你可以先离开当前页面，等待打包完成后来本页面下载APK文件。</li></ul>",

        "onlineBuildInfo1": "<span class='mainColor_color'>我们在应用市场提供了更优秀的“App在线打包”应用，您可以到应用市场查看获取</span>",

        "appPack": {
            "formSubmitBtnTitle": "提交并开始打包",
            "formReinputBtnTitle": "重新填写表单并打包",
            "formRePackBtnTitle": "使用原有资料直接打包",
            "formDownloadApkBtnTitle": "下载APK文件",
            "formDownloadPublishBtnTitle": "下载发布到本地",
            "refreshStatusBtnTitle": "刷新状态",
            "formUploadLogoBtnTitle": "上传图片",

            "messageO2cloudNotEnable": "O2云未启用或无法连接！",
            "messageO2cloudNotLogin": "请先登录O2云！",
            "messageO2cloudLoginFail": "App打包服务器登录失败！",
            "statusOrderInline": "排队中......",
            "statusPacking": "打包中......",
            "statusPackEnd": "打包完成",
            "statusPackError": "打包出错",
            "publishStatusNone": "未发布",
            "publishStatusDoing": "发布中...",
            "publishStatusCompleted": "发布完成，扫码登录界面的二维码可以安装APP！",
            "publishStatusFail": "发布失败，请重试或联系管理员！",
            "messageSubmitNotAtStatus": "当前正在打包中，请稍后再试！",
            "messageAppnameNotEmpty": "App名称不能为空！",
            "messageAppnameLenMax6": "App名称不能超过6个字！",
            "messageAppLogoNotEmpty": "请重新上传Logo图片！",
            "messageAppLogoNeedPng": "Logo图片必须要png格式！",
            "messagePortocolNotEmpty": "HTTP协议不能为空！",
            "messageHostNotEmpty": "中心服务器域名不能为空！",
            "messageHostFormatError": "请填写中心服务器域名或IP，如www.o2oa.net，不要带http这样的头！",
            "messagePortNotEmpty": "中心服务器端口号不能为空！",
            "messageContext_not_empty": "中心服务器上下文不能为空！",
            "messagePortocolMustBeHttpHttps": "HTTP协议只能是 http 或 https ！",
            "messageAlertTitle": "确认提交",
            "messageAlertSubmit": "确定要提交吗，当前表单信息将被打包成移动端App ？",

            "statusLabel": "当前状态",
            "publishStatusLabel": "发布状态",
            "formAppName": "App名称",
            "formAppNameTip": "app桌面显示名称，字数不超过6个",
            "formLogo": "Logo图片",
            "formLogoTip": "App桌面显示的Logo图片，必须是png格式",
            "formProtocol": "HTTP协议",
            "formProtocolTip": "http / https",
            "formHost": "域名",
            "formHostTip": "中心服务器域名或IP，如www.o2oa.net",
            "formPort": "端口号",
            "formPortTip": "中心服务器端口号，如20030",
            "formContext": "上下文",
            "formContextTip": "中心服务器上下文，如/x_program_center",
            "formUrlMapping": "代理urlMapping",
            "formUrlMappingTip": "服务器外网使用代理地址的时候使用，如{ \"demo.o2oa.net:20020\": \"demo.o2oa.net/dev/app\" }",
            "formAppVersionName": "app版本名称",
            "formAppVersionNameTip": "app的版本名称，如v1.0.0。这个字段默认不需要填写！",
            "formAppBuildNo": "app版本编号",
            "formAppBuildNoTip": "app的版本编号，必须是正整数， 如 100。这个字段默认不需要填写！",
            "formEnableOuterPackage": "是否启用外部包名",
            "formEnableOuterPackageTip": "启用外部包名可以防止和官方发布的APP冲突覆盖",
        },

        "mpMenu": {
            "mpweixinInfo": "⚠️ 微信公众号菜单功能需要先启用相关的配置文件[mpweixin.json]，并到微信的公众号管理后台，开发模块中启用服务器配置！",
            "mpweixin": "公众号",
            "publishMpweixin": "发布到微信公众号",
            "publishToWxmp": "注意！当前操作会把所有保存的菜单数据覆盖到微信公众号，确定要继续吗？",
            "publishSuccess": "发布成功，会在24小时后在手机端同步显示！",
            "subscribeMpweixin": "关注回复",
            "subscribeMpweixin_desc": "公众号有新用户关注的时候，自动发送的消息内容",
            "subscribeContentErrorEmpty": "回复消息内容不能为空！",
            "subscribeMpweixin_save": "保存",
            "deleteMenuBtnTitle": "删除菜单",

            "defaultNewName": "新增菜单",
            "formNameLabel": "菜单名称",
            "formOrderLabel": "菜单排序号",
            "formRadioLabel": "菜单内容",
            "formRadioTypeMsg": "发送消息",
            "formRadioTypeUrl": "跳转网页",
            "formRadioTypeMiniprogram": "跳转小程序",

            "formTypeMsgTips": "点击该菜单会发送下列文字给用户，未认证订阅号不支持文字消息",
            "formTypeMsgLabel": "文字消息",
            "formTypeMsgErrorEmpty": "文字消息内容不能为空！",
            "formSubscribeContentErrorEmpty": "回复消息内容不能为空！",
            "formTypeUrlTips": "点击该菜单会跳到以下链接",
            "formTypeUrlLabel": "页面地址",
            "formTypeUrlErrorEmpty": "页面地址不能为空！",
            "formTypeMiniprogramTips": "点击该菜单会跳到以下小程序",
            "formTypeMiniprogramAppidLabel": "小程序ID",
            "formTypeMiniprogramAppidPlaceholder": "小程序ID，请到微信的小程序管理后台查看",
            "formTypeMiniprogramAppidErrorEmpty": "小程序ID不能为空！",
            "formTypeMiniprogramPathLabel": "小程序路径",
            "formTypeMiniprogramPathPlaceholder": "小程序路径，请到微信的小程序管理后台查看",
            "formTypeMiniprogramPathErrorEmpty": "小程序路径不能为空！",
            "formTypeMiniprogramUrlLabel": "备用网页",
            "formTypeMiniprogramUrlPlaceholder": "备用网页，旧版微信会打开这个备用网页",
            "formTypeMiniprogramUrlErrorEmpty": "备用网页不能为空！",
            "formNameTips4": "仅支持中英文和数字，字数不超过4个",
            "formNameTips6": "仅支持中英文和数字，字数不超过6个",
            "formOrderTips": "仅支持数字，字数不超过6个，排序按照字符串排序",
            "msgFirstMaxLen": "一级菜单最多只能创建3个！",
            "menuMsgSubMaxLen": "二级菜单最多只能创建5个！",
            "menuMsgParentNotSave": "上级菜单数据未保存，请先保存数据！",
            "menuDeleteAlertMsg": "确认要删除这条数据吗，会同时删除它的子菜单？",
            "menuDeleteSuccess": "删除数据成功！",
            "menuSaveSuccess": "保存数据成功！",
            "formNameErrorEmpty": "菜单名称不能为空！",
            "formNameErrorMaxLen4": "菜单名称字数不能超过4个！",
            "formNameErrorMaxLen6": "菜单名称字数不能超过6个！",
            "formNameError": "字数超过上限",
            "formOrderErrorEmpty": "菜单排序号不能为空！",
            "formOrderErrorNotNumber": "菜单排序号只能输入数字！",
            "formOrderErrorMaxLen": "菜单排序号字数不能超过6个！",
        }
    },
    "_pushConfig": {
        "pushType": "消息推送服务",
        "pushTypeInfo": "O2OA支持极光推送服务和华为推送服务，您可以根据需要选择推送服务",
        "pushTypeData": [
            {"value": "jpush", "label": "jpush", "text": "极光推送服务"},
            {"value": "none", "label": "none", "text": "禁用消息推送"}
        ],

        "appKey": "极光推送AppKey",
        "masterSecret": "极光推送MasterSecret",
        "appKeyInfo": "极光推送应用的AppKey",
        "masterSecretInfo": "极光推送应用的Master Secret",

        "appId": "华为推送AppId",
        "appSecret": "华为推送AppSecret",
        "appIdInfo": "华为推送应用的appId",
        "appSecretInfo": "华为推送应用的appSecret"
    },
    "_messageConfig": {
        "messageConsumers": "通道配置",
        "messageType": "类型配置",
        "messageLoader": "加载器",
        "messageFilter": "过滤器",

        "consumerTypes": {
            "ws": "WebSocket",
            "pmsinner": "推送消息",
            "calendar": "日程",
            "dingding": "钉钉",
            "welink": "welink",
            "qiyeweixin": "企业微信",
            "mpweixin": "微信公众号",
            "kafka": "kafka",
            "activemq": "ActiveMQ",
            "restful": "Restful",
            "mail": "邮件",
            "jdbc": "JDBC",
            "table": "数据表",
            "hadoop": "Hadoop",
            "andfx": "移动办公消息"
        },
        "consumerInfoTitle": "消息通道配置",
        "consumerInfo": "O2OA系统提供多种消息通道，您可以在此处设置各类消息需要通过什么方式发送",
        "consumerInfo2": "更多关于消息配置的内容，请查看：<a href='https://www.o2oa.net/search.html?q=%E6%B6%88%E6%81%AF%E9%85%8D%E7%BD%AE' target='_blank'>消息</a>",

        "addConsumer": "添加消息通道",
        "consumerLabel": {
            "key": "通道名称",
            "type": "类型",
            "filter": "过滤器",
            "loader": "加载器",
            "startTlsEnable": "升级传输加密"
        },
        "none": "无",
        "editConsumer": "编辑消息通道",

        "inputKey": "请输入消息通道名称",
        "hasKey": "消息通道名称已存在，请使用其它名称",

        "consumerData": {
            "kafka": ['bootstrapServers', 'topic', 'securityProtocol', 'saslMechanism', 'saslMechanism', 'username', 'password'],
            "activemq": ['url', 'queueName', 'username', 'password'],
            "restful": ['url', 'method', 'internal'],
            "mail": ['host', 'port', 'sslEnable', 'auth', 'startTlsEnable', 'from', 'password'],
            "jdbc": ['driverClass', 'url', 'catalog', 'schema', 'table', 'username', 'password'],
            "table": ['table'],
            "hadoop": ['fsDefaultFS', 'path', 'username']
        },

        "messageTypeTitle": "消息类型设置",
        "messageTypeInfo": "O2OA系统内置的各种事件可以发送消息，您可以在此处设置这些事件需要通过那些通道来发送消息。您也可以增加自定义的消息类型",

        "noConsumer": "此类型消息未选择发送通道",
        "selectConsumer": "选择通道",
        "addTmpConsumer": "添加通道",

        "addMessageType": "添加消息类型",
        "newMessageData": {
            "key": "消息标识",
            "description": "描述"
        },
        "inputMessageKey": "请输入消息标识",
        "hasMessageKey": "消息标识已存在，请使用其它标识",

        "deleteTypeTitle": "删除消息类型确认",
        "deleteTypeInfo": "您确定要删除消息类型“{name}”吗？",

        "filterConfigTitle": "消息过滤器配置",
        "filterConfigInfo": "消息通道中可以使用过滤器，过滤器是一个服务端脚本，在消息发送前被调用，过滤器返回true表示允许消息发送，返回false则此消息不会发送",
        "addFilter": "添加消息过滤器",
        "filterKey": "过滤器名称",

        "inputFilterKey": "请输入过滤器名称",
        "hasFilterKey": "过滤器名称已存在，请使用其它名称",

        "deleteFilterTitle": "删除过滤器确认",
        "deleteFilterInfo": "您确定要删除过滤器“{name}”吗？",

        "loaderConfigTitle": "消息加载器配置",
        "loaderConfigInfo": "消息通道中可以使用加载器，过滤器是一个服务端脚本，它用于在发送消息前，对消息内容进行修改，在消息发送前被调用，您必须返回一个JSON格式的数据，作为要发送的消息内容",
        "addLoader": "添加消息加载器",
        "loaderKey": "加载器名称",

        "inputLoaderKey": "请输入加载器名称",
        "hasLoaderKey": "加载器名称已存在，请使用其它名称",


        "deleteLoaderTitle": "删除加载器确认",
        "deleteLoaderInfo": "您确定要删除加载器“{name}”吗？",

        "deleteConsumerTitle": "删除消息通道确认",
        "deleteConsumerInfo": "您确定要删除消息通道“{name}”吗？",

        "loaderComment": "/*\nmessage 对象是消息体,有脚本执行上下文环境环境自动注入,其中有四个字段\nmessage.title: 标题\nmessage.person: 发送对象\nmessage.type: 消息类型，如：task_create\nmessage.body: 消息体，如：类型是task_create的消息中消息体是json格式存储的task(待办)数据\nreturn 返回的message对象\n*/\nreturn message;",
        "filterComment": "/*\nmessage 对象是消息体,有脚本执行上下文环境环境自动注入,其中有四个字段\nmessage.title: 标题\nmessage.person: 发送对象\nmessage.type: 消息类型，如：task_create\nmessage.body: 消息体，如：类型是task_create的消息中消息体是json格式存储的task(待办)数据\nreturn 返回的boolan，true表示需要发送；false表示不发送\n*/\nreturn true;"
    }
}
