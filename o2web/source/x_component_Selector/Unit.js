MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.Unit = new Class({
    Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "units": [],
        //"unitTypes": [],
        "values": [],
        "zIndex": 1000,
        "expand": true,
        "exclude" : [],
        "expandSubEnable" : true, //是否允许展开下一层
        "selectAllEnable" : true, //分类是否允许全选下一层
        "selectType" : "unit"
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectUnit});
    },
    _init : function(){
        this.selectType = "unit";
        this.className = "Unit";
    },
    loadSelectItems: function(addToNext){

        if( this.options.disabled ){
            this.afterLoadSelectItem();
            return;
        }

        var afterLoadSelectItemFun = this.afterLoadSelectItem.bind(this);

        if (this.options.units.length){
            var unitList = [];
            for( var i=0 ; i<this.options.units.length; i++ ){
                var unit = this.options.units[i];
                if( typeOf( unit ) === "string" ){
                    unitList.push(unit);
                }else if( typeOf(unit)==="object"){
                    unitList.push(unit.id ||  unit.distinguishedName || unit.unique || unit.levelName);
                }
            }
            o2.Actions.load("x_organization_assemble_express").UnitAction.listObject( {"unitList" : unitList} , function (json) {
                debugger;
                if (json.data.length){
                    json.data.each( function(data){
                        if( this.options.expandSubEnable ) {
                            if (data.subDirectUnitCount){
                                var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode );
                                this.subCategorys.push(category);
                            }
                        }else{
                            var item = this._newItem(data, this, this.itemAreaNode );
                            this.items.push( item );
                            this.subItems.push(item);
                        }
                    }.bind(this));
                }
                afterLoadSelectItemFun();
            }.bind(this), afterLoadSelectItemFun )

            // var unitLoaded = 0;
            //
            // var loadUnitSuccess = function () {
            //     unitLoaded++;
            //     if( unitLoaded === this.options.units.length ){
            //         afterLoadSelectItemFun();
            //     }
            // }.bind(this);
            // var loadUnitFailure = loadUnitSuccess;
            //
            // this.options.units.each(function(unit){
            //
            //     var container = new Element("div").inject( this.itemAreaNode );
            //
            //     // this.action.listUnitByKey(function(json){
            //     //     if (json.data.length){
            //     //         json.data.each(function(data){
            //     //             if (data.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
            //     //         }.bind(this));
            //     //     }
            //     // }.bind(this), null, comp);
            //
            //     if (typeOf(unit)==="string"){
            //         this.orgAction.getUnit(function(json){
            //             json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
            //             if (json.data.length){
            //                 json.data.each( function(data){
            //                     if( this.options.expandSubEnable ){
            //                         if (data.subDirectUnitCount){
            //                             var category = this._newItemCategory("ItemCategory", data, this, container );
            //                             this.subCategorys.push(category);
            //                         }
            //                     }else{
            //                         var item = this._newItem( data, this, container );
            //                         this.items.push( item );
            //                         this.subItems.push(item);
            //                     }
            //                 }.bind(this));
            //             }
            //             loadUnitSuccess();
            //         }.bind(this), loadUnitFailure, unit);
            //
            //
            //     }else{
            //         this.orgAction.getUnit(function(json){
            //             json.data = typeOf( json.data ) == "object" ? [json.data] : json.data;
            //             if (json.data.length){
            //                 json.data.each( function(data){
            //                     if( this.options.expandSubEnable ) {
            //                         if (data.subDirectUnitCount){
            //                             var category = this._newItemCategory("ItemCategory", data, this, container );
            //                             this.subCategorys.push(category);
            //                         }
            //                     }else{
            //                         var item = this._newItem(data, this, container );
            //                         this.items.push( item );
            //                         this.subItems.push(item);
            //                     }
            //                 }.bind(this));
            //             }
            //             loadUnitSuccess();
            //         }.bind(this), loadUnitFailure, unit.id);
            //         //if (unit.subDirectUnitCount) var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
            //     }
            //
            // }.bind(this));
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
                        this.items.push( unit );
                        this.subItems.push(unit);
                    }
                    //unit.loadSubItem();
                    // }else{
                    //     var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode);
                    // }

                }.bind(this));
                afterLoadSelectItemFun();
            }.bind(this));
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category, delay){
        return new MWF.xApplication.Selector.Unit[type](data, selector, item, level, category, delay)
    },

    _listItemByKey: function(callback, failure, key){
        if( this.options.expandSubEnable ){
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
        }else{
            if (key){
                this.initSearchArea(true);
                this.searchInItems(key);
            }else{
                this.initSearchArea(false);
            }
        }
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getUnit(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },

    _newItemSelected: function(data, selector, item, selectedNode){
        return new MWF.xApplication.Selector.Unit.ItemSelected(data, selector, item, selectedNode)
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
    _newItem: function(data, selector, container, level, category, delay){
        return new MWF.xApplication.Selector.Unit.Item(data, selector, container, level, category, delay);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Unit.SearchItem(data, selector, container, level);
    }
});

