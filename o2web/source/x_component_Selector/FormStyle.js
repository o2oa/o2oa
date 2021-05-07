MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.FormStyle = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "mode" : "pc",
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        if (!this.options.title) this.setOptions({"title": MWF.xApplication.Selector.LP.selectFormStyle});
    },
    _init : function(){
        this.selectType = "formstyle";
        this.className = "FormStyle";
    },
    loadSelectItems: function(addToNext){
        var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/config.json";
        MWF.getJSON(stylesUrl,{
                "onSuccess": function(json){
                    debugger;
                    var subItemList = [];
                    Object.each(json, function(s, key){
                        if( s.mode.contains( this.options.mode ) ){
                            subItemList.push({
                                name : s.name,
                                id : key
                            });

                        }
                    }.bind(this));
                    var category = this._newItemCategory({
                        name : MWF.SelectorLP.systemStyle,
                        id : "stystem",
                        subItemList : subItemList
                    }, this, this.itemAreaNode);

                    var json = {};
                    var array = [];
                    o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.listPaging(1, 1000, {}, function( scriptJson ){
                        scriptJson.data.each(function (script) {
                            if (!json[script.application]) {
                                json[script.application] = {
                                    name : script.applicationName,
                                    applicationName: script.applicationName,
                                    appName: script.applicationName,
                                    application: script.application,
                                    appId: script.application
                                };
                                json[script.application].scriptList = [];
                            }
                            script.appName = script.applicationName;
                            script.appId = script.application;
                            script.type = "script";
                            json[script.application].scriptList.push(script)
                        }.bind(this));
                        for (var application in json) {
                            if (json[application].scriptList && json[application].scriptList.length) {
                                json[application].scriptList.sort(function (a, b) {
                                    return (a.name||"").localeCompare((b.name||""));
                                });
                                array.push(json[application]);
                            }
                        }
                        array.sort( function (a, b) {
                            return (a.name||"").localeCompare((b.name||""));
                        });

                        var category = this._newItemCategory({
                            name : MWF.SelectorLP.customStyle,
                            id : "script",
                            applicationList : array
                        }, this, this.itemAreaNode);

                    }.bind(this));

                    // o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.listNext("(0)", 500, function (scriptJson) {
                    //     o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.list(function (appJson) {
                    //         appJson.data.each( function (app) {
                    //             appJs[ app.id ] = app;
                    //         });
                    //         scriptJson.data.each( function (script) {
                    //             if( !json[script.application] ){
                    //                 json[script.application] = appJs[ script.application ];
                    //                 json[script.application].scriptList = [];
                    //             }
                    //             script.appName = appJs[ script.application ].name;
                    //             script.appId = script.application;
                    //             script.type = "script";
                    //             json[script.application].scriptList.push( script )
                    //         }.bind(this));
                    //         for( var application in json ){
                    //             if( json[application].scriptList && json[application].scriptList.length ){
                    //                 array.push( json[application] );
                    //             }
                    //         }
                    //
                    //         var category = this._newItemCategory({
                    //             name : "自定义样式（脚本）",
                    //             id : "script",
                    //             applicationList : array
                    //         }, this, this.itemAreaNode);
                    //     }.bind(this))
                    // }.bind(this));
                }.bind(this)
            }
        );
        // this.processAction.listApplications(function(json){
        //     json.data.each(function(data){
        //         if (!data.scriptList){
        //             this.designerAction.listScript(data.id, function(scriptJson){
        //                 data.scriptList = scriptJson.data;
        //             }.bind(this), null, false);
        //         }
        //         if (data.scriptList && data.scriptList.length){
        //             var category = this._newItemCategory(data, this, this.itemAreaNode);
        //             data.scriptList.each(function(d){
        //                 d.applicationName = data.name;
        //                 var item = this._newItem(d, this, category.children);
        //                 this.items.push(item);
        //             }.bind(this));
        //         }
        //     }.bind(this));
        // }.bind(this));
    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.scriptList || data.subItemList || data.applicationList;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.FormStyle.ItemCategory(data, selector, item, level)
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
        return new MWF.xApplication.Selector.FormStyle.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.FormStyle.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.FormStyle.Item = new Class({
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

MWF.xApplication.Selector.FormStyle.ItemSelected = new Class({
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

MWF.xApplication.Selector.FormStyle.ItemCategory = new Class({
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
            if( this.data.subItemList ){
                this.data.subItemList.each(function (subItem, index) {
                    var item = this.selector._newItem(subItem, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if(this.subItems)this.subItems.push( item );
                }.bind(this));
            }
            if( this.data.scriptList ){
                this.data.scriptList.each(function (subItem, index) {
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
        return ( this.data.scriptList && this.data.scriptList.length ) ||
            ( this.data.subItemList && this.data.subItemList.length) ||
            ( this.data.applicationList && this.data.applicationList.length);
    },
    afterLoad: function(){
        if ( this._hasChild() ){
            this.clickItem();
        }
    },
    check: function(){}
});
