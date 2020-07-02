MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Personfield", null, false);
MWF.xApplication.process.FormDesigner.Module.Orgfield = MWF.FCOrgfield = new Class({
	Extends: MWF.FCPersonfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "orgfield",
		"path": "../x_component_process_FormDesigner/Module/Orgfield/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Orgfield/orgfield.html"
	}
});
