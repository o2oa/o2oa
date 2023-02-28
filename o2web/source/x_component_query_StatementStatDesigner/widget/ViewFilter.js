MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementStatDesigner = MWF.xApplication.query.StatementStatDesigner || {};
if(!MWF.APPDSMSD)MWF.APPDSMSD = MWF.xApplication.query.StatementStatDesigner;
MWF.xApplication.query.StatementStatDesigner.widget = MWF.xApplication.query.StatementStatDesigner.widget || {};
if( !MWF.APPDSMSD.LP ){
    MWF.xDesktop.requireApp("query.StatementStatDesigner", "lp." + MWF.language, null, false);
}
MWF.xDesktop.requireApp("query.StatementDesigner", "widget.ViewFilter", null, false);

MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter = new Class({
    Extends: MWF.xApplication.query.StatementDesigner.widget.ViewFilter,
    options: {
        "style": "default",
        "type": "identity",
        "withForm" : false,
        "names": []
    },
    load: function (data) {
        this.app.statement = this.app.statementStat;
        var _load = function () {
            this.getInputNodes();
            this.createActionNode();
            this.loadData();
        }.bind(this);
        if( this.app.statement && this.app.statement.data ){
            this.statementData = this.app.statement.data;
            _load();
        }else if( this.options.statementId ){
            o2.Actions.load("x_query_assemble_designer").StatementAction.get( this.options.statementId, function (json) {
                this.statementData = json.data;
                _load();
            }.bind(this));
        }else{
            _load();
        }
    },
    loadData: function () {
        if (this.filtrData.parameterData && this.filtrData.parameterData.length ) {
            this.filtrData.parameterData.each(function (data) {
                if( this.options.withForm ){
                    if( this.parameterListAreaNode_form ){
                        data.type = "parameter_form";
                        this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameterForm(this, data));
                    }
                }else{
                    if( this.parameterListAreaNode ){
                        data.type = "parameter";
                        this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter(this, data));
                    }
                }
            }.bind(this));
        }

        if (this.filtrData.filtrData && this.filtrData.filtrData.length && this.filterListAreaNode ) {
            this.filtrData.filtrData.each(function (data) {
                data.type = "filter";
                this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemFilter(this, data));
            }.bind(this));
        }

        if (this.filtrData.customData && this.filtrData.customData.length && this.customFilterListAreaNode ) {
            this.filtrData.customData.each(function (data) {
                data.type = "custom";
                this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemCustom(this, data));
            }.bind(this));
        }
    },
    getInputNodes: function () {
        debugger;
        this.inputAreaNode = this.node.getElement(".inputAreaNode_vf");
        this.actionAreaNode = this.node.getElement(".actionAreaNode_vf");
        this.actionAreaNode.setStyles(this.css.actionAreaNode);

        this.filterListAreaNode = this.node.getElement(".filterListAreaNode_vf");
        this.parameterListAreaNode_form = this.node.getElement(".parameterListAreaNode_form_vf");
        this.parameterListAreaNode = this.node.getElement(".parameterListAreaNode_vf");
        this.customFilterListAreaNode = this.node.getElement(".customFilterListAreaNode_vf");

        this.restrictViewFilterTable = this.node.getElement(".restrictViewFilterTable_vf");

        var scriptValueArea = this.node.getElement(".MWFFilterFormulaArea");
        if (scriptValueArea) {
            this.createScriptArea(scriptValueArea);
        }

        this.titleInput = this.inputAreaNode.getElement(".titleInput_vf");
        this.applicableStatementSelector = this.inputAreaNode.getElement(".applicableStatement_vf");
        this.setApplicableStatementOptions();

        this.pathInput = this.inputAreaNode.getElement(".pathInput_vf");
        this.pathInputSelector = this.inputAreaNode.getElement(".pathInputSelector_vf");
        this.parameterInput = this.inputAreaNode.getElement(".parameterInput_vf");
        // this.parameterInputSelect = this.inputAreaNode.getElement(".parameterInputSelect_vf");
        this.datatypeInput = this.inputAreaNode.getElement(".datatypeInput_vf");

        this.restrictParameterInput = this.inputAreaNode.getElement(".restrictParameterInput_vf");
        this.customFilterInput = this.inputAreaNode.getElement(".customFilterInput_vf");

        this.restrictFilterInput = this.inputAreaNode.getElement(".restrictFilterInput_vf");
        this.restrictParameterInput_form = this.inputAreaNode.getElement(".restrictParameterInput_form_vf");

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
            if(this.valueDatetimeInput){
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
            }
            if(this.valueDateInput){
                new MWF.widget.Calendar(this.valueDateInput, {
                    "style": "xform",
                    "isTime": false,
                    "target": this.app.content,
                    "format": "%Y-%m-%d"
                });
            }
            if(this.valueTimeInput){
                new MWF.widget.Calendar(this.valueTimeInput, {
                    "style": "xform",
                    "timeOnly": true,
                    "secondEnable": true,
                    "target": this.app.content,
                    "format": "%H:%M:%S"
                });
            }
        }.bind(this));

        if(this.datatypeInput)this.datatypeInput.addEvent("change", function () {
            this.switchInputDisplay();
        }.bind(this));

        if(this.valueTextInput)this.valueTextInput.addEvent("keydown", function (e) {
            if (e.code == 13) this.modifyOrAddFilterItem();
        }.bind(this));
        if(this.valueNumberInput)this.valueNumberInput.addEvent("keydown", function (e) {
            if (e.code == 13) this.modifyOrAddFilterItem();
        }.bind(this));

        if(this.pathInputSelector){
            MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.PersonSelector", function() {
                new MWF.xApplication.process.ProcessDesigner.widget.PersonSelector(this.pathInputSelector, this.app, {
                    "type": "TableField",
                    "names": [],
                    "count": 1,
                    "onClick": function (selector) {
                        selector.options.tables = this.statementData.statementList;
                    }.bind(this),
                    "onChange": function (ids) {
                        this.afterSelectTableField( ids.map(function (id) {
                            return id.data;
                        }));
                    }.bind(this)
                });
            }.bind(this));
        }
        //this.setPathInputSelectOptions()
    },
    setApplicableStatementOptions: function(){
        this.applicableStatementSelector.getElements("option").each(function (opt, i) {
            if( i === 0 )return;
            opt.destroy();
        });
        if( this.statementData && this.statementData.statementList ){
            this.statementData.statementList.each(function (statement) {
                new Element("option", {
                    text: statement.name,
                    value: statement.name
                }).inject( this.applicableStatementSelector );
            }.bind(this));
        }
    },
    afterSelectTableField: function ( fieldList ) {
        if(this.verificationNode)this.verificationNode.destroy();
        if( fieldList.length ){
            var field = fieldList[0];
            this.titleInput.set("value", (field.description || field.name).replace(/\./g,""));
            if( field.name ){
                var path = this.pathInput.get("value");
                if( path.indexOf(".") > -1 ){
                    path = path.split(".")[0] +"."+ field.name;
                }else{
                    path = "o."+ field.name;
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
    },
    modifyOrAddFilterItem: function () {
        var flag;
        var type;
        if (this.currentItem) {
            flag = this.modifyFilterItem();
        } else {
            if( this.restrictFilterInput && this.restrictFilterInput.checked ){ //this.options.withForm this.restrictParameterInput
                flag = this.addFilterItem();
                type = "filter";
            }else if ( this.restrictParameterInput && this.restrictParameterInput.checked) {
                flag = this.addParameterItem();
                type = "parameter";
            }else if ( this.restrictParameterInput_form && this.restrictParameterInput_form.checked) {
                flag = this.addParameterItem_form();
                type = "parameter_form";
            } else {
                flag = this.addCustomFilterItem();
                type = "custom";
            }
        }
        if( flag ){
            this.setData({
                "logic": "and",
                "path": "",
                "parameter" : "",
                "statement": "all",
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
    addParameterItem: function () {
        var data = this.getInputData();
        if (this.verificationData(data)) {
            this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    addParameterItem_form: function () {
        var data = this.getInputData();
        if (this.verificationParameterDataWithForm(data)) {
            this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameterForm(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    addCustomFilterItem: function () {
        var data = this.getInputData();
        if (this.verificationDataCustom(data)) {
            this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemCustom(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    addFilterItem : function(){
        var data = this.getInputData();
        if (this.verificationDataWithForm(data)) {
            this.items.push(new MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemFilter(this, data));
            this.fireEvent("change");
            return true;
        }
        return false;
    },
    getInputData: function () {
        // var logic = this.logicInput.options[this.logicInput.selectedIndex].value;
        var statement = "all";
        if( this.applicableStatementSelector ){
            statement = this.applicableStatementSelector.options[this.applicableStatementSelector.selectedIndex].value;
        }

        var path = this.pathInput.get("value");
        var parameter = this.parameterInput ? this.parameterInput.get("value") : "";

        var title = this.titleInput.get("value");

        var type = "custom";
        if ( this.restrictFilterInput && this.restrictFilterInput.checked) type = "filter";
        if ( this.restrictParameterInput_form && this.restrictParameterInput_form.checked) type = "parameter_form";
        if ( this.restrictParameterInput && this.restrictParameterInput.checked) type = "parameter";
        if ( this.customFilterInput && this.customFilterInput.checked) type = "custom";

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
                break;
            case "numberValue":
                value = this.valueNumberInput.get("value").toFloat();
                break;
            case "datetimeValue":
            case "dateTimeValue":
                value = this.valueDatetimeInput.get("value") || "";
                break;
            case "dateValue":
                value = this.valueDateInput.get("value") || "";
                break;
            case "timeValue":
                value = this.valueTimeInput.get("value") || "";
                break;

            case "booleanValue":
                value = this.valueBooleanInput.options[this.valueBooleanInput.selectedIndex].value;
                if (value == "true") {
                    value = true;
                } else {
                    value = false;
                }
                break;
        }
        if ( type === "filter" ) { //this.options.withForm
            return {
                // "logic": "and",
                "path": path,
                "title": title,
                "statement": statement,
                "type": type,
                "comparison": comparison,
                "formatType": formatType,
                "value": value,
                "otherValue": value2,
                "code": this.scriptData
            };
        }else if( type === "parameter_form" ){
            return {
                "parameter": parameter,
                "title": title,
                "statement": statement,
                "type": type,
                "formatType": formatType,
                "value": value,
                "code": this.scriptData,
                "valueType": "script",
                "valueScript": this.scriptData
            };
        }else if (type === "parameter") {
            this.parameterValueType.each(function (radio) {
                if (radio.get("checked")) valueType = radio.get("value");
            });
            return {
                //"logic": logic,
                "parameter": parameter,
                "title": title,
                "statement": statement,
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
                "statement": statement,
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

        if( this.applicableStatementSelector ){
            for (var i=0; i<this.applicableStatementSelector.options.length; i++){
                if (this.applicableStatementSelector.options[i].value===data.statement){
                    this.applicableStatementSelector.options[i].set("selected", true);
                    break;
                }
            }
        }

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
    }
});

MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter = new Class({
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
            return "(" + this.data.statement + ") " + this.data.title + " " + this.data.parameter + " " + this.data.value;
        } else {
            return "(" + this.data.statement + ") " + this.data.title + " " + this.data.parameter + " \"" + this.data.value + "\"";
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

MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemCustom = new Class({
    Extends: MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter,
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
        return "(" + this.data.statement + ") " + this.data.title + " " + this.data.path;
    },
});

MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemFilter = new Class({
    Extends: MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter,
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
        return "(" + this.data.statement + ") " + this.data.title + " " + this.data.path;
    },
});


MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameterForm = new Class({
    Extends: MWF.xApplication.query.StatementStatDesigner.widget.ViewFilter.ItemParameter,
    initialize: function (filter, data) {
        this.filter = filter;
        this.data = data;
        this.container = this.filter.parameterListAreaNode_form;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    getText: function () {
        var lp = MWF.APPDSMD.LP.filter;
        return "(" + this.data.statement + ") " + this.data.parameter;
    },
    selected: function () {
        if( this.filter.verificationNode ){
            this.filter.verificationNode.destroy();
            this.filter.verificationNode = null;
            this.filter.parameterInput.setStyle("background-color", "#FFF");
        }
        this.filter.restrictParameterInput_form.set("checked", true);
        this.filter.restrictParameterInput_form.click();
        if (this.filter.currentItem) this.filter.currentItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentItem = this;
        this.filter.setData(this.data);
    }
});
