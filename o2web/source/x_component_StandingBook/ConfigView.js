o2.require("o2.widget.Paging", null, false);
o2.requireApp("StandingBook", "Common", null, false);

MWF.xApplication.StandingBook.ConfigView = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_StandingBook/$ConfigView/";

        this.app = app;
        this.node = $(node);
        this.container = new Element("div").inject(this.node);

        this.load();
    },
    load: function(){
        var url = this.path+this.options.style+"/view.html";
        this.container.loadHtml(url, {"bind": {
                "lp": this.app.lp
                // "data": json.data
            }, "module": this}, function(){
                this.configArea.getParent().setStyle("height", "100%");
                this.configArea.loadCss(this.path+this.options.style+"/style.css");
                this.loadCategoryList();
                this.loadCategoryForm();
                this.showEmpty();
            }.bind(this)
        );
    },
    loadCategoryList : function( options ){
        this.categoryList = new MWF.xApplication.StandingBook.ConfigList(this, this.menuContent, {
            onPostLoad : function(){

            }.bind(this)
        } );
    },
    showEmpty: function(){
        var html =
            '<div class="listNoData">'+
            '   <div style="font-size:48px;color:#ddd;"><i class="o2icon-config"></i></div>'+
            '   <div class="listNoDataText">'+this.app.lp.noConfigNote+'</div>'+
            '</div>';
        this.contentArea.set("html", html);
    },
    loadCategoryForm: function () {

    },
    returnIndex: function () {
        this.app.reload();
    },
    createConfigItem: function () {
        this.categoryList.newConfigItem();
    },
    recordStatus: function () {
        if( this.categoryList.currentConfigItem && this.categoryList.currentConfigItem.data ){
            return {
                "view": "config",
                "config": this.categoryList.currentConfigItem.data.id
            }
        }else{
            return {
                "view": "config"
            }
        }
    }
});

MWF.xApplication.StandingBook.ConfigList = new Class({
    Implements: [Options, Events],
    options : {
        currentCategoryId : "",
        columnId : ""
    },
    initialize: function(explorer, node, options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.node = $(node);

        this.configArr = [];
        this.configObj = {};
        this.currentConfigItem = null;

        this.load();
    },
    load: function(){
        var self = this;

        o2.Actions.load("x_custom_index_assemble_control").RevealAction.listEditable( function( json ){
            // json.data = [
            //     {
            //         id	: "1111111",
            //         name: "184-酒店场景技术支撑服务审批台账",
            //         enable: true,
            //         orderNumber:1
            //     },
            //     {
            //         id	: "222222",
            //         name: "政企05-183-政企综合审批流程台账",
            //         enable: true,
            //         orderNumber:1
            //     },
            //     {
            //         id	: "3333333333",
            //         name: "市场21-240-酬金数据制表稽核申请",
            //         enable: false,
            //         orderNumber:1
            //     }
            // ];
            json.data.each(function(cData){

                this.loadConfigByData( cData );

            }.bind(this));
            this.fireEvent("postLoad");
        }.bind(this),function(){
            this.fireEvent("postLoad");
        }.bind(this), true)

    },
    getCurrentNode : function(){
        return this.currentNode;
    },
    getConfigNodes : function(){
        var nodes = [];
        this.configArr.each( function( config ){
            nodes.push( config.node );
        }.bind(this) );
        return nodes;
    },
    cancelCurrentNode : function(){
        if( this.currentNode ){
            this.currentNode.removeClass("mainColor_bg_opacity");
            this.currentNode.removeClass("configNaviNode_selected");
            this.currentNode.removeClass("configNaviNode_over");
            this.currentNode.removeClass("mainColor_color");
            this.currentConfigItem._hideActions();
            this.currentNode = null;
        }
    },
    setCurrentConfigById : function( configId ){
        if( configId && this.configObj[configId]){
            this.setCurrentConfigItem( this.configObj[configId] );
        }
    },
    setCurrentConfigItem : function( config ){
        if( this.configForm && this.configForm.options.isEdited ){
            this.app.notice( this.app.lp.saveNotice, "error" );
            return false;
        }

        this.cancelCurrentNode();

        this.currentConfigItem = config;
        this.currentNode = config.node;
        config.node.addClass("mainColor_bg_opacity");
        config.node.addClass( "configNaviNode_selected" );
        config.node.removeClass("configNaviNode_over");
        config.node.addClass("mainColor_color");
        config._showActions();
        // this.currentTimeout = setTimeout( function(){
        //     config._showActions();
        //     this.currentTimeout = null;
        // }.bind(this), 100 );
        this.loadForm(config);
    },
    loadForm: function(config){
        this.explorer.contentArea.empty();
        this.configForm = new MWF.xApplication.StandingBook.ConfigForm( this.explorer.contentArea, config, {
            isNew: config.options.isNew,
            isEdited: config.options.isNew
       });
    },
    destroyConfig : function( config ){
        if( config.options.isNew ){
        }else{
            var id = config.data.id;
            delete this.configObj[id];
            var idx = this.configArr.indexOf( config );
            if( idx > -1 ){
                this.configArr.splice( idx,1);
            }
        }
        this.currentConfigItem = null;
        this.currentNode = null;
        this.configForm = null;
        config.destroy();
    },
    loadConfigByData: function( cData, relativeNode, relativePosition, callback ){
        var config = new MWF.xApplication.StandingBook.ConfigItem( this, this.node, cData, {
            relativeNode : relativeNode,
            relativePosition: relativePosition
        });
        this.configObj[cData.id] = config;
        this.configArr.push( config );
        if( callback )callback( config );
        if( this.options.currentConfigId && this.options.currentConfigId === cData.id ){
            this.setCurrentConfigItem( config );
            this.options.currentConfigId = "";
        }
    },
    loadConfigById: function( id, relativeNode, relativePosition, callback ){
        o2.Actions.load("x_custom_index_assemble_control").RevealAction.get( id , function( json ){
            var cData = json.data;
            var config = new MWF.xApplication.StandingBook.ConfigItem( this, this.node, cData, {
                relativeNode: relativeNode,
                relativePosition: relativePosition
            });
            this.configObj[cData.id] = config;
            this.configArr.push( config );
            if( callback )callback( config );
            if( this.options.currentConfigId && this.options.currentConfigId == cData.id ){
                this.setCurrentConfigItem( config );
                this.options.currentConfigId = "";
            }
        }.bind(this) )
    },
    adjustSeq : function( async ){
        var itemNodes = this.node.getElements( ".configNaviNode");
        itemNodes.each( function( itemNode, i ){
            var config = itemNode.retrieve("config");
            if( !config.options.isNew ){
                var data = config.data;
                var index = "000" + i; //(itemNodes.length - i);
                data.configSeq = index.substr( index.length-3 ,3);
                o2.Actions.load("x_custom_index_assemble_control").RevealAction.edit(  data.id, data, null, null, async === false ? false : true );
            }
        })
    },
    newConfigItem : function( relativeNode, positon ){
        if( this.configForm && this.configForm.options.isEdited ){
            this.app.notice( this.app.lp.saveNotice, "error" );
            return false;
        }
        var configItem = new MWF.xApplication.StandingBook.ConfigItem( this, this.node, {}, {
            "isNew" : true,
            "relativeNode" : relativeNode,
            "relativePosition" : positon
        });
        this.setCurrentConfigItem( configItem );
        return configItem;
    }
});

