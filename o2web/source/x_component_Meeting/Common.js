MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.require("MWF.widget.AttachmentController",null,false);

MWF.xApplication.Meeting = MWF.xApplication.Meeting || {};

MWF.xApplication.Meeting.BuildingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "800",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": false,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        this.formTopTextNode.set( "text", this.lp.editBuilding );
        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='name'></td>" +
            "    <td styles='formTableValue' item='name' colspan='3' ></td></tr>" +
            "<tr><td styles='formTableTitle' lable='address'></td>" +
            "    <td styles='formTableValue' item='address' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle'></td>" +
            "    <td styles='formTableValue' colspan='3' style='padding-top: 30px;'>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew ) ? "float:left;" : "float:right;margin-right:15px;")+"'></div>"+
            "   </td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                itemTemplate: {
                    name: { text : this.lp.name, notEmpty : true },
                    address: { text : this.lp.address },
                    saveAction : { type : "button", className : "inputOkButton", value : this.lp.save, event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "button", className : "inputCancelButton", value : this.lp.delete , event : {
                        click : function( item, ev ){ this.removeBuilding(ev); }.bind(this)
                    } },
                    cancelAction : { type : "button", className : "inputCancelButton", value : this.lp.close , event : {
                        click : function(){ this.close(); }.bind(this)
                    } }
                }
            }, this.app);
            this.form.load();

        }.bind(this), true);
    },
    save: function(){
        var data = this.form.getResult(true,null,true,false,true);
        this.actions.saveBuilding( data, function(json){
            this.app.notice(this.lp.save_success, "success");
            var view = this.view;
            this.close();
            view.reload();
        }.bind(this));
    },

    removeBuilding: function(e) {
        var info = this.app.lp.delete_building;
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
        this.app.actions.deleteBuilding(this.data.id, function(){
            view.reload();
        }.bind(this));
    }
});

MWF.xApplication.Meeting.BuildingTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        this.loadActionBar();
        if(callback)callback();
    },
    _getHtml : function(){
        var data = this.data;

        var html =
            "<div item='containr' style='height:16px;line-height:16px;'><div style='font-size: 14px;color:#666;float:left; '>"+ (data.address ? data.address : this.lp.noAddress) +"</div></div>";
        return html;
    },
    loadActionBar : function(){
        if( MWF.AC.isMeetingAdministrator() ){
            //this.actionBar = new Element("div", {
            //    "styles": this.css.actionBar
            //}).inject(this.node);
            var container = this.node.getElement("[item='containr']");

            this.editAction = new Element("div", {
                styles: this.css.action_edit,
                title : this.lp.editAddress,
                events : {
                    mouseover : function(){
                        this.editAction.setStyles( this.css.action_edit_over );
                    }.bind(this),
                    mouseout : function(){
                        this.editAction.setStyles( this.css.action_edit );
                    }.bind(this),
                    click : function(){
                        this.editBuilding()
                    }.bind(this)
                }
            }).inject(container);

            this.removeAction = new Element("div", {
                styles: this.css.action_remove,
                title: this.lp.removeBuilding,
                events : {
                    mouseover : function(){
                        this.removeAction.setStyles( this.css.action_remove_over );
                    }.bind(this),
                    mouseout : function(){
                        this.removeAction.setStyles( this.css.action_remove );
                    }.bind(this),
                    click : function( e ){
                        this.removeBuilding(e);
                    }.bind(this)
                }
            }).inject(container);
        }
    },
    editBuilding : function(){
        var form = new MWF.xApplication.Meeting.BuildingForm(this,this.data, {}, {app:this.app});
        form.edit();
    },
    removeBuilding: function(e) {
        this.app.actions.getBuilding( this.data.id, function( json ){
            if( json.data.roomList && json.data.roomList.length > 0 ){
                this.app.notice( this.app.lp.delete_building_hasRoom.replace(/{name}/g, this.data.name),"error", e.target );
            }else{
                var info = this.app.lp.delete_building;
                info = info.replace(/{name}/g, this.data.name);
                var _self = this;
                this.app.confirm("warn", e, this.app.lp.delete_building_title, info, 300, 120, function(){
                    _self.remove();
                    this.close();
                }, function(){
                    this.close();
                });
            }
        }.bind(this));
    },
    remove: function(){
        var view = this.view;
        this.app.actions.deleteBuilding(this.data.id, function(){
            view.reload();
        }.bind(this));
    },
    reload : function(){
        this.view.reload();
    }
});

