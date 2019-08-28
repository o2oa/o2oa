MWF.xDesktop.requireApp("Meeting", "MeetingView", null, false);
MWF.xApplication.Meeting.RoomView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "buildingId" : "",
        "date" : "",
        "hours" : 1,
        "minutes" : 0
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$RoomView/";
        this.cssPath = "/x_component_Meeting/$RoomView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.bulidings = [];
        this.rooms = [];
        if( this.options.date ){
            this.date = Date.parse( this.options.date);
        }else{
            this.date = new Date();
            this.date.increment("hour",1);
            this.date.setMinutes(0);
        }
        this.hours = parseInt( this.options.hours );
        this.minutes = parseInt( this.options.minutes );
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);

        this.roomArea = new Element("div.roomArea", {"styles": this.css.roomArea}).inject(this.node);
        //this.infoArea = new Element("div.infoArea", {"styles": this.css.infoArea}).inject(this.node);

        this.roomDateArea = new Element("div.roomDateArea", {"styles": this.css.roomDateArea}).inject(this.roomArea);
        this.roomTopArea = new Element("div.roomTopArea", {"styles": this.css.roomTopArea}).inject(this.roomArea);

        this.scrollNode = new Element("div", {
            "styles":  this.app.inContainer ? this.css.scrollNode_inContainer : this.css.scrollNode
        }).inject(this.roomArea);
        this.contentWarpNode = new Element("div", {
            "styles": this.css.contentWarpNode
        }).inject(this.scrollNode);

        this.contentContainerNode = new Element("div",{
            "styles" : this.css.contentContainerNode
        }).inject(this.contentWarpNode);
        this.bodyNode = new Element("div", {
            "styles": this.css.contentNode
        }).inject(this.contentContainerNode);

        this.roomBuildingsArea = new Element("div.roomBuildingsArea", {"styles": this.css.roomBuildingsArea}).inject(this.bodyNode);

        this.app.addEvent("resize", this.resetNodeSize.bind(this));

       // this.loadSideBar();
        this.loadDate();
        this.loadTop( function(){
            this.resetNodeSize();
        }.bind(this));
    },
    loadDate: function(){
        var dateText = this.date.format(this.app.lp.dateFormatAll);
        this.roomDateNode = new Element("div", {
            "styles": this.css.roomDateNode,
            "text" : dateText
        }).inject(this.roomDateArea);

        this.roomHourRangeNode = new Element("div", {"styles": this.css.roomHourRangeNode}).inject(this.roomDateArea);
        var html = this.app.lp.persist+" <select data-id='hour'>" +
            "<option "+((this.hours==0) ? "selected" : "")+" value=0>0</option>" +
            "<option "+((this.hours==1) ? "selected" : "")+" value=1>1</option>" +
            "<option "+((this.hours==2) ? "selected" : "")+" value=2>2</option>" +
            "<option "+((this.hours==3) ? "selected" : "")+" value=3>3</option>" +
            "<option "+((this.hours==4) ? "selected" : "")+" value=4>4</option>" +
            "<option "+((this.hours==5) ? "selected" : "")+" value=5>5</option>" +
            "<option "+((this.hours==6) ? "selected" : "")+" value=6>6</option>" +
            "<option "+((this.hours==7) ? "selected" : "")+" value=7>7</option>" +
            "<option "+((this.hours==8) ? "selected" : "")+" value=8>8</option>" +
            "<option "+((this.hours==9) ? "selected" : "")+" value=9>9</option>" +
            "<option "+((this.hours==10) ? "selected" : "")+" value=10>10</option>" +
            "<option "+((this.hours==11) ? "selected" : "")+" value=11>11</option>" +
            "<option "+((this.hours==12) ? "selected" : "")+" value=12>12</option>" +
            "<option "+((this.hours==13) ? "selected" : "")+" value=13>13</option>" +
            "<option "+((this.hours==14) ? "selected" : "")+" value=14>14</option>" +
            "<option "+((this.hours==15) ? "selected" : "")+" value=15>15</option>" +
            "<option "+((this.hours==16) ? "selected" : "")+" value=16>16</option>" +
            "<option "+((this.hours==17) ? "selected" : "")+" value=17>17</option>" +
            "<option "+((this.hours==18) ? "selected" : "")+" value=18>18</option>" +
            "<option "+((this.hours==19) ? "selected" : "")+" value=19>19</option>" +
            "<option "+((this.hours==20) ? "selected" : "")+" value=20>20</option>" +
            "<option "+((this.hours==21) ? "selected" : "")+" value=21>21</option>" +
            "<option "+((this.hours==22) ? "selected" : "")+" value=22>22</option>" +
            "<option "+((this.hours==23) ? "selected" : "")+" value=23>23</option>" +
            "<option "+((this.hours==24) ? "selected" : "")+" value=24>24</option>" +
            "</select> "+
            this.app.lp.hour+
                " <select data-id='minute'>" +
                "<option "+((this.minutes==0) ? "selected" : "")+" value=0>0</option>" +
                "<option "+((this.minutes==5) ? "selected" : "")+" value=5>5</option>" +
                "<option "+((this.minutes==10) ? "selected" : "")+" value=10>10</option>" +
                "<option "+((this.minutes==15) ? "selected" : "")+" value=15>15</option>" +
                "<option "+((this.minutes==20) ? "selected" : "")+" value=20>20</option>" +
                "<option "+((this.minutes==25) ? "selected" : "")+" value=25>25</option>" +
                "<option "+((this.minutes==30) ? "selected" : "")+" value=30>30</option>" +
                "<option "+((this.minutes==35) ? "selected" : "")+" value=35>35</option>" +
                "<option "+((this.minutes==40) ? "selected" : "")+" value=40>40</option>" +
                "<option "+((this.minutes==45) ? "selected" : "")+" value=45>45</option>" +
                "<option "+((this.minutes==50) ? "selected" : "")+" value=50>50</option>" +
                "<option "+((this.minutes==55) ? "selected" : "")+" value=55>55</option>" +
                "</select> "+this.app.lp.minute;

        this.roomHourRangeNode.set("html", html);

        this.roomHourRangeSelect = this.roomHourRangeNode.getElement("select[data-id='hour']");
        this.roomHourRangeSelect.setStyles(this.css.roomHourRangeSelectNode);

        this.roomMinuteRangeSelect = this.roomHourRangeNode.getElement("select[data-id='minute']");
        this.roomMinuteRangeSelect.setStyles(this.css.roomHourRangeSelectNode);

        this.roomHourRangeSelect.addEvent("change", function(){
            var h = this.roomHourRangeSelect.options[this.roomHourRangeSelect.selectedIndex].get("value");
            var m = this.roomMinuteRangeSelect.options[this.roomMinuteRangeSelect.selectedIndex].get("value");
            this.reload(this.date, h, m);
        }.bind(this));

        this.roomMinuteRangeSelect.addEvent("change", function(){
            var h = this.roomHourRangeSelect.options[this.roomHourRangeSelect.selectedIndex].get("value");
            var m = this.roomMinuteRangeSelect.options[this.roomMinuteRangeSelect.selectedIndex].get("value");
            this.reload(this.date, h, m);
        }.bind(this));

        this.roomDateNode.addEvents({
            "mouseover": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_over);
            }.bind(this),
            "mouseout": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode);
            }.bind(this),
            "mousedown": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_down);
            }.bind(this),
            "mouseup": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_over);
            }.bind(this)
        });
        MWF.require("MWF.widget.Calendar", function(){
            new MWF.widget.Calendar(this.roomDateNode, {
                "style":"meeting_blue",
                "isTime": true,
                "target": this.node,
                "onQueryComplate": function(e, dv, date){
                    this.selectedDate = true;
                    var selectedDate = new Date.parse(dv);
                    var h = this.roomHourRangeSelect.options[this.roomHourRangeSelect.selectedIndex].get("value");
                    var m = this.roomMinuteRangeSelect.options[this.roomMinuteRangeSelect.selectedIndex].get("value");
                    this.reload(selectedDate, h, m);
                }.bind(this)
            });
        }.bind(this));

        this.helpNode = new Element("div", {
            "styles": this.css.roomHelpNode,
            "events" : {
                mouseover : function(){
                    this.helpNode.setStyles( this.css.roomHelpNode_over );
                }.bind(this),
                mouseout : function(){
                    this.helpNode.setStyles( this.css.roomHelpNode );
                }.bind(this)
            }
        }).inject(this.roomDateArea);
        new MWF.xApplication.Meeting.RoomView.HelpTooltip( this.app.content, this.helpNode, this.app, {}, {
            hiddenDelay : 300,
            displayDelay : 0,
            nodeStyles : {
                "min-width" : "260px",
                "border-radius" : "4px"
            }
        });
    },
    loadTop : function( callback ){
        var startTime = this.date.clone();
        startTime.set("sec", 0);
        var start = startTime.format("%Y-%m-%d %H:%M");
        if(this.hours)startTime.increment("hour", this.hours);
        if(this.minutes)startTime.increment("minute", this.minutes);
        var complete = startTime.format("%Y-%m-%d %H:%M");
        this.endDate = startTime;

        var _self = this;

        var buliding = new Element( "div", {
            text : this.app.lp.all,
            styles : this.css.roomTopItemNode,
            events : {
                mouseover : function( ev ){
                    var node = ev.target;
                    if( _self.currentBuliding != node )node.setStyles( _self.css.roomTopItemNode_over );
                },
                mouseout : function( ev ){
                    var node = ev.target;
                    if( _self.currentBuliding != node )node.setStyles( _self.css.roomTopItemNode )
                },
                click : function(ev){
                    var node = ev.target;
                    if(_self.currentBuliding)_self.currentBuliding.setStyles( _self.css.roomTopItemNode );
                    _self.currentBuliding = node;
                    node.setStyles( _self.css.roomTopItemNode_current );
                    _self.emptyRooms();
                    _self.loadAllRooms();
                }
            }
        }).inject( this.roomTopArea );
        buliding.store( "data", {"id":"all"} );
        this.bulidings.push( buliding );

        this.app.actions.listBuildingByRange((start), (complete), function( json ){
            this.bulidingData = json.data;
            //buliding.click();
            json.data.each( function( b, i ){
                //if( !b.roomList || !b.roomList.length )return;
                var buliding = new Element( "div", {
                    text : b.name,
                    styles : this.css.roomTopItemNode,
                    events : {
                        mouseover : function( ev ){
                            var node = ev.target;
                            if( _self.currentBuliding != node )node.setStyles( _self.css.roomTopItemNode_over );
                            _self.showBulidingTooltip( ev.target );
                        },
                        mouseout : function( ev ){
                            var node = ev.target;
                            if( _self.currentBuliding != node )node.setStyles( _self.css.roomTopItemNode )
                        },
                        click : function(ev){
                            var node = ev.target;
                            if(_self.currentBuliding)_self.currentBuliding.setStyles( _self.css.roomTopItemNode );
                            _self.currentBuliding = node;
                            node.setStyles( _self.css.roomTopItemNode_current );
                            _self.emptyRooms();
                            _self.loadRooms( node );
                        }
                    }
                }).inject( this.roomTopArea );
                buliding.store( "data", b );
                this.bulidings.push( buliding );
                if( this.options.buildingId == b.id )buliding.click();
            }.bind(this));
            if( !this.options.buildingId || this.options.buildingId == "all" ){
                this.bulidings[0].click();
            }
            if( callback )callback();
        }.bind(this))
    },
    showBulidingTooltip : function( node ){
        var data = node.retrieve("data");
        if( !this.bulidingTooltips )this.bulidingTooltips = {};
        var tooltip = this.bulidingTooltips[ data.id ];
        if( tooltip ){
            tooltip.load();
        }else{
            tooltip = new MWF.xApplication.Meeting.BuildingTooltip( this.app.content, node, this.app, data, {
                hiddenDelay : 300,
                displayDelay : 0,
                nodeStyles : {
                    "min-width" : "100px",
                    "border-radius" : "4px"
                }
            });
            tooltip.view = this;
            tooltip.css = this.css;
            this.bulidingTooltips[data.id] = tooltip;
            tooltip.load();
        }
    },

    resetNodeSize: function(){
        //if( this.app.inContainer )return;
        var size = this.container.getSize();

        if( !this.app.inContainer ){
            var y = size.y-50;
            this.node.setStyle("height", ""+y+"px");
            this.node.setStyle("margin-top", "50px");
        }

        var dateSize = this.roomDateArea.getSize();
        var topSize = this.roomTopArea.getSize();

        var y = size.y-dateSize.y-topSize.y-60;

        this.roomNodeHeight = y-60;


        this.scrollNode.setStyle("height", ""+y+"px");

        var sideBarSize = this.app.sideBar ? this.app.sideBar.getSize() : { x :0 , y:0 };
        this.scrollNode.setStyle("width", ""+ ( size.x - sideBarSize.x ) +"px");

        var roomsWidth = this.rooms.length * 330 + 30;
        var x = size.x - sideBarSize.x - 50;

        if (this.contentWarpNode){
            this.contentWarpNode.setStyles({
                "width": Math.max( x, roomsWidth) +"px"
            });
        }

        this.rooms.each( function( m ){
            m.resetHeight();
        });
    },
    loadAllRooms : function(){
        this.bulidingData.each( function( b ){
            b.roomList.each(function(room){
                this.rooms.push(new MWF.xApplication.Meeting.RoomView.Room(this,  this.roomBuildingsArea ,room, b.name ));
            }.bind(this));
        }.bind(this));
        this.resetNodeSize();
    },
    loadRooms: function( node ){
        var data = node.retrieve("data");
        data.roomList.each(function(room){
            this.rooms.push(new MWF.xApplication.Meeting.RoomView.Room(this,  this.roomBuildingsArea ,room ));
        }.bind(this));
        this.resetNodeSize();
    },
    emptyRooms : function(){
        this.rooms.each( function(room){
            room.destroy();
        });
        this.rooms = [];
    },
    reload: function(date, hours, minutes){
        if( hours == 0 && minutes==0 )return;
        if (date) this.date = date;
        if (hours) this.hours = hours;
        if (minutes) this.minutes = minutes;
        this.rooms.each(function(r){
            r.destroy();
        }.bind(this));
        this.bulidingTooltips = {};
        this.rooms = [];
        this.bulidings = [];

        this.node.destroy();

        this.load();
        this.show();
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
    recordStatus : function(){
        var id = "";
        if( this.currentBuliding )id = this.currentBuliding.retrieve("data").id;
        return {
            buildingId : id,
            date : this.selectedDate ? this.date.toString() : null,
            hours : this.hours,
            minutes : this.minutes
        };
    },
    destroy: function(){
        this.rooms.each(function(r){
            r.destroy();
        }.bind(this));
        this.rooms = [];
        this.bulidings = [];

        this.node.destroy();
    }
});


