MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.Calendar = MWF.FCCalendar = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "calendar",
		"path": "../x_component_process_FormDesigner/Module/Calendar/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Calendar/calendar.html"
	}
});
