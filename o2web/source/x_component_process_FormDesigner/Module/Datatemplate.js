MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Component", null, false);
MWF.xApplication.process.FormDesigner.Module.Datatemplate = MWF.FCDatatemplate = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Datatemplate/datatemplate.html"
		// "disallowModules": ["subform","subpage","widget"]
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/Datatemplate/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Datatemplate/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "datatemplate";

		this.form = form;
	},
	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.clearStyles(false);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
		this._createIcon();
	},
	_createIcon: function(){
		this.iconNode = new Element("div", {
			"styles": this.css.iconNode,
			"o2icon": "datatemplate"
		}).inject(this.node, "top");
		new Element("div", {
			"styles": this.css.iconNodeIcon
		}).inject(this.iconNode);
		new Element("div", {
			"styles": this.css.iconNodeText,
			"text": "Datatemplate"
		}).inject(this.iconNode);
	},
	_loadNodeStyles: function(){
		this.iconNode = this.node.getElement("div[o2icon='datatemplate']");
		if( this.iconNode ){
			this.iconNode.setStyles(this.css.iconNode);
			this.iconNode.getFirst("div").setStyles(this.css.iconNodeIcon);
			this.iconNode.getLast("div").setStyles(this.css.iconNodeText);
		}else{
			this._createIcon()
		}
	},
	createImmediately: function(data, relativeNode, position, selectDisabled){
		this.json = data;
		this.json.id = this._getNewId();
		this._createMoveNode();
		this._dragMoveComplete( relativeNode, position, selectDisabled );
	},
	_dragComplete: function(relativeNode, position, selectDisabled){
		if (!this.node){
			this.showCreateDialog(relativeNode, position, selectDisabled );
		}else{
			this._dragMoveComplete(relativeNode, position, selectDisabled);
		}
	},
	_dragMoveComplete: function( relativeNode, position, selectDisabled ){
		this.setStyleTemplate();

		if( this.injectNoticeNode )this.injectNoticeNode.destroy();
		var overflow = this.moveNode.retrieve("overflow");
		if( overflow ){
			this.moveNode.setStyle("overflow",overflow);
			this.moveNode.eliminate("overflow");
		}

		if (!this.node){
			this._createNode();
		}
		this._resetTreeNode();

		if( relativeNode && position ){
			this.node.inject( relativeNode, position );
		}else{
			this.node.inject(this.copyNode, "before");
		}

		this._initModule();

		var thisDisplay = this.node.retrieve("thisDisplay");
		if (thisDisplay){
			this.node.setStyle("display", thisDisplay);
		}

		if (this.copyNode) this.copyNode.destroy();
		if (this.moveNode) this.moveNode.destroy();
		this.moveNode = null;
		this.copyNode = null;
		this.nextModule = null;
		this.form.moveModule = null;

		this.form.json.moduleList[this.json.id] = this.json;
		if (this.form.scriptDesigner) this.form.scriptDesigner.createModuleScript(this.json);

		if( !selectDisabled )this.selected();

		if( this.operation && !this.historyAddDelay ){
			this.addHistoryLog( this.operation, null, this.fromLog );
		}

		if( !this.historyAddDelay ){
			this.operation = null;
			this.fromLog = null;
		}
	},
	_clearDragComplete : function(){
		if( this.dragTimeout ){
			window.clearTimeout( this.dragTimeout );
			this.dragTimeout = null;
		}
		if( this.injectNoticeNode )this.injectNoticeNode.destroy();
		if (this.copyNode) this.copyNode.destroy();
		if (this.moveNode) this.moveNode.destroy();
		this.moveNode = null;
		this.copyNode = null;
		this.nextModule = null;
		this.form.moveModule = null;
		// delete this;
	},
	showCreateDialog: function(relativeNode, position){
		var module = this;
		var url ="../x_component_process_FormDesigner/Module/Datatemplate/createDialog.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();
			var x = size.x/2-180;
			var y = size.y/2-100;

			var dlg = new MWF.DL({
				"title": "Insert",
				"style": "property",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-65,
				"fromLeft": size.x/2,
				"width": 360,
				"height": 200,
				"url": url,
				"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate,
				"buttonList": [
					{
						"text": MWF.APPFD.LP.button.ok,
						"action": function(){
							var widthModules = "no";
							dlg.node.getElements(".widthModules").each( function (el) {
								if( el.get("checked") )widthModules = el.get("value");
							});

							if( widthModules === "yes" ){

								var wrapDiv = "yes";
								dlg.node.getElements(".wrapDiv").each( function (el) {
									if( el.get("checked") )wrapDiv = el.get("value");
								});

								module.appendModules(relativeNode, position, wrapDiv);
							}else{
								module._dragMoveComplete( relativeNode, position );
							}
							this.close();
						}
					},
					{
						"text": MWF.APPFD.LP.button.cancel,
						"action": function(){
							module._clearDragComplete();
							this.close();
						}
					}
				],
				"onPostShow": function(){
					var tr = dlg.node.getElement(".wrapDivTr");
					dlg.node.getElements(".widthModules").addEvent("change", function (el) {
						tr.setStyle("display", el.target.get("value") === "yes" ? "" : "none")
					})
				}.bind(this)
			});

			dlg.show();
		}.bind(this));
	},
	getModulesTemplateUrl: function(){
		return "../x_component_process_FormDesigner/Module/Datatemplate/modulesTemplate.json";
	},
	appendModules: function( relativeNode, position, wrapDiv ){
		debugger;
		MWF.getJSON(this.getModulesTemplateUrl(), function(responseJSON, responseText){

			var parentModule = this.parentContainer || this.inContainer || this.onDragModule;
			this.containerModule = this.form.createModuleImmediately(
				"Div", parentModule, relativeNode || this.copyNode, position || "before", true, false);

			var containerNode = this.containerModule.node;

			// var dataStr = null;
			// if (this.form.options.mode !== "Mobile"){
			// 	dataStr = responseJSON.data;
			// }else{
			// 	dataStr = responseJSON.mobileData;
			// }
			// var data = null;
			// if (dataStr){
			// 	// data = JSON.decode(MWF.decodeJsonString(dataStr));
			// 	data = JSON.decode(dataStr);
			// }
			var data;
			if (this.form.options.mode !== "Mobile"){
				data = responseJSON.data;
			}else{
				data = responseJSON.mobileData;
			}

			var tmpNode = new Element("div").inject( this.form.container );
			tmpNode.set("html", data.html);
			var html = tmpNode.get("html");
			tmpNode.destroy();

			containerNode.set("html", html );

			//替换重复id
			var changedIdMap = {};
			var dataTemplateModuleJson;
			Object.each(data.json.moduleList, function (moduleJson) {
				if( !dataTemplateModuleJson && moduleJson.type === "Datatemplate" ){
					dataTemplateModuleJson = moduleJson;
				}
				var oid = moduleJson.id;
				var id = moduleJson.id;
				var idx = 1;

				// while (this.form.json.moduleList[id]) {
				// 	id = oid + "_" + idx;
				// 	idx++;
				// }

				while (this.form.checkModuleId(id, moduleJson.type).elementConflict){
					id = oid + "_" + idx;
					idx++;
				}

				if (oid !== id) {
					changedIdMap[oid] = id;
					moduleJson.id = id;
					var moduleNode = containerNode.getElementById(oid);
					if (moduleNode) moduleNode.set("id", id);
				}
				this.form.json.moduleList[moduleJson.id] = moduleJson;
			}.bind(this));

			if( Object.keys(changedIdMap).length > 0 ){
				this.form.designer.checkDatatemplateRelativeId( dataTemplateModuleJson, changedIdMap );
			}

			var moduleList = [];
			if( wrapDiv !== "yes" ) {
				var moduleNodeList = [];
				containerNode.getChildren().each(function (el) {
					if (el.get("MWFType") && el.get("id")) {
						moduleNodeList.push(el);
						var id = el.get("id");
						el.inject(relativeNode || this.copyNode, position || "before");
					}
				}.bind(this));

				this.containerModule.destroy();
				this._clearDragComplete();

				moduleNodeList.each(function (el) {
					var json = this.form.getDomjson(el);
					var module = this.form.loadModule(json, el, parentModule);
					moduleList.push(module);
				}.bind(this));
				this.containerModule = null;
			}else{
				this._clearDragComplete();
				moduleList = [this.containerModule];
				this.form.parseModules(this.containerModule, containerNode);
			}

			// this.form.parseModules( parentModule, parentModule.node);
			//this.containerModule.delete();

			if(dataTemplateModuleJson){
				var node = parentModule.node.getElementById(dataTemplateModuleJson.id);
				if(node && node.retrieve("module")){
					node.retrieve("module").selected();
				}
			}

			if( this.operation && !this.historyAddDelay ){
				this.addHistoryLog( this.operation, moduleList, this.fromLog, this.json.id, "Datatemplate" );
			}

			if( !this.historyAddDelay ){
				this.operation = null;
				this.fromLog = null;
			}

		}.bind(this));
	},
	// checkRelativeId: function( json, idMap ){
	// 	["outerAddActionId","outerDeleteActionId","outerSelectAllId",
	// 		"addActionId","deleteActionId","sequenceId","selectorId"].each(function(key){
	// 		var str = json[key];
	// 		if(str){
	// 			var strArr;
	// 			if( str.indexOf("/") > -1 ) {
	// 				strArr = str.split("/");
	// 			}else if(str.indexOf(".*.") > -1){
	// 				strArr = str.split(".*.");
	// 			}
	// 			if(strArr){
	// 				strArr = strArr.map(function (s) {
	// 					return idMap[s] || s;
	// 				});
	// 				json[key] = strArr.join("/");
	// 			}else{
	// 				if( str && idMap[str] ){
	// 					json[key] = idMap[str];
	// 				}
	// 			}
	// 		}
	//
	// 	}.bind(this));
	// },
	// _getDroppableNodes: function(){
	// 	var nodes = [this.form.node].concat(this.form.moduleElementNodeList, this.form.moduleContainerNodeList, this.form.moduleComponentNodeList);
		// this.form.moduleList.each( function(module){
		// 	//数据模板不能往数据模板里拖
		// 	if( module.moduleName === "datatemplate" ){
		// 		var subDoms = this.form.getModuleNodes(module.node);
		// 		nodes.erase( module.node );
		// 		subDoms.each(function (dom) {
		// 			nodes.erase( dom );
		// 		})
		// 	}
		// }.bind(this));
		// return nodes;
	// },
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},
	_createMoveNode: function(){

		this.moveNode = new Element("div", {
			"MWFType": "datatemplate",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},

	_initModule: function(){
		if (!this.initialized){
			if (this.json.initialized!=="yes")this.setStyleTemplate();

			// this._getElements();
			// this._getContainers();

			this.setPropertiesOrStyles("styles");
			this.setPropertiesOrStyles("properties");

			if( !this.json.impExpTableStyles ){
				this.setImpExpTableStyles()
			}

			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();

			//     this.checkSequenceShow();

			this._setNodeEvent();

			this.initialized = true;
			this.json.initialized = "yes";
		}
	},
	setImpExpTableStyles: function(){
		//设置导入导出表格样式
		if(this.form.templateStyles && this.form.templateStyles.table){
			this.json.impExpTableStyles = Object.merge(this.json.impExpTableStyles||{}, this.form.templateStyles.table.styles||{});
			this.json.impExpTableTitleStyles = Object.merge(this.json.impExpTableTitleStyles||{}, this.form.templateStyles.table.titleStyles||{});
			this.json.impExpTableContentStyles = Object.merge(this.json.impExpTableContentStyles||{}, this.form.templateStyles.table.contentStyles||{});
			this.json.impExpTableProperties = Object.merge(this.json.impExpTableProperties||{}, this.form.templateStyles.table.properties||{});
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
			var border = this.node.getStyle("border");
			this.node.clearStyles();
			this.node.setStyles(this.css.moduleNode);
			this.node.setStyle("border", border);
			Object.each(this.json.styles, function(value, key){
				var reg = /^border\w*/ig;
				if (!key.test(reg)){
					this.node.setStyle(key, value);
				}
			}.bind(this));
		}

		if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
			this.node.setProperties(this.json.properties);
		}
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		if (name=="id"){
			if (name!=oldValue){
				// var reg = new RegExp("^"+oldValue, "i");
				// this.containers.each(function(container){
				// 	var id = container.json.id;
				// 	var newId = id.replace(reg, this.json.id);
				// 	container.json.id = newId;
				//
				// 	delete this.form.json.moduleList[id];
				// 	this.form.json.moduleList[newId] = container.json;
				// 	container._setEditStyle("id");
				// }.bind(this));
			}
		}
		//if (name=="sequence") this.checkSequenceShow();
	},
	// _dragIn: function(module){
	// 	if (this.options.disallowModules.indexOf(module.moduleName)===-1){
	// 		module.onDragModule = this;
	// 		if (!this.Component) module.inContainer = this;
	// 		module.parentContainer = this;
	// 		module.nextModule = null;
	//
	// 		this.node.setStyles({"border": "1px solid #ffa200"});
	//
	// 		if (module.controlMode){
	// 			if (module.copyNode) module.copyNode.hide();
	// 		}else{
	// 			var copyNode = module._getCopyNode(this);
	// 			copyNode.show();
	// 			copyNode.inject(this.node);
	// 		}
	// 	}else{
	// 		this.parentContainer._dragIn(module);
	// 	}
	// },
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("properties");

		this.setImpExpTableStyles();

		this.reloadMaplist();
	},

	getContainerNodes: function(){
		return this.node.getElements("td");
	},
	copyComponentJsonData: function(newNode, pid){
		var tds = newNode.getElements("td");
		var ths = newNode.getElements("th");
		tds.each(function(td, idx){
			var newContainerJson = Object.clone(this.containers[idx].json);
			newContainerJson.id = this.containers[idx]._getNewId(pid);
			this.form.json.moduleList[newContainerJson.id] = newContainerJson;
			td.set("id", newContainerJson.id);
		}.bind(this));
		ths.each(function(th, idx){
			var newElementJson = Object.clone(this.elements[idx].json);
			newElementJson.id = this.elements[idx]._getNewId(pid);
			this.form.json.moduleList[newElementJson.id] = newElementJson;
			th.set("id", newElementJson.id);
		}.bind(this));
	},
	_getDirectSubModuleJson: function(node, moduleJsons){
		var subNode = node.getFirst();
		while (subNode){
			var module = subNode.retrieve("module");
			var flag = module && !["datatable","datagrid","datatemplate"].contains(module.moduleName);
			if (flag) {
				moduleJsons[module.json.id] = Object.clone(module.json);
			}
			if( !module || flag){
				this._getDirectSubModuleJson(subNode, moduleJsons);
			}
			subNode = subNode.getNext();
		}
	},
	getExpImpFieldJson: function () {
		var o = {};
		var list = [];
		this._getDirectSubModuleJson(this.node, o);
		for( var key in o ){
			var json = o[key];
			if(this.form.options.fields.contains(json.type) && !["Office"].contains(json.type)){
				list.push({
					"field": json.id,
					"title": json.name || ""
				});
			}
		}
		return list;
	},

	changeRelativeId: function (idMap) {
		this.checkRelativeId(this.json, idMap);
	}

});
