MWF.require("MWF.xDesktop.UserData", null, false);
MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("Meeting", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Meeting", "MeetingView", null, false);
MWF.xApplication.Meeting.options.multitask = false;
MWF.xApplication.Meeting.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Meeting",
        "icon": "icon.png",
        "width": "1000",
        "height": "600",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.Meeting.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Meeting.LP;
        this.menuMode="show";
        this.isManager = MWF.AC.isMeetingAdministrator();
        if (!this.actions) this.actions = new MWF.xApplication.Meeting.Actions.RestActions();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadApplication: function(callback) {
        MWF.UD.getDataJson("meetingConfig", function(json){
            this.meetingConfig = json || {};
            this.createNode();
            if (!this.options.isRefresh) {
                this.maxSize(function () {
                    this.loadLayout();
                }.bind(this));
            } else {
                this.loadLayout();
            }
            if (callback) callback();
        }.bind(this));
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.content);
    },
    loadLayout: function(){
        this.topMenu = new Element("div", {"styles": this.css.topMenu}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.bottomMenu = new Element("div", {"styles": this.css.bottomMenu}).inject(this.node);
        this.loadTopMenus();
        this.loadBottomMenus();

        this.hideMenu();

        this.node.addEvent("contextmenu", function(e){
            if (this.menuMode=="show"){
                this.hideMenu();
            }else{
                this.showMenu();
            }
            e.preventDefault();
        }.bind(this));

        this.setEvent();

        if (this.meetingConfig.defaultView){
            this[this.meetingConfig.defaultView]();
        }else{
            this.toMyMeeting();
        }

    },
    setEvent: function(){
        //this.topMenu.addEvent("mouseover", function(){this.showMenu();}.bind(this));
        //this.topMenu.addEvent("mouseout", function(){this.hideMenu();}.bind(this));
        //this.bottomMenu.addEvent("mouseover", function(){this.showMenu();}.bind(this));
        //this.bottomMenu.addEvent("mouseout", function(){this.hideMenu();}.bind(this));
        this.contentNode.addEvent("click", function(){this.hideMenu();}.bind(this));
    },
    loadTopMenus: function(){
        this.createTopMenu(this.lp.myMeeting, "myMeeting", "toMyMeeting");
        this.createTopMenu(this.lp.month, "month", "toMonth");
        this.createTopMenu(this.lp.day, "day", "toDay");
        this.createTopMenu(this.lp.list, "list", "toList");
        this.createTopMenu(this.lp.room, "room", "toRoom");
    },
    createTopMenu: function(text, icon, action){
        var actionNode = new Element("div", {"styles": this.css.topMenuNode}).inject(this.topMenu);
        var actionIconNode = new Element("div", {"styles": this.css.topMenuIconNode}).inject(actionNode);
        actionIconNode.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+".png) no-repeat center center");
        var actionTextNode = new Element("div", {"styles": this.css.topMenuTextNode, "text": text}).inject(actionNode);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){this.setStyles(_self.css.topMenuNode_over);},
            "mouseout": function(){this.setStyles(_self.css.topMenuNode);},
            "mousedown": function(){this.setStyles(_self.css.topMenuNode_down);},
            "mouseup": function(){this.setStyles(_self.css.topMenuNode_over);},
            "click": function(){if (_self[action]) _self[action].apply(_self);}
        });
        return actionNode;
    },
    loadBottomMenus: function(){
        this.createBottomMenu(this.lp.addMeeting, "add", "addMeeting");
        if (this.isManager) this.createBottomMenu(this.lp.addRoom, "addRoom", "addRoom");

        var refreshNode = this.createBottomMenu(this.lp.refresh, "refresh", "refresh");
        refreshNode.setStyle("float", "right");

        var configNode = this.createBottomMenu(this.lp.config, "setup", "config");
        configNode.setStyle("float", "right");
    },
    createBottomMenu: function(text, icon, action){
        var actionNode = new Element("div", {"styles": this.css.bottomMenuNode, "title": text}).inject(this.bottomMenu);
        //var actionIconNode = new Element("div", {"styles": this.css.bottomMenuIconNode}).inject(actionNode);
        actionNode.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+".png) no-repeat center center");
        //var actionTextNode = new Element("div", {"styles": this.css.bottomMenuTextNode, "text": text}).inject(actionNode);

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){this.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+"_over.png) no-repeat center center");},
            "mouseout": function(){this.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+".png) no-repeat center center");},
            "mousedown": function(){this.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+"_down.png) no-repeat center center");},
            "mouseup": function(){this.setStyle("background", "url(/x_component_Meeting/$Main/default/icon/"+icon+"_over.png) no-repeat center center");},
            "click": function(){if (_self[action]) _self[action].apply(_self);}
        });
        return actionNode;
    },
    hideMenu: function(){
        if (!this.meetingConfig.hideMenu) this.meetingConfig.hideMenu="static";
        if (this.meetingConfig.hideMenu!="static"){
            if (this.menuMode=="show"){
                this.topMenu.set("tween", {duration: 100,  transition: "bounce:out"});
                this.bottomMenu.set("tween", {duration: 100,  transition: "bounce:out"});
                this.topMenu.tween("top", "0px", "-50px");
                this.bottomMenu.tween("bottom", "0px", "-50px");
                this.menuMode = "hide";

                if (!this.topMenuPoint){
                    this.topMenuPoint = new Element("div", {"styles": this.css.topMenuPoint}).inject(this.topMenu);
                    this.topMenuPointAction = new Element("div", {"styles": this.css.menuPointAction}).inject(this.topMenuPoint);
                    this.topMenuPointAction.addEvent("mouseover", function(){this.showMenu();}.bind(this));
                }
                if (!this.bottomMenuPoint){
                    this.bottomMenuPoint = new Element("div", {"styles": this.css.bottomMenuPoint}).inject(this.bottomMenu);
                    this.bottomMenuPointAction = new Element("div", {"styles": this.css.menuPointAction}).inject(this.bottomMenuPoint);
                    this.bottomMenuPointAction.addEvent("mouseover", function(){this.showMenu();}.bind(this));
                }
                this.topMenuPoint.setStyle("display", "block");
                this.bottomMenuPoint.setStyle("display", "block");
            }
        }
    },
    showMenu: function(){
        if (this.menuMode!="show") {
            this.topMenu.set("tween", {duration: 100, transition: "bounce:out"});
            this.bottomMenu.set("tween", {duration: 100, transition: "bounce:out"});
            this.topMenu.tween("top", "-50px", "0px");
            this.bottomMenu.tween("bottom", "-50px", "0px");
            this.menuMode = "show";

            if (this.topMenuPoint) this.topMenuPoint.setStyle("display", "none");
            if (this.bottomMenuPoint) this.bottomMenuPoint.setStyle("display", "none");
        }
    },
    hideCurrentView: function(){
        if (this.currentView){
            this.currentView.hide();
            this.currentView = null;
        }
    },
    toMyMeeting: function(){
        this.contentNode.setStyles(this.css.contentNode);
        if ((!this.myMeetingView) || this.currentView!=this.myMeetingView){
            this.hideCurrentView();
            this.getMyMeetingView(function(){
                this.myMeetingView.show();
                this.currentView = this.myMeetingView;
            }.bind(this));
            this.hideMenu();
        }
    },
    getMyMeetingView: function(callback){
        if (!this.myMeetingView){
            MWF.xDesktop.requireApp("Meeting", "MeetingView", function(){
                this.myMeetingView = new MWF.xApplication.Meeting.MeetingView(this.contentNode, this);
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    toList: function(){
        this.contentNode.setStyle("background", "#EEE");
        if ((!this.listView) || this.currentView!=this.listView){
            this.hideCurrentView();
            this.getListView(function(){
                this.listView.show();
                this.currentView = this.listView;
            }.bind(this));
            this.hideMenu();
        }
    },
    getListView: function(callback){
        if (!this.listView){
            MWF.xDesktop.requireApp("Meeting", "ListView", function(){
                this.listView = new MWF.xApplication.Meeting.ListView(this.contentNode, this);
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toMonth: function(){
        this.contentNode.setStyle("background", "#EEE");
        if ((!this.monthView) || this.currentView!=this.monthView){
            this.hideCurrentView();
            this.getMonthView(function(){
                this.monthView.show();
                this.currentView = this.monthView;
            }.bind(this));
            this.hideMenu();
        }
    },
    getMonthView: function(callback){
        if (!this.monthView){
            MWF.xDesktop.requireApp("Meeting", "MonthView", function(){
                this.monthView = new MWF.xApplication.Meeting.MonthView(this.contentNode, this);
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    toDay: function(d){
        this.contentNode.setStyle("background", "#EEE");
        if ((!this.dayView) || this.currentView!=this.dayView){
            this.hideCurrentView();
            this.getDayView(function(){
                this.dayView.show();
                this.currentView = this.dayView;
            }.bind(this), d);
            this.hideMenu();
        }
    },
    getDayView: function(callback, d){
        if (!this.dayView){
            MWF.xDesktop.requireApp("Meeting", "DayView", function(){
                this.dayView = new MWF.xApplication.Meeting.DayView(this.contentNode, this, {"date": d});
                if (callback) callback();
            }.bind(this));
        }else{
            this.dayView.toDay(d);
            if (callback) callback();
        }
    },

    toRoom: function(){
        this.contentNode.setStyle("background", "#EEE");
        if ((!this.roomView) || this.currentView!=this.roomView){
            this.hideCurrentView();
            this.getRoomView(function(){
                this.roomView.show();
                this.currentView = this.roomView;
            }.bind(this));
            this.hideMenu();
        }
    },
    getRoomView: function(callback){
        if (!this.roomView){
            MWF.xDesktop.requireApp("Meeting", "RoomView", function(){
                this.roomView = new MWF.xApplication.Meeting.RoomView(this.contentNode, this);
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }

    },

    refresh: function(){
        this.hideMenu();
        if (this.currentView) this.currentView.reload();
    },

    addMeeting: function(date, hour, room){
        MWF.UD.getPublicData("meetingConfig", function(json){
            var process = (json) ? json.process : null;
            if (process){

            }else{
                new MWF.xApplication.Meeting.Creator(this, date, hour, room);
                this.hideMenu();
            }
        }.bind(this));
    },

    config: function(){
        new MWF.xApplication.Meeting.Config(this, this.meetingConfig);
        this.hideMenu();
    },

    addRoom: function(){
        this.createAddRoomNodes();
        var createRoomFx = new Fx.Morph(this.createRoomNode, {
            "duration": "300",
            "transition": Fx.Transitions.Sine.easeOut
        });
        createRoomFx.start({"opacity": 1});
    },
    createAddRoomNodes: function(){
        this.createRoomNode = new Element("div", {"styles": this.css.createRoomNode}).inject(this.node);
        var createRoomAreaNode = new Element("div", {"styles": this.css.createRoomAreaNode}).inject(this.createRoomNode);
        var titleNode = new Element("div", {"styles": this.css.createRoomTitleNode, "text": this.lp.addRoom}).inject(createRoomAreaNode);
        var contentNode = new Element("div", {"styles": this.css.createRoomContentNode}).inject(createRoomAreaNode);
        var bottomNode = new Element("div", {"styles": this.css.createRoomBottomNode}).inject(createRoomAreaNode);

        var nameNode = new Element("div", {
            "styles": this.css.createRoomFormNode,
            "html": "<div>"+this.lp.roomForm.name+"</div><div><input type=\"text\"/></div>"
        }).inject(contentNode);
        nameNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        nameNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomNameInput = nameNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var buildingNode = new Element("div", {
            "styles": this.css.createRoomFormNode,
            "html": "<div>"+this.lp.roomForm.building+"</div><div><input type=\"text\"/></div>"
        }).inject(contentNode);
        buildingNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        buildingNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomBuildingInput = buildingNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var floorNode = new Element("div", {
            "styles": this.css.createRoomFormNode2,
            "html": "<div>"+this.lp.roomForm.floor+"</div><div></div>"
        }).inject(contentNode);
        floorNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        var node = floorNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomFloorSelect = new Element("select", {"styles": this.css.createRoomFormSelect}).inject(node);
        for (var i=-2; i<=50; i++){
            var option = new Element("option", {
                "value": i,
                "text": i+this.lp.floor,
                "selected": (i==1)
            }).inject(this.createRoomFloorSelect);
        }

        var capacityNode = new Element("div", {
            "styles": this.css.createRoomFormNode3,
            "html": "<div>"+this.lp.roomForm.capacity+"</div><div><input type=\"number\"/></div>"
        }).inject(contentNode);
        capacityNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        capacityNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomCapacityInput = capacityNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var roomNumberNode = new Element("div", {
            "styles": this.css.createRoomFormNode2,
            "html": "<div>"+this.lp.roomForm.roomNumber+"</div><div><input type=\"text\"/></div>"
        }).inject(contentNode);
        roomNumberNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        roomNumberNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomRoomNumberInput = roomNumberNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var phoneNode = new Element("div", {
            "styles": this.css.createRoomFormNode3,
            "html": "<div>"+this.lp.roomForm.phone+"</div><div><input type=\"text\"/></div>"
        }).inject(contentNode);
        phoneNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        phoneNode.getLast("div").setStyles(this.css.createRoomFormContentNode);
        this.createRoomRoomPhoneNode = phoneNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var auditorNode = new Element("div", {
            "styles": this.css.createRoomFormNode,
            "html": "<div>"+this.lp.roomForm.auditor+"</div><div></div>"
        }).inject(contentNode);
        auditorNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        this.createRoomRoomAuditorNode = auditorNode.getLast("div").setStyles(this.css.createRoomFormAuditorNode);
        auditorNode.setStyle("display", "none");
        //this.createRoomRoomAuditorInput = auditorNode.getElement("input").setStyles(this.css.createRoomFormInput);

        var deviceNode = new Element("div", {
            "styles": this.css.createRoomFormNode2,
            "html": "<div>"+this.lp.roomForm.device+"</div><div></div>"
        }).inject(contentNode);
        deviceNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        this.createRoomDeviceChecks = deviceNode.getLast("div").setStyles(this.css.createRoomFormContentNode2);
        Object.each(this.lp.device, function(d, k){
            new Element("div", {
                "styles": {"float": "left", "width": "80px"},
                "html": "<input type=\"checkbox\" name=\"createRoomDeviceCheckbox\" value=\""+k+"\">"+d
            }).inject(this.createRoomDeviceChecks);
        }.bind(this));

        var availableNode = new Element("div", {
            "styles": this.css.createRoomFormNode3,
            "html": "<div>"+this.lp.roomForm.available+"</div><div></div>"
        }).inject(contentNode);
        availableNode.getFirst("div").setStyles(this.css.createRoomFormTitleNode);
        this.createRoomAvailableChecks = availableNode.getLast("div").setStyles(this.css.createRoomFormContentNode2);
        new Element("div", {
            "styles": {"float": "left"},
            "html": "<input type=\"radio\" name=\"createRoomAvailableRadio\" checked value=\"y\">"+this.lp.enable
        }).inject(this.createRoomAvailableChecks);
        new Element("div", {
            "styles": {"float": "left"},
            "html": "<input type=\"radio\" name=\"createRoomAvailableRadio\" value=\"n\">"+this.lp.disable
        }).inject(this.createRoomAvailableChecks);


        this.createRoomCancelActionNode = new Element("div", {
            "styles": this.css.createRoomCancelActionNode,
            "text": this.lp.cancel
        }).inject(bottomNode);

        this.createRoomOkActionNode = new Element("div", {
            "styles": this.css.createRoomOkActionNode,
            "text": this.lp.save
        }).inject(bottomNode);

        this.createRoomCancelActionNode.addEvent("click", function(e){
            this.createRoomCancel(e);
        }.bind(this));
        this.createRoomOkActionNode.addEvent("click", function(e){
            this.createRoomSave(e);
        }.bind(this));

        this.setCreateRoomEvent();
    },
    setCreateRoomEvent: function(){
        this.createRoomNameInput.addEvents({
            "focus": function(){
                var errorNode = this.createRoomNameInput.retrieve("errorNode", null);
                if (errorNode){
                    errorNode.destroy();
                    this.createRoomNameInput.eliminate("errorNode");
                    this.createRoomNameInput.setStyle("border", "1px solid #999");
                }
            }.bind(this),
            "blur": function(){
                if (!this.createRoomNameInput.get("value")) this.createRoomVerification(this.createRoomNameInput, this.lp.roomForm.verification.inputName);
            }.bind(this),
        });
        this.createRoomBuildingInput.addEvents({
            "focus": function(){
                var errorNode = this.createRoomBuildingInput.retrieve("errorNode", null);
                if (errorNode){
                    errorNode.destroy();
                    this.createRoomBuildingInput.eliminate("errorNode");
                    this.createRoomBuildingInput.setStyle("border", "1px solid #999");
                }
            }.bind(this),
            "blur": function(){
                var createBuilding = false;
                var building = this.createRoomBuildingInput.retrieve("buildingId");
                if (!building){
                    var buildingName = this.createRoomBuildingInput.get("value");
                    if (buildingName) createBuilding = true;
                }
                if (!building && !createBuilding) this.createRoomVerification(this.createRoomBuildingInput, this.lp.roomForm.verification.inputBuilding);
            }.bind(this),
        });
        this.createRoomCapacityInput.addEvents({
            "focus": function(){
                var errorNode = this.createRoomCapacityInput.retrieve("errorNode", null);
                if (errorNode){
                    errorNode.destroy();
                    this.createRoomCapacityInput.eliminate("errorNode");
                    this.createRoomCapacityInput.setStyle("border", "1px solid #999");
                }
            }.bind(this),
            "blur": function(){
                if (!this.createRoomCapacityInput.get("value")) this.createRoomVerification(this.createRoomCapacityInput, this.lp.roomForm.verification.inputCapacity);
            }.bind(this),
        });

        this.createRoomBuildingInput.addEvents({
            "focus": function(){this.createRoomListBuilding();}.bind(this),
            //"blur": function(){}.bind(this),
            "change": function(){this.createRoomBuildingInput.eliminate("buildingId");}.bind(this),
            "keydown": function(e){
                //if ([13,40,38].indexOf(e.code)!=-1){
                //    if (!this.createRoomSelectBuildingNode){
                //        this.createRoomListBuilding();
                //    }
                //}
                if (e.code==13){
                    this.createRoomListBuildingHide();
                    this.createRoomListBuilding();
                }
                if (e.code==40) this.createRoomSelectBuildingNext();
                if (e.code==38) this.createRoomSelectBuildingPrev();
                if (e.code==32) this.createRoomSelectBuildingConfirm(e);
            }.bind(this)
        });


        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
            var name = this.createRoomRoomAuditorNode.retrieve("names", null);
            this.createRoomRoomAuditorNode.addEvents({
                "click": function(){
                    var options = {
                        "type": "person",
                        "names": (name) ? [name] : [],
                        "count": 1,
                        "onComplete": function(items){
                            var op = this.createRoomRoomAuditorNode.retrieve("person");
                            if (op){
                                op.node.destroy();
                                MWF.release(op);
                                this.createRoomRoomAuditorNode.eliminate("person");
                            }

                            this.createRoomRoomAuditorNode.store("names", items[0].data.name);
                            MWF.require("MWF.widget.Identity", function(){
                                var explorer = {
                                    "actions": this.personActions,
                                    "app": {
                                        "lp": this.lp
                                    }
                                }
                                var _self = this;
                                var person = new MWF.widget.Person(items[0].data, this.createRoomRoomAuditorNode, explorer, true, function(e){
                                    this.node.destroy();
                                    MWF.release(this);
                                    _self.createRoomRoomAuditorNode.eliminate("names");
                                    _self.createRoomRoomAuditorNode.eliminate("person");
                                    e.stopPropagation();
                                }, {"style": "meeting"});
                                this.createRoomRoomAuditorNode.store("person", person);
                            }.bind(this));

                        }.bind(this)
                    };
                    var selector = new MWF.OrgSelector(this.content, options);
                }.bind(this)
            });
        }.bind(this));
    },
    createRoomSelectBuildingNext: function(){
        if (this.createRoomSelectBuildingNode){
            var node=nu;;;
            if (this.createRoomSelectBuildingNode.selectedNode){
                var node = this.createRoomSelectBuildingNode.selectedNode.getNext();
                if (!node) node = this.createRoomSelectBuildingNode.getFirst();
                this.createRoomSelectBuildingNode.selectedNode.setStyle("background-color", this.createRoomSelectBuildingNode.selectedNode.retrieve("bg"));
            }else{
                node = this.createRoomSelectBuildingNode.getFirst();
            }
            if (node){
                var color = node.getStyle("background-color");
                node.store("bg", color);
                node.setStyles(this.css.createRoomBuildingSelectItem_over);
                this.createRoomSelectBuildingNode.selectedNode = node;
            }
        }
    },
    createRoomSelectBuildingPrev: function(){
        if (this.createRoomSelectBuildingNode){
            var node = null;
            if (this.createRoomSelectBuildingNode.selectedNode){
                var node = this.createRoomSelectBuildingNode.selectedNode.getPrevious();
                if (!node) node = this.createRoomSelectBuildingNode.getLast();
                this.createRoomSelectBuildingNode.selectedNode.setStyle("background-color", this.createRoomSelectBuildingNode.selectedNode.retrieve("bg"));
            }else{
                node = this.createRoomSelectBuildingNode.getLast();
            }
            if (node){
                var color = node.getStyle("background-color");
                node.store("bg", color);
                node.setStyles(this.css.createRoomBuildingSelectItem_over);
                this.createRoomSelectBuildingNode.selectedNode = node;
            }
        }
    },
    createRoomSelectBuildingConfirm: function(e){
        if (this.createRoomSelectBuildingNode.selectedNode){
            this.selectBuilding(this.createRoomSelectBuildingNode.selectedNode);
            e.preventDefault();
        }
    },

    //cancelCreateRoom: function(){
    //    this.createRoomNode.destroy();
    //},
    createRoomListBuilding: function(){
        var key = this.createRoomBuildingInput.get("value");
        this.actions.listBuildingByKey(key, function(json){
            if (json.data && json.data.length){
                this.createRoomSelectBuildingNode = new Element("div", {"styles": this.css.createRoomSelectBuildingNode}).inject(this.createRoomNode);
                this.setCreateRoomSelectBuildingNodeSize();
                this.createRoomListBuildingHideFun = this.createRoomListBuildingHide.bind(this);
                this.node.addEvent("mousedown", this.createRoomListBuildingHideFun);

                var _self = this;
                json.data.each(function(building, idx){
                    var node = new Element("div", {"styles": this.css.createRoomBuildingSelectItem}).inject(this.createRoomSelectBuildingNode);
                    var nameNode = new Element("div", {"styles": this.css.createRoomBuildingSelectItemName, "text": building.name}).inject(node);
                    var addrNode = new Element("div", {"styles": this.css.createRoomBuildingSelectItemAddr, "text": building.address}).inject(node);
                    if ((idx % 2)==1) node.setStyle("background-color", "#f1f6ff");
                    node.store("building", building.id);

                    node.addEvents({
                        "mouseover": function(e){
                            var color = this.getStyle("background-color");
                            this.store("bg", color);
                            this.setStyles(_self.css.createRoomBuildingSelectItem_over);
                        },
                        "mouseout": function(e){
                            this.setStyle("background-color", this.retrieve("bg"));
                        },
                        "mousedown": function(e){e.stopPropagation();},
                        "click": function(e){
                            _self.selectBuilding(this);
                        },
                    });

                }.bind(this));
            }
        }.bind(this));
    },
    selectBuilding: function(node){
        var id = node.retrieve("building");
        var text = node.getFirst().get("text");
        this.createRoomBuildingInput.set("value", text);
        this.createRoomBuildingInput.store("buildingId", id);

        this.createRoomListBuildingHide();

        var errorNode = this.createRoomBuildingInput.retrieve("errorNode", null);
        if (errorNode){
            errorNode.destroy();
            this.createRoomBuildingInput.eliminate("errorNode");
            this.createRoomBuildingInput.setStyle("border", "1px solid #999");
        }
    },

    setCreateRoomSelectBuildingNodeSize: function(){
        var p = this.createRoomBuildingInput.getPosition(this.createRoomBuildingInput.getOffsetParent());

        this.createRoomSelectBuildingNode.position({
            relativeTo: this.createRoomBuildingInput,
            position: 'bottomCenter',
            edge: 'upperCenter',
            offset: {x: 0- p.x, y: 0}
        });
        this.createRoomSelectBuildingNode.setStyle("left", p.x);

        var size = this.createRoomBuildingInput.getSize();
        var w = size.x-2
        this.createRoomSelectBuildingNode.setStyle("width", ""+w+"px");
    },
    createRoomListBuildingHide: function(){
        if (this.createRoomSelectBuildingNode){
            //this.removeEvent("resize", this.setCreateRoomSelectBuildingNodeSizeFun);

            this.createRoomSelectBuildingNode.destroy();
            this.createRoomSelectBuildingNode = null;

            this.node.removeEvent("mousedown", this.createRoomListBuildingHideFun);
        }
    },

    createRoomCancel: function(){
        var createRoomFx = new Fx.Morph(this.createRoomNode, {
            "duration": "300",
            "transition": Fx.Transitions.Sine.easeOut
        });
        createRoomFx.start({"opacity": 0}).chain(function(){
            this.createRoomNode.destroy();
            this.createRoomNode = null;

            this.createRoomNameInput = null;
            this.createRoomBuildingInput = null;
            this.createRoomFloorSelect = null;
            this.createRoomCapacityInput = null;
            this.createRoomRoomNumberInput = null;
            this.createRoomRoomAuditorNode = null;
            this.createRoomDeviceChecks = null;
            this.createRoomAvailableChecks = null;
            this.createRoomCancelActionNode = null;
            this.createRoomOkActionNode = null;
            this.createRoomSelectBuildingNode = null;

            delete this.createRoomNameInput;
            delete this.createRoomBuildingInput;
            delete this.createRoomFloorSelect;
            delete this.createRoomCapacityInput;
            delete this.createRoomRoomNumberInput;
            delete this.createRoomRoomAuditorNode;
            delete this.createRoomDeviceChecks;
            delete this.createRoomAvailableChecks;
            delete this.createRoomCancelActionNode;
            delete this.createRoomOkActionNode;
            delete this.createRoomSelectBuildingNode;
        }.bind(this));
    },
    createRoomSave: function(){
        this.getCreateRoomData(function(data){
            this.actions.saveRoom(data, function(json){
                this.notice(this.lp.roomForm.save_success, "success", this.node, {"x": "left", "y": "bottom"});
                this.createRoomCancel();
            }.bind(this));
        }.bind(this));
    },
    getCreateRoomData: function(callback){
        var flag = true;
        var name = this.createRoomNameInput.get("value");
        if (!name) flag = this.createRoomVerification(this.createRoomNameInput, this.lp.roomForm.verification.inputName);

        var createBuilding = false;
        var building = this.createRoomBuildingInput.retrieve("buildingId");
        if (!building){
            var buildingName = this.createRoomBuildingInput.get("value");
            if (buildingName) createBuilding = true;
        }
        if (!building && !createBuilding) flag = this.createRoomVerification(this.createRoomBuildingInput, this.lp.roomForm.verification.inputBuilding);

        var floor = this.createRoomFloorSelect.options[this.createRoomFloorSelect.selectedIndex].value;

        var capacity = this.createRoomCapacityInput.get("value");
        if (!capacity) flag = this.createRoomVerification(this.createRoomCapacityInput, this.lp.roomForm.verification.inputCapacity);

        var number = this.createRoomRoomNumberInput.get("value");
        var phoneNumber = this.createRoomRoomPhoneNode.get("value");
        var auditor = this.createRoomRoomAuditorNode.retrieve("names", "");

        var deviceList = [];
        var deviceChecks = this.createRoomDeviceChecks.getElements("input");
        deviceChecks.each(function(input){
            if (input.checked) deviceList.push(input.get("value"));
        }.bind(this));
        var device = deviceList.join("#");

        var available = true;
        var radios = this.createRoomAvailableChecks.getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                available = (radios[i].get("value")=="y") ? true : false;
                break;
            }
        }
        if (!flag) return false;

        if (createBuilding){
            this.actions.saveBuilding({"name": buildingName, "address": ""}, function(json){
                building = json.data.id;
                var roomData = {
                    "name": name,
                    "building": building,
                    "floor": floor,
                    "device": device,
                    "capacity": capacity,
                    "auditor": auditor,
                    "available": available,
                    "roomNumber": number,
                    "phoneNumber": phoneNumber
                }
                if (callback) callback(roomData);
            }.bind(this));
        }else{
            var roomData = {
                "name": name,
                "building": building,
                "floor": floor,
                "device": device,
                "capacity": capacity,
                "auditor": auditor,
                "available": available,
                "roomNumber": number,
                "phoneNumber": phoneNumber
            }
            if (callback) callback(roomData);
        }
    },
    createRoomVerification: function(node, text){
        var infoNode = new Element("div", {"styles": this.css.createRoomErrorInfoNode, "text": text}).inject(node, "after");
        node.setStyle("border", "1px solid #F00");
        node.store("errorNode", infoNode);
        return false;
    }
});

MWF.xApplication.Meeting.Creator = new Class({
    Implements: [Events],
    initialize: function(app, date, hour, room){
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.data = {};
        this.date = date || new Date();
        this.hour = hour;
        this.room = room;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.createMeetingNode}).inject(this.app.node);
        this.infoAreaNode = new Element("div", {"styles": this.css.createMeetingInfoAreaNode}).inject(this.node);
        this.contentAreaNode = new Element("div", {"styles": this.css.createMeetingContentAreaNode}).inject(this.node);

        this.loadInfo();
        this.loadContent();

        this.cancelNode.addEvents({
            "mouseover": function(){this.cancelNode.setStyles(this.css.createMeetingCancelNode_over);}.bind(this),
            "mouseout": function(){this.cancelNode.setStyles(this.css.createMeetingCancelNode);}.bind(this),
            "mousedown": function(){this.cancelNode.setStyles(this.css.createMeetingCancelNode_down);}.bind(this),
            "mouseup": function(){this.cancelNode.setStyles(this.css.createMeetingCancelNode_over);}.bind(this),
            "click": function(e){this.cancelCreateMeeting(e);}.bind(this)
        });
        this.saveNode.addEvents({
            "mouseover": function(){this.saveNode.setStyles(this.css.createMeetingSaveNode_over);}.bind(this),
            "mouseout": function(){this.saveNode.setStyles(this.css.createMeetingSaveNode);}.bind(this),
            "mousedown": function(){this.saveNode.setStyles(this.css.createMeetingSaveNode_down);}.bind(this),
            "mouseup": function(){this.saveNode.setStyles(this.css.createMeetingSaveNode_over);}.bind(this),
            "click": function(e){this.saveCreateMeeting(e);}.bind(this)
        });
        this.show();
    },

    cancelCreateMeeting: function(e){
        debugger;
        if (this.data.id){
            this.save(function(){
                if (this.setDescriptionHeightFun) this.app.removeEvent("resize", this.setDescriptionHeightFun);
                this.app.currentView.reload();
                this.hide();
            }.bind(this));
        }else{
            var _self = this;
            this.app.confirm("warn", e, this.lp.cancel_createMeeting_title, this.lp.cancel_createMeeting, 300, 120, function(){
                if (_self.setDescriptionHeightFun) _self.app.removeEvent("resize", _self.setDescriptionHeightFun);
                _self.hide();
                this.close();
            }, function(){
                this.close();
            });
        }
    },
    getSaveData: function(){
        var subject = this.subjectInputNode.get("value");
        if (subject==this.lp.meetingSubject) subject = "";
        var description = this.descriptionInput.get("value");
        if (description==this.lp.meetingDescription) description = "";
        var room = this.roomId;
        var d = this.dateInput.get("value");
        var bh = this.beginHourSelect.options[this.beginHourSelect.selectedIndex].value;
        var bm = this.beginMinuteSelect.options[this.beginMinuteSelect.selectedIndex].value;
        var eh = this.endHourSelect.options[this.endHourSelect.selectedIndex].value;
        var em = this.endMinuteSelect.options[this.endMinuteSelect.selectedIndex].value;
        var startTime = d+" "+bh+":"+bm+":0";
        var completedTime = d+" "+eh+":"+em+":0";
        var invitePersonList = this.invitePersonNames;

        var startTimeDate = Date.parse(startTime);
        var completedTimeDate = Date.parse(completedTime);

        this.data.subject = subject;
        this.data.description = description;
        this.data.room = room;
        this.data.startTime = startTime;
        this.data.completedTime = completedTime;
        this.data.startTimeDate = startTimeDate;
        this.data.completedTimeDate = completedTimeDate;
        this.data.invitePersonList = invitePersonList;
    },
    save: function(callback){
        this.getSaveData();
        var now = new Date();
        var errorText = "";
        if (!this.data.subject) errorText +=this.lp.meeting_input_subject_error;
        if (!this.data.room) errorText +=this.lp.meeting_input_room_error;
        if (!this.data.invitePersonList.length) errorText +=this.lp.meeting_input_person_error;
        if (this.data.startTimeDate.diff(this.data.completedTimeDate, "minute")<1) errorText +=this.lp.meeting_input_time_error;
        if (now.diff(this.data.startTimeDate, "minute")<0) errorText +=this.lp.meeting_input_date_error;

        if (errorText){
            this.app.notice(this.lp.meeting_input_error+errorText, "error", this.app.content, {"x": "left", "y": "top"});
            return false;
        }

        this.app.actions.saveMeeting(this.data, function(json){
            this.data.id = json.data.id
            if (callback) callback();
        }.bind(this));
    },
    saveCreateMeeting: function(){
        this.save(function(){
            this.app.notice(this.lp.meeting_saveSuccess, "success", this.app.content, {"x": "right", "y": "top"});
            if (!this.attachmentNode){
                this.loadAttachment();
            }
        }.bind(this));
    },

    loadInfo: function(){
        var infoHeadNode = new Element("div", {"styles": this.css.createMeetingInfoHeadNode}).inject(this.infoAreaNode);
        this.cancelNode = new Element("div", {"styles": this.css.createMeetingCancelNode}).inject(infoHeadNode);
        titleNode = new Element("div", {"styles": this.css.createMeetingInfoTitleNode}).inject(infoHeadNode);
        var text = this.lp.meetingApply.replace(/{person}/g, this.app.desktop.session.user.name);
        titleNode.set("text", text);

        this.infoContentNode = new Element("div", {"styles": this.css.createMeetingInfoContentNode}).inject(this.infoAreaNode);
        this.loadBeginDate();
        this.loadBeginTime();
        this.loadEndTime();
        this.loadSelectRoom();
        this.loadInvite();
    },

    loadContent: function(){
        this.contentHeadNode = new Element("div", {"styles": this.css.createMeetingContentHeadNode}).inject(this.contentAreaNode);
        this.saveNode = new Element("div", {"styles": this.css.createMeetingSaveNode}).inject(this.contentHeadNode);
        this.subjectNode = new Element("div", {"styles": this.css.createMeetingSubjectNode}).inject(this.contentHeadNode);
        this.subjectInputNode = new Element("input", {
            "styles": this.css.createMeetingSubjectInputNode,
            "type": "text",
            "value": this.lp.meetingSubject
        }).inject(this.subjectNode);

        var descriptionNode = new Element("div", {"styles": this.css.createMeetingDescriptionNode}).inject(this.contentAreaNode);
        var descriptionAreaNode = new Element("div", {"styles": this.css.createMeetingDescriptionAreaNode}).inject(descriptionNode);
        this.descriptionInput = new Element("textarea", {"styles": this.css.createMeetingDescriptionInputNode}).inject(descriptionAreaNode);
        this.descriptionInput.set("value", this.lp.meetingDescription);

        this.setDescriptionHeight();
        this.setDescriptionHeightFun = this.setDescriptionHeight.bind(this);
        this.app.addEvent("resize", this.setDescriptionHeightFun);

        this.subjectInputNode.addEvents({
            "focus": function(){
                if (this.subjectInputNode.get("value")==this.lp.meetingSubject) this.subjectInputNode.set("value", "");
            }.bind(this),
            "blur": function(){
                if (!this.subjectInputNode.get("value")) this.subjectInputNode.set("value", this.lp.meetingSubject);
            }.bind(this)
        });
        this.descriptionInput.addEvents({
            "focus": function(){
                if (this.descriptionInput.get("value")==this.lp.meetingDescription) this.descriptionInput.set("value", "");
            }.bind(this),
            "blur": function(){
                if (!this.descriptionInput.get("value")) this.descriptionInput.set("value", this.lp.meetingDescription);
            }.bind(this)
        });
    },
    loadAttachment: function(){
        this.attachmentNode = new Element("div", {"styles": this.css.createMeetingAttachmentNode}).inject(this.contentHeadNode, "after");
        var attachmentTitleNode = new Element("div", {"styles": this.css.createMeetingAttachmentTitleNode}).inject(this.attachmentNode);
        attachmentTitleNode.set("text", this.lp.meetingAttachment);
        var attachmentContentNode = new Element("div", {"styles": this.css.createMeetingAttachmentContentNode}).inject(this.attachmentNode);
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.AttachmentController(attachmentContentNode, this, {"size": "min", "isSizeChange": false, "isReplace": false});
            this.attachmentController.load();
        }.bind(this));
    },
    uploadAttachment: function(e, node){
        if (!this.uploadFileAreaNode){
            this.createUploadFileNode();
        }
        this.fileUploadNode.click();
    },

    createUploadFileNode: function(){
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function(){

            var files = this.fileUploadNode.files;
            if (files.length){

                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);

                    var formData = new FormData();
                    formData.append('file', file);
                    //formData.append('folder', folderId);

                    this.app.actions.addAttachment(function(o, text){
                        debugger;
                        if (o.id){
                            this.app.actions.getAttachment(o.id, function(json){
                                if (json.data) this.attachmentController.addAttachment(json.data);
                                this.attachmentController.checkActions();
                            }.bind(this))
                        }
                        this.attachmentController.checkActions();
                    }.bind(this), null, formData, this.data.id, file);
                }

            }
        }.bind(this));
    },

    deleteAttachments: function(e, node, attachments){
        var names = [];
        attachments.each(function(attachment){
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        this.app.confirm("warn", e, this.lp.deleteAttachmentTitle, this.lp.deleteAttachment+"( "+names.join(", ")+" )", 300, 120, function(){
            while (attachments.length){
                attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    deleteAttachment: function(attachment){
        this.app.actions.deleteFile(attachment.data.id, function(josn){
            this.attachmentController.removeAttachment(attachment);
            this.attachmentController.checkActions();
        }.bind(this));
    },
    downloadAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFileDownload(att.data.id);
        }.bind(this));
    },
    openAttachment: function(e, node, attachments){
        attachments.each(function(att){
            this.app.actions.getFile(att.data.id);
        }.bind(this));
    },
    getAttachmentUrl: function(attachment, callback){
        this.app.actions.getFileUrl(attachment.data.id, callback);
    },

    setDescriptionHeight: function(){
        var size = this.node.getSize();
        var y = size.y-190;
        this.descriptionInput.setStyle("height", ""+y+"px");
    },

    loadBeginDate: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.infoContentNode);
        var titleNode = new Element("div", {"styles": this.css.createMeetingInfoItemTitleNode, "text": this.lp.beginDate}).inject(lineNode);
        var editNode = new Element("div", {
            "styles": this.css.createMeetingInfoItemEditNode,
            "html": "<input type=\"rext\" readonly />"
        }).inject(lineNode);
        this.dateInput = editNode.getFirst();
        this.dateInput.setStyles(this.css.createMeetingInfoItemInputNode);
        this.dateInput.set("value", this.date.format("%Y-%m-%d"));
        var downNode = new Element("div", {"styles": this.css.createMeetingInfoItemDownNode}).inject(editNode);

        MWF.require("MWF.widget.Calendar", function(){
            new MWF.widget.Calendar(this.dateInput, {"style":"meeting", "target": this.node});
        }.bind(this));
        downNode.addEvent("click", function(){
            this.dateInput.focus();
        }.bind(this));
    },
    loadBeginTime: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.infoContentNode);
        var titleNode = new Element("div", {"styles": this.css.createMeetingInfoItemTitleNode, "text": this.lp.beginTime}).inject(lineNode);
        var editNode = new Element("div", {"styles": this.css.createMeetingInfoItemEditNode}).inject(lineNode);
        this.beginHourSelect = new Element("select", {"styles": this.css.createMeetingInfoItemSelectNode}).inject(editNode);
        this.beginMinuteSelect = new Element("select", {"styles": this.css.createMeetingInfoItemSelectNode}).inject(editNode);

        var hour = this.hour;
        if (!hour){
            var now = (new Date()).increment("hour", 1);
            var hour = now.getHours();
        }

        for (i=0; i<24; i++){
            var op = new Element("option", {
                "value": i,
                "text": i
            }).inject(this.beginHourSelect);
            if (i==hour) op.set("selected", true);
        }
        for (i=0; i<60; i = i+5){
            var op = new Element("option", {
                "value": i,
                "text": i
            }).inject(this.beginMinuteSelect);
            if (i==0) op.set("selected", true);
        }
    },
    loadEndTime: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.infoContentNode);
        var titleNode = new Element("div", {"styles": this.css.createMeetingInfoItemTitleNode, "text": this.lp.endTime}).inject(lineNode);
        var editNode = new Element("div", {"styles": this.css.createMeetingInfoItemEditNode}).inject(lineNode);
        this.endHourSelect = new Element("select", {"styles": this.css.createMeetingInfoItemSelectNode}).inject(editNode);
        this.endMinuteSelect = new Element("select", {"styles": this.css.createMeetingInfoItemSelectNode}).inject(editNode);

        var hour = this.hour;
        if (!hour){
            var now = (new Date()).increment("hour", 1);
            var hour = now.getHours();
        }
        hour++;


        for (i=0; i<24; i++){
            var op = new Element("option", {
                "value": i,
                "text": i
            }).inject(this.endHourSelect);
            if (i==hour) op.set("selected", true);
        }
        for (i=0; i<60; i = i+5){
            var op = new Element("option", {
                "value": i,
                "text": i
            }).inject(this.endMinuteSelect);
            if (i==0) op.set("selected", true);
        }
    },

    loadSelectRoom: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.infoContentNode);
        var titleNode = new Element("div", {"styles": this.css.createMeetingInfoItemTitleNode, "text": this.lp.selectRoom}).inject(lineNode);
        var editNode = new Element("div", {
            "styles": this.css.createMeetingInfoItemEditNode,
            "html": "<div></div>"
        }).inject(lineNode);
        this.roomInput = editNode.getFirst();
        this.roomInput.setStyles(this.css.createMeetingInfoItemDivNode);

        var downNode = new Element("div", {"styles": this.css.createMeetingInfoItemDownNode}).inject(editNode);

        this.roomInput.addEvents({
            "click": function(e){this.selectRooms();}.bind(this)
        });
        downNode.addEvent("click", function(){
            this.selectRooms();
        }.bind(this));

        if (this.room){
            this.app.actions.getRoom(this.room, function(json){
                this.app.actions.getBuilding(json.data.building, function(bjson){
                    this.roomId = this.room;
                    this.roomInput.set("text", json.data.name+" ("+bjson.data.name+")");
                }.bind(this));
            }.bind(this));

        }
    },

    selectRooms: function(){
        this.createRoomNode(function(){
            this.loadSelectRooms();
            this.selectRoomNode.setStyle("display", "block");
            this.hideRoomNodeFun = this.hideRoomNode.bind(this);
            document.body.addEvent("mousedown", this.hideRoomNodeFun);

            //var p = this.selectRoomNode.getPosition(this.selectRoomNode.getOffsetParent());
            this.selectRoomNode.position({
                relativeTo: this.roomInput,
                position: 'bottomLeft',
                edge: 'upperLeft',
                offset: {x: 0, y: 0}
            });
            var size = this.roomInput.getSize();
            var w = size.x-2
            this.selectRoomNode.setStyle("width", ""+w+"px");
        }.bind(this));
    },

    createRoomNode: function(callback){
        if (!this.selectRoomNode){
            this.selectRoomNode = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomNode}).inject(this.node);
            this.selectRoomNode.addEvent("mousedown", function(e){e.stopPropagation();});
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadSelectRooms: function(){
        var d = this.dateInput.get("value");
        var bh = this.beginHourSelect.options[this.beginHourSelect.selectedIndex].value;
        var bm = this.beginMinuteSelect.options[this.beginMinuteSelect.selectedIndex].value;
        var eh = this.endHourSelect.options[this.endHourSelect.selectedIndex].value;
        var em = this.endMinuteSelect.options[this.endMinuteSelect.selectedIndex].value;
        var start = d+" "+bh+":"+bm;
        var completed = d+" "+eh+":"+em;

        this.app.actions.listBuildingByRange(start, completed, function(json){
            json.data.each(function(building){
                var node = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1Node}).inject(this.selectRoomNode);
                var nodeName = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1NameNode, "text": building.name}).inject(node);
                var nodeAddr = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1AddrNode, "text": building.address}).inject(node);


                building.roomList.each(function(room, i){
                    if (room.available) this.createRoomSelectNode(room, i, building);
                }.bind(this));

            }.bind(this));
        }.bind(this));
    },

    createRoomSelectNode: function(room, i, building){
        var roomNode = new Element("div", {"styles": this.css.roomTitleNode}).inject(this.selectRoomNode);
        var capacityNode = new Element("div", {"styles": this.css.roomTitleCapacityNode, "text": room.capacity+this.lp.person}).inject(roomNode);
        var inforNode = new Element("div", {"styles": this.css.roomTitleInforNode}).inject(roomNode);

        var node = new Element("div", {"styles": {"height": "20px"}}).inject(inforNode);
        var numberNode = new Element("div", {"styles": this.css.roomTitleNumberNode, "text": (room.roomNumber) ? "#"+room.roomNumber : ""}).inject(node);
        var nameNode = new Element("div", {"styles": this.css.roomTitleNameNode, "text": room.name}).inject(node);

        var iconsNode = new Element("div", {"styles": this.css.roomTitleIconsNode}).inject(inforNode);

        var deviceList = room.device.split("#");
        deviceList.each(function(name){
            var node = new Element("div", {"styles": this.css.roomTitleIconNode, "title": this.lp.device[name]}).inject(iconsNode);
            node.setStyle("background-image", "url(/x_component_Meeting/$RoomView/default/icon/"+name+".png)");
        }.bind(this));
        if ((i % 2)!=0) roomNode.setStyle("background-color", "#f4f8ff");
        roomNode.store("room", room);

        var _self = this;
        if (room.idle){
            roomNode.addEvents({
                "mouseover": function(){
                    var color = roomNode.getStyle("background-color");
                    this.store("bgcolor", color);
                    this.setStyle("background-color", "#e4edfc");
                },
                "mouseout": function(){
                    var color = this.retrieve("bgcolor", "#FFF");
                    this.setStyle("background-color", color);
                },
                "click": function(){
                    var roomData = this.retrieve("room");
                    _self.roomId = roomData.id;
                    //roomNode.inject(_self.roomInput);
                    _self.roomInput.set("text", roomData.name+" ("+building.name+")");
                    _self.hideRoomNode();
                }
            });
        }else{
            roomNode.setStyle("background-color", "#fff6f6");
            var disabledNode = new Element("div", {"styles": this.css.roomTitleDisabledIconNode}).inject(roomNode);
        }

    },


    hideRoomNode: function(){
        this.selectRoomNode.empty();
        this.selectRoomNode.setStyle("display", "none");
        document.body.removeEvent("mousedown", this.hideRoomNodeFun);
    },

    setCreateRoomSelectBuildingNodeSize: function(){
        var p = this.createRoomBuildingInput.getPosition(this.createRoomBuildingInput.getOffsetParent());

        this.createRoomSelectBuildingNode.position({
            relativeTo: this.createRoomBuildingInput,
            position: 'bottomCenter',
            edge: 'upperCenter',
            offset: {x: 0, y: 0}
        });
        this.createRoomSelectBuildingNode.setStyle("left", p.x);

        var size = this.createRoomBuildingInput.getSize();
        var w = size.x-2
        this.createRoomSelectBuildingNode.setStyle("width", ""+w+"px");
    },

    loadInvite: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.infoContentNode);
        var titleNode = new Element("div", {"styles": this.css.createMeetingInfoItemTitleNode, "text": this.lp.invitePerson}).inject(lineNode);
        var editNode = new Element("div", {
            "styles": this.css.createMeetingInfoItemEditNode,
            "html": "<div></div>"
        }).inject(lineNode);
        this.inviteInput = editNode.getFirst();
        this.inviteInput.setStyles(this.css.createMeetingInfoItemDivNode);

        this.invitePersons = [];
        this.invitePersonNames = [];
        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
            this.inviteInput.addEvents({
                "click": function(){
                    var options = {
                        "type": "person",
                        "names": this.invitePersonNames,
                        "count": 0,
                        "onComplete": function(items){
                            this.invitePersons.each(function(op){
                                op.node.destroy();
                                MWF.release(op);
                            }.bind(this));
                            this.invitePersons = [];
                            this.invitePersonNames = [];

                            var explorer = {
                                "actions": this.app.personActions,
                                "app": {
                                    "lp": this.lp
                                }
                            }
                            MWF.require("MWF.widget.Identity", function(){
                                items.each(function(item){
                                    var _self = this;
                                    var person = new MWF.widget.Person(item.data, this.inviteInput, explorer, false, null, {"style": "room"});
                                    this.invitePersonNames.push(item.data.name);
                                    this.invitePersons.push(person);
                                }.bind(this));
                            }.bind(this));
                        }.bind(this)
                    };
                    var selector = new MWF.OrgSelector(this.app.content, options);
                }.bind(this)
            });
        }.bind(this));
    },


    show: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Sine.easeOut
        });
        fx.start({"opacity": 1});
    },
    hide: function(){
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Sine.easeOut
        });
        fx.start({"opacity": 0}).chain(function(){
            this.node.destroy();
            MWF.release(this);
        }.bind(this));
    }
});

