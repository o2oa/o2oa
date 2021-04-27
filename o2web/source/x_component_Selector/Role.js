MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Role = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "groups": [],
        "roles": [],
        "values": [],
        "names": [],
        "selectType" : "role"
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectRole});
    },
    _init : function(){
        this.selectType = "role";
        this.className = "Role";
    },
    _listItemByKey: function(callback, failure, key){
        if (this.options.units.length || this.options.roles.length) key = this.getLikeKey(key);
        this.orgAction.listRoleByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getRole(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item, selectedNode){
        return new MWF.xApplication.Selector.Role.ItemSelected(data, selector, item, selectedNode)
    },
    _listItemByPinyin: function(callback, failure, key){
        if (this.options.units.length || this.options.roles.length) key = this.getLikeKey(key);
        this.orgAction.listRoleByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Selector.Role.Item(data, selector, container);
    },
    _listItemNext: function(last, count, callback){
        this.orgAction.listRoleNext(last, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _getChildrenItemIds: function(data){
        return data.roleList;
    },
    getLikeKey : function( key ){
        var result = key;
        if (this.options.groups.length || this.options.roles.length){
            var array = [];
            this.options.groups.each( function(d){
                array.push( typeOf(d)==="object" ? d.distinguishedName : d );
            }.bind(this));

            var array2 = [];
            this.options.roles.each( function(d){
                array2.push( typeOf(d)==="object" ? d.distinguishedName : d );
            }.bind(this));
            result = {"key": key || "", "groupList": array, "roleList": array2};
        }
        return result;
    }
});
MWF.xApplication.Selector.Role.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/roleicon.png)");
    }
});

MWF.xApplication.Selector.Role.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/roleicon.png)");
    }
});
