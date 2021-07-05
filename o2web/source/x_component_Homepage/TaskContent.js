MWF.xApplication.Homepage.TaskContent = new Class({
    Implements: [Options, Events],
    options: {
        "view": "taskContent.html"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/"+this.options.view;
        this.load();
    },
    load: function(){
        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp}, "module": this}, function(){
            this.initSize();
            this.loadCount();
            this.loadTask(function(){
                this.fireEvent("load");
            }.bind(this));

            //是否需要定时自动刷新 @todo
            //this.startProcessAction.addEvent("click", this.startProcess.bind(this));
        }.bind(this));
    },
    startProcess: function(){
        // o2.requireApp("process.TaskCenter", "lp."+o2.language, null, false);
        // o2.requireApp("process.TaskCenter", "", null, false);
         o2.requireApp([["process.TaskCenter", "lp."+o2.language], ["process.TaskCenter", ""]],"", function(){
            var obj = {
                "lp": MWF.xApplication.process.TaskCenter.LP,
                "content": this.app.content,
                "addEvent": function(type, fun){
                    this.app.addEvent(type, fun);
                }.bind(this),
                "getAction": function (callback) {
                    if (!this.action) {
                        this.action = MWF.Actions.get("x_processplatform_assemble_surface");
                        if (callback) callback();
                    } else {
                        if (callback) callback();
                    }
                },
                "desktop": layout.desktop,
                "refreshAll": function(){},
                "notice": this.app.notice,
            }
            o2.JSON.get("../x_component_process_TaskCenter/$Main/default/css.wcss", function(data){
                obj.css = data;
            }, false);

            if (!this.processStarter) this.processStarter = new MWF.xApplication.process.TaskCenter.Starter(obj, {
                "onStartProcess": function(){
                    if (this.currentTab.options.type == "task") this.currentTab.reload();
                }.bind(this)
            });
            this.processStarter.load();
         }.bind(this), true, true);
    },
    initSize: function(){
        this.setContentSize();
        this.app.addEvent("resize", this.setContentSize.bind(this));
    },
    setContentSize: function(){
        var total = this.container.getSize().y;
        var titleHeight = this.taskTitleNode.getSize().y+this.taskTitleNode.getEdgeHeight();
        var bottomHeight = this.pageAreaNode.getSize().y+this.pageAreaNode.getEdgeHeight();
        var thisHeight = this.itemContentNode.getEdgeHeight();
        var contentHeight = total-titleHeight-bottomHeight-thisHeight;
        this.itemContentNode.setStyle("height", ""+contentHeight+"px");
        this.contentHeight = contentHeight;
        //this.pageSize = (this.options.itemHeight/this.contentHeight).toInt();

        if (this.noItemNode){
            var m = (this.contentHeight- this.noItemNode.getSize().y)/2;
            this.noItemNode.setStyle("margin-top", ""+m+"px");
        }
    },


    loadCount: function(){
        o2.Actions.load("x_processplatform_assemble_surface").WorkAction.countWithPerson(layout.session.user.id, function(json){
            if (!this.itemCounts) this.itemCounts = {};
            this.itemCounts.task = json.data.task;
            this.itemCounts.taskCompleted = json.data.taskCompleted;
            this.itemCounts.read = json.data.read;
            this.itemCounts.readCompleted = json.data.readCompleted;

            this.showTabCount(this.taskTab, json.data.task);
            this.showTabCount(this.taskCompletedTab, json.data.taskCompleted);
            this.showTabCount(this.readTab, json.data.read);
            this.showTabCount(this.readCompletedTab, json.data.readCompleted);

            this.fireEvent("loadTaskCount");
            this.fireEvent("loadReadCount");
            this.fireEvent("loadTaskCompletedCount");
            this.fireEvent("loadReadCompletedCount");

        }.bind(this));
        o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.V2Count({"creatorPersonList": [layout.session.user.id]}, function(json){
            if (!this.itemCounts) this.itemCounts = {};
            this.itemCounts.draft = json.data.count;

            this.showTabCount(this.draftTab, json.data.count);

            this.fireEvent("loadDraftCount");
        }.bind(this));
        o2.Actions.load("x_processplatform_assemble_surface").DraftAction.listNext("(0)", 1, function(json){
            debugger;
            if (!this.itemCounts) this.itemCounts = {};
            this.itemCounts.processDraft = json.count;

            this.showTabCount(this.processDraftTab, json.count);

            this.fireEvent("loadProcessDraftCount");
        }.bind(this));
    },
    showTabCount: function(node, count){
        var text = node.get("text");
        node.set("text", text+"("+count+")");
    },
    tabover: function(e){
        e.currentTarget.addClass("o2_homepage_title_tab_over");
    },
    tabout: function(e){
        e.currentTarget.removeClass("o2_homepage_title_tab_over");
        //e.currentTarget.removeClass("mainColor_border").removeClass("mainColor_color");
    },


    loadTask: function(callback){
        if (!this.isLoading){
            if (!this.taskContentTab){
                this.taskContentTab = new MWF.xApplication.Homepage.TaskContent.Task(this, this.taskTab, {
                    "onLoad": function(){ if (callback) callback(); }
                });
            }else{
                this.taskContentTab.reload();
            }
            this.currentTab = this.taskContentTab;
        }
    },

    loadTaskCompleted: function(){
        if (!this.isLoading){
            if (!this.taskCompletedContentTab){
                this.taskCompletedContentTab = new MWF.xApplication.Homepage.TaskContent.TaskCompleted(this, this.taskCompletedTab);
            }else{
                this.taskCompletedContentTab.reload();
            }
            this.currentTab = this.taskCompletedContentTab;
        }
    },
    loadRead: function(){
        if (!this.isLoading){
            if (!this.readContentTab){
                this.readContentTab = new MWF.xApplication.Homepage.TaskContent.Read(this, this.readTab);
            }else{
                this.readContentTab.reload();
            }
            this.currentTab = this.readContentTab;
        }
    },
    loadReadCompleted: function(){
        if (!this.isLoading){
            if (!this.readCompletedContentTab){
                this.readCompletedContentTab = new MWF.xApplication.Homepage.TaskContent.ReadCompleted(this, this.readCompletedTab);
            }else{
                this.readCompletedContentTab.reload();
            }
            this.currentTab = this.readCompletedContentTab;
        }
    },
    loadDraft: function(){
        if (!this.isLoading){
            if (!this.draftContentTab){
                this.draftContentTab = new MWF.xApplication.Homepage.TaskContent.Draft(this, this.draftTab);
            }else{
                this.draftContentTab.reload();
            }
            this.currentTab = this.draftContentTab;
        }
    },
    loadProcessDraft: function(){
        if (!this.isLoading){
            if (!this.processDraftContentTab){
                this.processDraftContentTab = new MWF.xApplication.Homepage.TaskContent.ProcessDraft(this, this.processDraftTab);
            }else{
                this.processDraftContentTab.reload();
            }
            this.currentTab = this.processDraftContentTab;
        }
    }
});

