MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.Office = MWF.APPOffice =  new Class({
	Extends: MWF.APP$Module,
	isActive: false,

	_loadUserInterface: function(){
		this.node.empty();
		this.node.setStyles({
			"min-height": "100px"
		});
        alert(Browser.name);
		if (Browser.name=="ie") this.isActive = true;
	},
	
	_afterLoaded: function(){
		if (!this.isActive){
			this.loadOfficeNotActive();
		}else{
			this.loadOffice();
		}
	},
	loadOffice: function(){
		this.node.set("text", "office");
	},
	loadOfficeNotActive: function(){
		this.node.setStyles({
			"background-color": "#EEE"
		});
		var icon = new Element("div", {
			"styles": {
				"width": "24px",
				"height": "24px",
				"float": "left",
				"background": "url("+this.form.path+""+this.form.options.style+"/icon/warning.png"+") no-repeat center center"
			}
		}).inject(this.node);
		var div = new Element("div", {
			"text": MWF.xApplication.process.Xform.LP.browserNotActiveX,
			"styles": {
				"height": "24px",
				"line-height": "24px",
				"float": "left",
				"font-size": "12px"
			}
		}).inject(this.node);
	}
}); 