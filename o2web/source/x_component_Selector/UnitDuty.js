MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.UnitDuty = new Class({
    Extends: MWF.xApplication.Selector.Identity,
    options: {
        "units": [],
        "values": [],
        "dutys": [],
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectDuty});
    },
    _init : function(){
        this.selectType = "duty";
        this.className = "UnitDuty";
    },
    loadSelectItems : function(){
        if (this.options.units.length){
            var unitList = this.options.units.map(function (unit) {
                return typeOf( unit ) === "string" ? unit : (unit.distinguishedName || unit.unique || unit.levelName || unit.id)
            });
            this._loadSelectItems( unitList );
        }else{
            this.orgAction.listTopUnit(function(json){
                var unitList = json.data.map(function (unit) {
                    return unit.distinguishedName;
                });
                this._loadSelectItems( unitList );
            }.bind(this));
        }
    },
    _loadSelectItems: function( unitList ){
        if( !unitList || !unitList.length )return;
        o2.Actions.load("x_organization_assemble_express").UnitAction.listObject( {"unitList" : unitList} , function (json) {
            if (json.data.length){
                json.data.each( function(data){
                    var category = this._newItemCategory("ItemCategory", data, this, this.itemAreaNode );
                    this.subCategorys.push(category);
                    this.subCategoryMap[data.levelName] = category;
                }.bind(this));
            }
        }.bind(this) );
    },
    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category, delay, notActive){
        return new MWF.xApplication.Selector.UnitDuty[type](data, selector, item, level, category, delay, notActive)
    },

    _listItem : function( callback, failure, key ){
        var units = ( this.options.units || [] ).map(function(u){
            return typeOf(u)==="string" ? u : u.distinguishedName;
        }).clean();
        var keyObj = units.length ? { "key": key, "unitList": units } : { "key": key };
        o2.Actions.load("x_organization_assemble_control").UnitDutyAction.listLike( keyObj, function (json) {
            if( json.data.length === 0 ){
                if (callback) callback.apply(this, []);
                return;
            }
            var unitIdList = json.data.map(function ( d ) { return d.unit; }).unique();
            o2.Actions.load("x_organization_assemble_express").UnitAction.listObject( {unitList: unitIdList}, function (unitJson) {
                var unitObjectMap = {};
                unitJson.data.each(function (u) {
                    unitObjectMap[u.matchKey] = u;
                });
                json.data.each(function (d) {
                    var u = unitObjectMap[d.unit];
                    if( u ){
                        d.unitLevelName = u.levelName;
                        d.woUnit = u;
                    }
                });
                if (callback) callback.apply(this, [json]);
            })
        }, failure );
    },

    _listItemByKey: function(callback, failure, key){
        this._listItem( callback, failure, key );
    },

    _getItem: function(callback, failure, id, async){
        this.orgAction.getIdentity(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item, selectedNode, delay){
        return new MWF.xApplication.Selector.UnitDuty.ItemSelected(data, selector, item, selectedNode, delay)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItem( callback, failure, key );
    },
    _newItem: function(data, selector, container, level, category, delay){
        return new MWF.xApplication.Selector.UnitDuty.Item(data, selector, container, level, category, delay);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.UnitDuty.SearchItem(data, selector, container, level);
    }
});


MWF.xApplication.Selector.UnitDuty.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        debugger;
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/duty.png)");
    },
    getData: function(callback, isWait){
        if( this.data.levelName ){
            if (callback) callback();
        }else if ( this.data.woUnit ) {
            this.data.unitLevelName = this.data.woUnit.levelName;
            if (callback) callback();
        }else if (this.data.unit){
            this.selector.orgAction.getUnit(function(json){
                this.data.woUnit = json.data;
                this.data.unitLevelName = this.data.woUnit.levelName;
                if (isWait && callback) callback();
            }.bind(this), function(xhr, text, error){
                return true;
            }.bind(this), this.data.unit)
        }else{
            if (callback) callback();
        }
    }
});
MWF.xApplication.Selector.UnitDuty.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.UnitDuty.Item,
    _getShowName: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    }
});

MWF.xApplication.Selector.UnitDuty.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    getData: function(callback, isWait){
        if( this.data.levelName ){
            if (callback) callback();
        }else if ( this.data.woUnit ) {
            this.data.unitLevelName = this.data.woUnit.levelName;
            if (callback) callback();
        }else if (this.data.unit){
            this.selector.orgAction.getUnit(function(json){
                this.data.woUnit = json.data;
                this.data.unitLevelName = this.data.woUnit.levelName;
                if (isWait && callback) callback();
            }.bind(this), function(xhr, text, error){
                return true;
            }.bind(this), this.data.unit)
        }else{
            if (callback) callback();
        }
    },
    _getShowName: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    },
    _getTtiteText: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/duty.png)");
    }
});

MWF.xApplication.Selector.UnitDuty.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department,
            "title" : this._getTtiteText()
        }).inject(this.container);
    },
    _getShowName: function(){
        // if( this._getTotalCount && this._getSelectedCount ){
        //     return "" + this._getTotalCount() + "-" + this._getSelectedCount() + "-" + this.data.name ;
        // }else{
        return this.data.name;
        // }
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+style+"/icon/companyicon.png)");
    },
    _loadSub: function(callback, notActive){
        if(notActive)this.subNotActive = true;
        if (!this.loaded){
            var loadSubUnit = function () {
                if( !this.categoryLoaded ){
                    o2.Actions.load("x_organization_assemble_express").UnitAction.listWithUnitSubDirectObject({
                            unitList: [this.data.distinguishedName],
                            countSubDirectUnit: true,
                            countSubDirectDuty: true
                        },
                        function(json){
                            json.data.each(function(subData){
                                if( !this.selector.isExcluded( subData ) ) {
                                    if( subData && this.data.parentLevelName)subData.parentLevelName = this.data.parentLevelName +"/" + subData.name;
                                    var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level + 1, this, false, notActive);
                                    this.subCategorys.push( category );
                                    this.subCategoryMap[subData.parentLevelName || subData.levelName] = category;
                                }
                            }.bind(this));
                            this.loaded = true;
                            if (callback) callback( true );
                        }.bind(this)
                    );
                }else{
                    this.loaded = true;
                    if (callback) callback( true );
                }
            }.bind(this);

            if( !this.itemLoaded ){
                o2.Actions.load("x_organization_assemble_control").UnitDutyAction.listWithUnit(function(idJson){
                    idJson.data.each(function(idSubData){
                        idSubData.unitLevelName = this.data.levelName;
                        idSubData.woUnit = this.data;
                        if( !this.selector.isExcluded( idSubData ) ) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this, notActive);
                            this.selector.items.push(item);
                            if(this.subItems)this.subItems.push( item );
                        }
                    }.bind(this));
                    loadSubUnit();
                }.bind(this), null, this.data.distinguishedName);
            }else{
                loadSubUnit();
            }

            // }
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        var iCount = (this.data.subDirectDutyCount) ? this.data.subDirectDutyCount : 0;
        return uCount + iCount;
    },
    _hasChildCategory: function(){
        return (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
    },
    _hasChildItem: function(){
        return (this.data.subDirectDutyCount) ? this.data.subDirectDutyCount : 0;
    },
    afterLoad: function(){
        if (this.level===1) this.clickItem();
    }
});