MWF.xApplication.Selector.Unit.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    load : function(){
        if( this.selector.isFlatCategory ){
            if( !this.justItem && this.selector.options.expandSubEnable && this.data.subDirectUnitCount ){
                this.loadCategoryForFlatCategory();
            }else if(!this.ignoreItem){
                this.loadForNormal(true);
            }
        }else{
            this.loadForNormal();
        }

    },

    loadForNormal : function( isNotLoadSubItem, container ){
        this.selector.fireEvent("queryLoadItem",[this]);

        if( !this.node )this.node = new Element("div", {
            "styles": this.selector.css.selectorItem
        }).inject(container || this.container);

        this.levelNode = new Element("div", {
            "styles": this.selector.css.selectorItemLevelNode
        }).inject(this.node);
        var indent = this.selector.options.level1Indent + (this.level-1)*this.selector.options.indent;
        this.levelNode.setStyle("width", ""+indent+"px");

        this.iconNode = new Element("div", {
            "styles": this.selector.css.selectorItemIconNode
        }).inject(this.node);
        this._setIcon();

        this.actionNode = new Element("div", {
            "styles": this.selector.css.selectorItemActionNode
        }).inject(this.node);
        if( ( this.selector.options.count.toInt() === 1 || this.selector.options.noSelectedContainer ) && this.selector.css.selectorItemActionNode_single  ){
            this.actionNode.setStyles( this.selector.css.selectorItemActionNode_single );
        }

        this.textNode = new Element("div", {
            "styles": this.selector.css.selectorItemTextNode,
            "text": this._getShowName(),
            "title": this._getTtiteText()
        }).inject(this.node);
        this.textNode.store("indent", indent);
        var m = this.textNode.getStyle("margin-left").toFloat()+indent;
        this.textNode.setStyle("margin-left", ""+m+"px");

        if(this.postLoad)this.postLoad();

        if(!isNotLoadSubItem)this.loadSubItem();

        this.setEvent();

        this.check();

        if(this.afterLoad)this.afterLoad();

        this.selector.fireEvent("postLoadItem",[this]);
    },
    _getShowName: function(){
        return (this.isShowLevelName && this.data.levelName) ? this.data.levelName : this.data.name;
    },
    _getTtiteText: function(){
        return this.data.levelName || this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/departmenticon.png)");
    },
    loadSubItem: function(){
        if( !this.selector.options.expandSubEnable && !this.selector.options.forceShowSubEnable )return;
        this.isExpand = (this.selector.options.expand);
        if ( this._hasChild() || this.selector.options.expandEmptyCategory ){
            if (this.selector.options.expand){
                if( typeOf(this.selector.options.defaultExpandLevel) === "number" ){
                    if (this.level <= this.selector.options.defaultExpandLevel && this._hasChild() ){
                        this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
                        this.loadSubItems();
                    }else{
                        this.isExpand = false;
                        this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                    }
                }else if (this.level===1 && this._hasChild() ){
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
                    this.selector.fireEvent("collapse", [this] );
                    this.children.setStyle("display", "none");
                    this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_collapse);
                    this.isExpand = false;
                }else{
                    this.selector.fireEvent("expand", [this] );
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

            if( !this.selectAllNode && this.selector.options.count.toInt() !== 1 &&
                ( this.selector.options.style!=="blue_flat" && this.selector.options.style!=="blue_flat_mobile")){
                this.selectAllNode = new Element("div", {
                    "styles": this.selector.css.selectorItemCategoryActionNode_selectAll,
                    "title" : MWF.SelectorLP.selectChildren
                }).inject(this.textNode, "before");
                this.selectAllNode.addEvent( "click", function(ev){
                    if( this.isSelectedAll ){
                        // this.unselectAll(ev);
                        this.selector.options.selectAllRange === "all" ? this.unselectAllNested(ev, null, true) : this.unselectAll(ev, null, true);
                        this.selector.fireEvent("unselectCatgory",[this]);
                        this.selector.fireEvent("unselectCategory",[this])
                    }else{
                        // this.selectAll(ev);
                        if( this.selector.options.selectAllRange === "all" ){
                            var node = new Element("div.categorySelectedNode").inject( this.selector.selectedNode );
                            this.selectAllNested(ev, true, node)
                        }else{
                            this.selectAll(ev ,true)
                        }
                        this.selector.fireEvent("selectCatgory",[this]);
                        this.selector.fireEvent("selectCategory",[this])
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
    unselectAll : function(ev, exclude, checkValid){
        //( this.subItems || [] ).each( function(item){
        //    if(item.isSelected)item.unSelected();
        //}.bind(this));
        var excludeList = exclude || [];
        if( exclude && typeOf(exclude) !== "array"  )excludeList = [exclude];
        ( this.subItems || [] ).each( function(item){
            if(item.isSelected && !excludeList.contains(item) ){
                item.unSelected( checkValid );
            }
        }.bind(this));

        if( this.selectAllNode ){
            if( this.selector.isFlatCategory ){
                this.selectAllNode.setStyles( this.selector.css.flatCategory_selectAll );
            }else if(this.selector.css.selectorItemCategoryActionNode_selectAll){
                this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll );
            }
        }
        this.isSelectedAll = false;
    },
    unselectAllNested : function( ev, exclude, checkValid ){
        this.unselectAll(ev, exclude, checkValid );
        if( this.subCategorys && this.subCategorys.length ){
            this.subCategorys.each( function( category ){
                if(category.unselectAllNested)category.unselectAllNested( ev, exclude, checkValid )
            })
        }
        if( this.subItems && this.subItems.length ){
            this.subItems.each( function( item ){
                if(item.unselectAllNested)item.unselectAllNested( ev, exclude, checkValid )
            })
        }
    },
    selectAllNested : function(ev, checkValid, selectedNode){
        var node;
        if( selectedNode )node = new Element("div.categorySelectedNode").inject( selectedNode );
        this.selectAll(ev, checkValid, node, function () {
            if( this.subCategorys && this.subCategorys.length ){
                this.subCategorys.each( function( category ){
                    if(selectedNode)var node = new Element("div.categorySelectedNode").inject( selectedNode );
                    if(category.selectAllNested)category.selectAllNested(ev, checkValid, node)
                })
            }
            if( this.subItems && this.subItems.length ){
                this.subItems.each( function( item ){
                    if(selectedNode)var node = new Element("div.categorySelectedNode").inject( selectedNode );
                    if(item.selectAllNested)item.selectAllNested(ev, checkValid, node)
                })
            }
        }.bind(this));
    },
    selectAll: function(ev, checkValid, selectedNode, callback){
        if( this.loaded || this.selector.isFlatCategory ){
            this._selectAll( ev, checkValid, selectedNode );
            if(callback)callback();
        }else{
            this.loadSubItems(function(){
                this._selectAll( ev, checkValid, selectedNode );
                if(callback)callback();
            }.bind(this));
            this.levelNode.setStyles(this.selector.css.selectorItemLevelNode_expand);
            this.isExpand = true;
        }
    },
    _selectAll : function( ev, checkValid, selectedNode ){
        if( !this.subItems || !this.subItems.length )return;
        var count = this.selector.options.maxCount || this.selector.options.count;
        if (!count) count = 0;
        var selectedSubItemCount = 0;

        this.subItems.each( function(item){
            if(item.isSelected)selectedSubItemCount++
        }.bind(this));

        if ((count.toInt()===0) || (this.selector.selectedItems.length+(this.subItems.length-selectedSubItemCount))<=count){

            var doSelectAll = function () {
                var checkedCount = 0;
                this.subItems.each( function(item){
                    if(!item.isSelected && !item.disabled )item.selected(false, function () {
                        checkedCount++;
                        if( this.subItems.length === checkedCount ){
                            if( checkValid )this.selector.fireEvent("valid", [this.selector, this]);
                        }
                    }.bind(this), selectedNode);
                }.bind(this));

                if( this.selectAllNode ){
                    if( this.selector.isFlatCategory ){
                        this.selectAllNode.setStyles( this.selector.css.flatCategory_selectAll_selected );
                    }else if(this.selector.css.selectorItemCategoryActionNode_selectAll_selected){
                        this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll_selected );
                    }
                }

                this.isSelectedAll = true;
            }.bind(this);

            if( this._beforeSelectAll ){
                this._beforeSelectAll( doSelectAll );
            }else{
                doSelectAll();
            }

        }else{
            MWF.xDesktop.notice("error", {x: "right", y:"top"}, MWF.SelectorLP.selectItemMaxText.replace("{count}", count), this.node);
        }
    },
    checkSelectAll : function(){
        if( this.isSelectedAll )return;
        if( !this.selectAllNode )return;
        if( !this.subItems )return;
        var isAllItemSelected = true;
        for( var i=0; i< this.subItems.length; i++ ){
            if( !this.subItems[i].isSelected ){
                isAllItemSelected = false;
                break;
            }
        }
        if( isAllItemSelected ){
            if( this.selector.isFlatCategory ){
                this.selectAllNode.setStyles( this.selector.css.flatCategory_selectAll_selected );
            }else if( this.selector.css.selectorItemCategoryActionNode_selectAll_selected ){
                this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll_selected );
            }
            this.isSelectedAll = true;
        }
    },
    checkUnselectAll : function(){
        if( !this.isSelectedAll )return;
        if( !this.selectAllNode )return;
        if( ! this.subItems )return;
        var hasSelectedItem = false;
        for( var i=0; i< this.subItems.length; i++ ){
            if( this.subItems[i].isSelected ){
                hasSelectedItem = true;
                break;
            }
        }
        if( !hasSelectedItem ){
            if( this.selector.isFlatCategory ){
                this.selectAllNode.setStyles( this.selector.css.flatCategory_selectAll );
            }else if( this.selector.css.selectorItemCategoryActionNode_selectAll ){
                this.selectAllNode.setStyles( this.selector.css.selectorItemCategoryActionNode_selectAll );
            }
            this.isSelectedAll = false;
        }
    },
    loadSubItems: function( callback ){
        if (!this.loaded && !this.loading){
            this.loading = true;
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
                        this.selector.items.push( category );
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( category );
                    }
                }.bind(this));
                this.loaded = true;
                this.loading = false;
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
        if( this.selector.options.style === "blue_flat" ) {
            if (this.level === 1) {
                var indent = 26;
                this.levelNode.setStyle("width", "" + indent + "px");
            } else {
                var indent = 26 + ( this.level - 1 ) * this.selector.options.indent;
                this.levelNode.setStyle("width", "" + indent + "px");
            }
        }
        //}else if( this.selector.options.style === "blue_flat_mobile" ){
        //    if( this.level === 1 ){
        //        var indent = 40;
        //        this.levelNode.setStyle("width", ""+indent+"px");
        //    }else{
        //        var indent = 40 + ( this.level -1 ) * this.selector.options.indent ;
        //        this.levelNode.setStyle("width", ""+indent+"px");
        //    }
        //}
    },


    loadCategoryForFlatCategory : function(){
        this.selector.fireEvent("queryLoadCategory",[this]);

        if( !this.flatCategoryItemNode ){
            this.flatCategoryItemNode = new Element("div.flatCategoryItemNode", {
                "styles": this.selector.css.flatCategoryItemNode,
                "title" : this._getTtiteText()
            });
            this.flatCategoryItemNode.store( "category", this );
            this.flatCategoryItemNode.store( "dn", this.data.distinguishedName );

            this.flatCategoryItemTextNode = new Element("div", {
                "styles": this.selector.css.flatCategoryItemTextNode,
                "text": this._getShowName(),
                "title": this._getTtiteText()
            }).inject(this.flatCategoryItemNode);

        }

        this.children = new Element("div", {
            "styles": this.selector.css.selectorItemCategoryChildrenNode
        }).inject(this.selector.itemAreaNode);
        this.children.setStyle("display", "none");

        if( this.level === 1 ){
            this.loadForNormal(true, this.children);
        }

        if( !this.selectAllNode && this.selector.options.count.toInt() !== 1 ){
            var selectAllWrap = new Element("div",{
                styles : this.selector.css.flatCategory_selectAllWrap
            }).inject(this.children);
            this.selectAllNode = new Element("div", {
                "styles": this.selector.css.flatCategory_selectAll,
                "text" : MWF.SelectorLP.selectAll
            }).inject(selectAllWrap);
            this.selectAllNode.addEvent( "click", function(ev){
                if( this.isSelectedAll ){
                    this.unselectAll(ev);
                    this.selector.fireEvent("unselectCatgory",[this]);
                    this.selector.fireEvent("unselectCategory",[this])
                }else{
                    this.selectAll(ev);
                    this.selector.fireEvent("selectCatgory",[this]);
                    this.selector.fireEvent("selectCategory",[this]);
                }
                ev.stopPropagation();
            }.bind(this));
        }

        //this.loadForNormal(true, this.children);


        this.flatCategoryItemNode.addEvents({
            //"mouseover": function(){
            //    if (!this.isSelected )this.node.setStyles(this.selector.css.flatCategoryItemNode_over );
            //}.bind(this),
            //"mouseout": function(){
            //    if (!this.isSelected )this.node.setStyles(this.selector.css.flatCategoryItemNode );
            //}.bind(this),
            "click": function(){
                if( this.selector.currentFlatCategory === this )return;
                if( this.selector.currentFlatCategory ){
                    this.selector.currentFlatCategory.clickFlatCategoryItem(null, true); //取消原来选择的
                }
                this.selector.currentFlatCategory = this;
                this.clickFlatCategoryItem();
            }.bind(this)
        });
        //this.setEvent();

        var isCreateSubCategoryListNode = this.data.subDirectUnitCount ?  this.data.subDirectUnitCount : true;
        var nodeContainer;
        if( this.nodeContainer ){
            nodeContainer = this.nodeContainer;
        }else{
            nodeContainer = (this.category &&  this.category.subCategoryListNode) ? this.category.subCategoryListNode : null;
        }
        this.subCategoryListNode = this.selector.addFlatCategoryItem( this.flatCategoryItemNode, this.data.subDirectUnitCount, nodeContainer,  isCreateSubCategoryListNode );

        //this.check();

        if( this.loadCategoryChildren )this.loadCategoryChildren();

        //if(this.postLoad)this.postLoad();

        //this.setEvent();

        //this.check();

        this.selector.fireEvent("postLoadCategory",[this]);
    },
    clickFlatCategoryItem : function( callback, hidden ){
        //if (this._hasChildItem()){
        var firstLoaded = !this.itemLoaded;
        this.loadItemChildren(function(){
            if( hidden ){
                this.children.setStyles({ "display": "none" });
                this.flatCategoryItemNode.setStyles(this.selector.css.flatCategoryItemNode);
                this.isExpand = false;
            }else if( firstLoaded ){
                this.children.setStyles({"display": "block"});
                this.flatCategoryItemNode.setStyles( this.selector.css.flatCategoryItemNode_selected );
                this.isExpand = true;
            }else {
                var display = this.children.getStyle("display");
                if (display === "none") {
                    this.children.setStyles({ "display": "block" });
                    this.flatCategoryItemNode.setStyles(this.selector.css.flatCategoryItemNode_selected);
                    this.isExpand = true;
                } else {
                    this.children.setStyles({ "display": "none" });
                    this.flatCategoryItemNode.setStyles(this.selector.css.flatCategoryItemNode);
                    this.isExpand = false;
                }
            }
            if(callback)callback()
        }.bind(this));
        //}
    },
    loadCategoryChildren : function( callback ){
        if (!this.categoryLoaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        if( subData.subDirectUnitCount ){
                            var category = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this);
                            //if( !this.subItems )this.subItems = [];
                            //this.subItems.push( category );
                        }
                    }
                }.bind(this));
                this.categoryLoaded = true;
                if(callback)callback();
            }.bind(this), null, this.data.distinguishedName);
        }else{
            if(callback)callback();
        }
    },
    loadItemChildren : function( callback ){
        if (!this.itemLoaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        //if( !subData.subDirectUnitCount ){
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this, true);
                        category.justItem = true;
                        category.load();
                        this.selector.items.push( category );
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( category );
                        //}
                    }
                }.bind(this));
                this.itemLoaded = true;
                if(callback)callback();
            }.bind(this), null, this.data.distinguishedName);
        }else{
            if(callback)callback();
        }
    },
    _hasChild : function () {
        return this.data.subDirectUnitCount;
    }
});

