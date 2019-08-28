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
            this.jpqlTypeSelect = this.areaNode.getElement("select");
            this.tableSelect = this.areaNode.getElement(".o2_statement_statementDesignerSelectTable");
            this.tableArea = this.areaNode.getElement(".o2_statement_statementDesignerTableContent");
            this.jpqlEditorNode = this.areaNode.getElement(".o2_statement_statementDesignerJpqlLine");
            this.runArea = this.areaNode.getElement(".o2_statement_statementRunNode");
            this.runTitleNode = this.areaNode.getElement(".o2_statement_statementRunTitleNode");
            this.runContentNode = this.areaNode.getElement(".o2_statement_statementRunContentNode");
            this.runJsonNode = this.runContentNode.getFirst();
            this.runActionNode = this.runJsonNode.getNext();
            this.runResultNode = this.runContentNode.getLast();

            this.setRunnerSize();
            this.designer.addEvent("resize", this.setRunnerSize.bind(this));
            this.loadStatementEditor();
            this.loadStatementRunner();
            this.setEvent();
        }.bind(this));
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
        o2.require("o2.widget.JavascriptEditor", function(){
            this.editor = new o2.widget.JavascriptEditor(this.jpqlEditorNode, {"title": "JPQL", "option": {"mode": "sql"}});
            this.editor.load(function(){
                if (this.json.table){
                    o2.Actions.get("x_query_assemble_designer").getTable(this.json.table, function(json){
                        this.json.tableObj = json.data;
                        this.tableArea.set("text", json.data.name);
                        this.setSatementTable();
                    }.bind(this))
                }else{
                    this.setSatementTable();
                }

                this.editor.editor.on("change", function(){
                    this.checkJpqlType();
                }.bind(this));
            }.bind(this));
        }.bind(this), false);
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
                    this.editor.editor.setValue("update "+table+" o set ");
                    break;
                case "delete":
                    this.editor.editor.setValue("select "+table+" o where ");
                    break;
                default:
                    this.editor.editor.setValue("select o from "+table+" o");
            }
        }
    },

    checkJpqlType: function(){
        var str = this.editor.editor.getValue();
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
            "bind": {"lp": this.designer.lp}
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
        this.runActionNode.getFirst().addEvent("click", this.runStatement.bind(this));
        this.tableSelect.addEvent("click", this.selectTable.bind(this));
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
                    this.tableArea.set("text", name);
                    this.json.table = name;
                    this.json.tableObj = items[0].data;
                }else{
                    this.tableArea.set("text", "");
                    this.json.table = "";
                }
            }.bind(this)
        });
    },

    runStatement:function(){
        debugger;
        if (!this.json.data){
            this.designer.notice(this.designer.lp.inputStatementData, "error");
            return false;
        }
        o2.require("o2.widget.Mask", null, false);
        this.runMask = new o2.widget.Mask();
        this.runMask.loadNode(this.node);

        this.saveSilence(function(){
            var json = this.jsonEditor.editor.getValue();
            var o = JSON.parse(json);
            o2.Actions.get("x_query_assemble_designer").executeStatement(this.json.id, 1, 10 , o, function(json){
                o2.require("o2.widget.JsonParse", function(){
                    this.runResultNode.empty();
                    var jsonResult = new o2.widget.JsonParse(json.data, this.runResultNode);
                    jsonResult.load();
                }.bind(this));
                this.runMask.hide();
            }.bind(this) )
        }.bind(this));
    },

    save: function(callback){
        if (!this.data.name){
            this.designer.notice(this.designer.lp.inputStatementName, "error");
            return false;
        }
        if( !this.data.tableType ){
            this.data.tableType = "dynamic";
        }

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