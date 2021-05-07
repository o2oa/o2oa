//MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.Org.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Org",
		"icon": "icon.png",
		"width": (layout.desktop.size) ? layout.desktop.size.x*0.7 : 1000,
		"height": (layout.desktop.size) ? layout.desktop.size.y*0.8: 800,
		"title": MWF.xApplication.Org.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Org.LP;
		this.restActions = MWF.Actions.get("x_organization_assemble_control");
	},
	loadApplication: function(callback){
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		this.loadStartMenu();
//		this.loadApplicationLayout();
	},
	loadStartMenu: function(){
		this.startMenuNode = new Element("div", {
			"styles": this.css.startMenuNode
		}).inject(this.node);
		
		this.menu = new MWF.xApplication.Org.Menu(this, this.startMenuNode, {
			"onPostLoad": function(){

				if (this.status){
					if (this.status.navi!==null){
						this.menu.doAction(this.menu.startNavis[this.status.navi]);
					}
				}
			}.bind(this)
		});
		this.addEvent("resize", function(){
			if (this.menu) this.menu.onResize();
		}.bind(this));
	},
	
	clearContent: function(){
		if (this.orgConfiguratorContentNode){
            if (this.orgConfigurator){
                this.orgConfigurator.destroy();
                this.orgConfigurator = null;
            }
			this.orgConfiguratorContentNode.destroy();
            this.orgConfiguratorContentNode = null;
		}
		if (this.identityConfiguratorContentNode){
			if (this.identityConfigurator) delete this.identityConfigurator;
			this.identityConfiguratorContentNode.destroy();
		}
		if (this.groupConfiguratorContentNode){
			if (this.groupConfigurator){
                this.groupConfigurator.destroy();
                this.groupConfigurator = null;
			}
			this.groupConfiguratorContentNode.destroy();
		}
		if (this.roleConfiguratorContentNode){
			if (this.roleConfigurator){
                this.roleConfigurator.destroy();
                this.roleConfigurator = null;
			}
			this.roleConfiguratorContentNode.destroy();
		}
		if (this.personConfiguratorContentNode){
			if (this.personConfigurator){
                this.personConfigurator.destroy();
                this.personConfigurator = null;
			}
			this.personConfiguratorContentNode.destroy();
		}
		if (this.importConfiguratorContentNode){
            this.importConfiguratorContentNode.destroy();
		}
		if (this.privateNamesQueryPowerContentNode){
            this.privateNamesQueryPowerContentNode.destroy();
		}
        if (this.pingyinArea) this.pingyinArea.empty();
	},
	orgConfig: function(){
		this.clearContent();
		this.orgConfiguratorContentNode = new Element("div", {
			"styles": this.css.rightContentNode
		}).inject(this.node);
		this.loadOrgConfig();
	},
	loadOrgConfig: function(){
        MWF.xDesktop.requireApp("Org", "UnitExplorer", function(){
            //MWF.xDesktop.requireApp("Org", "Actions.RestActions", function(){
			//	if (!this.restActions) this.restActions = new MWF.xApplication.Org.Actions.RestActions();
				this.orgConfigurator = new MWF.xApplication.Org.UnitExplorer(this.orgConfiguratorContentNode, this.restActions);
				this.orgConfigurator.app = this;
				this.orgConfigurator.load();
			//}.bind(this));
		}.bind(this));
	},
	groupConfig: function(){
		this.clearContent();
		this.groupConfiguratorContentNode = new Element("div", {
			"styles": this.css.rightContentNode
		}).inject(this.node);
		this.loadGroupConfig();
	},
	loadGroupConfig: function(){
        MWF.xDesktop.requireApp("Org", "GroupExplorer", function(){
            //MWF.xDesktop.requireApp("Org", "Actions.RestActions", function(){
			//	if (!this.restActions) this.restActions = new MWF.xApplication.Org.Actions.RestActions();
				this.groupConfigurator = new MWF.xApplication.Org.GroupExplorer(this.groupConfiguratorContentNode, this.restActions);
				this.groupConfigurator.app = this;
				this.groupConfigurator.load();
			//}.bind(this));
		}.bind(this));
	},
	roleConfig: function(){
		this.clearContent();
		this.roleConfiguratorContentNode = new Element("div", {
			"styles": this.css.rightContentNode
		}).inject(this.node);
		this.loadRoleConfig();
	},
	loadRoleConfig: function(){
        MWF.xDesktop.requireApp("Org", "RoleExplorer", function(){
            //MWF.xDesktop.requireApp("Org", "Actions.RestActions", function(){
			//	if (!this.restActions) this.restActions = new MWF.xApplication.Org.Actions.RestActions();
				this.roleConfigurator = new MWF.xApplication.Org.RoleExplorer(this.roleConfiguratorContentNode, this.restActions);
				this.roleConfigurator.app = this;
				this.roleConfigurator.load();
			//}.bind(this));
		}.bind(this));
	},

	personConfig: function(){
		this.clearContent();
		this.personConfiguratorContentNode = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.loadPersonConfig();
	},
	loadPersonConfig: function(){
        MWF.xDesktop.requireApp("Org", "PersonExplorer", function(){
            //MWF.xDesktop.requireApp("Org", "Actions.RestActions", function(){
			//	if (!this.restActions) this.restActions = new MWF.xApplication.Org.Actions.RestActions();
				this.personConfigurator = new MWF.xApplication.Org.PersonExplorer(this.personConfiguratorContentNode, this.restActions);
				this.personConfigurator.app = this;
				this.personConfigurator.load();
			//}.bind(this));
		}.bind(this));
	},

    personImport: function(){
		debugger;
		this.clearContent();
        this.importConfiguratorContentNode = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.importConfiguratorContentNode.set("load", {"onSuccess": function(){
        	this.importPersonTitleNode = this.importConfiguratorContentNode.getElement(".importPersonTitleNode");
            this.importPersonTemplateNode = this.importConfiguratorContentNode.getElement(".importPersonTemplateNode");
            this.importPersonNode = this.importConfiguratorContentNode.getElement(".importPersonNode");
            this.importPersonResultNode = this.importConfiguratorContentNode.getElement(".importPersonResultNode");
			this.exportPersonNode = this.importConfiguratorContentNode.getElement(".exportPersonNode");
            o2.loadCss(this.path+this.options.style+"/importCss.css", this.importConfiguratorContentNode, function(){
                this.loadPersonImport();
            }.bind(this));
        }.bind(this)}).load(this.path+this.options.style+"/importView.html");
	},
    loadPersonImport: function(){
    	var action = o2.Actions.get("x_organization_assemble_control");
        var url = o2.filterUrl(action.action.address + action.action.actions.getImportPersonTemplate.uri);
        var infor = this.lp.importPersonInfor.replace("{url}", url);

        this.importPersonTitleNode.set("text", this.lp.importPersonTitle);
        this.importPersonTemplateNode.set("html", infor);
        this.importPersonNode.set("text", this.lp.importPersonAction);
		

        this.importPersonNode.addEvent("click", function(){
            this.importPersonResultNode.hide();
        	o2.require("o2.widget.Upload", function(){
        		new o2.widget.Upload(this.content, {
                    "action": "x_organization_assemble_control",
                    "method": "importPerson",
                    "multiple": false,
                    "onCompleted": function(json){
                        var url = o2.filterUrl(action.action.address + action.action.actions.getImportPersonResault.uri);
                        url = url.replace("{flag}", json.data.flag);
                        var result = this.lp.importPersonResult.replace("{url}", url);
                        this.importPersonResultNode.set("html", result);
                        this.importPersonResultNode.show();
                    }.bind(this)
                }).load();
			}.bind(this));
		}.bind(this));

		var exporturl = o2.filterUrl(o2.Actions.getHost("x_cms_assemble_control") + "/x_organization_assemble_control/jaxrs/export/export/all");
		this.exportPersonNode.set("text", this.lp.exportPersonText);
		this.exportPersonNode.set("href", exporturl);
	},
	privateNamesQueryPower:function(){
		debugger;
		this.clearContent();
        this.privateNamesQueryPowerContentNode = new Element("div", {
            "styles": this.css.rightContentNode
        }).inject(this.node);
        this.privateNamesQueryPowerContentNode.set("load", {"onSuccess": function(){
			this.queryPrivateConfigAreaNode = this.privateNamesQueryPowerContentNode.getElement(".queryPrivateConfigAreaNode");

            o2.loadCss(this.path+this.options.style+"/queryPrivateConfig.css", this.privateNamesQueryPowerContentNode, function(){
                this.loadprivateNamesQueryPower();
            }.bind(this));
        }.bind(this)}).load(this.path+this.options.style+"/queryPrivateConfigView.html");
	},
    loadprivateNamesQueryPower: function(){
		MWF.xDesktop.requireApp("Org", "PrivateConfig", function(){
				this.privateConfigurator = new MWF.xApplication.Org.PrivateConfig(this.privateNamesQueryPowerContentNode);
				this.privateConfigurator.app = this;
				this.privateConfigurator.load();
		}.bind(this));
	},

    recordStatus: function(){
        var idx = null;
        if (this.menu.currentNavi){
            idx = this.menu.startNavis.indexOf(this.menu.currentNavi);
        }
        return {"navi": idx};
    }
	
});