MWF.xApplication.Homepage.TaskContent.Task = new Class({
    Implements: [Options, Events],
    options: {
        "itemHeight": 40,
        "showPages": 5,
        "type": "task"
    },
    initialize: function(content, tab, options){
        this.setOptions(options);
        this.content = content;
        this.app = this.content.app;
        this.container = this.content.itemContentNode;
        this.tab = tab;
        this.load();
    },
    load: function(){
        this.beginLoadContent();
        this.showTab();
        this.initItemCount();

        this.loadItemsRes();
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(this.page, this.pageSize, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
            this.fireEvent("load");
        }.bind(this));
    },
    reload: function(){
        if (!this.content.isLoading) {
            this.beginLoadContent();
            this.showTab();
            this.initItemCount(this.page);
            this.loadItemsRes();
        }
    },
    initItemCount: function(count){
        this.page = count || 1;
        this.pageSize = (this.container.getSize().y/this.options.itemHeight).toInt();
    },
    beginLoadContent: function(){
        if (this.content.currentTab) this.content.currentTab.hideTab();
        this.container.empty();
        this.content.noItemNode = null;
        this.container.addClass("o2_homepage_area_content_loading").addClass("icon_loading");
        this.content.isLoading = true;
    },
    endLoadContent: function(){
        if (this.content.noItemNode){
            this.content.noItemNode.destroy();
            this.content.noItemNode = null;
        }
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.isLoading = false;
    },
    emptyLoadContent: function(){
        this.container.empty();
        this.container.removeClass("o2_homepage_area_content_loading").removeClass("icon_loading");
        this.content.pageAreaNode.empty();
        //this.itemContentNode.addClass("o2_homepage_task_area_content_empty").addClass("icon_notask");
        this.content.noItemNode = new Element("div.o2_homepage_task_area_content_empty_node", {"text": this.app.lp.noWork}).inject(this.container);
        var m = (this.content.contentHeight- this.content.noItemNode.getSize().y)/2;
        this.content.noItemNode.setStyle("margin-top", ""+m+"px");

        this.content.isLoading = false;
    },
    hideTab: function(){
        this.container.empty();
        this.content.currentTab = null;
        this.tab.removeClass("mainColor_color").removeClass("mainColor_border").removeClass("o2_homepage_title_tab_current").removeClass("o2_homepage_title_tab_over");
        // if (this.destroy) this.destroy();
        // o2.release(this);
    },
    showTab: function(){
        this.content.currentTab = this;
        this.tab.addClass("mainColor_color").addClass("mainColor_border").addClass("o2_homepage_title_tab_current").removeClass("o2_homepage_title_tab_over");
    },

    loadItems: function(data){
        var table = new Element("table", { "width": "100%", "border": 0, "cellpadding": 0, "cellspacing": 0 }).inject(this.container);
        data.each(function(d, i){
            this.loadItem(table, d, i);
        }.bind(this));
        this.endLoadContent();
    },
    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        new Element("div.o2_homepage_task_item_activity", {"text": d.activityName, "title": this.app.lp.currentActivity + ": " + d.activityName}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": o2.name.cn(d.creatorPerson), "title": this.app.lp.draftUser + ": " + o2.name.cn(d.creatorPerson)}).inject(cell);

        var time = d.startTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title":this.app.lp.taskStartTime + ": " + time}).inject(cell);

        return row;
    },
    loadItem: function(table, d, i){
        var row = this.loadItemRow(table, d, i)

        var _self = this;
        row.store("data", d);
        row.addEvents({
            "mouseover": function(){
                this.addClass("mainColor_color").addClass("o2_homepage_task_item_row_over");
                this.getElement("div").addClass("mainColor_bg").addClass("o2_homepage_task_item_number_over");
            },
            "mouseout": function(){
                this.removeClass("mainColor_color").removeClass("o2_homepage_task_item_row_over");
                this.getElement("div").removeClass("mainColor_bg").removeClass("o2_homepage_task_item_number_over");
            },
            "click": function(e){
                var d = this.retrieve("data");
                if (d) {
                    _self.open(e, d);
                }
            }
        })
    },
    reloadTasks: function(){
        var type = this.content.currentTab.options.type;
        if (type=="task" || type=="taskCompleted"){
            this.content.currentTab.reload();
        }
    },
    reloadReads: function(){
        var type = this.content.currentTab.options.type;
        if (type=="read" || type=="readCompleted"){
            this.content.currentTab.reload();
        }
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        var options = {
            "workId": d.work, "appId": "process.Work"+d.work,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },
    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.task){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadTaskCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadTaskCount", loadPage);
    },

    getPageCount:function(){
        var n = this.content.itemCounts.task/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    loadPage: function(node){
        var pageNode = node || this.content.pageAreaNode;
        pageNode.empty();
        if (this.pages>1){
            this.pageNode = new Element("div.o2_homepage_task_page_area").inject(pageNode);
            this.prevPageNode = new Element("div.o2_homepage_task_page_prev", {"text": this.app.lp.prevPage}).inject(this.pageNode);
            this.itemsPageAreaNode = new Element("div.o2_homepage_task_page_items").inject(this.pageNode);
            this.nextPageNode = new Element("div.o2_homepage_task_page_next", {"text": this.app.lp.nextPage}).inject(this.pageNode);

            this.prevPageNode.addEvents({
                "mouseover": function () { this.addClass("o2_homepage_task_page_prev_over").addClass("mainColor_bg"); },
                "mouseout": function () {  this.removeClass("o2_homepage_task_page_prev_over").removeClass("mainColor_bg"); },
                "click": function () { this.prevPage(); }.bind(this),
            });
            this.nextPageNode.addEvents({
                "mouseover": function () { this.addClass("o2_homepage_task_page_next_over").addClass("mainColor_bg"); },
                "mouseout": function () {  this.removeClass("o2_homepage_task_page_next_over").removeClass("mainColor_bg"); },
                "click": function () { this.nextPage(); }.bind(this),
            });

            var size = this.pageNode.getSize();
            var w1 = this.prevPageNode.getEdgeWidth();
            var w2 = this.nextPageNode.getEdgeWidth();
            var x1 = this.prevPageNode.getSize().x;
            var x2 = this.nextPageNode.getSize().x;
            var x = size.x - w1 - w2 - x1 - x2;
            var count = (x/30).toInt()-2;
            if (count<3) count = 3;
            this.options.showPages = count;
            this.loadPageNumber();
        }else{

        }
    },
    getCurrentPageRange: function(){
        var beginNumber = 1;
        var endNumber = this.pages;
        var reverse = false;
        var forward = false;
        if (this.pages > this.options.showPages){
            beginNumber = this.page-((this.options.showPages/2).toInt());
            if (beginNumber<1) beginNumber = 1;
            endNumber = beginNumber+(this.options.showPages-1);

            if (endNumber>this.pages){
                endNumber = this.pages;
                beginNumber = endNumber-(this.options.showPages-1);
                if (beginNumber<1) beginNumber = 1;
            }

            if (beginNumber>1) reverse = true;
            if (endNumber<this.pages) forward = true;
        }
        return {"beginNumber": beginNumber, "endNumber": endNumber, "reverse": reverse, "forward": forward};
    },
    loadPageNumber: function(){
        var range = this.getCurrentPageRange();

        this.itemsPageAreaNode.empty();
        if (range.reverse){
            this.reverseNode = new Element("div.o2_homepage_task_page_reverse", {"text": "..."}).inject(this.itemsPageAreaNode);
            this.reverseNode.addEvents({
                "mouseover": function () { this.addClass("o2_homepage_task_page_item_over"); },
                "mouseout": function () {  this.removeClass("o2_homepage_task_page_item_over"); },
                "click": function () {
                    this.reversePage();
                }.bind(this),
            });
        }

        for (var i=range.beginNumber; i<=range.endNumber; i++){
            var pageNode = new Element("div.o2_homepage_task_page_item", {"text": i}).inject(this.itemsPageAreaNode);
            if (this.page===i) pageNode.addClass("o2_homepage_task_page_item_current").addClass("mainColor_bg");
            pageNode.addEvents({
                "mouseover": function () { this.addClass("o2_homepage_task_page_item_over"); },
                "mouseout": function () { this.removeClass("o2_homepage_task_page_item_over"); },
                "click": function (e) {
                    this.gotoPage(e.target.get("text"));
                }.bind(this),
            });
        }

        if (range.forward){
            this.forwardNode = new Element("div.o2_homepage_task_page_forward", {"text": "..."}).inject(this.itemsPageAreaNode);
            this.forwardNode.addEvents({
                "mouseover": function () { this.addClass("o2_homepage_task_page_item_over"); },
                "mouseout": function () {  this.removeClass("o2_homepage_task_page_item_over"); },
                "click": function () {
                    this.forwardPage();
                }.bind(this),
            });
        }
    },
    prevPage: function(){
        if (this.page>1){
            this.page--;
            //this.loadPageNumber();
            this.reload();
        }
    },
    nextPage: function(){
        if (this.page<this.pages){
            this.page++;
            //this.loadPageNumber();
            this.reload();
        }
    },
    gotoPage: function(i){
        this.page = i.toInt();
        //this.loadPageNumber();
        this.reload();
    },
    reversePage: function(){
        var range = this.getCurrentPageRange();
        var endNumber = range.beginNumber-1;
        var beginNumber = endNumber-(this.options.showPages-1);
        if (beginNumber<1) beginNumber = 1;
        this.page = beginNumber+((this.options.showPages/2).toInt());
        this.reload();
    },
    forwardPage: function(){
        var range = this.getCurrentPageRange();
        var beginNumber = range.endNumber+1;
        var endNumber = beginNumber+(this.options.showPages-1);
        if (beginNumber>=this.pages) endNumber = this.pages;
        this.page = endNumber-((this.options.showPages/2).toInt());
        this.reload();
    }

});

