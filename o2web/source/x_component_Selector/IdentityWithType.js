MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.IdentityWithType = new Class({
    Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "units": [],
        "values": [],
        "dutys": [],
        "zIndex": 1000,
        "expand": false,
        "noUnit" : false,
        "include" : [], //增加的可选项
        "exclude" : [], //排除的可选项
        "resultType" : "", //可以设置成个人，那么结果返回个人
        "expandSubEnable" : true, //是否允许展开下一层,
        "selectAllEnable" : true,  //分类是否允许全选下一层
        "unitType": "",
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectIdentity});
    },
    _init : function(){
        this.selectType = "identity";
        this.className = "IdentityWithType";
        if( !this.options.expandSubEnable ){
            this.options.forceSearchInItem = true;
        }
    },
    loadSelectItems : function(){
        this.itemsMap = {};
        // this.selectedItemsMap = {}; //所有已选项按dn或id

        if( this.options.disabled ){
            this.afterLoadSelectItem();
            return;
        }

        this._loadSelectItems();

        if( this.isCheckStatusOrCount() ) {
            this.loadingCount = "wait";
            this.loadCount();
        }
    },
    _loadSelectItems: function(addToNext){
        var afterLoadSelectItemFun = this.afterLoadSelectItem.bind(this);
        if( this.options.resultType === "person" ){
            if( this.titleTextNode ){
                this.titleTextNode.set("text", MWF.xApplication.Selector.LP.selectPerson );
            }else{
                this.options.title = MWF.xApplication.Selector.LP.selectPerson;
            }
        }

        var data = {};
        data.type = this.options.unitType;
        data.unitList = [];
        this.options.units.each(function(unit){
            if (typeOf(unit)==="string"){
                data.unitList.push(unit);
            }else{
                data.unitList.push(unit.distinguishedName);
            }
        }.bind(this));

        // if( this.isCheckStatusOrCount() ) {
        //     this.loadingCount = "wait";
        //     this.loadCount( data.unitList );
        // }

        var loadUnitSuccess = function () {
            this.unitLoaded = true;
            if( this.includeLoaded ){
                afterLoadSelectItemFun();
            }
        }.bind(this);
        var loadUnitFailure = loadUnitSuccess;

        this.loadInclude( function () {
            this.includeLoaded = true;
            if( this.unitLoaded ){
                afterLoadSelectItemFun();
            }
        }.bind(this));

        //data.unitList = this.options.units;
        this.orgAction.listUnitByType(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    if( !this.isExcluded( data ) ) {
                        var category;
                        if ( (!this.options.unitType) || data.typeList.indexOf(this.options.unitType)!==-1){
                            category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                        }else{
                            if (data.woSubDirectUnitList.length){
                                category = this._newItemCategory("ItemCategoryNoItem", data, this, this.itemAreaNode);
                            }
                        }
                        if(category){
                            this.subCategorys.push(category);
                            this.subCategoryMap[data.levelName] = category;
                        }
                    }
                }.bind(this));
            }
            loadUnitSuccess();
        }.bind(this), loadUnitFailure, data);
    },

    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category, delay, notActive){
        return new MWF.xApplication.Selector.IdentityWithType[type](data, selector, item, level, category, delay, notActive)
    },

    _listItem : function( filterType, callback, failure, key ){
        var action = filterType === "key" ? "listIdentityByKey" : "listIdentityByPinyin";
        if ( this.options.units.length ){
            key = this.getLikeKey( key, this.options.units );
            this.orgAction[action](function(json){ //搜若units内的组织
                this.includeObject.listByFilter( filterType, key, function( array ){
                    json.data = array.concat( json.data || [] );
                    if (callback) callback.apply(this, [json]);
                }.bind(this));
            }.bind(this), failure, key);
        }else{  //搜索所有人
            this.orgAction[action](function(json){
                if (callback) callback.apply(this, [json]);
            }.bind(this), failure, key);
        }
    },

    _listItemByKey: function(callback, failure, key){
        this._listItem( "key", callback, failure, key );
    },

    _getItem: function(callback, failure, id, async){
        this.orgAction.getIdentity(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item, selectedNode, delay){
        return new MWF.xApplication.Selector.IdentityWithType.ItemSelected(data, selector, item, selectedNode, delay)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItem( "pinyin", callback, failure, key );
    },
    _newItem: function(data, selector, container, level, category, delay){
        return new MWF.xApplication.Selector.IdentityWithType.Item(data, selector, container, level, category, delay);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.IdentityWithType.SearchItem(data, selector, container, level);
    },
    getLikeKey : function( key, unitObjects ){
        var unitObjects = unitObjects || [];
        var units = [];
        unitObjects.each(function(u){
            if (typeOf(u)==="string"){
                units.push(u);
            }
            if (typeOf(u)==="object"){
                units.push(u.distinguishedName);
            }
        });
        var keyObj = { "key": key, "unitList": units };

        if( !this.dutyDnList ){
            var dutyDnList = [];
            var dutyNameList = [];
            ( this.options.dutys || [] ).each(function(d){
                if (typeOf(d)==="string"){
                    var ds = d.split("@");
                    if( ds[ ds.length - 1].toUpperCase() === "UD" ){
                        dutyDnList.push( d )
                    }else{
                        dutyNameList.push( d );
                    }
                }
                if (typeOf(d)==="object"){
                    if( d.distinguishedName ){
                        dutyDnList.push(d.distinguishedName);
                    }else if( d.name ){
                        dutyNameList.push(d.name);
                    }
                }
            });
            if( dutyNameList.length ){
                o2.Actions.get("x_organization_assemble_express").listDutyWithName( { nameList : dutyNameList }, function(json){
                    if( json.data && json.data.unitDutyList ){
                        dutyDnList = dutyDnList.concat( json.data.unitDutyList )
                    }
                }.bind(this), null, false);
            }
            this.dutyDnList = dutyDnList;
        }
        if( this.dutyDnList.length )keyObj.unitDutyList = this.dutyDnList;

        return units.length ? keyObj : key;
    }
});


