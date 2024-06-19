MWF.xDesktop.requireApp("process.Xform", "Tab", null, false);
MWF.xApplication.cms.Xform.Tab = MWF.CMSTab =  new Class({
	Extends: MWF.APPTab
});

MWF.xApplication.cms.Xform.tab$Page = MWF.CMSTab$Page = new Class({
	Extends: MWF.APPTab$Page
});
MWF.xApplication.cms.Xform.tab$Content = MWF.CMSTab$Content = new Class({
	Extends: MWF.APPTab$Content,
	_loadUserInterface: function(){
		var _self = this;
		this.form._loadModules(this.node, function () {
			if( _self.widget )this.widget = _self.widget;
		}, null, function (moduleNodes, moduleJsons, modules) {
			var hasSubModule = false;
			(moduleJsons || []).each(function (json) {
				//流程组件
				if( ( json.type === "Log" && json.logType ) || ["Sidebar","Monitor","ReadLog"].contains(json.type) ){
				}else{
					hasSubModule = true;
				}
			});
			if(!hasSubModule){
				_self.page.tabNode.hide();
			}
		});
	}
});