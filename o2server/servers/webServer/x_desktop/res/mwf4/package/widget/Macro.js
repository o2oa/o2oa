MWF.widget = MWF.widget || {};
MWF.require("MWF.xScript.Environment", null, false);
MWF.widget.Macro = MWF.Macro = {
	"swapSpace": {},
	
	expression: function(code, bind){},
	runEvent: function(code, bind, arg){},
	
	exec: function(code, bind){
		var macroCode = "MWF.Macro.swapSpace.tmpMacroFunction = function (){\n"+code+"\n};";
		Browser.exec(macroCode);
		var returnValue;
		if (!bind) bind = window;
      //  try {
            returnValue = MWF.Macro.swapSpace.tmpMacroFunction.apply(bind);
      //  }catch(e){};
		return returnValue;
	}
};

MWF.Macro.FormContext = new Class({
    macroFunction: null,
    environment: {},

    initialize: function(form){
        var environment = {
            "form": form,
            "forms": form.forms,
            "all": form.all,
            "data": form.businessData.data,
            "work": form.businessData.work,
            "taskList": form.businessData.taskList,
            "control": form.businessData.control,
            "activity": form.businessData.activity,
            "task": form.businessData.task,
            "workLogList": form.businessData.workLogList,
            "attachmentList": form.businessData.attachmentList,
            "status": form.businessData.status,
            "target": null,
            "event": null
        }

        this.environment = new MWF.xScript.Environment(environment);
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
        return MWF.Macro.exec(code, this.environment);
    },
    fire: function(code, target, event){
        this.setTarget(target);
        this.setEvent(event);
        return MWF.Macro.exec(code, this.environment);
    }


});