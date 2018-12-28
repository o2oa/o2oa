MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.Unit = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectUnit,
        "units": [],
        //"unitTypes": [],
        "values": [],
        "zIndex": 1000,
        "expand": true,
        "exclude" : [],
        "expandSubEnable" : true //是否允许展开下一层
    },

    loadSelectItems: function(addToNext){
        if (this.options.units.length){
            this.options.units.each(function(unit){
                // this.action.listUnitByKey(function(json){
                //     if (json.data.length){
                //         json.data.each(function(data){
                //             if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                //         }.bind(this));
                //     }
                // }.bind(this), null, comp);

                if (typeOf(unit)==="string"){
                    // this.orgAction.listUnitByKey(function(json){
                    //     if (json.data.length){
                    //         json.data.each(function(data){
                    //             if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                    //         }.bind(this));
                    //     }
                    // }.bind(this), null, unit);
                    this.orgAction.getUnit(function(json){
                        json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
                        if (json.data.length){
                            json.data.each( function(data){
                                if( this.options.expandSubEnable ){
                                    if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                                }else{
                                    var item = this._newItem( data, this, this.itemAreaNode);
                                }
                            }.bind(this));
                        }
                    }.bind(this), null, unit);


                }else{
                    this.orgAction.getUnit(function(json){
                        json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
                        if (json.data.length){
                            json.data.each( function(data){
                                if( this.options.expandSubEnable ) {
                                    if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                                }else{
                                    var item = this._newItem(data, this, this.itemAreaNode);
                                }
                            }.bind(this));
                        }
                    }.bind(this), null, unit.id);
                    //if (unit.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
                }

            }.bind(this));
        }else{
            this.orgAction.listTopUnit(function(json){
                json.data.each(function(data){
                    // var flag = true;
                    // if (this.options.unitTypes.length){
                    //     flag = data.typeList.some(function(item){
                    //         return (!this.options.unitTypes.length) || (this.options.unitTypes.indexOf(item)!==-1)
                    //     }.bind(this));
                    // }
                    // if (flag){
                    if( !this.isExcluded( data ) ) {
                        var unit = this._newItem(data, this, this.itemAreaNode, 1);
                    }
                    //unit.loadSubItem();
                    // }else{
                    //     var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                    // }

                }.bind(this));
            }.bind(this));
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level){
        return new MWF.xApplication.Selector.Unit[type](data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getUnit(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },

    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Unit.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByPinyininitial(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Unit.Item(data, selector, container, level);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Unit.SearchItem(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Unit.Item = new Class({
	Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/departmenticon.png)");
    },
    loadSubItem: function(){
        if( !this.selector.options.expandSubEnable )return;
        this.isExpand = (this.selector.options.expand);
        if (this.data.subDirectUnitCount){
            if (this.selector.options.expand){
                if (this.level===1){
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
                    this.loadSubItems();
                }else{
                    this.isExpand = false;
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                }
            }else{
                this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
            }
            this.levelNode.addEvent("click", function(e){
                if (this.isExpand){
                    this.children.setStyle("display", "none");
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                    this.isExpand = false;
                }else{
                    this.loadSubItems();
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
                    this.isExpand = true;
                }
                e.stopPropagation();
            }.bind(this));
        }

        //this.actionNode.setStyles((this.selector.options.expand) ? this.selector.css.selectorItemCategoryActionNode_expand : this.selector.css.selectorItemCategoryActionNode_collapse);
    },
    loadSubItems: function(){
        if (!this.loaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.node, "after");
            }
            this.children.setStyle("display", "block");
            //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level + 1);
                    }
                }.bind(this));
                this.loaded = true;
            }.bind(this), null, this.data.distinguishedName);
        }else{
            this.children.setStyle("display", "block");
        }
    },
    getData: function(callback){
        if (callback) callback();
    }
});
MWF.xApplication.Selector.Unit.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.Unit.Item,
    _getShowName: function(){
        return this.data.levelName || this.data.name;
    }
});

MWF.xApplication.Selector.Unit.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    getData: function(callback){
        if (callback) callback();
    },
    _getTtiteText: function(){
        return this.data.levelName || this.data.name;
    },
    _getShowName: function(){
        return this.data.name+((this.data.levelName) ? "("+this.data.levelName+")" : "");
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/departmenticon.png)");
    }
});

MWF.xApplication.Selector.Unit.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    loadSub: function(callback){
        if (!this.loaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    }
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

MWF.xApplication.Selector.Unit.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "units": []
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;


        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});