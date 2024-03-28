MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.require("MWF.widget.Common", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Property", null, false);
MWF.xApplication.process.FormDesigner.Module.$Module = MWF.FC$Module = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"actions": [
			{
				"name": "move",
				"icon": "move1.png",
				"event": "mousedown",
				"action": "move",
				"title": MWF.APPFD.LP.formAction.move
			},
			{
				"name": "copy",
				"icon": "copy1.png",
				"event": "mousedown",
				"action": "copy",
				"title": MWF.APPFD.LP.formAction.copy
			},
			{
				"name": "delete",
				"icon": "delete1.png",
				"event": "click",
				"action": "delete",
				"title": MWF.APPFD.LP.formAction["delete"]
			},
			{
				"name": "selectParent",
				"icon": "selectParent.png",
				"event": "click",
				"action": "selectParent",
				"title": MWF.APPFD.LP.formAction["selectParent"]
			}
			// {
			//     "name": "styleBrush",
			//     "icon": "styleBrush.png",
			//     "event": "click",
			//     "action": "styleBrush",
			//     "title": MWF.APPFD.LP.formAction["styleBrush"]
			// }
		],
		"actionNodeStyles": {
			"width": "16px",
			"height": "16px",
			"margin-left": "2px",
			"margin-right": "2px",
			"float": "left",
			"border": "1px solid #F1F1F1",
			"cursor": "pointer"
		},

		"injectActions" : [
			{
				"name" : "before",
				"styles" : "injectActionBefore",
				"event" : "click",
				"action" : "injectBefore",
				"title": MWF.APPFD.LP.formAction["insertBefore"]
			},
			{
				"name" : "after",
				"styles" : "injectActionAfter",
				"event" : "click",
				"action" : "injectAfter",
				"title": MWF.APPFD.LP.formAction["insertAfter"]
			}
		],


		"propertyPath": "../x_component_process_FormDesigner/Module/Label/label.html"
	},

	_getNewId: function(prefix, moduleName){
		var p = "";
		if (prefix){
			p = prefix+"_";
		}
		if (!moduleName) moduleName = this.moduleName;
		var idx = 1;
		var id = p+moduleName;

		var type = (this.json) ? this.json.type : this.moduleName.capitalize();
		while (this.form.checkModuleId(id, type).elementConflict || this.form.json.moduleList[id]){
			//while (this.form.json.moduleList[id]){
			id = p+moduleName+"_"+idx;
			idx++;
		}
		return id;
	},
	_getCopyId: function( oid ){
		var id = oid;
		var idx = 1;
		while (this.form.json.moduleList[id]) {
			id = oid + "_" + idx;
			idx++;
		}
		return id;
	},
	load : function(json, node, parent){

		this.json = json;
		this.node= node;
		this.node.store("module", this);
		this.node.setStyles(this.css.moduleNode);

		this._loadNodeStyles();

		this._loadTreeNode(parent);
		this.parentContainer = this.treeNode.parentNode.module;

		this._initModule();
		this._setEditStyle_custom("id");
		this.json.moduleName = this.moduleName;
	},
	_loadNodeStyles: function(){
	},
	_loadNodeCustomStyles: function(){
		this.setCustomStyles();
	},
	_loadTreeNode: function(parent){
		var title = this.json.name || this.json.id;
		var text = "";
		if (this.json.type==="Common"){
			text = this.json.tagName+"(Common)";
		}else{
			text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
		}
		var o = {
			"expand": true,
			"title": this.json.id,
			"text": "&lt;"+text+"&gt; "+title,
			"icon": ""
		};
		o.action = function(){
			if (this.module) this.module.selected();
		};

		if (this.nextModule){
			this.treeNode = this.nextModule.treeNode.insertChild(o);
		}else{
			this.treeNode = parent.treeNode.appendChild(o);
		}
		this.treeNode.module = this;
	},
	copyStyles: function(from, to){
		if( this.form.isForceClearCustomStyle() ){
			this.json[to] = {};
		}else{
			if (!this.json[to]) this.json[to] = {};
		}
		Object.each(from, function(style, key){
			//if (!this.json[to][key])
			this.json[to][key] = style;
		}.bind(this));
	},
	removeStyles: function(from, to){
		if( this.form.isForceClearCustomStyle() ){
			this.json[to] = {};
		}else{
			if (this.json[to]){
				Object.each(from, function(style, key){
					if (this.json[to][key] && this.json[to][key]==style){
						delete this.json[to][key];
					}
				}.bind(this));
			}
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setStyleTemplate: function(){
		//if (this.form.stylesList){
		//	if (this.form.json.formStyleType){
		//		if (this.form.stylesList[this.form.json.formStyleType]){
		//			if (this.form.stylesList[this.form.json.formStyleType][this.moduleName]){
		//				this.setTemplateStyles(this.form.stylesList[this.form.json.formStyleType][this.moduleName]);
		//			}
		//		}
		//	}
		//}
		if( this.form.templateStyles && this.form.templateStyles[this.moduleName] ){
			this.setTemplateStyles(this.form.templateStyles[this.moduleName]);
		}
	},
	setAllStyles: function(){
		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("inputStyles");
		this.setPropertiesOrStyles("properties");
		this.reloadMaplist();
	},
	_initModule: function(){
		if (!this.json.isSaved) this.setStyleTemplate();

		this._resetModuleDomNode();

		this.setPropertiesOrStyles("styles");
		this.setPropertiesOrStyles("inputStyles");
		this.setPropertiesOrStyles("properties");

		this._setNodeProperty();
		if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		this.json.isSaved = true;
	},
	_resetModuleDomNode: function(){},
	_setNodeProperty: function(){},

	_createIconAction: function(){
		if (!this.actionArea){
			this.actionArea = new Element("div", {
				styles: {
					"display": "none",
					//	"width": 18*this.options.actions.length,
					"position": "absolute",
					"background-color": "#F1F1F1",
					"padding": "1px",
					"padding-right": "0px",
					"border": "1px solid #AAA",
					"box-shadow": "0px 2px 5px #999",
					"opacity": 1,
					"z-index": 100
				}
			}).inject(this.form.container, "after");

			this.options.actions.each(function(action){
				var actionNode = new Element("div", {
					"styles": this.options.actionNodeStyles,
					"title": action.title
				}).inject(this.actionArea);
				if( action.name === "selectParent" ){
					actionNode.setStyle("background", "url(../x_component_process_FormDesigner/Module/Form/default/icon/selectParent.png) no-repeat left center");
				}else{
					actionNode.setStyle("background", "url("+this.path+this.options.style+"/icon/"+action.icon+") no-repeat left center");
				}
				actionNode.addEvent(action.event, function(e){
					this[action.action](e);
				}.bind(this));
				actionNode.addEvents({
					"mouseover": function(e){
						e.target.setStyle("border", "1px solid #999");
					}.bind(this),
					"mouseout": function(e){
						e.target.setStyle("border", "1px solid #F1F1F1");
					}.bind(this)
				});


			}.bind(this));

			this._createCustomIconAction();
		}
	},



	_createCustomIconAction: function(){},

	_setActionAreaPosition: function(){
		var p = this.node.getPosition(this.form.node.getOffsetParent());
		var y = p.y-25;
		var x = p.x;
		this.actionArea.setPosition({"x": x, "y": y});
	},



	_moveTo: function(container){
		this.parentContainer = container;
		if (!this.node){
			this._createNode();
		}
		this._resetTreeNode();
		this.node.inject(container.node);
	},
	move: function(e, operation){
		this._createMoveNode();
		var thisDisplay = this.node.getStyle("display");
		this.node.store("thisDisplay", thisDisplay);
		this.node.setStyle("display", "none");
		this._setNodeMove(e, operation || "move");
	},
	copy: function(e){
		this.copyTo().move(e, "copy");
	},
	copyTo: function(node){
		if (!node) node = this.form;

		var newNode = this.node.clone(true, true);
		var newModuleJson = Object.clone(this.json);
		newNode.inject(node.node);

		var className = this.moduleName.capitalize();
		var prefix = (this.form.moduleType=="page") ? "PC" : "FC";
		if( this.form.designer.options.name.contains("cms.") ){
			prefix = "CMSFC";
		}
		var newTool = new MWF[prefix+className](this.form);
        var oldId = newModuleJson.id;
		newTool.json = newModuleJson;
		newModuleJson.id = newTool._getNewId();
		newNode.set("id", newModuleJson.id);
        if( this.form.copyedModule && this.form.copyedModule.checkCopySubModule){
			this.form.copyedModule.checkCopySubModule(newModuleJson, oldId);
		}
		this.form.json.moduleList[newModuleJson.id] = newModuleJson;
		if (this.form.scriptDesigner) this.form.scriptDesigner.createModuleScript(newModuleJson);


		newTool.load(newModuleJson, newNode, node);
		return newTool;
	},
	"delete": function(e){
		var module = this;
		this.form.designer.shortcut = false;
		this.form.designer.confirm("warn", module.node, MWF.APPFD.LP.notice.deleteElementTitle, MWF.APPFD.LP.notice.deleteElement, 300, 120, function(){

			module.form.selected();
			module.form.designer.shortcut = true;

			module.addHistoryLog("delete");

			module.destroy();
			this.close();
		}, function(){
			module.form.designer.shortcut = true;
			this.close();
		}, null);
	},
	selectedContainer: function(){
		debugger;
		if (this.parentContainer) this.parentContainer.selected();
	},
	styleBrush: function(){
		//@todo
		this.form.styleBrushContent = Object.clone(this.json.styles);
		if (this.json.inputStyles) this.form.inputStyleBrushContent = Object.clone(this.json.inputStyles);
	},
	selectParent: function(){
		var parentModule = this.getParentModule();
		if(parentModule){
			parentModule.selected();
			if( parentModule.actionArea ){

			}
		}
	},
	getParentModule: function(){
		var module;
		var parent = this.node.getParent();
		while(parent) {
			var MWFtype = parent.get("MWFtype");
			if( MWFtype ){
				module = parent.retrieve("module");
				if( module )return module;
			}else{
				parent = parent.getParent();
			}
		}
		return null;
	},

	_setNodeEvent: function(){
		if (this.form.moduleType!="subform" && this.form.moduleType!="widget" && this.form.moduleType!="subpage"){
			if (!this.isSetEvents){
				this.node.addEvent("click", function(e){
					if (!this.form.noSelected) this.selected();
					this.form.noSelected = false;
					e.stopPropagation();
				}.bind(this));

				this.node.addEvent("mouseover", function(e){
					this.over();
					e.stopPropagation();
				}.bind(this));
				this.node.addEvent("mouseout", function(e){
					this.unOver();
					e.stopPropagation();
				}.bind(this));

				this.node.addEvent("copy", function(e){
					this.copyModule(e);
				});

				this._setOtherNodeEvent();
				this.isSetEvents = true;
			}
		}
	},

	copyModule: function(e){
	},

	_setOtherNodeEvent: function(){},

	over: function(){
		if (!this.form.moveModule) if (this.form.currentSelectedModule!=this){
			this.node.store("normalBorder", this.node.getBorder());
			this.node.setStyles({
				"border-width": "1px",
				"border-color": "#4e73ff"
			});
		}
	},
	unOver: function(){
		if (!this.form.moveModule) if (this.form.currentSelectedModule!=this){
			this.node.setStyles({
				"border-width": "1px",
				"border-color": "#333"
			});
			var border = this.node.retrieve("normalBorder");
			this.node.setStyles(border);
		}
	},
	_showActions: function(){
		if (this.actionArea){
			if (this.options.actions.length){
				this._setActionAreaPosition();
				this.actionArea.setStyle("display", "block");
			}
		}
	},
	_hideActions: function(){
		if (this.actionArea) this.actionArea.setStyle("display", "none");
	},
	selected: function(force){
		if (this.form && this.form.node)this.form.node.focus();
		if (this.form.currentSelectedModule){
			if (!force && this.form.currentSelectedModule==this){
				return true;
			}else{
				this.form.currentSelectedModule.unSelected();
			}
		}
		if (this.form.propertyMultiTd){
			this.form.propertyMultiTd.hide();
			this.form.propertyMultiTd = null;
		}
		this.form.unSelectedMulti();
		this.node.setStyles({
			"border-width": "1px",
			"border-color": "red"
		});

		this._showActions();

		this.form.currentSelectedModule = this;

		if (this.treeNode){
			this.treeNode.selectNode();

			(new Fx.Scroll(this.form.designer.propertyDomScrollArea)).toElement(this.treeNode.node);

			//	this.treeNode.node.scrollIntoView();
		}

		var historyLog;
		if( this.form.brushStyle || this.form.brushInputStyle ){
			historyLog = this.createHistoryLog();
		}
		if (this.form.brushStyle){
			this.json.styles = Object.clone(this.form.brushStyle);
			this.setPropertiesOrStyles("styles");
			// if (this.property) this.property.loadMaplist();
		}
		if (this.form.brushInputStyle){
			this.json.inputStyles = Object.clone(this.form.brushInputStyle);
			this.setPropertiesOrStyles("inputStyles");
			// if (this.property) this.property.loadMaplist();
		}
		if( this.form.brushStyle || this.form.brushInputStyle ){
			if (this.property) this.property.loadMaplist();
			this.addHistoryLog("styleBrush", null, historyLog);
		}
		this.showProperty();
	},
	unSelected: function(){
		this.node.setStyles({
			"border-width": "1px",
			"border-color": "#333"
		});
		var border = this.node.retrieve("normalBorder");
		this.node.setStyles(border);

		this._hideActions();
		this.form.currentSelectedModule = null;

		this.hideProperty();
	},


	selectedMulti: function(){
		if (this.form.selectedModules.indexOf(this)==-1){
			this.form.selectedModules.push(this);
			this.node.setStyle("border-color", "red");
		}
	},
	unSelectedMulti: function(){
		if (this.form.selectedModules.indexOf(this)!=-1){
			this.form.selectedModules.erase(this);
			this.node.setStyle("border-color", "#333");
		}
	},

	showProperty: function(callback){
		if (!this.property){
			this.property = new MWF.xApplication.process.FormDesigner.Property(this, this.form.designer.propertyContentArea, this.form.designer, {
				"path": this.options.propertyPath,
				"onPostLoad": function(){
					this.property.show();
					this.isPropertyLoaded = true;
					if (callback) callback();
				}.bind(this),
				"onPostShow": function () {
					// if( this.form.history )this.originalJson = Object.clone(this.json);
				}.bind(this)
			});
			this.property.load();
		}else{
			this.property.show();
			if (callback) callback();
		}
	},
	hideProperty: function(){
		if (this.property) this.property.hide();
	},
	setBrushStyle: function( json ){
		this.json.styles = Object.clone(json.styles || {});
		this.setPropertiesOrStyles("styles");

		this.json.inputStyles = Object.clone(json.inputStyles || {});
		this.setPropertiesOrStyles("inputStyles");

		if (this.property) this.property.loadMaplist();
	},

	create: function(data, e, group){
		data.moduleGroup = group;
		this.json = data;
		this.json.id = this._getNewId();
		if (this.json.id){
			this._createMoveNode();
			this._setNodeMove(e, "create");
		}
	},
	createImmediately: function(data, relativeNode, position, selectDisabled){
		this.json = data;
		this.json.id = this._getNewId();
		this._createMoveNode();
		this._dragComplete( relativeNode, position, selectDisabled );
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "label",
			"styles": this.css.moduleNodeMove,
			"text": "Text",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_onEnterOther: function(dragging, inObj){
	},
	_onLeaveOther: function(dragging, inObj){
		if (this.copyNode) this.copyNode.destroy();
		this.copyNode = null;
	},
	_onMoveEnter: function(dragging, inObj){
		var module = inObj.retrieve("module");
		if (module) module._dragIn(this);
		this._onEnterOther(dragging, inObj);
	},
	_getDroppableNodes: function(){
		return [this.form.node].concat(this.form.moduleElementNodeList, this.form.moduleContainerNodeList, this.form.moduleComponentNodeList);
	},
	_setNodeMove: function(e, operation){
		this._setMoveNodePosition(e);
		this.form.node.focus();
		var droppables = this._getDroppableNodes();

		this.operation = operation;
		if( this.form.history && operation === "move" ){
			this.fromLog = { path: this.form.history.getPath( this.node ) };
		}

		var nodeDrag = new Drag.Move(this.moveNode, {
			"droppables": droppables,
			"onEnter": function(dragging, inObj){
				this._onMoveEnter(dragging, inObj);
			}.bind(this),
			"onLeave": function(dragging, inObj){
				var module = inObj.retrieve("module");
				if (module) module._dragOut(this);
				this._onLeaveOther(dragging, inObj);
			}.bind(this),
			"onDrag": function(e){
				this._nodeDrag(e, nodeDrag);
			}.bind(this),
			"onDrop": function(dragging, inObj){
				if (inObj){
					var module = inObj.retrieve("module");
					if (module) module._dragDrop(this);
					this._nodeDrop( module );
				}else{
					this._dragCancel(dragging);
				}
			}.bind(this),
			"onCancel": function(dragging){
				this._dragCancel(dragging);
			}.bind(this)
		});
		nodeDrag.start(e);

		this.form.moveModule = this;
		this.form.recordCurrentSelectedModule = this.form.currentSelectedModule;

		//    var d = (new Date()).getTime();
		this.form.selected();

		//var d1 = (new Date()).getTime();
		//alert((d1-d))
	},

	_setMoveNodePosition: function(e){
//		var x = e.event.clientX+2;
//		var y = e.event.clientY+2;
		var x = e.page.x+2;
		var y = e.page.y+2;
		this.moveNode.positionTo(x, y);
//		this.moveNode.setStyles({
//			"top": y,
//			"left": x
//		});
	},
	_getCopyNode: function(module){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyles(this.css.moduleNodeShow);
		this.copyNode.empty();
		if (module){
			try{
				var display = module.node.getStyle("display").toString().toLowerCase();
				this.copyNode.setStyle("display", display);
				if (display==="inline" || display==="inline-block"){

					var h = module.node.getSize().y-2;
					this.copyNode.setStyle("height", ""+h+"px");
					//this.copyNode.setStyle("display", "inline");
				}
			}catch(e){}
		}
		//	this.copyNode.setStyle("display", "block");
		return this.copyNode;
	},
	_positionCopyNode: function(copyNode, isIn){
		var display = this.node.getStyle("display");
		copyNode.setStyle("margin", "0");

		if (display.indexOf("inline") !==-1){
			var size = this.node.getComputedSize();
			copyNode.setStyle("position", "absolute");
			copyNode.setStyle("width", "1px");
			copyNode.setStyle("min-width", "0");
			copyNode.setStyle("height", size.height+"px");
			copyNode.position({
				relativeTo: this.node,
				position: 'leftTop',
				edge: 'rightTop'
			})
		}else{
			var w = copyNode.getStyle("width").toFloat();
			if (!w) w = copyNode.getSize().x;
			copyNode.setStyle("position", "absolute");
			copyNode.setStyle("width", ""+w+"px");
			copyNode.position({
				relativeTo: this.node,
				position: (!!isIn) ? 'leftBottom': 'leftTop',
				edge: 'leftBottom'
			})
		}
	},
	_createCopyNode: function(){
		this.copyNode = this.moveNode.clone();
		this.copyNode.setStyles(this.css.moduleNodeShow);
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},

	_showInjectAction : function( module ){
		if ( module.moveNode ){
			module.moveNode.setStyle("display","none");
		}

		this.draggingModule = module;

		//if( !this.node.getFirst() ){
		//	this.inject( "top" );
		//	return;
		//}

		if( !this.injectActionArea )this._createInjectAction();
		this.injectActionArea.setStyle("display","block");
		this._setInjectActionAreaPosition();

		this.injectActionEffect = new Fx.Morph(this.injectActionArea, {
			duration: 200,
			transition: Fx.Transitions.Sine.easeOut
		});
		this.injectActionEffect.start(this.form.css.injectActionArea_to);
	},
	_hideInjectAction : function(){
		this.draggingModule = null;
		if( this.injectActionArea ){
			this.injectActionArea.setStyle("display","none");
			// if (!this.injectActionEffect){
			// 	this.injectActionEffect = new Fx.Morph(this.injectActionArea, {
			// 		duration: 200,
			// 		transition: Fx.Transitions.Sine.easeOut
			// 	});
			// }
			//
			// var y = this.form.css.injectActionArea_to.top.toInt();
			// var x = this.form.css.injectActionArea_to.left.toInt();
			// y = y+60;
			// x = x+60;
			// // this.form.css.injectActionArea.top = ""+y+"px";
			// // this.form.css.injectActionArea.left = ""+x+"px";
			// this.injectActionEffect.start({
			// 	"width": "0px",
			// 	"height": "0px",
			// 	"top": ""+y+"px",
			// 	"left": ""+x+"px"
			// }).chain(function(){
			// 	this.injectActionArea.setStyle("display","none");
			// }.bind(this));
		}
	},
	_createInjectAction : function(){
		var css = this.form.css;
		if( !this.injectActionArea ){
			this.injectActionArea = new Element("div", { styles: css.injectActionArea }).inject(this.form.container, "after");

			this.injectActionTopBGNode = new Element("div", { styles : css.injectActionTopBGNode }).inject( this.injectActionArea );
			this.injectActionLeftBGNode = new Element("div", { styles : css.injectActionLeftBGNode }).inject( this.injectActionArea );
			this.injectActionRightBGNode = new Element("div", { styles : css.injectActionRightBGNode }).inject( this.injectActionArea );
			this.injectActionBottomBGNode = new Element("div", { styles : css.injectActionBottomBGNode }).inject( this.injectActionArea );

			var injectActions = {};
			this.options.injectActions.each( function( action ){
				injectActions[ action.name ] = action;
			});

			if( injectActions.before )this._createInjectActionNode( injectActions.before, this.injectActionTopBGNode );
			if( injectActions.top )this._createInjectActionNode( injectActions.top, this.injectActionLeftBGNode );
			if( injectActions.bottom )this._createInjectActionNode( injectActions.bottom, this.injectActionRightBGNode );
			if( injectActions.after )this._createInjectActionNode( injectActions.after, this.injectActionBottomBGNode );

			new Element("div", {
				styles : css.injectActionCancelNode,
				events : {
					click : function(){
						this.draggingModule._dragCancel();
						this._dragDrop( this.node, true );
						this._hideInjectAction();
					}.bind(this),
					mouseover : function(){
						this.setStyles( css.injectActionCancelNode_over )
					},
					mouseout : function(){
						this.setStyles( css.injectActionCancelNode )
					}
				}
			}).inject(this.injectActionArea);

		}
	},
	_createInjectActionNode : function( action, relativeNode ){
		var actionNode = new Element("div", {
			"styles": this.form.css[action.styles],
			"title": action.title
		}).inject( this.injectActionArea );
		actionNode.addEvent(action.event, function(e){
			this[action.action](e);
		}.bind(this));
		actionNode.addEvents({
			"mouseover": function(e){
				relativeNode.setStyle("background", "#ddd");
				this.draggingModule.copyNode.setStyle("display","");
				this.draggingModule.copyNode.inject( this.node, action.name );
			}.bind(this),
			"mouseout": function(e){
				relativeNode.setStyle("background", "transparent");
			}.bind(this)
		});
		relativeNode.set("title",action.title);
		relativeNode.addEvent(action.event, function(e){
			this[action.action](e);
		}.bind(this));
		relativeNode.setStyle("cursor","pointer");
		relativeNode.addEvents({
			"mouseover": function(e){
				relativeNode.setStyle("background", "#ddd");
				this.draggingModule.copyNode.setStyle("display","");
				this.draggingModule.copyNode.inject( this.node, action.name );
			}.bind(this),
			"mouseout": function(e){
				relativeNode.setStyle("background", "transparent");
				//this.draggingModule.copyNode.setStyle("display","none");
			}.bind(this)
		});
	},
	_setInjectActionAreaPosition: function(){
		var e = window.event || {};
		var formOffset = this.form.node.getOffsetParent().getPosition();
		//var p = this.node.getPosition(this.form.node.getOffsetParent());
		var y = e.pageY - formOffset.y;
		var x = e.pageX - formOffset.x;
		this.injectActionArea.setPosition({"x": x, "y": y});

		y = y-60;
		x = x-60;
		this.form.css.injectActionArea_to.top = ""+y+"px";
		this.form.css.injectActionArea_to.left = ""+x+"px";
	},
	injectBefore : function( e ){
		this.inject( "before" )
	},
	injectAfter : function( e ){
		this.inject( "after" )
	},
	injectTop : function( e ){
		this.inject( "top" )
	},
	injectBottom : function( e ){
		this.inject( "bottom" )
	},
	inject : function( position ){
		if ( this.draggingModule.moveNode ){
			this.draggingModule.moveNode.setStyle("display","");
		}
		this.draggingModule._dragComplete( this.node, position );
		this._dragDrop( this.node, true );
		this._hideInjectAction();
	},

	_nodeDrop: function( module ){
		if( this.dragTimeout ){
			window.clearTimeout( this.dragTimeout );
			this.dragTimeout = null;
		}
		if (this.parentContainer){
			var available = true;
			if( !this.options.injectActions )available = false;
			// if( module && module.moduleName === "datagrid$Data" )available = false;
			// if( module.parentContainer && module.parentContainer.moduleName == "datagrid$Data" )available = false;
			if( module && ["datagrid$Data"].contains(module.moduleName) )available = false;
			if( module.parentContainer && ["datagrid$Data"].contains(module.parentContainer.moduleName) )available = false;
			if( module.moduleName === "datatable$Data" && !module.options.allowModules.contains( this.moduleName ) )available = false;
			if( module.parentContainer && module.parentContainer.moduleName === "datatable$Data" &&
				!module.parentContainer.options.allowModules.contains( this.moduleName ) )available = false;
			var e = window.event || {};
			if( available && e.ctrlKey ){
				if( this.copyNode )this.copyNode.setStyle("display","none");
				module._showInjectAction( this );
			}else{
				this._dragComplete();
			}
		}else{
			this._dragCancel();
		}
	},
	_dragComplete: function( relativeNode, position, selectDisabled ){
		this.setStyleTemplate();

		if( this.injectNoticeNode )this.injectNoticeNode.destroy();
		if(this.moveNode){
			var overflow = this.moveNode.retrieve("overflow");
			if( overflow ){
				this.moveNode.setStyle("overflow",overflow);
				this.moveNode.eliminate("overflow");
			}
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
	_resetTreeNode: function(){


		if (this.parentContainer){
			if (this.treeNode){
				if (this.treeNode.parentNode){
					var originalModule = this.treeNode.parentNode.module;
					//	if (originalModule == this.parentContainer){
					//		if (!this.nextModule) return true;
					//	};
				}
				this.treeNode.destroy();
			}

			this._loadTreeNode(this.parentContainer);

			if (this.treeNode.parentNode){
				if (!this.treeNode.parentNode.options.expand) this.treeNode.parentNode.expandOrCollapse();
			}

			this._resetSubTreeNode(this.node)
		}
	},
	_resetSubTreeNode: function(node){
		var subNode = node.getFirst();
		while (subNode){
			var module = subNode.retrieve("module");
			if (module) module._resetTreeNode();

			this._resetSubTreeNode(subNode);

			subNode = subNode.getNext();
		}
	},

	_createNode: function(){
		this.node = this.moveNode.clone(true, true);
		this.node.clearStyles(false);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("id", this.json.id);
		this.node.addEvent("selectstart", function(){
			return false;
		});
	},
	_dragCancel: function(){
		if( this.dragTimeout ){
			window.clearTimeout( this.dragTimeout );
			this.dragTimeout = null;
		}
		if (this.node){
			var thisDisplay = this.node.retrieve("thisDisplay");
			if (thisDisplay){
				this.node.setStyle("display", thisDisplay);
			}
			this.selected();
		}else{
			this.data = null;
			if (this.form.recordCurrentSelectedModule) this.form.recordCurrentSelectedModule.selected();
		}
		this._hideInjectAction();
		if (this.moveNode) this.moveNode.destroy();
		if (this.copyNode) this.copyNode.destroy();
		this.copyNode = null;
		this.moveNode = null;
		this.form.moveModule = null;

		this.operation = null;
		this.fromLog = null;
	},
	_nodeDrag: function(e, drag){
		if( !this.dragTimeout ){
			this.dragTimeout = window.setTimeout(function(){

				var overflow = this.moveNode.getStyle("overflow");
				if( overflow && overflow !== "visible" ){
					this.moveNode.store("overflow",overflow);
					this.moveNode.setStyle("overflow","visible");
				}

				this.injectNoticeNode = new Element("div", {
					styles : this.form.css.injectNoticeNode,
					text : MWF.APPFD.LP.formAction.injectNotice
				}).inject( this.moveNode );

				//if (this.copyNode) this.copyNode.destroy();

			}.bind(this), 5000);
		}

		if (this.inContainer){
			var p = this.inContainer.node.getCoordinates();
			var now = drag.mouse.now;
			var height = p.height*0.4;
			if (p.height>200) height = 100;
			var y = p.top.toFloat()+height.toFloat();

			if (this.inContainer == this.parentContainer){
				if (this.parentContainer!=this.form){
					if (now.x > p.left && now.x < p.right && now.y < y && now.y > p.top){
						this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
						this.parentContainer.node.setStyles(this.parentContainer.json.styles);

						if(e.control ){
							this.inContainer._dragIn(this);
						}else{
							this.parentContainer._dragInLikeElement(this);
						}
					}
				}
			}else{
				if (now.x > p.left && now.x < p.right && now.y < p.bottom && now.y > y){
					this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
					this.parentContainer.node.setStyles(this.parentContainer.json.styles);
					this.inContainer._dragIn(this);
				}
			}
		}

	},
	_setControlMode: function(flag){
		if (this.controlMode!=flag){
			this.controlMode = flag;
			this._setControlModeNode();
		}
	},
	deletePropertiesOrStyles: function(name, key){
		if (name=="properties"){
			try{
				this.node.removeProperty(key);
			}catch(e){}
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="properties"){
			try{
				this.setCustomProperties();
			}catch(e){}
		}
	},
	setCustomProperties: function(){
		this.node.setProperties(this.json.properties);
	},
	setCustomNodeStyles: function(node, styles){
		var border = node.getStyle("border");
		node.clearStyles();
		//node.setStyles(styles);
		node.setStyle("border", border);

		Object.each(styles, function(value, key){
			var reg = /^border\w*/ig;
			if (!key.test(reg)){
				node.setStyle(key, value);
			}
		}.bind(this));
	},
	_preprocessingModuleData: function(){
		this.node.clearStyles();
		//if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.json.recoveryStyles = Object.clone(this.json.styles);

		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				this.node.setStyle(key, value);
				delete this.json.styles[key];
			}
		}.bind(this));
		this.json.preprocessing = "y";
	},
	_recoveryModuleData: function(){
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
	},
	setCustomStyles: function(){
		this._recoveryModuleData();
		//debugger;
		var border = this.node.getStyle("border");
		this.node.clearStyles();
		this.node.setStyles(this.css.moduleNode);

		if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.node.setStyle("border", border);

		this.parseStyles( this.node, this.json.styles );


		// Object.each(this.json.styles, function(value, key){
		// 	var reg = /^border\w*/ig;
		// 	if (!key.test(reg)){
		// 		if (key){
		// 			if (key.toString().toLowerCase()==="display"){
		// 				if (value.toString().toLowerCase()==="none"){
		//                    this.node.setStyle("opacity", 0.3);
		// 				}else{
		//                    this.node.setStyle("opacity", 1);
		// 				}
		// 			}else{
		//                this.node.setStyle(key, value);
		// 			}
		// 		}
		// 	}
		// }.bind(this));
	},
	parseStyles: function(node, styles){
		if (styles) Object.each(styles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
				var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
				if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
					value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
					value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
				}
				if (value.indexOf("/x_portal_assemble_surface")!==-1){
					value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}else if (value.indexOf("x_portal_assemble_surface")!==-1){
					value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
				}
				value = o2.filterUrl(value);
			}

			var reg = /^border\w*/ig;
			if (!key.test(reg)){
				if (key){
					if (key.toString().toLowerCase()==="display"){
						if (value.toString().toLowerCase()==="none"){
							node.setStyle("opacity", 0.3);
						}else{
							node.setStyle("opacity", 1);
							node.setStyle(key, value);
						}
					}else{
						node.setStyle(key, value);
					}
				}
			}
			//this.node.setStyle(key, value);
		}.bind(this));
	},

	_setEditStyle: function(name, obj, oldValue){
		var title = "";
		var text = "";
		debugger;
		if (name==="name"){
			title = this.json.name || this.json.id;
			if (this.json.type==="Common"){
				text = text = this.json.tagName+"(Common)";
			}else{
				text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
			}
			if (this.treeNode.setText) this.treeNode.setText("<"+text+"> "+title);
		}
		if (name==="id"){
			title = this.json.name || this.json.id;
			if (!this.json.name){
				if (this.json.type==="Common"){
					text = text = this.json.tagName+"(Common)";
				}else{
					text = this.json.type.substr(this.json.type.lastIndexOf("$")+1, this.json.type.length);
				}
				if (this.treeNode.setText) this.treeNode.setText("<"+text+"> "+this.json.id);
			}
			if (this.treeNode.setTitle) this.treeNode.setTitle(this.json.id);
			this.node.set("id", this.json.id);
		}

		this._setEditStyle_custom(name, obj, oldValue);
	},
	reloadMaplist: function(){
		if (this.property) Object.each(this.property.maplists, function(map, name){ map.reload(this.json[name]);}.bind(this));
	},
	_setEditStyle_custom: function(name, obj, oldValue){
	},

	getHtml: function(){
		var copy = this.node.clone(true, true);
		copy.clearStyles(true);

		this.form._clearNoId(copy);
		var html = copy.outerHTML;
		copy.destroy();

		return html;
	},
	_getSubModuleJson: function(node, moduleJsons){
		var subNode = node.getFirst();
		while (subNode){
			var module = subNode.retrieve("module");
			if (module) {
				moduleJsons[module.json.id] = Object.clone(module.json);
			}
			this._getSubModuleJson(subNode, moduleJsons);
			subNode = subNode.getNext();
		}
	},
	getJson: function(){
		var json = Object.clone(this.json);
		var o = {};
		o[json.id] = json;
		this._getSubModuleJson(this.node, o);
		return o;
	},

	getJsonData: function(name){
		var d = this.json;
		Array.each(name.split("."), function (n) {
			if (d) d = d[n];
		});
		return d;
	},
	checkPropertyHistory: function(name, oldValue, newValue, notSetEditStyle, compareName, force){
		if( !this.form.history )return null;
		var log = {
			"type": "property",
			"force": force,
			"moduleId": this.json.id,
			"moduleType": this.json.type,
			"notSetEditStyle": notSetEditStyle,
			"changeList": []
		};

		if( typeOf(name) === "array" ){
			name.each(function (n, i) {
				log.changeList.push({
					"name": n,
					"fromValue": oldValue[i],
					"toValue": newValue[i] || this.getJsonData(n)
				});
			}.bind(this));
		}else{
			log.changeList.push({
				"name": name,
				"compareName": compareName,
				"fromValue": oldValue,
				"toValue": newValue || this.getJsonData(name)
			});
		}
		this.form.history.checkProperty(log, this);
	},
	// createHistoryPropertyLogList: function( moduleList ){
	// 	if( !this.form.history )return null;
	// 	var logList = [];
	// 	if(moduleList){
	// 		var list = o2.typeOf(moduleList) === "array" ? moduleList : [moduleList];
	// 		list.each(function (module) {
	// 			logList.push( module.createHistoryPropertyLog() );
	// 		}.bind(this));
	// 	}
	// 	return logList;
	// },
	// createHistoryPropertyLog: function ( module ) {
	// 	if( !this.form.history )return null;
	// 	if( !module )module = this;
	// 	var obj = {
	// 		"path": module.form.history.getPath(module.node),
	// 		"from": module.originalJson,
	// 		"to": module.json
	// 	};
	// 	return obj;
	// },

	addHistoryLog: function(operation, toModuleList, fromList, moduleId, moduleType, html ){
		if( !this.form.history )return null;
		var log = {
			"operation": operation,
			"type": "module",
			"moduleType": moduleType || this.json.type,
			"moduleId": moduleId || this.json.id
		};
		if( toModuleList ){
			log.toList = this.createHistoryLogList( toModuleList );
		}else{
			var to = {
				"json": Object.clone(this.json),
				"path": this.form.history.getPath(this.node)
			};
			if( operation !== "move" ){
				to.jsonObject = this.getJson();
				to.html = html || this.node.outerHTML;
			}
			log.toList = [ to ];
		}

		if( fromList ){
			log.fromList = o2.typeOf(fromList) === "array" ? fromList : [fromList];
		}
		this.form.history.add( log, this);
	},
	createHistoryLogList: function( moduleList ){
		if( !this.form.history )return null;
		var logList = [];
		if(moduleList){
			var list = o2.typeOf(moduleList) === "array" ? moduleList : [moduleList];
			list.each(function (module) {
				logList.push( module.createHistoryLog() );
			}.bind(this));
		}
		return logList;
	},
	createHistoryLog: function ( module ) {
		if( !this.form.history )return null;
		if( !module )module = this;
		var obj = {
			"json": Object.clone(module.json),
			"path": module.form.history.getPath(module.node),
			"jsonObject": module.getJson(),
			"html": module.node.outerHTML
		};
		return obj;
	},

	//脚本附签上的脚本编辑器
	addScriptJsEditor: function (propertyName, jsEditor) {
		if( !this.scriptJsEditors )this.scriptJsEditors = {};
		this.scriptJsEditors[propertyName] = jsEditor;
	},
	getScriptJsEditor: function (propertyName) {
		if( !this.scriptJsEditors ){
			return null;
		}else{
			return this.scriptJsEditors[propertyName];
		}
	}


