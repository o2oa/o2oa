MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Identity", null, false);
MWF.xApplication.Organization.Selector.Department = new Class({
	Extends: MWF.xApplication.Organization.Selector.Identity,
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

        this.path = "/x_component_Organization/Selector/$Selector/";
        this.cssPath = "/x_component_Organization/Selector/$Selector/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.container = $(container);
        this.action = new MWF.xApplication.Organization.Actions.RestActions();

        this.lastPeople = "";
        this.pageCount = "13";
        this.selectedItems = [];
        this.items = [];
    },

    loadSelectItems: function(addToNext){
        if (this.options.companys.length){
            this.options.companys.each(function(comp){
                this.action.listCompanyByKey(function(json){
                    if (json.data.length){
                        json.data.each(function(data){
                            var category = this._newItemCategory("ItemCompanyCategory", data, this, this.itemAreaNode);
                        }.bind(this));
                    }
                }.bind(this), null, comp);
            }.bind(this));
        }else{
            this.action.listTopCompany(function(json){
                json.data.each(function(data){
                    var category = this._newItemCategory("ItemCompanyCategory", data, this, this.itemAreaNode);
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
    _newItemCategory: function(type, data, selector, item, level){
        return new MWF.xApplication.Organization.Selector.Department[type](data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        this.action.listDepartmentByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getDepartment(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.Department.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listDepartmentByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.Department.Item(data, selector, container, level);
    }
});
MWF.xApplication.Organization.Selector.Department.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/departmenticon.png)");
    },
    loadSubItem: function(){
        this.children = new Element("div", {
            "styles": this.selector.css.selectorItemCategoryChildrenNode,
        }).inject(this.node, "after");
        this.children.setStyle("display", "block");
    //    if (!this.selector.options.expand) this.children.setStyle("display", "none");

        this.selector.action.listSubDepartment(function(subJson){
            subJson.data.each(function(subData){
                var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            }.bind(this));
        }.bind(this), null, this.data.id);
    },
});

MWF.xApplication.Organization.Selector.Department.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/departmenticon.png)");
    }
});

MWF.xApplication.Organization.Selector.Department.ItemCompanyCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,

    loadSub: function(callback){
        if (!this.loaded){

            this.selector.action.listSubComplexDirect(function(subJson){
                subJson.data.companyList.each(function(subData){
                    var category = this.selector._newItemCategory("ItemCompanyCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));
                subJson.data.departmentList.each(function(subData){
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback();

            }.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
});

MWF.xApplication.Organization.Selector.Department.ItemDepartmentCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    loadSub: function(callback){
        if (!this.loaded){
            this.selector.action.listSubDepartment(function(subJson){
                subJson.data.each(function(subData){
                //    var category = this.selector._newItemCategory("ItemDepartmentCategory", subData, this.selector, this.children, this.level+1);
                    var item = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                }.bind(this));
            }.bind(this), null, this.data.id);
            //this.selector.action.listIdentity(function(subJson){
            //    subJson.data.each(function(subData){
            //        var item = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            //        this.selector.items.push(item);
            //    }.bind(this));
            //}.bind(this), null, this.data.id);

            this.loaded = true;
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){

        var cCount = (this.data.companySubDirectCount) ? this.data.companySubDirectCount : 0;
        var dCount = (this.data.departmentSubDirectCount) ? this.data.departmentSubDirectCount : 0;
  //      var iCount = (this.data.identitySubDirectCount) ? this.data.identitySubDirectCount : 0;
  //      var count = cCount + dCount + iCount;
        var count = cCount + dCount
        if (count) return true;
        return false;
    },
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/departmenticon.png)");
    }
});
