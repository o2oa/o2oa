MWF.require("MWF.widget.Calendar", null, false);
var MWFCalendarWeekView = MWF.xApplication.Calendar.WeekView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Calendar/$WeekView/";
        this.cssPath = "/x_component_Calendar/$WeekView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.weekBegin = this.app.calendarConfig.weekBegin || "0";
        this.load();
    },
    load: function(){
        this.node = new Element("div.node", {"styles": this.css.node}).inject(this.container);
        this.node.setStyle("position","relative");
        //this.loadSideBar();
        this.resetNodeSize();
        //this.app.addEvent("resize", this.resetNodeSize.bind(this));
        this.loadCalendar();
    },

    resetNodeSize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();
        var y = size.y-50;

        this.node.setStyle("height", ""+y+"px");
        //this.node.setStyle("margin-top", "60px");

        if( this.calendar ){
            this.calendar.resetBodySize()
        }

        //var sideBarSize = this.app.sideBar ?  this.app.sideBar.getSize() : { x : 0, y:0 };
        //this.node.setStyle("width", ""+(size.x - sideBarSize.x)+"px");
        //this.node.setStyle("margin-right", ""+sideBarSize.x+"px");

        //var size = this.container.getSize();
        //
        //this.scrollNode.setStyle("height", ""+(size.y-60)+"px");
        //this.scrollNode.setStyle("margin-top", "60px");
        //
        //if (this.contentWarpNode){
        //    this.contentWarpNode.setStyles({
        //        "width": (size.x - 50) +"px"
        //    });
        //}

    },

    loadCalendar: function(){
        var date = "";
        if( this.options.date ){
            date = Date.parse( this.options.date )
        }else{
            date = new Date();
        }

        this.currentWeek = this.getWeekNumber( date );

        this.calendar = new MWFCalendarWeekView.Calendar(this, date  );

    },
    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 0
        }).chain(function(){
            this.node.setStyle("display", "none");
        }.bind(this));

    },
    show: function(){
        this.node.setStyles(this.css.node);
        var fx = new Fx.Morph(this.node, {
            "duration": "800",
            "transition": Fx.Transitions.Expo.easeOut
        });
        //this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1
            //"left": MWFCalendar.LeftNaviWidth+"px"
        }).chain(function(){
            //this.node.setStyles({
            //    "position": "static",
            //    "width": "auto"
            //});
            if( this.calendar.dataTable_WholeDay ){
                this.calendar.dataTable_WholeDay.setStyle("display","");
            }
        }.bind(this));
    },
    reload: function(){
        if (this.calendar) this.calendar.reLoadCalendar();
    },
    recordStatus : function(){
        var date = "";
        if (this.calendar) date = this.calendar.baseDate;
        return {
            date : date.toString()
        };
    },
    destroy: function(){
        if (this.calendar){
            this.calendar.destroy();
        }
        this.node.destroy();
        //MWF.release( this );
    },
    getWeekNumber: function(d){
        // Create a copy of this date object
        var target  = d.clone();

        // ISO week date weeks start on monday
        // so correct the day number
        //var dayNr   = (d.getDay() + 6) % 7;
        var dayNr   =  ( 7 + d.getDay() - parseInt( this.weekBegin ) ) % 7;

        // ISO 8601 states that week 1 is the week
        // with the first thursday of that year.
        // Set the target date to the thursday in the target week
        target.setDate(target.getDate() - dayNr + 3);

        // Store the millisecond value of the target date
        var firstThursday = target.valueOf();

        // Set the target to the first thursday of the year
        // First set the target to january first
        target.setMonth(0, 1);
        // Not a thursday? Correct the date to the next thursday
        if (target.getDay() != 4) {
            target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
        }

        // The weeknumber is the number of weeks between the
        // first thursday of the year and the thursday in the target week
        return 1 + Math.ceil((firstThursday - target) / 604800000); // 604800000 = 7 * 24 * 3600 * 1000
    }

});


MWFCalendarWeekView.DayWidth;
MWFCalendarWeekView.HourHeight = 48;
MWFCalendarWeekView.DayHeight = 24*MWFCalendarWeekView.HourHeight;
MWFCalendarWeekView.DayMsec = 3600 * 24 * 1000;
MWFCalendarWeekView.WeekWidth;
MWFCalendarWeekView.WeekMsec = MWFCalendarWeekView.DayMsec * 7;

