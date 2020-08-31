MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("query.Query", "lp.zh-cn", null, false);
MWF.xApplication.query.Query.Statistician = MWF.QStatistician = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "resizeNode": true
    },
    initialize: function(app, container, json, options){
        this.setOptions(options);

        this.path = "../x_component_query_Query/$Statistician/";
        this.cssPath = "../x_component_query_Query/$Statistician/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.app = app;
        this.container = $(container);
        this.json = json;

        this.statJson = null;
        this.gridJson = null;

        this.load();
    },
    load: function(){
        this.loadLayout();
        this.loadStatData();
    },
    loadLayout: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //if (this.json.isChart) this.chartAreaNode = new Element("div", {"styles": this.css.statChartAreaNode}).inject(this.node);
        //if (this.json.isTable) this.tableAreaNode = new Element("div", {"styles": this.css.statTableAreaNode}).inject(this.node);
    },
    createLoadding: function(){
        this.node.empty();
        this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.node);
        new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
        var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
        new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
        var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
        loadingTextNode.set("text", "loading...");
    },
    loadStatData: function(node){
        this.createLoadding();
        MWF.Actions.get("x_query_assemble_surface").loadStat(this.json.statName, this.json.application, null, function(json){
            if (this.loadingAreaNode){
                this.loadingAreaNode.destroy();
                this.loadingAreaNode = null;
            }
            if (json.data.calculate.isGroup){
                this.stat = new MWF.xApplication.query.Query.Statistician.GroupStat(this, json.data);
            }else{
                this.stat = new MWF.xApplication.query.Query.Statistician.Stat(this, json.data);
            }
            this.fireEvent("loaded");
        }.bind(this));
    }
});

