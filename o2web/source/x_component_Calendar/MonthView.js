var MWFCalendarMonthView = MWF.xApplication.Calendar.MonthView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Calendar/$MonthView/";
        this.cssPath = "../x_component_Calendar/$MonthView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        //this.titleContainer = $(titleNode);
        this.container = $(node);
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //this.loadSideBar();
        //this.app.addEvent("resize", this.resetNodeSize.bind(this));
        this.loadCalendar();
        this.resetNodeSize();
    },

    resetNodeSize: function(){
        var size = this.container.getSize();
        var y = size.y-50;
        this.node.setStyle("height", ""+y+"px");
        //this.node.setStyle("margin-top", "60px");


        var sideBarSize = this.app.sideBar ?  this.app.sideBar.getSize() : { x : 0, y:0 };
        this.node.setStyle("width", ""+(size.x - sideBarSize.x)+"px");
        this.node.setStyle("margin-right", ""+sideBarSize.x+"px");

        if( this.calendar ){
            this.calendar.resetBodySize()
        }

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
        }
        this.calendar = new MWFCalendarMonthView.Calendar(this, date  );

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

MWFCalendarMonthView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.date = date || new Date();
        this.today = new Date();
        this.days = {};
        this.weekBegin = this.app.calendarConfig.weekBegin || 0;
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);
        this.titleTableContainer = new Element("div", {"styles": this.css.calendarTitleTableContainer}).inject(this.container);

        this.scrollNode = new Element("div", {
            "styles": this.css.scrollNode
        }).inject(this.container);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.bodyNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);
        this.bodyNode.setStyle("position","relative");

        //this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.setTitleTableNode();
        this.setBodyNode();

        //this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    resetBodySize: function(){
        var size = this.container.getSize();
        var titleSize = this.titleNode.getSize();
        var titleTableSize = this.titleTable.getSize();
        var y = size.y-titleSize.y-titleTableSize.y;
        //this.bodyNode.setStyle("height", ""+y+"px");

        //var size = this.container.getSize();

        this.scrollNode.setStyle("height", ""+y+"px");
        //this.scrollNode.setStyle("margin-top", "60px");

        this.titleTableContainer.setStyles({
            "width": (size.x - 40) +"px"
        });

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": (size.x - 40) +"px"
            });
        }

        var tableSize = this.calendarTable.getSize();
        MWFCalendarMonthView.WeekWidth = tableSize.x;
        MWFCalendarMonthView.DayWidth = tableSize.x / 7;
        this.dataTdList.each( function( td ){
            td.setStyle("width", MWFCalendarMonthView.WeekWidth)
        });

        if( this.wholeDayDocumentList && this.wholeDayDocumentList.length ){
            this.wholeDayDocumentList.each( function( doc ){
                doc.resize();
            }.bind(this))
        }

        if( this.oneDayDocumentList && this.oneDayDocumentList.length ){
            this.oneDayDocumentList.each( function( doc ){
                doc.resize();
            }.bind(this))
        }

        var top = 30; //MWFCalendarMonthView.THHeight + 30;
        var trs = this.calendarTable.getElements("tr");
        this.calendarTrHeight = [];
        for( var key in this.usedYIndex ){
            var idx = this.usedYIndex[key];
            var maxLength = Math.max( idx[0].length, idx[1].length, idx[2].length, idx[3].length, idx[4].length, idx[5].length, idx[6].length );
            if( maxLength > 4 ){
                this.dataTableList[key].setStyle("top", top );
                var height = 30 + maxLength * (22 + 2);
                top =  top + height;
                trs[ parseInt(key) ].getElements("td").each( function(td){
                   td.setStyle("height", height )
                });
                this.calendarTrHeight.push( height );
            }else{
                this.dataTableList[key].setStyle("top", top );
                top =  top + MWFCalendarMonthView.WeekHeight + 1;
                trs[ parseInt(key) ].getElements("td").each( function(td){
                    td.setStyle("height", MWFCalendarMonthView.WeekHeight )
                });
                this.calendarTrHeight.push( MWFCalendarMonthView.WeekHeight );
            }
        }


        //var tdy = (y-30)/6;
        //tdy = tdy-34;
        //var tds = this.calendarTable.getElements("td");
        //tds.each(function(td){
        //    var yy = tdy;
        //    var node = td.getLast("div");
        //    if (node.childNodes.length>=4){
        //        if (yy<92) yy = 69;
        //    }
        //    node.setStyle("height", ""+yy+"px");
        //}.bind(this));


    },
    setTitleNode: function(){
        //this.view.titleContainer.getElements("div:only-child").setStyle("display","none");
        //if( this.titleNode ){
        //    this.titleNode.setStyle("display","")
        //}
        //this.titleNode = new Element("div").inject(this.view.titleContainer);
        this.prevMonthNode =  new Element("div", {"styles": this.css.calendarPrevMonthNode}).inject(this.titleNode);

        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode = new Element("div", {"styles": this.css.calendarTitleTextNode, "text": text}).inject(this.titleNode);

        this.nextMonthNode =  new Element("div", {"styles": this.css.calendarNextMonthNode}).inject(this.titleNode);

        this.prevMonthNode.addEvents({
            "mouseover": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
            "mouseout": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode);}.bind(this),
            "mousedown": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_down);}.bind(this),
            "mouseup": function(){this.prevMonthNode.setStyles(this.css.calendarPrevMonthNode_over);}.bind(this),
            "click": function(){this.changeMonthPrev();}.bind(this)
        });
        this.nextMonthNode.addEvents({
            "mouseover": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
            "mouseout": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode);}.bind(this),
            "mousedown": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_down);}.bind(this),
            "mouseup": function(){this.nextMonthNode.setStyles(this.css.calendarNextMonthNode_over);}.bind(this),
            "click": function(){this.changeMonthNext();}.bind(this)
        });
        this.titleTextNode.addEvents({
            "mouseover": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "mouseout": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode);}.bind(this),
            "mousedown": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_down);}.bind(this),
            "mouseup": function(){this.titleTextNode.setStyles(this.css.calendarTitleTextNode_over);}.bind(this),
            "click": function(){this.changeMonthSelect();}.bind(this)
        });
    },
    changeMonthPrev: function(){
        this.date.decrement("month", 1);
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeMonthNext: function(){
        this.date.increment("month", 1);
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },
    changeMonthSelect: function(){
        if (!this.monthSelector) this.createMonthSelector();
        this.monthSelector.show();
    },
    createMonthSelector: function(){
        this.monthSelector = new MWFCalendarMonthView.MonthSelector(this.date, this);
    },
    changeMonthTo: function(d){
        this.date = d;
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },

    setTitleTableNode : function(){
        if( this.weekBegin == "1" ){
            var html = "<tr><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
                "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th><th>"+this.app.lp.weeks.Sun+"</th></tr>";
        }else{
            var html = "<tr><th>"+this.app.lp.weeks.Sun+"</th><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
                "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th></tr>";
        }
        this.titleTable = new Element("table", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0",
            "html": html
        }).inject(this.titleTableContainer);
        this.calendarTableTitleTr = this.titleTable.getElement("tr");
        this.calendarTableTitleTr.setStyles(this.css.calendarTableTitleTr);
        var ths = this.calendarTableTitleTr.getElements("th");
        ths.setStyles(this.css.calendarTableTh);
    },
    setBodyNode: function(){
        var html = "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        this.calendarTable = new Element("table", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0",
            "html": html
        }).inject(this.bodyNode);

        this.loadBackgroundCalendar( true );
        this.loadDataContainer();

        //var tds = this.calendarTable.getElements("td");
        //tds.setStyles(this.css.calendarTableCell);

        this.loadCalendar();
    },
    reLoadCalendar: function(){
        if( this.wholeDayDocumentList && this.wholeDayDocumentList.length ){
            this.wholeDayDocumentList.each( function( doc ){
                doc.destroy();
            }.bind(this))
        }
        this.wholeDayDocumentList = [];

        if( this.oneDayDocumentList && this.oneDayDocumentList.length ){
            this.oneDayDocumentList.each( function( doc ){
                doc.destroy();
            }.bind(this))
        }
        this.oneDayDocumentList = [];

        this.loadBackgroundCalendar( false );
        this.loadCalendar();
    },
    loadDataContainer : function(){
        this.dataTableList = [];
        this.dataTdList = [];
        [0,1,2,3,4,5,6].each( function( i){
            var dataTable = new Element("table.dataTable", {
                "styles": this.css.calendarTable,
                "border": "0",
                "cellPadding": "0",
                "cellSpacing": "0",
                "index" : i
            }).inject(this.bodyNode);
            dataTable.setStyles({
                //"display" : "none",
                "position":"absolute",
                "top" : (MWFCalendarMonthView.WeekHeight + 1) * i + MWFCalendarMonthView.THHeight + 30,
                "left" : "0px",
                "margin": "0px auto 0px 0px"
            });
            var tr = new Element("tr").inject(dataTable);

            var dataTd = new Element( "td" , {
                "valign" : "top",
                "styles" : {"height":"0px","position":"relative"}
            }).inject( tr );
            this.dataTableList.push( dataTable );
            this.dataTdList.push( dataTd );
        }.bind(this))
    },
    calculateMonthRange : function(){
        var date =  this.date.clone();

        var start = new Date( date.get("year"), date.get("month"), 1, 0, 0, 0 );
        var week = start.getDay();
        if( this.weekBegin == "1" ){
            var decrementDay = ((week-1)<0) ? 6 : week-1;
        }else{
            var decrementDay = week;
        }
        start.decrement("day", decrementDay);
        this.monthStart = start;
        this.monthStartStr = this.monthStart.format("db");

        var end = start.clone();
        end.increment("day", 41);
        this.monthEnd =  new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
        this.monthEndStr = this.monthEnd.format("db");

        this.calculateWeekRange();
    },
    calculateWeekRange: function(){
        this.weekRangeList = [];
        var start = this.monthStart.clone();
        var end;
        for( var i=0; i<6; i++ ){
            end = start.clone().increment("day", 6);
            end = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
            this.weekRangeList.push( {
                start : start,
                end : end
            });
            start = end.clone().increment("second",1);
        }

        this.weekDaysList = [];
        start = this.monthStart.clone();
        for( var i=0; i<this.weekRangeList.length; i++ ){
            var j =0;
            var days = [];
            while( j<7 ){
                days.push( start.format("%Y-%m-%d") );
                start.increment("day",1);
                j++;
            }
            this.weekDaysList.push(days);
        }


        this.usedYIndex = {};
        for( var i=0; i<this.weekRangeList.length; i++ ){
            this.usedYIndex[i] = {};
            var j =0;
            while( j<7 ){
                this.usedYIndex[i][j] = [];
                j++;
            }
        }
    },
    getUserfulYIndex : function( weekIndex, dayNumbersOfWeek ){ //获取指定周的y轴上的占用情况
        var yIndex = 0;
        var flag = true;
        var weekUsed = this.usedYIndex[weekIndex];
        while( flag ){
            var isContains = false;
            for( var j = 0; j<dayNumbersOfWeek.length; j++ ){
                if( weekUsed[ dayNumbersOfWeek[j]].contains( yIndex ) ){
                    isContains = true;
                    break;
                }
            }
            if( !isContains ){
                flag = false;
            }else{
                yIndex ++;
            }
        }
        for( var j = 0; j<dayNumbersOfWeek.length; j++ ){
            weekUsed[ dayNumbersOfWeek[j]].push( yIndex );
        }
        return yIndex;
    },
    getDateIndex : function( date ) {
        var dateStr = date.format("%Y-%m-%d");
        for (var i = 0; i < this.weekDaysList.length; i++) {
            var index = this.weekDaysList[i].indexOf(dateStr);
            if( index > -1 ){
                return {
                    weekIndex: i,
                    dayIndex: index
                }
            }
        }
        return null;
    },
    getDateIndexOfWeek : function( weekIndex, days ){
        var weekDays = this.weekDaysList[weekIndex];
        var indexs = [];
        for( var i=0; i<days.length;i++ ){
            indexs.push( weekDays.indexOf( days[i] ) );
        }
        return indexs;
    },
    inCurrentMonth : function( time ){
        return time > this.monthStart && time < this.monthEnd;
    },
    getTimeRange : function( bDate, eDate ){
        if( bDate > this.monthEnd || eDate < this.monthStart  )return null;
        var range = {
            startTime : bDate,
            endTime : eDate,
            start: ( bDate <= this.monthStart  ) ? this.monthStart.clone() : bDate.clone(),
            end: ( this.monthEnd <= eDate ) ? this.monthEnd.clone() : eDate.clone()
        };
        range.firstDay = range.start.clone().clearTime();
        range.diff = range.start - range.end;
        range.weekInforList = this.getWeekInfor(bDate, eDate);
        return range;
    },
    getWeekInfor : function( startTime, endTime ){
        if( startTime > this.monthEnd || endTime < this.monthStart  )return null;
        var rangeWeekInfor = {};
        for( var i=0 ; i<this.weekRangeList.length; i++ ){
            var range = this.weekRangeList[i];
            if(startTime > range.end || endTime < range.start )continue;
            var isStart = startTime >= range.start;
            var isEnd =  range.end >= endTime;
            var start =  isStart ? startTime : range.start;
            var end =  isEnd ? endTime : range.end;
            var diff = end - start;
            var left = start - range.start;
            var days = this.getDaysByRange(start, end);
            var daysIndex = this.getDateIndexOfWeek( i, days );
            rangeWeekInfor[i] = {
                index : i,
                isEventStart : isStart,
                isEventEnd : isEnd,
                start : start,
                end : end,
                diff : diff,
                days : days,
                left : left,
                daysIndex : daysIndex
            };
            if( isEnd )break;
        }
        return rangeWeekInfor;
    },
    getDaysByRange : function( startTime, endTime ){
        var start = startTime.clone();
        var end = endTime;
        var days = [];
        while( start < end ){
            days.push( start.clone().format("%Y-%m-%d") );
            start.increment()
        }
        return days;
    },
    loadCalendar: function(){
        this.app.currentDate = this.date.clone();

        this.calculateMonthRange();
        this.cancelCurrentTd();
        this.loadData( function(){
            this.loadWholeDay( this.wholeDayData );
            this.loadOneDay( this.inOneDayEvents );
            this.resetBodySize();
        }.bind(this));
    },
    loadBackgroundCalendar: function( isCreate ){
        var date = this.date.clone();
        date.set("date", 1);
        var week = date.getDay();
        if( this.weekBegin == "1" ){
            var decrementDay = ((week-1)<0) ? 6 : week-1;
        }else{
            var decrementDay = week;
        }

        date.decrement("day", decrementDay);
        var tds = this.calendarTable.getElements("td");
        tds.each(function(td){
            this.loadDay(td, date, isCreate);
            date.increment();
        }.bind(this));
    },
    loadData : function( callback ){
        this.app.actions.listEventWithFilter( {
            calendarIds : this.app.getSelectedCalendarId(),
            startTime : this.monthStartStr,
            endTime : this.monthEndStr //,
            //createPerson : this.app.userName
        }, function(json){
            this.wholeDayData = ( json.data  && json.data.wholeDayEvents ) ? json.data.wholeDayEvents : [];
            this.inOneDayEvents = [];
            (( json.data && json.data.inOneDayEvents) ? json.data.inOneDayEvents : []).each( function( d ){
                if(d.inOneDayEvents.length > 0 ){
                    this.inOneDayEvents.push( d );
                }
            }.bind(this));
            if(callback)callback();
        }.bind(this));
    },
    loadOneDay: function( data ){
        this.oneDayDocumentList = [];
        data.each( function( d, i ){
            d.inOneDayEvents.each( function( event, i ){
                this.oneDayDocumentList.push( new MWFCalendarMonthView.Calendar.InOnDayDocument( this, event, d.eventDate ) );
            }.bind(this))
        }.bind(this))
    },
    loadWholeDay: function( data ){
        this.wholeDayRange = [];
        data.each( function( d , i){

            var range = this.getTimeRange( Date.parse(d.startTime), Date.parse(d.endTime ) );
            if( !range )return;
            range.data = d;
            this.wholeDayRange.push( range );
        }.bind(this));

        this.wholeDayRange.sort( function( range1, range2 ){
            if( range1.firstDay > range2.firstDay )return 1;
            if( range1.firstDay < range2.firstDay )return -1;
            return range1.diff - range2.diff;
        }.bind(this));

        this.wholeDayDocumentList = [];
        this.wholeDayRange.each( function( r ){
            this.wholeDayDocumentList.push(  new MWFCalendarMonthView.Calendar.WholeDayDocument( this, r ) );
        }.bind(this))
    },
    loadDay: function(td, date, isCreate){
        var _self = this;
        td.empty();
        var type = "thisMonth";
        var m = date.get("month");
        var y = date.get("year");
        var d = date.get("date");
        var mm = this.date.get("month");
        var yy = this.date.get("year");
        var mmm = this.today.get("month");
        var yyy = this.today.get("year");
        var ddd = this.today.get("date");

        if ((m==mmm) && (y==yyy) && (d==ddd)){
            type = "today";
        }else if ((m==mm) && (y==yy)){
            type = "thisMonth";
        }else{
            type = "otherMonth";
        }

        //var node = new Element("div", {
        //    "styles" : this.css["calendarTableCell_"+type]
        //}).inject( td );
        td.set( "valign","top");
        td.setStyles( this.css["calendarTableCell_"+type] );
        td.store("dateStr",date.format("%Y-%m-%d"));
        td.store("type", type );

        if( isCreate ){
            td.addEvent("click", function(ev){
                this.setCurrentTd( td );
            }.bind(this));

            td.addEvent("dblclick", function(ev){
                _self.cancelCurrentTd();
                var form = new MWF.xApplication.Calendar.EventForm(_self,{}, {
                    startTime : Date.parse( this.retrieve("dateStr") + " 08:00") ,
                    endTime : Date.parse( this.retrieve("dateStr") + " 09:00")
                }, {app:_self.app});
                form.view = _self;
                form.create();
            }.bind(td));

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

        var titleNode = new Element("div", {"styles": this.css["dayTitle_"+ type]}).inject(td);
        var titleDayNode = new Element("div", {"styles": this.css["dayTitleDay_"+ type], "text": d }).inject(titleNode);

        titleDayNode.addEvent("click", function(){
            _self.app.toDay( this.date  );
        }.bind({ date : date.format("%Y-%m-%d") }));

        //var contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(node);

    },
    setCurrentTd : function(td){
        td.setStyle("background-color","#fffdf2");
        if( this.currentSelectedTd ){
            this.currentSelectedTd.setStyle("background-color",this.currentSelectedTd.retrieve("type")=="today"?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = td;
    },
    cancelCurrentTd : function(){
        if( this.currentSelectedTd ){
            this.currentSelectedTd.setStyle("background-color",this.currentSelectedTd.retrieve("type")=="today"?"#F8FBFF":"#fff");
        }
        this.currentSelectedTd = null;
    },
    //loadDay: function(td, date){
    //    td.empty();
    //    var type = "thisMonth";
    //    var m = date.get("month");
    //    var y = date.get("year");
    //    var d = date.get("date");
    //    var mm = this.date.get("month");
    //    var yy = this.date.get("year");
    //    var mmm = this.today.get("month");
    //    var yyy = this.today.get("year");
    //    var ddd = this.today.get("date");
    //
    //    if ((m==mmm) && (y==yyy) && (d==ddd)){
    //        type = "today";
    //    }else if ((m==mm) && (y==yy)){
    //        type = "thisMonth";
    //    }else{
    //        type = "otherMonth";
    //    }
    //
    //    var node = new Element("div", {
    //        "styles" : this.css["calendarTableCell_"+type]
    //    }).inject( td );
    //    node.store("dateStr",date.format("%Y-%m-%d"));
    //
    //    node.addEvent("dblclick", function(ev){
    //        var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
    //            startTime : Date.parse( ev.target.retrieve("dateStr") + " 08:00") ,
    //            endTime : Date.parse( ev.target.retrieve("dateStr") + " 09:00")
    //        }, {app:this.app});
    //        form.view = this;
    //        form.create();
    //    }.bind(this));
    //
    //    new Drag(node, {
    //        "onStart": function(dragged, e){
    //            this.cellDragStart(dragged, e);
    //        }.bind(this),
    //        "onDrag": function(dragged, e){
    //            this.cellDrag(dragged, e);
    //        }.bind(this),
    //        "onComplete": function(dragged, e){
    //            this.completeDrag(dragged, e);
    //        }.bind(this)
    //    });
    //
    //    var titleNode = new Element("div", {"styles": this.css["dayTitle_"+ type]}).inject(node);
    //    var titleDayNode = new Element("div", {"styles": this.css["dayTitleDay_"+ type], "text": d }).inject(titleNode);
    //
    //    var contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(node);
    //
    //},
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        Object.each(this.days, function(day){
            day.destroy();
        }.bind(this));
        this.container.empty();
    },
    getIndexByPage: function( page ){
        var pos = this.calendarTable.getPosition();
        var col = (page.x - pos.x ) / MWFCalendarMonthView.DayWidth;
        if( col < 0 || col > 7 )return null;

        this.pageOffsetHeight = page.y - pos.y;
        var y = page.y - pos.y - MWFCalendarMonthView.THHeight;
        if( y < 0 )return null;
        var row = null;
        for( var i = 0; i< this.calendarTrHeight.length; i++ ){
            if( y < this.calendarTrHeight[i] ){
                row = i;
                break;
            }else{
                y = y - this.calendarTrHeight[i];
            }
        }
        if( row != null ){
            return {
                row : row,
                col : Math.floor(col)
            }
        }else{
            return null;
        }
    },
    getTdsByIndexRange : function( index1, index2 ){
        if( this.calendarTableTds ){
            var tds = this.calendarTableTds;
        }else{
            var tds = this.calendarTableTds = this.calendarTable.getElements("td");
        }
        var minIndex, maxIndex;
        if( index1.row == index2.row ){
            if( index1.col <=  index2.col ){
                minIndex = index1;
                maxIndex = index2;
            }else{
                minIndex = index2;
                maxIndex = index1;
            }
        }else if( index1.row < index2.row ){
            minIndex = index1;
            maxIndex = index2;
        }else{
            minIndex = index2;
            maxIndex = index1;
        }
        var startIndex = minIndex.row * 7 + minIndex.col;
        var endIndex = maxIndex.row * 7 + maxIndex.col;
        var result = [];
        for( var i=startIndex; i<=endIndex; i++ ){
            result.push( tds[i] );
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
        var tds = this.getTdsByIndexRange( orgIndex, curIndex );
        if( this.selectedTds ){
            this.selectedTds.each( function( td ){
                var type = td.retrieve("type");
                if( !tds.contains(td) )td.setStyle("background-color", type == "today" ?  "#F8FBFF" : "#fff");
            }.bind(this));
            tds.each( function( td ){
                if( !this.selectedTds.contains(td) )td.setStyle("background-color", "#fffdf2");
            }.bind(this))
        }else{
            tds.each( function(td){
                td.setStyle("background-color", "#fffdf2");
            }.bind(this))
        }

        var scrollNodeTop = this.scrollNode.getScroll().y;
        if(( this.pageOffsetHeight + MWFCalendarMonthView.WeekHeight * 1.5) > ( this.scrollNodeHeight + scrollNodeTop )){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop + MWFCalendarMonthView.WeekHeight )
            }.bind(this), 200)
        }else if( this.pageOffsetHeight - MWFCalendarMonthView.WeekHeight * 1.5 < scrollNodeTop ){
            window.setTimeout( function(){
                this.scrollNode.scrollTo(0, scrollNodeTop - MWFCalendarMonthView.WeekHeight )
            }.bind(this), 200)
        }

        this.selectedTds = tds
    },
    completeDrag: function(td, e){
        if( this.selectedTds && this.selectedTds.length ){
            this.selectedTds.each( function( td ){
                var type = td.retrieve("type");
                td.setStyle("background-color", type == "today" ?  "#F8FBFF" : "#fff");
            }.bind(this));
            var startTime = this.selectedTds[0].retrieve("dateStr");
            var endTime = this.selectedTds.getLast().retrieve("dateStr");
            var form = new MWF.xApplication.Calendar.EventForm(this,{}, {
                startTime : startTime ,
                endTime : endTime,
                isWholeday : true
            }, {app:this.app});
            form.view = this;
            form.create();

            this.selectedTds = null;
        }
    }

});

