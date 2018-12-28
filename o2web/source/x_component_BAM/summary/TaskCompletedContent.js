MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xDesktop.requireApp("BAM", "summary.TaskContent", null, false);
MWF.xApplication.BAM.summary.TaskCompletedContent = new Class({
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
        this.barOptions = {"style": "task"};
        this.load();
    },
    load: function(){
        this.loadArea(this.lp.taskCompleted);
        this.loadBarData(this.loadBar.bind(this));
    },
    loadTab: function(){
        this.tab = new MWF.widget.Tab(this.chartAreaNode, {"style": "BAM_content"});
        this.tab.load();

        this.countNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.expiredNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.expiredCountNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.timeoutRateNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.timelinessRateNode = new Element("div", {"styles": this.css.contentTabAreaNode});

        this.countPage = this.tab.addTab(this.countNode, this.lp.contentTypeCount);
        this.expiredPage = this.tab.addTab(this.expiredNode, this.lp.contentTypeDuration);
        this.expiredCountPage = this.tab.addTab(this.expiredCountNode, this.lp.contentTypeExpiredCount);
        this.timeoutRatePage = this.tab.addTab(this.timeoutRateNode, this.lp.timeout);
        this.timelinessRatePage = this.tab.addTab(this.timelinessRateNode, this.lp.timeliness);
    },

    loadBar: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.countPage.addEvent("show", function(){
                if (!this.countBar){
                    this.countBar = this.loadBarChart(this.countNode, this[this.category+"Data"].taskCompletedCount.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.countBar.transition();
                }

            }.bind(this));

            this.expiredPage.addEvent("show", function(){
                if (!this.elapsedBar){
                    this.elapsedBar = this.loadBarChart(this.expiredNode, this[this.category+"Data"].taskCompletedDuration.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.elapsedBar.transition();
                }
            }.bind(this));

            this.expiredCountPage.addEvent("show", function(){
                if (!this.elapsedCountBar){
                    this.elapsedCountBar = this.loadBarChart(this.expiredCountNode, this[this.category+"Data"].taskCompletedElapsedCount.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.elapsedCountBar.transition();
                }
            }.bind(this));

            this.timeoutRatePage.addEvent("show", function(){
                if (!this.timeoutRateBar){
                    var options = (this.barOptions) ? Object.clone(this.barOptions) : {};
                    options.tickFormat = ".0%";
                    options.dataFormat = ".1%";
                    this.timeoutRateBar = this.loadBarChart(this.timeoutRateNode, this[this.category+"Data"].taskTimeoutRate.slice(0,this.maxColumn),options);
                } else {
                    this.timeoutRateBar.transition();
                }
            }.bind(this));

            this.timelinessRatePage.addEvent("show", function(){
                if (!this.timelinessRateBar){
                    var options = (this.barOptions) ? Object.clone(this.barOptions) : {};
                    options.tickFormat = ".0%";
                    options.dataFormat = ".1%";
                    this.timelinessRateBar = this.loadBarChart(this.timelinessRateNode, this[this.category+"Data"].taskTimelinessRate.slice(0,this.maxColumn),options);
                } else {
                    this.timelinessRateBar.transition();
                }
            }.bind(this));

            if (this.countPage.isShow){
                this.countPage.showIm();
            }else{
                this.countPage.showTabIm();
            }

        }.bind(this));
    },

    loadBarChart: function(node, data, options){
        var bar = new MWF.widget.chart.Bar(node, data, "name", options);
        bar.addBar("value");
        bar.addEvents({
            "mouseover": function(rects, texts, d, i){
                texts.filter(function(data, idx){return (idx==i);}).attr("display", "block");
                var rect = rects.filter(function(data, idx){return (idx==i);});
                var color = rect.attr("fill");
                rect.node().store("color", color);
                rect.attr("fill", "brown");
            }.bind(this),
            "mouseout": function(rects, texts, d, i){
                texts.filter(function(data, idx){return (idx==i);}).attr("display", "none");
                var rect = rects.filter(function(data, idx){return (idx==i);});
                var color = rect.node().retrieve("color");
                rect.attr("fill", color);
            }.bind(this)
        });
        bar.load();
        return bar;
    },

    loadCategory: function(){
        var id = new MWF.widget.UUID();
        var html = "<input name='"+id+"TaskRankCategory' type='radio' value='application' checked>"+this.lp.application+
            "<input name='"+id+"TaskRankCategory' type='radio' value='process'>"+this.lp.process+
            "<input name='"+id+"TaskRankCategory' type='radio' value='activity'>"+this.lp.activity;
        this.categoryNode.set("html", html);
        var _self = this;
        this.categoryNode.getElements("input").addEvent("click", function(){
            _self.category = this.value;
            _self.reload();
        });
    },
    loadApplicationData: function(callback){
        if (!this.applicationData){
            this.applicationData = this.app.actions.getTaskCompletedContentData(this.summary.categoryData.application || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadProcessData: function(callback){
        if (!this.processData){
            this.processData = this.app.actions.getTaskCompletedContentData(this.summary.categoryData.process || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadActivityData: function(callback){
        if (!this.activityData){
            this.activityData = this.app.actions.getTaskCompletedContentData(this.summary.categoryData.activity || [], function(i){return i.processName+"-"+i.name});
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    }
});