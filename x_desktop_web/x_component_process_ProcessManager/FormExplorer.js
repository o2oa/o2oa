MWF.xDesktop.requireApp("process.ProcessManager", "Explorer", null, false);
MWF.xApplication.process.ProcessManager.FormExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.form.create,
        "search": MWF.APPPM.LP.form.search,
        "searchText": MWF.APPPM.LP.form.searchText,
        "noElement": MWF.APPPM.LP.form.noFormNoticeText
    },

    _createElement: function(e){
        this.formTemplateList = null;
        this.defalutFormTemplateList = null;
        var _self = this;
        var createDefaultForm = function(e, template){
            layout.desktop.getFormDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "template": template,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.application = _self.app.options.application;
                    }
                };
                layout.desktop.openApplication(e, "process.FormDesigner", options);
            }.bind(this));
        };
        var createForm = function(e, template){
            layout.desktop.getFormDesignerStyle(function(){
                var options = {
                    "style": layout.desktop.formDesignerStyle,
                    "templateId": template,
                    "onQueryLoad": function(){
                        this.actions = _self.app.restActions;
                        this.application = _self.app.options.application;
                    }
                };
                layout.desktop.openApplication(e, "process.FormDesigner", options);
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
        this.app.restActions.listFormTemplateCategory(function(json){
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

        var getDefaultFormTemplateList = function(callback){
            if (this.defalutFormTemplateList){
                if (callback) callback();
            }else{
                var url = "/x_component_process_FormDesigner/Module/Form/template/templates.json";
                MWF.getJSON(url, function(json){
                    this.defalutFormTemplateList = json;
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this);
        var loadDefaultTemplate = function(){
            getDefaultFormTemplateList(function(){
                this.defalutFormTemplateList.each(function(template){
                    var templateNode = new Element("div", {"styles": this.css.formTemplateNode}).inject(createTemplateContentNode);
                    var templateIconNode = new Element("div", {"styles": this.css.formTemplateIconNode}).inject(templateNode);
                    var templateTitleNode = new Element("div", {"styles": this.css.formTemplateTitleNode, "text": template.title}).inject(templateNode);
                    templateNode.store("template", template.name);

                    var templateIconImgNode = new Element("img", {"styles": this.css.formTemplateIconImgNode}).inject(templateIconNode);
                    templateIconImgNode.set("src", "/x_component_process_FormDesigner/Module/Form/template/"+template.icon);

                    templateNode.addEvents({
                        "mouseover": function(){this.setStyles(_self.css.formTemplateNode_over)},
                        "mouseout": function(){this.setStyles(_self.css.formTemplateNode)},
                        "mousedown": function(){this.setStyles(_self.css.formTemplateNode_down)},
                        "mouseup": function(){this.setStyles(_self.css.formTemplateNode_over)},
                        "click": function(e){
                            createDefaultForm(e, this.retrieve("template"));
                            _self.app.removeEvent("resize", resize);
                            createTemplateAreaNode.destroy();
                            createTemplateMaskNode.destroy();
                        }
                    });
                }.bind(this))
            }.bind(this));
        }.bind(this);

        var getFormTemplateList = function(callback){
            if (this.formTemplateList){
                if (callback) callback();
            }else{
                this.app.restActions.listFormTemplate(function(json){
                    this.formTemplateList = json.data;
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this);
        var loadTemplates = function(category){
            getFormTemplateList(function(){
                Object.each(this.formTemplateList, function(v, k){
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
                                _self.app.confirm("wram", e, _self.app.lp.form.deleteFormTemplateTitle, _self.app.lp.form.deleteFormTemplate, 300, 120, function(){
                                    _self.app.restActions.deleteFormTemplate(id, function(json){
                                        thisNode.destroy();
                                    }.bind(this));
                                    this.close();
                                }, function(){
                                    this.close();
                                });
                                e.stopPropagation();
                            });
                            //templateIconImgNode.set("src", "/x_component_process_FormDesigner/Module/Form/template/"+template.icon);

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
        this.app.restActions.listForm(this.app.options.application.id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.FormExplorer.Form(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.form.create,
            "search": MWF.APPPM.LP.form.search,
            "searchText": MWF.APPPM.LP.form.searchText,
            "noElement": MWF.APPPM.LP.form.noFormNoticeText
        };
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteForm();
            }else{
                item.deleteForm(function(){
                //    this.reloadItems();
                //    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.FormExplorer.Form = new Class({
	Extends: MWF.xApplication.process.ProcessManager.Explorer.Item,
	_open: function(e){
        layout.desktop.getFormDesignerStyle(function(){
            var _self = this;
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "onQueryLoad": function(){
                    this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = _self.data.id;
                    this.application = _self.explorer.app.options.application;
                }
            };
            this.explorer.app.desktop.openApplication(e, "process.FormDesigner", options);
        }.bind(this));
	},
	_getIcon: function(){
		var x = (Math.random()*49).toInt();
		return "process_icon_"+x+".png";
	},
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/formIcon/lnk.png",
			"title": this.data.name,
			"par": "process.FormDesigner#{\"id\": \""+this.data.id+"\"}"
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
