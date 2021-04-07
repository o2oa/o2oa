MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.Attendance.MyDetail = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "../x_component_Attendance/$MyDetail/";
        this.cssPath = "../x_component_Attendance/$MyDetail/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);
    },
    load: function(){
        //options = {
        //    "name": "直接主管",
        //        "personName": this.workContext.getWork().creatorPerson
        //}
        //return this.org.getPersonAttribute(options);
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

            this.detailPage = this.tabs.addTab(this.detailArea, this.app.lp.myDetail, false);
            this.detailPage.contentNodeArea.set("class","detailPage");
            this.detailPage.addEvent("show",function(){
                if( !this.detailExplorer ){
                    this.detailExplorer = new MWF.xApplication.Attendance.MyDetail.Explorer( this.detailArea, this );
                    this.detailExplorer.load();
                }
            }.bind(this));

            //this.selfHolidayPage = this.tabs.addTab(this.selfHolidayArea, "我的休假明细", false);
            //this.selfHolidayPage.contentNodeArea.set("class","selfHolidayPage");
            //this.selfHolidayPage.addEvent("show",function(){
            //    if( !this.selfHoliday ){
            //        this.selfHoliday = new MWF.xApplication.Attendance.MyDetail.SelfHoliday( this.selfHolidayArea, this );
            //        this.selfHoliday.load();
            //    }
            //}.bind(this))


            this.detailStaticPage = this.tabs.addTab(this.detailStaticArea, this.app.lp.myDetailStatic, false);
            this.detailStaticPage.contentNodeArea.set("class","detailStaticPage");
            this.detailStaticPage.addEvent("show",function(){
                if( !this.detailStaticExplorer ){
                    this.detailStaticExplorer = new MWF.xApplication.Attendance.MyDetail.DetailStaticExplorer( this.detailStaticArea, this );
                    this.detailStaticExplorer.load();
                }
            }.bind(this));

            //this.selfHolidayStaticPage = this.tabs.addTab(this.selfHolidayStaticArea, "我的休假统计", false);
            //this.selfHolidayStaticPage.contentNodeArea.set("class","selfHolidayStaticPage");
            //this.selfHolidayStaticPage.addEvent("show",function(){
            //    if( !this.selfHolidayStaticExplorer ){
            //        this.selfHolidayStaticExplorer = new MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticExplorer( this.selfHolidayStaticArea, this );
            //        this.selfHolidayStaticExplorer.load();
            //    }
            //}.bind(this))

            this.tabs.pages[0].showTab();
        }.bind(this));
    }
});

