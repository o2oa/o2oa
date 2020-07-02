MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Address = MWF.FCAddress = new Class({
	Extends: MWF.FCCombox,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "address",
		"path": "../x_component_process_FormDesigner/Module/Address/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Address/address.html"
	}
});
