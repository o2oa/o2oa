MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);
MWF.xApplication.query.Query.Statement = MWF.QStatement = new Class({
    Extends: MWF.QViewer,
    options: {
        "lazy": false,
        "moduleEvents": ["queryLoad", "postLoad", "postLoadPageData", "postLoadPage", "selectRow", "unselectRow",
            "queryLoadItemRow", "postLoadItemRow", "queryLoadCategoryRow", "postLoadCategoryRow", "export", "exportRow"]
    },
    initialize: function (container, json, options, app, parentMacro) {
        //本类有三种事件，
        //一种是通过 options 传进来的事件，包括 loadView、openDocument、select
        //一种是用户配置的 事件， 在this.options.moduleEvents 中定义的作为类事件
        //还有一种也是用户配置的事件，不在this.options.moduleEvents 中定义的作为 this.node 的DOM事件

        this.setOptions(options);

        this.path = "../x_component_query_Query/$Viewer/";
        this.cssPath = "../x_component_query_Query/$Viewer/" + this.options.style + "/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.app = app;

        this.container = $(container);

        this.json = json || {};

        this.parentMacro = parentMacro;

        this.originalJson = Object.clone(json);

        this.viewJson = null;
        this.filterItems = [];
        this.searchStatus = "none"; //none, custom, default


        this.items = [];
        this.selectedItems = [];
        this.hideColumns = [];
        this.openColumns = [];
        this.parameter = {};

        this.gridJson = null;

        if (this.options.isload) {
            this.init(function () {
                this.load();
            }.bind(this));
        }

    },
    init: function (callback) {
        if (this.json.view) {
            this.viewJson = JSON.decode(this.json.view);
            this.statementJson = this.json;
            this.statementJson.viewJson = this.viewJson;
            if (callback) callback();
        } else {
            this.getView(callback);
        }
    },
    loadMacro: function (callback) {
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro.ViewContext(this);
            if (callback) callback();
        }.bind(this));
    },
    createActionbarNode: function () {
        this.actionbarAreaNode.empty();
        if (typeOf(this.json.showActionbar) === "boolean" && this.json.showActionbar !== true) return;
        if (typeOf(this.viewJson.actionbarHidden) === "boolean") {
            if (this.viewJson.actionbarHidden === true || !this.viewJson.actionbarList || !this.viewJson.actionbarList.length) return;
            this.actionbar = new MWF.xApplication.query.Query.Statement.Actionbar(this.actionbarAreaNode, this.viewJson.actionbarList[0], this, {});
            this.actionbar.load();
        }
    },
    _loadPageNode: function () {
        this.viewPageAreaNode.empty();
        if (!this.paging) {
            var json;
            if (!this.viewJson.pagingList || !this.viewJson.pagingList.length) {
                json = {
                    "firstPageText": this.lp.firstPage,
                    "lastPageText": this.lp.lastPage
                };
            } else {
                json = this.viewJson.pagingList[0];
            }
            this.paging = new MWF.xApplication.query.Query.Statement.Paging(this.viewPageAreaNode, json, this, {});
            this.paging.load();
        } else {
            this.paging.reload();
        }
    },
    // _initPage: function(){
    //     var i = this.count/this.json.pageSize;
    //     this.pages = (i.toInt()<i) ? i.toInt()+1 : i;
    //     this.currentPage = this.options.defaultPage || 1;
    //     this.options.defaultPage = null;
    // },
    lookup: function (data, callback) {
        if (this.lookuping) return;
        this.lookuping = true;
        // this.getLookupAction(function(){
        //     if (this.json.application){

        var d = Object.clone( data || {} );
        // d.count = this.json.count;
        // this.lookupAction.bundleView(this.json.id, d, function(json){
        //     this.bundleItems = json.data.valueList;

        // this._initPage();

        debugger;
        this.loadParameter(d);
        this.loadFilter(d);

        this.currentPage = this.options.defaultPage || 1;
        this.options.defaultPage = null;

        if (this.noDataTextNode) this.noDataTextNode.destroy();
        this.loadCurrentPageData(function (json) {
            if (this.count || (json.data && json.data.length )) {
                this.fireEvent("postLoad"); //用户配置的事件
                this.lookuping = false;
                if (callback) callback(this);
            } else {
                this.viewPageAreaNode.empty();
                if (this.viewJson.noDataText) {
                    var noDataTextNodeStyle = this.css.noDataTextNode;
                    if (this.viewJson.viewStyles && this.viewJson.viewStyles["noDataTextNode"]) {
                        noDataTextNodeStyle = this.viewJson.viewStyles["noDataTextNode"];
                    }
                    this.noDataTextNode = new Element("div", {
                        "styles": noDataTextNodeStyle,
                        "text": this.viewJson.noDataText
                    }).inject(this.contentAreaNode);
                }
                // if (this.loadingAreaNode){
                //     this.loadingAreaNode.destroy();
                //     this.loadingAreaNode = null;
                // }
                this.fireEvent("postLoad"); //用户配置的事件
                this.lookuping = false;
                if (callback) callback(this);
            }


        }.bind(this), true, "all");

        // }.bind(this));
        // }
        // }.bind(this));
    },
    loadFilter: function (data) {
        debugger;
        this.filterList = [];
        (data.filterList || []).each(function (d) {
            var parameterName = d.path.replace(/\./g, "_");
            var value = d.value;
            // if( d.code && d.code.code ){
            //     value = this.Macro.exec( d.code.code, this);
            // }
            if (d.comparison === "like" || d.comparison === "notLike") {
                if (value.substr(0, 1) !== "%") value = "%" + value;
                if (value.substr(value.length - 1, 1) !== "%") value = value + "%";
                this.parameter[parameterName] = value; //"%"+value+"%";
            } else {
                if (d.formatType === "dateTimeValue" || d.formatType === "datetimeValue") {
                    value = "{ts '" + value + "'}"
                } else if (d.formatType === "dateValue") {
                    value = "{d '" + value + "'}"
                } else if (d.formatType === "timeValue") {
                    value = "{t '" + value + "'}"
                } else if (d.formatType === "numberValue"){
                    value = parseFloat(value);
                }
                this.parameter[parameterName] = value;
            }
            d.value = parameterName;

            if( !d.logic )d.logic = "and";

            this.filterList.push(d);
        }.bind(this))
    },
    loadParameter: function () {
        this.parameter = {};
        debugger;
        var parameter = this.json.parameter ? Object.clone(this.json.parameter) : {};
        //系统默认的参数
        (this.viewJson.parameterList || []).each(function (f) {
            var value = f.value;
            if (parameter && parameter[f.parameter]) {
                value = parameter[f.parameter];
                delete parameter[f.parameter];
            }
            if (typeOf(value) === "date") {
                value = value.format("db");
            }
            if (f.valueType === "script") {
                value = this.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
            } else {
                var user = layout.user;
                switch (f.value) {
                    case "@person":
                        value = user.distinguishedName;
                        break;
                    case "@identityList":
                        value = user.identityList.map(function (d) {
                            return d.distinguishedName;
                        });
                        break;
                    case "@unitList":
                        o2.Actions.load("x_organization_assemble_express").UnitAction.listWithPerson({"personList": [user.distinguishedName]}, function (json) {
                            value = json.unitList;
                        }, null, false);
                        break;
                    case "@unitAllList":
                        o2.Actions.load("x_organization_assemble_express").UnitAction.listWithIdentitySupNested({"personList": [user.distinguishedName]}, function (json) {
                            value = json.unitList;
                        }, null, false);
                        break;
                    case "@year":
                        value = (new Date().getFullYear()).toString();
                        break;
                    case "@season":
                        var m = new Date().format("%m");
                        if (["01", "02", "03"].contains(m)) {
                            value = "1"
                        } else if (["04", "05", "06"].contains(m)) {
                            value = "2"
                        } else if (["07", "08", "09"].contains(m)) {
                            value = "3"
                        } else {
                            value = "4"
                        }
                        break;
                    case "@month":
                        value = new Date().format("%Y-%m");
                        break;
                    case "@time":
                        value = new Date().format("db");
                        break;
                    case "@date":
                        value = new Date().format("%Y-%m-%d");
                        break;
                    default:
                }
            }
            if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
                value = "{ts '" + value + "'}"
            } else if (f.formatType === "dateValue") {
                value = "{d '" + value + "'}"
            } else if (f.formatType === "timeValue") {
                value = "{t '" + value + "'}"
            }
            this.parameter[f.parameter] = value;
        }.bind(this));
        //传入的参数
        for (var p in parameter) {
            var value = parameter[p];
            if (typeOf(value) === "date") {
                value = "{ts '" + value.format("db") + "'}"
            }
            this.parameter[p] = value;
        }
    },
    loadCurrentPageData: function (callback, async, type) {
        //是否需要在翻页的时候清空之前的items ?

        debugger;

        if (this.pageloading) return;
        this.pageloading = true;

        if( this.io ){
            this.items.each(function(item){
                this.io.unobserve(item.node);
            }.bind(this))
        }

        this.items = [];

        var p = this.currentPage;
        var d = {
            "filterList": this.filterList,
            "parameter": this.parameter
        };

        while (this.viewTable.rows.length > 1) {
            this.viewTable.deleteRow(-1);
        }
        if( this.viewTable.rows.length>0 && !this.viewTable.rows[0].hasClass("viewTitleLine") ){
            this.viewTable.deleteRow(0);
        }

        this.contentAreaNode.scrollTo(0, 0);

        //this.createLoadding();

        this.loadViewRes = o2.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            this.options.statementId || this.options.statementName || this.json.statementId || this.json.statementName,
            type || "data", p, this.json.pageSize, d, function (json) {

                if (type === "all" || type === "count") {
                    if (typeOf(json.count) === "number") {
                        this.count = json.count;
                        var i = this.count / this.json.pageSize;
                        this.pages = (i.toInt() < i) ? i.toInt() + 1 : i;
                    }
                }

                this.gridJson = json.data;
                this.setSelectedableFlag();

                this.fireEvent("postLoadPageData");

                // if (this.viewJson.group.column){
                //     this.gridJson = json.data.groupGrid;
                // }else{
                //     this.gridJson = json.data.grid;
                this.loadData();
                // }
                if (this.gridJson.length) this._loadPageNode();
                if (this.loadingAreaNode) {
                    this.loadingAreaNode.destroy();
                    this.loadingAreaNode = null;
                }

                this.pageloading = false;

                this.fireEvent("loadView"); //options 传入的事件
                this.fireEvent("postLoadPage");

                if (callback) callback(json);
            }.bind(this), null, async === false ? false : true);
    },
    getView: function (callback) {
        this.getViewRes = o2.Actions.load("x_query_assemble_surface").StatementAction.get(this.json.statementId || this.json.statementName, function (json) {
            debugger;
            var viewData = JSON.decode(json.data.view);
            if (!this.json.pageSize) this.json.pageSize = viewData.pageSize || "20";
            this.viewJson = viewData.data;
            this.json.application = json.data.query;
            //this.json = Object.merge(this.json, json.data);
            this.statementJson = json.data;
            this.statementJson.viewJson = this.viewJson;
            if (callback) callback();
        }.bind(this));
    },

    loadData: function () {
        if (this.getSelectFlag() === "multi" && this.viewJson.allowSelectAll) {
            if (this.selectTitleCell && this.selectTitleCell.retrieve("selectAllLoaded")) {
                this.setUnSelectAllStyle();
            } else {
                this.createSelectAllNode();
            }
        } else if (this.selectAllNode) {
            this.clearSelectAllStyle();
        }

        if (this.gridJson.length) {
            // if( !this.options.paging ){
            this.gridJson.each(function (line, i) {
                this.items.push(new MWF.xApplication.query.Query.Statement.Item(this, line, null, i, null, this.options.lazy));
            }.bind(this));
            // }else{
            //     this.loadPaging();
            // }
        } else {
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    loadDataByPaging: function () {
        if (this.isItemsLoading) return;
        if (!this.isItemsLoaded) {
            var from = Math.min(this.pageNumber * this.options.perPageCount, this.gridJson.length);
            var to = Math.min((this.pageNumber + 1) * this.options.perPageCount + 1, this.gridJson.length);
            this.isItemsLoading = true;
            for (var i = from; i < to; i++) {
                this.items.push(new MWF.xApplication.query.Query.Statement.Item(this, this.gridJson[i], null, i, null, this.options.lazy));
            }
            this.isItemsLoading = false;
            this.pageNumber++;
            if (to == this.gridJson.length) this.isItemsLoaded = true;
        }
    },
    getFilter: function () {
        var filterData = [];
        if (this.searchStatus === "custom") {
            if (this.filterItems.length) {
                this.filterItems.each(function (filter) {
                    if( !filter.data.logic )filter.data.logic = "and";
                    filterData.push(filter.data);
                }.bind(this));
            }
        }
        if (this.searchStatus === "default") {
            var key = this.viewSearchInputNode.get("value");
            if (key && key !== this.lp.searchKeywork) {
                this.viewJson.customFilterList.each(function (entry) {
                    if (entry.formatType === "textValue") {
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                    }
                    if (entry.formatType === "numberValue") {
                        var v = key.toFloat();
                        if (!isNaN(v)) {
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "like"
                            };
                            filterData.push(d);
                        }
                    }
                }.bind(this));
            }
        }
        return (filterData.length) ? filterData : null;
    },
    viewSearchCustomAddToFilter: function () {
        var pathIdx = this.viewSearchCustomPathListNode.selectedIndex;
        var comparisonIdx = this.viewSearchCustomComparisonListNode.selectedIndex;
        if (pathIdx === -1) {
            MWF.xDesktop.notice("error", {
                "x": "left",
                "y": "top"
            }, this.lp.filterErrorTitle, this.viewSearchCustomPathListNode, {"x": 0, "y": 85});
            return false;
        }
        if (comparisonIdx === -1) {
            MWF.xDesktop.notice("error", {
                "x": "left",
                "y": "top"
            }, this.lp.filterErrorComparison, this.viewSearchCustomComparisonListNode, {"x": 0, "y": 85});
            return false;
        }
        var pathOption = this.viewSearchCustomPathListNode.options[pathIdx];
        var entry = pathOption.retrieve("entry");
        if (entry) {
            var pathTitle = entry.title;
            var path = entry.path;
            var comparison = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("value");
            var comparisonTitle = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("text");
            var value = "";

            if (entry.valueType === "script" && entry.valueScript && entry.valueScript.code) {
                var idx = this.viewSearchCustomValueNode.selectedIndex;
                if (idx !== -1) {
                    var v = this.viewSearchCustomValueNode.options[idx].get("value");
                    value = entry.formatType === "booleanValue" ? (v === "true") : v;
                }
            } else {
                switch (entry.formatType) {
                    case "numberValue":
                        value = this.viewSearchCustomValueNode.get("value");
                        break;
                    case "dateTimeValue":
                        value = this.viewSearchCustomValueNode.get("value");
                        break;
                    case "booleanValue":
                        var idx = this.viewSearchCustomValueNode.selectedIndex;
                        if (idx !== -1) {
                            var v = this.viewSearchCustomValueNode.options[idx].get("value");
                            value = (v === "true");
                        }
                        break;
                    default:
                        value = this.viewSearchCustomValueNode.get("value");
                }
            }

            if (value === "") {
                MWF.xDesktop.notice("error", {
                    "x": "left",
                    "y": "top"
                }, this.lp.filterErrorValue, this.viewSearchCustomValueContentNode, {"x": 0, "y": 85});
                return false;
            }

            this.filterItems.push(new MWF.xApplication.query.Query.Statement.Filter(this, {
                "logic": "and",
                "path": path,
                "title": pathTitle,
                "comparison": comparison,
                "comparisonTitle": comparisonTitle,
                "value": value,
                "formatType": (entry.formatType == "datetimeValue") ? "dateTimeValue" : entry.formatType
            }, this.viewSearchCustomFilterContentNode));

            this.searchCustomView();
        }
    },
    //搜索相关结束
    getStatementInfor: function () {
        debugger;
        return this.statementJson;
    },
    getPageInfor: function () {
        return {
            pages: this.pages,
            perPageCount: this.json.pageSize,
            currentPageNumber: this.currentPage
        };
    },
    switchStatement: function (json) {
        this.switchView(json);
    },
    setFilter: function (filter, parameter, callback) {
        if (this.lookuping || this.pageloading) return;
        if (!filter) filter = [];
        if (!parameter) parameter = {};
        this.json.filter = filter;
        this.json.parameter = parameter;
        if (this.viewAreaNode) {
            this.createViewNode({"filterList": this.json.filter.clone()}, callback);
        }
    },


    // getExportTotalCount: function(){
    //     return this.count || 0;
    // },
    // getExportMaxCount: function(){
    //     return 2000;
    // },
    exportView: function(){

        // var excelName = this.statementJson.name + "(" + start + "-" + end + ").xlsx";

        var excelName = this.statementJson.name;

        var p = this.currentPage;
        var d = {
            "filterList": this.filterList,
            "parameter": this.parameter
        };

        this.createLoadding();

        debugger;

        var exportArray = [];

        var titleArray = [];
        var colWidthArr = [];
        var dateIndexArray = [];
        var numberIndexArray = [];
        var idx = 0;
        Object.each(this.entries, function (c, k) {
            if (this.hideColumns.indexOf(k) === -1 && c.exportEnable !== false) {
                titleArray.push(c.displayName);
                colWidthArr.push(c.exportWidth || 200);
                if( c.isTime )dateIndexArray.push(idx);
                if( c.isNumber )numberIndexArray.push(idx);
                idx++;
            }
        }.bind(this));
        exportArray.push(titleArray);

        o2.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            this.options.statementId || this.options.statementName || this.json.statementId || this.json.statementName,
            "data", 1, 100000, d, function (json) {

                json.data.each(function (d, i) {
                    var dataArray = [];
                    Object.each(this.entries, function (c, k) {
                        if (this.hideColumns.indexOf(k) === -1 && c.exportEnable !== false) {
                            var text = this.getExportText(c, k, d);
                            // if( c.isNumber && typeOf(text) === "string" && (parseFloat(text).toString() !== "NaN") ){
                            //     text = parseFloat(text);
                            // }
                            dataArray.push( text );
                        }
                    }.bind(this));
                    //exportRow事件
                    var argu = {"index":i, "source": d, "data":dataArray};
                    this.fireEvent("exportRow", [argu]);
                    exportArray.push( argu.data || dataArray );
                }.bind(this));

                //export事件
                var arg = {
                    data : exportArray,
                    colWidthArray : colWidthArr,
                    title : excelName
                };
                this.fireEvent("export", [arg]);

                if (this.loadingAreaNode) {
                    this.loadingAreaNode.destroy();
                    this.loadingAreaNode = null;
                }

                new MWF.xApplication.query.Query.Statement.ExcelUtils().exportToExcel(
                    arg.data || exportArray,
                    arg.title || excelName,
                    arg.colWidthArray || colWidthArr,
                    dateIndexArray,  //日期格式列下标
                    numberIndexArray  //数字格式列下标
                );

            }.bind(this));
    },
    getDataByPath: function (obj, path) {
        var pathList = path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else {
                obj = "";
                break;
            }
        }
        return obj
    },
    getExportText: function (c, k, data) {
        var path = c.path, code = c.code, obj = data;
        if (!path) {
            return ""
        } else if (path === "$all") {
        } else {
            obj = this.getDataByPath(obj, path);
        }

        try{
            if (code && code.trim()) obj = this.view.Macro.exec(code, {
                "value": obj,
                "data": data,
                "entry": c,
                "json": c
            });
        }catch (e) {}

        var toName = function (value) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    value[idx] = toName(v)
                })
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, key) {
                    value[key] = toName(v);
                })
            } else if (typeOf(value) === "string") {
                value = o2.name.cn(value)
            }
            return value;
        };

        var d;
        if (obj != undefined && obj != null) {
            if (typeOf(obj) === "array") {
                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
            } else if (typeOf(obj) === "object") {
                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
            } else {
                d = c.isName ? o2.name.cn(obj.toString()) : obj;
            }
        }
        return d;
    }
});

