MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);

MWF.xApplication.Attendance.UnitDingdingDetail = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (node, app, actions, options) {
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$UnitDetail/";
        this.cssPath = "../x_component_Attendance/$UnitDetail/" + this.options.style + "/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    load: function () {
        this.loadTab();
    },
    loadTab: function () {

        this.tabNode = new Element("div", { "styles": this.css.tabNode }).inject(this.node);
        this.detailArea = new Element("div", { "styles": this.css.tabPageContainer }).inject(this.tabNode);
        //this.selfHolidayArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode)
        this.detailStaticArea = new Element("div", { "styles": this.css.tabPageContainer }).inject(this.tabNode);
        //this.selfHolidayStaticArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode)

        MWF.require("MWF.widget.Tab", function () {

            this.tabs = new MWF.widget.Tab(this.tabNode, { "style": "attendance" });
            this.tabs.load();

            this.detailPage = this.tabs.addTab(this.detailArea, this.app.lp.unitSigninDetail, false);
            this.detailPage.contentNodeArea.set("class", "detailPage");
            this.detailPage.addEvent("show", function () {
                if (!this.detailExplorer) {
                    this.detailExplorer = new MWF.xApplication.Attendance.UnitDingdingDetail.Explorer(this.detailArea, this);
                    this.detailExplorer.load();
                }
            }.bind(this));


            this.detailStaticPage = this.tabs.addTab(this.detailStaticArea, this.app.lp.unitSigninStatic, false);
            this.detailStaticPage.contentNodeArea.set("class", "detailStaticPage");
            this.detailStaticPage.addEvent("show", function () {
                if (!this.detailStaticExplorer) {
                    this.detailStaticExplorer = new MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticExplorer(this.detailStaticArea, this);
                    this.detailStaticExplorer.load();
                }
            }.bind(this));

            this.tabs.pages[0].showTab();
        }.bind(this));
    }
});

MWF.xApplication.Attendance.UnitDingdingDetail.Explorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function (node, parent, options) {
        this.setOptions(options);
        this.parent = parent;
        this.app = parent.app;
        this.lp = this.app.lp;
        this.css = parent.css;
        this.path = parent.path;

        this.actions = parent.actions;
        this.node = $(node);

        this.initData();
        if (!this.peopleActions) this.peopleActions = new MWF.xAction.org.express.RestActions();
    },
    initData: function () {
        this.toolItemNodes = [];
    },
    reload: function () {
        this.node.empty();
        this.load();
    },
    load: function () {
        this.loadFilter();
        this.loadContentNode();
        this.setNodeScroll();
    },
    loadFilter: function () {
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.node);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' lable='unit'></td>" +
            "    <td styles='filterTableTitle' item='unit'></td>" +
            "    <td styles='filterTableTitle' lable='year'></td>" +
            "    <td styles='filterTableValue' item='year'></td>" +
            "    <td styles='filterTableTitle' lable='month'></td>" +
            "    <td styles='filterTableValue' item='month'></td>" +
            "    <td styles='filterTableTitle' lable='day'></td>" +
            "    <td styles='filterTableValue' item='day'></td>" +
            "    <td styles='filterTableTitle' lable='checkType'></td>" +
            "    <td styles='filterTableValue' item='checkType'></td>" +
            "    <td styles='filterTableTitle' lable='timeResult'></td>" +
            "    <td styles='filterTableValue' item='timeResult'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.fileterNode, {}, {
                isEdited: true,
                itemTemplate: {
                    unit: { text: lp.unit, type: "org", orgType: "unit", notEmpty: true, style: { "min-width": "200px" } },
                    year: {
                        text: lp.annuaal,
                        "type": "select",
                        "selectValue": function () {
                            var years = [];
                            var year = new Date().getFullYear();
                            for (var i = 0; i < 6; i++) {
                                years.push(year--);
                            }
                            return years;
                        },
                        "event": {
                            "change": function (item, ev) {
                                var values = this.getDateSelectValue();
                                item.form.getItem("day").resetItemOptions(values, values)
                            }.bind(this)
                        }
                    },
                    month: {
                        text: lp.months,
                        "type": "select",
                        "defaultValue": function () {
                            var month = (new Date().getMonth() + 1).toString();
                            return month.length == 1 ? "0" + month : month;
                        },
                        "selectValue": ["", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
                        "event": {
                            "change": function (item, ev) {
                                var values = this.getDateSelectValue();
                                item.form.getItem("day").resetItemOptions(values, values)
                            }.bind(this)
                        }
                    },
                    day: { text: lp.date, "type": "select", "selectValue": this.getDateSelectValue.bind(this) },
                    checkType: { text: lp.signinType, "type": "select", "selectValue": ["", "OnDuty", "OffDuty"], "selectText": lp.signinTypeSelectText },
                    timeResult: { text: lp.signinResult, "type": "select", "selectValue": ["", "Normal", "Early", "Late", "SeriousLate", "Absenteeism", "NotSigned"], "selectText": lp.signinResultSelectText },
                    action: {
                        "value": lp.query, type: "button", className: "filterButton", event: {
                            click: function () {
                                var result = this.form.getResult(true, ",", true, true, false);
                                if (!result) return;
                                if (result.day && result.day != "") {
                                    result.q_date = result.year + "-" + result.month + "-" + result.day;
                                }
                                this.loadView(result);
                            }.bind(this)
                        }
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    getDateSelectValue: function () {
        if (this.form) {
            var year = parseInt(this.form.getItem("year").getValue());
            var month = parseInt(this.form.getItem("month").getValue()) - 1;
        } else {
            var year = (new Date()).getFullYear();
            var month = (new Date()).getMonth();
        }
        var date = new Date(year, month, 1);
        var days = [];
        days.push("");
        while (date.getMonth() === month) {
            var d = date.getDate().toString();
            if (d.length == 1) d = "0" + d;
            days.push(d);
            date.setDate(date.getDate() + 1);
        }
        return days;
    },
    loadContentNode: function () {
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);
        this.app.addEvent("resize", function () { this.setContentSize(); }.bind(this));

    },
    loadView: function (filterData) {
        this.elementContentNode.empty();
        if (this.view) delete this.view;
        this.view = new MWF.xApplication.Attendance.UnitDingdingDetail.View(this.elementContentNode, this.app, this);
        this.view.filterData = filterData;
        this.view.listItemUrl = this.path + "listItem_dingding.json";
        this.view.load();
        this.setContentSize();
    },
    setContentSize: function () {
        var tabNodeSize = this.parent.tabs ? this.parent.tabs.tabNodeContainer.getSize() : { "x": 0, "y": 0 };
        var fileterNodeSize = this.fileterNode ? this.fileterNode.getSize() : { "x": 0, "y": 0 };
        var nodeSize = this.parent.node.getSize();

        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();

        var height = nodeSize.y - tabNodeSize.y - pt - pb - fileterNodeSize.y - 20;
        this.elementContentNode.setStyle("height", "" + height + "px");

        this.pageCount = (height / 40).toInt() + 5;

        if (this.view && this.view.items.length < this.pageCount) {
            this.view.loadElementList(this.pageCount - this.view.items.length);
        }
    }
});



MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticExplorer = new Class({
    Extends: MWF.xApplication.Attendance.UnitDingdingDetail.Explorer,

    loadFilter: function () {
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.node);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' style='width: 660px;font-size: 14px;color:#666'>" +
            "<tr>" +
            "    <td styles='filterTableValue' lable='q_unitName'></td>" +
            "    <td styles='filterTableTitle' item='q_unitName'></td>" +
            "    <td styles='filterTableTitle' lable='cycleYear'></td>" +
            "    <td styles='filterTableValue' item='cycleYear'></td>" +
            "    <td styles='filterTableTitle' lable='cycleMonth'></td>" +
            "    <td styles='filterTableValue' item='cycleMonth'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.fileterNode, {}, {
                isEdited: true,
                itemTemplate: {
                    q_unitName: { text: lp.unit, type: "org", orgType: "unit", notEmpty: true, style: { "min-width": "200px" } },
                    cycleYear: {
                        text: lp.annuaal,
                        "type": "select",
                        "selectValue": function () {
                            var years = [];
                            var year = new Date().getFullYear();
                            for (var i = 0; i < 6; i++) {
                                years.push(year--);
                            }
                            return years;
                        }
                    },
                    cycleMonth: {
                        text: lp.months, notEmpty: true,
                        "type": "select",
                        "defaultValue": function () {
                            var month = (new Date().getMonth() + 1).toString();
                            return month.length == 1 ? "0" + month : month;
                        },
                        "selectValue": ["", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"]
                    },
                    action: {
                        "value": lp.query, type: "button", className: "filterButton", event: {
                            click: function () {
                                var result = this.form.getResult(true, ",", true, true, false);
                                if (!result) return;
                                this.loadView(result);
                            }.bind(this)
                        }
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },

    loadView: function (filterData) {
        this.elementContentNode.empty();
        if (this.view) delete this.view;
        this.view = new MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticView(this.elementContentNode, this.app, this);
        this.view.filterData = filterData;
        this.view.listItemUrl = this.path + "listItem_dingding_detailStatic.json";
        this.view.load();
        this.setContentSize();
    }
});



MWF.xApplication.Attendance.UnitDingdingDetail.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function (data) {
        return new MWF.xApplication.Attendance.UnitDingdingDetail.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function (callback, count) {
        if (!count) count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};

        var action = o2.Actions.load("x_attendance_assemble_control");
        action.DingdingAttendanceAction.listNextDingdingAttendance(id, count, filter, function (json) {
            if (callback) callback(json);
        }.bind(this));
    },
    _removeDocument: function (documentData, all) {

    },
    _createDocument: function () {

    },
    _openDocument: function (documentData) {

    }

});




MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticView = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function (data) {
        return new MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticDocument(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function (callback, count) {
        var filter = this.filterData || {};
        var action = o2.Actions.load("x_attendance_assemble_control");
        action.DingdingAttendanceStatisticAction.personMonthWithUnit(filter.q_unitName, filter.cycleYear, filter.cycleMonth, function (json) {
            if (callback) callback(json);
        }.bind(this));
    },
    _removeDocument: function (documentData, all) {

    },
    _createDocument: function () {

    },
    _openDocument: function (documentData) {

    }

});



MWF.xApplication.Attendance.UnitDingdingDetail.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.UnitDingdingDetail.DetailStaticDocument = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