//	dragInElement: function(dragging, inObj, module){
//		this.containerNode = module.containerNode;
//		
//	//	var border = this.containerNode.retrieve("thisborder", null);
//	//	if (!border){
//			var top = this.containerNode.getStyle("border-top");
//			var left = this.containerNode.getStyle("border-left");
//			var bottom = this.containerNode.getStyle("border-bottom");
//			var right = this.containerNode.getStyle("border-right");
//			
//			this.containerNode.store("thisborder", {"top": top, "left": left, "bottom": bottom, "right": right});
//	//	}
//		this.containerNode.setStyles({"border": "1px solid #ffa200"});
//		
//		if (!this.copyNode) this.createCopyNode(dragging, inObj);
//		this.copyNode.inject(inObj, "before");
//	},
//	dragInContainer: function(dragging, inObj){
//	//	var border = inObj.retrieve("thisborder", null);
//	//	if (!border){
//			var top = inObj.getStyle("border-top");
//			var left = inObj.getStyle("border-left");
//			var bottom = inObj.getStyle("border-bottom");
//			var right = inObj.getStyle("border-right");
//			inObj.store("thisborder", {"top": top, "left": left, "bottom": bottom, "right": right});
//	//	} 
//		
//		inObj.setStyles({"border": "1px solid #ffa200"});
//		
//		if (!this.copyNode) this.createCopyNode(dragging, inObj);
//		
//		this.copyNode.inject(inObj);
//		
//		this.containerNode = inObj;
//	},
//	
//	
//	dragOutElement: function(dragging, inObj){
//		var border = this.containerNode.retrieve("thisborder");
//		if (border) {
//			this.containerNode.setStyles({
//				"border-top": border.top,
//				"border-left": border.left,
//				"border-bottom": border.bottom,
//				"border-right": border.right
//			});
//		}
//		this.containerNode = null;
//	},
//	dragOutContainer: function(dragging, inObj){
//		var border = inObj.retrieve("thisborder");
//		if (border) {
//			inObj.setStyles({
//				"border-top": border.top,
//				"border-left": border.left,
//				"border-bottom": border.bottom,
//				"border-right": border.right
//			});
//		//	inObj.setStyles({"border": border});
//		}
//		if (!this.node){
//			if (this.copyNode){
//				this.copyNode.destroy();
//				this.copyNode = null;
//			}
//		}
//		this.containerNode = null;
//	},
//	
//	
//	
//	
//	dragCancel: function(dragging){
//		if (this.node){
//			var thisDisplay = this.node.retrieve("thisDisplay");
//			if (thisDisplay){
//				this.node.setStyle("display", thisDisplay);
//			}
//			this.selected();
//		}else{
//			this.data = null;
//			if (this.form.recordCurrentSelectedModule) this.form.recordCurrentSelectedModule.selected();
//		}
//		if (dragging) dragging.destroy();
//		if (this.copyNode) this.copyNode.destroy();
//		this.copyNode = null;
//		this.moveNode = null;
//		this.form.moveModule = null;
//	}

});
