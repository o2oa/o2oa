MWF.xDesktop.requireApp("cms.ColumnManager", "package", null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Identity", null,false);
MWF.xApplication.cms.ColumnManager.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
        "column": null,
        "application" : null,
		"style": "default",
		"name": "cms.ColumnManager",
		"icon": "icon.png",
		"width": "1100",
		"height": "720",
		"title": MWF.xApplication.cms.ColumnManager.LP.title
	},
	onQueryLoad: function(){
        if(this.options.column)this.options.column.icon = this.options.column.appIcon;
        if(!this.options.application) this.options.application = this.options.column;
		this.lp = MWF.xApplication.cms.ColumnManager.LP;
		this.currentContentNode = null;
	},
    loadApplication: function(callback){

        this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();

        //if (this.status){
        //    if (!this.options.application){
        //        if (this.status.application){
        //            this.restActions.getApplication(this.status.application, function(json){
        //                if (json.data){
        //                    this.options.application = json.data;
        //                    alert("sds"+this.options.application);
        //                }else{
        //                    this.close();
        //                }
        //            }.bind(this), function(){this.close();}.bind(this), false)
        //        }else{
        //            this.close();
        //        }
        //    }
        //}
        this.getColumn(function(){
            this.setTitle( this.options.column.appName );
            this.loadController(function(){
                if( !this.isAdmin ){
                    this.notice(  MWF.CMSCM.LP.noAdministratorAccess  , "error");
                    this.close();
                }else{
                    this.createNode();
                    this.loadApplicationContent();
                    if (callback) callback();
                }
            }.bind(this))
        }.bind(this), function(){
            this.close();
        }.bind(this));
    },
    loadController: function(callback){
        this.controllers = [];
        this.restActions.listColumnController(this.options.column.id, function( json ){
            json.data = json.data || [];
            json.data.each(function(item){
                this.controllers.push(item.adminUid)
            }.bind(this))
            this.isAdmin = MWF.AC.isAdministrator() || this.controllers.contains(layout.desktop.session.user.name);
            if(callback)callback(this.isAdmin);
        }.bind(this));
    },
    getColumn: function(success, failure){
        if (!this.options.column){
            if (this.status) {
                if (this.status.column){
                    this.restActions.getColumn({"id":this.status.column}, function(json){
                        if (json.data){
                            this.options.column = json.data;
                            this.options.application = json.data;
                            if (success) success();
                        }else{
                            if (failure) failure();
                        }
                    }.bind(this), function(){if (failure) failure();}.bind(this), false)
                }else{
                    if (failure) failure();
                }
            }else{
                if (failure) failure();
            }
        }else{
            if (success) success();
        }
    },

    loadApplicationContent: function(){
        this.loadStartMenu();
        this.loadApplicationLayout();
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },
    loadApplicationLayout: function(){
//		this.topMenuNode = new Element("div").inject(this.node);
//		MWF.require("MWF.widget.Toolbar", function(){
//			this.toobar = new MWF.widget.Toolbar(this.topMenuNode);
//			this.toobar.load();
//			alert("ok")
//		}.bind(this));
    },
    loadStartMenu: function(callback){
        this.leftContentNode = new Element("div", {
            "styles": this.css.leftContentNode
        }).inject(this.node);

        this.leftTitleNode = new Element("div", {
            "styles": this.css.leftTitleNode
        }).inject(this.leftContentNode);

        this.leftTitleIconNode = new Element("div", {
            "styles": this.css.leftTitleIconNode
        }).inject(this.leftTitleNode);

        if (this.options.column){
            var icon = this.options.column.icon || this.options.column.appIcon;
            if (icon){
                this.leftTitleIconNode.setStyle("background-image", "url(data:image/png;base64,"+icon+")");
            }else{
                this.leftTitleIconNode.setStyle("background-image", "url("+"/x_component_cms_Column/$Main/default/icon/column.png)");
            }
        }

        this.leftTitleTextNode = new Element("div", {
            "styles": this.css.leftTitleTextNode,
            "text" : this.options.column.appName
        }).inject(this.leftTitleNode);

        this.startMenuNode = new Element("div", {
            "styles": this.css.normalStartMenuNode
        }).inject(this.leftContentNode);

        this.menu = new MWF.xApplication.cms.ColumnManager.Menu(this, this.startMenuNode, {
            "onPostLoad": function(){
                var defaultId = "categoryConfig";
                if (this.status){
                    if (this.status.navi!=null && this.menu.itemObject[this.status.navi]){
                        this.menu.doAction(this.menu.itemObject[this.status.navi]);
                    }else{
                        this.menu.doAction(this.menu.itemObject[defaultId]);
                    }
                }else{
                    this.menu.doAction(this.menu.startNavis[0]);
                }
            }.bind(this)
        });
        //this.addEvent("resize", function(){
        //    if (this.menu) this.menu.onResize();
        //}.bind(this));
    },
    clearContent: function(){
        if (this.categoryConfiguratorContent){
            this.categoryConfiguratorContent.setStyle("display","none");
            //if (this.categoryConfigurator) delete this.categoryConfigurator;
            //this.categoryConfiguratorContent.destroy();
            //this.categoryConfiguratorContent = null;
        }
        if (this.formConfiguratorContent){
            if (this.formConfigurator) delete this.formConfigurator;
            this.formConfiguratorContent.destroy();
            this.formConfiguratorContent = null;
        }
        if (this.propertyConfiguratorContent){
            if (this.property) delete this.property;
            this.propertyConfiguratorContent.destroy();
            this.propertyConfiguratorContent = null;
        }
        if (this.dataConfiguratorContent){
            if (this.dataConfigurator) delete this.dataConfigurator;
            this.dataConfiguratorContent.destroy();
            this.dataConfiguratorContent = null;
        }
        if (this.scriptConfiguratorContent){
            if (this.scriptConfigurator) delete this.scriptConfigurator;
            this.scriptConfiguratorContent.destroy();
            this.scriptConfiguratorContent = null;
        }
        if (this.viewConfiguratorContent){
            if (this.viewConfigurator) delete this.viewConfigurator;
            this.viewConfiguratorContent.destroy();
            this.viewConfiguratorContent = null;
        }
        if (this.queryViewConfiguratorContent){
            if (this.queryViewConfigurator) delete this.queryViewConfigurator;
            this.queryViewConfiguratorContent.destroy();
            this.queryViewConfiguratorContent = null;
        }
    },

    applicationProperty: function(){
        this.clearContent();
        this.propertyConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.property = new MWF.xApplication.cms.ColumnManager.ApplicationProperty(this, this.propertyConfiguratorContent);
        this.property.load();
    },

    cagetoryConfig: function(){
        this.clearContent();
        if( this.categoryConfiguratorContent ) {
            this.categoryConfiguratorContent.setStyle("display","");
            if( this.menu.itemObject["categoryConfig"] ){
                this.menu.expend( this.menu.itemObject["categoryConfig"] );
            }
            this.categoryConfigurator.refresh();
        }else{
            this.categoryConfiguratorContent = new Element("div", {
                "styles": this.css.rightContentNode
            }).inject(this.node);
            this.loadCategoryConfig();
        }
    },
    loadCategoryConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "CategoryExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                var navi = this.menu.itemObject.categoryConfig;
                var subNode = navi.retrieve( "subNode" );
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.categoryConfigurator = new MWF.xApplication.cms.ColumnManager.CategoryExplorer(this.categoryConfiguratorContent, subNode, this.restActions, {
                    "onPostLoadCategoryList" : function(){
                    }.bind(this)
                });
                this.categoryConfigurator.app = this;
                //this.categoryConfigurator.categoryScrollWrapNode = this.menu.naviNode;
                //this.categoryConfigurator.categoryScrollContentNode = this.menu.areaNode;
                this.categoryConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createCategory : function(){
        this.cagetoryConfig();
        if( this.categoryConfigurator ){
            this.categoryConfigurator.categoryList.newCategory();
        }
    },

    formConfig: function(){
        this.clearContent();
        this.formConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadFormConfig();
    },
    loadFormConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "FormExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.formConfigurator = new MWF.xApplication.cms.ColumnManager.FormExplorer(this.formConfiguratorContent, this.restActions, { "title" : "表单配置" });
                this.formConfigurator.app = this;
                this.formConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createForm : function(){
        if( this.formConfigurator ){
            this.formConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "FormExplorer", function(){
                MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                    if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                    this.formConfigurator = new MWF.xApplication.cms.ColumnManager.FormExplorer(this.formConfiguratorContent, this.restActions);
                    this.formConfigurator.app = this;
                    this.formConfigurator._createElement();
                }.bind(this));
            }.bind(this));
        }
    },

    dataConfig: function(){
        this.clearContent();
        this.dataConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadDataConfig();
    },
    loadDataConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "DictionaryExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.dataConfigurator = new MWF.xApplication.cms.ColumnManager.DictionaryExplorer(this.dataConfiguratorContent, this.restActions, { "title" : "数据配置" });
                this.dataConfigurator.app = this;
                this.dataConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createDataConfig : function(){
        if( this.dataConfigurator ){
            this.dataConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "DictionaryExplorer", function(){
                MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                    if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                    this.dataConfigurator = new MWF.xApplication.cms.ColumnManager.DictionaryExplorer(this.dataConfiguratorContent, this.restActions);
                    this.dataConfigurator.app = this;
                    this.dataConfigurator._createElement();
                }.bind(this));
            }.bind(this));
        }
    },

    scriptConfig: function(){
        this.clearContent();
        this.scriptConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadScriptConfig();
    },
    loadScriptConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "ScriptExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.scriptConfigurator = new MWF.xApplication.cms.ColumnManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions, { "title" : "脚本配置" });
                this.scriptConfigurator.app = this;
                this.scriptConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createScriptConfig : function(){
        if( this.scriptConfigurator ){
            this.scriptConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "ScriptExplorer", function(){
                MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                    if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                    this.scriptConfigurator = new MWF.xApplication.cms.ColumnManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions);
                    this.scriptConfigurator.app = this;
                    this.scriptConfigurator._createElement();
                }.bind(this));
            }.bind(this));
        }
    },

    viewConfig: function(){
        this.clearContent();
        this.viewConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadViewConfig();
    },
    loadViewConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "ViewExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.viewConfigurator = new MWF.xApplication.cms.ColumnManager.ViewExplorer(this.viewConfiguratorContent, this.restActions, { "title" : "列表配置" });
                this.viewConfigurator.app = this;
                this.viewConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createView : function(){
        if( this.viewConfigurator ){
            this.viewConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "ViewExplorer", function(){
                MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                    if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                    this.viewConfigurator = new MWF.xApplication.cms.ColumnManager.ViewExplorer(this.viewConfiguratorContent, this.restActions);
                    this.viewConfigurator.app = this;
                    this.viewConfigurator._createElement();
                }.bind(this));
            }.bind(this));
        }
    },

    queryViewConfig: function(){
        this.clearContent();
        this.queryViewConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadQueryViewConfig();
    },
    loadQueryViewConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "QueryViewExplorer", function(){
            MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.queryViewConfigurator = new MWF.xApplication.cms.ColumnManager.QueryViewExplorer(this.queryViewConfiguratorContent, this.restActions, { "title" : "数据视图配置" });
                this.queryViewConfigurator.app = this;
                this.queryViewConfigurator.load();
            }.bind(this));
        }.bind(this));
    },
    createQueryView : function(){
        if( this.queryViewConfigurator ){
            this.queryViewConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "QueryViewExplorer", function(){
                MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                    if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                    this.queryViewConfigurator = new MWF.xApplication.cms.ColumnManager.QueryViewExplorer(this.queryViewConfiguratorContent, this.restActions);
                    this.queryViewConfigurator.app = this;
                    this.queryViewConfigurator._createElement();
                }.bind(this));
            }.bind(this));
        }
    },

    //getCategoryCount: function(){
    //    var size = this.categoryConfiguratorContent.getSize();
    //    categoryCount = parseInt(size.x/182)+5;
    //    return categoryCount;
    //},
    getCategoryCount: function(){
        if (this.categoryConfigurator){
            var size = this.categoryConfigurator.categoryNode.getSize();
            categoryCount = (parseInt(size.x/401)*parseInt(size.y/101))+10;
            return categoryCount;
        }
        return 20;
    },

    showContentNode: function(node){
        if (this.currentContentNode){
//			this.currentContentNode.setStyles({
//				"position": "absolute"
//			});
            this.currentContentNode.fade("hide");
            node.fade("show");
            node.setStyle("display", "node");
            this.currentContentNode = null;
        }
        node.setStyle("display", "block");
        node.fade("show");
        this.currentContentNode = node;
    },
    recordStatus: function(){
        var id = null;
        if (this.menu.currentNavi){
            var naviData = this.menu.currentNavi.retrieve( "naviData" );
            id = naviData.id;
        }
        return {"navi": id, "column": this.options.column.id};
    }

