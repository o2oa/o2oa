MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementStatDesigner = MWF.xApplication.query.StatementStatDesigner || {};
MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;

MWF.xDesktop.requireApp("query.StatementStatDesigner", "lp." + MWF.language, null, false);
MWF.xDesktop.requireApp("query.StatementStatDesigner", "Property", null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "View", null, false);
o2.require("o2.widget.JavascriptEditor", null, false);
o2.require("o2.widget.UUID", null, false);


MWF.xApplication.query.StatementStatDesigner.StatementStat = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_StatementStatDesigner/$StatementStat/stat.html"
    },
    initialize: function (designer, data, options) {
        this.setOptions(options);

        this.path = "../x_component_query_StatementStatDesigner/$StatementStat/";
        this.cssPath = "../x_component_query_StatementStatDesigner/$StatementStat/" + this.options.style + "/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        this.parseData();

        this.node = this.designer.designNode;
        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        //this.statementRunNode = this.designer.designerStatementArea;

        if (this.designer.application) this.data.applicationName = this.designer.application.name;
        if (this.designer.application) this.data.application = this.designer.application.id;

        this.isNewStatement = (this.data.id) ? false : true;

        this.view = this;

        this.autoSave();
        this.designer.addEvent("queryClose", function () {
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    parseData: function () {
        this.json = this.data;
        if (!this.json.id) {
            this.json.id = (new o2.widget.UUID).id;
        }
        if (!this.json.data || !this.json.data.events || !this.json.data.calculate ) {
            var url = "../x_component_query_StatementStatDesigner/$StatementStat/stat.json";
            MWF.getJSON(url, {
                "onSuccess": function (obj) {
                    if (!this.json.data || o2.typeOf(this.json.data) !== "object" ) this.json.data = obj.data;
                    if (!this.json.data.events) this.json.data.events = obj.data.events;
                    if (!this.json.data.calculate) this.json.data.calculate = obj.data.calculate;
                }.bind(this),
                "onerror": function (text) {
                    this.notice(text, "error");
                }.bind(this),
                "onRequestFailure": function (xhr) {
                    this.notice(xhr.responseText, "error");
                }.bind(this)
            }, false);
        }
    },
    autoSave: function () {
        this.autoSaveTimerID = window.setInterval(function () {
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFAutoSaveCheck");
            if (this.autoSaveCheckNode) {
                if (this.autoSaveCheckNode.get("checked")) {
                    this.save();
                }
            }
        }.bind(this), 60000);
    },
    getDefaultEditorData: function(){
        return {
            "javascriptEditor": {
                "monaco_theme": "vs",
                "fontSize" : "12px",
                "editor": "monaco"
            }
        };
        // return {
        //     "javascriptEditor": {
        //         "theme": "tomorrow",
        //         "fontSize" : "12px",
        //         "editor": "ace"
        //     }
        // };
    },
    getEditorTheme: function(callback){
        if (!o2.editorData){
            o2.UD.getData("editor", function(json){
                if (json.data){
                    o2.editorData = JSON.decode(json.data);
                }else{
                    o2.editorData = this.getDefaultEditorData();
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    load : function(){
        this.getEditorTheme( function () {
            this._load();
        }.bind(this))
    },
    _load: function () {

        // this.setAreaNodeSize();
        // this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.designer.statementListAreaNode.getChildren().each(function (node) {
            var statementStat = node.retrieve("statementStat");
            if (statementStat.id == this.data.id) {
                if (this.designer.currentListStatementItem) {
                    this.designer.currentListStatementItem.setStyles(this.designer.css.listStatementItem);
                }
                node.setStyles(this.designer.css.listStatementItem_current);
                this.designer.currentListStatementItem = node;
                this.lisNode = node;
            }
        }.bind(this));

        this.loadStatement();
        // this.showProperty();
        this.selected();
    },
    selected: function () {
        if (this.currentSelectedModule) {
            if (this.currentSelectedModule == this) {
                return true;
            } else {
                this.currentSelectedModule.unSelected();
            }
        }
        if (this.stat && this.stat.domListNode) {
            this.stat.domListNode.hide();
        }

        this.selectMode = "statementStat";
        this.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
        this.designer.setDesignerStatementResize();
    },
    unSelected: function () {
        this.currentSelectedModule = null;
        this.isSelected = false;
        this.hideProperty();
    },
    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementStatDesigner.Property(this, this.designer.designerContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function () {
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        } else {
            this.property.show();
        }
    },
    hideProperty: function () {
        if (this.property) this.property.hide();
    },
    // loadTab: function (callback) {
    //     var _self = this;
    //     MWF.require("MWF.widget.Tab", null, false);
    //
    //     this.tab = new MWF.widget.Tab(this.tabNode, {"style": "script"});
    //     this.tab.load();
    //
    //     this.tabRunNode = Element("div");
    //     this.pageRunNode = new Element("div", {
    //         "styles": {
    //             "overflow": "auto",
    //             "background-color": "#fff"
    //         }
    //     }).inject(this.tabRunNode);
    //     this.runArea.inject(this.pageRunNode);
    //
    //     this.tabStatNode = Element("div", {"styles": {"height": "100%"}});
    //     this.pageStatNode = new Element("div.pageStatNode").inject(this.tabStatNode);
    //     this.statArea.inject(this.pageStatNode);
    //
    //     this.runPage = this.tab.addTab(this.tabRunNode, this.designer.lp.runTest);
    //     this.statPage = this.tab.addTab(this.tabStatNode, this.designer.lp.stat);
    //
    //     this.runPage.showTabIm();
    //
    //     this.statPage.addEvent("postShow", function () {
    //         if (this.stat) {
    //             this.stat.setContentHeight();
    //             this.stat.selected();
    //         }
    //     }.bind(this));
    //     this.runPage.addEvent("postShow", function () {
    //         this.selected();
    //     }.bind(this));
    // },
    loadStatement: function(){
        this.loadStatementHtml(function () {

        });
    },
    selectStatement: function(){
        debugger;
        o2.requireApp("Selector", "package", null, false);
        new MWF.O2Selector( this.designer.content, {
            type: "QueryStatement",
            inViewCategory: this.data.application
        })
    },
    // loadStatement: function () {
    //     this.loadStatementHtml(function () {
    //         this.designerArea = this.areaNode.getElement(".o2_statement_statementDesignerNode");
    //
    //         this.jpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerJpql");
    //         this.scriptArea = this.areaNode.getElement(".o2_statement_statementDesignerScript");
    //
    //         this.formatTypeArea = this.areaNode.getElement(".o2_statement_statementDesignerFormatContent");
    //         this.entityCategorySelect = this.areaNode.getElement(".o2_statement_statementDesignerCategoryContent").getElement("select");
    //
    //         this.dynamicTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_dynamic");
    //         this.officialTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_official");
    //         this.customTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_custom");
    //
    //         this.dynamicTableSelect = this.areaNode.getElement(".o2_statement_statementDesignerSelectTable");
    //         this.officialTableSelect = this.officialTableArea.getElement("select");
    //
    //         this.fieldSelect = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_field").getElement("select");
    //         this.loadFieldSelect();
    //
    //         this.dynamicTableContent = this.areaNode.getElement(".o2_statement_statementDesignerTableContent");
    //
    //         this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");
    //
    //         this.resizeNode = this.areaNode.getElement(".o2_statement_resizeNode");
    //
    //         this.tabNode = this.areaNode.getElement(".o2_statement_tabNode");
    //
    //         this.runArea = this.areaNode.getElement(".o2_statement_statementRunNode");
    //         // this.runTitleNode = this.areaNode.getElement(".o2_statement_statementRunTitleNode");
    //         this.runContentNode = this.areaNode.getElement(".o2_statement_statementRunContentNode");
    //         this.runJsonNode = this.runContentNode.getFirst();
    //         this.runActionNode = this.runJsonNode.getNext();
    //         this.runResultNode = this.runContentNode.getLast();
    //         this.setRunnerSize();
    //         this.designer.addEvent("resize", this.setRunnerSize.bind(this));
    //         if (this.json.format == "script") {
    //             this.loadStatementScriptEditor();
    //         } else {
    //             this.loadStatementEditor();
    //         }
    //         this.loadStatementRunner();
    //
    //         this.statArea = this.areaNode.getElement(".o2_statement_statNode");
    //         this.loadStat();
    //
    //         // this.loadTab();
    //
    //         this.setEvent();
    //         this.loadVerticalResize();
    //     }.bind(this));
    // },

    loadVerticalResize: function(){
        this.verticalResize = new Drag(this.resizeNode, {
            "snap": 10,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.designerArea.getSize(); //designerArea
                el.store("initialHeight", size.y);

                var allSize = this.areaNode.getSize();
                el.store("initialAllHeight", allSize.y);
            }.bind(this),
            "onDrag": function(el, e){

                var allHeight = el.retrieve("initialAllHeight").toFloat(); //this.areaNode.getSize();

                //			var x = e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                var position = el.retrieve("position");
                var dy = y.toFloat()-position.y.toFloat();

                var initialHeight = el.retrieve("initialHeight").toFloat();
                var height = initialHeight+dy;
                if (height < 180) height = 180;
                if (height > allHeight-180) height = allHeight-180;

                this.designerAreaPercent = height/allHeight;

                this.setVerticalResize();

            }.bind(this)
        });
    },
    setVerticalResize: function(){
        var size = this.areaNode.getSize();

        var height = size.y;

        var designAreaHeight = this.designerAreaPercent*height - 52;
        // var runAreaHeight = height-designAreaHeight;

        this.designerArea.setStyle("height", ""+designAreaHeight+"px");

        debugger;

        var editorHeight = designAreaHeight - 98;

        if(this.jpqlEditorNode)this.jpqlEditorNode.setStyle( "height", ""+editorHeight+"px" );
        // if(this.countJpqlEditorNode)this.countJpqlEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.scriptArea)this.scriptArea.setStyle( "height", ""+editorHeight+"px" );
        // if(this.countScriptArea)this.countScriptArea.setStyle( "height", ""+editorHeight+"px" );

        if( this.editor )this.editor.resize();
        // if( this.countEditor )this.countEditor.resize();
        if( this.scriptEditor ){
            this.scriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.scriptEditor.resizeContentNodeSize();
        }
        // if( this.countScriptEditor ){
        //     this.countScriptEditor.container.setStyle("height", ""+editorHeight+"px");
        //     this.countScriptEditor.resizeContentNodeSize();
        // }

        // this.tabNode.setStyle("height", ""+runAreaHeight+"px");
        this.setRunnerSize();
        if( this.stat ){
            this.setStatSize();
            this.stat.setContentHeight()
        }
    },
    loadStatementScriptEditor: function () {
        if (!this.scriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.scriptEditor = new o2.widget.ScriptArea(this.scriptArea, {
                    "isbind": false,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.scriptTitle,
                    "type": "service",
                    "onChange": function () {
                        this.json.scriptText = this.scriptEditor.toJson().code;
                    }.bind(this)
                });
                this.scriptEditor.load({"code": this.json.scriptText})
            }.bind(this), false);
        }
    },
    //
    // setRunnerSize: function () {
    //     debugger;
    //     var size = this.areaNode.getSize();
    //     var designerSize = this.designerArea.getComputedSize();
    //     var reizeNodeSize = this.resizeNode.getComputedSize();
    //
    //     var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight;
    //     var mTop = this.runArea.getStyle("margin-top").toInt();
    //     var mBottom = this.runArea.getStyle("margin-bottom").toInt();
    //     var pTop = this.runArea.getStyle("padding-top").toInt();
    //     var pBottom = this.runArea.getStyle("padding-bottom").toInt();
    //     y = y - mTop - mBottom - pTop - pBottom - 5;
    //
    //     var tabSize = this.tabNode.getComputedSize();
    //     y = y - tabSize.totalHeight;
    //
    //     this.runArea.setStyle("height", "" + y + "px");
    //
    //     // var titleSize = this.runTitleNode.getComputedSize();
    //     // y = y - titleSize.totalHeight;
    //
    //     this.runContentNode.setStyle("height", "" + y + "px");
    // },
    loadStatementEditor: function () {
        if (!this.editor) {
            var value;
            if( !this.json.data ){
                var table = "table";
                // switch (this.json.type) {
                //     case "update":
                //         value = "UPDATE " + table + " o SET ";
                //         break;
                //     case "delete":
                //         value = "DELETE " + table + " o WHERE ";
                //         break;
                //     default:
                //         value = "SELECT o FROM " + table + " o";
                // }
                this.json.data = "SELECT o FROM " + table + " o";
            }
            if( this.jpqlEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadStatementEditor();
                    this.jpqlPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.jpqlPage.addEvent("postShow", postShowFun);
            }else{
                this._loadStatementEditor();
            }
        }
    },
    _loadStatementEditor: function () {
        if (!this.editor) {
            o2.require("o2.widget.JavascriptEditor", function () {
                this.editor = new o2.widget.JavascriptEditor(this.jpqlEditorNode, {
                    "title": "JPQL",
                    "option": {"mode": "sql"}
                });
                this.editor.load(function () {
                    // if (this.json.data) {
                        this.editor.editor.setValue(this.json.data);
                    // } else {
                    //     var table = "table";
                    //     switch (this.json.type) {
                    //         case "update":
                    //             this.editor.editor.setValue("UPDATE " + table + " o SET ");
                    //             break;
                    //         case "delete":
                    //             this.editor.editor.setValue("DELETE " + table + " o WHERE ");
                    //             break;
                    //         default:
                    //             this.editor.editor.setValue("SELECT o FROM " + table + " o");
                    //     }
                    // }
                    // this.json.data = this.editor.editor.getValue();

                    this.editor.addEditorEvent("change", function () {
                        debugger;
                        this.data.data = this.editor.getValue();
                        // this.checkJpqlType();
                    }.bind(this));

                }.bind(this));
            }.bind(this), false);
        }

    },
    // setSatementTable: function () {
    //     if (!this.json.type) this.json.type = "select";
    //     // this.changeType(this.json.type, true);
    //     if( this.editor && this.editor.editor){
    //         if (this.json.data) {
    //             this.editor.editor.setValue(this.json.data);
    //         } else {
    //             var table = (this.json.tableObj) ? this.json.tableObj.name : "table";
    //             // switch (this.json.type) {
    //             //     case "update":
    //             //         this.editor.editor.setValue("UPDATE " + table + " o SET ");
    //             //         break;
    //             //     case "delete":
    //             //         this.editor.editor.setValue("DELETE " + table + " o WHERE ");
    //             //         break;
    //             //     default:
    //                     this.editor.editor.setValue("SELECT o FROM " + table + " o");
    //             // }
    //         }
    //     }
    // },


    loadStatementHtml: function (callback) {
        this.areaNode.loadAll({
            "css": this.path + this.options.style + "/statement.css",
            "html": this.path + "statementDesigner.html"
        }, {
            "bind": {"lp": this.designer.lp, "data": this.data},
            "module": this
        }, function () {
            if (callback) callback();
        }.bind(this));
    },
    loadStatementRunner: function () {
        o2.require("o2.widget.JavascriptEditor", function () {
            this.jsonEditor = new o2.widget.JavascriptEditor(this.runJsonNode, {
                "title": "JPQL",
                "option": {"mode": "json"}
            });
            this.jsonEditor.load(function () {
                this.jsonEditor.editor.setValue(this.data.testParameters || "{}");
            }.bind(this));
        }.bind(this), false);
    },
    setEvent: function () {
        this.designerArea.addEvent("click", function (e) {
            this.selected();
            e.stopPropagation();
        }.bind(this));
        // this.formatTypeArea.getElements("input").addEvent("click", function (e) {
        //     if (e.target.checked) {
        //         var v = e.target.get("value");
        //         if (v === "script") {
        //             this.scriptArea.show();
        //             this.jpqlArea.hide();
        //             this.loadStatementScriptEditor();
        //
        //         } else {
        //             this.scriptArea.hide();
        //             this.jpqlArea.show();
        //             this.loadStatementEditor();
        //
        //         }
        //         this.json.format = v;
        //     }
        // }.bind(this));
        // this.entityCategorySelect.addEvent("change", function (e) {
        //     var entityCategory = e.target.options[e.target.selectedIndex].value;
        //     switch (entityCategory) {
        //         case "dynamic":
        //             this.officialTableArea.hide();
        //             this.dynamicTableArea.show();
        //             this.customTableArea.hide();
        //             break;
        //         case "custom":
        //             this.officialTableArea.hide();
        //             this.dynamicTableArea.hide();
        //             this.customTableArea.show();
        //             break;
        //         default:
        //             this.officialTableArea.show();
        //             this.dynamicTableArea.hide();
        //             this.customTableArea.hide();
        //             break;
        //     }
        //     this.json.entityCategory = entityCategory;
        //     // this.loadJpqlTypeSelect();
        //     this.loadFieldSelect();
        //     if(this.stat && this.stat.property && this.stat.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();
        // }.bind(this));
        //@todo change table
        // this.officialTableSelect.addEvent("change", function (e) {
        //     debugger;
        //     var entityClassName = e.target.options[e.target.selectedIndex].value;
        //     this.json.entityClassName = entityClassName;
        //     if( entityClassName ){
        //         this.changeEditorEntityClassName( entityClassName.split(".").getLast() );
        //     }
        //     this.loadFieldSelect();
        //
        //     this.json.table = "";
        //     this.json.tableObj = null;
        //
        //     if(this.stat && this.stat.property && this.stat.property.viewFilter)this.stat.property.viewFilter.setPathInputSelectOptions();
        //
        //
        // }.bind(this));

        // this.runActionNode.getFirst().addEvent("click", this.runStatement.bind(this));
        //
        // this.dynamicTableSelect.addEvent("click", this.selectTable.bind(this));
        //
        // this.fieldSelect.addEvent("change", function (ev) {
        //     var option = ev.target.options[ev.target.selectedIndex];
        //     var type = option.retrieve("type");
        //     var field = option.retrieve("field");
        //     if( !field )return;
        //     var text = field.name;
        //         if( this.data.format === "script" && this.scriptEditor.jsEditor ){
        //             this.scriptEditor.jsEditor.insertValue( text );
        //         }else if( this.editor ){
        //             this.editor.insertValue( text );
        //         }
        // }.bind(this))
    },
    // changeEditorEntityClassName : function( entityClassName ){
    //     if (this.json.format == "jpql") {
    //         if (this.editor) {
    //             var re = /(.*from\s*)/ig;
    //             var v = this.json.data;
    //
    //             var re2 = /(\s+)/ig;
    //             var arr = re.exec(v);
    //             if (arr && arr[0]) {
    //                 var left = arr[0]
    //                 v = v.substring(left.length, v.length);
    //                 //var ar = re2.exec(v);
    //                 var right = v.substring(v.indexOf(" "), v.length);
    //                 this.json.data = left + entityClassName + right;
    //                 this.editor.editor.setValue(this.json.data);
    //             }
    //         }
    //
    //     }
    // },


    // runStatement: function () {
    //
    //     this.saveSilence(function () {
    //         debugger;
    //         this.execute(function (json) {
    //             this.executeData = json;
    //             o2.require("o2.widget.JsonParse", function () {
    //                 this.runResultNode.empty();
    //                 var jsonResult = new o2.widget.JsonParse(json, this.runResultNode);
    //                 jsonResult.load();
    //             }.bind(this));
    //             if (this.stat) {
    //                 var flag = true;
    //                 // if (this.data.type !== "select") flag = false;
    //                 if (this.data.format === "script" && !this.data.scriptText) flag = false;
    //                 if (this.data.format !== "script" && !this.data.data) flag = false;
    //                 if (flag) this.stat.loadStatData();
    //             }
    //             this.setColumnDataPath(json);
    //         }.bind(this), function () {
    //         }.bind(this))
    //     }.bind(this));
    // },
    // setColumnDataPath: function (json) {
    //     if (this.data.format === "script" && !this.data.scriptText) return;
    //     if (this.data.format !== "script" && !this.data.data) return;
    //     this.columnDataPathList = [];
    //     debugger;
    //     var addPath = function (value, key) {
    //         if (typeOf(value) === "array") {
    //             Array.each(value, function (v, idx) {
    //                 var path = (key || typeOf(key) === "number") ? (key + "." + idx) : idx.toString();
    //                 if (!this.columnDataPathList.contains(path)) this.columnDataPathList.push(path);
    //                 if (typeOf(v) === "array" || typeOf(v) === "object") addPath(v, path);
    //             }.bind(this))
    //         } else if (typeOf(value) === "object") {
    //             Object.each(value, function (v, k) {
    //                 var path = (key || typeOf(key) === "number") ? (key + "." + k) : k;
    //                 if (!this.columnDataPathList.contains(path)) this.columnDataPathList.push(path);
    //                 if (typeOf(v) === "array" || typeOf(v) === "object") addPath(v, path);
    //                 addPath(v, path);
    //             }.bind(this))
    //         } else {
    //             // if( key && !this.columnDataPathList.indexOf(key) )this.columnDataPathList.push(key);
    //         }
    //     }.bind(this);
    //     for (var i = 0; i < json.data.length && i < 10; i++) {
    //         var d = json.data[i];
    //         addPath(d);
    //     }
    //     this.columnDataPathList.sort();
    //     if (this.stat && this.stat.items) {
    //         this.stat.items.each(function (column) {
    //             column.refreshColumnPathData()
    //         })
    //     }
    // },
    // getColumnDataPath: function () {
    //     return this.columnDataPathList || [];
    // },
    // execute: function (success, failure) {
    //     var json = this.jsonEditor.editor.getValue();
    //     var o = JSON.parse(json);
    //
    //     var mode = "data";
    //     o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.id, "data", 1, 50, o, function (json) {
    //         if (success) success(json)
    //     }.bind(this), function (xhr, text, error) {
    //         debugger;
    //         if (failure) failure();
    //         var errorText = error;
    //         if (xhr) {
    //             var json = JSON.decode(xhr.responseText);
    //             if (json) {
    //                 errorText = json.message.trim() || "request json error";
    //             } else {
    //                 errorText = "request json error: " + xhr.responseText;
    //             }
    //         }
    //         errorText = errorText.replace(/\</g, "&lt;");
    //         errorText = errorText.replace(/\</g, "&gt;");
    //         MWF.xDesktop.notice("error", {x: "right", y: "top"}, errorText);
    //     }.bind(this))
    // },

    save: function (callback) {
        debugger;
        if (!this.data.name) {
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }

        if (typeOf(this.statJson) === "object") {
            if(this.statJson.data && !this.statJson.data.group)this.statJson.data.group = {};
            if(!this.statJson.pageSize)this.statJson.pageSize = "20";
            this.data.stat = JSON.stringify(this.statJson);

            this.data.view = this.data.stat; //need delete
        }
        //if( !this.data.tableType ){
        //    this.data.tableType = "dynamic";
        //}
        if (this.editor) this.data.data = this.editor.editor.getValue();
        if (this.scriptEditor) this.data.scriptText = this.scriptEditor.toJson().code;
        if (this.jsonEditor) this.data.testParameters = this.jsonEditor.editor.getValue();

        this.designer.actions.saveStatement(this.data, function (json) {
            this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name + "(" + this.data.alias + ")");
            }
            if (callback) callback();
        }.bind(this));
    },
    _setEditStyle: function () {
    },

    saveSilence: function (callback) {
        if (!this.data.name) {
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }

        if (typeOf(this.statJson) === "object") {
            if(this.statJson.data && !this.statJson.data.group)this.statJson.data.group = {};
            if( !this.statJson.pageSize )this.statJson.pageSize = "20";
            this.data.stat = JSON.stringify(this.statJson);

            this.data.view = this.data.stat; //need delete
        }

        if (this.editor) this.data.data = this.editor.editor.getValue();
        if (this.scriptEditor) this.data.scriptText = this.scriptEditor.toJson().code;
        if (this.jsonEditor) this.data.testParameters = this.jsonEditor.editor.getValue();

        this.designer.actions.saveStatement(this.data, function (json) {
            //this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name + "(" + this.data.alias + ")");
            }
            if (callback) callback();
        }.bind(this));
    },

    loadStat: function (callback) {
        this.setStatSize();
        this.designer.addEvent("resize", this.setStatSize.bind(this));

        if (!this.data.view) {
            this.statJson = {};
        } else {
            this.statJson = JSON.parse(this.data.view)
        }
        this.stat = new MWF.xApplication.query.StatementStatDesigner.Stat(this.designer, this, this.statJson, {});
        this.view = this.stat;
        this.stat.load(function () {
            this.stat.setContentHeight();
        }.bind(this));
    },
    setStatSize: function () {
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.designerArea.getComputedSize();
        var reizeNodeSize = this.resizeNode.getComputedSize();

        var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight;
        var mTop = this.statArea.getStyle("margin-top").toInt();
        var mBottom = this.statArea.getStyle("margin-bottom").toInt();
        var pTop = this.statArea.getStyle("padding-top").toInt();
        var pBottom = this.statArea.getStyle("padding-bottom").toInt();
        y = y - mTop - mBottom - pTop - pBottom - 1;

        var tabSize = this.tabNode.getComputedSize();
        y = y - tabSize.totalHeight;

        this.statArea.setStyle("height", "" + y + "px");

        // var titleSize = this.runTitleNode.getComputedSize();
        // y = y - titleSize.totalHeight;
        // this.runContentNode.setStyle("height", ""+y+"px");
    },
    preview: function () {
        if (this.isNewStatement) {
            this.designer.notice(this.designer.lp.saveStatementNotice, "error");
            return;
        }
        // if (this.data.type !== "select") {
        //     this.designer.notice(this.designer.lp.previewNotSelectStatementNotice, "error");
        //     return;
        // }
        if (!this.data.stat) {
            this.designer.notice(this.designer.lp.noStatNotice, "error");

        }
        this.saveSilence(function () {
            var url = "../x_desktop/app.html?app=query.Query&status=";
            url += JSON.stringify({
                id: this.data.application,
                statementStatId: this.data.id
            });
            window.open(o2.filterUrl(url), "_blank");
        }.bind(this));
    }
});

