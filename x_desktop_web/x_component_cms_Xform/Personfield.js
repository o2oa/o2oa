MWF.xDesktop.requireApp("cms.Xform", "$Input", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.package", null, false);
MWF.xApplication.cms.Xform.Personfield = MWF.CMSPersonfield =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Input,
	iconStyle: "personfieldIcon",

    getDepartments: function(){
        if (this.json.range=="depart"){
            var depart = this.form.CMSMacro.exec(this.json.rangeKey.code, this);
            if (typeOf(depart)=="array"){
                return depart;
            }else{
                var d = depart.toString();
                return (d) ? [d] : [];
            }
        }
        if (this.json.range=="currentdepart"){
            return [this.form.app.document.creatorDepartment];
        }
        return [];
    },
    getCompanys: function(){
        if (this.json.range=="company"){
            var comp = "";
            if (this.json.rangeKey){
                comp = this.form.CMSMacro.exec(this.json.rangeKey.code, this);
            }
            if (typeOf(comp)=="array"){
                return comp;
            }else{
                var c = comp.toString();
                return (c) ? [c] : [];
            }
        }
        if (this.json.range=="currentcompany"){
            return [this.form.app.document.creatorCompany];
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
                this.form.notice(MWF.xApplication.cms.Xform.LP.noSelectRange, "error", this.node);
                return false;
            }
        }
        if (this.json.range=="company"){
            if (!companys.length){
                this.form.notice(MWF.xApplication.cms.Xform.LP.noSelectRange, "error", this.node);
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
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.form.node.getParent(), options);


        //value = MWF.CMSMacro.exec(this.json.defaultValue.code, this.form);
	},
    resetData: function(){
        var v = this.getValue();
        this.setData((v) ? v.join(", ") : "");
    },
    getInputData: function(){
        //if (this.json.count==1){
        //    return this.node.get("value");
        //}
        //debugger;
        var v = this.node.get("value");
        return (v) ? v.split(/,\s*/g) : "";
    }

}); 