MWF.xApplication.Org.Menu = new Class({
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
		//var menuUrl = (MWF.AC.isOrganizationManager()) ? this.app.path+"startMenu_admin.json" : this.app.path+"startMenu.json";
		var menuUrl = this.app.path+"startMenu.json";

		MWF.getJSON(menuUrl, function(json){
			json.each(function(navi){
				if (navi.display){
					var naviNode = new Element("div", {
						"styles": this.app.css.startMenuNaviNode,
						"title": navi.title
					});
					naviNode.store("naviData", navi);

					var iconNode =  new Element("div", {
						"styles": this.app.css.startMenuIconNode
					}).inject(naviNode);
					iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.icon+"60.png)");

					var textNode =  new Element("div", {
						"styles": this.app.css.startMenuTextNode,
						"text": navi.title
					});
					textNode.inject(naviNode);
					naviNode.inject(this.node);

					this.startNavis.push(naviNode);

					this.setStartNaviEvent(naviNode, navi);

					this.setNodeCenter(this.node);
				}
			}.bind(this));
			this.setStartMenuWidth();
			
			this.fireEvent("postLoad");
		}.bind(this));
	},
	setStartNaviEvent: function(naviNode){
		var _self = this;
        naviNode.addEvents({
            "mouseover": function(){
                //debugger;
                if (_self.currentNavi!==this){
                    var iconNode = this.getFirst();
                    this.setStyles(_self.app.css.startMenuNaviNode_over);
                    var navi = this.retrieve("naviData");
                    iconNode.setStyle("background-image", "url("+_self.app.path+_self.app.options.style+"/icon/"+navi.icon+".png)");
                    iconNode.setStyles(_self.app.css.startMenuIconNode_over);
                }
            },
            "mouseout": function(){
                var iconNode = this.getFirst();
                if (_self.currentNavi!==this) this.setStyles(_self.app.css.startMenuNaviNode);
                var navi = this.retrieve("naviData");
                iconNode.setStyle("background-image", "url("+_self.app.path+_self.app.options.style+"/icon/"+navi.icon+"60.png)");
                iconNode.setStyles(_self.app.css.startMenuIconNode);
            },
            "mousedown": function(){
                if (_self.currentNavi!==this) this.setStyles(_self.app.css.startMenuNaviNode_down);
            },
            "mouseup": function(){if (_self.currentNavi!==this) this.setStyles(_self.app.css.startMenuNaviNode_over);},
            "click": function(){
                _self.doAction.apply(_self, [this]);
            }
        });
	},
	doAction: function(naviNode){
		var navi = naviNode.retrieve("naviData");
		var action = navi.action;

        this.startNavis.each(function(node){
			node.removeEvents("mouseover");
            node.removeEvents("mouseout");
            node.removeEvents("mousedown");
            node.removeEvents("mouseup");
            node.setStyles(this.app.css.startMenuNaviLeftNode);
            var iconNode = node.getFirst();
			var textNode = node.getLast();

            var tmpnavi = node.retrieve("naviData");
			iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+tmpnavi.icon+"32.png)");
            iconNode.setStyles(this.app.css.startMenuIconLeftNode);
            textNode.setStyles(this.app.css.startMenuTextLeftNode);
		}.bind(this));

		naviNode.setStyles(this.app.css.startMenuNaviLeftNode_current);
		this.currentNavi = naviNode;

        if (this.status === "start"){
            this.toNormal();
            this.status = "normal";
        }
		if (this.app[action]) this.app[action].apply(this.app);
	},
	toNormal: function(){
		var css = this.app.css.normalStartMenuNode;
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 50, link: "chain"});
		}
        this.app.pingyinArea = new Element("div", {"styles": this.app.css.startMenuNaviLeftPingyinNode}).inject(this.node, "top");
		this.morph.start(css).chain(function(){
			this.node.setStyles(css);
			
			// MWF.require("MWF.widget.ScrollBar", function(){
			// 	new MWF.widget.ScrollBar(this.node, {
			// 		"style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			// 	});
			// }.bind(this));
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
		if (this.status === "start"){
			this.setNodeCenter(this.node);
		}
	}
});

