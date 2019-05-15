layout = window.layout || {};
layout.desktop = layout;

o2.addReady(function(){
    var locate = window.location;
    layout.protocol = locate.protocol;
    var href = locate.href;
    if (href.indexOf("debugger")!=-1) layout.debugger = true;
    layout.mobile = true;

    layout.desktop.session = {};
    var uri = new URI(href);
    var appNames = uri.getData("app");
    var optionsStr = (uri.getData("option"));
    var statusStr = (uri.getData("status"));

    var options = (optionsStr) ? JSON.decode(optionsStr) : null;
    var statusObj = (statusStr) ? JSON.decode(statusStr) : null;

    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], {"sequence": true}, function(){
        //MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
        MWF.loadLP("zh-cn");

        // MWF.require("MWF.widget.Mask", null, false);
        // layout.mask = new MWF.widget.Mask({"style": "desktop"});
        // layout.mask.load();

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
                    //
                    //MWF.require(appLp, null, false);
                    //MWF.require(appClass, function(){
                    //    if (callback) callback(baseObject);
                    //});
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
                        switch (appNames){
                            case "process.Work":
                                if (window.o2android && window.o2android.openO2Work){
                                    if( options.workId ){
                                        window.o2android.openO2Work(options.workId, "", title);
                                    }else if( options.workCompletedId ){
                                        window.o2android.openO2Work("", options.workCompletedId, title);
                                    }
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Work){
                                    if( options.workId ){
                                        window.webkit.messageHandlers.openO2Work.postMessage({"work":options.workId, "workCompleted":"", "title":title});
                                    }else if( options.workCompletedId ){
                                        window.webkit.messageHandlers.openO2Work.postMessage({"work":"", "workCompleted":options.workCompletedId, "title":title});
                                    }
                                }else{
                                    var uri = new URI(window.location.href);
                                    var redirectlink = uri.getData("redirectlink");
                                    if( !redirectlink ){
                                        redirectlink = encodeURIComponent(locate.pathname + locate.search);
                                    }else{
                                        redirectlink = encodeURIComponent(redirectlink);
                                    }
                                    if( options.workId ){
                                        window.location = "workmobilewithaction.html?workid="+options.workId+"&redirectlink="+redirectlink;
                                    }else if( options.workCompletedId ){
                                        window.location = "workmobilewithaction.html?workcompletedid="+options.workCompletedId+"&redirectlink="+redirectlink;
                                    }
                                }
                                break;
                            case "cms.Document":
                                if (window.o2android && window.o2android.openO2CmsDocument){
                                    window.o2android.openO2CmsDocument(options.documentId, title);
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument){
                                    window.webkit.messageHandlers.openO2CmsDocument.postMessage({"docId":options.documentId,"docTitle":title});
                                }else{
                                    //window.open("appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || ""));
                                    window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                                }
                                break;
                            case "cms.Module":
                                if (window.o2android && window.o2android.openO2CmsApplication){
                                    window.o2android.openO2CmsApplication(options.columnId, title);
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication){
                                    window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
                                }else{
                                    window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                                }
                                break;
                            case "Meeting":
                                if (window.o2android && window.o2android.openO2Meeting){
                                    window.o2android.openO2Meeting("");
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting){
                                    window.webkit.messageHandlers.openO2Meeting.postMessage("");
                                }else{
                                    window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                                }
                                break;

                            case "Calendar":
                                if (window.o2android && window.o2android.openO2Calendar){
                                    window.o2android.openO2Calendar("");
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar){
                                    window.webkit.messageHandlers.openO2Calendar.postMessage("");
                                }else{
                                    window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                                }
                                break;
                            case "process.TaskCenter":
                                var tab = ((options && options.navi) ? options.navi : "task").toLowerCase();
                                if (tab==="done") tab = "taskCompleted";
                                if (tab==="readed") tab = "readCompleted";
                                if (window.o2android && window.o2android.openO2WorkSpace){
                                    window.o2android.openO2WorkSpace(tab);
                                }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2WorkSpace){
                                    window.webkit.messageHandlers.openO2WorkSpace.postMessage(tab);
                                }else{
                                    window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                                }
                                break;
                            default:
                                window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                        }
                        // if (appNames === "process.Work"){
                        //     var uri = new URI(window.location.href);
                        //     var redirectlink = uri.getData("redirectlink");
                        //     if( !redirectlink ){
                        //         redirectlink = encodeURIComponent(locate.pathname + locate.search);
                        //     }else{
                        //         redirectlink = encodeURIComponent(redirectlink);
                        //     }
                        //     if( options.workId ){
                        //         window.location = "workmobilewithaction.html?workid="+options.workId+"&redirectlink="+redirectlink;
                        //     }else if( options.workCompletedId ){
                        //         window.location = "workmobilewithaction.html?workcompletedid="+options.workCompletedId+"&redirectlink="+redirectlink;
                        //     }
                        // }else{
                        //     window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
                        // }

                        //window.location = "workmobilewithaction.html?workid="+options.workId;
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
                    layout.desktop.currentApp = app;
                };
                layout.load = function(){
                    this.isAuthentication(function(){
                        //layout.desktop = layout;
                        layout.apps = [];
                        this.node = $("layout");
                        var topWindow = window.opener;
                        if (topWindow){
                            try{
                                var appName = topWindow.layout.desktop.openBrowserApp || appNames;
                                var m_status = topWindow.layout.desktop.openBrowserStatus || statusObj;
                                var option = topWindow.layout.desktop.openBrowserOption || options;
                                layout.openApplication(null, appName, option||{}, m_status);
                            }catch(e){
                                var appName = appNames;
                                var m_status = statusObj;
                                var option = options;
                                layout.openApplication(null, appName, option||{}, m_status);
                            }

                            //topWindow.layout.desktop.openBrowserApp = null;
                            //topWindow.layout.desktop.openBrowserStatus = null;
                            //topWindow.layout.desktop.openBrowserOption = null;
                        }else{
                            var appName = appNames;
                            var m_status = statusObj;
                            var option = options;
                            layout.openApplication(null, appName, option||{}, m_status);
                        }
                        if (layout.mask) layout.mask.hide();
                        if (document.all.appContentMask) document.all.appContentMask.destroy();
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
                        this.authentication.loadLogin(document.body);
                        if (layout.mask) layout.mask.hide();
                        if (document.all.appContentMask) document.all.appContentMask.destroy();
                        returnValue = false;
                    }.bind(this));
                    return returnValue;
                };

                MWF.getJSON("res/config/config.json", function(config){
                    layout.config = config;
                    if (layout.config.app_protocol=="auto"){
                        layout.config.app_protocol = window.location.protocol;
                    }
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