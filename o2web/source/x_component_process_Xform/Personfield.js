MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.process.Xform.Personfield = MWF.APPPersonfield =  new Class({
    Implements: [Events],
    Extends: MWF.APP$Input,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad", "change", "select"],
        "readonly": true
    },

    iconStyle: "personfieldIcon",

    getTextData: function(){
        //var value = this.node.get("value");
        //var text = this.node.get("text");
        var value = this.getValue();
        //var text = (this.node.getFirst()) ? this.node.getFirst().get("text") : this.node.get("text");
        var text = [];
        if( typeOf( value ) === "object" )value = [value];
        if( typeOf( value ) === "array" ){
            value.each(function(v){
                if( typeOf(v) === "string" ){
                    text.push(v);
                }else{
                    text.push(v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                }
            }.bind(this));
            return {"value": value || "", "text": [text.join(",")]};
        }else{
            return {"value": [""], "text": [""]};
        }
    },

    loadDescription: function(){
        if (this.readonly || this.json.isReadonly)return;
        var v = this._getBusinessData();
        if (!v || !v.length){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if( this.json.showIcon!='no' && !this.form.json.hideModuleIcon ) {
                    if (COMMON.Browser.safari) w = w - 20;
                }
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function( ev ){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect( ev );
                }.bind(this)
            });
        }
    },
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly){
            this._loadNodeRead();
        }else{
            this._getOrgOptions();
            if (this.json.isInput){
                this._loadNodeInputEdit();
            }else{
                this._loadNodeEdit();
            }

        }
    },
    _getOrgOptions: function(){
        this.selectUnits = this.getSelectRange();
        if (this.json.selectType=="identity"){
            this.selectDutys = this.getSelectRangeDuty();
        }
    },
    getScriptSelectUnit: function(){
        var rangeValues = [];
        if (this.json.rangeUnit && this.json.rangeUnit.length){
            this.json.rangeUnit.each(function(unit){
                var unitFlag = unit.distinguishedName || unit.id || unit.unique || unit.levelName;
                if (unitFlag) rangeValues.push(unitFlag);
            }.bind(this));
        }
        if (this.json.rangeField && this.json.rangeField.length){
            this.json.rangeField.each(function(field){
                var n = (typeOf(field)=="object") ? field.name : field;
                var v = this.form.businessData.data[n];
                if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d){
                        if (typeOf(d)==="string"){
                            var data;
                            this.getOrgAction().getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                            rangeValues.push(data);
                        }else{
                            rangeValues.push(d);
                        }
                    }
                }.bind(this));
            }.bind(this));
        }
        if (this.json.rangeKey && this.json.rangeKey.code){
            var v = this.form.Macro.exec(this.json.rangeKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d){
                    if (typeOf(d)==="string"){
                        var data;
                        this.getOrgAction().getUnit(function(json){ data = json.data }.bind(this), null, d, false);
                        rangeValues.push(data);
                    }else{
                        rangeValues.push(d);
                    }
                }
            }.bind(this));
        }
        return rangeValues;
    },

    getScriptSelectDuty: function(){
        var rangeValues = [];
        if (this.json.rangeDuty && this.json.rangeDuty.length){
            this.json.rangeDuty.each(function(unit){
                var unitFlag = unit.id || unit.name;
                if (unitFlag) rangeValues.push(unitFlag);
            }.bind(this));
        }
        if (this.json.rangeDutyField && this.json.rangeDutyField.length){
            this.json.rangeDutyField.each(function(field){
                var n = (typeOf(field)=="object") ? field.name : field;
                var v = this.form.businessData.data[n];
                if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d) rangeValues.push(d);
                }.bind(this));
            }.bind(this));
        }
        if (this.json.rangeDutyKey && this.json.rangeDutyKey.code){
            var v = this.form.Macro.exec(this.json.rangeDutyKey.code, this);
            if (typeOf(v)!=="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d) rangeValues.push(d);
            }.bind(this));
        }
        return rangeValues;
    },

    _computeValue: function(){
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.unitValue) {
            this.json.unitValue.each(function(v){ if (v) values.push(v)});
        }
        var simple = this.json.storeRange === "simple";

        if (this.json.dutyValue) {
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length){
                dutys.each(function(duty){
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    var code = "return this.org.getDuty(\""+duty.name+"\", \""+par+"\")";

                    //var code = "return this.org.getDepartmentDuty({\"name\": \""+duty.name+"\", \"departmentName\": \""+par+"\"})";
                    var d = this.form.Macro.exec(code, this);
                    if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                    d.each(function(dd){
                        if (dd) values.push(MWF.org.parseOrgData(dd,true,simple));
                    });

                    // code = "return this.org.getCompanyDuty({\"name\": \""+duty.name+"\", \"compName\": \""+par+"\"})";
                    // d = this.form.Macro.exec(code, this);
                    // if (typeOf(d)!=="array") d = (d) ? [d.toString()] : [];
                    // d.each(function(dd){values.push(dd);});
                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd)!=="array") fd = (fd) ? [fd] : [];
            fd.each(function(fdd){
                if (fdd){
                    if (typeOf(fdd)==="string"){
                        var data;
                        this.getOrgAction()[this.getValueMethod(fdd)](function(json){ data = MWF.org.parseOrgData(json.data,true, simple); }.bind(this), null, fdd, false);
                        values.push(data);
                    }else{
                        values.push(fdd);
                    }
                }
            }.bind(this));
        }
        if (this.json.count>0){
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    // getDepartments: function(){
    //     if (this.json.range==="depart"){
    //         return this.getRange();
    //     }
    //     if (this.json.range==="currentdepart"){
    //         return [this.form.app.currentTask.department];
    //     }
    //     return [];
    // },
    // getCompanys: function(){
    //     if (this.json.range==="company"){
    //         //var comp = "";
    //         //if (this.json.rangeKey){
    //             return this.getRange();
    //         //}
    //     }
    //     if (this.json.range==="currentcompany"){
    //         return [this.form.app.currentTask.company];
    //     }
    //     return [];
    // },
    getOrgAction: function(){
        if (!this.orgAction) this.orgAction = MWF.Actions.get("x_organization_assemble_control");
        //if (!this.orgAction) this.orgAction = new MWF.xApplication.Selector.Actions.RestActions();
        return this.orgAction;
    },
    getNextSelectUnit: function(id){
        var data;
        if (this.json.rangeNext === "direct"){
            if (typeOf(id)==="array"){
                var units = [];
                id.each(function(i){
                    this.getOrgAction().getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, i, false);
                    if (data && data.woUnit) units.push(data.woUnit);
                    data = null;
                }.bind(this));
                return units;
            }else{
                this.getOrgAction().getIdentity(function(json){ data = json.data }.bind(this), function(){data={"woUnit": null}}, id, false);
                return (data.woUnit) ? [data.woUnit] : [];
            }
        }
        if (this.json.rangeNext==="level"){
            this.getOrgAction().getUnitWithIdentityWithLevel(id, this.json.rangeNextLevel, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
            //this.json.rangeNextLevel
            return (data) ? [data] : [];
        }
        if (this.json.rangeNext==="type"){
            if (this.json.rangeNextUnitType==="all"){
                this.getOrgAction().getUnitWithIdentityWithLevel(id, 1, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
            }else{
                this.getOrgAction().getUnitWithIdentityWithType(id, this.json.rangeNextUnitType, function(json){ data = json.data }.bind(this), function(){data=null;}, false);
            }

            return (data) ? [data] : [];
        }

    },
    getSelectRange: function(){
        if (this.json.range==="unit"){
            return this.getScriptSelectUnit();
        }
        if (this.json.range==="draftUnit"){
            var dn = (this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn;
            if (!dn){
                if ( layout.session.user.identityList && layout.session.user.identityList.length){
                    var ids = [];
                    layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                    return this.getNextSelectUnit(ids);
                }else{
                    return [];
                }
            }else{
                return this.getNextSelectUnit((this.form.businessData.work || this.form.businessData.workCompleted).creatorIdentityDn);
            }
        }
        if (this.json.range==="currentUnit"){
            if (this.form.app.currentTask){
                return this.getNextSelectUnit(this.form.app.currentTask.identityDn);
            }else{
                if (this.form.app.taskList && this.form.app.taskList.length){
                    var ids = [];
                    this.form.app.taskList.each(function(task){ ids.push(task.identity); });
                    return this.getNextSelectUnit(ids);
                }else{
                    if ( layout.session.user.identityList && layout.session.user.identityList.length){
                        var ids = [];
                        layout.session.user.identityList.each(function(id){ ids.push(id.id); });
                        return this.getNextSelectUnit(ids);
                    }else{
                        return [];
                    }
                }
            }
        }
        return [];
    },
    getSelectRangeDuty: function(){
        if (this.json.dutyRange==="duty"){
            return this.getScriptSelectDuty();
        }
        return [];
    },
    getOptions: function(){
        var values = this.getInputData();
        var count = (this.json.count) ? this.json.count : 0;

        switch (this.json.range){
            case "":
        }


        var selectUnits = this.getSelectRange();
        if (this.json.selectType=="identity"){
            var selectDutys = this.getSelectRangeDuty();
        }

        if (this.json.range!=="all"){
            if (!selectUnits.length){
                this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
                return false;
            }
        }
        if (this.json.selectType=="identity"){
            if ((this.json.dutyRange) && this.json.dutyRange!=="all"){
                if (!selectDutys || !selectDutys.length){
                    this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
                    return false;
                }
            }
        }

        var exclude = [];
        if( this.json.exclude ){
            var v = this.form.Macro.exec(this.json.exclude.code, this);
            exclude = typeOf(v)==="array" ? v : [v];
        }

        var options = {
            "type": this.json.selectType,
            "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
            "values": (this.json.isInput) ? [] : values,
            "count": count,
            "units": selectUnits,
            "dutys": (this.json.selectType=="identity") ? selectDutys : [],
            "exclude" : exclude,
            "expandSubEnable" : (this.json.expandSubEnable=="no") ? false : true,
            "categoryType": this.json.categoryType || "unit",
            "onComplete": function(items){
                this.selectOnComplete(items);
            }.bind(this),
            "onCancel": this.selectOnCancel.bind(this),
            "onLoad": this.selectOnLoad.bind(this),
            "onClose": this.selectOnClose.bind(this)
        };
        if( this.form.json.selectorStyle )options = Object.merge( options, this.form.json.selectorStyle );

        return options;
    },
    selectOnComplete: function(items){
        var simple = this.json.storeRange === "simple";
        var values = [];
        items.each(function(item){
            values.push(MWF.org.parseOrgData(item.data, true, simple));
        }.bind(this));
        if (this.json.isInput){
            this.addData(values);
        }else{
            this.setData(values);
        }
        //this._setBusinessData(values);
        this.validationMode();
        this.validation();
        this.fireEvent("select");
    },
    selectOnCancel: function(){
        this.validation();
    },
    selectOnLoad: function(){
        if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
    },
    selectOnClose: function(){
        v = this._getBusinessData();
        if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
    },

    clickSelect: function( ev ){

        var options = this.getOptions();
        if( this.selector && this.selector.loading ) {
        }else if( this.selector && this.selector.selector && this.selector.selector.active ){
        }else{
            this.selector = new MWF.O2Selector(this.form.app.content, options);
        }
    },
    resetData: function(){
        var v = this.getValue();
        //this.setData((v) ? v.join(", ") : "");
        this.setData(v);
    },
    isEmpty: function(){
        var data = this.getData();
        if( typeOf(data) !== "array" )return true;
        if( data.length === 0 )return true;
        return false;
    },
    getInputData: function(){
        if (this.json.isInput){
            if (this.combox) return this.combox.getData();
            return this._getBusinessData();
        }else{
            return this._getBusinessData();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        var node = new Element("div").inject(this.node);
    },
    _searchConfirmPerson: function(item){
        var inforNode = item.inforNode || new Element("div");
        if (item.data){
            var text = "";
            var flag = item.data.distinguishedName.substr(item.data.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    text = item.data.name+"("+item.data.unitName+")";
                    break;
                case "@p":
                    text = item.data.name+"("+item.data.employee+")";
                    break;
                case "@u":
                    text = item.data.levelName;
                    break;
                case "@g":
                    text = item.data.name;
                    break;
                default:
                    text = item.data.name;
            }
            inforNode.set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });
        }else{
            inforNode.set({
                "styles": {"font-size": "14px", "color": "#bd0000"},
                "text": MWF.xApplication.process.Xform.LP.noOrgObject
            });
        }
        if (!item.inforNode){
            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: item.node,
                transition: 'flyin'
            });
            item.inforNode = inforNode;
        }

    },
    _searchOptions: function(value, callback){
        var options = {
            "type": this.json.selectType,
            "unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
            "units": this.selectUnits,
            "dutys": (this.json.selectType=="identity") ? this.selectDutys : []
        };
        if (!this.comboxFilter) this.comboxFilter = new MWF.O2SelectorFilter(value, options);
        this.comboxFilter.filter(value, function(data){
            data.map(function(d){
                var value = Object.clone(d);
                d.value = value;
                var flag = d.distinguishedName.substr(d.distinguishedName.length-2, 2);
                switch (flag.toLowerCase()){
                    case "@i":
                        d.text = d.name+"("+d.unitName+")";
                        break;
                    case "@p":
                        d.text = d.name+"("+d.employee+")";
                        break;
                    case "@u":
                        d.text = d.name;
                        break;
                    case "@g":
                        d.text = d.name;
                        break;
                    default:
                        d.text = d.name;
                }
            });
            if (callback) callback(data);
        });
    },
    _resetNodeInputEdit: function(){
        var node = new Element("div", {
            "styles": {
                "overflow": "hidden",
                //"position": "relative",
                "margin-right": "20px"
            }
        }).inject(this.node, "after");
        this.node.destroy();
        this.node = node;
    },
    _loadNodeInputEdit: function() {
        var input = null;
        MWF.require("MWF.widget.Combox", function () {
            this.combox = input = new MWF.widget.Combox({
                "count": this.json.count || 0,
                "splitShow": this.json.splitShow || ", ",
                "onCommitInput": function (item) {
                    this._searchConfirmPerson(item);
                    //this.fireEvent("change");
                }.bind(this),
                "onChange": function () {
                    this._setBusinessData(this.getInputData());
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions.bind(this)

            });
        }.bind(this), false);
        input.setStyles({
            "background": "transparent",
            "border": "0px"
        });
        input.set(this.json.properties);

        if (!this.json.preprocessing) this._resetNodeInputEdit();

        this.node.empty();
        input.inject(this.node);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        if (this.json.showIcon != 'no' && !this.form.json.hideModuleIcon){
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": function (ev) {
                        this.clickSelect( ev );
                    }.bind(this)
                    //this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
    },

    _resetNodeEdit: function(){
        var input = new Element("div", {
            "styles": {
                "background": "transparent",
                "border": "0px",
                "min-height": "24px"
            }
        });
        var node = new Element("div", {"styles": {
                "overflow": "hidden",
                "position": "relative",
                "margin-right": "20px",
                "min-height": "24px"
            }}).inject(this.node, "after");
        input.inject(node);
        this.node.destroy();
        this.node = node;
    },
    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        input.set(this.json.properties);
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type,
            "events": {
                "click": function (ev) {
                    this.clickSelect( ev );
                }.bind(this)
                //this.clickSelect.bind(this)
            }
        });
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon) this.iconNode = new Element("div", {
            "styles": this.form.css[this.iconStyle],
            "events": {
                "click": function (ev) {
                    this.clickSelect( ev );
                }.bind(this)
                //this.clickSelect.bind(this)
            }
        }).inject(this.node, "before");

        this.node.getFirst().setStyle("height", "auto");
        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()) this._setBusinessData(this.getInputData("change"));
        }.bind(this));
    },
    getDataText: function(data){
        if (typeOf(data)=="string") return data;
        var text = "";
        var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
        switch (flag.toLowerCase()){
            case "@i":
                text = data.name+"("+data.unitName+")";
                break;
            case "@p":
                text = data.name+"("+data.employee+")";
                break;
            case "@u":
                text = data.name;
                break;
            case "@g":
                text = data.name;
                break;
            default:
                text = data.name;
        }
        return text;
    },
    addData: function(value){
        if (!value) return false;
        var simple = this.json.storeRange === "simple";
        value.each(function(v){
            var vtype = typeOf(v);
            if (vtype==="string"){
                var data;
                this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple); }.bind(this), null, v, false);
                if (data) this.combox.addNewValue(this.getDataText(data), data);
            }
            if (vtype==="object"){
                this.combox.addNewValue(this.getDataText(v), v);
            }
        }.bind(this));
    },
    setData: function(value){
        if (!value) return false;
        var oldValues = this.getData();
        var values = [];
        var comboxValues = [];


        var simple = this.json.storeRange === "simple";

        var type = typeOf(value);
        if (type==="array"){
            value.each(function(v){
                var vtype = typeOf(v);
                var data = null;
                if (vtype==="string"){
                    var error = (this.json.isInput) ? function(){ comboxValues.push(v); } : null;
                    this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, false, simple); }.bind(this), error, v, false);
                }
                if (vtype==="object") {
                    data = MWF.org.parseOrgData(v, false, simple);
                    if(data.woPerson)delete data.woPerson;
                }
                if (data){
                    values.push(data);
                    comboxValues.push({"text": this.getDataText(data),"value": data});
                }
            }.bind(this));
        }
        if (type==="string"){
            var vData;
            var error = (this.json.isInput) ? function(){ comboxValues.push(value); } : null;
            this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = MWF.org.parseOrgData(json.data, false, simple); }.bind(this), error, value, false);
            if (vData){
                values.push(vData);
                comboxValues.push({"text": this.getDataText(vData),"value": vData});
            }
        }
        if (type==="object"){
            var vData = MWF.org.parseOrgData(value, false, simple);
            if(vData.woPerson)delete vData.woPerson;
            values.push( vData );
            comboxValues.push({"text": this.getDataText(value),"value": vData});
        }

        var change = false;
        if (oldValues.length && values.length){
            if (oldValues.length === values.length){
                for (var i=0; i<oldValues.length; i++){
                    if ((oldValues[i].distinguishedName!==values[i].distinguishedName) || (oldValues[i].name!==values[i].name) || (oldValues[i].unique!==values[i].unique)){
                        change = true;
                        break;
                    }
                }
            }else{
                change = true;
            }
        }else if (values.length || oldValues.length) {
            change = true;
        }
        this._setBusinessData(values);
        if (change) this.fireEvent("change");

        if (this.json.isInput){
            if (this.combox){
                this.combox.clear();
                this.combox.addNewValues(comboxValues);
            }else{
                var node = this.node.getFirst();
                if (node){
                    node.empty();
                    comboxValues.each(function(v, i){
                        this.creteShowNode(v, (i===comboxValues.length-1)).inject(node);
                    }.bind(this));
                }
            }
            //
            // this.combox.clear();
            // values.each(function(v){
            //     var vtype = typeOf(v);
            //     if (vtype==="string"){
            //         var data;
            //         this.getOrgAction()[this.getValueMethod(v)](function(json){ data = json.data }.bind(this), null, v, false);
            //         if (data) this.combox.addNewValue(this.getDataText(data), data);
            //     }
            //     if (vtype==="object"){
            //         this.combox.addNewValue(this.getDataText(v), v);
            //     }
            // }.bind(this));
        }else{
            if (this.node.getFirst()){
                var node = this.node.getFirst();
                node.empty();
                this.loadOrgWidget(values, node)
            }else{
                this.node.empty();
                this.loadOrgWidget(values, this.node);
            }
        }
    },
    creteShowNode: function(data, islast){
        var nodeText = (data.text) ?  data.text : data;
        if (!islast) nodeText = nodeText + (this.json.splitShow || ", ");

        var node = new Element("div", {
            "styles": {
                "float": "left",
                "margin-right": "5px"
            },
            "text": nodeText
        });
        var text = "";
        if (data.value){
            var flag = data.value.distinguishedName.substr(data.value.distinguishedName.length-2, 2);
            switch (flag.toLowerCase()){
                case "@i":
                    text = data.value.name+"("+data.value.unitName+")";
                    break;
                case "@p":
                    text = data.value.name+"("+data.value.employee+")";
                    break;
                case "@u":
                    text = data.value.levelName;
                    break;
                case "@g":
                    text = data.value.name;
                    break;
                default:
                    text = data.value.name;
            }
            var inforNode = new Element("div").set({
                "styles": {"font-size": "14px", "color": ""},
                "text": text
            });

            new mBox.Tooltip({
                content: inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: node,
                transition: 'flyin'
            });
        }


        return node;
    },
    _setValue: function(value){

        if (value.length==1 && !(value[0])) value=[];
        var values = [];
        var comboxValues = [];
        var type = typeOf(value);

        var simple = this.json.storeRange === "simple";

        if (type==="array"){
            value.each(function(v){
                var data=null;
                var vtype = typeOf(v);
                if (vtype==="string"){
                    var error = (this.json.isInput) ? function(){ comboxValues.push(v); } : null;
                    this.getOrgAction()[this.getValueMethod(v)](function(json){ data = MWF.org.parseOrgData(json.data, true, simple) }.bind(this), error, v, false);
                }
                if (vtype==="object") data = v;
                if (data){
                    values.push(data);
                    comboxValues.push({"text": this.getDataText(data),"value": data});
                }
            }.bind(this));
        }
        if (type==="string"){
            var vData;
            var error = (this.json.isInput) ? function(){ comboxValues.push(value); } : null;
            this.getOrgAction()[this.getValueMethod(value)](function(json){ vData = MWF.org.parseOrgData(json.data, true, simple) }.bind(this), error, value, false);
            if (vData){
                values.push(vData);
                comboxValues.push({"text": this.getDataText(vData),"value": vData});
            }
        }
        if (type==="object"){
            values.push(value);
            comboxValues.push({"text": this.getDataText(value),"value": value});
        }

        this._setBusinessData(values);

        if (this.json.isInput){

            if (this.combox){
                this.combox.clear();
                this.combox.addNewValues(comboxValues);

                // values.each(function(v){
                //     if (typeOf(v)=="string"){
                //         this.combox.addNewValue(v);
                //     }else{
                //         this.combox.addNewValue(this.getDataText(v), v);
                //     }
                // }.bind(this));
            }else{
                var node = this.node.getFirst();
                if (node){
                    node.empty();
                    comboxValues.each(function(v, i){
                        this.creteShowNode(v, (i===comboxValues.length-1)).inject(node);
                    }.bind(this));
                }
            }

        }else{
            if (this.node.getFirst()){
                var node = this.node.getFirst();
                this.loadOrgWidget(values, node)
            }else{
                this.loadOrgWidget(values, this.node);
            }
        }

        //if (this.readonly) this.loadOrgWidget(values, this.node)
        //this.node.set("text", value);
    },
    getValueMethod: function(value){
        if (value){
            var flag = value.substr(value.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    return "getIdentity";
                case "p":
                    return "getPerson";
                case "u":
                    return "getUnit";
                case "g":
                    return "getGroup";
                default:
                    return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
            }
        }
        return (this.json.selectType==="unit") ? "getUnit" : "getIdentity";
    },
    loadOrgWidget: function(value, node){
        var disableInfor = layout.mobile ? true : false;
        if( this.json.showCard === "no" )disableInfor = true;
        var height = node.getStyle("height").toInt();
        if (node.getStyle("overflow")==="visible" && !height) node.setStyle("overflow", "hidden");
        if (value && value.length){
            value.each(function(data){
                var flag = data.distinguishedName.substr(data.distinguishedName.length-2, 2);
                var copyData = Object.clone(data);
                switch (flag.toLowerCase()){
                    case "@i":
                        new MWF.widget.O2Identity(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@p":
                        new MWF.widget.O2Person(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@u":
                        new MWF.widget.O2Unit(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    case "@g":
                        new MWF.widget.O2Group(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                        break;
                    default:
                        new MWF.widget.O2Other(copyData, node, {"style": "xform","lazy":true,"disableInfor" : disableInfor});
                }
            }.bind(this));
        }
    },
    _loadStyles: function(){
        if (this.readonly || this.json.isReadonly){
            if (this.json.styles) this.node.setStyles(this.json.styles);
        }else{
            if (this.json.styles) this.node.setStyles(this.json.styles);
            if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
            if (this.iconNode && this.iconNode.offsetParent !== null ){
                var size = this.node.getSize();
                this.iconNode.setStyle("height", ""+size.y+"px");
            }
        }
    }

}); 
