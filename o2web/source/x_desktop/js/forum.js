layout = window.layout || {};
layout.desktop = layout;
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
layout.session = layout.session || {};
if (href.indexOf("debugger")!=-1) layout.debugger = true;
o2.addReady(function(){
    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], {"sequence": true}, function(){
        //MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
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
                    var id;
                    if (layout.app){
                        //layout.desktop.openBrowserApp = appNames;
                        //layout.desktop.openBrowserStatus = status;
                        //layout.desktop.openBrowserOption = options;
                        if( !appNames || appNames == "Forum"){
                            id = options.id;
                        }else if(appNames=="ForumCategory"){
                            id = options.categoryId;
                        }else if(appNames=="ForumDocument"){
                            id = options.id;
                        }else if(appNames=="ForumPerson"){
                            //var id = encodeURI( options.personName );
                        }else if(appNames=="ForumSearch"){

                        }else if(appNames=="ForumSection"){
                            id = options.sectionId;
                        }
                        //sessionStorage.setItem(appNames+id+"options", options);
                        //sessionStorage.getItem( appName+id+"status", status );
                        sessionStorage[appNames+(id ? id : "")+"options"] = JSON.stringify( options );
                        sessionStorage[appName+(id ? id : "")+"status"] = JSON.stringify( statusObj );
                        if( layout.debugger ){
                            window.open("forum.html?debugger&app="+appNames+ ( id ? "&id="+id : ""), "_blank");
                        }else{
                            window.open("forum.html?app="+appNames+ ( id ? "&id="+id : ""), "_blank");
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
                layout.load = function(){

                    // MWF.require("MWF.xDesktop.MessageMobile", function(){
                    //     layout.message = new MWF.xDesktop.MessageMobile();
                    //     layout.message.load();
                    // }.bind(this));

                    this.isAuthentication(function(){

                        layout.apps = [];
                        this.node = $("layout");
                        //var topWindow = window.opener;
                        //if (topWindow){
                        //
                        //    var appName = topWindow.layout.desktop.openBrowserApp;
                        //    var status = topWindow.layout.desktop.openBrowserStatus;
                        //    var option = topWindow.layout.desktop.openBrowserOption;
                        //    layout.openApplication(null, appName, option||{}, status);
                        //}
                        //sessionStorage.getItem()

                        var option = {};
                        var statusObj;
                        var urlParams = layout.getUrlParam();
                        if( urlParams.app ){
                            var appName = urlParams.app;
                            var id = urlParams.id;
                            //alert(sessionStorage.getItem( appName+id+"options").id)
                            //alert(sessionStorage.getItem( appName+id+"status").id)
                            var opt = sessionStorage[ appName+ (id ? id : "") +"options" ];
                            if(opt){
                                option = JSON.parse(opt)
                            }else if(id){
                                if( appName == "ForumCategory" ) {
                                    option = {categoryId: id};
                                }else if( appName=="ForumSection" ){
                                    option = {sectionId: id};
                                }else if( appName=="ForumDocument" ){
                                    option = {
                                        isNew : false,
                                        isEdited : false,
                                        id: id
                                    };
                                }else{
                                    option = {id : id};
                                }
                            }

                            statusObj = sessionStorage[ appName+ (id ? id : "") +"status" ];
                            if(statusObj){
                                statusObj = JSON.parse(option)
                            }
                        }else{
                            var appName = "Forum";
                        }
                        layout.openApplication(null, appName, option, statusObj);

                        layout.mask.hide();
                    }.bind(this));
                };

                layout.getUrlParam = function(){

                    var href = window.location.href;
                    var qStr = href.substr(href.indexOf("?")+1, href.length);
                    var qDatas = qStr.split("&");
                    var obj = {};
                    qDatas.each(function(d){
                        var q = d.split("=");
                        obj[q[0]] = q[1];
                    });

                    return obj;
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
                        //this.authentication.loadLogin(this.node);
                        //returnValue = false;
                        this.user = "anonymous";
                        this.session = {};
                        this.session.user = {
                            name : this.user,
                            roleList : []
                        };
                        if (callback) callback();
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