//	onResize: function(){
//		if (this.menu) this.menu.onResize();
//	}
});

MWF.xApplication.cms.ColumnManager.Menu = new Class({
    Implements: [Options, Events],

    initialize: function(app, node, options){
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.currentNavi = null;
        this.status = "start";
        this.startNavis = [];
        this.itemObject = {};
        //this.
        this.load();
    },
    load: function(){
        this.areaNode = new Element("div.startMenuAreaNode", this.app.css.startMenuAreaNode).inject( this.node );

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.node, {
                "style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        var menuUrl = this.app.path+"startMenu.json";
        MWF.getJSON(menuUrl, function(json){
            json.each(function(navi){
                var naviNode = new Element("div", {
                    "styles": this.app.css.startMenuNaviNode
                });
                naviNode.store("naviData", navi);

                var iconNode =  new Element("div", {
                    "styles": this.app.css.startMenuIconNode
                }).inject(naviNode);
                iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.icon+")");
                naviNode.store("iconNode", iconNode );

                var textNode =  new Element("div", {
                    "styles": this.app.css.startMenuTextNode,
                    "text": navi.title
                });
                textNode.inject(naviNode);

                if( navi.expand ){
                    var expandNode =  new Element("div", {
                        "styles": this.app.css.startMenuExpandNode
                    });
                    expandNode.inject(naviNode);
                    naviNode.store("expandNode", expandNode );
                }

                if( navi.create ){
                    var createNode = new Element("div", {
                        "styles": this.app.css.startMenuCreateNode,
                        "title" : "新建"+navi.title
                    });
                    createNode.inject(naviNode);
                    naviNode.store("createNode", createNode );
                    createNode.addEvents({
                        "click" : function(ev){
                            this.obj.app[ this.navi.createAction ]();
                            ev.stopPropagation();
                        }.bind( { obj : this, navi : navi } ),
                        "mouseover" : function(ev){
                            this.createNode.setStyles( this.obj.app.css.startMenuCreateNode_over )
                        }.bind({ obj : this, createNode : createNode }),
                        "mouseout" : function(ev){
                            this.createNode.setStyles( this.obj.app.css.startMenuCreateNode )
                        }.bind({ obj : this, createNode : createNode })
                    })
                }

                naviNode.inject(this.areaNode);

                if( navi.expand ){
                    var subNode = new Element("div", {
                        "styles": this.app.css.startMenuSubContentNode
                    });
                    subNode.inject(this.areaNode);
                    naviNode.store("subNode", subNode );
                }

                this.startNavis.push(naviNode);
                this.itemObject[ navi.id ] = naviNode;

                this.setStartNaviEvent(naviNode, navi);


                //this.setNodeCenter(this.node);
            }.bind(this));
            //this.setStartMenuWidth();
            this.setContentSize();

            this.app.addEvent("resize", this.setContentSize.bind(this));

            this.fireEvent("postLoad");

        }.bind(this));
    },
    setStartNaviEvent: function(naviNode){
        var _self = this;
        naviNode.addEvents({
            "mouseover": function(){ if (_self.currentNavi!=this) this.setStyles(_self.app.css.startMenuNaviNode_over);},
            "mouseout": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.startMenuNaviNode);},
            "mousedown": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.startMenuNaviNode_down);},
            "mouseup": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.startMenuNaviNode_over);},
            "click": function(){
                //if (_self.currentNavi!=this) _self.doAction.apply(_self, [this]);
                _self.doAction.apply(_self, [this]);
            }
        });
    },
    expend : function( naviNode ){
        var isExpand = naviNode.retrieve("isExpand");
        if( !isExpand ){
            var expandNode = naviNode.retrieve("expandNode");
            expandNode.setStyles(this.app.css.startMenuCollapseNode);
            var subNode = naviNode.retrieve("subNode");
            subNode.setStyle( "display" , "" );
            naviNode.store("isExpand",true);
        }
    },
    collapse : function( naviNode ){
        var isExpand = naviNode.retrieve("isExpand");
        if( isExpand ){
            var expandNode = naviNode.retrieve("expandNode");
            expandNode.setStyles(this.app.css.startMenuExpandNode);
            var subNode = naviNode.retrieve("subNode");
            subNode.setStyle( "display" , "none" );
            naviNode.store("isExpand",false);
        }
    },
    doAction: function(naviNode){
        if( this.currentNavi && this.currentNavi == naviNode ){
            var navi = this.currentNavi.retrieve("naviData");
            if( navi.expand ){
                var isExpand = this.currentNavi.retrieve("isExpand");
                if( isExpand ){
                    var expandNode = this.currentNavi.retrieve("expandNode");
                    expandNode.setStyles(this.app.css.startMenuExpandNode);
                    var subNode = this.currentNavi.retrieve("subNode");
                    subNode.setStyle( "display" , "none" );
                    this.currentNavi.store("isExpand",false);
                }else{
                    var expandNode = this.currentNavi.retrieve("expandNode");
                    expandNode.setStyles(this.app.css.startMenuCollapseNode);
                    var subNode = this.currentNavi.retrieve("subNode");
                    subNode.setStyle( "display" , "" );
                    this.currentNavi.store("isExpand",true);
                }
            }
            return;
        }

        if (this.currentNavi){
            this.currentNavi.setStyles(this.app.css.startMenuNaviNode);
            var iconNode = this.currentNavi.retrieve("iconNode");
            var navi = this.currentNavi.retrieve("naviData");
            iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.icon+")");
            if( navi.expand ){
                var expandNode = this.currentNavi.retrieve("expandNode");
                expandNode.setStyles(this.app.css.startMenuExpandNode);
                var subNode = this.currentNavi.retrieve("subNode");
                subNode.setStyle( "display" , "none" );
                this.currentNavi.store("isExpand",false);
            }
        }

        var navi = naviNode.retrieve("naviData");
        var action = navi.action;

        naviNode.setStyles(this.app.css.startMenuNaviNode_current);
        var iconNode = naviNode.retrieve("iconNode");
        iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.selectedIcon+")");

        if( navi.expand ){
            var expandNode = naviNode.retrieve("expandNode");
            expandNode.setStyles(this.app.css.startMenuCollapseNode);
            var subNode = naviNode.retrieve("subNode");
            subNode.setStyle( "display" , "" );
            naviNode.store("isExpand",true);

        }
        this.currentNavi = naviNode;

        if (this.app[action]) this.app[action].apply(this.app);

    },
    setContentSize : function(){
        var size = this.app.content.getSize();
        this.node.setStyle("height", size.y - 82);
    }
});

