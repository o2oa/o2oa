MWF.require("MWF.widget.Calendar", null, false);
var MWFCalendarDayView = MWF.xApplication.Calendar.DayView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Calendar/$DayView/";
        this.cssPath = "../x_component_Calendar/$DayView/"+this.options.style+"/css.wcss";
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
    },

    loadCalendar: function(){
        var date = "";
        if( this.options.date ){
            date = Date.parse( this.options.date )
        }else{
            date = new Date();
        }
        this.calendar = new MWFCalendarDayView.Calendar(this, date  );

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
            if( this.calendar.dataTable_wholeDay ){
                this.calendar.dataTable_wholeDay.setStyle("display","");
            }
        }.bind(this));
    },
    reload: function(){
        if (this.calendar) this.calendar.reLoadCalendar();
    },
    recordStatus : function(){
        var date = "";
        if (this.calendar) date = this.calendar.date;
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
    }
});


MWFCalendarDayView.DayWidth;
MWFCalendarDayView.HourHeight = 48;
MWFCalendarDayView.DayHeight = MWFCalendarDayView.HourHeight*24;
MWFCalendarDayView.DayMsec = 3600 * 24 * 1000;

MWFCalendarDayView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.lp = this.app.lp;
        this.date = date || new Date();
        this.today = new Date();
        this.load();
    },
    load: function(){

        this.startTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 0, 0, 0 );
        this.startTimeStr = this.startTime.format( "db" );

        this.endTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 23, 59, 59 );
        this.endTimeStr = this.endTime.format( "db" );

        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);


        this.titleTable = new Element("table", {
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

        //this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.loadTitleTable();
        this.loadBodyTable();

        //this.resetBodySize();

        this.loadDataTable_wholeDay();
        this.loadDataTable();

        this.loadCalendar();

    },
    setTitleNode: function(){
        this.prevDayNode =  new Element("div", {"styles": this.css.calendarPrevDayNode}).inject(this.titleNode);

        var text = this.date.format(this.lp.dateFormatDay) + "，" + this.lp.weeks.arr[ this.date.getDay() ];


        this.titleTextNode = new Element("div", {"styles": this.css.calendarTitleTextNode, "text": text}).inject(this.titleNode);

        this.nextDayNode =  new Element("div", {"styles": this.css.calendarNextDayNode}).inject(this.titleNode);

        this.prevDayNode.addEvents({
            "mouseover": function(){this.prevDayNode.setStyles(this.css.calendarPrevDayNode_over);}.bind(this),
            "mouseout": function(){this.prevDayNode.setStyles(this.css.calendarPrevDayNode);}.bind(this),
            "mousedown": function(){this.prevDayNode.setStyles(this.css.calendarPrevDayNode_down);}.bind(this),
            "mouseup": function(){this.prevDayNode.setStyles(this.css.calendarPrevDayNode_over);}.bind(this),
            "click": function(){this.changeDayPrev();}.bind(this)
        });
        this.nextDayNode.addEvents({
            "mouseover": function(){this.nextDayNode.setStyles(this.css.calendarNextDayNode_over);}.bind(this),
            "mouseout": function(){this.nextDayNode.setStyles(this.css.calendarNextDayNode);}.bind(this),
            "mousedown": function(){this.nextDayNode.setStyles(this.css.calendarNextDayNode_down);}.bind(this),
            "mouseup": function(){this.nextDayNode.setStyles(this.css.calendarNextDayNode_over);}.bind(this),
            "click": function(){this.changeDayNext();}.bind(this)
        });
        this.titleTextNode.addEvents({
            "mouseover": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "mouseout": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode);}.bind(this),
            "mousedown": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_down);}.bind(this),
            "mouseup": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this)
            //"click": function(){this.changeDaySelect();}.bind(this)
        });
        this.createDaySelector();
    },
    changeDayPrev: function(){
        this.date.decrement("day", 1);
        var text = this.date.format(this.lp.dateFormatDay) + "，" + this.lp.weeks.arr[ this.date.getDay() ];
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeDayNext: function(){
        this.date.increment("day", 1);
        var text = this.date.format(this.lp.dateFormatDay) + "，" + this.lp.weeks.arr[ this.date.getDay() ];
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeDaySelect: function(){
        if (!this.monthSelector) this.createDaySelector();
        //this.monthSelector.show();
    },
    createDaySelector: function(){
        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(this.titleTextNode, {
                "style":"meeting_blue",
                "target": this.node,
                "baseDate" : this.date,
                "onQueryComplate": function(e, dv, date){
                    var selectedDate = new Date.parse(dv);
                    this.changeDayTo(selectedDate);
                }.bind(this)
            });
        }.bind(this));
    },
    changeDayTo: function(d){
        this.date = d;

        var text = this.date.format(this.lp.dateFormatDay) + "，" + this.lp.weeks.arr[ this.date.getDay() ];

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
    isSameday : function( d1, d2 ){
        if( d1.get("year") != d2.get("year") )return false;
        if( d1.get("month") != d2.get("month") )return false;
        if( d1.get("date") != d2.get("date") )return false;
        return true;
    },
    loadTitleTable: function(){
        if( !this.wholeDayTd ){
            var tr = new Element( "tr").inject( this.titleTable );
            var td = new Element( "td.calendarTableCell", {
                "tdType" : "hour",
                styles : this.css.calendarTableCell_hour
            } ).inject( tr );
            td.setStyle("min-height","80px");
            var node = new Element("div",{
                text : this.lp.allDay
            }).inject( td );
            this.wholeDayTd =  new Element( "td" , {
                "tdType" : "calendar",
                "styles" : this.css.calendarTableCell
            }).inject( tr );

            this.wholeDayTd.addEvent("click", function(ev){
                this.setCurrentTd( this.wholeDayTd );
            }.bind(this));

            this.wholeDayTd.addEvent("dblclick", function(ev){
                this.cancelCurrentTd();
                var dateStr = this.date.format("%Y-%m-%d");
                var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                    startTime : Date.parse( ev.target.retrieve("dateStr")) ,
                    endTime : Date.parse( ev.target.retrieve("dateStr")),
                    isWholeday : true
                }, {app:this.app});
                form.view = this;
                form.create();
            }.bind(this));
        }
    },


    loadBodyTable: function(){
        this.calendarTable = new Element("table", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.bodyNode);

        this.hourTdMap = {};
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

            td = this.hourTdMap[i] = new Element( "td" , {
                "tdType" : "calendar",
                "styles" : this.css.calendarTableCell
            }).inject( tr );
            td.store("hour",i );

            td.addEvent("click", function(ev){
                this.setCurrentTd( ev.target );
            }.bind(this));

            td.addEvent("dblclick", function(ev){
                this.cancelCurrentTd();
                var hour = ev.target.retrieve("hour");
                var dateStr = this.date.format( "%Y-%m-%d" );
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

    },
    setCurrentTd : function(td){
        td.setStyle("background-color","#fffdf2");
        if( this.currentSelectedTd ){
            var flag = this.isToday( this.date );
            this.currentSelectedTd.setStyle("background-color",flag?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = td;
    },
    cancelCurrentTd : function(){
        if( this.currentSelectedTd ){
            var flag = this.isToday( this.date );
            this.currentSelectedTd.setStyle("background-color",flag?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = null;
    },

    reLoadCalendar: function(){
        this.startTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 0, 0, 0 );
        this.startTimeStr = this.startTime.format( "db" );

        this.endTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 23, 59, 59 );
        this.endTimeStr = this.endTime.format( "db" );

        if( this.day ){
            this.day.destroy();
        }
        this.day = null;

        if( this.wholeDay ){
            this.wholeDay.destroy();
        }
        this.wholeDay = null;

        //this.calendarTable.getElements("td[tdType='calendar']").each( function(td){
        //    td.empty();
        //}.bind(this));

        this.loadTitleTable();

        this.loadCalendar();
    },
    loadCalendar : function(){
        this.app.currentDate = this.date.clone();

        this.dateIndexMap = null;
        this.titleTable.getElement("td:nth-child(1)").setStyle("height", "auto") ;
        this.loadData( function( json ){
            this.loadwholeDay(this.wholeDayData);
            this.loadDataDay( this.inOneDayEvents );
            this.resetBodySize();
            this.setTodayTds();
            this.cancelCurrentTd();
        }.bind(this));

    },
    setTodayTds : function(){
        if( this.isToday( this.date ) ){
            this.wholeDayTd.setStyle("background-color","#f8fbff");
            Object.each( this.hourTdMap, function( td ){
                td.setStyle("background-color","#f8fbff");
            });
            var now = new Date();
            var nowTd =  this.nowTd = this.hourTdMap[ now.get("hours") ];
            var mintues = now.get("minutes");
            mintues = mintues < 2 ? 2 : mintues;

            var nowTdPosition = nowTd.getPosition(this.bodyNode);
            this.nowTimeNode = new Element("div",{
                styles : {
                    "position" : "absolute",
                    "left" : nowTdPosition.x,
                    "top" : nowTdPosition.y + ( (mintues - 2) / 60 ) * MWFCalendarDayView.HourHeight,
                    "height" : "2px",
                    "width" : MWFCalendarDayView.DayWidth,
                    "background-color" : "#ff3333"
                }
            }).inject(this.bodyNode);
            this.isSetToday = true;
        }else if( this.isSetToday ){
            this.wholeDayTd.setStyle("background-color","#fff");
            Object.each( this.hourTdMap, function( td ){
                td.setStyle("background-color","#fff");
            });
            if(this.nowTimeNode)this.nowTimeNode.destroy();
            if(this.nowTd)this.nowTd = null;
            this.isSetToday = false;
        }
    },
    loadData : function( callback ){
        this.app.actions.listEventWithFilter( {
            calendarIds : this.app.getSelectedCalendarId(),
            startTime : this.startTimeStr,
            endTime : this.endTimeStr //,
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
            //        this.dayData.push(d)
            //    }
            //}.bind(this));
            if(callback)callback();
        }.bind(this));
    },
    loadDataTable_wholeDay: function( json ){

        this.dataTable_wholeDay = new Element("table.dataTable", {
            "styles": this.css.calendarTable,
            "height" : "0",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.container);
        this.dataTable_wholeDay.setStyles({
            "display" : "none",
            "position":"absolute",
            "top" : "51px",
            "left" : "0px",
            "margin": "0px auto 0px 12px"
        });
        var tr = new Element("tr").inject(this.dataTable_wholeDay);

        new Element( "td" , {
            "styles" : {"height":"0px" ,"position":"relative"}
        }).inject( tr );

        this.dataTd_wholeDay = new Element( "td" , {
            "valign" : "top",
            "styles" : {"height":"0px","position":"relative"}
        }).inject( tr );
    },
    loadwholeDay : function( json ){
        this.wholeDay = new MWFCalendarDayView.Calendar.wholeDay( this, json, this.date);
    },

    loadDataTable: function( json ){
        this.bodyNode.setStyle("position","relative");

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

        this.dataTd = new Element( "td" , {
            "styles" : {"height":"0px","position":"relative"}
        }).inject( tr );
    },
    loadDataDay : function(data){
        if( data.length > 0 ){
            this.loadDay( data[0].inOneDayEvents );
        }
        //var dataArray = [];
        //json.each( function( d ){
        //    var date2 = Date.parse(d.startTime );
        //    if( this.isSameday( this.date, date2 ) ){
        //        dataArray.push( d );
        //    }
        //}.bind(this));
        //if( dataArray.length ){
        //    this.loadDay( dataArray );
        //}
    },
    loadDay: function(  dataArray ){
        this.day = new MWFCalendarDayView.Calendar.Day( this.dataTd, this.date, this, dataArray);
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
        var tdX = MWFCalendarDayView.DayWidth = size.x - 40 - hourTdX;
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
            tr.getElements("td").each( function( td, i ){
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
        if( this.day ){
            this.day.resize();
        }

        if( this.dataTable_wholeDay ){
            this.dataTable_wholeDay.setStyles({
                "width": (size.x - 40) +"px"
            });
            var tr =this.dataTable_wholeDay.getElement("tr:nth-child(1)");
            tr.getElements("td").each( function( td, i ){
                td.setStyle("width", (i==0 ? hourTdX : tdX )+"px");
            })
        }
        if(this.wholeDay){
            this.wholeDay.resize();
        }

        if(this.nowTimeNode){
            this.nowTimeNode.setStyle("width",tdX);
            if(this.nowTd)this.nowTimeNode.setStyle("left", this.nowTd.getPosition(this.bodyNode).x );
        }

        //for( var key in this.dayMap_wholeDay ){
        //    this.dayMap_wholeDay[key].resize();
        //}

    },
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        if( this.day ){
            this.day.destroy();
        }
        if( this.wholeDay ){
            this.wholeDay.destroy();
        }


        this.container.empty();
    },

    getIndexByPage: function( page ){
        var pos = this.calendarTable.getPosition();

        this.pageOffsetHeight = page.y - pos.y;
        var row = ( page.y - pos.y ) / MWFCalendarDayView.HourHeight;
        if( row < 0 || row > 24 )return null;
        return Math.floor(row);
    },
    getIndexListByRange : function( index1, index2 ){
        var minIndex = Math.min( index1, index2 );
        var maxIndex = Math.max( index1, index2 );
        var result = [];
        for( var i = minIndex; i<=maxIndex; i++ ){
            result.push( i );
        }
        return result;
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
        var flag = this.isToday( this.date );
        if( this.selectedIndexRange ){
            var oldIndex = this.selectedIndexRange;
            this.selectedIndexRange.each( function( index ){
                if( !indexs.contains(index) ){
                    this.hourTdMap[index].setStyle("background-color", flag ?  "#F8FBFF" : "#fff");
                }
            }.bind(this));
            indexs.each( function( index ){
                if( !this.selectedIndexRange.contains(index) ){
                    this.hourTdMap[index].setStyle("background-color", "#fffdf2")
                }
            }.bind(this))
        }else{
            for( var i=0; i<indexs.length; i++ ){
                this.hourTdMap[indexs[i]].setStyle("background-color", "#fffdf2")
            }
        }
        this.selectedIndexRange = indexs;

        var scrollNodeTop = this.scrollNode.getScroll().y;
        if(( this.pageOffsetHeight + MWFCalendarDayView.HourHeight * 1.5) > ( this.scrollNodeHeight + scrollNodeTop )){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop + MWFCalendarDayView.HourHeight )
            }.bind(this), 200)
        }else if( this.pageOffsetHeight - MWFCalendarDayView.HourHeight * 1.5 < scrollNodeTop ){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop - MWFCalendarDayView.HourHeight )
            }.bind(this), 200)
        }
    },
    completeDrag: function(td, e){
        var flag = this.isToday( this.date );
        if( this.selectedIndexRange && this.selectedIndexRange.length ){
            this.selectedIndexRange.each( function( index ){
                this.hourTdMap[index ].setStyle("background-color", flag ?  "#F8FBFF" : "#fff");
            }.bind(this));
            var beginIndex = this.selectedIndexRange[0];
            var endIndex = this.selectedIndexRange.getLast();
            var beginTime = this.date.format("%Y-%m-%d") + " " + beginIndex+":00";
            var endTime = this.date.format("%Y-%m-%d") + " " + endIndex+":59";
            var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                startTime : beginTime ,
                endTime : endTime
            }, {app:this.app});
            form.view = this;
            form.create();

            this.selectedIndexRange = null;
        }
    }

});