MWFCalendarWeekView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.weekBegin = this.app.calendarConfig.weekBegin || "0";
        this.baseDate = date || new Date();
        this.today = new Date();
        this.load();
    },
    load: function(){
        this.date = this.getWeekStartTime( this.baseDate );

        this.weekStartTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 0, 0, 0 );
        this.weekStartTimeStr = this.weekStartTime.format( "db" );
        var end = this.date.clone().increment( "day", 6 );
        this.weekEndTime = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
        this.weekEndTimeStr = this.weekEndTime.format( "db" );

        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);


        this.titleTable = new Element("table.titleTable", {
            "styles": this.css.titleTable,
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.container);

        this.scrollNode = new Element("div.scrollNode", {
            "styles": this.css.scrollNode  //this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.container);
        this.contentWarpNode = new Element("div.contentWarpNode", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div.contentContainerNode",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.bodyNode = new Element("div.bodyNode", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);
        this.bodyNode.setStyle("position","relative");

        //this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.loadTitleTable();
        this.loadBodyTable();

        //this.resetBodySize();

        this.loadDataTable_WholeDay();
        this.loadDataTable();

        this.loadCalendar();
        //this.loadWholedayData( function(json){
        //    this.loadWholeday(json);
        //}.bind(this));
        //
        //this.loadData( function( json ){
        //    this.loadDataDay( json );
        //}.bind(this));
        //this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    getWeekStartTime: function( d ){
        var date = d.clone();
        //var week = date.getDay();
        //if( this.weekBegin == "1" ){
        //    var decrementDay = ((week-1)<0) ? 6 : week-1;
        //}else{
        //    var decrementDay = week;
        //}

        var decrementDay = ( 7 + d.getDay() - parseInt( this.weekBegin ) ) % 7;

        return date.decrement("day", decrementDay);
    },
    setTitleNode: function(){
        this.prevWeekNode =  new Element("div", {"styles": this.css.calendarPrevWeekNode}).inject(this.titleNode);

        var text = this.baseDate.format(this.app.lp.dateFormatMonth)
            + "，第" + this.view.getWeekNumber( this.baseDate  ) + "周";


        this.titleTextNode = new Element("div", {"styles": this.css.calendarTitleTextNode, "text": text}).inject(this.titleNode);

        this.nextWeekNode =  new Element("div", {"styles": this.css.calendarNextWeekNode}).inject(this.titleNode);

        this.prevWeekNode.addEvents({
            "mouseover": function(){this.prevWeekNode.setStyles(this.css.calendarPrevWeekNode_over);}.bind(this),
            "mouseout": function(){this.prevWeekNode.setStyles(this.css.calendarPrevWeekNode);}.bind(this),
            "mousedown": function(){this.prevWeekNode.setStyles(this.css.calendarPrevWeekNode_down);}.bind(this),
            "mouseup": function(){this.prevWeekNode.setStyles(this.css.calendarPrevWeekNode_over);}.bind(this),
            "click": function(){this.changeWeekPrev();}.bind(this)
        });
        this.nextWeekNode.addEvents({
            "mouseover": function(){this.nextWeekNode.setStyles(this.css.calendarNextWeekNode_over);}.bind(this),
            "mouseout": function(){this.nextWeekNode.setStyles(this.css.calendarNextWeekNode);}.bind(this),
            "mousedown": function(){this.nextWeekNode.setStyles(this.css.calendarNextWeekNode_down);}.bind(this),
            "mouseup": function(){this.nextWeekNode.setStyles(this.css.calendarNextWeekNode_over);}.bind(this),
            "click": function(){this.changeWeekNext();}.bind(this)
        });
        this.titleTextNode.addEvents({
            "mouseover": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "mouseout": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode);}.bind(this),
            "mousedown": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_down);}.bind(this),
            "mouseup": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this)
            //"click": function(){this.changeWeekSelect();}.bind(this)
        });
        this.createWeekSelector();
    },
    changeWeekPrev: function(){
        this.date.decrement("week", 1);
        this.baseDate = this.date;
        var text = this.baseDate.format(this.app.lp.dateFormatMonth) + "，第" + this.view.getWeekNumber( this.baseDate  ) + "周";
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeWeekNext: function(){
        this.date.increment("week", 1);
        this.baseDate = this.date;
        var text = this.baseDate.format(this.app.lp.dateFormatMonth) + "，第" + this.view.getWeekNumber( this.baseDate  ) + "周";
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeWeekSelect: function(){
        if (!this.monthSelector) this.createWeekSelector();
        //this.monthSelector.show();
    },
    createWeekSelector: function(){
        //this.monthSelector = new MWFCalendarWeekView.Calendar.WeekSelector(this.date, this);
        this.weekCalendar = new MWFCalendarWeekView.WeekCalendar(this.titleTextNode, {
            "style":"meeting_blue",
            "weekBegin" : this.weekBegin,
            "target": this.node,
            "baseDate" : this.baseDate,
            "onInit" : function(){
                this.options.dayPath = this.options.path+this.options.style+"/day_week.html";
            },
            "onQueryComplate": function(e, dv, date){
                var selectedDate = new Date.parse(dv);
                this.changeWeekTo(selectedDate);
            }.bind(this)
        });
        //this.weekCalendar.app = this.app;
    },
    changeWeekTo: function(d){
        this.baseDate = d;
        this.date = this.getWeekStartTime( d );
        //var text = this.date.format(this.app.lp.dateFormatWeek);
        //this.titleTextNode.set("text", text);

        var text = this.baseDate.format(this.app.lp.dateFormatMonth) + "，第" + this.view.getWeekNumber( this.baseDate  ) + "周";
        this.titleTextNode.set("text", text);

        this.reLoadCalendar();
    },
    isToday : function( d ){
       var today = new Date();
        if( today.get("year") != d.get("year") )return false;
        if( today.get("month") != d.get("month") )return false;
        if( today.get("date") != d.get("date") )return false;
        return true;
    },
    loadTitleTable: function(){
        var _self = this;
        if( !this.tableHead ){
            var d = this.date.clone();

            var head = this.tableHead = new Element("tr", {
                "styles" : this.css.calendarTableTitleTr
            }).inject( this.titleTable );
            new Element("th", {
                "styles" : this.css.calendarTableTh_hour
            }).inject(head);
            for( var i=0; i<7; i++ ) {
                var index = (  i + parseInt( this.weekBegin ) ) % 7;
                var th = new Element("th", {
                    "styles": this.css.calendarTableTh,
                    text: this.app.lp.weeks.arr[index] + "(" + d.format("%m.%d") + ")"
                }).inject(head);
                th.store("date", d.format("%Y-%m-%d") );
                th.addEvent( "click", function(){
                    _self.app.toDay( this.retrieve("date") )
                }.bind(th));
                d.increment("day", 1);
            }

            var tr = this.wholeDayTr = new Element( "tr").inject( this.titleTable );
            var td = new Element( "td.calendarTableCell", {
                "tdType" : "hour",
                styles : this.css.calendarTableCell_hour
            } ).inject( tr );
            td.setStyle("min-height","80px");
            var node = new Element("div",{
                text : "全天"
            }).inject( td );
            td.store("hour",i );
            this.wholeDayTdMap = {};
            var d = this.date.clone();
            for( var j=0; j<7; j++ ){
                td = this.wholeDayTdMap[j] = new Element( "td" , {
                    "tdType" : "calendar",
                    "styles" : this.css.calendarTableCell,
                    "index" : j+1
                }).inject( tr );
                td.store("dateStr",d.format("%Y-%m-%d"));
                td.store("index",j);

                td.addEvent("click", function(ev){
                    this.setCurrentTd( ev.target );
                }.bind(this));

                td.addEvent("dblclick", function(ev){
                    this.cancelCurrentTd();
                    var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                        startTime : Date.parse( ev.target.retrieve("dateStr")) ,
                        endTime : Date.parse( ev.target.retrieve("dateStr")),
                        isWholeday : true
                    }, {app:this.app});
                    form.view = this;
                    form.create();
                }.bind(this));

                new Drag(td, {
                    "onStart": function(dragged, e){
                        this.cancelCurrentTd();
                        this.cellDragStart_wholeDay(dragged, e);
                    }.bind(this),
                    "onDrag": function(dragged, e){
                        this.cellDrag_wholeDay(dragged, e);
                    }.bind(this),
                    "onComplete": function(dragged, e){
                        this.completeDrag_wholeDay(dragged, e);
                    }.bind(this)
                });

                d.increment("day", 1);
            }

        }else{
            var d = this.date.clone();
            this.tableHead.getElements("th").each( function( th, i ){
                if( i == 0 )return;
                var index = (  i - 1 + parseInt( this.weekBegin ) ) % 7;
                th.set("text", this.app.lp.weeks.arr[index] + "(" + d.format("%d") + ")");
                th.store("date", d.format("%Y-%m-%d") );
                d.increment("day", 1);
            }.bind(this));

            var d = this.date.clone();
            Object.each( this.wholeDayTdMap, function( td, i ){
                td.store("dateStr",d.format("%Y-%m-%d"));
                d.increment("day", 1);
            }.bind(this))
        }
    },


    loadBodyTable: function(){
        this.calendarTable = new Element("table.dragTable", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.bodyNode);

        this.hourTdMap = {};
        this.hourTrMap = {};
        for( var i=0; i<24; i++ ){
            var tr = new Element( "tr").inject( this.calendarTable );
            var td = new Element( "td.calendarTableCell", {
                "tdType" : "hour",
                styles : this.css.calendarTableCell_hour,
                valign : "top"
            } ).inject( tr );
            var node = new Element("div",{
                text : i + ":00"
            }).inject( td );
            td.store("hour",i );
            for( var j=0; j<7; j++ ){
                if( !this.hourTdMap[j] )this.hourTdMap[j] = {};
                var td = this.hourTdMap[j][i] = new Element( "td" , {
                    "tdType" : "calendar",
                    "styles" : this.css.calendarTableCell,
                    "index" : j+1
                }).inject( tr );
                td.store("hour",i );
                td.store("index",j );

                td.addEvent("click", function(ev){
                    this.setCurrentTd( ev.target );
                }.bind(this));

                td.addEvent("dblclick", function(ev){
                    this.cancelCurrentTd();
                    var hour = ev.target.retrieve("hour");
                    var index = ev.target.retrieve("index");
                    var dateStr = this.getDateByIndex( index );
                    var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                        startTime : Date.parse( dateStr + " " +hour+":00") ,
                        endTime : Date.parse( dateStr + " " + (hour+1)+":00" )
                    }, {app:this.app});
                    form.view = this;
                    form.create();
                }.bind(this));

                new Drag(td, {
                    "onStart": function(dragged, e){
                        this.cancelCurrentTd();
                        this.cellDragStart(dragged, e);
                    }.bind(this),
                    "onDrag": function(dragged, e){
                        this.cellDrag(dragged, e);
                    }.bind(this),
                    "onComplete": function(dragged, e){
                        this.completeDrag(dragged, e);
                    }.bind(this)
                });

            }
            this.hourTrMap[ i ] = tr;
        }

    },

    setCurrentTd : function(td){
        td.setStyle("background-color","#fffdf2");
        if( this.currentSelectedTd ){
            var flag = this.currentSelectedTd.retrieve("index") == this.todayIndex;
            this.currentSelectedTd.setStyle("background-color",flag?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = td;
    },
    cancelCurrentTd : function(){
        if( this.currentSelectedTd ){
            var flag = this.currentSelectedTd.retrieve("index") == this.todayIndex;
            this.currentSelectedTd.setStyle("background-color",flag?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = null;
    },

    reLoadCalendar: function(){
        this.weekStartTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 0, 0, 0 );
        this.weekStartTimeStr = this.weekStartTime.format( "db" );
        var end = this.date.clone().increment( "day", 6 );
        this.weekEndTime = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
        this.weekEndTimeStr = this.weekEndTime.format( "db" );

        Object.each(this.dayMap || {}, function(day){
            day.destroy();
        }.bind(this));
        this.dayMap = {};

        if( this.wholeday ){
            this.wholeday.destroy();
        }
        this.wholeday = null;

        //this.calendarTable.getElements("td[tdType='calendar']").each( function(td){
        //    td.empty();
        //}.bind(this));

        this.loadTitleTable();

        this.loadCalendar();
    },
    loadCalendar : function(){
        this.app.currentDate = this.baseDate.clone();

        this.dateIndexMap = null;
        this.titleTable.getElement("td:nth-child(1)").setStyle("height", "auto") ;
        this.loadData( function(){
            this.loadWholeday(this.wholeDayData);
            this.loadDataDay( this.inOneDayEvents );
            this.resetBodySize();
            this.setTodayTds();
            this.cancelCurrentTd();
        }.bind(this));
    },
    setTodayTds : function(){
        var now = new Date();
        var index = this.todayIndex = this.getDateNumOfWeek( now.format("%Y-%m-%d") );
        var hour = now.get("hours");
        var mintues = now.get("minutes");
        mintues = mintues < 2 ? 2 : mintues;
        if( index > -1 ){
            this.todayTds = [];
            var td = this.wholeDayTdMap[index];
            td.setStyle("background-color","#f8fbff");
            this.todayTds.push( td );

            var tds = this.hourTdMap[index];
            Object.each( tds, function( td, i ){
                td.setStyle("background-color","#f8fbff");
                this.todayTds.push( td );
            }.bind(this));

            this.nowTd = tds[hour];
            var nowTdPosition = this.nowTd.getPosition(this.bodyNode);
            this.nowTimeNode = new Element("div",{
                styles : {
                    "position" : "absolute",
                    "left" : nowTdPosition.x,
                    "top" : nowTdPosition.y + ( (mintues - 2) / 60 ) * MWFCalendarWeekView.HourHeight,
                    "height" : "2px",
                    "width" : MWFCalendarWeekView.DayWidth,
                    "background-color" : "#ff3333"
                }
            }).inject(this.bodyNode);
        }else if( this.todayTds && this.todayTds.length ){
            while( this.todayTds.length > 0 ){
                this.todayTds.pop().setStyle("background-color","#fff");
            }
            if(this.nowTd)this.nowTd = null;
            if( this.nowTimeNode )this.nowTimeNode.destroy();
        }
    },
    loadData : function( callback ){
        this.app.actions.listEventWithFilter( {
            calendarIds : this.app.getSelectedCalendarId(),
            startTime : this.weekStartTimeStr,
            endTime : this.weekEndTimeStr //,
            //createPerson : this.app.userName
        }, function(json){
            this.wholeDayData = ( json.data  && json.data.wholeDayEvents ) ? json.data.wholeDayEvents : [];
            this.inOneDayEvents = [];
            (( json.data && json.data.inOneDayEvents) ? json.data.inOneDayEvents : []).each( function( d ){
                if(d.inOneDayEvents.length > 0 ){
                    this.inOneDayEvents.push( d );
                }
            }.bind(this));
            //json.data.each( function(d){
            //    var flag = false;
            //    if( d.isAllDayEvent ){
            //        flag = true;
            //    }else if( d.startTime.split(" ")[0] != d.endTime.split(" ")[0] ){
            //        flag = true;
            //    }
            //    if( flag ){
            //        this.wholeDayData.push( d )
            //    }else{
            //        this.inOneDayEvents.push(d)
            //    }
            //}.bind(this));
            if(callback)callback();
        }.bind(this))
    },
    loadDataTable_WholeDay: function( json ){

        this.dataTable_WholeDay = new Element("table.dataTable", {
            "styles": this.css.calendarTable,
            "border": "0",
            "cellPadding": "0",
            "height" : "0",
            "cellSpacing": "0"
        }).inject(this.container);
        this.dataTable_WholeDay.setStyles({
            "display" : "none",
            "position":"absolute",
            "top" : "92px",
            "left" : "0px",
            "margin": "0px auto 0px 12px"
        });
        var tr = new Element("tr").inject(this.dataTable_WholeDay);

        new Element( "td" , {
            "styles" : {"height":"0px" ,"position":"relative"}
        }).inject( tr );

        this.dataTd_WholeDay = new Element( "td" , {
            "valign" : "top",
             "styles" : {"height":"0px","position":"relative"}
        }).inject( tr );

        //this.dataTdMap_WholeDay = {};
        //for( var i=0 ;i <7; i++ ){
        //    this.dataTdMap_WholeDay[ i ] = new Element( "td" , {
        //        "valign" : "top",
        //        "styles" : {"height":"0px"}, //,"position":"relative"},
        //        "index" : i
        //    }).inject( tr );
        //}
    },
    loadWholeday : function( data ){
       this.wholeday = new MWFCalendarWeekView.Calendar.WholeDay( this, data, this.date);
    },

    loadDataTable: function( json ){

        this.dataTable = new Element("table.dataTable", {
            "styles": this.css.calendarTable,
            "height": "0",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.bodyNode);
        this.dataTable.setStyles({
            "position":"absolute",
            "top" : "0px",
            "left" : "0px"
        });
        var tr = new Element("tr").inject(this.dataTable);

        new Element( "td" , {
            "styles" : {"height":"0px","position":"relative"}
        }).inject( tr );

        this.dataTdMap = {};
        for( var i=0 ;i <7; i++ ){
                this.dataTdMap[ i ] = new Element( "td" , {
                    "styles" : {"height":"0px","position":"relative"},
                    "index" : i
                }).inject( tr );
        }
    },
    getDateIndexMap : function(){
        if( !this.dateIndexMap ){
            var date = this.getWeekStartTime( this.baseDate );
            this.dateIndexMap = {};
            for( var i=0 ;i <7; i++ ){
                var dateStr = date.format("%Y-%m-%d");
                this.dateIndexMap[ dateStr ] = i;
                date.increment();
            }
        }
        return this.dateIndexMap;
    },
    getDateByIndex : function( index ){
        var dateStr;
        var dateIndexMap = this.getDateIndexMap();
        for( var key in dateIndexMap ){
            if( dateIndexMap[key] == index ){
                return key;
            }
        }
    },
    getDateNumOfWeek : function( dateString ){
        var dateIndexMap = this.getDateIndexMap();
        return this.dateIndexMap[ dateString ]
    },
    loadDataDay : function(data){
        this.dayMap = {};

        data.each( function(d){
            var key = d.eventDate;
            var container = this.dataTdMap[ this.getDateNumOfWeek( key ) ];
            if(container){
                this.loadDay(container, key, d.inOneDayEvents );
            }
        }.bind(this));

        //var dataMap = {};
        //data.each( function( d ){
        //    var date2 = Date.parse( d.startTime );
        //    var dateStr = date2.format("%Y-%m-%d");
        //    if( !dataMap[dateStr] )dataMap[dateStr] = [];
        //    dataMap[dateStr].push( d )
        //}.bind(this));
        //
        //for( var key in dataMap ){
        //    var container = this.dataTdMap[ this.getDateNumOfWeek( key ) ];
        //    if(container){
        //        this.loadDay(container, key, dataMap[key]);
        //    }
        //}
    },
    loadDay: function( container, dateStr, array){
        var date = Date.parse(dateStr);
        var m = date.get("month");
        var y = date.get("year");
        var d = date.get("date");


        var startTime = new Date( y, m, d, 0, 0, 0 );
        var endTime = new Date( y, m, d, 23, 59, 59 );

        //if(dayArray.length>0){
            this.dayMap[dateStr] = new MWFCalendarWeekView.Calendar.Day( container, date, this,  array);
        //}
        //if(wholeDayArray.length>0){
        //    this.wholedayMap[dateStr] = new MWFCalendarWeekView.Calendar.WholeDay( container, date, this, type, wholeDayArray);
        //}
    },

    resetBodySize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();
        var titleSize = this.titleNode.getSize();
        var titleTableSize = this.titleTable.getSize();
        var y = size.y-titleSize.y-titleTableSize.y;
        //this.bodyNode.setStyle("height", ""+y+"px");

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": (size.x - 40) +"px"
            });
        }

        this.scrollNode.setStyle("height", ""+y+"px");

        var hourTdX = 60;
        MWFCalendarWeekView.WeekWidth = size.x - 40 - hourTdX;
        var tdX = MWFCalendarWeekView.DayWidth = Math.floor( (MWFCalendarWeekView.WeekWidth-8) / 7);
        if(this.calendarTable){
            this.calendarTable.setStyles({
                "width": (size.x - 40) +"px"
            });
            var tr =this.calendarTable.getElement("tr:nth-child(1)");
            tr.getElements("td").each( function( td, i ){
                td.setStyle("width", (i==0 ? hourTdX : tdX )+"px");
            })
        }
        if(this.titleTable){
            this.titleTable.setStyles({
                "width": (size.x - 40) +"px"
            });
            var tr =this.titleTable.getElement("tr:nth-child(1)");
            tr.getElements("th").each( function( td, i ){
                td.setStyle("width", (i==0 ? hourTdX : tdX )+"px");
            })
        }
        if( this.dataTable ){
            this.dataTable.setStyles({
                "width": (size.x - 40) +"px"
            });
            var tr =this.dataTable.getElement("tr:nth-child(1)");
            tr.getElements("td").each( function( td, i ){
                td.setStyle("width", (i==0 ? hourTdX : tdX )+"px");
            })
        }
        for( var key in this.dayMap ){
            this.dayMap[key].resize();
        }

        if( this.dataTable_WholeDay ){
            this.dataTable_WholeDay.setStyles({
                "width": (size.x - 40) +"px"
            });
            var tr =this.dataTable_WholeDay.getElement("tr:nth-child(1)");
            var contendTdWidth = size.x - 40 - hourTdX - 2;
            tr.getElements("td").each( function( td, i ){
                td.setStyle("width", (i==0 ? hourTdX : contendTdWidth )+"px");
            })
        }
        if(this.wholeday)this.wholeday.resize();

        if(this.nowTimeNode){
            this.nowTimeNode.setStyle("width",tdX);
            if(this.nowTd)this.nowTimeNode.setStyle("left", this.nowTd.getPosition(this.bodyNode).x );
        }

        //for( var key in this.dayMap_WholeDay ){
        //    this.dayMap_WholeDay[key].resize();
        //}

    },
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        Object.each(this.dayMap || {}, function(day){
            day.destroy();
        }.bind(this));
        if( this.wholeday ){
            this.wholeday.destroy();
        }


        this.container.empty();
    },

    getIndexByPage: function( page ){
        var pos = this.calendarTable.getPosition();
        if( !this.calendarTableFirstTd ){
            this.calendarTableFirstTd = this.calendarTable.getElement("td");
        }
        pos.x = pos.x + this.calendarTableFirstTd.getSize().x;
        var col = (page.x - pos.x ) / (MWFCalendarWeekView.DayWidth + 1);
        if( col < 0 || col > 7 )return null;

        this.pageOffsetHeight = page.y - pos.y;
        var row = ( page.y - pos.y ) / MWFCalendarWeekView.HourHeight;
        if( row < 0 || row > 24 )return null;
        return {
            row : Math.floor(row),
            col : Math.floor(col)
        }
    },
    getIndexListByRange : function( index1, index2 ){
        var minIndex, maxIndex;
        if( index1.col == index2.col ){
            if( index1.row <=  index2.row ){
                minIndex = index1;
                maxIndex = index2;
            }else{
                minIndex = index2;
                maxIndex = index1;
            }
        }else if( index1.col < index2.col ){
            minIndex = index1;
            maxIndex = index2;
        }else{
            minIndex = index2;
            maxIndex = index1;
        }
        var beginRow, endRow;
        var result = [];
        for( var i = minIndex.col; i<=maxIndex.col; i++ ){
            beginRow = i == minIndex.col ? minIndex.row : 0;
            endRow = i == maxIndex.col ? maxIndex.row : 23;
            for( var j = beginRow; j<= endRow; j++ ){
                result.push( i+"_"+j );
            }
        }
        return result;
    },
    getTdByIndexString : function(index){
        var indexList = index.split("_");
        var col = indexList[0];
        var row = indexList[1];
        return this.hourTdMap[col][row];
    },
    cellDragStart: function(td, e){
        td.store("index", this.getIndexByPage(e.page ) );
        this.scrollNodeHeight = this.scrollNode.getSize().y;
    },
    cellDrag: function(td, e){
        var orgIndex = td.retrieve( "index" );
        var curIndex = this.getIndexByPage( e.page );
        if( !curIndex )return;
        var indexs = this.getIndexListByRange( orgIndex, curIndex );
        var flag = this.todayIndex > -1;
        if( this.selectedIndexRange ){
            var oldIndex = this.selectedIndexRange;
            this.selectedIndexRange.each( function( index ){
                if( !indexs.contains(index) ){
                    this.getTdByIndexString( index ).setStyle("background-color", flag && ( index.split("_")[0] ==this.todayIndex ) ?  "#F8FBFF" : "#fff");
                }
            }.bind(this));
            indexs.each( function( index ){
                if( !this.selectedIndexRange.contains(index) ){
                    this.getTdByIndexString( index ).setStyle("background-color", "#fffdf2")
                }
            }.bind(this))
        }else{
            for( var i=0; i<indexs.length; i++ ){
                this.getTdByIndexString( indexs[i] ).setStyle("background-color", "#fffdf2")
            }
        }
        this.selectedIndexRange = indexs;

        var scrollNodeTop = this.scrollNode.getScroll().y;
        if(( this.pageOffsetHeight + MWFCalendarWeekView.HourHeight * 1.5) > ( this.scrollNodeHeight + scrollNodeTop )){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop + MWFCalendarWeekView.HourHeight )
            }.bind(this), 200)
        }else if( this.pageOffsetHeight - MWFCalendarWeekView.HourHeight * 1.5 < scrollNodeTop ){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop - MWFCalendarWeekView.HourHeight )
            }.bind(this), 200)
        }
    },
    completeDrag: function(td, e){
        var flag = this.todayIndex > -1;
        if( this.selectedIndexRange && this.selectedIndexRange.length ){
            this.selectedIndexRange.each( function( index ){
                this.getTdByIndexString( index ).setStyle("background-color", flag && ( index.split("_")[0] ==this.todayIndex ) ?  "#F8FBFF" : "#fff");
            }.bind(this));
            var beginIndex = this.selectedIndexRange[0].split("_");
            var endIndex = this.selectedIndexRange.getLast().split("_");
            var beginTime = this.getDateByIndex( beginIndex[0] ) + " " + beginIndex[1]+":00";
            var endTime = this.getDateByIndex( endIndex[0] ) + " " + endIndex[1]+":59";
            var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                startTime : beginTime ,
                endTime : endTime
            }, {app:this.app});
            form.view = this;
            form.create();

            this.selectedIndexRange = null;
        }
    },


    getIndexByPage_wholeDay: function( page ){
        var pos = this.wholeDayTr.getPosition();
        if( !this.wholeDayFirstTd ){
            this.wholeDayFirstTd = this.wholeDayTr.getElement("td");
        }
        pos.x = pos.x + this.wholeDayFirstTd.getSize().x;
        var col = (page.x - pos.x ) / (MWFCalendarWeekView.DayWidth + 1);
        if( col < 0 || col > 7 )return null;

        return Math.floor(col);
    },
    getIndexListByRange_wholeDay : function( index1, index2 ){
        var minIndex = Math.min( index1, index2 );
        var maxIndex = Math.max( index1, index2 );
        var result = [];
        for( var i = minIndex; i<=maxIndex; i++ ){
            result.push( i );
        }
        return result;
    },
    cellDragStart_wholeDay: function(td, e){
        td.store("index", this.getIndexByPage_wholeDay(e.page ) );
    },
    cellDrag_wholeDay: function(td, e){
        var orgIndex = td.retrieve( "index" );
        var curIndex = this.getIndexByPage_wholeDay( e.page );
        if( !curIndex )return;
        var indexs = this.getIndexListByRange_wholeDay( orgIndex, curIndex );
        var flag = this.todayIndex > -1;
        if( this.selectedIndexRange_wholeDay ){
            var oldIndex = this.selectedIndexRange_wholeDay;
            this.selectedIndexRange_wholeDay.each( function( index ){
                if( !indexs.contains(index) ){
                    this.wholeDayTdMap[index].setStyle("background-color", flag && ( index == this.todayIndex ) ?  "#F8FBFF" : "#fff");
                }
            }.bind(this));
            indexs.each( function( index ){
                if( !this.selectedIndexRange_wholeDay.contains(index) ){
                    this.wholeDayTdMap[index].setStyle("background-color", "#fffdf2")
                }
            }.bind(this))
        }else{
            for( var i=0; i<indexs.length; i++ ){
                this.wholeDayTdMap[indexs[i]].setStyle("background-color", "#fffdf2")
            }
        }
        this.selectedIndexRange_wholeDay = indexs;
    },
    completeDrag_wholeDay: function(td, e){
        var flag = this.todayIndex > -1;
        if( this.selectedIndexRange_wholeDay && this.selectedIndexRange_wholeDay.length ){
            this.selectedIndexRange_wholeDay.each( function( index ){
                this.wholeDayTdMap[index].setStyle("background-color", flag && ( index == this.todayIndex ) ?  "#F8FBFF" : "#fff");
            }.bind(this));
            var beginIndex = this.selectedIndexRange_wholeDay[0];
            var endIndex = this.selectedIndexRange_wholeDay.getLast();
            var beginTime = this.getDateByIndex( beginIndex );
            var endTime = this.getDateByIndex( endIndex );
            var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                startTime : beginTime ,
                endTime : endTime,
                isWholeday : true
            }, {app:this.app});
            form.view = this;
            form.create();

            this.selectedIndexRange_wholeDay = null;
        }
    }

});

