MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Module", null, false);
MWF.xApplication.process.FormDesigner.Module.$Element = MWF.FC$Element = new Class({
	Extends: MWF.FC$Module,
	Implements: [Options, Events],
	
	_setNodeProperty: function(){
		if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
		if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
		if (this.form.moduleElementNodeList.indexOf(this.node)==-1) this.form.moduleElementNodeList.push(this.node);
		this.node.store("module", this);
	},
	
	_dragIn: function(module){
        if (this.parentContainer.moduleName == "datagrid$Data"){
            this.parentContainer._dragIn(module);
        }else{
            module.inContainer = null;
            module.parentContainer = this.parentContainer;
            module.nextModule = this;
            this.parentContainer.node.setStyles({"border": "1px solid #ffa200"});
            var copyNode = module._getCopyNode();
            copyNode.inject(this.node, "before");
        }
	},
	_dragOut: function(module){
		module.inContainer = null;
		module.parentContainer = null;
		module.nextModule = null;
		this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
		this.parentContainer.setCustomStyles();
		var copyNode = module._getCopyNode();
		copyNode.setStyle("display", "none");
	},
	_dragDrop: function(module){
	//	this.node.setStyles(this.css.moduleNode);
	//	this.node.setStyles(this.json.styles);
		this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
		this.parentContainer.setCustomStyles();
	},
	destroy: function(){
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);
		
		this.node.destroy();
		this.actionArea.destroy();
		
		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;
		
		this.treeNode.destroy();
	},
	parseModules: function(){},
	_deleteModule: function(){
		this.destroy();
	},
    getJson: function(){
        var json = Object.clone(this.json);
        var o = {};
        o[json.id] = json;
        return o;
    }
	
});
