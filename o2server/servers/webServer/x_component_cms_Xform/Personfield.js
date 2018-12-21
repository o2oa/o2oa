MWF.xDesktop.requireApp("process.Xform", "Personfield", null, false);
MWF.xApplication.cms.Xform.Personfield = MWF.CMSPersonfield =  new Class({
	Extends: MWF.APPPersonfield,
	//getDepartments: function(){
	//	if (this.json.range=="depart"){
	//		return this.getRange();
	//	}
	//	if (this.json.range=="currentdepart"){
	//		return [this.form.businessData.document.creatorUnitName];
	//	}
	//	return [];
	//},
	//getCompanys: function(){
	//	if (this.json.range=="company"){
	//		var comp = "";
	//		//if (this.json.rangeKey){
	//		return this.getRange();
	//		//}
	//	}
	//	if (this.json.range=="currentcompany"){
	//		return [this.form.businessData.document.creatorTopUnitName];
	//	}
	//	return [];
	//}
	getSelectRange: function(){
		if (this.json.range==="unit"){
			return this.getScriptSelectUnit();
		}
		if (this.json.range==="draftUnit"){
			return this.getNextSelectUnit(this.form.businessData.document.creatorIdentity);
		}
		if (this.json.range==="currentUnit"){
			//目前无效 return this.getNextSelectUnit(this.form.app.currentTask.identity);
		}
		return [];
	},
	clickSelect: function(){
		//var names = (nameValue) ? this.getInputData().split(MWF.splitStr) : [];
		var values = this.getInputData();
		var count = (this.json.count) ? this.json.count : 0;

		switch (this.json.range){
			case "":
		}
		var selectUnits = this.getSelectRange();
		if (this.json.selectType=="identity"){
			var selectDutys = this.getSelectRangeDuty();
		}

		if (this.json.range!=="all"){
			if (!selectUnits.length){
				this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
				return false;
			}
		}
		//if (this.json.dutyRange!=="all"){
		//	if (!selectDutys.length){
		//		this.form.notice(MWF.xApplication.process.Xform.LP.noSelectRange, "error", this.node);
		//		return false;
		//	}
		//}
		var options = {
			"type": this.json.selectType,
			"unitType": (this.json.selectUnitType==="all") ? "" : this.json.selectUnitType,
			"values": values,
			"count": count,
			"units": selectUnits,
			"dutys": (this.json.selectType=="identity") ? selectDutys : [],
			"onComplete": function(items){
				var values = [];
				items.each(function(item){
					values.push(MWF.org.parseOrgData(item.data));
				}.bind(this));
				this.setData(values);

				//this._setBusinessData(values);
				this.validationMode();
				this.validation()
			}.bind(this),
			"onCancel": function(){
				this.validation();
			}.bind(this),
			"onLoad": function(){
				if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
			}.bind(this),
			"onClose": function(){
				v = this._getBusinessData();
				if (!v || !v.length) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");

			}.bind(this)
		};
		var selector = new MWF.O2Selector(this.form.app.content, options);
	}
}); 