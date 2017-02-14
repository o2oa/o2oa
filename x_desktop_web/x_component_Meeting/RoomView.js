MWF.xDesktop.requireApp("Meeting", "MeetingView", null, false);
MWF.xApplication.Meeting.RoomView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],

    options: {
        "style": "default"
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "/x_component_Meeting/$RoomView/";
        this.cssPath = "/x_component_Meeting/$RoomView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
        this.buildings = [];
        this.date = new Date();
        this.hours = 1;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);

        this.roomArea = new Element("div", {"styles": this.css.roomArea}).inject(this.node);
        this.infoArea = new Element("div", {"styles": this.css.infoArea}).inject(this.node);

        this.roomDateArea = new Element("div", {"styles": this.css.roomDateArea}).inject(this.roomArea);
        this.roomBuildingsArea = new Element("div", {"styles": this.css.roomBuildingsArea}).inject(this.roomArea);

        this.resetNodeSize();
        this.app.addEvent("resize", this.resetNodeSize.bind(this));

        this.loadDate();
        this.loadRooms();
    },
    loadDate: function(){
        var dateText = this.date.format(this.app.lp.dateFormatAll);
        this.roomDateNode = new Element("div", {"styles": this.css.roomDateNode}).inject(this.roomDateArea);
        this.roomDateTextNode = new Element("div", {"styles": this.css.roomDateTextNode}).inject(this.roomDateNode);
        this.roomDateIconNode = new Element("div", {"styles": this.css.roomDateIconNode}).inject(this.roomDateNode);

        this.roomHourRangeNode = new Element("div", {"styles": this.css.roomHourRangeNode}).inject(this.roomDateArea);
        var html = this.app.lp.persist+" <select>" +
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
            "</select> "+this.app.lp.hour;

        this.roomDateTextNode.set("text", dateText);
        this.roomHourRangeNode.set("html", html);
        this.roomHourRangeSelect = this.roomHourRangeNode.getElement("select").setStyles(this.css.roomHourRangeSelectNode);

        this.roomHourRangeSelect.addEvent("change", function(){
            var h = this.roomHourRangeSelect.options[this.roomHourRangeSelect.selectedIndex].get("value");
            debugger;
            this.reload(this.date, h);
        }.bind(this));

        this.roomDateNode.addEvents({
            "mouseover": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_over);
                this.roomDateIconNode.setStyles(this.css.roomDateIconNode_over);
            }.bind(this),
            "mouseout": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode);
                this.roomDateIconNode.setStyles(this.css.roomDateIconNode);
            }.bind(this),
            "mousedown": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_down);
                this.roomDateIconNode.setStyles(this.css.roomDateIconNode_down);
            }.bind(this),
            "mouseup": function(){
                this.roomDateNode.setStyles(this.css.roomDateNode_over);
                this.roomDateIconNode.setStyles(this.css.roomDateIconNode_over);
            }.bind(this)
        });
        MWF.require("MWF.widget.Calendar", function(){
            new MWF.widget.Calendar(this.roomDateNode, {
                "style":"meeting",
                "isTime": true,
                "target": this.node,
                "onQueryComplate": function(e, dv, date){
                    var selectedDate = new Date.parse(dv);
                    var h = this.roomHourRangeSelect.options[this.roomHourRangeSelect.selectedIndex].get("value");
                    this.reload(selectedDate, h);
                }.bind(this)
            });
        }.bind(this));
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

        var dateSize = this.roomDateArea.getSize();
        y = y-dateSize.y;
        this.roomBuildingsArea.setStyle("height", ""+y+"px");
    },

    loadRooms: function(){
        var startTime = this.date.clone();
        startTime.set("sec", 0);
        var start = startTime.format("%Y-%m-%d %H:%M");
        startTime.increment("hour", this.hours);
        var complete = startTime.format("%Y-%m-%d %H:%M");

        this.app.actions.listBuildingByRange((start), (complete), function(json){
            this.roomJson =json.data;

            //this.roomBuildingsArea;
            this.roomJson.each(function(building){
                this.buildings.push(new MWF.xApplication.Meeting.RoomView.Building(this, building));
            }.bind(this));

            this.checkWidth();
            this.checkWidthFun = this.checkWidth.bind(this);
            this.app.addEvent("resize", this.checkWidthFun);

            this.buildings.each(function(building){
                building.loadRooms();
            }.bind(this));
        }.bind(this));
    },
    checkWidth: function(){
        var count = this.buildings.length;
        if (count){
            var min = this.buildings[0].node.getStyle("min-width").toInt();
            var size = this.roomArea.getSize();
            while ((count>0) && (size.x/count)<min) count--;
            var w = size.x/count;
            var areaW = w*(this.buildings.length);

            this.buildings.each(function(building){
                building.node.setStyle("width", ""+w+"px");
            }.bind(this));
            this.roomBuildingsArea.setStyle("width", ""+areaW+"px");
        }

    },
    reload: function(date, hours){
        if (date) this.date = date;
        if (hours) this.hours = hours;
        this.buildings.each(function(building){
            building.destroy();
        }.bind(this));
        this.buildings = [];
        if (this.currentDocument) this.currentDocument.close();

        this.app.removeEvent("resize", this.checkWidthFun);

        this.currentDocument = null;
        this.selectedItem = null;

        this.node.destroy();

        this.load();
        this.show();
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
    }
});