MWF.xApplication.query.Query.Statistician.Stat = new Class({
    initialize: function(statistician, data){
        //this.explorer = explorer;
        this.statistician = statistician;
        this.json = this.statistician.json;
        this.data = data;
        this.statGridData = Array.clone(this.data.calculateGrid);
        this.css = this.statistician.css;
        this.lp = this.statistician.lp;
        this.node = this.statistician.node;
        this.load();
    },
    load: function(){
        this.charts = this.data.calculate.chart;
        if( !this.charts ){
            this.charts = ["bar", "pie", "line"];
        }else if( typeOf( this.charts ) === "string" ){
            this.charts = [this.charts];
        }

        if (this.json.isChart) this.chartAreaNode = new Element("div", {"styles": this.css.statChartAreaNode}).inject(this.node);
        if (this.json.isTable) this.tableAreaNode = new Element("div#tableAreaNode", {"styles": this.css.statTableAreaNode}).inject(this.node);
        this.loadData();
        if (this.json.isTable) this.createTable();
        if (this.json.isChart) this.createChart();
    },
    loadData: function(){
        var entries = {};
        this.data.calculate.calculateList.each(function(entry){entries[entry.column] = entry;}.bind(this));
    },
    createChart: function(){
        this.chartToolbarNode = new Element("div", {"styles": this.css.statChartToolbarNode}).inject(this.chartAreaNode);
        if( this.charts.length > 0 ){
            this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        }else{
            this.chartAreaNode.setStyles( this.css.statChartAreaNode_noChart )
        }
        if( this.charts.length > 0 || this.data.calculate.isGroup ){
            this.loadChartToolbar();
        }else{
            this.chartAreaNode.hide();
        }
        //this.loadChart();
        if (this.statistician.app){
            this.resizeChartFun = this.reloadChart.bind(this);
            this.statistician.app.addEvent("resizeCompleted", this.resizeChartFun);
            this.statistician.app.addEvent("postMaxSize", this.resizeChartFun);
            this.statistician.app.addEvent("postRestoreSize", this.resizeChartFun);
        }
    },
    loadChartToolbar: function(){
        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbar = new MWF.widget.Toolbar(this.chartToolbarNode, {"style": "simple"}, this);
            var charts = this.charts;
            if (charts.indexOf("bar")!==-1){
                var actionNode = new Element("div", {
                    "MWFnodeid": "bar",
                    "MWFnodetype": "MWFToolBarOnOffButton",
                    "MWFButtonImage": this.statistician.path+""+this.statistician.options.style+"/icon/barChart.png",
                    "title": this.lp.chart.bar,
                    "MWFButtonAction": "changeChartBar",
                    "MWFButtonText": this.lp.chart.bar
                }).inject(this.chartToolbarNode);
            }
            if (charts.indexOf("pie")!==-1){
                var actionNode = new Element("div", {
                    "MWFnodeid": "pie",
                    "MWFnodetype": "MWFToolBarOnOffButton",
                    "MWFButtonImage": this.statistician.path+""+this.statistician.options.style+"/icon/pieChart.png",
                    "title": this.lp.chart.pie,
                    "MWFButtonAction": "changeChartPie",
                    "MWFButtonText": this.lp.chart.pie
                }).inject(this.chartToolbarNode);
            }
            if (charts.indexOf("line")!==-1){
                var actionNode = new Element("div", {
                    "MWFnodeid": "line",
                    "MWFnodetype": "MWFToolBarOnOffButton",
                    "MWFButtonImage": this.statistician.path+""+this.statistician.options.style+"/icon/lineChart.png",
                    "title": this.lp.chart.line,
                    "MWFButtonAction": "changeChartLine",
                    "MWFButtonText": this.lp.chart.line
                }).inject(this.chartToolbarNode);
            }
            if (this.data.calculate.isGroup ){
                var actionNode = new Element("div", {
                    "MWFnodeid": "rowToColumn",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.statistician.path+""+this.statistician.options.style+"/icon/rowToColumn.png",
                    "title": this.lp.chart.rowToColumn,
                    "MWFButtonAction": "loadRowToColumn",
                    "MWFButtonText": this.lp.chart.rowToColumn
                }).inject(this.chartToolbarNode);
            }else if( charts.length === 0 ){
                this.chartToolbarNode.hide();
            }

            this.toolbar.load();

            var defaultChart = "";
            if( charts.indexOf( "bar" ) !== -1 ){
                defaultChart = "bar"
            }else{
                defaultChart = charts[0];
            }

            if( defaultChart === "bar" ){
                this.changeChartBar("on", this.toolbar.items[ defaultChart ]);
            }else if( defaultChart === "pie" ){
                this.changeChartPie("on", this.toolbar.items[ defaultChart ]);
            }else if( defaultChart === "line" ){
                this.changeChartLine("on", this.toolbar.items[ defaultChart ]);
            }
            //this.reloadChart();
        }.bind(this));
    },
    changeChartBar: function(status, btn){
        if(this.toolbar.items["pie"])this.toolbar.items["pie"].off();
        if(this.toolbar.items["line"])this.toolbar.items["line"].off();
        btn.on();
        this.currentChart = "bar";
        this.reloadChart();
    },
    changeChartPie: function(status, btn){
        if(this.toolbar.items["bar"])this.toolbar.items["bar"].off();
        if(this.toolbar.items["line"])this.toolbar.items["line"].off();
        btn.on();
        this.currentChart = "pie";
        this.reloadChart();
    },
    changeChartLine: function(status, btn){
        if(this.toolbar.items["pie"])this.toolbar.items["pie"].off();
        if(this.toolbar.items["bar"])this.toolbar.items["bar"].off();
        btn.on();
        this.currentChart = "line";
        this.reloadChart();
    },
    toFloat: function(value){
        if (value.substr(0,1)==="￥") value = value.substr(1, value.length);
        value = value.replace(",", "");
        value = value.replace(/\s/g, "");
        return value.toFloat()
    },
    loadChartBar: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.bar = new MWF.widget.chart.Bar(this.chartNode, this.statGridData, "displayName", {"delay": 0, "style": "monthly"});
            //this.bar.addBar("value");
            this.bar.addBar(function(d){
                var value = d.value;
                if (value.substr(0,1)==="￥") value = value.substr(1, value.length);
                value = value.replace(",", "");
                value = value.replace(/\s/g, "");
                return value.toFloat()
            }, function(d){
                return d.value;
            });
            //bar.addBar("value");
            this.bar.addEvents({
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
            this.bar.load();
        }.bind(this));
    },
    loadChartPie: function(){
        MWF.require("MWF.widget.chart.Pie", function(){
            var data = [];
            var total = 0;
            // this.data.calculateGrid.each(function(d){
            //     total += this.toFloat(d.value);
            // }.bind(this));
            this.data.calculateGrid.each(function(d){
                var v = this.toFloat(d.value);
                // var percent = ((v/total)*10000).toInt()/100;
                // percent = ""+ percent +"%";
                data.push({"name": d.displayName, "value": v});
            }.bind(this));

            this.bar = new MWF.widget.chart.Pie(this.chartNode, data, {"textType": "percent"});
            this.bar.load();
        }.bind(this));
    },
    loadChartLine: function(){
        MWF.require("MWF.widget.chart.Line", function(){
            this.bar = new MWF.widget.chart.Line(this.chartNode, this.statGridData, "displayName", {"delay": 0, "style": "monthly"});
            //this.bar.addBar("value");
            this.bar.addBar(function(d){
                var value = d.value;
                if (value.substr(0,1)==="￥") value = value.substr(1, value.length);
                value = value.replace(",", "");
                value = value.replace(/\s/g, "");
                return value.toFloat()
            }, function(d){
                return d.value;
            });

            this.bar.load();
        }.bind(this));
    },

    resizeChart: function(){
        if (this.bar) this.bar.destroy();
        this.bar = null;
        if (this.chartNode) this.chartNode.empty();
        this.loadChartBar();
    },
    // loadChart: function(){
    //
    // },
    createTable: function(){
        this.createTableHead();
        this.createTableData();
    },
    createTableHead: function(){
        this.table = new Element("table", {
            "styles": this.css.statTableNode,
            "width": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.tableAreaNode);
        this.headTr = new Element("tr").inject(this.table);

        this.data.calculate.calculateList.each(function(title){
            var th = new Element("th", {
                "styles": this.css.statHeadTh,
                "text": title.displayName
            }).inject(this.headTr);
        }.bind(this));
    },

    createTableData: function(){
        if (this.statGridData.length){
            var tr = new Element("tr").inject(this.table);
            this.statGridData.each(function(d){
                var td = new Element("td", {"styles": this.css.statContentTdNode}).inject(tr);
                td.set("text", d.value);
            }.bind(this));
        }
    },
    destroy: function(){
        if (this.bar) this.bar.destroy();
        if (this.statistician.app){
            if (this.resizeChartFun){
                this.resizeChartFun.app.removeEvent("resizeCompleted", this.resizeChartFun);
                this.resizeChartFun.app.removeEvent("postMaxSize", this.resizeChartFun);
                this.resizeChartFun.app.removeEvent("postRestoreSize", this.resizeChartFun);
            }
        }
        MWF.release(this);
    }
});
MWF.xApplication.query.Query.Statistician.GroupStat = new Class({
    Extends: MWF.xApplication.query.Query.Statistician.Stat,

    loadData: function(){
        var entries = {};
        var groupColumn = null;
        this.data.calculate.calculateList.each(function(entry){
            entries[entry.id] = entry;
            // if (entry.column === this.data.group.column){
            //     groupColumn = entry;
            // }
        }.bind(this));

        this.statGridData.each(function(dd){
            // if (groupColumn){
            //     dd.group = (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": dd.group, "data": this.data}) : dd.group;
            // }
            dd.list.each(function(c){
                c.value = (entries[c.column].code) ? MWF.Macro.exec(entries[c.column].code, {"value": c.value, "data": this.data}) : c.value
            }.bind(this));
        }.bind(this));

    },

    createChart: function(){
        this.chartToolbarNode = new Element("div", {"styles": this.css.statChartToolbarNode}).inject(this.chartAreaNode);

        if( this.charts.length > 0 ){
            if (this.json.isLegend) this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartAreaNode);
            this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        }else{
            this.chartAreaNode.setStyles( this.css.statChartAreaNode_noChart )
        }


        this.loadChartToolbar();
        //this.loadChartBar();
        if (this.statistician.app){
            this.resizeChartFun = this.reloadChart.bind(this);
            this.statistician.app.addEvent("resizeCompleted", this.resizeChartFun);
            this.statistician.app.addEvent("postMaxSize", this.resizeChartFun);
            this.statistician.app.addEvent("postRestoreSize", this.resizeChartFun);
        }
    },
    resizeChart: function(){
        if (this.bar) this.bar.destroy();
        this.bar = null;
        if (this.json.isLegend) this.chartFlagNode.empty();
        this.chartNode.empty();
        this.loadChartBar();
    },
    toFloat: function(value){
        if (value.substr(0,1)==="￥") value = value.substr(1, value.length);
        value = value.replace(",", "");
        value = value.replace(/\s/g, "");
        return value.toFloat()
    },
    loadChartBar: function(){
        if (this.json.isLegend) if (!this.chartFlagNode) this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartNode, "before");

        if (!this.selectedData) this.selectedData = this.statGridData;
        if (!this.selectedData.length) this.selectedData = this.statGridData;

        MWF.require("MWF.widget.chart.Bar", function(){
            //this.selectedData.each()

            this.flag = [];
            this.bar = new MWF.widget.chart.Bar(this.chartNode, this.selectedData, "group", {"delay": 0, "style": "monthly"});

            if (this.selectedData.length){
                this.selectedData[0].list.each(function(d, i){
                    this.flag.push({"name":d.displayName, "color": this.bar.colors[i]});
                    this.bar.addBar(function(v){
                        return this.toFloat(v.list[i].value);
                    }.bind(this), function(v){
                        return v.list[i].value;
                    });
                }.bind(this));
            }

            this.bar.addEvents({
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
            this.bar.load();
            if (this.json.isLegend) this.loadFlags();
        }.bind(this));
    },
    rowToColumn: function(data){
        var o = [];
        var idxs = {};
        data.each(function(d){
            var group = d.group;
            var column = d.column;
            d.list.each(function(l){
                if (l.column){
                    var idx = (l.column) ? idxs[l.column] : idxs[l.displayName];
                    if (!idx && idx!==0){
                        o.push({ "column": l.column, "group": l.displayName, "list": [] });
                        idx = o.length-1;
                        idxs[l.column] = idx;
                    }
                    o[idx].list.push({
                        "displayName": group,
                        "name": group,
                        "value": l.value
                    });
                }else{
                    var idx = (l.column) ? idxs[l.column] : idxs[l.displayName];
                    if (!idx && idx!==0){
                        o.push({ "group": l.displayName, "list": [] });
                        idx = o.length-1;
                        idxs[l.displayName] = idx;
                    }
                    o[idx].list.push({
                        "column": column,
                        "displayName": group,
                        "name": group,
                        "value": l.value
                    });
                }
            }.bind(this));
        }.bind(this));
        return o;
    },
    loadChartPie: function(){
        if (!this.selectedData) this.selectedData = this.statGridData;
        if (!this.selectedData.length) this.selectedData = this.statGridData;

        var pieData = this.rowToColumn(this.selectedData);

        MWF.require("MWF.widget.chart.Pie", function(){
            var count = pieData.length;
            var size = this.chartNode.getSize();
            var w = size.x/count;

            pieData.each(function(d){
                var pieAreaNode = new Element("div", {
                    "styles": {"overflow": "hidden", "height": ""+size.y+"px", "float": "left", "width": ""+w+"px"}
                }).inject(this.chartNode);
                var pieTitleNode = new Element("div", {
                    "styles": {"text-align": "center", "line-height":"30px", "font-size":"16px", "font-weight":"bold", "overflow": "hidden", "height": "30px"},
                    "text": d.group
                }).inject(pieAreaNode);
                var h = size.y-30;
                var pieNode = new Element("div", {
                    "styles": {"overflow": "hidden", "height": ""+h+"px"}
                }).inject(pieAreaNode);


                var data = [];
                d.list.each(function(d){
                    var v = this.toFloat(d.value);
                    data.push({"name": d.displayName, "value": v, "text": d.value});
                }.bind(this));

                var pie = new MWF.widget.chart.Pie(pieNode, data, {"textType": "percent"});
                pie.load();
            }.bind(this));

        }.bind(this));
    },

    loadChartLine: function(){
        if (this.json.isLegend) if (!this.chartFlagNode) this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartNode, "before");

        if (!this.selectedData) this.selectedData = this.statGridData;
        if (!this.selectedData.length) this.selectedData = this.statGridData;

        MWF.require("MWF.widget.chart.Line", function(){
            //this.selectedData.each()

            this.flag = [];
            this.bar = new MWF.widget.chart.Line(this.chartNode, this.selectedData, "group", {"delay": 0, "style": "monthly"});

            if (this.selectedData.length){
                this.selectedData[0].list.each(function(d, i){
                    this.flag.push({"name":d.displayName, "color": this.bar.colors[i]});
                    this.bar.addBar(function(v){
                        return this.toFloat(v.list[i].value);
                    }.bind(this), function(v){
                        return v.list[i].value;
                    });
                }.bind(this));
            }

            this.bar.addEvents({
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
            this.bar.load();
            if (this.json.isLegend) this.loadFlags();
        }.bind(this));
    },

    loadRowToColumn: function(){
        if (this.selectedRows.length || this.selectedCols.length) this.selectAll();
        this.statGridData = this.rowToColumn(this.statGridData);

        this.selectedData = [];
        this.reloadChart();
        this.reloadTable();
    },

    loadFlags: function(){
        this.flag.each(function(f, i){
            this.loadFlag(f, i);
        }.bind(this));
    },
    loadFlag: function(f, i){
        var flagNode = new Element("div", {"styles": this.css.ststChartFlagNode}).inject(this.chartFlagNode);
        var flagColorNode = new Element("div", {"styles": this.css.ststChartFlagColorNode}).inject(flagNode);
        flagColorNode.setStyle("background-color", f.color);
        var flagNameNode = new Element("div", {"styles": this.css.ststChartFlagNameNode}).inject(flagNode);
        flagNameNode.set("text", f.name);
        flagNameNode.set("title", f.name);

        flagNode.store("idx", i);
        flagNode.store("barColor", f.color);
        var _self = this;
        flagNode.addEvents({
            "mouseover": function(){
                this.getFirst().setStyles(_self.css.ststChartFlagColorNode_over);
                var idx = this.retrieve("idx");
                switch (_self.currentChart){
                    case "bar":
                        _self.highlightBar(idx);
                        break;
                    case "line":
                        _self.highlightLine(idx);
                        break;
                }
            },
            "mouseout": function(){
                this.getFirst().setStyles(_self.css.ststChartFlagColorNode);
                var idx = this.retrieve("idx");
                var barColor = this.retrieve("barColor");
                switch (_self.currentChart){
                    case "bar":
                        _self.unHighlightBar(idx, barColor);
                        break;
                    case "line":
                        _self.unHighlightLine(idx, barColor);
                        break;
                }

            }
        });
    },
    createDefs: function(node, data){
        var svgNode = node.append(data.tag);
        Object.each(data.attrs, function(v,k){svgNode.attr(k,v);});
        if (data.subs) {
            data.subs.each(function(v){
                this.createDefs(svgNode, v);
            }.bind(this));
        }
    },
    createHighlightDefs: function(id){
        //this.defssvg = this.bar.createDefs;
        var node = this.bar.svg.append("defs");
        var data = this.bar.css["rect_over_defs"];
        this.createDefs(node, data);
        node.select(function(){ return this.getFirst(); }).attr("id", id);
        node.attr("id", "defs_"+id);
    },
    unHighlightBar: function(i, barColor){
        var id = "rect_over_defs"+i;
        var def = d3.select("#defs_"+id);
        def.remove();
        var rects = this.bar.rectCluster[i];
        rects.attr(this.bar.css["rect_over_defs"].urlAttr, null);

        var texts = this.bar.textCluster[i];
        texts.attr("display", "none");
        texts.attr("font-weight", "normal");
    },
    unHighlightLine: function(i, barColor){
        var line = this.bar.lineCluster[i];
        var points = this.bar.pointCluster[i];
        var texts = this.bar.textCluster[i];

        line.attr("stroke-width", "1");
        points.attr("r", "5").attr("stroke-width", "1");
        texts.attr("display", "none");
        texts.attr("font-weight", "normal");
    },

    highlightBar: function(i){
        this.bar.rectCluster[i].remove();
        var rects = this.recreateBars(i);

        var id = "rect_over_defs"+i;
        this.createHighlightDefs(id);
        rects.attr(this.bar.css["rect_over_defs"].urlAttr, "url(#"+id+")");

        var texts = this.bar.textCluster[i];
        texts.attr("display", "block");
        texts.attr("font-weight", "bold");
    },
    highlightLine: function(i){
        var line = this.bar.lineCluster[i];
        var points = this.bar.pointCluster[i];
        var texts = this.bar.textCluster[i];
// debugger;
//         this.bar.lineGroup.selectAll("path").sort(function(a,b, i1, i2){
//             debugger;
//         });
        var p = this.bar.lineGroup.select(":last-child");
        line._groups[0][0].inject(p._groups[0][0],"after");

        line.attr("stroke-width", "3");
        points.attr("r", "6").attr("stroke-width", "3");
        texts.attr("display", "block");
        texts.attr("font-weight", "bold");
    },
    recreateBars: function(i){

        //var data = this.data.calculateGrid.map(function(d, idx) {
        var data = this.selectedData.map(function(d, idx) {
            return {"name": d["group"], "data": this.toFloat(d.list[i].value)};
        }.bind(this));


        this.bar.rectClass = Math.round(Math.random()*100);
        var barWidth = this.bar.xScale.bandwidth()/this.bar.barsData.length;
        var rects = this.bar.group.selectAll(".MWFBar_"+this.bar.rectClass+"_"+i)
            .data(data)
            .enter().append("rect")
            .attr("class", ".MWFBar_"+this.bar.rectClass+"_"+i)
            .attr("x", function(d) { return this.xScale(d.name)+(i*barWidth); }.bind(this.bar))
            .attr("width", barWidth)
            .attr("height", function(d) { return this.size.y - this.yScale(d.data); }.bind(this.bar))
            .attr("y", function(d) { return this.yScale(d.data); }.bind(this.bar))

            .attr("fill", this.bar.colors[i]);
        this.bar.rectCluster[i] = rects;
        this.bar.setEvents();

        return rects;
    },

    reloadTable: function(){
        this.tableAreaNode.empty();
        this.createTable();
    },

    createTableHead: function(){
        this.selectedCols = [];
        this.selectedRows = [];

        this.table = new Element("table", {
            "styles": this.css.statTableNode,
            "width": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.tableAreaNode);

        _self = this;
        this.headTr = this.table.insertRow();
        this.selectAllTd = (this.headTr.insertCell()).setStyles(this.css.statAllSelectTd).set("title", this.lp.selecteAll);
        this.selectAllTd.addEvent("click", function(){
            _self.selectAll(this);
        });

        this.selectEntryTd = (this.headTr.insertCell()).setStyles(this.css.statAllColSelectTd).set("title", this.lp.selecteAllCol);
        this.selectEntryTd.addEvent("click", function(){
            _self.selectAllCol(this);
        });

        if(this.statGridData.length){
            this.statGridData[0].list.each(function(d){
                var title = d.displayName;
                selectTd = (this.headTr.insertCell()).setStyles(this.css.statColSelectTd);
                selectTd.addEvent("click", function(){
                    _self.selectCol(this);
                });
            }.bind(this));
        }

        this.titleTr = this.table.insertRow();
        this.selectGroupTd = (this.titleTr.insertCell()).setStyles(this.css.statAllRowSelectTd).set("title", this.lp.selecteAllRow);
        this.categoryTitleTd = new Element("th", {
            "styles": this.css.statHeadTh,
            "text": this.data.calculate.title || this.lp.category
        }).inject(this.titleTr);
        this.categoryTitleTd.setStyle("width", "160px");

        if(this.statGridData.length) {
            this.statGridData[0].list.each(function (d) {
                var title = d.displayName;
                var th = new Element("th", {
                    "styles": this.css.statHeadTh,
                    "text": title
                }).inject(this.titleTr);
            }.bind(this));
        }
    },

    createTableData: function(){
        if (this.statGridData.length){
            var _self = this;

            // var groupColumn = null;
            // for (var c = 0; c<this.data.calculate.calculateList.length; c++){
            //     if (this.data.calculate.calculateList[c].column === this.data.group.column){
            //         groupColumn = this.data.calculate.calculateList[c];
            //         break;
            //     }
            // }

            this.statGridData.each(function(d){
                var tr = this.table.insertRow();
                var selectTd = tr.insertCell().setStyles(this.css.statRowSelectTd);
                selectTd.addEvent("click", function(){
                    _self.selectRow(this);
                });
                var text = d.group;
                // if (groupColumn){
                //     text = (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": d.group, "data": this.data}) : d.group;
                // }
                var categoryTh = new Element("th", {"styles": this.css.statHeadTh, "text": text}).inject(tr);

                d.list.each(function(l){
                    var td = new Element("td", {"styles": this.css.statContentTdNode}).inject(tr);
                    td.set("text", l.value);
                    this.setDragEvent(td);
                }.bind(this));
            }.bind(this));
        }
    },
    setDragEvent: function(td){
        new Drag(td, {
            "onStart": function(dragged, e){
                this.cellDragStart(dragged, e);
            }.bind(this),
            "onDrag": function(dragged, e){
                this.cellDrag(dragged, e);
            }.bind(this),
            "onComplete": function(dragged, e){
                this.completeDrag(dragged, e);
            }.bind(this)
        });
        var _self = this;
        td.addEvent("click", function(){
            var cellIndex = td.cellIndex-2;
            var rowIndex = td.getParent("tr").rowIndex-2;
            if (_self.checkIsSelected(cellIndex, rowIndex)){

            }else{
                if (_self.selectedCols.length || _self.selectedRows.length){
                    _self.selectAll();
                }
            }
        });
    },
    cellDragStart: function(td, e){
        //if (_self.selectedCols.length || _self.selectedRows.length){
        //    _self.selectAll();
        //}

        var p = td.getPosition();
        var size = td.getSize();
        td.store("start", {"x": p.x, "y": p.y});
        td.store("start2", {"x": p.x+size.x, "y": p.y+size.y});
    },
    getSelectedCells: function(td, start, e){
        var ox = e.page.x-start.x;
        var oy = e.page.y-start.y;
        var tdSize = td.getSize();

        var cols = (ox/tdSize.x).toInt();
        var rows = (oy/tdSize.y).toInt();
        return {"cols": cols, "rows": rows};
    },
    cellDrag: function(td, e){
        this.selectedRows = [];
        this.selectedCols = [];
        this.selectedData = [];

        var start = td.retrieve("start");
        var start2 = td.retrieve("start2");
        var c1 = this.getSelectedCells(td, start, e);
        var c2 = this.getSelectedCells(td, start2, e);

        var cols = (Math.abs(c1.cols)>Math.abs(c2.cols)) ? c1.cols : c2.cols;
        var rows = (Math.abs(c1.rows)>Math.abs(c2.rows)) ? c1.rows : c2.rows;

        var cellIndex = td.cellIndex-2;
        var rowIndex = td.getParent("tr").rowIndex-2;
        if (!cellIndex || cellIndex<0) cellIndex = 0;
        if (!rowIndex || rowIndex<0) rowIndex = 0;

        var toCellIndex = cellIndex+cols;
        var toRowIndex = rowIndex+rows;

        if (toRowIndex>rowIndex){
            for (var i=rowIndex; i<=toRowIndex; i++) this.selectedRows.push(i);
        }else{
            for (var i=toRowIndex; i<=rowIndex; i++) this.selectedRows.push(i);
        }
        if (toCellIndex>cellIndex){
            for (var i=cellIndex; i<=toCellIndex; i++) this.selectedCols.push(i);
        }else{
            for (var i=toCellIndex; i<=cellIndex; i++) this.selectedCols.push(i);
        }
        this.checkSelectedCells();
    },
    completeDrag: function(td, e){
        var trs = this.table.getElements("tr");
        var tds = trs[0].getElements("td");

        this.selectedCols.each(function(i){
            tds[i+2].setStyles(this.css.statTableSelectedTd);
        }.bind(this));
        this.selectedRows.each(function(i){
            trs[i+2].getElement("td").setStyles(this.css.statTableSelectedTd);
        }.bind(this));
        this.reloadChart();
    },

    selectCol: function(td){
        var i = td.cellIndex;
        var dataIndex = i-2;

        if (this.selectedCols.indexOf(dataIndex)!=-1){
            td.setStyles(this.css.statTableSelectTd);
            this.selectedCols.erase(dataIndex);
        }else{
            td.setStyles(this.css.statTableSelectedTd);
            this.selectedCols.push(dataIndex);
        }
        this.checkSelectedCells();
        this.reloadChart();
    },
    selectRow: function(td){
        var tr = td.getParent("tr");
        var i = tr.rowIndex;
        var dataIndex = i-2;

        if (this.selectedRows.indexOf(dataIndex)!=-1){
            td.setStyles(this.css.statTableSelectTd);
            this.selectedRows.erase(dataIndex);
        }else{
            td.setStyles(this.css.statTableSelectedTd);
            this.selectedRows.push(dataIndex);
        }
        this.checkSelectedCells();
        this.reloadChart();
    },
    selectAllCol: function(){
        if (this.selectedCols.length){
            this.selectedCols = [];
            var trs = this.table.getElements("tr");
            var tds = trs[0].getElements("td");
            for (var i=2; i<tds.length; i++){
                tds[i].setStyles(this.css.statTableSelectTd);
            }
            //for (var n=2; n<trs.length; n++){
            //    trs[n].getElement("td").setStyles(this.css.statTableSelectTd);
            //}
        }else{
            var seltrs = this.table.getElements("tr");
            var seltds = seltrs[0].getElements("td");
            for (var n=2; n<seltds.length; n++){
                this.selectedCols.push(n-2);
                seltds[n].setStyles(this.css.statTableSelectedTd);
            }
        }
        this.checkSelectedCells();
        this.reloadChart();
    },

    selectAll: function(){
        if (this.selectedRows.length || this.selectedCols.length){
            this.selectedRows = [];
            this.selectedCols = [];

            var trs = this.table.getElements("tr");
            var tds = trs[0].getElements("td");
            for (var i=2; i<tds.length; i++){
                tds[i].setStyles(this.css.statTableSelectTd);
            }
            for (var n=2; n<trs.length; n++){
                trs[n].getElement("td").setStyles(this.css.statTableSelectTd);
            }
        }else{
            var seltrs = this.table.getElements("tr");
            var seltds = seltrs[0].getElements("td");
            for (var seli=2; seli<seltds.length; seli++){
                this.selectedCols.push(seli-2);
                seltds[seli].setStyles(this.css.statTableSelectedTd);
            }
            for (var seln=2; seln<seltrs.length; seln++){
                this.selectedRows.push(seln-2);
                seltrs[seln].getElement("td").setStyles(this.css.statTableSelectedTd);
            }
        }
        this.checkSelectedCells();
        this.reloadChart();
    },

    checkIsSelected: function(ci, ri){
        if (!this.selectedCols.length && !this.selectedRows.length) return false;
        var colSelect = (!this.selectedCols.length) || this.selectedCols.indexOf(ci)!=-1;
        var rowSelect = (!this.selectedRows.length) || this.selectedRows.indexOf(ri)!=-1;
        return (colSelect && rowSelect);
    },

    checkSelectedCells: function(){
        this.selectedData = [];
        var rows = this.table.getElements("tr");
        for (var rowIdx = 2; rowIdx<rows.length; rowIdx++){
            var o = {
                "group": this.statGridData[rowIdx-2].group,
                "list": []
            };
            var cols = rows[rowIdx].getElements("td");
            for (var colIdx = 1; colIdx<cols.length; colIdx++){
                if (this.checkIsSelected(colIdx-1, rowIdx-2)){
                    cols[colIdx].setStyles(this.css.statContentTdNode_selected);
                    o.list.push({
                        "column": this.statGridData[rowIdx-2].list[colIdx-1].column,
                        "displayName": this.statGridData[rowIdx-2].list[colIdx-1].displayName,
                        "value": this.statGridData[rowIdx-2].list[colIdx-1].value
                    });
                }else{
                    cols[colIdx].setStyles(this.css.statContentTdNode);
                }
            }
            if (o.list.length) this.selectedData.push(o);
        }
    },
    reloadChart: function(){
        if (this.json.isChart && this.charts.length > 0 ){
            if (this.bar) this.bar.destroy();
            this.bar = null;
            if (this.chartFlagNode){
                this.chartFlagNode.destroy();
                this.chartFlagNode = null;
            }
            if (this.chartNode)this.chartNode.empty();
            switch (this.currentChart){
                case "bar":
                    this.loadChartBar();
                    break;
                case "pie":
                    this.loadChartPie();
                    break;
                case "line":
                    this.loadChartLine();
                    break;
                default:
                    this.loadChartBar();
            }
        }
    }
});