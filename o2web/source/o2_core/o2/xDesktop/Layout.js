MWF.xDesktop = MWF.xDesktop || {};
MWF.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async)
};
MWF.xApplication = MWF.xApplication || {};
// MWF.require("MWF.widget.Common", null, false);
// MWF.require("MWF.xDesktop.Common", null, false);
// MWF.require("o2.xDesktop.Dialog", null, false);
// MWF.require("MWF.xAction.RestActions", null, false);
// MWF.require("MWF.xDesktop.Menu", null, false);
// MWF.require("MWF.widget.UUID", null, false);
MWF.require("MWF.xDesktop.Lnk", null, false);
// MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
// MWF.require("MWF.xDesktop.Authentication", null, false);
MWF.require("MWF.xDesktop.Message", null, false);
// MWF.require("MWF.xDesktop.UserData", null, false);
MWF.require("MWF.xDesktop.shortcut", null, false);
// MWF.require("MWF.widget.PinYin", null, false);
// MWF.xDesktop.requireApp("Common", "", null, false);
MWF.xDesktop.requireApp("Common", "Widget", null, false);
MWF.require("MWF.xDesktop.WebSocket", null, false);
//MWF.require("MWF.xDesktop.UserPanel", null, false);
// MWF.require("MWF.xDesktop.Access", null, false);
 
