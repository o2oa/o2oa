MWF.xApplication.portal.PageDesigner.Module.Calendar = MWF.PCCalendar = new Class({
	Extends: MWF.FCCalendar,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Calendar/calendar.html"
	},
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Calendar/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Calendar/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "calendar";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
