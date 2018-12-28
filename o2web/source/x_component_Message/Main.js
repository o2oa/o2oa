MWF.xApplication.Message.options.multitask = true;
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xApplication.Message.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "Message",
		"icon": "icon.png",
		"width": "1100",
		"height": "700",
		"title": MWF.xApplication.Message.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Message.LP;
	},
	loadApplication: function(callback){
        this.node = new Element("div", {"styles": {"width": "100%", "height": "100%"}}).inject(this.content);
		this.leftNode = new Element("div", {"styles": this.css.leftNode}).inject(this.node);
		this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);

		this.menuNode = new Element("div", {"styles": this.css.menuNode}).inject(this.leftNode);
        this.listNode = new Element("div", {"styles": this.css.listNode}).inject(this.leftNode);

        this.setListNodeSizeFun = this.setListNodeSize.bind(this);
		this.addEvent("resize", this.setListNodeSizeFun);
        this.setListNodeSize();

		this.loadMenu(function(){
            if (MWF.AC.isMessageManager()){
                this.initList();
                this.loadList();
                this.listNode.addEvent("scroll", function(){
                    var s = this.listNode.getScroll();
                    var size = this.listNode.getSize();
                    var sSize = this.listNode.getScrollSize();
                    if (sSize.y-size.y-s.y<100) if (!this.listAll) this.loadList();
                }.bind(this));
                if (callback) callback();
            }else{
                this.createNoAcListNode();
            }
        }.bind(this));
	},
    initList: function(){
	    this.removeItems = [];
	    this.listItems = [];
	    this.lastId = "(0)";
	    this.listCount = 0;
	    this.listAll = false;
        this.listPageCount = this.getPageCount();
    },
    getPageCount: function(){
        var size = this.listNode.getSize();
        return (size.y/80).toInt()+5;
    },

    setListNodeSize: function(){
		var menuSize = this.menuNode.getSize();
		var size = this.content.getSize();
		var y = size.y-menuSize.y;
		this.listNode.setStyle("height", ""+y+"px");
	},

    loadMenu: function(callback){
        this.actionArea = new Element("div", {"styles": this.css.actionArea}).inject(this.menuNode);
        this.logoArea = new Element("div", {"styles": this.css.logoArea}).inject(this.menuNode);

        this.iconNode = new Element("div", {"styles": this.css.logoIconNode}).inject(this.logoArea);
        this.titleNode = new Element("div", {"styles": this.css.logoTitleNode}).inject(this.logoArea);

        var action = MWF.Actions.get("x_message_assemble_communicate");
        action.enableType(function(json){
            this.type = json.data.value;
            if (this.type==="qiyeweixin"){
                this.iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/weixin.png)");
                this.titleNode.set("text", this.lp.weixin);
                this.loadAddAction();
            }else if (this.type==="dingding"){
                this.iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/dingding.png)");
                this.titleNode.set("text", this.lp.dingding);
                this.loadAddAction();
            }else{
                this.iconNode.setStyle("background-image", "");
                this.titleNode.set("text", this.lp.disabled);
            }
            if (callback) callback();
        }.bind(this));
    },
    loadAddAction: function(){
        if (MWF.AC.isMessageManager()){
            this.addAction = new Element("div", {"styles": this.css.addAction, "title": this.lp.new}).inject(this.actionArea);
            this.addAction.addEvents({
                "click": function(){
                    this.createMessage();
                }.bind(this)
            });
        }
    },
    loadMenu1: function(){
        this.actionArea = new Element("div", {"styles": this.css.actionArea}).inject(this.menuNode);
		this.searchArea = new Element("div", {"styles": this.css.searchArea}).inject(this.menuNode);

        this.addAction = new Element("div", {"styles": this.css.addAction, "title": this.lp.new}).inject(this.actionArea);

        this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.searchArea);
        this.searchActionNode = new Element("div", {"styles": this.css.searchActionNode}).inject(this.searchNode);
        this.searchBarNode = new Element("div", {"styles": this.css.searchBarNode}).inject(this.searchNode);
        this.searchInput = new Element("input", {"styles": this.css.searchInput, "value": this.lp.search}).inject(this.searchBarNode);

        this.searchInput.addEvents({
			"focus": function(){if (this.searchInput.get("value")===this.lp.search) this.searchInput.set("value", "");}.bind(this),
            "blur": function(){if (!this.searchInput.get("value")) this.searchInput.set("value", this.lp.search);}.bind(this),
			"keydown": function(e){
				if (e.code===13) this.search();

                var key = this.searchInput.get("value");
                if (key && key!==this.lp.search){
					this.showSearchClear();
				}else{
                    this.clearSearch();
					this.hideSearchClear();
				}
			}.bind(this)
		});

        this.searchActionNode.addEvents({
			"mouseover": function(){this.setStyle("background-color", "#eeeeee")},
            "mouseout": function(){this.setStyle("background-color", "#ffffff")},
			"click": function(){this.search();}.bind(this)
		});

        this.addAction.addEvents({
			"click": function(e){
				this.createMessage(e);
			}.bind(this)
		});
	},

    loadList: function(){
		var action = MWF.Actions.get("x_message_assemble_communicate");
		action.list(this.lastId, this.listPageCount, function(json){
            var count = json.data.length;
            if (!count){
                if (this.lastId==="(0)"){
                    this.createNoListNode();
                    this.listAll = true;
                }
            }else{
                this.createListNodes(json.data);
                this.lastId = json.data[json.data.length-1].id;
            }
            if (count<this.listPageCount) this.listAll = true;
		}.bind(this));
	},
    createNoListNode: function(){
        new Element("div", {"styles": this.css.noListNode, "text": this.lp.noListNode}).inject(this.listNode)
    },
    createNoAcListNode: function(){
        new Element("div", {"styles": this.css.noListNode, "text": this.lp.noAcListNode}).inject(this.listNode)
    },
    createListNodes: function(items){
        items.each(function(item){
            this.listItems.push(new MWF.xApplication.Message.Item(item, this));
        }.bind(this))
    },
	showSearchClear: function(){
        this.searchArea.setStyles(this.css.searchArea_key);
        this.searchBarNode.setStyles(this.css.searchBarNode_key);
        if (!this.clearActionNode){
            this.clearActionNode = new Element("div", {"styles": this.css.clearActionNode}).inject(this.searchActionNode, "before");
            this.clearActionNode.addEvents({
                "mouseover": function(){this.setStyle("background-color", "#eeeeee")},
                "mouseout": function(){this.setStyle("background-color", "#ffffff")},
                "click": function(){
                    this.searchInput.set("value", this.lp.search);
                    this.clearSearch();
                    this.hideSearchClear();
                }.bind(this)
            });
        }
	},
    hideSearchClear: function(){
        if (this.clearActionNode){
            this.clearActionNode.destroy();
            this.clearActionNode = null;
            this.searchArea.setStyles(this.css.searchArea);
            this.searchBarNode.setStyles(this.css.searchBarNode);
        }
	},

    search: function(){
        var key = this.searchInput.get("value");
        if (e.code===13){
            if (key && key!==this.lp.search){
                this.doSearch(key);
            }
        }
	},
    doSearch: function(key){

	},
    clearSearch: function(){

	},
	createMessage: function(e){
    	if (this.currentDocument){
            this.currentDocument.close(null, e, function(){
                this.currentDocument = new MWF.xApplication.Message.Document(null, this);
            }.bind(this));
		}else{
            this.currentDocument = new MWF.xApplication.Message.Document(null, this);
		}
	},

    checkRemoveAction: function(item){
	    if (this.removeItems.length){
	        if (!this.removeAction) this.createRemoveAction();
            this.removeAction.position({
                relativeTo: item.node,
                position: "centerBottom",
                edge: "centerTop"
            });
        }else{
	        this.deleteRemoveAction();
        }
    },
    createRemoveAction: function(item){
        this.removeAction = new Element("div", {"styles": this.css.removeAction}).inject(this.content);
        this.removeDeleteAction = new Element("div", {"styles": this.css.removeDeleteAction, "text": this.lp.removeText}).inject(this.removeAction);
        this.removeCancelAction = new Element("div", {"styles": this.css.removeCancelAction, "text": this.lp.cancel}).inject(this.removeAction);

        this.removeCancelAction.addEvent("click", function(){
            while (this.removeItems.length){
                this.removeItems[0].readyRemove();
            }
        }.bind(this));

        this.removeDeleteAction.addEvent("click", function(){
            this.removeItems.each(function(item){
                item.remove(function(){
                    this.checkRemoveAction();
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },
    deleteRemoveAction: function(){
        if (this.removeAction){
            this.removeAction.destroy();
            this.removeAction = null;
        }
    }

});
MWF.xApplication.Message.Document = new Class({
    initialize: function(data, app, item){
        this.app = app;
        this.item = item;
        this.data = data || {};
        this.data.unitList = this.data.unitList || [];
        this.data.identityList = this.data.identityList || [];
        this.data.groupList = this.data.groupList || [];
        this.data.personList = this.data.personList || [];

        this.isNew = (this.data.id) ? false : true;
        this.contentNode = this.app.contentNode;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.load();
    },
    load: function(){
    	this.node = new Element("div", {"styles": this.css.documentNode}).inject(this.contentNode);
		this.menuNode = new Element("div", {"styles": this.css.documentMenuNode}).inject(this.node);
		this.loadMenu();
        this.contentNode = new Element("div", {"styles": this.css.documentContentNode}).inject(this.node);

        this.setContentNodeSizeFun = this.setContentNodeSize.bind(this);
        this.app.addEvent("resize", this.setContentNodeSizeFun);
        this.setContentNodeSize();

        this.loadContent();
	},
    loadMenu: function(){
		//this.actionArea = new Element("div", {"styles": this.css.documentActionArea}).inject(this.menuNode);
        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbar = new MWF.widget.Toolbar(this.menuNode, {"style": "message"}, this);
            if (this.isNew){
                new Element("div", {
                    "MWFnodeid": "send",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.app.path+this.app.options.style+"/icon/send.png",
                    "title": this.lp.send,
                    "MWFButtonAction": "send",
                    "MWFButtonText": this.lp.send
                }).inject(this.menuNode);
            }else{
                new Element("div", {
                    "MWFnodeid": "resend",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.app.path+this.app.options.style+"/icon/send.png",
                    "title": this.lp.send,
                    "MWFButtonAction": "resend",
                    "MWFButtonText": this.lp.resend
                }).inject(this.menuNode);

                new Element("div", {
                    "MWFnodeid": "remove",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.app.path+this.app.options.style+"/icon/remove.png",
                    "title": this.lp.remove,
                    "MWFButtonAction": "remove",
                    "MWFButtonText": this.lp.remove
                }).inject(this.menuNode);
            }

            new Element("div", {
                "MWFnodeid": "close",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonImage": this.app.path+this.app.options.style+"/icon/close.png",
                "title": this.lp.close,
                "MWFButtonAction": "close",
                "MWFButtonText": this.lp.close
            }).inject(this.menuNode);
            this.toolbar.load();

        }.bind(this));
	},

    loadContent: function(){
        if (this.isNew) {
            this.loadContentNew();
        }else{
            this.loadContentRead();
        }
    },
    loadContentNew: function(){
        var title = "";
        if (this.app.type==="qiyeweixin") title = this.lp.create_weixin;
        if (this.app.type==="dingding") title = this.lp.create_dingding;
        this.titleNode = new Element("div", {"styles": this.css.documentTitleNode, "text": title}).inject(this.contentNode);

        this.sendPersonArea = new Element("div", {"styles": this.css.documentSendPersonNode}).inject(this.contentNode);
        this.sendPersonTitle = new Element("div", {"styles": this.css.documentSendTitleNode, "text": this.lp.sendPerson}).inject(this.sendPersonArea);
        this.sendPersonSelect = new Element("div", {"styles": this.css.documentSendPersonSelectNode}).inject(this.sendPersonArea);
        this.sendPersonInputArea = new Element("div", {"styles": this.css.documentSendPersonInputAreaNode}).inject(this.sendPersonArea);

        this.selectAction = new Element("div", {"styles": this.css.documentSendPersonSelectAction, "text": this.lp.select}).inject(this.sendPersonSelect);
        this.sendPersonInput = new Element("div", {"styles": this.css.documentSendPersonInputNode}).inject(this.sendPersonInputArea);
        this.selectAction.addEvent("click", this.selectPerson.bind(this));
        this.sendPersonInput.addEvent("click", this.selectPerson.bind(this));

        var values = this.data.unitList.combine(this.data.identityList).combine(this.data.groupList).combine(this.data.personList);
        this.loadOrgWidget(values, this.sendPersonInput);


        this.sendBodyArea = new Element("div", {"styles": this.css.documentSendBodyNode}).inject(this.contentNode);
        this.sendBodyTitle = new Element("div", {"styles": this.css.documentSendTitleNode, "text": this.lp.sendBody}).inject(this.sendBodyArea);
        this.sendBodyInputArea = new Element("div", {"styles": this.css.documentSendBodyInputAreaNode}).inject(this.sendBodyArea);


        this.sendBodyInput = new Element("textarea", {"styles": this.css.documentSendBodyInputNode}).inject(this.sendBodyInputArea);
        if(this.data.body) this.sendBodyInput.set("value", this.data.body);
    },
    loadContentRead: function(){
        var title = "";
        if (this.data.type==="qiyeweixin") title = this.lp.open_weixin;
        if (this.data.type==="dingding") title = this.lp.open_dingding;
        this.titleNode = new Element("div", {"styles": this.css.documentTitleNode, "text": title}).inject(this.contentNode);

        this.sendPersonArea = new Element("div", {"styles": this.css.documentSendPersonNode}).inject(this.contentNode);
        this.sendPersonTitle = new Element("div", {"styles": this.css.documentSendTitleNode, "text": this.lp.sendPerson}).inject(this.sendPersonArea);
        this.sendPersonSelect = new Element("div", {"styles": this.css.documentSendPersonSelectNode}).inject(this.sendPersonArea);
        this.sendPersonInputArea = new Element("div", {"styles": this.css.documentSendPersonInputAreaNode}).inject(this.sendPersonArea);

        this.sendPersonInput = new Element("div", {"styles": this.css.documentSendPersonInputNode}).inject(this.sendPersonInputArea);
        var values = this.data.unitList.combine(this.data.identityList).combine(this.data.groupList).combine(this.data.personList);
        this.loadOrgWidget(values, this.sendPersonInput);

        this.sendBodyArea = new Element("div", {"styles": this.css.documentSendBodyNode}).inject(this.contentNode);
        this.sendBodyTitle = new Element("div", {"styles": this.css.documentSendTitleNode, "text": this.lp.sendBody}).inject(this.sendBodyArea);
        this.sendBodyInputArea = new Element("div", {"styles": this.css.documentSendBodyInputAreaNode}).inject(this.sendBodyArea);
        this.sendBodyInput = new Element("div", {"styles": this.css.documentSendBodyInputNode}).inject(this.sendBodyInputArea);
        this.sendBodyInput.set("text", this.data.body);
    },

    selectPerson: function(){
        MWF.xDesktop.requireApp("Selector", "package", function(){
            var values = this.data.unitList.combine(this.data.identityList).combine(this.data.groupList).combine(this.data.personList);

            var options = {
                "type" : "",
                "types": ["identity","unit","person","group"],
                "values" : values,
                "onComplete": function(items, itemsObject){
                    this.data.identityList = [];
                    this.data.unitList = [];
                    this.data.groupList = [];
                    this.data.personList = [];
                    var arr = [];
                    items.each( function(it){
                        var id = it.data.distinguishedName;
                        if (id) {
                            var flag = id.substr(id.length-1,1);
                            if (flag==="I") this.data.identityList.push(id);
                            if (flag==="U") this.data.unitList.push(id);
                            if (flag==="P") this.data.personList.push(id);
                            if (flag==="G") this.data.groupList.push(id);
                            arr.push(id);
                        }
                    }.bind(this));
                    this.sendPersonInput.empty();
                    this.loadOrgWidget(arr, this.sendPersonInput);

                }.bind(this)
            };
            new MWF.O2Selector(this.app.content, options);
        }.bind(this));
    },
    loadOrgWidget: function(values, node){
        var options = {"style": "xform", "canRemove":false , "onRemove" : this.removeItem};
        values.each(function(value){
            var flag = value.substr(value.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    new MWF.widget.O2Identity({"id":value}, node, options );
                    break;
                case "p":
                    new MWF.widget.O2Person({"id":value}, node, options);
                    break;
                case "u":
                    new MWF.widget.O2Unit({"id":value}, node, options);
                    break;
                case "g":
                    new MWF.widget.O2Group({"id":value}, node, options);
                    break;
                default:
                    new MWF.widget.O2Other({"id":value}, node, options);
            }
            if( layout.mobile ){
                widget.node.setStyles({
                    "float" : "none"
                })
            }
        }.bind(this));
    },
    setContentNodeSize: function(){
        var menuSize = this.menuNode.getSize();
        var size = this.app.content.getSize();
        var y = size.y-menuSize.y;
        this.contentNode.setStyle("height", ""+y+"px");
    },
    close: function(bt, e, success, failure){
        if (this.isNew){
            var _self = this;
            this.app.confirm("info", e, this.lp.closeConfirmTitle, this.lp.closeConfirmNew, "500", "120", function(){
                _self.doClose();
                if (success) success();
                this.close();
            }, function(){
                if (failure) failure();
                this.close();
            });
        }else{
            this.doClose();
            if (success) success();
        }
	},
    doClose: function(){
        if (this.setContentNodeSizeFun) this.app.removeEvent("resize", this.setContentNodeSizeFun);
        this.app.currentDocument = null;
        this.node.destroy();
        if (this.item) this.item.uncurrent();
        MWF.release(this);
    },
    remove: function(bt, e){
        var _self = this;
        this.app.confirm("info", e, this.lp.removeConfirmTitle, this.lp.removeConfirm, "400", "120", function(){
            _self.doRemove();
            this.close();
        }, function(){
            this.close();
        });
    },
    doRemove: function(){
        var action = MWF.Actions.get("x_message_assemble_communicate");
        action.delete(this.data.id, function(json){
            this.app.notice(this.lp.removeSuccess, "success");
            if (this.item) this.item.destroy();
            this.doClose();
        }.bind(this));
    },
    send: function(bt, e){
        if (!this.data.unitList.length && !this.data.identityList.length && !this.data.groupList.length && !this.data.personList.length){
            this.app.notice(this.lp.noSendPersonError, "error");
            return false;
        }
        if (!this.sendBodyInput.get("value")){
            this.app.notice(this.lp.noSendBodyError, "error");
            return false;
        }

        var _self = this;
        this.app.confirm("info", e, this.lp.sendConfirmTitle, this.lp.sendConfirm, "300", "120", function(){
            _self.doSend();
            this.close();
        }, function(){
            this.close();
        });
    },
    doSend: function(){
        this.data.body = this.sendBodyInput.get("value");
        var action = MWF.Actions.get("x_message_assemble_communicate");
        action.create(this.data, function(json){
            this.app.notice(this.lp.sendSuccess, "success");
            if (!this.app.listItems.length) this.app.listNode.empty();
            var item = new MWF.xApplication.Message.Item(json.data, this.app);
            item.node.inject(this.app.listNode, "top");
            this.app.listItems.push(item);

            this.doClose();
        }.bind(this));
    },
    resend: function(){
        var data = Object.clone(this.data);
        data.id = "";
        var app = this.app;
        this.doClose();
        new MWF.xApplication.Message.Document(data, app);
    }

});


MWF.xApplication.Message.Item = new Class({
    initialize: function(data, app){
        this.app = app;
        this.data = data;
        this.contentNode = this.app.listNode;
        this.css = this.app.css;
        this.lp = this.app.lp;
        this.isRemove = false;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.listItemNode}).inject(this.contentNode);
        this.action = new Element("div", {"styles": this.css.listItemActionNode}).inject(this.node);
        this.icon = new Element("div", {"styles": this.css.listItemIconNode}).inject(this.node);
        this.content = new Element("div", {"styles": this.css.listItemContentNode}).inject(this.node);

        if (this.data.type==="qiyeweixin"){
            this.icon.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/weixin32.png)");
        }else if (this.data.type==="dingding"){
            this.icon.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/dingding32.png)");
        }else{
            this.icon.setStyle("background-image", "");
        }


        this.titleLine = new Element("div", {"styles": this.css.listItemTitleLine}).inject(this.content);
        this.bodyLine = new Element("div", {"styles": this.css.listItemBodyLine}).inject(this.content);
        this.personLine = new Element("div", {"styles": this.css.listItemPersonLine}).inject(this.content);

        new Element("div", {"styles": this.css.listItemTitleLineTextLeft, "text": this.data.createTime}).inject(this.titleLine);
        new Element("div", {"styles": this.css.listItemTitleLineTextRight, "text": MWF.name.cn(this.data.creatorPerson)}).inject(this.titleLine);

        new Element("div", {"text": this.data.body}).inject(this.bodyLine);

        var values = this.data.unitList.combine(this.data.identityList).combine(this.data.groupList).combine(this.data.personList);
        new Element("div", {"text": this.lp.sendPerson+MWF.name.cns(values).join(", ")}).inject(this.personLine);

        this.setEvent();
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){
                if (!this.isRemove){
                    this.action.fade("in");
                    if (!this.app.currentDocument || this.app.currentDocument.data.id!==this.data.id) this.node.setStyle("background-color", "#eaf2fb");
                }
            }.bind(this),
            "mouseout": function(){
                if (!this.isRemove){
                    this.action.fade("out");
                    if (!this.app.currentDocument || this.app.currentDocument.data.id!==this.data.id) this.node.setStyle("background-color", "#ffffff");
                }
            }.bind(this),
            "click": function(e){
                if (!this.isRemove) this.node.setStyle("background-color", "#dcecfd");
                this.open(e);
            }.bind(this)
        });
        this.action.addEvents({
            "click": function(e){
                e.stopPropagation();
                this.readyRemove();
            }.bind(this)
        });
    },
    readyRemove: function(){
        if (this.isRemove){
            this.action.setStyles(this.css.listItemActionNode);
            this.action.fade("out");
            if (this.app.currentDocument && this.app.currentDocument.data.id===this.data.id){
                this.node.setStyle("background-color", "#dcecfd");
            }else{
                this.node.setStyle("background-color", "#ffffff");
            }
            this.isRemove = false;
            this.app.removeItems.erase(this);
        }else{
            this.action.setStyles(this.css.listItemActionNode_remove);
            this.node.setStyle("background-color", "#fed1d1");
            this.app.removeItems.push(this);
            this.isRemove = true;
        }
        this.app.checkRemoveAction(this);
    },
    uncurrent: function(){
        if (this.app.currentDocument && this.app.currentDocument.data.id===this.data.id) this.app.currentDocument = null;
        this.node.setStyle("background-color", "#ffffff");
    },
    open: function(e){
        if (this.app.currentDocument){
            this.app.currentDocument.close(null, e, function(){
                this.app.currentDocument = new MWF.xApplication.Message.Document(this.data, this.app, this);
            }.bind(this));
        }else{
            this.app.currentDocument = new MWF.xApplication.Message.Document(this.data, this.app, this);
        }
    },
    remove: function(callback){
        if (this.app.currentDocument && this.app.currentDocument.data.id===this.data.id){
            this.app.currentDocument.doClose();
            this.app.currentDocument = null;
        }
        var action = MWF.Actions.get("x_message_assemble_communicate");
        action.delete(this.data.id, function(json){
            this.destroy();
            if (callback) callback();
        }.bind(this), function(){
            if (callback) callback();
        });
    },
    destroy: function(){
        this.uncurrent();
        this.node.destroy();
        this.app.removeItems.erase(this);
        this.app.listItems.erase(this);
        MWF.release(this);
    }
});