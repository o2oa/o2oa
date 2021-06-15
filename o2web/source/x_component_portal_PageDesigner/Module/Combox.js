MWF.xDesktop.requireApp("process.FormDesigner", "Module.Combox", null, false);
MWF.xApplication.portal.PageDesigner.Module.Combox = MWF.PCCombox = new Class({
	Extends: MWF.FCCombox,
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Combox/combox.html"
	}
});
