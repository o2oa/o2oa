MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textarea", null, false);
MWF.xApplication.process.FormDesigner.Module.Opinion = MWF.FCOpinion = new Class({
	Extends: MWF.FCTextarea,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "opinion",
		"path": "../x_component_process_FormDesigner/Module/Opinion/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Opinion/opinion.html"
	}
});
