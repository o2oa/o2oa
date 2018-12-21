MWF.xDesktop.Message = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	
	initialize: function(desktop, options){
		this.setOptions(options);
		
		this.desktop = desktop;
		this.container = this.desktop.desktopNode;
		this.actionNode = this.desktop.top.messageActionNode;
		this.css = this.desktop.css;

        this.items = [];
		this.isShow = false;
		this.isMorph = false;
		this.unread = 0;
	},
	load: function(){
		this.maskNode = new Element("iframe", {"styles": this.css.messageContainerMaskNode});
        this.maskNode.setStyles({
			"border": "0px",
			"margin": "0px",
			"padding": "0px"
		});
		this.maskNode.inject(this.container);
		this.node = new Element("div", {"styles": this.css.messageContainerNode});
		this.node.inject(this.container);

		this.scrollNode = new Element("div", {"styles": this.css.messageContainersScrollNode}).inject(this.node);
        this.contentNode = new Element("div", {"styles": this.css.messageContainerContentNode}).inject(this.scrollNode);
		this.operationNode = new Element("div", {"styles": this.css.messageContainersOperationNode}).inject(this.node);
        this.clearAction = new Element("div", {"styles": this.css.messageContainersClearActionNode, "text": MWF.LP.desktop.clearMessage}).inject(this.operationNode);
        this.clearAction.addEvents({
            "mouseover": function(){this.clearAction.setStyles(this.css.messageContainersClearActionNode_over);}.bind(this),
            "mouseout": function(){this.clearAction.setStyles(this.css.messageContainersClearActionNode);}.bind(this),
            "mousedown": function(){this.clearAction.setStyles(this.css.messageContainersClearActionNode_down);}.bind(this),
            "mouseup": function(){this.clearAction.setStyles(this.css.messageContainersClearActionNode_over);}.bind(this),
            "click": function(){this.clearMessage();}.bind(this)
        });

		
		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(this.scrollNode, {
				"style":"xDesktop_Message", "where": "before", "indent": false, "distance": 100, "friction": 6,	"axis": {"x": false, "y": true}
			});
		}.bind(this));
		
		this.node.addEvent("click", function(e){
			e.stopPropagation();
			e.preventDefault();
		});
		
		this.setPosition();
		
		this.hideMessage = function(){
			this.hide();
		}.bind(this);
	},
    clearMessage: function(){
        var clearItems = [];
        this.items.each(function(item){
            if (item.status!="progress") clearItems.push(item);
        }.bind(this));

        while (clearItems.length){
            clearItems[0].close();
            clearItems.erase(clearItems[0]);
        }
    },
	setPosition: function(){
		var size = this.container.getSize();
		var position = this.container.getPosition();
		this.maskNode.setStyle("height", ""+size.y+"px");
		this.node.setStyle("height", ""+size.y+"px");
        var y = size.y - 40;
		this.scrollNode.setStyle("height", ""+y+"px");
		
		this.maskNode.position({
			relativeTo: this.container,
			position: "rightTop",
		    edge: (this.isShow) ? "rightTop" : "leftTop"
//		    offset: {"x": 0, "y": -100}
		});
		this.node.position({
			relativeTo: this.container,
			position: "rightTop",
		    edge: (this.isShow) ? "rightTop" : "leftTop"
//		    offset: {"x": 0, "y": -100}
		});
		
	},
	show: function(){
        var index = MWF.xDesktop.zIndexPool.zIndex
        this.css.messageContainerMaskNode["z-index"] = index;
        this.css.messageContainerNode["z-index"] = index;

		if (!this.isMorph){
			this.isMorph = true;
			this.setPosition();
			if (!this.morph){
				this.maskMorph = new Fx.Morph(this.maskNode, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
				this.morph = new Fx.Morph(this.node, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
			}
			this.maskNode.setStyle("display", "block");
			this.node.setStyle("display", "block");
			var position = this.node.getPosition();
			var size = this.node.getSize();
			var left = position.x-size.x;
			this.maskMorph.start({"left": ""+left+"px", "z-index":index});
			this.morph.start({"left": ""+left+"px", "z-index":index}).chain(function(){
				this.isShow = true;
				this.isMorph = false;
				this.desktop.node.addEvent("click", this.hideMessage);
			}.bind(this));
		}
	},
	hide: function(){
		if (!this.isMorph){
			this.isMorph = true;
			if (!this.morph){
				this.maskMorph = new Fx.Morph(this.maskNode, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
				this.morph = new Fx.Morph(this.node, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
			}
			var position = this.node.getPosition();
			var size = this.node.getSize();
			var left = position.x+size.x;
			
			this.maskMorph.start({"left": ""+left+"px"}).chain(function(){
				this.maskNode.setStyle("display", "none");
			}.bind(this));
			this.morph.start({"left": ""+left+"px"}).chain(function(){
				this.node.setStyle("display", "none");
				this.isShow = false;
				this.isMorph = false;
				this.desktop.node.removeEvent("click", this.hideMessage);
			}.bind(this));
		}
	},
	resize: function(){
		this.setPosition();
	},
	addMessage: function(msg){
		var item = new MWF.xDesktop.Message.Item(this,msg);
        this.items.push(item);
		this.addUnread();
		return item;
	},
    addTooltip: function(msg){
        var tooltop = new MWF.xDesktop.Message.Tooltip(this,msg);
        return tooltop;
    },
	getUnread: function(){
		//获取未读消息列表和数量
		return this.unread || 0;
	},
	setUnread: function(){
		//this.actionNode
		if (this.unread>0){
			if (!this.unreadNode){
                this.unreadNode = new Element("div", {"styles": this.css.messageUnreadCountNode}).inject(this.actionNode);
            }
            this.unreadNode.set("text", this.unread);
		}else{
			if (this.unreadNode){
				this.unreadNode.destroy();
				this.unreadNode = null;
				delete this.unreadNode;
			}
		}
	},
	addUnread: function(count){
		var addCount = count || 1;
		this.unread = this.unread+addCount;
		this.setUnread();
	}
	
});
MWF.xDesktop.Message.Item = new Class({
    Implements: [Events],
	initialize: function(message, msg){
		this.message = message;
		this.container = this.message.contentNode;
		this.css = this.message.css;
		this.msg = msg;
		
//		msg = {
//			"subject": "",
//			"content": "",
//			"type": "",
//			"action": {
//				
//			}
//		};
		this.load();
	},
	load: function(){
		this.node = new Element("div", {"styles": this.css.messageItemNode});
		
		this.topNode = new Element("div", {"styles": this.css.messageItemTopNode}).inject(this.node);
		this.closeNode = new Element("div", {"styles": this.css.messageItemCloseNode}).inject(this.topNode);
		this.subjectNode = new Element("div", {"styles": this.css.messageItemSubjectNode}).inject(this.topNode);
		
		this.contentNode = new Element("div", {"styles": this.css.messageItemContentNode}).inject(this.node);
		this.bottomNode = new Element("div", {"styles": this.css.messageItemBottomNode}).inject(this.node);
		
		this.dateNode = new Element("div", {"styles": this.css.messageItemDateNode}).inject(this.bottomNode);
		this.actionsNode = new Element("div", {"styles": this.css.messageItemActionsNode}).inject(this.bottomNode);
		
		this.subjectNode.set({"text": this.msg.subject, "title": this.msg.subject});
		this.contentNode.set({"html": this.msg.content});
		this.contentNode.set({"title": this.contentNode.get("text")});
		this.dateNode.set("text", (new Date()).format("db"));
		
		this.node.inject(this.container, "top");
		
		this.setEvent();
	},
	setEvent: function(){
		this.closeNode.addEvents({
			"click": function(e){
				this.close(null, e);
			}.bind(this)
		});
	},
	close: function(callback, e){
        //flag = true;
        //this.fireEvent("close", [flag, e]);
        //alert(flag);
        //if (flag){
            this.closeItem(callback, e);
        //}
	},
    closeItem: function(callback){
        var morph = new Fx.Morph(this.node, {
            duration: "200"
            //	transition: Fx.Transitions.Sine.easeOut
        });
        var size = this.node.getSize();
        this.node.setStyle("height", ""+size.y+"px");
        this.message.items.erase(this);
        morph.start({
            "opacity": 0,
            "height": "0px"
        }).chain(function(){
            this.message.addUnread(-1);
            this.node.destroy();
            if (callback) callback();
            delete this;
        }.bind(this));
    }
});
MWF.xDesktop.Message.Item.tooltips = [];
MWF.xDesktop.Message.tooltipNode = null;

MWF.xDesktop.Message.Tooltip = new Class({
    Extends: MWF.xDesktop.Message.Item,
    setEvent: function(){

        if (!MWF.xDesktop.Message.tooltipNode){
            MWF.xDesktop.Message.tooltipNode = new Element("div", {
                "styles": this.css.messageTooltipAreaNode
            }).inject(this.message.container);

            var toNode = this.message.desktop.desktopNode;
            MWF.xDesktop.Message.tooltipNode.position({
                relativeTo: toNode,
                position: "rightTop",
                edge: "rightTop"
            });
        }

        this.node.inject(MWF.xDesktop.Message.tooltipNode);
        this.node.setStyles(this.css.messageTooltipNode);


        //if (MWF.xDesktop.Message.Item.tooltips.length){
        //    var toNode = MWF.xDesktop.Message.Item.tooltips[MWF.xDesktop.Message.Item.tooltips.length-1].node;
        //    this.node.position({
        //        relativeTo: toNode,
        //        position: "centerBottom",
        //        edge: "centerTop",
        //        offset: {"x": 0, "y": 10}
        //    });
        //}else{
        //    var toNode = this.message.desktop.desktopNode;
        //    this.node.position({
        //        relativeTo: toNode,
        //        position: "rightTop",
        //        edge: "rightTop",
        //        offset: {"x": -10, "y": 10}
        //    });
        //}
        MWF.xDesktop.Message.Item.tooltips.push(this);
        window.setTimeout(function(){
            this.close(function(){
                MWF.xDesktop.Message.Item.tooltips.erase(this);
            }.bind(this));
        }.bind(this), 10000);

        this.closeNode.addEvents({
            "click": function(){
                this.close(function(){
                    MWF.xDesktop.Message.Item.tooltips.erase(this);
                }.bind(this));
            }.bind(this)
        });
    },
    closeItem: function(callback){
        var morph = new Fx.Morph(this.node, {
            duration: "200"
            //	transition: Fx.Transitions.Sine.easeOut
        });
        var size = this.node.getSize();
        this.node.setStyle("height", ""+size.y+"px");
        this.message.items.erase(this);
        morph.start({
            "opacity": 0,
            "height": "0px"
        }).chain(function(){
            //this.message.addUnread(-1);
            this.node.destroy();
            if (callback) callback();
            delete this;
        }.bind(this));
    }
});