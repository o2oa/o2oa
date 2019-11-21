MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.Unit = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectUnit,
        "units": [],
        //"unitTypes": [],
        "values": [],
        "zIndex": 1000,
        "expand": true,
        "exclude" : [],
        "expandSubEnable" : true, //是否允许展开下一层
        "selectAllEnable" : true //分类是否允许全选下一层
    },

    loadSelectItems: function(addToNext){
        if (this.options.units.length){
            this.options.units.each(function(unit){
                // this.action.listUnitByKey(function(json){
                //     if (json.data.length){
                //         json.data.each(function(data){
                //             if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                //         }.bind(this));
                //     }
                // }.bind(this), null, comp);

                if (typeOf(unit)==="string"){
                    // this.orgAction.listUnitByKey(function(json){
                    //     if (json.data.length){
                    //         json.data.each(function(data){
                    //             if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                    //         }.bind(this));
                    //     }
                    // }.bind(this), null, unit);
                    this.orgAction.getUnit(function(json){
                        json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
                        if (json.data.length){
                            json.data.each( function(data){
                                if( this.options.expandSubEnable ){
                                    if (data.subDirectUnitCount){
                                        var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                                        this.subCategorys.push(category);
                                    }
                                }else{
                                    var item = this._newItem( data, this, this.itemAreaNode);
                                    this.subItems.push(item);
                                }
                            }.bind(this));
                        }
                    }.bind(this), null, unit);


                }else{
                    this.orgAction.getUnit(function(json){
                        json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
                        if (json.data.length){
                            json.data.each( function(data){
                                if( this.options.expandSubEnable ) {
                                    if (data.subDirectUnitCount){
                                        var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                                        this.subCategorys.push(category);
                                    }
                                }else{
                                    var item = this._newItem(data, this, this.itemAreaNode);
                                    this.subItems.push(item);
                                }
                            }.bind(this));
                        }
                    }.bind(this), null, unit.id);
                    //if (unit.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
                }

            }.bind(this));
        }else{
            this.orgAction.listTopUnit(function(json){
                json.data.each(function(data){
                    // var flag = true;
                    // if (this.options.unitTypes.length){
                    //     flag = data.typeList.some(function(item){
                    //         return (!this.options.unitTypes.length) || (this.options.unitTypes.indexOf(item)!==-1)
                    //     }.bind(this));
                    // }
                    // if (flag){
                    if( !this.isExcluded( data ) ) {
                        var unit = this._newItem(data, this, this.itemAreaNode, 1);
                        this.subItems.push(unit);
                    }
                    //unit.loadSubItem();
                    // }else{
                    //     var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                    // }

                }.bind(this));
            }.bind(this));
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category){
        return new MWF.xApplication.Selector.Unit[type](data, selector, item, level, category)
    },

    _listItemByKey: function(callback, failure, key){
        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getUnit(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },

    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Unit.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByPinyininitial(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level, category){
        return new MWF.xApplication.Selector.Unit.Item(data, selector, container, level, category);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Unit.SearchItem(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Unit.Item = new Class({
	Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/departmenticon.png)");
    },
    loadSubItem: function(){
        if( !this.selector.options.expandSubEnable )return;
        this.isExpand = (this.selector.options.expand);
        if (this.data.subDirectUnitCount){
            if (this.selector.options.expand){
                if (this.level===1){
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
                    this.loadSubItems();
                }else{
                    this.isExpand = false;
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                }
            }else{
                this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
            }
            this.levelNode.addEvent("click", function(e){
                if (this.isExpand){
                    this.children.setStyle("display", "none");
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                    this.isExpand = false;
                }else{
                    this.loadSubItems();
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
                    this.isExpand = true;
                }
                e.stopPropagation();
            }.bind(this));

            if( this.selector.css.selectorItemLevelNode_expand_over && this.selector.css.selectorItemLevelNode_collapse_over ){
                this.levelNode.addEvents({
                    mouseover : function(e){
                        var styles = this.isExpand ? this.selector.css.selectorItemLevelNode_expand_over : this.selector.css.selectorItemLevelNode_collapse_over;
                        this.levelNode.setStyles(styles);
                    }.bind(this),
                    mouseout : function(e){
                        var styles = this.isExpand ? this.selector.css.selectorItemLevelNode_expand : this.selector.css.selectorItemLevelNode_collapse;
                        this.levelNode.setStyles(styles);
                    }.bind(this)
                })
            }

            if( !this.selectAllNode && this.selector.options.count.toInt() !== 1 && this.selector.options.style!=="blue_flat" ){
                this.selectAllNode = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryActionNode_selectAll,
                    "title" : "全选下级"
                }).inject(this.textNode, "before");
                this.selectAllNode.addEvent( "click", function(ev){
                    if( this.isSelectedAll ){
                        this.unselectAll(ev);
                        this.selector.fireEvent("unselectCatgory",[this])
                    }else{
                        this.selectAll(ev);
                        this.selector.fireEvent("selectCatgory",[this])
                    }
                    ev.stopPropagation();
                }.bind(this));
                if( this.selector.css.selectorItemCategoryActionNode_selectAll_over ){
                    this.selectAllNode.addEvents( {
                        "mouseover" : function(ev){
                            if( !this.isSelectedAll )this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll_over );
                            //ev.stopPropagation();
                        }.bind(this),
                        "mouseout" : function(ev){
                            if( !this.isSelectedAll )this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll );
                            //ev.stopPropagation();
                        }.bind(this)
                    })
                }
            }
        }

        //this.actionNode.setStyles((this.selector.options.expand) ? this.selector.css.selectorItemCategoryActionNode_expand : this.selector.css.selectorItemCategoryActionNode_collapse);
    },
    unselectAll : function(ev, exclude){
        //( this.subItems || [] ).each( function(item){
        //    if(item.isSelected)item.unSelected();
        //}.bind(this));
        var excludeList = exclude || [];
        if( exclude && typeOf(exclude) !== "array"  )excludeList = [exclude];
        ( this.subItems || [] ).each( function(item){
            if(item.isSelected && !excludeList.contains(item) ){
                item.unSelected();
            }
        }.bind(this));
        if( this.selectAllNode && this.selector.css.selectorItemCategoryActionNode_selectAll ){
            this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll );
        }
        this.isSelectedAll = false;
    },
    unselectAllNested : function( ev, exclude ){
        this.unselectAll(ev, exclude );
        if( this.subCategorys && this.subCategorys.length ){
            this.subCategorys.each( function( category ){
                if(category.unselectAllNested)category.unselectAllNested( ev, exclude )
            })
        }
        if( this.subItems && this.subItems.length ){
            this.subItems.each( function( item ){
                if(item.unselectAllNested)item.unselectAllNested( ev, exclude )
            })
        }
    },
    selectAllNested : function(){
        this.selectAll();
        if( this.subCategorys && this.subCategorys.length ){
            this.subCategorys.each( function( category ){
                if(category.selectAllNested)category.selectAllNested()
            })
        }
        if( this.subItems && this.subItems.length ){
            this.subItems.each( function( item ){
                if(item.selectAllNested)item.selectAllNested()
            })
        }
    },
    selectAll: function(ev){
        if( this.loaded ){
            this._selectAll( ev )
        }else{
            this.loadSubItems(function(){
                this._selectAll( ev )
            }.bind(this));
            this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
            this.isExpand = true;
        }
    },
    _selectAll : function( ev ){
        if( !this.subItems || !this.subItems.length )return;
        var count = this.selector.options.maxCount || this.selector.options.count;
        if (!count) count = 0;
        var selectedSubItemCount = 0;
        this.subItems.each( function(item){
            if(item.isSelected)selectedSubItemCount++
        }.bind(this));
        if ((count.toInt()===0) || (this.selector.selectedItems.length+(this.subItems.length-selectedSubItemCount))<=count){
            this.subItems.each( function(item){
                if(!item.isSelected)item.selected();
            }.bind(this))
            if( this.selectAllNode && this.selector.css.selectorItemCategoryActionNode_selectAll_selected ){
                this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll_selected );
            }
            this.isSelectedAll = true;
        }else{
            MWF.xDesktop.notice("error", {x: "right", y:"top"}, "最多可选择"+count+"个选项", this.node);
        }
    },
    loadSubItems: function( callback ){
        if (!this.loaded){
            if (!this.children){
                this.children = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryChildrenNode
                }).inject(this.node, "after");
            }
            this.children.setStyle("display", "block");
            //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this);
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( category );
                    }
                }.bind(this));
                this.loaded = true;
                if(callback)callback();
            }.bind(this), null, this.data.distinguishedName);
        }else{
            this.children.setStyle("display", "block");
        }
    },
    getData: function(callback){
        if (callback) callback();
    },
    postLoad : function(){
        if( this.selector.options.style === "blue_flat" ){
            if( this.level === 1 ){
                var indent = 26;
                this.levelNode.setStyle("width", ""+indent+"px");
            }else{
                var indent = 26 + ( this.level -1 ) * this.selector.options.indent ;
                this.levelNode.setStyle("width", ""+indent+"px");
            }
        }
    }
});
MWF.xApplication.Selector.Unit.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.Unit.Item,
    _getShowName: function(){
        return this.data.levelName || this.data.name;
    }
});

