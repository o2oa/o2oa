MWF.xScript = MWF.xScript || {};
MWF.xScript.Macro = MWF.Macro = {
    "swapSpace": {},
    "scriptSpace": {},

    expression: function(code, bind){},
    runEvent: function(code, bind, arg){},

    exec: function(code, bind){
        var returnValue;
        //try{
        if (!bind) bind = window;
        //this.bind = bind || window;

        var n = 0;
        var o = "f"+"_"+n;
        while (MWF.Macro.scriptSpace[o]){ n++; o = "f"+"_"+n; }
        //MWF.Macro.scriptSpace[o] = bind;

        if (o2.session.isDebugger){
            var f = "MWF.Macro.scriptSpace[\""+o+"\"] = function(){\n"+code+"\n}";
            Browser.exec(f);
            returnValue = (o2.Macro.scriptSpace[o]) ? o2.Macro.scriptSpace[o].apply(bind) : null;
        }else{
            var f = "MWF.Macro.scriptSpace[\""+o+"\"] = function(){try{\n"+code+"\n}catch(e){console.error(e)}}";
            Browser.exec(f);
            returnValue = (o2.Macro.scriptSpace[o]) ? o2.Macro.scriptSpace[o].apply(bind) : null;
        }
        o2.Macro.scriptSpace[o] = null;

        // if (o2.session.isDebugger){
        //     this.run(code, bind)
        // }else{
        //     try {
        //         var n = 0;
        //         var o = "o"+"_"+n;
        //         while (MWF.Macro.swapSpace[o]){
        //             n++;
        //             o = "o"+"_"+n;
        //         }
        //         MWF.Macro.swapSpace[o] = bind;
        //         var f = "try {(function(){\n"+code+"\n}).apply(MWF.Macro.swapSpace[\""+o+"\"])} catch(e){console.log(e);}";
        //         Browser.exec(f);
        //         o2.Macro.swapSpace[o] = null;
        //     }catch(e){
        //         console.log(o2.LP.script.error);
        //         if (code.length>500){
        //             var t = code.substr(0,500)+"\n...\n";
        //             console.log(t);
        //         }else{
        //             console.log(code);
        //         }
        //
        //         console.log(e);
        //         //throw e;
        //     }
        // }
        return returnValue;
    }
};
//try {


// var f = eval("(function(){return function(){\n"+code+"\n}})();");
// returnValue = f.apply(bind);
// }catch(e){
//     console.log(o2.LP.script.error);
//     if (code.length>500){
//         var t = code.substr(0,500)+"\n...\n";
//         console.log(t);
//     }else{
//         console.log(code);
//     }
//
//     console.log(e);
//     debugger;
//     throw e;
// }
MWF.Macro

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
        MWF.require("MWF.xScript.Environment", null, false);
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
        MWF.require("MWF.xScript.PageEnvironment", null, false);
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

if( !MWF.Macro.ViewContext ) {
    MWF.Macro.ViewContext = new Class({
        macroFunction: null,
        environment: {},
        initialize: function (view) {
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
        setTarget: function (target) {
            if (target) {
                this.environment.target = target;
            } else {
                this.environment.target = null;
            }
        },
        setEvent: function (event) {
            if (event) {
                this.environment.event = event;
            } else {
                this.environment.event = null;
            }
        },
        exec: function (code, target) {
            this.setTarget(target);
            var returnValue = MWF.Macro.exec(code, this.environment);
            //this.form.businessData.data = Object.merge(this.form.businessData.data, this.environment.data);

            return returnValue;
            //this.environment.data

        },
        fire: function (code, target, event) {
            this.setTarget(target);
            this.setEvent(event);
            return MWF.Macro.exec(code, this.environment);
        }
    });
}

JSONObject = function(o){
};
