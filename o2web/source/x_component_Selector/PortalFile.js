MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Process", null, false);
MWF.xApplication.Selector.PortalFile = new Class({
	Extends: MWF.xApplication.Selector.Process,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "isImage": false,
        "accept": [],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectFile});
    },
    _init : function(){
        this.selectType = "file";
        this.className = "PortalFile";
    },
    loadSelectItems: function(addToNext){
	    if (this.options.isImage) this.options.accept = ["png","jpg","bmp","gif","jpeg","jpe"];
        this.portalAction.listApplication(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    this.portalAction.listFile(data.id, function(fileJson){
                        var files = fileJson.data;
                        if (this.options.accept && this.options.accept.length){
                            files = files.filter(function(file){
                                var extName = file.fileName.substring(file.fileName.lastIndexOf(".")+1, file.fileName.length).toLowerCase();
                                return (this.options.accept.indexOf(extName)!==-1)
                            }.bind(this));
                        }

                        if (files.length){
                            data.files = files;
                            var category = this._newItemCategory(data, this, this.itemAreaNode);
                            files.each(function(d){
                                d.applicationName = data.name;
                                var item = this._newItem(d, this, category.children);
                                this.items.push(item);
                            }.bind(this));
                        }
                    }.bind(this));
                }.bind(this));
            }
        }.bind(this));
    },

    //loadSelectNode: function(){
    //    this.selectNode = new Element("div", {
    //        "styles": this.css.selectNode //(this.options.count.toInt()===1) ? this.css.selectNodeSingle : this.css.selectNode
    //    }).inject(this.contentNode);
    //
    //    this.itemAreaScrollNode = new Element("div", {
    //        "styles": this.css.itemAreaScrollNode
    //    }).inject(this.selectNode);
    //    this.itemAreaScrollNode.setStyle("height", "408px");
    //
    //    this.itemAreaNode = new Element("div", {
    //        "styles": this.css.itemAreaNode
    //    }).inject(this.itemAreaScrollNode);
    //    this.itemSearchAreaNode = new Element("div", {
    //        "styles": this.css.itemAreaNode
    //    }).inject(this.itemAreaScrollNode);
    //    this.itemSearchAreaNode.setStyle("display", "none");
    //
    //    this.loadSelectNodeScroll();
    //    this.initLoadSelectItems();
    //    this.checkLoadSelectItems();
    //},

    close: function(){
        this.fireEvent("close");
        this.node.destroy();
        this.container.unmask();
        if (this.maskInterval){
            window.clearInterval(this.maskInterval);
            this.maskInterval = null;
        }
        this.selectedItems.each(function(item){
            item.destroy();
        });
        this.items.each(function(item){
            item.destroy();
        });
        this.active = false;

        MWF.release(this);
        delete this;
    },

    _getChildrenItemIds: function(data){
        return data.files || [];
    },
    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.PortalFile.ItemCategory(data, selector, item, level)
    },

    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.PortalFile.ItemSelected(data, selector, item)
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.PortalFile.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.PortalFile.Item = new Class({
	Extends: MWF.xApplication.Selector.Process.Item,

    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    loadSubItem: function(){
        return false;
    },
    checkSelectedSingle: function(){
        var selectedItem = this.selector.options.values.filter(function(item, index){
            if (typeOf(item)==="object") return (this.data.id === item.id) || (this.data.name === item.name) ;
            if (typeOf(item)==="string") return (this.data.id === item) || (this.data.name === item);
            return false;
        }.bind(this));
        if (selectedItem.length){
            this.selectedSingle();
        }
    },
    checkSelected: function(){

        var selectedItem = this.selector.selectedItems.filter(function(item, index){
            return (item.data.id === this.data.id) || (item.data.name === this.data.name);
        }.bind(this));
        if (selectedItem.length){
            //selectedItem[0].item = this;
            selectedItem[0].addItem(this);
            this.selectedItem = selectedItem[0];
            this.setSelected();
        }
    },
    setEvent: function(){
        var url = MWF.xDesktop.getPortalFileUr(this.data.id, this.data.portal);
        this.data.url = url;
        this.node.addEvents({
            "mouseover": function(){
                this.overItem();
                if (!this.previewNode){
                    var extName = this.data.fileName.substring(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length).toLowerCase();
                    if (["png","jpg","bmp","gif","jpeg","jpe"].indexOf(extName)!==-1){
                        this.previewNode = new Element("div", {"styles": this.selector.css.filePreviewNode});

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

    },
    destroy: function(){
        if (this.tooltip) this.tooltip.destroy();
        this.node.destroy();
    }

});

MWF.xApplication.Selector.PortalFile.ItemSelected = new Class({
	Extends: MWF.xApplication.Selector.Process.ItemSelected,
    _getShowName: function(){
        return this.data.name;
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/attr.png)");
    },
    check: function(){
        if (this.selector.items.length){
            var items = this.selector.items.filter(function(item, index){
                return (item.data.id === this.data.id) || (item.data.name === this.data.name);
            }.bind(this));
            this.items = items;
            if (items.length){
                items.each(function(item){
                    item.selectedItem = this;
                    item.setSelected();
                }.bind(this));
            }
        }
    },
    setEvent: function(){
        var url = MWF.xDesktop.getPortalFileUr(this.data.id, this.data.portal);
        this.data.url = url;

        this.node.addEvents({
            "mouseover": function(){
                this.overItem();
                if (!this.previewNode){
                    var extName = this.data.fileName.substring(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length).toLowerCase();
                    if (["png","jpg","bmp","gif","jpeg","jpe"].indexOf(extName)!==-1){
                        this.previewNode = new Element("div", {"styles": this.selector.css.filePreviewNode}).inject(this.selector.node);

                        var img = new Element("img", {"src": url, "styles": this.selector.css.filePreviewNode}).inject(this.previewNode);
                        this.tooltip = new mBox.Tooltip({
                            content: this.previewNode,
                            setStyles: {content: {padding: 15, lineHeight: 20}},
                            attach: this.node,
                            position: {
                                y: ['center'],
                                x: ['left', 'outside']
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
    },
    destroy: function(){
        if (this.tooltip) this.tooltip.destroy();
        this.node.destroy();
    }
});

MWF.xApplication.Selector.PortalFile.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Process.ItemCategory,
    _getShowName: function(){
        return this.data.name;
    },
    createNode: function(){
        this.node = new Element("div", {
            "styles": this.selector.css.selectorItemCategory_department
        }).inject(this.container);
    },
    _setIcon: function(){
        this.iconNode.setStyle("background-image", "url("+"../x_component_Selector/$Selector/default/icon/applicationicon.png)");
    },
    afterLoad: function(){
        return true;
    },
    loadSub: function(callback){
        if (!this.loaded){
            this.selector.portalAction.listFile(this.data.id, function(subJson){
                subJson.data.each(function(subData){
                    subData.applicationName = this.data.name;
                    subData.application = this.data.id;
                    var category = this.selector._newItem(subData, this.selector, this.children, this.level+1);
                    this.selector.items.push( category );
                }.bind(this));

                this.loaded = true;
                if (callback) callback();
            }.bind(this), null, this.data.id);
        }else{
            if (callback) callback();
        }
    },
    _hasChild: function(){
        return true;
    },
    check: function(){}
});
