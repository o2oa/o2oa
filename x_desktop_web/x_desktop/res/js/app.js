layout = window.layout || {};
var href = window.location.href;
if (href.indexOf("debugger")!=-1) layout.debugger = true;
layout.desktop = layout;
COMMON.DOM.addReady(function(){
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Notice.js", null, false);
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Tooltip.js", null, false);

    COMMON.setContentPath("/x_desktop");
    COMMON.AjaxModule.load("mwf", function(){
        MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
        MWF.loadLP("zh-cn");

        MWF.require("MWF.widget.Mask", null, false);
        layout.mask = new MWF.widget.Mask({"style": "desktop"});
        layout.mask.load();

        MWF.require("MWF.xDesktop.Layout", function(){
            MWF.require("MWF.xDesktop.Authentication", null, false);
            MWF.xDesktop.requireApp("Common", "", null, false);
            (function(){
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

                    MWF.xDesktop.requireApp(appNames, "lp."+MWF.language, null, false);
                    MWF.xDesktop.requireApp(appNames, clazzName, function(){
                        if (callback) callback(baseObject);
                    });
                    //
                    //MWF.require(appLp, null, false);
                    //MWF.require(appClass, function(){
                    //    if (callback) callback(baseObject);
                    //});
                };
                layout.openApplication = function(e, appNames, options, status){

                    if (layout.app){
                        layout.desktop.openBrowserApp = appNames;
                        layout.desktop.openBrowserStatus = status;
                        layout.desktop.openBrowserOption = options;
                        window.open("app.html", "_blank");
                    }else{
                        var appPath = appNames.split(".");
                        var appName = appPath[appPath.length-1];

                        layout.requireApp(appNames, function(appNamespace){
                            this.createNewApplication(e, appNamespace, appName, options, status);
                        }.bind(this));
                    }
                };
                layout.createNewApplication = function(e, appNamespace, appName, options, status){
                    var app = new appNamespace["Main"](this, options);
                    app.desktop = layout;
                    app.inBrowser = true;
                    app.status = status;
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
                layout.load = function(){
                    this.isAuthentication(function(){
                        //layout.desktop = layout;
                        layout.apps = [];
                        this.node = $("layout");
                        var topWindow = window.opener;
                        if (topWindow){

                            var appName = topWindow.layout.desktop.openBrowserApp;
                            var status = topWindow.layout.desktop.openBrowserStatus;
                            var option = topWindow.layout.desktop.openBrowserOption;
                            layout.openApplication(null, appName, option||{}, status);
                            //topWindow.layout.desktop.openBrowserApp = null;
                            //topWindow.layout.desktop.openBrowserStatus = null;
                            //topWindow.layout.desktop.openBrowserOption = null;
                        }
                        layout.mask.hide();
                    }.bind(this));
                };

                //layout.getServiceAddress = function(callback){
                //    var host = layout.config.center.host || window.location.hostname;
                //    var port = layout.config.center.port;
                //    var uri = "";
                //    if (!port || port=="80"){
                //        uri = "http://"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }else{
                //        uri = "http://"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }
                //    var currenthost = window.location.hostname;
                //    uri = uri.replace(/{source}/g, currenthost);
                //    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";
                //    MWF.restful("get", uri, null, function(json){
                //        this.serviceAddressList = json.data;
                //        if (callback) callback();
                //    }.bind(this));
                //};
                //layout.getServiceAddress = function(callback){
                //    if (typeOf(layout.config.center)=="object"){
                //        this.getServiceAddressConfigObject(callback);
                //    }else if (typeOf(layout.config.center)=="array"){
                //        this.getServiceAddressConfigArray(callback);
                //    }
                //
                //};
                //layout.getServiceAddressConfigArray = function(callback) {
                //    var requests = [];
                //    layout.config.center.each(function(center){
                //        requests.push(
                //            this.getServiceAddressConfigObject(function(){
                //                requests.each(function(res){
                //                    if (res.isRunning()){res.cancel();}
                //                });
                //                if (callback) callback();
                //            }.bind(this), center)
                //        );
                //    }.bind(this));
                //};
                //layout.getServiceAddressConfigObject = function(callback, center){
                //    var centerConfig = center;
                //    if (!centerConfig) centerConfig = layout.config.center;
                //    var host = centerConfig.host || window.location.hostname;
                //    var port = centerConfig.port;
                //    var uri = "";
                //    if (!port || port=="80"){
                //        uri = "http://"+host+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }else{
                //        uri = "http://"+host+":"+port+"/x_program_center/jaxrs/distribute/assemble/source/{source}";
                //    }
                //    var currenthost = window.location.hostname;
                //    uri = uri.replace(/{source}/g, currenthost);
                //    //var uri = "http://"+layout.config.center+"/x_program_center/jaxrs/distribute/assemble";
                //    return MWF.restful("get", uri, null, function(json){
                //        this.serviceAddressList = json.data;
                //        this.centerServer = center;
                //        if (callback) callback();
                //    }.bind(this));
                //};

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
                        this.authentication.loadLogin(this.node);
                        returnValue = false;
                    }.bind(this));
                    return returnValue;
                };

                MWF.getJSON("res/config/config.json", function(config){
                    layout.config = config;

                    MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
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