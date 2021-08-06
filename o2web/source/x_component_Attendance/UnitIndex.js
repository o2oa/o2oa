MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Attendance", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Attendance", "Common", null, false);

MWF.xApplication.Attendance.UnitIndex = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    statusColor : {
        "normal" : "#4A90E2", //绿色，正常
        "levelAsked":"#2BC497", //蓝色，请假
        "late":"#F5A623", //黄色，迟到
        //"leaveEarly":"#fe8d03", //橙色，早退
        "noSign":"#FF8080", //粉红色,未签到
        "lackOfTime" : "#AC71E3",//工时不足人次
        "abNormalDuty" : "#8B572A"//异常打卡人次
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "../x_component_Attendance/$UnitIndex/";
        this.cssPath = "../x_component_Attendance/$UnitIndex/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.setDate();

        this.today = new Date();

        this.userName = layout.desktop.session.user.distinguishedName;
        this.data = {};
    },
    setDate : function( date ){
        this.date = date || new Date();
        this.year = this.date.getFullYear().toString();
        var month = this.date.getMonth()+1;
        this.month = month.toString().length == 2 ? month : "0"+month;
    },
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){
        this.loadTitleNode();
        this.loadContent();
    },
    loadTitleNode : function(){
        var text = this.date.format(this.app.lp.dateFormatMonth);

        this.titleNode = new Element("div.titleNode",{
            "styles" : this.css.titleNode
        }).inject(this.node);

        this.titleLeftArrowNode = new Element("div",{
            "styles" : this.css.titleLeftArrowNode
        }).inject(this.titleNode);
        this.titleTextNode = new Element("div",{
            "styles" : this.css.titleTextNode,
            "text" : text
        }).inject(this.titleNode);
        this.titleRightArrowNode = new Element("div",{
            "styles" : this.css.titleRightArrowNode
        }).inject(this.titleNode);

        this.titleLeftArrowNode.addEvents({
            "mouseover": function(){this.titleLeftArrowNode.setStyles(this.css.titleLeftArrowNode_over);}.bind(this),
            "mouseout": function(){this.titleLeftArrowNode.setStyles(this.css.titleLeftArrowNode);}.bind(this),
            "mousedown": function(){this.titleLeftArrowNode.setStyles(this.css.titleLeftArrowNode_down);}.bind(this),
            "mouseup": function(){this.titleLeftArrowNode.setStyles(this.css.titleLeftArrowNode_over);}.bind(this),
            "click": function(){this.changeMonthPrev();}.bind(this)
        });
        this.titleRightArrowNode.addEvents({
            "mouseover": function(){this.titleRightArrowNode.setStyles(this.css.titleRightArrowNode_over);}.bind(this),
            "mouseout": function(){this.titleRightArrowNode.setStyles(this.css.titleRightArrowNode);}.bind(this),
            "mousedown": function(){this.titleRightArrowNode.setStyles(this.css.titleRightArrowNode_down);}.bind(this),
            "mouseup": function(){this.titleRightArrowNode.setStyles(this.css.titleRightArrowNode_over);}.bind(this),
            "click": function(){this.changeMonthNext();}.bind(this)
        });
        this.titleTextNode.addEvents({
            "mouseover": function(){this.titleTextNode.setStyles(this.css.titleTextNode_over);}.bind(this),
            "mouseout": function(){this.titleTextNode.setStyles(this.css.titleTextNode);}.bind(this),
            "mousedown": function(){this.titleTextNode.setStyles(this.css.titleTextNode_down);}.bind(this),
            "mouseup": function(){this.titleTextNode.setStyles(this.css.titleTextNode_over);}.bind(this),
            "click": function(){this.changeMonthSelect();}.bind(this)
        });

        this.titleUnitArea = new Element("div.titleUnitArea",{
            "style" : "float:left"
        }).inject(this.titleNode);

        this.loadUnitNode();
    },
    changeMonthPrev: function(){
        this.date.decrement("month", 1);
        this.setDate( this.date );
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reloadContent();
    },
    changeMonthNext: function(){
        this.date.increment("month", 1);
        this.setDate( this.date );
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reloadContent();
    },
    changeMonthSelect: function(){
        if (!this.monthSelector) this.createMonthSelector();
        this.monthSelector.show();
    },
    createMonthSelector: function(){
        this.monthSelector = new MWF.xApplication.Attendance.MonthSelector(this.date, this);
    },
    changeMonthTo: function(d){
        this.setDate( d )
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reloadContent();
    },
    changeUnitTo : function( d ){
        this.unit = d;
        this.titleUnitActionTextNode.set("text", d.split("@")[0]);
        this.reloadContent();
    },
    loadUnitNode: function(){
        debugger;
        this.listUnitWithPerson( function( unitList ){
            this.unit = unitList[0] || "";
            this.units = unitList;
            var flag = true;
            if( this.app.isTopUnitManager() ){
                var data = {"unitList": this.app.getNameFlag( this.app.manageTopUnits )};
                this.app.orgActions.listUnitSubDirect( function( json ){
                    json.data.each(function( d ){
                        this.units.push( d.distinguishedName )
                    }.bind(this))
                }.bind(this), null , data, false )
            }else if( this.app.isUnitManager() ){
                this.units = this.app.manageUnits;
            }
            this.units = this.units.unique();
            this.unit = this.units[0] || this.unit;
            if( this.units.length > 1 ){ //(this.units.length==1 && this.units[0]!=this.unit )
                this.titleUnitAreaNode = new Element("div.titleUnitAreaNode",{
                    "styles" : this.css.titleUnitAreaNode
                }).inject(this.titleUnitArea)

                this.titleUnitActionNode = new Element("div",{
                    "styles" : this.css.titleUnitActionNode
                }).inject(this.titleUnitAreaNode)

                this.titleUnitActionTextNode = new Element("div.titleUnitActionTextNode",{
                    "styles" : this.css.titleUnitActionTextNode,
                    "text" : this.unit.split("@")[0]
                }).inject(this.titleUnitActionNode);

                this.titleUnitActionIconNode = new Element("div",{
                    "styles" : this.css.titleUnitActionIconNode
                }).inject(this.titleUnitActionNode);

                this.titleUnitActionNode.addEvents({
                    "mouseover": function(){
                        this.titleUnitActionTextNode.setStyles(this.css.titleUnitActionTextNode_over);
                        this.titleUnitActionIconNode.setStyles(this.css.titleUnitActionIconNode_over);
                    }.bind(this),
                    "mouseout": function(){
                        this.titleUnitActionTextNode.setStyles(this.css.titleUnitActionTextNode);
                        this.titleUnitActionIconNode.setStyles(this.css.titleUnitActionIconNode);
                    }.bind(this),
                    "click" : function( ev ){
                        this.switchUnit( ev.target );
                        ev.stopPropagation();
                    }.bind(this)
                })
            }else{
                this.titleUnitNode = new Element("div",{
                    "styles" : this.css.titleUnitNode,
                    "text" : this.unit.split("@")[0]
                }).inject(this.titleUnitArea);
            }
        }.bind(this) )
    },
    listUnitWithPerson : function( callback ){
        var data = {"personList": this.app.getNameFlag(this.userName)};
        this.app.orgActions.listUnitWithPerson( function( json ){
            var unitList = [];
            json.data.each(function(d){
                unitList.push( d.distinguishedName );
            });
            if(callback)callback(unitList);
            // if( json.data.length > 0 ){
            //     if(callback)callback( json.data[0].distinguishedName );
            // }else{
            //     if(callback)callback([]);
            // }
        }.bind(this), null, data , false )
    },
    switchUnit : function( el ){
        var _self = this;
        var node = this.titleUnitListNode;
        var parentNode = el.getParent();
        if(node){
            if(  node.getStyle("display") == "block" ){
                node.setStyle("display","none");
            }else{
                node.setStyle("display","block");
                node.position({
                    relativeTo: this.titleUnitActionNode,
                    position: 'bottomCenter',
                    edge: 'upperCenter'
                });
            }
        }else{
            node = this.titleUnitListNode = new Element("div",{
                "styles" :  this.css.titleUnitListNode
            }).inject(this.node);
            this.app.content.addEvent("click",function(){
                _self.titleUnitListNode.setStyle("display","none");
            });
            this.units.each(function( d ){
                var dNode = new Element("div",{
                    "text" : d.split('@')[0],
                    "styles" : this.css.titleUnitSelectNode
                }).inject(node);
                dNode.store("unit", d );
                dNode.addEvents({
                    "mouseover" : function(){ this.setStyles(_self.css.titleUnitSelectNode_over); },
                    "mouseout" : function(){  this.setStyles(_self.css.titleUnitSelectNode); },
                    "click" : function(e){
                        _self.titleUnitListNode.setStyle("display","none");
                        this.setStyles(_self.css.titleUnitSelectNode);
                        _self.changeUnitTo( this.retrieve("unit") );
                        e.stopPropagation();
                    }
                })
            }.bind(this));
            node.position({
                relativeTo: this.titleUnitActionNode,
                position: 'bottomCenter',
                edge: 'upperCenter'
            });
        }
    },
    reloadContent : function(){
        this.pieChartArea.empty();
        this.barChartArea.empty();
        // this.lineChartArea.empty();
        this.loadData(function(){
            this.loadStatusColorNode();
            this.loadPieChart();
            this.loadBarChart();
        }.bind(this));
        this.loadDetail();
    },
    loadContent : function(){
        this.loadContentNode();
        this.loadData(function(){
            this.loadStatusColorNode();
            this.loadPieChart();
            this.loadBarChart();
        }.bind(this))
        this.loadDetail();
        // this.setNodeScroll();
        this.setContentSize();
    },
    reloadChart : function(){
        this.pieChartArea.empty();
        this.barChartArea.empty();
        // this.lineChartArea.empty();
        this.loadPieChart();
        this.loadBarChart();
    },
    loadContentNode: function(){
        this.elementContentNode = new Element("div.elementContentNode", {
            "styles": this.css.elementContentNode
        }).inject(this.node);
        this.app.addEvent("resize", function(){
            this.setContentSize();
            this.reloadChart();
        }.bind(this));

        this.elementContentListNode = new Element("div.elementContentListNode", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.topContentArea = new Element("div.topContentArea",{
            "styles" : this.css.topContentArea
        }).inject(this.elementContentListNode);

        this.topLeftArea = new Element("div.topLeftArea",{
            "styles" : this.css.topLeftArea
        }).inject(this.topContentArea);

        this.topLeftTitleNode = new Element("div.topLeftTitleNode",{
            "styles" : this.css.topLeftTitleNode,
            "text": this.lp.attendanceSummary
        }).inject(this.topLeftArea);

        this.topLeftContentNode = new Element("div.topLeftContentNode",{
            "styles" : this.css.topLeftContentNode
        }).inject(this.topLeftArea);

        this.statusColorArea = new Element("div.statusColorArea",{
            "styles" : this.css.statusColorArea
        }).inject(this.topLeftContentNode)

        this.pieChartArea = new Element("div.pieChartArea",{
            "styles" : this.css.pieChartArea
        }).inject(this.topLeftContentNode)

        this.topRightArea = new Element("div.topRightArea",{
            "styles" : this.css.topRightArea
        }).inject(this.topContentArea);

        this.topRightTitleNode = new Element("div.topRightTitleNode",{
            "styles" : this.css.topRightTitleNode,
            "text": this.lp.attendanceTrend
        }).inject(this.topRightArea);

        this.topRightContentNode = new Element("div.topRightContentNode",{
            "styles" : this.css.topRightContentNode
        }).inject(this.topRightArea);

        this.barChartArea = new Element("div.barChartArea",{
            "styles" : this.css.barChartArea
        }).inject(this.topRightContentNode)

        // this.middleContentArea = new Element("div.middleContentArea",{
        //     "styles" : this.css.middleContentArea
        // }).inject(this.elementContentListNode)
        //
        // this.lineChartArea = new Element("div.lineChartArea",{
        //     "styles" : this.css.lineChartArea
        // }).inject(this.middleContentArea)

        this.bottomContentArea = new Element("div.bottomContentArea",{
            "styles" : this.css.bottomContentArea
        }).inject(this.elementContentListNode)

        this.detailArea = new Element("div.detailArea",{
            "styles" : this.css.detailArea
        }).inject(this.bottomContentArea)

    },
    loadData : function( callback, unit, year, month, async ){
        if( !unit  )unit = this.unit;
        if( !year )year = this.year;
        if( !month )month = this.month;
        if( this.data[ unit + year + month ] ) {
            if(callback)callback();
        }else{
            this.actions.listStaticMonthUnitSum( unit, year, month, function( json ){
                var d = json.data || {};
                var data = this.data[ unit + year + month ] = {};
                var totals = data.totalData = {
                    levelAsked : d.onSelfHolidayCount || 0,
                    noSign : d.absenceDayCount || 0,
                    lackOfTime : d.lackOfTimeCount || 0,
                    abNormalDuty : d.abNormalDutyCount || 0,
                    late : d.lateCount ? d.lateCount : 0,
                    //leaveEarly : d.leaveEarlyCount ? d.leaveEarlyCount/2 : 0,
                    normal : d.onDutyEmployeeCount || 0
                }

                var total = 0;
                for( var n in totals  ){
                    total += totals[n];
                }
                data.rateData = {
                    levelAsked : (!totals.levelAsked || !total) ? 0 : ((totals.levelAsked/total * 100).toFixed(2) + "%"),
                    noSign : (!totals.noSign || !total) ? 0 : ((totals.noSign/total * 100).toFixed(2)  + "%"),
                    lackOfTime : (!totals.lackOfTime || !total) ? 0 : ((totals.lackOfTime/total * 100).toFixed(2)  + "%"),
                    abNormalDuty : (!totals.abNormalDuty || !total) ? 0 : ((totals.abNormalDuty/total * 100).toFixed(2)  + "%"),
                    late : (!totals.late || !total) ? 0 : ((totals.late/total * 100).toFixed(2) + "%"),
                    //leaveEarly : (!totals.leaveEarly || !total) ? 0 : ((totals.leaveEarly/total* 100).toFixed(2)  + "%"),
                    normal : (!totals.normal || !total) ? 0 : ((totals.normal/total* 100).toFixed(2)  + "%")
                }
                if(callback)callback();
            }.bind(this), null, async )
        }
    },
    loadStatusColorNode : function(){
        this.statusColorArea.empty();

        this.statusColorTable = new Element("table",{
            "styles" : this.css.statusColorTable
        }).inject(this.statusColorArea)

        var totalData = this.data[ this.unit+this.year + this.month].totalData;
        var rateData = this.data[ this.unit+this.year + this.month].rateData;

        for(var status in this.statusColor){

            var tr = new Element("tr",{
                "styles" : this.css.statusColorTr,
                "title": this.lp[status]
            }).inject(this.statusColorTable)

            var td = new Element("td").inject(tr);
            new Element("div",{
                "styles" : {
                    "margin-top": "8px",
                    "width": "14px",
                    "height": "14px",
                    "border-radius": "14px",
                    "background-color": this.statusColor[status]
                }
            }).inject(td);

            var td = new Element("td").inject(tr);
            new Element("div",{
                "styles" : {
                    "margin-top": "8px",
                    "min-width": "30px",
                    "padding-left": "4px",
                    "font-size": "14px",
                    "color": "#666"
                },
                "text": this.lp.statusText[status]
            }).inject(td);

            var td = new Element("td").inject(tr);
            new Element("div",{
                "styles" : {
                    "margin-top": "8px",
                    "min-width": "60px",
                    "padding-left": "4px",
                    "font-size": "12px",
                    "color": "#999"
                },
                "text": "("+ totalData[status] + ""+ this.app.lp.day+")"
            }).inject(td);


            //var td = new Element("td",{
            //    "styles" : this.css.statusTextTd,
            //    "text" : this.lp[status] +":"+totalData[status]+ " " +this.lp.day +"("+rateData[status]+")"
            //}).inject(tr)
        }
    },
    loadPieChart : function(){

        //this.pieChartTitle = new Element("div.pieChartTitle",{
        //    "styles" : this.css.pieChartTitle,
        //    "text" : this.lp.index.pieChart
        //}).inject(this.pieChartArea)

        this.pieChartNode = new Element("div.pieChartNode",{
            "styles" : this.css.pieChartNode
        }).inject(this.pieChartArea)


        var data = this.data[this.unit+ this.year + this.month].totalData;
        this.pieChart = new MWF.xApplication.Attendance.Echarts(this.pieChartNode, this, data);
        this.pieChart.loadUnitPieChart();
    },
    loadBarChart : function(){
        this.barChartNode = new Element("div.barChartNode",{
            "styles" : this.css.barChartNode
        }).inject(this.barChartArea);

        var date = new Date( this.date.getFullYear() , this.date.getMonth(), this.date.getDate() );
        date.decrement("month", 1);
        var year_1 = date.getFullYear().toString();
        var month_1 = date.format( this.lp.dateFormatOnlyMonth );
        var data_1 = this.data[ this.unit + year_1 + month_1 ];

        date.decrement("month", 1);
        var year_2 = date.getFullYear().toString();
        var month_2 = date.format( this.lp.dateFormatOnlyMonth );
        var data_2 = this.data[ this.unit + year_2 + month_2 ];

        if( !data_1 ){
            this.loadData( null, this.unit, year_1, month_1, false )
        }
        if( !data_2 ){
            this.loadData( null, this.unit, year_2, month_2, false)
        }

        var d = [{
            year : year_2,
            month : month_2,
            data : this.data[this.unit+ year_2 + month_2].totalData
        },{
            year : year_1,
            month : month_1,
            data : this.data[this.unit+ year_1 + month_1].totalData
        },{
            year : this.year,
            month : this.month,
            data : this.data[this.unit+ this.year + this.month].totalData
        }];

        this.barChart = new MWF.xApplication.Attendance.Echarts(this.barChartNode, this, d );
        this.barChart.loadUnitBarChart();
    },
    loadDetail : function(){
        this.detailArea.empty();
        this.detailNode = new Element("div",{
            "styles" : this.css.detailNode
        }).inject(this.detailArea);

        this.detailTitleNode = new Element("div",{
            "styles" : this.css.detailTitleNode,
            "text" : this.lp.attendanceStatisic
        }).inject(this.detailNode)

        var table = new Element("table", {
            "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "styles" : this.css.table, "class" : "editTable"
        }).inject( this.detailNode );

        var tr = new Element("tr", { "styles" : this.css.listHeadNode }).inject(table);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.name  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.onDutyTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.offDutyTimes   }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.onDutyDayCount  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.onSelfHolidayCount  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.absenceDayCount  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.lateTimes  }).inject(tr);
        //var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.leaveEarlyTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.lackOfTimeCount  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.abNormalDutyCount  }).inject(tr);

        this.actions.listStaticMonthPersonByUnitNested(this.unit, this.year, this.month, function( json ){
            var data = json.data || [];
            data.sort( function(a, b){
               return   b.onDutyDayCount - a.onDutyDayCount;
            })
             data.each(function( d ){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.employeeName.split("@")[0] }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.onDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.offDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.onDutyDayCount }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.onSelfHolidayCount }).inject(tr);
                 var td = new Element("td", { "styles" : this.css.tableValue , "text": d.absenceDayCount }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.lateTimes }).inject(tr);
                //var td = new Element("td", { "styles" : this.css.tableValue , "text": d.leaveEarlyTimes }).inject(tr);
                 var td = new Element("td", { "styles" : this.css.tableValue , "text": d.lackOfTimeCount }).inject(tr);
                 var td = new Element("td", { "styles" : this.css.tableValue , "text": d.abNormalDutyCount }).inject(tr);
            }.bind(this))
        }.bind(this))


    },
    listDetailFilterUser :function( callback, name, year, month ){
        //{'q_empName':'林玲','q_year':'2016','q_month':'03'}
        var filter = {};
        if( name )filter.q_empName = name;
        if( year )filter.q_year = year;
        if( month )filter.q_month =  month.toString().length == 2 ? month : "0"+month;
        this.actions.listDetailFilterUser( filter, function(json){
            if( callback )callback(json.data);
        }.bind(this))
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.titleNode ? this.titleNode.getSize() : {"x":0,"y":0};
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();
        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y-10;
        this.elementContentNode.setStyle("height", ""+height+"px");

    },
    // setNodeScroll: function(){
    //     var _self = this;
    //     MWF.require("MWF.widget.ScrollBar", function(){
    //         new MWF.widget.ScrollBar(this.elementContentNode, {
    //             "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
    //             "onScroll": function(y){
    //             }
    //         });
    //     }.bind(this));
    // }
});
