MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.xApplication.ProcessTool.Work = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='processing'></td>" +
            "    <td styles='filterTableValue' item='endWork'></td>" +
            "    <td styles='filterTableValue' item='back'></td>" +
            "    <td styles='filterTableValue' item='addReview'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        this.fileterForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                remove: {
                    "value": "删除", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要删除吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._removeWork(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("删除成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                processing: {
                    "value": "尝试流转", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要流转吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._flow(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("流转成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                endWork: {
                    "value": "结束流程", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要结束流程吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._endWork(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("结束流程成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                addReview: {
                    "value": "增加参阅", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var ids = [];
                            checkedItems.each(function (item){
                                ids.push(item.data.id);
                            }.bind(this));

                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要增加参阅人员吗？", 350, 120, function () {

                                this.close();

                                var reviewNode = new Element("div",{"class":"control","style":"margin:10px"});
                                var personNode =  new Element("textarea",{"class":"textarea","placeholder":"参阅人员选择"});
                                personNode.inject(reviewNode);
                                personNode.addEvent("click",function(){
                                    var opt = {
                                        "type": "person",
                                        "count": 0,
                                        "values": personNode.retrieve("dataList") || [],
                                        "onComplete": function (dataList) {
                                            var arr = [];
                                            var arr2 = [];
                                            dataList.each(function (data) {
                                                arr.push(data.data);
                                                arr2.push(data.data.name);
                                            });
                                            personNode.set("value", arr2.join(","));
                                            personNode.store("dataList", arr);
                                        }.bind(this)
                                    };
                                    new MWF.O2Selector(_self.app.content, opt);
                                }.bind(this));

                                var reviewDlg = o2.DL.open({
                                    "title": "增加参阅",
                                    "width": "400px",
                                    "height": "260px",
                                    "mask": true,
                                    "content": reviewNode,
                                    "container": null,
                                    "positionNode": _self.app.content,
                                    "onQueryClose": function () {
                                        reviewNode.destroy();
                                    }.bind(this),
                                    "buttonList": [
                                        {
                                            "text": "确认",
                                            "action": function () {
                                                var personList = personNode.retrieve("dataList") ;
                                                var arr = [];
                                                personList.each(function(person){
                                                    arr.push(person.distinguishedName);
                                                });

                                                ids.each(function(workId){
                                                    var data = {
                                                        "work":workId,
                                                        "personList":arr
                                                    }
                                                    _self.view._addWorkReview(data);
                                                });
                                                _self.form.app.notice("增加成功","success");
                                                _self.loadView();
                                                reviewDlg.close();
                                            }.bind(this)
                                        },
                                        {
                                            "text": "关闭",
                                            "action": function () {
                                                reviewDlg.close();
                                            }.bind(this)
                                        }
                                    ],
                                    "onPostShow": function () {
                                        reviewDlg.reCenter();
                                    }.bind(this)
                                });



                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
});
MWF.xApplication.ProcessTool.Work.View = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.View,

    _getCurrentPageData: function (callback, count, pageNum) {
        this.clearBody();
        if (!count) count = 15;
        if (!pageNum) {
            if (this.pageNum) {
                pageNum = this.pageNum = this.pageNum + 1;
            } else {
                pageNum = this.pageNum = 1;
            }
        } else {
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};
        this.app.action.WorkAction.manageListFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _removeWork : function (data,flag){

        if(flag==="all"){
            this.app.action.WorkAction.manageDeleteSingleWork(data.id,null,null,false);
        }else{
            this.app.action.WorkAction.manageDeleteRelativeWork(data.id,null,null,false);
        }

    },
    _addWorkReview : function (data){
        this.app.action.ReviewAction.createWithWork(data,function (){},null,false);
    },
    _flow : function (data){
        this.app.action.WorkAction.processing(data.id,{},function (){},null,false);
    },
    _endWork : function (data){
        var processObj = this._getProcess(data.process);
        var endList = processObj.endList;
        var endActivityId = endList[0].id;

        var body = {
            "activity": endActivityId,
            "activityType": "end",
            "mergeWork": false,
            "manualForceTaskIdentityList": null
        };
        this.app.action.WorkAction.V2Reroute(data.id, body, function (json) {
        }.bind(this), null,false);
    },
    _snap : function (data){
        this.app.action.SnapAction.typeAbandoned(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _open: function (data) {
        var options = {"workId": data.id};
        this.app.app.desktop.openApplication(null, "process.Work", options);
    },
    _getProcess : function (processId){
        var data;
        this.app.action.ProcessAction.getComplex(processId,function (json){
            data = json.data;
        },null,false);
        return data;
    },
    _reroute : function (workId,data){
        this.app.action.WorkAction.V2Reroute(workId,data,function (json){
            data = json.data;
        },null,false);
    }
});
MWF.xApplication.ProcessTool.Work.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    },
    edit : function (){

        var form = new MWF.xApplication.ProcessTool.Work.EditForm({app: this.app.app}, this.data );
        form.open();

    },
    reroute : function (){
        var processObj = this.view._getProcess(this.data.process);
        var activityList = Object.merge(processObj.manualList,processObj.mergeList,processObj.parallelList,processObj.invokeList,processObj.splitList,processObj.agentList);

        var jumpContainer = new Element("div");
        var jumpNode = new Element("div",{"class":"select","style":"margin:10px"}).inject(jumpContainer);
        var jumpActivityNameNode = new Element("select").inject(jumpNode);

        activityList.each(function(activity){
            new Element("option",{"text":activity.name + "|" + activity.alias ,"value":activity.id}).inject(jumpActivityNameNode);
        });


        var mergeWorkNode = new Element("div").inject(jumpContainer);
        new Element("div",{
            "html" : '<div style="float:left">是否合并work：</div><div style="float:left"><input name=mergeWork type=radio checked value=no>否<input name=mergeWork type=radio value=yes>是</div>',
            "style":"margin:10px;height:24px"
        }).inject(mergeWorkNode);

        var personContainer = new Element("div",{"style":"margin:10px;width:100%;heigh:28px;line-height:28px"}).inject(jumpContainer);
        var personNode = new Element("input",{"class":"input","placeholder":"选择调度给谁"}).inject(personContainer);

        personNode.addEvent("click",function(){
            var opt = {
                "type": "identity",
                "count": 0,
                "values": personNode.retrieve("dataList") || [],
                "onComplete": function (dataList) {
                    var arr = [];
                    var arr2 = [];
                    dataList.each(function (data) {
                        arr.push(data.data);
                        arr2.push(data.data.name);
                    });
                    personNode.set("value", arr2.join(","));
                    personNode.store("dataList", arr);
                }.bind(this)
            };
            new MWF.O2Selector(this.explorer.app.content, opt);
        }.bind(this));

        var jumpDlg = o2.DL.open({
            "title": "调度",
            "width": "400px",
            "height": "260px",
            "mask": true,
            "content": jumpContainer,
            "container": null,
            "positionNode":this.explorer.app.content,
            "onQueryClose": function () {
                jumpContainer.destroy();
            }.bind(this),
            "buttonList": [
                {
                    "text": "确认",
                    "action": function () {
                        var identityList = personNode.retrieve("dataList") ;
                        var arr = [];
                        if(identityList){
                            identityList.each(function(identity){
                                arr.push(identity.distinguishedName);
                            });
                        }

                        var body = {
                            "activity": jumpActivityNameNode.get("value"),
                            "activityType": "manual",
                            "mergeWork": mergeWorkNode.getElement("input:checked").get("value")==="yes" ? true:false,
                            "manualForceTaskIdentityList": arr
                        };


                        this.view._reroute(this.data.id, body);
                        this.explorer.app.notice("调度成功", "info");
                        jumpDlg.close();
                        this.view.app.loadView();

                    }.bind(this)
                },
                {
                    "text": "关闭",
                    "action": function () {
                        jumpDlg.close();
                    }.bind(this)
                }
            ],
            "onPostShow": function () {
                jumpDlg.reCenter();
            }.bind(this)
        });
    },
    remove : function (ev){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;


        this.explorer.app.confirm("warn", ev,"删除确认！！", {
            "html": "<br/>请选择删除方式？ <br/><br/><input type='radio' value='soft' name='delete_type'/>软删除（可恢复）" +
                "<br/><br/><input type='radio' value='delete' name='delete_type'/>硬删除（不能恢复）<div class='checkInfor'></div>"

        }, 300, 280, function(){
            var inputs = this.content.getElements("input");

            var flag = "";
            for (var i=0; i<inputs.length; i++){
                if (inputs[i].checked){
                    flag = inputs[i].get("value");
                    break;
                }
            }
            if (flag){

                if(flag === "soft"){
                    _self.view._snap(_self.data);
                    _self.view.lockNodeStyle = false;
                    this.close();
                    _self.view.app.loadView();
                }else {

                    _self.explorer.app.confirm("warn", ev,"删除确认！！", {
                        "html": "您是否要删除此工作的关联工作？ <br/><br/><input type='radio' value='all' name='deleteWork_check'/>删除当前工作及其所有关联工作；" +
                            "<br/><input type='radio' value='single' name='deleteWork_check'/>只删除当前工作<br><div id='deleteWork_checkInfor'></div>"

                    }, 400, 300, function(){
                        var inputs = this.content.getElements("input");

                        var flag = "";
                        for (var i=0; i<inputs.length; i++){
                            if (inputs[i].checked){
                                flag = inputs[i].get("value");
                                break;
                            }
                        }
                        if (flag){

                            _self.view._removeWork(_self.data,flag);
                            _self.view.lockNodeStyle = false;
                            this.close();
                            _self.view.app.loadView();
                        }else{
                            this.content.getElement("#deleteWork_checkInfor").set("text", "请选择是否删除所有关联工作！").setStyle("color", "red");
                        }
                    }, function(){
                        _self.node.setStyles(_self.css.documentNode);
                        _self.readyRemove = false;
                        _self.view.lockNodeStyle = false;
                        this.close();
                    });
                    this.close();
                }

            }else{
                this.content.getElement(".checkInfor").set("text", "请选择删除方式！").setStyle("color", "red");
            }
        }, function(){
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });

    }
});
MWF.xApplication.ProcessTool.Work.EditForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "800",
        "height": "700",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": "维护管理",
        "hideBottomWhenReading": true,
        "closeByClickMaskWhenReading": true,
    },
    _postLoad: function(){
        if(this.data.completedTime){
            this.isCompletedWork = true;
        }
        this._createTableContent_();
    },
    _createTableContent: function(){},
    _createTableContent_: function () {

        //this.formTableArea.set("html", this.getHtml());
        this.formTableContainer.setStyle("width","90%");
        this.formTableContainer.setStyle("margin","0px auto 10px");
        this.loadTab();

    },
    loadTab : function (){

        this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.formTableArea);

        this.taskArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.taskDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.readArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.readDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.reviewArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.attachementArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.recordArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        this.businessDataArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);

        MWF.require("MWF.widget.Tab", function(){

            this.tabs = new MWF.widget.Tab(this.tabNode, {"style": "attendance"});
            this.tabs.load();

            this.taskPage = this.tabs.addTab(this.taskArea, "待办", false);
            this.taskPage.addEvent("show",function(){
                if(!this.initTask) this.loadTask();
            }.bind(this));

            this.taskDonePage = this.tabs.addTab(this.taskDoneArea, "已办", false);
            this.taskDonePage.addEvent("show",function(){
                if(!this.initTaskDone) this.loadTaskDone();
            }.bind(this));

            this.readPage = this.tabs.addTab(this.readArea, "待阅", false);
            this.readPage.addEvent("show",function(){
                if(!this.initRead) this.loadRead();
            }.bind(this));

            this.readDonePage = this.tabs.addTab(this.readDoneArea, "已阅", false);
            this.readDonePage.addEvent("show",function(){
                if(!this.initReadDone) this.loadReadDone();
            }.bind(this));

            this.recordPage = this.tabs.addTab(this.recordArea, "流转记录", false);
            this.recordPage.addEvent("show",function(){
                if(!this.initRecord) this.loadRecord();
            }.bind(this));

            this.reviewPage = this.tabs.addTab(this.reviewArea, "参阅", false);
            this.reviewPage.addEvent("show",function(){
                if(!this.initReview) this.loadReview();
            }.bind(this));


            this.attachementPage = this.tabs.addTab(this.attachementArea, "附件", false);
            this.attachementPage.addEvent("show",function(){
                if(!this.initAttachement) this.loadAttachement();
            }.bind(this));

            this.businessDataPage = this.tabs.addTab(this.businessDataArea, "业务数据", false);
            this.businessDataPage.addEvent("show",function(){
                if(!this.initBusinessData) this.loadBusinessData();
            }.bind(this));

            this.tabs.pages[0].showTab();
        }.bind(this));
    },
    loadTask : function () {
        this.app.action.TaskAction.listWithJob(this.data.job, function (json) {
            this.taskList = json.data;
            this._loadTask();
            this.initTask = true;
        }.bind(this), null, false);
    },
    _loadTask : function (){
        this.taskArea.empty();
        this.taskContentNode = new Element("div").inject(this.taskArea);
        var taskTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.taskContentNode);

        var taskTableTheadNode = new Element("thead").inject(taskTableNode);
        var taskTableTbodyNode = new Element("tbody").inject(taskTableNode);
        var taskTableTheadTrNode = new Element("tr").inject(taskTableTheadNode);
        Array.each(["处理人", "部门", "环节", "到达时间", "操作"], function (text) {
            new Element("th", {"text": text}).inject(taskTableTheadTrNode);
        });
        this.taskList.each(function (task) {
            var trNode = new Element("tr").inject(taskTableTbodyNode);
            trNode.store("data", task);

            Array.each([task.person.split("@")[0], task.unit.split("@")[0], task.activityName, task.startTime], function (text) {
                new Element("td", {
                    text: text
                }).inject(trNode);
            });
            var tdOpNode = new Element("td").inject(trNode);
            var restButton = new Element("button", {"text": "转交", "class": "button"}).inject(tdOpNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);
            _self = this;
            deleteButton.addEvent("click", function (e) {
                _self.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.TaskAction.manageDelete(task.id,function (){},null,false);
                    _self.loadTask();
                    this.close();
                }, function(){
                    this.close();
                });

            }.bind(this));
            restButton.addEvent("click", function (ev) {

                var opt = {
                    "type": "identity",
                    "count": 0,
                    "title": "转交",
                    "onComplete": function (items) {
                        var _self = this;
                        var nameArr = [];
                        items.each(function(item){
                            nameArr.push(item.data.name)
                        });
                        this.explorer.app.confirm("warn", ev,"转交确认！！", {
                            "html": "转交给：" + nameArr.join() +"<br/>您是否保留自身待办？ <br/><br/><input type='radio' value='yes' name='reset_keep'/>保留" +
                                "<br/><input type='radio' value='no' name='reset_keep'/>不保留<br>意见：<br><div id='reset_checkInfor'><textarea name='reset_idea' cols='40' style='width:200px'></textarea></div>"

                        }, 400, 300, function(){
                            var inputs = this.content.getElements("input");
                            var opinion = this.content.getElement("textarea").get("value");
                            var flag = "";
                            for (var i=0; i<inputs.length; i++){
                                if (inputs[i].checked){
                                    flag = inputs[i].get("value");
                                    break;
                                }
                            }
                            if (flag){

                                var keep = true;
                                if(flag==="no"){
                                    keep = false;
                                }

                                var nameList = [];
                                items.each(function (identity) {
                                    nameList.push(identity.data.distinguishedName);
                                });
                                var data = {
                                    "routeName":"转交",
                                    "opinion":opinion,
                                    "identityList":nameList,
                                    "keep" : keep
                                }
                                _self.app.action.TaskAction.manageReset(task.id,data,function(json){
                                },null,false);


                                _self.loadTask();
                                this.close();
                            }else{
                                this.content.getElement("#reset_checkInfor").set("text", "请选择是否保留自身待办！").setStyle("color", "red");
                            }
                        }, function(){
                            this.close();
                        });

                    }.bind(this)
                };
                new MWF.O2Selector(this.app.content, opt);

            }.bind(this));

        }.bind(this));
    },
    loadTaskDone : function () {
        this.app.action.TaskCompletedAction.listWithJob(this.data.job, function (json) {
            this.taskDoneList = json.data;
            this._loadTaskDone();
            this.initTaskDone = true;
        }.bind(this), null, false);
    },
    _loadTaskDone : function (){
        this.taskDoneArea.empty();
        this.taskDoneContentNode = new Element("div").inject(this.taskDoneArea);

        var taskDoneTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.taskDoneContentNode);

        var taskDoneTableTheadNode = new Element("thead").inject(taskDoneTableNode);
        var taskDoneTableTbodyNode = new Element("tbody").inject(taskDoneTableNode);
        var taskDoneTableTheadTrNode = new Element("tr").inject(taskDoneTableTheadNode);
        Array.each(["处理人", "部门", "环节", "到达时间", "提交时间", "决策", "意见", "操作"], function (text) {

            var tmpthNode = new Element("th", {"text": text}).inject(taskDoneTableTheadTrNode);
            if(text==="意见"){
                tmpthNode.setStyle("width","150px");
            }
        });

        this.taskDoneList.each(function (taskDone) {
            var trNode = new Element("tr").inject(taskDoneTableTbodyNode);
            trNode.store("data", taskDone);

            Array.each([taskDone.person.split("@")[0], taskDone.unit.split("@")[0], taskDone.activityName, taskDone.startTime, taskDone.updateTime, taskDone.routeName, taskDone.opinion], function (text) {
                new Element("td", {
                    text: text
                }).inject(trNode);
            });
            var tdOpNode = new Element("td").inject(trNode);
            var setOpinionButton = new Element("button", {"text": "意见", "class": "button"}).inject(tdOpNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);

            deleteButton.addEvent("click", function (e) {
                _self = this;
                this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.TaskCompletedAction.manageDelete(taskDone.id,function (){},null,false);
                    _self.loadTaskDone();
                    this.close();
                }, function(){
                    this.close();
                });

            }.bind(this));

            setOpinionButton.addEvent("click", function () {
                var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
                var textareaNode = new Element("textarea", {"class": "textarea", "text": taskDone.opinion});
                textareaNode.inject(ideaNode);

                var ideaDlg = o2.DL.open({
                    "title": "意见修改",
                    "width": "400px",
                    "height": "260px",
                    "mask": true,
                    "content": ideaNode,
                    "container": null,
                    "positionNode": this.app.content,
                    "onQueryClose": function () {
                        ideaNode.destroy();
                    }.bind(this),
                    "buttonList": [
                        {
                            "text": "确认",
                            "action": function () {
                                this.app.action.TaskCompletedAction.manageOpinion(taskDone.id,{"opinion":textareaNode.get("value")},function(json){
                                },null,false);
                                this.loadTaskDone();
                                ideaDlg.close();
                            }.bind(this)
                        },
                        {
                            "text": "关闭",
                            "action": function () {
                                ideaDlg.close();
                            }.bind(this)
                        }
                    ],
                    "onPostShow": function () {
                        ideaDlg.reCenter();
                    }.bind(this)
                });
            }.bind(this));
        }.bind(this));
    },
    loadRead : function () {
        this.app.action.ReadAction.listWithJob(this.data.job, function (json) {
            this.readList = json.data;
            this._loadRead();
            this.initRead = true;
        }.bind(this), null, false);
    },
    _loadRead : function (){
        this.readArea.empty();
        this.readContentNode = new Element("div").inject(this.readArea);
        var readTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.readContentNode);

        var readTableTheadNode = new Element("thead").inject(readTableNode);
        var readTableTbodyNode = new Element("tbody").inject(readTableNode);
        var readTableTheadTrNode = new Element("tr").inject(readTableTheadNode);
        Array.each(["待阅人", "部门", "环节", "到达时间", "操作"], function (text) {
            new Element("th", {"text": text}).inject(readTableTheadTrNode);
        });

        this.readList.each(function (read) {
            var trNode = new Element("tr").inject(readTableTbodyNode);
            trNode.store("data", read);

            Array.each([read.person.split("@")[0], read.unit.split("@")[0], read.activityName, read.startTime], function (text) {
                new Element("td", {
                    text: text
                }).inject(trNode);
            });
            var tdOpNode = new Element("td").inject(trNode);
            var readButton = new Element("button", {"text": "已阅", "class": "button"}).inject(tdOpNode);
            var restButton = new Element("button", {"text": "转交", "class": "button"}).inject(tdOpNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);

            deleteButton.addEvent("click", function (e) {
                var _self = this;
                _self.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.ReadAction.manageDelete(read.id,function (){},null,false);
                    _self.loadRead();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));

            restButton.addEvent("click", function () {
                var opt = {
                    "type": "identity",
                    "count": 0,
                    "title": "转交",
                    "onComplete": function (items) {

                        var nameList = [];
                        var nameDnList = [];
                        items.each(function (identity) {
                            nameList.push(identity.data.name);
                            nameDnList.push(identity.data.distinguishedName);
                        });
                        var data = {
                            "opinion":"转交给" + ": " + nameList.join(","),
                            "identityList":nameDnList
                        }
                        this.app.action.ReadAction.manageResetRead(read.id,data,function(json){
                        },null,false);


                        this.loadRead();
                    }.bind(this)
                };

                new MWF.O2Selector(this.app.content, opt);

            }.bind(this));


            readButton.addEvent("click", function () {

                var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
                var textareaNode = new Element("textarea", {"style": "min-width:100%;height: 100px", "text": read.opinion});
                textareaNode.inject(ideaNode);

                var ideaDlg = o2.DL.open({
                    "title": "待阅处理",
                    "width": "400px",
                    "height": "260px",
                    "mask": true,
                    "content": ideaNode,
                    "container": null,
                    "positionNode": this.app.content,
                    "onQueryClose": function () {
                        ideaNode.destroy();
                    }.bind(this),
                    "buttonList": [
                        {
                            "text": "确认",
                            "action": function () {
                                this.app.action.ReadAction.manageProcessing(read.id,{"opinion":textareaNode.get("value")},function(json){
                                },null,false);
                                this.loadRead();
                                ideaDlg.close();

                            }.bind(this)
                        },
                        {
                            "text": "关闭",
                            "action": function () {
                                ideaDlg.close();
                            }.bind(this)
                        }
                    ],
                    "onPostShow": function () {
                        ideaDlg.reCenter();
                    }.bind(this)
                });

            }.bind(this));

        }.bind(this));
    },
    loadReadDone : function () {
        this.app.action.ReadCompletedAction.listWithJob(this.data.job, function (json) {
            this.readDoneList = json.data;
            this._loadReadDone();
            this.initReadDone = true;
        }.bind(this), null, false);
    },
    _loadReadDone : function (){
        this.readDoneArea.empty();
        this.readDoneContentNode = new Element("div").inject(this.readDoneArea);
        var readDoneTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.readDoneContentNode);

        var readDoneTableTheadNode = new Element("thead").inject(readDoneTableNode);
        var readDoneTableTbodyNode = new Element("tbody").inject(readDoneTableNode);
        var readDoneTableTheadTrNode = new Element("tr").inject(readDoneTableTheadNode);
        Array.each(["处理人", "部门", "环节", "到达时间", "提交时间", "意见", "操作"], function (text) {
            new Element("th", {"text": text}).inject(readDoneTableTheadTrNode);
        });

        this.readDoneList.each(function (readDone) {
            var trNode = new Element("tr").inject(readDoneTableTbodyNode);
            trNode.store("data", readDone);

            Array.each([readDone.person.split("@")[0], readDone.unit.split("@")[0], readDone.activityName, readDone.startTime, readDone.updateTime, readDone.opinion], function (text) {
                new Element("td", {
                    text: text
                }).inject(trNode);
            });
            var tdOpNode = new Element("td").inject(trNode);
            var setOpinionButton = new Element("button", {"text": "意见", "class": "button"}).inject(tdOpNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);

            deleteButton.addEvent("click", function (e) {
                _self = this;
                this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.ReadCompletedAction.manageDelete(readDone.id,function (){},null,false);
                    _self.loadReadDone();
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));

            setOpinionButton.addEvent("click", function () {
                var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
                var textareaNode = new Element("textarea", {"class": "textarea", "text": readDone.opinion});
                textareaNode.inject(ideaNode);

                var ideaDlg = o2.DL.open({
                    "title": "意见修改",
                    "width": "400px",
                    "height": "260px",
                    "mask": true,
                    "content": ideaNode,
                    "container": null,
                    "positionNode": this.app.content,
                    "onQueryClose": function () {
                        ideaNode.destroy();
                    }.bind(this),
                    "buttonList": [
                        {
                            "text": "确认",
                            "action": function () {
                                this.app.action.ReadCompletedAction.manageUpdate(readDone.id,{"opinion":textareaNode.get("value")},function(json){
                                },null,false);
                                this.loadReadDone();
                                ideaDlg.close();

                            }.bind(this)
                        },
                        {
                            "text": "关闭",
                            "action": function () {
                                ideaDlg.close();
                            }.bind(this)
                        }
                    ],
                    "onPostShow": function () {
                        ideaDlg.reCenter();
                    }.bind(this)
                });
            }.bind(this));

        }.bind(this));
    },
    loadRecord : function (){
        this.app.action.RecordAction.listWithJob(this.data.job, function (json) {
            this.recordList = json.data;
            this._loadRecord();
            this.initRecord = true;
        }.bind(this), null, false);
    },
    _loadRecord : function (){
        this.recordArea.empty();
        this.recordContentNode = new Element("div").inject(this.recordArea);
        var recordTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.recordContentNode);

        var recordTableTheadNode = new Element("thead").inject(recordTableNode);
        var recordTableTbodyNode = new Element("tbody").inject(recordTableNode);
        var recordTableTheadTrNode = new Element("tr").inject(recordTableTheadNode);
        Array.each(["处理人", "部门", "环节", "到达时间", "提交时间", "决策", "意见", "操作"], function (text) {

            var tmpthNode = new Element("th", {"text": text}).inject(recordTableTheadTrNode);
            if(text==="意见"){
                tmpthNode.setStyle("width","150px");
            }
        });

        this.recordList.each(function (record) {

            if(record.type!=="currentTask"){

                var trNode = new Element("tr").inject(recordTableTbodyNode);
                trNode.store("data", record);

                Array.each([record.person.split("@")[0], record.unit.split("@")[0], record.fromActivityName, record.properties.startTime, record.updateTime, record.properties.routeName, record.properties.opinion], function (text) {
                    new Element("td", {
                        text: text
                    }).inject(trNode);
                });
                var tdOpNode = new Element("td").inject(trNode);
                var setOpinionButton = new Element("button", {"text": "意见", "class": "button"}).inject(tdOpNode);

                var modifyButton = new Element("button", {"text": "修改", "class": "button"}).inject(tdOpNode);
                var copyButton = new Element("button", {"text": "拷贝", "class": "button"}).inject(tdOpNode);
                var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);
                deleteButton.addEvent("click", function (e) {
                    _self = this;
                    this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                        _self.app.action.RecordAction.manageDelete(record.id,function(json){

                            _self.loadRecord();
                        },null,false);

                        this.close();
                    }, function(){
                        this.close();
                    });

                }.bind(this));
                setOpinionButton.addEvent("click", function () {
                    _self = this;
                    var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
                    var textareaNode = new Element("textarea", {"class": "textarea", "text": record.properties.opinion});
                    textareaNode.inject(ideaNode);

                    var ideaDlg = o2.DL.open({
                        "title": "意见修改",
                        "width": "400px",
                        "height": "260px",
                        "mask": true,
                        "content": ideaNode,
                        "container": null,
                        "positionNode": this.app.content,
                        "onQueryClose": function () {
                            ideaNode.destroy();
                        }.bind(this),
                        "buttonList": [
                            {
                                "text": "确认",
                                "action": function () {
                                    record.properties.opinion = textareaNode.get("value");
                                    _self.app.action.RecordAction.manageEdit(record.id,record,function(json){
                                        _self.loadRecord();
                                    },null,false);
                                    ideaDlg.close();

                                }.bind(this)
                            },
                            {
                                "text": "关闭",
                                "action": function () {
                                    ideaDlg.close();
                                }.bind(this)
                            }
                        ],
                        "onPostShow": function () {
                            ideaDlg.reCenter();
                        }.bind(this)
                    });
                }.bind(this));
                modifyButton.addEvent("click", function () {
                    var recordNode = new Element("div", {"class": "control", "style": "margin:10px"});
                    var textareaNode = new Element("textarea", {"style":"height:350px","class": "textarea", "text": JSON.stringify(record,null,"\t")});
                    textareaNode.inject(recordNode);

                    var recordDlg = o2.DL.open({
                        "title": "流程记录修改",
                        "width": "800px",
                        "height": "500",
                        "mask": true,
                        "content": recordNode,
                        "container": null,
                        "positionNode": this.app.content,
                        "onQueryClose": function () {
                            recordNode.destroy();
                        }.bind(this),
                        "buttonList": [
                            {
                                "text": "确认",
                                "action": function () {
                                    record = JSON.parse(textareaNode.get("value"));
                                    this.app.action.RecordAction.manageEdit(record.id,record,function(json){
                                        this.loadRecord();
                                    }.bind(this),null,false);
                                    recordDlg.close();

                                }.bind(this)
                            },
                            {
                                "text": "关闭",
                                "action": function () {
                                    recordDlg.close();
                                }.bind(this)
                            }
                        ],
                        "onPostShow": function () {
                            recordDlg.reCenter();
                        }.bind(this)
                    });
                }.bind(this));
                copyButton.addEvent("click", function () {
                    var recordNode = new Element("div", {"class": "control", "style": "margin:10px"});
                    var textareaNode = new Element("textarea", {"style":"height:350px","class": "textarea", "text": JSON.stringify(record,null,"\t")});
                    textareaNode.inject(recordNode);

                    var recordDlg = o2.DL.open({
                        "title": "流程记录新增",
                        "width": "800px",
                        "height": "500",
                        "mask": true,
                        "content": recordNode,
                        "container": null,
                        "positionNode": this.app.content,
                        "onQueryClose": function () {
                            recordNode.destroy();
                        }.bind(this),
                        "buttonList": [
                            {
                                "text": "确认",
                                "action": function () {
                                    record = JSON.parse(textareaNode.get("value"));
                                    this.app.action.RecordAction.manageCreateWithJob(record.job,record,function(json){
                                        this.loadRecord();
                                    }.bind(this),null,false);
                                    recordDlg.close();

                                }.bind(this)
                            },
                            {
                                "text": "关闭",
                                "action": function () {
                                    recordDlg.close();
                                }.bind(this)
                            }
                        ],
                        "onPostShow": function () {
                            recordDlg.reCenter();
                        }.bind(this)
                    });
                }.bind(this));
            }

        }.bind(this));
    },
    loadReview : function (){
        this.app.action.ReviewAction.listWithJob(this.data.job, function (json) {
            this.reviewList = json.data;
            this._loadReview();
            this.initReview = true;
        }.bind(this), null, false);
    },
    _loadReview : function (){
        this.reviewArea.empty();
        this.reviewContentNode = new Element("div").inject(this.reviewArea)
        var reviewTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.reviewContentNode);

        var reviewTableTheadNode = new Element("thead").inject(reviewTableNode);
        var reviewTableTbodyNode = new Element("tbody").inject(reviewTableNode);
        var reviewTableTheadTrNode = new Element("tr").inject(reviewTableTheadNode);
        Array.each(["参阅人员列表", "操作"], function (text) {
            new Element("th", {"text": text}).inject(reviewTableTheadTrNode);
        });

        this.reviewList.each(function (review) {
            var trNode = new Element("tr").inject(reviewTableTbodyNode);
            trNode.store("data", review);

            Array.each([review.person], function (text) {
                new Element("td", {
                    text: text
                }).inject(trNode);
            });
            var tdOpNode = new Element("td").inject(trNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);

            deleteButton.addEvent("click", function (e) {

                _self = this;
                this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.ReviewAction.manageDelete(review.id,review.application,function(json){
                        _self.loadReview();
                    },null,false);
                    this.close();
                }, function(){
                    this.close();
                });

            }.bind(this));

        }.bind(this));
    },
    loadAttachement : function (){
        this.app.action.AttachmentAction.listWithJob(this.data.job, function (json) {
            this.attachmentList = json.data;
            this._loadAttachement();
            this.initAttachement = true;
        }.bind(this), null, false);
    },
    _loadAttachement : function (){
        this.attachementArea.empty();
        this.attachmentContentNode = new Element("div").inject(this.attachementArea)
        var attachmentTableNode = new Element("table.table",{
            "border" : 0,
            "cellpadding" : 5,
            "cellspacing" : 0
        }).inject(this.attachmentContentNode);

        var attachmentTableTheadNode = new Element("thead").inject(attachmentTableNode);
        var attachmentTableTbodyNode = new Element("tbody").inject(attachmentTableNode);
        var attachmentTableTheadTrNode = new Element("tr").inject(attachmentTableTheadNode);
        Array.each(["附件名称", "上传环节", "上传人", "上传时间", "标识","大小" ,"排序","操作"], function (text) {
            new Element("th", {"text": text}).inject(attachmentTableTheadTrNode);
        });

        var siteArr = [];
        this.attachmentList.each(function (attachment) {

            if(!siteArr.contains(attachment.site)) siteArr.push(attachment.site);
            var trNode = new Element("tr").inject(attachmentTableTbodyNode);
            trNode.store("data", attachment);

            Array.each([attachment.name, attachment.activityName, attachment.person.split("@")[0], attachment.createTime ,attachment.site,attachment["length"],attachment.orderNumber], function (text, index) {
                new Element("td", {
                    text: index ===5 ? this.getFileSize(text) : text
                }).inject(trNode);
            }.bind(this));

            var tdOpNode = new Element("td").inject(trNode);
            var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);
            deleteButton.addEvent("click", function (e) {
                _self = this;
                this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
                    _self.app.action.AttachmentAction.manageBatchDelete({"idList" : [attachment.id]},function(json){
                        _self.loadAttachement();
                    },null,false);
                    this.close();
                }, function(){
                    this.close();
                });
            }.bind(this));
            var downButton = new Element("button", {"text": "下载", "class": "button"}).inject(tdOpNode);
            downButton.addEvent("click", function () {

                var locate = window.location;
                var protocol = locate.protocol;
                var addressObj = layout.serviceAddressList["x_processplatform_assemble_surface"];
                var address = protocol+"//"+addressObj.host+(addressObj.port==80|| addressObj.port === ""? "" : ":"+addressObj.port)+addressObj.context;
                window.open(o2.filterUrl(address) + "/jaxrs/attachment/download/"+ attachment.id +"/stream")

            }.bind(this));


            var sortButton = new Element("button", {"text": "排序", "class": "button"}).inject(tdOpNode);
            sortButton.addEvent("click", function () {

                var sortNode = new Element("div", {"class": "control", "style": "margin:10px"});
                var inputNode = new Element("input", {"class": "input", "text": attachment.orderNumber});
                inputNode.inject(sortNode);

                var sortDlg = o2.DL.open({
                    "title": "排序号修改",
                    "width": "400px",
                    "height": "260px",
                    "mask": true,
                    "content": sortNode,
                    "container": null,
                    "positionNode": this.app.content,
                    "onQueryClose": function () {
                        sortNode.destroy();
                    }.bind(this),
                    "buttonList": [
                        {
                            "text": "确认",
                            "action": function () {
                                var orderNumber = inputNode.get("value");
                                this.app.action.AttachmentAction.changeOrderNumber(attachment.id,attachment.id,orderNumber,function( json ){
                                    this.loadAttachement();
                                }.bind(this),null,false);
                                sortDlg.close();

                            }.bind(this)
                        },
                        {
                            "text": "关闭",
                            "action": function () {
                                sortDlg.close();
                            }.bind(this)
                        }
                    ],
                    "onPostShow": function () {
                        sortDlg.reCenter();
                    }.bind(this)
                });

            }.bind(this));

        }.bind(this));


        var attachmentUploadDiv = new Element("div").inject(this.attachmentContentNode);

        var siteSelect = new Element("select",{"class":"select","style":"float:left"}).inject(attachmentUploadDiv);
        new Element("option",{value:"",text:""}).inject(siteSelect);
        siteArr.each(function(site){
            new Element("option",{value:site,text:site}).inject(siteSelect);
        });
        siteSelect.addEvent("change",function(){
            uploadSite.set("value",siteSelect.get("value"));
        });
        var uploadSite = new Element("input",{
            "class":"input",
            "placeholder":"对应上传的附件标识",
            "style" :"width:200px;float:left"
        }).inject(attachmentUploadDiv);
        var uploadButton = new Element("button", {"text": "上传", "class": "button"}).inject(attachmentUploadDiv);

        uploadButton.addEvent("click", function () {

            if(uploadSite.get("value")==""){
                this.app.notice("对应上传的附件标识不能为空","error");
                return false;
            }
            var options = {
                "title": "附件区域"
            };

            var site = uploadSite.get("value");

            var uploadAction = this.isCompletedWork?"uploadAttachmentByWorkCompleted":"uploadAttachment";
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.app.content, {
                "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                "method": uploadAction,
                "parameter": {
                    "id": this.data.id
                },
                "data":{
                    "site": site
                },
                "onCompleted": function(){
                    this.loadAttachement();
                }.bind(this)
            });
            upload.load();


        }.bind(this));
    },
    loadBusinessData : function (){
        this.app.action.DataAction.getWithJob(this.data.job, function (json) {
            this.workData = json.data;
            this._loadBusinessData();
            this.initBusinessData = true;
        }.bind(this), null, false);
    },
    _loadBusinessData : function (){
        var workData = this.workData;
        var workDataContentNode = new Element("div",{"style":"margin:5px"}).inject(this.businessDataArea);

        this.workDataContentNode = workDataContentNode;

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='fieldList'></td>" +
            "    <td styles='filterTableTitle' item='fieldList'></td>" +
            "    <td styles='filterTableTitle' lable='fieldType'></td>" +
            "    <td styles='filterTableTitle' item='fieldType'></td>" +
            "    <td styles='filterTableTitle' lable='fieldName'></td>" +
            "    <td styles='filterTableTitle' item='fieldName'></td>" +
            "</tr>" +
            "<tr>" +
            "    <td styles='filterTableTitle' lable='fieldValue'></td>" +
            "    <td styles='filterTableValue' item='fieldValue'  colspan=3></td>" +
            "   <td styles='filterTableValue' colspan=2><div item='action' ></div></td>" +
            "</tr>" +
            "</table><div item='workData'></div>"

        workDataContentNode.set("html", html);

        this.form = new MForm(workDataContentNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                fieldList: {
                    "text": "字段列表",
                    "type": "select",
                    "style": {"max-width": "150px"},
                    "selectValue": function () {
                        var arr = [""];
                        arr.append(Object.keys(workData));
                        return arr;
                    },
                    "event": {
                        "change": function (item, ev) {

                            var type = typeof(workData[item.getValue()]);
                            item.form.getItem("fieldType").setValue(type);
                            item.form.getItem("fieldName").setValue(item.getValue());

                            if(type === "object" || type === "array"){
                                item.form.getItem("fieldValue").setValue(JSON.stringify(workData[item.getValue()]));
                            }else {
                                item.form.getItem("fieldValue").setValue(workData[item.getValue()]);
                            }


                        }.bind(this)
                    }
                },
                fieldType: {
                    "text": "字段类型",
                    "type": "select",
                    "style": {"max-width": "150px"},
                    "selectValue": function () {
                        var array = ["","array","boolean","string","number","object"];
                        return array;
                    },
                    "event": {
                        "change": function (item, ev) {

                        }.bind(this)
                    }
                },
                fieldName: {text: "字段名", "type": "text", "style": {"min-width": "100px"}},
                fieldValue: {text: "字段值", "type": "textarea", "style": {"width": "100%","margin-left": "10px"}},

                action: {
                    "value": "修改", type: "button", className: "filterButton", event: {
                        click: function (e) {
                            var result = this.form.getResult(false, null, false, true, false);

                            var fieldName = result["fieldName"];
                            var fieldType = result["fieldType"];
                            var fieldValue = result["fieldValue"];

                            if (!fieldName) return false;
                            workData[fieldName] = (fieldType === "object" ? JSON.parse(fieldValue) : fieldValue);

                            _self = this;
                            this.app.confirm("warn", e.node, "提示", "确认是否修改", 350, 120, function () {
                                if(_self.isCompletedWork){
                                    _self.app.action.DataAction.updateWithWorkCompleted(_self.data.id,workData,function (json){},null,false);
                                }else{
                                    _self.app.action.DataAction.updateWithWork(_self.data.id,workData,function (json){},null,false);
                                }
                                _self.app.notice("success");

                                _self.loadScriptEditor();

                                this.close();
                            }, function(){
                                this.close();
                            });

                        }.bind(this)
                    }
                }
            }
        }, this.app, this.css);
        this.form.load();
        this.loadScriptEditor();
    },
    loadScriptEditor:function(){
        if( !this.workData )return;
        MWF.require("MWF.widget.JavascriptEditor", null, false);

        var workDataNode = this.formTableContainer.getElement('[item="workData"]');

        this.scriptEditor = new MWF.widget.JavascriptEditor(workDataNode, {
            "forceType": "ace",
            "option": { "mode" : "json" }
        });
        this.scriptEditor.load(function(){
            this.scriptEditor.setValue(JSON.stringify(this.workData, null, "\t"));
            this.scriptEditor.editor.setReadOnly(true);
            this.addEvent("afterResize", function () {
                this.resizeScript();
            }.bind(this))
            this.addEvent("queryClose", function () {

            }.bind(this))
            this.resizeScript();
        }.bind(this));
    },
    resizeScript: function () {
        var size = this.formTableContainer.getSize();
        var tableSize = this.formTableContainer.getElement('table').getSize();
        this.formTableContainer.getElement('[item="workData"]').setStyle("height", size.y - 200);
        if (this.scriptEditor && this.scriptEditor.editor) this.scriptEditor.editor.resize();
    },
    getFileSize: function (size) {
        if (!size)
            return "";
        var num = 1024.00; //byte
        if (size < num)
            return size + "B";
        if (size < Math.pow(num, 2))
            return (size / num).toFixed(2) + "K"; //kb
        if (size < Math.pow(num, 3))
            return (size / Math.pow(num, 2)).toFixed(2) + "M"; //M
        if (size < Math.pow(num, 4))
            return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
    },

});
