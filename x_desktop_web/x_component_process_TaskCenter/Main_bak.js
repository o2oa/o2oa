MWF.xApplication.process.TaskCenter.options.multitask = false;
MWF.xApplication.process.TaskCenter.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "process.TaskCenter",
		"icon": "icon.png",
		"width": "900",
		"height": "700",
		"title": MWF.xApplication.process.TaskCenter.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.TaskCenter.LP;
	},
	loadApplication: function(callback){
        this.tabs = [];
        this.tabShadows = [];
        this.loadTitle();
        this.loadTab();
        this.loadContent();
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
        this.startProcessTween.start("left", "-400px", "0px");
    },
    closeStartProcessArea: function(){
        if (this.startProcessAreaNode) this.startProcessTween.start("left", "0px", "-400px");
    },

    createStartProcessArea: function(){
        this.createStartProcessAreaNode();
        this.createStartProcessCloseNode();
        this.createStartProcessScrollNode();

        this.listApplications();

        this.setResizeStartProcessAreaHeight();
        this.addEvent("resize", this.setResizeStartProcessAreaHeight.bind(this));
        this.startProcessTween = new Fx.Tween(this.startProcessAreaNode, {
            "duration": "200",
            "transition": Fx.Transitions.Quad.easeOut
        });
    },
    createStartProcessAreaNode: function(){
        this.startProcessAreaNode = new Element("div", {"styles": this.css.startProcessAreaNode}).inject(this.content);

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
            MWF.require("MWF.xApplication.process.TaskCenter.Actions.RestActions", function(){
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
            this.startProcessAreaNode.setStyle("height", ""+size.y+"px");
            var y = size.y-topSize.y-20;
            this.startProcessScrollNode.setStyle("height", ""+y+"px");
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
        this.createTabItem(this.lp.task, "task.png", function(){this.showTask();}.bind(this));
        this.createTabItem(this.lp.done, "done.png", function(){this.showDone();}.bind(this));
        this.createTabItem(this.lp.draft, "draft.png", function(){this.showDraft();}.bind(this));
        this.createTabItem(this.lp.myfile, "myfile.png", function(){this.showMyfile();}.bind(this));
        this.createTabRight();

        this.createShadowNode();
    },
    createTaskList: function(){
        if (!this.contentNode) this.loadContent();
        this.taskList = new MWF.xApplication.process.TaskCenter.TaskList(this.contentNode, this);
    },
    showTask: function(){
        if (this.currentTab != "task"){
            this.showTab(0);

            if (!this.taskList){
                this.createTaskList();
            }
            this.taskList.show();
            this.currentTab = "task";
        }
    },
    createTaskCompletedList: function(){
        if (!this.contentNode) this.loadContent();
        this.taskCompletedList = new MWF.xApplication.process.TaskCenter.TaskCompletedList(this.contentNode, this);
    },
    showDone: function(){
        if (this.currentTab != "done"){
            this.showTab(1);
            if (!this.taskCompletedList){
                this.createTaskCompletedList();
            }
            this.taskCompletedList.show();
            this.currentTab = "done"
        }
    },
    showDraft: function(){
        this.showTab(2);

    },
    showMyfile: function(){
        this.showTab(3);

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

    createTabItem: function(text, icon, action){
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
        for (var i=0; i<4; i++){
            this.tabShadows.push(new Element("div", {
                "styles": this.css.tabShadowItemNode,
            }).inject(this.tabShadowNode));
        }
    },


    loadContent: function(){

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
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
    },


    searchTask: function(){

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
        this.app = this.application.app
        this.container = container;
        this.css = this.app.css;

        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.processNode}).inject(this.container);

        this.iconNode = new Element("div", {"styles": this.css.processIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.processTextNode}).inject(this.node);

        this.textNode.set({
            "text": this.data.name,
            "title": this.data.name+"-"+this.data.description
        });
        var _self = this;
        this.node.addEvents({
            "mouseover": function(e){this.node.setStyles(this.css.processNode_over);}.bind(this),
            "mouseout": function(e){this.node.setStyles(this.css.processNode_out);}.bind(this),
            "click": function(e){
                this.startProcess(e);
            }.bind(this)
        });
    },
    startProcess: function(e){

        this.app.closeStartProcessArea();
        MWF.require("MWF.xApplication.process.TaskCenter.ProcessStarter", function(){
            var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(this.data, this.app, {
                "onStarted": function(workId){
                    var _self = this;
                    var options = {"workId": workId};
                    this.app.desktop.openApplication(e, "process.Work", options);
                }.bind(this)
            });
            starter.load();
        }.bind(this));

    }

});

