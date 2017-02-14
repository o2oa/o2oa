MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.Organization.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Organization",
		"icon": "icon.png",
		"width": (layout.desktop.size) ? layout.desktop.size.x*0.7 : 1000,
		"height": (layout.desktop.size) ? layout.desktop.size.y*0.7: 700,
		"title": MWF.xApplication.Organization.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Organization.LP;
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
	loadStartMenu: function(callback){
		this.startMenuNode = new Element("div", {
			"styles": this.css.startMenuNode
		}).inject(this.node);
		
		this.menu = new MWF.xApplication.Organization.Menu(this, this.startMenuNode, {
			"onPostLoad": function(){

				if (this.status){
					if (this.status.navi!=null){
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
			if (this.orgConfigurator) MWF.release(this.orgConfigurator);
			this.orgConfiguratorContentNode.destroy();
            this.orgConfiguratorContentNode = null;
		}
		if (this.identityConfiguratorContentNode){
			if (this.identityConfigurator) delete this.identityConfigurator;
			this.identityConfiguratorContentNode.destroy();
		}
		if (this.groupConfiguratorContentNode){
			if (this.groupConfigurator) delete this.groupConfigurator;
			this.groupConfiguratorContentNode.destroy();
		}
		if (this.roleConfiguratorContentNode){
			if (this.roleConfigurator) delete this.roleConfigurator;
			this.roleConfiguratorContentNode.destroy();
		}
		if (this.personConfiguratorContentNode){
			if (this.personConfigurator) delete this.personConfigurator;
			this.personConfiguratorContentNode.destroy();
		}
	},
	orgConfig: function(){
		this.clearContent();
		this.orgConfiguratorContentNode = new Element("div", {
			"styles": this.css.rightContentNode
		}).inject(this.node);
		this.loadOrgConfig();
	},
	loadOrgConfig: function(){
        MWF.xDesktop.requireApp("Organization", "OrgExplorer", function(){
            MWF.xDesktop.requireApp("Organization", "Actions.RestActions", function(){
				if (!this.restActions) this.restActions = new MWF.xApplication.Organization.Actions.RestActions();
				this.orgConfigurator = new MWF.xApplication.Organization.OrgExplorer(this.orgConfiguratorContentNode, this.restActions);
				this.orgConfigurator.app = this;
				this.orgConfigurator.load();
			}.bind(this));
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
        MWF.xDesktop.requireApp("Organization", "GroupExplorer", function(){
            MWF.xDesktop.requireApp("Organization", "Actions.RestActions", function(){
				if (!this.restActions) this.restActions = new MWF.xApplication.Organization.Actions.RestActions();
				this.groupConfigurator = new MWF.xApplication.Organization.GroupExplorer(this.groupConfiguratorContentNode, this.restActions);
				this.groupConfigurator.app = this;
				this.groupConfigurator.load();
			}.bind(this));
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
        MWF.xDesktop.requireApp("Organization", "RoleExplorer", function(){
            MWF.xDesktop.requireApp("Organization", "Actions.RestActions", function(){
				if (!this.restActions) this.restActions = new MWF.xApplication.Organization.Actions.RestActions();
				this.roleConfigurator = new MWF.xApplication.Organization.RoleExplorer(this.roleConfiguratorContentNode, this.restActions);
				this.roleConfigurator.app = this;
				this.roleConfigurator.load();
			}.bind(this));
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
        MWF.xDesktop.requireApp("Organization", "PersonExplorer", function(){
            MWF.xDesktop.requireApp("Organization", "Actions.RestActions", function(){
				if (!this.restActions) this.restActions = new MWF.xApplication.Organization.Actions.RestActions();
				this.personConfigurator = new MWF.xApplication.Organization.PersonExplorer(this.personConfiguratorContentNode, this.restActions);
				this.personConfigurator.app = this;
				this.personConfigurator.load();
			}.bind(this));
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


MWF.xApplication.Organization.Menu = new Class({
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
		var menuUrl = (MWF.AC.isAdministrator()) ? this.app.path+"startMenu_admin.json" : this.app.path+"startMenu.json";

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

