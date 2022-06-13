MWF.xDesktop.requireApp("process.Xform", "ReadLog", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "widget.View", null, false);
MWF.xApplication.cms.Xform.ProcessReadLog = MWF.CMSProcessReadLog =  new Class({
	Extends: MWF.APPReadLog,
	load: function(){
		this.node.empty();

		if( !this.form.businessData.data.$work || !this.form.businessData.data.$work.job ){
			return
		}

		if (!this.json.isDelay){
			this.active();
		}
	},
	_loadUserInterface: function(){
		this.node.setStyle("-webkit-user-select", "text");
		this.node.setStyles(this.form.css.logActivityNode_record);

		o2.Actions.load("x_processplatform_assemble_surface").ReadRecordAction.listWithJob(this.form.businessData.data.$work.job, function(json){
			this.readLog = json.data;
			this.fireEvent("postLoadData");
			this.loadReadLog();
		}.bind(this));
	}
});