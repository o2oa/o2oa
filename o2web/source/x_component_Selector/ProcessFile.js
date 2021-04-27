MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "PortalFile", null, false);
MWF.xApplication.Selector.ProcessFile = new Class({
	Extends: MWF.xApplication.Selector.PortalFile,
    options: {
        "style": "default",
        "count": 0,
        "title": "",
        "values": [],
        "names": [],
        "expand": false,
        "forceSearchInItem" : true
    },
    setInitTitle: function(){
        this.setOptions({"title": MWF.xApplication.Selector.LP.selectFile});
    },
    _init : function(){
        this.selectType = "file";
        this.className = "ProcessFile";
    },
    loadSelectItems: function(addToNext){
        if (this.options.isImage) this.options.accept = ["png","jpg","bmp","gif","jpeg","jpe"];
        this.processAction.listApplication(function(json){
            if (json.data.length){
                json.data.each(function(data){
                    this.processAction.listFile(data.id, function(fileJson){
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

    _newItemCategory: function(data, selector, item, level){
        return new MWF.xApplication.Selector.ProcessFile.ItemCategory(data, selector, item, level)
    },

    _newItemSelected: function(data, selector, item){
        return new MWF.xApplication.Selector.ProcessFile.ItemSelected(data, selector, item)
    },
    _newItem: function(data, selector, container, level){
        return new MWF.xApplication.Selector.ProcessFile.Item(data, selector, container, level);
    }
});
MWF.xApplication.Selector.ProcessFile.Item = new Class({
    Extends: MWF.xApplication.Selector.PortalFile.Item,
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){
                this.overItem();
            }.bind(this),
            "mouseout": function(){
                this.outItem();
            }.bind(this),
            "click": function(){
                this.clickItem();
            }.bind(this)
        });

        var url = MWF.xDesktop.getProcessFileUr(this.data.id, this.data.application);
        this.data.url = url;

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
});

MWF.xApplication.Selector.ProcessFile.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.PortalFile.ItemSelected
});

MWF.xApplication.Selector.ProcessFile.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.PortalFile.ItemCategory
});
