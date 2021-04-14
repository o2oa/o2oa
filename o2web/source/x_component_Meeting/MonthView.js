MWF.xApplication.Meeting.MonthView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Meeting/$MonthView/";
        this.cssPath = "../x_component_Meeting/$MonthView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //this.loadSideBar();
        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));
        this.loadCalendar();
    },

    resetNodeSize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();
        var y = size.y-60;
        this.node.setStyle("height", ""+y+"px");
        if( !this.app.inContainer  ) {
            this.node.setStyle("margin-top", "60px");
        }


        var sideBarSize = this.app.sideBar ?  this.app.sideBar.getSize() : { x : 0, y:0 };
        this.node.setStyle("width", ""+(size.x - sideBarSize.x)+"px");
        this.node.setStyle("margin-right", ""+sideBarSize.x+"px");

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
        this.calendar = new MWF.xApplication.Meeting.MonthView.Calendar(this, date  );

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
        if( this.app.inContainer ){
            this.node.setStyles({
                "opacity": 1,
                "position": "static",
                "width": "auto"
            });
        }else{
            var fx = new Fx.Morph(this.node, {
                "duration": "800",
                "transition": Fx.Transitions.Expo.easeOut
            });
            this.app.fireAppEvent("resize");
            fx.start({
                "opacity": 1,
                "left": "0px"
            }).chain(function(){
                this.node.setStyles({
                    "position": "static",
                    "width": "auto"
                });
            }.bind(this));
        }

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

MWF.xApplication.Meeting.MonthView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.date = date || new Date();
        this.today = new Date();
        this.days = {};
        this.weekBegin = this.app.meetingConfig.weekBegin || 0;
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);


        this.scrollNode = new Element("div", {
            "styles":  this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
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

        //this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.setBodyNode();

        this.resetBodySize();
        this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    resetBodySize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();
        var titleSize = this.titleNode.getSize();
        var y = size.y-titleSize.y;
        //this.bodyNode.setStyle("height", ""+y+"px");

        //var size = this.container.getSize();

        this.scrollNode.setStyle("height", ""+y+"px");
        //this.scrollNode.setStyle("margin-top", "60px");

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": (size.x - 40) +"px"
            });
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
        this.monthSelector = new MWF.xApplication.Meeting.MonthView.Calendar.MonthSelector(this.date, this);
    },
    changeMonthTo: function(d){
        this.date = d;
        var text = this.date.format(this.app.lp.dateFormatMonth);
        this.titleTextNode.set("text", text);
        this.reLoadCalendar();
    },

    setBodyNode: function(){
        if( this.weekBegin == "1" ){
            var html = "<tr><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
                "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th><th>"+this.app.lp.weeks.Sun+"</th></tr>";
        }else{
            var html = "<tr><th>"+this.app.lp.weeks.Sun+"</th><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
                "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th></tr>";
        }
        html += "<tr><td valign='top'></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
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

        this.calendarTableTitleTr = this.calendarTable.getElement("tr");
        this.calendarTableTitleTr.setStyles(this.css.calendarTableTitleTr);
        var ths = this.calendarTableTitleTr.getElements("th");
        ths.setStyles(this.css.calendarTableTh);

        //var tds = this.calendarTable.getElements("td");
        //tds.setStyles(this.css.calendarTableCell);

        this.loadCalendar();
    },
    reLoadCalendar: function(){
        Object.each(this.days, function(day){
            day.destroy();
        }.bind(this));

        this.loadCalendar();
    },

    loadCalendar: function(){
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
            this.loadDay(td, date);
            date.increment();
        }.bind(this));
    },
    loadDay: function(td, date){
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

        var key = date.format(this.app.lp.dateFormat);
        this.days[key] = new MWF.xApplication.Meeting.MonthView.Calendar.Day(td, date, this, type);
    },
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        Object.each(this.days, function(day){
            day.destroy();
        }.bind(this));
        this.container.empty();
    }

});

