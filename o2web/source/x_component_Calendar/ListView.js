MWF.xApplication.Calendar.ListView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null,
        "action" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Calendar/$ListView/";
        this.cssPath = "../x_component_Calendar/$ListView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.date = this.options.date || new Date();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.node);
        this.contentAreaNode  = new Element("div.contentAreaNode", {"styles": this.css.contentAreaNode}).inject(this.node);
        this.contentNode  = new Element("div.contentNode", {"styles": this.css.contentNode}).inject(this.contentAreaNode);

        //this.loadSideBar();

        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        //this.app.addEvent("resize", this.resetNodeSizeFun );

        this.setTitleNode();
        this.loadList();

        this.resetNodeSize();

    },
    resetNodeSize: function(){
        //var size = this.container.getSize();
        //if (this.app.calendarConfig.hideMenu=="static"){
        //    var y = size.y-120;
        //    this.node.setStyle("height", ""+y+"px");
        //    this.node.setStyle("margin-top", "60px");
        //}else{
        //    var y = size.y-20;
        //    this.node.setStyle("height", ""+y+"px");
        //}

        var size = this.container.getSize();

        var titleSize = this.titleNode ? this.titleNode.getSize() : {x:0, y:0};

        var y = size.y-50;
        //this.node.setStyle("margin-top", "60px");
        this.node.setStyle("height", ""+y+"px");

        var sideBar = this.app.sideBar ? this.app.sideBar.getSize() : { x : 0, y : 0 };
        //var x = size.x - sideBar.x;
        //this.node.setStyle("width", ""+x+"px");
        this.contentAreaNode.setStyle("height",(y-titleSize.y)+"px");
        this.contentAreaNode.setStyle("margin-right",sideBar.x+"px");
    },
    loadList : function(){
        this.app.currentDate = this.date.clone();

        this.monthStart = new Date( this.date.get("year"), this.date.get("month"), 1, 0, 0, 0 );
        this.monthStartStr = this.monthStart.format("db");

        var end = this.monthStart.clone().increment("month",1).decrement("day",1);
        this.monthEnd = new Date( end.get("year"), end.get("month"), end.get("date"), 23, 59, 59 );
        this.monthEndStr = this.monthEnd.format("db");

        this.view = new MWF.xApplication.Calendar.ListView.View(this);

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
        this.app.fireAppEvent("resize");
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
        //this.app.reload();
        this.reLoadView();
    },
    recordStatus : function(){
        //var action = "";
        //if( this.currentNavi )action = this.currentNavi.retrieve("action");
        //return {
        //    action : action
        //};
    },
    destroy : function(){
        if( this.currentView ){
            this.currentView.destroy()
        }
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.node.destroy();
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
        this.reLoadView();
    },
    changeMonthNext: function(){
        this.date.increment("month", 1);
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadView();
    },
    changeMonthSelect: function(){
        if (!this.monthSelector) this.createMonthSelector();
        this.monthSelector.show();
    },
    createMonthSelector: function(){
        MWF.xDesktop.requireApp("Calendar","MonthView",null, false);
        this.monthSelector = new MWFCalendarMonthView.MonthSelector(this.date, this);
    },
    changeMonthTo: function(d){
        this.date = d;
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadView();
    },
    reLoadView : function(){
        if(this.view)this.view.destroy();
        this.loadList();
    },

    inCurrentMonth : function( d ){
        if( d< this.monthStart ) return false;
        if( d > this.monthEnd )return false;
        return true;
    }

});