MWF.xApplication.Meeting.RoomView.Room = new Class({
    Implements: [Events],
    initialize: function(view, node, data, buildingName ){
        this.data = data;
        this.view = view;
        this.css = this.view.css;
        this.container = node;
        this.app = this.view.app;
        this.meetings = [];
        this.buildingName = buildingName;
        this.enable = this.data.available && this.data.idle;
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

        if( this.data.capacity ){
            this.titleCountNode = new Element("div.titleCountNode", {
                "styles": this.enable ? this.css.roomItemTitleCountNode : this.css.roomItemTitleCountNode_disable,
                "text" :  "("+this.data.capacity+ this.app.lp.person +")"
            }).inject(this.topNode);
        }

        this.titleTextNode = new Element("div.roomItemTitleTextNode", {
            "styles": this.enable ? this.css.roomItemTitleTextNode : this.css.roomItemTitleTextNode_disable ,
            "text" :  this.data.name
        }).inject(this.topNode);


        if( this.buildingName ){
            this.buildingTextNode = new Element("div.buildingTextNode", {
                "styles": this.enable ? this.css.roomItemBuildingTextNode : this.css.roomItemBuildingTextNode_disable,
                "text" :  this.buildingName
            }).inject(this.titleNode);
        }

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.roomItemTitleMiddleNode
        }).inject(this.titleNode);

        this.iconsNode = new Element("div.iconsNode", {
            "styles": this.css.roomItemTitleIconsNode
        }).inject(this.middleNode);

        var deviceList = this.data.device.split("#");
        deviceList.each(function(name){
            var node = new Element("div", {"styles": this.css.roomItemIconNode, "title": this.app.lp.device[name]}).inject(this.iconsNode);
            node.setStyle("background-image", "url(/x_component_Meeting/$RoomView/default/icon/device/"+  name + ( this.enable ? "" : "_disable" ) +".png)");
        }.bind(this));

        this.actionsNode = new Element("div.actionsNode", {
            "styles": this.css.roomItemTitleActionsNode
        }).inject(this.middleNode);

        this.loadActions();

        this.contentNode = new Element("div.roomItemContentNode", {"styles": this.css.roomItemContentNode}).inject(this.node);

        this.loadMeetings();
        this.loadTooltip();
    },
    loadTooltip : function(){
        this.tooltip = new MWF.xApplication.Meeting.RoomTooltip(this.app.content, this.titleNode, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300
        });
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
    },
    loadMeetings: function(){
        this.app.isMeetingViewer( function( isAll ){
            this.isMeetingViewer = isAll;
            this._loadMeetings();
        }.bind(this))
    },
    _loadMeetings: function(){
        if( this.data.meetingList.length > 0 ){
            this.data.meetingList.each(function(meeting, i){
                var m = new MWF.xApplication.Meeting.RoomView.Meeting( this.contentNode, this, meeting);
                this.meetings.push( m );
            }.bind(this));
        }else{
            this.noMeetingNode = new Element("div.noMeetingNode", {
                "styles": this.data.available ? this.css.noMeetingNode : this.css.noMeetingNode_disable,
                "text" :  this.data.available ? this.app.lp.noMeeting : this.app.lp.roomDisable
            }).inject(this.contentNode);
            this.noMeetingNode.setStyle("min-height",""+(this.view.roomNodeHeight - 160)+"px");
            this.noMeetingNode.setStyle("line-height",""+(this.view.roomNodeHeight - 160)+"px");
        }
    }
});

