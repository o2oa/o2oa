MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);

MWF.xApplication.Attendance.UnitDetail = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$UnitDetail/";
        this.cssPath = "/x_component_Attendance/$UnitDetail/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    load: function(){
        this.loadTab();
    },
    loadTab : function(){

        this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.node);
        this.detailArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        //this.selfHolidayArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode)
        this.detailStaticArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
        //this.selfHolidayStaticArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode)

        MWF.require("MWF.widget.Tab", function(){

            this.tabs = new MWF.widget.Tab(this.tabNode, {"style": "attendance"});
            this.tabs.load();

            this.detailPage = this.tabs.addTab(this.detailArea, this.app.lp.unitAttendanceDetail, false);
            this.detailPage.contentNodeArea.set("class","detailPage");
            this.detailPage.addEvent("show",function(){
                if( !this.detailExplorer ){
                    this.detailExplorer = new MWF.xApplication.Attendance.UnitDetail.Explorer( this.detailArea, this );
                    this.detailExplorer.load();
                }
            }.bind(this));


            this.detailStaticPage = this.tabs.addTab(this.detailStaticArea, this.app.lp.unitAttendanceStatic, false);
            this.detailStaticPage.contentNodeArea.set("class","detailStaticPage");
            this.detailStaticPage.addEvent("show",function(){
                if( !this.detailStaticExplorer ){
                    this.detailStaticExplorer = new MWF.xApplication.Attendance.UnitDetail.DetailStaticExplorer( this.detailStaticArea, this );
                    this.detailStaticExplorer.load();
                }
            }.bind(this));

            this.tabs.pages[0].showTab();
        }.bind(this));
    }
});