MWF.xApplication.Homepage.TaskContent.TaskCompleted = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.Task,
    Implements: [Options, Events],
    options: {
        "type": "taskCompleted"
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").TaskCompletedAction.listMyPaging(this.page, this.pageSize, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },

    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": d.activityName, "title":d.activityName}).inject(cell);
        routeName = d.routeName || this.app.lp.nextActivity;
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        new Element("div.o2_homepage_task_item_activity", {"text": routeName, "title": this.app.lp.taskRoute + ": " + routeName}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": o2.name.cn(d.creatorPerson), "title": this.app.lp.draftUser + ": " + o2.name.cn(d.creatorPerson)}).inject(cell);

        var time = d.completedTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.taskCompletedTime + ": " + time}).inject(cell);

        return row;
    },

    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.taskCompleted){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadTaskCompletedCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadTaskCompletedCount", loadPage);
    },
    getPageCount:function(){
        var n = this.content.itemCounts.taskCompleted/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        //var options = {"workId": d.work, "appId": "process.Work"+d.work};
        var options = {
            "jobId": d.job, "appId": "process.Work"+d.job, "priorityWork": d.work,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
    }
});
MWF.xApplication.Homepage.TaskContent.Read = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.TaskCompleted,
    Implements: [Options, Events],
    options: {
        "type": "read"
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").ReadAction.listMyPaging(this.page, this.pageSize, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },
    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": d.activityName, "title":d.activityName}).inject(cell);
        // activity = d.activityName || this.app.lp.completedActivityName;
        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": activity, "title": this.app.lp.readActivity + ": " + activity}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": o2.name.cn(d.creatorPerson), "title": this.app.lp.draftUser + ": " + o2.name.cn(d.creatorPerson)}).inject(cell);

        var time = d.startTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.readStartTime + ": " + time}).inject(cell);

        return row;
    },
    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.read){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadReadCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadReadCount", loadPage);
    },
    getPageCount:function(){
        var n = this.content.itemCounts.read/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        //var options = {"workId": d.work, "appId": "process.Work"+d.work};
        var options = {
            "jobId": d.job, "appId": "process.Work"+d.job, "priorityWork": d.work,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },
});

