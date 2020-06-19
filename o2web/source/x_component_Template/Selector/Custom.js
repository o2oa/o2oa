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
        "expandSubEnable" : true
    },
    initialize: function (container, options) {
        this.setOptions(options);

        this.path = "../x_component_Selector/$Selector/";
        this.cssPath = "../x_component_Selector/$Selector/" + this.options.style + "/css.wcss";
        this._loadCss(true);
        this.container = $(container);
        this.selectedItems = [];
        this.items = [];
        this.categorys = [];
    },
    loadSelectItems: function (addToNext) {
        debugger;
        if (!this.options.category) {
            this.options.selectableItems.each(function (it) {
                var name = typeOf(it) === "string" ? it : it.name;
                var id = typeOf(it) === "string" ? it : it.id;
                var item = this._newItem({name: name, id: id}, this, this.itemAreaNode);
                this.items.push(item);
            }.bind(this))
        } else {
            this.options.selectableItems.each(function (item, index) {
                if (item.isItem) {
                    var item = this._newItem(item, this, this.itemAreaNode);
                    this.items.push(item);
                }else{
                    // if ( (item.subItemList && item.subItemList.length > 0) || item.subCategoryList && item.subCategoryList.length > 0 ) {
                    if( this.options.categorySelectable ){
                        var category = this._newItemCategorySelectable(item, this, this.itemAreaNode);
                        this.categorys.push( category );
                    }else{
                        var category = this._newItemCategory(item, this, this.itemAreaNode);
                        this.categorys.push( category );
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
    _getItem: function (callback, failure, id, async) {
        if (callback) callback.apply(id, [{"id": id}]);
    },
    _newItem: function (data, selector, container, level, category, delay) {
        return new MWF.xApplication.Template.Selector.Custom.Item(data, selector, container, level, category, delay);
    },
    createItemsSearchData: function (callback) {
        if (!this.itemsSearchData) {
            this.itemsSearchData = [];
            MWF.require("MWF.widget.PinYin", function () {
                var initIds = [];
                this.items.each(function (item) {
                    if (initIds.indexOf(item.data.name) == -1) {
                        var text = item._getShowName().toLowerCase();
                        var pinyin = text.toPY().toLowerCase();
                        var firstPY = text.toPYFirst().toLowerCase();
                        this.itemsSearchData.push({
                            "text": text,
                            "pinyin": pinyin,
                            "firstPY": firstPY,
                            "data": item.data
                        });
                        initIds.push(item.data.name);
                    }
                }.bind(this));
                delete initIds;
                if (callback) callback();
            }.bind(this));
        } else {
            if (callback) callback();
        }
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
            if (typeOf(item) === "object") return (this.data.id === item.id) || (this.data.name === item.name);
            if (typeOf(item) === "string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length) {
            this.selectedSingle();
        }
    },
    checkSelected: function () {
        var selectedItem = this.selector.selectedItems.filter(function (item, index) {
            return (item.data.id === this.data.id) || (item.data.name === this.data.name);
        }.bind(this));
        if (selectedItem.length) {
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
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
                return (item.data.id === this.data.id) || (item.data.name === this.data.name);
            }.bind(this));
            this.items = items;
            if (items.length) {
                items.each(function (item) {
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.Template.Selector.Custom.ItemCategory = new Class({
    Extends: o2.xApplication.Selector.Person.ItemCategory,
    _getShowName: function () {
        return this.data.name;
    },
    createNode: function () {
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
    clickItem: function (callback) {
        if (this._hasChild()) {
            var firstLoaded = !this.loaded;
            debugger;
            this.loadSub(function () {
                if (firstLoaded) {
                    if (!this.selector.isFlatCategory) {
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    }
                } else {
                    var display = this.children.getStyle("display");
                    if (display === "none") {
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    } else {
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                        this.isExpand = false;
                    }
                }
                if (callback) callback();
            }.bind(this));
        }
    },
    loadSub: function (callback) {
        debugger;
        if (!this.loaded) {
            if( this._hasChild() ){
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
    loadSubItems: function( callback ){
        if (!this.loaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.node, "after");
            }
            this.children.setStyle("display", "block");
            //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

            if( this._hasChild() ){
                this.data.subItemList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            if ( this._hasChildCategory() ) {
                this.data.subCategoryList.each(function (subCategory, index) {
                    var category = this.selector._newItemCategorySelectable(subCategory, this.selector, this.children, this.level + 1, this);
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

            if( this._hasChild() ){
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
    check: function () {
    }
});