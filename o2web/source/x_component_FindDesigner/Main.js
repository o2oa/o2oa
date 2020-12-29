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
		"layout": {
			"type": "leftRight",
			"percent": 0.3
		},
		"title": MWF.xApplication.FindDesigner.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.FindDesigner.LP;
		this.filterOption = {
			"keyword": "",
			"designerTypes": [],
			"caseSensitive": false,
			"matchWholeWord": false,
			"matchRegExp": false,
			"moduleList": []
		}
		this.selectedModules = [];
		this.selectedRange = [];
		o2.UD.getDataJson("findDesignerLayout", function(json){
			this.setOptions(json);
		}.bind(this), false);
	},
	loadApplication: function(callback){
		var url = this.path+this.options.style+"/view.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.setSizeNode();

			if (callback) callback();
		}.bind(this));
	},
	initLayout: function(){
		this.listNode.set("style", "");
		this.previewNode.set("style", "");
		if (this.resizeDrag) this.resizeDrag.detach();
		if (this.sizeNodeFun) this.removeEvent("resize", this.sizeNodeFun);

		if (this.options.layout.type=="leftRight"){
			this.toLeftRight();
		}else{
			this.toTopBottom();
		}
	},
	setSizeNode: function(){
		this.initLayout();
		this["sizeNode_"+this.options.layout.type]();
		this["setResizeNode_"+this.options.layout.type]();

		this.sizeNodeFun = null;
		this.sizeNodeFun = this["sizeNode_"+this.options.layout.type].bind(this);
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

		var listHeight = h*this.options.layout.percent;
		this.listNode.setStyle("height", ""+listHeight+"px");
		var previewHeight = h*(1-this.options.layout.percent);
		this.previewNode.setStyle("height", ""+previewHeight+"px");

		var previewSeparatorSize = this.previewSeparatorNode.getSize();
		var previewTitleSize = this.previewTitleNode.getSize();
		var previewContentHeight = previewHeight - previewSeparatorSize.y - previewTitleSize.y;
		this.previewContentNode.setStyle("height", ""+previewContentHeight+"px");
	},
	sizeNode_leftRight: function(){
		var h = this.sizeResultNode();
		var w = this.resultNode.getSize().x;

		var listWidth = w*this.options.layout.percent;
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
					this.options.layout.percent = height/size.y;
					if (this.options.layout.percent<0.1) this.options.layout.percent = 0.1;
					if (this.options.layout.percent>0.85) this.options.layout.percent = 0.85;
					this.sizeNode_topBottom();
				}.bind(this),
				"onComplete": function(){
					o2.UD.putData("findDesignerLayout", {"layout": this.options.layout});
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
					this.options.layout.percent = width/size.x;
					if (this.options.layout.percent<0.1) this.options.layout.percent = 0.1;
					if (this.options.layout.percent>0.85) this.options.layout.percent = 0.85;

					this.sizeNode_leftRight();
				}.bind(this),
				"onComplete": function(){
					o2.UD.putData("findDesignerLayout", {"layout": this.options.layout});
				}.bind(this)
			});
		}
	},

	checkFilter: function(e){
		if (e.target.hasClass("filterNode_item")) e.target.getElement("input").click();
		e.stopPropagation();
	},
	checkRange: function(e){
		if (e.target.hasClass("rangeType_Item")) e.target.getElement("input").click();
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

	layoutAddClass: function(flag){
		flag = flag || "";
		this.listNode.addClass("listNode"+flag);
		this.previewNode.addClass("previewNode"+flag);
		this.previewSeparatorNode.addClass("previewNode_separator"+flag);
		this.previewTitleNode.addClass("previewNode_title"+flag);
		this.previewTitleActionNode.addClass("previewNode_title_action"+flag);
		this.previewContentNode.addClass("previewNode_content"+flag);
	},

	layoutRemoveClass: function(flag){
		flag = flag || "";
		this.listNode.removeClass("listNode"+flag);
		this.previewNode.removeClass("previewNode"+flag);
		this.previewSeparatorNode.removeClass("previewNode_separator"+flag);
		this.previewTitleNode.removeClass("previewNode_title"+flag);
		this.previewTitleActionNode.removeClass("previewNode_title_action"+flag);
		this.previewContentNode.removeClass("previewNode_content"+flag);
	},
	toLeftRight: function(){
		this.layoutAddClass("_lr");
		this.layoutRemoveClass();
		this.options.layout.type="leftRight";
	},
	toTopBottom: function(){
		this.layoutAddClass();
		this.layoutRemoveClass("_lr");
		this.options.layout.type="topBottom";
	},
	changeLayout: function(){
		if (this.options.layout.type=="leftRight"){
			this.options.layout.type="topBottom";
		}else{
			this.options.layout.type="leftRight";
		}
		this.setSizeNode();
		o2.UD.putData("findDesignerLayout", {"layout": this.options.layout});
	},

	getSelectedRange: function(){
		this.selectedRange = [];
		var rangeInputs = this.rangeContentNode.getElements("input");
		rangeInputs.each(function(input){
			if (input.checked) this.selectedRange.push(input.get("value"));
		}.bind(this));
	},

	setSelectedRange: function(){
		if (this.selectedRange && this.selectedRange.length){
			var rangeInputs = this.rangeContentNode.getElements("input");
			rangeInputs.each(function(input){
				if (this.selectedRange.indexOf(input.get("value"))!=-1) input.set("checked", true);
			}.bind(this));
		}
	},
	removeRangeItem: function(item){
		item.destroy();
		var itemNodes = this.rangeSelectedContentNode.getChildren();
		if (!itemNodes.length) this.setSelectedRange();
	},
	selectFindRange: function(){
		o2.requireApp("Selector", "package", function(){
			new o2.O2Selector(this.content, {
				"values": this.selectedModules,
				"type": "PlatApp",
				"selectAllEnable": true,
				"onComplete": function(list){
					this.rangeSelectedContentNode.empty();
					//this.selectedModules = [];
					if (list.length){
						this.getSelectedRange();
						this.rangeContentNode.getElements("input").set("checked", false);

						o2.require("o2.widget.O2Identity", function(){
							list.each(function(app){
								//this.selectedModules.push(app.data);
								app.data.name = this.lp.service + "-" + app.data.name;
								var item = new o2.widget.O2Other(app.data, this.rangeSelectedContentNode, {"canRemove": true, "style": "find", "onRemove": function(item){this.removeRangeItem(item);}.bind(this)});
								item.node.store("data", item.data);
							}.bind(this));
						}.bind(this));

					}else{
						this.setSelectedRange();
					}

				}.bind(this)
			});
		}.bind(this));
	},
	getFindOption: function(){
		var filterTypes = [];
		filterItems = this.filterNode.getElements("input");
		filterItems.each(function(item){
			if (item.checked) filterTypes.push(item.get("value"));
		}.bind(this));

		var keyword = this.keywordInputNode.get("value");

		var moduleList = [];
		var itemNodes = this.rangeSelectedContentNode.getChildren();
		if (!itemNodes.length){
			this.getSelectedRange();
			this.selectedRange.each(function(type){
				moduleList.push({"moduleType": type, "flagList": []});
			});
		}else{
			var rangeApp = {};
			itemNodes.each(function(node){
				var data = node.retrieve("data");
				if (!rangeApp[data.moduleType]) rangeApp[data.moduleType] = [];
				rangeApp[data.moduleType].push(data.id);
			}.bind(this));

			Object.keys(rangeApp).each(function(k){
				moduleList.push({"moduleType": k, "flagList": rangeApp[k]});
			});
		}

		this.filterOption.keyword = keyword;
		this.filterOption.designerTypes = filterTypes;
		this.filterOption.moduleList = moduleList;

		return this.filterOption;
	}
});
