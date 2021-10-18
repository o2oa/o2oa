MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};
if( !MWF.xApplication.cms.ColumnManager.LP ){
    MWF.requireApp("cms.ColumnManager", "lp."+o2.language, null, false);
}
MWF.xApplication.cms.ColumnManager.CMSFormTemplateSelector = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options:{
        "style": "default"
    },
    initialize: function(app, options){
        this.setOptions(options);
        this.path = "../x_component_cms_ColumnManager/widget/";
        this.cssPath = "../x_component_cms_ColumnManager/widget/$CMSFormTemplateSelector/"+this.options.style+"/css.wcss";
        this._loadCss(true);

        this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.ColumnManager.Actions.RestActions()

        this.app = app;
        this.formTemplateList = null;
        this.defalutFormTemplateList = null;
    },
    load: function(e){
        var _self = this;

        this.createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        this.createTemplateAreaNode = new Element("div", {"styles": this.css.createFormTemplateAreaNode}).inject(this.app.content);
        this.createTemplateAreaNode.fade("in");

        this.createTemplateTitleNode = new Element("div", {"styles": this.css.createTemplateFormTitleNode, "text": this.app.lp.createSelectTemplate}).inject(this.createTemplateAreaNode);
        this.createTemplateCategoryNode = new Element("div", {"styles": this.css.createTemplateFormCategoryNode}).inject(this.createTemplateAreaNode);
        this.createTemplateCategoryTitleNode = new Element("div", {"styles": this.css.createTemplateFormCategoryTitleNode, "text": this.app.lp.templateCategory}).inject(this.createTemplateCategoryNode);

        this.createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateFormContentNode}).inject(this.createTemplateAreaNode);

        this.createTemplateCategoryAllNode = new Element("div", {"styles": this.css.createTemplateFormCategoryItemNode, "text": this.app.lp.all}).inject(this.createTemplateCategoryNode);
        this.createTemplateCategoryAllNode.addEvent("click", function(){
            _self.loadAllTemplates();
        });
        this.restActions.listFormTemplateCategory(function(json){
            json.data.each(function(d){
                var createTemplateCategoryItemNode = new Element("div", {
                    "styles": this.css.createTemplateFormCategoryItemNode, "text": d.name+"("+ d.count+")",
                    "value": d.name
                }).inject(this.createTemplateCategoryNode);
                createTemplateCategoryItemNode.addEvent("click", function(){
                    _self.createTemplateContentNode.empty();
                    _self.createTemplateCategoryNode.getElements("div").each(function(node, i){
                        if (i>0) node.setStyles(_self.css.createTemplateFormCategoryItemNode);
                    });
                    this.setStyles(_self.css.createTemplateFormCategoryItemNode_current);
                    _self.loadTemplates(this.get("value"));
                });
            }.bind(this));
        }.bind(this));

        this.resizeFun = this.resize.bind(this);
        this.resizeFun();
        this.app.addEvent("resize", this.resizeFun);

        this.loadAllTemplates();

        this.createTemplateMaskNode.addEvent("click", function(){
            this.app.removeEvent("resize", this.resizeFun);
            this.createTemplateAreaNode.destroy();
            this.createTemplateMaskNode.destroy();
        }.bind(this));

    },
    resize: function(){
        var size = this.app.content.getSize();
        var y = (size.y*0.1)/2;
        var x = (size.x*0.1)/2;
        if (y<0) y=0;
        if (x<0) x=0;
        this.createTemplateAreaNode.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });
        y = size.y*0.9-this.createTemplateCategoryNode.getSize().y-70;
        this.createTemplateContentNode.setStyle("height", ""+y+"px");
    },
    loadAllTemplates : function(){
        this.createTemplateContentNode.empty();
        this.createTemplateCategoryNode.getElements("div").each(function(node, i){
            if (i>0) node.setStyles(this.css.createTemplateFormCategoryItemNode);
        }.bind(this));
        this.createTemplateCategoryAllNode.setStyles(this.css.createTemplateFormCategoryItemNode_current);
        this.loadDefaultTemplate();
        this.loadTemplates();
    },
    getDefaultFormTemplateList: function(callback){
        if (this.defalutFormTemplateList){
            if (callback) callback();
        }else{
            var url = "../x_component_cms_FormDesigner/Module/Form/template/templates.json";
            MWF.getJSON(url, function(json){
                this.defalutFormTemplateList = json;
                if (callback) callback();
            }.bind(this));
        }
    },
    getFormTemplateList: function(callback){
        if (this.formTemplateList){
            if (callback) callback();
        }else{
            this.restActions.listFormTemplate(function(json){
                this.formTemplateList = json.data;
                if (callback) callback();
            }.bind(this));
        }
    },
    loadDefaultTemplate: function(){
        var _self = this;
        this.getDefaultFormTemplateList(function(){
            this.defalutFormTemplateList.each(function(template){
                var templateNode = new Element("div", {"styles": this.css.formTemplateNode}).inject(_self.createTemplateContentNode);
                var templateIconNode = new Element("div", {"styles": this.css.formTemplateIconNode}).inject(templateNode);
                var templateTitleNode = new Element("div", {"styles": this.css.formTemplateTitleNode, "text": template.title}).inject(templateNode);
                templateNode.store("template", template.name);
                templateNode.store("templateTitle", template.title);

                var templateIconImgNode = new Element("img", {"styles": this.css.formTemplateIconImgNode}).inject(templateIconNode);
                templateIconImgNode.set("src", "../x_component_cms_FormDesigner/Module/Form/template/"+template.icon);

                templateNode.addEvents({
                    "mouseover": function(){this.setStyles(_self.css.formTemplateNode_over)},
                    "mouseout": function(){this.setStyles(_self.css.formTemplateNode)},
                    "mousedown": function(){this.setStyles(_self.css.formTemplateNode_down)},
                    "mouseup": function(){this.setStyles(_self.css.formTemplateNode_over)},
                    "click": function(e){
                        debugger;
                        // createDefaultForm(e, this.retrieve("template"));
                        _self.fireEvent("selectDefaultForm", [this.retrieve("template"), this.retrieve("templateTitle")]);
                        _self.app.removeEvent("resize", _self.resizeFun);
                        _self.createTemplateAreaNode.destroy();
                        _self.createTemplateMaskNode.destroy();
                    }
                });
            }.bind(this))
        }.bind(this));
    },
    loadTemplates: function(category){
        var _self = this;
        this.getFormTemplateList(function(){
            Object.each(this.formTemplateList, function(v, k){
                var flag = (category) ? (k==category) : true;
                if (flag){
                    v.each(function(template){
                        var templateNode = new Element("div", {"styles": this.css.formTemplateNode}).inject(this.createTemplateContentNode);
                        var templateIconNode = new Element("div", {"styles": this.css.formTemplatePreviewNode}).inject(templateNode);
                        var templateTitleNode = new Element("div", {"styles": this.css.formTemplateTitleNode, "text": template.name}).inject(templateNode);
                        templateNode.store("template", template.id);
                        templateNode.store("templateTitle", template.name);

                        templateIconNode.set("html", template.outline);

                        var templateActionNode = new Element("img", {"styles": this.css.formTemplateActionNode}).inject(templateIconNode);
                        templateActionNode.addEvent("click", function(e){
                            var thisNode = this.getParent().getParent();
                            var id = thisNode.retrieve("template");
                            _self.app.confirm("wram", e, _self.app.lp.form.deleteFormTemplateTitle, _self.app.lp.form.deleteFormTemplate, 300, 120, function(){
                                _self.restActions.deleteFormTemplate(id, function(json){
                                    //thisNode.destroy();
                                    _self.app.removeEvent("resize", _self.resizeFun);
                                    _self.createTemplateAreaNode.destroy();
                                    _self.createTemplateMaskNode.destroy();
                                    _self.load(e)
                                }.bind(this));
                                this.close();
                            }, function(){
                                this.close();
                            });
                            e.stopPropagation();
                        });
                        //templateIconImgNode.set("src", "../x_component_process_FormDesigner/Module/Form/template/"+template.icon);

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
                                debugger;
                                // _self.createForm(e, this.retrieve("template"));
                                _self.fireEvent("selectForm", [this.retrieve("template"), this.retrieve("templateTitle")]);
                                _self.app.removeEvent("resize", _self.resizeFun);
                                _self.createTemplateAreaNode.destroy();
                                _self.createTemplateMaskNode.destroy();
                            }
                        });
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    }
});