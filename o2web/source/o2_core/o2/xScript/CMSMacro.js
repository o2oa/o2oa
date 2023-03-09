MWF.xScript = MWF.xScript || {};
MWF.xScript.CMSMacro = MWF.CMSMacro = {
    "swapSpace": {},
    "scriptSpace": {},

    expression: function(code, bind){},
    runEvent: function(code, bind, arg){},

    exec: function(code, bind){
        var returnValue;
        if (!bind) bind = window;

        var n = 0;
        var o = "f"+"_"+n;
        while (MWF.CMSMacro.scriptSpace[o]){ n++; o = "f"+"_"+n; }

        if (o2.session.isDebugger){
            var f = "MWF.CMSMacro.scriptSpace[\""+o+"\"] = function(){\n"+code+"\n}";
            Browser.exec(f);
            returnValue = (o2.CMSMacro.scriptSpace[o]) ? o2.CMSMacro.scriptSpace[o].apply(bind) : null;
        }else{
            var f = "MWF.CMSMacro.scriptSpace[\""+o+"\"] = function(){try{\n"+code+"\n}catch(e){console.error(e)}}";
            Browser.exec(f);
            returnValue = (o2.CMSMacro.scriptSpace[o]) ? o2.CMSMacro.scriptSpace[o].apply(bind) : null;
        }
        o2.CMSMacro.scriptSpace[o] = null;
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
        MWF.require("MWF.xScript.CMSEnvironment", null, false);
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

if( !MWF.CMSMacro.ViewContext ){
    MWF.CMSMacro.ViewContext = new Class({
        macroFunction: null,
        environment: {},
        initialize: function(view){
            this.form = view;
            var environment = {
                "view": view,
                "viewInfor": view.viewInfor,
                "target": null,
                "event": null
            };
            MWF.require("MWF.xScript.ViewEnvironment", null, false);
            this.environment = new MWF.xScript.ViewEnvironment(environment);
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
}


JSONObject = function(o){

};