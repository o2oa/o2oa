MWF.xDesktop.requireApp("ProcessTool", "Work", null, false);
MWF.xApplication.ProcessTool.WorkCompleted = new Class({
    Extends: MWF.xApplication.ProcessTool.Work,
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
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
                                    _self.view._removeWorkCompleted(item.data);
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
                                                        "workCompleted":workId,
                                                        "personList":arr
                                                    }
                                                    _self.view._addWorkCompletedReview(data);
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
MWF.xApplication.ProcessTool.WorkCompleted.View = new Class({
    Extends: MWF.xApplication.ProcessTool.Work.View,

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
        this.app.action.WorkCompletedAction.manageListFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _removeWorkCompleted : function (data){
        this.app.action.WorkCompletedAction.manageDelete(data.id,function (){},null,false);
    },
    _snap : function (data){
        this.app.action.SnapAction.typeAbandonedWorkCompleted(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _addWorkCompletedReview : function (data){
        this.app.action.ReviewAction.createWithWorkCompleted(data,function (){},null,false);
    },
    _open: function (data) {
        var options = {"workCompletedId": data.id};
        this.app.app.desktop.openApplication(null, "process.Work", options);
    },
    _listRollbackWithWorkOrWorkCompleted : function (id){
        var data ;

        this.app.action.WorkLogAction.listRollbackWithWorkOrWorkCompleted(id,function (json){
            data = json.data;
        },null,false);
        return data;
    },
    _rollback : function (id,workLog,processing,opinion,idList){
        this.app.action.WorkCompletedAction.rollback(id,{
            "workLog": workLog,
            "distinguishedNameList": idList,
            "processing": processing,
            "opinion": opinion
        },function (json){
            data = json.data;
        },null,false);
    }

});
MWF.xApplication.ProcessTool.WorkCompleted.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Work.Document,

    open: function () {
        this.view._open(this.data);
    },
    edit : function (){

        var form = new MWF.xApplication.ProcessTool.WorkCompleted.EditForm({app: this.app.app}, this.data );
        form.open();

    },
    rollback : function (){

        var node = new Element("div", { "styles": this.css.rollbackAreaNode });
        var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:left;\">请选择文件要回溯到的位置：</div>";
        html += "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:right;\"><input class='rollback_flowOption' checked type='checkbox' />并尝试继续流转</div>";
        html += "<div style=\"clear:both; margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
        node.set("html", html);
        var rollbackItemNode = node.getLast();
        this.getRollbackLogs(rollbackItemNode);
        //node.inject(this.app.content);

        var dlg = o2.DL.open({
            "title": "回溯",
            "style": "user",
            "isResize": false,
            "content": node,
            "width": 600,
            "height" : 400,
            "buttonList": [
                {
                    "type": "ok",
                    "text": "确定",
                    "action": function (d, e) {
                        this.doRollback(node, e, dlg);
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": "取消",
                    "action": function () { dlg.close(); }
                }
            ]
        });
    },
    doRollback: function (node, e, dlg) {
        var rollbackItemNode = node.getLast();
        var items = rollbackItemNode.getChildren();
        var flowOption = (node.getElement(".rollback_flowOption").checked);
        var _self = this;
        for (var i = 0; i < items.length; i++) {
            if (items[i].retrieve("isSelected")) {
                var text = "您确定要将流程回溯到“{log}”状态吗？（流程回溯会清除此状态之后的所有信息）";
                var log = items[i].retrieve("log");
                var checks = items[i].getElements("input:checked");
                var idList = [];
                checks.each(function (check) {
                    var id = check.get("value");
                    if (idList.indexOf(id) == -1) idList.push(id);
                });

                var opinion = "回溯到:"+log.fromActivityName;

                text = text.replace("{log}", log.fromActivityName + "(" + log.arrivedTime + ")");
                this.explorer.app.confirm("infor", e, "流程回溯确认", text, 450, 120, function () {

                    // console.log(log.id)
                    // console.log(flowOption)
                    // //console.log(dlg)
                    // console.log(idList)
                    _self.view._rollback(_self.data.id, log.id,!!flowOption,opinion,idList);


                    _self.view.app.loadView();

                    dlg.close();

                    this.close();
                }, function () {
                    this.close();
                });
                break;
            }
        }
    },
    getRollbackLogs: function (rollbackItemNode) {
        var _self = this;

        var dataList = this.view._listRollbackWithWorkOrWorkCompleted(this.data.id);

        dataList.each(function (log) {
            if (!log.splitting && log.connected) {
                var node = new Element("div", { "styles": this.css.rollbackItemNode }).inject(rollbackItemNode);
                node.store("log", log);
                var iconNode = new Element("div", { "styles": this.css.rollbackItemIconNode }).inject(node);
                var contentNode = new Element("div", { "styles": this.css.rollbackItemContentNode }).inject(node);

                var div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
                var activityNode = new Element("div", { "styles": this.css.rollbackItemActivityNode, "text": log.fromActivityName }).inject(div);
                var timeNode = new Element("div", { "styles": this.css.rollbackItemTimeNode, "text": log.arrivedTime }).inject(div);
                div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
                var taskTitleNode = new Element("div", { "styles": this.css.rollbackItemTaskTitleNode, "text":  "办理人: " }).inject(div);

                if (log.taskCompletedList.length) {
                    log.taskCompletedList.each(function (o) {
                        var text = o2.name.cn(o.person) + "(" + o.completedTime + ")";
                        var check = new Element("input", {
                            "value": o.identity,
                            "type": "checkbox",
                            "disabled": true,
                            "styles": this.css.rollbackItemTaskCheckNode
                        }).inject(div);
                        check.addEvent("click", function (e) {
                            e.stopPropagation();
                        });
                        var taskNode = new Element("div", { "styles": this.css.rollbackItemTaskNode, "text": text }).inject(div);
                    }.bind(this));
                } else {
                    var text = "系统自动处理";
                    var taskNode = new Element("div", { "styles": this.css.rollbackItemTaskNode, "text": text }).inject(div);
                }

                node.addEvents({
                    "mouseover": function () {
                        var isSelected = this.retrieve("isSelected");
                        if (!isSelected) this.setStyles(_self.css.rollbackItemNode_over);
                    },
                    "mouseout": function () {
                        var isSelected = this.retrieve("isSelected");
                        if (!isSelected) this.setStyles(_self.css.rollbackItemNode)
                    },
                    "click": function () {
                        var isSelected = this.retrieve("isSelected");
                        if (isSelected) {
                            _self.setRollBackUnchecked(this);
                        } else {
                            var items = rollbackItemNode.getChildren();
                            items.each(function (item) {
                                _self.setRollBackUnchecked(item);
                            });
                            _self.setRollBackChecked(this);
                        }
                    }
                });
            }
        }.bind(this));

    },
    setRollBackChecked: function (item) {
        item.store("isSelected", true);
        item.setStyles(this.css.rollbackItemNode_current);

        item.getFirst().setStyles(this.css.rollbackItemIconNode_current);

        var node = item.getLast().getFirst();
        node.getFirst().setStyles(this.css.rollbackItemActivityNode_current);
        node.getLast().setStyles(this.css.rollbackItemTimeNode_current);

        node = item.getLast().getLast();
        node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode_current);
        node.getLast().setStyles(this.css.rollbackItemTaskNode_current);

        var checkeds = item.getElements("input");
        if (checkeds){
            checkeds.set("checked", true);
            checkeds.set("disabled", false);
        }
    },
    setRollBackUnchecked: function (item) {
        item.store("isSelected", false);
        item.setStyles(this.css.rollbackItemNode);

        item.getFirst().setStyles(this.css.rollbackItemIconNode);

        var node = item.getLast().getFirst();
        node.getFirst().setStyles(this.css.rollbackItemActivityNode);
        node.getLast().setStyles(this.css.rollbackItemTimeNode);

        node = item.getLast().getLast();
        node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode);
        node.getLast().setStyles(this.css.rollbackItemTaskNode);

        var checkeds = item.getElements("input");
        if (checkeds) {
            checkeds.set("checked", false);
            checkeds.set("disabled", true);
        }
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

                }else {
                    _self.view._removeWorkCompleted(_self.data);
                }

                _self.view.lockNodeStyle = false;
                this.close();
                _self.view.app.loadView();

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
MWF.xApplication.ProcessTool.WorkCompleted.EditForm = new Class({
    Extends: MWF.xApplication.ProcessTool.Work.EditForm,
    loadTab : function (){

        this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.formTableArea);
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
    }
});
