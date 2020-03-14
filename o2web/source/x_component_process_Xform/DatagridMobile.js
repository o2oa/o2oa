MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.DatagridMobile = new Class({
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
        this.node.setStyle("overflow-x", "hidden");
        this.node.setStyle("overflow-y", "hidden");
		this.table = this.node.getElement("table");

        this.createMobileTable();

        this.editable = (!this.readonly);
        if (this.editable) this.editable = this.form.Macro.exec(this.json.editableScript.code, this);
        //this.editable = false;

        this.gridData = this._getValue();

        this.totalModules = [];
		this._loadDatagridTitleModules();

		if (this.editable!=false){
            this._loadDatagridDataModules();
            //this._addTitleActionColumn();
			this._loadEditDatagrid();
			//this._loadReadDatagrid();
            this.fireEvent("postLoad");
            this.fireEvent("load");
		}else{
            this._loadDatagridDataModules();
			this._loadReadDatagrid();
            this.fireEvent("postLoad");
            this.fireEvent("load");
		}
	},
    createMobileTable: function(){
        var mobileTable = new Element("table").inject(this.node);
        mobileTable.set(this.json.properties);
        //mobileTable.setStyle("display", "none");
        //mobileTable.setStyle("margin-bottom", "10px");
        var trs = this.table.getElements("tr");
        var titleTds = trs[0].getElements("th");
        var contentTds = trs[1].getElements("td");
        titleTds.each(function(titleTd, i){
            var mobileTr = mobileTable.insertRow();
            //var mobileTr = new Element("tr").inject(mobileTable);
            var th = new Element("th").inject(mobileTr);
            th.set({
                "html": titleTd.get("html"),
                "id": titleTd.get("id"),
                "mwftype": titleTd.get("mwftype")
            });
            var td = new Element("td").inject(mobileTr);
            td.set({
                "html": contentTds[i].get("html"),
                "id": contentTds[i].get("id"),
                "mwftype": contentTds[i].get("mwftype")
            });
        }.bind(this));
        this.table.destroy();
        this.table = null;
        this.table = mobileTable;
    },

	_loadStyles: function(){
		//this.table.setStyles(this.json.tableStyles);
        this.node.setStyles(this.json.styles);
        var tables = this.node.getElements("table");
        tables.each(function(table){
            table.setStyles(this.json.tableStyles);
        }.bind(this));

	},
	_getValue: function(){
		var value = [];
		value = this._getBusinessData();
		if (!value){
			if (this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
            value = {"data": value || []};
		}
		return value || [];
	},

	_getValueText: function(idx, value){
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
				// 	}
				// }
				break;
			case "Radio":
				var ops = module.node.getElements("input");
				for (var i=0; i<ops.length; i++){
					if (ops[i].value == value){
						return ops[i].get("showText");
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
			}
		}
		return value;
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

	_loadReadDatagrid: function(){
		this.gridData = this._getValue();

        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");

        if (this.gridData.data){
            this.gridData.data.each(function(data, idx){
                var dataDiv = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.node);
                var tableDiv = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(dataDiv);
                var table = new Element("table").inject(tableDiv);
                table.set(this.json.properties);
                table.store("data", data);
                //table.setStyle("margin-bottom", "10px");
                titleHeaders.each(function(th, index){
                    var tr = table.insertRow(index);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    var cell = tr.insertCell(1);
                    cell.set("MWFId", tds[index].get("id"));

                    var cellData = data[th.get("id")];
                    if (cellData){
                        for (key in cellData){
                            var v = cellData[key];

                            var module = this.editModules[index];
                            if( module && module.json.type == "ImageClipper" ){
                                this._createImage( cell, module, v )
                            }else{
                                text = this._getValueText(index, v);
                                cell.set("text", text);
                            }


                            // if (typeOf(v)==="array"){
                            //     var textArray = [];
                            //     v.each( function( item ){
                            //         if (typeOf(item)==="object"){
                            //             textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
                            //         }else{
                            //             textArray.push(item);
                            //         }
                            //     }.bind(this));
                            //     cell.set("text", textArray.join(", "));
                            // }else if (typeOf(v)==="object"){
                            //     cell.set("text", v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                            // }else{
                            //     cell.set("text", v);
                            // }
                            break;
                            //
                            // cell.set("text", cellData[key]);
                            // break;
                        }
                    }else{ //Sequence
                        cell.setStyle("text-align", "left");
                        cell.set("text", idx+1);
                    }

                }.bind(this));
            }.bind(this));
        }

        //this._loadTotal();
	},
    _loadEditDatagrid: function(){
        //this._createHelpNode();

        this.gridData = this._getValue();

        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");

        var _self = this;

        if (this.gridData.data.length){
            this.gridData.data.each(function(data, idx){
                var dataDiv = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.node);
                this._createItemTitleNode(dataDiv, idx);

                var tableDiv = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(dataDiv);
                var table = new Element("table").inject(tableDiv);
                table.set(this.json.properties);
                table.store("data", data);
                //table.setStyle("margin-bottom", "10px");
                titleHeaders.each(function(th, index){
                    var tr = table.insertRow(index);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    var cell = tr.insertCell(1);
                    cell.set("MWFId", tds[index].get("id"));

                    var cellData = data[th.get("id")];
                    if (cellData){
                        for (key in cellData){
                            var v = cellData[key];

                            var module = this.editModules[index];
                            if( module && module.json.type == "ImageClipper" ){
                                this._createImage( cell, module, v )
                            }else{
                                text = this._getValueText(index, v);
                                cell.set("text", text);
                            }

                            // if (typeOf(v)==="object"){
                            //     cell.set("text", v.name+((v.unitName) ? "("+v.unitName+")" : ""));
                            // }else{
                            //     cell.set("text", v);
                            // }
                            break;
                            //
                            // cell.set("text", cellData[key]);
                            // break;
                        }
                    }else{ //Sequence
                        cell.setStyle("text-align", "left");
                        cell.set("text", idx+1);
                    }

                }.bind(this));
                var size = dataDiv.getSize();
                //dataDiv.setStyle("height", ""+size.y+"px");

                //dataDiv.addEvent("touchstart", function(e){_self.actionTouchstart(this, e);});
                //dataDiv.addEvent("touchmove", function(e){_self.actionTouchmove(this, e);});
                //dataDiv.addEvent("touchend", function(e){_self.actionTouchend(this, e);});
                //dataDiv.addEvent("touchcancel", function(e){_self.actionTouchcancel(this, e);});
                //
                //dataDiv.addEvent("mousedown", function(e){_self._actionMousedown(this, e);});
                //dataDiv.addEvent("mouseup", function(e){_self._actionMouseup(this, e);});

                //this.showMoveAction(dataDiv, 60);
                //this.showEndMoveAction(dataDiv);
            }.bind(this));
        }else{
            this._loadAddAction();
        }
        //this._loadTotal();
    },
    _loadActions: function(titleDiv){
        var actionNode = new Element("div", {
            "styles": this.form.css.mobileDatagridActionNode
        }).inject(titleDiv);
        var delAction = new Element("div", {
            "styles": this.form.css.mobileDatagridDelActionNode,
            "text": MWF.xApplication.process.Xform.LP["delete"]
        }).inject(actionNode);
        var editAction = new Element("div", {
            "styles": this.form.css.mobileDatagridEditActionNode,
            "text": MWF.xApplication.process.Xform.LP.edit
        }).inject(actionNode);
        var addAction = new Element("div", {
            "styles": this.form.css.mobileDatagridAddActionNode,
            "text": MWF.xApplication.process.Xform.LP.add
        }).inject(actionNode);
        var completeAction = new Element("div", {
            "styles": this.form.css.mobileDatagridCompleteActionNode,
            "text": MWF.xApplication.process.Xform.LP.completedEdit
        }).inject(actionNode);


        var _self = this;
        delAction.addEvent("click", function(e){
            _self._deleteLine(this.getParent().getParent().getParent(), e);
        });
        editAction.addEvent("click", function(e){
            _self._editLine(this.getParent().getParent().getParent(), e);
        });
        completeAction.addEvent("click", function(e){
            _self._completeLineEdit();
        });
        addAction.addEvent("click", function(e){
            _self._addLine();
        });
    },
    _createImage : function( cell, module, data ){
        cell.empty();
        if( !data )return;
        var img = new Element("img",{
            src : MWF.xDesktop.getImageSrc( data )
        }).inject( cell, "top" );
        img.setStyles({
            "max-width": "90%"
        })
    },
    _createItemTitleNode: function(node, idx){
        var n = idx+1;
        var titleDiv = new Element("div", {"styles": this.json.itemTitleStyles}).inject(node);
        titleDiv.setStyle("overflow", "hidden");
        var textNode = new Element("div", {
            "styles": {"float": "left"},
            "text": MWF.xApplication.process.Xform.LP.item+n
        }).inject(titleDiv);
        //if (idx==0){
            this._loadActions(titleDiv);
        //}
    },
    _createHelpNode: function(){
        this.helpNode = new Element("div", {"styles": this.form.css.mobileGridHelpNode}).inject(this.node, "top");
        this.helpContentNode = new Element("div", {"styles": this.form.css.mobileGridHelpContentNode}).inject(this.helpNode);
        this.helpContentNode.set("html", MWF.xApplication.process.Xform.LP.mobileGridHelp);
        new mBox.Tooltip({
            content: this.helpContentNode,
            setStyles: {content: {padding: 15, lineHeight: 20}},
            attach: this.helpNode,
            transition: 'flyin',
            position: {
                x: ['right'],
                y: ['center']
            },
            event: 'click'
        });
    },
    _editLine:function(node){
        if (this.isEdit){
            if (!this._completeLineEdit()) return false;
        }

        this.currentEditLine = node;
        var currentEditTable = node.getElement("table");
        if (this.currentEditLine){
            this.table.setStyles({
                "background-color": "#fffeb5",
                "display": "table"
            });
            this.table.inject(currentEditTable, "after");
            //this.currentEditLine.setStyle("display", "none");
            currentEditTable.setStyle("display", "none");

            var actions = this.currentEditLine.getFirst("div").getLast("div").getElements("div");
            if (actions[0]) actions[0].setStyle("display", "none");
            if (actions[1]) actions[1].setStyle("display", "none");
            if (actions[2]) actions[2].setStyle("display", "none");
            if (actions[3]) actions[3].setStyle("display", "block");

            //this.addAction.inject(this.table, "after");
            //this.addAction.set("text", MWF.xApplication.process.Xform.LP.completedEdit);
            //this.addAction.removeEvents("click");
            //this.addAction.addEvent("click", function(){
            //    this._completeLineEdit();
            //}.bind(this));


            var data = this.currentEditLine.getElement("table").retrieve("data");
            var titleThs = this.table.getElements("th");
            titleThs.each(function(th, idx){
                var id = th.get("id");
                var module = this.editModules[idx];
                if (module){
                    if (module.json.type=="sequence"){
                        module.node.set("text", this.currentEditLine.getElement("table").getElements("tr")[idx].getElement("td").get("text"));
                    }else {
                        if (data[id]) {
                            module.setData(data[id][module.json.id]);
                        } else {
                            module.setData(null);
                        }
                    }
                }
            }.bind(this));

            //var cellIdx = this.currentEditLine.getElements("td").indexOf(td);
            //var module = this.editModules[cellIdx-1];
            //if (module) module.focus();

            this.fireEvent("editLine");

            this.isEdit =true;
        }
        this.validationMode();
    },
    _loadAddAction: function(){
        this.addAction = new Element("div", {"styles": this.form.css.gridMobileActionNode}).inject(this.node);
        this.addAction.set("text", MWF.xApplication.process.Xform.LP.addLine);
        this.addAction.addEvent("click", function(){
            this._addLine();
        }.bind(this));
    },
    _addLine: function(){
        if (this.isEdit){
            if (!this._completeLineEdit()) return false;
        }
        if (this.addAction) this.addAction.setStyle("display", "none");

        var tables = this.node.getElements("table");
        var idx;
        var dataDiv = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}});
        if (this.totalDiv){
            idx = tables.length-2;
            dataDiv.inject(this.totalDiv, "before");
        }else{
            idx = tables.length-1;
            dataDiv.inject(this.node);
        }
        this._createItemTitleNode(dataDiv, idx);

        var tableDiv = new Element("div", {"styles":  this.form.css.gridMobileTableNode }).inject(dataDiv);

        this.table.setStyles({
            "background-color": "#fffeb5",
            "display": "table"
        });

        this.currentEditLine = null;
        this.table.inject(tableDiv);
        this.isEdit = true;

        var actions = dataDiv.getFirst("div").getLast("div").getElements("div");
        if (actions[0]) actions[0].setStyle("display", "none");
        if (actions[1]) actions[1].setStyle("display", "none");
        if (actions[2]) actions[2].setStyle("display", "none");
        if (actions[3]) actions[3].setStyle("display", "block");

        if (!dataDiv.isIntoView()) dataDiv.scrollIntoView(true);

        //this.addAction.set("text", MWF.xApplication.process.Xform.LP.completedEdit);
        //this.addAction.removeEvents("click");
        //this.addAction.addEvent("click", function(){
        //    this._completeLineEdit();
        //}.bind(this));

        this.validationMode();
        this.fireEvent("addLine");
    },
    _completeLineEdit: function(){
        if (!this.editValidation()){
            return false;
        }
        this.isEdit = false;
        //var flag = true;

        var griddata = {};
        var dataNode = null;
        var table;

        if (this.currentEditLine){
            dataNode = this.currentEditLine;
            griddata = dataNode.getElement("table").retrieve("data");
            this.currentEditLine.getElement("table").setStyle("display", "table");
            table = dataNode.getElement("table");

            var actions = this.currentEditLine.getFirst("div").getLast("div").getElements("div");
            if (actions[0]) actions[0].setStyle("display", "block");
            if (actions[1]) actions[1].setStyle("display", "block");
            if (actions[2]) actions[2].setStyle("display", "block");
            if (actions[3]) actions[3].setStyle("display", "none");
        }else{
            //dataNode = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.table, "before");
            //var tableDiv = new Element("div", {"styles": {"overflow": "hidden"}}).inject(dataNode);
            var actions = this.table.getParent().getPrevious("div").getLast("div").getElements("div");
            if (actions[0]) actions[0].setStyle("display", "block");
            if (actions[1]) actions[1].setStyle("display", "block");
            if (actions[2]) actions[2].setStyle("display", "block");
            if (actions[3]) actions[3].setStyle("display", "none");

            table = new Element("table", {
                styles : this.json.tableStyles
            }).inject(this.table, "before");
            table.set(this.json.properties);
            griddata = {};

            //dataNode.addEvent("touchstart", function(e){_self.actionTouchstart(this, e);});
            //dataNode.addEvent("touchmove", function(e){_self.actionTouchmove(this, e);});
            //dataNode.addEvent("touchend", function(e){_self.actionTouchend(this, e);});
            //dataNode.addEvent("touchcancel", function(e){_self.actionTouchcancel(this, e);});
        }

        var tables = this.node.getElements("table");
        var titleHeaders = this.table.getElements("th");
        var tds = this.table.getElements("td");
        var dataRows = table.getElements("tr");

        titleHeaders.each(function(th, idx){
            var dataRow = dataRows[idx];
            var id = th.get("id");
            var module = this.editModules[idx];
            if (module){
                var data;
                if (module.json.type=="sequence"){
                    var i;
                    if (!this.currentEditLine){
                        i = tables.length-1;
                        if (this.totalTable) i = tables.length-2;
                    }else{
                        i = this.currentEditLine.getElement("table").getElements("tr")[idx].getElement("td").get("text");
                    }
                    data = {"value": [i], "text": [i]};
                }else{
                    data = module.getTextData();
                    //if (data.value[0]) flag = false;
                    if (data.value.length<2){
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value[0];
                    }else{
                        if (!griddata[id]) griddata[id] = {};
                        griddata[id][module.json.id] = data.value;
                    }
                }

                var cell;
                if (dataRow){
                    cell = dataRow.getElement("td");

                    if( module.json.type == "ImageClipper" ){
                        this._createImage( cell, module, data.text );
                    }else{
                        cell.set("text", data.text.join(", "));
                    }
                }else{
                    dataRow = table.insertRow(idx);
                    var datath = new Element("th").inject(dataRow);
                    datath.set("text", th.get("text"));
                    datath.setStyle("width", "30%");

                    cell = dataRow.insertCell(1);
                    cell.set("MWFId", tds[idx].get("id"));

                    var cellData = data[th.get("id")];
                    if( module.json.type == "ImageClipper" ){
                        this._createImage( cell, module, data.text );
                    }else{
                        cell.set("text", data.text.join(", "));
                    }
                }
            }else{
                if (!dataRow) {
                    dataRow = table.insertRow(idx);
                    var datath1 = new Element("th").inject(dataRow);
                    datath1.set("text", th.get("text"));
                    datath1.setStyle("width", "30%");
                    cell = dataRow.insertCell(1);
                }
            }
            module = null;
        }.bind(this));

        table.store("data", griddata);
        table.setStyle("display", "table");

        //if (flag){
        //    newTr.destroy();
        //}
        this.currentEditLine = null;

        this._editorTrGoBack();

        this.getData();
        this._loadDatagridStyle();

        this.validationMode();
        this.fireEvent("completeLineEdit");

        this.addAction.set("text", MWF.xApplication.process.Xform.LP.addLine);
        this.addAction.removeEvents("click");
        this.addAction.addEvent("click", function(){
            this._addLine();
        }.bind(this));


        return true;
    },
    _editorTrGoBack: function(){
        this.table.setStyle("display", "none");
        this.table.inject(this.node, "top");

        //if (this.totalDiv){
        //    this.addAction.inject(this.totalDiv, "before");
        //}else{
        //    this.addAction.inject(this.node);
        //}

//		this.editTr.removeEvents("blur");
//        if (this.totalTr){
//            this.editorTr.inject(this.totalTr, "before");
//        }else{
//            var lastTrs = this.table.getElements("tr");
//            var lastTr = lastTrs[lastTrs.length-1];
//            this.editorTr.inject(lastTr, "after");
//        }
    },

    _actionMousedown: function(node, e){
        var status = node.retrieve("editStatus");
        if (!status){
            node.store("editStatus", new Date());
        }
        this._checkMouseDown(node);
    },
    _checkMouseDown: function(node){
        var status = node.retrieve("editStatus");
        if (status){
            var d = (new Date()).getTime();
            if (d-status.getTime()>1000){
                this._editLine(node);
                node.eliminate("editStatus");
            }else{
                window.setTimeout(function(){
                    this._checkMouseDown(node);
                }.bind(this), 1000)
            }
        }
    },
    _actionMouseup: function(node, e){
        node.eliminate("editStatus");
    },

    actionTouchstart: function(node, e){
        var p = {"x": e.touches[0].pageX, "y": e.touches[0].pageY};
        //node.store("start", p);
        var action = node.retrieve("action");
        if (action){
            node.store("touchStatus", {"p": p, "status": "hide", "isHide": false});
        }else{
            node.store("touchStatus", {"p": p, "status": "show"});
        }
        this._actionMousedown(node, e);
        //e.preventDefault();
    },
    actionTouchmove: function(node, e){
        var touchStatus = node.retrieve("touchStatus");
        var p = touchStatus.p;
        var status = touchStatus.status;
        var x = e.touches[0].pageX;
        var y = e.touches[0].pageY;

        if (Math.abs(p.x-x)>10 || Math.abs(p.y-y)>10){
            this._actionMouseup(node);
        }
        if (status=="show"){
            if ((p.x-x > 20) && Math.abs(p.y-y)<40){
                this.showMoveAction(node, p.x-x);
                e.preventDefault();
            }
        }else{
            if ((x-p.x > 20) && Math.abs(p.y-y)<40){
                touchStatus.isHide = true;
                this.hideMoveAction(node, x- p.x);
                e.preventDefault();
            }
        }

    },
    actionTouchend: function(node, e){
        var touchStatus = node.retrieve("touchStatus");
        var p = touchStatus.p;
        var status = touchStatus.status;
        if (status=="show"){
            var action = node.retrieve("action");
            if (action) this.showEndMoveAction(node);
        }else{
            if (touchStatus.isHide) this.hideEndMoveAction(node);
        }
    },
    actionTouchcancel: function(node, e){
        this._actionMouseup(node);
    },
    showEndMoveAction: function(node){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        new Fx.Tween(action, {"duration": 100}).start("width", "60px");
        new Fx.Tween(tableNode, {"duration": 100}).start("margin-right", "60px");

        var _self = this;
        action.addEvent("click", function(e){
            _self._deleteLine(node, e);
        });
    },
    _deleteLine: function(node, e){
        var currentTable = node.getElement("table");
        if (currentTable){

            var datagrid = this;
            var _self = this;
            this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
                _self.fireEvent("deleteLine", [currentTable]);

                node.destroy();
                //datagrid._loadZebraStyle();
                datagrid._loadSequence();
                datagrid._loadTotal();
                datagrid.getData();

                if (!_self.gridData.data.length) if (_self.addAction) _self.addAction.setStyle("display", "block");
                this.close();

                _self.fireEvent("afterDeleteLine");
            }, function(){
                //var color = currentTr.retrieve("bgcolor");
                //currentTr.tween("background-color", color);
                this.close();
            }, null, null, this.form.json.confirmStyle);
        }
        this.validationMode();
    },
    hideEndMoveAction: function(node){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");

        new Fx.Tween(action, {"duration": 100}).start("width", "0px");
        new Fx.Tween(tableNode, {"duration": 100}).start("margin-right", "0px").chain(function(){
            action.destroy();
            node.eliminate("action");
        });

    },
    showMoveAction: function(node, length){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        if (!action){
            var size = node.getSize();
            action = new Element("div", {
                "styles": this.form.css.gridMobileDeleteActionAreaNode
            }).inject(node, "top");
            var button = new Element("div", {
                "styles": this.form.css.gridMobileDeleteActionNode,
                "text": MWF.xApplication.process.Xform.LP["delete"]
            }).inject(action);

            action.setStyle("height",""+size.y+"px");
            action.setStyle("line-height",""+size.y+"px");
            node.store("action", action);
        }
        if (length>60) length = 60;
        action.setStyle("width", ""+length+"px");
        tableNode.setStyle("margin-right", ""+length+"px");
    },
    hideMoveAction: function(node, length){
        var action = node.retrieve("action");
        var tableNode = node.getLast("div");
        var width = 60-length;
        if (width<0) width=0;
        if (action) action.setStyle("width", ""+width+"px");
        tableNode.setStyle("margin-right", ""+width+"px");
    },

	_loadDatagridStyle: function(){
		//var ths = this.node.getElements("th");
		//ths.setStyles(this.form.css.datagridTitle);

        this.loadGridTitleStyle();
        this.loadGridContentStyle();
        //this.loadGridActionStyle();
        this.loadGridEditStyle();

        this._loadTotal();
		this._loadBorderStyle();
		this._loadZebraStyle();
        this._loadSequence();
	},

    loadGridEditStyle: function(){
        if (this.table){
            if (this.json.editStyles){
                var tds = this.table.getElements("td");
                tds.setStyles(this.json.editStyles);
            }
        }
    },
    //loadGridActionStyle: function(){
    //    if (this.editable!=false){
    //        if (this.json.actionStyles){
    //            var trs = this.table.getElements("tr");
    //            trs.each(function(tr, idx){
    //                if (idx != 0) tr.getFirst().setStyles(this.json.actionStyles);
    //            }.bind(this));
    //        }
    //    }
    //},
    loadGridTitleStyle: function(){
        if (this.json.titleStyles){
            var ths = this.node.getElements("th");
            ths.setStyles(this.json.titleStyles);
        }
    },
    loadGridContentStyle: function(){
        if (this.json.contentStyles){
            var tds = this.node.getElements("td");
            tds.setStyles(this.json.contentStyles);
        }
    },
	
	_loadZebraStyle: function(){
        if (this.json.zebraColor || this.json.backgroundColor){
            var tables = this.node.getElements("table");
            tables.each(function(table){
                this._loadTableZebraStyle(table);
            }.bind(this));
        }
	},
    _loadTableZebraStyle: function(table){
        var trs = table.getElements("tr");
        for (var i=0; i<trs.length; i++){
            if (this.json.backgroundColor) trs[i].setStyle("background-color", this.json.backgroundColor);
            if ((i%2)==0){
                if (this.json.zebraColor) trs[i].setStyle("background-color", this.json.zebraColor);
            }
        }
    },

    createTotalDiv: function(){
        this.totalDiv = new Element("div", {"styles": {"overflow": "hidden", "margin-bottom": "10px"}}).inject(this.node);
        var titleNode = new Element("div", {"styles": this.json.itemTitleStyles}).inject(this.totalDiv);
        titleNode.set("text", MWF.xApplication.process.Xform.LP.amount);
        var tableNode = new Element("div", {"styles": this.form.css.gridMobileTableNode }).inject(this.totalDiv);
        this.totalTable = new Element("table", {
            styles : this.json.tableStyles
        }).inject(tableNode);
        this.totalTable.set(this.json.properties);
    },

    _loadTotal: function(){
        var data = {};
        this.totalResaults = {};
        if (this.totalModules.length){
            if (!this.totalDiv){
                this.createTotalDiv();
            }
            var totalResaults = [];
            //this.totalModules.each(function(m. i){
            //    totalResaults.push(0);
            //}.bind(this));
            var tables = this.node.getElements("table");
            //var totalTds = this.totalTr.getElements("td");
            for (var i=1; i<tables.length-1; i++){

                var cells = tables[i].getElements("td");
                this.totalModules.each(function(m, idx){
                    if (!totalResaults[idx]) totalResaults.push(0);
                    var tmpV = new Decimal(totalResaults[idx]);
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
                    totalResaults[idx] = tmpV.toString();
                    data[m.module.json.id] = totalResaults[idx];
                }.bind(this));
            }

            var trs = this.totalTable.getElements("tr");
            this.totalModules.each(function(m, i){
                var tr = trs[i];
                if (!tr){
                    tr = this.totalTable.insertRow(i);
                    var datath = new Element("th").inject(tr);
                    datath.set("text", m.module.node.get("text"));
                //    datath.setStyle("width", "30%");
                    datath.setStyles(this.json.titleStyles);
                    tr.insertCell(1).setStyles(this.json.amountStyles).set("text", totalResaults[i] || "");
                }else{
                    tr.getElement("td").set("text", totalResaults[i] || "");
                }
                this.totalResaults[m.module.json.id] = totalResaults[i];
            }.bind(this));
            if (this.totalTable){
                var ths = this.totalTable.getElements("th");
                ths.setStyles(this.json.titleStyles);
                //if (this.json.border) this._loadTableBorderStyle(this.totalTable);
                //if (this.json.zebraColor || this.json.backgroundColor) this._loadTableZebraStyle(this.totalTable);
            }
        }
        return data;
    },
    _loadSequence: function(){
        var tables = this.node.getElements("table");
        tables.each(function(table, idx){
            var cells = table.getElements("td");
            cells.each(function(cell){
                var module = cell.retrieve("module");
                if (module){
                    if (module.json.cellType=="sequence"){
                        cell.set("text", idx)
                    }
                }
            }.bind(this));
        }.bind(this));
    },
	
	_loadBorderStyle: function(){
		if (this.json.border){
            var tables = this.node.getElements("table");
            tables.each(function(table){
                this._loadTableBorderStyle(table)
            }.bind(this));
		}
	},
    _loadTableBorderStyle: function(table){
        table.setStyles({
            "border-top": this.json.border,
            "border-left": this.json.border
        });
        var ths = table.getElements("th");
        ths.setStyles({
            "border-bottom": this.json.border,
            "border-right": this.json.border
        });
        var tds = table.getElements("td");
        tds.setStyles({
            "border-bottom": this.json.border,
            "border-right": this.json.border,
            "background": "transparent"
        });
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
        if (this.table) this.table.setStyle("display", "none");
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

            var tables = this.node.getElements("table");
            for (var i=1; i<tables.length-1; i++){
                var table = tables[i];
                var tds = table.getElements("td");
                for (var j=0; j<tds.length; j++){
                    var td = tds[j];
                    var moduleTd = td.retrieve("module");
                    if (moduleTd){
                        this.form.modules.erase(moduleTd);
                        delete moduleTd;
                    }
                }
                var ths = table.getElements("th");
                for (var k=0; k<ths.length; k++){
                    var th = ths[k];
                    var moduleTh = th.retrieve("module");
                    if (moduleTh){
                        this.form.modules.erase(moduleTh);
                        delete moduleTh;
                    }
                }
            }

            while (tables.length>2){
                tables[1].destroy();
                tables = this.node.getElements("table");
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
    getTotal: function(){
        this._loadTotal();
        return this.totalResaults;
    },
	getData: function(){
        if (this.editable!=false){
            if (this.isEdit) this._completeLineEdit();
            var data = [];

            var tables = this.node.getElements("table");
            for (var i=1; i<((this.totalTable) ? tables.length-1 : tables.length); i++){
                var table = tables[i];
                var d = table.retrieve("data");
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
                "text-align": "left",
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

MWF.xApplication.process.Xform.DatagridMobile$Title =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
        this.dataGrid = this.node.retrieve("dataGrid");
        if ((this.json.total == "number") || (this.json.total == "count")){
            this.dataGrid.totalModules.push({
                "module": this,
                "index": this.node.getParent("tr").rowIndex,
                "type": this.json.total
            })
        }
	//	this.form._loadModules(this.node);
	}
});
MWF.xApplication.process.Xform.DatagridMobile$Data =  new Class({
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
            td.empty();
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