MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.options = Object.clone(o2.xApplication.Common.options);
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
		"title": "",
        "workId": "",
        "draftId": "",
        "draft": null,
        "workCompletedId": "",
        "taskId": "",
        "taskCompletedId":"",
        "jobId": "",
        "form": null,
        "priorityWork": "",
        "isControl": false,
        "taskObject": null,
        "readonly": false,
        "worklogType": "record" //record, worklog
	},
	onQueryLoad: function(){
        if (!this.options.title && !layout.mobile) this.setOptions({
            "title": MWF.xApplication.process.Work.LP.title
        });
		this.lp = MWF.xApplication.process.Work.LP;
        if (!this.status) {
            if( this.options.readonly === "true" )this.options.readonly=true;
        } else {
            this.options.workId = this.status.workId;
            this.options.taskId = this.status.taskId;
            this.options.taskCompletedId = this.status.taskCompletedId;
            this.options.workCompletedId = this.status.workCompletedId;
            this.options.jobId = this.status.jobId;
            this.options.draftId = this.status.draftId;
            this.options.priorityWork = this.status.priorityWork;
            this.options.formid = this.status.formid;
            if( this.status.form && this.status.form.id )this.options.form = this.status.form;
            this.options.readonly = (this.status.readonly === true || this.status.readonly === "true");
        }
        this.action = MWF.Actions.get("x_processplatform_assemble_surface");
	},
    loadWorkApplication: function(callback, mask){
        var maskStyle = (Browser.name=="firefox") ? "work_firefox" : "desktop";
        //alert(maskStyle);
        if (mask) this.mask = new MWF.widget.Mask({"style": maskStyle, "loading": mask});
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
            if (layout.viewMode=="Default"){
                MWF.require("MWF.widget.Mask", function(){
                    this.loadWorkApplication(callback, true);
                }.bind(this));
            }else{
                this.loadWorkApplication(callback, false);
            }

        }

        this.addEvent("postClose", function(){
            //this.refreshTaskCenter();
        }.bind(this));

        this.addKeyboardEvents();

    },
    refreshTaskCenter: function(){
	    if (this.desktop.apps){
            if (this.desktop.apps["TaskCenter"]){
                if(this.desktop.apps["TaskCenter"].content){
                    this.desktop.apps["TaskCenter"].content.unmask();
                }
                if(this.desktop.apps["TaskCenter"].refreshAll){
                    this.desktop.apps["TaskCenter"].refreshAll();
                }
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
        if (this.appForm){
            this.formNode.empty();
            MWF.release(this.appForm);
            this.appForm = null;
            this.form = null;
            this.$events = {};
        }
        if (data){
            this.parseData(data).then(function(){
                this.openWork();
            }.bind(this));
        }else{
            this.loadWork();
        }
    },
    loadWork: function(){
        var id = this.options.workCompletedId || this.options.workId || this.options.workid || this.options.workcompletedid;
        // var methods = {
        //     "loadWork": false,
        //     "getWorkControl": false,
        //     "getForm": false
        // };
        if (id){
            this.loadWorkByWork(id);
           // }.bind(this), "failure": function(){}}, [id, true, true, true], id);
        }else if (this.options.draftId || this.options.draftid){
            var draftId = this.options.draftId || this.options.draftid;
            MWF.Actions.get("x_processplatform_assemble_surface").getDraft(draftId, function(json){
                this.loadWorkByDraft(json.data.work, json.data.data);
            }.bind(this));
        }else if (this.options.draft){
            this.loadWorkByDraft(this.options.draft, this.options.draftData);
        }else if (this.options.jobId || this.options.jobid || this.options.job){
            var jobId = this.options.jobId || this.options.jobid || this.options.job;
            delete this.options.jobId;
            delete this.options.jobid;
            delete this.options.job;
            this.loadWorkByJob(jobId);
        }else if(this.options.taskCompletedId){
            MWF.Actions.load('x_processplatform_assemble_surface').TaskCompletedAction.get(this.options.taskCompletedId, function (json){
                if( json.data.completed ){
                    this.options.workCompletedId = json.data.workCompleted;
                }else{
                    this.options.workId = json.data.work;
                }
                this.loadWork();
            }.bind(this));
        }
    },
    loadWorkByWork: function(id){
	    debugger;
        //var getWorkLogMothed = "getWorkLog";    //以前使用worklog，现在改成record了
        //var getWorkLogMothed = (this.options.worklogType.toLowerCase()==="worklog") ? "getWorkLog" : "getRecordLog";
        var loadFormFlag = false;
        var loadWorkFlag = false;
        var loadModuleFlag = false;

        var json_work, json_log, json_control, json_form;

        var check = function(){
             if (loadWorkFlag && loadFormFlag && loadModuleFlag){
                if (json_work && json_control && json_form && json_log){
                    this.parseData(json_work.data, json_control.data, json_form.data, json_log.data, json_work.data.recordList, json_work.data.attachmentList).then(function(){
                        if (this.mask) this.mask.hide();
                        //if (layout.mobile) this.loadMobileActions();
                        if (layout.session && layout.session.user){
                            this.openWork();
                            this.unLoading();
                        }else{
                            if (layout.sessionPromise){
                                layout.sessionPromise.then(function(){
                                    this.openWork();
                                    this.unLoading();
                                }.bind(this), function(){});
                            }
                        }
                    }.bind(this));
                } else{
                    if (this.options.jobId || this.options.jobid || this.options.job){
                        delete this.options.workCompletedId;
                        delete this.options.workId;
                        delete this.options.workid;
                        delete this.options.workcompletedid;
                        this.loadWork();
                    }else{
                        layout.sessionPromise.then(function(){
                            this.notice( this.lp.openWorkError, "error")
                            this.close();
                        }.bind(this), function(){});
                        //this.close();
                    }
                }
            }
        }.bind(this);

        if ((this.options.form && this.options.form.id) || this.options.formid){
            o2.Actions.invokeAsync([
                {"action": this.action, "name": "loadWorkV2"},
                {"action": this.action, "name": "getWorkLog"},
                {"action": this.action, "name": "getWorkControl"},
                {"action": this.action, "name": ((layout.mobile) ? "getFormV2Mobile": "getFormV2")}
            ], {"success": function(jsonWork, jsonLog, jsonControl, jsonForm){
                    json_work = jsonWork;
                    json_log = jsonLog;
                    json_control = jsonControl;
                    json_form = jsonForm;
                    loadWorkFlag = true;
                    loadFormFlag = true;
                    check();
                }.bind(this), "failure": function(){
                    // layout.sessionPromise.then(function(){
                    //     this.close();
                    // }.bind(this), function(){});
                    //this.close();
                }.bind(this)}, id, id, id, [this.options.formid || this.options.form.id , new Date().getTime()]);
        }else{
            var lookupMethod, lookupId;
            if( this.options.taskCompletedId ){
                lookupId = this.options.taskCompletedId;
                lookupMethod = layout.mobile ? "lookupFormWithTaskCompletedMobile" : "lookupFormWithTaskCompleted";
            }else{
                lookupId = id;
                lookupMethod = layout.mobile ? "lookupFormWithWorkMobile" : "lookupFormWithWork";
            }
            this.action[lookupMethod](lookupId, function(json){
                var formId = json.data.id;
                if (json.data.form){
                    json_form = json;
                    loadFormFlag = true;
                    check();
                }else{
                    //临时查看效果
                    // if (formId=="4f8b4fde-d963-468c-b6c9-9e7b919f0bd0"){
                    //     o2.JSON.get("../x_desktop/res/form/4f8b4fde-d963-468c-b6c9-9e7b919f0bd0.json", function(formJson){
                    //         json_form = formJson;
                    //         loadFormFlag = true;
                    //         check();
                    //     });
                    // }else{
                    var cacheTag = json.data.cacheTag || "";
                        this.action[((layout.mobile) ? "getFormV2Mobile": "getFormV2")](formId, cacheTag, function(formJson){
                            json_form = formJson;
                            loadFormFlag = true;
                            check();
                        }, function(){
                            loadFormFlag = true;
                            check();
                            return true;
                        });
                    // }

                }

            }.bind(this), function(){
                loadFormFlag = true;
                check();
            });
            o2.Actions.invokeAsync([
                    {"action": this.action, "name": "loadWorkV2"},
                    {"action": this.action, "name": "getWorkLog"},
                    {"action": this.action, "name": "getWorkControl"}
                ], {"success": function(jsonWork, jsonLog, jsonControl){
                        json_work = jsonWork;
                        json_log = jsonLog;
                        json_control = jsonControl;
                        loadWorkFlag = true;
                        check();
                    }.bind(this), "failure": function(){
                        // layout.sessionPromise.then(function(){
                        //     this.close();
                        // }.bind(this), function(){});
                        //this.close();
                    }.bind(this)}, id
            );
        }
        var cl = "$all";
        MWF.xDesktop.requireApp("process.Xform", cl, function(){
            loadModuleFlag = true;
            check();
        });
    },
    loadWorkByJob: function(jobId){
        MWF.Actions.get("x_processplatform_assemble_surface").listWorkByJob(jobId, function(json){
            var workCompletedCount = json.data.workCompletedList.length;
            var workCount = json.data.workList.length;
            var count = workCount+workCompletedCount;
            if (count===1){
                this.options.workId = (json.data.workList.length) ? json.data.workList[0].id : json.data.workCompletedList[0].id;
                this.loadWork();
            }else if (count>1){
                var id = this.filterId(json.data.workList, json.data.workCompletedList, this.options.priorityWork);
                if (id) {
                    this.options.workId = id;
                    this.loadWork();
                }else{
                    if (this.options.choice){
                        var worksAreaNode = this.createWorksArea();
                        // for (var x=0;x<3;x++){
                        json.data.workList.each(function(work){
                            this.createWorkNode(work, worksAreaNode);
                        }.bind(this));
                        json.data.workCompletedList.each(function(work){
                            this.createWorkCompletedNode(work, worksAreaNode);
                        }.bind(this));
                        // }
                        if (this.mask) this.mask.hide();
                        this.formNode.setStyles(this.css.formNode_bg);
                    }else{
                        if (json.data.workList.length){
                            this.options.workId =  json.data.workList[0].id;
                        }else{
                            this.options.workId =  json.data.workCompletedList[0].id;
                        }
                        this.loadWork();
                    }
                }
            }else{
                layout.sessionPromise.then(function(){
                    this.notice( this.lp.openWorkError, "error");
                    this.close();
                }.bind(this), function(){});
                //this.close();
            }
        }.bind(this));
    },
    loadWorkByDraft: function(work, data){
        o2.Actions.invokeAsync([
            //{"action": this.action, "name": (layout.mobile) ? "getFormMobile": "getForm"}
            {"action": this.action, "name": (layout.mobile) ? "getFormV2Mobile": "getFormV2"}
        ], {"success": function(json_form){
            if (json_form){
                var workData = {
                    "activity": {},
                    "data": data || {},
                    "taskList": [],
                    "work": work
                };
                var control = {
                    "allowVisit": true,
                    "allowFlow": true,
                    "allowProcessing": true,
                    "allowSave": true,
                    "allowDelete": true
                };

                this.parseData(workData, control, json_form.data, [], [], []).then(function(){
                    if (this.mask) this.mask.hide();

                    if (layout.session && layout.session.user){
                        this.openWork();
                        this.unLoading();
                    }else{
                        if (layout.sessionPromise){
                            layout.sessionPromise.then(function(){
                                this.openWork();
                                this.unLoading();
                            }.bind(this), function(){});
                        }
                    }
                }.bind(this));


                // this.openWork();
                // this.unLoading();

            }
        }.bind(this), "failure": function(){}}, [work.form, work.application]);
    },

    createWorkNode: function(work, node, completed){
	    var contentNode = node.getLast();
	    var workNode = new Element("div", {"styles": this.css.workItemNode}).inject(contentNode);
        var titleNode = new Element("div", {"styles": this.css.workItemTitleNode}).inject(workNode);
        titleNode.set("text", work.title);
        var inforNode = new Element("div", {"styles": this.css.workItemInforNode}).inject(workNode);

        if (completed){
            inforNode.set("text", this.lp.completedWork);
        }else{
            var activityTitleNode = new Element("div", {"styles": this.css.workItemInforTitleNode, "text": this.lp.currentActivity}).inject(inforNode);
            var activityContentNode = new Element("div", {"styles": this.css.workItemInforContentNode, "text": work.activityName}).inject(inforNode);

            var userTitleNode = new Element("div", {"styles": this.css.workItemInforTitleNode, "text": this.lp.currentUsers}).inject(inforNode);
            var taskUsers = [];
            MWF.Actions.get("x_processplatform_assemble_surface").listTaskByWork(work.id, function(json){
                json.data.each(function(task){
                    taskUsers.push(MWF.name.cn(task.person));
                }.bind(this));
                var activityContentNode = new Element("div", {"styles": this.css.workItemInforContentNode, "text": taskUsers.join(", ")}).inject(inforNode);
            }.bind(this));
        }

        var _self = this;
        workNode.store("workId", work.id);
        workNode.addEvents({
            "mouseover": function(){
                this.addClass("mainColor_border");
                this.setStyles(_self.css.workItemNode_over);
            },
            "mouseout": function(){
                this.removeClass("mainColor_border");
                this.setStyles(_self.css.workItemNode);
            },
            "click": function(){
                var id = this.retrieve("workId");
                if (id){
                    _self.options.workId = id;
                    _self.loadWork();
                }
            },
        });
    },
    createWorksArea: function(){
        var node = new Element("div", {"styles": this.css.workListArea}).inject(this.formNode);
        var titleNode = new Element("div", {"styles": this.css.workListAreaTitle, "text": this.lp.selectWork}).inject(node);
        var contentNode = new Element("div", {"styles": this.css.workListContent}).inject(node);
        return node;
    },
    filterId: function(list, completedList, id){
	    if (!id) return "";
        if (!list.length && !completedList.length) return "";
        if (list.length){
            var o = list.filter(function(work){
                return work.id == id;
            }.bind(this));
            if (o.length) return o[0].id;
        }
        if (completedList.length) {
            o = completedList.filter(function(work){
                return work.id == id;
            }.bind(this));
            return (o.length) ? o[0].id : "";
        }
        return "";
    },
    parseWorkData: function(workData, controlData, formData, logData, recordData, attData){

    },
    parseData: function(workData, controlData, formData, logData, recordData, attData){
        var title = workData.work.title;
        this.setTitle(title || this.options.title);

        this.activity = workData.activity;
        this.data = workData.data;
        this.taskList = workData.taskList;

        this.taskList = workData.taskList;
        this.readList = workData.readList;
        this.routeList = workData.routeList;
        this.work = workData.work;
        this.workCompleted = (workData.work.completedTime) ? workData.work : null;

        this.workLogList = logData;
        this.recordList = recordData;
        this.attachmentList = attData;

        this.control = controlData || {};
        if( this.control.allowProcessing || this.control.allowReset || this.control.allowAddTask || this.control.allowGoBack ){
            this.control.allowFlow = true;
        }

        if (formData){
            if (formData.form){
                this.formDataText = (formData.form.data) ? MWF.decodeJsonString(formData.form.data) : "";
                this.form = (this.formDataText) ? JSON.decode(this.formDataText): null;
                this.relatedFormMap = formData.relatedFormMap;
                this.relatedScriptMap = formData.relatedScriptMap;
                this.relatedLanguage = formData.relatedLanguage;
                delete formData.form.data;
                this.formInfor = formData.form;
            }else{
                this.formDataText = (formData.data) ? MWF.decodeJsonString(formData.data) : "";
                this.form = (this.formDataText) ? JSON.decode(this.formDataText): null;

                //this.form = (formData.data) ? MWF.decodeJsonString(formData.data): null;
                delete formData.data;
                this.formInfor = formData;
            }
        }

        return new Promise(function (resolve, reject){
            var currentTask = this.getCurrentTaskData(workData);
            if (o2.typeOf(currentTask)==="array"){
                if (currentTask.length){
                    this.selectPersonIdentity(currentTask, resolve);
                }else{
                    resolve();
                }
            }else{
                this.currentTask = this.getCurrentTaskData(workData);
                resolve();
            }
        }.bind(this));
    },
    selectPersonIdentity: function(tasks, callback){
        var size = this.content.getSize();
        var area = new Element("div", {"styles": this.css.identitySelectArea}).inject(this.content);
        var node = new Element("div", {"styles": this.css.identitySelectNode}).inject(area);
        var height = size.y*0.8;
        var margin = size.y*0.1;
        node.setStyles({
            "height": height+"px",
            "margin-top": margin+"px"
        });

        var titleNode = new Element("div", {
            "styles": this.css.identitySelectNodeTitle,
            "text": this.lp.selectIdentity
        }).inject(node);

        var infoNode = new Element("div", {
            "styles": this.css.identitySelectNodeInfo,
            "text": this.lp.selectIdentityInfo
        }).inject(node);

        var listNode = new Element("div", {"styles": this.css.identitySelectNodeList}).inject(node);

        if (!layout.session.user.iconUrl) layout.session.user.iconUrl = o2.filterUrl(o2.Actions.get("x_organization_assemble_control").getPersonIcon(layout.session.user.id));
        tasks.forEach(function(task){
            var id = layout.session.user.identityList.find(function(i){ return i.distinguishedName === task.identity });
            o2.Actions.load("x_organization_assemble_express").UnitDutyAction.listNameWithIdentity({"identityList": [id.distinguishedName]}, function(json){
                var duty = json.data.nameList.join(', ');
                var idNode = new Element("div", {"styles": this.css.identitySelectNodeItem}).inject(listNode);
                var html = "<div style=\"height: 50px; margin-bottom: 5px;\">\n" +
                    "                    <div style=\"height: 50px; width: 50px; border-radius: 25px; overflow: hidden; float: left;\"><img\n" +
                    "                        width=\"50\" height=\"50\" border=\"0\"\n" +
                    "                        src=\""+layout.session.user.iconUrl+"\">\n" +
                    "                    </div>\n" +
                    "                    <div\n" +
                    "                        style=\"height: 40px; line-height: 40px; overflow: hidden; float: left; margin-left: 10px; margin-right: 30px; width: 150px; color: rgb(51, 51, 51); font-size: 16px; text-align: left;\">"+id.name+"\n" +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "                <div style=\"height: 36px; line-height: 40px; overflow: hidden; font-size: 14px;\">\n" +
                    "                    <div style=\"color: rgb(0, 0, 0); width: 40px; float: left;\">"+this.lp.org+"</div>\n" +
                    "                    <div title=\""+id.unitLevelName+"\"\n" +
                    "                         style=\"margin-left: 40px; text-align: left; color: rgb(153, 153, 153);\">"+id.unitLevelName+"\n" +
                    "                    </div>\n" +
                    "                </div>\n" +
                    "                <div style=\"height: 36px; line-height: 40px; overflow: hidden; font-size: 14px;\">\n" +
                    "                    <div style=\"color: rgb(0, 0, 0); width: 40px; float: left;\">"+this.lp.duty+"</div>\n" +
                    "                    <div title=\""+duty+"\" style=\"margin-left: 40px; text-align: left; color: rgb(153, 153, 153);\">"+duty+"</div>\n" +
                    "                </div>\n" +
                    "                <div class=\"mainColor_color\"\n" +
                    "                     style=\"position: absolute; float: right; top: 14px; right: 14px;\">【"+id.unitName+"】\n" +
                    "                </div>";
                idNode.set("html", html);

                idNode.addEvents({
                    "mouseover": function(){
                        this.setStyle("border", "1px solid rgb(74, 144, 226)");
                    },
                    "mouseout": function(){
                        this.setStyle("border", "1px solid rgb(230, 230, 230)");
                    },
                    "click": function(){
                        this.currentTask = task;
                        area.destroy();
                        callback();
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this))
    },

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
        if (data.activity && data.activity.hasOwnProperty('processingTaskOnceUnderSamePerson') && !data.activity.processingTaskOnceUnderSamePerson){
            //不进行待办合并处理，需要用户选择一个身份后继续
            var _self = this;
            var task = (this.options.taskId) ?  this.taskList.find(function(t){
                return t.id === _self.options.taskId;
            }) : null;

            if (task) return task;

            var taskList = this.taskList.filter(function(t){
                return t.person === layout.session.user.distinguishedName;
            });
            return (taskList.length && taskList.length===1) ? taskList[0] : taskList;
        }else{
            //进行待办合并处理
            if ( this.taskList && (data.currentTaskIndex || data.currentTaskIndex===0) && data.currentTaskIndex != -1){
                if( this.taskList[data.currentTaskIndex] ){
                    this.options.taskId = this.taskList[data.currentTaskIndex].id;
                    return this.taskList[data.currentTaskIndex];
                }
            }
        }
        return null;
    },
    openWork: function(){
        if (this.form){
            if( this.options.readonly )this.readonly = true;
            this.formNode.empty();
            this.formNode.setStyles(this.css.formNode);
            var uri = window.location.href;
            //var cl = (uri.indexOf("$all")!=-1) ? "$all" : "Form";
            var cl = "$all";
            MWF.xDesktop.requireApp("process.Xform", cl, function(){
            //MWF.xDesktop.requireApp("process.Xform", "Form", function(){
                this.appForm = new MWF.APPForm(this.formNode, this.form, {"readonly": this.readonly});
                if( !this.currentTask && this.control.allowReset ){
                    this.control.allowReset = false;
                }
                this.appForm.businessData = {
                    "data": this.data,
                    "originalData" : Object.clone( this.data ),
                    "taskList": this.taskList,
                    "readList": this.readList,
                    "work": this.work,
                    "workCompleted": this.workCompleted,
                    "control": this.control,
                    "activity": this.activity,
                    "task": this.currentTask,
                    "workLogList": this.workLogList,
                    "recordList": this.recordList,
                    "routeList" : this.routeList,
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
                this.appForm.formDataText = this.formDataText;

                if( this.$events && this.$events.queryLoadForm ){
                    this.appForm.addEvent( "queryLoad", function () {
                        this.fireEvent("queryLoadForm", [this]);
                    }.bind(this));
                }

                this.appForm.load(function(){
                    if (this.mask) this.mask.hide();
                    if (window.o2android && window.o2android.postMessage) {
                        var body = {
                            type: "appFormLoaded",
                            data: this.appForm.mobileTools
                        }
                        window.o2android.postMessage(JSON.stringify(body));
                    } else if (window.o2android && window.o2android.appFormLoaded){
                        layout.appForm = this.appForm;
                        window.o2android.appFormLoaded(JSON.stringify(this.appForm.mobileTools));
                    } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.appFormLoaded){
                        layout.appForm = this.appForm;
                        window.webkit.messageHandlers.appFormLoaded.postMessage(JSON.stringify(this.appForm.mobileTools));
                    }
                    if (this.options.action=="processTask"){
                        this.appForm.processWork();
                        this.options.action = "";
                    }else if( this.options.action=="flowTask" ){
                        this.appForm.flowWork();
                        this.options.action = "";
                    }

                    this.fireEvent("postLoadForm", [this]);
                }.bind(this));
            }.bind(this));
        }
    },

    //errorWork: function(){
    //
    //},

    recordStatus: function(){
	    debugger;
	    var status = {
            "workId": this.options.workId,
            "taskId": this.options.taskId,
            "taskCompletedId": this.options.taskCompletedId,
            "workCompletedId": this.options.workCompletedId,
            "jobId": this.options.jobId,
            "draftId": this.options.draftId,
            "priorityWork": this.options.priorityWork,
            "readonly": this.readonly
        };
        if( this.options.formid )status.formid = this.options.formid;
        if( this.options.form && this.options.form.id )status.form = this.options.form;
        return status;
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
