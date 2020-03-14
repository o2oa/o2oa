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
        "names": [],
        "include" : []
    },
    checkLoadSelectItems: function(){
        if( this.options.include && this.options.include.length ){
            this.loadInclude();
        }else if (!this.options.groups.length && !this.options.roles.length){
            this.loadSelectItems();
        }else{
            this.loadSelectItemsByCondition();
        }
    },

    loadInclude: function(){
        if( !this.options.include || this.options.include.length === 0 )return;
        this.includeGroup = [];
        this.options.include.each( function( d ){
            var key = typeOf(d)==="object" ? d.distinguishedName : d;
            this.orgAction.listGroupByKey(function(json){
                if( !json.data )return;
                var array = typeOf( json.data ) === "array" ? json.data : [json.data];
                array.each(function(data){
                    if( !this.isExcluded( data ) ) {
                        this.includeGroup.push( data.distinguishedName );
                        var item = this._newItem(data, this, this.itemAreaNode, 1);
                        this.items.push(item);
                    }
                }.bind(this));
            }.bind(this), null, key);
        }.bind(this))
    },
    _listItemByKey: function(callback, failure, key){
        if (this.options.groups.length || this.options.roles.length) key = this.getLikeKey( key );
        this.orgAction.listGroupByKey(function(json){
            if( this.includeGroup && this.includeGroup.length > 0 ){
                json.data = json.data.filter( function(d){
                    return this.includeGroup.contains(d.distinguishedName);
                }.bind(this))
            }
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
        if (this.options.groups.length || this.options.roles.length) key = this.getLikeKey( key );
        this.orgAction.listGroupByPinyin(function(json){
            if( this.includeGroup && this.includeGroup.length > 0 ){
                json.data = json.data.filter( function(d){
                    return this.includeGroup.contains(d.distinguishedName);
                }.bind(this))
            }
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
MWF.xApplication.Selector.Group.Item = new Class({
    Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/groupicon.png)");
    },
    loadSubItem: function(){
        this.selector.orgAction.listPersonNested(this.data.id, function(json){
            this.textNode.set("text", this.textNode.get("text")+" ("+json.data.length+")");
            var usersText = "";
            json.data.each(function(item){
                usersText+=item.name+", ";
            }.bind(this));

            var node = new Element("div", {"styles": {"max-width": "300px"}, "text": usersText});

            if (!Browser.Platform.ios){
                this.tooltip = new mBox.Tooltip({
                    content: node,
                    setStyles: {content: {padding: 15, lineHeight: 20}},
                    attach: this.node,
                    transition: 'flyin'
                });
            }

            //this.textNode.set("title", usersText);
        }.bind(this));
    }
});

MWF.xApplication.Selector.Group.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/groupicon.png)");
    },
    loadSubItem: function(){
        this.selector.orgAction.listPersonNested(this.data.id, function(json){
            this.textNode.set("text", this.textNode.get("text")+" ("+json.data.length+")");
            var usersText = "";
            json.data.each(function(item){
                usersText+=item.name+", ";
            }.bind(this));

            var node = new Element("div", {"styles": {"max-width": "300px"}, "text": usersText});

            if (!Browser.Platform.ios){
                this.tooltip = new mBox.Tooltip({
                    content: node,
                    setStyles: {content: {padding: 15, lineHeight: 20}},
                    attach: this.node,
                    transition: 'flyin'
                });
            }

            //this.textNode.set("title", usersText);
        }.bind(this));
    }
});


MWF.xApplication.Selector.Group.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "groups": [],
        "roles": []
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;

        if (this.options.groups.length || this.options.roles.length) key = this.getLikeKey(key);
        this.orgAction.listGroupByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
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