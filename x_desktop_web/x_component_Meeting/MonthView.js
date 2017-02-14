MWF.xApplication.Meeting.MonthView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$MonthView/";
        this.cssPath = "/x_component_Meeting/$MonthView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));
        this.loadCalendar();
    },

    resetNodeSize: function(){
        var size = this.container.getSize();
        if (this.app.meetingConfig.hideMenu=="static"){
            var y = size.y-120;
            this.node.setStyle("height", ""+y+"px");
            this.node.setStyle("margin-top", "60px");
        }else{
            var y = size.y-20;
            this.node.setStyle("height", ""+y+"px");
        }
    },

    loadCalendar: function(){
        this.calendar = new MWF.xApplication.Meeting.MonthView.Calendar(this);

    },
    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        if (this.currentMeetingDocument) this.currentMeetingDocument.closeDocument();
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
            "opacity": 1,
            "left": "0px"
        }).chain(function(){
            this.node.setStyles({
                "position": "static",
                "width": "auto"
            });
        }.bind(this));
    },
    reload: function(){
        if (this.calendar) this.calendar.reLoadCalendar();
    }

});

MWF.xApplication.Meeting.MonthView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view){
        this.view = view
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.date = new Date();
        this.today = new Date();
        this.days = {};
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.calendarTitleNode}).inject(this.container);
        this.bodyNode = new Element("div", {"styles": this.css.calendarBodyNode}).inject(this.container);

        this.setTitleNode();
        this.setBodyNode();

        this.resetBodySize();
        this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    resetBodySize: function(){
        var size = this.container.getSize();
        var titleSize = this.titleNode.getSize();
        var y = size.y-titleSize.y;
        this.bodyNode.setStyle("height", ""+y+"px");

        var tdy = (y-30)/6;
        tdy = tdy-34;
        var tds = this.calendarTable.getElements("td");
        tds.each(function(td){
            var yy = tdy;
            var node = td.getLast("div");
            if (node.childNodes.length>=4){
                if (yy<92) yy = 69;
            }
            node.setStyle("height", ""+yy+"px");
        }.bind(this));


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
        var html = "<tr><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
            "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th><th>"+this.app.lp.weeks.Sun+"</th></tr>";
        html += "<tr><td vAlign=\"top\"></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
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
        var decrementDay = ((week-1)<0) ? 6 : week-1;

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
    }

});

