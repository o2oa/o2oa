MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textarea", null, false);
MWF.xApplication.process.FormDesigner.Module.Opinion = MWF.FCOpinion = new Class({
	Extends: MWF.FCTextarea,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "opinion",
		"path": "../x_component_process_FormDesigner/Module/Opinion/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Opinion/opinion.html"
	},
	_getDroppableNodes: function(){
		var nodes = [this.form.node].concat(this.form.moduleElementNodeList, this.form.moduleContainerNodeList, this.form.moduleComponentNodeList);
		this.form.moduleList.each( function(module){
			//意见不能往数据模板里拖
			if( module.moduleName === "datatemplate" ){
				var subDoms = this.form.getModuleNodes(module.node);
				nodes.erase( module.node );
				subDoms.each(function (dom) {
					nodes.erase( dom );
				})
			}
		}.bind(this));
		return nodes;
	}
});
