MWF.xDesktop.requireApp("process.Xform", "SmartBI", null, false);
MWF.xApplication.cms.Xform.SmartBI = MWF.CMSSmartBI =  new Class({
    Extends: MWF.APPSmartBI,

    _loadUserInterface: function(){
        
        if (!this.json.smartbiresource || this.json.smartbiresource==="none") this.node.destroy();
        else{
            var _iframe = this.node.getElement("iframe");
            var url = _iframe.get("src");
            
            this.iframe = new Element("iframe",{
                src:url,
                frameborder:"0",
                scrolling:"auto"
            }).inject(this.node,"after");

            var _recoveryStyles = this.json.recoveryStyles || {};
            this.node.destroy();
            this.node = this.iframe.setStyles({
                "width":"100%",
                "height":"100%",
                "min-height":"500px",
                "min-width":"500px"
            });
            this.node.setStyles(_recoveryStyles)
        }

	}
});