MWF.xApplication.Meeting.MonthView.Calendar.Day = new Class({
    Implements: [Events],
    initialize: function(td, date, calendar, type){
        this.node = td;
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
        this.day = this.date.getDate();
        this.month = this.date.getMonth();
        this.year = this.date.getYear();

        this.node.setStyles(this.css["calendarTableCell_"+this.type]);

        this.titleNode = new Element("div", {"styles": this.css["calendarDayTitle_"+this.type]}).inject(this.node);
        this.titleDayNode = new Element("div", {"styles": this.css["calendarDayTitleDay_"+this.type], "text": this.day}).inject(this.titleNode);

        if ((new Date()).diff(this.date)>=0){
            this.titleNode.set("title", this.app.lp.titleNode);
            this.titleNode.addEvent("click", function(){
                this.app.addMeeting(this.date);
            }.bind(this));
        }

        this.contentNode = new Element("div", {"styles": this.css.calendarDayContentNode}).inject(this.node);

        this.loadMeetings();

    },
    loadMeetings: function(){
        var y = this.date.getFullYear();
        var m = this.date.getMonth()+1;
        var d = this.date.getDate();
        var meetingCount = 0;
        this.app.actions.listMeetingDay(y, m, d, function(json){
            json.data.each(function(meeting, i){
                if (!meeting.myReject){
                    meetingCount++;
                    if (meetingCount==4){
                        this.contentNode.setStyle("height", "100px");
                    }
                    if (meetingCount<5) this.meetings.push(new MWF.xApplication.Meeting.MonthView.Calendar.Day.Meeting(this, meeting));
                }
            }.bind(this));

            if (meetingCount==0){
                var node = new Element("div", {
                    "styles": {
                        "line-height": "40px",
                        "font-size": "14px",
                        "color": "#888",
                        "padding": "0px 10px"
                    }
                }).inject(this.contentNode);
                node.set("text", this.app.lp.noMeeting);
            }else{
                if (meetingCount>3){
                    this.titleInforNode = new Element("div", {"styles": this.css["calendarDayTitleInfor_"+this.type]}).inject(this.titleNode);
                    this.titleInforNode.addEvent("click", function(e){
                        this.app.toDay(this.date);
                        e.stopPropagation();
                    }.bind(this));

                    this.titleInforNode.set("text", meetingCount+this.app.lp.countMeetings);
                }
            }
        }.bind(this));
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

        this.node.empty();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.MonthView.Calendar.Day.Meeting = new Class({
    initialize: function(day, data){
        this.day = day;
        this.css = this.day.css;
        this.view = this.day.view;
        this.app = this.day.app;
        this.container = this.day.contentNode;
        this.data = data;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.calendarDayContentMeetingNode}).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.calendarDayContentMeetingIconNode}).inject(this.node);
        this.timeNode = new Element("div", {"styles": this.css.calendarDayContentMeetingTimeNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.calendarDayContentMeetingTextNode}).inject(this.node);
        var timeStr = Date.parse(this.data.startTime).format("%H:%M")
        this.timeNode.set("text", timeStr);
        this.textNode.set("text", this.data.subject);
        this.node.set("title", this.data.subject);
        //
        //if (this.data.myWaitAccept){
        //    this.iconNode.setStyle("background", "url(/x_component_Meeting/$MonthView/"+this.app.options.style+"/icon/invite.png) no-repeat center center");
        //}

        switch (this.data.status){
            case "wait":
                //nothing
                break;
            case "processing":
                this.node.setStyles({
                    "border-left": "5px solid #18da14",
                    "background-color": "#deffdd"
                });
                break
            case "completed":
                //add attachment
                this.node.setStyles({
                    "border-left": "5px solid #555",
                    "background-color": "#F3F3F3"
                });
                this.textNode.setStyle("color", "#666");

                break;
        }
        if (this.data.myWaitAccept){
            this.node.setStyles({
                "border-left": "5px solid #ecd034",
                "background-color": "#fffade"
            });

        }
        this.node.addEvent("click", function(){this.openMeeting();}.bind(this));
    },
    openMeeting: function(){
        if (!this.document){
            if (this.view.currentMeetingDocument) this.view.currentMeetingDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.MonthView.Calendar.Day.Document(this);
            this.view.currentMeetingDocument = this.document;
        }
    },
    destroy: function(){
        if (this.document){
            this.document.closeDocument(function(){
                this.node.destroy();
                MWF.release(this);
            }.bind(this));
        }else{
            this.node.destroy();
            MWF.release(this);
        }
    }
});
MWF.xApplication.Meeting.MonthView.Calendar.Day.Document = new Class({
    Extends: MWF.xApplication.Meeting.MeetingView.Document,
    initialize: function(item){
        this.item = item;
        this.view = this.item.view
        this.container = this.view.node;
        this.app = this.view.app;
        this.path = "/x_component_Meeting/$MeetingView/";
        this.cssPath = "/x_component_Meeting/$MeetingView/default/css.wcss";
        this._loadCss();

        this.app.actions.getMeeting(this.item.data.id, function(json){
            this.data = json.data;
            this.isEdit = (this.data.applicant == this.app.desktop.session.user.name);
            this.load();
        }.bind(this));
    },
    closeDocument: function(callback){
        //this.saveDocument(true, true);

        if (this.setDescriptionNodeSizeFun) this.app.removeEvent("resize", this.setDescriptionNodeSizeFun);
        if (this.setNodeSizeFun) this.app.removeEvent("resize", this.setNodeSizeFun);

        var size = this.item.node.getSize();
        var position = this.item.node.getPosition(this.app.content);

        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.node.empty();
        this.view.currentMeetingDocument = null;
        fx.start({
            "opacity": 0,
            "width": ""+ size.x+"px",
            "height": ""+ size.y+"px",
            "left": ""+ position.x+"px",
            "top": ""+ position.y+"px",
        }).chain(function(){
            this.destroy();
            if (callback) callback();
        }.bind(this));
    },
    _loadCss: function(){
        var key = encodeURIComponent(this.cssPath);
        if (MWF.widget.css[key]){
            this.css = MWF.widget.css[key];
        }else{
            var r = new Request.JSON({
                url: this.cssPath,
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function(responseJSON, responseText){
                    this.css = responseJSON;
                    MWF.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function(text, error){
                    alert(error + text);
                }
            });
            r.send();
        }
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
            "cellSpacing": "5",
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
            td.set("text", ""+m+this.app.lp.month);

            td.setStyle("background-color", "#FFF");
            if ((this.year == thisY) && (idx == thisM)){
                td.setStyle("background-color", "#EEE");
            }
            if ((this.year == todayY) && (idx == todayM)){
                td.setStyle("background-color", "#CCC");
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

        this.monthSelectBottomNode.addEvent("click", function(){
            this.todayMonth();
        }.bind(this));
    },
    prevYear: function(){
        debugger;
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