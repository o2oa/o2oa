// MWF.xDesktop.requireApp("Organization", "GroupExplorer", null, false);
// MWF.xDesktop.requireApp("Organization", "OrgExplorer", null, false);
MWF.xDesktop.requireApp("Org", "List", null, false);
MWF.xApplication.Org.PersonExplorer = new Class({
	//Extends: MWF.xApplication.Org.GroupExplorer,
    Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
    _loadPath: function(){
        this.path = "/x_component_Org/$Explorer/";
        this.cssPath = "/x_component_Org/$Explorer/"+this.options.style+"/css.wcss";
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
    _loadLp: function(){
        this.options.lp = {
            "elementLoaded": this.app.lp.personLoaded,
            "search": this.app.lp.search,
            "searchText": this.app.lp.searchText,
            "elementSave": this.app.lp.personSave,
            "deleteElements": this.app.lp.deletePersons,
            "deleteElementsCancel": this.app.lp.deleteElementsCancel,

            "deleteElementsTitle": this.app.lp.deletePersonsTitle,
            "deleteElementsConfirm": this.app.lp.deletePersonsConfirm,

            "elementBaseText": this.app.lp.roleBaseText,
            "elementName": this.app.lp.roleName,

            "noSignature": this.app.lp.noSignature,

            "edit": this.app.lp.edit,
            "cancel": this.app.lp.cancel,
            "save": this.app.lp.save,
            "add": this.app.lp.add
        }
    },
    clear: function(){
        this.loaddingElement = false;
        this.isElementLoaded = false;
        this.loadElementQueue = 0;
        this.listNode.empty();
    },
    load: function(){
        this._loadLp();
        this.loadLayout();
        this.loadList();
    },

    loadLayout: function(){
        this.listAreaNode = new Element("div", {"styles": this.css.listAreaNode}).inject(this.node);
        this.propertyAreaNode = new Element("div", {"styles": this.css.propertyAreaNode}).inject(this.node);

        this.resizeBarNode = new Element("div", {"styles": this.css.resizeBarNode}).inject(this.propertyAreaNode);
        this.propertyNode = new Element("div", {"styles": this.css.propertyNode}).inject(this.propertyAreaNode);

        this.propertyTitleNode = new Element("div", {"styles": this.css.propertyTitleNode}).inject(this.propertyNode);
        this.propertyContentNode = new Element("div", {"styles": this.css.propertyContentNode}).inject(this.propertyNode);

        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.listAreaNode);

        this.listScrollNode = new Element("div", {"styles": this.css.listScrollNode}).inject(this.listAreaNode);
        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.listScrollNode);

        this.loadToolbar();

        this.resizePropertyContentNode();
        this.app.addEvent("resize", function(){this.resizePropertyContentNode();}.bind(this));

        this.loadScroll();
        this.loadResize();
    },
    loadToolbar: function(){
        if (MWF.AC.isCompanyCreator() || MWF.AC.isPersonManager()) {
            this.isEditor = true;
            this.addTopElementNode = new Element("div", {"styles": this.css.addTopGroupNode}).inject(this.toolbarNode);
            this.addTopElementNode.addEvent("click", function () {
                this.addTopElement();
            }.bind(this));
        }
        this.createSearchNode();
        this.loadPingyinArea();
    },
    loadPingyinArea: function(){
        this.pingyinAction = new Element("div", {"styles": this.css.pingyinAction}).inject(this.app.pingyinArea);
        this.pingyinAction.addEvent("click", function(e){
            if (!this.pingyinNode) this.createPingyinNode();
            if (this.pingyinMorph){
                if (!this.pingyinMorph.isRunning()){
                    if (this.pingyinNode.getStyle("display")==="none"){
                        this.showPingyin();
                    }else{
                        this.hidePingyin();
                    }
                }
            }else{
                this.showPingyin();
            }
        }.bind(this));
        this.pingyinAction.addEvent("mousedown", function(e){e.stopPropagation();});

        this.hidePingyinFun = this.hidePingyin.bind(this);
        this.app.content.addEvent("mousedown", this.hidePingyinFun);
        this.app.addEvent("queryClose", function(){
            this.app.content.removeEvent("mousedown", this.hidePingyinFun);
        }.bind(this));
    },
    hidePingyin: function(){
        if (this.pingyinNode){
            if (!this.pingyinMorph){
                this.pingyinMorph = new Fx.Morph(this.pingyinNode, {duration: 50, link: "chain"});
            }
            if (!this.pingyinMorph.isRunning()){
                if (this.pingyinNode.getStyle("display")!=="none"){
                    this.pingyinMorph.start(this.css.pingyinNode).chain(function(){
                        this.pingyinNode.setStyle("display", "none");
                    }.bind(this));
                }
            }
        }
    },
    showPingyin: function(){
        this.resizePropertyContentNode();

        if (!this.pingyinMorph){
            this.pingyinMorph = new Fx.Morph(this.pingyinNode, {duration: 50, link: "chain"});
        }
        this.pingyinNode.setStyle("display", "block");
        this.pingyinMorph.start(this.css.pingyinNode_to).chain(function(){
            this.pingyinNode.setStyles(this.css.pingyinNode_to);
        }.bind(this));
    },
    setPingyinNodePosition: function(){
        this.pingyinNode.position({
            relativeTo: this.node,
            position: "leftTop",
            edge: "leftTop"
        });
    },

    createPingyinNode: function(){
        this.pingyinNode = new Element("div", {"styles": this.css.pingyinNode}).inject(this.node);
        this.pingyinNode.addEvent("mousedown", function(e){e.stopPropagation();});

        letters = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
        letters.each(function(l){
            var letterNode = new Element("div", {"styles": this.css.letterNode,"text": l}).inject(this.pingyinNode);

            letterNode.addEvents({
                "mouseover": function(e){
                    e.target.setStyles(this.css.letterNode_over);
                }.bind(this),
                "mouseout": function(e){
                    e.target.setStyles(this.css.letterNode);
                }.bind(this),
                "click": function(e){
                    this.searchInputNode.set("value", e.target.get("text"));
                    this.searchOrg();
                    this.hidePingyin();
                }.bind(this)
            });
        }.bind(this));
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

    createSearchNode: function(){
        this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.toolbarNode);

        this.searchButtonNode = new Element("div", {"styles": this.css.searchButtonNode,"title": this.options.lp.search}).inject(this.searchNode);
        this.searchButtonNode.addEvent("click", function(){this.searchOrg();}.bind(this));
        this.searchInputAreaNode = new Element("div", {"styles": this.css.searchInputAreaNode}).inject(this.searchNode);
        this.searchInputNode = new Element("input", {
            "type": "text",
            "value": this.options.lp.searchText,
            "styles": this.css.searchInputNode,
            "x-webkit-speech": "1"
        }).inject(this.searchInputAreaNode);

        var _self = this;
        this.searchInputNode.addEvents({
            "focus": function(){if (this.value ===_self.options.lp.searchText) this.set("value", "");},
            "blur": function(){if (!this.value) this.set("value", _self.options.lp.searchText);},
            "keydown": function(e){
                if (e.code===13){
                    this.searchOrg();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function(e){e.preventDefault();},
            "change": function(){
                var key = this.searchInputNode.get("value");
                if (!key || key===this.options.lp.searchText) {
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
            if (key!==this.options.lp.searchText){
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
        this.listScrollNode.setStyle("height", ""+height+"px");

        if (this.pingyinNode){
            this.setPingyinNodePosition();
            this.pingyinNode.setStyle("height", ""+size.y+"px");
        }
    },
    loadScroll: function(){
        MWF.require("MWF.widget.ScrollBar", function(){
            var _self = this;
            new MWF.widget.ScrollBar(this.listScrollNode, {
                "style":"xApp_Organization_Explorer",
                "where": "before",
                "distance": 100,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function(y){
                    var scrollSize = _self.listScrollNode.getScrollSize();
                    var clientSize = _self.listScrollNode.getSize();
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
    },
    loadResize: function(){
        this.propertyResize = new Drag(this.resizeBarNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = e.event.clientX;
                var y = e.event.clientY;
                el.store("position", {"x": x, "y": y});

                var size = this.listAreaNode.getSize();
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
                if (width<300) width = 300;
                this.listAreaNode.setStyle("width", width+1);
                this.propertyAreaNode.setStyle("margin-left", width);
            }.bind(this)
        });
    },



    getPageNodeCount: function(){
        var size = this.listScrollNode.getSize();
        return (size.y / 50).toInt() + 5;
    },
    loadList: function(){
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
                        this.loadListContent(json.data);
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
    getLastLoadedElementId: function(){
        return (this.elements.length) ? this.elements[this.elements.length-1].data.id : "";
    },
    loadListContent: function(data){
        data.each(function(itemData){
            var item = this._newElement(itemData, this);
            this.elements.push(item);
            item.load();
        }.bind(this));
    },
    setNoElementNoticeArea: function(){
        //没有数据时的提示
    },

    checkDeleteElements: function(item){
        if (this.deleteElements.length){
            if (!this.deleteElementsNode){
                this.deleteElementsNode = new Element("div", {"styles": this.css.deleteElementsNode}).inject(this.node);
                this.deleteElementsDeleteNode = new Element("div", {"styles": this.css.deleteElementsDeleteNode,"text": this.options.lp.deleteElements}).inject(this.deleteElementsNode);
                this.deleteElementsCancelNode = new Element("div", {"styles": this.css.deleteElementsCancelNode,"text": this.options.lp.deleteElementsCancel}).inject(this.deleteElementsNode);

                this.deleteElementsDeleteNode.addEvent("click", function(e){this.deleteSelectedElements(e);}.bind(this));
                this.deleteElementsCancelNode.addEvent("click", function(e){this.deleteSelectedElementsCancel(e);}.bind(this));
            }
            this.deleteElementsNode.position({
                relativeTo: (item) ? item.node : this.toolbarNode,
                position: "centerBottom",
                edge: "centerTop"
            });
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

            var complete;
            complete = function () {
                if (doCount === readyCount) {
                    if (errorText) {
                        _self.app.notice(errorText, "error", _self.propertyContentNode, {x: "left", y: "top"});
                    }
                }
            };
            _self.deleteElements.each(function(element){
                element["delete"](function(){
                    deleted.push(element);
                    doCount++;
                    if (_self.deleteElements.length===doCount){
                        _self.deleteElements = _self.deleteElements.filter(function (item) {
                            return !deleted.contains(item);
                        });
                        _self.checkDeleteElements();
                    }
                    complete();
                }, function(error){
                    errorText = (errorText) ? errorText+"<br/><br/>"+error : error;
                    doCount++;
                    if (_self.deleteElements.length !== doCount) {
                    } else {
                        _self.deleteElements = _self.deleteElements.filter(function (item) {
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
    deleteSelectedElementsCancel: function() {
        while (this.deleteElements.length){
            var element = this.deleteElements[0];
            if (element.deleteNode) element.deleteNode.click();
            if (this.currentItem!==element){
                element.contentNode.setStyles(element.style.contentNode);
                if (element.data.id) element.actionNode.fade("out");
            }
        }
        this.checkDeleteElements();
    },
    destroy: function(){
        if (this.hidePingyinFun) this.app.content.removeEvent("mousedown", this.hidePingyinFun);
        MWF.release(this);
    },




    _listElementNext: function(lastid, count, callback){
        this.actions.listPersonNext(lastid, count, function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this));
    },
    _newElement: function(data, explorer){
        return new MWF.xApplication.Org.PersonExplorer.Person(data, explorer, this.isEditor);
    },
    _listElementByKey: function(callback, failure, key){
        this.actions.listPersonByKey(function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this), failure, key);
    },
    _getAddElementData: function(){
        return {
            "employee": "",
            "password": "",
            "display": "",
            "qq": "",
            "mail": "",
            "weixin": "",
            "weibo": "",
            "mobile": "",
            "name": "",
            "controllerList": []
        };
    }
});

MWF.xApplication.Org.PersonExplorer.Item = new Class({
	//Extends: MWF.xApplication.Organization.GroupExplorer.Group,
	
	initialize: function(data, explorer, isEditor){
		this.data = data;
		this.explorer = explorer;
		this.listNode = this.explorer.listNode;
		this.initStyle();
		
		this.selectedAttributes = [];

		this.isEdit = false;
        this.isEditor = isEditor;
		this.deleteSelected = false;
	},
    initStyle: function(){
        this.style = this.explorer.css.item;
    },
    load: function(){
        this.node = new Element("div", {"styles": this.style.node}).inject(this.listNode);
        this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);

        this.iconNode = new Element("div", {"styles": this.style.iconNode}).inject(this.contentNode);
        var src = this._getIcon();
        var img = new Element("img", {
            "styles": this.style.iconImgNode,
            "src": src
        }).inject(this.iconNode);


        this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);

        this.textNode = new Element("div", {"styles": this.style.textNode}).inject(this.contentNode);
        this.textNode.set({"text": this.data.name});

        this.setNewItem();

        this.node.inject(this.listNode);

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
            if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
                this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
//		this.addNode = new Element("div", {"styles": this.style.actionAddNode}).inject(this.actionNode);
                this.deleteNode.addEvent("click", function(e){
                    if (!this.deleteSelected){
                        this.deleteNode.setStyles(this.style.actionDeleteNode_delete);
                        this.contentNode.setStyles(this.style.contentNode_delete);

                        this.explorer.deleteElements.push(this);
                        this.deleteSelected = true;

                        this.explorer.checkDeleteElements(this);
                    }else{
                        if (this.explorer.currentItem!==this){
                            this.deleteNode.setStyles(this.style.actionDeleteNode);
                            this.contentNode.setStyles(this.style.contentNode);
                        }else{
                            this.contentNode.setStyles(this.style.contentNode_selected);
                            this.textNode.setStyles(this.style.textNode_selected);
                            this.actionNode.setStyles(this.style.actionNode_selected);
                            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
                        }

                        this.explorer.deleteElements.erase(this);
                        this.deleteSelected = false;
                        this.explorer.checkDeleteElements(this);
                    }
                    e.stopPropagation();
                }.bind(this));
            }
        }
    },
    setEvent: function(){
        this.contentNode.addEvents({
            "mouseover": function(e){
                if (this.explorer.currentItem!==this && !this.deleteSelected){
                    this.contentNode.setStyles(this.style.nodeOver);
                    if (!this.deleteSelected) if (this.data.id) this.actionNode.fade("in");
                }
            }.bind(this),
            "mouseout": function(e){
                if (this.explorer.currentItem!==this && !this.deleteSelected){
                    this.contentNode.setStyles(this.style.contentNode);
                    if (!this.deleteSelected) if (this.data.id) this.actionNode.fade("out");
                }
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

    unSelected: function(){
        if (this.content.baseInfor.mode==="edit") return false;
        this.explorer.currentItem = null;
        this.contentNode.setStyles(this.style.contentNode);
        this.textNode.setStyles(this.style.textNode);
        this.actionNode.setStyles(this.style.actionNode);
        if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode);
        this.clearItemProperty();
        return true;
    },
    clearItemProperty: function(){
        this.explorer.propertyTitleNode.empty();
        if (this.content) this.content.destroy();
        this.explorer.propertyContentNode.empty();
    },
    selected: function(){
        this.explorer.currentItem = this;
        this.contentNode.setStyles(this.style.contentNode_selected);
        this.textNode.setStyles(this.style.textNode_selected);
        this.actionNode.setStyles(this.style.actionNode_selected);
        if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
        this.showItemProperty();
    },

    showItemProperty: function(){
        this.content = new MWF.xApplication.Org.PersonExplorer.PersonContent(this);
    },

    destroy: function(){
        if (this.explorer.currentItem===this){
            this.explorer.currentItem = null;
            this.clearItemProperty();
        }
        this.node.destroy();
        delete this;
    },
    "delete": function(success, failure){
        this.explorer.actions.deletePerson(this.data.id, function(){
            this.destroy();
            if (success) success();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);

            if (failure) failure();
        });
    },
    _getIcon: function(){
        var src = "data:image/png;base64,"+this.data.icon;
        if (!this.data.icon){
            if (this.data.genderType==="f"){
                src = "/x_component_Org/$Explorer/default/icon/female24.png"
            }else{
                src = "/x_component_Org/$Explorer/default/icon/man24.png"
            }
        }
        return src;
    }

});

MWF.xApplication.Org.PersonExplorer.Person = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.Item
});

MWF.xApplication.Org.PersonExplorer.ItemContent = new Class({
    initialize: function(item){
        this.item = item;
        this.data = this.item.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.explorer.propertyContentNode;
        this.style = this.item.style.person;
        this.attributes = [];
        this.load();
    },
    load: function(){
        this.explorer.propertyTitleNode.set("text", this.data.name);
        this.showItemPropertyBase();
        this.showItemPropertyTitle();

        this.listAttribute();
        this.listIdentity();

        this.showAttribute();
        //
        // if (MWF.AC.isPersonEditor({"list": this.data.controllerList})) this.showItemPropertyAttribute();
        // this.showItemPropertyIdentity();
        //
        // this.showItemcontrollerList();
    },

    destroy: function(){
        if (this.baseInfor) this.baseInfor.destroy();
        this.attributes.each(function(attribute){
            attribute.destroy();
        });
        this.contentNode.empty();
        MWF.release(this);
    },
    showItemPropertyBase: function(){
        this.baseInfor = new MWF.xApplication.Org.BaseInfor(this);
    },


    showItemPropertyTitle: function(){
        this.propertyTitleNode = new Element("div", {"styles": this.item.style.tabTitleNode}).inject(this.contentNode);
        this.propertyTabAreaNode = new Element("div", {"styles": this.item.style.tabAreaNode}).inject(this.propertyTitleNode);
        this.attributeTabNode = new Element("div", {"styles": this.item.style.tabNode, "text": this.explorer.app.lp.personAttributeText}).inject(this.propertyTabAreaNode);
        this.identityTabNode = new Element("div", {"styles": this.item.style.tabNode, "text": this.explorer.app.lp.personIdentityText}).inject(this.propertyTabAreaNode);
        this.managerTabNode = new Element("div", {"styles": this.item.style.tabNode, "text": this.explorer.app.lp.controllerListText}).inject(this.propertyTabAreaNode);

        this.attributeTabNode.addEvent("click", this.showAttribute.bind(this));
        this.identityTabNode.addEvent("click", this.showIdentity.bind(this));
        this.managerTabNode.addEvent("click", this.showManager.bind(this));
    },
    showAttribute: function(){
        this.toggleTab(this.attributeTabNode, this.attributeTabContentNode);
    },
    showIdentity: function(){
        this.toggleTab(this.identityTabNode, this.identityTabContentNode);
    },
    showManager: function(){
        this.toggleTab(this.managerTabNode, this.managerTabContentNode);
    },

    toggleTab: function(tab, content){
        this.attributeTabNode.setStyles(this.item.style.tabNode);
        this.identityTabNode.setStyles(this.item.style.tabNode);
        this.managerTabNode.setStyles(this.item.style.tabNode);
        if (this.attributeTabContentNode) this.attributeTabContentNode.setStyle("display", "none");
        if (this.identityTabContentNode) this.identityTabContentNode.setStyle("display", "none");
        if (this.managerTabContentNode) this.managerTabContentNode.setStyle("display", "none");
        tab.setStyles(this.item.style.tabNode_current);
        if (content) content.setStyle("display", "block");
    },

    listAttribute: function(){
        this.attributeTabContentNode = new Element("div", {"styles": this.item.style.tabContentNode}).inject(this.contentNode);
        this.attributeList = new MWF.xApplication.Org.List(this.attributeTabContentNode, this, {
            "action": MWF.AC.isPersonEditor({"list": this.data.controllerList}),
            "data": {
                "person": this.data.id,
                "name": "",
                "attributeList": [""]
            },
            "attr": ["name", {
                "get": function(){return this.attributeList.join(",")},
                "set": function(value){debugger;this.attributeList = value.split(/,\s*/g)}
            }]
        });
        this.attributeList.load([
            {"style": "width: 20%", "text": this.explorer.app.lp.attributeName},
            {"style": "", "text": this.explorer.app.lp.attributeValue}
        ]);
        if (this.data.id){
            this.explorer.actions.listPersonAttribute(function(json){
                json.data.each(function(item){
                    //this.attributes.push(new MWF.xApplication.Org.PersonExplorer.PersonAttribute(this.attributeTabContentNode.getElement("table").getFirst(), item, this, this.explorer.css.list));
                    this.attributeList.push(item);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
    },
    listIdentity: function(){
        this.identityTabContentNode = new Element("div", {"styles": this.item.style.tabContentNode}).inject(this.contentNode);
        this.identityList = new MWF.xApplication.Org.List(this.identityTabContentNode, this, {
            "action": false,
            "data": {
                "person": this.data.id,
                "name": "",
                "attributeList": [""]
            },
            "attr": [
                "name", "departmentName", "companyName", "duty"
                // {
                //     "get": function(){return this.attributeList.join(",")},
                //     "set": function(value){debugger;this.attributeList = value.split(/,\s*/g)}
                // }
            ]
        });
        this.identityList.load([
            {"style": "width: 20%", "text": this.explorer.app.lp.name},
            {"style": "", "text": this.explorer.app.lp.department},
            {"style": "", "text": this.explorer.app.lp.company},
            {"style": "", "text": this.explorer.app.lp.duty}
        ]);

        if (this.data.id){
            this.explorer.actions.listIdentityByPerson(function(json){
                json.data.each(function(item){
debugger;
                    this.explorer.actions.getDepartment(function(deptJson){
                        this.explorer.actions.getCompany(function(compJson){
                            item.companyName = compJson.data.name;
                        }.bind(this), null, deptJson.data.company);

                    }.bind(this), null, item.department);

                    var dutys = [];
                    this.explorer.actions.listCompanyDutyByIdentity(function(deptDutyJson){
                        debugger;

                        deptDutyJson.data.each(function(duty){
                            duty.name()

                            var text = this.dutyTextNode.get("text");
                            if (text){
                                text = text+", "+duty.name;
                            }else{
                                text = duty.name;
                            }
                            this.dutyTextNode.set({"text": text, "title": text});
                        }.bind(this));
                    }.bind(this), null, item.id);


                    this.identityList.push(item);
                    //new MWF.xApplication.Org.PersonExplorer.PersonIdentity(this.propertyIdentityContentNode, item, this, this.style);
                }.bind(this));
            }.bind(this), null, this.data.id);
        }
    }

});
MWF.xApplication.Org.PersonExplorer.PersonContent = new Class({
    Extends: MWF.xApplication.Org.PersonExplorer.ItemContent
});

MWF.xApplication.Org.BaseInfor = new Class({
    initialize: function(content){
        this.content = content
        this.item = content.item;
        this.data = this.item.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.explorer.propertyContentNode;
        this.style = this.item.style.person;
        this.attributes = [];
        this.mode = "read";
        this.load();
    },
    load: function(){
        this.baseBgNode = new Element("div", {"styles": this.style.baseBgNode}).inject(this.contentNode);
        this.baseNode = new Element("div", {"styles": this.style.baseNode}).inject(this.baseBgNode);
        this.baseInforNode = new Element("div", {"styles": this.style.baseInforNode}).inject(this.baseNode);
        this.baseInforLeftNode = new Element("div", {"styles": this.style.baseInforLeftNode}).inject(this.baseInforNode);
        this.baseInforRightNode = new Element("div", {"styles": this.style.baseInforRightNode}).inject(this.baseInforNode);
        // this.actionEditAreaNode = new Element("div", {"styles": this.style.baseInforRightActionAreaNode}).inject(this.baseInforNode);
        // this.actionEditContentNode = new Element("div", {"styles": this.style.baseInforRightActionContentNode}).inject(this.actionEditAreaNode);

        this.actionAreaNode = new Element("div", {"styles": this.style.actionAreaNode}).inject(this.baseBgNode);

        this.loadLeftInfor();
        this.loadRightInfor();

        this.loadAction();
    },
    loadAction: function(){
        //this.explorer.app.lp.edit
        if (MWF.AC.isPersonEditor({"list": this.data.controllerList})){
            this.editNode = new Element("div", {"styles": this.style.actionNode, "text": this.explorer.app.lp.edit}).inject(this.actionAreaNode);
            var actionAreas = this.baseInforRightNode.getElements("td");
            var actionArea = actionAreas[actionAreas.length-1];
            this.baseInforEditActionAreaNode = new Element("div", {"styles": this.style.baseInforEditActionAreaNode}).inject(actionArea);

            this.saveNode = new Element("div", {"styles": this.style.actionSaveNode, "text": this.explorer.app.lp.save}).inject(this.baseInforEditActionAreaNode);
            this.cancelNode = new Element("div", {"styles": this.style.actionCancelNode, "text": this.explorer.app.lp.cancel}).inject(this.baseInforEditActionAreaNode);

            this.editNode.setStyle("display", "block");
            this.editNode.addEvent("click", this.edit.bind(this));
            this.saveNode.addEvent("click", this.save.bind(this));
            this.cancelNode.addEvent("click", this.cancel.bind(this));

            this.iconNode.setStyle("cursor", "pointer");
            this.iconNode.addEvent("click", function(){this.changePersonIcon();}.bind(this));
        }
    },
    edit: function(){
        this.nameNode.empty();
        this.nameInputNode = new Element("input", {"styles": this.style.nameInputNode}).inject(this.nameNode);
        this.nameInputNode.set("value", this.data.name);

        this.signatureNode.empty();
        this.signatureTextNode = new Element("textarea", {"styles": this.style.signatureTextNode}).inject(this.signatureNode);
        this.signatureTextNode.set("value", (this.data.signature));

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        tdContents[0].empty();
        this.uniqueInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[0]);
        this.uniqueInputNode.set("value", (this.data.unique));

        tdContents[1].empty();
        this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[1]);
        this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[2].empty();
        var html = "<input name=\"personGenderRadioNode\" value=\"m\" type=\"radio\" "+((this.data.genderType==="m") ? "checked" : "")+"/>"+this.explorer.app.lp.man;
        html += "<input name=\"personGenderRadioNode\" value=\"f\" type=\"radio\" "+((this.data.genderType==="f") ? "checked" : "")+"/>"+this.explorer.app.lp.female;
        html += "<input name=\"personGenderRadioNode\" value=\"o\" type=\"radio\" "+((this.data.genderType==="d") ? "checked" : "")+"/>"+this.explorer.app.lp.other;
        tdContents[2].set("html", html);

        // this.mobileInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[2]);
        // this.mobileInputNode.set("value", (this.data.mobile));

        tdContents[3].empty();
        this.mailInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[3]);
        this.mailInputNode.set("value", (this.data.mail));

        tdContents[4].empty();
        this.employeeInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[4]);
        this.employeeInputNode.set("value", (this.data.employee));

        tdContents[5].empty();
        this.qqInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[5]);
        this.qqInputNode.set("value", (this.data.qq));

        tdContents[6].empty();
        this.displayInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[6]);
        this.displayInputNode.set("value", (this.data.display));

        tdContents[7].empty();
        this.weiboInputNode = new Element("input", {"styles": this.style.inputNode}).inject(tdContents[7]);
        this.weiboInputNode.set("value", (this.data.weibo));

        var _self = this;
        this.baseInforNode.getElements("input").addEvents({
            "focus": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_focus);}},
            "blur": function(){if (this.get("type").toLowerCase()==="text"){this.setStyles(_self.style.inputNode_blur);}}
        });
        this.baseInforNode.getElements("textarea").addEvents({
            "focus": function(){this.setStyles(_self.style.inputNode_focus);},
            "blur": function(){this.setStyles(_self.style.inputNode_blur);}
        });

        this.mode = "edit";

        this.editNode.setStyle("display", "none");
        this.saveNode.setStyle("display", "block");
        this.cancelNode.setStyle("display", "block");
    },
    changePersonIcon: function(){
        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.explorer.app.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            MWF.require("MWF.widget.ImageClipper", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.explorer.app.lp.changePersonIcon,
                    "style": "image",
                    "top": y,
                    "left": x - 20,
                    "fromTop": y,
                    "fromLeft": x - 20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.explorer.app.content,
                    "container": this.explorer.app.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                _self.uploadPersonIcon();
                                this.close();
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                _self.image = null;
                                this.close();
                            }
                        }
                    ]
                });
                dlg.show();

                this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                    "aspectRatio": 1,
                    "description" : "",
                    "imageUrl" : "",
                    "resetEnable" : false
                });
                this.image.load(this.data.icon);
            }.bind(this));
        }.bind(this))
    },
    uploadPersonIcon: function(){
        if (this.image){
            if( this.image.getResizedImage() ){
                this.explorer.actions.changePersonIcon(this.data.id ,function(){
                    this.explorer.actions.getPerson(function(json){
                        if (json.data){
                            this.data.icon = json.data.icon;
                            if (this.data.icon){
                                this.iconNode.set("src", this._getIcon());
                                this.item.iconNode.getElement("img").set("src", this.item._getIcon());
                            }
                        }
                    }.bind(this), null, this.data.id, false)
                }.bind(this), null, this.image.getFormData(), this.image.resizedImage);
            }
        }
    },
    save: function(){
        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        var gender = "";
        var radios = tdContents[2].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                gender = radios[i].value;
                break;
            }
        }

        if (!this.nameInputNode.get("value") || !this.uniqueInputNode.get("value") || !this.employeeInputNode.get("value") || !gender){
            this.explorer.app.notice(this.explorer.app.lp.inputPersonInfor, "error", this.explorer.propertyContentNode);
            return false;
        }
        if (!this.displayInputNode.get("value")) this.data.display = this.nameInputNode.get("value");
        this.baseBgNode.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });

        this.savePerson(function(){
            this.cancel();
            this.baseBgNode.unmask();
        }.bind(this), function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.explorer.app.notice("request json error: "+errorText, "error");
            this.baseBgNode.unmask();
        }.bind(this));
    },
    savePerson: function(callback, cancel){
        this.data.name = this.nameInputNode.get("value");
        this.data.employee = this.employeeInputNode.get("value");
        this.data.unique = this.uniqueInputNode.get("value");
        this.data.display = this.displayInputNode.get("value");
        this.data.mobile = this.mobileInputNode.get("value");
        this.data.mail = this.mailInputNode.get("value");
        this.data.qq = this.qqInputNode.get("value");
        //this.data.weixin = this.personWeixinInput.input.get("value");
        this.data.weibo = this.weiboInputNode.get("value")

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        var radios = tdContents[2].getElements("input");
        for (var i=0; i<radios.length; i++){
            if (radios[i].checked){
                this.data.genderType = radios[i].value;
                break;
            }
        }
        this.explorer.actions.savePerson(this.data, function(json){
            this.data.id = json.data.id;
            this.iconNode.set("src", this._getIcon());
            if (callback) callback();
        }.bind(this), function(xhr, text, error){
            if (cancel) cancel(xhr, text, error);
        }.bind(this));
    },
    cancel: function(){
        this.nameNode.set("html", this.data.name);
        this.signatureNode.set("html", this.data.signature || this.explorer.options.lp.noSignature);

        var tdContents = this.baseInforRightNode.getElements("td.inforContent");
        tdContents[0].set("html", this.data.unique || "");
        tdContents[1].set("html", this.data.mobile || "");
        tdContents[2].set("html", this.getGenderType());
        tdContents[3].set("html", this.data.mail || "");
        tdContents[4].set("html", this.data.employee || "");
        tdContents[5].set("html", this.data.qq || "");
        tdContents[6].set("html", this.data.display || "");
        tdContents[7].set("html", this.data.weibo || "");

        this.mode = "read";

        this.editNode.setStyle("display", "block");
        this.saveNode.setStyle("display", "none");
        this.cancelNode.setStyle("display", "none");
    },

    getGenderType: function(){
        var text = "";
        if (this.data.genderType){
            switch (this.data.genderType) {
                case "m":
                    text = this.explorer.app.lp.man;
                    break;
                case "f":
                    text = this.explorer.app.lp.female;
                    break;
                default:
                    text = this.explorer.app.lp.other;
            }
        }
        return text;
    },

    loadLeftInfor: function(){
        this.iconAreaNode = new Element("div", {"styles": this.style.baseInforIconAreaNode}).inject(this.baseInforLeftNode);
        this.iconNode = new Element("img", {"styles": this.style.baseInforIconNode}).inject(this.iconAreaNode);
        this.iconNode.set("src", this._getIcon());

        this.nameNode = new Element("div", {"styles": this.style.baseInforNameNode, "text": this.data.name}).inject(this.baseInforLeftNode);
        this.signatureNode = new Element("div", {"styles": this.style.baseInforSignatureNode}).inject(this.baseInforLeftNode);
        this.signatureNode.set("text", (this.data.signature || this.explorer.options.lp.noSignature ));
    },
    loadRightInfor: function(){
        // var text = "";
        // if (this.data.genderType){
        //     switch (this.data.genderType) {
        //         case "m":
        //             text = this.explorer.app.lp.man;
        //             break;
        //         case "f":
        //             text = this.explorer.app.lp.female;
        //             break;
        //         default:
        //             text = this.explorer.app.lp.other;
        //     }
        // }
        var html = "<table cellpadding='3px' cellspacing='3px'>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personUnique+"</td><td class='inforContent'>"+(this.data.unique || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personMobile+"</td><td class='inforContent'>"+(this.data.mobile || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personGender+"</td><td class='inforContent'>"+this.getGenderType()+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personMail+"</td><td class='inforContent'>"+(this.data.mail || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personEmployee+"</td><td class='inforContent'>"+(this.data.employee || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personQQ+"</td><td class='inforContent'>"+(this.data.qq || "")+"</td></tr>";
        html += "<tr><td class='inforTitle'>"+this.explorer.app.lp.personDisplay+"</td><td class='inforContent'>"+(this.data.display || "")+"</td>" +
            "<td class='inforTitle'>"+this.explorer.app.lp.personWeixin+"</td><td class='inforContent'>"+(this.data.weibo || "")+"</td></tr>";

        html += "<tr><td colspan='4' class='inforAction'></td></tr>";
        this.baseInforRightNode.set("html", html);

        this.baseInforRightNode.getElements("td.inforTitle").setStyles(this.style.baseInforRightTitleNode);
        this.baseInforRightNode.getElements("td.inforContent").setStyles(this.style.baseInforRightContentNode);
        this.baseInforRightNode.getElements("td.inforAction").setStyles(this.style.baseInforRightActionNode);
    },
    destroy: function(){
        this.baseBgNode.empty();
        this.baseBgNode.destroy();
        MWF.release(this);
    },
    _getIcon: function(){
        var src = "data:image/png;base64,"+this.data.icon;
        if (!this.data.icon){
            if (this.data.genderType==="f"){
                src = "/x_component_Org/$Explorer/default/icon/female.png"
            }else{
                src = "/x_component_Org/$Explorer/default/icon/man.png"
            }
        }
        return src;
    }
});

