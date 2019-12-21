MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;

MWF.xDesktop.requireApp("query.StatementDesigner", "lp."+MWF.language, null, false);
//MWF.xDesktop.requireApp("query.StatementDesigner", "Property", null, false);
MWF.xDesktop.requireApp("query.TableDesigner", "Property", null, false);

MWF.xApplication.query.StatementDesigner.Statement = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isView": false,
        "showTab": true,
        "propertyPath": "/x_component_query_StatementDesigner/$Statement/statement.html"
    },

    initialize: function(designer, data, options){
        this.setOptions(options);

        this.path = "/x_component_query_StatementDesigner/$Statement/";
        this.cssPath = "/x_component_query_StatementDesigner/$Statement/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.designer = designer;
        this.data = data;
        this.parseData();

        this.node = this.designer.designNode;
        this.areaNode = new Element("div", {"styles": {"height": "100%", "overflow": "auto"}});

        //this.statementRunNode = this.designer.designerStatementArea;

        if(this.designer.application) this.data.applicationName = this.designer.application.name;
        if(this.designer.application) this.data.application = this.designer.application.id;

        this.isNewStatement = (this.data.id) ? false : true;

        this.view = this;

        this.autoSave();
        this.designer.addEvent("queryClose", function(){
            if (this.autoSaveTimerID) window.clearInterval(this.autoSaveTimerID);
        }.bind(this));
    },
    parseData: function(){
        this.json = this.data;
        if (!this.json.type) this.json.type = "select";
        if (!this.json.format) this.json.format = "jpql";
        if (!this.json.entityCategory) this.json.entityCategory = "official";
        if (!this.json.entityClassName) this.json.entityClassName = "com.x.processplatform.core.entity.content.Task";
    },
    autoSave: function(){
        this.autoSaveTimerID = window.setInterval(function(){
            if (!this.autoSaveCheckNode) this.autoSaveCheckNode = this.designer.contentToolbarNode.getElement("#MWFAutoSaveCheck");
            if (this.autoSaveCheckNode){
                if (this.autoSaveCheckNode.get("checked")){
                    this.save();
                }
            }
        }.bind(this), 60000);
    },

    load : function(){


        // this.setAreaNodeSize();
        // this.designer.addEvent("resize", this.setAreaNodeSize.bind(this));
        this.areaNode.inject(this.node);

        this.designer.statementListAreaNode.getChildren().each(function(node){
            var statement = node.retrieve("statement");
            if (statement.id==this.data.id){
                if (this.designer.currentListStatementItem){
                    this.designer.currentListStatementItem.setStyles(this.designer.css.listStatementItem);
                }
                node.setStyles(this.designer.css.listStatementItem_current);
                this.designer.currentListStatementItem = node;
                this.lisNode = node;
            }
        }.bind(this));

        this.loadStatement();
        this.showProperty();
    },
    showProperty: function(){
        if (!this.property){
            this.property = new MWF.xApplication.query.TableDesigner.Property(this, this.designer.designerContentArea, this.designer, {
                "path": this.options.propertyPath,
                "onPostLoad": function(){
                    this.property.show();
                }.bind(this)
            });
            this.property.load();
        }else{
            this.property.show();
        }
    },
    loadStatement: function(){
        //this.statementDesignerNode = new Element("div", {"styles": this.css.statementDesignerNode}).inject(this.areaNode);
        this.loadStatementHtml(function(){
            this.designerArea = this.areaNode.getElement(".o2_statement_statementDesignerNode");
            this.jpqlArea = this.areaNode.getElement(".o2_statement_statementDesignerJpql");
            this.scriptArea = this.areaNode.getElement(".o2_statement_statementDesignerScript");

            this.formatTypeArea = this.areaNode.getElement(".o2_statement_statementDesignerFormatContent");
            this.entityCategorySelect = this.areaNode.getElement(".o2_statement_statementDesignerCategoryContent").getElement("select");

            this.dynamicTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_dynamic");
            this.officialTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_official");
            this.customTableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableArea_custom");

            this.dynamicTableSelect = this.areaNode.getElement(".o2_statement_statementDesignerSelectTable");
            this.officialTableSelect = this.officialTableArea.getElement("select");

            this.dynamicTableContent = this.areaNode.getElement(".o2_statement_statementDesignerTableContent");


            this.jpqlTypeSelect = this.areaNode.getElement(".o2_statement_statementDesignerTypeContent").getElement("select");



            // this.jpqlSelectEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_select");
            // this.jpqlUpdateEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_update");
            // this.jpqlDeleteEditor = this.areaNode.getElement(".o2_statement_statementDesignerJpql_sdelete");

            // this.jpqlSelectEditor_selectContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_selectContent");
            // this.jpqlSelectEditor_fromContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_fromContent");
            // this.jpqlSelectEditor_whereContent= this.jpqlSelectEditor.getElement(".o2_statement_statementDesignerJpql_jpql_whereContent");

            this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");


            this.runArea = this.areaNode.getElement(".o2_statement_statementRunNode");
            this.runTitleNode = this.areaNode.getElement(".o2_statement_statementRunTitleNode");
            this.runContentNode = this.areaNode.getElement(".o2_statement_statementRunContentNode");
            this.runJsonNode = this.runContentNode.getFirst();
            this.runActionNode = this.runJsonNode.getNext();
            this.runResultNode = this.runContentNode.getLast();

            this.setRunnerSize();
            this.designer.addEvent("resize", this.setRunnerSize.bind(this));

            if (this.json.format=="script"){
                this.loadStatementScriptEditor();
            }else{
                this.loadStatementEditor();
            }

            this.loadStatementRunner();
            this.setEvent();
        }.bind(this));
    },
    loadStatementScriptEditor: function(){
        if (! this.scriptEditor){
            o2.require("o2.widget.ScriptArea", function(){
                this.scriptEditor = new o2.widget.ScriptArea(this.scriptArea, {
                    "isbind": false,
                    "maxObj": this.designer.content,
                    "title": this.designer.lp.scriptTitle,
                    "onChange": function(){
                        this.json.scriptText = this.scriptEditor.toJson().code;
                    }.bind(this)
                });
                this.scriptEditor.load({"code": this.json.scriptText})
            }.bind(this), false);
        }
    },
    setRunnerSize: function(){
        debugger;
        var size = this.areaNode.getSize();
        var designerSize = this.designerArea.getComputedSize();
        var y = size.y-designerSize.totalHeight;
        var mTop = this.runArea.getStyle("margin-top").toInt();
        var mBottom = this.runArea.getStyle("margin-bottom").toInt();
        var pTop = this.runArea.getStyle("padding-top").toInt();
        var pBottom = this.runArea.getStyle("padding-bottom").toInt();
        y = y-mTop-mBottom-pTop-pBottom-1;

        this.runArea.setStyle("height", ""+y+"px");

        var titleSize = this.runTitleNode.getComputedSize();
        y = y - titleSize.totalHeight;
        this.runContentNode.setStyle("height", ""+y+"px");
    },
    loadStatementEditor: function(){
        if (!this.editor){
            o2.require("o2.widget.JavascriptEditor", function(){
                this.editor = new o2.widget.JavascriptEditor(this.jpqlEditorNode, {"title": "JPQL", "option": {"mode": "sql"}});
                this.editor.load(function(){
                    if (this.json.data){
                        this.editor.editor.setValue(this.json.data);
                    }else{
                        var table = "table";
                        switch (this.json.type) {
                            case "update":
                                this.editor.editor.setValue("UPDATE "+table+" o SET ");
                                break;
                            case "delete":
                                this.editor.editor.setValue("DELETE "+table+" o WHERE ");
                                break;
                            default:
                                this.editor.editor.setValue("SELECT * FROM "+table+" o");
                        }
                    }
                    this.json.data = this.editor.editor.getValue();

                    this.editor.editor.on("change", function(){
                        this.data.data = this.editor.editor.getValue();
                        this.checkJpqlType();
                    }.bind(this));
                }.bind(this));
            }.bind(this), false);
        }

    },
    setSatementTable: function(){
        if (!this.json.type) this.json.type = "select";
        this.changeType(this.json.type, true);
        if (this.json.data){
            this.editor.editor.setValue(this.json.data);
        }else{
            var table = (this.json.tableObj) ? this.json.tableObj.name : "table";
            switch (this.json.type) {
                case "update":
                    this.editor.editor.setValue("UPDATE "+table+" o SET ");
                    break;
                case "delete":
                    this.editor.editor.setValue("DELETE "+table+" o WHERE ");
                    break;
                default:
                    this.editor.editor.setValue("SELECT * FROM "+table+" o");
            }
        }
    },

    checkJpqlType: function(){
        var str = this.json.data;
        this.json.data = str;
        var jpql_select = /^select/i;
        var jpql_update = /^update/i;
        var jpql_delete = /^delete/i;
        if (jpql_select.test(str)) return this.changeType("select");
        if (jpql_update.test(str)) return this.changeType("update");
        if (jpql_delete.test(str)) return this.changeType("delete");
    },
    changeType: function(type, force){
        if (this.json.type!=type) this.json.type=type;
        if (type != this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value || force){
            for (var i=0; i<this.jpqlTypeSelect.options.length; i++){
                if (this.jpqlTypeSelect.options[i].value==type){
                    this.jpqlTypeSelect.options[i].set("selected", true);
                    break;
                }
            }
        }
    },
    loadStatementHtml: function(callback){
        this.areaNode.loadAll({
            "css": this.path+this.options.style+"/statement.css",
            "html": this.path+"statementDesigner.html"
        }, {
            "bind": {"lp": this.designer.lp, "data": this.data}
        },function(){
            if (callback) callback();
        }.bind(this));
    },
    loadStatementRunner: function(){
        o2.require("o2.widget.JavascriptEditor", function(){
            this.jsonEditor = new o2.widget.JavascriptEditor(this.runJsonNode, {"title": "JPQL", "option": {"mode": "json"}});
            this.jsonEditor.load(function(){
                this.jsonEditor.editor.setValue("{}");
            }.bind(this));
        }.bind(this), false);
    },
    setEvent: function(){
        this.formatTypeArea.getElements("input").addEvent("click", function(e){
            if (e.target.checked){
                var v = e.target.get("value");
                if (v==="script"){
                    this.scriptArea.show();
                    this.jpqlArea.hide();
                    this.loadStatementScriptEditor();
                }else{
                    this.scriptArea.hide();
                    this.jpqlArea.show();
                    this.loadStatementEditor();
                }
                this.json.format = v;
            }
        }.bind(this));
        this.entityCategorySelect.addEvent("change", function(e){
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
            this.json.entityCategory = entityCategory
        }.bind(this));
        //@todo change table
        this.officialTableSelect.addEvent("change", function(e){
            debugger;
            var entityClassName = e.target.options[e.target.selectedIndex].value;
            this.json.entityClassName = entityClassName;
            if (this.json.format=="jpql"){
                if (this.editor){
                    var re = /(.*from\s*)/ig;
                    if (this.json.type=="update") re = /(.*update\s*)/ig;

                    //if (this.json.type=="select" && this.editor){
                        var v = this.json.data;

                        var re2 = /(\s+)/ig;
                        var arr = re.exec(v);
                        if (arr && arr[0]){
                            var left = arr[0]
                            v = v.substring(left.length, v.length);
                            //var ar = re2.exec(v);
                            var right = v.substring(v.indexOf(" "),v.length);
                            this.json.data = left+entityClassName+right;
                            this.editor.editor.setValue(this.json.data);
                        }
                    //}
                }

            }


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
        this.jpqlTypeSelect.addEvent("change", function(){
            var t = this.jpqlTypeSelect.options[this.jpqlTypeSelect.selectedIndex].value;
            if (t!=this.json.type) this.json.type=t;
        }.bind(this));
    },

    selectTable: function(){
        new MWF.O2Selector(this.designer.content, {
            "type": "queryTable",
            "count": 1,
            "values": (this.json.table) ? [this.json.table] : [],
            "title": this.designer.lp.selectTable,
            "onComplete": function(items){
                if (items.length){
                    var id = items[0].data.id;
                    var name = items[0].data.name;
                    this.dynamicTableContent.set("text", name);
                    this.json.table = name;
                    this.json.tableObj = items[0].data;
                }else{
                    this.dynamicTableContent.set("text", "");
                    this.json.table = "";
                }
            }.bind(this)
        });
    },

    runStatement:function(){
        debugger;
        // if (!this.json.data){
        //     this.designer.notice(this.designer.lp.inputStatementData, "error");
        //     return false;
        // }
        o2.require("o2.widget.Mask", null, false);
        this.runMask = new o2.widget.Mask();
        this.runMask.loadNode(this.node);

        this.saveSilence(function(){
            var json = this.jsonEditor.editor.getValue();
            var o = JSON.parse(json);
            o2.Actions.get("x_query_assemble_designer").executeStatement(this.json.id, 1, 50 , o, function(json){
                o2.require("o2.widget.JsonParse", function(){
                    this.runResultNode.empty();
                    var jsonResult = new o2.widget.JsonParse(json.data, this.runResultNode);
                    jsonResult.load();
                }.bind(this));
                this.runMask.hide();
            }.bind(this), function(xhr, text, error){
                debugger;
                if (this.runMask) this.runMask.hide();
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                errorText = errorText.replace(/\</g, "&lt;");
                errorText = errorText.replace(/\</g, "&gt;");
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }.bind(this))
        }.bind(this));
    },

    save: function(callback){
        debugger;
        if (!this.data.name){
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }
        //if( !this.data.tableType ){
        //    this.data.tableType = "dynamic";
        //}
        if (this.editor) this.data.data = this.editor.editor.getValue();
        if (this.scriptEditor) this.data.scriptText = this.scriptEditor.toJson().code;

        this.designer.actions.saveStatement(this.data, function(json){
            this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    },
    _setEditStyle: function(){},

    saveSilence: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }
        if (this.editor) this.data.data = this.editor.editor.getValue();
        if (this.scriptEditor) this.data.scriptText = this.scriptEditor.toJson().code;

        this.designer.actions.saveStatement(this.data, function(json){
            //this.designer.notice(this.designer.lp.save_success, "success", this.node, {"x": "left", "y": "bottom"});

            this.data.id = json.data.id;
            if (this.lisNode) {
                this.lisNode.getLast().set("text", this.data.name+"("+this.data.alias+")");
            }
            if (callback) callback();
        }.bind(this));
    }
});