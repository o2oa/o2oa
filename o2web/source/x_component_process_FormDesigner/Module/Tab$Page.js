MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Tab$Page = MWF.FCTab$Page = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Tab$Page/tab$Page.html",
		"actions": [
			{
		    	"name": "move",
		    	"icon": "move1.png",
		    	"event": "mousedown",
		    	"action": "move",
		    	"title": MWF.APPFD.LP.formAction.move
		    },
		    //{
		    //	"name": "copy",
		    //	"icon": "copy1.png",
		    //	"event": "click",
		    //	"action": "copy",
		    //	"title": MWF.APPFD.LP.formAction.copy
		    //},
		    {
		    	"name": "add",
		    	"icon": "add.png",
		    	"event": "click",
		    	"action": "addPage",
		    	"title": MWF.APPFD.LP.formAction.add
		    },
		    {
		    	"name": "delete",
		    	"icon": "delete1.png",
		    	"event": "click",
		    	"action": "delete",
		    	"title": MWF.APPFD.LP.formAction["delete"]
		    }
		],
		"injectActions" : null
	},
	
	initialize: function(tab, page, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Tab$Page/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Tab$Page/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tab$Page";

		this.form = tab.form;
		this.tab = tab;
		this.page = page;
	},

    setAllStyles: function(){},

	_initModule: function(){
		this._setNodeProperty();
        if (!this.form.isSubform) this._createIconAction();
		this._setNodeEvent();
		if (this.form.moduleElementNodeList.indexOf(this.node)!=-1) this.form.moduleElementNodeList.erase(this.node);
	},
	load : function(json, node, parent){
		this.json = json;
		this.node= node;
        this.node.store("module", this);

		this.node.setStyles(this.css.moduleNode);
		
		if (!this.json.id){
			var id = this._getNewId(parent.json.id);
			this.json.id = id;
		}
		
		if (this.json.name) this.page.textNode.set("text", this.json.name);
		
		node.set({
			"MWFType": "tab$Page",
			"id": this.json.id
		});
		
		if (!this.form.json.moduleList[this.json.id]){
			this.form.json.moduleList[this.json.id] = this.json;
		}
		this._initModule();
		
		this._loadTreeNode(parent);
		
		this.parentContainer = this.treeNode.parentNode.module;
		
		this.page.setOptions({
			"onPostShow": function(){
				this.initialStyles = this.page.tab.css.tabNodeCurrent;
				this.node.store("normalBorder", this.page.tab.css.tabNodeCurrent);
			}.bind(this),
			"onPostHide": function(){
				this.initialStyles = this.page.tab.css.tabNode;
				this.node.store("normalBorder", this.page.tab.css.tabNode);
			}.bind(this)
		});
        this._setEditStyle_custom("id");

        this.json.moduleName = this.moduleName;
	},
	//_dragIn: function(module){
		//this.treeNode.parentNode.module._dragIn(module);
	//},
	
	_setEditStyle_custom: function(name){
		if (name=="width"){
			if (this.json.width){
				if (this.json.width.toInt()>60) this.node.setStyle("width", ""+this.json.width+"px");
			}
		}
		if (name=="name"){
			this.page.textNode.set("text", this.json.name);
		}
	},
	"addPage": function(){
		var page = this.tab.addPage();
		page.tabNode.inject(this.page.tabNode, "before");
        page.contentNodeArea.inject(this.page.contentNodeArea, "before");
		page.showTabIm();
	},
	"delete": function(e){
		var module = this;
		this.form.designer.confirm("warn", e, MWF.APPFD.LP.notice.deleteElementTitle, MWF.APPFD.LP.notice.deleteElement, 300, 120, function(){

			if (module.tab.containers.length<=1){
				module.tab.destroy();
			}else{
				var contentModule = module.page.contentNode.retrieve("module");
				module.destroy();
                module.tab.elements.erase(module);
				contentModule.destroy();
                module.tab.containers.erase(contentModule);
				module.page.closeTab();
			}
			this.close();
		}, function(){
			this.close();
		}, null);
	},
	destroy: function(){
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

        //this.tab.elements.erase(this);
        this.node.destroy();
		this.actionArea.destroy();
		
		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;
		
		this.treeNode.destroy();
	},

	move: function(e){
		var pageNodes = [];
		this.tab.tabWidget.pages.each(function(page){
			if (page!=this.page){
				pageNodes.push(page.tabNode);
			}
		}.bind(this));

		this._createMoveNode();

		this._setNodeMove(pageNodes, e);
		
	},
	_createMoveNode: function(){
		this.moveNode = this.node.clone();
		this.moveNode.inject(this.form.node);
		this.moveNode.setStyles({
			"border": "2px dashed #ffa200",
			"opacity": 0.7,
			"position": "absolute"
		});
	},
	
	_onEnterOther: function(){
	//	this.copyNode.setStyles(this.tab.tabWidget.css.tabNode);
	//	this.copyNode.setStyles(this.css.moduleNodeShow);
	},
	_createCopyNode: function(){
		this.copyNode = new Element("div");
		this.copyNode.setStyles(this.css.moduleNodeShow);
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},
	_setNodeMove: function(droppables, e){
		this._setMoveNodePosition(e);
		var movePosition = this.moveNode.getPosition();
		var moveSize = this.moveNode.getSize();
		var tabPosition = this.tab.node.getPosition();
		var tabSize = this.tab.node.getSize();
		
		var nodeDrag = new Drag.Move(this.moveNode, {
			"droppables": droppables,
			"limit": {
				"x": [tabPosition.x, tabPosition.x+tabSize.x],
				"y": [movePosition.y, movePosition.y+moveSize.y]
			},
			"onEnter": function(dragging, inObj){
				var module = inObj.retrieve("module");
				if (module) module._dragIn(this);
				this._onEnterOther(dragging, inObj);
			}.bind(this),
			"onLeave": function(dragging, inObj){
				var module = inObj.retrieve("module");
				if (module) module._dragOut(this);
				this._onLeaveOther(dragging, inObj);
			}.bind(this),
			"onDrop": function(dragging, inObj){
				if (inObj){
					var module = inObj.retrieve("module");
					if (module) module._dragDrop(this);
					this._nodeDrop( module );

                    if (module){
                        this.page.contentNodeArea.inject(module.page.contentNodeArea, "before");
                    }

				}else{
					this._dragCancel(dragging);
				}
			}.bind(this),
			"onCancel": function(dragging){
				this._dragCancel(dragging);
			}.bind(this)
		});
		nodeDrag.start(e);
		this.form.moveModule = this;
		this.form.recordCurrentSelectedModule = this.form.currentSelectedModule;
		this.form.selected();
	},
	_preprocessingModuleData: function(){
		this.recoveryWidgetstyle = this.node.get("style");
		this.node.clearStyles();
		//if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.json.recoveryStyles = Object.clone(this.json.styles);
		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				this.node.setStyle(key, value);
				delete this.json.styles[key];
			}
		}.bind(this));
	},
	_recoveryModuleData: function(){
		if (this.recoveryWidgetstyle) this.node.set("style", this.recoveryWidgetstyle);
		this.recoveryWidgetstyle = null;
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
	},
	setCustomStyles: function(){
		this._recoveryModuleData();
		//debugger;
		// var border = this.node.getStyle("border");
		// this.node.clearStyles();
		// this.node.setStyles(this.css.moduleNode);
		//
		// if (this.initialStyles) this.node.setStyles(this.initialStyles);
		// this.node.setStyle("border", border);
		//
		// if (this.json.styles) Object.each(this.json.styles, function(value, key){
		// 	if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
		// 		var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
		// 		var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
		// 		if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
		// 			value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
		// 		}else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
		// 			value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
		// 		}
		// 		if (value.indexOf("/x_portal_assemble_surface")!==-1){
		// 			value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
		// 		}else if (value.indexOf("x_portal_assemble_surface")!==-1){
		// 			value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
		// 		}
		// 	}
		//
		// 	var reg = /^border\w*/ig;
		// 	if (!key.test(reg)){
		// 		if (key){
		// 			if (key.toString().toLowerCase()==="display"){
		// 				if (value.toString().toLowerCase()==="none"){
		// 					this.node.setStyle("opacity", 0.3);
		// 				}else{
		// 					this.node.setStyle("opacity", 1);
		// 					this.node.setStyle(key, value);
		// 				}
		// 			}else{
		// 				this.node.setStyle(key, value);
		// 			}
		// 		}
		// 	}
		// }.bind(this));
	}

});