MWF.xApplication.Meeting.RoomView.Meeting = new Class({
    Extends :  MWF.xApplication.Meeting.MeetingArea,
    load: function(){
        var userName = layout.desktop.session.user.distinguishedName;
        var isAdmin = MWF.AC.isMeetingAdministrator();
        var available = false;
        var rejected = false;
        var confilct = false;
        if( isAdmin || this.view.isMeetingViewer || this.data.invitePersonList.contains( userName ) || this.data.applicant == userName ){
            available = true
        }
        if( this.data.rejectPersonList.contains( userName ) ){
            rejected = true;
        }
        var rBeginDate = this.view.view.date;
        var rEndDate = this.view.view.endDate;
        if( (this.beginDate >= rBeginDate && this.beginDate <= rEndDate) || (this.endDate >= rBeginDate && this.endDate <= rEndDate) ){
            confilct = true;
            this.data.confilct = true;
        }
        //if( !confilct && ( !available || rejected )){
        //    return;
        //}

        this.node = new Element("div", {"styles": this.css.meetingNode}).inject( this.container );
        if( available ){
            this.node.addEvents({
                click : function(){
                    this.openMeeting()
                }.bind(this)
            });
            if( !rejected ){
                this.node.addEvents({
                    mouseenter : function(){
                        this.node.setStyles( this.css.meetingNode_over );
                        this.subjectNode.setStyles( this.css.meetingSubjectNode_over );
                    }.bind(this),
                    mouseleave : function(){
                        this.node.setStyles( this.css.meetingNode );
                        this.subjectNode.setStyles( this.css.meetingSubjectNode );
                    }.bind(this)
                })
            }
        }

        this.colorNode = new Element("div", {"styles": this.css.meetingColorNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.meetingContentNode}).inject(this.node);

        var dateStr = this.beginDate.format(this.app.lp.dateFormatMonthDay);
        var beginTime = this.getString( this.beginDate.getHours() ) + ":" + this.getString( this.beginDate.getMinutes() );
        var endTime = this.getString( this.endDate.getHours() ) + ":" + this.getString( this.endDate.getMinutes() );
        this.timeNode = new Element("div", {
            "styles": this.css.meetingTimeNode,
            "text" : dateStr + " " + beginTime + "-" + endTime
        }).inject(this.contentNode);
        if( !available || rejected )this.timeNode.setStyle("color" , "#ccc");

        if( available ){
            this.subjectNode = new Element("div", {
                "styles": this.css.meetingSubjectNode,
                "text": this.data.subject
            }).inject(this.contentNode);
            if( rejected )this.subjectNode.setStyle("color" , "#ccc");
        }


        this.descriptionNode = new Element("div", {
            "styles": this.css.meetingDescriptionNode,
            "text" : available ? (this.data.summary || "") : this.app.lp.noPermission
        }).inject(this.contentNode);
        if( !available || rejected )this.descriptionNode.setStyle("color" , "#ccc");

        this.loadActionBar();


        switch (this.data.status){
            case "wait":
                this.colorNode.setStyles({"background-color": "#4990E2"});
                this.timeNode.setStyles({"color": "#4990E2"});
                break;
            case "processing":
                this.colorNode.setStyles({"background-color": "#66CC7F"});
                this.timeNode.setStyles({"color": "#66CC7F"});
                break;
            case "completed":
                this.colorNode.setStyles({"background-color": "#ccc"});
                this.timeNode.setStyles({"color": "#ccc"});
                break;
        }
        if (this.data.myWaitAccept){
            this.colorNode.setStyles({"background-color": "#F6A623"});
            this.timeNode.setStyles({"color": "#F6A623"});
        }
        if (!available || rejected){
            this.colorNode.setStyles({"background-color": "#eee"});
            this.timeNode.setStyles({"color": "#ccc"});
        }
        if (confilct){
            this.colorNode.setStyles({"background-color": "#FF7F7F"});
            this.timeNode.setStyles({"color": "#FF7F7F"});
        }
        this.resetNodeSize();

        if( available ){
            this.loadTooltip( true );
        }

    }
});


MWF.xApplication.Meeting.RoomView.HelpTooltip = new Class({
    Extends: MTooltips,
    _getHtml : function(){
        var html =
            "<div item='containr' style='line-height:24px;'><div style='font-size: 14px;color:#666;float:left; '>"+ this.lp.roomViewHelp +"</div></div>";
        return html;
    }
});