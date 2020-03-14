MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.Identity = new Class({
	Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectIdentity,
        "units": [],
        "values": [],
        "dutys": [],
        "zIndex": 1000,
        "expand": false,
        "noUnit" : false,
        "include" : [], //增加的可选项
        "exclude" : [], //排除的可选项
        "resultType" : "", //可以设置成个人，那么结果返回个人
        "expandSubEnable" : true, //是否允许展开下一层,
        "selectAllEnable" : true //分类是否允许全选下一层
    },
    loadSelectItems: function(addToNext){
        if( this.options.resultType === "person" ){
            if( this.titleTextNode ){
                this.titleTextNode.set("text", MWF.xApplication.Selector.LP.selectPerson );
            }else{
                this.options.title = MWF.xApplication.Selector.LP.selectPerson;
            }
        }
        if(this.options.noUnit){
            this.loadInclude();
        }else if (this.options.units.length){
            // var units = [];
            // this.options.units.each(function(u){
            //     if (typeOf(u)==="string"){
            //         units.push(u);
            //     }else{
            //         units.push(u.distinguishedName)
            //     }
            // });
            // this.org2Action.listUnit({"unitList":units}, function(json){
            //     json.data.each(function(d){
            //         var category = this._newItemCategory("ItemUnitCategory", d, this, this.itemAreaNode);
            //         this.subCategorys.push( category );
            //     }.bind(this));
            // }.bind(this));
            this.options.units.each(function(unit){
                if (typeOf(unit)==="string"){


                    this.orgAction.getUnit(unit, function(json){
                        if (json.data){

                            var category = this._newItemCategory("ItemUnitCategory", json.data, this, this.itemAreaNode);
                            this.subCategorys.push( category );
                        }
                        this.loadInclude();
                    }.bind(this), function(){
                        this.orgAction.listUnitByKey(function(json){
                            if (json.data.length){
                                json.data.each(function(data){
                                    var category = this._newItemCategory("ItemUnitCategory", data, this, this.itemAreaNode);
                                    this.subCategorys.push( category );
                                }.bind(this))
                            }
                            this.loadInclude();
                        }.bind(this), null, unit);
                    }.bind(this));
                }else{
                    this.orgAction.getUnit(function(json){
                        if (json.data){
                            var category = this._newItemCategory("ItemUnitCategory", json.data, this, this.itemAreaNode);
                            this.subCategorys.push( category );
                        }
                        this.loadInclude();
                    }.bind(this), null, unit.distinguishedName);
                    //var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
                }

            }.bind(this));
        }else{
            this.orgAction.listTopUnit(function(json){
                json.data.each(function(data){
                    if( !this.isExcluded( data ) ){
                        var category = this._newItemCategory("ItemUnitCategory", data, this, this.itemAreaNode);
                        this.subCategorys.push( category );
                    }
                }.bind(this));
                this.loadInclude();
            }.bind(this));
        }
    },

    loadInclude: function() {
        if (!this.includeObject){
            this.includeObject = new MWF.xApplication.Selector.Identity.Include(this, this.itemAreaNode, {
                "include": this.options.include, //增加的可选项
                "resultType": this.options.resultType, //可以设置成个人，那么结果返回个人
                "expandSubEnable": this.options.expandSubEnable //是否允许展开下一层
            });
        }
        this.includeObject.load();
    },

    checkLoadSelectItems: function(){
        if (!this.options.units.length){
            this.loadSelectItems();
        }else{
            this.loadSelectItems();
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level, category, delay){
        return new MWF.xApplication.Selector.Identity[type](data, selector, item, level, category, delay)
    },

    _listItem : function( filterType, callback, failure, key ){
        if( this.options.noUnit ){
            this.includeObject.listByFilter( filterType,  key, function( array ){
                var json = {"data" : array} ;
                if (callback) callback.apply(this, [json]);
            }.bind(this))
        }else{
            var action = filterType === "key" ? "listIdentityByKey" : "listIdentityByPinyin";
            if ( this.options.units.length ){
                key = this.getLikeKey( key, this.options.units );
                this.orgAction[action](function(json){ //搜若units内的组织
                    this.includeObject.listByFilter( filterType, key, function( array ){
                        json.data = array.concat( json.data || [] );
                        if (callback) callback.apply(this, [json]);
                    }.bind(this));
                }.bind(this), failure, key);
            }else{  //搜索所有人
                this.orgAction[action](function(json){
                    if (callback) callback.apply(this, [json]);
                }.bind(this), failure, key);
            }
        }
    },

    _listItemByKey: function(callback, failure, key){
        this._listItem( "key", callback, failure, key );
    },

    _getItem: function(callback, failure, id, async){
        this.orgAction.getIdentity(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Identity.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItem( "pinyin", callback, failure, key );
    },
    _newItem: function(data, selector, container, level, category){
        return new MWF.xApplication.Selector.Identity.Item(data, selector, container, level, category);
    },
    _newItemSearch: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Identity.SearchItem(data, selector, container, level);
    },
    getLikeKey : function( key, unitObjects ){
        var unitObjects = unitObjects || [];
        var units = [];
        unitObjects.each(function(u){
            if (typeOf(u)==="string"){
                units.push(u);
            }
            if (typeOf(u)==="object"){
                units.push(u.distinguishedName);
            }
        });
        var keyObj = { "key": key, "unitList": units };

        if( !this.dutyDnList ){
            var dutyDnList = [];
            var dutyNameList = [];
            ( this.options.dutys || [] ).each(function(d){
                if (typeOf(d)==="string"){
                    var ds = d.split("@");
                    if( ds[ ds.length - 1].toUpperCase() === "UD" ){
                        dutyDnList.push( d )
                    }else{
                        dutyNameList.push( d );
                    }
                }
                if (typeOf(d)==="object"){
                    if( d.distinguishedName ){
                        dutyDnList.push(d.distinguishedName);
                    }else if( d.name ){
                        dutyNameList.push(d.name);
                    }
                }
            });
            if( dutyNameList.length ){
                o2.Actions.get("x_organization_assemble_express").listDutyWithName( { nameList : dutyNameList }, function(json){
                    if( json.data && json.data.unitDutyList ){
                        dutyDnList = dutyDnList.concat( json.data.unitDutyList )
                    }
                }.bind(this), null, false);
            }
            this.dutyDnList = dutyDnList;
        }
        if( this.dutyDnList.length )keyObj.unitDutyList = this.dutyDnList;

        return units.length ? keyObj : key;
    }
    //_listItemNext: function(last, count, callback){
    //    this.action.listRoleNext(last, count, function(json){
    //        if (callback) callback.apply(this, [json]);
    //    }.bind(this));
    //}
});


