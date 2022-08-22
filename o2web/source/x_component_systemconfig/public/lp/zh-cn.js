o2.xApplication.systemconfig.LP = {
    "title": "系统配置",
    "searchKey": "搜索设置项",
    "default": "默认",

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

    "mobileConfig": "移动端配置",
    "connectConfig": "连接配置",
    "moduleConfig": "模块配置",
    "iconConfig": "图标配置",
    "ddConfig": "钉钉集成",
    "wechatConfig": "微信集成",
    "welinkConfig": "welink集成",
    "appTools": "APP工具",

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
        "languageValues":{
            "zh-CN": "简体中文",
            "en": "英文"
        },

        "running": "运行中",
        "stop": "已停用",
        "enable": "已启用",

        "server": "服务器",
        "node": "节点",
        "serverInfo":"服务器信息",
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
        "disable": "禁用"
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
        "deploySuccess": "部署资源成功"
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
        "initialPasswordTypeOptions":{
            "mobile": "手机号码后六位",
            "unique": "唯一编码后六位",
            "employee": "人员工号",
            "pinyin": "人员名称全拼",
            "text": "固定口令",
            'script': "通过脚本自定义初始密码"
        },
        "initialPasswordType": {
            "mobileScript": "return person.getMobile().slice(-6)",
            "uniqueScript": "return person.getunique().slice(-6)",
            "employeesScript":"return person.getEmployee()",
            "pinyinScript":"return person.getPinyin()",
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


        "adminPasswordInfo": "您可以在此处修改超级管理员xadmin的密码。(修改后需要重启服务器)",
        "modifyAdminPassword": "修改管理员密码",

        "oldPassword": "原密码",
        "newPassword": "新密码",
        "confirmPassword": "确认密码",

        "ternaryPassword": "三元管理员密码",
        "ternaryPasswordInfo": "如果您启用了三元管理，系统管理员可以在此处修改系统管理员（systemManager）、安全管理员（securityManager）和安全审计员（auditManager）的密码。",
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
        "captchaLogin":"启用图片验证码登录",
        "codeLogin": "启用短信验证码登录",
        "bindLogin": "启用扫描二维码登录",
        "faceLogin": "启用人脸识别登录",
        "captchaLoginInfo":"启用后登陆时必须正确输入图片验证码",
        "codeLoginInfo": "启用后允许通过短信验证码登录",
        "bindLoginInfo": "启用后允许扫描二维码登录",
        "faceLoginInfo": "启用后允许人脸识别登录，用户可到个人设置中设置人脸特征。启用后您必须创建一个SSO配置，名称为face，密钥为xplatform（这是一个试验性功能，您必须启用https）",

        "loginError": "登录错误处理",
        "loginErrorInfo": "用户登录时，如果连续多次输入错误密码，账号将被锁定。您可以在此处设置连续登录错误次数上限，及账号锁定的时长。",

        "loginErrorCount": "登录错误次数上限",
        "lockTime": "锁定时长（分钟）",

        "tokenExpired": "登录有效时长",
        "tokenExpiredInfo": "用户登录系统后，如果长时间不和服务器发生交互，系统就会注销次此登录。您可以在此处设置登录有效时长，单位为分钟。",

        "tokenName": "token名称",
        "tokenNameInfo": "系统默认的token名称为x-token，您可以在此处修改token名称，以防止在相同Domain下的Cookie冲突，这在相同Domain下部署多套O2OA时尤其有用。(需要重启服务器)",

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
        "userDn": "认证用户的DN(UserDN)",
        "userDnInfo": "认证用户的DN(UserDN), 如：uid=*,ou=users,dc=zone,DC=COM，其中uid=*中的*表示用户的唯一编码，此唯一编码与O2用户的唯一编码对应",

        "superPermission": "启用超级管理员口令",
        "superPermissionInfo": "开启此项允许用超级管理员（xadmin）的口令登录其他用户账户，以方便管理员用普通用户的身份进行数据维护和故障排除。"
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
            "<span style='color: blue'>timestamp</span>：表示为1970年1月1日0时0秒到当前时间的毫秒数。（为了确保token的时效性,有效时间为15分钟）<br/>",

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
        "logBodyEnableInfo": "记录Body内容会得到更详细的日志信息，但也会大大增加磁盘空间占用和服务器开销"
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

        "innerDataSources":"内置数据库",
        "externalDataSources":"扩展数据库",
        "innerDataSourcesInfo": "O2OA自带的数据库是一个内嵌式的内存数据库，适合用于开发环境、功能演示环境。",
        "externalDataSourcesInfo":"O2OA支持外部数据库扩展，建议生产环境使用商用级别数据库以保证数据安全和性能。",

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

        "dumpRestoreTools": "数据库备份回复工具",
        "toolsInfo": "O2OA提供了数据备份和回复工具，<span style='color: red'>修改数据库配置在大部分情况下都会影响到系统现有数据</span>，" +
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

        "deployResourceEnable": "是否允许前端部署资源",
        "deployResourceEnableInfo": "此配置控制前端组件和静态资源，是否允许在WEB端上传部署（需要重启服务器）",

        "statEnable": "启用Druid统计",
        "statExclusions": "统计忽略路径",
        "statEnableInfo": "是否启用Druid统计数据库连接、SQL执行，http请求等相关信息，您可以通过URL：<a href='{url}' target='_blank'>Druid Monitor</a> 访问统计结果页面。",

        "exposeJest": "是否输出Restful API文档页面",
        "exposeJestInfo": "输出Restful API文档可以通过URL：<a href='{url}' target='_blank'>Restful API</a> 访问。",

        "scriptingBlockedClasses": "服务端脚本禁用的Java类",
        "scriptingBlockedClassesInfo": "在此设置不允许在服务端脚本中使用的Java类，用逗号分隔。",

        "refererHeadCheckRegular": "请求Referer校验",
        "refererHeadCheckRegularInfo": "在此处可配置服务器对于请求的Referer头的校验规则，配置一个正则表达式，通过正则表达式校验Referer值的请求才被允许。" +
            "合理配置此项可有效防止CSRF攻击。如配置 (.+?)o2oa.net(.+?) 仅允许referer包含“o2oa.net”的请求",

        "accessControlAllowOrigin": "跨域来源许可",
        "accessControlAllowOriginInfo": "跨源资源共享许可，设置http返回的Access-Control-Allow-Origin标识，可以用于CORS攻击防护，如:https://www.o2oa.net"

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
    }

}
