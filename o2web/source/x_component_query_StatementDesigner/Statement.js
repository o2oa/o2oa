MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;

MWF.xDesktop.requireApp("query.StatementDesigner", "lp." + MWF.language, null, false);
MWF.xDesktop.requireApp("query.StatementDesigner", "Property", null, false);
MWF.xDesktop.requireApp("query.ViewDesigner", "View", null, false);
o2.require("o2.widget.JavascriptEditor", null, false);
o2.require("o2.widget.UUID", null, false);

MWF.xApplication.query.StatementDesigner.Statement = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_StatementDesigner/$Statement/statement.html"
    },

    initialize: function (designer, data, options) {
        this.setOptions(options);

        this.path = "../x_component_query_StatementDesigner/$Statement/";
        this.cssPath = "../x_component_query_StatementDesigner/$Statement/" + this.options.style + "/css.wcss";

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
        if (!this.json.type) this.json.type = "select";
        if (!this.json.format) this.json.format = "jpql";
        if (!this.json.entityCategory) this.json.entityCategory = "official";
        if (!this.json.entityClassName) this.json.entityClassName = ""; //"com.x.processplatform.core.entity.content.Task";
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
            var statement = node.retrieve("statement");
            if (statement.id == this.data.id) {
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
        if (this.view && this.view.domListNode) {
            this.view.domListNode.hide();
        }

        this.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
    },
    unSelected: function () {
        this.currentSelectedModule = null;
        this.isSelected = false;
        this.hideProperty();
    },
    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementDesigner.Property(this, this.designer.designerContentArea, this.designer, {
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
    loadJpqlTab: function (callback) {
        var _self = this;
        MWF.require("MWF.widget.Tab", null, false);

        this.jpqlTab = new MWF.widget.Tab(this.jpqlTabNode, {"style": "script"});
        this.jpqlTab.load();

        this.tabJpqlNode = Element("div");
        this.jpqlTabPageNode.inject(this.tabJpqlNode);

        this.tabCountJpqlNode = Element("div");
        this.countJpqlTabPageNode.inject(this.tabCountJpqlNode);

        this.jpqlPage = this.jpqlTab.addTab(this.tabJpqlNode, this.designer.lp.queryStatement);
        this.countJpqlPage = this.jpqlTab.addTab(this.tabCountJpqlNode, this.designer.lp.countStatement);

        this.jpqlPage.showTabIm();

        // this.jpqlPage.addEvent("postShow", function(){
        //     if( this.view ){
        //         this.view.setContentHeight();
        //         this.view.selected();
        //     }
        // }.bind(this));
        // this.countJpqlPage.addEvent("postShow", function(){
        //     this.selected();
        // }.bind(this));
    },
    loadTab: function (callback) {
        var _self = this;
        MWF.require("MWF.widget.Tab", null, false);

        this.tab = new MWF.widget.Tab(this.tabNode, {"style": "script"});
        this.tab.load();

        this.tabRunNode = Element("div");
        this.pageRunNode = new Element("div", {
            "styles": {
                "overflow": "auto",
                "background-color": "#fff"
            }
        }).inject(this.tabRunNode);
        this.runArea.inject(this.pageRunNode);

        this.tabViewNode = Element("div", {"styles": {"height": "100%"}});
        this.pageViewNode = new Element("div.pageViewNode").inject(this.tabViewNode);
        this.viewArea.inject(this.pageViewNode);

        this.runPage = this.tab.addTab(this.tabRunNode, this.designer.lp.runTest);
        this.viewPage = this.tab.addTab(this.tabViewNode, this.designer.lp.view);

        this.runPage.showTabIm();

        this.viewPage.addEvent("postShow", function () {
            if (this.view) {
                this.view.setContentHeight();
                this.view.selected();
            }
        }.bind(this));
        this.runPage.addEvent("postShow", function () {
            this.selected();
        }.bind(this));
    },
    loadStatement: function () {
        //this.statementDesignerNode = new Element("div", {"styles": this.css.statementDesignerNode}).inject(this.areaNode);
        this.loadStatementHtml(function () {
            this.designerArea = this.areaNode.getElement(".o2_statement_statementDesignerNode");

            this.jpqlTabPageNode = this.areaNode.getElement(".o2_statement_statementJpqlTabPageNode");

            this.jpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerJpql");
            this.scriptArea = this.areaNode.getElement(".o2_statement_statementDesignerScript");

            this.formatTypeArea = this.areaNode.getElement(".o2_statement_statementDesignerFormatContent");
            this.entityCategorySelect = this.areaNode.getElement(".o2_statement_statementDesignerCategoryContent").getElement("select");

            this.dynamicTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_dynamic");
            this.officialTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_official");
            this.customTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_custom");

            this.dynamicTableSelect = this.areaNode.getElement(".o2_statement_statementDesignerSelectTable");
            this.officialTableSelect = this.officialTableArea.getElement("select");

            this.fieldSelect = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_field").getElement("select");
            this.loadFieldSelect();

            this.dynamicTableContent = this.areaNode.getElement(".o2_statement_statementDesignerTableContent");

            this.jpqlTabNode = this.areaNode.getElement(".o2_statement_statementJpqlTabNode");

            this.jpqlTypeSelect = this.areaNode.getElement(".o2_statement_statementDesignerTypeContent").getElement("select");
            this.loadJpqlTypeSelect();

            // this.jpqlSelectEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_select");
            // this.jpqlUpdateEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_update");
            // this.jpqlDeleteEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_sdelete");

            // this.jpqlSelectEditor_selectContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_selectContent");
            // this.jpqlSelectEditor_fromContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_fromContent");
            // this.jpqlSelectEditor_whereContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_whereContent");

            this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");


            this.countJpqlTabPageNode = this.areaNode.getElement(".o2_statement_statementCountJpqlTabPageNode");
            this.countJpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerCountJpql");
            this.countScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerCountScript");
            this.countJpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerCountJpqlLine");
            this.loadJpqlTab();

            this.resizeNode = this.areaNode.getElement(".o2_statement_resizeNode");

            this.tabNode = this.areaNode.getElement(".o2_statement_tabNode");

            this.runArea = this.areaNode.getElement(".o2_statement_statementRunNode");
            // this.runTitleNode = this.areaNode.getElement(".o2_statement_statementRunTitleNode");
            this.runContentNode = this.areaNode.getElement(".o2_statement_statementRunContentNode");
            this.runJsonNode = this.runContentNode.getFirst();
            this.runActionNode = this.runJsonNode.getNext();
            this.runResultNode = this.runContentNode.getLast();
            this.setRunnerSize();
            this.designer.addEvent("resize", this.setRunnerSize.bind(this));
            if (this.json.format == "script") {
                this.loadStatementScriptEditor();
                this.loadStatementCountScriptEditor();
            } else {
                this.loadStatementEditor();
                this.loadStatementCountEditor();
            }
            this.loadStatementRunner();

            this.viewArea = this.areaNode.getElement(".o2_statement_viewNode");
            this.loadView();

            this.loadTab();

            this.setEvent();
            this.loadVerticalResize();
        }.bind(this));
    },
    loadJpqlTypeSelect : function(){
      this.jpqlTypeSelect.empty();
      var optionList = [{text:"SELECT", value:"select"}];
        if( this.data.entityCategory === "dynamic" || (this.data.description && this.data.description.indexOf("update")>-1)){
          optionList = optionList.concat([
              {text:"UPDATE", value:"update"},
              {text:"DELETE", value:"delete"}
           ])
      }

      var flag = true;
        optionList.each( function ( field ) {
            var option = new Element("option", {
                "text": field.text,
                "value": field.value
            }).inject(this.jpqlTypeSelect);
            if( this.json.type === field.value ){
                flag = false;
                option.selected = true;
            }
        }.bind(this));
        if( flag ){
            this.jpqlTypeSelect.options[0].selected = true;
            this.json.type = this.jpqlTypeSelect.options[0].value;
            this.jpqlTypeSelect.fireEvent("change");
        }
    },
    loadFieldSelect : function(){
        this.fieldSelect.empty();
        var d = this.data;
        var className = d.entityCategory === "dynamic" ? d.table : d.entityClassName;
        if( !className )return;
        o2.Actions.load("x_query_assemble_designer").QueryAction.getEntityProperties(
            className,
            d.entityCategory,
            function(json){
                var option = new Element("option", { "text": this.designer.lp.fileldSelectNote, "value": "" }).inject(this.fieldSelect);
                option.store("type", d.entityCategory);
                option.store("tableName", className );
                (json.data||[]).each( function ( field ) {
                    var option = new Element("option", {
                        "text": field.name + ( field.description ? ("-" + field.description) : "" ),
                        "value": field.name
                    }).inject(this.fieldSelect);
                    option.store("field", field);
                    option.store("type", d.entityCategory );
                    option.store("tableName", className );
                }.bind(this))
            }.bind(this)
        )
    },
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
        if(this.countJpqlEditorNode)this.countJpqlEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.scriptArea)this.scriptArea.setStyle( "height", ""+editorHeight+"px" );
        if(this.countScriptArea)this.countScriptArea.setStyle( "height", ""+editorHeight+"px" );

        if( this.editor )this.editor.resize();
        if( this.countEditor )this.countEditor.resize();
        if( this.scriptEditor ){
            this.scriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.scriptEditor.resizeContentNodeSize();
        }
        if( this.countScriptEditor ){
            this.countScriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.countScriptEditor.resizeContentNodeSize();
        }

        // this.tabNode.setStyle("height", ""+runAreaHeight+"px");
        this.setRunnerSize();
        if( this.view ){
            this.setViewSize();
            this.view.setContentHeight()
        }
    },
    loadStatementScriptEditor: function () {
        if (!this.scriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.scriptEditor = new o2.widget.ScriptArea(this.scriptArea, {
                    "isbind": false,
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.scriptTitle,
                    "onChange": function () {
                        this.json.scriptText = this.scriptEditor.toJson().code;
                    }.bind(this)
                });
                this.scriptEditor.load({"code": this.json.scriptText})
            }.bind(this), false);
        }
    },
    loadStatementCountScriptEditor: function () {
        if (!this.countScriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.countScriptEditor = new o2.widget.ScriptArea(this.countScriptArea, {
                    "isbind": false,
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.scriptTitle,
                    "onChange": function () {
                        this.json.countScriptText = this.countScriptEditor.toJson().code;
                    }.bind(this)
                });
                this.countScriptEditor.load({"code": this.json.countScriptText})
            }.bind(this), false);
        }
    },
    setRunnerSize: function () {
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.designerArea.getComputedSize();
        var reizeNodeSize = this.resizeNode.getComputedSize();

        var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight;
        var mTop = this.runArea.getStyle("margin-top").toInt();
        var mBottom = this.runArea.getStyle("margin-bottom").toInt();
        var pTop = this.runArea.getStyle("padding-top").toInt();
        var pBottom = this.runArea.getStyle("padding-bottom").toInt();
        y = y - mTop - mBottom - pTop - pBottom - 5;

        var tabSize = this.tabNode.getComputedSize();
        y = y - tabSize.totalHeight;

        this.runArea.setStyle("height", "" + y + "px");

        // var titleSize = this.runTitleNode.getComputedSize();
        // y = y - titleSize.totalHeight;

        this.runContentNode.setStyle("height", "" + y + "px");
    },
    loadStatementEditor: function () {
        if (!this.editor) {
            var value;
            if( !this.json.data ){
                var table = "table";
                switch (this.json.type) {
                    case "update":
                        value = "UPDATE " + table + " o SET ";
                        break;
                    case "delete":
                        value = "DELETE " + table + " o WHERE ";
                        break;
                    default:
                        value = "SELECT o FROM " + table + " o";
                }
                this.json.data = value;
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
                        this.checkJpqlType();
                    }.bind(this));

                    // this.editor.editor.on("change", function(){
                    //     this.data.data = this.editor.getValue();
                    //     this.checkJpqlType();
                    // }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }

    },
    loadStatementCountEditor: function () {
        if (!this.countEditor) {
            if( !this.json.countData )this.json.countData = "SELECT count(o.id) FROM table o";
            if( this.countJpqlEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadStatementCountEditor();
                    this.countJpqlPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.countJpqlPage.addEvent("postShow", postShowFun);
            }else{
                this._loadStatementCountEditor();
            }
        }
    },
    _loadStatementCountEditor : function(){
        o2.require("o2.widget.JavascriptEditor", function () {
            this.countEditor = new o2.widget.JavascriptEditor(this.countJpqlEditorNode, {
                "title": "JPQL",
                "option": {"mode": "sql"}
            });
            this.countEditor.load(function () {
                // if (this.json.countData) {
                    this.countEditor.editor.setValue(this.json.countData);
                // } else {
                //     var table = "table";
                //     this.countEditor.editor.setValue("SELECT count(o.id) FROM " + table + " o");
                // }
                // this.json.countData = this.countEditor.editor.getValue();

                this.countEditor.addEditorEvent("change", function () {
                    this.data.countData = this.countEditor.getValue();
                }.bind(this));

                // this.editor.editor.on("change", function(){
                //     this.data.data = this.editor.getValue();
                //     this.checkJpqlType();
                // }.bind(this));
            }.bind(this));
        }.bind(this), false);
    },
    setSatementTable: function () {
        if (!this.json.type) this.json.type = "select";
        this.changeType(this.json.type, true);
        if( this.editor && this.editor.editor){
            if (this.json.data) {
                this.editor.editor.setValue(this.json.data);
            } else {
                var table = (this.json.tableObj) ? this.json.tableObj.name : "table";
                switch (this.json.type) {
                    case "update":
                        this.editor.editor.setValue("UPDATE " + table + " o SET ");
                        break;
                    case "delete":
                        this.editor.editor.setValue("DELETE " + table + " o WHERE ");
                        break;
                    default:
                        this.editor.editor.setValue("SELECT o FROM " + table + " o");
                }
            }
        }
    },

    checkJpqlType: function () {
        var str = this.json.data;
        this.json.data = str;
        var jpql_select = /^select/i;
        var jpql_update = /^update/i;
        var jpql_delete = /^delete/i;
        if (jpql_select.test(str)) return this.changeType("select");
        if (jpql_update.test(str)) return this.changeType("update");
        if (jpql_delete.test(str)) return this.changeType("delete");
    },
    changeType: function (type, force) {
        if (this.json.type != type) this.json.type = type;
        if (type != this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value || force) {
            for (var i = 0; i < this.jpqlTypeSelect.options.length; i++) {
                if (this.jpqlTypeSelect.options[i].value == type) {
                    this.jpqlTypeSelect.options[i].set("selected", true);
                    break;
                }
            }
        }
    },
    loadStatementHtml: function (callback) {
        this.areaNode.loadAll({
            "css": this.path + this.options.style + "/statement.css",
            "html": this.path + "statementDesigner.html"
        }, {
            "bind": {"lp": this.designer.lp, "data": this.data}
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
        this.formatTypeArea.getElements("input").addEvent("click", function (e) {
            if (e.target.checked) {
                var v = e.target.get("value");
                if (v === "script") {
                    this.scriptArea.show();
                    this.jpqlArea.hide();
                    this.loadStatementScriptEditor();

                    this.countScriptArea.show();
                    this.countJpqlArea.hide();
                    this.loadStatementCountScriptEditor();
                } else {
                    this.scriptArea.hide();
                    this.jpqlArea.show();
                    this.loadStatementEditor();

                    this.countScriptArea.hide();
                    this.countJpqlArea.show();
                    this.loadStatementCountEditor();
                }
                this.json.format = v;
            }
        }.bind(this));
        this.entityCategorySelect.addEvent("change", function (e) {
            var entityCategory = e.target.options[e.target.selectedIndex].value;
            switch (entityCategory) {
                case "dynamic":
                    this.officialTableArea.hide();
                    this.dynamicTableArea.show();
                    this.customTableArea.hide();
                    break;
                case "custom":
                    this.officialTableArea.hide();
                    this.dynamicTableArea.hide();
                    this.customTableArea.show();
                    break;
                default:
                    this.officialTableArea.show();
                    this.dynamicTableArea.hide();
                    this.customTableArea.hide();
                    break;
            }
            this.json.entityCategory = entityCategory;
            this.loadJpqlTypeSelect();
            this.loadFieldSelect();
            if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();
        }.bind(this));
        //@todo change table
        this.officialTableSelect.addEvent("change", function (e) {
            debugger;
            var entityClassName = e.target.options[e.target.selectedIndex].value;
            this.json.entityClassName = entityClassName;
            if( entityClassName ){
                this.changeEditorEntityClassName( entityClassName.split(".").getLast() );
            }
            this.loadFieldSelect();

            this.json.table = "";
            this.json.tableObj = null;

            if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();

            //     var className = e.target.options[e.target.selectedIndex].value;
            //     if (this.json.type=="select"){
            //         this.json.data
            //         /(select)*(where|)/g
            //     }
            // }.bind(this));

            // this.jpqlTypeSelect.addEvent("change", function(){
            //     var type = e.target.options[e.target.selectedIndex].value;
            //     switch (entityCategory) {
            //         case "update":
            //             this.jpqlSelectEditor.hide();
            //             this.jpqlUpdateEditor.show();
            //             this.jpqlDeleteEditor.hide();
            //             this.loadJpqlUpdateEditor();
            //             break;
            //         case "delete":
            //             this.jpqlSelectEditor.hide();
            //             this.jpqlUpdateEditor.hide();
            //             this.jpqlDeleteEditor.show();
            //             break;
            //         default:
            //             this.jpqlSelectEditor.show();
            //             this.jpqlUpdateEditor.hide();
            //             this.jpqlDeleteEditor.hide();
            //             break;
            //     }
        }.bind(this));

        this.runActionNode.getFirst().addEvent("click", this.runStatement.bind(this));

        this.dynamicTableSelect.addEvent("click", this.selectTable.bind(this));
        this.jpqlTypeSelect.addEvent("change", function () {
            var t = this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value;
            if (t != this.json.type) {
                this.json.type = t;
            }
            if (t != "select") {
                this.jpqlPage.showTabIm();
                this.countJpqlPage.disableTab();

                this.runPage.showTabIm();
                this.viewPage.disableTab();
            } else {
                this.countJpqlPage.enableTab(true);
                this.viewPage.enableTab(true);
            }
        }.bind(this));

        this.fieldSelect.addEvent("change", function (ev) {
            var option = ev.target.options[ev.target.selectedIndex];
            var type = option.retrieve("type");
            var field = option.retrieve("field");
            if( !field )return;
            var text = field.name;
            if( this.countJpqlPage && this.countJpqlPage.isShow && !this.countJpqlPage.disabled ){
                if( this.data.format === "script" && this.countScriptEditor.jsEditor ){
                    this.countScriptEditor.jsEditor.insertValue( text );
                }else if(this.countEditor){
                    this.countEditor.insertValue( text );
                }
            }else{
                if( this.data.format === "script" && this.scriptEditor.jsEditor ){
                    this.scriptEditor.jsEditor.insertValue( text );
                }else if( this.editor ){
                    this.editor.insertValue( text );
                }
            }
        }.bind(this))
    },
    changeEditorEntityClassName : function( entityClassName ){
        if (this.json.format == "jpql") {
            if (this.editor) {
                var re = /(.*from\s*)/ig;
                if (this.json.type == "update") re = /(.*update\s*)/ig;

                //if (this.json.type=="select" && this.editor){
                var v = this.json.data;

                var re2 = /(\s+)/ig;
                var arr = re.exec(v);
                if (arr && arr[0]) {
                    var left = arr[0]
                    v = v.substring(left.length, v.length);
                    //var ar = re2.exec(v);
                    var right = v.substring(v.indexOf(" "), v.length);
                    this.json.data = left + entityClassName + right;
                    this.editor.editor.setValue(this.json.data);
                }

                //}
            }

            if( this.countEditor ){
                var re = /(.*from\s*)/ig;
                var v = this.json.countData;

                var re2 = /(\s+)/ig;
                var arr = re.exec(v);
                if (arr && arr[0]) {
                    var left = arr[0]
                    v = v.substring(left.length, v.length);
                    //var ar = re2.exec(v);
                    var right = v.substring(v.indexOf(" "), v.length);
                    this.json.countData = left + entityClassName + right;
                    this.countEditor.editor.setValue(this.json.countData);
                }
            }

        }
    },

    selectTable: function () {
        new MWF.O2Selector(this.designer.content, {
            "type": "queryTable",
            "count": 1,
            "values": (this.json.table) ? [this.json.table] : [],
            "title": this.designer.lp.selectTable,
            "onComplete": function (items) {
                if (items.length) {
                    var id = items[0].data.id;
                    var name = items[0].data.name;
                    this.dynamicTableContent.set("text", name);
                    this.json.table = name;
                    this.json.tableObj = items[0].data;

                    this.officialTableSelect.options[0].set("selected", true);
                    this.json.entityClassName = "";

                    this.changeEditorEntityClassName( name );
                    this.loadFieldSelect();
                    if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();
                } else {
                    this.dynamicTableContent.set("text", "");
                    this.json.table = "";
                    this.loadFieldSelect();
                    if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();
                }
            }.bind(this)
        });
    },

    runStatement: function () {
        debugger;
        // if (!this.json.data){
        //     this.designer.notice(this.designer.lp.inputStatementData, "error");
        //     return false;
        // }
        // o2.require("o2.widget.Mask", null, false);
        // this.runMask = new o2.widget.Mask();
        // this.runMask.loadNode(this.node);

        this.saveSilence(function () {
            debugger;
            this.execute(function (json) {
                this.executeData = json;
                o2.require("o2.widget.JsonParse", function () {
                    this.runResultNode.empty();
                    var jsonResult = new o2.widget.JsonParse(json, this.runResultNode);
                    jsonResult.load();
                }.bind(this));
                if (this.view) {
                    var flag = true;
                    if (this.data.type !== "select") flag = false;
                    if (this.data.format === "script" && !this.data.scriptText) flag = false;
                    if (this.data.format !== "script" && !this.data.data) flag = false;
                    if (flag) this.view.loadViewData();
                }
                this.setColumnDataPath(json);
                // this.runMask.hide();
            }.bind(this), function () {
                // if (this.runMask) this.runMask.hide();
            }.bind(this))

            // var json = this.jsonEditor.editor.getValue();
            // var o = JSON.parse(json);
            //
            // var mode = "data";
            // if( this.data.type === "select" ){
            //     if( this.data.format === "script" ){
            //         if( this.data.scriptText && this.data.countScriptText ){
            //             mode = "all"
            //         }else if( this.data.scriptText && !this.data.countScriptText ){
            //             mode = "data"
            //         }else if( !this.data.scriptText && this.data.countScriptText ){
            //             mode = "count"
            //         }else{
            //             this.designer.notice(this.designer.lp.inputStatementData, "error");
            //             return false;
            //         }
            //     }else{
            //         if( this.data.data && this.data.countData ){
            //             mode = "all"
            //         }else if( this.data.data && !this.data.countData ){
            //             mode = "data"
            //         }else if( !this.data.data && this.data.countData ){
            //             mode = "count"
            //         }else{
            //             this.designer.notice(this.designer.lp.inputStatementData, "error");
            //             return false;
            //         }
            //     }
            // }
            // o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.id, mode, 1, 50 , o, function(json){
            //     o2.require("o2.widget.JsonParse", function(){
            //         this.runResultNode.empty();
            //         var jsonResult = new o2.widget.JsonParse(json, this.runResultNode);
            //         jsonResult.load();
            //     }.bind(this));
            //     this.runMask.hide();
            // }.bind(this), function(xhr, text, error){
            //     debugger;
            //     if (this.runMask) this.runMask.hide();
            //     var errorText = error;
            //     if (xhr){
            //         var json = JSON.decode(xhr.responseText);
            //         if (json){
            //             errorText = json.message.trim() || "request json error";
            //         }else{
            //             errorText = "request json error: "+xhr.responseText;
            //         }
            //     }
            //     errorText = errorText.replace(/\</g, "&lt;");
            //     errorText = errorText.replace(/\</g, "&gt;");
            //     MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            // }.bind(this))
        }.bind(this));
    },
    setColumnDataPath: function (json) {
        if (this.data.type !== "select") return;
        if (this.data.format === "script" && !this.data.scriptText) return;
        if (this.data.format !== "script" && !this.data.data) return;
        this.columnDataPathList = [];
        debugger;
        var addPath = function (value, key) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    var path = (key || typeOf(key) === "number") ? (key + "." + idx) : idx.toString();
                    if (!this.columnDataPathList.contains(path)) this.columnDataPathList.push(path);
                    if (typeOf(v) === "array" || typeOf(v) === "object") addPath(v, path);
                }.bind(this))
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, k) {
                    var path = (key || typeOf(key) === "number") ? (key + "." + k) : k;
                    if (!this.columnDataPathList.contains(path)) this.columnDataPathList.push(path);
                    if (typeOf(v) === "array" || typeOf(v) === "object") addPath(v, path);
                    addPath(v, path);
                }.bind(this))
            } else {
                // if( key && !this.columnDataPathList.indexOf(key) )this.columnDataPathList.push(key);
            }
        }.bind(this);
        for (var i = 0; i < json.data.length && i < 10; i++) {
            var d = json.data[i];
            addPath(d);
        }
        this.columnDataPathList.sort();
        if (this.view && this.view.items) {
            this.view.items.each(function (column) {
                column.refreshColumnPathData()
            })
        }
    },
    getColumnDataPath: function () {
        return this.columnDataPathList || [];
    },
    execute: function (success, failure) {
        var json = this.jsonEditor.editor.getValue();
        var o = JSON.parse(json);

        var mode = "data";
        if (this.data.type === "select") {
            if (this.data.format === "script") {
                if (this.data.scriptText && this.data.countScriptText) {
                    mode = "all"
                } else if (this.data.scriptText && !this.data.countScriptText) {
                    mode = "data"
                } else if (!this.data.scriptText && this.data.countScriptText) {
                    mode = "count"
                } else {
                    this.designer.notice(this.designer.lp.inputStatementData, "error");
                    return false;
                }
            } else {
                if (this.data.data && this.data.countData) {
                    mode = "all"
                } else if (this.data.data && !this.data.countData) {
                    mode = "data"
                } else if (!this.data.data && this.data.countData) {
                    mode = "count"
                } else {
                    this.designer.notice(this.designer.lp.inputStatementData, "error");
                    return false;
                }
            }
        }
        o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.id, mode, 1, 50, o, function (json) {
            if (success) success(json)
        }.bind(this), function (xhr, text, error) {
            debugger;
            if (failure) failure();
            var errorText = error;
            if (xhr) {
                var json = JSON.decode(xhr.responseText);
                if (json) {
                    errorText = json.message.trim() || "request json error";
                } else {
                    errorText = "request json error: " + xhr.responseText;
                }
            }
            errorText = errorText.replace(/\</g, "&lt;");
            errorText = errorText.replace(/\</g, "&gt;");
            MWF.xDesktop.notice("error", {x: "right", y: "top"}, errorText);
        }.bind(this))
    },

    save: function (callback) {
        debugger;
        if (!this.data.name) {
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }

        if (typeOf(this.viewJson) === "object") {
            if(this.viewJson.data && !this.viewJson.data.group)this.viewJson.data.group = {};
            if(!this.viewJson.pageSize)this.viewJson.pageSize = "20";
            this.data.view = JSON.stringify(this.viewJson);
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

        if (typeOf(this.viewJson) === "object") {
            if(this.viewJson.data && !this.viewJson.data.group)this.viewJson.data.group = {};
            if( !this.viewJson.pageSize )this.viewJson.pageSize = "20";
            this.data.view = JSON.stringify(this.viewJson);
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

    loadView: function (callback) {
        this.setViewSize();
        this.designer.addEvent("resize", this.setViewSize.bind(this));

        if (!this.data.view) {
            this.viewJson = {};
        } else {
            this.viewJson = JSON.parse(this.data.view)
        }
        this.view = new MWF.xApplication.query.StatementDesigner.View(this.designer, this, this.viewJson, {});
        this.view.load(function () {
            this.view.setContentHeight();
        }.bind(this));
    },
    setViewSize: function () {
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.designerArea.getComputedSize();
        var reizeNodeSize = this.resizeNode.getComputedSize();

        var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight;
        var mTop = this.viewArea.getStyle("margin-top").toInt();
        var mBottom = this.viewArea.getStyle("margin-bottom").toInt();
        var pTop = this.viewArea.getStyle("padding-top").toInt();
        var pBottom = this.viewArea.getStyle("padding-bottom").toInt();
        y = y - mTop - mBottom - pTop - pBottom - 1;

        var tabSize = this.tabNode.getComputedSize();
        y = y - tabSize.totalHeight;

        this.viewArea.setStyle("height", "" + y + "px");

        // var titleSize = this.runTitleNode.getComputedSize();
        // y = y - titleSize.totalHeight;
        // this.runContentNode.setStyle("height", ""+y+"px");
    },
    preview: function () {
        if (this.isNewStatement) {
            this.designer.notice(this.designer.lp.saveStatementNotice, "error");
            return;
        }
        if (this.data.type !== "select") {
            this.designer.notice(this.designer.lp.previewNotSelectStatementNotice, "error");
            return;
        }
        if (!this.data.view) {
            this.designer.notice(this.designer.lp.noViewNotice, "error");

        }
        this.saveSilence(function () {
            var url = "../x_desktop/app.html?app=query.Query&status=";
            url += JSON.stringify({
                id: this.data.application,
                statementId: this.data.id
            });
            window.open(o2.filterUrl(url), "_blank");
        }.bind(this));
    }
});

MWF.xApplication.query.StatementDesigner.View = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "../x_component_query_StatementDesigner/$Statement/view.html"
    },

    initialize: function (designer, statement, data, options) {
        this.setOptions(options);

        this.path = "../x_component_query_ViewDesigner/$View/";
        this.cssPath = "../x_component_query_ViewDesigner/$View/" + this.options.style + "/css.wcss";

        this._loadCss();

        this.statement = statement;
        this.designer = designer;
        this.data = data;

        // if (!this.data.data) this.data.data = {};
        this.parseData();

        this.node = this.statement.viewArea;
        //this.tab = this.designer.tab;

        this.areaNode = new Element("div", {"styles": {"height": "calc(100% - 2px)", "overflow": "auto"}});
        this.areaNode.setStyles(this.css.areaNode);

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.areaNode, {"distance": 100});
        //}.bind(this));


        // this.propertyListNode = this.designer.propertyDomArea;
        //this.propertyNode = this.designer.propertyContentArea;

        // if(this.designer.application) this.data.applicationName = this.designer.application.name;
        // if(this.designer.application) this.data.application = this.designer.application.id;

        // this.isNewView = (this.data.name) ? false : true;

        this.items = [];
        this.view = this;

        // this.autoSave();
        // this.designer.addEvent("queryClose", function(){
        //     if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        // }.bind(this));
    },
    load: function (callback) {
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.domListNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.designer.propertyDomArea);

        this.loadTemplateStyle(function () {

            this.loadActionbar();

            this.loadView();

            this.loadPaging();

            // this.selected();
            this.setEvent();

            //if (this.options.showTab) this.page.showTabIm();
            this.setViewWidth();

            this.designer.addEvent("resize", this.setViewWidth.bind(this));

            if (callback) callback();
        }.bind(this))
    },
    parseData: function () {
        this.json = this.data;
        if (!this.json.id) {
            this.json.id = (new o2.widget.UUID).id;
        }
        if (!this.json.data || !this.json.data.events) {
            var url = "../x_component_query_StatementDesigner/$Statement/view.json";
            MWF.getJSON(url, {
                "onSuccess": function (obj) {
                    if (!this.json.data) this.json.data = obj.data;
                    if (!this.json.data.events) this.json.data.events = obj.data.events;
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
            this.statement.runStatement();
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function (e) {
            this.addColumn();
            e.stopPropagation();
        }.bind(this));
    },
    selected: function () {
        debugger;
        if (this.statement.currentSelectedModule) {
            if (this.statement.currentSelectedModule == this) {
                return true;
            } else {
                this.statement.currentSelectedModule.unSelected();
            }
        }
        this.areaNode.setStyles(this.css.areaNode_selected);
        this.statement.currentSelectedModule = this;
        this.domListNode.show();
        this.isSelected = true;
        this.showProperty();
    },
    unSelected: function () {
        this.statement.currentSelectedModule = null;
        this.isSelected = false;
        this.areaNode.setStyles(this.css.areaNode);
        this.hideProperty();
    },

    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
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

    loadViewData: function () {
        debugger;
        if (this.data.id) {
            // this.statement.saveSilence(function () {
            this.viewContentBodyNode.empty();
            this.viewContentTableNode = new Element("table", {
                "styles": this.css.viewContentTableNode,
                "border": "0px",
                "cellPadding": "0",
                "cellSpacing": "0"
            }).inject(this.viewContentBodyNode);

            // this.statement.execute( function (json) {

            // this.statement.setColumnDataPath( json );

            var entries = {};

            this.json.data.selectList.each(function (entry) {
                entries[entry.column] = entry;
            }.bind(this));

            if (this.statement.executeData && this.statement.executeData.data && this.statement.executeData.data.length) {
                this.statement.executeData.data.each(function (line, idx) {
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
                                }else{
                                    obj = "";
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
                                "styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTd"] : this.css.viewContentTdNode
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
                if (this.json.data.viewStyles) {
                    if (this.json.data.viewStyles["noDataTextNode"]) {
                        noDataTextNodeStyle = this.json.data.viewStyles["noDataTextNode"]
                    } else {
                        this.json.data.viewStyles["noDataTextNode"] = this.css.noDataTextNode
                    }
                }
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
            var column = new MWF.xApplication.query.StatementDesigner.View.Column(json, this);
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
            this.addColumnNode.scrollIntoView(true);

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
                this.items.push(new MWF.xApplication.query.StatementDesigner.View.Column(json, this));

            }.bind(this));
        }
        //    }
    },
    showActionbar: function (noSetHeight) {
        this.actionbarNode.show();
        if (!this.json.data.actionbarList) this.json.data.actionbarList = [];
        if (!this.actionbarList || this.actionbarList.length == 0) {
            if (this.json.data.actionbarList.length) {
                this.json.data.actionbarList.each(function (json) {
                    this.actionbarList.push(new MWF.xApplication.query.StatementDesigner.View.Actionbar(json, this.json.data.actionbarList, this))
                }.bind(this));
            } else {
                this.actionbarList.push(new MWF.xApplication.query.StatementDesigner.View.Actionbar(null, this.json.data.actionbarList, this))
            }
        }
        if (!noSetHeight) this.setContentHeight();
    },
    loadPaging: function (noSetHeight) {
        this.pagingNode = new Element("div#pagingNode", {"styles": this.css.pagingNode}).inject(this.areaNode);
        this.pagingList = [];
        if (!this.json.data.pagingList) this.json.data.pagingList = [];
        if (!this.pagingList || this.pagingList.length == 0) {
            if (this.json.data.pagingList.length) {
                this.json.data.pagingList.each(function (json) {
                    this.pagingList.push(new MWF.xApplication.query.StatementDesigner.View.Paging(json, this.json.data.pagingList, this))
                }.bind(this));
            } else {
                this.pagingList.push(new MWF.xApplication.query.StatementDesigner.View.Paging(null, this.json.data.pagingList, this))
            }
        }
        // if( !noSetHeight )this.setContentHeight();
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
        if (name == "data.actionbarHidden") {
            if (this.json.data.actionbarHidden) {
                this.hideActionbar()
            } else {
                this.showActionbar()
            }
        }
        if (name == "data.selectAllEnable") {
            if (this.json.data.selectAllEnable) {
                this.viewTitleTrNode.getElement(".viewTitleCheckboxTd").setStyle("display", "table-cell");
                this.viewContentTableNode.getElements(".viewContentCheckboxTd").setStyle("display", "table-cell");
            } else {
                this.viewTitleTrNode.getElement(".viewTitleCheckboxTd").setStyle("display", "none");
                this.viewContentTableNode.getElements(".viewContentCheckboxTd").setStyle("display", "none");
            }
        }
        if (name == "data.viewStyleType") {

            var file = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].file : null;
            var extendFile = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType].extendFile : null;
            this.loadTemplateStyles(file, extendFile, function (templateStyles) {
                this.templateStyles = templateStyles;

                var oldFile, oldExtendFile;
                if (oldValue && this.stylesList[oldValue]) {
                    oldFile = this.stylesList[oldValue].file;
                    oldExtendFile = this.stylesList[oldValue].extendFile;
                }
                this.loadTemplateStyles(oldFile, oldExtendFile, function (oldTemplateStyles) {

                    this.json.data.styleConfig = (this.stylesList && this.json.data.viewStyleType) ? this.stylesList[this.json.data.viewStyleType] : null;

                    if (oldTemplateStyles["view"]) this.clearTemplateStyles(oldTemplateStyles["view"]);
                    if (this.templateStyles["view"]) this.setTemplateStyles(this.templateStyles["view"]);
                    this.setAllStyles();

                    this.actionbarList.each(function (module) {
                        if (oldTemplateStyles["actionbar"]) {
                            module.clearTemplateStyles(oldTemplateStyles["actionbar"]);
                        }
                        module.setStyleTemplate();
                        module.setAllStyles();
                    })

                    this.pagingList.each(function (module) {
                        if (oldTemplateStyles["paging"]) {
                            module.clearTemplateStyles(oldTemplateStyles["paging"]);
                        }
                        module.setStyleTemplate();
                        module.setAllStyles();
                    });

                    // this.moduleList.each(function(module){
                    //     if (oldTemplateStyles[module.moduleName]){
                    //         module.clearTemplateStyles(oldTemplateStyles[module.moduleName]);
                    //     }
                    //     module.setStyleTemplate();
                    //     module.setAllStyles();
                    // }.bind(this));
                }.bind(this))

            }.bind(this))
        }
        if (name == "data.viewStyles") {
            this.setCustomStyles();
        }
    },

    loadTemplateStyle: function (callback) {
        this.loadStylesList(function () {
            var oldStyleValue = "";
            if ((!this.json.data.viewStyleType) || !this.stylesList[this.json.data.viewStyleType]) this.json.data.viewStyleType = "default";
            this.loadTemplateStyles(this.stylesList[this.json.data.viewStyleType].file, this.stylesList[this.json.data.viewStyleType].extendFile,
                function (templateStyles) {
                    this.templateStyles = templateStyles;
                    if (!this.json.data.viewStyleType) this.json.data.viewStyleType = "default";

                    if (this.templateStyles && this.templateStyles["view"]) {
                        var viewStyles = Object.clone(this.templateStyles["view"]);
                        if (viewStyles.contentGroupTd) delete viewStyles.contentGroupTd;
                        if (viewStyles.groupCollapseNode) delete viewStyles.groupCollapseNode;
                        if (viewStyles.groupExpandNode) delete viewStyles.groupExpandNode;
                        if (!this.json.data.viewStyles) {
                            this.json.data.viewStyles = viewStyles;
                        } else {
                            this.setTemplateStyles(viewStyles);
                        }
                    }

                    this.setCustomStyles();

                    if (callback) callback();
                }.bind(this)
            );
        }.bind(this));
    },
    clearTemplateStyles: function (styles) {
        if (styles) {
            if (styles.container) this.removeStyles(styles.container, "container");
            if (styles.table) this.removeStyles(styles.table, "table");
            if (styles.titleTr) this.removeStyles(styles.titleTr, "titleTr");
            if (styles.titleTd) this.removeStyles(styles.titleTd, "titleTd");
            if (styles.contentTr) this.removeStyles(styles.contentTr, "contentTr");
            if (styles.contentSelectedTr) this.removeStyles(styles.contentSelectedTr, "contentSelectedTr");
            if (styles.contentTd) this.removeStyles(styles.contentTd, "contentTd");
            // if (styles.contentGroupTd) this.removeStyles(styles.contentGroupTd, "contentGroupTd");
            // if (styles.groupCollapseNode) this.removeStyles(styles.groupCollapseNode, "groupCollapseNode");
            // if (styles.groupExpandNode) this.removeStyles(styles.groupExpandNode, "groupExpandNode");
            if (styles.checkboxNode) this.removeStyles(styles.checkboxNode, "checkboxNode");
            if (styles.checkedCheckboxNode) this.removeStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
            if (styles.radioNode) this.removeStyles(styles.radioNode, "radioNode");
            if (styles.checkedRadioNode) this.removeStyles(styles.checkedRadioNode, "checkedRadioNode");
            if (styles.tableProperties) this.removeStyles(styles.tableProperties, "tableProperties");
        }
    },

    setTemplateStyles: function (styles) {
        if (styles.container) this.copyStyles(styles.container, "container");
        if (styles.table) this.copyStyles(styles.table, "table");
        if (styles.titleTr) this.copyStyles(styles.titleTr, "titleTr");
        if (styles.titleTd) this.copyStyles(styles.titleTd, "titleTd");
        if (styles.contentTr) this.copyStyles(styles.contentTr, "contentTr");
        if (styles.contentSelectedTr) this.copyStyles(styles.contentSelectedTr, "contentSelectedTr");
        if (styles.contentTd) this.copyStyles(styles.contentTd, "contentTd");
        // if (styles.contentGroupTd) this.copyStyles(styles.contentGroupTd, "contentGroupTd");
        // if (styles.groupCollapseNode) this.copyStyles(styles.groupCollapseNode, "groupCollapseNode");
        // if (styles.groupExpandNode) this.copyStyles(styles.groupExpandNode, "groupExpandNode");
        if (styles.checkboxNode) this.copyStyles(styles.checkboxNode, "checkboxNode");
        if (styles.checkedCheckboxNode) this.copyStyles(styles.checkedCheckboxNode, "checkedCheckboxNode");
        if (styles.radioNode) this.copyStyles(styles.radioNode, "radioNode");
        if (styles.checkedRadioNode) this.copyStyles(styles.checkedRadioNode, "checkedRadioNode");
        if (styles.tableProperties) this.copyStyles(styles.tableProperties, "tableProperties");
    },
    removeStyles: function (from, to) {
        if (this.json.data.viewStyles[to]) {
            Object.each(from, function (style, key) {
                if (this.json.data.viewStyles[to][key] && this.json.data.viewStyles[to][key] == style) {
                    delete this.json.data.viewStyles[to][key];
                }
            }.bind(this));
        }
    },
    copyStyles: function (from, to) {
        if (!this.json.data.viewStyles[to]) this.json.data.viewStyles[to] = {};
        Object.each(from, function (style, key) {
            if (!this.json.data.viewStyles[to][key]) this.json.data.viewStyles[to][key] = style;
        }.bind(this));
    }
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

MWF.xApplication.query.StatementDesigner.View.Column = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Column,
    initialize: function (json, view, next) {
        this.propertyPath = "../x_component_query_StatementDesigner/$Statement/column.html";
        this.view = view;
        this.json = json;
        this.next = next;
        this.css = this.view.css;
        this.content = this.view.viewTitleTrNode;
        this.domListNode = this.view.domListNode;
        this.load();
    },
    refreshColumnPathData: function () {
        if (this.property) {
            this.property.loadDataPathSelect();
        }
    },
    getColumnDataPath: function () {
        return this.view.statement.getColumnDataPath();
    },
    showProperty: function () {
        if (!this.property) {
            this.property = new MWF.xApplication.query.StatementDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
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
        if (this.view.statement.currentSelectedModule) {
            if (this.view.statement.currentSelectedModule == this) {
                return true;
            } else {
                this.view.statement.currentSelectedModule.unSelected();
            }
        }
        this.view.domListNode.show();
        this.node.setStyles(this.css.viewTitleColumnNode_selected);
        this.listNode.setStyles(this.css.cloumnListNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        new Fx.Scroll(this.view.designer.propertyDomArea, {
            "wheelStops": false,
            "duration": 100
        }).toElement(this.listNode);

        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
    },
    unSelected: function () {
        this.view.statement.currentSelectedModule = null;
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
    }
});

MWF.xApplication.query.StatementDesigner.View.Actionbar = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Actionbar,
    initialize: function (json, jsonList, view, options) {
        this.setOptions(options);
        this.propertyPath = "../x_component_query_StatementDesigner/$Statement/actionbar.html";
        this.path = "../x_component_query_ViewDesigner/$View/";
        this.imagePath_default = "../x_component_query_ViewDesigner/$View/";
        this.imagePath_custom = "../x_component_process_FormDesigner/Module/Actionbar/";
        this.cssPath = "../x_component_query_ViewDesigner/$View/" + this.options.style + "/actionbar.wcss";

        this.view = view;
        this.json = json;
        this.jsonList = jsonList;
        this.css = this.view.css;
        this.container = this.view.actionbarNode;
        this.moduleName = "actionbar";
        this.load();
    },
    getJsonPath: function () {
        return "../x_component_query_StatementDesigner/$Statement/toolbars.json";
    },
    selected: function () {
        if (this.view.statement.currentSelectedModule) {
            if (this.view.statement.currentSelectedModule == this) {
                return true;
            } else {
                this.view.statement.currentSelectedModule.unSelected();
            }
        }
        this.view.domListNode.show();
        this.node.setStyles(this.css.toolbarWarpNode_selected);
        //this.listNode.setStyles(this.css.cloumnListNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        //new Fx.Scroll(this.view.designer.propertyDomArea, {"wheelStops": false, "duration": 100}).toElement(this.listNode);

        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        //this._showActions();
        this.showProperty();
    },
    unSelected: function () {
        this.view.statement.currentSelectedModule = null;
        this.node.setStyles(this.css.toolbarWarpNode);

        //this.listNode.setStyles(this.css.cloumnListNode);
        this.isSelected = false;
        //this._hideActions();
        this.hideProperty();
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.StatementDesigner.Property(this, this.view.designer.propertyContentArea, this.view.designer, {
                "path": this.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    }
});

MWF.xApplication.query.StatementDesigner.View.Paging = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View.Paging,
    selected: function () {
        if (this.view.statement.currentSelectedModule) {
            if (this.view.statement.currentSelectedModule == this) {
                return true;
            } else {
                this.view.statement.currentSelectedModule.unSelected();
            }
        }
        this.view.domListNode.show();
        this.node.setStyles(this.css.pagingWarpNode_selected);
        new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);

        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
    },
    unSelected: function () {
        this.view.statement.currentSelectedModule = null;
        this.node.setStyles(this.css.pagingWarpNode);

        this.isSelected = false;
        this.hideProperty();
    }
});