MWF.xApplication.StandingBook.ConfigItem = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isNew" : false,
        "relativeNode" : null,
        "relativePosition" : "bottom",
        "actions_edit" : [
            {
                "name": "saveConfig",
                "class": "o2icon-save",
                // "icon": "save.png",
                // "icon_over": "save_over.png",
                // "unselectedIcon" : "save_over.png",
                // "unselectedIcon_over" : "save_unselected_over.png",
                "event": "click",
                "action": "saveConfig",
                "title": MWF.xApplication.StandingBook.LP.save
            },
            {
                "name": "cancel",
                "class": "o2icon-forbid",
                // "icon": "cancel.png",
                // "icon_over": "cancel_over.png",
                // "unselectedIcon" : "cancel_over.png",
                // "unselectedIcon_over" : "cancel_unselected_over.png",
                "event": "click",
                "action": "cancel",
                "title": MWF.xApplication.StandingBook.LP.cancelEdit
            }
        ],
        "actions_read": [
            // {
            //     "name": "editConfig",
            //     "icon": "edit.png",
            //     "icon_over": "edit_over.png",
            //     "event": "click",
            //     "action": "editConfig",
            //     "title": MWF.xApplication.StandingBook.LP.edit
            // },
            // {
            //     "name": "insertCateogryBefore",
            //     "class": "o2icon-insert",
            //     "event": "click",
            //     "action": "insertCateogryBefore",
            //     "title": MWF.xApplication.StandingBook.LP.insertBefore
            // },
            // {
            //     "name": "insertCateogryAfter",
            //     "class": "o2icon-after",
            //     "event": "click",
            //     "action": "insertCateogryAfter",
            //     "title": MWF.xApplication.StandingBook.LP.insertAfter
            // },
            {
                "name": "deleteConfig",
                "class": "o2icon-delete",
                // "icon": "trash.png",
                // "icon_over": "trash_gray.png",
                "event": "click",
                "action": "deleteConfig",
                "title": MWF.xApplication.StandingBook.LP.delete
            }
            // {
            //     "name": "moveConfig",
            //     "class": "o2icon-sort",
            //     "styles": {"font-size":"16px"},
            //     "event": "click",
            //     "action": "moveConfig",
            //     "title": MWF.xApplication.StandingBook.LP.move
            // }
        ]
    },
    initialize: function ( configList, container, data, options) {
        this.setOptions(options);
        this.configList = configList;
        this.explorer = configList.explorer;
        this.app = configList.app;
        this.container = $(container);
        this.data = data;
        this.lp = this.app.lp;
        this.configViewArr = [];
        this.configViewObj = {};

        this.load();
    },
    load: function( ){
        var _self = this;
        this.node = new Element("div.configNaviNode").inject(
            this.options.relativeNode || this.container, this.options.relativePosition || "bottom"
        );
        this.node.store("config", this);

        this.textNode = new Element("div.configNaviTextNode", {
            "text": this.options.isNew ? this.lp.isNewing : this.data.name
        }).inject(this.node);
        if( this.data.enable === false ){
            this.textNode.addClass( "configNaviTextNode_disabled" )
        }

        this.node.addEvents({
            "mouseover": function(){
                if (_self.configList.getCurrentNode() !=this && !_self.configList.isOnDragging ){
                    this.addClass("configNaviNode_over").addClass("mainColor_bg_opacity")
                }
            },
            "mouseout": function(){
                if (_self.configList.getCurrentNode() !=this){
                    this.removeClass("configNaviNode_over").removeClass("mainColor_bg_opacity")
                }
            },
            "click" : function(){
                if( !_self.configList.isOnDragging ){
                    _self.setCurrentNode(this);
                }
            }
        });

        this.loadSeparatNode();
        this.createIconAction();
        // if( this.options.isNew ){
        //     this.editConfig();
        // }
    },
    setText: function(text){
        this.textNode.set("text", text);
    },
    setEnable: function(enable){
        if( enable ){
            this.textNode.removeClass("configNaviTextNode_disabled")
        }else{
            this.textNode.addClass("configNaviTextNode_disabled")
        }
    },
    loadSeparatNode : function(){
        this.separatNode = new Element("div.separatNode").inject(this.node, "after");
    },
    destroy: function(){
        this.node.destroy();
        this.separatNode.destroy();
        delete this;
    },
    saveConfig : function(){
        var d = this.data || {};
        if( this.options.isNew ){
            d.isNew = this.options.isNew;
        }
        if( this.editMode && this.input ){
            var value = this.input.get("value");
            if( value === ""  ){
                this.app.notice( MWF.xApplication.StandingBook.LP.inputConfigNotice,"error");
                return;
            }else{
                d.enable = true;
                d.name = value;
            }
        }

        var p;
        if( this.options.isNew ){
            p =  o2.Actions.load("x_custom_index_assemble_control").RevealAction.create(d);
        }else{
            p =  o2.Actions.load("x_custom_index_assemble_control").RevealAction.edit(d.id, d);
        }
        p.then(function (json) {
            this.configList.loadConfigById( json.data.id, this.node, "before", function( config ){
                this.configList.setCurrentConfigItem( config );
                if(this.options.isNew)this.configList.adjustSeq(false);
                this.configList.destroyConfig( this );
            }.bind(this));
        }.bind(this))
    },
    cancel : function(){
        if( this.options.isNew ){
            this.destroy();
            this.explorer.showEmpty();
        }else{
            this.editMode = false;
            this.input.destroy();
            this.textNode.setStyle("display","");
            this._showActions();
        }
    },
    editConfig : function(){
        this.textNode.setStyle("display","none");
        this.editMode = true;
        this.input = new Element("input.configInput", {
            "type": "text",
            "value" : this.data.name || "",
            "placeholder": MWF.xApplication.StandingBook.LP.inputName
        }).inject(this.node, "top");
        this.input.addEvents( {
            "click" : function(ev){
                this._showActions(true);
                if( this.configList.currentConfigItem != this ){
                    this.setCurrentNode();
                }
                ev.stopPropagation();
            }.bind(this)
        });
        this._showActions(true);
    },
    setCurrentNode : function(){
        this.configList.setCurrentConfigItem( this );
    },
    _showActions: function( ){
        if( this.editMode ){
            if (this.actionArea_edit){
                this.actionArea_edit.setStyle("display", "");
            }
            if (this.actionArea_read){
                this.actionArea_read.setStyle("display", "none");
            }
        }else{
            if (this.actionArea_edit){
                this.actionArea_edit.setStyle("display", "none");
            }
            if (this.actionArea_read){
                this.actionArea_read.setStyle("display", "");
            }
        }
    },
    _hideActions: function(){
        if (this.actionArea_read) this.actionArea_read.setStyle("display", "none");
        if (this.actionArea_edit) this.actionArea_edit.setStyle("display", "none");
    },
    createIconAction: function(){
        this.actionNodes = this.actionNodes || {};
        if (!this.actionArea_read && !this.options.isNew){
            this.actionArea_read = new Element("div.actionArea").inject(this.node);
            this.actionArea_read.hide();
            this.options.actions_read.each(function(action){
                var actionNode = this.actionNodes[action.name] = new Element("i.actionNodeStyles", {
                    "title": action.title
                }).inject(this.actionArea_read);
                actionNode.addClass(action.class);
                if( action.styles )actionNode.setStyles( action.styles );
                // actionNode.setStyle("background", "url("+this.explorer.path+this.options.style+"/icons/"+action.icon+") no-repeat left center");
                actionNode.addEvent(action.event, function(e){
                    this[action.action](e);
                    e.stopPropagation();
                }.bind(this));
            }.bind(this));
        }
        if( !this.actionArea_edit ){
            this.actionArea_edit = new Element("div.actionArea").inject(this.node);
            this.actionArea_edit.hide();
            this.options.actions_edit.each(function(action){
                var actionNode = this.actionNodes[action.name] = new Element("i.actionNodeStyles", {
                    "title": action.title
                }).inject(this.actionArea_edit);
                actionNode.addClass(action.class);
                // actionNode.setStyle("background", "url("+this.explorer.path+this.options.style+"/icons/"+action.icon+") no-repeat left center");
                actionNode.addEvent(action.event, function(e){
                    this[action.action](e);
                    e.stopPropagation();
                }.bind(this));
            }.bind(this));
        }
    },
    insertCateogryBefore: function(ev){
        this.configList.newConfigItem( this.node, "before" );
    },
    insertCateogryAfter: function(ev){
        this.configList.newConfigItem( this.separatNode, "after" );
    },
    deleteConfig: function(ev){
        var _self = this;
        this.app.confirm("warn", this.actionNodes.deleteConfig, this.lp.deleteConfigTitle, this.lp.deleteConfigConfirm, 300, 120, function(){
            _self._deleteConfig();
            this.close();
        }, function(){
            this.close();
        });
    },
    _deleteConfig: function(callback){
        o2.Actions.load("x_custom_index_assemble_control").RevealAction.delete(this.data.id, function(){
            this.node.destroy();
            this.separatNode.destroy();
            if(this.configForm)this.configForm.destroy();
            this.explorer.showEmpty();
            if (callback) callback();
        }.bind(this),function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            try{
                var errorObj = JSON.parse(errorText);
                if( errorObj.message ){
                    this.app.notice( errorObj.message,"error");
                }
            }catch(e){

            }
        }.bind(this));
    },
    moveConfig: function(e){
        this._createMoveNode();
        this._setNodeMove(e);
        //this._hideActions();
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div.moduleNodeMove", {
            "text": this.node.get("text"),
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.container);
    },
    _setNodeMove: function(e){
        this._setMoveNodePosition(e);

        var droppables = this.configList.getConfigNodes(); //[this.container].concat(this.view.node, this.view.areaNode,this.view.columns);
        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "onEnter": function(dragging, inObj){
                var config = inObj.retrieve("config");
                if (config) config._dragIn(this);
            }.bind(this),
            "onLeave": function(dragging, inObj){
                var config = inObj.retrieve("config");
                if (config) config._dragOut(this);
            }.bind(this),
            "onDrag": function(e){
                this.configList.isOnDragging = true;
                //this._setScroll();
            }.bind(this),
            "onDrop": function(dragging, inObj, e){
                if (inObj){
                    var config = inObj.retrieve("config");
                    if (config){
                        this._dragComplete( config );
                        config._dragDrop(this);
                    }else{
                        this._dragCancel(dragging);
                    }
                }else{
                    this._dragCancel(dragging);
                }
                if( this.dragInterval ){
                    clearInterval( this.dragInterval );
                    this.dragInterval = null;
                }
                setTimeout( function(){
                    this.configList.isOnDragging = false;
                }.bind(this), 100 );
                e.stopPropagation();
            }.bind(this),
            "onCancel": function(dragging){
                if( this.dragInterval ){
                    clearInterval( this.dragInterval );
                    this.dragInterval = null;
                }
                setTimeout( function(){
                    this.configList.isOnDragging = false;
                }.bind(this), 100 )
            }.bind(this)
        });
        nodeDrag.start(e);

    },
    _dragIn : function(){   //移动时鼠标进入
        this.separatNode.addClass( "separatNode_dragIn" );
    },
    _dragOut : function(){  //移动时鼠标移出
        this.separatNode.removeClass( "separatNode_dragIn" );
    },
    _dragDrop : function(){ //移动到该对象时鼠标松开
        this.separatNode.removeClass( "separatNode_dragIn" );
    },
    _dragComplete: function( config ){ //拖拽完成
        this.node.inject(config.separatNode,"after");
        this.separatNode.inject(this.node,"after");
        this.setCurrentNode();
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
        this.configList.adjustSeq();
    },
    _dragCancel: function(){  //拖拽取消
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
    },
    _setScroll : function(){
        var areaNode = this.explorer.configScrollWrapNode || this.explorer.naviArea;

        var areaCrd = areaNode.getCoordinates();
        var topPoint = areaCrd.top;
        var bottomPoint = topPoint + areaCrd.height;

        var node = this.explorer.configScrollContentNode || this.explorer.naviNode;
        var coordinates = this.moveNode.getCoordinates();
        if( coordinates.top < topPoint && coordinates.bottom > topPoint ) {
            if (!this.dragInterval) {
                this.dragInterval = setInterval(function () {
                    if( areaNode.getScroll().y - 15  > 0 ){
                        areaNode.scrollTo( 0, areaNode.getScroll().y - 15);
                    }else{
                        areaNode.scrollTo(0, 0);
                    }
                }.bind(this), 100)
            }
        }else if( coordinates.top < bottomPoint &&  coordinates.bottom > bottomPoint ){
            if (!this.dragInterval) {
                this.dragInterval = setInterval(function () {
                    if( areaNode.getScroll().y + 15 < node.getSize().y ){
                        areaNode.scrollTo(0, areaNode.getScroll().y + 15);
                    }else{
                        areaNode.scrollTo( 0, node.getSize().y );
                    }
                }.bind(this), 100)
            }
        }else{
            if( this.dragInterval ){
                clearInterval( this.dragInterval );
                this.dragInterval = null;
            }
        }
    },
    _setMoveNodePosition: function(e){
        var x = e.page.x+2;
        var y = e.page.y+2;
        this.moveNode.positionTo(x, y);
    }
});

