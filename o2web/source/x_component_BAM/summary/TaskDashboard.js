MWF.xApplication.BAM.summary = MWF.xApplication.BAM.summary || {};
MWF.xApplication.BAM.summary.TaskDashboard = new Class({
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(summary, node, options){
        this.setOptions(options);
        this.summary = summary;
        this.app = this.summary.app;
        this.css = this.app.css;
        this.container = $(node);
        //this.data = this.app.categoryData;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.taskDashboardNode}).inject(this.container);
        this.taskElapsed = new MWF.xApplication.BAM.summary.TaskDashboard.TaskElapsed(this);
        this.workElapsed = new MWF.xApplication.BAM.summary.TaskDashboard.WorkElapsed(this);
        this.taskElapsedCount = new MWF.xApplication.BAM.summary.TaskDashboard.TaskElapsedCount(this);
        this.workElapsedCount = new MWF.xApplication.BAM.summary.TaskDashboard.WorkElapsedCount(this);

    },
    destroy: function(){
        this.taskElapsed.destroy();
        this.workElapsed.destroy();
        this.taskElapsedCount.destroy();
        this.workElapsedCount.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.BAM.summary.TaskDashboard.TaskChart = new Class({
    initialize: function(dashboard){
        this.dashboard = dashboard;
        this.app = this.dashboard.app;
        this.css = this.app.css;
        this.container = this.dashboard.node;
        this.categoryData = this.dashboard.summary.categoryData;
        this.status = "dashboard"; //proportion  //distribution
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.taskChartNode}).inject(this.container);
        this.titleNode = new Element("div", {"styles": this.css.taskChartTitleNode}).inject(this.node);
        this.chartNode = new Element("div", {"styles": this.css.taskChartChartNode}).inject(this.node);
        this.actionNode = new Element("div", {"styles": this.css.taskChartActionNode}).inject(this.node);

        this.setTitle();
        this.createActions();

        this.draw();
    },
    draw: function(){
        MWF.require("MWF.widget.chart.Dashboard", function(){
            var totalElapsed = 0;
            var count = 0;
            if (this.categoryData.application && this.categoryData.application.length){
                this.categoryData.application.each(function(d){
                    count += d.taskCount;
                    totalElapsed += d.taskDuration;
                });
            }

            this.taskCount = count;

            var d = (Math.round((totalElapsed/count)/60)*100)/100;

            this.chart = new MWF.widget.chart.Dashboard(this.chartNode, d, {"domain": [0, 72]});
            this.chart.load();
        }.bind(this));
    },
    setTitle: function(){
        this.titleNode.set("text", this.app.lp.taskElapsedTitle);
    },
    createActions: function(){
        this.proportionAction = new Element("div", {"styles": this.css.taskChartProportionAction, "text": this.app.lp.proportion}).inject(this.actionNode);
        this.distributionAction = new Element("div", {"styles": this.css.taskChartDistributionAction, "text": this.app.lp.distribution}).inject(this.actionNode);

        this.proportionAction.addEvent("click", function(){
            this.proportion();
        }.bind(this));
        this.distributionAction.addEvent("click", function(){
            this.distribution();
        }.bind(this));
    },
    proportion: function(){
        switch (this.status){
            case "dashboard":
                this.status = "proportion";
                this.changeToProportion();
                break;
            case "proportion":
                this.status = "dashboard";
                this.changeToDashboard();
                break;
            case "distribution":
                this.status = "proportion";
                this.changeToProportion();
                break;
        }
    },
    distribution: function(){
        switch (this.status){
            case "dashboard":
                this.status = "distribution";
                this.changeToDistribution();
                break;
            case "proportion":
                this.status = "distribution";
                this.changeToDistribution();
                break;
            case "distribution":
                this.status = "dashboard";
                this.changeToDashboard();
                break;
        }
    },
    changeToDashboard: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction_back);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction);
        if (this.proportionChart) this.proportionChart.hide();
        if (this.distributionChart) this.distributionChart.hide();

        if (this.chart) this.chart.show();
    },
    changeToProportion: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction_current);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction);
        this.getProportionData(function(){
            if (this.chart) this.chart.hide();
            if (this.distributionChart) this.distributionChart.hide();

            if (this.proportionChart){
                this.proportionChart.show();
            }else{
                MWF.require("MWF.widget.chart.Pie", function(){
                    this.proportionChart = new MWF.widget.chart.Pie(this.chartNode, this.proportionData, {"dataFormat": ".1%"});
                    this.proportionChart.load();
                }.bind(this));
            }
        }.bind(this));
    },
    changeToDistribution: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction_current);
        this.getDistributionData(function(){
            if (this.chart) this.chart.hide();
            if (this.proportionChart) this.proportionChart.hide();

            if (this.distributionChart){
                this.distributionChart.show();
            }else{
                MWF.require("MWF.widget.chart.Pie", function(){
                    this.distributionChart = new MWF.widget.chart.Pie(this.chartNode, this.distributionData, {"dataFormat": ".1%"});
                    this.distributionChart.load();
                }.bind(this));
            }

            //if (this.chart) this.chart.destroy();
            //@todo
            //
            //
            //
            //
        }.bind(this));
    },
    destroy: function(){
        if (this.chart) this.chart.destroy();
        if (this.proportionChart) this.proportionChart.destroy();
        if (this.distributionChart) this.distributionChart.destroy();
        MWF.release(this);
    }

});