MWFCalendarWeekView.Calendar.WholeDay = new Class({
    Implements: [Events],
    initialize: function( calendar, data, date){
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date.clone();
        this.data = data;
        this.load();
    },
    load: function(){
        //var start = this.calendar.getWeekStartTime( this.date );
        //this.weekStartTime = new Date( start.get("year"), start.get("month"), start.get("date"), 0, 0, 0 );
        //
        //var end = start.clone().increment( "day", 6 );
        //this.weekEndTime = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );

        this.weekStartTime = this.calendar.weekStartTime;
        this.weekEndTime = this.calendar.weekEndTime;

        this.rangeList = [];
        this.rangeObject = {};
        this.data.each( function(d , i){
            var range = this.getTimeRange( d.startTime, d.endTime );
            if( !range )return null;
            d.range = range;
            d.range.id = d.id;
            d.range.data = d;
            this.rangeList.push( range );
            this.rangeObject[d.id] = range;
        }.bind(this));

        var itemCountOfDay = {};
        this.rangeList.each( function(r){
            r.days.each( function( d ){
                itemCountOfDay[ d ] = itemCountOfDay[ d ] ? itemCountOfDay[ d ]+1 : 1
            }.bind(this))
        }.bind(this));

        this.maxDayLength = 0;
        for(var key in itemCountOfDay){
            if( itemCountOfDay[key] > this.maxDayLength )this.maxDayLength = itemCountOfDay[key];
        }

        if( this.maxDayLength ){
            this.calendar.titleTable.getElement("td:nth-child(1)").setStyle("height", 24*this.maxDayLength);
        }

        this.usefulTdFlagArray = [];
        for( var i=0; i<this.maxDayLength; i++ ){
            var array = [];
            for( var j=0; j<7; j++ ){
                array.push( true );
            }
            this.usefulTdFlagArray.push( array )
        }


        this.sortRange();
        //this.getIntersectedPath2();


        this.documentList = [];
        this.rangeList.each( function(r , i){
            var d = r.data;
            if( !d )return null;
            this.documentList.push( new MWFCalendarWeekView.Calendar.WholeDayDocument( this, d, r ) );
        }.bind(this))

    },
    sortRange : function(){
        this.rangeList.sort( function( range1, range2 ){
            if( range1.days[0] > range2.days[0] )return 1;
            if( range1.days[0] < range2.days[0] )return -1;
            return range2.diff - range1.diff;
        }.bind(this));
    },
    getTimeRange : function( startTime, endTime ){
        var start = Date.parse(startTime );
        var end = Date.parse(endTime );

        if( end < this.weekStartTime || start > this.weekEndDate  )return null;

        if( start < this.weekStartTime )start = this.weekStartTime.clone();
        if( this.weekEndTime < end )end = this.weekEndTime.clone();

        var end = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
        var days = [];
        while( start < end ){
            days.push( start.clone().format("%Y-%m-%d") );
            start.increment()
        }

        start = Date.parse(startTime );
        end = Date.parse(endTime );
        return {
            start : start,
            end : end,
            days : days,
            diff : end - start
        }
    },
    resize: function(){
        if(!this.documentList)return;
        this.documentList.each( function(d){
            d.resize();
        }.bind(this))
    },
    destroy : function(){
        if(!this.documentList)return;
        while( this.documentList.length ){
            this.documentList.pop().destroy()
        }
        this.calendar.dataTd_WholeDay.empty();
    }
});

