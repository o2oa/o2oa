MWF.xScript = MWF.xScript || {};
MWF.require("MWF.xScript.Environment", null, false);
MWF.require("MWF.xScript.PageEnvironment", null, false);
MWF.xScript.Macro = MWF.Macro = {
	"swapSpace": {},
	
	expression: function(code, bind){},
	runEvent: function(code, bind, arg){},
	
	exec: function(code, bind){
        var returnValue;
        //try{
            if (!bind) bind = window;
            if (o2.session.isDebugger){
                try {
                    var f = eval("(function(){return function(){\n"+code+"\n}})();");
                    returnValue = f.apply(bind);
                }catch(e){
                    console.log(o2.LP.script.error);
                    if (code.length>500){
                        var t = code.substr(0,500)+"\n...\n";
                        console.log(t);
                    }else{
                        console.log(code);
                    }

                    console.log(e);
                    //throw e;
                }
            }else{
                try {
                    var f = eval("(function(){return function(){\n"+code+"\n}})();");
                    returnValue = f.apply(bind);
                }catch(e){
                    console.log(o2.LP.script.error);
                    if (code.length>500){
                        var t = code.substr(0,500)+"\n...\n";
                        console.log(t);
                    }else{
                        console.log(code);
                    }

                    console.log(e);
                    //throw e;
                }
            }

        //}catch(e){}//


	//	var macroCode = "MWF.Macro.swapSpace.tmpMacroFunction = function (){"+code+"};";
	//	Browser.exec(macroCode);
	//	var returnValue;
	//	if (!bind) bind = window;
  ////      try {
  //          returnValue = MWF.Macro.swapSpace.tmpMacroFunction.apply(bind);
  ////      }catch(e){};
		return returnValue;
	}
};

MWF.Macro.FormContext = new Class({
    macroFunction: null,
    environment: {},

    initialize: function(form){
        this.form = form;
        var environment = {
            "form": form,
            "forms": form.forms,
            "all": form.all,
            "data": form.businessData.data,
            "work": form.businessData.work,
            "workCompleted": form.businessData.workCompleted,
            "taskList": form.businessData.taskList,
            "readList": form.businessData.readList,
            "control": form.businessData.control,
            "activity": form.businessData.activity,
            "task": form.businessData.task,
            "taskCompletedList": form.businessData.taskCompletedList,
            "workLogList": form.businessData.workLogList,
            "recordList": form.businessData.recordList,
            "attachmentList": form.businessData.attachmentList,
            "inheritedAttachmentList": form.businessData.inheritedAttachmentList,
            "formInfor": form.businessData.formInfor,
            "status": form.businessData.status,
            "target": null,
            "event": null
        };
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
        var returnValue = MWF.Macro.exec(code, this.environment);
        //this.form.businessData.data = Object.merge(this.form.businessData.data, this.environment.data);

        return returnValue;
        //this.environment.data

    },
    fire: function(code, target, event){
        this.setTarget(target);
        this.setEvent(event);
        return MWF.Macro.exec(code, this.environment);
    }
});
MWF.Macro.PageContext = new Class({
    macroFunction: null,
    environment: {},
    initialize: function(page){
        this.form = page;
        var environment = {
            "form": page,
            "forms": page.forms,
            "all": page.all,
            "data": page.businessData.data,
            "status": page.businessData.status,
            "pageInfor": page.businessData.pageInfor,
            "target": null,
            "event": null
        };
        this.environment = new MWF.xScript.PageEnvironment(environment);
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
        var returnValue = MWF.Macro.exec(code, this.environment);
        //this.form.businessData.data = Object.merge(this.form.businessData.data, this.environment.data);

        return returnValue;
        //this.environment.data

    },
    fire: function(code, target, event){
        this.setTarget(target);
        this.setEvent(event);
        return MWF.Macro.exec(code, this.environment);
    }
});

JSONObject = function(o){
};
