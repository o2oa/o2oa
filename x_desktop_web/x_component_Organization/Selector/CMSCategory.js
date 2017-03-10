MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Department", null, false);
MWF.xApplication.Organization.Selector.CMSCategory = new Class({
	Extends: MWF.xApplication.Organization.Selector.Department,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Category",
        "values": [],
        "names": [],
        "expand": false
    },

    loadSelectItems: function(addToNext){
        this.action.listCMSApplication(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    data.name = data.appName;
                    var category = this._newItemCategory(data, this, this.itemAreaNode);
                }.bind(this));
            }
        }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Organization.Selector.CMSCategory.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        //this.action.getDepartment(function(json){
        //    if (callback) callback.apply(this, [json]);
        //}.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.CMSCategory.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.CMSCategory.Item(data, selector, container, level);
    }
});
MWF.xApplication.Organization.Selector.CMSCategory.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/processicon.png)");
    },
    loadSubItem: function(){
        return false;
    //    this.children = new Element("div", {
    //        "styles": this.selector.css.selectorItemCategoryChildrenNode
    //    }).inject(this.node, "after");
    //    this.children.setStyle("display", "block");
    ////    if (!this.selector.options.expand) this.children.setStyle("display", "none");
    //
    //    this.selector.action.listProcess(function(subJson){
    //        subJson.data.each(function(subData){
    //            var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
    //        }.bind(this));
    //    }.bind(this), null, this.data.id);
    }
});

MWF.xApplication.Organization.Selector.CMSCategory.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/processicon.png)");
    }
});

MWF.xApplication.Organization.Selector.CMSCategory.ItemCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,

    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){

            this.selector.action.listCMSCategory(function(subJson){
                subJson.data.each(function(subData){
                    subData.name = subData.categoryName;
                    subData.applicationName = this.data.name;
                    subData.application = this.data.id;
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback();
            }.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return (this.data.wrapOutCategoryList && this.data.wrapOutCategoryList.length);
    }
});