MWF.xApplication.Meeting.RoomView.Building = new Class({
    Implements: [Events],
    initialize: function(view, json){
        this.view = view;
        this.json = json;
        this.container = this.view.roomBuildingsArea;
        this.css = this.view.css;
        this.app = this.view.app;
        this.rooms = [];
        this.floors = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.buildingNode}).inject(this.container);
        this.titleNode = new Element("div", {"styles": this.css.buildingTitleNode}).inject(this.node);
        this.titleNameNode = new Element("div", {"styles": this.css.buildingTitleNameNode, "text": this.json.name}).inject(this.titleNode);
        this.titleAddrNode = new Element("div", {"styles": this.css.buildingTitleAddrNode, "text": this.json.address}).inject(this.titleNode);

        this.contentScrollNode = new Element("div", {"styles": this.css.buildingContentScrollNode}).inject(this.node);
        this.setContentScrollSize();
        this.setContentScrollSizeFun = this.setContentScrollSize.bind(this);
        this.app.addEvent("resize", this.setContentScrollSizeFun);
        this.contentNode = new Element("div", {"styles": this.css.buildingContentNode}).inject(this.contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentScrollNode, {"style": "view", "distance": 100, "indent": false});
        }.bind(this));

        this.titleNode.addEvent("click", function(){
            this.selected();
            //if (this.app.isManager){
            //    this.editBuilding();
            //}else{
            //    this.readBuilding();
            //}
        }.bind(this));
    },
    selected: function(){
        var openDocument = (this.view.selectedItem!=this);
        if (this.view.selectedItem) this.view.selectedItem.unSelected();
        this.titleNode.setStyles(this.css.buildingTitleNode_selected);
        this.node.setStyles(this.css.buildingNode_selected);
        this.titleNameNode.setStyles(this.css.buildingTitleNameNode_selected);
        this.titleAddrNode.setStyles(this.css.buildingTitleAddrNode_selected);
        this.view.selectedItem = this;
        if (openDocument) this.openDocBuilding();
    },
    unSelected: function(){
        this.titleNode.setStyles(this.css.buildingTitleNode);
        this.node.setStyles(this.css.buildingNode);
        this.titleNameNode.setStyles(this.css.buildingTitleNameNode);
        this.titleAddrNode.setStyles(this.css.buildingTitleAddrNode);
        this.view.selectedItem = null;
    },
    openDocBuilding: function(){
        if (this.view.currentDocument) this.view.currentDocument.close();
        var doc = new MWF.xApplication.Meeting.RoomView.Building.Document(this);
        this.view.currentDocument = doc;
    },

    setContentScrollSize: function(){
        var size = this.view.roomBuildingsArea.getSize();
        var titleSize = this.titleNode.getSize();
        var y = size.y-titleSize.y;
        this.contentScrollNode.setStyle("height", ""+y+"px");
    },
    loadRooms: function(){
        var floor;
        var contentFloor = null;

        this.json.roomList.each(function(room){
            if (floor!=room.floor){
                if (contentFloor) contentFloor.checkWidth();
                floor = room.floor;
                contentFloor = new MWF.xApplication.Meeting.RoomView.Building.Floor(this, room);
                this.floors.push(contentFloor);
            }
            this.rooms.push(new MWF.xApplication.Meeting.RoomView.Building.Room(contentFloor, room));
        }.bind(this));
        if (contentFloor) contentFloor.checkWidth();
    },
    destroy: function(){
        this.rooms.each(function(room){
            room.destroy();
        }.bind(this));
        this.floors.each(function(floor){
            floor.destroy();
        }.bind(this));

        this.app.removeEvent("resize", this.setContentScrollSizeFun);

        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.RoomView.Building.Document = new Class({
    Implements: [Events],
    initialize: function(building){
        this.building = building;
        this.view = this.building.view;
        this.json = this.building.json;
        this.container = this.view.infoArea;
        this.css = this.building.css;
        this.app = this.building.app;
        this.isEditMode = false;
        this.load();
    },
    load: function() {
        this.node = new Element("div", {"styles": this.css.buildingDocNode}).inject(this.container);
        this.actionNode = new Element("div", {"styles": this.css.buildingDocActionNode}).inject(this.node);
        this.titleNode = new Element("div", {"styles": this.css.buildingDocTitleNode, "text": this.json.name}).inject(this.node);

        this.loadContentNode();

        //this.meetingNode = new Element("div", {"styles": this.css.buildingDocMeetingNode}).inject(this.node);
        this.setMeetingNodeSize();
        this.setMeetingNodeSizeFun = this.setMeetingNodeSize.bind(this);
        this.app.addEvent("resize", this.setMeetingNodeSizeFun);

        //MWF.require("MWF.widget.ScrollBar", function(){
        //    new MWF.widget.ScrollBar(this.meetingNode, {"style": "default", "distance": 100, "indent": false});
        //}.bind(this));

        if (this.app.isManager) this.loadActions();
    },
    loadContentNode: function(){
        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        this.addrNode = new Element("div", {"styles": this.css.buildingDocAddrNode}).inject(this.contentNode);
        var addrText = this.json.address || this.app.lp.inputAddress;
        this.addrNode.set("text", addrText);
    },
    setMeetingNodeSize: function(){
        if (this.meetingNode){
            var size = this.node.getSize();
            var actionSize = this.actionNode.getSize();
            var titleSize = this.titleNode.getSize();
            var contentSize = this.contentNode.getSize();
            var y = size.y-actionSize.y-titleSize.y-contentSize.y-30;

            this.meetingNode.setStyle("height", ""+y+"px");
        }
    },
    loadActions: function(){
        this.deleteAction = new Element("div", {"styles": this.css.buildingDocDeleteActionNode}).inject(this.actionNode);
        this.saveAction = new Element("div", {"styles": this.css.buildingDocSaveActionNode}).inject(this.actionNode);
        this.saveAction.setStyle("display", "none");
        this.editAction = new Element("div", {"styles": this.css.buildingDocEditActionNode}).inject(this.actionNode);

        this.deleteAction.addEvents({
            "mouseover": function(){this.deleteAction.setStyles(this.css.buildingDocDeleteActionNode_over);}.bind(this),
            "mouseout": function(){this.deleteAction.setStyles(this.css.buildingDocDeleteActionNode);}.bind(this),
            "mousedown": function(){this.deleteAction.setStyles(this.css.buildingDocDeleteActionNode_down);}.bind(this),
            "mouseup": function(){this.deleteAction.setStyles(this.css.buildingDocDeleteActionNode_over);}.bind(this),
            "click": function(e){this.deleteDocument(e);}.bind(this)
        });
        this.saveAction.addEvents({
            "mouseover": function(){this.saveAction.setStyles(this.css.buildingDocSaveActionNode_over);}.bind(this),
            "mouseout": function(){this.saveAction.setStyles(this.css.buildingDocSaveActionNode);}.bind(this),
            "mousedown": function(){this.saveAction.setStyles(this.css.buildingDocSaveActionNode_down);}.bind(this),
            "mouseup": function(){this.saveAction.setStyles(this.css.buildingDocSaveActionNode_over);}.bind(this),
            "click": function(){this.saveDocument();}.bind(this)
        });
        this.editAction.addEvents({
            "mouseover": function(){this.editAction.setStyles(this.css.buildingDocEditActionNode_over);}.bind(this),
            "mouseout": function(){this.editAction.setStyles(this.css.buildingDocEditActionNode);}.bind(this),
            "mousedown": function(){this.editAction.setStyles(this.css.buildingDocEditActionNode_down);}.bind(this),
            "mouseup": function(){this.editAction.setStyles(this.css.buildingDocEditActionNode_over);}.bind(this),
            "click": function(){this.editDocument();}.bind(this)
        });

        this.editDocumentFun = this.editDocument.bind(this);
        this.titleNode.addEvent("click", this.editDocumentFun);
        if (this.addrNode) this.addrNode.addEvent("click", this.editDocumentFun);
    },

    editDocument: function(){
        this.saveAction.setStyle("display", "block");
        this.editAction.setStyle("display", "none");

        var name = this.titleNode.get("text");
        this.titleNode.empty();
        this.titleInput = new Element("input", {
            "styles": this.css.buildingDocTitleInputNode,
            "type": "text",
            "value": name
        }).inject(this.titleNode);
        //this.titleInput.focus()

        var addr = this.addrNode.get("text");
        //if (addr == this.app.lp.inputAddress) addr = "";
        this.addrNode.empty();
        this.addrInput = new Element("textarea", {
            "styles": this.css.buildingDocAddrInputNode,
            "text": addr
        }).inject(this.addrNode);
        this.addrInput.addEvents({
            "focus": function(){
                if (this.addrInput.get("value")==this.app.lp.inputAddress) this.addrInput.set("value", "");
            }.bind(this),
            "blur": function(){
                if (!this.addrInput.get("value")) this.addrInput.set("value", this.app.lp.inputAddress);
            }.bind(this)
        });

        this.titleNode.removeEvent("click", this.editDocumentFun);
        if (this.addrNode) this.addrNode.removeEvent("click", this.editDocumentFun);

        this.isEditMode = true;
    },
    readDocument: function(){
        this.saveAction.setStyle("display", "none");
        this.editAction.setStyle("display", "block");

        var name = this.titleInput.get("value");
        var addr = this.addrInput.get("value");
        this.titleInput.destroy();
        this.addrInput.destroy();

        this.titleNode.set("text", name);
        this.addrNode.set("text", addr);

        this.titleNode.addEvent("click", this.editDocumentFun);
        if (this.addrNode) this.addrNode.addEvent("click", this.editDocumentFun);

        this.isEditMode = false;
    },

    saveDocument: function(noNotice, callback){
        var name = this.titleInput.get("value");
        var addr = this.addrInput.get("value");
        this.app.actions.saveBuilding({"name": name, "address": addr, "id": this.json.id}, function(json){
            if (!noNotice) this.app.notice(this.app.lp.save_success, "success", this.node, {"x": "right", "y": "top"});

            this.app.actions.getBuilding(this.json.id, function(json){
                this.json.name = json.data.name;
                this.json.address = json.data.address;
                this.building.titleNameNode.set("text", this.json.name);
                this.building.titleAddrNode.set("text", this.json.address);

                if (callback) callback();
            }.bind(this));

            this.readDocument();
        }.bind(this));
    },
    deleteDocument: function(e) {
        var info = this.app.lp.delete_building;
        info = info.replace(/{name}/g, this.json.name);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.delete_building_title, info, 300, 120, function(){
            _self.remove();
            this.close();
        }, function(){
            this.close();
        });
    },
    remove: function(){
        this.app.actions.deleteBuilding(this.json.id, function(){
            this.view.reload();
        }.bind(this));
    },

    close: function(callback){
        if (this.isEditMode){
            this.saveDocument(true, function(){
                this.node.destroy();
                this.app.removeEvent("resize", this.setMeetingNodeSizeFun);
                this.view.currentDocument = null;
                MWF.release(this);
                if (callback) callback();
            }.bind(this));
        }else{
            this.node.destroy();
            this.app.removeEvent("resize", this.setMeetingNodeSizeFun);
            this.view.currentDocument = null;
            MWF.release(this);
            if (callback) callback();
        }
    }


});