MWFCalendarMonthView.THHeight = 50;
MWFCalendarMonthView.WeekHeight = 140;
MWFCalendarMonthView.WeekWidth;
MWFCalendarMonthView.DayWidth;

MWFCalendarMonthView.Calendar.WholeDayDocument = new Class({
    Implements: [Events],
    initialize: function(calendar, range){
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.range = range;
        this.load();
    },
    load: function(){
        this.weekList = [];
        Object.each( this.range.weekInforList, function( weekInfor ){
            this.weekList.push( new MWFCalendarMonthView.Calendar.WholeDayWeek(this, weekInfor, this.range.data ) )
        }.bind(this))
    },
    setMouseOver : function(){
        this.weekList.each( function( week ){
            week.mouseover();
        }.bind(this))
    },
    setMouseOut : function(){
        this.weekList.each( function( week ){
            week.mouseout();
        }.bind(this))
    },
    resize : function(){
        this.weekList.each( function( week ){
            week.resize();
        }.bind(this))
    },
    destroy : function(){
        this.weekList.each( function( week ){
            week.destroy();
        }.bind(this))
    }
});

MWFCalendarMonthView.Calendar.WholeDayWeek = new Class({
    Implements: [Events],
    initialize: function(document, weekInfor, data){
        this.document = document;
        this.calendar = document.calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.weekInfor = weekInfor;
        this.data = data;
        this.load();
    },
    load: function(){
        this.timeStart = Date.parse( this.data.startTime );
        this.timeEnd = Date.parse( this.data.endTime );


        this.yIndex = this.calendar.getUserfulYIndex( this.weekInfor.index, this.weekInfor.daysIndex );
        this.container = this.getContainer();
        this.createNode();
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
                mouseover : function(){
                    this.document.setMouseOver();
                }.bind(this),
                mouseout : function(){
                    this.document.setMouseOut();
                }.bind(this)
            }
        }).inject( this.container );

        var coordinate = this.getCoordinate();
        node.setStyles(coordinate);

        if( this.weekInfor.isEventStart ){
            node.setStyles({
                "border-left" : "1px solid " + lightColor,
                "border-top-left-radius" : "10px",
                "border-bottom-left-radius" : "10px"
            })
        }
        if( this.weekInfor.isEventEnd ){
            node.setStyles({
                "border-right" : "1px solid " + lightColor,
                "border-top-right-radius" : "10px",
                "border-bottom-right-radius" : "10px"
            })
        }

        if( this.weekInfor.isEventStart ){
            this.timeNode = new Element("div",{
                styles : {
                    "font-size" : "10px",
                    "padding-left" : "2px",
                    "float" : "left"
                },
                text : this.timeStart.format("%m-%d %H:%M") + MWF.xApplication.Calendar.LP.to + this.timeEnd.format("%m-%d %H:%M")
            }).inject( node );
        }

        this.titleNode = new Element("div",{
            styles : {
                "padding-left" : "5px",
                "font-size" : "12px",
                "float" : "left",
                "overflow" : "hidden",
                "text-overflow" : "ellipsis",
                "white-space" : "nowrap"
            },
            text : this.data.title
        }).inject( node );

        this.titleNode.setStyle("width", coordinate.width - ( this.timeNode ? this.timeNode.getSize().x : 0 ) -6  );

        //}


        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "y", "delay" : 350
        });
        this.tooltip.view = this.view;
    },
    getContainer : function(){
        return this.calendar.dataTdList[ this.weekInfor.index ]
    },
    getCoordinate : function(){
        var data = this.data;
        var infor = this.weekInfor;

        var top = this.yIndex * 24;

        var width = ( infor.daysIndex.length / 7 ) * MWFCalendarMonthView.WeekWidth - 3;
        var left = ( infor.daysIndex[0] / 7 ) * MWFCalendarMonthView.WeekWidth;

        //var width = ( infor.diff / MWFCalendarMonthView.WeekMsec  ) * MWFCalendarMonthView.WeekWidth - 2;
        //var left = ( infor.left / MWFCalendarMonthView.WeekMsec ) * MWFCalendarMonthView.WeekWidth + 3;
        return {
            top : top + 2,
            left : left,
            width : width
        }
    },
    mouseover : function(){
        this.node.setStyle("border-color", this.data.color );
    },
    mouseout : function(){
        this.node.setStyle("border-color", this.lightColor );
    },
    resize : function(){
        // this.node.setStyles(this.getCoordinate());

        var coordinate = this.getCoordinate();
        this.node.setStyles( coordinate );

        this.titleNode.setStyle("width", coordinate.width - ( this.timeNode ? this.timeNode.getSize().x : 0 ) - 6  );

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

MWFCalendarMonthView.Calendar.InOnDayDocument = new Class({
    Implements: [Events],
    initialize: function(calendar, data, dateStr){
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.data = data;
        this.dateStr = dateStr;
        this.date = Date.parse( dateStr );
        this.load();
    },
    load: function(){
        this.timeStart = Date.parse( this.data.startTime );
        this.timeEnd = Date.parse( this.data.endTime );
        this.index  = this.calendar.getDateIndex( this.date );
        this.yIndex = this.calendar.getUserfulYIndex( this.index.weekIndex, [this.index.dayIndex] );
        this.container = this.getContainer();
        this.createNode();
    },
    createNode : function(){
        var lightColor = this.lightColor = MWFCalendar.ColorOptions.getLightColor( this.data.color );

        var node = this.node = new Element("div",{
            styles : {
                position : "absolute",
                border : "1px solid "+lightColor,
                "background-color" : lightColor,
                "overflow" : "hidden",
                "height" : "20px",
                "line-height" : "20px",
                "border-radius" : "10px"
            },
            events : {
                click : function(){
                    var form = new MWF.xApplication.Calendar.EventForm(this, this.data, {
                        isFull : true
                    }, {app:this.app});
                    form.view = this.view;
                    this.app.isEventEditable(this.data) ? form.edit() : form.open();
                }.bind(this),
                "mouseover" : function () {
                    this.node.setStyle("border-color", this.data.color );
                }.bind(this),
                "mouseout" : function () {
                    this.node.setStyle("border-color", this.lightColor );
                }.bind(this)
            }
        }).inject( this.container );

        var coordinate = this.getCoordinate();
        node.setStyles(coordinate);

        //if( this.isFirst ){
        this.timeNode = new Element("div",{
            styles : {
                "font-size" : "10px",
                "padding-left" : "2px",
                "float" : "left"
            },
            text : this.timeStart.format("%H:%M") + MWF.xApplication.Calendar.LP.to + this.timeEnd.format("%H:%M")
        }).inject( node );

        this.titleNode = new Element("div",{
            styles : {
                "padding-left" : "5px",
                "font-size" : "12px",
                "float" : "left",
                "overflow" : "hidden",
                "text-overflow" : "ellipsis",
                "white-space" : "nowrap"
            },
            text : this.data.title
        }).inject( node );

        this.titleNode.setStyle("width", coordinate.width - this.timeNode.getSize().x - 6 );
        //}


        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "y", "delay" : 350
        });
        this.tooltip.view = this.view;
    },
    getContainer : function(){
        return this.calendar.dataTdList[ this.index.weekIndex ]
    },
    getCoordinate : function(){
        var data = this.data;
        var top = this.yIndex * 24;

        var width = MWFCalendarMonthView.DayWidth - 3;
        var left = this.index.dayIndex * MWFCalendarMonthView.DayWidth;

        //var width = ( infor.diff / MWFCalendarMonthView.WeekMsec  ) * MWFCalendarMonthView.WeekWidth - 2;
        //var left = ( infor.left / MWFCalendarMonthView.WeekMsec ) * MWFCalendarMonthView.WeekWidth + 3;
        return {
            top : top + 2,
            left : left,
            width : width
        }
    },
    resize : function(){
        var coordinate = this.getCoordinate();
        this.node.setStyles( coordinate );
        this.titleNode.setStyle("width", coordinate.width - this.timeNode.getSize().x - 6 );
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

MWFCalendarMonthView.Document = new Class({
    initialize: function(day, data, index){
        this.day = day;
        this.css = this.day.css;
        this.view = this.day.view;
        this.app = this.day.app;
        this.container = this.day.contentNode;
        this.data = data;
        this.index = index;
        this.load();
    },
    load: function(){
        this.nodeStyles = (this.day.type == "today") ? this.css.calendarNode_today : this.css.calendarNode;
        this.node = new Element("div", {
            "styles": this.nodeStyles
        }).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.calendarIconNode}).inject(this.node);
        this.timeNode = new Element("div", {"styles": this.css.calendarTimeNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.calendarTextNode}).inject(this.node);
        var timeStr = Date.parse(this.data.startTime).format("%H:%M");
        this.timeNode.set("text", timeStr);
        this.textNode.set("text", this.data.subject);
        //this.node.set("title", this.data.subject);
        //
        //if (this.data.myWaitAccept){
        //    this.iconNode.setStyle("background", "url(../x_component_Calendar/$MonthView/"+this.app.options.style+"/icon/invite.png) no-repeat center center");
        //}

        this.node.addEvents({
            mouseenter : function(){
                this.day.collapseReady = false;
                this.node.setStyles( this.css.calendarNode_over );
                //this.showTooltip();
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.nodeStyles );
            }.bind(this),
            "click": function(){this.openCalendar();}.bind(this)
        });
        this.loadTooltip();
    },
    loadTooltip : function(){
        this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300,
            onShow : function(){
                this.day.collapseDisable = true;
            }.bind(this),
            onQueryCreate : function(){
                this.day.collapseDisable = true;
            }.bind(this),
            onHide : function(){
                this.day.collapseDisable = false;
                this.day.collapse();
            }.bind(this)
        });
        this.tooltip.view = this.view;
    },
    showTooltip: function(  ){
        //if( this.index > 3 && this.day.isCollapse ){
        //}else{
            if( this.tooltip ){
                this.tooltip.load();
            }else{
                this.tooltip = new MWF.xApplication.Calendar.EventTooltip(this.app.content, this.node, this.app, this.data, {
                    axis : "x", "delay" : 150
                    //onShow : function(){
                    //    this.day.collapseDisable = true;
                    //}.bind(this),
                    //onQueryCreate : function(){
                    //    this.day.collapseDisable = true;
                    //}.bind(this),
                    //onHide : function(){
                    //    this.day.collapseDisable = false;
                    //}.bind(this)
                });
                this.tooltip.view = this.view;
                this.tooltip.load();
            }
        //}
    },
    openCalendar: function(){
        this.form = new MWF.xApplication.Calendar.CalendarForm(this,this.data, {}, {app:this.app});
        this.form.view = this;
        this.form.open();
    },
    destroy: function(){
        if(this.tooltip)this.tooltip.destroy();
       this.node.destroy();
       MWF.release(this);
    },
    reload: function(){
        this.view.reload();
    }
});

