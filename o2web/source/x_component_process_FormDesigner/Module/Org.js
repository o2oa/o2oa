MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Personfield", null, false);
MWF.xApplication.process.FormDesigner.Module.Org = MWF.FCOrg = new Class({
	Extends: MWF.FCPersonfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "org",
		"path": "../x_component_process_FormDesigner/Module/Org/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Org/org.html"
	}
});
