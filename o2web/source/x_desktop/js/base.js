layout = window.layout || {};
layout.desktop = layout;
layout.desktop.type = "app";
var locate = window.location;
layout.protocol = locate.protocol;
layout.inBrowser = true;
layout.session = layout.session || {};
layout.debugger = (locate.href.toString().indexOf("debugger") !== -1);
layout.anonymous = (locate.href.toString().indexOf("anonymous") !== -1);
o2.xApplication = o2.xApplication || {};

o2.xDesktop = o2.xDesktop || {};
o2.xDesktop.requireApp = function (module, clazz, callback, async) {
    o2.requireApp(module, clazz, callback, async);
};

(function (layout) {
    layout.readys = [];
    layout.addReady = function () {
        for (var i = 0; i < arguments.length; i++) {
            if (o2.typeOf(arguments[i]) === "function") layout.readys.push(arguments[i]);
        }
    };
    var _requireApp = function (appNames, callback, clazzName) {
        var appPath = appNames.split(".");
        var baseObject = o2.xApplication;
        appPath.each(function (path, i) {
            if (i < (appPath.length - 1)) {
                baseObject[path] = baseObject[path] || {};
            } else {
                baseObject[path] = baseObject[path] || { "options": Object.clone(o2.xApplication.Common.options) };
            }
            baseObject = baseObject[path];
        }.bind(this));
        if (!baseObject.options) baseObject.options = Object.clone(o2.xApplication.Common.options);

        var _lpLoaded = false;
        o2.xDesktop.requireApp(appNames, "lp." + o2.language, {
            "failure": function () {
                o2.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this)
        }, false);

        o2.xDesktop.requireApp(appNames, clazzName, function () {
            if (callback) callback(baseObject);
        });
    };
    var _createNewApplication = function (e, appNamespace, appName, options, statusObj, inBrowser, taskitem, notCurrent) {
        if (options){ options.event = e; }else{ options = {"event": e}; }
        var app = new appNamespace["Main"](layout.desktop, options);
        app.desktop = layout.desktop;
        app.status = statusObj;
        app.inBrowser = !!(inBrowser || layout.inBrowser);

        if (layout.desktop.type === "layout"){
            app.appId = (options.appId) ? options.appId : ((appNamespace.options.multitask) ? appName + "-" + (new o2.widget.UUID()) : appName);
            app.options.appId = app.appId;

            if (!taskitem) taskitem = layout.desktop.createTaskItem(app);
            app.taskitem = taskitem;
            app.taskitem.app = app;

            // if (layout.viewMode=="Default"){
            //     if (!notCurrent){
            //         app.load(!notCurrent);
            //     }else{
            //         app.isLoadApplication = false;
            //     }
            // }else{
            //     app.load(!notCurrent);
            // }
            app.isLoadApplication = true;
            app.load(!notCurrent);

            if (!layout.desktop.apps) layout.desktop.apps = {};
            if (layout.desktop.apps[app.appId]){
                var tmpApp = layout.desktop.apps[app.appId];

            }else{
                layout.desktop.apps[app.appId] = app;
            }



            layout.desktop.appArr.push(app);
            layout.desktop.appCurrentList.push(app);
            if (!notCurrent) layout.desktop.currentApp = app;

            //app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
        }else{
            app.load(true);
            layout.app = app;
        }



        var mask = document.getElementById("appContentMask");
        if (mask) mask.destroy();
    };

    var _openWorkAndroid = function (options) {
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
    var _openWorkIOS = function (options) {
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
    var _openWorkHTML = function (options) {
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if (!redirectlink) {
            redirectlink = encodeURIComponent(locate.pathname + locate.search);
        } else {
            redirectlink = encodeURIComponent(redirectlink);
        }
        if (options.workId) {
            window.location = "workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink;
        } else if (options.workCompletedId) {
            window.location = "workmobilewithaction.html?workcompletedid=" + options.workCompletedId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink;
        }
    };
    var _openWork = function (options) {
        if (!_openWorkAndroid(options)) if (!_openWorkIOS(options)) _openWorkHTML(options);
    };
    var _openDocument = function (appNames, options, statusObj) {
        var title = typeOf(options) === "object" ? (options.docTitle || options.title) : "";
        title = title || "";
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsDocument) {
            window.o2android.openO2CmsDocument(options.documentId, title);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument) {
            window.webkit.messageHandlers.openO2CmsDocument.postMessage({ "docId": options.documentId, "docTitle": title });
        } else {
            window.location = "appMobile.html?" + par + ((layout.debugger) ? "&debugger" : "");
        }
    };
    var _openCms = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsApplication) {
            window.o2android.openO2CmsApplication(options.columnId, options.title || "");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication) {
            window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
        } else {
            window.location = "appMobile.html?" + par + ((layout.debugger) ? "&debugger" : "");
        }
    };
    var _openMeeting = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Meeting) {
            window.o2android.openO2Meeting("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting) {
            window.webkit.messageHandlers.openO2Meeting.postMessage("");
        } else {
            window.location = "appMobile.html?" + par + ((layout.debugger) ? "&debugger" : "");
        }
    };

    var _openCalendar = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Calendar) {
            window.o2android.openO2Calendar("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar) {
            window.webkit.messageHandlers.openO2Calendar.postMessage("");
        } else {
            window.location = "appMobile.html?" + par + ((layout.debugger) ? "&debugger" : "");
        }
    };
    var _openTaskCenter = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        var tab = ((options && options.navi) ? options.navi : "task").toLowerCase();
        if (tab === "done") tab = "taskCompleted";
        if (tab === "readed") tab = "readCompleted";

        if (window.o2android && window.o2android.openO2WorkSpace) {
            window.o2android.openO2WorkSpace(tab);
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2WorkSpace) {
            window.webkit.messageHandlers.openO2WorkSpace.postMessage(tab);
        } else {
            window.location = "appMobile.html?" + par + ((layout.debugger) ? "&debugger" : "");
        }
    };

    var _openApplicationMobile = function (appNames, options, statusObj) {
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
                var uri = new URI(window.location.href);
                var optionsStr = uri.getData("option");
                var statusStr = uri.getData("status");
                window.location = "appMobile.html?app=" + appNames + "&option=" + (optionsStr || "") + "&status=" + (statusStr || "") + ((layout.debugger) ? "&debugger" : "");
        }
    };

    layout.openApplication = function (e, appNames, options, statusObj, inBrowser, taskitem, notCurrent) {
        if (appNames.substring(0, 4)==="@url"){
            var url = appNames.replace(/\@url\:/i, "");
            var a = new Element("a", {"href": url, "target": "_blank"});
            a.click();
            a.destroy();
            a = null;
            return true;
        }

        if (layout.app) {
            if (layout.mobile) {
                _openApplicationMobile(appNames, options, statusObj);
            } else {
                var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");

                if (layout.app.$openWithSelf) {
                    return window.location = "app.html?" + par + ((layout.debugger) ? "&debugger" : "");
                } else {
                    return window.open("app.html?" + par + ((layout.debugger) ? "&debugger" : ""), par);
                }
            }
        } else {
            var appPath = appNames.split(".");
            var appName = appPath[appPath.length - 1];
            _requireApp(appNames, function (appNamespace) {
                var appId = (options && options.appId) ? options.appId : ((appNamespace.options.multitask) ? "" : appName);

                if (appId && layout.desktop.apps && layout.desktop.apps[appId] && layout.desktop.apps[appId].window){
                    layout.desktop.apps[appId].setCurrent();
                }else{
                    if (options) options.appId = appId;
                    _createNewApplication(e, appNamespace, appName, (options || {"appId": appId}), statusObj, inBrowser, taskitem, notCurrent);
                }
                // if (appNamespace.options.multitask){
                //     if (options && options.appId){
                //         if (layout.desktop.apps[options.appId]){
                //             layout.desktop.apps[options.appId].setCurrent();
                //         }else {
                //             _createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                //         }
                //     }else{
                //         _createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                //     }
                // }else{
                //     if (layout.desktop.apps[appName]){
                //         layout.desktop.apps[appName].setCurrent();
                //     }else{
                //         _createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                //     }
                // }
                // _createNewApplication(e, appNamespace, appName, (options || {}), statusObj, inBrowser);
            }.bind(this));
        }
    };

    layout.refreshApp = function (app) {
        var status = app.recordStatus();

        var uri = new URI(window.location.href);
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var statusStr = uri.getData("status");
        if (status) statusStr = JSON.encode(status);

        var port = uri.get("port");
        window.location = uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port + "/" : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "") + ((layout.debugger) ? "&debugger" : "");
    };

    layout.load = function (appNames, options, statusObj) {
        // layout.message = new o2.xDesktop.MessageMobile();
        // layout.message.load();

        layout.apps = [];
        layout.node = $("layout");
        var appName = appNames, m_status = statusObj, option = options;

        var topWindow = window.opener;
        if (topWindow) {
            try {
                if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                if (!option) option = topWindow.layout.desktop.openBrowserOption;
            } catch (e) { }
        }
        layout.openApplication(null, appName, option || {}, m_status);
    };

})(layout);

