MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xApplication.BAM.summary.TaskContent = new Class({
    Implements: [Options, Events],

    options: {
        "style": "default",
        "max": true
    },
    initialize: function(summary, node, options){
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
        //this.titleNode = new Element("div", {"styles": this.css.contentTitleNode, "text": this.lp.task}).inject(this.container);
        //this.chartAreaNode = new Element("div", {"styles": this.css.contentChartAreaNode}).inject(this.container);
        this.loadArea(this.lp.task);
        //this.loadBar();
        this.loadBarData(this.loadBar.bind(this));
    },
    reload: function(){
        if (this.countBar) this.countBar.destroy();
        if (this.elapsedBar) this.elapsedBar.destroy();
        if (this.elapsedCountBar) this.elapsedCountBar.destroy();
        if (this.timeoutRateBar) this.timeoutRateBar.destroy();
        if (this.timelinessRateBar) this.timelinessRateBar.destroy();

        this.countBar = null;
        this.elapsedBar = null;
        this.elapsedCountBar = null;
        this.timeoutRateBar = null;
        this.timelinessRateBar = null;

        if (this.countNode) this.countNode.empty();
        if (this.expiredNode) this.expiredNode.empty();
        if (this.expiredCountNode) this.expiredCountNode.empty();
        if (this.timeoutRateNode) this.timeoutRateNode.empty();
        if (this.timelinessRateNode) this.timelinessRateNode.empty();

        this.loadBarData(function(){
            this.loadBar();
        }.bind(this));
        //this.expiredPage.showTabIm();
    },
    loadArea: function(text){
        this.node = new Element("div", {"styles": this.css.taskContentNode}).inject(this.container);
        this.headNode = new Element("div", {"styles": this.css.taskRankHeadNode}).inject(this.node);
        this.categoryNode = new Element("div", {"styles": this.css.taskRankCategoryNode}).inject(this.headNode);
        this.actionNode = new Element("div", {"styles": this.css.taskRankActionNode}).inject(this.headNode);
        this.titleNode = new Element("div", {"styles": this.css.taskRankTitleNode, "text": text}).inject(this.headNode);

        this.chartAreaNode = new Element("div", {"styles": this.css.contentChartAreaNode}).inject(this.node);
        this.loadTab();
        this.loadCategory();

        if (this.options.max){
            this.actionNode.addEvent("click", function(){
                if (!this.maxNode){
                    this.maxSizeChart();
                }else{
                    this.returnSizeChart();
                }

            }.bind(this));
        }
    },
    returnSizeChart: function(){
        this.resizeReturnChart();
        var size = this.container.getSize();
        var position = this.container.getPosition(this.container.getOffsetParent());
        new Fx.Morph(this.maxNode, {"duration": 150}).start({
            "height": ""+size.y+"px",
            "width": ""+size.x+"px",
            "left": ""+position.x+"px",
            "top": ""+position.y+"px"
        }).chain(function(){
            this.maxNode.destroy();
            this.maxNode = null;

            this.reload();
            if (this.resizeMaxChartFun) this.app.removeEvent("resize", this.resizeMaxChartFun);
        }.bind(this));
    },
    resizeReturnChart: function(){
        this.node.inject(this.container);
        this.node.setStyles(this.css.taskContentNode);
        this.chartAreaNode.setStyles(this.css.contentChartAreaNode);
        this.actionNode.setStyles(this.css.taskRankActionNode);

        var tabSize = this.chartAreaNode.getSize();
        var h = tabSize.y-24;
        this.countNode.setStyle("height", ""+h+"px");
        this.expiredNode.setStyle("height", ""+h+"px");
        this.expiredCountNode.setStyle("height", ""+h+"px");
        if (this.timeoutRateNode) this.timeoutRateNode.setStyle("height", ""+h+"px");
        if (this.timelinessRateNode) this.timelinessRateNode.setStyle("height", ""+h+"px");

        this.maxColumn = 10;
        this.barOptions = {"marginBottom": 46, "style": "task"};
    },

    maxSizeChart: function(){
        this.createMaxNode();
        var maxSize = this.app.content.getSize();
        var x = maxSize.x-18;
        var y= maxSize.y-18;
        new Fx.Morph(this.maxNode, {"duration": 150}).start({
            "height": ""+y+"px",
            "width": ""+x+"px",
            "left": "5px",
            "top": "5px"
        }).chain(function(){
            this.reloadMaxChart();
            this.resizeMaxChartFun = this.resizeMaxChart.bind(this);
            this.app.addEvent("resize", this.resizeMaxChartFun);
        }.bind(this));
    },
    resizeMaxChart: function(){
        var maxSize = this.app.content.getSize();
        var x = maxSize.x-18;
        var y= maxSize.y-18;
        this.maxNode.setStyles({
            "height": ""+y+"px",
            "width": ""+x+"px",
            "left": "5px",
            "top": "5px"
        });
        this.reloadMaxChart();
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
        this.barOptions = {"marginBottom": 100, "delay": 10, "style": "task_max"};
        this.reload();
    },
    createMaxNode: function(){
        this.maxNode = new Element("div", {"styles": this.css.contentChartMaxAreaNode}).inject(this.container, "after");
        var size = this.container.getSize();
        this.maxNode.setStyles({
            "height": ""+size.y+"px",
            "width": ""+size.x+"px"
        });
        this.maxNode.position({
            "relativeTo": this.container,
            "position": "upperLeft",
            "edge": "upperLeft"
        });
    },

    loadTab: function(){
        this.tab = new MWF.widget.Tab(this.chartAreaNode, {"style": "BAM_content"});
        this.tab.load();

        this.countNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.expiredNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.expiredCountNode = new Element("div", {"styles": this.css.contentTabAreaNode});
        this.timeoutRateNode = new Element("div", {"styles": this.css.contentTabAreaNode});

        this.countPage = this.tab.addTab(this.countNode, this.lp.contentTypeCount);
        this.expiredPage = this.tab.addTab(this.expiredNode, this.lp.contentTypeExpired);
        this.expiredCountPage = this.tab.addTab(this.expiredCountNode, this.lp.taskContentTypeExpiredCount);
        this.timeoutRatePage = this.tab.addTab(this.timeoutRateNode, this.lp.timeout);
    },

    loadBar: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.countPage.addEvent("show", function(){
                if (!this.countBar){
                    this.countBar = this.loadBarChart(this.countNode, this[this.category+"Data"].taskCount.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.countBar.transition();
                }

            }.bind(this));

            this.expiredPage.addEvent("show", function(){
                if (!this.elapsedBar){
                    this.elapsedBar = this.loadBarChart(this.expiredNode, this[this.category+"Data"].taskDuration.slice(0,this.maxColumn),this.barOptions);
                } else {
                    this.elapsedBar.transition();
                }
            }.bind(this));

            this.expiredCountPage.addEvent("show", function(){
                if (!this.elapsedCountBar){
                    this.elapsedCountBar = this.loadBarChart(this.expiredCountNode, this[this.category+"Data"].taskElapsedCount.slice(0,this.maxColumn),this.barOptions);
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

            if (this.countPage.isShow){
                this.countPage.showIm();
            }else{
                this.countPage.showTabIm();
            }

        }.bind(this));
    },

    loadBarChart: function(node, data, options){
        //if (!options) options = {};
        //options.style = "task";
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

    loadBarData: function(callback){
        var method = "load-"+this.category+"-data";

        //this.data = this.app.actions.getRankData(this.app.organizationData[this.category]);

        this[method.camelCase()](function(){
            if (callback) callback();
        }.bind(this));
    },
    loadApplicationData: function(callback){
        if (!this.applicationData){
            this.applicationData = this.app.actions.getTaskContentData(this.summary.categoryData.application || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadProcessData: function(callback){
        if (!this.processData){
            this.processData = this.app.actions.getTaskContentData(this.summary.categoryData.process || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadActivityData: function(callback){
        if (!this.activityData){
            this.activityData = this.app.actions.getTaskContentData(this.summary.categoryData.activity || [], function(i){return i.processName+"-"+i.name});
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },

    destroy: function(){
        if (this.countBar) this.countBar.destroy();
        if (this.elapsedBar) this.elapsedBar.destroy();
        if (this.elapsedCountBar) this.elapsedCountBar.destroy();
        if (this.timeoutRateBar) this.timeoutRateBar.destroy();
        if (this.timelinessRateBar) this.timelinessRateBar.destroy();
        MWF.release(this);
    }
});