MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.widget.JsonTemplate", null, false);
MWF.xApplication.query.ViewDesigner.Property = MWF.FVProperty = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "path": "../x_component_query_FormDesigner/property/property.html"
    },

    initialize: function (module, propertyNode, designer, options) {
        this.setOptions(options);
        this.module = module;
        this.view = module.view;
        this.data = module.json;
        this.data.vid = this.view.json.id;
        this.data.vtype = this.view.json.type;
        this.data.pid = this.view.json.id + this.data.id;
        this.htmlPath = this.options.path;

        this.maplists = {};

        this.designer = designer;

        this.propertyNode = propertyNode;
    },

    load: function () {
        if (this.fireEvent("queryLoad")) {
            MWF.getRequestText(this.htmlPath, function (responseText, responseXML) {
                this.htmlString = responseText;
                this.fireEvent("postLoad");
            }.bind(this));
        }
        this.propertyNode.addEvent("keydown", function (e) {
            e.stopPropagation();
        });
    },
    editProperty: function (td) {
    },
    getHtmlString: function (callback) {
        if (!this.htmlString) {
            MWF.getRequestText(this.htmlPath, function (responseText, responseXML) {
                this.htmlString = responseText;
                if (callback) callback();
            }.bind(this));
        } else {
            if (callback) callback();
        }
    },
    show: function () {
        if (!this.propertyContent) {
            this.getHtmlString(function () {
                if (this.htmlString) {
                    this.htmlString = o2.bindJson(this.htmlString, {"lp": MWF.xApplication.query.ViewDesigner.LP.propertyTemplate});
                    this.JsonTemplate = new MWF.widget.JsonTemplate(this.data, this.htmlString);
                    this.propertyContent = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyNode);
                    //var htmlStr = this.JsonTemplate.load();
                    this.propertyContent.set("html", this.JsonTemplate.load());
                    //this.propertyContent.injectHtml(htmlStr);

                    this.setEditNodeEvent();
                    this.setEditNodeStyles(this.propertyContent);
                    this.loadPropertyTab();
                    this.loadPersonInput();
                    this.loadPersonSelectInput();
                    this.loadViewFilter();
                    this.loadScriptArea();

                    this.loadColumnExportEditor();

                    this.loadJSONArea();

                    this.loadEventsEditor();
                    this.loadViewStylesArea();
                    this.loadPagingStylesArea();
                    this.loadActionStylesArea();
                    this.loadActionArea();
                    this.loadStylesList();
                    this.loadMaplist();
                }
            }.bind(this));
        } else {
            this.propertyContent.setStyle("display", "block");
        }


    },
    hide: function () {
        //this.JsonTemplate = null;
        //this.propertyNode.set("html", "");
        if (this.propertyContent) this.propertyContent.setStyle("display", "none");
    },

    loadJSONArea: function () {
        var jsonNode = this.propertyContent.getElement(".MWFJSONArea");

        if (jsonNode) {
            this.propertyTab.pages.each(function (page) {
                if (page.contentNode == jsonNode.parentElement) {
                    page.setOptions({
                        "onShow": function () {
                            jsonNode.empty();
                            MWF.require("MWF.widget.JsonParse", function () {
                                this.json = new MWF.widget.JsonParse(this.module.json, jsonNode, null);
                                this.json.load();
                            }.bind(this));
                        }.bind(this)
                    });
                }
            }.bind(this));
        }
    },
    loadPropertyTab: function () {
        var tabNodes = this.propertyContent.getElements(".MWFTab");
        if (tabNodes.length) {
            var tmpNode = this.propertyContent.getFirst();
            var tabAreaNode = new Element("div", {
                "styles": this.view.css.propertyTabNode
            }).inject(tmpNode, "before");

            MWF.require("MWF.widget.Tab", function () {
                var tab = new MWF.widget.Tab(tabAreaNode, {"style": "formPropertyList"});
                tab.load();
                var tabPages = [];
                tabNodes.each(function (node) {
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

    setEditNodeEvent: function () {
        var property = this;
        //	var inputs = this.process.propertyListNode.getElements(".editTableInput");
        var inputs = this.propertyContent.getElements("input");
        inputs.each(function (input) {
            var jsondata = input.get("name");
            if (jsondata && jsondata.substr(0, 1) != "_") {
                if (this.module) {
                    var id = this.module.json.id;
                    input.set("name", id + jsondata);
                }

                if (jsondata) {
                    var inputType = input.get("type").toLowerCase();
                    switch (inputType) {
                        case "radio":
                            input.addEvent("change", function (e) {
                                property.setRadioValue(jsondata, this);
                            });
                            input.addEvent("blur", function (e) {
                                property.setRadioValue(jsondata, this);
                            });
                            input.addEvent("keydown", function (e) {
                                e.stopPropagation();
                            });
                            property.setRadioValue(jsondata, input);
                            break;
                        case "checkbox":

                            input.addEvent("change", function (e) {
                                property.setCheckboxValue(jsondata, this);
                            });
                            input.addEvent("click", function (e) {
                                property.setCheckboxValue(jsondata, this);
                            });
                            input.addEvent("keydown", function (e) {
                                e.stopPropagation();
                            });
                            break;
                        default:
                            input.addEvent("change", function (e) {
                                property.setValue(jsondata, this.value, this);
                            });
                            input.addEvent("blur", function (e) {
                                property.setValue(jsondata, this.value, this);
                            });
                            input.addEvent("keydown", function (e) {
                                if (e.code == 13) {
                                    property.setValue(jsondata, this.value, this);
                                }
                                e.stopPropagation();
                            });
                            if (input.hasClass("editTableInputDate")) {
                                this.loadCalendar(input, jsondata);
                            }
                    }
                }
            }
        }.bind(this));

        var selects = this.propertyContent.getElements("select");
        selects.each(function (select) {
            var jsondata = select.get("name");
            if (jsondata) {
                select.addEvent("change", function (e) {
                    property.setSelectValue(jsondata, this);
                });
                //property.setSelectValue(jsondata, select);
            }
        });

        var textareas = this.propertyContent.getElements("textarea");
        textareas.each(function (input) {
            var jsondata = input.get("name");
            if (jsondata) {
                input.addEvent("change", function (e) {
                    property.setValue(jsondata, this.value);
                });
                input.addEvent("blur", function (e) {
                    property.setValue(jsondata, this.value);
                });
                input.addEvent("keydown", function (e) {
                    e.stopPropagation();
                });
            }
        }.bind(this));

    },
    loadCalendar: function (node, jsondata) {
        MWF.require("MWF.widget.Calendar", function () {
            this.calendar = new MWF.widget.Calendar(node, {
                "style": "xform",
                "isTime": false,
                "target": this.module.designer.content,
                "format": "%Y-%m-%d",
                "onClear" : function () {
                    debugger;
                    this.setValue(jsondata, node.value, node);
                }.bind(this),
                "onComplate": function () {
                    this.setValue(jsondata, node.value, node);
                    //this.validationMode();
                    //this.validation();
                    //this.fireEvent("complete");
                }.bind(this)
            });
            //this.calendar.show();
        }.bind(this));
    },
    changeStyle: function (name) {
        this.module.setPropertiesOrStyles(name);
    },
    changeData: function (name, input, oldValue) {
        var i = name.lastIndexOf("*");
        var n = (i != -1) ? name.substr(i + 1, name.length) : name;
        this.module._setEditStyle(n, input, oldValue);
    },
    changeJsonDate: function (key, value) {
        if (typeOf(key) != "array") key = [key];
        var o = this.data;
        var len = key.length - 1;
        key.each(function (n, i) {
            if (!o[n]) o[n] = {};
            if (i < len) o = o[n];
        }.bind(this));
        o[key[len]] = value;
    },
    setRadioValue: function (name, input) {
        if (input.checked) {
            var i = name.indexOf("*");
            var names = (i == -1) ? name.split(".") : name.substr(i + 1, name.length).split(".");

            var value = input.value;
            if (value == "false") value = false;
            if (value == "true") value = true;

            var oldValue = this.data;
            for (var idx = 0; idx < names.length; idx++) {
                if (!oldValue[names[idx]]) {
                    oldValue = null;
                    break;
                } else {
                    oldValue = oldValue[names[idx]];
                }
            }

            //var oldValue = this.data[name];
            this.changeJsonDate(names, value);
            this.changeData(name, input, oldValue);
        }
    },
    setCheckboxValue: function (name, input) {
        var id = this.module.json.id;
        var checkboxList = $$("input[name='" + id + name + "']");
        var values = [];
        checkboxList.each(function (checkbox) {
            if (checkbox.get("checked")) {
                values.push(checkbox.value);
            }
        });
        var oldValue = this.data[name];
        //this.data[name] = values;
        this.changeJsonDate(name, values);
        this.changeData(name, input, oldValue);
    },
    setSelectValue: function (name, select) {
        var idx = select.selectedIndex;
        var options = select.getElements("option");
        var value = "";
        if (options[idx]) {
            value = options[idx].get("value");
        }

        var i = name.indexOf("*");
        var names = (i == -1) ? name.split(".") : name.substr(i + 1, name.length).split(".");

        //var oldValue = this.data[name];
        var oldValue = this.data;
        for (var idx = 0; idx < names.length; idx++) {
            if (!oldValue[names[idx]]) {
                oldValue = null;
                break;
            } else {
                oldValue = oldValue[names[idx]];
            }
        }

        //var oldValue = this.data[name];
        //this.data[name] = value;
        this.changeJsonDate(names, value);
        this.changeData(name, select, oldValue);
    },

    setValue: function (name, value, obj) {
        var names = name.split(".");
        var oldValue = this.data;
        for (var idx = 0; idx < names.length; idx++) {
            if (!oldValue[names[idx]]) {
                oldValue = null;
                break;
            } else {
                oldValue = oldValue[names[idx]];
            }
        }

        //var oldValue = this.data[name];
        //this.data[name] = value;
        this.changeJsonDate(names, value);
        this.changeData(name, obj, oldValue);
    },
    setEditNodeStyles: function (node) {
        var nodes = node.getChildren();
        if (nodes.length) {
            nodes.each(function (el) {
                var cName = el.get("class");
                if (cName) {
                    if (this.view.css[cName]) el.setStyles(this.view.css[cName]);
                }
                this.setEditNodeStyles(el);
            }.bind(this));
        }
    },
    loadScriptArea: function () {
        var scriptAreas = this.propertyContent.getElements(".MWFScriptArea");
        var formulaAreas = this.propertyContent.getElements(".MWFFormulaArea");
        this.loadScriptEditor(scriptAreas);
        this.loadScriptEditor(formulaAreas, "formula");
    },
    loadScriptEditor: function (scriptAreas, style) {
        scriptAreas.each(function (node) {
            var title = node.get("title");
            var name = node.get("name");
            var names = name.split(".");

            var scriptContent = this.data;
            Array.each(names, function (n) {
                if (scriptContent) scriptContent = scriptContent[n];
            });

            // var scriptContent = this.data[name];

            MWF.require("MWF.widget.ScriptArea", function () {
                var scriptArea = new MWF.widget.ScriptArea(node, {
                    "title": title,
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.editContentNode,
                    "onChange": function () {

                        var scriptObj = this.data;
                        Array.each(names, function (n, idx) {
                            if( idx === names.length -1 )return;
                            if (scriptObj) scriptObj = scriptObj[n];
                        });

                        scriptObj[names[names.length -1]] = scriptArea.toJson().code;

                        // this.data[name] = scriptArea.toJson().code;
                    }.bind(this),
                    "onSave": function () {
                        this.designer.saveView();
                    }.bind(this),
                    "style": style || "default",
                    "runtime": "server"
                });
                scriptArea.load({"code": scriptContent});
            }.bind(this));

        }.bind(this));
    },
    loadStatColumnSelect: function () {
    },
    loadPersonInput: function () {
        var identityNodes = this.propertyContent.getElements(".MWFPersonIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFPersonUnit");

        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function () {
            identityNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": this.data[node.get("name")],
                    "onChange": function (ids) {
                        this.savePersonItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            personUnitNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": this.data[node.get("name")],
                    "onChange": function (ids) {
                        this.savePersonItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    savePersonItem: function (node, ids) {
        var values = [];
        ids.each(function (id) {
            //values.push({"name": (id.data.distinguishedName || id.data.name), "id": id.data.id});
            values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        key = name.split(".");
        var o = this.data;
        var len = key.length - 1;
        key.each(function (n, i) {
            if (!o[n]) o[n] = {};
            if (i < len) o = o[n];
        }.bind(this));
        o[key[len]] = values;

        //this.data.data.restrictWhereEntry[node.get("name")] = values;
    },

    loadPersonSelectInput: function () {
        var applicationNodes = this.propertyContent.getElements(".MWFSelectApplication");
        var processNodes = this.propertyContent.getElements(".MWFSelectProcess");
        // var companyNodes = this.propertyContent.getElements(".MWFSelectCompany");
        // var departmentNodes = this.propertyContent.getElements(".MWFSelectDepartment");
        var personNodes = this.propertyContent.getElements(".MWFSelectPerson");
        var identityNodes = this.propertyContent.getElements(".MWFSelectIdentity");
        var personUnitNodes = this.propertyContent.getElements(".MWFSelectUnit");

        var cmsapplicationNodes = this.propertyContent.getElements(".MWFSelectCMSApplication");
        var cmscategoryNodes = this.propertyContent.getElements(".MWFSelecCMStCategory");

        MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function () {
            applicationNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "application",
                    "names": (this.data.data.where) ? this.data.data.where.applicationList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
            processNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "process",
                    "names": (this.data.data.where) ? this.data.data.where.processList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
            personUnitNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "unit",
                    "names": (this.data.data.where) ? this.data.data.where.creatorUnitList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            personNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "person",
                    "names": (this.data.data.where) ? this.data.data.where.creatorPersonList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            identityNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "identity",
                    "names": (this.data.data.where) ? this.data.data.where.creatorIdentityList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

            cmsapplicationNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "CMSApplication",
                    "names": (this.data.data.where) ? this.data.data.where.appInfoList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));
            cmscategoryNodes.each(function (node) {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(node, this.view.designer, {
                    "type": "CMSCategory",
                    "names": (this.data.data.where) ? this.data.data.where.categoryInfoList : [],
                    "onChange": function (ids) {
                        this.savePersonSelectItem(node, ids);
                    }.bind(this)
                });
            }.bind(this));

        }.bind(this));
    },
    savePersonSelectItem: function (node, ids) {
        //this.initWhereEntryData();
        var values = [];
        ids.each(function (id) {
            values.push({"name": (id.data.distinguishedName || id.data.name), "id": id.data.id});
            //values.push((id.data.distinguishedName || id.data.id || id.data.name));
        }.bind(this));
        var name = node.get("name");

        key = name.split(".");
        var o = this.data;
        var len = key.length - 1;
        key.each(function (n, i) {
            if (!o[n]) o[n] = {};
            if (i < len) o = o[n];
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
    loadColumnExportEditor: function () {
        var _self = this;
        var nodes = this.propertyContent.getElements(".MWFColumnExport");
        nodes.each(function (node) {
            //if (!this.data.export) this.data.export = {};
            //var sort = this.data.export.sort || "";
            //var sortOrder = this.data.export.sortOrder || "1";
            var select = node.getElement("select");
            var sortList = this.view.data.data.orderList;
            sortList.each(function (order) {
                if (order.column == this.data.column) {
                    if (order.orderType == "asc") select.options[1].set("selected", true);
                    if (order.orderType == "desc") select.options[1].set("selected", false);
                }
            }.bind(this));
            select.addEvent("change", function (e) {
                debugger;
                var sortList = this.view.data.data.orderList;
                var v = select.options[select.selectedIndex].value;
                if (v != "none") {
                    var flag = false;
                    sortList.each(function (order) {
                        if (order.column == this.data.column) {
                            flag = true;
                            order.orderType = select.options[select.selectedIndex].value;
                        }
                    }.bind(this));
                    if (!flag) sortList.push({
                        "column": this.data.column,
                        "orderType": select.options[select.selectedIndex].value
                    });

                    var oldOrderList = Array.clone(this.view.data.data.orderList);
                    this.view.data.data.orderList = [];
                    this.view.json.data.selectList.each(function (sel) {
                        oldOrderList.each(function (order) {
                            if (order.column == sel.column) {
                                this.view.data.data.orderList.push(order);
                            }
                        }.bind(this));
                    }.bind(this));
                } else {
                    var sortList = this.view.data.data.orderList;
                    var deleteItem = null;
                    sortList.each(function (order) {
                        if (order.column == this.data.column) {
                            deleteItem = order;
                        }
                    }.bind(this));
                    if (deleteItem) sortList.erase(deleteItem);
                }
            }.bind(this));

            var radios = node.getElements("input[name='" + this.module.json.id + "groupEntry']");
            var group = this.view.data.data.group;
            if (group.column == this.data.column) radios[0].set("checked", true);
            radios.addEvent("click", function (e) {
                if (this.checked) {
                    if (this.value == "true") {
                        _self.view.data.data.group.column = _self.data.column;
                        _self.view.items.each(function (col) {
                            if (col.property) {
                                var groupRadios = col.property.propertyContent.getElement(".MWFColumnExportGroup").getElements("input");
                                groupRadios.each(function (r) {
                                    if (r.value == "true") r.set("checked", false);
                                    if (r.value == "false") r.set("checked", true);
                                });
                            }
                        });
                        (_self.view.data.data.selectList).each(function (s) {
                            if (s.column !== _self.data.column) s.groupEntry = false;
                        });
                        this.set("checked", true);
                    } else {
                        if (group.column == _self.data.column) _self.view.data.data.group = {};
                    }
                }
            });
        }.bind(this));

    },

    loadViewFilter: function () {
        var nodes = this.propertyContent.getElements(".MWFViewFilter");
        var filtrData = this.view.data.data.filterList;
        var customData = this.view.data.data.customFilterList;
        nodes.each(function (node) {
            MWF.xDesktop.requireApp("query.ViewDesigner", "widget.ViewFilter", function () {
                var _slef = this;
                new MWF.xApplication.query.ViewDesigner.widget.ViewFilter(node, this.view.designer, {
                    "filtrData": filtrData,
                    "customData": customData
                }, {
                    "onChange": function (ids) {
                        var data = this.getData();
                        _slef.changeJsonDate(["data", "filterList"], data.data);
                        _slef.changeJsonDate(["data", "customFilterList"], data.customData);
                    }
                });
            }.bind(this));
        }.bind(this));
    },


    loadColumnFilter: function () {
        var nodes = this.propertyContent.getElements(".MWFColumnFilter");
        nodes.each(function (node) {
            this.module.filterAreaNode = node;
            var table = new Element("table", {
                "styles": {"width": "100%"},
                "border": "0px",
                "cellPadding": "0",
                "cellSpacing": "0"
            }).inject(node);
            var tr = new Element("tr", {"styles": this.module.css.filterTableTitle}).inject(table);
            var lp =  MWF.APPDVD.LP.filter;
            var html = "<th style='width:24px;border-right:1px solid #CCC;border-bottom:1px solid #999;'></th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>"+lp.logic+"</th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>"+lp.path+"</th>" +
                "<th style='border-right:1px solid #CCC;border-left:1px solid #FFF;border-bottom:1px solid #999;'>"+lp.compare+"</th>" +
                "<th style='border-left:1px solid #FFF;border-bottom:1px solid #999;'>"+lp.value+"</th>";
            tr.set("html", html);
            var addActionNode = new Element("div", {"styles": this.module.css.filterAddActionNode}).inject(tr.getFirst("th"));
            addActionNode.addEvent("click", function () {
                this.addFilter(table);
            }.bind(this));

            if (this.data.filterList) {
                this.data.filterList.each(function (op) {
                    new MWF.xApplication.query.ViewDesigner.Property.Filter(op, table, this);
                }.bind(this));
            }
        }.bind(this));
    },
    addFilter: function (table) {
        op = {
            "logic": "and",
            "comparison": "",
            "value": ""
        }
        if (!this.data.filterList) this.data.filterList = [];
        this.data.filterList.push(op);
        var filter = new MWF.xApplication.query.ViewDesigner.Property.Filter(op, table, this);
        filter.editMode();
    },
    loadViewStylesArea: function () {
        var _self = this;
        var viewAreas = this.propertyContent.getElements(".MWFViewStylesArea");
        viewAreas.each(function (node) {
            var name = node.get("name");

            var d = this.data;
            Array.each(name.split("."), function (n) {
                if (d) d = d[n];
            });
            var viewStyles = d || {};
            MWF.require("MWF.widget.Maplist", function () {
                var maps = [];
                Object.each(viewStyles, function (v, k) {
                    var mapNode = new Element("div").inject(node);
                    mapNode.empty();

                    var maplist = new MWF.widget.Maplist(mapNode, {
                        "title": k,
                        "collapse": true,
                        "onChange": function () {
                            // var oldData = _self.data[name];
                            var oldData = this.data;
                            Array.each(name.split("."), function (n) {
                                if (oldData) oldData = oldData[n];
                            });
                            maps.each(function (o) {
                                d[o.key] = o.map.toJson();
                            }.bind(this));
                            _self.changeData(name, node, oldData);
                        }
                    });
                    maps.push({"key": k, "map": maplist});
                    maplist.load(v);
                }.bind(this));
            }.bind(this));


        }.bind(this));
    },
    loadMaplist: function(){
        var maplists = this.propertyContent.getElements(".MWFMaplist");
        debugger;
        maplists.each(function(node){
            var title = node.get("title");
            var name = node.get("name");
            var lName = name.toLowerCase();
            var collapse = node.get("collapse");
            var mapObj = this.data[name];
            if (!mapObj) mapObj = {};
            MWF.require("MWF.widget.Maplist", function(){
                node.empty();
                var maplist = new MWF.widget.Maplist(node, {
                    "title": title,
                    "collapse": (collapse) ? true : false,
                    "onChange": function(){
                        //this.data[name] = maplist.toJson();
                        //
                        var oldData = this.data[name];
                        this.changeJsonDate(name, maplist.toJson());
                        this.changeStyle(name, oldData);
                        this.changeData(name);
                    }.bind(this),
                    "onDelete": function(key){
                        debugger;

                        this.module.deletePropertiesOrStyles(name, key);
                    }.bind(this),
                    "isProperty": (lName.contains("properties") || lName.contains("property") || lName.contains("attribute"))
                });
                maplist.load(mapObj);
                this.maplists[name] = maplist;
            }.bind(this));
        }.bind(this));
    },
    loadActionStylesArea: function () {
        var _self = this;
        var actionAreas = this.propertyContent.getElements(".MWFActionStylesArea");
        actionAreas.each(function (node) {
            var name = node.get("name");
            var actionStyles = this.data[name];
            MWF.require("MWF.widget.Maplist", function () {
                var maps = [];
                Object.each(actionStyles, function (v, k) {
                    var mapNode = new Element("div").inject(node);
                    mapNode.empty();

                    var maplist = new MWF.widget.Maplist(mapNode, {
                        "title": k,
                        "collapse": true,
                        "onChange": function () {
                            var oldData = _self.data[name];
                            maps.each(function (o) {
                                _self.data[name][o.key] = o.map.toJson();
                            }.bind(this));
                            _self.changeData(name, node, oldData);
                        }
                    });
                    maps.push({"key": k, "map": maplist});
                    maplist.load(v);
                }.bind(this));
            }.bind(this));


        }.bind(this));
    },
    loadPagingStylesArea: function () {
        var _self = this;
        var pagingAreas = this.propertyContent.getElements(".MWFPagingStylesArea");
        pagingAreas.each(function (node) {
            var name = node.get("name");
            var pagingStyles = this.data[name];
            MWF.require("MWF.widget.Maplist", function () {
                var maps = [];
                Object.each(pagingStyles, function (v, k) {
                    var mapNode = new Element("div").inject(node);
                    mapNode.empty();

                    var maplist = new MWF.widget.Maplist(mapNode, {
                        "title": k,
                        "collapse": true,
                        "onChange": function () {
                            var oldData = _self.data[name];
                            maps.each(function (o) {
                                _self.data[name][o.key] = o.map.toJson();
                            }.bind(this));
                            _self.changeData(name, node, oldData);
                        }
                    });
                    maps.push({"key": k, "map": maplist});
                    maplist.load(v);
                }.bind(this));
            }.bind(this));


        }.bind(this));
    },
    loadEventsEditor: function () {
        MWF.xApplication.process = MWF.xApplication.process || {};
        MWF.APPFD = MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
        MWF.xDesktop.requireApp("process.FormDesigner", "lp." + o2.language, null, false);

        var events = this.propertyContent.getElement(".MWFEventsArea");
        if (events) {
            var name = events.get("name");
            var eventsObj = this.data;
            Array.each(name.split("."), function (n) {
                if (eventsObj) eventsObj = eventsObj[n];
            })
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.EventsEditor", function () {
                var eventsEditor = new MWF.xApplication.process.FormDesigner.widget.EventsEditor(events, this.designer, {
                    //"maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "maxObj": this.designer.contentNode
                });
                eventsEditor.load(eventsObj, this.data, name);
            }.bind(this));
        }
    },
    loadActionArea: function () {
        debugger;
        MWF.xApplication.process = MWF.xApplication.process || {};
        MWF.APPFD = MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
        MWF.xDesktop.requireApp("process.FormDesigner", "lp." + o2.language, null, false);

        var multiActionArea = this.propertyContent.getElements(".MWFMultiActionArea");
        multiActionArea.each(function(node){
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function(){
                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                    "systemToolsAddress": "../x_component_query_ViewDesigner/$View/toolbars.json",
                    "isSystemTool" : true,
                    "noEditShow": true,
                    "noReadShow": true,
                    "onChange": function(){
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));
        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFActionArea");
        actionAreas.each(function (node) {
            var name = node.get("name");
            var actionContent = this.data[name];
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {

                // debugger;
                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "noCreate": true,
                //     "noDelete": true,
                //     "noCode": true,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(this.module.defaultToolBarsData);

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement,
                    "noEditShow": true,
                    "noReadShow": true,
                    "onChange": function () {
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

        var actionAreas = this.propertyContent.getElements(".MWFDefaultActionArea");
        actionAreas.each(function (node) {
            var name = node.get("name");
            var actionContent = this.data[name] || this.module.defaultToolBarsData;
            MWF.xDesktop.requireApp("process.FormDesigner", "widget.ActionsEditor", function () {

                var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, this.data, {
                    "maxObj": this.propertyNode.parentElement.parentElement,
                    "isSystemTool": true,
                    "systemToolsAddress": "../x_component_query_ViewDesigner/$View/toolbars.json",
                    "noCreate": true,
                    "noDelete": false,
                    "noCode": true,
                    "noReadShow": true,
                    "noEditShow": true,
                    "onChange": function () {
                        this.data[name] = actionEditor.data;
                        this.changeData(name);
                    }.bind(this)
                });
                actionEditor.load(actionContent);

                // var actionEditor = new MWF.xApplication.process.FormDesigner.widget.ActionsEditor(node, this.designer, {
                //     "maxObj": this.propertyNode.parentElement.parentElement.parentElement,
                //     "onChange": function(){
                //         this.data[name] = actionEditor.data;
                //     }.bind(this)
                // });
                // actionEditor.load(actionContent);
            }.bind(this));

        }.bind(this));

    },
    loadStylesList: function () {
        var styleSelNodes = this.propertyContent.getElements(".MWFViewStyle");
        styleSelNodes.each(function (node) {
            debugger;
            if (this.module.stylesList) {
                if (!this.data.data.viewStyleType) this.data.data.viewStyleType = "default";
                // var mode = ( this.form.options.mode || "" ).toLowerCase() === "mobile" ? "mobile" : "pc";
                Object.each(this.module.stylesList, function (s, key) {
                    // if( s.mode.contains( mode ) ){
                    new Element("option", {
                        "text": s.name,
                        "value": key,
                        "selected": ((!this.data.data.viewStyleType && key == "default") || (this.data.data.viewStyleType == key))
                    }).inject(node)
                    // }
                }.bind(this));
            } else {
                node.getParent("tr").setStyle("display", "none");
            }
        }.bind(this));
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

MWF.xApplication.query.ViewDesigner.Property.Filter = new Class({
    Implements: [Events],
    initialize: function (json, table, property) {
        this.property = property;
        this.module = property.module;
        this.table = table;
        this.data = json;

        this.load();
    },
    load: function () {
        var lp =  MWF.APPDVD.LP.filter;
        this.node = new Element("tr", {"styles": this.module.css.filterTableTd}).inject(this.table);
        var html = "<td style='widtd:24px;border-right:1px solid #CCC;border-bottom:1px solid #999;'></td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999; width:60px'>" + this.data.logic + "</td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999; width:30px'>"+lp.columnValue+"</td>" +
            "<td style='padding:3px;border-right:1px solid #CCC;border-bottom:1px solid #999;'>" + this.data.comparison + "</td>" +
            "<td style='padding:3px;border-bottom:1px solid #999;'>" + this.data.value + "</td>";
        this.node.set("html", html);
        var tds = this.node.getElements("td");

        this.delActionNode = new Element("div", {"styles": this.module.css.filterDelActionNode}).inject(tds[0]);
        this.delActionNode.addEvent("click", function (e) {
            this.delFilter(e);
            e.stopPropagation();
        }.bind(this));

        this.logicNode = tds[1];
        this.comparisonNode = tds[3];
        this.valueNode = tds[4];

        this.node.addEvent("click", function () {
            if (!this.isEditMode) this.editMode();
        }.bind(this));
        this.node.addEvent("blur", function () {
            if (this.isEditMode) this.readMode();
        }.bind(this));
    },
    delFilter: function (e) {
        var _self = this;
        this.property.designer.confirm("warn", e, MWF.APPDVD.LP.notice.deleteFilterTitle, MWF.APPDVD.LP.notice.deleteFilter, 300, 120, function () {

            _self.node.destroy();
            _self.property.data.filterList.erase(_self.data);
            MWF.release(_self);

            this.close();
        }, function () {
            this.close();
        }, null);
    },
    editMode: function () {
        if (this.property.editModeFilter) {
            if (this.property.editModeFilter != this) this.property.editModeFilter.readMode();
        }

        var width = this.logicNode.getSize().x - 9;
        this.logicNode.empty();
        var logicSelect = new Element("select", {"styles": {"width": "90%"}}).inject(this.logicNode);
        var html = "";
        if (this.data.logic == "and") {
            html = "<option value=\"and\" selected>and</option><option value=\"or\">or</option>";
        } else {
            html = "<option value=\"and\">and</option><option value=\"or\" selected>or</option>";
        }
        logicSelect.set("html", html);
        logicSelect.addEvent("change", function () {
            this.data.logic = logicSelect.options[logicSelect.selectedIndex].value;
        }.bind(this));

        width = this.comparisonNode.getSize().x - 9;
        this.comparisonNode.empty();
        var comparisonSelect = new Element("select", {"styles": {"width": "90%"}}).inject(this.comparisonNode);
        var lp =  MWF.APPDVD.LP.filter;
        html = "";
        switch (this.property.data.type) {
            case "text":
                html += "<option value=''></option><option value='==' " + ((this.data.comparison == "==") ? "selected" : "") + ">"+lp.equals+"(==)</option>" +
                    "<option value='!=' " + ((this.data.comparison == "!=") ? "selected" : "") + ">"+lp.notEquals+"(!=)</option>" +
                    "<option value='@' " + ((this.data.comparison == "@") ? "selected" : "") + ">"+lp.contain+"(@)</option>";
                break;
            case "date":
                html += "<option value=''></option><option value='&gt;' " + ((this.data.comparison == ">") ? "selected" : "") + ">"+lp.greaterThan+"(&gt;)</option>" +
                    "<option value='&lt;' " + ((this.data.comparison == "<") ? "selected" : "") + ">"+lp.lessThan+"(&lt;)</option>" +
                    "<option value='&gt;=' " + ((this.data.comparison == ">=") ? "selected" : "") + ">"+lp.greaterThanOrEqualTo+"(&gt;=)</option>" +
                    "<option value='&lt;=' " + ((this.data.comparison == "<=") ? "selected" : "") + ">"+lp.lessThanOrEqualTo+"(&lt;=)</option>";
                break;
            case "number":
                html += "<option value=''></option><option value='==' " + ((this.data.comparison == "==") ? "selected" : "") + ">"+lp.equals+"(==)</option>" +
                    "<option value='!=' " + ((this.data.comparison == "!=") ? "selected" : "") + ">"+lp.notEquals+"(!=)</option>" +
                    "<option value='&gt;' " + ((this.data.comparison == ">") ? "selected" : "") + ">"+lp.greaterThan+"(&gt;)</option>" +
                    "<option value='&lt;' " + ((this.data.comparison == "<") ? "selected" : "") + ">"+lp.lessThan+"(&lt;)</option>" +
                    "<option value='&gt;=' " + ((this.data.comparison == ">=") ? "selected" : "") + ">"+lp.greaterThanOrEqualTo+"(&gt;=)</option>" +
                    "<option value='&lt;=' " + ((this.data.comparison == "<=") ? "selected" : "") + ">"+lp.lessThanOrEqualTo+"(&lt;=)</option>";
                break;
            case "boolean":
                html += "<option value=''></option><option value='==' " + ((this.data.comparison == "==") ? "selected" : "") + ">"+lp.equals+"(==)</option>" +
                    "<option value='!=' " + ((this.data.comparison == "!=") ? "selected" : "") + ">"+lp.notEquals+"(!=)</option>";
                break;
        }
        comparisonSelect.set("html", html);
        comparisonSelect.addEvent("change", function () {
            this.data.comparison = comparisonSelect.options[comparisonSelect.selectedIndex].value;
        }.bind(this));


        width = this.valueNode.getSize().x - 9;
        this.valueNode.empty();
        var type = "text";
        switch (this.property.data.type) {
            case "date":
                var valueInput = new Element("input", {
                    "styles": {"width": "90%"},
                    "type": "text",
                    "value": this.data.value
                }).inject(this.valueNode);

                MWF.require("MWF.widget.Calendar", function () {
                    this.calendar = new MWF.widget.Calendar(valueInput, {
                        "style": "xform",
                        "isTime": true,
                        "secondEnable": true,
                        "target": this.property.designer.content,
                        "format": "%Y-%m-%d %H:%M:%S"
                    });
                    //this.calendar.show();
                }.bind(this));

                break;
            case "number":
                var valueInput = new Element("input", {
                    "styles": {"width": "90%"},
                    "type": "number",
                    "value": this.data.value
                }).inject(this.valueNode);
                break;
            case "boolean":
                var valueInput = new Element("select", {
                    "styles": {"width": "" + width + "px"},
                    "html": "<option value=\"\"></option><option value=\"true\" " + ((this.data.value) ? "selected" : "") + ">true</option><option value=\"false\" " + ((!this.data.value) ? "selected" : "") + ">false</option>"
                }).inject(this.valueNode);
                break;
            default:
                var valueInput = new Element("input", {
                    "styles": {"width": "90%"},
                    "type": "text",
                    "value": this.data.value
                }).inject(this.valueNode);
        }
        if (valueInput.tagName.toLowerCase() == "select") {
            valueInput.addEvent("change", function () {
                var v = valueInput.options[valueInput.selectedIndex].value;
                this.data.value = (v = "true") ? true : false;
            }.bind(this));
        } else {
            valueInput.addEvent("change", function (e) {
                this.data.value = valueInput.get("value");
            }.bind(this));
            valueInput.addEvent("blur", function (e) {
                this.data.value = valueInput.get("value");
            }.bind(this));
            valueInput.addEvent("keydown", function (e) {
                if (e.code == 13) {
                    this.data.value = valueInput.get("value");
                    this.readMode();
                }
                e.stopPropagation();
            }.bind(this));
        }
        this.isEditMode = true;
        this.property.editModeFilter = this;
    },
    readMode: function () {
        if (this.isEditMode) {
            var logicSelect = this.logicNode.getElement("select");
            this.data.logic = logicSelect.options[logicSelect.selectedIndex].value;

            var comparisonSelect = this.comparisonNode.getElement("select");
            this.data.comparison = comparisonSelect.options[comparisonSelect.selectedIndex].value;

            var valueInput = this.valueNode.getFirst();
            if (valueInput.tagName.toLowerCase() == "select") {
                var v = valueInput.options[valueInput.selectedIndex].value;
                this.data.value = (v = "true") ? true : false;
            } else {
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