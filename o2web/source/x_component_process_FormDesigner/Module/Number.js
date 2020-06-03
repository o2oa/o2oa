MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textfield", null, false);
MWF.xApplication.process.FormDesigner.Module.Number = MWF.FCNumber = new Class({
	Extends: MWF.FCTextfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "number",
		"path": "../x_component_process_FormDesigner/Module/Number/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Number/number.html"
	}
});
