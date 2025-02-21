MWF.xApplication.Empty.options.multitask = true;
MWF.xApplication.Empty.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style1": "default",
        "style": "default",
        "name": "Empty",
        "mvcStyle": "style.css",
        "icon": "icon.png",
        "title": MWF.xApplication.Empty.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Empty.LP;
    },
    loadApplication: function(callback){
        var url = this.path+this.options.style+"/view.html";
        this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
            this.loadTaskView();
        }.bind(this));
    },
    loadTaskView: function(){
        o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,5, function(json){
            debugger;
            this.taskListView.loadHtml(this.path+this.options.style+"/taskView.html", {"bind": {"lp": this.lp, "data": json.data}, "module": this}, function(){
                this.doSomething();
            }.bind(this));
        }.bind(this));
    },
    doSomething: function(){

    },
    openTask: function(e, data, id){
        layout.openApplication(null, "process.Work", {"workid": id});
    },
    openCalendar: function(){
        layout.openApplication(null, "Calendar");
    },
    openOrganization: function(){
        layout.openApplication(null, "Org");
    },
    openInBrowser: function() {
        this.openInNewBrowser(true);
    },
    startProcess: function(){
        o2.api.page.startProcess();
        // const cmpt = this;
        // o2.requireApp([["process.TaskCenter", "lp."+o2.language], ["process.TaskCenter", ""]],"", ()=>{
        // 	var obj = {
        // 		"lp": o2.xApplication.process.TaskCenter.LP,
        // 		"content": cmpt.content,
        // 		"addEvent": function(type, fun){
        // 			cmpt.addEvent(type, fun);
        // 		},
        // 		"getAction": function (callback) {
        // 			if (!this.action) {
        // 				this.action = o2.Actions.get("x_processplatform_assemble_surface");
        // 				if (callback) callback();
        // 			} else {
        // 				if (callback) callback();
        // 			}
        // 		},
        // 		"desktop": layout.desktop,
        // 		"refreshAll": function(){},
        // 		"notice": cmpt.notice,
        // 	}
        // 	o2.JSON.get("../x_component_process_TaskCenter/$Main/default/css.wcss", function(data){
        // 		obj.css = data;
        // 	}, false);
        //
        // 	if (!cmpt.processStarter) cmpt.processStarter = new o2.xApplication.process.TaskCenter.Starter(obj);
        // 	cmpt.processStarter.load();
        // }, true, true);
    }
});
