MWF.xApplication.Meeting.DayView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$DayView/";
        this.cssPath = "/x_component_Meeting/$DayView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.date = this.options.date || new Date();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.dayNodeA = new Element("div", {"styles": this.css.dayAreaNode}).inject(this.node);
        this.dayNodeB = new Element("div", {"styles": this.css.dayAreaNode}).inject(this.node);
        this.dayNodeC = new Element("div", {"styles": this.css.dayAreaNode}).inject(this.node);

        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));

        //this.dateNode = new Element("div", {"styles": this.css.dateNode}).inject(this.node);

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

        //var size = this.container.getSize();
        //var y = size.y-20;
        //this.node.setStyle("height", ""+y+"px");

        var dayx = size.x/3
        this.dayNodeA.setStyle("width", ""+dayx+"px");
        this.dayNodeB.setStyle("width", ""+dayx+"px");
        this.dayNodeC.setStyle("width", ""+dayx+"px");
    },
    toDay: function(date){
        this.date = date;
        if (this.currentDocument) this.currentDocument.closeDocument();
        this.dayA.destroy();
        this.dayB.destroy();
        this.dayC.destroy();
        this.loadCalendar();
    },
    loadCalendar: function(){
        if (!this.date) this.date = new Date();
        var date = this.date.clone();
        this.dayA = new MWF.xApplication.Meeting.DayView.Calendar(this, this.dayNodeA, date);
        date.increment();
        this.dayB = new MWF.xApplication.Meeting.DayView.Calendar(this, this.dayNodeB, date);
        date.increment();
        this.dayC = new MWF.xApplication.Meeting.DayView.Calendar(this, this.dayNodeC, date);

        this.dayA.loadAction();
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
        this.dayA.destroy();
        this.dayB.destroy();
        this.dayC.destroy();
        this.loadCalendar();
    }

});

