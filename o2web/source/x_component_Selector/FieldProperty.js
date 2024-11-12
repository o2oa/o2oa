MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
MWF.xApplication.Selector.FieldProperty = new Class({
    Extends: MWF.xApplication.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "values": [],
        "names": [],
        "appType" : ["currentForm","process","portal","cms"],
        "excludeModuleTypes": ["Div","Table$Td","Datatable$Data","Datatable$Title","Label"],
        "currentFormFields": [],
        "applications": [],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectFieldProperty});
    },
    _init : function(){
        this.selectType = "fieldProperty";
        this.className = "FieldProperty";
    },
    loadSelectItems: function(addToNext){
        var json = {};
        this.options.appType.each( function (type) {
            if( type === 'currentForm' && !this.options.currentFormFields.length )return;

            var container = new Element("div").inject(this.itemAreaNode);

            this._newItemCategory({
                name: MWF.xApplication.Selector.LP.appType[type],
                id: type,
                _appType: type
            }, this, container);


        }.bind(this));

    },
    _scrollEvent: function(y){
        return true;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.FieldProperty.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        // this.queryAction.getTable(function(json){
        //     if (callback) callback.apply(this, [json]);
        // }.bind(this), failure, ((typeOf(id)==="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.FieldProperty.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.FieldProperty.Item(data, selector, container, level);
    }
});

MWF.xApplication.Selector.FieldProperty.Item = new Class({
    Extends: MWF.xApplication.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name+"<"+this.data.type+">";
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
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
            if( item.data.id && this.data.id){
                return item.data.id === this.data.id;
            }
        }.bind(this));
        if (selectedItem.length){
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.FieldProperty.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name+"<"+this.data.type+">";
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                if( item.data.id && this.data.id){
                    return item.data.id === this.data.id;
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

MWF.xApplication.Selector.FieldProperty.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemCategory,
    _setIcon: function(){
        switch (this.level){
            case 1:
                this.iconNode.setStyle("background-image", "url(/x_component_Selector/$Selector/default/icon/processicon.png)");
                break;
            case 2:
                this.iconNode.setStyle("background-image", "url(/x_component_Selector/$Selector/default/icon/applicationicon.png)");
                break;
            case 3:
                this.iconNode.setStyle("background-image", "url(/x_component_Selector/$Selector/default/icon/table.png)");
                break;
        }
    },
    clickItem: function (callback) {
        if (this._hasChild() ) {
            var firstLoaded = !this.loaded;
            this.loadSub(function () {
                if (firstLoaded && this._hasChild() ) {
                    if (!this.selector.isFlatCategory) {
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    }
                    // this.checkSelectAll();
                } else {
                    var display = this.children.getStyle("display");
                    if (display === "none") {
                        // this.selector.fireEvent("expand", [this] );
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                        this.isExpand = true;
                    } else {
                        // this.selector.fireEvent("collapse", [this] );
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                        this.isExpand = false;
                    }
                }
                if (callback) callback();
            }.bind(this));
        }
    },
    loadSub: function (callback) {
        var action;
        if (!this.loaded) {
            if( this.level === 1 ){
                if( this.data._appType === "currentForm" ){
                    this.selector.options.currentFormFields.filter(function (d){
                        return !this.selector.options.excludeModuleTypes.contains(d.type);
                    }.bind(this)).sort(function (a, b){
                        return a.id.localeCompare(b.id);
                    }).each(function (d) {
                        d._appType = this.data._appType;
                        d.name = d.id;
                        var item = this.selector._newItem(d, this.selector, this.children, this.level + 1, this);
                        this.selector.items.push(item);
                        if(this.subItems)this.subItems.push( item );
                    }.bind(this));
                    this.loaded = true;
                    if (callback) callback();
                }else{
                    switch (this.data._appType) {
                        case "process":
                            action = o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.list;
                            break;
                        case "portal":
                            action = o2.Actions.load("x_portal_assemble_designer").PortalAction.list;
                            break;
                        case  "cms":
                            action = o2.Actions.load("x_cms_assemble_control").AppInfoAction.listAllAppInfo;
                            break;
                    }
                    action(function (json){
                        json.data.sort(function (a, b){
                            a.name = a.name || a.appName;
                            b.name = b.name || b.appName;
                            return a.name.localeCompare(b.name);
                        }).each(function (d){
                            d._appType = this.data._appType;
                            var category = this.selector._newItemCategory(d, this.selector, this.children, this.level + 1, this);
                            this.subCategorys.push( category );
                        }.bind(this));

                        this.loaded = true;
                        if (callback) callback();

                    }.bind(this));
                }
            }else if(this.level === 2){
                switch (this.data._appType) {
                    case "process":
                        action = o2.Actions.load("x_processplatform_assemble_designer").FormAction.listWithApplication(this.data.id);
                        break;
                    case "portal":
                        action = o2.Actions.load("x_portal_assemble_designer").PageAction.listWithPortal(this.data.id);
                        break;
                    case  "cms":
                        action = o2.Actions.load("x_cms_assemble_control").FormAction.listFormByAppId(this.data.id);
                        break;
                }
                action.then(function (json){
                    json.data.sort(function (a, b){{
                        return a.name.localeCompare(b.name);
                    }}).each(function (d){
                        d._appType = this.data._appType;
                        var category = this.selector._newItemCategory(d, this.selector, this.children, this.level + 1, this);
                        this.subCategorys.push( category );
                    }.bind(this));

                    this.loaded = true;
                    if (callback) callback();

                }.bind(this));
            }else if(this.level === 3){
                switch (this.data._appType) {
                    case "process":
                        action = o2.Actions.load("x_processplatform_assemble_designer").FormAction.get(this.data.id);
                        break;
                    case "portal":
                        action = o2.Actions.load("x_portal_assemble_designer").PageAction.get(this.data.id);
                        break;
                    case  "cms":
                        action = o2.Actions.load("x_cms_assemble_control").FormAction.get(this.data.id);
                        break;
                }
                action.then(function (json){
                    var formData = JSON.decode(MWF.decodeJsonString(json.data.data));
                    Object.values(formData.json.moduleList).filter(function (d){
                        return !this.selector.options.excludeModuleTypes.contains(d.type);
                    }.bind(this)).sort(function (a, b){
                        return a.id.localeCompare(b.id);
                    }).each(function (d) {
                        d._appType = this.data._appType;
                        d.name = d.id;
                        var item = this.selector._newItem(d, this.selector, this.children, this.level + 1, this);
                        this.selector.items.push(item);
                        if(this.subItems)this.subItems.push( item );
                    }.bind(this));

                    this.loaded = true;
                    if (callback) callback();

                }.bind(this));

            }
        } else {
            if (callback) callback();
        }
    },
    _getShowName: function(){
        return this.data.name;
    },
    _getTtiteText: function () {
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _hasChild: function(){
        return true;
        // return ( this.data.scriptList && this.data.scriptList.length ) ||
        //     ( this.data.applicationList && this.data.applicationList.length);
    },
    afterLoad: function(){
        if ( this._hasChild() ){
            if( this.level === 1 && this.data._appType === 'currentForm' ){
                this.clickItem();
            }

        }
    },
    check: function(){}
});