MWF.xDesktop.Layout = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "defaultStyle": "default",

        "topShim": "layout_top_shim",
        "top": "layout_top",
        "desktop": "layout_desktop",
        "content": "desktop_content",
        "navi": "desktop_navi"
    },
    categoryCount: 20,
    processCount: 40,

    initialize: function(node, options){
        this.setOptions(options);
        this.type = "layout";
        this.path = MWF.defaultPath+"/xDesktop/$Layout/";
        this.initData();
        this.initNode(node);
        //this.load();

        // this.authentication = new MWF.xDesktop.Authentication({
        //     "onLogin": this.load.bind(this)
        // });
        // this.authentication.isAuthenticated(function(json){
        //     this.session.user = json.data;
        //     layout.session.user = json.data;
        //this.getStyleLoad(node);
        // }.bind(this), function(){
        //     if (layout.config.loginPage && layout.config.loginPage.enable && layout.config.loginPage.portal){
        //         MWF.xDesktop.loadPortal(layout.config.loginPage.portal);
        //         //window.location = "portal.html?portal="+layout.config.loginPage.portal;
        //     }else{
        //         this.getPublicStyleLoad(node);
        //     }
        //     // this.initNode(node);
        //     // this.load();
        // }.bind(this));
    },

    getPublicStyleLoad: function(node){
        MWF.UD.getPublicData("defaultLayout", function(json) {
            if (json){
                this.status = json;
            }
            this.initNode(node);
            this.load();
        }.bind(this));
    },
    // getStyleLoad: function(node){
    //
    //     MWF.UD.getPublicData("forceLayout", function(json) {
    //         var forceStatus = null;
    //         if (json){
    //             forceStatus = json;
    //         }
    //
    //         MWF.UD.getData("layout", function(json) {
    //             if (json.data) {
    //                 this.status = JSON.decode(json.data);
    //
    //                 if (forceStatus) this.status.apps = Object.merge(this.status.apps, forceStatus.apps);
    //
    //                 if (this.status.style) this.options.style = this.status.style;
    //                 if (this.status.styleType) this.options.styleType = this.status.styleType;
    //                 this.initNode(node);
    //                 this.load();
    //             }else{
    //                 MWF.UD.getPublicData("defaultLayout", function(json) {
    //                     if (json){
    //                         this.status = json;
    //
    //                         if (forceStatus) this.status.apps = Object.merge(this.status.apps, forceStatus.apps);
    //
    //                         if (this.status.style) this.options.style = this.status.style;
    //                         if (this.status.styleType) this.options.styleType = this.status.styleType;
    //                     }
    //                     this.initNode(node);
    //                     this.load();
    //                 }.bind(this));
    //             }
    //         }.bind(this));
    //     }.bind(this));
    // },
    loadCss: function(){
        var uri = new URI(window.location.href);
        var style = uri.getData("style");
        var styletype = (uri.getData("styletype"));
        if (style){
            if (styletype=="custom"){
                MWF.UD.getPublicData(style, function(json){
                    if (json && json.data.desktop && json.data.window){
                        this.css = json.data.desktop;
                        this.windowCss = json.data.window;
                    }else{
                        MWF.UD.deletePublicData(style);
                        this.options.style = this.options.defaultStyle;
                        this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
                        this._loadCss();
                    }
                }.bind(this), false);
            }else{
                this.options.style = style;
                this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
                this._loadCss();
            }
        }else{
            if ((!this.status) || !this.status.style || this.status.styleType != "custom"){
                if (this.status) if (this.status.style) this.options.style = this.status.style;
                //this.options.style = "default";
                this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
                this._loadCss();
                if (!this.css){
                    this.options.style = this.options.defaultStyle;
                    this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
                    this._loadCss();
                }
            }else{
                MWF.UD.getPublicData(this.status.style, function(json){
                    if (json && json.data.desktop && json.data.window){
                        this.css = json.data.desktop;
                        this.windowCss = json.data.window;
                    }else{
                        MWF.UD.deletePublicData(this.status.style);
                        this.options.style = this.options.defaultStyle;
                        this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
                        this._loadCss();
                    }
                }.bind(this), false);
            }
        }
    },
    initNode: function(node){


        this.loadCss();

        //$("appContent").destroy();
        this.node = $(node);
        this.topShimNode = this.node.getElement("#"+this.options.topShim);
        this.topNode = this.node.getElement("#"+this.options.top);
        this.topAreaNode = this.node.getElement("#"+this.options.top);
        this.desktopNode = this.node.getElement("#"+this.options.desktop);
        this.contentNode = this.node.getElement("#"+this.options.content);
        this.naviNode = this.node.getElement("#"+this.options.navi);

        if (this.node) this.node.setStyles(this.css.layoutNode);
        if (this.topShimNode) this.topShimNode.setStyles(this.css.layoutTopShimNode);
        if (this.topNode) this.topNode.setStyles(this.css.layoutTopNode);
        if (this.desktopNode) this.desktopNode.setStyles(this.css.desktopNode);
        if (this.contentNode) this.contentNode.setStyles(this.css.contentNode);
        if (this.naviNode) this.naviNode.setStyles(this.css.naviNode);

        var dskImg = this.css.desktop.background;
        //MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/desktop.jpg";
        this.node.setStyle("background-image", this.css.desktop.background);
    },
    initData: function(){
        this.apps = {};
        this.widgets = {};
        this.appCurrentList = [];
        this.lnkAreas = [];
        this.lnks = [];
        this.currentApp = null;
        this.session = {};

        var uri = new URI(window.location.href);
        var df = uri.getData("default") || "";
        this.noDefault = df.toString().toLowerCase()==="false";
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var options = (optionsStr) ? JSON.decode(optionsStr) : null;
        if (appNames){
            this.status = layout.userLayout;
            this.status.apps = {};
            this.status.apps[appNames] = options || {};
            this.status.apps[appNames].name = appNames;
            this.status.apps[appNames].appId = appNames;
            this.status.apps[appNames].window={"isMax": true, "isHide": false};
            this.status.currentApp = appNames;
        }else{
            this.status = layout.userLayout;
        }

        //this.status = layout.userLayout;
        this.session.user = layout.session.user
        this.serviceAddressList = layout.serviceAddressList;
        this.centerServer = layout.centerServer;

        if (this.status && this.status.style) this.options.style = this.status.style;
        if (this.status && this.status.styleType) this.options.styleType = this.status.styleType;

        layout.openApplication = this.openApplication.bind(this);
    },

    // isAuthentication: function(success){
    //     var returnValue = true;
    //     if (this.session.user){
    //         if (success) success();
    //     }else{
    //         this.authentication.loadLogin(this.node);
    //         this.fireEvent("login");
    //         returnValue = false;
    //     }
    //     return returnValue;
    // },

    load : function(){
        if (document.body.addEventListener){
            document.body.addEventListener('dragover', function(e){
                e.stopPropagation();
                e.preventDefault();
            }.bind(this), false);
        }

        //this.isAuthentication(function(){
            if (this.status){
                if (this.status.style){
                    if (this.options.style !== this.status.style){
                        this.changStyle(this.status.style);
                    }
                }
            }
            this.getNodeBackground();

            this.loadDesktop();

            if (this.session.user.passwordExpired){
                this.openApplication({"page":{"x": 0, "y": 0}}, "Profile", {"tab": "passwordConfigPage"});
                window.setTimeout(function(){
                    MWF.xDesktop.notice("error", {"y":"top", "x": "left"}, MWF.LP.desktop.notice.changePassword, this.desktopNode);
                }.bind(this), 500);
            }

            if (MWF.AC.isAdministrator()){
                //this.checkO2Collect();
            }
            this.fireEvent("load");

        //}.bind(this));
    },
    checkO2Collect: function(){
        var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_program_center");
        action.invoke({"name": "collectConnected", "success": function(json){
            if (json.data.value){
                action.invoke({"name": "collectValidate", "success": function(json){
                    if (!json.data.value){
                        this.openApplication({"page":{"x": 0, "y": 0}}, "Collect");
                    }
                }.bind(this), "failure": function(){
                    this.openApplication({"page":{"x": 0, "y": 0}}, "Collect");
                }.bind(this)});
            }else{
                this.showMessageNotConnectCollect();
            }
        }.bind(this), "failure": function(){
            this.showMessageNotConnectCollect();
        }.bind(this)});
    },
    showMessageNotConnectCollect: function(){
        var msg = {
            "subject": MWF.LP.desktop.collect.collectNotConnected,
            "content": MWF.LP.desktop.collect.collectNotConnectedText
        };
        var tooltipItem = layout.desktop.message.addTooltip(msg);
        var messageItem = layout.desktop.message.addMessage(msg);
    },

    loadDesktop: function(){
        this.setHeight();

        var size = this.desktopNode.getSize();
        this.size = {
            "x" : size.x,
            "y": size.y
        };
        //this.loadSession();
        this.loadTop();
        //this.loadNavi(); //@todo 暂时不要快速启动导航了
        this.loadLnkArea();
        this.setEvent();
        this.loadWidget();
        this.loadStatus(function(){
            this.openWebSocket();
        }.bind(this));
    },
    openWebSocket: function(){
        this.socket = new MWF.xDesktop.WebSocket();
        //window.setTimeout(this.checkWebSocket.bind(this), 30000);
    },
    checkWebSocket: function(){
        if (!this.socket || this.socket.webSocket.readyState !== 1) {
            this.socket = new MWF.xDesktop.WebSocket();
        }else{
            this.socket.heartbeat("heartbeat");
        }
        window.setTimeout(this.checkWebSocket.bind(this), 30000);
    },
    loadWidget: function(){

    },
    loadLnkAreaContainer: function(){
        this.lnkAreaContainer = new Element("div", {
            "styles": {
                "height": "100%",
                "overflow": "hidden"
            }
        }).inject(this.contentNode);
    },
    loadLnkArea: function(){
        if (!this.lnkAreaContainer) this.loadLnkAreaContainer();
        var lnkArea = new Element("div", {
            "styles": this.css.dsektopLnkArea
        }).inject(this.lnkAreaContainer);
        this.lnkAreas.push(lnkArea);

        var width = (lnkArea.getSize().x)*(this.lnkAreas.length);
        var contentSize = this.contentNode.getSize();
        this.lnkAreaContainer.setStyle("width", ""+Math.max(width, contentSize.x)+"px");

        this.setCurrentLnkArea();
    },
    setCurrentLnkArea: function(){
        if (this.lnkAreas.length>1){
            var lnkSize = this.lnkAreas[0].getSize().x;
            var width = (lnkSize)*(this.lnkAreas.length);
            var contentSize = this.contentNode.getSize();
            var currentArea = this.lnkAreas[this.lnkAreas.length-1];
            if (width<contentSize.x){
                width = width-lnkSize;
                currentArea.setStyles({
                    "width": "auto",
                    "margin-left": ""+width+"px",
                    "float": "none"
                });
            }else{
                currentArea.setStyles(this.css.dsektopLnkArea);
            }
        }else{
            if (this.lnkAreas.length){
                this.lnkAreas[0].setStyles({
                    "width": "auto",
                    "margin-left": "0px",
                    "float": "none"
                });
            }
        }

    },
    addLnkArea: function(){
        if (this.lnkAreas.length){
            this.lnkAreas[this.lnkAreas.length-1].setStyles(this.css.dsektopLnkArea);
        }
        this.loadLnkArea();
    },
    addLnk: function(json){
        var lnk = new MWF.xDesktop.Lnk(json.icon, json.title, json.par);
        if (!this.lnkAreas.length) this.loadLnkArea();
        lnk.inject(this.lnkAreas[this.lnkAreas.length-1]);
        this.lnks.push(lnk);
    },
    resizeLnk: function(){
        if (this.lnkAreaContainer){
            if (this.lnkAreas.length>1){
                var width = (this.lnkAreas[0].getSize().x)*(this.lnkAreas.length);
                var contentSize = this.contentNode.getSize();
                this.lnkAreaContainer.setStyle("width", ""+Math.max(width, contentSize.x)+"px");
            }else{
                this.lnkAreaContainer.setStyle("width", ""+this.contentNode.getSize().x+"px");
            }
        }


        var n=0;
        var count = 0;
        this.lnks.each(function(lnk, idx){
            while(!this.lnkAreas[n]) this.addLnkArea();
            var linkArea = this.lnkAreas[n];
            lnk.inject(linkArea);
            count++;

            //var y = lnk.node.getSize().y+lnk.node.getStyle("margin-top").toFloat()+lnk.node.getStyle("margin-bottom").toFloat();
            var y = lnk.node.getSize().y+lnk.node.getStyle("margin-top").toFloat();
            if (y*(count+1)>linkArea.getSize().y){
                if (idx<this.lnks.length-1) n++;
                count = 0;
            }
        }.bind(this));
        if (this.lnkAreas.length) while (this.lnkAreas.length>n+1 ) this.lnkAreas.pop().destroy();

        this.setCurrentLnkArea();
    },

    refreshApp:function(app){
        if (app.window){
            var appStatus ={
                "id": app.appId,
                "name": app.options.name,
                "style": app.options.style,
                "appId": app.appId,
                "window": {
                    "size": {"x": app.window.css.to.width.toFloat(), "y": app.window.css.to.height.toFloat()},
                    "position": {"x": app.window.css.to.left.toFloat(), "y": app.window.css.to.top.toFloat()},
                    "isMax": app.window.isMax,
                    "isHide": app.window.isHide,
                    "style": app.window.options.style
                },
                "app": null
            };
            if (app.recordStatus) appStatus.app = app.recordStatus();

            app.close();
            this.openApplicationWithStatus(appStatus);

        }
    },
    openApplicationWithStatus: function(appStatus){
        var appName = appStatus.name;
        var style = (appStatus.style) ? appStatus.style : this.options.defaultStyle;
        //	var appClass = "MWF.xApplication."+appName+".Main";
        this.requireApp(appName, function(appNamespace){
            var app = new appNamespace["Main"](this, {
                "style": style,
                "isRefresh": true,
                "onLoadWindow": function(){
                    this.window.setOptions({
                        "top": appStatus.window.position.y,
                        "left": appStatus.window.position.x,
                        "width": appStatus.window.size.x,
                        "height": appStatus.window.size.y
                    });
                    this.window.reStyle();
                },
                "onPostLoadWindow": function(){
                    if (appStatus.window.isMax){
                        this.maxSize(function(){
                            this.fireAppEvent("postLoadWindowMax");
                        }.bind(this));
                    }
                    if (appStatus.window.isHide){
                        this.minSize(function(){
                            this.fireAppEvent("postLoadWindowMin");
                        }.bind(this));
                    }
                },
                "onPostLoad": function(){
                }
            });

            app.desktop = this;
            app.appId = appStatus.id;
            app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
            this.apps[appStatus.id] = app;
            app.status = appStatus.app;
            this.appCurrentList.push(app);
            app.loadNoAnimation(true, appStatus.window.isMax, appStatus.window.isHide);

        }.bind(this));
    },
    loadStatus: function(callback){
        if (this.status){
            if (this.status.apps){
                Object.each(this.status.apps, function(appStatus, id){
                    var appName = appStatus.name;
                    var style = (appStatus.style) ? appStatus.style : this.options.defaultStyle;
                    //	var appClass = "MWF.xApplication."+appName+".Main";

                    this.requireApp(appName, function(appNamespace){
                        var app = new appNamespace["Main"](this, {
                            "style": style,
                            "isRefresh": true,
                            "onLoadWindow": function(){
                                this.window.setOptions({
                                    "top": (appStatus.window.position) ? appStatus.window.position.y : 10,
                                    "left": (appStatus.window.position) ? appStatus.window.position.x : 10,
                                    "width": (appStatus.window.size) ? appStatus.window.size.x : this.options.width,
                                    "height": (appStatus.window.size) ? appStatus.window.size.y : this.options.height,
                                });
                                this.window.reStyle();
                            },
                            "onPostLoadWindow": function(){
                                if (appStatus.window.isMax){
                                    this.maxSize(function(){
                                        this.fireAppEvent("postLoadWindowMax");
                                    }.bind(this));
                                }
                                if (appStatus.window.isHide){
                                    this.minSize(function(){
                                        this.fireAppEvent("postLoadWindowMin");
                                    }.bind(this));
                                }
                            },
                            "onPostLoad": function(){
                            }
                        });

                        app.desktop = this;
                        app.appId = id;
                        app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
                        this.apps[id] = app;
                        app.status = appStatus.app;
                        this.appCurrentList.push(app);
                        app.loadNoAnimation((this.status.currentApp==id), appStatus.window.isMax, appStatus.window.isHide);

                    }.bind(this));
                }.bind(this));
            }

            if (this.status.widgets){
                Object.each(this.status.widgets, function(widgetStatus, id){
                    var name = widgetStatus.name;
                    var appName = widgetStatus.appName;

                    this.requireApp(appName, function(appNamespace){
                        var widget = new appNamespace[name](this, {
                            "position": widgetStatus.position,
                            "onLoadWidget": function(){
                                this.widget.setOptions({
                                    "position": widgetStatus.position
                                });
                            }
                        });

                        widget.desktop = this;
                        widget.widgetId = id;

                        this.widgets[id] = widget;
                        widget.status = widgetStatus.widget;
                        widget.load();

                    }.bind(this), name);
                }.bind(this));
            }

            if (this.status.lnks){
                this.status.lnks.each(function(lnkJson){
                    this.addLnk(lnkJson);
                }.bind(this));
                this.resizeLnk();
            }
        }

        var loadLocal = false;
        var loadWeb = false;
        var checkLoaded = function(){
            if (loadLocal && loadWeb) if (callback) callback();
        };
        var url = MWF.defaultPath+"/xDesktop/$Layout/applications.json";
        MWF.getJSON(url, function(json){
            json.each(function(value, key){
                if (value.widgetName){
                    if (value.widgetStart){
                        this.openWidget(null, value.widgetName, value.path);
                    }
                }
            }.bind(this));
            loadLocal = true;
            checkLoaded();
        }.bind(this));

        var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_component_assemble_control");
        action.invoke({"name": "listComponent", "success": function(json){
            //var currentName = this.session.user.name;
            var user = this.session.user;
            var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
            if (user.roleList) currentNames = currentNames.concat(user.roleList);
            if (user.groupList) currentNames = currentNames.concat(user.groupList);

            json.data.each(function(value, key){
                if (value.visible){
                    var isAllow = (value.allowList.length) ? (value.allowList.isIntersect(currentNames)) : true;
                    var isDeny = (value.denyList.length) ? (value.denyList.isIntersect(currentNames)) : false;
                    if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                        if (value.widgetName){
                            if (value.widgetStart){
                                this.openWidget(null, value.widgetName, value.path);
                            }
                        }
                    }
                }
            }.bind(this));
            loadWeb = true;
            checkLoaded();
        }.bind(this)});
    },

    setEvent: function(){
        this.node.addEvent("selectstart", function(e){
            var select = "text";
            if (e.target.getStyle("-webkit-user-select")){
                select = e.target.getStyle("-webkit-user-select").toString().toLowerCase();
            }

            if (select.toString()!=="text" && select.toString()!=="auto") e.preventDefault();
        });
        window.onunload = function(e){
            //if (this.socket && this.socket.webSocket &&  this.socket.webSocket.readyState.toInt() === 1) {\
            // if (this.socket && this.socket.webSocket) {
            //     console.log("websocket is onunload close ...");
            //     this.socket.webSocket.close();
            // }
        }.bind(this);

        window.onbeforeunload = function(e){
            if (!this.isLogout){
                if (!this.notRecordStatus) this.recordDesktopStatus();
                if (this.socket && this.socket.webSocket) {
                    //console.log("websocket is onbeforeunload close ...");

                    this.socket.reConnect = false;
                    this.socket.webSocket.close();
                    //return false;
                }
                this.fireEvent("unload");
                e = e || window.event;
                e.returnValue = MWF.LP.desktop.notice.unload;
                return MWF.LP.desktop.notice.unload;
            }
        }.bind(this);
    },
    recordDesktopStatus: function(callback){
        Object.each(this.apps, function(app, id){
            if (!app.options.desktopReload){
                this.closeApp(app);
            }
        }.bind(this));
        this.recordStatusData(function(){
            if (callback) callback();
        });
    },
    getLayoutStatusData: function(){
        var status = {
            "viewMode": "Layout",
            "style": this.options.style,
            "styleType": this.options.styleType || "",
            "flatStyle": this.status.flatStyle || "blue",
            "currentApp": (this.currentApp) ? this.currentApp.appId : "Homepage",
            "apps": {},
            "lnks": [],
            "flatLnks": (this.status.flatLnks && o2.typeOf(this.status.flatLnks)==="array") ? this.status.flatLnks : [],
            "widgets": {}
        };
        Object.each(this.apps, function(app, id){

            if (app.window){
                if (app.options.desktopReload){
                    var appStatus ={
                        "title": app.options.title,
                        "appId": app.appId,
                        "name": app.options.name,
                        "style": app.options.style,
                        "window": {
                            //"size": app.window.node.getSize(),
                            "size": {"x": app.window.css.to.width.toFloat(), "y": app.window.css.to.height.toFloat()},
                            "position": {"x": app.window.css.to.left.toFloat(), "y": app.window.css.to.top.toFloat()},
                            "isMax": app.window.isMax,
                            "isHide": app.window.isHide,
                            "style": app.window.options.style
                        },
                        "app": null
                    };
                    if (app.recordStatus) appStatus.app = app.recordStatus();
                    status.apps[id] = appStatus;
                }
            }
        });
        this.lnks.each(function(lnk){
            status.lnks.push({
                "icon": lnk.icon,
                "title": lnk.title,
                "par": lnk.par
            });
        });
        Object.each(this.widgets, function(widget, id){
            //var p = widget.widget.node.getPosition(widget.widget.node.getOffsetParent());
            var widgetStatus ={
                "name": widget.options.name,
                "appName": widget.options.appName,
                "position": widget.options.position,
                "widget": null
            };
            if (widget.recordStatus) widgetStatus.widget = widget.recordStatus();
            status.widgets[id] = widgetStatus;
        });

        return status;
    },
    recordStatusData: function(callback){
        var status = this.getLayoutStatusData();
        MWF.UD.putData("layout", status, function(){
            if (callback) callback();
        });
        //this.recordStatusCookies(status);
    },

    getPageDesignerStyle: function(callback){
        if (!this.pageDesignerStyle){
            this.pageDesignerStyle = "default";
            MWF.UD.getData("pageDesignerStyle", function(json) {
                if (json.data) {
                    var styles = JSON.decode(json.data);
                    this.pageDesignerStyle = styles.style;
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getFormDesignerStyle: function(callback){
        if (!this.formDesignerStyle){
            this.formDesignerStyle = "default";
            MWF.UD.getData("formDesignerStyle", function(json) {
                if (json.data) {
                    var styles = JSON.decode(json.data);
                    this.formDesignerStyle = styles.style;
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    recordStatusCookies: function(status){
        var statusString = JSON.encode(status);
        Cookie.write("xdesktop", statusString);
    },
    setHeight: function(){
        this.resizeHeight();
        $(window).addEvent("resize", function(){
            this.resizeHeight();
        }.bind(this));
    },

    resizeHeight: function(){

        var yTop = this.topNode.getSize().y;
        var yBody = $(document.body).getSize().y;
        var y = yBody - yTop;

        this.desktopNode.setStyle("height", ""+y+"px");
        this.desktopHeight = y;

        var yNavi = this.naviNode.getSize().y;
        y = y - yNavi;
        this.contentNode.setStyle("height", ""+y+"px");

        this.resizeApps();
        this.resizeLnk();
        this.resizeMessage();

        this.setTaskitemSize();

        if (this.top) if (this.top.userPanel) this.top.userPanel.setPosition();

        this.fireEvent("resize");
    },
    setTaskitemSize: function(){
        if (this.top){

            var x1 = 10;
            var x2 = 5;

            var size = this.top.taskbar.getSize();
            var taskItems = this.top.taskbar.getChildren();


            var allWidth = 0;
            if (taskItems.length){
                var w = taskItems[0].getStyles("border-left-width", "border-right-width", "margin-left", "margin-right", "padding-left", "padding-right");
                for (var i=0; i<taskItems.length; i++){
                    taskItems[i].setStyle("width", "auto");
                    allWidth += taskItems[i].getSize().x+w["border-left-width"].toInt()+w["border-right-width"].toInt()+w["margin-left"].toInt()+w["margin-right"].toInt()+w["padding-left"].toInt()+w["padding-right"].toInt();
                }
                if (allWidth>(size.x-x1)){
                    var x = (size.x-x1)/taskItems.length;
                    var width = x-w["border-left-width"].toInt()-w["border-right-width"].toInt()-w["margin-left"].toInt()-w["margin-right"].toInt()-w["padding-left"].toInt()-w["padding-right"].toInt();
                    taskItems.each(function(item){
                        item.setStyle("width", ""+width+"px");
                    });
                }else{
                    taskItems.each(function(item){
                        item.setStyle("width", "auto");
                    });
                }
            }

            // var x = (size.x-x1)/taskItems.length;
            // if (x<165){
            //     var width = x-x2;
            //     taskItems.each(function(item){
            //         item.setStyle("width", ""+width+"px");
            //     });
            // }else{
            //     taskItems.each(function(item){
            //         item.setStyle("width", "auto");
            //     });
            //
            //     //this.node.setStyle("width", "160px");
            // }
        }
    },
    resizeMessage: function(){
        if (this.message) this.message.resize();
    },
    resizeApps: function(){
        var size = $(document.body).getSize();

        Object.each(this.apps, function(app, id){
            var left = app.window.css.to.left.toFloat();
            var top = app.window.css.to.top.toFloat();

            if (left>size.x) left = size.x-100;
            if (top>size.y) top = size.y-100;

            app.window.css.to.left = left+"px";
            app.window.css.to.top = top+"px";
            if (app.window.css.spacerTo) app.window.css.spacerTo.left = left+"px";
            if (app.window.css.spacerTo) app.window.css.spacerTo.top = top+"px";

            if (!app.window.isHide){
                if (app.window.isMax){
                    app.window.maxSizeIm();
                }else{
                    app.window.restoreIm();
                }
            }
        }.bind(this));
    },

    loadTop: function(){
        if (!this.top){
            this.top = new MWF.xDesktop.Layout.Top(this.topNode, this);
            this.top.load();
        }
    },
    loadNavi: function(){
        if (!this.navi){
            this.navi = new MWF.xDesktop.Layout.Navi(this.naviNode, this);
            this.navi.load();
        }
    },
    getNodeBackground: function(){
        MWF.UD.getDataJson("layoutDesktop", function(json){
            //var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/desktop.jpg";
            var dskImg = this.css.desktop.background;
            if (json){
                currentSrc = json.src;
                dskImg = "url("+MWF.defaultPath+"/xDesktop/$Layout/"+currentSrc+"/desktop.jpg"+")";
            }
            this.node.setStyle("background-image", dskImg);
        }.bind(this), false);
    },
    changStyle: function(style, isCustom){
        if (!isCustom){
            this.options.style = style;
            this.options.styleType = "default";
            this.cssPath = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/css.wcss";
            this._loadCss();
        }else{
            this.options.style = style;
            this.options.styleType = "custom";
            MWF.UD.getPublicData(style, function(json){
                this.css = json.data.desktop;
                this.windowCss = json.data.window;
            }.bind(this), false);
        }

        MWF.require("MWF.widget.Mask", function(){
            // this.mask = new MWF.widget.Mask({"style": "desktop"});
            // this.mask.load();

            window.setTimeout(function(){
                if (this.node) this.node.setStyles(this.css.layoutNode);
                if (this.topShimNode) this.topShimNode.setStyles(this.css.layoutTopShimNode);
                if (this.topNode) this.topNode.setStyles(this.css.layoutTopNode);
                if (this.desktopNode) this.desktopNode.setStyles(this.css.desktopNode);
                if (this.contentNode) this.contentNode.setStyles(this.css.contentNode);
                if (this.naviNode) this.naviNode.setStyles(this.css.naviNode);

                if (this.message){
                    if (this.message.unreadNode){
                        this.message.unreadNode.clearStyles();
                        this.message.unreadNode.setStyles(this.css.messageUnreadCountNode);
                    }
                }

                this.getNodeBackground();

                this.changeAppsStyle();

                if (this.top) this.top.changStyle();
                if (this.navi) this.navi.changStyle();

                Object.each(this.apps, function(app){
                    if (app.taskitem) app.taskitem.changStyle();
                });

                this.resizeHeight();
                //this.mask.hide();
            }.bind(this), 0);

        }.bind(this));
    },
    changeAppsStyle: function(){
        Object.each(this.apps, function(app, id){
            app.window.options.style = "desktop_"+this.options.style;
            app.window.changeStyle();
        }.bind(this));
    },
    openProcessApp: function(e, app){
        var options = {"id": app.id, "appId": "process.Application"+app.id};
        this.openApplication(e, "process.Application", options);
    },
    openQueryApp: function(e, app){
        var options = {"id": app.id, "appId": "query.Query"+app.id};
        this.openApplication(e, "query.Query", options);
    },
    openPortalApp: function(e, app){
        var options = {"portalId": app.id, "appId": "portal.Portal"+app.id};
        this.openApplication(e, "portal.Portal", options);
    },
    openCMSApp: function(e, app){
        var appId = "cms.Module"+app.id;
        if (this.apps[appId]){
            this.apps[appId].setCurrent();
        }else{
            this.openApplication(e, "cms.Module", {
                "columnData": app,
                "appId": appId
            });
        }
    },
    openWidget: function(e, widgetName, appName, options, obj){

        this.requireApp(appName, function(appNamespace){
            if (!this.widgets[appName+widgetName]){
                this.createNewWidget(e, appNamespace, appName+widgetName, widgetName, options, obj);
            }
        }.bind(this), widgetName);
    },
    openApplication: function(e, appNames, options, obj, inBrowser){
        if (appNames.substring(0, 4)==="@url"){
            var url = appNames.replace(/\@url\:/i, "");
            var a = new Element("a", {"href": url, "target": "_blank"});
            a.click();
            a.destroy();
            a = null;
            return true;
        }
        var appPath = appNames.split(".");
        var appName = appPath[appPath.length-1];

        this.requireApp(appNames, function(appNamespace){
            if (appNamespace.options.multitask){
                if (options && options.appId){
                    if (this.apps[options.appId]){
                        this.apps[options.appId].setCurrent();
                    }else {
                        this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                    }
                }else{
                    this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                }
            }else{
                if (this.apps[appName]){
                    this.apps[appName].setCurrent();
                }else{
                    this.createNewApplication(e, appNamespace, appName, options, obj, inBrowser);
                }
            }
        }.bind(this));
    },
    createNewWidget: function(e, appNamespace, appName, widgetName, options, obj){
        var widget = new appNamespace[widgetName](this, options);
        widget.desktop = this;
        if (obj){
            Object.each(obj, function(value, key){
                app[key] = value;
            });
        }
        widget.load(true);

        var widgetId = appName;
        if (options){
            if (options.widgetId){
                widgetId = options.widgetId;
            }
        }

        widget.widgetId = widgetId;
        this.widgets[widgetId] = widget;
    },
    createNewApplication: function(e, appNamespace, appName, options, obj, inBrowser){
        if (options){
            options.event = e;
        }else{
            options = {"event": e};
        }

        var app = new appNamespace["Main"](this, options);
        app.desktop = this;
        if (obj){
            Object.each(obj, function(value, key){
                app[key] = value;
            });
        }
        if (!inBrowser){
            app.taskitem = new MWF.xDesktop.Layout.Taskitem(app, this);
        }else{
            app.inBrowser = true;
        }

        app.load(true);

        var appId = appName;
        if (options.appId){
            appId = options.appId;
        }else{
            if (appNamespace.options.multitask) appId = appId+"-"+(new MWF.widget.UUID());
        }
        app.appId = appId;

        this.apps[appId] = app;
        return app;
    },
    closeApp: function(app){
        var appId = app.appId;

        this.appCurrentList.erase(app);
        //	if (this.appCurrentList.length) this.appCurrentList[this.appCurrentList.length-1].setCurrent();
        this.setTaskitemSize();
        this.apps[appId] = null;
        delete this.apps[appId];
    },
    closeWidget: function(app){
        var widgetId = app.widgetId;
        delete this.widgets[widgetId];
    },
    requireApp: function(appNames, callback, clazzName){
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

        MWF.xDesktop.requireApp(appNames, "lp."+MWF.language, {
            "onRequestFailure": function(){
                MWF.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this),
            "onSuccess": function(){}.bind(this)
        }, false);

        MWF.xDesktop.requireApp(appNames, clazzName, function(){
            if (callback) callback(baseObject);
        });
    },

    playMessageAudio: function(){
        var flag = true;
        if (this.playMessageAudioTime){
            var now = new Date();
            var diff = now.getTime()-this.playMessageAudioTime.getTime();
            if (diff<5000) flag = false;
        }

        if (flag){
            if (!this.messageAudioNode){
                var url = MWF.defaultPath+"/xDesktop/$Layout/"+layout.desktop.options.style+"/sound/message.wav";
                var url2 = MWF.defaultPath+"/xDesktop/$Layout/"+layout.desktop.options.style+"/sound/message.mp3";
                this.messageAudioNode = new Element("div", {
                    "html": "<audio autoplay=\"autoplay\" volume=0.6 preload=\"metadata\"><source src=\""+url+"\" type=\"audio/x-wav\"><source src=\""+url2+"\" type=\"audio/mpeg\"></source></audio>",
                    "styles": {"display": "none"}
                }).inject(this.node);
                this.messageAudio = this.messageAudioNode.getFirst();
            }
            this.messageAudio.play();
            this.playMessageAudioTime = new Date();
        }
    },
    logout: function(){
        this.isLogout = true;

        if (!this.notRecordStatus){
            this.recordDesktopStatus(function(){
                (layout.authentication ||  new o2.xDesktop.Authentication()).logout();
            }.bind(this.layout));
        }else{
            (layout.authentication ||  new o2.xDesktop.Authentication()).logout();
        }
    }
});
MWF.xDesktop.Layout.Taskitem = new Class({
    initialize: function(app, layout){

        this.layout = layout;
        this.app = app;
        this.node = new Element("div", {
            "styles": this.layout.css.taskItemNode,
            "title": this.app.options.title+((this.app.appId) ? "-"+this.app.appId : "")
        }).inject(this.layout.top.taskbar);

        this.iconNode = new Element("div", {
            "styles": this.layout.css.taskItemIconNode
        }).inject(this.node);
        this.iconNode.setStyle("background-image", "url("+this.app.options.icon+")");

        this.closeNode = new Element("div", {
            "styles": this.layout.css.taskItemCloseNode
        }).inject(this.node);
        //this.closeNode.

        this.textNode = new Element("div", {
            "styles": this.layout.css.taskItemTextNode
        }).inject(this.node);
        this.textNode.set("text", this.app.options.title);

        this.setTaskitemSize();
        this.setEvent();
    },
    setTaskitemSize: function(){
        var x1 = 10;
        var x2 = 5;

        var size = this.layout.top.taskbar.getSize();
        var taskItems = this.layout.top.taskbar.getChildren();

        var allWidth = 0;
        if (taskItems.length){
            var w = taskItems[0].getStyles("border-left-width", "border-right-width", "margin-left", "margin-right", "padding-left", "padding-right");
            for (var i=0; i<taskItems.length; i++){
                taskItems[i].setStyle("width", "auto");
                allWidth += taskItems[i].getSize().x+w["border-left-width"].toInt()+w["border-right-width"].toInt()+w["margin-left"].toInt()+w["margin-right"].toInt()+w["padding-left"].toInt()+w["padding-right"].toInt();
            }
            if (allWidth>(size.x-x1)){
                var x = (size.x-x1)/taskItems.length;
                var width = x-w["border-left-width"].toInt()-w["border-right-width"].toInt()-w["margin-left"].toInt()-w["margin-right"].toInt()-w["padding-left"].toInt()-w["padding-right"].toInt();
                taskItems.each(function(item){
                    item.setStyle("width", ""+width+"px");
                });
            }else{
                taskItems.each(function(item){
                    item.setStyle("width", "auto");
                });
            }
        }

        // var x = (size.x-x1)/taskItems.length;
        // if (x<165){
        //     var width = x-x2;
        //     taskItems.each(function(item){
        //         item.setStyle("width", ""+width+"px");
        //     });
        // }else{
        //     taskItems.each(function(item){
        //         item.setStyle("width", "auto");
        //     });
        //     //this.node.setStyle("width", "160px");
        // }
    },

    setText: function(str){
        this.textNode.set("text", str || this.app.options.title);
    },
    setEvent: function(){
        this.textNode.addEvents({
            "mouseover": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_over);
            }.bind(this),
            "mouseout": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode);
            }.bind(this),
            "mousedown": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_down);
            }.bind(this),
            "mouseup": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_over);
            }.bind(this),
            "click": function(){
                if (this.layout.currentApp==this.app){
                    this.app.minSize();
                }else{
                    this.app.setCurrent();
                }
            }.bind(this)
        });
        this.iconNode.addEvents({
            "mouseover": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_over);
            }.bind(this),
            "mouseout": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode);
            }.bind(this),
            "mousedown": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_down);
            }.bind(this),
            "mouseup": function(){
                if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this) this.node.setStyles(this.layout.css.taskItemNode_over);
            }.bind(this),
            "click": function(){
                if (this.layout.currentApp==this.app){
                    this.app.minSize();
                }else{
                    this.app.setCurrent();
                }
            }.bind(this)
        });

        this.node.addEvents({
            "mouseover": function(){
                //if (this.layout.currentApp!==this.app)
                    this.closeNode.fade("in");
            }.bind(this),
            "mouseout": function(){
                //if (this.layout.currentApp!==this.app)
                    this.closeNode.fade("out");
            }.bind(this)
        });
        this.closeNode.addEvent("click", function(){
            this.app.close();
        }.bind(this));
    },
    selected: function(){
        this.node.setStyles(this.layout.css.taskItemNode_current);
        //this.closeNode.setStyles(this.layout.css.taskItemCloseNode_current);
    },
    unSelected: function(){
        this.node.setStyles(this.layout.css.taskItemNode);
        //this.closeNode.setStyles(this.layout.css.taskItemCloseNode);
    },
    changStyle: function(){
        if (this.node){
            if (!this.layout.currentApp || this.layout.currentApp.taskitem!=this){
                this.node.setStyles(this.layout.css.taskItemNode);
            }else{
                this.node.setStyles(this.layout.css.taskItemNode);
                this.node.setStyles(this.layout.css.taskItemNode_current);
            }
        }
        if (this.iconNode) this.iconNode.setStyles(this.layout.css.taskItemIconNode);
        if (this.textNode) this.textNode.setStyles(this.layout.css.taskItemTextNode);
    },
    destroy: function(){
        this.node.destroy();
        o2.release(this);
        //this.layout.setTaskitemSize();
    }
});

