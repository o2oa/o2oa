MWF.xDesktop.requireApp("process.FormDesigner", "Module.Address", null, false);
MWF.xApplication.portal.PageDesigner.Module.Address = MWF.PCAddress = new Class({
	Extends: MWF.FCAddress,
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Address/address.html"
	}
});
