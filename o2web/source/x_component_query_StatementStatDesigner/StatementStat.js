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
        if (!this.json.stat || !this.json.stat.events || !this.json.stat.calculate ) {
            var url = "../x_component_query_StatementStatDesigner/$StatementStat/stat.json";
            MWF.getJSON(url, {
                "onSuccess": function (obj) {
                    if( !this.json.statementList )this.json.statementList = [];

                    if (!this.json.stat) {
                        this.json.stat = obj.stat;
                    } else {
                        this.json.stat = JSON.parse(this.json.stat);
                    }
                    if (!this.json.stat.data || o2.typeOf(this.json.stat.data) !== "object" ) this.json.stat.data = obj.stat.data;
                    if (!this.json.stat.data.events) this.json.stat.data.events = obj.stat.data.events;
                    if (!this.json.stat.data.calculate) this.json.stat.data.calculate = obj.stat.data.calculate;

                    this.statJson = this.json.stat;
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
    refreshViewFilterOption: function(){
        if( this.property && this.property.viewFilter ){
            this.property.viewFilter.setApplicableStatementOptions();
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

        this.areaNode.addEvent("click", function () {
            this.selected();
        }.bind(this))

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

        this.loadStatementStat();
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
        // if (this.stat && this.stat.domListNode) {
        //     this.stat.domListNode.hide();
        // }

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
    loadStatementStat: function(){
        this.loadStatementHtml(function () {
            this.list = new MWF.xApplication.query.StatementStatDesigner.StatementList(this, this.statementListContent, this.json.statementList);
            this.loadVerticalResize();
            // this.setRunnerSize();
            // this.designer.addEvent("resize", this.setRunnerSize.bind(this));
            this.loadStatementRunner();
            this.loadStat();
        }.bind(this));
    },
    addStatement: function(){
        var statementForm = new MWF.xApplication.query.StatementStatDesigner.StatementForm({
            app: this.designer
        }, {}, {
            title: this.designer.lp.addStatement,
            onPostOk: function (data) {

                if( data.countData )delete data.countData;
                if( data.data )delete data.data;
                if( data.scriptText )delete data.scriptText;
                if( data.countScriptText )delete data.countScriptText;
                if( data.testParameters )delete data.testParameters;
                if( data.view )delete data.view;

                if( data.mid )delete data.mid;
                if( data.pid )delete data.pid;
                if( data.vid )delete data.vid;
                if( data.vtype )delete data.vtype;

                this.json.statementList.push(data);
                this.refreshViewFilterOption();
                this.list.addItems( [data] );
            }.bind(this)
        });
        statementForm.create();
    },
    selectStatement: function(){
        debugger;
        o2.requireApp("Selector", "package", null, false);
        new MWF.O2Selector( this.designer.content, {
            type: "QueryStatement",
            // inViewCategory: this.data.application,
            exclude: this.json.statementList,
            onComplete: function (items) {
                var data = items.map(function (item) {
                    this.json.statementList.push(item.data);
                    return item.data;
                }.bind(this));
                this.list.addItems( data );
                this.refreshViewFilterOption();
            }.bind(this)
        });
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

                var size = this.statementDesignerArea.getSize(); //statementDesignerArea
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


        this.verticalResize2 = new Drag(this.resizeNode2, {
            "snap": 10,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.runArea.getSize(); //statementDesignerArea
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

                this.runAreaPercent = height/allHeight;

                this.setVerticalResize2();

            }.bind(this)
        });
    },
    setVerticalResize: function(){
        var size = this.areaNode.getSize();

        var height = size.y;

        var designAreaHeight = this.designerAreaPercent*height - 52;
        // var runAreaHeight = height-designAreaHeight;

        this.statementDesignerArea.setStyle("height", ""+designAreaHeight+"px");

        debugger;

        var editorHeight = designAreaHeight - 40;
        this.statementDetailNode.setStyle( "height", ""+editorHeight+"px" );

        var detail = this.list.getCurrentDetail();
        if( detail ){
            if( detail.editor )detail.editor.resize();
            if( detail.scriptEditor ){
                detail.scriptEditor.container.setStyle("height", ""+editorHeight+"px");
                detail.scriptEditor.resizeContentNodeSize();
            }
        }
    },
    setVerticalResize2: function(){
        var size = this.areaNode.getSize();

        var height = size.y;

        var runAreaHeight = this.runAreaPercent*height - 52;

        this.runArea.setStyle("height", ""+runAreaHeight+"px");

        debugger;

        var editorHeight = runAreaHeight - 40;
        this.runContentNode.setStyle( "height", ""+editorHeight+"px" );

        this.jsonEditor.resize();
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

    setRunnerSize: function () {
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.statementDesignerArea.getComputedSize();
        var reizeNodeSize = this.resizeNode.getComputedSize();
        var statNodeSize = this.statArea.getComputedSize();

        var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight - statNodeSize.totalHeight;
        var mTop = this.runArea.getStyle("margin-top").toInt();
        var mBottom = this.runArea.getStyle("margin-bottom").toInt();
        var pTop = this.runArea.getStyle("padding-top").toInt();
        var pBottom = this.runArea.getStyle("padding-bottom").toInt();
        y = y - mTop - mBottom - pTop - pBottom - 5;

        // var tabSize = this.tabNode.getComputedSize();
        // y = y - tabSize.totalHeight;

        this.runArea.setStyle("height", "" + y + "px");

        // var titleSize = this.runTitleNode.getComputedSize();
        // y = y - titleSize.totalHeight;

        this.runContentNode.setStyle("height", "" + y + "px");
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
        this.statementDesignerArea.addEvent("click", function (e) {
            this.selected();
            e.stopPropagation();
        }.bind(this));
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


    runStatement: function () {
        this.saveSilence(function () {
            this.execute(function (json) {
                this.executeData = json;
                o2.require("o2.widget.JsonParse", function () {
                    this.runResultNode.empty();
                    var jsonResult = new o2.widget.JsonParse(json, this.runResultNode);
                    jsonResult.load();
                }.bind(this));
                if (this.stat) {
                    var flag = true;
                    // if (this.data.type !== "select") flag = false;
                    if (this.data.format === "script" && !this.data.scriptText) flag = false;
                    if (this.data.format !== "script" && !this.data.data) flag = false;
                    if (flag) this.stat.loadStatData();
                }
                // this.setColumnDataPath(json);
            }.bind(this), function () {
            }.bind(this))
        }.bind(this));
    },
    setColumnDataPath: function (json) {
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
        if (this.stat && this.stat.items) {
            this.stat.items.each(function (column) {
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

        //var id = this.json.id;

        debugger;

        if( !this.json.statementList || this.json.statementList.length === 0 )return;

        o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.statementList[0].id, "data", 1, 50, o, function (json) {
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
        if(callback)callback();
        return;

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
        debugger;
        this.stat = new MWF.xApplication.query.StatementStatDesigner.Stat(this.designer, this, this.statJson, {});
        this.view = this.stat;
        this.stat.load(function () {
            this.stat.setContentHeight();
        }.bind(this));
    },
    setStatSize: function () {
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.statementDesignerArea.getComputedSize();
        var reizeNodeSize = this.resizeNode.getComputedSize();
        var runSize = this.runArea.getComputedSize();
        var reizeNode2Size = this.resizeNode2.getComputedSize();

        var y = size.y - designerSize.totalHeight - reizeNodeSize.totalHeight - runSize.totalHeight - reizeNode2Size.totalHeight;
        var mTop = this.statArea.getStyle("margin-top").toInt();
        var mBottom = this.statArea.getStyle("margin-bottom").toInt();
        var pTop = this.statArea.getStyle("padding-top").toInt();
        var pBottom = this.statArea.getStyle("padding-bottom").toInt();
        y = y - mTop - mBottom - pTop - pBottom - 1;

        // var tabSize = this.tabNode.getComputedSize();
        // y = y - tabSize.totalHeight;

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
    },
    reloadItem: function (ev) {
        if( this.list && this.list.currentObject && this.list.currentObject.detail)this.list.currentObject.detail.reload(ev);
    },
    deleteItem: function (ev) {
        if( this.list && this.list.currentObject && this.list.currentObject.detail)this.list.currentObject.detail.delete(ev);
    },
    editItem: function (ev) {
        if( this.list && this.list.currentObject && this.list.currentObject.detail)this.list.currentObject.detail.edit(ev);
    }
});

MWF.xApplication.query.StatementStatDesigner.StatementList = new Class({
    Implements: [Options, Events],
    options : {
        "style" :"default"
    },
    initialize: function(statementStat, node, data, options){
        this.setOptions(options);
        this.statementStat = statementStat;
        this.designer = statementStat.designer;
        this.node = $(node);
        this.data = data;
        this.itemList = [];
        this.load();
    },
    load: function(){
        if( this.data && this.data.length ){
            this.addItems( this.data );
        }
        this.fireEvent("postLoad");
    },
    hideEmptyNode: function(){
      if(this.statementStat.statementEmptyNode){
          this.statementStat.statementEmptyNode.hide();
      }
    },
    showEmptyNode: function(){
        if(this.statementStat.statementEmptyNode){
            this.statementStat.statementEmptyNode.show();
        }
    },
    addItems: function (data) {
        if( !data || data.length === 0 )return;
        this.hideEmptyNode();
        data.each(function (d, idx) {
            var item = new MWF.xApplication.query.StatementStatDesigner.StatementList.Item(this, this.node, d, {} );
            if( this.itemList.length === 0 ){
                item.setCurrent();
            }
            this.itemList.push( item );
        }.bind(this));
    },
    getCurrentDetail: function () {
        if( this.currentObject && this.currentObject.detail )return this.currentObject.detail;
    }
});

MWF.xApplication.query.StatementStatDesigner.StatementList.Item = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function ( list, container, data, options) {
        this.setOptions(options);
        this.list = list;
        this.designer = list.designer;
        this.container = $(container);
        this.data = data;
        this.load();
    },
    load: function(){
        var _self = this;

        this.node = new Element("div.o2_query_SSDStatementItemNode").inject(this.container);

        this.iconNode = new Element("i.o2_query_SSDStatementItemIconNode").inject(this.node);

        this.textNode = new Element("div.o2_query_SSDStatementItemTextNode", {
            "text" : this.data.name,
            "title" : this.data.name
        }).inject(this.node);

        this.deleteAction = new Element("i.o2_query_SSDStatementItemDeleteNode", {
            events:{
                "click": function (ev) {
                    _self.delete(ev);
                }
            }
        }).inject(this.node);

        this.editAction = new Element("i.o2_query_SSDStatementItemEditNode", {
            events:{
                "click": function (ev) {
                    _self.edit(ev);
                }
            }
        }).inject(this.node);

        this.node.addEvents({
            "mouseover": function(){
                if ( !_self.isCurrent )this.addClass( "o2_query_SSDStatementItemNode_over" );
                _self.deleteAction.fade("in");
                _self.editAction.fade("in");
            },
            "mouseout": function(){
                if ( !_self.isCurrent )this.removeClass( "o2_query_SSDStatementItemNode_over" );
                _self.deleteAction.fade("out");
                _self.editAction.fade("out");
            },
            "click": function (el) {
                _self.setCurrent();
            }
        });

        if( this.isCurrent ){
            this.setCurrent();
        }
    },
    setCurrent : function(){

        if( this.list.currentObject ){
            this.list.currentObject.cancelCurrent();
        }

        this.node.addClass( "o2_query_SSDStatementItemNode_current" );
        this.isCurrent = true;
        this.list.currentObject = this;
        this.loadDetail();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.removeClass( "o2_query_SSDStatementItemNode_current" ).removeClass( "o2_query_SSDStatementItemNode_over" );
        if( this.detail )this.detail.destroy();
    },
    getCategoryId : function(){
        return null;
    },
    delete: function (ev) {
        var _self = this;
        this.designer.confirm("wram", ev, this.designer.lp.deleteStatmentTitle, this.designer.lp.deleteStatment, 300, 120, function(){
            _self._delete(ev);
            this.close();
        }, function(){
            this.close();
        });
        ev.stopPropagation();
    },
    _delete: function( ev ){
        debugger;
        if( this.isCurrent ){
            this.cancelCurrent();
            this.list.currentObject = null;
            this.list.itemList.erase( this );
            this.list.statementStat.json.statementList.erase( this.data );
            if(this.list.itemList && this.list.itemList.length)this.list.itemList[0].setCurrent();
        }else{
            this.list.statementStat.json.statementList.erase( this.data );
            this.list.itemList.erase( this );
        }
        this.list.statementStat.refreshViewFilterOption();
        this.node.destroy();
        if( this.list.itemList.length === 0 ){
            this.list.showEmptyNode();
        }
        o2.release(this);
    },
    edit: function(ev){
        this.statementForm = new MWF.xApplication.query.StatementStatDesigner.StatementForm({
            app: this.designer
        }, this.data, {
            title: this.designer.lp.editStatement + "：" + this.data.name,
            onPostOk: function (data) {
                this.textNode.set("text", data.name);
                if( this.detail )this.detail.reload();
            }.bind(this)
        });
        this.statementForm.edit();


        // layout.openApplication(ev, "query.StatementDesigner", {
        //     "appId": "query.StatementDesigner"+this.data.query,
        //     "id": this.data.id,
        //     "application":{"name": this.data.applicationName,"id": this.data.query }
        // });
        ev.stopPropagation();
    },
    loadDetail : function( searchKey ){
        this.detail = new MWF.xApplication.query.StatementStatDesigner.StatementDetail(this, this.data);
    }
});

MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.query.StatementStatDesigner.StatementForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "design",
        "width": "90%",
        "height": "90%",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": true,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        var appNames = "query.StatementDesigner";
        var options;
        if( this.isNew ){
            options = {
                "appId": "query.StatementDesigner",
                "mode": "stat",
                "application":{"name": this.app.application.name,"id": this.app.application.id }
            };
        }else{
            options = {
                "appId": "query.StatementDesigner"+this.data.query,
                "id": this.data.id,
                "mode": "stat",
                "application":{"name": this.data.applicationName,"id": this.data.query }
            };
        }
        var par = "app=" + encodeURIComponent(appNames) +  "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
        var url = o2.filterUrl("../x_desktop/app.html?" + par + ((layout.debugger) ? "&debugger" : ""));

        this.formContentNode.empty();
        this.iframe = new Element("iframe", {
            "styles": {
                "width": "100%",
                "height": "calc( 100% - 10px )",
                "padding-bottom": "10px"
            },
            "src" : url,
            "frameborder": "0px",
            "scrolling": "auto",
            "seamless": "seamless"
        }).inject(this.formContentNode);
    },
    ok: function (e) {
        this.fireEvent("queryOk");
        this.iframe.contentWindow.layout.app.saveStatement(function (data) {
            debugger;
            if( this.formMaskNode )this.formMaskNode.destroy();
            if( this.formAreaNode )this.formAreaNode.destroy();
            if( this.app && this.app.notice)this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
            this.fireEvent("postOk", data);
            this.close();
        }.bind(this))
    },
});

MWF.xApplication.query.StatementStatDesigner.StatementDetail = new Class({
    Implements: [Options, Events],
    options : {
        "style" :"default"
    },
    initialize: function(item, sdata, options){
        this.setOptions(options);
        this.item = item;
        this.statementStat = item.list.statementStat;
        this.designer = this.statementStat.designer;
        this.node = this.statementStat.statementDetailNode;
        this.sdata = sdata;
        this.load();
    },
    load: function(){
        this.statementStat.detailEmptyNode.hide();
        this.statementStat.reloadItemNode.setStyle("display", "inline-block");
        this.statementStat.deleteItemNode.setStyle("display", "inline-block");
        this.statementStat.editItemNode.setStyle("display", "inline-block");
        this.node.show();
        var p = o2.Actions.load("x_query_assemble_surface").StatementAction.get( this.sdata.id );
        p.then(function (json) {
            this.data = json.data;
            if( this.data.format === "script" ){
                this.loadStatementScriptEditor();
            }else{
                this.loadStatementEditor();
            }
        }.bind(this));

    },
    reload: function(){
        this.destroy();
        this.load();
    },
    delete: function(ev){
        this.item.delete(ev);
    },
    edit: function(ev){
        this.item.edit(ev);
    },
    destroy: function(){
        if( this.editor ){
            this.editor.destroy();
            this.editor = null;
        }
        if( this.scriptEditor ){
            this.scriptEditor.destroy();
            this.scriptEditor = null;
        }
        this.statementStat.detailEmptyNode.show();
        this.statementStat.reloadItemNode.hide();
        this.statementStat.deleteItemNode.hide();
        this.statementStat.editItemNode.hide();
        this.node.empty();
    },
    loadStatementScriptEditor: function () {
        if (!this.scriptEditor) {
            debugger;
            var _self = this;
            o2.require("o2.widget.ScriptArea", function () {
                this.scriptEditor = new o2.widget.ScriptArea(this.node, {
                    "isbind": false,
                    "isload": true,
                    "api": "../api/server.service.module_parameters.html#server.service.module_parameters",
                    "maxObj": this.designer.designNode,
                    // "title": this.designer.lp.scriptTitle,
                    "type": "service",
                    "onPostLoadEditor": function () {
                        debugger;
                        this.setReadOnly(true);

                        this.container.setStyle("height", ""+(_self.node.getSize().y-10)+"px");
                        this.resizeContentNodeSize();
                    }
                    // "onChange": function () {
                    //     this.json.scriptText = this.scriptEditor.toJson().code;
                    // }.bind(this)
                });
                this.scriptEditor.load({"code": this.data.scriptText});
            }.bind(this), false);
        }
    },
    loadStatementEditor: function () {
        if (!this.editor) {
            o2.require("o2.widget.JavascriptEditor", function () {
                this.editor = new o2.widget.JavascriptEditor(this.node, {
                    "title": "JPQL",
                    "option": {"mode": "sql"}
                });
                this.editor.load(function () {
                    this.editor.editor.setValue(this.data.data);

                    this.editor.setReadOnly(true);

                    // this.editor.addEditorEvent("change", function () {
                    //     this.data.data = this.editor.getValue();
                    // }.bind(this));

                }.bind(this));
            }.bind(this), false);
        }

    }
});

MWF.xApplication.query.StatementStatDesigner.Stat = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.View,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true
        // "propertyPath": "../x_component_query_StatementStatDesigner/$StatementStat/stat.html"
    },

    initialize: function (designer, statementStat, data, options) {
        this.setOptions(options);

        this.path = "../x_component_query_ViewDesigner/$View/";
        // this.cssPath = "../x_component_query_ViewDesigner/$View/" + this.options.style + "/css.wcss";
        //
        // this._loadCss();

        this.statementStat = statementStat;
        this.designer = designer;
        this.css = this.statementStat.css;
        this.data = data;
        this.json = this.data;
        this.data.id = this.statementStat.data.id + "_stat";

        // this.parseData();

        this.node = this.statementStat.statArea;

        this.areaNode = new Element("div", {"styles": {"height": "calc(100% - 2px)", "overflow": "auto"}});
        this.areaNode.setStyles(this.css.areaNode);

        this.domListNode = this.designer.domListNode;

        this.items = [];
        this.stat = this;

    },
    load: function (callback) {
        debugger;
        this.setAreaNodeSize();
        this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        // this.loadTemplateStyle(function () {


            this.loadView();

            this.setEvent();

            //if (this.options.showTab) this.page.showTabIm();
            this.setViewWidth();

            this.designer.addEvent("resize", this.setViewWidth.bind(this));

            if (callback) callback();
        // }.bind(this))
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
    loadView: function(){
        this.loadViewNodes();
        //this.loadViewSelectAllNode();
        this.loadViewColumns();
//        this.addTopItemNode.addEvent("click", this.addTopItem.bind(this));
    },
    setEvent: function () {
        // this.areaNode.addEvents({
        //     "click": function (e) {
        //         this.selected();
        //         e.stopPropagation();
        //     }.bind(this),
        //     "mouseover": function () {
        //         if (!this.isSelected) this.areaNode.setStyles(this.css.areaNode_over)
        //     }.bind(this),
        //     "mouseout": function () {
        //         if (!this.isSelected) this.areaNode.setStyles(this.css.areaNode)
        //     }.bind(this)
        // });
        this.refreshNode.addEvent("click", function (e) {
            this.statementStat.runStatement();
            e.stopPropagation();
        }.bind(this));
        this.addColumnNode.addEvent("click", function (e) {
            this.addColumn();
            e.stopPropagation();
        }.bind(this));
        this.addCategoryNode.addEvent("click", function (e) {
            this.addCategory();
            e.stopPropagation();
        }.bind(this));
    },
    loadViewNodes: function(){
        this.viewAreaNode = new Element("div#viewAreaNode", {"styles": this.css.viewAreaNode}).inject(this.areaNode);
        this.viewTitleNode = new Element("div#viewTitleNode", {"styles": this.css.viewTitleNode}).inject(this.viewAreaNode);

        this.refreshNode = new Element("div", {"styles": this.css.refreshNode}).inject(this.viewTitleNode);
        this.addCategoryNode = new Element("div", {"styles": this.css.addCategoryNode, "title":this.designer.lp.addCategory}).inject(this.viewTitleNode);
        this.addColumnNode = new Element("div", {"styles": this.css.addColumnNode, "title":this.designer.lp.addColumn}).inject(this.viewTitleNode);

        this.viewTitleContentNode = new Element("div", {"styles": this.css.viewTitleContentNode}).inject(this.viewTitleNode);
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
    // selected: function () {
    //     if (this.statementStat.currentSelectedModule) {
    //         if (this.statementStat.currentSelectedModule == this) {
    //             return true;
    //         } else {
    //             this.statementStat.currentSelectedModule.unSelected();
    //         }
    //     }
    //     this.areaNode.setStyles(this.css.areaNode_selected);
    //     this.statementStat.currentSelectedModule = this;
    //     this.statementStat.selectMode = "view";
    //     this.domListNode.show();
    //     this.isSelected = true;
    //     this.showProperty();
    //     this.statementStat.designer.setDesignerStatementResize();
    // },
    // unSelected: function () {
    //     this.statementStat.currentSelectedModule = null;
    //     this.isSelected = false;
    //     this.areaNode.setStyles(this.css.areaNode);
    //     this.hideProperty();
    // },
    //
    // showProperty: function () {
    //     if (!this.property) {
    //         this.property = new MWF.xApplication.query.StatementStatDesigner.Property(this, this.designer.propertyContentArea, this.designer, {
    //             "path": this.options.propertyPath,
    //             "onPostLoad": function () {
    //                 this.property.show();
    //             }.bind(this)
    //         });
    //         this.property.load();
    //     } else {
    //         this.property.show();
    //     }
    // },
    // hideProperty: function () {
    //     if (this.property) this.property.hide();
    // },

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

            this.json.data.categoryList.each(function (entry) {
                entries[entry.column] = entry;
            }.bind(this));

            this.json.data.selectList.each(function (entry) {
                entries[entry.column] = entry;
            }.bind(this));

            if (this.statementStat.executeData && this.statementStat.executeData.data && this.statementStat.executeData.data.length) {
                this.statementStat.executeData.data.each(function (line, idx) {
                    var tr = new Element("tr", {
                        //"styles": this.json.data.viewStyles ? this.json.data.viewStyles["contentTr"] : this.css.viewContentTrNode
                        "styles": this.css.viewContentTrNode
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
                "type": "column",
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
    addCategory: function () {

        debugger;

        MWF.require("MWF.widget.UUID", function () {
            var id = (new MWF.widget.UUID).id;
            var json = {
                "id": id,
                "type": "category",
                "column": id,
                "displayName": this.designer.lp.unnamedCategory,
                "orderType": "original"
            };
            if (!this.json.data.categoryList) this.json.data.categoryList = [];
            this.json.data.categoryList.push(json);

            var next;
            if( this.json.data.selectList && this.json.data.selectList.length){
                this.items.each(function (item) {
                    if( item.json === this.json.data.selectList[0] )next = item;
                }.bind(this))
            }
            var column = new MWF.xApplication.query.StatementStatDesigner.Stat.Category(json, this, next);
            this.items.push(column);
            column.selected();

            if (this.viewContentTableNode) {
                var trs = this.viewContentTableNode.getElements("tr");
                trs.each(function (tr) {
                    new Element("td", {"styles": this.css.viewContentCategoryTdNode}).inject(tr)
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
        if (this.json.data.categoryList) {
            this.json.data.categoryList.each(function (json) {
                this.items.push(new MWF.xApplication.query.StatementStatDesigner.Stat.Category(json, this));

            }.bind(this));
        }
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
        this.type = "column";
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
    setCustomStyles : function(){
        // var viewStyles = this.view.json.data.viewStyles;
        var border = this.areaNode.getStyle("border");
        this.areaNode.clearStyles();
        this.areaNode.setStyles(this.css.viewTitleColumnAreaNode);
        this.node.setStyle("border", border);

        // if(viewStyles)Object.each(viewStyles.titleTd, function(value, key){
        //     var reg = /^border\w*/ig;
        //     if (!key.test(reg)){
        //         this.node.setStyle(key, value);
        //     }
        // }.bind(this));
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

            var index;
            this.view.items.each(function (item, i) {
                if( item.json === this.json)index = i;
            }.bind(this));
            var column = new MWF.xApplication.query.StatementStatDesigner.Stat.Column(json, this.view, this);
            this.view.items.splice(index, 0, column);
            column.selected();

            if (this.view.viewContentTableNode){
                var trs = this.view.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    var td = tr.insertCell(index);
                    td.setStyles(this.css.viewContentTdNode);
                }.bind(this));
            }
            this.view.setViewWidth();

        }.bind(this));
    },
    move: function(e){
        var columnNodes = [];
        this.view.items.each(function(item){
            if (item.type === "column" && item!=this){
                columnNodes.push(item.areaNode);
            }
        }.bind(this));

        this._createMoveNode();

        this._setNodeMove(columnNodes, e);
    },
    _setNodeMove: function(droppables, e){
        this._setMoveNodePosition(e);
        var movePosition = this.moveNode.getPosition();
        var moveSize = this.moveNode.getSize();
        var contentPosition = this.content.getPosition();
        var contentSize = this.content.getSize();

        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "limit": {
                "x": [contentPosition.x, contentPosition.x+contentSize.x],
                "y": [movePosition.y, movePosition.y+moveSize.y]
            },
            "onEnter": function(dragging, inObj){
                if (!this.moveFlagNode) this.createMoveFlagNode();
                this.moveFlagNode.inject(inObj, "before");
            }.bind(this),
            "onLeave": function(dragging, inObj){
                if (this.moveFlagNode){
                    this.moveFlagNode.dispose();
                }
            }.bind(this),
            "onDrop": function(dragging, inObj){
                if (inObj){
                    this.areaNode.inject(inObj, "before");
                    var column = inObj.retrieve("column");
                    this.listNode.inject(column.listNode, "before");
                    // var idx = this.view.json.data.selectList.indexOf(column.json);

                    this.view.json.data.selectList.erase(this.json);
                    this.view.items.erase(this);

                    var idx = this.view.json.data.selectList.indexOf(column.json);
                    this.view.json.data.selectList.splice(idx, 0, this.json);

                    var index;
                    this.view.items.each(function (item, i) {
                        if( item.json === column.json )index = i;
                    });
                    this.view.items.splice(index, 0, this);

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                    this._setActionAreaPosition();
                }else{
                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                }
            }.bind(this),
            "onCancel": function(dragging){
                if (this.moveNode) this.moveNode.destroy();
                if (this.moveFlagNode) this.moveFlagNode.destroy();
            }.bind(this)
        });
        nodeDrag.start(e);
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                tr.deleteCell(idx);
            }.bind(this));
        }

        var sortList = this.view.json.data.orderList || [];
        var deleteItem = null;
        sortList.each(function(order){
            if (order.column==this.json.column){
                deleteItem = order;
            }
        }.bind(this));
        if (deleteItem) sortList.erase(deleteItem);

        if (this.view.json.data.selectList) this.view.json.data.selectList.erase(this.json);
        this.view.items.erase(this);
        if (this.view.property) this.view.property.loadStatColumnSelect();

        this.areaNode.destroy();
        this.view.statementStat.selected();

        this.view.setViewWidth();

        MWF.release(this);
        delete this;
    }
});


MWF.xApplication.query.StatementStatDesigner.Stat.Category = new Class({
    Extends: MWF.xApplication.query.StatementStatDesigner.Stat.Column,
    initialize: function (json, stat, next) {
        this.propertyPath = "../x_component_query_StatementStatDesigner/$StatementStat/category.html";
        this.stat = stat;
        this.view = stat;
        this.json = json;
        this.next = next;
        this.css = this.stat.css;
        this.type = "category";
        this.content = this.stat.viewTitleTrNode;
        this.domListNode = this.stat.domListNode;
        this.load();
    },
    _load: function(){
        this.areaNode = new Element("td", {"styles": this.css.viewTitleColumnAreaNode});
        this.areaNode.store("column", this);

        if (this.next){
            this.areaNode.inject(this.next.areaNode, "before");
        }else{
            this.areaNode.inject(this.content);
        }

        this.node = new Element("div", {
            "styles": this.css.viewTitleCategoryNode
        }).inject(this.areaNode);
        this.textNode = new Element("div", {
            "styles": this.css.viewTitleColumnTextNode,
            "text": this.json.displayName
        }).inject(this.node);

        this.createDomListItem();


        this._createIconAction();

        //if (!this.json.export) this.hideMode();

        this.setEvent();

        this.setCustomStyles();
    },
    createDomListItem: function(){
        this.listNode = new Element("div", {"styles": this.css.cloumnListNode});
        if (this.next){
            this.listNode.inject(this.next.listNode, "before");
        }else{
            this.listNode.inject(this.domListNode);
        }
        var listIconNode = new Element("div", {"styles": this.css.cloumnListCategoryIconNode}).inject(this.listNode);
        var listTextNode = new Element("div", {"styles": this.css.cloumnListCategoryTextNode}).inject(this.listNode);
        this.resetTextNode();
    },
    setEvent: function(){
        this.node.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css.viewTitleColumnNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) if (this.isError){
                this.node.setStyles(this.css.viewTitleColumnNode_error)
            }else{
                this.node.setStyles(this.css.viewTitleCategoryNode)
            }}.bind(this)
        });
        this.listNode.addEvents({
            "click": function(e){this.selected(); e.stopPropagation();}.bind(this),
            "mouseover": function(){debugger; if (!this.isSelected) this.listNode.setStyles(this.css.cloumnListNode_over)}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.listNode.setStyles(this.css.cloumnListNode)}.bind(this)
        });
    },
    unSelected: function () {
        this.stat.statementStat.currentSelectedModule = null;
        //this.node.setStyles(this.css.viewTitleColumnNode);
        if (this.isError) {
            this.node.setStyles(this.css.viewTitleColumnNode_error)
        } else {
            this.node.setStyles(this.css.viewTitleCategoryNode)
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
                    "displayName": this.view.designer.lp.unnamedCategory,
                    "orderType": "original"
                };
            }

            var idx = this.view.json.data.categoryList.indexOf(this.json);
            this.view.json.data.categoryList.splice(idx, 0, json);

            var index;
            this.view.items.each(function (item, i) {
                if( item.json === this.json)index = i;
            }.bind(this));
            var column = new MWF.xApplication.query.StatementStatDesigner.Stat.Category(json, this.view, this);
            this.view.items.splice(index, 0, column);
            column.selected();

            if (this.view.viewContentTableNode){
                var trs = this.view.viewContentTableNode.getElements("tr");
                trs.each(function(tr){
                    var td = tr.insertCell(index);
                    td.setStyles(this.css.viewContentCategoryTdNode);
                }.bind(this));
            }
            this.view.setViewWidth();

        }.bind(this));
    },
    move: function(e){
        var columnNodes = [];
        this.view.items.each(function(item){
            if (item.type === "category" && item!=this){
                columnNodes.push(item.areaNode);
            }
        }.bind(this));

        this._createMoveNode();

        this._setNodeMove(columnNodes, e);
    },
    _setNodeMove: function(droppables, e){
        this._setMoveNodePosition(e);
        var movePosition = this.moveNode.getPosition();
        var moveSize = this.moveNode.getSize();
        var contentPosition = this.content.getPosition();
        var contentSize = this.content.getSize();

        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "limit": {
                "x": [contentPosition.x, contentPosition.x+contentSize.x],
                "y": [movePosition.y, movePosition.y+moveSize.y]
            },
            "onEnter": function(dragging, inObj){
                if (!this.moveFlagNode) this.createMoveFlagNode();
                this.moveFlagNode.inject(inObj, "before");
            }.bind(this),
            "onLeave": function(dragging, inObj){
                if (this.moveFlagNode){
                    this.moveFlagNode.dispose();
                }
            }.bind(this),
            "onDrop": function(dragging, inObj){
                if (inObj){
                    this.areaNode.inject(inObj, "before");
                    var column = inObj.retrieve("column");
                    this.listNode.inject(column.listNode, "before");
                    // var idx = this.view.json.data.selectList.indexOf(column.json);

                    this.view.json.data.categoryList.erase(this.json);
                    this.view.items.erase(this);

                    var idx = this.view.json.data.categoryList.indexOf(column.json);
                    this.view.json.data.categoryList.splice(idx, 0, this.json);

                    var index;
                    this.view.items.each(function (item, i) {
                        if( item.json === column.json )index = i;
                    });
                    this.view.items.splice(index, 0, this);

                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                    this._setActionAreaPosition();
                }else{
                    if (this.moveNode) this.moveNode.destroy();
                    if (this.moveFlagNode) this.moveFlagNode.destroy();
                }
            }.bind(this),
            "onCancel": function(dragging){
                if (this.moveNode) this.moveNode.destroy();
                if (this.moveFlagNode) this.moveFlagNode.destroy();
            }.bind(this)
        });
        nodeDrag.start(e);
    },
    "delete": function(e){
        var _self = this;
        if (!e) e = this.node;
        this.view.designer.confirm("warn", e, MWF.APPDSMSD.LP.deleteCategoryTitle, MWF.APPDSMSD.LP.deleteCategory, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    destroy: function(){
        if (this.view.currentSelectedModule==this) this.view.currentSelectedModule = null;
        if (this.actionArea) this.actionArea.destroy();
        if (this.listNode) this.listNode.destroy();
        if (this.property) this.property.propertyContent.destroy();

        var idx = this.view.items.indexOf(this);

        if (this.view.viewContentTableNode){
            var trs = this.view.viewContentTableNode.getElements("tr");
            trs.each(function(tr){
                tr.deleteCell(idx);
            }.bind(this));
        }

        var sortList = this.view.json.data.orderList || [];
        var deleteItem = null;
        sortList.each(function(order){
            if (order.column==this.json.column){
                deleteItem = order;
            }
        }.bind(this));
        if (deleteItem) sortList.erase(deleteItem);

        if (this.view.json.data.categoryList) this.view.json.data.categoryList.erase(this.json);
        this.view.items.erase(this);
        // if (this.view.property) this.view.property.loadStatColumnSelect();

        this.areaNode.destroy();
        this.view.statementStat.selected();

        this.view.setViewWidth();

        MWF.release(this);
        delete this;
    }
});

