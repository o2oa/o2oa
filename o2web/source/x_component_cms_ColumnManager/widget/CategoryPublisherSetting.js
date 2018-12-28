MWF.xDesktop.requireApp("cms.ColumnManager", "widget.ColumnViewerSetting", null, false);
MWF.xApplication.cms.ColumnManager.CategoryPublisherSetting = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.ColumnViewerSetting,
    listData: function( callback ){
        this.app.restActions.listCategoryInfoPublishers(this.options.objectId, function(json){
            this.data = json.data;
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    saveData: function( data, callback ){
        this.app.restActions.saveCategoryInfoPublisher(this.options.objectId, data, function (json) {
            this.app.restActions.getCategory( this.options.objectId, function( js ){
                this.category.data = js.data;
                if( callback )callback( json );
            }.bind(this));
        }.bind(this), null, false);
    }
});
