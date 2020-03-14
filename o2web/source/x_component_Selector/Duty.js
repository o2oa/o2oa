MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Duty = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectDuty,
        "values": [],
        "expand": false
    },

    loadSelectItems: function(addToNext){
        this.orgAction.listUnitdutyName(function(json){
           json.data.nameList.each(function(data){
               var category = this._newItem(data, this, this.itemAreaNode);
               this.items.push( category );
           }.bind(this));
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level){
        return new MWF.xApplication.Selector.Duty[type](data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        this.orgAction.listUnitdutyNameByKey(function(json){
            if (callback) callback.apply(this, [{"data": json.data.nameList}]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback.apply(this, [{"data": {"name": id, "id":id}}]);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Duty.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.orgAction.listUnitdutyNameByKey(function(json){
            if (callback) callback.apply(this, [{"data": json.data.nameList}]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Duty.Item({"name":data, "id":data}, selector, container, level);
    }
});
MWF.xApplication.Selector.Duty.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        return this.data.name;
    },
    _setIcon: function() {
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url(" + "/x_component_Selector/$Selector/"+style+"/icon/duty.png)");
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

MWF.xApplication.Selector.Duty.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/duty.png)");
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
