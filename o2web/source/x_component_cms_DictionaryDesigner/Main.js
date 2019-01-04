MWF.CMSDD = MWF.xApplication.cms.DictionaryDesigner;
MWF.CMSDD.options = {
	"multitask": true,
	"executable": false
};
//MWF.xDesktop.requireApp("cms.ColumnManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("cms.DictionaryDesigner", "Dictionary", null, false);
MWF.xApplication.cms.DictionaryDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"name": "cms.DictionaryDesigner",
		"icon": "icon.png",
		"title": MWF.CMSDD.LP.title,
		"appTitle": MWF.CMSDD.LP.title,
		"id": "",
        "width": "1200",
        "height": "600",
		"actions": null,
		"category": null,
		"processData": null
	},
	onQueryLoad: function(){
        this.shortcut = true;
        if (this.status){
            this.options.application = this.status.applicationId;
            this.application = this.status.application || this.status.applicationId;;
            this.options.id = this.status.id;
            this.setOptions(this.status.options);
        }

		if (!this.options.id){
			this.options.desktopReload = false;
			this.options.title = this.options.title + "-"+MWF.CMSDD.LP.newDictionary;
		}
		this.actions =  MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.ColumnManager.Actions.RestActions();
		
		this.lp = MWF.xApplication.cms.DictionaryDesigner.LP;
//		this.cmsData = this.options.processData;
        this.actions.application = this.application;

          this.addEvent("queryClose", function(e){
            if (this.explorer){
                this.explorer.reload();
            }
        }.bind(this));
	},
	
	loadApplication: function(callback){
		this.createNode();
		if (!this.options.isRefresh){
			this.maxSize(function(){
				this.openForm();
			}.bind(this));
		}else{
			this.openForm();
		}

        if (!this.options.readMode) this.addKeyboardEvents();
		if (callback) callback();
	},
    addKeyboardEvents: function(){
        this.addEvent("copy", function(){
            this.copyModule();
        }.bind(this));
        this.addEvent("paste", function(){
            this.pasteModule();
        }.bind(this));
        this.addEvent("cut", function(){
            this.cutModule();
        }.bind(this));
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
        this.addEvent("keyDelete", function(e){
            this.keyDelete(e);
        }.bind(this));
    },
    keySave: function(e){
        if (this.shortcut) {
            if (this.tab.showPage) {
                var dictionary = this.tab.showPage.dictionary;
                if (dictionary) {
                    dictionary.save();
                    e.preventDefault();
                }
            }
        }
    },
    keyDelete: function(){
        if (this.shortcut) {
            if (this.tab.showPage) {
                var dictionary = this.tab.showPage.dictionary;
                if (dictionary) {
                    if (dictionary.currentSelectedItem) {
                        var item = dictionary.currentSelectedItem;
                        item.delItem(item.itemTextNode);
                    }
                }
            }
        }
    },

    copyModule: function(){
        if (this.shortcut) {
            if (this.tab.showPage) {
                var dictionary = this.tab.showPage.dictionary;
                if (dictionary) {
                    if (dictionary.currentSelectedItem) {
                        var item = dictionary.currentSelectedItem;
                        MWF.clipboard.data = {
                            "type": "dictionary",
                            "data": {
                                "key": item.key,
                                "value": (typeOf(item.value)=="object") ? Object.clone(item.value) : item.value
                            }
                        };
                    }
                }
            }
        }
    },
    cutModule: function(){
        if (this.shortcut) {
            if (this.tab.showPage) {
                var dictionary = this.tab.showPage.dictionary;
                if (dictionary) {
                    if (dictionary.currentSelectedItem) {
                        this.copyModule();
                        var item = dictionary.currentSelectedItem;
                        item.destroy();
                    }
                }
            }
        }
    },
    pasteModule: function(){
        if (this.shortcut) {
            if (MWF.clipboard.data) {
                if (MWF.clipboard.data.type == "dictionary") {
                    if (this.tab.showPage) {
                        var dictionary = this.tab.showPage.dictionary;
                        if (dictionary) {
                            if (dictionary.currentSelectedItem) {
                                var item = dictionary.currentSelectedItem;
                                var key = MWF.clipboard.data.data.key;

                                var value = (typeOf(MWF.clipboard.data.data.value)=="object") ? Object.clone(MWF.clipboard.data.data.value) : MWF.clipboard.data.data.value;

                                var level = item.level;
                                var parent = item;
                                var nextSibling = null;
                                if (!item.parent){//top level
                                    level = 1;
                                }else{
                                    if (item.type!="array" && item.type!="object"){
                                        parent = item.parent;
                                        nextSibling = item;
                                    }else{
                                        if (item.exp){
                                            level = item.level+1;
                                        }else{
                                            parent = item.parent;
                                            nextSibling = item;
                                        }
                                    }
                                }
                                var idx = parent.children.length;
                                if (item.type=="array"){
                                    if (nextSibling){
                                        key = nextSibling.key;
                                        parent.value.splice(nextSibling.key, 0, value);
                                        for (var i=nextSibling.key; i<parent.children.length; i++){
                                            subItem = parent.children[i];
                                            subItem.key = subItem.key+1;
                                            subItem.setNodeText();
                                        }
                                    }else{
                                        var key = parent.value.length;
                                        parent.value.push(value);
                                    }
                                    idx = key;
                                }else{
                                    var oldKey = key;
                                    var i = 0;
                                    while (parent.value[key] != undefined) {
                                        i++;
                                        key = oldKey + i;
                                    }
                                    parent.value[key] = value;
                                    if (nextSibling) var idx = parent.children.indexOf(nextSibling);
                                }
                                var item = new MWF.xApplication.cms.DictionaryDesigner.Dictionary.item(key, value, parent, level, this.dictionary, true, nextSibling);
                                if (idx) parent.children[idx-1].nextSibling = item;
                                parent.children.splice(idx, 0, item);

                            }
                        }
                    }
                }
            }
        }
    },

	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
    getApplication:function(callback){
        if (!this.application){
            this.actions.getApplication(this.options.application, function(json){
                this.application = {"name": json.data.name || json.data.appName, "id": json.data.id};
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

	openForm: function(){
        this.getApplication(function(){
            this.initOptions();
            this.loadNodes();
            this.loadDictionaryListNodes();
        //	this.loadToolbar();
            this.loadContentNode();
            this.loadProperty();
        //	this.loadTools();
            this.resizeNode();
            this.addEvent("resize", this.resizeNode.bind(this));
            this.loadDictionary();

            if (this.toolbarContentNode){
                this.setScrollBar(this.toolbarContentNode, null, {
                    "V": {"x": 0, "y": 0},
                    "H": {"x": 0, "y": 0}
                });
                this.setScrollBar(this.propertyDomArea, null, {
                    "V": {"x": 0, "y": 0},
                    "H": {"x": 0, "y": 0}
                });
            }
        }.bind(this));
	},
	initOptions: function(){
		//this.toolsData = null;
		//this.toolbarMode = "all";
		//this.tools = [];
		//this.toolbarDecrease = 0;
		//
		//this.designNode = null;
		//this.form = null;
	},
	loadNodes: function(){
        this.dictionaryListNode = new Element("div", {
            "styles": this.css.dictionaryListNode
        }).inject(this.node);
		this.propertyNode = new Element("div", {
			"styles": this.css.propertyNode
		}).inject(this.node);
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.node);
	},

    //loadDictionaryListNodes-------------------------------
    loadDictionaryListNodes: function(){
        this.dictionaryListTitleNode = new Element("div", {
            "styles": this.css.dictionaryListTitleNode,
            "text": MWF.CMSDD.LP.dictionary
        }).inject(this.dictionaryListNode);

        this.dictionaryListResizeNode = new Element("div", {"styles": this.css.dictionaryListResizeNode}).inject(this.dictionaryListNode);
        this.dictionaryListAreaSccrollNode = new Element("div", {"styles": this.css.dictionaryListAreaSccrollNode}).inject(this.dictionaryListNode);
        this.dictionaryListAreaNode = new Element("div", {"styles": this.css.dictionaryListAreaNode}).inject(this.dictionaryListAreaSccrollNode);

        this.loadDictionaryListResize();

        this.loadDictionaryList();
    },

    loadDictionaryListResize: function(){
        this.dictionaryListResize = new Drag(this.dictionaryListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.dictionaryListAreaSccrollNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<40) width = 40;
                this.contentNode.setStyle("margin-left", width+1);
                this.dictionaryListNode.setStyle("width", width);
            }.bind(this)
        });
    },

    loadDictionaryList: function(){
        this.actions.listDictionary(this.application.id || this.application, function (json) {
            json.data.each(function(dictionary){
                this.createListDictionaryItem(dictionary);
            }.bind(this));
        }.bind(this), null, false);
    },

    createListDictionaryItem: function(dictionary, isNew){
        var _self = this;
        var listDictionaryItem = new Element("div", {"styles": this.css.listDictionaryItem}).inject(this.dictionaryListAreaNode, (isNew) ? "top": "bottom");
        var listDictionaryItemIcon = new Element("div", {"styles": this.css.listDictionaryItemIcon}).inject(listDictionaryItem);
        var listDictionaryItemText = new Element("div", {"styles": this.css.listDictionaryItemText, "text": (dictionary.name) ? dictionary.name+" ("+dictionary.alias+")" : this.lp.newDictionary}).inject(listDictionaryItem);

        listDictionaryItem.store("dictionary", dictionary);
        listDictionaryItem.addEvents({
            "dblclick": function(e){_self.loadDictionaryByData(this, e);},
            "mouseover": function(){if (_self.currentListDictionaryItem!=this) this.setStyles(_self.css.listDictionaryItem_over);},
            "mouseout": function(){if (_self.currentListDictionaryItem!=this) this.setStyles(_self.css.listDictionaryItem);}
        });
    },
    loadDictionaryByData: function(node, e){
        var dictionary = node.retrieve("dictionary");

        var openNew = true;
        for (var i = 0; i<this.tab.pages.length; i++){
            if (dictionary.id==this.tab.pages[i].dictionary.data.id){
                this.tab.pages[i].showTabIm();
                openNew = false;
                break;
            }
        }
        if (openNew){
            this.loadDictionaryData(dictionary.id, function(data){
                var dictionary = new MWF.xApplication.cms.DictionaryDesigner.Dictionary(this, data);
                dictionary.load();
            }.bind(this), true);
        }
    },


	//loadContentNode------------------------------
    loadContentNode: function(){
		this.contentToolbarNode = new Element("div#contentToolbarNode", {
			"styles": this.css.contentToolbarNode
		}).inject(this.contentNode);
		if (!this.options.readMode) this.loadContentToolbar();
		
		this.editContentNode = new Element("div", {
			"styles": this.css.editContentNode
		}).inject(this.contentNode);

		this.loadEditContent(function(){
		//	if (this.designDcoument) this.designDcoument.body.setStyles(this.css.designBody);
			if (this.designNode) this.designNode.setStyles(this.css.designNode);
		}.bind(this));
	},
    loadContentToolbar: function(callback){
		this.getFormToolbarHTML(function(toolbarNode){
			var spans = toolbarNode.getElements("span");
			spans.each(function(item, idx){
				var img = item.get("MWFButtonImage");
				if (img){
					item.set("MWFButtonImage", this.path+""+this.options.style+"/toolbar/"+img);
				}
			}.bind(this));

			$(toolbarNode).inject(this.contentToolbarNode);
			MWF.require("MWF.widget.Toolbar", function(){
				this.toolbar = new MWF.widget.Toolbar(toolbarNode, {"style": "ProcessCategory"}, this);
				this.toolbar.load();
				if (callback) callback();
			}.bind(this));
		}.bind(this));
	},
	getFormToolbarHTML: function(callback){
		var toolbarUrl = this.path+this.options.style+"/toolbars.html";
		var r = new Request.HTML({
			url: toolbarUrl,
			method: "get",
			onSuccess: function(responseTree, responseElements, responseHTML, responseJavaScript){
				var toolbarNode = responseTree[0];
				if (callback) callback(toolbarNode);
			}.bind(this),
			onFailure: function(xhr){
				this.notice("request cmsToolbars error: "+xhr.responseText, "error");
			}.bind(this)
		});
		r.send();
	},
    maxOrReturnEditor: function(){
        if (!this.isMax){
            this.designNode.inject(this.node);
            this.designNode.setStyles({
                "position": "absolute",
                "width": "100%",
                "height": "100%",
                "top": "0px",
                "margin": "0px",
                "left": "0px"
            });
            this.tab.pages.each(function(page){
                page.dictionary.setAreaNodeSize();
            });
            this.isMax = true;
        }else{
            this.isMax = false;
            this.designNode.inject(this.editContentNode);
            this.designNode.setStyles(this.css.designNode);
            this.designNode.setStyles({
                "position": "static"
            });
            this.resizeNode();
            this.tab.pages.each(function(page){
                page.dictionary.setAreaNodeSize();
            });
        }

    },
    loadEditContent: function(callback){
        this.designNode = new Element("div", {
            "styles": this.css.designNode
        }).inject(this.editContentNode);

        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.designNode, {"style": "dictionary"});
            this.tab.load();
        }.bind(this), false);

        //    MWF.require("MWF.widget.ScrollBar", function(){
        //        new MWF.widget.ScrollBar(this.designNode, {"distance": 100});
        //    }.bind(this));
	},
	
	//loadProperty------------------------
	loadProperty: function(){
		this.propertyTitleNode = new Element("div", {
			"styles": this.css.propertyTitleNode,
			"text": MWF.CMSDD.LP.property
		}).inject(this.propertyNode);
		
		this.propertyResizeBar = new Element("div", {
			"styles": this.css.propertyResizeBar
		}).inject(this.propertyNode);
		this.loadPropertyResize();
		
		this.propertyContentNode = new Element("div", {
			"styles": this.css.propertyContentNode
		}).inject(this.propertyNode);
		
		this.propertyDomArea = new Element("div", {
			"styles": this.css.propertyDomArea
		}).inject(this.propertyContentNode);
		
		this.propertyDomPercent = 0.3;
		this.propertyContentResizeNode = new Element("div", {
			"styles": this.css.propertyContentResizeNode
		}).inject(this.propertyContentNode);
		
		this.propertyContentArea = new Element("div", {
			"styles": this.css.propertyContentArea
		}).inject(this.propertyContentNode);
		
		this.loadPropertyContentResize();

        this.setPropertyContent();
        this.propertyNode.addEvent("keydown", function(e){e.stopPropagation();});
	},

    setPropertyContent: function(){
        this.dictionaryPropertyNode = new Element("div", {"styles": this.css.dictionaryPropertyNode});
        this.jsonDomNode = new Element("div", {"styles": this.css.jsonDomNode});
        this.jsonTextNode = new Element("div", {"styles": this.css.jsonTextNode});
        this.jsonTextAreaNode = new Element("textarea", {"styles": this.css.jsonTextAreaNode}).inject(this.jsonTextNode);

        MWF.require("MWF.widget.Tab", function(){
            var tab = new MWF.widget.Tab(this.propertyContentArea, {"style": "moduleList"});
            tab.load();
            tab.addTab(this.dictionaryPropertyNode, this.lp.property, false);
            tab.addTab(this.jsonDomNode, "JSON", false);
            tab.addTab(this.jsonTextNode, "TEXT", false);
            tab.pages[0].showTab();
        }.bind(this));

        var node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.id+":"}).inject(this.dictionaryPropertyNode);
        this.propertyIdNode = new Element("div", {"styles": this.css.propertyTextNode}).inject(this.dictionaryPropertyNode);

        node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.name+":"}).inject(this.dictionaryPropertyNode);
        this.propertyNameNode = new Element("input", {"styles": this.css.propertyInputNode}).inject(this.dictionaryPropertyNode);
        if (this.options.noModifyName || this.options.readMode){
            this.propertyNameNode.set("readonly", true);
            this.propertyNameNode.addEvent("keydown", function(){
                this.notice(this.lp.notice.noModifyName, "error");
            }.bind(this));
        }

        node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.alias+":"}).inject(this.dictionaryPropertyNode);
        this.propertyAliasNode = new Element("input", {"styles": this.css.propertyInputNode}).inject(this.dictionaryPropertyNode);
        if (this.options.noModifyName || this.options.readMode){
            this.propertyAliasNode.set("readonly", true);
            this.propertyAliasNode.addEvent("keydown", function(){
                this.notice(this.lp.notice.noModifyName, "error");
            }.bind(this));
        }

        node = new Element("div", {"styles": this.css.propertyTitleNode, "text": this.lp.description+":"}).inject(this.dictionaryPropertyNode);
        this.propertyDescriptionNode = new Element("textarea", {"styles": this.css.propertyInputAreaNode}).inject(this.dictionaryPropertyNode);
        if (this.options.noModifyName || this.options.readMode){
            this.propertyDescriptionNode.set("readonly", true);
        }
    },

	loadPropertyResize: function(){
//		var size = this.propertyNode.getSize();
//		var position = this.propertyResizeBar.getPosition();
		this.propertyResize = new Drag(this.propertyResizeBar,{
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
				var bodySize = this.content.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth+dx;
				if (width> bodySize.x/2) width =  bodySize.x/2;
				if (width<40) width = 40;
				this.contentNode.setStyle("margin-right", width+1);
				this.propertyNode.setStyle("width", width);
			}.bind(this)
		});
	},
	loadPropertyContentResize: function(){
		this.propertyContentResize = new Drag(this.propertyContentResizeNode, {
			"snap": 1,
			"onStart": function(el, e){
				var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				el.store("position", {"x": x, "y": y});
				
				var size = this.propertyDomArea.getSize();
				el.store("initialHeight", size.y);
			}.bind(this),
			"onDrag": function(el, e){
				var size = this.propertyContentNode.getSize();
				
	//			var x = e.event.x;
				var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
				var position = el.retrieve("position");
				var dy = y.toFloat()-position.y.toFloat();

				var initialHeight = el.retrieve("initialHeight").toFloat();
				var height = initialHeight+dy;
				if (height<40) height = 40;
				if (height> size.y-40) height = size.y-40;
				
				this.propertyDomPercent = height/size.y;
				
				this.setPropertyContentResize();
				
			}.bind(this)
		});
	},
	setPropertyContentResize: function(){
		var size = this.propertyContentNode.getSize();
		var resizeNodeSize = this.propertyContentResizeNode.getSize();
		var height = size.y-resizeNodeSize.y;
		
		var domHeight = this.propertyDomPercent*height;
		var contentHeight = height-domHeight;
		
		this.propertyDomArea.setStyle("height", ""+domHeight+"px");
		this.propertyContentArea.setStyle("height", ""+contentHeight+"px");
		
		if (this.form){
			if (this.form.currentSelectedModule){
				if (this.form.currentSelectedModule.property){
					var tab = this.form.currentSelectedModule.property.propertyTab;
					if (tab){
						var tabTitleSize = tab.tabNodeContainer.getSize();
						
						tab.pages.each(function(page){
							var topMargin = page.contentNodeArea.getStyle("margin-top").toFloat();
							var bottomMargin = page.contentNodeArea.getStyle("margin-bottom").toFloat();
							
							var tabContentNodeAreaHeight = contentHeight - topMargin - bottomMargin - tabTitleSize.y.toFloat()-15;
							page.contentNodeArea.setStyle("height", tabContentNodeAreaHeight);
						}.bind(this));
						
					}
				}
			}
		}
	},
	

	
	//resizeNode------------------------------------------------
	resizeNode: function(){
		var nodeSize = this.node.getSize();
		this.contentNode.setStyle("height", ""+nodeSize.y+"px");
		this.propertyNode.setStyle("height", ""+nodeSize.y+"px");
		
		var contentToolbarMarginTop = this.contentToolbarNode.getStyle("margin-top").toFloat();
		var contentToolbarMarginBottom = this.contentToolbarNode.getStyle("margin-bottom").toFloat();
		var allContentToolberSize = this.contentToolbarNode.getComputedSize();
		var y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom;
		this.editContentNode.setStyle("height", ""+y+"px");
		
		if (this.designNode){
			var designMarginTop = this.designNode.getStyle("margin-top").toFloat();
			var designMarginBottom = this.designNode.getStyle("margin-bottom").toFloat();
			y = nodeSize.y - allContentToolberSize.totalHeight - contentToolbarMarginTop - contentToolbarMarginBottom - designMarginTop - designMarginBottom;
			this.designNode.setStyle("height", ""+y+"px");
		}
		
		
		titleSize = this.propertyTitleNode.getSize();
		titleMarginTop = this.propertyTitleNode.getStyle("margin-top").toFloat();
		titleMarginBottom = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		titlePaddingTop = this.propertyTitleNode.getStyle("padding-top").toFloat();
		titlePaddingBottom = this.propertyTitleNode.getStyle("padding-bottom").toFloat();
		
		y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom;
		y = nodeSize.y-y;
		this.propertyContentNode.setStyle("height", ""+y+"px");
		this.propertyResizeBar.setStyle("height", ""+y+"px");
		
		this.setPropertyContentResize();

        titleSize = this.dictionaryListTitleNode.getSize();
        titleMarginTop = this.dictionaryListTitleNode.getStyle("margin-top").toFloat();
        titleMarginBottom = this.dictionaryListTitleNode.getStyle("margin-bottom").toFloat();
        titlePaddingTop = this.dictionaryListTitleNode.getStyle("padding-top").toFloat();
        titlePaddingBottom = this.dictionaryListTitleNode.getStyle("padding-bottom").toFloat();
        nodeMarginTop = this.dictionaryListAreaSccrollNode.getStyle("margin-top").toFloat();
        nodeMarginBottom = this.dictionaryListAreaSccrollNode.getStyle("margin-bottom").toFloat();

        y = titleSize.y+titleMarginTop+titleMarginBottom+titlePaddingTop+titlePaddingBottom+nodeMarginTop+nodeMarginBottom;
        y = nodeSize.y-y;
        this.dictionaryListAreaSccrollNode.setStyle("height", ""+y+"px");
        this.dictionaryListResizeNode.setStyle("height", ""+y+"px");
	},
	
	//loadForm------------------------------------------
    loadDictionary: function(){
        //debugger;
		this.getDictionaryData(this.options.id, function(ddata){
            this.setTitle(this.options.appTitle + "-"+ddata.name);
            this.taskitem.setText(this.options.appTitle + "-"+ddata.name);
            this.options.appTitle = this.options.appTitle + "-"+ddata.name;

            if (this.options.readMode){
                this.dictionary = new MWF.xApplication.cms.DictionaryDesigner.DictionaryReader(this, ddata);
            }else{
                this.dictionary = new MWF.xApplication.cms.DictionaryDesigner.Dictionary(this, ddata);
            }

			this.dictionary.load();

            if (this.status){
                if (this.status.openDictionarys){
                    this.status.openDictionarys.each(function(id){
                        this.loadDictionaryData(id, function(data){
                            var showTab = true;
                            if (this.status.currentId){
                                if (this.status.currentId!=data.id) showTab = false;
                            }
                            if (this.options.readMode){
                                var dictionary = new MWF.xApplication.cms.DictionaryDesigner.DictionaryReader(this, data, {"showTab": showTab});
                            }else{
                                var dictionary = new MWF.xApplication.cms.DictionaryDesigner.Dictionary(this, data, {"showTab": showTab});
                            }

                            dictionary.load();
                        }.bind(this), true);
                    }.bind(this));
                }
            }
		}.bind(this));
	},
    getDictionaryData: function(id, callback){
		if (!this.options.id){
			this.loadNewDictionaryData(callback);
		}else{
			this.loadDictionaryData(id, callback);
		}
	},
	loadNewDictionaryData: function(callback){
        var data = {
            "name": "",
            "id": "",
            "application": this.application.id,
            "alias": "",
            "description": "",
            "data": {}
        };
        this.createListDictionaryItem(data, true);
        if (callback) callback(data);
	},
	loadDictionaryData: function(id, callback){
        //debugger;
		this.actions.getDictionary(id, function(json){
			if (json){
				var data = json.data;

                if (!this.application){
                    this.actions.getApplication(data.application, function(json){
                        this.application = {"name": json.data.name, "id": json.data.id};
                        if (callback) callback(data);
                    }.bind(this));
                }else{
                    if (callback) callback(data);
                }
			}
		}.bind(this));
	},

    saveDictionary: function(){
        if (this.tab.showPage){
            var dictionary = this.tab.showPage.dictionary;
            dictionary.save(function(){
                if (dictionary==this.dictionary){
                    var name = dictionary.data.name;
                    this.setTitle(MWF.CMSDD.LP.title + "-"+name);
                    this.options.desktopReload = true;
                    this.options.id = dictionary.data.id;
                }
                this.fireAppEvent("postSave")
            }.bind(this));
        }
	},
    saveDictionaryAs: function(){
        this.dictionary.saveAs();
	},
    dictionaryExplode: function(){
        this.dictionary.explode();
    },
    dictionaryImplode: function(){
        this.dictionary.implode();
    },
	//recordStatus: function(){
	//	return {"id": this.options.id};
	//},
	    dictionarySearch: function(){
        this.dictionary.loadSearch();
    },
    recordStatus: function(){
        if (this.tab){
            var openDictionarys = [];
            this.tab.pages.each(function(page){
                if (page.dictionary.data.id!=this.options.id) openDictionarys.push(page.dictionary.data.id);
            }.bind(this));
            var currentId = this.tab.showPage.dictionary.data.id;
            var status = {
                "id": this.options.id,
                "application": this.application,
                "applicationId": this.application.id || this.application,
                "openDictionarys": openDictionarys,
                "currentId": currentId,
                "options": {
                    "action": this.options.action,
                    "noCreate": this.options.noCreate,
                    "noDelete": this.options.noDelete,
                    "noModifyName": this.options.noModifyName,
                    "readMode": this.options.readMode
                }
            };
            return status;
        }
        return {"id": this.options.id, "application": this.application};
    }
});