MWF.xApplication.query.Query.Statement.Item = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Item,
    initialize: function (view, data, prev, i, category, lazy) {
        this.view = view;
        this.data = data;
        this.dataString = JSON.stringify(data);
        this.css = this.view.css;
        this.isSelected = false;
        this.category = category;
        this.prev = prev;
        this.idx = i;
        this.clazzType = "item";
        this.lazy = lazy;
        this.load();
    },
    _load: function () {
        this.loading = true;

        if(!this.node)this.view.fireEvent("queryLoadItemRow", [null, this]);

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = (viewStyles && viewStyles["contentTd"]) ? viewStyles["contentTd"] : this.css.viewContentTdNode;

        if(!this.node)this.loadNode();

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
        this.selectTd.setStyles({"cursor": "pointer"});
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);

        //var selectFlag = this.view.json.select || this.view.viewJson.select ||  "none";
        var selectFlag = this.view.getSelectFlag();
        if (this.data.$selectedEnable && ["multi", "single"].contains(selectFlag) && this.view.viewJson.selectBoxShow === "always") {
            var viewStyles = this.view.viewJson.viewStyles;
            if (viewStyles) {
                if (selectFlag === "single") {
                    this.selectTd.setStyles(viewStyles["radioNode"]);
                } else {
                    this.selectTd.setStyles(viewStyles["checkboxNode"]);
                }
            } else {
                var iconName = "checkbox";
                if (selectFlag === "single") iconName = "radiobox";
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/" + iconName + ".png) center center no-repeat"});
            }
        }

        if (this.view.isSelectTdHidden()) {
            this.selectTd.hide();
        }
        //}

        //序号
        if (this.view.viewJson.isSequence === "yes") {
            this.sequenceTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyle("width", "10px");
            var s = 1 + this.view.json.pageSize * (this.view.currentPage - 1) + this.idx;
            this.sequenceTd.set("text", s);
        }

        debugger;

        Object.each(this.view.entries, function (c, k) {
            //if (cell){
            if (this.view.hideColumns.indexOf(k) === -1) {
                var td = new Element("td", {"styles": viewContentTdNode}).inject(this.node);

                var cell = this.getText(c, k, td); //this.data[k];
                if (cell === undefined || cell === null) cell = "";

                // if (k!== this.view.viewJson.group.column){
                var v = cell;
                if (c.isHtml) {
                    td.set("html", v);
                } else {
                    td.set("text", v);
                }

                if (typeOf(c.contentProperties) === "object") td.setProperties(c.contentProperties);
                if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                if (typeOf(c.contentStyles) === "object") td.setStyles(c.contentStyles);
                // }else{
                //     if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                // }

                if (this.view.openColumns.indexOf(k) !== -1) {
                    this.setOpenWork(td, c)
                }

                // if (k!== this.view.viewJson.group.column){
                Object.each(c.events || {}, function (e, key) {
                    if (e.code) {
                        if (key === "loadContent") {
                            this.view.Macro.fire(e.code,
                                {"node": td, "json": c, "data": v, "view": this.view, "row": this});
                        } else if (key !== "loadTitle") {
                            td.addEvent(key, function (event) {
                                return this.view.Macro.fire(
                                    e.code,
                                    {"node": td, "json": c, "data": v, "view": this.view, "row": this},
                                    event
                                );
                            }.bind(this));
                        }
                    }
                }.bind(this));
                // }
            }
            //}
        }.bind(this));

        if(this.placeholderTd){
            this.placeholderTd.destroy();
            this.placeholderTd = null;
        }

        //默认选中
        var selectedFlag;

        var defaultSelectedScript = this.view.json.defaultSelectedScript || this.view.viewJson.defaultSelectedScript;
        if (!this.isSelected && defaultSelectedScript) {
            // var flag = this.view.json.select || this.view.viewJson.select ||  "none";
            // if ( flag ==="single" || flag==="multi"){
            //
            // }
            selectedFlag = this.view.Macro.exec(defaultSelectedScript,
                {"node": this.node, "data": this.data, "view": this.view, "row": this});
        }

        //判断是不是在selectedItems中，用户手工选择
        if (!this.isSelected && this.view.selectedItems.length) {
            for (var i = 0; i < this.view.selectedItems.length; i++) {
                if (this.view.selectedItems[i].dataString === this.dataString) {
                    selectedFlag = "true";
                    break;
                }
            }
        }

        if (selectedFlag) {
            if (selectedFlag === "multi" || selectedFlag === "single") {
                this.select(selectedFlag);
            } else if (selectedFlag.toString() === "true") {
                var f = this.view.json.select || this.view.viewJson.select || "none";
                if (f === "single" || f === "multi") {
                    this.select();
                }
            }
        }

        this.setEvent();

        this.view.fireEvent("postLoadItemRow", [null, this]);

        this.loading = false;
        this.loaded = true;
    },
    selected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        this.view.selectedItems.push(this);
        var viewStyles = this.view.viewJson.viewStyles;
        if( viewStyles ){
            this.selectTd.setStyles( viewStyles["checkedCheckboxNode"] );
            this.node.setStyles( viewStyles["contentSelectedTr"] );
        }else{
            this.selectTd.setStyles({"background": "url("+"../x_component_query_Query/$Viewer/default/icon/checkbox_checked.png) center center no-repeat"});
            this.node.setStyles(this.css.viewContentTrNode_selected);
        }
        this.isSelected = true;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("selectRow", [this]);
    },
    unSelected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        var viewStyles = this.view.viewJson.viewStyles;
        if( this.view.viewJson.selectBoxShow !=="always" ){
            this.selectTd.setStyles({"background": "transparent"});
        }else{
            if (viewStyles) {
                this.selectTd.setStyles(viewStyles["checkboxNode"]);
            }else{
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/checkbox.png) center center no-repeat"});
            }
        }
        if( viewStyles ){
            this.node.setStyles( viewStyles["contentTr"] );
        }else{
            this.node.setStyles(this.css.viewContentTrNode);
        }
        this.isSelected = false;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("unselectRow", [this]);
    },
    getDataByPath: function (obj, path) {
        var pathList = path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else {
                obj = "";
                break;
            }
        }
        return obj
    },
    getText: function (c, k, td) {
        var path = c.path, code = c.code, obj = this.data;
        if (!path) {
           var co = code && code.trim();
           if( !co )return "";
           obj = "";
        } else if (path === "$all") {
        } else {
            obj = this.getDataByPath(obj, path);
        }

        if (code && code.trim()) obj = this.view.Macro.exec(code, {
            "value": obj,
            "data": this.data,
            "entry": c,
            "node": td,
            "json": c,
            "row": this
        });

        var toName = function (value) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    value[idx] = toName(v)
                })
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, key) {
                    value[key] = toName(v);
                })
            } else if (typeOf(value) === "string") {
                value = o2.name.cn(value)
            }
            return value;
        };

        var d;
        if (obj != undefined && obj != null) {
            if (typeOf(obj) === "array") {
                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
            } else if (typeOf(obj) === "object") {
                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
            } else {
                d = c.isName ? o2.name.cn(obj.toString()) : obj;
            }
        }

        return d;
    },
    setOpenWork: function (td, column) {
        td.setStyle("cursor", "pointer");
        if (column.clickCode) {
            if (!this.view.Macro) {
                MWF.require("MWF.xScript.Macro", function () {
                    this.view.businessData = {};
                    this.view.Macro = new MWF.Macro.ViewContext(this.view);
                }.bind(this), false);
            }
            td.addEvent("click", function (ev) {
                var result = this.view.Macro.fire(column.clickCode, this, ev);
                ev.stopPropagation();
                return result;
            }.bind(this));
        } else if (this.view.statementJson.entityCategory === "official" && column.idPath) {
            var id = this.getDataByPath(this.data, column.idPath);
            if (id) {
                if (this.view.statementJson.entityClassName === "com.x.cms.core.entity.Document") {
                    td.addEvent("click", function (ev) {
                        this.openCms(ev, id);
                        ev.stopPropagation();
                    }.bind(this));
                } else {
                    td.addEvent("click", function (ev) {
                        this.openWork(ev, id);
                        ev.stopPropagation();
                    }.bind(this));
                }
            }
        }
    },
    openCms: function (e, id) {
        var options = {"documentId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
        layout.desktop.openApplication(e, "cms.Document", options);
    },
    openWork: function (e, id) {
        var options = {"workId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
        layout.desktop.openApplication(e, "process.Work", options);
    }
});

