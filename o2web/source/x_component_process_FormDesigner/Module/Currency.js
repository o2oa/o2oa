MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textfield", null, false);
MWF.xApplication.process.FormDesigner.Module.Currency = MWF.FCCurrency = new Class({
	Extends: MWF.FCTextfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "currency",
		"path": "../x_component_process_FormDesigner/Module/Currency/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Currency/currency.html"
	},
	clearTemplateStyles: function(styles){
		if (styles){
			if (styles.styles) this.removeStyles(styles.styles, "styles");
			if (styles.inputStyles) this.removeStyles(styles.inputStyles, "inputStyles");
			if (styles.symbolStyles) this.removeStyles(styles.symbolStyles, "symbolStyles");
			if (styles.properties) this.removeStyles(styles.properties, "properties");
		}
	},
	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.inputStyles) this.copyStyles(styles.inputStyles, "inputStyles");
		if (styles.symbolStyles) this.copyStyles(styles.symbolStyles, "symbolStyles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
	},
});
