

MWF.xApplication.Collect = MWF.xApplication.Collect || {};
MWF.xApplication.Collect.options = MWF.xApplication.Collect.options || {};
MWF.xDesktop.requireApp("Collect", "MainInContainer", null, false);

MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.EmptyDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function(){
        // this.node = new Element("div", {"styles": {"overflow": "hidden", "padding-bottom": "80px"}}).inject(this.contentAreaNode);
        debugger;
        this.node = new MWF.xApplication.Collect.MainInContainer(this.app.desktop, {}, this.contentAreaNode, this.contentAreaNode);
        this.node.load();
    }
});