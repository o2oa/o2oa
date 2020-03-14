MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Component", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datagrid$Data", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datagrid$Title", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Table$Td", null, false);
MWF.xApplication.process.FormDesigner.Module.Datagrid = MWF.FCDatagrid = new Class({
	Extends: MWF.FC$Component,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Datagrid/datagrid.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Datagrid/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Datagrid/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "datagrid";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
		this.selectedMultiTds = [];
	},
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.tableStyles) this.removeStyles(styles.tableStyles, "tableStyles");
            if (styles.titleStyles) this.removeStyles(styles.titleStyles, "titleStyles");
            if (styles.contentStyles) this.removeStyles(styles.contentStyles, "contentStyles");
            if (styles.actionStyles) this.removeStyles(styles.actionStyles, "actionStyles");
            if (styles.editStyles) this.removeStyles(styles.editStyles, "editStyles");
            if (styles.amountStyles) this.removeStyles(styles.amountStyles, "amountStyles");
            if (styles.itemTitleStyles) this.removeStyles(styles.itemTitleStyles, "itemTitleStyles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
        }
    },
    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.tableStyles) this.copyStyles(styles.tableStyles, "tableStyles");
        if (styles.titleStyles) this.copyStyles(styles.titleStyles, "titleStyles");
        if (styles.contentStyles) this.copyStyles(styles.contentStyles, "contentStyles");
        if (styles.actionStyles) this.copyStyles(styles.actionStyles, "actionStyles");
        if (styles.editStyles) this.copyStyles(styles.editStyles, "editStyles");
        if (styles.amountStyles) this.copyStyles(styles.amountStyles, "amountStyles");
        if (styles.itemTitleStyles) this.copyStyles(styles.itemTitleStyles, "itemTitleStyles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
    },
	_createMoveNode: function(){

		var tableHTML = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\" width=\"100%\" align=\"center\">";
		tableHTML += "<tr><th></th><th></th><th></th></tr>";
		tableHTML += "<tr><td></td><td></td><td></td></tr>";
		tableHTML += "</table>";
		this.moveNode = new Element("div", {
			"html": tableHTML
		}).inject(this.form.container);
//		this.moveNode = divNode.getFirst(); 
//		this.moveNode.inject(divNode, "after");
//		divNode.destroy();
		
		this.moveNode.setStyles(this.css.moduleNodeMove);
		
		this._setTableStyle();
	},
	_setTableStyle: function(){
		var tds = this.moveNode.getElements("td");
		tds.setStyles({
			"border": "1px dashed #999",
			"height": "20px"
		});
		var tds = this.moveNode.getElements("th");
		tds.setStyles({
			"background": "#C3CEEA",
			"border": "1px dashed #999",
			"height": "20px"
		});
	},

	_getContainers: function(){

		var tds = this.node.getElements("td");
		
		this.form.getTemplateData("Datagrid$Data", function(data){
			tds.each(function(td){
				var json = this.form.getDomjson(td);
				var tdContainer = null;
				if (!json){
					var moduleData = Object.clone(data);
					tdContainer = new MWF.FCDatagrid$Data(this.form);
					tdContainer.load(moduleData, td, this);
				}else{
					tdContainer = new MWF.FCDatagrid$Data(this.form);
					tdContainer.load(json, td, this);
				}
				this.containers.push(tdContainer);
			}.bind(this));
		}.bind(this), false);
	},
	_getElements: function(){
		//this.elements.push(this);
		var ths = this.node.getElements("th");
		
		this.form.getTemplateData("Datagrid$Title", function(data){
			ths.each(function(th){
				var json = this.form.getDomjson(th);
				var thElement = null;
				if (!json){
					var moduleData = Object.clone(data);
					thElement = new MWF.FCDatagrid$Title(this.form);
					thElement.load(moduleData, th, this);
				}else{
					thElement = new MWF.FCDatagrid$Title(this.form);
					thElement.load(json, th, this);
				}
				this.elements.push(thElement);
			}.bind(this));
		}.bind(this), false);
	},
	
	_createNode: function(callback){
		var module = this;
		var url = this.path+"datagridCreate.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();			
			var x = size.x/2-180;
			var y = size.y/2-130;

			var dlg = new MWF.DL({
				"title": "Create Datagrid",
				"style": "property",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-65,
				"fromLeft": size.x/2,
				"width": 360,
				"height": 260,
				"url": url,
				"buttonList": [
				    {
				    	"text": MWF.APPFD.LP.button.ok,
				    	"action": function(){
				    		module._createTableNode();
				    		callback();
				    		this.close();
				    	}
				    }
				]
			});
			
			dlg.show();
		}.bind(this));
	},
	_createTableNode: function(){
		var cols = $("MWFNewTableColumn").get("value");
		
		var width = $("MWFNewTableWidth").get("value");
		var widthUnitNode = $("MWFNewTableWidthUnit");
		var widthUnit = widthUnitNode.options[widthUnitNode.selectedIndex].value;
		
		var border = $("MWFNewTableBorder").get("value");
		var cellpadding = $("MWFNewTableCellpadding").get("value");
		var cellspacing = $("MWFNewTableCellspacing").get("value");
		
		var w = "";
		if (widthUnit=="percent"){
			w = width+"%";
		}else{
			w = width+"px";
		}
		
		this.json.properties.width = w;
		this.json.properties.border = border;
		this.json.properties.cellpadding = cellpadding;
		this.json.properties.cellspacing = cellspacing;
		
		var tableHTML = "<table border=\""+border+"\" cellpadding=\""+cellpadding+"\" cellspacing=\""+cellspacing+"\" width=\""+w+"\" align=\"center\">";
		tableHTML += "<tr>";
		for (var j=0; j<cols.toInt(); j++){
			tableHTML += "<th></th>";
		}
		tableHTML += "</tr>";
		tableHTML += "<tr>";
		for (var j=0; j<cols.toInt(); j++){
			tableHTML += "<td></td>";
		}
		tableHTML += "</tr></table>";
		
		this.node = new Element("div", {
			"id": this.json.id,
			"MWFType": "datagrid",
			"html": tableHTML,
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(e){
					e.preventDefault();
				}
			}
		}).inject(this.form.node);

        this.table = this.node.getElement("table");
	},
	_dragComplete: function(){
		if (!this.node){
			this._createNode(function(){
				this._dragMoveComplete();
			}.bind(this)); 
		}else{
			this._dragMoveComplete();
		}
	},
	_dragMoveComplete: function(){
		this._resetTreeNode();
		this.node.inject(this.copyNode, "before");
		
		this._initModule();
		
		var thisDisplay = this.node.retrieve("thisDisplay");
		if (thisDisplay){
			this.node.setStyle("display", thisDisplay);
		}
		
		if (this.copyNode) this.copyNode.destroy();
		if (this.moveNode) this.moveNode.destroy();
		this.moveNode = null;
		this.copyNode = null;
		this.nextModule = null;
		this.form.moveModule = null;

		this.form.json.moduleList[this.json.id] = this.json;
		this.selected();
	},
	
	_initModule: function(){
        if (!this.initialized){
			if (this.json.initialized!=="yes")this.setStyleTemplate();

			this.table = this.node.getElement("table");
			this._getElements();
			this._getContainers();

			this.setPropertiesOrStyles("styles");
			this.setPropertiesOrStyles("tableStyles");
			this.setPropertiesOrStyles("properties");

			this._setNodeProperty();
			if (!this.form.isSubform) this._createIconAction();

	   //     this.checkSequenceShow();

			this._setNodeEvent();

			this.setDatagridStyles();

			this.initialized = true;
			this.json.initialized = "yes";
		}
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			var border = this.node.getStyle("border");
			this.node.clearStyles();
			this.node.setStyles(this.css.moduleNode);
			this.node.setStyle("border", border);
			Object.each(this.json.styles, function(value, key){
				var reg = /^border\w*/ig;
				if (!key.test(reg)){
					this.node.setStyle(key, value);
				}
			}.bind(this));
		}
        if (name=="tableStyles"){
            this.table.clearStyles();
            Object.each(this.json.tableStyles, function(value, key){
                var reg = /^border\w*/ig;
                if (!key.test(reg)){
                    this.table.setStyle(key, value);
                }
            }.bind(this));
        }

		if (name=="properties"){
			this.node.getFirst().setProperties(this.json.properties);
		}
	},
	_setEditStyle_custom: function(name, obj, oldValue){
		if (name=="id"){
			if (name!=oldValue){
				var reg = new RegExp("^"+oldValue, "i");
				this.containers.each(function(container){
					var id = container.json.id;
					var newId = id.replace(reg, this.json.id);
					container.json.id = newId;
					
					delete this.form.json.moduleList[id];
					this.form.json.moduleList[newId] = container.json;
					container._setEditStyle("id");
				}.bind(this));
			}
		}

        if (name=="titleStyles"){
            var ths = this.table.getElements("th");
            ths.each(function(th){
                this.setCustomNodeStyles(th, this.json.titleStyles);
            }.bind(this));
        }
        if (name=="contentStyles"){
            var tds = this.table.getElements("td");
            tds.each(function(td){
                this.setCustomNodeStyles(td, this.json.contentStyles);
            }.bind(this));
        }
        //if (name=="sequence") this.checkSequenceShow();
	},
    setDatagridStyles: function(){
        if (this.json.titleStyles){
            var ths = this.table.getElements("th");
            ths.each(function(th){
                this.setCustomNodeStyles(th, this.json.titleStyles);
            }.bind(this));
        }
        if (this.json.contentStyles){
            var tds = this.table.getElements("td");
            tds.each(function(td){
                this.setCustomNodeStyles(td, this.json.contentStyles);
            }.bind(this));
        }
    },
    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("tableStyles");
        this.setPropertiesOrStyles("properties");

        this.setDatagridStyles();

        this.reloadMaplist();
    },

    //checkSequenceShow: function(){
    //    if (this.json.sequence=="yes"){
    //        if (!this.sequenceTitleTd || !this.sequenceTd){
    //            if (this.sequenceTitleTd){
    //                this.sequenceTitleTd.destroy();
    //                this.sequenceTitleTd = null;
    //            }
    //            if (this.sequenceTd){
    //                this.sequenceTd.destroy();
    //                this.sequenceTd = null;
    //            }
    //            var trs = this.node.getElements("tr");
    //            if (trs[0]){
    //                this.sequenceTitleTd = new Element("th", {"styles": this.css.sequenceTitleTd}).inject(trs[0], "top");
    //            }
    //            if (trs[1]){
    //                this.sequenceTd = new Element("td", {"styles": this.css.sequenceTd, "text": "1"}).inject(trs[1], "top");
    //            }
    //        }
    //    }else{
    //        if (this.sequenceTitleTd){
    //            this.sequenceTitleTd.destroy();
    //            this.sequenceTitleTd = null;
    //        }
    //        if (this.sequenceTd){
    //            this.sequenceTd.destroy();
    //            this.sequenceTd = null;
    //        }
    //    }
    //},
	getContainerNodes: function(){
		return this.node.getElements("td");
	},
    copyComponentJsonData: function(newNode, pid){
        var tds = newNode.getElements("td");
        var ths = newNode.getElements("th");
        tds.each(function(td, idx){
            var newContainerJson = Object.clone(this.containers[idx].json);
            newContainerJson.id = this.containers[idx]._getNewId(pid);
            this.form.json.moduleList[newContainerJson.id] = newContainerJson;
            td.set("id", newContainerJson.id);
        }.bind(this));
        ths.each(function(th, idx){
            var newElementJson = Object.clone(this.elements[idx].json);
            newElementJson.id = this.elements[idx]._getNewId(pid);
            this.form.json.moduleList[newElementJson.id] = newElementJson;
            th.set("id", newElementJson.id);
        }.bind(this));
    }
	
});