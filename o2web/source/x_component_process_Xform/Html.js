MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Html = MWF.APPHtml =  new Class({
	Extends: MWF.APP$Module,

	load: function(){
	    debugger;
        this.source = this._getSource();
        if (this.source){
            this._loadJsonData();
        }else{
            debugger;
            this.node.appendHTML(this.json.text, "after");
            this.node.destory();
        }
	},
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (parent.get("MWFtype")!="source" && parent.get("MWFtype")!="subSource" && parent.get("MWFtype")!="subSourceItem")) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    _loadJsonData: function(){
        this.node.set("html", "");
        this.source = this._getSource();
        if (this.source){
            if (this.source.data){
                this.template = new Template();
                this.html = this.template.substitute("{"+this.json.text+"}", this.source.data);

                this.node.set("html", this.html);
            //    if (this.json.jsonText){
            //        if (this.json.jsonText.code){
            //            this.text = this.form.Macro.exec(this.json.jsonText.code, this);
            //            this.node.set("text", this.text);
            //        }else{
            //            this.node.set("text", this.text);
            //        }
            //    }else{
            //        this.node.set("text", this.text);
            //    }
            }
        }
    }
});