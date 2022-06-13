MWF.xDesktop.requireApp("process.Xform", "Monitor", null, false);
//MWF.xDesktop.requireApp("cms.Xform", "widget.View", null, false);
MWF.xApplication.cms.Xform.ProcessMonitor = MWF.CMSProcessMonitor =  new Class({
	Extends: MWF.APPMonitor,
	_loadUserInterface: function(){
		this.node.empty();

		if( !this.form.businessData.data.$work || !this.form.businessData.data.$work.process ){
			return
		}

		this.form.listWorkLog( function ( workLogList ) {
			MWF.xDesktop.requireApp("process.Xform", "widget.Monitor", function(){
				//    debugger;
				var process = this.form.businessData.data.$work.process;
				/**
				 * @summary 流程图对象，是一个 MWF.xApplication.process.Xform.widget.Monitor 类实例
				 */
				this.monitor = new MWF.xApplication.process.Xform.widget.Monitor(this.node, workLogList, process,{
					"onPostLoad" : function(){
						this.fireEvent("postLoad");
					}.bind(this)
				});
			}.bind(this), false);
		}.bind(this))

	}
});