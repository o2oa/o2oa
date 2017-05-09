MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xDesktop.requireApp("BAM", "summary.TaskContent", null, false);
MWF.xApplication.BAM.summary.WorkContent = new Class({
    Extends: MWF.xApplication.BAM.summary.TaskContent,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(summary, node, data, options){
        this.setOptions(options);
        this.summary = summary;
        this.app = this.summary.app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.container = $(node);
        this.category = "application";
        this.data = this.summary.categoryData;
        this.maxColumn = 10;
        this.barOptions = {"style": "work"};
        this.load();
    },
    load: function(){
        this.loadArea(this.lp.work);
        this.loadBarData(this.loadBar.bind(this));
    },
    loadCategory: function(){
        var id = new MWF.widget.UUID();
        var html = "<input name='"+id+"TaskRankCategory' type='radio' value='application' checked>"+this.lp.application+
            "<input name='"+id+"TaskRankCategory' type='radio' value='process'>"+this.lp.process
        this.categoryNode.set("html", html);
        var _self = this;
        this.categoryNode.getElements("input").addEvent("click", function(){
            _self.category = this.value;
            _self.reload();
        });
    },
    reloadMaxChart: function(){
        this.node.inject(this.maxNode);
        this.node.setStyles(this.css.taskContentNode_max);
        this.chartAreaNode.setStyles(this.css.contentChartAreaNode_max);
        this.actionNode.setStyles(this.css.taskRankActionNode_max);

        var tabSize = this.chartAreaNode.getSize();
        var h = tabSize.y-80;
        this.countNode.setStyle("height", ""+h+"px");
        this.expiredNode.setStyle("height", ""+h+"px");
        this.expiredCountNode.setStyle("height", ""+h+"px");
        if (this.timeoutRateNode) this.timeoutRateNode.setStyle("height", ""+h+"px");
        if (this.timelinessRateNode) this.timelinessRateNode.setStyle("height", ""+h+"px");

        this.maxColumn = Math.round(tabSize.x/40);
        this.barOptions = {"marginBottom": 100, "delay": 10, "style": "work_max"};
        this.reload();
    },
    loadBar: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.countPage.addEvent("show", function(){
                if (!this.countBar){
                    this.countBar = this.loadBarChart(this.countNode, this[this.category+"Data"].workCount.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.countBar.transition();
                }

            }.bind(this));

            this.expiredPage.addEvent("show", function(){
                if (!this.elapsedBar){
                    this.elapsedBar = this.loadBarChart(this.expiredNode, this[this.category+"Data"].workDuration.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.elapsedBar.transition();
                }
            }.bind(this));

            this.expiredCountPage.addEvent("show", function(){
                if (!this.elapsedCountBar){
                    this.elapsedCountBar = this.loadBarChart(this.expiredCountNode, this[this.category+"Data"].workElapsedCount.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.elapsedCountBar.transition();
                }
            }.bind(this));

            this.timeoutRatePage.addEvent("show", function(){
                if (!this.timeoutBar){
                    var options = (this.barOptions) ? Object.clone(this.barOptions) : {};
                    options.tickFormat = ".0%";
                    options.dataFormat = ".1%";
                    this.timeoutBar = this.loadBarChart(this.timeoutRateNode, this[this.category+"Data"].workTimeoutRate.slice(0,this.maxColumn),options);
                } else {
                    this.timeoutBar.transition();
                }
            }.bind(this));

            if (this.countPage.isShow){
                this.countPage.showIm();
            }else{
                this.countPage.showTabIm();
            }

        }.bind(this));
    },
    loadApplicationData: function(callback){
        if (!this.applicationData){
            this.applicationData = this.app.actions.getWorkContentData(this.summary.categoryData.application || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadProcessData: function(callback){
        if (!this.processData){
            this.processData = this.app.actions.getWorkContentData(this.summary.categoryData.process || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadActivityData: function(callback){
        if (!this.activityData){
            this.activityData = this.app.actions.getWorkContentData(this.summary.categoryData.activity || [], function(i){return i.processName+"-"+i.name});
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    }
});