MWF.xApplication.Homepage.TaskContent.ReadCompleted = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.TaskCompleted,
    Implements: [Options, Events],
    options: {
        "type": "readCompleted"
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").ReadCompletedAction.listMyPaging(this.page, this.pageSize, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },
    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": d.activityName, "title":d.activityName}).inject(cell);
        // activity = d.activityName || this.app.lp.completedActivityName;
        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": activity, "title": this.app.lp.readActivity + ": " + activity}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": o2.name.cn(d.creatorPerson), "title": this.app.lp.draftUser + ": " + o2.name.cn(d.creatorPerson)}).inject(cell);

        var time = d.completedTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.readCompletedTime + ": " + time}).inject(cell);

        return row;
    },
    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.readCompleted){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadReadCompletedCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadReadCompletedCount", loadPage);
    },
    getPageCount:function(){
        var n = this.content.itemCounts.readCompleted/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        //var options = {"workId": d.work, "appId": "process.Work"+d.work};
        var options = {
            "jobId": d.job, "appId": "process.Work"+d.job, "priorityWork": d.work,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },
});

MWF.xApplication.Homepage.TaskContent.Draft = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.TaskCompleted,
    Implements: [Options, Events],
    options: {
        "type": "draft"
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.V2ListCreatePaging(this.page, this.pageSize, {"relateTask": true}, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },

    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        // cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        // new Element("div.o2_homepage_task_item_activity", {"text": d.activityName, "title":d.activityName}).inject(cell);
        var activitys = [];
        var users = [];
        d.taskList.each(function(task){
            var userName = o2.name.cn(task.person);
            if (users.indexOf(userName)==-1) users.push(userName);
            if (activitys.indexOf(task.activityName)==-1) activitys.push(task.activityName);
        });

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        new Element("div.o2_homepage_task_item_activity", {"text": activitys.join(","), "title": this.app.lp.currentActivity+": "+activitys.join(",")}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": users.join(","), "title": this.app.lp.currentUser+": "+users.join(",")}).inject(cell);

        var time = d.createTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.draftTime+": "+time}).inject(cell);

        return row;
    },
    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.draft){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadDraftCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadDraftCount", loadPage);
    },
    getPageCount:function(){
        var n = this.content.itemCounts.draft/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        var options = {
            "jobId": d.job, "appId": "process.Work"+d.job, "priorityWork": d.work,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },
});