MWFCalendarWeekView.Calendar.WholeDayDocument = new Class({
    initialize: function(day, data, range ){
        this.day = day;
        this.calendar = day.calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = day.date.clone();
        this.data = data;
        this.range = range;
        this.load();
    },
    load: function(){
        this.container = this.calendar.dataTd_WholeDay;
        var items = this.items = [];
        this.data.timeStart = Date.parse( this.data.startTime );
        this.data.timeEnd = Date.parse( this.data.endTime );


        this.getUsefulTdYIndex();
        this.createNode();
        //this.range.days.each( function( d, i ){
        //    items.push( new MWFCalendarWeekView.Calendar.WholeDayItem( this, d, i ) )
        //}.bind(this))
    },
    getUsefulTdYIndex : function(){
        for( var i=0; i<this.day.maxDayLength; i++ ){
            var flag = true;
            for( var j=0; j<this.range.days.length; j++ ){
                var idx = this.calendar.getDateNumOfWeek( this.range.days[j] );
                if( !this.day.usefulTdFlagArray[i][idx] ){
                    flag = false;
                    break;
                }
            }
            if( flag ){
                this.yIndex = i;
                for( var j=0; j<this.range.days.length; j++ ){
                    var idx = this.calendar.getDateNumOfWeek( this.range.days[j] );
                    this.day.usefulTdFlagArray[i][idx] = false;
                }
                break;
            }
        }
    },
    createNode : function(){
        var lightColor = this.lightColor = MWFCalendar.ColorOptions.getLightColor( this.data.color );

        var node = this.node = new Element("div",{
            styles : {
                position : "absolute",
                //border : "1px solid #cae2ff",
                "background-color" : "#cae2ff",
                "overflow" : "hidden",
                "height" : "20px",
                "line-height" : "20px",
                "border-top" : "1px solid " + lightColor,
                "border-bottom" : "1px solid " + lightColor
            },
            events : {
                click : function(){
                    var form = new MWF.xApplication.Calendar.EventForm(this, this.data, {
                        isFull : true
                }, {app:this.app});
                    form.view = this.view;
                    form.edit();
                }.bind(this),
                "mouseover" : function () {
                    this.node.setStyle("border-color", this.data.color );
                }.bind(this),
                "mouseout" : function () {
                    this.node.setStyle("border-color", this.lightColor );
                }.bind(this)
            }
        }).inject( this.container );

        node.setStyles(this.getCoordinate());

        if( !this.startTimeOutRange ){
            node.setStyles({
                "border-left" : "1px solid " + lightColor,
                "border-top-left-radius" : "10px",
                "border-bottom-left-radius" : "10px"
            })
        }
        if( !this.endTimeOutRange ){
            node.setStyles({
                "border-right" : "1px solid " + lightColor,
                "border-top-right-radius" : "10px",
                "border-bottom-right-radius" : "10px"
            })
        }

        //if( this.isFirst ){
            var timeNode = new Element("div",{
                styles : {
                    "font-size" : "10px",
                    "padding-left" : "2px",
                    "float" : "left"
                },
                text : this.data.timeStart.format("%m-%d %H:%M") + "至" + this.data.timeEnd.format("%m-%d %H:%M")
            }).inject( node );

            var titleNode = new Element("div",{
                styles : {
                    "padding-left" : "5px",
                    "font-size" : "12px",
                    "float" : "left"
                },
                text : this.data.title
            }).inject( node );
        //}


        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "y", "delay" : 350
        });
    },
    getCoordinate : function(){
        var data = this.data;
        var range = this.range;
        var top = this.yIndex * 24;

        var timeStart = this.day.weekStartTime;
        var timeEnd = this.day.weekEndTime;

        if( this.data.timeStart < timeStart ){
            this.startTimeOutRange = true;
        }else{
            this.startTimeOutRange = false;
            timeStart = this.data.timeStart;
        }

        if( this.data.timeEnd > timeEnd ){
            this.endTimeOutRange = true;
        }else{
            this.endTimeOutRange = false;
            timeEnd = this.data.timeEnd;
        }
        var diff = timeEnd - timeStart;

        var width = (  diff / MWFCalendarWeekView.WeekMsec  ) * MWFCalendarWeekView.WeekWidth - 2;
        var left = ( ( timeStart - this.day.weekStartTime ) / MWFCalendarWeekView.WeekMsec ) * MWFCalendarWeekView.WeekWidth + 3;
        //var marginRight = ( ( this.weekTimeEnd - timeEnd ) / MWFCalendarWeekView.DayMsec) * MWFCalendarWeekView.DayWidth;
        return {
            top : top + 2,
            left : left,
            width : width // + ( !this.isLast ? 2 : -2 ) //,
            //"margin-left" : marginLeft,
            //"margin-right" : marginRight
        }
    },
    resize : function(){
        this.node.setStyles(this.getCoordinate());
    },
    reload: function(){
        if( this.tooltip )this.tooltip.destroy();
        this.view.reload();
    },
    destroy : function(){
        if( this.tooltip )this.tooltip.destroy();
        this.node.destroy();
    }
});

