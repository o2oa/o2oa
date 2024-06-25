if (!window.layout || !layout.desktop || !layout.addReady) {
    layout = window.layout || {};
    layout.desktop = layout;
    layout.desktop.type = "app";
    layout.apps = [];
    var locate = window.location;
    layout.protocol = locate.protocol;
    layout.port = locate.port;
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
                if (o2.typeOf(arguments[i]) === "function") {
                    if (layout.isReady) {
                        arguments[i].apply(window);
                    } else {
                        layout.readys.push(arguments[i]);
                    }
                }
            }
        };
        var _requireApp = function (appNames, callback, clazzName) {
            var appPath = appNames.split(".");
            var baseObject = o2.xApplication;
            appPath.each(function (path, i) {
                if (i < (appPath.length - 1)) {
                    baseObject[path] = baseObject[path] || {};
                } else {
                    baseObject[path] = baseObject[path] || {"options": Object.clone(o2.xApplication.Common.options)};
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
        var _createNewApplication = function (e, appNamespace, appName, options, statusObj, inBrowser, taskitem, notCurrent, node) {
            if (options) {
                options.event = e;
            } else {
                options = {"event": e};
            }
            var app = new appNamespace["Main"](layout.desktop, options);
            app.desktop = layout.desktop;
            app.status = statusObj;
            app.inBrowser = !!(inBrowser || layout.inBrowser);
            app.windowNode = node;

            if (layout.desktop.type === "layout") {
                app.appId = (options.appId) ? options.appId : ((appNamespace.options.multitask) ? appName + "-" + (new o2.widget.UUID()) : appName);
                app.options.appId = app.appId;

                if (layout.desktop.createTaskItem) {
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

                layout.desktop.appArr.push(app);
                layout.desktop.appCurrentList.push(app);
                if (!notCurrent) layout.desktop.currentApp = app;
            } else {
                app.load(true);
                layout.app = app;
            }

            var mask = document.getElementById("appContentMask");
            if (mask) mask.destroy();
        };

        var _openWorkAndroid = function (options) {
            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2Work",
                    data: {
                        title : options.title || ""
                    }
                };
                if (options.workId) {
                    body.data.workId = options.workId;
                } else if (options.workCompletedId) {
                    body.data.workCompletedId = options.workCompletedId;
                } else if (options.draftId) {
                    body.data.draftId = options.draftId;
                }
                window.o2android.postMessage(JSON.stringify(body));
                return true;
            }
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
                        "draftId": options.draftId,
                        "title": options.title || options.docTitle || ""
                    });
                } else if (options.workCompletedId) {
                    window.webkit.messageHandlers.openO2Work.postMessage({
                        "work": "",
                        "workCompleted": options.workCompletedId,
                        "draftId": options.draftId,
                        "title": options.title || options.docTitle || ""
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
            var docurl = "../x_desktop/workmobilewithaction.html".toURI();
            if (options.draft) {
                var par = "draft=" + encodeURIComponent(JSON.stringify(options.draft));
                docurl = "../x_desktop/workmobilewithaction.html?" + par;
            } else {
                docurl = docurl.setData(options).toString();
            }
            var job = (options.jobid || options.jobId || options.job);
            if (job) docurl += ((docurl.indexOf("?") != -1) ? "&" : "?") + "jobid=" + job;
            docurl += ((redirectlink) ? "&redirectlink=" + redirectlink : "");
            docurl += ((layout.debugger) ? "&debugger" : "");
            //使用相对路径不需要filterUrl
            //window.location = o2.filterUrl(docurl);
            window.location = docurl;
        };
        var _openWork = function (options) {
            if (!_openWorkAndroid(options)) if (!_openWorkIOS(options)) _openWorkHTML(options);
        };
        var _openDocument = function (appNames, options, statusObj) {
            var title = typeOf(options) === "object" ? (options.docTitle || options.title) : "";
            title = title || "";
            var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2CmsDocument",
                    data: {
                        docId : options.documentId,
                        title: title,
                        options: options
                    }
                };
                window.o2android.postMessage(JSON.stringify(body));
            } else if (window.o2android && window.o2android.openO2CmsDocumentV2) {
                window.o2android.openO2CmsDocumentV2(options.documentId, title, JSON.stringify(options));
            } else if (window.o2android && window.o2android.openO2CmsDocument) {
                window.o2android.openO2CmsDocument(options.documentId, title);
            } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument) {
                window.webkit.messageHandlers.openO2CmsDocument.postMessage({
                    "docId": options.documentId,
                    "docTitle": title, "options": JSON.stringify(options)
                });
            } else {
                window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
            }
        };
        var _openCms = function (appNames, options, statusObj) {
            var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2CmsApplication",
                    data: {
                        appId : options.columnId,
                        title: options.title || "",
                        categoryId: options.categoryId || ""
                    }
                };
                window.o2android.postMessage(JSON.stringify(body));
            } else if (window.o2android && window.o2android.openO2CmsApplication) {
                window.o2android.openO2CmsApplication(options.columnId, options.title || "");
            } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication) {
                window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
            } else {
                window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
            }
        };
        var _openMeeting = function (appNames, options, statusObj) {
            var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2Meeting",
                    data: {}
                };
                window.o2android.postMessage(JSON.stringify(body));
            } else if (window.o2android && window.o2android.openO2Meeting) {
                window.o2android.openO2Meeting("");
            } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting) {
                window.webkit.messageHandlers.openO2Meeting.postMessage("");
            } else {
                window.location = o2.filterUrl("../x_desktop/appMobile.html?" + par + ((layout.debugger) ? "&debugger" : ""));
            }
        };

        var _openCalendar = function (appNames, options, statusObj) {
            var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2Calendar",
                    data: {}
                };
                window.o2android.postMessage(JSON.stringify(body));
            } else if (window.o2android && window.o2android.openO2Calendar) {
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

            if (window.o2android && window.o2android.postMessage) {
                var body = {
                    type: "openO2WorkSpace",
                    data: { type: tab }
                };
                window.o2android.postMessage(JSON.stringify(body));
            } else if (window.o2android && window.o2android.openO2WorkSpace) {
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
                    var optionsStr, statusStr;
                    if( options || statusObj){
                        optionsStr = encodeURIComponent((options) ? JSON.encode(options) : "")
                        statusStr = encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "")
                    }else{
                        var uri = new URI(window.location.href);
                        optionsStr = uri.getData("option");
                        statusStr = uri.getData("status");
                    }
                    window.location = o2.filterUrl("../x_desktop/appMobile.html?app=" + appNames + "&option=" + (optionsStr || "") + "&status=" + (statusStr || "") + ((layout.debugger) ? "&debugger" : ""));
            }
        };

        var _openWindow = function (url, par) {
            var a = new Element("a", {
                "href": url,
                "target": par
            });
            a.click();
            a.destroy();
        };
        var _openApplicationPC = function (appNames, options, statusObj) {
            if (options) delete options.docTitle;
            var par = "app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent((statusObj) ? JSON.encode(statusObj) : "") + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
            switch (appNames) {
                case "process.Work":
                    var url = "../x_desktop/work.html";
                    if (options.draft) {
                        url = "../x_desktop/app.html?" + par;
                    } else {
                        Object.keys(options).forEach(function (k) {
                            if (options[k]) url += ((url.indexOf("?") != -1) ? "&" : "?") + k + "=" + options[k];
                        });
                        //url = url.setData(options).toString();
                    }
                    var job = (options.jobid || options.jobId || options.job);
                    if (job) url += ((url.indexOf("?") != -1) ? "&" : "?") + "jobid=" + job;
                    url += ((layout.debugger) ? "&debugger" : "");

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
                    url += ((layout.debugger) ? "&debugger" : "");
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
                    if (layout.app.$openWithSelf) {
                        return window.location = o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : ""));
                    } else {
                        return window.open(o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : "")), par);
                    }
            }
        };

        layout.openApplication = function (e, appNames, options, statusObj, inBrowser, taskitem, notCurrent, node) {
            if (appNames.substring(0, 4) === "@url") {
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
                    return _openApplicationPC(appNames, options, statusObj);
                }
            } else {
                var appPath = appNames.split(".");
                var appName = appPath[appPath.length - 1];
                _requireApp(appNames, function (appNamespace) {
                    var appId = (options && options.appId) ? options.appId : ((appNamespace.options.multitask) ? "" : appName);

                    if (appId && layout.desktop.apps && layout.desktop.apps[appId]) {
                        layout.desktop.apps[appId].setCurrent();
                    } else {
                        if (options) options.appId = appId;
                        if (appNamespace.loading && appNamespace.loading.then){

                            if (!layout.desktop.loadingAppIdArr) layout.desktop.loadingAppIdArr = [];
                            if( !layout.desktop.loadingAppIdArr.contains( appId ) ){

                                if( appId )layout.desktop.loadingAppIdArr.push(appId);

                                appNamespace.loading.then(function(){
                                    _createNewApplication(e, appNamespace, appName, (options || {"appId": appId}), statusObj, inBrowser, taskitem, notCurrent, node);

                                    if( appId )layout.desktop.loadingAppIdArr.erase(appId);
                                });
                            }
                        }else{
                            _createNewApplication(e, appNamespace, appName, (options || {"appId": appId}), statusObj, inBrowser, taskitem, notCurrent, node);
                        }
                    }
                }.bind(this));
            }
        };

        layout.refreshApp = function (app) {
            var status = app.recordStatus();

            var _uri = new URI(window.location.href);
            var appNames = _uri.getData("app");
            var optionsStr = _uri.getData("option");
            var statusStr = _uri.getData("status");
            if (status) statusStr = JSON.encode(status);

            var port = _uri.get("port");
            var u = _uri.get("scheme") + "://" + _uri.get("host") + ((port && port != "80") ? ":" + port : "") + _uri.get("directory") + _uri.get("file") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((optionsStr) ? JSON.encode(optionsStr) : "") + ((layout.debugger) ? "&debugger" : "");
            u = o2.filterUrl(u);
            window.location = u;
        };

        layout.load = function (appNames, options, statusObj) {
            layout.apps = [];
            layout.node = $("layout") || $("appContent") || document.body;
            var appName = appNames, m_status = statusObj, option = options;

            var topWindow = window.opener;
            if (topWindow) {
                try {
                    if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                    if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option) option = topWindow.layout.desktop.openBrowserOption;
                } catch (e) {
                }
            }
            layout.openApplication(null, appName, option || {}, m_status);
        };

        layout.getFormDesignerStyle = function (callback) {
            if (!this.formDesignerStyle) {
                this.formDesignerStyle = "default";
                MWF.UD.getData("formDesignerStyle", function (json) {
                    if (json.data) {
                        var styles = JSON.decode(json.data);
                        this.formDesignerStyle = styles.style;
                    }
                    if (callback) callback();
                }.bind(this));
            } else {
                if (callback) callback();
            }
        }

    })(layout);

}