MWF.xApplication.Calendar.ListView.View = new Class({
    initialize: function(view){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.contentNode;
        this.app = this.view.app;
        this.lp = this.app.lp;
        this.items = [];
        this.load();
    },
    load: function(){
        //this.loadHead();
        this.loadList();
    },
    loadHead: function(){
        this.table = new Element("table", {
            "styles": this.css.listViewTable,
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.container);
    },
    loadEmptyNode : function(){
        this.noEventNode = new Element("div",{
            "styles" : this.css.noEventNode,
            "text" : this.lp.noEventCurMonth
        }).inject( this.container );
    },
    loadList: function() {
        this.app.actions.listEventWithFilter( {
            calendarIds : this.app.getSelectedCalendarId(),
            startTime : this.view.monthStartStr,
            endTime : this.view.monthEndStr //,
            //createPerson : this.app.userName
        }, function(json){
            this.parseDate( json );
            if( this.wholeDayData.length == 0 && this.inOneDayEvents.length == 0){
                this.loadEmptyNode()
            }else{
                this.loadHead();
                this.loadDays();
            }
        }.bind(this))
    },
    parseDate: function( json ){
        this.dataMap = {};
        this.wholeDayData = ( json.data  && json.data.wholeDayEvents ) ? json.data.wholeDayEvents : [];
        this.inOneDayEvents = [];
        (( json.data && json.data.inOneDayEvents) ? json.data.inOneDayEvents : []).each( function( d ){
            if(d.inOneDayEvents.length > 0 ){
                this.inOneDayEvents.push( d );
            }
        }.bind(this));
        this.getEveryDayByWholeDayData();
        this.inOneDayEvents.each( function( d ){
            var map = this.dataMap[d.eventDate];
            if( !map )map = this.dataMap[d.eventDate] = {};
            if( !map.inoneday )map.inoneday = [];
            d.inOneDayEvents.each( function( event ){
                map.inoneday.push({
                    start : Date.parse( event.startTime).format("%H:%M"),
                    end : Date.parse( event.endTime).format("%H:%M"),
                    data : event
                })
            });
        }.bind(this));
        //this.loadLines(json.data);
    },
    getEveryDayByWholeDayData : function(){
        this.wholeDayData.each( function( event ){
            var startTime = Date.parse(event.startTime);
            var endTime = Date.parse(event.endTime);
            var start, end;
            start = startTime < this.view.monthStart ? this.view.monthStart.clone() : startTime.clone();
            end = endTime > this.view.monthEnd ?  this.view.monthEnd.clone() : endTime.clone();
            var startDayTime = start.format("%H:%M");
            var endDayTime = end.format("%H:%M");

            start.clearTime();
            end.clearTime();

            var i = 0;
            while( start <= end ){
                var startStr = start.format("%Y-%m-%d");
                var alldayFlag = false;
                var map = this.dataMap[startStr];
                if( !map )map = this.dataMap[startStr] = {};
                if( event.isAllDayEvent ){
                    alldayFlag = true;
                }else{
                    var startT = i==0 ? startDayTime : "00:00";
                    var endT = start == end ? endDayTime : "23:59";
                    if( startT == "00:00" &&  endT == "23:59")alldayFlag = true;
                }
                if( alldayFlag ){
                    if( !map.wholeday )map.wholeday = [];
                    map.wholeday.push({data : event});
                }else{
                    if( !map.inoneday )map.inoneday = [];
                    map.inoneday.push({
                        start :  startT,
                        end : endT,
                        data : event
                    });
                }
                start.increment( "day", 1 );
                i++;
            }
        }.bind(this))
    },
    loadDays: function(items){
        for( var dateStr in this.dataMap ){
            this.loadLine( dateStr, this.dataMap[dateStr] );
        }
        if (this.mask){
            this.mask.hide(function(){
                //MWF.release(this.mask);
                this.mask = null;
            }.bind(this));
        }
    },
    loadLine: function(dateStr, item){
        this.items.push(new MWF.xApplication.Calendar.ListView.View.DayLine(this, dateStr, item));
    },
    destroy: function(){
        this.items.each(function(item){
            item.destroy();
        });
        this.items = [];
        this.view.currentView = null;
        if(this.table)this.table.destroy();
        if( this.noEventNode )this.noEventNode.destroy();
    }

});

MWF.xApplication.Calendar.ListView.View.DayLine = new Class({
    initialize: function(table, dateStr, item){
        this.table = table;
        this.view = this.table.view;
        this.css = this.view.css;
        this.container = this.table.table;
        this.dateStr = dateStr;
        this.date = Date.parse(dateStr);
        this.app = this.view.app;
        this.data = item;
        this.load();
    },
    load: function(){
        this.isToday =  this.date.clone().clearTime().diff( new Date().clearTime(), "day" ) == 0;
        var showDate = this.date.format(this.app.lp.dateFormatMonthDay2) + "  " + this.app.lp.weeks.arr[ this.date.getDay() ];
        this.node = new Element("tr",{
            "html": "<td colspan='4'>"+showDate+"</td>"
        }).inject(this.container);

        if( this.isToday ){
            this.node.getElements("td").setStyles(this.css.listViewTableTd_ToDay);
        }else{
            this.node.getElements("td").setStyles(this.css.listViewTableTd_Day);
        }

        var wholeday =this.data.wholeday || [];
        wholeday.each( function( d ){
            new MWF.xApplication.Calendar.ListView.View.Line( this, d, true );
        }.bind(this));

        var inoneday = this.data.inoneday || [];
        inoneday.each( function( d ){
            new MWF.xApplication.Calendar.ListView.View.Line( this, d, false );
        }.bind(this))
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        //MWF.release(this);
    }
});

MWF.xApplication.Calendar.ListView.View.Line = new Class({
    initialize: function(day, item, isWholeday ){
        this.day = day;
        this.table = day.table;
        this.view = this.table.view;
        this.css = this.view.css;
        this.container = this.table.table;
        this.app = this.view.app;
        this.data = item;
        this.isWholeday = isWholeday;
        this.load();
    },
    load: function(){
        //var sTime = Date.parse(this.data.startTime);
        //
        //var bdate = sTime.format(this.app.lp.dateFormatDay);
        //
        //var btime = sTime.format("%H:%M");
        //var etime = Date.parse(this.data.completedTime).format("%H:%M");

        if( this.isWholeday ){
            this.node = new Element("tr",{
                "html": "<td width='30'><div></div></td><td width='100'>"+this.app.lp.allDay+"</td><td>"+this.data.data.title +"</td><td>"+ (this.data.data.locationName || "") +"</td>"
            }).inject(this.container);
        }else{
            var bdate = this.data.start;
            var edate = this.data.end;

            this.node = new Element("tr",{
                "html": "<td width='30'><div></div></td><td>"+bdate+"  -  "+edate+"</td><td>"+this.data.data.title +"</td><td>"+(this.data.data.locationName || "")  +"</td>"
            }).inject(this.container);
        }
        if( this.day.isToday ){
            this.node.getElements("td").setStyles(this.css.listViewTableTd_today2);
        }else{
            this.node.getElements("td").setStyles(this.css.listViewTableTd);
        }
        var colorNode = this.node.getElement("div");
        colorNode.setStyles(this.css.colorTdNode);
        colorNode.setStyle("background-color", this.data.data.color );


        this.node.addEvents({
            "mouseover" : function(){
                this.node.getElements("td").setStyles( this.css.listViewTableTd_over )
            }.bind(this),
            "mouseout" : function(){
                this.node.getElements("td").setStyles(  this.day.isToday ? this.css.listViewTableTd_today2 : this.css.listViewTableTd );
            }.bind(this)
        });

        this.node.addEvent("click", function(e){
            this.openCalendar(e);
        }.bind(this));
    },
    openCalendar: function(e){ 
        this.form = new MWF.xApplication.Calendar.EventForm(this,this.data.data, {
            isFull : true
        }, {app:this.app});
        this.form.view = this.view;
        this.app.isEventEditable(this.data) ? this.form.edit() : this.form.open();
    },
    destroy: function(){
        if (this.node) this.node.destroy();
        //MWF.release(this);
    }
});
