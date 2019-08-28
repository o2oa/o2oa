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
        if (!this.status) {
        } else {
            this.options.workId = this.status.workId;
            this.options.workCompletedId = this.status.workCompletedId;
            this.options.readonly = (this.status.readonly === "true");
        }
        this.action = MWF.Actions.get("x_processplatform_assemble_surface");
	},
    loadWorkApplication: function(callback, mask){
        if (mask) this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.formNode = new Element("div", {"styles": this.css.formNode}).inject(this.node);
        if (!this.options.isRefresh){
            this.maxSize(function(){
                if (mask) this.mask.loadNode(this.content);
                this.loadWork();
            }.bind(this));
        }else{
            if (mask) this.mask.loadNode(this.content);
            this.loadWork();
        }
        if (callback) callback();
    },
	loadApplication: function(callback){
        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);

        if (layout.mobile){
            this.loadWorkApplication(callback, false)
        }else{
            MWF.require("MWF.widget.Mask", function(){
                this.loadWorkApplication(callback, true)
            }.bind(this));
        }

        this.addEvent("postClose", function(){
            //this.refreshTaskCenter();
        }.bind(this));

        this.addKeyboardEvents();

    },
    refreshTaskCenter: function(){
	    if (this.desktop.apps){
            if (this.desktop.apps["TaskCenter"]){
                this.desktop.apps["TaskCenter"].content.unmask();
                this.desktop.apps["TaskCenter"].refreshAll();
            }
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
        // var method = "";
        var id = this.options.workCompletedId || this.options.workId || this.options.workid || this.options.workcompletedid;
        // var methods = {
        //     "loadWork": false,
        //     "getWorkControl": false,
        //     "getForm": false
        // };
        debugger;
        if (id){
            o2.Actions.invokeAsync([
                {"action": this.action, "name": (layout.mobile) ? "getWorkFormMobile": "getWorkForm"},
                {"action": this.action, "name": "loadWork"},
                {"action": this.action, "name": "getWorkControl"},
                {"action": this.action, "name": "getWorkLog"},
                {"action": this.action, "name": "listAttachments"}
            ], {"success": function(json_form, json_work, json_control, json_log, json_att){
                if (json_work && json_control && json_form && json_log && json_att){
                    this.parseData(json_work.data, json_control.data, json_form.data, json_log.data, json_att.data);
                    if (this.mask) this.mask.hide();
                    //if (layout.mobile) this.loadMobileActions();
                    this.openWork();
                } else{
                    this.close();
                }
            }.bind(this), "failure": function(){}}, id);
            //}.bind(this), "failure": function(){}}, [id, true, true, true], id);
        }
    },
    parseData: function(workData, controlData, formData, logData, attData){
        debugger;
        var title = workData.work.title;
        this.setTitle(this.options.title+"-"+title);

        this.activity = workData.activity;
        this.data = workData.data;
        this.taskList = workData.taskList;
        this.currentTask = this.getCurrentTaskData(workData);
        this.taskList = workData.taskList;
        this.readList = workData.readList;
        this.work = workData.work;
        this.workCompleted = (workData.work.completedTime) ? workData.work : null;

        this.workLogList = logData;
        this.attachmentList = attData;
        //this.inheritedAttachmentList = data.inheritedAttachmentList;

        this.control = controlData;
        this.form = (formData.data) ? JSON.decode(MWF.decodeJsonString(formData.data)): null;
        delete formData.data;
        this.formInfor = formData;
    },

    // loadWork2: function(){
    //     var method = "";
    //     var id = "";
    //
    //     if (this.options.workCompletedId){
    //         method = (layout.mobile) ? "getJobByWorkCompletedMobile" : "getJobByWorkCompleted";
    //         id = this.options.workCompletedId;
    //     }else if (this.options.workId) {
    //         method = (layout.mobile) ? "getJobByWorkMobile" : "getJobByWork";
    //         id = this.options.workId;
    //     }
    //     if (method && id){
    //         this.action[method](function(json){
    //             if (this.mask) this.mask.hide();
    //             this.parseData(json.data);
    //             if (layout.mobile) this.loadMobileActions();
    //             this.openWork();
    //         }.bind(this), function(){
    //             this.close();
    //         }.bind(this), id);
    //     }
    // },
    loadMobileActions: function(){
        if( this.control.allowSave || this.control.allowProcessing ){
            this.mobileActionBarNode = new Element("div", {"styles": this.css.mobileActionBarNode}).inject(this.node, "after");
            var size = this.content.getSize();
            var y = size.y-40;
            this.node.setStyles({
                "height": ""+y+"px",
                "min-height": ""+y+"px",
                "overflow": "auto",
                "padding-bottom": "40px"
            });
            //this.node.set("id", "formNode111111111");
        }
        if( this.control.allowSave ){
            this.mobileSaveActionNode = new Element("div", {"styles": this.css.mobileSaveActionNode, "text": this.lp.save}).inject(this.mobileActionBarNode);
            this.mobileSaveActionNode.addEvents({
                "click": function(){
                    this.appForm.saveWork();
                }.bind(this),
                "touchstart": function(){
                    this.setStyle("background-color", "#EEEEEE");
                },
                "touchcancel": function(){
                    this.setStyle("background-color", "#ffffff");
                },
                "touchend": function(){
                    this.setStyle("background-color", "#ffffff");
                }
            });
            if (this.control.allowProcessing){
                this.mobileSaveActionNode.setStyles({
                    "width": "49%",
                    "float": "left"
                });
            }
        }
        if( this.control.allowProcessing ){
            this.mobileProcessActionNode = new Element("div", {"styles": this.css.mobileSaveActionNode, "text": this.lp.process}).inject(this.mobileActionBarNode);
            this.mobileProcessActionNode.addEvents({
                "click": function(){
                    this.appForm.processWork();
                }.bind(this),
                "touchstart": function(){
                    this.setStyle("background-color", "#EEEEEE");
                },
                "touchcancel": function(){
                    this.setStyle("background-color", "#ffffff");
                },
                "touchend": function(){
                    this.setStyle("background-color", "#ffffff");
                }
            });
            if (this.control.allowSave){
                this.mobileProcessActionNode.setStyles({
                    "width": "49%",
                    "float": "right"
                });
            }
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
    // parseData: function(data){
    //     var title = "";
    //     if (this.options.taskId){
    //         title = data.work.title;
    //         this.options.workId = data.work.id;
    //     }else if (this.options.workCompletedId){
    //         title = data.workCompleted.title;
    //         this.options.workCompleted = data.workCompleted.id;
    //     }else if (this.options.workId) {
    //         title = data.work.title;
    //         this.options.workId = data.work.id;
    //     }
    //
    //     this.setTitle(this.options.title+"-"+title);
    //
    //     this.activity = data.activity;
    //     this.data = data.data;
    //     this.taskList = data.taskList;
    //     this.currentTask = this.getCurrentTaskData(data);
    //     this.taskList = data.taskList;
    //     this.readList = data.readList;
    //     this.work = data.work;
    //     this.workCompleted = data.workCompleted;
    //     this.workLogList = data.workLogList;
    //     this.attachmentList = data.attachmentList;
    //     this.inheritedAttachmentList = data.inheritedAttachmentList;
    //     this.control = data.control;
    //     this.form = (data.form) ? JSON.decode(MWF.decodeJsonString(data.form.data)): null;
    //     this.formInfor = data.form;
    // },
    openWork: function(){
        if (this.form){
            //this.readonly = true;
            //if (this.currentTask) {
            //    this.readonly = false;
            //}else if(this.options.isControl && this.work){
            //    this.readonly = false;
            //}

            // MWF.xDesktop.requireApp("process.Xform", "Package", function(){
            //     MWF.xApplication.process.Xform.require(function(){
            //         this.appForm = new MWF.APPForm(this.formNode, this.form, {});
            //         this.appForm.businessData = {
            //             "data": this.data,
            //             "taskList": this.taskList,
            //             "readList": this.readList,
            //             "work": this.work,
            //             "workCompleted": this.workCompleted,
            //             "control": this.control,
            //             "activity": this.activity,
            //             "task": this.currentTask,
            //             "workLogList": this.workLogList,
            //             "attachmentList": this.attachmentList,
            //             "inheritedAttachmentList": this.inheritedAttachmentList,
            //             "formInfor": this.formInfor,
            //             "status": {
            //                 //"readonly": (this.options.readonly) ? true : false
            //                 "readonly": this.readonly
            //             }
            //         };
            //         this.appForm.workAction = this.action;
            //         this.appForm.app = this;
            //         this.appForm.load();
            //     }.bind(this));
            // }.bind(this));


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
                    "inheritedAttachmentList": this.inheritedAttachmentList,
                    "formInfor": this.formInfor,
                    "status": {
                        //"readonly": (this.options.readonly) ? true : false
                        "readonly": this.readonly
                    }
                };
                this.appForm.workAction = this.action;
                this.appForm.app = this;
                debugger;
                this.appForm.load(function(){
                    if (window.o2android && window.o2android.appFormLoaded){
                        layout.appForm = this.appForm;
                        window.o2android.appFormLoaded(JSON.stringify(this.appForm.mobileTools));
                    }
                    if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.appFormLoaded){
                        layout.appForm = this.appForm;
                        window.webkit.messageHandlers.appFormLoaded.postMessage(JSON.stringify(this.appForm.mobileTools));
                    }
                }.bind(this));
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