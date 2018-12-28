MWF.xDesktop.requireApp("cms.ColumnManager", "widget.PermissionSetting", null, false);
MWF.xApplication.cms.ColumnManager.CategoryPermissionSetting = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.PermissionSetting,
    listData: function( callback ){
        this.app.restActions.listCategoryPermission(this.options.objectId, function(json){
            if( callback )callback( json );
        }.bind(this), null ,false)
    }
});
