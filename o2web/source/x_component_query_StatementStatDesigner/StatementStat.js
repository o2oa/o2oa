MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementStatDesigner = MWF.xApplication.query.StatementStatDesigner || {};
MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;

MWF.xDesktop.requireApp("query.StatementDesigner", "Statement", null, false);
MWF.xDesktop.requireApp("query.StatementStatDesigner", "lp." + MWF.language, null, false);
MWF.xDesktop.requireApp("query.StatementStatDesigner", "Property", null, false);


MWF.xApplication.query.StatementStatDesigner.StatementStat = new Class({
    Extends: MWF.xApplication.query.StatementDesigner.Statement,
    Implements: [Options, Events],

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
        if (this.view && this.view.domListNode) {
            this.view.domListNode.hide();
        }

        this.selectMode = "statementStat";
        this.currentSelectedModule = this;
        this.isSelected = true;
        this.showProperty();
        this.designer.setDesignerStatementResize();
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

            // this.jpqlTabPageNode = this.areaNode.getElement(".o2_statement_statementJpqlTabPageNode");

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

            // this.jpqlTabNode = this.areaNode.getElement(".o2_statement_statementJpqlTabNode");

            // this.jpqlTypeSelect = this.areaNode.getElement(".o2_statement_statementDesignerTypeContent").getElement("select");
            // this.loadJpqlTypeSelect();

            this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");


            // this.countJpqlTabPageNode = this.areaNode.getElement(".o2_statement_statementCountJpqlTabPageNode");
            // this.countJpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerCountJpql");
            // this.countScriptArea = this.areaNode.getElement(".o2_statement_statementDesignerCountScript");
            // this.countJpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerCountJpqlLine");


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
                // this.loadStatementCountScriptEditor();
            } else {
                this.loadStatementEditor();
                // this.loadStatementCountEditor();
            }
            this.loadStatementRunner();

            this.viewArea = this.areaNode.getElement(".o2_statement_viewNode");
            this.loadView();

            this.loadTab();

            this.setEvent();
            this.loadVerticalResize();
        }.bind(this));
    },
    // loadJpqlTypeSelect : function(){
    //   this.jpqlTypeSelect.empty();
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
    //         }).inject(this.jpqlTypeSelect);
    //         if( this.json.type === field.value ){
    //             flag = false;
    //             option.selected = true;
    //         }
    //     }.bind(this));
    //     if( flag ){
    //         this.jpqlTypeSelect.options[0].selected = true;
    //         this.json.type = this.jpqlTypeSelect.options[0].value;
    //         this.jpqlTypeSelect.fireEvent("change");
    //     }
    // },
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
    // loadStatementCountScriptEditor: function () {
    //     if (!this.countScriptEditor) {
    //         debugger;
    //         o2.require("o2.widget.ScriptArea", function () {
    //             this.countScriptEditor = new o2.widget.ScriptArea(this.countScriptArea, {
    //                 "isbind": false,
    //                 "maxObj": this.designer.designNode,
    //                 "title": this.designer.lp.scriptTitle,
    //                 "onChange": function () {
    //                     this.json.countScriptText = this.countScriptEditor.toJson().code;
    //                 }.bind(this)
    //             });
    //             this.countScriptEditor.load({"code": this.json.countScriptText})
    //         }.bind(this), false);
    //     }
    // },
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

                    // this.editor.editor.on("change", function(){
                    //     this.data.data = this.editor.getValue();
                    //     this.checkJpqlType();
                    // }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }

    },
    // loadStatementCountEditor: function () {
    //     if (!this.countEditor) {
    //         if( !this.json.countData )this.json.countData = "SELECT count(o.id) FROM table o";
    //         if( this.countJpqlEditorNode.offsetParent === null && o2.editorData.javascriptEditor.editor === "monaco" ){
    //             var postShowFun = function() {
    //                 this._loadStatementCountEditor();
    //                 this.countJpqlPage.removeEvent("postShow", postShowFun);
    //             }.bind(this);
    //             this.countJpqlPage.addEvent("postShow", postShowFun);
    //         }else{
    //             this._loadStatementCountEditor();
    //         }
    //     }
    // },
    // _loadStatementCountEditor : function(){
    //     o2.require("o2.widget.JavascriptEditor", function () {
    //         this.countEditor = new o2.widget.JavascriptEditor(this.countJpqlEditorNode, {
    //             "title": "JPQL",
    //             "option": {"mode": "sql"}
    //         });
    //         this.countEditor.load(function () {
    //
    //                 this.countEditor.editor.setValue(this.json.countData);
    //
    //             this.countEditor.addEditorEvent("change", function () {
    //                 this.data.countData = this.countEditor.getValue();
    //             }.bind(this));
    //         }.bind(this));
    //     }.bind(this), false);
    // },
    setSatementTable: function () {
        if (!this.json.type) this.json.type = "select";
        // this.changeType(this.json.type, true);
        if( this.editor && this.editor.editor){
            if (this.json.data) {
                this.editor.editor.setValue(this.json.data);
            } else {
                var table = (this.json.tableObj) ? this.json.tableObj.name : "table";
                // switch (this.json.type) {
                //     case "update":
                //         this.editor.editor.setValue("UPDATE " + table + " o SET ");
                //         break;
                //     case "delete":
                //         this.editor.editor.setValue("DELETE " + table + " o WHERE ");
                //         break;
                //     default:
                        this.editor.editor.setValue("SELECT o FROM " + table + " o");
                // }
            }
        }
    },

    // checkJpqlType: function () {
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
    //     if (type != this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value || force) {
    //         for (var i = 0; i < this.jpqlTypeSelect.options.length; i++) {
    //             if (this.jpqlTypeSelect.options[i].value == type) {
    //                 this.jpqlTypeSelect.options[i].set("selected", true);
    //                 break;
    //             }
    //         }
    //     }
    // },
    loadStatementHtml: function (callback) {
        this.areaNode.loadAll({
            "css": this.path + this.options.style + "/statement.css",
            "html": "../x_component_query_StatementStatDesigner/$StatementStat/statementDesigner.html"
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

                    //this.countScriptArea.show();
                    // this.countJpqlArea.hide();
                    // this.loadStatementCountScriptEditor();
                } else {
                    this.scriptArea.hide();
                    this.jpqlArea.show();
                    this.loadStatementEditor();

                    //this.countScriptArea.hide();
                    // this.countJpqlArea.show();
                    // this.loadStatementCountEditor();
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
            // this.loadJpqlTypeSelect();
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
        // this.jpqlTypeSelect.addEvent("change", function () {
        //     var t = this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value;
        //     if (t != this.json.type) {
        //         this.json.type = t;
        //     }
        //     if (t != "select") {
        //         this.jpqlPage.showTabIm();
        //         // this.countJpqlPage.disableTab();
        //
        //         this.runPage.showTabIm();
        //         this.viewPage.disableTab();
        //     } else {
        //         // this.countJpqlPage.enableTab(true);
        //         this.viewPage.enableTab(true);
        //     }
        // }.bind(this));

        this.fieldSelect.addEvent("change", function (ev) {
            var option = ev.target.options[ev.target.selectedIndex];
            var type = option.retrieve("type");
            var field = option.retrieve("field");
            if( !field )return;
            var text = field.name;
            // if( this.countJpqlPage && this.countJpqlPage.isShow && !this.countJpqlPage.disabled ){
            //     if( this.data.format === "script" && this.countScriptEditor.jsEditor ){
            //         this.countScriptEditor.jsEditor.insertValue( text );
            //     }else if(this.countEditor){
            //         this.countEditor.insertValue( text );
            //     }
            // }else{
                if( this.data.format === "script" && this.scriptEditor.jsEditor ){
                    this.scriptEditor.jsEditor.insertValue( text );
                }else if( this.editor ){
                    this.editor.insertValue( text );
                }
            // }
        }.bind(this))
    },
    changeEditorEntityClassName : function( entityClassName ){
        if (this.json.format == "jpql") {
            if (this.editor) {
                var re = /(.*from\s*)/ig;
                // if (this.json.type == "update") re = /(.*update\s*)/ig;

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

            // if( this.countEditor ){
            //     var re = /(.*from\s*)/ig;
            //     var v = this.json.countData;
            //
            //     var re2 = /(\s+)/ig;
            //     var arr = re.exec(v);
            //     if (arr && arr[0]) {
            //         var left = arr[0]
            //         v = v.substring(left.length, v.length);
            //         //var ar = re2.exec(v);
            //         var right = v.substring(v.indexOf(" "), v.length);
            //         this.json.countData = left + entityClassName + right;
            //         this.countEditor.editor.setValue(this.json.countData);
            //     }
            // }

        }
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
                    // if (this.data.type !== "select") flag = false;
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
        // if (this.data.type !== "select") return;
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
        // if (this.data.type === "select") {
        //     if (this.data.format === "script") {
        //         if (this.data.scriptText && this.data.countScriptText) {
        //             mode = "all"
        //         } else if (this.data.scriptText && !this.data.countScriptText) {
        //             mode = "data"
        //         } else if (!this.data.scriptText && this.data.countScriptText) {
        //             mode = "count"
        //         } else {
        //             this.designer.notice(this.designer.lp.inputStatementData, "error");
        //             return false;
        //         }
        //     } else {
        //         if (this.data.data && this.data.countData) {
        //             mode = "all"
        //         } else if (this.data.data && !this.data.countData) {
        //             mode = "data"
        //         } else if (!this.data.data && this.data.countData) {
        //             mode = "count"
        //         } else {
        //             this.designer.notice(this.designer.lp.inputStatementData, "error");
        //             return false;
        //         }
        //     }
        // }
        o2.Actions.load("x_query_assemble_designer").StatementAction.executeV2(this.json.id, "data", 1, 50, o, function (json) {
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