MWF.xApplication.Selector.Unit.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    getData: function(callback){
        if (callback) callback();
    },
    _getTtiteText: function(){
        return this.data.levelName || this.data.name;
    },
    _getShowName: function(){
        return this.data.name+((this.data.levelName) ? "("+this.data.levelName+")" : "");
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/departmenticon.png)");
    }
});

MWF.xApplication.Selector.Unit.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    loadSub: function(callback){
        if (!this.loaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1, this);
                        if(this.subItems)this.subItems.push( category );
                        this.subCategorys.push( category );
                    }
                    //var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback();

            }.bind(this), null, this.data.distinguishedName);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        //var iCount = (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
        return uCount;
    },
    _hasChildItem: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        return uCount;
    }
});

MWF.xApplication.Selector.Unit.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "units": []
    },
    initialize: function(value, options){
        this.setOptions(options);
        this.value = value;
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    filter: function(value, callback){
        this.value = value;
        var key = this.value;


        if (this.options.units.length){
            var units = [];
            this.options.units.each(function(u){
                if (typeOf(u)==="string"){
                    units.push(u);
                }
                if (typeOf(u)==="object"){
                    units.push(u.distinguishedName);
                }
            });
            key = {"key": key, "unitList": units};
        }
        this.orgAction.listUnitByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});