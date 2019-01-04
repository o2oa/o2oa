MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Group = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectGroup,
        "groups": [],
        "roles": [],
        "values": [],
        "names": []
    },

    _listItemByKey: function(callback, failure, key){
        if (this.options.groups.length || this.options.roles.length) key = {"key": key, "groupList": this.options.groups, "roleList": this.options.roles};
        this.orgAction.listGroupByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getGroup(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Group.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        if (this.options.groups.length || this.options.roles.length) key = {"key": key, "groupList": this.options.groups, "roleList": this.options.roles};
        this.orgAction.listGroupByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Selector.Group.Item(data, selector, container);
    },

    _listItemNext: function(last, count, callback){
        this.orgAction.listGroupNext(last, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _getChildrenItemIds: function(data){
        return data.groupList;
    }
});
MWF.xApplication.Selector.Group.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/groupicon.png)");
    }
});

MWF.xApplication.Selector.Group.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/groupicon.png)");
    }
});
MWF.xApplication.Selector.Group.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    loadSub: function(callback){
        if (!this.loaded){

            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    //var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback();

            }.bind(this), null, this.data.distinguishedName);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        //var iCount = (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
        return uCount;
    }
});


MWF.xApplication.Selector.Group.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "groups": [],
        "roles": [],
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;

        if (this.options.groups.length || this.options.roles.length) key = {"key": key, "groupList": this.options.groupList, "roleList": this.options.roleList};
        this.orgAction.listGroupByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});