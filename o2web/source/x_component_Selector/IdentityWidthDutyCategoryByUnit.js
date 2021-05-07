MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "IdentityWidthDuty", null, false);
MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "dutys": [],
        "units": [],
        "values": [],
        "zIndex": 1000,
        "expand": false,
        "noUnit": false,
        "include": [], //增加的可选项
        "resultType": "", //可以设置成个人，那么结果返回个人
        "expandSubEnable": true,
        "selectAllEnable": true, //分类是否允许全选下一层
        "exclude": [],
        "dutyUnitLevelBy": "duty", //组织层级是按身份所在群组还是职务,
        "identitySortBy" : "identityNumber", //身份排序按身份排序号，还是传入的duty
        "selectType": "identity"
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectIdentity});
    },
    _init: function () {
        this.selectType = "identity";
        this.className = "IdentityWidthDutyCategoryByUnit";
    },
    // loadSelectItems: function (addToNext) {
    //     //根据组织分类展现职务
    //     if (this.options.resultType === "person") {
    //         if (this.titleTextNode) {
    //             this.titleTextNode.set("text", MWF.xApplication.Selector.LP.selectPerson);
    //         } else {
    //             this.options.title = MWF.xApplication.Selector.LP.selectPerson;
    //         }
    //     }
    //     if (this.options.disabled) {
    //         this.afterLoadSelectItem();
    //         return;
    //     }
    //
    //     if (this.options.dutys.length) {
    //         this.loadInclude(function () {
    //             this.includeLoaded = true;
    //             if (this.dutyLoaded) {
    //                 this.afterLoadSelectItem();
    //             }
    //         }.bind(this));
    //         if (this.options.units.length) {
    //
    //             var units = [];
    //             for (var i = 0; i < this.options.units.length; i++) {
    //                 var unit = this.options.units[i];
    //                 if (typeOf(unit) === "string") {
    //                     units.push(unit)
    //                 } else {
    //                     units.push(unit.distinguishedName || unit.unique || unit.id || unit.levelName)
    //                 }
    //             }
    //             this.unitStringList = units;
    //
    //             var getAllIdentity = function (unitList) {
    //                 o2.Actions.load("x_organization_assemble_express").UnitDutyAction.listIdentityWithUnitWithNameObject({
    //                     nameList: this.options.dutys,
    //                     unitList: unitList
    //                 }, function (json) {
    //                     this._loadSelectItems(json.data)
    //                 }.bind(this))
    //             }.bind(this);
    //
    //             if (this.options.expandSubEnable) {
    //                 o2.Actions.load("x_organization_assemble_express").UnitAction.listWithUnitSubNested({
    //                     unitList: units
    //                 }, function (json) {
    //                     getAllIdentity(units.combine(json.data ? json.data.unitList : []));
    //                 }.bind(this))
    //             } else {
    //                 getAllIdentity(units);
    //             }
    //         } else {
    //             var identityList = [];
    //             var count = 0;
    //             this.options.dutys.each(function (d) {
    //                 this.orgAction.listIdentityWithDuty(function (json) {
    //                     count++;
    //                     identityList = identityList.combine(json.data);
    //                     if (this.options.dutys.length === count) this._loadSelectItems(identityList);
    //                 }.bind(this), function () {
    //                     count++;
    //                     if (this.options.dutys.length === count) this._loadSelectItems(identityList);
    //                 }.bind(this), d);
    //             }.bind(this));
    //         }
    //     }
    // },
    getUnitUniqueFormDn : function(dn){
        if(!dn)return "";
        var arr = dn.split("@");
        if( arr.length === 3 )return arr[1];
        return dn;
    },
    loadSelectItems: function (addToNext) {
        //根据组织分类展现职务
        if (this.options.resultType === "person") {
            if (this.titleTextNode) {
                this.titleTextNode.set("text", MWF.xApplication.Selector.LP.selectPerson);
            } else {
                this.options.title = MWF.xApplication.Selector.LP.selectPerson;
            }
        }
        if (this.options.disabled) {
            this.afterLoadSelectItem();
            return;
        }

        if (this.options.dutys.length) {
            this.loadInclude(function () {
                this.includeLoaded = true;
                if (this.dutyLoaded) {
                    this.afterLoadSelectItem();
                }
            }.bind(this));

            var units = [];
            var unitUniques = [];
            for (var i = 0; i < this.options.units.length; i++) {
                var unit = this.options.units[i];
                if (typeOf(unit) === "string") {
                    units.push(unit);
                    unitUniques.push( this.getUnitUniqueFormDn(unit) );
                } else {
                    units.push(unit.distinguishedName || unit.unique || unit.id || unit.levelName);
                    unitUniques.push( unit.distinguishedName ? this.getUnitUniqueFormDn(unit.distinguishedName) : (unit.unique || unit.id || unit.levelName) );
                }
            }
            this.unitStringList = units;
            this.unitUniqueList = unitUniques;

            o2.Actions.load("x_organization_assemble_express").UnitDutyAction.listIdentityWithUnitWithNameObject({
                nameList: this.options.dutys,
                unitList: units,
                recursiveUnit : !!this.options.expandSubEnable
            }, function (json) {
                this.allIdentityData = json.data;
                this._loadSelectItems(json.data)
            }.bind(this))
        }else{
            this.afterLoadSelectItem();
        }
    },
    _loadSelectItems: function (identityList) {
        //this.listAllIdentityInUnitObject( identityList );
        var unitTree = this.listNestedUnitByIdentity(identityList);
        this.dataTree = unitTree;
        this.uniqueIdentity(unitTree);
        if (this.options.dutyUnitLevelBy === "duty") {
            this.level1Container = [];
            if (this.options.units && this.options.units.length) {
                this.options.units.each( function (unit ,i) {
                    var div = new Element("div").inject(this.itemAreaNode);
                    this.level1Container.push(div);
                }.bind(this))
            }
            this._loadSelectItemsByDutyUnit(unitTree);
        } else {
            this._loadSelectItemsByIdentityUnit(unitTree);
        }

        this.dutyLoaded = true;
        if (this.includeLoaded) {
            this.afterLoadSelectItem();
        }
    },
    _loadSelectItemsByIdentityUnit: function (unitTree) {
        if (!unitTree.unitList) return;
        this.sortUnit(unitTree.unitList);
        for (var i = 0; i < unitTree.unitList.length; i++) {
            var unit = unitTree.unitList[i];
            // if( !this.isExcluded( unit ) ) {
            var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
            this.subCategorys.push(category);
            // }
        }
    },
    sortUnit: function (unitList) {
        if (this.options.dutyUnitLevelBy === "duty") {
            if (this.options.units) {
                unitList.sort(function (a, b) {
                    var idxA = this.getIndexFromUnitOption(a);
                    var idxB = this.getIndexFromUnitOption(b);
                    if( a.orderNumber === 0 )a.orderNumber = -1;
                    if( b.orderNumber === 0 )b.orderNumber = -1;
                    idxA = idxA === -1 ? 9999999 + (a.orderNumber || 9999999) : idxA;
                    idxB = idxB === -1 ? 9999999 + (b.orderNumber || 9999999) : idxB;
                    return idxA - idxB;
                }.bind(this))
            } else {
                unitList.sort(function (a, b) {
                    if( a.orderNumber === 0 )a.orderNumber = -1;
                    if( b.orderNumber === 0 )b.orderNumber = -1;
                    return (a.orderNumber || 9999999) - (b.orderNumber || 9999999);
                }.bind(this))
            }
        } else {
            unitList.sort(function (a, b) {
                if( a.orderNumber === 0 )a.orderNumber = -1;
                if( b.orderNumber === 0 )b.orderNumber = -1;
                return (a.orderNumber || 9999999) - (b.orderNumber || 9999999);
            }.bind(this))
        }
    },
    _loadSelectItemsByDutyUnit: function (unitTree) {
        if (!unitTree.unitList) return;
        // this.sortUnit( unitTree.unitList );
        for (var i = 0; i < unitTree.unitList.length; i++) {
            var unit = unitTree.unitList[i];
            if (this.isUnitContain(unit)) {
                // if( !this.isExcluded( unit ) ) {
                var container = this.itemAreaNode;
                if (this.level1Container && this.level1Container.length) {
                    var index = this.getIndexFromUnitOption(unit);
                    if (index > -1 && (this.level1Container.length > index) && this.level1Container[index]) container = this.level1Container[index];
                }
                var category = this._newItemCategory("ItemCategory", unit, this, container);
                this.subCategorys.push(category);
                // }
            } else {
                this._loadSelectItemsByDutyUnit(unit);
            }
        }
    },
    getIndexFromUnitOption: function (unit) {
        if (!this.unitStringList || !this.unitStringList.length) return -1;
        var idx = -1;
        if (idx == -1 && unit.distinguishedName) idx = this.unitStringList.indexOf(unit.distinguishedName);
        if (idx == -1 && unit.id) idx = this.unitStringList.indexOf(unit.id);
        if (idx == -1 && unit.unique) idx = this.unitStringList.indexOf(unit.unique);
        if (idx == -1 && unit.levelName) idx = this.unitStringList.indexOf(unit.levelName);
        if (idx == -1 && unit.unique ) idx = this.unitUniqueList.indexOf(unit.unique);
        if (idx == -1 && !unit.unique && unit.distinguishedName ) idx = this.unitUniqueList.indexOf(unit.distinguishedName.split("@")[1]||"");
        return idx
    },
    isUnitContain: function (d) {
        if (this.options.units.length === 0) return true;
        if (!this.unitFlagMap) {
            this.unitFlagMap = {};
            this.options.units.each(function (e) {
                if (!e) return;
                this.unitFlagMap[typeOf(e) === "string" ? e : (e.distinguishedName || e.id || e.unique || e.employee || e.levelName)] = true;
            }.bind(this));
        }
        if (!this.unitUniqueMap) {
            this.unitUniqueMap = {};
            this.unitUniqueList.each(function (e) {
                if (!e) return;
                this.unitUniqueMap[e] = true;
            }.bind(this));
        }
        var map = this.unitFlagMap;
        var uniqueMap = this.unitUniqueMap;
        var flag = (d.distinguishedName && map[d.distinguishedName]) ||
            (d.levelName && map[d.levelName]) ||
            (d.id && map[d.id]) ||
            (d.unique && map[d.unique]) ||
            (d.unique && uniqueMap[d.unique]);
        if( !flag && !d.unique && d.distinguishedName ){
            var arr = d.distinguishedName.split("@");
            if( arr.length === 3 && arr[1] && uniqueMap[arr[1]] ){
                flag = true;
            }
        }
        return flag;
    },
    // listAllIdentityInUnitObject: function () {
    //     var unitArray = [];
    //     for (var i = 0; i < identityList.length; i++) {
    //         unitArray.push(identityList[i].unit || identityList[i].unitLevelName);
    //     }
    //     o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({
    //         unitList: unitArray
    //     }, function (json) {
    //         this.allIdentityInUnitObject = {};
    //         json.data.each(function (u) {
    //             this.allIdentityInUnitObject[u.levelName] = u;
    //         }.bind(this));
    //         if (callback) callback();
    //     }.bind(this), null, false)
    // },
    getUnitOrderNumber: function (unit) {
        return this.allIdentityInUnitObject[unit.levelName].orderNumber;
    },
    listAllUnitObject: function (identityList, callback) {
        var key = this.options.dutyUnitLevelBy === "duty" ? "matchUnitLevelName" : "unitLevelName";
        var unitArray = [];
        for (var i = 0; i < identityList.length; i++) {
            var levelNames = identityList[i][key];
            //if( !levelNames && key === "matchUnitLevelName" )levelNames = identityList[i].unitLevelName;
            var unitLevelNameList = levelNames.split("/");
            var nameList = [];
            for (var j = 0; j < unitLevelNameList.length; j++) {
                nameList.push(unitLevelNameList[j]);
                var name = nameList.join("/");
                if (!unitArray.contains(name)) {
                    unitArray.push(name);
                }
            }
        }
        o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({
            unitList: unitArray
        }, function (json) {
            this.allUnitObject = {};
            json.data.each(function (u) {
                this.allUnitObject[u.levelName] = u;
            }.bind(this));
            if (callback) callback();
        }.bind(this), null, false)
    },
    listNestedUnitByIdentity: function (identityList) {
        this.listAllUnitObject(identityList);
        return this._listNestedUnitByIdentity(identityList);
    },
    _listNestedUnitByIdentity: function (identityList) {
        debugger;
        //identityList = Array.unique(identityList);
        var key = this.options.dutyUnitLevelBy === "duty" ? "matchUnitLevelName" : "unitLevelName";
        //根据unitLevelName整合成组织树
        var unitTree = {};
        for (var i = 0; i < identityList.length; i++) {
            var flag = true;
            if (this.isExcluded(identityList[i])) continue;

            var levelNames = identityList[i][key];
            //if( !levelNames && key === "matchUnitLevelName" )levelNames = identityList[i].unitLevelName;
            var unitLevelNameList = levelNames.split("/");
            var nameList = [];
            var tree = unitTree;
            for (var j = 0; j < unitLevelNameList.length; j++) {
                nameList.push(unitLevelNameList[j]);
                var name = nameList.join("/");

                if (this.isExcluded(this.allUnitObject[name] || {})) {
                    flag = false;
                    break;
                }

                if (!tree.unitList) tree.unitList = [];
                var found = false;
                for (var k = 0; k < tree.unitList.length; k++) {
                    if (tree.unitList[k].levelName == name) {
                        tree = tree.unitList[k];
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // var obj = {};
                    var obj = this.allUnitObject[name] || {};
                    obj.matchLevelName = name;
                    tree.unitList.push(obj);
                    tree = obj;
                }
                // if( !tree.distinguishedName ){
                //     tree = Object.merge( tree, this.allUnitObject[name] );
                // }
                if (!tree.identityList) tree.identityList = [];

            }
            if (flag) tree.identityList.push(identityList[i]);
        }
        return unitTree;
    },
    uniqueIdentity: function (tree) {

        var map = {};
        var indexMap = {};

        var isExist = function (d, index) {
            if ((d.distinguishedName && map[d.distinguishedName]) ||
                (d.levelName && map[d.levelName]) ||
                (d.id && map[d.id]) ||
                (d.unique && map[d.unique])) {
                return true;
            } else {
                var key = typeOf(d) === "string" ? d : (d.distinguishedName || d.id || d.unique || d.employee || d.levelName);
                map[key] = true;
                indexMap[key] = index+1;
                return false;
            }
        };

        var getIndex = function (d){
            var index = ((d.distinguishedName && indexMap[d.distinguishedName]) ||
                (d.levelName && indexMap[d.levelName]) ||
                (d.id && indexMap[d.id]) ||
                (d.unique && indexMap[d.unique]));
            if( !index )index = 0;
            return index-1;
        };

        var identityList = [];
        if (tree.identityList) {
            for (var i = 0; i < tree.identityList.length; i++) {
                if (!isExist(tree.identityList[i], i)){
                    identityList.push(tree.identityList[i]);
                }else if( this.options.identitySortBy !== "identityNumber" ){
                    debugger;
                    var index = getIndex(tree.identityList[i]);
                    if( index > -1 ){
                        var identity = tree.identityList[index];
                        if( !identity.otherMatchUnitDutyList ){
                            identity.otherMatchUnitDutyList = []
                        }
                        identity.otherMatchUnitDutyList.push({
                            matchUnitDutyNumber : tree.identityList[i].matchUnitDutyNumber,
                            matchUnitDutyName : tree.identityList[i].matchUnitDutyName
                        })
                    }

                }
            }
        }
        tree.identityList = identityList;

        if (this.options.isCheckStatus) {
            var names = (tree.matchLevelName || tree.levelName || "").split("/");
            var nameList = [];
            for (var i = 0; i < names.length; i++) {
                nameList.push(names[i]);
                var name = nameList.join("/");
                var obj = this.allUnitObject[name];
                if (obj) {
                    obj.subNestedIdentityCount = (obj.subNestedIdentityCount || 0) + identityList.length;
                }
            }
        }

        if (tree.unitList) {
            for (var i = 0; i < tree.unitList.length; i++) {
                this.uniqueIdentity(tree.unitList[i]);
            }
        }
    },
    createItemsSearchData: function(callback){
        if (!this.itemsSearchData){
            this.itemsSearchData = [];
            MWF.require("MWF.widget.PinYin", function(){
                var initIds = [];

                var _self = this;
                function checkIdentity(data) {
                    var id = data.distinguishedName || data.id || data.name || data.text;
                    if (initIds.indexOf( id )==-1){
                        var text = data.name || "";
                        if( !text && data.distinguishedName ){
                            var dn = data.distinguishedName.split("@");
                            text = dn[0];
                        }
                        var pinyin = text.toPY().toLowerCase();
                        var firstPY = text.toPYFirst().toLowerCase();
                        _self.itemsSearchData.push({
                            "text": text,
                            "pinyin": pinyin,
                            "firstPY": firstPY,
                            "data": data
                        });
                        initIds.push( id );
                    }
                }

                function checkUnit(unit) {
                    if(unit.identityList && unit.identityList.length){
                        unit.identityList.each( function (identity) {
                            checkIdentity(identity);
                        })
                    }
                    if(unit.unitList && unit.unitList.length){
                        unit.unitList.each( function (unit) {
                            checkUnit(unit);
                        })
                    }
                }

                this.dataTree.unitList.each(function(unit){
                    checkUnit(unit);
                }.bind(this));
                delete initIds;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    //listNestedUnitByIdentity : function( identityList ){
    //    debugger;
    //    this.unitArray = [];
    //    var key = this.options.dutyUnitLevelBy === "duty" ? "matchUnitLevelName" : "unitLevelName";
    //    //根据unitLevelName整合成组织树
    //    var unitTree = {};
    //    for( var i=0; i<identityList.length; i++ ){
    //        var levelNames = identityList[i][key];
    //        //if( !levelNames && key === "matchUnitLevelName" )levelNames = identityList[i].unitLevelName;
    //        var unitLevelNameList = levelNames.split("/");
    //        var nameList = [];
    //        var tree = unitTree;
    //        for( var j=0; j<unitLevelNameList.length; j++ ){
    //            nameList.push( unitLevelNameList[j] );
    //            var name = nameList.join("/");
    //            if( !tree[ name ] ){
    //                tree[ name ] = {
    //                    name : unitLevelNameList[j],
    //                    levelName : name,
    //                    identityList : []
    //                };
    //                this.unitArray.push( name );
    //            }
    //            tree =  tree[name];
    //        }
    //        tree.identityList.push( identityList[i] );
    //    }
    //    return unitTree;
    //},
    _scrollEvent: function (y) {
        return true;
    },
    _getChildrenItemIds: function () {
        return null;
    },
    _newItemCategory: function (type, data, selector, item, level, category, delay) {
        return new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit[type](data, selector, item, level, category, delay)
    },

    _listItemByKey: function (callback, failure, key) {
        if (this.options.units.length) key = {"key": key, "unitList": this.options.units};
        this.orgAction.listIdentityByKey(function (json) {
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function (callback, failure, id, async) {
        if (typeOf(id) === "string") {
            this.orgAction.getIdentity(function (json) {
                if (callback) callback.apply(this, [json]);
            }.bind(this), failure, ((typeOf(id) === "string") ? id : id.distinguishedName), async);
        } else {
            if (callback) callback.apply(this, [id]);
        }
        //this.orgAction.getIdentity(function(json){
        //    if (callback) callback.apply(this, [json]);
        //}.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function (data, selector, item, selectedNode) {
        return new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.ItemSelected(data, selector, item, selectedNode)
    },
    _listItemByPinyin: function (callback, failure, key) {
        if (this.options.units.length) key = {"key": key, "unitList": this.options.units};
        this.orgAction.listIdentityByPinyin(function (json) {
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function (data, selector, container, level, category, delay) {
        return new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.Item(data, selector, container, level, category, delay);
    },
    _newItemSearch: function (data, selector, container, level) {
        return new MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.SearchItem(data, selector, container, level);
    }
    //_listItemNext: function(last, count, callback){
    //    this.action.listRoleNext(last, count, function(json){
    //        if (callback) callback.apply(this, [json]);
    //    }.bind(this));
    //}
});
MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.Item = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.Item
});
MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.SearchItem
});

MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.ItemSelected
});

MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.ItemCategory,
    _getShowName: function () {
        return this.data.name;
    },
    _addSelectedCount: function (count, nested) {
        debugger;
        var c = (this._getSelectedCount() || 0) + count;
        this.selectedCount = c;
        this.checkCountAndStatus(c);

        if (nested && this.category && this.category._addSelectedCount) {
            this.category._addSelectedCount(count, nested);
        }
    },
    // _getSelectedCount : function(){
    //     var list = this.subItems.filter( function (item) { return item.isSelected; });
    //     return list.length;
    // },
    _getTotalCount: function () {
        return this.data.subNestedIdentityCount;
    },
    _getSelectedCount: function () {
        debugger;
        if (typeOf(this.selectedCount) === "number") {
            return this.selectedCount;
        } else {
            return 0;
        }
    },
    _checkStatus: function () {

    },
    isExisted: function (d) {
        if (!d) return true;
        if (!this.createdItemObject) this.createdItemObject = {};
        var map = this.createdItemObject;
        if ((d.distinguishedName && map[d.distinguishedName]) ||
            (d.levelName && map[d.levelName]) ||
            (d.id && map[d.id]) ||
            (d.unique && map[d.unique])) {
            return true;
        } else {
            //if( typeOf( d ) === "string" ){
            //    this.createdItemObject[ d ] = true;
            //}else{
            //    if( d.distinguishedName )this.createdItemObject[ d.distinguishedName ] = true;
            //    if( d.id )this.createdItemObject[ d.id ] = true;
            //    if( d.unique )this.createdItemObject[ d.unique ] = true;
            //    if( d.employee )this.createdItemObject[ d.employee ] = true;
            //    if( d.levelName )this.createdItemObject[ d.levelName ] = true;
            //}
            this.createdItemObject[typeOf(d) === "string" ? d : (d.distinguishedName || d.id || d.unique || d.employee || d.levelName)] = true;
            return false;
        }
    },
    sortIdentityList : function(){
        if( this.selector.options.identitySortBy === "identityNumber" ){
            this.data.identityList.sort(function (a, b) {
                //this.selector.getUnitOrderNumber( a.unitLevelName )
                if( a.orderNumber === 0 )a.orderNumber = -1;
                if( b.orderNumber === 0 )b.orderNumber = -1;
                return (a.orderNumber || 9999999) - (b.orderNumber || 9999999);
            });
        }else{
            debugger;
            var getOrderNumber = function (d) {
                var orderNumber = "999";
                if( d.matchUnitDutyName ){
                    var idx = this.selector.options.dutys.indexOf( d.matchUnitDutyName );
                    if( idx > -1 )orderNumber = idx.toString();
                }
                if( typeOf(d.matchUnitDutyNumber) === "number" ){
                    orderNumber += d.matchUnitDutyNumber.toString();
                }else{
                    orderNumber += "9999";
                }
                return orderNumber.toInt();
            }.bind(this);

            var getOrder = function (d) {
                if( d.otherMatchUnitDutyList && d.otherMatchUnitDutyList.length ){
                    var array = [getOrderNumber(d)];
                    d.otherMatchUnitDutyList.each(function (duty) {
                        array.push( getOrderNumber(duty) );
                    });
                    return array.min();
                }else{
                   return getOrderNumber(d);
                }
            }.bind(this);

            this.data.identityList.sort(function (a, b) {
                return getOrder(a) - getOrder(b);
            });
        }
    },
    loadSub: function (callback) {
        this._loadSub(function (firstLoad) {
            if (firstLoad && (this.selector.options.showSelectedCount || this.selector.options.isCheckStatus)) {
                var count = 0;
                this.subCategorys.each(function (category) {
                    var l = category.subItems.filter(function (item) {
                        return item.isSelected;
                    });
                    count = count + l.length;
                });

                var list = this.subItems.filter(function (item) {
                    return item.isSelected;
                });
                this.selectedCount = count + list.length;

                this.checkCountAndStatus(this.selectedCount);
            }
            if (callback) callback();
        }.bind(this))
    },
    _loadSub: function (callback) {
        if (!this.loaded) {
            if (!this.itemLoaded) {
                if (this.data.identityList && this.data.identityList.length > 0) {

                    this.sortIdentityList();

                    this.data.identityList.each(function (identity) {
                        // if( !this.selector.isExcluded( identity ) ) {
                        //     if( !this.isExisted( identity ) ){
                        var item = this.selector._newItem(identity, this.selector, this.children, this.level + 1, this);
                        this.selector.items.push(item);
                        if (this.subItems) this.subItems.push(item);
                        // }
                        // }
                    }.bind(this))
                }
                this.itemLoaded = true;
            }

            if( !this.categoryLoaded ){
                if (this.data.unitList && this.data.unitList.length) {
                    this.data.unitList.sort(function (a, b) {
                        if( a.orderNumber === 0 )a.orderNumber = -1;
                        if( b.orderNumber === 0 )b.orderNumber = -1;
                        return (a.orderNumber || 9999999) - (b.orderNumber || 9999999);
                    });
                    this.data.unitList.each(function (subData) {
                        // if( !this.selector.isExcluded( subData ) ) {
                        var category = this.selector._newItemCategory("ItemCategory", subData, this.selector, this.children, this.level + 1, this);
                        this.subCategorys.push(category);
                        // category.loadSub()
                        // }
                    }.bind(this));
                }
                this.categoryLoaded = true;
            }

            this.loaded = true;

            if (callback) callback(true);
        } else {
            if (callback) callback();
        }
    },
    loadCategoryChildren: function (callback) {
        if (!this.categoryLoaded) {
            this.loadSub();

            this.categoryLoaded = true;
            this.itemLoaded = true;
            if (callback) callback();
        } else {
            if (callback) callback();
        }
    },
    loadItemChildren: function (callback) {
        if (!this.itemLoaded) {
            if (this.data.identityList && this.data.identityList.length > 0) {
                // this.data.identityList.sort(function (a, b) {
                //     if( a.orderNumber === 0 )a.orderNumber = -1;
                //     if( b.orderNumber === 0 )b.orderNumber = -1;
                //     return (a.orderNumber || 9999999) - (b.orderNumber || 9999999);
                // });

                this.sortIdentityList();

                this.data.identityList.each(function (identity) {
                    // if( !this.selector.isExcluded( identity ) ) {
                    //     if( !this.isExisted( identity ) ){
                    var item = this.selector._newItem(identity, this.selector, this.children, this.level + 1, this);
                    this.selector.items.push(item);
                    if (this.subItems) this.subItems.push(item);
                    // }
                    // }
                }.bind(this))
            }
            this.itemLoaded = true;
            if (callback) callback();
        } else {
            if (callback) callback();
        }
    },
    _hasChild: function () {
        return (this.data.unitList && this.data.unitList.length > 0) ||
            (this.data.identityList && this.data.identityList.length > 0);
    },
    _hasChildCategory: function () {
        return (this.data.unitList && this.data.unitList.length > 0);
    },
    _hasChildItem: function () {
        return this.data.identityList && this.data.identityList.length > 0;
    }

});

MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.ItemUnitCategory = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.ItemUnitCategory
});

MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.ItemGroupCategory = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.ItemGroupCategory
});

MWF.xApplication.Selector.IdentityWidthDutyCategoryByUnit.Filter = new Class({
    Extends: MWF.xApplication.Selector.IdentityWidthDuty.Filter
});
