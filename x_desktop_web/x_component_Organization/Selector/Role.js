MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Person", null, false);
MWF.xApplication.Organization.Selector.Role = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Role",
        "groups": [],
        "roles": [],
        "values": [],
        "names": []
    },
    initialize: function(container, options){
        this.setOptions(options);
        this.options.groups = [];
        this.options.roles = [];

        this.path = "/x_component_Organization/Selector/$Selector/";
        this.cssPath = "/x_component_Organization/Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.action = new MWF.xApplication.Organization.Actions.RestActions();

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.items = [];
    },
    _listItemByKey: function(callback, failure, key){
        this.action.listRoleByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getRole(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.Role.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listRoleByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Organization.Selector.Role.Item(data, selector, container);
    },
    _listItemNext: function(last, count, callback){
        this.action.listRoleNext(last, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    }
});
MWF.xApplication.Organization.Selector.Role.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/roleicon.png)");
    }
});

MWF.xApplication.Organization.Selector.Role.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/roleicon.png)");
    }
});
