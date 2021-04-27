MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Dictionary = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "appType" : ["process","cms"],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        if (!this.options.title) this.setOptions({"title": MWF.xApplication.Selector.LP.selectDictionary});
    },
    _init : function(){
        this.selectType = "dictionary";
        this.className = "Dictionary";
    },
    loadSelectItems: function(addToNext){
        var json = {};
        this.options.appType.each( function (type) {
            var container = new Element("div").inject(this.itemAreaNode);

            var action;
            if( type === "process" ){
                action = o2.Actions.load("x_processplatform_assemble_designer").ApplicationDictAction.listPaging;
            }else if( type === "cms" ){
                action = o2.Actions.load("x_cms_assemble_control").AppDictDesignAction.listPaging;
            }

            var json = {};
            var array = [];
            action(1, 1000, {}, function( dictionaryJson ) {
                dictionaryJson.data.each(function (dictionary) {
                    var appName = dictionary.appName || dictionary.applicationName;
                    var appId = dictionary.appId || dictionary.application;
                    var appAlias = dictionary.appAlias || dictionary.applicationAlias;
                    if (!json[appId]) {
                        json[appId] = {
                            name: appName,
                            applicationName: appName,
                            appName: appName,
                            appAlias: appAlias,
                            application: appId,
                            appId: appId
                        };
                        json[appId].dictionaryList = [];
                    }
                    dictionary.appName = appName;
                    dictionary.appId = appId;
                    dictionary.appAlias = appAlias;
                    dictionary.appType = type;
                    dictionary.type = "dictionary";
                    json[appId].dictionaryList.push(dictionary)
                }.bind(this));
                for (var application in json) {
                    if (json[application].dictionaryList && json[application].dictionaryList.length) {
                        json[application].dictionaryList.sort(function (a, b) {
                            return (a.name||"").localeCompare((b.name||""));
                        });
                        array.push(json[application]);
                    }
                }
                array.sort( function (a, b) {
                    return (a.name||"").localeCompare((b.name||""));
                });

                if( this.options.appType.length === 1 ){
                    array.each( function (data) {
                        var category = this._newItemCategory(data, this, container);
                    }.bind(this))
                }else{
                    var category = this._newItemCategory({
                        name: MWF.xApplication.Selector.LP.appType[type],
                        id: type,
                        applicationList: array
                    }, this, container);
                }
            }.bind(this))
        }.bind(this));

    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.dictionaryList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.Dictionary.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        this.queryAction.getTable(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Dictionary.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Dictionary.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Dictionary.Item = new Class({
    Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }
                //return (this.data.id === item.id) || (this.data.name === item.name) ;
            }
            //if (typeOf(item)==="object") return (this.data.id === item.id) || (this.data.name === item.name) ;
            if (typeOf(item)==="string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){

        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            if( item.data.id && this.data.id){
                return item.data.id === this.data.id;
            }
            //return (item.data.id === this.data.id) || (item.data.name === this.data.name);
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.Dictionary.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                //return (item.data.id === this.data.id) || (item.data.name === this.data.name);
                if( item.data.id && this.data.id){
                    return item.data.id === this.data.id;
                }
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

MWF.xApplication.Selector.Dictionary.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemCategory,
    clickItem: function (callback) {
        if (this._hasChild() ) {
            var firstLoaded = !this.loaded;
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
    loadSub: function (callback) {
        if (!this.loaded) {
            if( this.data.dictionaryList ){
                this.data.dictionaryList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            if ( this.data.applicationList ) {
                this.data.applicationList.each(function (subCategory, index) {
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
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function () {
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    _hasChild: function(){
        return ( this.data.dictionaryList && this.data.dictionaryList.length ) ||
            ( this.data.applicationList && this.data.applicationList.length);
    },
    afterLoad: function(){
        if ( this._hasChild() ){
            this.clickItem();
        }
    },
    check: function(){}
});