MWF.xApplication.Attendance.UnitDetail.Explorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, parent, options){
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
    initData: function(){
        this.toolItemNodes = [];
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.loadFilter();
        this.loadContentNode();
        this.setNodeScroll();
    },
    loadFilter: function(){
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>"+
            "<tr>" +
            "    <td styles='filterTableValue' lable='q_unitName'></td>"+
            "    <td styles='filterTableTitle' item='q_unitName'></td>"+
            "    <td styles='filterTableTitle' lable='cycleYear'></td>"+
            "    <td styles='filterTableValue' item='cycleYear'></td>" +
            "    <td styles='filterTableTitle' lable='cycleMonth'></td>"+
            "    <td styles='filterTableValue' item='cycleMonth'></td>" +
            "    <td styles='filterTableTitle' lable='date'></td>"+
            "    <td styles='filterTableValue' item='date'></td>" +
            "    <td styles='filterTableTitle' lable='isAbsent'></td>"+
            "    <td styles='filterTableValue' item='isAbsent'></td>" +
            "    <td styles='filterTableTitle' lable='isLate'></td>"+
            "    <td styles='filterTableValue' item='isLate'></td>" +
            "    <td styles='filterTableTitle' lable='isLackOfTime'></td>"+
            "    <td styles='filterTableValue' item='isLackOfTime'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='export'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.fileterNode, {}, {
                isEdited : true,
                itemTemplate : {
                    q_unitName : { text : lp.unit, type : "org", orgType : "unit", notEmpty : true, style : {"min-width": "200px" } },
                    cycleYear : {
                        text : lp.annuaal,
                        "type" : "select",
                        "selectValue" : function(){
                            var years = [];
                            var year = new Date().getFullYear();
                            for(var i=0; i<6; i++ ){
                                years.push( year-- );
                            }
                            return years;
                        },
                        "event" : {
                            "change" : function( item, ev ){
                                var values = this.getDateSelectValue();
                                item.form.getItem( "date").resetItemOptions( values , values )
                            }.bind(this)
                        }
                    },
                    cycleMonth : {
                        text : lp.months,
                        "type" : "select",
                        "defaultValue" : function(){
                            var month = (new Date().getMonth() + 1 ).toString();
                            return  month.length == 1 ? "0"+month : month;
                        },
                        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"],
                        "event" : {
                            "change" : function( item, ev ){
                                var values = this.getDateSelectValue();
                                item.form.getItem( "date").resetItemOptions( values , values )
                            }.bind(this)
                        }
                    },
                    date : { text : lp.date,  "type" : "select", "selectValue" : this.getDateSelectValue.bind(this) },
                    isAbsent : { text: lp.absent,  "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.absendSelectText },
                    isLate : { text: lp.late,  "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.lateSelectText },
                    isLackOfTime : { text: lp.lackOfTime, "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.truefalseSelectText },
                    action : { "value" : lp.query, type : "button", className : "filterButton", event : {
                        click : function(){
                            var result = this.form.getResult(true,",",true,true,false);
                            if( !result )return;
                            if( typeOf( result.isAbsent ) == "string" )result.isAbsent = this.getBoolean( result.isAbsent );
                            if( typeOf( result.isLate ) == "string" )result.isLate = this.getBoolean( result.isLate );
                            if( typeOf( result.isLackOfTime ) == "string" )result.isLackOfTime = this.getBoolean( result.isLackOfTime );

                            if( result.date && result.date !="" ){
                                result.q_date =  result.cycleYear + "-" + result.cycleMonth + "-" + result.date;
                            }
                            this.loadView( result );
                        }.bind(this)
                    }},
                    export : { "value" : lp.export, type : "button", className : "filterButton", event : {
                            click : function(){
                                var result = this.form.getResult(true,",",true,true,false);
                                if( !result )return;
                                debugger;
                                if( !result.q_topUnitName )result.q_topUnitName = "0";
                                if( !result.q_unitName)result.q_unitName = "0";
                                if( !result.q_empName)result.q_empName = "0";
                                if( !result.cycleYear )result.cycleYear = "0";
                                if( !result.cycleMonth )result.cycleMonth = "0";
                                if( result.date && result.date !="" ){
                                    result.q_date =  result.cycleYear + "-" + result.cycleMonth + "-" + result.date;
                                }else{
                                    result.q_date ="0";
                                }
                                if( !result.isAbsent )result.isAbsent = "0";
                                if( !result.isLackOfTime  )result.isLackOfTime = "0";
                                if( !result.isLate )result.isLate = "0";
                                debugger;
                                this.actions.detailsExportStream(result.q_topUnitName,result.q_unitName,result.q_empName,result.cycleYear,result.cycleMonth,result.q_date,result.isAbsent,result.isLackOfTime,result.isLate,true);
                            }.bind(this)
                        }}
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    getDateSelectValue : function(){
        if( this.form ){
            var year = parseInt(this.form.getItem("cycleYear").getValue());
            var month = parseInt(this.form.getItem("cycleMonth").getValue())-1;
        }else{
            var year =  (new Date()).getFullYear() ;
            var month =  (new Date()).getMonth() ;
        }
        var date = new Date(year, month, 1);
        var days = [];
        days.push("");
        while (date.getMonth() === month) {
            var d = date.getDate().toString();
            if( d.length == 1 )d = "0"+d;
            days.push( d );
            date.setDate(date.getDate() + 1);
        }
        return days;
    },
    //loadFilter : function(){
    //     this.fileterNode = new Element("div.fileterNode", {
    //         "styles" : this.css.fileterNode
    //     }).inject(this.node)
    //
    //    var table = new Element("table", {
    //        "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
    //    }).inject( this.fileterNode );
    //    var tr = new Element("tr").inject(table);
    //
    //    this.createUnitTd( tr )
    //    this.createYearSelectTd( tr )
    //    this.createMonthSelectTd( tr )
    //    this.createDateSelectTd( tr )
    //    this.createIsAbsent(tr)
    //    this.createIsLate( tr )
    //    //this.createIsLeaveEarlier( tr )
    //    this.createLackOfTimeCount(tr)
    //    this.createActionTd( tr )
    //},
    //createTypeId : function(tr){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.lp.type  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.q_type = new MDomItem( td, {
    //        "name" : "q_type",
    //        "type" : "select",
    //        "selectValue": ["day","month"],
    //        "selectText": [this.lp.staticByDay,this.lp.staticByMonth],
    //    }, true, this.app );
    //    this.q_type.load();
    //},
    //createUnitTd : function(tr){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "部门"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    if( this.app.isAdmin() ){
    //        this.q_unitName = new MDomItem( td, {
    //            "name" : "q_unitName",
    //            "defaultValue" : this.app.manageUnits.length > 0 ? this.app.manageUnits[0] : "",
    //            "event" : {
    //                "click" : function(el){ _self.selecePerson(); }
    //            }
    //        }, true, this.app );
    //        this.q_unitName.load();
    //    }else{
    //        this.q_unitName = new MDomItem( td, {
    //            "name" : "q_unitName",
    //            "type" : "select",
    //            "selectValue": this.app.manageUnits
    //        }, true, this.app );
    //        this.q_unitName.load();
    //    }
    //},
    //createYearSelectTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "年度"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.cycleYear = new MDomItem( td, {
    //        "name" : "cycleYear",
    //        "type" : "select",
    //        "selectValue" : function(){
    //            var years = [];
    //            var year = new Date().getFullYear();
    //            for(var i=0; i<6; i++ ){
    //                years.push( year-- );
    //            }
    //            return years;
    //        },
    //        "event" : {
    //            "change" : function(){ if(_self.dateSelecterTd)_self.createDateSelectTd() }
    //        }
    //    }, true, this.app );
    //    this.cycleYear.load();
    //},
    //createMonthSelectTd : function( tr ){
    //    var _self = this;
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "月份"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.cycleMonth = new MDomItem( td, {
    //        "name" : "cycleMonth",
    //        "type" : "select",
    //        "defaultValue" : function(){
    //            var month = (new Date().getMonth() + 1 ).toString();
    //            return  month.length == 1 ? "0"+month : month;
    //        },
    //        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"],
    //        "event" : {
    //            "change" : function(){ if(_self.dateSelecterTd)_self.createDateSelectTd() }
    //        }
    //    }, true, this.app );
    //    this.cycleMonth.load();
    //},
    //createDateSelectTd : function( tr ){
    //    var _self = this;
    //    if( tr ){
    //        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "日期"  }).inject(tr);
    //        this.dateSelecterTd = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    }
    //    if( this.q_date ){
    //        this.dateSelecterTd.empty();
    //    }
    //    this.q_date = new MDomItem( this.dateSelecterTd, {
    //        "name" : "q_date",
    //        "type" : "select",
    //        "selectValue" : function(){
    //            var year = parseInt(_self.cycleYear.getValue());
    //            var month = parseInt(_self.cycleMonth.getValue())-1;
    //            var date = new Date(year, month, 1);
    //            var days = [];
    //            days.push("");
    //            while (date.getMonth() === month) {
    //                var d = date.getDate().toString();
    //                if( d.length == 1 )d = "0"+d
    //                days.push( d );
    //                date.setDate(date.getDate() + 1);
    //            }
    //            return days;
    //        }
    //    }, true, this.app );
    //    this.q_date.load();
    //},
    //createIsAbsent: function(tr){
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "缺勤"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.isAbsent = new MDomItem( td, {
    //        "name" : "isAbsent",
    //        "type" : "select",
    //        "selectValue" : ["","true","false"],
    //        "selectText" : ["","缺勤","未缺勤"],
    //    }, true, this.app );
    //    this.isAbsent.load();
    //},
    //createLackOfTimeCount: function(tr){
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "工时不足"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.isLackOfTime = new MDomItem( td, {
    //        "name" : "isLackOfTime",
    //        "type" : "select",
    //        "selectValue" : ["","true","false"],
    //        "selectText" : ["","是","否"],
    //    }, true, this.app );
    //    this.isLackOfTime.load();
    //},
    //createIsLate: function(tr){
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "迟到"  }).inject(tr);
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    this.isLate  = new MDomItem( td, {
    //        "name" : "isLate",
    //        "type" : "select",
    //        "selectValue" : ["","true","false"],
    //        "selectText" : ["","迟到","未迟到"],
    //    }, true, this.app );
    //    this.isLate.load();
    //},
    //createActionTd : function( tr ){
    //    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    //    var input = new Element("button",{
    //        "text" : "查询",
    //        "styles" : this.css.filterButton
    //    }).inject(td);
    //    input.addEvent("click", function(){
    //        if( this.q_unitName.getValue().trim() == "" ){
    //            this.app.notice( "请先选择部门", "error" );
    //            return;
    //        }
    //        var filterData = {
    //            q_unitName : this.q_unitName.getValue(),
    //            cycleYear : this.cycleYear.getValue(),
    //            cycleMonth : this.cycleMonth.getValue()
    //        }
    //        if( this.q_type ){
    //            filterData.q_type =  this.q_type.getValue();
    //        }
    //        if( this.isAbsent && this.isAbsent.getValue()!=""  ){
    //            filterData.isAbsent =  this.getBoolean(this.isAbsent.getValue());
    //        }
    //        if( this.isLeaveEarlier && this.isLeaveEarlier.getValue()!=""  ){
    //            filterData.isLeaveEarlier =  this.getBoolean(this.isLeaveEarlier.getValue());
    //        }
    //        if( this.isLate && this.isLate.getValue()!=""  ){
    //            filterData.isLate =  this.getBoolean(this.isLate.getValue());
    //        }
    //        if( this.isLackOfTime && this.isLackOfTime.getValue()!=""  ){
    //            filterData.isLackOfTime =  this.getBoolean( this.isLackOfTime.getValue() );
    //        }
    //        if( this.q_date && this.q_date.getValue()!="" ){
    //            filterData.q_day = this.q_date.getValue();
    //            filterData.q_date =  this.cycleYear.getValue() + "-" + this.cycleMonth.getValue() + "-" + this.q_date.getValue();
    //        }
    //        this.loadView( filterData );
    //    }.bind(this))
    //},
    getBoolean : function( value ){
        if( value === "true" )return true;
        if( value === "false" )return false;
        return value;
    },
    //selecePerson: function(){
    //    var options = {
    //        "type": "unit",
    //        "title": "选择部门",
    //        "count" : "1",
    //        "onComplete": function(items){
    //            var names = [];
    //            items.each(function(item){
    //                names.push(item.data.name);
    //            }.bind(this));
    //            this.q_unitName.setValue( names.join(",") )
    //        }.bind(this)
    //    };
    //    var selector = new MWF.O2Selector(this.app.content, options);
    //},
    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));

    },
    loadView : function( filterData ){
        this.elementContentNode.empty();
        if( this.view )delete this.view;
        this.view = new MWF.xApplication.Attendance.UnitDetail.View(this.elementContentNode, this.app,this );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    },
    setContentSize: function(){
        var tabNodeSize = this.parent.tabs ? this.parent.tabs.tabNodeContainer.getSize() : {"x":0,"y":0};
        var fileterNodeSize = this.fileterNode ? this.fileterNode.getSize() : {"x":0,"y":0};
        var nodeSize = this.parent.node.getSize();

        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();

        var height = nodeSize.y-tabNodeSize.y-pt-pb-fileterNodeSize.y-20;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;

        if (this.view && this.view.items.length<this.pageCount){
            this.view.loadElementList(this.pageCount-this.view.items.length);
        }
    }
});



MWF.xApplication.Attendance.UnitDetail.DetailStaticExplorer = new Class({
    Extends: MWF.xApplication.Attendance.UnitDetail.Explorer,

    loadFilter: function(){
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' style='width: 660px;font-size: 14px;color:#666'>"+
            "<tr>" +
            "    <td styles='filterTableValue' lable='q_unitName'></td>"+
            "    <td styles='filterTableTitle' item='q_unitName'></td>"+
            "    <td styles='filterTableTitle' lable='cycleYear'></td>"+
            "    <td styles='filterTableValue' item='cycleYear'></td>" +
            "    <td styles='filterTableTitle' lable='cycleMonth'></td>"+
            "    <td styles='filterTableValue' item='cycleMonth'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='export'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.fileterNode, {}, {
                isEdited : true,
                itemTemplate : {
                    q_unitName : { text : lp.unit, type : "org", orgType : "unit", notEmpty : true, style : {"min-width": "200px" } },
                    cycleYear : {
                        text : lp.annuaal,
                        "type" : "select",
                        "selectValue" : function(){
                            var years = [];
                            var year = new Date().getFullYear();
                            for(var i=0; i<6; i++ ){
                                years.push( year-- );
                            }
                            return years;
                        }
                    },
                    cycleMonth : {
                        text : lp.months, notEmpty : true,
                        "type" : "select",
                        "defaultValue" : function(){
                            var month = (new Date().getMonth() + 1 ).toString();
                            return  month.length == 1 ? "0"+month : month;
                        },
                        "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"]
                    },
                    action : { "value" : lp.query, type : "button", className : "filterButton", event : {
                        click : function(){
                            var result = this.form.getResult(true,",",true,true,false);
                            if( !result )return;
                            this.loadView( result );
                        }.bind(this)
                    }},
                    export : { "value" : lp.export, type : "button", className : "filterButton", event : {
                            click : function(){
                                var result = this.form.getResult(true,",",true,true,false);
                                if( !result )return;
                                this.actions.exportUnitStatisticAttachment(result.q_unitName,result.cycleYear,result.cycleMonth,true);
                            }.bind(this)
                        }}
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    //loadFilter2 : function(){
    //    this.fileterNode = new Element("div.fileterNode", {
    //        "styles" : this.css.fileterNode
    //    }).inject(this.node)
    //
    //    var table = new Element("table", {
    //        "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
    //    }).inject( this.fileterNode );
    //    table.setStyle("width","700px");
    //    var tr = new Element("tr").inject(table);
    //
    //    this.createUnitTd( tr )
    //    this.createYearSelectTd( tr )
    //    this.createMonthSelectTd( tr )
    //    this.createActionTd( tr )
    //},
    //createActionTd : function( tr ) {
    //    var td = new Element("td", {"styles": this.css.filterTableValue}).inject(tr);
    //    var input = new Element("button", {
    //        "text": "查询",
    //        "styles": this.css.filterButton
    //    }).inject(td);
    //    input.addEvent("click", function () {
    //        if (this.q_unitName.getValue().trim() == "") {
    //            this.app.notice("请先选择部门", "error");
    //            return;
    //        }if (this.cycleMonth.getValue().trim() == "") {
    //            this.app.notice("请先选择月份", "error");
    //            return;
    //        }
    //        var filterData = {
    //            q_unitName: this.q_unitName.getValue(),
    //            cycleYear: this.cycleYear.getValue(),
    //            cycleMonth: this.cycleMonth.getValue()
    //        }
    //        if (this.q_type) {
    //            filterData.q_type = this.q_type.getValue();
    //        }
    //        if (this.isAbsent  && this.isAbsent.getValue()!="" ) {
    //            filterData.isAbsent = this.isAbsent.getValue();
    //        }
    //        if (this.isLeaveEarlier && this.isLeaveEarlier.getValue()!="" ) {
    //            filterData.isLeaveEarlier = this.isLeaveEarlier.getValue();
    //        }
    //        if (this.isLate && this.isLate.getValue()!="" ) {
    //            filterData.isLate = this.isLate.getValue();
    //        }
    //        if (this.q_date && this.q_date.getValue() != "") {
    //            filterData.q_day = this.q_date.getValue();
    //            filterData.q_date = this.cycleYear.getValue() + "-" + this.cycleMonth.getValue() + "-" + this.q_date.getValue();
    //        }
    //        this.loadView(filterData);
    //    }.bind(this))
    //},
    loadView : function( filterData ){
        this.elementContentNode.empty();
        if( this.view )delete this.view;
        this.view = new MWF.xApplication.Attendance.UnitDetail.DetailStaticView(this.elementContentNode, this.app,this );
        this.view.filterData = filterData;
        this.view.listItemUrl = this.path+"listItem_detailStatic.json";
        this.view.load();
        this.setContentSize();
    }
});



MWF.xApplication.Attendance.UnitDetail.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.UnitDetail.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};
        //filter.key = this.sortField || this.sortFieldDefault || "";
        //filter.order = this.sortType || this.sortTypeDefault || "";
        this.actions.listDetailFilterNext( id, count, filter, function(json){
            if( callback )callback(json);
        }.bind(this));
        //var filter = this.filterData || {};
        //this.actions.listUnitDetailFilter( filter, function(json){
        //    if( callback )callback(json);
        //}.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});




MWF.xApplication.Attendance.UnitDetail.DetailStaticView = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.UnitDetail.DetailStaticDocument(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var filter = this.filterData || {};
        //if( !filter.cycleMonth || filter.cycleMonth == "" )filter.cycleMonth = "(0)";
        this.actions.listPersonMonthStaticByUnit( filter.q_unitName, filter.cycleYear, filter.cycleMonth, function(json){
            //var data = json.data;
            //data.sort( function( a, b ){
            //    return parseInt( b.statisticYear + b.statisticMonth ) - parseInt( a.statisticYear + a.statisticMonth )
            //})
            //json.data = data;
            if( callback )callback(json);
        }.bind(this));

        //if( filter.q_type == "day" ) {
        //    if( filter.q_date && filter.q_date != ""  ){
        //        this.actions.listStaticDateUnit( filter.q_unitName, filter.q_date  , function(json){
        //            if( callback )callback(json);
        //        }.bind(this))
        //    }else{
        //        this.actions.listStaticDayUnit( filter.q_unitName, filter.cycleYear, filter.cycleMonth, function(json){
        //            //var data = json.data;
        //            //data.sort( function( a, b ){
        //            //    return parseInt( b.statisticDate.replace(/-/g,"") ) -  parseInt( a.statisticDate.replace(/-/g,"") );
        //            //})
        //            //json.data = data;
        //            if( callback )callback(json);
        //        }.bind(this))
        //    }
        //}else{
        //    if( !filter.cycleMonth || filter.cycleMonth == "" )filter.cycleMonth = "(0)";
        //    this.actions.listStaticMonthUnit( filter.q_unitName, filter.cycleYear, filter.cycleMonth, function(json){
        //        //var data = json.data;
        //        //data.sort( function( a, b ){
        //        //    return parseInt( b.statisticYear + b.statisticMonth ) - parseInt( a.statisticYear + a.statisticMonth )
        //        //})
        //        //json.data = data;
        //        if( callback )callback(json);
        //    }.bind(this))
        //}

    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});



MWF.xApplication.Attendance.UnitDetail.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.UnitDetail.DetailStaticDocument = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