MWF.xApplication.StandingBook.ConfigForm = new Class({
    Implements: [Options, Events],
    options: {
        isNew: false,
        isEdited: true
    },
    initialize: function(node, configItem, options){
        this.setOptions(options);
        this.configItem = configItem;
        configItem.configForm = this;
        this.configList = configItem.configList;
        this.explorer = configItem.explorer;
        this.app = configItem.app;
        this.content = $(node);
        this.lp = this.app.lp;
        this.load();
    },
    reload: function () {
        this.clear();
        this.load();
    },
    destroy: function(){
        this.clear();
        this.explorer.showEmpty();
    },
    load: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div.formNode").inject(this.content);

        this.contentContainerNode = new Element("div.formContentContainerNode").inject(this.node);

        this.middleNode = new Element("div.formMiddleNode").inject(this.contentContainerNode);

        this.scrollNode = new Element("div.formContentScrollNode").inject(this.middleNode);

        this.contentNode = new Element("div.formContentNode").inject(this.scrollNode);

        this.bottomNode = new Element("div.formBottomNode").inject(this.contentContainerNode);
        this.loadActions();

        this.loadForm();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
        this.setContentSize();

    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setContentSize: function () {
        var nodeSize = this.content.getSize();
        var h = nodeSize.y - this.getOffsetY(this.content);

        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;

        var bottomY = this.bottomNode ? (this.getOffsetY(this.bottomNode) + this.bottomNode.getSize().y) : 0;
        h = h - bottomY;

        h = h - this.getOffsetY(this.scrollNode);

        this.scrollNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    clear: function () {
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if( this.form )this.form.destroy();
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
        }
        this.node.destroy();
    },
    changeMode: function(keepData){
        debugger;
        this.options.isEdited = !this.options.isEdited;
        this.form.changeMode(keepData);
        if( this.options.isEdited ){
            this.contentNode.getElement("[item='switchField']").show();
            this.loadEditModeAction();
        }else{
            this.contentNode.getElement("[item='switchField']").hide();
            this.loadReadModeAction();
        }
        this.reloadFilePanel(keepData);
    },
    loadActions: function(){
        if( this.options.isEdited ){
            this.loadEditModeAction();
        }else{
            this.loadReadModeAction();
        }
    },
    loadReadModeAction: function(){
        this.bottomNode.empty();
        this.saveAction = null;
        this.cancelAction = null;
        this.editAction = new Element("div.formInputEditButton", {
            "text": this.lp.edit,
            "events":{
                "click": function () {
                    this.changeMode(true);
                }.bind(this)
            }
        }).inject( this.bottomNode );
        this.editAction.addClass("mainColor_bg");
    },
    loadEditModeAction: function(){
        this.bottomNode.empty();
        this.editAction = null;
        this.saveAction = new Element("div.formInputOkButton", {
            "text": this.lp.save,
            "events":{
                "click": function () {
                    var data = this.form.getResult(true, null, true, true, true);
                    if(!data)return;

                    var d = Object.clone(data);

                    if( d.enable === "true" || d.enable === true ){
                        d.enable = true;
                    }else{
                        d.enable = false;
                    }

                    if( !d.id && this.data)d.id = this.data.id;


                    d.cmsList = this.cmsList || [];
                    d.processPlatformList = this.processPlatformList || [];

                    var fieldList = this.fieldPanel.getData();
                    if( !fieldList || !fieldList.length ){
                        var y = this.fileListArea.getSize().y;
                        this.app.notice(this.lp.fieldListNotEmpty,"error",this.fileListArea, {"x": "right", "y": "top"});
                        return
                    }
                    d.data = fieldList;

                    if( d.availableList ){
                        d.availablePersonList = d.availableList.filter(function (item) { return item.split("@").getLast() === "P" });
                        d.availableUnitList = d.availableList.filter(function (item) { return item.split("@").getLast() === "U" });
                        d.availableGroupList = d.availableList.filter(function (item) { return item.split("@").getLast() === "G" });
                    }

                    d.ignorePermission = typeOf(d.ignorePermission) === 'boolean' ? d.ignorePermission : (d.ignorePermission === 'true');

                    this.saveForm(d);
                }.bind(this)
            }
        }).inject( this.bottomNode );
        this.saveAction.addClass("mainColor_bg");
        this.cancelAction = new Element("div.formInputCancelButton", {
            "text": this.lp.cancel,
            "events":{
                "click": function () {
                    if( this.options.isNew ){
                        this.configItem.destroy();
                        this.destroy();
                        this.configList.currentConfigItem = null;
                        this.configList.currentNode = null;
                        this.configList.configForm = null;
                    }else{
                        this.changeMode();
                    }
                }.bind(this)
            }
        }).inject( this.bottomNode );
    },
    saveForm: function(data){
        var p;
        if( this.options.isNew ){
            p =  o2.Actions.load("x_custom_index_assemble_control").RevealAction.create(data);
        }else{
            p =  o2.Actions.load("x_custom_index_assemble_control").RevealAction.edit(data.id, data);
        }
        p.then(function (json) {
            o2.Actions.load("x_custom_index_assemble_control").RevealAction.get(data.id || json.data.id, function (js1) {
                this.data = this.parseData( js1.data );
                this.form.data = [ this.data ];

                if( this.options.isNew ){
                    this.options.isNew = false;
                    this.configItem.options.isNew = false;
                    this.configItem.setText( data.name );
                    this.configItem.setEnable( data.enable );
                    this.configItem.createIconAction();
                    this.configItem._showActions();
                }else{
                    this.configItem.setText( data.name );
                    this.configItem.setEnable( data.enable );
                }

                this.configItem.data = js1.data;
                this.app.notice( this.lp.saveSuccess );

                this.changeMode(true);

            }.bind(this));
            // this.loadReadModeAction();
        }.bind(this)).catch(function (json) {
            if (json.text) {
                this.app.notice(json.text, "error");
            }else if(json.xhr){
                var responseJSON = JSON.parse( json.xhr.responseText );
                if( responseJSON.message ){
                    this.app.notice( responseJSON.message, "error" );
                }else{
                    this.app.notice( this.lp.saveFailure, "error" );
                }
            }else{
                this.app.notice( this.lp.saveFailure, "error" );
            }
        }.bind(this))
    },
    parseData: function(d) {
        d.availableList = (d.availablePersonList || []).concat( d.availableUnitList || [], d.availableGroupList || [] );
        this.cmsList = d.cmsList || [];
        this.processPlatformList = d.processPlatformList || [];
        if( d.enable )d.enable = "true";
        return d;
    },
    getData: function(){
        var p1, p2;
        if( this.options.isNew ) {
            p1 = Promise.resolve({});
        // }else if( this.configItem && this.configItem.data ){
        //     p1 =  Promise.resolve( this.parseData( this.configItem.data ));
        }else{
            p1 =  o2.Actions.load("x_custom_index_assemble_control").RevealAction.get(this.configItem.data.id).then(function (json) {
                return this.parseData( json.data )
            }.bind(this))
        }

        p2 = o2.Actions.load("x_custom_index_assemble_control").RevealAction.listDirectory().then(function (json) {
            return json.data;
        });

        return [p1, p2];
    },
    loadForm: function(){
        Promise.all(this.getData()).then(function (array) {
            var data = this.data = array[0];
            var directoryList = this.directoryList = array[1];
            var lp = this.lp;
            this.contentNode.empty();
            this.contentNode.set("html", this.getHtml());
            MWF.xDesktop.requireApp("Template", "MForm", function () {
                this.form = new MForm(this.contentNode, [data], {
                    isNew: this.options.isNew,
                    isEdited: this.options.isEdited,
                    style : "setting",
                    hasColon : true,
                    itemTemplate: {
                        enable: { "text": lp.enable, type : "select", notEmpty: true, selectText: lp.enableSelectText, selectValue: ["true", "false"], defaultValue: "true",
                            event: {
                                mouseenter: function (item) { item.items[0].addClass("mainColor_border") },
                                mouseleave: function (item) { item.items[0].removeClass("mainColor_border") }
                            }
                        },
                        name: { "text": lp.name, tType : "text", notEmpty: true, attr: {"placeholder": lp.inputNamePlacholder},
                            event: {
                                mouseenter: function (item) { item.items[0].addClass("mainColor_border") },
                                mouseleave: function (item) { item.items[0].removeClass("mainColor_border") }
                            }
                        },
                        processPlatformList: { "text": lp.processPlatformList, type : "org",  orgType: "custom", style: {
                                "padding-right": "3%",
                                "width": "97%",
                                "background-image": "url(../x_component_StandingBook/$ConfigView/default/icons/icon_process.png)",
                                "background-position": "99% center"
                            },
                            orgOptions: {
                                "count": 0,
                                "uniqueFlag": "key",
                                "onLoad" : function(selector) {
                                    this.searchInput.setStyle("width","calc( 100% - 10px )");
                                },
                                "selectableItems": [ //可选项树
                                    {
                                        "name": lp.processPlatformList,
                                        "subItemList": directoryList.filter(function (d) { return d.category === "processPlatform" })
                                    }
                                ]
                            },
                            validRule: function (value, item) {
                                var cmsList = this.form.getItem("cmsList").getValue();
                                if( !value.length && (!cmsList || !cmsList.length) ){
                                    return lp.appEmptyNotice
                                }
                            }.bind(this),
                            event: {
                                change: function (item) {
                                    this.processPlatformList = item.orgObject.map( function (org) { return org.data });
                                    this.changeApp();
                                }.bind(this),
                                mouseenter: function (item) { item.items[0].addClass("mainColor_border") },
                                mouseleave: function (item) { item.items[0].removeClass("mainColor_border") }
                            }
                        },
                        cmsList: { "text": lp.cmsList, type : "org", orgType: "custom", style: {
                                "padding-right": "3%",
                                "width": "97%",
                                "background-image": "url(../x_component_StandingBook/$ConfigView/default/icons/icon_cms.png)",
                                "background-position": "99% center"
                            },
                            orgOptions: {
                                "count": 0,
                                "uniqueFlag": "key",
                                "onLoad" : function(selector) {
                                    this.searchInput.setStyle("width","calc( 100% - 10px )");
                                },
                                "selectableItems": [ //可选项树
                                    {
                                        "name": lp.cmsList,
                                        "subItemList": directoryList.filter(function (d) { return d.category === "cms" })
                                    }
                                ]
                            },
                            event: {
                                change: function (item) {
                                    this.cmsList = item.orgObject.map( function (org) { return org.data });
                                    this.changeApp();
                                }.bind(this),
                                mouseenter: function (item) { item.items[0].addClass("mainColor_border") },
                                mouseleave: function (item) { item.items[0].removeClass("mainColor_border") }
                            }
                        },
                        availableList: { "text": lp.availableList, type : "org", defaultValue: [], style: {
                                "padding-right": "3%",
                                "width": "97%",
                                "background-position": "99% center"
                            }, orgOptions:{
                                "types": ["identity", "unit", "group"],
                                "expand": true,
                                "resultType": "person",
                                "count": 0
                            },
                            event: {
                                mouseenter: function (item) { item.items[0].addClass("mainColor_border") },
                                mouseleave: function (item) { item.items[0].removeClass("mainColor_border") }
                            }
                        },
                        ignorePermission: {
                            "text": lp.ignorePermission,
                            type: "radio",
                            selectText: [lp.yes, lp.no],
                            selectValue: ['true', 'false'],
                            defaultValue: 'false'
                        }
                    }
                }, this.app);
                this.form.load();

                this.fileListArea = this.contentNode.getElement("[item='fieldList']");

                this.expand = false;
                var switchFieldNode = this.contentNode.getElement("[item='switchField']");
                switchFieldNode.addEvent("click", function () {
                    if( this.expand ){
                        switchFieldNode.getElement("i").addClass("o2icon-triangle_down").removeClass("o2icon-triangle_up");
                        switchFieldNode.getElement("div").set("text", this.lp.allField);
                        this.fieldPanel.hideUncommon()
                    }else{
                        switchFieldNode.getElement("i").addClass("o2icon-triangle_up").removeClass("o2icon-triangle_down");
                        switchFieldNode.getElement("div").set("text", this.lp.commonField);
                        this.fieldPanel.showUncommon();
                    }
                    this.expand = !this.expand;
                }.bind(this));
                if( !this.options.isEdited ){
                    switchFieldNode.hide();
                }

                debugger;
                this.loadFilePanel( this.options.isNew ? [] : this.data.data );
            }.bind(this), true);
        }.bind(this))
    },
    getHtml : function(){
        var lp = this.lp;
        return  "<div styles='formTitle'>"+lp.formTitle+"</div>"+
            "<table width='98%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable'>" +

            "<tr><td styles='formTableTitle'>"+lp.name+"</td></tr>" +
            "<tr><td styles='formTableValue'>" +
            "   <div item='enable' style='float:left; width: 80px;'></div>" +
            "   <div item='name' style='margin-left:90px;'></div>" +
            "</td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.processPlatformList+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.processPlatformListNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='processPlatformList'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.cmsList+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.cmsListNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='cmsList'></td></tr>" +

            "<tr><td styles='formTableTitle'>" +
            "   <span>"+lp.fieldList+"</span>" +
            "   <div item='switchField' style='float:right; margin-right: 3%;cursor: pointer;line-height: 20px;width:100px;overflow:hidden;'>" +
            "       <i class='o2icon-triangle_down' style='float:left;'></i>" +
            "       <div style='color: #666;font-size: 14px;font-weight: normal;'>全部字段</div>" +
            "   </div>"+
            "</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.fieldListNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='fieldList'>请先选择流程应用或内容管理</td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.availableList+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.availableListNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='availableList'></td></tr>" +

            "<tr><td styles='formTableTitle'>"+lp.ignorePermission+"</td></tr>" +
            "<tr><td styles='formTableNote'>"+lp.ignorePermissionNote+"</td></tr>" +
            "<tr><td styles='formTableValue' item='ignorePermission'></td></tr>" +

            "</table>"

    },
    changeApp: function(){
        this.reloadFilePanel(true);
    },
    reloadFilePanel: function( keepData ){
        var fieldList;
        if( keepData && this.fieldPanel ){
            fieldList = this.fieldPanel.getData();
            this.loadFilePanel( fieldList );
        }else{
            fieldList = (this.data && this.data.data) ? this.data.data : [];
            this.loadFilePanel( fieldList );
        }
    },
    loadFilePanel: function ( data ) {
        if( this.cmsList || this.processPlatformList || data ){
            this.fileListArea.empty();
        }
        var lastDirectoryField = {};
        if( this.fieldPanel ){
            lastDirectoryField = this.fieldPanel.sourceDirectoryField || {};
            this.fieldPanel.destroy();
        }


        // var cmsList = [], cmsField = this.form.getItem("cmsList");
        // if( cmsField && cmsField.orgObject ){
        //     cmsList = cmsField.orgObject.map( function (org) { return org.data });
        // }else{
        //     cmsList = this.data.cmsList || []
        // }
        //
        // var processPlatformList = [], processPlatformField = this.form.getItem("processPlatformList");
        // if( processPlatformField && processPlatformField.orgObject ){
        //     processPlatformList = processPlatformField.orgObject.map( function (org) { return org.data });
        // }else{
        //     processPlatformList = this.data.processPlatformList || []
        // }


        this.fieldPanel = new MWF.xApplication.StandingBook.ConfigForm.FieldPanel(this.fileListArea, this, {
            isNew: this.options.isNew && ( !data || !data.length ),
            isEdited: this.options.isEdited,
            cmsList: this.cmsList || this.data.cmsList || [],
            processPlatformList: this.processPlatformList || this.data.processPlatformList || []
        }, data, lastDirectoryField)
    }
});

