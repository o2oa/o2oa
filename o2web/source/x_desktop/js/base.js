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
            "onFailure": function () {
                o2.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this)
        }, false);

        o2.xDesktop.requireApp(appNames, clazzName, function () {
            if (callback) callback(baseObject);
        });
    };
    var _createNewApplication = function (e, appNamespace, appName, options, statusObj, inBrowser, taskitem, notCurrent) {
        if (options) { options.event = e; } else { options = { "event": e }; }
        var app = new appNamespace["Main"](layout.desktop, options);
        app.desktop = layout.desktop;
        app.status = statusObj;
        app.inBrowser = !!(inBrowser || layout.inBrowser);

        if (layout.desktop.type === "layout") {
            app.appId = (options.appId) ? options.appId : ((appNamespace.options.multitask) ? appName + "-" + (new o2.widget.UUID()) : appName);
            app.options.appId = app.appId;

            if (layout.desktop.createTaskItem){
                if (!taskitem) taskitem = layout.desktop.createTaskItem(app);
                app.taskitem = taskitem;
                app.taskitem.app = app;
            }

            app.isLoadApplication = true;
            app.load(!notCurrent);

            if (!layout.desktop.apps) layout.desktop.apps = {};
            if (layout.desktop.apps[app.appId]) {
                var tmpApp = layout.desktop.apps[app.appId];

            } else {
                layout.desktop.apps[app.appId] = app;
            }

            //if (layout.desktop.appArr){
                layout.desktop.appArr.push(app);
                layout.desktop.appCurrentList.push(app);
                if (!notCurrent) layout.desktop.currentApp = app;
            //}
            //app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
        } else {
            app.load(true);
            layout.app = app;
        }



        var mask = document.getElementById("appContentMask");
        if (mask) mask.destroy();
    };

    var _openWorkAndroid = function (options) {
        if (window.o2android && window.o2android.openO2Work) {
            if (options.workId) {
                window.o2android.openO2Work(options.workId, "", options.title || options.docTitle || "");
            } else if (options.workCompletedId) {
                window.o2android.openO2Work("", options.workCompletedId, options.title || options.docTitle || "");
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
                    "title": options.title || options.docTitle || ""
                });
            } else if (options.workCompletedId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": "",
                    "workCompleted": options.workCompletedId,
                    "title": options.title || options.docTitle || ""
                });
            }
            return true;
        }
        return false;
    };
    var _openWorkHTML = function (options) {
        // var uri = new URI(window.location.href);
        // var redirectlink = uri.getData("redirectlink");
        // if (!redirectlink) {
        //     redirectlink = encodeURIComponent(locate.pathname + locate.search);
        // } else {
        //     redirectlink = encodeURIComponent(redirectlink);
        // }
        // if (options.workId) {
        //     window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
        // } else if (options.workCompletedId) {
        //     window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workcompletedid=" + options.workCompletedId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
        // }
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if (!redirectlink) {
            redirectlink = encodeURIComponent(locate.pathname + locate.search);
        } else {
            redirectlink = encodeURIComponent(redirectlink);
        }
        var docurl = "../x_desktop/workmobilewithaction.html".toURI();
        if (options.draft){
            var par = "draft="+encodeURIComponent(JSON.stringify(options.draft));
            docurl = "../x_desktop/workmobilewithaction.html?" + par;
        }else{
            docurl = docurl.setData(options).toString();
        }
        var job = (options.jobid || options.jobId || options.job);
        if (job) docurl += ((docurl.indexOf("?")!=-1) ? "&" : "?") + "jobid="+job;
        docurl += ((redirectlink) ? "&redirectlink=" + redirectlink : "");
        docurl +=((layout.debugger) ? "&debugger" : "");

        window.location = o2.filterUrl(docurl);
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
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };
    var _openCms = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsApplication) {
            window.o2android.openO2CmsApplication(options.columnId, options.title || "");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication) {
            window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };
    var _openMeeting = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Meeting) {
            window.o2android.openO2Meeting("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting) {
            window.webkit.messageHandlers.openO2Meeting.postMessage("");
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
        }
    };

    var _openCalendar = function (appNames, options, statusObj) {
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Calendar) {
            window.o2android.openO2Calendar("");
        } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar) {
            window.webkit.messageHandlers.openO2Calendar.postMessage("");
        } else {
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
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
            window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
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
                window.location = o2.filterUrl("../x_desktop/appMobile.html?app=" + appNames + "&option=" + (optionsStr || "") + "&status=" + (statusStr || "") + ((layout.debugger) ? "&debugger" : ""));
        }
    };

    var _openApplicationPC = function (appNames, options, statusObj) {
        if (options) delete options.docTitle;
        var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        switch (appNames) {
            case "process.Work":
                var url = "../x_desktop/work.html".toURI();
                if (options.draft){
                    url = "../x_desktop/app.html?" + par;
                }else{
                    url = url.setData(options).toString();
                }

                // if (options.workId) {
                //     url += "?workid=" + options.workId+"&"+par;
                //     //window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workid=" + options.workId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
                // } else if (options.workCompletedId) {
                //     url += "?workcompletedid=" + options.workCompletedId+"&"+par;;
                //     //window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workcompletedid=" + options.workCompletedId + ((layout.debugger) ? "&debugger" : "") + "&redirectlink=" + redirectlink);
                // } else if (options.draftId){
                //     url += "?draftId=" + options.draftId+"&"+par;;
                // } else if (options.draft) {
                //     url = "../x_desktop/app.html?" + par;
                // }
                var job = (options.jobid || options.jobId || options.job);
                if (job) url += ((url.indexOf("?")!=-1) ? "&" : "?") + "jobid="+job;
                url +=((layout.debugger) ? "&debugger" : "");

                if (layout.app.$openWithSelf) {
                    return window.location = o2.filterUrl(url);
                } else {
                    return window.open(o2.filterUrl(url), par);
                }
                break;
            case "cms.Document":
                // _openDocument(appNames, options, statusObj);
                var url = "../x_desktop/cmsdoc.html".toURI();
                url = url.setData(options).toString();
                url +=((layout.debugger) ? "&debugger" : "");
                if (layout.app.$openWithSelf) {
                    return window.location = o2.filterUrl(url);
                } else {
                    return window.open(o2.filterUrl(url), par);
                }
                break;
            // case "cms.Module":
            //     _openCms(appNames, options, statusObj);
            //     break;
            // case "Meeting":
            //     _openMeeting(appNames, options, statusObj);
            //     break;
            // case "Calendar":
            //     _openCalendar(appNames, options, statusObj);
            //     break;
            // case "process.TaskCenter":
            //     _openTaskCenter(appNames, options, statusObj);
            //     break;
            default:
                //var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
                if (layout.app.$openWithSelf) {
                    return window.location = o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : ""));
                } else {
                    return window.open(o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : "")), par);
                }
        }
    };

    layout.openApplication = function (e, appNames, options, statusObj, inBrowser, taskitem, notCurrent) {
        if (appNames.substring(0, 4) === "@url") {
            var url = appNames.replace(/\@url\:/i, "");
            var a = new Element("a", { "href": url, "target": "_blank" });
            a.click();
            a.destroy();
            a = null;
            return true;
        }

        if (layout.app) {
            if (layout.mobile) {
                _openApplicationMobile(appNames, options, statusObj);
            } else {
                return _openApplicationPC(appNames, options, statusObj);
                // var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
                //
                // if (layout.app.$openWithSelf) {
                //     return window.location = o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : ""));
                // } else {
                //     return window.open(o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : "")), par);
                // }
            }
        } else {
            var appPath = appNames.split(".");
            var appName = appPath[appPath.length - 1];
            _requireApp(appNames, function (appNamespace) {
                var appId = (options && options.appId) ? options.appId : ((appNamespace.options.multitask) ? "" : appName);

                //if (appId && layout.desktop.apps && layout.desktop.apps[appId] && layout.desktop.apps[appId].window){
                if (appId && layout.desktop.apps && layout.desktop.apps[appId]) {
                    layout.desktop.apps[appId].setCurrent();
                } else {
                    if (options) options.appId = appId;
                    _createNewApplication(e, appNamespace, appName, (options || { "appId": appId }), statusObj, inBrowser, taskitem, notCurrent);
                }
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
        window.location = o2.filterUrl(uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port + "/" : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((optionsStr) ? JSON.encode(optionsStr) : "") + ((layout.debugger) ? "&debugger" : ""));
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

    // layout.getAppUrlMapping = function(url){
    //     return this.getUrlMapping(url, "app");
    // };
    // layout.getCenterUrlMapping = function(url){
    //     return this.getUrlMapping(url,"center");
    // };
    // layout.getWebUrlMapping = function(url){
    //     return this.getUrlMapping(url, "web");
    // };
    // layout.getUrlMapping = function(url, type){
    //     var urlContextMapping = layout.config.urlMapping;
    //     if (urlContextMapping && urlContextMapping.app){
    //         var href = url || window.location.href.toString();
    //
    //         for (var k in urlContextMapping[type]){
    //             var regex = new RegExp(k);
    //             if (regex.test(href)){
    //                 return urlContextMapping[type][k];
    //             }
    //         }
    //     }
    //     return "";
    // };

})(layout);

o2.addReady(function () {
    // layout.desktop = new (new Class({Implements: [Options, Events]}))();
    // layout.desktop.openApplication = layout.openApplication;
    // layout.desktop.refreshApp = layout.refreshApp;
    // layout.desktop.load = layout.load;

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

    var loadingNode = (window.$) ? $("loaddingArea") : null;
    var loadeds = 0;
    var loadCount = 4;
    var size = (window.document && document.body) ? document.body.getSize() : null;
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
                    _loadProgressBar();
                    if (callback) callback();
                }.bind(this));
            }
        }else{
            o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                _setLayoutService(service, center);
                _loadProgressBar();
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

            _loadProgressBar(true);
            while (layout.readys && layout.readys.length) {
                console.log("load app ...")
                layout.readys.shift().apply(window);
            }
        };

        //修改支持x-token
        var uri = new URI(window.location.href);
        var options = uri.get("data");
        if (options["x-token"]) {
            Cookie.write("x-token", options["x-token"]);
        }

        layout.sessionPromise = new Promise(function(resolve, reject){
            o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
                if (resolve) resolve(json.data);
            }.bind(this), function (xhr, text, error) {
                if (reject) reject({"xhr": xhr, "text": text, "error": error});
            }.bind(this));
        });

        // layout.sessionPromise = {
        //     "resolveList": [],
        //     "rejectList": [],
        //     "init": function(resolve, reject){
        //         if (resolve) this.resolveList.push(resolve);
        //         if (reject) this.rejectList.push(reject);
        //         this.status = "pending";
        //         this.resolveReturn = this;
        //
        //         //先判断用户是否登录
        //         console.log("layout.sessionPromise.init")
        //         o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
        //             this.status = "fulfilled";
        //             this.resolveReturn = json.data;
        //             this.runResolve(this.resolveReturn);
        //         }.bind(this), function (xhr, text, error) {
        //             this.status = "rejected";
        //             this.resolveReturn = {"xhr": xhr, "text": text, "error": error};
        //             this.runReject(this.resolveReturn);
        //         }.bind(this));
        //     },
        //     "runResolve": function(json){
        //         while (this.resolveList.length){
        //             var r = this.resolveList.shift()(this.resolveReturn);
        //             if (r) this.resolveReturn = r;
        //         }
        //     },
        //     "runReject": function(json){
        //         while (this.rejectList.length){
        //             var r = this.rejectList.shift()(json);
        //             if (r) this.resolveReturn = r;
        //         }
        //     },
        //     "then": function(resolve, reject){
        //         if (resolve) this.resolveList.push(resolve);
        //         if (reject) this.rejectList.push(reject);
        //         switch (this.status){
        //             case "fulfilled":
        //                 this.runResolve();
        //                 break;
        //             case "rejected":
        //                 this.runReject();
        //                 break;
        //             default:
        //             //nothing
        //         }
        //         return this;
        //     }
        // }

        layout.sessionPromise.then(function(data){
            //已经登录
            layout.user = data;
            layout.session = layout.session || {};
            layout.session.user = data;
            layout.session.token = data.token;
            layout.desktop.session = layout.session;
            //_loadApp();
        }, function(){
            //允许匿名访问
            if (layout.anonymous) {
                var data = { name: "anonymous", roleList: [] };
                layout.user = data;
                layout.session = layout.session || {};
                layout.session.user = data;
                layout.session.token = data.token;
                layout.desktop.session = layout.session;
                //_loadApp();
            } else {
                _loadProgressBar(true);
                if (layout.yqwx) {
                    layout.openLoginQywx();
                } else {
                    layout.openLogin();
                }
            }
        });
        _loadApp();

        // //先判断用户是否登录
        // o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
        //     //已经登录
        //     //_loadProgressBar();
        //
        //     layout.user = json.data;
        //     layout.session = layout.session || {};
        //     layout.session.user = json.data;
        //     layout.session.token = json.data.token;
        //     layout.desktop.session = layout.session;
        //
        //     //_loadApp(json);
        // }.bind(this), function (json) {
        //     //_loadProgressBar();
        //     //允许匿名访问
        //     if (layout.anonymous) {
        //         //_loadProgressBar(true);
        //         // _loadApp({
        //         //     data : {
        //         //         user: "anonymous",
        //         //         session: {
        //         //             user: {
        //         //                 name: "anonymous",
        //         //                 roleList: []
        //         //             }
        //         //         }
        //         //     }
        //         // });
        //     } else {
        //         _loadProgressBar(true);
        //         if (layout.yqwx) {
        //             layout.openLoginQywx();
        //         } else {
        //             layout.openLogin();
        //         }
        //     }
        // });



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

        layout.openLoginQywx = function () {
            console.log("开始login。。。。。。。。。。。。。");
            var uri = locate.href.toURI();

            console.log("执行单点。。。。。。。。。。");
            var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
            action.getActions = function (actionCallback) {
                this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" } };
                if (actionCallback) actionCallback();
            };
            action.invoke({
                "name": "sso", "async": true, "parameter": { "code": uri.getData("code") }, "success": function (json) {
                    console.log("单点成功。");
                    console.log(json);
                    //基础数据。。。。
                    layout.session.user = json.data;
                    //
                    _load();

                }.bind(this), "failure": function (xhr, text, error) {
                    var n = document.getElementById("loaddingArea");
                    if (n) { n.destroy(); }
                    document.id("layout").set("html", "<div>企业微信单点异常！</div>")
                }.bind(this)
            });
        };
    };

    //异步载入必要模块
    layout.config = null;
    var configLoaded = false;
    var lpLoaded = false;
    var commonLoaded = false;
    var lp = o2.session.path + "/lp/" + o2.language + ".js";

    if (o2.session.isDebugger && (o2.session.isMobile || layout.mobile)) o2.load("../o2_lib/eruda/eruda.js");
    debugger;
    var loadModuls = function () {
        _loadProgressBar();
        lpLoaded = true;

        var modules = ["o2.xDesktop.$all"];
        o2.require(modules, {
            "onSuccess": function () {
                commonLoaded = true;
                if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
            },
            "onEvery": function () {
                _loadProgressBar();
            }
        });
    }

    if (!o2.LP){
        o2.load(lp, loadModuls);
    }else{
        loadModuls();
    }

    o2.getJSON("../x_desktop/res/config/config.json", function (config) {
        _loadProgressBar();
        layout.config = config;
        configLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    });
});
