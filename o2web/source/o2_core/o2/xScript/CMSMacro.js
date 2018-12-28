MWF.xScript = MWF.xScript || {};
MWF.require("MWF.xScript.CMSEnvironment", null, false);
MWF.xScript.CMSMacro = MWF.CMSMacro = {
    "swapSpace": {},

    expression: function(code, bind){},
    runEvent: function(code, bind, arg){},

    exec: function(code, bind){
        var returnValue;
        //     try{
        if (!bind) bind = window;
        try {
            var f = eval("(function(){\n"+code+"\n})");
            returnValue = f.apply(bind);
        }catch(e){}
        //     }catch(e){}


        //	var CMSMacroCode = "MWF.CMSMacro.swapSpace.tmpMacroFunction = function (){"+code+"};";
        //	Browser.exec(macroCode);
        //	var returnValue;
        //	if (!bind) bind = window;
        ////      try {
        //          returnValue = MWF.Macro.swapSpace.tmpMacroFunction.apply(bind);
        ////      }catch(e){};
        return returnValue;
    }
};

MWF.CMSMacro.CMSFormContext = new Class({
    macroFunction: null,
    environment: {},

    initialize: function(form){
        this.form = form;
        var environment = {
            "form": form,
            "forms": form.forms,
            "all": form.all,
            "data": form.businessData.data,
            "document": form.businessData.document,
            "control": form.businessData.control,
            "attachmentList": form.businessData.attachmentList,
            "status": form.businessData.status,
            "formInfor": form.businessData.formInfor,
            "target": null,
            "event": null
        };
        this.environment = new MWF.xScript.CMSEnvironment(environment);
    },
    setTarget: function(target){
        if (target){
            this.environment.target = target;
        }else{
            this.environment.target = null;
        }
    },
    setEvent: function(event){
        if (event){
            this.environment.event = event;
        }else{
            this.environment.event = null;
        }
    },
    exec: function(code, target){
        this.setTarget(target);
        var returnValue = MWF.CMSMacro.exec(code, this.environment);
        //this.form.businessData.data = Object.merge(this.form.businessData.data, this.environment.data);

        return returnValue;
        //this.environment.data

    },
    fire: function(code, target, event){
        this.setTarget(target);
        this.setEvent(event);
        return MWF.CMSMacro.exec(code, this.environment);
    }


});

JSONObject = function(o){

};