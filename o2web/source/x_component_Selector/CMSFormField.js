MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.CMSFormField = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": MWF.xApplication.Selector.LP.selectField,
        "fieldType": "",
        "values": [],
        "names": [],
        "application": "",
        "form" : "",
        "expand": false,
        "forceSearchInItem" : true
    },
    getFields : function(){
        var dataTypes = {
            "string": ["htmledit", "radio", "select", "textarea", "textfield"],
            "person": ["personfield","org","orgfield"],
            "date": ["calender"],
            "number": ["number"],
            "array": ["checkbox"]
        };
        fieldList = [];
        Object.each( this.relativeFormData.json.moduleList, function(moudle){
            var key = "";
            for (k in dataTypes){
                if (dataTypes[k].indexOf( ( moudle.moduleName || moudle.type ).toLowerCase())!==-1){
                    key = k;
                    break;
                }
            }
            if (key){
                fieldList.push({
                    "name": moudle.id,
                    "dataType": key
                });
            }
        }.bind(this));
        return fieldList;
    },
    loadSelectItems: function(addToNext){
        if( this.options.form ){
            this.cmsAction.getCMSForm(this.options.form, function(json){
                this.relativeFormData = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                this.getFields().each( function( f ){
                    if( !this.options.fieldType || this.options.fieldType === f.dataType ){
                        f.id = f.name;
                        var item = this._newItem(f, this, this.itemAreaNode);
                        this.items.push( item );
                    }
                }.bind(this));
            }.bind(this))
        }else{
            if (this.options.application){
                this.cmsAction.listCMSFormField(this.options.application, function(json){
                    this.fieldData = json.data;
                    if (this.options.fieldType){
                        json.data[this.options.fieldType].each(function(data){
                            data.id = data.name;
                            var item = this._newItem(data, this, this.itemAreaNode);
                            this.items.push( item );
                        }.bind(this));
                    }else{
                        Object.each(json.data, function(v, k){
                            var category = this._newItemCategory({"name": k, "data": v}, this, this.itemAreaNode);
                        }.bind(this));
                    }
                }.bind(this));
            }
        }
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(){
        return null;
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.CMSFormField.ItemCategory(data, selector, item, level)
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
            if (d.name.toLowerCase().indexOf(key.toLowerCase())!==-1) searchData.push(d);
        }.bind(this));

        if (callback) callback.apply(this, [{"data": searchData}]);
        //if (callback) callback({"data": {"name": key, "id": key}});
    },
    _getItem: function(callback, failure, id, async){
        if (callback) callback({"data": {"name": id, "id": id}});
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.CMSFormField.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        this._listItemByKey(callback, failure, key);
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.CMSFormField.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.CMSFormField.Item = new Class({
    Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
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
    },
    getData: function(callback){
        if (callback) callback();
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object") return (this.data.name === item.name) ;
            if (typeOf(item)==="string") return (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){
        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return item.data.name === this.data.name;
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.CMSFormField.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/processicon.png)");
    }
});

MWF.xApplication.Selector.CMSFormField.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,

    _setIcon: function(){
        var style = "default";
        this.iconNode.setStyle("background-image", "url("+"/x_component_Selector/$Selector/"+style+"/icon/applicationicon.png)");
    },
    loadSub: function(callback){
        if (!this.loaded){
            var subJson = this.selector.fieldData[this.data.name];

            //this.selector.action.listProcess(function(subJson){
                subJson.each(function(subData){
                    //subData.applicationName = this.data.name;
                    //subData.application = this.data.id;
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    this.selector.items.push( item );
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