MWFCalendarMonthView.MonthSelector = new Class({
    Implements: [Events],
    initialize: function(date, calendar){
        this.calendar = calendar;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date;
        this.year = this.date.get("year");
        this.load();
    },
    load: function(){
        this.monthSelectNode = new Element("div", {"styles": this.css.calendarMonthSelectNode}).inject(this.calendar.container);
        this.monthSelectNode.position({
            relativeTo: this.calendar.titleTextNode,
            position: 'bottomCenter',
            edge: 'upperCenter'
        });
        this.monthSelectNode.addEvent("mousedown", function(e){e.stopPropagation();});

        this.monthSelectTitleNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNode}).inject(this.monthSelectNode);
        this.monthSelectPrevYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitlePrevYearNode}).inject(this.monthSelectTitleNode);
        this.monthSelectNextYearNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleNextYearNode}).inject(this.monthSelectTitleNode);
        this.monthSelectTextNode = new Element("div", {"styles": this.css.calendarMonthSelectTitleTextNode}).inject(this.monthSelectTitleNode);
        this.monthSelectTextNode.set("text", this.year);

        var html = "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        html += "<tr><td></td><td></td><td></td></tr>";
        this.monthSelectTable = new Element("table", {
            "styles": {"margin-top": "10px"},
            "height": "200px",
            "width": "90%",
            "align": "center",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0", //5
            "html": html
        }).inject(this.monthSelectNode);

        //this.loadMonth();

        this.monthSelectBottomNode = new Element("div", {"styles": this.css.calendarMonthSelectBottomNode, "text": this.app.lp.today}).inject(this.monthSelectNode);

        this.setEvent();
    },
    loadMonth: function(){
        this.monthSelectTextNode.set("text", this.year);
        var d = new Date();
        var todayY = d.get("year");
        var todayM = d.get("month");

        var thisY = this.date.get("year");
        var thisM = this.date.get("month");

        var _self = this;
        var tds = this.monthSelectTable.getElements("td");
        tds.each(function(td, idx){
            td.empty();
            td.removeEvents("mouseover");
            td.removeEvents("mouseout");
            td.removeEvents("mousedown");
            td.removeEvents("mouseup");
            td.removeEvents("click");

            var m = idx+1;
            td.store("month", m);
            td.setStyles(this.css.calendarMonthSelectTdNode);

            td.setStyle("background-color", "#FFF");
            if ((this.year == todayY) && (idx == todayM)){
                new Element("div", {
                    styles : _self.css.calendarMonthSelectTodayNode,
                    text : ""+m+ (MWF.language.substr(0,2) === "zh" ? this.app.lp.month : "")
                }).inject( td );
            }else if ((this.year == thisY) && (idx == thisM)){
                //td.setStyle("background-color", "#EEE");
                new Element("div", {
                    styles : _self.css.calendarMonthSelectCurrentNode,
                    text : ""+m+ (MWF.language.substr(0,2) === "zh" ? this.app.lp.month : "")
                }).inject( td );
            }else{
                td.set("text", ""+m+ (MWF.language.substr(0,2) === "zh" ? this.app.lp.month : ""));
            }

            td.addEvents({
                "mouseover": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
                "mouseout": function(){this.setStyles(_self.css.calendarMonthSelectTdNode);},
                "mousedown": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_down);},
                "mouseup": function(){this.setStyles(_self.css.calendarMonthSelectTdNode_over);},
                "click": function(){
                    _self.selectedMonth(this);
                }
            });
        }.bind(this));
    },
    setEvent: function(){
        this.monthSelectPrevYearNode.addEvent("click", function(){
            this.prevYear();
        }.bind(this));

        this.monthSelectNextYearNode.addEvent("click", function(){
            this.nextYear();
        }.bind(this));

        this.monthSelectBottomNode.addEvents({
            "mouuseover" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode_over ); }.bind(this),
            "mouuseout" : function(){ this.monthSelectBottomNode.setStyles( this.css.calendarMonthSelectBottomNode ); }.bind(this),
            "click" : function(){ this.todayMonth(); }.bind(this)
        });
    },
    prevYear: function(){
        this.year--;
        if (this.year<1900) this.year=1900;
        this.monthSelectTextNode.set("text", this.year);
        this.loadMonth();
    },
    nextYear: function(){
        this.year++;
        //if (this.year<1900) this.year=1900;
        this.monthSelectTextNode.set("text", this.year);
        this.loadMonth();
    },
    todayMonth: function(){
        var d = new Date();
        this.calendar.changeMonthTo(d);
        this.hide();
    },
    selectedMonth: function(td){
        var m = td.retrieve("month");
        var d = Date.parse(this.year+"/"+m+"/1");
        this.calendar.changeMonthTo(d);
        this.hide();
    },

    show: function(){
        this.date = this.calendar.date;
        this.year = this.date.get("year");
        this.loadMonth();
        this.monthSelectNode.setStyle("display", "block");
        this.hideFun = this.hide.bind(this);
        document.body.addEvent("mousedown", this.hideFun);
    },
    hide: function(){
        this.monthSelectNode.setStyle("display", "none");
        document.body.removeEvent("mousedown", this.hideFun);
    },

    destroy: function(){
        //this.titleNode.destroy();
        //this.titleNode = null;
        //this.titleDayNode = null;
        //this.titleInforNode = null;
        //
        //delete this.calendar.days[this.key];
        //
        //this.node.empty();
        //MWF.release(this);
    }

});