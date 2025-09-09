MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Widget = new Class({
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
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectWidget});
    },
    _init : function(){
        this.selectType = "widget";
        this.className = "Widget";
    },
    loadSelectItems: function(){
        o2.Actions.load('x_portal_assemble_surface').PortalAction.list().then((json)=>{
            var applications = json.data;
            applications.forEach((data)=>{
                o2.Actions.load('x_portal_assemble_surface').WidgetAction.list(data.id, (json)=>{
                    const widgets = json.data;
                    if (widgets.length){
                        data.widgetList = widgets;
                        const category = this._newItemCategory(data, this, this.itemAreaNode);
                        widgets.each(function(widget){
                            const item = this._newItem(widget, this, category.children);
                            this.items.push(item);
                        }.bind(this));
                    }
                });
            });
        });
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.widgetList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.Widget.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        if( !id )return;
        o2.Actions.load('x_portal_assemble_surface').WidgetAction.get(((typeOf(id)==="string") ? id : (typeOf(id)=="string") ? id : id.id), (json)=>{
            if (callback) callback.apply(this, [json]);
        }, failure, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Widget.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Widget.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Widget.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/widgeticon.png)");
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
            //return (item.data.id === this.data.id);
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

MWF.xApplication.Selector.Widget.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/widgeticon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
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

MWF.xApplication.Selector.Widget.ItemCategory = new Class({
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
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        // if (!this.loaded){
        //     this.selector.action.listProcess(function(subJson){
        //         subJson.data.each(function(subData){
        //             subData.applicationName = this.data.name;
        //             subData.application = this.data.id;
        //             var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
        //             this.selector.items.push( category );
        //         }.bind(this));

        //         this.loaded = true;
        //         if (callback) callback();
        //     }.bind(this), null, this.data.id);
        // }else{
        //     if (callback) callback();
        // }
    },
    _hasChild: function(){
        return true;
    },
    check: function(){}
});
