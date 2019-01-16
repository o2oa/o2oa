MWF.xApplication.Setting.preview = MWF.xApplication.Setting.preview || {};
MWF.xApplication.Setting.preview.Layout = new Class({
    Extends: MWF.xDesktop.Layout,
    Implements: [Options, Events],
    options: {
        "style": "default",
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
        this.initData();
        this.session.user = layout.desktop.session.user;
        this.node = $(node);
    },
    initNode: function(node){
        this.path = MWF.defaultPath+"/xDesktop/$Layout/";
        var html = "<div id=\"layout\" style=\"overflow: hidden; height: 100%; background-size: cover;\">\n" +
            "            <div id=\"layout_top_shim\"></div>\n" +
            "            <div id=\"layout_top\"></div>\n" +
            "            <div id=\"layout_desktop\">\n" +
            "            <div id=\"desktop_content\">\n" +
            "         \n" +
            "            <div id=\"desktop_navi\"></div>\n" +
            "        </div>\n" +
            "        </div>";
        this.node.set("html", html);
        this.node = this.node.getFirst();

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

        this.node.setStyle("background-image", this.css.desktop.background);
        this.node.setStyles(this.options.styles);
        //"url("+this.css.desktop.background+")"
    },
    initData: function(callback){
        this.apps = {};
        this.widgets = {};
        this.appCurrentList = [];
        this.lnkAreas = [];
        this.lnks = [];
        this.currentApp = null;
        this.status = null;
        this.session = {};
        this.serviceAddressList = null;
    },

    load : function(){
        this.initNode();
        this.loadDesktop();
        this.loadWindow();

        //
        // this.isAuthentication(function(){
        //     if (this.status){
        //         if (this.status.style){
        //             if (this.options.style !== this.status.style){
        //                 this.changStyle(this.status.style);
        //             }
        //         }
        //     }
        //     this.getNodeBackground();
        //
        //     this.loadDesktop();
        //
        //     if (this.session.user.passwordExpired){
        //         this.openApplication({"page":{"x": 0, "y": 0}}, "Profile", {"tab": "passwordConfigPage"});
        //         window.setTimeout(function(){
        //             MWF.xDesktop.notice("error", {"y":"top", "x": "left"}, MWF.LP.desktop.notice.changePassword, this.desktopNode);
        //         }.bind(this), 500);
        //     }
        //
        // }.bind(this));
    },
    loadWindow: function(){
        var options = {
            "style": "desktop_default",
            "title": "app",
            "isResize": true,
            "isMax": true,
            "isRefresh": true,
            "container": this.node,
            "width": "400",
            "height": "280",
            "top": "100",
            "left": "50",
            "fromTop": "0",
            "fromLeft": "0",
            "isMove": false
        };

        this.window = new MWF.xDesktop.Window(this.app, options);
        this.window.css = this.windowCss;
        this.content = this.window.content;
        //if (animation===false){
        this.window.reStyle();
        this.window.showNoAnimation(false, false);
        //}else{
        //    this.window.show();
        //}
    },
    maxOrRestoreSize: function(){},

    loadDesktop: function(){
        this.setHeight();

        var size = this.desktopNode.getSize();
        this.size = {
            "x" : size.x,
            "y": size.y
        };
        this.loadTop();
        this.loadLnkArea();
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


    setHeight: function(){
        this.resizeHeight();
        $(window).addEvent("resize", function(){
            this.resizeHeight();
        }.bind(this));
    },

    resizeHeight: function(){

        var yTop = this.topNode.getSize().y;
        var yBody = this.node.getSize().y;
        var y = yBody - yTop;

        this.desktopNode.setStyle("height", ""+y+"px");
        this.desktopHeight = y;

        var yNavi = this.naviNode.getSize().y;
        y = y - yNavi;
        this.contentNode.setStyle("height", ""+y+"px");

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
        }
    },
    resizeMessage: function(){
        if (this.message) this.message.resize();
    },


    loadTop: function(){
        if (!this.top){
            this.top = new MWF.xApplication.Setting.preview.Layout.Top(this.topNode, this);
            this.top.load();
        }
    },
    getNodeBackground: function(){
        MWF.UD.getDataJson("layoutDesktop", function(json){
            var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+this.options.style+"/desktop.jpg";
            if (json){
                currentSrc = json.src;
                dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+currentSrc+"/desktop.jpg";
            }
            this.node.setStyle("background-image", "url("+dskImg+")");
        }.bind(this), false);
    }
});
MWF.xApplication.Setting.preview.Layout.Taskitem = new Class({
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
        //this.layout.setTaskitemSize();
    }
});

MWF.xApplication.Setting.preview.Layout.Top = new Class({
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
    },
    loadShowDesktop: function(){
        this.showDesktopAction = new Element("div", {
            "styles": this.layout.css.showDesktopAction
        }).inject(this.node);
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
    },

    loadStyleAction: function(){
        this.styleActionNode = new Element("div", {
            "styles": this.layout.css.styleActionNode,
            "title": MWF.LP.desktop.styleAction
        }).inject(this.node);
    },

    loadMessageAction: function(){
        this.messageActionNode = new Element("div", {
            "styles": this.layout.css.messageActionNode,
            "title": MWF.LP.desktop.showMessage
        }).inject(this.node);
    },

    loadUserMenu: function(){
        this.userMenuNode = new Element("div", {
            "styles": this.layout.css.userMenuNode,
            "title": MWF.LP.desktop.userMenu
        }).inject(this.node);
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
    }
});