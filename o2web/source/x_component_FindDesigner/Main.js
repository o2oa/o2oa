MWF.xApplication.FindDesigner.options.multitask = false;
MWF.xApplication.FindDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "FindDesigner",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"isResize": true,
		"isMax": true,
		"layoutType": "leftRight",
		"title": MWF.xApplication.FindDesigner.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.FindDesigner.LP;
		this.filterOption = {
			"keyword": "",
			"type": [],
			"caseSensitive": false,
			"matchWholeWord": false,
			"matchRegExp": false,
			"moduleList": []
		}
		this.heightPercent = {
			"list": 0.4,
			"preview": 0.6
		}
	},
	loadApplication: function(callback){
		var url = this.path+this.options.style+"/view.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.setSizeNode();

			if (callback) callback();
		}.bind(this));
	},
	initLayout: function(){
		if (this.options.layoutType=="leftRight"){
			this.listNode.setStyles({
				"height":"100%",
				"width": "auto"
			});
			this.previewNode.setStyles({
				"height": "100%",
				"margin-left": ""
			});
			this.previewContentNode.setStyle("height", "auto");
		}else {
			this.listNode.setStyles({
				"height":"auto",
				"width": "auto"
			});
			this.previewNode.setStyles({
				"height": "auto",
				"margin-left": ""
			});
			this.previewContentNode.setStyle("height", "auto");
		}
	},
	setSizeNode: function(){
		debugger;
		this.initLayout();
		this["sizeNode_"+this.options.layoutType]();
		this["setResizeNode_"+this.options.layoutType]();

		this.sizeNodeFun = this["sizeNode_"+this.options.layoutType].bind(this);
		this.addEvent("resize", this.sizeNodeFun);
	},
	sizeResultNode: function(){
		var size = this.content.getSize();
		var filterSzie = this.filterNode.getSize();
		var keywordSize = this.keywordNode.getSize();
		var rangeSize = this.rangeNode.getSize();
		var h = size.y-filterSzie.y-keywordSize.y-rangeSize.y;
		this.resultNode.setStyle("height", ""+h+"px");
		return h;
	},
	sizeNode_topBottom: function(){
		var h = this.sizeResultNode();

		var listHeight = h*this.heightPercent.list;
		this.listNode.setStyle("height", ""+listHeight+"px");
		var previewHeight = h*this.heightPercent.preview;;
		this.previewNode.setStyle("height", ""+previewHeight+"px");

		var previewSeparatorSize = this.previewSeparatorNode.getSize();
		var previewTitleSize = this.previewTitleNode.getSize();
		var previewContentHeight = previewHeight - previewSeparatorSize.y - previewTitleSize.y;
		this.previewContentNode.setStyle("height", ""+previewContentHeight+"px");
	},
	sizeNode_leftRight: function(){
		var h = this.sizeResultNode();
		var w = this.resultNode.getSize().x;

		var listWidth = w*this.heightPercent.list;
		this.listNode.setStyle("width", ""+listWidth+"px");
		this.previewNode.setStyle("margin-left", ""+listWidth+"px");

		//var previewSeparatorSize = this.previewSeparatorNode.getSize();
		var previewTitleSize = this.previewTitleNode.getSize();
		var previewContentHeight = h - previewTitleSize.y;
		this.previewContentNode.setStyle("height", ""+previewContentHeight+"px");
	},

	setResizeNode_topBottom: function(){
		if (this.previewSeparatorNode){
			this.resizeDrag = new Drag(this.previewSeparatorNode, {
				"onStart": function(el, e){
					el.store("position", o2.eventPosition(e));
					el.store("initialSize", this.listNode.getSize());
				}.bind(this),
				"onDrag": function(el, e){
					var p = o2.eventPosition(e);
					var position = el.retrieve("position");
					var initialSize = el.retrieve("initialSize");
					var dy = position.y.toFloat()-p.y.toFloat();
					var height = initialSize.y-dy;

					var size = this.resultNode.getSize();
					this.heightPercent.list = height/size.y;
					if (this.heightPercent.list<0.1) this.heightPercent.list = 0.1;
					if (this.heightPercent.list>0.85) this.heightPercent.list = 0.85;
					this.heightPercent.preview = 1-this.heightPercent.list;
					this.sizeNode_topBottom();
				}.bind(this)
			});
		}
	},
	setResizeNode_leftRight: function(){
		if (this.previewSeparatorNode){
			this.resizeDrag = new Drag(this.previewSeparatorNode, {
				"onStart": function(el, e){
					el.store("position", o2.eventPosition(e));
					el.store("initialSize", this.listNode.getSize());
				}.bind(this),
				"onDrag": function(el, e){
					var p = o2.eventPosition(e);
					var position = el.retrieve("position");
					var initialSize = el.retrieve("initialSize");
					var dx = position.x.toFloat()-p.x.toFloat();
					var width = initialSize.x-dx;

					var size = this.resultNode.getSize();
					this.heightPercent.list = width/size.x;
					if (this.heightPercent.list<0.1) this.heightPercent.list = 0.1;
					if (this.heightPercent.list>0.85) this.heightPercent.list = 0.85;
					this.heightPercent.preview = 1-this.heightPercent.list;
					this.sizeNode_leftRight();
				}.bind(this)
			});
		}
	},

	checkFilter: function(e){
		if (e.target.hasClass("o2_findDesigner_filterNode_item")) e.target.getElement("input").click();
		e.stopPropagation();
	},
	overKeywordOption: function(e){
		if (e.target.hasClass("o2_findDesigner_keywordNode_optionItem")){
			if (!e.target.hasClass("optionItem_over")) e.target.addClass("optionItem_over");
		}
	},
	outKeywordOption: function(e){
		if (e.target.hasClass("o2_findDesigner_keywordNode_optionItem")) e.target.removeClass("optionItem_over");
	},

	setCaseSensitive: function(e){
		this.filterOption.caseSensitive = !this.filterOption.caseSensitive;
		this.caseSensitiveNode.removeClass("caseSensitiveNode_"+!this.filterOption.caseSensitive);
		this.caseSensitiveNode.addClass("caseSensitiveNode_"+this.filterOption.caseSensitive);
	},
	setMatchWholeWord: function(e){
		this.filterOption.matchWholeWord = !this.filterOption.matchWholeWord;
		this.matchWholeWordNode.removeClass("matchWholeWordNode_"+!this.filterOption.matchWholeWord);
		this.matchWholeWordNode.addClass("matchWholeWordNode_"+this.filterOption.matchWholeWord);
	},
	setMatchRegExp: function(e){
		this.filterOption.matchRegExp = !this.filterOption.matchRegExp;
		this.matchRegExpNode.removeClass("matchRegExpNode_"+!this.filterOption.matchRegExp);
		this.matchRegExpNode.addClass("matchRegExpNode_"+this.filterOption.matchRegExp);
	},
	changeLayout: function(){
		if (this.options.layoutType=="leftRight"){
			this.listNode.addClass("listNode");
			this.previewNode.addClass("previewNode");
			this.previewSeparatorNode.addClass("previewNode_separator");
			this.previewTitleNode.addClass("previewNode_title");
			this.previewTitleActionNode.addClass("previewNode_title_action");
			this.previewContentNode.addClass("previewNode_content");

			this.listNode.removeClass("listNode_lr");
			this.previewNode.removeClass("previewNode_lr");
			this.previewSeparatorNode.removeClass("previewNode_separator_lr");
			this.previewTitleNode.removeClass("previewNode_title_lr");
			this.previewTitleActionNode.removeClass("previewNode_title_action_lr");
			this.previewContentNode.removeClass("previewNode_content_lr");

			this.options.layoutType="topBottom";
		}else{
			this.listNode.removeClass("listNode");
			this.previewNode.removeClass("previewNode");
			this.previewSeparatorNode.removeClass("previewNode_separator");
			this.previewTitleNode.removeClass("previewNode_title");
			this.previewTitleActionNode.removeClass("previewNode_title_action");
			this.previewContentNode.removeClass("previewNode_content");

			this.listNode.addClass("listNode_lr");
			this.previewNode.addClass("previewNode_lr");
			this.previewSeparatorNode.addClass("previewNode_separator_lr");
			this.previewTitleNode.addClass("previewNode_title_lr");
			this.previewTitleActionNode.addClass("previewNode_title_action_lr");
			this.previewContentNode.addClass("previewNode_content_lr");

			this.options.layoutType="leftRight";
		}
		if (this.sizeNodeFun) this.removeEvent("resize", this.sizeNodeFun);
		this.setSizeNode();
	}
});
