MWF.require("MWF.widget.Tab", null, false);
MWF.xDesktop.requireApp("Org", "List", null, false);
MWF.xApplication.Org.$Explorer = new Class({
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

            "elementBaseText": this.app.lp.personBaseText,
            "elementName": this.app.lp.personName,

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
        this.propertyContentNode.setStyle("-webkit-user-select", "text");
        this.node.addEvent("selectstart", function(e){
            this.propertyContentNode.setStyle("-webkit-user-select", "text");
        }.bind(this));


        this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode}).inject(this.listAreaNode);

        this.listScrollNode = new Element("div", {"styles": this.css.listScrollNode}).inject(this.listAreaNode);
        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.listScrollNode);

        this.loadToolbar();

        this.resizePropertyContentNodeFun =  this.resizePropertyContentNode.bind(this);
        this.resizePropertyContentNodeFun();
        this.app.addEvent("resize", this.resizePropertyContentNodeFun);

        this.loadScroll();
        this.loadResize();
    },
    loadToolbar: function(){
        if (this._isActionManager()) {
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
        this.removePingyinFun = this.removePingyin.bind(this);
        this.app.addEvent("queryClose", this.removePingyinFun);
    },
    removePingyin: function(){
        this.app.content.removeEvent("mousedown", this.hidePingyinFun);
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

            (new Fx.Scroll(this.listScrollNode)).toElementCenter(item.node);
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
            //"x-webkit-speech": "1",
            "x-webkit-speech": "x-webkit-speech"
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
            // new MWF.widget.ScrollBar(this.propertyContentNode, {
            //     "style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
            // });
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
        this.continueLoadFun = this.continueLoad.bind(this);
        this.app.addEvent("resize", this.continueLoadFun);
    },
    continueLoad: function(){
        if (this.elements.length<this.getPageNodeCount()){
            this.loadElements(true);
        }
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
                            this.app.notice(this.options.lp.elementLoaded, "ok", this.listScrollNode, {"x": "center", "y": "bottom"});
                        }else{
                            if (this.loadElementQueue>0){
                                this.loadElementQueue--;
                                this.loadElements();
                            }
                        }
                    }else{
                        if (!this.elements.length){
                            //this.setNoGroupNoticeArea();
                        }else{
                            this.app.notice(this.options.lp.elementLoaded, "ok", this.listScrollNode, {"x": "center", "y": "bottom"});
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
        data.each(function(itemData, i){
            var item = this._newElement(itemData, this, i);
            this.elements.push(item);
            item.load();
            if (this.elements.length===1) item.selected();
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
            element.setUndelete();
            //if (element.deleteNode) element.deleteNode.click();
            if (this.currentItem!==element){
                element.contentNode.setStyles(element.style.contentNode);
                if (element.data.id) element.actionNode.fade("out");
            }
        }
        this.checkDeleteElements();
    },
    destroy: function(){

        if (this.hidePingyinFun) this.app.content.removeEvent("mousedown", this.hidePingyinFun);
        if (this.resizePropertyContentNodeFun) this.app.removeEvent("resize", this.resizePropertyContentNodeFun);
        if (this.continueLoadFun) this.app.removeEvent("resize", this.continueLoadFun);
        if (this.removePingyinFun) this.app.removeEvent("queryClose", this.removePingyinFun);
        MWF.release(this);
    },

    _isActionManager: function(){
        return MWF.AC.isOrganizationManager();
    },

    _listElementNext: function(lastid, count, callback){
        this.actions.listPersonNext(lastid, count, function(json){
            if (callback) {
                callback.apply(this, [json]);
            }
        }.bind(this));
    },
    _newElement: function(data, explorer, i){
        return new MWF.xApplication.Org.PersonExplorer.Person(data, explorer, this.isEditor, i);
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
    },
    getContentStyle: function(contentNode, node){
        var position = this.propertyContentNode.getPosition(this.propertyContentNode.getOffsetParent());
        var size = this.propertyContentNode.getSize();
        contentNode.position({"relativeTo": node,"position": "upperLeft","edge": "upperLeft"});
        return {
            "top": ""+position.y+"px",
            "left": ""+position.x+"px",
            "height": ""+size.y+"px",
            "width": ""+size.x+"px"
        };
    },
    openPerson: function(data, node){
        this.openContent("PersonExplorer", "PersonContent", data, node);
    },
    openGroup: function(data, node){
        this.openContent("GroupExplorer", "GroupContent", data, node);
    },
    openUnit: function(data, node){
        this.openContent("UnitExplorer", "UnitContent", data, node);
    },
    openContent: function(explorerClazz, contentClazz, data, node){
        MWF.xDesktop.requireApp("Org", explorerClazz, function(){
            var contentNode = new Element("div", {"styles": this.css.popContentNode}).inject(this.propertyContentNode, "top");
            var to = this.getContentStyle(contentNode, node);

            var resize = true;
            new Fx.Morph(contentNode, {
                "duration": 300,
                "transition": Fx.Transitions.Expo.easeOut
            }).start(to).chain(function(){
                content.setContentSize();
                resize = false;
                contentNode.setStyles({"position": "static","width": "100%","height": "100%"});
            }.bind(this));

            var item = {
                "explorer": this,
                "style": this.css.item,
                "data": data,
                "isEdit": false,
                "refresh": function(){},
                "propertyContentNode": contentNode
            };
            var content = new MWF.xApplication.Org[explorerClazz][contentClazz](item, true);
            var timeoutResize = function(){
                content.setContentSize();
                if (resize)window.setTimeout(function(){timeoutResize();}, 30);
            };
            window.setTimeout(function(){timeoutResize();}, 30);
        }.bind(this));
    }
});

