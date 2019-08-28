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

    var loadingNode = $("loaddingArea");
    var loadeds = 0;
    var loadCount = 15;
    var size = document.body.getSize();
    var _loadProgressBar = function(){
        if (loadingNode){
            loadeds++;
            var p = (loadeds/loadCount)*size.x;
            loadingNode.set('morph', {duration: 300}).morph({"width": ""+p+"px"});
            if (loadeds>=loadCount) window.setTimeout(function(){loadingNode.destroy();}, 500);
        }
    };

    //异步载入必要模块
    layout.config = null;
    var modules = [ "MWF.xDesktop.Common", "MWF.xAction.RestActions" ];
    MWF.require(modules, {
        "onSuccess": function(){
            if (layout.config) _getDistribute(function(){ _load(); });
        },
        "onEvery": function(){
            _loadProgressBar();
        }
    });
    o2.getJSON("/x_desktop/res/config/config.json", function(config){
        _loadProgressBar();
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
                    var isLoadedB = false;
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

                    o2.load(["/o2_lib/mootools/plugin/mBox.min.js",lp], function(){_loadProgressBar(); isLoadedA = true; _check();});
                    o2.require("MWF.widget.Common", function(){
                        _loadProgressBar();
                        o2.require(modules, {
                            "onSuccess": function(){
                                o2.requireApp("Common", "", function(){_loadProgressBar(); isLoadedB = true; _check();})
                            },
                            "onEvery": function(){
                                _loadProgressBar();
                            }
                        });
                    });
                };

                var _loadContent =function(){
                    _loadResource(function(){
                        //window.status = window.status+layout.readys.length;
                        while (layout.readys && layout.readys.length){
                            layout.readys.shift().apply(window);
                        }
                        // for (var i=0; i<layout.readys.length; i++){
                        //     layout.readys[i].apply(window);
                        // }
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

                o2.load(["/o2_lib/mootools/plugin/mBox.min.js",lp], function(){_loadProgressBar(); isLoadedA = true; _check();});
                o2.require("MWF.widget.Common", function(){
                    _loadProgressBar();
                    o2.require(modules, {
                        "onSuccess":function(){
                            o2.requireApp("Common", "", function(){isLoadedB = true; _check();})
                        },
                        "onEvery": function(){
                            _loadProgressBar();
                        }
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

        var mask = document.getElementById("appContentMask");
        if (mask) mask.destroy();
    };

    var _openWorkAndroid = function(options){
        if (window.o2android && window.o2android.openO2Work) {
            if (options.workId) {
                window.o2android.openO2Work(options.workId, "", options.title || "");
            } else if (options.workCompletedId) {
                window.o2android.openO2Work("", options.workCompletedId, options.title || "");
            }
            return true;
        }
        return false;
    };
    var _openWorkIOS = function(options){
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Work) {
            if (options.workId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": options.workId,
                    "workCompleted": "",
                    "title": options.title || ""
                });
            } else if (options.workCompletedId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": "",
                    "workCompleted": options.workCompletedId,
                    "title": options.title || ""
                });
            }
            return true;
        }
        return false;
    };
    var _openWorkHTML = function(options){
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if (!redirectlink) {
            redirectlink = encodeURIComponent(locate.pathname + locate.search);
        } else {
            redirectlink = encodeURIComponent(redirectlink);
        }
        if (options.workId) {
            window.location = "workmobilewithaction.html?workid=" + options.workId + ((layout.debugger)? "&debugger":"") + "&redirectlink=" + redirectlink;
        } else if (options.workCompletedId) {
            window.location = "workmobilewithaction.html?workcompletedid=" + options.workCompletedId + ((layout.debugger)? "&debugger":"") + "&redirectlink=" + redirectlink;
        }
    };
    var _openWork = function(options){
        if (!_openWorkAndroid(options)) if (!_openWorkIOS(options)) _openWorkHTML(options);
    };
    var _openDocument = function(appNames, options, statusObj){
        var title = typeOf( options ) === "object" ? ( options.docTitle || options.title ) : "";
        title = title || "";
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsDocument){
            window.o2android.openO2CmsDocument(options.documentId, title);
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument){
            window.webkit.messageHandlers.openO2CmsDocument.postMessage({"docId":options.documentId,"docTitle":title});
        }else{
            window.location = "appMobile.html?"+par + ((layout.debugger)? "&debugger":"");
        }
    };
    var _openCms = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsApplication){
            window.o2android.openO2CmsApplication(options.columnId, options.title || "");
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication){
            window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
        }else{
            window.location = "appMobile.html?app="+par + ((layout.debugger)? "&debugger":"");
        }
    };
    var _openMeeting = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Meeting){
            window.o2android.openO2Meeting("");
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting){
            window.webkit.messageHandlers.openO2Meeting.postMessage("");
        }else{
            window.location = "appMobile.html?app="+par + ((layout.debugger)? "&debugger":"");
        }
    };

    var _openCalendar = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Calendar){
            window.o2android.openO2Calendar("");
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar){
            window.webkit.messageHandlers.openO2Calendar.postMessage("");
        }else{
            window.location = "appMobile.html?app="+par + ((layout.debugger)? "&debugger":"");
        }
    };
    var _openTaskCenter = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        var tab = ((options && options.navi) ? options.navi : "task").toLowerCase();
        if (tab==="done") tab = "taskCompleted";
        if (tab==="readed") tab = "readCompleted";

        if (window.o2android && window.o2android.openO2WorkSpace){
            window.o2android.openO2WorkSpace(tab);
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2WorkSpace){
            window.webkit.messageHandlers.openO2WorkSpace.postMessage(tab);
        }else{
            window.location = "appMobile.html?app="+par + ((layout.debugger)? "&debugger":"");
        }
    };

    var _openApplicationMobile = function(appNames, options, statusObj){
        switch (appNames) {
            case "process.Work":
                _openWork(options);
                break;
            case "cms.Document":
                _openDocument(appNames, options, statusObj);
                break;
            case "cms.Module":
                _openCms(appNames, options, statusObj);
                break;
            case "Meeting":
                _openMeeting(appNames, options, statusObj);
                break;
            case "Calendar":
                _openCalendar(appNames, options, statusObj);
                break;
            case "process.TaskCenter":
                _openTaskCenter(appNames, options, statusObj);
                break;
            default:
                window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "") + ((layout.debugger)? "&debugger":"");
        }
    };

    layout.openApplication = function(e, appNames, options, statusObj){
        if (layout.app){
            if (layout.mobile){
                _openApplicationMobile(appNames, options, statusObj);
            }else{
                var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
                return window.open("app.html?"+par + ((layout.debugger)? "&debugger":""), "_blank");
            }
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
        window.location = uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port + "/" : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "") + ((layout.debugger)? "&debugger":"");
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