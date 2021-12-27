MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcarousel = MWF.FCElcarousel = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcarousel/elcarousel.html"
	},

	_initModuleType: function(){
		this.className = "Elcarousel";
		this.moduleType = "element";
		this.moduleName = "elcarousel";
	},
	_createElementHtml: function(){

		var html = "<el-carousel";
		html += " :size=\"size\"";
		html += " :placement=\"placement\"";
		// html += " :disabled=\"disabled\"";
		html += " :trigger=\"trigger\"";
		html += " :hide-on-click=\"hideOnClick\"";

		if( this.json.showButton && this.json.splitButton ){
			html += " :split-button=\"splitButton\"";
			html += " :type=\"buttonType\"";
		}

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"elStyles\">";

		if (this.json.vueSlot){
			html += this.json.vueSlot;
		}else{
			html += this.getButtonHtml();
		}
		html += "</el-carousel>";

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
	getButtonHtml: function(){
		if( this.json.showButton ){
			if( this.json.splitButton ) {
				return this.json.text || this.json.id;
			}else{
				return "<el-button type=\""+this.json.buttonType+"\" size=\""+this.json.size+"\">"+ ( this.json.text || this.json.id ) +
					"<i class=\"el-icon-arrow-down el-icon--right\"></i>"+
					"</el-button>";
			}
		}else{
			return " <span class=\"el-dropdown-link\">"+ ( this.json.text || this.json.id ) +
				"<i class=\"el-icon-arrow-down el-icon--right\"></i>"+
				"</span>";
		}
	},
	_setEditStyle_custom: function(name){
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id":
			case "text":
			case "size":
			case "buttonType":
			case "vueSlot":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			case "showButton":
			case "splitButton":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			default: break;
		}
	},
	setPropertyName: function(){
		// if (this.json.name){
		// 	var input = this.node.getElement("input");
		// 	if (input) input.set("value", this.json.name);
		// }
	},
	setPropertyId: function(){
		// if (!this.json.name){
		// 	var input = this.node.getElement("input");
		// 	if (input) input.set("value", this.json.id);
		// }
	}
});
