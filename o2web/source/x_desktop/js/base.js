layout = window.layout || {};
layout.desktop = layout;
var locate = window.location;
layout.protocol = locate.protocol;
layout.session = layout.session || {};
layout.debugger = (locate.href.toString().indexOf("debugger")!==-1);
o2.xApplication = o2.xApplication || {};

o2.xDesktop = o2.xDesktop || {};
o2.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async);
};
o2.addReady(function(){
    //兼容方法
    Element.implement({
        "makeLnk": function(options){}
    });
    //异步载入必要模块
    layout.config = null;
    var modules = [ "MWF.xDesktop.Common", "MWF.xAction.RestActions" ];
    MWF.require(modules, function(){
        if (layout.serviceAddressList) _getDistribute(function(){ _load(); });
    });
    o2.getJSON("res/config/config.json", function(config){
        layout.config = config;
        if (MWF.xDesktop.getServiceAddress) _getDistribute(function(){ _load(); });
    });

    var _getDistribute = function(callback){
        if (layout.config.app_protocol==="auto"){
            layout.config.app_protocol = window.location.protocol;
        }
        MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
            layout.serviceAddressList = service;
            layout.centerServer = center;
            if (callback) callback();
        }.bind(this));
    };

    var _load = function(){
        //先判断用户是否登录
        MWF.Actions.get("x_organization_assemble_authentication").getAuthentication(function(json){
            //用户已经登录
            layout.user = json.data;
            layout.session = {};
            layout.session.user = json.data;

            (function(layout){
                var _loadResource = function(callback){
                    var isLoadedA = false;
                    var isLoadedB = false
                    //var isLoadedC = false;

                    var lp = o2.session.path+"/lp/"+o2.language+".js";
                    var modules = [
                        "o2.xDesktop.Dialog",
                        "MWF.xDesktop.UserData",
                        "MWF.xDesktop.Access",
                        "MWF.widget.UUID",
                        "MWF.xDesktop.Menu",
                        "MWF.xDesktop.shortcut",
                        "MWF.widget.PinYin",
                        "MWF.xDesktop.Access",
                        "MWF.xDesktop.MessageMobile"
                    ];
                    //MWF.xDesktop.requireApp("Common", "", null, false);
                    var _check = function(){ if (isLoadedA && isLoadedB) if (callback) callback(); };

                    o2.load(["../o2_lib/mootools/plugin/mBox.min.js",lp], function(){isLoadedA = true; _check();});
                    o2.require("MWF.widget.Common", function(){
                        o2.require(modules, function(){
                            o2.requireApp("Common", "", function(){isLoadedB = true; _check();})
                        });
                    });
                };

                var _loadContent =function(){
                    _loadResource(function(){
                        for (var i=0; i<layout.readys.length; i++){
                            layout.readys[i].apply(window);
                        }
                    });
                };

                _loadContent();
            })(layout);
        }, function(){
            //用户未经登录
            //打开登录页面
            var _loadResource = function(callback){
                var isLoadedA = false;
                var isLoadedB = false
                //var isLoadedC = false;

                var lp = o2.session.path+"/lp/"+o2.language+".js";
                var modules = [
                    "o2.xDesktop.Dialog",
                    "MWF.xDesktop.UserData",
                    "MWF.xDesktop.Access",
                    "MWF.widget.UUID",
                    "MWF.xDesktop.Menu",
                    "MWF.xDesktop.shortcut",
                    "MWF.widget.PinYin",
                    "MWF.xDesktop.Access",
                    "MWF.xDesktop.MessageMobile"
                ];
                //MWF.xDesktop.requireApp("Common", "", null, false);
                var _check = function(){ if (isLoadedA && isLoadedB) if (callback) callback(); };

                o2.load(["../o2_lib/mootools/plugin/mBox.min.js",lp], function(){isLoadedA = true; _check();});
                o2.require("MWF.widget.Common", function(){
                    debugger;
                    o2.require(modules, function(){
                        o2.requireApp("Common", "", function(){isLoadedB = true; _check();})
                    });
                });
            };
            _loadResource(function(){
                layout.openLogin();
            });

        });

        layout.openLogin = function(){
            MWF.require("MWF.widget.Common", null, false);
            MWF.require("MWF.xDesktop.Authentication", function(){
                var authentication = new MWF.xDesktop.Authentication({
                    "onLogin": _load.bind(layout)
                });
                authentication.loadLogin(document.body);
            });
        };
    };
});

(function(layout){
    layout.readys = [];
    layout.addReady = function(){
        for (var i = 0; i<arguments.length; i++){
            if (o2.typeOf(arguments[i])==="function") layout.readys.push(arguments[i]);
        }
    };
    var _requireApp = function(appNames, callback, clazzName){
        var appPath = appNames.split(".");
        var baseObject = o2.xApplication;
        appPath.each(function(path, i){
            if (i<(appPath.length-1)){
                baseObject[path] = baseObject[path] || {};
            }else {
                baseObject[path] = baseObject[path] || {"options": Object.clone(MWF.xApplication.Common.options)};
            }
            baseObject = baseObject[path];
        }.bind(this));
        if (!baseObject.options) baseObject.options = Object.clone(MWF.xApplication.Common.options);

        var _lpLoaded = false;
        MWF.xDesktop.requireApp(appNames, "lp."+o2.language, {
            "failure": function(){
                MWF.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this)
        }, false);
        MWF.xDesktop.requireApp(appNames, clazzName, function(){
            if (callback) callback(baseObject);
        });
    };
    var _createNewApplication = function(e, appNamespace, appName, options, statusObj){
        var app = new appNamespace["Main"](this, options);
        app.desktop = layout;
        app.inBrowser = true;
        app.status = statusObj;
        app.load(true);

        var appId = appName;
        if (options.appId){
            appId = options.appId;
        }else{
            if (appNamespace.options.multitask) appId = appId+"-"+(new MWF.widget.UUID());
        }
        app.appId = appId;
        layout.app = app;
        layout.desktop.currentApp = app;
    };

    layout.openApplication = function(e, appNames, options, statusObj){
        if (layout.app){
            var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
            return window.open("app.html?"+par, "_blank");
        }else{
            var appPath = appNames.split(".");
            var appName = appPath[appPath.length-1];

            _requireApp(appNames, function(appNamespace){
                _createNewApplication(e, appNamespace, appName, options, statusObj);
            }.bind(this));
        }
    };

    layout.refreshApp = function(app){
        var status = app.recordStatus();

        var uri = new URI(window.location.href);
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var statusStr = uri.getData("status");
        if (status) statusStr = JSON.encode(status);

        var port = uri.get("port");
        window.location = uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port + "/" : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
    };

    layout.load =function(appNames, options, statusObj){
        layout.message = new MWF.xDesktop.MessageMobile();
        layout.message.load();

        layout.apps = [];
        layout.node = $("layout");
        var appName=appNames, m_status=statusObj, option=options;

        var topWindow = window.opener;
        if (topWindow){
            try{
                if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                if (!option)  option = topWindow.layout.desktop.openBrowserOption;
            }catch(e){}
        }
        layout.openApplication(null, appName, option||{}, m_status);
    }

})(layout);