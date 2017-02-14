MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.cms.FormDesigner.Module.Datagrid$Data = MWF.CMSFCDatagrid$Data = new Class({
	Extends: MWF.CMSFCTable$Td,
	Implements: [Options, Events],
	options: {
        "propertyPath": "/x_component_cms_FormDesigner/Module/Datagrid$Data/datagrid$Data.html",
		"actions": [
		    {
		    	"name": "insertCol",
		    	"icon": "insertCol1.png",
		    	"event": "click",
		    	"action": "insertCol",
		    	"title": MWF.xApplication.cms.FormDesigner.LP.formAction.insertCol
		    },
		    {
		    	"name": "deleteCol",
		    	"icon": "deleteCol1.png",
		    	"event": "click",
		    	"action": "deleteCol",
		    	"title": MWF.xApplication.cms.FormDesigner.LP.formAction.deleteCol
		    }
		],
		"allowModules": ["textfield", "personfield", "calendar", "textarea", "select", "radio", "checkbox", "html", "image", "label", "htmleditor", "button"]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Datagrid$Data/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Datagrid$Data/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "datagrid$Data";
		
		this.Node = null;
		this.form = form;
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
		
		node.set({
			"MWFType": "datagrid$Data",
			"id": this.json.id
		});
		
		if (!this.form.json.moduleList[this.json.id]){
			this.form.json.moduleList[this.json.id] = this.json;
		}
		this._initModule();
		this._loadTreeNode(parent);
		this.form.parseModules(this, this.node);
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");
        this.checkSequence();
	},
    _setEditStyle_custom: function(name, obj, oldValue) {
        if (name == "cellType") this.checkSequence(obj, oldValue);
    },
    checkSequence: function(obj, oldValue){
        if ((this.json.cellType == "sequence") && (oldValue != "sequence")){
            if (this.treeNode.firstChild){
                var _self = this;
                var module = this.treeNode.firstChild.module;
                this.form.designer.confirm("warn", module.node, MWF.CMSFD.LP.notice.changeToSequenceTitle, MWF.CMSFD.LP.notice.changeToSequence, 300, 120, function(){
                    module.destroy();
                    this.close();

                    if (!_self.sequenceNode){
                        _self.sequenceNode = new Element("div", {"styles": _self.css.sequenceNode, "text": "(N)", "MWFType": "MWFTemp"}).inject(_self.node);
                    }

                }, function(){
                    _self.json.cellType == "content";
                    obj.checked = false;

                    this.close();
                }, null);
            }else{
                if (!this.sequenceNode){
                    this.sequenceNode = new Element("div", {"styles": this.css.sequenceNode, "text": "(N)", "MWFType": "MWFTemp"}).inject(this.node);
                }
            }
        }else{
            if (this.sequenceNode){
                this.sequenceNode.destroy();
                this.sequenceNode = null;
            }
        }
    },
	_dragIn: function(module){

		if (this.treeNode.firstChild || this.json.cellType == "sequence"){
			this.parentContainer._dragIn(module);
		}else{
			if (this.options.allowModules.indexOf(module.moduleName)!=-1){
				if (!this.Component) module.inContainer = this;
				module.parentContainer = this;
				module.nextModule = null;
				this.node.setStyles({"border": "1px solid #ffa200"});
				var copyNode = module._getCopyNode();
				copyNode.inject(this.node);
			}else{
				this.parentContainer._dragIn(module);
			}
		}
	},
	
	_showActions: function(){
		if (this.actionArea){
			this._setActionAreaPosition();
			this.actionArea.setStyle("display", "block");
		}
	},
	_insertCol: function(){
		var cols = $("MWFInsertColNumber").get("value");
		var positionRadios = document.getElementsByName("MWFInsertColPosition");
		var position = "before";
		for (var i=0; i<positionRadios.length; i++){
			if (positionRadios[i].checked){
				position = positionRadios[i].value;
				break;
			}
		}
		
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		
		var colIndex = this.node.cellIndex;
		var titleTr = table.rows[0];
		var dataTr = table.rows[1];
		
		var baseTh = titleTr.cells[colIndex];
		for (var m=1; m<=cols; m++){
			var newTh = new Element("th").inject(baseTh, position);
			this.form.getTemplateData("Datagrid$Title", function(data){
				var moduleData = Object.clone(data);
				var thElement = new MWF.CMSFCDatagrid$Title(this.form);
				thElement.load(moduleData, newTh, this.parentContainer);
				this.parentContainer.elements.push(thElement);
			}.bind(this));
		}
		
		var baseTd = dataTr.cells[colIndex];
		for (var n=1; n<=cols; n++){
			var newTd = new Element("td").inject(baseTd, position);
			this.form.getTemplateData("Datagrid$Data", function(data){
				var moduleData = Object.clone(data);
				var tdContainer = new MWF.CMSFCDatagrid$Data(this.form);
				tdContainer.load(moduleData, newTd, this.parentContainer);
				this.parentContainer.containers.push(tdContainer);
			}.bind(this));
		}
		
		this.unSelected();
		this.selected();
		
	},
	_deleteCol: function(){
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		var colIndex = this.node.cellIndex;
		
		var titleTr = table.rows[0];
		var dataTr = table.rows[1];
		
		if (tr.cells.length<=1){
			this.parentContainer.destroy();
		}else{
			var deleteTh = titleTr.cells[colIndex];
			var deleteTd = dataTr.cells[colIndex];

			var thModule = deleteTh.retrieve("module");
			if (thModule){
				thModule.parentContainer.elements.erase(thModule);
				thModule.destroy();
			}
			
			var tdModule = deleteTd.retrieve("module");
			if (tdModule){
				tdModule.parentContainer.containers.erase(tdModule);
				tdModule.destroy();
			}
			
		}
	}
	
});
