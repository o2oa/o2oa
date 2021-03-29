MWF.xApplication.Meeting.DayView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date": null
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Meeting/$DayView/";
        this.cssPath = "../x_component_Meeting/$DayView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        var d = this.options.date;
        if( d ){
            this.date =  typeOf( d ) == "string" ? new Date( d ) : d;
        }else{
            this.date = new Date();
        }
        this.load();
    },
    recordStatus : function(){
        return {
            date : (this.days.length > 0 ? this.days[0].date.clone() : this.date)
        };
    },
    load: function(){
        this.days = [];
        this.scrollNode = new Element("div", {
            "styles":  this.css.scrollNode //this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.container);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.node = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);

        this.leftNode = new Element("div",{
            "styles" : this.css.leftNode
        }).inject(this.node);
        this.leftNode.addEvents( {
            "click" : function(){ this.decrementDay() }.bind(this),
            "mouseover" : function(){ this.leftNode.setStyles( this.css.leftNode_over ) }.bind(this),
            "mouseout" : function(){ this.leftNode.setStyles( this.css.leftNode ) }.bind(this)
        });

        this.dayContainerNode = new Element("div", {
            "styles": this.css.dayContainerNode
        }).inject(this.node);

        this.rightNode = new Element("div",{
            "styles" : this.css.rightNode
        }).inject(this.node);
        this.rightNode.addEvents( {
            "click" : function(){ this.incrementDay() }.bind(this),
            "mouseover" : function(){ this.rightNode.setStyles( this.css.rightNode_over ) }.bind(this),
            "mouseout" : function(){ this.rightNode.setStyles( this.css.rightNode ) }.bind(this)
        });

        //this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        //this.loadSideBar();

        this.resetNodeSize();
        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        this.app.addEvent("resize", this.resetNodeSizeFun );

        //this.dateNode = new Element("div", {"styles": this.css.dateNode}).inject(this.node);

    },
    resetNodeSize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();

        var leftNodeSize = this.leftNode ? this.leftNode.getSize() : {x:0,y:0};
        var rightNodeSize = this.rightNode ? this.rightNode.getSize() : {x:0,y:0};
        var sideBarSize = this.app.sideBar ? this.app.sideBar.getSize() : {x:0,y:0};

        var availableX = size.x - leftNodeSize.x - rightNodeSize.x - sideBarSize.x ;

        this.dayNodeHeight = size.y-110;

        var leftTop = ( this.dayNodeHeight - leftNodeSize.y ) / 2;
        var rightTop = ( this.dayNodeHeight - rightNodeSize.y ) / 2;
        this.leftNode.setStyle("margin-top", ""+leftTop+"px");
        this.rightNode.setStyle("margin-top", ""+rightTop+"px");

        var dayCount = (availableX/330).toInt();

        this.scrollNode.setStyle("height", ""+(size.y-60)+"px");
        if( !this.app.inContainer ){
            this.scrollNode.setStyle("margin-top", "60px");
        }
        this.scrollNode.setStyle("margin-right", sideBarSize.x);

        if (this.contentWarpNode){
            var x = 330 * dayCount + leftNodeSize.x + rightNodeSize.x + sideBarSize.x;
            var m = (size.x - x)/2-10;
            this.contentWarpNode.setStyles({
                "width": ""+x+"px",
                "margin-left": ""+m+"px"
            });
        }
        if( this.dayCount != dayCount ){
            this.dayCount = dayCount;
            this.adjustDay();
        }else{
            for(var i = 0; i<this.days.length; i++ ){
                this.days[i].resetHeight();
            }
        }
    },
    toDay: function(date){
        this.date = date;
        this.dayContainerNode.empty();
        this.days = [];
        this.adjustDay();
    },
    adjustDay: function(){
        if( this.dayCount <= this.days.length ){
            this.date = this.days[this.dayCount].date.clone();
            for(var i = 0; i<this.days.length; i++ ){
                if( i < this.dayCount ){
                    this.days[i].resetHeight();
                }else{
                    if(this.days[i])this.days[i].destroy();
                }
            }
            this.days.splice( this.dayCount, (this.days.length - this.dayCount) );
        }else{
            for(var i = 0; i<this.days.length; i++ ){
                this.days[i].resetHeight();
            }
            if( this.days.length )this.date = this.days[this.days.length-1].date.clone().increment();
            this.loadDay( this.dayCount - this.days.length, this.days.length==0 )
        }
    },
    incrementDay : function(){
        if( this.days.length > 1 ) {
            var date = this.date = this.days[this.days.length-1].date.clone().increment();
            var node = this.days[this.days.length-1].node;
            this.days[0].destroy();
            this.days.splice(0, 1);
            this.days[0].setFrist();
            var day = new MWF.xApplication.Meeting.DayView.Day(this, node, "after", date, false );
            this.days.push(day);
        }else if( this.days.length == 1 ){
            var date = this.date = this.days[this.days.length-1].date.clone().increment();
            this.days[0].destroy();
            this.days.splice(0, 1);
            var day = new MWF.xApplication.Meeting.DayView.Day(this, this.dayContainerNode, null, date, false );
            this.days.push(day);
        }
    },
    decrementDay : function( node ){
        if( this.days.length > 1 ){
            var date = this.days[0].date.clone().decrement();
            this.days[0].disposeFrist();
            this.date = this.days[this.days.length-1].date.clone().decrement();
            this.days[this.days.length-1].destroy();
            this.days.splice(this.days.length-1, 1);
            var node = this.days[0].node;
            var day = new MWF.xApplication.Meeting.DayView.Day(this, node, "before", date, true);
            this.days.unshift( day )
        }else if( this.days.length == 1 ){
            var date = this.days[0].date.clone().decrement();
            this.date = this.days[this.days.length-1].date.clone().decrement();
            this.days[this.days.length-1].destroy();
            this.days.splice(this.days.length-1, 1);
            var day = new MWF.xApplication.Meeting.DayView.Day(this, this.dayContainerNode, null, date, true);
            this.days.unshift( day )
        }
    },
    loadDay: function( count, setFirst ){
        if (!this.date) this.date = new Date();
        if( !count )count = this.dayCount;
        var date = this.date;
        for( var i = 0; i< ( count || this.dayCount ); i++ ){
            var day = new MWF.xApplication.Meeting.DayView.Day(this, this.dayContainerNode, null,date, setFirst && i==0);
            this.days.push( day );
            date.increment();
        }

        //this.dayA.loadAction();
    },

    hide: function(){
        //this.app.removeEvent("resize", this.resetNodeSizeFun );
        var fx = new Fx.Morph(this.scrollNode, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 0
        }).chain(function(){
            this.scrollNode.setStyle("display", "none");
        }.bind(this));

    },
    show: function() {
        //this.app.addEvent("resize", this.resetNodeSizeFun );
        this.scrollNode.setStyles(this.css.scrollNode);
        this.scrollNode.setStyles({"display": ""});
        if (this.app.inContainer) {
            this.node.setStyles({
                "opacity": 1,
                "position": "static",
                "width": "auto",
                "display": ""
            });
        } else {
            var fx = new Fx.Morph(this.scrollNode, {
                "duration": "800",
                "transition": Fx.Transitions.Expo.easeOut
            });
            this.app.fireAppEvent("resize");
            fx.start({
                "opacity": 1,
                "left": "0px"
            }).chain(function () {
                this.scrollNode.setStyles({
                    "position": "static",
                    "width": "auto",
                    "display": ""
                });
            }.bind(this));
        }
    },
    reload: function(){
        this.date = (this.days.length > 0 ? this.days[0].date.clone() : this.date);
        this.days.each( function(d){
            d.destroy();
        });
        this.dayContainerNode.empty();
        this.days = [];
        this.loadDay( null, true );
    },
    destroy : function(){
        this.days.each( function(d){
            d.destroy();
        });
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.scrollNode.destroy();
    }

});

