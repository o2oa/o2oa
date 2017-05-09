MWF.require("MWF.widget.Mask", null, false);
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
        "filterMap":{
            "applicationList": "applicationList",
            "processList": "processList",
            "creatorCompanyList": "creatorCompanyList",
            "creatorDepartmentList": "creatorDepartmentList",
            "activityNameList": "activityNameList",
            "completedTimeMonthList": "completedTimeMonthList",
            "key": "key"
        }
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.TaskCenter.LP;
	},
	loadApplication: function(callback){
        this.appIcons = {};
        this.tabs = [];
        this.tabShadows = [];

        this.loadTitle();
        this.loadTab();
        this.loadContent();

        this.currentTab = "";

        this.openTab();
	},
    openTab: function(){
        var tab = "task";
        if (this.status) tab = this.status.navi;
        switch (tab){
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
    loadTitle: function(){
        this.loadTitleBar();
        this.loadTitleUserNode();
        this.loadStartProcessActionNode();
        this.loadTitleTextNode();
        this.loadSearchNode();
    },
    //createCreateAction: function(){
    //    this.createApplicationNode = new Element("div", {
    //        "styles": this.css.createApplicationNode,
    //        "title": this.options.tooltip.create
    //    }).inject(this.toolbarAreaNode);
    //    this.createApplicationNode.addEvent("click", function(){
    //        this.createApplication();
    //    }.bind(this));
    //},

    loadTitleBar: function(){
        this.taskTitleBar = new Element("div", {
            "styles": this.css.taskTitleBar
        }).inject(this.content);
    },
    loadTitleUserNode: function(){
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
    loadStartProcessActionNode: function() {
        this.startProcessAction = new Element("div", {
            "styles": this.css.startProcessAction
        }).inject(this.taskTitleBar);
        this.startProcessAction.addEvents({
            "click": function(e){
                this.showStartProcessArea();
            }.bind(this)
        });
    },
    showStartProcessArea: function(){
        if (!this.startProcessAreaNode){
            this.createStartProcessArea();
        }
        this.startProcessAreaNode.fade("0.9")
        //this.startProcessTween.start("left", "-400px", "0px");
    },
    closeStartProcessArea: function(){
        //if (this.startProcessAreaNode) this.startProcessTween.start("left", "0px", "-400px");
        if (this.startProcessAreaNode) this.startProcessAreaNode.fade("out");
    },

    createStartProcessArea: function(){
        this.createStartProcessAreaNode();
        this.createStartProcessCloseNode();
        this.createStartProcessScrollNode();

        this.listApplications();

        this.setResizeStartProcessAreaHeight();
        this.addEvent("resize", this.setResizeStartProcessAreaHeight.bind(this));
        //this.startProcessTween = new Fx.Tween(this.startProcessAreaNode, {
        //    "duration": "200",
        //    "transition": Fx.Transitions.Quad.easeOut
        //});
    },
    createStartProcessAreaNode: function(){
        this.startProcessAreaNode = new Element("div", {"styles": this.css.startProcessAreaNode}).inject(this.content);
        this.startProcessAreaNode.addEvent("click", function(e){
            this.closeStartProcessArea();
        }.bind(this));
    },
    createStartProcessCloseNode: function(){
        this.startProcessTopNode = new Element("div", {"styles": this.css.startProcessTopNode}).inject(this.startProcessAreaNode);
        this.startProcessCloseNode = new Element("div", {"styles": this.css.startProcessCloseNode}).inject(this.startProcessTopNode);
        this.startProcessCloseNode.addEvent("click", function(e){
            this.closeStartProcessArea();
        }.bind(this));
    },
    createStartProcessScrollNode: function(){
        this.startProcessScrollNode = new Element("div", {"styles": this.css.startProcessScrollNode}).inject(this.startProcessAreaNode);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.startProcessScrollNode, {
                "style":"xApp_taskcenter", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
        this.startProcessContentNode = new Element("div", {"styles": this.css.startProcessContentNode}).inject(this.startProcessScrollNode);
    },
    listApplications: function(){

        this.getAction(function(){
            this.action.listApplication(function(json){
                json.data.each(function(app){
                    new MWF.xApplication.process.TaskCenter.Application(app, this, this.startProcessContentNode);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    getAction: function(callback){
        if (!this.action){
            MWF.xDesktop.requireApp("process.TaskCenter", "Actions.RestActions", function(){
                this.action = new MWF.xApplication.process.TaskCenter.Actions.RestActions();
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },


    setResizeStartProcessAreaHeight: function(){
        var size = this.content.getSize();
        if (this.startProcessAreaNode){
            var topSize = this.startProcessCloseNode.getSize();
            var y = size.y-topSize.y-80;
            var x = size.x - 110;
            var areay = size.y-60;
            var areax = size.x-90;
            this.startProcessScrollNode.setStyle("height", ""+y+"px");
            this.startProcessScrollNode.setStyle("width", ""+x+"px");
            this.startProcessAreaNode.setStyle("height", ""+areay+"px");
            this.startProcessAreaNode.setStyle("width", ""+areax+"px");
        }
    },

    loadTitleTextNode: function(){
        this.taskTitleTextNode = new Element("div", {
            "styles": this.css.taskTitleTextNode,
            "text": this.lp.title
        }).inject(this.taskTitleBar);
    },
    loadSearchNode: function(){
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.taskTitleBar);

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
        this.searchBarActionNode.addEvent("click", function(){
            this.searchTask();
        }.bind(this));
        this.searchBarInputNode.addEvents({
            "focus": function(){
                if (this.value==_self.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.searchTask();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function(e){
                e.preventDefault();
            }
        });
    },

    loadTab: function(){
        this.tabAreaNode = new Element("div", {
            "styles": this.css.tabAreaNode
        }).inject(this.content);

        this.createTabLeft();
        this.createTabItem(this.lp.task, "task.png", "task", function(){this.showTask();}.bind(this));
        this.createTabItem(this.lp.done, "done.png", "taskCompleted", function(){this.showDone();}.bind(this));
        this.createTabItem(this.lp.read, "read.png", "read", function(){this.showRead();}.bind(this));
        this.createTabItem(this.lp.readed, "readed.png", "readCompleted", function(){this.showReaded();}.bind(this));
        this.createTabItem(this.lp.review, "review.png", "review", function(){this.showReview();}.bind(this));
        this.createTabRight();

        this.createShadowNode();

        this.getWorkCounts();

    },
    getWorkCounts: function(){
        this.getAction(function(){
            this.action.getCount(function(json){
                this.counts = json.data;
                this["taskCountNode"].set("text", "[ "+((this.counts.task>100) ? "99" : this.counts.task)+" ]");
                this["taskCompletedCountNode"].set("text", "[ "+((this.counts.taskCompleted>100) ? "99" : this.counts.taskCompleted)+" ]");
                this["readCountNode"].set("text", "[ "+((this.counts.read>100) ? "99" : this.counts.read)+" ]");
                this["readCompletedCountNode"].set("text", "[ "+((this.counts.readCompleted>100) ? "99" : this.counts.readCompleted)+" ]");
                this["reviewCountNode"].set("text", "[ "+((this.counts.review>100) ? "99" : this.counts.review)+" ]");

            }.bind(this), null, this.desktop.session.user.name);
        }.bind(this));
    },
    createTaskList: function(){
        if (!this.contentNode) this.loadContent();
        this.taskList = new MWF.xApplication.process.TaskCenter.TaskList(this.contentListAreaNode, this);
    },
    refreshAll: function(){
        this.getWorkCounts();
        if (this.taskList) if (this.currentTab == "task") this.taskList.refresh();
        //if (this.taskCompletedList) if (this.currentTab == "done") this.taskCompletedList.refresh();
        //if (this.readList) if (this.currentTab == "read") this.readList.refresh();
        //if (this.readedList) if (this.currentTab == "readed") this.readedList.refresh();
        //if (this.reviewList) if (this.currentTab == "review") this.reviewList.refresh();
    },
    showTask: function(){
        if (this.currentTab != "task"){
            this.showTab(0);
            this.currentTab = "task";
            if (!this.taskList){
                this.createTaskList();
                this.taskList.show();
            }else{
                this.taskList.show();
                if (this.taskList) this.taskList.refresh();
            }
        }else{
            if (this.taskList) this.taskList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "none");
    },
    createTaskCompletedList: function(filterData){
        if (!this.contentNode) this.loadContent();
        this.taskCompletedList = new MWF.xApplication.process.TaskCenter.TaskCompletedList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showDone: function(){
        if (this.currentTab != "done"){
            this.showTab(1);
            this.currentTab = "done";
            if (!this.taskCompletedList){
                this.createTaskCompletedList((this.status) ? this.status.filter : null);
                this.taskCompletedList.show();
            }else{
                this.taskCompletedList.show();
                if (this.taskCompletedList) this.taskCompletedList.refresh();
            }

        }else{
            if (this.taskCompletedList) this.taskCompletedList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },
    createReadList: function(filterData){
        if (!this.contentNode) this.loadContent();
        this.readList = new MWF.xApplication.process.TaskCenter.ReadList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showRead: function(){
        if (this.currentTab != "read"){
            this.showTab(2);
            this.currentTab = "read";
            if (!this.readList){
                this.createReadList((this.status) ? this.status.filter : null);
                this.readList.show();
            }else{
                this.readList.show();
                if (this.readList) this.readList.refresh();
            }
        }else{
            if (this.readList) this.readList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },
    createReadedList: function(filterData){
        if (!this.contentNode) this.loadContent();
        this.readedList = new MWF.xApplication.process.TaskCenter.ReadedList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showReaded: function(){
        if (this.currentTab != "readed"){
            this.showTab(3);
            this.currentTab = "readed";
            if (!this.readedList){
                this.createReadedList((this.status) ? this.status.filter : null);
                this.readedList.show();
            }else{
                this.readedList.show();
                if (this.readedList) this.readedList.refresh();
            }
        }else{
            if (this.readedList) this.readedList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
        this.searchBarInputNode.set("value", this.lp.searchKey);
    },

    createReviewList: function(filterData){
        if (!this.contentNode) this.loadContent();
        this.reviewList = new MWF.xApplication.process.TaskCenter.ReviewList(this.contentListAreaNode, this, filterData);
        //if (filterData) this.taskCompletedList.filterData = filterData;
    },
    showReview: function(){
        if (this.currentTab != "review"){
            this.showTab(4);
            this.currentTab = "review";
            if (!this.reviewList){
                this.createReviewList((this.status) ? this.status.filter : null)
                this.reviewList.show();
            }else{
                this.reviewList.show();
                if (this.reviewList) this.reviewList.refresh();
            }

        }else{
            if (this.reviewList) this.reviewList.refresh();
        }
        this.searchBarAreaNode.setStyle("display", "block");
    },

    showTab: function(idx){
        this.tabs.each(function(node, i){
            if (i==idx){
                node.setStyles(this.css.tabItemNode_current);
            }else{
                node.setStyles(this.css.tabItemNode);
            }
        }.bind(this));
        this.tabShadows.each(function(node, i){
            if (i==idx){
                node.setStyles(this.css.tabShadowItemNode_current);
            }else{
                node.setStyles(this.css.tabShadowItemNode);
            }
        }.bind(this));
    },

    createTabLeft: function(){
        var tab = new Element("div", {
            "styles": this.css.tabItemLeftNode,
        }).inject(this.tabAreaNode);

    },
    createTabRight: function(){
        var tab = new Element("div", {
            "styles": this.css.tabItemRightNode,
        }).inject(this.tabAreaNode);
        var box = new Element("div", {
            "styles": this.css.tabItemRightBoxNode,
        }).inject(tab);
    },

    createTabItem: function(text, icon, countKey, action){
        var tab = new Element("div", {
            "styles": this.css.tabItemNode,
        }).inject(this.tabAreaNode);

        var tabItem = new Element("div", {
            "styles": this.css.tabItemAreaNode,
        }).inject(tab);

        var tabContent = new Element("div", {
            "styles": this.css.tabItemContentNode,
        }).inject(tabItem);

        var tabIcon = new Element("div", {
            "styles": this.css.tabItemIconNode,
        }).inject(tabContent);
        tabIcon.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/tab/"+icon+")");

        var tabText = new Element("div", {
            "styles": this.css.tabItemTextNode,
            "text": text
        }).inject(tabContent);

        this[countKey+"CountNode"] = new Element("div", {
            "styles": this.css.tabItemCountNode
        }).inject(tabContent);

        tab.addEvent("click", function(){action();}.bind(this));

        this.tabs.push(tab);
    },
    createShadowNode: function(){
        this.tabShadowNode = new Element("div", {
            "styles": this.css.tabShadowNode,
        }).inject(this.content);

        this.createShadowLeftRightNode();
        this.createShadowItemNode();
        this.createShadowLeftRightNode();
    },
    createShadowLeftRightNode: function(){
        new Element("div", {
            "styles": this.css.tabShadowLeftRightNode,
        }).inject(this.tabShadowNode);
    },
    createShadowItemNode: function(){
        for (var i=0; i<5; i++){
            this.tabShadows.push(new Element("div", {
                "styles": this.css.tabShadowItemNode,
            }).inject(this.tabShadowNode));
        }
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
        var tabSize1 = this.tabAreaNode.getSize();
        var tabSize2 = this.tabShadowNode.getSize();
        var y = size.y-titleSize.y-tabSize1.y-tabSize2.y-1;

        this.contentNode.setStyle("height", ""+y+"px");

        var x = 10;
        if (this.taskList) x = x + size.x;
        if (this.taskCompletedList) x = x + size.x;
        if (this.readList) x = x + size.x;
        if (this.readCompletedList) x = x + size.x;

        this.contentListAreaNode.setStyle("width", ""+x+"px");
    },
    recordStatus: function(){
        var tab = this.currentTab || "task";
        var filter = null;
        if (tab=="done"){
            filter = this.taskCompletedList.filterData;
        }
        if (tab=="read"){
            filter = this.readList.filterData;
        }
        if (tab=="readed"){
            filter = this.readedList.filterData;
        }
        if (tab=="review"){
            filter = this.reviewList.filterData;
        }
        return {"navi": this.currentTab || "task", "filter": filter};
    },

    searchTask: function(){
        var keyWord = this.searchBarInputNode.get("value");
        if (keyWord && (keyWord!=this.lp.searchKey)){
            var tab = this.currentTab || "task";
            switch (tab){
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

    initialize: function(data, app, container){
        this.bgColors = ["#30afdc", "#e9573e", "#8dc153", "#9d4a9c", "#ab8465", "#959801", "#434343", "#ffb400", "#9e7698", "#00a489"];
        this.data = data;
        this.app = app;
        this.container = container;
        this.css = this.app.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.applicationNode}).inject(this.container);

        this.topNode = new Element("div", {"styles": this.css.applicationTopNode}).inject(this.node);
     //   this.topNode.setStyle("background-color", this.bgColors[(Math.random()*10).toInt()]);

        this.iconNode = new Element("div", {"styles": this.css.applicationIconNode}).inject(this.topNode);
        if (this.data.icon){
            this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.icon+")");
        }else{
            this.iconNode.setStyle("background-image", "url("+"/x_component_process_ApplicationExplorer/$Main/default/icon/application.png)")
        }

        this.textNode = new Element("div", {"styles": this.css.applicationTextNode}).inject(this.topNode);
        this.textNode.set("text", this.data.name);

        this.childNode = new Element("div", {"styles": this.css.applicationChildNode}).inject(this.node);
        this.loadChild();
    },
    loadChild: function(){
        this.app.action.listProcess(function(json){
            if (json.data.length){
                json.data.each(function(process){
                    new MWF.xApplication.process.TaskCenter.Process(process, this, this.childNode);
                }.bind(this));
            }else{
                this.node.setStyle("display", "none");
            }
        }.bind(this), null, this.data.id)

    }
});

MWF.xApplication.process.TaskCenter.Process = new Class({
    initialize: function(data, application, container){
        this.data = data;
        this.application = application;
        this.app = this.application.app;
        this.container = container;
        this.css = this.app.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div.processItem", {"styles": this.css.startProcessNode}).inject(this.container);

        this.iconNode = new Element("div", {"styles": this.css.processIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.processTextNode}).inject(this.node);

        this.textNode.set({
            "text": this.data.name,
            "title": this.data.name+"-"+this.data.description
        });
        var _self = this;
        this.node.addEvents({
            "mouseover": function(e){this.node.setStyles(this.css.startProcessNode_over);}.bind(this),
            "mouseout": function(e){this.node.setStyles(this.css.startProcessNode_out);}.bind(this),
            "click": function(e){
                this.startProcess(e);
            }.bind(this)
        });
    },
    startProcess: function(e){
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
    afterStartProcess: function(data, title, processName){
        var workInfors = [];
        var currentTask = [];

        data.each(function(work){
            if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));

        if (currentTask.length==1){
            var options = {"workId": currentTask[0], "appId": currentTask[0]};
            this.app.desktop.openApplication(null, "process.Work", options);

            this.createStartWorkResault(workInfors, title, processName, false);
        }else{
            this.createStartWorkResault(workInfors, title, processName, true);
        }
    },
    getStartWorkInforObj: function(work){
        var users = [];
        var currentTask = "";
        work.taskList.each(function(task, idx){
            users.push(task.person+"("+task.department + ")");
            if (work.currentTaskIndex==idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
    },
    createStartWorkResault: function(workInfors, title, processName, isopen){
        var content = ""
        workInfors.each(function(infor){
            content += "<div><b>"+this.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+infor.activity+"</font>, "+this.app.lp.nextUser+"<font style=\"color: #ea621f\">"+infor.users.join(", ")+"</font></b>"
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
            _self.app.desktop.openApplication(e, "process.Work", options);nbyc
        });
    }

});

MWF.xApplication.process.TaskCenter.List = new Class({
    initialize: function(container, app, filterData){
        this.container = $(container);
        this.app = app;
        this.css = this.app.css;
        this.currentPageData = [];
        this.nextPageData = [];
        this.prevPageData = [];

        this.initData();
        this.filterData = null;
        if (filterData) this.filterData = filterData;

        this.load();
    },
    initData: function(){
        this.count = 0;
        this.isCountShow = false;
        this.currentPage = 1;
        this.pageCount = 20;
        this.pages = 0;

        this.items = [];
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;

        this.filterApplication = "";
        this.currentFilterNode = null;
        this.filterListNode = null;
    },
    load: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);

        this.contentNode = new Element("div", {"styles": this.css.listContentNode}).inject(this.container);

        this.createActionBarNode();
        this.createListAreaNode();

        this.resetListAreaHeight();
        this.app.addEvent("resize", this.resetListAreaHeight.bind(this));

        this.setAppContentSize();

    //    this.listItemNext();
    },
    refresh: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);
        this.initData();
        this.filterData = null;
        this.applicationFilterAreaNode.empty();
        this.createAppFilterNodes();
        this.listAreaNode.empty();
        this.resetListAreaHeight();
        this.app.getWorkCounts();
    },
    refilter: function(){
        this.mask = new MWF.widget.Mask({"style": "desktop"});
        this.mask.loadNode(this.app.content);
        this.initData();
        this.applicationFilterAreaNode.empty();
        this.createAppFilterNodes();
        this.listAreaNode.empty();
        this.resetListAreaHeight();
    },
    listItemNext: function(count){
        debugger;
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    debugger;
                    this.count = json.count;


                    if (!this.isCountShow){
                        this.currentFilterNode.getFirst("span").set("text", "("+this.count+")");
                        this.isCountShow = true;
                    }
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(task){
                        this.items.push(this._createItem(task));
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.listItemNext();
                    }

                    this.mask.hide();
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    createActionBarNode: function(){
        this.actionBarNode = new Element("div", {"styles": this.css.actionBarNode}).inject(this.contentNode);

        this.isFilterOpen = false;
        this.filterActionNode = new Element("div", {
            "styles": this.css.filterActionNode,
            "text": this.app.lp.filter
        }).inject(this.actionBarNode);
        this.filterActionNode.addEvents({
            "click": function (e){this.showOrHideFilter();e.stopPropagation();}.bind(this)
        });

        this.applicationFilterAreaNode = new Element("div", {"styles": this.css.applicationFilterAreaNode}).inject(this.actionBarNode);

        this.createAppFilterNodes();
    },
    showOrHideFilter: function(){
        if (!this.isFilterOpen){
            if (!this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.showFilter();
        }else{
            if (this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.hideFilter();
        }
    },
    showFilter: function(){
        this.filterActionNode.setStyles(this.css.filterActionNode_check);
        if (!this.filterAreaNode) this.createFilterAreaNode();

        this.filterAreaTipNode.setStyle("display", "block");
        this.filterAreaNode.setStyle("display", "block");
        this.resizeFilterAreaNode();
        var toStyle = {
            "width": "460px",
            "height": "500px"
        }

        this.isFilterOpen = true;

        this.filterAreaMorph.start(toStyle).chain(function(){
            this.createFilterAreaTitle();
            this.createFilterAreaContent();

            this.hideFilterFun = this.hideFilter.bind(this);
            $(document.body).addEvent("click", this.hideFilterFun);
        }.bind(this));
    },
    hideFilter: function(){
        if (this.filterAreaNode){
            var toStyle = {
                "width": "460px",
                "height": "0px"
            }
            this.filterAreaNode.empty();
            this.isFilterOpen = false;
            this.filterAreaMorph.start(toStyle).chain(function(){
                this.filterAreaNode.eliminate("input");
                this.filterAreaNode.setStyle("display", "none");
                this.filterAreaTipNode.setStyle("display", "none");
                this.filterActionNode.setStyles(this.css.filterActionNode);
            }.bind(this));

            $(document.body).removeEvent("click", this.hideFilterFun);
        }
    },


    createFilterAreaContent: function(){
        var contentScrollNode = new Element("div", {"styles": this.css.applicationFilterAreaContentScrollNode}).inject(this.filterAreaNode);
        var contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(contentScrollNode, {
                "style":"xApp_filter", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        var _self = this;
        this.app.getAction(function(){
            this._getFilterCount(function(json){
                var obj = json.data;
                Object.each(obj, function(v, key){
                    var categoryNode = new Element("div", {"styles": this.css.applicationFilterCategoryNode}).inject(contentNode);
                    categoryNode.set("text", this.app.lp[key]);
                    var itemAreaNode = new Element("div", {"styles": this.css.applicationFilterItemAreaNode}).inject(contentNode);

       //             for (var x=0; x<10; x++){
                        v.each(function(item){
                            var itemNode = new Element("div", {"styles": this.css.applicationFilterItemNode}).inject(itemAreaNode);
                            //itemNode.set("text", item.name+"("+item.count+")");
                            itemNode.set("text", item.name);
                            itemNode.store("value", item.value);
                            itemNode.store("textname", item.name);
                            itemNode.store("key", key);

                            itemNode.addEvent("click", function(){
                                if (this.hasClass("applicationFilterItemNode_over")){
                                    _self.unSelectedFilterItem(this);
                                }else{
                                    _self.selectedFilterItem(this);
                                }
                            });
                            if (this.filterData){
                                if (this.filterData[key]){
                                    if (item.value == this.filterData[key].value){
                                        this.selectedFilterItem(itemNode);
                                    }
                                }
                            }


                        }.bind(this));
         //           }


                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    _getFilterCount: function(callback){
        this.app.action.listTaskCompletedFilterCount(function(json){
            if (callback) callback(json);
        });
    },
    unSelectedFilterItem: function(item){
        if (item.hasClass("applicationFilterItemNode_over")){
            var value = item.retrieve("value");
            var name = item.retrieve("textname");
            var key = item.retrieve("key");

            item.setStyles(this.css.applicationFilterItemNode);
            item.removeClass("applicationFilterItemNode_over");
            item.addClass("applicationFilterItemNode");

            if (!this.filterData) this.filterData = {};
            this.filterData[key] = null;
            delete this.filterData[key];

            item.getParent().eliminate("current");
        }
    },
    selectedFilterItem: function(item){
        if (!item.hasClass("applicationFilterItemNode_over")){
            var current = item.getParent().retrieve("current");
            if (current) this.unSelectedFilterItem(current);

            var value = item.retrieve("value");
            var key = item.retrieve("key");
            var name = item.retrieve("textname");

            item.setStyles(this.css.applicationFilterItemNode_over);
            item.removeClass("applicationFilterItemNode");
            item.addClass("applicationFilterItemNode_over");

            if (!this.filterData) this.filterData = {};
            this.filterData[key] = {"value": value, "name": name};

            item.getParent().store("current", item);
        }
    },

    createFilterAreaTitle: function(){
        var titleNode = new Element("div", {"styles": this.app.css.filterAreaTitleNode}).inject(this.filterAreaNode);
        var okNode = new Element("div", {"styles": this.app.css.filterAreaTitleActionOkNode, "text": this.app.lp.ok}).inject(titleNode);
        var clearNode = new Element("div", {"styles": this.app.css.filterAreaTitleActionClearNode, "text": this.app.lp.clear}).inject(titleNode);
        clearNode.addEvent("click", function(){
            this.filterAreaNode.getElements(".filterItem").each(function(el){
                this.unSelectedFilterItem(el);
            }.bind(this));
            var input = this.filterAreaNode.retrieve("input");
            input.set("value", "");
            this.filterData = null;

            this.hideFilter();
            this.refilter();
        }.bind(this));
        okNode.addEvent("click", function(){
            var input = this.filterAreaNode.retrieve("input");
            if (!this.filterData) this.filterData = {};
            var key = input.get("value");
            if (key && key!=this.app.lp.searchKey){
                this.filterData.key = key;
            }else{
                this.filterData.key = "";
                delete this.filterData.key
            }

            this.hideFilter();
            this.refilter();
        }.bind(this));

        var searchNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchNode}).inject(titleNode);
        var searchIconNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchIconNode}).inject(searchNode);
        var searchInputAreaNode = new Element("div", {"styles": this.app.css.filterAreaTitleSearchInputAreaNode}).inject(searchNode);
        var searchInputNode = new Element("input", {"styles": this.app.css.filterAreaTitleSearchInputNode, "value": this.app.lp.searchKey}).inject(searchInputAreaNode);
        if (this.filterData){
            if (this.filterData.key) searchInputNode.set("value", this.filterData.key);
        }
        this.filterAreaNode.store("input", searchInputNode);

        var key = this.app.lp.searchKey;
        searchInputNode.addEvents({
            "blur": function(){if (!this.get("value")) this.set("value", key)},
            "focus": function(){if (this.get("value")==key) this.set("value", "")}
        });
    },

    createFilterAreaNode: function(){
        this.filterAreaNode = new Element("div", {"styles": this.app.css.filterAreaNode}).inject(this.container);
        this.filterAreaNode.addEvent("click", function(e){e.stopPropagation();});

        this.filterAreaTipNode = new Element("div", {"styles": this.app.css.filterAreaTipNode}).inject(this.container);
        //var size = this.filterActionNode.getSize();
        this.filterAreaNode.setStyles({
            "width": "460px",
            "height": "0px"
        });
        this.filterAreaNode.position({
            relativeTo: this.actionBarNode,
            position: 'bottomRight',
            edge: 'upperRight',
            offset: {x:0, y: -2}
        });
        this.filterAreaTipNode.position({
            relativeTo: this.filterAreaNode,
            position: 'topRight',
            edge: 'bottomRight',
            offset: {x:-26, y: 1}
        });
        this.app.addEvent("resize", function(){
            this.resizeFilterAreaNode();
        }.bind(this));

        this.filterAreaMorph = new Fx.Morph(this.filterAreaNode, {
            duration: '100',
            transition: Fx.Transitions.Sine.easeInOut
        });
    },
    resizeFilterAreaNode: function(){
        if (this.filterAreaNode){
            this.filterAreaNode.position({
                relativeTo: this.actionBarNode,
                position: 'bottomRight',
                edge: 'upperRight',
                offset: {x:0, y: -2}
            });
            if (this.filterAreaTipNode){
                this.filterAreaTipNode.position({
                    relativeTo: this.filterAreaNode,
                    position: 'topRight',
                    edge: 'bottomRight',
                    offset: {x:-26, y: 1}
                });
            }
        }
    },


    createAppFilterNodes: function(){
        var allApp = {"name": this.app.lp.all, "application": "", "count": 0};
        this.allAppFilterNode = this.createAppFilterNode(allApp, "appFilterNode_current");
        this.currentFilterNode = this.allAppFilterNode;

        this._getApplicationCount(function(json){
            json.data.each(function(app){
                this.createAppFilterNode(app);
            }.bind(this));
        }.bind(this));
    },

    createAppFilterNode: function(app, style){
        style = style || "appFilterNode";
        var node = new Element("div", {"styles": this.app.css[style]}).inject(this.applicationFilterAreaNode);
        var text = (app.count) ? app.name+"<span>("+app.count+")</span>" : app.name+"<span></span>"
        node.set({"html": text, "id": app.value});
        var _self = this;
        node.addEvent("click", function(e){
            _self.filterByApplication(this);
        });
        return node;
    },
    filterByApplication: function(node){
        var id = node.get("id");
        if (!id){
            this.refresh();
        }else{
            if (this.currentFilterNode) this.currentFilterNode.setStyles(this.app.css.appFilterNode);

            this.initData();
            this.filterApplication = id;
            this.listAreaNode.empty();
            this.resetListAreaHeight();

            this.currentFilterNode = node;
            this.currentFilterNode.setStyles(this.app.css.appFilterNode_current);
        }
    },

    createListAreaNode: function(){
        this.listScrollAreaNode = new Element("div", {"styles": this.css.listScrollAreaNode}).inject(this.contentNode);
        this.listAreaNode = new Element("div", {"styles": this.css.listAreaNode}).inject(this.listScrollAreaNode);
        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.listScrollAreaNode, {
                "style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){
                    var scrollSize = _self.listScrollAreaNode.getScrollSize();
                    var clientSize = _self.listScrollAreaNode.getSize();
                    var scrollHeight = scrollSize.y-clientSize.y;
                    if (y+200>scrollHeight) {
                        if (!_self.isElementLoaded) _self.listItemNext();
                    }
                }
            });
        }.bind(this));
    },
    setAppContentSize: function(){
        var size = this.app.contentNode.getSize();
        var x = this.container.getSize().x+size.x;
        this.container.setStyle("width", ""+x+"px");
    },
    resetListAreaHeight: function(){
        var contentSize = this.app.contentNode.getSize();
        this.contentNode.setStyle("width", ""+contentSize.x+"px");

        var size = this.contentNode.getSize();
        var barSize = this.actionBarNode.getSize();
        var y = size.y - barSize.y;

        this.listScrollAreaNode.setStyle("height", ""+y+"px");

        if (this.listAreaNode){
            var count = (size.x/402).toInt();
            var x = 402 * count;
            var m = (size.x-x)/2;
            this.listAreaNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });

            if (this.actionBarNode) this.actionBarNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });

            var hCount = (y/102).toInt()+1;
            this.pageCount = count*hCount;

            if (this.items.length<this.pageCount){
                this.listItemNext(this.pageCount-this.items.length);
            }
            //this.listAreaNode
        }

    },

    show: function(){
        debugger;
        //if (!this.app.contentScroll){
        //    this.app.contentScroll = new Fx.Scroll(this.app.contentNode, {"wheelStops": false});
        //}
        //this.app.contentScroll.toElement(this.contentNode, "x");

        if (this.app.currentList) this.app.currentList.hide();
        this.app.currentList = this;
        this.contentNode.setStyle("display", "block");
        //this.refresh();
    },
    hide: function(){
        this.contentNode.setStyle("display", "none");
    },

    _getCurrentPageData: function(callback, count){
        this.app.getAction(function(){
            if (this.filterApplication){
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNextByApp(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount, this.filterApplication);
            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);
            }
        }.bind(this));
    },
    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listTaskApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.TaskList.Item(task, this)
    }

});

MWF.xApplication.process.TaskCenter.TaskList = new Class({
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
                this.app.action.listTaskFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listTaskNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    }

    //createActionBarNode: function(){
    //    this.actionBarNode = new Element("div", {"styles": this.css.actionBarNode}).inject(this.contentNode);
    //
    //    //this.isFilterOpen = false;
    //    //this.filterActionNode = new Element("div", {
    //    //    "styles": this.css.filterActionNode,
    //    //    "text": this.app.lp.filter
    //    //}).inject(this.actionBarNode);
    //    //this.filterActionNode.addEvents({
    //    //    "click": function (e){this.showOrHideFilter();e.stopPropagation();}.bind(this)
    //    //});
    //
    //    this.applicationFilterAreaNode = new Element("div", {"styles": this.css.applicationFilterAreaNode}).inject(this.actionBarNode);
    //
    //    this.createAppFilterNodes();
    //}
});
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
MWF.xApplication.process.TaskCenter.ReadedList = new Class({
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
                this.app.action.listReadedFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listReadedNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listReadedApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.ReadedList.Item(task, this)
    },
    _getFilterCount: function(callback){
        this.app.action.listReadedFilterCount(function(json){
            if (callback) callback(json);
        });
    }
});
MWF.xApplication.process.TaskCenter.ReviewList = new Class({
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
                this.app.action.listReviewFilter(function(json){

                    if (callback) callback(json);
                }, null, id, count || this.pageCount, data);

            }else{
                var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
                this.app.action.listReviewNext(function(json){
                    if (callback) callback(json);
                }, null, id, count || this.pageCount);

            }

        }.bind(this));
    },

    _getApplicationCount: function(callback){
        this.app.getAction(function(){
            this.app.action.listReviewApplication(function(json){
                if (callback) callback(json);
            }.bind(this));
        }.bind(this));
    },
    _createItem: function(task){
        return new MWF.xApplication.process.TaskCenter.ReviewList.Item(task, this)
    },
    _getFilterCount: function(callback){
        this.app.action.listReviewFilterCount(function(json){
            if (callback) callback(json);
        });
    }
});



MWF.xApplication.process.TaskCenter.List.Item = new Class({
    initialize: function(data, list){
        this.data = data;
        this.list = list;
        this.container = this.list.listAreaNode;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.list.css.itemNode}).inject(this.container);
        this.applicationIconAreaNode = new Element("div", {"styles": this.list.css.itemApplicationIconAreaNode, "title": this.data.applicationName}).inject(this.node);
        this.applicationIconNode = new Element("div", {"styles": this.list.css.itemApplicationIconNode}).inject(this.applicationIconAreaNode);
        this.timeIconNode = new Element("div", {"styles": this.list.css.itemTimeIconNode}).inject(this.node);

        this.contentNode = new Element("div", {"styles": this.list.css.itemContentNode}).inject(this.node);
        this.titleNode = new Element("div", {"styles": this.list.css.itemTitleNode}).inject(this.contentNode);
        this.inforNode = new Element("div", {"styles": this.list.css.itemInforNode}).inject(this.contentNode);

        this.newIconNode = new Element("div", {"styles": this.list.css.itemNewIconNode}).inject(this.applicationIconAreaNode);

        this.setContent();
        this.setNewIcon();
        this.setEvent();
        this.setTimeIcon();

        this.node.fade("in");
    },
    setTimeIcon: function(){
        //this.data.expireTime = "2016-08-05 19:00";
        this.timeIconNode.empty();
        if (this.data.expireTime){
            var d1 = Date.parse(this.data.expireTime);
            var d2 = Date.parse(this.data.createTime);
            var now = new Date();
            var time1 = d2.diff(now, "second");
            var time2 = now.diff(d1, "second");
            var time3 = d2.diff(d1, "second");

            var size = this.timeIconNode.getSize();
            var n = time1/time3;
            var height = size.y*((n>1) ? 1 : n);
            if (height<5) height = 5;
            var mTop = size.y - height;
            this.expireIconNode = new Element("div", {
                "styles": {"height": ""+height+"px", "margin-top": ""+mTop+"px"}
            }).inject(this.timeIconNode);

            var color = "#00FF00";
            var text = this.list.app.lp.expire1;
            text = text.replace(/{time}/g, this.data.expireTime);
            if (n<0.6){

            }else if(n<0.8){
                color = "yellow";
            }else if (n<1){
                text = this.list.app.lp.expire2;
                text = text.replace(/{time}/g, this.data.expireTime);
                color = "orange";
            }else if (n<2){
                color = "red";
                text = this.list.app.lp.expire3;
                text = text.replace(/{time}/g, this.data.expireTime);
            }else{
                color = "red";
                text = this.list.app.lp.expire3;
                text = text.replace(/{time}/g, this.data.expireTime);
            }
            this.expireIconNode.setStyle("background", color);
            this.expireIconNode.set("title", text);

            //this.expireIconNode.setStyle("background", "linear-gradient(to top, "+beginColor+", "+middleColor+" "+middlePercent+", "+endColor+")");


        }
        //this.data.expireTime
    },

    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){this.showAction();}.bind(this),
            "mouseout": function(){this.hideAction();}.bind(this)
        });

        if (this.editNode){
            this.editNode.addEvent("click", function(e){
                this.editTask();
            }.bind(this));
        }
        if (this.closeNode){
            this.closeNode.addEvent("click", function(e){
                this.closeEditTask();
            }.bind(this));
        }

        if (this.titleTextNode){
            this.titleTextNode.addEvent("click", function(e){
                this.openTask(e);
            }.bind(this));
        }
    },
    showAction: function(){
     //   if (this.editNode) this.editNode.fade("in");
    //    if (this.closeNode) this.closeNode.fade("in");
    },
    hideAction: function(){
     //   if (this.editNode) this.editNode.fade("out");
    //    if (this.closeNode) this.closeNode.fade("out");
    },

    openTask: function(e){

   //     this._getJobByTask(function(data){
            var options = {"workId": this.data.work, "appId": this.data.work};
            this.list.app.desktop.openApplication(e, "process.Work", options);
   //     }.bind(this));
    },

    closeEditTask: function(callback){
        this.closeNode.setStyle("display", "none");

        this.flowInforLeftNode.destroy();
        this.flowInforRightNode.destroy();
        this.flowInforContentNode.destroy();
        this.flowInforScrollNode.destroy();

        this.flowInforNode.destroy();
        this.processNode.destroy();

        this.flowInforScrollFx = null;
        this.flowInforLeftNode = null;
        this.flowInforRightNode = null;
        this.flowInforScrollNode = null;
        this.flowInforContentNode = null;

        delete this.flowInforScrollFx;
        delete this.flowInforLeftNode;
        delete this.flowInforRightNode;
        delete this.flowInforScrollNode;
        delete this.flowInforContentNode;
        delete this.flowInforNode;
        delete this.processNode;

        var p = this.nodeClone.getPosition(this.nodeClone.getOffsetParent());
        this.list.css.itemNode_edit_from.top = ""+ p.y+"px";
        this.list.css.itemNode_edit_from.left = ""+ p.x+"px";

        var morph = new Fx.Morph(this.node, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeIn,
            "onComplete": function(){
                this.nodeClone.destroy();
                this.list.app.content.unmask();
                this.node.setStyles(this.list.css.itemNode);
                this.node.setStyle("opacity", 1);
                this.list.app.removeEvent("resize", this.resizeEditNodeFun);

                this.editNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit_from);
    },

    editTask: function(){
        this.list.app.content.mask({
            "destroyOnHide": true,
            "id": "mask_"+this.data.id,
            "style": this.list.css.maskNode
        });

        this._getJobByTask(function(data){
            this.nodeClone = this.node.clone(false);
            this.nodeClone.inject(this.node, "after");
            this.node.setStyles(this.list.css.itemNode_edit_from);
            this.node.position({
                relativeTo: this.nodeClone,
                position: "topleft",
                edge: "topleft"
            });

            this.showEditNode(data);
        }.bind(this));
    },
    setEditTaskNodes: function(data){
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.node);
        this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.node);
        this.setFlowInfor(data);
        this.setProcessor();
    },
    setFlowChart: function(data){
        var idx = 0;
        data.workLogList.each(function(worklog){
            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, worklog.fromActivityToken == data.task.activityToken);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    setFlowInfor: function(data){
        this.flowInforLeftNode = new Element("div", {"styles": this.list.css.flowInforLeftNode}).inject(this.flowInforNode);
        this.flowInforRightNode = new Element("div", {"styles": this.list.css.flowInforRightNode}).inject(this.flowInforNode);
        this.flowInforScrollNode = new Element("div", {"styles": this.list.css.flowInforScrollNode}).inject(this.flowInforNode);
        this.flowInforContentNode = new Element("div", {"styles": this.list.css.flowInforContentNode}).inject(this.flowInforScrollNode);

        var idx = this.setFlowChart(data);

        var x = (idx*40)+((idx-1)*16);
        this.flowInforContentNode.setStyle("width", ""+x+"px");

        this.setFlowInforScroll();
    },
    toFlowInforLeft: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scroll.x>0){
            var scrollX = scroll.x-size.x;
            if (scrollX<0) scrollX = 0;

            if (scrollX>0){
            //    this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            }else{
                this.flowInforLeftNode.setStyle("background-image", "");
            }
            if (scrollX+size.x<scrollSize.x){
                this.flowInforRightNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/right.png)");
            }else{
            //    this.flowInforRightNode.setStyle("background-image", "");
            }

            this.flowInforScrollFx.start(scrollX);
        }


    },
    toFlowInforRight: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scroll.x+size.x<scrollSize.x){
            var scrollX = scroll.x+size.x;
            if (scrollX>scrollSize.x) scrollX = scrollSize.x;

            if (scrollX>0){
                this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            }else{
                //this.flowInforLeftNode.setStyle("background-image", "");
            }
            if (scrollX+size.x<scrollSize.x){
                //this.flowInforRightNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/right.png)");
            }else{
                this.flowInforRightNode.setStyle("background-image", "");
            }

            this.flowInforScrollFx.start(scrollX);
        }

    },
    setFlowInforScroll: function(){
        var size = this.flowInforScrollNode.getSize();
        var scrollSize = this.flowInforScrollNode.getScrollSize();
        var scroll = this.flowInforScrollNode.getScroll();

        if (scrollSize.x>size.x){
            if (!this.flowInforScrollFx) this.flowInforScrollFx = new Fx.Scroll(this.flowInforScrollNode, {"wheelStops": false});
            this.flowInforScrollFx.toRight();
            this.flowInforLeftNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/processor/left.png)");
            this.flowInforLeftNode.addEvent("click", function(){this.toFlowInforLeft();}.bind(this));
            this.flowInforRightNode.addEvent("click", function(){this.toFlowInforRight();}.bind(this));
        }
    },
    createFlowInforWorklogNode: function(activityName, taskCompleteList, taskList, idx, isCurrent){
        if (idx!=0) var logLineNode = new Element("div", {"styles": this.list.css.logLineNode}).inject(this.flowInforContentNode);
        var logActivityNode = new Element("div", {"styles": this.list.css.logActivityNode}).inject(this.flowInforContentNode);
        var logActivityIconNode = new Element("div", {"styles": this.list.css.logActivityIconNode}).inject(logActivityNode);
        var logActivityTextNode = new Element("div", {"styles": this.list.css.logActivityTextNode, "text": activityName}).inject(logActivityNode);

        var iconName = "user";
        var iconSuffix = "";
        if ((taskCompleteList.length+taskList.length)>1) iconName = "users";
        if (isCurrent) iconSuffix = "_red";


        var inforNode = new Element("div", {"styles": this.list.css.logInforNode});
        taskCompleteList.each(function(route){
            var routeNode = new Element("div", {"styles": this.list.css.logRouteNode}).inject(inforNode);
            routeNode.set("text", route.person+": ");
            var opinionNode = new Element("div", {"styles": this.list.css.logOpinionNode}).inject(inforNode);
            if (!route.opinion) route.opinion = "";
            opinionNode.set("text", "["+route.routeName+"] "+route.opinion);
            var timeNode = new Element("div", {"styles": this.list.css.logTimeNode}).inject(inforNode);
            timeNode.set("text", route.completedTime);

            if (this.list.app.desktop.session.user.name == route.person) if (!iconSuffix) iconSuffix = "_yellow";
        }.bind(this));

        taskList.each(function(task){
            var taskTextNode = new Element("div", {"styles": this.list.css.taskTextNode}).inject(inforNode);
            taskTextNode.set("text", task.person+" "+this.list.app.lp.processing);
        }.bind(this));

        var icon = "url("+"/x_component_process_TaskCenter/$Main/default/processor/"+iconName+iconSuffix+".png)";
        logActivityIconNode.setStyle("background-image", icon);

        if (taskList.length){
            var countNode = new Element("div", {"styles": this.list.css.logTaskCountNode}).inject(logActivityNode);
            var text = (taskList.length>99) ? "99+" : taskList.length;
            countNode.set("text", text);
        }
        new mBox.Tooltip({
            content: inforNode,
            setStyles: {content: {padding: 10, lineHeight: 20}},
            attach: logActivityNode,
            transition: 'flyin',
            offset: {
                x: this.list.app.contentNode.getScroll().x,
                y: this.list.listScrollAreaNode.getScroll().y
            }
        });
    },

    setProcessor: function(){
        var _self = this;
        MWF.xDesktop.requireApp("process.Work", "Processor", function(){
            new MWF.xApplication.process.Work.Processor(this.processNode, this.data, {
                "style": "task",
                "onCancel": function(){
                    _self.closeEditTask();
                    delete this;
                },
                "onSubmit": function(routeName, opinion){
                    _self.submitTask(routeName, opinion, this);
                    delete this;
                }
            })
        }.bind(this));
    },
    addMessage: function(data){
        var content = "";

        if (data.length){
            data.each(function(work){
                var users = [];
                work.taskList.each(function(task){
                    users.push(task.person+"("+task.department+")");
                }.bind(this));

                content += "<div><b>"+this.list.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+work.fromActivityName+"</font>, "+this.list.app.lp.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b></div>"
            }.bind(this));
        }else{
            content += this.list.app.lp.workCompleted;
        }
        //
        //
        //data.taskList.each(function(list){
        //    content += "<div><b>"+this.list.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+list.activityName+"</font>, "+this.list.app.lp.nextUser+"<font style=\"color: #ea621f\">"+list.personList.join(", ")+"</font></b></div>"
        //}.bind(this));

        var msg = {
            "subject": this.list.app.lp.taskProcessed,
            "content": "<div>"+this.list.app.lp.taskProcessedMessage+"“"+this.data.title+"”</div>"+content
        };

        layout.desktop.message.addTooltip(msg);
        layout.desktop.message.addMessage(msg);
    },
    submitTask: function(routeName, opinion, processor){
        if (!opinion) opinion = routeName;

        this.data.routeName = routeName;
        this.data.opinion = opinion;

        this.list.app.action.processTask(function(json){
        //    this.list.app.notice(this.list.app.lp.taskProcessed, "success");
            processor.destroy();
            this.closeEditTask(function(){
                this.node.destroy();
                this.list.refresh();
                this.addMessage(json.data);
                delete this;
            }.bind(this));
        }.bind(this), null, this.data.id, this.data);
    },

    resizeEditNode: function(){
        var p = this.getEditNodePosition();
        var size = this.list.app.content.getSize();
        var maskNode = this.list.app.window.node.getElement("#mask_"+this.data.id);

        if (maskNode) maskNode.setStyles({"width": ""+size.x+"px", "height": ""+size.y+"px"});

        this.node.setStyles({"top": ""+ p.y+"px", "left": ""+ p.x +"px"});
    },

    getEditNodePosition: function(){
        var size = this.list.app.content.getSize();

        var top = size.y/2-160;
        var left = size.x/2-300;
        if (top<0) top = 0;
        return {"x": left, "y": top};
    },
    showEditNode: function(data, callback){
        var p = this.getEditNodePosition();
        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";

        this.editNode.setStyle("display", "none");

        var morph = new Fx.Morph(this.node, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeOut,
            "onComplete": function(){
                this.resizeEditNodeFun = this.resizeEditNode.bind(this);
                this.list.app.addEvent("resize", this.resizeEditNodeFun);
                this.setEditTaskNodes(data);

                this.closeNode.setStyle("display", "block");

                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit);
    },

    _getJobByTask: function(callback){
        this.list.app.action.getSimpleJobByTask(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },

    setContent: function(){
        this.titleActionNode = new Element("div", {"styles": this.list.css.titleActionNode}).inject(this.titleNode);
        this.titleTextNode = new Element("div", {"styles": this.list.css.titleTextNode, "title": this.data.title}).inject(this.titleNode);

        this.titleTextNode.set("html", "<font style=\"color: #333;\">["+this.data.processName+"]</font>"+this.data.title);

        //var processNode = new Element("div", {"styles": this.list.css.itemInforProcessNode,"text": this.data.processName}).inject(this.inforNode);
        var timeNode = new Element("div", {"styles": this.list.css.itemInforTimeNode, "text": this.data.startTime}).inject(this.inforNode);
        var activityNode = new Element("div", {"styles": this.list.css.itemInforActivityNode, "text": this.data.activityName, "title": this.data.activityName}).inject(this.inforNode);

        this.loadActions();
        this.loadApplicationIcon();
    //    this.setTimeIconNode();
    },
    loadActions: function(){
        if (this.data.allowRapid){
            this.editNode = new Element("div", {"styles": this.list.css.titleActionEditNode}).inject(this.titleActionNode);
            this.closeNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.titleActionNode);
        }
    },
    loadApplicationIcon: function(){
        this.getApplicationIcon(function(icon){
            if (icon){
                this.applicationIconNode.setStyle("background-image", "url(data:image/png;base64,"+icon+")");
            }else{
                this.applicationIconNode.setStyle("background-image", "url("+"/x_component_process_ApplicationExplorer/$Main/default/icon/application.png)")
            }
        }.bind(this));
    },
    getApplicationIcon: function(callback){
        var icon = this.list.app.appIcons[this.data.application];
        if (!icon) {
            this.list.app.action.getApplicationIcon(function (json) {
                if (json.data){
                    this.list.app.appIcons[this.data.application] = json.data.icon;
                    if (callback) callback(json.data.icon);
                }else{
                    this.invalidItem = true;
                    if (callback) callback("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC");
                }
            }.bind(this), function(){
                this.invalidItem = true;
                if (callback) callback("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC");
            }.bind(this), this.data.application);
        }else{
            if (callback) callback(icon);
        }
    },
    setNewIcon: function(){
        var start = new Date().parse(this.data.startTime);
        var now = new Date();
        if (now.getTime()-start.getTime()<86400000){
            this.newIconNode.setStyle("background-image", "url("+"/x_component_process_TaskCenter/$Main/default/new.png)");
        }
    },

    setTimeIconNode: function(){
        var colors = ["#FF0000", "#00d400", "#f6ff0c"];
        var idx = (Math.random()*3).toInt();
        var color = colors[idx];
        this.timeIconNode.setStyle("background-color", color);
    },


    //load: function(){
    //    this.node = this.table.insertRow(this.table.rows.length || 0);
    //    this.node.setStyles(this.list.css.itemLine);
    //
    //    this.appNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.applicationName}).inject(this.node);
    //    this.titleNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.title}).inject(this.node);
    //    this.processNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.processName}).inject(this.node);
    //    this.activityNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.activityName}).inject(this.node);
    //    this.dateNode = new Element("td", {"styles": this.list.css.itemCell, "text": this.data.updateTime}).inject(this.node);
    //
    //
    //    //this.titleNode = this.node.insertCell(this.node.cells.length || 0);
    //    //this.titleNode.setStyles(this.list.css.itemCell);
    //    //this.titleNode.set("text", this.data.title);
    //}
});