MWFCalendarWeekView.Calendar.Day = new Class({
    Implements: [Events],
    initialize: function(container, date, calendar,  data){
        this.container = container;
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date.clone();
        this.data = data;
        this.calendars = [];
        this.load();
    },
    load: function(){
        this.day = this.date.getDate();
        this.month = this.date.getMonth();
        this.year = this.date.getFullYear();

        this.startTime = new Date( this.year, this.month, this.day, 0, 0, 0 );
        this.endTime = new Date( this.year, this.month, this.day, 23, 59, 59 );

        this.rangeList = [];
        this.rangeObject = {};
        this.data.each( function(d , i){
            var range = this.getTimeRange( d.startTime, d.endTime );
            if( !range )return null;
            d.range = range;
            d.range.id = d.id;
            this.rangeList.push( range );
            this.rangeObject[d.id] = range;
        }.bind(this));

        this.sortRange();


        var length = this.data.length;
        this.documentList = [];
        this.data.each( function(d , i){
            var range = d.range;
            range.dayRangeCount = length;
            range.index = this.rangeList.indexOf( range );
            if( !range )return null;
            //var coordinate = this.getCoordinate( d, range );
            this.documentList.push( new MWFCalendarWeekView.Calendar.Document(this.container, this, d, range ) );
        }.bind(this))
    },
    sortRange : function(){
        this.rangeList.sort( function(range1, range2){
            return range1.startTime - range2.startTime;
        });
    },
    getTimeRange: function( bDate1,  eDate1 ){
        var bDate = typeOf(bDate1) == "string" ? Date.parse(bDate1) : bDate1;
        var eDate = typeOf(eDate1) == "string" ? Date.parse(eDate1) : eDate1;
        if( eDate <= this.startTime ){ //比当天天12点更晚
            return null;
        }
        if( this.endTime <= bDate ){
            return null;
        }
        var range = {
            start: ( bDate <= this.startTime  ) ? [0, 0, 0] : [bDate.get("hr"), bDate.get("min"), bDate.get("sec")],
            end: ( this.endTime <= eDate ) ? [23, 59, 59] : [eDate.get("hr"), eDate.get("min"), eDate.get("sec")]
        };
        range.startTime = new Date( this.year, this.month, this.day, range.start[0], range.start[1], range.start[2] );
        range.endTime = new Date( this.year, this.month, this.day, range.end[0], range.end[1], range.end[2] );
        range.diff = range.endTime - range.startTime;
        return range;
    },
    resize : function(){
        if(!this.documentList)return;
        this.documentList.each( function( doc ){
            doc.resize();
        }.bind(this))
    },
    reload: function(){
        this.view.reload();
    },
    destroy : function(){
        while( this.documentList.length > 0 ){
            this.documentList.pop().destroy();
        }
        this.container.empty();
    }
});