MWF.xApplication.Org.attribute = new Class({
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
            "styles": this.style.contentTrNode
        }).inject(this.container);

        this.selectNode = new Element("td", {
            "styles": this.style.selectNode
        }).inject(this.node);

        this.nameNode = new Element("td", {
            "styles": this.style.nameNode,
            "html": (this.data.name) ? this.data.name : "<input type='text'/>"
        }).inject(this.node);
        this.input = this.nameNode.getFirst("input");
        if (this.input) this.setEditNameInput();

        this.valueNode = new Element("td", {
            "styles": this.style.valueNode
        }).inject(this.node);

        // this.createActionNode();
        // this.setEvent();
        this.loadValue();
    },
    loadValue: function(){
        if (this.data.attributeList) this.valueNode.set("text", this.data.attributeList.join(","));
    },

    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    },







    
    createActionNode: function(){
        this.actionNode = new Element("td", {"styles": this.style.actionAttributeNode}).inject(this.node);
    },
    selectNodeClick: function(){
        if (!this.selected){
            this.selected = true;
            this.selectNode.setStyles(this.style.selectNode_selected);
            this.node.setStyles(this.style.contentNode_selected);
            this.item.selectedAttributes.push(this);
            this.item.checkDeleteAttributeAction();
        }else{
            this.selected = false;
            this.selectNode.setStyles(this.style.selectNode);
            this.node.setStyles(this.style.contentNode);
            this.item.selectedAttributes.erase(this);
            this.item.checkDeleteAttributeAction();
        }
    },
    valueNodeClick: function(){
        this.valueNode.addEvent("click", function(){
            if (!this.valueInput){
                this.valueNode.empty();
                this.valueInput = new Element("input", {"type": "text", "value": (this.data.attributeList) ? this.data.attributeList.join(",") : ""}).inject(this.valueNode);
                this.setEditValueInput();
            }
        }.bind(this));
    },
    setEditValueInput: function(){
        this.valueInput.setStyles(this.style.nameInputNode);
        this.valueInput.focus();
        this.valueInput.addEvents({
            "blur": function(){
                var value = this.valueInput.get("value");
                if (value){
                    if (value != this.data.attributeList.join(",")){
                        this.saveValue(value);
                    }else{
                        this.valueNode.empty();
                        this.valueInput = null;
                        this.valueNode.set("text", this.data.attributeList.join(","));
                    }
                }else{
                    if (!this.data.id){
                        this.node.destroy();
                        delete this;
                    }else{
                        this.valueNode.empty();
                        this.valueInput = null;
                        this.valueNode.set("text", this.data.attributeList.join(","));
                    }
                }
            }.bind(this)
        });
    },
    saveValue: function(value){
        var oldValue = this.data.attributeList;
        this.data.attributeList = value.split("/,\s*/");
        this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
            this.data.id = json.data.id;
            this.valueNode.empty();
            this.valueInput = null;
            this.valueNode.set("text", this.data.attributeList.join(","));
        }.bind(this), function(xhr, text, error){
            this.data.attributeList = oldValue;
            this.valueInput.focus();
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.item.explorer.app.notice("request json error: "+errorText, "error");
        }.bind(this));
    },

    save: function(name){
        var oldName = this.data.name;
        this.data.name = name;
        this.item.explorer.actions.saveCompanyAttribute(this.data, function(json){
            this.data.id = json.data.id;
            this.nameNode.empty();
            this.input = null;
            this.nameNode.set("text", this.data.name);
        }.bind(this), function(xhr, text, error){
            this.data.name = oldName;
            this.input.focus();
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            this.item.explorer.app.notice("request json error: "+errorText, "error");
        }.bind(this));
    },
    remove: function(){
        this.item.explorer.actions.deleteCompanyAttribute(this.data.id, function(){
            this.node.destroy();
            delete this;
        }.bind(this));
    }
});
MWF.xApplication.Org.PersonExplorer.PersonAttribute = new Class({
    Extends: MWF.xApplication.Org.attribute
});
MWF.xApplication.Org.PersonExplorer.PersonIdentity = new Class({
    Extends: MWF.xApplication.Org.attribute,

    load: function(){
        this.node = new Element("tr", {
            "styles": this.style.contentTrNode
        }).inject(this.container);

        this.selectNode = new Element("td", {
            "styles": this.style.selectNode
        }).inject(this.node);

        this.nameNode = new Element("td", {
            "styles": this.style.nameNode,
            "html": (this.data.name) ? this.data.name : "<input type='text'/>"
        }).inject(this.node);
        this.input = this.nameNode.getFirst("input");
        if (this.input) this.setEditNameInput();

        this.departmentNode = new Element("td", {
            "styles": this.style.valueNode,
            "text": this.data.departmentName
        }).inject(this.node);

        this.companyNode = new Element("td", {
            "styles": this.style.valueNode,
            "text": this.data.companyName
        }).inject(this.node);


        this.valueNode = new Element("td", {
            "styles": this.style.valueNode
        }).inject(this.node);

        // this.createActionNode();
        // this.setEvent();
        //this.loadValue();
    }
});