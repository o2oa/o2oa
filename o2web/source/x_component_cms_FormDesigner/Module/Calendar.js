MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Calendar", null, false);
MWF.xApplication.cms.FormDesigner.Module.Calendar = MWF.CMSFCCalendar = new Class({
	Extends: MWF.FCCalendar,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Calendar/calendar.html"
	}
});
