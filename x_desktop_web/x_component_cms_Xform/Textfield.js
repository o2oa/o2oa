MWF.xDesktop.requireApp("cms.Xform", "$Input", null, false);
MWF.xApplication.cms.Xform.Textfield = MWF.CMSTextfield =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Input,
	iconStyle: "textFieldIcon",
	
	_loadUserInterface: function(){

		this._loadNode();
        if (this.json.compute == "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
        }
	},
    _loadNode: function(){
        if (this.readonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
    },
    _loadNodeEdit: function(){
		var input = new Element("input");
		input.set(this.json.properties);
		input.inject(this.node, "after");
		this.node.destroy();
		this.node = input;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type
		});
		this.node.setStyle("margin-right", "18px");

        var styles = this.form.css[this.styles];
        if( styles )this.node.setStyles( styles );
        var styles_select = this.form.css[this.styles+ "_select"];
        if( styles && styles_select ){
            this.node.addEvents( { "focus" : function(){
                this.node.setStyles( this.form.css[this.styles+ "_select"] )
            }.bind(this),
                "blur" : function(){
                    this.node.setStyles( this.form.css[this.styles] )
                }.bind(this)
            });
        }

        this.node.addEvent("change", function(){
            //if (this.json.validation){
            //    if (this.json.validation.code) {
            //        if (this.validation()) this._setBusinessData(this.getInputData("change"));
            //    }else{
            //        this._setBusinessData(this.getInputData("change"));
            //    }
            //}else{
                this._setBusinessData(this.getInputData("change"));
            //}
        }.bind(this));
        this.node.addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
	}
}); 