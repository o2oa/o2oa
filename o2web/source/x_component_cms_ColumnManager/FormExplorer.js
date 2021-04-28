MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);

MWF.xApplication.cms.ColumnManager.FormExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.CMSCM.LP.form.create,
        "search": MWF.CMSCM.LP.form.search,
        "searchText": MWF.CMSCM.LP.form.searchText,
        "noElement": MWF.CMSCM.LP.form.noFormNoticeText
    },
    //
    //_createElement: function(e){
    //    var _self = this;
    //    var createForm = function(e, template){
    //        layout.desktop.getFormDesignerStyle(function(){
    //            var options = {
    //                "style": layout.desktop.formDesignerStyle,
    //                "template": template,
    //                "onQueryLoad": function(){
    //                    this.actions = _self.app.restActions;
    //                    this.column = _self.app.options.column;
    //                    this.application = _self.app.options.column;
    //                },
    //                "onPostSave" : function(){
    //                    _self.reload();
    //                }
    //            };
    //            layout.desktop.openApplication(e, "cms.FormDesigner", options);
    //        }.bind(this));
    //
    //    }
    //
    //    var createTemplateMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
    //    var createTemplateAreaNode = new Element("div", {"styles": this.css.createFormTemplateAreaNode}).inject(this.app.content);
    //    createTemplateAreaNode.fade("in");
    //
    //    var createTemplateScrollNode = new Element("div", {"styles": this.css.createTemplateScrollNode}).inject(createTemplateAreaNode);
    //    var createTemplateContentNode = new Element("div", {"styles": this.css.createTemplateContentNode}).inject(createTemplateScrollNode);
    //    MWF.require("MWF.widget.ScrollBar", function(){
    //        new MWF.widget.ScrollBar(createTemplateScrollNode, {"indent": false});
    //    }.bind(this));
    //
    //    var _self = this;
    //    var url = "../x_component_cms_FormDesigner/Module/Form/template/templates.json";
    //    MWF.getJSON(url, function(json){
    //        json.each(function(template){
    //            var templateNode = new Element("div", {"styles": this.css.templateNode}).inject(createTemplateContentNode);
    //            var templateIconNode = new Element("div", {"styles": this.css.templateIconNode}).inject(templateNode);
    //            var templateTitleNode = new Element("div", {"styles": this.css.templateTitleNode, "text": template.title}).inject(templateNode);
    //            templateNode.store("template", template.name);
    //
    //            var templateIconImgNode = new Element("img", {"styles": this.css.templateIconImgNode}).inject(templateIconNode);
    //            templateIconImgNode.set("src", "../x_component_cms_FormDesigner/Module/Form/template/"+template.icon);
    //
    //            templateNode.addEvents({
    //                "mouseover": function(){this.setStyles(_self.css.templateNode_over)},
    //                "mouseout": function(){this.setStyles(_self.css.templateNode)},
    //                "mousedown": function(){this.setStyles(_self.css.templateNode_down)},
    //                "mouseup": function(){this.setStyles(_self.css.templateNode_over)},
    //                "click": function(e){
    //                    createForm(e, this.retrieve("template"));
    //                    createTemplateAreaNode.destroy();
    //                    createTemplateMaskNode.destroy();
    //                }
    //            });
    //
    //        }.bind(this))
    //
    //    }.bind(this));
    //
    //    createTemplateMaskNode.addEvent("click", function(){
    //        createTemplateAreaNode.destroy();
    //        createTemplateMaskNode.destroy();
    //    });
    //
    //    var size = this.app.content.getSize();
    //    var y = (size.y - 262)/2;
    //    var x = (size.x - 828)/2;
    //    if (y<0) y=0;
    //    if (x<0) x=0;
    //    createTemplateAreaNode.setStyles({
    //        "top": ""+y+"px",
    //        "left": ""+x+"px"
    //    });
    //
    //},


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
                    },
                    "onPostSave" : function () {
                        _self.reload();
                    }
                };
                layout.desktop.openApplication(e, "cms.FormDesigner", options);
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
                    },
                    "onPostSave" : function () {
                        _self.reload();
                    }
                };
                layout.desktop.openApplication(e, "cms.FormDesigner", options);
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
                var url = "../x_component_cms_FormDesigner/Module/Form/template/templates.json";
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
                    templateIconImgNode.set("src", "../x_component_cms_FormDesigner/Module/Form/template/"+template.icon);

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
                                        //thisNode.destroy();
                                        _self.app.removeEvent("resize", resize);
                                        createTemplateAreaNode.destroy();
                                        createTemplateMaskNode.destroy();
                                        _self._createElement(e)
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
        return new MWF.xApplication.cms.ColumnManager.FormExplorer.Form(this, item, {index:index})
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
    },

    keyCopy: function(e){
        if (this.selectMarkItems.length){
            var items = [];
            var i = 0;

            var checkItems = function(e){
                if (i>=this.selectMarkItems.length){
                    if (items.length){
                        var str = JSON.encode(items);
                        if (e){
                            e.clipboardData.setData('text/plain', str);
                        }else {
                            window.clipboardData.setData("Text", str);
                        }
                        this.app.notice(this.app.lp.copyed, "success");
                    }
                }
            }.bind(this);

            this.selectMarkItems.each(function(item){
                this.app.restActions.getForm(item.data.id, function(json){

                    //this.app.restActions.listFormFieldWithForm(item.data.id, function(fieldJson){
                    //    var fieldList = [];
                    //    fieldJson.data.number.each(function(f){ fieldList.push({"dataType": "number", "name": f.name}) });
                    //    fieldJson.data.person.each(function(f){ fieldList.push({"dataType": "person", "name": f.name}) });
                    //    fieldJson.data.string.each(function(f){ fieldList.push({"dataType": "string", "name": f.name}) });
                    //
                    //    json.data.fieldList = fieldList;
                    //}.bind(this), null, false);

                    json.data.elementType = "form";
                    items.push(json.data);
                    i++;
                    checkItems(e);
                }.bind(this), null, false)
            }.bind(this));
        }
    },
    keyPaste: function(e){
        var dataStr = "";
        if (e){
            dataStr = e.clipboardData.getData('text/plain');
        }else{
            dataStr = window.clipboardData.getData("Text");
        }
        var data = JSON.decode(dataStr);
        this.pasteItem(data, 0);
    },
    pasteItem: function(data, i){
        if (i<data.length){
            var item = data[i];
            if (item.elementType==="form"){
                this.saveItemAs(item, function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    i++;
                    this.pasteItem(data, i);
                }.bind(this), function(){
                    this.reload();
                }.bind(this));
            }else{
                i++;
                this.pasteItem(data, i);
            }
        }else{
            this.reload();
        }
    },
    saveItemAs: function(data, success, failure, cancel){
        this.app.restActions.listForm(this.app.options.application.id, function(dJson){
            dJson.data = dJson.data || [];
            var i=1;
            var someItems = dJson.data.filter(function(d){ return d.id===data.id });
            if (someItems.length){
                var someItem = someItems[0];
                var lp = this.app.lp;
                var _self = this;

                var d1 = new Date().parse(data.updateTime);
                var d2 = new Date().parse(someItem.updateTime);
                var html = "<div>"+lp.copyConfirmInfor+"</div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='font-weight: bold; font-size:14px;'>"+lp.copySource+" "+someItem.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left'>"+someItem.updateTime+"</div>" +
                    //"<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(someItem.lastUpdatePerson)+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1>=d2) ? "": lp.copynew)+"</div></div>";
                html += "<div style='overflow: hidden; margin: 10px 0px; padding: 5px 10px; background-color: #ffffff; border-radius: 6px;'><div style='clear: both;font-weight: bold; font-size:14px;'>"+lp.copyTarget+" "+data.name+"</div>";
                html += "<div style='font-size:12px; color: #666666; float: left;'>"+data.updateTime+"</div>" +
                    //"<div style='font-size:12px; color: #666666; float: left; margin-left: 20px;'>"+MWF.name.cn(data.lastUpdatePerson)+"</div>" +
                    "<div style='color: red; float: right;'>"+((d1<=d2) ? "": lp.copynew)+"</div></div>";
//                html += "<>"
                this.app.dlg("inofr", null, this.app.lp.copyConfirmTitle, {"html": html}, 500, 290, [
                    {
                        "text": lp.copyConfirm_overwrite,
                        "action": function(){_self.saveItemAsUpdate(someItem, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_new,
                        "action": function(){_self.saveItemAsNew(dJson, data, success, failure);this.close();}
                    },
                    {
                        "text": lp.copyConfirm_skip,
                        "action": function(){/*nothing*/ this.close(); if (success) success();}
                    },
                    {
                        "text": lp.copyConfirm_cancel,
                        "action": function(){this.close(); if (cancel) cancel();}
                    }
                ]);
            }else{
                this.saveItemAsNew(dJson, data, success, failure)
            }
        }.bind(this), function(){if (failure) failure();}.bind(this));
    },
    saveItemAsUpdate: function(someItem, form, success, failure){
        var item = this.app.options.application;

        var pcdata = JSON.decode(MWF.decodeJsonString(form.data));
        var mobiledata = JSON.decode(MWF.decodeJsonString(form.mobileData));

        pcdata.id = someItem.id;
        pcdata.isNewForm = false;
        pcdata.json.id = someItem.id;
        pcdata.json.application = item.id;
        pcdata.json.applicationName = item.name;
        pcdata.json.name = someItem.name;
        pcdata.json.alias = someItem.alias;
        pcdata.json.appId = item.id;
        pcdata.json.appName = item.name;


        mobiledata.json.id = someItem.id;
        mobiledata.json.application = item.id;
        mobiledata.json.applicationName = item.name;
        mobiledata.applicationName = item.name;
        mobiledata.json.name = someItem.name;
        mobiledata.json.alias = someItem.alias;
        mobiledata.json.appId = item.id;
        mobiledata.json.appName = item.name;

        this.app.restActions.saveForm(pcdata, mobiledata, form.fieldList, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    },
    saveItemAsNew: function(formsJson, form, success, failure){
        var item = this.app.options.application;
        var id = item.id;
        var name = item.name;

        var pcdata = JSON.decode(MWF.decodeJsonString(form.data));
        var mobiledata = JSON.decode(MWF.decodeJsonString(form.mobileData));

        var oldName = pcdata.json.name;

        var i=1;
        while (formsJson.data.some(function(d){ return d.name==pcdata.json.name })){
            pcdata.json.name = oldName+"_copy"+i;
            mobiledata.json.name = oldName+"_copy"+i;
            i++;
        }
        pcdata.id = "";
        pcdata.isNewForm = true;
        pcdata.json.id = "";
        pcdata.json.application = id;
        pcdata.json.applicationName = name;
        pcdata.json.appId = id;
        pcdata.json.appName = name;
        pcdata.json.alias = "";

        mobiledata.json.id = "";
        mobiledata.json.application = id;
        mobiledata.json.applicationName = name;
        mobiledata.applicationName = name;
        mobiledata.json.appId = id;
        mobiledata.json.appName = name;
        mobiledata.json.alias = "";

        this.app.restActions.saveForm(pcdata, mobiledata, form.fieldList, function(){
            if (success) success();
        }.bind(this), function(){
            if (failure) failure();
        }.bind(this));
    }
});

MWF.xApplication.cms.ColumnManager.FormExplorer.Form = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer.Item,
	_open: function(e){
        layout.desktop.getFormDesignerStyle(function(){
            var _self = this;
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "appId": "cms.FormDesigner"+_self.data.id,
                "id": _self.data.id,
                "application": _self.explorer.app.options.column.id,
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
		this.explorer.app.restActions.removeForm(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},
    saveItemAs: function(item) {
        var id = item.id;
        var name = item.name || item.appName;
        this.explorer.app.restActions.getForm(this.data.id, function (json) {
            var pcdata = JSON.decode(MWF.decodeJsonString(json.data.data));
            var mobiledata = JSON.decode(MWF.decodeJsonString(json.data.mobileData));
            pcdata.json.alias = "";
            mobiledata.json.alias = "";
            var oldName = pcdata.json.name;
            this.explorer.app.restActions.listForm(id, function (formsJson) {
                formsJson.data = formsJson.data || [];
                var i = 1;
                while (formsJson.data.some(function (d) {
                    return d.name == pcdata.json.name
                })) {
                    pcdata.json.name = oldName + "_copy" + i;
                    mobiledata.json.name = oldName + "_copy" + i;
                    i++;
                }
                pcdata.id = "";
                pcdata.isNewForm = true;
                pcdata.json.id = "";
                pcdata.json.application = id;
                pcdata.json.applicationName = name;
                pcdata.json.appId = id;
                pcdata.json.appName = name;
                mobiledata.json.id = "";
                mobiledata.json.application = id;
                mobiledata.applicationName = name;
                mobiledata.json.applicationName = name;
                mobiledata.json.appId = id;
                mobiledata.json.appName = name;

                //this.explorer.app.restActions.listFormFieldWithForm(this.data.id, function(fieldJson){
                //    var fieldList = [];
                //    if (fieldJson.data.number) fieldJson.data.number.each(function(f){ fieldList.push({"dataType": "number", "name": f.name}) });
                //    if (fieldJson.data.person) fieldJson.data.person.each(function(f){ fieldList.push({"dataType": "person", "name": f.name}) });
                //    if (fieldJson.data.string) fieldJson.data.string.each(function(f){ fieldList.push({"dataType": "string", "name": f.name}) });

                var fieldList = [];
                this.explorer.app.restActions.saveForm(pcdata, mobiledata, fieldList, function () {
                    if (id == this.explorer.app.options.application.id) this.explorer.reload();
                }.bind(this));
                //}.bind(this));
            }.bind(this));
        }.bind(this));
    }
});
