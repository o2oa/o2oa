MWF.xDesktop.requireApp("process.Xform", "Subform", null, false);
MWF.xApplication.cms.Xform.Subform = MWF.CMSSubform =  new Class({
    Extends: MWF.APPSubform,

    getSubform: function(callback){
        if (this.json.subformType==="script"){
            if (this.json.subformScript.code){
                var formNome = this.form.Macro.exec(this.json.subformScript.code, this);
                if (formNome){
                    MWF.Actions.get("x_cms_assemble_control").getForm(formNome, function(json){
                        this.getSubformData(json.data);
                        if (callback) callback();
                    }.bind(this));
                }
            }
        }else{
            if (this.json.subformSelected && this.json.subformSelected!=="none"){
                MWF.Actions.get("x_cms_assemble_control").getForm(this.json.subformSelected, function(json){
                    this.getSubformData(json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                if (callback) callback();
            }
        }
    }
});