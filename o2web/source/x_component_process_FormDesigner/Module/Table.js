MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Component", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Table$Td", null, false);
MWF.xApplication.process.FormDesigner.Module.Table = MWF.FCTable = new Class({
	Extends: MWF.FC$Component,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Table/table.html",
        "propertyMultiPath": "../x_component_process_FormDesigner/Module/Table$Td/table$tdMulti.html",
		"multiActions": [
			{
		    	"name": "mergerCell",
		    	"icon": "mergerCell.png",
		    	"event": "click",
		    	"action": "mergerCell",
		    	"title": MWF.APPFD.LP.formAction.mergerCell
		    }
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Table/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Table/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "table";

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
			if (styles.properties) this.removeStyles(styles.properties, "properties");
			if (styles.titleStyles) this.removeStyles(styles.titleStyles, "titleTdStyles");
			if (styles.contentStyles) this.removeStyles(styles.contentStyles, "contentTdStyles");
			if (styles.layoutStyles) this.removeStyles(styles.layoutStyles, "layoutTdStyles");
		}
	},

	setTemplateStyles: function(styles){
		if (styles.styles) this.copyStyles(styles.styles, "styles");
		if (styles.properties) this.copyStyles(styles.properties, "properties");
		if (styles.titleStyles) this.copyStyles(styles.titleStyles, "titleTdStyles");
		if (styles.contentStyles) this.copyStyles(styles.contentStyles, "contentTdStyles");
		if (styles.layoutStyles) this.copyStyles(styles.layoutStyles, "layoutTdStyles");
	},

	_createMoveNode: function(){
		var tableHTML = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\" width=\"100%\" align=\"center\">";
		tableHTML += "<tr><td></td><td></td><td></td></tr>";
		tableHTML += "<tr><td></td><td></td><td></td></tr>";
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
        //var tds = this._getTds(this.moveNode);
		tds.setStyles({
			"border": "1px dashed #999",
			"height": "20px"
		});
        var ths = this.moveNode.getElements("th");
        //var tds = this._getTds(this.moveNode);
        ths.setStyles({
            "border": "1px dashed #999",
            "height": "20px"
        });
	},
	_getTds: function(node){
		tds = [];
        var table = (node || this.node).getElement("table");
        var rows = table.rows;
        for (var i=0; i<rows.length; i++){
            var row = rows[i];
            for (var j=0; j<row.cells.length; j++){
                tds.push(row.cells[j]);
            }
        }
        return tds;
	},
	_checkSelectedTds: function(sp, ep){
		this.selectedMultiTds = [];
		//var tds = this.node.getElements("td");
		var tds = this._getTds();
//		var tmpSelectedTds = [];
//		var startXList = [];
//		var startYList = [];
//		var endXList = [];
//		var endYList = [];
		
		var sx = sp.x, sy = sp.y, ex = ep.x, ey = ep.y;
		
		while (true){
			var tmpsx = sp.x, tmpsy = sp.y, tmpex = ep.x, tmpey = ep.y;
			tds.each(function(td){
				if (td.isInPointInRect(sx, sy, ex, ey)){
					var position = td.getPosition();
					var size = td.getSize();
					if (!tmpsx || position.x<tmpsx) tmpsx = position.x;
					if (!tmpsy || position.y<tmpsy) tmpsy = position.y;
					
					if (!tmpex || position.x+size.x>tmpex) tmpex = position.x+size.x;
					if (!tmpey || position.y+size.y>tmpey) tmpey = position.y+size.y;
				}
			}.bind(this));
			if (sx==tmpsx && sy==tmpsy && ex==tmpex && ey==tmpey) break;
			
			sx = tmpsx, sy = tmpsy, ex = tmpex, ey = tmpey;
		}
		
		tds.each(function(td){
			var module = td.retrieve("module");
			if (td.isInPointInRect(sx, sy, ex, ey)){
				module.selectedMulti();
			}else{
				module.unSelectedMulti();
			}
		}.bind(this));
		
	},
	
	_setOtherNodeEvent: function(){
		this.dragInfor = {};
		this.tdDragSelect = new Drag(this.node, {
            "stopPropagation": true,
            "preventDefault": true,
			"onStart": function(el, e){
				this.form._beginSelectMulti();
				
				var position = e.event.target.getPosition();
				this.dragInfor.start = {"x": e.event.offsetX+position.x, "y": e.event.offsetY+position.y};
			}.bind(this),
			"onDrag": function(el, e){
				var position = e.event.target.getPosition();
				var p = {"x": e.event.offsetX+position.x, "y": e.event.offsetY+position.y};
				this._checkSelectedTds(this.dragInfor.start, p);
				
				e.stopPropagation();
			}.bind(this),
			"onComplete": function(el, e){
				var position = e.event.target.getPosition();
				var p = {"x": e.event.offsetX+position.x, "y": e.event.offsetY+position.y};
				this._checkSelectedTds(this.dragInfor.start, p);
				
				this.form._completeSelectMulti();
				this._createMultiSelectedActions();
                this.showMultiProperty();
				
				e.stopPropagation();
				e.preventDefault();
			}.bind(this)
		});
	},
    showMultiProperty: function(){
        if (this.form.propertyMultiTd){
            this.form.propertyMultiTd.hide();
            this.form.propertyMultiTd = null;
        }

        this.form.propertyMultiTd = new MWF.xApplication.process.FormDesigner.PropertyMulti(this.form, this.form.selectedModules, this.form.designer.propertyContentArea, this.form.designer, {
            "path": this.options.propertyMultiPath,
            "onPostLoad": function(){
                this.show();
            }
        });
        this.form.propertyMultiTd.load();
    },
	_createMultiSelectedActions: function(){
		if (this.form.selectedModules.length>1){
			if (this.form.multimoduleActionsArea){
				this.form.multimoduleActionsArea.empty();
				this.options.multiActions.each(function(action){
					var actionNode = new Element("div", {
						"styles": this.options.actionNodeStyles,
						"title": action.title
					}).inject(this.form.multimoduleActionsArea);
					actionNode.setStyle("background", "url("+this.path+this.options.style+"/icon/"+action.icon+") no-repeat left center");
					actionNode.addEvent(action.event, function(e){
						this[action.action](e);
					}.bind(this));
				}.bind(this));
				this.form.multimoduleActionsArea.setStyle("width", 18*this.options.multiActions.length);
			}
		}else{
			this.form.multimoduleActionsArea.setStyle("display", "none");
		}
	},
	mergerCell: function(){

		if (this.form.selectedModules.length>1){
			var firstModuleObj = this.form._getFirstMultiSelectedModule();
			var firstModule = firstModuleObj.module;
			
	//		var n=0;
			var td = firstModule.node;
			
			var colspan = 0;
			while (td && this.form.selectedModules.indexOf(td.retrieve("module"))!=-1 ){
				var tmpColspan = td.get("colspan").toInt() || 1;
				colspan = colspan+tmpColspan;
				td = td.getNext("td");
			}

			var maxRowIndex = Number.NEGATIVE_INFINITY;
			var minRowIndex = Number.POSITIVE_INFINITY;
			this.form.selectedModules.each(function(module, idx){
				var rIdx = module.node.getParent("tr").rowIndex;
				var tmpRowspan = module.node.get("rowspan").toInt() || 1;
				var rows = rIdx+tmpRowspan-1;
				
				maxRowIndex = Math.max(maxRowIndex, rows);
				minRowIndex = Math.min(minRowIndex, rows);
			}.bind(this));
			
			var rowspan = maxRowIndex-minRowIndex+1;
			
			if (colspan>1){
				firstModule.node.set("colspan", colspan);
				firstModule.json.properties.colspan = colspan;
			}else{
				firstModule.node.set("colspan", 1);
				delete firstModule.node.colspan;
				delete firstModule.json.properties.colspan;
			}
			if (rowspan>1){
				firstModule.node.set("rowspan", rowspan);
				firstModule.json.properties.rowspan = rowspan;
			}else{
				firstModule.node.set("rowspan", 1);
				delete firstModule.node.rowspan;
				delete firstModule.json.properties.rowspan;
			}
			
			while (this.form.selectedModules.length){
				var module = this.form.selectedModules[0];
				this.form.selectedModules.erase(module);
				if (module!==firstModule){
					var modules = module._getSubModule();
					modules.each(function(module){
						module._moveTo(firstModule);
					});
					
					this.containers.erase(module);
					module.destroy();
				}
			}
			firstModule.selected();
		}
	},
	
	_getContainers: function(){
		//var tds = this.node.getElements("td");
        var tds = this._getTds();
		this.form.getTemplateData("Table$Td", function(data){
			tds.each(function(td){
				var json = this.form.getDomjson(td);
				var tdContainer = null;
				if (!json){
					var moduleData = Object.clone(data);
					tdContainer = new MWF.FCTable$Td(this.form);
                    tdContainer.table = this;
					tdContainer.load(moduleData, td, this);
				}else{
					var moduleData = Object.clone(data);
					Object.merge(moduleData, json);
					Object.merge(json, moduleData);
					tdContainer = new MWF.FCTable$Td(this.form);
                    tdContainer.table = this;
					tdContainer.load(json, td, this);
				}
				this.containers.push(tdContainer);
			}.bind(this));
		}.bind(this));
	},
	_getElements: function(){
	//	this.elements.push(this);
        var captions = this.node.getElements("caption");
        captions.each(function(caption){
            var json = this.form.getDomjson(caption);
            var el = null;
            if (!json){
                this.form.getTemplateData("Common", function(data){
                    var moduleData = Object.clone(data);
                    el = new MWF.FCCommon(this.form);
                    el.table = this;
                    el.load(moduleData, caption, this);
                }.bind(this));
            }else{
                el = new MWF.FCCommon(this.form);
                el.table = this;
                el.load(json, caption, this);
			}
            this.elements.push(el);
        }.bind(this));
	},
	
	_createNode: function(callback){
		var module = this;
		var url = this.path+"tableCreate.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();			
			var x = size.x/2-180;
			var y = size.y/2-130;

			var dlg = new MWF.DL({
				"title": "Create Table",
				"style": "property",
				"top": y,
				"left": x-40,
				"fromTop":size.y/2-65,
				"fromLeft": size.x/2,
				"width": 360,
				"height": 260,
				"url": url,
				"lp": MWF.xApplication.process.FormDesigner.LP.propertyTemplate,
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
		var rows = $("MWFNewTableLine").get("value");
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
		for (var i=0; i<rows.toInt(); i++){
			tableHTML += "<tr>";
			for (var j=0; j<cols.toInt(); j++){
				tableHTML += "<td></td>";
			}
			tableHTML += "</tr>";
		}
		tableHTML += "</table>";

        this.node = this.node = new Element("div", {
			"id": this.json.id,
			"MWFType": "table",
			"html": tableHTML,
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(e){
					e.preventDefault();
				}
			}
		}).inject(this.form.node);
        //if (w!="100%"){
        //    this.node.setStyle("width", w);
        //}
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
	
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
            try{
                this.setCustomStyles();
            }catch(e){}
			// var border = this.node.getStyle("border");
			// this.node.clearStyles();
			// this.node.setStyles(this.css.moduleNode);
			// this.node.setStyle("border", border);
			// Object.each(this.json.styles, function(value, key){
			// 	var reg = /^border\w*/ig;
			// 	if (!key.test(reg)){
			// 		this.node.setStyle(key, value);
			// 	}
			// }.bind(this));
		}
		if (name=="properties"){
			this.node.getFirst().setProperties(this.json.properties);
			if (this.json.properties.cellspacing==="0"){
				this.node.getFirst().setProperties({"cellspacing": "1"});
			}

            //if (this.json.properties.width){
            //    if (this.json.properties.width!="100%"){
            //        this.node.setStyle("width", this.json.properties.width);
            //    }
            //}
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
        if (name=="titleTdStyles") this.setTabStyles();
        if (name=="contentTdStyles") this.setTabStyles();
        if (name=="layoutTdStyles") this.setTabStyles();
	},

    setTabStyles: function(){
        this.containers.each(function(module){
            //if (module.json.cellType=="title"){
            //
            //}
            //if (module.json.cellType=="content"){
            //
            //}
            //if (module.json.cellType=="layout"){
            //
            //}
            module.setCustomStyles();
        }.bind(this));
    },
    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("properties");

        this.setTabStyles();
        this.reloadMaplist();
    },

    copyComponentJsonData: function(newNode, pid){
        //var tds = newNode.getElements("td");
        var tds = this._getTds(newNode);

//        this.form.getTemplateData("Table$Td", function(data){
        tds.each(function(td, idx){
            var newContainerJson = Object.clone(this.containers[idx].json);
            newContainerJson.id = this.containers[idx]._getNewId(pid);
            this.form.json.moduleList[newContainerJson.id] = newContainerJson;
            td.set("id", newContainerJson.id);
        }.bind(this));
//        }.bind(this));
    },
//	setOtherNodeEvent: function(){
////		var tds = this.node.getElements("td");
////		tds.addEvent("click", function(e){
////			this.selectedTd(e.target);
////		}.bind(this));
//	},
//	
//	selectedTd: function(td){
//		if (this.currentSelectedTd){
//			if (this.currentSelectedTd==td){
//				return true;
//			}else{
//				this.unSelectedTd(this.currentSelectedTd);
//			}
//		} 
//		
//		var top = td.getStyle("border-top");
//		var left = td.getStyle("border-left");
//		var bottom = td.getStyle("border-bottom");
//		var right = td.getStyle("border-right");
//		td.store("thisborder", {"top": top, "left": left, "bottom": bottom, "right": right});
//
//		td.setStyles({
//			"border": "1px dashed #ff6b49"
//		});
//		this.currentSelectedTd = td;
//	},
//	unSelectedTd: function(td){
//		var border = td.retrieve("thisborder");
//		if (border) {
//			td.setStyles({
//				"border-top": border.top,
//				"border-left": border.left,
//				"border-bottom": border.bottom,
//				"border-right": border.right
//			});
//		}
//		this.currentSelectedTd = null;
//	},
	
	getContainerNodes: function(){
		//return this.node.getElements("td");
        return this._getTds();
	},
	_preprocessingModuleData: function(){
		this.node.clearStyles();
		this.json.recoveryStyles = Object.clone(this.json.styles);

		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				var reg = /^border\w*/ig;
				if (!key.test(reg)){
					if (key){
						this.node.setStyle(key, value);
						delete this.json.styles[key];
					}
				}
			}
		}.bind(this));

		if (this.json.styles && this.json.styles.border){
			if (!this.table) this.table = this.node.getElement("table");
			if( this.json.styles["table-layout"] ){
				this.table.setStyle("table-layout",this.json.styles["table-layout"]);
			}
			this.table.setStyle("border-collapse","collapse");
		}
		this.json.preprocessing = "y";
	},
	_recoveryModuleData: function(){
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
		if (!this.table) this.table = this.node.getElement("table");
		this.table.setStyle("border-collapse","separate");
	}
	
});