MWF.xApplication.Selector.Identity.Item = new Class({
	Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/personicon.png)");
    },
    getData: function(callback){
        if( this.selector.options.resultType === "person" ){
            var isPerson = false;
            if( this.data && this.data.distinguishedName ){
                var dn = this.data.distinguishedName;
                if( dn.substr(dn.length-1, 1).toLowerCase() === "p" )isPerson = true;
            }
            if( isPerson ) {
                if (callback) callback();
            }else if( this.data.woPerson ){
                this.data == this.data.woPerson;
                if (callback) callback();
            }else if( this.data.person ){
                this.selector.orgAction.getPerson(function(json){
                    this.data = json.data;
                    if (callback) callback();
                }.bind(this), null, this.data.person)
            }else{
                if (callback) callback();
            }
        }else{
            if (!this.data.woPerson){
                this.selector.orgAction.getPerson(function(json){
                    this.data.woPerson = json.data;
                    if (callback) callback();
                }.bind(this), null, this.data.person)
            }else{
                if (callback) callback();
            }
        }
    },
    checkSelectedSingle: function(){
        var isPerson = this.selector.options.resultType === "person";
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if( isPerson ){
                if (typeOf(item)==="object") return (item.id && item.id === this.data.person) ||
                    ( item.person && item.person === this.data.id ) ||
                    ( this.data.distinguishedName && this.data.distinguishedName === item.distinguishedName);
                return false;
            }else{
                if (typeOf(item)==="object") return this.data.distinguishedName === item.distinguishedName;
                if (typeOf(item)==="string") return this.data.distinguishedName === item;
                return false;
            }
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var isPerson = this.selector.options.resultType === "person";
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            if( isPerson ){
                return ( item.data.id && item.data.id === this.data.person ) ||
                    ( item.data.person && item.data.person === this.data.id ) ||
                    ( item.data.distinguishedName && item.data.distinguishedName === this.data.distinguishedName );
            }else{
                return item.data.distinguishedName === this.data.distinguishedName;
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
MWF.xApplication.Selector.Identity.SearchItem = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name+((this.data.unitLevelName) ? "("+this.data.unitLevelName+")" : "");
    }
});