MWF.xApplication.Meeting.RoomForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "900",
        "height": "500",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": false,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {
        if( this.isNew ){
            this.formTopTextNode.set( "text", this.lp.addRoom );
        }else if( this.isEdited ){
            this.formTopTextNode.set( "text", this.lp.editRoom );
            //this.options.height = "550";
        }else{
            this.formTopTextNode.set( "text", this.lp.room );
            //this.options.height = "460";
        }

        var lp = this.lp.roomForm;

        var editEnable = ( !this.isEdited && !this.isNew && MWF.AC.isMeetingAdministrator() );

        var boxStyle = (this.isEdited || this.isNew) ? "border:1px solid #ccc; border-radius: 4px;overflow: hidden;padding:8px;" : "";

        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTableTitle'>"+ lp.name+":</td>" +
            "    <td styles='formTableValue' item='name' colspan='3' ></td></tr>" +
            "<tr><td styles='formTableTitle'>"+ lp.building +":</td>" +
            "    <td styles='formTableValue' item='buildingName' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+ lp.floor +":</td>" +
            "    <td styles='formTableValue' item='floor'></td>"+
            "    <td styles='formTableTitle2'>"+ lp.capacity +":</td>" +
            "    <td styles='formTableValue' item='capacity'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+lp.roomNumber+":</td>" +
            "    <td styles='formTableValue' item='roomNumber'></td>" +
            "    <td styles='formTableTitle2'>"+lp.phone+":</td>" +
            "    <td styles='formTableValue' item='phoneNumber'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+lp.device+":</td>" +
            "    <td styles='formTableValue' colspan='3'>" +
            "       <div item='deviceList' style='"+ boxStyle +"'></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableTitle'>"+lp.available+":</td>" +
            "    <td styles='formTableValue' colspan='3'>" +
            "       <div item='available' style='"+ boxStyle +"'></div>" +
            "</td></tr>" +
            "<tr><td styles='formTableTitle'></td>" +
            "    <td styles='formTableValue' colspan='3' style='padding-top: 30px;'>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( editEnable ? "" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew || editEnable) ? "float:left;" : "float:right;margin-right:15px;")+"'></div>"+
            "   </td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        var data = this.data || {};
        this.buildingId = data.building;
        if( this.buildingId ){
            this.getBuliding()
        }
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                itemTemplate: {
                    name: { text : lp.name, notEmpty : true },
                    buildingName: {
                        text : lp.building,  notEmpty : true,
                        value : this.buildingName || "",
                        event : {
                            "click": function(){this.listBuilding();}.bind(this),
                            //"blur": function(){}.bind(this),
                            "change": function(){ this.buildingId = null; }.bind(this),
                            "focus": function(){
                                this.listBuildingHide();
                                this.listBuilding();
                            },
                            "keydown": function(e){
                                //if ([13,40,38].indexOf(e.code)!=-1){
                                //    if (!this.selectBuildingNode){
                                //        this.listBuilding();
                                //    }
                                //}
                                switch (e.code){
                                    case 13:
                                        this.listBuildingHide();
                                        this.listBuilding();
                                        break;
                                    case 40:
                                        this.selectBuildingNext();
                                        break;
                                    case 38:
                                        this.selectBuildingPrev();
                                        break;
                                    case 38:
                                        this.selectBuildingConfirm();
                                        break;
                                    default:
                                        // this.listBuildingHide();
                                        // this.listBuilding();
                                }
                                // if (e.code==13){
                                //     this.listBuildingHide();
                                //     this.listBuilding();
                                // }
                                // if (e.code==40) this.selectBuildingNext();
                                // if (e.code==38) this.selectBuildingPrev();
                                // if (e.code==32) this.selectBuildingConfirm(e);
                            }.bind(this)
                        }
                    },
                    floor: { type: "select", defaultValue: "1", notEmpty : true, text : lp.floor,
                        selectText : function(){
                            var floors = [];
                            for (var i=-2; i<=50; i++){
                                if( i != 0  )floors.push(i+ this.lp.floor )
                            }
                            return floors;
                        }.bind(this), selectValue : function(){
                            var floors = [];
                            for (var i=-2; i<=50; i++){
                                if( i != 0  )floors.push(i)
                            }
                            return floors;
                        }.bind(this)
                    },
                    capacity: { notEmpty : true, tType: "number", text : lp.capacity },
                    roomNumber: {},
                    phoneNumber: {},
                    deviceList: { type : "checkbox",
                        value : this.data.device ? this.data.device.split("#") : "",
                        selectValue : function(){ return Object.keys(this.lp.device); }.bind(this),
                        selectText : function(){ return Object.values(this.lp.device); }.bind(this)
                    },
                    available: { type : "radio", defaultValue : "true",
                        selectValue : [ "true", "false" ],
                        selectText : [ this.lp.enable, this.lp.disable ]
                    },
                    saveAction : { type : "button", className : "inputOkButton", value : this.lp.save, event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "button", className : "inputCancelButton", value : this.lp.delete , event : {
                        click : function( item, ev ){ this.removeRoom(ev); }.bind(this)
                    } },
                    editAction : { type : "button", className : "inputOkButton", value : this.lp.editRoom , event : {
                        click : function(){ this.editRoom(); }.bind(this)
                    } },
                    cancelAction : { type : "button", className : "inputCancelButton", value : this.lp.close , event : {
                        click : function(){ this.close(); }.bind(this)
                    } }
                }
            }, this.app);
            this.form.load();

        }.bind(this), true);
    },
    editRoom : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();

        this.edit();
    },
    getBuliding : function(){
        this.actions.getBuilding( this.buildingId, function(json){
            this.buildingName = json.data.name
        }.bind(this), null, false )
    },
    reset: function(){
        this.formTableArea.empty();
        this._createTableContent();
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
    selectBuildingNext: function(){
        if (this.selectBuildingNode){
            var node=null;
            if (this.selectBuildingNode.selectedNode){
                node = this.selectBuildingNode.selectedNode.getNext();
                if (!node) node = this.selectBuildingNode.getFirst();
                this.selectBuildingNode.selectedNode.setStyle("background-color", this.selectBuildingNode.selectedNode.retrieve("bg"));
            }else{
                node = this.selectBuildingNode.getFirst();
            }
            if (node){
                var color = node.getStyle("background-color");
                node.store("bg", color);
                node.setStyles(this.css.createRoomBuildingSelectItem_over);
                this.selectBuildingNode.selectedNode = node;
            }
        }
    },
    selectBuildingPrev: function(){
        if (this.selectBuildingNode){
            var node = null;
            if (this.selectBuildingNode.selectedNode){
                var node = this.selectBuildingNode.selectedNode.getPrevious();
                if (!node) node = this.selectBuildingNode.getLast();
                this.selectBuildingNode.selectedNode.setStyle("background-color", this.selectBuildingNode.selectedNode.retrieve("bg"));
            }else{
                node = this.selectBuildingNode.getLast();
            }
            if (node){
                var color = node.getStyle("background-color");
                node.store("bg", color);
                node.setStyles(this.css.createRoomBuildingSelectItem_over);
                this.selectBuildingNode.selectedNode = node;
            }
        }
    },
    selectBuildingConfirm: function(e){
        if (this.selectBuildingNode.selectedNode){
            this.selectBuilding(this.selectBuildingNode.selectedNode);
            e.preventDefault();
        }
    },

    //cancelCreateRoom: function(){
    //    this.createRoomNode.destroy();
    //},
    listBuildingData: function(key, callback){
        if (key){
            this.actions.listBuildingByKey(key, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listBuilding(function(json){
                if (callback) callback(json);
            });
        }
    },
    listBuilding: function(){
        var item = this.form.getItem("buildingName");
        var key = item.getValue();
        this.listBuildingData(key, function(json){
            if (json.data && json.data.length){
                this.selectBuildingNode = new Element("div", {"styles": this.css.createRoomSelectBuildingNode}).inject(item.container);
                this.setSelectBuildingNodeSize();
                this.listBuildingHideFun = this.listBuildingHide.bind(this);
                this.formMaskNode.addEvent("mousedown", this.listBuildingHideFun);
                this.formNode.addEvent("mousedown", this.listBuildingHideFun);

                var _self = this;
                json.data.each(function(building, idx){
                    var node = new Element("div", {"styles": this.css.createRoomBuildingSelectItem}).inject(this.selectBuildingNode);
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
                        }
                    });

                }.bind(this));
            }
        }.bind(this));
        // if( !key ){
        //     listBuilding
        // }else{
        //
        // }
        // this.actions.listBuildingByKey(key, function(json){
        //
        // }.bind(this));
    },
    selectBuilding: function(node){
        var id = node.retrieve("building");
        var text = node.getFirst().get("text");
        this.form.getItem("buildingName").setValue(text);
        this.buildingId = id;

        this.listBuildingHide();
    },

    setSelectBuildingNodeSize: function(){
        var buildingInput = this.form.getItem("buildingName").getElements()[0];

        //var p = buildingInput.getPosition(buildingInput.getOffsetParent());

        this.selectBuildingNode.position({
            relativeTo: buildingInput,
            position: 'bottomLeft',
            edge: 'upperCenter',
            offset: {x: 4, y: 0}
        });
        //this.selectBuildingNode.setStyle("left", p.x);

        var size = buildingInput.getSize();
        var w = size.x-8;
        this.selectBuildingNode.setStyle("width", ""+w+"px");
    },
    listBuildingHide: function(){
        if (this.selectBuildingNode){
            //this.removeEvent("resize", this.setSelectBuildingNodeSizeFun);

            this.selectBuildingNode.destroy();
            this.selectBuildingNode = null;

            this.formMaskNode.removeEvent("mousedown", this.listBuildingHideFun);
            this.formNode.removeEvent("mousedown", this.listBuildingHideFun);
        }
    },
    save: function(){
        this.getData(function(data){
            this.actions.saveRoom(data, function(json){
                this.app.notice(this.lp.roomForm.save_success, "success");
                var view = this.view;
                this.close();
                view.reload();
            }.bind(this));
        }.bind(this));
    },
    getData: function(callback){
        var data = this.form.getResult(true,null,true,false,true);
        if( !data )return;
        data.device = data.deviceList.join("#");
        data.available = data.available == "true";

        if (!this.buildingId){
            this.actions.saveBuilding({"name": data.buildingName, "address": ""}, function(json){
                data.building = json.data.id;
                if (callback) callback(data);
            }.bind(this));
        }else{
            data.building = this.buildingId;
            if (callback) callback(data);
        }
    }
});

