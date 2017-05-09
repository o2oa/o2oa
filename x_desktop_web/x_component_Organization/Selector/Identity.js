MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Person", null, false);
MWF.xApplication.Organization.Selector.Identity = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Identity",
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

    //loadCompanylevel: function(parent){
    //
    //    this.action.listSubComplexDirect(function(subJson){
    //        subJson.data.companyList.each(function(subData){
    //            var category = this._newItemCategory("ItemCompanyCategory", subData, this, parent.children, parent.level+1);
    //            this.loadCompanylevel(category);
    //        }.bind(this));
    //        subJson.data.departmentList.each(function(subData){
    //            var category = this._newItemCategory("ItemDepartmentCategory", subData, this, parent.children, parent.level+1);
    //            this.loadDepartmentlevel(category);
    //        }.bind(this));
    //    }.bind(this), null, parent.data.id);
    //},
    //loadDepartmentlevel: function(parent){
    //    this.action.listSubDepartment(function(subJson){
    //        subJson.data.each(function(subData){
    //            var category = this._newItemCategory("ItemDepartmentCategory", subData, this, parent.children, parent.level+1);
    //            this.loadDepartmentlevel(category);
    //        }.bind(this));
    //    }.bind(this), null, parent.data.id);
    //},

    loadSelectItems: function(addToNext){
        if (this.options.companys.length || this.options.departments.length){
            this.options.companys.each(function(comp){
                this.action.listCompanyByKey(function(json){
                    if (json.data.length){
                        json.data.each(function(data){
                            var category = this._newItemCategory("ItemCompanyCategory", data, this, this.itemAreaNode);
                        }.bind(this));
                    }
                }.bind(this), null, comp);
            }.bind(this));

            this.options.departments.each(function(depart){
                this.action.listDepartmentByKey(function(json){
                    if (json.data.length){
                        json.data.each(function(data){
                            var category = this._newItemCategory("ItemDepartmentCategory", data, this, this.itemAreaNode);
                        }.bind(this));
                    }
                }.bind(this), null, depart);
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
        return new MWF.xApplication.Organization.Selector.Identity[type](data, selector, item, level)
    },


    _listItemByKey: function(callback, failure, key){
        this.action.listIdentityByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getItem: function(callback, failure, id, async){
        this.action.getIdentity(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, id, async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.Identity.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listIdentityByPinyin(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.Identity.Item(data, selector, container, level);
    }
    //_listItemNext: function(last, count, callback){
    //    this.action.listRoleNext(last, count, function(json){
    //        if (callback) callback.apply(this, [json]);
    //    }.bind(this));
    //}
});
MWF.xApplication.Organization.Selector.Identity.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/personicon.png)");
    }
});

MWF.xApplication.Organization.Selector.Identity.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/personicon.png)");
    }
});

MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Person.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/companyicon.png)");
    },
    clickItem: function(){
        if (this._hasChild()){
            var firstLoaded = !this.loaded;
            this.loadSub(function(){
                if( firstLoaded ){
                    this.children.setStyles({"display": "block", "height": "auto"});
                    this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);
                }else{
                    var display = this.children.getStyle("display");
                    if (display == "none"){
                        this.children.setStyles({"display": "block", "height": "auto"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_expand);

                    }else{
                        this.children.setStyles({"display": "none", "height": "0px"});
                        this.actionNode.setStyles(this.selector.css.selectorItemCategoryActionNode_collapse);
                    }
                }
            }.bind(this));
        }
    },
    loadSub: function(callback){
        if (!this.loaded){

            this.selector.action.listSubComplexDirect(function(subJson){
                subJson.data.companyList.each(function(subData){
                    var category = this.selector._newItemCategory("ItemCompanyCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));
                subJson.data.departmentList.each(function(subData){
                    var category = this.selector._newItemCategory("ItemDepartmentCategory", subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback( );

            }.bind(this), null, this.data.id);
        }else{
            if (callback) callback( );
        }
    },

    _hasChild: function(){
        var cCount = (this.data.companySubDirectCount) ? this.data.companySubDirectCount : 0;
        var dCount = (this.data.departmentSubDirectCount) ? this.data.departmentSubDirectCount : 0;
        var count = cCount + dCount;
        if (count) return true;
        return false;
    }

});

MWF.xApplication.Organization.Selector.Identity.ItemDepartmentCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    loadSub: function(callback){
        if (!this.loaded){
            if (this.data.departmentSubDirectCount){
                this.selector.action.listSubDepartment(function(subJson){
                    subJson.data.each(function(subData){
                        var category = this.selector._newItemCategory("ItemDepartmentCategory", subData, this.selector, this.children, this.level+1);
                    }.bind(this));
                }.bind(this), null, this.data.id);
            }
            if (this.data.identitySubDirectCount){
                this.selector.action.listIdentity(function(subJson){
                    subJson.data.each(function(subData){
                        var item = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                        this.selector.items.push(item);
                    }.bind(this));
                }.bind(this), null, this.data.id);
            }


            this.loaded = true;
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){

        var cCount = (this.data.companySubDirectCount) ? this.data.companySubDirectCount : 0;
        var dCount = (this.data.departmentSubDirectCount) ? this.data.departmentSubDirectCount : 0;
        var iCount = (this.data.identitySubDirectCount) ? this.data.identitySubDirectCount : 0;
        var count = cCount + dCount + iCount;
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
