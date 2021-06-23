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
		"filter": null,
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
			this.createToolbar();
debugger;
			this.initFilter();

			if (callback) callback();
		}.bind(this));
	},
	initFilter: function(){
		if (this.options.filter){
			if (this.options.filter.moduleList){
				var inputs = this.rangeContentNode.getElements("input");
				inputs.forEach(function(input){
					if (input.get("value").indexOf(this.options.filter.moduleList) !== -1){
						input.set("checked", true);
					}else{
						input.set("checked", false);
					}
				}.bind(this));
			}
			if (this.options.filter.appList){
				o2.require("o2.widget.O2Identity", function(){
					this.options.filter.appList.each(function(app){
						app.name = this.lp[app.moduleType] + "-" + app.name;
						var item = new o2.widget.O2Other(app, this.rangeSelectedContentNode, {"canRemove": true, "style": "find", "onRemove": function(item){this.removeRangeItem(item);}.bind(this)});
						item.node.store("data", item.data);
					}.bind(this));
				}.bind(this));
			}
		}
	},
	createToolbar: function(){
		o2.require("o2.widget.Toolbar", function(){
			this.previewToolbar = new o2.widget.Toolbar(this.previewTitleToolbar, {"style": "findDesigner"}, this);
			this.previewToolbar.load();
			this.previewToolbar.childrenButton[0].disable();
			this.previewToolbar.childrenButton[1].disable();
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

		var listTitleSize = this.listTitleNode.getSize();
		var listContentHeight = listHeight - listTitleSize.y;
		this.listAreaNode.setStyle("height", ""+listContentHeight+"px");

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

		var listTitleSize = this.listTitleNode.getSize();
		var listContentHeight = h - listTitleSize.y;
		this.listAreaNode.setStyle("height", ""+listContentHeight+"px");

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

					if (this.editor) if(this.editor.resize) this.editor.resize();
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

					if (this.editor) if(this.editor.resize) this.editor.resize();
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
	selectFindRange: function(loadFun){
		debugger;
		o2.requireApp("Selector", "package", function(){
			new o2.O2Selector(this.content, {
				"values": this.selectedModules,
				"type": "PlatApp",
				"selectAllEnable": true,
				"onLoad": function(){
					if (loadFun && o2.typeOf(loadFun)=="function") loadFun();
				},
				"onComplete": function(list){
					this.rangeSelectedContentNode.empty();
					//this.selectedModules = [];
					if (list.length){
						this.getSelectedRange();
						this.rangeContentNode.getElements("input").set("checked", false);

						o2.require("o2.widget.O2Identity", function(){
							list.each(function(app){
								//this.selectedModules.push(app.data);
								debugger;
								app.data.name = this.lp[app.data.moduleType] + "-" + app.data.name;
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
				rangeApp[data.moduleType].push({"id": data.id});
			}.bind(this));

			Object.keys(rangeApp).each(function(k){
				moduleList.push({"moduleType": k, "flagList": rangeApp[k]});
			});
		}

		this.filterOption.keyword = keyword;
		this.filterOption.designerTypes = filterTypes;
		this.filterOption.moduleList = moduleList;

		return this.filterOption;
	},

	checkFindDesigner: function(e){
		if (e.keyCode===13){
			this.findDesigner();
		}
	},

	getFindWorker: function(){
		if (!this.findWorker) this.findWorker = new Worker("../x_component_FindDesigner/FindWorker.js");
		this.findWorker.onmessage = function(e) {
			if (e.data && e.data.type=="receive") this.setReceiveMessage();
			if (e.data && e.data.type=="ready") this.setReadyMessage(e.data);
			if (e.data && e.data.type=="done") this.doFindOptionResult(e.data);
			if (e.data && e.data.type=="find") this.doFindResult(e.data);
			if (e.data && e.data.type=="completed") this.doFindCompleted(e.data);
		}.bind(this);
	},
	doFindCompleted: function(){
		if (!this.tree || !this.tree.children.length){
			//not find
			this.listContentNode.hide();
			this.listContentNode.empty();
			this.listInfoNode.removeClass("loadding");
			this.listInfoNode.show().getFirst().set("text", this.lp.nothingFind);
		}

		if (!this.patternCount) this.patternCount = 0;
		var t = this.lp.findPatternCountCompleted.replace("{n}", this.patternCount);
		this.listTitleInfoNode.set("text", t);
	},
	doFindOptionResult: function(){
		// this.listInfoNode.hide();
		// this.listContentNode.show();
		// moduleNode = this.createResultCategroyItem("xxxx", "", this.tree);
		this.findOptionModuleProcessed++;
		this.updateFindProgress();
	},
	doFindResult: function(data){
		debugger
		if (data.data) this.showFindResult(data.data, data.option);
	},

	getResultTree: function(callback){
		if (!this.tree){
			o2.require("o2.widget.Tree", function(){
				this.tree = new o2.widget.Tree(this.listContentNode, {
					"style": "findDesigner"
					// "onQueryExpand": function(item){
					// 	if (item.designer) this.loadDesignerPattern(item);
					// }.bind(this)
				});
				this.tree.load();
				if (callback) callback();
			}.bind(this), null, false);
		}else{
			if (callback) callback();
		}
	},
	createResultCategroyItem: function(text, title, tree){
		var obj = {
			"title": title,
			"text": "<span style='font-weight: bold'>"+text+"</span>",
			"icon": ""
		}
		return tree.appendChild(obj);
	},
	createResultAppItem: function(text, title, tree){
		var obj = {
			"title": title,
			"text": "<span style='font-weight: bold; color: #0b58a2'>"+text+"</span>",
			"icon": ""
		}
		return tree.appendChild(obj);
	},
	createResultTypeItem: function(text, title, tree){
		var obj = {
			"title": title,
			"text": "<span style='color: #333333'>"+text+"</span>",
			"icon": ""
		}
		return tree.appendChild(obj);
	},
	createResultDesignerItem: function(text, title, tree){
		var obj = {
			"expand": false,
			"title": title,
			"text": "<span style='color: #333333'>"+text+"</span>",
			"icon": ""
		}
		return tree.appendChild(obj);
	},
	createResultPatternItem: function(text, title, tree, icon, action, treeNode){
		var obj = {
			"title": title,
			"text": "<span style='color: #000000'>"+text+"</span>",
			"icon": icon||"",
			"action": action || null
		}

		var node = (treeNode) ? treeNode.insertChild(obj) : tree.appendChild(obj);
		node.addEvent("unselect", function(){
			if (this.editor){
				if (this.editor.pattern.designerType === "script"){
					if (this.scriptDesignerDataObject && this.scriptDesignerDataObject[this.editor.pattern.designerId]){
						this.scriptDesignerDataObject[this.editor.pattern.designerId].text = this.editor.getValue();
					}
				}else{
					debugger;
					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]){
						var d = this.designerDataObject[this.editor.pattern.designerId];
						switch (this.editor.pattern.pattern.propertyType){
							case "duty":
								if (this.editor.pattern.pattern.path){
									var path = this.editor.pattern.pattern.path;
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.parse(d[path[i]]);
											d = d[path[i]];
										}else{
											d = d[path[i]];
										}
									}
									d[path[path.length-1]] = this.editor.getValue();

									d = this.designerDataObject[this.editor.pattern.designerId];
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.stringify(d[path[i]]);
											break;
										}else{
											d = d[path[i]];
										}
									}
								}
								break;
							default:
								if (this.editor.getValue){
									if (this.editor.pattern.pattern.path){
										var path = this.editor.pattern.pattern.path;
										for (var i=0; i<path.length-1; i++){
											d = d[path[i]];
										}
									}
									if (path[path.length-1]=="styles"){
										d["recoveryStyles"] = this.editor.getValue();
									}else if (path[path.length-1]=="inputStyles"){
										d["recoveryInputStyles"] = this.editor.getValue();
									}
									d[path[path.length-1]] = this.editor.getValue();
								}
						}

					}
				}
				this.editor.destroy();
				this.editor = null;
				this.previewInforNode.show().inject(this.previewContentNode);
			}
		}.bind(this));
		return node;
	},

	updatePatternCount: function(node){
		node.patternCount++;
		var textDivNode = node.textNode.getElement("div");
		if (textDivNode){
			var text = this.lp.patternCount.replace("{n}", node.patternCount)
			var t = node.options.text+"&nbsp;&nbsp;<span style='color: #666666'>("+text+")</span>>";
			//var html = item.options.text;
			textDivNode.set("html", t);
		}
	},
	subPatternCount: function(node){
		node.patternCount--;
		if (node.patternCount<0) node.patternCount=0;
		var textDivNode = node.textNode.getElement("div");
		if (textDivNode){
			var text = this.lp.patternCount.replace("{n}", node.patternCount)
			var t = node.options.text+"&nbsp;&nbsp;<span style='color: #666666'>("+text+")</span>>";
			//var html = item.options.text;
			textDivNode.set("html", t);
		}
	},
	addResultTitle: function(){
		if (!this.patternCount) this.patternCount = 0;
		this.patternCount++;
		var t = this.lp.findPatternCount.replace("{n}", this.patternCount);
		this.listTitleInfoNode.set("text", t);
	},
	subResultTitle: function(){
		if (!this.patternCount) this.patternCount = 1;
		this.patternCount--;
		var t = this.lp.findPatternCount.replace("{n}", this.patternCount);
		this.listTitleInfoNode.set("text", t);
	},
	showFindResult: function(data,option, treeNode){
		this.addResultTitle();

		this.listInfoNode.hide();
		this.listContentNode.show();

		var regexp = this.getFilterOptionRegex(option);
		var moduleNode = (this.tree.modules) ? this.tree.modules[data.module] : null;
		if (!moduleNode){
			moduleNode = this.createResultCategroyItem(this.lp[data.module], this.lp[data.module], this.tree);
			moduleNode.patternCount = 0;
			if (!this.tree.modules) this.tree.modules = {};
			this.tree.modules[data.module] = moduleNode;
		}
		this.updatePatternCount(moduleNode);
debugger;
		var appNode = (moduleNode.apps) ? moduleNode.apps[data.appId] : null;
		if (!appNode){
			appNode = this.createResultAppItem(data.appName, data.appName, moduleNode);
			appNode.patternCount = 0;
			if (!moduleNode.apps) moduleNode.apps = {};
			moduleNode.apps[data.appId] = appNode;
		}
		this.updatePatternCount(appNode);

		var typeNode = (appNode.types) ? appNode.types[data.designerType] : null;
		if (!typeNode){
			typeNode = this.createResultTypeItem(this.lp[data.designerType], this.lp[data.designerType], appNode);
			typeNode.patternCount = 0;
			if (!appNode.types) appNode.types = {};
			appNode.types[data.designerType] = typeNode;
		}
		this.updatePatternCount(typeNode);

		var designerNode = (typeNode.designers) ? typeNode.designers[data.designerId] : null;
		if (!designerNode){
			designerNode = this.createResultDesignerItem(data.designerName, data.designerName, typeNode);
			designerNode.patternCount = 0;
			if (!typeNode.designers) typeNode.designers = {};
			typeNode.designers[data.designerId] = designerNode;
		}
		this.updatePatternCount(designerNode);

		switch (data.designerType){
			case "script":
				this.createScriptPatternNode(data, designerNode, regexp, treeNode);
				break;
			case "form":
			case "page":
			case "widget":
			case "view":
			case "statement":
			case "process":
				this.createFormPatternNode(data, designerNode, regexp, treeNode);
				break;
			// case "process":
			// 	this.createProcessPatternNode(data, designerNode, regexp, treeNode);
			// 	break;

		}
	},

	// createProcessPatternNode: function(data, node, regexp){
	// 	var text = this.lp.elementPattern.replace("{element}", "&lt;"+data.pattern.type+"&gt;"+data.pattern.name).
	// 	replace("{property}", "{"+data.pattern.key+"}"+data.pattern.propertyName);
	// 	text = "<span style='color: #666666'>"+text+"</span>&nbsp;&nbsp;"
	//
	// 	if (data.pattern.line){
	// 		if (data.pattern.evkey){
	// 			text += "<b>["+data.pattern.evkey+"]</b>&nbsp;"+((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp);
	// 		}else{
	// 			text += ((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp);
	// 		}
	// 	}else{
	// 		text += this.getPatternValue(data.pattern.value, regexp);
	// 	}
	// 	if (data.pattern.mode){
	// 		text = "<b>["+data.pattern.mode+"]</b>&nbsp;"+text;
	// 	}
	//
	// 	patternNode = this.createResultPatternItem(text, "", node, "icon_"+data.pattern.propertyType+".png");
	// },

	getPatternValueText: function(data, regexp){
		var text = "";
		if (data.pattern.line){
			if (data.pattern.evkey){
				text += "<b>["+data.pattern.evkey+"]</b>&nbsp;"+((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp, data.pattern);
			}else{
				text += ((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp, data.pattern);
			}
		}else{
			text += this.getPatternValue(data.pattern.value, regexp, data.pattern);
		}
		return text;
	},
	getFormPatternNodeText: function(data, regexp){
		var text = this.lp.elementPattern.replace("{element}", "&lt;"+data.pattern.type+"&gt;"+data.pattern.name).
		replace("{property}", "{"+data.pattern.key+"}"+data.pattern.propertyName);
		text = "<span style='color: #666666'>"+text+"</span>&nbsp;&nbsp;";

		text += this.getPatternValueText(data, regexp);
		// if (data.pattern.line){
		// 	if (data.pattern.evkey){
		// 		text += "<b>["+data.pattern.evkey+"]</b>&nbsp;"+((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp, data.pattern);
		// 	}else{
		// 		text += ((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp, data.pattern);
		// 	}
		// }else{
		// 	text += this.getPatternValue(data.pattern.value, regexp, data.pattern);
		// }
		if (data.pattern.mode){
			text = "<b>["+data.pattern.mode+"]</b>&nbsp;"+text;
		}
		return text;
	},
	createFormPatternNode: function(data, node, regexp, treeNode){
		var text = this.getFormPatternNodeText(data, regexp)

		var openScript = function(node){
			this.openPatternForm(node);
		}.bind(this);

		patternNode = this.createResultPatternItem(text, "", node, "icon_"+data.pattern.propertyType+".png", openScript, treeNode);
		patternNode.pattern = data;
	},

	getPatternValue: function(value, regexp, pattern){
		regexp.lastIndex = 0;
		var valueHtml = "";
		var idx = 0;
		while ((arr = regexp.exec(value)) !== null) {
			if (pattern){
				if (!pattern.cols) pattern.cols = [];
				pattern.cols.push({"start": arr.index+1, "end": regexp.lastIndex+1});
			}
			valueHtml += o2.common.encodeHtml(value.substring(idx, arr.index));
			valueHtml += "<span style='background-color: #ffef8f'>"+o2.common.encodeHtml(value.substring(arr.index, regexp.lastIndex))+"</span>";
			idx = regexp.lastIndex;
		}
		valueHtml += o2.common.encodeHtml(value.substring(idx, value.length));
		return valueHtml;
	},
	createScriptPatternNode: function(data, node, regexp){
		var patternNode;
		var text;

		var openScript = function(node){
			this.openPatternScript(node);
		}.bind(this);

		if (data.pattern.property=="text"){
			text = "<span style='color: #666666'>"+data.pattern.line+"</span>&nbsp;&nbsp;"+this.getPatternValue(data.pattern.value, regexp, data.pattern);
			patternNode = this.createResultPatternItem(text, "", node, "icon_script.png", openScript);
		}else{
			text = this.lp.property+":&nbsp;<b>"+data.pattern.property+"</b> "+this.lp.value+":&nbsp;"+this.getPatternValue(data.pattern.value, regexp);
			patternNode = this.createResultPatternItem(text, "", node, "icon_text.png", openScript);
		}
		patternNode.pattern = data;
	},

	openPatternForm: function(node){
		var pattern = node.pattern;

		if (this.editor && this.editor.pattern.designerId === node.pattern.designerId && this.editor.pattern.module === node.pattern.module){
			this.resetFormEditor(node.pattern);
		}else{
			if (this.editor){
				if (this.editor.pattern.designerType === "script"){
					if (this.scriptDesignerDataObject && this.scriptDesignerDataObject[this.editor.pattern.designerId]){
						this.scriptDesignerDataObject[this.editor.pattern.designerId].text = this.editor.getValue();
					}
				}else{

					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]){
						var d = this.designerDataObject[this.editor.pattern.designerId];
						switch (this.editor.pattern.pattern.propertyType){
							case "duty":
								if (this.editor.pattern.pattern.path){
									var path = this.editor.pattern.pattern.path;
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.parse(d[path[i]]);
											d = d[path[i]];
										}else{
											d = d[path[i]];
										}
									}
									d[path[path.length-1]] = this.editor.getValue();

									d = this.designerDataObject[this.editor.pattern.designerId];
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.stringify(d[path[i]]);
											break;
										}else{
											d = d[path[i]];
										}
									}
								}
								break;
							default:
								if (this.editor.getValue){
									if (this.editor.pattern.pattern.path){
										var path = this.editor.pattern.pattern.path;
										for (var i=0; i<path.length-1; i++){
											d = d[path[i]];
										}
									}
									if (path[path.length-1]=="styles"){
										d["recoveryStyles"] = this.editor.getValue();
									}else if (path[path.length-1]=="inputStyles"){
										d["recoveryInputStyles"] = this.editor.getValue();
									}
									d[path[path.length-1]] = this.editor.getValue();
								}
						}

						// if (this.editor.pattern.pattern.path){
						// 	var path = this.editor.pattern.pattern.path;
						// 	for (var i=0; i<path.length-1; i++){
						// 		d = d[path[i]];
						// 	}
						// }
						// d[path[path.length-1]] = this.editor.getValue();
					}
				}
				this.editor.destroy();
			}
			this.editor = null;
			this.previewInforNode.hide().dispose();

			var m;
			switch (node.pattern.module){
				case "processPlatform":
					if (node.pattern.designerType==="form"){
						m = o2.Actions.load("x_processplatform_assemble_designer").FormAction.get;
						break;
					}
					if (node.pattern.designerType==="process"){
						m = o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.get;
						break;
					}
					break;
				case "cms":
					m = o2.Actions.load("x_cms_assemble_control").FormAction.get;
					//m = o2.Actions.load("x_cms_assemble_control").ScriptAction.get;
					break;
				case "portal":
					if (node.pattern.designerType==="page"){
						m = o2.Actions.load("x_portal_assemble_designer").PageAction.get;
						break;
					}
					if (node.pattern.designerType==="widget"){
						m = o2.Actions.load("x_portal_assemble_designer").WidgetAction.get;
						break;
					}
					break;
				case "query":
					if (node.pattern.designerType==="view"){
						m = o2.Actions.load("x_query_assemble_designer").ViewAction.get;
						break;
					}
					if (node.pattern.designerType==="statement"){
						m = o2.Actions.load("x_query_assemble_designer").StatementAction.get;
						break;
					}
					break;

				case "service":
					//m = (node.pattern.appId==="invoke") ? o2.Actions.load("x_program_center").InvokeAction.get : o2.Actions.load("x_program_center").AgentAction.get;
					break;
			}
			if (m) this.openPatternFormWithData(m, node);
		}
	},
	resetFormEditor: function(pattern){
		switch (pattern.pattern.propertyType){
			case "html":
			case "script":
			case "css":
			case "sql":
			case "events":
				this.reLocationEditor(pattern);
				break;
			case "duty":
				if (pattern.pattern.valueKey=="code") this.reLocationEditor(pattern);
				break;
			case "map":
				this.reLocationMapEditor(pattern);
				break;
			case "array":
				this.reLocationArrayEditor(pattern);
				break;

		}
	},
	reLocationArrayEditor: function(pattern){
		if (this.editor){
			var i = pattern.pattern.line-1;
			if (this.editor.items[i]) this.editor.items[i].editValue();
		}
	},
	reLocationMapEditor: function(pattern){
		if (this.editor){
			var keyv = pattern.pattern.value.split(":");
			var key = keyv[0];

			for (var i=0; i<this.editor.items.length; i++){
				if (this.editor.items[i].key===key){
					this.editor.items[i].editValue();
					break;
				}
			}
		}
	},
	openPatternFormWithData: function(m, node){
		if (this.designerDataObject && this.designerDataObject[node.pattern.designerId]){
			this.openPatternFormEditor(this.designerDataObject[node.pattern.designerId], node);
		}else{
			if (m) m(node.pattern.designerId).then(function(json){
				if (json.data){
					var d = json.data;
					if (node.pattern.designerType=="form" || node.pattern.designerType=="page" || node.pattern.designerType=="widget"){
						var pcData = JSON.decode(MWF.decodeJsonString(json.data.data));
						var mobileData = (json.data.mobileData) ? JSON.decode(MWF.decodeJsonString(json.data.mobileData)) : null;
						d = {"data": pcData, "mobileData": mobileData};
					}
					if (node.pattern.designerType=="view"){
						var dataJson = JSON.decode(json.data.data);
						d.application = d.query
						d.applicationName = d.queryName;
						d.data = dataJson;
					}
					if (node.pattern.designerType=="statement"){
						d.application = d.query
						d.applicationName = d.queryName;
						var viewJson = JSON.decode(d.view);
						d.view = viewJson;
					}

					if (!this.designerDataObject) this.designerDataObject = {};
					this.designerDataObject[node.pattern.designerId] = d;
					this.openPatternFormEditor(d, node);
				}
			}.bind(this), function(){});
		}
	},
	openPatternFormEditor: function(data, node){
		switch (node.pattern.pattern.propertyType){
			case "html":
			case "script":
			case "css":
			case "sql":
			case "events":
				this.openPatternFormEditor_script(data, node);
				break;
			case "map":
				this.openPatternFormEditor_map(data, node);
				break;
			case "array":
				this.openPatternFormEditor_array(data, node);
				break;
			case "duty":
				this.openPatternFormEditor_duty(data, node);
				break;

			default:
				this.openPatternFormEditor_default(data, node);
		}
	},
	getValueWithPath: function(data, pattern, offset){
		var path = pattern.pattern.path;
		var d = data;
		var i=0;
		var oset = (offset) ? offset.toInt() : 0;
		while (i<(path.length-oset)){
			if (path[i]=="styles"){
				d.styles = d.recoveryStyles;
			}else if (path[i]=="inputStyles"){
				d.inputStyles = d.recoveryInputStyles;
			}
			d = d[path[i]];
			i++;
		}
		return d;
	},
	getTitleWithPath: function(data, pattern){
		var el = this.lp.elementPattern.replace("{element}", "&lt;"+pattern.pattern.type+"&gt;"+pattern.pattern.name).
		replace("{property}", pattern.pattern.propertyName+"{"+pattern.pattern.key+"}");
		var title = "<b>"+this.lp[pattern.module]+":&nbsp;<span style='color: #4A90E2'>"+pattern.appName+"</span></b>->"+": "+"<b>["+pattern.pattern.mode+this.lp[pattern.designerType]+"]</b>&nbsp;"+pattern.designerName+"->"+el;

		return "<div style='line-height: 30px'>"+title+"</div>"
	},
	getDefaultEditorContent: function(data, pattern){
		var el = this.lp.elementPattern.replace("{element}", "&lt;"+pattern.pattern.type+"&gt;"+pattern.pattern.name).
		replace("{property}", pattern.pattern.propertyName+"{"+pattern.pattern.key+"}");

		var html = "<div style='padding: 20px;'>" +
			"<div style='height: 40px; line-height: 40px; font-size: 18px'>"+this.lp.findInfor+"</div>" +
			"<div style='padding-left: 10px; padding: 30px; border: 1px solid #eeeeee; background-color: #f9f9f9; border-radius: 5px; float: left'>" +
			"<div style='padding-left: 20px; height: 30px; line-height: 30px'><b>"+this.lp[pattern.module]+":&nbsp;<span style='color: #4A90E2'>"+pattern.appName+"</span></b></div>" +
			"<div style='padding-left: 40px; height: 30px; line-height: 30px'><b>["+(pattern.pattern.mode || "")+this.lp[pattern.designerType]+"]</b>&nbsp;"+pattern.designerName+"</div>" +
			"<div style='padding-left: 60px; height: 30px; line-height: 30px'><b>"+this.lp.element+":</b>&nbsp;"+"&lt;"+pattern.pattern.type+"&gt;"+pattern.pattern.name+"</div>" +
			"<div style='padding-left: 80px; height: 30px; line-height: 30px'><div style='float: left'><b>"+this.lp.property+":</b>&nbsp;"+pattern.pattern.propertyName+"{"+pattern.pattern.key+"}:&nbsp;"+"</div>" +
			"<div style='margin-left: 10px; float:left; height: 30px; padding: 0px 10px; line-height: 30px; border-radius: 5px; background-color: #eeeeee;'>"+this.getPatternValueText(pattern, this.getFindRegExp())+"</div></div></div>" +
			"<div style='float:left; margin-top:20px; clear:both; height: 40px; line-height: 40px; cursor: pointer; font-size: 18px; color: #0b58a2'>"+this.lp.findInforOpen+"</div>" +
			"</div>";

		return html;
	},

	commonEditor: new Class({
		Implements: [Events],
		initialize: function(node){
			this.node = $(node);
			this.container = new Element("div", {
				"styles": {
					"padding": "10px"
				}
			});
		},
		load : function(title){
			this.container.set("html", title).inject(this.node);
		},
		destroy: function(){
			this.fireEvent("destroy");
			this.container.destroy();
			o2.release(this);
		},
		// getValue: function(){
		// 	return this.value;
		// },
		getContent: function(){

		}
	}),
	openPatternFormEditor_default: function(data, node){
		debugger;
		// var d = this.getValueWithPath(data, node.pattern);
		// if (d){
			debugger;
			var content = this.getDefaultEditorContent(data, node.pattern);
			this.editor = new this.commonEditor(this.previewContentNode);
			this.editor.addEvent("destroy", function(){
				this.previewToolbar.childrenButton[0].disable();
				this.previewToolbar.childrenButton[1].disable();
			}.bind(this));
			this.editor.pattern = node.pattern;
			this.editor.designerNode = node;
			this.editor.designerData = data;
			this.editor.load(content);
			this.editor.container.getFirst().getLast().addEvent("click", function(){
				this.openDesinger();
			}.bind(this));

			this.previewToolbar.childrenButton[0].disable();
			this.previewToolbar.childrenButton[1].enable();
		// }
	 },
	openPatternFormEditor_duty: function(data, node){
		debugger;
		if (node.pattern.pattern.valueKey=="name"){
			this.openPatternFormEditor_default(data, node)
		}else {
			var d = this.getValueWithPath(data, node.pattern, 2);
			if (d){
				var json = JSON.parse(d);
				var idx = node.pattern.pattern.idx.toInt();

				var code = json[idx].code;

				o2.require("o2.widget.JavascriptEditor", function(){
					this.editor = new o2.widget.JavascriptEditor(this.previewContentNode, {
						"option": {
							"value": code,
							"mode": "javascript"
						}
					});
					this.editor.pattern = node.pattern;
					this.editor.designerNode = node;
					this.editor.designerData = data;
					this.editor.load(function(){
						if (this.previewToolbar){
							this.previewToolbar.childrenButton[0].enable();
							this.previewToolbar.childrenButton[1].enable();
						}
						this.editor.addEvent("change", function(){
							this.editor.isRefind = true;
						}.bind(this));
						this.editor.addEvent("blur", function(){
							if (this.editor.isRefind) this.reFindInFormDesigner();
						}.bind(this));
						this.editor.addEvent("destroy", function(){
							this.previewToolbar.childrenButton[0].disable();
							this.previewToolbar.childrenButton[1].disable();
						}.bind(this));
						this.editor.addEvent("save", function(){
							this.saveDesigner();
						}.bind(this));

						this.resetFormEditor(node.pattern);
					}.bind(this));
				}.bind(this));

			}
		}
		//var d = this.getValueWithPath(data, node.pattern);
	},

	openPatternFormEditor_array: function(data, node){
		var d = this.getValueWithPath(data, node.pattern);
		if (d){
			var title = this.getTitleWithPath(data, node.pattern);
			o2.require("o2.widget.Arraylist", function(){
				this.editor = new o2.widget.Arraylist(this.previewContentNode, {
					"htmlTitle": title,
					"style": "findDesigner",
					"onChange": function(){
						this.reFindInFormDesigner();
					}.bind(this),
					"onPostLoad": function(){
						if (this.previewToolbar){
							this.previewToolbar.childrenButton[0].enable();
							this.previewToolbar.childrenButton[1].enable();
						}
					}.bind(this),
				});
				this.editor.addEvent("destroy", function(){
					this.previewToolbar.childrenButton[0].disable();
					this.previewToolbar.childrenButton[1].disable();
				}.bind(this));

				this.editor.pattern = node.pattern;
				this.editor.designerNode = node;
				this.editor.designerData = data;

				this.editor.load(d);

				this.resetFormEditor(node.pattern);

			}.bind(this))
		}

	},

	openPatternFormEditor_map: function(data, node){
		debugger;
		var d = this.getValueWithPath(data, node.pattern);

		if (d){
			var title = this.getTitleWithPath(data, node.pattern);
			o2.require("o2.widget.Maplist", function(){
				this.editor = new o2.widget.Maplist(this.previewContentNode, {
					"htmlTitle": title,
					"style": "findDesigner",
					"onChange": function(){
						this.reFindInFormDesigner();
					}.bind(this),
					"onPostLoad": function(){
						if (this.previewToolbar){
							this.previewToolbar.childrenButton[0].enable();
							this.previewToolbar.childrenButton[1].enable();
						}
					}.bind(this),
				});
				this.editor.addEvent("destroy", function(){
					this.previewToolbar.childrenButton[0].disable();
					this.previewToolbar.childrenButton[1].disable();
				}.bind(this));

				this.editor.pattern = node.pattern;
				this.editor.designerNode = node;
				this.editor.designerData = data;

				this.editor.load(d);

				this.resetFormEditor(node.pattern);

			}.bind(this))
		}
	},

	openPatternFormEditor_script: function(data, node){
		var path = node.pattern.pattern.path;
		var d = data;
		var i=0;
		while (i<path.length){
			d = d[path[i]];
			i++;
		}

		if (d){
			o2.require("o2.widget.JavascriptEditor", function(){
				this.editor = new o2.widget.JavascriptEditor(this.previewContentNode, {
					"option": {
						"value": d,
						"mode": (!node.pattern.pattern.propertyType || node.pattern.pattern.propertyType==="script" || node.pattern.pattern.propertyType==="events") ? "javascript" : node.pattern.pattern.propertyType
					}
				});
				this.editor.pattern = node.pattern;
				this.editor.designerNode = node;
				this.editor.designerData = data;
				this.editor.load(function(){
					if (this.previewToolbar){
						this.previewToolbar.childrenButton[0].enable();
						this.previewToolbar.childrenButton[1].enable();
					}
					this.editor.addEvent("change", function(){
						this.editor.isRefind = true;
					}.bind(this));
					this.editor.addEvent("blur", function(){
						if (this.editor.isRefind) this.reFindInFormDesigner();
					}.bind(this));
					this.editor.addEvent("destroy", function(){
						this.previewToolbar.childrenButton[0].disable();
						this.previewToolbar.childrenButton[1].disable();
					}.bind(this));
					this.editor.addEvent("save", function(){
						this.saveDesigner();
					}.bind(this));

					this.resetFormEditor(node.pattern);
				}.bind(this));
			}.bind(this));

		}

	},

	openPatternScript: function(node){
		var pattern = node.pattern;
		if (this.editor && this.editor.pattern.designerId === node.pattern.designerId && this.editor.pattern.module === node.pattern.module){
			this.reLocationEditor(node.pattern);
		}else{
			if (this.editor){
				if (this.editor.pattern.designerType === "script"){
					if (this.scriptDesignerDataObject && this.scriptDesignerDataObject[this.editor.pattern.designerId]){
						this.scriptDesignerDataObject[this.editor.pattern.designerId].text = this.editor.getValue();
					}
				}else{
					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]){
						this.designerDataObject[this.editor.pattern.designerId] = this.editor.designerData;
					}
				}
				this.editor.destroy();
			}
			this.editor = null;
			this.previewInforNode.hide().dispose();

			var m;
			switch (node.pattern.module){
				case "processPlatform":
					m = o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.get;
					break;
				case "cms":
					m = o2.Actions.load("x_cms_assemble_control").ScriptAction.get;
					break;
				case "portal":
					m = o2.Actions.load("x_portal_assemble_designer").ScriptAction.get;
					break;
				case "service":
					m = (node.pattern.appId==="invoke") ? o2.Actions.load("x_program_center").InvokeAction.get : o2.Actions.load("x_program_center").AgentAction.get;
					break;
			}
			this.openPatternScriptWithData(m, node);
		}
	},
	openPatternScriptWithData: function(m, node){
		if (this.scriptDesignerDataObject && this.scriptDesignerDataObject[node.pattern.designerId]){
			this.openPatternScriptEditor(this.scriptDesignerDataObject[node.pattern.designerId], node);
		}else{
			if (m) m(node.pattern.designerId).then(function(json){
				if (!this.scriptDesignerDataObject) this.scriptDesignerDataObject = {};
				this.scriptDesignerDataObject[node.pattern.designerId] = json.data;
				if (json.data) this.openPatternScriptEditor(json.data, node);
			}.bind(this), function(){});
		}
	},

	openPatternScriptEditor: function(data, node){
		o2.require("o2.widget.JavascriptEditor", function(){
			this.editor = new o2.widget.JavascriptEditor(this.previewContentNode, {
				"option": {"value": data.text}
			});
			this.editor.pattern = node.pattern;
			this.editor.designerNode = node.parentNode;
			this.editor.designerData = data;
			this.editor.load(function(){
				if (this.previewToolbar){
					this.previewToolbar.childrenButton[0].enable();
					this.previewToolbar.childrenButton[1].enable();
				}
				this.editor.addEvent("change", function(){
					this.editor.isRefind = true;
				}.bind(this));
				this.editor.addEvent("blur", function(){
					if (this.editor.isRefind) this.reFindInDesigner();
				}.bind(this));
				this.editor.addEvent("destroy", function(){
					this.previewToolbar.childrenButton[0].disable();
					this.previewToolbar.childrenButton[1].disable();
				}.bind(this));
				this.editor.addEvent("save", function(){
					this.saveDesigner();
				}.bind(this));


				//var idx = node.parentNode.children.indexOf(node);
				//this.reFindInDesigner();
				this.reLocationEditor(node.pattern);
				//if (node.parentNode.children[idx]) node.parentNode.children[idx].clickNode();
			}.bind(this));
		}.bind(this));
	},
	findScriptLineValue: function(result, code, preLine, preIndex, len, regex){
		var lineRegexp = /\r\n|\n|\r/g;
		var preText = code.substring(preIndex, result.index);
		var m = preText.match(lineRegexp);
		preLine += (m) ? m.length : 0;

		var value = result[0];

		var n = result.index-1;
		var char = code.charAt(n);
		while (!lineRegexp.test(char) && n>=0){
			value = char+value;
			n--;
			char = code.charAt(n);
		}
		n =  regex.lastIndex;
		char = code.charAt(n);
		while (!lineRegexp.test(char) && n<len){
			value = value+char;
			n++;
			char = code.charAt(n);
		}
		preIndex = regex.lastIndex = n;
		return {"value": value, "preLine": preLine, "preIndex": preIndex};
	},

	subResultCount: function(pattern){
		this.subResultTitle();

		var moduleNode = (this.tree.modules) ? this.tree.modules[pattern.module] : null;
		if (moduleNode) this.subPatternCount(moduleNode);

		var appNode = (moduleNode.apps) ? moduleNode.apps[pattern.appId] : null;
		if (appNode) this.subPatternCount(appNode);

		var typeNode = (appNode.types) ? appNode.types[pattern.designerType] : null;
		if (typeNode) this.subPatternCount(typeNode);

		var designerNode = (typeNode.designers) ? typeNode.designers[pattern.designerId] : null;
		if (designerNode) this.subPatternCount(designerNode);
	},

	reFindInFormDesigner: function(){
		debugger;
		if (this.editor && this.editor.designerNode){

			var pathStr = this.editor.pattern.pattern.path.join(".");
			var pNode = this.editor.designerNode.parentNode;
			var removeNodes = [];
			pNode.children.forEach(function(n){
				if (n.pattern.pattern.path && n.pattern.pattern.path.join(".")===pathStr) removeNodes.push(n);
			}.bind(this));

			var pattern = this.editor.pattern;
debugger;
			switch (pattern.pattern.propertyType){
				case "html":
				case "script":
				case "css":
				case "sql":
				case "events":
					if (removeNodes.length>1){
						for (var i=1; i<removeNodes.length; i++){
							removeNodes[i].destroy();
							this.subResultCount(this.editor.pattern);
						}
					}
					flagNode = removeNodes[0];
					this.reFindInFormDesigner_script(flagNode, pattern);
					break;
				case "duty":
					if (removeNodes.length>1){
						for (var i=1; i<removeNodes.length; i++){
							removeNodes[i].destroy();
							this.subResultCount(this.editor.pattern);
						}
					}
					flagNode = removeNodes[0];
					this.reFindInFormDesigner_duty(flagNode, pattern);
					break;
				case "map":
					this.reFindInFormDesigner_map(removeNodes, pattern);
					break;
				case "array":
					this.reFindInFormDesigner_array(removeNodes, pattern);
					break;
			}
			this.subResultCount(this.editor.pattern);
		}
		this.editor.isRefind = false;
	},
	reFindInFormDesigner_duty: function(removeNodes, pattern){
		var code = this.editor.getValue();
		if (code){
			var regex = this.getFilterOptionRegex(this.filterOption)
			regex.lastIndex = 0;
			var len = code.length;

			var preLine = 0;
			var preIndex = 0;
			var result;
			while ((result = regex.exec(code)) !== null){
				var obj = this.findScriptLineValue(result, code, preLine, preIndex, len, regex);
				preLine = obj.preLine;
				preIndex = obj.preIndex;

				this.showFindResult(this._createFindMessageReplyData( this.editor.pattern.module, this.editor.pattern, "", {
					"type": pattern.pattern.type,
					"propertyType": pattern.pattern.propertyType,
					"propertyName": pattern.pattern.propertyName,
					"name": pattern.pattern.name,
					"id": pattern.pattern.id,
					"key": pattern.pattern.key,
					"evkey": pattern.pattern.evkey,
					"idx": pattern.pattern.idx,
					"valueKey": pattern.pattern.valueKey,
					"value": obj.value,
					"line": preLine+1,
					"mode": pattern.pattern.mode,
					"path": pattern.pattern.path
				}), this.filterOption, flagNode);
			}

		}
		if (flagNode) flagNode.destroy();
	},
	reFindInFormDesigner_array: function(removeNodes, pattern){
		var arr = this.editor.getValue();
		if (arr){
			var regex = this.getFilterOptionRegex(this.filterOption)
			removeNodes.forEach(function(i){
				var idx = i.pattern.pattern.line.toInt()-1;
				var value = arr[idx] || "";
				if (i.pattern.pattern.value!=value){
					i.pattern.pattern.value = value;
					var text = this.getFormPatternNodeText(i.pattern, regex)
					var textDivNode = i.textNode.getElement("div");
					if (textDivNode){
						textDivNode.set("html", "<span style='color: #000000'>"+text+"</span>");
					}
				}
			}.bind(this));

			arr.forEach(function(v, i) {
				regex.lastIndex = 0;
				var text = v.toString();
				if (regex.test(text)){


					var n = removeNodes.filter(function(n){
						var idx = n.pattern.pattern.line.toInt()-1;
						return (idx==i);
					});
					if (!n.length){
						this.showFindResult(this._createFindMessageReplyData(this.editor.pattern.module, this.editor.pattern, "", {
							"type": pattern.pattern.type,
							"propertyType": pattern.pattern.propertyType,
							"propertyName": pattern.pattern.propertyName,
							"name": pattern.pattern.name,
							"id": pattern.pattern.id,
							"line": i+1,
							"key": pattern.pattern.key,
							"value": text,
							"mode": pattern.pattern.mode,
							"path": pattern.pattern.path
						}), this.filterOption, removeNodes[removeNodes.length-1].nextSibling);
					}
				}
			}.bind(this));
		}
	},
	reFindInFormDesigner_map: function(removeNodes, pattern){
		var map = this.editor.getValue();
		if (map){
			var regex = this.getFilterOptionRegex(this.filterOption)
			removeNodes.forEach(function(i){
				var k = i.pattern.pattern.value.split(":")[0];
				if (i.pattern.pattern.value!=(k+": "+map[k])){
					i.pattern.pattern.value = k+": "+(map[k] || "");
					var text = this.getFormPatternNodeText(i.pattern, regex)

					var textDivNode = i.textNode.getElement("div");
					if (textDivNode){
						textDivNode.set("html", "<span style='color: #000000'>"+text+"</span>");
					}
				}
			}.bind(this));

			Object.keys(map).forEach(function(evkey) {
				regex.lastIndex = 0;
				var text = map[evkey];
				if (text){
					if ((typeof text)=="string") {
						if (regex.test(text)) {

							var n = removeNodes.filter(function(i){
								var k = i.pattern.pattern.value.split(":")[0];
								return (k==evkey)
							});
							if (!n.length){
								this.showFindResult(this._createFindMessageReplyData(this.editor.pattern.module, this.editor.pattern, "", {
									"type": pattern.pattern.type,
									"propertyType": pattern.pattern.propertyType,
									"propertyName": pattern.pattern.propertyName,
									"name": pattern.pattern.name,
									"id": pattern.pattern.id,
									"key": pattern.pattern.key,
									"value": evkey + ": " + text,
									"mode": pattern.pattern.mode,
									"path": pattern.pattern.path
								}), this.filterOption, removeNodes[removeNodes.length-1].nextSibling);
							}
						}
					}
				}
			}.bind(this));
		}
	},

	reFindInFormDesigner_script: function(flagNode){
		var code = this.editor.getValue();
		if (code){
			var regex = this.getFilterOptionRegex(this.filterOption)
			regex.lastIndex = 0;
			var len = code.length;

			var preLine = 0;
			var preIndex = 0;
			var result;
			while ((result = regex.exec(code)) !== null){
				var obj = this.findScriptLineValue(result, code, preLine, preIndex, len, regex);
				preLine = obj.preLine;
				preIndex = obj.preIndex;

				var pattern =  this.editor.pattern;
				this.showFindResult(this._createFindMessageReplyData( this.editor.pattern.module, this.editor.pattern, "", {
					"type": pattern.pattern.type,
					"propertyType": pattern.pattern.propertyType,
					"propertyName": pattern.pattern.propertyName,
					"name": pattern.pattern.name,
					"id": pattern.pattern.id,
					"key": pattern.pattern.key,
					"evkey": pattern.pattern.evkey,
					"value": obj.value,
					"line": preLine+1,
					"mode": pattern.pattern.mode,
					"path": pattern.pattern.path
				}), this.filterOption, flagNode);
			}

		}
		if (flagNode) flagNode.destroy();
	},

	reFindInDesigner: function(){
		if (this.editor && this.editor.designerNode){
			while (this.editor.designerNode.firstChild){
				this.editor.designerNode.firstChild.destroy();
				this.subResultCount(this.editor.pattern);
			}
			var code = this.editor.getValue();
			if (code){
				var regex = this.getFilterOptionRegex(this.filterOption)
				regex.lastIndex = 0;
				var len = code.length;

				var preLine = 0;
				var preIndex = 0;
				var result;
				while ((result = regex.exec(code)) !== null){
					var obj = this.findScriptLineValue(result, code, preLine, preIndex, len, regex);
					preLine = obj.preLine;
					preIndex = obj.preIndex;

					this.showFindResult(this._createFindMessageReplyData( this.editor.pattern.module, this.editor.pattern, "", {
						"property": "text",
						"value": obj.value,
						"line": preLine+1
					}), this.filterOption);
				}
			}
		}
		this.editor.isRefind = false;
	},

	saveDesigner: function(){
		debugger;
		if (this.editor && this.editor.pattern){
			var pattern = this.editor.pattern;
			var data = this.editor.designerData;

			switch (pattern.designerType){
				case "script":
					var m;
					switch (pattern.module){
						case "processPlatform":
							m = o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.put;
							break;
						case "cms":
							m = o2.Actions.load("x_cms_assemble_control").ScriptAction.put;
							break;
						case "portal":
							m = o2.Actions.load("x_portal_assemble_designer").ScriptAction.edit;
							break;
						case "service":
							m = (pattern.appId==="invoke") ? o2.Actions.load("x_program_center").InvokeAction.edit : o2.Actions.load("x_program_center").AgentAction.edit;
							break;
					}
					data.text = this.editor.getValue();
					if (m) m(data.id, data).then(function(){
						this.notice(this.lp.notice.save_success, "success", this.previewContentNode, {"x": "left", "y": "bottom"});
					}.bind(this), function(){});

					break;
				case "form":
				case "page":
				case "widget":
					switch (pattern.module){
						case "processPlatform":
							var action = MWF.Actions.get("x_processplatform_assemble_designer");
							m = action.saveForm.bind(action);
							break;
						case "cms":
							var action = MWF.Actions.get("x_cms_assemble_control");
							m = action.saveForm.bind(action);
							break;
						case "portal":
							var action = MWF.Actions.get("x_portal_assemble_designer");
							m = (pattern.designerType=="page") ? action.savePage.bind(action) : action.saveWidget.bind(action);
							break;
					}
					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]){
						var d = this.designerDataObject[this.editor.pattern.designerId];
						switch (this.editor.pattern.pattern.propertyType){
							case "duty":
								if (this.editor.pattern.pattern.path){
									var path = this.editor.pattern.pattern.path;
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.parse(d[path[i]]);
											d = d[path[i]];
										}else{
											d = d[path[i]];
										}
									}
									d[path[path.length-1]] = this.editor.getValue();

									d = this.designerDataObject[this.editor.pattern.designerId];
									for (var i=0; i<path.length-1; i++){
										if (path[i]==this.editor.pattern.pattern.key){
											d[path[i]] = JSON.stringify(d[path[i]]);
											break;
										}else{
											d = d[path[i]];
										}
									}
								}
								break;
							default:
								if (this.editor.getValue){
									if (this.editor.pattern.pattern.path){
										var path = this.editor.pattern.pattern.path;
										for (var i=0; i<path.length-1; i++){
											d = d[path[i]];
										}
									}
									if (path[path.length-1]=="styles"){
										d["recoveryStyles"] = this.editor.getValue();
									}else if (path[path.length-1]=="inputStyles"){
										d["recoveryInputStyles"] = this.editor.getValue();
									}
									d[path[path.length-1]] = this.editor.getValue();
								}
						}

					}

					if (m) m(data.data, data.mobileData, null, function(){
						this.notice(this.lp.notice.save_success, "success", this.previewContentNode, {"x": "left", "y": "bottom"});
					}.bind(this), function(){});

					break;
				case "process":
					var action = MWF.Actions.get("x_processplatform_assemble_designer");
					m = action.saveProcess.bind(action);

					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]) {
						var d = this.designerDataObject[this.editor.pattern.designerId];
						if (this.editor.getValue){
							if (this.editor.pattern.pattern.path){
								var path = this.editor.pattern.pattern.path;
								for (var i=0; i<path.length-1; i++){
									d = d[path[i]];
								}
							}
							d[path[path.length-1]] = this.editor.getValue();
						}
					}

					if (m) m(data, function(){
						this.notice(this.lp.notice.save_success, "success", this.previewContentNode, {"x": "left", "y": "bottom"});
					}.bind(this), function(){});

					break;
				case "view":
					var action = MWF.Actions.get("x_query_assemble_designer");
					m = action.saveView.bind(action);

					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]) {
						var d = this.designerDataObject[this.editor.pattern.designerId];
						if (this.editor.getValue){
							if (this.editor.pattern.pattern.path){
								var path = this.editor.pattern.pattern.path;
								for (var i=0; i<path.length-1; i++){
									d = d[path[i]];
								}
							}
							d[path[path.length-1]] = this.editor.getValue();
						}
					}

					if (m) m(data, function(){
						this.notice(this.lp.notice.save_success, "success", this.previewContentNode, {"x": "left", "y": "bottom"});
					}.bind(this), function(){});

					break;

				case "statement":
					var action = MWF.Actions.get("x_query_assemble_designer");
					m = action.saveStatement.bind(action);
					if (this.designerDataObject && this.designerDataObject[this.editor.pattern.designerId]) {
						var d = this.designerDataObject[this.editor.pattern.designerId];
						if (this.editor.getValue){
							if (this.editor.pattern.pattern.path){
								var path = this.editor.pattern.pattern.path;
								for (var i=0; i<path.length-1; i++){
									d = d[path[i]];
								}
							}
							d[path[path.length-1]] = this.editor.getValue();
						}
					}
					var viewText = JSON.stringify(data.view);
					data.view = viewText;

					if (m) m(data, function(){
						this.notice(this.lp.notice.save_success, "success", this.previewContentNode, {"x": "left", "y": "bottom"});
					}.bind(this), function(){});
					break;
			}
		}
	},

	openDesinger: function(){
		if (this.editor && this.editor.pattern){
			var pattern = this.editor.pattern;

			switch (pattern.designerType){
				case "script":
					var m;
					switch (pattern.module){
						case "processPlatform":
							var options = {
								"appId": "process.ScriptDesigner"+pattern.designerId,
								"id": pattern.designerId,
								"application":  pattern.appId,
							};
							layout.openApplication(null, "process.ScriptDesigner", options);
							break;
						case "cms":
							var options = {
								"appId": "cms.ScriptDesigner"+pattern.designerId,
								"id": pattern.designerId,
								"application":  pattern.appId,
							};
							layout.openApplication(null, "cms.ScriptDesigner", options);
							break;
						case "portal":
							var options = {
								"appId": "portal.ScriptDesigner"+pattern.designerId,
								"id": pattern.designerId,
								"application":  pattern.appId,
							};
							layout.openApplication(null, "portal.ScriptDesigner", options);
							break;
						case "service":
							if (pattern.appId==="invoke"){
								var options = {
									"appId": "service.InvokeDesigner"+pattern.designerId,
									"id": pattern.designerId,
								};
								layout.openApplication(null, "service.InvokeDesigner", options);
							}else{
								var options = {
									"appId": "service.AgentDesigner"+pattern.designerId,
									"id": pattern.designerId,
								};
								layout.openApplication(null, "service.AgentDesigner", options);
							}
							break;
					}
					break;
				case "form":
					switch (pattern.module) {
						case "processPlatform":
							var _self = this;
							var options = {
								"style": layout.desktop.formDesignerStyle || "default",
								"appId": "process.FormDesigner"+pattern.designerId,
								"id": pattern.designerId,
								"onPostFormLoad": function(){
									_self.checkSelectDesignerElement_form(this, pattern, 0);
								}
							};
							layout.openApplication(null, "process.FormDesigner", options);
							break;
						case "cms":
							var _self = this;
							var options = {
								"style": layout.desktop.formDesignerStyle || "default",
								"appId": "cms.FormDesigner"+pattern.designerId,
								"id": pattern.designerId,
								"onPostFormLoad": function(){
									_self.checkSelectDesignerElement_form(this, pattern, 0);
								}
							};
							layout.openApplication(null, "cms.FormDesigner", options);
							break;
					}
					//this.createFormPatternNode(data, designerNode, regexp);
					break;
				case "process":
					var _self = this;
					var options = {
						"appId": "process.ProcessDesigner"+pattern.designerId,
						"id": pattern.designerId,
						"onPostProcessLoad": function(){
							_self.checkSelectDesignerElement_process(this, pattern, 0);
						}
					};
					layout.openApplication(null, "process.ProcessDesigner", options);
					break;
				case "page":
					var _self = this;
					var options = {
						"appId": "portal.PageDesigner"+pattern.designerId,
						"id": pattern.designerId,
						"onPostPageLoad": function(){
							_self.checkSelectDesignerElement_form(this, pattern, 0);
						}
					};
					layout.openApplication(null, "portal.PageDesigner", options);
					break;
				case "widget":
					var _self = this;
					var options = {
						"appId": "portal.WidgetDesigner"+pattern.designerId,
						"id": pattern.designerId,
						"onPostWidgetLoad": function(){
							_self.checkSelectDesignerElement_form(this, pattern, 0);
						}
					};
					layout.openApplication(null, "portal.WidgetDesigner", options);
					break;
				case "view":
					var _self = this;
					var options = {
						"appId": "query.ViewDesigner"+pattern.designerId,
						"id": pattern.designerId,
						"application": pattern.appId,
						"onPostViewLoad": function(){
							_self.checkSelectDesignerElement_view(this, pattern, 0);
						}
					};
					layout.openApplication(null, "query.ViewDesigner", options);
					break;
				case "statement":
					var _self = this;
					var options = {
						"appId": "query.StatementDesigner"+pattern.designerId,
						"id": pattern.designerId,
						"application": pattern.appId
					};
					layout.openApplication(null, "query.StatementDesigner", options);
					break;
			}
			if (this.editor.getValue) window.setTimeout(function(){
				if (this.scriptDesignerDataObject && this.scriptDesignerDataObject[this.editor.pattern.designerId]){
					this.scriptDesignerDataObject[this.editor.pattern.designerId] = null;
					delete this.scriptDesignerDataObject[this.editor.pattern.designerId];
				}
				if (this.editor) this.editor.destroy();
				this.editor = null;

				this.previewInforNode.show().inject(this.previewContentNode);
			}.bind(this), 100);
		}
	},
	checkSelectDesignerElement_view: function(app, pattern, idx){
		var flag = false;
		var view = app.view;
		if (view){
			var type = pattern.pattern.type.toLowerCase();
			switch (type){
				case "view":
					flag = true;
					break;
				case "column":
					for (var i=0; i<view.items.length; i++){
						var m = view.items[i];
						if (m.json.id==pattern.pattern.id || m.json.name==pattern.pattern.id){
							window.setTimeout(function(){
								if (m.view) m.view.unSelected();
								m.selected();
							}, 500);
							flag = true;
							break;
						}
					}
					break;
				case "actionbar":
					for (var i=0; i<view.actionbarList.length; i++){
						var m = view.actionbarList[i];
						if (m.json.id==pattern.pattern.id || m.json.name==pattern.pattern.id){
							window.setTimeout(function(){
								if (m.view) m.view.unSelected();
								m.selected();
							}, 500);
							flag = true;
							break;
						}
					}
					break;
				case "paging":
					for (var i=0; i<view.pagingList.length; i++){
						var m = view.pagingList[i];
						if (m.json.id==pattern.pattern.id || m.json.name==pattern.pattern.id){
							window.setTimeout(function(){
								if (m.view) m.view.unSelected();
								m.selected();
							}, 500);
							flag = true;
							break;
						}
					}
					break;
			}
		}

		if (!flag){
			idx++;
			if (idx<10) window.setTimeout(function(){
				this.checkSelectDesignerElement_view(app, pattern, idx);
			}.bind(this), 300);
		}
	},
	checkSelectDesignerElement_process: function(app, pattern, idx){
		var flag = false;
		var process = app.process;
		if (process){
			var type = pattern.pattern.type.toLowerCase();
			switch (type){
				case "process":
					flag = true;
					break;
				case "route":
					if (process.routes && process.routes[pattern.pattern.id]){
						process.routes[pattern.pattern.id].selected();
						flag = true;
					}
					break;
				case "begin":
					if (process.begin){
						process.begin.selected();
						flag = true;
					}
					break;
				default:
					if (process[type+"s"] && process[type+"s"][pattern.pattern.id]){
						process[type+"s"][pattern.pattern.id].selected();
						flag = true;
					}
			}
		}

		if (!flag){
			idx++;
			if (idx<10) window.setTimeout(function(){
				this.checkSelectDesignerElement_process(app, pattern, idx);
			}.bind(this), 300);
		}
	},
	checkSelectDesignerElement_form: function(app, pattern, idx){
		var flag = false;
		var form;
		debugger;
		try{
			if (pattern.pattern.mode=="PC"){
				app.changeDesignerModeToPC();
				form = app.pcForm || app.pcPage;
			}else{
				app.changeDesignerModeToMobile();
				form = app.mobileForm || app.mobilePage;
			}
		}catch(e){}

		if (form && pattern.pattern.mode=="PC"){
			if (pattern.pattern.type.toLowerCase()!="form"){
				for (var i=0; i<form.moduleList.length; i++){
					var m = form.moduleList[i];
					if (m.json.id==pattern.pattern.name || m.json.name==pattern.pattern.name){
						window.setTimeout(function(){
							if (m.form) m.form.unSelected();
							m.selected()
						}, 500);
						flag = true;
						break;
					}
				}
			}else{
				flag = true;
			}
		}

		if (!flag){
			idx++;
			if (idx<10) window.setTimeout(function(){
				this.checkSelectDesignerElement_form(app, pattern, idx);
			}.bind(this), 300);
		}
	},


	_createFindMessageReplyData: function(module, designer, aliase, pattern){
		return {
			"module": module,
			"appId": designer.appId,
			"appName": designer.appName,
			"designerId": designer.designerId,
			"designerName": designer.designerName,
			"designerType": designer.designerType,
			"designerAliase": aliase,

			"pattern": pattern
		};
	},

	reLocationEditor: function(pattern){
		this.editor.pattern = pattern;
		this.editor.gotoLine(pattern.pattern.line, 1);
		if (pattern.pattern.cols && pattern.pattern.cols.length){
			var rs = [];
			pattern.pattern.cols.forEach(function(col){
				rs.push(this.editor.getRange(pattern.pattern.line,col.start, pattern.pattern.line, col.end));
			}.bind(this));
			this.editor.selectRange(rs);
		}else{
			this.editor.selectRange(this.editor.getRange(pattern.pattern.line,0));
		}
	},


	getFilterOptionRegex: function(option){
		var keyword = option.keyword;
		if (option.matchRegExp){
			var flag = (option.caseSensitive) ? "gm" : "gmi";
			return new RegExp(keyword, flag);
		}else{
			keyword = keyword.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)").replace("{", "\\{").replace("}", "\\}")
				.replace("^", "\\^").replace("$", "\\$").replace(".", "\\.").replace("?", "\\?").replace("+", "\\+").replace("*", "\\*").replace("|", "\\|");

			var flag = (option.caseSensitive) ? "gm" : "gmi";
			keyword = (option.matchWholeWord) ? "\\b"+keyword+"\\b" : keyword;
			return new RegExp(keyword, flag);
		}
	},

	setReceiveMessage: function(){
		this.listTitleInfoNode.set("text", this.lp.receiveToFind);
	},
	setReadyMessage: function(data){
		this.findOptionModuleCount = data.count;
		this.findOptionModuleProcessed = 0;
		this.updateFindProgress();
		this.listTitleInfoNode.set("text", this.lp.readyToFind.replace("{n}", data.count));
	},
	updateFindProgress: function(){
		var percent = (this.findOptionModuleProcessed/this.findOptionModuleCount)*100;
		this.listTitleProgressNode.setStyle("width", ""+percent+"%");
	},

	getActionsUrl:function(){
		var processHost = o2.Actions.getHost("x_processplatform_assemble_designer");
		var cmsHost = o2.Actions.getHost("x_cms_assemble_control");
		var portalHost = o2.Actions.getHost("x_portal_assemble_designer");
		var queryHost = o2.Actions.getHost("x_query_assemble_designer");
		var serviceHost = o2.Actions.getHost("x_program_center");
		var findHost = o2.Actions.getHost("x_query_service_processing");

		var actions = {
			"listProcess": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/application/list"),
			"listProcessProcess": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/process/application/{applicationId}"),
			"listProcessForm": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/form/list/application/{applicationId}"),
			"listProcessScript": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/script/application/{applicationId}"),
			"getProcessProcess": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/process/{id}"),
			"getProcessForm": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/form/{id}"),
			"getProcessScript": o2.filterUrl(processHost+"/x_processplatform_assemble_designer/jaxrs/script/{id}"),

			"listCms": o2.filterUrl(cmsHost+"/x_cms_assemble_control/jaxrs/appinfo/list/manage"),
			"listCmsForm": o2.filterUrl(cmsHost+"/x_cms_assemble_control/jaxrs/form/list/app/{appId}"),
			"listCmsScript": o2.filterUrl(cmsHost+"/x_cms_assemble_control/jaxrs/script/list/app/{flag}"),
			"getCmsForm": o2.filterUrl(cmsHost+"/x_cms_assemble_control/jaxrs/form/{id}"),
			"getCmsScript": o2.filterUrl(cmsHost+"/x_cms_assemble_control/jaxrs/script/{id}"),

			"listPortal": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/portal/list"),
			"listPortalPage": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/page/list/portal/{portalId}"),
			"listPortalScript": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/script/list/portal/{portalId}"),
			"listPortalWidget": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/widget/list/portal/{portalId}"),
			"getPortalPage": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/page/{id}"),
			"getPortalScript": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/script/{id}"),
			"getPortalWidget": o2.filterUrl(portalHost+"/x_portal_assemble_designer/jaxrs/widget/{id}"),

			"listQuery": o2.filterUrl(queryHost+"/x_query_assemble_designer/jaxrs/query/list/summary"),
			"listQueryView": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/view/list/query/{flag}"),
			"listQueryStat": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/stat/list/query/{flag}"),
			"listQueryStatement": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/statement/list/query/{flag}"),
			"getQueryView": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/view/{id}"),
			"getQueryStat": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/stat/{id}"),
			"getQueryStatement": o2.filterUrl(portalHost+"/x_query_assemble_designer/jaxrs/statement/{id}"),

			"listInvoke": o2.filterUrl(serviceHost+"/x_program_center/jaxrs/invoke"),
			"listAgent": o2.filterUrl(serviceHost+"/x_program_center/jaxrs/agent"),
			"getInvoke": o2.filterUrl(serviceHost+"/x_program_center/jaxrs/invoke/{flag}"),
			"getAgent": o2.filterUrl(serviceHost+"/x_program_center/jaxrs/agent/{flag}"),

			"findAction": o2.filterUrl(findHost+"/x_query_service_processing/jaxrs/design/search")
		};
		return actions;
	},

	findDesigner: function(){
		this.getFindOption();

		if (!this.filterOption.keyword){
			this.listInfoNode.show().removeClass("loadding").getFirst().set("text", this.lp.nothingFind_keyword);
			return false;
		}
		if (!this.filterOption.designerTypes.length){
			this.listInfoNode.show().removeClass("loadding").getFirst().set("text", this.lp.nothingFind_noFilter);
			return false;
		}
		if (!this.filterOption.moduleList.length){
			this.listInfoNode.show().removeClass("loadding").getFirst().set("text", this.lp.nothingFind_noRange);
			return false;
		}

		this.listContentNode.hide();
		this.listContentNode.empty();
		this.listInfoNode.show().getFirst().set("text", "");
		this.listInfoNode.addClass("loadding");
		this.patternCount = 0;

		if (this.editor) this.editor.destroy();
		this.editor = null;
		this.previewInforNode.show().inject(this.previewContentNode);

		this.scriptDesignerDataObject = null;
		this.designerDataObject = null;

		this.getFindWorker();
		var actions = this.getActionsUrl();

		this.tree = null;
		this.getResultTree(function(){
			var workerMessage = {
				actions:actions,
				filterOption: this.filterOption,
				debug: (window.layout && layout["debugger"]),
				token: (window.layout && layout.session && layout.session.user) ? layout.session.user.token : "",
				tokenName: o2.tokenName
			};
			this.findWorker.postMessage(workerMessage);
		}.bind(this));
	},
	getFindRegExp: function(){
		var flag = "gm";
		var keyword = this.filterOption.keyword;
		if (!this.filterOption.caseSensitive) flag+="i";
		if (this.filterOption.matchRegExp){
			return new RegExp(keyword, flag)
		}else{
			keyword = keyword.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)").replace("{", "\\{").replace("}", "\\}")
				.replace("^", "\\^").replace("$", "\\$").replace(".", "\\.").replace("?", "\\?").replace("+", "\\+").replace("*", "\\*").replace("|", "\\|");

			if (this.filterOption.matchWholeWord) keyword = "\\b"+keyword+"\\b";
			return new RegExp(keyword, flag)
		}
	}
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	//------------------------------------------------------------
	// ------------------------------------------------------------
	// ------------------------------------------------------------



	// findDesigner_bak: function(){
	// 	this.listContentNode.hide();
	// 	this.listInfoNode.show().getFirst().set("text", "");
	// 	this.listInfoNode.addClass("loadding")
	// 	o2.Actions.load("x_query_service_processing").DesignAction.search(this.filterOption, function(json){
	// 		if ((json.data.processPlatformList && json.data.processPlatformList.length) ||
	// 			(json.data.cmsList && json.data.cmsList.length) ||
	// 			(json.data.portalList && json.data.portalList.length) ||
	// 			(json.data.queryList && json.data.queryList.length) ||
	// 			(json.data.serviceList && json.data.serviceList.length)){
	//
	// 			this.listInfoNode.hide();
	// 			this.listFindResult(json.data);
	//
	// 		}else{
	// 			this.listInfoNode.show().removeClass("loadding").getFirst().set("text", this.lp.nothingFind);
	// 		}
	// 	}.bind(this));
	// },
	//
	//
	// createResultAppItem: function(text, title, tree){
	// 	var obj = {
	// 		"title": title,
	// 		"text": "<span style='font-weight: bold; color: #4A90E2'>"+text+"</span>",
	// 		"icon": ""
	// 	}
	// 	return tree.appendChild(obj);
	// },
	// // createResultDesignerItem: function(designer, tree){
	// // 	var title = this.lp[designer.designerType]+ ": "+ designer.designerName + " ("+designer.designerId+")";
	// // 	var text = this.lp[designer.designerType]+ ": <b>"+ designer.designerName+"</b>";
	// // 	var obj = {
	// // 		"expand": false,
	// // 		"title": title,
	// // 		"text": text,
	// // 		"icon": ""
	// // 	}
	// // 	var item = tree.appendChild(obj);
	// // 	item.designer = designer;
	// // 	item.appendChild({ "expand": false, "text": "loading...", "icon": "" });
	// // 	return item;
	// // },
	// listFindResult: function(data){
	// 	this.listContentNode.empty();
	// 	this.listContentNode.show();
	// 	o2.require("o2.widget.Tree", function(){
	// 		var tree = new o2.widget.Tree(this.listContentNode, {
	// 			"onQueryExpand": function(item){
	// 				if (item.designer) this.loadDesignerPattern(item);
	// 			}.bind(this)
	// 		});
	// 		tree.load();
	// 		if (data.processPlatformList && data.processPlatformList.length){
	// 			var platformItem = this.createResultCategroyItem(this.lp.processPlatform, this.lp.processPlatform, tree);
	// 			this.listProcessResult(platformItem, data.processPlatformList, "processPlatform");
	// 		}
	// 		if (data.cmsList && data.cmsList.length){
	// 			var platformItem = this.createResultCategroyItem(this.lp.cms, this.lp.cms, tree);
	// 			//this.listProcessResult(categroyItem, data.cmsList);
	// 		}
	// 		if (data.portalList && data.portalList.length){
	// 			var platformItem = this.createResultCategroyItem(this.lp.portal, this.lp.portal, tree);
	//
	// 		}
	// 		if (data.queryList && data.queryList.length){
	// 			var platformItem = this.createResultCategroyItem(this.lp.query, this.lp.query, tree);
	// 		}
	// 		if (data.serviceList && data.serviceList.length){
	// 			var platformItem = this.createResultCategroyItem(this.lp.service, this.lp.service, tree);
	// 		}
	//
	//
	// 	}.bind(this));
	// },
	// addPatternCount: function(item, count){
	// 	if (!item.count) item.count = 0;
	// 	item.count += count;
	// 	var t = this.lp.patternCount.replace("{n}", item.count);
	// 	var textDivNode = item.textNode.getElement("div");
	// 	if (textDivNode){
	// 		var html = item.options.text;
	// 		textDivNode.set("html", html+" <span style=''>( "+t+" )</span>");
	// 	}
	// },
	// listProcessResult: function(platformItem, list, platform){
	// 	var applicationItems = {};
	// 	list.each(function(designer){
	// 		if (designer.patternList && designer.patternList.length){
	// 			var appItem = applicationItems[designer.appId];
	// 			if (!appItem){
	// 				applicationItems[designer.appId] = appItem = this.createResultAppItem(designer.appName, designer.appName+" ("+designer.appId+")", platformItem);
	// 			}
	// 			designer.platform = platform;
	// 			var designerItem = this.createResultDesignerItem(designer, appItem);
	// 			var count=0;
	// 			designer.patternList.each(function(p){
	// 				if (p.lines && p.lines.length){
	// 					count += p.lines.length;
	// 				}else{
	// 					count++;
	// 				}
	// 			});
	// 			// var count = designer.patternList.length;
	//
	// 			this.addPatternCount(designerItem, count);
	// 			this.addPatternCount(appItem, count);
	// 			this.addPatternCount(platformItem, count);
	// 		}
	// 	}.bind(this));
	// },
	//
	// getDesignerObject: function(designer){
	// 	switch (designer.platform){
	// 		case "processPlatform":
	// 			var action = this.Actions.load("x_processplatform_assemble_designer");
	// 			switch (designer.designerType){
	// 				case "script":
	// 					return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
	// 				case "form":
	// 					return action.FomrAction.get(designer.designerId, function(json){return json.data;});
	// 				case "process":
	// 					return action.ProcessAction.get(designer.designerId, function(json){return json.data;});
	// 			}
	// 		case "cms":
	// 			var action = this.Actions.load("x_cms_assemble_control");
	// 			switch (designer.designerType){
	// 				case "script":
	// 					return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
	// 				case "form":
	// 					return action.FormAction.get(designer.designerId, function(json){return json.data;});
	// 			}
	//
	// 		case "portal":
	// 			var action = this.Actions.load("x_portal_assemble_designer");
	// 			switch (designer.designerType){
	// 				case "script":
	// 					return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
	// 				case "page":
	// 					return action.PageAction.get(designer.designerId, function(json){return json.data;});
	// 				case "widget":
	// 					return action.WidgetAction.get(designer.designerId, function(json){return json.data;});
	// 			}
	// 		case "query":
	// 			var action = this.Actions.load("x_query_assemble_designer");
	// 			switch (designer.designerType){
	// 				case "view":
	// 					return action.ViewAction.get(designer.designerId, function(json){return json.data;});
	// 				case "statement":
	// 					return action.StatementAction.get(designer.designerId, function(json){return json.data;});
	// 				case "stat":
	// 					return action.StatAction.get(designer.designerId, function(json){return json.data;});
	// 			}
	// 		case "service":
	// 			var action = this.Actions.load("x_program_center");
	// 			switch (designer.appId){
	// 				case "invoke":
	// 					return action.InvokeAction.get(designer.designerId, function(json){return json.data;});
	// 				case "agent":
	// 					return action.AgentAction.get(designer.designerId, function(json){return json.data;});
	// 			}
	// 	}
	// },
	// loadDesignerPattern: function(item){
	// 	if (item.firstChild && item.firstChild.options.text==="loading..."){
	// 		item.firstChild.destroy();
	//
	// 		var root, actionName, fun;
	// 		switch (designer.platform) {
	// 			case "processPlatform":
	// 				root = "x_processplatform_assemble_designer";
	// 				switch (designer.designerType) {
	// 					case "script": actionName = "ScriptAction"; fun = "listProcessScriptPattern";
	// 					case "form": actionName = "FomrAction"; fun = "listProcessFormPattern";
	// 					case "process": actionName = "ProcessAction"; fun = "listProcessProcessPattern";
	// 				}
	// 			case "cms":
	// 				root = "x_cms_assemble_control";
	// 				switch (designer.designerType) {
	// 					case "script": actionName = "ScriptAction"; fun = "listCmsScriptPattern";
	// 					case "form": actionName = "FormAction"; fun = "listCmsFormPattern";
	// 				}
	//
	// 			case "portal":
	// 				root = "x_portal_assemble_designer";
	// 				switch (designer.designerType) {
	// 					case "script": actionName = "ScriptAction"; fun = "listPortalScriptPattern";
	// 					case "page": actionName = "PageAction"; fun = "listPortalPagePattern";
	// 					case "widget": actionName = "WidgetAction"; fun = "listPortalWidgetPattern";
	// 				}
	// 			case "query":
	// 				root = "x_query_assemble_designer";
	// 				switch (designer.designerType) {
	// 					case "view": actionName = "ViewAction"; fun = "listQueryViewPattern";
	// 					case "statement": actionName = "StatementAction"; fun = "listQueryStatementPattern";
	// 					case "stat": actionName = "StatAction"; fun = "listQueryStatPattern";
	// 				}
	// 			case "service":
	// 				root = "x_program_center";
	// 				switch (designer.appId) {
	// 					case "invoke": actionName = "InvokeAction"; fun = "listServiceInvokePattern";
	// 					case "agent": actionName = "AgentAction"; fun = "listServiceAgentPattern";
	// 				}
	// 		}
	// 		this.Actions.load(root)[actionName].get(designer.designerId, function(json){
	// 			this[fun](json.data, designer.patternList, item);
	// 		}.bind(this))
	// 	}
	// },
	//

	//
	//
	// //启动一个webworker处理
	// listProcessScriptPattern: function (data, patternList, item){
	// 	patternList.each(function(pattern){
	// 		if (pattern.property == "text"){
	// 			var textArr = data.split("\n");
	// 			var regex = this.getFindRegExp();
	// 			pattern.lines.each(function(line){
	// 				var text = textArr[line];
	//
	//
	//
	// 			}.bind(this));
	// 		}else{
	//
	// 		}
	// 	}.bind(this));
	// }


});
