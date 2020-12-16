o2test = window.o2test || {};

layout.config = {
    "center": [
        {
            "port": "20030",
            "host": "develop.o2oa.net"
        }
    ],
    "initManagerChanged": true,
    "initManagerName": "",
    "initManagerPassword": "",
    "footer": "开发系统Local",
    "title": "o2oa开发平台local",
    "app_protocol": "auto",
    "loginPage": {
        "enable": false,
        "portal": "b82d7669-85d6-4c10-8151-c4d1f18ba6ef",
        "page": ""
    },
    "configMapping": {
        "localhost": {
            "center": {
                "port": "20030",
                "host": "develop.o2oa.net"
            },
            "servers": {
                "x_portal_assemble_designer": {
                    "name": "门户设计",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_portal_assemble_designer"
                },
                "x_portal_assemble_surface": {
                    "name": "门户",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_portal_assemble_surface"
                },
                "x_query_assemble_surface": {
                    "name": "数据查询",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_query_assemble_surface"
                },
                "x_file_assemble_control": {
                    "name": "云文件",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_file_assemble_control"
                },
                "x_organization_assemble_control": {
                    "name": "组织管理",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_organization_assemble_control"
                },
                "x_cms_assemble_control": {
                    "name": "内容管理",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_cms_assemble_control"
                },
                "x_mind_assemble_control": {
                    "name": "脑图",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_mind_assemble_control"
                },
                "x_query_assemble_designer": {
                    "name": "数据查询设计",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_query_assemble_designer"
                },
                "x_general_assemble_control": {
                    "name": "公共模块",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_general_assemble_control"
                },
                "x_processplatform_assemble_surface": {
                    "name": "流程",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_processplatform_assemble_surface"
                },
                "x_processplatform_assemble_designer": {
                    "name": "流程设计",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_processplatform_assemble_designer"
                },
                "x_meeting_assemble_control": {
                    "name": "会议管理",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_meeting_assemble_control"
                },
                "x_processplatform_assemble_bam": {
                    "name": "流程监控",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_processplatform_assemble_bam"
                },
                "x_hotpic_assemble_control": {
                    "name": "热点图片",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_hotpic_assemble_control"
                },
                "x_jpush_assemble_control": {
                    "name": "极光推送服务模块",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_jpush_assemble_control"
                },
                "x_attendance_assemble_control": {
                    "name": "考勤管理",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_attendance_assemble_control"
                },
                "x_bbs_assemble_control": {
                    "name": "论坛",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_bbs_assemble_control"
                },
                "x_processplatform_service_processing": {
                    "name": "流程服务",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_processplatform_service_processing"
                },
                "x_organization_assemble_express": {
                    "name": "组织管理接口服务",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_organization_assemble_express"
                },
                "x_organization_assemble_personal": {
                    "name": "组织管理个人",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_organization_assemble_personal"
                },
                "x_component_assemble_control": {
                    "name": "组件",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_component_assemble_control"
                },
                "x_message_assemble_communicate": {
                    "name": "消息通讯",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_message_assemble_communicate"
                },
                "x_query_service_processing": {
                    "name": "数据查询服务",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_query_service_processing"
                },
                "x_organization_assemble_authentication": {
                    "name": "组织管理认证",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_organization_assemble_authentication"
                },
                "x_calendar_assemble_control": {
                    "name": "日程管理",
                    "host": "develop.o2oa.net",
                    "port": 20020,
                    "context": "/x_calendar_assemble_control"
                }
            }
        }

    }
};