MWF.xApplication.query.StatementStatDesigner.Stat = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_StatementStatDesigner/$StatementStat/stat.html"
    },

    initialize: function (designer, statementStat, data, options) {
        this.setOptions(options);

        this.path = "../x_component_query_ViewDesigner/$View/";
        this.cssPath = "../x_component_query_ViewDesigner/$View/" + this.options.style + "/css.wcss";

        this._loadCss();

        this.statementStat = statementStat;
        this.designer = designer;
        this.data = data;
        this.data.id = this.statementStat.data.id + "_stat";

        // this.parseData();

        this.node = this.statementStat.statArea;

        this.areaNode = new Element("div", {"styles": {"height": "calc(100% - 2px)", "overflow": "auto"}});
        this.areaNode.setStyles(this.css.areaNode);

        this.items = [];
        this.stat = this;

    },
    load: function (callback) {
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.domListNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.designer.propertyDomArea);

        this.loadTemplateStyle(function () {


            this.loadView();

            this.setEvent();

            //if (this.options.showTab) this.page.showTabIm();
            this.setViewWidth();

            this.designer.addEvent("resize", this.setViewWidth.bind(this));

            if (callback) callback();
        }.bind(this))
    },
    // parseData: function () {
    //     this.json = this.data;
    //     if (!this.json.id) {
    //         this.json.id = (new o2.widget.UUID).id;
    //     }
    //     if (!this.json.data || !this.json.data.events || !this.json.data.calculate ) {
    //         var url = "../x_component_query_StatementStatDesigner/$StatementStat/stat.json";
    //         MWF.getJSON(url, {
    //             "onSuccess": function (obj) {
    //                 if (!this.json.data) this.json.data = obj.data;
    //                 if (!this.json.data.events) this.json.data.events = obj.data.events;
    //                 if (!this.json.data.calculate) this.json.data.calculate = obj.data.calculate;
    //             }.bind(this),
    //             "onerror": function (text) {
    //                 this.notice(text, "error");
    //             }.bind(this),
    //             "onRequestFailure": function (xhr) {
    //                 this.notice(xhr.responseText, "error");
    //             }.bind(this)
    //         }, false);
    //     }
    // },
    setEvent: function () {
        this.areaNode.addEvents({
            "click": function (e) {
                this.selected();
                e.stopPropagation();
            }.bind(this),
            "mouseover": function () {
                if (!this.isSelected) this.areaNode.setStyles(this.css.areaNode_over)
            }.bind(this),
            "mouseout": function () {
                if (!this.isSelected) this.areaNode.setStyles(this.css.areaNode)
            }.bind(this)
        });
        this.refreshNode.addEvent("click", function (e) {
            this.statementStat.runStatement();
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function (e) {
            this.addColumn();
            e.stopPropagation();
        }.bind(this));
    },
    selected: function () {
        if (this.statementStat.currentSelectedModule) {
            if (this.statementStat.currentSelectedModule == this) {
                return true;
            } else {
                this.statementStat.currentSelectedModule.unSelected();
            }
        }
        this.areaNode.setStyles(this.css.areaNode_selected);
        this.statementStat.currentSelectedModule = this;
        this.statementStat.selectMode = "view";
        this.domListNode.show();
        this.isSelected = true;
        this.showProperty();
        this.statementStat.designer.setDesignerStatementResize();
    },
    unSelected: function () {
        this.statementStat.currentSelectedModule = null;
        this.isSelected = false;
        this.areaNode.setStyles(this.css.areaNode);
        this.hideProperty();
    },

    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementStatDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function () {
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        } else {
            this.property.show();
        }
    },
    hideProperty: function () {
        if (this.property) this.property.hide();
    },

    loadStatData: function () {
        debugger;
        if (this.data.id) {

            this.viewContentBodyNode.empty();
            this.viewContentTableNode = new Element("table", {
                "styles": this.css.viewContentTableNode,
                "border": "0px",
                "cellPadding": "0",
                "cellSpacing": "0"
            }).inject(this.viewContentBodyNode);

            var entries = {};

            this.json.data.selectList.each(function (entry) {
                entries[entry.column] = entry;
            }.bind(this));

            if (this.statementStat.executeData && this.statementStat.executeData.data && this.statementStat.executeData.data.length) {
                this.statementStat.executeData.data.each(function (line, idx) {
                    var tr = new Element("tr", {
                        "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTr"] : this.css.viewContentTrNode
                    }).inject(this.viewContentTableNode);

                    //this.createViewCheckboxTd( tr );

                    Object.each(entries, function (c, k) {
                        debugger;

                        var path = c.path, code = c.code, obj = line;
                        if( path ){
                            var pathList = path.split(".");
                            for( var i=0; i<pathList.length; i++ ){
                                var p = pathList[i];
                                if( (/(^[1-9]\d*$)/.test(p)) )p = p.toInt();
                                if( obj[ p ] ){
                                    obj = obj[ p ];
                                }else if(obj[p] === undefined || obj[p] === null) {
                                    obj = "";
                                    break;
                                } else {
                                    obj = obj[p];
                                    break;
                                }
                            }
                        }

                        if( code && code.trim())obj = MWF.Macro.exec( code, { "target" : {"value": obj,  "data": line, "entry": c} });

                        var toName = function (value) {
                            if(typeOf(value) === "array"){
                                Array.each( value, function (v, idx) {
                                    value[idx] = toName(v)
                                })
                            }else if( typeOf(value) === "object" ){
                                Object.each( value, function (v, key) {
                                    value[key] = toName(v);
                                })
                            }else if( typeOf( value ) === "string" ){
                                value = o2.name.cn( value )
                            }
                            return value;
                        };

                        var d;
                        if( obj!= undefined && obj!= null ){
                            if( typeOf(obj) === "array" ) {
                                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
                            }else if( typeOf(obj) === "object" ){
                                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
                            }else{
                                d = c.isName ? o2.name.cn( obj.toString() ) : obj;
                            }
                        }


                        if (d != undefined && d != null ) {
                            var td = new Element("td", {
                                "styles": this.css.viewContentTdNode
                            }).inject(tr);
                            if (c.isHtml) {
                                td.set("html", d);
                            } else {
                                td.set("text", d);
                            }
                        }
                    }.bind(this));
                }.bind(this));
                this.setContentColumnWidth();
                this.setContentHeight();
            } else if (this.json.data.noDataText) {
                var noDataTextNodeStyle = this.css.noDataTextNode;
                this.noDataTextNode = new Element("div", {
                    "styles": noDataTextNodeStyle,
                    "text": this.json.data.noDataText
                }).inject(this.viewContentBodyNode);
            }
            // }.bind(this));
            // }.bind(this));
        }
    },
    addColumn: function () {

        debugger;

        MWF.require("MWF.widget.UUID", function () {
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "column": id,
                "displayName": this.designer.lp.unnamed,
                "orderType": "original"
            };
            if (!this.json.data.selectList) this.json.data.selectList = [];
            this.json.data.selectList.push(json);
            var column = new MWF.xApplication.query.StatementStatDesigner.Stat.Column(json, this);
            this.items.push(column);
            column.selected();

            if (this.viewContentTableNode) {
                var trs = this.viewContentTableNode.getElements("tr");
                trs.each(function (tr) {
                    new Element("td", {"styles": this.css.viewContentTdNode}).inject(tr)
                }.bind(this));
                //this.setContentColumnWidth();
            }
            this.setViewWidth();
            this.addColumnNode.scrollIntoView(false);

        }.bind(this));
        //new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 0}).toRight();
    },
    setContentHeight: function () {
        var size = this.areaNode.getSize();
        var titleSize = this.viewTitleNode.getSize();
        var actionbarSize = this.actionbarNode ? this.actionbarNode.getSize() : {x: 0, y: 0};
        var pagingSize = this.pagingNode ? this.pagingNode.getSize() : {x: 0, y: 0};
        var height = size.y - titleSize.y - actionbarSize.y - pagingSize.y - 4;

        this.viewContentScrollNode.setStyle("height", height);

        var contentSize = this.viewContentBodyNode.getSize();
        if (height < contentSize.y) height = contentSize.y + 10;

        this.viewContentNode.setStyle("height", height);
        this.contentLeftNode.setStyle("height", height);
        this.contentRightNode.setStyle("height", height);
        //this.viewContentBodyNode.setStyle("min-height", height);
    },
    loadViewColumns: function () {
        //    for (var i=0; i<10; i++){
        if (this.json.data.selectList) {
            this.json.data.selectList.each(function (json) {
                this.items.push(new MWF.xApplication.query.StatementStatDesigner.Stat.Column(json, this));

            }.bind(this));
        }
        //    }
    },
    setViewWidth: function () {
        if (!this.viewAreaNode) return;
        this.viewAreaNode.setStyle("width", "auto");
        this.viewTitleNode.setStyle("width", "auto");

        var s1 = this.viewTitleTableNode.getSize();
        var s2 = this.refreshNode.getSize();
        var s3 = this.addColumnNode.getSize();
        var width = s1.x + s2.x + s2.x;
        var size = this.areaNode.getSize();

        if (width > size.x) {
            this.viewTitleNode.setStyle("width", "" + (width - 2) + "px");
            this.viewAreaNode.setStyle("width", "" + (width - 2) + "px");
        } else {
            this.viewTitleNode.setStyle("width", "" + (size.x - 2) + "px");
            this.viewAreaNode.setStyle("width", "" + (size.x - 2) + "px");
        }
        this.setContentColumnWidth();
        this.setContentHeight();
    },

    _setEditStyle: function (name, input, oldValue) {

        // if (name == "data.viewStyleType") {
        //
        //     var file = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].file : null;
        //     var extendFile = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].extendFile : null;
        //     this.loadTemplateStyles(file, extendFile, function (templateStyles) {
        //         this.templateStyles = templateStyles;
        //
        //         var oldFile, oldExtendFile;
        //         if (oldValue && this.stylesList[oldValue]) {
        //             oldFile = this.stylesList[oldValue].file;
        //             oldExtendFile = this.stylesList[oldValue].extendFile;
        //         }
        //         this.loadTemplateStyles(oldFile, oldExtendFile, function (oldTemplateStyles) {
        //
        //             this.json.data.styleConfig = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType] : null;
        //
        //             if (oldTemplateStyles["view"]) this.clearTemplateStyles(oldTemplateStyles["view"]);
        //             if (this.templateStyles["view"]) this.setTemplateStyles(this.templateStyles["view"]);
        //             this.setAllStyles();
        //
        //             this.actionbarList.each(function (module) {
        //                 if (oldTemplateStyles["actionbar"]) {
        //                     module.clearTemplateStyles(oldTemplateStyles["actionbar"]);
        //                 }
        //                 module.setStyleTemplate();
        //                 module.setAllStyles();
        //             })
        //
        //             this.pagingList.each(function (module) {
        //                 if (oldTemplateStyles["paging"]) {
        //                     module.clearTemplateStyles(oldTemplateStyles["paging"]);
        //                 }
        //                 module.setStyleTemplate();
        //                 module.setAllStyles();
        //             });
        //
        //             // this.moduleList.each(function(module){
        //             //     if (oldTemplateStyles[module.moduleName]){
        //             //         module.clearTemplateStyles(oldTemplateStyles[module.moduleName]);
        //             //     }
        //             //     module.setStyleTemplate();
        //             //     module.setAllStyles();
        //             // }.bind(this));
        //         }.bind(this))
        //
        //     }.bind(this))
        // }
        // if (name == "data.viewStyles") {
        //     this.setCustomStyles();
        // }
    },

    // loadTemplateStyle: function (callback) {
    //     this.loadStylesList(function () {
    //         var oldStyleValue = "";
    //         if ((!this.json.data.viewStyleType) || !this.stylesList[this.json.data.viewStyleType]) this.json.data.viewStyleType = "default";
    //         this.loadTemplateStyles(this.stylesList[this.json.data.viewStyleType].file, this.stylesList[this.json.data.viewStyleType].extendFile,
    //             function (templateStyles) {
    //                 this.templateStyles = templateStyles;
    //                 if (!this.json.data.viewStyleType) this.json.data.viewStyleType = "default";
    //
    //                 if (this.templateStyles && this.templateStyles["view"]) {
    //                     var viewStyles = Object.clone(this.templateStyles["view"]);
    //                     if (viewStyles.contentGroupTd) delete viewStyles.contentGroupTd;
    //                     if (viewStyles.groupCollapseNode) delete viewStyles.groupCollapseNode;
    //                     if (viewStyles.groupExpandNode) delete viewStyles.groupExpandNode;
    //                     if (!this.json.data.viewStyles) {
    //                         this.json.data.viewStyles = viewStyles;
    //                     } else {
    //                         this.setTemplateStyles(viewStyles);
    //                     }
    //                 }
    //
    //                 this.setCustomStyles();
    //
    //                 if (callback) callback();
    //             }.bind(this)
    //         );
    //     }.bind(this));
    // },
    // clearTemplateStyles: function (styles) {
    //     if (styles) {
    //         if (styles.container) this.removeStyles(styles.container, "container");
    //         if (styles.table) this.removeStyles(styles.table, "table");
    //         if (styles.titleTr) this.removeStyles(styles.titleTr, "titleTr");
    //         if (styles.titleTd) this.removeStyles(styles.titleTd, "titleTd");
    //         if (styles.contentTr) this.removeStyles(styles.contentTr, "contentTr");
    //         if (styles.contentSelectedTr) this.removeStyles(styles.contentSelectedTr, "contentSelectedTr");
    //         if (styles.contentTd) this.removeStyles(styles.contentTd, "contentTd");
    //         // if (styles.contentGroupTd) this.removeStyles(styles.contentGroupTd, "contentGroupTd");
    //         // if (styles.groupCollapseNode) this.removeStyles(styles.groupCollapseNode, "groupCollapseNode");
    //         // if (styles.groupExpandNode) this.removeStyles(styles.groupExpandNode, "groupExpandNode");
    //         if (styles.checkboxNode) this.removeStyles(styles.checkboxNode, "checkboxNode");
    //         if (styles.checkedCheckboxNode) this.removeStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
    //         if (styles.radioNode) this.removeStyles(styles.radioNode, "radioNode");
    //         if (styles.checkedRadioNode) this.removeStyles(styles.checkedRadioNode, "checkedRadioNode");
    //         if (styles.tableProperties) this.removeStyles(styles.tableProperties, "tableProperties");
    //     }
    // },
    //
    // setTemplateStyles: function (styles) {
    //     if (styles.container) this.copyStyles(styles.container, "container");
    //     if (styles.table) this.copyStyles(styles.table, "table");
    //     if (styles.titleTr) this.copyStyles(styles.titleTr, "titleTr");
    //     if (styles.titleTd) this.copyStyles(styles.titleTd, "titleTd");
    //     if (styles.contentTr) this.copyStyles(styles.contentTr, "contentTr");
    //     if (styles.contentSelectedTr) this.copyStyles(styles.contentSelectedTr, "contentSelectedTr");
    //     if (styles.contentTd) this.copyStyles(styles.contentTd, "contentTd");
    //     // if (styles.contentGroupTd) this.copyStyles(styles.contentGroupTd, "contentGroupTd");
    //     // if (styles.groupCollapseNode) this.copyStyles(styles.groupCollapseNode, "groupCollapseNode");
    //     // if (styles.groupExpandNode) this.copyStyles(styles.groupExpandNode, "groupExpandNode");
    //     if (styles.checkboxNode) this.copyStyles(styles.checkboxNode, "checkboxNode");
    //     if (styles.checkedCheckboxNode) this.copyStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
    //     if (styles.radioNode) this.copyStyles(styles.radioNode, "radioNode");
    //     if (styles.checkedRadioNode) this.copyStyles(styles.checkedRadioNode, "checkedRadioNode");
    //     if (styles.tableProperties) this.copyStyles(styles.tableProperties, "tableProperties");
    // },
    // removeStyles: function (from, to) {
    //     if (this.json.data.viewStyles[to]) {
    //         Object.each(from, function (style, key) {
    //             if (this.json.data.viewStyles[to][key] && this.json.data.viewStyles[to][key] == style) {
    //                 delete this.json.data.viewStyles[to][key];
    //             }
    //         }.bind(this));
    //     }
    // },
    // copyStyles: function (from, to) {
    //     if (!this.json.data.viewStyles[to]) this.json.data.viewStyles[to] = {};
    //     Object.each(from, function (style, key) {
    //         if (!this.json.data.viewStyles[to][key]) this.json.data.viewStyles[to][key] = style;
    //     }.bind(this));
    // }
    // preview: function(){
    //     if( this.isNewView ){
    //         this.designer.notice( this.designer.lp.saveViewNotice, "error" );
    //         return;
    //     }
    //     this.saveSilence( function () {
    //         var url = "../x_desktop/app.html?app=query.Query&status=";
    //         url += JSON.stringify({
    //             id : this.data.application,
    //             viewId : this.data.id
    //         });
    //         window.open(o2.filterUrl(url),"_blank");
    //     }.bind(this));
    // },
    // saveSilence: function(callback){
    //     if (!this.data.name){
    //         this.designer.notice(this.designer.lp.notice.inputName, "error");
    //         return false;
    //     }
    //
    //     this.designer.actions.saveView(this.data, function(json){
    //         this.data.id = json.data.id;
    //         this.isNewView = false;
    //         //this.page.textNode.set("text", this.data.name);
    //         if (this.lisNode) {
    //             this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
    //         }
    //         if (callback) callback();
    //     }.bind(this));
    // },
    // save: function(callback){
    //     //if (this.designer.tab.showPage==this.page){
    //     if (!this.data.name){
    //         this.designer.notice(this.designer.lp.notice.inputName, "error");
    //         return false;
    //     }
    //     //}
    //     this.designer.actions.saveView(this.data, function(json){
    //         this.designer.notice(this.designer.lp.notice.save_success, "success", this.node, {"x": "left", "y": "bottom"});
    //         this.isNewView = false;
    //         this.data.id = json.data.id;
    //         //this.page.textNode.set("text", this.data.name);
    //         if (this.lisNode) {
    //             this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
    //         }
    //         if (callback) callback();
    //     }.bind(this));
    // },
    // saveAs: function(){
    //     var form = new MWF.xApplication.query.StatementDesigner.View.NewNameForm(this, {
    //         name : this.data.name + "_" + MWF.xApplication.query.StatementDesigner.LP.copy,
    //         query : this.data.query || this.data.application,
    //         queryName :	this.data.queryName || this.data.applicationName
    //     }, {
    //         onSave : function( data, callback ){
    //             this._saveAs( data, callback );
    //         }.bind(this)
    //     }, {
    //         app: this.designer
    //     });
    //     form.edit()
    // },
    // _saveAs : function( data , callback){
    //     var _self = this;
    //
    //     var d = this.cloneObject( this.data );
    //
    //     d.isNewView = true;
    //     d.id = this.designer.actions.getUUID();
    //     d.name = data.name;
    //     d.alias = "";
    //     d.query = data.query;
    //     d.queryName = data.queryName;
    //     d.application = data.query;
    //     d.applicationName = data.queryName;
    //     d.pid = d.id + d.id;
    //
    //     delete d[this.data.id+"viewFilterType"];
    //     d[d.id+"viewFilterType"]="custom";
    //
    //     d.data.selectList.each( function( entry ){
    //         entry.id = (new MWF.widget.UUID).id;
    //     }.bind(this));
    //
    //     this.designer.actions.saveView(d, function(json){
    //         this.designer.notice(this.designer.lp.notice.saveAs_success, "success", this.node, {"x": "left", "y": "bottom"});
    //         if (callback) callback();
    //     }.bind(this));
    // }

});

