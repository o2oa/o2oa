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

        this.path = "/x_component_query_Query/$Statistician/";
        this.cssPath = "/x_component_query_Query/$Statistician/"+this.options.style+"/css.wcss";
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
        this.css = this.statistician.css;
        this.lp = this.statistician.lp;
        this.node = this.statistician.node;
        this.load();
    },
    load: function(){
        if (this.json.isChart) this.chartAreaNode = new Element("div", {"styles": this.css.statChartAreaNode}).inject(this.node);
        if (this.json.isTable) this.tableAreaNode = new Element("div", {"styles": this.css.statTableAreaNode}).inject(this.node);
        this.loadData();
        if (this.json.isTable) this.createTable();
        if (this.json.isChart) this.createChart();
    },
    loadData: function(){
        var entries = {};
        this.data.selectList.each(function(entry){entries[entry.column] = entry;}.bind(this));

    },
    createChart: function(){
        this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        this.loadChart();
        if (this.statistician.app){
            this.resizeChartFun = this.resizeChart.bind(this);
            this.statistician.app.addEvent("resizeCompleted", this.resizeChartFun);
            this.statistician.app.addEvent("postMaxSize", this.resizeChartFun);
            this.statistician.app.addEvent("postRestoreSize", this.resizeChartFun);
        }
    },
    resizeChart: function(){
        if (this.bar) this.bar.destroy();
        this.bar = null;
        if (this.chartNode) this.chartNode.empty();
        this.loadChart();
    },
    loadChart: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.bar = new MWF.widget.chart.Bar(this.chartNode, this.data.calculateGrid, "displayName", {"delay": 0, "style": "monthly"});
            this.bar.addBar("value");
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
        }).inject(this.node);
        this.headTr = new Element("tr").inject(this.table);

        this.data.calculate.calculateList.each(function(title){
            var th = new Element("th", {
                "styles": this.css.statHeadTh,
                "text": title.displayName
            }).inject(this.headTr);
        }.bind(this));
    },

    createTableData: function(){
        if (this.data.calculateGrid.length){
            var tr = new Element("tr").inject(this.table);
            this.data.calculateGrid.each(function(d){
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
        this.data.selectList.each(function(entry){
            entries[entry.column] = entry;
            if (entry.column === this.data.group.column){
                groupColumn = entry;
            }
        }.bind(this));

        this.data.calculateGrid.each(function(dd){
            if (groupColumn){
                dd.group = (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": dd.group, "data": this.data}) : dd.group;
            }
            dd.list.each(function(c){
                c.value = (entries[c.column].code) ? MWF.Macro.exec(entries[c.column].code, {"value": c.value, "data": this.data}) : c.value
            }.bind(this));
        }.bind(this));

    },

    createChart: function(){
        if (this.json.isLegend) this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartAreaNode);
        this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        this.loadChart();
        if (this.statistician.app){
            this.resizeChartFun = this.resizeChart.bind(this);
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
        this.loadChart();
    },
    loadChart: function(){
        if (!this.selectedData) this.selectedData = this.data.calculateGrid;
        if (!this.selectedData.length) this.selectedData = this.data.calculateGrid;

        MWF.require("MWF.widget.chart.Bar", function(){
            this.flag = [];
            this.bar = new MWF.widget.chart.Bar(this.chartNode, this.selectedData, "group", {"delay": 0, "style": "monthly"});

            if (this.selectedData.length){
                this.selectedData[0].list.each(function(d, i){
                    this.flag.push({"name":d.displayName, "color": this.bar.colors[i]});
                    this.bar.addBar(function(v){return v.list[i].value});
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
                _self.highlightBar(idx);
            },
            "mouseout": function(){
                this.getFirst().setStyles(_self.css.ststChartFlagColorNode);
                var idx = this.retrieve("idx");
                var barColor = this.retrieve("barColor");
                _self.unHighlightBar(idx, barColor);
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
    recreateBars: function(i){
        var data = this.data.calculateGrid.map(function(d, idx) {
            return {"name": d["group"], "data": d.list[i].value};
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

    createTableHead: function(){
        this.selectedCols = [];
        this.selectedRows = [];

        this.table = new Element("table", {
            "styles": this.css.statTableNode,
            "width": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.node);

        _self = this;
        this.headTr = this.table.insertRow();
        this.selectAllTd = this.headTr.insertCell().setStyles(this.css.statAllSelectTd).set("title", this.lp.selecteAll);
        this.selectAllTd.addEvent("click", function(){
            _self.selectAll(this);
        });

        this.selectEntryTd = this.headTr.insertCell().setStyles(this.css.statAllColSelectTd).set("title", this.lp.selecteAllCol);
        this.selectEntryTd.addEvent("click", function(){
            _self.selectAllCol(this);
        });

        this.data.calculate.calculateList.each(function(title){
            selectTd = this.headTr.insertCell().setStyles(this.css.statColSelectTd);
            selectTd.addEvent("click", function(){
                _self.selectCol(this);
            });
        }.bind(this));

        this.titleTr = this.table.insertRow();
        this.selectGroupTd = this.titleTr.insertCell().setStyles(this.css.statAllRowSelectTd).set("title", this.lp.selecteAllRow);
        this.categoryTitleTd = new Element("th", {
            "styles": this.css.statHeadTh,
            "text": this.data.calculate.title || this.lp.category
        }).inject(this.titleTr);
        this.categoryTitleTd.setStyle("width", "160px");

        this.data.calculate.calculateList.each(function(title){
            var th = new Element("th", {
                "styles": this.css.statHeadTh,
                "text": title.displayName
            }).inject(this.titleTr);
        }.bind(this));
    },

    createTableData: function(){
        if (this.data.calculateGrid.length){
            var _self = this;

            var groupColumn = null;
            for (var c = 0; c<this.data.selectList.length; c++){
                if (this.data.selectList[c].column === this.data.group.column){
                    groupColumn = this.data.selectList[c];
                    break;
                }
            }

            this.data.calculateGrid.each(function(d){
                var tr = this.table.insertRow();
                var selectTd = tr.insertCell().setStyles(this.css.statRowSelectTd);
                selectTd.addEvent("click", function(){
                    _self.selectRow(this);
                });
                var text = d.group;
                if (groupColumn){
                    text = (groupColumn.code) ? MWF.Macro.exec(groupColumn.code, {"value": d.group, "data": this.data}) : d.group;
                }
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
                debugger;
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
                "group": this.data.calculateGrid[rowIdx-2].group,
                "list": []
            };
            var cols = rows[rowIdx].getElements("td");
            for (var colIdx = 1; colIdx<cols.length; colIdx++){
                if (this.checkIsSelected(colIdx-1, rowIdx-2)){
                    cols[colIdx].setStyles(this.css.statContentTdNode_selected);
                    o.list.push({
                        "column": this.data.calculateGrid[rowIdx-2].list[colIdx-1].column,
                        "displayName": this.data.calculateGrid[rowIdx-2].list[colIdx-1].displayName,
                        "value": this.data.calculateGrid[rowIdx-2].list[colIdx-1].value
                    });
                }else{
                    cols[colIdx].setStyles(this.css.statContentTdNode);
                }
            }
            if (o.list.length) this.selectedData.push(o);
        }
    },
    reloadChart: function(){
        if (this.json.isChart){
            if (this.bar) this.bar.destroy();
            this.bar = null;
            this.chartFlagNode.empty();
            this.chartNode.empty();
            this.loadChart();
        }
    }
});