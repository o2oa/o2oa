o2.widget = o2.widget || {};
o2.widget.MorphWindow = o2.MW = new Class({
	css: {
		from: {
			"width": "1px",
			"height": "1px",
			"position": "absolute",
			"top": "0px",
			"left": "0px",
			"border-style": "dashed",
			"border-color": "#333",
			"border-width": "1",
			"z-index": "100",
			"opacity": 0
		},
		to: {
			"background-color": "#d5e2f0",
			"border-style": "dashed",
			"border-color": "#333",
			"border-width": "1",
			"font-size": "12px",
			"height": "60",
			"padding": "0",
			"width": "300",
			"position": "absolute",
			"top": "0",
			"left": "0",
			"z-index": "100",
			"opacity": 1
		},
		content: {
			"font-size": "12px",
			"line-height": "22px",
			"color": "#666"
		},
		title: {
			"height": "30",
			"font-size": "12px",
			"border-bottom": "1px dashed #000",
			"line-height": "30px",
			"padding-left": "10px",
			"padding-left": "10px",
			"font-weight": "bold",
			"background-color": "#4579b0",
			"cursor": "move",
			"color": "#ffffff"
			
		},
		titleText:{
			"float": "left"
		},
		titleClose:{
			"float": "right",
			"width": "30",
			"height": "30",
			"cursor": "pointer",
			"background": "url(img/close.png) no-repeat center center"
		},
		mark: {
			"height": $("layout").getSize().y,
			"width": $("layout").getSize().x,
			"opacity": 0.3,
			"position": "absolute",
			"top": "0",
			"left": "0",
			"z-index": "99",
			"border-style": "none",
			"border-width": "0",
			"background-color": "#ddd"
		}
	},
	mark: false,
	initialize: function(title, toWidth, toHeight, fromTop, fromLeft, mark){
		this.mark = mark;
		
		this.css.to.width = toWidth;
		this.css.to.height = toHeight;
		this.css.to.top = fromTop;
		this.css.to.left = fromLeft;
		
		this.css.from.top = fromTop;
		this.css.from.left = fromLeft;
		
		this.node = new Element("div", {
			styles: this.css.from
		}).inject($("layout"));
		
		this.titleNode = new Element("div", {
			styles: this.css.title
		}).inject(this.node);
		
	//	this.containerDrag = new Drag.Move(this.node);
	
		this.titleNode.addEvent("mousedown", function(){
			this.containerDrag = new Drag.Move(this.node);
		}.bind(this));
		this.titleNode.addEvent("mouseup", function(){
			this.node.removeEvents("mousedown");
			this.titleNode.addEvent("mousedown", function(){
				this.containerDrag = new Drag.Move(this.node);
			}.bind(this));
		}.bind(this));
	
		var textNode = new Element("div", {
			text: title,
			styles: this.css.titleText
		}).inject(this.titleNode);
		
//		var closeNode = new Element("div", {
//			styles: this.css.titleClose
//		}).inject(this.titleNode);
		
		var mw = this;
//		closeNode.addEvent("click", function(){
//			mw.hide.apply(mw);
//		});
		
		this.css.content.height = this.css.to.height - this.css.title.height;
		this.contentNode = new Element("div", {
			styles: this.css.content
		}).inject(this.node);
	},
	
	show: function(){
		var mw = this;
		this._markShow();
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.queryShow()){
			this.morph.start(this.css.to).chain(function(){
				mw.postShow.apply(mw);
			});
		}
	},
	
	hide: function() {
		var mw = this;
		if (!this.morph){
			this.morph = new Fx.Morph(this.node, {duration: 200});
		}
		if (this.queryHide()){
			this.morph.start(this.css.from).chain(function(){
				mw._markHide();
				mw.postHide.apply(mw);
			});
		}
	},
	
	_markShow: function(){
		if (this.mark){
			if (!this.markNode){
				this.markNode = new Element("iframe", {
					styles: this.css.mark
				}).inject($("layout"));
			}
			this.markNode.setStyle("display", "block");
		}
	},
	
	_markHide: function(){
		if (this.mark){
			if (!this.markNode){
				this.markNode = new Element("iframe", {
					styles: this.css.mark
				}).inject($("layout"));
			}
			this.markNode.setStyle("display", "none");
		}
	},
	
	postShow: function(){
		return true;
	},
	postHide: function() {
		return true;
	},
	queryShow: function(){
		return true;
	},
	queryHide: function() {
		return true;
	}
});