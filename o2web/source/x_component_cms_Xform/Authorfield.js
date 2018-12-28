MWF.xDesktop.requireApp("cms.Xform", "Readerfield", null, false);
MWF.xApplication.cms.Xform.Authorfield = MWF.CMSAuthorfield =  new Class({
	Extends: MWF.CMSReaderfield,
	iconStyle: "authorfieldIcon",
	_loadNodeEdit : function(){
		var input = this.input = new Element("div", {
			"styles": {
				"background": "transparent",
				"border": "0px",
                "min-height": "20px"
			}
		});
		input.set(this.json.properties);

		var node = new Element("div", {"styles": {
			"overflow": "hidden",
			"position": "relative",
			"min-height" : "20px",
			"margin-right": "20px"
		}}).inject(this.node, "after");
		input.inject(node);

		this.node.destroy();
		this.node = node;
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"title" : MWF.xApplication.cms.Xform.LP.readerFieldNotice
		});
		if( !this.readonly ) {
			this.node.setStyle("cursor" , "pointer");
			this.node.addEvents({
				"click": this.clickSelect.bind(this)
			});
			if (this.json.showIcon!='no')this.iconNode = new Element("div", {  //this.form.css[this.iconStyle],
				"styles": {
					"background": "url("+"/x_component_cms_Xform/$Form/default/icon/selectauthor.png) center center no-repeat",
					"width": "18px",
					"height": "18px",
					"float": "right"
				}
			}).inject(this.node, "before");
			if (this.iconNode){
                this.iconNode.setStyle("cursor" , "pointer");
                this.iconNode.addEvents({
                    "click": this.clickSelect.bind(this)
                });
			}
		}
	}
}); 