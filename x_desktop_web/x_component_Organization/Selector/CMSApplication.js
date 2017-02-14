MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Department", null, false);
MWF.xApplication.Organization.Selector.CMSApplication = new Class({
	Extends: MWF.xApplication.Organization.Selector.Department,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Application",
        "values": [],
        "names": [],
        "expand": false
    },

    loadSelectItems: function(addToNext){
        this.action.listCMSApplication(function(json){
            json.data.each(function(data){
                data.name = data.appName;
                var category = this._newItem(data, this, this.itemAreaNode);
            }.bind(this));
        }.bind(this));
    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        this.action.getCMSApplication(function(json){
            if(json.data)json.data.name = json.data.appName;
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.CMSApplication.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.CMSApplication.Item(data, selector, container, level);
    }
});
MWF.xApplication.Organization.Selector.CMSApplication.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Department.Item,
    _getShowName: function(){
        return this.data.appName;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSubItem: function(){
        return false;
    }
});

MWF.xApplication.Organization.Selector.CMSApplication.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Department.ItemSelected,
    _getShowName: function(){
        return this.data.appName;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/applicationicon.png)");
    }
});