MWF.xApplication.Meeting.RoomTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        this.loadBuilding( function(){
            if(callback)callback();
        }.bind(this) );
    },
    _getHtml : function(){
        var data = this.data;
        var lp = this.lp.device;

        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";
        var device = [];
        data.device.split("#").each( function( d ){
            device.push( lp[d] );
        }.bind(this));

        lp = this.lp.roomForm;

        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.room +"</div>"+
            "</div>"+
            "<div style='font-size: 18px;color:#333;padding:0px 10px 15px 20px;'>"+ data.name +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='100'>"+ lp.building+":</td>" +
            "    <td style='"+valueStyle+"' item='building'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>" + lp.floor+":</td>" +
            "    <td style='"+valueStyle+"'>" + data.floor + "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.capacity +":</td>" +
            "    <td style='"+valueStyle+"'>"+ data.capacity+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.roomNumber +":</td>" +
            "    <td style='"+valueStyle+"'>"+ data.roomNumber+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.phone+":</td>" +
            "    <td style='"+valueStyle+"'>"+ data.phoneNumber +"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.device +":</td>" +
            "    <td style='"+valueStyle+"'>"+ device.join( "," ) +"</td></tr>"+
            "<tr><td style='"+titleStyle+"'>"+ lp.available +":</td>" +
            "    <td style='"+valueStyle+"'>" + ( !data.available ? this.lp.disable : this.lp.enable ) + "</td></tr>"+
            "</table>";
        return html;
    },
    loadBuilding: function( callback ){
        var area = this.node.getElement("[item='building']");
        if (this.data.building){
            this.app.actions.getBuilding(this.data.building, function(bjson){
                area.set("text", bjson.data.name );
                if( callback )callback();
            }.bind(this));
        }
    }
});

