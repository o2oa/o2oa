MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Checkbox", null, false);
MWF.xApplication.process.FormDesigner.Module.Radio = MWF.FCRadio = new Class({
	Extends: MWF.FCCheckbox,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "radio",
		"path": "../x_component_process_FormDesigner/Module/Radio/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Radio/radio.html"
	}
});
