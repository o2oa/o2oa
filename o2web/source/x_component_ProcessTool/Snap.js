MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.xApplication.ProcessTool.Snap = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
    loadOperate: function () {
        var lp = MWF.xApplication.ProcessTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='restore'></td>" +
            "    <td styles='filterTableValue' item='import'></td>" +
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
                                    _self.view._remove(item.data);
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
                restore: {
                    "value": "恢复", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要恢复吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._restore(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("恢复成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                import: {
                    "value": "导入", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {


                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要导入吗？", 350, 120, function () {
                                this.close();

                                o2.Actions.get("x_processplatform_assemble_surface").action.actions.uploadSnap = {
                                    "enctype": "formData",
                                    "method": "POST",
                                    "uri": "/jaxrs/snap/upload"
                                }

                                o2.require("o2.widget.Upload", null, false);
                                var upload = new o2.widget.Upload(_self.app.content, {
                                    "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                                    "method": "uploadSnap",
                                    "parameter": {
                                    },
                                    "data":{
                                    },
                                    "onCompleted": function(){
                                        _self.form.app.notice("导入成功","success");
                                        _self.loadView();
                                    }.bind(this)
                                });
                                upload.load();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    }
});
MWF.xApplication.ProcessTool.Snap.View = new Class({
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
        this.app.action.SnapAction.manageListFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _remove : function (data){
        this.app.action.SnapAction.delete(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _restore : function (data){
        this.app.action.SnapAction.restore(data.id,function (){},null,false);
    },
    _open: function (data) {
        console.log(data)
    },
    _download : function (data){
        var locate = window.location;
        var protocol = locate.protocol;
        var addressObj = layout.serviceAddressList["x_processplatform_assemble_surface"];
        var address = protocol+"//"+addressObj.host+(addressObj.port==80|| addressObj.port === ""? "" : ":"+addressObj.port)+addressObj.context;
        window.open(o2.filterUrl(address) + "/jaxrs/snap/" + data.id + "/download")
    }
});
MWF.xApplication.ProcessTool.Snap.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    },
    download : function (){
        this.view._download(this.data);
    },
    restore : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.explorer.app.confirm("warn", e, "提示", "确认是否恢复文件", 350, 120, function () {

            _self.view._restore(_self.data);
            _self.view.lockNodeStyle = false;

            this.close();
            _self.view.app.loadView();

        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    },
    remove : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.explorer.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {

            _self.view._remove(_self.data);
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
