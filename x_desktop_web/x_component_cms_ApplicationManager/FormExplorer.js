MWF.xDesktop.requireApp("cms.ApplicationManager", "Explorer", null, false);

MWF.xApplication.cms.ApplicationManager.FormExplorer = new Class({
	Extends: MWF.xApplication.cms.ApplicationManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.CMSCM.LP.form.create,
        "search": MWF.CMSCM.LP.form.search,
        "searchText": MWF.CMSCM.LP.form.searchText,
        "noElement": MWF.CMSCM.LP.form.noFormNoticeText
    },

    _createElement: function(e){
        var _self = this;
        var createForm = function(e, template){
            layout.desktop.getFormDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "template": template,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.column = _self.app.options.column;
                        this.application = _self.app.options.column;
                    },
                    "onPostSave" : function(){
                        _self.reload();
                    }
                };
                layout.desktop.openApplication(e, "cms.FormDesigner", options);
            }.bind(this));

        }

        var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        var createTemplateAreaNode = new Element("div", {"styles": this.css.createFormTemplateAreaNode}).inject(this.app.content);
        createTemplateAreaNode.fade("in");

        var createTemplateScrollNode = new Element("div", {"styles": this.css.createTemplateScrollNode}).inject(createTemplateAreaNode);
        var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateContentNode}).inject(createTemplateScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(createTemplateScrollNode, {"indent": false});
        }.bind(this));

        var _self = this;
        var url = "/x_component_cms_FormDesigner/Module/Form/template/templates.json";
        MWF.getJSON(url, function(json){
            json.each(function(template){
                var templateNode = new Element("div", {"styles": this.css.templateNode}).inject(createTemplateContentNode);
                var templateIconNode = new Element("div", {"styles": this.css.templateIconNode}).inject(templateNode);
                var templateTitleNode = new Element("div", {"styles": this.css.templateTitleNode, "text": template.title}).inject(templateNode);
                templateNode.store("template", template.name);

                var templateIconImgNode = new Element("img", {"styles": this.css.templateIconImgNode}).inject(templateIconNode);
                templateIconImgNode.set("src", "/x_component_cms_FormDesigner/Module/Form/template/"+template.icon);

                templateNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.templateNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.templateNode)},
                    "mousedown": function(){this.setStyles(_self.css.templateNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.templateNode_over)},
                    "click": function(e){
                        createForm(e, this.retrieve("template"));
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
    showDeleteAction: function(){
        if (!this.deleteItemsAction){
            this.deleteItemsAction = new Element("div", {
                "styles": this.css.deleteItemsAction,
                "text": this.app.lp.deleteItems
            }).inject(this.node);
            this.deleteItemsAction.fade("in");
            this.deleteItemsAction.position({
                relativeTo: this.elementContentListNode
            });
            this.deleteItemsAction.addEvent("click", function(){
                var _self = this;
                this.app.confirm("warn", this.deleteItemsAction, MWF.CMSCM.LP.form.deleteFormTitle, MWF.CMSCM.LP.form.deleteForm, 300, 120, function(){
                    _self.deleteItems();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
        }
    },

    _loadItemDataList: function(callback){
        this.app.restActions.listForm(this.app.options.column.id,callback);
    },
    _getItemObject: function(item, index){
        return new MWF.xApplication.cms.ApplicationManager.FormExplorer.Form(this, item, {index:index})
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.CMSCM.LP.form.create,
            "search": MWF.CMSCM.LP.form.search,
            "searchText": MWF.CMSCM.LP.form.searchText,
            "noElement": MWF.CMSCM.LP.form.noFormNoticeText
        };
    },
    deleteItems: function(){
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteForm();
            }else{
                item.deleteForm(function(){
                //    this.reloadItems();
                    this.hideDeleteAction();
                    this.reload();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.cms.ApplicationManager.FormExplorer.Form = new Class({
	Extends: MWF.xApplication.cms.ApplicationManager.Explorer.Item,
	_open: function(e){
        layout.desktop.getFormDesignerStyle(function(){
            var _self = this;
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "onQueryLoad": function(){
                    this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = _self.data.id;
                    this.column = _self.explorer.app.options.column;
                    this.application = _self.explorer.app.options.column;
                }
            };
            this.explorer.app.desktop.openApplication(e, "cms.FormDesigner", options);
        }.bind(this));
	},
	_getIcon: function(){
		var x = (Math.random()*33).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/formIcon/lnk.png",
			"title": this.data.name,
			"par": "cms.FormDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.form.deleteFormTitle, this.explorer.app.lp.form.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
	deleteForm: function(callback){
		this.explorer.app.restActions.deleteForm(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