MWF.xApplication.Selector.Identity.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Person.ItemSelected,
    getData: function(callback){
        if( this.selector.options.resultType === "person" ){
            var isPerson = false;
            if( this.data && this.data.distinguishedName ){
                var dn = this.data.distinguishedName;
                if( dn.substr(dn.length-1, 1).toLowerCase() === "p" )isPerson = true;
            }
            if( isPerson ){
                if (callback) callback();
            }else if( this.data.woPerson ){
                this.data = this.data.woPerson;
                if (callback) callback();
            }else if( this.data.person ){
                this.selector.orgAction.getPerson(function(json){
                    this.data = json.data;
                    if (callback) callback();
                }.bind(this), null, this.data.person )
            }else{
                if (callback) callback();
            }
        }else if (!this.data.woPerson){
            if (this.data.person){
                this.selector.orgAction.getPerson(function(json){
                    this.data.woPerson = json.data;
                    if (callback) callback();
                }.bind(this), function(xhr, text, error){
                    var errorText = error;
                    if (xhr){
                        var json = JSON.decode(xhr.responseText);
                        if (json){
                            errorText = json.message.trim() || "request json error";
                        }else{
                            errorText = "request json error: "+xhr.responseText;
                        }
                    }
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
                    if (callback) callback();
                }.bind(this), this.data.person)
            }else{
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, MWF.SelectorLP.noPerson.replace(/{name}/g, this.data.name));
                if (callback) callback();
            }
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
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/personicon.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var isPerson = this.selector.options.resultType === "person";
            var items = this.selector.items.filter(function(item, index){
                if( isPerson ){
                    return item.data.person === this.data.id ||
                        item.data.id === this.data.person ||
                        item.data.distinguishedName === this.data.distinguishedName;
                }else{
                    return item.data.distinguishedName === this.data.distinguishedName;
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

MWF.xApplication.Selector.Identity.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemCategory,
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department,
            "title" : this._getTtiteText()
        }).inject(this.container);
    },
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/companyicon.png)");
    },
    clickItem: function( callback ){
        if (this._hasChild()){
            var firstLoaded = !this.loaded;
            this.loadSub(function(){
                if( firstLoaded ){
                    if( !this.selector.isFlatCategory ){
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    }
                }else{
                    var display = this.children.getStyle("display");
                    if (display === "none"){
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    }else{
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                        this.isExpand = false;
                    }
                }
                if(callback)callback();
            }.bind(this));
        }
    },
    loadSub: function(callback){
        if (!this.loaded){
            if (this.selector.options.dutys && this.selector.options.dutys.length){
                var ids = [];
                var object = {};
                this.selector.options.dutys.each(function(duty){
                    this.selector.orgAction.listIdentityWidthUnitWithDutyName(this.data.distinguishedName, duty, function(json){
                        if (json.data && json.data.length){
                            ids = ids.concat(json.data);
                        }
                    }.bind(this), null, false);

                    ids.each(function(idSubData){
                        if( !this.selector.isExcluded( idSubData ) && !object[ idSubData.id || idSubData.distinguishedName ]) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this);
                            this.selector.items.push(item);
                            if(this.subItems)this.subItems.push( item );
                            object[ idSubData.id || idSubData.distinguishedName ] = true;
                        }
                        if( !this.selector.options.expandSubEnable ){
                            this.loaded = true;
                            if (callback) callback();
                        }
                    }.bind(this));
                }.bind(this));

                if( this.selector.options.expandSubEnable ){
                    this.selector.orgAction.listSubUnitDirect(function(json){
                        json.data.each(function(subData){
                            if( !this.selector.isExcluded( subData ) ) {
                                var category = this.selector._newItemCategory("ItemUnitCategory", subData, this.selector, this.children, this.level + 1, this);
                                this.subCategorys.push( category );
                            }
                        }.bind(this));
                        this.loaded = true;
                        if (callback) callback();
                    }.bind(this), null, this.data.distinguishedName);
                }
            }else{

                this.selector.orgAction.listIdentityWithUnit(function(idJson){
                    idJson.data.each(function(idSubData){
                        if( !this.selector.isExcluded( idSubData ) ) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this);
                            this.selector.items.push(item);
                            if(this.subItems)this.subItems.push( item );
                        }
                        if( !this.selector.options.expandSubEnable ){
                            this.loaded = true;
                            if (callback) callback();
                        }
                    }.bind(this));

                    if( this.selector.options.expandSubEnable ){
                        this.selector.orgAction.listSubUnitDirect(function(json){
                            json.data.each(function(subData){
                                if( !this.selector.isExcluded( subData ) ) {
                                    var category = this.selector._newItemCategory("ItemUnitCategory", subData, this.selector, this.children, this.level + 1, this);
                                    this.subCategorys.push( category );
                                }
                            }.bind(this));
                            this.loaded = true;
                            if (callback) callback();
                        }.bind(this), null, this.data.distinguishedName);
                    }
                }.bind(this), null, this.data.distinguishedName);
            }
        }else{
            if (callback) callback( );
        }
    },
    _hasChild: function(){
        var uCount = (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
        var iCount = (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
        return uCount + iCount;
    },
    _hasChildCategory: function(){
        return (this.data.subDirectUnitCount) ? this.data.subDirectUnitCount : 0;
    },
    _hasChildItem: function(){
        return (this.data.subDirectIdentityCount) ? this.data.subDirectIdentityCount : 0;
    },


    //for flat category start
    clickFlatCategoryItem: function( callback, hidden ){
        if (this._hasChildItem()){
            var firstLoaded = !this.itemLoaded;
            this.loadItemChildren(function(){
                if( hidden ){
                    //alert("hidden")
                    this.children.setStyles({"display": "none", "height": "0px"});
                    this.node.setStyles( this.selector.css.flatCategoryItemNode );
                    this.isExpand = false;
                }else if( firstLoaded ){
                    this.children.setStyles({"display": "block", "height": "auto"});
                    this.node.setStyles( this.selector.css.flatCategoryItemNode_selected );
                    this.isExpand = true;
                }else{
                    var display = this.children.getStyle("display");
                    if (display === "none"){
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.node.setStyles( this.selector.css.flatCategoryItemNode_selected );
                        this.isExpand = true;
                    }else{
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.node.setStyles( this.selector.css.flatCategoryItemNode );
                        this.isExpand = false;
                    }
                }
                if(callback)callback();
            }.bind(this));
        }
    },
    loadCategoryChildren: function(callback){
        if (!this.categoryLoaded){
            if( this.selector.options.expandSubEnable ){
                this.selector.orgAction.listSubUnitDirect(function(json){
                    json.data.each(function(subData){
                        if( !this.selector.isExcluded( subData ) ) {
                            var category = this.selector._newItemCategory("ItemUnitCategory", subData, this.selector, this.children, this.level + 1, this);
                            this.subCategorys.push( category );
                        }
                    }.bind(this));
                    this.categoryLoaded = true;
                    if (callback) callback();
                }.bind(this), null, this.data.distinguishedName);
            }else{
                if (callback) callback();
            }
        }else{
            if (callback) callback( );
        }
    },
    loadItemChildren: function(callback){
        if (!this.itemLoaded){
            if (this.selector.options.dutys && this.selector.options.dutys.length){
                var ids = [];
                var object = {};
                this.selector.options.dutys.each(function(duty){
                    this.selector.orgAction.listIdentityWidthUnitWithDutyName(this.data.distinguishedName, duty, function(json){
                        if (json.data && json.data.length){
                            ids = ids.concat(json.data);
                        }
                    }.bind(this), null, false);

                    ids.each(function(idSubData){
                        if( !this.selector.isExcluded( idSubData ) && !object[ idSubData.id || idSubData.distinguishedName ]) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this);
                            this.selector.items.push(item);
                            if(this.subItems)this.subItems.push( item );
                            object[ idSubData.id || idSubData.distinguishedName ] = true;
                        }
                        this.itemLoaded = true;
                    }.bind(this));
                    if (callback) callback();
                }.bind(this));
            }else{
                this.selector.orgAction.listIdentityWithUnit(function(idJson){
                    idJson.data.each(function(idSubData){
                        if( !this.selector.isExcluded( idSubData ) ) {
                            var item = this.selector._newItem(idSubData, this.selector, this.children, this.level + 1, this);
                            this.selector.items.push(item);
                            if(this.subItems)this.subItems.push( item );
                        }
                        this.itemLoaded = true;
                    }.bind(this));
                    if (callback) callback();
                }.bind(this), null, this.data.distinguishedName);
            }
        }else{
            if (callback) callback( );
        }
    }
    //for flat category end
});