MWF.xDesktop.Layout.Top = new Class({
    initialize: function(node, layout){
        this.layout = layout;
        this.node = $(node);
        this.userApplicationsLog = [];
        this.sysApplicationsLog = [];
    },
    load: function(){
        this.loadMenuAction();
        this.loadSeparate();

        this.loadShowDesktop();
        this.loadClock();
        this.loadSeparate("right");

        this.loadUserMenu();
        this.loadStyleAction();
        this.loadUserChat();
        this.loadMessageAction();
        this.loadSeparate("right");

        this.loadTaskbar();
    },
    loadMenuAction: function(){
        this.loadMenuAction = new Element("div", {
            "styles": this.layout.css.loadMenuAction,
            "title": MWF.LP.desktop.menuAction
        }).inject(this.node);
        //	this.loadMenu();
        this.loadMenuAction.addEvent("click", function(){
            this.loadMenu();
        }.bind(this));
    },
    loadShowDesktop: function(){
        this.showDesktopAction = new Element("div", {
            "styles": this.layout.css.showDesktopAction
        }).inject(this.node);
        this.showDesktopAction.addEvents({
            "mouseover": function(){
                this.showDesktopAction.setStyles(this.layout.css.showDesktopAction_over);
            }.bind(this),
            "mouseout": function(){
                this.showDesktopAction.setStyles(this.layout.css.showDesktopAction);
            }.bind(this),
            "click": function(){
                var flag = true;
                Object.each(this.layout.apps, function(app, id){
                    if (!app.window.isHide){
                        flag = false;
                        app.minSize();
                    }
                }.bind(this));
                if (flag){
                    Object.each(this.layout.apps, function(app, id){
                        app.setCurrent();
                    }.bind(this));
                }
            }.bind(this)
        });
    },

    loadMenu: function(){
        this.createApplicationMenuArea();
        this.showApplicationMenu();

        window.setTimeout(function(){
            o2.require("o2.widget.PinYin", null, false);
            this.getApplicationsCatalogue(function(catalog){
                //var currentName = this.layout.session.user.name;
                var user = this.layout.session.user;
                var currentNames = [user.name, user.distinguishedName, user.id, user.unique];
                if (user.roleList) currentNames = currentNames.concat(user.roleList);
                if (user.groupList) currentNames = currentNames.concat(user.groupList);

                catalog.each(function(value, key){
                    var isAllow = true;
                    if (value.allowList) isAllow = (value.allowList.length) ? (value.allowList.isIntersect(currentNames)) : true;
                    var isDeny = false;
                    if (value.denyList) isDeny = (value.denyList.length) ? (value.denyList.isIntersect(currentNames)) : false;
                    if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                        this.createApplicationMenu(value, key);
                    }
                }.bind(this));

                this.getComponentList(function(json){
                    json.data.each(function(value, key){
                        if (value.visible){
                            var isAllow = (value.allowList.length) ? (value.allowList.isIntersect(currentNames)) : true;
                            var isDeny = (value.denyList.length) ? (value.denyList.isIntersect(currentNames)) : false;
                            if ((!isDeny && isAllow) || MWF.AC.isAdministrator()){
                                this.createApplicationMenu(value, key);
                            }
                        }
                    }.bind(this));
                }.bind(this));

                var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_portal_assemble_surface");
                action.invoke({"name": "listPortalApplication", "success": function(json){
                    if (json.data){
                        json.data.each(function(app){
                            this.createPortalAppMenu(app);
                        }.bind(this));
                    }
                }.bind(this)});

            }.bind(this));


            this.getProcessApplications(function(json){
                //json.data.each(function(app){
                //    if( app.isCMSApp ){
                //        this.createCMSAppMenu(app);
                //    }else{
                //        this.createProcessAppMenu(app);
                //    }
                //}.bind(this));
            }.bind(this));

        }.bind(this), 0);

    },

    showApplicationMenu: function(){
        this.applicationMenuAreaMark.fade(0.7);
        this.applicationMenuArea.fade("in");
    },
    closeApplicationMenu: function(){
        if (this.applicationMenuSearchBar) this.closeApplicationMenuSearchBar();
        if (!this.isApplicationMenuScroll){
            this.applicationMenuAreaMark.destroy();
            this.applicationMenuArea.destroy();
            this.applicationMenuFxScroll = null;
            this.userApplicationsLog = [];
            this.sysApplicationsLog = [];
            this.layout.removeEvent("resize", this.resizeApplicationMenuSizeFun);
        }
        this.currentApplicationMenuContent = "app";
        this.isApplicationMenuScroll = false;
        if (this.closeApplicationMenuFun) document.body.removeEvent("keydown", this.closeApplicationMenuFun);
    },
    closeApplicationMenuEsc: function(e){
        if (e.key==="esc"){
            if (!this.applicationMenuSearchBar){
                this.closeApplicationMenu();
            }
        }
    },
    createCMSAppMenu: function(app){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.applicationMenuNode,
            "title": app.appName
        }).inject(this.applicationMenuProcessArea);

        this.userApplicationsLog.push({
            "node": applicationMenuNode,
            "py": app.appName.toPY().toLowerCase(),
            "name": app.appName.toLowerCase(),
            "first": app.appName.toPYFirst().toLowerCase()
        });

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.applicationMenuIconNode
        }).inject(applicationMenuNode);
        var icon = "";
        if (app.appIcon){
            icon = "url(data:image/png;base64,"+app.appIcon+")";
        }else{
            icon = "url(../x_component_cms_Index/$Main/default/icon/column.png)";
        }
        applicationMenuIconNode.setStyle("background-image", icon);

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": app.appName
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openCMSApp(e, app);
            this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {
                "icon": icon,
                "title": app.name,
                "par": "cms.Module#{\"columnId\": \""+app.id+"\", \"appId\": \"cms.Module"+app.id+"\"}"
            },
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                this.showApplicationMenu();
            }.bind(this)
        });

    },
    createPortalAppMenu: function(app){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.applicationMenuNode,
            "title": app.name
        }).inject(this.applicationMenuAppIconArea);
        this.userApplicationsLog.push({
            "node": applicationMenuNode,
            "py": app.name.toPY().toLowerCase(),
            "name": app.name.toLowerCase(),
            "first": app.name.toPYFirst().toLowerCase()
        });

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.applicationMenuIconNode
        }).inject(applicationMenuNode);
        var icon = "";
        if (app.icon){
            icon = "url(data:image/png;base64,"+app.icon+")";
        }else{
            icon = "url(../x_component_portal_PortalExplorer/$Main/default/icon/application.png)";
        }
        applicationMenuIconNode.setStyle("background-image", icon);

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": app.name
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openPortalApp(e, app);
            this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {"icon": icon, "title": app.name, "par": "portal.Portal#{\"portalId\": \""+app.id+"\", \"appId\": \"portal.Portal"+app.id+"\"}"},
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                this.showApplicationMenu();
            }.bind(this)
        });
    },
    createQueryAppMenu: function(app){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.applicationMenuNode,
            "title": app.name
        }).inject(this.applicationMenuProcessArea);
        this.userApplicationsLog.push({
            "node": applicationMenuNode,
            "py": app.name.toPY().toLowerCase(),
            "name": app.name.toLowerCase(),
            "first": app.name.toPYFirst().toLowerCase()
        });

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.applicationMenuIconNode
        }).inject(applicationMenuNode);
        var icon = "";
        if (app.icon){
            icon = "url(data:image/png;base64,"+app.icon+")";
        }else{
            icon = "url(../x_component_query_Query/$Main/appicon.png)";
        }
        applicationMenuIconNode.setStyle("background-image", icon);

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": app.name
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openQueryApp(e, app);
            this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {"icon": icon, "title": app.name, "par": "query.Query#{\"id\": \""+app.id+"\", \"appId\": \"query.Query"+app.id+"\"}"},
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                this.showApplicationMenu();
            }.bind(this)
        });
    },
    createProcessAppMenu: function(app){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.applicationMenuNode,
            "title": app.name
        }).inject(this.applicationMenuProcessArea);
        this.userApplicationsLog.push({
            "node": applicationMenuNode,
            "py": app.name.toPY().toLowerCase(),
            "name": app.name.toLowerCase(),
            "first": app.name.toPYFirst().toLowerCase()
        });

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.applicationMenuIconNode
        }).inject(applicationMenuNode);
        var icon = "";
        if (app.icon){
            icon = "url(data:image/png;base64,"+app.icon+")";
        }else{
            icon = "url(../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)";
        }
        applicationMenuIconNode.setStyle("background-image", icon);

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": app.name
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openProcessApp(e, app);
            this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {"icon": icon, "title": app.name, "par": "process.Application#{\"id\": \""+app.id+"\", \"appId\": \"process.Application"+app.id+"\"}"},
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                this.showApplicationMenu();
            }.bind(this)
        });
    },
    createApplicationMenu: function(value, key){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.applicationMenuNode,
            "title": value.title
        }).inject(this.applicationMenuAppIconArea);

        this.sysApplicationsLog.push({
            "node": applicationMenuNode,
            "py": value.title.toPY().toLowerCase(),
            "name": value.title.toLowerCase(),
            "first": value.title.toPYFirst().toLowerCase()
        });

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.applicationMenuIconNode
        }).inject(applicationMenuNode);

        var icon;
        if (value.path.substring(0, 4)==="@url"){
            if (value.iconPath){
                icon = value.iconPath;
            }else{
                icon = "../x_component_Setting/$Main/default/icon/site.png";
            }
        }else{
            icon = "../x_component_"+value.path.replace(/\./g, "_")+"/$Main/"+value.iconPath;
        }
        applicationMenuIconNode.setStyle("background-image", "url("+icon+")");

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": value.title
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openApplication(e, value.path);

            this.closeApplicationMenu();
        }.bind(this));
        applicationMenuNode.makeLnk({
            "par": {"icon": icon, "title": value.title, "par": value.path},
            "onStart": function(){
                this.applicationMenuAreaMark.fade("out");
                this.applicationMenuArea.fade("out");
            }.bind(this),
            "onComplete": function(){
                this.showApplicationMenu();
            }.bind(this)
        });

        var appName = value.path;
        //if (value.widgetName){
        //    if (!(value.widgetVisible===false)){
        //        this.createApplicationWidgetMenu(value.widgetName, value.widgetTitle, value.widgetIconPath, appName);
        //    }
        //    //Object.each(value.widget, function(value, key){
        //    //    this.createApplicationWidgetMenu(value.widgetName, value.widgetTitle, value.widgetIconPath, appName);
        //    //}.bind(this));
        //    //if (value.widgetStart){
        //    //    this.layout.openWidget(null, value.widgetName, appName);
        //    //}
        //
        //}
    },
    createApplicationWidgetMenu: function(name, title, icon, appName){
        var applicationMenuNode = new Element("div", {
            "styles": this.layout.css.widgetMenuNode,
            "title": title
        }).inject(this.applicationMenuWidgetArea);

        var applicationMenuIconNode = new Element("div", {
            "styles": this.layout.css.widgetMenuIconNode
        }).inject(applicationMenuNode);

        var icon = "../x_component_"+appName.replace(/\./g, "_")+"/$"+name+"/"+icon;
        applicationMenuIconNode.setStyle("background-image", "url("+icon+")");

        new Element("div", {
            "styles": this.layout.css.applicationMenuTextNode,
            "text": title
        }).inject(applicationMenuNode);

        applicationMenuNode.addEvent("click", function(e){
            this.layout.openWidget(e, name, appName);
            this.closeApplicationMenu();
        }.bind(this));
    },
    createApplicationMenuArea: function(){
        var index = MWF.xDesktop.zIndexPool.zIndex;

        //mask-----------------------------------------
        this.applicationMenuAreaMark = new Element("iframe", {
            //"src": (Browser.iecomp) ? MWF.defaultPath+"/xDesktop/_blank.html" : "",
            "styles": this.layout.css.applicationMenuAreaMark
        }).inject(this.layout.node);
        //this.applicationMenuAreaMark.contentWindow.document.documentElement.styles.background = "#000000";
        this.applicationMenuAreaMark.setStyle("z-index", index);
        //---------------------------------------------

        //top node ------------------------------------
        this.applicationMenuArea = new Element("div", {
            "styles": this.layout.css.applicationMenuArea
        }).inject(this.layout.node);
        this.applicationMenuArea.setStyle("z-index", index+1);
        //---------------------------------------------

        this.applicationMenuLeftAction = new Element("div", {
            "styles": this.layout.css.applicationMenuLeftAction
        }).inject(this.applicationMenuArea);
        this.applicationMenuRightAction = new Element("div", {
            "styles": this.layout.css.applicationMenuRightAction
        }).inject(this.applicationMenuArea);

        this.applicationMenuScrollArea = new Element("div", {
            "styles": this.layout.css.applicationMenuScrollArea
        }).inject(this.applicationMenuArea);

        this.applicationMenuContentArea = new Element("div", {
            "styles": this.layout.css.applicationMenuContentArea
        }).inject(this.applicationMenuScrollArea);

        ////widget---------------------------------------
        //this.applicationMemuWidgetContent =  new Element("div", {
        //    "styles": this.layout.css.applicationMemuWidgetContent
        //}).inject(this.applicationMenuContentArea);
        //
        //this.applicationMenuWidgetTitleArea = new Element("div", {
        //    "styles": this.layout.css.applicationMenuWidgetTitleArea,
        //    "text": MWF.LP.desktop.widget
        //}).inject(this.applicationMemuWidgetContent);
        //
        //this.applicationMenuWidgetScrollArea = new Element("div", {
        //    "styles": this.layout.css.applicationMenuWidgetScrollArea
        //}).inject(this.applicationMemuWidgetContent);
        //
        //this.applicationMenuWidgetArea = new Element("div", {
        //    "styles": this.layout.css.applicationMenuWidgetArea
        //}).inject(this.applicationMenuWidgetScrollArea);
        ////---------------------------------------------

        //application----------------------------------
        this.applicationMemuAppContent =  new Element("div", {
            "styles": this.layout.css.applicationMemuAppContent
        }).inject(this.applicationMenuContentArea);

        this.applicationMenuAppTitleArea = new Element("div", {
            "styles": this.layout.css.applicationMenuAppTitleArea,
            "text": MWF.LP.desktop.application
        }).inject(this.applicationMemuAppContent);

        this.applicationMenuAppIconScrollArea = new Element("div", {
            "styles": this.layout.css.applicationMenuAppIconScrollArea
        }).inject(this.applicationMemuAppContent);

        this.applicationMenuAppIconArea = new Element("div", {
            "styles": this.layout.css.applicationMenuAppIconArea
        }).inject(this.applicationMenuAppIconScrollArea);
        //---------------------------------------------

        //Process---------------------------------------
        this.applicationMemuProcessContent =  new Element("div", {
            "styles": this.layout.css.applicationMemuProcessContent
        }).inject(this.applicationMenuContentArea);

        this.applicationMenuProcessTitleArea = new Element("div", {
            "styles": this.layout.css.applicationMenuProcessTitleArea,
            "text": MWF.LP.desktop.process
        }).inject(this.applicationMemuProcessContent);

        this.applicationMenuProcessScrollArea = new Element("div", {
            "styles": this.layout.css.applicationMenuProcessScrollArea
        }).inject(this.applicationMemuProcessContent);

        this.applicationMenuProcessArea = new Element("div", {
            "styles": this.layout.css.applicationMenuProcessArea
        }).inject(this.applicationMenuProcessScrollArea);
        //---------------------------------------------

        this.setApplicationMenuSize();

        this.setApplicationMenuEvent();
    },
    setApplicationMenuSize: function(){
        this.resizeApplicationMenuSize();
        this.resizeApplicationMenuSizeFun = this.resizeApplicationMenuSize.bind(this);
        this.layout.addEvent("resize", this.resizeApplicationMenuSizeFun);
    },
    resizeApplicationMenuSize: function(){
        var wSize = this.applicationMenuScrollArea.getSize();
        this.applicationMemuAppContent.setStyle("width", ""+wSize.x+"px");
        //this.applicationMemuWidgetContent.setStyle("width", ""+wSize.x+"px");
        this.applicationMemuProcessContent.setStyle("width", ""+wSize.x+"px");
        var x = wSize.x*2;
        this.applicationMenuContentArea.setStyle("width", ""+x+"px");

        var size = this.applicationMenuArea.getSize();
        var titleSize = this.applicationMenuAppTitleArea.getSize();
        var tmt = this.applicationMenuAppTitleArea.getStyle("margin-top").toInt();
        var tmb = this.applicationMenuAppTitleArea.getStyle("margin-bottom").toInt();
        var cmt = this.applicationMenuAppIconScrollArea.getStyle("margin-top").toInt();
        var cmb = this.applicationMenuAppIconScrollArea.getStyle("margin-bottom").toInt();
        var y = size.y - titleSize.y - tmt - tmb - cmt - cmb;
        this.applicationMenuAppIconScrollArea.setStyle("height", ""+y+"px");

        //titleSize = this.applicationMenuWidgetTitleArea.getSize();
        //tmt = this.applicationMenuWidgetTitleArea.getStyle("margin-top").toInt();
        //tmb = this.applicationMenuWidgetTitleArea.getStyle("margin-bottom").toInt();
        //cmt = this.applicationMenuWidgetScrollArea.getStyle("margin-top").toInt();
        //cmb = this.applicationMenuWidgetScrollArea.getStyle("margin-bottom").toInt();
        //y = size.y - titleSize.y - tmt - tmb - cmt - cmb;
        //this.applicationMenuWidgetScrollArea.setStyle("height", ""+y+"px");

        titleSize = this.applicationMenuProcessTitleArea.getSize();
        tmt = this.applicationMenuProcessTitleArea.getStyle("margin-top").toInt();
        tmb = this.applicationMenuProcessTitleArea.getStyle("margin-bottom").toInt();
        cmt = this.applicationMenuProcessScrollArea.getStyle("margin-top").toInt();
        cmb = this.applicationMenuProcessScrollArea.getStyle("margin-bottom").toInt();
        y = size.y - titleSize.y - tmt - tmb - cmt - cmb;
        this.applicationMenuProcessScrollArea.setStyle("height", ""+y+"px");
    },

    setApplicationMenuEvent: function(){
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.applicationMenuAppIconScrollArea, {
                "style":"xDesktop_Menu", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScrollStart": function(){
                    this.isApplicationMenuScroll = true;
                }.bind(this)
            });
            //new MWF.widget.ScrollBar(this.applicationMenuWidgetScrollArea, {
            //    "style":"xDesktop_Menu", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
            //    "onScrollStart": function(){
            //        this.isApplicationMenuScroll = true;
            //    }.bind(this)
            //});
            new MWF.widget.ScrollBar(this.applicationMenuProcessScrollArea, {
                "style":"xDesktop_Menu", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScrollStart": function(){
                    this.isApplicationMenuScroll = true;
                }.bind(this)
            });
        }.bind(this));

        this.applicationMenuRightAction.addEvent("click", function(e){
            this.closeApplicationMenuSearchBar();
            if (!this.currentApplicationMenuContent) this.currentApplicationMenuContent = "app";
            var next = "";
            var nextNode = null;
            var currentNode = null;
            if (this.currentApplicationMenuContent == "app"){
                //nextNode = this.applicationMemuWidgetContent;
                nextNode = this.applicationMemuProcessContent;
                next="process";
                currentNode = this.applicationMemuAppContent;
            }
            //if (this.currentApplicationMenuContent == "widget"){
            //    nextNode = this.applicationMemuProcessContent;
            //    next="process";
            //    currentNode = this.applicationMemuWidgetContent;
            //}
            if (this.currentApplicationMenuContent == "process"){
                nextNode = this.applicationMemuAppContent;
                next="app";
                currentNode = this.applicationMemuProcessContent;
            }

            //this.applicationMenuScrollArea.set("scrollLeft", "0px");
            //if (this.applicationMenuFxScroll){
            //    this.applicationMenuFxScroll = null;
            //    delete this.applicationMenuFxScroll
            //}

            if (!this.applicationMenuFxScroll) this.applicationMenuFxScroll = new Fx.Scroll(this.applicationMenuScrollArea, {"wheelStops": false});
            nextNode.inject(currentNode, "after");
            this.applicationMenuFxScroll.set(0);

            this.applicationMenuFxScroll.toElement(nextNode, "x").chain(function(){
                //currentNode.inject(nextNode, "after");
                //this.applicationMenuFxScroll.set(0);
                //this.applicationMenuFxScroll.toLeft();
            }.bind(this));
            //this.applicationMenuScrollArea.toElement(nextNode);
            this.currentApplicationMenuContent = next;
            e.stopPropagation();
        }.bind(this));
        this.applicationMenuLeftAction.addEvent("click", function(e){
            this.closeApplicationMenuSearchBar();
            if (!this.currentApplicationMenuContent) this.currentApplicationMenuContent = "app";
            var next = "";
            var nextNode = null;
            var currentNode = null;
            if (this.currentApplicationMenuContent == "app"){
                nextNode = this.applicationMemuProcessContent;
                next="process";
                currentNode = this.applicationMemuAppContent;
            }
            //if (this.currentApplicationMenuContent == "widget"){
            //    nextNode = this.applicationMemuAppContent;
            //    next="app";
            //    currentNode = this.applicationMemuWidgetContent;
            //}
            if (this.currentApplicationMenuContent == "process"){
                //nextNode = this.applicationMemuWidgetContent;
                nextNode = this.applicationMemuAppContent;
                next="app";
                currentNode = this.applicationMemuProcessContent;
            }

            //nextNode.inject(currentNode, "before");
            if (!this.applicationMenuFxScroll) this.applicationMenuFxScroll = new Fx.Scroll(this.applicationMenuScrollArea, {"wheelStops": false});
            nextNode.inject(currentNode, "before");
            this.applicationMenuFxScroll.set(this.applicationMenuScrollArea.getScrollSize().x);
            this.applicationMenuFxScroll.toElement(nextNode);

            //this.applicationMenuScrollArea.toElement(nextNode);
            this.currentApplicationMenuContent = next;
            e.stopPropagation();
        }.bind(this));

        this.applicationMenuArea.addEvent("click", function(){
            this.closeApplicationMenu();
        }.bind(this));
        this.closeApplicationMenuFun = this.closeApplicationMenuEsc.bind(this);
        document.body.addEvent("keydown", this.closeApplicationMenuFun);

        this.showApplicationSearchFun = this.showApplicationSearch.bind(this);
        document.body.addEvent("keydown", this.showApplicationSearchFun);
    },
    showApplicationSearch: function(e){
        if (e.key==="esc"){
            if (this.applicationMenuSearchBar) this.closeApplicationMenuSearchBar();
        }else{
            if (!this.applicationMenuSearchBar) this.createApplicationMenuSearchBar(e);
        }
    },
    closeApplicationMenuSearchBar: function(){
        if (this.applicationMenuSearchBar){
            this.applicationMenuSearchBar.destroy();
            this.applicationMenuSearchBar = null;
            //if (this.showApplicationSearchFun) document.body.removeEvent("keydown", this.showApplicationSearchFun);

            if ((this.currentApplicationMenuContent === "process") || (!this.currentApplicationMenuContent)){
                this.userApplicationsLog.each(function(o){
                    o.node.setStyle("display", "block");
                }.bind(this));
            }
            if (this.currentApplicationMenuContent === "app"){
                this.sysApplicationsLog.each(function(o){
                    o.node.setStyle("display", "block");
                }.bind(this));
            }
        }
    },
    createApplicationMenuSearchBar: function(e){
        this.applicationMenuSearchBar = new Element("div", {
            "styles": this.layout.css.applicationMenuSearchBar
        }).inject(this.applicationMenuArea);

        this.applicationMenuSearchBarBox = new Element("div", {
            "styles": this.layout.css.applicationMenuSearchBarBox
        }).inject(this.applicationMenuSearchBar);

        this.applicationMenuSearchBarClose = new Element("div", {
            "styles": this.layout.css.applicationMenuSearchBarClose
        }).inject(this.applicationMenuSearchBarBox);
        this.applicationMenuSearchBarInputBox = new Element("div", {
            "styles": this.layout.css.applicationMenuSearchBarInputBox
        }).inject(this.applicationMenuSearchBarBox);

        this.applicationMenuSearchBarInput = new Element("input", {
            "styles": this.layout.css.applicationMenuSearchBarInput
        }).inject(this.applicationMenuSearchBarInputBox);

        this.applicationMenuSearchBarInput.focus();

        this.applicationMenuSearchBarInput.addEvent("keydown", function(e){
            if (e.key==="esc"){
                this.closeApplicationMenuSearchBar();
            }
            if (e.key==="enter"){
                this.doApplicationMenuSearch();
            }
            e.stopPropagation();
        }.bind(this));
        this.applicationMenuSearchBarInput.addEvent("click", function(e){
            e.stopPropagation();
        }.bind(this));

        this.applicationMenuSearchBarClose.addEvent("click", function(e){
            this.closeApplicationMenuSearchBar();
        }.bind(this));
    },
    doApplicationMenuSearch: function(){
        var value = this.applicationMenuSearchBarInput.get("value").toLowerCase();
        if (value){
            if ((this.currentApplicationMenuContent === "process") || (!this.currentApplicationMenuContent) ){
                this.userApplicationsLog.each(function(o){
                    if ((o.py.indexOf(value)===-1) && (o.first.indexOf(value)===-1) && (o.name.indexOf(value)===-1)){
                        o.node.setStyle("display", "none");
                    }else{
                        o.node.setStyle("display", "block");
                    }
                }.bind(this));
            }
            if (this.currentApplicationMenuContent === "app"){
                this.sysApplicationsLog.each(function(o){
                    if ((o.py.indexOf(value)===-1) && (o.first.indexOf(value)===-1) && (o.name.indexOf(value)===-1)){
                        o.node.setStyle("display", "none");
                    }else{
                        o.node.setStyle("display", "block");
                    }
                }.bind(this));
            }
        }
    },

    getApplicationsCatalogue: function(callback){
        var url = MWF.defaultPath+"/xDesktop/$Layout/applications.json";
        MWF.getJSON(url, function(json){
            if (callback) callback(json);
        }.bind(this));
    },
    getProcessApplications: function(callback){
        action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_processplatform_assemble_surface");
        action.invoke({"name": "listApplication", "success": function(json){
            if (json.data){
                json.data.each(function(app){
                    this.createProcessAppMenu(app);
                }.bind(this));
            }
        }.bind(this)});

        action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_cms_assemble_control");
        action.invoke({"name": "listCMSApplication", "success": function(json){
            if (json.data) {
                json.data.each(function (app) {
                    app.name = app.appName;
                    app.icon = app.appIcon;
                    this.createCMSAppMenu(app);
                }.bind(this));
            }
        }.bind(this)});
        action = MWF.Actions.get("x_query_assemble_surface");
        action.listQuery(function(json){
            if (json.data) {
                json.data.each(function (app) {
                    this.createQueryAppMenu(app);
                }.bind(this));
            }
        }.bind(this));


        //var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_processplatform_assemble_surface");
        //action.invoke({"name": "listApplication", "success": function(json){
        //    //add by cxy
        //    var action_cms = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_cms_assemble_control");
        //    action_cms.invoke({"name": "listCMSApplication", "success": function(json2){
        //        var data = json2.data || [];
        //        data.each(function(d){
        //            d.isCMSApp = true;
        //            d.name = d.appName;
        //            d.icon = d.appIcon;
        //            json.data.push(d);
        //        })
        //        if (callback) callback(json)
        //    }})
        //}.bind(this)});
    },
    getComponentList: function(callback){
        var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_component_assemble_control");
        action.invoke({"name": "listComponent", "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    },

//	loadMenu: function(){
//		this.startMenu = new MWF.xDesktop.Menu(this.loadMenuAction, {
//			"event": "click",
//			"style": "desktopStyle",
//			"offsetX": -3,
//			"offsetY": 8,
//		});
//		this.startMenu.load();
//
//		var icon = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/menu/find.png";
//		this.startMenu.addMenuItem("查找", "click", function(){}.bind(this), icon);
//
//		this.startMenu.addMenuLine();
//
//		Properties.navi.each(function(item){
//			this.addStartMenuItem(item, this.startMenu);
//		}.bind(this));
//	},
//	addStartMenuItem: function(item, menu){
//		if (item.sub && item.sub.length>0){
//			startSubMenu = new MWF.xDesktop.Menu(null, {
//				"event": "click",
//				"style": "desktopMenuSub",
//				"offsetX": 2
//			});
//			startSubMenu.load();
//			item.sub.each(function(subitem){
//				this.addStartMenuItem(subitem, startSubMenu);
//			}.bind(this));
//
//			var icon = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/menu/"+item.icon;
//			menu.addMenuMenu(item.title, icon, startSubMenu);
//		}else{
//			var icon = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/menu/"+item.icon;
//			menu.addMenuItem(item.title, "click", function(){}, icon);
//		}
//	},

    loadConfigAction: function(){
        this.configActionNode = new Element("div", {
            "styles": this.layout.css.configActionNode,
            "title": MWF.LP.desktop.configAction
        }).inject(this.node);
        this.configActionNode.addEvent("click", function(){
            alert("show config");
        });
    },
    loadSeparate : function(cssfloat){
        var separateNode = new Element("div.separateNode", {
            "styles": this.layout.css.separateNode
        }).inject(this.node);
        if (cssfloat) separateNode.setStyle("float",cssfloat);
    },
    loadTaskbar: function(){
        this.taskbar = new Element("div", {
            "styles": this.layout.css.taskbar
        }).inject(this.node);
    },
    loadUserChat: function(){
        this.userChatNode = new Element("div", {
            "styles": this.layout.css.userChatNode,
            "title": MWF.LP.desktop.userChat
        }).inject(this.node);

        this.userChatNode.addEvents({
            "mouseover": function(){if (this.layout.css.userChatNode_over) this.userChatNode.setStyles(this.layout.css.userChatNode_over);}.bind(this),
            "mouseout": function(){this.userChatNode.setStyles(this.layout.css.userChatNode);}.bind(this)
        });


        this.userChatNode.addEvent("click", function(e){
             this.userConfig();
             return false;

            if (!this.socket || this.layout.socket.webSocket.readyState != 1) {
                this.layout.socket = new MWF.xDesktop.WebSocket();
            }
            this.layout.openApplication(e, "IM");
            var widget = this.layout.widgets["IMIMWidget"];
            if (widget){
                if (widget.unreadNode){
                    var chat = this.layout.apps["Chat"];
                    if (chat){

                        Object.each(widget.unShowMessage, function(v, k){
                            if (v.length){
                                var dialogue = chat.dialogues[k];
                                if (!dialogue){
                                    widget.getPerson(v[0].from, function(){
                                        dialogue = chat.addDialogue(widget.owner, [widget.users[v[0].from]]);
                                    }.bind(this));
                                }
                            }


                            //if (!dialogue) dialogue = chat.addDialogue(widget.owner, [this.data]);
                        }.bind(this));

                        //var key = this.data.name+layout.desktop.session.user.name;
                        //
                        //dialogue.setCurrent();
                        //    this.clearUnread();
                    }
                    var _self = this;
                    layout.desktop.openApplication(e, "Chat", {
                        "onPostLoad": function(){
                            Object.each(widget.unShowMessage, function(v, k){
                                if (v.length){
                                    widget.getPerson(v[0].from, function(){
                                        dialogue = this.addDialogue(widget.owner, [widget.users[v[0].from]]);
                                    }.bind(this));
                                }
                            }.bind(this));
                        }
                    });
                }
            }


        }.bind(this));

////		this.userActionNode.addEvent("click", function(){
////			alert("show user infor");
////		});
//
//		this.userMenu = new MWF.xDesktop.Menu(this.userActionNode, {
//			"event": "click",
//			"style": "desktopUser",
//			"offsetX": -10,
//			"offsetY": 10
//		});
//		this.userMenu.load();
//        var img = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/usermenu/config.png";
//        this.userMenu.addMenuItem(MWF.LP.desktop.userConfig, "click", function(e){this.userConfig(e);}.bind(this), img);
//
//        this.userMenu.addMenuLine();
//
//		img = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/usermenu/logout.png";
//		this.userMenu.addMenuItem(MWF.LP.desktop.logout, "click", function(){this.logout();}.bind(this), img);

        //this.userPanel = new MWF.xDesktop.UserPanel(this.layout.desktopNode, {"style": this.layout.options.style});
        //this.userPanel.desktop = this.layout;
        //this.userPanel.load();
        //this.userChatNode.addEvent("click", function(){
        //    if (this.layout.socket.webSocket.readyState != 1) {
        //        this.layout.socket = new MWF.xDesktop.WebSocket();
        //    }
        //    this.userPanel.show();
        //}.bind(this));

    },
    userConfig: function(e){
        this.layout.openApplication(e, "Profile");
    },
    logout: function(){
        this.layout.isLogout = true;

        if (!this.notRecordStatus){
            this.layout.recordDesktopStatus(function(){
                (layout.authentication ||  new o2.xDesktop.Authentication()).logout();
            }.bind(this.layout));
        }else{
            (layout.authentication ||  new o2.xDesktop.Authentication()).logout();
        }
    },

    loadStyleAction: function(){
        this.styleActionNode = new Element("div", {
            "styles": this.layout.css.styleActionNode,
            "title": MWF.LP.desktop.styleAction
        }).inject(this.node);
        this.styleActionNode.addEvents({
            "mouseover": function(){if (this.layout.css.styleActionNode_over) this.styleActionNode.setStyles(this.layout.css.styleActionNode_over);}.bind(this),
            "mouseout": function(){this.styleActionNode.setStyles(this.layout.css.styleActionNode);}.bind(this)
        });

        this.setChangeStyle();
    },
    setChangeStyle: function(){
        if (!this.styleMenu){

            this.styleMenu = new MWF.xDesktop.Menu(this.styleActionNode, {
                "event": "click",
                "style": "desktopStyle",
                "offsetX": -60,
                "offsetY": 10,
                "container": this.layout.node,
                "onQueryShow": function(){
                    this.styleMenu.items.each(function(item){
                        if (this.layout.options.style==item.styleName){
                            item.setDisable(true);
                        }else{
                            item.setDisable(false);
                        }
                    }.bind(this));
                    //if (this.layout.options.style=="default"){
                    //    this.styleMenu.items[0].setDisable(true);
                    //}else if (this.layout.options.style=="black"){
                    //    this.styleMenu.items[1].setDisable(true);
                    //}else if (this.layout.options.style=="color"){
                    //    this.styleMenu.items[2].setDisable(true);
                    //}else if (this.layout.options.style=="lotus"){
                    //    this.styleMenu.items[3].setDisable(true);
                    //}else if (this.layout.options.style=="crane"){
                    //    this.styleMenu.items[4].setDisable(true);
                    //}else if (this.layout.options.style=="peony"){
                    //    this.styleMenu.items[5].setDisable(true);
                    //}else if (this.layout.options.style=="car"){
                    //    this.styleMenu.items[6].setDisable(true);
                    //}
                }.bind(this)
            });
            this.styleMenu.load();


            MWF.UD.getPublicData("indexThemes", function(themesJson){
                MWF.getJSON(this.layout.path+"styles.json", function(json){
                    json.each(function(style){
                        if (!themesJson || themesJson.indexOf(style.style)!=-1){
                            var img = MWF.defaultPath+"/xDesktop/$Layout/"+style.style+"/preview.jpg";
                            var memuItem = this.styleMenu.addMenuItem(style.title, "click", function(){this.changeLayoutStyle(style.style);}.bind(this), img);
                            memuItem.styleName = style.style
                        }
                    }.bind(this));
                }.bind(this));

                MWF.UD.getPublicData("indexStyleList", function(json){
                    this.indexStyleList = json;
                    if (!this.indexStyleList) this.indexStyleList = {"styleList": []};

                    this.indexStyleList.styleList.each(function(style){
                        if (style.enabled){
                            //MWF.UD.getPublicData(style.id, function(styleData){
                            //    var img = styleData.data.desktop.desktop.background;
                                var img = style.preview;
                                //img = img.substr(4, img.length-1);
                                var memuItem = this.styleMenu.addMenuItem(style.title, "click", function(){this.changeLayoutStyle(style.id, true);}.bind(this), img);
                                memuItem.styleName = style.id
                            //}.bind(this), false);
                        }
                    }.bind(this));
                    this.styleMenu.addMenuLine();
                    var memuItem = this.styleMenu.addMenuItem(o2.LP.flat_style, "click", function(e){this.changeToFlatStyle(e);}.bind(this));
                    var imgDiv = memuItem.item.getFirst();
                    var imgNode = new Element("div", {"styles": memuItem.menu.css.menuItemImg}).inject(imgDiv);
                    imgNode.setStyle("background-color", "#4A90E2");

                }.bind(this));
            }.bind(this));


            //var img = MWF.defaultPath+"/xDesktop/$Layout/default/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu["default"], "click", function(){this.changeLayoutStyle("default");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/black/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.black, "click", function(){this.changeLayoutStyle("black");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/color/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.color, "click", function(){this.changeLayoutStyle("color");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/lotus/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.lotus, "click", function(){this.changeLayoutStyle("lotus");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/crane/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.crane, "click", function(){this.changeLayoutStyle("crane");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/peony/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.peony, "click", function(){this.changeLayoutStyle("peony");}.bind(this), img);
            //
            //img = MWF.defaultPath+"/xDesktop/$Layout/car/preview.jpg";
            //this.styleMenu.addMenuItem(MWF.LP.desktop.styleMenu.car, "click", function(){this.changeLayoutStyle("car");}.bind(this), img);
        }
    },
    changeToFlatStyle: function(e){
        // MWF.xDesktop.confirm("infor", e, o2.LP.desktop.changeViewTitle, {"html": o2.LP.desktop.changeView}, 500, 100, function(){
        //         this.close();
                var uri = new URI(window.location.href);
                uri.setData("view", "default");
                uri.go();
        //     }, function(){
        //         this.close();
        //     }, null, null, "o2"
        // );
    },
    changeLayoutStyle: function(style, isCustom){
        MWF.UD.deleteData("layoutDesktop", function(){
            this.layout.changStyle(style, isCustom);
        }.bind(this));
    },
    loadMessageAction: function(){
        this.messageActionNode = new Element("div", {
            "styles": this.layout.css.messageActionNode,
            "title": MWF.LP.desktop.showMessage
        }).inject(this.node);

        this.messageActionNode.addEvents({
            "mouseover": function(){if (this.layout.css.messageActionNode_over) this.messageActionNode.setStyles(this.layout.css.messageActionNode_over);}.bind(this),
            "mouseout": function(){this.messageActionNode.setStyles(this.layout.css.messageActionNode);}.bind(this)
        });

        this.layout.message = new MWF.xDesktop.Message(this.layout);
        this.layout.message.load();

        this.messageActionNode.addEvent("click", function(){
            this.showDesktopMessage();
        }.bind(this));
    },
    showDesktopMessage: function(){
        if (!this.layout.message.isShow){
			// this.layout.message.addMessage({
			// 	"subject": "测试消息",
			// 	"content": "这是一个测试消息，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果，看看效果"
			// });
            this.layout.message.show();
        }
    },
    loadUserMenu: function(){
        this.userMenuNode = new Element("div", {
            "styles": this.layout.css.userMenuNode,
            "title": MWF.LP.desktop.userMenu
        }).inject(this.node);
        this.userMenuNode.addEvents({
            "mouseover": function(){if (this.layout.css.userMenuNode_over) this.userMenuNode.setStyles(this.layout.css.userMenuNode_over);}.bind(this),
            "mouseout": function(){this.userMenuNode.setStyles(this.layout.css.userMenuNode);}.bind(this)
        });

        this.userMenu = new MWF.xDesktop.Menu(this.userMenuNode, {
            "event": "click",
            "style": "desktopUser",
            "offsetX": -10,
            "offsetY": 10,
            "container": this.layout.node
        });

        this.userMenu.load();
        var img = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/usermenu/config.png";
        this.userMenu.addMenuItem(MWF.LP.desktop.userConfig, "click", function(e){this.userConfig(e);}.bind(this), img);

        this.userMenu.addMenuLine();

        img = MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/usermenu/logout.png";
        this.userMenu.addMenuItem(MWF.LP.desktop.logout, "click", function(){this.logout();}.bind(this), img);
    },
    loadClock: function(){
        this.clockNode = new Element("div", {
            "styles": this.layout.css.clockNode
        }).inject(this.node);
        this.setTime();
    },
    setTime: function(){
        var now = new Date();
        var ms = 1000-now.getMilliseconds();
        var ss = 60-now.getSeconds();

        var d = now.format("%Y/%m/%d#%H:%M");
        dl = d.split("#");
        this.clockNode.set("html", dl[1]+"<br/>"+dl[0]);

        window.setTimeout(this.setTime.bind(this), ss*1000+ms);
    },
    changStyle: function(){
        if (this.loadMenuAction) this.loadMenuAction.setStyles(this.layout.css.loadMenuAction);
        if (this.configActionNode) this.configActionNode.setStyles(this.layout.css.configActionNode);
        var separateNodeStyle = this.layout.css.separateNode;
        delete separateNodeStyle['float'];
        this.node.getElements(".separateNode").setStyles(separateNodeStyle);
        if (this.userChatNode) this.userChatNode.setStyles(this.layout.css.userChatNode);
        if (this.styleActionNode) this.styleActionNode.setStyles(this.layout.css.styleActionNode);
        if (this.messageActionNode) this.messageActionNode.setStyles(this.layout.css.messageActionNode);

        if (this.clockNode) this.clockNode.setStyles(this.layout.css.clockNode);
        if (this.userMenuNode) this.userMenuNode.setStyles(this.layout.css.userMenuNode);
        if (this.styleActionNode) this.styleActionNode.setStyles(this.layout.css.styleActionNode);
        if (this.userActionNode) this.userActionNode.setStyles(this.layout.css.userActionNode);

        if (this.taskbar) this.taskbar.setStyles(this.layout.css.taskbar);
        if (this.userPanel) this.userPanel.changStyle(this.layout.options.style);
    }
});

MWF.xDesktop.Layout.Navi = new Class({
    initialize: function(node, layout){
        this.layout = layout;
        this.node = $(node);
        this.navis = [];
    },
    load: function(){
        this.createNaviArea();
        this.loadNavis();
    },
    createNaviArea: function(){
        this.naviNodeBottomArea = new Element("div", {
            "styles": this.layout.css.naviNodeBottomArea
        }).inject(this.node);

        this.naviNodeArea = new Element("div", {
            "styles": this.layout.css.naviNodeArea
        }).inject(this.node);

        //this.editButton = new Element("div", {
        //    "styles": this.layout.css.naviEditButton
        //}).inject(this.naviNodeArea);
        //
        //this.naviNodeArea.addEvents({
        //    "mouseover": function(){ this.editButton.fade("in");}.bind(this),
        //    "mouseout": function(){ this.editButton.fade("out");}.bind(this)
        //});
        //
        //this.editButton.addEvent("click", function(){
        //    this.editQuickNavi();
        //}.bind(this));
    },
    editQuickNavi: function(){

    },
    loadNavis: function(){
        MWF.getJSON(this.layout.path+"quickStart.json", function(json){
            json.navi.each(function(navi){
                this.navis.push(this.createNaviNode(navi));
            }.bind(this));
        }.bind(this));
    },
    createNaviNode: function(navi){
        var node = new Element("div", {
            "styles": this.layout.css.mainNaviNode
        }).inject(this.naviNodeArea);
        node.store("navi", navi);

        var iconNode = new Element("div", {
            "styles": this.layout.css.mainNaviIconNode
        }).inject(node);
        iconNode.setStyle("background-image", "url("+MWF.defaultPath+"/xDesktop/$Layout/"+this.layout.options.style+"/navi/"+navi.icon+")");
        new Element("div", {
            "styles": this.layout.css.mainNaviTextNode,
            "text": navi.title
        }).inject(node);

        var _self = this;
        node.addEvents({
            "mouseover": function(){
                var navi = this.retrieve("navi");
                this.setStyle("background", "#CCC");
                var icon = navi.icon.replace(".", "_over.");
                this.getFirst("div").setStyle("background-image", "url("+MWF.defaultPath+"/xDesktop/$Layout/"+_self.layout.options.style+"/navi/"+icon+")");
                this.getLast("div").setStyle("color", "#444");
            },
            "mouseout": function(){
                this.setStyle("background", "transparent");
                var navi = this.retrieve("navi");
                this.setStyle("background", "transparent");
                this.getFirst("div").setStyle("background-image", "url("+MWF.defaultPath+"/xDesktop/$Layout/"+_self.layout.options.style+"/navi/"+navi.icon+")");
                this.getLast("div").setStyle("color", "#FFF");
            },
            "mousedown": function(){
                this.setStyle("background", "#FFF");
            },
            "mouseup": function(){
                this.setStyle("background", "#CCC");
            },
            "click": function(e){
                var navi = this.retrieve("navi");

                _self.layout.openApplication(e,navi.action);
            }
        });
        return node;
    },

    changStyle: function(){

        if (this.naviNodeBottomArea) this.naviNodeBottomArea.setStyles(this.layout.css.naviNodeBottomArea);
        if (this.naviNodeArea) this.naviNodeArea.setStyles(this.layout.css.naviNodeArea);
        this.navis.each(function(nv){
            nv.setStyles(this.layout.css.mainNaviNode);
            nv.getFirst("div").setStyles(this.layout.css.mainNaviIconNode);
            nv.getLast("div").setStyles(this.layout.css.mainNaviTextNode);
        }.bind(this));
    }
});

MWF.xDesktop.zIndexPool = {
    zIndex: 100,
    applyZindex: function(){
        var i = this.zIndex;
        this.zIndex = this.zIndex+2;
        return i;
    }
};
