MWF.xDesktop.requireApp("process.Xform", "Calendar", null, false);
MWF.xApplication.cms.Xform.Calendar = MWF.CMSCalendar =  new Class({
	Extends: MWF.APPCalendar,
	clickSelect: function() {
		if (!this.calendar) {
			MWF.require("MWF.widget.Calendar", function () {
				this.calendar = new MWF.widget.Calendar(this.node.getFirst(), {
					"style": "xform",
					"isTime": (this.json.selectType === "datetime" || this.json.selectType === "time"),
					"timeOnly": (this.json.selectType === "time"),
					//"target": this.form.node,
					"target": this.form.app.content,
					"format": this.json.format,
					"onComplate": function () {
						this.validationMode();
						//this.validation();
						if (this.validation()) this._setBusinessData(this.getInputData("change"));
						this.fireEvent("complete");
					}.bind(this),
					"onChange": function () {
						this.fireEvent("change");
					}.bind(this),
					"onClear": function () {
						this.validationMode();
						//this.validation();
						if (this.validation()) this._setBusinessData(this.getInputData("change"));
						this.fireEvent("clear");
						if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
					}.bind(this),
					"onShow": function () {
						if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
					}.bind(this),
					"onHide": function () {
						if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
					}.bind(this)
				});
				this.calendar.show();
			}.bind(this));
		} else {
			this.node.getFirst().focus();
		}
	}
}); 