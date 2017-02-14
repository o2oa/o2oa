MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Attendance", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Attendance", "Common", null, false);

MWF.xApplication.Attendance.DepartmentIndex = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    statusColor : {
        "normal" : "#9acd32", //绿色，正常
        "levelAsked":"#4f94cd", //蓝色，请假
        "late":"#fede03", //黄色，迟到
        //"leaveEarly":"#fe8d03", //橙色，早退
        "noSign":"#ee807f", //粉红色,未签到
        "lackOfTime" : "#dec674",//工时不足人次
        "abNormalDuty" : "#fedcbd"//异常打卡人次
    },
    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.lp = app.lp;
        this.path = "/x_component_Attendance/$DepartmentIndex/";
        this.cssPath = "/x_component_Attendance/$DepartmentIndex/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.setDate();

        this.today = new Date();

        this.userName = layout.desktop.session.user.name;
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

        this.loadDepartmentNode();
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
    changeDepartmentTo : function( d ){
        this.department = d;
        this.titleDepartmentActionTextNode.set("text",d);
        this.reloadContent();
    },
    loadDepartmentNode: function(){
        this.getDepartmentByPerson( function( departmentData ){
            this.department = departmentData.name;
            this.departments = [];
            var flag = true;
            if( this.app.isCompanyManager() ){
                this.app.manageCompanys.each(function( c ){
                    this.actions.listDepartmentByCompany( function( json ){
                        json.data.each(function( d ){
                            this.departments.push( d.name )
                        }.bind(this))
                    }.bind(this), null , c, false )
                }.bind(this))
            }else if( this.app.isDepartmentManager() ){
                this.departments = this.app.manageDepartments;
            }
            this.department = this.departments[0] || this.department;
            if( this.departments.length > 1 ){ //(this.departments.length==1 && this.departments[0]!=this.department )
                this.titleDepartmentAreaNode = new Element("div.titleDepartmentAreaNode",{
                    "styles" : this.css.titleDepartmentAreaNode
                }).inject(this.titleNode)

                this.titleDepartmentActionNode = new Element("div",{
                    "styles" : this.css.titleDepartmentActionNode
                }).inject(this.titleDepartmentAreaNode)

                this.titleDepartmentActionTextNode = new Element("div",{
                    "styles" : this.css.titleDepartmentActionTextNode,
                    "text" : this.department
                }).inject(this.titleDepartmentActionNode)

                this.titleDepartmentActionIconNode = new Element("div",{
                    "styles" : this.css.titleDepartmentActionIconNode,
                }).inject(this.titleDepartmentActionNode)

                this.titleDepartmentActionNode.addEvents({
                    "mouseover": function(){
                        this.titleDepartmentActionTextNode.setStyles(this.css.titleDepartmentActionTextNode_over);
                        this.titleDepartmentActionIconNode.setStyles(this.css.titleDepartmentActionIconNode_over);
                    }.bind(this),
                    "mouseout": function(){
                        this.titleDepartmentActionTextNode.setStyles(this.css.titleDepartmentActionTextNode);
                        this.titleDepartmentActionIconNode.setStyles(this.css.titleDepartmentActionIconNode);
                    }.bind(this),
                    "click" : function( ev ){
                        this.switchDepartment( ev.target )
                        ev.stopPropagation();
                    }.bind(this)
                })
            }else{
                this.titleDepartmentNode = new Element("div",{
                    "styles" : this.css.titleDepartmentNode,
                    "text" : this.department
                }).inject(this.titleNode);
            }
        }.bind(this) )
    },
    getDepartmentByPerson : function( callback ){
        this.actions.listDepartmentByPerson( function( json ){
            if( json.data.length > 0 ){
                if(callback)callback( json.data[0] );
            }else{
                if(callback)callback();
            }
        }.bind(this), null, this.userName, false )
    },
    switchDepartment : function( el ){
        var _self = this;
        var node = this.titleDepartmentListNode;
        var parentNode = el.getParent();
        if(node){
            if(  node.getStyle("display") == "block" ){
                node.setStyle("display","none");
            }else{
                node.setStyle("display","block");
                node.position({
                    relativeTo: this.titleDepartmentActionNode,
                    position: 'bottomCenter',
                    edge: 'upperCenter'
                });
            }
        }else{
            node = this.titleDepartmentListNode = new Element("div",{
                "styles" :  this.css.titleDepartmentListNode
            }).inject(this.node)
            this.app.content.addEvent("click",function(){
                _self.titleDepartmentListNode.setStyle("display","none");
            })
            this.departments.each(function( d ){
                var dNode = new Element("div",{
                    "text" : d,
                    "styles" : this.css.titleDepartmentSelectNode
                }).inject(node);
                dNode.store("department", d );
                dNode.addEvents({
                    "mouseover" : function(){ this.setStyles(_self.css.titleDepartmentSelectNode_over); },
                    "mouseout" : function(){  this.setStyles(_self.css.titleDepartmentSelectNode); },
                    "click" : function(e){
                        _self.titleDepartmentListNode.setStyle("display","none");
                        this.setStyles(_self.css.titleDepartmentSelectNode);
                        _self.changeDepartmentTo( this.retrieve("department") );
                        e.stopPropagation();
                    }
                })
            }.bind(this))
            node.position({
                relativeTo: this.titleDepartmentActionNode,
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
        }.bind(this))
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
    loadData : function( callback, department, year, month, async ){
        if( !department  )department = this.department;
        if( !year )year = this.year;
        if( !month )month = this.month;
        if( this.data[ department + year + month ] ) {
            if(callback)callback();
        }else{
            this.actions.listStaticMonthDepartmentSum( department, year, month, function( json ){
                var d = json.data || {};
                var data = this.data[ department + year + month ] = {};
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

        var totalData = this.data[ this.department+this.year + this.month].totalData;
        var rateData = this.data[ this.department+this.year + this.month].rateData;

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

        //this.pieChartTitle = new Element("div.pieChartTitle",{
        //    "styles" : this.css.pieChartTitle,
        //    "text" : this.lp.index.pieChart
        //}).inject(this.pieChartArea)

        this.pieChartNode = new Element("div.pieChartNode",{
            "styles" : this.css.pieChartNode
        }).inject(this.pieChartArea)


        var data = this.data[this.department+ this.year + this.month].totalData;
        this.pieChart = new MWF.xApplication.Attendance.Echarts(this.pieChartNode, this, data);
        this.pieChart.loadDepartmentPieChart();
    },
    loadBarChart : function(){
        this.barChartNode = new Element("div.barChartNode",{
            "styles" : this.css.barChartNode
        }).inject(this.barChartArea)

        var date = new Date( this.date.getFullYear() , this.date.getMonth(), this.date.getDate() )
        date.decrement("month", 1);
        var year_1 = date.getFullYear().toString();
        var month_1 = date.format( this.lp.dateFormatOnlyMonth )
        var data_1 = this.data[ this.department + year_1 + month_1 ];

        date.decrement("month", 1);
        var year_2 = date.getFullYear().toString();
        var month_2 = date.format( this.lp.dateFormatOnlyMonth )
        var data_2 = this.data[ this.department + year_2 + month_2 ];

        if( !data_1 ){
            this.loadData( null, this.department, year_1, month_1, false )
        }
        if( !data_2 ){
            this.loadData( null, this.department, year_2, month_2, false)
        }

        var d = [{
            year : year_2,
            month : month_2,
            data : this.data[this.department+ year_2 + month_2].totalData
        },{
            year : year_1,
            month : month_1,
            data : this.data[this.department+ year_1 + month_1].totalData
        },{
            year : this.year,
            month : this.month,
            data : this.data[this.department+ this.year + this.month].totalData
        }]

        this.barChart = new MWF.xApplication.Attendance.Echarts(this.barChartNode, this, d );
        this.barChart.loadDepartmentBarChart();
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

        this.actions.listStaticMonthPersonByDepartmentNested(this.department, this.year, this.month, function( json ){
            var data = json.data || [];
            data.sort( function(a, b){
               return   b.onDutyDayCount - a.onDutyDayCount;
            })
             data.each(function( d ){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", { "styles" : this.css.tableValue , "text": d.employeeName }).inject(tr);
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
