MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.OOInput = MWF.FCOOInput = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOInput",
		"path": "../x_component_process_FormDesigner/Module/OOInput/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOInput/OOInput.html"
	},
	_loadNodeStyles: function(){
		this.node.setAttribute('readonly', true);
		if (this.json.innerHTML){
			this.node.set("html", this.json.innerHTML);
		}
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
	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-input", {
			"MWFType": "OOInput",
			"id": this.json.id,
			// "label-style": "width:6.2vw; min-width:5em; max-width:9em",
			"styles": this.css.moduleNodeMove,
			"placeholder": this.json.id,
			"readonly": "true",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_resetModuleDomNode: function(){
        if (this.json.preprocessing){
            this.node.empty();
			if (this.json.innerHTML) this.node.set("html", this.json.innerHTML);
        }
    },

	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="inputStyles"){
			try{
				this.setCustomInputStyles();
			}catch(e){}
		}
		if (name=="properties"){
			this.node.setProperties(this.json.properties);
		}
	},

	_preprocessingModuleData: function(){
		this.node.clearStyles();
		if (this.json.styles){
			this.json.recoveryStyles = Object.clone(this.json.styles);
			this.ooinput = this.node;
			if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
				if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
					this.ooinput.setStyle(key, value);
					delete this.json.styles[key];
				}
			}.bind(this));
		}
		this.json.preprocessing = "y";
	},
	setCustomInputStyles: function(){
        this._recoveryModuleData();

        // var inputNode = this.node.node;
        // if (inputNode){
        //     // inputNode.clearStyles();
        //     // inputNode.setStyles(this.css.moduleText);

        //     if (this.json.inputStyles) Object.each(this.json.inputStyles, function(value, key){
        //         if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
        //             var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
        //             var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
        //             if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
        //                 value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
        //             }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
        //                 value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
        //             }
        //             if (value.indexOf("/x_portal_assemble_surface")!==-1){
        //                 value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
        //             }else if (value.indexOf("x_portal_assemble_surface")!==-1){
        //                 value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
        //             }
        //             value = o2.filterUrl(value);
        //         }

        //         var reg = /^border\w*/ig;
        //         if (!key.test(reg)){
        //             if (key){
        //                 if (key.toString().toLowerCase()==="display"){
        //                     if (value.toString().toLowerCase()==="none"){
        //                         inputNode.setStyle("opacity", 0.3);
        //                     }else{
        //                         inputNode.setStyle("opacity", 1);
        //                         inputNode.setStyle(key, value);
        //                     }
        //                 }else{
        //                     inputNode.setStyle(key, value);
        //                 }
        //             }
        //         }
        //     }.bind(this));
        // }
    }
});