MWFCalendarWeekView.Calendar.Document = new Class({
    initialize: function(container, day, data, range, coordinate ){
        this.container = container;
        this.day = day;
        this.calendar = day.calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = day.date.clone();
        this.data = data;
        this.range = range;
        this.coordinate = coordinate; //today, otherMonth, thisMonth
        this.load();
    },
    load: function(){

        var lightColor = this.lightColor = MWFCalendar.ColorOptions.getLightColor( this.data.color );

        var node = this.node = new Element("div",{
            styles : {
                position : "absolute",
                border : "1px solid "+lightColor,
                "background-color" : lightColor,
                "overflow" : "hidden",
                "border-radius" : "5px"
            },
            events : {
                click : function(){
                    var form = new MWF.xApplication.Calendar.EventForm(this, this.data, {
                        isFull : true
                    }, {app:this.app});
                    form.view = this.view;
                    form.edit();
                }.bind(this),
                "mouseover" : function () {
                    this.node.setStyle("border-color", this.data.color );
                }.bind(this),
                "mouseout" : function () {
                    this.node.setStyle("border-color", this.lightColor );
                }.bind(this)
            }
        }).inject( this.container );
        node.setStyles(this.getCoordinate());

        var timeNode = new Element("div",{
            styles : {
                "font-size" : "10px",
                "padding-top" : "2px",
                "padding-left" : "2px"
            },
            text : this.range.startTime.format("%H:%M") + "-" + this.range.endTime.format("%H:%M")
        }).inject( node );

        var titleNode = new Element("div",{
            styles : {
                "padding-top" : "10px",
                "padding-left" : "5px",
                "font-size" : "12px"
            },
            text : this.data.title
        }).inject( node );

        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x", "delay" : 350
        });

    },
    resize : function(){
        this.node.setStyles(this.getCoordinate());
    },
    getCoordinate : function(){
        var data = this.data;
        var range = this.range;
        var width_div = 8;
        var top_div = 0;
        if(Browser.name === "ie" ){
            width_div = 4;
            top_div = -2
        }
        var height = Math.floor( ( ( range.endTime - range.startTime ) / MWFCalendarWeekView.DayMsec ) * MWFCalendarWeekView.DayHeight)-width_div;

        if(height < 16) height = 16;
        var top = Math.floor( ( ( range.startTime - this.day.startTime ) / MWFCalendarWeekView.DayMsec ) * MWFCalendarWeekView.DayHeight)-top_div;
        //var width = Math.floor(  MWFCalendarWeekView.DayWidth / length )-2;
        //var left = ( width + 2)* index + 1;

        //var width = Math.floor(  MWFCalendarWeekView.DayWidth / range.path.length  )-5;
        //var left = ( width + 5)* range.path.indexOf( data.id ) + 3;

        var width = Math.floor(  MWFCalendarWeekView.DayWidth / this.range.dayRangeCount  )-5;
        var left = ( width + 5)* this.range.index + 3;
        return {
            top : top,
            left : left,
            width : width,
            height : height
        }
    },
    reload: function(){
        if( this.tooltip )this.tooltip.destroy();
        this.view.reload();
    },
    destroy: function(){
        if( this.tooltip )this.tooltip.destroy();
        this.node.destroy()
    }
});

