MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Unit", null, false);
MWF.xApplication.Selector.UnitWithType = new Class({
	Extends: MWF.xApplication.Selector.Unit,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectUnit,
        "units": [],
        "unitType": "",
        "values": [],
        "zIndex": 1000,
        "expand": true,
        "exclude" : [],
        "expandSubEnable" : true, //是否允许展开下一层
        "selectAllEnable" : true //分类是否允许全选下一层
    },

    loadSelectItems: function(addToNext){
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

        //data.unitList = this.options.units;
        this.orgAction.listUnitByType(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    if( !this.isExcluded( data ) ) {
                        if ( !this.options.expandSubEnable || (!this.options.unitType) || data.typeList.indexOf(this.options.unitType)!==-1){
                            var unit = this._newItem(data, this, this.itemAreaNode, 1);
                            this.subItems.push(unit);
                        }else{
                            if (data.woSubDirectUnitList.length){
                                var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                                this.subCategorys.push(category);
                            }
                        }
                    }
                }.bind(this));
            }
        }.bind(this), null, data);
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category){
        return new MWF.xApplication.Selector.UnitWithType[type](data, selector, item, level, category)
    },

    _listItemByKey: function(callback, failure, key){
        key = {"key": key};
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
            key.unitList = units;
        }
        if (this.options.unitType) key.type = this.options.unitType;
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
        return new MWF.xApplication.Selector.UnitWithType.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        key = {"key": key};
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
            key.unitList = units;
        }
        if (this.options.unitType) key.type = this.options.unitType;
        this.orgAction.listUnitByPinyininitial(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level, category, delay){
        return new MWF.xApplication.Selector.UnitWithType.Item(data, selector, container, level, category, delay);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.UnitWithType.SearchItem(data, selector, container, level);
    }
});
MWF.xApplication.Selector.UnitWithType.Item = new Class({
	Extends: MWF.xApplication.Selector.Unit.Item,
    _getShowName: function(){
        return (this.isShowLevelName && this.data.levelName) ? this.data.levelName : this.data.name;
    },
    _getTtiteText: function(){
        return this.data.levelName || this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/departmenticon.png)");
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

            if( !this.selectAllNode && !this.selector.isFlatCategory ){
                this.selectAllNode = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryActionNode_selectAll,
                    "title" : "全选下级"
                }).inject(this.textNode, "before");
                //this.selectAllNode.addEvent( "click", function(ev){
                //    this.selectAll(ev);
                //    ev.stopPropagation();
                //}.bind(this))
                this.selectAllNode.addEvent( "click", function(ev) {
                    if (this.isSelectedAll) {
                        this.unselectAll(ev);
                        this.selector.fireEvent("unselectCatgory", [this])
                    } else {
                        this.selectAll(ev);
                        this.selector.fireEvent("selectCatgory", [this])
                    }
                    ev.stopPropagation();
                });
            }
        }
    },
    loadSubItems: function(callback){
        if (!this.loaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.node, "after");
            }
            this.children.setStyle("display", "block");
            ( this.data.woSubDirectUnitList || [] ).each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType) !== -1) {
                        var unit = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this);
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( unit );
                    } else {
                        if (subData.woSubDirectUnitList.length){ // ? 原来是data.woSubDirectUnitList.length 2020-3-1
                            var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1, this);
                            this.subCategorys.push( category );
                        }
                    }
                }
                if(callback)callback()
            }.bind(this));
            this.loaded = true;
        }else{
            this.children.setStyle("display", "block");
        }
    },
    postLoad : function(){
    },

    //for flat category start
    loadCategoryChildren : function( callback ){
        if (!this.categoryLoaded){
            this.data.woSubDirectUnitList.each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType) !== -1) {
                    } else {
                        if (subData.woSubDirectUnitList.length){
                            var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1, this);
                            this.subCategorys.push( category );
                        }
                    }
                }
                if(callback)callback()
            }.bind(this));
            this.categoryLoaded = true;
        }else{
            //if(callback)callback();
        }
    },
    loadItemChildren : function( callback ){
        if (!this.itemLoaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.selector.itemAreaNode);
            }
            this.children.setStyle("display", "block");
            this.data.woSubDirectUnitList.each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType) !== -1) {
                        var unit = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this, true);
                        category.justItem = true;
                        category.load();
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( unit );
                    }
                }
                if(callback)callback()
            }.bind(this));
            this.itemLoaded = true;
        }else{
            this.children.setStyle("display", "block");
            //if(callback)callback();
        }
    }
    //for flat category end
});

