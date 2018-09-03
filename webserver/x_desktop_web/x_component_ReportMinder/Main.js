MWF.xApplication.Report = MWF.xApplication.Report || {};
MWF.xApplication.ReportMinder = MWF.xApplication.ReportMinder || {};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("Report", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Report", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Report", "Common", null, false);

MWF.xApplication.ReportMinder.options = {
	multitask: true,
	executable: true
};
MWF.xApplication.ReportMinder.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ReportMinder",
		"icon": "icon.png",
		"width": "1324",
		"height": "720",
		"isResize": true,
		"isMax": true,
		"isNew" : false,
		"isEdited" : true,
		"title": MWF.xApplication.ReportMinder.LP.title,
		"id" : ""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Report.LP;
	},
	onQueryClose: function(){

	},
	loadApplication: function(callback){
		this.userData = layout.desktop.session.user;
		this.userName = this.userData.distinguishedName;
		this.restActions = this.actions = MWF.Actions.get("x_report_assemble_control"); //new MWF.xApplication.Report.Actions.RestActions();
		this.common = new MWF.xApplication.Report.Common(this);

		this.path = "/x_component_ReportMinder/$Main/"+this.options.style+"/";

		if( this.status ){
			this.setOptions( this.status )
		}

		this.createNode();
		this.loadApplicationLayout();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		//this.node = new Element("div", {
		//	"styles": this.css.node
		//}).inject(this.content);
	},
	clearContent : function(){
		this.content.empty();
	},
	reload : function(oldid, appid){
		this.content.empty();
		this.loadApplicationLayout();

	},
	loadApplicationLayout : function(){
		if( this.options.mindType == "department" ){
			MWF.xDesktop.requireApp("Report", "DepartmentMinder", null, false);
			this.minder = new MWF.xApplication.Report.DepartmentMinder(this.content, this, this.restActions, {
				department : this.options.department,
				year : this.options.year
			});
			this.minder.load();
		}else{
			MWF.xDesktop.requireApp("Report", "KeyWorkMinder", null, false);
			this.minder = new MWF.xApplication.Report.KeyWorkMinder(this.content, this, this.restActions, {
				id : this.options.id,
				year : this.options.year
			});
			this.minder.load();
		}
	},
	recordStatus: function(){
		return this.options;
	}

});