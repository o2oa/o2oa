MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.process.FormDesigner.Module.Datagrid$Title = MWF.FCDatagrid$Title = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Datagrid$Title/datagrid$Title.html",
		"actions": [
		    {
		    	"name": "insertCol",
		    	"icon": "insertCol1.png",
		    	"event": "click",
		    	"action": "insertCol",
		    	"title": MWF.LP.process.formAction.insertCol
		    },
		    {
		    	"name": "deleteCol",
		    	"icon": "deleteCol1.png",
		    	"event": "click",
		    	"action": "deleteCol",
		    	"title": MWF.LP.process.formAction.deleteCol
		    }
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Datagrid$Title/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Datagrid$Title/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "datagrid$Title";
		
		this.Node = null;
		this.form = form;
	},
    setAllStyles: function(){
        Object.each(this.json.styles, function(value, key){
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key) this.node.setStyle(key, value);
            }
        }.bind(this));
        this.setPropertiesOrStyles("properties");
        this.reloadMaplist();
    },
	_dragIn: function(module){
		this.parentContainer._dragIn(module);
	},
	
	over: function(){
		if (this.form.selectedModules.indexOf(this)==-1){
			if (!this.form.moveModule) if (this.form.currentSelectedModule!=this) this.node.setStyles({
				"border-width": "1px",
				"border-color": "#0072ff"
			});
		}
	},
	unOver: function(){
		if (this.form.selectedModules.indexOf(this)==-1){
			if (!this.form.moveModule) if (this.form.currentSelectedModule!=this) this.node.setStyles({
				"border-width": "1px",
				"border-color": "#999"
			});
		}
	},
	unSelected: function(){
		this.node.setStyles({
			"border-width": "1px",
			"border-color": "#999"
		});
		this._hideActions();
		this.form.currentSelectedModule = null;
		
		this.hideProperty();
	},
	
	load : function(json, node, parent){
		this.json = json;
		this.node= node;
		this.node.store("module", this);
		this.node.setStyles(this.css.moduleNode);
		this.node.set("text", this.json.name || "DataTitle");
		
		if (!this.json.id){
			var id = this._getNewId(parent.json.id);
			this.json.id = id;
		}
		
		node.set({
			"MWFType": "datagrid$Title",
			"id": this.json.id
		});
		
		if (!this.form.json.moduleList[this.json.id]){
			this.form.json.moduleList[this.json.id] = this.json;
		}
		this._initModule();
		this._loadTreeNode(parent);
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");
        this.json.moduleName = this.moduleName;
	},
	
	_createMoveNode: function(){
		return false;
	},
	_setEditStyle_custom: function(name){

	},
	_dragInLikeElement: function(module){
		return false;
	},
	
	insertCol: function(){
		var module = this;
		var url = this.path+"insertCol.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();			
			var x = size.x/2-150;
			var y = size.y/2-90;

			var dlg = new MWF.DL({
				"title": "Insert Col",
				"style": "property",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-45,
				"fromLeft": size.x/2,
				"width": 300,
				"height": 180,
				"url": url,
				"buttonList": [
				    {
				    	"text": MWF.APPFD.LP.button.ok,
				    	"action": function(){

				    		module._insertCol();
				    		this.close();
				    	}
				    },
				    {
				    	"text": MWF.APPFD.LP.button.cancel,
				    	"action": function(){
				    		this.close();
				    	}
				    }
				]
			});
			
			dlg.show();
		}.bind(this));
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
				var thElement = new MWF.FCDatagrid$Title(this.form);
				thElement.load(moduleData, newTh, this.parentContainer);
				this.parentContainer.elements.push(thElement);
			}.bind(this));
		}
		
		var baseTd = dataTr.cells[colIndex];
		for (var n=1; n<=cols; n++){
			var newTd = new Element("td").inject(baseTd, position);
			this.form.getTemplateData("Datagrid$Data", function(data){
				var moduleData = Object.clone(data);
				var tdContainer = new MWF.FCDatagrid$Data(this.form);
				tdContainer.load(moduleData, newTd, this.parentContainer);
				this.parentContainer.containers.push(tdContainer);
			}.bind(this));
		}
		
		this.unSelected();
		this.selected();
	},
	
	deleteCol: function(e){
		var module = this;
		this.form.designer.confirm("warn", e, MWF.LP.process.notice.deleteColTitle, MWF.LP.process.notice.deleteCol, 300, 120, function(){
			module._deleteCol();
			this.close();
		}, function(){
			this.close();
		}, null);
	},
	_deleteCol: function(){
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		var colIndex = this.node.cellIndex;
		
		var titleTr = table.rows[0];
		var dataTr = table.rows[1];

		this.unSelected();

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
	},
	_setEditStyle_custom: function(name){
		if (name=="name"){
			if (!this.json.name){
				this.node.set("text", "DataTitle");
			}else{
				this.node.set("text", this.json.name);
			}
		}
	}
});