MWF.xApplication.Selector.Unit.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.Unit.Item,
    load : function(){
        this.loadForNormal();
    },
    _getShowName: function(){
        return this.data.levelName || this.data.name;
    },
    loadSubItems: function( callback ){
        //只是为了在isFlatCategory模式下，加载全称用的，否则用继承的就可以
        if (!this.loaded && !this.loading){
            this.loading = true;
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
                        var category;
                        if( this.selector.isFlatCategory ){
                            category = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this, true);
                            category.isShowLevelName = true;
                            category.load();
                        }else{
                            category = this.selector._newItem(subData, this.selector, this.children, this.level + 1, this);
                        }
                        this.selector.items.push( category );
                        if( !this.subItems )this.subItems = [];
                        this.subItems.push( category );
                    }
                }.bind(this));
                this.loaded = true;
                this.loading = false;
                if(callback)callback();
            }.bind(this), null, this.data.distinguishedName);
        }else{
            this.children.setStyle("display", "block");
        }
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
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/departmenticon.png)");
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
                        category.isItem = true;
                        category.isCategory = true;
                        this.selector.items.push( category );
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
    _hasChildCategory: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        return uCount;
    },
    _hasChildItem: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        return uCount;
    },


    //for flat category start
    loadCategoryChildren: function(callback){
        if (!this.categoryLoaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1, this, true);
                        category.ignoreItem = true;
                        category.load();
                        //if(this.subItems)this.subItems.push( category );
                        this.subCategorys.push( category );
                    }
                    //var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.categoryLoaded = true;
                if (callback) callback();

            }.bind(this), null, this.data.distinguishedName);
        }else{
            if (callback) callback( );
        }
    },
    loadItemChildren: function(callback){
        if (!this.itemLoaded){
            this.selector.orgAction.listSubUnitDirect(function(subJson){
                subJson.data.each(function(subData){
                    if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1, this, true);
                        category.justItem = true;
                        category.load();
                        this.selector.items.push( category );
                        if(this.subItems)this.subItems.push( category );
                        //this.subCategorys.push( category );
                    }
                    //var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.itemLoaded = true;
                if (callback) callback();

            }.bind(this), null, this.data.distinguishedName);
        }else{
            if (callback) callback( );
        }
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
