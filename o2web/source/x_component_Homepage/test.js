MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.xDesktop.UserData", null, false);
MWF.xDesktop.requireApp("process.TaskCenter", "TaskList", null, false);
MWF.xDesktop.requireApp("process.TaskCenter", "TaskCompletedList", null, false);
MWF.xDesktop.requireApp("process.TaskCenter", "ReadList", null, false);
MWF.xDesktop.requireApp("process.TaskCenter", "ReadCompletedList", null, false);
MWF.xDesktop.requireApp("process.TaskCenter", "ReviewList", null, false);
MWF.xApplication.process.TaskCenter.options.multitask = false;
MWF.xApplication.process.TaskCenter.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "process.TaskCenter",
        "icon": "icon.png",
        "width": "1280",
        "height": "700",
        "title": MWF.xApplication.process.TaskCenter.LP.title,
        "filterMap": {
            "applicationList": "applicationList",
            "processList": "processList",
            "creatorUnitList": "creatorUnitList",
            "creatorCompanyList": "creatorCompanyList",
            "creatorDepartmentList": "creatorDepartmentList",
            "activityNameList": "activityNameList",
            "completedTimeMonthList": "completedTimeMonthList",
            "key": "key"
        }
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.process.TaskCenter.LP;
    },
    loadApplication: function (callback) {
        this.appIcons = {};
        this.tabs = [];
        this.tabShadows = [];
        this.startApplications = [];
        this.appStartableData = null;

        this.loadTitle();
        this.loadTab();
        this.loadFilterAction();
        this.loadContent();

        this.currentTab = "";

        this.openTab();
        if (callback) callback();
    },
    loadTitle: function () {
        this.loadTitleBar();
        if (!layout.mobile) this.loadTitleUserNode();
        this.loadStartProcessActionNode();
        if (!layout.mobile) this.loadTitleTextNode();
        this.loadSearchNode();
    },
    loadTitleBar: function () {
        this.taskTitleBar = new Element("div", {
            "styles": this.css.taskTitleBar
        }).inject(this.content);
    },
    loadTitleUserNode: function () {
        this.taskTitleUserNode = new Element("div", {
            "styles": this.css.taskTitleUserNode
        }).inject(this.taskTitleBar);
        this.taskTitleUserIconNode = new Element("div", {
            "styles": this.css.taskTitleUserIconNode
        }).inject(this.taskTitleUserNode);
        this.taskTitleUserTextNode = new Element("div", {
            "styles": this.css.taskTitleUserTextNode,
            "text": this.desktop.session.user.name
        }).inject(this.taskTitleUserNode);
    },
    loadStartProcessActionNode: function () {
        this.startProcessAction = new Element("div", {
            "styles": this.css.startProcessAction
        }).inject(this.taskTitleBar);
        this.startProcessAction.addEvents({
            "click": function (e) {
                this.showStartProcessArea(e);
            }.bind(this)
        });
    },
    loadTitleTextNode: function () {
        this.taskTitleTextNode = new Element("div", {
            "styles": this.css.taskTitleTextNode,
            "text": this.lp.title
        }).inject(this.taskTitleBar);
    },
    loadSearchNode: function () {
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.taskTitleBar);
        if (layout.mobile) this.searchBarAreaNode.setStyle("margin-left", "10px");
        if (layout.mobile) this.searchBarAreaNode.setStyle("margin-right", "20px");

        this.searchBarNode = new Element("div", {
            "styles": this.css.searchBarNode
        }).inject(this.searchBarAreaNode);

        this.searchBarActionNode = new Element("div", {
            "styles": this.css.searchBarActionNode
        }).inject(this.searchBarNode);
        this.searchBarInputBoxNode = new Element("div", {
            "styles": this.css.searchBarInputBoxNode
        }).inject(this.searchBarNode);
        this.searchBarInputNode = new Element("input", {
            "type": "text",
            "value": this.lp.searchKey,
            "styles": this.css.searchBarInputNode
        }).inject(this.searchBarInputBoxNode);

        var _self = this;
        this.searchBarActionNode.addEvent("click", function () {
            this.searchTask();
        }.bind(this));
        this.searchBarInputNode.addEvents({
            "focus": function () {
                if (this.value === _self.lp.searchKey) this.set("value", "");
            },
            "blur": function () {
                if (!this.value) this.set("value", _self.lp.searchKey);
            },
            "keydown": function (e) {
                if (e.code === 13) {
                    this.searchTask();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function (e) {
                e.preventDefault();
            }
        });
    },
    loadTab: function () {
        this.tabAreaNode = new Element("div", {
            "styles": this.css.tabAreaNode
        }).inject(this.content);

        this.createTabItem(this.lp.task, "task.png", "task", function () {
            this.showTask();
        }.bind(this));
        this.createTabItem(this.lp.done, "done.png", "taskCompleted", function () {
            this.showDone();
        }.bind(this));
        this.createTabItem(this.lp.read, "read.png", "read", function () {
            this.showRead();
        }.bind(this));
        this.createTabItem(this.lp.readed, "readed.png", "readCompleted", function () {
            this.showReaded();
        }.bind(this));
        //this.createTabItem(this.lp.review, "review.png", "review", function(){this.showReview();}.bind(this));

        this.getWorkCounts();
    },

    // createTabItem: function (text, icon, countKey, action) {
    //     if (COMMON.Browser.Platform.isMobile){
    //         this.createTabItem_mobile(text, icon, countKey, action);
    //     }else{
    //         this.createTabItem_pc(text, icon, countKey, action);
    //     }
    // },
    // createTabItem_mobile: function(text, icon, countKey, action){
    //     var tab = new Element("div", {
    //         "styles": this.css.tabItemNode
    //     }).inject(this.tabAreaNode);
    //
    //     var tabItem = new Element("div", {
    //         "styles": this.css.tabItemAreaNode
    //     }).inject(tab);
    //
    //     var tabContent = new Element("div", {
    //         "styles": this.css.tabItemContentNode_mobile
    //     }).inject(tabItem);
    //
    //     var tabText = new Element("div", {
    //         "styles": this.css.tabItemTextNode_mobile,
    //         "text": text
    //     }).inject(tabContent);
    //
    //     this[countKey+"CountNode"] = new Element("div", {
    //         "styles": this.css.tabItemCountNode
    //     }).inject(tabContent);
    //
    //     tab.addEvent("click", function(){action();}.bind(this));
    //
    //     this.tabs.push(tab);
    // },

    createTabItem: function(text, icon, countKey, action){
        var tab = new Element("div", {
            "styles": this.css.tabItemNode
        }).inject(this.tabAreaNode);

        if (!layout.mobile){
            var tabIcon = new Element("div", {
                "styles": this.css.tabItemIconNode
            }).inject(tab);
            tabIcon.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/tab/"+icon+")");
        }
        var tabText = new Element("div", {
            "styles": this.css.tabItemTextNode,
            "text": text
        }).inject(tab);

        this[countKey+"CountNode"] = new Element("div", {
            "styles": this.css.tabItemCountNode
        }).inject(tab);

        tab.addEvent("click", function(){action();}.bind(this));

        this.tabs.push(tab);
    },
    loadFilterAction: function(){
        // this.flterAction = new Element("div", {
        //     "styles": this.css.tabItemNode
        // }).inject(this.tabAreaNode);
        //
        // var tabIcon = new Element("div", {
        //     "styles": this.css.tabItemIconNode
        // }).inject(tab);
        // tabIcon.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/tab/"+icon+")");
        // var tabText = new Element("div", {
        //     "styles": this.css.tabItemTextNode,
        //     "text": text
        // }).inject(tab);
        //
        // this[countKey+"CountNode"] = new Element("div", {
        //     "styles": this.css.tabItemCountNode
        // }).inject(tab);
        //
        // tab.addEvent("click", function(){action();}.bind(this));
    },


    getWorkCounts: function () {
        this.getAction(function () {
            this.action.getCount(function (json) {
                this.counts = json.data;
                this["taskCountNode"].set("text", "( " + ((this.counts.task > 100) ? "99" : this.counts.task) + " )");
                this["taskCompletedCountNode"].set("text", "( " + ((this.counts.taskCompleted > 100) ? "99" : this.counts.taskCompleted) + " )");
                this["readCountNode"].set("text", "( " + ((this.counts.read > 100) ? "99" : this.counts.read) + " )");
                this["readCompletedCountNode"].set("text", "( " + ((this.counts.readCompleted > 100) ? "99" : this.counts.readCompleted) + " )");
                //this["reviewCountNode"].set("text", "[ "+((this.counts.review>100) ? "99" : this.counts.review)+" ]");
            }.bind(this), null, this.desktop.session.user.distinguishedName);
        }.bind(this));
    },
    loadContent: function(){
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        this.contentListAreaNode = new Element("div", {"styles": this.css.contentListAreaNode}).inject(this.contentNode);

        this.setContentNodeHeight();
        this.addEvent("resize", this.setContentNodeHeight.bind(this));
    },
    setContentNodeHeight: function(){
        var size = this.content.getSize();
        var titleSize = this.taskTitleBar.getSize();
        var tabSize = this.tabAreaNode.getSize();
        var y = size.y-titleSize.y-tabSize.y-1;
        this.contentNode.setStyle("height", ""+y+"px");

        var x = 0;
        if (this.taskList) x = x + size.x;
        if (this.taskCompletedList) x = x + size.x;
        if (this.readList) x = x + size.x;
        if (this.readCompletedList) x = x + size.x;

        this.contentListAreaNode.setStyle("width", ""+x+"px");
    },
    openTab: function () {
        var tab = "task";
        if (this.options.navi) tab = this.options.navi;
        if (this.status) tab = this.status.navi;
        switch (tab) {
            case "task":
                this.showTask();
                break;
            case "done":
                this.showDone();
                break;
            case "read":
                this.showRead();
                break;
            case "readed":
                this.showReaded();
                break;
            case "review":
                this.showReview();
                break;
            default:
                this.showTask();
                break;
        }
    },
    showTask: function () {
        if (this.currentTab !== "task") {
            this.showTab(0);
            this.currentTab = "task";
            if (!this.taskList) {
                this.createTaskList();
                this.taskList.show();
            } else {
                this.taskList.show();
                if (this.taskList) this.taskList.refresh();
            }
        } else {
            if (this.taskList) this.taskList.refresh();
        }
        //this.searchBarAreaNode.setStyle("display", "none");
    },
    showTab: function (idx) {
        this.tabs.each(function (node, i) {
            if (i === idx) {
                node.getLast().setStyles(this.css.tabItemTextNode_current);
                if (!layout.mobile){
                    node.getFirst().getNext().setStyles(this.css.tabItemTextNode_current);
                    var icon = node.getFirst().getStyle("background-image");
                    node.getFirst().setStyle("background-image", icon.replace(".png", "_cur.png"));
                }else{
                    node.getFirst().setStyles(this.css.tabItemTextNode_current);
                }
            } else {
                node.getLast().setStyles(this.css.tabItemCountNode);
                if (!layout.mobile){
                    node.getFirst().getNext().setStyles(this.css.tabItemTextNode);
                    var icon = node.getFirst().getStyle("background-image");
                    node.getFirst().setStyle("background-image", icon.replace("_cur.png", ".png"));
                }else{
                    node.getFirst().setStyles(this.css.tabItemTextNode);
                }
            }
        }.bind(this));
    },
    createTaskList: function () {
        if (!this.contentNode) this.loadContent();
        this.taskList = new MWF.xApplication.process.TaskCenter.TaskList(this.contentListAreaNode, this);
    },


    //@todo 起草 搜索 筛选 。。。
    showStartProcessArea: function(){
        if (layout.mobile){
            this.showStartProcessArea_mobile();
        }else{
            this.showStartProcessArea_pc();
        }
    },
    showStartProcessArea_mobile: function(){
        if (!this.startProcessAreaNode) {
            this.createStartProcessArea_mobile();
        }
        this.startProcessAreaNode.setStyle("display", "block");
        //document.body.setStyle("-webkit-overflow-scrolling", "auto");
        var morph = new Fx.Morph(this.startProcessAreaNode, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut
        });
        morph.start({"left": "0px"});
    },
    showStartProcessArea_pc: function () {
        if (!this.startProcessAreaNode) {
            this.createStartProcessArea();
        }
        this.content.mask({
            "inject": this.content,
            "destroyOnHide": true,
            "id": "process_taskcenter_startProcessMask",
            "style": this.css.maskNode
        });

        //var maskNode = this.window.node.getElement("#process_taskcenter_startProcessMask");
        var maskNode = this.content.getParent().getElement("#process_taskcenter_startProcessMask");
        if (maskNode){
            if( this.inBrowser ){
                maskNode.setStyles({"width":"100%","height":"100%"});
            }
            maskNode.addEvent("click", function (e) {
                this.closeStartProcessArea(e);
            }.bind(this));
        }
        //if (this.allApplicationStarter) this.allApplicationStarter.loadChild();
        this.startProcessAreaNode.fade("in");
        //this.startProcessTween.start("left", "-400px", "0px");
    },
    createStartProcessArea_mobile: function(){
        this.startProcessAreaNode = new Element("div", {"styles": this.css.startProcessAreaNode_mobile}).inject(this.content);
        var size = this.content.getSize();
        this.startProcessAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px",
            "top": "0px",
            "left": ""+size.x+"px"
        });

        this.startProcessTopNode = new Element("div", {"styles": this.css.startProcessTopNode_mobile}).inject(this.startProcessAreaNode);
        this.startProcessCloseNode = new Element("div", {"styles": this.css.startProcessCloseNode_mobile, "text": this.lp.back}).inject(this.startProcessTopNode);
        this.startProcessCloseNode.addEvent("click", function (e) {
            this.closeStartProcessArea(e);
        }.bind(this));

        this.startProcessListNode = new Element("div", {"styles": this.css.startProcessListNode_mobile}).inject(this.startProcessAreaNode);
        var h = size.y-this.startProcessTopNode.getSize().y;
        this.startProcessListNode.setStyle("height", ""+h+"px");

        //this.createStartProcessScrollNode();
        this.getAction(function () {
            this.action.listApplicationStartable(function (appjson) {
                this.app = this;
                MWF.UD.getDataJson("taskCenter_startTop", function(json){
                    this.top5Data = json;
                    if (this.top5Data && this.top5Data.length){
                        new Element("div", {"styles": this.css.applicationChildTitleNode, "text": this.lp.startTop5}).inject(this.startProcessListNode);
                        var top5ChildNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.startProcessListNode);

                        this.top5Data.sort(function(p1, p2){
                            return 0-(p1.count-p2.count);
                        });
                        this.top5Data.each(function(process, i){
                            if (i<5) new MWF.xApplication.process.TaskCenter.Process(process, this, {"name": process.applicationName}, top5ChildNode);
                        }.bind(this));
                    }
                    appjson.data.each(function (app) {
                        new Element("div", {"styles": this.css.applicationChildTitleNode, "text": app.name}).inject(this.startProcessListNode);
                        var appChildNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.startProcessListNode);
                        app.processList.each(function(process){
                            new MWF.xApplication.process.TaskCenter.Process(process, this, app, appChildNode);
                        }.bind(this));
                    }.bind(this));
                }.bind(this));


                // this.allApplicationStarter = new MWF.xApplication.process.TaskCenter.AllApplication(json.data, this);
                // this.allApplicationStarter.selected();
                // json.data.each(function (app) {
                //     new MWF.xApplication.process.TaskCenter.Application(app, this);
                // }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    createStartProcessArea: function () {
        this.createStartProcessAreaNode();
        this.createStartProcessScrollNode();

        this.listApplications();

        this.setResizeStartProcessAreaHeight();
        this.addEvent("resize", this.setResizeStartProcessAreaHeight.bind(this));

        //this.startProcessTween = new Fx.Tween(this.startProcessAreaNode, {
        //    "duration": "200",
        //    "transition": Fx.Transitions.Quad.easeOut
        //});
    },
    createStartProcessAreaNode: function () {
        this.startProcessAreaNode = new Element("div", {"styles": this.css.startProcessAreaNode}).inject(this.content);
    },
    createStartProcessCloseNode: function () {
        this.startProcessTopNode = new Element("div", {"styles": this.css.startProcessTopNode}).inject(this.startProcessRightListNode);
        this.startProcessCloseNode = new Element("div", {"styles": this.css.startProcessCloseNode}).inject(this.startProcessTopNode);
        this.startProcessCloseNode.addEvent("click", function (e) {
            this.closeStartProcessArea(e);
        }.bind(this));
    },
    createStartProcessSearchNode: function(){
        this.startProcessSearchNode = new Element("div", {"styles": this.css.startProcessSearchNode}).inject(this.startProcessRightListNode);
        this.startProcessSearchIconNode = new Element("div", {"styles": this.css.startProcessSearchIconNode}).inject(this.startProcessSearchNode);
        this.startProcessSearchAreaNode = new Element("div", {"styles": this.css.startProcessSearchAreaNode}).inject(this.startProcessSearchNode);
        this.startProcessSearchInputNode = new Element("input", {"styles": this.css.startProcessSearchInputNode}).inject(this.startProcessSearchAreaNode);
        this.startProcessSearchInputNode.set("value", this.lp.searchProcess);
        this.startProcessSearchInputNode.addEvents({
            "focus": function(){ if (this.startProcessSearchInputNode.get("value")===this.lp.searchProcess) this.startProcessSearchInputNode.set("value", ""); }.bind(this),
            "blur": function(){if (!this.startProcessSearchInputNode.get("value")) this.startProcessSearchInputNode.set("value", this.lp.searchProcess);}.bind(this),
            "keydown": function(e){ if (e.code===13) this.searchStartProcess(); }.bind(this)
        });
        this.startProcessSearchIconNode.addEvent("click", function(){ this.searchStartProcess(); }.bind(this));
    },
    searchStartProcess: function(){
        var key = this.startProcessSearchInputNode.get("value");
        if (key && key!==this.lp.searchProcess){
            if (this.appStartableData){
                this.startApplications.each(function(app){ app.unselected(); });
                if (this.searchProcessSearchchildNode) this.searchProcessSearchchildNode.destroy();
                var text = this.lp.searchProcessResault.replace("{key}", key);

                this.searchProcessSearchchildNode = new Element("div", {"styles": this.css.applicationChildNode}).inject(this.startProcessProcessAreaNode);
                this.searchProcessSearchchildNode.setStyle("display", "block");
                new Element("div", {"styles": this.css.applicationChildTitleNode, "text": text}).inject(this.searchProcessSearchchildNode);
                var proListNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.searchProcessSearchchildNode);

                this.appStartableData.each(function (app) {
                    app.processList.each(function(pro){
                        if (pro.name.indexOf(key)!==-1){
                            var data = Object.clone(pro);
                            data.applicationName = app.name;
                            new MWF.xApplication.process.TaskCenter.Process(data, {"app": this}, {"name": app}, proListNode);
                        }
                    }.bind(this));
                }.bind(this));
            }
        }
    },
    createStartProcessScrollNode: function () {
        this.startProcessApplicationListNode = new Element("div", {"styles": this.css.startProcessApplicationListNode}).inject(this.startProcessAreaNode);
        this.startProcessRightListNode = new Element("div", {"styles": this.css.startProcessRightListNode}).inject(this.startProcessAreaNode);

        this.createStartProcessCloseNode();
        this.createStartProcessSearchNode();

        this.startProcessApplicationScrollNode = new Element("div", {"styles": this.css.startProcessApplicationScrollNode}).inject(this.startProcessApplicationListNode);
        this.startProcessApplicationAreaNode = new Element("div", {"styles": this.css.startProcessApplicationAreaNode}).inject(this.startProcessApplicationScrollNode);

        this.startProcessProcessListNode = new Element("div", {"styles": this.css.startProcessProcessListNode}).inject(this.startProcessRightListNode);
        this.startProcessProcessScrollNode = new Element("div", {"styles": this.css.startProcessProcessScrollNode}).inject(this.startProcessProcessListNode);
        this.startProcessProcessAreaNode = new Element("div", {"styles": this.css.startProcessProcessAreaNode}).inject(this.startProcessProcessScrollNode);

        MWF.require("MWF.widget.ScrollBar", function () {
            new MWF.widget.ScrollBar(this.startProcessApplicationScrollNode, {
                "distance": 100,
                "friction": 4
            });
            new MWF.widget.ScrollBar(this.startProcessProcessScrollNode, {
                "distance": 100,
                "friction": 4
            });
        }.bind(this));

        //this.startProcessContentNode = new Element("div", {"styles": this.css.startProcessContentNode}).inject(this.startProcessScrollNode);
    },
    closeStartProcessArea: function () {
        //if (this.startProcessAreaNode) this.startProcessTween.start("left", "0px", "-400px");
        if (layout.mobile){
            var size = this.startProcessAreaNode.getSize();
            var morph = new Fx.Morph(this.startProcessAreaNode, {
                "duration": 200,
                "transition": Fx.Transitions.Expo.easeOut,
                "onComplete": function(){
                    this.startProcessAreaNode.setStyle("display", "none");
                }.bind(this)
            });
            morph.start({"left": ""+size.x+"px"});
        }else{
            this.content.unmask();
            if (this.startProcessAreaNode) this.startProcessAreaNode.fade("out");
        }

    },
    setResizeStartProcessAreaHeight: function () {
        if (this.startProcessAreaNode) {
            var size = this.content.getSize();
            var nodeSize = this.startProcessAreaNode.getSize();
            var x = (size.x-nodeSize.x)/2;
            var y = (size.y-nodeSize.y)/2;
            this.startProcessAreaNode.setStyle("top", "" + y + "px");
            this.startProcessAreaNode.setStyle("left", "" + x + "px");

            var maskNode = this.content.getParent().getElement("#process_taskcenter_startProcessMask");
            //var maskNode = this.window.node.getElement("#process_taskcenter_startProcessMask");
            if (maskNode){
                maskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px"});
                maskNode.position({
                    "relativeTo": this.content,
                    "position": "topLeft",
                    "edge": "topLeft"
                });
            }

            if (this.startProcessProcessListNode){
                var topSize = this.startProcessTopNode.getSize();
                var searchSize = this.startProcessSearchNode.getSize();
                var h = nodeSize.y-topSize.y-searchSize.y;
                this.startProcessProcessListNode.setStyle("height", ""+h+"px");
            }

        }
    },
    listApplications: function () {
        this.getAction(function () {
            this.action.listApplicationStartable(function (json) {
                this.appStartableData = json.data;
                this.startProcessSearchNode.setStyle("display", "block");
                this.allApplicationStarter = new MWF.xApplication.process.TaskCenter.AllApplication(json.data, this);
                this.allApplicationStarter.selected();
                json.data.each(function (app) {
                    new MWF.xApplication.process.TaskCenter.Application(app, this);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },

    getAction: function (callback) {
        if (!this.action) {
            this.action = MWF.Actions.get("x_processplatform_assemble_surface");
            if (callback) callback();
            // MWF.xDesktop.requireApp("process.TaskCenter", "Actions.RestActions", function () {
            //     this.action = new MWF.xApplication.process.TaskCenter.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
        } else {
            if (callback) callback();
        }
    },


    refreshAll: function () {
        this.getWorkCounts();
        if (this.taskList) if (this.currentTab === "task") this.taskList.refresh();
        //if (this.taskCompletedList) if (this.currentTab == "done") this.taskCompletedList.refresh();
        //if (this.readList) if (this.currentTab == "read") this.readList.refresh();
        //if (this.readedList) if (this.currentTab == "readed") this.readedList.refresh();
        //if (this.reviewList) if (this.currentTab == "review") this.reviewList.refresh();
    },

    createTaskCompletedList: function (filterData) {
        if (!this.contentNode) this.loadContent();
        this.taskCompletedList = new MWF.xApplication.process.TaskCenter.TaskCompletedList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showDone: function () {
        if (this.currentTab !== "done") {
            this.showTab(1);
            this.currentTab = "done";
            if (!this.taskCompletedList) {
                this.createTaskCompletedList((this.status) ? this.status.filter : null);
                this.taskCompletedList.show();
            } else {
                this.taskCompletedList.show();
                if (this.taskCompletedList) this.taskCompletedList.refresh();
            }

        } else {
            if (this.taskCompletedList) this.taskCompletedList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },
    createReadList: function (filterData) {
        if (!this.contentNode) this.loadContent();
        this.readList = new MWF.xApplication.process.TaskCenter.ReadList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showRead: function () {
        if (this.currentTab !== "read") {
            this.showTab(2);
            this.currentTab = "read";
            if (!this.readList) {
                this.createReadList((this.status) ? this.status.filter : null);
                this.readList.show();
            } else {
                this.readList.show();
                if (this.readList) this.readList.refresh();
            }
        } else {
            if (this.readList) this.readList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },
    createReadedList: function (filterData) {
        if (!this.contentNode) this.loadContent();
        this.readedList = new MWF.xApplication.process.TaskCenter.ReadCompletedList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showReaded: function () {
        if (this.currentTab !== "readed") {
            this.showTab(3);
            this.currentTab = "readed";
            if (!this.readedList) {
                this.createReadedList((this.status) ? this.status.filter : null);
                this.readedList.show();
            } else {
                this.readedList.show();
                if (this.readedList) this.readedList.refresh();
            }
        } else {
            if (this.readedList) this.readedList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },

    createReviewList: function (filterData) {
        if (!this.contentNode) this.loadContent();
        this.reviewList = new MWF.xApplication.process.TaskCenter.ReviewList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showReview: function () {
        if (this.currentTab !== "review") {
            this.showTab(4);
            this.currentTab = "review";
            if (!this.reviewList) {
                this.createReviewList((this.status) ? this.status.filter : null);
                this.reviewList.show();
            } else {
                this.reviewList.show();
                if (this.reviewList) this.reviewList.refresh();
            }

        } else {
            if (this.reviewList) this.reviewList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
    },

    recordStatus: function(){
        var tab = this.currentTab || "task";
        var filter = null;
        if (tab==="done"){
            filter = this.taskCompletedList.filterData;
        }
        if (tab==="read"){
            filter = this.readList.filterData;
        }
        if (tab==="readed"){
            filter = this.readedList.filterData;
        }
        if (tab==="review"){
            filter = this.reviewList.filterData;
        }
        return {"navi": this.currentTab || "task", "filter": filter};
    },

    searchTask: function(){
        var keyWord = this.searchBarInputNode.get("value");
        if (keyWord && (keyWord!==this.lp.searchKey)){
            var tab = this.currentTab || "task";
            switch (tab){
                case "task":
                    if (!this.taskList.filterData) this.taskList.filterData  = {};
                    this.taskList.filterData.key = keyWord;
                    this.taskList.refilter();
                    break;
                case "done":
                    if (!this.taskCompletedList.filterData) this.taskCompletedList.filterData  = {};
                    this.taskCompletedList.filterData.key = keyWord;
                    this.taskCompletedList.refilter();
                    break;
                case "read":
                    if (!this.readList.filterData) this.readList.filterData  = {};
                    this.readList.filterData.key = keyWord;
                    this.readList.refilter();
                    break;
                case "readed":
                    if (!this.readedList.filterData) this.readedList.filterData  = {};
                    this.readedList.filterData.key = keyWord;
                    this.readedList.refilter();
                    break;
                case "review":
                    if (!this.reviewList.filterData) this.reviewList.filterData  = {};
                    this.reviewList.filterData.key = keyWord;
                    this.reviewList.refilter();
                    break;
            }

        }
    }
});
MWF.xApplication.process.TaskCenter.Application = new Class({
    initialize: function(data, app){
        this.bgColors = ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"];
        this.data = data;
        this.app = app;
        this.container = this.app.startProcessApplicationAreaNode;
        this.processContainer = this.app.startProcessProcessAreaNode;
        this.css = this.app.css;
        this.isLoaded = false;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationNode}).inject(this.container);
        this.iconAreaNode = new Element("div", {"styles": this.css.applicationIconAreaNode}).inject(this.node);
        this.iconNode = new Element("img", {"styles": this.css.applicationIconNode}).inject(this.iconAreaNode);
        if (this.data.icon){
            this.iconNode.set("src", "data:image/png;base64,"+this.data.icon+"");
        }else{
            this.iconNode.set("src", "/x_component_process_ApplicationExplorer/$Main/default/icon/application.png");
        }

        this.textNode = new Element("div", {"styles": this.css.applicationTextNode}).inject(this.node);
        this.textNode.set("text", this.data.name);
        this.textNode.set("title", this.data.name);

        this.childNode = new Element("div", {"styles": this.css.applicationChildNode}).inject(this.processContainer);
        //this.loadChild();
        this.node.addEvent("click", function(){
            this.selected();
        }.bind(this));
        this.app.startApplications.push(this);
    },
    unselected: function(){
        this.childNode.setStyle("display", "none");
        this.node.setStyles(this.css.applicationNode);
    },
    selected: function(){
        this.app.startApplications.each(function(app){
            app.unselected();
        });
        if (this.app.searchProcessSearchchildNode) this.app.searchProcessSearchchildNode.destroy();
        if (this.app.startProcessSearchInputNode) this.app.startProcessSearchInputNode.set("value", this.app.lp.searchProcess);
        this.childNode.setStyle("display", "block");
        this.node.setStyles(this.css.applicationNode_selected);
        if (!this.isLoaded){
            this.loadChild();
            this.isLoaded = true;
        }
    },
    loadChild: function(){
        new Element("div", {"styles": this.css.applicationChildTitleNode, "text": this.app.lp.startProcess}).inject(this.childNode);
        var childNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.childNode);
        this.data.processList.each(function(process){
            new MWF.xApplication.process.TaskCenter.Process(process, this, this.data, childNode);
        }.bind(this));
    }
});

MWF.xApplication.process.TaskCenter.AllApplication = new Class({
    Extends: MWF.xApplication.process.TaskCenter.Application,
    initialize: function(data, app){
        this.bgColors = ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"];
        this.data = data;
        this.app = app;
        this.container = this.app.startProcessApplicationAreaNode;
        this.processContainer = this.app.startProcessProcessAreaNode;
        this.css = this.app.css;
        this.isLoaded = false;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationNode}).inject(this.container);
        this.iconAreaNode = new Element("div", {"styles": this.css.applicationIconAreaNode}).inject(this.node);
        this.iconNode = new Element("img", {"styles": this.css.applicationIconNode}).inject(this.iconAreaNode);
        this.iconNode.set("src", "/x_component_process_TaskCenter/$Main/default/icon/appAppliction.png");

        this.textNode = new Element("div", {"styles": this.css.applicationTextNode}).inject(this.node);
        this.textNode.set("text", this.app.lp.all);
        this.textNode.set("title", this.app.lp.all);

        this.childNode = new Element("div", {"styles": this.css.applicationChildNode}).inject(this.processContainer);
        //this.loadChild();
        this.node.addEvent("click", function(){
            this.selected();
        }.bind(this));
        this.app.startApplications.push(this);
    },
    unselected: function(){
        this.childNode.empty();
        this.isLoaded = false;
        this.childNode.setStyle("display", "none");
        this.node.setStyles(this.css.applicationNode);
    },
    loadChild: function(){
        //this.loadSearch();
        MWF.UD.getDataJson("taskCenter_startTop", function(json){
            this.top5Data = json;
            if (this.top5Data && this.top5Data.length){
                new Element("div", {"styles": this.css.applicationChildTitleNode, "text": this.app.lp.startTop5}).inject(this.childNode);
                var top5ChildNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.childNode);

                this.top5Data.sort(function(p1, p2){
                    return 0-(p1.count-p2.count);
                });
            }

            var allowProcessIds = [];
            this.data.each(function (app) {
                new Element("div", {"styles": this.css.applicationChildTitleNode, "text": app.name}).inject(this.childNode);
                var appChildNode = new Element("div", {"styles": this.css.applicationChildChildNode}).inject(this.childNode);
                app.processList.each(function(process){
                    allowProcessIds.push(process.id);
                    new MWF.xApplication.process.TaskCenter.Process(process, this, app, appChildNode);
                }.bind(this));
            }.bind(this));

            if (top5ChildNode){
                saveflag = false;
                this.top5Data.each(function(process, i){
                    if (allowProcessIds.indexOf(process.id)!==-1){
                        if (i<5) new MWF.xApplication.process.TaskCenter.Process(process, this, {"name": process.applicationName}, top5ChildNode);
                    }else{
                        saveflag = true;
                        process.count=0;
                    }
                }.bind(this));
                if (saveflag) MWF.UD.putData("taskCenter_startTop", this.top5Data);
            }

        }.bind(this));
    }
});




MWF.xApplication.process.TaskCenter.Process = new Class({
    initialize: function(data, application, applicationData, container){
        this.data = data;
        this.application = application;
        this.applicationData = applicationData;
        this.app = this.application.app;
        this.container = container;
        this.css = this.app.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div.processItem", {"styles": this.css.startProcessNode}).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.processIconNode}).inject(this.node);
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url("+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url(/x_component_process_ProcessManager/$Explorer/default/processIcon/process.png)");
        }
        this.actionNode = new Element("div", {"styles": this.css.processActionNode, "text": this.app.lp.start}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.processTextNode}).inject(this.node);

        this.textNode.set({
            "text": this.data.name+((this.data.applicationName) ? " -- ("+this.data.applicationName+")" : ""),
            "title": this.data.name+"-"+this.data.description
        });
        //var _self = this;

        this.actionNode.addEvents({
            "mouseover": function(){this.actionNode.setStyles(this.css.processActionNode_over);}.bind(this),
            "mouseout": function(){this.actionNode.setStyles(this.css.processActionNode);}.bind(this),
            "click": function(e){
                this.startProcess(e);
            }.bind(this)
        });
        this.node.addEvents({
            "mouseover": function(){
                this.node.setStyles(this.css.startProcessNode_over);
                this.actionNode.setStyle("display", "block");
            }.bind(this),
            "mouseout": function(){
                this.node.setStyles(this.css.startProcessNode_out);
                //this.actionNode.setStyle("display", "none");
            }.bind(this)
        });
    },
    startProcess: function(){
        this.app.closeStartProcessArea();
        MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
            var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(this.data, this.app, {
                "onStarted": function(data, title, processName){
                    this.afterStartProcess(data, title, processName);
                }.bind(this)
            });
            starter.load();
        }.bind(this));
    },
    recordProcessData: function(){
        MWF.UD.getDataJson("taskCenter_startTop", function(json){
            if (!json || !json.length) json = [];
            var recordProcess = null;
            this.data.lastStartTime = new Date();
            var earlyProcessIdx = 0;
            var flag = true;
            for (var i=0; i<json.length; i++){
                var process = json[i];
                if (process.id === this.data.id) recordProcess = process;
                if (flag){
                    if (!process.lastStartTime){
                        earlyProcessIdx = i;
                        flag = false;
                    }else{
                        if (new Date(process.lastStartTime)<new Date(json[earlyProcessIdx].lastStartTime)){
                            earlyProcessIdx = i;
                        }
                    }
                }
            }
            if (recordProcess) {
                recordProcess.lastStartTime = new Date();
                recordProcess.count = (recordProcess.count || 0)+1;
                recordProcess.applicationName = this.applicationData.name;
            }else{
                if (json.length<10){
                    this.data.count = 1;
                    this.data.applicationName = this.applicationData.name;
                    json.push(this.data);
                }else{
                    json.splice(earlyProcessIdx, 1);
                    this.data.count = 1;
                    this.data.applicationName = this.applicationData.name;
                    json.push(this.data);
                }
            }
            MWF.UD.putData("taskCenter_startTop", json);
        }.bind(this));
    },
    afterStartProcess: function(data, title, processName){
        this.recordProcessData();
        var workInfors = [];
        var currentTask = [];

        data.each(function(work){
            if (work.currentTaskIndex !== -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));

        if (currentTask.length===1){
            var options = {"workId": currentTask[0], "appId": currentTask[0]};
            this.app.desktop.openApplication(null, "process.Work", options);

            if (layout.desktop.message) this.createStartWorkResault(workInfors, title, processName, false);
        }else{
            if (layout.desktop.message) this.createStartWorkResault(workInfors, title, processName, true);
        }
    },
    getStartWorkInforObj: function(work){
        var users = [];
        var currentTask = "";
        work.taskList.each(function(task, idx){
            users.push(task.person+"("+task.department + ")");
            if (work.currentTaskIndex===idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
    },
    createStartWorkResault: function(workInfors, title, processName, isopen){
        var content = "";
        workInfors.each(function(infor){
            var users = [];
            infor.users.each(function(uname){
                users.push(MWF.name.cn(uname));
            });

            content += "<div><b>"+this.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+infor.activity+"</font>, "+this.app.lp.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b>";
            if (infor.currentTask && isopen){
                content += "&nbsp;&nbsp;&nbsp;&nbsp;<span value=\""+infor.currentTask+"\">"+this.app.lp.deal+"</span></div>";
            }else{
                content += "</div>";
            }
        }.bind(this));

        var msg = {
            "subject": this.app.lp.processStarted,
            "content": "<div>"+this.app.lp.processStartedMessage+"“["+processName+"]"+title+"”</div>"+content
        };
        var tooltip = layout.desktop.message.addTooltip(msg);
        var item = layout.desktop.message.addMessage(msg);

        this.setStartWorkResaultAction(tooltip);
        this.setStartWorkResaultAction(item);
    },
    setStartWorkResaultAction: function(item){
        var node = item.node.getElements("span");
        node.setStyles(this.app.css.dealStartedWorkAction);
        var _self = this;
        node.addEvent("click", function(e){
            var options = {"taskId": this.get("value"), "appId": this.get("value")};
            _self.app.desktop.openApplication(e, "process.Work", options);
        });
    }

});
