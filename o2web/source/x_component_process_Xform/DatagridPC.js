MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.DatagridPC = new Class({
	Implements: [Events],
	Extends: MWF.APP$Module,
	isEdit: false,
    options: {
        "moduleEvents": ["queryLoad","postLoad","load","completeLineEdit", "addLine", "deleteLine", "afterDeleteLine","editLine"]
    },

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },

	_loadUserInterface: function(){
		this.fireEvent("queryLoad");

		this.editModules = [];
        this.node.setStyle("overflow-x", "auto");
        this.node.setStyle("overflow-y", "hidden");
		this.table = this.node.getElement("table");

        this.editable = (this.readonly) ? false : true;
        if (this.editable) this.editable = this.form.Macro.exec(this.json.editableScript.code, this);

		this.gridData = this._getValue();

        this.totalModules = [];
		this._loadDatagridTitleModules();

		if (this.editable!=false){
            this._loadDatagridDataModules();
            this._addTitleActionColumn();

			this._loadEditDatagrid();

			this.fireEvent("postLoad");
			this.fireEvent("load");
			//this._loadReadDatagrid();
		}else{
            this._loadDatagridDataModules();
            this._getDatagridEditorTr();
			this._loadReadDatagrid();
			if(this.editorTr)this.editorTr.setStyle("display", "none");

			this.fireEvent("postLoad");
			this.fireEvent("load");
		}
	},
	_loadStyles: function(){
		this.table.setStyles(this.json.tableStyles);
        this.node.setStyles(this.json.styles);
	},
	_getValue: function(){
		var value = [];
		value = this._getBusinessData();
		if (!value){
			if (this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
            value = {"data": value || []};
		}
		return value || {};
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

        if (this.gridData.data){
            this.gridData.data.each(function(data, idx){
                var tr = $(this.table.insertRow(idx+1));
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
        }

		
		this.editorTr.setStyle("display", "none");
	},
	
	_getValueText: function(idx, value){
		debugger;
		var module = this.editModules[idx];
		if (module){
			switch (module.json.type){
			case "Select":
                for (var i=0; i<module.json.itemValues.length; i++){
                    var itemv = module.json.itemValues[i];
                    var arr = itemv.split(/\|/);
                    var text = arr[0];
                    var v = (arr.length>1) ? arr[1] : arr[0];
					if (value===v) return text;
				}
				// var ops = module.node.getElements("option");
				// for (var i=0; i<ops.length; i++){
				// 	if (ops[i].value == value){
				// 		return ops[i].get("text");
				// 		break;
				// 	}
				// }
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
			case "Orgfield":
			case "Personfield":
			case "Org":
                //var v = module.getTextData();
				//return v.text[0];

				if (typeOf(value)==="array"){
					var textArray = [];
					value.each( function( item ){
						if (typeOf(item)==="object"){
							textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
						}else{
							textArray.push(item);
						}
					}.bind(this));
					return textArray.join(", ");
				}else if (typeOf(value)==="object"){
					return value.name+((value.unitName) ? "("+value.unitName+")" : "");
				}else{
					return value;
				}

				break;
			// case "address":
			// 	if (typeOf(value)==="array"){
			//
			// 	}
			// 	break;
			}
		}
		return value;
	},
	
	_createNewEditTd: function(tr, idx, id, text, lastIdx){
		var cell = $(tr.insertCell(idx));
		if (idx==0){
			cell.setStyles(this.form.css.gridLineActionCell);
			this._createAddLineAction(cell);
			this._createDelLineAction(cell);
		}else if (idx == lastIdx){
			cell.setStyles(this.form.css.gridMoveActionCell);
			this._createMoveLineAction(cell);
		}else{
			cell.set("MWFId", id);

			var module = this.editModules[idx-1];
			if( module && module.json.type == "ImageClipper" ){
				this._createImage( cell, module, text )
			}else{
				cell.set("text", text);
			}
			cell.addEvent("click", function(e){
				this._editLine(e.target);
			}.bind(this));
		}
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
			if (!this._completeLineEdit()) return false;
		}
		
		this.currentEditLine = td.getParent("tr");
		if (this.currentEditLine){
			this.editorTr.setStyles({
				//"background-color": "#fffeb5",
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
                    	debugger;
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


			this.fireEvent("editLine");
			
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

	_cancelLineEdit: function(){
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

		if (flag){
			newTr.destroy();
		}
		this.currentEditLine = null;

		this._editorTrGoBack();

		// if (this.json.contentStyles){
		// 	var tds = newTr.getElements("td");
		// 	tds.setStyles(this.json.contentStyles);
		// }
		// if (this.json.actionStyles){
		// 	newTr.getFirst().setStyles(this.json.actionStyles);
		// }

		// this._loadBorderStyle();
		// this._loadZebraStyle();
		// this._loadSequence();

		this.fireEvent("cancelLineEdit");
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
					if( module.json.type == "ImageClipper" ){
						this._createImage( cell, module, data.text );
					}else{
						cell.set("text", data.text.join(", "));
					}
				}else{
					this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), data.text.join(", "), titleThs.length-1);
				}
			}else{
				if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
			}
            module = null;
		}.bind(this));
		
		newTr.store("data", griddata);
		newTr.setStyle("display", "table-row");
		
		if (flag){
			newTr.destroy();
		}
		this.currentEditLine = null;
		
		this._editorTrGoBack();

        if (this.json.contentStyles){
            var tds = newTr.getElements("td");
            tds.setStyles(this.json.contentStyles);
        }
        if (this.json.actionStyles){
            newTr.getFirst().setStyles(this.json.actionStyles);
        }


        //this._loadTotal();

		this._loadBorderStyle();
		this._loadZebraStyle();
        this._loadSequence();

			
		this.getData();
        this.validationMode();
        this.fireEvent("completeLineEdit");

        return true;
	},
	_createImage : function( cell, module, data ){
		cell.empty();
		if( !data )return;
		var img = new Element("img",{
			src : MWF.xDesktop.getImageSrc( data )
		}).inject( cell, "top" );
		if( module.json.clipperType == "size" ){
			var width = module.json.imageWidth;
			var height = module.json.imageHeight;
			if (width && height) {
				img.setStyles({
					width: width + "px",
					height: height + "px"
				})
			}
		}
	},
	_editorTrGoBack: function(){
		this.editorTr.setStyle("display", "none");
//		this.editTr.removeEvents("blur");
        if (this.totalTr){
            this.editorTr.inject(this.totalTr, "before");
        }else{
            var lastTrs = this.table.getElements("tr");
            var lastTr = lastTrs[lastTrs.length-1];
            this.editorTr.inject(lastTr, "after");
        }
	},
	_addLine: function(node){
		if (this.isEdit){
            if (!this._completeLineEdit()) return false;
		}
		this.editorTr.setStyles({
			//"background-color": "#fffeb5",
			"display": "table-row"
		});
		this.currentEditLine = null; 
		var currentTr = node.getParent("tr");
		if (currentTr){
			this.editorTr.inject(currentTr, "after");
		}
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
			var color = currentTr.getStyle("background");
			currentTr.store("bgcolor", color);
			currentTr.tween("background-color", "#ffd4d4");
			var datagrid = this;
			var _self = this;
			this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
                _self.fireEvent("deleteLine", [currentTr]);

                currentTr.destroy();
				datagrid._loadZebraStyle();
                datagrid._loadSequence();
                datagrid._loadTotal();
				datagrid.getData();
				this.close();

                _self.fireEvent("afterDeleteLine");
			}, function(){
				var color = currentTr.retrieve("bgcolor");
				currentTr.tween("background", color);
				this.close();
			}, null, null, this.form.json.confirmStyle);
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
		
		var color = tr.getStyle("background");
		tr.store("bgcolor", color);
		//tr.tween('background-color', '#f3f1ad');
        tr.tween('background-color', '#e4f6e9');


		var drag = new Drag.Move(dragNode, {
			"droppables": trs.erase(tr),
			"limit": {"x": [coordinates.left, coordinates.left]},
			onDrop: function(dragging, droppable){
				dragging.destroy();
                //debugger;
                var color = tr.retrieve("bgcolor");
                if (color){
                    tr.tween("background", color);
                }else{
                    tr.tween("background", "transparent");
                }
                //if (droppable){
                //    color = droppable.retrieve("bgcolor");
                //    if (color){
                //        droppable.tween("background", color);
                //    }else{
                //        droppable.tween("background", "transparent");
                //    }
                //}

                tr.setStyle("display", "table-row");
		        if (droppable != null){
		        	tr.inject(dragTr, "after");
		        	dragTr.destroy();
		        	//this._loadZebraStyle();
                    //this._loadSequence();
                    //this._loadTotal();
                    this._loadDatagridStyle();
		        	this.getData();
		        }

			}.bind(this),
			"onEnter": function(dragging, drop){
				//var color = drop.getStyle("background");
				//if (color.toUpperCase()!='#d1eaf3') drop.store("bgcolor", color);
				//drop.tween("background-color", "#d1eaf3");
				dragNode.setStyle("display", "none");
				dragTr.inject(drop, "after");
			},
			"onLeave": function(dragging, drop){
				//var color = drop.retrieve("bgcolor");
				//if (color){
				//	drop.tween("background", color);
				//}else{
				//	drop.tween("background", "transparent");
				//}
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
                    tr.tween("background", color);
                }else{
                    tr.tween("background", "transparent");
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
		//var tds = lastTr.getElements("td");

        if (this.gridData.data){
            this.gridData.data.each(function(data, idx){
                var tr = this.table.insertRow(idx+1);
                tr.store("data", data);

                titleHeaders.each(function(th, index){
                    var cell = tr.insertCell(index);
                    // cell.set("MWFId", tds[index].get("id"));
                    var cellData = data[th.get("id")];
                    if (cellData){

                        for (key in cellData){
                        	var v = cellData[key];

							var module = this.editModules[index];
							if( module && module.json.type == "ImageClipper" ){
								this._createImage( cell, module, v )
							}else{
								var text = this._getValueText(index, v);
								cell.set("text", text);
							}


							// if (typeOf(v)==="array"){
							// 	var textArray = [];
							// 	v.each( function( item ){
							// 		if (typeOf(item)==="object"){
							// 			textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
							// 		}else{
							// 			textArray.push(item);
							// 		}
							// 	}.bind(this));
							// 	cell.set("text", textArray.join(", "));
							// }else if (typeOf(v)==="object"){
                             //    cell.set("text", v.name+((v.unitName) ? "("+v.unitName+")" : ""));
							// }else{
                             //    cell.set("text", v);
							// }
                            break;
                        }
                    }else{ //Sequence
                        cell.setStyle("text-align", "center");
                        cell.set("text", tr.rowIndex);
                    }

                }.bind(this));
            }.bind(this));
        }


        //lastTr.destroy();

        this._loadTotal();
     //   this._loadSequenceRead();
		

	},
	
	_loadDatagridStyle: function(){
		//var ths = this.titleTr.getElements("th");
		//ths.setStyles(this.form.css.datagridTitle);
        this.loadGridTitleStyle();
        this.loadGridContentStyle();
        this.loadGridActionStyle();
        this.loadGridEditStyle();

        this._loadTotal();
		this._loadBorderStyle();
		this._loadZebraStyle();
        this._loadSequence();

	},
    loadGridEditStyle: function(){
        if (this.editorTr){
            if (this.json.editStyles){
                var tds = this.editorTr.getElements("td");
                tds.setStyles(this.json.editStyles);
            }
        }
    },
    loadGridActionStyle: function(){
        if (this.editable!=false){
            if (this.json.actionStyles){
                var trs = this.table.getElements("tr");
                trs.each(function(tr, idx){
                    if (idx != 0) tr.getFirst().setStyles(this.json.actionStyles);
                }.bind(this));
            }
        }
    },
    loadGridTitleStyle: function(){
        if (this.json.titleStyles){
            var ths = this.titleTr.getElements("th");
            ths.setStyles(this.json.titleStyles);
        }
    },
    loadGridContentStyle: function(){
        if (this.json.contentStyles){
            var tds = this.table.getElements("td");
            tds.setStyles(this.json.contentStyles);
        }
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
        ths.each(function(th, idx){
            var td = new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
            if (this.json.amountStyles) td.setStyles(this.json.amountStyles);
        }.bind(this));
    },
    _loadTotal: function(){
        var data = {};
        this.totalResaults = {};
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
                        var tmpV = new Decimal(totalResaults[i]);
                        if (m.type=="number"){
                            var cell = cells[m.index];
                            var addv = cell.get("text").toFloat();
                            tmpV = tmpV.plus(addv);
                            //tmpV = tmpV + addv;
                        }
                        if (m.type=="count"){
                            tmpV = tmpV.plus(1);
                            //tmpV = tmpV+1;
                        }
                        totalResaults[i] = tmpV.toString();
                        data[m.module.json.id] = totalResaults[i];

                    }.bind(this));
                }
            }

            this.totalModules.each(function(m, i){
                this.totalResaults[m.module.json.id] = totalResaults[i];
                var td = totalTds[m.index];
                td.set("text", totalResaults[i] || "");
            }.bind(this));
        }
        return data;
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
        this.setData(this._getValue());
    },
    setData: function(data){
        if (data){
            this._setBusinessData(data);
            this.gridData = data;
        }else{
            this.gridData = this._getValue();
        }

        if (this.isEdit) this._completeLineEdit();
        if (this.gridData){
            var trs = this.table.getElements("tr");
            for (var i=1; i<trs.length-1; i++){
                var tr = trs[i];
				if( tr.hasClass("datagridEditorTr") )continue;
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
			for (var i=1; i<trs.length-1; i++){
				if( trs[i].hasClass("datagridTotalTr") )continue;
				if( trs[i].hasClass("datagridEditorTr") )continue;
				trs[i].destroy();
			}
            //while (this.table.rows.length>2){
				//this.table.rows[1].destroy();
            //}
            if (this.editable!=false){
                this._loadEditDatagrid();
                //this._loadReadDatagrid();
            }else{
                this._loadReadDatagrid();
            }
            this._loadDatagridStyle();
        }


    },
    getTotal: function(){
        this._loadTotal();
        return this.totalResaults;
    },
	getData: function(){
        if (this.editable!=false){
			if (this.isEdit) this._completeLineEdit();
            var data = [];
            var trs = this.table.getElements("tr");
            for (var i=1; i<trs.length-1; i++){
                var tr = trs[i];
                var d = tr.retrieve("data");
                if (d) data.push(d);
            }

            this.gridData = {};
            this.gridData.data = data;

            this._loadTotal();
            this.gridData.total = this.totalResaults;

            this._setBusinessData(this.gridData);

            return (this.gridData.data.length) ? this.gridData : null;
        }else{
            return this._getBusinessData();
        }
	},
    getAmount: function(){
        return this._loadTotal();
    },
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red",
                "word-break": "keep-all"
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
            this.showNotValidationMode(this.node);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            if (p.get("MWFtype") == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
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

    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    validation: function(routeName, opinion){
        if (this.isEdit){
            if (!this.editValidation()){
                return false;
            }
        }
        if (!this.validationConfig(routeName, opinion))  return false;

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }
	
});

MWF.xApplication.process.Xform.DatagridPC$Title =  new Class({
	Extends: MWF.APP$Module,
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
MWF.xApplication.process.Xform.DatagridPC$Data =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
		//this.form._loadModules(this.node);
		this.dataGrid = this.node.retrieve("dataGrid");

        var td = this.node;

        if (this.json.cellType == "sequence"){
            var flag = true;
            for (var i=0; i<this.dataGrid.editModules.length; i++){
                if (this.dataGrid.editModules[i].json.id == this.json.id){
                    flag = false;
                    break;
                }
            }
            if (flag){
                this.dataGrid.editModules.push({
                    "json": {"type": "sequence", "id": this.json.id},
                    "node": td  ,
                    "focus": function(){}
                });
            }
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