MWF.xApplication.Selector.IdentityWithType.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    }
});
MWF.xApplication.Selector.IdentityWithType.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.IdentityWithType.Item,
    _getShowName: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    }
});

MWF.xApplication.Selector.IdentityWithType.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    _getShowName: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    },
    _getTtiteText: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    }
});

MWF.xApplication.Selector.IdentityWithType.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    _getShowName: function(){
        // if( this._getTotalCount && this._getSelectedCount ){
        //     return "" + this._getTotalCount() + "-" + this._getSelectedCount() + "-" + this.data.name ;
        // }else{
        return this.data.name;
        // }
    },
    _loadSub: function(callback, notActive){
        if(notActive)this.subNotActive = true;
        if (!this.loaded){
            var loadSubUnit = function () {
                if( this.selector.options.expandSubEnable && !this.categoryLoaded ){
                    this.data.woSubDirectUnitList.each(function( data ){
                        var category;
                        if( data && this.data.parentLevelName)data.parentLevelName = this.data.parentLevelName +"/" + data.name;
                        if ( (!this.selector.options.unitType) || data.typeList.indexOf(this.selector.options.unitType)!==-1){
                            category = this.selector._newItemCategory("ItemCategory", data, this.selector, this.children, this.level + 1, this, false, notActive);
                        }else{
                            if (data.woSubDirectUnitList.length){
                                category = this.selector._newItemCategory("ItemCategoryNoItem", data, this.selector, this.children, this.level + 1, this, false, notActive);
                            }
                        }
                        if(category){
                            this.subCategorys.push( category );
                            this.subCategoryMap[data.parentLevelName || data.levelName] = category;
                        }
                    }.bind(this));
                    this.loaded = true;
                    if (callback) callback( true );
                }else{
                    this.loaded = true;
                    if (callback) callback( true );
                }
            }.bind(this);

            if( !this.itemLoaded ){
                this.selector.orgAction.listIdentityWithUnit(function(idJson){
                    this.subDirectIdentityCount = idJson.data.length;
                    idJson.data.each(function(idSubData){
                        if( !this.selector.isExcluded( idSubData ) ) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this, notActive);
                            this.selector.items.push(item);
                            if(this.selector.addToItemMap)this.selector.addToItemMap(idSubData,item);
                            if(this.subItems)this.subItems.push( item );
                        }
                    }.bind(this));
                    loadSubUnit();
                }.bind(this), null, this.data.distinguishedName);
            }else{
                loadSubUnit();
            }

            // }
        }else{
            if (callback) callback( );
        }
    },
    _hasChild: function(){
        var uCount = (this.data.woSubDirectUnitList) ? this.data.woSubDirectUnitList : 0;
        var iCount = (this.subDirectIdentityCount) ? this.subDirectIdentityCount : 0;
        return uCount + iCount;
    },
    _hasChildCategory: function(){
        return (this.data.woSubDirectUnitList) ? this.data.woSubDirectUnitList : 0;
    },
    _hasChildItem: function(){
        return (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
    }
});

MWF.xApplication.Selector.IdentityWithType.ItemCategoryNoItem = new Class({
    Extends: MWF.xApplication.Selector.IdentityWithType.ItemCategory,
    _loadSub: function(callback, notActive){
        if(notActive)this.subNotActive = true;
        if (!this.loaded){
            var loadSubUnit = function () {
                if( this.selector.options.expandSubEnable && !this.categoryLoaded ){
                    this.data.woSubDirectUnitList.each(function( data ){
                        var category;
                        if( data && this.data.parentLevelName)data.parentLevelName = this.data.parentLevelName +"/" + data.name;
                        if ( (!this.selector.options.unitType) || data.typeList.indexOf(this.selector.options.unitType)!==-1){
                            category = this.selector._newItemCategory("ItemCategory", data, this.selector, this.children, this.level + 1, this, false, notActive);
                        }else{
                            if (data.woSubDirectUnitList.length){
                                category = this.selector._newItemCategory("ItemCategoryNoItem", data, this.selector, this.children, this.level + 1, this, false, notActive);
                            }
                        }
                        if(category){
                            this.subCategorys.push( category );
                            this.subCategoryMap[data.parentLevelName || data.levelName] = category;
                        }
                    }.bind(this));
                    this.loaded = true;
                    if (callback) callback( true );
                }else{
                    this.loaded = true;
                    if (callback) callback( true );
                }
            }.bind(this);

            loadSubUnit();

        }else{
            if (callback) callback( );
        }
    },
    _hasChild: function(){
        return this._hasChildCategory();
        // var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        // var iCount = (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
        // return uCount + iCount;
    },
    _hasChildCategory: function(){
        return (this.data.woSubDirectUnitList) ? this.data.woSubDirectUnitList : 0;
    }
});

MWF.xApplication.Selector.IdentityWithType.ItemUnitCategory = new Class({
    Extends: MWF.xApplication.Selector.IdentityWithType.ItemCategory
});

MWF.xApplication.Selector.IdentityWithType.ItemGroupCategory = new Class({
    Extends: MWF.xApplication.Selector.IdentityWithType.ItemCategory
});

MWF.xApplication.Selector.IdentityWithType.ItemRoleCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory
});

MWF.xApplication.Selector.IdentityWithType.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "units": [],
        "resultType" : "" //可以设置成个人，那么结果返回个人
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
            key = {"key": this.value, "unitList": units};
        }
        var data = null;
        this.orgAction.listIdentityByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});