MWF.xApplication.Meeting.DayView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, node, date){
        this.view = view
        this.css = this.view.css;
        this.container = node;
        this.app = this.view.app;
        this.date = (date) ? date.clone().clearTime() : (new Date()).clearTime();
        this.today = new Date().clearTime();
        this.isToday = (this.date.diff(this.today)==0);
        this.times = [];
        this.meetings = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.dayNode}).inject(this.container);

        this.titleNode = new Element("div", {"styles": this.css.dayTitleNode}).inject(this.node);
        this.titleDateNode = new Element("div", {"styles": this.css.dayTitleDateNode}).inject(this.node);
        if (this.isToday) this.titleDateNode.setStyles(this.css.dayTitleDateNode_today);

        this.timeAreaNode = new Element("div", {"styles": this.css.dayTimeAreaNode}).inject(this.node);
        this.timeContentNode = new Element("div", {"styles": this.css.dayTimeContentNode}).inject(this.timeAreaNode);

        this.setTimeAreaSize();
        this.setTimeAreaSizeFun = this.setTimeAreaSize.bind(this);
        this.app.addEvent("resize", this.setTimeAreaSizeFun);

        this.loadContent();
    },
    loadAction: function(){
        this.downNode = new Element("div", {"styles": this.css.dayTitleDownNode}).inject(this.titleNode);
        this.titleNode.setStyle("cursor", "pointer");
        this.setActionEvent(this.titleNode);
        this.setActionEvent(this.downNode);
        this.titleNode.set("value", this.date.format("db"));
    },
    setActionEvent: function(node){
        node.addEvents({
            "mouseover": function(){
                this.titleNode.setStyles(this.css.dayTitleNode_over);
                this.downNode.setStyles(this.css.dayTitleDownNode_over);
            }.bind(this),
            "mouseout": function(){
                this.titleNode.setStyles(this.css.dayTitleNode);
                this.downNode.setStyles(this.css.dayTitleDownNode);
            }.bind(this),
            "mousedown": function(){
                this.titleNode.setStyles(this.css.dayTitleNode_down);
                this.downNode.setStyles(this.css.dayTitleDownNode_down);
            }.bind(this),
            "mouseup": function(){
                this.titleNode.setStyles(this.css.dayTitleNode_over);
                this.downNode.setStyles(this.css.dayTitleDownNode_over);
            }.bind(this)
        });

        MWF.require("MWF.widget.Calendar", function(){
            new MWF.widget.Calendar(this.titleNode, {
                "style":"meeting",
                "target": this.node,
                "onQueryComplate": function(e, dv, date){
                    var selectedDate = new Date.parse(dv);
                    this.view.toDay(selectedDate);
                }.bind(this)
            });
        }.bind(this));
    },

    setTimeAreaSize: function(){
        var size = this.node.getSize();
        var titleSize = this.titleNode.getSize();
        var titleDateSize = this.titleDateNode.getSize();
        var y = size.y-titleSize.y-titleDateSize.y;
        this.timeAreaNode.setStyle("height", ""+y+"px");
    },
    loadContent: function(){
        this.loadTitle();
        this.loadTimes();
        this.loadMeetings();
    },
    loadTitle: function(){
        var week = this.app.lp.weeks.arr[this.date.getDay()];
        var title = "";
        var now = this.today;
        var d = now.diff(this.date);
        if (d==0){
            title = this.app.lp.today;
        }else if (d==1){
            title = this.app.lp.tomorrow
        }else if (d==2){
            title = this.app.lp.afterTomorrow
        }else if (d==-1){
            title = this.app.lp.yesterday
        }else if (d==-2){
            title = this.app.lp.beforeYesterday
        }else{
            title = week;
        }
        this.titleNode.set("text", title);
        this.titleDateNode.set("text", this.date.format(this.app.lp.dateFormatDay)+" "+week);
    },
    loadTimes: function(){
        this.timeTitleAreaNode = new Element("div", {"styles": this.css.timeTitleAreaNode}).inject(this.timeContentNode);
        this.timeBodyAreaNode = new Element("div", {"styles": this.css.timeBodyAreaNode}).inject(this.timeContentNode);

        for (var h=0; h<24; h++){
            var date = this.date.clone();
            date.set("hr", h);
            date.set("min", 0);
            date.set("sec", 0);
            date.set("ms", 0);
            this.times.push(new MWF.xApplication.Meeting.DayView.Calendar.Hour(this, date));
        }
    },
    loadMeetings: function(){
        var y = this.date.getFullYear();
        var m = this.date.getMonth()+1;
        var d = this.date.getDate();
        this.app.actions.listMeetingDay(y, m, d, function(json){
            json.data.each(function(meeting, i){
                if (!meeting.myReject){
                    this.meetings.push(new MWF.xApplication.Meeting.DayView.Calendar.Meeting(this, meeting));
                }
            }.bind(this));

            this.checkMeetingWidth();
            this.checkMeetingWidthFun = this.checkMeetingWidth.bind(this);
            this.app.addEvent("resize", this.checkMeetingWidthFun);


        }.bind(this));
    },
    checkMeetingWidth: function(){
        this.meetings.each(function(meeting){
            var timeBodysize = this.timeBodyAreaNode.getSize();
            var w = (timeBodysize.x/meeting.intersectionCount)-1;
            meeting.node.setStyle("width",""+w+"px");
        }.bind(this));
    },

    destroy: function(){
        if (this.checkMeetingWidthFun) this.app.removeEvent("resize", this.checkMeetingWidthFun);
        this.app.removeEvent("resize", this.setTimeAreaSizeFun);
        this.times.each(function(time){
            time.destroy();
        });
        this.meetings.each(function(meeting){
            meeting.destroy();
        });
        this.meetings = [];
        this.times = [];
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Meeting.DayView.Calendar.Meeting = new Class({
    initialize: function(day, data){
        this.day = day
        this.view = this.day.view
        this.css = this.view.css;
        this.app = this.view.app;
        this.data = data;
        this.beginDate = Date.parse(this.data.startTime);
        this.endDate = Date.parse(this.data.completedTime);
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.meetingNode});
        this.colorNode = new Element("div", {"styles": this.css.meetingColorNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.meetingContentNode}).inject(this.node);
        this.subjectNode = new Element("div", {"styles": this.css.meetingSubjectNode}).inject(this.contentNode);
        //this.descriptionNode = new Element("div", {"styles": this.css.meetingSubjectNode}).inject(this.node);
        this.subjectNode.set("text", this.data.subject);

        switch (this.data.status){
            case "wait":
                //nothing
                break;
            case "processing":
                this.colorNode.setStyles({"background-color": "#18da14"});
                this.contentNode.setStyles({"background-color": "#deffdd"});
                break
            case "completed":
                //add attachment
                this.colorNode.setStyles({"background-color": "#555"});
                this.subjectNode.setStyles({"color": "#666"});
                break;
        }
        if (this.data.myWaitAccept){
            this.colorNode.setStyles({"background-color": "#ecd034"});
            this.contentNode.setStyles({"background-color": "#fffade"});
        }
        this.positionNode();
        this.checkMax();

        this.node.addEvents({
            "mouseover": function(){this.node.setStyles(this.css.meetingNode_over);}.bind(this),
            "mouseout": function(){this.node.setStyles(this.css.meetingNode);}.bind(this),
            "mousedown": function(){this.node.setStyles(this.css.meetingNode_down);}.bind(this),
            "mouseup": function(){this.node.setStyles(this.css.meetingNode_over);}.bind(this),
            "click": function(){this.openMeeting();}.bind(this),
        });
    },
    openMeeting: function(){
        if (!this.document){
            if (this.view.currentDocument) this.view.currentDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.DayView.Calendar.Meeting.Document(this);
            this.view.currentDocument = this.document;
        }
    },
    checkMax: function(){
        this.prevIntersection = null;
        this.intersectionCount = 1;
        debugger;
        for (var i=this.day.meetings.length-1; i>=0; i--){
            var pmt = this.day.meetings[i];
            if (this.isIntersection(this.beginDate, this.endDate, pmt.beginDate, pmt.endDate)){
                this.prevIntersection = pmt;
                break;
            }
        }

        var mt = this;
        while (mt.prevIntersection){
            this.intersectionCount++;
            mt = mt.prevIntersection;
        }

        if (this.prevIntersection){
            if (this.prevIntersection.intersectionCount<this.intersectionCount){
                mt = this.prevIntersection;
                while (mt){
                    mt.intersectionCount = this.intersectionCount;
                    mt = mt.prevIntersection;
                }
            }
        }

    },
    isIntersection: function(d1,d2,d3,d4) {
        var bd1 = d1.clone();
        var ed1 = d2.clone();
        var bd2 = d3.clone();
        var ed2 = d4.clone();
        this.expandDateRange(bd1, ed1);
        this.expandDateRange(bd2, ed2);

        if ((bd1 > bd2) && (bd1 < ed2)) return true;
        if ((ed1 > bd2) && (ed1 < ed2)) return true;
        if ((bd2 > bd1) && (bd2 < ed1)) return true;
        if ((ed2 > bd1) && (ed2 < ed1)) return true;
        if ((bd1.diff(bd2, "minute")==0) && (ed1.diff(ed2, "minute")==0)) return true;

        return false;
    },
    expandDateRange: function(bd, ed){
        bd.set("min", 0);
        bd.set("sec", 0);
        bd.set("ms", 0);
        ed.set("min", 59);
        ed.set("sec", 59);
        ed.set("ms", 0);
        //ed.increment("hour", 1);
    },
    positionNode: function(){
        var bh = this.beginDate.getHours();
        var bm = this.beginDate.getMinutes();
        var eh = this.endDate.getHours();
        var em = this.endDate.getMinutes();

        //this.hours = [];
        //for (var i=bh; i<=eh; i++){
        //    this.hours.push(i);
        //    this.day.times[i].meetings.push(this);
        //}
        var m = this.beginDate.diff(this.endDate, "minute");
        var y = (m/60)*49;

        var marginTop = (bm/60)*49;

        this.node.inject(this.day.times[bh].contentNode);

        this.node.setStyle("padding-top", ""+marginTop+"px");
        if (y>30) this.contentNode.setStyle("height", ""+y+"px");
        this.colorNode.setStyle("height", ""+y+"px");
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.DayView.Calendar.Meeting.Document = new Class({
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

MWF.xApplication.Meeting.DayView.Calendar.Hour = new Class({
    initialize: function(day, date){
        this.day = day
        this.view = this.day.view
        this.css = this.view.css;
        this.app = this.view.app;
        this.date = date;
        this.meetings = [];
        this.load();
    },
    load: function(){
        this.titleNode = new Element("div", {"styles": this.css.hourTitleNode}).inject(this.day.timeTitleAreaNode);
        this.contentNode = new Element("div", {"styles": this.css.hourContentNode}).inject(this.day.timeBodyAreaNode);

        this.titleNode.set("text", this.date.getHours());

        this.titleNode.addEvent("click", function(){
            this.app.addMeeting(this.date, this.date.getHours());
        }.bind(this));
    },
    destroy: function(){
        this.titleNode.destroy();
        this.contentNode.destroy();
        MWF.release(this);
    }
});