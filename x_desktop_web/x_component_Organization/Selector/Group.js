MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Person", null, false);
MWF.xApplication.Organization.Selector.Group = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Group",
        "groups": [],
        "roles": [],
        "values": [],
        "names": []
    },

    _listItemByKey: function(callback, failure, key){
        this.action.listGroupByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getGroup(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.Group.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listGroupByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Organization.Selector.Group.Item(data, selector, container);
    },
    _listItemNext: function(last, count, callback){
        this.action.listGroupNext(last, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _getChildrenItemIds: function(data){
        return data.groupList;
    }
});
MWF.xApplication.Organization.Selector.Group.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/groupicon.png)");
    }
});

MWF.xApplication.Organization.Selector.Group.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/groupicon.png)");
    }
});
