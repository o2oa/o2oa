layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger")!=-1) layout["debugger"] = true;
layout.session = layout.session || {};
//layout.session.user = {};
layout.desktop = layout;

var uri = new URI(href);
var appNames = "portal.Portal";
var id = "17d2cc06-b28a-4e40-b588-edf72f84a011"; //uri.getData("id");
var page = uri.getData("page");
if( !page )page = "fade60f4-b4c9-446d-8ff3-9f1e0da4813c";
var statusObj = {"portalId": id, "pageId": page};
var options = null;


layout.desktop = layout;
COMMON.DOM.addReady(function(){
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Notice.js", null, false);
    COMMON.AjaxModule.load("/x_desktop/res/framework/mootools/plugin/mBox.Tooltip.js", null, false);
    COMMON.setContentPath("/x_desktop");
    //COMMON.AjaxModule.load("ie_adapter", function(){
        COMMON.AjaxModule.load("mwf", function(){
            MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
            MWF.loadLP("zh-cn");

            // MWF.require("MWF.widget.Mask", null, false);
            // layout.mask = new MWF.widget.Mask({"style": "desktop"});
            // layout.mask.load();

            MWF.require("MWF.xDesktop.Layout", function(){
                MWF.xDesktop.requireApp("Common", "", null, false);

                MWF.require("MWF.xDesktop.Authentication", null, false);
                MWF.xDesktop.Authentication2 = new Class({
                    Extends : MWF.xDesktop.Authentication,
                    openLoginForm: function( options, callback ){
                        var opt = Object.merge( this.popupOptions || {}, this.options.loginOptions || {}, options || {}, {
                            onPostOk : function(json){
                                if( callback )callback(json);
                                if( this.postLogin  )this.postLogin( json );
                                this.fireEvent("postOk",json)
                            }.bind(this)
                        });
                        var form = new MWF.xDesktop.Authentication.LoginForm(this, {}, opt, this.popupPara );
                        form.create();
                    }
                });

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
                            window.open("/x_desktop/app.html", "_blank");
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
                    layout.refreshApp = function(app){
                        var status = app.recordStatus();

                        var topWindow = window.opener;
                        if (topWindow){
                            topWindow.layout.desktop.openBrowserStatus = status;
                            var appName = topWindow.layout.desktop.openBrowserApp || appNames;
                            var m_status = status;
                            var option = topWindow.layout.desktop.openBrowserOption || options;
                            window.location.reload();
                        }else{
                            statusStr = encodeURIComponent(JSON.encode(status));
                            var port = uri.get("port");
                            var url = uri.get("scheme")+"://"+uri.get("host")+((port) ? ":"+port+"" : "")+uri.get("directory")+uri.get("file")+"?app="+appNames+"&status="+statusStr;
                            window.location = url;
                        }
                        //layout.openApplication(null, appName, option||{}, m_status);
                    };
                    layout.load = function(){
                        debugger;

                        this.isAuthentication(function(){
                            layout.desktop = layout;
                            layout.apps = [];
                            this.node = $("layout");
                            var topWindow = window.opener;
                            if (topWindow){

                                var appName = topWindow.layout.desktop.openBrowserApp || appNames;
                                var m_status = topWindow.layout.desktop.openBrowserStatus || statusObj;
                                var option = topWindow.layout.desktop.openBrowserOption || options;
                                layout.openApplication(null, appName, option||{}, m_status);
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
                        }.bind(this));
                    };

                    layout.isAuthentication = function(callback){
                        this.authentication = new MWF.xDesktop.Authentication2({
                            loginOptions : {
                                "title" : "浙江省机关事务管理局"
                            },
                            "onLogin": layout.load.bind(layout)
                        });

                        var returnValue = true;
                        this.authentication.isAuthenticated(function(json){
                            this.user = json.data;
                            this.session = {};
                            this.session.user = json.data;
                            $("appContent").setStyle("display","");
                            $("loginBackground").setStyle("display","none");
                            if (callback) callback();
                        }.bind(this), function(){
                            this.authentication.loadLogin(document.body);
                            //if (layout.mask) layout.mask.hide();
                            returnValue = false;
                        }.bind(this));
                        return returnValue;
                    };

                    MWF.getJSON("/x_desktop/res/config/config.json", function(config){
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
    //}.bind(this));
});