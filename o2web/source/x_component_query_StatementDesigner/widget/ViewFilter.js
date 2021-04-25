MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
if(!MWF.APPDSMD)MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;
MWF.xApplication.query.StatementDesigner.widget = MWF.xApplication.query.StatementDesigner.widget || {};
if( !MWF.APPDSMD.LP ){
    MWF.xDesktop.requireApp("query.StatementDesigner", "lp." + MWF.language, null, false);
}

MWF.xApplication.query.StatementDesigner.widget.ViewFilter = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "type": "identity",
        "withForm" : false,
        "names": []
    },
    initialize: function (node, app, filtrData, options) {
        this.setOptions(options);
        this.node = $(node);
        this.app = app;
        this.filtrData = filtrData;

        this.path = "../x_component_query_StatementDesigner/widget/$ViewFilter/";
        this.cssPath = "../x_component_query_StatementDesigner/widget/$ViewFilter/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.items = [];
        this.load();

    },
    load: function (data) {
        var _load = function () {
            this.getInputNodes();
            this.createActionNode();
            //this.createAddNode();
            //this.loadIdentitys();
            this.loadData();
        }.bind(this)
        if( this.app.statement && this.app.statement.data ){
            this.statementData = this.app.statement.data;
            _load();
        }else if( this.options.statementId ){
            o2.Actions.load("x_query_assemble_designer").StatementAction.get( this.options.statementId, function (json) {
                this.statementData = json.data;
                _load();
            }.bind(this))
        }else{
            _load();
        }
    },
    loadData: function () {
        if (this.filtrData.parameterData && this.filtrData.parameterData.length && this.parameterListAreaNode) {
            this.filtrData.parameterData.each(function (data) {
                data.type = "parameter";
                this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemParameter(this, data));
            }.bind(this));
        }

        if (this.filtrData.filtrData && this.filtrData.filtrData.length && this.filterListAreaNode ) {
            this.filtrData.filtrData.each(function (data) {
                data.type = "filter";
                this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemFilter(this, data));
            }.bind(this));
        }

        if (this.filtrData.customData && this.filtrData.customData.length && this.customFilterListAreaNode ) {
            this.filtrData.customData.each(function (data) {
                data.type = "custom";
                this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemCustom(this, data));
            }.bind(this));
        }
    },
    createScriptArea: function (node) {
        this.scriptValueArea = node;
        var title = node.get("title");

        MWF.require("MWF.widget.ScriptArea", function () {
            this.scriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "maxObj": this.app.formContentNode || this.app.pageContentNode,
                "onChange": function () {
                    this.scriptData = this.scriptArea.toJson();
                }.bind(this),
                "onSave": function () {
                    //this.app.saveForm();
                }.bind(this),
                "style": "formula"
            });
            var v = (this.scriptData) ? this.scriptData.code : "";
            this.scriptArea.load(v);
        }.bind(this));
    },
    createParameterValueScriptArea: function (node) {
        var title = node.get("title");

        MWF.require("MWF.widget.ScriptArea", function () {
            this.parameterValueScriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "isload": true,
                "isbind": false,
                "maxObj": this.app.formContentNode || this.app.pageContentNode,
                "onChange": function () {
                    this.parameterValueScriptData = this.parameterValueScriptArea.toJson();
                }.bind(this),
                "onSave": function () {
                    //this.app.saveForm();
                }.bind(this),
                "style": "formula"
            });
            var v = (this.parameterValueScriptData) ? this.parameterValueScriptData.code : "";
            this.parameterValueScriptArea.load(v);
        }.bind(this));
    },
    createCustomFilterValueScriptArea: function (node) {
        var title = node.get("title");

        MWF.require("MWF.widget.ScriptArea", function () {
            this.customFilterValueScriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "isload": true,
                "isbind": false,
                "maxObj": this.app.formContentNode || this.app.pageContentNode,
                "onChange": function () {
                    this.customFilterValueScriptData = this.customFilterValueScriptArea.toJson();
                }.bind(this),
                "onSave": function () {
                    //this.app.saveForm();
                }.bind(this),
                "style": "formula"
            });
            var v = (this.customFilterValueScriptData) ? this.customFilterValueScriptData.code : "";
            this.customFilterValueScriptArea.load(v);
        }.bind(this));
    },
    getInputNodes: function () {
        debugger;
        this.inputAreaNode = this.node.getElement(".inputAreaNode_vf");
        this.actionAreaNode = this.node.getElement(".actionAreaNode_vf");
        this.actionAreaNode.setStyles(this.css.actionAreaNode);

        this.filterListAreaNode = this.node.getElement(".filterListAreaNode_vf");
        this.parameterListAreaNode = this.node.getElement(".parameterListAreaNode_vf");
        this.customFilterListAreaNode = this.node.getElement(".customFilterListAreaNode_vf");

        this.restrictViewFilterTable = this.node.getElement(".restrictViewFilterTable_vf");

        var scriptValueArea = this.node.getElement(".MWFFilterFormulaArea");
        if (scriptValueArea) {
            this.createScriptArea(scriptValueArea);
        }

        this.titleInput = this.inputAreaNode.getElement(".titleInput_vf");
        this.pathInput = this.inputAreaNode.getElement(".pathInput_vf");
        this.pathInputSelect = this.inputAreaNode.getElement(".pathInputSelect_vf");
        this.parameterInput = this.inputAreaNode.getElement(".parameterInput_vf");
        // this.parameterInputSelect = this.inputAreaNode.getElement(".parameterInputSelect_vf");
        this.datatypeInput = this.inputAreaNode.getElement(".datatypeInput_vf");

        this.restrictParameterInput = this.inputAreaNode.getElement(".restrictParameterInput_vf");
        this.restrictFilterInput = this.inputAreaNode.getElement(".restrictFilterInput_vf");
        this.customFilterInput = this.inputAreaNode.getElement(".customFilterInput_vf");

        // this.logicInput = this.inputAreaNode.getElement(".logicInput_vf");

        this.comparisonInput = this.inputAreaNode.getElement(".comparisonInput_vf");
        // this.comparisonInput.addEvent("change", function(){
        //     this.switchInputDisplay();
        // }.bind(this))

        this.valueTextInput = this.inputAreaNode.getElement(".valueTextInput_vf");
        this.valueNumberInput = this.inputAreaNode.getElement(".valueNumberInput_vf");
        this.valueDatetimeInput = this.inputAreaNode.getElement(".valueDatetimeInput_vf");
        this.valueBooleanInput = this.inputAreaNode.getElement(".valueBooleanInput_vf");
        this.valueDateInput = this.inputAreaNode.getElement(".valueDateInput_vf");
        this.valueTimeInput = this.inputAreaNode.getElement(".valueTimeInput_vf");

        if (this.app.statement && this.app.statement.view) {
            var dataId = this.app.statement.view.data.id;

            this.parameterValueType = this.inputAreaNode.getElements("[name='" + dataId + "viewParameterValueType']");
            this.parameterValueScriptDiv = this.inputAreaNode.getElement("#" + dataId + "viewParameterValueScriptDiv");
            this.parameterValueScript = this.inputAreaNode.getElement("[name='" + dataId + "viewParameterValueScript']");
            if (this.parameterValueScript) {
                this.createParameterValueScriptArea(this.parameterValueScript);
            }

            this.customFilterValueTypes = this.inputAreaNode.getElements("[name='" + dataId + "viewCustomFilterValueType']");
            this.customFilterValueScriptDiv = this.inputAreaNode.getElement("#" + dataId + "viewCustomFilterValueScriptDiv");
            this.customFilterValueScript = this.inputAreaNode.getElement("[name='" + dataId + "viewCustomFilterValueScript']");
            if (this.customFilterValueScript) {
                this.createCustomFilterValueScriptArea(this.customFilterValueScript);
            }
        }

        MWF.require("MWF.widget.Calendar", function () {
            this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput, {
                "style": "xform",
                "isTime": true,
                "secondEnable": true,
                "target": this.app.content,
                "format": "db",
                "onComplate": function () {
                    this.node.getElement("#" + id + "viewParameterDateFormulaSelector").getElements("input").set("checked", false);
                }.bind(this)
            });
            new MWF.widget.Calendar(this.valueDateInput, {
                "style": "xform",
                "isTime": false,
                "target": this.app.content,
                "format": "%Y-%m-%d"
            });
            new MWF.widget.Calendar(this.valueTimeInput, {
                "style": "xform",
                "timeOnly": true,
                "secondEnable": true,
                "target": this.app.content,
                "format": "%H:%M:%S"
            });
        }.bind(this));

        this.datatypeInput.addEvent("change", function () {
            this.switchInputDisplay();
        }.bind(this));

        this.valueTextInput.addEvent("keydown", function (e) {
            if (e.code == 13) this.modifyOrAddFilterItem();
        }.bind(this));
        this.valueNumberInput.addEvent("keydown", function (e) {
            if (e.code == 13) this.modifyOrAddFilterItem();
        }.bind(this));

        this.pathInputSelect.addEvent("change", function ( ev ) {
            var option = ev.target.options[ev.target.selectedIndex];
            if(this.verificationNode)this.verificationNode.destroy();
            if( !this.statementData )return;
            debugger;
            var type = option.retrieve("type");
            var d = this.statementData;
            var field = option.retrieve("field");
            if( field ){
                this.titleInput.set("value", (field.description || field.name).replace(/\./g,""));
                if( field.name ){
                    var path = this.pathInput.get("value");
                    if( path.indexOf(".") > -1 ){
                        path = path.split(".")[0] +"."+ field.name;
                    }else{
                        var alias;
                        var tableName = option.retrieve("tableName");
                        if( d.data.indexOf(tableName) > -1){
                            var str = d.data.split(tableName)[1].trim();
                            if( str.indexOf(" ") )alias = str.split(" ")[0];
                        }
                        path = alias ? ( alias +"."+ field.name ) : field.name;
                    }
                    this.pathInput.set("value", path);
                }
                if( field.type ){
                    var t;
                    switch (( field.type || "string" ).toLowerCase()) {
                        case "string":
                        case "stringList":
                        case "stringLob":
                        case "stringMap":
                            t = "textValue";
                            break;
                        case "integer":
                        case "long":
                        case "double":
                        case "integerList":
                        case "longList":
                        case "doubleList":
                            t = "numberValue";
                            break;
                        case "dateTime":
                        case "date":
                            t = "dateTimeValue";
                            break;
                        // t = "dateValue";
                        // break;
                        case "time":
                            t = "timeValue";
                            break;
                        case "boolean":
                        case "booleanList":
                            t = "booleanValue";
                            break;
                        default:
                            t = "textValue";
                            break;
                    }
                    for (var i = 0; i < this.datatypeInput.options.length; i++) {
                        if (this.datatypeInput.options[i].value === t) {
                            this.datatypeInput.options[i].set("selected", true);
                            this.switchInputDisplay();
                            if (this.datatypeInput.onchange) this.datatypeInput.onchange();
                            break;
                        }
                    }
                }
            }else{
                this.titleInput.set("value", "");
                this.pathInput.set("value", "");
                this.datatypeInput.options[0].set("selected", true);
                this.switchInputDisplay();
                if (this.datatypeInput.onchange)this.datatypeInput.onchange();
            }
        }.bind(this));
        this.setPathInputSelectOptions()

        //if (this.app.statement.view){
        //    var id = this.app.view.data.id;
        //     var div = this.node.getElement("#"+id+"viewParameterValueArea2");
        //     // inputs = div.getElements("input");
        //     if( div ){
        //this.valueTextInput2 = div.getElement(".valueTextInput2_vf") || null;
        //this.valueNumberInput2 = div.getElement(".valueNumberInput2_vf") || null;
        //this.valueDatetimeInput2 = div.getElement(".valueDatetimeInput2_vf") || null;
        //this.valueDateInput2 = div.getElement(".valueDateInput2_vf") || null;
        //this.valueTimeInput2 = div.getElement(".valueTimeInput2_vf") || null;
        //this.valueBooleanInput2 = div.getElement(".valueBooleanInput2_vf") || null;
        //
        //MWF.require("MWF.widget.Calendar", function(){
        //    this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput2, {
        //        "style": "xform",
        //        "isTime": true,
        //        "secondEnable": true,
        //        "target": this.app.content,
        //        "format": "db",
        //        "onComplate": function(){
        //            this.node.getElement("#"+id+"viewParameterDateFormulaSelector2").getElements("input").set("checked", false);
        //        }.bind(this)
        //    });
        //    new MWF.widget.Calendar(this.valueDateInput2, {
        //        "style": "xform",
        //        "isTime": false,
        //        "target": this.app.content,
        //        "format": "%Y-%m-%d"
        //    });
        //    new MWF.widget.Calendar(this.valueTimeInput2, {
        //        "style": "xform",
        //        "timeOnly": true,
        //        "secondEnable": true,
        //        "target": this.app.content,
        //        "format": "%H:%M:%S"
        //    });
        //}.bind(this));
        //
        //this.valueTextInput2.addEvent("keydown", function(e){
        //    if (e.code==13) this.modifyOrAddFilterItem();
        //}.bind(this));
        //this.valueNumberInput2.addEvent("keydown", function(e){
        //    if (e.code==13) this.modifyOrAddFilterItem();
        //}.bind(this));
        //     }
        // }
    },
    setPathInputSelectOptions : function(){
        debugger;
        this.pathInputSelect.empty();
        if( !this.statementData )return;
        var d = this.statementData;
        var fun = function ( tableName ) {
            o2.Actions.load("x_query_assemble_designer").QueryAction.getEntityProperties(
                d.entityCategory === "dynamic" ? d.table : d.entityClassName,
                d.entityCategory,
                function(json){
                    var ps = this.pathInput.get("value").split(".");
                    var p = ps[1] ? ps[1] : ps[0];
                    var option = new Element("option", { "text": "", "value": "" }).inject(this.pathInputSelect);
                    option.store("type", d.entityCategory);
                    option.store("tableName", tableName );
                    (json.data||[]).each( function ( field ) {
                        var option = new Element("option", {
                            "text": field.name + ( field.description ? ("-" + field.description) : "" ),
                            "value": field.name,
                            "selected": (field.name===p)
                        }).inject(this.pathInputSelect);
                        option.store("field", field);
                        option.store("type", d.entityCategory );
                        option.store("tableName", tableName );
                    }.bind(this))
                }.bind(this)
            )
        }.bind(this);

        if( d.entityCategory === "dynamic" ){
            if( d.table ){
                o2.Actions.load("x_query_assemble_designer").TableAction.get(d.table, function(json){
                    fun( json.data.name )
                })
            }
        }else{
            fun( d.entityClassName.split(".").getLast() )
        }
    },
    resetStatementData : function( statementId, callback ){
        if( statementId && statementId !== "none" ){
            this.options.statementId = statementId;
            o2.Actions.load("x_query_assemble_designer").StatementAction.get( statementId, function (json) {
                this.statementData = json.data;
                this.setPathInputSelectOptions();
                if(callback)callback();
            }.bind(this))
        }else{
            this.options.statementId = "";
            this.statementData = null;
            this.setPathInputSelectOptions();
            if(callback)callback();
        }
    },
    switchInputDisplay: function () {
        var formatType = this.datatypeInput.options[this.datatypeInput.selectedIndex].value;

        if( !this.options.withForm ) {
            var id = "";
            if ( this.app.statement && this.app.statement.view ) {
                id = this.app.statement.view.data.id;
            }
            var config = {
                "textValue": {
                    "selectorArea": "#" + id + "viewParameterTextFormulaSelector",
                    "input": this.valueTextInput
                },
                "datetimeValue": {
                    "selectorArea": "#" + id + "viewParameterDateFormulaSelector",
                    "input": this.valueDatetimeInput
                },
                "dateTimeValue": {
                    "selectorArea": "#" + id + "viewParameterDateFormulaSelector",
                    "input": this.valueDatetimeInput
                },
                "dateValue": {
                    "selectorArea": "#" + id + "viewParameterDateOnlyFormulaSelector",
                    "input": this.valueDateInput
                },
                "timeValue": {
                    "selectorArea": "#" + id + "viewParameterTimeOnlyFormulaSelector",
                    "input": this.valueTimeInput
                },
                "numberValue": {
                    "input": this.valueNumberInput
                },
                "booleanValue": {
                    "input": this.valueBooleanInput
                }
            };

            var formulaSelectorIdList = [
                "#" + id + "viewParameterTextFormulaSelector",
                "#" + id + "viewParameterDateFormulaSelector",
                "#" + id + "viewParameterDateOnlyFormulaSelector",
                "#" + id + "viewParameterTimeOnlyFormulaSelector"
            ];

            var inputList = [
                this.valueTextInput,
                this.valueDatetimeInput,
                this.valueDateInput,
                this.valueTimeInput,
                this.valueNumberInput,
                this.valueBooleanInput
            ];
            formulaSelectorIdList.each(function (id) {
                var el = this.inputAreaNode.getElement(id);
                if (!el) return;
                el.setStyle("display", "none");
            }.bind(this));
            inputList.each(function (el) {
                el.setStyle("display", "none");
            }.bind(this));
            var obj = config[formatType];
            if (obj) {
                if (obj.selectorArea) {
                    var el = this.inputAreaNode.getElement(obj.selectorArea);
                    if (el) el.setStyle("display", "block");
                }
                if (obj.input) obj.input.setStyle("display", "block");
            }
        }


        var comparisonConfig = {
            "textValue": ["equals", "notEquals", "greaterThan", "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo", "like", "notLike"],
            "numberValue": ["equals", "notEquals", "greaterThan", "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo"],
            "dateTimeValue": ["equals", "notEquals", "greaterThan", "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo"],
            "dateValue": ["equals", "notEquals", "greaterThan", "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo" ],
            "timeValue": [ "equals", "notEquals", "greaterThan", "greaterThanOrEqualTo", "lessThan", "lessThanOrEqualTo"],
            "booleanValue": ["equals","notEquals"]
        };
        if( this.comparisonInput ){
            var availableComparisonList = comparisonConfig[formatType];
            var options = this.comparisonInput.options;
            var comparison = options[this.comparisonInput.selectedIndex].value;
            if( !this.originalComparisonOptions ){
                this.originalComparisonOptions = [];
                for( var i=0; i< options.length; i++ ){
                    this.originalComparisonOptions.push({
                        "text" : options[i].text,
                        "value" : options[i].value
                    });
                }
            }
            while( this.comparisonInput.options && this.comparisonInput.options.length ){
                this.comparisonInput.options[0].destroy();
            }
            for( var i=0; i<this.originalComparisonOptions.length; i++ ){
                var opt = this.originalComparisonOptions[i];
                if( availableComparisonList.contains( opt.value )){
                    var option = new Element("option", {
                        text : opt.text,
                        value : opt.value
                    }).inject( this.comparisonInput );
                    if( opt.value === comparison )option.selected = true;
                }
            }
        }
    },
    createActionNode: function () {
        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.actionAreaNode);
        this.actionNode.addEvent("click", function () {
            this.modifyOrAddFilterItem();
        }.bind(this));
    },
    modifyOrAddFilterItem: function () {
        var flag;
        var type;
        if (this.currentItem) {
            flag = this.modifyFilterItem();
        } else {
            if( this.restrictFilterInput && this.restrictFilterInput.checked ){ //this.options.withForm this.restrictParameterInput
                flag = this.addFilterItem();
                type = "filter"
            }else if ( this.restrictParameterInput && this.restrictParameterInput.checked) {
                flag = this.addParameterItem();
                type = "parameter"
            } else {
                flag = this.addCustomFilterItem();
                type = "custom"
            }
        }
        if( flag ){
            this.setData({
                "logic": "and",
                "path": "",
                "parameter" : "",
                "title": "",
                "type": type,
                "comparison": "equals",
                "formatType": "textValue",
                "value": "",
                "otherValue": "",
                "code": ""
            });
        }
    },
    modifyFilterItem: function () {
        var data = this.getInputData();
        if( this.restrictFilterInput && this.restrictFilterInput.checked ){ //this.options.withForm
            if (this.verificationDataWithForm(data)) {
                this.currentItem.reload(data);
                this.currentItem.unSelected();
                this.fireEvent("change");
                return true;
            }
        }else if( this.restrictParameterInput && this.restrictParameterInput.checked ){
            if (this.verificationData(data)) {
                this.currentItem.reload(data);
                this.currentItem.unSelected();
                this.fireEvent("change");
                return true;
            }
        }else{
            if (this.verificationDataCustom(data)) {
                this.currentItem.reload(data);
                this.currentItem.unSelected();
                this.fireEvent("change");
                return true;
            }
        }
        return false;
    },
    addParameterItem: function () {
        var data = this.getInputData();
        if (this.verificationData(data)) {
            this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemParameter(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    addCustomFilterItem: function () {
        var data = this.getInputData();
        if (this.verificationDataCustom(data)) {
            this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemCustom(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    addFilterItem : function(){
        var data = this.getInputData();
        if (this.verificationDataWithForm(data)) {
            this.items.push(new MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemFilter(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    verificationData: function (data) {
        if ( this.parameterInput && !data.parameter) {
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            new Element("div", {
                "styles": this.css.verificationTextNode,
                "text": MWF.APPDSMD.LP.mastInputParameter
            }).inject(this.verificationNode);
            this.parameterInput.focus();
            this.parameterInput.setStyle("background-color", "#fbe8e8");

            this.parameterInput.addEvents({
                "keydown": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.parameterInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        // if (data.comparison=="range" && !data.otherValue){
        //     this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
        //     new Element("div", {"styles": this.css.verificationTextNode, "text": MWF.APPDSMD.LP.mastInputPath}).inject(this.verificationNode);
        // }
        return true;
    },
    verificationDataCustom: function (data) {
        if (!data.title) {
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            new Element("div", {
                "styles": this.css.verificationTextNode,
                "text": MWF.APPDSMD.LP.mastInputTitle
            }).inject(this.verificationNode);
            this.titleInput.focus();
            this.titleInput.setStyle("background-color", "#fbe8e8");

            this.titleInput.addEvents({
                "keydown": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.titleInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        if (!data.path || data.path.indexOf(".")<1 ) {
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            var text = !data.path ? MWF.APPDSMD.LP.mastInputPath : MWF.APPDSMD.LP.pathExecption;
            new Element("div", {
                "styles": this.css.verificationTextNode,
                "text": text
            }).inject(this.verificationNode);
            this.pathInput.focus();
            this.pathInput.setStyle("background-color", "#fbe8e8");

            this.pathInput.addEvents({
                "keydown": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.pathInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        return true;
    },
    verificationDataWithForm: function (data) {
        // if (!data.title) {
        //     this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
        //     new Element("div", {
        //         "styles": this.css.verificationTextNode,
        //         "text": MWF.APPDSMD.LP.mastInputTitle
        //     }).inject(this.verificationNode);
        //     this.titleInput.focus();
        //     this.titleInput.setStyle("background-color", "#fbe8e8");
        //
        //     this.titleInput.addEvents({
        //         "keydown": function () {
        //             if (this.verificationNode) {
        //                 this.verificationNode.destroy();
        //                 this.verificationNode = null;
        //                 this.titleInput.setStyle("background-color", "#FFF");
        //             }
        //         }.bind(this),
        //         "click": function () {
        //             if (this.verificationNode) {
        //                 this.verificationNode.destroy();
        //                 this.verificationNode = null;
        //             }
        //         }.bind(this)
        //     });
        //     return false;
        // }
        if (!data.path || data.path.indexOf(".")<1 ) {
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            var text = !data.path ? MWF.APPDSMD.LP.mastInputPath : MWF.APPDSMD.LP.pathExecption;
            new Element("div", {
                "styles": this.css.verificationTextNode,
                "text": text
            }).inject(this.verificationNode);
            this.pathInput.focus();
            this.pathInput.setStyle("background-color", "#fbe8e8");

            this.pathInput.addEvents({
                "keydown": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.pathInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function () {
                    if (this.verificationNode) {
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        return true;
    },
    getInputData: function () {
        // var logic = this.logicInput.options[this.logicInput.selectedIndex].value;
        var path = this.pathInput.get("value");
        var parameter = this.parameterInput ? this.parameterInput.get("value") : "";

        var title = this.titleInput.get("value");

        var type = "custom";
        if ( this.restrictFilterInput && this.restrictFilterInput.checked) type = "filter";
        if ( this.restrictParameterInput && this.restrictParameterInput.checked) type = "parameter";
        if (this.customFilterInput.checked) type = "custom";

        // var comparison = this.comparisonInput.options[this.comparisonInput.selectedIndex].value;
        var comparison = "";
        if( this.comparisonInput ){
            comparison = this.comparisonInput.options[this.comparisonInput.selectedIndex].value;
        }

        var formatType = this.datatypeInput.options[this.datatypeInput.selectedIndex].value;
        var value = "";
        var value2 = "";
        switch (formatType) {
            case "textValue":
                value = this.valueTextInput.get("value") || "";
                //value2 = (this.valueTextInput2) ? (this.valueTextInput2.get("value") || "") : "";
                break;
            case "numberValue":
                value = this.valueNumberInput.get("value").toFloat();
                //value2 = (this.valueNumberInput2) ? this.valueNumberInput2.get("value").toFloat() : "";
                break;
            case "datetimeValue":
            case "dateTimeValue":
                value = this.valueDatetimeInput.get("value") || "";
                //value2 = (this.valueDatetimeInput2) ? (this.valueDatetimeInput2.get("value") || "") : "";
                break;
            case "dateValue":
                value = this.valueDateInput.get("value") || "";
                //value2 = (this.valueDateInput2) ? (this.valueDateInput2.get("value") || "") : "";
                break;
            case "timeValue":
                value = this.valueTimeInput.get("value") || "";
                //value2 = (this.valueTimeInput2) ? (this.valueTimeInput2.get("value") || "") : "";
                break;

            case "booleanValue":
                value = this.valueBooleanInput.options[this.valueBooleanInput.selectedIndex].value;
                //value2 = (this.valueBooleanInput2) ? this.valueBooleanInput2.options[this.valueBooleanInput.selectedIndex].value : "";
                if (value == "true") {
                    value = true;
                } else {
                    value = false;
                }
                //if (value2=="true"){
                //    value2 = true;
                //}else{
                //    value2 = false;
                //}
                break;
        }
        if ( type === "filter" ) { //this.options.withForm
            return {
                // "logic": "and",
                "path": path,
                "title": title,
                "type": type,
                "comparison": comparison,
                "formatType": formatType,
                "value": value,
                "otherValue": value2,
                "code": this.scriptData
            };
        }else if (type === "parameter") {
            this.parameterValueType.each(function (radio) {
                if (radio.get("checked")) valueType = radio.get("value");
            });
            return {
                //"logic": logic,
                "parameter": parameter,
                "title": title,
                "type": type,
                //"comparison": comparison,
                "formatType": formatType,
                "value": value,
                //"otherValue": value2,
                "code": this.scriptData,
                "valueType": valueType,
                "valueScript": this.parameterValueScriptData
            };
        } else {
            var valueType = "";
            this.customFilterValueTypes.each(function (radio) {
                if (radio.get("checked")) valueType = radio.get("value");
            });
            return {
                // "logic": "and",
                "path": path,
                "title": title,
                "type": type,
                // "comparison": comparison,
                "formatType": formatType,
                "value": value,
                "otherValue": value2,
                "code": this.scriptData,
                "valueType": valueType,
                "valueScript": this.customFilterValueScriptData
            };
        }
    },

    setData: function (data) {
        // for (var i=0; i<this.logicInput.options.length; i++){
        //     if (this.logicInput.options[i].value===data.logic){
        //this.logicInput.options[i].set("selected", true);
        //break;
        //     }
        // }

        if(this.titleInput)this.titleInput.set("value", data.title);
        if(this.pathInput)this.pathInput.set("value", data.path);
        if(this.parameterInput)this.parameterInput.set("value", data.parameter);

        if( this.comparisonInput ){
            for (var i=0; i<this.comparisonInput.options.length; i++){
                if (this.comparisonInput.options[i].value===data.comparison){
                    this.comparisonInput.options[i].set("selected", true);
                    break;
                }
            }
        }


        for (var i = 0; i < this.datatypeInput.options.length; i++) {
            if (this.datatypeInput.options[i].value === data.formatType) {
                this.datatypeInput.options[i].set("selected", true);
                break;
            }
        }

        var ps = this.pathInput.get("value").split(".");
        var p = ps[1] ? ps[1] : ps[0];
        var flag = true;
        for (var i = 0; i < this.pathInputSelect.options.length; i++) {
            if (this.pathInputSelect.options[i].value === p) {
                this.pathInputSelect.options[i].set("selected", true);
                flag = false;
                break;
            }
        }
        if(flag && this.pathInputSelect.options.length)this.pathInputSelect.options[0].set("selected", true);


        this.scriptData = data.code;
        try {
            if (this.scriptArea && this.scriptArea.editor) this.scriptArea.editor.setValue(this.scriptData.code);
        } catch (e) {
        }

        if (data.type === "parameter"){
            switch (data.formatType) {
                case "textValue":
                    this.valueTextInput.set("value", data.value);
                    //if (this.valueTextInput2) this.valueTextInput2.set("value", data.otherValue);
                    break;
                case "numberValue":
                    this.valueNumberInput.set("value", data.value);
                    //if (this.valueNumberInput2) this.valueNumberInput2.set("value", data.otherValue);
                    break;
                case "datetimeValue":
                case "dateTimeValue":
                    this.valueDatetimeInput.set("value", data.value);
                    //if (this.valueDatetimeInput2) this.valueDatetimeInput2.set("value", data.otherValue);
                    break;
                case "dateValue":
                    this.valueDateInput.set("value", data.value);
                    //if (this.valueDateInput2) this.valueDateInput2.set("value", data.otherValue);
                    break;
                case "timeValue":
                    this.valueTimeInput.set("value", data.value);
                    //if (this.valueTimeInput2) this.valueTimeInput2.set("value", data.otherValue);
                    break;
                case "booleanValue":

                    for (var i = 0; i < this.valueBooleanInput.options.length; i++) {
                        var v = this.valueBooleanInput.options[i].value;
                        if (v == "true") {
                            v = true;
                        } else {
                            v = false;
                        }
                        if (v === data.value) {
                            this.valueBooleanInput.options[i].set("selected", true);
                            break;
                        }
                    }
                    break;
            }
        }

        if (data.type === "custom") {
            this.customFilterValueTypes.each(function (radio) {
                if (data.valueType) {
                    if (data.valueType === radio.get("value")) radio.set("checked", true);
                } else {
                    if ("input" === radio.get("value")) radio.set("checked", true);
                }
            });
            if (this.customFilterValueScriptArea) {
                if (!data.valueType || data.valueType === "input") {
                    this.customFilterValueScriptDiv.hide();
                    this.customFilterValueScriptData = "";
                    this.customFilterValueScriptArea.editor.setValue("");
                } else {
                    this.customFilterValueScriptDiv.show();
                    this.customFilterValueScriptData = data.valueScript;
                    this.customFilterValueScriptArea.editor.setValue(data.valueScript ? data.valueScript.code : "");
                }
            }
        }

        if (data.type === "parameter") {
            this.parameterValueType.each(function (radio) {
                if (data.valueType) {
                    if (data.valueType === radio.get("value")) radio.set("checked", true);
                } else {
                    if ("input" === radio.get("value")) radio.set("checked", true);
                }
            });
            if (this.parameterValueScriptArea) {
                if (!data.valueType || data.valueType === "input") {
                    this.parameterValueScriptDiv.hide();
                    this.parameterValueScriptData = "";
                    this.parameterValueScriptArea.editor.setValue("");
                } else {
                    this.parameterValueScriptDiv.show();
                    this.parameterValueScriptData = data.valueScript;
                    this.parameterValueScriptArea.editor.setValue(data.valueScript ? data.valueScript.code : "");
                }
            }
        }
        this.switchInputDisplay();

        if (this.datatypeInput.onchange) {
            this.datatypeInput.onchange();
        }
    },

    deleteItem: function (item) {
        if (this.currentItem == item) item.unSelected();
        this.items.erase(item);
        item.node.destroy();
        MWF.release(item);
        this.fireEvent("change");
    },
    getData: function () {
        var parameterData = [];
        var customData = [];
        var filterData = [];
        this.items.each(function (item) {
            if (item.data.type === "custom") {
                customData.push(item.data);
            }else if (item.data.type === "filter") {
                filterData.push(item.data);
            } else {
                parameterData.push(item.data);
            }
        }.bind(this));
        return {"parameterData": parameterData, "customData": customData, "filterData" : filterData };
    }
});

MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemParameter = new Class({
    Implements: [Events],
    initialize: function (filter, data) {
        this.filter = filter;
        this.data = data;
        this.container = this.filter.parameterListAreaNode;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    load: function () {
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.container);
        this.deleteNode = new Element("div", {"styles": this.css.itemDeleteNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.itemContentNode}).inject(this.node);
        this.contentNode.set("text", this.getText());

        this.contentNode.addEvent("click", function () {
            this.selected();
        }.bind(this));

        this.deleteNode.addEvent("click", function (e) {
            this.deleteItem(e);
        }.bind(this));
    },
    getText: function () {
        var lp = MWF.APPDSMD.LP.filter;
        if (this.data.formatType === "numberValue") {
            return this.data.title + " " + this.data.parameter + " " + this.data.value;
        } else {
            return this.data.title + " " + this.data.parameter + " \"" + this.data.value + "\"";
        }
    },
    reload: function (data) {
        this.data = data;
        this.contentNode.set("text", this.getText());
    },
    selected: function () {
        if( this.filter.verificationNode ){
            this.filter.verificationNode.destroy();
            this.filter.verificationNode = null;
            this.filter.parameterInput.setStyle("background-color", "#FFF");
        }
        this.filter.restrictParameterInput.set("checked", true);
        this.filter.restrictParameterInput.click();
        if (this.filter.currentItem) this.filter.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentItem = this;
        this.filter.setData(this.data);
    },
    unSelected: function () {
        this.node.setStyles(this.css.itemNode);
        this.filter.currentItem = null;
    },
    deleteItem: function (e) {
        var _self = this;
        this.filter.app.confirm("warn", e, MWF.APPDSMD.LP.delete_filterItem_title, MWF.APPDSMD.LP.delete_filterItem, 300, 120, function () {
            _self.destroy();
            this.close();
        }, function () {
            this.close();
        });
    },
    destroy: function () {
        this.filter.deleteItem(this);
    }
});

MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemCustom = new Class({
    Extends: MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemParameter,
    initialize: function (filter, data) {
        this.filter = filter;
        this.data = data;
        this.container = this.filter.customFilterListAreaNode;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    selected: function () {
        if( this.filter.verificationNode ){
            this.filter.verificationNode.destroy();
            this.filter.verificationNode = null;
            this.filter.pathInput.setStyle("background-color", "#FFF");
        }
        this.filter.customFilterInput.set("checked", true);
        this.filter.customFilterInput.click();
        if (this.filter.currentItem) this.filter.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentItem = this;
        this.filter.setData(this.data);
    },
    getText: function () {
        var lp = MWF.APPDSMD.LP.filter;
        return this.data.title + "(" + this.data.path + ")";
    },
});

MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemFilter = new Class({
    Extends: MWF.xApplication.query.StatementDesigner.widget.ViewFilter.ItemParameter,
    initialize: function (filter, data) {
        this.filter = filter;
        this.data = data;
        this.container = this.filter.filterListAreaNode;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    selected: function () {
        if( this.filter.verificationNode ){
            this.filter.verificationNode.destroy();
            this.filter.verificationNode = null;
            this.filter.pathInput.setStyle("background-color", "#FFF");
        }
        this.filter.restrictFilterInput.set("checked", true);
        this.filter.restrictFilterInput.click();
        if (this.filter.currentItem) this.filter.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentItem = this;
        this.filter.setData(this.data);
    },
    getText: function () {
        var lp = MWF.APPDSMD.LP.filter;
        return this.data.title + "(" + this.data.path + ")";
    },
});