MWF.xApplication.Meeting.MeetingForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "meeting",
        "width": "900",
        "height": "640",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": false,
        "draggable": true,
        "closeAction": true,
        "maxAction" : true
    },
    open: function (e) {
        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this.getMeetingData( function(){
            this._open();
        }.bind(this));
        this.fireEvent("postOpen");
    },
    create: function () {
        this.fireEvent("queryCreate");
        this.isNew = true;
        this._open();
        this.fireEvent("postCreate");
    },
    edit: function () {
        this.fireEvent("queryEdit");
        this.isEdited = true;
        this.getMeetingData( function(){
            this._open();
        }.bind(this));
        this.fireEvent("postEdit");
    },
    _createTableContent: function () {
        this.userName = layout.desktop.session.user.distinguishedName;
        this.userId = layout.desktop.session.user.id;
        if( this.isNew ){
            this.formTopTextNode.set( "text", this.lp.addMeeting );
        }else if( this.isEdited ){
            this.formTopTextNode.set( "text", this.lp.editMeeting );
            this.options.height = "610";
        }else{
            this.formTopTextNode.set( "text", this.lp.metting );
            this.options.height = "540";
        }

        var defaultDate, defaultBeginTime, date1, defaultEndTime;
        if( this.options.date || this.options.minute ){
            this.date = typeOf( this.options.date )=="string" ? Date.parse( this.options.date ) : this.options.date;
            defaultDate = this.date.clone().format("%Y-%m-%d");
            if( this.date.getHours() ){
                defaultBeginTime = this.getString( this.date.getHours() )+":"+ this.getString( this.date.getMinutes() );
                date1 = this.date.clone().increment("hour", this.options.hour ? parseInt( this.options.hour )  : 1 );
                if(this.options.minute)date1 = date1.increment("minute", parseInt( this.options.minute ) );
                defaultEndTime = this.getString( date1.getHours() )+":"+ this.getString( date1.getMinutes() );
            }else{
                var now = new Date();
                defaultBeginTime = (now.getHours()+1)+":"+"00";
                date1 = now.clone().increment("hour", this.options.hour ? parseInt( this.options.hour )  : 1 );
                if(this.options.minute)date1 = date1.increment("minute", parseInt( this.options.minute ) );
                defaultEndTime =  (date1.getHours()+1)+":"+"00";
            }
        }else{
            this.date = new Date();
            defaultDate = this.date.clone().format("%Y-%m-%d");
            defaultBeginTime = (this.date.getHours()+1)+":"+"00";
            date1 = this.date.clone().increment("hour", this.options.hour ? parseInt( this.options.hour )  : 1 );
            if(this.options.minute)date1 = date1.increment("minute", parseInt( this.options.minute ) );
            defaultEndTime =  (date1.getHours()+1)+":"+"00";
        }

        this.formTableArea.setStyle("position","relative");


        var data = this.data;

        var isEditer = this.userName == this.data.applicant || this.userId == this.data.applicant || MWF.AC.isMeetingAdministrator();
        var editEnable = ( !this.isEdited && !this.isNew && this.data.status == "wait" &&  isEditer  );

        var html = "<div item='qrCode' style='position: absolute;right:0px;top:-20px;width:100px;height:130px;'></div><table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr>"+
            "   <td styles='formTableTitle' width='70'>"+this.lp.applyPerson+":</td>" +
            "    <td styles='formTableValue' item='applicant'></td>" +
            "</tr>"+
            "<tr><td styles='formTableTitle' width='100'>"+this.lp.beginDate+":</td>" +
            "    <td styles='formTableValue' item='dateInput'></td>" +
            "</tr>" +
            "<tr><td styles='formTableTitle'>"+ this.lp.time +":</td>" +
            "    <td styles='formTableValue'>" +
            "       <div item='beginTimeInput' style='float:left'></div>"+
            "       <div style='float:left; "+ ( this.isNew ? "margin:5px;" : "margin:0px 5px;") + "'>"+ this.lp.to+ "</div>"+
            "       <div item='endTimeInput' style='float:left'></div>"+
            "   </td></tr>" +
            "<tr><td styles='formTableTitle'>"+ this.lp.selectRoom +":</td>" +
            "    <td styles='formTableValue' item='meetingRoom'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+this.lp.invitePerson2+":</td>" +
            "    <td styles='formTableValue'>" +
            "       <div item='invitePersonList'></div>" +
            "       <div style='display:"+ ( this.isEdited ? "" : "none") +";' item='selectinvitePerson'></div>" +
            (( !this.isNew && this.data.myWaitAccept ) ?
            "       <tr><td></td><td styles='formTableValue'><div item='acceptAction'></div><div item='rejectAction'></div></td></tr>" : ""
            ) +
            "   </td>" +
            "</tr>" +
            "<tr style='display:"+ ( this.isNew ? "none" : "") +" ;' item='checkPersonTr'><td styles='formTableTitle'>签到人员:</td>" +
            "    <td styles='formTableValue' item='checkinPersonList'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+this.lp.meetingSubject+":</td>" +
            "    <td styles='formTableValue' item='subject'></td></tr>" +
            "<tr><td styles='formTableTitle'>"+this.lp.meetingDescription+":</td>" +
            "    <td styles='formTableValue' item='summary'></td></tr>" +
            "<tr style='display:none ;' item='attachmentTr'><td styles='formTableTitle'>"+this.lp.meetingAttachment+":</td>" +
            "    <td styles='formTableValue' item='attachment'></td></tr>" +
            "<tr><td styles='formTableTitle'></td>" +
            "    <td styles='formTableValue' style='padding-top: 10px;'>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( editEnable ? "" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew || editEnable) ? "float:left;" : "float:right;margin-right:15px;")+"'></div>"+
            "   </td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        if( data.startTime ){
            var beignDate = Date.parse( data.startTime  );
            data.dateInput = beignDate.format("%Y-%m-%d");
            data.beginTimeInput = this.getString( beignDate.getHours() ) + ":" + this.getString( beignDate.getMinutes() );
        }
        if( data.completedTime ){
            var endDate = Date.parse( data.completedTime  );
            data.endTimeInput = this.getString( endDate.getHours() ) + ":" + this.getString( endDate.getMinutes() );
        }

        this.meetingRoomArea = this.formTableArea.getElement("[item='meetingRoom']");
        this.attachmentTr = this.formTableArea.getElement("[item='attachmentTr']");
        this.attachmentArea = this.formTableArea.getElement("[item='attachment']");


        this.qrCodeArea = this.formTableArea.getElement("[item='qrCode']");

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "meeting",
                itemTemplate: {
                    applicant : { type : "org", orgType : "person", isEdited : false,
                        defaultValue : this.userName
                    },
                    dateInput: {tType: "date", isEdited : this.isNew, defaultValue: defaultDate,
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    beginTimeInput: { tType: "time", isEdited : this.isNew,
                        defaultValue: defaultBeginTime, className : ( this.isNew ?  "inputTimeUnformatWidth" : "" ),
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    endTimeInput: { tType: "time",  isEdited : this.isNew,
                        defaultValue: defaultEndTime, className : ( this.isNew ?  "inputTimeUnformatWidth" : "" ),
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    invitePersonList: { type: "org", isEdited : this.isNew, orgType: ["identity","person"], count : 0, orgWidgetOptions : {
                        "onLoadedInfor": function(item){
                            this.loadAcceptAndReject( item );
                        }.bind(this)
                    }},
                    checkinPersonList : { type: "org", isEdited : false, orgType: ["identity","person"], count : 0},
                    selectinvitePerson : { type : "button", value : this.lp.addInvitePerson1, style : {"margin-left": "20px"}, event : {
                        click : function( it ){
                            var options = {
                                "type" : "",
                                "types": ["identity","person"],
                                "values": [],
                                "count": 0,
                                "onComplete": function(items){
                                    MWF.require("MWF.widget.O2Identity", function(){
                                        var invitePersonList = [];
                                        items.each(function(item){
                                            var _self = this;
                                            if( item.data.distinguishedName.split("@").getLast().toLowerCase() == "i" ){
                                                var person = new MWF.widget.O2Identity(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                                                invitePersonList.push( item.data.distinguishedName );
                                            }else{
                                                var person = new MWF.widget.O2Person(item.data, it.form.getItem("invitePersonList").container, {"style": "room"});
                                                invitePersonList.push(item.data.distinguishedName);
                                            }
                                        }.bind(this));

                                        this.app.actions.addMeetingInvite(this.data.id, {"invitePersonList": invitePersonList, "id": this.data.id}, function(json){
                                            this.app.actions.getMeeting(json.data.id, function(meeting){
                                                this.invitePersonList = meeting.data.invitePersonList;
                                                this.app.notice(this.app.lp.addedInvitePerson1, "success", this.node );
                                            }.bind(this));
                                        }.bind(this));

                                    }.bind(this));
                                }.bind(this)
                            };
                            var selector = new MWF.O2Selector(this.app.content, options);
                        }.bind(this)
                    } },
                    subject: {},
                    summary: {type: "textarea"},
                    acceptAction : { type : "button", value : this.lp.accept,  className : "inputAcceptButton",
                        event : {  click : function( it, ev ){ this.accept(ev); }.bind(this) }
                    },
                    rejectAction : { type : "button", value : this.lp.reject, style : {"margin-left": "20px"}, className : "inputDenyButton",
                        event : {  click : function( it, ev ){ this.reject(ev) }.bind(this) }
                    },
                    saveAction : { type : "button", className : "inputOkButton", value : this.lp.save, event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "button", className : "inputCancelButton", value : this.lp.cancelMeeting , event : {
                        click : function( item, ev ){ this.cancelMeeting(ev); }.bind(this)
                    } },
                    editAction : { type : "button", className : "inputOkButton", value : this.lp.editMeeting , event : {
                        click : function(){ this.editMeeting(); }.bind(this)
                    } },
                    cancelAction : { type : "button", className : "inputCancelButton", value : this.lp.close , event : {
                        click : function(){ this.close(); }.bind(this)
                    } }
                }
            }, this.app);
            this.form.load();

            this.loadSelectRoom();
            if( this.data.id )this.loadAttachment();

            if( isEditer && !this.isNew ){
                this.loadQrCode();
            }else{
                this.qrCodeArea.destroy();
            }
        }.bind(this), true);
    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
    },
    loadQrCode : function(){
        this.actions.getCheckinQrCode( this.data.id, function(json){
            var img = new Element("img",{
                src : "data:image/png;base64,"+json.data.image,
                styles : {
                    width : "100px",
                    height : "100px"
                }
            }).inject( this.qrCodeArea );
            var div = new Element("div",{
                text : "打印签到二维码",
                styles : {
                    color : "#3c75b7",
                    cursor : "pointer"
                },
                events : {
                    click : function(){
                        window.open( "/x_component_Meeting/$Main/qrPrint.html?meeting="+this.data.id, "_blank" );
                    }.bind(this)
                }
            }).inject( this.qrCodeArea );
        }.bind(this));
    },
    loadAcceptAndReject : function( item ){
        var personName = item.data.distinguishedName;
        if(this.data.acceptPersonList){
            if (this.data.acceptPersonList.indexOf(personName)!==-1){
                var acceptNode = new Element("div", {"styles": this.css.acceptIconNode}).inject(item.node, "top");
                new Element("div", {
                    "styles": this.css.acceptTextNode,
                    "text": this.app.lp.accepted
                }).inject(item.inforNode);
            }
        }
        if(this.data.rejectPersonList){
            if (this.data.rejectPersonList.indexOf(personName)!==-1){
                var rejectNode = new Element("div", {"styles": this.css.rejectIconNode}).inject(item.node, "top");
                new Element("div", {
                    "styles": this.css.rejectTextNode,
                    "text": this.app.lp.rejected
                }).inject(item.inforNode);
            }
        }
    },
    getMeetingData : function( callback ){
        if( this.data && this.data.id ){
            ( this.app.actions || this.actions ).getMeeting( this.data.id, function( json ){
                this.data = json.data;
                if(callback)callback();
            }.bind(this))
        }else{
            if(callback)callback();
        }
    },
    editMeeting : function(){
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();

        this.edit();
    },
    cancelMeeting: function(e){
        var _self = this;
        var text = this.app.lp.cancel_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.cancel_confirm_title, text, 380, 200, function(){
            _self._cancelMeeting();
            this.close();
            _self.close();
        }, function(){
            this.close();
        });
    },
    _cancelMeeting: function(){
        var view = this.view;
        this.app.actions.deleteMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },
    reset: function(){
        this.formTableArea.empty();
        this._createTableContent();
    },
    clearRoom : function(){
        this.roomId = "";
        if(this.roomInput)this.roomInput.set("text", "");
    },
    loadSelectRoom: function(){
        var lineNode = new Element("div", {"styles": this.css.createMeetingInfoLineNode}).inject(this.meetingRoomArea);
        var editNode = new Element("div", {
            "styles": this.css.createMeetingInfoItemEditNode,
            "html": "<div></div>"
        }).inject(lineNode);
        this.roomInput = editNode.getFirst();
        this.roomInput.setStyles(this.css.createMeetingInfoItemDivNode);

        //var downNode = new Element("div", {"styles": this.css.createMeetingInfoItemDownNode}).inject(editNode);

        if( this.isNew ){
            this.roomInput.addEvents({
                "click": function(e){this.selectRooms();}.bind(this)
            });
        }

        var roomId = this.data.room || this.options.room;
        if (roomId){
            ( this.app.actions || this.actions ).getRoom(roomId, function(json){
                ( this.app.actions || this.actions ).getBuilding(json.data.building, function(bjson){
                    this.roomId = roomId;
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
            var w = size.x-2;
            this.selectRoomNode.setStyle("width", ""+w+"px");
        }.bind(this));
    },

    createRoomNode: function(callback){
        if (!this.selectRoomNode){
            this.selectRoomNode = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomNode}).inject(this.formNode);
            this.selectRoomNode.addEvent("mousedown", function(e){e.stopPropagation();});
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    loadSelectRooms: function(){
        var result = this.form.getResult(false,null,false,false,true);
        var d = result.dateInput;
        var bt = result.beginTimeInput;
        var et = result.endTimeInput;
        var start = d+" "+bt;
        var completed = d+" "+et;

        this.app.actions.listBuildingByRange(start, completed, function(json){
            json.data.each(function(building){
                var node = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1Node}).inject(this.selectRoomNode);
                var nodeName = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1NameNode, "text": building.name}).inject(node);
                var nodeAddr = new Element("div", {"styles": this.css.createMeetingInfoSelectRoomItem1AddrNode, "text": building.address}).inject(node);


                building.roomList.each(function(room, i){
                    if (room.available && room.idle) this.createRoomSelectNode(room, i, building);
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
            node.setStyle("background-image", "url(/x_component_Meeting/$RoomView/default/icon/device/"+name+"_disable.png)");
        }.bind(this));
        if ((i % 2)!=0) roomNode.setStyle("background-color", "#f4f8ff");
        roomNode.store("room", room);

        var _self = this;
        if (room.idle){
            roomNode.addEvents({
                "mouseover": function(e){
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
        var w = size.x-2;
        this.createRoomSelectBuildingNode.setStyle("width", ""+w+"px");
    },
    loadAttachment: function(){
        this.attachmentTr.setStyle("display","");
        this.attachmentNode = new Element("div", {"styles": this.css.createMeetingAttachmentNode}).inject(this.attachmentArea);
        var attachmentContentNode = new Element("div", {"styles": this.css.createMeetingAttachmentContentNode}).inject(this.attachmentNode);
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.AttachmentController(attachmentContentNode, this, {
                "size": "min",
                "isSizeChange": false,
                "isReplace": false,
                "isUpload": this.isNew || this.isEdited,
                "isDelete": this.isNew || this.isEdited,
                "isDownload": true,
                "readonly": !this.isNew && !this.isEdited
            });
            this.attachmentController.load();
            if( this.data.attachmentList ){
                this.data.attachmentList.each(function (att) {
                    att.person = att.lastUpdatePerson.split("@")[0];
                    var at = this.attachmentController.addAttachment(att);
                }.bind(this));
            }
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

                    this.app.actions.addAttachment(this.data.id, formData, file, function(o, text){
                        if (o.id){
                            this.app.actions.getAttachment(o.id, function(json){
                                if (json.data) this.attachmentController.addAttachment(json.data);
                                this.attachmentController.checkActions();
                            }.bind(this))
                        }
                        this.attachmentController.checkActions();
                    }.bind(this));
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

    save: function(){
        this._save(function(){
            this.app.notice(this.lp.meeting_saveSuccess, "success");
            if (!this.attachmentNode){
                this.loadAttachment();
            }
        }.bind(this));
    },
    _save: function(callback){
        this.getSaveData();
        var now = new Date();
        var errorText = "";
        if (!this.data.subject) errorText +=this.lp.meeting_input_subject_error;
        if (!this.data.room) errorText +=this.lp.meeting_input_room_error;
        if (!this.data.invitePersonList.length) errorText +=this.lp.meeting_input_person_error;
        if( this.data.startTimeDate ){
            if (this.data.startTimeDate.diff(this.data.completedTimeDate, "minute")<1) errorText +=this.lp.meeting_input_time_error;
            if (now.diff(this.data.startTimeDate, "minute")<0) errorText +=this.lp.meeting_input_date_error;

            delete this.data.startTimeDate;
            delete this.data.completedTimeDate;
        }

        if (errorText){
            this.app.notice(this.lp.meeting_input_error+errorText, "error");
            return false;
        }
        this.app.actions.saveMeeting(this.data, function(json){
            this.data.id = json.data.id;
            this.waitReload = true;
            if (callback) callback();
        }.bind(this));
    },
    getSaveData: function(){
        this.data = this.form.getResult(false,null,false,false,true);
        this.data.room = this.roomId;
        if( this.invitePersonList ){
            this.data.invitePersonList = this.invitePersonList;
        }

        var d = this.data.dateInput;
        if( this.isNew ) {
            var startTime = d + " " + this.data.beginTimeInput + ":0";
            var completedTime = d + " " + this.data.endTimeInput + ":0";
            var startTimeDate = Date.parse(startTime);
            var completedTimeDate = Date.parse(completedTime);

            this.data.startTime = startTime;
            this.data.completedTime = completedTime;
            this.data.startTimeDate = startTimeDate;
            this.data.completedTimeDate = completedTimeDate;
        }

        if( this.isNew ){
            delete this.data.applicant;
        }else{
            this.data.applicant = this.data.applicant.join(",")
        }

    },
    getPersonByIdentity : function( identity ){

    },
    getPerson : function( source ){
        if( typeOf(source) != "array" )source = [source];
        var target = [];
        source.each( function( s ){
            if( s.split("@").getLast().toLowerCase() == "i" ){
                target.push( this.getPersonByIdentity( s ) )
            }else{
                target.push( s )
            }
        }.bind(this));
        return target;
    },
    reject: function(e){
        var _self = this;
        var text = this.app.lp.reject_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.reject_confirm_title, text, 300, 120, function(){
            _self.rejectMeeting();
            this.close();
            _self.close();
        }, function(){
            this.close();
        });
    },
    rejectMeeting: function(){
        var view = this.view;
        this.app.actions.rejectMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },

    accept: function(e){
        var _self = this;
        var text = this.app.lp.accept_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.accept_confirm_title, text, 300, 120, function(){
            _self.acceptMeeting();
            this.close();
            _self.close();
        }, function(){
            this.close();
        });
    },
    acceptMeeting: function(){
        var view = this.view;
        this.app.actions.acceptMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },
    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postClose");
        debugger;
        if( this.waitReload )this.view.reload();
        delete this;
    }
});

MWF.xApplication.Meeting.MeetingTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        this.loadRoom( function(){
            this.loadInvite();
            this.loadAttachment();
            if(callback)callback();
        }.bind(this) );
    },
    _getHtml : function(){
        var data = this.data;
        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";
        var persons = [];
        data.invitePersonList.each( function( p ){
            persons.push(p.split("@")[0] )
        }.bind(this));

        var color = "#ccc";
        switch (data.status){
            case "wait":
                color = "#4990E2";
                break;
            case "processing":
                color = "#66CC7F";
                break;
            case "completed":
                color = "#666";
                break;
        }
        if (data.myWaitAccept){
            color = "#F6A623";
        }
        if (data.confilct){
            color = "#FF7F7F";
        }


        var beginDate = Date.parse(data.startTime);
        var endDate = Date.parse(data.completedTime);
        var dateStr = beginDate.format(this.app.lp.dateFormatDay);
        var beginTime = this.getString( beginDate.getHours() ) + ":" + this.getString( beginDate.getMinutes() );
        var endTime = this.getString( endDate.getHours() ) + ":" + this.getString( endDate.getMinutes() );
        var meetingTime = dateStr + " " +  beginTime + "-" + endTime;
        var description = (data.description || "")+(data.summary || "");
        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 12px;color:#666; float: right'>"+ this.lp.applyPerson  +":" + data.applicant.split("@")[0] +"</div>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.meetingDetail +"</div>"+
            "</div>"+
            "<div style='font-size: 18px;color:#333;padding:0px 10px 15px 20px;'>"+ data.subject +"</div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+";' width='70'>"+this.lp.meetingTime+":</td>" +
            "    <td style='"+valueStyle+";color:"+ color +"'>" + meetingTime + "</td></tr>" +
            //"<tr><td style='"+titleStyle+"' width='70'>"+this.lp.beginTime+":</td>" +
            //"    <td style='"+valueStyle+"'>" + data.startTime + "</td></tr>" +
            //"<tr><td style='"+titleStyle+"'>"+this.lp.endTime+":</td>" +
            //"    <td style='"+valueStyle+"'>" + data.completedTime + "</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+this.lp.selectRoom +":</td>" +
            "    <td style='"+valueStyle+"' item='meetingRoom'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+this.lp.invitePerson2+":</td>" +
            "    <td style='"+valueStyle+"' item='invitePerson'>"+persons.join(",")+"</td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+this.lp.meetingDescription+":</td>" +
            "    <td style='"+valueStyle+"'>"+ description +"</td></tr>" +
            ( this.options.isHideAttachment ? "" :
            "<tr><td style='"+titleStyle+"'>"+this.lp.meetingAttachment+":</td>" +
            "    <td style='"+valueStyle+"' item='attachment'></td></tr>"
            )+
        "</table>";
        return html;
    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
    },
    loadRoom: function( callback ){
        var area = this.node.getElement("[item='meetingRoom']");
        if (this.data.room){
            this.app.actions.getRoom(this.data.room, function(json){
                this.app.actions.getBuilding(json.data.building, function(bjson){
                    area.set("text", json.data.name+" ("+bjson.data.name+")");
                    if( callback )callback();
                }.bind(this));
            }.bind(this));
        }
    },
    loadInvite : function(){
        this.O2PersonList = [];
        var area = this.node.getElement("[item='invitePerson']");
        MWF.require("MWF.widget.O2Identity", function(){
            area.empty();
            this.data.invitePersonList.each(function( person ){
                var O2person = new MWF.widget.O2Person({name : person}, area, {"style": "room", "onLoadedInfor": function(item){
                    this.loadAcceptAndReject( item );
                }.bind(this) });
                this.O2PersonList.push( O2person );
            }.bind(this));
        }.bind(this));
    },
    loadAcceptAndReject : function( item ){
        var personName = item.data.distinguishedName;
        if( this.data.acceptPersonList ){
            if (this.data.acceptPersonList.indexOf(personName)!==-1){
                var acceptNode = new Element("div", {"styles": {
                    "height": "20px",
                    "width": "14px",
                    "margin-right": "3px",
                    "float": "left",
                    "background": "url(/x_component_Template/$MPopupForm/meeting/icon/accept.png) no-repeat center center"
                }}).inject(item.node, "top");
                new Element("div", {
                    "styles" : {"color": "#1fbf04", "clear":"both","text-align":"center"},
                    "text": this.app.lp.accepted
                }).inject(item.inforNode);
            }
        }
        if(this.data.rejectPersonList){
            if (this.data.rejectPersonList.indexOf(personName)!==-1){
                var rejectNode = new Element("div", {"styles": {
                    "height": "20px",
                    "width": "14px",
                    "margin-right": "3px",
                    "float": "left",
                    "background": "url(/x_component_Template/$MPopupForm/meeting/icon/reject.png) no-repeat center center"
                }}).inject(item.node, "top");
                new Element("div", {
                    "styles" : {"color": "#FF0000", "clear":"both","text-align":"center"},
                    "text": this.app.lp.rejected
                }).inject(item.inforNode);
            }
        }
    },
    destroy: function(){
        if( this.O2PersonList ){
            this.O2PersonList.each(function(p){
                p.destroy();
            })
        }
        if( this.node ){
            this.node.destroy();
            this.node = null;
        }
    },
    loadAttachment: function(){
        if( this.options.isHideAttachment )return;
        if( typeOf(this.data.attachmentList)=="array" && this.data.attachmentList[0] ){
            var area = this.node.getElement("[item='attachment']");
            this.attachmentNode = new Element("div"
                //{"styles": this.css.createMeetingAttachmentNode}
            ).inject(area);
            var attachmentContentNode = new Element("div", {
                //"styles": this.css.createMeetingAttachmentContentNode
            }).inject(this.attachmentNode);

                this.attachmentController = new MWF.xApplication.Meeting.MeetingTooltip.AttachmentController(attachmentContentNode, this, {
                    "size": "min",
                    "isSizeChange": false,
                    "isReplace": false,
                    "isUpload": false,
                    "isDelete": false,
                    "isDownload": true,
                    "readonly": true
                });
                this.attachmentController.load();
                this.data.attachmentList.each(function (att) {
                    att.person = att.lastUpdatePerson.split("@")[0];
                    var at = this.attachmentController.addAttachment(att);
                }.bind(this));
        }
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
    }
});

