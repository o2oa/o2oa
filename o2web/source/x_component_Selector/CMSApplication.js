MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.CMSApplication = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectCMSApplication,
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true
    },

    loadSelectItems: function(addToNext){
        this.cmsAction.listCMSApplication(function(json){
            json.data.each(function(data){
                data.name = data.appName;
                var category = this._newItem(data, this, this.itemAreaNode);
                this.items.push(category)
            }.bind(this));
        }.bind(this));
    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        this.cmsAction.getCMSApplication(function(json){
            if(json.data)json.data.name = json.data.appName;
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.CMSApplication.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.CMSApplication.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.CMSApplication.Item = new Class({
	Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.appName;
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
    },
    loadSubItem: function(){
        return false;
    },
    getData: function(callback){
        if (callback) callback();
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                // return (this.data.id === item.id) || (this.data.name === item.name) ;
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
            //return (item.data.id === this.data.id) || (item.data.name === this.data.name);
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

MWF.xApplication.Selector.CMSApplication.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    getData: function(callback){
        if (callback) callback();
    },
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
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
