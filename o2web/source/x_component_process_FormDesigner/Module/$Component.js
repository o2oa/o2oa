MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Module", null, false);
MWF.xApplication.process.FormDesigner.Module.$Component = MWF.FC$Component = new Class({
	Extends: MWF.FC$Module,
	Implements: [Options, Events],
	
	_setNodeProperty: function(){
		this.node.store("module", this);
		if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
		if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
		if (this.form.moduleComponentNodeList.indexOf(this.node)==-1) this.form.moduleComponentNodeList.push(this.node);

//		this.containers.each(function(container){
//			if (this.form.moduleContainerNodeList.indexOf(container.node)==-1) this.form.moduleContainerNodeList.push(container.node);
//			container.node.store("module", container);
//		}.bind(this));
		
//		this.elements.each(function(element){
//			if (this.form.moduleElementNodeList.indexOf(element.node)==-1) this.form.moduleElementNodeList.push(element.node);
//			element.node.store("module", element);
//		}.bind(this));
	},
	_initModule: function(){
		if (!this.initialized){
            if (this.json.initialized!=="yes") this.setStyleTemplate();

            this.setPropertiesOrStyles("styles");
            this.setPropertiesOrStyles("properties");

			this._getContainers();
			this._getElements();

			this._setNodeProperty();
            if (!this.form.isSubform) this._createIconAction();
			this._setNodeEvent();
			
			this.initialized = true;
			this.json.initialized = "yes";
		}
	},
	_getElements: function(){
	//	this.elements.push(this);
	},
	_getContainers: function(){
		//this.containers
	},
	destroy: function(){
		this.containers.each(function(module){
			//module._deleteModule();
            module.destroy();
		});
		this.elements.each(function(module){
			module._deleteModule();
		});
		this._deleteModule();
	},
	_deleteModule: function(){
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
	_dragIn: function(module){
        module.onDragModule = this;
		module.inContainer = null;
		module.parentContainer = this.parentContainer;
		module.nextModule = this;
		//this.parentContainer.node.setStyles({"border": "1px solid #ffa200"});

        this.node.setStyles({"border": "1px solid #ffa200"});

        //this._showInjectAction( module );

        if (module.controlMode){
            if (module.copyNode) module.copyNode.hide();
        }else{
            var copyNode = module._getCopyNode(this);
            copyNode.show();
            copyNode.inject(this.node, "before");
        }

		// var copyNode = module._getCopyNode();
		// copyNode.inject(this.node, "before");
	},
    _setControlModeNode: function(){
        if (this.controlMode){
            if (this.copyNode) this.copyNode.hide();
        }else{
            if (this.onDragModule) this.onDragModule._dragIn(this);
        }
    },

	_dragOut: function(module){
		module.inContainer = null;
		module.parentContainer = null;
		module.nextModule = null;

		//this.parentContainer.node.setStyles(this.parentContainer.css.moduleNode);
		//this.parentContainer.node.setStyles(this.parentContainer.json.styles);
        this.node.setStyles(this.css.moduleNode);
        this.setCustomStyles();

        //this._hideInjectAction();

		var copyNode = module._getCopyNode();
		copyNode.setStyle("display", "none");
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
    copy: function(e){
        var newModule = this.copyTo();
        newModule.move(e);
    },

    copyClearContainer: function(){
        var containerTmpNodes = [];
        this.containers.each(function(module){
            var containerTmpNode = new Element("div");
            var subNode = module.node.getFirst();
            while (subNode){
                var tmpSubNode = subNode.getNext();
                subNode.inject(containerTmpNode);
                subNode = tmpSubNode;
            }
            containerTmpNodes.push(containerTmpNode);
        }.bind(this));
        return containerTmpNodes;
    },
    copyRecoverContainer: function(containerTmpNodes){
        this.containers.each(function(module, idx){
            var containerTmpNode = containerTmpNodes[idx];
            var subNode = containerTmpNode.getFirst();
            while (subNode){
                var tmpSubNode = subNode.getNext();
                subNode.inject(module.node);
                subNode = tmpSubNode;
            }
            containerTmpNode.destroy();
        }.bind(this));
        delete containerTmpNodes;
    },

    copyTo: function(node){
        if (!node) node = this.form;

        var containerTmpNodes = this.copyClearContainer();

        var newNode = this.node.clone(true);
        var newModuleJson = Object.clone(this.json);
        newNode.inject(node.node);

        var className = this.moduleName.capitalize();
        var prefix = (this.form.moduleType=="page") ? "PC" : "FC";
        var newTool = new MWF[prefix+className](this.form);

        newModuleJson.id = newTool._getNewId();
        newNode.set("id", newModuleJson.id);
        this.form.json.moduleList[newModuleJson.id] = newModuleJson;
        if (this.form.scriptDesigner) this.form.scriptDesigner.createModuleScript(newModuleJson);

        this.copyComponentJsonData(newNode, newModuleJson.id);


        newTool.load(newModuleJson, newNode, node);

        this.copyRecoverContainer(containerTmpNodes);

        this.containers.each(function(module, idx){
            var modules = module._getSubModule();
            modules.each(function(subModule){
                //module._deleteModule();
                if (subModule.moduleType=="container"){
                    subModule.copySubModule(newTool.containers[idx]);
                }
                if (subModule.moduleType=="element"){
                    subModule.copyTo(newTool.containers[idx]);
                }
                if (subModule.moduleType=="component"){
                    subModule.copyTo(newTool.containers[idx]);
                }
            });
        });

        return newTool;
    },


	parseModules: function(){}

});
