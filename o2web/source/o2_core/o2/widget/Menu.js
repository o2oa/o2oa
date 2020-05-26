o2.widget = o2.widget || {};
o2.widget.Menu = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.Common,
	options: {
		"style": "default",
		"event": "contextmenu",
		"disable": false,
		"top": -1,
		"left": -1,
        "container": null
	},
	initialize: function(target, options){
		this.setOptions(options);

		this.items = [];
		
		this.path = o2.session.path+"/widget/$Menu/";
		this.cssPath = o2.session.path+"/widget/$Menu/"+this.options.style+"/css.wcss";

		this._loadCss();

		this.target = $(target);
	//	if (this.target.onselectstart) 
		if (this.target) this.target.onselectstart = function(){return false;};
	//	if (this.target.oncontextmenu) 
		if (this.target) this.target.oncontextmenu = function(){return false;};
		
		this.pauseCount = 0;
	},
	pause: function(count){
		this.pauseCount = count;
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			this.node = new Element("div.MWFMenu");
			this.node.set("styles", this.css.container);

			if (this.options.event){
				if (this.target) this.target.addEvent(this.options.event, this.showIm.bind(this));
			}
			//this.node.inject(this.options.container || $(document.body));
			this.node.inject(this.options.container || this.target);

			this.hide = this.hideMenu.bind(this);
			this.fireEvent("postLoad");
		}
	},
	setItemWidth: function(){
		this.items.each(function(item){
			var w1 = this.node.getStyle("padding-left").toInt();
			var w2 = this.node.getStyle("padding-right").toInt();
			var w3 = this.node.getStyle("border-left-width").toInt();
			var w4 = this.node.getStyle("border-right-width").toInt();
			var w5 = item.item.getStyle("border-left-width").toInt();
			var w6 = item.item.getStyle("border-right-width").toInt();
			var w7 = item.item.getStyle("margin-left").toInt();

		//	item.item.setStyle("width", this.node.getSize().x-w1-w2-w3-w4-w5-w6-w7);
			/*
			if (item.type=="line"){
				
				item.item.setStyle("width", this.node.getSize().x-8-28);
			}else{
				item.item.setStyle("width", this.node.getSize().x-8);
			}*/
		}.bind(this));
	},
	setPosition: function(e){
		var top;
		var left;
		if (this.options.top==-1){
			top = e.page.y;
		}else{
			top = this.options.top;
		}
		if (this.options.left==-1){
			left = e.page.x;
		}else{
			left = this.options.left;
		}
		var size = this.node.getSize();

		var bodyNode = this.node.getOffsetParent();

		//var bodySize = $(document.body).getSize();
        var bodySize = bodyNode.getSize();

		if (left+size.x>bodySize.x){
			left = left-size.x-5;
			if (left<0) left = 0;
		}
		
		//var scrollTop = ($(document.body).getScroll().y.toFloat()) || 0;
        var scrollTop = (bodyNode.getScroll().y.toFloat()) || 0;
		
		if (top+size.y>bodySize.y+scrollTop){
			top = top-size.y-5;
			if (top<0) top = 0;
		}
		this.node.setStyle("top", top);
		this.node.setStyle("left", left);

		var nodeSize = this.node.getSize();
        if (!this.nodeFrame) this.createIframeNode();
        this.nodeFrame.setStyles({
			"top": top,
            "left": left,
			"width": ""+nodeSize.x+"px",
            "height": ""+nodeSize.y+"px"
		});
	},
	showIm: function(e){
		if (!this.options.disable){
			this.hide = this.hideIm.bind(this);
			if (this.fireEvent("queryShow", [e])){
				this.tmpBodyOncontextmenu = document.body.oncontextmenu;
				document.body.oncontextmenu = function(){return false;};
				if (this.pauseCount<=0){
					this.setItemWidth();
					
					this.node.setStyles({
						"display": "block",
						"opacity": this.options.opacity || 1
					});

                    if (!this.nodeFrame) this.createIframeNode();
                    this.nodeFrame.setStyles({
                        "display": "block",
                        "opacity": 0
                    });
					
					this.setPosition(e);
					
					$(document.body).removeEvent("mousedown", this.hide);
					$(document.body).addEvent("mousedown", this.hide);
					
					this.show = true;
				}else{
					this.pauseCount--;
				}
				this.fireEvent("postShow", [e]);
			}
		}
		
	},
	hideIm: function(all){
		if (this.fireEvent("queryHide")){
			$(document.body).removeEvent("mousedown", this.hide);
			this.node.set("styles", {
				"display": "none",
				"opacity": 0
			});
            if (this.nodeFrame){
                this.nodeFrame.set("styles", {
                    "display": "none",
                    "opacity": 0
                });
			}

			this.show = false;
			document.body.oncontextmenu = this.tmpBodyOncontextmenu;
			this.tmpBodyOncontextmenu = null;
			
			if (all){
                var menu = this;
                while (menu.topMenu){
                    menu = menu.topMenu;
                }
                menu.hideIm();
                //if (this.topMenu) this.topMenu.hideIm();
            } else {
                this.items.each(function(item){
                    if (item.type=="menu"){
                        item.subMenu.hideIm();
                    }
                });
            }

			this.fireEvent("postHide");
		}
	},
	createIframeNode: function(){
        if (!this.nodeFrame){
            this.nodeFrame = new Element("iframe");
            //this.nodeFrame.set("styles", this.css.container);
            this.nodeFrame.setStyles({
                "border": "0px",
                "z-index": "998",
                "margin": "0px",
                "padding": "0px",
                "position": "absolute",
                "display": "none",
                "opacity": 0
            });
        }
        //this.nodeFrame.inject(this.options.container || $(document.body));
        this.nodeFrame.inject(this.options.container || this.target);
	},
	showMenu: function(e){
		if (!this.show){
			if (this.pauseCount<=0){
				if (!this.options.disable){
					this.hide = this.hideMenu.bind(this);
					if (this.fireEvent("queryShow", [e])){
						this.tmpBodyOncontextmenu = document.body.oncontextmenu;
						document.body.oncontextmenu = function(){return false;};
						this.node.setStyle("display", "block");
                        if (!this.nodeFrame) this.createIframeNode();
                        this.nodeFrame.setStyle("display", "block");
						this.setItemWidth();
						this.setPosition(e);
						if (!this.morph){
							this.morph = new Fx.Morph(this.node, {duration: 100});
                            //this.morphFrame = new Fx.Morph(this.nodeFrame, {duration: 100});
						}

                        // this.morphFrame.start({
                        //     "opacity": this.options.opacity || 1
                        // });
						this.morph.start({
							"opacity": this.options.opacity || 1
						}).chain(function(){
							$(document).removeEvent("click", this.hide);
							$(document).addEvent("click", this.hide);
							$(document).removeEvent("mousedown", this.hide);
							$(document).addEvent("mousedown", this.hide);
							this.show = true;
							this.fireEvent("postShow");
						}.bind(this));
					}
				}
			}else{
				this.pauseCount--;
			}
		}
	},
	hideMenu: function(){
		$(document).removeEvent("click", this.hide);
		if (this.show){
			if (!this.morph){
				this.morph = new Fx.Morph(this.node, {duration: 100});
                //this.morphFrame = new Fx.Morph(this.nodeFrame, {duration: 100});
			}
			if (this.fireEvent("queryHide")){
                // this.morphFrame.start({
                //     "opacity": 0
                // });
				this.morph.start({
					"opacity": 0
				}).chain(function(){
					this.node.set("styles", {
						"display": "none"
					});
                    this.nodeFrame.set("styles", {
                        "display": "none"
                    });
					this.show = false;
					document.body.oncontextmenu = this.tmpBodyOncontextmenu;
					this.tmpBodyOncontextmenu = null;
					this.fireEvent("postHide");
				}.bind(this));
			}
		}
	},
	clearItems: function(){
		this.items.each(function(item){
			item.remove();
		});
		this.items = [];
	},
	addMenuItem: function(str, even, fun, img, disable){
		var item = new o2.widget.MenuItem(this, {
			"text": str,
			"event": even,
			"action": fun,
			"img": img,
			"disable": disable
		});
		item.load();
		this.items.push(item);
		return item;
	},
	addMenuMenu: function(str, img, menu, disable){
		var item = new o2.widget.MenuMenu(this, menu, {
			"text": str,
			"img": img,
			"disable": disable
		});
		item.load();
		this.items.push(item);
		return item;
	},
	addMenuLine: function(){
		var item = new o2.widget.MenuLine(this);
		item.load();
		this.items.push(item);
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


	_loadMenuItems: function(){
		this._loadToolBarSeparator(this.MWFToolBarMenuItem);
		this._loadToolBarButton(this.MWFToolBarMenuLine);
		this._loadToolBarMenu(this.MWFToolBarMenuItem);
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
				var btn =  new o2.widget.ToolbarButton(node, this);
				btn.load();
				if (btn.buttonID){
					this.items[btn.buttonID] = btn;
				}
				this.children.push(btn);
				this.childrenButton.push(btn);
			}.bind(this));
		}
	},
	_loadToolBarMenu: function(nodes){
		if (nodes) {
			nodes.each(function(node, idx){
				var btn =  new o2.widget.ToolbarMenu(node, this);
				btn.load();
				if (btn.buttonID){
					this.items[btn.buttonID] = btn;
				}
				this.children.push(btn);
				this.childrenMenu.push(btn);
			}.bind(this));
		}
	}

});

