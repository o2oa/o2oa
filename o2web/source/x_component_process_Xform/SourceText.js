MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.SourceText = MWF.APPSourceText =  new Class({
    Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        this._loadJsonData();
	},
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (parent.get("MWFtype")!="source" && parent.get("MWFtype")!="subSource" && parent.get("MWFtype")!="subSourceItem")) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    _loadJsonData: function(){
        this.node.set("text", "");
        this.source = this._getSource();
        if (this.source){
            if (this.source.data){
                COMMON.AjaxModule.load("JSONTemplate", function(){

                    this.template = new Template();
                    this.text = this.template.substitute("{"+this.json.jsonPath+"}", this.source.data);

                    if (this.json.jsonText){
                        if (this.json.jsonText.code){
                            this.text = this.form.Macro.exec(this.json.jsonText.code, this);
                            if( typeOf(this.text) === "string" )this.node.set("text", this.text);
                        }else{
                            this.node.set("text", this.text);
                        }
                    }else{
                        this.node.set("text", this.text);
                    }

                }.bind(this));
            }
        }
    }
}); 