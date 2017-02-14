MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Selector", "Department", null, false);
MWF.xApplication.Selector.Company = new Class({
	Extends: MWF.xApplication.Selector.Department,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Department",
        "companys": [],
        "departments": [],
        "values": [],
        "names": [],
        "expand": false
    },
    initialize: function(container, options){
        this.setOptions(options);
        this.options.groups = [];
        this.options.roles = [];

        this.path = "/x_component_Selector/$Selector/";
        this.cssPath = "/x_component_Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.action = new MWF.xApplication.Selector.Actions.RestActions();

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.items = [];
    },

    loadSelectItems: function(addToNext){
        this.action.listTopCompany(function(json){
            json.data.each(function(data){
                var category = this._newItem(data, this, this.itemAreaNode);
            }.bind(this));
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level){
        return new MWF.xApplication.Selector.Department[type](data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        this.action.listIdentityByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getCompany(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.Company.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listIdentityByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.Company.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.Company.Item = new Class({
	Extends: MWF.xApplication.Selector.Department.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/companyicon.png)");
    },
    loadSubItem: function(){
        this.children = new Element("div", {
            "styles": this.selector.css.selectorItemCategoryChildrenNode,
        }).inject(this.node, "after");
        this.children.setStyle("display", "block");
    //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

        this.selector.action.listSubCompany(function(subJson){
            subJson.data.each(function(subData){
                var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            }.bind(this));
        }.bind(this), null, this.data.name);
    },
});

MWF.xApplication.Selector.Company.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Department.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/default/icon/companyicon.png)");
    }
});