MWF.xApplication.BAM.summary.TaskDashboard.TaskElapsed = new Class({
    Extends: MWF.xApplication.BAM.summary.TaskDashboard.TaskChart,
    createActions: function(){
        this.proportionAction = new Element("div", {"styles": this.css.taskChartProportionAction_one, "text": this.app.lp.proportion}).inject(this.actionNode);
        this.proportionAction.addEvent("click", function(){
            this.proportion();
        }.bind(this));
    },

    getProportionData: function(callback){
        if (!this.proportionData){
            this.dashboard.summary.loadRunningData(function(){
                var obj = this.dashboard.summary.runningData.task;
                var pdata = [];
                var lp = this.dashboard.app.lp.taskElapsed;
                var count = obj.halfDay+obj.oneDay+obj.twoDay+obj.threeDay+obj.moreDay;
                pdata.push({"name": lp.halfDay, "value": obj.halfDay/count});
                pdata.push({"name": lp.oneDay, "value": obj.oneDay/count});
                pdata.push({"name": lp.twoDay, "value": obj.twoDay/count});
                pdata.push({"name": lp.threeDay, "value": obj.threeDay/count});
                pdata.push({"name": lp.moreDay, "value": obj.moreDay/count});
                this.proportionData = pdata;
                if (callback) callback();
            }.bind(this));
            //this.app.actions.getTaskElapsedPercent(function(json){
            //
            //}.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getDistributionData: function(callback){
        if (!this.distributionData){
            this.app.actions.getTaskElapsedPercent(function(json){
                this.distributionData = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    }

});

MWF.xApplication.BAM.summary.TaskDashboard.WorkElapsed = new Class({
    Extends: MWF.xApplication.BAM.summary.TaskDashboard.TaskChart,
    setTitle: function(){
        this.titleNode.set("text", this.app.lp.workElapsedTitle);
    },
    createActions: function(){
        this.proportionAction = new Element("div", {"styles": this.css.taskChartProportionAction_one, "text": this.app.lp.proportion}).inject(this.actionNode);
        this.proportionAction.addEvent("click", function(){
            this.proportion();
        }.bind(this));
    },
    draw: function(){
        MWF.require("MWF.widget.chart.Dashboard", function(){
            var totalElapsed = 0;
            var count = 0;
            if (this.categoryData.application && this.categoryData.application.length){
                this.categoryData.application.each(function(d){
                    count += d.workCount;
                    totalElapsed += d.workDuration;
                });
            }

            this.workCount = count;
            var d = (Math.round((totalElapsed/count)/60)*100)/100;

            this.chart = new MWF.widget.chart.Dashboard(this.chartNode, d, {"domain": [0, 720]});
            this.chart.load();
        }.bind(this));
    },

    getProportionData: function(callback){
        if (!this.proportionData){
            this.dashboard.summary.loadRunningData(function(){
                var obj = this.dashboard.summary.runningData.work;
                var pdata = [];
                var lp = this.dashboard.app.lp.workElapsed;
                var count = obj.threeDay+obj.oneWeek+obj.twoWeek+obj.oneMonth+obj.moreMonth;
                pdata.push({"name": lp.threeDay, "value": obj.threeDay/count});
                pdata.push({"name": lp.oneWeek, "value": obj.oneWeek/count});
                pdata.push({"name": lp.twoWeek, "value": obj.twoWeek/count});
                pdata.push({"name": lp.oneMonth, "value": obj.oneMonth/count});
                pdata.push({"name": lp.moreMonth, "value": obj.moreMonth/count});
                this.proportionData = pdata;
                if (callback) callback();
            }.bind(this));
            //this.app.actions.getTaskElapsedPercent(function(json){
            //
            //}.bind(this));
        }else{
            if (callback) callback();
        }
    }
});
MWF.xApplication.BAM.summary.TaskDashboard.TaskElapsedCount = new Class({
    Extends: MWF.xApplication.BAM.summary.TaskDashboard.TaskChart,
    setTitle: function(){
        this.titleNode.set("text", this.app.lp.taskElapsedCountTitle);
    },
    createActions: function(){
        this.proportionAction = new Element("div", {"styles": this.css.taskChartProportionAction, "text": this.app.lp.count}).inject(this.actionNode);
        this.distributionAction = new Element("div", {"styles": this.css.taskChartDistributionAction, "text": this.app.lp.distribution}).inject(this.actionNode);

        this.proportionAction.addEvent("click", function(){
            this.proportion();
        }.bind(this));
        this.distributionAction.addEvent("click", function(){
            this.distribution();
        }.bind(this));
    },

    draw: function(){
        MWF.require("MWF.widget.chart.Dashboard", function(){
            var totalCount = 0;
            var count = 0;
            if (this.categoryData.application && this.categoryData.application.length){
                this.categoryData.application.each(function(d){
                    count += d.taskExpiredCount;
                    totalCount += d.taskCount;
                });
            }

            this.taskCount = totalCount;
            var d = Math.round((count/totalCount)*100*100)/100;

            this.chart = new MWF.widget.chart.Dashboard(this.chartNode, d, {"domain": [0, 100], "text": d+"%"});
            this.chart.load();
        }.bind(this));
    },

    changeToProportion: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction_current);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction);
        //this.getProportionData(function(){
            if (this.chart) this.chart.hide();
            if (this.distributionChart) this.distributionChart.hide();

            if (this.proportionChart){
                this.proportionChart.show();
            }else{
                var totalCount = 0;
                var count = 0;
                if (this.categoryData.application && this.categoryData.application.length){
                    this.categoryData.application.each(function(d){
                        count += d.taskExpiredCount;
                        totalCount += d.taskCount;
                    });
                }
                //var max = totalCount*0.7;
                //MWF.require("MWF.widget.chart.Pie", function(){
                    this.proportionChart = new MWF.widget.chart.Dashboard(this.chartNode, count, {"domain": [0, totalCount]});
                    this.proportionChart.load();
                //}.bind(this));
            }
       // }.bind(this));
    },
    changeToDistribution: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction_current);
        //this.getDistributionData(function(){
        this.getDistributionData();
        if (this.chart) this.chart.hide();
        if (this.proportionChart) this.proportionChart.hide();

        if (this.distributionChart){
            this.distributionChart.show();
        }else{

            MWF.require("MWF.widget.chart.Pie", function(){
                this.distributionChart = new MWF.widget.chart.Pie(this.chartNode, this.distributionData);
                this.distributionChart.load();
            }.bind(this));
        }
        //}.bind(this));
    },
    getDistributionData: function(callback){
        if (!this.distributionData){
            var arr = (this.categoryData.application) ? Array.clone(this.categoryData.application) : [];
            arr.sort(function(a,b){
                return a.taskExpiredCount>b.taskExpiredCount;
            });
            var pieData = [];
            var otherCount = 0;
            var pieCount = 0;
            arr.each(function(app){
                if ((pieCount/this.taskCount>0.8) || pieData.length>5){
                    otherCount += app.taskExpiredCount;
                }else{
                    if (app.taskExpiredCount){
                        pieData.push({"name": app.name, "value": app.taskExpiredCount});
                        pieCount += app.taskExpiredCount;
                    }
                }
            }.bind(this));
            if (otherCount>0){
                pieData.push({"name": this.app.lp.other, "value": otherCount});
            }

            this.distributionData = pieData;
        }else{
            if (callback) callback();
        }
    }
});
MWF.xApplication.BAM.summary.TaskDashboard.WorkElapsedCount = new Class({
    Extends: MWF.xApplication.BAM.summary.TaskDashboard.TaskElapsedCount,
    setTitle: function(){
        this.titleNode.set("text", this.app.lp.workElapsedCountTitle);
    },
    draw: function(){
        MWF.require("MWF.widget.chart.Dashboard", function(){
            var totalCount = 0;
            var count = 0;
            if (this.categoryData.application && this.categoryData.application.length){
                this.categoryData.application.each(function(d){
                    count += d.workExpiredCount;
                    totalCount += d.workCount;
                });
            }

            this.workCount = totalCount;
            var d = Math.round((count/totalCount)*100*100)/100;
            this.chart = new MWF.widget.chart.Dashboard(this.chartNode, d, {"domain": [0, 100], "text": d+"%"});
            this.chart.load();
        }.bind(this));
    },
    changeToProportion: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction_current);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction);

        if (this.chart) this.chart.hide();
        if (this.distributionChart) this.distributionChart.hide();

        if (this.proportionChart){
            this.proportionChart.show();
        }else{
            var totalCount = 0;
            var count = 0;
            if (this.categoryData.application && this.categoryData.application.length){
                this.categoryData.application.each(function(d){
                    count += d.workExpiredCount;
                    totalCount += d.workCount;
                });
            }

            //var max = totalCount*0.7;
            //MWF.require("MWF.widget.chart.Pie", function(){
            this.proportionChart = new MWF.widget.chart.Dashboard(this.chartNode, count, {"domain": [0, totalCount]});
            this.proportionChart.load();
            //}.bind(this));
        }
    },

    changeToDistribution: function(){
        if (this.proportionAction) this.proportionAction.setStyles(this.css.taskChartProportionAction);
        if (this.distributionAction) this.distributionAction.setStyles(this.css.taskChartDistributionAction_current);
        //this.getDistributionData(function(){
        this.getDistributionData();
        if (this.chart) this.chart.hide();
        if (this.proportionChart) this.proportionChart.hide();

        if (this.distributionChart){
            this.distributionChart.show();
        }else{

            MWF.require("MWF.widget.chart.Pie", function(){
                this.distributionChart = new MWF.widget.chart.Pie(this.chartNode, this.distributionData);
                this.distributionChart.load();
            }.bind(this));
        }
        //}.bind(this));
    },
    getDistributionData: function(callback){
        if (!this.distributionData){
            var arr = (this.categoryData.application) ? Array.clone(this.categoryData.application) : [];
            arr.sort(function(a,b){
                return a.workExpiredCount>b.workExpiredCount;
            });
            var pieData = [];
            var otherCount = 0;
            var pieCount = 0;
            arr.each(function(app){
                if ((pieCount/this.workCount>0.8) || pieData.length>5){
                    otherCount += app.workExpiredCount;
                }else{
                    if (app.workExpiredCount){
                        pieData.push({"name": app.name, "value": app.workExpiredCount});
                        pieCount += app.workExpiredCount;
                    }
                }
            }.bind(this));
            if (otherCount>0){
                pieData.push({"name": this.app.lp.other, "value": otherCount});
            }

            this.distributionData = pieData;
        }else{
            if (callback) callback();
        }
    }
});