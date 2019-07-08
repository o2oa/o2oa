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
		module.onDragModule = this;
        if (this.parentContainer.moduleName == "datagrid$Data"){
            this.parentContainer._dragIn(module);
        }else{
            module.inContainer = null;
            module.parentContainer = this.parentContainer;
            module.nextModule = this;
            //this.parentContainer.node.setStyles({"border": "1px solid #ffa200"});

			this.node.setStyles({"border": "1px solid #ffa200"});

			if (module.controlMode){
				if (module.copyNode) module.copyNode.hide();
			}else{
				var copyNode = module._getCopyNode(this);
				copyNode.show();
				copyNode.inject(this.node, "before");
			}

			//this._showInjectAction( module );
			// var e = new Event(event);
			// if (e.control){
			//
			// }else{
			// 	var copyNode = module._getCopyNode(this);
			// 	copyNode.inject(this.node, "before");
			// }

        }
	},

	_setControlModeNode: function(){
		if (this.controlMode){
			if (this.copyNode) this.copyNode.hide();
		}else{
			if (this.onDragModule) this.onDragModule._dragIn(this);
		}
	},
	_dragOut: function(module){
		module.onDragModule = null;
		module.inContainer = null;
		module.parentContainer = null;
		module.nextModule = null;

		//this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
		//this.parentContainer.setCustomStyles();
		this.node.setStyles(this.css.moduleNode);
		this.setCustomStyles();

		//this._hideInjectAction();

		// if (this._controlKeyEventFun){
		// 	this.node.removeEvent("keydown", this._controlKeyEventFun);
		// 	this._controlKeyEventFun = null;
		// }
		if (!module.controlMode){
			var copyNode = module._getCopyNode();
			copyNode.setStyle("display", "none");
		}

	},
	_dragDrop: function(module, flag){
		var f = flag || !(new Event(event)).control;
		if( f ){
			this.node.setStyles(this.css.moduleNode);
			this.setCustomStyles();
		}
		//this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
		//this.parentContainer.setCustomStyles();
		//this._hideInjectAction();
	},
	destroy: function(){
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

        if (this.form.scriptDesigner){
            this.form.scriptDesigner.removeModule(this.json);
		}

        if (this.property) this.property.destroy();
		this.node.destroy();
		this.actionArea.destroy();
		
		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;
		
		this.treeNode.destroy();
		o2.release(this);
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