MWF.xApplication.Meeting.MeetingTooltip.AttachmentController = new Class({
    Extends : MWF.widget.AttachmentController,
    loadMin: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode){
            //this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode}).inject(this.node);
            this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);
            this.minContent.setStyles({
                "margin-right" : "0px"
            });
            //this.min_downloadAction = this.createAction(this.minActionAreaNode, "download", MWF.LP.widget.download, function(e, node){
            //    this.downloadAttachment(e, node);
            //}.bind(this));

            this.node.inject(this.container);

            //if (this.options.readonly) this.setReadonly();
            this.checkActions();

            this.setEvent();
        }else{
            //this.minActionAreaNode.setStyle("display", "block");
            this.minContent.setStyle("display", "block");
            this.minContent.empty();
        }
        var atts = [];
        while (this.attachments.length){
            var att = this.attachments.shift();
            atts.push(new MWF.xApplication.Meeting.MeetingTooltip.AttachmentMin(att.data, this));
        }
        this.attachments = atts;
    },
    addAttachment: function(data){
        if (this.options.size=="min"){
            this.attachments.push(new MWF.xApplication.Meeting.MeetingTooltip.AttachmentMin(data, this));
        }else{
            this.attachments.push(new MWF.widget.AttachmentController.Attachment(data, this));
        }
    }

});

