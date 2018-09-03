MWF.xApplication.process.Work.options.multitask = true;
MWF.xApplication.process.Work.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "process.Work",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"title": MWF.xApplication.process.Work.LP.title,
        "workId": "",
        "workCompletedId": "",
        "taskId": "",
        "isControl": false,
        "taskObject": null,
        "readonly": false
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.Work.LP;
        if (this.status){
            this.options.workId = this.status.workId;
            this.options.workCompletedId = this.status.workCompletedId;
            this.options.readonly = (this.status.readonly=="true") ? true : false;
        }
	},
	loadApplication: function(callback){
        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);

        MWF.require("MWF.widget.Mask", function(){
            this.mask = new MWF.widget.Mask({"style": "desktop"});

            this.formNode = new Element("div", {"styles": {"min-height": "100%", "font-size": "14px"}}).inject(this.node);
            this.action = MWF.Actions.get("x_processplatform_assemble_surface");
            //MWF.xDesktop.requireApp("process.Work", "Actions.RestActions", function(){
            //    this.action = new MWF.xApplication.process.Work.Actions.RestActions();
                if (!this.options.isRefresh){
                    this.maxSize(function(){
                        this.mask.loadNode(this.content);
                        this.loadWork();
                    }.bind(this));
                }else{
                    this.mask.loadNode(this.content);
                    this.loadWork();
                }
                if (callback) callback();
            //}.bind(this));

        }.bind(this));

        this.addEvent("queryClose", function(){
            this.refreshTaskCenter();
        }.bind(this));

        this.addKeyboardEvents();

    },
    refreshTaskCenter: function(){
        if (this.desktop.apps["TaskCenter"]){
            this.desktop.apps["TaskCenter"].content.unmask();
            this.desktop.apps["TaskCenter"].refreshAll();
        }
    },
    addKeyboardEvents: function(){
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
    },
    keySave: function(e){
        if (this.appForm){
            if (!this.options.readonly){
                this.appForm.saveWork();
                e.preventDefault();
            }
        }
    },
    reload: function(data){
        if (this.form){
            this.formNode.empty();
            MWF.release(this.form);
            this.form = null;
        }
        if (data){
            this.parseData(data);
            this.openWork();
        }else{
            this.loadWork();
        }

    },
    loadWork: function(){
        var method = "";
        var id = "";

        if (this.options.taskId){
            method = "getJobByTask";
            id = this.options.taskId;
        }else if (this.options.workCompletedId){
            method = "getJobByWorkCompleted";
            id = this.options.workCompletedId;
        }else if (this.options.workId) {
            method = "getJobByWork";
            id = this.options.workId;
        }
        if (method && id){
            this.action[method](function(json){
                if (this.mask) this.mask.hide();
                this.parseData(json.data);
                this.openWork();
            }.bind(this), function(){
                this.close();
            }.bind(this), id);
        }
    },
    errorWork: function(){
        if (this.mask) this.mask.hide();
        this.node.set("text", "openError");
    },
    getCurrentTaskData: function(data){
        if ((data.currentTaskIndex || data.currentTaskIndex===0) && data.currentTaskIndex != -1){
            this.options.taskId = this.taskList[data.currentTaskIndex].id;
            return this.taskList[data.currentTaskIndex];
        }
        //if (this.taskList){
        //    if (this.taskList.length==1){
        //        this.options.taskId = this.taskList[0].id;
        //        return this.taskList[0];
        //    }
        //}
        return null;
    },
    parseData: function(data){
        var title = "";
        if (this.options.taskId){
            title = data.work.title;
            this.options.workId = data.work.id;
        }else if (this.options.workCompletedId){
            title = data.workCompleted.title;
            this.options.workCompleted = data.workCompleted.id;
        }else if (this.options.workId) {
            title = data.work.title;
            this.options.workId = data.work.id;
        }

        this.setTitle(this.options.title+"-"+title);

        this.activity = data.activity;
        this.data = data.data;
        this.taskList = data.taskList;
        this.currentTask = this.getCurrentTaskData(data);
        this.taskList = data.taskList;
        this.readList = data.readList;
        this.work = data.work;
        this.workCompleted = data.workCompleted;
        this.workLogList = data.workLogList;
        this.attachmentList = data.attachmentList;
        this.inheritedAttachmentList = data.inheritedAttachmentList;
        this.control = data.control;
        this.form = (data.form) ? JSON.decode(MWF.decodeJsonString(data.form.data)): null;
    },
    openWork: function(){
        if (this.form){
            //this.readonly = true;
            //if (this.currentTask) {
            //    this.readonly = false;
            //}else if(this.options.isControl && this.work){
            //    this.readonly = false;
            //}
            MWF.xDesktop.requireApp("process.Xform", "Form", function(){
                this.appForm = new MWF.APPForm(this.formNode, this.form, {});
                this.appForm.businessData = {
                    "data": this.data,
                    "taskList": this.taskList,
                    "readList": this.readList,
                    "work": this.work,
                    "workCompleted": this.workCompleted,
                    "control": this.control,
                    "activity": this.activity,
                    "task": this.currentTask,
                    "workLogList": this.workLogList,
                    "attachmentList": this.attachmentList,
                    "status": {
                        //"readonly": (this.options.readonly) ? true : false
                        "readonly": this.readonly
                    }
                };
                this.appForm.workAction = this.action;
                this.appForm.app = this;
                this.appForm.load();
            }.bind(this));
        }
    },

    //errorWork: function(){
    //
    //},

    recordStatus: function(){
        return {"workId": this.options.workId, "workCompletedId": this.options.workCompletedId, "readonly": this.readonly};
    },
    onPostClose: function(){
        if (this.appForm){
            this.appForm.modules.each(function(module){
                MWF.release(module);
            });
            MWF.release(this.appForm);
        }
    }

});