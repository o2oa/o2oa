MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.Textfield = MWF.FCTextfield = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "textfield",
		"path": "../x_component_process_FormDesigner/Module/Textfield/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Textfield/textfield.html"
	}
});
