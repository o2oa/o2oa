MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.xApplication.ProcessTool.Task = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "className" : ""
    },

    initialize: function(node, app, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_ProcessTool/$Main/" + this.options.style + "/";

        this.css = this.app.css;
        this.lp = this.app.lp;

        this.className = this.options.className;

        this.action = this.app.action;
        this.node = $(node);
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.node.empty();
        this.createTopNode();
        this.createContainerNode();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun);
        this.loadView();

        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    loadView: function (filterData) {
        console.log(filterData)
        if (this.view) this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.ProcessTool[this.className].View(viewContainerNode, this, this, {
            templateUrl: this.path + this.className + "_listItem.json",
            "pagingEnable": true,
            "wrapView": true,
            "noItemText": this.lp.noItem,
            // "scrollType": "window",
            "pagingPar": {
                pagingBarUseWidget: true,
                position: ["bottom"],
                style: "blue_round",
                hasReturn: false,
                currentPage: this.options.viewPageNum,
                countPerPage: 15,
                visiblePages: 9,
                hasNextPage: true,
                hasPrevPage: true,
                hasTruningBar: true,
                hasJumper: true,
                returnText: "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: this.lp.firstPage,
                    lastPage: this.lp.lastPage
                },
                onPostLoad: function () {
                    this.setContentSize();
                }.bind(this)
            }
        }, {
            lp: this.lp
        });
        if (filterData) this.view.filterData = filterData;
        this.view.load();
    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setContentSize: function () {

        var nodeSize = this.app.node.getSize();
        var h = nodeSize.y - this.getOffsetY(this.node);
        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;
        h = h - this.getOffsetY(this.viewContainerNode);
        h = h - this.getOffsetY(this.app.node);

        var pageSize = (this.view && this.view.pagingContainerBottom) ? this.view.pagingContainerBottom.getComputedSize() : {totalHeight: 0};
        h = h - pageSize.totalHeight;

        this.view.viewWrapNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    createContainerNode: function () {
        this.createContent();
    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.node);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.node);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

        this.topOperateNode = new Element("div", {
            "styles": this.css.topOperateNode
        }).inject(this.topNode);



        this.loadOperate();
        this.loadFilter();

    },
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
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
                                    _self.view._removeTask(item.data);
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
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    loadFilter: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>" +
            "    <td styles='filterTableTitle' lable='key'></td>" +
            "    <td styles='filterTableValue' item='key'></td>" +
            "    <td styles='filterTableTitle' lable='activityName'></td>" +
            "    <td styles='filterTableValue' item='activityName'></td>" +
            "    <td styles='filterTableTitle' lable='processName'></td>" +
            "    <td styles='filterTableValue' item='processName'></td>" +
            "    <td styles='filterTableTitle' lable='credentialList'></td>" +
            "    <td styles='filterTableValue' item='credentialList'></td>" +
            "</tr>" +
            "<tr style='height: 45px;'>" +
            "    <td styles='filterTableTitle' lable='creatorUnitList'></td>" +
            "    <td styles='filterTableValue' item='creatorUnitList'></td>" +

            "    <td styles='filterTableTitle' lable='startTime'></td>" +
            "    <td styles='filterTableValue' item='startTime'></td>" +
            "    <td styles='filterTableTitle' lable='endTime'></td>" +
            "    <td styles='filterTableValue' item='endTime'></td>" +
            "    <td styles='filterTableValue' colspan='2'><div style='float:left' item='action'></div><div item='reset'></div></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                key: {text: "标题", "type": "text", "style": {"min-width": "150px"}},
                activityName: {text: "状态", "type": "text", "style": {"min-width": "150px"}},
                processName: {
                    text: "流程",
                    "type": "text",

                    "style": {"min-width": "150px"},
                    "event": {

                        "click": function (item, ev){
                            var v = item.getValue();
                            o2.xDesktop.requireApp("Selector", "package", function(){
                                var options = {
                                    "type": "Process",
                                    "values": v!==""?[item.getValue().split("|")[1]] : [],
                                    "count": 1,
                                    "onComplete": function (items) {
                                        var arr = [];
                                        var arr2 = [];
                                        items.each(function (data) {
                                            arr.push(data.data);
                                            arr2.push(items[0].data.name+"|"+items[0].data.id);
                                        });
                                        item.setValue(arr2.join(","));
                                    }.bind(this)
                                };
                                new o2.O2Selector(this.app.desktop.node, options);
                            }.bind(this),false);
                        }.bind(this)}
                },
                credentialList: {
                    "text": lp.person,
                    "type": "org",
                    "orgType": "identity",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "150px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                creatorUnitList: {
                    "text": "部门",
                    "type": "org",
                    "orgType": "unit",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "150px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                startTime: {
                    text: lp.startTime,
                    "tType": "date",
                    "style": {"min-width":"150px"}
                },
                endTime: {
                    text: lp.endTime,
                    "tType": "date",
                    "style": {"min-width":"150px"}
                },
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.form.getResult(false, null, false, true, false);
                            for (var key in result) {
                                if (!result[key]) {
                                    delete result[key];
                                } else if (key === "activityName" && result[key].length > 0) {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["activityNameList"] = [result[key]];
                                    delete result[key];
                                }else if (key === "processName" && result[key] !== "") {
                                    //result[key] = result[key][0].split("@")[1];
                                    result["processList"] = [result[key].split("|")[1]];
                                    delete result[key];
                                }else if (key === "endTime" && result[key] !== "") {
                                    result[key] = result[key][0] + " 23:59:59"

                                }
                            }
                            console.log(result)
                            this.loadView(result);
                        }.bind(this)
                    }
                },
                reset: {
                    "value": lp.reset, type: "button", className: "filterButtonGrey", event: {
                        click: function () {
                            this.form.reset();
                            this.loadView();
                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.form.load();
    },
});
MWF.xApplication.ProcessTool.Task.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ProcessTool[this.app.className].Document(this.viewNode, data, this.explorer, this, null, index);
    },
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
        this.app.action.TaskAction.manageListFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _getTask : function (id){

        var taskData;
        this.app.action.TaskAction.get(id,function (json){
            taskData = json.data;
        },null,false);

        return taskData;
    },
    _removeTask : function (data){
        this.app.action.TaskAction.manageDelete(data.id,function (){},null,false);
    },
    _flowTask : function (taskId,routeName, opinion){
        var data = {
            "routeName": routeName,
            "opinion": opinion
        };
        this.app.action.TaskAction.processing(taskId,data,function(json){

        },null,false);
    },
    _resetTask: function (taskId, identityList,flag,opinion) {

        var keep = true;
        if(flag==="no"){
            keep = false;
        }

        var nameList = [];
        identityList.each(function (identity) {
            nameList.push(identity.data.distinguishedName);
        });
        var data = {
            "routeName":"转交",
            "opinion":opinion,
            "identityList":nameList,
            "keep" : keep
        }
        this.app.action.TaskAction.V2Reset(taskId,data,function(json){
        },null,false);

    },

    _create: function () {

    },
    _open: function (data) {
        var options = {"workId": data.work};
        this.app.app.desktop.openApplication(null, "process.Work", options);
    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    }


});
MWF.xApplication.ProcessTool.Task.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 1)
    },
    mouseoutDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 0)
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {

    },
    open: function () {
        this.view._open(this.data);
    },
    flow : function (ev){
        var _self = this;
        var taskData = this.view._getTask(this.data.id);
        var processNode = new Element("div");
        var dlg = o2.DL.open({
            "title": "任务提交",
            "width": "600px",
            "height": "360px",
            "mask": true,
            "content": processNode,
            "container": null,
            "positionNode": this.explorer.app.content,
            "onQueryClose": function () {
                processNode.destroy();
            }.bind(this),
            "onPostShow": function () {
                dlg.reCenter();

                o2.xDesktop.requireApp("process.Work", "Processor", function(){
                    new o2.xApplication.process.Work.Processor(processNode, taskData, {
                        "style": "task",
                        "onCancel": function(){
                            dlg.close();
                        },
                        "onSubmit": function(routeName, opinion){
                            _self.view._flowTask(taskData.id,routeName, opinion);
                            dlg.close();
                            _self.view.app.loadView();
                        }
                    });
                }.bind(this));
            }.bind(this)
        });
    },
    reset : function (ev){

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
                        _self.view._resetTask(_self.data.id, items,flag,opinion);
                        _self.view.app.loadView();
                        this.close();
                    }else{
                        this.content.getElement("#reset_checkInfor").set("text", "请选择是否保留自身待办！").setStyle("color", "red");
                    }
                }, function(){
                    this.close();
                });

            }.bind(this)
        };
        new MWF.O2Selector(this.explorer.app.content, opt);

    },
    remove : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.explorer.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {

            _self.view._removeTask(_self.data);
            _self.view.lockNodeStyle = false;

            this.close();
            _self.view.app.loadView();

        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    }
});