MWFCalendarDayView.Calendar.wholeDay = new Class({
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
        //this.startTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 0, 0, 0 );
        //this.endTime = new Date( this.date.get("year"), this.date.get("month"), this.date.get("date"), 23, 59, 59 );

        this.startTime = this.calendar.startTime;
        this.endTime = this.calendar.endTime;

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

        this.sortRange();

        this.calendar.titleTable.getElement("td:nth-child(1)").setStyle("height", 24*this.rangeList.length + 3) ;

        this.documentList = [];
        this.rangeList.each( function(r , i){
            var d = r.data;
            if( !d )return null;
            this.documentList.push( new MWFCalendarDayView.Calendar.wholeDayDocument( this, d, r ) );
        }.bind(this))

    },
    sortRange : function(){
        this.rangeList.sort( function( range1, range2 ){
            return range2.diff - range1.diff;
        }.bind(this));
    },
    getTimeRange : function( startTime, endTime ){
        var start = Date.parse(startTime );
        var end = Date.parse(endTime );

        if( end < this.startTime )return null;
        if( this.endTime < start )return null;

        if( start < this.startTime )start = this.startTime.clone();
        if( this.endTime < end )end = this.endTime.clone();

        var end = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );

        start = Date.parse(startTime );
        end = Date.parse(endTime );
        return {
            start : start,
            end : end,
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
        this.calendar.dataTd_wholeDay.empty();
    }
});

