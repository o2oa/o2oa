MWF.xDesktop.requireApp("cms.ColumnManager", "widget.ColumnViewerSetting", null, false);
MWF.xApplication.cms.ColumnManager.ColumnPublisherSetting = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.ColumnViewerSetting,
    listData: function( callback ){
        this.app.restActions.listAppInfoPublishers(this.options.objectId, function(json){
            this.data = json.data;
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    saveData: function( data, callback ){
        this.app.restActions.saveAppInfoPublisher(this.options.objectId, data, function (json) {
            this.app.restActions.getColumn( this.options.objectId, function( js ){
                this.dataParent.data = js.data;
                this.app.options.column = js.data;
                this.app.options.application = js.data;
                if( callback )callback( json );
            }.bind(this));
        }.bind(this), null, false);
    }
});