MWF.xApplication.Org.$Explorer.Item = new Class({
    //Extends: MWF.xApplication.Organization.GroupExplorer.Group,

    initialize: function(data, explorer, isEditor, i){
        this.i = i;
        this.data = data;
        this.explorer = explorer;
        this.listNode = this.explorer.listNode;
        this.propertyContentNode = this.explorer.propertyContentNode;
        this.initStyle();

        this.selectedAttributes = [];

        this.isEdit = false;
        this.isEditor = isEditor;
        this.deleteSelected = false;
    },
    initStyle: function(){
        this.style = this.explorer.css.item;
    },
    refresh: function(){
        this.iconNode.getElement("img").set("src", this._getIcon());
        this._loadTextNode();
        if (this.content){
            if (this.content.titleInfor) this.content.titleInfor.refresh();
            if (this.content.bottomInfor) this.content.bottomInfor.refresh();
        }

        this.addActions();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.style.node}).inject(this.listNode);
        // if (this.i<10){
        //     var r = (59+((255-59)*this.i)/10).toInt();
        //     var g = (118+((255-118)*this.i)/10).toInt();
        //     var b = (182+((255-182)*this.i)/10).toInt();
        //     this.node.setStyle("background-color", "rgb("+r+","+g+","+b+")");
        // }

        this.contentNode = new Element("div", {"styles": this.style.contentNode}).inject(this.node);

        this.iconNode = new Element("div", {"styles": this.style.iconNode}).inject(this.contentNode);
        var src = this._getIcon();
        var img = new Element("img", {
            "styles": this.style.iconImgNode,
            "src": src
        }).inject(this.iconNode);


        this.actionNode = new Element("div", {"styles": this.style.actionNode}).inject(this.contentNode);

        this.textNode = new Element("div", {"styles": this.style.textNode}).inject(this.contentNode);
        this._loadTextNode();

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
            if (this.data.id){
                if (this.data.control.allowDelete){
                    if (!this.deleteNode){
                        this.deleteNode = new Element("div", {"styles": this.style.actionDeleteNode}).inject(this.actionNode);
                        this.deleteNode.addEvent("click", function(e){
                            if (!this.notDelete){
                                if (!this.deleteSelected){
                                    this.setDelete();
                                }else{
                                    this.setUndelete();
                                }
                            }
                            e.stopPropagation();
                        }.bind(this));

                        if (this.explorer.currentItem===this){
                            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
                        }
                    }
                }
            }
        }
    },
    setDelete: function(){
        this.actionNode.fade("in");
        this.deleteNode.setStyles(this.style.actionDeleteNode_delete);
        this.contentNode.setStyles(this.style.contentNode_delete);
        this.textNode.setStyles(this.style.textNode);
        this.explorer.deleteElements.push(this);
        this.deleteSelected = true;

        this.explorer.checkDeleteElements(this);
    },
    setUndelete: function(){
        this.actionNode.fade("out");
        if (this.explorer.currentItem!==this){
            this.deleteNode.setStyles(this.style.actionDeleteNode);
            this.contentNode.setStyles(this.style.contentNode);
            this.textNode.setStyles(this.style.textNode);
        }else{
            this.contentNode.setStyles(this.style.contentNode_selected);
            this.textNode.setStyles(this.style.textNode_selected);
            this.actionNode.setStyles(this.style.actionNode_selected);
            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode_selected);
        }

        this.explorer.deleteElements.erase(this);
        this.deleteSelected = false;
        this.explorer.checkDeleteElements(this);
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
                if (!this.deleteSelected){
                    if (this.explorer.currentItem){
                        if (this.explorer.currentItem.unSelected()){
                            this.selected();
                        }else{
                            this.explorer.app.notice(this.explorer.options.lp.elementSave, "error", this.propertyContentNode);
                        }
                    }else{
                        this.selected();
                    }
                }
            }.bind(this)
        });
    },

    unSelected: function(){
        if (this.content.baseInfor.mode==="edit") return false;
        if (!this.deleteSelected){
            this.explorer.currentItem = null;
            this.contentNode.setStyles(this.style.contentNode);
            this.textNode.setStyles(this.style.textNode);
            this.actionNode.setStyles(this.style.actionNode);
            if (this.deleteNode) this.deleteNode.setStyles(this.style.actionDeleteNode);
        }

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

    editBaseInfor: function(){
        this.content.edit();
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
    _loadTextNode: function(){
        this.textNode.set({"text": this.data.name});
    },

    _getIcon: function(nocache){
        var url = (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "/x_component_Org/$Explorer/default/icon/man.png";
        return (nocache) ? url+"?"+(new Date().getTime()) : url;

        //return (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "/x_component_Org/$Explorer/default/icon/man.png";
        // var src = "data:image/png;base64,"+this.data.icon;
        // if (!this.data.icon){
        //     if (this.data.genderType==="f"){
        //         src = "/x_component_Org/$Explorer/default/icon/female24.png"
        //     }else{
        //         src = "/x_component_Org/$Explorer/default/icon/man24.png"
        //     }
        // }
        // return src;
    }

});

MWF.xApplication.Org.$Explorer.ItemContent = new Class({
    initialize: function (item, isClose) {
        this.item = item;
        this.isClose = isClose;
        this.explorer = this.item.explorer;
        this.contentNode = this.item.propertyContentNode;
        this.style = this.item.style.person;
        this.load();
    },
    _getData: function(callback){
        this.data = this.item.data;
        if (callback) callback();
    },
    load: function(){

        this.titleContentNode = new Element("div").inject(this.contentNode);
        this.propertyContentScrollNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentNode);
        this.propertyContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.propertyContentScrollNode);
        this.bottomContentNode = new Element("div").inject(this.contentNode);

        this._getData(function(){
            this.explorer.propertyTitleNode.set("text", this.data.name);
            this._showItemPropertyTitle();
            this.loadItemPropertyTab(function(){
                this._loadTabs();
                this._loadContent();
                if (this.propertyTab.pages.length) this.propertyTab.pages[0].showTabIm();
            }.bind(this));
            this._showItemPropertyBottom();

            this.setContentSizeFun = this.setContentSize.bind(this);
            this.setContentSize();
            this.explorer.app.addEvent("resize", this.setContentSizeFun);

            new MWF.widget.ScrollBar(this.propertyContentScrollNode, {
                "style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        //this.showItemPropertyTitle();
    },
    setContentSize: function(){
        var size = this.contentNode.getSize();
        var titleSize = this.titleContentNode.getSize();
        var bottomSize = this.bottomContentNode.getSize();
        var y = size.y-titleSize.y-bottomSize.y;
        this.propertyContentScrollNode.setStyle("height", ""+y+"px")
    },

    loadItemPropertyTab: function(callback){
        this.propertyTabContainerNode = new Element("div", {"styles": this.item.style.tabTitleNode}).inject(this.propertyContentNode, "top");
        MWF.require("MWF.widget.Tab", function(){
            this.propertyTab = new MWF.widget.Tab(this.propertyContentNode, {"style": "org"});
            this.propertyTab.load();

            this.propertyTab.tabNodeContainer.inject(this.propertyTabContainerNode);

            if (callback) callback();
        }.bind(this));
    },

    edit: function(){

        if (this.baseInfor) this.baseInfor.edit();
    },
    destroy: function(){
        if (this.setContentSizeFun) this.explorer.app.removeEvent("resize", this.setContentSizeFun);
        if (this.titleInfor) this.titleInfor.destroy();
        if (this.bottomInfor) this.bottomInfor.destroy();
        if (this.baseInfor) this.baseInfor.destroy();
        this.contentNode.empty();
        MWF.release(this);
    },

    _showItemPropertyTitle: function(){
        this.titleInfor = new MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor(this);
    },
    _showItemPropertyBottom: function(){
        this.bottomInfor = new MWF.xApplication.Org.$Explorer.ItemContent.BottmInfor(this);
    },
    _loadTabs: function(){},
    _loadContent: function(){}
});

MWF.xApplication.Org.$Explorer.ItemContent.TitleInfor = new Class({
    initialize: function (content) {
        this.content = content;
        this.item = content.item;
        this.data = this.content.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.content.titleContentNode;
        this.style = this._getStyle();
        this.load();
    },
    _getStyle: function(){
        return this.item.style.person;
    },
    load: function(){
        this.titleBgNode = new Element("div", {"styles": this.style.titleBgNode}).inject(this.contentNode);
        this.titleNode = new Element("div", {"styles": this.style.titleNode}).inject(this.titleBgNode);
        this.setBackground();
        this.titleInforNode = new Element("div#titleInfor", {"styles": this.style.titleInforNode}).inject(this.titleNode);
        this.titleInforLeftNode = new Element("div", {"styles": this.style.titleInforLeftNode}).inject(this.titleInforNode);
        this.titleInforRightNode = new Element("div", {"styles": this.style.titleInforRightNode}).inject(this.titleInforNode);
        this.loadLeftInfor();
        this.loadRightInfor();
        this.loadAction();
        if (this.content.isClose) this.createCloseNode();
    },
    createCloseNode: function(){
        this.closeNode = new Element("div", {"styles": this.style.titleCloseNode}).inject(this.titleBgNode);
        this.closeNode.addEvents({
            "mousedown": function(){this.closeNode.setStyles(this.style.titleCloseNode_down)}.bind(this),
            "mouseup": function(){this.closeNode.setStyles(this.style.titleCloseNode)}.bind(this),
            "click": function(){
                var node = this.content.contentNode;
                this.content.destroy();
                node.destroy();
                node = null;
            }.bind(this)
        });
    },
    setBackground: function(){
        this.titleBgNode.setStyle("background-image", "url(/x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/person_bg_bg.png)");
        this.titleNode.setStyle("background-image", "url(/x_component_Org/$Explorer/"+this.explorer.app.options.style+"/icon/person_bg.png)");
    },
    loadLeftInfor: function(){
        if (!this.iconAreaNode) this.iconAreaNode = new Element("div", {"styles": this.style.titleInforIconAreaNode}).inject(this.titleInforLeftNode);
        if (!this.iconNode) this.iconNode = new Element("img", {"styles": this.style.titleInforIconNode}).inject(this.iconAreaNode);
        this.iconNode.set("src", this._getIcon());
    },
    _getIcon: function(nocache){
        var url = (this.data.id) ? this.explorer.actions.getPersonIcon(this.data.id) : "/x_component_Org/$Explorer/default/icon/man.png";
        return (nocache) ? url+"?"+(new Date().getTime()) : url;
        // var src = "data:image/png;base64,"+this.data.icon;
        // if (!this.data.icon){
        //     if (this.data.genderType==="f"){
        //         src = "/x_component_Org/$Explorer/default/icon/female.png"
        //     }else{
        //         src = "/x_component_Org/$Explorer/default/icon/man.png"
        //     }
        // }
        // return src;
    },
    loadRightInfor: function(){
        var text = this.data.name+((this.data.employee) ? "（"+this.data.employee+"）" : "");
        if (!this.nameNode) this.nameNode = new Element("div", {"styles": this.style.titleInforNameNode}).inject(this.titleInforRightNode);
        if (!this.signatureNode) this.signatureNode = new Element("div", {"styles": this.style.titleInforSignatureNode}).inject(this.titleInforRightNode);
        this.nameNode.set("text", text);
        this.signatureNode.set("text", (this.data.signature || "" ));
    },
    refresh: function(){
        this.loadLeftInfor();
        this.loadRightInfor();
    },
    loadAction: function(){},
    destroy: function(){
        this.contentNode.empty();
        MWF.release(this);
    }
});
MWF.xApplication.Org.$Explorer.ItemContent.BottomInfor = new Class({
    initialize: function (content) {
        this.content = content;
        this.item = content.item;
        this.data = this.content.data;
        this.explorer = this.item.explorer;
        this.contentNode = this.content.bottomContentNode;
        this.style = this._getStyle();
        this.load();
    },
    _getStyle: function(){
        return this.item.style.person;
    },
    load: function(){
        this.readContentNode = new Element("div", {"styles": this.style.baseReadNode}).inject(this.contentNode);
        this.addInforList();
    },
    addInfor: function(text){
        return new Element("div", {"styles": this.style.baseReadContentNode, "text": text}).inject(this.readContentNode);
    },
    addInforList: function(){},
    refresh: function(){
        this.readContentNode.empty();
        this.addInforList();
    },
    destroy: function(){
        this.readContentNode.empty();
        this.readContentNode.destroy();
        MWF.release(this);
    }
});
