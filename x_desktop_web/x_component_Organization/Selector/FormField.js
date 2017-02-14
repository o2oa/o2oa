MWF.xApplication.Organization.Selector = MWF.xApplication.Organization.Selector || {};
MWF.xDesktop.requireApp("Organization", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Organization", "Selector.Department", null, false);
MWF.xApplication.Organization.Selector.FormField = new Class({
	Extends: MWF.xApplication.Organization.Selector.Department,
    options: {
        "style": "default",
        "count": 0,
        "title": "Select Field",
        "fieldType": "",
        "values": [],
        "names": [],
        "application": "",
        "expand": false
    },

    loadSelectItems: function(addToNext){
        debugger;
        if (this.options.application){
            this.action.listFormField(this.options.application, function(json){
                this.fieldData = json.data;
                if (this.options.fieldType){
                    json.data[this.options.fieldType].each(function(data){
                        data.id = data.name;
                        var item = this._newItem(data, this, this.itemAreaNode);
                    }.bind(this));
                }else{
                    Object.each(json.data, function(v, k){
                        var category = this._newItemCategory({"name": k, "data": v}, this, this.itemAreaNode);
                    }.bind(this));
                }

                //json.data.each(function(data){
                //    var category = this._newItem(data, this, this.itemAreaNode);
                //}.bind(this));
            }.bind(this));
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Organization.Selector.FormField.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        var data = [];
        if (this.options.fieldType){
            data = this.fieldData[this.options.fieldType];
        }else{
            Object.each(this.fieldData, function(v, k){
                data = (data.length) ? data.concat(v) : v
            }.bind(this));
        }
        var searchData = [];
        data.each(function(d){
            if (d.name.toLowerCase().indexOf(key.toLowerCase())!=-1) searchData.push(d);
        }.bind(this));

        if (callback) callback.apply(this, [{"data": searchData}]);
        //if (callback) callback({"data": {"name": key, "id": key}});
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback({"data": {"name": id, "id": id}});
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Organization.Selector.FormField.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItemByKey(callback, failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Organization.Selector.FormField.Item(data, selector, container, level);
    }
});
MWF.xApplication.Organization.Selector.FormField.Item = new Class({
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

MWF.xApplication.Organization.Selector.FormField.ItemSelected = new Class({
    Extends: MWF.xApplication.Organization.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/processicon.png)");
    }
});

MWF.xApplication.Organization.Selector.FormField.ItemCategory = new Class({
    Extends: MWF.xApplication.Organization.Selector.Identity.ItemCompanyCategory,

    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"/x_component_Organization/Selector/$Selector/default/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            var subJson = this.selector.fieldData[this.data.name];

            //this.selector.action.listProcess(function(subJson){
                subJson.each(function(subData){
                    //subData.applicationName = this.data.name;
                    //subData.application = this.data.id;
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                }.bind(this));

                this.loaded = true;
                if (callback) callback();
            //}.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        var d = this.selector.fieldData[this.data.name];
        return (d && d.length);
    }
});
