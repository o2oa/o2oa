MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xApplication.BAM.summary.TaskRank = new Class({
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(summary, node, options){
        this.setOptions(options);
        this.summary = summary;
        this.app = this.summary.app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.container = $(node);
        this.category = "unit";
        this.data = null;
        this.maxColumn = 10;
        this.barOptions = {"style": "default", "marginBottom": 5};
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.taskRankContentAreaNode}).inject(this.container);
        this.loadTaskRankArea();
        //this.loadBar();
        this.loadBarData(this.loadBar.bind(this));
    },
    reload: function(){
        if (this.durationBar) this.durationBar.destroy();
        if (this.elapsedCountBar) this.elapsedCountBar.destroy();
        if (this.completedCountBar) this.completedCountBar.destroy();
        if (this.completedTimelinessBar) this.completedTimelinessBar.destroy();
        if (this.timeoutRateBar) this.timeoutRateBar.destroy();

        this.durationBar = null;
        this.elapsedCountBar = null;
        this.completedCountBar = null;
        this.completedTimelinessBar = null;
        this.timeoutRateBar = null;

        this.durationNode.empty();
        this.expiredCountNode.empty();
        this.completedCountNode.empty();
        this.completedTimelinessNode.empty();
        this.timeoutRateNode.empty();

        this.loadBarData(this.loadBar.bind(this));
        //this.expiredPage.showTabIm();
    },
    loadBar: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.durationPage.addEvent("show", function(){
                if (!this.durationBar){
                    this.durationBar = this.loadBarChart(this.durationNode, this[this.category+"RankData"].taskDuration.slice(0,this.maxColumn));
                } else {
                    this.durationBar.transition();
                }

            }.bind(this));

            this.expiredCountPage.addEvent("show", function(){
                if (!this.elapsedCountBar){
                    this.elapsedCountBar = this.loadBarChart(this.expiredCountNode, this[this.category+"RankData"].taskElapsedCount.slice(0,this.maxColumn));
                } else {
                    this.elapsedCountBar.transition();
                }

            }.bind(this));

            this.completedCountPage.addEvent("show", function(){
                if (!this.completedCountBar){
                    this.completedCountBar = this.loadBarChart(this.completedCountNode, this[this.category+"RankData"].taskCompletedCount.slice(0,this.maxColumn));
                } else {
                    this.completedCountBar.transition();
                }
            }.bind(this));

            this.completedTimelinessPage.addEvent("show", function(){
                if (!this.completedTimelinessBar){
                    var options = (this.barOptions) ? Object.clone(this.barOptions) : {};
                    options.tickFormat = ".0%";
                    options.dataFormat = ".1%";
                    this.completedTimelinessBar = this.loadBarChart(this.completedTimelinessNode, this[this.category+"RankData"].taskCompletedTimeliness.slice(0,this.maxColumn), options);
                } else {
                    this.completedTimelinessBar.transition();
                }
            }.bind(this));

            this.timeoutRatePage.addEvent("show", function(){
                if (!this.timeoutRateBar){
                    var options = (this.barOptions) ? Object.clone(this.barOptions) : {};
                    options.tickFormat = ".0%";
                    options.dataFormat = ".1%";
                    this.timeoutRateBar = this.loadBarChart(this.timeoutRateNode, this[this.category+"RankData"].taskTimeoutRate.slice(0,this.maxColumn), options);
                } else {
                    this.timeoutRateBar.transition();
                }
            }.bind(this));

            if (this.durationPage.isShow){
                this.durationPage.showIm();
            }else{
                this.durationPage.showTabIm();
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

    loadBarData: function(callback){
        var method = "load-rank-"+this.category+"-data";

        //this.data = this.app.actions.getRankData(this.app.organizationData[this.category]);

        this[method.camelCase()](function(){
            if (callback) callback();
        }.bind(this));
    },

    loadTab: function(){
        this.tab = new MWF.widget.Tab(this.chartAreaNode, {"style": "BAM_rank"});
        this.tab.load();

        this.durationNode = new Element("div", {"styles": this.css.taskRankTabAreaNode});
        this.expiredCountNode = new Element("div", {"styles": this.css.taskRankTabAreaNode});
        this.completedCountNode = new Element("div", {"styles": this.css.taskRankTabAreaNode});
        this.completedTimelinessNode = new Element("div", {"styles": this.css.taskRankTabAreaNode});
        this.timeoutRateNode = new Element("div", {"styles": this.css.taskRankTabAreaNode});

        this.durationPage = this.tab.addTab(this.durationNode, this.lp.taskRankTypeExpired);
        this.expiredCountPage = this.tab.addTab(this.expiredCountNode, this.lp.taskRankTypeExpiredCount);
        this.completedCountPage = this.tab.addTab(this.completedCountNode, this.lp.taskRankTypeCompletedCount);
        this.completedTimelinessPage = this.tab.addTab(this.completedTimelinessNode, this.lp.timeliness);
        this.timeoutRatePage = this.tab.addTab(this.timeoutRateNode, this.lp.timeout);
    },
    loadCategory: function(){
        var id = new MWF.widget.UUID();
        // var html = "<input name='"+id+"TaskRankCategory' type='radio' value='company'>"+this.lp.company+
        //     "<input name='"+id+"TaskRankCategory' type='radio' value='department' checked>"+this.lp.department+
        //     "<input name='"+id+"TaskRankCategory' type='radio' value='person'>"+this.lp.person;
        var html = "<input name='"+id+"TaskRankCategory' type='radio' value='unit' checked>"+this.lp.unit+
            "<input name='"+id+"TaskRankCategory' type='radio' value='person'>"+this.lp.person;
        this.categoryNode.set("html", html);
        var _self = this;
        this.categoryNode.getElements("input").addEvent("click", function(){
            _self.category = this.value;
            _self.reload();
        });
    },
    loadTaskRankArea: function(){
        this.headNode = new Element("div", {"styles": this.css.taskRankHeadNode}).inject(this.node);
        this.categoryNode = new Element("div", {"styles": this.css.taskRankCategoryNode}).inject(this.headNode);
        this.actionNode = new Element("div", {"styles": this.css.taskRankActionNode}).inject(this.headNode);
        this.titleNode = new Element("div", {"styles": this.css.taskRankTitleNode, "text": this.lp.top10}).inject(this.headNode);

        this.chartAreaNode = new Element("div", {"styles": this.css.taskRankChartAreaNode}).inject(this.node);
        this.loadTab();

        this.loadCategory();

        this.actionNode.addEvent("click", function(){
            if (!this.maxNode){
                this.maxSizeChart();
            }else{
                this.returnSizeChart();
            }

        }.bind(this));
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
        this.durationNode.setStyle("height", ""+h+"px");
        this.expiredCountNode.setStyle("height", ""+h+"px");
        this.completedCountNode.setStyle("height", ""+h+"px");
        if (this.completedTimelinessNode) this.completedTimelinessNode.setStyle("height", ""+h+"px");
        if (this.timeoutRateNode) this.timeoutRateNode.setStyle("height", ""+h+"px");

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
        this.durationNode.setStyle("height", ""+h+"px");
        this.expiredCountNode.setStyle("height", ""+h+"px");
        this.completedCountNode.setStyle("height", ""+h+"px");
        if (this.completedTimelinessNode) this.completedTimelinessNode.setStyle("height", ""+h+"px");
        if (this.timeoutRateNode) this.timeoutRateNode.setStyle("height", ""+h+"px");

        this.maxColumn = Math.round(tabSize.x/40);
        this.barOptions = {"marginBottom": 100, "delay": 10, "style": "default_max"};
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

    loadRankUnitData: function(callback){
        if (!this.unitRankData){
            this.unitRankData = this.app.actions.getRankData(this.summary.organizationData.unit || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },

    // loadRankCompanyData: function(callback){
    //     if (!this.companyRankData){
    //         this.companyRankData = this.app.actions.getRankData(this.summary.organizationData.company || []);
    //         if (callback) callback();
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    // loadRankDepartmentData: function(callback){
    //     if (!this.departmentRankData){
    //         this.departmentRankData = this.app.actions.getRankData(this.summary.organizationData.department || []);
    //         if (callback) callback();
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    loadRankPersonData: function(callback){
        if (!this.personRankData){
            this.personRankData = this.app.actions.getRankData(this.summary.organizationData.person || []);
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    destroy: function(){
        if (this.durationBar) this.durationBar.destroy();
        if (this.elapsedCountBar) this.elapsedCountBar.destroy();
        if (this.completedCountBar) this.completedCountBar.destroy();
        if (this.completedTimelinessBar) this.completedTimelinessBar.destroy();
        if (this.timeoutRateBar) this.timeoutRateBar.destroy();
        MWF.release(this);
    }
});