MWF.xApplication.cms.ColumnManager.ApplicationProperty = new Class({
    initialize: function(app, node){
        this.app = app;
        this.node = $(node);
        this.data = this.app.options.application;

        this.controllerData = [];
        this.controllerList = [];

        this.availableData = [];
        this.availablePersonList = [];
        this.availableDepartmentList = [];
        this.availableCompanyList = [];
    },
    load: function(){
        this.propertyTitleBar = new Element("div", {
            "styles": this.app.css.propertyTitleBar,
            "text": "栏目属性"  //this.data.name || this.data.appName
        }).inject(this.node);

        this.contentNode =  new Element("div", {
            "styles": this.app.css.propertyContentNode
        }).inject(this.node);
        this.contentAreaNode =  new Element("div", {
            "styles": this.app.css.propertyContentAreaNode
        }).inject(this.contentNode);

        this.setContentHeight();
        this.setContentHeightFun = this.setContentHeight.bind(this);
        this.app.addEvent("resize", this.setContentHeightFun);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentNode, {"indent": false});
        }.bind(this));

        this.baseActionAreaNode = new Element("div", {
            "styles": this.app.css.baseActionAreaNode
        }).inject(this.contentAreaNode);

        this.baseActionNode = new Element("div", {
            "styles": this.app.css.propertyInforActionNode
        }).inject(this.baseActionAreaNode);
        this.baseTextNode = new Element("div", {
            "styles": this.app.css.baseTextNode,
            "text": this.app.lp.application.property
        }).inject(this.baseActionAreaNode);

        this.createEditBaseNode();

        this.createPropertyContentNode();

        this.createIconContentNode();

        this.listPermission( function(){
            this.createAvailableNode();
        }.bind(this))

        this.listController( function(  ){
            this.createControllerListNode();
        }.bind(this) )


    },
    listController : function( callback ){
        this.app.restActions.listColumnController(this.data.id, function(json){
                json.data = json.data || [];
                this.controllerData = json.data;
                json.data.each(function( d ){
                    this.controllerList.push( d.adminName );
                }.bind(this))
                callback.call(  )
        }.bind(this), null ,false)
    },
    listPermission:function( callback ){
        this.app.restActions.listColumnPermission(this.data.id, function(json){
            json.data = json.data || [];
            this.availableData = json.data;
            json.data.each(function( d ){
                if(d.usedObjectType == "USER" ){
                    this.availablePersonList.push( d.usedObjectName )
                }else if(d.usedObjectType == "DEPARTMENT"){
                    this.availableDepartmentList.push( d.usedObjectName )
                }else{
                    this.availableCompanyList.push( d.usedObjectName )
                }
            }.bind(this))
            callback.call( )
        }.bind(this), null ,false)
    },
    setContentHeight: function(){
        var size = this.app.content.getSize();
        var titleSize = this.propertyTitleBar.getSize();
        var y = size.y-titleSize.y;
        this.contentNode.setStyle("height", ""+y+"px");
    },

    createIconContentNode: function(){
        this.iconContentTitleNode = new Element("div", {
            "styles": this.app.css.iconContentTitleNode,
            "text": this.app.lp.application.icon
        }).inject(this.contentAreaNode);

        this.iconContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center' style='margin-top: 20px'>";
        html += "<tr><td class='formTitle'><div id='formIconPreview'></div></td><td id='formChangeIconAction'></td></tr>";
        html += "</table>";
        this.iconContentNode.set("html", html);
        this.iconContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);
        this.iconPreviewNode = this.iconContentNode.getElement("div#formIconPreview");
        this.iconActionNode = this.iconContentNode.getElement("td#formChangeIconAction");
        this.iconPreviewNode.setStyles({
            "height": "72px",
            "width": "72px",
            "float": "right"
        });
        var icon = this.data.icon || this.data.appIcon;
        if (icon){
            this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+icon+") center center no-repeat");
        }else{
            this.iconPreviewNode.setStyle("background", "url("+"/x_component_cms_Column/$Main/default/icon/column.png) center center no-repeat")
        }
        var changeIconAction = new Element("div", {
            "styles": {
                "margin-left": "20px",
                "float": "left",
                "background-color": "#FFF",
                "padding": "4px 14px",
                "border": "1px solid #999",
                "border-radius": "3px",
                "margin-top": "10px",
                "font-size": "14px",
                "color": "#666",
                "cursor": "pointer"
            },
            "text": "更改图标"
        }).inject(this.iconActionNode);
        changeIconAction.addEvent("click", function(){
            this.changeIcon();
        }.bind(this));
    },
    changeIcon: function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" type=\"file\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function(){

                var files = fileNode.files;
                if (files.length){
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);

                        var formData = new FormData();
                        formData.append('file', file);
                        //formData.append('name', file.name);
                        //formData.append('folder', folderId);

                        this.app.restActions.updataColumnIcon(this.data.id ,function(){
                            this.app.restActions.getColumn(this.data, function(json){
                                if (json.data){
                                    this.data = json.data;
                                    if (this.data.appIcon){
                                        this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+this.data.appIcon+") center center no-repeat");
                                    }else{
                                        this.iconPreviewNode.setStyle("background", "url("+"/x_component_cms_Column/$Main/default/icon/category2.png) center center no-repeat")
                                    }
                                }
                            }.bind(this), false)
                        }.bind(this), null, formData, file);
                    }
                }

            }.bind(this));
        }
        var fileNode = this.uploadFileAreaNode.getFirst();
        fileNode.click();
    },



    createPropertyContentNode: function(){
        this.propertyContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center' style='margin-top: 20px'>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.alias+"</td><td id='formApplicationAlias'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.description+"</td><td id='formApplicationDescription'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.sort+"</td><td id='formApplicationSort'></td></tr>";
       // html += "<tr><td class='formTitle'>"+this.app.lp.application.type+"</td><td id='formApplicationType'></td></tr>";
   //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);

        this.nameInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name, this.app.css.formInput);
        this.aliasInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationAlias"), this.data.alias, this.app.css.formInput);
        this.descriptionInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        this.sortInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationSort"), this.data.appInfoSeq, this.app.css.formInput);

        //this.typeInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.applicationCategory, this.app.css.formInput);
    },
    createControllerListNode: function(){
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.controllerListTitleNode = new Element("div", {
            "styles": this.app.css.controllerListTitleNode,
            "text": this.app.lp.application.controllerList
        }).inject(this.contentAreaNode);

        this.controllerListContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.administratorsContentNode = new Element("div", {"styles": this.app.css.administratorsContentNode}).inject(this.controllerListContentNode);

        var changeAdministrators = new Element("div", {
            "styles": {
                "margin-left": "40px",
                "float": "left",
                "background-color": "#FFF",
                "padding": "4px 14px",
                "border": "1px solid #999",
                "border-radius": "3px",
                "margin-top": "10px",
                "margin-bottom": "20px",
                "font-size": "14px",
                "color": "#666",
                "cursor": "pointer"
            },
            "text": "设置管理者"
        }).inject(this.contentAreaNode);
        changeAdministrators.addEvent("click", function(){
            this.changeAdministrators();
        }.bind(this));

        if (this.controllerList){
            var explorer = {
                "actions": this.personActions,
                "app": {
                    "lp": this.app.lp
                }
            }
            this.controllerList.each(function(name){
                if (name) var admin = new MWF.widget.Person({"name": name}, this.administratorsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
    },
    changeAdministrators: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }

        var options = {
            "type": "person",
            "title": "设置应用管理者",
            "names": this.controllerList || [],
            "onComplete": function(items){

                this.administratorsContentNode.empty();

                //var controllerList = [];
                //items.each(function(item){
                //    controllerList.push(item.data.name);
                //    var admin = new MWF.widget.Person(item.data, this.administratorsContentNode, explorer, false, null, {"style": "application"});
                //}.bind(this));
                //this.controllerList = controllerList;
                //this.app.restActions.saveApplication(this.data, function(json){
                //
                //}.bind(this));


                var controllerList = [];

                items.each(function(item){
                    controllerList.push(item.data.name);
                    var admin = new MWF.widget.Person(item.data, this.administratorsContentNode, explorer, false, null, {"style": "application"});
                }.bind(this));

                controllerList.each(function(item){
                    if( !this.controllerList.contains( item ) ){
                        var controllerData = {
                            "objectType": "APPINFO",
                            "objectId": this.data.id,
                            "adminUid": item,
                            "adminName": item,
                            "adminLevel": "ADMIN"
                        }
                        this.app.restActions.saveController(controllerData, function(json){
                            controllerData.id = json.data.id;
                            this.controllerData.push( controllerData );
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.controllerList.each(function(item){
                    if( !controllerList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.controllerData.each(function(data){
                            if( data.adminName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.app.restActions.removeController(id, function(json){
                            this.controllerData.erase( ad )
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.controllerList = controllerList;
                this.app.notice(  MWF.CMSCM.LP.setControllerSuccess  , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },

    createAvailableNode: function(){
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.availableTitleNode = new Element("div", {
            "styles": this.app.css.availableTitleNode,
            "text": this.app.lp.application.available
        }).inject(this.contentAreaNode);

        this.availableContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.availableItemsContentNode = new Element("div", {"styles": this.app.css.availableItemsContentNode}).inject(this.availableContentNode);
        this.availableActionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);

        var changeIdentityList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": "设置可用人员"
        }).inject(this.availableActionAreaNode);
        changeIdentityList.addEvent("click", function(){
            this.changeAvailableIdentitys();
        }.bind(this));

        var changeDepartmentList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": "设置可用部门"
        }).inject(this.availableActionAreaNode);
        changeDepartmentList.addEvent("click", function(){
            this.changeAvailableDepartments();
        }.bind(this));

        var changeCompanyList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": "设置可用公司"
        }).inject(this.availableActionAreaNode);
        changeCompanyList.addEvent("click", function(){
            this.changeAvailableCompanys();
        }.bind(this));

        this.setAvailableItems();
    },
    setAvailableItems: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.availablePersonList){
            this.availablePersonList.each(function(name){
                if (name) new MWF.widget.Person({"name": name}, this.availableItemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
        if (this.availableDepartmentList){
            this.availableDepartmentList.each(function(name){
                if (name) new MWF.widget.Department({"name": name}, this.availableItemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
        if (this.availableCompanyList){
            this.availableCompanyList.each(function(name){
                if (name) new MWF.widget.Company({"name": name}, this.availableItemsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
    },

    changeAvailableIdentitys: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        var options = {
            "type": "person",
            "title": "设置应用可用人员",
            "names": this.availablePersonList || [],
            "onComplete": function(items){
                var availablePersonList = [];

                items.each(function(item){
                    availablePersonList.push(item.data.name);
                }.bind(this));

                availablePersonList.each(function(item){
                    if( !this.availablePersonList.contains( item ) ){
                        var permissionData = {
                            "objectType": "APPINFO",
                            "objectId": this.data.id,
                            "usedObjectType": "USER",
                            "usedObjectCode": item,
                            "usedObjectName": item
                        }
                        this.app.restActions.savePermission(permissionData, function(json){
                            permissionData.id = json.data.id;
                            this.availableData.push( permissionData );
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availablePersonList.each(function(item){
                    if( !availablePersonList.contains( item ) ){
                        var ad = null;
                        var id = ""
                        this.availableData.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.app.restActions.removePermission(id, function(json){
                            this.availableData.erase( ad )
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availablePersonList = availablePersonList;
                this.availableItemsContentNode.empty();
                this.setAvailableItems();

                this.app.notice(  MWF.CMSCM.LP.setAvailableIdentitySuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    changeAvailableDepartments: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        var options = {
            "type": "department",
            "title": "设置应用可用部门",
            "names": this.availableDepartmentList || [],
            "onComplete": function(items){
                var availableDepartmentList = [];

                items.each(function(item){
                    availableDepartmentList.push(item.data.name);
                }.bind(this));

                availableDepartmentList.each(function(item){
                    if( !this.availableDepartmentList.contains( item ) ){
                        var permissionData = {
                            "objectType": "APPINFO",
                            "objectId": this.data.id,
                            "usedObjectType": "DEPARTMENT",
                            "usedObjectCode": item,
                            "usedObjectName": item
                        }
                        this.app.restActions.savePermission(permissionData, function(json){
                            permissionData.id = json.data.id;
                            this.availableData.push( permissionData );
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availableDepartmentList.each(function(item){
                    if( !availableDepartmentList.contains( item ) ){
                        var ad = null;
                        var id = ""
                        this.availableData.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.app.restActions.removePermission(id, function(json){
                            this.availableData.erase( ad )
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availableDepartmentList = availableDepartmentList;
                this.availableItemsContentNode.empty();
                this.setAvailableItems();

                this.app.notice(  MWF.CMSCM.LP.setAvailableDepartmentSuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },
    changeAvailableCompanys: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        var options = {
            "type": "company",
            "title": "设置应用可用公司",
            "names": this.availableCompanyList || [],
            "onComplete": function(items) {
                var availableCompanyList = [];

                items.each(function (item) {
                    availableCompanyList.push(item.data.name);
                }.bind(this));

                availableCompanyList.each(function (item) {
                    if (!this.availableCompanyList.contains(item)) {
                        var permissionData = {
                            "objectType": "APPINFO",
                            "objectId": this.data.id,
                            "usedObjectType": "COMPANY",
                            "usedObjectCode": item,
                            "usedObjectName": item
                        }
                        this.app.restActions.savePermission(permissionData, function (json) {
                            permissionData.id = json.data.id;
                            this.availableData.push( permissionData );
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availableCompanyList.each(function (item) {
                    if (!availableCompanyList.contains(item)) {
                        var ad = null;
                        var id = ""
                        this.availableData.each(function (data) {
                            if (data.usedObjectName == item) {
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.app.restActions.removePermission(id, function (json) {
                            this.availableData.erase(ad)
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.availableCompanyList = availableCompanyList;
                this.availableItemsContentNode.empty();
                this.setAvailableItems();


                this.app.notice(  MWF.CMSCM.LP.setAvailableCompanySuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    },

    createEditBaseNode: function(){
        this.editBaseNode = new Element("button", {
            "styles": this.app.css.editBaseNode,
            "text": this.app.lp.edit,
            "events": {"click": this.editBaseInfor.bind(this)}
        }).inject(this.baseActionNode);
    },
    createCancelBaseNode: function(){
        this.cancelBaseNode = new Element("button", {
            "styles": this.app.css.cancelBaseNode,
            "text": this.app.lp.cancel,
            "events": {"click": this.cancelBaseInfor.bind(this)}
        }).inject(this.baseActionNode);
    },
    createSaveBaseNode: function(){
        this.saveBaseNode = new Element("button", {
            "styles": this.app.css.saveBaseNode,
            "text": this.app.lp.save,
            "events": {"click": this.saveBaseInfor.bind(this)}
        }).inject(this.baseActionNode);
    },
    editBaseInfor: function(){
        this.baseActionNode.empty();
        this.editBaseNode = null;
        this.createCancelBaseNode();
        this.createSaveBaseNode();

        this.editMode();
    },
    editMode: function(){
        this.nameInput.editMode();
        this.aliasInput.editMode();
        this.descriptionInput.editMode();
        this.sortInput.editMode();
        //this.typeInput.editMode();
        this.isEdit = true;
    },
    readMode: function(){
        this.nameInput.readMode();
        this.aliasInput.readMode();
        this.descriptionInput.readMode();
        this.sortInput.readMode();
        //this.typeInput.readMode();
        this.isEdit = false;
    },
    cancelBaseInfor: function(){
        if (this.data.name){
            this.baseActionNode.empty();
            this.cancelBaseNode = null;
            this.saveBaseNode = null;
            this.createEditBaseNode();

            this.readMode();
        }else{
            this.destroy();
        }
    },
    saveBaseInfor: function(){
        if (!this.nameInput.input.get("value")){
            this.app.notice(this.app.lp.application.inputApplicationName, "error", this.node);
            return false;
        }
        this.node.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });
        this.save(function(){
            this.baseActionNode.empty();
            this.cancelBaseNode = null;
            this.saveBaseNode = null;
            this.createEditBaseNode();

            this.readMode();

            this.node.unmask();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.app.notice("request json error: "+errorText, "error");
            this.node.unmask();
        }.bind(this));
    },
    save: function(callback, cancel){

        this.data.name = this.nameInput.input.get("value");
        this.data.appName = this.data.name;
        this.data.alias = this.aliasInput.input.get("value");
        this.data.appAlias = this.data.alias;
        this.data.description = this.descriptionInput.input.get("value");
        this.data.appInfoSeq = this.sortInput.input.get("value");
        //this.data.applicationCategory = this.typeInput.input.get("value");

        this.app.restActions.saveColumn(this.data, function(json){
            this.propertyTitleBar.set("text", this.data.name);
            this.data.id = json.data.id;
            this.nameInput.save();
            this.aliasInput.save();
            this.descriptionInput.save();
            this.sortInput.save();
            //this.typeInput.save();

            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    }
});

MWF.xApplication.cms.ColumnManager.Input = new Class({
    Implements: [Events],
    initialize: function(node, value, style){
        this.node = $(node);
        this.value = value || "";
        this.style = style;
        this.load();
    },
    load: function(){
        this.content = new Element("div", {
            "styles": this.style.content,
            "text": this.value
        }).inject(this.node);
    },
    editMode: function(){
        this.content.empty();
        this.input = new Element("input",{
            "styles": this.style.input,
            "value": this.value
        }).inject(this.content);

        this.input.addEvents({
            "focus": function(){
                this.input.setStyles(this.style.input_focus);
            }.bind(this),
            "blur": function(){
                this.input.setStyles(this.style.input);
            }.bind(this)
        });

    },
    readMode: function(){
        this.content.empty();
        this.input = null;
        this.content.set("text", this.value);
    },
    save: function(){
        if (this.input) this.value = this.input.get("value");
        return this.value;
    }
});