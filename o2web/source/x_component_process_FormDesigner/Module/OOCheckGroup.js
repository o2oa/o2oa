MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OORadioGroup", null, false);
MWF.xApplication.process.FormDesigner.Module.OOCheckGroup = MWF.FCOOCheckGroup = new Class({
	Extends: MWF.FCOORadioGroup,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOCheckGroup",
		"path": "../x_component_process_FormDesigner/Module/OOCheckGroup/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOCheckGroup/OOCheckGroup.html",
		"tag": "oo-checkbox-group"
	}
});
