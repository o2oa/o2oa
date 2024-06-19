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
        // if (!this.json.type) this.json.type = "select";
        if (!this.json.format) this.json.format = "jpql";
        if (!this.json.entityCategory) this.json.entityCategory = "official";
        if (!this.json.countMethod){
            if( this.json.countData || this.json.countScriptText ){
                this.json.countMethod = "assign";
            }else{
                this.json.countMethod = "auto";
            }
        }
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

        this.selectMode = "statement";
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
    loadStatementTab: function (callback) {
        var _self = this;
        MWF.require("MWF.widget.Tab", null, false);

        this.statementTab = new MWF.widget.Tab(this.statementTabNode, {"style": "script"});
        this.statementTab.load();

        this.tabQueryNode = Element("div");
        this.queryTabPageNode.inject(this.tabQueryNode);
        this.queryPage = this.statementTab.addTab(this.tabQueryNode, this.designer.lp.queryStatement);
        this.queryPage.addEvent("postShow", function(){
            switch (this.json.format) {
                case "script":
                    if( this.jpqlScriptEditor ){
                        this.jpqlScriptEditor.container.setStyle("height", ""+this.getEditorHeight()+"px");
                        this.jpqlScriptEditor.resizeContentNodeSize();
                    }
                    break;
                case "sql":
                    if(this.sqlEditor)this.sqlEditor.resize();
                    break;
                case "sqlScript":
                    if( this.sqlScriptEditor ){
                        this.sqlScriptEditor.container.setStyle("height", ""+this.getEditorHeight()+"px");
                        this.sqlScriptEditor.resizeContentNodeSize();
                    }
                    break;
                default:
                    if(this.jpqlEditor)this.jpqlEditor.resize();
            }
            this.loadEditor();
        }.bind(this))

        this.tabCountNode = Element("div");
        this.countTabPageNode.inject(this.tabCountNode);
        this.countPage = this.statementTab.addTab(this.tabCountNode, this.designer.lp.countStatement);
        this.countPage.addEvent("postShow", function(){
            switch (this.json.format) {
                case "script":
                    if( this.jpqlCountScriptEditor ){
                        this.jpqlCountScriptEditor.container.setStyle("height", ""+this.getEditorHeight()+"px");
                        this.jpqlCountScriptEditor.resizeContentNodeSize();
                    }
                    break;
                case "sql":
                    if( this.sqlCountEditor )this.sqlCountEditor.resize();
                    break;
                case "sqlScript":
                    if( this.sqlCountScriptEditor ){
                        this.sqlCountScriptEditor.container.setStyle("height", ""+this.getEditorHeight()+"px");
                        this.sqlCountScriptEditor.resizeContentNodeSize();
                    }
                    break;
                default:
                    if( this.jpqlCountEditor )this.jpqlCountEditor.resize();
            }
            this.loadEditor();
        }.bind(this))

        // this.tabSqlNode = Element("div");
        // this.sqlTabPageNode.inject(this.tabSqlNode);
        //
        // this.tabCountSqlNode = Element("div");
        // this.countSqlTabPageNode.inject(this.tabCountSqlNode);


        this.queryPage.showTabIm();

        if( ["auto", "ignore"].contains(this.json.countMethod) ){
            this.countPage.disableTab();
        }

        // this.queryPage.addEvent("postShow", function(){
        //     if( this.view ){
        //         this.view.setContentHeight();
        //         this.view.selected();
        //     }
        // }.bind(this));
        // this.countPage.addEvent("postShow", function(){
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
                //this.view.setContentHeight();
                this.view.setViewWidth();
                this.view.selected();
            }
        }.bind(this));
        this.runPage.addEvent("postShow", function () {
            this.selected();
        }.bind(this));

        if( this.options.viewEnable === false ){
            this.viewPage.disableTab();
        }

    },
    loadStatement: function () {
        //this.statementDesignerNode = new Element("div", {"styles": this.css.statementDesignerNode}).inject(this.areaNode);
        this.loadStatementHtml(function () {
            this.designerArea = this.areaNode.getElement(".o2_statement_statementDesignerNode");

            this.statementTabNode = this.areaNode.getElement(".o2_statement_statementTabNode");

            this.queryTabPageNode = this.areaNode.getElement(".o2_statement_statementQueryTabPageNode");

            this.jpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerJpql");
            this.jpqlScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerScript");
            this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");

            this.sqlArea = this.areaNode.getElement(".o2_statement_statementDesignerSql");
            this.sqlScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerSqlScript");
            this.sqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerSqlLine");

            this.countTabPageNode = this.areaNode.getElement(".o2_statement_statementCountTabPageNode");

            this.jpqlCountArea = this.areaNode.getElement(".o2_statement_statementDesignerCountJpql");
            this.jpqlCountScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerCountScript");
            this.jpqlCountEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerCountJpqlLine");

            this.sqlCountArea = this.areaNode.getElement(".o2_statement_statementDesignerCountSql");
            this.sqlCountScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerCountSqlScript");
            this.sqlCountEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerCountSqlLine");

            this.formatTypeArea = this.areaNode.getElement(".o2_statement_statementDesignerFormatContent");
            this.formatTypeArea.getElements("input").each(function (input) {
                input.set("name", input.get("name") + "_" +this.json.id);
            }.bind(this));
            this.entityCategorySelect = this.areaNode.getElement(".o2_statement_statementDesignerCategoryContent").getElement("select");

            this.dynamicTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_dynamic");

            this.officialTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_official");
            this.jpqlOfficalTable = this.areaNode.getElement(".o2_statement_statementDesignerOfficialTable_JPQL");
            this.sqlOfficalTable = this.areaNode.getElement(".o2_statement_statementDesignerOfficialTable_SQL");

            this.customTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_custom");

            this.dynamicTableSelect = this.areaNode.getElement(".o2_statement_statementDesignerSelectTable");
            this.officialTableSelectJPQL = this.jpqlOfficalTable.getElement("select");
            this.officialTableSelectSQL = this.sqlOfficalTable.getElement("select");

            this.fieldSelect = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_field").getElement("select");

            this.dynamicTableContent = this.areaNode.getElement(".o2_statement_statementDesignerTableContent");

            // this.statementTypeSelect = this.areaNode.getElement(".o2_statement_statementDesignerTypeContent").getElement("select");
            // this.loadStatementTypeSelect();

            this.countMethodSelect = this.areaNode.getElement(".o2_statement_statementDesignerCountMethodContent").getElement("select");
            this.loadCountMethodSelect();

            // this.jpqlSelectEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_select");
            // this.jpqlUpdateEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_update");
            // this.jpqlDeleteEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_sdelete");

            // this.jpqlSelectEditor_selectContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_selectContent");
            // this.jpqlSelectEditor_fromContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_fromContent");
            // this.jpqlSelectEditor_whereContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_whereContent");


            this.loadStatementTab();

            this.resizeNode = this.areaNode.getElement(".o2_statement_resizeNode");

            this.tabNode = this.areaNode.getElement(".o2_statement_tabNode");

            this.runArea = this.areaNode.getElement(".o2_statement_statementRunNode");
            // this.runTitleNode = this.areaNode.getElement(".o2_statement_statementRunTitleNode");
            this.runContentNode = this.areaNode.getElement(".o2_statement_statementRunContentNode");

            this.runJsonNode = this.runContentNode.getElement(".o2_statement_statementRunJsonNode");
            this.runFilterNode = this.runContentNode.getElement(".o2_statement_statementRunFilterNode");
            this.runPageNoInput = this.runContentNode.getElement(".o2_statement_statementRunPageNoInput");
            this.runPageSizeInput = this.runContentNode.getElement(".o2_statement_statementRunPageSizeInput");


            this.runActionNode = this.runContentNode.getElement(".o2_statement_statementRunActionNode");
            this.runResultNode = this.runContentNode.getElement(".o2_statement_statementRunResultNode");
            // this.runDefaultNode = this.runContentNode.getElement(".o2_statement_statementRunDefaultContent");
            this.setRunnerSize();
            this.designer.addEvent("resize", this.setRunnerSize.bind(this));
            debugger;

            this.loadFieldSelect();

            switch (this.json.format) {
                case "script":
                    this.jpqlOfficalTable.show();
                    this.sqlOfficalTable.hide();

                    this.loadJpqlScriptEditor();
                    this.loadJpqlCountScriptEditor();
                    break;
                case "sql":
                    this.jpqlOfficalTable.hide();
                    this.sqlOfficalTable.show();

                    this.loadSqlEditor();
                    this.loadSqlCountEditor();
                    break;
                case "sqlScript":
                    this.jpqlOfficalTable.hide();
                    this.sqlOfficalTable.show();

                    this.loadSqlScriptEditor();
                    this.loadSqlCountScriptEditor();
                    break;
                default:
                    this.jpqlOfficalTable.show();
                    this.sqlOfficalTable.hide();

                    this.loadJpqlEditor();
                    this.loadJpqlCountEditor();
            }
            // this.loadDefaultCondition();
            this.loadStatementRunner();

            this.viewArea = this.areaNode.getElement(".o2_statement_viewNode");
            this.loadView();

            this.loadTab();

            if( this.json.table ){
                o2.Actions.load("x_query_assemble_designer").TableAction.get( this.json.table, function(json){
                    this.json.tableObj = json.data;
                    this.setDynamicTableName();
                }.bind(this), function(){
                    return true;
                });
            }

            this.setEvent();
            this.loadVerticalResize();
        }.bind(this));
    },
    // loadDefaultCondition: function(){
    //     var lp = ["currentPerson","currentIdentity","currentPersonDirectUnit","currentPersonAllUnit","currentPersonGroupList","currentPersonRoleList"];
    //     ["person","identityList","unitList","unitAllList","groupList","roleList"].each(function (key, i) {
    //         var div = new Element("div", {
    //             style: "float:left;margin-right:10px;"
    //         }).inject(this.runDefaultNode);
    //         new Element("input", {
    //             type: "checkbox",
    //             value: "@"+key,
    //             name: this.json.id + "defaultConditoin",
    //             id: this.json.id + "defaultConditoin" + key
    //         }).inject(div);
    //         new Element("label", {
    //             for: this.json.id + "defaultConditoin" + key,
    //             text: this.designer.lp[lp[i]]
    //         }).inject(div);
    //     }.bind(this));
    // },
    loadCountMethodSelect: function(){
        this.countMethodSelect.getElements("option").each(function(o){
            if( this.json.countMethod === o.value ){
                o.selected = true;
            }
        }.bind(this));
    },
    // loadStatementTypeSelect : function(){
    //   this.statementTypeSelect.empty();
    //   var optionList = [{text:"SELECT", value:"select"}];
    //     if( this.data.entityCategory === "dynamic" || (this.data.description && this.data.description.indexOf("update")>-1)){
    //       optionList = optionList.concat([
    //           {text:"UPDATE", value:"update"},
    //           {text:"DELETE", value:"delete"}
    //        ])
    //   }
    //
    //   var flag = true;
    //     optionList.each( function ( field ) {
    //         var option = new Element("option", {
    //             "text": field.text,
    //             "value": field.value
    //         }).inject(this.statementTypeSelect);
    //         if( this.json.type === field.value ){
    //             flag = false;
    //             option.selected = true;
    //         }
    //     }.bind(this));
    //     if( flag ){
    //         this.statementTypeSelect.options[0].selected = true;
    //         this.json.type = this.statementTypeSelect.options[0].value;
    //     }
    // },
    loadFieldSelect : function(){
        this.fieldSelect.empty();
        var d = this.data;
        var className = d.entityCategory === "dynamic" ? d.table : d.entityClassName;
        if( !className )return;
        var pre = ["sql", "sqlScript"].contains(d.format) ? "x" : "";
        o2.Actions.load("x_query_assemble_designer").QueryAction.getEntityProperties(
            className,
            d.entityCategory,
            function(json){
                json = Object.clone(json);
                var option = new Element("option", { "text": this.designer.lp.fileldSelectNote, "value": "" }).inject(this.fieldSelect);
                option.store("type", d.entityCategory);
                option.store("tableName", className );
                (json.data||[]).each( function ( field ) {
                    if( pre )field.name = pre + field.name;
                    var option = new Element("option", {
                        "text": field.name + ( field.description ? ("-" + field.description) : "" ),
                        "value": field.name
                    }).inject(this.fieldSelect);
                    option.store("field", field);
                    option.store("type", d.entityCategory );
                    option.store("tableName", className );
                }.bind(this));
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

        var editorHeight = designAreaHeight - 98;

        if(this.jpqlEditorNode)this.jpqlEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.jpqlCountEditorNode)this.jpqlCountEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.jpqlScriptArea)this.jpqlScriptArea.setStyle( "height", ""+editorHeight+"px" );
        if(this.jpqlCountScriptArea)this.jpqlCountScriptArea.setStyle( "height", ""+editorHeight+"px" );

        if(this.sqlEditorNode)this.sqlEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.sqlCountEditorNode)this.sqlCountEditorNode.setStyle( "height", ""+editorHeight+"px" );
        if(this.sqlScriptArea)this.sqlScriptArea.setStyle( "height", ""+editorHeight+"px" );
        if(this.sqlCountScriptArea)this.sqlCountScriptArea.setStyle( "height", ""+editorHeight+"px" );

        if( this.jpqlEditor )this.jpqlEditor.resize();
        if( this.jpqlCountEditor )this.jpqlCountEditor.resize();
        if( this.jpqlScriptEditor ){
            this.jpqlScriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.jpqlScriptEditor.resizeContentNodeSize();
        }
        if( this.jpqlCountScriptEditor ){
            this.jpqlCountScriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.jpqlCountScriptEditor.resizeContentNodeSize();
        }

        if( this.sqlEditor )this.sqlEditor.resize();
        if( this.sqlCountEditor )this.sqlCountEditor.resize();
        if( this.sqlScriptEditor ){
            this.sqlScriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.sqlScriptEditor.resizeContentNodeSize();
        }
        if( this.sqlCountScriptEditor ){
            this.sqlCountScriptEditor.container.setStyle("height", ""+editorHeight+"px");
            this.sqlCountScriptEditor.resizeContentNodeSize();
        }

        // this.tabNode.setStyle("height", ""+runAreaHeight+"px");
        this.setRunnerSize();
        if( this.view ){
            this.setViewSize();
            this.view.setContentHeight()
        }
    },
    getEditorHeight: function(){
        var size = this.areaNode.getSize();
        var height = size.y;
        var designAreaHeight = this.designerAreaPercent*height - 52;
        return designAreaHeight - 98;
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

    loadJpqlEditor: function () {
        if (!this.jpqlEditor) {
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
                        value = "SELECT o FROM " + table + " o";
                // }
                this.json.data = value;
            }
            if( this.jpqlEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadJpqlEditor();
                    this.queryPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.queryPage.addEvent("postShow", postShowFun);
            }else{
                this._loadJpqlEditor();
            }
        }else{
            this.jpqlEditor.resize();
        }
    },
    _loadJpqlEditor: function () {
        if (!this.jpqlEditor) {
            o2.require("o2.widget.JavascriptEditor", function () {
                this.jpqlEditor = new o2.widget.JavascriptEditor(this.jpqlEditorNode, {
                    "title": "JPQL",
                    "option": {"mode": "sql"}
                });
                this.jpqlEditor.load(function () {
                    this.jpqlEditor.editor.setValue(this.json.data);
                    this.jpqlEditor.addEditorEvent("change", function () {
                        this.data.data = this.jpqlEditor.getValue();
                        // this.checkStatementType();
                    }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }
    },

    loadJpqlCountEditor: function () {
        if (!this.jpqlCountEditor) {
            if( !this.json.countData )this.json.countData = "SELECT count(o.id) FROM table o";
            if( this.jpqlCountEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadJpqlCountEditor();
                    this.countPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.countPage.addEvent("postShow", postShowFun);
            }else{
                this._loadJpqlCountEditor();
            }
        }else{
            this.jpqlCountEditor.resize();
        }
    },
    _loadJpqlCountEditor : function(){
        o2.require("o2.widget.JavascriptEditor", function () {
            this.jpqlCountEditor = new o2.widget.JavascriptEditor(this.jpqlCountEditorNode, {
                "title": "JPQL",
                "option": {"mode": "sql"}
            });
            this.jpqlCountEditor.load(function () {
                this.jpqlCountEditor.editor.setValue(this.json.countData);

                this.jpqlCountEditor.addEditorEvent("change", function () {
                    this.data.countData = this.jpqlCountEditor.getValue();
                }.bind(this));
            }.bind(this));
        }.bind(this), false);
    },

    loadSqlEditor: function () {
        if (!this.sqlEditor) {
            var value;
            if( !this.json.sql ){
                var table = "table";
                // switch (this.json.type) {
                //     case "update":
                //         value = "UPDATE " + table + " SET ";
                //         break;
                //     case "delete":
                //         value = "DELETE FROM " + table + " WHERE ";
                //         break;
                //     default:
                        value = "SELECT * FROM " + table + "";
                // }
                this.json.sql = value;
            }
            if( this.sqlEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadSqlEditor();
                    this.queryPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.queryPage.addEvent("postShow", postShowFun);
            }else{
                this._loadSqlEditor();
            }
        }else{
            this.sqlEditor.resize();
        }
    },
    _loadSqlEditor: function () {
        if (!this.sqlEditor) {
            o2.require("o2.widget.JavascriptEditor", function () {
                this.sqlEditor = new o2.widget.JavascriptEditor(this.sqlEditorNode, {
                    "title": "SQL",
                    "option": {"mode": "sql"}
                });
                this.sqlEditor.load(function () {
                    this.sqlEditor.editor.setValue(this.json.sql);

                    this.sqlEditor.addEditorEvent("change", function () {
                        this.data.sql = this.sqlEditor.getValue();
                        // this.checkStatementType();
                    }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }
    },

    loadSqlCountEditor: function () {
        if (!this.sqlCountEditor) {
            if( !this.json.sqlCount )this.json.sqlCount = "SELECT count(id) FROM table";
            if( this.sqlCountEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
                var postShowFun = function() {
                    this._loadSqlCountEditor();
                    this.countPage.removeEvent("postShow", postShowFun);
                }.bind(this);
                this.countPage.addEvent("postShow", postShowFun);
            }else{
                this._loadSqlCountEditor();
            }
        }else{
            // this.sqlCountEditorNode.setStyle( "height", ""+editorHeight+"px" );
            this.sqlCountEditor.resize();
        }
    },
    _loadSqlCountEditor : function(){
        if (!this.sqlCountEditor) {
            o2.require("o2.widget.JavascriptEditor", function () {
                this.sqlCountEditor = new o2.widget.JavascriptEditor(this.sqlCountEditorNode, {
                    "title": "SQL",
                    "option": {"mode": "sql"}
                });
                this.sqlCountEditor.load(function () {
                    this.sqlCountEditor.editor.setValue(this.json.sqlCount);
                    this.sqlCountEditor.addEditorEvent("change", function () {
                        this.data.sqlCount = this.sqlCountEditor.getValue();
                    }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }
    },

    loadJpqlScriptEditor: function () {
        if (!this.jpqlScriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.jpqlScriptEditor = new o2.widget.ScriptArea(this.jpqlScriptArea, {
                    "isbind": false,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.scriptTitle,
                    "type": "service",
                    "onChange": function () {
                        this.json.scriptText = this.jpqlScriptEditor.toJson().code;
                    }.bind(this),
                    "onSave": function () {
                        this.designer.saveStatement();
                    }.bind(this)
                });
                this.jpqlScriptEditor.load({"code": this.json.scriptText})
            }.bind(this), false);
        }
    },
    loadJpqlCountScriptEditor: function () {
        if (!this.jpqlCountScriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.jpqlCountScriptEditor = new o2.widget.ScriptArea(this.jpqlCountScriptArea, {
                    "isbind": false,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.scriptTitle,
                    "type": "service",
                    "onChange": function () {
                        this.json.countScriptText = this.jpqlCountScriptEditor.toJson().code;
                    }.bind(this),
                    "onSave": function () {
                        this.designer.saveStatement();
                    }.bind(this)
                });
                this.jpqlCountScriptEditor.load({"code": this.json.countScriptText})
            }.bind(this), false);
        }
    },

    loadSqlScriptEditor: function () {
        if (!this.sqlScriptEditor) {
            debugger;
            o2.require("o2.widget.ScriptArea", function () {
                this.sqlScriptEditor = new o2.widget.ScriptArea(this.sqlScriptArea, {
                    "isbind": false,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.sqlScriptTitle,
                    "type": "service",
                    "onChange": function () {
                        this.json.sqlScriptText = this.sqlScriptEditor.toJson().code;
                    }.bind(this),
                    "onSave": function () {
                        this.designer.saveStatement();
                    }.bind(this)
                });
                this.sqlScriptEditor.load({"code": this.json.sqlScriptText})
            }.bind(this), false);
        }
    },
    loadSqlCountScriptEditor: function () {
        if (!this.sqlCountScriptEditor) {
            o2.require("o2.widget.ScriptArea", function () {
                this.sqlCountScriptEditor = new o2.widget.ScriptArea(this.sqlCountScriptArea, {
                    "isbind": false,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    "title": this.designer.lp.sqlScriptTitle,
                    "type": "service",
                    "onChange": function () {
                        this.json.sqlCountScriptText = this.sqlCountScriptEditor.toJson().code;
                    }.bind(this),
                    "onSave": function () {
                        this.designer.saveStatement();
                    }.bind(this)
                });
                this.sqlCountScriptEditor.load({"code": this.json.sqlCountScriptText})
            }.bind(this), false);
        }
    },

    // setSatementTable: function () {
    //     if (!this.json.type) this.json.type = "select";
    //     this.changeType(this.json.type, true);
    // },

    // checkStatementType: function () {
    //     var str = this.json.data;
    //     this.json.data = str;
    //     var jpql_select = /^select/i;
    //     var jpql_update = /^update/i;
    //     var jpql_delete = /^delete/i;
    //     if (jpql_select.test(str)) return this.changeType("select");
    //     if (jpql_update.test(str)) return this.changeType("update");
    //     if (jpql_delete.test(str)) return this.changeType("delete");
    // },
    // changeType: function (type, force) {
    //     if (this.json.type != type) this.json.type = type;
    //     if (type != this.statementTypeSelect.options[this.statementTypeSelect.selectedIndex].value || force) {
    //         for (var i = 0; i < this.statementTypeSelect.options.length; i++) {
    //             if (this.statementTypeSelect.options[i].value == type) {
    //                 this.statementTypeSelect.options[i].set("selected", true);
    //                 break;
    //             }
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
    addFilterSample: function(){
        var filterList = this.filterListEditor.editor.getValue() || [];
        try{
            filterList = JSON.parse( filterList );
        }catch (e) {
            filterList = [];
        }
        filterList.push({
            "path": ["sql", "sqlScript"].contains(this.json.format) ? "xtitle" : "o.title",
            "comparison":"like",
            "value": ["sql", "sqlScript"].contains(this.json.format) ? "xtitle" : "o_title",
            "formatType":"textValue"
        });
        this.filterListEditor.editor.setValue( JSON.stringify(filterList, null, 4) );

        var parameter = this.jsonEditor.editor.getValue() || {};
        try{
            parameter = JSON.parse( parameter );
        }catch (e) {
            parameter = {};
        }
        parameter[ ["sql", "sqlScript"].contains(this.json.format) ? "xtitle" : "o_title" ] = "%关于%";
        this.jsonEditor.editor.setValue( JSON.stringify(parameter, null, 4) );

    },
    loadStatementRunner: function () {
        o2.require("o2.widget.JavascriptEditor", function () {
            this.jsonEditor = new o2.widget.JavascriptEditor(this.runJsonNode, {
                "title": "parameter",
                "option": {"mode": "json"}
            });
            this.jsonEditor.load(function () {
                debugger;
                var json = JSON.parse( this.data.testParameters || "{}" );
                if( json.parameter )json = json.parameter;
                this.jsonEditor.editor.setValue( JSON.stringify(json, null, 4) );
            }.bind(this));

            this.filterListEditor = new o2.widget.JavascriptEditor(this.runFilterNode, {
                "title": "filterList",
                "option": {"mode": "json"}
            });
            this.filterListEditor.load(function () {
                var json = JSON.parse( this.data.testParameters || "{}" );
                json = json.filterList || [];
                this.filterListEditor.editor.setValue( JSON.stringify(json, null, 4) );
            }.bind(this));
        }.bind(this), false);
    },
    getSQLTableByEntity: function(entityClassName){
        switch (entityClassName) {
            case "com.x.processplatform.core.entity.content.Task":
                return "PP_C_TASK";
            case "com.x.processplatform.core.entity.content.TaskCompleted":
                return "PP_C_TASKCOMPLETED";
            case "com.x.processplatform.core.entity.content.Read":
                return "PP_C_READ";
            case "com.x.processplatform.core.entity.content.ReadCompleted":
                return "PP_C_READCOMPLETED";
            case "com.x.processplatform.core.entity.content.Work":
                return "PP_C_WORK";
            case "com.x.processplatform.core.entity.content.WorkCompleted":
                return "PP_C_WORKCOMPLETED";
            case "com.x.processplatform.core.entity.content.Review":
                return "PP_C_REVIEW";
            case "com.x.cms.core.entity.Document":
                return "CMS_DOCUMENT";
            case "com.x.cms.core.entity.Review":
                return "CMS_REVIEW";
            case "com.x.cms.core.entity.DocumentViewRecord":
                return "CMS_DOCUMENT_VIEWRECORD";
            case "com.x.cms.core.entity.DocumentCommentInfo":
                return "CMS_DOCUMENT_COMMENTINFO";
        }
    },
    setDynamicTableName: function(){
        var name = this.json.tableObj && this.json.tableObj.name;
        if( name ){
            if( ["sql", "sqlScript"].contains(this.json.format) ){
                name = "QRY_DYN_" + name.toUpperCase();
            }
            this.dynamicTableContent.set("text", name);
        }else{
            this.dynamicTableContent.set("text", "");
        }
    },
    loadEditor: function(){
        switch (this.json.format) {
            case "sql":
                this.jpqlOfficalTable.hide();
                this.sqlOfficalTable.show();

                this.jpqlArea.hide();
                this.jpqlScriptArea.hide();
                this.sqlArea.show();
                this.sqlScriptArea.hide();
                this.loadSqlEditor();

                this.jpqlCountArea.hide();
                this.jpqlCountScriptArea.hide();
                this.sqlCountArea.show();
                this.sqlCountScriptArea.hide();
                this.loadSqlCountEditor();
                break;
            case "sqlScript":
                this.jpqlOfficalTable.hide();
                this.sqlOfficalTable.show();

                this.jpqlArea.hide();
                this.jpqlScriptArea.hide();
                this.sqlArea.hide();
                this.sqlScriptArea.show();
                this.loadSqlScriptEditor();

                this.jpqlCountArea.hide();
                this.jpqlCountScriptArea.hide();
                this.sqlCountArea.hide();
                this.sqlCountScriptArea.show();
                this.loadSqlCountScriptEditor();
                break;
            case "script":
                this.jpqlOfficalTable.show();
                this.sqlOfficalTable.hide();

                this.jpqlArea.hide();
                this.jpqlScriptArea.show();
                this.sqlArea.hide();
                this.sqlScriptArea.hide();
                this.loadJpqlScriptEditor();

                this.jpqlCountArea.hide();
                this.jpqlCountScriptArea.show();
                this.sqlCountArea.hide();
                this.sqlCountScriptArea.hide();
                this.loadJpqlCountScriptEditor();
                break;
            default:
                this.jpqlOfficalTable.show();
                this.sqlOfficalTable.hide();

                this.jpqlArea.show();
                this.jpqlScriptArea.hide();
                this.sqlArea.hide();
                this.sqlScriptArea.hide();
                this.loadJpqlEditor();

                this.jpqlCountArea.show();
                this.jpqlCountScriptArea.hide();
                this.sqlCountArea.hide();
                this.sqlCountScriptArea.hide();
                this.loadJpqlCountEditor();
        }
    },
    setEvent: function () {
        this.designerArea.addEvent("click", function (e) {
            this.selected();
            e.stopPropagation();
        }.bind(this));
        this.formatTypeArea.getElements("input").addEvent("click", function (e) {
            debugger;
            if (e.target.checked) {
                var v = e.target.get("value");
                this.json.format = v;
                this.loadEditor();
                this.setDynamicTableName();
            }
            this.loadFieldSelect();
            if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.changeStatementType();
            this.checkViewFilter();
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
            // this.loadStatementTypeSelect();
            this.loadFieldSelect();
            if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();
        }.bind(this));
        //@todo change table
        this.officialTableSelectJPQL.addEvent("change", function (e) {
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

        }.bind(this));

        this.officialTableSelectSQL.addEvent("change", function (e) {
            debugger;
            var entityClassName = e.target.options[e.target.selectedIndex].value;
            this.json.entityClassName = entityClassName;
            if( entityClassName ){
                this.changeEditorEntityClassName( this.getSQLTableByEntity(entityClassName) );
            }
            this.loadFieldSelect();

            this.json.table = "";
            this.json.tableObj = null;

            if(this.view && this.view.property && this.view.property.viewFilter)this.view.property.viewFilter.setPathInputSelectOptions();

        }.bind(this));

        this.runActionNode.getFirst().addEvent("click", this.runStatement.bind(this));

        this.dynamicTableSelect.addEvent("click", this.selectTable.bind(this));
        // this.statementTypeSelect.addEvent("change", function () {
        //     var t = this.statementTypeSelect.options[this.statementTypeSelect.selectedIndex].value;
        //     if (t != this.json.type) {
        //         this.json.type = t;
        //     }
        //     if (t !== "select") {
        //         this.queryPage.showTabIm();
        //         this.countPage.disableTab();
        //
        //         this.runPage.showTabIm();
        //         this.viewPage.disableTab();
        //     } else {
        //         if( this.json.countMethod === "assign" )this.countPage.enableTab(true);
        //         this.viewPage.enableTab(true);
        //     }
        // }.bind(this));

        this.countMethodSelect.addEvent("change", function () {
            this.json.countMethod = this.countMethodSelect.options[this.countMethodSelect.selectedIndex].value;
            switch (this.json.countMethod) {
                case "auto":
                case "ignore":
                    // if (this.json.type === "select") {
                        this.queryPage.showTabIm();
                        this.countPage.disableTab();
                    // }
                    break;
                // case "assign":
                //     break;
                default:
                    // if (this.json.type === "select") {
                        this.countPage.enableTab();
                    // }
                    break;
            }
            this.loadEditor();
        }.bind(this));

        this.fieldSelect.addEvent("change", function (ev) {
            var option = ev.target.options[ev.target.selectedIndex];
            var type = option.retrieve("type");
            var field = option.retrieve("field");
            if( !field )return;
            var text = field.name;
            if( this.countPage && this.countPage.isShow && !this.countPage.disabled ) {
                if (this.data.format === "script" && this.jpqlCountScriptEditor.jsEditor) {
                    this.jpqlCountScriptEditor.jsEditor.insertValue(text);
                } else if (this.data.format === "sqlScript" && this.sqlCountScriptEditor.jsEditor) {
                    this.sqlCountScriptEditor.jsEditor.insertValue(text);
                } else if (this.data.format === "sql" && this.sqlCountEditor) {
                    this.sqlCountEditor.insertValue(text);
                } else if (this.jpqlCountEditor) {
                    this.jpqlCountEditor.insertValue(text);
                }
            }else{
                if( this.data.format === "script" && this.jpqlScriptEditor.jsEditor ){
                    this.jpqlScriptEditor.jsEditor.insertValue( text );
                }else if( this.data.format === "sqlScript" && this.sqlScriptEditor.jsEditor ){
                    this.sqlScriptEditor.jsEditor.insertValue( text );
                }else if( this.data.format === "sql" && this.sqlEditor ){
                    this.sqlEditor.insertValue( text );
                }else if( this.jpqlEditor ) {
                    this.jpqlEditor.insertValue(text);
                }
            }
        }.bind(this));
    },
    changeEditorEntityClassName : function( entityClassName ){

        debugger;

        var re, v, replaceClassName;
        if (this.json.format === "jpql") {

            replaceClassName = function (re, v) {
                if( !re )re = /(.*from\s*)/ig;
                //var re2 = /(\s+)/ig;
                var arr = re.exec(v);
                if (arr && arr[0]) {
                    var left = arr[0];
                    v = v.substring(left.length, v.length);
                    //var ar = re2.exec(v);
                    var right = v.substring(v.indexOf(" "), v.length);
                    return left + entityClassName + right;
                }
                return "";
            };

            if (this.jpqlEditor) {
                // if (this.json.type === "update") re = /(.*update\s*)/ig;
                v = replaceClassName( re, this.json.data);
                if (v) {
                    this.json.data = v;
                    this.jpqlEditor.editor.setValue(this.json.data);
                }
            }

            if( this.jpqlCountEditor ){
                v = replaceClassName( re, this.json.countData);
                if (v) {
                    this.json.countData = v;
                    this.jpqlCountEditor.editor.setValue(this.json.countData);
                }
            }
        }else if (this.json.format === "sql") {

            replaceClassName = function (re, v) {
                if( !re )re = /(.*from\s*)/ig;
                //var re2 = /(\s+)/ig;
                var arr = re.exec(v);
                if (arr && arr[0]) {
                    var left = arr[0];
                    v = v.substring(left.length, v.length);
                    //var ar = re2.exec(v);
                    var right;
                    if( v.indexOf(" ") > -1 ){
                        right = v.substring(v.indexOf(" "), v.length);
                    }else{
                        right = "";
                    }
                    return left + entityClassName + right;
                }
                return "";
            };

            if (this.sqlEditor) {
                // if (this.json.type === "update") re = /(.*update\s*)/ig;
                v = replaceClassName( re, this.json.sql);
                if (v) {
                    this.json.sql = v;
                    this.sqlEditor.editor.setValue(this.json.sql);
                }
            }

            if( this.sqlCountEditor ){
                v = replaceClassName( re, this.json.sqlCount);
                if (v) {
                    this.json.sqlCount = v;
                    this.sqlCountEditor.editor.setValue(this.json.sqlCount);
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
                    this.json.table = name;
                    this.json.tableObj = items[0].data;
                    if( name ){
                        this.json.tableObj.nativeTableName = "QRY_DYN_" + name.toUpperCase();
                    }

                    this.officialTableSelectJPQL.options[0].set("selected", true);
                    this.officialTableSelectSQL.options[0].set("selected", true);
                    this.json.entityClassName = "";

                    if( name && ["sql", "sqlScript"].contains(this.json.format) ){
                        name = "QRY_DYN_" + name.toUpperCase();
                    }
                    this.dynamicTableContent.set("text", name);

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
        // if (!this.json.data){
        //     this.designer.notice(this.designer.lp.inputStatementData, "error");
        //     return false;
        // }
        // o2.require("o2.widget.Mask", null, false);
        // this.runMask = new o2.widget.Mask();
        // this.runMask.loadNode(this.node);

        this.saveSilence(function () {
            this.execute(function (json) {
                this.executeData = json;
                o2.require("o2.widget.JsonParse", function () {
                    this.runResultNode.empty();
                    var jsonResult = new o2.widget.JsonParse(json, this.runResultNode);
                    jsonResult.load();
                }.bind(this));
                if (this.view) {
                    var flag = true;
                    // if (this.data.type !== "select") flag = false;
                    if( this.data.viewEnable === false )flag = false;
                    if (this.data.format === "script" && !this.data.scriptText) flag = false;
                    if (this.data.format === "jpql" && !this.data.data) flag = false;
                    if (this.data.format === "sql" && !this.data.sql) flag = false;
                    if (this.data.format === "sqlScript" && !this.data.sqlScriptText) flag = false;
                    if (flag) this.view.loadViewData();
                }
                this.setColumnDataPath(json);
                // this.runMask.hide();
            }.bind(this), function () {
                // if (this.runMask) this.runMask.hide();
            }.bind(this));
        }.bind(this));
    },
    setColumnDataPath: function (json) {
        // if (this.data.type !== "select") return;
        if (this.data.format === "script" && !this.data.scriptText) return;
        if (this.data.format === "jpql" && !this.data.data) return;
        if (this.data.format === "sql" && !this.data.sql) return;
        if (this.data.format === "sqlScript" && !this.data.sqlScriptText) return;
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
    checkViewFilter: function(){
        if( this.json.viewEnable === false )return true;

        var noteFlag = false;
        if (typeOf(this.viewJson) === "object" && this.viewJson.data && this.viewJson.data.customFilterList) {
            this.viewJson.data.customFilterList.each(function (item) {
                if (item.path) {
                    if (["sql", "sqlScript"].contains(this.data.format) && item.path.contains(".")) {
                        noteFlag = true;
                    }
                    if (!["sql", "sqlScript"].contains(this.data.format) && !item.path.contains(".")) {
                        noteFlag = true;
                    }
                }
            }.bind(this));
            if (noteFlag) this.designer.notice(MWF.xApplication.query.StatementDesigner.LP.modifyViewFilterNote, "info");
        }
        return !noteFlag;
    },
    execute: function (success, failure) {
        var json = this.jsonEditor.editor.getValue() || "{}";
        var parameter = JSON.parse(json);

        var filter = this.filterListEditor.editor.getValue() || "[]";
        var filterList = JSON.parse(filter);

        var pageNo = this.runPageNoInput.get("value").toInt();
        var pageSize = this.runPageSizeInput.get("value").toInt();

        var mode = "data";
        // if (this.data.type === "select") {
            var getMode = function (queryName, countName) {
                switch (this.data.countMethod) {
                    case "ignore":
                        return this.data[queryName] ? "data" : false;
                    case "auto":
                        return this.data[queryName] ? "all" : false;
                    default:
                        if (this.data[queryName] && this.data[countName]) {
                            return "all";
                        } else if (this.data[queryName] && !this.data[countName]) {
                            return "data";
                        } else if (!this.data[queryName] && this.data[countName]) {
                            return "count";
                        } else {
                            return false;
                        }
                }
            }.bind(this);
            switch (this.data.format) {
                case "script":
                    mode = getMode("scriptText", "countScriptText");
                    break;
                case "sqlScript":
                    mode = getMode("sqlScriptText", "sqlCountScriptText");
                    break;
                case "sql":
                    mode = getMode("sql", "sqlCount");
                    break;
                default:
                    mode = getMode("data", "countData");
                    break;
            // }
            if( !mode ){
                this.designer.notice(this.designer.lp.inputStatementData, "error");
                return false;
            }
        }
        o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.id, mode, pageNo || 1, pageSize || 50, {
            parameter: parameter,
            filterList: filterList
        }, function (json) {
            if (success) success(json)
        }.bind(this), function (xhr, text, error) {
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
        if (!this.data.name) {
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }

        if( !this.checkViewFilter() ){
            return false;
        }

        if (typeOf(this.viewJson) === "object") {
            if(this.viewJson.data && !this.viewJson.data.group)this.viewJson.data.group = {};
            if(!this.viewJson.pageSize)this.viewJson.pageSize = "20";
            this.data.view = JSON.stringify(this.viewJson);
        }
        // if (this.jpqlEditor) this.data.data = this.jpqlEditor.editor.getValue();
        // if (this.jpqlScriptEditor) this.data.scriptText = this.jpqlScriptEditor.toJson().code;

        var textJson = {};
        if (this.jsonEditor) textJson.parameter = JSON.parse(this.jsonEditor.editor.getValue() || "{}");
        if (this.filterListEditor) textJson.filterList = JSON.parse(this.filterListEditor.editor.getValue() || "[]");
        this.data.testParameters = JSON.stringify(textJson);

        this.designer.actions.saveStatement(this.data, function (json) {
            this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name + "(" + this.data.alias + ")");
            }
            if (callback) callback();
        }.bind(this));
    },
    _setEditStyle: function (name, input, oldValue) {
        if( name === "viewEnable" && this.viewPage ){
            if (this.data.viewEnable === false ) {
                this.viewPage.disableTab();
            } else {
                this.viewPage.enableTab(true);
            }
        }
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

        // if (this.jpqlEditor) this.data.data = this.jpqlEditor.editor.getValue();
        // if (this.jpqlScriptEditor) this.data.scriptText = this.jpqlScriptEditor.toJson().code;

        // if (this.jsonEditor) this.data.testParameters = this.jsonEditor.editor.getValue();

        var textJson = {};
        if (this.jsonEditor) textJson.parameter = JSON.parse(this.jsonEditor.editor.getValue() || "{}");
        if (this.filterListEditor) textJson.filterList = JSON.parse(this.filterListEditor.editor.getValue() || "[]");
        this.data.testParameters = JSON.stringify(textJson);

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
        if( this.json.viewEnable === false ){
            this.designer.notice(this.designer.lp.cannotDisabledViewNotice, "error");
            return;
        }
        if (this.isNewStatement) {
            this.designer.notice(this.designer.lp.saveStatementNotice, "error");
            return;
        }
        // if (this.data.type !== "select") {
        //     this.designer.notice(this.designer.lp.previewNotSelectStatementNotice, "error");
        //     return;
        // }
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
        this.data.id = this.statement.data.id + "_view";

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
    loadViewNodes: function(){
        this.viewAreaNode = new Element("div#viewAreaNode", {"styles": this.css.viewAreaNode}).inject(this.areaNode);
        this.viewTitleNode = new Element("div#viewTitleNode", {"styles": this.css.viewTitleNode}).inject(this.viewAreaNode);

        this.refreshNode = new Element("div", {"styles": this.css.refreshNode}).inject(this.viewTitleNode);
        this.addColumnNode = new Element("div", {"styles": this.css.addColumnNode}).inject(this.viewTitleNode);

        this.viewTitleContentNode = new Element("div", {"styles": this.css.viewTitleContentNode}).inject(this.viewTitleNode);

        this.autoAddColumnsNode = new Element("div.autoAddColumnsNode", {
            styles: this.css.autoAddColumnsNode,
            title: this.designer.lp.autoAddColumns
        }).inject(this.viewTitleContentNode);
        if( this.json.data.selectList && this.json.data.selectList.length ){
            this.autoAddColumnsNode.hide();
        }


        this.viewTitleTableNode = new Element("table", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.viewTitleContentNode);
        this.viewTitleTrNode = new Element("tr", {"styles": this.css.viewTitleTrNode}).inject(this.viewTitleTableNode);


        this.viewContentScrollNode = new Element("div", {"styles": this.css.viewContentScrollNode}).inject(this.viewAreaNode);
        this.viewContentNode = new Element("div", {"styles": this.css.viewContentNode}).inject(this.viewContentScrollNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.viewContentScrollNode, {"style": "view", "distance": 100, "indent": false});
        }.bind(this));

        this.contentLeftNode = new Element("div", {"styles": this.css.contentLeftNode}).inject(this.viewContentNode);
        this.contentRightNode = new Element("div", {"styles": this.css.contentRightNode}).inject(this.viewContentNode);
        this.viewContentBodyNode = new Element("div", {"styles": this.css.viewContentBodyNode}).inject(this.viewContentNode);
        this.viewContentTableNode = new Element("table", {
            "styles": this.css.viewContentTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.viewContentBodyNode);
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
        this.autoAddColumnsNode.addEvent("click", function (e) {
            this.autoAddColumns();
            e.stopPropagation();
        }.bind(this));
    },
    autoAddColumns: function(){
        MWF.require("MWF.widget.UUID", null, false);
        var d = this.statement.data;
        var className = d.entityCategory === "dynamic" ? d.table : d.entityClassName;
        if( !className )return;
        var pre = ["sql", "sqlScript"].contains(d.format) ? "x" : "";

        var p;
        if( d.entityCategory === "dynamic" ){
            p = o2.Actions.load("x_query_assemble_designer").TableAction.get(d.table, function(json){
                if (json){
                    var dataJson = JSON.decode(json.data.data);
                    return dataJson.fieldList || [];
                }
            }.bind(this));
        }else{
            p = o2.Actions.load("x_query_assemble_designer").QueryAction.getEntityProperties(
                className,
                d.entityCategory,
                function(json){
                    return json.data||[];
                }.bind(this)
            );
        }
        Promise.resolve(p).then(function (data){
            this.json.data.selectList = data.map( function ( field ) {
                return {
                    "id": (new MWF.widget.UUID).id,
                    "column": field.name,
                    "path": pre ? (pre + field.name) : field.name,
                    "displayName": field.description || field.name,
                    "orderType": "original"
                }
            }.bind(this));

            this.json.data.selectList.each(function (d) {
                this.items.push(new MWF.xApplication.query.StatementDesigner.View.Column(d, this));
            }.bind(this));
        }.bind(this))
    },
    selected: function () {
        if (this.statement.currentSelectedModule) {
            if (this.statement.currentSelectedModule == this) {
                return true;
            } else {
                this.statement.currentSelectedModule.unSelected();
            }
        }
        this.areaNode.setStyles(this.css.areaNode_selected);
        this.statement.currentSelectedModule = this;
        this.statement.selectMode = "view";
        this.domListNode.show();
        this.isSelected = true;
        this.showProperty();
        this.statement.designer.setDesignerStatementResize();
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
    showPagingbar: function (noSetHeight) {
        this.pagingNode.show();
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
        if( !noSetHeight )this.setContentHeight();
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
        if (name == "data.pagingbarHidden") {
            if (this.json.data.pagingbarHidden) {
                this.hidePagingbar()
            } else {
                this.showPagingbar()
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
            if (styles.zebraContentTd) this.removeStyles(styles.zebraContentTd, "zebraContentTd");
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
        if (styles.zebraContentTd) this.copyStyles(styles.zebraContentTd, "zebraContentTd");
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
        if( this.isForceClearCustomStyle() ){
            this.json.data.viewStyles[to] = {};
        }else{
            if (this.json.data.viewStyles[to]) {
                Object.each(from, function (style, key) {
                    if (this.json.data.viewStyles[to][key] && this.json.data.viewStyles[to][key] == style) {
                        delete this.json.data.viewStyles[to][key];
                    }
                }.bind(this));
            }
        }

    },
    copyStyles: function (from, to) {
        if( this.isForceClearCustomStyle() ){
            this.json.data.viewStyles[to] = {};
            Object.each(from, function (style, key) {
                this.json.data.viewStyles[to][key] = style;
            }.bind(this));
        }else{
            if (!this.json.data.viewStyles[to]) this.json.data.viewStyles[to] = {};
            Object.each(from, function (style, key) {
                if (!this.json.data.viewStyles[to][key]) this.json.data.viewStyles[to][key] = style;
            }.bind(this));
        }
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
        this.view.autoAddColumnsNode.hide();
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

        this.view.statement.selectMode = "viewColumn";
        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        this._showActions();
        this.showProperty();
        this.view.statement.designer.setDesignerStatementResize();
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
    },
    _destroy: function (){
        if( !this.view.json.data.selectList  || !this.view.json.data.selectList.length ){
            this.view.autoAddColumnsNode.show();
        }
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

            var column = new MWF.xApplication.query.StatementDesigner.View.Column(json, this.view, this);
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
        // new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        try {
            this.node.scrollIntoView(false);
        }catch (e) {

        }

        this.view.statement.selectMode = "viewActionbar";
        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        //this._showActions();
        this.showProperty();
        this.view.statement.designer.setDesignerStatementResize();
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
        // new Fx.Scroll(this.view.areaNode, {"wheelStops": false, "duration": 100}).toElementEdge(this.node);
        try {
            this.node.scrollIntoView(false);
        }catch (e) {

        }

        this.view.statement.selectMode = "viewPaging";
        this.view.statement.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
        this.view.statement.designer.setDesignerStatementResize();
    },
    unSelected: function () {
        this.view.statement.currentSelectedModule = null;
        this.node.setStyles(this.css.pagingWarpNode);

        this.isSelected = false;
        this.hideProperty();
    }
});
