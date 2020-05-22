MWF.xApplication.BAM.monthly = MWF.xApplication.BAM.monthly || {};
MWF.xApplication.BAM.monthly.MonthlyContent = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(monthly, node, options){
        this.setOptions(options);

        this.path = "../x_component_BAM/monthly/$MonthlyContent/";
        this.cssPath = "../x_component_BAM/monthly/$MonthlyContent/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.monthly = monthly;
        this.app = this.monthly.app;
        this.actions = this.app.actions;
        this.container = $(node);
        this.initData();
        this.load();
    },
    initData: function(){
        this.sort = {
            "type": "application",
            "range": {}
        };
        this.filter = {
            "company": "",
            "department": "",
            "person": "",
            "application": "",
            "applicationName": "",
            "process":"",
            "processName":"",
            "activity": "",
            "activityName": ""
        };
    },
    clearFilter: function(){
        this.filter = {
            "unit": "",
            "topUnit": "",
            // "company": "",
            // "department": "",
            "person": "",
            "application": "",
            "applicationName": "",
            "process":"",
            "processName":"",
            "activity": "",
            "activityName": ""
        };
    },
    reload: function(){
        this.sortActionTextNode.set("html", this.getSortHtml());
        this.filterActionTextNode.set("html", this.getFilterHtml());
        this.table.destroy();
        this.chart.destroy();
        this.app.mask();
        this.loadData(function(){
            this.loadTable();
            this.loadChart();
            this.fireEvent("loaded");
        }.bind(this));
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.monthNode}).inject(this.container);

        this.titleAreaNode = new Element("div", {"styles": this.css.monthTitleAreaNode}).inject(this.node);

        this.sortByAreaNode = new Element("div", {"styles": this.css.monthSortByAreaNode}).inject(this.titleAreaNode);
        this.filterAreaNode = new Element("div", {"styles": this.css.monthFilterAreaNode}).inject(this.titleAreaNode);
        this.chartAreaNode = new Element("div", {"styles": this.css.monthChartAreaNode}).inject(this.node);
        this.tableAreaNode = new Element("div", {"styles": this.css.monthTableAreaNode}).inject(this.node);

        this.createSortNode();
        this.createFilterNode();
        this.app.mask();
        this.loadData(function(){
            this.loadTable();
            this.loadChart();
            this.fireEvent("loaded");
        }.bind(this));
    },
    createSortNode: function(){
        this.sortActionNode = new Element("div", {"styles": this.css.sortActionNode}).inject(this.sortByAreaNode);
        this.sortActionTitleNode = new Element("div", {"styles": this.css.sortActionTitleNode}).inject(this.sortActionNode);
        this.sortActionTextNode = new Element("div", {"styles": this.css.sortActionTextNode}).inject(this.sortActionNode);

        this.sortActionTitleNode.set("text", this.app.lp.monthly.sort);
        this.sortActionTextNode.set("html", this.getSortHtml());

        this.selectSort();
    },
    selectSort: function(){
        if (!this.sortSelectMenu){
            this.sortSelectMenu = new MWF.xDesktop.Menu(this.sortActionTitleNode, {
                "event": "click",
                "style": "BAMStyle",
                "offsetX": -1,
                "offsetY": -1,
                "container": this.app.content,
                "onQueryShow": function(){
                    for (var i=0; i<5; i++){
                        //this.sortSelectMenu.items[i].setDisable(false);
                        this.sortSelectMenu.items[i].item.getFirst().empty();
                    }
                    var arr = ["company", "department", "application", "process", "activity"];
                    var idx = arr.indexOf(this.sort.type);
                    if (idx!=-1){
                        //this.sortSelectMenu.items[idx].setDisable(true);
                        //this.sortSelectMenu.items[idx].
                        src = "../x_component_BAM/monthly/$MonthlyContent/"+this.options.style+"/icon/selected.png";
                        var img = new Element("img", {"styles": this.sortSelectMenu.css.menuItemImg, "src": src}).inject(this.sortSelectMenu.items[idx].item.getFirst());
                    }
                }.bind(this),
                "onPostShow": function(){
                    var szie = this.sortActionNode.getSize();
                    var menuSzie = this.sortSelectMenu.borderNode.getSize();
                    if (szie.x>menuSzie.x){
                        this.sortSelectMenu.node.setStyle("width", ""+szie.x+"px");
                    }
                }.bind(this),
                "onPostHide": function(){
                    this.sortSelectMenu.node.setStyle("width", "auto");
                    this.sortActionNode.setStyles(this.css.sortActionNode);
                    this.sortMenu = false;
                    this.sortActionTitleNode.setStyles(this.css.sortActionTitleNode);
                }.bind(this)
            });
            this.sortSelectMenu.load();
            this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.topUnit, "click", function(){this.sortSelected("topUnit");}.bind(this));
            this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.unit, "click", function(){this.sortSelected("unit");}.bind(this));
            //this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.company, "click", function(){this.sortSelected("company");}.bind(this));
            //this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.department, "click", function(){this.sortSelected("department");}.bind(this));
            this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.application, "click", function(){this.sortSelected("application");}.bind(this));
            this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.process, "click", function(){this.sortSelected("process");}.bind(this));
            this.sortSelectMenu.addMenuItem(this.app.lp.monthly.sortItems.activity, "click", function(){this.sortSelected("activity");}.bind(this));
        }
        this.sortActionTitleNode.addEvents({
            "click": function(){
                var position = this.sortByAreaNode.getPosition(this.sortByAreaNode.getOffsetParent());
                this.sortActionNode.setStyles(this.css.sortActionNode_menu);
                var i = MWF.xDesktop.zIndexPool.zIndex+1;
                this.sortActionNode.setStyle("border-bottom", "0px solid #999");
                this.sortActionNode.setStyles({
                    "left": position.x+"px",
                    "z-index": i,
                    "top": position.y+"px"
                });
                this.sortActionTitleNode.setStyles(this.css.sortActionTitleNode_menu);
                this.sortMenu = true;
            }.bind(this),
            "mouseover": function(){
                if (!this.sortMenu) this.sortActionTitleNode.setStyles(this.css.sortActionTitleNode_over);
            }.bind(this),
            "mouseout": function(){
                if (!this.sortMenu) this.sortActionTitleNode.setStyles(this.css.sortActionTitleNode);
            }.bind(this)
        });
    },
    sortSelected: function(type){
        switch (type){
            case "topUnit":
                this.sort = {"type": "topUnit","range": {}};
                this.filter.company = "";
                this.filter.department = "";
                this.filter.person = "";
                this.reload();
                break;
            case "unit":
                this.selectedSortUnit(type);
                break;
            // case "company":
            //     this.sort = {"type": "company","range": {}};
            //     this.filter.company = "";
            //     this.filter.department = "";
            //     this.filter.person = "";
            //     this.reload();
            //     break;
            // case "department":
            //     this.selectedSortCompany(type);
            //     break;
            case "application":
                this.sort = {"type": "application","range": {}};
                this.filter.application = "";
                this.filter.process = "";
                this.filter.activity = "";
                this.reload();
                break;
            case "process":
                this.selectedSortApplication(type);
                break;
            case "activity":
                this.selectedSortProcess(type);
                break;
        }
    },
    selectedSortUnit: function(type){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "unit",
                "count": 1,
                "title": this.app.lp.monthly.selectSortCompany,
                "onComplete": function(items){
                    this.sort = {"type": type,"range": {
                        "unitName": items[0].data.id,
                        "company": items[0].data.id
                    }};
                    this.filter.unit = "";
                    this.filter.person = "";
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    // selectedSortCompany: function(type){
    //     MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
    //         var options = {
    //             "type": "company",
    //             "count": 1,
    //             "title": this.app.lp.monthly.selectSortCompany,
    //             "onComplete": function(items){
    //                 this.sort = {"type": type,"range": {
    //                     "companyName": items[0].data.name,
    //                     "company": items[0].data.id
    //                 }};
    //                 this.filter.company = "";
    //                 this.filter.department = "";
    //                 this.filter.person = "";
    //                 this.reload();
    //             }.bind(this)
    //         };
    //         var selector = new MWF.OrgSelector(this.app.content, options);
    //     }.bind(this));
    // },
    selectedSortApplication: function(type){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "application",
                "count": 1,
                "title": this.app.lp.monthly.selectSortApplication,
                "onComplete": function(items){
                    this.sort = {"type": type,"range": {
                        "applicationName": items[0].data.name,
                        "application": items[0].data.id
                    }};
                    this.filter.application = "";
                    this.filter.process = "";
                    this.filter.activity = "";
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    selectedSortProcess: function(type){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "process",
                "count": 1,
                "title": this.app.lp.monthly.selectSortProcess,
                "onComplete": function(items){
                    this.sort = {"type": type,"range": {
                        "applicationName": items[0].data.applicationName,
                        "application": items[0].data.application,
                        "processName": items[0].data.name,
                        "process": items[0].data.id
                    }};
                    this.filter.application = "";
                    this.filter.process = "";
                    this.filter.activity = "";
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },

    getSortHtml: function(){
        var typeText = this.app.lp.monthly.sortInfor[this.sort.type];
        if (this.sort.type==="process"){
            typeText = typeText.replace(/{app}/g, "<span style='color: #ff0000'>"+this.sort.range.applicationName+"</span>");
        }
        if (this.sort.type==="activity"){
            typeText = typeText.replace(/{app}/g, "<span style='color: #ff0000'>"+this.sort.range.applicationName+"</span>");
            typeText = typeText.replace(/{pro}/g, "<span style='color: #ff0000'>"+this.sort.range.processName+"</span>");
        }
        // if (this.sort.type=="department"){
        //     typeText = typeText.replace(/{com}/g, "<span style='color: #ff0000'>"+this.sort.range.companyName+"</span>");
        // }
        if (this.sort.type==="unit"){
            typeText = typeText.replace(/{unit}/g, "<span style='color: #ff0000'>"+this.sort.range.unitName+"</span>");
        }
        return typeText;
    },

    selectFilter: function(){
        if (!this.filterSelectMenu){
            this.filterSelectMenu = new MWF.xDesktop.Menu(this.filterActionTitleNode, {
                "event": "click",
                "style": "BAMStyle",
                "offsetX": -1,
                "offsetY": -1,
                "container": this.app.content,
                "onQueryShow": function(){
                    this.filterSelectMenu.clearItems();
                    if (this.sort.type=="activity" || this.sort.type=="application" || this.sort.type=="process"){
                        this.filterSelectMenu.addMenuItem(this.app.lp.monthly.filterItems.unit, "click", function(){this.filterSelected("unit");}.bind(this));

                        // this.filterSelectMenu.addMenuItem(this.app.lp.monthly.filterItems.company, "click", function(){this.filterSelected("company");}.bind(this));
                        // this.filterSelectMenu.addMenuItem(this.app.lp.monthly.filterItems.department, "click", function(){this.filterSelected("department");}.bind(this));
                    }else{
                        this.filterSelectMenu.addMenuItem(this.app.lp.monthly.filterItems.application, "click", function(){this.filterSelected("application");}.bind(this));
                        this.filterSelectMenu.addMenuItem(this.app.lp.monthly.filterItems.process, "click", function(){this.filterSelected("process");}.bind(this));
                    }
                }.bind(this),
                "onPostShow": function(){
                    var size = this.filterActionNode.getSize();
                    var titleSize = this.filterActionTitleNode.getSize();
                    var menuSize = this.filterSelectMenu.borderNode.getSize();
                    if (size.x>menuSize.x){
                        this.filterSelectMenu.node.setStyle("width", ""+size.x+"px");
                    }
                    menuSize = this.filterSelectMenu.borderNode.getSize();
                    var x = 0-(menuSize.x - titleSize.x);
                    this.filterSelectMenu.setOptions({
                        "offsetX": x
                    });
                    this.filterSelectMenu.setPosition();
                }.bind(this),
                "onPostHide": function(){
                    this.filterSelectMenu.node.setStyle("width", "auto");
                    this.filterActionNode.setStyles(this.css.filterActionNode);
                    this.filterMenu = false;
                    this.filterActionTitleNode.setStyles(this.css.filterActionTitleNode);
                }.bind(this)
            });
            this.filterSelectMenu.load();
        }
        this.filterActionTitleNode.addEvents({
            "click": function(){
                var position = this.filterActionNode.getPosition(this.filterActionNode.getOffsetParent());
                this.filterActionNode.setStyles(this.css.filterActionNode_menu);
                var i = MWF.xDesktop.zIndexPool.zIndex+1;
                this.filterActionNode.setStyle("border-bottom", "0px solid #999");
                this.filterActionNode.setStyles({
                    "left": position.x+"px",
                    "z-index": i,
                    "top": position.y+"px"
                });
                this.filterActionTitleNode.setStyles(this.css.filterActionTitleNode_menu);
                this.filterMenu = true;
            }.bind(this),
            "mouseover": function(){
                if (!this.filterMenu) this.filterActionTitleNode.setStyles(this.css.filterActionTitleNode_over);
            }.bind(this),
            "mouseout": function(){
                if (!this.filterMenu) this.filterActionTitleNode.setStyles(this.css.filterActionTitleNode);
            }.bind(this)
        });
    },
    filterSelected: function(type){
        switch (type){
            case "unit":
                this.selectedFilterUnit(type);
                break;
            // case "company":
            //     this.selectedFilterCompany(type);
            //     break;
            // case "department":
            //     this.selectedFilterDepartemnt(type);
            //     break;
            case "application":
                this.selectedFilterApplication(type);
                break;
            case "process":
                this.selectedFilterProcess(type);
                break;
        }
    },
    selectedFilterUnit: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "unit",
                "count": 1,
                "title": this.app.lp.monthly.selectSortUnit,
                "onComplete": function(items){
                    this.clearFilter();
                    //this.filter.company = items[0].data.id;
                    this.filter.unit = items[0].data.distinguishedName;
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    // selectedFilterCompany: function(type){
    //     MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
    //         var options = {
    //             "type": "company",
    //             "count": 1,
    //             "title": this.app.lp.monthly.selectSortCompany,
    //             "onComplete": function(items){
    //                 this.clearFilter();
    //                 //this.filter.company = items[0].data.id;
    //                 this.filter.company = items[0].data.name;
    //                 this.reload();
    //             }.bind(this)
    //         };
    //         var selector = new MWF.OrgSelector(this.app.content, options);
    //     }.bind(this));
    // },
    // selectedFilterDepartemnt: function(type){
    //     MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
    //         var options = {
    //             "type": "department",
    //             "count": 1,
    //             "title": this.app.lp.monthly.selectSortCompany,
    //             "onComplete": function(items){
    //                 this.clearFilter();
    //                 //this.filter.department = items[0].data.id;
    //                 this.filter.department = items[0].data.name;
    //                 this.reload();
    //             }.bind(this)
    //         };
    //         var selector = new MWF.OrgSelector(this.app.content, options);
    //     }.bind(this));
    // },
    selectedFilterApplication: function(type){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "application",
                "count": 1,
                "title": this.app.lp.monthly.selectSortApplication,
                "onComplete": function(items){
                    this.clearFilter();
                    this.filter.application = items[0].data.id;
                    this.filter.applicationName = items[0].data.name;
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    selectedFilterProcess: function(type){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var options = {
                "type": "process",
                "count": 1,
                "title": this.app.lp.monthly.selectSortProcess,
                "onComplete": function(items){
                    this.clearFilter();
                    this.filter.process = items[0].data.id;
                    this.filter.processName = items[0].data.name;
                    this.reload();
                }.bind(this)
            };
            var selector = new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    createFilterNode: function(){
        this.filterActionNode = new Element("div", {"styles": this.css.filterActionNode}).inject(this.filterAreaNode);
        this.filterActionTitleNode = new Element("div", {"styles": this.css.filterActionTitleNode}).inject(this.filterActionNode);
        this.filterActionTextNode = new Element("div", {"styles": this.css.filterActionTextNode}).inject(this.filterActionNode);

        this.filterActionTitleNode.set("text", this.app.lp.monthly.filter);
        this.filterActionTextNode.set("html", this.getFilterHtml());
        this.selectFilter();
    },
    getFilterHtml: function(){
        var textList = [];
        if (this.filter.unit) textList.push(this.app.lp.monthly.filterInfor.unit.replace(/{name}/g, "<span style='color: #ff0000'>"+MWF.name.cn(this.filter.unit)+"</span>"));
        // if (this.filter.company) textList.push(this.app.lp.monthly.filterInfor.company.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.company+"</span>"));
        // if (this.filter.department) textList.push(this.app.lp.monthly.filterInfor.department.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.department+"</span>"));
        if (this.filter.personName) textList.push(this.app.lp.monthly.filterInfor.person.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.person+"</span>"));
        if (this.filter.applicationName) textList.push(this.app.lp.monthly.filterInfor.application.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.applicationName+"</span>"));
        if (this.filter.processName) textList.push(this.app.lp.monthly.filterInfor.process.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.processName+"</span>"));
        if (this.filter.activityName) textList.push(this.app.lp.monthly.filterInfor.activity.replace(/{name}/g, "<span style='color: #ff0000'>"+this.filter.activityName+"</span>"));

        if (textList.length) return textList.join(", ");
        return this.app.lp.monthly.filterInfor.all;
    },

    loadData: function(callback){
        this.actions.loadMonthly("task" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
        //this.actions.loadMonthlyByApplication("task" ,this.filter.company, this.filter.department, this.filter.person, function(json){
        //    this.data = json.data;
        //    if (callback) callback();
        //}.bind(this));
    },

    loadTable: function(){
        this.createTable();
    },
    createTable: function(){
        this.table = new MWF.xApplication.BAM.monthly.MonthlyContent.Table(this, this.tableAreaNode, this.data);
    },
    getSelectedData: function(){
        if (this.table.selectedData.length) return this.table.selectedData;
        var data = [];
        Object.each(this.data, function(v, k){
            var count = 0;
            v.each(function(o){count+=o.count;}.bind(this));
            var o = {
                "column": k,
                "data":[{"name": "all", "count": count}]
            };
            data.push(o);
        }.bind(this));
        return data;
    },
    loadChart: function(){
        this.chart = new MWF.xApplication.BAM.monthly.MonthlyContent.Chart(this, this.chartAreaNode, this.getSelectedData());
    },
    reloadChart: function(){
        if (this.chart) this.chart.destroy();
        this.chartAreaNode.empty();
        this.chart = new MWF.xApplication.BAM.monthly.MonthlyContent.Chart(this, this.chartAreaNode, this.getSelectedData());
    },

    show: function(){
        this.node.setStyle("display", "block");
    },
    hide: function(){
        this.node.setStyle("display", "none");
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table = new Class({
    initialize: function(content, node, data){
        this.content = content;
        this.monthly = this.content.monthly;
        this.app = this.monthly.app;
        this.actions = this.app.actions;
        this.css = this.content.css;
        this.container = $(node);
        this.data = data;
        this.rowSelectors = [];
        this.colSelectors = [];
        this.tableData = {
            "cols": [],
            "rows": [],
            "data": [],
            "cells": []
        };
        this.selectedRows = [];
        this.selectedCols = [];
        this.selectedData = [];
        this.load();
    },
    load: function(){
        this.table = new Element("table", {
            "styles": this.css.monthlyTable,
            "cellPadding": "0",
            "cellSpacing": "0",
            "border": "0"
        });
        this.getTableData();
        this.createColSelectRow();
        this.createTitleRow();
        this.createDataRow();
        this.table.inject(this.container);
    },
    getTableData: function(){
        var colData = [];
        var rowData = [];
        var data = [];
        var cells = [];
        var i=0;
        Object.each(this.data, function(v, k){
            colData.push(k);
            data.push([]);
            cells.push([]);
            v.each(function(o){
                if (!rowData.filter(function(x){return x.value==o.value;}).length){
                    rowData.push(o);
                }
                data[i].push(o.count);
                cells[i].push(null);
            }.bind(this));
            i++
        }.bind(this));
        this.tableData = {
            "cols": colData,
            "rows": rowData,
            "data": data,
            "cells": cells
        };
    },

    createColSelectRow: function(){
        this.selectColTr = this.table.insertRow();
        var td = this.selectColTr.insertCell().setStyles(this.css.monthlyTableAllSelectTd).set("title", this.app.lp.monthly.selecteAll);
        new MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllSelector(this, td);

        td = this.selectColTr.insertCell().setStyles(this.css.monthlyTableAllColSelectTd).set("title", this.app.lp.monthly.selecteAllCol);
        new MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllColSelector(this, td);

        this.tableData.cols.each(function(n, i){
            td = this.selectColTr.insertCell().setStyles(this.css.monthlyTableColSelectTd);
            this.setColSelectTdEvent(td, i);
        }.bind(this));
    },
    createTitleRow: function(){
        var title = this.app.lp.monthly.columnTitle[this.content.sort.type];
        this.titleTr = this.table.insertRow();
        td = this.titleTr.insertCell().setStyles(this.css.monthlyTableAllRowSelectTd).set("title", this.app.lp.monthly.selecteAllRow);
        new MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllRowSelector(this, td);

        td = this.titleTr.insertCell().setStyles(this.css.monthlyTableTitleTd);
        td.set("text", title);

        _self = this;
        this.tableData.cols.each(function(n, i){
            td = this.titleTr.insertCell().setStyles(this.css.monthlyTableTitleTd);
            td.set("text", n);



            td.store("idx", i);
            td.addEvent("click", function(){
                var idx = this.retrieve("idx");
                _self.colSelectors[idx].td.click();
            });
        }.bind(this));
    },
    createDataRow: function(){
        _self = this;
        this.tableData.rows.each(function(r, ri){
            var tr = this.table.insertRow();
            var td = tr.insertCell().setStyles(this.css.monthlyTableRowSelectTd);
            this.setRowSelectTdEvent(td, ri);
            td = tr.insertCell().setStyles(this.css.monthlyTableTitleTd).set("text", r.name);
            td.store("idx", ri);
            td.addEvent("click", function(){
                var idx = this.retrieve("idx");
                _self.rowSelectors[idx].td.click();
            });

            this.tableData.cols.each(function(c, ci){
                td = tr.insertCell().setStyles(this.css.monthlyTableTd);
                td.set("text", this.tableData.data[ci][ri]);
                this.tableData.cells[ci][ri] = td;
                new MWF.xApplication.BAM.monthly.MonthlyContent.Table.CellSelector(this, td);

                //this.setDrag(td);
            }.bind(this));
        }.bind(this));
    },

    setColSelectTdEvent: function(td, i){
        this.colSelectors.push(new MWF.xApplication.BAM.monthly.MonthlyContent.Table.ColSelector(this, td, i));
    },
    setRowSelectTdEvent: function(td, i){
        this.rowSelectors.push(new MWF.xApplication.BAM.monthly.MonthlyContent.Table.RowSelector(this, td, i));
    },
    destroy: function(){
        this.container.empty();
        MWF.release(this);
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.ColSelector = new Class({
    initialize: function(table, td, i){
        this.table = table;
        this.td = td;
        this.idx = i;
        this.load();
    },
    load: function(){
        this.td.addEvents({
            "click": function(){
                if (this.table.selectedCols.indexOf(this.idx)!=-1){
                    this.unselectedCol();
                }else{
                    this.selectedCol();
                }
            }.bind(this)
        });
    },
    unselectedCol: function(){
        this.td.setStyles(this.table.css.monthlyTableSelectTd);
        this.table.selectedCols.erase(this.idx);
        this.checkSelectedCells();
    },
    selectedCol: function(){
        this.td.setStyles(this.table.css.monthlyTableSelectedTd);
        this.table.selectedCols.push(this.idx);
        this.checkSelectedCells();

    },
    checkIsSelected: function(ci, ri){
        if (!this.table.selectedCols.length && !this.table.selectedRows.length) return false;
        var colSelect = (!this.table.selectedCols.length) || this.table.selectedCols.indexOf(ci)!=-1;
        var rowSelect = (!this.table.selectedRows.length) || this.table.selectedRows.indexOf(ri)!=-1;
        return (colSelect && rowSelect);
    },

    checkSelectedCells: function(){
        this.table.selectedData =[];
        this.table.tableData.cells.each(function(cells, ci){
            var o = {
                "column": this.table.tableData.cols[ci],
                "data": []
            };
            cells.each(function(cell, ri){
                if (this.checkIsSelected(ci, ri)){
                    cell.setStyles(this.table.css.monthlyTableTd_selected);
                    o.data.push({
                        "name": this.table.tableData.rows[ri].name,
                        "value": this.table.tableData.rows[ri].value,
                        "count": this.table.tableData.data[ci][ri]
                    });
                }else{
                    cell.setStyles(this.table.css.monthlyTableTd);
                }
            }.bind(this));
            if (o.data.length) this.table.selectedData.push(o);
        }.bind(this));
        this.table.content.reloadChart();
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.RowSelector = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent.Table.ColSelector,
    load: function(){
        this.td.addEvents({
            "click": function(){
                if (this.table.selectedRows.indexOf(this.idx)!=-1){
                    this.unselectedCol();
                }else{
                    this.selectedCol();
                }
            }.bind(this)
        });
    },
    unselectedCol: function(){
        this.td.setStyles(this.table.css.monthlyTableSelectTd);
        this.table.selectedRows.erase(this.idx);
        this.checkSelectedCells();
    },
    selectedCol: function(){
        this.td.setStyles(this.table.css.monthlyTableSelectedTd);
        this.table.selectedRows.push(this.idx);
        this.checkSelectedCells();

    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllSelector = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent.Table.ColSelector,
    load: function(){
        this.td.addEvents({
            "click": function(){
                this.selectedAll();
            }.bind(this)
        });
    },
    selectedAll: function(){
        if (this.table.selectedRows.length || this.table.selectedCols.length){
            this.table.selectedRows = [];
            this.table.selectedCols = [];
            this.table.rowSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectTd);
            });
            this.table.colSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectTd);
            });
        }else{
            this.table.tableData.rows.each(function(r, i){
                if (i<20) this.table.selectedRows.push(i);
            }.bind(this));
            this.table.tableData.cols.each(function(c, i){
                if (i<20) this.table.selectedCols.push(i);
            }.bind(this));
            this.table.rowSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectedTd);
            });
            this.table.colSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectedTd);
            });
        }
        this.checkSelectedCells();
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllColSelector = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllSelector,
    selectedAll: function(){
        if (this.table.selectedCols.length){
            this.table.selectedCols = [];
            this.table.colSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectTd);
            });
        }else{
            this.table.tableData.cols.each(function(c, i){
                if (i<20) this.table.selectedCols.push(i);
            }.bind(this));
            this.table.colSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectedTd);
            });
        }
        this.checkSelectedCells();
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllRowSelector = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent.Table.AllSelector,
    selectedAll: function(){
        if (this.table.selectedRows.length){
            this.table.selectedRows = [];
            this.table.rowSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectTd);
            });
        }else{
            this.table.tableData.rows.each(function(c, i){
                if (i<20) this.table.selectedRows.push(i);
            }.bind(this));
            this.table.rowSelectors.each(function(sel){
                sel.td.setStyles(sel.table.css.monthlyTableSelectedTd);
            });
        }
        this.checkSelectedCells();
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Table.CellSelector = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent.Table.ColSelector,
    load: function(){
        new Drag(this.td, {
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
        this.td.addEvent("click", function(){
            var cellIndex = this.td.cellIndex-2;
            var rowIndex = this.td.getParent("tr").rowIndex-2;
            if (this.checkIsSelected(cellIndex, rowIndex)){

            }else{
                this.table.selectedCols.each(function(i){
                    this.table.colSelectors[i].td.setStyles(this.table.css.monthlyTableSelectTd);
                }.bind(this));
                this.table.selectedRows.each(function(i){
                    this.table.rowSelectors[i].td.setStyles(this.table.css.monthlyTableSelectTd);
                }.bind(this));
                this.table.selectedRows = [];
                this.table.selectedCols = [];
                this.checkSelectedCells();
                this.table.content.reloadChart();
            }
        }.bind(this));
    },
    cellDragStart: function(td, e){
        var p = this.td.getPosition();
        var size = this.td.getSize();
        this.td.store("start", {"x": p.x, "y": p.y});
        this.td.store("start2", {"x": p.x+size.x, "y": p.y+size.y});

        //this.td.store("eStart", {"x": e.page.x, "y": e.page.y});
        //this.boxNode = new Element("div", {"styles": this.table.css.selectBox}).inject(this.table.app.content);
        //var offp = this.table.app.content .getPosition();
        //x = e.page.x-offp.x;
        //y = e.page.y-offp.y;
        //this.boxNode.setStyles({
        //    "top": ""+y+"px",
        //    "left": ""+x+"px"
        //});
    },
    getSelectedCells: function(start, e){
        var ox = e.page.x-start.x;
        var oy = e.page.y-start.y;
        var tdSize = this.td.getSize();

        var cols = (ox/tdSize.x).toInt();
        var rows = (oy/tdSize.y).toInt();
        return {"cols": cols, "rows": rows};
    },
    cellDrag: function(td, e){
        this.table.selectedRows = [];
        this.table.selectedCols = [];
        this.table.selectedData = [];

        var start = this.td.retrieve("start");
        var start2 = this.td.retrieve("start2");
        var c1 = this.getSelectedCells(start, e);
        var c2 = this.getSelectedCells(start2, e);

        var cols = (Math.abs(c1.cols)>Math.abs(c2.cols)) ? c1.cols : c2.cols;
        var rows = (Math.abs(c1.rows)>Math.abs(c2.rows)) ? c1.rows : c2.rows;

        var cellIndex = this.td.cellIndex-2;
        var rowIndex = this.td.getParent("tr").rowIndex-2;
        if (!cellIndex || cellIndex<0) cellIndex = 0;
        if (!rowIndex || rowIndex<0) rowIndex = 0;

        var toCellIndex = cellIndex+cols;
        var toRowIndex = rowIndex+rows;

        if (toRowIndex>rowIndex){
            for (var i=rowIndex; i<=toRowIndex; i++) this.table.selectedRows.push(i);
        }else{
            for (var i=toRowIndex; i<=rowIndex; i++) this.table.selectedRows.push(i);
        }
        if (toCellIndex>cellIndex){
            for (var i=cellIndex; i<=toCellIndex; i++) this.table.selectedCols.push(i);
        }else{
            for (var i=toCellIndex; i<=cellIndex; i++) this.table.selectedCols.push(i);
        }

        //var eStart = this.td.retrieve("eStart");
        //this.boxNode.setStyles({
        //    "width": ""+ox+"px",
        //    "height": ""+oy+"px"
        //});

        this.checkSelectedCells();
    },
    completeDrag: function(td, e){
        this.table.selectedCols.each(function(i){
            this.table.colSelectors[i].td.setStyles(this.table.css.monthlyTableSelectedTd);
        }.bind(this));
        this.table.selectedRows.each(function(i){
            this.table.rowSelectors[i].td.setStyles(this.table.css.monthlyTableSelectedTd);
        }.bind(this));
        //this.boxNode.destroy();
        //this.boxNode = null;
        this.table.content.reloadChart();
    },

    checkSelectedCells: function(){
        this.table.selectedData =[];
        this.table.tableData.cells.each(function(cells, ci){
            var o = {
                "column": this.table.tableData.cols[ci],
                "data": []
            };
            cells.each(function(cell, ri){
                if (this.checkIsSelected(ci, ri)){
                    cell.setStyles(this.table.css.monthlyTableTd_selected);
                    o.data.push({
                        "name": this.table.tableData.rows[ri].name,
                        "value": this.table.tableData.rows[ri].value,
                        "count": this.table.tableData.data[ci][ri]
                    });
                }else{
                    cell.setStyles(this.table.css.monthlyTableTd);
                }
            }.bind(this));
            if (o.data.length) this.table.selectedData.push(o);
        }.bind(this));
        //this.table.content.reloadChart();
    }
});

