o2.xApplication.systemconfig.LP = {
    "title": "系统配置",
    "searchKey": "搜索设置项",


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
    "logConfig": "日志配置",

    "serverConfig": "服务配置",
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

    "_systemInfo": {
        "title": "配置系统的基础信息",
        "systemName": "系统名称",
        "systemNameInfo": "您的系统名称，它将会显示在您的登录页面和浏览器标题栏",
        "systemSubTitle": "系统副标题",
        "systemSubTitleInfo": "您的系统副标题，它将会显示在您的登录页面下方",
        "systemVersion": "当前系统版本",
        "systemVersionInfo": "当前系统版本",
        "baseInfo": "基本信息",
        "systemStatus": "系统状态"

    },
    "operation": {
        "edit": "编辑",
        "ok": "确定",
        "cancel": "取消"
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
        "uploadWarn": "上传组件zip包，原有组件将被覆盖，请谨慎操作！"
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
        "adminPassword": "管理员密码设置",
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
            'script': "通过脚本自定义初始密码",
        },
        "initialPasswordType": {
            "mobileScript": "return person.getMobile().slice(-6)",
            "uniqueScript": "return person.getunique().slice(-6)",
            "employeesScript":"return person.getEmployee()",
            "pinyinScript":"return person.getPinyin()",
            "textInfo": "在下面的输入框中输入的密码，将作为新创建用户的初始密码。",
            'scriptInfo': "在下面的编辑器中输入脚本，返回一个字符串值，作为新创建用户的初始密码。您可以使用person对象获取人员相关信息。如将人员姓名全拼作为初始密码，可使用脚本：return person.getPinyin()"
        }


        //
        // "initialPasswordType": [
        //     {"label": "mobile", "value": "mobile", "text": "手机号码后六位"},
        //     {"label": "unique", "value": "unique", "text": "唯一编码后六位"},
        //     {"label": "employee", "value": "employee", "text": "人员工号后六位"},
        //     {"label": "pinyin", "value": "pinyin", "text": "人员名称全拼"},
        //     {"label": "text", "value": "text", "text": "固定口令"},
        //     {"label": "script", "value": "script", "text": "通过脚本自定义初始密码"}
        // ]
    }

}
