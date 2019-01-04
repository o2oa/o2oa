MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Button = MWF.APPButton =  new Class({
    Implements: [Events],
    Extends: MWF.APP$Module,
    iconStyle: "personfieldIcon",

    _loadUserInterface: function(){
        var button = new Element("button");
        button.inject(this.node, "after");
        this.node.destroy();
        this.node = button;
        this.node.set({
            "id": this.json.id,
            "text": this.json.name || this.json.id,
            "styles": this.form.css.buttonStyles,
            "MWFType": this.json.type
        });
    }

}); 