MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Eldropdown = MWF.FCEldropdown = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Eldropdown/eldropdown.html"
	},

	_initModuleType: function(){
		this.className = "Eldropdown";
		this.moduleType = "element";
		this.moduleName = "eldropdown";
	},
	_createElementHtml: function(){

		var html = "<el-dropdown";
		html += " :type=\"buttonType\"";
		html += " :size=\"size\"";
		html += " :split-button=\"splitButton\"";
		html += " :placement=\"placement\"";
		html += " :disabled=\"disabled\"";
		html += " readonly";
		html += " :trigger=\"trigger\"";
		html += " :hide-on-click=\"hideOnClick\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"elStyles\">";

		// <span class="el-dropdown-link">
		// 	下拉菜单<i class="el-icon-arrow-down el-icon--right"></i>
		// </span>

		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-dropdown>";
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
