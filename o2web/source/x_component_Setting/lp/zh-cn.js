MWF.xApplication.Setting.LP = {
	"title": "系统设置",
    "default": "默认",

    "tab_base": "基础配置",
    "tab_ui": "界面配置",
    "tab_mobile": "移动办公配置",
    "tab_name": "系统名称配置",
    "tab_user": "系统用户配置",
    "tab_login": "系统登录配置",
    "tab_sso": "系统SSO配置",

    "tab_mobile_connect": "连接配置",
    "tab_mobile_module": "模块配置",
    "tab_mobile_style": "样式配置",

    "tab_ui_login": "登录页样式",
    "tab_ui_index": "主页样式",
    "tab_ui_module": "模块部署",


    "base_nameSetting": "配置您的系统名称",

    "base_title": "系统名称",
    "base_title_infor": "您的系统名称，它将会显示在您的登录页面和浏览器标题栏",
    "base_title_action": "编辑您的系统名称",
    "base_title_empty": "系统名称不能为空，请输入系统名称",

    "base_footer": "系统副标题",
    "base_footer_infor": "您的系统副标题，它将会显示在您的登录页面下方",
    "base_footer_action": "编辑您的系统副标题",


    "base_personSetting": "配置系统的人员账户基本信息",
    "base_UserPassword": "新建用户的默认密码",
    "base_UserPassword_infor": "创建新建用户时，使用以下密码，用户可登录系统后自行修改",
    "base_UserPassword_action": "修改默认密码",
    "base_UserPassword_empty": "默认密码不能为空",

    "base_passwordPeriod": "密码过期时间（天）",
    "base_passwordPeriod_infor": "超过此设定天数未修改密码的用户，登录后会强制要求修改密码，否则无法进入系统",
    "base_passwordPeriod_action": "修改密码期限",

    "base_adminPassword": "超级管理员密码",
    "base_adminPassword_infor": "超级管理员xadmin的密码，",
    "base_adminPassword_action": "修改超级管理员密码",
    "base_adminPassword_confirm": "<div style='color:red'>超级管理员密码关联默认数据库口令等，请慎重修改超级管理员密码！</div><br>您确定要修改吗？",


    "base_loginSetting": "配置用户登录选项",


    "base_captchaLogin": "启用图片验证码登录",
    "base_captchaLogin_infor": "启用后登陆时必须正确输入图片验证码",
    "base_captchaLogin_action": "",

    "base_codeLogin": "启用短信验证码登录",
    "base_codeLogin_infor": "启用后允许通过短信验证码登录",
    "base_codeLogin_action": "",

    "base_bindLogin": "启用扫描二维码登录",
    "base_bindLogin_infor": "启用后允许扫描二维码登录",

    "base_faceLogin": "启用人脸识别登录",
    "base_faceLogin_infor": "启用后允许人脸识别登录，用户可到个人设置中设置人脸特征",

    "base_faceApi": "人脸识别服务",
    "base_faceApi_action": "编辑人脸识别服务",
    "base_faceApi_delete": "编辑人脸识别服务",
    "base_faceApi_infor": "使用face++人脸服务，您可以编辑您的API Key和API Secret。否则系统将使用默认账号，QPS会受到限制。",

    "base_register": "启用自助注册",
    "base_register_infor": "是否允许自助注册成为系统用户，以及注册方式",
    "register_disable": "不允许自助注册",
    "register_captcha": "允许使用图片验证码自助注册",
    "register_code": "允许使用短信验证码自助注册",

    "base_portalLogin": "使用门户页面登录",
    "base_portalLogin_infor": "可使用定制的门户页面作为登录页，登录页面模板可从应用市场下载。(重启服务器后生效)",
    "base_portalLogin_action": "",

    "base_loginPortalId": "登录门户",
    "base_loginPortalId_infor": "选择登录页面的门户。(重启服务器后生效)",

    "base_portalIndex": "使用门户页面作为系统首页",
    "base_portalIndex_infor": "可使用定制的门户页面作为系统首页，登录后打开此页面。(重启服务器后生效)",
    "base_portalIndex_action": "",
    "base_indexPortalId": "系统首页门户",
    "base_indexPortalId_infor": "选择系统首页的门户。(重启服务器后生效)",


    "base_ssoSetting": "配置与其它系统的单点登录设置",
    "base_ssos": "SSO配置",
    "base_sso_infor": "您可以为多个系统创建SSO配置",
    "base_sso_action": "添加SSO配置",
    "base_sso_editAction": "编辑SSO配置",

    "base_oauths": "OAUTH客户端配置",
    "base_oauths_infor": "如果本系统作为OAUTH2认证服务器，您可以在此可以配置多个OAUTH客户端，为其他系统实现授权",
    "base_oauths_action": "添加oauth配置",
    "base_oauths_editAction": "编辑oauth配置",

    "base_oauths_server": "OAUTH服务端配置",
    "base_oauths_infor_server": "如果本系统需要通过其他OAUTH2服务器认证，您可以在此可以配置多个OAUTH服务端，为本系统实现授权",


    "base_qyweixin": "企业微信配置",
    "base_qyweixin_infor": "系统可以和企业微信集成，请先配置微信企业号和密钥",
    "base_qyweixin_action": "编辑企业微信配置",

    "base_dingding": "钉钉配置",
    "base_dingding_infor": "系统可以和钉钉集成，请先配置钉钉企业号和密钥",
    "base_dingding_action": "编辑钉钉配置",


    "mobile_connectSetting": "移动办公连接配置",

    "mobile_connectO2Cloud": "连接到O2云",
    "mobile_connectO2Cloud_infor": "使用移动办公请先连接到O2云，这有助于APP定位到您的企业服务器，可以使用短信服务等",
    "mobile_connectO2Cloud_action": "连接到O2云",
    "mobile_connectO2Cloud_success": "已连接到O2云",
    "mobile_connectO2Cloud_error": "还未连接到O2云",

    "mobile_httpProtocol": "http协议",
    "mobile_httpProtocol_infor": "请选择使用http协议还是https协议",

    "mobile_center": "中心服务器",
    "mobile_center_infor": "中心服务器对外服务的IP地址或域名和端口。（使用http协议时只能使用IP地址）",
    "mobile_center_action": "编辑中心服务器",

    "mobile_web": "WEB服务器",
    "mobile_web_infor": "WEB服务器对外服务的IP地址或域名和端口，如果域名或IP地址为空或”127.0.0.1“则使用Center服务器地址。（使用http协议时只能使用IP地址）",
    "mobile_web_action": "编辑WEB服务器",

    "mobile_application": "应用服务器",
    "mobile_application_infor": "应用服务器对外服务的IP地址或域名和端口，如果域名或IP地址为空或”127.0.0.1“则使用Center服务器地址。（使用http协议时只能使用IP地址）",
    "mobile_application_action": "编辑应用服务器",

    "mobile_moduleSetting": "移动端模块配置",
    "mobile_index": "移动端首页配置",
    "mobile_index_infor": "您可以配置移动端的首页为默认APP样式，或指定一个门户页面",
    "mobile_index_defalue": "默认",

    "mobile_module": "{name}模块",
    "mobile_module_infor": "移动端是否开启{name}模块",

    "mobile_styleSetting": "移动端图标样式配置",
    "mobile_style": "{name}图片 ",
    "mobile_style_infor": "点击更换{name}图片",
    "mobile_style_imgs": {
        "launch_logo": "启动Logo",
        "login_avatar": "登录界面默认头像",
        "index_bottom_menu_logo_blur": "主页导航图标（未选中）",
        "index_bottom_menu_logo_focus": "主页导航图标（选中）",
        "people_avatar_default": "用户默认头像",
        "process_default": "流程默认图标",
        "setup_about_logo": "关于页面图标"
    },
    "mobile_style_imgs_defaultTitle": "默认图片确认",
    "mobile_style_imgs_defaultInfor": "您确定要将{name}，替换成默认图像吗？",

    "imgSize": "图片尺寸：",
    "defaultImg": "默认图片",

    "ui_loginSetting": "登录页面样式设置",
    "ui_login_default": "默认样式",
    "ui_login_defaultStyle": "登录页面默认样式",
    "ui_login_defaultStyle_infor": "",
    "ui_login_customStyle": "登录页面自定义样式",
    "ui_login_customStyle_infor": "",
    "ui_login_customStyle_Action": "创建自定义样式",
    "ui_login_customStyle_newName": "新样式名称",
    "ui_login_customStyle_newName_empty": "请输入新样式名称",
    "ui_login_setCurrent": "使用此样式",
    "ui_login_current": "当前使用的样式",
    "ui_login_setCurrent_confirmTitle": "使用样式确认",
    "ui_login_setCurrent_confirm": "您确定要使用 “{title}” 样式吗？",

    "ui_login_delete_confirmTitle": "删除样式确认",
    "ui_login_delete_confirm": "您确定要删除 “{title}” 样式吗？",

    "ui_indexSetting": "平台样式配置",
    "ui_index_systemStyle": "平台内置样式",
    "ui_index_systemStyle_infor": "",

    "ui_index_customStyle": "自定义样式配置",
    "ui_index_customStyle_infor": "",
    "ui_index_customStyle_Action": "",
    "ui_index_enabled": "已启用",
    "ui_index_disabled": "已禁用",

    "ui_moduleSetting": "系统模块配置",
    "ui_module_modules": "已部署模块",
    "ui_module_modules_infor": "",
    "ui_module_modules_Action": "部署模块",

    "on": "开",
    "off": "关",

    "loading": "正在加载样式，请稍候……",
    "returnBack": "返回",

    "ok": "确定",
    "cancel": "取消",
    "edit": "编辑",
    "delete": "删除",
    "copy": "复制",
    "copyName": "拷贝",

    "deleteItem": "删除配置确认",
    "deleteItemInfor": "您确定要删除此项配置吗？",

    "setSaved": "配置数据已保存",

    "pleaseInput": "请输入",
    "list": {
        "client": "SSO名称",
        "key": "密钥",
        "clientId": "客户号(ClientId)",
        "mapping": "映射 (Mapping)",
        "corpId": "corpId",
        "corpSecret": "corpSecret",
        "agentId": "agentId",
        "proxyHost": "域名或IP地址",
        "proxyPort": "端口",
        "node": "应用服务器",

        "enable": "是否启用",
        "name": "名称",
        "icon": "图标",
        "clientSecret": "客户密钥",
        "authAddress": "请求密钥网址",
        "authParameter": "请求密钥方法参数",
        "authMethod": "请求密钥方法（GET或POST）",
        "tokenAddress": "请求令牌网址",
        "tokenParameter": "请求令牌方法参数",
        "tokenMethod": "请求令牌方法（GET或POST）",
        "tokenType": "token信息格式（json或form）",
        "infoAddress": "请求信息网址",
        "infoParameter": "请求信息方法参数",
        "infoMethod": "请求信息方法（GET或POST）",
        "infoType": "info信息格式（json或form）",
        "infoCredentialField": "info信息中用于标识个人的字段"
    },

    "module": {
        "title": "应用部署",
        "open": "打开",
        "edit": "编辑",
        "remove": "卸载",
        "add": "部署组件",

        "name": "组件名称",
        "componentTitle": "组件标题",
        "path": "组件路径",
        "icon": "组件图标",
        "isVisible": "是否可见",
        "yes": "是",
        "no": "否",
        "widgetName": "部件名称",
        "widgetTitle": "部件标题",
        "widgetStart": "部件自动启动",
        "widgetVisible": "部件是否可见",

        "allowList": "可访问列表",
        "denyList": "拒绝访问列表",
        "controllerList": "管理者",
        "selPerson": "选择人员",
        "selIcon": "选择图标",
        "urlInfor": "您可以通过“@url:”将路径添加为一个网页URL，如“@url:http://www.bing.com”",

        "phone": "手机",
        "mail": "邮件",

        "noInputInfor": "请完整填写组建信息。（必须填写：组件名称、组件标题、组件路径）",
        "deploySuccess": "部署成功",
        "modifySuccess": "修改组件信息成功",

        "removeComponentTitle": "卸载组件确认",
        "removeComponent": "您确定要卸载组件：{name} 吗？",
        "removeComponentOk": "组件已卸载",

        "modify": "修改组件信息"
    },






    "tab_centerServer": "Center服务器",
    "tab_Server": "服务主机配置",
    "tab_Application": "应用数据配置",
    "tab_Resource": "资源参数配置",
    "tab_Mobile": "移动办公配置",

    "tab_ApplicationServer": "应用服务器",
    "tab_DataServer": "数据库服务器",
    "tab_StorageServer": "存储服务器",
    "tab_WebServer": "Web服务器",

    "deploy": "部署应用",
    "deleteAppServer_title": "移除应用服务器配置确认",
    "deleteAppServer": "您即将移除应用服务器配置文档，移除此文档不会影响服务器运行，是否要继续？",
    "deployAppServer_title": "部署应用确认",
    "deployAppServer": "为当前服务器部署应用，是否确认？<br><br><div style='color: #F00'><input type='checkbox'>重新部署所有应用</div>",

    "deleteDataServer_title": "移除数据库服务器配置确认",
    "deleteDataServer": "您即将移除数据库服务器配置文档，移除此文档不会影响服务器运行，是否要继续？",

    "deleteStorageServer_title": "移除存储服务器配置确认",
    "deleteStorageServer": "您即将移除存储服务器配置文档，移除此文档不会影响服务器运行，是否要继续？",

    "deleteWebServer_title": "移除Web服务器配置确认",
    "deleteWebServer": "您即将移除Web服务器配置文档，移除此文档不会影响服务器运行，是否要继续？",

    "deleteStorage_title": "移除存储配置确认",
    "deleteStorage": "您即将移除存储配置文档，是否要继续？",

    "centerSaveInfor": "数据已提交",
    "saveWeightError": "必须是数字",

    "mobileSetting1": "1、检查",
    "mobileSetting2": "2、账号",
    "mobileSetting3": "3、服务器",

    "checkCenterToCollect": "服务器连接O2中心",
    "checkComputerToCollect": "客户端连接O2中心",
    "success": "成功",
    "failure": "失败",

    "reCheck": "重新检查连接",
    "next": "下一步",
    "loginCollect": "登录到O2中心",
    "loggedCollect": "您已登录O2中心",
    "loginText": "登录O2中心",
    "logoutText": "从O2中心登出",
    "registerText": "如果您还没有O2中心的公司账号，请先注册您的公司",

    "loginInputUsername": "请输入用户名和密码",
    "loginInputCode": "请输入验证码",

    "companyName": "公司名称",
    "code": "验证码",
    "phone": "手机号码",
    "password": "输入密码",
    "confirmPassword": "确认密码",

    "registerTitle": "在O2中心注册您的公司",
    "getPhoneCode": "获取手机验证码",
    "getPhoneCodeWait": "验证码已发送",
    "registerActionText": "注 册",
    "registerCancelActionText": "取消注册",
    "phoneError": "请输入手机号码",
    "phoneTypeError": "请正确输入手机号码",
    "nameError": "请输入公司名称",
    "codeError": "请输入验证码",
    "passwordError": "请输入密码",
    "confirmError": "请输入确认密码",
    "confirmPasswordError": "密码和确认密码输入不一致",

    "cancelRegisterTitle": "取消注册确认",
    "cancelRegister": "您确定要取消注册吗？",
    "registerSuccess": "您的公司已经在O2中心注册成功<br/>您可以用“{name}”登录到O2中心",
    "registerSuccessTitle": "您的公司已经在O2中心注册成功",

    "mobileServerSaveInfor": "数据已提交",
    "mobileServerSaveErrorInfor": "数据已提交, 旦有以下错误：{error}"

};
