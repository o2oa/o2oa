MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);
MWF.xApplication.process.ViewDesigner.Property = MWF.FVProperty = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"path": "/x_component_process_FormDesigner/property/property.html"
	},
	
	initialize: function(module, propertyNode, designer, options){
		this.setOptions(options);
		this.module = module;
		this.view = module.view;
		this.data = module.json;
        this.data.pid = this.view.json.id+this.data.id;
		this.htmlPath = this.options.path;
		this.designer = designer;
		
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
                    this.loadPersonInput();
                    this.loadPersonSelectInput();
                    this.loadViewFilter();
                    this.loadScriptArea();

                    this.loadColumnExportEditor();

                    this.loadJSONArea();
                }
            }.bind(this));
        }else{
            this.propertyContent.setStyle("display", "block");
        }

		

	},
	hide: function(){
		//this.JsonTemplate = null;
		//this.propertyNode.set("html", "");
        if (this.propertyContent) this.propertyContent.setStyle("display", "none");
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
	loadPropertyTab: function(){
		var tabNodes = this.propertyContent.getElements(".MWFTab");
		if (tabNodes.length){
			var tmpNode = this.propertyContent.getFirst();
			var tabAreaNode = new Element("div", {
				"styles": this.view.css.propertyTabNode
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
            if (jsondata && jsondata.substr(0,1)!="_"){
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
                            if (input.hasClass("editTableInputDate")){
                                this.loadCalendar(input);
                            }
                    }
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
                //property.setSelectValue(jsondata, select);
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
    loadCalendar: function(node){
        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(node, {
                "style": "xform",
                "isTime": false,
                "target": this.module.designer.content,
                "format": "%Y-%m-%d",
                "onComplate": function(){
                    //this.validationMode();
                    //this.validation();
                    //this.fireEvent("complete");
                }.bind(this)
            });
            //this.calendar.show();
        }.bind(this));
    },
    changeStyle: function(name){
        this.module.setPropertiesOrStyles(name);
    },
    changeData: function(name, input, oldValue){
        this.module._setEditStyle(name, input, oldValue);
    },
    changeJsonDate: function(key, value){
        if (typeOf(key)!="array") key = [key];
        var o = this.data;
        var len = key.length-1;
        key.each(function(n, i){
            if (!o[n]) o[n] = {};
            if (i<len) o = o[n];
        }.bind(this));
        o[key[len]] = value;
    },
	setRadioValue: function(name, input){
		if (input.checked){
            var i = name.indexOf("*");
            var names = (i==-1) ? name.split(".") : name.substr(i+1, name.length).split(".");

			var value = input.value;
			if (value=="false") value = false;
			if (value=="true") value = true;

            var oldValue = this.data;
            for (var idx = 0; idx<names.length; idx++){
                if (!oldValue[names[idx]]){
                    oldValue = null;
                    break;
                }else{
                    oldValue = oldValue[names[idx]];
                }
            }

			//var oldValue = this.data[name];
			this.changeJsonDate(names, value);
            this.changeData(name, input, oldValue);
		}
	},
	setCheckboxValue: function(name, input){
        var id = this.module.json.id;
        var checkboxList = $$("input[name='"+id+name+"']");
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
        var names = name.split(".");
        var oldValue = this.data;
        for (var idx = 0; idx<names.length; idx++){
            if (!oldValue[names[idx]]){
                oldValue = null;
                break;
            }else{
                oldValue = oldValue[names[idx]];
            }
        }

		//var oldValue = this.data[name];
		//this.data[name] = value;
        this.changeJsonDate(names, value);
        this.changeData(name, obj, oldValue);
	},
	setEditNodeStyles: function(node){
		var nodes = node.getChildren();
		if (nodes.length){
			nodes.each(function(el){
				var cName = el.get("class");
				if (cName){
					if (this.view.css[cName]) el.setStyles(this.view.css[cName]);
				}
				this.setEditNodeStyles(el);
			}.bind(this));
		}
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
                    "maxObj": this.designer.editContentNode,
                    "onChange": function(){
                        this.data[name] = scriptArea.toJson().code;
                    }.bind(this),
                    "onSave": function(){
                        this.designer.saveView();
                    }.bind(this),
                    "style": style || "default"
                });
                scriptArea.load({"code": scriptContent});
            }.bind(this));

        }.bind(this));
    },
    loadPersonInput: function(){
        var identityNodes = this.propertyContent.getElements(".MWFPersonIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFPersonUnit");

        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            identityNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));

            personUnitNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": this.data[node.get("name")],
                    "onChange": function(ids){this.savePersonItem(node, ids);}.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    savePersonItem: function(node, ids){
        var values = [];
        ids.each(function(id){
            //values.push({"name": (id.data.distinguishedName || id.data.name), "id": id.data.id});
            values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        key = name.split(".");
        var o = this.data;
        var len = key.length-1;
        key.each(function(n, i){
            if (!o[n]) o[n] = {};
            if (i<len) o = o[n];
        }.bind(this));
        o[key[len]] = values;

        //this.data.data.restrictWhereEntry[node.get("name")] = values;
    },

    loadPersonSelectInput: function(){
        var applicationNodes = this.propertyContent.getElements(".MWFSelectApplication");
        var processNodes = this.propertyContent.getElements(".MWFSelectProcess");
        // var companyNodes = this.propertyContent.getElements(".MWFSelectCompany");
        // var departmentNodes = this.propertyContent.getElements(".MWFSelectDepartment");
        var personNodes = this.propertyContent.getElements(".MWFSelectPerson");
        var identityNodes = this.propertyContent.getElements(".MWFSelectIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFSelectUnit");

        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function(){
            applicationNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "application",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.applicationList : [],
                    "onChange": function(ids){this.savePersonSelectItem(node, ids);}.bind(this)
                });
            }.bind(this));
            processNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "process",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.processList : [],
                    "onChange": function(ids){this.savePersonSelectItem(node, ids);}.bind(this)
                });
            }.bind(this));
            personUnitNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.unitList : [],
                    "onChange": function(ids){this.savePersonSelectItem(node, ids);}.bind(this)
                });
            }.bind(this));

            personNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "person",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.personList : [],
                    "onChange": function(ids){this.savePersonSelectItem(node, ids);}.bind(this)
                });
            }.bind(this));

            identityNodes.each(function(node){
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": (this.data.data.restrictWhereEntry) ? this.data.data.restrictWhereEntry.identityList : [],
                    "onChange": function(ids){this.savePersonSelectItem(node, ids);}.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    savePersonSelectItem: function(node, ids){
        //this.initWhereEntryData();
        var values = [];
        ids.each(function(id){
            values.push({"name": (id.data.distinguishedName || id.data.name), "id": id.data.id});
            //values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        key = name.split(".");
        var o = this.data;
        var len = key.length-1;
        key.each(function(n, i){
            if (!o[n]) o[n] = {};
            if (i<len) o = o[n];
        }.bind(this));
        o[key[len]] = values;

        //this.data.data.restrictWhereEntry[node.get("name")] = values;
    },
    //loadWorkDataEditor: function(){
    //    var workDataNodes = this.propertyContent.getElements(".MWFWorkData");
    //    workDataNodes.each(function(node){
    //        var select = node.getElement("select");
    //        for (var i=0; i<select.options.length; i++){
    //            if (select.options[i].value==this.data.name){
    //                select.options[i].set("selected", true);
    //                break;
    //            }
    //        }
    //        if (!this.data.type) this.data.type = "text";
    //        select.addEvent("change", function(e){
    //            delete this.data.path;
    //            this.data.name = select.options[select.selectedIndex].value;
    //            this.module.listNode.getLast().set("text", this.data.text+"("+this.data.name+")");
    //            this.setDataType();
    //        }.bind(this));
    //
    //        this.setDataType();
    //    }.bind(this));
    //    var nodes = this.propertyContent.getElements(".MWFWorkDataCheck");
    //    nodes.each(function(node){
    //        if (this.data.name) node.set("checked", true);
    //    }.bind(this));
    //},
    //setDataType: function(){
    //    switch (this.data.name){
    //        case "startTime":case "completedTime":
    //        this.data.type  ="date";
    //        break;
    //        case "completed":
    //            this.data.type  ="boolean";
    //            break;
    //        default:
    //            this.data.type  ="text";
    //    }
    //},
    //loadDataDataEditor: function(){
    //    var nodes = this.propertyContent.getElements(".MWFDataData");
    //    nodes.each(function(node){
    //        var input = node.getElement("input");
    //        input.set("value", this.data.path);
    //        input.addEvent("change", function(e){
    //            delete this.data.name;
    //            this.data.path = input.get("value");
    //            this.module.listNode.getLast().set("text", this.data.text+"("+this.data.path+")");
    //        }.bind(this));
    //        input.addEvent("blur", function(e){
    //            delete this.data.name;
    //            this.data.path = input.get("value");
    //            this.module.listNode.getLast().set("text", this.data.text+"("+this.data.path+")");
    //        }.bind(this));
    //        input.addEvent("keydown", function(e){
    //            if (e.code==13){
    //                delete this.data.name;
    //                this.data.path = input.get("value");
    //                this.module.listNode.getLast().set("text", this.data.text+"("+this.data.path+")");
    //            }
    //            e.stopPropagation();
    //        }.bind(this));
    //
    //        var select = node.getElement("select");
    //        for (var i=0; i<select.options.length; i++){
    //            if (select.options[i].value==this.data.type){
    //                select.options[i].set("selected", true);
    //                break;
    //            }
    //        }
    //        if (!this.data.type) this.data.type = "text";
    //        select.addEvent("change", function(e){
    //            this.data.type = select.options[select.selectedIndex].value;
    //        }.bind(this));
    //
    //    }.bind(this));
    //    var nodes = this.propertyContent.getElements(".MWFDataDataCheck");
    //    nodes.each(function(node){
    //        if (this.data.path) node.set("checked", true);
    //    }.bind(this));
    //},
    loadColumnExportEditor: function(){
        var _self = this;
        var nodes = this.propertyContent.getElements(".MWFColumnExport");
        nodes.each(function(node){
            //if (!this.data.export) this.data.export = {};
            //var sort = this.data.export.sort || "";
            //var sortOrder = this.data.export.sortOrder || "1";
            var select = node.getElement("select");
            var sortList = this.view.data.data.orderEntryList;
            sortList.each(function(order){
                if (order.column==this.data.column){
                    if (order.orderType=="asc") select.options[1].set("selected", true);
                    if (order.orderType=="desc") select.options[1].set("selected", false);
                }
            }.bind(this));
            select.addEvent("change", function(e){
                var v = select.options[select.selectedIndex].value;
                if (v!="none"){
                    var flag = false;
                    sortList.each(function(order){
                        if (order.column==this.data.column){
                            flag = true;
                            order.orderType=select.options[select.selectedIndex].value;
                        }
                    }.bind(this));
                    if (!flag) sortList.push({"column": this.data.column, "orderType": select.options[select.selectedIndex].value});
                }else{
                    var deleteItem = null;
                    sortList.each(function(order){
                        if (order.column==this.data.column){
                            deleteItem = order;
                        }
                    }.bind(this));
                    if (deleteItem) sortList.erase(deleteItem);
                }

            }.bind(this));

            var radios = node.getElements("input");
            var group = this.view.data.data.group;
            if (group.column==this.data.column) radios[0].set("checked", true);
            radios.addEvent("click", function(e){
                if (this.checked){
                    if (this.value=="true") {
                        _self.view.data.data.group.column = _self.data.column;
                        _self.view.items.each(function(col){
                            if (col.property){
                                var groupRadios = col.property.propertyContent.getElement(".MWFColumnExportGroup").getElements("input");
                                groupRadios.each(function(r){
                                    if (r.value=="true") r.set("checked", false);
                                    if (r.value=="false") r.set("checked", true);
                                });
                            }
                        });
                        this.set("checked", true);
                    }else{
                        if (group.column ==_self.data.column) _self.view.data.data.group = {};
                    }
                }
            });
        }.bind(this));

    },

    loadViewFilter: function(){
        var nodes = this.propertyContent.getElements(".MWFViewFilter");
        var filtrData = this.view.data.data.restrictFilterEntryList;
        var customData = this.view.data.data.customFilterEntryList;
        nodes.each(function(node){
            MWF.xDesktop.requireApp("process.ViewDesigner", "widget.ViewFilter", function(){
                var _slef = this;
                new MWF.xApplication.process.ViewDesigner.widget.ViewFilter(node, this.view.designer, {"filtrData": filtrData, "customData": customData}, {
                    "onChange": function(ids){
                        var data = this.getData();
                        _slef.changeJsonDate(["data", "restrictFilterEntryList"], data.data);
                        _slef.changeJsonDate(["data", "customFilterEntryList"], data.customData);
                    }
                });
            }.bind(this));
        }.bind(this));
    },



    loadColumnFilter: function(){
        var nodes = this.propertyContent.getElements(".MWFColumnFilter");
        nodes.each(function(node){
            this.module.filterAreaNode = node;
            var table = new Element("table", {
                "styles": {"width": "100%"},
                "border": "0px",
                "cellPadding": "0",
                "cellSpacing": "0"
            }).inject(node);
            var tr = new Element("tr", {"styles": this.module.css.filterTableTitle}).inject(table);
            var html = "<th style='width:24px;border-right:1px solid #CCC;border-bottom:1px solid #999;'></th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>逻辑</th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>路径</th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>比较</th>" +
                "<th style='border-left:1px solid #FFF;border-bottom:1px solid #999;'>值</th>";
            tr.set("html", html);
            var addActionNode = new Element("div", {"styles": this.module.css.filterAddActionNode}).inject(tr.getFirst("th"));
            addActionNode.addEvent("click", function(){
                this.addFilter(table);
            }.bind(this));

            if (this.data.filterList) {
                this.data.filterList.each(function (op) {
                    new MWF.xApplication.process.ViewDesigner.Property.Filter(op, table, this);
                }.bind(this));
            }
        }.bind(this));
    },
    addFilter: function(table){
        op = {
            "logic": "and",
            "comparison": "",
            "value": ""
        }
        if (!this.data.filterList) this.data.filterList = [];
        this.data.filterList.push(op);
        var filter = new MWF.xApplication.process.ViewDesigner.Property.Filter(op, table, this);
        filter.editMode();
    }
    //initWhereEntryData: function(){
    //    if (!this.data.data.restrictWhereEntry) this.data.data.restrictWhereEntry = {
    //        "applicationList": [],
    //        "processList": [],
    //        "companyList": [],
    //        "departmentList": [],
    //        "personList": [],
    //        "identityList": []
    //    };
    //},
    //loadApplicationSelector: function(){
    //    var nodes = this.propertyContent.getElements(".MWFApplicationSelect");
    //    if (nodes.length){
    //        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
    //            nodes.each(function(node){
    //                var title = new Element("div", {"styles": this.view.css.applicationSelectTitle, "text": node.get("title")}).inject(node);
    //                var content = new Element("div", {"styles": this.view.css.applicationSelectContent}).inject(node);
    //                var action = new Element("div", {"styles": this.view.css.applicationSelectAction, "text": node.get("title")}).inject(node);
    //                action.addEvent("click", function(e){
    //                    var values = [];
    //                    if (this.data.data.whereEntry){
    //                        if (this.data.data.whereEntry.applicationList.length){
    //                            this.data.data.whereEntry.applicationList.each(function(item){
    //                                values.push(item.id);
    //                            }.bind(this));
    //                        }
    //                    }
    //                    var options = {
    //                        "type": "application",
    //                        "count": 0,
    //                        "values": values,
    //                        //"title": this.app.lp.monthly.selectSortApplication,
    //                        "onComplete": function(items){
    //                            this.initWhereEntryData();
    //                            this.data.data.whereEntry.applicationList = [];
    //                            content.empty();
    //                            items.each(function(item){
    //                                this.data.data.whereEntry.applicationList.push({
    //                                    "id": item.data.id,
    //                                    "name": item.data.name
    //                                });
    //                                new Element("div", {
    //                                    "styles": this.view.css.applicationSelectItem,
    //                                    "text": item.data.name
    //                                }).inject(content);
    //                            }.bind(this));
    //                        }.bind(this)
    //                    };
    //                    var selector = new MWF.OrgSelector(this.view.designer.content, options);
    //                }.bind(this));
    //
    //                this.initWhereEntryData();
    //                this.data.data.whereEntry.applicationList.each(function(app){
    //                    new Element("div", {
    //                        "styles": this.view.css.applicationSelectItem,
    //                        "text": app.name
    //                    }).inject(content);
    //                }.bind(this));
    //            }.bind(this));
    //        }.bind(this));
    //    }
    //},

    //loadApplicationSelector1: function(){
    //    var nodes = this.propertyContent.getElements(".MWFApplicationSelect");
    //    if (nodes.length){
    //        this._getAppSelector(function(){
    //            nodes.each(function(node){
    //                var title = new Element("div", {"styles": this.view.css.applicationSelectTitle, "text": node.get("title")}).inject(node);
    //                var content = new Element("div", {"styles": this.view.css.applicationSelectContent}).inject(node);
    //                var action = new Element("div", {"styles": this.view.css.applicationSelectAction, "text": node.get("title")}).inject(node);
    //                action.addEvent("click", function(e){
    //                    this.appSelector.load(function(apps){
    //                        if (!this.data.data.select) this.data.data.select = {"applicationRestrictList": [], "processRestrictList": []};
    //                        this.data.data.select.applicationRestrictList = [];
    //                        content.empty();
    //                        if (apps.length){
    //                            apps.each(function(app){
    //                                var o = {
    //                                    "name": app.name,
    //                                    "id": app.id,
    //                                    "alias": app.alias
    //                                }
    //                                this.data.data.select.applicationRestrictList.push(o);
    //
    //                                new Element("div", {
    //                                    "styles": this.view.css.applicationSelectItem,
    //                                    "text": app.name
    //                                }).inject(content);
    //
    //                            }.bind(this));
    //                        }
    //                    }.bind(this));
    //                }.bind(this));
    //                if (!this.data.data.select) this.data.data.select = {"applicationRestrictList": [], "processRestrictList": []};
    //               this.data.data.select.applicationRestrictList.each(function(app){
    //                   new Element("div", {
    //                       "styles": this.view.css.applicationSelectItem,
    //                       "text": app.name
    //                   }).inject(content);
    //               }.bind(this));
    //
    //            }.bind(this));
    //        }.bind(this));
    //    }
    //},
    //
    //_getAppSelector: function(callback){
    //    if (!this.appSelector){
    //        MWF.xDesktop.requireApp("process.ProcessManager", "widget.ApplicationSelector", function(){
    //            this.appSelector = new MWF.xApplication.process.ProcessManager.widget.ApplicationSelector(this.view.designer, {"maskNode": this.view.designer.content});
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //},
    //this.initWhereEntryData();
    //loadProcessSelector: function(){
    //    var nodes = this.propertyContent.getElements(".MWFApplicationSelect");
    //    if (nodes.length){
    //        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
    //            nodes.each(function(node){
    //                var title = new Element("div", {"styles": this.view.css.applicationSelectTitle, "text": node.get("title")}).inject(node);
    //                var content = new Element("div", {"styles": this.view.css.applicationSelectContent}).inject(node);
    //                var action = new Element("div", {"styles": this.view.css.applicationSelectAction, "text": node.get("title")}).inject(node);
    //                action.addEvent("click", function(e){
    //                    var values = [];
    //                    if (this.data.data.whereEntry){
    //                        if (this.data.data.whereEntry.processList.length){
    //                            this.data.data.whereEntry.processList.each(function(item){
    //                                values.push(item.id);
    //                            }.bind(this));
    //                        }
    //                    }
    //                    var options = {
    //                        "type": "process",
    //                        "count": 0,
    //                        "values": values,
    //                        "onComplete": function(items){
    //                            this.initWhereEntryData();
    //                            this.data.data.whereEntry.processList = [];
    //                            content.empty();
    //                            items.each(function(item){
    //                                this.data.data.whereEntry.processList.push({
    //                                    "id": item.data.id,
    //                                    "name": item.data.name
    //                                });
    //                                new Element("div", {
    //                                    "styles": this.view.css.applicationSelectItem,
    //                                    "text": item.data.name
    //                                }).inject(content);
    //                            }.bind(this));
    //                        }.bind(this)
    //                    };
    //                    var selector = new MWF.OrgSelector(this.view.designer.content, options);
    //                }.bind(this));
    //
    //                this.initWhereEntryData();
    //                this.data.data.whereEntry.processList.each(function(app){
    //                    new Element("div", {
    //                        "styles": this.view.css.applicationSelectItem,
    //                        "text": app.name
    //                    }).inject(content);
    //                }.bind(this));
    //            }.bind(this));
    //        }.bind(this));
    //    }
    //}

    //loadProcessSelector1: function(){
    //    var nodes = this.propertyContent.getElements(".MWFProcessSelect");
    //    if (nodes.length){
    //        this._getProcessSelector(function(){
    //            nodes.each(function(node){
    //                var title = new Element("div", {"styles": this.view.css.applicationSelectTitle, "text": node.get("title")}).inject(node);
    //                var content = new Element("div", {"styles": this.view.css.applicationSelectContent}).inject(node);
    //                var action = new Element("div", {"styles": this.view.css.applicationSelectAction, "text": node.get("title")}).inject(node);
    //                action.addEvent("click", function(e){
    //                    var ids=[];
    //                    this.data.data.select.applicationRestrictList.each(function(app){
    //                        ids.push(app.id);
    //                    });
    //                    this.processSelector.load(ids, function(apps){
    //                        if (!this.data.data.select) this.data.data.select = {"applicationRestrictList": [], "processRestrictList": []};
    //                        this.data.data.select.processRestrictList = [];
    //                        content.empty();
    //                        if (apps.length){
    //                            apps.each(function(app){
    //                                var o = {
    //                                    "name": app.name,
    //                                    "id": app.id,
    //                                    "alias": app.alias
    //                                }
    //                                this.data.data.select.processRestrictList.push(o);
    //
    //                                new Element("div", {
    //                                    "styles": this.view.css.applicationSelectItem,
    //                                    "text": app.name
    //                                }).inject(content);
    //
    //                            }.bind(this));
    //                        }
    //                    }.bind(this));
    //                }.bind(this));
    //                if (!this.data.data.select) this.data.data.select = {"applicationRestrictList": [], "processRestrictList": []};
    //                this.data.data.select.processRestrictList.each(function(app){
    //                    new Element("div", {
    //                        "styles": this.view.css.applicationSelectItem,
    //                        "text": app.name
    //                    }).inject(content);
    //                }.bind(this));
    //
    //            }.bind(this));
    //        }.bind(this));
    //    }
    //},
    //_getProcessSelector: function(callback){
    //    if (!this.processSelector){
    //        MWF.xDesktop.requireApp("process.ProcessManager", "widget.ProcessSelector", function(){
    //            this.processSelector = new MWF.xApplication.process.ProcessManager.widget.ProcessSelector(this.view.designer, {"maskNode": this.view.designer.content});
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //}


});

MWF.xApplication.process.ViewDesigner.Property.Filter = new Class({
    Implements: [Events],
    initialize: function(json, table, property){
        this.property = property;
        this.module = property.module;
        this.table = table;
        this.data = json;

        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.module.css.filterTableTd}).inject(this.table);
        var html = "<td style='widtd:24px;border-right:1px solid #CCC;border-bottom:1px solid #999;'></td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999; width:60px'>"+this.data.logic+"</td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999; width:30px'>列值</td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999;'>"+this.data.comparison+"</td>" +
            "<td style='padding:3px;border-bottom:1px solid #999;'>"+this.data.value+"</td>";
        this.node.set("html", html);
        var tds = this.node.getElements("td");

        this.delActionNode = new Element("div", {"styles": this.module.css.filterDelActionNode}).inject(tds[0]);
        this.delActionNode.addEvent("click", function(e){
            this.delFilter(e);
            e.stopPropagation();
        }.bind(this));

        this.logicNode = tds[1];
        this.comparisonNode = tds[3];
        this.valueNode = tds[4];

        this.node.addEvent("click", function(){
            if (!this.isEditMode) this.editMode();
        }.bind(this));
        this.node.addEvent("blur", function(){
            if (this.isEditMode) this.readMode();
        }.bind(this));
    },
    delFilter: function(e){
        var _self = this;
        this.property.designer.confirm("warn", e, MWF.APPVD.LP.notice.deleteFilterTitle, MWF.APPVD.LP.notice.deleteFilter, 300, 120, function(){

            _self.node.destroy();
            _self.property.data.filterList.erase(_self.data);
            MWF.release(_self);

            this.close();
        }, function(){
            this.close();
        }, null);
    },
    editMode: function(){
        if (this.property.editModeFilter){
            if (this.property.editModeFilter!=this) this.property.editModeFilter.readMode();
        }

        var width = this.logicNode.getSize().x-9;
        this.logicNode.empty();
        var logicSelect = new Element("select", {"styles": {"width": "90%"}}).inject(this.logicNode);
        var html = "";
        if (this.data.logic=="and"){
            html = "<option value=\"and\" selected>and</option><option value=\"or\">or</option>";
        }else{
            html = "<option value=\"and\">and</option><option value=\"or\" selected>or</option>";
        }
        logicSelect.set("html", html);
        logicSelect.addEvent("change", function(){
            this.data.logic = logicSelect.options[logicSelect.selectedIndex].value;
        }.bind(this));

        width = this.comparisonNode.getSize().x-9;
        this.comparisonNode.empty();
        var comparisonSelect = new Element("select", {"styles": {"width": "90%"}}).inject(this.comparisonNode);
        html = "";
        switch (this.property.data.type){
            case "text":
                html += "<option value=''></option><option value='==' "+((this.data.comparison=="==") ? "selected": "")+">等于(==)</option>" +
                "<option value='!=' "+((this.data.comparison=="!=") ? "selected": "")+">不等于(!=)</option>" +
                "<option value='@' "+((this.data.comparison=="@") ? "selected": "")+">包含(@)</option>";
                break;
            case "date":
                html += "<option value=''></option><option value='&gt;' "+((this.data.comparison==">") ? "selected": "")+">大于(&gt;)</option>" +
                "<option value='&lt;' "+((this.data.comparison=="<") ? "selected": "")+">小于(&lt;)</option>" +
                "<option value='&gt;=' "+((this.data.comparison==">=") ? "selected": "")+">大于等于(&gt;=)</option>" +
                "<option value='&lt;=' "+((this.data.comparison=="<=") ? "selected": "")+">小于等于(&lt;=)</option>";
                break;
            case "number":
                html += "<option value=''></option><option value='==' "+((this.data.comparison=="==") ? "selected": "")+">等于(==)</option>" +
                "<option value='!=' "+((this.data.comparison=="!=") ? "selected": "")+">不等于(!=)</option>" +
                "<option value='&gt;' "+((this.data.comparison==">") ? "selected": "")+">大于(&gt;)</option>" +
                "<option value='&lt;' "+((this.data.comparison=="<") ? "selected": "")+">小于(&lt;)</option>" +
                "<option value='&gt;=' "+((this.data.comparison==">=") ? "selected": "")+">大于等于(&gt;=)</option>" +
                "<option value='&lt;=' "+((this.data.comparison=="<=") ? "selected": "")+">小于等于(&lt;=)</option>";
                break;
            case "boolean":
                html += "<option value=''></option><option value='==' "+((this.data.comparison=="==") ? "selected": "")+">等于(==)</option>" +
                "<option value='!=' "+((this.data.comparison=="!=") ? "selected": "")+">不等于(!=)</option>";
                break;
        }
        comparisonSelect.set("html", html);
        comparisonSelect.addEvent("change", function(){
            this.data.comparison = comparisonSelect.options[comparisonSelect.selectedIndex].value;
        }.bind(this));


        width = this.valueNode.getSize().x-9;
        this.valueNode.empty();
        var type = "text";
        switch (this.property.data.type){
            case "date":
                var valueInput = new Element("input", {"styles": {"width": "90%"},
                    "type": "text",
                    "value": this.data.value
                }).inject(this.valueNode);

                MWF.require("MWF.widget.Calendar", function(){
                    this.calendar = new MWF.widget.Calendar(valueInput, {
                        "style": "xform",
                        "isTime": true,
                        "target": this.property.designer.content,
                        "format": "%Y-%m-%d %H:%M:%S"
                    });
                    //this.calendar.show();
                }.bind(this));

                break;
            case "number":
                var valueInput = new Element("input", {"styles": {"width": "90%"},
                    "type": "number",
                    "value": this.data.value
                }).inject(this.valueNode);
                break;
            case "boolean":
                var valueInput = new Element("select", {"styles": {"width": ""+width+"px"},
                    "html": "<option value=\"\"></option><option value=\"true\" "+((this.data.value) ? "selected": "")+">true</option><option value=\"false\" "+((!this.data.value) ? "selected": "")+">false</option>"
                }).inject(this.valueNode);
                break;
            default:
                var valueInput = new Element("input", {"styles": {"width": "90%"},
                    "type": "text",
                    "value": this.data.value
                }).inject(this.valueNode);
        }
        if (valueInput.tagName.toLowerCase()=="select"){
            valueInput.addEvent("change", function(){
                var v = valueInput.options[valueInput.selectedIndex].value;
                this.data.value = (v="true") ? true : false;
            }.bind(this));
        }else{
            valueInput.addEvent("change", function(e){
                this.data.value = valueInput.get("value");
            }.bind(this));
            valueInput.addEvent("blur", function(e){
                this.data.value = valueInput.get("value");
            }.bind(this));
            valueInput.addEvent("keydown", function(e){
                if (e.code==13){
                    this.data.value = valueInput.get("value");
                    this.readMode();
                }
                e.stopPropagation();
            }.bind(this));
        }
        this.isEditMode = true;
        this.property.editModeFilter = this;
    },
    readMode: function(){
        if (this.isEditMode){
            var logicSelect = this.logicNode.getElement("select");
            this.data.logic = logicSelect.options[logicSelect.selectedIndex].value;

            var comparisonSelect = this.comparisonNode.getElement("select");
            this.data.comparison = comparisonSelect.options[comparisonSelect.selectedIndex].value;

            var valueInput = this.valueNode.getFirst();
            if (valueInput.tagName.toLowerCase()=="select"){
                var v = valueInput.options[valueInput.selectedIndex].value;
                this.data.value = (v="true") ? true : false;
            }else{
                this.data.value = valueInput.get("value");
            }

            this.logicNode.empty();
            this.comparisonNode.empty();
            this.valueNode.empty();

            this.logicNode.set("text", this.data.logic);
            this.comparisonNode.set("text", this.data.comparison);
            this.valueNode.set("text", this.data.value);
            this.isEditMode = false;
            this.property.editModeFilter = null;
        }
    }
});