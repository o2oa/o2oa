MWF.xDesktop = MWF.xDesktop || {};
MWF.require("MWF.widget.Menu", null, false);
MWF.xDesktop.Menu = new Class({
	Extends: MWF.widget.Menu,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"event": "contextmenu",
		"disable": false,
		"top": -1,
		"left": -1,
        "container": null,
		"where": {"x": "left", "y": "bottom"}
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			this.node = new Element("div#menu");
			this.node.set("styles", this.css.container);
			
			if (this.options.event){
				if (this.target) this.target.addEvent(this.options.event, this.showIm.bind(this));
			}
			this.borderNode = new Element("div.MWFMenu", {
				"styles": this.css.borderNode
			}).inject(this.options.container || $(document.body));
			
			this.node.inject(this.borderNode);

			this.hide = this.hideMenu.bind(this);
			this.fireEvent("postLoad");
		}
	},
	showIm: function(e){
		if (!this.options.disable){
			this.hide = this.hideIm.bind(this);
			if (this.fireEvent("queryShow", [e])){
				this.tmpBodyOncontextmenu = document.body.oncontextmenu;
				document.body.oncontextmenu = function(){return false;};
				if (this.pauseCount<=0){
					this.setItemWidth();
					
					var i = MWF.xDesktop.zIndexPool.zIndex;
					
					this.borderNode.setStyles({
						"display": "block",
						"opacity": this.options.opacity || 1,
						"z-index": i
					});
					
					this.setPosition(e);
					
					$(document.body).removeEvent("mousedown", this.hide);
					$(document.body).addEvent("mousedown", this.hide);
					
					this.show = true;
				}else{
					this.pauseCount--;
				}

				var p = this.node.getPosition(document.body);
				var size = this.node.getSize();
				var bodySize = document.body.getSize();
				if (p.y+size.y+10>bodySize.y){
					var y = bodySize.y-p.y-10;
					this.node.setStyle("height", ""+y+"px");
					this.node.addEvent("mousedown", function(e){ e.stopPropagation(); })
				}

				this.fireEvent("postShow");
			}
		}
	},
	hideIm: function(all){
		if (this.fireEvent("queryHide")){
			$(document.body).removeEvent("mousedown", this.hide);
			this.borderNode.set("styles", {
				"display": "none",
				"opacity": 0
			});
			this.show = false;
			document.body.oncontextmenu = this.tmpBodyOncontextmenu;
			this.tmpBodyOncontextmenu = null;
			
			if (all) if (this.topMenu) this.topMenu.hideIm();
			
			this.fireEvent("postHide");
		}
	},
	setPosition: function(e){
		var position = this.target.getPosition(this.target.getOffsetParent());
		var size = this.target.getSize();
        this.borderNode.show();
        var nodeSize = this.borderNode.getSize();

        var left=0, top=0;
        switch (this.options.where.x.toLowerCase()){
			case "right":
                left = position.x-nodeSize.x+size.x;
				break;
			default:
                left = position.x-0;
		}
        switch (this.options.where.y.toLowerCase()){
            case "top":
                top = position.y-nodeSize.y;
                break;
            default:
                top = position.y+size.y;
        }
		//(this.options.where)


		if (this.options.offsetY) top = top+this.options.offsetY;
		if (this.options.offsetX) left = left+this.options.offsetX;

        var bodySize = $(document.body).getSize();
        var borderSize = this.borderNode.getSize();
        if (left+borderSize.x>bodySize.x) left = bodySize.x-borderSize.x-10;
		
		this.borderNode.setStyle("top", top);
		this.borderNode.setStyle("left", left);
	}
});









