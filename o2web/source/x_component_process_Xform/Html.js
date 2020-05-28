MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Html = MWF.APPHtml =  new Class({
    Extends: MWF.APP$Module,
    load: function(){
        this.node.insertAdjacentHTML("beforebegin", this.json.text);
        this.node.destroy();
    }
});
