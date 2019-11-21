MWF.xDesktop.requireApp("process.Xform", "Subform", null, false);
MWF.xApplication.cms.Xform.Subform = MWF.CMSSubform =  new Class({
    Extends: MWF.APPSubform,

    getSubform: function(callback){
        if (this.json.subformType==="script"){
            if (this.json.subformScript.code){
                var data = this.form.Macro.exec(this.json.subformScript.code, this);
                if (data){
                    var formName, app;
                    if( typeOf( data ) === "string" ){
                        formName = data;
                    }else{
                        if( data.application )app = data.application;
                        if( data.subform )formName = data.subform;
                    }
                    if( formName ){
                        if( !app )app = this.form.businessData.document.appId || this.form.businessData.document.application;
                        MWF.Actions.get("x_cms_assemble_control").getFormWithColumn(formName, app, function(json){
                            this.getSubformData(json.data);
                            if (callback) callback();
                        }.bind(this));
                    }else{
                        if (callback) callback();
                    }
                }else{
                    if (callback) callback();
                }
            }
        }else{
            if (this.json.subformSelected && this.json.subformSelected!=="none"){
                var app;
                if( this.json.subformAppSelected ){
                    app = this.json.subformAppSelected;
                }else{
                    app = this.form.businessData.document.appId || this.form.businessData.document.application;
                }
                MWF.Actions.get("x_cms_assemble_control").getFormWithColumn(this.json.subformSelected, app, function(json){
                    this.getSubformData(json.data);
                    if (callback) callback();
                }.bind(this));
            }else{
                if (callback) callback();
            }
        }

        //if (this.json.subformType==="script"){
        //    if (this.json.subformScript.code){
        //        var formNome = this.form.Macro.exec(this.json.subformScript.code, this);
        //        if (formNome){
        //            MWF.Actions.get("x_cms_assemble_control").getForm(formNome, function(json){
        //                this.getSubformData(json.data);
        //                if (callback) callback();
        //            }.bind(this));
        //        }
        //    }
        //}else{
        //    if (this.json.subformSelected && this.json.subformSelected!=="none"){
        //        MWF.Actions.get("x_cms_assemble_control").getForm(this.json.subformSelected, function(json){
        //            this.getSubformData(json.data);
        //            if (callback) callback();
        //        }.bind(this));
        //    }else{
        //        if (callback) callback();
        //    }
        //}
    }
});