o2.widget.MenuItem = new Class({
	Implements: [Options],
	options: {
		"text": "",
		"event": "",
		"action": null,
		"img": "",
		"disable": false
	},
	initialize: function(menu, options){
		this.setOptions(options);
		this.menu = menu;
		this.type="item";
		this.createNode();
		
	},
	createNode: function(){
		this.item = new Element("div", {"styles": this.menu.css.menuItem});
		var imgDiv = new Element("div", {"styles": this.menu.css.menuItemImgDiv}).inject(this.item);
        if (this.options.img){
            if (this.options.img.substr(0,3)=="url"){
                var img = new Element("div", {"styles": this.menu.css.menuItemImg}).inject(imgDiv);
                img.setStyles({
                    "background-size": "cover",
                    "background-image": this.options.img
				});
            }else{
                var img = new Element("img", {"styles": this.menu.css.menuItemImg, "src": this.options.img}).inject(imgDiv);
            }
		}


		var separator = new Element("div", {"styles": this.menu.css.menuItemSeparator}).inject(this.item);
		this.text = new Element("div", {"styles": this.menu.css.menuItemText, "text": this.options.text}).inject(this.item);
		if (this.options.event) this.item.addEvent(this.options.event, this.doAction.bind(this));

		this.setDisable(this.options.disable);
	},
	setText: function(text){
		this.options.text = text;
		var textNode = this.item.getLast("div");
		if (textNode) textNode.set("text", text);
	},
	load: function(){
		this.item.inject(this.menu.node);
		this._addButtonEvent();
	},
	setDisable: function(flag){
		if (this.options.disable!=flag){
			this.options.disable = flag;
			if (flag){
				this.item.set("styles", this.menu.css.menuItemDisable);
				var img = this.item.getElement("img");
				if (img){
					var src = img.get("src");
					//src = src.substr(0, src.lastIndexOf("."));

					if (src.substr(0,5) != "data:"){
                        var i = src.lastIndexOf(".");
                        srcLeft = src.substr(0, i);
                        srcRight = src.substr(i, src.length-i);
                        src = srcLeft+"_gray"+srcRight;
                        //src = src.replace(i, "_gray.");
                        img.set("src", src);
                    }
				}
			}else{
				this.item.set("styles", this.menu.css.menuItem);
				var img = this.item.getElement("img");
				if (img){
					var src = img.get("src");
                    if (src.substr(0,5) != "data:"){
                        src = src.replace("_gray", "");
                        img.set("src", src);
                    }
				}
			}
		}
	},

	_addButtonEvent: function(){
		this.item.addEvent("mouseover", this._menuItemMouseOver.bind(this));
		this.item.addEvent("mouseout", this._menuItemMouseOut.bind(this));
		this.item.addEvent("mousedown", this._menuItemMouseDown.bind(this));
		this.item.addEvent("mouseup", this._menuItemMouseUp.bind(this));
		//this.item.addEvent("click", this.doAction.bind(this));
	},
	_menuItemMouseOver: function(e){
		this.menu.items.each(function(item){
			if (item!=this) if (item.type!="line")	if (!item.options.disable){item._menuItemMouseOut(e);};
		}.bind(this));
		if (!this.options.disable){this.item.set("styles", this.menu.css.menuOver); this.menu.current = this;};
	},
	_menuItemMouseOut: function(e){
		if (!this.options.disable){this.item.set("styles", this.menu.css.menuOut);};
	},
	_menuItemMouseDown: function(e){
		if (!this.options.disable){this.item.set("styles", this.menu.css.menuDown);};
		e.stopPropagation();
	},
	_menuItemMouseUp: function(e){
		if (!this.options.disable){this.item.set("styles", this.menu.css.menuUp);};
	},
	doAction: function(e){
		if (!this.options.disable){
			if (this.options.action){
				this.options.action.apply(this, [e]);
			}
			this.menu.hideIm(true);
		}
	},
	remove: function(){
		this.item.destroy();
	}
});