MWF.xApplication.process.TaskCenter.List = new Class({
    initialize: function(container, app){
        this.container = $(container);
        this.app = app;
        this.css = this.app.css;
        this.currentPageData = [];
        this.nextPageData = [];
        this.prevPageData = [];

        this.count = 0;
        this.currentPage = 1;
        this.pageCount = 20;
        this.pages = 0;

        this.load();
    },

    load: function(){
        this.contentNode = new Element("div", {"styles": this.css.listContentNode}).inject(this.container);
        this.topPageAreaNode = new Element("div", {"styles": this.css.listPageAreaNode}).inject(this.contentNode);
        this.createListAreaNode();
        this.bottomPageAreaNode = new Element("div", {"styles": this.css.listPageAreaNode}).inject(this.contentNode);

        this.resetListAreaHeight();
        this.app.addEvent("resize", this.resetListAreaHeight.bind(this));

        this.listCurrentPage();
        //this.getAction(function(){
        //    this.action.listTask(function(json){
        //        json.data.each(function(task){
        //
        //
        //
        //            //new MWF.xApplication.process.TaskCenter.Application(app, this, this.startProcessContentNode);
        //        }.bind(this));
        //    }.bind(this));
        //}.bind(this));
    },

    listCurrentPage: function(){
        this._getCurrentPageData(function(json){
            this.count = json.count;
            this.reloadPages();

            json.data.each(function(task){

                new MWF.xApplication.process.TaskCenter.List.Item(task, this);
            }.bind(this));

            //this.listTable

        }.bind(this));
    },
    reloadPages: function(){
        var tmpPages = this.count/this.pageCount;
        this.pages = tmpPages.toInt();
        if (this.pages<tmpPages) this.pages = this.pages+1;

        this.reloadItemPages(this.topPageAreaNode);
        this.reloadItemPages(this.bottomPageAreaNode);
    },
    reloadItemPages: function(node){
        var _self = this;
        for (var i=this.pages; i>=1; i--){
            var pageNode = new Element("div", {
                "styles": this.css.listPageNode,
                "text": i
            }).inject(node);
            pageNode.addEvents({
                "mouseover": function(){this.setStyle("background-color", "#ffad64");},
                "mouseout": function(){this.setStyle("background-color", "#fff");},
                "click": function(){_self.gotoPage(this.get("text"));}
            });
        }
    },
    gotoPage: function(page){

    },
    createPageNode: function(){
        this.leftAreaPageNode = new Element("div", {"styles": this.css.leftAreaPageNode}).inject(this.contentNode);
        this.rightAreaPageNode = new Element("div", {"styles": this.css.rightAreaPageNode}).inject(this.contentNode);

        this.leftPageNode = new Element("div", {"styles": this.css.leftPageNode}).inject(this.leftAreaPageNode);
        this.rightPageNode = new Element("div", {"styles": this.css.rightPageNode}).inject(this.rightAreaPageNode);

        this.leftPageIconNode = new Element("div", {"styles": this.css.leftPageIconNode}).inject(this.leftPageNode);
        this.rightPagIconeNode = new Element("div", {"styles": this.css.rightPagIconeNode}).inject(this.rightPageNode);

        this.leftAreaPageNode.addEvents({
            "mouseover": function(){
                this.leftPageNode.fade("in");
                this.rightPageNode.fade("in");
            }.bind(this),
            "mouseout": function(){
                this.leftPageNode.fade("out");
                this.rightPageNode.fade("out");
            }.bind(this),
        });
        this.rightAreaPageNode.addEvents({
            "mouseover": function(){
                this.leftPageNode.fade("in");
                this.rightPageNode.fade("in");
            }.bind(this),
            "mouseout": function(){
                this.leftPageNode.fade("out");
                this.rightPageNode.fade("out");
            }.bind(this),
        });

        this.leftPageNode.addEvent("click", function(){
            //for (i=2; i<=100; i++){
            //    var data = {
            //        "title": "这是一个测试的流程待办，当前编号是"+i,
            //        "identity": "胡起(山西-运城办公室)"
            //    };
            //    this.app.action.startWork(null, null, "e2520e24-ee72-4bff-bdee-c5e9a4a0dc0d", data)
            //}
        }.bind(this));
    },
    //createListTableNode: function(){
    //    this.listTable = new Element("table", {
    //        "styles": this.css.listAreaTable,
    //        "border": "0",
    //        "cellSpacing": "0",
    //        "cellpadding": "0"
    //    }).inject(this.listAreaNode);
    //
    //    var tr = this.listTable.insertRow(0).setStyles(this.css.listTableTitleTr);
    //    var th = new Element("th", {"styles": this.css.listTableTitle, "text": this.app.lp.list_application}).inject(tr);
    //    var th = new Element("th", {"styles": this.css.listTableTitle, "text": this.app.lp.list_title}).inject(tr);
    //    var th = new Element("th", {"styles": this.css.listTableTitle, "text": this.app.lp.list_process}).inject(tr);
    //    var th = new Element("th", {"styles": this.css.listTableTitle, "text": this.app.lp.list_activity}).inject(tr);
    //    var th = new Element("th", {"styles": this.css.listTableTitle, "text": this.app.lp.list_comedate}).inject(tr);
    //},

    createListAreaNode: function(){


    },

    createListAreaNode: function(){
        this.createPageNode();
        this.listScrollAreaNode = new Element("div", {"styles": this.css.listScrollAreaNode}).inject(this.contentNode);
        this.listAreaNode = new Element("div", {"styles": this.css.listAreaNode}).inject(this.listScrollAreaNode);
        //this.createListAreaNode();

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.listScrollAreaNode, {
                "style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
    },
    resetListAreaHeight: function(){
        var size = this.contentNode.getSize();
        var pageSize = this.topPageAreaNode.getSize();
        var y = size.y - (pageSize.y*2);

        this.listScrollAreaNode.setStyle("height", ""+y+"px");
        this.leftAreaPageNode.setStyle("height", ""+y+"px");
        this.rightAreaPageNode.setStyle("height", ""+y+"px");

        //if (this.leftPageNode){
        //    var nodeSize = this.leftPageNode.getSize();
        //    var marginY = (y-nodeSize.y)/2-100;
        //    if (marginY<0) marginY = 0;
        //
        //    this.leftPageNode.setStyle("margin-top", ""+marginY+"px");
        //    if (this.rightPageNode) this.rightPageNode.setStyle("margin-top", ""+marginY+"px");
        //}

        this.pageCount = ((y-30)/30).toInt();
    },

    show: function(){
        (new Fx.Scroll(this.container)).toElementEdge(this.contentNode);
    },

    _getCurrentPageData: function(callback){
        this.app.getAction(function(){
            var id = (this.currentPageData.length) ? this.currentPageData[this.currentPageData.length-1].id : "(0)";

            this.app.action.listTaskNext(function(json){
                if (callback) callback(json);
            }, null, id, this.pageCount);
        }.bind(this));
    }

});

MWF.xApplication.process.TaskCenter.TaskList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List
});
MWF.xApplication.process.TaskCenter.TaskCompletedList = new Class({
    Extends: MWF.xApplication.process.TaskCenter.List,

    _getCurrentPageData: function(callback){
        this.app.getAction(function(){
            var id = (this.currentPageData.length) ? this.currentPageData[this.currentPageData.length-1].id : "(0)";

            this.app.action.listTaskCompletedNext(function(json){
                if (callback) callback(json);
            }, null, id, this.pageCount);
        }.bind(this));
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
        this.applicationIconNode = new Element("div", {"styles": this.list.css.itemApplicationIconNode}).inject(this.node);
        this.timeIconNode = new Element("div", {"styles": this.list.css.itemTimeIconNode}).inject(this.node);

        this.contentNode = new Element("div", {"styles": this.list.css.itemContentNode}).inject(this.node);
        this.titleNode = new Element("div", {"styles": this.list.css.itemTitleNode}).inject(this.contentNode);
        this.inforNode = new Element("div", {"styles": this.list.css.itemInforNode}).inject(this.contentNode);

        this.setContent();
    },
    setContent: function(){
        this.titleNode.set("text", this.data.title);
        var processNode = new Element("div", {"styles": this.list.css.itemInforProcessNode,"text": this.data.processName}).inject(this.inforNode);
        var timeNode = new Element("div", {"styles": this.list.css.itemInforTimeNode, "text": this.data.updateTime}).inject(this.inforNode);
        var activityNode = new Element("div", {"styles": this.list.css.itemInforActivityNode, "text": this.data.activityName}).inject(this.inforNode);



    }
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