MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Log", null, false);
MWF.xApplication.cms.FormDesigner.Module.Log = MWF.CMSFCLog = new Class({
	Extends: MWF.FCLog,
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Log/log.html"
	}
});