MWF.xApplication.query.StatementStatDesigner.Stat.Column = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Column,
    initialize: function (json, stat, next) {
        this.propertyPath = "../x_component_query_StatementStatDesigner/$StatementStat/column.html";
        this.stat = stat;
        this.view = stat;
        this.json = json;
        this.next = next;
        this.css = this.stat.css;
        this.content = this.stat.viewTitleTrNode;
        this.domListNode = this.stat.domListNode;
        this.load();
    },
    refreshColumnPathData: function () {
        if (this.property) {
            this.property.loadDataPathSelect();
        }
    },
    getColumnDataPath: function () {
        return this.stat.statementStat.getColumnDataPath();
    },
    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementStatDesigner.Property(this, this.stat.designer.propertyContentArea, this.stat.designer, {
                "path": this.propertyPath,
                "onPostLoad": function () {
                    this.property.show();

                    var processDiv = this.property.propertyContent.getElements("#" + this.json.id + "dataPathSelectedProcessArea");
                    var cmsDiv = this.property.propertyContent.getElements("#" + this.json.id + "dataPathSelectedCMSArea");

                    if (this.view.json.type == "cms") {
                        processDiv.setStyle("display", "none");
                        cmsDiv.setStyle("display", "block");
                    } else {
                        processDiv.setStyle("display", "block");
                        cmsDiv.setStyle("display", "none");
                    }
                }.bind(this)
            });
            this.property.load();
        } else {
            this.property.show();
        }
    },
    selected: function () {
        if (this.stat.statementStat.currentSelectedModule) {
            if (this.stat.statementStat.currentSelectedModule == this) {
                return true;
            } else {
                this.stat.statementStat.currentSelectedModule.unSelected();
            }
        }
        this.stat.domListNode.show();
        this.node.setStyles(this.css.viewTitleColumnNode_selected);
        this.listNode.setStyles(this.css.cloumnListNode_selected);
        // new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        // new Fx.Scroll(this.view.designer.propertyDomArea, {
        //     "wheelStops": false,
        //     "duration": 100
        // }).toElement(this.listNode);

        try{
            this.node.scrollIntoView(false);
            this.listNode.scrollIntoView(false);
        }catch (e) {

        }

        this.stat.statementStat.selectMode = "viewColumn";
        this.stat.statementStat.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
        this.stat.statementStat.designer.setDesignerStatementResize();
    },
    unSelected: function () {
        this.stat.statementStat.currentSelectedModule = null;
        //this.node.setStyles(this.css.viewTitleColumnNode);
        if (this.isError) {
            this.node.setStyles(this.css.viewTitleColumnNode_error)
        } else {
            this.node.setStyles(this.css.viewTitleColumnNode)
        }

        this.listNode.setStyles(this.css.cloumnListNode);
        this.isSelected = false;
        this._hideActions();
        this.hideProperty();
    },
    addColumn: function(e, data){
        MWF.require("MWF.widget.UUID", function(){
            var json;
            if (data){
                json = Object.clone(data);
                json.id = (new MWF.widget.UUID).id;
                json.column = (new MWF.widget.UUID).id;
            }else{
                var id = (new MWF.widget.UUID).id;
                json = {
                    "id": id,
                    "column": id,
                    "displayName": this.view.designer.lp.unnamed,
                    "orderType": "original"
                };
            }

            var idx = this.view.json.data.selectList.indexOf(this.json);
            this.view.json.data.selectList.splice(idx, 0, json);

            var column = new MWF.xApplication.query.StatementStatDesigner.Stat.Column(json, this.view, this);
            this.view.items.splice(idx, 0, column);
            column.selected();

            if (this.view.viewContentTableNode){
                var trs = this.view.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    var td = tr.insertCell(idx);
                    td.setStyles(this.css.viewContentTdNode);
                }.bind(this));
            }
            this.view.setViewWidth();

        }.bind(this));
    }
});

