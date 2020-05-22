MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.query.QueryManager.RevealExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],
    options: {
        "style": "default",
        "tooltip": {
            "create": MWF.xApplication.query.QueryManager.LP.reveal.create,
            "search": MWF.xApplication.query.QueryManager.LP.reveal.search,
            "searchText": MWF.xApplication.query.QueryManager.LP.reveal.searchText,
            "noElement": MWF.xApplication.query.QueryManager.LP.reveal.noRevealNoticeText
        }
    },

    _createElement: function(e){
        this.pageTemplateList = null;
        this.defalutPageTemplateList = null;
        var _self = this;
        var createDefaultPage = function(e, template){
            layout.desktop.getPageDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.pageDesignerStyle,
                    "template": template,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.application = _self.app.options.application;
                    }
                };
                layout.desktop.openApplication(e, "portal.PageDesigner", options);
            }.bind(this));
        };
        var createPage = function(e, template){
            layout.desktop.getPageDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.pageDesignerStyle,
                    "templateId": template,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.application = _self.app.options.application;
                    }
                };
                layout.desktop.openApplication(e, "portal.PageDesigner", options);
            }.bind(this));
        };

        var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        var createTemplateAreaNode = new Element("div", {"styles": this.css.createFormTemplateAreaNode}).inject(this.app.content);
        createTemplateAreaNode.fade("in");

        var createTemplateTitleNode = new Element("div", {"styles": this.css.createTemplateFormTitleNode, "text": this.app.lp.createSelectTemplate}).inject(createTemplateAreaNode);
        var createTemplateCategoryNode = new Element("div", {"styles": this.css.createTemplateFormCategoryNode}).inject(createTemplateAreaNode);
        var createTemplateCategoryTitleNode = new Element("div", {"styles": this.css.createTemplateFormCategoryTitleNode, "text": this.app.lp.templateCategory}).inject(createTemplateCategoryNode);

        var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateFormContentNode}).inject(createTemplateAreaNode);

        var createTemplateCategoryAllNode = new Element("div", {"styles": this.css.createTemplateFormCategoryItemNode, "text": this.app.lp.all}).inject(createTemplateCategoryNode);
        createTemplateCategoryAllNode.addEvent("click", function(){
            loadAllTemplates();
        });
        this.app.restActions.listPageTemplateCategory(function(json){
            json.data.each(function(d){
                var createTemplateCategoryItemNode = new Element("div", {"styles": this.css.createTemplateFormCategoryItemNode, "text": d.name+"("+ d.count+")", "value": d.name}).inject(createTemplateCategoryNode);
                createTemplateCategoryItemNode.addEvent("click", function(){
                    createTemplateContentNode.empty();
                    createTemplateCategoryNode.getElements("div").each(function(node, i){
                        if (i>0) node.setStyles(_self.css.createTemplateFormCategoryItemNode);
                    });
                    this.setStyles(_self.css.createTemplateFormCategoryItemNode_current);
                    loadTemplates(this.get("value"));
                });
            }.bind(this));
        }.bind(this));

        var resize = function(){
            var size = this.app.content.getSize();
            var y = (size.y*0.1)/2;
            var x = (size.x*0.1)/2;
            if (y<0) y=0;
            if (x<0) x=0;
            createTemplateAreaNode.setStyles({
                "top": ""+y+"px",
                "left": ""+x+"px"
            });
            y = size.y*0.9-createTemplateCategoryNode.getSize().y-70;
            createTemplateContentNode.setStyle("height", ""+y+"px");
        }.bind(this);
        resize();
        this.app.addEvent("resize", resize);

        var getDefaultPageTemplateList = function(callback){
            if (this.defalutPageTemplateList){
                if (callback) callback();
            }else{
                var url = "../x_component_portal_PageDesigner/Module/Page/template/templates.json";
                MWF.getJSON(url, function(json){
                    this.defalutPageTemplateList = json;
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this);
        var loadDefaultTemplate = function(){
            getDefaultPageTemplateList(function(){
                this.defalutPageTemplateList.each(function(template){
                    var templateNode = new Element("div", {"styles": this.css.formTemplateNode}).inject(createTemplateContentNode);
                    var templateIconNode = new Element("div", {"styles": this.css.formTemplateIconNode}).inject(templateNode);
                    var templateTitleNode = new Element("div", {"styles": this.css.formTemplateTitleNode, "text": template.title}).inject(templateNode);
                    templateNode.store("template", template.name);

                    var templateIconImgNode = new Element("img", {"styles": this.css.formTemplateIconImgNode}).inject(templateIconNode);
                    templateIconImgNode.set("src", "../x_component_portal_PageDesigner/Module/Page/template/"+template.icon);

                    templateNode.addEvents({
                        "mouseover": function(){this.setStyles(_self.css.formTemplateNode_over)},
                        "mouseout": function(){this.setStyles(_self.css.formTemplateNode)},
                        "mousedown": function(){this.setStyles(_self.css.formTemplateNode_down)},
                        "mouseup": function(){this.setStyles(_self.css.formTemplateNode_over)},
                        "click": function(e){
                            createDefaultPage(e, this.retrieve("template"));
                            _self.app.removeEvent("resize", resize);
                            createTemplateAreaNode.destroy();
                            createTemplateMaskNode.destroy();
                        }
                    });
                }.bind(this))
            }.bind(this));
        }.bind(this);

        var getPageTemplateList = function(callback){
            if (this.pageTemplateList){
                if (callback) callback();
            }else{
                this.app.restActions.listPageTemplate(function(json){
                    this.pageTemplateList = json.data;
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this);
        var loadTemplates = function(category){
            getPageTemplateList(function(){
                Object.each(this.pageTemplateList, function(v, k){
                    var flag = (category) ? (k==category) : true;
                    if (flag){
                        v.each(function(template){
                            var templateNode = new Element("div", {"styles": this.css.formTemplateNode}).inject(createTemplateContentNode);
                            var templateIconNode = new Element("div", {"styles": this.css.formTemplatePreviewNode}).inject(templateNode);
                            var templateTitleNode = new Element("div", {"styles": this.css.formTemplateTitleNode, "text": template.name}).inject(templateNode);
                            templateNode.store("template", template.id);

                            templateIconNode.set("html", template.outline);

                            var templateActionNode = new Element("img", {"styles": this.css.formTemplateActionNode}).inject(templateIconNode);
                            templateActionNode.addEvent("click", function(e){
                                var thisNode = this.getParent().getParent();
                                var id = thisNode.retrieve("template");
                                _self.app.confirm("wram", e, _self.app.lp.page.deletePageTemplateTitle, _self.app.lp.page.deletePageTemplate, 300, 120, function(){
                                    _self.app.restActions.deletePageTemplate(id, function(json){
                                        thisNode.destroy();
                                    }.bind(this));
                                    this.close();
                                }, function(){
                                    this.close();
                                });
                                e.stopPropagation();
                            });

                            templateNode.addEvents({
                                "mouseover": function(){
                                    this.setStyles(_self.css.formTemplateNode_over);
                                    if (templateActionNode) templateActionNode.setStyle("display", "block");
                                },
                                "mouseout": function(){
                                    this.setStyles(_self.css.formTemplateNode);
                                    if (templateActionNode) templateActionNode.setStyle("display", "none");
                                },
                                "mousedown": function(){this.setStyles(_self.css.formTemplateNode_down)},
                                "mouseup": function(){this.setStyles(_self.css.formTemplateNode_over)},
                                "click": function(e){
                                    createForm(e, this.retrieve("template"));
                                    _self.app.removeEvent("resize", resize);
                                    createTemplateAreaNode.destroy();
                                    createTemplateMaskNode.destroy();
                                }
                            });
                        }.bind(this));
                    }
                }.bind(this));
            }.bind(this));
        }.bind(this);

        var loadAllTemplates = function(){
            createTemplateContentNode.empty();
            createTemplateCategoryNode.getElements("div").each(function(node, i){
                if (i>0) node.setStyles(_self.css.createTemplateFormCategoryItemNode);
            });
            createTemplateCategoryAllNode.setStyles(_self.css.createTemplateFormCategoryItemNode_current);
            loadDefaultTemplate();
            loadTemplates();
        };
        loadAllTemplates();

        createTemplateMaskNode.addEvent("click", function(){
            this.app.removeEvent("resize", resize);
            createTemplateAreaNode.destroy();
            createTemplateMaskNode.destroy();
        }.bind(this));

    },

    _loadItemDataList: function(callback){
        this.app.restActions.listReveal(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.query.QueryManager.RevealExplorer.Reveal(this, item);
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteReveal();
            }else{
                item.deleteReveal(function(){
                    //    this.reloadItems();
                    //this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.query.QueryManager.RevealExplorer.Reveal= new Class({
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
        this.explorer.app.desktop.openApplication(e, "query.SelectDesigner", options);
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/processIcon/lnk.png",
			"title": this.data.name,
			"par": "query.SelectDesigner#{\"id\": \""+this.data.id+"\"}"
		};
	},
	deletePage: function(callback){
		this.explorer.actions.deleteSelect(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});
