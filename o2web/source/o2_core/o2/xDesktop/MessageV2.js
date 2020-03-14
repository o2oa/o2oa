//o2.require("o2.xDesktop.Message", null, false);
MWF.xDesktop.MessageV2 = new Class({
	// Extends: MWF.xDesktop.Message,
	Implements: [Events],
	
	initialize: function(desktop){
		this.desktop = desktop;
		this.container = this.desktop.contentNode;
		this.actionNode = this.desktop.msgActionNode;
        this.items = [];
		this.isShow = false;
		this.isMorph = false;
		this.unread = 0;

		this.itemTemplate ='<div class="layout_message_item_top" data-o2-element="topNode">'+
			'	<div class="layout_message_item_top_close icon_msg_close" data-o2-element="closeNode" data-o2-events="click:closeMsg"></div>'+
			'	<div class="layout_message_item_top_subject" data-o2-element="subjectNode" title="{{$.msg.subject}}">{{$.msg.subject}}</div>'+
			'</div>'+
			'<div class="layout_message_item_content" data-o2-element="contentNode"></div>'+
			'<div class="layout_message_item_bottom" data-o2-element="bottomNode">'+
			'	<div class="layout_message_item_bottom_date" data-o2-element="dateNode"></div>'+
			'	<div class="layout_message_item_bottom_action" data-o2-element="actionsNode"></div>'+
			'</div>';
	},
	load: function(){
		var path = this.desktop.path+this.desktop.options.style+((o2.session.isMobile || layout.mobile) ? "/layout-message-mobile.html" : "/layout-message-pc.html");
		this.container.loadHtml(path, {"bind": {"lp": o2.LP.desktop}, "module": this}, function(){
			MWF.require("MWF.widget.ScrollBar", function(){
				new MWF.widget.ScrollBar(this.scrollNode, {
					"style":"xDesktop_Message", "where": "before", "indent": false, "distance": 100, "friction": 6,	"axis": {"x": false, "y": true}
				});
			}.bind(this));

			this.node.addEvent("mousedown", function(e){
				e.stopPropagation();
				e.preventDefault();
			});

			this.hideMessage = function(){ this.hide(); }.bind(this);

			this.fireEvent("load");
		}.bind(this));
	},
	setPosition: function(){
		var size = this.container.getSize();
		var position = this.container.getPosition();
		this.maskNode.setStyle("height", ""+size.y+"px");
		this.node.setStyle("height", ""+size.y+"px");
        var y = size.y - 40;
		this.scrollNode.setStyle("height", ""+y+"px");

		var left = size.x;
		var top = position.y
		this.maskNode.setStyles({"left": ""+left+"px", "top": "0px"});
		this.node.setStyles({"left": ""+left+"px", "top": "0px"});
		
	},
	show: function(){
        var index = MWF.xDesktop.zIndexPool.zIndex;

        // this.css.messageContainerMaskNode["z-index"] = index;
        // this.css.messageContainerNode["z-index"] = index;

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
			this.maskNode.setStyles({"display": "block", "z-index": index});
			this.node.setStyles({"display": "block", "z-index": index});
			var position = this.node.getPosition(this.node.getOffsetParent());
			var size = this.node.getSize();
			var left = position.x-size.x;

			this.maskMorph.start({"left": ""+left+"px", "z-index":index});
			this.morph.start({"left": ""+left+"px", "z-index":index}).chain(function(){
				this.isShow = true;
				this.isMorph = false;
				this.desktop.desktopNode.addEvent("mousedown", this.hideMessage);
				this.fireEvent("show");
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
			var position = this.node.getPosition(this.node.getOffsetParent());
			var size = this.node.getSize();
			var left = position.x+size.x;
			
			this.maskMorph.start({"left": ""+left+"px"}).chain(function(){
				this.maskNode.setStyle("display", "none");
			}.bind(this));
			this.morph.start({"left": ""+left+"px"}).chain(function(){
				this.node.setStyle("display", "none");
				this.isShow = false;
				this.isMorph = false;
				this.desktop.desktopNode.removeEvent("mousedown", this.hideMessage);
				this.fireEvent("hide");
			}.bind(this));
		}
	},
	resize: function(){
		this.setPosition();
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
	addMessage: function(msg){
		var item = new MWF.xDesktop.MessageV2.Item(this,msg);
        this.items.push(item);
		this.addUnread();
		return item;
	},
    addTooltip: function(msg){
        var tooltop = new MWF.xDesktop.MessageV2.Tooltip(this,msg);
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
                this.unreadNode = new Element("div.layout_message_unread_node").inject(this.actionNode);
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
MWF.xDesktop.MessageV2.Item = new Class({
    Implements: [Events],
	initialize: function(message, msg){
		this.message = message;
		this.container = this.message.contentNode;
		this.css = this.message.css;
		this.msg = msg;
		this.load();
	},
	load: function(){
		//injectHtml
		this.node = new Element("div.layout_message_item").inject(this.container, "top");
		this.node.injectHtml(this.message.itemTemplate, {"bind": {"msg": this.msg}, "module": this});

		this.contentNode.set({"html": this.msg.content});
		this.contentNode.set({"title": this.contentNode.get("text")});
		this.dateNode.set("text", (new Date()).format("db"));

		this.setEvent();
	},
	setEvent: function(){},
	closeMsg: function(e){
		this.close(null, e)
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
MWF.xDesktop.MessageV2.Item.tooltips = [];
MWF.xDesktop.MessageV2.tooltipNode = null;

MWF.xDesktop.MessageV2.Tooltip = new Class({
    Extends: MWF.xDesktop.MessageV2.Item,
    setEvent: function(){

        if (!MWF.xDesktop.MessageV2.tooltipNode){
            MWF.xDesktop.MessageV2.tooltipNode = new Element("div.layout_message_tooltipArea").inject(this.message.container);
            var toNode = this.message.desktop.contentNode;
            MWF.xDesktop.MessageV2.tooltipNode.position({
                relativeTo: toNode,
                position: "rightTop",
                edge: "rightTop"
            });
        }

        this.node.inject(MWF.xDesktop.MessageV2.tooltipNode);
        this.node.addClass("layout_message_tooltip_item");

        MWF.xDesktop.MessageV2.Item.tooltips.push(this);
        window.setTimeout(function(){
            this.close(function(){
                MWF.xDesktop.MessageV2.Item.tooltips.erase(this);
            }.bind(this));
        }.bind(this), 10000);

        // this.closeNode.addEvents({
        //     "click": function(){
        //         this.close(function(){
        //             MWF.xDesktop.MessageV2.Item.tooltips.erase(this);
        //         }.bind(this));
        //     }.bind(this)
        // });
    },
	closeMsg: function(){
		this.close(function(){
			MWF.xDesktop.MessageV2.Item.tooltips.erase(this);
		}.bind(this));
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
MWF.xDesktop.Message = MWF.xDesktop.MessageV2;
