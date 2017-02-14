MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Datagrid = MWF.CMSDatagrid =  new Class({
	Implements: [Events],
	Extends: MWF.CMS$Module,
	isEdit: false,

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },

	_loadUserInterface: function(){
		this.editModules = [];
		this.table = this.node.getElement("table");

        this.editable = (this.readonly) ? false : true;
        if (this.editable) this.editable = this.form.CMSMacro.exec(this.json.editableScript.code, this);

		this.gridData = this._getValue();

        this.totalModules = [];
		this._loadDatagridTitleModules();

		if (this.editable!=false){
            this._loadDatagridDataModules();
            this._addTitleActionColumn();
			this._loadEditDatagrid();
			//this._loadReadDatagrid();
		}else{
			this._loadReadDatagrid();
		}
	},
	_loadStyles: function(){
		this.table.setStyles(this.json.styles);
	},
	_getValue: function(){
		var value = [];
		value = this._getBusinessData();
		if (!value){
			if (this.json.defaultData.code) value = this.form.CMSMacro.exec(this.json.defaultData.code, this);
		}
		return value || [];
	},
	_getDatagridTr: function(){
		this._getDatagridTitleTr();
		this._getDatagridEditorTr();
	},
	_getDatagridTitleTr: function(){
		this.titleTr = this.table.getElement("tr");
		return this.titleTr;
	},
	_getDatagridEditorTr: function(){
		var trs = this.table.getElements("tr");
		this.editorTr = trs[trs.length-1];
        this.editorTr.addClass("datagridEditorTr");

		return this.editorTr;
	},
	_addTitleActionColumn: function(){
		if (!this.titleTr) this._getDatagridTitleTr();
		if (!this.editorTr) this._getDatagridEditorTr();
		
		var actionTh = new Element("th", {"styles": {"width": "46px"}}).inject(this.titleTr, "top");
		new Element("th").inject(this.titleTr, "bottom");
		this._createAddLineAction(actionTh);
		//this._createDelLineAction(actionTh);
		
		var actionEditTd = new Element("td").inject(this.editorTr, "top");
		this._createCompleteAction(actionEditTd);
		
		new Element("td").inject(this.editorTr, "bottom");

        //if (this.totalTr){
        //    new Element("td").inject(this.totalTr, "top");
        //    new Element("td").inject(this.totalTr, "bottom");
        //    this.totalModules.each(function(m){
        //        m.index = m.index+1;
        //    });
        //}
	},
	
	_loadEditDatagrid: function(){
        var titleThs = this.titleTr.getElements("th");
        var editorTds = this.editorTr.getElements("td");

        this.gridData.each(function(data, idx){
            var tr = this.table.insertRow(idx+1);
            tr.store("data", data);
            titleThs.each(function(th, index){
                var cellData = data[th.get("id")];
                var text = "";
                for (key in cellData){
                    var value = cellData[key];
                    text = this._getValueText(index-1, value);
                    break;
                }
                this._createNewEditTd(tr, index, editorTds[index].get("id"), text, titleThs.length-1);
            }.bind(this));
        }.bind(this));
		
		this.editorTr.setStyle("display", "none");
	},
	
	_getValueText: function(idx, value){
		var module = this.editModules[idx];
		if (module){
			switch (module.json.type){
			case "Select":
				var ops = module.node.getElements("option");
				for (var i=0; i<ops.length; i++){
					if (ops[i].value == value){
						return ops[i].get("text");
						break;
					}
				}
				break;
			case "Radio":
				var ops = module.node.getElements("input");
				for (var i=0; i<ops.length; i++){
					if (ops[i].value == value){
						return ops[i].get("showText");
						break;
					} 
				}
				break;
			case "Checkbox":
				var ops = module.node.getElements("input");
				var texts = [];
				for (var i=0; i<ops.length; i++){
					if (value.indexOf(ops[i].value) != -1) texts.push(ops[i].get("showText"));
				}
				if (texts.length) return texts.join(", ");
				break;
			}
		}
		return value;
	},
	
	_createNewEditTd: function(tr, idx, id, text, lastIdx){
		var cell = tr.insertCell(idx);
		if (idx==0){
			cell.setStyles(this.form.css.gridLineActionCell);
			this._createAddLineAction(cell);
			this._createDelLineAction(cell);
		}else if (idx == lastIdx){
			cell.setStyles(this.form.css.gridMoveActionCell);
			this._createMoveLineAction(cell);
		}else{
			cell.set("MWFId", id);
			cell.set("text", text);
			cell.addEvent("click", function(e){
				this._editLine(e.target);
			}.bind(this));
		};
		var json = this.form._getDomjson(cell);

		if (json){
            cell.store("dataGrid", this);
			var module = this.form._loadModule(json, cell);
			cell.store("module", module);
			this.form.modules.push(module);
		}
		
	},
	
	_createAddLineAction: function(td){
		var addLineAction = new Element("div", {
			"styles": this.form.css.addLineAction, 
			"events": {
				"click": function(e){
					this._addLine(e.target);
				}.bind(this)
			}
		});
		addLineAction.inject(td);
	},
	_createDelLineAction: function(td){
		var delLineAction = new Element("div", {
			"styles": this.form.css.delLineAction, 
			"events": {
				"click": function(e){
					this._deleteLine(e);
				}.bind(this)
			}
		});
		delLineAction.inject(td);
	},
	_createCompleteAction: function(td){
		var completeAction = new Element("div", {
			"styles": this.form.css.completeLineAction, 
			"events": {
				"click": function(e){
					this._completeLineEdit(e);
				}.bind(this)
			}
		});
		completeAction.inject(td);
	},
	
	_editLine:function(td){
		if (this.isEdit){
			this._completeLineEdit();
		};
		
		this.currentEditLine = td.getParent("tr");
		if (this.currentEditLine){
			this.editorTr.setStyles({
				"background-color": "#fffeb5",
				"display": "table-row"
			});
			this.editorTr.inject(this.currentEditLine, "before");
			this.currentEditLine.setStyle("display", "none");
			
			var data = this.currentEditLine.retrieve("data");
			var titleThs = this.titleTr.getElements("th");
			titleThs.each(function(th, idx){
				var id = th.get("id");
				var module = this.editModules[idx-1];
				if (module){
                    if (module.json.type=="sequence"){
                        module.node.set("text", module.node.getParent("tr").rowIndex);

                        //var i = newTr.rowIndex;
                        //var data = {"value": [i], "text": [i]};
                    }else {
                        if (data[id]) {
                            module.setData(data[id][module.json.id]);
                        } else {
                            module.setData(null);
                        }
                    }
				}
			}.bind(this));
			
			var cellIdx = this.currentEditLine.getElements("td").indexOf(td);
			var module = this.editModules[cellIdx-1];
			if (module) module.focus();
			
			this.isEdit =true;
		}
        this.validationMode();
	},
    editValidation: function(){
        var flag = true;
        this.editModules.each(function(field, key){
            if (field.json.type!="sequence"){
                field.validationMode();
                if (!field.validation()) flag = false;
            }
        }.bind(this));
        return flag;
    },

	_completeLineEdit: function(){
		//this.currentEditLine.getElemets(td);
        if (!this.editValidation()){
            return false;
        }

		this.isEdit = false;
		
		var flag = true;
		
		var griddata = {};
		var newTr = null;

		if (this.currentEditLine){
			newTr = this.currentEditLine;
			griddata = this.currentEditLine.retrieve("data");
		}else{
			newTr = new Element("tr").inject(this.editorTr, "before");
			griddata = {};
		}
			

		var titleThs = this.titleTr.getElements("th");
		var editorTds = this.editorTr.getElements("td");
		var cells = newTr.getElements("td");
		
		titleThs.each(function(th, idx){
			var cell = cells[idx];
			var id = th.get("id");
			var module = this.editModules[idx-1];
			if (module){
                if (module.json.type=="sequence"){
                    var i = newTr.rowIndex;
                    var data = {"value": [i], "text": [i]};
                }else{
                    var data = module.getTextData();
                    if (data.value[0]) flag = false;
                    if (data.value.length<2){
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value[0];
                    }else{
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value;
                    }
                }

				if (cell){
					cell.set("text", data.text.join(", "));
				}else{
					this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), data.text.join(", "), titleThs.length-1);
				}
			}else{
				if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
			}
		}.bind(this));
		
		newTr.store("data", griddata);
		newTr.setStyle("display", "table-row");
		
		if (flag){
			newTr.destroy();
		}
		this.currentEditLine = null;
		
		this._editorTrGoBack();

        this._loadTotal();

		this._loadBorderStyle();
		this._loadZebraStyle();
        this._loadSequence();

			
		this.getData();
        this.validationMode();
	},
	_editorTrGoBack: function(){
		this.editorTr.setStyle("display", "none");
//		this.editTr.removeEvents("blur");
		var lastTrs = this.table.getElements("tr");
		var lastTr = lastTrs[lastTrs.length-1];
		this.editorTr.inject(lastTr, "after");
	},
	_addLine: function(node){
		if (this.isEdit){
			this._completeLineEdit();
		};
		this.editorTr.setStyles({
			"background-color": "#fffeb5",
			"display": "table-row"
		});
		this.currentEditLine = null; 
		var currentTr = node.getParent("tr");
		if (currentTr){
			this.editorTr.inject(currentTr, "after");
		};
		this.isEdit =true;
        this.validationMode();
        this.fireEvent("addLine");

//		newTr.addEvent("blur", function(e){
//			this._completeLineEdit();
//		}.bind(this));
	},
	_deleteLine: function(e){
		var currentTr = e.target.getParent("tr");
		if (currentTr){
			var color = currentTr.getStyle("background-color");
			currentTr.store("bgcolor", color);
			currentTr.tween("background-color", "#ffd4d4");
			var datagrid = this;
			
			this.form.confirm("warn", e, MWF.xApplication.cms.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.cms.Xform.LP.deleteDatagridLine, 300, 120, function(){
                this.fireEvent("deleteLine", [currentTr]);

                currentTr.destroy();
				datagrid._loadZebraStyle();
                datagrid._loadSequence();
                datagrid._loadTotal();
				datagrid.getData();
				this.close();
			}, function(){
				var color = currentTr.retrieve("bgcolor");
				currentTr.tween("background-color", color);
				this.close();
			}, null);
		};
        this.validationMode();
	},
	_createMoveLineAction: function(td){
		var moveLineAction = new Element("div", {
			"styles": this.form.css.moveLineAction,
			"events": {
				"mousedown": function(e){
					this._moveLine(e);
				}.bind(this)
			}
		});
		moveLineAction.inject(td);
	},
	_getMoveDragNode: function(tr){
		var table = tr.getParent("table");
		var div = table.getParent("div");
		var size = div.getSize();
		var dragNode = div.clone().setStyle("width", size.x).inject(document.body);
		var dragtable = dragNode.getElement("table");
		dragtable.empty();
		
		var clone = tr.clone().setStyles(this.form.css.gridMoveLineDragNodeTr).inject(dragtable);
		var tds = tr.getElements("td");
		var clonetds = clone.getElements("td");
		tds.each(function(td, idx){
			var size = td.getComputedSize();
			clonetds[idx].setStyle("width", size.width+1);
		});
		
		var coordinates = tr.getCoordinates();
		dragNode.setStyles(this.form.css.gridMoveLineDragNode);
		dragNode.setStyles(coordinates);
		
		return dragNode;
	},
	_moveLine: function(e){
		var trs = this.table.getElements("tr");
		var div = e.target;
		var tr = div.getParent("tr");
		
		var dragNode = this._getMoveDragNode(tr);
		coordinates = dragNode.getCoordinates();
		var dragTr = dragNode.getElement("tr");
		var dragTable = dragNode.getElement("table");
		
		var color = tr.getStyle("background-color");
		tr.store("bgcolor", color);
		//tr.tween('background-color', '#f3f1ad');
        tr.tween('background-color', '#e4f6e9');


		var drag = new Drag.Move(dragNode, {
			"droppables": trs.erase(tr),
			"limit": {"x": [coordinates.left, coordinates.left]},
			onDrop: function(dragging, droppable){
				dragging.destroy();
		        if (droppable != null){
		        	tr.inject(dragTr, "after");
		        	tr.setStyle("display", "table-row");
		        	dragTr.destroy();
		        	this._loadZebraStyle();
                    this._loadSequence();
                    this._loadTotal();
		        	this.getData();
		        }else{
		        	var color = tr.retrieve("bgcolor");
		        	if (color){
		        		tr.tween("background-color", color);
					}else{
						tr.tween("background-color", "#FFF");
					}
		        	tr.setStyle("display", "table-row");
		        }
			}.bind(this),
			"onEnter": function(dragging, drop){
				var color = drop.getStyle("background-color");
				if (color.toUpperCase()!='#d1eaf3') drop.store("bgcolor", color);
				drop.tween("background-color", "#d1eaf3");
				dragNode.setStyle("display", "none");
				dragTr.inject(drop, "after");
			},
			"onLeave": function(dragging, drop){
				var color = drop.retrieve("bgcolor");
				if (color){
					drop.tween("background-color", color);
				}else{
					drop.tween("background-color", "#FFF");
				}
				dragTr.inject(dragTable);
				dragNode.setStyle("display", "block");
//				tr.setStyle("display", "table-row");
			},
			"ondrag": function(){
				this.table.setStyle("cursor", "move");
				dragNode.setStyle("cursor", "move");
			},
			"onCancel": function(dragging){
                dragging.destroy();
                var color = tr.retrieve("bgcolor");
                if (color){
                    tr.tween("background-color", color);
                }else{
                    tr.tween("background-color", "#FFF");
                }
                tr.setStyle("display", "table-row");
			}
		});
		drag.start(e);
		tr.setStyle("display", "none");
	},
	_loadReadDatagrid: function(){

		this.gridData = this._getValue();
		
		if (!this.titleTr) this._getDatagridTitleTr();
		//var titleTr = this.table.getElement("tr");
		var titleHeaders = this.titleTr.getElements("th");
		
		var lastTrs = this.table.getElements("tr");
		var lastTr = lastTrs[lastTrs.length-1];
		var tds = lastTr.getElements("td");
		
		this.gridData.each(function(data, idx){
			var tr = this.table.insertRow(idx+1);
            tr.store("data", data);

			titleHeaders.each(function(th, index){
				var cell = tr.insertCell(index);
				cell.set("MWFId", tds[index].get("id"));
				var cellData = data[th.get("id")];
                if (cellData){
                    for (key in cellData){
                        cell.set("text", cellData[key]);
                        break;
                    }
                }else{ //Sequence
                    cell.setStyle("text-align", "center");
                    cell.set("text", tr.rowIndex);
                }

			}.bind(this));
		}.bind(this));

        lastTr.destroy();

        this._loadTotal();
     //   this._loadSequenceRead();
		

	},
	
	_loadDatagridStyle: function(){
		var ths = this.titleTr.getElements("th");
		ths.setStyles(this.form.css.datagridTitle);

        this._loadTotal();
		this._loadBorderStyle();
		this._loadZebraStyle();
        this._loadSequence();

	},
	
	_loadZebraStyle: function(){
		var trs = this.table.getElements("tr");
		for (var i=1; i<trs.length; i++){
            if (!trs[i].hasClass("datagridTotalTr")){
                if (this.json.backgroundColor) trs[i].setStyle("background-color", this.json.backgroundColor);
                if ((i%2)==0){
                    if (this.json.zebraColor) trs[i].setStyle("background-color", this.json.zebraColor);
                }
            }
		}
	},
    createTotalTr: function(){
        var trs = this.node.getElements("tr");
        var lastTr = trs[trs.length-1];
        this.totalTr = new Element("tr.datagridTotalTr", {"styles": this.form.css.datagridTotalTr}).inject(lastTr, "after");
        var ths = this.node.getElements("th");
        ths.each(function(th){
            new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
        }.bind(this));
    },
    _loadTotal: function(){
        if (this.totalModules.length){
            if (!this.totalTr){
                this.createTotalTr();
            }

            var totalResaults = [];
            //this.totalModules.each(function(m. i){
            //    totalResaults.push(0);
            //}.bind(this));

            var trs = this.table.getElements("tr");
            var totalTds = this.totalTr.getElements("td");

            for (var i=1; i<trs.length; i++){
                if (!trs[i].hasClass("datagridTotalTr") && (!trs[i].hasClass("datagridEditorTr"))){
                    var cells = trs[i].getElements("td");

                    this.totalModules.each(function(m, i){
                        if (!totalResaults[i]) totalResaults.push(0);
                        var tmpV = totalResaults[i];
                        if (m.type=="number"){
                            var cell = cells[m.index];
                            var addv = cell.get("text").toFloat();
                            tmpV = tmpV + addv;
                        }
                        if (m.type=="count"){
                            tmpV = tmpV+1;
                        }
                        totalResaults[i] = tmpV;
                    }.bind(this));
                }
            }

            this.totalModules.each(function(m, i){
                var td = totalTds[m.index];
                td.set("text", totalResaults[i]);
            }.bind(this));
        }
    },
    _loadSequence: function(){
        var trs = this.table.getElements("tr");
        for (var i=1; i<trs.length; i++){
            var cells = trs[i].getElements("td");
            cells.each(function(cell){
                var module = cell.retrieve("module");
                if (module){
                    if (module.json.cellType=="sequence"){
                        cell.set("text", i)
                    }
                }
            }.bind(this));
        }
    },
	
	_loadBorderStyle: function(){
		if (this.json.border){
			this.table.setStyles({
				"border-top": this.json.border,
				"border-left": this.json.border
			});
			var ths = this.table.getElements("th");
			ths.setStyles({
				"border-bottom": this.json.border,
				"border-right": this.json.border
			});
			var tds = this.table.getElements("td");
			tds.setStyles({
				"border-bottom": this.json.border,
				"border-right": this.json.border,
				"background": "transparent"
			});
		}
	},
	_loadDatagridTitleModules: function(){
		var ths = this.node.getElements("th");
		ths.each(function(th){
            var json = this.form._getDomjson(th);
            th.store("dataGrid", this);
            if (json){
                var module = this.form._loadModule(json, th);
                this.form.modules.push(module);
            }
        }.bind(this));
	},
	_loadDatagridDataModules: function(){
		var tds = this.node.getElements("td");
		tds.each(function(td){
			var json = this.form._getDomjson(td);
			td.store("dataGrid", this);
			if (json){
				var module = this.form._loadModule(json, td, function(){
                    this.field = false;
                });
				td.store("module", module);
				this.form.modules.push(module);
			}
		}.bind(this));
	},
	_afterLoaded: function(){
		this._loadDatagridStyle();
	},
    resetData: function(){
        this.setData();
    },
    setData: function(data){
        this._setBusinessData(data);
        if (data){
            this.gridData = data;
        }else{
            this.gridData = this._getValue();
        }

        if (this.isEdit) this._completeLineEdit();
        if (this.gridData){
            var trs = this.table.getElements("tr");
            for (var i=1; i<trs.length-1; i++){
                var tr = trs[i];
                var tds = tr.getElements("td");
                for (var j=0; j<tds.length; j++){
                    var td = tds[j];
                    var module = td.retrieve("module");
                    if (module){
                        this.form.modules.erase(module);
                        delete module;
                    }
                }
            }
            while (this.table.rows.length>2){
                this.table.rows[1].destroy();
            }
            if (this.editable!=false){
                this._loadEditDatagrid();
                //this._loadReadDatagrid();
            }else{
                this._loadReadDatagrid();
            }
            this._loadDatagridStyle();
        }


    },
	getData: function(){
        if (this.editable!=false){
            var data = [];
            var trs = this.table.getElements("tr");
            for (var i=1; i<trs.length-1; i++){
                var tr = trs[i];
                var d = tr.retrieve("data");
                if (d) data.push(d);
            }

            this.gridData = null;
            this.gridData = data;

            this._setBusinessData(data);

            return (this.gridData.length) ? this.gridData : null;
        }else{
            return this._getBusinessData();
        }

	},
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_cms_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red"
            },
            "text": text
        }).inject(node);
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },
    validation: function(){
        if (this.isEdit){
            if (!this.editValidation()){
                return false;
            }
        }

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.CMSMacro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
	
});

