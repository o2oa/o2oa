MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Identity", null, false);
MWF.xApplication.Selector.PanFile = new Class({
	Extends: MWF.xApplication.Selector.Identity,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true,
        "allowPersonFile": true, //显示个人文件
        "allowUnitFile": true //显示企业文件
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectPanFile});
    },
    _init : function(){
        this.selectType = "panFile";
        this.className = "panFile";
    },
    loadSelectItems: function(addToNext){
        var category;
	    if( this.options.allowPersonFile ){
            category = this._newItemCategory({
                root: true,
                _type: 'personFile',
                name: MWF.xApplication.Selector.LP.personFile
            }, this, this.itemAreaNode);
        }
        if( this.options.allowUnitFile ){
            category = this._newItemCategory({
                root: true,
                _type: 'unitFile',
                name: MWF.xApplication.Selector.LP.unitFile
            }, this, this.itemAreaNode);
        }
        // this.queryAction.listApplication(function(json){
        //     if (json.data.length){
        //         json.data.each(function(data){
        //             if (!data.statementList){
        //                 this.queryAction.listStatement(data.id, filter,function(statementsJson){
        //                     data.statementList = statementsJson.data;
        //                 }.bind(this), null, false);
        //             }
        //             if (data.statementList && data.statementList.length){
        //                 var category = this._newItemCategory(data, this, this.itemAreaNode);
        //                 data.statementList.each(function(d){
        //                     d.applicationName = data.name;
        //                     var item = this._newItem(d, this, category.children);
        //                     this.items.push(item);
        //                 }.bind(this));
        //             }
        //         }.bind(this));
        //     }
        // }.bind(this));
    },

    _scrollEvent: function(y){
        return true;
    },
    _getChildrenItemIds: function(data){
        return data.statementList || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.PanFile.ItemCategory(data, selector, item, level)
    },

    _listItemByKey: function(callback, failure, key){
        return false;
    },
    _getItem: function(callback, failure, id, async){
        this.queryAction.getStatement(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.PanFile.ItemSelected(data, selector, item)
    },
    _listItemByPinyin: function(callback, failure, key){
        return false;
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.PanFile.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.PanFile.Item = new Class({
	Extends: MWF.xApplication.Selector.Identity.Item,
    _getShowName: function(){
        return this.data.name || this.data.text;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/category.png)");
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object"){
                if( this.data.id && item.id ){
                    return this.data.id === item.id;
                }else{
                    return this.data.name === item.name;
                }
                //return (this.data.id === item.id) || (this.data.name === item.name) ;
            }
            //if (typeOf(item)==="object") return (this.data.id === item.id) || (this.data.name === item.name) ;
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
            }else{
                return item.data.name === this.data.name;
            }
            //return (item.data.id === this.data.id) || (item.data.name === this.data.name);
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    }
});

MWF.xApplication.Selector.PanFile.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/view.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                //return (item.data.id === this.data.id) || (item.data.name === this.data.name);
                if( item.data.id && this.data.id){
                    return item.data.id === this.data.id;
                }else{
                    return item.data.name === this.data.name;
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

MWF.xApplication.Selector.PanFile.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Identity.ItemCategory,
    _getShowName: function(){
        return this.data.name || this.data.text;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    _hasChild: function(){
        return true;
    },
    loadSub: function(callback){
        debugger;
        if (!this.loaded){
            var actions = o2.Actions.load('x_pan_assemble_control');
            var categoryPromise, filePromise;
            var addType = function (promise, type){
                return promise.then( function(json){
                    json.data.each(function (d){ d._type = type; });
                    return json.data;
                });
            };
            switch (this.data._type){
                case "personFile":
                    categoryPromise = addType(
                        actions.Folder2Action.listTop('updateTime', true),
                        'person_folder_normal'
                    );
                    filePromise = addType(
                        actions.actions.Attachment2Action.listTop('updateTime', true),
                        'person_file'
                    );
                    break;
                case "person_folder_normal":
                    categoryPromise = addType(
                        actions.Folder2Action.listWithFolder(this.data.id, 'updateTime', true),
                        'person_folder_normal'
                    );
                    filePromise = addType(
                        actions.Attachment2Action.listWithFolder(this.data.id, 'updateTime', true),
                        'person_file'
                    );
                    break;
                case "unitFile":
                    categoryPromise = addType(
                        actions.ZoneAction.list(),
                        'unit_zone'
                    );
                    break;
                case 'unit_zone':
                    categoryPromise = addType(
                        actions.Folder3Action.listWithFolder(this.data.id, 'updateTime', true),
                        'person_folder_normal'
                    );
                    filePromise = addType(
                        actions.Attachment3Action.listWithFolder(this.data.id, 'updateTime', true),
                        'person_file'
                    );
                    break;
            }
            Promise.all([categoryPromise, filePromise]).then(function(arr){
                var categorys = arr[0] || [];
                var attachments = arr[1] || [];

                categorys.data.each(function(subData){
                    var category = this.selector._newItemCategory(subData, this.selector, this.children, this.level + 1);
                    // this.subCategorys.push( category );
                }.bind(this));

                attachments.data.each(function(subData){
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    this.selector.items.push( category );
                }.bind(this));

                this.loaded = true;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    check: function(){}
});
