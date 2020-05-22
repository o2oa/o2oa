MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Application = MWF.xApplication.process.Application || {};
MWF.require("MWF.widget.Mask", null, false);
MWF.xDesktop.requireApp("process.Application", "ViewExplorer", null, false);
MWF.xApplication.process.Application.StatExplorer = new Class({
	Extends: MWF.xApplication.process.Application.ViewExplorer,
	Implements: [Options, Events],

    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "../x_component_process_Application/$WorkExplorer/";
        this.cssPath = "../x_component_process_Application/$WorkExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.items=[];
    },

    loadContentNode: function(){
        this.filterNode.setStyles(this.css.statListNode);
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.createWorkListHead();
        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },

    setContentSize: function(){
        //var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        var filterSize = this.filterNode.getSize();

        var height = nodeSize.y-pt-pb-filterSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;
    },
    showMask: function(){
        if (!this.mask){
            this.mask = new MWF.widget.Mask({"style": "desktop"});
            this.mask.loadNode(this.node);
        }
    },
    hideMask: function(){
        if (this.mask){
            this.mask.hide();
            this.mask = null;
        }
    },
    loadViewList: function(){
        this.actions.listStat(this.app.options.id, function(json){

            if (json.data.length){
                json.data.each(function(process){
                    this.loadViewListNode(process);
                }.bind(this));
                this.hideMask();
                if (this.currentView){
                    this.currentView.click();
                }else{
                    this.items[0].click();
                }
            }else{
                this.filterNode.destroy();
                var noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.app.lp.noStat
                }).inject(this.elementContentListNode);
                this.hideMask();
            }
        }.bind(this));
    },
    _getLnkPar: function(view){
        return {
            "icon": this.path+this.options.style+"/statIcon/lnk.png",
            "title": view.name,
            "par": "process.Application#{\"navi\": 3, \"id\": \""+this.app.options.id+"\", \"viewName\": \""+view.name+"\", \"hideMenu\": true}"
        };
    },
    loadViewData: function(node){
        this.showMask();
        this.items.each(function(item){
            item.setStyles(this.css.filterViewNode);
        }.bind(this));
        node.setStyles(this.css.filterViewNode_current);

        this.elementContentListNode.empty();
        if (this.stat){
            this.stat.destroy();
            this.stat = null;
        }

        var view = node.retrieve("view");
        this.actions.loadStat(view.id, this.app.options.id, null, function(json){
            if (json.data.calculate.isGroup){
                this.stat = new MWF.xApplication.process.Application.StatExplorer.GroupStat(this, json.data);
            }else{
                this.stat = new MWF.xApplication.process.Application.StatExplorer.Stat(this, json.data);
            }
            //this.showViewData(json.data);
            this.hideMask();
        }.bind(this));
    },
    destroy: function(){
        if (this.stat){
            this.stat.destroy();
        }
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.process.Application.StatExplorer.Stat = new Class({
    initialize: function(explorer, data){
        this.explorer = explorer;
        this.data = data;
        this.css = this.explorer.css;
        this.lp = this.explorer.app.lp;
        this.node = this.explorer.elementContentListNode;
        this.load();
    },
    load: function(){
        this.chartAreaNode = new Element("div", {"styles": this.css.statChartAreaNode}).inject(this.node);
        this.tableAreaNode = new Element("div", {"styles": this.css.statTableAreaNode}).inject(this.node);
        this.loadData();
        this.createTable();
        this.createChart();
    },
    loadData: function(){
        var entries = {};
        this.data.selectEntryList.each(function(entry){entries[entry.column] = entry;}.bind(this));

    },
    createChart: function(){
        //this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartAreaNode);
        this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        this.loadChart();
        this.resizeChartFun = this.resizeChart.bind(this);
        this.explorer.app.addEvent("resizeCompleted", this.resizeChartFun);
        this.explorer.app.addEvent("postMaxSize", this.resizeChartFun);
        this.explorer.app.addEvent("postRestoreSize", this.resizeChartFun);
    },
    resizeChart: function(){
        if (this.bar) this.bar.destroy();
        this.bar = null;
        this.chartNode.empty();
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

        this.data.calculate.calculateEntryList.each(function(title){
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
        if (this.resizeChartFun){
            this.explorer.app.removeEvent("resizeCompleted", this.resizeChartFun);
            this.explorer.app.removeEvent("postMaxSize", this.resizeChartFun);
            this.explorer.app.removeEvent("postRestoreSize", this.resizeChartFun);
        }
        MWF.release(this);
    }
});

MWF.xApplication.process.Application.StatExplorer.GroupStat = new Class({
    Extends: MWF.xApplication.process.Application.StatExplorer.Stat,

    loadData: function(){
        var entries = {};
        var groupColumn = null;
        this.data.selectEntryList.each(function(entry){
            entries[entry.column] = entry;
            if (entry.column === this.data.groupEntry.column){
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
        this.chartFlagNode = new Element("div", {"styles": this.css.statChartFlagAreaNode}).inject(this.chartAreaNode);
        this.chartNode = new Element("div", {"styles": this.css.statChartNode}).inject(this.chartAreaNode);
        this.loadChart();
        this.resizeChartFun = this.resizeChart.bind(this);
        this.explorer.app.addEvent("resizeCompleted", this.resizeChartFun);
        this.explorer.app.addEvent("postMaxSize", this.resizeChartFun);
        this.explorer.app.addEvent("postRestoreSize", this.resizeChartFun);
    },
    resizeChart: function(){
        if (this.bar) this.bar.destroy();
        this.bar = null;
        this.chartFlagNode.empty();
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
            this.loadFlags();
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
        this.selectAllTd = this.headTr.insertCell().setStyles(this.css.statAllSelectTd).set("title", this.explorer.app.lp.selecteAll);
        this.selectAllTd.addEvent("click", function(){
            _self.selectAll(this);
        });

        this.selectEntryTd = this.headTr.insertCell().setStyles(this.css.statAllColSelectTd).set("title", this.explorer.app.lp.selecteAllCol);
        this.selectEntryTd.addEvent("click", function(){
            _self.selectAllCol(this);
        });

        this.data.calculate.calculateEntryList.each(function(title){
            selectTd = this.headTr.insertCell().setStyles(this.css.statColSelectTd);
            selectTd.addEvent("click", function(){
                _self.selectCol(this);
            });
        }.bind(this));

        this.titleTr = this.table.insertRow();
        this.selectGroupTd = this.titleTr.insertCell().setStyles(this.css.statAllRowSelectTd).set("title", this.explorer.app.lp.selecteAllRow);
        this.categoryTitleTd = new Element("th", {
            "styles": this.css.statHeadTh,
            "text": this.data.calculate.title || this.explorer.app.lp.category
        }).inject(this.titleTr);
        this.categoryTitleTd.setStyle("width", "160px");

        this.data.calculate.calculateEntryList.each(function(title){
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
            for (var c = 0; c<this.data.selectEntryList.length; c++){
                if (this.data.selectEntryList[c].column === this.data.groupEntry.column){
                    groupColumn = this.data.selectEntryList[c];
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
        if (this.bar) this.bar.destroy();
        this.bar = null;
        this.chartFlagNode.empty();
        this.chartNode.empty();
        this.loadChart();
    }
});

MWF.xApplication.process.Application.StatExplorer.GroupStat.Select = new Class({
    //Extends: MWF.xApplication.process.Application.StatExplorer.Stat
});