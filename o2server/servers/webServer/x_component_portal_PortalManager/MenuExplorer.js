MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.portal.PortalManager.MenuExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],

    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.portal.PortalManager.LP.menu.create,
            "search": MWF.xApplication.portal.PortalManager.LP.menu.search,
            "searchText": MWF.xApplication.portal.PortalManager.LP.menu.searchText,
            "noElement": MWF.xApplication.portal.PortalManager.LP.menu.noProcessNoticeText
        }
    },

    _createElement: function(e){
        var createPortal = function(e, template){
            var options = {
                "template": template,
                "onQueryLoad": function(){
                    this.actions = _self.app.restActions;
                    this.application = _self.app.options.application;
                }
            };
            layout.desktop.openApplication(e, "portal.MenuDesigner", options);
        };

        var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        var createTemplateAreaNode = new Element("div", {"styles": this.css.createTemplateAreaNode}).inject(this.app.content);
        createTemplateAreaNode.fade("in");

        var createTemplateScrollNode = new Element("div", {"styles": this.css.createTemplateScrollNode}).inject(createTemplateAreaNode);
        var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateContentNode}).inject(createTemplateScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(createTemplateScrollNode, {"indent": false});
        }.bind(this));

        var _self = this;
        var url = "/x_component_portal_PageDesigner/$Page/template/templates.json";
        MWF.getJSON(url, function(json){
            json.each(function(template){
                var templateNode = new Element("div", {"styles": this.css.templateNode}).inject(createTemplateContentNode);
                var templateIconNode = new Element("div", {"styles": this.css.templateIconNode}).inject(templateNode);
                var templateTitleNode = new Element("div", {"styles": this.css.templateTitleNode, "text": template.title}).inject(templateNode);
                templateNode.store("template", template.name);

                var templateIconImgNode = new Element("img", {"styles": this.css.templateIconImgNode}).inject(templateIconNode);
                templateIconImgNode.set("src", "/x_component_process_ProcessDesigner/$Process/template/"+template.icon);

                templateNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.templateNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.templateNode)},
                    "mousedown": function(){this.setStyles(_self.css.templateNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.templateNode_over)},
                    "click": function(e){
                        createPage(e, this.retrieve("template"));
                        createTemplateAreaNode.destroy();
                        createTemplateMaskNode.destroy();
                    }
                });

            }.bind(this))

        }.bind(this));

        createTemplateMaskNode.addEvent("click", function(){
            createTemplateAreaNode.destroy();
            createTemplateMaskNode.destroy();
        });

        var size = this.app.content.getSize();
        var y = (size.y - 262)/2;
        var x = (size.x - 828)/2;
        if (y<0) y=0;
        if (x<0) x=0;
        createTemplateAreaNode.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });
    },
    _loadItemDataList: function(callback){
        this.app.restActions.listMenu(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.portal.PortalManager.MenuExplorer.Menu(this, item)
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteMenu();
            }else{
                item.deleteMenu(function(){
                    //    this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.portal.PortalManager.MenuExplorer.Menu= new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,
	
	_open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.application = _self.explorer.app.options.application;
            }
        };
        this.explorer.app.desktop.openApplication(e, "portal.MenuDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
			"title": this.data.name,
			"par": "portal.MenuDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.process.deleteProcessTitle, this.explorer.app.lp.process.deleteProcess, 320, 110, function(){
//			_self.deleteProcess();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
	deleteMenu: function(callback){
		this.explorer.actions.deleteMenu(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
