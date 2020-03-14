MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Selector = MWF.xApplication.Template.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Template.Selector.Custom = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "选择列表",
        "values": [],
        "selectableItems" : [],
        "names": [],
        "category" : false,
        "expand": false
    },
    initialize: function(container, options  ){
        this.setOptions(options);
        this.path = "/x_component_Selector/$Selector/";
        this.cssPath = "/x_component_Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss(true);
        this.container = $(container);
        this.selectedItems = [];
        this.items = [];
    },
    loadSelectItems: function(addToNext){
        if(!this.options.category){
            this.options.selectableItems.each( function( it ){
                var name = typeOf( it ) === "string" ? it : it.name;
                var id = typeOf( it ) === "string" ? it : it.id;
                var item = this._newItem({ name : name, id : id }, this, this.itemAreaNode );
                this.items.push(item);
            }.bind(this))
        }else{
            this.options.selectableItems.each(function (item, index) {
                if( item.isItem ){
                    var item = this._newItem(item, this, this.itemAreaNode);
                    this.items.push(item);
                }
                if(item.subItemList && item.subItemList.length>0){
                    var category = this._newItemCategory(item, this, this.itemAreaNode);
                    item.subItemList.each(function (subItem, index) {
                        var item = this._newItem(subItem, this, category.children, 2, category);
                        this.items.push(item);
                        category.subItems.push(item);
                    }.bind(this));
                }
            }.bind(this));
        }
    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.subItemList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Template.Selector.Custom.ItemCategory(data, selector, item, level)
    },
    _listItemByKey: function(callback, failure, key){
        if (key){
            this.initSearchArea(true);
            this.searchInItems(key);
        }else{
            this.initSearchArea(false);
        }
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Template.Selector.Custom.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        if (key){
            this.initSearchArea(true);
            this.searchInItems(key);
        }else{
            this.initSearchArea(false);
        }
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback.apply(id, [{"id":id}]);
    },
    _newItem: function(data, selector, container, level, category){
        return new MWF.xApplication.Template.Selector.Custom.Item(data, selector, container, level, category);
    },
    createItemsSearchData: function(callback){
        if (!this.itemsSearchData){
            this.itemsSearchData = [];
            MWF.require("MWF.widget.PinYin", function(){
                var initIds = [];
                this.items.each(function(item){
                    if (initIds.indexOf(item.data.name)==-1){
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
        }else{
            if (callback) callback();
        }
    }
});
MWF.xApplication.Template.Selector.Custom.Item = new Class({
    Extends: o2.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object") return (this.data.id === item.id) || (this.data.name === item.name) ;
            if (typeOf(item)==="string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return (item.data.id === this.data.id) || (item.data.name === this.data.name);
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});
MWF.xApplication.Template.Selector.Custom.ItemSelected = new Class({
    Extends: o2.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return (item.data.id === this.data.id) || (item.data.name === this.data.name);
            }.bind(this));
            this.items = items;
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    }
});
MWF.xApplication.Template.Selector.Custom.ItemCategory = new Class({
    Extends: o2.xApplication.Selector.Person.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
        this.loaded = true;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    _hasChildItem : function(){
        return this._hasChild();
    },
    _hasChild: function(){
        return (this.data.subItemList && this.data.subItemList.length);
    },
    check: function(){}
});