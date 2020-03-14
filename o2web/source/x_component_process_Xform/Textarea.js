MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.xApplication.process.Xform.Textarea = MWF.APPTextarea =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Input,
	
	_loadUserInterface: function(){

		this._loadNode();
        if (this.json.compute == "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
        }
	},
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
    },
    _setValue: function(value){
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
        if (this.readonly || this.json.isReadonly){
            var reg = new RegExp("\n","g");
            var text = value.replace(reg,"<br/>");
            this.node.set("html", text);
        }
    },
    _loadNodeEdit: function(){
		var input = new Element("textarea", {"styles": {
            "background": "transparent",
            "width": "100%",
            "border": "0px"
        }});
		input.set(this.json.properties);
        if( this.form.json.textareaDisableResize )input.setStyle("resize","none");

        var node = new Element("div", {"styles": {
            "ovwrflow": "hidden",
            "position": "relative",
            "padding-right": "2px"
        }}).inject(this.node, "after");
        input.inject(node);
        this.node.destroy();
        this.node = node;
		//this.node = input;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type
		});
        this.node.addEvent("change", function(){
            this._setBusinessData(this.getInputData());
        }.bind(this));

        this.node.getFirst().addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.getFirst().addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
	},
	_afterLoaded: function(){
        if (!this.readonly){
            this.loadDescription();
        }
	},
    loadDescription: function(){
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size = this.node.getFirst().getSize();
                var w = size.x-23;
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "width": ""+w+"px",
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            if (COMMON.Browser.Platform.name==="ios"){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else if (COMMON.Browser.Platform.name==="android"){
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }else{
                this.descriptionNode.addEvents({
                    "click": function(){
                        this.descriptionNode.setStyle("display", "none");
                        this.node.getFirst().focus();
                    }.bind(this)
                });
            }
            this.node.getFirst().addEvents({
                "focus": function(){
                    this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    }
	
}); 