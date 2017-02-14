MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Monitor = MWF.CMSMonitor =  new Class({
	Extends: MWF.CMS$Module,

	_loadUserInterface: function(){
		this.node.empty();

        MWF.xDesktop.requireApp("cms.Xform", "widget.Monitor", function(){
            //var cms = (this.form.businessData.work) ? this.form.businessData.work.cms : this.form.businessData.workCompleted.cms;
            //this.monitor = new MWF.xApplication.cms.Xform.widget.Monitor(this.node, this.form.businessData.workLogList, cms);
        }.bind(this), false);
}
});