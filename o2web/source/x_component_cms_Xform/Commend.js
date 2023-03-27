
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);

MWF.xApplication.cms.Xform.Commend = MWF.CMSCommend =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	iconStyle: "personfieldIcon",
	_loadCommend  : function (){

		var icon ;
		if(!this.form.businessData.extend.isCommend){
			icon = "commend.png";
			this.button.set("title",this.lp.commend.do);
		}else {
			icon = "commend_do.png";
			this.button.set("title",this.lp.commend.undo);
		}

		this.button.setStyles({
			"border" : 0 ,
			"width" : "14px",
			"height" : "22px",
			"background" : "url(../x_component_cms_FormDesigner/Module/Commend/default/icon/"+ icon +") no-repeat",
			"background-size" : "cover",
			"background-position": "center 0px"

		});
		this.countNode.setStyles({
			"height" : "22px",
			"font-size" : "16px",
			"line-height" : "22px"
		});
		this.countNode.set("text",this.form.businessData.document.commendCount);
	},
	_loadUserInterface: function(){
		this.lp = MWF.xApplication.cms.Xform.LP;
		var div = new Element("div");
		div.set(this.json.properties);
		div.inject(this.node, "after");

		this.node.destroy();
		this.node = div;

		this.button = new Element("button",{"text":""}).inject(this.node);
		this.countNode = new Element("span").inject(this.node);
		this._loadCommend();


		this.button.addEvent("click",function (){
			if(!this.form.businessData.extend.isCommend){
				o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_commend(this.form.businessData.document.id, function( json ){
					this.form.businessData.extend.isCommend = true;
					this.form.businessData.document.commendCount = this.form.businessData.document.commendCount + 1;
					this._loadCommend();
				}.bind(this));
			}else {
				o2.Actions.load("x_cms_assemble_control").DocumentAction.persist_unCommend(this.form.businessData.document.id, function( json ){
					this.form.businessData.extend.isCommend = false;
					this.form.businessData.document.commendCount = this.form.businessData.document.commendCount - 1;
					this._loadCommend();
				}.bind(this));
			}

		}.bind(this));
	}

});
