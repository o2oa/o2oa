MWF.xDesktop.requireApp("cms.ColumnManager", "package", null, false);
//MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.O2Identity", null,false);
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
        "height": "700",
        "title": MWF.xApplication.cms.ColumnManager.LP.title,
        "currentCategoryId" : ""
    },
    onQueryLoad: function(){
        if(this.options.column)this.options.column.icon = this.options.column.appIcon;
        if(!this.options.application) this.options.application = this.options.column;
        this.lp = MWF.xApplication.cms.ColumnManager.LP;
        this.currentContentNode = null;
    },
    loadApplication: function(callback){

        this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.ColumnManager.Actions.RestActions();

        if (this.status && !this.options.currentCategoryId ){
            if( this.status.categoryId ){
                this.options.currentCategoryId =  this.status.categoryId;
            }
        }
        this.getColumn(function(){
            this.setTitle( this.options.column.appName +this.lp.setting );
            this.loadController(function(){
                if( !this.isAdmin ){
                    this.notice(  MWF.CMSCM.LP.noAdministratorAccess  , "error");
                    this.close();
                }else{
                    this.createNode();
                    this.loadApplicationContent();

                    if (window.clipboardData){
                        this.addKeyboardEvents();
                    }else{
                        this.keyCopyItemsFun = this.keyCopyItems.bind(this);
                        this.keyPasteItemsFun = this.keyPasteItems.bind(this);
                        document.addEventListener('copy',  this.keyCopyItemsFun);
                        document.addEventListener('paste', this.keyPasteItemsFun);

                        this.addEvent("queryClose", function(){
                            if (this.keyCopyItemsFun) document.removeEventListener('copy',  this.keyCopyItemsFun);
                            if (this.keyPasteItemsFun) document.removeEventListener('paste', this.keyPasteItemsFun);
                        }.bind(this));
                    }

                    if (callback) callback();
                }
            }.bind(this))
        }.bind(this), function(){
            this.close();
        }.bind(this));
    },
    addKeyboardEvents: function(){
        this.addEvent("copy", function(){
            this.keyCopyItems();
        }.bind(this));
        this.addEvent("paste", function(){
            this.keyPasteItems();
        }.bind(this));
    },
    keyCopyItems: function(e){
        if (layout.desktop.currentApp && layout.desktop.currentApp.appId===this.appId){
            //if (this.categoryConfigurator && this.categoryConfigurator.isActive ){
            //    this.categoryConfigurator.keyCopy(e);
            //}
            if (this.formConfigurator) {
                this.formConfigurator.keyCopy(e);
                if (e) e.preventDefault();
            }
            if (this.viewConfigurator){
                this.viewConfigurator.keyCopy(e);
                if (e) e.preventDefault();
            }
            if (this.dataConfigurator){
                this.dataConfigurator.keyCopy(e);
                if (e) e.preventDefault();
            }
            if (this.scriptConfigurator){
                this.scriptConfigurator.keyCopy(e);
                if (e) e.preventDefault();
            }
        }
    },
    keyPasteItems: function(e){
        if (layout.desktop.currentApp && layout.desktop.currentApp.appId===this.appId) {
            //if (this.categoryConfigurator && this.categoryConfigurator.isActive ){
            //    this.categoryConfigurator.keyPaste(e);
            //}
            if (this.formConfigurator) {
                this.formConfigurator.keyPaste(e);
            }
            if (this.viewConfigurator){
                this.viewConfigurator.keyPaste(e);
            }
            if (this.dataConfigurator){
                this.dataConfigurator.keyPaste(e);
            }
            if (this.scriptConfigurator){
                this.scriptConfigurator.keyPaste(e);
            }
        }
    },

    loadController: function(callback){
        //this.controllers = [];
        //this.restActions.listColumnController(this.options.column.id, function( json ){
        //    json.data = json.data || [];
        //    json.data.each(function(item){
        //        this.controllers.push(item.adminUid)
        //    }.bind(this));
        //    this.isAdmin =  MWF.AC.isCMSManager() || this.controllers.contains(layout.desktop.session.user.distinguishedName);
        //    if(callback)callback(this.isAdmin);
        //}.bind(this));

        this.restActions.isAppInfoManager(this.options.column.id, function( json ){
            this.isAdmin =  MWF.AC.isCMSManager() || json.data.value;
            if(callback)callback(this.isAdmin);
        }.bind(this));
    },
    getColumn: function(success, failure){
        if( this.options.column ){
            if (success) success();
            return;
        }
        var columnId = this.options.columnId;
        if( !columnId )columnId = this.status && this.status.column;
        if( !columnId ){
            if (failure) failure();
            return;
        }
        this.restActions.getColumn(columnId, function(json){
            if (json.data){
                this.options.column = json.data;
                this.options.application = json.data;
                if (success) success();
            }else{
                if (failure) failure();
            }
        }.bind(this), function(){if (failure) failure();}.bind(this), false)
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
            "text" : this.options.column.appName + this.lp.setting,
            "title" : this.options.column.appName + this.lp.setting
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
            if (this.categoryConfigurator) this.categoryConfigurator.isActive = false;
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
        if (this.fileConfiguratorContent){
            if (this.queryViewConfigurator) delete this.fileConfiguratorContent;
            this.fileConfiguratorContent.destroy();
            this.fileConfiguratorContent = null;
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

    cagetoryConfig: function( noRefresh ){
        this.clearContent();
        if( this.categoryConfiguratorContent ) {
            this.categoryConfiguratorContent.setStyle("display","");
            if( this.menu.itemObject["categoryConfig"] ){
                this.menu.expend( this.menu.itemObject["categoryConfig"] );
            }
            this.categoryConfigurator.isActive = true;
            if(!noRefresh)this.categoryConfigurator.refresh();
        }else{
            this.categoryConfiguratorContent = new Element("div", {
                "styles": this.css.rightContentNode
            }).inject(this.node);
            this.loadCategoryConfig();
        }
    },
    loadCategoryConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "CategoryExplorer", function(){
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            var navi = this.menu.itemObject.categoryConfig;
            var subNode = navi.retrieve( "subNode" );
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.categoryConfigurator = new MWF.xApplication.cms.ColumnManager.CategoryExplorer(this.categoryConfiguratorContent, subNode, this.restActions, {
                "currentCategoryId" : this.options.currentCategoryId,
                "onPostLoadCategoryList" : function(){
                }.bind(this),
                "onPostClickSub" : function(){
                    this.menu.cancelCurrentNavi();
                }.bind(this)
            });
            this.categoryConfigurator.isActive = true;
            this.categoryConfigurator.app = this;
            //this.categoryConfigurator.categoryScrollWrapNode = this.menu.naviNode;
            //this.categoryConfigurator.categoryScrollContentNode = this.menu.areaNode;
            this.categoryConfigurator.load();
            this.options.currentCategoryId = "";
            //}.bind(this));
        }.bind(this));
    },
    createCategory : function(){
        this.cagetoryConfig( true );
        if( this.categoryConfigurator ){
            this.categoryConfigurator.categoryList.newCategory();
        }
    },
    setCategory : function( categoryId ){
        this.cagetoryConfig( true );
        if( this.categoryConfigurator ){
            this.categoryConfigurator.categoryList.setCurrentCategoryById( categoryId );
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
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.formConfigurator = new MWF.xApplication.cms.ColumnManager.FormExplorer(this.formConfiguratorContent, this.restActions, { "title" : "表单配置" });
            this.formConfigurator.app = this;
            this.formConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createForm : function(){
        if( this.formConfigurator ){
            this.formConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "FormExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.formConfigurator = new MWF.xApplication.cms.ColumnManager.FormExplorer(this.formConfiguratorContent, this.restActions);
                this.formConfigurator.app = this;
                this.formConfigurator._createElement();
                //}.bind(this));
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
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.dataConfigurator = new MWF.xApplication.cms.ColumnManager.DictionaryExplorer(this.dataConfiguratorContent, this.restActions, { "title" : "数据配置" });
            this.dataConfigurator.app = this;
            this.dataConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createDataConfig : function(){
        if( this.dataConfigurator ){
            this.dataConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "DictionaryExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.dataConfigurator = new MWF.xApplication.cms.ColumnManager.DictionaryExplorer(this.dataConfiguratorContent, this.restActions);
                this.dataConfigurator.app = this;
                this.dataConfigurator._createElement();
                //}.bind(this));
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
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.scriptConfigurator = new MWF.xApplication.cms.ColumnManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions, { "title" : "脚本配置" });
            this.scriptConfigurator.app = this;
            this.scriptConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createScriptConfig : function(){
        if( this.scriptConfigurator ){
            this.scriptConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "ScriptExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.scriptConfigurator = new MWF.xApplication.cms.ColumnManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions);
                this.scriptConfigurator.app = this;
                this.scriptConfigurator._createElement();
                //}.bind(this));
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
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.viewConfigurator = new MWF.xApplication.cms.ColumnManager.ViewExplorer(this.viewConfiguratorContent, this.restActions, { "title" : "列表配置" });
            this.viewConfigurator.app = this;
            this.viewConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createView : function(){
        if( this.viewConfigurator ){
            this.viewConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "ViewExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.viewConfigurator = new MWF.xApplication.cms.ColumnManager.ViewExplorer(this.viewConfiguratorContent, this.restActions);
                this.viewConfigurator.app = this;
                this.viewConfigurator._createElement();
                //}.bind(this));
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
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.queryViewConfigurator = new MWF.xApplication.cms.ColumnManager.QueryViewExplorer(this.queryViewConfiguratorContent, this.restActions, { "title" : "数据视图配置" });
            this.queryViewConfigurator.app = this;
            this.queryViewConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createQueryView : function(){
        if( this.queryViewConfigurator ){
            this.queryViewConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "QueryViewExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.queryViewConfigurator = new MWF.xApplication.cms.ColumnManager.QueryViewExplorer(this.queryViewConfiguratorContent, this.restActions);
                this.queryViewConfigurator.app = this;
                this.queryViewConfigurator._createElement();
                // }.bind(this));
            }.bind(this));
        }
    },

    fileConfig: function(){
        this.clearContent();
        this.fileConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadFileConfig();
    },
    loadFileConfig: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "FileExplorer", function(){
            //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
            //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
            this.fileConfigurator = new MWF.xApplication.cms.ColumnManager.FileExplorer(this.fileConfiguratorContent, this.restActions, { "title" : "附件配置" });
            this.fileConfigurator.app = this;
            this.fileConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    createFileConfig : function(){
        if( this.fileConfigurator ){
            this.fileConfigurator._createElement();
        }else{
            MWF.xDesktop.requireApp("cms.ColumnManager", "QueryViewExplorer", function(){
                //MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
                this.fileConfigurator = new MWF.xApplication.cms.ColumnManager.FileViewExplorer(this.fileConfiguratorContent, this.restActions);
                this.fileConfigurator.app = this;
                this.fileConfigurator._createElement();
                // }.bind(this));
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
        var categoryId = "";
        if (this.menu.currentNavi){
            var naviData = this.menu.currentNavi.retrieve( "naviData" );
            id = naviData.id;
        }
        if( id == "categoryConfig" ){
            if( this.categoryConfigurator && this.categoryConfigurator.categoryList ){
                var list = this.categoryConfigurator.categoryList;
                if( list.currentCategory && list.currentCategory.data ){
                    categoryId = list.currentCategory.data.id;
                }
            }
        }
        return {
            "navi": id,
            "column": this.options.column.id,
            "categoryId" : categoryId
        };
    }

//onResize: function() {
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

                if( navi.expand ){
                    var expandNode =  new Element("div", {
                        "styles": this.app.css.startMenuExpandNode
                    });
                    expandNode.inject(naviNode);
                    naviNode.store("expandNode", expandNode );
                }else{
                    new Element("div", {
                        "styles": this.app.css.startMenuEmptyNode
                    }).inject(naviNode);
                }

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
                            if( this.obj.currentNavi == this.naviNode && !this.naviData.unselected ){
                                this.createNode.setStyles( this.obj.app.css.startMenuCreateNode_current_over )
                            }else{
                                this.createNode.setStyles( this.obj.app.css.startMenuCreateNode_over )
                            }
                        }.bind({ obj : this, createNode : createNode, naviNode : naviNode , naviData : navi}),
                        "mouseout" : function(ev){
                            if( this.obj.currentNavi == this.naviNode && !this.naviData.unselected ){
                                this.createNode.setStyles( this.obj.app.css.startMenuCreateNode_current )
                            }else{
                                this.createNode.setStyles( this.obj.app.css.startMenuCreateNode )
                            }
                        }.bind({ obj : this, createNode : createNode, naviNode : naviNode, naviData : navi })
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
    cancelCurrentNavi: function(){
        if( this.currentNavi ){
            this.currentNavi.setStyles(this.app.css.startMenuNaviNode);
            var iconNode = this.currentNavi.retrieve("iconNode");
            var navi = this.currentNavi.retrieve("naviData");
            iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.icon+")");
        }
    },
    doAction: function( naviNode ){
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
            if( navi.create ){
                var createNode = this.currentNavi.retrieve("createNode");
                createNode.setStyles(this.app.css.startMenuCreateNode);
            }
        }

        if( naviNode ){
            var navi = naviNode.retrieve("naviData");
            var action = navi.action;

            if( !navi.unselected ){
                naviNode.setStyles(this.app.css.startMenuNaviNode_current);
                var iconNode = naviNode.retrieve("iconNode");
                iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.selectedIcon+")");
            }

            if( navi.expand ){
                if( !navi.unselected ){
                    var expandNode = naviNode.retrieve("expandNode");
                    expandNode.setStyles(this.app.css.startMenuCollapseNode_current);
                }else{
                    var expandNode = naviNode.retrieve("expandNode");
                    expandNode.setStyles(this.app.css.startMenuCollapseNode );
                }
                var subNode = naviNode.retrieve("subNode");
                subNode.setStyle( "display" , "" );
                naviNode.store("isExpand",true);
            }

            if( navi.create ){
                var createNode = naviNode.retrieve("createNode");
                if( !navi.unselected ){
                    createNode.setStyles(this.app.css.startMenuCreateNode_current);
                }else{
                    createNode.setStyles(this.app.css.startMenuCreateNode);
                }
            }

            if (this.app[action]) this.app[action].apply( this.app );
        }

        this.currentNavi = naviNode;

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

        if( typeOf(this.data.config) === "string" ){
            this.config = JSON.parse( this.data.config || {} );
        }else{
            this.config = Object.clone(this.data.config || {});
        }

        this.controllerData = [];
        this.controllerList = [];
    },
    load: function(){
        this.propertyTitleBar = new Element("div.propertyTitleBar", {
            "styles": this.app.css.propertyTitleBar,
            "text": "栏目属性"  //this.data.name || this.data.appName
        }).inject(this.node);

        this.contentNode =  new Element("div.propertyContentNode", {
            "styles": this.app.css.propertyContentNode
        }).inject(this.node);
        this.contentAreaNode =  new Element("div.propertyContentAreaNode", {
            "styles": this.app.css.propertyContentAreaNode
        }).inject(this.contentNode);

        this.setContentHeight();
        this.setContentHeightFun = this.setContentHeight.bind(this);
        this.app.addEvent("resize", this.setContentHeightFun);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentNode, {"indent": false});
        }.bind(this));

        this.baseActionAreaNode = new Element("div.baseActionAreaNode", {
            "styles": this.app.css.baseActionAreaNode
        }).inject(this.contentAreaNode);

        this.baseActionNode = new Element("div.propertyInforActionNode", {
            "styles": this.app.css.propertyInforActionNode
        }).inject(this.baseActionAreaNode);
        this.baseTextNode = new Element("div.baseTextNode", {
            "styles": this.app.css.baseTextNode,
            "text": this.app.lp.application.property
        }).inject(this.baseActionAreaNode);

        this.createEditBaseNode();

        this.createPropertyContentNode();

        this.createIconContentNode();

        this.viewerContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.ColumnViewerSetting", null, false);
        this.viewerSetting = new MWF.xApplication.cms.ColumnManager.ColumnViewerSetting( this.app,
            this.app.lp.application.viewerSetting, this.viewerContainer, {
                objectId : this.data.id
            }
        );
        this.viewerSetting.dataParent = this;
        this.viewerSetting.load();

        this.publisherContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.ColumnPublisherSetting", null, false);
        this.publisherSetting = new MWF.xApplication.cms.ColumnManager.ColumnPublisherSetting( this.app,
            this.app.lp.application.publisherSetting, this.publisherContainer, {
                objectId : this.data.id
            }
        );
        this.publisherSetting.dataParent = this;
        this.publisherSetting.load();

        this.managerContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.ColumnManagerSetting", null, false);
        this.viewerSetting = new MWF.xApplication.cms.ColumnManager.ColumnManagerSetting( this.app,
            this.app.lp.application.managerSetting, this.managerContainer, {
                objectId : this.data.id
            }
        );
        this.viewerSetting.dataParent = this;
        this.viewerSetting.load();
    },
    setContentHeight: function(){
        var size = this.app.content.getSize();
        var titleSize = this.propertyTitleBar.getSize();
        var y = size.y-titleSize.y;
        this.contentNode.setStyle("height", ""+y+"px");
    },

    createIconContentNode: function(){
        this.iconContentTitleNode = new Element("div.iconContentTitleNode", {
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
            "styles": this.app.css.selectButtonStyle,
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
                            this.app.restActions.getColumn(this.data.id, function(json){
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
        html += "<tr><td class='formTitle'>"+this.app.lp.application.id +"</td><td id='formApplicationId' class='formValue'>"+this.data.id+"</td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.sign+"</td><td id='formApplicationAlias'></td></tr>"; //"+(this.data.alias||this.data.appAlias||'')+"
        html += "<tr><td class='formTitle'>"+this.app.lp.application.appType+"</td><td id='formApplicationAppType'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.documentType+"</td><td id='formApplicationType' class='formValue'>"+(this.data.documentType || "信息" )+"</td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.description+"</td><td id='formApplicationDescription'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.sort+"</td><td id='formApplicationSort'></td></tr>";
        var flag = typeOf(this.config.ignoreTitle) === "boolean" ? this.config.ignoreTitle : false;
        html += "<tr><td class='formTitle'>"+this.app.lp.application.ignoreTitle+"</td><td id='formApplicationIgnoreTitle' class='formValue'>"+(flag ? "新建界面不填写标题" : "新建界面需要填写标题" )+"</td></tr>";
        var flag = typeOf(this.config.latest) === "boolean" ? this.config.latest : true;
        html += "<tr><td class='formTitle'>"+this.app.lp.application.latest+"</td><td id='formApplicationLatest' class='formValue'>"+(flag ? "新建界面检查草稿" : "新建界面不检查草稿" )+"</td></tr>";
        var flag = typeOf(this.data.showAllDocuments) === "boolean" ? this.data.showAllDocuments : true;
        html += "<tr><td class='formTitle'>"+this.app.lp.application.showAllDocumentViews+"</td><td id='showAllDocumentViews' class='formValue'>"+(flag ? "显示所有文档视图" : "隐藏所有文档视图" )+"</td></tr>";
        // html += "<tr><td class='formTitle'>"+this.app.lp.application.type+"</td><td id='formApplicationType'></td></tr>";
        //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);
        this.propertyContentNode.getElements("td.formValue").setStyles(this.app.css.propertyBaseContentTdValue);

        this.nameInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name || this.data.appName, this.app.css.formInput);

        this.aliasInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationAlias"), this.data.alias || this.data.appAlias, this.app.css.formInput);

        this.appTypeInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationAppType"), this.data.appType || "", this.app.css.formInput);

        this.typeSelect = new MDomItem( this.propertyContentNode.getElement("#formApplicationType"), {
            type : "select",
            value : this.data.documentType || "信息",
            selectValue : [ "信息", "数据" ]
        });

        this.descriptionInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        this.sortInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationSort"), this.data.appInfoSeq, this.app.css.formInput);

        debugger;
        this.ignoreTitleSelect = new MDomItem( this.propertyContentNode.getElement("#formApplicationIgnoreTitle"), {
            type : "select",
            defaultValue : "false",
            value : ( typeOf(this.config.ignoreTitle) === "boolean" ? this.config.ignoreTitle : false ).toString(),
            selectValue : [ "true", "false" ],
            selectText : [ "新建界面不填写标题", "新建界面需要填写标题" ]
        });


        this.latestSelect = new MDomItem( this.propertyContentNode.getElement("#formApplicationLatest"), {
            type : "select",
            defaultValue : "true",
            value : ( typeOf(this.config.latest) === "boolean" ? this.config.latest : true ).toString(),
            selectValue : [ "true", "false" ],
            selectText : [ "新建界面检查草稿", "新建界面不检查草稿" ]
        });

        this.allDocumentViewSelect = new MDomItem( this.propertyContentNode.getElement("#showAllDocumentViews"), {
            type : "select",
            defaultValue : "true",
            value : ( typeOf(this.data.showAllDocuments) === "boolean" ? this.data.showAllDocuments : true ).toString(),
            selectValue : [ "true", "false" ],
            selectText : [ "显示所有文档视图", "隐藏所有文档视图" ]
        });

        //this.typeInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.applicationCategory, this.app.css.formInput);
    },

    createEditBaseNode: function(){
        this.editBaseNode = new Element("button.editBaseNode", {
            "styles": this.app.css.editBaseNode,
            "text": this.app.lp.edit,
            "events": {"click": this.editBaseInfor.bind(this)}
        }).inject(this.baseActionNode);
    },
    createCancelBaseNode: function(){
        this.cancelBaseNode = new Element("button.cancelBaseNode", {
            "styles": this.app.css.cancelBaseNode,
            "text": this.app.lp.cancel,
            "events": {"click": this.cancelBaseInfor.bind(this)}
        }).inject(this.baseActionNode);
    },
    createSaveBaseNode: function(){
        this.saveBaseNode = new Element("button.saveBaseNode", {
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
        this.appTypeInput.editMode();
        this.descriptionInput.editMode();
        this.sortInput.editMode();

        this.typeSelect.editMode();
        this.latestSelect.editMode();
        this.ignoreTitleSelect.editMode();
        this.allDocumentViewSelect.editMode();
        //this.typeInput.editMode();
        this.isEdit = true;
    },
    readMode: function(){
        this.nameInput.readMode();
        this.aliasInput.readMode();
        this.appTypeInput.readMode();
        this.descriptionInput.readMode();
        this.sortInput.readMode();
        this.typeSelect.readMode();
        this.latestSelect.readMode();
        this.ignoreTitleSelect.readMode();
        this.allDocumentViewSelect.readMode();
        //this.typeInput.readMode();
        this.isEdit = false;
    },
    cancelBaseInfor: function(){
        if (this.data.name || this.data.appName || this.data.id ){
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
        //this.node.mask({
        //    "style": {
        //        "opacity": 0.7,
        //        "background-color": "#999"
        //    }
        //});
        this.save(function(){
            this.baseActionNode.empty();
            this.cancelBaseNode = null;
            this.saveBaseNode = null;
            this.createEditBaseNode();

            this.readMode();

            //this.node.unmask();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.app.notice("request json error: "+errorText, "error");
            //this.node.unmask();
        }.bind(this));
    },
    save: function(callback, cancel){

        this.data.name = this.nameInput.input.get("value");
        this.data.appName = this.data.name;
        this.data.alias = this.aliasInput.input.get("value");
        this.data.appAlias = this.data.alias;
        this.data.appType = this.appTypeInput.input.get("value");
        this.data.description = this.descriptionInput.input.get("value");
        this.data.appInfoSeq = this.sortInput.input.get("value");
        this.data.documentType = this.typeSelect.getValue();
        this.data.showAllDocuments = this.allDocumentViewSelect.getValue() !== "false";

        this.config.ignoreTitle = this.ignoreTitleSelect.getValue() !== "false";
        this.config.latest = this.latestSelect.getValue() !== "false";

        this.data.config = JSON.stringify( this.config );

        //this.data.applicationCategory = this.appTypeInput.input.get("value");

        this.app.restActions.saveColumn(this.data, function(json){
            this.propertyTitleBar.set("text", this.data.name);
            this.data.id = json.data.id;
            this.nameInput.save();
            this.aliasInput.save();
            this.appTypeInput.save();
            this.descriptionInput.save();
            this.sortInput.save();
            this.typeSelect.save();
            this.latestSelect.save();
            this.ignoreTitleSelect.save();
            this.allDocumentViewSelect.save();
            //this.typeInput.save();
            this.app.notice( this.app.lp.application.saveSuccess, "success");

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

//MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
//MWF.xApplication.cms.ColumnManager.TypeTooltip = new Class({
//    Extends: MTooltips,
//    _loadCustom : function( callback ){
//        if(callback)callback();
//    },
//    _getHtml : function(){
//        var data = this.data;
//        var titleStyle = "font-size:12px;color:#333";
//        var valueStyle = "font-size:12px;color:#666;padding-right:20px";
//
//            var html =
//                "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
//                "<tr><td style='"+valueStyle+";' width='70'>"+"栏目类型设置为“数据存储”时，将不在信息中心中展现。"+":</td>" +
//                " </tr>" +
//                "</table>";
//        return html;
//    }
//});