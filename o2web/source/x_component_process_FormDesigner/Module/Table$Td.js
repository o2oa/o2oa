MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.process.FormDesigner.Module.Table$Td = MWF.FCTable$Td = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Table$Td/table$td.html",
		
		"actions": [
		    {
		    	"name": "insertRow",
		    	"icon": "insertRow1.png",
		    	"event": "click",
		    	"action": "insertRow",
		    	"title": MWF.APPFD.LP.formAction.insertRow
		    },
		    {
		    	"name": "insertCol",
		    	"icon": "insertCol1.png",
		    	"event": "click",
		    	"action": "insertCol",
		    	"title": MWF.APPFD.LP.formAction.insertCol
		    },
		    {
		    	"name": "deleteRow",
		    	"icon": "deleteRow1.png",
		    	"event": "click",
		    	"action": "deleteRow",
		    	"title": MWF.APPFD.LP.formAction.deleteRow
		    },
		    {
		    	"name": "deleteCol",
		    	"icon": "deleteCol1.png",
		    	"event": "click",
		    	"action": "deleteCol",
		    	"title": MWF.APPFD.LP.formAction.deleteCol
		    },
		    {
		    	"name": "splitCell",
		    	"icon": "splitCell.png",
		    	"event": "click",
		    	"action": "splitCell",
		    	"title": MWF.APPFD.LP.formAction.splitCell
		    }
		],
		"injectActions" : [
			{
				"name" : "top",
				"styles" : "injectActionTop",
				"event" : "click",
				"action" : "injectTop",
				"title": MWF.APPFD.LP.formAction["insertTop"]
			},
			{
				"name" : "bottom",
				"styles" : "injectActionBottom",
				"event" : "click",
				"action" : "injectBottom",
				"title": MWF.APPFD.LP.formAction["insertBottom"]
			}
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Table$Td/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Table$Td/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "table$Td";
		
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
	
	_showActions: function(){
		if (this.actionArea){
			this._setActionAreaPosition();
			this.actionArea.setStyle("display", "block");

			var colspan = this.node.get("colspan").toInt() || 1;
			var rowspan = this.node.get("rowspan").toInt() || 1;
			if (colspan<=1 && rowspan<=1){
				this.actionArea.getLast("div").setStyle("display", "none");
			}else{
                this.actionArea.getLast("div").setStyle("display", "block");
            }
		}
	},
	
	unSelectedMulti: function(){
		if (this.form.selectedModules.indexOf(this)!=-1){
			this.form.selectedModules.erase(this);
			this.node.setStyle("border-color", "#999");
		}
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
			"MWFType": "table$Td",
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

        this.json.moduleName = this.moduleName;
	},
	
//	_setNodeEvent: function(){
//		if (!this.isSetEvents){
//			this.node.addEvent("click", function(e){
//				this.selected();
//				e.stop();
//			}.bind(this));
//			
//			this.node.addEvent("mouseover", function(e){
//				this.over();
//				e.stop();
//			}.bind(this));
//			this.node.addEvent("mouseout", function(e){
//				this.unOver();
//				e.stop();
//			}.bind(this));
//			
//			this._setOtherNodeEvent();cellType
//	},
	
	_createMoveNode: function(){
		return false;
	},
	_setEditStyle_custom: function(name){
        if (name=="cellType"){
            this.setCustomStyles();

        }
		
	},
    setCustomStyles: function(){
        var border = this.node.getStyle("border");
        this.node.clearStyles();
        this.node.setStyles(this.css.moduleNode);

        var addStyles = {};
        if (this.json.cellType=="title"){
            addStyles = this.table.json.titleTdStyles
        }
        if (this.json.cellType=="content"){
            addStyles = this.table.json.contentTdStyles
        }
        if (this.json.cellType=="layout"){
            addStyles = this.table.json.layoutTdStyles
        }

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        Object.each(addStyles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!==-1 || value.indexOf("x_portal_assemble_surface")!==-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
            }

            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
        }.bind(this));

        Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!==-1 || value.indexOf("x_portal_assemble_surface")!==-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
            }
            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
        }.bind(this));
    },

	_dragInLikeElement: function(module){
		return false;
	},
	
	insertRow: function(){
		var module = this;
		var url = this.path+"insertRow.html";
		MWF.require("MWF.widget.Dialog", function(){
			var size = $(document.body).getSize();			
			var x = size.x/2-150;
			var y = size.y/2-90;

			var dlg = new MWF.DL({
				"title": "Insert Row",
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
				    		module._insertRow();
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
	_insertRow: function(){
		var rows = $("MWFInsertRowNumber").get("value");
		var positionRadios = document.getElementsByName("MWFInsertRowPosition");
		var position = "before";
		for (var i=0; i<positionRadios.length; i++){
			if (positionRadios[i].checked){
				position = positionRadios[i].value;
				break;
			}
		}
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		
		
		var cellNumber = tr.cells.length;
		var rowIndex = tr.rowIndex;
		
		var rowspanBeforeTds = table.getElements("td:rowspanBefore("+rowIndex+")");
		var colCurrentTds = tr.getElements("td:colspan");
		colCurrentTds.each(function(td){
			var colspan = td.get("colspan").toInt() || 1;
			cellNumber = cellNumber+colspan-1;
		});	
		rowspanBeforeTds.each(function(td){
			this.__rowspanPlus(td, rows);
		}.bind(this));
		
		if (position=="after"){
			var rowspanCurrentTds = tr.getElements("td:rowspan");
			rowspanCurrentTds.each(function(td){
				this.__rowspanPlus(td, rows);
				var colspan = td.get("colspan").toInt() || 1;
				cellNumber = cellNumber-colspan;
			}.bind(this));
		}
		
		for (var n=1; n<=rows; n++){
			var newTr = new Element("tr").inject(tr, position);
			for (var m=1; m<=cellNumber; m++){
				var cell = new Element("td").inject(newTr);
				
				this.form.getTemplateData("Table$Td", function(data){
					var moduleData = Object.clone(data);
					var tdContainer = new MWF.FCTable$Td(this.form);
                    tdContainer.table = this.table;
					tdContainer.load(moduleData, cell, this.parentContainer);
					this.parentContainer.containers.push(tdContainer);
			
				}.bind(this));
				
			}
		}
		this.unSelected();
		this.selected();
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
		var colIndex = this.__getCellIndex(this.node);
		

		for (var m=1; m<=cols; m++){
			var insertTdObjs = this.__getInsertTableColTds(table, colIndex);
			insertTdObjs.each(function(obj){
				obj.td.inject(obj.toTd, position);
				
				this.form.getTemplateData("Table$Td", function(data){
					var moduleData = Object.clone(data);
					var tdContainer = new MWF.FCTable$Td(this.form);
                    tdContainer.table = this.table;
					tdContainer.load(moduleData, obj.td, this.parentContainer);
					this.parentContainer.containers.push(tdContainer);
				}.bind(this));
			}.bind(this));
		}
		
		this.unSelected();
		this.selected();
		
	},
	
	deleteRow: function(e){
		var module = this;
		this.form.designer.confirm("warn", e, MWF.APPFD.LP.notice.deleteRowTitle, MWF.APPFD.LP.notice.deleteRow, 300, 120, function(){
			module._deleteRow();
			this.close();
		}, function(){
			this.close();
		}, null);
	},
	
	__rowspanPlus: function(td,n){
		var rowspan = td.get("rowspan").toInt() || 1;
		rowspan = rowspan+n.toInt();
		var module = td.retrieve("module");
		if (rowspan>1){
			td.set("rowspan", rowspan);
			if (module) module.json.properties.rowspan = rowspan;
		}else{
			td.set("rowspan", 1);
			delete td.rowspan;
			if (module) delete module.json.properties.rowspan;
		}
		
	},
	__rowspanMinus: function(td){
		var rowspan = td.get("rowspan").toInt() || 1;
		rowspan = rowspan-1;
		var module = td.retrieve("module");
		if (rowspan>1){
			td.set("rowspan", rowspan);
			if (module) module.json.properties.rowspan = rowspan;
		}else{
			td.set("rowspan", 1);
			delete td.rowspan;
			if (module) delete module.json.properties.rowspan;
		}
	},
	__colspanPlus: function(td, n){
		var colspan = td.get("colspan").toInt() || 1;
		colspan = colspan+n.toInt();
		var module = td.retrieve("module");
		if (colspan>1){
			td.set("colspan", colspan);
			if (module) module.json.properties.colspan = colspan;
		}else{
			td.set("colspan", 1);
			delete td.colspan;
			if (module) delete module.json.properties.colspan;
		}
	},
	__colspanMinus: function(td){
		var colspan = td.get("colspan").toInt() || 1;
		colspan = colspan-1;
		var module = td.retrieve("module");
		if (colspan>1){
			td.set("colspan", colspan);
			if (module) module.json.properties.colspan = colspan;
		}else{
			td.set("colspan", 1);
			delete td.colspan;
			if (module) delete module.json.properties.colspan;
		}
	},
	
	__getNextTd: function(nextTr, cellIndex){
		var nextTd = null;
		while (cellIndex>0){
			if (!nextTd){
				nextTd = nextTr.getFirst("td");
			}else{
				nextTd = nextTd.getNext("td");
			}
			cellIndex--;
			var colspan = nextTd.get("colspan").toInt() || 1;
			cellIndex = cellIndex-colspan-1;
		}
		return nextTd;
	},
	__getCellIndex: function(td){
		var tr = td.getParent("tr");
		var table = tr.getParent("table");
	//	var rowindex = tr.rowIndex;
		
		var idx = -1;
		var trs = table.rows;
		var rowspanTds = {};
		
		var isBreak = false;
		for (var i=0; i<trs.length; i++){
			var ervryTd = null;
			var j=0;
			while (true){
				var rowspan = rowspanTds["rowspan_"+j];
				if (rowspan){
					rowspan.rows = rowspan.rows-1;
					if (!rowspan.rows){
						delete rowspanTds["rowspan_"+j];
					}
					j++;
				}else{
					if (!ervryTd){
						ervryTd = trs[i].getFirst("td");
					}else{
						ervryTd = ervryTd.getNext("td");
					}
					if (!ervryTd) break;
					
					if (ervryTd==td){
						idx = j;
						isBreak = true;
						break;
					}else{
						var rowspan = ervryTd.get("rowspan").toInt() || 1;
						var colspan = ervryTd.get("colspan").toInt() || 1;
						if (rowspan>1){
							var rows = rowspan-1;
							for (var x=0; x<colspan; x++){
								var n = j+x;
								rowspanTds["rowspan_"+n] = {"rows": rows};
							}
						}
						j = j + colspan-1;
					}
					j++;
				}
			}
			if (isBreak) break;
		}
			
		return idx;
	},
	__getInsertTableColTds: function(table, idx){
		var insertTds = [];
		var trs = table.rows;
		var rowspanTds = {};
		
		for (var i=0; i<trs.length; i++){
			var ervryTd = null;
			var j=0;
			while (true){
				var rowspan = rowspanTds["rowspan_"+j];
				if (rowspan){
					rowspan.rows = rowspan.rows-1;
					if (!rowspan.rows){
						delete rowspanTds["rowspan_"+j];
					}
					j++;
				}else{
					if (!ervryTd){
						ervryTd = trs[i].getFirst("td");
					}else{
						ervryTd = ervryTd.getNext("td");
					}
					if (!ervryTd) break;
					
					var rowspan = ervryTd.get("rowspan").toInt() || 1;
					var colspan = ervryTd.get("colspan").toInt() || 1;
					
					if (rowspan>1){
						var rows = rowspan-1;
						for (var x=0; x<colspan; x++){
							var n = j+x;
							rowspanTds["rowspan_"+n] = {"rows": rows};
						}
					}
					
					if (colspan>1){
						if (j + colspan-1>=idx && j<=idx){
						//	ervryTd.setStyle("background", "#FF9999");
							this.__colspanPlus(ervryTd, 1);
							break;
						}
						
					}else{
						if (j==idx){
							var newTd = new Element("td");
							insertTds.push({"td": newTd, "toTd": ervryTd});
							break;
						}
					}
					
					j = j + colspan-1;
					j++;
				}
			}
		}
			
		return insertTds;
		
	},
	__getDeleteTableColTds: function(table, idx){
		var deleteTds = [];
		var trs = table.rows;
		var rowspanTds = {};
		
		for (var i=0; i<trs.length; i++){
			var ervryTd = null;
			var j=0;
			while (true){
				var rowspan = rowspanTds["rowspan_"+j];
				if (rowspan){
					rowspan.rows = rowspan.rows-1;
					if (!rowspan.rows){
						delete rowspanTds["rowspan_"+j];
					}
					j++;
				}else{
					if (!ervryTd){
						ervryTd = trs[i].getFirst("td");
					}else{
						ervryTd = ervryTd.getNext("td");
					}
					if (!ervryTd) break;
					
					var rowspan = ervryTd.get("rowspan").toInt() || 1;
					var colspan = ervryTd.get("colspan").toInt() || 1;
					
					if (rowspan>1){
						var rows = rowspan-1;
						for (var x=0; x<colspan; x++){
							var n = j+x;
							rowspanTds["rowspan_"+n] = {"rows": rows};
						}
					}
					
					if (colspan>1){
						if (j + colspan-1>=idx && j<=idx){
						//	ervryTd.setStyle("background", "#FF9999");
							this.__colspanMinus(ervryTd);
							break;
						}
					}else{
						if (j==idx){
							deleteTds.push(ervryTd);
							break;
						}
					}
					
					j = j + colspan-1;
					j++;
				}
			}
		}
			
		return deleteTds;
	},
	
	_deleteRow:function(){
		var _form = this.form;
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		var rowIndex = tr.rowIndex;
//		var currentRowspan = this.node.get("rowspan").toInt() || 1;
		
		var rowspanBeforeTds = table.getElements("td:rowspanBefore("+rowIndex+")");
		var rowspanCurrentTds = tr.getElements("td:rowspan");
		
		rowspanBeforeTds.each(function(td){
			this.__rowspanMinus(td);
		}.bind(this));
		
		rowspanCurrentTds.each(function(td){
			this.__rowspanMinus(td);
			var nextTr = table.rows[rowIndex+1];
			if (nextTr){
				var cellIndex = td.cellIndex;
				var nextTd = null;
				if (cellIndex>0){
					nextTd = this.__getNextTd(nextTr, cellIndex);
				}else{
					nextTd = this.__getNextTd(nextTr, 2);
				}
				if (nextTd) td.inject(nextTd, "after");
			}
		}.bind(this));

		if (table.rows.length<=1){
			this.parentContainer.destroy();
		}else{
			tds = tr.getElements("td");
			tds.each(function(td){
				var module = td.retrieve("module");
				if (module){
					module.parentContainer.containers.erase(module);
					module.destroy();
				}
			});
			tr.destroy();
		}
        _form.currentSelectedModule = null;
        _form.selected();
        _form = null;
	},
	deleteCol: function(e){
		var module = this;
		this.form.designer.confirm("warn", e, MWF.APPFD.LP.notice.deleteColTitle, MWF.APPFD.LP.notice.deleteCol, 300, 120, function(){
			module._deleteCol();
			this.close();
		}, function(){
			this.close();
		}, null);
	},
	_deleteCol: function(){
		var _form = this.form;
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		var colIndex = this.__getCellIndex(this.node);
		var currentRowspan = this.node.get("colspan").toInt() || 1;
		
		if (tr.cells.length<=1 && currentRowspan<=1){
			this.parentContainer.destroy();
		}else{
			var deleteTds = this.__getDeleteTableColTds(table, colIndex);
			deleteTds.each(function(dtd){
			//	dtd.setStyle("background", "#999999");
				var module = dtd.retrieve("module");
				if (module){
					module.parentContainer.containers.erase(module);
					module.destroy();
				}
			});
		}
		_form.currentSelectedModule = null;
		_form.selected();
		_form = null;
	},

//	__getTdByIndex11: function(tr, idx){
//		//??????????????????????????????????????
//		//??????????????????????????????????????
//		//??????????????????????????????????????
//		//??????????????????????????????????????
//		//??????????????????????????????????????
//		var findTd = false;
//		var td = null;
//		var n=-1;
//		while (n!=idx){
//			if (!td){
//				td = tr.getFirst("td");
//			}else{
//				td = tr.getNext("td");
//			}
//			var colspan = td.get("colspan").toInt() || 1;
//			n = n+colspan;
//			if (n>=idx && n-colspan<=idx) n=idx;
//		}
//		return td;	
//	},
	
	__getTdsByIndex: function(table, beginRow, rows, idx){
		var indexTds = [];
		var trs = table.rows;
		var rowspanTds = {};
		
		for (var i=0; i<trs.length; i++){
			var ervryTd = null;
			var j=0;
			var findTd=false;
			while (true){
				var rowspan = rowspanTds["rowspan_"+j];
				if (rowspan){
					rowspan.rows = rowspan.rows-1;
					if (!rowspan.rows){
						delete rowspanTds["rowspan_"+j];
					}
					j++;
				}else{
					if (!ervryTd){
						ervryTd = trs[i].getFirst("td");
					}else{
						ervryTd = ervryTd.getNext("td");
					}
					if (!ervryTd){
						if (i>=beginRow && i<=beginRow+rows) if (!findTd) indexTds.push(null);
						break;
					}
					
					var rowspan = ervryTd.get("rowspan").toInt() || 1;
					var colspan = ervryTd.get("colspan").toInt() || 1;
					
					var rows;
					if (rowspan>1){
						rows = rowspan-1;
						for (var x=0; x<colspan; x++){
							var n = j+x;
							rowspanTds["rowspan_"+n] = {"rows": rows};
						}
					}

					if (j + colspan-1>=idx && j<=idx){
						if (i>=beginRow && i<=beginRow+rows){
							indexTds.push(ervryTd);
							findTd = true;
						} 
						break;
					}
	
					j = j + colspan-1;
					j++;
				}
			}
		}
			
		return indexTds;
	},
	
	splitCell: function(){

		var colspan = this.node.get("colspan").toInt() || 1;
		var rowspan = this.node.get("rowspan").toInt() || 1;
		
		var tr = this.node.getParent("tr");
		var table = tr.getParent("table");
		var rowIndex = tr.rowIndex;
		
		var colIndex = this.__getCellIndex(this.node);

		this.node.set("rowspan", 1);
		delete this.node.rowspan;
		delete this.json.properties.rowspan;
		this.node.set("colspan", 1);
		delete this.node.colspan;
		delete this.json.properties.colspan;
		
		if (this.form.currentSelectedModule) this.form.currentSelectedModule.unSelected();
		this.unSelectedMulti();
		
		this.selectedMulti();
		
		var startTds = this.__getTdsByIndex(table, rowIndex+1, rowspan-1, colIndex-1);
		
		for (var i=1; i<=rowspan; i++){
			if (i==1){
				for (var j=2; j<=colspan; j++){
					var newTd = new Element("td").inject(this.node, "after");
					
					this.form.getTemplateData("Table$Td", function(data){
						var moduleData = Object.clone(data);
						var tdContainer = new MWF.FCTable$Td(this.form);
                        tdContainer.table = this.table;
						tdContainer.load(moduleData, newTd, this.parentContainer);
						this.parentContainer.containers.push(tdContainer);
						tdContainer.selectedMulti();
					}.bind(this));
				}
			}else{
				var tr = tr.getNext("tr");
				var startTd = startTds[i-2];
				
				for (var j=1; j<=colspan; j++){
					var newTd = new Element("td");
					if (startTd){
						newTd.inject(startTd, "after");
					}else{
						newTd.inject(tr, "top");
					}
					
					this.form.getTemplateData("Table$Td", function(data){
						var moduleData = Object.clone(data);
						var tdContainer = new MWF.FCTable$Td(this.form);
                        tdContainer.table = this.table;
						tdContainer.load(moduleData, newTd, this.parentContainer);
						this.parentContainer.containers.push(tdContainer);
						tdContainer.selectedMulti();
					}.bind(this));
				}
			}
		}
		this.form._completeSelectMulti();
	}

	//_showInjectAction : function( module ){
	//	if ( module.moveNode ){
	//		module.moveNode.setStyle("display","none");
	//	}
    //
	//	this.draggingModule = module;
	//	if( !this.node.getFirst() ){
	//		this.inject( "top" );
	//		return;
	//	}
    //
	//	if( !this.injectActionArea )this._createInjectAction();
	//	this.injectActionArea.setStyle("display","");
	//	this._setInjectActionAreaPosition();
	//}
	
});
