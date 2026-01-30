MWF.xDesktop.requireApp("ProcessTool", "Task", null ,false);
MWF.xApplication.ProcessTool.ReadDone = new Class({
    Extends: MWF.xApplication.ProcessTool.Task,
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
                                    _self.view._removeReadDone(item.data);
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
});
MWF.xApplication.ProcessTool.ReadDone.View = new Class({
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
        this.app.action.ReadCompletedAction.manageListFilterPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))

    },
    _setReadDoneOpinion : function (id,opinion){
        this.app.action.ReadCompletedAction.manageUpdate(id,{"opinion":opinion},function(json){
        },null,false);
    },
    _removeReadDone : function (data){
        this.app.action.ReadCompletedAction.manageDelete(data.id,function (){},null,false);
    },
    _create: function () {

    },
    _open: function (data) {
        var options = {"workId": data.work};
        this.app.app.desktop.openApplication(null, "process.Work", options);
    },

});
MWF.xApplication.ProcessTool.ReadDone.Document = new Class({
    Extends: MWF.xApplication.ProcessTool.Task.Document,

    open: function () {
        this.view._open(this.data);
    },
    setOpinion : function (e){
        var ideaNode = new Element("div", {"style": "margin:10px"});
        var textareaNode = new Element("textarea", {"style": "min-width:100%;height: 100px", "text": this.data.opinion});
        textareaNode.inject(ideaNode);

        var ideaDlg = o2.DL.open({
            "title": "意见修改",
            "width": "400px",
            "height": "260px",
            "mask": true,
            "content": ideaNode,
            "container": null,
            "positionNode": this.explorer.app.content,
            "onQueryClose": function () {
                ideaNode.destroy();
            }.bind(this),
            "buttonList": [
                {
                    "text": "确认",
                    "action": function () {
                        this.view._setReadDoneOpinion(this.data.id,textareaNode.get("value"));
                        this.view.app.loadView();
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

    },
    remove : function (e){

        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.explorer.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {

            _self.view._removeReadDone(_self.data);
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