MWF.xApplication.query.Query.Statement.Filter = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Filter
});

MWF.xApplication.query.Query.Statement.Actionbar = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Actionbar
});

MWF.xApplication.query.Query.Statement.Paging = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Paging
});


MWF.xApplication.query.Query.Statement.ExcelUtils = new Class({
    initialize: function(){
        // this.datatemplate = datatemplate;
        // this.form = datatemplate.form;
        if (!FileReader.prototype.readAsBinaryString) {
            FileReader.prototype.readAsBinaryString = function (fileData) {
                var binary = "";
                var pt = this;
                var reader = new FileReader();
                reader.onload = function (e) {
                    var bytes = new Uint8Array(reader.result);
                    var length = bytes.byteLength;
                    for (var i = 0; i < length; i++) {
                        binary += String.fromCharCode(bytes[i]);
                    }
                    //pt.result  - readonly so assign binary
                    pt.content = binary;
                    pt.onload();
                };
                reader.readAsArrayBuffer(fileData);
            }
        }
    },
    _loadResource : function( callback ){
        if( !window.XLSX || !window.xlsxUtils ){
            var uri = "../x_component_Template/framework/xlsx/xlsx.full.js";
            var uri2 = "../x_component_Template/framework/xlsx/xlsxUtils.js";
            COMMON.AjaxModule.load(uri, function(){
                COMMON.AjaxModule.load(uri2, function(){
                    callback();
                }.bind(this))
            }.bind(this))
        }else{
            callback();
        }
    },
    _openDownloadDialog: function(url, saveName){
        /**
         * 通用的打开下载对话框方法，没有测试过具体兼容性
         * @param url 下载地址，也可以是一个blob对象，必选
         * @param saveName 保存文件名，可选
         */
        if( Browser.name !== 'ie' ){
            if(typeof url == 'object' && url instanceof Blob){
                url = URL.createObjectURL(url); // 创建blob地址
            }
            var aLink = document.createElement('a');
            aLink.href = url;
            aLink.download = saveName || ''; // HTML5新增的属性，指定保存文件名，可以不要后缀，注意，file:///模式下不会生效
            var event;
            if(window.MouseEvent && typeOf( window.MouseEvent ) == "function" ) event = new MouseEvent('click');
            else
            {
                event = document.createEvent('MouseEvents');
                event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            }
            aLink.dispatchEvent(event);
        }else{
            window.navigator.msSaveBlob( url, saveName);
        }
    },

    index2ColName : function( index ){
        if (index < 0) {
            return null;
        }
        var num = 65;// A的Unicode码
        var colName = "";
        do {
            if (colName.length > 0)index--;
            var remainder = index % 26;
            colName =  String.fromCharCode(remainder + num) + colName;
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName;
    },

    upload : function ( dateColIndexArray, callback ) {
        var dateColArray = [];
        dateColIndexArray.each( function (idx) {
            dateColArray.push( this.index2ColName( idx ));
        }.bind(this))


        var uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" accept=\"csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel\" />";
        uploadFileAreaNode.set("html", html);

        var fileUploadNode = uploadFileAreaNode.getFirst();
        fileUploadNode.addEvent("change", function () {
            var files = fileNode.files;
            if (files.length) {
                var file = files.item(0);
                // if( file.name.indexOf(" ") > -1 ){
                // 	this.form.notice( MWF.xApplication.process.Xform.LP.uploadedFilesCannotHaveSpaces, "error");
                // 	return false;
                // }

                //第三个参数是日期的列
                this.importFromExcel( file, function(json){
                    //json为导入的结果
                    if(callback)callback(json);
                    uploadFileAreaNode.destroy();
                }.bind(this), dateColArray ); //["E","F"]

            }
        }.bind(this));
        var fileNode = uploadFileAreaNode.getFirst();
        fileNode.click();
    },
    exportToExcel : function(array, fileName, colWidthArr, dateIndexArray, numberIndexArray){
        // var array = [["姓名","性别","学历","专业","出生日期","毕业日期"]];
        // array.push([ "张三","男","大学本科","计算机","2001-1-2","2019-9-2" ]);
        // array.push([ "李四","男","大学专科","数学","1998-1-2","2018-9-2" ]);
        // this.exportToExcel(array, "导出数据"+(new Date).format("db"));
        this._loadResource( function(){
            var data = window.xlsxUtils.format2Sheet(array, 0, 0, null);//偏移3行按keyMap顺序转换
            var wb = window.xlsxUtils.format2WB(data, "sheet1", undefined);
            var wopts = { bookType: 'xlsx', bookSST: false, type: 'binary' };
            var dataInfo = wb.Sheets[wb.SheetNames[0]];

            var widthArray = [];
            array[0].each( function( v, i ){ //设置标题行样式

                if( !colWidthArr )widthArray.push( {wpx: 100} );

                // var at = String.fromCharCode(97 + i).toUpperCase();
                var at = this.index2ColName(i);
                var di = dataInfo[at+"1"];
                // di.v = v;
                // di.t = "s";
                di.s = {  //设置副标题样式
                    font: {
                        //name: '宋体',
                        sz: 12,
                        color: {rgb: "#FFFF0000"},
                        bold: true,
                        italic: false,
                        underline: false
                    },
                    alignment: {
                        horizontal: "center" ,
                        vertical: "center"
                    }
                };
            }.bind(this));

            if( dateIndexArray && dateIndexArray.length ){
                dateIndexArray.each( function( value, index ){
                    dateIndexArray[ index ] = this.index2ColName(value);
                }.bind(this))
            }

            if( numberIndexArray && numberIndexArray.length ){
                numberIndexArray.each( function( value, index ){
                    numberIndexArray[ index ] = this.index2ColName(value);
                }.bind(this))
            }

            var typeFlag = ( dateIndexArray && dateIndexArray.length ) || ( numberIndexArray && numberIndexArray.length );

            for( var key in dataInfo ){
                //设置所有样式，wrapText=true 后 /n会被换行
                if( key.substr(0, 1) !== "!" ){
                    var di = dataInfo[key];
                    if( !di.s )di.s = {};
                    if( !di.s.alignment )di.s.alignment = {};
                    di.s.alignment.wrapText = true;

                    if( typeFlag ){

                        var colName = key.replace(/\d+/g,''); //清除数字
                        var rowNum = key.replace( colName, '');

                        if( rowNum > 1 ){
                            if( dateIndexArray && dateIndexArray.length && dateIndexArray.contains( colName ) ){
                                //di.s.numFmt = "yyyy-mm-dd HH:MM:SS"; //日期列 两种方式都可以
                                di.z = 'yyyy-mm-dd HH:MM:SS'; //日期列
                            }
                            if( numberIndexArray && numberIndexArray.length && numberIndexArray.contains( colName ) ){
                                di.s.alignment.wrapText = false;
                                di.t = 'n'; //数字类型
                            }
                        }

                    }
                }

            }

            if( colWidthArr ){
                colWidthArr.each( function (w) {
                    widthArray.push( {wpx: w} );
                })
            }
            dataInfo['!cols'] = widthArray; //列宽度

            this._openDownloadDialog(window.xlsxUtils.format2Blob(wb), fileName +".xlsx");
        }.bind(this))
    },
    importFromExcel : function( file, callback, dateColArray ){
        this._loadResource( function(){
            var reader = new FileReader();
            var workbook, data;
            reader.onload = function (e) {
                //var data = data.content;
                if (!e) {
                    data = reader.content;
                }else {
                    data = e.target.result;
                }
                workbook = window.XLSX.read(data, { type: 'binary' });
                //wb.SheetNames[0]是获取Sheets中第一个Sheet的名字
                //wb.Sheets[Sheet名]获取第一个Sheet的数据
                var sheet = workbook.SheetNames[0];
                if (workbook.Sheets.hasOwnProperty(sheet)) {
                    // fromTo = workbook.Sheets[sheet]['!ref'];
                    // console.log(fromTo);
                    var worksheet = workbook.Sheets[sheet];

                    if( dateColArray && typeOf(dateColArray) == "array" && dateColArray.length ){
                        var rowCount;
                        if( worksheet['!range'] ){
                            rowCount = worksheet['!range'].e.r;
                        }else{
                            var ref = worksheet['!ref'];
                            var arr = ref.split(":");
                            if(arr.length === 2){
                                rowCount = parseInt( arr[1].replace(/[^0-9]/ig,"") );
                            }
                        }
                        if( rowCount ){
                            for( var i=0; i<dateColArray.length; i++ ){
                                for( var j=1; j<=rowCount; j++ ){
                                    var cell = worksheet[ dateColArray[i]+j ];
                                    if( cell ){
                                        delete cell.w; // remove old formatted text
                                        cell.z = 'yyyy-mm-dd'; // set cell format
                                        window.XLSX.utils.format_cell(cell); // this refreshes the formatted text.
                                    }
                                }
                            }
                        }
                    }

                    var json = window.XLSX.utils.sheet_to_json( worksheet );
                    //var data = window.XLSX.utils.sheet_to_row_object_array(workbook.Sheets[sheet], {dateNF:'YYYY-MM-DD'});
                    if(callback)callback(json);
                    // console.log(JSON.stringify(json));
                    // break; // 如果只取第一张表，就取消注释这行
                }
                // for (var sheet in workbook.Sheets) {
                //     if (workbook.Sheets.hasOwnProperty(sheet)) {
                //         fromTo = workbook.Sheets[sheet]['!ref'];
                //         console.log(fromTo);
                //         var json = window.XLSX.utils.sheet_to_json(workbook.Sheets[sheet]);
                //         console.log(JSON.stringify(json));
                //         // break; // 如果只取第一张表，就取消注释这行
                //     }
                // }
            };
            reader.readAsBinaryString(file);
        })
    }
});