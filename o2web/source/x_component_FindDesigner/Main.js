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

					if (this.editor) this.editor.resize();
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

					if (this.editor) this.editor.resize();
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
		}.bind(this);
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
	createResultPatternItem: function(text, title, tree, icon, action){
		var obj = {
			"title": title,
			"text": "<span style='color: #000000'>"+text+"</span>",
			"icon": icon||"",
			"action": action || null
		}
		return tree.appendChild(obj);
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
	showFindResult: function(data,option){
		if (!this.patternCount) this.patternCount = 0;
		this.patternCount++;
		var t = this.lp.findPatternCount.replace("{n}", this.patternCount);
		this.listTitleInfoNode.set("text", t);

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
				this.createScriptPatternNode(data, designerNode, regexp);
				break;
			case "form":
				this.createFormPatternNode(data, designerNode, regexp);
				break;
			case "process":

				break;

		}
	},

	createFormPatternNode: function(data, node, regexp){
		var text = this.lp.elementPattern.replace("{element}", "&lt;"+data.pattern.type+"&gt;"+data.pattern.name).
			replace("{property}", "{"+data.pattern.key+"}"+data.pattern.propertyName);
		text = "<span style='color: #666666'>"+text+"</span>&nbsp;&nbsp;"

		if (data.pattern.line){
			if (data.pattern.evkey){
				text += "<b>["+data.pattern.evkey+"]</b>&nbsp;"+((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp);
			}else{
				text += ((data.pattern.line) ? data.pattern.line+"&nbsp;&nbsp;" : "" )+this.getPatternValue(data.pattern.value, regexp);
			}
		}else{
			text += this.getPatternValue(data.pattern.value, regexp);
		}
		if (data.pattern.mode){
			text = "<b>["+data.pattern.mode+"]</b>&nbsp;"+text;
		}

		patternNode = this.createResultPatternItem(text, "", node, "icon_"+data.pattern.propertyType+".png");
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
			this.openPatternScript(node.pattern);
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
	openPatternScript: function(pattern){
		// appId: "267d4445-b75c-4627-af26-251e623a5fe8"
		// appName: "合同"
		// designerAliase: ""
		// designerId: "ff1d382f-54af-4bb1-80f9-1e3bc3e4f03c"
		// designerName: "file"
		// designerType: "script"
		// module: "processPlatform"
		//
		// appId: "invoke"
		// appName: "接口"
		// designerAliase: ""
		// designerId: "80c08112-afa1-48b1-b693-87ba8504f47c"
		// designerName: "getService"
		// designerType: "script"
		// module: "service"
debugger;
		if (this.editor && this.editor.pattern.designerId === pattern.designerId && this.editor.pattern.module === pattern.module){
			this.reLocationEditor(pattern);
		}else{
			if (this.editor) this.editor.destroyEditor();
			this.editor = null;
			this.previewInforNode.hide().dispose();

			switch (pattern.module){
				case "processPlatform":
					o2.Actions.load("x_processplatform_assemble_designer").ScriptAction.get(pattern.designerId).then(function(json){
						if (json.data) this.openProcessPlatformPatternScript(json.data, pattern);
					}.bind(this), function(){});

					break;
				case "cms":

					break;
				case "portal":

					break;
				case "service":

					break;
			}
		}
	},
	openProcessPlatformPatternScript: function(data, pattern){
		o2.require("o2.widget.JavascriptEditor", function(){

			this.editor = new o2.widget.JavascriptEditor(this.previewContentNode, {
				"option": {"value": data.text}
			});
			this.editor.pattern = pattern;
			this.editor.load(function(){
				this.reLocationEditor(pattern);
			}.bind(this));
		}.bind(this));
	},

	reLocationEditor: function(pattern){
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
		this.listContentNode.hide();
		this.listContentNode.empty();
		this.listInfoNode.show().getFirst().set("text", "");
		this.listInfoNode.addClass("loadding");
		this.patternCount = 0;

		if (this.editor) this.editor.destroyEditor();
		this.editor = null;
		this.previewInforNode.show().inject(this.previewContentNode);

		this.getFindWorker();
		var actions = this.getActionsUrl();

		this.tree = null;
		this.getResultTree(function(){
			var workerMessage = {
				actions:actions,
				filterOption: this.filterOption,
				debug: (window.layout && layout["debugger"]),
				token: (window.layout && layout.session && layout.session.user) ? layout.session.user.token : ""
			};
			this.findWorker.postMessage(workerMessage);
		}.bind(this));
	},

	//------------------------------------------------------------

	findDesigner_bak: function(){
		this.listContentNode.hide();
		this.listInfoNode.show().getFirst().set("text", "");
		this.listInfoNode.addClass("loadding")
		o2.Actions.load("x_query_service_processing").DesignAction.search(this.filterOption, function(json){
			if ((json.data.processPlatformList && json.data.processPlatformList.length) ||
				(json.data.cmsList && json.data.cmsList.length) ||
				(json.data.portalList && json.data.portalList.length) ||
				(json.data.queryList && json.data.queryList.length) ||
				(json.data.serviceList && json.data.serviceList.length)){

				this.listInfoNode.hide();
				this.listFindResult(json.data);

			}else{
				this.listInfoNode.show().removeClass("loadding").getFirst().set("text", this.lp.nothingFind);
			}
		}.bind(this));
	},


	createResultAppItem: function(text, title, tree){
		var obj = {
			"title": title,
			"text": "<span style='font-weight: bold; color: #4A90E2'>"+text+"</span>",
			"icon": ""
		}
		return tree.appendChild(obj);
	},
	// createResultDesignerItem: function(designer, tree){
	// 	var title = this.lp[designer.designerType]+ ": "+ designer.designerName + " ("+designer.designerId+")";
	// 	var text = this.lp[designer.designerType]+ ": <b>"+ designer.designerName+"</b>";
	// 	var obj = {
	// 		"expand": false,
	// 		"title": title,
	// 		"text": text,
	// 		"icon": ""
	// 	}
	// 	var item = tree.appendChild(obj);
	// 	item.designer = designer;
	// 	item.appendChild({ "expand": false, "text": "loading...", "icon": "" });
	// 	return item;
	// },
	listFindResult: function(data){
		this.listContentNode.empty();
		this.listContentNode.show();
		o2.require("o2.widget.Tree", function(){
			var tree = new o2.widget.Tree(this.listContentNode, {
				"onQueryExpand": function(item){
					if (item.designer) this.loadDesignerPattern(item);
				}.bind(this)
			});
			tree.load();
			if (data.processPlatformList && data.processPlatformList.length){
				var platformItem = this.createResultCategroyItem(this.lp.processPlatform, this.lp.processPlatform, tree);
				this.listProcessResult(platformItem, data.processPlatformList, "processPlatform");
			}
			if (data.cmsList && data.cmsList.length){
				var platformItem = this.createResultCategroyItem(this.lp.cms, this.lp.cms, tree);
				//this.listProcessResult(categroyItem, data.cmsList);
			}
			if (data.portalList && data.portalList.length){
				var platformItem = this.createResultCategroyItem(this.lp.portal, this.lp.portal, tree);

			}
			if (data.queryList && data.queryList.length){
				var platformItem = this.createResultCategroyItem(this.lp.query, this.lp.query, tree);
			}
			if (data.serviceList && data.serviceList.length){
				var platformItem = this.createResultCategroyItem(this.lp.service, this.lp.service, tree);
			}


		}.bind(this));
	},
	addPatternCount: function(item, count){
		if (!item.count) item.count = 0;
		item.count += count;
		var t = this.lp.patternCount.replace("{n}", item.count);
		var textDivNode = item.textNode.getElement("div");
		if (textDivNode){
			var html = item.options.text;
			textDivNode.set("html", html+" <span style=''>( "+t+" )</span>");
		}
	},
	listProcessResult: function(platformItem, list, platform){
		var applicationItems = {};
		list.each(function(designer){
			if (designer.patternList && designer.patternList.length){
				var appItem = applicationItems[designer.appId];
				if (!appItem){
					applicationItems[designer.appId] = appItem = this.createResultAppItem(designer.appName, designer.appName+" ("+designer.appId+")", platformItem);
				}
				designer.platform = platform;
				var designerItem = this.createResultDesignerItem(designer, appItem);
				var count=0;
				designer.patternList.each(function(p){
					if (p.lines && p.lines.length){
						count += p.lines.length;
					}else{
						count++;
					}
				});
				// var count = designer.patternList.length;

				this.addPatternCount(designerItem, count);
				this.addPatternCount(appItem, count);
				this.addPatternCount(platformItem, count);
			}
		}.bind(this));
	},

	getDesignerObject: function(designer){
		switch (designer.platform){
			case "processPlatform":
				var action = this.Actions.load("x_processplatform_assemble_designer");
				switch (designer.designerType){
					case "script":
						return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
					case "form":
						return action.FomrAction.get(designer.designerId, function(json){return json.data;});
					case "process":
						return action.ProcessAction.get(designer.designerId, function(json){return json.data;});
				}
			case "cms":
				var action = this.Actions.load("x_cms_assemble_control");
				switch (designer.designerType){
					case "script":
						return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
					case "form":
						return action.FormAction.get(designer.designerId, function(json){return json.data;});
				}

			case "portal":
				var action = this.Actions.load("x_portal_assemble_designer");
				switch (designer.designerType){
					case "script":
						return action.ScriptAction.get(designer.designerId, function(json){return json.data;});
					case "page":
						return action.PageAction.get(designer.designerId, function(json){return json.data;});
					case "widget":
						return action.WidgetAction.get(designer.designerId, function(json){return json.data;});
				}
			case "query":
				var action = this.Actions.load("x_query_assemble_designer");
				switch (designer.designerType){
					case "view":
						return action.ViewAction.get(designer.designerId, function(json){return json.data;});
					case "statement":
						return action.StatementAction.get(designer.designerId, function(json){return json.data;});
					case "stat":
						return action.StatAction.get(designer.designerId, function(json){return json.data;});
				}
			case "service":
				var action = this.Actions.load("x_program_center");
				switch (designer.appId){
					case "invoke":
						return action.InvokeAction.get(designer.designerId, function(json){return json.data;});
					case "agent":
						return action.AgentAction.get(designer.designerId, function(json){return json.data;});
				}
		}
	},
	loadDesignerPattern: function(item){
		if (item.firstChild && item.firstChild.options.text==="loading..."){
			item.firstChild.destroy();

			var root, actionName, fun;
			switch (designer.platform) {
				case "processPlatform":
					root = "x_processplatform_assemble_designer";
					switch (designer.designerType) {
						case "script": actionName = "ScriptAction"; fun = "listProcessScriptPattern";
						case "form": actionName = "FomrAction"; fun = "listProcessFormPattern";
						case "process": actionName = "ProcessAction"; fun = "listProcessProcessPattern";
					}
				case "cms":
					root = "x_cms_assemble_control";
					switch (designer.designerType) {
						case "script": actionName = "ScriptAction"; fun = "listCmsScriptPattern";
						case "form": actionName = "FormAction"; fun = "listCmsFormPattern";
					}

				case "portal":
					root = "x_portal_assemble_designer";
					switch (designer.designerType) {
						case "script": actionName = "ScriptAction"; fun = "listPortalScriptPattern";
						case "page": actionName = "PageAction"; fun = "listPortalPagePattern";
						case "widget": actionName = "WidgetAction"; fun = "listPortalWidgetPattern";
					}
				case "query":
					root = "x_query_assemble_designer";
					switch (designer.designerType) {
						case "view": actionName = "ViewAction"; fun = "listQueryViewPattern";
						case "statement": actionName = "StatementAction"; fun = "listQueryStatementPattern";
						case "stat": actionName = "StatAction"; fun = "listQueryStatPattern";
					}
				case "service":
					root = "x_program_center";
					switch (designer.appId) {
						case "invoke": actionName = "InvokeAction"; fun = "listServiceInvokePattern";
						case "agent": actionName = "AgentAction"; fun = "listServiceAgentPattern";
					}
			}
			this.Actions.load(root)[actionName].get(designer.designerId, function(json){
				this[fun](json.data, designer.patternList, item);
			}.bind(this))
		}
	},

	getFindRegExp: function(){
		var flag = "gm";
		var keyword = this.filterOption.keyword;
		if (!this.filterOption.caseSensitive) flag+="i";
		if (this.filterOption.matchRegExp){
			return new RegExp(keyword, flag)
		}else{
			if (this.filterOption.matchWholeWord) keyword = "\\b"+keyword+"\\b";
			return new RegExp(keyword, flag)
		}
	},


	//启动一个webworker处理
	listProcessScriptPattern: function (data, patternList, item){
		patternList.each(function(pattern){
			if (pattern.property == "text"){
				var textArr = data.split("\n");
				var regex = this.getFindRegExp();
				pattern.lines.each(function(line){
					var text = textArr[line];



				}.bind(this));
			}else{

			}
		}.bind(this));
	}


});
