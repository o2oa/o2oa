MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Monitor", null, false);
MWF.xApplication.cms.FormDesigner.Module.ProcessMonitor = MWF.CMSFCProcessMonitor = new Class({
	Extends: MWF.FCMonitor,
	Implements : [MWF.CMSFCMI],
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_process_FormDesigner/Module/Monitor/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Monitor/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "processMonitor";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "processMonitor",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	}
});