MWF.xApplication.BAM.monthly.MonthlyContent.Chart = new Class({
    initialize: function(content, node, data){
        this.content = content;
        this.monthly = this.content.monthly;
        this.app = this.monthly.app;
        this.css = this.content.css;
        this.container = $(node);
        this.data = data;
        this.load();
    },
    load: function(){
        this.chartFlagNode = new Element("div", {"styles": this.css.monthlyChartFlagAreaNode}).inject(this.container);
        this.chartNode = new Element("div", {"styles": this.css.monthlyChartNode}).inject(this.container);
        this.loadChart();
    },
    loadChart: function(){
        MWF.require("MWF.widget.chart.Bar", function(){
            this.flag = [];
            this.bar = new MWF.widget.chart.Bar(this.chartNode, this.data, "column", {"delay": 0, "style": "monthly"});
            this.data[0].data.each(function(d, i){
                this.flag.push({"name":d.name, "color": this.bar.colors[i]});
                this.bar.addBar(function(v){return v.data[i].count});
            }.bind(this));
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
            this.loadFlags();
        }.bind(this));
    },
    loadFlags: function(){
        this.flag.each(function(f, i){
            this.loadFlag(f, i);
        }.bind(this));
    },
    loadFlag: function(f, i){
        var flagNode = new Element("div", {"styles": this.css.monthlyChartFlagNode}).inject(this.chartFlagNode);
        var flagColorNode = new Element("div", {"styles": this.css.monthlyChartFlagColorNode}).inject(flagNode);
        flagColorNode.setStyle("background-color", f.color);
        var flagNameNode = new Element("div", {"styles": this.css.monthlyChartFlagNameNode}).inject(flagNode);
        flagNameNode.set("text", f.name);
        flagNameNode.set("title", f.name);

        flagNode.store("idx", i);
        flagNode.store("barColor", f.color);
        var _self = this;
        flagNode.addEvents({
            "mouseover": function(){
                this.getFirst().setStyles(_self.css.monthlyChartFlagColorNode_over);
                var idx = this.retrieve("idx");
                _self.highlightBar(idx);
            },
            "mouseout": function(){
                this.getFirst().setStyles(_self.css.monthlyChartFlagColorNode);
                var idx = this.retrieve("idx");
                var barColor = this.retrieve("barColor");
                _self.unHighlightBar(idx, barColor);
            }
        });
    },
    createHighlightDefs: function(id){
        //this.defssvg = this.bar.createDefs;
        var node = this.bar.svg.append("defs");
        var data = this.bar.css["rect_over_defs"];
        this.createDefs(node, data);
        node.select(function(){ return this.getFirst(); }).attr("id", id);
        node.attr("id", "defs_"+id);
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

    recreateBars: function(i){
        var data = this.data.map(function(d, idx) {
            return {"name": d["column"], "data": d.data[i].count};
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
    destroy: function(){
        this.container.empty();
        this.bar.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.BAM.monthly.MonthlyContent.Filter = new Class({
    Implements: [Options, Events],
    initialize: function (content, node) {
        this.content = content;
        this.monthly = this.content.monthly;
        this.app = this.monthly.app;
        this.css = this.content.css;
        this.sort = this.content.sort;
        this.filter = this.content.filter;
        this.node = $(node);
        this.load();
    },
    load: function(){
        this.node.addEvent("cilck", function(){
            this.showFilterMenu();
        }.bind(this));
    },
    showFilterMenu: function(){
        this.node = new Element("div", {"styles": this.css.filterAreaNode});
        this.conditionNode = new Element("div", {"styles": this.css.filterAreaConditionNode}).inject(this.node);
        this.actionNode = new Element("div", {"styles": this.css.filterAreaActionNode}).inject(this.node);
        this.createAction();
    },
    createAction: function(){
        this.okAction = new Element("div", {"styles": this.css.filterAreaOkActionNode}).inject(this.actionNode);
        this.cancelAction = new Element("div", {"styles": this.css.filterAreaCancelActionNode}).inject(this.actionNode);
    }
});

MWF.xApplication.BAM.monthly.MonthlyTaskContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent
});
MWF.xApplication.BAM.monthly.MonthlyTaskCompletedContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent,
    loadData: function(callback){
        this.actions.loadMonthly("taskCompleted" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    }
});
MWF.xApplication.BAM.monthly.MonthlyWorkContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent,
    loadData: function(callback){
        this.actions.loadMonthly("work" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    }
});
MWF.xApplication.BAM.monthly.MonthlyWorkCompletedContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent,
    loadData: function(callback){
        this.actions.loadMonthly("workCompleted" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    }
});
MWF.xApplication.BAM.monthly.MonthlyTaskExpiredContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent,
    loadData: function(callback){
        this.actions.loadMonthly("taskExpired" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    }
});
MWF.xApplication.BAM.monthly.MonthlyWorkExpiredContent = new Class({
    Extends: MWF.xApplication.BAM.monthly.MonthlyContent,
    loadData: function(callback){
        this.actions.loadMonthly("workExpired" ,this.sort, this.filter, function(json){
            this.data = json.data;
            if (callback) callback();
        }.bind(this));
    }
});