MWF.xApplication.Meeting.DayView.Day = new Class({
    Implements: [Events],
    initialize: function(view, node, position, date, isFirst){
        this.view = view;
        this.css = this.view.css;
        this.container = node;
        this.position = position || "bottom";
        this.app = this.view.app;
        this.date = (date) ? date.clone().clearTime() : (new Date()).clearTime();
        this.today = new Date().clearTime();
        this.isToday = (this.date.diff(this.today)==0);
        this.times = [];
        this.meetings = [];
        this.isFirst = isFirst;
        this.load();
    },
    load : function(){
        this.node = new Element("div.dayNode", {"styles": this.css.dayNode}).inject(this.container , this.position);
        this.node.setStyle("min-height",""+this.view.dayNodeHeight+"px");
        this.node.addEvents( {
            mouseover : function(){
                this.node.setStyles( this.css.dayNode_over  );
            }.bind(this),
            mouseout : function(){
                this.node.setStyles( this.css.dayNode  );
            }.bind(this)
         });

        this.titleNode = new Element("div.titleNode", { "styles": this.css[ !this.isToday ? "dayTitleNode" : "dayTitleNode_today"] }).inject(this.node);

        if( this.today.diff(this.date) >= 0  ){
            var className;
            className = !this.isToday ? "dayCreateIconNode" : "dayCreateIconNode_today";
            this.dayCreateIconNode = new Element("div.dayCreateIconNode", {
                "styles": this.css[ className ],
                "events" : {
                    "click" : function(){
                        this.app.addMeeting( this.date.clone().clearTime() );
                    }.bind(this)
                }
            }).inject(this.titleNode);
        }

        if( this.isFirst ){
            className = !this.isToday ? "dayTitleTextNode_first" : "dayTitleTextNode_today_first";
        }else{
            className = !this.isToday ? "dayTitleTextNode" : "dayTitleTextNode_today";
        }
        this.titleTextNode = new Element("div.dayTitleTextNode", {
            "styles": this.css[ className ],
            "text" :  this.date.format(this.app.lp.dateFormatDay)
        }).inject(this.titleNode);
        if( this.isFirst ){
            MWF.require("MWF.widget.Calendar", function(){
                this.calendar = new MWF.widget.Calendar(this.titleTextNode, {
                    "style":"meeting_blue",
                    "target": this.node,
                    "baseDate" : this.date,
                    "onQueryComplate": function(e, dv, date){
                        var selectedDate = new Date.parse(dv);
                        this.view.toDay(selectedDate);
                    }.bind(this)
                });
            }.bind(this));
        }

        this.dayWeekNode = new Element("div.dayWeekNode", {
            "styles": this.css[ !this.isToday ? "dayWeekNode" : "dayWeekNode_today"],
            "text" : this.getWeek()
        }).inject(this.titleNode);

        this.dayContentNode = new Element("div.dayContentNode", {"styles": this.css.dayContentNode}).inject(this.node);

        this.loadMeetings();

    },
    resetHeight: function(){
        this.node.setStyle("min-height",""+this.view.dayNodeHeight+"px");
        if( this.noMeetingNode ){
            this.noMeetingNode.setStyle("min-height",""+(this.view.dayNodeHeight - 220)+"px");
            this.noMeetingNode.setStyle("line-height",""+(this.view.dayNodeHeight - 220)+"px");
        }
    },
    getWeek: function(){
        var week = this.app.lp.weeks.arr[this.date.getDay()];
        var title = "";
        var now = this.today;
        var d = now.diff(this.date);
        if (d==0){
            title = this.app.lp.today;
        }else{
            title = week;
        }
        return title;
    },
    setFrist: function(){
        if( this.isFirst )return;
        this.isFirst = true;
        className = !this.isToday ? "dayTitleTextNode_first" : "dayTitleTextNode_today_first";
        this.titleTextNode.setStyles( this.css[ className ] );
        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(this.titleTextNode, {
                "style":"meeting_blue",
                "target": this.node,
                "baseDate" : this.date,
                "onQueryComplate": function(e, dv, date){
                    var selectedDate = new Date.parse(dv);
                    this.view.toDay(selectedDate);
                }.bind(this)
            });
        }.bind(this));
    },
    disposeFrist : function(){
        if( !this.isFirst )return;
        this.isFirst = false;
        this.titleTextNode.removeEvent("click");
        this.titleTextNode.removeEvent("focus");
        var className = !this.isToday ? "dayTitleTextNode" : "dayTitleTextNode_today";
        this.titleTextNode.setStyles( this.css[ className ] );
        this.calendar.container.destroy();
        this.calendar = null;
    },
    destroy: function(){
        if( this.calendar ){
            this.calendar.container.destroy();
        }
        this.meetings.each( function(m){
            m.destroy();
        });
        this.meetings = [];
        this.node.destroy();
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

        this.app.actions[ isAll ? "listMeetingDayAll" : "listMeetingDay" ](y, m, d, function(json){
            var flag = true;
            if( !json.data || json.data.length == 0 ){
            }else{
                json.data.each(function(meeting, i){
                    if (!meeting.myReject){
                        flag = false;
                        this.meetings.push(new MWF.xApplication.Meeting.DayView.Meeting(this.dayContentNode, this, meeting));
                    }
                }.bind(this));
            }
            if( flag ){
                this.noMeetingNode = new Element("div.noMeetingNode", {
                    "styles": this.css.noMeetingNode,
                    "text" :  this.app.lp.noMeeting
                }).inject(this.dayContentNode);
                this.noMeetingNode.setStyle("min-height",""+(this.view.dayNodeHeight - 220)+"px");
                this.noMeetingNode.setStyle("line-height",""+(this.view.dayNodeHeight - 220)+"px");
            }
            //this.checkMeetingWidth();
            //this.checkMeetingWidthFun = this.checkMeetingWidth.bind(this);
            //this.app.addEvent("resize", this.checkMeetingWidthFun);


        }.bind(this));
    },
    reload : function(){
        this.view.reload();
    }
});

MWF.xApplication.Meeting.DayView.Meeting = new Class({
    Extends :  MWF.xApplication.Meeting.MeetingArea
});