MWF.xApplication.Homepage.TaskContent.ProcessDraft = new Class({
    Extends: MWF.xApplication.Homepage.TaskContent.TaskCompleted,
    Implements: [Options, Events],
    options: {
        "type": "processDraft"
    },
    loadItemsRes: function(){
        o2.Actions.load("x_processplatform_assemble_surface").DraftAction.listMyPaging(this.page, this.pageSize, {}, function(json){
            if (json.data && json.data.length){
                this.loadItems(json.data);
                this.checkLoadPage();
            }else{
                this.emptyLoadContent();
            }
        }.bind(this));
    },

    loadItemRow: function(table, d, i){
        var row = table.insertRow(-1).addClass("o2_homepage_task_item_row");
        var idx = (this.page-1)*this.pageSize+i+1;
        var idxShow = (idx>99) ? "···" : idx;

        var cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_number");
        var numberNode = new Element("div.o2_homepage_task_item_number", {"text": idxShow, "title":idx}).inject(cell);

        //var subject = "<span>["+d.processName+"]</span> "+(d.title || this.app.lp.noSubject);
        var subject = "["+d.processName+"] "+o2.txt(d.title || this.app.lp.noSubject);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_subject");
        new Element("div.o2_homepage_task_item_subject", {"html": subject, "title":subject}).inject(cell);

        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_activity");
        new Element("div.o2_homepage_task_item_activity", {"text": this.app.lp.myProcessDraft, "title": this.app.lp.currentActivity+": "+this.app.lp.myProcessDraft}).inject(cell);

        var user = o2.name.cn(layout.session.user.name)
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_creator");
        new Element("div.o2_homepage_task_item_creator", {"text": user, "title": this.app.lp.currentUser+": "+user}).inject(cell);

        var time = d.createTime.substr(0,10);
        cell = row.insertCell(-1).addClass("o2_homepage_task_item_cell_time");
        new Element("div.o2_homepage_task_item_time", {"text": time, "title": this.app.lp.draftTime+": "+time}).inject(cell);

        return row;
    },
    checkLoadPage: function(){
        if (this.content.itemCounts && this.content.itemCounts.processDraft){
            this.getPageCount();
            this.loadPage();
        }else{
            this.addLoadPageEvent();
        }
    },
    addLoadPageEvent: function(){
        var loadPage = function(){
            this.getPageCount();
            this.loadPage();
            this.content.removeEvent("loadProcessDraftCount", loadPage);
        }.bind(this);
        this.content.addEvent("loadProcessDraftCount", loadPage);
    },
    getPageCount:function(){
        var n = this.content.itemCounts.processDraft/this.pageSize;
        var nInt = n.toInt();
        this.pages = (nInt===n) ? nInt : nInt+1;
    },
    open: function(e, d){
        //     this._getJobByTask(function(data){
        var options = {
            "draftId": d.id, "appId": "process.Work"+ d.id,
            "onAfterProcess": this.reloadTasks.bind(this),
            "onAfterReset": this.reloadTasks.bind(this),
            "onAfterRetract": this.reloadTasks.bind(this),
            "onAfterReroute": this.reloadTasks.bind(this),
            "onAfterDelete": this.reloadTasks.bind(this),
            "onAfterReaded": this.reloadReads.bind(this),
        };
        layout.openApplication(e, "process.Work", options);
        //     }.bind(this));
    },
});
