MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.ReadLog", null, false);
MWF.xApplication.cms.FormDesigner.Module.ProcessReadLog = MWF.CMSFCProcessReadLog = new Class({
	Extends: MWF.FCReadLog,
	Implements : [MWF.CMSFCMI],
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/ReadLog/";
		this.cssPath = "../x_component_process_FormDesigner/Module/ReadLog/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "processReadLog";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});