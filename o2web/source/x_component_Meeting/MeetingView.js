MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Meeting", "Common", null, false);

MWF.xApplication.Meeting.MeetingView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "months": 1
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Meeting/$MeetingView/";
        this.cssPath = "../x_component_Meeting/$MeetingView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.days = [];

        this.load();
    },
    recordStatus : function(){
        return {
            months : this.monthSelect.getValue()
        };
    },
    load: function(){
        this.userName = layout.desktop.session.user.distinguishedName;
        this.userId = layout.desktop.session.user.id;
        this.userIdentity = [];
        ( layout.desktop.session.user.identityList || [] ).each( function( i ){
            this.userIdentity.push( i.distinguishedName )
        }.bind(this));

        this.date = new Date();
        this.node = new Element("div#meetingNode", {"styles": this.css.node}).inject(this.container);
        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.node);

        this.todayNode = new Element("div", {"styles": this.css.todayNode}).inject(this.titleNode);
        var d = this.date.format(this.app.lp.dateFormatMonthDay);
        var w = this.app.lp.weeks.arr[this.date.getDay()];
        this.todayNode.set("text", d+","+w);

        this.scrollNode = new Element("div", {
            "styles":  this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.node);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.contentNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);

        this.dayContainerNode = new Element("div", {
            "styles": this.css.dayContainerNode
        }).inject(this.contentNode);

        this.loadMonthSelect();
        this.loadContent( );

        //this.node = new Element("div", {"styles": this.css.node}).inject(this.container);

        this.resetNodeSizeFun = this.resetNodeSize.bind(this);
        this.app.addEvent("resize", this.resetNodeSizeFun );

        //this.dateNode = new Element("div", {"styles": this.css.dateNode}).inject(this.node);

    },
    loadMonthSelect: function(){
        this.monthSelectContainer = new Element("div", {
            "styles": this.css.monthSelectContainer
        }).inject(this.titleNode);
        new Element("div", {
            "styles" : this.css.monthSelectTextNode,
            "text" : this.app.lp.monthSelectTextPrev
        }).inject(this.monthSelectContainer);

        var c = new Element("div", {
            "styles" : this.css.monthSelectTextNode
        }).inject(this.monthSelectContainer);

        this.monthSelect = new MDomItem( c, {
            name : "monthSelect",
            type : "select",
            style : this.css.monthSelect,
            defaultValue : this.options.months,
            selectValue : [1,2,3,4,5,6,7,8,9,10,11,12],
            event : {
                change : function(){
                    this.reload();
                }.bind(this)
            }
        });
        this.monthSelect.load();

        new Element("div", {
            "styles" : this.css.monthSelectTextNode,
            "text" : this.app.lp.monthSelectTextAfter
        }).inject(this.monthSelectContainer);
    },
    resetNodeSize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();

        if( !this.app.inContainer ){
            var y = size.y-60;
            this.node.setStyle("height", ""+y+"px");
            this.node.setStyle("margin-top", "60px");
        }

        var titleSize = this.titleNode.getSize();



        var y = size.y-titleSize.y-60;

        this.dayNodeHeight = y-60;


        this.scrollNode.setStyle("height", ""+y+"px");

        var sideBarSize = this.app.sideBar ? this.app.sideBar.getSize() : { x :0 , y :0 };
        this.scrollNode.setStyle("width", ""+(size.x - sideBarSize.x) +"px");

        var daysWidth = this.days.length * 330 + 30;
        var x = size.x - sideBarSize.x - 50;

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": Math.max( x, daysWidth) +"px"
            });
        };

        //if( this.noMeetingNode ){
        //    this.noMeetingNode.setStyles({
        //
        //    })
        //}


        this.days.each( function( d ){
            d.resetHeight();
        });
    },
    hasSameItem : function( array1, array2 ){
        for( var i=0;  i<array2.length; i++){
            if( array1.contains( array2[i] ) ){
                return true;
            }
        }
        return false;
    },
    loadContent: function(  ){
        var count = 0;
        this.daysData = {};
        var months = this.monthSelect.getValue() || this.options.months;
        this.app.actions.listMeetingMonths( months , function( json ){
            json.data.each( function( data ){
                if( data.invitePersonList.contains( this.userName ) || data.invitePersonList.contains( this.userId ) || data.applicant == this.userName || this.hasSameItem( data.invitePersonList, this.userIdentity ) ){
                    if( !data.rejectPersonList.contains( this.userName ) || !data.rejectPersonList.contains( this.userId ) || !this.hasSameItem( data.rejectPersonList, this.userIdentity ) ){
                        var date = Date.parse( data.startTime ).clone().clearTime();
                        if( !this.daysData[date] )this.daysData[date] = [];
                        this.daysData[date].push( data );
                        count++;
                    }
                }
            }.bind(this));

            for( var d in this.daysData ){
                var day = new MWF.xApplication.Meeting.MeetingView.Day(this, this.dayContainerNode, d , this.daysData[d]);
                this.days.push( day )
            }

            if( count == 0 ){
                this.loadEmptyNode();
            }

            this.resetNodeSize();

        }.bind(this));
        //this.dayA.loadAction();
    },
    loadEmptyNode : function(){
        this.noMeetingNode = new Element("div",{
            "styles" : this.css.noMeetingNode,
            "text" : this.app.lp.noComingMeeting.replace("{month}", this.monthSelect.getValue() || this.options.months )
        }).inject( this.dayContainerNode );
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
            }.bind(this))
        }

    },
    reload: function(){
        this.date = (this.days.length > 0 ? this.days[0].date.clone() : this.date);
        this.days.each( function(d){
            d.destroy();
        });
        this.dayContainerNode.empty();
        this.days = [];
        this.loadContent( );
    },
    destroy : function(){
        this.days.each( function(d){
            d.destroy();
        });
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.node.destroy();
    }

});

