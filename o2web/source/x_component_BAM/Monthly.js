MWF.xApplication.BAM.monthly = MWF.xApplication.BAM.monthly || {};
MWF.xApplication.BAM.Monthly = new Class({
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(app, node, options){
        this.setOptions(options);
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.container = $(node);
        this.actions = this.app.actions;
        this.initData();
        this.load();
    },
    initData: function(){
        this.categoryDataLoaded = false;
        this.organizationDataLoaded = false;
        this.overviewDataLoaded = false;
        //////////////
        ///////////
        //////////////
        ////////////
    },


    load: function(){
        this.loadMonthlyLayout();
        //this.loadMonthly();

        //this.fireEvent("loaded");
    },
    loadMonthlyLayout: function(){
        this.tabAreaNode = new Element("div", {"styles": this.css.monthTabAreaNode}).inject(this.container);
        this.contentAreaNode = new Element("div", {"styles": this.css.monthContentAreaNode}).inject(this.container);
        this.createTab();
    },
    createTab: function(){
        var html = "<table border='0' cellpadding='0' cellSpacing='0' align='center'><tr>" +
            "<td></td><td></td><td></td><td></td><td></td><td></td>" +
            "</tr></table>";
        this.tabAreaNode.set("html", html);
        this.table = this.tabAreaNode.getElement("table");
        var cells = this.tabAreaNode.getElements("td");

        this.table.setStyles(this.css.monthTabTableNode);
        cells.setStyles(this.css.monthTabCellNode);
        cells[0].setStyle("border-left", "0px");

        this.taskTabItem = this.createTabItemNode(this.lp.monthly.task, "task", cells[0]);
        this.taskCompletedTabItem = this.createTabItemNode(this.lp.monthly.taskCompleted, "taskCompleted", cells[1]);
        this.workTabItem = this.createTabItemNode(this.lp.monthly.work, "work", cells[2]);
        this.workCompletedTabItem = this.createTabItemNode(this.lp.monthly.workCompleted, "workCompleted", cells[3]);
        this.taskExpiredTabItem = this.createTabItemNode(this.lp.monthly.taskExpired, "taskExpired", cells[4]);
        this.workExpiredTabItem = this.createTabItemNode(this.lp.monthly.workExpired, "workExpired", cells[5]);

        this.taskTabItem.addEvent("click", this.changeToTask.bind(this));
        this.taskCompletedTabItem.addEvent("click", this.changeToTaskCompleted.bind(this));
        this.workTabItem.addEvent("click", this.changeToWork.bind(this));
        this.workCompletedTabItem.addEvent("click", this.changeToWorkCompleted.bind(this));
        this.taskExpiredTabItem.addEvent("click", this.changeToTaskExpired.bind(this));
        this.workExpiredTabItem.addEvent("click", this.changeToWorkExpired.bind(this));

        this.taskTabItem.click();
    },
    createTabItemNode: function(text, icon, content){
        var node = new Element("div", {"styles": this.css.monthTabItemNode}).inject(content);
        node.store("icon", icon);
        var iconNode = new Element("div", {"styles": this.css.monthTabItemIconNode}).inject(node);
        iconNode.setStyle("background-image", "url(/x_component_BAM/$Main/"+this.app.options.style+"/monthly/"+icon+".png)");
        var textNode = new Element("div", {"styles": this.css.monthTabItemTextNode}).inject(node);
        textNode.set("text", text);
        return node;
    },
    changeToTask: function(){
        this.setCurrentTabItem(0);
        this.loadContent("task");
        if (this.taskCompletedContent) this.taskCompletedContent.hide();
        if (this.workContent) this.workContent.hide();
        if (this.workCompletedContent) this.workCompletedContent.hide();
        if (this.taskExpiredContent) this.taskExpiredContent.hide();
        if (this.workExpiredContent) this.workExpiredContent.hide();
    },
    changeToTaskCompleted: function(){
        this.setCurrentTabItem(1);
        this.loadContent("taskCompleted");
        if (this.taskContent) this.taskContent.hide();
        if (this.workContent) this.workContent.hide();
        if (this.workCompletedContent) this.workCompletedContent.hide();
        if (this.taskExpiredContent) this.taskExpiredContent.hide();
        if (this.workExpiredContent) this.workExpiredContent.hide();
    },
    changeToWork: function(){
        this.setCurrentTabItem(2);
        this.loadContent("work");
        if (this.taskContent) this.taskContent.hide();
        if (this.taskCompletedContent) this.taskCompletedContent.hide();
        if (this.workCompletedContent) this.workCompletedContent.hide();
        if (this.taskExpiredContent) this.taskExpiredContent.hide();
        if (this.workExpiredContent) this.workExpiredContent.hide();
    },
    changeToWorkCompleted: function(){
        this.setCurrentTabItem(3);
        this.loadContent("workCompleted");
        if (this.taskContent) this.taskContent.hide();
        if (this.taskCompletedContent) this.taskCompletedContent.hide();
        if (this.workContent) this.workContent.hide();
        if (this.taskExpiredContent) this.taskExpiredContent.hide();
        if (this.workExpiredContent) this.workExpiredContent.hide();
    },
    changeToTaskExpired: function(){
        this.setCurrentTabItem(4);
        this.loadContent("taskExpired");
        if (this.taskContent) this.taskContent.hide();
        if (this.taskCompletedContent) this.taskCompletedContent.hide();
        if (this.workContent) this.workContent.hide();
        if (this.workCompletedContent) this.workCompletedContent.hide();
        if (this.workExpiredContent) this.workExpiredContent.hide();
    },
    changeToWorkExpired: function(){
        this.setCurrentTabItem(5);
        this.loadContent("workExpired");
        if (this.taskContent) this.taskContent.hide();
        if (this.taskCompletedContent) this.taskCompletedContent.hide();
        if (this.workContent) this.workContent.hide();
        if (this.workCompletedContent) this.workCompletedContent.hide();
        if (this.taskExpiredContent) this.taskExpiredContent.hide();
    },

    setCurrentTabItem: function(idx){
        var cells = this.table.getElements("td");
        cells.each(function(cell, i){
            if (i==idx){
                var currentDiv = cell.getFirst("div");
                var currentIcon = currentDiv.retrieve("icon");
                currentDiv.setStyles(this.css.monthTabItemNode_current);
                currentDiv.getFirst("div").setStyle("background-image", "url(/x_component_BAM/$Main/"+this.app.options.style+"/monthly/"+currentIcon+"_current.png)");
            }else{
                var div = cell.getFirst("div");
                var icon = div.retrieve("icon");
                div.setStyles(this.css.monthTabItemNode);
                div.getFirst("div").setStyle("background-image", "url(/x_component_BAM/$Main/"+this.app.options.style+"/monthly/"+icon+".png)");
            }
        }.bind(this));
    },
    loadContent: function(name){
        var className = "Monthly"+name.capitalize()+"Content";
        if (this[name+"Content"]){
            this[name+"Content"].show();
        }else{
            MWF.xDesktop.requireApp("BAM", "monthly.MonthlyContent", function(){
                this[name+"Content"] = new MWF.xApplication.BAM.monthly[className](this, this.contentAreaNode, {
                    "onLoaded": function(){
                        this.fireEvent("loaded");
                    }.bind(this)
                });
            }.bind(this));
        }
    },
    destroy: function(){
        //this.summaryChart.destroy();
        //this.rankChart.destroy();
        //this.dashboardChart.destroy();
        //this.taskChart.destroy();
        //this.taskCompletedChart.destroy();
        //this.workChart.destroy();
        //this.workCompletedChart.destroy();
        //if (this.taskContent) this.taskContent.destroy();
        //if (this.workContent) this.workContent.destroy();
        //if (this.workCompletedContent) this.workCompletedContent.destroy();
        //if (this.taskExpiredContent) this.taskExpiredContent.destroy();
        //if (this.workExpiredContent) this.workExpiredContent.destroy();

        this.container.empty();
        MWF.release(this);
    }


    //
    //
    //
    //
    //
    //
    //checkLoadDataCompleted: function(){
    //    if (this.overviewDataLoaded && this.categoryDataLoaded && this.organizationDataLoaded){
    //        this.fireEvent("loaded");
    //    }
    //},
    //loadSummary: function(){
    //    this.loadOverviewData(function(){
    //        this.loadOverview();
    //        this.overviewDataLoaded = true;
    //        this.checkLoadDataCompleted();
    //    }.bind(this));
    //    this.loadCategoryData(function(){
    //        this.loadTaskDashboard();
    //        this.loadTaskContent();
    //        this.loadTaskCompletedContent();
    //        this.loadWorkContent();
    //        this.loadWorkCompletedContent();
    //        this.categoryDataLoaded = true;
    //        this.checkLoadDataCompleted();
    //    }.bind(this));
    //    this.loadOrganizationData(function(){
    //        this.loadTaskRank()
    //        this.organizationDataLoaded = true;
    //        this.checkLoadDataCompleted();
    //    }.bind(this));
    //},
    //loadOverviewData: function(callback){
    //    this.actions.summary(function(json){
    //        this.summaryData = json.data;
    //        if (callback) callback();
    //    }.bind(this));
    //},
    //loadOverview: function(){
    ////    this.actions.summary(function(json){
    ////        this.summaryData = json.data;
    //        MWF.xDesktop.requireApp("BAM", "summary.Overview", function(){
    //            this.summaryChart = new MWF.xApplication.BAM.summary.Overview(this, this.overviewAreaNode, this.summaryData);
    //        }.bind(this));
    ////    }.bind(this));
    //},
    //loadTaskRank: function(){
    //    MWF.xDesktop.requireApp("BAM", "summary.TaskRank", function(){
    //        this.rankChart = new MWF.xApplication.BAM.summary.TaskRank(this, this.taskRankAreaNode);
    //    }.bind(this));
    //},
    //loadTaskDashboard: function(){
    //    MWF.xDesktop.requireApp("BAM", "summary.TaskDashboard", function(){
    //        this.dashboardChart = new MWF.xApplication.BAM.summary.TaskDashboard(this, this.taskDashboardAreaNode);
    //    }.bind(this));
    //},
    //
    //loadTaskContent: function(){
    //    //MWF.xDesktop.requireApp("BAM", "summary.TaskContent", function(){
    //    //    this.taskChart = new MWF.xApplication.BAM.summary.TaskContent(this, this.taskContentAreaNode);
    //    //}.bind(this));
    //    MWF.xDesktop.requireApp("BAM", "summary.TaskContent", null, false);
    //    this.taskChart = new MWF.xApplication.BAM.summary.TaskContent(this, this.taskContentAreaNode);
    //},
    //loadTaskCompletedContent: function(){
    //    //MWF.xDesktop.requireApp("BAM", "summary.TaskCompletedContent", function(){
    //    //    this.taskCompletedChart = new MWF.xApplication.BAM.summary.TaskCompletedContent(this, this.taskCompletedContentAreaNode, this.scalingData);
    //    //}.bind(this));
    //    MWF.xDesktop.requireApp("BAM", "summary.TaskCompletedContent". null, false);
    //    this.taskCompletedChart = new MWF.xApplication.BAM.summary.TaskCompletedContent(this, this.taskCompletedContentAreaNode, this.scalingData);
    //},
    //loadWorkContent: function(){
    //    //MWF.xDesktop.requireApp("BAM", "summary.WorkContent", function(){
    //    //    this.workChart = new MWF.xApplication.BAM.summary.WorkContent(this, this.workContentAreaNode, this.scalingData);
    //    //}.bind(this));
    //    MWF.xDesktop.requireApp("BAM", "summary.WorkContent", null, false);
    //    this.workChart = new MWF.xApplication.BAM.summary.WorkContent(this, this.workContentAreaNode, this.scalingData);
    //},
    //loadWorkCompletedContent: function(){
    //    //MWF.xDesktop.requireApp("BAM", "summary.WorkCompletedContent", function(){
    //    //    this.workCompletedChart = new MWF.xApplication.BAM.summary.WorkCompletedContent(this, this.workCompletedContentAreaNode, this.scalingData);
    //    //}.bind(this));
    //    MWF.xDesktop.requireApp("BAM", "summary.WorkCompletedContent", null,false);
    //    this.workCompletedChart = new MWF.xApplication.BAM.summary.WorkCompletedContent(this, this.workCompletedContentAreaNode, this.scalingData);
    //},
    //
    //loadRunningData: function(callback){
    //    if (!this.runningData){
    //        this.actions.loadRunning(function(json){
    //            this.runningData = json.data;
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //},
    //loadOrganizationData: function(callback){
    //    this.organizationData = {};
    //    this.actions.loadOrganization(function(json){
    //        this.organizationData = json.data;
    //        if (callback) callback();
    //    }.bind(this));
    //},
    //loadCategoryData: function(callback){
    //    this.categoryData = {
    //        "application": [],
    //        "process": [],
    //        "activity": []
    //    };
    //    this.actions.loadCategory(function(json){
    //        this.categoryData = json.data;
    //        if (callback) callback();
    //    }.bind(this));
    //}
});