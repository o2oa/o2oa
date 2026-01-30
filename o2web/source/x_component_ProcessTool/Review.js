
MWF.xApplication.ProcessTool.Review = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    initialize: function(node, app, form,options){

        this.eform = form;
        this.perosn = this.eform.form.getItem("person").getValue();
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_ProcessTool/$Main/" + this.options.style + "/";

        this.css = this.app.css;
        this.lp = this.app.lp;

        this.className = this.options.className;

        this.action = this.app.action;
        this.node = $(node);
    },
    setContentSize: function () {

        var h = 400

        this.view.viewWrapNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
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
    loadOperate: function () {
        return;
        var _form = this.eform;
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='ok'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        this.fileterForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                ok: {
                    "value": "确认选择", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {


                            var arr = [];

                            var checkedItems = this.view.getCheckedItems();
                            checkedItems.each(function (item){
                                console.log(item)

                                arr.push(item.data.job);
                            }.bind(this));

                            _form.curitem.setValue(arr.join("\n"));
                            _form.data.jobList = arr;
                            _form.dlg.close();

                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
});
MWF.xApplication.ProcessTool.Review.View = new Class({
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
        filter.personList = [this.explorer.perosn];

        this.app.action.ReviewAction.V2ManageListPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _create: function () {

    },
    _open: function (data) {

    }

});
MWF.xApplication.ProcessTool.Review.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    }
});

