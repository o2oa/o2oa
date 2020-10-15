MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Statement", null, false);
MWF.xApplication.cms.FormDesigner.Module.Statement = MWF.CMSFCStatement = new Class({
	Extends: MWF.FCStatement,
	Implements : [MWF.CMSFCMI]//,
	//_createViewNode: function(callback){
	//	if (!this.viewNode) this.viewNode = new Element("div", {"styles": this.css.viewNode}).inject(this.node);
	//	this.node.setStyle("background", "transparent");
    //
	//	this.viewTable = new Element("table", {
	//		"styles": this.css.viewTitleTableNode,
	//		"border": "0px",
	//		"cellPadding": "0",
	//		"cellSpacing": "0"
	//	}).inject(this.viewNode);
	//	this.viewLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.viewTable);
    //
	//	if (this.json.select!="no"){
	//		this.viewSelectCell = new Element("td", {
	//			"styles": this.css.viewTitleCellNode
	//		}).inject(this.viewLine);
	//		this.viewSelectCell.setStyle("width", "10px");
	//	}
	//	this.form.designer.actions.getQueryView(this.json["view"], function(json){
	//		var viewData = JSON.decode(json.data.data);
	//		viewData.selectEntryList.each(function(column){
	//			//    if (column.export){
	//			var viewCell = new Element("td", {
	//				"styles": this.css.viewTitleCellNode,
	//				"text": column.displayName
	//			}).inject(this.viewLine);
	//			//    }
	//		}.bind(this));
    //
	//		if (callback) callback();
	//	}.bind(this));
	//	this._setViewNodeTitle();
	//}
});
