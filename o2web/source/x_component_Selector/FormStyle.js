MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.FormStyle = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectFormStyle,
        "values": [],
        "names": [],
        "expand": false,
        "mode" : "pc",
        "forceSearchInItem" : true
    },
    loadSelectItems: function(addToNext){
        var stylesUrl = "../x_component_process_FormDesigner/Module/Form/skin/config.json";
        MWF.getJSON(stylesUrl,{
                "onSuccess": function(json){
                    var category = this._newItemCategory({
                        name : "系统样式",
                        id : "stystem"
                    }, this, this.itemAreaNode);
                    Object.each(json, function(s, key){
                        if( s.mode.contains( this.options.mode ) ){
                            var d = {
                                name : s.name,
                                id : key
                            };
                            var item = this._newItem(d, this, category.children);
                            this.items.push(item);
                        }
                    }.bind(this));

                    var category = this._newItemCategory({
                        name : "脚本",
                        id : "script"
                    }, this, this.itemAreaNode);

                    var json = {};
                    var appJs = {};
                    o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.listNext("(0)", 500, function (scriptJson) {
                        o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.list(function (appJson) {
                            appJson.data.each( function (app) {
                                appJs[ app.id ] = app;
                            });
                            scriptJson.data.each( function (script) {
                                if( !json[script.application] ){
                                    json[script.application] = appJs[ script.application ];
                                    json[script.application].scriptList = [];
                                }
                                script.appName = appJs[ script.application ].name;
                                script.appId = script.application;
                                script.type = "script";
                                json[script.application].scriptList.push( script )
                            }.bind(this));
                            for( var application in json ){
                                var category = this._newItemCategory(json[application], this, category.children);
                                json[application].scriptList.each(function(d){
                                    var item = this._newItem(d, this, category.children);
                                    this.items.push(item);
                                }.bind(this));
                            }
                        }.bind(this))
                    }.bind(this));
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
        return data.scriptList || [];
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
            }else{
                return item.data.name === this.data.name;
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
                }else{
                    return item.data.name === this.data.name;
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
    _getShowName: function(){
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
        return (this.data.scriptList && this.data.scriptList.length);
    },
    check: function(){}
});
