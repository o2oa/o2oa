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
    "mobile_web_infor": "The IP address or domain name and port of the external service of the Web server.",
    "mobile_web_action": "Edit Web server",

    "mobile_application": "Application Server",
    "mobile_application_infor": "The external service IP address or domain name and port of the Application server.",
    "mobile_application_action": "Edit Application server",

    "mobile_moduleSetting": "Mobile module configuration",
    "mobile_index": "Mobile home page configuration",
    "mobile_index_infor": "You can configure the homepage of the mobile as the default APP style, or specify a portal page",
    "mobile_index_defalue": "defalue",

    "mobile_module": "{name} Module",
    "mobile_module_infor": "Whether the {name} module is enabled on the mobile",

    "mobile_module_simple_mode": "Simple mode on mobile",
    "mobile_module_simple_mode_infor": "Only the homepage and settings page will be displayed after the simple mode is enabled",

    "mobile_styleSetting": "Mobile icon style configuration surface",
    "mobile_style": "{name} picture ",
    "mobile_style_infor": "Click to change {name} picture",
    "mobile_style_imgs": {
        "launch_logo": "Start logo",
        "login_avatar": "Default avatar for login page",
        "index_bottom_menu_logo_blur": "Home navigation icon (unchecked)",
        "index_bottom_menu_logo_focus": "Home navigation icon (selected)",
        "people_avatar_default": "User default avatar",
        "process_default": "Process default icon",
        "setup_about_logo": "About page icon"
    },
    "mobile_style_imgs_defaultTitle": "Default picture confirmation",
    "mobile_style_imgs_defaultInfor": "Are you sure you want to replace {name} with the default image?",

    "imgSize": "size of the picture：",
    "defaultImg": "Default picture",

    "ui_loginSetting": "Login page style setting",
    "ui_login_default": "Default style",
    "ui_login_defaultStyle": "The default style of the login page",
    "ui_login_defaultStyle_infor": "",
    "ui_login_customStyle": "Login page custom style",
    "ui_login_customStyle_infor": "",
    "ui_login_customStyle_Action": "Create a custom style",
    "ui_login_customStyle_newName": "New style name",
    "ui_login_customStyle_newName_empty": "Please enter the name of the new style",
    "ui_login_setCurrent": "Use this style",
    "ui_login_current": "Currently used style",
    "ui_login_setCurrent_confirmTitle": "Use style confirmation",
    "ui_login_setCurrent_confirm": "Are you sure you want to use the \"{title}\" style?",

    "ui_login_delete_confirmTitle": "Delete style confirmation",
    "ui_login_delete_confirm": "Are you sure you want to delete the \"{title}\" style?",

    "ui_indexSetting": "Platform style configuration",
    "ui_index_systemStyle": "Platform built-in style",
    "ui_index_systemStyle_infor": "",

    "Ui_index_customStyle": "Custom style configuration",
    "Ui_index_customStyle_infor": "",
    "Ui_index_customStyle_Action": "",
    "Ui_index_enabled": "Enabled",
    "Ui_index_disabled": "Disabled",

    "ui_moduleSetting": "System Module Setting",
    "ui_module_modules": "Deployed Modules",
    "ui_module_modules_infor": "",
    "ui_module_modules_Action": "Deployment Module",

    "ui_moduleSetting_resource": "Web-side resource deployment",
    "ui_moduleSetting_service": "Service Deployment",

    "resource_upload":"Resource Selection",
    "resource_replace": "Whether to cover",

    "resource_replaceDesc":"Overwrite type: \"No\" delete the original file and upload it, \"Yes\" to overwrite the original",
    "resource_replace_yes":"YES",
    "resource_replace_no":"NO",
    "resource_filePath":"Deployment path",
    "resource_filePathDesc":"If you deploy a zip file, the path can be empty; otherwise, please enter the path in the format: /xxx/xxx",
    "resource_success":"Successful deployment",

    "service_ctl":"Command name",

    "service_node":"Server Node",
    "service_allNode":"All Nodes",
    "service_success": "Deployed successfully, it will take effect after restarting the server",

    "on": "ON",
    "off": "OFF",

    "loading": "Loading styles, please wait...",
    "returnBack": "Return",

    "ok": "OK",
    "cancel": "Cancel",
    "edit": "Edit",
    "delete": "Delete",
    "copy": "Copy",
    "copyName": "Copy",

    "deleteItem": "Delete configuration confirmation",
    "deleteItemInfor": "Are you sure you want to delete this configuration?",

    "setSaved": "Configuration data has been saved",

    "pleaseInput": "Please enter",
    "list": {
        "client": "Client",
        "key": "Key(At least 8 digits)",
        "clientId": "ClientId",
        "mapping": "Mapping",
        "corpId": "corpId",
        "corpSecret": "corpSecret",
        "agentId": "agentId",
        "proxyHost": "Proxy Host",
        "proxyPort": "Proxy Port",
        "node": "Node",

        "enable": "Enable",
        "name": "Name",
        "icon": "Icon",
        "clientSecret": "Client Secret",
        "authAddress": "Auth Address",
        "authParameter": "Auth Parameter",
        "authMethod": "Auth Method (GET or POST)",
        "tokenAddress": "Token Address",
        "tokenParameter": "Token Parameter",
        "tokenMethod": "Token Method (GET or POST)",
        "tokenType": "Token Type (json or form)",
        "infoAddress": "Info Address",
        "infoParameter": "Info Parameter",
        "infoMethod": "Info Method (GET or POST)",
        "infoType": "Info Type (json or form)",
        "infoCredentialField": "Fields used to identify individuals in info",

        "infoProxyHost" : "The domain name does not need to contain \"http\""
    },

    "module": {
        "title": "Application Deployment",
        "open": "open",
        "edit": "edit",
        "remove": "del",
        "add": "Deploy",

        "name": "Name",
        "componentTitle": "Title",
        "path": "Path",
        "icon": "Icon",
        "isVisible": "visible",

        "yes": "Yes",
        "no": "No",
        "widgetName": "Widget Name",
        "widgetTitle": "Widget Title",
        "widgetStart": "Widget Automatic start",
        "widgetVisible": "Widget visible",

        "allowList": "Accessible list",
        "denyList": "Deny List",
        "controllerList": "Manager",
        "selPerson": "Select",
        "selIcon": "Select Icon",
        "urlInfor": "You can add the path as a web page use \"@url:\", such as \"@url:http://www.bing.com\"",

        "phone": "Phone",
        "mail": "Mail",

        "noInputInfor": "Please fill in the complete assembly information. (Required: component name, component title, component path)",
        "deploySuccess": "Deployed successfully",
        "modifySuccess": "Modify the component information successfully",

        "removeComponentTitle": "Confirm to uninstall component",
        "removeComponent": "Are you sure you want to uninstall the component: {name}?",
        "removeComponentOk": "Component has been uninstalled",

        "modify": "Modify component information",

        "moduleDeployed": "Deployed component",
        "inputAppNameNotice": "Please enter the application name and application title",
        "uploadZipFileNotice": "Please upload the ZIP package of the file"
    },

    "tab_centerServer": "Center Server",
    "tab_Server": "Service Host Configuration",
    "tab_Application": "Application Data Configuration",
    "tab_Resource": "Resource parameter configuration",
    "tab_Mobile": "Mobile office configuration",

    "tab_ApplicationServer": "Application Server",
    "tab_DataServer": "Data Server",
    "tab_StorageServer": "Storage Server",
    "tab_WebServer": "Web Server",

    "deploy": "Deploy Application",
    "deleteAppServer_title": "Remove application server configuration confirmation",
    "deleteAppServer": "You are about to remove the application server configuration file. Removing this file will not affect the operation of the server. Do you want to continue?",
    "deployAppServer_title": "Deploy Application Confirmation",
    "deployAppServer": "Deploy the application for the current server, are you sure? <br><br><div style='color: #F00'><input type='checkbox'>Redeploy all applications</div>",

    "deleteDataServer_title": "Remove database server configuration confirmation",
    "deleteDataServer": "You are about to remove the database server configuration file. Removing this file will not affect the server operation. Do you want to continue?",

    "deleteStorageServer_title": "Remove Storage Server Configuration Confirmation",
    "deleteStorageServer": "You are about to remove the storage server configuration file. Removing this file will not affect the operation of the server. Do you want to continue?",

    "deleteWebServer_title": "Remove Web Server Configuration Confirmation",
    "deleteWebServer": "You are about to remove the web server configuration file. Removing this file will not affect the server operation. Do you want to continue?",

    "deleteStorage_title": "Remove storage configuration confirmation",
    "deleteStorage": "You are about to delete the storage configuration file. Do you want to continue?",

    "centerSaveInfor": "Data has been submitted",
    "saveWeightError": "Must be a number",

    "mobileSetting1": "1, Check",
    "mobileSetting2": "2, Account number",
    "mobileSetting3": "3, Server",

    "checkCenterToCollect": "Server connects to O2 Center",
    "checkComputerToCollect": "Client connects to O2 Center",
    "success": "Success",
    "failure": "Failure",

    "reCheck": "Recheck the connection",
    "next": "Next",
    "loginCollect": "Login to O2 Center",
    "loggedCollect": "You have logged into O2 Center",
    "loginText": "Login to O2 Center",
    "logoutText": "Logout from O2 Center",
    "registerText": "If you do not have an O2 Center company account, please register your company first",

    "loginInputUsername": "Please enter your username and password",
    "loginInputCode": "Please enter the verification code",

    "companyName": "Company Name",
    "code": "Verification Code",
    "phone": "Cellphone number",
    "password": "Enter password",
    "confirmPassword": "Confirm Password",

    "registerTitle": "Register your company in O2 Center",
    "getPhoneCode": "Get Phone Verification Code",
    "getPhoneCodeWait": "Verification code has been sent",
    "registerActionText": "Register",
    "registerCancelActionText": "Cancel Registration",
    "phoneError": "Please enter your phone number",
    "phoneTypeError": "Please enter the phone number correctly",
    "nameError": "Please enter the company name",
    "codeError": "Please enter the verification code",
    "passwordError": "Please enter a password",
    "confirmError": "Please enter the confirm password",
    "confirmPasswordError": "The password and confirm password input are inconsistent",

    "cancelRegisterTitle": "Cancel Registration Confirmation",
    "cancelRegister": "Are you sure you want to cancel registration?",

    "registerSuccess": "Your company has been successfully registered in O2 Center<br/>You can log in to O2 Center with \"{name}\"",
    "registerSuccessTitle": "Your company has been successfully registered in the O2 Center",

    "mobileServerSaveInfor": "Data has been submitted",
    "mobileServerSaveErrorInfor": "Data has been submitted, once there is the following error: {error}"
};
