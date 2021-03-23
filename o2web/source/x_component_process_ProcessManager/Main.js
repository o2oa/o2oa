MWF.xDesktop.requireApp("process.ProcessManager", "package", null, false);
//MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("process.ProcessManager", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
//MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xApplication.process.ProcessManager.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
        "application": null,
		"style": "default",
		"name": "process.ProcessManager",
		"icon": "icon.png",
		"width": "1100",
		"height": "700",
		"title": MWF.xApplication.process.ProcessManager.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.ProcessManager.LP;
		this.currentContentNode = null;
        this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
        //this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
	},
    loadApplication: function(callback){
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
        this.getApplication(function(){
            this.setTitle(this.lp.title + "-"+this.options.application.name);
            this.createNode();
            this.loadApplicationContent();
            if (callback) callback();


            //var clipboardEvent = new ClipboardEvent("copy", {dataType: "text/plain", data:""});

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
            if (this.formConfigurator){
                this.formConfigurator.keyCopy(e);
                if (e) e.preventDefault();
            }
            if (this.processConfigurator){
                this.processConfigurator.keyCopy(e);
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
            if (this.formConfigurator) {
                this.formConfigurator.keyPaste(e);
            }
            if (this.processConfigurator) {
                this.processConfigurator.keyPaste(e);
            }
            if (this.dataConfigurator) {
                this.dataConfigurator.keyPaste(e);
            }
            if (this.scriptConfigurator) {
                this.scriptConfigurator.keyPaste(e);
            }
        }
    },

    getApplication: function(success, failure){
        if (!this.options.application){
            if (this.status) {
                if (this.status.application){
                    this.restActions.getApplication(this.status.application, function(json){
                        if (json.data){
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
        this.startMenuNode = new Element("div", {
            "styles": this.css.startMenuNode
        }).inject(this.node);

        this.menu = new MWF.xApplication.process.ProcessManager.Menu(this, this.startMenuNode, {
            "onPostLoad": function(){
                if (this.status){
                    if (this.status.navi!=null){
                        this.menu.doAction(this.menu.startNavis[this.status.navi]);
                    }else{
                        this.menu.doAction(this.menu.startNavis[0]);
                    }
                }else{
                    this.menu.doAction(this.menu.startNavis[0]);
                }
            }.bind(this)
        });
        this.addEvent("resize", function(){
            if (this.menu) this.menu.onResize();
        }.bind(this));
    },
    clearContent: function(){
        if (this.processConfiguratorContent){
            if (this.processConfigurator){
                if (this.processConfigurator.destroy) this.processConfigurator.destroy();
                delete this.processConfigurator;
            }
            this.processConfiguratorContent.destroy();
            this.processConfiguratorContent = null;
        }
        if (this.formConfiguratorContent){
            if (this.formConfigurator){
                if (this.formConfigurator.destroy) this.formConfigurator.destroy();
                delete this.formConfigurator;
            }
            this.formConfiguratorContent.destroy();
            this.formConfiguratorContent = null;
        }
        if (this.viewConfiguratorContent){
            if (this.viewConfigurator){
                if (this.viewConfigurator.destroy) this.viewConfigurator.destroy();
                delete this.viewConfigurator;
            }
            this.viewConfiguratorContent.destroy();
            this.viewConfiguratorContent = null;
        }
        if (this.statConfiguratorContent){
            if (this.statConfigurator){
                if (this.statConfigurator.destroy) this.statConfigurator.destroy();
                delete this.statConfigurator;
            }
            this.statConfiguratorContent.destroy();
            this.statConfiguratorContent = null;
        }
        if (this.propertyConfiguratorContent){
            if (this.property){
                if (this.property.destroy) this.property.destroy();
                delete this.property;
            }
            this.propertyConfiguratorContent.destroy();
            this.propertyConfiguratorContent = null;
        }
        if (this.dataConfiguratorContent){
            if (this.dataConfigurator){
                if (this.dataConfigurator.destroy) this.dataConfigurator.destroy();
                delete this.dataConfigurator;
            }
            this.dataConfiguratorContent.destroy();
            this.dataConfiguratorContent = null;
        }
        if (this.scriptConfiguratorContent){
            if (this.scriptConfigurator){
                if (this.scriptConfigurator.destroy) this.scriptConfigurator.destroy();
                delete this.scriptConfigurator;
            }
            this.scriptConfiguratorContent.destroy();
            this.scriptConfiguratorContent = null;
        }
        if (this.fileConfiguratorContent){
            if (this.fileConfigurator){
                if (this.fileConfigurator.destroy) this.fileConfigurator.destroy();
                delete this.fileConfigurator;
            }
            this.fileConfiguratorContent.destroy();
            this.fileConfiguratorContent = null;
        }
        if (this.projectionConfiguratorContent){
            if (this.projectionConfigurator){
                if (this.projectionConfigurator.destroy) this.projectionConfigurator.destroy();
                delete this.projectionConfigurator;
            }
            this.projectionConfiguratorContent.destroy();
            this.projectionConfiguratorContent = null;
        }
    },

    applicationProperty: function(){
        this.clearContent();
        this.propertyConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.property = new MWF.xApplication.process.ProcessManager.ApplicationProperty(this, this.propertyConfiguratorContent);
        this.property.load();
    },
    projectionConfig: function(){
        this.clearContent();
        this.projectionConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadProjectionConfig();
    },
    loadProjectionConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "ProjectionExplorer", function(){
            this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
            this.projectionConfigurator = new MWF.xApplication.process.ProcessManager.ProjectionExplorer(this.projectionConfiguratorContent, this.restActions);
            this.projectionConfigurator.app = this;
            this.projectionConfigurator.load();
        }.bind(this));
    },


    processConfig: function(){
        this.clearContent();
        this.processConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadProcessConfig();
    },
    loadProcessConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "ProcessExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.processConfigurator = new MWF.xApplication.process.ProcessManager.ProcessExplorer(this.processConfiguratorContent, this.restActions);
                this.processConfigurator.app = this;
                this.processConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    formConfig: function(){
        this.clearContent();
        this.formConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadFormConfig();
    },
    loadFormConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "FormExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.formConfigurator = new MWF.xApplication.process.ProcessManager.FormExplorer(this.formConfiguratorContent, this.restActions);
                this.formConfigurator.app = this;
                this.formConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    viewConfig: function(){
        this.clearContent();
        this.viewConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadViewConfig();
    },
    loadViewConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "ViewExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.viewConfigurator = new MWF.xApplication.process.ProcessManager.ViewExplorer(this.viewConfiguratorContent, this.restActions);
                this.viewConfigurator.app = this;
                this.viewConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    statConfig: function(){
        this.clearContent();
        this.statConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadStatConfig();
    },
    loadStatConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "StatExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.statConfigurator = new MWF.xApplication.process.ProcessManager.StatExplorer(this.statConfiguratorContent, this.restActions);
                this.statConfigurator.app = this;
                this.statConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    dataConfig: function(){
        this.clearContent();
        this.dataConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadDataConfig();
    },
    loadDataConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.dataConfigurator = new MWF.xApplication.process.ProcessManager.DictionaryExplorer(this.dataConfiguratorContent, this.restActions);
                this.dataConfigurator.app = this;
                this.dataConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    scriptConfig: function(){
        this.clearContent();
        this.scriptConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadScriptConfig();
    },
    loadScriptConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "ScriptExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.scriptConfigurator = new MWF.xApplication.process.ProcessManager.ScriptExplorer(this.scriptConfiguratorContent, this.restActions);
                this.scriptConfigurator.app = this;
                this.scriptConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    fileConfig: function(){
        this.clearContent();
        this.fileConfiguratorContent = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadFileConfig();
    },
    loadFileConfig: function(){
        MWF.xDesktop.requireApp("process.ProcessManager", "FileExplorer", function(){
            //MWF.xDesktop.requireApp("process.ProcessManager", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
            this.restActions = MWF.Actions.get("x_processplatform_assemble_designer");
            this.fileConfigurator = new MWF.xApplication.process.ProcessManager.FileExplorer(this.fileConfiguratorContent, this.restActions);
            this.fileConfigurator.app = this;
            this.fileConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },



    getCategoryCount: function(){
        var size = this.processConfiguratorContent.getSize();
        categoryCount = parseInt(size.x/182)+5;
        return categoryCount;
    },
    getProcessCount: function(){
        if (this.processConfigurator){
            var size = this.processConfigurator.processNode.getSize();
            processCount = (parseInt(size.x/401)*parseInt(size.y/101))+10;
            return processCount;
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
        var idx = null;
        if (this.menu.currentNavi){
            idx = this.menu.startNavis.indexOf(this.menu.currentNavi);
        }
        return {"navi": idx, "application": this.options.application.id};
    }

//	onResize: function(){
//		if (this.menu) this.menu.onResize();
//	}
});

MWF.xApplication.process.ProcessManager.Menu = new Class({
    Implements: [Options, Events],

    initialize: function(app, node, options){
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.currentNavi = null;
        this.status = "start";
        this.startNavis = [];
        this.load();
    },
    load: function(){
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

                var textNode =  new Element("div", {
                    "styles": this.app.css.startMenuTextNode,
                    "text": navi.title
                });
                textNode.inject(naviNode);
                naviNode.inject(this.node);

                this.startNavis.push(naviNode);

                this.setStartNaviEvent(naviNode, navi);

                this.setNodeCenter(this.node);
            }.bind(this));
            this.setStartMenuWidth();

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
    doAction: function(naviNode){
        var navi = naviNode.retrieve("naviData");
        var action = navi.action;

        if (this.currentNavi) this.currentNavi.setStyles(this.app.css.startMenuNaviNode);

        naviNode.setStyles(this.app.css.startMenuNaviNode_current);
        this.currentNavi = naviNode;

        if (this.app[action]) this.app[action].apply(this.app);

        if (this.status == "start"){
            this.toNormal();
            this.status = "normal";
        }
    },
    toNormal: function(){
//		var size = this.getStartMenuNormalSize();
        var css = this.app.css.normalStartMenuNode;
        //css.height = size.height+"px";
//		css.height = "100%";
//		css.width = size.width+"px";

////		this.node.setStyles(css);
//
        if (!this.morph){
            this.morph = new Fx.Morph(this.node, {duration: 50, link: "chain"});
        }
//        this.morph.start(css).chain(function(){
            this.node.setStyles(css);

            MWF.require("MWF.widget.ScrollBar", function(){
                new MWF.widget.ScrollBar(this.node, {
                    "style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
                });
            }.bind(this));
//        }.bind(this));
//
////			this.node.setStyles(css);
////
////			this.startNavis.each(function(naviNode){
////				if (this.currentNavi!=naviNode) naviNode.setStyles(this.app.css.startMenuNaviNode);
////			}.bind(this));

//
//		this.node.set("morph", {duration: 50});
//		this.node.morph(css);
    },
    setNodeCenter: function(node){
        var size = node.getSize();
        var contentSize = this.app.node.getSize();

        var top = contentSize.y/2 - size.y/2;
        var left = contentSize.x/2 - size.x/2;

        if (left<0) left = 0;
        if (top<0) top = 0;
        node.setStyles({"left": left, "top": top});
    },
    getStartMenuNormalSize: function(){
        var naviItemNode = this.node.getFirst();

        var size = naviItemNode.getComputedSize();
        var mt = naviItemNode.getStyle("margin-top").toFloat();
        var mb = naviItemNode.getStyle("margin-bottom").toFloat();
        var height = size.totalWidth+mt+mb;

        var ml = naviItemNode.getStyle("margin-left").toFloat();
        var mr = naviItemNode.getStyle("margin-right").toFloat();
        var width = size.totalWidth+ml+mr;

        return {"width": width, "height": height*this.startNavis.length};
    },
    setStartMenuWidth: function(){
        var naviItemNode = this.node.getFirst();

        var size = naviItemNode.getComputedSize();
        var ml = naviItemNode.getStyle("margin-left").toFloat();
        var mr = naviItemNode.getStyle("margin-right").toFloat();
        var width = size.totalWidth+ml+mr;
        this.node.setStyle("width", (width*this.startNavis.length)+"px");
    },
    onResize: function(){
        if (this.status == "start"){
            this.setNodeCenter(this.node);
        }
    }
});

MWF.xApplication.process.ProcessManager.ApplicationProperty = new Class({
    initialize: function(app, node){
        this.app = app;
        this.node = $(node);
        this.data = this.app.options.application;
    },
    load: function(){
        this.app.restActions.getApplication(this.app.options.application.id, function(json){
            this.data = json.data;
            this.propertyTitleBar = new Element("div", {
                "styles": this.app.css.propertyTitleBar,
                "text": this.data.name
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

            this.createAvailableNode();
            this.createControllerListNode();
        }.bind(this));
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
        if (this.data.icon){
            this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
        }else{
            //this.iconPreviewNode.setStyle("background", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png) center center no-repeat")
            this.iconPreviewNode.setStyle("background", "url("+this.app.path+this.app.options.style+"/icon/application.png) center center no-repeat")
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
            "text": this.app.lp.application.changeIcon
        }).inject(this.iconActionNode);
        changeIconAction.addEvent("click", function(){
            this.changeIcon();
        }.bind(this));
    },
    changeIcon: function(){
        MWF.require("MWF.widget.Upload", function(){
            var upload = new MWF.widget.Upload(this.app.content, {
                "data": null,
                "parameter": {"id": this.data.id},
                "action": this.app.restActions.action,
                "method": "updateApplicationIcon",
                "onCompleted": function(json){
                    this.app.restActions.getApplication(this.data.id, function(json){
                        if (json.data){
                            this.data = json.data;
                            if (this.data.icon){
                                this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
                            }else{
                                this.iconPreviewNode.setStyle("background", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png) center center no-repeat")
                            }
                        }
                    }.bind(this), false)
                }.bind(this)
            });
            upload.load();
        }.bind(this));
    },

    changeIcon_1: function(){
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

                        this.app.restActions.changeApplicationIcon(this.data.id ,function(){
                            this.app.restActions.getApplication(this.data.id, function(json){
                                if (json.data){
                                    this.data = json.data;
                                    if (this.data.icon){
                                        this.iconPreviewNode.setStyle("background", "url(data:image/png;base64,"+this.data.icon+") center center no-repeat");
                                    }else{
                                        this.iconPreviewNode.setStyle("background", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png) center center no-repeat")
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
        this.propertyContentNode = new Element("div", {"styles": {
            "overflow": "hidden",
            "-webkit-user-select": "text",
            "-moz-user-select": "text"
        }}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center' style='margin-top: 20px'>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.alias+"</td><td id='formApplicationAlias'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.description+"</td><td id='formApplicationDescription'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.type+"</td><td id='formApplicationType'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.application.id+"</td><td id='formApplicationId'></td></tr>";
   //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);

        this.nameInput = new MWF.xApplication.process.ProcessManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name, this.app.css.formInput);
        this.aliasInput = new MWF.xApplication.process.ProcessManager.Input(this.propertyContentNode.getElement("#formApplicationAlias"), this.data.alias, this.app.css.formInput);
        this.descriptionInput = new MWF.xApplication.process.ProcessManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        this.typeInput = new MWF.xApplication.process.ProcessManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.applicationCategory, this.app.css.formInput);
        this.idInput = new MWF.xApplication.process.ProcessManager.Input(this.propertyContentNode.getElement("#formApplicationId"), this.data.id, this.app.css.formInput);
    },
    createControllerListNode: function(){
        //if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

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
            "text": this.app.lp.application.setManager //"设置管理者"
        }).inject(this.contentAreaNode);
        changeAdministrators.addEvent("click", function(){
            this.changeAdministrators();
        }.bind(this));

        if (this.data.controllerList){
            this.data.controllerList.each(function(name){
                if (name) var admin = new MWF.widget.O2Person({"name": name}, this.administratorsContentNode, {"style": "application"});
            }.bind(this));
        }
    },
    changeAdministrators: function(){
        var options = {
            "type": "person",
            "title": this.app.lp.application.setAppManager,
            "values": this.data.controllerList || [],
            "onComplete": function(items){
                this.administratorsContentNode.empty();

                var controllerList = [];
                items.each(function(item){
                    controllerList.push(item.data.distinguishedName);
                    var admin = new MWF.widget.O2Person(item.data, this.administratorsContentNode, {"style": "application"});
                }.bind(this));
                this.data.controllerList = controllerList;
                this.app.restActions.saveApplication(this.data, function(json){

                }.bind(this));
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    },

    createAvailableNode: function(){
        //if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.availableTitleNode = new Element("div", {
            "styles": this.app.css.availableTitleNode,
            "text": this.app.lp.application.available
        }).inject(this.contentAreaNode);

        this.availableContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.availableItemsContentNode = new Element("div", {"styles": this.app.css.availableItemsContentNode}).inject(this.availableContentNode);
        this.availableActionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);

        var changeIdentityList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.app.lp.application.setUsableIdentity
        }).inject(this.availableActionAreaNode);
        changeIdentityList.addEvent("click", function(){
            this.changeAvailableIdentitys();
        }.bind(this));

        var changeUnitList = new Element("div", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.app.lp.application.setUsableUnit
        }).inject(this.availableActionAreaNode);
        changeUnitList.addEvent("click", function(){
            this.changeAvailableUnit();
        }.bind(this));

        // var changeDepartmentList = new Element("div", {
        //     "styles": this.app.css.selectButtonStyle,
        //     "text": "设置可用部门"
        // }).inject(this.availableActionAreaNode);
        // changeDepartmentList.addEvent("click", function(){
        //     this.changeAvailableDepartments();
        // }.bind(this));
        //
        // var changeCompanyList = new Element("div", {
        //     "styles": this.app.css.selectButtonStyle,
        //     "text": "设置可用公司"
        // }).inject(this.availableActionAreaNode);
        // changeCompanyList.addEvent("click", function(){
        //     this.changeAvailableCompanys();
        // }.bind(this));

        this.setAvailableItems();
    },
    setAvailableItems: function(){
        if (this.data.availableIdentityList){
            this.data.availableIdentityList.each(function(name){
                if (name) new MWF.widget.O2Identity({"name": name}, this.availableItemsContentNode, {"style": "application"});
            }.bind(this));
        }
        if (this.data.availableUnitList){
            this.data.availableUnitList.each(function(name){
                if (name) new MWF.widget.O2Unit({"name": name}, this.availableItemsContentNode, {"style": "application"});
            }.bind(this));
        }
        // if (this.data.availableDepartmentList){
        //     this.data.availableDepartmentList.each(function(name){
        //         if (name) new MWF.widget.Department({"name": name}, this.availableItemsContentNode, explorer, false, null, {"style": "application"});
        //     }.bind(this));
        // }
        // if (this.data.availableCompanyList){
        //     this.data.availableCompanyList.each(function(name){
        //         if (name) new MWF.widget.Company({"name": name}, this.availableItemsContentNode, explorer, false, null, {"style": "application"});
        //     }.bind(this));
        // }
    },

    changeAvailableIdentitys: function(){
        var options = {
            "type": "identity",
            "title": this.app.lp.application.setAppUsableIdentity,
            "values": this.data.availableIdentityList || [],
            "onComplete": function(items){
                var availableIdentityList = [];
                items.each(function(item){
                    availableIdentityList.push(item.data.distinguishedName);
                }.bind(this));
                this.data.availableIdentityList = availableIdentityList;
                this.app.restActions.saveApplication(this.data, function(json){
                    this.availableItemsContentNode.empty();
                    this.setAvailableItems();
                }.bind(this));
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    },

    changeAvailableUnit: function(){
        var options = {
            "type": "unit",
            "title": this.app.lp.application.setAppUsableUnit,
            "values": this.data.availableUnitList || [],
            "onComplete": function(items){
                var availableUnitList = [];
                items.each(function(item){
                    availableUnitList.push(item.data.distinguishedName);
                }.bind(this));
                this.data.availableUnitList = availableUnitList;
                this.app.restActions.saveApplication(this.data, function(json){
                    this.availableItemsContentNode.empty();
                    this.setAvailableItems();
                }.bind(this));
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
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
        this.typeInput.editMode();
        this.isEdit = true;
    },
    readMode: function(){
        this.nameInput.readMode();
        this.aliasInput.readMode();
        this.descriptionInput.readMode();
        this.typeInput.readMode();
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
        this.data.alias = this.aliasInput.input.get("value");
        this.data.description = this.descriptionInput.input.get("value");
        this.data.applicationCategory = this.typeInput.input.get("value");

        this.app.restActions.saveApplication(this.data, function(json){
            this.propertyTitleBar.set("text", this.data.name);
            this.data.id = json.data.id;
            this.nameInput.save();
            this.aliasInput.save();
            this.descriptionInput.save();
            this.typeInput.save();

            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    }
});

MWF.xApplication.process.ProcessManager.Input = new Class({
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
