MWF.widget = MWF.widget || {};
MWF.widget.Toolbar = new Class({
	Implements: [Options, Events],
    Extends: MWF.widget.Common,
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

		this.path = MWF.defaultPath+"/widget/$Toolbar/";
		this.cssPath = MWF.defaultPath+"/widget/$Toolbar/"+this.options.style+"/css.wcss";

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
		this._loadToolBarSeparator(this.MWFToolBarSeparator);
		this._loadToolBarButton(this.MWFToolBarButton);
		this._loadToolBarMenu(this.MWFToolBarMenu);
	},
	_loadToolBarSeparator: function(nodes){
		if (nodes) {
			nodes.each(function(node, idx){
				node.set("styles", this.css.toolbarSeparator);
			}.bind(this));
		}
	},
	_loadToolBarButton: function(nodes){
		if (nodes) {
			nodes.each(function(node, idx){
				var btn =  new MWF.widget.ToolbarButton(node, this, this.options);
				btn.load();
				this.fireEvent("buttonLoad", [btn]);
				if (btn.buttonID){
					this.items[btn.buttonID] = btn;
				}
				this.children.push(btn);
				this.childrenButton.push(btn);
			}.bind(this));
		}
	},
	_loadToolBarMenu: function(nodes){
		var _self = this;
		if (nodes) {
			nodes.each(function(node, idx){
				var btn =  new MWF.widget.ToolbarMenu(node, this, {
					"onAddMenuItem": function(item){
						this.fireEvent("addMenuItem", [item]);
					}.bind(this),
					"onMenuQueryShow": function(menu){
						_self.fireEvent("menuQueryShow", [this, menu]);
					},
					"onMenuPostShow": function(menu){
						_self.fireEvent("menuPostShow", [this, menu]);
					},
					"onMenuQueryHide": function(menu){
						_self.fireEvent("menuQueryHide", [this, menu]);
					},
					"onMenuPostHide": function(menu){
						_self.fireEvent("menuPostHide", [this, menu]);
					},
					"onLoad": function(menu){
						_self.fireEvent("menuLoaded", [this, menu]);
					}
				});
				btn.load();
				this.fireEvent("menuLoad", [btn]);
				if (btn.buttonID){
					this.items[btn.buttonID] = btn;
				}
				this.children.push(btn);
				this.childrenMenu.push(btn);
			}.bind(this));
		}
	}

});

