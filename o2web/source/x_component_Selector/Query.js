MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Query = new Class({
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
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectView});
    },
    _init : function(){
        this.selectType = "query";
        this.className = "Query";
    },
    loadSelectItems: function(addToNext){
        this.queryAction.listApplication(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    data.applicationName = data.name;
                    var item = this._newItem(data, this, this.itemAreaNode);
                    this.items.push(item);
                }.bind(this));
            }
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.viewList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return false;
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        this.items.each( function( item ){
            if( id == item.data.id ){
                if (callback) callback.apply(this, [item.data]);
            }
        }.bind(this));
        //this.queryAction.getView(function(json){
        //    if (callback) callback.apply(this, [json]);
        //}.bind(this), failure, ((typeOf(id)==="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Query.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Query.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Query.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }else{
                    return this.data.name === item.name;
                }
                //return (this.data.id === item.id) || (this.data.name === item.name) ;
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

MWF.xApplication.Selector.Query.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
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
//
//MWF.xApplication.Selector.Query.ItemCategory = new Class({
//    Extends: MWF.xApplication.Selector.Person.ItemCategory,
//    _getShowName: function(){
//        return this.data.name;
//    },
//    createNode: function(){
//        this.node = new Element("div", {
//            "styles": this.selector.css.selectorItemCategory_department
//        }).inject(this.container);
//    },
//    _setIcon: function(){
//        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
//    },
//    _hasChild: function(){
//        return (this.data.viewList && this.data.viewList.length);
//    },
//    check: function(){}
//});
