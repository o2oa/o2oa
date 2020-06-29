layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
layout.mobile = true;

layout.desktop = layout;
layout.session = layout.session || {};

o2.addReady(function(){
    var href = locate.href;
    if (href.indexOf("debugger")!==-1) layout.debugger = true;
    var uri = new URI(href);
    var redirectTo = uri.getData("redirectTo");
    var optionsStr = (uri.getData("option"));
    var statusStr = (uri.getData("status"));

    var options = (optionsStr) ? JSON.decode(optionsStr) : null;
    var statusObj = (statusStr) ? JSON.decode(statusStr) : null;

    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], {"sequence": true}, function(){
        //MWF.defaultPath = "../x_desktop" + MWF.defaultPath;
        MWF.loadLP("zh-cn");

        // MWF.require("MWF.widget.Mask", null, false);
        // layout.mask = new MWF.widget.Mask({"style": "desktop"});
        // layout.mask.load();

        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.xDesktop.requireApp("Common", "", null, false);
            MWF.require("MWF.xDesktop.Authentication", null, false);

            (function () {
                layout.requireApp = function(appNames, callback, clazzName){
                    var appPath = appNames.split(".");
                    var appName = appPath[appPath.length-1];
                    var appObject = "MWF.xApplication."+appNames;
                    var className = clazzName || "Main";
                    var appClass = appObject+"."+className;
                    var appLp = appObject+".lp."+MWF.language;
                    var baseObject = MWF.xApplication;
                    appPath.each(function(path, i){
                        if (i<(appPath.length-1)){
                            baseObject[path] = baseObject[path] || {};
                        }else {
                            baseObject[path] = baseObject[path] || {"options": Object.clone(MWF.xApplication.Common.options)};
                        }
                        baseObject = baseObject[path];
                    }.bind(this));
                    if (!baseObject.options) baseObject.options = Object.clone(MWF.xApplication.Common.options);

                    //MWF.xDesktop.requireApp(appNames, "lp."+MWF.language, null, false);
                    MWF.xDesktop.requireApp(appNames, "lp."+MWF.language, {
                        "onRequestFailure": function(){
                            MWF.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
                        }.bind(this),
                        "onSuccess": function(){}.bind(this)
                    }, false);

                    MWF.xDesktop.requireApp(appNames, clazzName, function(){
                        if (callback) callback(baseObject);
                    });
                };
                layout.openApplication = function(e, appNames, options, statusObj){
                    if (layout.app){
                        layout.desktop.openBrowserApp = appNames;
                        layout.desktop.openBrowserStatus = statusObj;
                        layout.desktop.openBrowserOption = options;

                        var optionsStr = JSON.encode(options);
                        var statusStr = JSON.encode(statusObj);
                        //window.open("appMobile.html", "_blank");
                        //layout.openApplication(null, appNames, option||{}, m_status);
                        var title = options.docTitle || "";
                        if (appNames==="process.Work"){
                            var uri = new URI(window.location.href);
                            var redirectlink = uri.getData("redirectlink");
                            if( !redirectlink ){
                                redirectlink = encodeURIComponent(locate.pathname + locate.search);
                            }else{
                                redirectlink = encodeURIComponent(redirectlink);
                            }
                            if( options.workId ){
                                window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workid="+options.workId+"&redirectlink="+redirectlink);
                            }else if( options.workCompletedId ){
                                window.location = o2.filterUrl("../x_desktop/workmobilewithaction.html?workcompletedid="+options.workCompletedId+"&redirectlink="+redirectlink);
                            }
                        }else{
                            window.location = o2.filterUrl("../x_desktop/appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || ""));
                        }
                    }else{
                        var appPath = appNames.split(".");
                        var appName = appPath[appPath.length-1];

                        layout.requireApp(appNames, function(appNamespace){
                            this.createNewApplication(e, appNamespace, appName, options, statusObj);
                        }.bind(this));
                    }
                };
                layout.createNewApplication = function(e, appNamespace, appName, options, statusObj){
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
                };

                layout.load = function () {
                    // MWF.require("MWF.xDesktop.MessageMobile", function(){
                    //     layout.message = new MWF.xDesktop.MessageMobile();
                    //     layout.message.load();
                    // }.bind(this));

                    var uri = href.toURI();
                    MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                        action.getActions = function (actionCallback) {
                            this.actions = {
                                "info": {"uri": "/jaxrs/zhengwudingding/info", "method": "POST"},
                                "auth": {"uri": "/jaxrs/zhengwudingding/code/{code}"}
                            };
                            if (actionCallback) actionCallback();
                        };
                        action.invoke({
                            "name": "info", "async": true, "data": {"url": href}, "success": function (json) {
                                // if (redirectTo){
                                //     document.all.appContent.src = (redirectTo+"&option="+optionsStr);
                                // }else{
                                //     document.all.appContent.src = "appMobile.html?app=process.TaskCenter";
                                // }
                                //if (document.all.appContentMask) document.all.appContentMask.destroy();

                                // debugger;
                                this.isAuthentication(function(){
                                    var appName = "portal.Portal";
                                    var m_status = statusObj;
                                    var option = options;
                                    layout.openApplication(null, appName, option||{}, m_status);

                                    if (document.all.appContentMask) document.all.appContentMask.destroy();
                                }.bind(this));
                                // return false;

                                var _config = json.data;
                                dd.config({
                                    agentId: _config.agentid,
                                    corpId: _config.corpId,
                                    timeStamp: _config.timeStamp,
                                    nonceStr: _config.nonceStr,
                                    signature: _config.signature,
                                    jsApiList: ['runtime.info']
                                });
                                //dd.ready(function() {
                                dd.biz.navigation.setTitle({
                                    title: ''
                                });
                                // dd.runtime.info({
                                //     onSuccess : function(info) {
                                //         logger.e('runtime info: ' + JSON.stringify(info));
                                //     },
                                //     onFail : function(err) {
                                //         logger.e('fail: ' + JSON.stringify(err));
                                //     }
                                // });
                                dd.runtime.permission.requestAuthCode({

                                    corpId: _config.corpId,
                                    onSuccess: function (info) {
                                        action.invoke({
                                            "name": "auth",
                                            "async": true,
                                            "parameter": {"code": info.code},
                                            "success": function (json) {

                                                // var appName = appNames;
                                                // var m_status = statusObj;
                                                // var option = options;
                                                // layout.openApplication(null, appName, option||{}, m_status);


                                                // if (redirectTo){
                                                //     document.all.appContent.src = (redirectTo+"&option="+option);
                                                // }else{
                                                //     document.all.appContent.src = "appMobile.html?app=process.TaskCenter";
                                                // }
                                                // if (document.all.appContentMask) document.all.appContentMask.destroy();

                                                // if (redirectTo){
                                                //     (redirectTo+"&option="+option).toURI().go();
                                                // }else{
                                                //     "appMobile.html?app=process.TaskCenter".toURI().go();
                                                // }

                                            }.bind(this),
                                            "failure": function (xhr, text, error) {
                                                "appMobile.html?app=process.TaskCenter".toURI().go();
                                            }.bind(this)
                                        });
                                    }.bind(this),
                                    onFail: function (err) {
                                    }
                                });
                                //});

                            }.bind(this), "failure": function (xhr, text, error) {

                            }.bind(this)
                        });
                    }.bind(this));
                };

                layout.isAuthentication = function(callback){
                    this.authentication = new MWF.xDesktop.Authentication({
                        "onLogin": layout.load.bind(layout)
                    });

                    var returnValue = true;
                    this.authentication.isAuthenticated(function(json){
                        this.user = json.data;
                        this.session = {};
                        this.session.user = json.data;
                        if (callback) callback();
                    }.bind(this), function(){
                        this.authentication.loadLogin(document.body);
                        if (layout.mask) layout.mask.hide();
                        if (document.all.appContentMask) document.all.appContentMask.destroy();
                        returnValue = false;
                    }.bind(this));
                    return returnValue;
                };

                layout.notice = function (content, type, target, where, offset) {
                    if (!where) where = {"x": "right", "y": "top"};
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

                MWF.getJSON("res/config/config.json", function (config) {
                    layout.config = config;
                    MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));
                    //layout.getServiceAddress(function(){
                    //    layout.load();
                    //});
                });

            })();

        });
    });
});
