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
        "hasLetter": false,
        "expand": false,
        "forceSearchInItem" : false,
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
        if( this.selectAllNode )this.selectAllNode.hide();
        var category;
	    if( this.options.allowPersonFile ){
            category = this._newItemCategory({
                root: true,
                _type: 'person_root',
                name: MWF.xApplication.Selector.LP.personFile
            }, this, this.itemAreaNode);
            this.subCategorys.push(category);
        }
        if( this.options.allowUnitFile ){
            category = this._newItemCategory({
                root: true,
                _type: 'unit_root',
                name: MWF.xApplication.Selector.LP.unitFile
            }, this, this.itemAreaNode);
            this.subCategorys.push(category);
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
        this._listItem( "key", callback, failure, key );
    },

    _listItemByPinyin: function(callback, failure, key){
        this._listItem( "pinyin", callback, failure, key );
    },

    _listItem : function( filterType, callback, failure, key ){
        var ps = [];
        var action = o2.Actions.load('x_pan_assemble_control');
        var p1, p2;
        if( this.options.allowPersonFile ){
            p1 = action.Attachment2Action.listWithFilter(key).then(function(json){
                json.data.each(function (d) { d._type = 'person_file'; });
                return json;
            });
            ps.push( p1 );
        }
        if( this.options.allowUnitFile ){
            p2 = action.Attachment3Action.listWithFilter(key).then(function(json){
                json.data.each(function (d) { d._type = 'unit_file'; });
                return json;
            });
            ps.push( p2 );
        }
        if(ps.length){
            Promise.all(ps).then(function(arr){
                var result = {data: []};
                arr.each(function(json){
                    result.data = result.data.concat(json.data);
                });
                if (callback) callback.apply(this, [result]);
            });
        }
    },
    _getItem: function(callback, failure, id, async){
        this.queryAction.getStatement(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.id), async);
    },
    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.PanFile.ItemSelected(data, selector, item)
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
    _getOOIcon: function (){
        return 'ooicon-doc-cooperation';
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+this.selector.options.style+"/icon/file.png)");
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
    },
    setEvent: function(){
        // var url = MWF.xDesktop.getPortalFileUr(this.data.id, this.data.portal);
        // this.data.url = url;
        this.node.addEvents({
            "mouseover": function(){
                this.overItem();
                if (!this.previewNode){
                    if (["png","jpg","bmp","gif","jpeg","jpe"].indexOf(this.data.extension)!==-1){
                        this.previewNode = new Element("div", {"styles": this.selector.css.filePreviewNode});

                        var action = o2.Actions.load('x_pan_assemble_control');
                        var host = o2.Actions.getHost('x_pan_assemble_control');
                        var url;
                        if( this.data._type === 'unit_file' ){
                            url = host + "/x_pan_assemble_control" + action.Attachment3Action.action.actions.downloadStream.uri.replace('{id}', this.data.id);
                        }else{
                            url = host + "/x_pan_assemble_control" + action.Attachment2Action.action.actions.downloadStream.uri.replace('{id}', this.data.id);
                        }

                        var img = new Element("img", {"src": url, "styles": this.selector.css.filePreviewNode}).inject(this.previewNode);
                        this.tooltip = new mBox.Tooltip({
                            content: this.previewNode,
                            setStyles: {content: {padding: 15, lineHeight: 20}},
                            attach: this.node,
                            position: {
                                y: ['center'],
                                x: ['right', 'outside']
                            },
                            transition: 'flyin'
                        });
                    }
                }
            }.bind(this),
            "mouseout": function(){
                this.outItem();
            }.bind(this),
            "click": function(){
                this.clickItem();
            }.bind(this)
        });

    }
});

MWF.xApplication.Selector.PanFile.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Identity.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _getOOIcon: function (){
        return 'ooicon-doc-cooperation';
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+this.selector.options.style+"/icon/file.png)");
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
    _getOOIcon: function (){
        return 'ooicon-files';
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/"+this.selector.options.style+"/icon/category.png)");
    },
    _hasChild: function(){
        if( this.data.hasOwnProperty("folderCount") || this.data.hasOwnProperty("attachmentCount") ){
            return (this.data.folderCount||0) + (this.data.attachmentCount||0);
        }else{
            return true;
        }
    },
    loadSub: function(callback){
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
                case "person_root":
                    categoryPromise = addType(
                        actions.Folder2Action.listTop('updateTime', true),
                        'person_folder_normal'
                    );
                    filePromise = addType(
                        actions.Attachment2Action.listTop('updateTime', true),
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
                case "unit_root":
                    categoryPromise = addType(
                        actions.ZoneAction.list(),
                        'unit_zone'
                    );
                    break;
                case 'unit_zone':
                case 'unit_folder_normal':
                    categoryPromise = addType(
                        actions.Folder3Action.listWithFolder(this.data.id, 'updateTime', true),
                        'unit_folder_normal'
                    );
                    filePromise = addType(
                        actions.Attachment3Action.listWithFolder(this.data.id, 'updateTime', true),
                        'unit_file'
                    );
                    break;
            }
            Promise.all([categoryPromise, filePromise]).then(function(arr){
                var categorys = arr[0] || [];
                var attachments = arr[1] || [];

                categorys.each(function(subData){
                    var category = this.selector._newItemCategory(subData, this.selector, this.children, this.level + 1);
                    this.subCategorys.push( category );
                }.bind(this));

                attachments.each(function(subData){
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    this.subItems.push( category );
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
