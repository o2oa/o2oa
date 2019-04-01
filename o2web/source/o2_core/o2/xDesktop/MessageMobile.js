MWF.xDesktop.MessageMobile = new Class({
	Implements: [Options, Events],
	options: {
        "css":{
            "messageContainerNode": {
                "width": "100%",
                "position": "absolute",
                "z-index": 500000,
                "top": "0px",
                "left": "0px",
                "background-color": "#FFF",
                "overflow": "hidden"
            },

            "messageContentNode":{
                "overflow": "hidden"
            },
            "messageHideContentNode": {
                "height": "10px",
                "line-height": "6px",
                "text-align": "center",
                "display": "none",
                "border-bottom": "1px solid #999",
                "background-color": "#EEE"
            },
            "messageItemNode": {
                "padding": "5px",
                "overflow": "hidden",
                "border-bottom": "1px solid #CCC",
                "background": "transparent",
                "cursor": "default"
            },
            "messageItemTopNode": {
                "height": "24px",
                "line-height": "24px",
                "font-size": "16px",
                "color": "#4387cd"
            },
            "messageItemCloseNode": {
                "height": "30px",
                "width": "16px",
                "float": "right",
                "cursor": "pointer",
                "background": "url("+MWF.defaultPath+"/xDesktop/$Layout/default/message/close.png) no-repeat center center"
            },
            "messageItemSubjectNode": {
                "height": "24px",
                "margin-right": "18px"
            },
            "messageItemContentNode": {
                "overflow": "hidden",
                "color": "#333",
                "font-size": "12px",
                "line-height": "20px"
            },
            "messageItemBottomNode": {
                "height": "24px",
                "font-size": "12px",
                "line-height": "24px"
            },
            "messageItemDateNode": {
                "color": "#666"
            }
        }
    },
	
	initialize: function(options){
		this.setOptions(options);
		this.container = $(document.body);
		this.css = this.options.css;
		this.isShow = false;
		this.isMorph = false;
	},
	load: function(){
		this.node = new Element("div", {"styles": this.css.messageContainerNode});
		this.node.inject(this.container);

        this.contentNode = new Element("div", {"styles": this.css.messageContentNode}).inject(this.node);
		this.hideNode = new Element("div", {"styles": this.css.messageHideContentNode, "text": "——"}).inject(this.node);

        this.hideNode.addEvent("click", this.hide.bind(this));

        this.setPosition();
	},
	show: function(){
		if (!this.isMorph){
			this.isMorph = true;
			if (!this.morph){
				this.morph = new Fx.Morph(this.node, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
			}
			this.node.setStyle("display", "block");
            this.hideNode.setStyle("display", "block");
            this.setPosition();
			this.morph.start({"top": "0px"}).chain(function(){
				this.isShow = true;
				this.isMorph = false;
			}.bind(this));
		}
	},
	hide: function(){
		if (!this.isMorph){
			this.isMorph = true;
			if (!this.morph){
				this.morph = new Fx.Morph(this.node, {
					duration: "200",
					transition: Fx.Transitions.Sine.easeOut
				});
			}
			var position = this.node.getPosition();
			var size = this.node.getSize();
			var top = 0-size.y;
			this.morph.start({"top": ""+top+"px"}).chain(function(){
				this.node.setStyle("display", "none");
				this.isShow = false;
				this.isMorph = false;
			}.bind(this));
		}
	},
	addMessage: function(msg){
		var item = new MWF.xDesktop.MessageMobile.Item(this,msg);
        if (!this.isShow){
            this.setPosition();
            //this.show();
        }
		return item;
	},
    setPosition: function(){
        if (!this.isShow){
            var size = this.node.getSize();
            var top = 0-size.y;
            this.node.setStyle("top", ""+top+"px");
        }
    },
    addTooltip: function(msg){}
});
MWF.xDesktop.MessageMobile.Item = new Class({
	initialize: function(message, msg){
		this.message = message;
		this.container = this.message.contentNode;
		this.css = this.message.css;
		this.msg = msg;
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
		
		this.subjectNode.set({"text": this.msg.subject, "title": this.msg.subject});
		this.contentNode.set({"html": this.msg.content});
		this.contentNode.set({"title": this.contentNode.get("text")});
		this.dateNode.set("text", (new Date()).format("db"));
		
		this.node.inject(this.container, "top");

        this.setEvent();
	},
    setEvent: function(){
        this.closeNode.addEvents({
            "click": function(){
                this.close();
            }.bind(this)
        });
    },
    close: function(callback){
        var morph = new Fx.Morph(this.node, {
            duration: "200"
            //	transition: Fx.Transitions.Sine.easeOut
        });
        var size = this.node.getSize();
        this.node.setStyle("height", ""+size.y+"px");
        morph.start({
            "opacity": 0,
            "height": "0px"
        }).chain(function(){
            this.node.destroy();
            delete this;
        }.bind(this));
    },
    closeItem: function(callback){
        var morph = new Fx.Morph(this.node, {
            duration: "200"
            //	transition: Fx.Transitions.Sine.easeOut
        });
        var size = this.node.getSize();
        this.node.setStyle("height", ""+size.y+"px");
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