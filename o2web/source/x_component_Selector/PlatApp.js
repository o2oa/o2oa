MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.PlatApp = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectProcess});
    },
    _init : function(){
        this.selectType = "platApp";
        this.className = "platApp";
    },
    loadSelectItems: function(addToNext){
	    var categorys = [
            {"name": MWF.SelectorLP.processPlatform, "id": "processPlatform"},
            {"name": MWF.SelectorLP.cms, "id": "cms"},
            {"name": MWF.SelectorLP.portal, "id": "portal"},
            {"name": MWF.SelectorLP.query, "id": "query"},
            {"name": MWF.SelectorLP.service, "id": "service"}
        ]
        //window.setTimeout(function(){
            categorys.each(function(data){
                switch (data.id){
                    case "processPlatform":
                        this.processAction.listApplication(function(json){ data.appList = json.data; }.bind(this), null, false);
                        break;
                    case "cms":
                        this.cmsAction.listColumn(function(json){ data.appList = json.data; }.bind(this), null, false);
                        break;
                    case "portal":
                        this.portalDesignerAction.listApplication(function(json){ data.appList = json.data; }.bind(this), null, false);
                        break;
                    case "query":
                        this.queryAction.listApplication(function(json){ data.appList = json.data; }.bind(this), null, false);
                        break;
                    case "service":
                        data.appList = [
                            { "id": "invoke", "name": MWF.SelectorLP.service1 },
                            { "id": "agent", "name": MWF.SelectorLP.agent }
                        ];
                        break;
                    default:
                        this.processAction.listApplication(function(json){ data.appList = json.data; }.bind(this), null, false);
                };

                var category = this._newItemCategory(data, this, this.itemAreaNode);
                if (data.appList && data.appList.length){
                    data.appList.each(function(d){
                        debugger;
                        d.moduleType = data.id;
                        if (d.moduleType=="cms") d.name = d.appName;
                        var item = this._newItem(d, this, category.children);
                        this.items.push(item);
                    }.bind(this));
                }
            }.bind(this));
        //}.bind(this), 1);
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
	    debugger;
        return data.appList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.PlatApp.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        if( !id )return;
        this.processAction.getProcess(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : (typeOf(id)=="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.PlatApp.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.PlatApp.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.PlatApp.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
	    switch (this.data.moduleType){
            case "processPlatform":
                return this.data.name;
            case "cms":
                return this.data.appName;
            case "portal":
                return this.data.name;
            case "query":
                return this.data.name;
            case "service":
                return this.data.name;
            default:
                return this.data.name;
        };
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                // return (this.data.id === item.id);
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }else{
                    return this.data.name === item.name;
                }
            }
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
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.PlatApp.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        switch (this.data.moduleType){
            case "processPlatform":
                return this.data.name;
            case "cms":
                return this.data.appName;
            case "portal":
                return this.data.name;
            case "query":
                return this.data.name;
            case "service":
                return this.data.name;
            default:
                return this.data.name;
        };
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                debugger;
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

MWF.xApplication.Selector.PlatApp.ItemCategory = new Class({
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
    loadSub: function(callback){
        if (!this.loaded){
            if (this.data.id=="processPlatform"){
                this.selector.processAction.listApplication(function(subJson){
                    subJson.data.each(function(subData){
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                        this.selector.items.push( category );
                    }.bind(this));

                    this.loaded = true;
                    if (callback) callback();
                }.bind(this));
            }else{
                this.loaded = true;
                if (callback) callback();
            }


            // this.selector.action.listProcess(function(subJson){
            //     subJson.data.each(function(subData){
            //         subData.applicationName = this.data.name;
            //         subData.application = this.data.id;
            //         var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            //         this.selector.items.push( category );
            //     }.bind(this));
            //
            //     this.loaded = true;
            //     if (callback) callback();
            // }.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    },
    check: function(){}
});
