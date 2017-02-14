MWF.xApplication.Organization.GroupExplorer = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "lp": {

        },
        "creator": false
	},

    _loadPath: function(){
        this.path = "/x_component_Organization/$GroupExplorer/";
        this.cssPath = "/x_component_Organization/$GroupExplorer/"+this.options.style+"/css.wcss";
    },
    _loadLp: function(){
        this.options.lp = {
            "elementLoaded": this.app.lp.groupLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.groupSave,
            "deleteElements": this.app.lp.deleteGroups,

            "deleteElementsTitle": this.app.lp.deleteGroupsTitle,
            "deleteElementsConfirm": this.app.lp.deleteGroupsConfirm,

            "elementBaseText": this.app.lp.groupBaseText,
            "elementName": this.app.lp.groupName,

            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add,

            "inputElementName": this.app.lp.inputGroupName,

            "elementMemberPersonText": this.app.lp.groupMemberPersonText,

            "personEmployee": this.app.lp.personEmployee,
            "personDisplay": this.app.lp.personDisplay,
            "personMail": this.app.lp.personMail,
            "personPhone": this.app.lp.personPhone,

            "deletePersonMemberTitle": this.app.lp.deletePersonMemberTitle,
            "deletePersonMember": this.app.lp.deletePersonMember,

            "elementMemberGroupText": this.app.lp.groupMemberGroupText,

            "groupDescription": this.app.lp.groupDescription,
            "groupName": this.app.lp.groupName,

            "deleteGroupMemberTitle": this.app.lp.deleteGroupMemberTitle,
            "deleteGroupMember": this.app.lp.deleteGroupMember
        }
    },
	initialize: function(node, actions, options){
		this.setOptions(options);
		
        this._loadPath();
		this._loadCss();
		
		this.actions = actions;
		this.node = $(node);
		
		this.loaddingElement = false;
		this.elements = [];
		this.isElementLoaded = false;
		this.loadElementQueue = 0;
		
		this.deleteElements = [];
	},
	clear: function(){
		this.loaddingElement = false;
		this.isElementLoaded = false;
		this.loadElementQueue = 0;
		this.chartNode.empty();
	},
	load: function(){
        this._loadLp();
		this.loadLayout();
		this.loadChart();
	},
	loadLayout: function(){
		this.chartAreaNode = new Element("div", {"styles": this.css.chartAreaNode}).inject(this.node);
		this.propertyAreaNode = new Element("div", {"styles": this.css.propertyAreaNode}).inject(this.node);
		
		this.resizeBarNode = new Element("div", {"styles": this.css.resizeBarNode}).inject(this.propertyAreaNode);
		this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.propertyAreaNode);
		
		this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
		this.propertyContentNode = new Element("div", {"styles": this.css.propertyContentNode}).inject(this.propertyNode);
		
		this.loadToolbar();
		this.chartScrollNode = new Element("div", {"styles": this.css.chartScrollNode}).inject(this.chartAreaNode);
		this.chartNode = new Element("div", {"styles": this.css.chartNode}).inject(this.chartScrollNode);
		
		this.resizePropertyContentNode();
		this.app.addEvent("resize", function(){this.resizePropertyContentNode();}.bind(this));
		
		MWF.require("MWF.widget.ScrollBar", function(){
			var _self = this;
			new MWF.widget.ScrollBar(this.chartScrollNode, {
				"style":"xApp_Organization_Explorer", 
				"where": "before", 
				"distance": 100, 
				"friction": 4,	
				"axis": {"x": false, "y": true},
				"onScroll": function(y){
					var scrollSize = _self.chartScrollNode.getScrollSize();
					var clientSize = _self.chartScrollNode.getSize();
					var scrollHeight = scrollSize.y-clientSize.y;
					if (y+200>scrollHeight) {
						if (!_self.isElementLoaded) _self.loadElements();
					}
				}
			});
			new MWF.widget.ScrollBar(this.propertyContentNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			});
		}.bind(this));
		
		this.propertyResize = new Drag(this.resizeBarNode,{
			"snap": 1,
			"onStart": function(el, e){
				var x = e.event.clientX;
				var y = e.event.clientY;
				el.store("position", {"x": x, "y": y});
				
				var size = this.chartAreaNode.getSize();
				el.store("initialWidth", size.x);
			}.bind(this),
			"onDrag": function(el, e){
				var x = e.event.clientX;
//				var y = e.event.y;
				var bodySize = this.node.getSize();
				var position = el.retrieve("position");
				var initialWidth = el.retrieve("initialWidth").toFloat();
				var dx = position.x.toFloat()-x.toFloat();
				
				var width = initialWidth-dx;
				if (width> bodySize.x/1.5) width = bodySize.x/1.5;
				if (width<400) width = 400;
				this.chartAreaNode.setStyle("width", width+1);
				this.propertyAreaNode.setStyle("margin-left", width);
			}.bind(this)
		});
	},
	
	getPageNodeCount: function(){
		var size = this.chartScrollNode.getSize();
		count = (size.y/46).toInt()+5;
		return count;
	},
	getLastLoadedElementId: function(){
		return (this.elements.length) ? this.elements[this.elements.length-1].data.id : "";
	},
	
	loadChart: function(){
		this.loadElements();
		this.app.addEvent("resize", function(){
			if (this.elements.length<this.getPageNodeCount()){
				this.loadElements(true);
			}
		}.bind(this));
	},

	loadElements: function(addToNext){
		if (!this.isElementLoaded){
			if (!this.loaddingElement){
				this.loaddingElement = true;
                var count = this.getPageNodeCount();
				this._listElementNext(this.getLastLoadedElementId(), count, function(json){
					if (json.data.length){
						this.loadChartContent(json.data);
						this.loaddingElement = false;
						
						if (json.data.length<count){
							this.isElementLoaded = true;
							this.app.notice(this.options.lp.elementLoaded, "ok", this.chartScrollNode, {"x": "center", "y": "bottom"});
						}else{
							if (this.loadElementQueue>0){
								this.loadElementQueue--;
								this.loadElements();
							}
						}
					}else{
						if (!this.elements.length){
							this.setNoGroupNoticeArea();
						}else{
							this.app.notice(this.options.lp.elementLoaded, "ok", this.chartScrollNode, {"x": "center", "y": "bottom"});
						}
						this.isElementLoaded = true;
						this.loaddingElement = false;
					}
					
				}.bind(this));
			}else{
				if (addToNext) this.loadElementQueue++;
			}
		}
	},
    setNoGroupNoticeArea: function(){},
	loadChartContent: function(data){
		data.each(function(itemData){
			var item = this._newElement(itemData, this);
			this.elements.push(item);
			item.load();
		}.bind(this));
	},
	
	resizePropertyContentNode: function(){
		var size = this.node.getSize();
		var tSize = this.propertyTitleNode.getSize();
		var mtt = this.propertyTitleNode.getStyle("margin-top").toFloat();
		var mbt = this.propertyTitleNode.getStyle("margin-bottom").toFloat();
		var mtc = this.propertyContentNode.getStyle("margin-top").toFloat();
		var mbc = this.propertyContentNode.getStyle("margin-bottom").toFloat();		
		var height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.propertyContentNode.setStyle("height", height);
		
		tSize = this.toolbarNode.getSize();
		mtt = this.toolbarNode.getStyle("margin-top").toFloat();
		mbt = this.toolbarNode.getStyle("margin-bottom").toFloat();
		mtc = this.toolbarNode.getStyle("margin-top").toFloat();
		mbc = this.toolbarNode.getStyle("margin-bottom").toFloat();		
		height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.chartScrollNode.setStyle("height", height);
	},
	
	loadToolbar: function(){
		this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.chartAreaNode);
        if (MWF.AC.isGroupCreator()){
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function(){
                this.addTopElement();
            }.bind(this));
        }
		this.createSearchNode();
	},
	createSearchNode: function(){
		this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.toolbarNode);
		
		this.searchButtonNode = new Element("div", {
			"styles": this.css.searchButtonNode,
			"title": this.options.lp.search
		}).inject(this.searchNode);
		
		this.searchButtonNode.addEvent("click", function(){
			this.searchOrg();
		}.bind(this));
		
		this.searchInputAreaNode = new Element("div", {
			"styles": this.css.searchInputAreaNode
		}).inject(this.searchNode);
		
		this.searchInputBoxNode = new Element("div", {
			"styles": this.css.searchInputBoxNode
		}).inject(this.searchInputAreaNode);
		
		this.searchInputNode = new Element("input", {
			"type": "text",
			"value": this.options.lp.searchText,
			"styles": this.css.searchInputNode,
			"x-webkit-speech": "1"
		}).inject(this.searchInputBoxNode);
		var _self = this;
		this.searchInputNode.addEvents({
			"focus": function(){
				if (this.value==_self.options.lp.searchText) this.set("value", "");
			},
			"blur": function(){if (!this.value) this.set("value", _self.options.lp.searchText);},
			"keydown": function(e){
				if (e.code==13){
					this.searchOrg();
					e.preventDefault();
				}
			}.bind(this),
			"selectstart": function(e){
				e.preventDefault();
			},
			"change": function(){
				var key = this.searchInputNode.get("value");
				if (!key || key==this.options.lp.searchText) {
					if (this.currentItem){
						if (this.currentItem.unSelected()){
							this.clear();
							this.loadElements();
						}else{
							this.app.notice(this.options.lp.elementSave, "error", this.propertyContentNode);
						}
					} 
				}
			}.bind(this)
		});
		this.searchButtonNode.addEvent("click", function(){this.searchOrg();}.bind(this));
	},

	searchOrg: function(){
		var key = this.searchInputNode.get("value");
		if (key){
			if (key!=this.options.lp.searchText){
				var isSearchElement = true;
				if (this.currentItem) isSearchElement = this.currentItem.unSelected();
				if (isSearchElement){
					this._listElementByKey(function(json){
						if (this.currentItem) this.currentItem.unSelected();
						this.clear();
						json.data.each(function(itemData){
							var item = this._newElement(itemData, this);
							item.load();
						}.bind(this));
					}.bind(this), null, key);
				}else{
					this.app.notice(this.options.lp.elementSave, "error", this.propertyContentNode);
				}
			}else{
				if (this.currentItem) isSearchElement = this.currentItem.unSelected();
				if (isSearchElement){
					this.clear();
					this.loadElements();
				}else{
					this.app.notice(this.options.lp.elementSave, "error", this.propertyContentNode);
				}
			}
		}else{
			if (this.currentItem) isSearchElement = this.currentItem.unSelected();
			if (isSearchElement){
				this.clear();
				this.loadElements();
			}else{
				this.app.notice(this.options.lp.elementSave, "error", this.propertyContentNode);
			}
		}
	},

	addTopElement: function(){
		var isNewElement = true;
		if (this.currentItem) isNewElement = this.currentItem.unSelected();
		if (isNewElement){
			var newElementData = this._getAddElementData();
			var item = this._newElement(newElementData, this);
			item.load();
			item.selected();
			item.editBaseInfor();
			
			(new Fx.Scroll(this.chartScrollNode)).toElementCenter(item.node);
		}else{
			this.app.notice(this.options.lp.elementSave, "error", this.propertyContentNode);
		}
	},
	checkDeleteElements: function(){
		if (this.deleteElements.length){
			if (!this.deleteElementsNode){
				this.deleteElementsNode = new Element("div", {
					"styles": this.css.deleteGroupsNode,
					"text": this.options.lp.deleteElements
				}).inject(this.node);
				this.deleteElementsNode.position({
					relativeTo: this.chartScrollNode,
				    position: "centerTop",
				    edge: "centerTop"
				});
				this.deleteElementsNode.addEvent("click", function(e){
					this.deleteSelectedElements(e);
				}.bind(this));
			}
		}else{
			if (this.deleteElementsNode){
				this.deleteElementsNode.destroy();
				this.deleteElementsNode = null;
				delete this.deleteElementsNode;
			}
		}
	},
	deleteSelectedElements: function(e){
		var _self = this;
		this.app.confirm("infor", e, this.options.lp.deleteElementsTitle, this.options.lp.deleteElementsConfirm, 300, 120, function(){
			var deleted = [];
			var doCount = 0;
            var readyCount = _self.deleteElements.length;
            var errorText = "";

            var complete = function(){
                if (doCount == readyCount){
                    if (errorText){
                        _self.app.notice(errorText, "error", _self.propertyContentNode, {x: "left", y:"top"});
                    }
                }
            }
			_self.deleteElements.each(function(group){
				group["delete"](function(){
					deleted.push(group);
					doCount++;
					if (_self.deleteElements.length==doCount){
						_self.deleteElements = _self.deleteElements.filter(function(item, index){
							return !deleted.contains(item);
						});
						_self.checkDeleteElements();
					}
                    complete();
				}, function(error){
                    errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
					doCount++;
					if (_self.deleteElements.length==doCount){
						_self.deleteElements = _self.deleteElements.filter(function(item, index){
							return !deleted.contains(item);
						});
						_self.checkDeleteElements();
					}
                    complete();
				});
			});
			this.close();
		}, function(){
			this.close();
		});
	},
    _listElementNext: function(lastid, count, callback){
        this.actions.listGroupNext(lastid, count, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Organization.GroupExplorer.Group(data, explorer, this.isEditor);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listGroupByKey(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, key);
    },
    _getAddElementData: function(){
        return {
            "personList": [],
            "groupList": [],
            "id": "",
            "name": ""
        };
    }
	
});