MWFCalendarDayView.Calendar.wholeDayDocument = new Class({
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
        this.container = this.calendar.dataTd_wholeDay;
        var items = this.items = [];
        this.data.dateStart = Date.parse( this.data.startTime );
        this.data.dateEnd = Date.parse( this.data.endTime );


        this.yIndex = this.getUsefulTdYIndex();

        this.createNode();
        //this.range.days.each( function( d, i ){
        //    items.push( new MWFCalendarDayView.Calendar.wholeDayItem( this, d, i ) )
        //}.bind(this))
    },
    getUsefulTdYIndex : function(){
        if( typeOf( this.day.yIndex )=="null" ){
            this.day.yIndex = 0
        }else{
            this.day.yIndex ++
        }
        return this.day.yIndex;
    },
    createNode : function(){
        var lightColor = this.lightColor = MWFCalendar.ColorOptions.getLightColor( this.data.color );

        var node = this.node = new Element("div",{
            styles : {
                position : "absolute",
                "overflow" : "hidden",
                "height" : "20px",
                "line-height" : "20px",
                "border-top" : "1px solid " + lightColor,
                "border-bottom" : "1px solid " + lightColor,
                "background-color": lightColor
            },
            events : {
                click : function(){
                    var form = new MWF.xApplication.Calendar.EventForm(this, this.data, {
                        isFull : true
                    }, {app:this.app});
                    form.view = this.view;
                    this.calendar.app.isEventEditable(this.data) ? form.edit() : form.open();
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

        if( !this.beginDateOutRange ){
            node.setStyles({
                "border-left" : "1px solid " + lightColor,
                "border-top-left-radius" : "10px",
                "border-bottom-left-radius" : "10px"
            })
        }
        if( !this.endDateOutRange ){
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
            text : this.data.dateStart.format("%m-%d %H:%M") + this.app.lp.to + this.data.dateEnd.format("%m-%d %H:%M")
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
        this.tooltip.view = this.view;
    },
    getCoordinate : function(){
        var data = this.data;
        var range = this.range;
        var top = this.yIndex * 24;

        var dateStart = this.day.startTime;
        var dateEnd = this.day.endTime;

        if( this.data.dateStart < dateStart ){
            this.beginDateOutRange = true;
        }else{
            this.beginDateOutRange = false;
            dateStart = this.data.dateStart;
        }

        if( this.data.dateEnd > dateEnd ){
            this.endDateOutRange = true;
        }else{
            this.endDateOutRange = false;
            dateEnd = this.data.dateEnd;
        }
        var diff = dateEnd - dateStart;

        var width = (  diff / MWFCalendarDayView.DayMsec  ) * MWFCalendarDayView.DayWidth - 2;
        var left = ( ( dateStart - this.day.startTime ) / MWFCalendarDayView.DayMsec ) * MWFCalendarDayView.DayWidth + 3;
        //var marginRight = ( ( this.weekDateEnd - dateEnd ) / MWFCalendarDayView.DayMsec) * MWFCalendarDayView.DayWidth;
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

MWFCalendarDayView.Calendar.Day = new Class({
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

        //this.startTime = new Date( this.year, this.month, this.day, 0, 0, 0 );
        //this.endTime = new Date( this.year, this.month, this.day, 23, 59, 59 );

        this.startTime = this.calendar.startTime;
        this.endTime = this.calendar.endTime;

        this.rangeList = [];
        this.rangeObject = {};
        this.data.each( function(d , i){
            var range = this.getTimeRange( d.startTime, d.endTime );
            if( !range )return null;
            d.range = range;
            d.range.data = d;
            d.range.id = d.id;
            this.rangeList.push( range );
            this.rangeObject[d.id] = range;
        }.bind(this));

        this.sortRange();


        var length = this.data.length;
        this.documentList = [];
        this.rangeList.each( function(r , i){
            r.index = this.rangeList.indexOf( r );
            var d = r.data;
            //var coordinate = this.getCoordinate( d, range );
            this.documentList.push( new MWFCalendarDayView.Calendar.Document(this.container, this, d, r ) );
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

MWFCalendarDayView.Calendar.Document = new Class({
    initialize: function(container, day, data, range ){
        this.container = container;
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

        var lightColor = this.lightColor = MWFCalendar.ColorOptions.getLightColor( this.data.color );

        var node = this.node = new Element("div",{
            styles : {
                position : "absolute",
                border : "1px solid "+lightColor,
                "background-color" : lightColor,
                "overflow" : "hidden",
                "border-radius" : "5px",
                "max-width" : "150px"
            },
            events : {
                click : function(){
                    var form = new MWF.xApplication.Calendar.EventForm(this, this.data, {
                        isFull : true
                    }, {app:this.app});
                    form.view = this.view;
                    this.calendar.app.isEventEditable(this.data) ? form.edit() : form.open();
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
                "padding-top" : "7px",
                "padding-left" : "5px",
                "font-size" : "12px"
            },
            text : this.data.title
        }).inject( node );

        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x", "delay" : 350
        });
        this.tooltip.view = this.view;
    },
    resize : function(){
        this.node.setStyles(this.getCoordinate());
    },
    getCoordinate : function(){
        var data = this.data;
        var range = this.range;
        var width_div = 4;
        var top_div = 0;
        if(Browser.name === "ie" ){
            width_div = 4;
            top_div = -2
        }
        var height = Math.floor( ( ( range.endTime - range.startTime ) / MWFCalendarDayView.DayMsec ) * MWFCalendarDayView.DayHeight)-width_div;

        if(height < 16) height = 16;

        var top = Math.floor( ( ( range.startTime - this.day.startTime ) / MWFCalendarDayView.DayMsec ) * MWFCalendarDayView.DayHeight)-top_div;
        //var width = Math.floor(  MWFCalendarDayView.DayWidth / length )-2;
        //var left = ( width + 2)* index + 1;

        //var width = Math.floor(  MWFCalendarDayView.DayWidth / range.path.length  )-5;
        //var left = ( width + 5)* range.path.indexOf( data.id ) + 3;

        var width = Math.floor(  MWFCalendarDayView.DayWidth / this.day.rangeList.length  )-5;
        if( width > 150 ){
            width = 150;
        }
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