MWF.xApplication.Meeting.MeetingView.Day = new Class({
    Implements: [Events],
    initialize: function(view, node, date, data){
        this.view = view;
        this.css = this.view.css;
        this.container = node;
        this.app = this.view.app;
        this.date = (date) ? Date.parse(date).clone().clearTime() : (new Date()).clearTime();
        this.today = new Date().clearTime();
        this.isToday = (this.date.diff(this.today)==0);
        this.times = [];
        this.meetings = [];
        this.data = data;
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

        var className = !this.isToday ? "dayTitleTextNode" : "dayTitleTextNode_today";
        this.titleTextNode = new Element("div.dayTitleTextNode", {
            "styles": this.css[ className ],
            "text" :  this.date.format( this.app.lp.dateFormatDay )
        }).inject(this.titleNode);

        this.dayWeekNode = new Element("div.dayWeekNode", {
            "styles": this.css[ !this.isToday ? "dayWeekNode" : "dayWeekNode_today"],
            "text" : this.getWeek()
        }).inject(this.titleNode);

        this.dayContentNode = new Element("div.dayContentNode", {"styles": this.css.dayContentNode}).inject(this.node);

        this.loadMeetings();

    },
    resetHeight: function(){
        this.node.setStyle("min-height",""+this.view.dayNodeHeight+"px");
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
    destroy: function(){
        if( this.calendar ){
            this.calendar.container.destroy();
        }
        this.meetings.each( function(m){
            m.destroy();
        });
        this.node.destroy();
        MWF.release(this);
    },
    loadMeetings: function(){
        this.data.each(function(meeting, i){
            this.meetings.push(new MWF.xApplication.Meeting.MeetingView.Meeting(this.dayContentNode, this, meeting));
        }.bind(this));
    },
    reload : function(){
        this.view.reload();
    }
});

MWF.xApplication.Meeting.MeetingView.Meeting = new Class({
    Extends :  MWF.xApplication.Meeting.MeetingArea
});