MWF.xApplication.Selector.UnitWithType.SearchItem = new Class({
    //Extends: MWF.xApplication.Selector.Unit.Item,
    Extends: MWF.xApplication.Selector.UnitWithType.Item,
    load : function(){
        this.loadForNormal();
    },
    _getShowName: function(){
        return this.data.levelName || this.data.name;
    },
    loadSubItems: function( callback ){
    }
});

MWF.xApplication.Selector.UnitWithType.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Unit.ItemSelected,
    // _getShowName: function(){
    //     return this.data.levelName || this.data.name;
    // },
    _getTtiteText: function(){
        return this.data.levelName || this.data.name;
    },
    _getShowName: function(){
        return this.data.name+((this.data.levelName) ? "("+this.data.levelName+")" : "");
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/departmenticon.png)");
    }
});

MWF.xApplication.Selector.UnitWithType.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Unit.ItemCategory,

    loadSub: function(callback){
        if (!this.loaded){
            this.data.woSubDirectUnitList.each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType)!==-1){
                        var unit = this.selector._newItem(subData, this.selector, this.children, this.level+1, this);
                        if(this.subItems)this.subItems.push( unit );
                    }else{
                        if (subData.woSubDirectUnitList.length){
                            var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1, this);
                            this.subCategorys.push(category);
                        }
                    }
                }
                if( !this.subItems || !this.subItems.length ){
                    if( this.selectAllNode ){
                        this.selectAllNode.destroy();
                    }
                }
            }.bind(this));
            this.loaded = true;
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    afterLoad: function(){
        if (this.level===1) this.clickItem();
        var flag = false;
        this.data.woSubDirectUnitList.each(function(subData) {
            if (!this.selector.isExcluded(subData)) {
                if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType) !== -1) {
                    flag = true;
                }
            }
            if (!flag && this.selectAllNode)this.selectAllNode.destroy();
        }.bind(this));
    },
    _hasChild: function(){
        return this.data.woSubDirectUnitList.length;
    },
    _hasChildCategory: function(){
        return this.data.woSubDirectUnitList.length;
    },
    _hasChildItem: function(){
        if( typeOf(this.isHasChildItem) === "boolean" )return this.isHasChildItem;
        this.isHasChildItem = false;
        var unitList = this.data.woSubDirectUnitList || [];
        for( var i=0; i<unitList.length; i++){
            if ((!this.selector.options.unitType) || unitList[i].typeList.indexOf(this.selector.options.unitType) !== -1) {
                this.isHasChildItem = true;
                break;
            }
        }
        return this.isHasChildItem;
    },

    //for flat category start
    loadCategoryChildren: function(callback){
        if (!this.categoryLoaded){
            this.data.woSubDirectUnitList.each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType)!==-1){
                    }else{
                        if (subData.woSubDirectUnitList.length){
                            var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1, this);
                            this.subCategorys.push(category);
                        }
                    }
                }
            }.bind(this));
            this.categoryLoaded = true;
            if (callback) callback();
        }else{
            if (callback) callback( );
        }
    },
    loadItemChildren: function(callback){
        if (!this.itemLoaded){
            this.data.woSubDirectUnitList.each(function(subData){
                if( !this.selector.isExcluded( subData ) ) {
                    if ((!this.selector.options.unitType) || subData.typeList.indexOf(this.selector.options.unitType)!==-1){
                        var unit = this.selector._newItem(subData, this.selector, this.children, this.level+1, this);
                        if(this.subItems)this.subItems.push( unit );
                    }
                }
                if( !this.subItems || !this.subItems.length ){
                    if( this.selectAllNode ){
                        this.selectAllNode.destroy();
                    }
                }
            }.bind(this));
            this.itemLoaded = true;
            if (callback) callback();
        }else{
            if (callback) callback( );
        }
    }
});

MWF.xApplication.Selector.UnitWithType.Filter = new Class({
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
        key = {"key": key};

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
            key.unitList = units;
        }
        if (this.options.unitType) key.type = this.options.unitType;
        this.orgAction.listUnitByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), failure, key);

    }
});