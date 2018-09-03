MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xApplication.BAM.Summary = new Class({
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
        this.categoryDataLoaded = false;
        this.organizationDataLoaded = false;
        this.overviewDataLoaded = false;
        this.load();
    },
    load: function(){
        this.loadSummaryLayout();
        this.loadSummary();

        //this.loadDecimal(function(){
        //    this.loadSummary();
        //}.bind(this));
    },
    loadSummaryLayout: function(){
        this.overviewAreaNode = new Element("div", {"styles": this.css.overviewAreaNode}).inject(this.container);
        this.taskAreaNode = new Element("div", {"styles": this.css.taskAreaNode}).inject(this.container);
        this.countAreaNode = new Element("div", {"styles": this.css.countAreaNode}).inject(this.container);

        this.taskDashboardAreaNode = new Element("div", {"styles": this.css.taskDashboardAreaNode}).inject(this.taskAreaNode);
        this.taskRankAreaNode = new Element("div", {"styles": this.css.taskRankAreaNode}).inject(this.taskAreaNode);

        this.taskContentAreaNode = new Element("div", {"styles": this.css.taskContentAreaNode}).inject(this.countAreaNode);
        this.taskCompletedContentAreaNode = new Element("div", {"styles": this.css.taskCompletedContentAreaNode}).inject(this.countAreaNode);
        this.workContentAreaNode = new Element("div", {"styles": this.css.workContentAreaNode}).inject(this.countAreaNode);
        this.workCompletedContentAreaNode = new Element("div", {"styles": this.css.workCompletedContentAreaNode}).inject(this.countAreaNode);
    },
    checkLoadDataCompleted: function(){
        if (this.overviewDataLoaded && this.categoryDataLoaded && this.organizationDataLoaded){
            this.fireEvent("loaded");
        }
    },
    loadSummary: function(){
        this.loadOverviewData(function(){
            this.loadOverview();
            this.overviewDataLoaded = true;
            this.checkLoadDataCompleted();
        }.bind(this));
        this.loadCategoryData(function(){
            this.loadTaskDashboard();
            this.loadTaskContent();
            this.loadTaskCompletedContent();
            this.loadWorkContent();
            this.loadWorkCompletedContent();
            this.categoryDataLoaded = true;
            this.checkLoadDataCompleted();
        }.bind(this));
        this.loadOrganizationData(function(){
            this.loadTaskRank();
            this.organizationDataLoaded = true;
            this.checkLoadDataCompleted();
        }.bind(this));
    },
    loadOverviewData: function(callback){
        this.actions.summary(function(json){
            this.summaryData = json.data;
            if (callback) callback();
        }.bind(this));
    },
    loadOverview: function(){
    //    this.actions.summary(function(json){
    //        this.summaryData = json.data;
            MWF.xDesktop.requireApp("BAM", "summary.Overview", function(){
                this.summaryChart = new MWF.xApplication.BAM.summary.Overview(this, this.overviewAreaNode, this.summaryData);
            }.bind(this));
    //    }.bind(this));
    },
    loadTaskRank: function(){
        MWF.xDesktop.requireApp("BAM", "summary.TaskRank", function(){
            this.rankChart = new MWF.xApplication.BAM.summary.TaskRank(this, this.taskRankAreaNode);
        }.bind(this));
    },
    loadTaskDashboard: function(){
        MWF.xDesktop.requireApp("BAM", "summary.TaskDashboard", function(){
            this.dashboardChart = new MWF.xApplication.BAM.summary.TaskDashboard(this, this.taskDashboardAreaNode);
        }.bind(this));
    },

    loadTaskContent: function(){
        MWF.xDesktop.requireApp("BAM", "summary.TaskContent", function(){
            this.taskChart = new MWF.xApplication.BAM.summary.TaskContent(this, this.taskContentAreaNode);
        }.bind(this));
    },
    loadTaskCompletedContent: function(){
        MWF.xDesktop.requireApp("BAM", "summary.TaskCompletedContent", function(){
            this.taskCompletedChart = new MWF.xApplication.BAM.summary.TaskCompletedContent(this, this.taskCompletedContentAreaNode, this.scalingData);
        }.bind(this));
    },
    loadWorkContent: function(){
        MWF.xDesktop.requireApp("BAM", "summary.WorkContent", function(){
            this.workChart = new MWF.xApplication.BAM.summary.WorkContent(this, this.workContentAreaNode, this.scalingData);
        }.bind(this));
    },
    loadWorkCompletedContent: function(){
        MWF.xDesktop.requireApp("BAM", "summary.WorkCompletedContent", function(){
            this.workCompletedChart = new MWF.xApplication.BAM.summary.WorkCompletedContent(this, this.workCompletedContentAreaNode, this.scalingData);
        }.bind(this));
    },

    loadRunningData: function(callback){
        if (!this.runningData){
            this.actions.loadRunning(function(json){
                this.runningData = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    loadOrganizationData: function(callback){
        this.organizationData = {};
        this.actions.loadOrganization(function(json){
            this.organizationData = json.data;
            if (callback) callback();
        }.bind(this));
    },
    loadCategoryData: function(callback){
        this.categoryData = {
            "application": [],
            "process": [],
            "activity": []
        };
        this.actions.loadCategory(function(json){
            this.categoryData = json.data;
            if (callback) callback();
        }.bind(this));
    },
    destroy: function(){
        if(this.summaryChart) this.summaryChart.destroy();
        if(this.rankChart) this.rankChart.destroy();
        if(this.dashboardChart) this.dashboardChart.destroy();
        if(this.taskChart) this.taskChart.destroy();
        if(this.taskCompletedChart) this.taskCompletedChart.destroy();
        if(this.workChart) this.workChart.destroy();
        if(this.workCompletedChart) this.workCompletedChart.destroy();

        this.container.empty();
        MWF.release(this);
    }
});