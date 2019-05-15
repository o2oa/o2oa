MWF.require("MWF.widget.Calendar", null, false);
MWF.xApplication.Meeting.WeekView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "date" : ""
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$WeekView/";
        this.cssPath = "/x_component_Meeting/$WeekView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.weekBegin = this.app.meetingConfig.weekBegin || "0";
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

        var size = this.container.getSize();
        var y = size.y-60;

        this.node.setStyle("height", ""+y+"px");
        if( !this.app.inContainer  ){
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
        }else{
            date = new Date();
        }

        this.currentWeek = this.getWeekNumber( date );

        this.calendar = new MWF.xApplication.Meeting.WeekView.Calendar(this, date  );

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

MWF.xApplication.Meeting.WeekView.Calendar = new Class({
    Implements: [Events],
    initialize: function(view, date){
        this.view = view;
        this.css = this.view.css;
        this.container = this.view.node;
        this.app = this.view.app;
        this.weekBegin = this.app.meetingConfig.weekBegin || "0";
        this.baseDate = date || new Date();
        this.today = new Date();
        this.days = {};
        this.load();
    },
    load: function(){
        this.date = this.getWeekBeginDate( this.baseDate );

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
        this.listRoom( function(){
            this.setBodyNode();
        }.bind(this) );

        this.resetBodySize();
        this.app.addEvent("resize", this.resetBodySize.bind(this));

    },
    getWeekBeginDate: function( d ){
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
        //this.monthSelector = new MWF.xApplication.Meeting.WeekView.Calendar.WeekSelector(this.date, this);
        this.weekCalendar = new MWF.xApplication.Meeting.WeekView.WeekCalendar(this.titleTextNode, {
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
        this.date = this.getWeekBeginDate( d );
        //var text = this.date.format(this.app.lp.dateFormatWeek);
        //this.titleTextNode.set("text", text);

        var text = this.baseDate.format(this.app.lp.dateFormatMonth) + "，第" + this.view.getWeekNumber( this.baseDate  ) + "周";
        this.titleTextNode.set("text", text);

        this.reLoadCalendar();
    },
    listRoom : function( callback ){
        this.app.actions.listBuilding( function( json ){
            this.bulidingData = json.data;
            if(callback)callback();
        }.bind(this) )
    },
    setBodyNode: function(){
        this.roomTooltips = [];
        this.roomTrMap = {};

        //var html = "<tr><th>"+this.app.lp.room+"</th><th>"+this.app.lp.weeks.Mon+"</th><th>"+this.app.lp.weeks.Tues+"</th><th>"+this.app.lp.weeks.Wed+"</th>" +
        //    "<th>"+this.app.lp.weeks.Thur+"</th><th>"+this.app.lp.weeks.Fri+"</th><th>"+this.app.lp.weeks.Sat+"</th><th>"+this.app.lp.weeks.Sun+"</th></tr>";
        //html += "<tr><td valign='top'></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        //html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        //html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        //html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        //html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        //html += "<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
        this.calendarTable = new Element("table", {
            "styles": this.css.calendarTable,
            "height": "100%",
            "border": "0",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.bodyNode);

        this.loadTableHead();

        this.bulidingData.each( function( buliding ){
            buliding.roomList.each( function( room ){
                var tr = new Element( "tr").inject( this.calendarTable );
                var td = new Element( "td", {
                    "tdType" : "room"
                } ).inject( tr );
                var node = new Element("div",{
                    //text : room.name,
                    styles : this.css.calendarTableCell_room
                }).inject( td );
                td.store("room",room );
                for( var i=0; i<7; i++ ){
                    new Element( "td" , {
                        "tdType" : "meeting",
                        "room" : room.id,
                        "index" : i+1
                    }).inject( tr );
                }
                this.roomTrMap[ room.id ] = tr;
                this.rooms = this.rooms || {};

                this.rooms[ room.id ] = new MWF.xApplication.Meeting.WeekView.Room(this, node, room, buliding.name);

                this.roomTooltips.push(
                    new MWF.xApplication.Meeting.RoomTooltip(this.app.content, node, this.app, room, {
                        axis : "x",
                        hiddenDelay : 300,
                        displayDelay : 300
                    })
                );
            }.bind(this))
        }.bind(this));



        this.loadCalendar();
    },
    loadTableHead: function(){
        var d = this.date.clone();
        var today = new Date();
        if( !this.tableHead ){
            var head = this.tableHead = new Element("tr", {
                "styles" : this.css.calendarTableTitleTr
            }).inject( this.calendarTable );
            new Element("th", {
                "styles" : this.css.calendarTableTh,
                text : this.app.lp.room
            }).inject(head);
            for( var i=0; i<7; i++ ) {
                //if( this.weekBegin == "0" ){
                //    var index = i
                //}else{
                //    var index = i == 6 ? 0 : i+1
                //}
                var index = (  i + parseInt( this.weekBegin ) ) % 7;
                var th = new Element("th", {
                    "styles": (d < today)  ? this.css.calendarTableTh_pre :  this.css.calendarTableTh,
                    text: this.app.lp.weeks.arr[index] + "(" + d.format("%m.%d") + ")"
                }).inject(head);
                d.increment("day", 1);
            }
        }else{
            this.tableHead.getElements("th").each( function( th, i ){
                if( i == 0 )return;
                //if( this.weekBegin == "0" ){
                //    var index = i-1
                //}else{
                //    var index = i == 7 ? 0 : i
                //}
                th.setStyles( (d < today)  ? this.css.calendarTableTh_pre :  this.css.calendarTableTh);
                var index = (  i - 1 + parseInt( this.weekBegin ) ) % 7;
                th.set("text", this.app.lp.weeks.arr[index] + "(" + d.format("%m.%d") + ")");
                d.increment("day", 1);
            }.bind(this))
        }

    },
    reLoadCalendar: function(){
        for( var key in this.days ){
            this.days[key].destroy();
            delete this.days[key];
        }

        this.calendarTable.getElements("td[tdType='meeting']").each( function(td){
            td.empty();
        }.bind(this));

        this.loadTableHead();

        this.loadCalendar();
    },

    loadCalendar: function(){
        var date = this.date.clone();
        for( var i = 1; i<8; i++ ){
            this.loadDay(i, date);
            date.increment();
        }
        //var tds = this.calendarTable.getElements("td");
        //tds.each(function(td){
        //    this.loadDay(td, date);
        //    date.increment();
        //}.bind(this));
    },
    loadDay: function(index, date){
        var type = "thisWeek";
        var m = date.get("month");
        var y = date.get("year");
        var d = date.get("date");
        var mm = this.date.get("month");
        var yy = this.date.get("year");
        var mmm = this.today.get("month");
        var yyy = this.today.get("year");
        var ddd = this.today.get("date");
        if ((m==mmm) && (y==yyy) && (d==ddd)) {
            type = "today";
        }else{
            type = "thisWeek";
        }
        //}else if ( this.view.getWeekNumber( date ) == this.view.currentWeek ){
        //    type = "thisWeek";
        //}else{
        //    type = "otherWeek";
        //}

        var key = date.format("%Y%m%d");
        this.days[key] = new MWF.xApplication.Meeting.WeekView.Calendar.Day(index, date, this, type);
    },
    reload : function(){
        this.view.reload();
    },
    destroy: function(){
        for( var key in this.days ){
            this.days[key].destroy();
            delete this.days[key];
        }
        for( var key in this.rooms ){
            this.rooms[key].destroy();
        }

        this.roomTooltips.each( function(tooltip){
            tooltip.destroy();
        }.bind(this));

        this.calendarTable.getElements("td[tdType='meeting']").each( function(td){
            td.empty();
        }.bind(this));

        this.container.empty();
    }

});

MWF.xApplication.Meeting.WeekView.Room = new Class({
    Implements: [Events],
    initialize: function(view, node, data, buildingName ){
        this.data = data;
        this.view = view;
        this.css = this.view.css;
        this.container = node;
        this.app = this.view.app;
        this.meetings = [];
        this.buildingName = buildingName;
        this.enable = this.data.available;
        this.load();
    },
    load : function(){

        this.node = new Element("div.roomItemNode", {"styles": this.css.roomItemNode}).inject(this.container);
        this.node.setStyle("min-height",""+this.view.roomNodeHeight+"px");
        this.node.addEvents( {
            mouseover : function(){
                this.node.setStyles( this.css.roomItemNode_over  );
            }.bind(this),
            mouseout : function(){
                this.node.setStyles( this.css.roomItemNode  );
            }.bind(this)
        });

        this.titleNode = new Element("div.titleNode", { "styles": this.css.roomItemTitleNode }).inject(this.node);
        this.titleNode.addEvents({
            click : function(){
                this.openRoom()
            }.bind(this)
        });
        if( this.enable ){
            this.titleNode.addEvents({
                mouseenter : function(){
                    this.titleTextNode.setStyles( this.css.roomItemTitleTextNode_over );
                }.bind(this),
                mouseleave : function(){
                    this.titleTextNode.setStyles( this.css.roomItemTitleTextNode );
                }.bind(this)
            });
        }

        this.topNode = new Element("div.topNode", { styles : this.css.roomItemTitleTopNode }).inject( this.titleNode );

        this.descriptNode = new Element("div.roomItemDescriptNode",{
            styles : this.css.roomItemDescriptNode
        }).inject(this.titleNode);

        if( this.data.capacity ){
            this.titleCountNode = new Element("div.titleCountNode", {
                "styles": this.enable ? this.css.roomItemTitleCountNode : this.css.roomItemTitleCountNode_disable,
                "text" :  this.data.capacity+ this.app.lp.person
            }).inject(this.descriptNode);
        }
        if( this.data.roomNumber ){
            new Element("div.titleCountNode", {
                "styles": this.enable ? this.css.roomItemTitleCountNode : this.css.roomItemTitleCountNode_disable,
                "text" :  this.data.roomNumber
            }).inject(this.descriptNode);
        }


        if( this.buildingName ){
            this.buildingTextNode = new Element("div.buildingTextNode", {
                "styles": this.enable ? this.css.roomItemBuildingTextNode : this.css.roomItemBuildingTextNode_disable,
                "text" :  this.buildingName
            }).inject(this.titleNode);
        }

        this.titleTextNode = new Element("div.roomItemTitleTextNode", {
            "styles": this.enable ? this.css.roomItemTitleTextNode : this.css.roomItemTitleTextNode_disable ,
            "text" :  this.data.name
        }).inject(this.topNode);


        //this.middleNode = new Element("div.middleNode", {
        //    "styles": this.css.roomItemTitleMiddleNode
        //}).inject(this.titleNode);
        //
        //this.iconsNode = new Element("div.iconsNode", {
        //    "styles": this.css.roomItemTitleIconsNode
        //}).inject(this.middleNode);
        //
        //var deviceList = this.data.device.split("#");
        //deviceList.each(function(name){
        //    var node = new Element("div", {"styles": this.css.roomItemIconNode, "title": this.app.lp.device[name]}).inject(this.iconsNode);
        //    node.setStyle("background-image", "url(/x_component_Meeting/$RoomView/default/icon/device/"+  name + ( this.enable ? "" : "_disable" ) +".png)");
        //}.bind(this));
        //
        //this.actionsNode = new Element("div.actionsNode", {
        //    "styles": this.css.roomItemTitleActionsNode
        //}).inject(this.middleNode);
        //
        //this.loadActions();

    },
    loadActions: function(){

        if( MWF.AC.isMeetingAdministrator() ){
            this.editAction = new Element("div", {
                styles: this.css.roomAction_edit,
                events : {
                    mouseover : function(){
                        this.editAction.setStyles( this.css.roomAction_edit_over );
                    }.bind(this),
                    mouseout : function(){
                        this.editAction.setStyles( this.css.roomAction_edit );
                    }.bind(this),
                    click : function(e){
                        this.editRoom();
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionsNode);

            this.removeAction = new Element("div", {
                styles: this.css.roomAction_remove,
                events : {
                    mouseover : function(){
                        this.removeAction.setStyles( this.css.roomAction_remove_over );
                    }.bind(this),
                    mouseout : function(){
                        this.removeAction.setStyles( this.css.roomAction_remove );
                    }.bind(this),
                    click : function( e ){
                        this.removeRoom(e);
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionsNode);
        }

        if( this.enable ){
            this.createMeetingAction = new Element("div", {
                tltile : this.app.lp.addMeeting,
                styles: this.css.createMeetingAction,
                events : {
                    mouseover : function(){
                        this.createMeetingAction.setStyles( this.css.createMeetingAction_over );
                    }.bind(this),
                    mouseout : function(){
                        this.createMeetingAction.setStyles( this.css.createMeetingAction );
                    }.bind(this),
                    click : function(e){
                        this.app.addMeeting( this.view.date, this.view.hours, this.view.minutes, this.data.id);
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionsNode);
        }
    },
    editRoom : function(){
        var form = new MWF.xApplication.Meeting.RoomForm(this.app,this.data, {}, {app:this.app});
        form.view = this;
        form.edit();
    },
    openRoom : function(){
        var form = new MWF.xApplication.Meeting.RoomForm(this.app,this.data, {}, {app:this.app});
        form.view = this;
        form.open();
    },
    reload : function(){
        this.view.reload( this.view.date, this.view.hours, this.view.minutes );
    },
    removeRoom: function(e) {
        var info = this.app.lp.delete_room;
        info = info.replace(/{name}/g, this.data.name);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.delete_building_title, info, 300, 120, function(){
            _self.remove();
            this.close();
        }, function(){
            this.close();
        });
    },
    remove: function(){

        var view = this.view;
        this.app.actions.deleteRoom(this.data.id, function(){
            view.reload();
        }.bind(this));
    },
    resetHeight: function(){
        this.node.setStyle("min-height",""+this.view.roomNodeHeight+"px");
        if( this.noMeetingNode ){
            this.noMeetingNode.setStyle("min-height",""+(this.view.roomNodeHeight - 170)+"px");
            this.noMeetingNode.setStyle("line-height",""+(this.view.roomNodeHeight - 170)+"px");
        }
    },
    destroy: function(){
        if( this.calendar ){
            this.calendar.container.destroy();
        }
        if( this.tooltip ){
            this.tooltip.destroy();
        }
        this.meetings.each( function(m){
            m.destroy();
        });
        this.node.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Meeting.WeekView.Calendar.Day = new Class({
    Implements: [Events],
    initialize: function(index, date, calendar, type){
        this.index = index;
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
        //if(  this.type == "thisWeek" ){
        //}else if( this.type == "otherWeek" ){
        //    //this.color = "#ccc";
        //}
        this.day = this.date.getDate();
        this.month = this.date.getMonth();
        this.year = this.date.getYear();

        this.loadMeetings();

        this.roomMeetingObject = {};

        this.containerObject = {};
        this.calendar.calendarTable.getElements("td[index='"+ this.index +"']").each( function(td){
            this.containerObject[ td.get("room") ] = td;
        }.bind(this));
    },
    loadEmpty: function(){
        for( var key in this.containerObject ){
            var td = this.containerObject[key];
            if( !this.roomMeetingObject[ key ] ){
                var node = new Element("div", {
                    "styles" : this.css["calendarTableCell_"+this.type]
                }).inject( td );
                //var titleNode = new Element("div", {"styles": this.css["dayTitle_" + this.type]}).inject(node);
                //var titleDayNode = new Element("div", {
                //    "styles": this.css["dayTitleDay_" + this.type],
                //    "text": this.day
                //}).inject(titleNode);
                //
                //if ((new Date()).diff(this.date) >= 0) {
                //    titleNode.set("title", this.app.lp.titleNode);
                //    titleNode.addEvent("click", function () {
                //        this.app.addMeeting(this.date);
                //    }.bind(this));
                //}
                var contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(node);
                var textNode = new Element("div", {
                    "styles": {
                        "line-height": "60px",
                        "font-size": "14px",
                        "text-align" : "center",
                        "color" : this.color,
                        "padding": "20px 10px"
                    }
                }).inject( contentNode);
                textNode.set("text", this.app.lp.noMeeting);
            }
        }
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
            var length = json.data.length;
            json.data.each(function(meeting, i){
                if( !this.roomMeetingObject[ meeting.room ] ){
                    this.roomMeetingObject[ meeting.room ] = [];
                }
                this.roomMeetingObject[ meeting.room].push( meeting );
            }.bind(this));

            this.loadEmpty();

            this.loadRoomMeeting();
        }.bind(this));
    },
    loadRoomMeeting : function(){
        var meetingCount = 0;
        var firstStatus = "";
        var lastStatus = "";

        for( var key in this.roomMeetingObject ) {

            var td = this.containerObject[key];
            var node = new Element("div", {
                "styles": this.css["calendarTableCell_" + this.type]
            }).inject(td);
            var titleNode = new Element("div", {"styles": this.css["dayTitle_" + this.type]}).inject(node);
            var titleDayNode = new Element("div", {
                "styles": this.css["dayTitleDay_" + this.type],
                "text": this.day
            }).inject(titleNode);

            if ((new Date()).diff(this.date) >= 0) {
                titleNode.set("title", this.app.lp.titleNode);
                titleNode.addEvent("click", function () {
                    this.app.addMeeting(this.date);
                }.bind(this));
            }
            var contentNode = new Element("div", {"styles": this.css.dayContentNode}).inject(node);
            var meetingCount = 0;
            var myRejectCount = 0;
            var length = this.roomMeetingObject[key].length;
            this.roomMeetingObject[key].each(function (meeting, idx) {
                if (!meeting.myReject) {
                    meetingCount++;
                    if (meetingCount == 3) {
                        //this.contentNode.setStyle("height", "100px");
                    }
                    if (meetingCount == 1) {
                        firstStatus = meeting.status;
                        if (meeting.myWaitAccept)firstStatus = "myWaitAccept"
                    }
                    if (meetingCount + myRejectCount == length ) {
                        lastStatus = meeting.status;
                        if (meeting.myWaitAccept)lastStatus = "myWaitAccept"
                    }
                    //if (meetingCount<4)
                    var m = new MWF.xApplication.Meeting.WeekView.Calendar.Day.Meeting(this, contentNode, meeting, meetingCount);
                    m.parentNode = node;
                    m.parentTd = td;
                    this.meetings.push( m );
                }else{
                    myRejectCount++;
                }
            }.bind(this));

            if (meetingCount == 0) {
                //var node = new Element("div", {
                //    "styles": {
                //        "line-height": "40px",
                //        "font-size": "14px",
                //        "text-align" : "center",
                //        "color" : this.color,
                //        "padding": "0px 10px"
                //    }
                //}).inject(this.contentNode);
                //node.set("text", this.app.lp.noMeeting);
            } else {
                var titleInforNode = new Element("div", {"styles": this.css["dayTitleInfor_" + this.type]}).inject(titleNode);
                if( this.app.isViewAvailable( "toDay" ) ){
                    titleInforNode.addEvent("click", function (e) {
                        this.app.toDay(this.date);
                        e.stopPropagation();
                    }.bind(this));
                }else{
                    titleInforNode.setStyle("cursor","default");
                }
                titleInforNode.set("text", "" + meetingCount + this.app.lp.countMeetings + "");
                if (meetingCount > 3) {
                    node.addEvents({
                        "mouseenter": function () {
                            this.obj.expend( this.td, this.node );
                        }.bind({ obj : this, td : td, node : node }),
                        "mouseleave": function (){
                            this.obj.collapseReady = true;
                            this.obj.collapse( this.td, this.node );
                        }.bind({ obj : this, td : td, node : node })
                    })
                } else {
                    titleInforNode.setStyle("color", this.type == "otherMonth" ? "#ccc" : "#999");
                }

                if (firstStatus) {
                    switch (firstStatus) {
                        case "wait":
                            titleNode.setStyles({"border-left": "6px solid #4990E2"});
                            break;
                        case "processing":
                            titleNode.setStyles({"border-left": "6px solid #66CC7F"});
                            break;
                        case "completed":
                            titleNode.setStyles({"border-left": "6px solid #ccc"});
                            break;
                        case "myWaitAccept":
                            titleNode.setStyles({"border-left": "6px solid #F6A623"});
                            break
                    }
                }

                if (lastStatus) {
                    var heigth = 0;
                    if (meetingCount >= 3) {
                        heigth = 10;
                    } else {
                        heigth = 100 - meetingCount * 30;
                    }
                    var bottomEmptyNode = new Element("div", {
                        styles: {
                            "height": "" + heigth + "px"
                        }
                    }).inject(node);
                    switch (lastStatus) {
                        case "wait":
                            bottomEmptyNode.setStyles({"border-left": "6px solid #4990E2"});
                            break;
                        case "processing":
                            bottomEmptyNode.setStyles({"border-left": "6px solid #66CC7F"});
                            break;
                        case "completed":
                            bottomEmptyNode.setStyles({"border-left": "6px solid #ccc"});
                            break;
                        case "myWaitAccept":
                            bottomEmptyNode.setStyles({"border-left": "6px solid #F6A623"});
                            break
                    }
                }
            }
        }
    },
    expend : function( container, node ){
        this.oSize = node.getSize();
        container.setStyles({
            "position" : "relative"
        });
        this.tempNode = new Element("div",{
            styles : {
                width : (node.getSize().x ) + "px",
                height : "1px",
                margin : "7px"
            }
        }).inject(container);
        node.setStyles({
            "height" : node.getScrollSize().y + "px",
            "width" : (node.getSize().x ) + "px",
            "position" : "absolute",
            "top" : "0px",
            "left" : "0px",
            "box-shadow": "0 0 8px 0 rgba(0,0,0,0.25)"
        });
        var nodeCoordinate = node.getCoordinates();
        var contentNode = this.calendar.contentWarpNode;
        var contentCoordinate = contentNode.getCoordinates();
        if( nodeCoordinate.bottom > contentCoordinate.bottom ){
            this.contentHeight = contentCoordinate.height;
            contentNode.setStyle("height", (  nodeCoordinate.bottom - contentCoordinate.top  )+"px"  );
        }
        this.isCollapse = false;
    },
    collapse : function(container, node){
        if( !this.collapseDisable && this.collapseReady){
            container.setStyles({
                "position" : "static"
            });
            if( this.tempNode )this.tempNode.destroy();
            node.setStyles({
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
        if(this.titleNode){
            this.titleNode.destroy();
            this.titleNode = null;
        }
        this.titleDayNode = null;
        this.titleInforNode = null;

        delete this.calendar.days[this.key];

        //this.container.empty();
        MWF.release(this);
    },
    reload: function(){
        this.view.reload();
    }
});

MWF.xApplication.Meeting.WeekView.Calendar.Day.Meeting = new Class({
    initialize: function(day, node, data, index){
        this.day = day;
        this.css = this.day.css;
        this.view = this.day.view;
        this.app = this.day.app;
        this.container = node;
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
        //    this.iconNode.setStyle("background", "url(/x_component_Meeting/$WeekView/"+this.app.options.style+"/icon/invite.png) no-repeat center center");
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
                this.day.collapse(this.parentTd, this.parentNode);
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

MWF.xApplication.Meeting.WeekView.WeekCalendar = new Class({
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