MWF.xApplication.StandingBook.ConfigForm.FieldPanel = new Class({
    Implements: [Options, Events],
    options: {
        isNew: false,
        isEdited: true,
        expand: false,
        cmsList: [],
        processPlatformList: []
    },
    initialize: function(node, configForm, options, data, lastDirectoryField){
        this.setOptions(options);
        this.configForm = configForm;
        this.configItem = configForm.configItem;
        this.configList = configForm.configItem.configList;
        this.explorer = configForm.configItem.explorer;
        this.app = configForm.configItem.app;
        this.container = $(node);
        this.lp = this.app.lp;
        this.scrollNode = configForm.scrollNode;
        this.contentNode = configForm.contentNode;
        this.lastDirectoryField = lastDirectoryField;

        // if( data ){
        //     this.data = data.map(function (d) {
        //         d.enable = "true";
        //         return d;
        //     });
        // }

        this.filterFields = [];
        this.displayFields = [];
        this.displayDefaultFields = [];
        this.usedFields = [];
        this.fieldsMap = {};
        if( data ){
            this.data = data;
            this.data.each(function (d) {
                if( d.filter )this.filterFields.push( d.field );
                if( d.display )this.displayFields.push( d.field );
                if( d.displayDefault )this.displayDefaultFields.push( d.field );
                if( d.filter || d.display || d.displayDefault )this.usedFields.push( d.field );
                this.fieldsMap[d.field] = d;
            }.bind(this))
        }
        // this.usedFields = this.filterFields.concat( this.displayFields, this.displayDefaultFields );

        this.load();
    },
    reload: function (processPlatformList, cmsList) {
        this.options.processPlatformList = processPlatformList;
        this.options.cmsList = cmsList;
        this.destroy();
        this.load();
    },
    destroy(){
        if (this.dragComplete ) document.removeEvent( "mouseup", this.dragComplete );
        this.container.empty();
    },
    load: function () {
        this.node = new Element("div.fileListNode").inject(this.container);
        if( this.options.isNew || this.options.isEdited ){
            this.listDirectoryField().then( function () {
                this.showCommon();
                this.showUncommon(this.options.expand);
            }.bind(this))
        }else{
            this.showRead();
        }
    },
    getData: function(){
        var result = this.grid.getResult(false, "", false, false, true);
        this.data = result.filter(function (d) {
            if( d.filter )d.filter = true;
            if( d.display )d.display = true;
            if( d.displayDefault )d.displayDefault = true;
            return d.filter || d.display || d.displayDefault
        }.bind(this));
        return this.data;
    },
    showRead: function(){
        this.loadGrid(this.data);
    },
    showCommon: function(){
        if( !this.isCommonLoaded ) {
            this.loadGrid(this.commonFields);
            this.isCommonLoaded = true;
        }
    },
    showUncommon: function( show ){
        if( !this.isUncommonLoaded ) {
            this.uncommonNodeList = [];
            this.uncommonFields.each(function (d) {
                var tr = this.grid.appendTr( d, null, null, d );
                if( !show && !tr.items["filter"].getValue().length && !tr.items["display"].getValue().length )tr.mElement.hide();
                this.setEvents( [tr.mElement] );
                this.uncommonNodeList.push( tr );
            }.bind(this));
            this.isUncommonLoaded = true;
        }else{
            this.uncommonNodeList.each(function (tr) {
                tr.mElement.show();
            })
        }
    },
    hideUncommon: function(){
        if(this.uncommonNodeList)this.uncommonNodeList.each(function (tr) {
            if( !tr.items["filter"].getValue().length && !tr.items["display"].getValue().length )tr.mElement.hide();
        })
    },
    isNewAddField: function(d, type){
        if( !this.lastDirectoryField[type] )return true;
        var fieldList = this.lastDirectoryField[type];
        for( var i=0; i<fieldList.length; i++ ){
            if( fieldList[i].field === d.field )return false;
        }
        return true;
    },
    listDirectoryField: function(){
        return o2.Actions.load("x_custom_index_assemble_control").RevealAction.directoryField({
            directoryList: ( this.options.processPlatformList || [] ).concat( this.options.cmsList || [] )
        }).then(function (json) {

            this.sourceDirectoryField = Object.clone(json.data);

            this.commonFields = [];
            this.uncommonFields = [];

            debugger;


            if( !json.data ){
                this.directoryField = [];
                return this.directoryField;
            }else{
                var map = {};
                ( json.data.facetFieldList || [] ).each(function (d) { //维度字段，只筛选
                    if( !map[ d.field ] )map[ d.field ] = d;
                    var f = map[ d.field ];
                    f.facet = true; //维度，只筛选
                    if( this.isNewAddField(f, "facetFieldList") ){ //this.options.isNew
                        f.filter = "true";
                        // f.display = "";
                        // f.displayDefault = ""; //默认展现
                    }
                }.bind(this));
                ( json.data.fixedFieldList || [] ).map(function (d) { //固定列
                    if( !map[ d.field ] )map[ d.field ] = d;
                    var f = map[ d.field ];
                    f.fixed = true; //固定列，只展现
                    if( this.isNewAddField(f, "fixedFieldList") ) { //this.options.isNew
                        // d.filter = "";
                        f.display = "true";
                        f.displayDefault = "true"; //默认展现
                    }
                }.bind(this));
                ( json.data.dynamicFieldList || [] ).map(function (d) { //动态字段
                    if( !map[ d.field ] )map[ d.field ] = d;
                    var f = map[ d.field ];
                    f.dynamic = true;
                    // if( this.isNewAddField(d, "dynamicFieldList") ) { //this.options.isNew
                    //     d.filter = "";
                    //     d.display = "";
                    //     d.displayDefault = ""; //默认展现
                    // }
                }.bind(this));

                // this.directoryField  = json.data.facetFieldList.concat( json.data.fixedFieldList, json.data.dynamicFieldList ).map(function (d) {
                this.directoryField  = Object.values(map).map(function (d) {
                    if( !d.field )d.field = "";

                    if( !this.options.isNew ){
                        d.filter =  this.filterFields.contains( d.field ) ? "true" : (d.filter||"");
                        d.display =  this.displayFields.contains( d.field ) ? "true" : (d.display||"");
                        d.displayDefault =  this.displayDefaultFields.contains( d.field ) ? "true" : (d.displayDefault||"");
                    }

                    var type = "";
                    if( d.facet ){
                        type = "facetFieldList"
                    }else if( d.fixed ){
                        type = "fixedFieldList"
                    }else if( d.dynamic ){
                        type = "dynamicFieldList"
                    }
                    var isNewAdd = this.isNewAddField(d, type);

                    if( d.field.split("_").length >= 3 ){
                        var arr = d.field.split("_");
                        arr.shift();
                        arr.shift();
                        d.fieldName = arr.join("_");
                    }else{
                        d.fieldName = d.field;
                    }

                    if( this.fieldsMap[d.field] ){
                        d.text = this.fieldsMap[d.field].text;
                    }

                    if( !d.text ){
                        if( this.lp.defaultText[d.fieldName] ){
                            d.text = this.lp.defaultText[d.fieldName];
                        }else{
                            var name = d.name || "";
                            d.text = this.endsWith(name, ".name") ? name.substring(0, name.length-".name".length) : name;
                        }
                    }

                    if( this.isCommonField(d) ){
                        if( isNewAdd ){ //this.options.isNew
                            // if( this.isFilterable(d) && d.facet )d.filter = "true";
                            if(d.fixed || d.dynamic)d.display = "true";
                        }
                        this.commonFields.push( d );
                    }else{
                        this.uncommonFields.push( d );
                    }

                    return d;
                }.bind(this));

                return this.directoryField;
            }
        }.bind(this));
    },
    isFilterable: function(d){
        if( !d )return false;
        if( d.facet )return true;
        if( d.dynamic && ["string","number","date"].contains(d.fieldType) )return true;
        return false;
    },
    isCommonField: function(d){
        if( this.usedFields.contains( d.field ) )return true;

        var endWith = MWF.xApplication.StandingBook.options.NotCommonlyField.endWith;
        var equals = MWF.xApplication.StandingBook.options.NotCommonlyField.equals;

        if( equals.contains( d.fieldName ) ){
            return false;
        }
        for( var i=0; i<endWith.length; i++ ){
            if( this.endsWith( d.fieldName, endWith[i] )){
                return false;
            }
        }
        return true;
    },
    loadGrid: function(data){
        var _self = this;
        this.grid = new MDataGrid(this.node, data , {
            style: "forum",
            isNew: this.options.isNew,
            isEdited:  this.options.isEdited,

            isCreateTh : false,
            containerIsTable : true,
            isCreateTrOnNull : false,

            hasOperation : false,
            hasSequence : false,
            itemTemplate: {
                fieldName: { type : "innerText"},
                text: {
                    style: this.options.isEdited ? {
                        "width": "calc( 100% - 2 )",
                        "border": "1px solid #ccc",
                        "height": "26px",
                        "border-radius": "13px",
                        "color": "blue"
                    }: { "height": "26px", "color": "blue" },
                    attr: this.options.isEdited ? {placeholder: "字段名称"} : {}
                },
                filter : { type : "checkbox",
                    selectText : ["可过滤"],
                    selectValue: ["true"],
                    onQueryLoad: function () {
                        // this.options.disable = !_self.isFilterable(this.parent.sourceData);
                        if( !_self.isFilterable(this.parent.sourceData) ){
                            this.options.attr = {
                                disabled: true,
                                title: "该字段不可过滤"
                            };
                            this.options.style = {"opacity":0.3};
                        }
                    }
                },
                display : { type : "checkbox",
                    selectText : ["可展现"],
                    selectValue: ["true"],
                    onQueryLoad: function () {
                        if( this.parent.sourceData && !this.parent.sourceData.fixed && !this.parent.sourceData.dynamic ){
                            //this.options.disable = true;
                            this.options.attr = {
                                disabled: true,
                                title: "该字段不可展现"
                            };
                            this.options.style = {"opacity":0.3};
                        }
                    },
                    event: {
                        change: function (item, ev) {
                            if( !item.getValue()[0] && item.parent.items.displayDefault){
                                item.parent.items.displayDefault.setValue("");
                            }
                        }
                    }
                },
                displayDefault : { type : "checkbox",
                    selectText : ["默认展现"],
                    selectValue: ["true"],
                    onQueryLoad: function () {
                        if( this.parent.sourceData && !this.parent.sourceData.fixed && !this.parent.sourceData.dynamic ){
                            //this.options.disable = true;
                            this.options.attr = {
                                disabled: true,
                                title: "该字段不可展现"
                            };
                            this.options.style = {"opacity":0.3};
                        }
                    },
                    event: {
                        change: function (item, ev) {
                            if( item.getValue()[0] === "true" && item.parent.items.display ){
                                item.parent.items.display.setValue("true");
                            }
                        }
                    }
                }
            },
            onPostLoad: function () {
                if( this.options.isEdited ){
                    this.setEvents( this.node.getElements(".fileListItem") )
                }
            }.bind(this)
        }, this.app );
        if( this.options.isEdited ) {
            this.grid.setTrTemplate(
                "<div class='fileListItem'>" +
                "   <i class='o2icon-log fieldListIcon'></i>" +
                "   <div class='filteListName' item='fieldName'></div>" +
                "   <div class='filteListText' item='text'></div>" +
                "   <div class='filteListEnable' item='filter' style='margin-left:5px;'></div>" +
                "   <div class='filteListEnable' item='display'></div>" +
                "   <div class='filteListDisplayDefault' item='displayDefault'></div>" +
                "</div>"
            );
        }else{
            this.grid.setTrTemplate(
                "<div class='fileListItem'>" +
                "   <i class='o2icon-log fieldListIcon'></i>" +
                "   <div class='filteListName' item='fieldName'></div>" +
                "   <div class='filteListText' item='text'></div>" +
                "   <div class='filteListEnable' item='filter'></div>" +
                "   <div class='filteListEnable' item='display'></div>" +
                "   <div class='filteListDisplayDefault' item='displayDefault'></div>" +
                "</div>"
            );
        }
        this.grid.load();

    },
    setEvents: function( itemList ){
        itemList.each(function (itemNode) {
            itemNode.addEvents({
                mouseenter: function (e) {
                    itemNode.addClass("mainColor_bg_opacity")
                }.bind(this),
                mouseleave: function (e) {
                    itemNode.removeClass("mainColor_bg_opacity")
                }.bind(this)
            })
        }.bind(this));
        // return;
        // itemList.each(function (itemNode) {
        //     itemNode.addEvents({
        //         mousedown: function (e) {
        //             this.startIndex = itemNode.get("data-id").replace("_","").toInt();
        //             this.scrollNodeHeight = this.scrollNode.getSize().y;
        //             this.isChecked = itemNode.getElement("input[type='checkbox']").get("checked");
        //         }.bind(this),
        //         mouseenter: function (e) {
        //             if( !this.startIndex )return;
        //             var curIndex = itemNode.get("data-id").replace("_","").toInt();
        //             if( !curIndex )return;
        //             var indexs = this.getIndexListByRange( this.startIndex, curIndex );
        //             if( this.selectedIndexs ){
        //                 this.selectedIndexs.each( function( index ){
        //                     if( !indexs.contains(index) ){
        //                         this.node.getElement("[data-id='_"+index+"']").setStyle("background-color", "#ffffff" );
        //                     }
        //                 }.bind(this));
        //                 indexs.each( function( index ){
        //                     if( !this.selectedIndexs.contains(index) ){
        //                         this.node.getElement("[data-id='_"+index+"']").setStyle("background-color", "#fffdf2")
        //                     }
        //                 }.bind(this))
        //             }else{
        //                 for( var i=0; i<indexs.length; i++ ){
        //                     this.node.getElement("[data-id='_"+indexs[i]+"']" ).setStyle("background-color", "#fffdf2");
        //                 }
        //             }
        //             this.selectedIndexs = indexs;
        //
        //             var scrollNodeTop = this.scrollNode.getScroll().y;
        //             // var pageOffsetHeight = e.event.pageY - this.scrollNodeTop;
        //             var positionY = itemNode.getPosition( this.contentNode ).y;
        //             if(( positionY + 50 * 1.5) > ( this.scrollNodeHeight + scrollNodeTop )){
        //                 window.setTimeout( function(){
        //                     this.scrollNode.scrollTo(0, scrollNodeTop + 200 )
        //                 }.bind(this), 200)
        //             }else if( positionY - 50 * 1.5 < scrollNodeTop ){
        //                 window.setTimeout( function(){
        //                     this.scrollNode.scrollTo(0, scrollNodeTop - 200 )
        //                 }.bind(this), 200)
        //             }
        //         }.bind(this)
        //     })
        // }.bind(this));
        // this.dragComplete = function (e) {
        //     if( !this.startIndex )return;
        //     this.startIndex = null;
        //     if( this.selectedIndexs && this.selectedIndexs.length ){
        //         this.selectedIndexs.each( function( index ){
        //             var itemNode = this.node.getElement("[data-id='_"+index+"']");
        //             itemNode.getElement("input[type='checkbox']").set("checked", !this.isChecked);
        //             itemNode.setStyle("background-color", "#ffffff" );
        //         }.bind(this));
        //     }
        //     this.selectedIndexs = null;
        // }.bind(this);
        // document.addEvent( "mouseup", this.dragComplete )
    },
    getIndexListByRange: function(index1, index2){
        var min = Math.min( index1, index2 );
        var max = Math.max( index1, index2 );
        var indexs = [];
        for( var i=min; i<=max; i++ ){
            indexs.push(i);
        }
        return indexs;
    },
    endsWith: function(s, e) {
        return s.substring(s.length-e.length) === e;
    }
})



