MWF.xApplication.process.Application.options = {
    "multitask": true,
    "executable": false
};
MWF.xApplication.process.Application.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "process.Application",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
        "application": "",
        "id": "",
		"title": MWF.xApplication.process.Application.LP.title
	},
	onQueryLoad: function() {
        this.lp = MWF.xApplication.process.Application.LP;
        if (this.status) this.options.id = this.status.id;
    },

    getApplication: function(success, failure){
        if (!this.options.application){
            this.getAction(function(){
                this.action.getApplication(this.options.id, function(json){
                    if (json.data){
                        this.setTitle(this.options.title+"-"+json.data.name);
                        this.options.application = json.data;
                        if (success) success();
                    }else{
                        if (failure) failure();
                    }
                }.bind(this), function(){if (failure) failure();}.bind(this), false)
            }.bind(this));
        }else{
            if (success) success();
        }
    },

    getAction: function(callback){
        if (!this.action){
            this.action = MWF.Actions.get("x_processplatform_assemble_surface");
            if (callback) callback();
            // MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //     this.action = new MWF.xApplication.process.Application.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden", "background-color": "#ffffff"}
        }).inject(this.content);
    },
	loadApplication: function(callback){
        this.getApplication(function(){
            this.createNode();
            this.loadStartMenu();
            if (callback) callback();
        }.bind(this), function(){
            this.close();
        }.bind(this));
	},
    loadStartMenu: function(callback){
        this.startMenuNode = new Element("div", {
            "styles": this.css.startMenuNode
        }).inject(this.node);

        this.menu = new MWF.xApplication.process.Application.Menu(this, this.startMenuNode, {
            "onPostLoad": function(){
                this.css.rightContentNode["margin-left"] = "140px";
                if (this.status){
                    if (this.status.hideMenu){
                        this.startMenuNode.setStyle("display", "none");
                        this.css.rightContentNode["margin-left"] = "0px";
                    }
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
        if (this.myWorkConfiguratorContentNode){
            if (this.myWorkConfigurator){
                if (this.myWorkConfigurator.setContentSizeFun) this.removeEvent("resize", this.myWorkConfigurator.setContentSizeFun);
                MWF.release(this.myWorkConfigurator);
                this.myWorkConfigurator = null;
            }
            this.myWorkConfiguratorContentNode.destroy();
            this.myWorkConfiguratorContentNode = null;
        }
        if (this.myCompletedConfiguratorContentNode){
            if (this.myCompletedConfigurator){
                if (this.myCompletedConfigurator.setContentSizeFun) this.removeEvent("resize", this.myCompletedConfigurator.setContentSizeFun);
                MWF.release(this.myCompletedConfigurator);
                this.myCompletedConfigurator = null;
            }
            this.myCompletedConfiguratorContentNode.destroy();
            this.myCompletedConfiguratorContentNode = null;
        }
        if (this.workConfiguratorContentNode){
            if (this.workConfigurator){
                if (this.workConfigurator.setContentSizeFun) this.removeEvent("resize", this.workConfigurator.setContentSizeFun);
                MWF.release(this.workConfigurator);
                this.workConfigurator = null;
            }
            this.workConfiguratorContentNode.destroy();
            this.workConfiguratorContentNode = null;
        }
        if (this.completedConfiguratorContentNode){
            if (this.completedConfigurator){
                if (this.completedConfigurator.setContentSizeFun) this.removeEvent("resize", this.completedConfigurator.setContentSizeFun);
                MWF.release(this.completedConfigurator);
                this.completedConfigurator = null;
            }
            this.completedConfiguratorContentNode.destroy();
            this.completedConfiguratorContentNode = null;
        }
        if (this.dataConfiguratorContent){
            if (this.dataConfigurator){
                if (this.dataConfigurator.setContentSizeFun) this.removeEvent("resize", this.dataConfigurator.setContentSizeFun);
                MWF.release(this.dataConfigurator);
                this.dataConfigurator = null;
            }
            this.dataConfiguratorContent.destroy();
            this.dataConfiguratorContent = null;
        }
        if (this.serialConfiguratorContent){
            if (this.serialConfigurator){
                if (this.serialConfigurator.setContentSizeFun) this.removeEvent("resize", this.serialConfigurator.setContentSizeFun);
                MWF.release(this.serialConfigurator);
                this.serialConfigurator = null;
            }
            this.serialConfiguratorContent.destroy();
            this.serialConfiguratorContent = null;
        }
        if (this.statConfiguratorContentNode){
            if (this.statConfigurator){
                if (this.statConfigurator.setContentSizeFun) this.removeEvent("resize", this.statConfigurator.setContentSizeFun);
                MWF.release(this.statConfigurator);
                this.statConfigurator = null;
            }
            this.statConfiguratorContentNode.destroy();
            this.statConfiguratorContentNode = null;
        }
        if (this.viewConfiguratorContentNode){
            if (this.viewConfigurator){
                if (this.viewConfigurator.setContentSizeFun) this.removeEvent("resize", this.viewConfigurator.setContentSizeFun);
                MWF.release(this.viewConfigurator);
                this.viewConfigurator = null;
            }
            this.viewConfiguratorContentNode.destroy();
            this.viewConfiguratorContentNode = null;
        }
        // if (this.statConfiguratorContentNode){
        //     if (this.statConfigurator){
        //         MWF.release(this.statConfigurator);
        //         this.statConfigurator = null;
        //     }
        //     this.statConfiguratorContentNode.destroy();
        //     this.statConfiguratorContentNode = null;
        // }
    },

    // statConfigTmp: function(){
    //     this.clearContent();
    //     this.statConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
    //     this.loadStatConfig();
    // },
    // loadStatConfigTmp: function(){
    //     MWF.xDesktop.requireApp("process.Application", "StatExplorer", function(){
    //         MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
    //             if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
    //             this.statConfigurator = new MWF.xApplication.process.Application.StatExplorer(this.statConfiguratorContentNode, this.restActions);
    //             this.statConfigurator.app = this;
    //             this.statConfigurator.load();
    //         }.bind(this));
    //     }.bind(this));
    // },

    statConfig: function(){
        this.clearContent();
        this.statConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadStatConfig();
    },
    loadStatConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "StatExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
                //if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.statConfigurator = new MWF.xApplication.process.Application.StatExplorer(this.statConfiguratorContentNode, this.restActions);
                this.statConfigurator.app = this;
                this.statConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    viewConfig: function(){
        this.clearContent();
        this.viewConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadViewConfig();
    },
    loadViewConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "ViewExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.viewConfigurator = new MWF.xApplication.process.Application.ViewExplorer(this.viewConfiguratorContentNode, this.restActions);
                this.viewConfigurator.app = this;
                this.viewConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    myWorkConfig: function(){
        this.clearContent();
        this.myWorkConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadMyWorkConfig();
    },
    loadMyWorkConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "MyWorkExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.myWorkConfigurator = new MWF.xApplication.process.Application.MyWorkExplorer(this.myWorkConfiguratorContentNode, this.restActions);
                this.myWorkConfigurator.app = this;
                this.myWorkConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    myWorkCompletedConfig: function(){
        this.clearContent();
        this.myCompletedConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadMyWorkCompletedConfig();
    },
    loadMyWorkCompletedConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "MyWorkCompletedExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.myCompletedConfigurator = new MWF.xApplication.process.Application.MyWorkCompletedExplorer(this.myCompletedConfiguratorContentNode, this.restActions);
                this.myCompletedConfigurator.app = this;
                this.myCompletedConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    workConfig: function(){
        this.clearContent();
        this.workConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadWorkConfig();
    },
    loadWorkConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "WorkExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.workConfigurator = new MWF.xApplication.process.Application.WorkExplorer(this.workConfiguratorContentNode, this.restActions);
                this.workConfigurator.app = this;
                this.workConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    workCompletedConfig: function(){
        this.clearContent();
        this.completedConfiguratorContentNode = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadWorkCompletedConfig();
    },
    loadWorkCompletedConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "WorkCompletedExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.completedConfigurator = new MWF.xApplication.process.Application.WorkCompletedExplorer(this.completedConfiguratorContentNode, this.restActions);
                this.completedConfigurator.app = this;
                this.completedConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },
    dataConfig: function(){
        this.clearContent();
        this.dataConfiguratorContent = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadDataConfig();
    },
    loadDataConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "DictionaryExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
                //if (!this.dictActions) this.dictActions = new MWF.xApplication.process.ProcessManager.Actions.RestActions();
                //if (!this.dictActions) this.dictActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.dictActions) this.dictActions = MWF.Actions.get("x_processplatform_assemble_designer");
                this.dataConfigurator = new MWF.xApplication.process.Application.DictionaryExplorer(this.dataConfiguratorContent, this.dictActions, {
                    "noCreate": true,
                    "noDelete": true,
                    "noModifyName": true,
                    "readMode": !this.options.application.allowControl
                });
                this.dataConfigurator.app = this;
                this.dataConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    serialConfig: function(){
        this.clearContent();
        this.serialConfiguratorContent = new Element("div", {"styles": this.css.rightContentNode}).inject(this.node);
        this.loadSerialConfig();
    },
    loadSerialConfig: function(){
        MWF.xDesktop.requireApp("process.Application", "SerialExplorer", function(){
            //MWF.xDesktop.requireApp("process.Application", "Actions.RestActions", function(){
            //    if (!this.restActions) this.restActions = new MWF.xApplication.process.Application.Actions.RestActions();
                if (!this.restActions) this.restActions = MWF.Actions.get("x_processplatform_assemble_surface");
                this.serialConfigurator = new MWF.xApplication.process.Application.SerialExplorer(this.serialConfiguratorContent, this.restActions);
                this.serialConfigurator.app = this;
                this.serialConfigurator.load();
            //}.bind(this));
        }.bind(this));
    },

    recordStatus: function(){
        var idx = null;
        if (this.menu.currentNavi){
            idx = this.menu.startNavis.indexOf(this.menu.currentNavi);
        }
        return {"navi": idx, "id": this.options.id};
    }
});

MWF.xApplication.process.Application.Menu = new Class({
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
        //if (MWF.AC.isAdministrator()) menuUrl = this.app.path+"startMenu_admin.json";

        MWF.getJSON(menuUrl, function(json){
            json.each(function(navi){
                var naviNode = new Element("div", {
                    "styles": this.app.css.startMenuNaviNode
                });
                naviNode.store("naviData", navi);
                if (navi.hint) naviNode.set("title", navi.hint);

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
        var css = this.app.css.normalStartMenuNode;
        if (!this.morph){
            this.morph = new Fx.Morph(this.node, {duration: 50, link: "chain"});
        }
        this.morph.start(css).chain(function(){
            this.node.setStyles(css);

            MWF.require("MWF.widget.ScrollBar", function(){
                new MWF.widget.ScrollBar(this.node, {
                    "style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
                });
            }.bind(this));
        }.bind(this));
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