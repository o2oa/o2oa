MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Group = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "groups": [],
        "roles": [],
        "values": [],
        "names": [],
        "include" : [],
        "selectType" : "group"
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectGroup});
    },
    _init : function(){
        this.selectType = "group";
        this.className = "Group";
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
    _newItemSelected: function(data, selector, item, selectedNode){
        return new MWF.xApplication.Selector.Group.ItemSelected(data, selector, item, selectedNode)
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
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/groupicon.png)");
    },
    loadSubItem: function(){
        if( !layout.mobile ){
            this.tooltip = new MWF.xApplication.Selector.Group.Tooltip(document.body, this.node, null, {}, {
                axis : "y",
                hiddenDelay : 0,
                displayDelay : 300,
                groupId: this.data.id
            });
            if( this.selector.tooltips ){
                this.selector.tooltips.push(this.tooltip);
            }
        }
    }
    // loadSubItem: function(){
    //     this.selector.orgAction.listPersonNested(this.data.id, function(json){
    //         this.textNode.set("text", this.textNode.get("text")+" ("+json.data.length+")");
    //         var usersText = "";
    //         json.data.each(function(item){
    //             usersText+=item.name+", ";
    //         }.bind(this));
    //
    //         var node = new Element("div", {"styles": {"max-width": "300px"}, "text": usersText});
    //
    //         if (!Browser.Platform.ios){
    //             this.tooltip = new mBox.Tooltip({
    //                 content: node,
    //                 setStyles: {content: {padding: 15, lineHeight: 20}},
    //                 attach: this.node,
    //                 transition: 'flyin'
    //             });
    //         }
    //
    //         //this.textNode.set("title", usersText);
    //     }.bind(this));
    // }
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
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/groupicon.png)");
    },
    loadSubItem: function(){
        if( !layout.mobile ){
            this.tooltip = new MWF.xApplication.Selector.Group.Tooltip(document.body, this.node, null, {}, {
                axis : "y",
                hiddenDelay : 0,
                displayDelay : 300,
                groupId: this.data.id
            });
            if( this.selector.tooltips ){
                this.selector.tooltips.push(this.tooltip);
            }
        }
    }
    // loadSubItem: function(){
    //     this.selector.orgAction.listPersonNested(this.data.id, function(json){
    //         this.textNode.set("text", this.textNode.get("text")+" ("+json.data.length+")");
    //         var usersText = "";
    //         json.data.each(function(item){
    //             usersText+=item.name+", ";
    //         }.bind(this));
    //
    //         var node = new Element("div", {"styles": {"max-width": "300px"}, "text": usersText});
    //
    //         if (!Browser.Platform.ios){
    //             this.tooltip = new mBox.Tooltip({
    //                 content: node,
    //                 setStyles: {content: {padding: 15, lineHeight: 20}},
    //                 attach: this.node,
    //                 transition: 'flyin'
    //             });
    //         }
    //
    //         //this.textNode.set("title", usersText);
    //     }.bind(this));
    // }
});




MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.Selector.Group.Tooltip = new Class({
    Extends: MTooltips,
    options:{
        nodeStyles: {
            "font-size" : "12px",
            "position" : "absolute",
            "max-width" : "300px",
            "min-width" : "180px",
            "z-index" : "1000001",
            "background-color" : "#fff",
            "padding" : "10px",
            "border-radius" : "8px",
            "box-shadow": "0 0 18px 0 #999999",
            "-webkit-user-select": "text",
            "-moz-user-select": "text",
            "line-height": "20px"
        },
        position : { //node 固定的位置
            x : "auto", //x轴上left center right,  auto 系统自动计算
            y : "top" //y 轴上top middle bottom, auto 系统自动计算
        },
        priorityOfAuto :{
            x : [ "center", "right", "left" ], //当position x 为 auto 时候的优先级
            y : [ "middle", "top", "bottom" ] //当position y 为 auto 时候的优先级, "middle", "top", "bottom"
        },
        offset : {
            x : 0,
            y : 0
        },
        isFitToContainer : true,
        overflow : "scroll"
    },
    _loadCustom : function( callback ){

        o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({
            groupList: [this.options.groupId]
        }, function(json){
            var d = json.data[0];
            var text = "";
            if( d.personList && d.personList.length ){
                text += MWF.xApplication.Selector.LP.person + ": " + d.personList.map(function(item){
                    return item.split("@")[0]
                }.bind(this)).join(",")+ "\n";
            }

            if( d.identityList && d.identityList.length ){
                text += MWF.xApplication.Selector.LP.identity + ": " + d.identityList.map(function(item){
                    return item.split("@")[0]
                }.bind(this)).join(",")+ "\n";
            }

            if( d.unitList && d.unitList.length ){
                text += MWF.xApplication.Selector.LP.unit + ": " + d.unitList.map(function(item){
                    return item.split("@")[0]
                }.bind(this)).join(",")+ "\n";
            }

            if( d.groupList && d.groupList.length ){
                text += MWF.xApplication.Selector.LP.group + ": " + d.groupList.map(function(item){
                    return item.split("@")[0]
                }.bind(this)).join(",")+ "\n";
            }

            var node = new Element("div", {"styles": {"max-width": "300px", "white-space": "pre-line"}, "text": text});

            node.inject( this.contentNode );
            if(callback)callback();

        }.bind(this))

    },
    _customNode : function( node, contentNode ){
        this.fireEvent("customContent", [contentNode, node]);
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