MWFCalendarWeekView.WeekCalendar = new Class({
    Extends : MWF.widget.Calendar,
    initialize: function(node, options){
        this.options.weekBegin = "0";
        Locale.use("zh-CHS");
        this.options.defaultTime = ""+this.options.baseDate.getHours()+":"+this.options.baseDate.getMinutes()+":"+this.options.baseDate.getSeconds();
        this.setOptions(options);

        this.path = MWF.defaultPath+"/widget/$Calendar/";
        this.cssPath = MWF.defaultPath+"/widget/$Calendar/"+this.options.style+"/css.wcss";

        this._loadCss();
        //	this.options.containerPath = this.path+this.style+"/container.html";
        //	this.options.dayPath = this.path+this.style+"/day.html";
        //	this.options.monthPath = this.path+this.style+"/month.html";
        //	this.options.yearPath = this.path+this.style+"/year.html";
        //	this.options.timePath = this.path+this.style+"/time.html";

        if (!this.options.format){
            if (this.options.isTime){
                //this.options.format = Locale.get("Date").shortDate + " " + Locale.get("Date").shortTime;
                if(this.options.timeOnly){
                    this.options.format="%H:%M";
                }
                else{
                    this.options.format = Locale.get("Date").shortDate + " " + "%H:%M";
                }
            }else{
                this.options.format = Locale.get("Date").shortDate;
            }
        }

        this.options.containerPath = this.options.path+this.options.style+"/container.html";
        this.options.dayPath = this.options.path+this.options.style+"/day_week.html";
        this.options.monthPath = this.options.path+this.options.style+"/month.html";
        this.options.yearPath = this.options.path+this.options.style+"/year.html";
        this.options.timePath = this.options.path+this.options.style+"/time.html";

        this.today = new Date();

        this.currentView = this.options.defaultView;

        this.node = $(node);

        this.visible = false;



        this.container = this.createContainer();


        this.container.inject((this.options.target) || $(document.body));

        this.contentTable = this.createContentTable();
        this.contentTable.inject(this.contentDateNode);

        this.addEvents();
        this.container.set({
            styles: {
                "display": "none",
                "opacity": 1
            }
        });
        this.fireEvent("init");

        //this.move = true;
        //this.containerDrag = new Drag.Move(this.container);
    },
    showDay: function(year, month){
        this._setDayTitle(null, year, month);
        this._setDayWeekTitleTh();
        this._setDayDate(null, year, month);

        //	if (!this.move){
        //		this.move = true;
        //		this.containerDrag = new Drag.Move(this.container);
        //	}
    },
    _setDayTitle: function(node, year, month){
        var thisYear = (year!=undefined) ? year : this.options.baseDate.getFullYear();
        var thisMonth = (month!=undefined) ? month : this.options.baseDate.getMonth();
        thisMonth++;

        var text = thisYear+"年"+thisMonth+"月";
        var thisNode = node || this.currentTextNode;
        thisNode.set("text", text);

        thisNode.store("year", thisYear);
        thisNode.store("month", thisMonth);
    },

    _setDayWeekTitleTh: function(table){
        var dayTable = table || this.contentTable;

        var thead = dayTable.getElement("thead");
        var cells = thead.getElements("th");

        if (this.css.calendarDaysContentTh) cells.setStyles(this.css.calendarDaysContentTh);

        //var days_abbr = Locale.get("Date").days_abbr;
        var days_abbr = MWF.LP.widget.days_abbr;
        cells.each(function(item, idx){
            if( idx == 0 ){
                item.set("text", "周");
            }else{
                //var index;
                //if( this.options.weekBegin == "0" ){
                //    index = idx-1;
                //}else{
                //    index = idx == 7 ? 0 : idx;
                //}
                var index = (  idx - 1 + parseInt( this.options.weekBegin ) ) % 7;
                item.set("text", days_abbr[index]);
            }
        }.bind(this));
        return cells;
    },
    _setDayDate: function(table, year, month){
        var dayTable = table || this.contentTable;
        var baseDate = this.options.baseDate;
        if ((year!=undefined) && (month!=undefined)){
            baseDate = new Date();
            baseDate.setDate(1);
            baseDate.setFullYear(year);
            baseDate.setMonth(month);
        }

        var tbody = dayTable.getElement("tbody");
        var tds = tbody.getElements("td");

        var firstDate = baseDate.clone();
        firstDate.setDate(1);
        //if( this.options.weekBegin == "0" ){
        //    var day = firstDate.getDay()+1;
        //}else{
        //
        //}
        var day = ( 7 + firstDate.getDay() - parseInt( this.options.weekBegin ) ) % 7 + 1;

        var tmpDate = firstDate.clone();
        for (var i=day-1; i>=0; i--){
            if( i % 8 == 0 ){ //设置周数
                var week = this.getWeekNumber( tmpDate );
                tds[i].set("text", week);
                tds[i].setStyles(this.css.week);
                tds[i].store("weekValue", week.toString());
                tds[i].store("dateValue", tmpDate.toString());
                i--;
                if( i<0 )break;
            }
            tmpDate.increment("day", -1);
            tds[i].set("text", tmpDate.getDate());
            tds[i].addClass("gray_"+this.options.style);
            tds[i].setStyles(this.css["gray_"+this.options.style]);
            tds[i].store("dateValue", tmpDate.toString())
        }

        for (var i=day; i<tds.length; i++){

            if( i % 8 == 0 ){ //设置周数
                var week = this.getWeekNumber( firstDate );
                tds[i].set("text", week );
                //tds[i].addClass("gray_"+this.options.style);
                tds[i].setStyles(this.css.week);
                tds[i].store("weekValue", week.toString());
                tds[i].store("dateValue", firstDate.toString());
                i++;
                if( i>=tds.length )break;
            }

            tds[i].set("text", firstDate.getDate());
            if (firstDate.toString() == this.options.baseDate.toString()){
                tds[i].addClass("current_"+this.options.style);
                tds[i].setStyles(this.css["current_"+this.options.style]);

                tds[i].removeClass("gray_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }else if (firstDate.getMonth()!=baseDate.getMonth()){
                tds[i].addClass("gray_"+this.options.style);
                tds[i].setStyles(this.css["gray_"+this.options.style]);
                tds[i].removeClass("current_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }else{
                tds[i].setStyles(this.css["normal_"+this.options.style]);
                tds[i].removeClass("current_"+this.options.style);
                tds[i].removeClass("gray_"+this.options.style);
                tds[i].setStyle("border", "1px solid #FFF");
            }
            var tmp = firstDate.clone();
            if (tmp.clearTime().toString() == this.today.clearTime().toString()){
                //tds[i].addClass("today_"+this.options.style);
                tds[i].setStyles(this.css["today_"+this.options.style]);
                tds[i].setStyle("border", "0px solid #AAA");
            }
            tds[i].store("dateValue", firstDate.toString());
            firstDate.increment("day", 1);
        }
    },
    getWeekNumber: function( d  ){
        // Create a copy of this date object
        var target  = d.clone();

        // ISO week date weeks start on monday
        // so correct the day number
        //if( this.options.weekBegin == "1" ){
        //    var dayNr   = (d.getDay() + 6) % 7;
        //}else{
        //    var dayNr   = (d.getDay() + 7) % 7;
        //}
        var dayNr   =  ( 7 + d.getDay() - parseInt( this.options.weekBegin ) ) % 7;

        // ISO 8601 states that week 1 is the week
        // with the first thursday of that year.
        // Set the target date to the thursday in the target week
        target.setDate(target.getDate() - dayNr + 3);

        // Store the millisecond value of the target date
        var firstThursday = target.valueOf();

        // Set the target to the first thursday of the year
        // First set the target to january first
        target.setMonth(0, 1);
        // Not a thursday? Correct the date to the next thursday
        if (target.getDay() != 4) {
            target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
        }

        // The weeknumber is the number of weeks between the
        // first thursday of the year and the thursday in the target week
        return 1 + Math.ceil((firstThursday - target) / 604800000); // 604800000 = 7 * 24 * 3600 * 1000
    }


});