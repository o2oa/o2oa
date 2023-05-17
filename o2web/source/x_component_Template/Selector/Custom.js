MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Selector = MWF.xApplication.Template.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xDesktop.requireApp("Selector", "Unit", null, false);

// this.options.selectableItems = [
//     {
//         "name": "项目1",
//         "id": "item1",
//         "isItem" : true //第一层的item需要isItem = true
//     },
//     {
//         "name": "项目2",
//         "id": "item2",
//         "isItem" : true //第一层的item需要isItem = true
//     },
//     {
//         "name": "分类1",
//         "id": "category1",
//         "subItemList": [
//             {
//                 "id": "item1.1",
//                 "name": "项目1.1"
//             },
//             {
//                 "id": "item1.2",
//                 "name": "项目1.2"
//             }
//         ],
//         "subCategoryList" : [
//             {
//                 "name": "分类1.1",
//                 "id": "category1.1",
//                 "subItemList" : [
//                     {
//                         "id": "item1.1.1",
//                         "name": "项目1.1.1"
//                     }
//                 ]
//             }
//         ]
//     }
// ];

MWF.xApplication.Template.Selector.Custom = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "选择列表",
        "values": [],
        "selectableItems": [],
        "names": [],
        "category": false,
        "expand": true,
        "categorySelectable" : false,
        "expandSubEnable" : true,
        "uniqueFlag" : false,
        "defaultExpandLevel" : 1
    },
    initialize: function (container, options) {
        this.setOptions(options);

        this.path = "../x_component_Selector/$Selector/";
        this.cssPath = "../x_component_Selector/$Selector/" + this.options.style + "/css.wcss";
        this._loadCss(true);
        this.container = $(container);
        this.selectedItems = [];
        this.items = [];
        // this.categorys = [];

        this.subCategorys = []; //直接的分类
        this.subItems = []; //直接的选择项

        this.availableStatusTypes = ["identity","custom"];

        this._init();
    },
    _init : function(){
        this.selectType = "custom";
        this.className = "Custom"
    },
    loadSelectItems: function (addToNext) {
        this.loadingCount = "done";
        if (!this.options.category) {
            this.options.selectableItems.each(function (it) {
                var name = typeOf(it) === "string" ? it : it.name;
                var id = typeOf(it) === "string" ? it : it.id;
                var item = this._newItem({name: name, id: id}, this, this.itemAreaNode);
                this.items.push(item);
                this.subItems.push( item );
            }.bind(this))
        } else {
            if( this.isCheckStatusOrCount() ){
                this.loadCount();
            }
            this.options.selectableItems.each(function (item, index) {
                if (item.isItem) {
                    var item = this._newItem(item, this, this.itemAreaNode);
                    this.items.push(item);
                    this.subItems.push( item );
                }else{
                    // if ( (item.subItemList && item.subItemList.length > 0) || item.subCategoryList && item.subCategoryList.length > 0 ) {
                    if( this.options.categorySelectable ){
                        var category = this._newItemCategorySelectable(item, this, this.itemAreaNode);
                        this.items.push(category);
                        this.subItems.push( category );
                        this.subCategorys.push( category );
                    }else{
                        var category = this._newItemCategory(item, this, this.itemAreaNode);
                        this.subCategorys.push( category );
                    }
                        // item.subItemList.each(function (subItem, index) {
                        //     var item = this._newItem(subItem, this, category.children, 2, category);
                        //     this.items.push(item);
                        //     category.subItems.push(item);
                        // }.bind(this));
                    // }
                }
            }.bind(this));
        }
        if(this.afterLoadSelectItem)this.afterLoadSelectItem();
    },
    _scrollEvent: function (y) {
        return true;
    },
    _getChildrenItemIds: function (data) {
        return data.subItemList || [];
    },
    _newItemCategory: function (data, selector, container, level, parentCategory, delay) {
        return new MWF.xApplication.Template.Selector.Custom.ItemCategory(data, selector, container, level, parentCategory, delay)
    },
    _newItemCategorySelectable: function (data, selector, container, level, category, delay) {
        return new MWF.xApplication.Template.Selector.Custom.ItemCategorySelectable(data, selector, container, level, category, delay)
    },
    _listItemByKey: function (callback, failure, key) {
        if (key) {
            this.initSearchArea(true);
            this.searchInItems(key);
        } else {
            this.initSearchArea(false);
        }
    },
    _newItemSelected: function (data, selector, container, level, category, delay) {
        return new MWF.xApplication.Template.Selector.Custom.ItemSelected(data, selector, container, level, category, delay)
    },
    _listItemByPinyin: function (callback, failure, key) {
        if (key) {
            this.initSearchArea(true);
            this.searchInItems(key);
        } else {
            this.initSearchArea(false);
        }
    },
    nestData : function( data, isItem ){
        if( !this.nestedData )this.nestedData = {};
        var setNest = function (d, isItem) {
            if( isItem ){
                this.nestedData[ d["id"] || d["name"] ] = d;
            }else if( this.options.categorySelectable ){
                this.nestedData[ d["id"] || d["name"] ] = { id : d.id , name : d.name };
                if( d.subItemList )this.nestData( d.subItemList, true  );
                if( d.subCategoryList )this.nestData( d.subCategoryList );
            }else{
                if( d.subItemList )this.nestData( d.subItemList, true );
                if( d.subCategoryList )this.nestData( d.subCategoryList );
            }
        }.bind(this);
        if( data ){
            for( var i=0; i<data.length; i++ ){
                var d = data[i];
                setNest(d, isItem );
            }
        }else{
            for( var i=0; i<this.options.selectableItems.length; i++ ){
                var d = this.options.selectableItems[i];
                setNest(d, d.isItem);
            }
        }
    },
    _getItem: function (callback, failure, id, async, data) {
        if( !this.nestedData )this.nestData();
        if (callback) callback.apply(id, [{ "data": this.nestedData[id] || {"id": id} }]);
    },
    _newItem: function (data, selector, container, level, category, delay) {
        return new MWF.xApplication.Template.Selector.Custom.Item(data, selector, container, level, category, delay);
    },
    createItemsSearchData: function (callback) {
        // if (!this.itemsSearchData) {
            this.itemsSearchData = [];
            this.getAllItemData();
            MWF.require("MWF.widget.PinYin", function () {
                var initIds = [];
                this.itemDataList.each(function (item) {
                    if (initIds.indexOf(item.name) === -1) {
                        var text = item.name.toLowerCase();
                        var pinyin = text.toPY().toLowerCase();
                        var firstPY = text.toPYFirst().toLowerCase();
                        this.itemsSearchData.push({
                            "text": text,
                            "pinyin": pinyin,
                            "firstPY": firstPY,
                            "data": item
                        });
                        initIds.push(item.name);
                    }
                }.bind(this));
                delete initIds;
                if (callback) callback();
            }.bind(this));
        // } else {
        //     if (callback) callback();
        // }
    },
    getAllItemData: function(){
        this.itemDataList = [];
        this.options.selectableItems.each(function (item, index) {
            if (item.isItem) {
                this.itemDataList.push(item);
            }else{
                if( this.options.categorySelectable ){
                    this.itemDataList.push(item);
                    this.flatItemData(item);
                }else{
                    this.flatItemData(item);
                }
            }
        }.bind(this));
    },
    flatItemData: function( c ){
        if( o2.typeOf( c.subItemList ) === "array" ){
            c.subItemList.each(function (item) {
                this.itemDataList.push(item)
            }.bind(this))
        }
        if( o2.typeOf( c.subCategoryList ) === "array" ){
            c.subCategoryList.each(function (item) {
                if( this.options.categorySelectable )this.itemDataList.push(item);
                this.flatItemData( item );
            }.bind(this));
        }
    },

    loadCount: function(){
        this.allCategoryDataObject = {};
        this.getValueString(this.options.values);
        this.calculateCount();
    },
    reloadCount: function(){
        var map = this.allCategoryDataObject;
        for(var key in map){
            map[key].subNestedItemCount = 0;
            map[key].selectedNestedItemCount = 0;
        }
        var values = this.selectedItems.map(function (item) { return item.data; }.bind(this));
        this.getValueString(values);
        this.calculateCount();
    },
    getValueString: function(values){
        this.valueStringList = values.map(function (item) {
            switch (o2.typeOf(item)) {
                case "string":
                    return item;
                case "object":
                    return this.options.uniqueFlag ? item[this.options.uniqueFlag] : (item.id || item.name);
                default:
                    return "";
            }
        }.bind(this));
    },
    calculateCount: function( data, parentLevelName ){
        var map = this.allCategoryDataObject;
        if( data ){
            var levelName = parentLevelName ? (parentLevelName+"/"+data.name) : data.name;
            if( !map[levelName] )map[levelName] = data;

            if( data.subItemList && data.subItemList.length ){

                var selectedItems = data.subItemList.filter(function(item){
                    var string = this.options.uniqueFlag ? item[this.options.uniqueFlag] : (item.id || item.name);
                    return this.valueStringList.contains(string);
                }.bind(this));

                var nameList = [];
                levelName.split("/").each(function(name){
                    nameList.push(name);
                    var lName = nameList.join("/");
                    var d = map[lName];
                    if( d ){
                        d.subNestedItemCount = (d.subNestedItemCount || 0) + data.subItemList.length;
                        d.selectedNestedItemCount = (d.selectedNestedItemCount || 0) + selectedItems.length;
                    }
                });
            }

            if( data.subCategoryList && data.subCategoryList.length ){
                data.subCategoryList.each(function (category) {
                    this.calculateCount( category, levelName );
                }.bind(this))
            }

        }else{
            this.options.selectableItems.each(function (item, i) {
                if( !item.isItem )this.calculateCount( item, "" );
            }.bind(this))
        }
    },
    addSelectedCount: function( itemOrItemSelected, count, items ){
        var itemData = itemOrItemSelected.data;
        debugger;
        items.each(function(item){
            if(item.category && item.category._addSelectedCount){
                item.category._addSelectedCount( count, true );
            }
        }.bind(this));
    }
});
MWF.xApplication.Template.Selector.Custom.Item = new Class({
    Extends: o2.xApplication.Selector.Person.Item,
    _getShowName: function () {
        return this.data.name;
    },
    _setIcon: function () {
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url(" + "../x_component_Selector/$Selector/" + style + "/icon/processicon.png)");
    },
    _getTtiteText: function () {
        return this.data.name;
    },
    loadSubItem: function () {
        return false;
    },
    checkSelectedSingle: function () {
        var selectedItem = this.selector.options.values.filter(function (item, index) {
            if( this.selector.options.uniqueFlag ){
                var flag = this.selector.options.uniqueFlag;
                if (typeOf(item) === "object") return ( this.data[flag] && this.data[flag] === item[flag] );
                if (typeOf(item) === "string") return ( this.data[flag] && this.data[flag] === item );
            }else{
                if (typeOf(item) === "object") return ( this.data.id && this.data.id === item.id) || (this.data.name && this.data.name === item.name);
                if (typeOf(item) === "string") return ( this.data.id && this.data.id === item) || (this.data.name && this.data.name === item);
            }
            return false;
        }.bind(this));
        if (selectedItem.length) {
            this.selectedSingle();
        }
    },
    checkSelected: function () {
        var selectedItem = this.selector.selectedItems.filter(function (item, index) {
            if( this.selector.options.uniqueFlag ){
                var flag = this.selector.options.uniqueFlag;
                return ( item.data[flag] && item.data[flag] === this.data[flag]);
            }else{
                return ( item.data.id && item.data.id === this.data.id) || (item.data.name && item.data.name === this.data.name);
            }
        }.bind(this));
        if (selectedItem.length) {
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    },
    destroy: function(){
        if( this.isSelected )this.unSelected();
        this.selector.items.erase( this );

        if( this.category ){
            if( this.category.subCategorys && this.category.subCategorys.length ){
                this.category.subCategorys.erase( this );
            }
            if( this.category.subItems && this.category.subItems.length ){
                this.category.subItems.erase( this );
            }
        }

        if(this.node)this.node.destroy();
        delete this;
    }
});
MWF.xApplication.Template.Selector.Custom.ItemSelected = new Class({
    Extends: o2.xApplication.Selector.Person.ItemSelected,
    _getShowName: function () {
        return this.data.name;
    },
    _setIcon: function () {
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url(" + "../x_component_Selector/$Selector/" + style + "/icon/processicon.png)");
    },
    _getTtiteText: function () {
        return this.data.name;
    },
    check: function () {
        if (this.selector.items.length) {
            var items = this.selector.items.filter(function (item, index) {
                if( this.selector.options.uniqueFlag ){
                    var flag = this.selector.options.uniqueFlag;
                    return ( item.data[flag] && item.data[flag] === this.data[flag]);
                }else{
                    return ( item.data.id && item.data.id === this.data.id) || (item.data.name && item.data.name === this.data.name);
                }
            }.bind(this));
            this.items = items;
            if (items.length) {
                items.each(function (item) {
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }

        debugger;

        if( !this.isFromValues ){
            if( this.selector.isCheckStatusOrCount() ){
                this.selector.addSelectedCount(this, 1, items||[]);
            }
        }

        if( this.afterCheck )this.afterCheck();
    }
});

MWF.xApplication.Template.Selector.Custom.ItemCategory = new Class({
    Extends: o2.xApplication.Selector.Person.ItemCategory,
    _getShowName: function () {
        return this.data.name;
    },
    createNode: function () {
        if( !this.levelName ){
            this.levelName  = this.category ? (this.category.levelName + "/" + this.data.name) : this.data.name;
        }
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function () {
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url(" + "../x_component_Selector/$Selector/" + style + "/icon/applicationicon.png)");
    },
    _getTtiteText: function () {
        return this.data.name;
    },
    _addSelectedCount : function( count, nested ){ //增加数字并向上回溯
        if( this.selector.loadingCount === "done" ){
            var c = ( this._getSelectedCount() || 0 ) + count;
            this.selectedCount = c;
            this._checkCountAndStatus( c );
        }else{
            this.selectedCount_wait = (this.selectedCount_wait || 0) + count;
        }
        if( nested && this.category && this.category._addSelectedCount ){
            this.category._addSelectedCount(count, nested);
        }
    },
    _getTotalCount : function(){
        // return this.subItems.length;
        if( !this.selector.allCategoryDataObject )return 0;
        var d =  this.selector.allCategoryDataObject[this.levelName];
        return d ? d.subNestedItemCount : 0;
    },
    _getSelectedCount : function(){
        if( typeOf(this.selectedCount) === "number" )return this.selectedCount;
        if( !this.selector.allCategoryDataObject )return 0;
        var d =  this.selector.allCategoryDataObject[this.levelName];
        var count = d ? d.selectedNestedItemCount : 0;
        this.selectedCount = count + (this.selectedCount_wait || 0);
        this.selectedCount_wait = 0;
        return this.selectedCount;
    },
    // _getSelectedCount : function(){
    //     var list = this.subItems.filter( function (item) { return item.isSelected; });
    //     return list.length;
    // },
    clickItem: function (callback) {
        if (this._hasChild() || this.selector.options.expandEmptyCategory ) {
            var firstLoaded = !this.loaded;
            if( !firstLoaded || this.selector.options.expandEmptyCategory  ){
                if(this.isExpand){
                    this.selector.fireEvent("collapse", [this] );
                }else{
                    this.selector.fireEvent("expand", [this] );
                }
            }
            this.loadSub(function () {
                if (firstLoaded && this._hasChild() ) {
                    if (!this.selector.isFlatCategory) {
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    }
                    // this.checkSelectAll();
                } else {
                    var display = this.children.getStyle("display");
                    if (display === "none") {
                        // this.selector.fireEvent("expand", [this] );
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    } else {
                        // this.selector.fireEvent("collapse", [this] );
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                        this.isExpand = false;
                    }
                }
                if (callback) callback();
            }.bind(this));
        }
    },
    destroy : function(){
        while( this.subItems.length )this.subItems[0].destroy();

        while( this.subCategorys.length )this.subCategorys[0].destroy();

        if( this.category && this.category.subCategorys && this.category.subCategorys.length ){
            this.category.subCategorys.erase( this );
        }

        if(this.node)this.node.destroy();
        delete this;
    },
    reloadSub : function(callback){
        while( this.subItems.length )this.subItems[0].destroy();
        this.subItems = [];

        while( this.subCategorys.length )this.subCategorys[0].destroy();
        this.subCategorys = [];

        this.loaded = false;
        this.loadSub( callback )
    },
    loadSub: function (callback) {
        if (!this.loaded) {
            if( this._hasChildItem() ){
                this.data.subItemList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            if ( this._hasChildCategory() ) {
                this.data.subCategoryList.each(function (subCategory, index) {
                    var category = this.selector._newItemCategory(subCategory, this.selector, this.children, this.level + 1, this);
                    this.subCategorys.push( category );
                }.bind(this));
            }

            this.loaded = true;
            if (callback) callback();
        } else {
            if (callback) callback();
        }
    },
    _hasChildCategory: function () {
        return (this.data.subCategoryList && this.data.subCategoryList.length);
    },
    _hasChildItem: function () {
        return (this.data.subItemList && this.data.subItemList.length);
    },
    _hasChild: function () {
        return this._hasChildCategory() || this._hasChildItem();
    },
    check: function () {
    },
    afterLoad: function(){
        if ( this.level <= this.selector.options.defaultExpandLevel && (this._hasChild())  ){
            this.clickItem();
        }else{
            this.children.hide();
            this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
            this.isExpand = false;
        }

        debugger;

        if( this.selector.isCheckStatusOrCount() ){
            if( this.selector.loadingCount === "done" ){
                this.checkCountAndStatus();
            }
        }
    },
    reloadCount: function () {
        if( this.selector.isCheckStatusOrCount() ){
            this.selector.reloadCount();
            if( this.selector.loadingCount === "done" ){
                this.checkCountAndStatus();
            }
        }
    }
});


MWF.xApplication.Template.Selector.Custom.ItemCategorySelectable = new Class({
    Extends: o2.xApplication.Selector.Unit.Item,
    _getShowName: function () {
        return this.data.name;
    },
    createNode: function () {
        // this.node = new Element("div", {
        //     "styles": this.selector.css.selectorItemCategory_department //this.selector.css.selectorItemCategory_department
        // }).inject(this.container);
    },
    _setIcon: function () {
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url(" + "../x_component_Selector/$Selector/" + style + "/icon/applicationicon.png)");
    },
    _getTtiteText: function () {
        return this.data.name;
    },

    _addSelectedCount : function( count, nested ){ //增加数字并向上回溯

    },
    _getTotalCount : function(){

    },
    _getSelectedCount : function(){

    },

    destroy : function(){
        if( this.isSelected )this.unSelected();
        this.selector.items.erase( this );

        while( this.subItems.length )this.subItems[0].destroy();

        while( this.subCategorys.length )this.subCategorys[0].destroy();

        if( this.category ){
            if( this.category.subCategorys && this.category.subCategorys.length ){
                this.category.subCategorys.erase( this );
            }
            if( this.category.subItems && this.category.subItems.length ){
                this.category.subItems.erase( this );
            }
        }

        if(this.node)this.node.destroy();
        delete this;
    },
    reloadSub : function(callback){
        while( this.subItems.length )this.subItems[0].destroy();
        this.subItems = [];

        while( this.subCategorys.length )this.subCategorys[0].destroy();
        this.subCategorys = [];

        this.loaded = false;
        this.loadSubItems( callback );
    },
    loadSubItems: function( callback ){
        if (!this.loaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.node, "after");
            }
            this.children.setStyle("display", "block");
            //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

            if( this._hasChildItem() ){
                this.data.subItemList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            if ( this._hasChildCategory() ) {
                this.data.subCategoryList.each(function (subCategory, index) {
                    var category = this.selector._newItemCategorySelectable(subCategory, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(category);
                    this.subCategorys.push( category );
                }.bind(this));
            }

            this.loaded = true;
            if(callback)callback();
        }else{
            this.children.setStyle("display", "block");
        }
    },
    loadCategoryChildren : function( callback ){
        if (!this.categoryLoaded){
            if ( this._hasChildCategory() ) {
                this.data.subCategoryList.each(function (subCategory, index) {
                    var category = this.selector._newItemCategorySelectable(subCategory, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(category);
                    this.subCategorys.push( category );
                }.bind(this));
            }

            this.categoryLoaded = true;
            if(callback)callback();
        }else{
            if(callback)callback();
        }
    },
    loadItemChildren : function( callback ){
        if (!this.itemLoaded){

            if( this._hasChildItem() ){
                this.data.subItemList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            this.itemLoaded = true;
            if(callback)callback();
        }else{
            if(callback)callback();
        }
    },
    _hasChildCategory: function () {
        return (this.data.subCategoryList && this.data.subCategoryList.length);
    },
    _hasChildItem: function () {
        return (this.data.subItemList && this.data.subItemList.length);
    },
    _hasChild: function () {
        return this._hasChildCategory() || this._hasChildItem();
    },
    checkSelectedSingle: function () {
        var selectedItem = this.selector.options.values.filter(function (item, index) {
            if( this.selector.options.uniqueFlag ){
                var flag = this.selector.options.uniqueFlag;
                if (typeOf(item) === "object") return ( this.data[flag] && this.data[flag] === item[flag] );
                if (typeOf(item) === "string") return ( this.data[flag] && this.data[flag] === item );
            }else{
                if (typeOf(item) === "object") return ( this.data.id && this.data.id === item.id) || (this.data.name && this.data.name === item.name);
                if (typeOf(item) === "string") return ( this.data.id && this.data.id === item) || (this.data.name && this.data.name === item);
            }
            return false;
        }.bind(this));
        if (selectedItem.length) {
            this.selectedSingle();
        }
    },
    checkSelected: function () {
        var selectedItem = this.selector.selectedItems.filter(function (item, index) {
            if( this.selector.options.uniqueFlag ){
                var flag = this.selector.options.uniqueFlag;
                return ( item.data[flag] && item.data[flag] === this.data[flag]);
            }else{
                return ( item.data.id && item.data.id === this.data.id) || (item.data.name && item.data.name === this.data.name);
            }
        }.bind(this));
        if (selectedItem.length) {
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    },
    check: function () {
        this.checkSelected();
    },
    afterLoad : function () {

    },
    reloadCount: function () {

    }
});