MWF.xApplication.Meeting.RoomView.Building.Floor = new Class({
    Implements: [Events],
    initialize: function(building, json){
        this.building = building;
        this.view = this.building.view;
        this.json = json;
        this.container = this.building.contentNode;
        this.css = this.building.css;
        this.app = this.building.app;
        this.rooms = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.buildingContentFloorNode}).inject(this.container);
        this.checkWidthFun = this.checkWidth.bind(this);
        this.app.addEvent("resize", this.checkWidthFun);
    },
    checkWidth: function(){
        var count = this.rooms.length;
        if (count){
            var min = this.rooms[0].node.getStyle("min-width").toInt();
            var size = this.node.getSize();

            while ((count>0) && ((size.x-10)/count)-12<min) count--;
            var w = ((size.x-10)/count)-12;
            this.rooms.each(function(room){
                room.node.setStyle("width", ""+w+"px");
            }.bind(this));
        }
    },
    destroy: function(){
        this.app.removeEvent("resize", this.checkWidthFun);
        this.node.destroy();
        MWF.release(this);
    }

});

MWF.xApplication.Meeting.RoomView.Building.Room = new Class({
    Implements: [Events],
    initialize: function(floor, json){
        this.floor = floor;
        this.building = this.floor.building;
        this.view = this.floor.view;
        this.json = json;
        this.container = this.floor.node;
        this.css = this.floor.css;
        this.app = this.floor.app;
        this.floor.rooms.push(this);
        this.isBusy = (this.json.idle===false);
        this.meetings = [];
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.roomNode}).inject(this.container);
        this.titleNode = new Element("div", {"styles": this.css.roomTitleNode}).inject(this.node);

        this.capacityNode = new Element("div", {"styles": this.css.roomTitleCapacityNode, "text": this.json.capacity+this.app.lp.person}).inject(this.titleNode);
        this.inforNode = new Element("div", {"styles": this.css.roomTitleInforNode}).inject(this.titleNode);

        var node = new Element("div", {"styles": {"height": "20px"}}).inject(this.inforNode);
        this.numberNode = new Element("div", {"styles": this.css.roomTitleNumberNode, "text": (this.json.roomNumber) ? "#"+this.json.roomNumber : ""}).inject(node);
        this.nameNode = new Element("div", {"styles": this.css.roomTitleNameNode, "text": this.json.name}).inject(node);

        this.iconsNode = new Element("div", {"styles": this.css.roomTitleIconsNode}).inject(this.inforNode);

        this.contentNode = new Element("div", {"styles": this.css.roomContentNode}).inject(this.node);

        this.loadContent();

        if (this.view.selectedItem==this) this.selected();
        this.titleNode.addEvent("click", function(){
            this.selected();
        }.bind(this));
    },
    loadContent: function(){
        if (!this.json.available){
            this.node.setStyles(this.css.roomNode_disable);
            this.titleNode.setStyles(this.css.roomTitleNode_disable);
        }else{
            if (this.isBusy){
                this.node.setStyles(this.css.roomNode_busy);
                this.titleNode.setStyles(this.css.roomTitleNode_busy);
            }
        }

        var deviceList = this.json.device.split("#");
        deviceList.each(function(name){
            var node = new Element("div", {"styles": this.css.roomTitleIconNode, "title": this.app.lp.device[name]}).inject(this.iconsNode);
            node.setStyle("background-image", "url(/x_component_Meeting/$RoomView/default/icon/"+name+".png)");
        }.bind(this));

        this.json.meetingList.each(function(meeting, i){
            if (i<4) this.meetings.push(new MWF.xApplication.Meeting.RoomView.Building.Room.Meeting(this, meeting));
        }.bind(this));

        if (!this.isBusy && this.json.available){
            var node = new Element("div", {"styles": this.css.roomContentAddMeetingNode}).inject(this.node);
            node.set("text", this.app.lp.applyMeeting);
            node.addEvent("click", function(){
                this.app.addMeeting(this.view.date, this.view.date.getHours(), this.json.id);
            }.bind(this));
        }
    },

    selected: function(){
        var openDocument = (this.view.selectedItem!=this);
        if (this.view.selectedItem) this.view.selectedItem.unSelected();
        this.node.setStyles(this.css.roomNode_selected);
        this.view.selectedItem = this;
        if (openDocument) this.openDocRoom();
    },
    unSelected: function(){
        this.node.setStyles(this.css.roomNode);
        if (!this.json.available){
            this.node.setStyles(this.css.roomNode_disable);
        }else{
            if (this.isBusy){
                this.node.setStyles(this.css.roomNode_busy);
            }
        }
        this.view.selectedItem = null;
    },
    openDocRoom: function(){
        debugger;
        if (this.view.currentDocument){
            this.view.currentDocument.close(function(){
                var doc = new MWF.xApplication.Meeting.RoomView.Building.Room.Document(this);
                this.view.currentDocument = doc;
            }.bind(this));
        }else{
            var doc = new MWF.xApplication.Meeting.RoomView.Building.Room.Document(this);
            this.view.currentDocument = doc;
        }

    },
    reload: function(){
        this.node.destroy();
        this.load();
        this.floor.checkWidth();
    },

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});
MWF.xApplication.Meeting.RoomView.Building.Room.Meeting = new Class({
    initialize: function(room, data){
        this.room = room;
        this.view = this.room.view;
        this.json = this.room.json;
        this.container = this.room.contentNode;
        this.css = this.room.css;
        this.app = this.room.app;
        this.data = data;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.roomMeetingNode}).inject(this.container);
        var timeStr = "";
        var now = new Date();
        var mDate = Date.parse(this.data.startTime);
        var d = now.diff(mDate);
        if (d==0){
            timeStr = Date.parse(this.data.startTime).format("%H:%M");
        }else if (d==1){
            timeStr = this.app.lp.tomorrow;
        }else if (d==2){
            timeStr = this.app.lp.afterTomorrow;
        }else{
            var m = now.diff(mDate, "month");
            if (m==0){
                timeStr = Date.parse(this.data.startTime).format(this.app.lp.dateFormatDayOnly);
            }else if (m==1){
                timeStr = this.app.lp.nextMonth;
            }else{
                timeStr = Date.parse(this.data.startTime).format(this.app.lp.dateFormatMonthOnly);
            }
        }

        var title = timeStr+" "+this.data.subject;
        this.node.set("text", title);
        this.node.set("title", title);

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
                break;
        }

        this.node.addEvent("click", function(){this.openMeeting();}.bind(this));
    },
    openMeeting: function(){
        if (!this.document){
            if (this.view.currentMeetingDocument) this.view.currentMeetingDocument.closeDocument();
            this.document = new MWF.xApplication.Meeting.RoomView.Building.Room.Meeting.Document(this);
            this.view.currentMeetingDocument = this.document;
        }

    }
});
MWF.xApplication.Meeting.RoomView.Building.Room.Meeting.Document = new Class({
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
        var position = this.item.node.getPosition(this.item.node.getOffsetParent());

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

MWF.xApplication.Meeting.RoomView.Building.Room.Document = new Class({
    Extends: MWF.xApplication.Meeting.RoomView.Building.Document,
    Implements: [Events],
    initialize: function(room){
        this.room = room;
        this.view = this.room.view;
        this.json = this.room.json;
        this.container = this.view.infoArea;
        this.css = this.room.css;
        this.app = this.room.app;
        this.isEditMode = false;
        this.load();
    },
    loadContentNode: function(){
        this.contentNode = new Element("div", {"styles": {
            "overflow": "hidden",
            "padding": "20px",
            "border-bottom": "0px solid #999"
        }}).inject(this.node);

        this.floorNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        var titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.floor}).inject(this.floorNode);
        var contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode, "text": this.json.floor+this.app.lp.floor}).inject(this.floorNode);

        this.roomNumberNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.roomNumber}).inject(this.roomNumberNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode, "text": this.json.roomNumber}).inject(this.roomNumberNode);

        this.phoneNumberNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.phone}).inject(this.phoneNumberNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode, "text": (this.json.phoneNumber || "")}).inject(this.phoneNumberNode);

        this.deviceberNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.device}).inject(this.deviceberNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode}).inject(this.deviceberNode);
