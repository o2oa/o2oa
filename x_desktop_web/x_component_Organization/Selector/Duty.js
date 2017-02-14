MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Identity", null, false);
MWF.xApplication.Organization.Selector.Duty = new Class({
	Extends: MWF.xApplication.Organization.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Duty",
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
        this.comapnyCategory = this._newItemCategory("Category", {
            "name": "公司职务",
            "id": "company"
        }, this, this.itemAreaNode);

        this.comapnyCategory = this._newItemCategory("Category", {
            "name": "部门职务",
            "id": "department"
        }, this, this.itemAreaNode);

        //this.action.listTopCompany(function(json){
        //    json.data.each(function(data){
        //        var category = this._newItemCategory("ItemCompanyCategory", data, this, this.itemAreaNode);
        //    }.bind(this));
        //}.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(type, data, selector, item, level){
        return new MWF.xApplication.Organization.Selector.Duty[type](data, selector, item, level)
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
        return new MWF.xApplication.Organization.Selector.Duty.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this.action.listDepartmentByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.Duty.Item(data, selector, container, level);
    }
});
MWF.xApplication.Organization.Selector.Duty.Item = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/duty.png)");
    }
    //selected: function(){
    //    if ((this.selector.options.count==0) || (this.selector.selectedItems.length+1)<=this.selector.options.count){
    //        this.isSelected = true;
    //        this.node.setStyles(this.selector.css.selectorItem_selected);
    //        this.textNode.setStyles(this.selector.css.selectorItemTextNode_selected);
    //
    //        this.createInputDialog();
    //
    //        //this.actionNode.setStyles(this.selector.css.selectorItemActionNode_selected);
    //        //
    //        //this.selectedItem = this.selector._newItemSelected(this.data, this.selector, this);
    //        //this.selectedItem.check();
    //        //this.selector.selectedItems.push(this.selectedItem);
    //    }else{
    //        MWF.xDesktop.notice("error", {x: "right", y:"top"}, "最多可选择"+this.selector.options.count+"个选项", this.selector.node);
    //    }
    //},
    //unSelected: function(){
    //    //if (this.isSelected){
    //    //    this.node.addEvent("click", function(){
    //    //        this.clickItem();
    //    //    }.bind(this));
    //    //}
    //    this.isSelected = false;
    //    this.node.setStyles(this.selector.css.selectorItem);
    //    this.textNode.setStyles(this.selector.css.selectorItemTextNode);
    //    this.actionNode.setStyles(this.selector.css.selectorItemActionNode);
    //
    //    if (this.selectedItem){
    //        this.selector.selectedItems.erase(this.selectedItem);
    //
    //        this.selectedItem.items.each(function(item){
    //            if (item != this){
    //                item.isSelected = false;
    //                item.node.setStyles(this.selector.css.selectorItem);
    //                item.textNode.setStyles(this.selector.css.selectorItemTextNode);
    //                item.actionNode.setStyles(this.selector.css.selectorItemActionNode);
    //            }
    //        }.bind(this));
    //
    //        this.selectedItem.destroy();
    //        this.selectedItem = null;
    //    }
    //
    //    if (this.inputDialog){
    //        this.inputDialog.destroy();
    //        this.inputDialog = null;
    //        this.inputNode = null;
    //    }
    //},
    //createInputDialog: function(){
    //    var levelSize = this.levelNode.getSize();
    //    var iconSize = this.iconNode.getSize();
    //    var height = levelSize.y*3;
    //
    //    this.node.set("tween", {"duration": 100});
    //    this.node.tween("height", ""+height+"px");
    //    this.inputDialog = new Element("div", {"styles": this.selector.css.dutyInputDialog}).inject(this.node);
    //    this.inputNode = new Element("input", {"styles": this.selector.css.dutyInputNode}).inject(this.inputDialog);
    //
    //    this.node.removeEvents("click");
    //
    //
    //    MWF.xDesktop.requireApp("process.ProcessDesigner", "widget.ScriptText", function(){
    //        var script = new MWF.xApplication.process.ProcessDesigner.widget.ScriptText(node, this.data[node.get("name")], this.process.designer, {
    //            "maskNode": this.process.designer.content,
    //            "maxObj": this.process.designer.paperNode,
    //            "onChange": function(code){
    //                _self.data[node.get("name")] = code;
    //            }
    //        });
    //    }.bind(this));
    //
    //
    //    //if (!this.inputDialog){
    //    //    //this.inputDialog = new Element("div", {"styles": this.selector.css.dutyInputDialog}).inject(this.selector.node);
    //    //    //this.inputDialog.position({
    //    //    //    relativeTo: this.node,
    //    //    //    position: "top",
    //    //    //    edge: "top"
    //    //    //});
    //    //}
    //
    //}

});

MWF.xApplication.Organization.Selector.Duty.ItemSelected = new Class({
	Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/duty.png)");
    }
});

MWF.xApplication.Organization.Selector.Duty.Category = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/dutyCategory.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            if (this.data.id=="company"){
                this.selector.action.listCompanyDutyName(function(json){
                    json.data.valueList.each(function(value){
                        this.selector._newItem({"name": value}, this.selector, this.children, this.level+1);
                    }.bind(this));
                }.bind(this));
            }
            if (this.data.id=="department"){
                this.selector.action.listDepartmentDutyName(function(json){
                    json.data.valueList.each(function(value){
                        this.selector._newItem({"name": value}, this.selector, this.children, this.level+1);
                    }.bind(this));
                }.bind(this));
            }
            this.loaded = true;
            if (callback) callback();

            //this.selector.action.listSubComplexDirect(function(subJson){
            //    subJson.data.companyList.each(function(subData){
            //        var category = this.selector._newItemCategory("ItemCompanyCategory", subData, this.selector, this.children, this.level+1);
            //    }.bind(this));
            //    subJson.data.departmentList.each(function(subData){
            //        var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
            //    }.bind(this));
            //
            //    this.loaded = true;
            //    if (callback) callback();
            //
            //}.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    }
});