MWF.xApplication.Organization.GroupExplorer.Group = new Class({
	initialize: function(data, explorer, isEditor){
		this.data = data;
		if (this.data.personList) this.data.personList = data.personList.filter(function(item){return item;});
		if (this.data.groupList) this.data.groupList = data.groupList.filter(function(item){return item;});
		this.explorer = explorer;
		this.chartNode = this.explorer.chartNode;
		this.initStyle();
		this.selectedPersons = [];
		this.selectedGroups = [];
		this.isEdit = false;
        this.isEditor = isEditor;
		this.deleteSelected = false;
	},
	initStyle: function(){
		this.style = this.explorer.css.groupItem;
	},
	load: function(){
		this.node = new Element("div", {"styles": this.style.node}).inject(this.chartNode);
		this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);
		this.childNode = new Element("div", {"styles": this.style.childNode}).inject(this.node);
		
		this.flagNode = new Element("div", {"styles": this.style.flagNode}).inject(this.contentNode);
		this.iconNode = new Element("div", {"styles": this.style.iconNode}).inject(this.contentNode);
		this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);
		
		this.textNode = new Element("div", {"styles": this.style.textNode}).inject(this.contentNode);
		this.textNode.set({
			"text": this.data.name
		});

        this.setNewItem();

		this.node.inject(this.chartNode);
		
		this.addActions();
		this.setEvent();
	},

    setNewItem: function(){
        if (!this.created){
            if (!this.data.id){
                this.created = false;
                this.contentNode.setStyles(this.style.contentNodeNew);
            }else {
                this.created = true;
                this.contentNode.setStyles(this.style.contentNode);
            }
        }
    },
	addActions: function(){
        if (this.isEditor){
            if (MWF.AC.isGroupCreator()){
                this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
//		this.addNode = new Element("div", {"styles": this.style.actionAddNode}).inject(this.actionNode);
                this.deleteNode.addEvent("click", function(e){
                    if (!this.deleteSelected){
                        this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
                        this.contentNode.setStyles(this.style.contentNode_selected);

                        this.explorer.deleteElements.push(this);
                        this.deleteSelected = true;

                        this.explorer.checkDeleteElements();
                    }else{
                        this.deleteNode.setStyles(this.style.actionDeleteNode);
                        this.contentNode.setStyles(this.style.contentNode);

                        this.explorer.deleteElements.erase(this);
                        this.deleteSelected = false;
                        this.explorer.checkDeleteElements();
                    }
                    e.stopPropagation();
                }.bind(this));
            }
        }
	},
	setEvent: function(){
		this.contentNode.addEvents({
			"mouseover": function(e){
				if (this.explorer.currentItem!=this){
					this.flagNode.setStyles(this.style.flagNodeOver);
				}
				if (!this.deleteSelected) if (this.data.id) this.actionNode.fade("in");
			}.bind(this),
			"mouseout": function(e){
				if (this.explorer.currentItem!=this){
					this.flagNode.setStyles(this.style.flagNode);
				}
				if (!this.deleteSelected) if (this.data.id) this.actionNode.fade("out");
			}.bind(this),
			"click": function(e){
				if (this.explorer.currentItem){
					if (this.explorer.currentItem.unSelected()){
						this.selected();
					}else{
						this.explorer.app.notice(this.explorer.options.lp.elementSave, "error", this.propertyContentNode);
					}
				}else{
					this.selected();
				}
			}.bind(this)
		});
	},
	selected: function(){
		this.explorer.currentItem = this;
		this.flagNode.setStyles(this.style.flagNodeSelected);
		this.showItemProperty();
	},
	unSelected: function(){
		if (this.isEdit) return false;
		this.explorer.currentItem = null;
		this.flagNode.setStyles(this.style.flagNode);
		this.clearItemProperty();
		return true;
	},
	showItemProperty: function(){
		this.explorer.propertyTitleNode.set("text", this.data.name);
		this.showItemPropertyBase();
		this.showItemPropertyPerson();
		this.showItemPropertyGroup();
//		this.showItemPropertyBase();
//		this.showItemPropertyDuty();
//		this.showItemPropertyAttribute();
	},
	clearItemProperty: function(){
		this.explorer.propertyTitleNode.empty();
		this.explorer.propertyContentNode.empty();
	},
	showItemPropertyBase: function(){
		this.propertyBaseNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.baseActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyBaseNode);
		this.propertyBaseTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.options.lp.elementBaseText
		}).inject(this.propertyBaseNode);
		
		if (this.isEditor) this.createEditBaseNode();
		
		this.propertyBaseContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyBaseNode);
		
		var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center'>";
		html += "<tr><td class='formTitle'>"+this.explorer.options.lp.elementName+"</td><td id='formGroupName'></td></tr>";
		html += "</table>";
		this.propertyBaseContentNode.set("html", html);
		this.propertyBaseContentNode.getElements("td.formTitle").setStyles(this.style.propertyBaseContentTdTitle);
		
		this.elementNameInput = new MWF.xApplication.Organization.GroupExplorer.Input(this.propertyBaseContentNode.getElement("#formGroupName"), this.data.name, this.explorer.css.formInput);
	},
	createEditBaseNode: function(){
        if (MWF.AC.isGroupCreator()){
            this.editBaseNode = new Element("button", {
                "styles": this.style.editBaseNode,
                "text": this.explorer.options.lp.edit,
                "events": {"click": this.editBaseInfor.bind(this)}
            }).inject(this.baseActionNode);
        }
	},
	createCancelBaseNode: function(){
		this.cancelBaseNode = new Element("button", {
			"styles": this.style.cancelBaseNode,
			"text": this.explorer.options.lp.cancel,
			"events": {"click": this.cancelBaseInfor.bind(this)}
		}).inject(this.baseActionNode);
	},
	createSaveBaseNode: function(){
		this.saveBaseNode = new Element("button", {
			"styles": this.style.saveBaseNode,
			"text": this.explorer.options.lp.save,
			"events": {"click": this.saveBaseInfor.bind(this)}
		}).inject(this.baseActionNode);
	},
	editBaseInfor: function(){
		this.baseActionNode.empty();
		this.editBaseNode = null;
		this.createCancelBaseNode();
		this.createSaveBaseNode();
		
		this.editMode();
	},
	cancelBaseInfor: function(){
		if (this.data.name){
			this.baseActionNode.empty();
			this.cancelBaseNode = null;
			this.saveBaseNode = null;
			this.createEditBaseNode();
			
			this.readMode();
		}else{
			this.destroy();
		}
	},
	saveBaseInfor: function(callback){
		if (!this.elementNameInput.input.get("value")){
			this.explorer.app.notice(this.explorer.options.lp.inputElementName, "error", this.explorer.propertyContentNode);
			return false;
		}
		this.propertyBaseNode.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		this.save(function(){
			this.baseActionNode.empty();
			this.cancelBaseNode = null;
			this.saveBaseNode = null;
			this.createEditBaseNode();
			
			this.readMode();
            this.setNewItem();
			
			this.propertyBaseNode.unmask();
            if (callback) callback();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
			this.explorer.app.notice("request json error: "+errorText, "error");
			this.propertyBaseNode.unmask();
		}.bind(this));
	},
	editMode: function(){
		this.elementNameInput.editMode();
		this.isEdit = true;
	},
	readMode: function(){
		this.elementNameInput.readMode();
		this.isEdit = false;
	},

	save: function(callback, cancel){
		this.data.name = this.elementNameInput.input.get("value");
		
		this._saveElement(this.data, function(json){
            this.data.id = json.data.id;
			this.textNode.set("text", this.data.name);
			this.elementNameInput.save();
			
			if (callback) callback();
		}.bind(this), function(xhr, text, error){
			if (cancel) cancel(xhr, text, error);
		}.bind(this));
	},
	
	showItemPropertyPerson: function(){
		this.propertyPersonNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.personActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyPersonNode);
		this.propertyPersonTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.options.lp.elementMemberPersonText
		}).inject(this.propertyPersonNode);
	//	this.createEditBaseNode();
		
		this.propertyPersonContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyPersonNode);

        if (this.isEditor){
            if (MWF.AC.isGroupCreator()){
                this.createDeletePersonNode();
                this.createAddPersonNode();
            }
        }
		this.listPerson();
	},
	createAddPersonNode: function(){
		this.addPersonNode = new Element("button", {
			"styles": this.style.addActionNode,
			"text": this.explorer.options.lp.add,
			"events": {"click": this.addPerson.bind(this)}
		}).inject(this.personActionNode);
	},
	createDeletePersonNode: function(){
		this.deletePersonNode = new Element("button", {
			"styles": this.style.deleteActionNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.personActionNode);
	},
    checkSaveBaseInfor: function(callback){
        if (!this.created){
            this.saveBaseInfor(function(){
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
	addPerson: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Organization", "Selector.Person", function(){
                var selector = new MWF.xApplication.Organization.Selector.Person(this.explorer.app.content,{
                    "values": this.data.personList,
                    "onComplete": function(items){
                        var ids = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                        });
                        this.data.personList = ids;

                        this._saveElement(this.data, function(){
                            this.listPerson();
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
	},
	listPerson: function(){
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='border-right: 1px solid #FFF'>"+this.explorer.options.lp.personEmployee+"</th>";
		html += "<th style='border-right: 1px solid #FFF'>"+this.explorer.options.lp.personDisplay+"</th>";
		html += "<th style='border-right: 1px solid #FFF'>"+this.explorer.options.lp.personMail+"</th>";
		html += "<th>"+this.explorer.options.lp.personPhone+"</th></tr>";
		html += "</table>";
		
		this.propertyPersonContentNode.set("html", html);
		this.propertyPersonContentNode.getElements("th").setStyles(this.style.propertyContentTdTitle);
		
		this.data.personList.each(function(id){
			this.explorer.actions.getPerson(function(json){
				new MWF.xApplication.Organization.GroupExplorer.PersonMember(this.propertyPersonContentNode.getElement("table").getFirst(), json.data, this, this.explorer.css.map);
			}.bind(this), null, id, false);
		}.bind(this));
//		this.explorer.actions.listCompanyDuty(function(json){
//			json.data.each(function(item){
//				new MWF.xApplication.Organization.CompanyDuty(this.propertyDutyContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
//			}.bind(this));
//		}.bind(this), null, this.data.id);
	},
	checkDeletePersonAction: function(){

		if (this.selectedPersons.length){
			if (this.deletePersonNode.get("disable")){
				this.deletePersonNode.set({
					"styles": this.style.deleteActionNode
				});
				this.deletePersonNode.removeProperty("disable");
				this.deletePersonNode.addEvent("click", function(e){this.deletePerson(e);}.bind(this));
			}
		}else{
			if (!this.deletePersonNode.get("disable")){
				this.deletePersonNode.set({
					"styles": this.style.deleteActionNode_desable,
					"disable": true
				});
				this.deletePersonNode.removeEvents("click");
			}
		}
	},
	deletePerson: function(e){
		var _self = this;
		this.explorer.app.confirm("infor", e, this.explorer.options.lp.deletePersonMemberTitle, this.explorer.options.lp.deletePersonMember, 300, 120, function(){
			var deleteIds = [];
			_self.selectedPersons.each(function(item){
				this.data.personList = this.data.personList.erase(item.data.id);
			}.bind(_self));
			_self._saveElement(_self.data, function(){
				this.listPerson();
			}.bind(_self));
			this.close();
		}, function(){
			this.close();
		});
	},
	
	showItemPropertyGroup: function(){
		this.propertyGroupNode = new Element("div", {
			"styles": this.style.propertyInforNode
		}).inject(this.explorer.propertyContentNode);
		
		this.groupActionNode = new Element("div", {
			"styles": this.style.propertyInforActionNode
		}).inject(this.propertyGroupNode);
		this.propertyGroupTextNode = new Element("div", {
			"styles": this.style.propertyInforTextNode,
			"text": this.explorer.options.lp.elementMemberGroupText
		}).inject(this.propertyGroupNode);
	//	this.createEditBaseNode();
		
		this.propertyGroupContentNode = new Element("div", {
			"styles": this.style.propertyInforContentNode
		}).inject(this.propertyGroupNode);

        if (this.isEditor) {
            if (MWF.AC.isGroupCreator()){
                this.createDeleteGroupNode();
                this.createAddGroupNode();
            }
        }
		this.listGroup();
	},
	createAddGroupNode: function(){
		this.addGroupNode = new Element("button", {
			"styles": this.style.addActionNode,
			"text": this.explorer.options.lp.add,
			"events": {"click": this.addGroup.bind(this)}
		}).inject(this.groupActionNode);
	},
	createDeleteGroupNode: function(){
		this.deleteGroupNode = new Element("button", {
			"styles": this.style.deleteActionNode_desable,
			"text": this.explorer.app.lp["delete"],
			"disable": true
		}).inject(this.groupActionNode);
	},
	addGroup: function(){
        this.checkSaveBaseInfor(function(){
            MWF.xDesktop.requireApp("Organization", "Selector.Group", function(){
                var selector = new MWF.xApplication.Organization.Selector.Group(this.explorer.app.content,{
                    "values": this.data.groupList,
                    "onComplete": function(items){
                        var ids = [];
                        items.each(function(item){
                            ids.push(item.data.id);
                        });
                        this.data.groupList = ids;

                        this._saveElement(this.data, function(){
                            this.listGroup();
                        }.bind(this));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));
	},
	listGroup: function(){
		var html = "<table cellspacing='0' cellpadding='5' border='0' width='95%' align='center' style='line-height:normal'>";
		html += "<tr><th style='width:20px'></th>";
		html += "<th style='width:25%; border-right: 1px solid #FFF'>"+this.explorer.options.lp.groupName+"</th>";
		html += "<th style='border-right: 1px solid #FFF'>"+this.explorer.options.lp.groupDescription+"</th>";
		html += "</table>";
		
		this.propertyGroupContentNode.set("html", html);
		this.propertyGroupContentNode.getElements("th").setStyles(this.style.propertyContentTdTitle);
		
		this.data.groupList.each(function(id){
			this.explorer.actions.getGroup(function(json){
				new MWF.xApplication.Organization.GroupExplorer.GroupMember(this.propertyGroupContentNode.getElement("table").getFirst(), json.data, this, this.explorer.css.map);
			}.bind(this), null, id);
		}.bind(this));
//		this.explorer.actions.listCompanyDuty(function(json){
//			json.data.each(function(item){
//				new MWF.xApplication.Organization.CompanyDuty(this.propertyDutyContentNode.getElement("table").getFirst(), item, this, this.explorer.css.map);
//			}.bind(this));
//		}.bind(this), null, this.data.id);
	},
	checkDeleteGroupAction: function(){

		if (this.selectedGroups.length){
			if (this.deleteGroupNode.get("disable")){
				this.deleteGroupNode.set({
					"styles": this.style.deleteActionNode
				});
				this.deleteGroupNode.removeProperty("disable");
				this.deleteGroupNode.addEvent("click", function(e){this.deleteGroupMember(e);}.bind(this));
			}
		}else{
			if (!this.deleteGroupNode.get("disable")){
				this.deleteGroupNode.set({
					"styles": this.style.deleteActionNode_desable,
					"disable": true
				});
				this.deleteGroupNode.removeEvents("click");
			}
		}
	},
	destroy: function(){
		this.explorer.currentItem = null;
		this.clearItemProperty();
		this.node.destroy();
		delete this;
	},
	"delete": function(success, failure){
		this._deleteElement(this.data.id, function(){
			this.destroy();
			if (success) success();
		}.bind(this), function(xhr, text, error){
			var errorText = error;
			if (xhr) errorText = xhr.responseText;
		//	this.explorer.app.notice(errorText, "error", this.explorer.propertyContentNode, {x: "left", y:"top"});
			
			if (failure) failure(errorText);
        }.bind(this));
	},
	deleteGroupMember: function(e){
		var _self = this;
		this.explorer.app.confirm("infor", e, this.explorer.options.lp.deleteGroupMemberTitle, this.explorer.options.lp.deleteGroupMember, 300, 120, function(){
			var deleteIds = [];
			_self.selectedGroups.each(function(item){
				this.data.groupList = this.data.groupList.erase(item.data.id);
			}.bind(_self));
			_self._saveElement(_self.data, function(){
				this.listGroup();
			}.bind(_self));
			this.close();
		}, function(){
			this.close();
		});
	},
    _saveElement: function(data, success, failure){
        this.explorer.actions.saveGroup(data, success, failure);
    },
    _deleteElement: function(id, success, failure){
        var errorText = "";
        var deleteFlag = true;
        this.explorer.actions.listRoleByGroup(function(json){
            if (json.data.length){
                deleteFlag = false;
                var name = "";
                json.data.each(function(data){name = (name) ? name+", "+data.name : data.name;});
                errorText = "“"+this.data.name+"”"+this.explorer.app.lp.deleteGroupError_InRole+"“"+name+"”";
            }
        }.bind(this), null, id,false);

        this.explorer.actions.listSupGroupDirect(function(json){
            if (json.data.length){
                deleteFlag = false;
                var name = "";
                json.data.each(function(data){name = (name) ? name+", "+data.name : data.name;});
                errorText = (errorText) ? errorText+"<br/>"+"“"+this.data.name+"”"+this.explorer.app.lp.deleteGroupError_InGroup+"“"+name+"”" : this.explorer.app.lp.deleteGroupError_InGroup+"“"+name+"”";
            }
        }.bind(this), null, id,false);

        if (deleteFlag){
            this.explorer.actions.deleteGroup(id, success, failure);
        }else{
            if (failure) failure(null, null, errorText);
        };
    }
});

MWF.xApplication.Organization.GroupExplorer.PersonMember = new Class({
	initialize: function(container, data, item, style){
		this.container = $(container);
		this.data = data;
		this.style = style;
		this.item = item;
		this.selected = false;
		this.load();
	},
	load: function(){
		this.node = new Element("tr", {
			"styles": this.style.contentNode
		}).inject(this.container);
		
		this.selectNode = new Element("td", {
			"styles": this.style.selectNode
		}).inject(this.node);
		
		this.employeeNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.employee || ""
		}).inject(this.node);
		
		this.displayNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.display
		}).inject(this.node);
		
		this.mailNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.mail || ""
		}).inject(this.node);
		
		this.phoneNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.mobile || ""
		}).inject(this.node);
		
		this.setEvent();
	},
	setEvent: function(){
		this.selectNode.addEvent("click", function(){
			this.selectNodeClick();
		}.bind(this));
	},
	selectNodeClick: function(){
		if (!this.selected){
			this.selected = true;
			this.selectNode.setStyles(this.style.selectNode_selected);
			this.node.setStyles(this.style.contentNode_selected);
			this.item.selectedPersons.push(this);
			this.item.checkDeletePersonAction();
		}else{
			this.selected = false;
			this.selectNode.setStyles(this.style.selectNode);
			this.node.setStyles(this.style.contentNode);
			this.item.selectedPersons.erase(this);
			this.item.checkDeletePersonAction();
		}
	}
	
});
MWF.xApplication.Organization.GroupExplorer.GroupMember = new Class({
	initialize: function(container, data, item, style){
		this.container = $(container);
		this.data = data;
		this.style = style;
		this.item = item;
		this.selected = false;
		this.load();
	},
	load: function(){
		this.node = new Element("tr", {
			"styles": this.style.contentNode
		}).inject(this.container);
		
		this.selectNode = new Element("td", {
			"styles": this.style.selectNode
		}).inject(this.node);
		
		this.nameNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.name || ""
		}).inject(this.node);
		
		this.descriptionNode = new Element("td", {
			"styles": this.style.valueNode,
			"text": this.data.description || ""
		}).inject(this.node);
		
		this.setEvent();
	},
	setEvent: function(){
		this.selectNode.addEvent("click", function(){
			this.selectNodeClick();
		}.bind(this));
	},
	selectNodeClick: function(){
		if (!this.selected){
			this.selected = true;
			this.selectNode.setStyles(this.style.selectNode_selected);
			this.node.setStyles(this.style.contentNode_selected);
			this.item.selectedGroups.push(this);
			this.item.checkDeleteGroupAction();
		}else{
			this.selected = false;
			this.selectNode.setStyles(this.style.selectNode);
			this.node.setStyles(this.style.contentNode);
			this.item.selectedGroups.erase(this);
			this.item.checkDeleteGroupAction();
		}
	}
	
});

MWF.xApplication.Organization.GroupExplorer.Input = new Class({
	Implements: [Events],
	initialize: function(node, value, style, display){
		this.node = $(node);
		this.value = value || "";
		this.style = style;
        this.display = display;
		this.load();
	},
	load: function(){
		this.content = new Element("div", {
			"styles": this.style.content,
			"text": (this.display=="none") ? "***" : this.value
		}).inject(this.node);
	},
	editMode: function(){
		this.content.empty();
		this.input = new Element("input",{
			"styles": this.style.input,
			"value": this.value
		}).inject(this.content);
		
		this.input.addEvents({
			"focus": function(){
				this.input.setStyles(this.style.input_focus);
			}.bind(this),
			"blur": function(){
				this.input.setStyles(this.style.input);
			}.bind(this)
		});
		
	},
	readMode: function(){
		this.content.empty();
		this.input = null;
		this.content.set("text", this.value);
        if (this.display=="none") this.content.set("text", "***");
	},
	save: function(){
		if (this.input) this.value = this.input.get("value");
		return this.value;
	}
});