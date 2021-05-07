MWF.xDesktop.requireApp("process.TaskCenter", "TaskCompletedList", null, false);
MWF.xApplication.process.TaskCenter.ReadList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList,

    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
            if (this.filterData){
                this.filterListNode.empty();
                var data = {};
                Object.each(this.filterData, function(v, key){
                    if (key!="key"){
                        if (v) {
                            //data[this.app.options.filterMap[key]] = v.value;
                            if (!data[this.app.options.filterMap[key]]) data[this.app.options.filterMap[key]] = [];
                            data[this.app.options.filterMap[key]].push(v.value);
                            this.createFilterItemNode(key, v);
                        }
                    }else{
                        data.key = v;
                    }
                }.bind(this));
                if (this.filterData.key){
                    this.createFilterItemNode("key", {"name": this.filterData.key});
                }

                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listReadFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listReadNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listReadApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.ReadList.Item(task, this)
    },
    _getFilterCount: function(callback){
        this.app.action.listReadFilterCount(function(json){
            if (callback) callback(json);
        });
    }
});
MWF.xApplication.process.TaskCenter.ReadList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList.Item,
    setFlowChart: function(data){
        var idx = 0;
        data.workLogTokenList = {};
        data.workLogList.each(function(worklog){
            data.workLogTokenList[worklog.fromActivityToken] = worklog;

            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, worklog.fromActivityToken == data.read.activityToken);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    _getSimpleJobByTaskComplete: function(callback){
        this.list.app.action.getSimpleJobByRead(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },
    //loadActions: function(){
    //    this.editNode = new Element("div", {"styles": this.list.css.titleActionReadedNode, "title": "设置为已阅"}).inject(this.titleActionNode);
    //    this.closeNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.titleActionNode);
    //},
    loadActions: function(){
        this.showTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionReadedNode, "title": this.list.app.lp.setReaded }).inject(this.actionContentNode);
        this.closeTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.actionContentNode);
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){this.showAction();}.bind(this),
            "mouseout": function(){this.hideAction();}.bind(this)
        });

        if (this.showTaskCompletedNode){
            this.showTaskCompletedNode.addEvent("click", function(e){
                this.setReadedClose(e);
            }.bind(this));
        }
        if (this.closeTaskCompletedNode){
            this.closeTaskCompletedNode.addEvent("click", function(e){
                this.closeTaskCompleted();
            }.bind(this));
        }

        if (this.rightContentNode){
            this.rightContentNode.addEvent("click", function(e){
                this.showTaskCompleted(e);
            }.bind(this));
        }
    },
    setEditTaskCompleledNodes: function(data) {
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.mainContentNode);
        //    this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.node);
        this.workInforNode = new Element("div", {"styles": this.list.css.workInforNode}).inject(this.mainContentNode);
        //    this.myDoneInforNode = new Element("div", {"styles": this.list.css.myDoneInforNode}).inject(this.node);

        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.workInforNode, {
                "style": "xApp_Task_infor",
                "where": "before",
                "distance": 30,
                "friction": 4,
                "axis": {"x": false, "y": true}
            });
        }.bind(this));

        this.setFlowInfor(data);
        this.setWorkInfor(data);
        this.setReadedButton();
    },
    setReadedButton: function(){
        this.setReadedAction = Element("div", {"styles": this.list.css.setReadedAction, "text": this.list.app.lp.setReaded}).inject(this.mainContentNode);
        this.setReadedAction.addEvent("click", function(e){
            this.setReaded(e);
        }.bind(this));
    },
    setReadedClose: function(e){
        var _self = this;
        var text = this.list.app.lp.setReadedConfirmContent.replace("{title}", this.data.title );
        this.list.app.confirm("infor", e, this.list.app.lp.setReadedConfirmTitle, text, 350, 230, function(){
            _self.list.app.action.setReaded(function(){
                this.node.destroy();
                this.list.refresh();
            }.bind(_self), null, _self.data.id, _self.data);
            this.close();
        }, function(){
            this.close();
        }, null, this.list.app.content);
    },
    setReaded: function(e){
        var _self = this;
        // var text = "您确定要将“"+this.data.title+"”标记为已阅吗？"
        var text = this.list.app.lp.setReadedConfirmContent.replace("{title}", this.data.title );
        this.list.app.confirm("infor", e, this.list.app.lp.setReadedConfirmTitle, text, 350, 230, function(){
            _self.list.app.action.setReaded(function(){
                this.closeTaskCompleted(function(){
                    this.node.destroy();
                    this.list.refresh();
                }.bind(this));
            }.bind(_self), null, _self.data.id, _self.data);
            this.close();
        }, function(){
            this.close();
        }, null, this.list.app.content);
    },
    openWorkByTaskCompleted: function(e, id){
        var _self = this;
        var options = {"workId": id, "readonly": true, "appId": "process.Work"+id,
            "onQueryLoadForm" : function () {
                this.appForm.addEvent("afterReaded", function () {
                    _self.node.destroy();
                    _self.list.refresh();
                })
            }, "onPostLoadForm" :function () {

            }
        };
        this.list.app.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleteedByTaskCompleted: function(e, id){
        var _self = this;
        var options = {"workCompletedId": id, "readonly": true, "appId": "process.Work"+id,
            "onQueryLoadForm" : function () {
                this.appForm.addEvent("afterReaded", function () {
                    _self.node.destroy();
                    _self.list.refresh();
                })
            }, "onPostLoadForm" :function () {

            }
        };
        this.list.app.desktop.openApplication(e, "process.Work", options);
    },
    closeTaskCompleted: function(callback){

        this.closeTaskCompletedNode.setStyle("display", "none");

        this.flowInforLeftNode.destroy();
        this.flowInforRightNode.destroy();
        this.flowInforContentNode.destroy();
        this.flowInforScrollNode.destroy();

        this.flowInforNode.destroy();
        this.workInforNode.destroy();
        this.setReadedAction.destroy();
        //     this.processNode.destroy();

        this.flowInforScrollFx = null;
        this.flowInforLeftNode = null;
        this.flowInforRightNode = null;
        this.flowInforScrollNode = null;
        this.flowInforContentNode = null;
        this.flowInforNode = null;
        this.workInforNode = null;
        this.setReadedAction = null;

        delete this.flowInforScrollFx;
        delete this.flowInforLeftNode;
        delete this.flowInforRightNode;
        delete this.flowInforScrollNode;
        delete this.flowInforContentNode;
        delete this.flowInforNode;
        delete this.workInforNode;
        delete this.setReadedAction;

        var p = this.node.getPosition(this.list.app.content);
        this.list.css.itemNode_edit_from.top = ""+ p.y+"px";
        this.list.css.itemNode_edit_from.left = ""+ p.x+"px";

        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeIn,
            "onComplete": function(){
                this.nodeClone.destroy();
                this.nodeClone = null;
                this.list.app.content.unmask();
                this.mainContentNode.setStyles(this.list.css.itemMainContentAreaNode);
                this.mainContentNode.setStyle("opacity", 1);
                document.body.setStyle("-webkit-overflow-scrolling", "touch");
                this.list.app.removeEvent("resize", this.resizeEditNodeFun);

                this.showTaskCompletedNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit_from);
    }
});