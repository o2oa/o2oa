MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.FormField = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectField,
        "fieldType": "",
        "values": [],
        "application": "",
        "include" : [],
        "expand": false,
        "forceSearchInItem" : true
    },

    loadSelectItems: function(addToNext){
        if (this.options.application){
            this.designerAction.listFormField(this.options.application, function(json){
                this.fieldData = json.data;
                if (this.options.fieldType){
                    var array = ( json.data[this.options.fieldType] || [] ).concat( this.options.include );
                    if ( array && array.length ){
                        array.each(function(data){
                            data.id = data.name;
                            var item = this._newItem(data, this, this.itemAreaNode);
                            this.items.push( item );
                        }.bind(this));
                    }
                }else{
                    Object.each(json.data, function(v, k){
                        var category = this._newItemCategory({"name": k, "data": v}, this, this.itemAreaNode);
                    }.bind(this));
                }
            }.bind(this));
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.FormField.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        var data = [];
        if (this.options.fieldType){
            data = this.fieldData[this.options.fieldType];
        }else{
            Object.each(this.fieldData, function(v, k){
                data = (data.length) ? data.concat(v) : v
            }.bind(this));
        }
        var searchData = [];
        data.each(function(d){
            if (d.name.toLowerCase().indexOf(key.toLowerCase())!==-1) searchData.push(d);
        }.bind(this));

        if (callback) callback.apply(this, [{"data": searchData}]);
        //if (callback) callback({"data": {"name": key, "id": key}});
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback.apply(this, [{"data": {"name": id, "id":id}}]);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.FormField.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItemByKey(callback, failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.FormField.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.FormField.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object") return (this.data.name === item.name) ;
            if (typeOf(item)==="string") return (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return item.data.name === this.data.name;
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    },
    loadSubItem: function(){
        return false;
        //    this.children = new Element("div", {
        //        "styles": this.selector.css.selectorItemCategoryChildrenNode
        //    }).inject(this.node, "after");
        //    this.children.setStyle("display", "block");
        ////    if (!this.selector.options.expand) this.children.setStyle("display", "none");
        //
        //    this.selector.action.listProcess(function(subJson){
        //        subJson.data.each(function(subData){
        //            var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
        //        }.bind(this));
        //    }.bind(this), null, this.data.id);
    },
    getData: function(callback){
        if (callback) callback();
    },
});

MWF.xApplication.Selector.FormField.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    getData: function(callback){
        if (callback) callback();
    },
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/processicon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return item.data.name === this.data.name;
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

MWF.xApplication.Selector.FormField.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            var subJson = this.selector.fieldData[this.data.name];

            //this.selector.action.listProcess(function(subJson){
                subJson.each(function(subData){
                    //subData.applicationName = this.data.name;
                    //subData.application = this.data.id;
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    this.selector.items.push( category );
                }.bind(this));

                this.loaded = true;
                if (callback) callback();
            //}.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        var d = this.selector.fieldData[this.data.name];
        return (d && d.length);
    }
});
