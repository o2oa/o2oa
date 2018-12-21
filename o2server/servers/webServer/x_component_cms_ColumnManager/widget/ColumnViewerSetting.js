MWF.xApplication.cms.ColumnManager = MWF.xApplication.cms.ColumnManager || {};
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xApplication.cms.ColumnManager.ColumnViewerSetting = new Class({
    Implements: [Options],
    options : {
        objectId : "" //对象或分类的ID
        //objectType : "APPINFO", //CATEGORY
        //permission : "VIEW" //PUBLISH
    },
    initialize: function(app, lp, node, options){
        this.app = app;
        this.node = $(node);
        this.lp = lp;
        this.setOptions( options );

        //this.lp = this.app.lp.application.availableSetting;
    },
    load: function(){
        this.createNode();
        this.listData( function(){
            this.loadOrg();
        }.bind(this));
    },
    createNode: function(){
        this.titleNode = new Element("div.availableTitleNode", {
            "styles": this.app.css.availableTitleNode,
            "text": this.lp.title
        }).inject(this.node);

        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        this.itemsContentNode = new Element("div.availableItemsContentNode", {"styles": this.app.css.availableItemsContentNode}).inject(this.contentNode);
        this.actionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);

        var changeAction = new Element("div.selectButtonStyle", {
            "styles": this.app.css.selectButtonStyle,
            "text": this.lp.set
        }).inject(this.actionAreaNode);
        changeAction.addEvent("click", function(){
            this.change();
        }.bind(this));
    },
    loadOrg : function(){
        this.itemsContentNode.empty();
        this.loadOrgWidget( this.data.personList );
        this.loadOrgWidget( this.data.unitList );
        this.loadOrgWidget( this.data.groupList );
    },
    loadOrgWidget: function(value ){
        this.OrgWidgetList = this.OrgWidgetList || [];
        var options = { "style": "xform", "canRemove": false };
        var node = this.itemsContentNode;
        (value || []).each(function( distinguishedName ){
            var flag = distinguishedName.substr(distinguishedName.length-1, 1);
            var data = { "name" : distinguishedName };
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
                    var widget = new MWF.widget.O2Other( data, node, options);
            }
            this.OrgWidgetList.push( widget );
        }.bind(this));
    },
    change: function(){
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var opt  = {
            "type" : "",
            "types" : ["person","unit","group"],
            "title": this.lp.set,
            "count" : 0,
            "values":  ( this.data.personList || [] ).combine( this.data.unitList || []).combine( this.data.groupList || [] ),
            "expand": false,
            "onComplete": function( array ){
                var data = {
                    personList : [],
                    unitList : [],
                    groupList : []
                };
                array.each( function( a ){
                    var dn = a.data.distinguishedName;
                    var flag = dn.substr(dn.length-1, 1);
                    switch (flag.toLowerCase()){
                        case "p":
                            data.personList.push( dn );
                            break;
                        case "u":
                            data.unitList.push( dn );
                            break;
                        case "g":
                            data.groupList.push( dn );
                            break;
                    }
                });
                this.saveData( data, function(){
                    this.listData( function(){
                        this.loadOrg();
                    }.bind(this));
                }.bind(this))
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, opt );
    },
    listData: function( callback ){
        this.app.restActions.listAppInfoViewers(this.options.objectId, function(json){
            this.data = json.data;
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    saveData: function( data, callback ){
        this.app.restActions.saveAppInfoViewer(this.options.objectId, data, function (json) {
            this.app.restActions.getColumn( this.options.objectId, function( js ){
                this.dataParent.data = js.data;
                this.app.options.column = js.data;
                this.app.options.application = js.data;
                if( callback )callback( json );
            }.bind(this));
        }.bind(this), null, false);
    }
});