MWF.xApplication.Meeting.MeetingTooltip.AttachmentMin = new Class({
    Extends : MWF.widget.AttachmentController.AttachmentMin,
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list"])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "click": function(e){this.downloadAttachment(e);}.bind(this)
        });
    },
    downloadAttachment: function(e){
        if (this.controller.module) this.controller.module.downloadAttachment(e, null, [this]);
    }
});

MWF.xApplication.Meeting.MeetingArea = new Class({
    initialize: function(container, view, data){
        this.container = container;
        this.view = view;
        this.css = this.view.css;
        this.app = this.view.app;
        this.data = data;
        this.beginDate = Date.parse(this.data.startTime);
        this.endDate = Date.parse(this.data.completedTime);

        this.userName = layout.desktop.session.user.distinguishedName;
        this.userId = layout.desktop.session.user.id;

        this.parseData();

        this.path = "/x_component_Meeting/$Common/default/meetingarea/";
        this.cssPath = "/x_component_Meeting/$Common/default/meetingarea/css.wcss";
        this._loadCss();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.meetingNode}).inject( this.container );
        this.node.addEvents({
            mouseenter : function(){
                this.node.setStyles( this.css.meetingNode_over );
                this.subjectNode.setStyles( this.css.meetingSubjectNode_over );
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.css.meetingNode );
                this.subjectNode.setStyles( this.css.meetingSubjectNode );
            }.bind(this),
            click : function(){
                this.openMeeting()
            }.bind(this)
        });

        this.colorNode = new Element("div", {"styles": this.css.meetingColorNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.meetingContentNode}).inject(this.node);

        var beginTime = (this.beginDate.getHours() < 12 ? this.app.lp.am : this.app.lp.pm) + " " + this.getString( this.beginDate.getHours() ) + ":" + this.getString( this.beginDate.getMinutes() );
        var endTime = (this.endDate.getHours() < 12 ? this.app.lp.am : this.app.lp.pm) + " " + this.getString( this.endDate.getHours() ) + ":" + this.getString( this.endDate.getMinutes() );
        this.timeNode = new Element("div", {
            "styles": this.css.meetingTimeNode,
            "text" : beginTime + "-" + endTime
        }).inject(this.contentNode);

        this.subjectNode = new Element("div", {
            "styles": this.css.meetingSubjectNode,
            "text": this.data.subject
        }).inject(this.contentNode);

        var description = (this.data.description || "")+(this.data.summary || "");
        this.descriptionNode = new Element("div", {
            "styles": this.css.meetingDescriptionNode,
            "text" : description
        }).inject(this.contentNode);

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
                //add attachment
                this.colorNode.setStyles({"background-color": "#ccc"});
                this.timeNode.setStyles({"color": "#ccc"});
                break;
        }
        if (this.data.myWaitAccept){
            this.colorNode.setStyles({"background-color": "#F6A623"});
            this.timeNode.setStyles({"color": "#F6A623"});
        }
        this.resetNodeSize();

        this.loadTooltip();

    },
    parseData : function(){
        if( !this.data.status ){
            var now = new Date();
            var status;
            if( this.beginDate > now ){
                status = "wait";
            }else if( this.endDate < now ){
                status = "completed";
            }else{
                status = "processing";
            }
            this.data.status = status;
        }

        if( typeOf( this.data.myWaitAccept ) != "boolean" ){
            if( this.data.invitePersonList.contains( this.userName ) || this.data.invitePersonList.contains( this.userId ) ){
                this.data.myWaitAccept = ( !this.data.acceptPersonList.contains( this.userName ) || this.data.acceptPersonList.contains( this.userId ) ) &&
                    ( !this.data.rejectPersonList.contains( this.userName ) || !this.data.rejectPersonList.contains( this.userId ) )
            }
        }

    },
    loadActionBar : function(){
        if( this.userName == this.data.applicant || this.userId == this.data.applicant || MWF.AC.isMeetingAdministrator() || this.data.myWaitAccept ){
        }else{
            return;
        }

        this.actionBar = new Element("div", {
            "styles": this.css.actionBar
        }).inject(this.contentNode);

        //this.viewAction = new Element("div", {
        //    styles: this.css.action_view,
        //    events : {
        //        mouseenter : function(){
        //            this.viewAction.setStyles( this.css.action_view_over );
        //        }.bind(this),
        //        mouseleave : function(){
        //            this.viewAction.setStyles( this.css.action_view );
        //        }.bind(this),
        //        click : function(){
        //            this.openMeeting()
        //        }.bind(this)
        //    }
        //}).inject(this.actionBar);

        if( this.userName == this.data.applicant || this.userId == this.data.applicant || MWF.AC.isMeetingAdministrator() ){

            if( this.data.status=="wait"  ){
                this.editAction = new Element("div", {
                    styles: this.css.action_edit,
                    events : {
                        mouseover : function(){
                            this.editAction.setStyles( this.css.action_edit_over );
                        }.bind(this),
                        mouseout : function(){
                            this.editAction.setStyles( this.css.action_edit );
                        }.bind(this),
                        click : function(e){
                            this.editMeeting();
                            e.stopPropagation();
                        }.bind(this)
                    }
                }).inject(this.actionBar);

                //if (this.data.myWaitConfirm) this.createConfirmActions();
                //if (this.data.myWaitAccept) this.createAcceptActions();
                //if (this.data.status=="wait" && this.isEdit) this.createCancelActions();

                this.removeAction = new Element("div", {
                    styles: this.css.action_remove,
                    events : {
                        mouseover : function(){
                            this.removeAction.setStyles( this.css.action_remove_over );
                        }.bind(this),
                        mouseout : function(){
                            this.removeAction.setStyles( this.css.action_remove );
                        }.bind(this),
                        click : function( e ){
                            this.cancel(e);
                            e.stopPropagation();
                        }.bind(this)
                    }
                }).inject(this.actionBar);
            }
        }


        if (this.data.myWaitAccept){
            this.acceptAction = new Element("div", {
                styles: this.css.action_accept,
                title : this.app.lp.accept,
                events : {
                    mouseover : function(){
                        this.acceptAction.setStyles( this.css.action_accept_over );
                    }.bind(this),
                    mouseout : function(){
                        this.acceptAction.setStyles( this.css.action_accept );
                    }.bind(this),
                    click : function( e ){
                        this.accept(e);
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionBar);

            this.rejectAction = new Element("div", {
                styles: this.css.action_reject,
                title : this.app.lp.reject,
                events : {
                    mouseover : function(){
                        this.rejectAction.setStyles( this.css.action_reject_over );
                    }.bind(this),
                    mouseout : function(){
                        this.rejectAction.setStyles( this.css.action_reject );
                    }.bind(this),
                    click : function( e ){
                        this.reject(e);
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionBar);
        }
    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
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
    },
    loadTooltip : function( isHideAttachment ){
        this.tooltip = new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300,
            isHideAttachment : isHideAttachment
        });
    },
    showTooltip: function(  ){
        if( this.tooltip ){
            this.tooltip.load();
        }else{
            this.tooltip = new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, this.viewAction, this.app, this.data);
            this.tooltip.load();
        }
    },
    openMeeting: function(){
        var form = new MWF.xApplication.Meeting.MeetingForm(this,this.data, {}, {app:this.app});
        form.view = this.view;
        form.open();
    },
    resetNodeSize: function(){
        var contentSize = this.contentNode.getSize();
        this.colorNode.setStyle("height", contentSize.y );
    },
    destroy: function(){
        if(this.tooltip)this.tooltip.destroy();
        this.node.destroy();
        MWF.release(this);
    },
    editMeeting : function(){
        var form = new MWF.xApplication.Meeting.MeetingForm(this,this.data, {}, {app:this.app});
        form.view = this.view;
        form.edit();
    },
    cancel: function(e){
        var _self = this;
        var text = this.app.lp.cancel_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.cancel_confirm_title, text, 380, 150, function(){
            _self.cancelMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    cancelMeeting: function(){
        var view = this.view;
        this.app.actions.deleteMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },


    reject: function(e){
        var _self = this;
        var text = this.app.lp.reject_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.reject_confirm_title, text, 300, 120, function(){
            _self.rejectMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    rejectMeeting: function(){
        var view = this.view;
        this.app.actions.rejectMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },

    accept: function(e){
        var _self = this;
        var text = this.app.lp.accept_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.accept_confirm_title, text, 300, 120, function(){
            _self.acceptMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    acceptMeeting: function(){
        var view = this.view;
        this.app.actions.acceptMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },

    disagree: function(e){
        var _self = this;
        var text = this.app.lp.disagree_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.disagree_confirm_title, text, 300, 120, function(){
            _self.disagreeMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    disagreeMeeting: function(){
        var view = this.view;
        this.app.actions.denyMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    },

    agree: function(e){
        var _self = this;
        var text = this.app.lp.agree_confirm.replace(/{name}/g, this.data.subject);
        this.app.confirm("infor", e, this.app.lp.agree_confirm_title, text, 300, 120, function(){
            _self.agreeMeeting();
            this.close();
        }, function(){
            this.close();
        });
    },
    agreeMeeting: function(){
        var view = this.view;
        this.app.actions.allowMeeting(this.data.id, function(){
            view.reload();
        }.bind(this))
    }
});

MWF.xApplication.Meeting.SideBar = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize : function( container, app,  options){
        this.setOptions( options );
        this.container = container;
        this.app = app;
        //this.css = this.app.css;
        this.lp = this.app.lp;
        this.isHidden = false;
        this.cssPath = "/x_component_Meeting/$Common/"+this.options.style+"/sidebar/css.wcss";
        this._loadCss();

        this.load();
    },
    load : function(){
        this.node = new Element("div.sideBar", {
            "styles": this.css.node,
            events : {
                mousedown : function( ev ){
                    ev.stopPropagation();
                }
            }
        }).inject(this.container);

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.loadStatusArea();

        new Element("div.contentLine", {
            "styles" : this.css.contentLine
        }).inject( this.contentNode );

        this.loadTodayMeetingNode();

        this.trapezoid = new Element("div.trapezoid",{
            "styles":this.css.trapezoid_toRight,
            events : {
                click : function(){
                    this.trigger();
                }.bind(this)
            }
        }).inject(this.node);
        //this.contentNode.getElements("div.line").setStyles(this.css.configContentLine);

        this.loadTodayMeeting( function(){
            var x = this.node.getSize().x - 8;
            this.node.setStyle( "right", "-"+x+"px" );

            this.resetNodeSize();
            this.resetNodeSizeFun = this.resetNodeSize.bind(this);
            this.app.addEvent("resize", this.resetNodeSizeFun );

            this.hideFun = this.hide.bind(this);
            this.app.node.addEvent("mousedown", this.hideFun);
        }.bind(this));
    },
    loadStatusArea : function(){
        var area = new Element("div", {
            "styles" : this.css.statusArea
        }).inject( this.contentNode );

        var html = "<div class='titleDiv'>"+this.lp.config.meetingStatus+"</div>" +
            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:#4990E2'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.wait+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:#66CC7F'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.progress+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:#F6A623'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.invite+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div  class='statusIconStyle' style='background-color:#ccc'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.completed+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div  class='statusIconStyle2' style='border:2px solid #FF7F7F;'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.conflict+"</div></div>" +
            "</div>";

        area.set("html", html);
        area.getElements("div.titleDiv").setStyles( this.css.titleDiv );
        area.getElements("div.statusStyle").setStyles( this.css.statusStyle );
        area.getElements("div.statusIconStyle").setStyles( this.css.statusIconStyle );
        area.getElements("div.statusIconStyle2").setStyles( this.css.statusIconStyle2 );
        area.getElements("div.statusTextStyle").setStyles( this.css.statusTextStyle );
    },
    loadTodayMeetingNode: function(){

        var area = new Element("div.meetingArea", {
            "styles" : this.css.meetingArea
        }).inject( this.contentNode );

        new Element("div.titleDiv", {
            "styles" : this.css.titleDiv,
            "text" : this.lp.meetingNotice
        }).inject( area );

        this.meetingNode = Element("div", {
            "styles" : this.css.meetingNode
        }).inject( area );

    },
    loadTodayMeeting : function( callback ){
        var today = new Date();
        var user = layout.desktop.session.user;
        var dn = user.distinguishedName;

        var y = today.getFullYear();
        var m = today.getMonth()+1;
        var d = today.getDate();

        this.app.actions.listMeetingDay(y, m, d, function(json) {
            var data = [];
            json.data.each( function( d ){
                if( d.invitePersonList.contains( dn ) || d.applicant == dn ){
                    if( !d.rejectPersonList.contains( dn ) ){
                        data.push( d );
                    }
                }
            }.bind(this));

            if( user.distinguishedName ){
                var userName = user.distinguishedName.split("@")[0]
            }else{
                var userName = user.name
            }
            var lp = data.length ?  this.lp.meetingTopInfor : this.lp.noMeetingTopInfor ;
            this.meetingTopNode = new Element("div", {
                "styles" : this.css.meetingTopNode,
                "html" : lp.replace("{userName}",userName).replace("{count}",data.length )
            }).inject( this.meetingNode );

            this.scrollNode = new Element("div.scrollNode", {
                "styles" : this.css.scrollNode
            }).inject( this.meetingNode );

            this.meetingItemContainer = new Element("div.meetingItemContainer", {
                "styles" : this.css.meetingItemContainer
            }).inject( this.scrollNode );

            data.each( function( d, i ){
                var itemNode = new Element("div.meetingItemNode", {
                    "styles" : this.css.meetingItemNode,
                    "events" : {
                        click : function(){
                            this.obj.openMeeting( this.data );
                        }.bind({ obj : this, data : d })
                    }
                }).inject( this.meetingItemContainer );

                this.tooltipList = this.tooltipList || [];
                this.tooltipList.push( new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, itemNode, this.app, d, {
                        axis : "x",
                        hiddenDelay : 300,
                        displayDelay : 300
                    })
                );

                var colorNode = new Element("div.meetingItemColorNode", {
                    "styles" : this.css.meetingItemColorNode,
                    "text" : i+1
                }).inject( itemNode );

                var textNode = new Element("div.meetingItemTextNode", {
                    "styles" : this.css.meetingItemTextNode,
                    "text" : d.subject
                }).inject( itemNode );

                switch (d.status){
                    case "wait":
                        colorNode.setStyles({"background-color": "#4990E2"});
                        break;
                    case "processing":
                        colorNode.setStyles({"background-color": "#66CC7F"});
                        break;
                    case "completed":
                        //add attachment
                        colorNode.setStyles({"background-color": "#ccc"});
                        break;
                }
                if (d.myWaitAccept){
                    colorNode.setStyles({"background-color": "#F6A623"});
                }
                var y = itemNode.getSize().y ;
                colorNode.setStyle("margin-top", ( y - 20)/2 );
            }.bind(this));

            this.setScrollBar( this.scrollNode );
            if( callback )callback();
        }.bind(this));
    },
    trigger : function(){
        this.isHidden ? this.show( true ) : this.hide( true )
    },
    hide: function( isFireEvent ){
        var x = this.node.getSize().x - 9;
        var fx = new Fx.Morph(this.node, {
            "duration": "300",
            "transition": Fx.Transitions.Expo.easeOut
        });
        fx.start({
            //"opacity": 0
        }).chain(function(){
            this.isHidden = true;
            //this.node.setStyle("display", "none");
            this.node.setStyles({
                "right": "-"+x+"px"
            });
            this.trapezoid.setStyles( this.css.trapezoid_toLeft );
            //if(isFireEvent)this.app.fireEvent("resize");
        }.bind(this));
    },
    show: function( isFireEvent ){
        this.node.setStyles(this.css.node);
        this.trapezoid.setStyles( this.css.trapezoid_toRight );
        //var x = this.node.getSize().x - 8;
        //this.node.setStyles( "right", "-"+x+"px" );
        var fx = new Fx.Morph(this.node, {
            "duration": "500",
            "transition": Fx.Transitions.Expo.easeOut
        });
        this.app.fireAppEvent("resize");
        fx.start({
            "opacity": 1
        }).chain(function(){
            this.node.setStyles({
                //"position": "static",
                //"width": "auto"
                "right": "0px"
            });
            this.isHidden = false;
            //if(isFireEvent)this.app.fireEvent("resize");
        }.bind(this))
    },
    //show: function(){
    //    this.node.setStyles(this.css.configNode);
    //    var fx = new Fx.Morph(this.node, {
    //        "duration": "500",
    //        "transition": Fx.Transitions.Expo.easeOut
    //    });
    //    fx.start({
    //        "opacity": 1
    //    }).chain(function(){
    //        this.hideFun = this.hide.bind(this);
    //        this.app.content.addEvent("mousedown", this.hideFun);
    //    }.bind(this));
    //},
    //hide: function(){
    //    this.node.destroy();
    //    this.app.content.removeEvent("mousedown", this.hideFun);
    //    MWF.release(this);
    //},
    resetNodeSize: function(){
        var size = this.container.getSize();

        this.node.setStyle("height", size.y - 50 );
        this.trapezoid.setStyle("top", ( (size.y - 50)/2 - this.trapezoid.getSize().y/2 ));

        var y = size.y - 395;
        var meetContainerY = this.meetingItemContainer.getSize().y + 12;
        this.scrollNode.setStyle("height", Math.min( y, meetContainerY ) );
    },
    getSize : function(){
        //var size = this.node.getSize();
        //return {
        //    x : this.isHidden ? 9 : size.x,
        //    y : size.y
        //}
        return { x : 9, y : 0 }
    },
    showByType : function( type ){

    },
    reload : function(){
        this.destory();
        this.app.reload();
    },
    openMeeting : function( data ){
        var form = new MWF.xApplication.Meeting.MeetingForm(this, data, {}, {app:this.app});
        form.view = this.app;
        form.open();
    },
    destory : function(){
        this.tooltipList.each( function( t ){
            t.destory();
        });
        this.app.removeEvent("resize", this.resetNodeSizeFun );
        this.app.node.removeEvent("mousedown", this.hideFun);
        this.node.destory();
    }
});