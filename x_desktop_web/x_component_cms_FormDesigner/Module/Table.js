MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Table", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "Property", null, false);
MWF.xApplication.cms.FormDesigner.Module.Table = MWF.CMSFCTable = new Class({
	Extends: MWF.FCTable,
	Implements : [MWF.CMSFCMI],
	showMultiProperty: function(){
		if (this.form.propertyMultiTd){
			this.form.propertyMultiTd.hide();
			this.form.propertyMultiTd = null;
		}

		this.form.propertyMultiTd = new MWF.xApplication.cms.FormDesigner.PropertyMulti(this.form, this.form.selectedModules, this.form.designer.propertyContentArea, this.form.designer, {
			"path": this.options.propertyMultiPath,
			"onPostLoad": function(){
				this.show();
			}
		});
		this.form.propertyMultiTd.load();
	}
});