MWF.xApplication.process.TaskCenter.TaskList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List.Item
});
MWF.xApplication.process.TaskCenter.TaskCompletedList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List.Item,

    loadActions: function(){
        this.showTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionShowNode}).inject(this.titleActionNode);
        this.closeTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.titleActionNode);
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

        if (this.titleTextNode){
            this.titleTextNode.addEvent("click", function(e){
                this.showTaskCompleted(e);
            }.bind(this));
        }
    },
    setFlowChart: function(data){
        var idx = 0;
        debugger;
        data.workLogTokenList = {};
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
        if (this.showTaskCompletedNode) this.showTaskCompletedNode.fade("in");
    },
    hideAction: function(){
        if (this.showTaskCompletedNode) this.showTaskCompletedNode.fade("out");
    },
    _getJobByTaskComplete: function(){
        this.list.app.action.getSimpleJobByTaskCompleted(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },
    showTaskCompleted: function(){
        if (!this.nodeClone){
            this.list.app.content.mask({
                "destroyOnHide": true,
                "id": "mask_"+this.data.id,
                "style": this.list.css.maskNode
            });

            this._getSimpleJobByTaskComplete(function(data){
                this.nodeClone = this.node.clone(false);
                this.nodeClone.inject(this.node, "after");
                this.node.setStyles(this.list.css.itemNode_edit_from);
                this.node.position({
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
        var p = this.getEditNodePosition();
        this.list.css.itemNode_edit.top = ""+ p.y+"px";
        this.list.css.itemNode_edit.left = ""+ p.x+"px";

        this.showTaskCompletedNode.setStyle("display", "none");
        var morph = new Fx.Morph(this.node, {
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
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.node);
    //    this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.node);
        this.workInforNode = new Element("div", {"styles": this.list.css.workInforNode}).inject(this.node);
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
                    users.push(task.person+"("+task.department+")");
                }.bind(this));

                var html = "<table border=\"0\" width=\"96%\" align=\"center\"><tr>" +
                    "<td style=\"white-space: normal;word-break: break-all;word-wrap:break-word;\">"+
                    ""+lp.fileat+"<font style=\"color: #00F\"> "+log.fromTime+" </font>"+lp.flowto+"<font style=\"color: #00F\"> ["+log.fromActivityName+"] </font>" +
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
        var options = {"workId": id, "readonly": true, "appId": id};
        this.list.app.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleteedByTaskCompleted: function(e, id){
        var options = {"workCompletedId": id, "readonly": true, "appId": id};
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

        var p = this.nodeClone.getPosition(this.nodeClone.getOffsetParent());
        this.list.css.itemNode_edit_from.top = ""+ p.y+"px";
        this.list.css.itemNode_edit_from.left = ""+ p.x+"px";

        var morph = new Fx.Morph(this.node, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeIn,
            "onComplete": function(){
                this.nodeClone.destroy();
                this.nodeClone = null;
                this.list.app.content.unmask();
                this.node.setStyles(this.list.css.itemNode);
                this.node.setStyle("opacity", 1);
                this.list.app.removeEvent("resize", this.resizeEditNodeFun);

                this.showTaskCompletedNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit_from);
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
        this.showTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionReadedNode, "title": "设置为已阅"}).inject(this.titleActionNode);
        this.closeTaskCompletedNode = new Element("div", {"styles": this.list.css.titleActionCloseNode}).inject(this.titleActionNode);
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

        if (this.titleTextNode){
            this.titleTextNode.addEvent("click", function(e){
                this.showTaskCompleted(e);
            }.bind(this));
        }
    },
    setEditTaskCompleledNodes: function(data) {
        this.flowInforNode = new Element("div", {"styles": this.list.css.flowInforNode}).inject(this.node);
        //    this.processNode = new Element("div", {"styles": this.list.css.processNode}).inject(this.node);
        this.workInforNode = new Element("div", {"styles": this.list.css.workInforNode}).inject(this.node);
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
        this.setReadedAction = Element("div", {"styles": this.list.css.setReadedAction, "text": "设置为已阅"}).inject(this.node);
        this.setReadedAction.addEvent("click", function(e){
            this.setReaded(e);
        }.bind(this));
    },
    setReadedClose: function(e){
        var _self = this;
        var text = "您确定要将“"+this.data.title+"”标记为已阅吗？"
        this.list.app.confirm("infor", e, "标记已阅确认", text, 350, 130, function(){
            debugger;
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
        var text = "您确定要将“"+this.data.title+"”标记为已阅吗？"
        this.list.app.confirm("infor", e, "标记已阅确认", text, 350, 130, function(){
            debugger;
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

        var p = this.nodeClone.getPosition(this.nodeClone.getOffsetParent());
        this.list.css.itemNode_edit_from.top = ""+ p.y+"px";
        this.list.css.itemNode_edit_from.left = ""+ p.x+"px";

        var morph = new Fx.Morph(this.node, {
            "duration": 200,
            "transition": Fx.Transitions.Expo.easeIn,
            "onComplete": function(){
                this.nodeClone.destroy();
                this.nodeClone = null;
                this.list.app.content.unmask();
                this.node.setStyles(this.list.css.itemNode);
                this.node.setStyle("opacity", 1);
                this.list.app.removeEvent("resize", this.resizeEditNodeFun);

                this.showTaskCompletedNode.setStyle("display", "block");
                if (callback) callback();
            }.bind(this)
        });
        morph.start(this.list.css.itemNode_edit_from);
    }
});

MWF.xApplication.process.TaskCenter.ReadedList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList.Item,
    setFlowChart: function(data){
        var idx = 0;
        data.workLogTokenList = {};
        data.workLogList.each(function(worklog){
            data.workLogTokenList[worklog.fromActivityToken] = worklog;

            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, false);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    _getSimpleJobByTaskComplete: function(callback){
        this.list.app.action.getSimpleJobByReaded(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    }
});

MWF.xApplication.process.TaskCenter.ReviewList.Item = new Class({
    Extends: MWF.xApplication.process.TaskCenter.TaskCompletedList.Item,
    setFlowChart: function(data){
        var idx = 0;
        data.workLogTokenList = {};
        data.workLogList.each(function(worklog){
            data.workLogTokenList[worklog.fromActivityToken] = worklog;

            if (!worklog.taskCompletedList) worklog.taskCompletedList = [];
            if (!worklog.taskList) worklog.taskList = [];
            if (worklog.taskCompletedList.length || worklog.taskList.length){
                this.createFlowInforWorklogNode(worklog.fromActivityName, worklog.taskCompletedList, worklog.taskList || [], idx, false);
                idx++;
            }
        }.bind(this));
        return idx;
    },
    _getSimpleJobByTaskComplete: function(callback){
        this.list.app.action.getSimpleJobByReview(function(json){
            if (callback) callback(json.data);
        }.bind(this), null, this.data.id);
    },
    setContent: function(){
        this.titleActionNode = new Element("div", {"styles": this.list.css.titleActionNode}).inject(this.titleNode);
        this.titleTextNode = new Element("div", {"styles": this.list.css.titleTextNode, "title": this.data.title}).inject(this.titleNode);

        this.titleTextNode.set("html", "<font style=\"color: #333;\">["+this.data.processName+"]</font>"+this.data.title);

        //var processNode = new Element("div", {"styles": this.list.css.itemInforProcessNode,"text": this.data.processName}).inject(this.inforNode);
        var timeNode = new Element("div", {"styles": this.list.css.itemInforTimeNode, "text": this.data.startTime}).inject(this.inforNode);
        var activityNode = new Element("div", {"styles": this.list.css.itemInforActivityNode, "text": this.data.applicationName, "title": this.data.applicationName}).inject(this.inforNode);

        this.loadActions();
        this.loadApplicationIcon();
        //    this.setTimeIconNode();
    }
});