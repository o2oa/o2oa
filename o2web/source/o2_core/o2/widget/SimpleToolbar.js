o2.widget.SimpleToolbar = new Class({
	Extends: o2.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function(container, options, bindObject){
		this.setOptions(options);
		this.bindObject = bindObject;

		this.items = [];
		this.children = [];
		this.childrenButton = [];
		this.childrenMenu = [];

		this.path = o2.session.path+"/widget/$SimpleToolbar/";
		this.cssPath = o2.session.path+"/widget/$SimpleToolbar/"+this.options.style+"/css.wcss";

		this._loadCss();

		this.node = $(container);
		this.node.onselectstart = function (){return false;};
		this.node.oncontextmenu = function (){return false;};

	},
	load: function(){
		if (this.fireEvent("queryLoad")){

			this.node.set("styles", this.css.container);

			this._loadToolbarItemNode();
			this._loadToolbarItems();
			this.fireEvent("postLoad");
		}
	},
	_loadToolbarItemNode: function(){
		var subNodes = this.node.getChildren();
		subNodes.each(function(node, idx){
			var type = node.get("MWFnodetype");
			if (type){
				if (typeOf(this[type])=="array"){
					this[type].push(node);
				}else{
					this[type] = [];
					this[type].push(node);
				}
			}
		}.bind(this));
	},
	_loadToolbarItems: function(){
		//this._loadToolBarSeparator(this.MWFToolBarSeparator);
		this._loadToolBarButton(this.MWFToolBarButton);
		//this._loadToolBarMenu(this.MWFToolBarMenu);
	},
	_loadToolBarButton: function(nodes){
		if (nodes) {
			nodes.each(function(node, idx){
				var btn =  new o2.widget.SimpleToolbarButton(node, this, this.options);
				btn.load();
				this.fireEvent("buttonLoad", [btn]);
				if (btn.buttonID){
					this.items[btn.buttonID] = btn;
				}
				this.children.push(btn);
				this.childrenButton.push(btn);
			}.bind(this));
		}
	}

});

o2.widget.SimpleToolbarButton = new Class({
	Implements: [Options, Events],
	options: {
		"text": "",
		"title": "",
		"pic": "",
		"pic_over": "",
		"action": "",
        "actionScript": "",
		"disable": false
	},
	initialize: function(node, toolbar, options){
		this.setOptions(options);
		this.node = $(node);
		this.toolbar = toolbar;
		this.buttonID = this.node.MWFnodeid || this.node.id;		

		if (!this.node){
			this.node = new Element("div").inject(this.toolbar.node);
		}else{
			var buttonText = this.node.get("MWFButtonText");
			if (buttonText) this.options.text = buttonText;
			
			var title = this.node.get("title");
			if (title) this.options.title = title;
			
			var buttonImage = this.node.get("MWFButtonImage");
			if (buttonImage) this.options.pic = buttonImage;

			var buttonImageOver = this.node.get("MWFButtonImageOver");
			if (buttonImageOver) this.options.pic_over = buttonImageOver;

			var buttonDisable = this.node.get("MWFButtonDisable");
			if (buttonDisable) this.options.disable = true;
			
			var buttonAction = this.node.get("MWFButtonAction");
			if (buttonAction) this.options.action = buttonAction;

            //var buttonActionScript = this.node.get("MWFButtonActionScript");
            //if (buttonActionScript) this.options.actionScript = buttonActionScript;
		}

		this.modifiyStyle = true;
	},
	load: function(){
		this._addButtonEvent();
		this.node.title = this.options.title;
		this.node.set("styles", this.toolbar.css.button);

		if (this.options.pic) this.picNode = this._createImageNode(this.options.pic);
		if (this.options.text) this.textNode = this._createTextNode(this.options.text);

		this.setDisable(this.options.disable);

	},
	enable: function(){
		if (this.options.disable){
			this.setDisable(false);
			this.options.disable = false;
		}
	},
	disable: function(){
		if (!this.options.disable){
			this.setDisable(true);
			this.options.disable = true;
		}
	},
	setDisable: function(flag){
		if (flag){
			this.node.set("styles", this.toolbar.css.buttonDisable);
			if (this.picNode){
				this.picNode.set("styles", this.toolbar.css.buttonImgDivDisable);
				var img = this.picNode.getElement("img");
				var src = img.get("src");
				var ext = src.substr(src.lastIndexOf("."), src.length);
				src = src.substr(0, src.lastIndexOf("."));
				src = src+"_gray"+ext;
				this.src_gray = src;
				img.set("src", src);
			}
			if (this.textNode) this.textNode.set("styles", this.toolbar.css.buttonTextDivDisable);
		}else{
			this.node.set("styles", this.toolbar.css.button);
			if (this.picNode){
				this.picNode.set("styles", this.toolbar.css.buttonImgDiv);
				var img = this.picNode.getElement("img");
				var src = img.get("src");
				src = src.replace("_gray", "");
				this.src_gray = src;
				img.set("src", src);
			}
			if (this.textNode) this.textNode.set("styles", this.toolbar.css.buttonTextDiv);
		}
	},
	_createImageNode: function(src){
		if (src){
			var div = new Element("span", {"styles": this.toolbar.css.buttonImgDiv}).inject(this.node);
			var img = this.img = new Element("img", {
				"styles": this.toolbar.css.buttonImg,
				"src": src
			}).inject(div);
			return div;
		}else{
			return null;
		}
	},
	_createTextNode: function(text){
		if (text){
			var div = new Element("span", {
				"styles": this.toolbar.css.buttonTextDiv,
				"text": text
			}).inject(this.node);
			return div;
		}else{
			return null;
		}
	},

	_addButtonEvent: function(){
		this.node.addEvent("mouseover", this._buttonMouseOver.bind(this));
		this.node.addEvent("mouseout", this._buttonMouseOut.bind(this));
		this.node.addEvent("mousedown", this._buttonMouseDown.bind(this));
		this.node.addEvent("mouseup", this._buttonMouseUp.bind(this));
		this.node.addEvent("click", this._buttonClick.bind(this));
	},
	_buttonMouseOver: function(){
		if (this.modifiyStyle) if (!this.options.disable){
			if(this.options.pic_over)this.img.set("src",this.options.pic_over)
			this.node.set("styles", this.toolbar.css.buttonOver);
		};
	},
	_buttonMouseOut: function(){
		if (this.modifiyStyle) if (!this.options.disable){
			this.img.set("src",this.options.pic)
			this.node.set("styles", this.toolbar.css.buttonOut);
		};
	},
	_buttonMouseDown: function(){
		if (this.modifiyStyle) if (!this.options.disable){this.node.set("styles", this.toolbar.css.buttonDown);};
	},
	_buttonMouseUp: function(){
		if (this.modifiyStyle) if (!this.options.disable){this.node.set("styles", this.toolbar.css.buttonUp);};
	},
	_buttonClick: function(e){
		if (!this.options.disable){
			if (this.options.action){
				var tmparr = this.options.action.split(":");
				var action = tmparr.shift();
				var bindObj = (this.toolbar.bindObject) ? this.toolbar.bindObject : window;

				if (bindObj[action]){
					tmparr.push(this);
                    tmparr.push(e);
					bindObj[action].apply(bindObj,tmparr);
				}else{
					if (window[action]){
						window[action].apply(this,tmparr);
					}
				}
			}
		}
	}
});