MWF.xApplication.cms.Xform.Datagrid$Title = MWF.CMSDatagrid$Title =  new Class({
	Extends: MWF.CMS$Module,
	_afterLoaded: function(){
        this.dataGrid = this.node.retrieve("dataGrid");
        if ((this.json.total == "number") || (this.json.total == "count")){
            this.dataGrid.totalModules.push({
                "module": this,
                "index": (this.dataGrid.editable!=false) ? this.node.cellIndex+1 : this.node.cellIndex,
                "type": this.json.total
            })
        }
	//	this.form._loadModules(this.node);
	}
});
MWF.xApplication.cms.Xform.Datagrid$Data = MWF.CMSDatagrid$Data =  new Class({
	Extends: MWF.CMS$Module,
	_afterLoaded: function(){
		//this.form._loadModules(this.node);
		this.dataGrid = this.node.retrieve("dataGrid");

        var td = this.node;
        if (this.json.cellType == "sequence"){
            this.dataGrid.editModules.push({
                "json": {"type": "sequence"},
                "node": td
            });
        }else{
            var moduleNodes = this.form._getModuleNodes(this.node);
            moduleNodes.each(function(node){
                var json = this.form._getDomjson(node);
                var module = this.form._loadModule(json, node, function(){
                    this.field = false;
                });
                module.dataModule = this;
                this.dataGrid.editModules.push(module);
            }.bind(this));
        }
	}
});
