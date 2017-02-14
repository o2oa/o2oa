MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);
MWF.xApplication.process.FormDesigner.Property = MWF.FCProperty = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": "/x_component_process_FormDesigner/property/property.html"
	},
	
	initialize: function(module, propertyNode, designer, options){
		this.setOptions(options);
		this.module = module;
		this.form = module.form;
		this.data = module.json;
		this.htmlPath = this.options.path;
		this.designer = designer;
		this.maplists = {};
		this.propertyNode = propertyNode;
	},

	load: function(){
		if (this.fireEvent("queryLoad")){
			MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
				this.htmlString = responseText;
				this.fireEvent("postLoad");
			}.bind(this));
		}
        this.propertyNode.addEvent("keydown", function(e){e.stopPropagation();});
	},
	editProperty: function(td){
	},
    getHtmlString: function(callback){
        if (!this.htmlString){
            MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                this.htmlString = responseText;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	show: function(){
        if (!this.propertyContent){
            this.getHtmlString(function(){
                if (this.htmlString){
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                    this.propertyContent.set("html", this.JsonTemplate.load());

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadMaplist();
                    this.loadStylesList();
                    this.loadDivTemplateType();
                    this.loadPersonInput();
                    this.loadFormFieldInput();
                    this.loadScriptArea();
                    this.loadHtmlEditorArea();
                    this.loadTreeData();
                    this.loadArrayList();
                    this.loadEventsEditor();
                    this.loadActionArea();
                    this.loadHTMLArea();
                    this.loadJSONArea();
                    this.loadFormSelect();
                    this.loadViewSelect();
                    this.loadValidation();
                    this.loadIconSelect();
//			this.loadScriptInput();
                    //MWF.process.widget.EventsEditor
                }
            }.bind(this));
        }else{
            this.propertyContent.setStyle("display", "block");
        }

        (new Fx.Scroll(layout.desktop.node)).toTop();

	},
	hide: function(){
		//this.JsonTemplate = null;
		//this.propertyNode.set("html", "");
        if (this.propertyContent) this.propertyContent.setStyle("display", "none");
	},
	
	loadTreeData: function(){
		var arrays = this.propertyContent.getElements(".MWFTreeData");
		arrays.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var json = this.data[name];
			if (!json) json = [];
			MWF.require("MWF.widget.TreeEditor", function(){
				var treeEditor = new MWF.widget.TreeEditor(node, {
					"title": title,
					"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
					"onChange": function(){
						this.data[name] = treeEditor.toJson();
						this.module.json[name] = this.data[name];

						this.module._refreshTree();
					}.bind(this)
				});
				treeEditor.load(json);
			}.bind(this));
            node.addEvent("keydown", function(e){e.stopPropagation();});
		}.bind(this));
	},

	loadJSONArea: function(){
		var jsonNode = this.propertyContent.getElement(".MWFJSONArea");

        if (jsonNode){
            this.propertyTab.pages.each(function(page){
                if (page.contentNode == jsonNode.parentElement){
                    page.setOptions({
                        "onShow": function(){
                            jsonNode.empty();
                            MWF.require("MWF.widget.JsonParse", function(){
                                this.json = new MWF.widget.JsonParse(this.module.json, jsonNode, null);
                                this.json.load();
                            }.bind(this));
                        }.bind(this)
                    });
                }
            }.bind(this));
        }
	},
	loadHTMLArea: function(){
		var htmlNode = this.propertyContent.getElement(".MWFHTMLArea");
		if (htmlNode){
			var copy = this.module.node.clone(true, true);
			copy.clearStyles(true);
			htmlNode.set("text", copy.outerHTML);
			copy.destroy();

			this.propertyTab.pages.each(function(page){
				if (page.contentNode == htmlNode.parentElement){
					page.setOptions({
						"onShow": function(){
							var copy = this.module.node.clone(true, true);
							copy.clearStyles(true);
							htmlNode.set("text", copy.outerHTML);
							copy.destroy();
						}.bind(this)
					});
				}
			}.bind(this));
		}
	},
    loadFormSelect: function(){
        var formNodes = this.propertyContent.getElements(".MWFFormSelect");
        if (formNodes.length){
            this.getFormList(function(){
                formNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        this.setValue(e.target.getParent("div").get("name"), e.target.options[e.target.selectedIndex].value);
                    }.bind(this));
                    this.setFormSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getFormList(function(){
                            this.setFormSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                    //select.addEvent("click", function(e){
                    //    this.setFormSelectOptions(node, select);
                    //}.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setFormSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.forms.each(function(form){
            var option = new Element("option", {
                "text": form.name,
                "value": form.id,
                "selected": (this.data[name]==form.id)
            }).inject(select);
        }.bind(this));
    },
    getFormList: function(callback, refresh){
        if (!this.forms || refresh){
            this.form.designer.actions.listForm(this.form.designer.application.id, function(json){
                this.forms = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
//	clearStyles: function(node){
//		node.removeProperty("style");
//		var subNode = node.getFirst();
//		while (subNode){
//			this.clearStyles(subNode);
//			subNode = subNode.getNext();
//		}
//	},
    loadViewSelect: function(){
        var viewNodes = this.propertyContent.getElements(".MWFViewSelect");
        if (viewNodes.length){
            this.getViewList(function(){
                viewNodes.each(function(node){
                    var select = new Element("select").inject(node);
                    select.addEvent("change", function(e){
                        var viewId = e.target.options[e.target.selectedIndex].value;
                        var viewName = e.target.options[e.target.selectedIndex].get("text");
                        this.setValue(e.target.getParent("div").get("name"), viewId);
                        this.setValue(e.target.getParent("div").get("name")+"Name", viewName);
                    }.bind(this));
                    this.setViewSelectOptions(node, select);

                    var refreshNode = new Element("div", {"styles": this.form.css.propertyRefreshFormNode}).inject(node);
                    refreshNode.addEvent("click", function(e){
                        this.getViewList(function(){
                            this.setViewSelectOptions(node, select);
                        }.bind(this), true);
                    }.bind(this));
                    //select.addEvent("click", function(e){
                    //    this.setFormSelectOptions(node, select);
                    //}.bind(this));
                }.bind(this));
            }.bind(this));
        }
    },
    setViewSelectOptions: function(node, select){
        var name = node.get("name");
        select.empty();
        var option = new Element("option", {"text": "none"}).inject(select);
        this.views.each(function(view){
            var option = new Element("option", {
                "text": view.name,
                "value": view.id,
                "selected": (this.data[name]==view.id)
            }).inject(select);
        }.bind(this));
    },
    getViewList: function(callback, refresh){
        if (!this.views || refresh){
            this.form.designer.actions.listView(this.form.designer.application.id, function(json){
                this.views = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    loadValidation: function(){
        var nodes = this.propertyContent.getElements(".MWFValidation");
        if (nodes.length){
            nodes.each(function(node){
                var name = node.get("name");
                MWF.xDesktop.requireApp("process.FormDesigner", "widget.ValidationEditor", function(){
                    var validationEditor = new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer, {
                        "onChange": function(){
                            var data = validationEditor.getValidationData();
                            this.data[name] = data;
                        }.bind(this)
                    });
                    validationEditor.load(this.data[name])
                }.bind(this));

                //new MWF.xApplication.process.FormDesigner.widget.ValidationEditor(node, this.designer);
            }.bind(this));
        }
    },

    loadIconSelect: function(){
        var nodes = this.propertyContent.getElements(".MWFIcon");
        if (nodes.length){
            nodes.each(function(node){
                var id = node.get("name");
                var icon = this.data[id];
                var iconNode = new Element("div", {"styles": this.form.css.processIconNode}).inject(node);
                if (icon) iconNode.setStyles({"background": "url("+icon+") center center no-repeat"});
                var selectNode = new Element("div", {"styles": this.form.css.processIconSelectNode, "text": this.form.designer.lp.selectIcon}).inject(node);
                selectNode.addEvent("click", function(){
                    this.selectIcon(node);
                }.bind(this));


            }.bind(this));
        }
    },
    selectIcon: function(node){
        if (!node.iconMenu){
            var iconSelectMenu = new MWF.widget.Menu(node, {"event": "click", "style": "processIcon"});
            iconSelectMenu.load();
            node.iconMenu = iconSelectMenu;
            var _self = this;
            for (var i=0; i<=48; i++){
                var icon = "/x_component_process_ProcessManager/$Explorer/default/processIcon/process_icon_"+i+".png";
                var item = iconSelectMenu.addMenuItem("", "click", function(){
                    var id = node.get("name");
                    var src = this.item.getElement("img").get("src");

                    _self.data[id] = src;
                    node.getFirst("div").setStyle("background-image", "url("+src+")");
                }, icon);
                item.iconName = icon;
            }
        }
    },
	
	loadEventsEditor: function(){
		var events = this.propertyContent.getElement(".MWFEventsArea");
		if (events){
			var name = events.get("name");
			var eventsObj = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", function(){
				var eventsEditor = new MWF.xApplication.process.FormDesigner.widget.EventsEditor(events, this.designer, {
					//"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode
				});
				eventsEditor.load(eventsObj);
			}.bind(this));
		}
	},
	
	loadArrayList: function(){
		var arrays = this.propertyContent.getElements(".MWFArraylist");
		arrays.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var arr = this.data[name];
			if (!arr) arr = [];
			MWF.require("MWF.widget.Arraylist", function(){
				var arraylist = new MWF.widget.Arraylist(node, {
					"title": title,
					"onChange": function(){
						this.data[name] = arraylist.toArray();
					}.bind(this)
				});
				arraylist.load(arr);
			}.bind(this));
            node.addEvent("keydown", function(e){e.stopPropagation();});
		}.bind(this));
	},

    loadHtmlEditorArea: function(){
        var htmlAreas = this.propertyContent.getElements(".MWFHtmlEditorArea");
        htmlAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            var scriptContent = this.data[name];
            MWF.require("MWF.widget.HtmlEditorArea", function(){
                var htmlArea = new MWF.widget.HtmlEditorArea(node, {
                    "title": title,
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode,
                    "onChange": function(){
                        this.data[name] = htmlArea.getValue();
                        this.changeData(name);
                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this)
                });
                htmlArea.load({"code": scriptContent});
            }.bind(this));

        }.bind(this));
    },
    loadStylesList: function(){
        var styleSelNodes = this.propertyContent.getElements(".MWFFormStyle");
        styleSelNodes.each(function(node){
            if (this.module.form.stylesList){
                if (!this.data.formStyleType) this.data.formStyleType = "default";
                Object.each(this.module.form.stylesList, function(s, key){
                    new Element("option", {
                        "text": s.name,
                        "value": key,
                        "selected": ((!this.data.formStyleType && key=="default") || (this.data.formStyleType==key))
                    }).inject(node)
                }.bind(this));
            }else{
                node.getParent("tr").setStyle("display", "none");
            }
        }.bind(this));
    },
    loadDivTemplateType: function(){
        var nodes = this.propertyContent.getElements(".MWFDivTemplate");
        if (nodes.length){
            var keys = [];
            if (this.module.form.stylesList) {
                if (this.module.form.stylesList[this.module.form.json.formStyleType]){
                    var styles = this.module.form.stylesList[this.module.form.json.formStyleType][this.module.moduleName];
                    if (styles) {
                        Object.each(styles, function (v, k) {
                            keys.push(k);
                        }.bind(this));
                    }
                }
            }

            nodes.each(function(node){
                node.empty();
                new Element("option", {
                    "text": "default",
                    "value": "default",
                    "selected": (!this.data.templateType || this.data.templateType=="default")
                }).inject(node);
                if (keys.length){
                    keys.each(function(k){
                        new Element("option", {
                            "text": styles[k].name,
                            "value": k,
                            "selected": (this.data.templateType==k)
                        }).inject(node)
                    }.bind(this));
                }else{
                    node.getParent("tr").setStyle("display", "none");
                }
            }.bind(this));
        }

    },
    loadPersonInput: function(){
        var personIdentityNodes = this.propertyContent.getElements(".MWFPersonIdentity");
        var personDepartmentNodes = this.propertyContent.getElements(".MWFPersonDepartment");
        var personCompanyNodes = this.propertyContent.getElements(".MWFPersonCompany");
        var dutyNodes = this.propertyContent.getElements(".MWFDutySelector");
        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            personIdentityNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "identity",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));

            personDepartmentNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "department",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
            personCompanyNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "company",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
            dutyNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "duty",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.addDutyItem(node, ids);}.bind(this),
                    "onRemoveDuty": function(item){this.removeDutyItem(node, item);}.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    removeDutyItem: function(node, item){
        if (item.data.id){
            var values = this.data[node.get("name")] || [];
            var value = values.filter(function(v){
                return v.id == item.data.id;
            });
            value.each(function(v) {
                values = values.erase(v);
            });
            this.data[node.get("name")] = values;
        }
        item.node.destroy();
        MWF.release(item);
        delete item;
    },
    addDutyItem: function(node, ids){
        var value = this.data[node.get("name")] || "";
        if (!value) value = "[]";
        var values = JSON.decode(value);
        ids.each(function(id){
            if (id.data.id){
                for (var i=0; i<values.length; i++){
                    if (values[i].id==id.data.id){
                        values[i].name = id.data.name;
                        values[i].code = id.data.code;
                        break;
                    }
                }
            }else{
                id.data.id = new MWF.widget.UUID().toString();
                values.push({"name": id.data.name, "id": id.data.id, "code": id.data.code});
            }
        }.bind(this));
        this.data[node.get("name")] = JSON.encode(values);
    },
    loadFormFieldInput: function(){
        var fieldNodes = this.propertyContent.getElements(".MWFFormFieldPerson");
        MWF.xDesktop.requireApp("process.ViewDesigner", "widget.PersonSelector", function(){
            fieldNodes.each(function(node){
                new MWF.xApplication.process.ViewDesigner.widget.PersonSelector(node, this.form.designer, {
                    "type": "formField",
                    "application": this.form.json.application,
                    "fieldType": "person",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    savePersonItem: function(node, ids){
        var values = [];
        ids.each(function(id){
            values.push(id.data.name);
        }.bind(this));
        this.data[node.get("name")] = values;
    },

	loadScriptArea: function(){
		var scriptAreas = this.propertyContent.getElements(".MWFScriptArea");
        var formulaAreas = this.propertyContent.getElements(".MWFFormulaArea");
        this.loadScriptEditor(scriptAreas);
        this.loadScriptEditor(formulaAreas, "formula");
	},
    loadScriptEditor: function(scriptAreas, style){
        scriptAreas.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            var scriptContent = this.data[name];

            MWF.require("MWF.widget.ScriptArea", function(){
                var scriptArea = new MWF.widget.ScriptArea(node, {
                    "title": title,
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.formContentNode,
                    "onChange": function(){
                        this.data[name] = scriptArea.toJson();
                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveForm();
                    }.bind(this),
                    "style": style || "default"
                });
                scriptArea.load(scriptContent);
            }.bind(this));

        }.bind(this));
    },

    loadActionArea: function(){
        var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        actionAreas.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name];

            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));
    },


	loadMaplist: function(){
		var maplists = this.propertyContent.getElements(".MWFMaplist");
		maplists.each(function(node){
			var title = node.get("title");
			var name = node.get("name");
			var collapse = node.get("collapse");
			var mapObj = this.data[name];
			if (!mapObj) mapObj = {};
			MWF.require("MWF.widget.Maplist", function(){
				var maplist = new MWF.widget.Maplist(node, {
					"title": title,
					"collapse": (collapse) ? true : false,
					"onChange": function(){
						//this.data[name] = maplist.toJson();
						//
                        this.changeJsonDate(name, maplist.toJson());
                        this.changeStyle(name);
                        this.changeData(name);
					}.bind(this)
				});
				maplist.load(mapObj);
                this.maplists[name] = maplist;
			}.bind(this));
		}.bind(this));
	},
	loadPropertyTab: function(){
		var tabNodes = this.propertyContent.getElements(".MWFTab");
		if (tabNodes.length){
			var tmpNode = this.propertyContent.getFirst();
			var tabAreaNode = new Element("div", {
				"styles": this.form.css.propertyTabNode
			}).inject(tmpNode, "before");
			
			MWF.require("MWF.widget.Tab", function(){
				var tab = new MWF.widget.Tab(tabAreaNode, {"style": "formPropertyList"});
				tab.load();
				var tabPages = [];
				tabNodes.each(function(node){
					var page = tab.addTab(node, node.get("title"), false);
					tabPages.push(page);
					this.setScrollBar(page.contentNodeArea, "small", null, null);
				}.bind(this));
				tabPages[0].showTab();
				
				this.propertyTab = tab;
				
				this.designer.resizeNode();
			}.bind(this), false);
		}
	},
	
	setEditNodeEvent: function(){
		var property = this;
	//	var inputs = this.process.propertyListNode.getElements(".editTableInput");
		var inputs = this.propertyContent.getElements("input");
		inputs.each(function(input){
			
			var jsondata = input.get("name");

            if (this.module){
                var id = this.module.json.id;
                input.set("name", id+jsondata);
            }

			if (jsondata){
				var inputType = input.get("type").toLowerCase();
				switch (inputType){
					case "radio":
						input.addEvent("change", function(e){
							property.setRadioValue(jsondata, this);
						});
						input.addEvent("blur", function(e){
							property.setRadioValue(jsondata, this);
						});
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
                        property.setRadioValue(jsondata, input);
						break;
					case "checkbox":

						input.addEvent("change", function(e){
							property.setCheckboxValue(jsondata, this);
						});
						input.addEvent("click", function(e){
							property.setCheckboxValue(jsondata, this);
						});
                        input.addEvent("keydown", function(e){
                            e.stopPropagation();
                        });
						break;
					default:
						input.addEvent("change", function(e){
							property.setValue(jsondata, this.value, this);
						});
						input.addEvent("blur", function(e){
							property.setValue(jsondata, this.value, this);
						});
						input.addEvent("keydown", function(e){
							if (e.code==13){
								property.setValue(jsondata, this.value, this);
							}
                            e.stopPropagation();
						});
				}
			}
		}.bind(this));
		
		var selects = this.propertyContent.getElements("select");

		selects.each(function(select){
			var jsondata = select.get("name");
			if (jsondata){
				select.addEvent("change", function(e){
					property.setSelectValue(jsondata, this);
				});
			}
		});
		
		var textareas = this.propertyContent.getElements("textarea");
		textareas.each(function(input){
			var jsondata = input.get("name");
			if (jsondata){
				input.addEvent("change", function(e){
					property.setValue(jsondata, this.value);
				});
				input.addEvent("blur", function(e){
					property.setValue(jsondata, this.value);
				});
                input.addEvent("keydown", function(e){
                    e.stopPropagation();
                });
			}
		}.bind(this));
		
	},
    changeStyle: function(name){
        this.module.setPropertiesOrStyles(name);
    },
    changeData: function(name, input, oldValue){
        this.module._setEditStyle(name, input, oldValue);
    },
    changeJsonDate: function(key, value){
        this.data[key] = value;
    },
	setRadioValue: function(name, input){
		if (input.checked){
			var value = input.value;
			if (value=="false") value = false;
			if (value=="true") value = true;
			var oldValue = this.data[name];
			this.changeJsonDate(name, value);
            this.changeData(name, input, oldValue);
		}
	},
	setCheckboxValue: function(name, input){
		
		var checkboxList = $$("input:[name='"+name+"']");
		var values = [];
		checkboxList.each(function(checkbox){
			if (checkbox.get("checked")){
				values.push(checkbox.value);
			}
		});
		var oldValue = this.data[name];
		//this.data[name] = values;
        this.changeJsonDate(name, values);
        this.changeData(name, input, oldValue);
	},
	setSelectValue: function(name, select){
		var idx = select.selectedIndex;
		var options = select.getElements("option");
		var value = "";
		if (options[idx]){
			value = options[idx].get("value");
		}
		var oldValue = this.data[name];
		//this.data[name] = value;
        this.changeJsonDate(name, value);
        this.changeData(name, select, oldValue);
	},
	
	setValue: function(name, value, obj){
        debugger;
		if (name=="id"){
			if (value!=this.module.json.id){
				if (!value){
					this.designer.notice(MWF.APPFD.LP.notNullId, "error", this.module.form.designer.propertyContentArea, {x:"right", y:"bottom"});
					obj.focus();
					return false;
				}else if (this.module.form.json.moduleList[value]){
					this.designer.notice("error", MWF.APPFD.LP.repetitionsId, this.module.form.designer.propertyContentArea, {x:"right", y:"bottom"});
					obj.focus();
					return false;
				}else{
					var json = this.module.form.json.moduleList[this.module.json.id];
					this.module.form.json.moduleList[value]=json;
					delete this.module.form.json.moduleList[this.module.json.id];
				}
			}
		}
		var oldValue = this.data[name];
		//this.data[name] = value;
        this.changeJsonDate(name, value);
        this.changeData(name, obj, oldValue);
	},
	setEditNodeStyles: function(node){
		var nodes = node.getChildren();
		if (nodes.length){
			nodes.each(function(el){
				var cName = el.get("class");
				if (cName){
					if (this.form.css[cName]) el.setStyles(this.form.css[cName]);
				}
				this.setEditNodeStyles(el);
			}.bind(this));
		}
	},
	loadScriptInput: function(){

		var scriptNodes = this.propertyContent.getElements(".MWFScript");
		scriptNodes.each(function(node){
			MWF.require("MWF.widget.ScriptEditor", function(){
				var script = new MWF.widget.ScriptEditor(node, {
					"onPostSave": function(script){
						this.saveScriptItem(node, script);
					}.bind(this),
					"onQueryDelete": function(script){
						this.deleteScriptItem(node, script);
					}.bind(this)
				});
				this.setScriptItems(script, node);
			}.bind(this));
		}.bind(this));
	},
	deleteScriptItem: function(node, script){
		var jsondata = node.get("name");
		this.data[jsondata].erase(script.data.id);
		this.process.scripts[script.data.id] = null;
		delete this.process.scripts[script.data.id];
		this.process.process.scriptList.erase(script.data);
	},
	saveScriptItem: function(node, script){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		
		var data = script.data;
		var scriptData = this.process.scripts[script.data.id];
		if (!scriptData){
			this.process.process.scriptList.push(data);
			this.process.scripts[script.data.id] = data;
		}
		if (scriptList.indexOf(data.id) == -1){
			this.data[jsondata].push(data.id);
		}
	},
	setScriptItems: function(script, node){
		var jsondata = node.get("name");
		var scriptList = this.data[jsondata];
		scriptList.each(function(id){
			if (id){
				var data = this.process.scripts[id];
				if (data) script.setScriptItem(data);
			}
		}.bind(this));
	}
});
MWF.xApplication.process.FormDesigner.PropertyMulti = new Class({
    Extends: MWF.xApplication.process.FormDesigner.Property,
    Implements: [Options, Events],

    initialize: function(form, modules, propertyNode, designer, options){
        this.setOptions(options);
        this.modules = modules;
        this.form = form;
    //    this.data = module.json;
        this.data = {};
        this.htmlPath = this.options.path;
        this.designer = designer;
        this.maplists = {};
        this.propertyNode = propertyNode;
    },
    load: function(){
        if (this.fireEvent("queryLoad")){
            MWF.getRequestText(this.htmlPath, function(responseText, responseXML){
                this.htmlString = responseText;
                MWF.require("MWF.widget.JsonTemplate", function(){
                    this.fireEvent("postLoad");
                }.bind(this));
            }.bind(this));
        }
    },
    show: function(){
        if (!this.propertyContent){
            if (this.htmlString){
                this.JsonTemplate = new MWF.widget.JsonTemplate({}, this.htmlString);
                this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                this.propertyContent.set("html", this.JsonTemplate.load());

                this.setEditNodeEvent();
                this.setEditNodeStyles(this.propertyContent);
                this.loadPropertyTab();
                this.loadMaplist();
                this.loadScriptArea();
                this.loadTreeData();
                this.loadArrayList();
                //this.loadEventsEditor();
                //this.loadHTMLArea();
                //this.loadJSONArea();
//			this.loadScriptInput();
                //MWF.process.widget.EventsEditor
            }
        }else{
            this.propertyContent.setStyle("display", "block");
        }

    },
    hide: function(){
        if (this.propertyContent) this.propertyContent.destroy();
    },
    changeStyle: function(name){
        this.modules.each(function(module){
            module.setPropertiesOrStyles(name);
        }.bind(this));
    },
    changeData: function(name, input, oldValue){

        this.modules.each(function(module){
            module._setEditStyle(name, input, oldValue);
        }.bind(this));
    },
    changeJsonDate: function(key, value){
        //alert(key+": "+value );
        this.modules.each(function(module){
            module.json[key] = value;
        }.bind(this));
    },
});