o2.widget.MenuLine = new Class({
	initialize: function(menu, options){
		this.type="line";
		this.menu = menu;
		this.createNode();
	},
	createNode: function(){
		this.item = new Element("div", {"styles": this.menu.css.menuLine});
	},
	load: function(){
		this.item.inject(this.menu.node);
	},
	remove: function(){
		this.item.destroy();
	},
	setDisable: function(){}
});

o2.widget.MenuMenu = new Class({
	Implements: [Options],
	Extends: o2.widget.MenuItem,

	initialize: function(menu, submenu, options){
		//this.setOptions(options);

		this.subMenu = submenu;
		this.parent(menu, options);
		this.subMenu.topMenu = this.menu;
		this.type="menu";
		
		this.createIcon();
		
		this.subMenu.setPosition = function(){
			this.setPosition();
		}.bind(this);
	},
	createIcon: function(){
		var icon = new Element("div", {"styles": this.menu.css.menuItemSubmenuIcon});
		icon.inject(this.text, "before");
		this.text.setStyle("margin-right", "16px");
	},
	_menuItemMouseOver: function(e){
		this.menu.items.each(function(item){
			if (item!=this) if (item.type!="line")	if (!item.options.disable){item._menuItemMouseOut(e);};
		}.bind(this));
		if (!this.options.disable){this.item.set("styles", this.menu.css.menuOver); this.menu.current = this;};
		this.subMenu.showIm();
	},
	_menuItemMouseOut: function(e){
		if (e.event.toElement!=this.subMenu.node && !this.subMenu.node.contains(e.event.toElement)){
			if (!this.options.disable){this.item.set("styles", this.menu.css.menuOut);};
			this.subMenu.hideIm();
		}
	},
	
	setPosition: function(e){
		var top;
		var left;
		
		var position = this.item.getPosition();
		var size = this.item.getSize();
        this.subMenu.node.setStyle("display", "block");
		var menuSize = this.subMenu.node.getSize();

		var bodyNode = this.subMenu.node.getOffsetParent();
		//var bodySize = $(document.body).getSize();
        var bodySize = bodyNode.getSize();
		
		top = position.y;
		left = (position.x.toFloat()) + (size.x.toFloat())-3;
		if ((left.toFloat()) + (menuSize.x.toFloat())>bodySize.x){
			left = (position.x.toFloat()) - (menuSize.x.toFloat())+8;
		}
		
		//var scrollTop = ($(document.body).getScroll().y.toFloat()) || 0;
        var scrollTop = (bodyNode.getScroll().y.toFloat()) || 0;

		if (top+menuSize.y>bodySize.y+scrollTop){
			top = top-menuSize.y + size.y+3;
			if (top<0) top = 0;
		}
		
		if (this.subMenu.options.offsetX) left = left + this.subMenu.options.offsetX;
		if (this.subMenu.options.offsetY) top = top + this.subMenu.options.offsetY;
			
		var zIndex = this.menu.node.getStyle("z-index");
		this.subMenu.node.setStyle("z-index", zIndex+1);
		this.subMenu.node.setStyle("top", top);
		this.subMenu.node.setStyle("left", left);
	}
	
});









