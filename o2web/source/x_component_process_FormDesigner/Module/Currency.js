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
	}
});
