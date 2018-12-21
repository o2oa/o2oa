MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Module", null, false);
MWF.xApplication.process.FormDesigner.Module.$Container = MWF.FC$Container = new Class({
	Extends: MWF.FC$Module,
	Implements: [Options, Events],
	
	_setNodeProperty: function(){
		this.node.store("module", this);
		if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
		if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
		if (this.form.moduleContainerNodeList.indexOf(this.node)==-1) this.form.moduleContainerNodeList.push(this.node);
		this.node.store("module", this);
	},
	
	_dragIn: function(module){
		if (!this.Component) module.inContainer = this;
		module.parentContainer = this;
		module.nextModule = null;
		this.node.setStyles({"border": "1px solid #ffa200"});
		var copyNode = module._getCopyNode();
		copyNode.inject(this.node);
	},
	_dragOut: function(module){
		module.inContainer = null;
		module.parentContainer = null;
		module.nextModule = null;
		this.node.setStyles(this.css.moduleNode);
	//	this.node.setStyles(this.json.styles);
		this.setCustomStyles();
		var copyNode = module._getCopyNode();
		copyNode.setStyle("display", "none");
	},
	_dragDrop: function(module){
		this.node.setStyles(this.css.moduleNode);
		//this.node.setStyles(this.json.styles);
		this.setCustomStyles();
		this.parentContainer.node.setStyles(this.css.moduleNode);
		//this.node.setStyles(this.json.styles);
		this.parentContainer.setCustomStyles();
	},
	_dragInLikeElement: function(module){
		module.parentContainer = this.parentContainer;
		module.nextModule = this;
		this.parentContainer.node.setStyles({"border": "1px solid #ffa200"});
		var copyNode = module._getCopyNode();
		copyNode.inject(this.node, "before");
	},
	
	_getSubModule: function(){
		var modules = [];
		var subNode = this.node.getFirst();
		while (subNode){
			var module = subNode.retrieve("module");
			if (module) {
				modules.push(module);
			//	if (module.moduleType=="container"){
			//		modules = modules.concat(module._getSubModule());
			//	}
			}
			subNode = subNode.getNext();
		}
		return modules;
	},
    load : function(json, node, parent){

        this.json = json;
        this.node= node;
        this.node.store("module", this);
        this.node.setStyles(this.css.moduleNode);

        this._loadNodeStyles();
        this._loadNodeCustomStyles();

        this._initModule();
        this._loadTreeNode(parent);

        this.parseModules();

        this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");
        this.json.moduleName = this.moduleName;
    },
	parseModules: function(){
		var subDom = this.node.getFirst();
		while (subDom){
			if (subDom.get("MWFtype")){
				var json = this.form.getDomjson(subDom);
				module = this.form.loadModule(json, subDom, this);
			}
			subDom = subDom.getNext();
		}
	},
	destroy: function(){
		var modules = this._getSubModule();
		modules.each(function(module){
			//module._deleteModule();
            module.destroy();
		});
		this._deleteModule();
	},
	_deleteModule: function(){
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
    copy: function(e){




        //var newNode = this.node.clone(true, true);
        //var newModuleJson = Object.clone(this.json);
        //
        //var className = this.moduleName.capitalize();
        //var newTool = new MWF["FC"+className](this.form);
        //newModuleJson.id = newTool._getNewId();
        //newNode.set("id", newModuleJson.id);


        this.copySubModule().move(e);

        //newTool.load(newModuleJson, newNode, this.form);
        //
        //newTool.move(e);
    },

    copyTo: function(node){
        if (!node) node = this.form;

        var newNode = this.node.clone(false, true);
        var newModuleJson = Object.clone(this.json);
        newNode.inject(node.node);

        var className = this.moduleName.capitalize();
        var prefix = (this.form.moduleType=="page") ? "PC" : "FC";
        var newTool = new MWF[prefix+className](this.form);
        newModuleJson.id = newTool._getNewId();
        newNode.set("id", newModuleJson.id);

        this.form.json.moduleList[newModuleJson.id] = newModuleJson;

        newTool.load(newModuleJson, newNode, node);
        return newTool;
    },


    copySubModule: function(node){

        var newModule = this.copyTo(node);
        var modules = this._getSubModule();
        modules.each(function(module){
            //module._deleteModule();
            if (module.moduleType=="container"){
                module.copySubModule(newModule);
            }
            if (module.moduleType=="element"){
                module.copyTo(newModule);
            }
            if (module.moduleType=="component"){
                module.copyTo(newModule);
            }
        });
        return newModule

        //var subNode = node.getFirst();
        //while (subNode){
        //
        //    var type = subNode.get("MWFType");
        //    if (type){
        //        var newModuleJson = Object.clone(this.form.getDomjson(subNode));
        //        newModuleJson.id = this._getNewId("", newModuleJson.type.toLowerCase());
        //        subNode.set("id", newModuleJson.id);
        //        this.form.json.moduleList[newModuleJson.id] = newModuleJson;
        //
        //        //subNode.eliminate("module");
        //
        //    //    if (module.moduleType=="container"){
        //            this.copySubModule(subNode);
        //    //    }
        //    }
        //    subNode = subNode.getNext();
        //}
    }
	
});
