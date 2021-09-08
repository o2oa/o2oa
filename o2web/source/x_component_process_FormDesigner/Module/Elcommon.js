MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcommon = MWF.FCElcommon = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcommon/elcommon.html"
	},

	_initModuleType: function(){
		this.className = "Elcommon";
		this.moduleType = "element";
		this.moduleName = "elcommon";
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": this.moduleName,
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	// _createVueAppNode: function(){
	// 	this.node = new Element("div.o2_vue", {
	// 		"MWFType": this.moduleName,
	// 		"id": this.json.id,
	// 		"styles": this.css.moduleNode,
	// 		"events": {
	// 			"selectstart": function(){
	// 				return false;
	// 			}
	// 		}
	// 	});
	// },
	// _afterMounted: function(el, callback){
	// 	this.node = el;
	// 	this.node.store("module", this);
	// 	this._loadVueCss();
	// 	if (callback) callback();
	// 	this._createIconNode();
	// },
	//
	// _createIconNode: function(){
	// 	this.iconNode = new Element("div", {
	// 		"styles": this.css.iconNode
	// 	}).inject(this.node, "top");
	// 	new Element("div", {
	// 		"styles": this.css.iconNodeIcon
	// 	}).inject(this.iconNode);
	// 	new Element("div", {
	// 		"styles": this.css.iconNodeText,
	// 		"text": "refresh"
	// 	}).inject(this.iconNode);
	//
	// 	this.iconNode.addEvent("click", function(){
	// 		this.resetElement();
	// 	}.bind(this));
	// },
	// getIconPosition: function(){
	// 	var p = this.node.getPosition(this.node.getOffsetParent());
	// 	var size = this.node.getSize();
	// 	var iconSize = this.iconNode.getSize();
	//
	// 	return {"x": p.x+size.x-iconSize.x-1, "y": p.y+1};
	// },
	// reloadElcommon: function(){
	// 	this.resetElement();
	// 	this._createIconNode();
	// 	var p = this.getIconPosition();
	// 	this.iconNode.setStyles({
	// 		"float": "right",
	// 		"margin-top": "0px",
	// 		"position": "absolute",
	// 		"top": p.y,
	// 		"left": p.x-18
	// 	});
	// },
	// _initModule: function(){
	// 	if (!this.json.isSaved) this.setStyleTemplate();
	// 	//this._resetVueModuleDomNode(function(){
	// 		this._setNodeProperty();
	// 		if (!this.form.isSubform) this._createIconAction();
	// 		this._setNodeEvent();
	// 		//this.selected(true);
	// 	//}.bind(this));
	// 	this.json.isSaved = true;
	// },

	_createElementHtml: function(){
		//if (this.styleNode) this.styleNode.destroy();
		var html = this.json.vueTemplate || "";
		return html;
	},
	_createCopyNode: function(){
		this.copyNode = new Element("div", {
			"styles": this.css.moduleNodeShow
		});
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	setPropertyName: function(){},
	setPropertyId: function(){}
	// _loadVueCss: function(){
	// 	if (this.json.vueCss && this.json.vueCss.code){
	// 		this.styleNode = this.node.loadCssText(this.json.vueCss.code, {"notInject": true});
	// 		this.styleNode.inject(this.node, "top");
	// 	}
	// },
	// _afterMounted: function(el, callback){
	// 	this.node = el;
	// 	this.node.store("module", this);
	// 	this._loadVueCss();
	// 	if (callback) callback();
	// }
});