MWF.xApplication.Selector.Identity.ItemUnitCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory
});

MWF.xApplication.Selector.Identity.ItemGroupCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory
        }).inject(this.container);
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/groupicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){

            var personLoadedCount = 0;
            var unitLoadedCount = 0;
            var groupLoadedCount = 0;

            var checkCallback = function( type ){
                if( !this.selector.options.expandSubEnable ){
                    if( type === "person" )personLoadedCount++;
                    if( !this.data.personList || this.data.personList.length === 0 || this.data.personList.length == personLoadedCount){
                        this.loaded = true;
                        if (callback) callback();
                    }
                }else{
                    if( type === "person" )personLoadedCount++;
                    if( type === "unit" )unitLoadedCount++;
                    if( type === "group" )groupLoadedCount++;
                    if( ( !this.data.personList || this.data.personList.length === 0 || this.data.personList.length == personLoadedCount ) &&
                        ( !this.data.unitList || this.data.unitList.length === 0 || this.data.unitList.length == unitLoadedCount ) &&
                        ( !this.data.groupList || this.data.groupList.length === 0 || this.data.groupList.length == groupLoadedCount ) ){
                        this.loaded = true;
                        if (callback) callback();
                    }
                }
            }.bind(this);

            checkCallback();

            ( this.data.personList || [] ).each( function(p){
                if( this.selector.options.resultType === "person" ){
                    this.selector.orgAction.getPerson(function (json) {
                        this.selector.includeObject.loadPersonItem(json, this.children, this.level + 1, this );
                        checkCallback("person");
                    }.bind(this), function(){ checkCallback("person") }, p );
                }else{
                    this.selector.orgAction.listIdentityByPerson(function(json){
                        this.selector.includeObject.loadIdentityItem(json, this.children, this.level + 1, this);
                        checkCallback("person")
                    }.bind(this), function(){ checkCallback("person") }, p );
                }
            }.bind(this));

            if( this.selector.options.expandSubEnable ){
                ( this.data.unitList || [] ).each( function(u){
                    this.selector.orgAction.getUnit(function (json) {
                        this.selector.includeObject.loadUnitItem(json, this.children, this.level + 1, this);
                        checkCallback("unit");
                    }.bind(this), function(){ checkCallback("unit") }, u );
                }.bind(this));

                ( this.data.groupList || [] ).each( function(g){
                    this.selector.orgAction.getGroup(function (json) {
                        this.selector.includeObject.loadGroupItem(json, this.children, this.level + 1, this);
                        checkCallback("group");
                    }.bind(this), function(){ checkCallback("group") }, g );
                }.bind(this));
            }
        }else{
            if (callback) callback( );
        }
    },
    loadCategoryChildren: function(callback){
        if (!this.categoryLoaded){

            var unitLoadedCount = 0;
            var groupLoadedCount = 0;

            var checkCallback = function( type ){
                if( !this.selector.options.expandSubEnable ){
                    this.categoryLoaded = true;
                }else{
                    if( type === "unit" )unitLoadedCount++;
                    if( type === "group" )groupLoadedCount++;
                    if( ( !this.data.unitList || this.data.unitList.length === 0 || this.data.unitList.length == unitLoadedCount ) &&
                        ( !this.data.groupList || this.data.groupList.length === 0 || this.data.groupList.length == groupLoadedCount ) ){
                        this.categoryLoaded = true;
                        if (callback) callback();
                    }
                }
            }.bind(this);

            checkCallback();

            if( this.selector.options.expandSubEnable ){
                ( this.data.unitList || [] ).each( function(u){
                    this.selector.orgAction.getUnit(function (json) {
                        this.selector.includeObject.loadUnitItem(json, this.children, this.level + 1, this);
                        checkCallback("unit");
                    }.bind(this), function(){ checkCallback("unit") }, u );
                }.bind(this));

                ( this.data.groupList || [] ).each( function(g){
                    this.selector.orgAction.getGroup(function (json) {
                        this.selector.includeObject.loadGroupItem(json, this.children, this.level + 1, this);
                        checkCallback("group");
                    }.bind(this), function(){ checkCallback("group") }, g );
                }.bind(this));
            }
        }else{
            if (callback) callback( );
        }
    },
    loadItemChildren: function(callback){
        if (!this.itemLoaded){
            var personLoadedCount = 0;

            var checkCallback = function( type ){
                if( type === "person" )personLoadedCount++;
                if( !this.data.personList || this.data.personList.length === 0 || this.data.personList.length == personLoadedCount){
                    this.itemLoaded = true;
                    if (callback) callback();
                }
            }.bind(this);

            checkCallback();

            ( this.data.personList || [] ).each( function(p){
                if( this.selector.options.resultType === "person" ){
                    this.selector.orgAction.getPerson(function (json) {
                        this.selector.includeObject.loadPersonItem(json, this.children, this.level + 1, this );
                        checkCallback("person");
                    }.bind(this), function(){ checkCallback("person") }, p );
                }else{
                    this.selector.orgAction.listIdentityByPerson(function(json){
                        this.selector.includeObject.loadIdentityItem(json, this.children, this.level + 1, this);
                        checkCallback("person")
                    }.bind(this), function(){ checkCallback("person") }, p );
                }
            }.bind(this));
        }else{
            if (callback) callback( );
        }
    },
    _hasChild: function(){
        var uCount = (this.data.unitList) ? this.data.unitList.length : 0;
        var gCount = (this.data.groupList) ? this.data.groupList.length : 0;
        var pCount = (this.data.personList) ? this.data.personList.length : 0;
        return uCount + gCount + pCount;
    },
    _hasChildCategory: function(){
        var uCount = (this.data.unitList) ? this.data.unitList.length : 0;
        var gCount = (this.data.groupList) ? this.data.groupList.length : 0;
        return uCount + gCount;
    },
    _hasChildItem: function(){
        return (this.data.personList) ? this.data.personList.length : 0;
    }
});

