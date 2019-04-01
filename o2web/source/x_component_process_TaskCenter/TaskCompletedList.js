MWF.xDesktop.requireApp("process.TaskCenter", "List", null, false);
MWF.xApplication.process.TaskCenter.TaskCompletedList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List,
    createAppFilterNodes: function(){
        var allApp = {"name": this.app.lp.all, "application": "", "count": 0};
        this.allAppFilterNode = this.createAppFilterNode(allApp, "appFilterNode_current");
        this.currentFilterNode = this.allAppFilterNode;

        this.filterListNode = new Element("div", {"styles": this.css.filterListNode}).inject(this.applicationFilterAreaNode);
    },

    createFilterItemNode: function(key, v){
        var _self = this;

        var node = new Element("div", {"styles": this.css.filterListItemNode}).inject(this.filterListNode);
        var actionNode = new Element("div", {"styles": this.css.filterListItemActionNode}).inject(node);
        var textNode = new Element("div", {"styles": this.css.filterListItemTextNode}).inject(node);
        textNode.set("text", this.app.lp[key]+": "+ v.name);

        actionNode.store("key", key);
        node.addEvents({
            "mouseover": function(){
                this.setStyles(_self.css.filterListItemNode_over);
                this.getLast().setStyles(_self.css.filterListItemTextNode_over);
                this.getFirst().setStyles(_self.css.filterListItemActionNode_over);
            },
            "mouseout": function(){
                this.setStyles(_self.css.filterListItemNode);
                this.getLast().setStyles(_self.css.filterListItemTextNode);
                this.getFirst().setStyles(_self.css.filterListItemActionNode);
            }
        });
        actionNode.addEvent("click", function(){
            var key = this.retrieve("key");
            if (_self.filterData[key]) _self.filterData[key] = null;
            delete _self.filterData[key];
            this.destroy();
            _self.refilter();
        });
    },
    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
            if (this.filterData){
                this.filterListNode.empty();
                var data = {};
                Object.each(this.filterData, function(v, key){
                    if (key!=="key"){
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
                this.app.action.listTaskCompletedFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskCompletedNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listTaskCompletedApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.TaskCompletedList.Item(task, this)
    }

});

MWF.xApplication.process.TaskCenter.TaskCompletedList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List.Item,

    loadActions: function(){
        this.showTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionShowNode}).inject(this.actionContentNode);
        this.closeTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.actionContentNode);
    },
    setTimeIcon: function(){
        if (this.data.completed){
            this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/time/pic_ok.png)");
            return true;
        }
    },

    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){this.showAction();}.bind(this),
            "mouseout": function(){this.hideAction();}.bind(this)
        });

        if (this.showTaskCompletedNode){
            this.showTaskCompletedNode.addEvent("click", function(e){
                this.showTaskCompleted();
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
    setFlowChart: function(data){
        var idx = 0;
        data.workLogTokenList = {};
        var current = data.workLogList.shift();
        data.workLogList.push(current);
        data.workLogList.each(function(worklog){
            data.workLogTokenList[worklog.fromActivityToken] = worklog;

            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, worklog.fromActivityToken == data.taskCompleted.activityToken);
                idx++;
            }
        }.bind(this));
        return idx;
    },

    showAction: function(){
        //if (this.showTaskCompletedNode) this.showTaskCompletedNode.fade("in");
    },
    hideAction: function(){
        //if (this.showTaskCompletedNode) this.showTaskCompletedNode.fade("out");
    },
    _getJobByTaskComplete: function(){
        this.list.app.action.getSimpleJobByTaskCompleted(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },
    showTaskCompleted: function(){
        if (layout.mobile){
            this.showTaskCompleted_mobile();
        }else{
            this.showTaskCompleted_pc();
        }
    },
    showTaskCompleted_mobile: function(){
        if (!this.nodeClone){
            this._getSimpleJobByTaskComplete(function(data){
                this.nodeClone = this.mainContentNode.clone(false);
                this.nodeClone.inject(this.mainContentNode, "after");
                this.mainContentNode.setStyles(this.list.css.itemNode_edit_from_mobile);
                this.mainContentNode.position({
                    relativeTo: this.nodeClone,
                    position: "topleft",
                    edge: "topleft"
                });

                this.showEditTaskCompletedNode(data);
            }.bind(this));
        }
    },
    showTaskCompleted_pc: function(){
        if (!this.nodeClone){
            this.list.app.content.mask({
                "destroyOnHide": true,
                "id": "mask_"+this.data.id,
                "style": this.list.css.maskNode
            });

            this._getSimpleJobByTaskComplete(function(data){
                this.nodeClone = this.mainContentNode.clone(false);
                this.nodeClone.inject(this.mainContentNode, "after");
                this.mainContentNode.setStyles(this.list.css.itemNode_edit_from);
                this.mainContentNode.position({
                    relativeTo: this.nodeClone,
                    position: "topleft",
                    edge: "topleft"
                });

                this.showEditTaskCompletedNode(data);
            }.bind(this));
        }
    },

    _getSimpleJobByTaskComplete: function(callback){
        this.list.app.action.getSimpleJobByTaskCompleted(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },

    showEditTaskCompletedNode: function(data, callback){
        if (layout.mobile){
            this.showEditTaskCompletedNode_mobile(data, callback);
        }else{
            this.showEditTaskCompletedNode_pc(data, callback);
        }
    },
    showEditTaskCompletedNode_mobile: function(data, callback){
        var p = this.list.app.tabAreaNode.getPosition(this.list.app.content);
        var contentSize = this.list.app.contentNode.getSize();
        var tabSize = this.list.app.tabAreaNode.getSize();
        var y = contentSize.y+tabSize.y;

        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";
        this.list.css.itemNode_edit.width = ""+ contentSize.x+"px";
        this.list.css.itemNode_edit.height = ""+ y+"px";

        document.body.setStyle("-webkit-overflow-scrolling", "auto");

        this.showTaskCompletedNode.setStyle("display", "none");
        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut,
            "onComplete": function(){
                this.resizeEditNodeFun = this.resizeEditNode.bind(this);
                this.list.app.addEvent("resize", this.resizeEditNodeFun);
                this.setEditTaskCompleledNodes(data);

                this.closeTaskCompletedNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit);
    },
    showEditTaskCompletedNode_pc: function(data, callback){
        var p = this.getEditNodePosition();
        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";

        this.showTaskCompletedNode.setStyle("display", "none");
        var morph = new Fx.Morph(this.mainContentNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut,
            "onComplete": function(){
                this.resizeEditNodeFun = this.resizeEditNode.bind(this);
                this.list.app.addEvent("resize", this.resizeEditNodeFun);
                this.setEditTaskCompleledNodes(data);

                this.closeTaskCompletedNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit);
    },
    setEditTaskCompleledNodes: function(data){
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.mainContentNode);
        //    this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.node);
        this.workInforNode = new Element("div", {"styles": this.list.css.workInforNode}).inject(this.mainContentNode);
        //    this.myDoneInforNode = new Element("div", {"styles": this.list.css.myDoneInforNode}).inject(this.node);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.workInforNode, {
                "style":"xApp_Task_infor", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        this.setFlowInfor(data);
        this.setWorkInfor(data);
        //    this.setProcessor();
    },
    setWorkInfor: function(data){
        var lp = this.list.app.lp;
        var taskCompletedWorkInforTitleNode = new Element("div", {
            "styles": this.list.css.taskCompletedWorkInforTitleNode,
            "text": lp.currentFileStatus
        }).inject(this.workInforNode);

        data.workList.each(function(work){
            var log = data.workLogTokenList[work.activityToken];
            if (log){
                var users = [];
                log.taskList.each(function(task){
                    var unit = task.department || task.unit || "";
                    unit = unit.substring(0, unit.indexOf("@"));
                    users.push(task.person.substring(0, task.person.indexOf("@"))+((unit) ? "("+unit+")" : ""));
                }.bind(this));

                var html = "<table border=\"0\" width=\"96%\" align=\"center\"><tr>" +
                    "<td style=\"white-space: normal;word-break: break-all;word-wrap:break-word;\">"+
                    ""+lp.fileat+"<font style=\"color: #00F\"> "+log.fromTime.substr(0, log.fromTime.lastIndexOf(":"))+" </font>"+lp.flowto+"<font style=\"color: #00F\"> ["+log.fromActivityName+"] </font>" +
                    "<br/><font style=\"font-weight:bold\">"+lp.list_owner+": </font>"+users.join(", ")+"</td>" +
                    "<td style=\"width:60px; text-align:right\"><div id=\""+work.id+"\">打开</div></td>" +
                    "</tr></table>";

                var taskCompletedWorkInforNode = new Element("div", {
                    "styles": this.list.css.taskCompletedWorkInforNode,
                    "html": html
                }).inject(this.workInforNode);
                var table = taskCompletedWorkInforNode.getElement("table");
                //table

                var openNode = taskCompletedWorkInforNode.getElement("div");
                if (openNode) {
                    openNode.setStyles(this.list.css.taskCompletedOpenNode);
                    var _self = this;
                    openNode.addEvent("click", function(e){

                        var id = this.get("id");
                        _self.openWorkByTaskCompleted(e, id);
                    });
                }
            }
        }.bind(this));

        data.workCompletedList.each(function(work){
            //         var log = data.workLogTokenList[work.activityToken];
            //         if (log){
            var html = "<table border=\"0\" width=\"90%\" align=\"center\"><tr>" +
                "<td>“"+work.title+"”"+lp.fileat+""+work.completedTime+""+lp.completed+"</td>" +
                "<td><div id=\""+work.id+"\">打开</div></td>" +
                "</tr></table>";

            var taskCompletedWorkInforNode = new Element("div", {
                "styles": this.list.css.taskCompletedWorkInforNode,
                "html": html
            }).inject(this.workInforNode);

            var openNode = taskCompletedWorkInforNode.getElement("div");
            if (openNode) {
                openNode.setStyles(this.list.css.taskCompletedOpenNode);
                var _self = this;
                openNode.addEvent("click", function(e){

                    var id = this.get("id");
                    _self.openWorkCompleteedByTaskCompleted(e, id);
                });
            }
            //         }
        }.bind(this));
    },
    openWorkByTaskCompleted: function(e, id){
        var options = {"workId": id, "readonly": true, "appId": "process.Work"+id};
        this.list.app.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleteedByTaskCompleted: function(e, id){
        var options = {"workCompletedId": id, "readonly": true, "appId": "process.Work"+id};
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
        //     this.processNode.destroy();

        this.flowInforScrollFx = null;
        this.flowInforLeftNode = null;
        this.flowInforRightNode = null;
        this.flowInforScrollNode = null;
        this.flowInforContentNode = null;
        this.flowInforNode = null;
        this.workInforNode = null;

        delete this.flowInforScrollFx;
        delete this.flowInforLeftNode;
        delete this.flowInforRightNode;
        delete this.flowInforScrollNode;
        delete this.flowInforContentNode;
        delete this.flowInforNode;
        delete this.workInforNode;

        this.closeTaskCompletedDom();
    },

    closeTaskCompletedDom: function(callback){
        var p = this.nodeClone.getPosition(this.list.app.content);
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