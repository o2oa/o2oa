MWF.xApplication.Report = MWF.xApplication.Report || {};
MWF.xApplication.ReportDocument = MWF.xApplication.ReportDocument || {};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("Report", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Report", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Report", "Common", null, false);
MWF.xDesktop.requireApp("Report", "Setting", null, false);
MWF.xDesktop.requireApp("Report", "StrategyExplorer", null, false);

MWF.xApplication.ReportDocument.options = {
	multitask: true,
	executable: true
};
MWF.xApplication.ReportDocument.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ReportDocument",
		"icon": "icon.png",
		"width": "1324",
		"height": "720",
		"isResize": true,
		"isMax": true,
		"isNew" : false,
		"isEdited" : true,
		"title": MWF.xApplication.ReportDocument.LP.title,
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
		this.restActions = this.actions =  MWF.Actions.get("x_report_assemble_control"); //new MWF.xApplication.Report.Actions.RestActions();
		this.common = new MWF.xApplication.Report.Common(this);

		this.path = "/x_component_ReportDocument/$Main/"+this.options.style+"/";

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
		this.report = new MWF.xApplication.Report.StrategyExplorer(this.content, this, this.restActions, {
			id : this.options.id
		});
		this.report.load();
	},
	recordStatus: function(){
		return {
			"id" : this.options.id,
			"isEdited" : this.options.isEdited,
			"isNew" : this.options.isNew
		};
	},
	createNewDocument: function(){
		var _self = this;
		var appId = "ReportDocument"+this.sectionData.id;
		if (_self.desktop.apps[appId]){
			_self.desktop.apps[appId].setCurrent();
		}else {
			this.desktop.openApplication(null, "ReportDocument", {
				"sectionId": this.sectionData.id,
				"appId": appId,
				"isNew" : true,
				"isEdited" : true,
				"onPostPublish" : function(){
					//this.view.reload();
				}.bind(this)
			});
		}
	},
	edit : function(){
		var appId = "ReportDocument"+this.data.id;
		this.options.isEdited = true;
		this.reload(appId , appId );
	},
	delete : function( ev ){
		var _self = this;
		this.confirm("warn", ev, this.lp.deleteDocumentTitle, this.lp.deleteDocument, 350, 120, function(){
			_self.restActions.deleteSubject( _self.data.id, function(){
				_self.notice( _self.lp.deleteDocumentOK, "ok");
				_self.reloadAllParents();
				_self.close();
			}.bind(this) );
			this.close();
		}, function(){
			this.close();
		});
	}

});