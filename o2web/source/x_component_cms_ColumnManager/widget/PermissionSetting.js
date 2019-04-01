MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};
MWF.xApplication.cms.ColumnManager.PermissionSetting = new Class({
    Implements: [Options],
    options : {
        objectId : "", //对象或分类的ID
        objectType : "APPINFO", //CATEGORY
        permission : "VIEW" //PUBLISH
    },
    initialize: function(app, lp, node, options){
        this.app = app;
        this.node = $(node);
        this.lp = lp;
        this.setOptions( options );

        this.data = [];
        this.personList = [];
        this.unitList = [];

        //this.lp = this.app.lp.application.availableSetting;
    },
    load: function(){
        this.listData( function( json ){
            json.data = json.data || [];
            this.data = json.data;
            json.data.each(function( d ){
                if(d.usedObjectType == "USER" ){
                    this.personList.push( d.usedObjectName )
                }else if(d.usedObjectType == "UNIT"){
                    this.unitList.push( d.usedObjectName )
                }
            }.bind(this));
            this.createNode();
        }.bind(this));
    },
    createNode: function(){
        //if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.titleNode = new Element("div.availableTitleNode", {
            "styles": this.app.css.availableTitleNode,
            "text": this.lp.title
        }).inject(this.node);

        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        this.itemsContentNode = new Element("div.availableItemsContentNode", {"styles": this.app.css.availableItemsContentNode}).inject(this.contentNode);
        this.actionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);

        var changeIdentityList = new Element("div.selectButtonStyle", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.lp.setPerson
        }).inject(this.actionAreaNode);
        changeIdentityList.addEvent("click", function(){
            this.changeIdentitys();
        }.bind(this));

        var changeUnitList = new Element("div.selectButtonStyle", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.lp.setUnit
        }).inject(this.actionAreaNode);
        changeUnitList.addEvent("click", function(){
            this.changeUnit();
        }.bind(this));

        this.setItems();
    },
    setItems: function(){
        if (this.personList){
            this.personList.each(function(name){
                if (name) new MWF.widget.O2Person({"name": name}, this.itemsContentNode, {"style": "application"});
            }.bind(this));
        }
        if (this.unitList){
            this.unitList.each(function(name){
                if (name) new MWF.widget.O2Unit({"name": name}, this.itemsContentNode,  {"style": "application"});
            }.bind(this));
        }
    },

    changeIdentitys: function(){
        var options = {
            "type": "person",
            "title": this.lp.setPerson,
            "values": this.personList || [],
            "onComplete": function(items){
                var personList = [];

                items.each(function(item){
                    personList.push(item.data.distinguishedName);
                }.bind(this));

                personList.each(function(item){
                    if( !this.personList.contains( item ) ){
                        var object = {
                            "objectType": this.options.objectType,
                            "objectId": this.options.objectId,
                            "usedObjectType": "USER",
                            "usedObjectCode": item,
                            "usedObjectName": item,
                            "permission" : this.options.permission
                        };
                        this.saveData(object, function(json){
                            object.id = json.data.id;
                            this.data.push( object );
                        }.bind(this), null, false);
                    }
                }.bind(this));

                this.personList.each(function(item){
                    if( !personList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.data.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.removeData(id, function(json){
                            this.data.erase( ad )
                        }.bind(this));
                    }
                }.bind(this));

                this.personList = personList;
                this.itemsContentNode.empty();
                this.setItems();

                this.app.notice(  this.lp.setIdentitySuccess , "success");
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, options);
    },
    changeUnit: function(){
        var options = {
            "type": "unit",
            "title": this.lp.setUnit,
            "values": this.unitList || [],
            "onComplete": function(items){
                var unitList = [];

                items.each(function(item){
                    unitList.push(item.data.distinguishedName);
                }.bind(this));

                unitList.each(function(item){
                    if( !this.unitList.contains( item ) ){
                        var object = {
                            "objectType": this.options.objectType,
                            "objectId": this.options.objectId,
                            "usedObjectType": "UNIT",
                            "usedObjectCode": item,
                            "usedObjectName": item,
                            "permission" : this.options.permission
                        };
                        this.saveData(object, function(json){
                            object.id = json.data.id;
                            this.data.push( object );
                        }.bind(this));
                    }
                }.bind(this));

                this.unitList.each(function(item){
                    if( !unitList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.data.each(function(data){
                            if( data.usedObjectName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.removeData(id, function(json){
                            this.data.erase( ad )
                        }.bind(this));
                    }
                }.bind(this));

                this.unitList = unitList;
                this.itemsContentNode.empty();
                this.setItems();

                this.app.notice(  this.lp.setUnitSuccess , "success");
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    },
    listData: function( callback ){
        this.app.restActions.listColumnPermission(this.options.objectId, function(json){
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    removeData: function( id, callback ){
        this.app.restActions.removePermission(id, function (json) {
            if( callback )callback( json );
        }.bind(this), null, false);
    },
    saveData: function( data, callback ){
        this.app.restActions.savePermission(data, function (json) {
            if( callback )callback( json );
        }.bind(this), null, false);
    }
});