MWF.widget.ToolbarButton = new Class({
	Implements: [Options, Events],
	options: {
		"text": "",
		"title": "",
		"pic": "",
		"action": "",
        "actionScript": "",
		"disable": false
	},
	initialize: function(node, toolbar, options){
		this.setOptions(options);
		this.node = $(node);
		this.toolbar = toolbar;
		this.buttonID = this.node.get("MWFnodeid") || this.node.get("id");

		if (!this.node){
			this.node = new Element("div").inject(this.toolbar.node);
		}else{
			var buttonText = this.node.get("MWFButtonText");
			if (buttonText) this.options.text = buttonText;
			
			var title = this.node.get("title");
			if (title) this.options.title = title;
			
			var buttonImage = this.node.get("MWFButtonImage");
			if (buttonImage) this.options.pic = buttonImage;

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
				img.set("src", src);
			}
			if (this.textNode) this.textNode.set("styles", this.toolbar.css.buttonTextDiv);
		}
	},
	_createImageNode: function(src){
		if (src){
			var div = new Element("span", {"styles": this.toolbar.css.buttonImgDiv}).inject(this.node);
			var img = new Element("img", {
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
	setText: function(text){
        this.node.getLast().set("text", text);
	},

	_addButtonEvent: function(){
		this.node.addEvent("mouseover", this._buttonMouseOver.bind(this));
		this.node.addEvent("mouseout", this._buttonMouseOut.bind(this));
		this.node.addEvent("mousedown", this._buttonMouseDown.bind(this));
		this.node.addEvent("mouseup", this._buttonMouseUp.bind(this));
		this.node.addEvent("click", this._buttonClick.bind(this));
	},
	_buttonMouseOver: function(){
		if (this.modifiyStyle) if (!this.options.disable){this.node.set("styles", this.toolbar.css.buttonOver);};
	},
	_buttonMouseOut: function(){
		if (this.modifiyStyle) if (!this.options.disable){this.node.set("styles", this.toolbar.css.buttonOut);};
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
				if (typeOf(this.options.action)==="string"){
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
				}else{
                    this.options.action();
				}
			}
		}
	}
});

MWF.widget.ToolbarMenu = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.ToolbarButton,

	initialize: function(node, toolbar, options){
		this.parent(node, toolbar, options);
		this.modifiyStyle = true;
		this.menu = null;
        this.buttonID = this.node.get("MWFnodeid") || this.node.get("id");
	},
	setDisable: function(flag){
		if (this.menu) this.menu.options.disable = flag;
		if (flag){
			this.node.set("styles", this.toolbar.css.buttonDisable);
			if (this.picNode){
				this.picNode.set("styles", this.toolbar.css.buttonImgDivDisable);
				var img = this.picNode.getElement("img");
				var src = img.get("src");
				
				var ext = src.substr(src.lastIndexOf("."), src.length);
				src = src.substr(0, src.lastIndexOf("."));
				src = src+"_gray"+ext;
				
			//	src = src.substr(0, src.lastIndexOf("."));
			//	src = src+"_gray.gif";
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
				img.set("src", src);
			}
			if (this.textNode) this.textNode.set("styles", this.toolbar.css.buttonTextDiv);
		}
	},
	load: function(){

		this._addButtonEvent();
		this.node.title = this.options.title;
		this.node.set("styles", this.toolbar.css.button);

		if (this.options.pic) this.picNode = this._createImageNode(this.options.pic);
		if (this.options.text) this.textNode = this._createTextNode(this.options.text);
		
		this._createDownNode();

		var toolbarMenu = this;
		MWF.require("MWF.widget.Menu", function(){
			this.menu = new MWF.widget.Menu(this.node, {
				"style": this.toolbar.options.style,
				"bindObject": this.options.bindObject,
				"event": "click",
				"disable": toolbarMenu.options.disable,
				"onQueryShow": function(){
					var p = toolbarMenu.node.getPosition();
					var s = toolbarMenu.node.getSize();
					this.setOptions({
						"top": p.y+s.y-2,
						"left": p.x
					});
					toolbarMenu.fireEvent("menuQueryShow", [this]);
					return true;
				},
				"onPostShow": function(){
					var p = toolbarMenu.node.getPosition();
					var s = toolbarMenu.node.getSize();
					toolbarMenu.node.set("styles", {
						"background-color": this.node.getStyle("background-color"),
						"border-top": this.node.getStyle("border-top"),
						"border-right": this.node.getStyle("border-top"),
						"border-left": this.node.getStyle("border-top"),
						"border-bottom": "0"
					});
					toolbarMenu.modifiyStyle = false;

					toolbarMenu.tmpStyleNode = new Element("div",{
						"styles":{
							"position":"absolute",
							"top": p.y+s.y-2,
							"left": p.x+1,
							"width": s.x-2,
							"z-index": this.node.getStyle("z-index")+1,
							"background-color": this.node.getStyle("background-color"),
							"height": "1px",
							"overflow": "hidden"
						}
					}).inject($(document.body));

					toolbarMenu.fireEvent("menuPostShow", [this]);
				},
				"onQueryHide": function(){
					toolbarMenu.fireEvent("menuQueryHide", [this]);
				},
				"onPostHide": function(){
					toolbarMenu.node.set("styles", toolbarMenu.toolbar.css.button);
					toolbarMenu.modifiyStyle = true;
					if (toolbarMenu.tmpStyleNode) toolbarMenu.tmpStyleNode.destroy();
					toolbarMenu.fireEvent("menuPostHide", [this]);
				}
			});
			this.menu.load();
			this.fireEvent("load", [this.menu]);
			this.addMenuItems();
		}.bind(this));

		this.setDisable(this.options.disable);

	},
	_createDownNode: function(){
		var spanNode = new Element("div",{
			"styles": this.toolbar.css.toolbarButtonDownNode
		}).inject(this.node);
		spanNode.set("html", "<img src=\""+MWF.defaultPath+"/widget/$Toolbar/"+this.toolbar.options.style+"/downicon.gif"+"\" />");
		return spanNode;
	},
	addMenuItems: function(){
		var els = this.node.getChildren();
		els.each(function(node, idx){
			var type = node.get("MWFnodetype");
			if (type=="MWFToolBarMenuItem"){
				this._loadMenuItem(node);
			}
			if (type=="MWFToolBarMenuLine"){
				this._loadMenuLine(node);
			}
		}.bind(this));
	},
	_loadMenuItem: function(node){
		var toolBarMenu = this;
		var item = this.menu.addMenuItem(node.get("MWFButtonText"),"click",function(e){toolBarMenu._menuItemClick(node.get("MWFButtonAction"), e, this);}, node.get("MWFButtonImage"), node.get("MWFButtonDisable"));
		this.fireEvent("addMenuItem", [item]);
	},

	_menuItemClick: function(itemAction, e, item){
		if (itemAction){
			var tmparr = itemAction.split(":");
			var action = tmparr.shift();
			var bindObj = (this.toolbar.bindObject) ? this.toolbar.bindObject : window;
			if (bindObj[action]){
				tmparr.push(this);
                tmparr.push(e);
                tmparr.push(item);
				bindObj[action].apply(bindObj,tmparr);
				this.menu.hideMenu();
			}else{
				if (window[action]){
					window[action].apply(this,tmparr);
					this.menu.hideMenu();
				}
			}
		}
	},

	_loadMenuLine: function(){
		this.menu.addMenuLine();
	}

});