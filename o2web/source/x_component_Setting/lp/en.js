MWF.xApplication.Setting.LP = {
    "title": "Settings",
    "default": "Default",

    "tab_base": "Basic Setting",
    "tab_ui": "UI Setting",
    "tab_mobile": "Mobile Setting",
    "tab_cloud": "Cloud Setting",
    "tab_dispose": "Dispose",
    "tab_name": "System Name",
    "tab_user": "System Users",
    "tab_login": "System Login",
    "tab_sso": "System SSO",
    "tab_config": "Setting File",

    "tab_cloud_connect": "Connect",

    "tab_mobile_connect": "Connect",
    "tab_mobile_module": "Module",
    "tab_mobile_style": "Style",

    "tab_ui_login": "Login UI",
    "tab_ui_index": "Index UI",
    "tab_ui_module": "Module",
    "tab_ui_resource": "Resource",
    "tab_ui_service": "Service",

    "base_nameSetting": "Configure your system name",

    "base_title": "System Name",
    "base_title_infor": "Your system name, it will be displayed on your login page and browser title bar",
    "base_title_action": "Edit System Name",
    "base_title_empty": "The system name cannot be empty, please enter the system name",

    "base_footer": "System Subtitle",
    "base_footer_infor": "Your system subtitle, it will be displayed at the bottom of your login page",
    "base_footer_action": "Edit System Subtitle",


    "base_personSetting": "Configure the basic information of the personnel account of the system",
    "base_UserPassword": "Default password for new users",
    "base_UserPassword_infor": "When creating a new user, use the following password, and the user can modify it after logging in to the system",
    "base_UserPassword_action": "Modify the default password",
    "base_UserPassword_empty": "The default password cannot be empty",

    "base_passwordPeriod": "Password expiration time (days)",
    "base_passwordPeriod_infor": "Users who have not changed their password for more than this set number of days will be forced to change their password after logging in, otherwise they will not be able to enter the system",
    "base_passwordPeriod_action": "Modify Password Period",

    "base_adminPassword": "Super administrator password",
    "base_adminPassword_infor": "Password of the super administrator xadmin",
    "base_adminPassword_action": "Modify the super administrator password",
    "base_adminPassword_confirm": "<div style='color:red'>The super administrator password is associated with the default database password, etc. Please carefully modify the super administrator password! </div><br>Are you sure you want to modify?",


    "base_loginSetting": "Configure user login options",


    "base_captchaLogin": "Enable image verification code login",
    "base_captchaLogin_infor": "After enabling, you must enter the image verification code correctly when logging in",
    "base_captchaLogin_action": "",

    "base_codeLogin": "Enable SMS verification code login",
    "base_codeLogin_infor": "Allow login via SMS verification code after activation",
    "base_codeLogin_action": "",

    "base_bindLogin": "Enable scanning QR code login",
    "base_bindLogin_infor": "After enabling, scan the QR code to log in",

    "base_faceLogin": "Enable face recognition login",
    "base_faceLogin_infor": "Allow face recognition login after enabling, Users can set facial features in personal settings. After enabling, you must create an SSO configuration with the name \"face\" and the key \"xplatform\". (This is an experimental feature, you must enable https)",

    "base_faceApi": "Face recognition service",
    "base_faceApi_action": "Edit facial recognition service",
    "base_faceApi_delete": "Delete facial recognition service",
    "base_faceApi_infor": "With face++ face service, you can edit your API Key and API Secret. Otherwise, the system will use the default account and QPS will be restricted.",

    "base_register": "Enable self-registration",
    "base_register_infor": "Whether to allow self-registration as a system user",
    "register_disable": "Self-registration is not allowed",
    "register_captcha": "Allow self-registration using image verification code",
    "register_code": "Allow self-registration using SMS verification code",

    "base_portalLogin": "Login using the portal page",
    "base_portalLogin_infor": "A customized portal page can be used as the login page, and the login page template can be downloaded from the application market.",
    "base_portalLogin_action": "",

    "base_loginPortalId": "Login portal",
    "base_loginPortalId_infor": "Select the portal for the login page.",

    "base_portalIndex": "Use the portal page as the system homepage",
    "base_portalIndex_infor": "You can use a customized portal page as the system homepage, open this page after logging in.",
    "base_portalIndex_action": "",
    "base_indexPortalId": "System Home Portal",
    "base_indexPortalId_infor": "Select the portal of the system homepage.",


    "base_ssoSetting": "Configure authentication and single sign-on settings with other systems",
    "base_ssos": "Authentication configuration",
    "base_sso_infor": "You can create authentication for multiple systems for SSO login and service invocation",
    "base_sso_action": "Add authentication configuration",
    "base_sso_editAction": "Edit authentication configuration",

    "base_oauths": "OAUTH client configuration",
    "base_oauths_infor": "If this system is used as an OAUTH2 authentication server, you can configure multiple OAUTH clients here to achieve authorization for other systems",
    "base_oauths_action": "Add OAUTH configuration",
    "base_oauths_editAction": "Edit OAUTH configuration",

    "base_oauths_server": "OAUTH server configuration",
    "base_oauths_infor_server": "If this system needs to be authenticated by other OAUTH2 servers, you can configure multiple OAUTH servers here to achieve authorization for this system",


    "base_qyweixin": "Enterprise WeChat configuration",
    "base_qyweixin_infor": "The system can be integrated with enterprise WeChat, please configure the WeChat enterprise account and key first",
    "base_qyweixin_action": "Edit enterprise WeChat configuration",

    "base_dingding": "DingTalk configuration",
    "base_dingding_infor": "The system can be integrated with DingTalk, please configure DingTalk’s enterprise number and key first",
    "base_dingding_action": "Edit DingTalk configuration",


    "mobile_connectSetting": "Mobile Connection Setting",
    "cloud_connectSetting": "Cloud Connection Setting",

    "mobile_connectO2Cloud": "Connect to O2 Cloud",
    "mobile_connectO2Cloud_infor": "To use mobile office, please connect to the O2 cloud first, which will help the APP locate your corporate server, and you can use SMS services, etc.",
    "mobile_connectO2Cloud_action": "Connect to O2 Cloud",
    "mobile_connectO2Cloud_success": "Connect Setting",
    "mobile_connectO2Cloud_error": "Not yet connected to O2 Cloud",

    "mobile_httpProtocol": "http protocol",
    "mobile_httpProtocol_infor": "Please choose to use http protocol or https protocol",

    "mobile_center": "Center Server",
    "mobile_center_infor": "The IP address or domain name and port of the external service of the center server.",
    "mobile_center_action": "Edit center server",

    "mobile_web": "Web Server",
    "mobile_web_infor": "The IP address or domain name and port of the external service of the Web server",
    "mobile_web_action": "Edit Web server",

    "mobile_application": "应用服务器",
    "mobile_application_infor": "The external service IP address or domain name and port of the Application server，如果域名或IP地址为空或”127.0.0.1“则使用Center服务器地址。（使用http协议时只能使用IP地址）",
    "mobile_application_action": "编辑应用服务器",

    "mobile_moduleSetting": "移动端模块配置",
    "mobile_index": "移动端首页配置",
    "mobile_index_infor": "您可以配置移动端的首页为默认APP样式，或指定一个门户页面",
    "mobile_index_defalue": "默认",

    "mobile_module": "{name}模块",
    "mobile_module_infor": "移动端是否开启{name}模块",

    "mobile_module_simple_mode": "移动端简易模式",
    "mobile_module_simple_mode_infor": "移动端开启简易模式后只显示首页和设置页面",

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

    "ui_moduleSetting_resource": "web端资源部署",
    "ui_moduleSetting_service": "服务部署",

    "resource_upload":"资源选择",
    "resource_replace":"是否覆盖",
    "resource_replaceDesc":"覆盖类型：‘否’删除原文件然后上传，‘是’覆盖原文件",
    "resource_replace_yes":"是",
    "resource_replace_no":"否",
    "resource_filePath":"存放目录",
    "resource_filePathDesc":"zip文件可以为空，其他不能为空path:/xxx/xxx",
    "resource_success":"部署成功",

    "service_ctl":"命令名称",
    "service_node":"服务器节点",
    "service_allNode":"全部节点",
    "service_success":"部署成功,需要重启服务后生效",

    "on": "ON",
    "off": "OFF",

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
        "client": "鉴权名称",
        "key": "密钥(最少8位)",
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
        "infoCredentialField": "info信息中用于标识个人的字段",

        "infoProxyHost" : "域名不需要包含http"
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

        "modify": "修改组件信息",

        "moduleDeployed" : "已部署组件",
        "inputAppNameNotice" : "请输入应用名称和应用标题",
        "uploadZipFileNotice": "请上传文件的ZIP包"
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
    "mobileServerSaveErrorInfor": "数据已提交, 旦有以下错误：{error}",

};
