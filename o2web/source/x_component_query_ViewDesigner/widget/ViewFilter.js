MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xApplication.query.ViewDesigner.widget = MWF.xApplication.query.ViewDesigner.widget || {};

MWF.xApplication.query.ViewDesigner.widget.ViewFilter = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "type": "identity",
        "names": []
    },
    initialize: function(node, app, filtrData, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = app;
        this.filtrData = filtrData;

        this.path = "../x_component_query_ViewDesigner/widget/$ViewFilter/";
        this.cssPath = "../x_component_query_ViewDesigner/widget/$ViewFilter/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.items = [];
        this.load();

    },
    load: function(data){
        this.getInputNodes();
        this.createActionNode();
        //this.createAddNode();
        //this.loadIdentitys();
        this.loadData();
    },
    loadData: function(){
        if (this.filtrData.filtrData && this.filtrData.filtrData.length){
            this.filtrData.filtrData.each(function(data){
                this.items.push(new MWF.xApplication.query.ViewDesigner.widget.ViewFilter.Item(this, data));
            }.bind(this));
        }

        if (this.filtrData.customData && this.filtrData.customData.length){
            this.filtrData.customData.each(function(data){
                data.type = "custom";
                this.items.push(new MWF.xApplication.query.ViewDesigner.widget.ViewFilter.ItemCustom(this, data));
            }.bind(this));
        }
    },
    createScriptArea: function(node){
        this.scriptValueArea = node;
        var title = node.get("title");

        MWF.require("MWF.widget.ScriptArea", function(){
            this.scriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "maxObj": this.app.formContentNode || this.app.pageContentNode,
                "onChange": function(){
                    this.scriptData = this.scriptArea.toJson();
                }.bind(this),
                "onSave": function(){
                    //this.app.saveForm();
                }.bind(this),
                "style": "formula"
            });
            var v = (this.scriptData) ? this.scriptData.code : "";
            this.scriptArea.load(v);
        }.bind(this));
    },
    createCustomFilterValueScriptArea : function(node){
        var title = node.get("title");

        MWF.require("MWF.widget.ScriptArea", function(){
            this.customFilterValueScriptArea = new MWF.widget.ScriptArea(node, {
                "title": title,
                "isload" : true,
                "isbind" : false,
                "forceType": "ace",
                "maxObj": this.app.formContentNode || this.app.pageContentNode,
                "onChange": function(){
                    this.customFilterValueScriptData = this.customFilterValueScriptArea.toJson();
                }.bind(this),
                "onSave": function(){
                    //this.app.saveForm();
                }.bind(this),
                "style": "formula"
            });
            var v = (this.customFilterValueScriptData) ? this.customFilterValueScriptData.code : "";
            this.customFilterValueScriptArea.load(v);
        }.bind(this));
    },
    getInputNodes: function(){
        debugger;
        this.inputAreaNode = this.node.getElement(".inputAreaNode_vf");
        this.actionAreaNode = this.node.getElement(".actionAreaNode_vf");
        this.actionAreaNode.setStyles( this.css.actionAreaNode );
        this.listAreaNode = this.node.getElement(".listAreaNode_vf");
        this.fieldListAreaNode = this.node.getElement(".fieldListAreaNode_vf");

        this.restrictViewFilterTable = this.node.getElement(".restrictViewFilterTable_vf");

        var scriptValueArea = this.node.getElement(".MWFFilterFormulaArea");
        if (scriptValueArea){
            this.createScriptArea(scriptValueArea);
        }

        this.multiValueNoticeNode = this.inputAreaNode.getElement(".multiValueNotice_vf");

        this.titleInput = this.inputAreaNode.getElement(".titleInput_vf");
        this.pathInput = this.inputAreaNode.getElement(".pathInput_vf");
        this.datatypeInput = this.inputAreaNode.getElement(".datatypeInput_vf");

        this.restrictFilterInput = this.inputAreaNode.getElement(".restrictFilterInput_vf");
        this.customFilterInput = this.inputAreaNode.getElement(".customFilterInput_vf");

        this.logicInput = this.inputAreaNode.getElement(".logicInput_vf");

        this.comparisonInput = this.inputAreaNode.getElement(".comparisonInput_vf");
        this.comparisonInput.addEvent("change", function(){
            this.switchInputDisplay();
        }.bind(this))

        this.valueTextInput = this.inputAreaNode.getElement(".valueTextInput_vf");
        this.valueNumberInput = this.inputAreaNode.getElement(".valueNumberInput_vf");
        this.valueDatetimeInput = this.inputAreaNode.getElement(".valueDatetimeInput_vf");
        this.valueBooleanInput = this.inputAreaNode.getElement(".valueBooleanInput_vf");
        this.valueDateInput = this.inputAreaNode.getElement(".valueDateInput_vf");
        this.valueTimeInput = this.inputAreaNode.getElement(".valueTimeInput_vf");

        if( this.app.view ){
            var dataId = this.app.view.data.id;
            this.customFilterValueTypes = this.inputAreaNode.getElements("[name='"+dataId+"viewCustomFilterValueType']");

            this.customFilterValueScriptDiv = this.inputAreaNode.getElement("#"+dataId+"viewCustomFilterValueScriptDiv");
            this.customFilterValueScript = this.inputAreaNode.getElement("[name='"+dataId+"viewCustomFilterValueScript']");
            if( this.customFilterValueScript ){
                this.createCustomFilterValueScriptArea(this.customFilterValueScript);
            }
        }

        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput, {
                "style": "xform",
                "isTime": true,
                "secondEnable": true,
                "target": this.app.content,
                "format": "db",
                "onComplate": function(){
                    this.node.getElement("#"+id+"viewFilterDateFormulaSelector").getElements("input").set("checked", false);
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

        this.datatypeInput.addEvent("change", function(){
            // this.changeValueInput();
            this.switchInputDisplay();
        }.bind(this));

        this.valueTextInput.addEvent("keydown", function(e){
            if (e.code==13) this.modifyOrAddFilterItem();
        }.bind(this));
        this.valueNumberInput.addEvent("keydown", function(e){
            if (e.code==13) this.modifyOrAddFilterItem();
        }.bind(this));

        if (this.app.view){
            var id = this.app.view.data.id;
            var div = this.node.getElement("#"+id+"viewFilterValueArea2");
            // inputs = div.getElements("input");
            if( div ){
                this.valueTextInput2 = div.getElement(".valueTextInput2_vf") || null;
                this.valueNumberInput2 = div.getElement(".valueNumberInput2_vf") || null;
                this.valueDatetimeInput2 = div.getElement(".valueDatetimeInput2_vf") || null;
                this.valueDateInput2 = div.getElement(".valueDateInput2_vf") || null;
                this.valueTimeInput2 = div.getElement(".valueTimeInput2_vf") || null;
                this.valueBooleanInput2 = div.getElement(".valueBooleanInput2_vf") || null;

                MWF.require("MWF.widget.Calendar", function(){
                    this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput2, {
                        "style": "xform",
                        "isTime": true,
                        "secondEnable": true,
                        "target": this.app.content,
                        "format": "db",
                        "onComplate": function(){
                            this.node.getElement("#"+id+"viewFilterDateFormulaSelector2").getElements("input").set("checked", false);
                        }.bind(this)
                    });
                    new MWF.widget.Calendar(this.valueDateInput2, {
                        "style": "xform",
                        "isTime": false,
                        "target": this.app.content,
                        "format": "%Y-%m-%d"
                    });
                    new MWF.widget.Calendar(this.valueTimeInput2, {
                        "style": "xform",
                        "timeOnly": true,
                        "secondEnable": true,
                        "target": this.app.content,
                        "format": "%H:%M:%S"
                    });
                }.bind(this));

                this.valueTextInput2.addEvent("keydown", function(e){
                    if (e.code==13) this.modifyOrAddFilterItem();
                }.bind(this));
                this.valueNumberInput2.addEvent("keydown", function(e){
                    if (e.code==13) this.modifyOrAddFilterItem();
                }.bind(this));
            }
        }
    },
    switchInputDisplay : function(){
        var id = "";
        if( this.app.view ){
            id = this.app.view.data.id;
        }

        var lp = this.app.lp.filter;

        var config = {
            "textValue": {
                "equals": {
                    "selectorArea" : ["#"+id+"viewFilterTextFormulaSelector"] //["input", "@person", "@unitList", "@unitAllList", "@identityList" ],
                },
                "notEquals": {
                    "selectorArea" : ["#"+id+"viewFilterTextFormulaSelector"] //["input", "@person", "@unitList", "@unitAllList", "@identityList" ],
                },
                "greaterThan": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "greaterThanOrEqualTo": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "lessThan": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "lessThanOrEqualTo": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "like": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "notLike": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "range": {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                },
                "in" : {
                    "invalidValue" : ["@person", "@unitList", "@unitAllList", "@identityList" ]
                }
            },
            "numberValue": {
                "equals": {}, //["input"]
                "notEquals": {},
                "greaterThan": {},
                "greaterThanOrEqualTo": {},
                "lessThan": {},
                "lessThanOrEqualTo": {},
                "range": {}
            },
            "dateTimeValue": {
                "equals": {
                    "invalidValue" : ["@year","@season","@month","@time","@date"]
                },
                "notEquals": {
                    "selectorArea" : ["#"+id+"viewFilterDateFormulaSelector"]
                },
                "greaterThan": {
                    "selectorArea" : ["#"+id+"viewFilterDateFormulaSelector"]
                },
                "greaterThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterDateFormulaSelector"]
                },
                "lessThan": {
                    "selectorArea" : ["#"+id+"viewFilterDateFormulaSelector"]
                },
                "lessThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterDateFormulaSelector"]
                },
                "range": {
                    "invalidValue" : ["@year","@season","@month","@time","@date"]
                }
            },
            "dateValue": {
                "equals": {
                    "invalidValue" : ["@year","@season","@month","@date"]
                },
                "notEquals": {
                    "selectorArea" : ["#"+id+"viewFilterDateOnlyFormulaSelector"]
                },
                "greaterThan":{
                    "selectorArea" : ["#"+id+"viewFilterDateOnlyFormulaSelector"]
                },
                "greaterThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterDateOnlyFormulaSelector"]
                },
                "lessThan": {
                    "selectorArea" : ["#"+id+"viewFilterDateOnlyFormulaSelector"]
                },
                "lessThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterDateOnlyFormulaSelector"]
                },
                "range": {
                    "invalidValue" : ["@year","@season","@month","@date"]
                }
            },
            "timeValue": {
                "equals": {
                    "invalidValue" : ["@time"]
                },
                "notEquals": {
                    "selectorArea" : ["#"+id+"viewFilterTimeOnlyFormulaSelector"]
                },
                "greaterThan": {
                    "selectorArea" : ["#"+id+"viewFilterTimeOnlyFormulaSelector"]
                },
                "greaterThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterTimeOnlyFormulaSelector"]
                },
                "lessThan": {
                    "selectorArea" : ["#"+id+"viewFilterTimeOnlyFormulaSelector"]
                },
                "lessThanOrEqualTo": {
                    "selectorArea" : ["#"+id+"viewFilterTimeOnlyFormulaSelector"]
                },
                "range": {
                    "invalidValue" : ["@time"]
                }
            },
            "booleanValue": {
                "equals": {},
                "notEquals": {}
            }
        };

        debugger;

        var formulaSelectorIdList = [
            "#"+id+"viewFilterTextFormulaSelector",
            "#"+id+"viewFilterDateFormulaSelector",
            "#"+id+"viewFilterDateOnlyFormulaSelector",
            "#"+id+"viewFilterTimeOnlyFormulaSelector"
        ];

        var formatType = this.datatypeInput.options[this.datatypeInput.selectedIndex].value;
        var object = config[formatType];

        var availableComparisonList = Object.keys( object );
        var options = this.comparisonInput.options;

        var comparison = options[this.comparisonInput.selectedIndex].value;
        if( availableComparisonList.contains( "in" ) && comparison === "in" ){
            if(this.multiValueNoticeNode)this.multiValueNoticeNode.show()
        }else{
            if(this.multiValueNoticeNode)this.multiValueNoticeNode.hide()
        }

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

        var availableInputArray = [];
        var invalidValue = [];
        for( var key in object ){
            if( comparison === key ){
                availableInputArray = object[key].selectorArea || [];
                invalidValue = object[key].invalidValue || [];
                break;
            }
        }
        for( var i=0; i<invalidValue.length; i++ ){
            invalidValue[i] = invalidValue[i].toLowerCase();
        }

        formulaSelectorIdList.each( function(id) {
            var el = this.inputAreaNode.getElement( id );
            if( !el )return;
            if( availableInputArray.contains(id) ){
                el.setStyle("display", "block");
            }else{
                el.getElements("input").set("checked", false);
                el.setStyle("display", "none");
            }
        }.bind(this));

        var viewFilterValue2Area, viewFilterValueTitleArea;
        if ( availableComparisonList.contains("range") && comparison=="range"){
            viewFilterValue2Area = this.inputAreaNode.getElement("#"+id+"viewFilterValue2Area");
            if(viewFilterValue2Area)viewFilterValue2Area.setStyle('display', 'block');

            viewFilterValueTitleArea = this.inputAreaNode.getElement("#"+id+"viewFilterValueTitleArea");
            if(viewFilterValueTitleArea)viewFilterValueTitleArea.set('text', lp.from );
        }else{
            viewFilterValue2Area = this.inputAreaNode.getElement("#"+id+"viewFilterValue2Area");
            if(viewFilterValue2Area)viewFilterValue2Area.setStyle('display', 'none');

            viewFilterValueTitleArea = this.inputAreaNode.getElement("#"+id+"viewFilterValueTitleArea");
            if(viewFilterValueTitleArea)viewFilterValueTitleArea.set('text', lp.value );
        }
        switch (formatType){
            case "textValue":
                this.valueTextInput.setStyle("display", "block");
                if( invalidValue.contains( (this.valueTextInput.get("value") || "" ).toLowerCase() )){
                    this.valueTextInput.set("value","");
                }
                this.valueNumberInput.setStyle("display", "none");
                this.valueDatetimeInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "none");
                this.valueTimeInput.setStyle("display", "none");
                this.valueBooleanInput.setStyle("display", "none");
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "block");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
                break;
            case "numberValue":
                this.valueTextInput.setStyle("display", "none");
                this.valueNumberInput.setStyle("display", "block");
                if( invalidValue.contains( (this.valueNumberInput.get("value") || "" ).toLowerCase() )){
                    this.valueNumberInput.set("value","");
                }
                this.valueDatetimeInput.setStyle("display", "none");
                this.valueBooleanInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "none");
                this.valueTimeInput.setStyle("display", "none");
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "block");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
                break;
            case "datetimeValue":
            case "dateTimeValue":
                this.valueTextInput.setStyle("display", "none");
                this.valueNumberInput.setStyle("display", "none");
                this.valueDatetimeInput.setStyle("display", "block");
                if( invalidValue.contains( (this.valueDatetimeInput.get("value") || "" ).toLowerCase() )){
                    this.valueDatetimeInput.set("value","");
                }
                this.valueBooleanInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "none");
                this.valueTimeInput.setStyle("display", "none");
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "block");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
                break;
            case "dateValue":
                this.valueTextInput.setStyle("display", "none");
                this.valueNumberInput.setStyle("display", "none");
                this.valueDatetimeInput.setStyle("display", "none");
                this.valueBooleanInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "block");
                if( invalidValue.contains( (this.valueDateInput.get("value") || "" ).toLowerCase() )){
                    this.valueDateInput.set("value","");
                }
                this.valueTimeInput.setStyle("display", "none");
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "block");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
                break;
            case "timeValue":
                this.valueTextInput.setStyle("display", "none");
                this.valueNumberInput.setStyle("display", "none");
                this.valueDatetimeInput.setStyle("display", "none");
                this.valueBooleanInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "none");
                this.valueTimeInput.setStyle("display", "block");
                if( invalidValue.contains( (this.valueTimeInput.get("value") || "" ).toLowerCase() )){
                    this.valueTimeInput.set("value","");
                }
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "block");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
                break;
            case "booleanValue":
                this.valueTextInput.setStyle("display", "none");
                this.valueNumberInput.setStyle("display", "none");
                this.valueDatetimeInput.setStyle("display", "none");
                this.valueDateInput.setStyle("display", "none");
                this.valueTimeInput.setStyle("display", "none");
                this.valueBooleanInput.setStyle("display", "block");
                if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
                if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
                if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
                if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
                if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "block");
                break;
        }
    },
    // getInputNodes: function(){
    //     this.inputAreaNode = this.node.getFirst("div");
    //     this.actionAreaNode = this.inputAreaNode.getNext().setStyles(this.css.actionAreaNode);
    //     this.listAreaNode = this.actionAreaNode.getNext().getNext();
    //     this.fieldListAreaNode = this.listAreaNode.getNext().getNext();
    //     this.restrictViewFilterTable = this.inputAreaNode.getLast("table");
    //
    //     var selects = this.inputAreaNode.getElements("select");
    //     var inputs = this.inputAreaNode.getElements("input");
    //     var scriptValueArea = this.node.getElement(".MWFFilterFormulaArea");
    //     if (scriptValueArea){
    //         this.createScriptArea(scriptValueArea);
    //     }
    //
    //     this.titleInput = inputs[0];
    //     this.pathInput = inputs[1];
    //     this.datatypeInput = selects[0];
    //
    //     this.restrictFilterInput = inputs[2];
    //     this.customFilterInput = inputs[3];
    //
    //     this.logicInput = selects[1];
    //     this.comparisonInput = selects[2];
    //     this.valueTextInput = inputs[4];
    //     this.valueNumberInput = inputs[5];
    //     this.valueDatetimeInput = inputs[6];
    //     this.valueBooleanInput = selects[3];
    //     this.valueDateInput = inputs[7];
    //     this.valueTimeInput = inputs[8];
    //
    //     debugger;
    //     var dataId = this.app.view.data.id;
    //     this.customFilterValueTypes = this.inputAreaNode.getElements("[name='"+dataId+"viewCustomFilterValueType']");
    //
    //     this.customFilterValueScriptDiv = this.inputAreaNode.getElement("#"+dataId+"viewCustomFilterValueScriptDiv");
    //     this.customFilterValueScript = this.inputAreaNode.getElement("[name='"+dataId+"viewCustomFilterValueScript']");
    //     if( this.customFilterValueScript ){
    //         this.createCustomFilterValueScriptArea(this.customFilterValueScript);
    //     }
    //
    //     this.datatypeInput.addEvent("change");
    //
    //     MWF.require("MWF.widget.Calendar", function(){
    //         this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput, {
    //             "style": "xform",
    //             "isTime": true,
    //             "secondEnable": true,
    //             "target": this.app.content,
    //             "format": "db",
    //             "onComplate": function(){
    //                 this.node.getElement("#"+id+"viewFilterDateFormulaSelector").getElements("input").set("checked", false);
    //             }.bind(this)
    //         });
    //         new MWF.widget.Calendar(this.valueDateInput, {
    //             "style": "xform",
    //             "isTime": false,
    //             "target": this.app.content,
    //             "format": "%Y-%m-%d"
    //         });
    //         new MWF.widget.Calendar(this.valueTimeInput, {
    //             "style": "xform",
    //             "timeOnly": true,
    //             "secondEnable": true,
    //             "target": this.app.content,
    //             "format": "%H:%M:%S"
    //         });
    //     }.bind(this));
    //
    //     this.datatypeInput.addEvent("change", function(){
    //         this.changeValueInput();
    //     }.bind(this));
    //
    //     this.valueTextInput.addEvent("keydown", function(e){
    //         if (e.code==13) this.modifyOrAddFilterItem();
    //     }.bind(this));
    //     this.valueNumberInput.addEvent("keydown", function(e){
    //         if (e.code==13) this.modifyOrAddFilterItem();
    //     }.bind(this));
    //
    //     if (this.app.view){
    //         var id = this.app.view.data.id;
    //         var div = this.node.getElement("#"+id+"viewFilterValueArea2");
    //         inputs = div.getElements("input");
    //         this.valueTextInput2 = inputs[0] || null;
    //         this.valueNumberInput2 = inputs[1] || null;
    //         this.valueDatetimeInput2 = inputs[2] || null;
    //         this.valueDateInput2 = inputs[3] || null;
    //         this.valueTimeInput2 = inputs[4] || null;
    //         this.valueBooleanInput2 = div.getElement("select") || null;
    //
    //         MWF.require("MWF.widget.Calendar", function(){
    //             this.calendar = new MWF.widget.Calendar(this.valueDatetimeInput2, {
    //                 "style": "xform",
    //                 "isTime": true,
    //                 "secondEnable": true,
    //                 "target": this.app.content,
    //                 "format": "db",
    //                 "onComplate": function(){
    //                     this.node.getElement("#"+id+"viewFilterDateFormulaSelector2").getElements("input").set("checked", false);
    //                 }.bind(this)
    //             });
    //             new MWF.widget.Calendar(this.valueDateInput2, {
    //                 "style": "xform",
    //                 "isTime": false,
    //                 "target": this.app.content,
    //                 "format": "%Y-%m-%d"
    //             });
    //             new MWF.widget.Calendar(this.valueTimeInput2, {
    //                 "style": "xform",
    //                 "timeOnly": true,
    //                 "secondEnable": true,
    //                 "target": this.app.content,
    //                 "format": "%H:%M:%S"
    //             });
    //         }.bind(this));
    //
    //         this.valueTextInput2.addEvent("keydown", function(e){
    //             if (e.code==13) this.modifyOrAddFilterItem();
    //         }.bind(this));
    //         this.valueNumberInput2.addEvent("keydown", function(e){
    //             if (e.code==13) this.modifyOrAddFilterItem();
    //         }.bind(this));
    //     }
    // },
    // changeValueInput: function(){
    //     var type = this.datatypeInput.options[this.datatypeInput.selectedIndex].value;
    //     switch (type){
    //         case "textValue":
    //             this.valueTextInput.setStyle("display", "block");
    //             this.valueNumberInput.setStyle("display", "none");
    //             this.valueDatetimeInput.setStyle("display", "none");
    //             this.valueDateInput.setStyle("display", "none");
    //             this.valueTimeInput.setStyle("display", "none");
    //             this.valueBooleanInput.setStyle("display", "none");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "block");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
    //             break;
    //         case "numberValue":
    //             this.valueTextInput.setStyle("display", "none");
    //             this.valueNumberInput.setStyle("display", "block");
    //             this.valueDatetimeInput.setStyle("display", "none");
    //             this.valueBooleanInput.setStyle("display", "none");
    //             this.valueDateInput.setStyle("display", "none");
    //             this.valueTimeInput.setStyle("display", "none");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "block");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
    //             break;
    //         case "datetimeValue":
    //         case "dateTimeValue":
    //             this.valueTextInput.setStyle("display", "none");
    //             this.valueNumberInput.setStyle("display", "none");
    //             this.valueDatetimeInput.setStyle("display", "block");
    //             this.valueBooleanInput.setStyle("display", "none");
    //             this.valueDateInput.setStyle("display", "none");
    //             this.valueTimeInput.setStyle("display", "none");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "block");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
    //             break;
    //         case "dateValue":
    //             this.valueTextInput.setStyle("display", "none");
    //             this.valueNumberInput.setStyle("display", "none");
    //             this.valueDatetimeInput.setStyle("display", "none");
    //             this.valueBooleanInput.setStyle("display", "none");
    //             this.valueDateInput.setStyle("display", "block");
    //             this.valueTimeInput.setStyle("display", "none");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "block");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
    //             break;
    //         case "timeValue":
    //             this.valueTextInput.setStyle("display", "none");
    //             this.valueNumberInput.setStyle("display", "none");
    //             this.valueDatetimeInput.setStyle("display", "none");
    //             this.valueBooleanInput.setStyle("display", "none");
    //             this.valueDateInput.setStyle("display", "none");
    //             this.valueTimeInput.setStyle("display", "block");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "block");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "none");
    //             break;
    //         case "booleanValue":
    //             this.valueTextInput.setStyle("display", "none");
    //             this.valueNumberInput.setStyle("display", "none");
    //             this.valueDatetimeInput.setStyle("display", "none");
    //             this.valueTimeInput.setStyle("display", "none");
    //             this.valueBooleanInput.setStyle("display", "block");
    //             if (this.valueTextInput2) this.valueTextInput2.setStyle("display", "none");
    //             if (this.valueNumberInput2) this.valueNumberInput2.setStyle("display", "none");
    //             if (this.valueDatetimeInput2) this.valueDatetimeInput2.setStyle("display", "none");
    //             if (this.valueDateInput2) this.valueDateInput2.setStyle("display", "none");
    //             if (this.valueTimeInput2) this.valueTimeInput2.setStyle("display", "none");
    //             if (this.valueBooleanInput2) this.valueBooleanInput2.setStyle("display", "block");
    //             break;
    //     }
    // },
    createActionNode: function(){
        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.actionAreaNode);
        this.actionNode.addEvent("click", function(){
            this.modifyOrAddFilterItem();
        }.bind(this));
    },
    modifyOrAddFilterItem: function(){
        if (this.currentFilterItem){
            this.modifyFilterItem();
        }else{
            if (this.restrictFilterInput.checked){
                this.addFilterItem();
            }else{
                this.addCustomFilterItem();
            }
        }
        this.setData({
            "logic": "and",
            "path": "",
            "title": "",
            "type": this.restrictFilterInput.checked ? "restrict" : "custom",
            "comparison": "equals",
            "formatType": "textValue",
            "value": "",
            "otherValue": "",
            "code": ""
        });
    },
    modifyFilterItem: function(){
        var data = this.getInputData();
        if (this.verificationData(data)){
            this.currentFilterItem.reload(data);
            this.currentFilterItem.unSelected();
            this.fireEvent("change");
        }
    },
    addFilterItem: function(){
        var data = this.getInputData();
        if (this.verificationData(data)){
            this.items.push(new MWF.xApplication.query.ViewDesigner.widget.ViewFilter.Item(this, data));
            this.fireEvent("change");
        }
    },
    addCustomFilterItem: function(){
        var data = this.getInputData();
        if (this.verificationDataCustom(data)){
            this.items.push(new MWF.xApplication.query.ViewDesigner.widget.ViewFilter.ItemCustom(this, data));
            this.fireEvent("change");
        }
    },
    verificationData: function(data){
        if (!data.path){
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            new Element("div", {"styles": this.css.verificationTextNode, "text": this.app.lp.mastInputPath}).inject(this.verificationNode);
            this.pathInput.focus();
            this.pathInput.setStyle("background-color", "#fbe8e8");

            this.pathInput.addEvents({
                "keydown": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.pathInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        // if (data.comparison=="range" && !data.otherValue){
        //     this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
        //     new Element("div", {"styles": this.css.verificationTextNode, "text": this.app.lp.mastInputPath}).inject(this.verificationNode);
        // }
        return true;
    },
    verificationDataCustom: function(data){
        if (!data.title){
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            new Element("div", {"styles": this.css.verificationTextNode, "text": this.app.lp.mastInputTitle}).inject(this.verificationNode);
            this.titleInput.focus();
            this.titleInput.setStyle("background-color", "#fbe8e8");

            this.titleInput.addEvents({
                "keydown": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.titleInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        if (!data.path){
            this.verificationNode = new Element("div", {"styles": this.css.verificationNode}).inject(this.inputAreaNode);
            new Element("div", {"styles": this.css.verificationTextNode, "text": this.app.lp.mastInputPath}).inject(this.verificationNode);
            this.pathInput.focus();
            this.pathInput.setStyle("background-color", "#fbe8e8");

            this.pathInput.addEvents({
                "keydown": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                        this.pathInput.setStyle("background-color", "#FFF");
                    }
                }.bind(this),
                "click": function(){
                    if (this.verificationNode){
                        this.verificationNode.destroy();
                        this.verificationNode = null;
                    }
                }.bind(this)
            });
            return false;
        }
        return true;
    },
    getInputData: function(){
        var logic = this.logicInput.options[this.logicInput.selectedIndex].value;
        var path = this.pathInput.get("value");
        var title = this.titleInput.get("value");
        if (this.restrictFilterInput.checked) var type = "restrict";
        if (this.customFilterInput.checked) var type = "custom";

        var comparison = this.comparisonInput.options[this.comparisonInput.selectedIndex].value;
        var formatType = this.datatypeInput.options[this.datatypeInput.selectedIndex].value;
        var value = "";
        var value2 = "";
        switch (formatType){
            case "textValue":
                value = this.valueTextInput.get("value") || "";
                value2 = (this.valueTextInput2) ? (this.valueTextInput2.get("value") || "") : "";
                break;
            case "numberValue":
                value = this.valueNumberInput.get("value").toFloat();
                value2 = (this.valueNumberInput2) ? this.valueNumberInput2.get("value").toFloat() : "";
                break;
            case "datetimeValue":
            case "dateTimeValue":
                value = this.valueDatetimeInput.get("value") || "";
                value2 = (this.valueDatetimeInput2) ? (this.valueDatetimeInput2.get("value") || "") : "";
                break;
            case "dateValue":
                value = this.valueDateInput.get("value") || "";
                value2 = (this.valueDateInput2) ? (this.valueDateInput2.get("value") || "") : "";
                break;
            case "timeValue":
                value = this.valueTimeInput.get("value") || "";
                value2 = (this.valueTimeInput2) ? (this.valueTimeInput2.get("value") || "") : "";
                break;

            case "booleanValue":
                value = this.valueBooleanInput.options[this.valueBooleanInput.selectedIndex].value;
                value2 = (this.valueBooleanInput2) ? this.valueBooleanInput2.options[this.valueBooleanInput.selectedIndex].value : "";
                if (value=="true"){
                    value = true;
                }else{
                    value = false;
                }
                if (value2=="true"){
                    value2 = true;
                }else{
                    value2 = false;
                }
                break;
        }
        if( type === "restrict" ){
            return {
                "logic": logic,
                "path": path,
                "title": title,
                "type": type,
                "comparison": comparison,
                "formatType": formatType,
                "value": value,
                "otherValue": value2,
                "code": this.scriptData
            };
        }else{
            var valueType = "";
            this.customFilterValueTypes.each( function (radio) {
                if( radio.get("checked") )valueType = radio.get("value");
            });
            return {
                "logic": logic,
                "path": path,
                "title": title,
                "type": type,
                "comparison": comparison,
                "formatType": formatType,
                "value": value,
                "otherValue": value2,
                "code": this.scriptData,
                "valueType" : valueType,
                "valueScript" : this.customFilterValueScriptData
            };
        }
    },

    setData: function(data){
        for (var i=0; i<this.logicInput.options.length; i++){
            if (this.logicInput.options[i].value===data.logic){
                this.logicInput.options[i].set("selected", true);
                break;
            }
        }

        this.titleInput.set("value", data.title);
        this.pathInput.set("value", data.path);

        for (var i=0; i<this.comparisonInput.options.length; i++){
            if (this.comparisonInput.options[i].value===data.comparison){
                this.comparisonInput.options[i].set("selected", true);
                break;
            }
        }

        for (var i=0; i<this.datatypeInput.options.length; i++){
            if (this.datatypeInput.options[i].value===data.formatType){
                this.datatypeInput.options[i].set("selected", true);
                break;
            }
        }

        switch (data.formatType){
            case "textValue":
                this.valueTextInput.set("value", data.value);
                if (this.valueTextInput2) this.valueTextInput2.set("value", data.otherValue);
                break;
            case "numberValue":
                this.valueNumberInput.set("value", data.value);
                if (this.valueNumberInput2) this.valueNumberInput2.set("value", data.otherValue);
                break;
            case "datetimeValue":
            case "dateTimeValue":
                this.valueDatetimeInput.set("value", data.value);
                if (this.valueDatetimeInput2) this.valueDatetimeInput2.set("value", data.otherValue);
                break;
            case "dateValue":
                this.valueDateInput.set("value", data.value);
                if (this.valueDateInput2) this.valueDateInput2.set("value", data.otherValue);
                break;
            case "timeValue":
                this.valueTimeInput.set("value", data.value);
                if (this.valueTimeInput2) this.valueTimeInput2.set("value", data.otherValue);
                break;
            case "booleanValue":

                for (var i=0; i<this.valueBooleanInput.options.length; i++){
                    var v = this.valueBooleanInput.options[i].value;
                    if (v=="true"){
                        v = true;
                    }else{
                        v = false;
                    }
                    if (v===data.value){
                        this.valueBooleanInput.options[i].set("selected", true);
                        break;
                    }
                }
                if (this.valueBooleanInput2){
                    for (var i=0; i<this.valueBooleanInput2.options.length; i++){
                        var v = this.valueBooleanInput2.options[i].value;
                        if (v=="true"){
                            v = true;
                        }else{
                            v = false;
                        }
                        if (v===data.otherValue){
                            this.valueBooleanInput2.options[i].set("selected", true);
                            break;
                        }
                    }
                }
                break;
        }
        this.scriptData = data.code;
        try{
            if (this.scriptArea && this.scriptArea.editor) this.scriptArea.editor.setValue(this.scriptData.code);
        }catch (e) {}

        debugger;
        if( data.type === "custom" ){
            this.customFilterValueTypes.each( function (radio) {
                if( data.valueType ){
                    if( data.valueType === radio.get("value") )radio.set("checked", true);
                }else{
                    if( "input" === radio.get("value") )radio.set("checked", true);
                }
            });
            if ( this.customFilterValueScriptArea ){
                if( !data.valueType || data.valueType === "input" ){
                    this.customFilterValueScriptDiv.hide();
                    this.customFilterValueScriptData = "";
                    this.customFilterValueScriptArea.editor.setValue( "" );
                }else{
                    this.customFilterValueScriptDiv.show();
                    this.customFilterValueScriptData = data.valueScript;
                    this.customFilterValueScriptArea.editor.setValue( data.valueScript ? data.valueScript.code : "" );
                }
            }
        }


        this.switchInputDisplay();
        if(this.datatypeInput.onchange){
            this.datatypeInput.onchange();
        }
    },

    deleteItem: function(item){
        if (this.currentFilterItem == item) item.unSelected();
        this.items.erase(item);
        item.node.destroy();
        MWF.release(item);
        this.fireEvent("change");
    },
    getData: function(){
        var data = [];
        var customData = [];
        this.items.each(function(item){
            if (item.data.type==="custom"){
                customData.push(item.data);
            }else{
                data.push(item.data);
            }
        }.bind(this));
        return {"data": data, "customData": customData};
    }
});



MWF.xApplication.query.ViewDesigner.widget.ViewFilter.Item = new Class({
    Implements: [Events],
    initialize: function(filter, data){
        this.filter = filter;
        this.data = data;
        this.container = this.filter.listAreaNode;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.itemNode}).inject(this.container);
        this.deleteNode = new Element("div", {"styles": this.css.itemDeleteNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.itemContentNode}).inject(this.node);
        this.contentNode.set("text", this.getText());

        this.contentNode.addEvent("click", function(){
            this.selected();
        }.bind(this));

        this.deleteNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },
    getText: function(){
        var lp = this.app.lp.filter;
        if( this.data.formatType === "numberValue" ){
            return lp[this.data.logic]+" "+this.data.path+" "+lp[this.data.comparison] + " "+this.data.value+((this.data.comparison=="range") ? ", \""+this.data.otherValue+"\"" : "");
        }else{
            return lp[this.data.logic]+" "+this.data.path+" "+lp[this.data.comparison] + " \""+this.data.value+"\""+((this.data.comparison=="range") ? ", \""+this.data.otherValue+"\"" : "");
        }
    },
    reload: function(data){
        this.data = data;
        this.contentNode.set("text", this.getText());
    },
    selected: function(){
        this.filter.restrictFilterInput.set("checked", true);
        this.filter.restrictFilterInput.click();
        if (this.filter.currentFilterItem) this.filter.currentFilterItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentFilterItem = this;
        this.filter.setData(this.data);
    },
    unSelected: function(){
        this.node.setStyles(this.css.itemNode);
        this.filter.currentFilterItem = null;
        this.filter.currentItem = this;
    },
    deleteItem: function(e){
        var _self = this;
        this.filter.app.confirm("warn", e, this.app.lp.delete_filterItem_title, this.app.lp.delete_filterItem, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        });
    },
    destroy: function(){
        this.filter.deleteItem(this);
    }
});
MWF.xApplication.query.ViewDesigner.widget.ViewFilter.ItemCustom = new Class({
    Extends: MWF.xApplication.query.ViewDesigner.widget.ViewFilter.Item,
    initialize: function(filter, data){
        this.filter = filter;
        this.data = data;
        this.container = this.filter.fieldListAreaNode;
        this.css = this.filter.css;
        this.app = this.filter.app;
        this.load();
    },
    selected: function(){
        this.filter.customFilterInput.set("checked", true);
        this.filter.customFilterInput.click();
        if (this.filter.currentFilterItem) this.filter.currentFilterItem.unSelected();
        this.node.setStyles(this.css.itemNode_current);
        this.filter.currentFilterItem = this;
        this.filter.setData(this.data);
    },
    getText: function(){
        var lp = this.app.lp.filter;
        return this.data.title+"("+this.data.path+")";
    }
});