MWF.xApplication.Attendance.MyDetail.Explorer = new Class({
    Extends: MWF.xApplication.Attendance.Explorer,
    Implements: [Options, Events],

    initialize: function(node, parent, options){
        this.setOptions(options);
        this.parent = parent;
        this.app = parent.app;
        this.css = parent.css;
        this.path = parent.path;

        this.actions = parent.actions;
        this.node = $(node);

        this.preMonthDate = new Date();
        //this.preMonthDate.decrement("month", 1);

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
        this.loadConfig();
        this.loadFilter();
        this.loadContentNode();
        this.setNodeScroll();

        var month = (this.preMonthDate.getMonth()+1).toString();
        if( month.length == 1 )month = "0"+month;
        var filterData = {
            cycleYear : this.preMonthDate.getFullYear().toString(),
            cycleMonth : month
        };
        this.loadView( filterData );
    },
    loadConfig : function(){
        this.config = {};
        var v;
        //需要判断申述类型listSetting2020年6月16日 by gee
        this.configSetting = new Object(null);
        this.actions.listSetting(function(json){
            var data = json.data;
            if(!!data){
                json.data.map(function(e){
                    this.configSetting[e.configCode]=e;
                }.bind(this));
                v = this.configSetting.APPEALABLE.configValue;
            }else{
                v = null;
            }
        }.bind(this),null,false);
        /*this.actions.getSettingCode( "APPEALABLE", function(json){
            v =  json.data ? json.data.configValue : null;
        },null, false);*/
        if( !v ){
            this.config.APPEALABLE = true;
        }else{
            this.config.APPEALABLE = (v != "false" )
        }
    },
    loadFilter: function(){
        var lp = MWF.xApplication.Attendance.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='filterTable'>"+
            "<tr>" +
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
            "</tr>" +
            "</table>";
        this.fileterNode.set("html",html);

        MWF.xDesktop.requireApp("Template", "MForm", function(){
            this.form = new MForm( this.fileterNode, {}, {
                isEdited : true,
                itemTemplate : {
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
                    date : { text : lp.date,  "type" : "select", "selectValue" : function(){
                            var year =  this.preMonthDate.getFullYear() ;
                            var month =  this.preMonthDate.getMonth() ;
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
                        }.bind(this)
                    },
                    isAbsent : { text: lp.absent,  "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.absendSelectText },
                    isLate : { text: lp.late,  "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.lateSelectText },
                    isLackOfTime : { text: lp.lackOfTime, "type" : "select", "selectValue" : ["","true","false"], "selectText" : lp.truefalseSelectText },
                    action : { "value" : lp.query, type : "button", className : "filterButton", event : {
                            click : function(){
                                var result = this.form.getResult(false,null,false,true,false);

                               /* var year = this.preMonthDate.getFullYear().toString();
                                var month = (this.preMonthDate.getMonth()+1).toString();
                                if( month.length == 1 )month = "0"+month;
                                result.cycleYear = year;
                                result.cycleMonth = month;*/

                                if( typeOf( result.isAbsent ) == "string" )result.isAbsent = this.getBoolean( result.isAbsent );
                                if( typeOf( result.isLate ) == "string" )result.isLate = this.getBoolean( result.isLate );
                                if( typeOf( result.isLackOfTime ) == "string" )result.isLackOfTime = this.getBoolean( result.isLackOfTime );

                                if( result.date && result.date !="" ){
                                    result.q_date =  year + "-" + month + "-" + result.date;
                                }
                                this.loadView( result );
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
    //    var tr = new Element("tr").inject(table);
    //
    //    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : this.preMonthDate.format(this.app.lp.dateFormatMonth)  }).inject(tr);
    //
    //    //this.createYearSelectTd( tr )
    //    //this.createMonthSelectTd( tr )
    //    this.createDateSelectTd( tr )
    //    this.createIsAbsent(tr)
    //    this.createIsLate( tr )
    //    //this.createIsLeaveEarlier( tr )
    //    this.createLackOfTimeCount(tr)
    //    this.createActionTd( tr )
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
    //            var year =  _self.cycleYear ? parseInt(_self.cycleYear.getValue()) : _self.preMonthDate.getFullYear() ;
    //            var month =  _self.cycleMonth ? (parseInt(_self.cycleMonth.getValue())-1) :  _self.preMonthDate.getMonth() ;
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
    ////createIsLeaveEarlier: function(tr){
    ////    var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : "早退"  }).inject(tr);
    ////    var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
    ////    this.isLeaveEarlier = new MDomItem( td, {
    ////        "name" : "isLeaveEarlier",
    ////        "type" : "select",
    ////        "selectValue" : ["-1","true","false"],
    ////        "selectText" : ["","早退","未早退"],
    ////    }, true, this.app );
    ////    this.isLeaveEarlier.load();
    ////},
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
    //        //var filterData = {
    //        //    cycleYear : this.cycleYear.getValue(),
    //        //    cycleMonth : this.cycleMonth.getValue()
    //        //}
    //        var result = this.form.getResult(false, null,false,true,false);
    //        var year = this.preMonthDate.getFullYear().toString();
    //        var month = (this.preMonthDate.getMonth()+1).toString();
    //        if( month.length == 1 )month = "0"+month;
    //        var filterData = {
    //            cycleYear : year,
    //            cycleMonth : month
    //        }
    //        if( this.isAbsent && this.isAbsent.getValue()!=""){
    //            filterData.isAbsent = this.getBoolean( this.isAbsent.getValue() );
    //        }
    //        if( this.isLeaveEarlier && this.isLeaveEarlier.getValue()!=""){
    //            filterData.isLeaveEarlier =  this.getBoolean( this.isLeaveEarlier.getValue());
    //        }
    //        if( this.isLate && this.isLate.getValue()!="" ){
    //            filterData.isLate =  this.getBoolean( this.isLate.getValue());
    //        }
    //        if( this.isLackOfTime && this.isLackOfTime.getValue()!=""){
    //            filterData.isLackOfTime =  this.getBoolean( this.isLackOfTime.getValue());
    //        }
    //        if( this.q_date && this.q_date.getValue()!="" ){
    //            filterData.q_date =  year + "-" + month + "-" + this.q_date.getValue();
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
    //        "type": "person",
    //        "title": "选择人员",
    //        "count" : "1",
    //        "onComplete": function(items){
    //            var names = [];
    //            items.each(function(item){
    //                names.push(item.data.name);
    //            }.bind(this));
    //            this.q_empName.setValue( names.join(",") )
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
        this.view = new MWF.xApplication.Attendance.MyDetail.View(this.elementContentNode, this.app,this );
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

MWF.xApplication.Attendance.MyDetail.SelfHoliday = new Class({
    Extends: MWF.xApplication.Attendance.MyDetail.Explorer,

    loadView : function( filterData ){
        this.elementContentNode.empty();
        if( this.view )delete this.view;
        this.view = new MWF.xApplication.Attendance.MyDetail.SelfHolidayView(this.elementContentNode, this.app,this );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    }

});


MWF.xApplication.Attendance.MyDetail.DetailStaticExplorer = new Class({
    Extends: MWF.xApplication.Attendance.MyDetail.Explorer,

    load: function(){
        //this.loadFilter();
        this.loadContentNode();
        this.setNodeScroll();

        var filterData = {
            cycleYear : this.preMonthDate.getFullYear().toString(),
            cycleMonth : this.preMonthDate.format(this.app.lp.dateFormatOnlyMonth)
        };
        this.loadView( filterData );
    },
    loadFilter : function(){
        this.fileterNode = new Element("div.fileterNode", {
            "styles" : this.css.fileterNode
        }).inject(this.node);

        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.filterTable, "class" : "filterTable"
        }).inject( this.fileterNode );
        table.setStyle("width","360px");
        var tr = new Element("tr").inject(table);

        this.createYearSelectTd( tr );
        this.createMonthSelectTd( tr );
        //this.createDateSelectTd( tr )
        this.createActionTd( tr )
    },
    createMonthSelectTd : function( tr ){
        var _self = this;
        var td = new Element("td", {  "styles" : this.css.filterTableTitle, "text" : MWF.xApplication.Attendance.LP.months  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        this.cycleMonth = new MDomItem( td, {
            "name" : "cycleMonth",
            "type" : "select",
            "selectValue" :["","01","02","03","04","05","06","07","08","09","10","11","12"],
            "event" : {
                "change" : function(){ if(_self.dateSelecterTd)_self.createDateSelectTd() }
            }
        }, true, this.app );
        this.cycleMonth.load();
    },
    createActionTd : function( tr ){
        var td = new Element("td", {  "styles" : this.css.filterTableValue }).inject(tr);
        var input = new Element("button",{
            "text" : MWF.xApplication.Attendance.LP.query,
            "styles" : this.css.filterButton
        }).inject(td);
        input.addEvent("click", function(){
            //var filterData = {
            //    cycleYear : this.cycleYear.getValue(),
            //    cycleMonth : this.cycleMonth.getValue()
            //}
            var year = this.preMonthDate.getFullYear().toString();
            var month = (this.preMonthDate.getMonth()+1).toString();
            if( month.length == 1 )month = "0"+month;
            var filterData = {
                cycleYear : year,
                cycleMonth : month
            };
            if( this.isAbsent  && this.isAbsent.getValue()!="" ){
                filterData.isAbsent =  this.isAbsent.getValue();
            }
            if( this.isLeaveEarlier   && this.isLeaveEarlier.getValue()!=""){
                filterData.isLeaveEarlier =  this.isLeaveEarlier.getValue();
            }
            if( this.isLate  && this.isLate.getValue()!="" ){
                filterData.isLate =  this.isLate.getValue();
            }
            if( this.q_date && this.q_date.getValue()!="" ){
                filterData.q_date =  year + "-" + month + "-" + this.q_date.getValue();
            }
            this.loadView( filterData );
        }.bind(this))
    },
    loadView : function( filterData ){
        this.elementContentNode.empty();
        if( this.view )delete this.view;
        this.view = new MWF.xApplication.Attendance.MyDetail.DetailStaticView(this.elementContentNode, this.app,this );
        this.view.filterData = filterData;
        this.view.listItemUrl = this.path+"listItem_detailStatic.json";
        this.view.load();
        this.setContentSize();
    }
});


MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticExplorer = new Class({
    Extends: MWF.xApplication.Attendance.MyDetail.Explorer,

    loadView : function( filterData ){
        this.elementContentNode.empty();
        if( this.view )delete this.view;
        this.view = new MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticView(this.elementContentNode, this.app,this );
        this.view.filterData = filterData;
        this.view.load();
        this.setContentSize();
    }
});


MWF.xApplication.Attendance.MyDetail.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.MyDetail.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!count)count=100;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = this.filterData || {};
        filter.key = this.sortField || this.sortFieldDefault || "";
        filter.order = this.sortType || this.sortTypeDefault || "";
        filter.q_empName = layout.desktop.session.user.distinguishedName;
        this.actions.listDetailFilterNext( id, count, filter, function(json){
            var data = json.data;
            data.each(function(d){
                d.APPEALABLE = this.explorer.config.APPEALABLE;
            }.bind(this));
            json.data = data;
            if( callback )callback(json);
        }.bind(this));
        //this.actions.listDetailFilterUser( filter, function(json){
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

MWF.xApplication.Attendance.MyDetail.SelfHolidayView = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.MyDetail.SelfHolidayDocument(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var filter = this.filterData || {};
        filter.q_empName = layout.desktop.session.user.distinguishedName;
        this.actions.listDetailFilterUser( filter, function(json){
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});


MWF.xApplication.Attendance.MyDetail.DetailStaticView = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.MyDetail.DetailStaticDocument(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var filter = this.filterData || {};
        filter.key = this.sortField || this.sortFieldDefault || "";
        filter.order = this.sortType || this.sortTypeDefault || "";
        filter.q_empName = layout.desktop.session.user.distinguishedName;
        //if( filter.cycleMonth == "" )filter.cycleMonth="(0)";
        var month = (new Date().getMonth()+1).toString();
        if( month.length == 1 )month = "0"+month;
        filter.cycleMonth = month;
        this.actions.listStaticMonthPerson( filter.q_empName, filter.cycleYear,filter.cycleMonth, function(json){

            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});

MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticView = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticDocument(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        var filter = this.filterData || {};
        this.actions.listDetailFilterUser( filter, function(json){
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){

    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});

MWF.xApplication.Attendance.MyDetail.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document,
    appeal :function(){

        if(this.explorer.configSetting.APPEAL_AUDIFLOWTYPE.configValue=="BUILTIN"){
            var form = new MWF.xApplication.Attendance.MyDetail.Appeal( this.explorer, this.data );
            form.create();
        }else{
            this.loadProcess(this.explorer.configSetting.APPEAL_AUDIFLOW_ID.configValue,{record:this.data} ,null);
        }

    },
    loadProcess: function(id, processData, latest){

        this.getProcess(id, function(process){
            MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
                var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, this.app, {
                    "latest" : latest,
                    "workData" : processData,
                    "onStarted": function(data, title, processName){
                        this.afterStartProcess(data, title, processName);
                    }.bind(this)
                });
                starter.load();
            }.bind(this));
        }.bind(this));
    },
    afterStartProcess: function(data, title, processName){
        var workInfors = [];
        var currentTask = [];
        data.each(function(work){
            if (work.currentTaskIndex !== -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
            workInfors.push(this.getStartWorkInforObj(work));
        }.bind(this));

        if (currentTask.length===1){
            var options = {"workId": currentTask[0], "appId": currentTask[0]};
            this.app.desktop.openApplication(null, "process.Work", options);

            this.createStartWorkResault(workInfors, title, processName, false);
        }else{
            this.createStartWorkResault(workInfors, title, processName, true);
        }
    },
    createStartWorkResault: function(workInfors, title, processName, isopen){
        var content = "";
        workInfors.each(function(infor){
            var users = [];
            infor.users.each(function(uname){
                users.push(MWF.name.cn(uname));
            });

            content += "<div><b>"+this.app.lp.nextActivity+"<font style=\"color: #ea621f\">"+infor.activity+"</font>, "+this.app.lp.nextUser+"<font style=\"color: #ea621f\">"+users.join(", ")+"</font></b>";
            if (infor.currentTask && isopen){
                content += "&nbsp;&nbsp;&nbsp;&nbsp;<span value=\""+infor.currentTask+"\">"+this.app.lp.deal+"</span></div>";
            }else{
                content += "</div>";
            }
        }.bind(this));

        var t = workInfors[0].title || title;
        var msg = {
            "subject": this.app.lp.processStarted,
            "content": "<div>"+this.app.lp.processStartedMessage+"“["+processName+"]"+t+"”</div>"+content
        };

        var tooltip = layout.desktop.message.addTooltip(msg);
        var item = layout.desktop.message.addMessage(msg);

        this.setStartWorkResaultAction(tooltip);
        this.setStartWorkResaultAction(item);
    },
    getStartWorkInforObj: function(work){
        var title = "";
        var users = [];
        var currentTask = "";
        work.taskList.each(function(task, idx){
            title = task.title;
            users.push(task.person+"("+task.department + ")");
            if (work.currentTaskIndex===idx) currentTask = task.id;
        }.bind(this));
        return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask, "title" : title };
    },
    setStartWorkResaultAction: function(item){
        var node = item.node.getElements("span");
        node.setStyles(this.css.dealStartedWorkAction);
        var _self = this;
        node.addEvent("click", function(e){
            var options = {"taskId": this.get("value"), "appId": this.get("value")};
            _self.app.desktop.openApplication(e, "process.Work", options);
        });
    },
    getProcess: function(id, callback){
        this.action = new o2.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
        this.action.actions = {"getProces": {"uri": "/jaxrs/process/{id}/complex"}};
        this.action.invoke({"name": "getProces", "async": false, "parameter": {"id": id}, "success": function(json){
                if (callback) callback(json.data);
            }.bind(this)});
    },
    seeAppeal : function(){

        if(this.data.appealInfos[0].appealAuditInfo){
            if(this.data.appealInfos[0].appealAuditInfo.workId){
                var workid = this.data.appealInfos[0].appealAuditInfo.workId;
                var options = {"workId":workid, "appId": "process.Work"+workid};
                this.app.desktop.openApplication(null, "process.Work", options);
                return;
            }
        }
        var form = new MWF.xApplication.Attendance.MyDetail.Appeal( this.explorer, this.data );
        form.open();
    }
});

MWF.xApplication.Attendance.MyDetail.SelfHolidayDocument = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});


MWF.xApplication.Attendance.MyDetail.DetailStaticDocument = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

MWF.xApplication.Attendance.MyDetail.SelfHolidayStaticDocument = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

MWF.xApplication.Attendance.MyDetail.Appeal = new Class({
    Extends: MWF.widget.Common,
    initialize: function( explorer, detailData ){
        this.explorer = explorer;
        this.app = explorer.app;
        this.detailData = detailData;
        this.css = this.explorer.css;

        this.load();
    },
    load: function(){

    },

    open: function(e){
        this.isNew = false;
        this.isEdited = false;
        this.app.restActions.getAppeal(this.detailData.id, function(json){
            this.data = json.data;
            this.data.onDutyTime = this.detailData.onDutyTime;
            this.data.offDutyTime = this.detailData.offDutyTime;
        }.bind(this),null,false);
        if(!this.data)this.data = this.detailData || {};
        this._open();
    },
    create: function(){
        this.isNew = true;
        this.data = this.detailData || {};
        this._open();
    },
    edit: function(){
        this.isEdited = true;
        this.app.restActions.getAppeal(this.detailData.id, function(json){
            this.data = json.data;
            this.data.onDutyTime = this.detailData.onDutyTime;
            this.data.offDutyTime = this.detailData.offDutyTime;
        }.bind(this),null,false);
        if(!this.data)this.data = this.detailData || {};
        this._open();
    },
    _open : function(){
        this.createMarkNode = new Element("div", {
            "styles": this.css.createMarkNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.app.content, "after");

        this.createAreaNode = new Element("div", {
            "styles": this.css.createAreaNode
        });

        this.createNode();

        this.createAreaNode.inject(this.createMarkNode, "after");
        this.createAreaNode.fade("in");

        this.setCreateNodeSize();
        this.setCreateNodeSizeFun = this.setCreateNodeSize.bind(this);
        this.addEvent("resize", this.setCreateNodeSizeFun);
    },
    createNode: function(){
        var appLp = MWF.xApplication.Attendance.LP;
        var _self = this;

        this.createNode = new Element("div", {
            "styles": this.css.createNode
        }).inject(this.createAreaNode);

        this.createContainerNode = new Element("div", {
            "styles": this.css.createContainerNode
        }).inject(this.createNode);


        this.setScrollBar( this.createContainerNode );

        this.createIconNode = new Element("div", {
            "styles": this.isNew ? this.css.createNewNode : this.css.createIconNode
        }).inject(this.createContainerNode);


        this.createFormNode = new Element("div", {
            "styles": this.css.createFormNode
        }).inject(this.createContainerNode);

        this.createTableContainer = new Element("div", {
            "styles": this.css.createTableContainer
        }).inject(this.createFormNode);

        this.createTableArea = new Element("div", {
            "styles": this.css.createTableArea
        }).inject(this.createTableContainer);

        var d = this.data;
        var status = [];
        if(d.isGetSelfHolidays && ( d.selfHolidayDayTime == appLp.wholeDay || d.selfHolidayDayTime == "全天")) {
            status.push( appLp.levelAsked )
        }else if( d.isAbnormalDuty && (d.abnormalDutyDayTime == appLp.wholeDay || d.abnormalDutyDayTime == "全天")){
            status.push( appLp.abNormalDuty )
        }else if(d.isLackOfTime  ) {
            status.push( appLp.lackOfTime )
        }else{
            if( d.isGetSelfHolidays && ( d.selfHolidayDayTime == appLp.am || d.selfHolidayDayTime == "上午" ) ){
                status.push( appLp.levelAsked )
            }else if(d.isLate){
                status.push( appLp.late )
            }else if(d.isAbsent && ["上午","全天",appLp.am, appLp.wholeDay].contains(d.absentDayTime) ){
                status.push( appLp.absent )
            }else if( d.isAbnormalDuty && ["上午","全天",appLp.am, appLp.wholeDay].contains(d.abnormalDutyDayTime)){
                status.push( appLp.abNormalDuty )
            }
            if( d.isGetSelfHolidays && ( d.selfHolidayDayTime == "下午" || d.selfHolidayDayTime == appLp.pm ) ){
                status.push( appLp.levelAsked );
                //}else if(d.isLeaveEarlier){
                //    status.push( '早退')
            }else if(d.isAbsent && ( d.absentDayTime == "下午" || d.absentDayTime == appLp.pm ) ){
                status.push( appLp.abNormalDuty)
            }else if(d.isAbnormalDuty && ( d.abnormalDutyDayTime == "下午" || d.abnormalDutyDayTime == appLp.pm ) ){
                status.push( appLp.abNormalDuty )
            }
        }
        this.data.statusShow = status.unique().join();

        var appealStatus = appLp.draft;
        if (d.status == 0 ) {
            appealStatus = appLp.todo
        } else if (d.status == 1) {
            appealStatus = appLp.approve
        } else if (d.status == -1) {
            appealStatus = appLp.deny
        }
        //if (d.appealStatus == 1) {
        //    appealStatus = "申诉中"
        //} else if (d.appealStatus == -1) {
        //    appealStatus = "申诉未通过"
        //} else if (d.appealStatus == 9) {
        //    appealStatus = "申诉通过"
        //}
        this.data.appealStatusShow = appealStatus;

        //var auditors = this.getAuditor();
        var identityList = this.getIdentity();

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>"+
            "<tr><td colspan='4' styles='formTableHead'>"+appLp.apealApplyForm+"</td></tr>" +
            "<tr><td styles='formTableTitle'>"+appLp.employeeName+"</td>"+
            "    <td styles='formTableValue'>"+this.data.empName.split("@")[0]+"</td>" +
            "    <td styles='formTableTitle' lable='recordDateString'></td>"+
            "    <td styles='formTableValue' item='recordDateString'></td></tr>"
            +"<tr><td styles='formTableTitle' lable='onDutyTime'></td>"+
            "    <td styles='formTableValue' item='onDutyTime'></td>" +
            ((this.data.signProxy=="2"||this.data.signProxy=="3")?
                    "    <td styles='formTableTitle' lable='morningOffDutyTime'></td>"+
                    "    <td styles='formTableValue' item='morningOffDutyTime'></td></tr>" +
                    "<tr><td styles='formTableTitle' lable='afternoonOnDutyTime'></td>"+
                    "    <td styles='formTableValue' item='afternoonOnDutyTime'></td>" : ""
            ) +
            "    <td styles='formTableTitle' lable='offDutyTime'></td>"+
            "    <td styles='formTableValue' item='offDutyTime'></td></tr>" +
            ( ( this.isNew && identityList.identities.length > 1 ) ?
                    "<tr><td styles='formTableTitle' lable='identity'></td>"+
                    "    <td styles='formTableValue' item='identity'  colspan='3'></td></tr>" : ""
            ) +
            ( this.isNew ?
                    "<tr><td styles='formTableTitle' lable='statusShow'></td>"+
                    "    <td styles='formTableValue' item='statusShow'></td>" +
                    "    <td styles='formTableTitle' lable='appealStatusShow'></td>"+
                    "    <td styles='formTableValue' item='appealStatusShow'></td></tr>"
                    :
                    "<tr><td styles='formTableTitle' lable='appealStatusShow'></td>"+
                    "    <td styles='formTableValue' item='appealStatusShow'  colspan='3'></td></tr>"
            )
            +
            "<tr><td styles='formTableTitle' lable='appealReason'></td>"+
            "    <td styles='formTableValue' item='appealReason' colspan='3'></td></tr>" +
            "<tr contain='selfHolidayType'><td styles='formTableTitle' lable='selfHolidayType'></td>"+
            "    <td styles='formTableValue' item='selfHolidayType' colspan='3'></td></tr>" +
            "<tr contain='address'><td styles='formTableTitle' lable='address'></td>"+
            "    <td styles='formTableValue' item='address' colspan='3'></td></tr>" +
            "<tr contain='startTime'><td styles='formTableTitle' lable='startTime'></td>"+
            "    <td styles='formTableValue' item='startTime' colspan='3'></td></tr>" +
            "<tr contain='endTime'><td styles='formTableTitle' lable='endTime'></td>"+
            "    <td styles='formTableValue' item='endTime' colspan='3'></td></tr>" +
            "<tr contain='appealDescription'><td styles='formTableTitle' lable='appealDescription'></td>"+
            "    <td styles='formTableValue' item='appealDescription' colspan='3'></td></tr>" +
            "</table>";
        this.createTableArea.set("html",html);
        var lp = this.app.lp.schedule;
        var signProxy = this.data.signProxy||1;
        this.document = new MForm( this.createTableArea, this.data, {
            style : "popup",
            isEdited : this.isEdited || this.isNew,
            itemTemplate : {
                recordDateString : { text: appLp.recordDate,  type : "innertext"},
                onDutyTime : { text:appLp.onDutyTime,  type : "innertext"},
                morningOffDutyTime : { text:signProxy==1?"":lp.signProxy[signProxy].middayRestStartTime,  type : "innertext"},
                afternoonOnDutyTime : { text:signProxy==1?"":lp.signProxy[signProxy].middayRestEndTime,  type : "innertext"},
                offDutyTime : { text: appLp.offDutyTime,  type : "innertext"},
                statusShow : {  text: appLp.attendanceStatus, type : "innertext" },
                appealStatusShow : { text: appLp.appealStatus, type : "innertext"},
                //processPerson1 : {
                //    text : "审核人", type : "select", selectValue : auditors, selectText : function(){
                //        var array = [];
                //        auditors.each( function( a ){ array.push(a.split("@")[0] ) } );
                //        return array;
                //    }
                //},
                appealReason : {
                    notEmpty : true,
                    text: appLp.appealReason,
                    type : "select",
                    selectValue : appLp.appealReasonSelectText,
                    event : { change : function(mdi){
                            _self.switchFieldByAppealReason(mdi.getValue());
                        }}
                },
                identity : {
                    notEmpty : true,
                    text: appLp.selectDepartment,
                    type : "radio",
                    defaultValue : function(){
                        return identityList.identities[0];
                    }.bind(this),
                    selectText : identityList.units,
                    selectValue : identityList.identities
                },
                address : { text: appLp.address },
                selfHolidayType : {
                    text: appLp.leaveType,
                    type : "select",
                    selectValue : appLp.leaveTypeSelectText
                },
                startTime : {  text: appLp.startTime, tType : "datetime" },
                endTime : {  text: appLp.endTime, tType : "datetime" },
                appealDescription : { text:appLp.appealDescriptoin }
            }
        }, this.app,this.css);
        this.document.load();
        _self.switchFieldByAppealReason(this.data.appealReason);

        //createFormNode.set("html", html);

        //this.setScrollBar(this.createTableContainer)


        this.cancelActionNode = new Element("div", {
            "styles": this.css.createCancelActionNode,
            "text": appLp.cancel
        }).inject(this.createFormNode);
        this.cancelActionNode.addEvent("click", function(e){
            this.cancelCreate(e);
        }.bind(this));

        if( this.isNew || this.isEdited ){
            this.createOkActionNode = new Element("div", {
                "styles": this.css.createOkActionNode,
                "text": appLp.ok
            }).inject(this.createFormNode);
            this.createOkActionNode.addEvent("click", function(e){
                this.okCreate(e);
            }.bind(this));
        }
    },
    switchFieldByAppealReason : function( ar ){
        var lp = MWF.xApplication.Attendance.LP;
        var tempField = ["selfHolidayType","startTime","endTime","address","appealDescription"];
        var showField = [];
        if( ar == lp.temporaryLeave ){
            showField = ["selfHolidayType","startTime","endTime"];
        }else if( ar == lp.out ){
            showField = ["address","startTime","endTime"];
        }else if( ar == lp.businessTrip ){
            showField = ["address","startTime","endTime","appealDescription"];
        }else if( ar == lp.other ){
            showField = ["appealDescription"];
        }
        tempField.each( function( f ){
            this.createTableArea.getElement("[contain='"+f+"']").setStyle("display", showField.contains(f) ? "" : "none" );
            if( this.isNew || this.isEdited )this.document.items[f].options.notEmpty = (showField.contains(f) ? true : false )
        }.bind(this))
    },
    getIdentity : function(){
        var identityList = { identities : [], units : [] };
        this.app.personActions.getPerson( function( json ){
            json.data.woIdentityList.each( function( id ){
                var unit = id.woUnit;
                identityList.identities.push( id.distinguishedName );
                identityList.units.push( unit.name );
            }.bind(this))
        }.bind(this), null, false );
        return identityList;
    },
    getAuditor : function(){
        var lp = MWF.xApplication.Attendance.LP;
        //获取设置
        var setting = {};
        var result = [];
        this.app.restActions.listSetting(function(json){
            json.data.each(function( d ){
                setting[d.configCode] = d;
            }.bind(this))
        }.bind(this),null,false);
        if( setting.APPEAL_AUDITOR_TYPE && setting.APPEAL_AUDITOR_TYPE.configValue!="" && setting.APPEAL_AUDITOR_VALUE && setting.APPEAL_AUDITOR_VALUE.configValue!=""){
            if( setting.APPEAL_AUDITOR_TYPE.configValue == lp.reportTo || setting.APPEAL_AUDITOR_TYPE.configValue == "汇报对象" ) {
                var d = {"personList": [layout.desktop.session.user.distinguishedName] };
                this.app.orgActions.listPersonSupDirectValue( d, function( json ){
                    var superior = json.data.personList;
                    if( !superior || !superior[0] ){
                        this.app.notice( lp.noReportToNotice, "error");
                    }else{
                        var p = superior[0];
                        if( p.split("@")[ p.split("@").length - 1].toLowerCase() == "i"  ){
                            result.push( this.getPersonByIdentity( p ) )
                        }else{
                            result.push( p )
                        }
                    }
                }.bind(this), null, false );
            }else if( setting.APPEAL_AUDITOR_TYPE.configValue == "所属部门职位" || setting.APPEAL_AUDITOR_TYPE.configValue == lp.unitDuty ){
                this.app.personActions.getPerson( function( json ){
                    json.data.woIdentityList.each( function( id ){
                        var unit = id.woUnit;
                        var d = {"name": setting.APPEAL_AUDITOR_VALUE.configValue, "unit": unit.distinguishedName};
                        this.app.orgActions.getDutyValue( d, function( js ){
                            var ids = js.data ? js.data.identityList : [];
                            if ( typeOf( ids ) == "array" && ids[0] ) {
                                ids.each( function( id  ){
                                    result = result.concat( this.getPersonByIdentity( id ) );
                                }.bind(this));
                            }else{
                                var text = lp.noUnitDutyNotice.replace("{unit}",unit.name).replace("{duty}",setting.APPEAL_AUDITOR_VALUE.configValue);
                                this.app.notice( text, "error");
                                // this.app.notice("系统中没有配置"+unit.name+"的"+setting.APPEAL_AUDITOR_VALUE.configValue+"职位，请联系管理员", "error");
                            }
                        }.bind(this),null ,false)
                    }.bind(this))
                }.bind(this), null, false );
            }else if( setting.APPEAL_AUDITOR_TYPE.configValue == "人员属性" || setting.APPEAL_AUDITOR_TYPE.configValue == lp.personAttribute ){
                this.app.personActions.getPerson( function( json ){
                    var attribute = setting.APPEAL_AUDITOR_VALUE.configValue;
                    json.data.woPersonAttributeList.each( function( attr ){
                        if( attr.name == attribute ){
                            var p = attr.attributeList[0];
                            if( p ){
                                if( p.split("@")[ p.split("@").length - 1].toLowerCase() == "i"  ){
                                    result.push( this.getPersonByIdentity( p ) )
                                }else{
                                    result.push( p )
                                }
                            }
                        }
                    })
                }.bind(this),null ,false);
                if( result.length == 0 ){
                    var text = lp.noPersonAttribute.replace("{att}", setting.APPEAL_AUDITOR_VALUE.configValue);
                    this.app.notice( text, "error");
                }
            }else if( setting.APPEAL_AUDITOR_TYPE.configValue == "指定人" || setting.APPEAL_AUDITOR_TYPE.configValue == lp.assignedPerson){
                var p = setting.APPEAL_AUDITOR_TYPE.configValue;
                if( p.split("@")[ p.split("@").length - 1].toLowerCase() == "i"  ){
                    result.push( this.getPersonByIdentity( p ) )
                }else{
                    result.push( p )
                }
            }
        }else{
            this.app.personActions.getPerson( function( json ){
                var attribute = lp.directLeader;
                json.data.woPersonAttributeList.each( function( attr ){
                    if( attr.name == attribute ){
                        var p = attr.attributeList[0];
                        if( p ){
                            if( p.split("@")[ p.split("@").length - 1].toLowerCase() == "i"  ){
                                result.push( this.getPersonByIdentity( p ) )
                            }else{
                                result.push( p )
                            }
                        }
                    }
                })
            }.bind(this),null ,false);
            this.app.notice(lp.noDirectLeader, "error");
        }
        return result;
    },
    getPersonByIdentity : function( identity ){
        var d = {"identityList":[ identity ]};
        var result = [];
        this.app.orgActions.listPersonWithIdentityValue( d, function (js){
            result = js.data.personList;
        }.bind(this), null, false);
        return result;
    },
    setCreateNodeSize: function(){
        var size = this.app.node.getSize();
        var allSize = this.app.content.getSize();

        var height = "580";
        var width = "800";

        this.createAreaNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+size.y+"px"
        });
        var hY = height;
        var mY = (size.y-height)/2;
        this.createNode.setStyles({
            "height": ""+hY+"px",
            "margin-top": ""+mY+"px",
            "width" : ""+width+"px"
        });

        this.createContainerNode.setStyles({
            "height": ""+hY+"px"
        });

        var iconSize = this.createIconNode ? this.createIconNode.getSize() : {x:0,y:0};
        var formMargin = hY-iconSize.y-20;
        this.createFormNode.setStyles({
            "height": ""+formMargin+"px",
            "margin-top": ""+20+"px"
        });
    },
    cancelCreate: function(e){
        this.createMarkNode.destroy();
        this.createAreaNode.destroy();
        delete this;
    },
    okCreate: function(e){
        var data = this.document.getResult(true,",",true,false,true);
        if (data ) {
            var start = data.startTime;
            var end = data.endTime;
            if (start != "" && end != "") {
                var starTime = new Date(start.replace(/-/g, "/"));
                var endTime = new Date(end.replace(/-/g, "/"));
                if (starTime >= endTime) {
                    this.app.notice( MWF.xApplication.Attendance.LP.holiday.beginGreateThanEndNotice, "error");
                    return;
                }
            }
            this._ok( data )
        }
        //}else{
        //    this.app.notice( "请填写申诉原因和具体描述", "error");
        //}
    },
    _ok: function( data ){
        this.app.restActions.createAppeal(this.data.id, data, function (json) {
            if (json.type == "ERROR") {
                this.app.notice(json.message, "error");
            } else {
                this.createMarkNode.destroy();
                this.createAreaNode.destroy();
                if (this.explorer.view)this.explorer.view.reload();
                this.app.notice( MWF.xApplication.Attendance.LP.createAppealNotice, "success");
            }
        }.bind(this));
    },
    selectDateTime : function( el, timeOnly, isTme, baseDate ){
        var opt = {
            "style": "xform",
            "timeOnly": timeOnly,
            "isTime": isTme,
            "target": this.app.content
        };
        if( baseDate )opt.baseDate = baseDate;
        MWF.require("MWF.widget.Calendar", function(){
            var calendar = new MWF.widget.Calendar( el, opt );
            calendar.show();
        }.bind(this));
    },
    //getProcessPerson : function( callback ){
    //    this.getUnitByPerson( function( unitData ){
    //        this.app.restActions.listPermission( function(json){
    //            if( json.data.length == 0 ){
    //                this.app.notice( "系统未配置考勤员，请联系管理员！", "error");
    //                return;
    //            }
    //            var unitManager, topUnitManager, processer;
    //            json.data.each( function( d ){
    //                if( d.unitName == unitData.name  ){
    //                    unitManager = d.adminName;
    //                }
    //                if(d.unitName == unitData.topUnit ){
    //                    topUnitManager = d.adminName;
    //                }
    //            })
    //            processer = unitManager || topUnitManager;
    //            if( !processer ){
    //                this.app.notice( "未设置您所在部门和公司的考勤管理员，请联系系统管理员！", "error");
    //            }else{
    //                if(callback)callback(processer)
    //            }
    //        }.bind(this))
    //    }.bind(this));
    //},
    getUnitByPerson : function( callback ){
        var data = {"personList": [layout.desktop.session.user.distinguishedName] };
        this.app.orgActions.listUnitWithPerson( function( json ){
            if( json.data.length > 0 ){
                if(callback)callback( json.data );
            }else{
                this.app.notice( MWF.xApplication.Attendance.LP.noDepartment, "error");
            }
        }.bind(this), function(){
            this.app.notice( MWF.xApplication.Attendance.LP.noDepartment, "error");
        }.bind(this), data, false )
    }
});
