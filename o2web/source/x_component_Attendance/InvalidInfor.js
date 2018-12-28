MWF.xDesktop.requireApp("Attendance", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xDesktop.requireApp("Selector", "package", null, false);
MWF.xApplication.Attendance.InvalidInfor = new Class({
	Extends: MWF.xApplication.Attendance.Explorer,
	Implements: [Options, Events],

    initialize: function(node, app, actions, options){
        this.setOptions(options);
        this.app = app;
        this.path = "/x_component_Attendance/$InvalidInfor/";
        this.cssPath = "/x_component_Attendance/$InvalidInfor/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.actions = actions;
        this.node = $(node);

        this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
    },
    loadView : function(){
        this.view = new MWF.xApplication.Attendance.InvalidInfor.View(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        this.view.load();
        this.setContentSize();
    },
    removeSelectedDocument: function(){
        this.view.items.each( function( it ){
           if( it.checkboxElement.get("checked" ) ){
               this.actions.deleteDetail(it.data.id, null, null, false );
               this.view.reload();
               this.app.notice(this.app.lp.deleteDocumentOK, "success");
           }
        }.bind(this))
    }
});

MWF.xApplication.Attendance.InvalidInfor.View = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.View,
    _createItem: function(data){
        return new MWF.xApplication.Attendance.InvalidInfor.Document(this.table, data, this.explorer, this);
    },

    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var filter = {"recordStatus": -1 };
        this.actions.listDetailFilterNext( id, count, filter, function(json){
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(document, all){
        this.actions.deleteDetail(document.id, function(json){
            this.explorer.view.reload();
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _createDocument: function(){

    },
    _openDocument: function( documentData ){

    }

});

MWF.xApplication.Attendance.InvalidInfor.Document = new Class({
    Extends: MWF.xApplication.Attendance.Explorer.Document

});

