MWF.xDesktop.requireApp("process.Xform", "View", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.View", null, false);
MWF.xApplication.cms.Xform.View = MWF.CMSView =  new Class({
	Extends: MWF.APPView,

    _loadUserInterface: function(){
        this.node.empty();
        //MWF.xDesktop.requireApp("process.Xform", "widget.View", function(){
            this.json.application = this.form.businessData.document.appName ;//this.form.json.applicationName;
            this.view = new MWF.xApplication.cms.Xform.widget.View(this.node, this.json, {
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        //}.bind(this), false);
    }
});