function o2TestLoader() {
    //o2.base = "base/";
    layout.debugger = true;
    o2.session.isDebugger = true;
    //兼容方法
    if (window.Element){
        Element.implement({
            "makeLnk": function (options) { }
        });
    }
    layout.desktop.addEvent = function (type, e, d) {
        window.addEvent(type, e, d);
    };
    layout.desktop.addEvents = function (e) {
        window.addEvents(e);
    };

    // var loadingNode = (window.$) ? $("loaddingArea") : null;
    var loadeds = 0;
    var loadCount = 4;
    var size = (window.document && document.body) ? document.body.getSize() : null;

    var _setLayoutService = function(service, center){
        layout.serviceAddressList = service;
        layout.centerServer = center;
        layout.desktop.serviceAddressList = service;
        layout.desktop.centerServer = center;
    };
    var _getDistribute = function (callback) {

        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }

        if (layout.config.configMapping && (layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname])){
            var mapping = layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname];
            if (mapping.servers){
                layout.serviceAddressList = mapping.servers;
                layout.desktop.serviceAddressList = mapping.servers;
                if (mapping.center) center = (o2.typeOf(mapping.center)==="array") ? mapping.center[0] : mapping.center;
                layout.centerServer = center;
                layout.desktop.centerServer = center;
                if (callback) callback();
            }else{
                if (mapping.center) layout.config.center = (o2.typeOf(mapping.center)==="array") ? mapping.center : [mapping.center];
                o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                    _setLayoutService(service, center);
                    if (callback) callback();
                }.bind(this));
            }
        }else{
            o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                _setLayoutService(service, center);
                if (callback) callback();
            }.bind(this));
        }

    };

    var _load = function () {
        var _loadApp = function (json) {
            //用户已经登录
            if (json){
                layout.user = json.data;
                layout.session = layout.session || {};
                layout.session.user = json.data;
                layout.session.token = json.data.token;
                layout.desktop.session = layout.session;
            }

            while (layout.readys && layout.readys.length) {
                layout.readys.shift().apply(window);
            }
        };

        layout.sessionPromise = new Promise(function(resolve, reject){
            o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
                if (resolve) resolve(json.data);
            }.bind(this), function (xhr, text, error) {
                if (reject) reject({"xhr": xhr, "text": text, "error": error});
            }.bind(this));
        });

        layout.sessionPromise.then(function(data){
            //已经登录
            layout.user = data;
            layout.session = layout.session || {};
            layout.session.user = data;
            layout.session.token = data.token;
            layout.desktop.session = layout.session;
            _loadApp();
        }, function(){
            //允许匿名访问
            if (layout.anonymous) {
                var data = { name: "anonymous", roleList: [] };
                layout.user = data;
                layout.session = layout.session || {};
                layout.session.user = data;
                layout.session.token = data.token;
                layout.desktop.session = layout.session;
                _loadApp();
            } else {
                o2.Actions.load("x_organization_assemble_authentication").AuthenticationAction.login({"credential":o2test.username,"password":o2test.password}, function (json) {
                    _loadApp(json);
                }.bind(this), function (xhr, text, error) {
                    layout.openLogin();
                }.bind(this));
            }
        });
        //_loadApp();

        layout.openLogin = function () {
            layout.desktop.type = "app";
            layout.app = null;
            var content = $("appContent") || $("layout");
            if (content) content.empty();
            layout.authentication = new o2.xDesktop.Authentication({
                "style": "flat",
                "onLogin": _load.bind(layout)
            });
            layout.authentication.loadLogin(document.body);
            var loadingNode = $("browser_loading");
            if (loadingNode) loadingNode.fade("out");
        };
    };

    //异步载入必要模块
    var configLoaded = false;
    var lpLoaded = false;
    var commonLoaded = false;
    var appLoaded = false;
    var lp = o2.session.path + "/lp/" + o2.language + ".js";

    if (o2.session.isDebugger && (o2.session.isMobile || layout.mobile)) o2.load("../o2_lib/eruda/eruda.js");
    debugger;
    var loadModuls = function () {
        lpLoaded = true;

        var modules = [
            'o2.widget.Common',
            'o2.widget.Dialog',
            'o2.widget.UUID',
            'o2.xDesktop.Common',
            'o2.xDesktop.Actions.RestActions',
            'o2.xAction.RestActions',
            'o2.xDesktop.Access',
            'o2.xDesktop.Dialog',
            'o2.xDesktop.Menu',
            'o2.xDesktop.UserData',
            'o2.xDesktop.Authentication',
            'o2.xDesktop.Dialog',
            'o2.xDesktop.Window'
        ];
        o2.require(modules, {
            "onSuccess": function () {
                commonLoaded = true;
                if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
            }
        });
        var apps = [
            ["Template", "MPopupForm"],
            ["Common", ""]
        ];
        o2.requireApp(apps, null, function(){
            appLoaded = true;
            if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
        });
    }

    if (!o2.LP){
        o2.load(lp, loadModuls);
    }else{
        loadModuls();
    }

    //o2.getJSON("../x_desktop/res/config/config.json", function (config) {
        configLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded && appLoaded) _getDistribute(function () { _load(); });
    //});
};