MWF.xApplication.Selector.Identity.ItemRoleCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = this.selector.options.style;
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/roleicon.png)");
    }
});

MWF.xApplication.Selector.Identity.Include = new Class({
    Implements: [Options, Events],
    options: {
        "include" : [], //增加的可选项
        "resultType" : "", //可以设置成个人，那么结果返回个人
        "expandSubEnable" : true //是否允许展开下一层
    },
    initialize: function(selector, itemAreaNode, options){
        this.setOptions(options);
        this.selector = selector;
        this.itemAreaNode = $(itemAreaNode);
        this.orgAction = MWF.Actions.get("x_organization_assemble_control");
    },
    load : function(){
        if( !this.options.include || this.options.include.length === 0 )return;

        this.includeAreaNode = new Element( "div.includeAreaNode").inject( this.itemAreaNode, "top" );

        if( this.selector.isFlatCategory ){
            this.flatCategoryAreaNode = new Element( "div.includeFlatCategoryAreaNode").inject( this.selector.flatCategoryNode, "top" );
        }

        //this.includeIdentityAreaNode = new Element( "div").inject( this.includeAreaNode );
        //this.includePersonAreaNode = new Element( "div").inject( this.includeAreaNode );
        //this.includeUnitAreaNode = new Element( "div").inject( this.includeAreaNode );
        //this.includeGroupAreaNode = new Element( "div").inject( this.includeAreaNode );

        this.options.include.each( function( d ){
            var container = new Element("div").inject( this.includeAreaNode );

            var flatCategoryContainer;
            if( this.flatCategoryAreaNode ){
                flatCategoryContainer = new Element("div").inject( this.flatCategoryAreaNode );
            }

            if (typeOf(d)==="string"){
                var arr = d.split("@");
                var flag = arr[ arr.length - 1].toLowerCase();
                if( flag === "u" ){
                    this.orgAction.listUnitByKey(function(json){
                        this.loadUnitItem(json, container, null, null, flatCategoryContainer);
                    }.bind(this), null, d);
                }else if( flag === "i" ) {
                    this.orgAction.listIdentityByKey(function (json) {
                        this.loadIdentityItem(json, container, null, null, true )
                    }.bind(this), null, d);
                }else if( flag === "g" ){
                    this.orgAction.listGroupByKey(function(json){
                        this.loadGroupItem( json , container, null, null, flatCategoryContainer)
                    }.bind(this), null, d);
                }else if( flag === "p" ){
                    if( this.options.resultType === "person" ){
                        this.orgAction.getPerson(function (json){
                            this.loadPersonItem( json , container, null, null, true)
                        }.bind(this), null, d);
                    }else{
                        this.orgAction.listIdentityByPerson(function(json){
                            this.loadIdentityItem(json, container , null, null, true)
                        }.bind(this), null, d);
                    }
                }else{
                    if( this.options.resultType === "person" ){
                        this.orgAction.listPersonByKey(function (json) {
                            this.loadPersonItem( json , container, null, null, true)
                        }.bind(this), null, d);
                    }else{
                        this.orgAction.listIdentityByKey(function(json){
                            this.loadIdentityItem(json, container, null, null, true)
                        }.bind(this), null, d);
                    }
                }
            }else{
                var arr = d.distinguishedName.split("@");
                var flag = arr[ arr.length - 1].toLowerCase();
                if( flag === "u" ) {
                    this.orgAction.getUnit(function (json) {
                        this.loadUnitItem(json, container, null, null, flatCategoryContainer);
                    }.bind(this), null, d.distinguishedName);
                }else if( flag === "i" ){
                    this.orgAction.getIdentity(function (json) {
                        this.loadIdentityItem(json, container, null, null, true)
                    }.bind(this), null, d.distinguishedName);
                }else if( flag === "g" ){
                    this.orgAction.getGroup(function(json){
                        this.loadGroupItem( json , container, null, null, flatCategoryContainer)
                    }.bind(this), null, d.distinguishedName, null, null, true);
                }else if( flag === "p" ){
                    if( this.options.resultType === "person" ){
                        this.orgAction.getPerson(function (json) {
                            this.loadPersonItem(json, container, null, null, true)
                        }.bind(this), null, d.distinguishedName);
                    }else{
                        this.orgAction.listIdentityByPerson(function(json){
                            this.loadIdentityItem(json, container, null, null, true)
                        }.bind(this), null, d.distinguishedName);
                    }
                }else{
                    if( this.options.resultType === "person" ){
                        this.orgAction.getPerson(function (json) {
                            this.loadPersonItem(json, container, null, null, true)
                        }.bind(this), null, d.distinguishedName);
                    }else{
                        this.orgAction.getIdentity(function (json) {
                            this.loadIdentityItem(json, container, null, null, true)
                        }.bind(this), null, d.distinguishedName);
                    }
                }
                //var category = this._newItemCategory("ItemCategory", unit, this, this.itemAreaNode);
            }
        }.bind(this))
    },
    loadPersonItem : function( json, container, level, category, isIncludedPerson ){
        if( !json.data )return;
        var array = typeOf( json.data ) === "array" ? json.data : [json.data];
        array.each(function(data){
            if( !this.selector.isExcluded( data ) ) {

                if(!this.includePerson)this.includePerson = [];
                if(isIncludedPerson)this.includePerson.push( data.distinguishedName );

                if(!this.includePersonObject)this.includePersonObject = [];
                if(isIncludedPerson)this.includePersonObject.push( data );

                var item = this.selector._newItem(data, this.selector, container || this.includeAreaNode, level || 1, category);
                this.selector.items.push(item);
                if( category && category.subItems ){
                    category.subItems.push( item );
                }
            }
        }.bind(this));
    },
    loadIdentityItem : function( json, container, level, category, isIncludedIdentity ){
        if( !json.data )return;
        var array = typeOf( json.data ) === "array" ? json.data : [json.data];
        array.each(function(data){
            if( !this.selector.isExcluded( data ) ) {

                if(!this.includeIdentity)this.includeIdentity = [];
                if(isIncludedIdentity)this.includeIdentity.push( data.distinguishedName );

                if(!this.includeIdentityObject)this.includeIdentityObject = [];
                if(isIncludedIdentity)this.includeIdentityObject.push( data );

                var item = this.selector._newItem(data, this.selector, container || this.includeAreaNode, level || 1, category);
                this.selector.items.push(item);
                if( category && category.subItems ){
                    category.subItems.push( item );
                }
            }
        }.bind(this));
    },
    loadUnitItem : function( json, container, level, parentCategory, flatCategoryContainer ){
        if( !json.data )return;
        var array = typeOf( json.data ) === "array" ? json.data : [json.data];
        array.each(function(data){
            if( !this.selector.isExcluded( data ) ) {
                if(!this.includeUnit)this.includeUnit = [];
                this.includeUnit.push( data.distinguishedName );
                var category;
                if( flatCategoryContainer ){
                    category = this.selector._newItemCategory("ItemUnitCategory", data, this.selector,
                        container || this.includeAreaNode, level, parentCategory, true);
                    category.nodeContainer = flatCategoryContainer;
                    category.load();
                }else{
                    category = this.selector._newItemCategory("ItemUnitCategory", data, this.selector,
                        container || this.includeAreaNode, level, parentCategory);
                }
                if( parentCategory && parentCategory.subCategorys ){
                    parentCategory.subCategorys.push( category )
                }else if(this.selector.subCategorys){
                    this.selector.subCategorys.push( category )
                }
            }
        }.bind(this));
    },
    loadGroupItem : function( json, container, level, parentCategory, flatCategoryContainer ){
        if( !json.data )return;
        var array = typeOf( json.data ) === "array" ? json.data : [json.data];
        array.each(function(data){
            if( !this.selector.isExcluded( data ) ) {
                if(!this.includeGroup)this.includeGroup = [];
                this.includeGroup.push( data.distinguishedName );
                var category;
                if( flatCategoryContainer ){
                    category = this.selector._newItemCategory("ItemGroupCategory", data, this.selector, container || this.includeAreaNode, level, parentCategory, true);
                    category.nodeContainer = flatCategoryContainer;
                    category.load();
                }else{
                    category = this.selector._newItemCategory("ItemGroupCategory", data, this.selector, container || this.includeAreaNode, level, parentCategory)
                }
                if( parentCategory && parentCategory.subCategorys ){
                    parentCategory.subCategorys.push( category )
                }else if(this.selector.subCategorys){
                    this.selector.subCategorys.push( category )
                }
            }
        }.bind(this));
    },

    listByFilter : function( type, key, callback ){
        var arr1 = this.listByFilterPerson(key) || [];
        this.listByFilterUnit( type, key, function(arr2){
            this.listByFilterGroup( type, key, function(arr3){
                if (callback) callback( arr1.concat( arr2 || [] ).concat( arr3 || [] ) );
            }.bind(this))
        }.bind(this))
    },
    listByFilterPerson : function( key ){
        var identitys = [];
        var persons = [];
        var keyString = typeOf( key )==="string" ? key.toLowerCase() : key.key.toLowerCase();
        if( this.includeIdentityObject && this.includeIdentityObject.length ){
            identitys = this.includeIdentityObject.filter( function(id) {
                return ( id.pinyin || "" ).indexOf(keyString) > -1 || (id.pinyinInitial || "").indexOf(keyString) > -1 || (id.distinguishedName || "").indexOf(keyString) > -1;
            })
        }
        if( this.includePersonObject && this.includePersonObject.length ){
            persons = this.includePersonObject.filter( function(id) {
                return ( id.pinyin || "" ).indexOf(keyString) > -1 || (id.pinyinInitial || "").indexOf(keyString) > -1 || (id.distinguishedName || "").indexOf(keyString) > -1;
            })
        }
        return identitys.concat( persons);
    },
    listByFilterGroup : function( type, key, callback ){
        var keyString = typeOf( key )==="string" ? key.toLowerCase() : key.key.toLowerCase();
        if( this.includeGroup && this.includeGroup.length ){
            var keyObject = { "key" : keyString, "groupList" : this.includeGroup };
            this.orgAction[ type === "pinyin" ? "listPersonByPinyin" : "listPersonByKey" ](function(json){
                if( this.options.resultType === "person" ){
                    if (callback) callback( json.data );
                }else{
                    var personList = (json.data || []).map( function(d){
                        return d.id
                    });
                    o2.Actions.get("x_organization_assemble_express").listIdentityWithPerson( { "personList" : personList }, function(js){
                        if (callback) callback(js.data);
                    }.bind(this), function(){
                        if (callback) callback();
                    }.bind(this))
                }
            }.bind(this), function(){
                if (callback) callback();
            }.bind(this), keyObject);
        }else{
            if (callback) callback();
        }
    },
    listByFilterUnit : function( type, key, callback ){
        var keyString = typeOf( key )==="string" ? key.toLowerCase() : key.key.toLowerCase();

        if ( this.includeUnit && this.includeUnit.length ){
            key = this.getUnitFilterKey( key, this.includeUnit );

            this.orgAction.listIdentityByKey(function(json){
                if (callback) callback(json.data);
            }.bind(this), function(){
                if (callback) callback();
            }, key);
        }else{
            if (callback) callback();
        }
    },
    getUnitFilterKey : function( key, unitObjects ){
        var unitObjects = unitObjects || [];
        var units = [];
        unitObjects.each(function(u){
            if (typeOf(u)==="string"){
                units.push(u);
            }
            if (typeOf(u)==="object"){
                units.push(u.distinguishedName);
            }
        });
        return units.length ? {"key": key, "unitList": units} : key;
    }
});

MWF.xApplication.Selector.Identity.Filter = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "units": [],
        "resultType" : "" //可以设置成个人，那么结果返回个人
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
            key = {"key": this.value, "unitList": units};
        }
        var data = null;
        this.orgAction.listIdentityByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});
