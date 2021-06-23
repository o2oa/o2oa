MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Attendance", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Attendance", "Common", null, false);

MWF.xApplication.Attendance.UnitQywxIndex = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    statusColor : {
        "resultNormal" : "#9acd32", //绿色，正常
        "leaveEarlyTimes":"#4f94cd", //蓝色，早退
        "lateTimes":"#fede03", //黄色，迟到
        "notSignedCount":"#ee807f", //粉红色,未签到
        "outsideDutyTimes" : "#dec674",//外出签到次数
        "absenteeismTimes" : "#fedcbd"//矿工
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
        this.listUnitWithPerson( function( unit ){
            this.unit = unit;
            this.units = [];
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
            this.unit = this.units[0] || this.unit;
            if( this.units.length > 1 ){ //(this.units.length==1 && this.units[0]!=this.unit )
                this.titleUnitAreaNode = new Element("div.titleUnitAreaNode",{
                    "styles" : this.css.titleUnitAreaNode
                }).inject(this.titleNode)

                this.titleUnitActionNode = new Element("div",{
                    "styles" : this.css.titleUnitActionNode
                }).inject(this.titleUnitAreaNode)

                this.titleUnitActionTextNode = new Element("div",{
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
                }).inject(this.titleNode);
            }
        }.bind(this) )
    },
    listUnitWithPerson : function( callback ){
        var data = {"personList": this.app.getNameFlag(this.userName)};
        this.app.orgActions.listUnitWithPerson( function( json ){
            if( json.data.length > 0 ){
                if(callback)callback( json.data[0].distinguishedName );
            }else{
                if(callback)callback();
            }
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
        this.lineChartArea.empty();
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
        this.setNodeScroll();
        this.setContentSize();
    },
    reloadChart : function(){
        this.pieChartArea.empty();
        this.barChartArea.empty();
        this.lineChartArea.empty();
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
        }).inject(this.elementContentListNode)


        this.pieChartArea = new Element("div.pieChartArea",{
            "styles" : this.css.pieChartArea
        }).inject(this.topContentArea)

        this.statusColorArea = new Element("div.statusColorArea",{
            "styles" : this.css.statusColorArea
        }).inject(this.topContentArea)

        this.barChartArea = new Element("div.barChartArea",{
            "styles" : this.css.barChartArea
        }).inject(this.topContentArea)

        this.middleContentArea = new Element("div.middleContentArea",{
            "styles" : this.css.middleContentArea
        }).inject(this.elementContentListNode)

        this.lineChartArea = new Element("div.lineChartArea",{
            "styles" : this.css.lineChartArea
        }).inject(this.middleContentArea)

        this.bottomContentArea = new Element("div.middleContentArea",{
            "styles" : this.css.bottomContentArea
        }).inject(this.elementContentListNode)

        this.detailArea = new Element("div.lineChartArea",{
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
            var action = o2.Actions.load("x_attendance_assemble_control");
            action.QywxAttendanceStatisticAction.unitMonth(unit, year, month, function(json){
                var d = json.data || {};
                var data = this.data[ unit + year + month ] = {};
                var totals = data.totalData = {
                    resultNormal: d.resultNormal || 0,
                    lateTimes: d.lateTimes || 0,
                    leaveEarlyTimes: d.leaveEarlyTimes || 0,
                    absenteeismTimes: d.absenteeismTimes || 0,
                    outsideDutyTimes: d.outsideDutyTimes || 0,
                    notSignedCount: d.notSignedCount || 0
                }

                var total = 0;
                for( var n in totals  ){
                    total += totals[n];
                }
                data.rateData = {
                    resultNormal : (!totals.resultNormal || !total) ? 0 : ((totals.resultNormal/total * 100).toFixed(2) + "%"),
                    lateTimes : (!totals.lateTimes || !total) ? 0 : ((totals.lateTimes/total * 100).toFixed(2)  + "%"),
                    leaveEarlyTimes : (!totals.leaveEarlyTimes || !total) ? 0 : ((totals.leaveEarlyTimes/total * 100).toFixed(2)  + "%"),
                    absenteeismTimes : (!totals.absenteeismTimes || !total) ? 0 : ((totals.absenteeismTimes/total * 100).toFixed(2)  + "%"),
                    outsideDutyTimes : (!totals.outsideDutyTimes || !total) ? 0 : ((totals.outsideDutyTimes/total * 100).toFixed(2) + "%"),
                    notSignedCount : (!totals.notSignedCount || !total) ? 0 : ((totals.notSignedCount/total* 100).toFixed(2)  + "%")
                }
                if(callback)callback();

            }.bind(this), null, async);
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
                "styles" : this.css.statusColorTr
            }).inject(this.statusColorTable)
            var td = new Element("td",{
                "styles" : this.css.statusColorTd
            }).inject(tr)
            td.setStyle("background-color",this.statusColor[status]);

            var tr = new Element("tr",{
                "styles" : this.css.statusTextTr
            }).inject(this.statusColorTable)
            var td = new Element("td",{
                "styles" : this.css.statusTextTd,
                "text" : this.lp[status] +totalData[status]+ this.lp.day +"("+rateData[status]+")"
            }).inject(tr)
        }
    },
    loadPieChart : function(){
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
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.resultNormal  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.lateTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.outsideDutyTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.leaveEarlyTimes  }).inject(tr);

        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.absenteeismTimes  }).inject(tr);
        var td = new Element("td", {  "styles" : this.css.tableTitle, "text" : this.lp.notSignedCount  }).inject(tr);
        var action = o2.Actions.load("x_attendance_assemble_control");
        action.QywxAttendanceStatisticAction.personMonthWithUnit(this.unit, this.year, this.month, function(json){
            var data = json.data || [];
            data.sort( function(a, b){
               return b.workDayCount - a.workDayCount;
            });
            data.each(function( d ){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.o2User.split("@")[0] }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.onDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.offDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.resultNormal }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.lateTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.outsideDutyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.leaveEarlyTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.absenteeismTimes }).inject(tr);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.notSignedCount }).inject(tr);
            }.bind(this))

        }.bind(this));
        
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
    setNodeScroll: function(){
        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {
                "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){
                }
            });
        }.bind(this));
    }
});