MWF.xApplication.Meeting.Config = new Class({
    Implements: [Events],
    initialize: function(app){
        this.app = app;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.configData = this.app.meetingConfig || {};

        MWF.UD.getPublicData("meetingConfig", function(json){
            var jsonData = json || {};
            if (jsonData.process){
                this.configData.process = jsonData.process;
            }else{
                this.configData.process = "";
            }
            this.load();
        }.bind(this));
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.configNode}).inject(this.app.node);
        this.contentNode = new Element("div", {"styles": this.css.configContentNode}).inject(this.node);
        var html = "<div class='line'><div class='configTitle'>"+this.lp.config.navi+
            "</div><div><input type='checkbox' "+((this.configData.hideMenu!="static") ? "checked" : "")+" value='auto' />"+this.lp.config.autoHide+"</div></div>" +

            "<div class='line'><div class='configTitle'>"+this.lp.config["default"]+"</div><div>" +
            "<input type='radio' name='configSelectDefaultView' "+(((!this.configData.defaultView) || this.configData.defaultView=="toMyMeeting") ? "checked" : "")+" value='toMyMeeting'>"+this.lp.myMeeting+
            "<input type='radio' name='configSelectDefaultView' "+((this.configData.defaultView=="toMonth") ? "checked" : "")+" value='toMonth'>"+this.lp.month+
            "<input type='radio' name='configSelectDefaultView' "+((this.configData.defaultView=="toDay") ? "checked" : "")+" value='toDay'>"+this.lp.day+
            "<input type='radio' name='configSelectDefaultView' "+((this.configData.defaultView=="toList") ? "checked" : "")+" value='toList'>"+this.lp.list+
            "<input type='radio' name='configSelectDefaultView' "+((this.configData.defaultView=="toRoom") ? "checked" : "")+" value='toRoom'>"+this.lp.room+"</div></div>" +

            "<div class='line'><div class='configTitle'>"+this.lp.config.legend+"</div><div style='overflow: hidden; height: 50px'>" +

            "<div style='float: left; width: 80px; height: 20px;margin-right:10px'>" +
            "<div style='float: left; width: 10px; height: 20px; background-color:#2968cf'></div>" +
            "<div style='margin-left:15px; line-height:20px; height:20px'>"+this.lp.config.wait+"</div></div>" +

            "<div style='float: left; width: 80px; height: 20px;margin-right:10px'>" +
            "<div style='float: left; width: 10px; height: 20px; background-color:#18da14'></div>" +
            "<div style='margin-left:15px; line-height:20px; height:20px'>"+this.lp.config.progress+"</div></div>" +

            "<div style='float: left; width: 80px; height: 20px;margin-right:10px'>" +
            "<div style='float: left; width: 10px; height: 20px; background-color:#ecd034'></div>" +
            "<div style='margin-left:15px; line-height:20px; height:20px'>"+this.lp.config.invite+"</div></div>" +

            "<div style='float: left; width: 80px; height: 20px;margin-right:10px'>" +
            "<div style='float: left; width: 10px; height: 20px; background-color:#555555'></div>" +
            "<div style='margin-left:15px; line-height:20px; height:20px'>"+this.lp.config.completed+"</div></div>" +

            "</div></div>"+
            "<div class='line'><div class='configTitle'>"+this.lp.config.applyProcess+"</div><div></div></div>";;
        this.contentNode.set("html", html);
        this.contentNode.getElements("div.line").setStyles(this.css.configContentLine);
        this.contentNode.getElements("div.configTitle").setStyles(this.css.configTitleDiv);

        this.processNode = this.contentNode.getLast("div").getLast("div");
        if (this.app.isManager && false){ //@todo no process
            this.createApplicationSelect();

        }else{
            this.contentNode.getLast("div").setStyle("display", "none");
        }


        this.actionNode = new Element("div", {"styles": this.css.configActionNode}).inject(this.node);
        this.cancelNode = new Element("div", {"styles": this.css.configActionCancelNode, "text": this.app.lp.cancel}).inject(this.actionNode);
        this.saveNode = new Element("div", {"styles": this.css.configActionSaveNode, "text": this.app.lp.save}).inject(this.actionNode);

        this.cancelNode.addEvent("click", this.hide.bind(this));
        this.saveNode.addEvent("click", this.save.bind(this));

        this.node.addEvent("mousedown", function(e){e.stopPropagation();}.bind(this));

        this.show();
    },
    createApplicationSelect: function(){
        MWF.xDesktop.requireApp("process.ApplicationExplorer", "Actions.RestActions", function(){
            this.appActions = new MWF.xApplication.process.ApplicationExplorer.Actions.RestActions();

            this.getCurrentProcess(function(){

                this.applicationSelect = new Element("select").inject(this.processNode);
                var op = new Element("option", {"text": "", "value": "", "selected": true}).inject(this.applicationSelect);
                this.appActions.listApplication("", function(json){
                    json.data.each(function(app){
                        var op = new Element("option", {
                            "text": app.name,
                            "value": app.id,
                            "selected": (this.currentProcess) ? (this.currentProcess.application==app.id) : false
                        }).inject(this.applicationSelect);
                    }.bind(this));

                    if (this.applicationSelect.selectedIndex && this.applicationSelect.selectedIndex!=0){
                        var id = this.applicationSelect.options[this.applicationSelect.selectedIndex].get("value");
                        if (id) this.createProcessSelect(id);
                    }

                    this.applicationSelect.addEvent("change", function(){
                        var id = this.applicationSelect.options[this.applicationSelect.selectedIndex].get("value");
                        this.createProcessSelect(id);
                    }.bind(this));
                }.bind(this))

            }.bind(this));
        }.bind(this));
    },
    createProcessSelect: function(id){
        if (id){
            if (!this.processSelect){
                this.processSelect = new Element("select").inject(this.processNode);
            }else{
                this.processSelect.empty();
            }
            var op = new Element("option", {"text": "", "value": "", "selected": true}).inject(this.processSelect);
            this.appActions.listProcess(id, function(json){
                json.data.each(function(process){
                    var op = new Element("option", {
                        "text": process.name,
                        "value": process.id,
                        "selected": (this.currentProcess) ? (this.currentProcess.id==process.id) : false
                    }).inject(this.processSelect);
                }.bind(this));
            }.bind(this));
        }else{
            if (this.processSelect){
                this.processSelect.destroy();
                this.processSelect = null;
            }
        }
    },
    getCurrentProcess: function(callback){
        if (this.configData.process){
            this.appActions.getProcess(this.configData.process, function(json){
                this.currentProcess = json.data;
                if (callback) callback();
            }.bind(this));
        }else{
            this.currentProcess = null;
            if (callback) callback();
        }
    },

    save: function(){
        var hideMenu = "auto";
        var defaultView = "toMyMeeting";
        var process = "";
        var node = this.contentNode.getFirst("div");
        var hideMenuNode = node.getElement("input");
        if (hideMenuNode) if (!hideMenuNode.checked) hideMenu = "static";

        node = node.getNext();
        var viewNodes = node.getElements("input");
        for (var i=0; i<viewNodes.length; i++){
            if (viewNodes[i].checked){
                defaultView = viewNodes[i].get("value");
                break;
            }
        }
        if (this.processSelect){
            process = this.processSelect.options[this.processSelect.selectedIndex].get("value");
            MWF.UD.putPublicData("meetingConfig", {"process": process});
        }

        MWF.UD.putData("meetingConfig", {"hideMenu": hideMenu, "defaultView": defaultView});

        this.hide();
    },
    show: function(){
        this.node.setStyles(this.css.configNode);
        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            "opacity": 1
        }).chain(function(){
            this.hideFun = this.hide.bind(this);
            this.app.content.addEvent("mousedown", this.hideFun);
        }.bind(this));
    },
    hide: function(){
        this.node.destroy();
        this.app.content.removeEvent("mousedown", this.hideFun);
        MWF.release(this);
    }

});