MWF.xApplication.Meeting.MonthView.Calendar.Day = new Class({
    Implements: [Events],
    initialize: function(td, date, calendar, type){
        this.container = td;
        this.calendar = calendar;
        this.view = this.calendar.view;
        this.css = this.calendar.css;
        this.app = this.calendar.app;
        this.date = date.clone();
        this.key = this.date.format(this.app.lp.dateFormat);
        this.type = type; //today, otherMonth, thisMonth
        this.meetings = [];
        this.load();
    },
    load: function(){
        this.color = "#666";
        if(  this.type == "thisMonth" ){
        }else if( this.type == "otherMonth" ){
            this.color = "#ccc";
        }
        this.day = this.date.getDate();
        this.month = this.date.getMonth();
        this.year = this.date.getYear();

        this.node = new Element("div", {
            "styles" : this.css["calendarTableCell_"+this.type]
        }).inject( this.container );

        this.titleNode = new Element("div", {"styles": this.css["dayTitle_"+this.type]}).inject(this.node);
        this.titleDayNode = new Element("div", {"styles": this.css["dayTitleDay_"+this.type], "text": this.day}).inject(this.titleNode);

        if ((new Date()).diff(this.date)>=0){
            this.titleNode.set("title", this.app.lp.titleNode);
            this.titleNode.addEvent("click", function(){
                this.app.addMeeting(this.date);
            }.bind(this));
        }

        this.contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(this.node);

        this.loadMeetings();

    },
    loadMeetings: function(){
        this.app.isMeetingViewer( function( isAll ){
            this._loadMeetings( isAll );
        }.bind(this))
    },
    _loadMeetings: function( isAll ){
        var y = this.date.getFullYear();
        var m = this.date.getMonth()+1;
        var d = this.date.getDate();
        var meetingCount = 0;
        var myRejectCount = 0;
        this.firstStatus = "";
        this.lastStatus = "";
        this.app.actions[ isAll ? "listMeetingDayAll" : "listMeetingDay" ](y, m, d, function(json){
            var length = json.data.length;
            json.data.each(function(meeting, i){
                if (!meeting.myReject){
                    meetingCount++;
                    if (meetingCount==3){
                        //this.contentNode.setStyle("height", "100px");
                    }
                    if( meetingCount == 1 ){
                        this.firstStatus = meeting.status;
                        if( meeting.myWaitAccept )this.firstStatus = "myWaitAccept"
                    }
                    if( meetingCount + myRejectCount == length ){
                        this.lastStatus = meeting.status;
                        if( meeting.myWaitAccept )this.lastStatus = "myWaitAccept"
                    }
                    //if (meetingCount<4)
                        this.meetings.push(new MWF.xApplication.Meeting.MonthView.Calendar.Day.Meeting(this, meeting, meetingCount));
                }else{
                    myRejectCount++;
                }
            }.bind(this));

            if (meetingCount==0){
                var node = new Element("div", {
                    "styles": {
                        "line-height": "40px",
                        "font-size": "14px",
                        "text-align" : "center",
                        "color" : this.color,
                        "padding": "0px 10px"
                    }
                }).inject(this.contentNode);
                node.set("text", this.app.lp.noMeeting);
            }else{
                this.titleInforNode = new Element("div", {"styles": this.css["dayTitleInfor_"+this.type]}).inject(this.titleNode);
                if( this.app.isViewAvailable( "toDay" ) ) {
                    this.titleInforNode.addEvent("click", function (e) {
                        this.app.toDay(this.date);
                        e.stopPropagation();
                    }.bind(this));
                }else{
                    this.titleInforNode.setStyle("cursor","default");
                }
                this.titleInforNode.set("text", ""+meetingCount+this.app.lp.countMeetings+"");
                if (meetingCount>3){
                    this.node.addEvents( {
                        "mouseenter" : function(){
                            this.expend();
                        }.bind(this),
                        "mouseleave" : function(){
                            this.collapseReady = true;
                            this.collapse();
                        }.bind(this)
                    } )
                }else{
                    this.titleInforNode.setStyle("color", this.type == "otherMonth" ? "#ccc" : "#999");
                }
            }

            if(this.firstStatus){
                switch (this.firstStatus){
                    case "wait":
                        this.titleNode.setStyles({ "border-left": "6px solid #4990E2" });
                        break;
                    case "processing":
                        this.titleNode.setStyles({ "border-left": "6px solid #66CC7F" });
                        break;
                    case "completed":
                        this.titleNode.setStyles({ "border-left": "6px solid #ccc" });
                        break;
                    case "myWaitAccept":
                        this.titleNode.setStyles({ "border-left": "6px solid #F6A623" });
                        break
                }
            }

            if( this.lastStatus ){
                var heigth=0;
                if( meetingCount >= 3 ){
                    heigth = 10;
                }else{
                    heigth = 100 - meetingCount*30;
                }
                var bottomEmptyNode = new Element("div", {
                    styles : {
                        "height" : ""+heigth+"px"
                    }
                }).inject( this.node );
                switch (this.lastStatus){
                    case "wait":
                        bottomEmptyNode.setStyles({ "border-left": "6px solid #4990E2" });
                        break;
                    case "processing":
                        bottomEmptyNode.setStyles({ "border-left": "6px solid #66CC7F" });
                        break;
                    case "completed":
                        bottomEmptyNode.setStyles({ "border-left": "6px solid #ccc" });
                        break;
                    case "myWaitAccept":
                        bottomEmptyNode.setStyles({ "border-left": "6px solid #F6A623" });
                        break
                }
            }
        }.bind(this));

    },
    expend : function(){
        this.oSize = this.node.getSize();
        this.container.setStyles({
            "position" : "relative"
        });
        this.tempNode = new Element("div",{
            styles : {
                width : (this.node.getSize().x ) + "px",
                height : "1px",
                margin : "7px"
            }
        }).inject(this.container);
        this.node.setStyles({
            "height" : this.node.getScrollSize().y + "px",
            "width" : (this.node.getSize().x ) + "px",
            "position" : "absolute",
            "top" : "0px",
            "left" : "0px",
            "box-shadow": "0 0 8px 0 rgba(0,0,0,0.25)"
        });
        var nodeCoordinate = this.node.getCoordinates();
        var contentNode = this.calendar.contentWarpNode;
        var contentCoordinate = contentNode.getCoordinates();
        if( nodeCoordinate.bottom > contentCoordinate.bottom ){
            this.contentHeight = contentCoordinate.height;
            contentNode.setStyle("height", (  nodeCoordinate.bottom - contentCoordinate.top  )+"px"  );
        }
        this.isCollapse = false;
    },
    collapse : function(){
        if( !this.collapseDisable && this.collapseReady){
            this.container.setStyles({
                "position" : "static"
            });
            if( this.tempNode )this.tempNode.destroy();
            this.node.setStyles({
                "height" : "140px",
                "width" : "auto",
                "position" : "static",
                "box-shadow": "none"
            });
            if( this.contentHeight ){
                var contentNode = this.calendar.contentWarpNode;
                contentNode .setStyle("height", ( this.contentHeight )+"px"  );
                this.contentHeight = null;
            }
            this.isCollapse = true;
        }
    },
    destroy: function(){
        this.meetings.each(function(meeting){
            meeting.destroy();
        }.bind(this));
        this.meetings = [];
        this.titleNode.destroy();
        this.titleNode = null;
        this.titleDayNode = null;
        this.titleInforNode = null;

        delete this.calendar.days[this.key];

        this.container.empty();
        MWF.release(this);
    },
    reload: function(){
        this.view.reload();
    }
});
MWF.xApplication.Meeting.MonthView.Calendar.Day.Meeting = new Class({
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
        this.nodeStyles = (this.day.type == "today") ? this.css.meetingNode_today : this.css.meetingNode;
        this.node = new Element("div", {
            "styles": this.nodeStyles
        }).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.meetingIconNode}).inject(this.node);
        this.timeNode = new Element("div", {"styles": this.css.meetingTimeNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.meetingTextNode}).inject(this.node);
        var timeStr = Date.parse(this.data.startTime).format("%H:%M");
        this.timeNode.set("text", timeStr);
        this.textNode.set("text", this.data.subject);
        //this.node.set("title", this.data.subject);
        //
        //if (this.data.myWaitAccept){
        //    this.iconNode.setStyle("background", "url(../x_component_Meeting/$MonthView/"+this.app.options.style+"/icon/invite.png) no-repeat center center");
        //}

        switch (this.data.status){
            case "wait":
                this.node.setStyles({
                    "border-left": "6px solid #4990E2"
                });
                break;
            case "processing":
                this.node.setStyles({
                    "border-left": "6px solid #66CC7F"
                });
                break;
            case "completed":
                //add attachment
                this.node.setStyles({
                    "border-left": "6px solid #ccc"
                });
                //this.textNode.setStyle("color", "#666");

                break;
        }
        if (this.data.myWaitAccept){
            this.node.setStyles({
                "border-left": "6px solid #F6A623"
            });

        }
        this.node.addEvents({
            mouseenter : function(){
                this.day.collapseReady = false;
                this.node.setStyles( this.css.meetingNode_over );
                //this.showTooltip();
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.nodeStyles );
            }.bind(this),
            "click": function(){this.openMeeting();}.bind(this)
        });
        this.loadTooltip();
    },
    loadTooltip : function(){
        this.tooltip = new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, this.node, this.app, this.data, {
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
    },
    showTooltip: function(  ){
        //if( this.index > 3 && this.day.isCollapse ){
        //}else{
            if( this.tooltip ){
                this.tooltip.load();
            }else{
                this.tooltip = new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, this.node, this.app, this.data, {
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
                this.tooltip.load();
            }
        //}
    },
    openMeeting: function(){
        this.form = new MWF.xApplication.Meeting.MeetingForm(this,this.data, {}, {app:this.app});
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


MWF.xApplication.Meeting.MonthView.Calendar.MonthSelector = new Class({
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