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
        "style": "v10",
        "width": "800",
        "height": "300",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": false,
        "draggable": true,
        "closeAction": true,
        "scrollType": "window"
    },
    _createTableContent: function () {
        this.formAreaNode.setStyle('z-index', '100');
        this.formTopTextNode?.set( "text", this.lp.editBuilding );
        var html = `<div class='formTable'>
            <div item='name'></div>
            <div item='address'></div>
            <div style="padding-top: 30px;display: flex;justify-content: center;">
               <div item='saveAction' style='float:left;display:${(this.isEdited || this.isNew) ? "" : "none"};'></div>
               <div item='removeAction' style='float:left;display:${this.isEdited ? "" : "none"};'></div>
               <div item='cancelAction'></div>
            </div>`;
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                isEdited: this.isEdited || this.isNew,
                style : "v10", mvcStyle: "v10",
                itemTemplate: {
                    name: { type: 'oo-input', text : this.lp.name, notEmpty : true },
                    address: { type: 'oo-input', text : this.lp.address },
                    saveAction : { type : "oo-button", className : "inputOkButton", clazz: "mainColor_bg", value : this.lp.save, event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "oo-button", className : "inputCancelButton", appearance:'cancel', value : this.lp.delete , event : {
                        click : function( item, ev ){ this.removeBuilding(ev); }.bind(this)
                    } },
                    cancelAction : { type : "oo-button", className : "inputCancelButton", appearance:'cancel', value : this.lp.close , event : {
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
            this.fireEvent("postSave", [data]);
            this.close();
            if(view)view.reload();
        }.bind(this));
    },

    removeBuilding: function(e) {
        var info = this.app.lp.delete_building;
        info = info.replace(/{name}/g, this.data.name);
        var _self = this;
        this.app.confirm("warn", e, this.app.lp.delete_building_title, info, 300, 120, function(){
            _self.remove();
            this.close();
            _self.close();
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
            "<div item='containr' style='height:16px;line-height:16px;'><div style='font-size: 14px;color:#666;float:left; ' item='address'></div></div>";
        return html;
    },
    _customNode : function( node, contentNode ){
        var data = this.data;
        contentNode.getElement("[item='address']").set("text", (data.address ? data.address : this.lp.noAddress) );

        this.fireEvent("customContent", [contentNode, node])
    },
    loadActionBar : function(){
        if( MWF.AC.isMeetingAdministrator() ){
            //this.actionBar = new Element("div", {
            //    "styles": this.css.actionBar
            //}).inject(this.node);
            var container = this.node.getElement("[item='containr']");

            this.editAction = new Element("div.ooicon-edit", {
                styles: this.css.action_edit,
                title : this.lp.editAddress,
                events : {
                    mouseover : function(){
                        this.editAction.setStyles( this.css.action_edit_over );
                        this.editAction.addClass("mainColor_color");
                    }.bind(this),
                    mouseout : function(){
                        this.editAction.setStyles( this.css.action_edit );
                        this.editAction.removeClass("mainColor_color");
                    }.bind(this),
                    click : function(){
                        this.editBuilding( function(data){
                            this.node.getElement("[item='containr']").getFirst().set("text", (data.address ? data.address : this.lp.noAddress))
                        }.bind(this))
                    }.bind(this)
                }
            }).inject(container);

            this.removeAction = new Element("div.ooicon-delete", {
                styles: this.css.action_remove,
                title: this.lp.removeBuilding,
                events : {
                    mouseover : function(){
                        this.removeAction.setStyles( this.css.action_remove_over );
                        this.removeAction.addClass("mainColor_color");
                    }.bind(this),
                    mouseout : function(){
                        this.removeAction.setStyles( this.css.action_remove );
                        this.removeAction.removeClass("mainColor_color");
                    }.bind(this),
                    click : function( e ){
                        this.removeBuilding(e);
                    }.bind(this)
                }
            }).inject(container);
        }
    },
    editBuilding : function( save_callback ){
        var options = save_callback ? {
            "onPostSave" : save_callback
        } : {};
        var form = new MWF.xApplication.Meeting.BuildingForm(this,this.data, options, {app:this.app});
        form.view = this.view;
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
        "style": "v10",
        "width": "900",
        "height": "450",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": false,
        "draggable": true,
        "closeAction": true,
        "scrollType": "window"
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

        var html = `<div class='formTable'>
            <div item='name'></div>
            <div item='building'></div>
            <div style="display: flex;">
                <div item='floor' style="flex: 1;"></div>
                <div item='capacity' style="flex: 1;"></div>
            </div>
            <div style="display: flex;">
                <div item='roomNumber' style="flex: 1;"></div>
                <div item='phoneNumber' style="flex: 1;"></div>
            </div>
            <div item='deviceList'></div>
            <div style="display: flex;">
                <div item='available' style="flex: 1;"></div>
                <div item='orderNumber' style="flex: 1;"></div>
            </div>
            <div style='padding-top: 30px;display: flex;justify-content: center;'>
               <div item='saveAction' style='display:${( (this.isEdited || this.isNew) ? "" : "none")};'></div>
               <div item='editAction' style='display:${editEnable ? "" : "none"};'></div>
               <div item='removeAction' style='display:${this.isEdited ? "" : "none"};'></div>
               <div item='cancelAction'></div>
           </div>
           </div>`;
        this.formTableArea.set("html", html);

        var data = this.data || {};
        // this.buildingId = data.building;
        // if( this.buildingId ){
        //     this.getBuliding()
        // }
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "v10", mvcStyle: "v10",
                itemTemplate: {
                    name: { type:'oo-input', label : lp.name, notEmpty : true },
                    building: {
                        type:'oo-select', label : lp.building,  notEmpty : true, labelKey: 'text', valueKey: 'id',
                        // value : this.buildingName || "",
                        selectOption: ()=>{
                            return this.actions.listBuilding().then(function(json){
                                json.data.forEach(function(item){
                                    item.text = `${item.name}(${item.address})`;
                                })
                                return json.data;
                            });
                        },
                        onPostLoad:  (item)=>{
                            if( this.isEdited || this.isNew ){
                                var ooinput = item.items[0];
                                var addButton = new Element("oo-button", {
                                    'slot':'after-outer',
                                    style: "font-size:14px;width:5em;",
                                    text: this.lp.add
                                }).inject( ooinput );
                                addButton.addEventListener('click', (e)=>{
                                    var form = new MWF.xApplication.Meeting.BuildingForm(this,this.data, {
                                        onPostSave: ()=>{
                                            item.reload();
                                        }
                                    }, {app:this.app});
                                    form.create();
                                });
                            }
                        }
                    },
                    floor: { type: "oo-select", defaultValue: "1", notEmpty : true, label : lp.floor,
                        selectText : function(){
                            var floors = [];
                            for (var i=-2; i<=50; i++){
                                if( i != 0  )floors.push(i) //this.lp.floor
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
                    capacity: { type:'oo-input', notEmpty : true, dataType: "number", label : lp.capacity },
                    roomNumber: {type:'oo-input', label: lp.roomNumber},
                    orderNumber: {type:'oo-input', label: lp.orderNumber},
                    phoneNumber: {type:'oo-input', label: lp.phone},
                    deviceList: { type : "oo-checkgroup", label: lp.device,
                        value : this.data.device ? this.data.device.split("#") : "",
                        selectValue : function(){ return Object.keys(this.lp.device); }.bind(this),
                        selectText : function(){ return Object.values(this.lp.device); }.bind(this)
                    },
                    available: { type : "oo-radiogroup", defaultValue : "true", label: lp.available,
                        selectValue : [ "true", "false" ],
                        selectText : [ this.lp.enable, this.lp.disable ]
                    },
                    saveAction : { type : "oo-button", value : this.lp.save, className: "inputOkButton", event : {
                        click : function(){ this.save();}.bind(this)
                    } },
                    removeAction : { type : "oo-button", appearance : "cancel", value : this.lp.delete , className: "inputCancelButton", event : {
                        click : function( item, ev ){ this.removeRoom(ev); }.bind(this)
                    } },
                    editAction : { type : "oo-button", value : this.lp.editRoom , className: "inputOkButton", event : {
                        click : function(){ this.editRoom(); }.bind(this)
                    } },
                    cancelAction : { type : "oo-button", appearance : "cancel", value : this.lp.close , className: "inputCancelButton", event : {
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
    // getBuliding : function(){
    //     this.actions.getBuilding( this.buildingId, function(json){
    //         this.buildingName = json.data.name
    //     }.bind(this), null, false )
    // },
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
            this.close();
            view.reload();
        }.bind(this));
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
        data.available = data.available === "true";

        if (callback) callback(data);
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

        lp = this.lp.roomForm;

        var html =
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 16px;color:#333;float: left'>"+ this.lp.room +"</div>"+
            "</div>"+
            "<div style='font-size: 18px;color:#333;padding:0px 10px 15px 20px;' item='name'></div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+"' width='100'>"+ lp.building+":</td>" +
            "    <td style='"+valueStyle+"' item='building'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>" + lp.floor+":</td>" +
            "    <td style='"+valueStyle+"' item='floor'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.capacity +":</td>" +
            "    <td style='"+valueStyle+"' item='capacity'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.roomNumber +":</td>" +
            "    <td style='"+valueStyle+"' item='roomNumber'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.phone+":</td>" +
            "    <td style='"+valueStyle+"' item='phoneNumber'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+ lp.device +":</td>" +
            "    <td style='"+valueStyle+"' item='device'></td></tr>"+
            "<tr><td style='"+titleStyle+"'>"+ lp.available +":</td>" +
            "    <td style='"+valueStyle+"' item='available'></td></tr>"+
            "</table>";
        return html;
    },
    _customNode : function( node, contentNode ){
        var data = this.data;
        var lp = this.lp.roomForm;

        var device = [];
        ( data.device || "" ).split("#").each( function( d ){
            device.push( lp[d] );
        }.bind(this));

        contentNode.getElement("[item='name']").set("text", data.name );
        // contentNode.getElement("[item='building']").set("text", end );
        contentNode.getElement("[item='floor']").set("text", data.floor );
        contentNode.getElement("[item='capacity']").set("text", data.capacity );
        contentNode.getElement("[item='roomNumber']").set("text", data.roomNumber );
        contentNode.getElement("[item='phoneNumber']").set("text", data.phoneNumber );
        contentNode.getElement("[item='device']").set("text", device.join( "," ) );
        contentNode.getElement("[item='available']").set("text", ( !data.available ? this.lp.disable : this.lp.enable ) );

        this.fireEvent("customContent", [contentNode, node]);
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
        "style": "v10",
        "width": 900,
        "height": 780,
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasBottom": true,
        "draggable": true,
        "closeAction": true,
        "maxAction" : true,
        "buttonList": [],
        "scrollType": "window"
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
    getIdentity: function(){
        var user = layout.desktop.session.user;
        var list = (user.identityList || []).filter(function(i){ return i.major; });
        if( list.length )return list[0].distinguishedName;
        if( user.identityList && user.identityList.length ){
            return user.identityList[0].distinguishedName;
        }else{
            return "";
        }
    },
    _createTableContent: function () {
        var _self = this;
        if( layout.mobile ){
            this.formTableContainer.setStyles({
                "width" : "auto",
                "padding-left" : "20px",
                "padding-right" : "20px"
            });
        }else{
            this.formTableContainer.setStyle("padding-top", "20px");
        }
        var user = layout.desktop.session.user;
        this.userName = user.distinguishedName;
        this.userId = user.id;
        if( !layout.mobile ){
            if( this.isNew ){
                this.formTopTextNode?.set( "text", this.lp.addMeeting );
            }else if( this.isEdited ){
                this.formTopTextNode?.set( "text", this.lp.editMeeting );
                this.options.height = this.isShowInviteDelPersonList() ? 900 : 810;
                if( this.data.inviteDelPersonList && this.data.inviteDelPersonList.length )this.options.height += 40;
            }else{
                this.formTopTextNode?.set( "text", this.lp.metting );
                this.options.height = 730;
            }
            if( this.data.acceptPersonList && this.data.acceptPersonList.length )this.options.height += 40;
            if( this.data.rejectPersonList && this.data.rejectPersonList.length )this.options.height += 40;
        }

        if( this.isNew && !this.data.mode){
            this.data.mode = this.app.isAutoCreateOnlineRoom() ? "online" : "offline";
        }
        debugger;
        if( !layout.mobile ){
            if(this.isEdited || this.isNew){
                if( this.data.mode === "online" )this.options.height += 80;
            }else{
                if( this.data.mode === "online" )this.options.height += 40;
            }
            this.options.height = ""+this.options.height;
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

        var isEditer = this.userName === this.data.applicant || this.userId === this.data.applicant || MWF.AC.isMeetingAdministrator();
        var editEnable = ( !this.isEdited && !this.isNew && this.data.status === "wait" &&  isEditer );
        var isEditing = this.isNew || (this.isEdited  && this.data.status === "wait" &&  isEditer);

        var startImmediatelyEnable = false;
        if( editEnable && this.data.room){ //&& this.data.mode !== "online"
            if( MWF.AC.isMeetingAdministrator() ){
                startImmediatelyEnable = true;
            }else{
                if( this.userName === this.data.applicant || this.userId === this.data.applicant ){
                    if( !this.app.meetingConfig.process )startImmediatelyEnable = true;
                }
            }
        }

        var finishImmediatelyEnable = isEditer && !this.isEdited && !this.isNew && this.data.status === "processing" && this.data.room;

        this.isEditer = isEditer;
        this.editEnable = editEnable;
        this.isEditing = isEditing;
        this.startImmediatelyEnable = startImmediatelyEnable;
        this.finishImmediatelyEnable = finishImmediatelyEnable;

        var html = "<div item='qrCode' style='position: absolute;right:0px;top:-20px;width:150px;height:180px;z-index: 1;'></div>" +

            (( this.isShowCurrentUserDelPersonInfor() ) ?
                    "<div style='position: absolute;left:0px;top:-25px;height:20px;min-width:200px;color: rgb(246, 166, 35);'>"+this.lp.userDeleteInvitePerson+"</div>" : ""
            ) +

            "<div class='formTable'>" +
            "<div item='applicant'></div><div item='type'></div>";

        var isShowOnline = this.isEditing ? this.app.isOnlineAvailable() : (data.mode === "online" || data.roomLink || data.roomLink);
        if( isShowOnline ){
            html += `<div item='mode'></div>
                <div item='roomLink' style="display:${data.mode === 'online' ? '' : 'none'}"></div>
                <div item='roomId' style="display:${data.mode === 'online' ? '' : 'none'}"></div>`;
        }

        html += `<div item='dateInput'></div>
            <div style="display: flex;"><div item='beginTimeInput'></div><div item='endTimeInput'></div></div>
            <div style='display:${data.mode !== "online" ? "" : "" }' item='room'></div>
            <div item='hostPerson'></div>
            <div item='hostUnit'></div>
            
            <div item='inviteMemberList'></div>
            ${(!this.isNew && data.myWaitAccept) ? "<div style='padding-left: 132px;'><span item='acceptAction'></span><span item='rejectAction'></span></div>" : ""}
            <div item='acceptPersonList' style='display:${data.acceptPersonList?.length ?'':'none'}'></div>
            <div item='rejectPersonList' style='display:${data.rejectPersonList?.length?'':'none'}'></div>
            <div item='inviteDelPersonList' style='display:${this.isShowInviteDelPersonList()?'':'none'}'></div>
            
            <div style='display:${ this.isNew ? "none" : ""};' item='checkinPersonList'></div>
            <div item='externalPerson'></div>
            <div item='subject'></div>
            <div item='summary'></div>
            <div item="attachmentRow" class="formLine hide">
                <div class='formLabel'>${this.lp.meetingAttachment}</div>
                <div class="formValue" item='attachment'></div>
            </div>
            </div>`;
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
        this.attachmentRow = this.formTableArea.getElement("[item='attachmentRow']");
        this.attachmentArea = this.formTableArea.getElement("[item='attachment']");

        this.qrCodeArea = this.formTableArea.getElement("[item='qrCode']");

        var lp = this.lp;
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : layout.mobile ? "v10_mobile" : "v10",
                mvcStyle: "v10",
                itemTemplate: {
                    applicant : {  label: lp.applyPerson, type : "oo-org", orgType : "person", isEdited : false,
                        defaultValue : this.userName
                    },
                    mode: {
                        required: this.app.isOnlineAvailable(),
                        type: "oo-select", isEdited : isEditing,
                        label: lp.meetingMode,
                        selectValue : this.lp.meetingModeSelectValue || [],
                        selectText : this.lp.meetingModeSelectText || [],
                        event : {
                            change : function( item, ev ){
                                this.changeMode();
                            }.bind(this)
                        }
                    },
                    dateInput: {
                        required: true,
                        type: "oo-datetime", isEdited : isEditing, defaultValue: defaultDate, label: lp.beginDate,
                        attr : {'mode': 'date'},
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    beginTimeInput: {
                        required: true,
                        type: "oo-datetime", isEdited : isEditing, label: lp.time,
                        defaultValue: defaultBeginTime, attr : {'mode': 'time'},
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    endTimeInput: {
                        required: true,
                        type: "oo-datetime",  isEdited : isEditing, label: lp.to,
                        defaultValue: defaultEndTime, attr : {'mode': 'time', 'label-style':''},
                        event : {
                            change : function( item, ev ){
                                this.clearRoom();
                            }.bind(this)
                        }
                    },
                    // invitePersonList: { type: "oo-org", isEdited : this.isNew, orgType: ["identity","person"], count : 0, orgWidgetOptions : {
                    //     "onLoadedInfor": function(item){
                    //         // this.loadAcceptAndReject( item );
                    //     }.bind(this)
                    // }},
                    inviteMemberList: {
                        required: true,
                        type: "oo-org", isEdited : isEditing, orgType: ["identity","person","group","unit"], count : 0,
                        label: lp.invitePerson2
                    },
                    acceptPersonList: { label:lp.acceptPerson, type: "oo-org", isEdited : false, orgType: ["identity","person","group","unit"], count : 0, orgWidgetOptions : {}},
                    rejectPersonList: { label:lp.rejectPerson, type: "oo-org", isEdited : false, orgType: ["identity","person","group","unit"], count : 0, orgWidgetOptions : {}},
                    inviteDelPersonList: { label:lp.deleteInvitePerson2, type: "oo-org", isEdited : false, orgType: ["identity","person"], count : 0},
                    checkinPersonList : { label:lp.needSignInPerson, type: "oo-org", isEdited : false, orgType: ["identity","person"], count : 0},
                    subject: { required: true, type: "oo-input", label: lp.subject },
                    externalPerson :{type: "oo-textarea", label: lp.externalPerson},
                    room: {
                        required: !this.app.isOnlineAvailable(),
                        type: "oo-select", label: lp.selectRoom, grouplabelKey: 'label', labelKey: 'name', valueKey: 'id', childrenKey: 'roomList',
                        selectGroup:  ()=>{
                            var d = this.form.getItem('dateInput').getValue();
                            var bt = this.form.getItem('beginTimeInput').getValue();
                            var et = this.form.getItem('endTimeInput').getValue();
                            var start = d+" "+bt;
                            var completed = d+" "+et;
                            var p;
                            if( this.isEdited && this.data.room ){
                                p = o2.Actions.load("x_meeting_assemble_control").BuildingAction.listWithStartCompletedRoom( start, completed, this.data.room, this.data.id)
                            }else{
                                p = this.app.actions.listBuildingByRange(start, completed);
                            }
                            return p.then(function (json){
                                json.data.each(function(building){
                                    building.label = `${building.name}(${building.address})`
                                    building.roomList.each(function(room, i){
                                        room.disabled = !room.available || !room.idle;
                                    }.bind(this));

                                }.bind(this));
                                console.log(json.data)
                                return json.data;
                            });
                        }
                    },
                    roomLink: {
                        label: lp.meetingUrl, showIcon: 'no',
                        type: "oo-input", //this.app.isAutoCreateOnlineRoom() ? ( this.isNew ? "oo-input" : "innerText" ) : "oo-input",
                        attr: {
                            readonly: this.app.isAutoCreateOnlineRoom(),
                            placeholder: this.app.isAutoCreateOnlineRoom() ? this.lp.createOnSave: ""
                        },
                        onPostLoad:  (item)=>{
                            if( !this.isNew && item.getValue() ){
                                var ooinput = item.items[0];
                                ooinput.value = '';
                                new Element("a", {
                                    'slot':'before-inner-after',
                                    style: "font-size:14px;background:#f7f7f7;",
                                    href: this.data.roomLink,
                                    text: this.lp.openMeetingUrl,
                                    target: "_blank"
                                }).inject( ooinput );
                            }
                        }
                    },
                    roomId: {
                        label: lp.meetingNumber, showIcon: 'no',
                        type: "oo-input", //this.app.isAutoCreateOnlineRoom() ? ( this.isNew ? "oo-input" : "innerText" ) : "oo-input",
                        attr: {
                            readonly: this.app.isAutoCreateOnlineRoom(),
                            placeholder: this.app.isAutoCreateOnlineRoom() ? this.lp.createOnSave: ""
                        }
                    },
                    summary: {type: "oo-textarea", label:lp.meetingDescription},
                    hostPerson: { required: true, type : "oo-org", label:lp.hostPerson,
                        orgType : "identity",  count: 1, "defaultValue": this.getIdentity(), orgOptions: {"resultType": "person"}
                    },
                    hostUnit: { type : "oo-org", label:lp.hostUnit, orgType : "unit",  count: 1 },
                    type: {
                        label: lp.meetingType,
                        type : this.app.meetingConfig.typeList?.length ? "oo-select" : "oo-input",
                        selectValue : this.app.meetingConfig.typeList || [],
                        selectText : this.app.meetingConfig.typeList || [],
                        notEmpty: true
                    },
                    acceptAction : { type : "oo-button", value : this.lp.accept,
                        event : {  click : function( it, ev ){ this.accept(ev); }.bind(this) }
                    },
                    rejectAction : { type : "oo-button", value : this.lp.reject, appearance : "cancel", style: {'margin-left': "10px"},
                        event : {  click : function( it, ev ){ this.reject(ev) }.bind(this) }
                    }
                }
            }, this.app);
            this.form.load();

            // if( isEditing ){
            //     this.loadSelectRoom();
            // }else{
            //     this.loadSelectRoom_read();
            // }
            if( this.data.id )this.loadAttachment();

            if( !layout.mobile && isEditer && !this.isNew && !this.isEdited && data.mode !== "online"){
                this.loadQrCode();
            }else{
                this.qrCodeArea.destroy();
            }

            // if( this.data.mode === "online" ){
            //     if( !isEditing ){
            //         this.loadRoomUrl_read();
            //     }else if( this.app.isAutoCreateOnlineRoom() ){
            //         if( this.data.roomLink || this.data.roomId ){
            //             this.loadRoomUrl_read();
            //         }
            //     }
            // }
        }.bind(this), true);
    },
    _createBottomContent: function(){
        var editEnable = this.editEnable;
        var startImmediatelyEnable = this.startImmediatelyEnable;
        var finishImmediatelyEnable = this.finishImmediatelyEnable;
        var html = layout.mobile ?
            "<div style='display:contents;'>" +
            "       <div item='cancelAction' style='display:contents;'></div>"+
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "contents" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( editEnable ? "contents" : "none") +";'></div>"+
            "       <div item='startImmediatelyAction' style='float:left;display:"+ ( startImmediatelyEnable ? "contents" : "none") +";'></div>"+
            "       <div item='finishImmediatelyAction' style='float:left;display:"+ ( finishImmediatelyEnable ? "contents" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "contents" : "none") +";'></div>"+
            "   </div>" : "<div style='display:flex; justify-content: center;'>" +
            "       <div item='saveAction' style='float:left;display:"+ ( (this.isEdited || this.isNew) ? "" : "none") +";'></div>"+
            "       <div item='editAction' style='float:left;display:"+ ( editEnable ? "" : "none") +";'></div>"+
            "       <div item='startImmediatelyAction' style='float:left;display:"+ ( startImmediatelyEnable ? "" : "none") +";'></div>"+
            "       <div item='finishImmediatelyAction' style='float:left;display:"+ ( finishImmediatelyEnable ? "" : "none") +";'></div>"+
            "       <div item='removeAction' style='float:left;display:"+ ( this.isEdited ? "" : "none") +";'></div>"+
            "       <div item='cancelAction' style='"+( (this.isEdited || this.isNew || editEnable || startImmediatelyEnable || finishImmediatelyEnable ) ? "float:left;" : "float:right;margin-right:15px;")+"'></div>"+
            "   </div>";
        this.formBottomNode.set("html", html);
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.actionForm = new MForm(this.formBottomNode, {}, {
                isEdited: this.isEdited || this.isNew,
                style: layout.mobile ? "v10_mobile" : "v10",
                itemTemplate: {
                    saveAction: {
                        type: "oo-button", className: "inputOkButton", clazz: "mainColor_bg", value: this.lp.save, event: {
                            click: function () {
                                this.save();
                            }.bind(this)
                        }
                    },
                    removeAction: {
                        type: "oo-button", appearance: 'cancel', className: "inputCancelButton", value: this.lp.cancelMeeting, event: {
                            click: function (item, ev) {
                                this.cancelMeeting(ev);
                            }.bind(this)
                        }
                    },
                    editAction: {
                        type: "oo-button", className: "inputOkButton", clazz: "mainColor_bg", value: this.lp.editMeeting, event: {
                            click: function () {
                                this.editMeeting();
                            }.bind(this)
                        }
                    },
                    startImmediatelyAction: {
                        type: "oo-button", appearance: 'cancel', className: "inputCancelButton", value: this.lp.startMeetingImmediately, event: {
                            click: function () {
                                this.startImmediately();
                            }.bind(this)
                        }
                    },
                    finishImmediatelyAction: {
                        type: "oo-button", appearance: 'cancel', className: "inputCancelButton", value: this.lp.endMeetingImmediately, event: {
                            click: function () {
                                this.finishImmediately();
                            }.bind(this)
                        }
                    },
                    cancelAction: {
                        type: "oo-button", appearance: 'cancel', className: "inputCancelButton", value: this.lp.close, event: {
                            click: function (){
                                this.close();
                            }.bind(this)
                        }
                    }
                }
            }, this.app);
            this.actionForm.load();
            if(layout.mobile){
                this.formatMobileButton(this.formBottomNode.getFirst(), this.formAreaNode);
            }
        }.bind(this))
    },


    isShowInviteDelPersonList : function () {
        return this.isEdited && this.data.inviteDelPersonList && this.data.inviteDelPersonList.length > 0;
    },
    checkShowInviteDelPersonList: function(){
        var node = this.formTableArea.getElement('[item="inviteDelPersonList"]');
        node.setStyle( "display", this.isShowInviteDelPersonList() ? "" : "none" );
    },
    isShowCurrentUserDelPersonInfor : function () {
        return (!this.isEdited) && (this.data.inviteDelPersonList||[]).contains(this.userName) && !(this.data.invitePersonList||[]).contains(this.userName);
    },
    reloadinviteDelPerson: function(){
        if(!this.form)return;
        this.form.getItem("inviteDelPersonList").setValue(this.data.inviteDelPersonList);
        // var container = this.form.getItem("inviteDelPersonList").container;
        // container.empty();
        // this.data.inviteDelPersonList.each(function(item){
        //     var _self = this;
        //     if( item.split("@").getLast().toLowerCase() === "i" ){
        //         new MWF.widget.O2Identity(item, container, {"style": "room"});
        //     }else{
        //         new MWF.widget.O2Person(item, container, {"style": "room"});
        //     }
        // }.bind(this));
    },
    startImmediately : function(){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.editStartTime( this.data.id, {
            room : this.data.room,
            startTime : ( new Date() ).format("db")
        }, function () {
            this.app.notice( this.lp.startMeetingSucccess, "success");
            this.waitReload = true;
            this.close()
        }.bind(this))
    },
    finishImmediately : function(){
        o2.Actions.load("x_meeting_assemble_control").MeetingAction.editCompletedTime( this.data.id, {
            room : this.data.room,
            completedTime : ( new Date() ).format("db")
        }, function () {
            this.app.notice( this.lp.endMeetingSucccess, "success");
            this.waitReload = true;
            this.reload()
        }.bind(this))
    },
    getInvitePersonExclude : function(){
        var invitePersonList = this.invitePersonList || this.data.invitePersonList;
        var identityList = [];
        if( invitePersonList.length > 0 ){
            o2.Actions.load("x_organization_assemble_express").IdentityAction.listWithPerson({
                personList : invitePersonList
            }, function( json ){
                identityList = json.data ? json.data.identityList : [];
            }, null ,false );
            return ( identityList || [] ).concat(invitePersonList);
        }else{
            return [];
        }
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
                    width : "150px",
                    height : "150px"
                }
            }).inject( this.qrCodeArea );
            var div = new Element("div",{
                text : this.app.lp.printQrcode,
                styles : {
                    "color" : "#3c75b7",
                    "cursor" : "pointer",
                    "text-align": "center"
                },
                events : {
                    click : function(){
                        window.open(o2.filterUrl("../x_desktop/meetingQrPrint.html?meeting="+this.data.id), "_blank" );
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
        this.app.confirm("infor", e, this.app.lp.cancel_confirm_title, text, 400, 200, function(){
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
    changeMode: function(){
        var mode = this.form.getItem("mode").getValue();
        if( mode === "online" ){
            this.form.getItem("roomLink").node.setStyle("display", "");
            this.form.getItem("roomId").node.setStyle("display", "");
            // this.meetingRoomArea.getParent("tr").setStyle("display", "none");
        }else{
            this.form.getItem("roomLink").node.setStyle("display", "none");
            this.form.getItem("roomId").node.setStyle("display", "none");
            // this.meetingRoomArea.getParent("tr").setStyle("display", "");
        }
        //this.resetHeight("auto");
    },
    clearRoom : function(){
        // this.roomId = "";
        // debugger;
        this.form.getItem('room')?.reloadItemOptions();
    },
    loadSelectRoom_read : function(){
        // var roomId = this.data.room || this.options.room;
        // if (roomId){
        //     ( this.app.actions || this.actions ).getRoom(roomId, function(json){
        //         ( this.app.actions || this.actions ).getBuilding(json.data.building, function(bjson){
        //             this.roomId = roomId;
        //             this.meetingRoomArea.set("text", json.data.name+" ("+bjson.data.name+")");
        //         }.bind(this));
        //     }.bind(this));
        // }
    },

    loadAttachment: function(){
        this.attachmentRow.removeClass('hide');
        this.attachmentNode = new Element("div", {"styles": this.css.createMeetingAttachmentNode}).inject(this.attachmentArea);
        var attachmentContentNode = new Element("div", {"styles": this.css.createMeetingAttachmentContentNode}).inject(this.attachmentNode);
        MWF.require("MWF.widget.AttachmentController", function(){
            this.attachmentController = new MWF.widget.AttachmentController(attachmentContentNode, this, {
                "style": "v10", mvcStyle: "v10",
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
            debugger;
            this.app.notice(this.lp.meeting_saveSuccess, "success");
            if (!this.attachmentNode){
                this.loadAttachment();

                if( this.data.mode === "online" ){
                    o2.Actions.load("x_meeting_assemble_control").MeetingAction.get( this.data.id, function (json) {
                        this.data.roomLink = json.data.roomLink;
                        this.data.roomId = json.data.roomId;
                        if( this.data.mode === "online" && this.app.isAutoCreateOnlineRoom() ){
                            this.form.get('roomLink').reload();
                            this.form.get('roomId').reload();
                        }
                    }.bind(this))
                }

                //this.resetHeight("auto", 30);
            }
        }.bind(this));
    },
    _save: function(callback){
        this.getSaveData();
        var now = new Date();
        var errorText = "";
        if (!this.data.subject) {
            if( !errorText )this.formTableArea.getElement("[item='subject']").scrollIntoView(false);
            errorText +=this.lp.meeting_input_subject_error;
        }
        if (!this.data.hostPerson) {
            if( !errorText )this.formTableArea.getElement("[item='hostPerson']").scrollIntoView(false);
            errorText +=this.lp.meeting_input_hostPerson_error;
        }
        if( this.data.mode === "online" ){
            if ( !this.app.isAutoCreateOnlineRoom() && !this.data.roomLink && !this.data.roomId ) {
                if( !errorText )this.formTableArea.getElement("[item='roomLink']").scrollIntoView(false);
                errorText +=this.lp.meeting_input_url_number_error;
            }
        }else{
            if (!this.data.room) {
                if( !errorText )this.formTableArea.getElement("[item='room']").scrollIntoView(false);
                errorText +=this.lp.meeting_input_room_error;
            }
        }
        if (!this.data.inviteMemberList || !this.data.inviteMemberList.length){
            if( !errorText && this.formTableArea.getElement("[item='inviteMemberList']")){
                this.formTableArea.getElement("[item='inviteMemberList']").scrollIntoView(false);
            }
            errorText +=this.lp.meeting_input_person_error;
        }
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
        debugger;
        this.data = this.form.getResult(false,null,false,false,true);

        if( this.onlineRoomLink )this.data.roomLink = this.onlineRoomLink;
        if( this.onlineRoomId )this.data.roomId = this.onlineRoomId; //在线会议室ID

        // this.data.room = this.roomId;
        if( this.invitePersonList ){
            this.data.invitePersonList = this.invitePersonList;
        }
        if( o2.typeOf( this.data.hostPerson ) === "array" ){
            this.data.hostPerson = this.data.hostPerson[0];
        }
        if( o2.typeOf( this.data.hostUnit ) === "array" ){
            this.data.hostUnit = this.data.hostUnit[0];
        }

        var d = this.data.dateInput;
        // if( this.isNew ) {
        var startTime = d + " " + this.data.beginTimeInput + ":0";
        var completedTime = d + " " + this.data.endTimeInput + ":0";
        var startTimeDate = Date.parse(startTime);
        var completedTimeDate = Date.parse(completedTime);

        this.data.startTime = startTime;
        this.data.completedTime = completedTime;
        this.data.startTimeDate = startTimeDate;
        this.data.completedTimeDate = completedTimeDate;
        // }

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
        if( this.waitReload )this.view.reload();
        delete this;
    }
});

MWF.xApplication.Meeting.MeetingTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        var fun = function () {
            this.loadRoom( function(){
                this.loadInvite();
                this.loadAttachment();
                if(callback)callback();
            }.bind(this) );
        }.bind(this)
        if( this.options.isResetData ){
            if( this.data && this.data.id ){
                ( this.app.actions || this.actions ).getMeeting( this.data.id, function( json ){
                    this.data = json.data;
                    fun();
                }.bind(this))
            }else{
                fun();
            }
        }else{
            fun();
        }
    },
    _getHtml : function(){
        var data = this.data;
        var titleStyle = "font-size:14px;color:#333";
        var valueStyle = "font-size:14px;color:#666;padding-right:20px";

        var color = "#ccc";
        switch (data.status){
            case "wait":
                color = "#51B749";
                break;
            case "processing":
                color = "#5484ED";
                break;
            case "applying":
                color = "#F9905A";
                break;
            case "completed":
                color = "#666";
                break;
        }
        if (data.myWaitAccept){
            color = "#F6A623";
        }
        if (data.confilct){
            color = "#FBD75B";
        }

        debugger;
        var deletedInfor = "";
        this.userName = layout.desktop.session.user.distinguishedName;
        if((this.data.inviteDelPersonList||[]).contains(this.userName) && !(this.data.invitePersonList||[]).contains(this.userName)){
            deletedInfor = "<div style='position: absolute;left:20px;top:5px;height:20px;min-width:200px;color: rgb(246, 166, 35);'>"+this.lp.userDeleteInvitePerson+"</div>";
        }
        var html = deletedInfor +
            "<div style='overflow: hidden;padding:15px 20px 20px 10px;height:16px;line-height:16px;'>" +
            "   <div style='font-size: 12px;color:#666; float: right' item='applicant'></div>" +
            "   <div style='float: left' item='meetingFlag'></div>"+
            "   <div style='font-size: 16px;color:#333;float: left' item='type'></div>"+
            "</div>"+
            "<div style='font-size: 18px;color:#333;padding:0px 10px 15px 20px;overflow:hidden;' item='subject'></div>"+
            "<div style='height:1px;margin:0px 20px;border-bottom:1px solid #ccc;'></div>"+
            "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
            "<tr><td style='"+titleStyle+";' width='70'>"+this.lp.meetingTime+":</td>" +
            "    <td style='"+valueStyle+";color:"+ color +"' item='meetingTime'></td></tr>";

        if( this.data.mode === "online" ){
            html += "<tr><td style='"+titleStyle+"'>"+this.lp.meetingUrl +":</td>" +
                "    <td style='"+valueStyle+"' item='roomLink'></td></tr>" +
                "<tr><td style='"+titleStyle+"'>"+this.lp.meetingNumber +":</td>" +
                "    <td style='"+valueStyle+"' item='roomId'></td></tr>";
        }

        if( this.data.room && this.data.room !== "noRoom" ){ //this.data.mode !== "online"
            html += "<tr><td style='"+titleStyle+"'>"+this.lp.selectRoom +":</td>" +
                "    <td style='"+valueStyle+"' item='meetingRoom'></td></tr>";
        }

        html += "<tr><td style='"+titleStyle+"'>"+this.lp.invitePerson2+":</td>" +
            "    <td style='"+valueStyle+"' item='invitePerson'></td></tr>" +
            "<tr><td style='"+titleStyle+"'>"+this.lp.meetingDescription+":</td>" +
            "    <td style='"+valueStyle+"' item='description'></td></tr>";

        if( this.data.hostPerson ){
            html += "<tr><td style='"+titleStyle+"'>"+this.lp.hostPerson+":</td>" +
                "    <td style='"+valueStyle+"' item='hostPerson'></td></tr>";
        }

        if( this.data.hostUnit ){
            html += "<tr><td style='"+titleStyle+"'>"+this.lp.hostUnit+":</td>" +
                "    <td style='"+valueStyle+"' item='hostUnit'></td></tr>";
        }

        if( !this.options.isHideAttachment ){
            html += "<tr><td style='"+titleStyle+"'>"+this.lp.meetingAttachment+":</td>" +
                "    <td style='"+valueStyle+"' item='attachment'></td></tr>";
        }

        html += "</table>";
        return html;
    },
    setItemValue: function( contentNode, name, value ){
        var itemNode = contentNode.getElement("[item='"+name+"']");
        if(itemNode)itemNode.set("text", value );
    },
    _customNode : function( node, contentNode ){
        var data = this.data;

        var persons = [];
        data.invitePersonList.each( function( p ){
            persons.push(p.split("@")[0] )
        }.bind(this));

        var beginDate = Date.parse(data.startTime);
        var endDate = Date.parse(data.completedTime);
        var dateStr = beginDate.format(this.app.lp.dateFormatDay);
        var beginTime = this.getString( beginDate.getHours() ) + ":" + this.getString( beginDate.getMinutes() );
        var endTime = this.getString( endDate.getHours() ) + ":" + this.getString( endDate.getMinutes() );
        var meetingTime = dateStr + " " +  beginTime + "-" + endTime;
        var description = (data.description || "")+(data.summary || "");


        this.setItemValue(contentNode, "roomLink",  data.roomLink );
        this.setItemValue(contentNode, "roomId",  data.roomId );
        this.setItemValue(contentNode, "type", (this.data.type || this.lp.meetingDetail));
        this.setItemValue(contentNode, "applicant",  this.lp.applyPerson  +":" + data.applicant.split("@")[0] );
        this.setItemValue(contentNode, "subject",  data.subject );
        this.setItemValue(contentNode, "meetingTime",  meetingTime );
        this.setItemValue(contentNode, "subject",  data.subject );
        this.setItemValue(contentNode, "invitePerson", persons.join(",") );
        this.setItemValue(contentNode, "description",  description );
        if(this.data.hostPerson)this.setItemValue(contentNode, "hostPerson",  this.data.hostPerson.split("@")[0] );
        if(this.data.hostUnit)this.setItemValue(contentNode, "hostUnit",  this.data.hostUnit.split("@")[0] );

        var meetingFlagArea = contentNode.getElement("[item='meetingFlag']");
        if(meetingFlagArea){
            if( data.mode === "online" ){
                new Element("div.mainColor_bg", {
                    style: "font-size:12px; line-height:22px; text-align:center; height:22px; width:22px;border-radius:22px; background:#4A90E2; color:#fff;margin-right:5px;margin-top:-4px;",
                    text: this.lp.netMeetingAbb
                }).inject( meetingFlagArea )
            }
        }

        if( this.data.mode === "online" )this.loadRoomLink();

        this.fireEvent("customContent", [contentNode, node]);
    },
    getString : function( str ){
        var s = "00" + str;
        return s.substr(s.length - 2, 2 );
    },
    loadRoomLink: function(){
        var node;
        if( this.data.roomLink ){
            node = this.node.getElement("[item='roomLink']");
            node.empty();
            new Element("a", {
                style: "font-size:13px;",
                href: this.data.roomLink,
                text: this.lp.openMeetingUrl,
                target: "_blank"
            }).inject( node );
        }
        // if( this.data.roomId ){
        //     this.app.isCopyEnable().then(function(flag){
        //         if( flag ){
        //             node = this.node.getElement("[item='roomId']");
        //             node.empty();
        //             new Element("span", { text: this.data.roomId, name: "roomId" }).inject( node );
        //             new Element("div", {
        //                 "text": this.lp.copy,
        //                 "styles": this.app.css.inputDenyButton,
        //                 "events": {
        //                     "click": function (){ this.app.copyTextToClipboard(this.data.roomId) }.bind(this)
        //                 }
        //             }).inject( node );
        //         }
        //     }.bind(this))
        //}
    },
    loadRoom: function( callback ){
        var area = this.node.getElement("[item='meetingRoom']");
        if (area && this.data.room && this.data.room !== "noRoom"){
            this.app.actions.getRoom(this.data.room, function(json){
                this.app.actions.getBuilding(json.data.building, function(bjson){
                    area.set("text", json.data.name+" ("+bjson.data.name+")");
                    if( callback )callback();
                }.bind(this));
            }.bind(this));
        }else{
            if( callback )callback();
        }
    },
    loadInvite : function(){
        this.O2PersonList = [];
        var area = this.node.getElement("[item='invitePerson']");
        area.empty();
        var inviteList;
        if( this.data.inviteMemberList && this.data.inviteMemberList.length ){
            inviteList = this.data.inviteMemberList;
        }else{
            inviteList = this.data.invitePersonList || [];
        }
        this.loadOrgWidget(inviteList, area, {"onLoadedInfor": function(item){
            // this.loadAcceptAndReject( item );
        }.bind(this)})
    },
    loadOrgWidget: function(value, node, opt){
        this.O2PersonList = [];
        var options = Object.merge({"lazy":true, "style": "room"}, opt);
        MWF.require("MWF.widget.O2Identity", null, false);
        value.each(function( v ){
            var data;
            var distinguishedName;
            if( typeOf(v) === "string" ){
                distinguishedName = v;
                if( distinguishedName.indexOf("@") > 0 ){
                    data = {
                        "distinguishedName" : distinguishedName,
                        "name": distinguishedName.split("@")[0]
                    }
                }else{
                    data = {
                        "id" : distinguishedName,
                        "name": distinguishedName.split("@")[0]
                    }
                }
            }else{
                distinguishedName = v.distinguishedName || v.name || "";
                if( !v.name )v.name = distinguishedName.split("@")[0];
                data = v;
            }
            var flag = distinguishedName.substr(distinguishedName.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    var widget = new MWF.widget.O2Identity( data, node, options );
                    break;
                case "p":
                    var widget = new MWF.widget.O2Person(data, node, options);
                    break;
                case "u":
                    var widget = new MWF.widget.O2Unit(data, node, options);
                    break;
                case "g":
                    var widget = new MWF.widget.O2Group(data, node, options);
                    break;
                default:
                    var orgType = this.options.orgType;
                    var t = ( typeOf( orgType ) == "array" && orgType.length == 1 ) ? orgType[0] : orgType;
                    t = typeOf( t ) == "string" ? t.toLowerCase() : "";
                    if( t == "identity" ){
                        var widget = new MWF.widget.O2Identity( data, node, options );
                    }else if( t == "person" ){
                        var widget = new MWF.widget.O2Person(data, node, options);
                    }else if( t == "unit" ){
                        var widget = new MWF.widget.O2Unit(data, node, options);
                    }else if( t == "group" ){
                        var widget = new MWF.widget.O2Group(data, node, options);
                    }
            }
            widget.field = this;
            this.O2PersonList.push( widget );
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
                    "background": "url(../x_component_Template/$MPopupForm/meeting/icon/accept.png) no-repeat center center"
                }}).inject(item.node, "top");
                if(item.inforNode)new Element("div", {
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
                    "background": "url(../x_component_Template/$MPopupForm/meeting/icon/reject.png) no-repeat center center"
                }}).inject(item.node, "top");
                if(item.inforNode)new Element("div", {
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
            "mouseover": function(){
                var styleName = "attachmentNode_"+this.controller.options.listStyle+"_over";
                if (!this.isSelected) this.node.setStyles(this.css[styleName])
            }.bind(this),
            "mouseout": function(){
                var styleName = "attachmentNode_"+this.controller.options.listStyle;
                if (!this.isSelected) this.node.setStyles(this.css[styleName])
            }.bind(this),
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

        this.path = "../x_component_Meeting/$Common/default/meetingarea/";
        this.cssPath = "../x_component_Meeting/$Common/default/meetingarea/css.wcss";
        this._loadCss();
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.meetingNode}).inject( this.container );
        this.node.addEvents({
            mouseenter : function(){
                this.node.setStyles( this.css.meetingNode_over );
                this.subjectNode.setStyles( this.css.meetingSubjectNode_over );
                this.subjectNode.addClass("mainColor_color");
            }.bind(this),
            mouseleave : function(){
                this.node.setStyles( this.css.meetingNode );
                this.subjectNode.setStyles( this.css.meetingSubjectNode );
                this.subjectNode.removeClass("mainColor_color");
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
                this.colorNode.setStyles({"background-color": "#51B749"});
                this.timeNode.setStyles({"color": "#51B749"});
                break;
            case "processing":
                this.colorNode.setStyles({"background-color": "#5484ED"});
                this.timeNode.setStyles({"color": "#5484ED"});
                break;
            case "completed":
                //add attachment
                this.colorNode.setStyles({"background-color": "#FBD75B"});
                this.timeNode.setStyles({"color": "#FBD75B"});
                break;

            case "applying":
                //add attachment
                this.colorNode.setStyles({"background-color": "#F9905A"});
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
                this.editAction = new Element("div.ooicon-edit", {
                    styles: this.css.action_edit,
                    events : {
                        mouseover : function(){
                            this.editAction.setStyles( this.css.action_edit_over );
                            this.editAction.addClass("mainColor_color");
                        }.bind(this),
                        mouseout : function(){
                            this.editAction.setStyles( this.css.action_edit );
                            this.editAction.removeClass("mainColor_color");
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

                this.removeAction = new Element("div.ooicon-delete", {
                    styles: this.css.action_remove,
                    events : {
                        mouseover : function(){
                            this.removeAction.setStyles( this.css.action_remove_over );
                            this.removeAction.addClass("mainColor_color");
                        }.bind(this),
                        mouseout : function(){
                            this.removeAction.setStyles( this.css.action_remove );
                            this.removeAction.removeClass("mainColor_color");
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
            this.acceptAction = new Element("div.ooicon-check_outline", {
                styles: this.css.action_accept,
                title : this.app.lp.accept,
                events : {
                    mouseover : function(){
                        this.acceptAction.setStyles( this.css.action_accept_over );
                        this.acceptAction.addClass("mainColor_color");
                    }.bind(this),
                    mouseout : function(){
                        this.acceptAction.setStyles( this.css.action_accept );
                        this.acceptAction.removeClass("mainColor_color");
                    }.bind(this),
                    click : function( e ){
                        this.accept(e);
                        e.stopPropagation();
                    }.bind(this)
                }
            }).inject(this.actionBar);

            this.rejectAction = new Element("div.ooicon-process-cancel", {
                styles: this.css.action_reject,
                title : this.app.lp.reject,
                events : {
                    mouseover : function(){
                        this.rejectAction.setStyles( this.css.action_reject_over );
                        this.rejectAction.addClass("mainColor_color");
                    }.bind(this),
                    mouseout : function(){
                        this.rejectAction.setStyles( this.css.action_reject );
                        this.rejectAction.removeClass("mainColor_color");
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
    loadTooltip : function( isHideAttachment, isResetData ){
        this.tooltip = new MWF.xApplication.Meeting.MeetingTooltip(this.app.content, this.node, this.app, this.data, {
            axis : "x",
            hiddenDelay : 300,
            displayDelay : 300,
            isHideAttachment : isHideAttachment,
            isResetData : isResetData
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
        this.app.confirm("infor", e, this.app.lp.cancel_confirm_title, text, 400, 200, function(){
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
        this.cssPath = "../x_component_Meeting/$Common/"+this.options.style+"/sidebar/css.wcss";
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
            "   <div class='statusIconStyle' style='background-color:#51B749'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.wait+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:#5484ED'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.progress+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div class='statusIconStyle' style='background-color:#F6A623'></div>" +
            "   <div class = 'statusTextStyle'>"+this.lp.config.invite+"</div></div>" +
            "</div>"+

            "<div class = 'statusStyle'>"+
            "   <div  class='statusIconStyle' style='background-color:#FBD75B'></div>" +
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
                        colorNode.setStyles({"background-color": "#51B749"});
                        break;
                    case "processing":
                        colorNode.setStyles({"background-color": "#5484ED"});
                        break;
                    case "completed":
                        //add attachment
                        colorNode.setStyles({"background-color": "#FBD75B"});
                        break;
                    case "applying":
                        //add attachment
                        colorNode.setStyles({"background-color": "#F9905A"});
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