//        contentNode.set

        var deviceList = this.json.device.split("#");
        var deviceNameList = [];
        deviceList.each(function(d){deviceNameList.push(this.app.lp.device[d]);}.bind(this));
        contentNode.set("text", deviceNameList.join(" "));

        this.capacityNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.capacity}).inject(this.capacityNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode, "text": this.json.capacity+this.app.lp.person}).inject(this.capacityNode);

        this.auditorNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.auditor}).inject(this.auditorNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode}).inject(this.auditorNode);
        this.auditorNode.setStyle("display", "none");

        var explorer = {
            "actions": this.app.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.json.auditor){
            MWF.require("MWF.widget.Identity", function(){
                var _self = this;
                var person = new MWF.widget.Person({"name": this.json.auditor}, contentNode, explorer, false, null, {"style": "meeting"});
            }.bind(this));
        }


        this.availableNode = new Element("div", {"styles": this.css.roomDocItemNode}).inject(this.contentNode);
        titleNode = new Element("div", {"styles": this.css.roomDocItemTitleNode, "text": this.app.lp.roomForm.available}).inject(this.availableNode);
        contentNode = new Element("div", {"styles": this.css.roomDocItemContentNode, "text": this.json.capacity+this.app.lp.auditor}).inject(this.availableNode);
        var availableTxt = (this.json.available) ? this.app.lp.enable : this.app.lp.disable;
        contentNode.set("text", availableTxt);

        this.meetingNode = new Element("div", {"styles": this.css.buildingDocMeetingNode}).inject(this.node);
        this.loadRoomMeetings();
    },
    loadRoomMeetings: function(){
        this.app.actions.getRoom(this.json.id, function(json){
            var html = "<table width='100%' border='0' cellSpacing='0' cellPadding='0'><tr><th>"+this.app.lp.beginTime+"</th><th>"+this.app.lp.subject+"</th><th>"+this.app.lp.applyPerson+"</th></tr></table>"
            this.meetingNode.set("html", html);
            var table = this.meetingNode.getElement("table");
            var _self = this;
            json.data.meetingList.each(function(meeting){
                var tr = new Element("tr").inject(table);
                var td = new Element("td", {"text": Date.parse(meeting.startTime).format(this.app.lp.dateFormatMonthDay+" %H:%M")}).inject(tr);
                var td = new Element("td", {"text": meeting.subject}).inject(tr);
                var td = new Element("td", {"text": meeting.applicant}).inject(tr);
                tr.store("meeting", meeting);

                tr.addEvent("click", function(){
                    var mt = this.retrieve("meeting");
                    if (mt){
                        if (!_self.document){
                            if (_self.view.currentMeetingDocument) _self.view.currentMeetingDocument.closeDocument();
                            _self.data = mt
                            _self.document = new MWF.xApplication.Meeting.RoomView.Building.Room.Meeting.Document(_self);
                            _self.view.currentMeetingDocument = _self.document;
                        }
                    }
                });
            }.bind(this));

            table.setStyles(this.css.roomDocMeetingListTable);
            table.getElements("th").setStyles(this.css.roomDocMeetingListTh);
            table.getElements("td").setStyles(this.css.roomDocMeetingListTd);

        }.bind(this));
    },

    editDocument: function(){
        this.saveAction.setStyle("display", "block");
        this.editAction.setStyle("display", "none");

        var name = this.titleNode.get("text");
        this.titleNode.empty();
        this.titleInput = new Element("input", {
            "styles": this.css.buildingDocTitleInputNode,
            "type": "text",
            "value": name
        }).inject(this.titleNode);

        var node = this.floorNode.getLast().empty();
        var selectNode = new Element("select", {"styles": this.css.roomDocItemSelectNode}).inject(node);
        for (var i=-2; i<=50; i++){
            var option = new Element("option", {
                "value": i,
                "text": i+this.app.lp.floor,
                "selected": (i==this.json.floor)
            }).inject(selectNode);
        }

        node = this.roomNumberNode.getLast().empty();
        var input = new Element("input", {
            "styles": this.css.roomDocItemInputNode,
            "type": "text",
            "value": this.json.roomNumber
        }).inject(node);

        node = this.phoneNumberNode.getLast().empty();
        var input = new Element("input", {
            "styles": this.css.roomDocItemInputNode,
            "type": "text",
            "value": this.json.phoneNumber || ""
        }).inject(node);

        node = this.deviceberNode.getLast().empty();
        var deviceList = this.json.device.split("#");
        Object.each(this.app.lp.device, function(d, k){
            var check = (deviceList.indexOf(k)!=-1) ? "checked" : "";
            new Element("div", {
                "styles": {"float": "left", "width": "90px"},
                "html": "<input type=\"checkbox\" name=\"updateRoomDeviceCheckbox\" "+check+" value=\""+k+"\">"+d
            }).inject(node);
        }.bind(this));

        node = this.capacityNode.getLast().empty();
        input = new Element("input", {
            "styles": this.css.roomDocItemInputNode,
            "type": "number",
            "value": this.json.capacity
        }).inject(node);


        node = this.auditorNode.getLast().empty();
        auditorDiv = new Element("div", {"styles": this.css.roomDocItemAuditorDivNode}).inject(node);
        this.auditorNode.store("names", this.json.auditor);

        var explorer = {
            "actions": this.app.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.json.auditor){
            MWF.require("MWF.widget.Identity", function(){
                var _self = this;
                var person = new MWF.widget.Person({"name": this.json.auditor}, auditorDiv, explorer, true, function(e){
                    this.node.destroy();
                    MWF.release(this);
                    _self.auditorNode.eliminate("names");
                    _self.auditorNode.eliminate("person");
                    e.stopPropagation();
                }, {"style": "meeting"});
                this.auditorNode.store("person", person);
            }.bind(this));
        }
        var name = this.json.auditor;
        MWF.xDesktop.requireApp("Organization", "Selector.package", function(){
            auditorDiv.addEvents({
                "click": function(){
                    var options = {
                        "type": "person",
                        "names": (name) ? [name] : [],
                        "count": 1,
                        "onComplete": function(items){
                            var op = this.auditorNode.retrieve("person");
                            if (op){
                                op.node.destroy();
                                MWF.release(op);
                                this.auditorNode.eliminate("person");
                            }

                            this.auditorNode.store("names", items[0].data.name);
                            MWF.require("MWF.widget.Identity", function(){
                                var _self = this;
                                var person = new MWF.widget.Person(items[0].data, auditorDiv, explorer, true, function(e){
                                    this.node.destroy();
                                    MWF.release(this);
                                    _self.auditorNode.eliminate("names");
                                    _self.auditorNode.eliminate("person");
                                    e.stopPropagation();
                                }, {"style": "meeting"});
                                this.auditorNode.store("person", person);
                            }.bind(this));

                        }.bind(this)
                    };
                    var selector = new MWF.OrgSelector(this.app.content, options);
                }.bind(this)
            });
        }.bind(this));

        node = this.availableNode.getLast().empty();
        new Element("div", {
            "styles": {"float": "left"},
            "html": "<input type=\"radio\" name=\"updateRoomAvailableRadio\" "+((this.json.available) ? "checked" : "")+" value=\"y\">"+this.app.lp.enable
        }).inject(node);
        new Element("div", {
            "styles": {"float": "left"},
            "html": "<input type=\"radio\" name=\"updateRoomAvailableRadio\" "+((!this.json.available) ? "checked" : "")+" value=\"n\">"+this.app.lp.disable
        }).inject(node);


        this.titleNode.removeEvent("click", this.editDocumentFun);
        if (this.addrNode) this.addrNode.removeEvent("click", this.editDocumentFun);

        this.isEditMode = true;

        this.setMeetingNodeSize();
    },
    readDocument: function(){
        this.saveAction.setStyle("display", "none");
        this.editAction.setStyle("display", "block");

        var name = this.titleInput.get("value");
        this.titleInput.destroy();
        this.titleNode.set("text", name);

        var node = this.floorNode.getLast().empty();
        node.set("text", this.json.floor+this.app.lp.floor);

        node = this.roomNumberNode.getLast().empty();
        node.set("text", this.json.roomNumber);

        node = this.phoneNumberNode.getLast().empty();
        node.set("text", this.json.phoneNumber);

        node = this.deviceberNode.getLast().empty();
        var deviceList = this.json.device.split("#");
        var deviceNameList = [];
        deviceList.each(function(d){deviceNameList.push(this.app.lp.device[d]);}.bind(this));
        node.set("text", deviceNameList.join(" "));

        node = this.capacityNode.getLast().empty();
        node.set("text", this.json.capacity+this.app.lp.person);

        node = this.auditorNode.getLast().empty();
        var explorer = {
            "actions": this.app.personActions,
            "app": {
                "lp": this.app.lp
            }
        }
        if (this.json.auditor){
            MWF.require("MWF.widget.Identity", function(){
                var _self = this;
                var person = new MWF.widget.Person({"name": this.json.auditor}, node, explorer, false, null, {"style": "meeting"});
            }.bind(this));
        }

        node = this.availableNode.getLast().empty();
        var availableTxt = (this.json.available) ? this.app.lp.enable : this.app.lp.disable;
        node.set("text", availableTxt);


        this.titleNode.addEvent("click", this.editDocumentFun);
        if (this.addrNode) this.addrNode.addEvent("click", this.editDocumentFun);

        this.isEditMode = false;
    },

    saveDocument: function(noNotice, callback){
        if (this.getRoomData()){
            this.app.actions.saveRoom(this.json, function(json){
                if (!noNotice) this.app.notice(this.app.lp.save_success, "success", this.node, {"x": "right", "y": "top"});
                this.readDocument();
                this.room.reload();
                if (callback) callback();
            }.bind(this));
        }
    },
    getRoomData: function(){
        var flag = true;
        var name = this.titleInput.get("value");

        var select = this.floorNode.getElement("select");
        var floor = select.options[select.selectedIndex].value;
        var number = this.roomNumberNode.getElement("input").get("value");
        var phone = this.phoneNumberNode.getElement("input").get("value");

        var deviceList = [];
        var deviceChecks = this.deviceberNode.getElements("input");
        deviceChecks.each(function(input){
            if (input.checked) deviceList.push(input.get("value"));
        }.bind(this));
        var device = deviceList.join("#");

        var capacity = this.capacityNode.getElement("input").get("value");

        var auditor = this.auditorNode.retrieve("names", "")

        var available = true;
        var radios = this.availableNode.getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                available = (radios[i].get("value")=="y") ? true : false;
                break;
            }
        }
        if (!name){
            this.app.notice(this.app.lp.verification.inputName, "error", this.node, {"x": "right", "y": "top"});
            return false;
        }

        this.json.name = name;
        this.json.floor = floor;
        this.json.roomNumber = number;
        this.json.phoneNumber = phone;
        this.json.device = device;
        this.json.capacity = capacity;
        this.json.auditor = auditor;
        this.json.available = available;

        return true;
    },
    deleteDocument: function(e) {
        var info = this.app.lp.delete_room;
        info = info.replace(/{name}/g, this.json.name);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.delete_building_title, info, 300, 120, function(){
            _self.remove();
            this.close();
        }, function(){
            this.close();
        });
    },
    remove: function(){
        this.app.actions.deleteRoom(this.json.id, function(){
            this.view.reload();
        }.bind(this));
    },

});