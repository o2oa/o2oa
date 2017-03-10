MWF.xDesktop.requireApp("process.Xform", "Personfield", null, false);
MWF.xApplication.cms.Xform.Personfield = MWF.CMSPersonfield =  new Class({
	Extends: MWF.APPPersonfield,
	getDepartments: function(){
		if (this.json.range=="depart"){
			return this.getRange();
		}
		if (this.json.range=="currentdepart"){
			return [this.form.businessData.document.creatorDepartment];
		}
		return [];
	},
	getCompanys: function(){
		if (this.json.range=="company"){
			var comp = "";
			//if (this.json.rangeKey){
			return this.getRange();
			//}
		}
		if (this.json.range=="currentcompany"){
			return [this.form.businessData.document.creatorCompany];
		}
		return [];
	}
}); 