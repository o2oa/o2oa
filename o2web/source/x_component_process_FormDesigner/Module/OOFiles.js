MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOInput", null, false);
MWF.xApplication.process.FormDesigner.Module.OOFiles = MWF.FCOOFiles = new Class({
	Extends: MWF.FCOOInput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOFiles",
		"path": "../x_component_process_FormDesigner/Module/OOFiles/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOFiles/OOFiles.html"
	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-files", {
			"MWFType": "OOFiles",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"placeholder": this.json.id,
			"readonly": "true",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);

		this.moveNode.setAttribute('readonly', 'true');
	},
	_setEditStyle_custom: function(name){
		if (name==="id") this.node.set("placeholder", this.json.id);
		if (["label"].includes(name)){
			this.node.setAttribute(name, this.json[name]);
		}

		if (name==="showMode"){
			if (this.json.showMode==="disabled"){
				this.node.setAttribute("bgcolor", "#f3f3f3");
				this.node.setAttribute("readmode", false);
			}else if (this.json.showMode==="read"){
				// this.node.setAttribute("readmode", true);
				this.node.removeAttribute("bgcolor");
			}else{
				this.node.setAttribute("readmode", false);
				this.node.removeAttribute("bgcolor");
			}
		}
		if (name==="dataType"){
			this.node.setAttribute("type", this.json.dataType);
			this.node._elements.input.value = '';
		}

		if (name==="showIcon"){
			if (this.json.showIcon==="yes"){
				this.node.setAttribute("right-icon", this.json.properties["right-icon"] || "edit");
			}else{
				this.node.removeAttribute("right-icon");
			}
		}
		if (name==="required"){
			if (this.json.required){
				this.node.setAttribute("required", true);
			}else{
				this.node.removeAttribute("required");
			}
		}
		if (name==="innerHTML"){
			this.node.set("html", this.json.innerHTML);
		}

		if (this.form.options.mode == "Mobile"){
			if (!this.node.getParent('table.form-datatable')){
				this.node.setAttribute("skin-mode", 'mobile');
			}
		}


		if (name==="selectFileTitle"){
			this.node.setAttribute("select-file", this.json.selectFileTitle || "");
		}
		if (name==="uploadButtonStyle"){
			this.node.setAttribute("file-button-style", this.json.uploadButtonStyle || "simple");
		}
		if (name==="uploadButtonIcon"){
			this.node.setAttribute("file-button-icon", this.json.uploadButtonIcon || "create");
		}
	},
});
