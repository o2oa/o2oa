MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.TableField = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "fieldType": "",
        "values": [],
        "names": [],
        "tables": [],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        if (!this.options.title) this.setOptions({"title": MWF.xApplication.Selector.LP.selectField});
    },
    _init : function(){
        this.selectType = "tablefield";
        this.className = "TableField";
    },
    loadSelectItems: function(addToNext){
        // entity:实体类名称(系统表要全称如com.x.query.core.entity.Query，自建表只要名称)
        // entityCategory:实体类类型(自建表：dynamic|系统表：official)
	    this.options.tables.each(function (d) {
            var category = this._newItemCategory(d, this, this.itemAreaNode);
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.TableField.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback({"data": {"name": id, "id": id}});
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.TableField.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItemByKey(callback, failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.TableField.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.TableField.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name + ( this.data.description ? ("-" + this.data.description) : "" );
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    },
    loadSubItem: function(){
        return false;
    },
    getData: function(callback){
        if (callback) callback();
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object") return (this.data.name === item.name && item.tableName === this.data.tableName) ;
            if (typeOf(item)==="string") return (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return item.data.name === this.data.name && item.data.tableName === this.data.tableName;
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.TableField.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name + ( this.data.description ? ("-" + this.data.description) : "" );
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return item.data.name === this.data.name && item.data.tableName === this.data.tableName;
            }.bind(this));
            this.items = items;
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                    // if( this.selector.options.showSelectedCount ){
                    //     if(item.category)item.category._addSelectedCount( 1, true );
                    // }
                }.bind(this));
            }
        }
        if( this.afterCheck )this.afterCheck();
    },
});

MWF.xApplication.Selector.TableField.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            var d = this.data;
            o2.Actions.load("x_query_assemble_designer").QueryAction.getEntityProperties(
                d.entityCategory === "dynamic" ? d.table : d.entityClassName,
                d.entityCategory,
                function(json){
                    (json.data||[]).each( function ( field ) {
                        field.entityCategory = d.entityCategory;
                        field.tableName = d.name;
                        field.table = d.table;
                        field.entityClassName = d.entityClassName;
                        var item = this.selector._newItem(field, this.selector, this.children, this.level+1);
                        this.selector.items.push( item );
                    }.bind(this));
                }.bind(this)
            );

            this.loaded = true;
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    }
});
