o2.require("o2.xDesktop.Default", null, false);

o2.xApplication.Setting.ModuleMenuDocument = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(explorer, content, options){
        this.setOptions(options);
        this.content = content;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.css = this.app.css;
        this.lp = this.app.lp.module;
        this.load();
    },
    "destroy": function(){
        // this.appDeploymentContent.destroy();
        // this.content.empty();
        // if (this.setContentHeightFun) this.app.removeEvent("resize", this.setContentHeightFun);
        MWF.release(this);
    },
    load: function(){
        this.menuShowArea = new Element("div", {
            styles: {
                "height": "100%",
                "overflow": "hidden",
                "width": "400px",
                "float": "left"
            }
        }).inject(this.content);
        this.actionsArea = new Element("div", {
            styles: {
                "height": "calc( 100% - 40px )",
                "overflow": "hidden",
                "margin-left": "420px",
                "padding": "20px 20px"
            }
        }).inject(this.content);



        this.startMenu = new o2.xApplication.Setting.ModuleMenuDocument.StartMenu(this);
        this.startMenu.addEvents({
            // "onHide": function(){
            //     this.startMenuNode.removeClass("overColor_bg");
            // }.bind(this),
            "onLoad": function(){
                if (!this.startMenu.isShow){
                    this.startMenu.show();
                }

                this.createDefaultMenuDataActions();
                this.createForceMenuDataActions();
                this.createCustomMenuDataActions();
            }.bind(this)
        });
        this.startMenu.load();
    },
    createDefaultMenuDataActions: function(){
        var node = new Element("div", {"styles": {"margin-bottom": "40px", "overflow": "hidden"}}).inject(this.actionsArea);
        new Element("div", {"styles": this.css.menuDataInfor,"text": this.app.lp.saveDefaultMenuDataInfor}).inject(node);
        this.createButton(this.app.lp.saveDefaultMenuData, this.saveDefaultMenuData.bind(this)).inject(node);
        this.createButton(this.app.lp.clearDefaultMenuData, this.clearDefaultMenuData.bind(this), "grayColor_bg", "grayColor_bg", "grayColor_bg").inject(node)

        if (this.startMenu.defaultData){
            this.createButton(this.app.lp.loadDefaultMenuData, this.loadDefaultMenuData.bind(this), "grayColor_bg", "grayColor_bg", "grayColor_bg").inject(node)
        }

    },
    createForceMenuDataActions: function(){
        var node = new Element("div", {"styles": {"margin-bottom": "40px", "overflow": "hidden"}}).inject(this.actionsArea);
        new Element("div", {"styles": this.css.menuDataInfor,"text": this.app.lp.saveForceMenuDataInfor}).inject(node);
        this.createButton(this.app.lp.saveForceMenuData, this.saveForceMenuData.bind(this)).inject(node);
        this.createButton(this.app.lp.clearForceMenuData, this.clearForceMenuData.bind(this), "grayColor_bg", "grayColor_bg", "grayColor_bg").inject(node)

        if (this.startMenu.forceData){
            this.createButton(this.app.lp.loadForceMenuData, this.loadForceMenuData.bind(this), "grayColor_bg", "grayColor_bg", "grayColor_bg").inject(node)
        }
    },
    createCustomMenuDataActions: function(){
        var node = new Element("div", {"styles": {"margin-bottom": "40px", "overflow": "hidden"}}).inject(this.actionsArea);
        new Element("div", {"styles": this.css.menuDataInfor,"text": this.app.lp.clearCustomMenuDataInfor}).inject(node);
        this.createButton(this.app.lp.clearCustomMenuData, this.clearCustomMenuData.bind(this), "grayColor_bg", "grayColor_bg", "grayColor_bg").inject(node)
    },
    createButton: function(text, action, main, over, down){
        var mainClass = main || "mainColor_bg";
        var overClass = over || "overColor_bg";
        var downClass = down || "deepColor_bg";

        var button = new Element("div."+mainClass, {"type": "button", "styles": this.css.buttonNode, "text": text});
        button.addEvents({
            "click": function(){
                if (action) action(button);
            }.bind(this),
            "mouseover": function(){this.removeClass(mainClass); this.removeClass(downClass); this.addClass(overClass);},
            "mouseout": function(){this.removeClass(overClass); this.removeClass(downClass); this.addClass(mainClass);},
            "mousedown": function(){this.removeClass(mainClass); this.removeClass(overClass); this.addClass(downClass);},
            "mouseup": function(){this.removeClass(mainClass); this.removeClass(downClass); this.addClass(overClass);}
        });
        return button
    },
    saveDefaultMenuData: function(){
        if (this.startMenu){
            this.startMenu.resetMenuData();
            if (this.startMenu.menuData){
                o2.UD.putPublicData("defaultMainMenuData", this.startMenu.menuData, function(){
                    this.app.notice(this.app.lp.saveDefaultMenuDataSuccess, "success");
                }.bind(this));
            }
        }
    },
    clearDefaultMenuData: function(bt){
        var _self = this;
        this.app.confirm("warn", bt, this.app.lp.clearDefaultMenuData, this.app.lp.clearDefaultMenuDataConfirm, 380, 120, function(){
            o2.UD.deletePublicData("defaultMainMenuData", function(){
                _self.app.notice(_self.app.lp.clearDefaultMenuDataSuccess, "success");
            });
            this.close();
        }, function(){
            this.close();
        });
    },
    saveForceMenuData: function(){
        if (this.startMenu){
            this.startMenu.resetMenuData();
            if (this.startMenu.menuData){
                o2.UD.putPublicData("forceMainMenuData", this.startMenu.menuData, function(){
                    this.app.notice(this.app.lp.saveForceMenuDataSuccess, "success");
                }.bind(this));
            }
        }
    },
    clearForceMenuData: function(bt){
        var _self = this;
        this.app.confirm("warn", bt, this.app.lp.clearForceMenuData, this.app.lp.clearForceMenuDataConfirm, 380, 120, function(){
            o2.UD.deletePublicData("forceMainMenuData", function(){
                _self.app.notice(_self.app.lp.clearForceMenuDataSuccess, "success");
            });
            this.close();
        }, function(){
            this.close();
        });
    },
    clearCustomMenuData: function(bt){
        var _self = this;
        this.app.confirm("warn", bt, this.app.lp.clearForceMenuData, this.app.lp.clearCustomMenuDataConfirm, 380, 120, function(){
            o2.require("o2.widget.UUID", function(){
                var id = new o2.widget.UUID();
                o2.UD.putPublicData("clearCustomMenuDataFlag", {"id": id.toString()}, function(){
                    _self.app.notice(_self.app.lp.clearForceMenuDataSuccess, "success");
                });
                this.close();
            }.bind(this));
        }, function(){
            this.close();
        });
    },
    loadForceMenuData: function(){
        if (this.startMenu){
            this.startMenu.reload(this.startMenu.forceData);
        }
    },
    loadDefaultMenuData: function(){
        if (this.startMenu){
            this.startMenu.reload(this.startMenu.defaultData);
        }
    }

});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu = new Class({
    Extends: o2.xDesktop.Default.StartMenu,
    Implements: [Events],

    initialize: function (setting) {
        this.layout = layout.desktop;
        this.setting = setting;
        this.container = this.setting.menuShowArea;
        //this.actionNode = this.setting.startMenuNode;
        this.isLoaded = false;
        this.isShow = false;
        this.isMorph = false;
        this.items = [];

        // this.menuData = (this.layout.status && this.layout.status.menuData) ? Object.clone(this.layout.status.menuData) : {
        //     "appList": [],
        //     "processList": [],
        //     "inforList": [],
        //     "queryList": []
        // };

        this.itemTempletedHtml = "" +
            "   <div class='layout_start_item_iconArea'>" +
            "       <div class='layout_start_item_icon'></div>" +
            "   </div>" +
            "   <div class='layout_start_item_text'></div>" +
            "   <div class='layout_start_item_badge'></div>";

        this.checkLayout();
    },
    loadMenuData: function(callback){
        debugger;
        this.menuData = (this.layout.status && this.layout.status.menuData) ? Object.clone(this.layout.status.menuData) : {
            "appList": [],
            "processList": [],
            "inforList": [],
            "queryList": []
        };
        var forceData = null, defaultData = null;
        var forceLoaded = false, defaultLoaded = false;

        var check = function(){
            if (forceLoaded && defaultLoaded){
                this.forceData = forceData;
                this.defaultData = defaultData;
                if (forceData){
                    this.menuData=forceData;
                }else if (defaultData){
                    this.menuData=defaultData;
                }
                if (callback) callback();
            }
        }.bind(this)

        o2.UD.getPublicData("forceMainMenuData", function(fData){
            forceData = fData;
            forceLoaded = true;
            check();
        }.bind(this));
        o2.UD.getPublicData("defaultMainMenuData", function(dData){
            defaultData = dData;
            defaultLoaded = true;
            check();
        }.bind(this));
    },
    load: function(){
        this.loadMenuData(function(){
            //var view = this.layout.path+this.layout.options.style+((o2.session.isMobile || layout.mobile) ? "/layout-menu-mobile.html" : "/layout-menu-pc.html");
            var view = this.layout.path+this.layout.options.style+"/layout-menu-pc.html";
            this.container.loadHtml(view, {"module": this}, function(){
                this.maskNode.destroy();
                this.node.setStyle("z-index", o2.xDesktop.zIndexPool.applyZindex());
                //this.layout.menuNode.setStyle("z-index", o2.xDesktop.zIndexPool.applyZindex());

                this.node.addEvent("mousedown", function(e){
                    e.stopPropagation();
                    e.preventDefault();
                });

                // this.triangleNode = new Element("div.layout_menu_start_triangle").inject(this.layout.menuNode, "after");
                // this.hideMessage = function(){ this.hide(); }.bind(this);
                //this.fireEvent("load");
                this.layout.addEvent("resize", this.setSize.bind(this));
                this.loadTitle();
                this.loadLnks();

                this.setSize();

                this.isLoaded = true;
                this.fireEvent("load");
            }.bind(this));
        }.bind(this));
    },
    setSize: function(){

        if (this.appScrollBar && this.appScrollBar.scrollVNode) this.appScrollBar.scrollVNode.setStyle("margin-top", "0px");
        var isLnk = false;
        if (false && this.layout.lnks && this.layout.lnks.length){
            this.lnkAreaNode.show();
            this.lineNode.show();
            var h = 100*3;
            this.lnkScrollNode.setStyle("height", ""+h+"px");
            isLnk = true;
        }else{
            this.lnkAreaNode.hide();
            this.lineNode.hide();
        }

        debugger;
        if( !this.node.offsetParent )this.node.show();

        var size = this.node.getSize();
        var lnkSizeY = (isLnk) ? this.lnkAreaNode.getSize().y : 0;
        var lineSizeY = (isLnk) ? this.lineNode.getSize().y : 0;
        var mt = (isLnk) ? (this.lineNode.getStyle("margin-top").toInt() || 0) : 0;
        var mb = (isLnk) ? (this.lineNode.getStyle("margin-bottom").toInt() || 0) : 0;
        var titleSize = this.appTitleNode.getSize();

        var y = size.y-lnkSizeY-lineSizeY-mt-mb-titleSize.y - 40;
        this.appScrollNode.setStyle("height", ""+y+"px");
    },
    checkLayout: function(){
        if( !this.layout.path )this.layout.path = o2.session.path+"/xDesktop/$Default/";
        if( !this.layout.options )this.layout.options = {};
        if( !this.layout.options.style )this.layout.options.style = "blue";
    },
    reload: function(menuData){
        this.menuData = menuData || {
            "appList": [],
            "processList": [],
            "inforList": [],
            "queryList": []
        };
        this.container.empty();

        //var view = this.layout.path+this.layout.options.style+((o2.session.isMobile || layout.mobile) ? "/layout-menu-mobile.html" : "/layout-menu-pc.html");
        var view = this.layout.path+this.layout.options.style+"/layout-menu-pc.html";
        this.container.loadHtml(view, {"module": this}, function(){
            if (this.maskNode) this.maskNode.destroy();

            this.node.addEvent("mousedown", function(e){
                e.stopPropagation();
                e.preventDefault();
            });

            this.loadTitle();
            this.loadLnks();

            this.node.setStyles({
                "display": "block",
                "position": "relative",
                "left": "0",
                "top": "0"
            });

            this.loadJsons(function(){
                (this.currentTab || this.appCategoryTab).click();
            }.bind(this));

            this.isShow = true;
            this.setScroll();

            this.isLoaded = true;
        }.bind(this));
    },
    show: function(){
        this.node.setStyles({
            "display": "block",
            "position": "relative",
            "left": "0",
            "top": "0"
        });
        this.appAreaNode.setStyles({ "filter": "" });

        this.loadJsons(function(){
            (this.currentTab || this.appCategoryTab).click();
        }.bind(this));

        this.isShow = true;
        this.isMorph = false;
        this.setScroll();
    },
    hide: function(){

    },
    createApplicationMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.Item(this, this.appContentNode, value));
    },
    createPortalMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.PortalItem(this, this.appContentNode, value));
    },
    createProcessMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.ProcessItem(this, this.appContentNode, value));
    },
    createInforMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.InforItem(this, this.appContentNode, value));
    },
    createQueryMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.QueryItem(this, this.appContentNode, value));
    },
    createGroupMenuItem: function(value){
        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.GroupItem(this, this.appContentNode, value));
    },
    resetMenuData: function(){
        if (!this.menuData) this.menuData = {
            "appList": [],
            "processList": [],
            "inforList": [],
            "queryList": []
        }
        var nodes = this.appContentNode.getChildren();
        var data = [];

        nodes.each(function(node){
            var item = node.retrieve("item");
            if (item){
                if (item.data.type==="group"){
                    var d = {
                        "id": item.data.id,
                        "name": item.data.name,
                        "type": item.data.type,
                        "itemDataList": []
                    }
                    if (item.data.itemDataList) item.data.itemDataList.each(function(i){
                        d.itemDataList.push(i);
                    });
                    data.push(d);
                }else{
                    data.push({
                        "id": item.data.id,
                        "name": item.data.name,
                        "type": item.data.type,
                    });
                }
            }
        }.bind(this));
        if (this.currentTab === this.appCategoryTab){
            this.menuData.appList = data;
        }else if (this.currentTab === this.processCategoryTab){
            this.menuData.processList = data;
        }else if (this.currentTab === this.inforCategoryTab){
            this.menuData.inforList = data;
        }else if (this.currentTab === this.queryCategoryTab){
            this.menuData.queryList = data;
        }
        //this.layout.menuData = this.menuData;
    },
    defaultMenu: function(){
        this.menuData = null;
        this.reload();
    }
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.Item = new Class({
    Extends: o2.xDesktop.Default.StartMenu.Item,

    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    }
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.GroupItem = new Class({
    Extends: o2.xDesktop.Default.StartMenu.GroupItem,
    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    },
    loadItems: function(){
        if (!this.items) this.items = [];
        this.data.itemDataList.each(function(data){
            var item = this.items.find(function(i){
                return i.data.id == data.id;
            });
            if (!item){
                switch (data.type){
                    case "portal":
                        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.PortalItem(this, this.menuContentNode, data));
                        break;
                    case "process":
                        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.ProcessItem(this, this.menuContentNode, data));
                        break;
                    case "cms":
                        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.InforItem(this, this.menuContentNode, data));
                        break;
                    case "query":
                        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.QueryItem(this, this.menuContentNode, data));
                        break;
                    default:
                        this.items.push(new o2.xApplication.Setting.ModuleMenuDocument.StartMenu.Item(this, this.menuContentNode, data));
                }
            }
        }.bind(this));
    },
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.PortalItem = new Class({
    Extends: o2.xDesktop.Default.StartMenu.PortalItem,
    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    }
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.ProcessItem = new Class({
    Extends: o2.xDesktop.Default.StartMenu.ProcessItem,
    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    }
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.InforItem = new Class({
    Extends: o2.xDesktop.Default.StartMenu.InforItem,
    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    }
});
o2.xApplication.Setting.ModuleMenuDocument.StartMenu.QueryItem = new Class({
    Extends: o2.xDesktop.Default.StartMenu.QueryItem,
    loadBadge: function(){
        this.badgeNode.hide();
    },
    _drag_drag: function(dragging, e){
        if (this.dragStatus == "group"){
            if (!this.onGroup) this.checkDargOver(dragging);
            if (!this.overItem){
                this.checkDargPosition(dragging);
            }
        }
    }
});
