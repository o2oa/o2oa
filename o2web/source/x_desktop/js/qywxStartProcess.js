layout = window.layout || {};
layout.desktop = layout;
layout.desktop.type = "app";
var locate = window.location;
layout.protocol = locate.protocol;
layout.inBrowser = true;
layout.session = layout.session || {};
layout.debugger = (locate.href.toString().indexOf("debugger") !== -1);
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
        if (options) { options.event = e; } else { options = { "event": e }; }
        var app = new appNamespace["Main"](layout.desktop, options);
        app.desktop = layout.desktop;
        app.status = statusObj;
        app.inBrowser = !!(inBrowser || layout.inBrowser);

        if (layout.desktop.type === "layout") {
            app.appId = (options.appId) ? options.appId : ((appNamespace.options.multitask) ? appName + "-" + (new o2.widget.UUID()) : appName);
            app.options.appId = app.appId;

            if (!taskitem) taskitem = layout.desktop.createTaskItem(app);
            app.taskitem = taskitem;
            app.taskitem.app = app;

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

            //app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
        } else {
            app.load(true);
            layout.app = app;
        }



        var mask = document.getElementById("appContentMask");
        if (mask) mask.destroy();
    };

    layout.openApplication = function (e, appNames, options, statusObj, inBrowser, taskitem, notCurrent) {

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
    };

    layout.startProcess = function (appId, pId, redirect) {
        MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(pId, appId, function (json) {
  
            if (json.data) {
                MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function () {
                    var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, layout.app, {
                        "workData": {},
                        "identity": null,
                        "latest": false,
                        "onStarted": function (data, title, processName) {

                            if (data.work){
                                layout.startProcessDraft(data, title, processName, redirect);
                            }else{
                                layout.startProcessInstance(data, title, processName, redirect);
                            }
                            
                        }.bind(this)
  
                    });
                    var mask = document.getElementById("loaddingArea");
                    if (mask) mask.destroy();
                    starter.load();
                }.bind(this));
            }
        }.bind(this));
    };
  
    layout.startProcessDraft = function(data, title, processName, redirect){
        o2.require("o2.widget.UUID", function () {
            var work = data.work;
            var options = {"draft": work, "appId": "process.Work"+(new o2.widget.UUID).toString(), "desktopReload": false};
            //先修改当前url为配置的门户地址
            if (redirect) {
                history.replaceState(null, "startProcess", redirect);
  
            } else {
                history.replaceState(null, "startProcess", "../x_desktop/appMobile.html?app=process.TaskCenter");
                
            }

            // layout.openApplication(null, "process.Work", options);
            layout.openWorkIn(options);
        });
        
    };
    layout.startProcessInstance = function(data, title, processName, redirect){
        var currentTask = [];
        data.each(function (work) {
            if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
        }.bind(this));
  
        if (currentTask.length == 1) {
            var options = { "workId": currentTask[0], "appId": currentTask[0] };
  
            //先修改当前url为配置的门户地址
            if (redirect) {
                history.replaceState(null, "startProcess", redirect);
  
            } else {
                history.replaceState(null, "startProcess", "../x_desktop/appMobile.html?app=process.TaskCenter");
            }
  
            // layout.openApplication(null, "process.Work", options);
            layout.openWorkIn(options);
  
        } else { }
    };
    layout.openWorkIn =  function(options){
        o2.requireApp("Common", "", function() {
            var uri = new URI(window.location.href);
            var redirectlink = uri.getData("redirectlink");
            if (!redirectlink) {
                redirectlink = encodeURIComponent(locate.pathname + locate.search);
            } else {
                redirectlink = encodeURIComponent(redirectlink);
            }
            var appName="process.Work", m_status=null;
            options.redirectlink = redirectlink; 
            layout.app = null;//创建工作界面
            layout.openApplication(null, appName, options, m_status);
        }, true);
    };
    layout.notice = function (content, type, target, where, offset) {
        if (!where) where = { "x": "right", "y": "top" };
        if (!target) target = this.content;
        if (!type) type = "ok";
        var noticeTarget = target || $(document.body);
        var off = offset;
        if (!off) {
            off = {
                x: 10,
                y: where.y.toString().toLowerCase() == "bottom" ? 10 : 10
            };
        }
  
        new mBox.Notice({
            type: type,
            position: where,
            move: false,
            target: noticeTarget,
            delayClose: (type == "error") ? 10000 : 5000,
            offset: off,
            content: content
        });
    };
  

})(layout);

o2.addReady(function () {

    //兼容方法
    Element.implement({
        "makeLnk": function (options) { }
    });
    layout.desktop.addEvent = function (type, e, d) {
        window.addEvent(type, e, d);
    };
    layout.desktop.addEvents = function (e) {
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
    o2.getJSON("../x_desktop/res/config/config.json", function (config) {
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

                    o2.load(["../o2_lib/mootools/plugin/mBox.min.js"], function () { _loadProgressBar(); isLoadedA = true; _check(); });
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
                        // while (layout.readys && layout.readys.length) {
                        //     layout.readys.shift().apply(window);
                        // }

                        var uri = locate.href.toURI();
                        var redirect = uri.getData("redirect");
                        var processId = uri.getData("processId");
                        var applicationId = uri.getData("appId");
                        layout.content = $(document.body);
                        layout.app = layout;
                        layout.startProcess(applicationId, processId, redirect);

                    });
                };

                _loadContent();
            })(layout);
        };

        // 是否ip
        var _isIp = function(ip) {
            var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
            return reg.test(ip);
        };
        //修改支持x-token
        var uri = new URI(window.location.href);
        var options = uri.get("data");
        if (options[o2.tokenName]) {
            // 删除
            Cookie.dispose(o2.tokenName);
            // 写入
            var host = window.location.host; // 域名 
            var domain = null;
            if (_isIp(host)) {
                domain = host;
            }else {
                if (host.indexOf(".") > 0) {
                    domain = host.substring(host.indexOf(".")); // 上级域名 如 .o2oa.net
                }
            }
            if (domain) {
                Cookie.write(o2.tokenName, options[o2.tokenName], {domain: domain, path:"/"});
            }else {
                Cookie.write(o2.tokenName, options[o2.tokenName]);
            }
        }

        //先判断用户是否登录
        o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
            //已经登录
            _loadProgressBar();
            _loadApp(json);
        }.bind(this), function (json) {
            _loadProgressBar();

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

                o2.load(["../o2_lib/mootools/plugin/mBox.min.js"], function () { _loadProgressBar(); isLoadedA = true; _check(); });
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
                layout.openLoginQywx();
            });
        });

        layout.openLoginQywx = function () {

            var uri = locate.href.toURI();

            MWF.require("MWF.xDesktop.Actions.RestActions", function () {

                var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                action.getActions = function (actionCallback) {
                    this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" } };
                    if (actionCallback) actionCallback();
                };
                action.invoke({
                    "name": "sso", "async": true, "parameter": { "code": uri.getData("code") }, "success": function (json) {
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
            });
        };

    };
});
