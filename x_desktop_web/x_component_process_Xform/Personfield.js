MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.process.Xform.Personfield = MWF.APPPersonfield =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "personfieldIcon",

    loadDescription: function(){
        var v = this._getBusinessData();
        if (!v || !v.length){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-3;
                if (COMMON.Browser.safari) w = w-20;
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
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect();
                }.bind(this)
            });
        }
    },
    getRange: function(){
        var rangeValues = [];
        if (this.json.rangeField && this.json.rangeField.length){
            this.json.rangeField.each(function(field){
                var v = this.form.businessData.data[field];
                if (typeOf(v)!="array") v = (v) ? [v.toString()] : [];
                v.each(function(d){
                    if (d) rangeValues.push(d);
                });
            }.bind(this));
        }
        if (this.json.rangeKey && this.json.rangeKey.code){
            var v = this.form.Macro.exec(this.json.rangeKey.code, this);
            if (typeOf(v)!="array") v = (v) ? [v.toString()] : [];
            v.each(function(d){
                if (d) rangeValues.push(d);
            });
        }
        return rangeValues;
    },
    _computeValue: function(value){
        var values = [];
        if (this.json.identityValue) {
            this.json.identityValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.departmentValue) {
            this.json.departmentValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.companyValue) {
            this.json.companyValue.each(function(v){ if (v) values.push(v)});
        }
        if (this.json.dutyValue) {
            debugger;
            var dutys = JSON.decode(this.json.dutyValue);
            var par;
            if (dutys.length){
                dutys.each(function(duty){
                    if (duty.code) par = this.form.Macro.exec(duty.code, this);
                    var code = "return this.org.getDepartmentDuty({\"name\": \""+duty.name+"\", \"departmentName\": \""+par+"\"})";
                    var d = this.form.Macro.exec(code, this);
                    if (typeOf(d)!="array") d = (d) ? [d.toString()] : [];
                    d.each(function(dd){values.push(dd);});

                    code = "return this.org.getCompanyDuty({\"name\": \""+duty.name+"\", \"compName\": \""+par+"\"})";
                    d = this.form.Macro.exec(code, this);
                    if (typeOf(d)!="array") d = (d) ? [d.toString()] : [];
                    d.each(function(dd){values.push(dd);});
                }.bind(this));
            }
        }
        if (this.json.defaultValue && this.json.defaultValue.code){
            var fd = this.form.Macro.exec(this.json.defaultValue.code, this);
            if (typeOf(fd)!="array") fd = (fd) ? [fd.toString()] : [];
            fd.each(function(fdd){values.push(fdd);});
        }
        if (this.json.count>0){
            return values.slice(0, this.json.count);
        }
        return values;
        //return (this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
    getDepartments: function(){
        if (this.json.range=="depart"){
            return this.getRange();
        }
        if (this.json.range=="currentdepart"){
            return [this.form.app.currentTask.department];
        }
        return [];
    },
    getCompanys: function(){
        if (this.json.range=="company"){
            var comp = "";
            //if (this.json.rangeKey){
                return this.getRange();
            //}
        }
        if (this.json.range=="currentcompany"){
            return [this.form.app.currentTask.company];
        }
        return [];
    },
	clickSelect: function(){
        var nameValue = this.getInputData();
        //var names = (nameValue) ? this.getInputData().split(MWF.splitStr) : [];
        var names = nameValue;
        var count = (this.json.count) ? this.json.count : 0;
        var companys = this.getCompanys();
        var departments = this.getDepartments();

        if (this.json.range=="depart"){
            if (!departments.length){
                this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
                return false;
            }
        }
        if (this.json.range=="company"){
            if (!companys.length){
                this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
                return false;
            }
        }

        var options = {
            "type": this.json.selectType,
            "names": names,
            "count": count,
            "departments": departments,
            "companys": companys,
            "onComplete": function(items){
                var values = [];
                items.each(function(item){
                    values.push(item.data.name);
                }.bind(this));
                this.setData(values.join(", "));

                this._setBusinessData(this.getInputData());
                this.validationMode();
                this.validation()
            }.bind(this),
            "onCancel": function(){
                this.validation();
            }.bind(this),
            "onLoad": function(){
                if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
            }.bind(this),
            "onClose": function(){
                var v = this.node.getFirst().get("value");
                if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");

            }.bind(this)
        };

        //if (layout.mobile) options.style = "mobile";

        //var selector = new MWF.OrgSelector(this.form.node.getParent(), options);
        var selector = new MWF.OrgSelector(this.form.app.content, options);


        //value = MWF.Macro.exec(this.json.defaultValue.code, this.form);
	},
    resetData: function(){
        var v = this.getValue();
        this.setData((v) ? v.join(", ") : "");
    },
    getInputData: function(){
        //if (this.json.count==1){
        //    return this.node.get("value");
        //}
        debugger;
        var v = this.node.getElement("input").get("value");
        return (v) ? v.split(/,\s*/g) : "";
    }

}); 