o2.addReady(function () {
    // layout.desktop = new (new Class({Implements: [Options, Events]}))();
    // layout.desktop.openApplication = layout.openApplication;
    // layout.desktop.refreshApp = layout.refreshApp;
    // layout.desktop.load = layout.load;

    //兼容方法
    Element.implement({
        "makeLnk": function (options) { }
    });
    layout.desktop.addEvent = function(type, e, d){
        window.addEvent(type, e, d);
    };
    layout.desktop.addEvents = function(e){
        window.addEvents(e);
    };

    var loadingNode = $("loaddingArea");
    var loadeds = 0;
    var loadCount = 16;
    var size = document.body.getSize();
    var _closeLoadingNode = function () {
        if (loadingNode) {
            loadingNode.destroy();
            loadingNode = null;
        }

    };
    var _loadProgressBar = function (complete) {
        if (loadingNode) {
            if (complete) {
                loadingNode.setStyles({ "width": "" + size.x + "px" });
                //loadingNode.set('morph', {duration: 100}).morph({"width": ""+size.x+"px"});
                window.setTimeout(_closeLoadingNode, 500);
            } else {
                loadeds++;
                var p = (loadeds / loadCount) * size.x;
                loadingNode.setStyles({ "width": "" + p + "px" });
                //loadingNode.set('morph', {duration: 100}).morph({"width": ""+p+"px"});
                if (loadeds >= loadCount) window.setTimeout(_closeLoadingNode, 500);
            }
        }
    };

    //异步载入必要模块
    layout.config = null;
    var configLoaded = false;
    var lpLoaded = false;
    var commonLoaded = false;
    var lp = o2.session.path + "/lp/" + o2.language + ".js";
    o2.load(lp, function () {
        _loadProgressBar();
        lpLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    });
    var modules = ["o2.xDesktop.Common", "o2.xDesktop.Actions.RestActions", "o2.xAction.RestActions"];
    o2.require(modules, {
        "onSuccess": function () {
            commonLoaded = true;
            if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
        },
        "onEvery": function () {
            _loadProgressBar();
        }
    });
    o2.getJSON("/x_desktop/res/config/config.json", function (config) {
        _loadProgressBar();
        layout.config = config;
        configLoaded = true
        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    });

    var _getDistribute = function (callback) {
        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }
        o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
            layout.serviceAddressList = service;
            layout.centerServer = center;
            layout.desktop.serviceAddressList = service;
            layout.desktop.centerServer = center;
            _loadProgressBar();
            if (callback) callback();
        }.bind(this));
    };

    var _load = function () {
        var _loadApp = function (json) {
            //用户已经登录
            layout.user = json.data;
            //layout.session = {};
            layout.session.user = json.data;
            layout.session.token = json.data.token;
            layout.desktop.session = layout.session;
            (function (layout) {
                var _loadResource = function (callback) {
                    var isLoadedA = false;
                    var isLoadedB = false;
                    //var isLoadedC = false;

                    var modules = [
                        "o2.xDesktop.Dialog",
                        "o2.xDesktop.UserData",
                        "o2.xDesktop.Access",
                        "o2.widget.UUID",
                        "o2.xDesktop.Menu",
                        "o2.xDesktop.Authentication",
                        // "o2.xDesktop.shortcut",
                        "o2.widget.PinYin",
                        "o2.xDesktop.Access"
                        // "o2.xDesktop.MessageMobile"
                    ];
                    //o2.xDesktop.requireApp("Common", "", null, false);
                    var _check = function () { if (isLoadedA && isLoadedB) if (callback) callback(); };

                    o2.load(["/o2_lib/mootools/plugin/mBox.min.js"], function () { _loadProgressBar(); isLoadedA = true; _check(); });
                    o2.require("o2.widget.Common", function () {
                        _loadProgressBar();
                        o2.require(modules, {
                            "onSuccess": function () {
                                o2.requireApp("Common", "", function () { _loadProgressBar(); isLoadedB = true; _check(); })
                            },
                            "onEvery": function () {
                                _loadProgressBar();
                            }
                        });
                    });
                };

                var _loadContent = function () {
                    _loadResource(function () {
                        _loadProgressBar(true);
                        while (layout.readys && layout.readys.length) {
                            layout.readys.shift().apply(window);
                        }

                    });
                };

                _loadContent();
            })(layout);
        };
        //先判断用户是否登录
        o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
            //已经登录
            _loadProgressBar();
            _loadApp(json);
        }.bind(this), function (json) {
            _loadProgressBar();
            //允许匿名访问
            if (layout.anonymous) {
                _loadProgressBar(true);
                _loadApp({
                    user: "anonymous",
                    session: {
                        user: {
                            name: "anonymous",
                            roleList: []
                        }
                    }
                });
            } else {
                //用户未经登录
                //打开登录页面
                var _loadResource = function (callback) {
                    var isLoadedA = false;
                    var isLoadedB = false;
                    //var isLoadedC = false;

                    //var lp = o2.session.path+"/lp/"+o2.language+".js";
                    var modules = [
                        "o2.xDesktop.Dialog",
                        "o2.xDesktop.UserData",
                        "o2.xDesktop.Access",
                        "o2.widget.UUID",
                        "o2.xDesktop.Menu",
                        //"o2.xDesktop.shortcut",
                        "o2.widget.PinYin",
                        "o2.xDesktop.Access",
                        //"o2.xDesktop.MessageMobile"
                    ];
                    //o2.xDesktop.requireApp("Common", "", null, false);
                    var _check = function () { if (isLoadedA && isLoadedB) if (callback) callback(); };

                    o2.load(["/o2_lib/mootools/plugin/mBox.min.js"], function () { _loadProgressBar(); isLoadedA = true; _check(); });
                    o2.require("o2.widget.Common", function () {
                        _loadProgressBar();
                        o2.require(modules, {
                            "onSuccess": function () {
                                o2.requireApp("Common", "", function () { isLoadedB = true; _check(); })
                            },
                            "onEvery": function () {
                                _loadProgressBar();
                            }
                        });
                    });
                };
                _loadResource(function () {
                    _loadProgressBar(true);
                    layout.openLogin();
                });
            }
        });

        layout.openLogin = function () {
            o2.require("o2.widget.Common", null, false);
            o2.require("o2.xDesktop.Authentication", function () {
                layout.authentication = new o2.xDesktop.Authentication({
                    "style": "flat",
                    "onLogin": _load.bind(layout)
                });
                layout.authentication.loadLogin(document.body);
                var loadingNode = $("browser_loading");
                if (loadingNode) loadingNode.fade("out");
            });
        };
    };
});
